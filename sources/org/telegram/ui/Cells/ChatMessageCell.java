package org.telegram.ui.Cells;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
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
import android.util.SparseArray;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeInfo.AccessibilityAction;
import android.view.accessibility.AccessibilityNodeInfo.CollectionItemInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.DownloadController.FileDownloadProgressListener;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
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
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.GeoPoint;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC.TL_poll;
import org.telegram.tgnet.TLRPC.TL_pollAnswer;
import org.telegram.tgnet.TLRPC.TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RoundVideoPlayingDrawable;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBar.SeekBarDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.PhotoViewer;

public class ChatMessageCell extends BaseCell implements SeekBarDelegate, ImageReceiverDelegate, FileDownloadProgressListener {
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
    private SparseArray<Rect> accessibilityVirtualViewBounds = new SparseArray();
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
    private boolean autoPlayingMedia;
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
    private CharSequence currentCaption;
    private Chat currentChat;
    private int currentFocusedVirtualView = -1;
    private Chat currentForwardChannel;
    private String currentForwardName;
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
    private boolean drawPhotoCheckBox;
    private boolean drawPhotoImage;
    private boolean drawPinnedBottom;
    private boolean drawPinnedTop;
    private boolean drawRadialCheckBackground;
    private boolean drawSelectionBackground;
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
    private int forwardNameCenterX;
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
    private TL_poll lastPoll;
    private ArrayList<TL_pollAnswerVoters> lastPollResults;
    private int lastPollResultsVoters;
    private int lastSendState;
    private int lastTime;
    private float lastTouchX;
    private float lastTouchY;
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
    private CheckBoxBase photoCheckBox;
    private ImageReceiver photoImage;
    private boolean photoNotSet;
    private TLObject photoParentObject;
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
    private int selectorDrawableMaskType;
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

        /* synthetic */ BotButton(ChatMessageCell chatMessageCell, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public interface ChatMessageCellDelegate {

        public final /* synthetic */ class -CC {
            public static boolean $default$canPerformActions(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$didLongPress(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressBotButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, KeyboardButton keyboardButton) {
            }

            public static void $default$didPressCancelSendButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressChannelAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, Chat chat, int i, float f, float f2) {
            }

            public static void $default$didPressHiddenForward(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressImage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressInstantButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressOther(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressReplyMessage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressShare(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressUrl(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject, CharacterStyle characterStyle, boolean z) {
            }

            public static void $default$didPressUserAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, User user, float f, float f2) {
            }

            public static void $default$didPressViaBot(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, String str) {
            }

            public static void $default$didPressVoteButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer) {
            }

            public static void $default$didStartVideoStream(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
            }

            public static String $default$getAdminRank(ChatMessageCellDelegate chatMessageCellDelegate, int i) {
                return null;
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

        void didPressBotButton(ChatMessageCell chatMessageCell, KeyboardButton keyboardButton);

        void didPressCancelSendButton(ChatMessageCell chatMessageCell);

        void didPressChannelAvatar(ChatMessageCell chatMessageCell, Chat chat, int i, float f, float f2);

        void didPressHiddenForward(ChatMessageCell chatMessageCell);

        void didPressImage(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressInstantButton(ChatMessageCell chatMessageCell, int i);

        void didPressOther(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressReplyMessage(ChatMessageCell chatMessageCell, int i);

        void didPressShare(ChatMessageCell chatMessageCell);

        void didPressUrl(MessageObject messageObject, CharacterStyle characterStyle, boolean z);

        void didPressUserAvatar(ChatMessageCell chatMessageCell, User user, float f, float f2);

        void didPressViaBot(ChatMessageCell chatMessageCell, String str);

        void didPressVoteButton(ChatMessageCell chatMessageCell, TL_pollAnswer tL_pollAnswer);

        void didStartVideoStream(MessageObject messageObject);

        String getAdminRank(int i);

        void needOpenWebView(String str, String str2, String str3, String str4, int i, int i2);

        boolean needPlayMessage(MessageObject messageObject);

        void setShouldNotRepeatSticker(MessageObject messageObject);

        boolean shouldRepeatSticker(MessageObject messageObject);

        void videoTimerReached();
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

        /* synthetic */ MessageAccessibilityNodeProvider(ChatMessageCell chatMessageCell, AnonymousClass1 anonymousClass1) {
            this();
        }

        public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
            int[] iArr = new int[]{0, 0};
            ChatMessageCell.this.getLocationOnScreen(iArr);
            CharSequence charSequence = null;
            String str = ", ";
            int i2 = 0;
            String stringBuilder;
            int i3;
            int i4;
            if (i == -1) {
                int access$3200;
                AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain(ChatMessageCell.this);
                ChatMessageCell.this.onInitializeAccessibilityNodeInfo(obtain);
                StringBuilder stringBuilder2 = new StringBuilder();
                ChatMessageCell chatMessageCell = ChatMessageCell.this;
                if (!(!chatMessageCell.isChat || chatMessageCell.currentUser == null || ChatMessageCell.this.currentMessageObject.isOut())) {
                    stringBuilder2.append(UserObject.getUserName(ChatMessageCell.this.currentUser));
                    stringBuilder2.append(10);
                }
                if (!TextUtils.isEmpty(ChatMessageCell.this.currentMessageObject.messageText)) {
                    stringBuilder2.append(ChatMessageCell.this.currentMessageObject.messageText);
                }
                String str2 = "\n";
                if (ChatMessageCell.this.currentMessageObject.isMusic()) {
                    stringBuilder2.append(str2);
                    stringBuilder2.append(LocaleController.formatString("AccDescrMusicInfo", NUM, ChatMessageCell.this.currentMessageObject.getMusicAuthor(), ChatMessageCell.this.currentMessageObject.getMusicTitle()));
                } else if (ChatMessageCell.this.currentMessageObject.isVoice() || ChatMessageCell.this.currentMessageObject.isRoundVideo()) {
                    stringBuilder2.append(str);
                    stringBuilder2.append(LocaleController.formatCallDuration(ChatMessageCell.this.currentMessageObject.getDuration()));
                    if (ChatMessageCell.this.currentMessageObject.isContentUnread()) {
                        stringBuilder2.append(str);
                        stringBuilder2.append(LocaleController.getString("AccDescrMsgNotPlayed", NUM));
                    }
                }
                if (ChatMessageCell.this.lastPoll != null) {
                    stringBuilder2.append(str);
                    stringBuilder2.append(ChatMessageCell.this.lastPoll.question);
                    stringBuilder2.append(str);
                    stringBuilder2.append(LocaleController.getString("AnonymousPoll", NUM));
                }
                if (!(ChatMessageCell.this.currentMessageObject.messageOwner.media == null || TextUtils.isEmpty(ChatMessageCell.this.currentMessageObject.caption))) {
                    stringBuilder2.append(str2);
                    stringBuilder2.append(ChatMessageCell.this.currentMessageObject.caption);
                }
                stringBuilder2.append(str2);
                StringBuilder stringBuilder3 = new StringBuilder();
                stringBuilder3.append(LocaleController.getString("TodayAt", NUM));
                stringBuilder3.append(" ");
                stringBuilder3.append(ChatMessageCell.this.currentTimeString);
                stringBuilder = stringBuilder3.toString();
                if (ChatMessageCell.this.currentMessageObject.isOut()) {
                    stringBuilder2.append(LocaleController.formatString("AccDescrSentDate", NUM, stringBuilder));
                    stringBuilder2.append(str);
                    if (ChatMessageCell.this.currentMessageObject.isUnread()) {
                        i3 = NUM;
                        stringBuilder = "AccDescrMsgUnread";
                    } else {
                        i3 = NUM;
                        stringBuilder = "AccDescrMsgRead";
                    }
                    stringBuilder2.append(LocaleController.getString(stringBuilder, i3));
                } else {
                    stringBuilder2.append(LocaleController.formatString("AccDescrReceivedDate", NUM, stringBuilder));
                }
                obtain.setContentDescription(stringBuilder2.toString());
                obtain.setEnabled(true);
                if (VERSION.SDK_INT >= 19) {
                    CollectionItemInfo collectionItemInfo = obtain.getCollectionItemInfo();
                    if (collectionItemInfo != null) {
                        obtain.setCollectionItemInfo(CollectionItemInfo.obtain(collectionItemInfo.getRowIndex(), 1, 0, 1, false));
                    }
                }
                if (VERSION.SDK_INT >= 21) {
                    obtain.addAction(new AccessibilityAction(NUM, LocaleController.getString("AccActionMessageOptions", NUM)));
                    access$3200 = ChatMessageCell.this.getIconForCurrentState();
                    if (access$3200 == 0) {
                        charSequence = LocaleController.getString("AccActionPlay", NUM);
                    } else if (access$3200 == 1) {
                        charSequence = LocaleController.getString("AccActionPause", NUM);
                    } else if (access$3200 == 2) {
                        charSequence = LocaleController.getString("AccActionDownload", NUM);
                    } else if (access$3200 == 3) {
                        charSequence = LocaleController.getString("AccActionCancelDownload", NUM);
                    } else if (access$3200 == 5) {
                        charSequence = LocaleController.getString("AccActionOpenFile", NUM);
                    } else if (ChatMessageCell.this.currentMessageObject.type == 16) {
                        charSequence = LocaleController.getString("CallAgain", NUM);
                    }
                    obtain.addAction(new AccessibilityAction(16, charSequence));
                    obtain.addAction(new AccessibilityAction(32, LocaleController.getString("AccActionEnterSelectionMode", NUM)));
                    if (ChatMessageCell.this.getMiniIconForCurrentState() == 2) {
                        obtain.addAction(new AccessibilityAction(NUM, LocaleController.getString("AccActionDownload", NUM)));
                    }
                } else {
                    obtain.addAction(16);
                    obtain.addAction(32);
                }
                if (ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable) {
                    Spannable spannable = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
                    i4 = 0;
                    for (CharacterStyle characterStyle : (CharacterStyle[]) spannable.getSpans(0, spannable.length(), ClickableSpan.class)) {
                        obtain.addChild(ChatMessageCell.this, i4 + 2000);
                        i4++;
                    }
                }
                Iterator it = ChatMessageCell.this.botButtons.iterator();
                access$3200 = 0;
                while (it.hasNext()) {
                    BotButton botButton = (BotButton) it.next();
                    obtain.addChild(ChatMessageCell.this, access$3200 + 1000);
                    access$3200++;
                }
                it = ChatMessageCell.this.pollButtons.iterator();
                while (it.hasNext()) {
                    PollButton pollButton = (PollButton) it.next();
                    obtain.addChild(ChatMessageCell.this, i2 + 500);
                    i2++;
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
                int[] access$4000 = ChatMessageCell.this.getRealSpanStartAndEnd(spannable2, linkById);
                obtain2.setText(spannable2.subSequence(access$4000[0], access$4000[1]).toString());
                Iterator it2 = ChatMessageCell.this.currentMessageObject.textLayoutBlocks.iterator();
                while (it2.hasNext()) {
                    TextLayoutBlock textLayoutBlock = (TextLayoutBlock) it2.next();
                    i3 = textLayoutBlock.textLayout.getText().length();
                    int i5 = textLayoutBlock.charactersOffset;
                    if (i5 <= access$4000[0] && i3 + i5 >= access$4000[1]) {
                        textLayoutBlock.textLayout.getSelectionPath(access$4000[0] - i5, access$4000[1] - i5, this.linkPath);
                        this.linkPath.computeBounds(this.rectF, true);
                        Rect rect = this.rect;
                        RectF rectF = this.rectF;
                        rect.set((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        this.rect.offset(0, (int) textLayoutBlock.textYOffset);
                        this.rect.offset(ChatMessageCell.this.textX, ChatMessageCell.this.textY);
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
                    }
                }
                obtain2.setClassName("android.widget.TextView");
                obtain2.setEnabled(true);
                obtain2.setClickable(true);
                obtain2.setLongClickable(true);
                obtain2.addAction(16);
                obtain2.addAction(32);
            } else {
                stringBuilder = "android.widget.Button";
                int i6;
                if (i >= 1000) {
                    i6 = i - 1000;
                    if (i6 >= ChatMessageCell.this.botButtons.size()) {
                        return null;
                    }
                    BotButton botButton2 = (BotButton) ChatMessageCell.this.botButtons.get(i6);
                    obtain2.setText(botButton2.title.getText());
                    obtain2.setClassName(stringBuilder);
                    obtain2.setEnabled(true);
                    obtain2.setClickable(true);
                    obtain2.addAction(16);
                    this.rect.set(botButton2.x, botButton2.y, botButton2.x + botButton2.width, botButton2.y + botButton2.height);
                    if (ChatMessageCell.this.currentMessageObject.isOutOwner()) {
                        i6 = (ChatMessageCell.this.getMeasuredWidth() - ChatMessageCell.this.widthForButtons) - AndroidUtilities.dp(10.0f);
                    } else {
                        i6 = ChatMessageCell.this.backgroundDrawableLeft + AndroidUtilities.dp(ChatMessageCell.this.mediaBackground ? 1.0f : 7.0f);
                    }
                    this.rect.offset(i6, ChatMessageCell.this.layoutHeight);
                    obtain2.setBoundsInParent(this.rect);
                    if (ChatMessageCell.this.accessibilityVirtualViewBounds.get(i) == null) {
                        ChatMessageCell.this.accessibilityVirtualViewBounds.put(i, new Rect(this.rect));
                    }
                    this.rect.offset(iArr[0], iArr[1]);
                    obtain2.setBoundsInScreen(this.rect);
                } else if (i >= 500) {
                    i6 = i - 500;
                    if (i6 >= ChatMessageCell.this.pollButtons.size()) {
                        return null;
                    }
                    PollButton pollButton2 = (PollButton) ChatMessageCell.this.pollButtons.get(i6);
                    obtain2.setText(pollButton2.title.getText());
                    if (ChatMessageCell.this.pollVoted) {
                        StringBuilder stringBuilder4 = new StringBuilder();
                        stringBuilder4.append(obtain2.getText());
                        stringBuilder4.append(str);
                        stringBuilder4.append(pollButton2.percent);
                        stringBuilder4.append("%");
                        obtain2.setText(stringBuilder4.toString());
                    } else {
                        obtain2.setClassName(stringBuilder);
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
                    obtain2.setClassName(stringBuilder);
                    obtain2.setEnabled(true);
                    if (ChatMessageCell.this.instantViewLayout != null) {
                        obtain2.setText(ChatMessageCell.this.instantViewLayout.getText());
                    }
                    obtain2.addAction(16);
                    i6 = ChatMessageCell.this.photoImage.getImageX();
                    int measuredHeight = ChatMessageCell.this.getMeasuredHeight() - AndroidUtilities.dp(64.0f);
                    if (ChatMessageCell.this.currentMessageObject.isOutOwner()) {
                        i4 = (ChatMessageCell.this.getMeasuredWidth() - ChatMessageCell.this.widthForButtons) - AndroidUtilities.dp(10.0f);
                    } else {
                        i4 = AndroidUtilities.dp(ChatMessageCell.this.mediaBackground ? 1.0f : 7.0f) + ChatMessageCell.this.backgroundDrawableLeft;
                    }
                    this.rect.set(i6 + i4, measuredHeight, (i6 + ChatMessageCell.this.instantWidth) + i4, AndroidUtilities.dp(38.0f) + measuredHeight);
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
                    StringBuilder stringBuilder5 = new StringBuilder();
                    stringBuilder5.append(LocaleController.getString("Reply", NUM));
                    stringBuilder5.append(str);
                    if (ChatMessageCell.this.replyNameLayout != null) {
                        stringBuilder5.append(ChatMessageCell.this.replyNameLayout.getText());
                        stringBuilder5.append(str);
                    }
                    if (ChatMessageCell.this.replyTextLayout != null) {
                        stringBuilder5.append(ChatMessageCell.this.replyTextLayout.getText());
                    }
                    obtain2.setContentDescription(stringBuilder5.toString());
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
                ChatMessageCellDelegate access$6200;
                ChatMessageCell chatMessageCell;
                if (i >= 2000) {
                    linkById = getLinkById(i);
                    if (linkById != null) {
                        ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this.currentMessageObject, linkById, false);
                        ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                    }
                } else if (i >= 1000) {
                    i2 = i - 1000;
                    if (i2 >= ChatMessageCell.this.botButtons.size()) {
                        return false;
                    }
                    BotButton botButton = (BotButton) ChatMessageCell.this.botButtons.get(i2);
                    if (ChatMessageCell.this.delegate != null) {
                        ChatMessageCell.this.delegate.didPressBotButton(ChatMessageCell.this, botButton.button);
                    }
                    ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                } else if (i >= 500) {
                    i2 = i - 500;
                    if (i2 >= ChatMessageCell.this.pollButtons.size()) {
                        return false;
                    }
                    PollButton pollButton = (PollButton) ChatMessageCell.this.pollButtons.get(i2);
                    if (ChatMessageCell.this.delegate != null) {
                        ChatMessageCell.this.delegate.didPressVoteButton(ChatMessageCell.this, pollButton.answer);
                    }
                    ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 1);
                } else if (i == 499) {
                    if (ChatMessageCell.this.delegate != null) {
                        access$6200 = ChatMessageCell.this.delegate;
                        chatMessageCell = ChatMessageCell.this;
                        access$6200.didPressInstantButton(chatMessageCell, chatMessageCell.drawInstantViewType);
                    }
                } else if (i == 498) {
                    if (ChatMessageCell.this.delegate != null) {
                        ChatMessageCell.this.delegate.didPressShare(ChatMessageCell.this);
                    }
                } else if (i == 497 && ChatMessageCell.this.delegate != null) {
                    access$6200 = ChatMessageCell.this.delegate;
                    chatMessageCell = ChatMessageCell.this;
                    access$6200.didPressReplyMessage(chatMessageCell, chatMessageCell.currentMessageObject.messageOwner.reply_to_msg_id);
                }
            } else if (i2 == 32) {
                linkById = getLinkById(i);
                if (linkById != null) {
                    ChatMessageCell.this.delegate.didPressUrl(ChatMessageCell.this.currentMessageObject, linkById, true);
                    ChatMessageCell.this.sendAccessibilityEventForVirtualView(i, 2);
                }
            }
            return true;
        }

        private ClickableSpan getLinkById(int i) {
            i -= 2000;
            if (!(ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable)) {
                return null;
            }
            Spannable spannable = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
            ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(0, spannable.length(), ClickableSpan.class);
            if (clickableSpanArr.length <= i) {
                return null;
            }
            return clickableSpanArr[i];
        }
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

        /* synthetic */ PollButton(ChatMessageCell chatMessageCell, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    private boolean intersect(float f, float f2, float f3, float f4) {
        boolean z = true;
        if (f <= f3) {
            if (f2 < f3) {
                z = false;
            }
            return z;
        }
        if (f > f4) {
            z = false;
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
        if (this.urlPathCache.isEmpty()) {
            linkPath = new LinkPath();
        } else {
            linkPath = (LinkPath) this.urlPathCache.get(0);
            this.urlPathCache.remove(0);
        }
        linkPath.reset();
        if (z) {
            this.urlPathSelection.add(linkPath);
        } else {
            this.urlPath.add(linkPath);
        }
        return linkPath;
    }

    /* JADX WARNING: Removed duplicated region for block: B:9:0x0020  */
    private int[] getRealSpanStartAndEnd(android.text.Spannable r6, android.text.style.CharacterStyle r7) {
        /*
        r5 = this;
        r0 = r7 instanceof org.telegram.ui.Components.URLSpanBrowser;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x001b;
    L_0x0006:
        r0 = r7;
        r0 = (org.telegram.ui.Components.URLSpanBrowser) r0;
        r0 = r0.getStyle();
        if (r0 == 0) goto L_0x001b;
    L_0x000f:
        r0 = r0.urlEntity;
        if (r0 == 0) goto L_0x001b;
    L_0x0013:
        r3 = r0.offset;
        r0 = r0.length;
        r0 = r0 + r3;
        r4 = r0;
        r0 = 1;
        goto L_0x001e;
    L_0x001b:
        r0 = 0;
        r3 = 0;
        r4 = 0;
    L_0x001e:
        if (r0 != 0) goto L_0x0028;
    L_0x0020:
        r3 = r6.getSpanStart(r7);
        r4 = r6.getSpanEnd(r7);
    L_0x0028:
        r6 = 2;
        r6 = new int[r6];
        r6[r2] = r3;
        r6[r1] = r4;
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getRealSpanStartAndEnd(android.text.Spannable, android.text.style.CharacterStyle):int[]");
    }

    /* JADX WARNING: Removed duplicated region for block: B:56:0x00e9 A:{Catch:{ Exception -> 0x01f3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00d6 A:{Catch:{ Exception -> 0x01f3 }} */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00e9 A:{Catch:{ Exception -> 0x01f3 }} */
    private boolean checkTextBlockMotionEvent(android.view.MotionEvent r14) {
        /*
        r13 = this;
        r0 = r13.currentMessageObject;
        r1 = r0.type;
        r2 = 0;
        if (r1 != 0) goto L_0x01fb;
    L_0x0007:
        r0 = r0.textLayoutBlocks;
        if (r0 == 0) goto L_0x01fb;
    L_0x000b:
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x01fb;
    L_0x0011:
        r0 = r13.currentMessageObject;
        r0 = r0.messageText;
        r0 = r0 instanceof android.text.Spannable;
        if (r0 != 0) goto L_0x001b;
    L_0x0019:
        goto L_0x01fb;
    L_0x001b:
        r0 = r14.getAction();
        r1 = 1;
        if (r0 == 0) goto L_0x002c;
    L_0x0022:
        r0 = r14.getAction();
        if (r0 != r1) goto L_0x01fb;
    L_0x0028:
        r0 = r13.pressedLinkType;
        if (r0 != r1) goto L_0x01fb;
    L_0x002c:
        r0 = r14.getX();
        r0 = (int) r0;
        r3 = r14.getY();
        r3 = (int) r3;
        r4 = r13.textX;
        if (r0 < r4) goto L_0x01f8;
    L_0x003a:
        r5 = r13.textY;
        if (r3 < r5) goto L_0x01f8;
    L_0x003e:
        r6 = r13.currentMessageObject;
        r7 = r6.textWidth;
        r4 = r4 + r7;
        if (r0 > r4) goto L_0x01f8;
    L_0x0045:
        r4 = r6.textHeight;
        r4 = r4 + r5;
        if (r3 > r4) goto L_0x01f8;
    L_0x004a:
        r3 = r3 - r5;
        r4 = 0;
        r5 = 0;
    L_0x004d:
        r6 = r13.currentMessageObject;
        r6 = r6.textLayoutBlocks;
        r6 = r6.size();
        if (r4 >= r6) goto L_0x006f;
    L_0x0057:
        r6 = r13.currentMessageObject;
        r6 = r6.textLayoutBlocks;
        r6 = r6.get(r4);
        r6 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r6;
        r6 = r6.textYOffset;
        r7 = (float) r3;
        r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
        if (r6 <= 0) goto L_0x0069;
    L_0x0068:
        goto L_0x006f;
    L_0x0069:
        r5 = r4 + 1;
        r12 = r5;
        r5 = r4;
        r4 = r12;
        goto L_0x004d;
    L_0x006f:
        r4 = r13.currentMessageObject;	 Catch:{ Exception -> 0x01f3 }
        r4 = r4.textLayoutBlocks;	 Catch:{ Exception -> 0x01f3 }
        r4 = r4.get(r5);	 Catch:{ Exception -> 0x01f3 }
        r4 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r4;	 Catch:{ Exception -> 0x01f3 }
        r0 = (float) r0;	 Catch:{ Exception -> 0x01f3 }
        r6 = r13.textX;	 Catch:{ Exception -> 0x01f3 }
        r6 = (float) r6;	 Catch:{ Exception -> 0x01f3 }
        r7 = r4.isRtl();	 Catch:{ Exception -> 0x01f3 }
        r8 = 0;
        if (r7 == 0) goto L_0x0089;
    L_0x0084:
        r7 = r13.currentMessageObject;	 Catch:{ Exception -> 0x01f3 }
        r7 = r7.textXOffset;	 Catch:{ Exception -> 0x01f3 }
        goto L_0x008a;
    L_0x0089:
        r7 = 0;
    L_0x008a:
        r6 = r6 - r7;
        r0 = r0 - r6;
        r0 = (int) r0;	 Catch:{ Exception -> 0x01f3 }
        r3 = (float) r3;	 Catch:{ Exception -> 0x01f3 }
        r6 = r4.textYOffset;	 Catch:{ Exception -> 0x01f3 }
        r3 = r3 - r6;
        r3 = (int) r3;	 Catch:{ Exception -> 0x01f3 }
        r6 = r4.textLayout;	 Catch:{ Exception -> 0x01f3 }
        r3 = r6.getLineForVertical(r3);	 Catch:{ Exception -> 0x01f3 }
        r6 = r4.textLayout;	 Catch:{ Exception -> 0x01f3 }
        r0 = (float) r0;	 Catch:{ Exception -> 0x01f3 }
        r6 = r6.getOffsetForHorizontal(r3, r0);	 Catch:{ Exception -> 0x01f3 }
        r7 = r4.textLayout;	 Catch:{ Exception -> 0x01f3 }
        r7 = r7.getLineLeft(r3);	 Catch:{ Exception -> 0x01f3 }
        r9 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1));
        if (r9 > 0) goto L_0x01fb;
    L_0x00a9:
        r9 = r4.textLayout;	 Catch:{ Exception -> 0x01f3 }
        r3 = r9.getLineWidth(r3);	 Catch:{ Exception -> 0x01f3 }
        r7 = r7 + r3;
        r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1));
        if (r0 < 0) goto L_0x01fb;
    L_0x00b4:
        r0 = r13.currentMessageObject;	 Catch:{ Exception -> 0x01f3 }
        r0 = r0.messageText;	 Catch:{ Exception -> 0x01f3 }
        r0 = (android.text.Spannable) r0;	 Catch:{ Exception -> 0x01f3 }
        r3 = android.text.style.ClickableSpan.class;
        r3 = r0.getSpans(r6, r6, r3);	 Catch:{ Exception -> 0x01f3 }
        r3 = (android.text.style.CharacterStyle[]) r3;	 Catch:{ Exception -> 0x01f3 }
        if (r3 == 0) goto L_0x00ca;
    L_0x00c4:
        r7 = r3.length;	 Catch:{ Exception -> 0x01f3 }
        if (r7 != 0) goto L_0x00c8;
    L_0x00c7:
        goto L_0x00ca;
    L_0x00c8:
        r6 = 0;
        goto L_0x00d3;
    L_0x00ca:
        r3 = org.telegram.ui.Components.URLSpanMono.class;
        r3 = r0.getSpans(r6, r6, r3);	 Catch:{ Exception -> 0x01f3 }
        r3 = (android.text.style.CharacterStyle[]) r3;	 Catch:{ Exception -> 0x01f3 }
        r6 = 1;
    L_0x00d3:
        r7 = r3.length;	 Catch:{ Exception -> 0x01f3 }
        if (r7 == 0) goto L_0x00e6;
    L_0x00d6:
        r7 = r3.length;	 Catch:{ Exception -> 0x01f3 }
        if (r7 == 0) goto L_0x00e4;
    L_0x00d9:
        r7 = r3[r2];	 Catch:{ Exception -> 0x01f3 }
        r7 = r7 instanceof org.telegram.ui.Components.URLSpanBotCommand;	 Catch:{ Exception -> 0x01f3 }
        if (r7 == 0) goto L_0x00e4;
    L_0x00df:
        r7 = org.telegram.ui.Components.URLSpanBotCommand.enabled;	 Catch:{ Exception -> 0x01f3 }
        if (r7 != 0) goto L_0x00e4;
    L_0x00e3:
        goto L_0x00e6;
    L_0x00e4:
        r7 = 0;
        goto L_0x00e7;
    L_0x00e6:
        r7 = 1;
    L_0x00e7:
        if (r7 != 0) goto L_0x01fb;
    L_0x00e9:
        r14 = r14.getAction();	 Catch:{ Exception -> 0x01f3 }
        if (r14 != 0) goto L_0x01e0;
    L_0x00ef:
        r14 = r3[r2];	 Catch:{ Exception -> 0x01f3 }
        r13.pressedLink = r14;	 Catch:{ Exception -> 0x01f3 }
        r13.linkBlockNum = r5;	 Catch:{ Exception -> 0x01f3 }
        r13.pressedLinkType = r1;	 Catch:{ Exception -> 0x01f3 }
        r13.resetUrlPaths(r2);	 Catch:{ Exception -> 0x01f3 }
        r14 = r13.obtainNewUrlPath(r2);	 Catch:{ Exception -> 0x01d8 }
        r3 = r13.pressedLink;	 Catch:{ Exception -> 0x01d8 }
        r3 = r13.getRealSpanStartAndEnd(r0, r3);	 Catch:{ Exception -> 0x01d8 }
        r7 = r4.textLayout;	 Catch:{ Exception -> 0x01d8 }
        r9 = r3[r2];	 Catch:{ Exception -> 0x01d8 }
        r14.setCurrentLayout(r7, r9, r8);	 Catch:{ Exception -> 0x01d8 }
        r7 = r4.textLayout;	 Catch:{ Exception -> 0x01d8 }
        r8 = r3[r2];	 Catch:{ Exception -> 0x01d8 }
        r9 = r3[r1];	 Catch:{ Exception -> 0x01d8 }
        r7.getSelectionPath(r8, r9, r14);	 Catch:{ Exception -> 0x01d8 }
        r14 = r3[r1];	 Catch:{ Exception -> 0x01d8 }
        r7 = r4.charactersEnd;	 Catch:{ Exception -> 0x01d8 }
        if (r14 < r7) goto L_0x0177;
    L_0x011a:
        r14 = r5 + 1;
    L_0x011c:
        r7 = r13.currentMessageObject;	 Catch:{ Exception -> 0x01d8 }
        r7 = r7.textLayoutBlocks;	 Catch:{ Exception -> 0x01d8 }
        r7 = r7.size();	 Catch:{ Exception -> 0x01d8 }
        if (r14 >= r7) goto L_0x0177;
    L_0x0126:
        r7 = r13.currentMessageObject;	 Catch:{ Exception -> 0x01d8 }
        r7 = r7.textLayoutBlocks;	 Catch:{ Exception -> 0x01d8 }
        r7 = r7.get(r14);	 Catch:{ Exception -> 0x01d8 }
        r7 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r7;	 Catch:{ Exception -> 0x01d8 }
        if (r6 == 0) goto L_0x013f;
    L_0x0132:
        r8 = r7.charactersOffset;	 Catch:{ Exception -> 0x01d8 }
        r9 = r7.charactersOffset;	 Catch:{ Exception -> 0x01d8 }
        r10 = org.telegram.ui.Components.URLSpanMono.class;
        r8 = r0.getSpans(r8, r9, r10);	 Catch:{ Exception -> 0x01d8 }
        r8 = (android.text.style.CharacterStyle[]) r8;	 Catch:{ Exception -> 0x01d8 }
        goto L_0x014b;
    L_0x013f:
        r8 = r7.charactersOffset;	 Catch:{ Exception -> 0x01d8 }
        r9 = r7.charactersOffset;	 Catch:{ Exception -> 0x01d8 }
        r10 = android.text.style.ClickableSpan.class;
        r8 = r0.getSpans(r8, r9, r10);	 Catch:{ Exception -> 0x01d8 }
        r8 = (android.text.style.CharacterStyle[]) r8;	 Catch:{ Exception -> 0x01d8 }
    L_0x014b:
        if (r8 == 0) goto L_0x0177;
    L_0x014d:
        r9 = r8.length;	 Catch:{ Exception -> 0x01d8 }
        if (r9 == 0) goto L_0x0177;
    L_0x0150:
        r8 = r8[r2];	 Catch:{ Exception -> 0x01d8 }
        r9 = r13.pressedLink;	 Catch:{ Exception -> 0x01d8 }
        if (r8 == r9) goto L_0x0157;
    L_0x0156:
        goto L_0x0177;
    L_0x0157:
        r8 = r13.obtainNewUrlPath(r2);	 Catch:{ Exception -> 0x01d8 }
        r9 = r7.textLayout;	 Catch:{ Exception -> 0x01d8 }
        r10 = r7.textYOffset;	 Catch:{ Exception -> 0x01d8 }
        r11 = r4.textYOffset;	 Catch:{ Exception -> 0x01d8 }
        r10 = r10 - r11;
        r8.setCurrentLayout(r9, r2, r10);	 Catch:{ Exception -> 0x01d8 }
        r9 = r7.textLayout;	 Catch:{ Exception -> 0x01d8 }
        r10 = r3[r1];	 Catch:{ Exception -> 0x01d8 }
        r9.getSelectionPath(r2, r10, r8);	 Catch:{ Exception -> 0x01d8 }
        r8 = r3[r1];	 Catch:{ Exception -> 0x01d8 }
        r7 = r7.charactersEnd;	 Catch:{ Exception -> 0x01d8 }
        r7 = r7 - r1;
        if (r8 >= r7) goto L_0x0174;
    L_0x0173:
        goto L_0x0177;
    L_0x0174:
        r14 = r14 + 1;
        goto L_0x011c;
    L_0x0177:
        r14 = r3[r2];	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.charactersOffset;	 Catch:{ Exception -> 0x01d8 }
        if (r14 > r4) goto L_0x01dc;
    L_0x017d:
        r5 = r5 - r1;
        r14 = 0;
    L_0x017f:
        if (r5 < 0) goto L_0x01dc;
    L_0x0181:
        r4 = r13.currentMessageObject;	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.textLayoutBlocks;	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.get(r5);	 Catch:{ Exception -> 0x01d8 }
        r4 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r4;	 Catch:{ Exception -> 0x01d8 }
        if (r6 == 0) goto L_0x019c;
    L_0x018d:
        r7 = r4.charactersEnd;	 Catch:{ Exception -> 0x01d8 }
        r7 = r7 - r1;
        r8 = r4.charactersEnd;	 Catch:{ Exception -> 0x01d8 }
        r8 = r8 - r1;
        r9 = org.telegram.ui.Components.URLSpanMono.class;
        r7 = r0.getSpans(r7, r8, r9);	 Catch:{ Exception -> 0x01d8 }
        r7 = (android.text.style.CharacterStyle[]) r7;	 Catch:{ Exception -> 0x01d8 }
        goto L_0x01aa;
    L_0x019c:
        r7 = r4.charactersEnd;	 Catch:{ Exception -> 0x01d8 }
        r7 = r7 - r1;
        r8 = r4.charactersEnd;	 Catch:{ Exception -> 0x01d8 }
        r8 = r8 - r1;
        r9 = android.text.style.ClickableSpan.class;
        r7 = r0.getSpans(r7, r8, r9);	 Catch:{ Exception -> 0x01d8 }
        r7 = (android.text.style.CharacterStyle[]) r7;	 Catch:{ Exception -> 0x01d8 }
    L_0x01aa:
        if (r7 == 0) goto L_0x01dc;
    L_0x01ac:
        r8 = r7.length;	 Catch:{ Exception -> 0x01d8 }
        if (r8 == 0) goto L_0x01dc;
    L_0x01af:
        r7 = r7[r2];	 Catch:{ Exception -> 0x01d8 }
        r8 = r13.pressedLink;	 Catch:{ Exception -> 0x01d8 }
        if (r7 == r8) goto L_0x01b6;
    L_0x01b5:
        goto L_0x01dc;
    L_0x01b6:
        r7 = r13.obtainNewUrlPath(r2);	 Catch:{ Exception -> 0x01d8 }
        r8 = r4.height;	 Catch:{ Exception -> 0x01d8 }
        r14 = r14 - r8;
        r8 = r4.textLayout;	 Catch:{ Exception -> 0x01d8 }
        r9 = r3[r2];	 Catch:{ Exception -> 0x01d8 }
        r10 = (float) r14;	 Catch:{ Exception -> 0x01d8 }
        r7.setCurrentLayout(r8, r9, r10);	 Catch:{ Exception -> 0x01d8 }
        r8 = r4.textLayout;	 Catch:{ Exception -> 0x01d8 }
        r9 = r3[r2];	 Catch:{ Exception -> 0x01d8 }
        r10 = r3[r1];	 Catch:{ Exception -> 0x01d8 }
        r8.getSelectionPath(r9, r10, r7);	 Catch:{ Exception -> 0x01d8 }
        r7 = r3[r2];	 Catch:{ Exception -> 0x01d8 }
        r4 = r4.charactersOffset;	 Catch:{ Exception -> 0x01d8 }
        if (r7 <= r4) goto L_0x01d5;
    L_0x01d4:
        goto L_0x01dc;
    L_0x01d5:
        r5 = r5 + -1;
        goto L_0x017f;
    L_0x01d8:
        r14 = move-exception;
        org.telegram.messenger.FileLog.e(r14);	 Catch:{ Exception -> 0x01f3 }
    L_0x01dc:
        r13.invalidate();	 Catch:{ Exception -> 0x01f3 }
        return r1;
    L_0x01e0:
        r14 = r3[r2];	 Catch:{ Exception -> 0x01f3 }
        r0 = r13.pressedLink;	 Catch:{ Exception -> 0x01f3 }
        if (r14 != r0) goto L_0x01fb;
    L_0x01e6:
        r14 = r13.delegate;	 Catch:{ Exception -> 0x01f3 }
        r0 = r13.currentMessageObject;	 Catch:{ Exception -> 0x01f3 }
        r3 = r13.pressedLink;	 Catch:{ Exception -> 0x01f3 }
        r14.didPressUrl(r0, r3, r2);	 Catch:{ Exception -> 0x01f3 }
        r13.resetPressedLink(r1);	 Catch:{ Exception -> 0x01f3 }
        return r1;
    L_0x01f3:
        r14 = move-exception;
        org.telegram.messenger.FileLog.e(r14);
        goto L_0x01fb;
    L_0x01f8:
        r13.resetPressedLink(r1);
    L_0x01fb:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkTextBlockMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:44:0x009c A:{Catch:{ Exception -> 0x00dc }} */
    private boolean checkCaptionMotionEvent(android.view.MotionEvent r8) {
        /*
        r7 = this;
        r0 = r7.currentCaption;
        r0 = r0 instanceof android.text.Spannable;
        r1 = 0;
        if (r0 == 0) goto L_0x00f5;
    L_0x0007:
        r0 = r7.captionLayout;
        if (r0 != 0) goto L_0x000d;
    L_0x000b:
        goto L_0x00f5;
    L_0x000d:
        r0 = r8.getAction();
        r2 = 1;
        if (r0 == 0) goto L_0x0022;
    L_0x0014:
        r0 = r7.linkPreviewPressed;
        if (r0 != 0) goto L_0x001c;
    L_0x0018:
        r0 = r7.pressedLink;
        if (r0 == 0) goto L_0x00f5;
    L_0x001c:
        r0 = r8.getAction();
        if (r0 != r2) goto L_0x00f5;
    L_0x0022:
        r0 = r8.getX();
        r0 = (int) r0;
        r3 = r8.getY();
        r3 = (int) r3;
        r4 = r7.captionX;
        r5 = 3;
        if (r0 < r4) goto L_0x00f2;
    L_0x0031:
        r6 = r7.captionWidth;
        r4 = r4 + r6;
        if (r0 > r4) goto L_0x00f2;
    L_0x0036:
        r4 = r7.captionY;
        if (r3 < r4) goto L_0x00f2;
    L_0x003a:
        r6 = r7.captionHeight;
        r4 = r4 + r6;
        if (r3 > r4) goto L_0x00f2;
    L_0x003f:
        r8 = r8.getAction();
        if (r8 != 0) goto L_0x00e1;
    L_0x0045:
        r8 = r7.captionX;	 Catch:{ Exception -> 0x00dc }
        r0 = r0 - r8;
        r8 = r7.captionY;	 Catch:{ Exception -> 0x00dc }
        r3 = r3 - r8;
        r8 = r7.captionLayout;	 Catch:{ Exception -> 0x00dc }
        r8 = r8.getLineForVertical(r3);	 Catch:{ Exception -> 0x00dc }
        r3 = r7.captionLayout;	 Catch:{ Exception -> 0x00dc }
        r0 = (float) r0;	 Catch:{ Exception -> 0x00dc }
        r3 = r3.getOffsetForHorizontal(r8, r0);	 Catch:{ Exception -> 0x00dc }
        r4 = r7.captionLayout;	 Catch:{ Exception -> 0x00dc }
        r4 = r4.getLineLeft(r8);	 Catch:{ Exception -> 0x00dc }
        r6 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r6 > 0) goto L_0x00f5;
    L_0x0062:
        r6 = r7.captionLayout;	 Catch:{ Exception -> 0x00dc }
        r8 = r6.getLineWidth(r8);	 Catch:{ Exception -> 0x00dc }
        r4 = r4 + r8;
        r8 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r8 < 0) goto L_0x00f5;
    L_0x006d:
        r8 = r7.currentCaption;	 Catch:{ Exception -> 0x00dc }
        r8 = (android.text.Spannable) r8;	 Catch:{ Exception -> 0x00dc }
        r0 = android.text.style.ClickableSpan.class;
        r0 = r8.getSpans(r3, r3, r0);	 Catch:{ Exception -> 0x00dc }
        r0 = (android.text.style.CharacterStyle[]) r0;	 Catch:{ Exception -> 0x00dc }
        if (r0 == 0) goto L_0x007e;
    L_0x007b:
        r4 = r0.length;	 Catch:{ Exception -> 0x00dc }
        if (r4 != 0) goto L_0x0086;
    L_0x007e:
        r0 = org.telegram.ui.Components.URLSpanMono.class;
        r0 = r8.getSpans(r3, r3, r0);	 Catch:{ Exception -> 0x00dc }
        r0 = (android.text.style.CharacterStyle[]) r0;	 Catch:{ Exception -> 0x00dc }
    L_0x0086:
        r3 = r0.length;	 Catch:{ Exception -> 0x00dc }
        if (r3 == 0) goto L_0x0099;
    L_0x0089:
        r3 = r0.length;	 Catch:{ Exception -> 0x00dc }
        if (r3 == 0) goto L_0x0097;
    L_0x008c:
        r3 = r0[r1];	 Catch:{ Exception -> 0x00dc }
        r3 = r3 instanceof org.telegram.ui.Components.URLSpanBotCommand;	 Catch:{ Exception -> 0x00dc }
        if (r3 == 0) goto L_0x0097;
    L_0x0092:
        r3 = org.telegram.ui.Components.URLSpanBotCommand.enabled;	 Catch:{ Exception -> 0x00dc }
        if (r3 != 0) goto L_0x0097;
    L_0x0096:
        goto L_0x0099;
    L_0x0097:
        r3 = 0;
        goto L_0x009a;
    L_0x0099:
        r3 = 1;
    L_0x009a:
        if (r3 != 0) goto L_0x00f5;
    L_0x009c:
        r0 = r0[r1];	 Catch:{ Exception -> 0x00dc }
        r7.pressedLink = r0;	 Catch:{ Exception -> 0x00dc }
        r7.pressedLinkType = r5;	 Catch:{ Exception -> 0x00dc }
        r7.resetUrlPaths(r1);	 Catch:{ Exception -> 0x00dc }
        r0 = r7.obtainNewUrlPath(r1);	 Catch:{ Exception -> 0x00c1 }
        r3 = r7.pressedLink;	 Catch:{ Exception -> 0x00c1 }
        r8 = r7.getRealSpanStartAndEnd(r8, r3);	 Catch:{ Exception -> 0x00c1 }
        r3 = r7.captionLayout;	 Catch:{ Exception -> 0x00c1 }
        r4 = r8[r1];	 Catch:{ Exception -> 0x00c1 }
        r5 = 0;
        r0.setCurrentLayout(r3, r4, r5);	 Catch:{ Exception -> 0x00c1 }
        r3 = r7.captionLayout;	 Catch:{ Exception -> 0x00c1 }
        r4 = r8[r1];	 Catch:{ Exception -> 0x00c1 }
        r8 = r8[r2];	 Catch:{ Exception -> 0x00c1 }
        r3.getSelectionPath(r4, r8, r0);	 Catch:{ Exception -> 0x00c1 }
        goto L_0x00c5;
    L_0x00c1:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);	 Catch:{ Exception -> 0x00dc }
    L_0x00c5:
        r8 = r7.currentMessagesGroup;	 Catch:{ Exception -> 0x00dc }
        if (r8 == 0) goto L_0x00d8;
    L_0x00c9:
        r8 = r7.getParent();	 Catch:{ Exception -> 0x00dc }
        if (r8 == 0) goto L_0x00d8;
    L_0x00cf:
        r8 = r7.getParent();	 Catch:{ Exception -> 0x00dc }
        r8 = (android.view.ViewGroup) r8;	 Catch:{ Exception -> 0x00dc }
        r8.invalidate();	 Catch:{ Exception -> 0x00dc }
    L_0x00d8:
        r7.invalidate();	 Catch:{ Exception -> 0x00dc }
        return r2;
    L_0x00dc:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
        goto L_0x00f5;
    L_0x00e1:
        r8 = r7.pressedLinkType;
        if (r8 != r5) goto L_0x00f5;
    L_0x00e5:
        r8 = r7.delegate;
        r0 = r7.currentMessageObject;
        r3 = r7.pressedLink;
        r8.didPressUrl(r0, r3, r1);
        r7.resetPressedLink(r5);
        return r2;
    L_0x00f2:
        r7.resetPressedLink(r5);
    L_0x00f5:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkCaptionMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:0x00bb A:{Catch:{ Exception -> 0x00ec }} */
    private boolean checkGameMotionEvent(android.view.MotionEvent r8) {
        /*
        r7 = this;
        r0 = r7.hasGamePreview;
        r1 = 0;
        if (r0 != 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        r0 = r8.getX();
        r0 = (int) r0;
        r2 = r8.getY();
        r2 = (int) r2;
        r3 = r8.getAction();
        r4 = 2;
        r5 = 1;
        if (r3 != 0) goto L_0x00f2;
    L_0x0018:
        r8 = r7.drawPhotoImage;
        if (r8 == 0) goto L_0x004c;
    L_0x001c:
        r8 = r7.drawImageButton;
        if (r8 == 0) goto L_0x004c;
    L_0x0020:
        r8 = r7.buttonState;
        r3 = -1;
        if (r8 == r3) goto L_0x004c;
    L_0x0025:
        r8 = r7.buttonX;
        if (r0 < r8) goto L_0x004c;
    L_0x0029:
        r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r8 = r8 + r6;
        if (r0 > r8) goto L_0x004c;
    L_0x0032:
        r8 = r7.buttonY;
        if (r2 < r8) goto L_0x004c;
    L_0x0036:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r8 = r8 + r3;
        if (r2 > r8) goto L_0x004c;
    L_0x003d:
        r8 = r7.radialProgress;
        r8 = r8.getIcon();
        r3 = 4;
        if (r8 == r3) goto L_0x004c;
    L_0x0046:
        r7.buttonPressed = r5;
        r7.invalidate();
        return r5;
    L_0x004c:
        r8 = r7.drawPhotoImage;
        if (r8 == 0) goto L_0x005d;
    L_0x0050:
        r8 = r7.photoImage;
        r3 = (float) r0;
        r6 = (float) r2;
        r8 = r8.isInsideImage(r3, r6);
        if (r8 == 0) goto L_0x005d;
    L_0x005a:
        r7.gamePreviewPressed = r5;
        return r5;
    L_0x005d:
        r8 = r7.descriptionLayout;
        if (r8 == 0) goto L_0x0170;
    L_0x0061:
        r8 = r7.descriptionY;
        if (r2 < r8) goto L_0x0170;
    L_0x0065:
        r8 = r7.unmovedTextX;	 Catch:{ Exception -> 0x00ec }
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x00ec }
        r8 = r8 + r3;
        r3 = r7.descriptionX;	 Catch:{ Exception -> 0x00ec }
        r8 = r8 + r3;
        r0 = r0 - r8;
        r8 = r7.descriptionY;	 Catch:{ Exception -> 0x00ec }
        r2 = r2 - r8;
        r8 = r7.descriptionLayout;	 Catch:{ Exception -> 0x00ec }
        r8 = r8.getLineForVertical(r2);	 Catch:{ Exception -> 0x00ec }
        r2 = r7.descriptionLayout;	 Catch:{ Exception -> 0x00ec }
        r0 = (float) r0;	 Catch:{ Exception -> 0x00ec }
        r2 = r2.getOffsetForHorizontal(r8, r0);	 Catch:{ Exception -> 0x00ec }
        r3 = r7.descriptionLayout;	 Catch:{ Exception -> 0x00ec }
        r3 = r3.getLineLeft(r8);	 Catch:{ Exception -> 0x00ec }
        r6 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1));
        if (r6 > 0) goto L_0x0170;
    L_0x008c:
        r6 = r7.descriptionLayout;	 Catch:{ Exception -> 0x00ec }
        r8 = r6.getLineWidth(r8);	 Catch:{ Exception -> 0x00ec }
        r3 = r3 + r8;
        r8 = (r3 > r0 ? 1 : (r3 == r0 ? 0 : -1));
        if (r8 < 0) goto L_0x0170;
    L_0x0097:
        r8 = r7.currentMessageObject;	 Catch:{ Exception -> 0x00ec }
        r8 = r8.linkDescription;	 Catch:{ Exception -> 0x00ec }
        r8 = (android.text.Spannable) r8;	 Catch:{ Exception -> 0x00ec }
        r0 = android.text.style.ClickableSpan.class;
        r0 = r8.getSpans(r2, r2, r0);	 Catch:{ Exception -> 0x00ec }
        r0 = (android.text.style.ClickableSpan[]) r0;	 Catch:{ Exception -> 0x00ec }
        r2 = r0.length;	 Catch:{ Exception -> 0x00ec }
        if (r2 == 0) goto L_0x00b8;
    L_0x00a8:
        r2 = r0.length;	 Catch:{ Exception -> 0x00ec }
        if (r2 == 0) goto L_0x00b6;
    L_0x00ab:
        r2 = r0[r1];	 Catch:{ Exception -> 0x00ec }
        r2 = r2 instanceof org.telegram.ui.Components.URLSpanBotCommand;	 Catch:{ Exception -> 0x00ec }
        if (r2 == 0) goto L_0x00b6;
    L_0x00b1:
        r2 = org.telegram.ui.Components.URLSpanBotCommand.enabled;	 Catch:{ Exception -> 0x00ec }
        if (r2 != 0) goto L_0x00b6;
    L_0x00b5:
        goto L_0x00b8;
    L_0x00b6:
        r2 = 0;
        goto L_0x00b9;
    L_0x00b8:
        r2 = 1;
    L_0x00b9:
        if (r2 != 0) goto L_0x0170;
    L_0x00bb:
        r0 = r0[r1];	 Catch:{ Exception -> 0x00ec }
        r7.pressedLink = r0;	 Catch:{ Exception -> 0x00ec }
        r0 = -10;
        r7.linkBlockNum = r0;	 Catch:{ Exception -> 0x00ec }
        r7.pressedLinkType = r4;	 Catch:{ Exception -> 0x00ec }
        r7.resetUrlPaths(r1);	 Catch:{ Exception -> 0x00ec }
        r0 = r7.obtainNewUrlPath(r1);	 Catch:{ Exception -> 0x00e4 }
        r2 = r7.pressedLink;	 Catch:{ Exception -> 0x00e4 }
        r8 = r7.getRealSpanStartAndEnd(r8, r2);	 Catch:{ Exception -> 0x00e4 }
        r2 = r7.descriptionLayout;	 Catch:{ Exception -> 0x00e4 }
        r3 = r8[r1];	 Catch:{ Exception -> 0x00e4 }
        r4 = 0;
        r0.setCurrentLayout(r2, r3, r4);	 Catch:{ Exception -> 0x00e4 }
        r2 = r7.descriptionLayout;	 Catch:{ Exception -> 0x00e4 }
        r3 = r8[r1];	 Catch:{ Exception -> 0x00e4 }
        r8 = r8[r5];	 Catch:{ Exception -> 0x00e4 }
        r2.getSelectionPath(r3, r8, r0);	 Catch:{ Exception -> 0x00e4 }
        goto L_0x00e8;
    L_0x00e4:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);	 Catch:{ Exception -> 0x00ec }
    L_0x00e8:
        r7.invalidate();	 Catch:{ Exception -> 0x00ec }
        return r5;
    L_0x00ec:
        r8 = move-exception;
        org.telegram.messenger.FileLog.e(r8);
        goto L_0x0170;
    L_0x00f2:
        r8 = r8.getAction();
        if (r8 != r5) goto L_0x0170;
    L_0x00f8:
        r8 = r7.pressedLinkType;
        if (r8 == r4) goto L_0x0109;
    L_0x00fc:
        r8 = r7.gamePreviewPressed;
        if (r8 != 0) goto L_0x0109;
    L_0x0100:
        r8 = r7.buttonPressed;
        if (r8 == 0) goto L_0x0105;
    L_0x0104:
        goto L_0x0109;
    L_0x0105:
        r7.resetPressedLink(r4);
        goto L_0x0170;
    L_0x0109:
        r8 = r7.buttonPressed;
        if (r8 == 0) goto L_0x0119;
    L_0x010d:
        r7.buttonPressed = r1;
        r7.playSoundEffect(r1);
        r7.didPressButton(r5, r1);
        r7.invalidate();
        goto L_0x0170;
    L_0x0119:
        r8 = r7.pressedLink;
        if (r8 == 0) goto L_0x013e;
    L_0x011d:
        r0 = r8 instanceof android.text.style.URLSpan;
        if (r0 == 0) goto L_0x0131;
    L_0x0121:
        r8 = r7.getContext();
        r0 = r7.pressedLink;
        r0 = (android.text.style.URLSpan) r0;
        r0 = r0.getURL();
        org.telegram.messenger.browser.Browser.openUrl(r8, r0);
        goto L_0x013a;
    L_0x0131:
        r0 = r8 instanceof android.text.style.ClickableSpan;
        if (r0 == 0) goto L_0x013a;
    L_0x0135:
        r8 = (android.text.style.ClickableSpan) r8;
        r8.onClick(r7);
    L_0x013a:
        r7.resetPressedLink(r4);
        goto L_0x0170;
    L_0x013e:
        r7.gamePreviewPressed = r1;
        r8 = 0;
    L_0x0141:
        r0 = r7.botButtons;
        r0 = r0.size();
        if (r8 >= r0) goto L_0x016c;
    L_0x0149:
        r0 = r7.botButtons;
        r0 = r0.get(r8);
        r0 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r0;
        r2 = r0.button;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
        if (r2 == 0) goto L_0x0169;
    L_0x0159:
        r7.playSoundEffect(r1);
        r8 = r7.delegate;
        r0 = r0.button;
        r8.didPressBotButton(r7, r0);
        r7.invalidate();
        goto L_0x016c;
    L_0x0169:
        r8 = r8 + 1;
        goto L_0x0141;
    L_0x016c:
        r7.resetPressedLink(r4);
        return r5;
    L_0x0170:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkGameMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:41:0x00b5 A:{Catch:{ Exception -> 0x00e6 }} */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0117  */
    /* JADX WARNING: Missing block: B:101:0x0185, code skipped:
            if (r1.radialProgress.getIcon() != 4) goto L_0x0187;
     */
    private boolean checkLinkPreviewMotionEvent(android.view.MotionEvent r17) {
        /*
        r16 = this;
        r1 = r16;
        r0 = r1.currentMessageObject;
        r0 = r0.type;
        r2 = 0;
        if (r0 != 0) goto L_0x0371;
    L_0x0009:
        r0 = r1.hasLinkPreview;
        if (r0 != 0) goto L_0x000f;
    L_0x000d:
        goto L_0x0371;
    L_0x000f:
        r0 = r17.getX();
        r3 = (int) r0;
        r0 = r17.getY();
        r4 = (int) r0;
        r0 = r1.unmovedTextX;
        if (r3 < r0) goto L_0x0371;
    L_0x001d:
        r5 = r1.backgroundWidth;
        r0 = r0 + r5;
        if (r3 > r0) goto L_0x0371;
    L_0x0022:
        r0 = r1.textY;
        r5 = r1.currentMessageObject;
        r5 = r5.textHeight;
        r6 = r0 + r5;
        if (r4 < r6) goto L_0x0371;
    L_0x002c:
        r0 = r0 + r5;
        r5 = r1.linkPreviewHeight;
        r0 = r0 + r5;
        r5 = r1.drawInstantView;
        if (r5 == 0) goto L_0x0037;
    L_0x0034:
        r5 = 46;
        goto L_0x0038;
    L_0x0037:
        r5 = 0;
    L_0x0038:
        r5 = r5 + 8;
        r5 = (float) r5;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0 = r0 + r5;
        if (r4 > r0) goto L_0x0371;
    L_0x0042:
        r0 = r17.getAction();
        r5 = 21;
        r6 = -1;
        r7 = 2;
        r8 = 1;
        if (r0 != 0) goto L_0x01f5;
    L_0x004d:
        r0 = r1.descriptionLayout;
        if (r0 == 0) goto L_0x00ea;
    L_0x0051:
        r0 = r1.descriptionY;
        if (r4 < r0) goto L_0x00ea;
    L_0x0055:
        r0 = r1.unmovedTextX;	 Catch:{ Exception -> 0x00e6 }
        r9 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Exception -> 0x00e6 }
        r0 = r0 + r9;
        r9 = r1.descriptionX;	 Catch:{ Exception -> 0x00e6 }
        r0 = r0 + r9;
        r0 = r3 - r0;
        r9 = r1.descriptionY;	 Catch:{ Exception -> 0x00e6 }
        r9 = r4 - r9;
        r10 = r1.descriptionLayout;	 Catch:{ Exception -> 0x00e6 }
        r10 = r10.getHeight();	 Catch:{ Exception -> 0x00e6 }
        if (r9 > r10) goto L_0x00ea;
    L_0x006f:
        r10 = r1.descriptionLayout;	 Catch:{ Exception -> 0x00e6 }
        r9 = r10.getLineForVertical(r9);	 Catch:{ Exception -> 0x00e6 }
        r10 = r1.descriptionLayout;	 Catch:{ Exception -> 0x00e6 }
        r0 = (float) r0;	 Catch:{ Exception -> 0x00e6 }
        r10 = r10.getOffsetForHorizontal(r9, r0);	 Catch:{ Exception -> 0x00e6 }
        r11 = r1.descriptionLayout;	 Catch:{ Exception -> 0x00e6 }
        r11 = r11.getLineLeft(r9);	 Catch:{ Exception -> 0x00e6 }
        r12 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1));
        if (r12 > 0) goto L_0x00ea;
    L_0x0086:
        r12 = r1.descriptionLayout;	 Catch:{ Exception -> 0x00e6 }
        r9 = r12.getLineWidth(r9);	 Catch:{ Exception -> 0x00e6 }
        r11 = r11 + r9;
        r0 = (r11 > r0 ? 1 : (r11 == r0 ? 0 : -1));
        if (r0 < 0) goto L_0x00ea;
    L_0x0091:
        r0 = r1.currentMessageObject;	 Catch:{ Exception -> 0x00e6 }
        r0 = r0.linkDescription;	 Catch:{ Exception -> 0x00e6 }
        r0 = (android.text.Spannable) r0;	 Catch:{ Exception -> 0x00e6 }
        r9 = android.text.style.ClickableSpan.class;
        r9 = r0.getSpans(r10, r10, r9);	 Catch:{ Exception -> 0x00e6 }
        r9 = (android.text.style.ClickableSpan[]) r9;	 Catch:{ Exception -> 0x00e6 }
        r10 = r9.length;	 Catch:{ Exception -> 0x00e6 }
        if (r10 == 0) goto L_0x00b2;
    L_0x00a2:
        r10 = r9.length;	 Catch:{ Exception -> 0x00e6 }
        if (r10 == 0) goto L_0x00b0;
    L_0x00a5:
        r10 = r9[r2];	 Catch:{ Exception -> 0x00e6 }
        r10 = r10 instanceof org.telegram.ui.Components.URLSpanBotCommand;	 Catch:{ Exception -> 0x00e6 }
        if (r10 == 0) goto L_0x00b0;
    L_0x00ab:
        r10 = org.telegram.ui.Components.URLSpanBotCommand.enabled;	 Catch:{ Exception -> 0x00e6 }
        if (r10 != 0) goto L_0x00b0;
    L_0x00af:
        goto L_0x00b2;
    L_0x00b0:
        r10 = 0;
        goto L_0x00b3;
    L_0x00b2:
        r10 = 1;
    L_0x00b3:
        if (r10 != 0) goto L_0x00ea;
    L_0x00b5:
        r9 = r9[r2];	 Catch:{ Exception -> 0x00e6 }
        r1.pressedLink = r9;	 Catch:{ Exception -> 0x00e6 }
        r9 = -10;
        r1.linkBlockNum = r9;	 Catch:{ Exception -> 0x00e6 }
        r1.pressedLinkType = r7;	 Catch:{ Exception -> 0x00e6 }
        r1.resetUrlPaths(r2);	 Catch:{ Exception -> 0x00e6 }
        r9 = r1.obtainNewUrlPath(r2);	 Catch:{ Exception -> 0x00de }
        r10 = r1.pressedLink;	 Catch:{ Exception -> 0x00de }
        r0 = r1.getRealSpanStartAndEnd(r0, r10);	 Catch:{ Exception -> 0x00de }
        r10 = r1.descriptionLayout;	 Catch:{ Exception -> 0x00de }
        r11 = r0[r2];	 Catch:{ Exception -> 0x00de }
        r12 = 0;
        r9.setCurrentLayout(r10, r11, r12);	 Catch:{ Exception -> 0x00de }
        r10 = r1.descriptionLayout;	 Catch:{ Exception -> 0x00de }
        r11 = r0[r2];	 Catch:{ Exception -> 0x00de }
        r0 = r0[r8];	 Catch:{ Exception -> 0x00de }
        r10.getSelectionPath(r11, r0, r9);	 Catch:{ Exception -> 0x00de }
        goto L_0x00e2;
    L_0x00de:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ Exception -> 0x00e6 }
    L_0x00e2:
        r16.invalidate();	 Catch:{ Exception -> 0x00e6 }
        return r8;
    L_0x00e6:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00ea:
        r0 = r1.pressedLink;
        if (r0 != 0) goto L_0x0371;
    L_0x00ee:
        r0 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r10 = r1.miniButtonState;
        if (r10 < 0) goto L_0x0114;
    L_0x00f8:
        r10 = NUM; // 0x41d80000 float:27.0 double:5.457818764E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = r1.buttonX;
        r12 = r11 + r10;
        if (r3 < r12) goto L_0x0114;
    L_0x0104:
        r11 = r11 + r10;
        r11 = r11 + r9;
        if (r3 > r11) goto L_0x0114;
    L_0x0108:
        r11 = r1.buttonY;
        r12 = r11 + r10;
        if (r4 < r12) goto L_0x0114;
    L_0x010e:
        r11 = r11 + r10;
        r11 = r11 + r9;
        if (r4 > r11) goto L_0x0114;
    L_0x0112:
        r9 = 1;
        goto L_0x0115;
    L_0x0114:
        r9 = 0;
    L_0x0115:
        if (r9 == 0) goto L_0x011d;
    L_0x0117:
        r1.miniButtonPressed = r8;
        r16.invalidate();
        return r8;
    L_0x011d:
        r9 = r1.drawVideoImageButton;
        if (r9 == 0) goto L_0x014e;
    L_0x0121:
        r9 = r1.buttonState;
        if (r9 == r6) goto L_0x014e;
    L_0x0125:
        r9 = r1.videoButtonX;
        if (r3 < r9) goto L_0x014e;
    L_0x0129:
        r10 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r9 = r9 + r10;
        r10 = r1.infoWidth;
        r11 = r1.docTitleWidth;
        r10 = java.lang.Math.max(r10, r11);
        r9 = r9 + r10;
        if (r3 > r9) goto L_0x014e;
    L_0x013b:
        r9 = r1.videoButtonY;
        if (r4 < r9) goto L_0x014e;
    L_0x013f:
        r10 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r9 = r9 + r10;
        if (r4 > r9) goto L_0x014e;
    L_0x0148:
        r1.videoButtonPressed = r8;
        r16.invalidate();
        return r8;
    L_0x014e:
        r9 = r1.drawPhotoImage;
        if (r9 == 0) goto L_0x018d;
    L_0x0152:
        r9 = r1.drawImageButton;
        if (r9 == 0) goto L_0x018d;
    L_0x0156:
        r9 = r1.buttonState;
        if (r9 == r6) goto L_0x018d;
    L_0x015a:
        r9 = r1.checkOnlyButtonPressed;
        if (r9 != 0) goto L_0x0168;
    L_0x015e:
        r9 = r1.photoImage;
        r10 = (float) r3;
        r11 = (float) r4;
        r9 = r9.isInsideImage(r10, r11);
        if (r9 != 0) goto L_0x0187;
    L_0x0168:
        r9 = r1.buttonX;
        if (r3 < r9) goto L_0x018d;
    L_0x016c:
        r10 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r9 = r9 + r10;
        if (r3 > r9) goto L_0x018d;
    L_0x0173:
        r9 = r1.buttonY;
        if (r4 < r9) goto L_0x018d;
    L_0x0177:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r9 = r9 + r0;
        if (r4 > r9) goto L_0x018d;
    L_0x017e:
        r0 = r1.radialProgress;
        r0 = r0.getIcon();
        r9 = 4;
        if (r0 == r9) goto L_0x018d;
    L_0x0187:
        r1.buttonPressed = r8;
        r16.invalidate();
        return r8;
    L_0x018d:
        r0 = r1.drawInstantView;
        if (r0 == 0) goto L_0x01b9;
    L_0x0191:
        r1.instantPressed = r8;
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r5) goto L_0x01b5;
    L_0x0197:
        r0 = r1.selectorDrawable;
        if (r0 == 0) goto L_0x01b5;
    L_0x019b:
        r0 = r0.getBounds();
        r0 = r0.contains(r3, r4);
        if (r0 == 0) goto L_0x01b5;
    L_0x01a5:
        r0 = r1.selectorDrawable;
        r2 = r1.pressedState;
        r0.setState(r2);
        r0 = r1.selectorDrawable;
        r2 = (float) r3;
        r3 = (float) r4;
        r0.setHotspot(r2, r3);
        r1.instantButtonPressed = r8;
    L_0x01b5:
        r16.invalidate();
        return r8;
    L_0x01b9:
        r0 = r1.documentAttachType;
        if (r0 == r8) goto L_0x0371;
    L_0x01bd:
        r0 = r1.drawPhotoImage;
        if (r0 == 0) goto L_0x0371;
    L_0x01c1:
        r0 = r1.photoImage;
        r3 = (float) r3;
        r4 = (float) r4;
        r0 = r0.isInsideImage(r3, r4);
        if (r0 == 0) goto L_0x0371;
    L_0x01cb:
        r1.linkPreviewPressed = r8;
        r0 = r1.currentMessageObject;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.webpage;
        r3 = r1.documentAttachType;
        if (r3 != r7) goto L_0x01f4;
    L_0x01d9:
        r3 = r1.buttonState;
        if (r3 != r6) goto L_0x01f4;
    L_0x01dd:
        r3 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r3 == 0) goto L_0x01f4;
    L_0x01e1:
        r3 = r1.photoImage;
        r3 = r3.getAnimation();
        if (r3 == 0) goto L_0x01f1;
    L_0x01e9:
        r0 = r0.embed_url;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x01f4;
    L_0x01f1:
        r1.linkPreviewPressed = r2;
        return r2;
    L_0x01f4:
        return r8;
    L_0x01f5:
        r0 = r17.getAction();
        if (r0 != r8) goto L_0x035a;
    L_0x01fb:
        r0 = r1.instantPressed;
        if (r0 == 0) goto L_0x0221;
    L_0x01ff:
        r0 = r1.delegate;
        if (r0 == 0) goto L_0x0208;
    L_0x0203:
        r3 = r1.drawInstantViewType;
        r0.didPressInstantButton(r1, r3);
    L_0x0208:
        r1.playSoundEffect(r2);
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r5) goto L_0x0218;
    L_0x020f:
        r0 = r1.selectorDrawable;
        if (r0 == 0) goto L_0x0218;
    L_0x0213:
        r3 = android.util.StateSet.NOTHING;
        r0.setState(r3);
    L_0x0218:
        r1.instantButtonPressed = r2;
        r1.instantPressed = r2;
        r16.invalidate();
        goto L_0x0371;
    L_0x0221:
        r0 = r1.pressedLinkType;
        if (r0 == r7) goto L_0x023b;
    L_0x0225:
        r0 = r1.buttonPressed;
        if (r0 != 0) goto L_0x023b;
    L_0x0229:
        r0 = r1.miniButtonPressed;
        if (r0 != 0) goto L_0x023b;
    L_0x022d:
        r0 = r1.videoButtonPressed;
        if (r0 != 0) goto L_0x023b;
    L_0x0231:
        r0 = r1.linkPreviewPressed;
        if (r0 == 0) goto L_0x0236;
    L_0x0235:
        goto L_0x023b;
    L_0x0236:
        r1.resetPressedLink(r7);
        goto L_0x0371;
    L_0x023b:
        r0 = r1.videoButtonPressed;
        if (r0 != r8) goto L_0x024c;
    L_0x023f:
        r1.videoButtonPressed = r2;
        r1.playSoundEffect(r2);
        r1.didPressButton(r8, r8);
        r16.invalidate();
        goto L_0x0371;
    L_0x024c:
        r0 = r1.buttonPressed;
        if (r0 == 0) goto L_0x0265;
    L_0x0250:
        r1.buttonPressed = r2;
        r1.playSoundEffect(r2);
        r0 = r1.drawVideoImageButton;
        if (r0 == 0) goto L_0x025d;
    L_0x0259:
        r16.didClickedImage();
        goto L_0x0260;
    L_0x025d:
        r1.didPressButton(r8, r2);
    L_0x0260:
        r16.invalidate();
        goto L_0x0371;
    L_0x0265:
        r0 = r1.miniButtonPressed;
        if (r0 == 0) goto L_0x0276;
    L_0x0269:
        r1.miniButtonPressed = r2;
        r1.playSoundEffect(r2);
        r1.didPressMiniButton(r8);
        r16.invalidate();
        goto L_0x0371;
    L_0x0276:
        r0 = r1.pressedLink;
        if (r0 == 0) goto L_0x029c;
    L_0x027a:
        r3 = r0 instanceof android.text.style.URLSpan;
        if (r3 == 0) goto L_0x028e;
    L_0x027e:
        r0 = r16.getContext();
        r3 = r1.pressedLink;
        r3 = (android.text.style.URLSpan) r3;
        r3 = r3.getURL();
        org.telegram.messenger.browser.Browser.openUrl(r0, r3);
        goto L_0x0297;
    L_0x028e:
        r3 = r0 instanceof android.text.style.ClickableSpan;
        if (r3 == 0) goto L_0x0297;
    L_0x0292:
        r0 = (android.text.style.ClickableSpan) r0;
        r0.onClick(r1);
    L_0x0297:
        r1.resetPressedLink(r7);
        goto L_0x0371;
    L_0x029c:
        r0 = r1.documentAttachType;
        r3 = 7;
        if (r0 != r3) goto L_0x02cc;
    L_0x02a1:
        r0 = org.telegram.messenger.MediaController.getInstance();
        r2 = r1.currentMessageObject;
        r0 = r0.isPlayingMessage(r2);
        if (r0 == 0) goto L_0x02c3;
    L_0x02ad:
        r0 = org.telegram.messenger.MediaController.getInstance();
        r0 = r0.isMessagePaused();
        if (r0 == 0) goto L_0x02b8;
    L_0x02b7:
        goto L_0x02c3;
    L_0x02b8:
        r0 = org.telegram.messenger.MediaController.getInstance();
        r2 = r1.currentMessageObject;
        r0.lambda$startAudioAgain$5$MediaController(r2);
        goto L_0x0356;
    L_0x02c3:
        r0 = r1.delegate;
        r2 = r1.currentMessageObject;
        r0.needPlayMessage(r2);
        goto L_0x0356;
    L_0x02cc:
        if (r0 != r7) goto L_0x0312;
    L_0x02ce:
        r0 = r1.drawImageButton;
        if (r0 == 0) goto L_0x0312;
    L_0x02d2:
        r0 = r1.buttonState;
        if (r0 != r6) goto L_0x0307;
    L_0x02d6:
        r0 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r0 == 0) goto L_0x02e5;
    L_0x02da:
        r0 = r1.delegate;
        r2 = r1.lastTouchX;
        r3 = r1.lastTouchY;
        r0.didPressImage(r1, r2, r3);
        goto L_0x0356;
    L_0x02e5:
        r1.buttonState = r7;
        r0 = r1.currentMessageObject;
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0.gifState = r3;
        r0 = r1.photoImage;
        r0.setAllowStartAnimation(r2);
        r0 = r1.photoImage;
        r0.stopAnimation();
        r0 = r1.radialProgress;
        r3 = r16.getIconForCurrentState();
        r0.setIcon(r3, r2, r8);
        r16.invalidate();
        r1.playSoundEffect(r2);
        goto L_0x0356;
    L_0x0307:
        if (r0 == r7) goto L_0x030b;
    L_0x0309:
        if (r0 != 0) goto L_0x0356;
    L_0x030b:
        r1.didPressButton(r8, r2);
        r1.playSoundEffect(r2);
        goto L_0x0356;
    L_0x0312:
        r0 = r1.currentMessageObject;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.webpage;
        if (r0 == 0) goto L_0x0336;
    L_0x031c:
        r3 = r0.embed_url;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0336;
    L_0x0324:
        r9 = r1.delegate;
        r10 = r0.embed_url;
        r11 = r0.site_name;
        r12 = r0.title;
        r13 = r0.url;
        r14 = r0.embed_width;
        r15 = r0.embed_height;
        r9.needOpenWebView(r10, r11, r12, r13, r14, r15);
        goto L_0x0356;
    L_0x0336:
        r3 = r1.buttonState;
        if (r3 == r6) goto L_0x034a;
    L_0x033a:
        r4 = 3;
        if (r3 != r4) goto L_0x033e;
    L_0x033d:
        goto L_0x034a;
    L_0x033e:
        if (r0 == 0) goto L_0x0356;
    L_0x0340:
        r2 = r16.getContext();
        r0 = r0.url;
        org.telegram.messenger.browser.Browser.openUrl(r2, r0);
        goto L_0x0356;
    L_0x034a:
        r0 = r1.delegate;
        r3 = r1.lastTouchX;
        r4 = r1.lastTouchY;
        r0.didPressImage(r1, r3, r4);
        r1.playSoundEffect(r2);
    L_0x0356:
        r1.resetPressedLink(r7);
        return r8;
    L_0x035a:
        r0 = r17.getAction();
        if (r0 != r7) goto L_0x0371;
    L_0x0360:
        r0 = r1.instantButtonPressed;
        if (r0 == 0) goto L_0x0371;
    L_0x0364:
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r5) goto L_0x0371;
    L_0x0368:
        r0 = r1.selectorDrawable;
        if (r0 == 0) goto L_0x0371;
    L_0x036c:
        r3 = (float) r3;
        r4 = (float) r4;
        r0.setHotspot(r3, r4);
    L_0x0371:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkLinkPreviewMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkPollButtonMotionEvent(MotionEvent motionEvent) {
        if (this.currentMessageObject.eventId != 0 || this.pollVoted || this.pollClosed || this.pollVoteInProgress || this.pollUnvoteInProgress || this.pollButtons.isEmpty()) {
            return false;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.type != 17 || !messageObject.isSent()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int i;
        Drawable drawable;
        if (motionEvent.getAction() == 0) {
            this.pressedVoteButton = -1;
            i = 0;
            while (i < this.pollButtons.size()) {
                PollButton pollButton = (PollButton) this.pollButtons.get(i);
                int access$600 = (pollButton.y + this.namesOffset) - AndroidUtilities.dp(13.0f);
                if (x < pollButton.x || x > (pollButton.x + this.backgroundWidth) - AndroidUtilities.dp(31.0f) || y < access$600 || y > (pollButton.height + access$600) + AndroidUtilities.dp(26.0f)) {
                    i++;
                } else {
                    this.pressedVoteButton = i;
                    if (VERSION.SDK_INT >= 21) {
                        drawable = this.selectorDrawable;
                        if (drawable != null) {
                            drawable.setBounds(pollButton.x - AndroidUtilities.dp(9.0f), access$600, (pollButton.x + this.backgroundWidth) - AndroidUtilities.dp(22.0f), (pollButton.height + access$600) + AndroidUtilities.dp(26.0f));
                            this.selectorDrawable.setState(this.pressedState);
                            this.selectorDrawable.setHotspot((float) x, (float) y);
                        }
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
            if (VERSION.SDK_INT >= 21) {
                drawable = this.selectorDrawable;
                if (drawable != null) {
                    drawable.setState(StateSet.NOTHING);
                }
            }
            i = this.pressedVoteButton;
            this.pollVoteInProgressNum = i;
            this.pollVoteInProgress = true;
            this.voteCurrentProgressTime = 0.0f;
            this.firstCircleLength = true;
            this.voteCurrentCircleLength = 360.0f;
            this.voteRisingCircleLength = false;
            this.delegate.didPressVoteButton(this, ((PollButton) this.pollButtons.get(i)).answer);
            this.pressedVoteButton = -1;
            invalidate();
            return false;
        } else if (motionEvent.getAction() != 2 || this.pressedVoteButton == -1 || VERSION.SDK_INT < 21) {
            return false;
        } else {
            drawable = this.selectorDrawable;
            if (drawable == null) {
                return false;
            }
            drawable.setHotspot((float) x, (float) y);
            return false;
        }
    }

    private boolean checkInstantButtonMotionEvent(MotionEvent motionEvent) {
        if (this.drawInstantView && this.currentMessageObject.type != 0) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            Drawable drawable;
            if (motionEvent.getAction() == 0) {
                if (this.drawInstantView) {
                    float f = (float) x;
                    float f2 = (float) y;
                    if (this.instantButtonRect.contains(f, f2)) {
                        this.instantPressed = true;
                        if (VERSION.SDK_INT >= 21) {
                            drawable = this.selectorDrawable;
                            if (drawable != null && drawable.getBounds().contains(x, y)) {
                                this.selectorDrawable.setState(this.pressedState);
                                this.selectorDrawable.setHotspot(f, f2);
                                this.instantButtonPressed = true;
                            }
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
                    if (VERSION.SDK_INT >= 21) {
                        drawable = this.selectorDrawable;
                        if (drawable != null) {
                            drawable.setState(StateSet.NOTHING);
                        }
                    }
                    this.instantButtonPressed = false;
                    this.instantPressed = false;
                    invalidate();
                }
            } else if (motionEvent.getAction() == 2 && this.instantButtonPressed && VERSION.SDK_INT >= 21) {
                drawable = this.selectorDrawable;
                if (drawable != null) {
                    drawable.setHotspot((float) x, (float) y);
                }
            }
        }
        return false;
    }

    /* JADX WARNING: Missing block: B:16:0x0026, code skipped:
            if (r4 != 8) goto L_0x0032;
     */
    private boolean checkOtherButtonMotionEvent(android.view.MotionEvent r7) {
        /*
        r6 = this;
        r0 = r6.currentMessageObject;
        r0 = r0.type;
        r1 = 16;
        r2 = 0;
        r3 = 1;
        if (r0 != r1) goto L_0x000c;
    L_0x000a:
        r0 = 1;
        goto L_0x000d;
    L_0x000c:
        r0 = 0;
    L_0x000d:
        if (r0 != 0) goto L_0x0033;
    L_0x000f:
        r0 = r6.documentAttachType;
        if (r0 == r3) goto L_0x0028;
    L_0x0013:
        r4 = r6.currentMessageObject;
        r4 = r4.type;
        r5 = 12;
        if (r4 == r5) goto L_0x0028;
    L_0x001b:
        r5 = 5;
        if (r0 == r5) goto L_0x0028;
    L_0x001e:
        r5 = 4;
        if (r0 == r5) goto L_0x0028;
    L_0x0021:
        r5 = 2;
        if (r0 == r5) goto L_0x0028;
    L_0x0024:
        r0 = 8;
        if (r4 != r0) goto L_0x0032;
    L_0x0028:
        r0 = r6.hasGamePreview;
        if (r0 != 0) goto L_0x0032;
    L_0x002c:
        r0 = r6.hasInvoicePreview;
        if (r0 != 0) goto L_0x0032;
    L_0x0030:
        r0 = 1;
        goto L_0x0033;
    L_0x0032:
        r0 = 0;
    L_0x0033:
        if (r0 != 0) goto L_0x0036;
    L_0x0035:
        return r2;
    L_0x0036:
        r0 = r7.getX();
        r0 = (int) r0;
        r4 = r7.getY();
        r4 = (int) r4;
        r5 = r7.getAction();
        if (r5 != 0) goto L_0x00a5;
    L_0x0046:
        r7 = r6.currentMessageObject;
        r7 = r7.type;
        if (r7 != r1) goto L_0x0075;
    L_0x004c:
        r7 = r6.otherX;
        if (r0 < r7) goto L_0x00c3;
    L_0x0050:
        r1 = NUM; // 0x436b0000 float:235.0 double:5.58830648E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r7 = r7 + r1;
        if (r0 > r7) goto L_0x00c3;
    L_0x0059:
        r7 = r6.otherY;
        r0 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r7 = r7 - r0;
        if (r4 < r7) goto L_0x00c3;
    L_0x0064:
        r7 = r6.otherY;
        r0 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r7 = r7 + r0;
        if (r4 > r7) goto L_0x00c3;
    L_0x006f:
        r6.otherPressed = r3;
        r6.invalidate();
        goto L_0x00c4;
    L_0x0075:
        r7 = r6.otherX;
        r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r7 = r7 - r5;
        if (r0 < r7) goto L_0x00c3;
    L_0x0080:
        r7 = r6.otherX;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r7 = r7 + r1;
        if (r0 > r7) goto L_0x00c3;
    L_0x0089:
        r7 = r6.otherY;
        r0 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r7 = r7 - r0;
        if (r4 < r7) goto L_0x00c3;
    L_0x0094:
        r7 = r6.otherY;
        r0 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r7 = r7 + r0;
        if (r4 > r7) goto L_0x00c3;
    L_0x009f:
        r6.otherPressed = r3;
        r6.invalidate();
        goto L_0x00c4;
    L_0x00a5:
        r7 = r7.getAction();
        if (r7 != r3) goto L_0x00c3;
    L_0x00ab:
        r7 = r6.otherPressed;
        if (r7 == 0) goto L_0x00c3;
    L_0x00af:
        r6.otherPressed = r2;
        r6.playSoundEffect(r2);
        r7 = r6.delegate;
        r0 = r6.otherX;
        r0 = (float) r0;
        r1 = r6.otherY;
        r1 = (float) r1;
        r7.didPressOther(r6, r0, r1);
        r6.invalidate();
        goto L_0x00c4;
    L_0x00c3:
        r3 = 0;
    L_0x00c4:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkOtherButtonMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x004c  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0045  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0144  */
    private boolean checkPhotoImageMotionEvent(android.view.MotionEvent r9) {
        /*
        r8 = this;
        r0 = r8.drawPhotoImage;
        r1 = 0;
        r2 = 1;
        if (r0 != 0) goto L_0x000b;
    L_0x0006:
        r0 = r8.documentAttachType;
        if (r0 == r2) goto L_0x000b;
    L_0x000a:
        return r1;
    L_0x000b:
        r0 = r9.getX();
        r0 = (int) r0;
        r3 = r9.getY();
        r3 = (int) r3;
        r4 = r9.getAction();
        r5 = -1;
        if (r4 != 0) goto L_0x016e;
    L_0x001c:
        r9 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r4 = r8.miniButtonState;
        if (r4 < 0) goto L_0x0042;
    L_0x0026:
        r4 = NUM; // 0x41d80000 float:27.0 double:5.457818764E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = r8.buttonX;
        r7 = r6 + r4;
        if (r0 < r7) goto L_0x0042;
    L_0x0032:
        r6 = r6 + r4;
        r6 = r6 + r9;
        if (r0 > r6) goto L_0x0042;
    L_0x0036:
        r6 = r8.buttonY;
        r7 = r6 + r4;
        if (r3 < r7) goto L_0x0042;
    L_0x003c:
        r6 = r6 + r4;
        r6 = r6 + r9;
        if (r3 > r6) goto L_0x0042;
    L_0x0040:
        r4 = 1;
        goto L_0x0043;
    L_0x0042:
        r4 = 0;
    L_0x0043:
        if (r4 == 0) goto L_0x004c;
    L_0x0045:
        r8.miniButtonPressed = r2;
        r8.invalidate();
        goto L_0x0140;
    L_0x004c:
        r4 = r8.buttonState;
        if (r4 == r5) goto L_0x006e;
    L_0x0050:
        r4 = r8.radialProgress;
        r4 = r4.getIcon();
        r6 = 4;
        if (r4 == r6) goto L_0x006e;
    L_0x0059:
        r4 = r8.buttonX;
        if (r0 < r4) goto L_0x006e;
    L_0x005d:
        r4 = r4 + r9;
        if (r0 > r4) goto L_0x006e;
    L_0x0060:
        r4 = r8.buttonY;
        if (r3 < r4) goto L_0x006e;
    L_0x0064:
        r4 = r4 + r9;
        if (r3 > r4) goto L_0x006e;
    L_0x0067:
        r8.buttonPressed = r2;
        r8.invalidate();
        goto L_0x0140;
    L_0x006e:
        r9 = r8.drawVideoImageButton;
        if (r9 == 0) goto L_0x00a0;
    L_0x0072:
        r9 = r8.buttonState;
        if (r9 == r5) goto L_0x00a0;
    L_0x0076:
        r9 = r8.videoButtonX;
        if (r0 < r9) goto L_0x00a0;
    L_0x007a:
        r4 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = r9 + r4;
        r4 = r8.infoWidth;
        r6 = r8.docTitleWidth;
        r4 = java.lang.Math.max(r4, r6);
        r9 = r9 + r4;
        if (r0 > r9) goto L_0x00a0;
    L_0x008c:
        r9 = r8.videoButtonY;
        if (r3 < r9) goto L_0x00a0;
    L_0x0090:
        r4 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = r9 + r4;
        if (r3 > r9) goto L_0x00a0;
    L_0x0099:
        r8.videoButtonPressed = r2;
        r8.invalidate();
        goto L_0x0140;
    L_0x00a0:
        r9 = r8.documentAttachType;
        if (r9 != r2) goto L_0x00d8;
    L_0x00a4:
        r9 = r8.photoImage;
        r9 = r9.getImageX();
        if (r0 < r9) goto L_0x00e9;
    L_0x00ac:
        r9 = r8.photoImage;
        r9 = r9.getImageX();
        r4 = r8.backgroundWidth;
        r9 = r9 + r4;
        r4 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r9 = r9 - r4;
        if (r0 > r9) goto L_0x00e9;
    L_0x00be:
        r9 = r8.photoImage;
        r9 = r9.getImageY();
        if (r3 < r9) goto L_0x00e9;
    L_0x00c6:
        r9 = r8.photoImage;
        r9 = r9.getImageY();
        r0 = r8.photoImage;
        r0 = r0.getImageHeight();
        r9 = r9 + r0;
        if (r3 > r9) goto L_0x00e9;
    L_0x00d5:
        r8.imagePressed = r2;
        goto L_0x0140;
    L_0x00d8:
        r9 = r8.currentMessageObject;
        r9 = r9.isAnyKindOfSticker();
        if (r9 == 0) goto L_0x00eb;
    L_0x00e0:
        r9 = r8.currentMessageObject;
        r9 = r9.getInputStickerSet();
        if (r9 == 0) goto L_0x00e9;
    L_0x00e8:
        goto L_0x00eb;
    L_0x00e9:
        r2 = 0;
        goto L_0x0140;
    L_0x00eb:
        r9 = r8.photoImage;
        r9 = r9.getImageX();
        if (r0 < r9) goto L_0x011c;
    L_0x00f3:
        r9 = r8.photoImage;
        r9 = r9.getImageX();
        r4 = r8.photoImage;
        r4 = r4.getImageWidth();
        r9 = r9 + r4;
        if (r0 > r9) goto L_0x011c;
    L_0x0102:
        r9 = r8.photoImage;
        r9 = r9.getImageY();
        if (r3 < r9) goto L_0x011c;
    L_0x010a:
        r9 = r8.photoImage;
        r9 = r9.getImageY();
        r0 = r8.photoImage;
        r0 = r0.getImageHeight();
        r9 = r9 + r0;
        if (r3 > r9) goto L_0x011c;
    L_0x0119:
        r8.imagePressed = r2;
        goto L_0x011d;
    L_0x011c:
        r2 = 0;
    L_0x011d:
        r9 = r8.currentMessageObject;
        r9 = r9.type;
        r0 = 12;
        if (r9 != r0) goto L_0x0140;
    L_0x0125:
        r9 = r8.currentAccount;
        r9 = org.telegram.messenger.MessagesController.getInstance(r9);
        r0 = r8.currentMessageObject;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.user_id;
        r0 = java.lang.Integer.valueOf(r0);
        r9 = r9.getUser(r0);
        if (r9 != 0) goto L_0x0140;
    L_0x013d:
        r8.imagePressed = r1;
        goto L_0x00e9;
    L_0x0140:
        r9 = r8.imagePressed;
        if (r9 == 0) goto L_0x016c;
    L_0x0144:
        r9 = r8.currentMessageObject;
        r9 = r9.isSendError();
        if (r9 == 0) goto L_0x0150;
    L_0x014c:
        r8.imagePressed = r1;
        goto L_0x01d3;
    L_0x0150:
        r9 = r8.currentMessageObject;
        r9 = r9.type;
        r0 = 8;
        if (r9 != r0) goto L_0x016c;
    L_0x0158:
        r9 = r8.buttonState;
        if (r9 != r5) goto L_0x016c;
    L_0x015c:
        r9 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r9 == 0) goto L_0x016c;
    L_0x0160:
        r9 = r8.photoImage;
        r9 = r9.getAnimation();
        if (r9 != 0) goto L_0x016c;
    L_0x0168:
        r8.imagePressed = r1;
        goto L_0x01d3;
    L_0x016c:
        r1 = r2;
        goto L_0x01d3;
    L_0x016e:
        r9 = r9.getAction();
        if (r9 != r2) goto L_0x01d3;
    L_0x0174:
        r9 = r8.videoButtonPressed;
        if (r9 != r2) goto L_0x0184;
    L_0x0178:
        r8.videoButtonPressed = r1;
        r8.playSoundEffect(r1);
        r8.didPressButton(r2, r2);
        r8.invalidate();
        goto L_0x01d3;
    L_0x0184:
        r9 = r8.buttonPressed;
        if (r9 != r2) goto L_0x019c;
    L_0x0188:
        r8.buttonPressed = r1;
        r8.playSoundEffect(r1);
        r9 = r8.drawVideoImageButton;
        if (r9 == 0) goto L_0x0195;
    L_0x0191:
        r8.didClickedImage();
        goto L_0x0198;
    L_0x0195:
        r8.didPressButton(r2, r1);
    L_0x0198:
        r8.invalidate();
        goto L_0x01d3;
    L_0x019c:
        r9 = r8.miniButtonPressed;
        if (r9 != r2) goto L_0x01ac;
    L_0x01a0:
        r8.miniButtonPressed = r1;
        r8.playSoundEffect(r1);
        r8.didPressMiniButton(r2);
        r8.invalidate();
        goto L_0x01d3;
    L_0x01ac:
        r9 = r8.imagePressed;
        if (r9 == 0) goto L_0x01d3;
    L_0x01b0:
        r8.imagePressed = r1;
        r9 = r8.buttonState;
        if (r9 == r5) goto L_0x01ca;
    L_0x01b6:
        r0 = 2;
        if (r9 == r0) goto L_0x01ca;
    L_0x01b9:
        r0 = 3;
        if (r9 == r0) goto L_0x01ca;
    L_0x01bc:
        r0 = r8.drawVideoImageButton;
        if (r0 == 0) goto L_0x01c1;
    L_0x01c0:
        goto L_0x01ca;
    L_0x01c1:
        if (r9 != 0) goto L_0x01d0;
    L_0x01c3:
        r8.playSoundEffect(r1);
        r8.didPressButton(r2, r1);
        goto L_0x01d0;
    L_0x01ca:
        r8.playSoundEffect(r1);
        r8.didClickedImage();
    L_0x01d0:
        r8.invalidate();
    L_0x01d3:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkPhotoImageMotionEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARNING: Removed duplicated region for block: B:75:0x011b  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x010b A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00b0  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x010b A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x011b  */
    /* JADX WARNING: Missing block: B:49:0x00c5, code skipped:
            if (r3 <= (r0 + r6)) goto L_0x00c7;
     */
    /* JADX WARNING: Missing block: B:65:0x0101, code skipped:
            if (r3 <= (r0 + r6)) goto L_0x00c7;
     */
    private boolean checkAudioMotionEvent(android.view.MotionEvent r13) {
        /*
        r12 = this;
        r0 = r12.documentAttachType;
        r1 = 3;
        r2 = 0;
        if (r0 == r1) goto L_0x000a;
    L_0x0006:
        r3 = 5;
        if (r0 == r3) goto L_0x000a;
    L_0x0009:
        return r2;
    L_0x000a:
        r0 = r13.getX();
        r0 = (int) r0;
        r3 = r13.getY();
        r3 = (int) r3;
        r4 = r12.useSeekBarWaweform;
        if (r4 == 0) goto L_0x003b;
    L_0x0018:
        r4 = r12.seekBarWaveform;
        r5 = r13.getAction();
        r6 = r13.getX();
        r7 = r12.seekBarX;
        r7 = (float) r7;
        r6 = r6 - r7;
        r7 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = (float) r7;
        r6 = r6 - r7;
        r7 = r13.getY();
        r8 = r12.seekBarY;
        r8 = (float) r8;
        r7 = r7 - r8;
        r4 = r4.onTouch(r5, r6, r7);
        goto L_0x0055;
    L_0x003b:
        r4 = r12.seekBar;
        r5 = r13.getAction();
        r6 = r13.getX();
        r7 = r12.seekBarX;
        r7 = (float) r7;
        r6 = r6 - r7;
        r7 = r13.getY();
        r8 = r12.seekBarY;
        r8 = (float) r8;
        r7 = r7 - r8;
        r4 = r4.onTouch(r5, r6, r7);
    L_0x0055:
        r5 = 1;
        if (r4 == 0) goto L_0x0086;
    L_0x0058:
        r0 = r12.useSeekBarWaweform;
        if (r0 != 0) goto L_0x006a;
    L_0x005c:
        r0 = r13.getAction();
        if (r0 != 0) goto L_0x006a;
    L_0x0062:
        r13 = r12.getParent();
        r13.requestDisallowInterceptTouchEvent(r5);
        goto L_0x007f;
    L_0x006a:
        r0 = r12.useSeekBarWaweform;
        if (r0 == 0) goto L_0x007f;
    L_0x006e:
        r0 = r12.seekBarWaveform;
        r0 = r0.isStartDraging();
        if (r0 != 0) goto L_0x007f;
    L_0x0076:
        r13 = r13.getAction();
        if (r13 != r5) goto L_0x007f;
    L_0x007c:
        r12.didPressButton(r5, r2);
    L_0x007f:
        r12.disallowLongPress = r5;
        r12.invalidate();
        goto L_0x017a;
    L_0x0086:
        r6 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = r12.miniButtonState;
        if (r7 < 0) goto L_0x00ac;
    L_0x0090:
        r7 = NUM; // 0x41d80000 float:27.0 double:5.457818764E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r8 = r12.buttonX;
        r9 = r8 + r7;
        if (r0 < r9) goto L_0x00ac;
    L_0x009c:
        r8 = r8 + r7;
        r8 = r8 + r6;
        if (r0 > r8) goto L_0x00ac;
    L_0x00a0:
        r8 = r12.buttonY;
        r9 = r8 + r7;
        if (r3 < r9) goto L_0x00ac;
    L_0x00a6:
        r8 = r8 + r7;
        r8 = r8 + r6;
        if (r3 > r8) goto L_0x00ac;
    L_0x00aa:
        r7 = 1;
        goto L_0x00ad;
    L_0x00ac:
        r7 = 0;
    L_0x00ad:
        r8 = 2;
        if (r7 != 0) goto L_0x0104;
    L_0x00b0:
        r9 = r12.buttonState;
        if (r9 == 0) goto L_0x00c9;
    L_0x00b4:
        if (r9 == r5) goto L_0x00c9;
    L_0x00b6:
        if (r9 != r8) goto L_0x00b9;
    L_0x00b8:
        goto L_0x00c9;
    L_0x00b9:
        r9 = r12.buttonX;
        if (r0 < r9) goto L_0x0104;
    L_0x00bd:
        r9 = r9 + r6;
        if (r0 > r9) goto L_0x0104;
    L_0x00c0:
        r0 = r12.buttonY;
        if (r3 < r0) goto L_0x0104;
    L_0x00c4:
        r0 = r0 + r6;
        if (r3 > r0) goto L_0x0104;
    L_0x00c7:
        r0 = 1;
        goto L_0x0105;
    L_0x00c9:
        r9 = r12.buttonX;
        r10 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r9 = r9 - r11;
        if (r0 < r9) goto L_0x0104;
    L_0x00d4:
        r9 = r12.buttonX;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r9 = r9 - r10;
        r10 = r12.backgroundWidth;
        r9 = r9 + r10;
        if (r0 > r9) goto L_0x0104;
    L_0x00e0:
        r0 = r12.drawInstantView;
        if (r0 == 0) goto L_0x00e7;
    L_0x00e4:
        r0 = r12.buttonY;
        goto L_0x00ec;
    L_0x00e7:
        r0 = r12.namesOffset;
        r9 = r12.mediaOffsetY;
        r0 = r0 + r9;
    L_0x00ec:
        if (r3 < r0) goto L_0x0104;
    L_0x00ee:
        r0 = r12.drawInstantView;
        if (r0 == 0) goto L_0x00f5;
    L_0x00f2:
        r0 = r12.buttonY;
        goto L_0x0100;
    L_0x00f5:
        r0 = r12.namesOffset;
        r6 = r12.mediaOffsetY;
        r0 = r0 + r6;
        r6 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
    L_0x0100:
        r0 = r0 + r6;
        if (r3 > r0) goto L_0x0104;
    L_0x0103:
        goto L_0x00c7;
    L_0x0104:
        r0 = 0;
    L_0x0105:
        r3 = r13.getAction();
        if (r3 != 0) goto L_0x011b;
    L_0x010b:
        if (r0 != 0) goto L_0x010f;
    L_0x010d:
        if (r7 == 0) goto L_0x017a;
    L_0x010f:
        if (r0 == 0) goto L_0x0114;
    L_0x0111:
        r12.buttonPressed = r5;
        goto L_0x0116;
    L_0x0114:
        r12.miniButtonPressed = r5;
    L_0x0116:
        r12.invalidate();
        r4 = 1;
        goto L_0x017a;
    L_0x011b:
        r3 = r12.buttonPressed;
        if (r3 == 0) goto L_0x014b;
    L_0x011f:
        r3 = r13.getAction();
        if (r3 != r5) goto L_0x0131;
    L_0x0125:
        r12.buttonPressed = r2;
        r12.playSoundEffect(r2);
        r12.didPressButton(r5, r2);
        r12.invalidate();
        goto L_0x017a;
    L_0x0131:
        r3 = r13.getAction();
        if (r3 != r1) goto L_0x013d;
    L_0x0137:
        r12.buttonPressed = r2;
        r12.invalidate();
        goto L_0x017a;
    L_0x013d:
        r13 = r13.getAction();
        if (r13 != r8) goto L_0x017a;
    L_0x0143:
        if (r0 != 0) goto L_0x017a;
    L_0x0145:
        r12.buttonPressed = r2;
        r12.invalidate();
        goto L_0x017a;
    L_0x014b:
        r0 = r12.miniButtonPressed;
        if (r0 == 0) goto L_0x017a;
    L_0x014f:
        r0 = r13.getAction();
        if (r0 != r5) goto L_0x0161;
    L_0x0155:
        r12.miniButtonPressed = r2;
        r12.playSoundEffect(r2);
        r12.didPressMiniButton(r5);
        r12.invalidate();
        goto L_0x017a;
    L_0x0161:
        r0 = r13.getAction();
        if (r0 != r1) goto L_0x016d;
    L_0x0167:
        r12.miniButtonPressed = r2;
        r12.invalidate();
        goto L_0x017a;
    L_0x016d:
        r13 = r13.getAction();
        if (r13 != r8) goto L_0x017a;
    L_0x0173:
        if (r7 != 0) goto L_0x017a;
    L_0x0175:
        r12.miniButtonPressed = r2;
        r12.invalidate();
    L_0x017a:
        return r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkAudioMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkBotButtonMotionEvent(MotionEvent motionEvent) {
        if (this.botButtons.isEmpty() || this.currentMessageObject.eventId != 0) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            int measuredWidth;
            if (this.currentMessageObject.isOutOwner()) {
                measuredWidth = (getMeasuredWidth() - this.widthForButtons) - AndroidUtilities.dp(10.0f);
            } else {
                measuredWidth = this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
            }
            int i = 0;
            while (i < this.botButtons.size()) {
                BotButton botButton = (BotButton) this.botButtons.get(i);
                int access$1000 = (botButton.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
                if (x < botButton.x + measuredWidth || x > (botButton.x + measuredWidth) + botButton.width || y < access$1000 || y > access$1000 + botButton.height) {
                    i++;
                } else {
                    this.pressedBotButton = i;
                    invalidate();
                    return true;
                }
            }
            return false;
        } else if (motionEvent.getAction() != 1 || this.pressedBotButton == -1) {
            return false;
        } else {
            playSoundEffect(0);
            this.delegate.didPressBotButton(this, ((BotButton) this.botButtons.get(this.pressedBotButton)).button);
            this.pressedBotButton = -1;
            invalidate();
            return false;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:116:0x01d0  */
    /* JADX WARNING: Missing block: B:172:0x02b0, code skipped:
            if (r4 <= ((float) (r1 + org.telegram.messenger.AndroidUtilities.dp(32.0f)))) goto L_0x03fc;
     */
    /* JADX WARNING: Missing block: B:201:0x0313, code skipped:
            if (r4 <= ((float) (r1 + org.telegram.messenger.AndroidUtilities.dp(32.0f)))) goto L_0x03fc;
     */
    /* JADX WARNING: Missing block: B:235:0x03aa, code skipped:
            if (r4 <= ((float) (r1 + org.telegram.messenger.AndroidUtilities.dp(35.0f)))) goto L_0x03fc;
     */
    /* JADX WARNING: Missing block: B:256:0x03f5, code skipped:
            if (r4 <= ((float) (r1 + org.telegram.messenger.AndroidUtilities.dp(32.0f)))) goto L_0x03f9;
     */
    public boolean onTouchEvent(android.view.MotionEvent r14) {
        /*
        r13 = this;
        r0 = r13.currentMessageObject;
        if (r0 == 0) goto L_0x03fd;
    L_0x0004:
        r0 = r13.delegate;
        r0 = r0.canPerformActions();
        if (r0 != 0) goto L_0x000e;
    L_0x000c:
        goto L_0x03fd;
    L_0x000e:
        r0 = 0;
        r13.disallowLongPress = r0;
        r1 = r14.getX();
        r13.lastTouchX = r1;
        r1 = r14.getY();
        r13.lastTouchX = r1;
        r1 = r13.checkTextBlockMotionEvent(r14);
        if (r1 != 0) goto L_0x0027;
    L_0x0023:
        r1 = r13.checkOtherButtonMotionEvent(r14);
    L_0x0027:
        if (r1 != 0) goto L_0x002d;
    L_0x0029:
        r1 = r13.checkCaptionMotionEvent(r14);
    L_0x002d:
        if (r1 != 0) goto L_0x0033;
    L_0x002f:
        r1 = r13.checkAudioMotionEvent(r14);
    L_0x0033:
        if (r1 != 0) goto L_0x0039;
    L_0x0035:
        r1 = r13.checkLinkPreviewMotionEvent(r14);
    L_0x0039:
        if (r1 != 0) goto L_0x003f;
    L_0x003b:
        r1 = r13.checkInstantButtonMotionEvent(r14);
    L_0x003f:
        if (r1 != 0) goto L_0x0045;
    L_0x0041:
        r1 = r13.checkGameMotionEvent(r14);
    L_0x0045:
        if (r1 != 0) goto L_0x004b;
    L_0x0047:
        r1 = r13.checkPhotoImageMotionEvent(r14);
    L_0x004b:
        if (r1 != 0) goto L_0x0051;
    L_0x004d:
        r1 = r13.checkBotButtonMotionEvent(r14);
    L_0x0051:
        if (r1 != 0) goto L_0x0057;
    L_0x0053:
        r1 = r13.checkPollButtonMotionEvent(r14);
    L_0x0057:
        r2 = r14.getAction();
        r3 = 3;
        if (r2 != r3) goto L_0x0089;
    L_0x005e:
        r13.buttonPressed = r0;
        r13.miniButtonPressed = r0;
        r1 = -1;
        r13.pressedBotButton = r1;
        r13.pressedVoteButton = r1;
        r13.linkPreviewPressed = r0;
        r13.otherPressed = r0;
        r13.sharePressed = r0;
        r13.imagePressed = r0;
        r13.gamePreviewPressed = r0;
        r13.instantButtonPressed = r0;
        r13.instantPressed = r0;
        r2 = android.os.Build.VERSION.SDK_INT;
        r4 = 21;
        if (r2 < r4) goto L_0x0084;
    L_0x007b:
        r2 = r13.selectorDrawable;
        if (r2 == 0) goto L_0x0084;
    L_0x007f:
        r4 = android.util.StateSet.NOTHING;
        r2.setState(r4);
    L_0x0084:
        r13.resetPressedLink(r1);
        r6 = 0;
        goto L_0x008a;
    L_0x0089:
        r6 = r1;
    L_0x008a:
        r13.updateRadialProgressBackground();
        r1 = r13.disallowLongPress;
        if (r1 != 0) goto L_0x009c;
    L_0x0091:
        if (r6 == 0) goto L_0x009c;
    L_0x0093:
        r1 = r14.getAction();
        if (r1 != 0) goto L_0x009c;
    L_0x0099:
        r13.startCheckLongPress();
    L_0x009c:
        r1 = r14.getAction();
        r2 = 2;
        if (r1 == 0) goto L_0x00ac;
    L_0x00a3:
        r1 = r14.getAction();
        if (r1 == r2) goto L_0x00ac;
    L_0x00a9:
        r13.cancelCheckLongPress();
    L_0x00ac:
        if (r6 != 0) goto L_0x03fc;
    L_0x00ae:
        r1 = r14.getX();
        r4 = r14.getY();
        r5 = r14.getAction();
        r7 = NUM; // 0x420CLASSNAME float:35.0 double:5.47465589E-315;
        r8 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r10 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r11 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r12 = 1;
        if (r5 != 0) goto L_0x01d5;
    L_0x00c7:
        r2 = r13.delegate;
        if (r2 == 0) goto L_0x00d1;
    L_0x00cb:
        r2 = r2.canPerformActions();
        if (r2 == 0) goto L_0x03fc;
    L_0x00d1:
        r2 = r13.isAvatarVisible;
        if (r2 == 0) goto L_0x00e8;
    L_0x00d5:
        r2 = r13.avatarImage;
        r3 = r13.getTop();
        r3 = (float) r3;
        r3 = r3 + r4;
        r2 = r2.isInsideImage(r1, r3);
        if (r2 == 0) goto L_0x00e8;
    L_0x00e3:
        r13.avatarPressed = r12;
    L_0x00e5:
        r6 = 1;
        goto L_0x01ce;
    L_0x00e8:
        r2 = r13.drawForwardedName;
        if (r2 == 0) goto L_0x012b;
    L_0x00ec:
        r2 = r13.forwardedNameLayout;
        r0 = r2[r0];
        if (r0 == 0) goto L_0x012b;
    L_0x00f2:
        r0 = r13.forwardNameX;
        r2 = (float) r0;
        r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x012b;
    L_0x00f9:
        r2 = r13.forwardedNameWidth;
        r0 = r0 + r2;
        r0 = (float) r0;
        r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x012b;
    L_0x0101:
        r0 = r13.forwardNameY;
        r2 = (float) r0;
        r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x012b;
    L_0x0108:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r0 = r0 + r2;
        r0 = (float) r0;
        r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x012b;
    L_0x0112:
        r0 = r13.viaWidth;
        if (r0 == 0) goto L_0x0128;
    L_0x0116:
        r0 = r13.forwardNameX;
        r2 = r13.viaNameWidth;
        r0 = r0 + r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r0 = r0 + r2;
        r0 = (float) r0;
        r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
        if (r0 < 0) goto L_0x0128;
    L_0x0125:
        r13.forwardBotPressed = r12;
        goto L_0x00e5;
    L_0x0128:
        r13.forwardNamePressed = r12;
        goto L_0x00e5;
    L_0x012b:
        r0 = r13.drawNameLayout;
        if (r0 == 0) goto L_0x0164;
    L_0x012f:
        r0 = r13.nameLayout;
        if (r0 == 0) goto L_0x0164;
    L_0x0133:
        r0 = r13.viaWidth;
        if (r0 == 0) goto L_0x0164;
    L_0x0137:
        r2 = r13.nameX;
        r3 = r13.viaNameWidth;
        r5 = (float) r3;
        r5 = r5 + r2;
        r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
        if (r5 < 0) goto L_0x0164;
    L_0x0141:
        r3 = (float) r3;
        r2 = r2 + r3;
        r0 = (float) r0;
        r2 = r2 + r0;
        r0 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r0 > 0) goto L_0x0164;
    L_0x0149:
        r0 = r13.nameY;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r2 = (float) r2;
        r0 = r0 - r2;
        r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r0 < 0) goto L_0x0164;
    L_0x0155:
        r0 = r13.nameY;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r2 = (float) r2;
        r0 = r0 + r2;
        r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x0164;
    L_0x0161:
        r13.forwardBotPressed = r12;
        goto L_0x00e5;
    L_0x0164:
        r0 = r13.drawShareButton;
        if (r0 == 0) goto L_0x0191;
    L_0x0168:
        r0 = r13.shareStartX;
        r2 = (float) r0;
        r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x0191;
    L_0x016f:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r0 + r2;
        r0 = (float) r0;
        r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x0191;
    L_0x0179:
        r0 = r13.shareStartY;
        r2 = (float) r0;
        r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x0191;
    L_0x0180:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r0 = r0 + r2;
        r0 = (float) r0;
        r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x0191;
    L_0x018a:
        r13.sharePressed = r12;
        r13.invalidate();
        goto L_0x00e5;
    L_0x0191:
        r0 = r13.replyNameLayout;
        if (r0 == 0) goto L_0x01ce;
    L_0x0195:
        r0 = r13.currentMessageObject;
        r0 = r0.shouldDrawWithoutBackground();
        if (r0 == 0) goto L_0x01a8;
    L_0x019d:
        r0 = r13.replyStartX;
        r2 = r13.replyNameWidth;
        r3 = r13.replyTextWidth;
        r2 = java.lang.Math.max(r2, r3);
        goto L_0x01ac;
    L_0x01a8:
        r0 = r13.replyStartX;
        r2 = r13.backgroundDrawableRight;
    L_0x01ac:
        r0 = r0 + r2;
        r2 = r13.replyStartX;
        r2 = (float) r2;
        r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x01ce;
    L_0x01b4:
        r0 = (float) r0;
        r0 = (r1 > r0 ? 1 : (r1 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x01ce;
    L_0x01b9:
        r0 = r13.replyStartY;
        r1 = (float) r0;
        r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r1 < 0) goto L_0x01ce;
    L_0x01c0:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r0 = r0 + r1;
        r0 = (float) r0;
        r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r0 > 0) goto L_0x01ce;
    L_0x01ca:
        r13.replyPressed = r12;
        goto L_0x00e5;
    L_0x01ce:
        if (r6 == 0) goto L_0x03fc;
    L_0x01d0:
        r13.startCheckLongPress();
        goto L_0x03fc;
    L_0x01d5:
        r5 = r14.getAction();
        if (r5 == r2) goto L_0x01de;
    L_0x01db:
        r13.cancelCheckLongPress();
    L_0x01de:
        r5 = r13.avatarPressed;
        if (r5 == 0) goto L_0x0243;
    L_0x01e2:
        r5 = r14.getAction();
        if (r5 != r12) goto L_0x021d;
    L_0x01e8:
        r13.avatarPressed = r0;
        r13.playSoundEffect(r0);
        r0 = r13.delegate;
        if (r0 == 0) goto L_0x03fc;
    L_0x01f1:
        r1 = r13.currentUser;
        if (r1 == 0) goto L_0x0207;
    L_0x01f5:
        r2 = r1.id;
        if (r2 != 0) goto L_0x01fe;
    L_0x01f9:
        r0.didPressHiddenForward(r13);
        goto L_0x03fc;
    L_0x01fe:
        r2 = r13.lastTouchX;
        r3 = r13.lastTouchY;
        r0.didPressUserAvatar(r13, r1, r2, r3);
        goto L_0x03fc;
    L_0x0207:
        r2 = r13.currentChat;
        if (r2 == 0) goto L_0x03fc;
    L_0x020b:
        r1 = r13.currentMessageObject;
        r1 = r1.messageOwner;
        r1 = r1.fwd_from;
        r3 = r1.channel_post;
        r4 = r13.lastTouchX;
        r5 = r13.lastTouchY;
        r1 = r13;
        r0.didPressChannelAvatar(r1, r2, r3, r4, r5);
        goto L_0x03fc;
    L_0x021d:
        r5 = r14.getAction();
        if (r5 != r3) goto L_0x0227;
    L_0x0223:
        r13.avatarPressed = r0;
        goto L_0x03fc;
    L_0x0227:
        r3 = r14.getAction();
        if (r3 != r2) goto L_0x03fc;
    L_0x022d:
        r2 = r13.isAvatarVisible;
        if (r2 == 0) goto L_0x03fc;
    L_0x0231:
        r2 = r13.avatarImage;
        r3 = r13.getTop();
        r3 = (float) r3;
        r4 = r4 + r3;
        r1 = r2.isInsideImage(r1, r4);
        if (r1 != 0) goto L_0x03fc;
    L_0x023f:
        r13.avatarPressed = r0;
        goto L_0x03fc;
    L_0x0243:
        r5 = r13.forwardNamePressed;
        if (r5 == 0) goto L_0x02b6;
    L_0x0247:
        r5 = r14.getAction();
        if (r5 != r12) goto L_0x0282;
    L_0x024d:
        r13.forwardNamePressed = r0;
        r13.playSoundEffect(r0);
        r0 = r13.delegate;
        if (r0 == 0) goto L_0x03fc;
    L_0x0256:
        r2 = r13.currentForwardChannel;
        if (r2 == 0) goto L_0x026c;
    L_0x025a:
        r1 = r13.currentMessageObject;
        r1 = r1.messageOwner;
        r1 = r1.fwd_from;
        r3 = r1.channel_post;
        r4 = r13.lastTouchX;
        r5 = r13.lastTouchY;
        r1 = r13;
        r0.didPressChannelAvatar(r1, r2, r3, r4, r5);
        goto L_0x03fc;
    L_0x026c:
        r1 = r13.currentForwardUser;
        if (r1 == 0) goto L_0x0279;
    L_0x0270:
        r2 = r13.lastTouchX;
        r3 = r13.lastTouchY;
        r0.didPressUserAvatar(r13, r1, r2, r3);
        goto L_0x03fc;
    L_0x0279:
        r1 = r13.currentForwardName;
        if (r1 == 0) goto L_0x03fc;
    L_0x027d:
        r0.didPressHiddenForward(r13);
        goto L_0x03fc;
    L_0x0282:
        r5 = r14.getAction();
        if (r5 != r3) goto L_0x028c;
    L_0x0288:
        r13.forwardNamePressed = r0;
        goto L_0x03fc;
    L_0x028c:
        r3 = r14.getAction();
        if (r3 != r2) goto L_0x03fc;
    L_0x0292:
        r2 = r13.forwardNameX;
        r3 = (float) r2;
        r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r3 < 0) goto L_0x02b2;
    L_0x0299:
        r3 = r13.forwardedNameWidth;
        r2 = r2 + r3;
        r2 = (float) r2;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 > 0) goto L_0x02b2;
    L_0x02a1:
        r1 = r13.forwardNameY;
        r2 = (float) r1;
        r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x02b2;
    L_0x02a8:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1 = r1 + r2;
        r1 = (float) r1;
        r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r1 <= 0) goto L_0x03fc;
    L_0x02b2:
        r13.forwardNamePressed = r0;
        goto L_0x03fc;
    L_0x02b6:
        r5 = r13.forwardBotPressed;
        if (r5 == 0) goto L_0x0349;
    L_0x02ba:
        r5 = r14.getAction();
        if (r5 != r12) goto L_0x02db;
    L_0x02c0:
        r13.forwardBotPressed = r0;
        r13.playSoundEffect(r0);
        r0 = r13.delegate;
        if (r0 == 0) goto L_0x03fc;
    L_0x02c9:
        r1 = r13.currentViaBotUser;
        if (r1 == 0) goto L_0x02d0;
    L_0x02cd:
        r1 = r1.username;
        goto L_0x02d6;
    L_0x02d0:
        r1 = r13.currentMessageObject;
        r1 = r1.messageOwner;
        r1 = r1.via_bot_name;
    L_0x02d6:
        r0.didPressViaBot(r13, r1);
        goto L_0x03fc;
    L_0x02db:
        r5 = r14.getAction();
        if (r5 != r3) goto L_0x02e5;
    L_0x02e1:
        r13.forwardBotPressed = r0;
        goto L_0x03fc;
    L_0x02e5:
        r3 = r14.getAction();
        if (r3 != r2) goto L_0x03fc;
    L_0x02eb:
        r2 = r13.drawForwardedName;
        if (r2 == 0) goto L_0x0319;
    L_0x02ef:
        r2 = r13.forwardedNameLayout;
        r2 = r2[r0];
        if (r2 == 0) goto L_0x0319;
    L_0x02f5:
        r2 = r13.forwardNameX;
        r3 = (float) r2;
        r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r3 < 0) goto L_0x0315;
    L_0x02fc:
        r3 = r13.forwardedNameWidth;
        r2 = r2 + r3;
        r2 = (float) r2;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 > 0) goto L_0x0315;
    L_0x0304:
        r1 = r13.forwardNameY;
        r2 = (float) r1;
        r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x0315;
    L_0x030b:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1 = r1 + r2;
        r1 = (float) r1;
        r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r1 <= 0) goto L_0x03fc;
    L_0x0315:
        r13.forwardBotPressed = r0;
        goto L_0x03fc;
    L_0x0319:
        r2 = r13.nameX;
        r3 = r13.viaNameWidth;
        r5 = (float) r3;
        r5 = r5 + r2;
        r5 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1));
        if (r5 < 0) goto L_0x0345;
    L_0x0323:
        r3 = (float) r3;
        r2 = r2 + r3;
        r3 = r13.viaWidth;
        r3 = (float) r3;
        r2 = r2 + r3;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 > 0) goto L_0x0345;
    L_0x032d:
        r1 = r13.nameY;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r2 = (float) r2;
        r1 = r1 - r2;
        r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r1 < 0) goto L_0x0345;
    L_0x0339:
        r1 = r13.nameY;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r2 = (float) r2;
        r1 = r1 + r2;
        r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r1 <= 0) goto L_0x03fc;
    L_0x0345:
        r13.forwardBotPressed = r0;
        goto L_0x03fc;
    L_0x0349:
        r5 = r13.replyPressed;
        if (r5 == 0) goto L_0x03af;
    L_0x034d:
        r5 = r14.getAction();
        if (r5 != r12) goto L_0x0367;
    L_0x0353:
        r13.replyPressed = r0;
        r13.playSoundEffect(r0);
        r0 = r13.delegate;
        if (r0 == 0) goto L_0x03fc;
    L_0x035c:
        r1 = r13.currentMessageObject;
        r1 = r1.messageOwner;
        r1 = r1.reply_to_msg_id;
        r0.didPressReplyMessage(r13, r1);
        goto L_0x03fc;
    L_0x0367:
        r5 = r14.getAction();
        if (r5 != r3) goto L_0x0371;
    L_0x036d:
        r13.replyPressed = r0;
        goto L_0x03fc;
    L_0x0371:
        r3 = r14.getAction();
        if (r3 != r2) goto L_0x03fc;
    L_0x0377:
        r2 = r13.currentMessageObject;
        r2 = r2.shouldDrawWithoutBackground();
        if (r2 == 0) goto L_0x038a;
    L_0x037f:
        r2 = r13.replyStartX;
        r3 = r13.replyNameWidth;
        r5 = r13.replyTextWidth;
        r3 = java.lang.Math.max(r3, r5);
        goto L_0x038e;
    L_0x038a:
        r2 = r13.replyStartX;
        r3 = r13.backgroundDrawableRight;
    L_0x038e:
        r2 = r2 + r3;
        r3 = r13.replyStartX;
        r3 = (float) r3;
        r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r3 < 0) goto L_0x03ac;
    L_0x0396:
        r2 = (float) r2;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 > 0) goto L_0x03ac;
    L_0x039b:
        r1 = r13.replyStartY;
        r2 = (float) r1;
        r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x03ac;
    L_0x03a2:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r1 = r1 + r2;
        r1 = (float) r1;
        r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r1 <= 0) goto L_0x03fc;
    L_0x03ac:
        r13.replyPressed = r0;
        goto L_0x03fc;
    L_0x03af:
        r5 = r13.sharePressed;
        if (r5 == 0) goto L_0x03fc;
    L_0x03b3:
        r5 = r14.getAction();
        if (r5 != r12) goto L_0x03c6;
    L_0x03b9:
        r13.sharePressed = r0;
        r13.playSoundEffect(r0);
        r0 = r13.delegate;
        if (r0 == 0) goto L_0x03f9;
    L_0x03c2:
        r0.didPressShare(r13);
        goto L_0x03f9;
    L_0x03c6:
        r5 = r14.getAction();
        if (r5 != r3) goto L_0x03cf;
    L_0x03cc:
        r13.sharePressed = r0;
        goto L_0x03f9;
    L_0x03cf:
        r3 = r14.getAction();
        if (r3 != r2) goto L_0x03f9;
    L_0x03d5:
        r2 = r13.shareStartX;
        r3 = (float) r2;
        r3 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1));
        if (r3 < 0) goto L_0x03f7;
    L_0x03dc:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r2 = r2 + r3;
        r2 = (float) r2;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 > 0) goto L_0x03f7;
    L_0x03e6:
        r1 = r13.shareStartY;
        r2 = (float) r1;
        r2 = (r4 > r2 ? 1 : (r4 == r2 ? 0 : -1));
        if (r2 < 0) goto L_0x03f7;
    L_0x03ed:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r1 = r1 + r2;
        r1 = (float) r1;
        r1 = (r4 > r1 ? 1 : (r4 == r1 ? 0 : -1));
        if (r1 <= 0) goto L_0x03f9;
    L_0x03f7:
        r13.sharePressed = r0;
    L_0x03f9:
        r13.invalidate();
    L_0x03fc:
        return r6;
    L_0x03fd:
        r0 = super.onTouchEvent(r14);
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void updatePlayingMessageProgress() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            int i;
            int i2;
            int i3;
            if (this.documentAttachType != 4) {
                String str = "%02d:%02d";
                String format;
                if (messageObject.isRoundVideo()) {
                    Document document = this.currentMessageObject.getDocument();
                    for (i = 0; i < document.attributes.size(); i++) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                        if (documentAttribute instanceof TL_documentAttributeVideo) {
                            i2 = documentAttribute.duration;
                            break;
                        }
                    }
                    i2 = 0;
                    if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                        i2 = Math.max(0, i2 - this.currentMessageObject.audioProgressSec);
                    }
                    if (this.lastTime != i2) {
                        this.lastTime = i2;
                        format = String.format(str, new Object[]{Integer.valueOf(i2 / 60), Integer.valueOf(i2 % 60)});
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
                            i2 = this.currentMessageObject.audioProgressSec;
                        } else {
                            for (i2 = 0; i2 < this.documentAttach.attributes.size(); i2++) {
                                DocumentAttribute documentAttribute2 = (DocumentAttribute) this.documentAttach.attributes.get(i2);
                                if (documentAttribute2 instanceof TL_documentAttributeAudio) {
                                    i2 = documentAttribute2.duration;
                                    break;
                                }
                            }
                            i2 = 0;
                        }
                        if (this.lastTime != i2) {
                            this.lastTime = i2;
                            format = String.format(str, new Object[]{Integer.valueOf(i2 / 60), Integer.valueOf(i2 % 60)});
                            this.timeWidthAudio = (int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(format));
                            this.durationLayout = new StaticLayout(format, Theme.chat_audioTimePaint, this.timeWidthAudio, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        }
                    } else {
                        i2 = this.currentMessageObject.getDuration();
                        i3 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject) ? this.currentMessageObject.audioProgressSec : 0;
                        if (this.lastTime != i3) {
                            String format2;
                            this.lastTime = i3;
                            if (i2 == 0) {
                                format2 = String.format("%d:%02d / -:--", new Object[]{Integer.valueOf(i3 / 60), Integer.valueOf(i3 % 60)});
                            } else {
                                format2 = String.format("%d:%02d / %d:%02d", new Object[]{Integer.valueOf(i3 / 60), Integer.valueOf(i3 % 60), Integer.valueOf(i2 / 60), Integer.valueOf(i2 % 60)});
                            }
                            String str2 = format2;
                            this.durationLayout = new StaticLayout(str2, Theme.chat_audioTimePaint, (int) Math.ceil((double) Theme.chat_audioTimePaint.measureText(str2)), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        }
                    }
                    invalidate();
                }
            } else if (!PhotoViewer.isPlayingMessage(messageObject) && !MediaController.getInstance().isGoingToShowMessageObject(this.currentMessageObject)) {
                AnimatedFileDrawable animation = this.photoImage.getAnimation();
                if (animation != null) {
                    i = animation.getDurationMs() / 1000;
                    this.currentMessageObject.audioPlayerDuration = i;
                    MessageObject messageObject2 = this.currentMessageObject;
                    Message message = messageObject2.messageOwner;
                    if (message.ttl > 0 && message.destroyTime == 0 && !messageObject2.needDrawBluredPreview() && this.currentMessageObject.isVideo() && animation.hasBitmap()) {
                        this.delegate.didStartVideoStream(this.currentMessageObject);
                    }
                } else {
                    i = 0;
                }
                if (i == 0) {
                    i = this.currentMessageObject.getDuration();
                }
                if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    float f = (float) i;
                    i = (int) (f - (this.currentMessageObject.audioProgress * f));
                } else if (animation != null) {
                    if (i != 0) {
                        i -= animation.getCurrentProgressMs() / 1000;
                    }
                    if (this.delegate != null && animation.getCurrentProgressMs() >= 3000) {
                        this.delegate.videoTimerReached();
                    }
                }
                i3 = i - ((i / 60) * 60);
                if (this.lastTime != i) {
                    String format3 = String.format("%d:%02d", new Object[]{Integer.valueOf(i2), Integer.valueOf(i3)});
                    this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(format3));
                    this.infoLayout = new StaticLayout(format3, Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.lastTime = i;
                }
            }
        }
    }

    public void setFullyDraw(boolean z) {
        this.fullyDraw = z;
    }

    public void setVisiblePart(int i, int i2) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.textLayoutBlocks != null) {
            i -= this.textY;
            int i3 = 0;
            int i4 = 0;
            while (i3 < this.currentMessageObject.textLayoutBlocks.size() && ((TextLayoutBlock) this.currentMessageObject.textLayoutBlocks.get(i3)).textYOffset <= ((float) i)) {
                i4 = i3;
                i3++;
            }
            int i5 = -1;
            int i6 = -1;
            int i7 = 0;
            for (i4 = 
/*
Method generation error in method: org.telegram.ui.Cells.ChatMessageCell.setVisiblePart(int, int):void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r2_1 'i4' int) = (r2_0 'i4' int), (r2_3 'i4' int) binds: {(r2_0 'i4' int)=B:4:0x000a, (r2_3 'i4' int)=B:9:0x002c} in method: org.telegram.ui.Cells.ChatMessageCell.setVisiblePart(int, int):void, dex: classes.dex
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:228)
	at jadx.core.codegen.RegionGen.makeLoop(RegionGen.java:185)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:63)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:95)
	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:120)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:59)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:89)
	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:183)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:321)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:259)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:221)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:111)
	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:77)
	at jadx.core.codegen.CodeGen.visit(CodeGen.java:10)
	at jadx.core.ProcessClass.process(ProcessClass.java:38)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
Caused by: jadx.core.utils.exceptions.CodegenException: PHI can be used only in fallback mode
	at jadx.core.codegen.InsnGen.fallbackOnlyInsn(InsnGen.java:539)
	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:511)
	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:222)
	... 22 more

*/

    public static StaticLayout generateStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, int i2, int i3, int i4) {
        CharSequence charSequence2 = charSequence;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        StaticLayout staticLayout = new StaticLayout(charSequence, textPaint, i2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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
                lineEnd = (lineEnd - 1) + i8;
                String str = "\n";
                if (spannableStringBuilder.charAt(lineEnd) == ' ') {
                    spannableStringBuilder.replace(lineEnd, lineEnd + 1, str);
                } else if (spannableStringBuilder.charAt(lineEnd) != 10) {
                    spannableStringBuilder.insert(lineEnd, str);
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
        int i9 = i5;
        return StaticLayoutEx.createStaticLayout(spannableStringBuilder, textPaint, i9, Alignment.ALIGN_NORMAL, 1.0f, (float) AndroidUtilities.dp(1.0f), false, TruncateAt.END, i9, i4, true);
    }

    private void didClickedImage() {
        MessageObject messageObject = this.currentMessageObject;
        int i;
        if (messageObject.type == 1 || messageObject.isAnyKindOfSticker()) {
            i = this.buttonState;
            if (i == -1) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                return;
            } else if (i == 0) {
                didPressButton(true, false);
                return;
            } else {
                return;
            }
        }
        messageObject = this.currentMessageObject;
        int i2 = messageObject.type;
        if (i2 == 12) {
            this.delegate.didPressUserAvatar(this, MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)), this.lastTouchX, this.lastTouchY);
        } else if (i2 == 5) {
            if (this.buttonState != -1) {
                didPressButton(true, false);
            } else if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isMessagePaused()) {
                this.delegate.needPlayMessage(this.currentMessageObject);
            } else {
                MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject);
            }
        } else if (i2 == 8) {
            i = this.buttonState;
            if (i == -1 || (i == 1 && this.canStreamVideo && this.autoPlayingMedia)) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                return;
            }
            i = this.buttonState;
            if (i == 2 || i == 0) {
                didPressButton(true, false);
            }
        } else {
            int i3 = this.documentAttachType;
            if (i3 == 4) {
                if (this.buttonState == -1 || (this.drawVideoImageButton && (this.autoPlayingMedia || (SharedConfig.streamMedia && this.canStreamVideo)))) {
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                } else if (this.drawVideoImageButton) {
                    didPressButton(true, true);
                } else {
                    i = this.buttonState;
                    if (i == 0 || i == 3) {
                        didPressButton(true, false);
                    }
                }
            } else if (i2 == 4) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            } else if (i3 == 1) {
                if (this.buttonState == -1) {
                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                }
            } else if (i3 == 2) {
                if (this.buttonState == -1) {
                    WebPage webPage = messageObject.messageOwner.media.webpage;
                    if (webPage != null) {
                        String str = webPage.embed_url;
                        if (str == null || str.length() == 0) {
                            Browser.openUrl(getContext(), webPage.url);
                        } else {
                            this.delegate.needOpenWebView(webPage.embed_url, webPage.site_name, webPage.description, webPage.url, webPage.embed_width, webPage.embed_height);
                        }
                    }
                }
            } else if (this.hasInvoicePreview && this.buttonState == -1) {
                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
            }
        }
    }

    private void updateSecretTimeText(MessageObject messageObject) {
        if (messageObject != null && messageObject.needDrawBluredPreview()) {
            String secretTimeString = messageObject.getSecretTimeString();
            if (secretTimeString != null) {
                this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(secretTimeString));
                this.infoLayout = new StaticLayout(TextUtils.ellipsize(secretTimeString, Theme.chat_infoPaint, (float) this.infoWidth, TruncateAt.END), Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                invalidate();
            }
        }
    }

    private boolean isPhotoDataChanged(MessageObject messageObject) {
        MessageObject messageObject2 = messageObject;
        int i = messageObject2.type;
        boolean z = false;
        if (!(i == 0 || i == 14)) {
            if (i != 4) {
                PhotoSize photoSize = this.currentPhotoObject;
                if (photoSize == null || (photoSize.location instanceof TL_fileLocationUnavailable)) {
                    i = messageObject2.type;
                    if (i == 1 || i == 5 || i == 3 || i == 8 || messageObject.isAnyKindOfSticker()) {
                        z = true;
                    }
                } else {
                    messageObject2 = this.currentMessageObject;
                    if (messageObject2 == null || !this.photoNotSet) {
                        return false;
                    }
                    return FileLoader.getPathToMessage(messageObject2.messageOwner).exists();
                }
            } else if (this.currentUrl == null) {
                return true;
            } else {
                String formapMapUrl;
                MessageMedia messageMedia = messageObject2.messageOwner.media;
                GeoPoint geoPoint = messageMedia.geo;
                double d = geoPoint.lat;
                double d2 = geoPoint._long;
                int dp;
                float f;
                float f2;
                int i2;
                if (messageMedia instanceof TL_messageMediaGeoLive) {
                    dp = this.backgroundWidth - AndroidUtilities.dp(21.0f);
                    i = AndroidUtilities.dp(195.0f);
                    double d3 = (double) NUM;
                    Double.isNaN(d3);
                    double d4 = d3 / 3.141592653589793d;
                    d = (d * 3.141592653589793d) / 180.0d;
                    double log = (Math.log((Math.sin(d) + 1.0d) / (1.0d - Math.sin(d))) * d4) / 2.0d;
                    Double.isNaN(d3);
                    log = (double) (Math.round(d3 - log) - ((long) (AndroidUtilities.dp(10.3f) << 6)));
                    Double.isNaN(log);
                    Double.isNaN(d3);
                    double atan = ((1.5707963267948966d - (Math.atan(Math.exp((log - d3) / d4)) * 2.0d)) * 180.0d) / 3.141592653589793d;
                    int i3 = this.currentAccount;
                    f = (float) dp;
                    f2 = AndroidUtilities.density;
                    formapMapUrl = AndroidUtilities.formapMapUrl(i3, atan, d2, (int) (f / f2), (int) (((float) i) / f2), false, 15);
                } else if (TextUtils.isEmpty(messageMedia.title)) {
                    dp = this.backgroundWidth - AndroidUtilities.dp(12.0f);
                    i = AndroidUtilities.dp(195.0f);
                    i2 = this.currentAccount;
                    f = (float) dp;
                    f2 = AndroidUtilities.density;
                    formapMapUrl = AndroidUtilities.formapMapUrl(i2, d, d2, (int) (f / f2), (int) (((float) i) / f2), true, 15);
                } else {
                    dp = this.backgroundWidth - AndroidUtilities.dp(21.0f);
                    i = AndroidUtilities.dp(195.0f);
                    i2 = this.currentAccount;
                    f = (float) dp;
                    f2 = AndroidUtilities.density;
                    formapMapUrl = AndroidUtilities.formapMapUrl(i2, d, d2, (int) (f / f2), (int) (((float) i) / f2), true, 15);
                }
                return formapMapUrl.equals(this.currentUrl) ^ 1;
            }
        }
        return z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:58:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00c7  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0061  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00ab A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x00c2  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x00c7  */
    /* JADX WARNING: Missing block: B:61:0x00a3, code skipped:
            if (r8.currentMessageObject.replyMessageObject.isAnyKindOfSticker() == false) goto L_0x00a7;
     */
    private boolean isUserDataChanged() {
        /*
        r8 = this;
        r0 = r8.currentMessageObject;
        r1 = 1;
        if (r0 == 0) goto L_0x0016;
    L_0x0005:
        r2 = r8.hasLinkPreview;
        if (r2 != 0) goto L_0x0016;
    L_0x0009:
        r0 = r0.messageOwner;
        r0 = r0.media;
        if (r0 == 0) goto L_0x0016;
    L_0x000f:
        r0 = r0.webpage;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r0 == 0) goto L_0x0016;
    L_0x0015:
        return r1;
    L_0x0016:
        r0 = r8.currentMessageObject;
        r2 = 0;
        if (r0 == 0) goto L_0x0113;
    L_0x001b:
        r0 = r8.currentUser;
        if (r0 != 0) goto L_0x0025;
    L_0x001f:
        r0 = r8.currentChat;
        if (r0 != 0) goto L_0x0025;
    L_0x0023:
        goto L_0x0113;
    L_0x0025:
        r0 = r8.lastSendState;
        r3 = r8.currentMessageObject;
        r3 = r3.messageOwner;
        r4 = r3.send_state;
        if (r0 == r4) goto L_0x0030;
    L_0x002f:
        return r1;
    L_0x0030:
        r0 = r8.lastDeleteDate;
        r4 = r3.destroyTime;
        if (r0 == r4) goto L_0x0037;
    L_0x0036:
        return r1;
    L_0x0037:
        r0 = r8.lastViewsCount;
        r3 = r3.views;
        if (r0 == r3) goto L_0x003e;
    L_0x003d:
        return r1;
    L_0x003e:
        r8.updateCurrentUserAndChat();
        r0 = r8.isAvatarVisible;
        r3 = 0;
        if (r0 == 0) goto L_0x005c;
    L_0x0046:
        r0 = r8.currentUser;
        if (r0 == 0) goto L_0x0051;
    L_0x004a:
        r0 = r0.photo;
        if (r0 == 0) goto L_0x0051;
    L_0x004e:
        r0 = r0.photo_small;
        goto L_0x005d;
    L_0x0051:
        r0 = r8.currentChat;
        if (r0 == 0) goto L_0x005c;
    L_0x0055:
        r0 = r0.photo;
        if (r0 == 0) goto L_0x005c;
    L_0x0059:
        r0 = r0.photo_small;
        goto L_0x005d;
    L_0x005c:
        r0 = r3;
    L_0x005d:
        r4 = r8.replyTextLayout;
        if (r4 != 0) goto L_0x0068;
    L_0x0061:
        r4 = r8.currentMessageObject;
        r4 = r4.replyMessageObject;
        if (r4 == 0) goto L_0x0068;
    L_0x0067:
        return r1;
    L_0x0068:
        r4 = r8.currentPhoto;
        if (r4 != 0) goto L_0x006e;
    L_0x006c:
        if (r0 != 0) goto L_0x0088;
    L_0x006e:
        r4 = r8.currentPhoto;
        if (r4 == 0) goto L_0x0074;
    L_0x0072:
        if (r0 == 0) goto L_0x0088;
    L_0x0074:
        r4 = r8.currentPhoto;
        if (r4 == 0) goto L_0x0089;
    L_0x0078:
        if (r0 == 0) goto L_0x0089;
    L_0x007a:
        r5 = r4.local_id;
        r6 = r0.local_id;
        if (r5 != r6) goto L_0x0088;
    L_0x0080:
        r4 = r4.volume_id;
        r6 = r0.volume_id;
        r0 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r0 == 0) goto L_0x0089;
    L_0x0088:
        return r1;
    L_0x0089:
        r0 = r8.replyNameLayout;
        if (r0 == 0) goto L_0x00a6;
    L_0x008d:
        r0 = r8.currentMessageObject;
        r0 = r0.replyMessageObject;
        r0 = r0.photoThumbs;
        r4 = 40;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r4);
        if (r0 == 0) goto L_0x00a6;
    L_0x009b:
        r4 = r8.currentMessageObject;
        r4 = r4.replyMessageObject;
        r4 = r4.isAnyKindOfSticker();
        if (r4 != 0) goto L_0x00a6;
    L_0x00a5:
        goto L_0x00a7;
    L_0x00a6:
        r0 = r3;
    L_0x00a7:
        r4 = r8.currentReplyPhoto;
        if (r4 != 0) goto L_0x00ae;
    L_0x00ab:
        if (r0 == 0) goto L_0x00ae;
    L_0x00ad:
        return r1;
    L_0x00ae:
        r0 = r8.drawName;
        if (r0 == 0) goto L_0x00cd;
    L_0x00b2:
        r0 = r8.isChat;
        if (r0 == 0) goto L_0x00cd;
    L_0x00b6:
        r0 = r8.currentMessageObject;
        r0 = r0.isOutOwner();
        if (r0 != 0) goto L_0x00cd;
    L_0x00be:
        r0 = r8.currentUser;
        if (r0 == 0) goto L_0x00c7;
    L_0x00c2:
        r3 = org.telegram.messenger.UserObject.getUserName(r0);
        goto L_0x00cd;
    L_0x00c7:
        r0 = r8.currentChat;
        if (r0 == 0) goto L_0x00cd;
    L_0x00cb:
        r3 = r0.title;
    L_0x00cd:
        r0 = r8.currentNameString;
        if (r0 != 0) goto L_0x00d3;
    L_0x00d1:
        if (r3 != 0) goto L_0x00e5;
    L_0x00d3:
        r0 = r8.currentNameString;
        if (r0 == 0) goto L_0x00d9;
    L_0x00d7:
        if (r3 == 0) goto L_0x00e5;
    L_0x00d9:
        r0 = r8.currentNameString;
        if (r0 == 0) goto L_0x00e6;
    L_0x00dd:
        if (r3 == 0) goto L_0x00e6;
    L_0x00df:
        r0 = r0.equals(r3);
        if (r0 != 0) goto L_0x00e6;
    L_0x00e5:
        return r1;
    L_0x00e6:
        r0 = r8.drawForwardedName;
        if (r0 == 0) goto L_0x0113;
    L_0x00ea:
        r0 = r8.currentMessageObject;
        r0 = r0.needDrawForwarded();
        if (r0 == 0) goto L_0x0113;
    L_0x00f2:
        r0 = r8.currentMessageObject;
        r0 = r0.getForwardedName();
        r3 = r8.currentForwardNameString;
        if (r3 != 0) goto L_0x00fe;
    L_0x00fc:
        if (r0 != 0) goto L_0x0112;
    L_0x00fe:
        r3 = r8.currentForwardNameString;
        if (r3 == 0) goto L_0x0104;
    L_0x0102:
        if (r0 == 0) goto L_0x0112;
    L_0x0104:
        r3 = r8.currentForwardNameString;
        if (r3 == 0) goto L_0x0111;
    L_0x0108:
        if (r0 == 0) goto L_0x0111;
    L_0x010a:
        r0 = r3.equals(r0);
        if (r0 != 0) goto L_0x0111;
    L_0x0110:
        goto L_0x0112;
    L_0x0111:
        r1 = 0;
    L_0x0112:
        return r1;
    L_0x0113:
        return r2;
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
        User user = this.currentUser;
        if (user == null || user.id != 0) {
            return this.forwardNameX + this.forwardNameCenterX;
        }
        return (int) this.avatarImage.getCenterX();
    }

    /* Access modifiers changed, original: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.onDetachedFromWindow();
        }
        checkBoxBase = this.photoCheckBox;
        if (checkBoxBase != null) {
            checkBoxBase.onDetachedFromWindow();
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
    }

    /* Access modifiers changed, original: protected */
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
        checkBoxBase = this.photoCheckBox;
        if (checkBoxBase != null) {
            checkBoxBase.onAttachedToWindow();
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
        if (this.documentAttachType == 4 && this.autoPlayingMedia) {
            this.animatingNoSoundPlaying = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            if (!this.animatingNoSoundPlaying) {
                f = 1.0f;
            }
            this.animatingNoSoundProgress = f;
            this.animatingNoSound = 0;
            return;
        }
        this.animatingNoSoundPlaying = false;
        this.animatingNoSoundProgress = 0.0f;
        int i = this.documentAttachType;
        if ((i == 4 || i == 2) && this.drawVideoSize) {
            f = 1.0f;
        }
        this.animatingDrawVideoImageButtonProgress = f;
    }

    /* JADX WARNING: Removed duplicated region for block: B:382:0x078f  */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x0783  */
    /* JADX WARNING: Removed duplicated region for block: B:385:0x0794  */
    /* JADX WARNING: Removed duplicated region for block: B:607:0x0bf8 A:{Catch:{ Exception -> 0x0CLASSNAME }} */
    /* JADX WARNING: Removed duplicated region for block: B:1005:0x1600  */
    /* JADX WARNING: Removed duplicated region for block: B:1004:0x15fe  */
    /* JADX WARNING: Removed duplicated region for block: B:1009:0x1629  */
    /* JADX WARNING: Removed duplicated region for block: B:1015:0x1678  */
    /* JADX WARNING: Removed duplicated region for block: B:851:0x119a  */
    /* JADX WARNING: Removed duplicated region for block: B:855:0x11b5  */
    /* JADX WARNING: Removed duplicated region for block: B:854:0x11ae  */
    /* JADX WARNING: Removed duplicated region for block: B:875:0x11f7  */
    /* JADX WARNING: Removed duplicated region for block: B:874:0x11f0  */
    /* JADX WARNING: Removed duplicated region for block: B:881:0x120d  */
    /* JADX WARNING: Removed duplicated region for block: B:878:0x1203  */
    /* JADX WARNING: Removed duplicated region for block: B:884:0x1214  */
    /* JADX WARNING: Removed duplicated region for block: B:919:0x12dc  */
    /* JADX WARNING: Removed duplicated region for block: B:915:0x12ab  */
    /* JADX WARNING: Removed duplicated region for block: B:930:0x1365  */
    /* JADX WARNING: Removed duplicated region for block: B:928:0x1344  */
    /* JADX WARNING: Removed duplicated region for block: B:1009:0x1629  */
    /* JADX WARNING: Removed duplicated region for block: B:1015:0x1678  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:813:0x1112  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x080a  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x081b  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0817  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x08af  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0834 A:{SYNTHETIC, Splitter:B:421:0x0834} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0a1f  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x08b7  */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0620  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0673  */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x06df  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x06b7  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x070b  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x07b0  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0716  */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x080a  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0817  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x081b  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0834 A:{SYNTHETIC, Splitter:B:421:0x0834} */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x08af  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x08b7  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0a1f  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:296:0x05c6  */
    /* JADX WARNING: Removed duplicated region for block: B:304:0x0620  */
    /* JADX WARNING: Removed duplicated region for block: B:311:0x0653  */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x0673  */
    /* JADX WARNING: Removed duplicated region for block: B:320:0x069a  */
    /* JADX WARNING: Removed duplicated region for block: B:327:0x06b7  */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x06df  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x070b  */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0716  */
    /* JADX WARNING: Removed duplicated region for block: B:396:0x07b0  */
    /* JADX WARNING: Removed duplicated region for block: B:411:0x080a  */
    /* JADX WARNING: Removed duplicated region for block: B:415:0x081b  */
    /* JADX WARNING: Removed duplicated region for block: B:414:0x0817  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x08af  */
    /* JADX WARNING: Removed duplicated region for block: B:421:0x0834 A:{SYNTHETIC, Splitter:B:421:0x0834} */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0a1f  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x08b7  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:1148:0x1ae3  */
    /* JADX WARNING: Removed duplicated region for block: B:1152:0x1af5  */
    /* JADX WARNING: Removed duplicated region for block: B:1151:0x1af0  */
    /* JADX WARNING: Removed duplicated region for block: B:1318:0x1f6f  */
    /* JADX WARNING: Removed duplicated region for block: B:1308:0x1var_  */
    /* JADX WARNING: Removed duplicated region for block: B:1307:0x1var_  */
    /* JADX WARNING: Removed duplicated region for block: B:1318:0x1f6f  */
    /* JADX WARNING: Removed duplicated region for block: B:1326:0x1fa8  */
    /* JADX WARNING: Removed duplicated region for block: B:1318:0x1f6f  */
    /* JADX WARNING: Removed duplicated region for block: B:1326:0x1fa8  */
    /* JADX WARNING: Removed duplicated region for block: B:1589:0x2779  */
    /* JADX WARNING: Removed duplicated region for block: B:1581:0x2767  */
    /* JADX WARNING: Removed duplicated region for block: B:1593:0x279e  */
    /* JADX WARNING: Removed duplicated region for block: B:1592:0x2788  */
    /* JADX WARNING: Removed duplicated region for block: B:2128:0x32b3  */
    /* JADX WARNING: Removed duplicated region for block: B:2127:0x3266  */
    /* JADX WARNING: Removed duplicated region for block: B:1993:0x2fd4  */
    /* JADX WARNING: Removed duplicated region for block: B:1842:0x2cb8  */
    /* JADX WARNING: Removed duplicated region for block: B:2055:0x3135  */
    /* JADX WARNING: Removed duplicated region for block: B:2010:0x3028 A:{SYNTHETIC, Splitter:B:2010:0x3028} */
    /* JADX WARNING: Removed duplicated region for block: B:2070:0x319c  */
    /* JADX WARNING: Removed duplicated region for block: B:2077:0x31d4  */
    /* JADX WARNING: Removed duplicated region for block: B:2076:0x31a9  */
    /* JADX WARNING: Removed duplicated region for block: B:2123:0x325e  */
    /* JADX WARNING: Removed duplicated region for block: B:2101:0x3218  */
    /* JADX WARNING: Removed duplicated region for block: B:2127:0x3266  */
    /* JADX WARNING: Removed duplicated region for block: B:2128:0x32b3  */
    /* JADX WARNING: Removed duplicated region for block: B:1818:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1811:0x2bf0  */
    /* JADX WARNING: Removed duplicated region for block: B:1821:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1825:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1824:0x2c6d  */
    /* JADX WARNING: Removed duplicated region for block: B:1828:0x2c7b  */
    /* JADX WARNING: Removed duplicated region for block: B:1833:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1831:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1842:0x2cb8  */
    /* JADX WARNING: Removed duplicated region for block: B:1993:0x2fd4  */
    /* JADX WARNING: Removed duplicated region for block: B:2010:0x3028 A:{SYNTHETIC, Splitter:B:2010:0x3028} */
    /* JADX WARNING: Removed duplicated region for block: B:2055:0x3135  */
    /* JADX WARNING: Removed duplicated region for block: B:2057:0x313b  */
    /* JADX WARNING: Removed duplicated region for block: B:2070:0x319c  */
    /* JADX WARNING: Removed duplicated region for block: B:2076:0x31a9  */
    /* JADX WARNING: Removed duplicated region for block: B:2077:0x31d4  */
    /* JADX WARNING: Removed duplicated region for block: B:2080:0x31ee  */
    /* JADX WARNING: Removed duplicated region for block: B:2088:0x31fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:2101:0x3218  */
    /* JADX WARNING: Removed duplicated region for block: B:2123:0x325e  */
    /* JADX WARNING: Removed duplicated region for block: B:2128:0x32b3  */
    /* JADX WARNING: Removed duplicated region for block: B:2127:0x3266  */
    /* JADX WARNING: Removed duplicated region for block: B:1760:0x2b2b  */
    /* JADX WARNING: Removed duplicated region for block: B:1759:0x2b28  */
    /* JADX WARNING: Removed duplicated region for block: B:1770:0x2b41  */
    /* JADX WARNING: Removed duplicated region for block: B:1785:0x2b76  */
    /* JADX WARNING: Removed duplicated region for block: B:1811:0x2bf0  */
    /* JADX WARNING: Removed duplicated region for block: B:1818:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1821:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1824:0x2c6d  */
    /* JADX WARNING: Removed duplicated region for block: B:1825:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1828:0x2c7b  */
    /* JADX WARNING: Removed duplicated region for block: B:1831:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1833:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1993:0x2fd4  */
    /* JADX WARNING: Removed duplicated region for block: B:1842:0x2cb8  */
    /* JADX WARNING: Removed duplicated region for block: B:2055:0x3135  */
    /* JADX WARNING: Removed duplicated region for block: B:2010:0x3028 A:{SYNTHETIC, Splitter:B:2010:0x3028} */
    /* JADX WARNING: Removed duplicated region for block: B:2057:0x313b  */
    /* JADX WARNING: Removed duplicated region for block: B:2070:0x319c  */
    /* JADX WARNING: Removed duplicated region for block: B:2077:0x31d4  */
    /* JADX WARNING: Removed duplicated region for block: B:2076:0x31a9  */
    /* JADX WARNING: Removed duplicated region for block: B:2080:0x31ee  */
    /* JADX WARNING: Removed duplicated region for block: B:2088:0x31fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:2123:0x325e  */
    /* JADX WARNING: Removed duplicated region for block: B:2101:0x3218  */
    /* JADX WARNING: Removed duplicated region for block: B:2127:0x3266  */
    /* JADX WARNING: Removed duplicated region for block: B:2128:0x32b3  */
    /* JADX WARNING: Removed duplicated region for block: B:1719:0x2a95  */
    /* JADX WARNING: Removed duplicated region for block: B:1718:0x2a90  */
    /* JADX WARNING: Removed duplicated region for block: B:1749:0x2b0c  */
    /* JADX WARNING: Removed duplicated region for block: B:1759:0x2b28  */
    /* JADX WARNING: Removed duplicated region for block: B:1760:0x2b2b  */
    /* JADX WARNING: Removed duplicated region for block: B:1770:0x2b41  */
    /* JADX WARNING: Removed duplicated region for block: B:1785:0x2b76  */
    /* JADX WARNING: Removed duplicated region for block: B:1818:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1811:0x2bf0  */
    /* JADX WARNING: Removed duplicated region for block: B:1821:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1825:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1824:0x2c6d  */
    /* JADX WARNING: Removed duplicated region for block: B:1828:0x2c7b  */
    /* JADX WARNING: Removed duplicated region for block: B:1833:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1831:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1842:0x2cb8  */
    /* JADX WARNING: Removed duplicated region for block: B:1993:0x2fd4  */
    /* JADX WARNING: Removed duplicated region for block: B:2010:0x3028 A:{SYNTHETIC, Splitter:B:2010:0x3028} */
    /* JADX WARNING: Removed duplicated region for block: B:2055:0x3135  */
    /* JADX WARNING: Removed duplicated region for block: B:2057:0x313b  */
    /* JADX WARNING: Removed duplicated region for block: B:2070:0x319c  */
    /* JADX WARNING: Removed duplicated region for block: B:2076:0x31a9  */
    /* JADX WARNING: Removed duplicated region for block: B:2077:0x31d4  */
    /* JADX WARNING: Removed duplicated region for block: B:2080:0x31ee  */
    /* JADX WARNING: Removed duplicated region for block: B:2088:0x31fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:2101:0x3218  */
    /* JADX WARNING: Removed duplicated region for block: B:2123:0x325e  */
    /* JADX WARNING: Removed duplicated region for block: B:2128:0x32b3  */
    /* JADX WARNING: Removed duplicated region for block: B:2127:0x3266  */
    /* JADX WARNING: Removed duplicated region for block: B:1697:0x2a32  */
    /* JADX WARNING: Removed duplicated region for block: B:1685:0x2a01  */
    /* JADX WARNING: Removed duplicated region for block: B:1708:0x2a64  */
    /* JADX WARNING: Removed duplicated region for block: B:1707:0x2a56  */
    /* JADX WARNING: Removed duplicated region for block: B:1718:0x2a90  */
    /* JADX WARNING: Removed duplicated region for block: B:1719:0x2a95  */
    /* JADX WARNING: Removed duplicated region for block: B:1749:0x2b0c  */
    /* JADX WARNING: Removed duplicated region for block: B:1760:0x2b2b  */
    /* JADX WARNING: Removed duplicated region for block: B:1759:0x2b28  */
    /* JADX WARNING: Removed duplicated region for block: B:1770:0x2b41  */
    /* JADX WARNING: Removed duplicated region for block: B:1785:0x2b76  */
    /* JADX WARNING: Removed duplicated region for block: B:1811:0x2bf0  */
    /* JADX WARNING: Removed duplicated region for block: B:1818:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1821:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1824:0x2c6d  */
    /* JADX WARNING: Removed duplicated region for block: B:1825:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1828:0x2c7b  */
    /* JADX WARNING: Removed duplicated region for block: B:1831:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1833:0x2CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:1993:0x2fd4  */
    /* JADX WARNING: Removed duplicated region for block: B:1842:0x2cb8  */
    /* JADX WARNING: Removed duplicated region for block: B:2055:0x3135  */
    /* JADX WARNING: Removed duplicated region for block: B:2010:0x3028 A:{SYNTHETIC, Splitter:B:2010:0x3028} */
    /* JADX WARNING: Removed duplicated region for block: B:2057:0x313b  */
    /* JADX WARNING: Removed duplicated region for block: B:2070:0x319c  */
    /* JADX WARNING: Removed duplicated region for block: B:2077:0x31d4  */
    /* JADX WARNING: Removed duplicated region for block: B:2076:0x31a9  */
    /* JADX WARNING: Removed duplicated region for block: B:2080:0x31ee  */
    /* JADX WARNING: Removed duplicated region for block: B:2088:0x31fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:2123:0x325e  */
    /* JADX WARNING: Removed duplicated region for block: B:2101:0x3218  */
    /* JADX WARNING: Removed duplicated region for block: B:2127:0x3266  */
    /* JADX WARNING: Removed duplicated region for block: B:2128:0x32b3  */
    /* JADX WARNING: Removed duplicated region for block: B:2371:0x38ba  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2240:0x3549  */
    /* JADX WARNING: Removed duplicated region for block: B:2233:0x351a  */
    /* JADX WARNING: Removed duplicated region for block: B:2243:0x354f  */
    /* JADX WARNING: Removed duplicated region for block: B:2250:0x3582  */
    /* JADX WARNING: Removed duplicated region for block: B:2246:0x355c  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2220:0x34d1  */
    /* JADX WARNING: Removed duplicated region for block: B:2233:0x351a  */
    /* JADX WARNING: Removed duplicated region for block: B:2240:0x3549  */
    /* JADX WARNING: Removed duplicated region for block: B:2243:0x354f  */
    /* JADX WARNING: Removed duplicated region for block: B:2246:0x355c  */
    /* JADX WARNING: Removed duplicated region for block: B:2250:0x3582  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:1372:0x20a1  */
    /* JADX WARNING: Removed duplicated region for block: B:1371:0x209b  */
    /* JADX WARNING: Removed duplicated region for block: B:1478:0x22eb  */
    /* JADX WARNING: Removed duplicated region for block: B:1388:0x20df  */
    /* JADX WARNING: Removed duplicated region for block: B:2220:0x34d1  */
    /* JADX WARNING: Removed duplicated region for block: B:2240:0x3549  */
    /* JADX WARNING: Removed duplicated region for block: B:2233:0x351a  */
    /* JADX WARNING: Removed duplicated region for block: B:2243:0x354f  */
    /* JADX WARNING: Removed duplicated region for block: B:2250:0x3582  */
    /* JADX WARNING: Removed duplicated region for block: B:2246:0x355c  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0200  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02b2  */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x17bb  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x02c3  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0200  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02b2  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x02c3  */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x17bb  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0156  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x01d8  */
    /* JADX WARNING: Removed duplicated region for block: B:139:0x0200  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x02b2  */
    /* JADX WARNING: Removed duplicated region for block: B:1044:0x17bb  */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x02c3  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:2057:0x313b  */
    /* JADX WARNING: Removed duplicated region for block: B:2070:0x319c  */
    /* JADX WARNING: Removed duplicated region for block: B:2076:0x31a9  */
    /* JADX WARNING: Removed duplicated region for block: B:2077:0x31d4  */
    /* JADX WARNING: Removed duplicated region for block: B:2080:0x31ee  */
    /* JADX WARNING: Removed duplicated region for block: B:2088:0x31fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:2101:0x3218  */
    /* JADX WARNING: Removed duplicated region for block: B:2123:0x325e  */
    /* JADX WARNING: Removed duplicated region for block: B:2128:0x32b3  */
    /* JADX WARNING: Removed duplicated region for block: B:2127:0x3266  */
    /* JADX WARNING: Removed duplicated region for block: B:2220:0x34d1  */
    /* JADX WARNING: Removed duplicated region for block: B:2233:0x351a  */
    /* JADX WARNING: Removed duplicated region for block: B:2240:0x3549  */
    /* JADX WARNING: Removed duplicated region for block: B:2243:0x354f  */
    /* JADX WARNING: Removed duplicated region for block: B:2246:0x355c  */
    /* JADX WARNING: Removed duplicated region for block: B:2250:0x3582  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:1435:0x21f6  */
    /* JADX WARNING: Removed duplicated region for block: B:1428:0x21c3  */
    /* JADX WARNING: Removed duplicated region for block: B:1438:0x21fb  */
    /* JADX WARNING: Removed duplicated region for block: B:1447:0x222a  */
    /* JADX WARNING: Removed duplicated region for block: B:1454:0x2258  */
    /* JADX WARNING: Removed duplicated region for block: B:1458:0x2273  */
    /* JADX WARNING: Removed duplicated region for block: B:1457:0x2266  */
    /* JADX WARNING: Removed duplicated region for block: B:1469:0x22ac  */
    /* JADX WARNING: Removed duplicated region for block: B:2220:0x34d1  */
    /* JADX WARNING: Removed duplicated region for block: B:2240:0x3549  */
    /* JADX WARNING: Removed duplicated region for block: B:2233:0x351a  */
    /* JADX WARNING: Removed duplicated region for block: B:2243:0x354f  */
    /* JADX WARNING: Removed duplicated region for block: B:2250:0x3582  */
    /* JADX WARNING: Removed duplicated region for block: B:2246:0x355c  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:1428:0x21c3  */
    /* JADX WARNING: Removed duplicated region for block: B:1435:0x21f6  */
    /* JADX WARNING: Removed duplicated region for block: B:1438:0x21fb  */
    /* JADX WARNING: Removed duplicated region for block: B:1447:0x222a  */
    /* JADX WARNING: Removed duplicated region for block: B:1454:0x2258  */
    /* JADX WARNING: Removed duplicated region for block: B:1457:0x2266  */
    /* JADX WARNING: Removed duplicated region for block: B:1458:0x2273  */
    /* JADX WARNING: Removed duplicated region for block: B:1469:0x22ac  */
    /* JADX WARNING: Removed duplicated region for block: B:2220:0x34d1  */
    /* JADX WARNING: Removed duplicated region for block: B:2233:0x351a  */
    /* JADX WARNING: Removed duplicated region for block: B:2240:0x3549  */
    /* JADX WARNING: Removed duplicated region for block: B:2243:0x354f  */
    /* JADX WARNING: Removed duplicated region for block: B:2246:0x355c  */
    /* JADX WARNING: Removed duplicated region for block: B:2250:0x3582  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x08b7  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0a1f  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:2057:0x313b  */
    /* JADX WARNING: Removed duplicated region for block: B:2070:0x319c  */
    /* JADX WARNING: Removed duplicated region for block: B:2077:0x31d4  */
    /* JADX WARNING: Removed duplicated region for block: B:2076:0x31a9  */
    /* JADX WARNING: Removed duplicated region for block: B:2080:0x31ee  */
    /* JADX WARNING: Removed duplicated region for block: B:2088:0x31fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:2123:0x325e  */
    /* JADX WARNING: Removed duplicated region for block: B:2101:0x3218  */
    /* JADX WARNING: Removed duplicated region for block: B:2127:0x3266  */
    /* JADX WARNING: Removed duplicated region for block: B:2128:0x32b3  */
    /* JADX WARNING: Removed duplicated region for block: B:2220:0x34d1  */
    /* JADX WARNING: Removed duplicated region for block: B:2240:0x3549  */
    /* JADX WARNING: Removed duplicated region for block: B:2233:0x351a  */
    /* JADX WARNING: Removed duplicated region for block: B:2243:0x354f  */
    /* JADX WARNING: Removed duplicated region for block: B:2250:0x3582  */
    /* JADX WARNING: Removed duplicated region for block: B:2246:0x355c  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0a1f  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x08b7  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:2057:0x313b  */
    /* JADX WARNING: Removed duplicated region for block: B:2070:0x319c  */
    /* JADX WARNING: Removed duplicated region for block: B:2076:0x31a9  */
    /* JADX WARNING: Removed duplicated region for block: B:2077:0x31d4  */
    /* JADX WARNING: Removed duplicated region for block: B:2080:0x31ee  */
    /* JADX WARNING: Removed duplicated region for block: B:2088:0x31fd A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:2101:0x3218  */
    /* JADX WARNING: Removed duplicated region for block: B:2123:0x325e  */
    /* JADX WARNING: Removed duplicated region for block: B:2128:0x32b3  */
    /* JADX WARNING: Removed duplicated region for block: B:2127:0x3266  */
    /* JADX WARNING: Removed duplicated region for block: B:2220:0x34d1  */
    /* JADX WARNING: Removed duplicated region for block: B:2233:0x351a  */
    /* JADX WARNING: Removed duplicated region for block: B:2240:0x3549  */
    /* JADX WARNING: Removed duplicated region for block: B:2243:0x354f  */
    /* JADX WARNING: Removed duplicated region for block: B:2246:0x355c  */
    /* JADX WARNING: Removed duplicated region for block: B:2250:0x3582  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x08b7  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0a1f  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Removed duplicated region for block: B:520:0x0a1f  */
    /* JADX WARNING: Removed duplicated region for block: B:446:0x08b7  */
    /* JADX WARNING: Removed duplicated region for block: B:522:0x0a30 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:548:0x0ad9  */
    /* JADX WARNING: Removed duplicated region for block: B:620:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:627:0x0CLASSNAME  */
    /* JADX WARNING: Removed duplicated region for block: B:798:0x10c8  */
    /* JADX WARNING: Removed duplicated region for block: B:629:0x0c3c  */
    /* JADX WARNING: Removed duplicated region for block: B:824:0x1133  */
    /* JADX WARNING: Removed duplicated region for block: B:2263:0x35c6  */
    /* JADX WARNING: Removed duplicated region for block: B:2274:0x3626 A:{SYNTHETIC, Splitter:B:2274:0x3626} */
    /* JADX WARNING: Removed duplicated region for block: B:2291:0x36b2  */
    /* JADX WARNING: Removed duplicated region for block: B:2306:0x3730 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2307:0x3732 A:{Catch:{ Exception -> 0x374e }} */
    /* JADX WARNING: Removed duplicated region for block: B:2314:0x3758 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2318:0x37a3 A:{Catch:{ Exception -> 0x37c5 }} */
    /* JADX WARNING: Removed duplicated region for block: B:2329:0x37d6  */
    /* JADX WARNING: Removed duplicated region for block: B:2335:0x37f8  */
    /* JADX WARNING: Removed duplicated region for block: B:2338:0x3809  */
    /* JADX WARNING: Removed duplicated region for block: B:2400:0x3a17  */
    /* JADX WARNING: Removed duplicated region for block: B:2406:0x3a33  */
    /* JADX WARNING: Removed duplicated region for block: B:2405:0x3a29  */
    /* JADX WARNING: Removed duplicated region for block: B:2422:0x3a68  */
    /* JADX WARNING: Removed duplicated region for block: B:2432:0x3aa5  */
    /* JADX WARNING: Removed duplicated region for block: B:2425:0x3a73  */
    /* JADX WARNING: Removed duplicated region for block: B:2435:0x3ab0  */
    /* JADX WARNING: Missing block: B:846:0x118f, code skipped:
            if (r5 != 8) goto L_0x1193;
     */
    /* JADX WARNING: Missing block: B:1771:0x2b49, code skipped:
            if ("m".equals(r4.type) == false) goto L_0x2b4b;
     */
    /* JADX WARNING: Missing block: B:2073:0x31a1, code skipped:
            if (r2 != 5) goto L_0x31e9;
     */
    private void setMessageContent(org.telegram.messenger.MessageObject r59, org.telegram.messenger.MessageObject.GroupedMessages r60, boolean r61, boolean r62) {
        /*
        r58 = this;
        r1 = r58;
        r14 = r59;
        r0 = r60;
        r2 = r61;
        r3 = r62;
        r4 = r59.checkLayout();
        r15 = 0;
        if (r4 != 0) goto L_0x001d;
    L_0x0011:
        r4 = r1.currentPosition;
        if (r4 == 0) goto L_0x001f;
    L_0x0015:
        r4 = r1.lastHeight;
        r5 = org.telegram.messenger.AndroidUtilities.displaySize;
        r5 = r5.y;
        if (r4 == r5) goto L_0x001f;
    L_0x001d:
        r1.currentMessageObject = r15;
    L_0x001f:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r1.lastHeight = r4;
        r4 = r1.currentMessageObject;
        r13 = 1;
        r12 = 0;
        if (r4 == 0) goto L_0x0039;
    L_0x002b:
        r4 = r4.getId();
        r5 = r59.getId();
        if (r4 == r5) goto L_0x0036;
    L_0x0035:
        goto L_0x0039;
    L_0x0036:
        r16 = 0;
        goto L_0x003b;
    L_0x0039:
        r16 = 1;
    L_0x003b:
        r4 = r1.currentMessageObject;
        if (r4 != r14) goto L_0x0046;
    L_0x003f:
        r4 = r14.forceUpdate;
        if (r4 == 0) goto L_0x0044;
    L_0x0043:
        goto L_0x0046;
    L_0x0044:
        r4 = 0;
        goto L_0x0047;
    L_0x0046:
        r4 = 1;
    L_0x0047:
        r5 = r1.currentMessageObject;
        r11 = 3;
        if (r5 == 0) goto L_0x0060;
    L_0x004c:
        r5 = r5.getId();
        r6 = r59.getId();
        if (r5 != r6) goto L_0x0060;
    L_0x0056:
        r5 = r1.lastSendState;
        if (r5 != r11) goto L_0x0060;
    L_0x005a:
        r5 = r59.isSent();
        if (r5 != 0) goto L_0x006e;
    L_0x0060:
        r5 = r1.currentMessageObject;
        if (r5 != r14) goto L_0x0071;
    L_0x0064:
        r5 = r58.isUserDataChanged();
        if (r5 != 0) goto L_0x006e;
    L_0x006a:
        r5 = r1.photoNotSet;
        if (r5 == 0) goto L_0x0071;
    L_0x006e:
        r17 = 1;
        goto L_0x0073;
    L_0x0071:
        r17 = 0;
    L_0x0073:
        r5 = r1.currentMessagesGroup;
        if (r0 == r5) goto L_0x0079;
    L_0x0077:
        r5 = 1;
        goto L_0x007a;
    L_0x0079:
        r5 = 0;
    L_0x007a:
        r10 = 0;
        if (r4 != 0) goto L_0x00cf;
    L_0x007d:
        r6 = r14.type;
        r7 = 17;
        if (r6 != r7) goto L_0x00cf;
    L_0x0083:
        r6 = r14.messageOwner;
        r6 = r6.media;
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
        if (r7 == 0) goto L_0x0096;
    L_0x008b:
        r6 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r6;
        r7 = r6.results;
        r8 = r7.results;
        r6 = r6.poll;
        r7 = r7.total_voters;
        goto L_0x0099;
    L_0x0096:
        r6 = r15;
        r8 = r6;
        r7 = 0;
    L_0x0099:
        if (r8 == 0) goto L_0x00a5;
    L_0x009b:
        r9 = r1.lastPollResults;
        if (r9 == 0) goto L_0x00a5;
    L_0x009f:
        r9 = r1.lastPollResultsVoters;
        if (r7 == r9) goto L_0x00a5;
    L_0x00a3:
        r7 = 1;
        goto L_0x00a6;
    L_0x00a5:
        r7 = 0;
    L_0x00a6:
        if (r7 != 0) goto L_0x00ad;
    L_0x00a8:
        r9 = r1.lastPollResults;
        if (r8 == r9) goto L_0x00ad;
    L_0x00ac:
        r7 = 1;
    L_0x00ad:
        if (r7 != 0) goto L_0x00ba;
    L_0x00af:
        r8 = r1.lastPoll;
        if (r8 == r6) goto L_0x00ba;
    L_0x00b3:
        r8 = r8.closed;
        r6 = r6.closed;
        if (r8 == r6) goto L_0x00ba;
    L_0x00b9:
        r7 = 1;
    L_0x00ba:
        if (r7 == 0) goto L_0x00d0;
    L_0x00bc:
        r6 = r1.attachedToWindow;
        if (r6 == 0) goto L_0x00d0;
    L_0x00c0:
        r1.pollAnimationProgressTime = r10;
        r6 = r1.pollVoted;
        if (r6 == 0) goto L_0x00d0;
    L_0x00c6:
        r6 = r59.isVoted();
        if (r6 != 0) goto L_0x00d0;
    L_0x00cc:
        r1.pollUnvoteInProgress = r13;
        goto L_0x00d0;
    L_0x00cf:
        r7 = 0;
    L_0x00d0:
        if (r5 != 0) goto L_0x00f1;
    L_0x00d2:
        if (r0 == 0) goto L_0x00f1;
    L_0x00d4:
        r5 = r0.messages;
        r5 = r5.size();
        if (r5 <= r13) goto L_0x00e9;
    L_0x00dc:
        r5 = r1.currentMessagesGroup;
        r5 = r5.positions;
        r6 = r1.currentMessageObject;
        r5 = r5.get(r6);
        r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5;
        goto L_0x00ea;
    L_0x00e9:
        r5 = r15;
    L_0x00ea:
        r6 = r1.currentPosition;
        if (r5 == r6) goto L_0x00f0;
    L_0x00ee:
        r5 = 1;
        goto L_0x00f1;
    L_0x00f0:
        r5 = 0;
    L_0x00f1:
        r9 = 2;
        if (r4 != 0) goto L_0x0108;
    L_0x00f4:
        if (r17 != 0) goto L_0x0108;
    L_0x00f6:
        if (r5 != 0) goto L_0x0108;
    L_0x00f8:
        if (r7 != 0) goto L_0x0108;
    L_0x00fa:
        r5 = r58.isPhotoDataChanged(r59);
        if (r5 != 0) goto L_0x0108;
    L_0x0100:
        r5 = r1.pinnedBottom;
        if (r5 != r2) goto L_0x0108;
    L_0x0104:
        r5 = r1.pinnedTop;
        if (r5 == r3) goto L_0x3aab;
    L_0x0108:
        r1.pinnedBottom = r2;
        r1.pinnedTop = r3;
        r1.currentMessageObject = r14;
        r1.currentMessagesGroup = r0;
        r0 = -2;
        r1.lastTime = r0;
        r1.isHighlightedAnimated = r12;
        r8 = -1;
        r1.widthBeforeNewTimeLine = r8;
        r0 = r1.currentMessagesGroup;
        if (r0 == 0) goto L_0x0139;
    L_0x011c:
        r0 = r0.posArray;
        r0 = r0.size();
        if (r0 <= r13) goto L_0x0139;
    L_0x0124:
        r0 = r1.currentMessagesGroup;
        r0 = r0.positions;
        r2 = r1.currentMessageObject;
        r0 = r0.get(r2);
        r0 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r0;
        r1.currentPosition = r0;
        r0 = r1.currentPosition;
        if (r0 != 0) goto L_0x013d;
    L_0x0136:
        r1.currentMessagesGroup = r15;
        goto L_0x013d;
    L_0x0139:
        r1.currentMessagesGroup = r15;
        r1.currentPosition = r15;
    L_0x013d:
        r0 = r1.pinnedTop;
        if (r0 == 0) goto L_0x014d;
    L_0x0141:
        r0 = r1.currentPosition;
        if (r0 == 0) goto L_0x014b;
    L_0x0145:
        r0 = r0.flags;
        r0 = r0 & 4;
        if (r0 == 0) goto L_0x014d;
    L_0x014b:
        r0 = 1;
        goto L_0x014e;
    L_0x014d:
        r0 = 0;
    L_0x014e:
        r1.drawPinnedTop = r0;
        r0 = r1.pinnedBottom;
        r6 = 8;
        if (r0 == 0) goto L_0x0161;
    L_0x0156:
        r0 = r1.currentPosition;
        if (r0 == 0) goto L_0x015f;
    L_0x015a:
        r0 = r0.flags;
        r0 = r0 & r6;
        if (r0 == 0) goto L_0x0161;
    L_0x015f:
        r0 = 1;
        goto L_0x0162;
    L_0x0161:
        r0 = 0;
    L_0x0162:
        r1.drawPinnedBottom = r0;
        r0 = r1.photoImage;
        r0.setCrossfadeWithOldImage(r12);
        r0 = r14.messageOwner;
        r2 = r0.send_state;
        r1.lastSendState = r2;
        r2 = r0.destroyTime;
        r1.lastDeleteDate = r2;
        r0 = r0.views;
        r1.lastViewsCount = r0;
        r1.isPressed = r12;
        r1.gamePreviewPressed = r12;
        r1.sharePressed = r12;
        r1.isCheckPressed = r13;
        r1.hasNewLineForTime = r12;
        r0 = r1.isChat;
        if (r0 == 0) goto L_0x019b;
    L_0x0185:
        r0 = r59.isOutOwner();
        if (r0 != 0) goto L_0x019b;
    L_0x018b:
        r0 = r59.needDrawAvatar();
        if (r0 == 0) goto L_0x019b;
    L_0x0191:
        r0 = r1.currentPosition;
        if (r0 == 0) goto L_0x0199;
    L_0x0195:
        r0 = r0.edge;
        if (r0 == 0) goto L_0x019b;
    L_0x0199:
        r0 = 1;
        goto L_0x019c;
    L_0x019b:
        r0 = 0;
    L_0x019c:
        r1.isAvatarVisible = r0;
        r1.wasLayout = r12;
        r1.drwaShareGoIcon = r12;
        r1.groupPhotoInvisible = r12;
        r1.animatingDrawVideoImageButton = r12;
        r1.drawVideoSize = r12;
        r1.canStreamVideo = r12;
        r1.animatingNoSound = r12;
        r0 = r58.checkNeedDrawShareButton(r59);
        r1.drawShareButton = r0;
        r1.replyNameLayout = r15;
        r1.adminLayout = r15;
        r1.checkOnlyButtonPressed = r12;
        r1.replyTextLayout = r15;
        r1.hasEmbed = r12;
        r1.autoPlayingMedia = r12;
        r1.replyNameWidth = r12;
        r1.replyTextWidth = r12;
        r1.viaWidth = r12;
        r1.viaNameWidth = r12;
        r1.addedCaptionHeight = r12;
        r1.currentReplyPhoto = r15;
        r1.currentUser = r15;
        r1.currentChat = r15;
        r1.currentViaBotUser = r15;
        r1.instantViewLayout = r15;
        r1.drawNameLayout = r12;
        r0 = r1.scheduledInvalidate;
        if (r0 == 0) goto L_0x01df;
    L_0x01d8:
        r0 = r1.invalidateRunnable;
        org.telegram.messenger.AndroidUtilities.cancelRunOnUIThread(r0);
        r1.scheduledInvalidate = r12;
    L_0x01df:
        r1.resetPressedLink(r8);
        r14.forceUpdate = r12;
        r1.drawPhotoImage = r12;
        r1.drawPhotoCheckBox = r12;
        r1.hasLinkPreview = r12;
        r1.hasOldCaptionPreview = r12;
        r1.hasGamePreview = r12;
        r1.hasInvoicePreview = r12;
        r1.instantButtonPressed = r12;
        r1.instantPressed = r12;
        if (r7 != 0) goto L_0x020a;
    L_0x01f6:
        r0 = android.os.Build.VERSION.SDK_INT;
        r2 = 21;
        if (r0 < r2) goto L_0x020a;
    L_0x01fc:
        r0 = r1.selectorDrawable;
        if (r0 == 0) goto L_0x020a;
    L_0x0200:
        r0.setVisible(r12, r12);
        r0 = r1.selectorDrawable;
        r2 = android.util.StateSet.NOTHING;
        r0.setState(r2);
    L_0x020a:
        r1.linkPreviewPressed = r12;
        r1.buttonPressed = r12;
        r1.miniButtonPressed = r12;
        r1.pressedBotButton = r8;
        r1.pressedVoteButton = r8;
        r1.linkPreviewHeight = r12;
        r1.mediaOffsetY = r12;
        r1.documentAttachType = r12;
        r1.documentAttach = r15;
        r1.descriptionLayout = r15;
        r1.titleLayout = r15;
        r1.videoInfoLayout = r15;
        r1.photosCountLayout = r15;
        r1.siteNameLayout = r15;
        r1.authorLayout = r15;
        r1.captionLayout = r15;
        r1.captionOffsetX = r12;
        r1.currentCaption = r15;
        r1.docTitleLayout = r15;
        r1.drawImageButton = r12;
        r1.drawVideoImageButton = r12;
        r1.currentPhotoObject = r15;
        r1.photoParentObject = r15;
        r1.currentPhotoObjectThumb = r15;
        r1.currentPhotoFilter = r15;
        r1.infoLayout = r15;
        r1.cancelLoading = r12;
        r1.buttonState = r8;
        r1.miniButtonState = r8;
        r1.hasMiniProgress = r12;
        r0 = r1.addedForTest;
        if (r0 == 0) goto L_0x025b;
    L_0x024a:
        r0 = r1.currentUrl;
        if (r0 == 0) goto L_0x025b;
    L_0x024e:
        r0 = r1.currentWebFile;
        if (r0 == 0) goto L_0x025b;
    L_0x0252:
        r0 = org.telegram.messenger.ImageLoader.getInstance();
        r2 = r1.currentUrl;
        r0.removeTestWebFile(r2);
    L_0x025b:
        r1.addedForTest = r12;
        r1.currentUrl = r15;
        r1.currentWebFile = r15;
        r1.photoNotSet = r12;
        r1.drawBackground = r13;
        r1.drawName = r12;
        r1.useSeekBarWaweform = r12;
        r1.drawInstantView = r12;
        r1.drawInstantViewType = r12;
        r1.drawForwardedName = r12;
        r0 = r1.photoImage;
        r0.setSideClip(r10);
        r1.imageBackgroundColor = r12;
        r1.imageBackgroundSideColor = r12;
        r1.mediaBackground = r12;
        r0 = r1.photoImage;
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0.setAlpha(r7);
        if (r4 != 0) goto L_0x0285;
    L_0x0283:
        if (r17 == 0) goto L_0x028a;
    L_0x0285:
        r0 = r1.pollButtons;
        r0.clear();
    L_0x028a:
        r1.availableTimeWidth = r12;
        r0 = r1.photoImage;
        r0.setForceLoading(r12);
        r0 = r1.photoImage;
        r0.setNeedsQualityThumb(r12);
        r0 = r1.photoImage;
        r0.setShouldGenerateQualityThumb(r12);
        r0 = r1.photoImage;
        r0.setAllowDecodeSingleFrame(r12);
        r0 = r1.photoImage;
        r2 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.setRoundRadius(r2);
        r0 = r1.photoImage;
        r0.setColorFilter(r15);
        if (r4 == 0) goto L_0x02b8;
    L_0x02b2:
        r1.firstVisibleBlockNum = r12;
        r1.lastVisibleBlockNum = r12;
        r1.needNewVisiblePart = r13;
    L_0x02b8:
        r0 = r14.type;
        r18 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r2 = 6;
        r19 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r20 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        if (r0 != 0) goto L_0x17bb;
    L_0x02c3:
        r1.drawForwardedName = r13;
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x0308;
    L_0x02cb:
        r0 = r1.isChat;
        if (r0 == 0) goto L_0x02e9;
    L_0x02cf:
        r0 = r59.isOutOwner();
        if (r0 != 0) goto L_0x02e9;
    L_0x02d5:
        r0 = r59.needDrawAvatar();
        if (r0 == 0) goto L_0x02e9;
    L_0x02db:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = NUM; // 0x42var_ float:122.0 double:5.54977537E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 - r4;
        r1.drawName = r13;
        goto L_0x0350;
    L_0x02e9:
        r0 = r14.messageOwner;
        r0 = r0.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x02f9;
    L_0x02f1:
        r0 = r59.isOutOwner();
        if (r0 != 0) goto L_0x02f9;
    L_0x02f7:
        r0 = 1;
        goto L_0x02fa;
    L_0x02f9:
        r0 = 0;
    L_0x02fa:
        r1.drawName = r0;
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r4 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 - r4;
        goto L_0x0350;
    L_0x0308:
        r0 = r1.isChat;
        if (r0 == 0) goto L_0x032c;
    L_0x030c:
        r0 = r59.isOutOwner();
        if (r0 != 0) goto L_0x032c;
    L_0x0312:
        r0 = r59.needDrawAvatar();
        if (r0 == 0) goto L_0x032c;
    L_0x0318:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r0.x;
        r0 = r0.y;
        r0 = java.lang.Math.min(r4, r0);
        r4 = NUM; // 0x42var_ float:122.0 double:5.54977537E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 - r4;
        r1.drawName = r13;
        goto L_0x0350;
    L_0x032c:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r0.x;
        r0 = r0.y;
        r0 = java.lang.Math.min(r4, r0);
        r4 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 - r4;
        r4 = r14.messageOwner;
        r4 = r4.to_id;
        r4 = r4.channel_id;
        if (r4 == 0) goto L_0x034d;
    L_0x0345:
        r4 = r59.isOutOwner();
        if (r4 != 0) goto L_0x034d;
    L_0x034b:
        r4 = 1;
        goto L_0x034e;
    L_0x034d:
        r4 = 0;
    L_0x034e:
        r1.drawName = r4;
    L_0x0350:
        r4 = r0;
        r1.availableTimeWidth = r4;
        r0 = r59.isRoundVideo();
        if (r0 == 0) goto L_0x0385;
    L_0x0359:
        r0 = r1.availableTimeWidth;
        r6 = (double) r0;
        r0 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r5 = "00:00";
        r0 = r0.measureText(r5);
        r5 = r4;
        r3 = (double) r0;
        r3 = java.lang.Math.ceil(r3);
        r0 = r59.isOutOwner();
        if (r0 == 0) goto L_0x0372;
    L_0x0370:
        r0 = 0;
        goto L_0x0378;
    L_0x0372:
        r0 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
    L_0x0378:
        r10 = (double) r0;
        java.lang.Double.isNaN(r10);
        r3 = r3 + r10;
        java.lang.Double.isNaN(r6);
        r6 = r6 - r3;
        r0 = (int) r6;
        r1.availableTimeWidth = r0;
        goto L_0x0386;
    L_0x0385:
        r5 = r4;
    L_0x0386:
        r58.measureTime(r59);
        r0 = r1.timeWidth;
        r3 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 + r3;
        r3 = r59.isOutOwner();
        if (r3 == 0) goto L_0x039f;
    L_0x0398:
        r3 = NUM; // 0x41a40000 float:20.5 double:5.44098164E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 + r3;
    L_0x039f:
        r11 = r0;
        r0 = r14.messageOwner;
        r0 = r0.media;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r3 == 0) goto L_0x03b0;
    L_0x03a8:
        r0 = r0.game;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_game;
        if (r0 == 0) goto L_0x03b0;
    L_0x03ae:
        r0 = 1;
        goto L_0x03b1;
    L_0x03b0:
        r0 = 0;
    L_0x03b1:
        r1.hasGamePreview = r0;
        r0 = r14.messageOwner;
        r0 = r0.media;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
        r1.hasInvoicePreview = r3;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r3 == 0) goto L_0x03c7;
    L_0x03bf:
        r0 = r0.webpage;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r0 == 0) goto L_0x03c7;
    L_0x03c5:
        r0 = 1;
        goto L_0x03c8;
    L_0x03c7:
        r0 = 0;
    L_0x03c8:
        r1.hasLinkPreview = r0;
        r0 = r1.hasLinkPreview;
        if (r0 == 0) goto L_0x03da;
    L_0x03ce:
        r0 = r14.messageOwner;
        r0 = r0.media;
        r0 = r0.webpage;
        r0 = r0.cached_page;
        if (r0 == 0) goto L_0x03da;
    L_0x03d8:
        r0 = 1;
        goto L_0x03db;
    L_0x03da:
        r0 = 0;
    L_0x03db:
        r1.drawInstantView = r0;
        r0 = r1.hasLinkPreview;
        if (r0 == 0) goto L_0x03f7;
    L_0x03e1:
        r0 = r14.messageOwner;
        r0 = r0.media;
        r0 = r0.webpage;
        r0 = r0.embed_url;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x03f7;
    L_0x03ef:
        r0 = r59.isGif();
        if (r0 != 0) goto L_0x03f7;
    L_0x03f5:
        r0 = 1;
        goto L_0x03f8;
    L_0x03f7:
        r0 = 0;
    L_0x03f8:
        r1.hasEmbed = r0;
        r0 = r1.hasLinkPreview;
        if (r0 == 0) goto L_0x0407;
    L_0x03fe:
        r0 = r14.messageOwner;
        r0 = r0.media;
        r0 = r0.webpage;
        r0 = r0.site_name;
        goto L_0x0408;
    L_0x0407:
        r0 = r15;
    L_0x0408:
        r3 = r1.hasLinkPreview;
        if (r3 == 0) goto L_0x0415;
    L_0x040c:
        r3 = r14.messageOwner;
        r3 = r3.media;
        r3 = r3.webpage;
        r3 = r3.type;
        goto L_0x0416;
    L_0x0415:
        r3 = r15;
    L_0x0416:
        r4 = r1.drawInstantView;
        if (r4 != 0) goto L_0x0502;
    L_0x041a:
        r0 = "telegram_channel";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x0428;
    L_0x0422:
        r1.drawInstantView = r13;
        r1.drawInstantViewType = r13;
        goto L_0x05b0;
    L_0x0428:
        r0 = "telegram_megagroup";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x0436;
    L_0x0430:
        r1.drawInstantView = r13;
        r1.drawInstantViewType = r9;
        goto L_0x05b0;
    L_0x0436:
        r0 = "telegram_message";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x0445;
    L_0x043e:
        r1.drawInstantView = r13;
        r3 = 3;
        r1.drawInstantViewType = r3;
        goto L_0x05b0;
    L_0x0445:
        r0 = "telegram_background";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x05b0;
    L_0x044d:
        r1.drawInstantView = r13;
        r1.drawInstantViewType = r2;
        r0 = r14.messageOwner;	 Catch:{ Exception -> 0x05b0 }
        r0 = r0.media;	 Catch:{ Exception -> 0x05b0 }
        r0 = r0.webpage;	 Catch:{ Exception -> 0x05b0 }
        r0 = r0.url;	 Catch:{ Exception -> 0x05b0 }
        r0 = android.net.Uri.parse(r0);	 Catch:{ Exception -> 0x05b0 }
        r3 = "intensity";
        r3 = r0.getQueryParameter(r3);	 Catch:{ Exception -> 0x05b0 }
        r3 = org.telegram.messenger.Utilities.parseInt(r3);	 Catch:{ Exception -> 0x05b0 }
        r3 = r3.intValue();	 Catch:{ Exception -> 0x05b0 }
        r4 = "bg_color";
        r4 = r0.getQueryParameter(r4);	 Catch:{ Exception -> 0x05b0 }
        r6 = android.text.TextUtils.isEmpty(r4);	 Catch:{ Exception -> 0x05b0 }
        if (r6 == 0) goto L_0x048d;
    L_0x0477:
        r6 = r59.getDocument();	 Catch:{ Exception -> 0x05b0 }
        if (r6 == 0) goto L_0x0489;
    L_0x047d:
        r7 = "image/png";
        r6 = r6.mime_type;	 Catch:{ Exception -> 0x05b0 }
        r6 = r7.equals(r6);	 Catch:{ Exception -> 0x05b0 }
        if (r6 == 0) goto L_0x0489;
    L_0x0487:
        r4 = "ffffff";
    L_0x0489:
        if (r3 != 0) goto L_0x048d;
    L_0x048b:
        r3 = 50;
    L_0x048d:
        if (r4 == 0) goto L_0x04bf;
    L_0x048f:
        r0 = 16;
        r0 = java.lang.Integer.parseInt(r4, r0);	 Catch:{ Exception -> 0x05b0 }
        r4 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r0 = r0 | r4;
        r1.imageBackgroundColor = r0;	 Catch:{ Exception -> 0x05b0 }
        r0 = r1.imageBackgroundColor;	 Catch:{ Exception -> 0x05b0 }
        r0 = org.telegram.messenger.AndroidUtilities.getPatternSideColor(r0);	 Catch:{ Exception -> 0x05b0 }
        r1.imageBackgroundSideColor = r0;	 Catch:{ Exception -> 0x05b0 }
        r0 = r1.photoImage;	 Catch:{ Exception -> 0x05b0 }
        r4 = new android.graphics.PorterDuffColorFilter;	 Catch:{ Exception -> 0x05b0 }
        r6 = r1.imageBackgroundColor;	 Catch:{ Exception -> 0x05b0 }
        r6 = org.telegram.messenger.AndroidUtilities.getPatternColor(r6);	 Catch:{ Exception -> 0x05b0 }
        r7 = android.graphics.PorterDuff.Mode.SRC_IN;	 Catch:{ Exception -> 0x05b0 }
        r4.<init>(r6, r7);	 Catch:{ Exception -> 0x05b0 }
        r0.setColorFilter(r4);	 Catch:{ Exception -> 0x05b0 }
        r0 = r1.photoImage;	 Catch:{ Exception -> 0x05b0 }
        r3 = (float) r3;	 Catch:{ Exception -> 0x05b0 }
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r3 = r3 / r4;
        r0.setAlpha(r3);	 Catch:{ Exception -> 0x05b0 }
        goto L_0x05b0;
    L_0x04bf:
        r0 = r0.getLastPathSegment();	 Catch:{ Exception -> 0x05b0 }
        if (r0 == 0) goto L_0x05b0;
    L_0x04c5:
        r3 = r0.length();	 Catch:{ Exception -> 0x05b0 }
        if (r3 != r2) goto L_0x05b0;
    L_0x04cb:
        r3 = 16;
        r0 = java.lang.Integer.parseInt(r0, r3);	 Catch:{ Exception -> 0x05b0 }
        r3 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r0 = r0 | r3;
        r1.imageBackgroundColor = r0;	 Catch:{ Exception -> 0x05b0 }
        r0 = new org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;	 Catch:{ Exception -> 0x05b0 }
        r0.<init>();	 Catch:{ Exception -> 0x05b0 }
        r1.currentPhotoObject = r0;	 Catch:{ Exception -> 0x05b0 }
        r0 = r1.currentPhotoObject;	 Catch:{ Exception -> 0x05b0 }
        r3 = "s";
        r0.type = r3;	 Catch:{ Exception -> 0x05b0 }
        r0 = r1.currentPhotoObject;	 Catch:{ Exception -> 0x05b0 }
        r3 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x05b0 }
        r0.w = r3;	 Catch:{ Exception -> 0x05b0 }
        r0 = r1.currentPhotoObject;	 Catch:{ Exception -> 0x05b0 }
        r3 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x05b0 }
        r0.h = r3;	 Catch:{ Exception -> 0x05b0 }
        r0 = r1.currentPhotoObject;	 Catch:{ Exception -> 0x05b0 }
        r3 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;	 Catch:{ Exception -> 0x05b0 }
        r3.<init>();	 Catch:{ Exception -> 0x05b0 }
        r0.location = r3;	 Catch:{ Exception -> 0x05b0 }
        goto L_0x05b0;
    L_0x0502:
        if (r0 == 0) goto L_0x05b0;
    L_0x0504:
        r0 = r0.toLowerCase();
        r4 = "instagram";
        r4 = r0.equals(r4);
        if (r4 != 0) goto L_0x0520;
    L_0x0510:
        r4 = "twitter";
        r0 = r0.equals(r4);
        if (r0 != 0) goto L_0x0520;
    L_0x0518:
        r0 = "telegram_album";
        r0 = r0.equals(r3);
        if (r0 == 0) goto L_0x05b0;
    L_0x0520:
        r0 = r14.messageOwner;
        r0 = r0.media;
        r0 = r0.webpage;
        r3 = r0.cached_page;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_page;
        if (r3 == 0) goto L_0x05b0;
    L_0x052c:
        r3 = r0.photo;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photo;
        if (r3 != 0) goto L_0x053a;
    L_0x0532:
        r0 = r0.document;
        r0 = org.telegram.messenger.MessageObject.isVideoDocument(r0);
        if (r0 == 0) goto L_0x05b0;
    L_0x053a:
        r1.drawInstantView = r12;
        r0 = r14.messageOwner;
        r0 = r0.media;
        r0 = r0.webpage;
        r0 = r0.cached_page;
        r0 = r0.blocks;
        r3 = 0;
        r4 = 1;
    L_0x0548:
        r6 = r0.size();
        if (r3 >= r6) goto L_0x0570;
    L_0x054e:
        r6 = r0.get(r3);
        r6 = (org.telegram.tgnet.TLRPC.PageBlock) r6;
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
        if (r7 == 0) goto L_0x0561;
    L_0x0558:
        r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow) r6;
        r4 = r6.items;
        r4 = r4.size();
        goto L_0x056d;
    L_0x0561:
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
        if (r7 == 0) goto L_0x056d;
    L_0x0565:
        r6 = (org.telegram.tgnet.TLRPC.TL_pageBlockCollage) r6;
        r4 = r6.items;
        r4 = r4.size();
    L_0x056d:
        r3 = r3 + 1;
        goto L_0x0548;
    L_0x0570:
        r0 = NUM; // 0x7f0d06f0 float:1.8745717E38 double:1.053130655E-314;
        r3 = new java.lang.Object[r9];
        r6 = java.lang.Integer.valueOf(r13);
        r3[r12] = r6;
        r4 = java.lang.Integer.valueOf(r4);
        r3[r13] = r4;
        r4 = "Of";
        r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r3);
        r3 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r3 = r3.measureText(r0);
        r3 = (double) r3;
        r3 = java.lang.Math.ceil(r3);
        r3 = (int) r3;
        r1.photosCountWidth = r3;
        r3 = new android.text.StaticLayout;
        r25 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r4 = r1.photosCountWidth;
        r27 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r28 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r29 = 0;
        r30 = 0;
        r23 = r3;
        r24 = r0;
        r26 = r4;
        r23.<init>(r24, r25, r26, r27, r28, r29, r30);
        r1.photosCountLayout = r3;
        r0 = 1;
        goto L_0x05b1;
    L_0x05b0:
        r0 = 0;
    L_0x05b1:
        r1.backgroundWidth = r5;
        r3 = r1.hasLinkPreview;
        if (r3 != 0) goto L_0x05ed;
    L_0x05b7:
        r3 = r1.hasGamePreview;
        if (r3 != 0) goto L_0x05ed;
    L_0x05bb:
        r3 = r1.hasInvoicePreview;
        if (r3 != 0) goto L_0x05ed;
    L_0x05bf:
        r3 = r14.lastLineWidth;
        r4 = r5 - r3;
        if (r4 >= r11) goto L_0x05c6;
    L_0x05c5:
        goto L_0x05ed;
    L_0x05c6:
        r4 = r1.backgroundWidth;
        r3 = r4 - r3;
        if (r3 < 0) goto L_0x05da;
    L_0x05cc:
        if (r3 > r11) goto L_0x05da;
    L_0x05ce:
        r4 = r4 + r11;
        r4 = r4 - r3;
        r3 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r4 + r3;
        r1.backgroundWidth = r4;
        goto L_0x060f;
    L_0x05da:
        r3 = r1.backgroundWidth;
        r4 = r14.lastLineWidth;
        r4 = r4 + r11;
        r3 = java.lang.Math.max(r3, r4);
        r4 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r4;
        r1.backgroundWidth = r3;
        goto L_0x060f;
    L_0x05ed:
        r3 = r1.backgroundWidth;
        r4 = r14.lastLineWidth;
        r3 = java.lang.Math.max(r3, r4);
        r4 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r4;
        r1.backgroundWidth = r3;
        r3 = r1.backgroundWidth;
        r4 = r1.timeWidth;
        r6 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r3 = java.lang.Math.max(r3, r4);
        r1.backgroundWidth = r3;
    L_0x060f:
        r3 = r1.backgroundWidth;
        r4 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r1.availableTimeWidth = r3;
        r3 = r59.isRoundVideo();
        if (r3 == 0) goto L_0x064a;
    L_0x0620:
        r3 = r1.availableTimeWidth;
        r3 = (double) r3;
        r6 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r7 = "00:00";
        r6 = r6.measureText(r7);
        r6 = (double) r6;
        r6 = java.lang.Math.ceil(r6);
        r10 = r59.isOutOwner();
        if (r10 == 0) goto L_0x0638;
    L_0x0636:
        r10 = 0;
        goto L_0x063e;
    L_0x0638:
        r10 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
    L_0x063e:
        r8 = (double) r10;
        java.lang.Double.isNaN(r8);
        r6 = r6 + r8;
        java.lang.Double.isNaN(r3);
        r3 = r3 - r6;
        r3 = (int) r3;
        r1.availableTimeWidth = r3;
    L_0x064a:
        r58.setMessageObjectInternal(r59);
        r3 = r14.textWidth;
        r4 = r1.hasGamePreview;
        if (r4 != 0) goto L_0x065a;
    L_0x0653:
        r4 = r1.hasInvoicePreview;
        if (r4 == 0) goto L_0x0658;
    L_0x0657:
        goto L_0x065a;
    L_0x0658:
        r4 = 0;
        goto L_0x065e;
    L_0x065a:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r19);
    L_0x065e:
        r3 = r3 + r4;
        r1.backgroundWidth = r3;
        r3 = r14.textHeight;
        r4 = NUM; // 0x419CLASSNAME float:19.5 double:5.43839131E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r4;
        r4 = r1.namesOffset;
        r3 = r3 + r4;
        r1.totalHeight = r3;
        r3 = r1.drawPinnedTop;
        if (r3 == 0) goto L_0x067c;
    L_0x0673:
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r4 - r6;
        r1.namesOffset = r4;
    L_0x067c:
        r3 = r1.backgroundWidth;
        r4 = r1.nameWidth;
        r3 = java.lang.Math.max(r3, r4);
        r4 = r1.forwardedNameWidth;
        r3 = java.lang.Math.max(r3, r4);
        r4 = r1.replyNameWidth;
        r3 = java.lang.Math.max(r3, r4);
        r4 = r1.replyTextWidth;
        r3 = java.lang.Math.max(r3, r4);
        r4 = r1.hasLinkPreview;
        if (r4 != 0) goto L_0x06b1;
    L_0x069a:
        r4 = r1.hasGamePreview;
        if (r4 != 0) goto L_0x06b1;
    L_0x069e:
        r4 = r1.hasInvoicePreview;
        if (r4 == 0) goto L_0x06a3;
    L_0x06a2:
        goto L_0x06b1;
    L_0x06a3:
        r0 = r1.photoImage;
        r0.setImageBitmap(r15);
        r1.calcBackgroundWidth(r5, r11, r3);
        r61 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = 2;
        r15 = 1;
        goto L_0x2047;
    L_0x06b1:
        r4 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r4 == 0) goto L_0x06df;
    L_0x06b7:
        r4 = r1.isChat;
        if (r4 == 0) goto L_0x06d4;
    L_0x06bb:
        r4 = r59.needDrawAvatar();
        if (r4 == 0) goto L_0x06d4;
    L_0x06c1:
        r4 = r1.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 != 0) goto L_0x06d4;
    L_0x06c9:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        goto L_0x0706;
    L_0x06d4:
        r4 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        goto L_0x0706;
    L_0x06df:
        r4 = r1.isChat;
        if (r4 == 0) goto L_0x06fc;
    L_0x06e3:
        r4 = r59.needDrawAvatar();
        if (r4 == 0) goto L_0x06fc;
    L_0x06e9:
        r4 = r1.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 != 0) goto L_0x06fc;
    L_0x06f1:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        goto L_0x0706;
    L_0x06fc:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.x;
        r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
    L_0x0706:
        r4 = r4 - r6;
        r6 = r1.drawShareButton;
        if (r6 == 0) goto L_0x0712;
    L_0x070b:
        r6 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 - r6;
    L_0x0712:
        r6 = r1.hasLinkPreview;
        if (r6 == 0) goto L_0x07b0;
    L_0x0716:
        r6 = r14.messageOwner;
        r6 = r6.media;
        r6 = r6.webpage;
        r6 = (org.telegram.tgnet.TLRPC.TL_webPage) r6;
        r7 = r6.site_name;
        r8 = r1.drawInstantViewType;
        if (r8 == r2) goto L_0x0727;
    L_0x0724:
        r8 = r6.title;
        goto L_0x0728;
    L_0x0727:
        r8 = r15;
    L_0x0728:
        r9 = r1.drawInstantViewType;
        if (r9 == r2) goto L_0x072f;
    L_0x072c:
        r9 = r6.author;
        goto L_0x0730;
    L_0x072f:
        r9 = r15;
    L_0x0730:
        r10 = r1.drawInstantViewType;
        if (r10 == r2) goto L_0x0737;
    L_0x0734:
        r10 = r6.description;
        goto L_0x0738;
    L_0x0737:
        r10 = r15;
    L_0x0738:
        r15 = r6.photo;
        r13 = r6.document;
        r2 = r6.type;
        r6 = r6.duration;
        if (r7 == 0) goto L_0x0761;
    L_0x0742:
        if (r15 == 0) goto L_0x0761;
    L_0x0744:
        r12 = r7.toLowerCase();
        r29 = r4;
        r4 = "instagram";
        r4 = r12.equals(r4);
        if (r4 == 0) goto L_0x0763;
    L_0x0752:
        r4 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r4.y;
        r12 = 3;
        r4 = r4 / r12;
        r12 = r1.currentMessageObject;
        r12 = r12.textWidth;
        r4 = java.lang.Math.max(r4, r12);
        goto L_0x0765;
    L_0x0761:
        r29 = r4;
    L_0x0763:
        r4 = r29;
    L_0x0765:
        r12 = "app";
        r12 = r12.equals(r2);
        if (r12 != 0) goto L_0x0780;
    L_0x076d:
        r12 = "profile";
        r12 = r12.equals(r2);
        if (r12 != 0) goto L_0x0780;
    L_0x0775:
        r12 = "article";
        r12 = r12.equals(r2);
        if (r12 == 0) goto L_0x077e;
    L_0x077d:
        goto L_0x0780;
    L_0x077e:
        r12 = 0;
        goto L_0x0781;
    L_0x0780:
        r12 = 1;
    L_0x0781:
        if (r0 != 0) goto L_0x078f;
    L_0x0783:
        r29 = r4;
        r4 = r1.drawInstantView;
        if (r4 != 0) goto L_0x0791;
    L_0x0789:
        if (r13 != 0) goto L_0x0791;
    L_0x078b:
        if (r12 == 0) goto L_0x0791;
    L_0x078d:
        r4 = 1;
        goto L_0x0792;
    L_0x078f:
        r29 = r4;
    L_0x0791:
        r4 = 0;
    L_0x0792:
        if (r0 != 0) goto L_0x07a8;
    L_0x0794:
        r0 = r1.drawInstantView;
        if (r0 != 0) goto L_0x07a8;
    L_0x0798:
        if (r13 != 0) goto L_0x07a8;
    L_0x079a:
        if (r10 == 0) goto L_0x07a8;
    L_0x079c:
        if (r2 == 0) goto L_0x07a8;
    L_0x079e:
        if (r12 == 0) goto L_0x07a8;
    L_0x07a0:
        r0 = r1.currentMessageObject;
        r0 = r0.photoThumbs;
        if (r0 == 0) goto L_0x07a8;
    L_0x07a6:
        r0 = 1;
        goto L_0x07a9;
    L_0x07a8:
        r0 = 0;
    L_0x07a9:
        r1.isSmallImage = r0;
        r12 = r4;
        r40 = r6;
        r6 = r2;
        goto L_0x0804;
    L_0x07b0:
        r29 = r4;
        r0 = r1.hasInvoicePreview;
        if (r0 == 0) goto L_0x07dd;
    L_0x07b6:
        r0 = r14.messageOwner;
        r0 = r0.media;
        r2 = r0;
        r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) r2;
        r7 = r0.title;
        r0 = r2.photo;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webDocument;
        if (r2 == 0) goto L_0x07cc;
    L_0x07c5:
        r0 = org.telegram.messenger.WebFile.createWithWebDocument(r0);
        r15 = r0;
        r2 = 0;
        goto L_0x07ce;
    L_0x07cc:
        r2 = 0;
        r15 = 0;
    L_0x07ce:
        r1.isSmallImage = r2;
        r2 = "invoice";
        r6 = r2;
        r2 = r15;
        r8 = 0;
        r9 = 0;
        r10 = 0;
        r12 = 0;
        r13 = 0;
        r15 = 0;
        r40 = 0;
        goto L_0x0805;
    L_0x07dd:
        r0 = r14.messageOwner;
        r0 = r0.media;
        r0 = r0.game;
        r7 = r0.title;
        r2 = r14.messageText;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 == 0) goto L_0x07f1;
    L_0x07ed:
        r2 = r0.description;
        r15 = r2;
        goto L_0x07f2;
    L_0x07f1:
        r15 = 0;
    L_0x07f2:
        r2 = r0.photo;
        r0 = r0.document;
        r4 = 0;
        r1.isSmallImage = r4;
        r4 = "game";
        r13 = r0;
        r6 = r4;
        r10 = r15;
        r8 = 0;
        r9 = 0;
        r12 = 0;
        r40 = 0;
        r15 = r2;
    L_0x0804:
        r2 = 0;
    L_0x0805:
        r0 = r1.drawInstantViewType;
        r4 = 6;
        if (r0 != r4) goto L_0x0813;
    L_0x080a:
        r0 = NUM; // 0x7f0d0281 float:1.8743415E38 double:1.053130094E-314;
        r4 = "ChatBackground";
        r7 = org.telegram.messenger.LocaleController.getString(r4, r0);
    L_0x0813:
        r0 = r1.hasInvoicePreview;
        if (r0 == 0) goto L_0x081b;
    L_0x0817:
        r41 = r2;
        r4 = 0;
        goto L_0x0822;
    L_0x081b:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r4 = r0;
        r41 = r2;
    L_0x0822:
        r2 = r29 - r4;
        r0 = r1.currentMessageObject;
        r29 = r6;
        r6 = r0.photoThumbs;
        if (r6 != 0) goto L_0x0832;
    L_0x082c:
        if (r15 == 0) goto L_0x0832;
    L_0x082e:
        r6 = 1;
        r0.generateThumbs(r6);
    L_0x0832:
        if (r7 == 0) goto L_0x08af;
    L_0x0834:
        r0 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x08a4 }
        r0 = r0.measureText(r7);	 Catch:{ Exception -> 0x08a4 }
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = r0 + r6;
        r42 = r5;
        r5 = (double) r0;
        r5 = java.lang.Math.ceil(r5);	 Catch:{ Exception -> 0x08a2 }
        r0 = (int) r5;	 Catch:{ Exception -> 0x08a2 }
        r5 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x08a2 }
        r32 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x08a2 }
        r33 = java.lang.Math.min(r0, r2);	 Catch:{ Exception -> 0x08a2 }
        r34 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x08a2 }
        r35 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r36 = 0;
        r37 = 0;
        r30 = r5;
        r31 = r7;
        r30.<init>(r31, r32, r33, r34, r35, r36, r37);	 Catch:{ Exception -> 0x08a2 }
        r1.siteNameLayout = r5;	 Catch:{ Exception -> 0x08a2 }
        r0 = r1.siteNameLayout;	 Catch:{ Exception -> 0x08a2 }
        r5 = 0;
        r0 = r0.getLineLeft(r5);	 Catch:{ Exception -> 0x08a2 }
        r22 = 0;
        r0 = (r0 > r22 ? 1 : (r0 == r22 ? 0 : -1));
        if (r0 == 0) goto L_0x086d;
    L_0x086b:
        r0 = 1;
        goto L_0x086e;
    L_0x086d:
        r0 = 0;
    L_0x086e:
        r1.siteNameRtl = r0;	 Catch:{ Exception -> 0x08a0 }
        r0 = r1.siteNameLayout;	 Catch:{ Exception -> 0x08a0 }
        r5 = r1.siteNameLayout;	 Catch:{ Exception -> 0x08a0 }
        r5 = r5.getLineCount();	 Catch:{ Exception -> 0x08a0 }
        r6 = 1;
        r5 = r5 - r6;
        r0 = r0.getLineBottom(r5);	 Catch:{ Exception -> 0x08a0 }
        r5 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x08a0 }
        r5 = r5 + r0;
        r1.linkPreviewHeight = r5;	 Catch:{ Exception -> 0x08a0 }
        r5 = r1.totalHeight;	 Catch:{ Exception -> 0x08a0 }
        r5 = r5 + r0;
        r1.totalHeight = r5;	 Catch:{ Exception -> 0x08a0 }
        r5 = 0;
        r6 = r0 + 0;
        r0 = r1.siteNameLayout;	 Catch:{ Exception -> 0x089e }
        r0 = r0.getWidth();	 Catch:{ Exception -> 0x089e }
        r1.siteNameWidth = r0;	 Catch:{ Exception -> 0x089e }
        r0 = r0 + r4;
        r3 = java.lang.Math.max(r3, r0);	 Catch:{ Exception -> 0x089e }
        r0 = java.lang.Math.max(r5, r0);	 Catch:{ Exception -> 0x089e }
        r5 = r0;
        goto L_0x08b5;
    L_0x089e:
        r0 = move-exception;
        goto L_0x08aa;
    L_0x08a0:
        r0 = move-exception;
        goto L_0x08a9;
    L_0x08a2:
        r0 = move-exception;
        goto L_0x08a7;
    L_0x08a4:
        r0 = move-exception;
        r42 = r5;
    L_0x08a7:
        r22 = 0;
    L_0x08a9:
        r6 = 0;
    L_0x08aa:
        org.telegram.messenger.FileLog.e(r0);
        r5 = 0;
        goto L_0x08b5;
    L_0x08af:
        r42 = r5;
        r22 = 0;
        r5 = 0;
        r6 = 0;
    L_0x08b5:
        if (r8 == 0) goto L_0x0a1f;
    L_0x08b7:
        r0 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r1.titleX = r0;	 Catch:{ Exception -> 0x09f2 }
        r0 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x09f2 }
        if (r0 == 0) goto L_0x08e7;
    L_0x08c0:
        r0 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x08d5 }
        r30 = org.telegram.messenger.AndroidUtilities.dp(r20);	 Catch:{ Exception -> 0x08d5 }
        r0 = r0 + r30;
        r1.linkPreviewHeight = r0;	 Catch:{ Exception -> 0x08d5 }
        r0 = r1.totalHeight;	 Catch:{ Exception -> 0x08d5 }
        r30 = org.telegram.messenger.AndroidUtilities.dp(r20);	 Catch:{ Exception -> 0x08d5 }
        r0 = r0 + r30;
        r1.totalHeight = r0;	 Catch:{ Exception -> 0x08d5 }
        goto L_0x08e7;
    L_0x08d5:
        r0 = move-exception;
        r38 = r6;
        r39 = r11;
        r44 = r13;
        r43 = r15;
        r30 = 3;
        r31 = 0;
        r6 = r5;
        r15 = r12;
        r5 = r3;
        goto L_0x0a08;
    L_0x08e7:
        r0 = r1.isSmallImage;	 Catch:{ Exception -> 0x09f2 }
        if (r0 == 0) goto L_0x0916;
    L_0x08eb:
        if (r10 != 0) goto L_0x08ee;
    L_0x08ed:
        goto L_0x0916;
    L_0x08ee:
        r31 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x08d5 }
        r0 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Exception -> 0x08d5 }
        r33 = r2 - r0;
        r35 = 4;
        r34 = 3;
        r30 = r8;
        r32 = r2;
        r0 = generateStaticLayout(r30, r31, r32, r33, r34, r35);	 Catch:{ Exception -> 0x08d5 }
        r1.titleLayout = r0;	 Catch:{ Exception -> 0x08d5 }
        r0 = r1.titleLayout;	 Catch:{ Exception -> 0x08d5 }
        r0 = r0.getLineCount();	 Catch:{ Exception -> 0x08d5 }
        r21 = 3;
        r0 = 3 - r0;
        r30 = r0;
        r31 = r3;
        r0 = 3;
        goto L_0x093c;
    L_0x0916:
        r31 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x09f2 }
        r33 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x09f2 }
        r34 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r30 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r30);	 Catch:{ Exception -> 0x09f2 }
        r0 = (float) r0;	 Catch:{ Exception -> 0x09f2 }
        r36 = 0;
        r37 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x09f2 }
        r39 = 4;
        r30 = r8;
        r32 = r2;
        r35 = r0;
        r38 = r2;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r30, r31, r32, r33, r34, r35, r36, r37, r38, r39);	 Catch:{ Exception -> 0x09f2 }
        r1.titleLayout = r0;	 Catch:{ Exception -> 0x09f2 }
        r31 = r3;
        r0 = 0;
        r30 = 3;
    L_0x093c:
        r3 = r1.titleLayout;	 Catch:{ Exception -> 0x09e1 }
        r32 = r5;
        r5 = r1.titleLayout;	 Catch:{ Exception -> 0x09df }
        r5 = r5.getLineCount();	 Catch:{ Exception -> 0x09df }
        r26 = 1;
        r5 = r5 + -1;
        r3 = r3.getLineBottom(r5);	 Catch:{ Exception -> 0x09df }
        r5 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x09df }
        r5 = r5 + r3;
        r1.linkPreviewHeight = r5;	 Catch:{ Exception -> 0x09df }
        r5 = r1.totalHeight;	 Catch:{ Exception -> 0x09df }
        r5 = r5 + r3;
        r1.totalHeight = r5;	 Catch:{ Exception -> 0x09df }
        r38 = r6;
        r39 = r11;
        r5 = r31;
        r6 = r32;
        r3 = 0;
        r31 = 0;
    L_0x0963:
        r11 = r1.titleLayout;	 Catch:{ Exception -> 0x09d8 }
        r11 = r11.getLineCount();	 Catch:{ Exception -> 0x09d8 }
        if (r3 >= r11) goto L_0x09d2;
    L_0x096b:
        r11 = r1.titleLayout;	 Catch:{ Exception -> 0x09d8 }
        r11 = r11.getLineLeft(r3);	 Catch:{ Exception -> 0x09d8 }
        r11 = (int) r11;
        if (r11 == 0) goto L_0x0979;
    L_0x0974:
        r43 = r15;
        r31 = 1;
        goto L_0x097b;
    L_0x0979:
        r43 = r15;
    L_0x097b:
        r15 = r1.titleX;	 Catch:{ Exception -> 0x09cd }
        r44 = r13;
        r13 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r15 != r13) goto L_0x0988;
    L_0x0984:
        r13 = -r11;
        r1.titleX = r13;	 Catch:{ Exception -> 0x09cb }
        goto L_0x0991;
    L_0x0988:
        r13 = r1.titleX;	 Catch:{ Exception -> 0x09cb }
        r15 = -r11;
        r13 = java.lang.Math.max(r13, r15);	 Catch:{ Exception -> 0x09cb }
        r1.titleX = r13;	 Catch:{ Exception -> 0x09cb }
    L_0x0991:
        if (r11 == 0) goto L_0x099c;
    L_0x0993:
        r13 = r1.titleLayout;	 Catch:{ Exception -> 0x09cb }
        r13 = r13.getWidth();	 Catch:{ Exception -> 0x09cb }
        r13 = r13 - r11;
        r15 = r12;
        goto L_0x09a9;
    L_0x099c:
        r13 = r1.titleLayout;	 Catch:{ Exception -> 0x09cb }
        r13 = r13.getLineWidth(r3);	 Catch:{ Exception -> 0x09cb }
        r15 = r12;
        r12 = (double) r13;
        r12 = java.lang.Math.ceil(r12);	 Catch:{ Exception -> 0x09c9 }
        r13 = (int) r12;	 Catch:{ Exception -> 0x09c9 }
    L_0x09a9:
        if (r3 < r0) goto L_0x09b1;
    L_0x09ab:
        if (r11 == 0) goto L_0x09b8;
    L_0x09ad:
        r11 = r1.isSmallImage;	 Catch:{ Exception -> 0x09c9 }
        if (r11 == 0) goto L_0x09b8;
    L_0x09b1:
        r11 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);	 Catch:{ Exception -> 0x09c9 }
        r13 = r13 + r11;
    L_0x09b8:
        r13 = r13 + r4;
        r5 = java.lang.Math.max(r5, r13);	 Catch:{ Exception -> 0x09c9 }
        r6 = java.lang.Math.max(r6, r13);	 Catch:{ Exception -> 0x09c9 }
        r3 = r3 + 1;
        r12 = r15;
        r15 = r43;
        r13 = r44;
        goto L_0x0963;
    L_0x09c9:
        r0 = move-exception;
        goto L_0x0a08;
    L_0x09cb:
        r0 = move-exception;
        goto L_0x09dd;
    L_0x09cd:
        r0 = move-exception;
        r15 = r12;
        r44 = r13;
        goto L_0x0a08;
    L_0x09d2:
        r44 = r13;
        r43 = r15;
        r15 = r12;
        goto L_0x0a0b;
    L_0x09d8:
        r0 = move-exception;
        r44 = r13;
        r43 = r15;
    L_0x09dd:
        r15 = r12;
        goto L_0x0a08;
    L_0x09df:
        r0 = move-exception;
        goto L_0x09e4;
    L_0x09e1:
        r0 = move-exception;
        r32 = r5;
    L_0x09e4:
        r38 = r6;
        r39 = r11;
        r44 = r13;
        r43 = r15;
        r15 = r12;
        r5 = r31;
        r6 = r32;
        goto L_0x0a06;
    L_0x09f2:
        r0 = move-exception;
        r31 = r3;
        r32 = r5;
        r38 = r6;
        r39 = r11;
        r44 = r13;
        r43 = r15;
        r15 = r12;
        r5 = r31;
        r6 = r32;
        r30 = 3;
    L_0x0a06:
        r31 = 0;
    L_0x0a08:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0a0b:
        r3 = r5;
        r5 = r6;
        r11 = r30;
        r12 = r31;
        if (r12 == 0) goto L_0x0a2e;
    L_0x0a13:
        r0 = r1.isSmallImage;
        if (r0 == 0) goto L_0x0a2e;
    L_0x0a17:
        r0 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = r2 - r0;
        goto L_0x0a2e;
    L_0x0a1f:
        r31 = r3;
        r32 = r5;
        r38 = r6;
        r39 = r11;
        r44 = r13;
        r43 = r15;
        r15 = r12;
        r11 = 3;
        r12 = 0;
    L_0x0a2e:
        if (r9 == 0) goto L_0x0ad6;
    L_0x0a30:
        if (r8 != 0) goto L_0x0ad6;
    L_0x0a32:
        r0 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0ad0 }
        if (r0 == 0) goto L_0x0a48;
    L_0x0a36:
        r0 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0ad0 }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r20);	 Catch:{ Exception -> 0x0ad0 }
        r0 = r0 + r6;
        r1.linkPreviewHeight = r0;	 Catch:{ Exception -> 0x0ad0 }
        r0 = r1.totalHeight;	 Catch:{ Exception -> 0x0ad0 }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r20);	 Catch:{ Exception -> 0x0ad0 }
        r0 = r0 + r6;
        r1.totalHeight = r0;	 Catch:{ Exception -> 0x0ad0 }
    L_0x0a48:
        r6 = 3;
        if (r11 != r6) goto L_0x0a69;
    L_0x0a4b:
        r0 = r1.isSmallImage;	 Catch:{ Exception -> 0x0ad0 }
        if (r0 == 0) goto L_0x0a51;
    L_0x0a4f:
        if (r10 != 0) goto L_0x0a69;
    L_0x0a51:
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0ad0 }
        r32 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0ad0 }
        r34 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0ad0 }
        r35 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r36 = 0;
        r37 = 0;
        r30 = r0;
        r31 = r9;
        r33 = r2;
        r30.<init>(r31, r32, r33, r34, r35, r36, r37);	 Catch:{ Exception -> 0x0ad0 }
        r1.authorLayout = r0;	 Catch:{ Exception -> 0x0ad0 }
        goto L_0x0a88;
    L_0x0a69:
        r31 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0ad0 }
        r0 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Exception -> 0x0ad0 }
        r33 = r2 - r0;
        r35 = 1;
        r30 = r9;
        r32 = r2;
        r34 = r11;
        r0 = generateStaticLayout(r30, r31, r32, r33, r34, r35);	 Catch:{ Exception -> 0x0ad0 }
        r1.authorLayout = r0;	 Catch:{ Exception -> 0x0ad0 }
        r0 = r1.authorLayout;	 Catch:{ Exception -> 0x0ad0 }
        r0 = r0.getLineCount();	 Catch:{ Exception -> 0x0ad0 }
        r11 = r11 - r0;
    L_0x0a88:
        r0 = r1.authorLayout;	 Catch:{ Exception -> 0x0ad0 }
        r6 = r1.authorLayout;	 Catch:{ Exception -> 0x0ad0 }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0ad0 }
        r8 = 1;
        r6 = r6 - r8;
        r0 = r0.getLineBottom(r6);	 Catch:{ Exception -> 0x0ad0 }
        r6 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0ad0 }
        r6 = r6 + r0;
        r1.linkPreviewHeight = r6;	 Catch:{ Exception -> 0x0ad0 }
        r6 = r1.totalHeight;	 Catch:{ Exception -> 0x0ad0 }
        r6 = r6 + r0;
        r1.totalHeight = r6;	 Catch:{ Exception -> 0x0ad0 }
        r0 = r1.authorLayout;	 Catch:{ Exception -> 0x0ad0 }
        r6 = 0;
        r0 = r0.getLineLeft(r6);	 Catch:{ Exception -> 0x0ad0 }
        r0 = (int) r0;	 Catch:{ Exception -> 0x0ad0 }
        r6 = -r0;
        r1.authorX = r6;	 Catch:{ Exception -> 0x0ad0 }
        if (r0 == 0) goto L_0x0ab6;
    L_0x0aad:
        r6 = r1.authorLayout;	 Catch:{ Exception -> 0x0ad0 }
        r6 = r6.getWidth();	 Catch:{ Exception -> 0x0ad0 }
        r6 = r6 - r0;
        r8 = 1;
        goto L_0x0ac4;
    L_0x0ab6:
        r0 = r1.authorLayout;	 Catch:{ Exception -> 0x0ad0 }
        r6 = 0;
        r0 = r0.getLineWidth(r6);	 Catch:{ Exception -> 0x0ad0 }
        r8 = (double) r0;	 Catch:{ Exception -> 0x0ad0 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0ad0 }
        r6 = (int) r8;
        r8 = 0;
    L_0x0ac4:
        r6 = r6 + r4;
        r3 = java.lang.Math.max(r3, r6);	 Catch:{ Exception -> 0x0ace }
        r5 = java.lang.Math.max(r5, r6);	 Catch:{ Exception -> 0x0ace }
        goto L_0x0ad7;
    L_0x0ace:
        r0 = move-exception;
        goto L_0x0ad2;
    L_0x0ad0:
        r0 = move-exception;
        r8 = 0;
    L_0x0ad2:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x0ad7;
    L_0x0ad6:
        r8 = 0;
    L_0x0ad7:
        if (r10 == 0) goto L_0x0c1f;
    L_0x0ad9:
        r6 = 0;
        r1.descriptionX = r6;	 Catch:{ Exception -> 0x0c1b }
        r0 = r1.currentMessageObject;	 Catch:{ Exception -> 0x0c1b }
        r0.generateLinkDescription();	 Catch:{ Exception -> 0x0c1b }
        r0 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0c1b }
        if (r0 == 0) goto L_0x0af7;
    L_0x0ae5:
        r0 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0c1b }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r20);	 Catch:{ Exception -> 0x0c1b }
        r0 = r0 + r6;
        r1.linkPreviewHeight = r0;	 Catch:{ Exception -> 0x0c1b }
        r0 = r1.totalHeight;	 Catch:{ Exception -> 0x0c1b }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r20);	 Catch:{ Exception -> 0x0c1b }
        r0 = r0 + r6;
        r1.totalHeight = r0;	 Catch:{ Exception -> 0x0c1b }
    L_0x0af7:
        if (r7 == 0) goto L_0x0b07;
    L_0x0af9:
        r0 = r7.toLowerCase();	 Catch:{ Exception -> 0x0c1b }
        r6 = "twitter";
        r0 = r0.equals(r6);	 Catch:{ Exception -> 0x0c1b }
        if (r0 == 0) goto L_0x0b07;
    L_0x0b05:
        r0 = 1;
        goto L_0x0b08;
    L_0x0b07:
        r0 = 0;
    L_0x0b08:
        r6 = 3;
        if (r11 != r6) goto L_0x0b3b;
    L_0x0b0b:
        r6 = r1.isSmallImage;	 Catch:{ Exception -> 0x0c1b }
        if (r6 != 0) goto L_0x0b3b;
    L_0x0b0f:
        r6 = r14.linkDescription;	 Catch:{ Exception -> 0x0c1b }
        r46 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0c1b }
        r48 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0c1b }
        r49 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Exception -> 0x0c1b }
        r9 = (float) r10;	 Catch:{ Exception -> 0x0c1b }
        r51 = 0;
        r52 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0c1b }
        if (r0 == 0) goto L_0x0b29;
    L_0x0b24:
        r0 = 100;
        r54 = 100;
        goto L_0x0b2b;
    L_0x0b29:
        r54 = 6;
    L_0x0b2b:
        r45 = r6;
        r47 = r2;
        r50 = r9;
        r53 = r2;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r45, r46, r47, r48, r49, r50, r51, r52, r53, r54);	 Catch:{ Exception -> 0x0c1b }
        r1.descriptionLayout = r0;	 Catch:{ Exception -> 0x0c1b }
        r11 = 0;
        goto L_0x0b5c;
    L_0x0b3b:
        r6 = r14.linkDescription;	 Catch:{ Exception -> 0x0c1b }
        r31 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x0c1b }
        r9 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Exception -> 0x0c1b }
        r33 = r2 - r9;
        if (r0 == 0) goto L_0x0b4e;
    L_0x0b49:
        r0 = 100;
        r35 = 100;
        goto L_0x0b50;
    L_0x0b4e:
        r35 = 6;
    L_0x0b50:
        r30 = r6;
        r32 = r2;
        r34 = r11;
        r0 = generateStaticLayout(r30, r31, r32, r33, r34, r35);	 Catch:{ Exception -> 0x0c1b }
        r1.descriptionLayout = r0;	 Catch:{ Exception -> 0x0c1b }
    L_0x0b5c:
        r0 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0c1b }
        r6 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0c1b }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0c1b }
        r9 = 1;
        r6 = r6 - r9;
        r0 = r0.getLineBottom(r6);	 Catch:{ Exception -> 0x0c1b }
        r6 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x0c1b }
        r6 = r6 + r0;
        r1.linkPreviewHeight = r6;	 Catch:{ Exception -> 0x0c1b }
        r6 = r1.totalHeight;	 Catch:{ Exception -> 0x0c1b }
        r6 = r6 + r0;
        r1.totalHeight = r6;	 Catch:{ Exception -> 0x0c1b }
        r0 = 0;
        r13 = 0;
    L_0x0b76:
        r6 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0c1b }
        r6 = r6.getLineCount();	 Catch:{ Exception -> 0x0c1b }
        if (r0 >= r6) goto L_0x0ba1;
    L_0x0b7e:
        r6 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0c1b }
        r6 = r6.getLineLeft(r0);	 Catch:{ Exception -> 0x0c1b }
        r9 = (double) r6;	 Catch:{ Exception -> 0x0c1b }
        r9 = java.lang.Math.ceil(r9);	 Catch:{ Exception -> 0x0c1b }
        r6 = (int) r9;	 Catch:{ Exception -> 0x0c1b }
        if (r6 == 0) goto L_0x0b9e;
    L_0x0b8c:
        r9 = r1.descriptionX;	 Catch:{ Exception -> 0x0c1b }
        if (r9 != 0) goto L_0x0b94;
    L_0x0b90:
        r6 = -r6;
        r1.descriptionX = r6;	 Catch:{ Exception -> 0x0c1b }
        goto L_0x0b9d;
    L_0x0b94:
        r9 = r1.descriptionX;	 Catch:{ Exception -> 0x0c1b }
        r6 = -r6;
        r6 = java.lang.Math.max(r9, r6);	 Catch:{ Exception -> 0x0c1b }
        r1.descriptionX = r6;	 Catch:{ Exception -> 0x0c1b }
    L_0x0b9d:
        r13 = 1;
    L_0x0b9e:
        r0 = r0 + 1;
        goto L_0x0b76;
    L_0x0ba1:
        r0 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0c1b }
        r0 = r0.getWidth();	 Catch:{ Exception -> 0x0c1b }
        r6 = r5;
        r5 = r3;
        r3 = 0;
    L_0x0baa:
        r9 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0CLASSNAME }
        r9 = r9.getLineCount();	 Catch:{ Exception -> 0x0CLASSNAME }
        if (r3 >= r9) goto L_0x0CLASSNAME;
    L_0x0bb2:
        r9 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0CLASSNAME }
        r9 = r9.getLineLeft(r3);	 Catch:{ Exception -> 0x0CLASSNAME }
        r9 = (double) r9;	 Catch:{ Exception -> 0x0CLASSNAME }
        r9 = java.lang.Math.ceil(r9);	 Catch:{ Exception -> 0x0CLASSNAME }
        r9 = (int) r9;	 Catch:{ Exception -> 0x0CLASSNAME }
        if (r9 != 0) goto L_0x0bc7;
    L_0x0bc0:
        r10 = r1.descriptionX;	 Catch:{ Exception -> 0x0CLASSNAME }
        if (r10 == 0) goto L_0x0bc7;
    L_0x0bc4:
        r10 = 0;
        r1.descriptionX = r10;	 Catch:{ Exception -> 0x0CLASSNAME }
    L_0x0bc7:
        if (r9 == 0) goto L_0x0bce;
    L_0x0bc9:
        r10 = r0 - r9;
    L_0x0bcb:
        r30 = r13;
        goto L_0x0be4;
    L_0x0bce:
        if (r13 == 0) goto L_0x0bd2;
    L_0x0bd0:
        r10 = r0;
        goto L_0x0bcb;
    L_0x0bd2:
        r10 = r1.descriptionLayout;	 Catch:{ Exception -> 0x0CLASSNAME }
        r10 = r10.getLineWidth(r3);	 Catch:{ Exception -> 0x0CLASSNAME }
        r30 = r13;
        r13 = (double) r10;	 Catch:{ Exception -> 0x0CLASSNAME }
        r13 = java.lang.Math.ceil(r13);	 Catch:{ Exception -> 0x0CLASSNAME }
        r10 = (int) r13;	 Catch:{ Exception -> 0x0CLASSNAME }
        r10 = java.lang.Math.min(r10, r0);	 Catch:{ Exception -> 0x0CLASSNAME }
    L_0x0be4:
        if (r3 < r11) goto L_0x0bee;
    L_0x0be6:
        if (r11 == 0) goto L_0x0bf5;
    L_0x0be8:
        if (r9 == 0) goto L_0x0bf5;
    L_0x0bea:
        r9 = r1.isSmallImage;	 Catch:{ Exception -> 0x0CLASSNAME }
        if (r9 == 0) goto L_0x0bf5;
    L_0x0bee:
        r9 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);	 Catch:{ Exception -> 0x0CLASSNAME }
        r10 = r10 + r9;
    L_0x0bf5:
        r10 = r10 + r4;
        if (r6 >= r10) goto L_0x0c0b;
    L_0x0bf8:
        if (r12 == 0) goto L_0x0CLASSNAME;
    L_0x0bfa:
        r9 = r1.titleX;	 Catch:{ Exception -> 0x0CLASSNAME }
        r13 = r10 - r6;
        r9 = r9 + r13;
        r1.titleX = r9;	 Catch:{ Exception -> 0x0CLASSNAME }
    L_0x0CLASSNAME:
        if (r8 == 0) goto L_0x0c0a;
    L_0x0CLASSNAME:
        r9 = r1.authorX;	 Catch:{ Exception -> 0x0CLASSNAME }
        r6 = r10 - r6;
        r9 = r9 + r6;
        r1.authorX = r9;	 Catch:{ Exception -> 0x0CLASSNAME }
    L_0x0c0a:
        r6 = r10;
    L_0x0c0b:
        r5 = java.lang.Math.max(r5, r10);	 Catch:{ Exception -> 0x0CLASSNAME }
        r3 = r3 + 1;
        r14 = r59;
        r13 = r30;
        goto L_0x0baa;
    L_0x0CLASSNAME:
        r3 = r5;
        goto L_0x0c1f;
    L_0x0CLASSNAME:
        r0 = move-exception;
        r3 = r5;
        goto L_0x0c1c;
    L_0x0c1b:
        r0 = move-exception;
    L_0x0c1c:
        org.telegram.messenger.FileLog.e(r0);
    L_0x0c1f:
        if (r15 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = r1.descriptionLayout;
        if (r0 == 0) goto L_0x0c2e;
    L_0x0CLASSNAME:
        if (r0 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = r0.getLineCount();
        r5 = 1;
        if (r0 != r5) goto L_0x0CLASSNAME;
    L_0x0c2e:
        r5 = 0;
        r1.isSmallImage = r5;
        r15 = 0;
    L_0x0CLASSNAME:
        if (r15 == 0) goto L_0x0c3a;
    L_0x0CLASSNAME:
        r0 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r0);
    L_0x0c3a:
        if (r44 == 0) goto L_0x10c8;
    L_0x0c3c:
        r0 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r44);
        if (r0 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r13 = r44;
        r0 = r13.thumbs;
        r5 = 90;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5);
        r1.currentPhotoObject = r0;
        r1.photoParentObject = r13;
        r1.documentAttach = r13;
        r0 = 7;
        r1.documentAttachType = r0;
        r14 = r59;
    L_0x0CLASSNAME:
        r8 = r29;
        r11 = r39;
        r9 = r41;
        r6 = r42;
        r5 = r43;
        goto L_0x112d;
    L_0x0CLASSNAME:
        r13 = r44;
        r0 = org.telegram.messenger.MessageObject.isGifDocument(r13);
        if (r0 == 0) goto L_0x0cea;
    L_0x0c6b:
        r0 = r59.isGame();
        if (r0 != 0) goto L_0x0c7c;
    L_0x0CLASSNAME:
        r0 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r0 != 0) goto L_0x0c7c;
    L_0x0CLASSNAME:
        r14 = r59;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r14.gifState = r8;
        goto L_0x0CLASSNAME;
    L_0x0c7c:
        r14 = r59;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x0CLASSNAME:
        r0 = r1.photoImage;
        r5 = r14.gifState;
        r5 = (r5 > r8 ? 1 : (r5 == r8 ? 0 : -1));
        if (r5 == 0) goto L_0x0c8a;
    L_0x0CLASSNAME:
        r5 = 1;
        goto L_0x0c8b;
    L_0x0c8a:
        r5 = 0;
    L_0x0c8b:
        r0.setAllowStartAnimation(r5);
        r0 = r13.thumbs;
        r5 = 90;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r5);
        r1.currentPhotoObject = r0;
        r1.photoParentObject = r13;
        r0 = r1.currentPhotoObject;
        if (r0 == 0) goto L_0x0ce3;
    L_0x0c9e:
        r5 = r0.w;
        if (r5 == 0) goto L_0x0ca6;
    L_0x0ca2:
        r0 = r0.h;
        if (r0 != 0) goto L_0x0ce3;
    L_0x0ca6:
        r0 = 0;
    L_0x0ca7:
        r5 = r13.attributes;
        r5 = r5.size();
        if (r0 >= r5) goto L_0x0ccd;
    L_0x0caf:
        r5 = r13.attributes;
        r5 = r5.get(r0);
        r5 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r5;
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r6 != 0) goto L_0x0cc3;
    L_0x0cbb:
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r6 == 0) goto L_0x0cc0;
    L_0x0cbf:
        goto L_0x0cc3;
    L_0x0cc0:
        r0 = r0 + 1;
        goto L_0x0ca7;
    L_0x0cc3:
        r0 = r1.currentPhotoObject;
        r6 = r5.w;
        r0.w = r6;
        r5 = r5.h;
        r0.h = r5;
    L_0x0ccd:
        r0 = r1.currentPhotoObject;
        r5 = r0.w;
        if (r5 == 0) goto L_0x0cd7;
    L_0x0cd3:
        r0 = r0.h;
        if (r0 != 0) goto L_0x0ce3;
    L_0x0cd7:
        r0 = r1.currentPhotoObject;
        r5 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0.h = r5;
        r0.w = r5;
    L_0x0ce3:
        r1.documentAttach = r13;
        r5 = 2;
        r1.documentAttachType = r5;
        goto L_0x0CLASSNAME;
    L_0x0cea:
        r14 = r59;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = org.telegram.messenger.MessageObject.isVideoDocument(r13);
        if (r0 == 0) goto L_0x0dbd;
    L_0x0cf4:
        if (r43 == 0) goto L_0x0d12;
    L_0x0cf6:
        r5 = r43;
        r0 = r5.sizes;
        r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r9 = 1;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r6, r9);
        r1.currentPhotoObject = r0;
        r0 = r5.sizes;
        r6 = 40;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r6);
        r1.currentPhotoObjectThumb = r0;
        r1.photoParentObject = r5;
        goto L_0x0d14;
    L_0x0d12:
        r5 = r43;
    L_0x0d14:
        r0 = r1.currentPhotoObject;
        if (r0 != 0) goto L_0x0d2e;
    L_0x0d18:
        r0 = r13.thumbs;
        r6 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r6);
        r1.currentPhotoObject = r0;
        r0 = r13.thumbs;
        r6 = 40;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r6);
        r1.currentPhotoObjectThumb = r0;
        r1.photoParentObject = r13;
    L_0x0d2e:
        r0 = r1.currentPhotoObject;
        r6 = r1.currentPhotoObjectThumb;
        if (r0 != r6) goto L_0x0d37;
    L_0x0d34:
        r6 = 0;
        r1.currentPhotoObjectThumb = r6;
    L_0x0d37:
        r0 = r1.currentPhotoObject;
        if (r0 != 0) goto L_0x0d4f;
    L_0x0d3b:
        r0 = new org.telegram.tgnet.TLRPC$TL_photoSize;
        r0.<init>();
        r1.currentPhotoObject = r0;
        r0 = r1.currentPhotoObject;
        r6 = "s";
        r0.type = r6;
        r6 = new org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
        r6.<init>();
        r0.location = r6;
    L_0x0d4f:
        r0 = r1.currentPhotoObject;
        if (r0 == 0) goto L_0x0db7;
    L_0x0d53:
        r6 = r0.w;
        if (r6 == 0) goto L_0x0d5f;
    L_0x0d57:
        r6 = r0.h;
        if (r6 == 0) goto L_0x0d5f;
    L_0x0d5b:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r0 == 0) goto L_0x0db7;
    L_0x0d5f:
        r0 = 0;
    L_0x0d60:
        r6 = r13.attributes;
        r6 = r6.size();
        if (r0 >= r6) goto L_0x0da1;
    L_0x0d68:
        r6 = r13.attributes;
        r6 = r6.get(r0);
        r6 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r6;
        r9 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r9 == 0) goto L_0x0d9e;
    L_0x0d74:
        r0 = r1.currentPhotoObject;
        r9 = r0 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r9 == 0) goto L_0x0d95;
    L_0x0d7a:
        r0 = r6.w;
        r0 = java.lang.Math.max(r0, r0);
        r0 = (float) r0;
        r9 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r0 = r0 / r9;
        r9 = r1.currentPhotoObject;
        r10 = r6.w;
        r10 = (float) r10;
        r10 = r10 / r0;
        r10 = (int) r10;
        r9.w = r10;
        r6 = r6.h;
        r6 = (float) r6;
        r6 = r6 / r0;
        r0 = (int) r6;
        r9.h = r0;
        goto L_0x0da1;
    L_0x0d95:
        r9 = r6.w;
        r0.w = r9;
        r6 = r6.h;
        r0.h = r6;
        goto L_0x0da1;
    L_0x0d9e:
        r0 = r0 + 1;
        goto L_0x0d60;
    L_0x0da1:
        r0 = r1.currentPhotoObject;
        r6 = r0.w;
        if (r6 == 0) goto L_0x0dab;
    L_0x0da7:
        r0 = r0.h;
        if (r0 != 0) goto L_0x0db7;
    L_0x0dab:
        r0 = r1.currentPhotoObject;
        r6 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0.h = r6;
        r0.w = r6;
    L_0x0db7:
        r6 = 0;
        r1.createDocumentLayout(r6, r14);
        goto L_0x0e59;
    L_0x0dbd:
        r5 = r43;
        r0 = org.telegram.messenger.MessageObject.isStickerDocument(r13);
        if (r0 != 0) goto L_0x106a;
    L_0x0dc5:
        r0 = org.telegram.messenger.MessageObject.isAnimatedStickerDocument(r13);
        if (r0 == 0) goto L_0x0dcd;
    L_0x0dcb:
        goto L_0x106a;
    L_0x0dcd:
        r0 = r1.drawInstantViewType;
        r6 = 6;
        if (r0 != r6) goto L_0x0e63;
    L_0x0dd2:
        r0 = r13.thumbs;
        r6 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r6);
        r1.currentPhotoObject = r0;
        r1.photoParentObject = r13;
        r0 = r1.currentPhotoObject;
        if (r0 == 0) goto L_0x0e23;
    L_0x0de2:
        r6 = r0.w;
        if (r6 == 0) goto L_0x0dea;
    L_0x0de6:
        r0 = r0.h;
        if (r0 != 0) goto L_0x0e23;
    L_0x0dea:
        r0 = 0;
    L_0x0deb:
        r6 = r13.attributes;
        r6 = r6.size();
        if (r0 >= r6) goto L_0x0e0d;
    L_0x0df3:
        r6 = r13.attributes;
        r6 = r6.get(r0);
        r6 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r6;
        r9 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r9 == 0) goto L_0x0e0a;
    L_0x0dff:
        r0 = r1.currentPhotoObject;
        r9 = r6.w;
        r0.w = r9;
        r6 = r6.h;
        r0.h = r6;
        goto L_0x0e0d;
    L_0x0e0a:
        r0 = r0 + 1;
        goto L_0x0deb;
    L_0x0e0d:
        r0 = r1.currentPhotoObject;
        r6 = r0.w;
        if (r6 == 0) goto L_0x0e17;
    L_0x0e13:
        r0 = r0.h;
        if (r0 != 0) goto L_0x0e23;
    L_0x0e17:
        r0 = r1.currentPhotoObject;
        r6 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r0.h = r6;
        r0.w = r6;
    L_0x0e23:
        r1.documentAttach = r13;
        r6 = 8;
        r1.documentAttachType = r6;
        r0 = r1.documentAttach;
        r0 = r0.size;
        r9 = (long) r0;
        r0 = org.telegram.messenger.AndroidUtilities.formatFileSize(r9);
        r6 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r6 = r6.measureText(r0);
        r9 = (double) r6;
        r9 = java.lang.Math.ceil(r9);
        r6 = (int) r9;
        r1.durationWidth = r6;
        r6 = new android.text.StaticLayout;
        r32 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r9 = r1.durationWidth;
        r34 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r35 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r36 = 0;
        r37 = 0;
        r30 = r6;
        r31 = r0;
        r33 = r9;
        r30.<init>(r31, r32, r33, r34, r35, r36, r37);
        r1.videoInfoLayout = r6;
    L_0x0e59:
        r8 = r29;
        r11 = r39;
        r9 = r41;
        r6 = r42;
        goto L_0x112d;
    L_0x0e63:
        r11 = r39;
        r6 = r42;
        r1.calcBackgroundWidth(r6, r11, r3);
        r0 = r1.backgroundWidth;
        r9 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = r9 + r6;
        if (r0 >= r9) goto L_0x0e7e;
    L_0x0e75:
        r0 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = r0 + r6;
        r1.backgroundWidth = r0;
    L_0x0e7e:
        r0 = org.telegram.messenger.MessageObject.isVoiceDocument(r13);
        if (r0 == 0) goto L_0x0f2c;
    L_0x0e84:
        r0 = r1.backgroundWidth;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r0 = r0 - r9;
        r1.createDocumentLayout(r0, r14);
        r0 = r1.currentMessageObject;
        r0 = r0.textHeight;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r0 = r0 + r9;
        r9 = r1.linkPreviewHeight;
        r0 = r0 + r9;
        r1.mediaOffsetY = r0;
        r0 = r1.totalHeight;
        r9 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r0 + r9;
        r1.totalHeight = r0;
        r0 = r1.linkPreviewHeight;
        r9 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r0 + r9;
        r1.linkPreviewHeight = r0;
        r0 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = r6 - r0;
        r6 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r6 == 0) goto L_0x0ef4;
    L_0x0ec0:
        r6 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r9 = r1.isChat;
        if (r9 == 0) goto L_0x0ed7;
    L_0x0ec8:
        r9 = r59.needDrawAvatar();
        if (r9 == 0) goto L_0x0ed7;
    L_0x0ece:
        r9 = r59.isOutOwner();
        if (r9 != 0) goto L_0x0ed7;
    L_0x0ed4:
        r10 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        goto L_0x0ed8;
    L_0x0ed7:
        r10 = 0;
    L_0x0ed8:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r6 = r6 - r9;
        r9 = NUM; // 0x435CLASSNAME float:220.0 double:5.58344962E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = java.lang.Math.min(r6, r9);
        r9 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = r6 - r9;
        r6 = r6 + r4;
        r3 = java.lang.Math.max(r3, r6);
        goto L_0x0var_;
    L_0x0ef4:
        r6 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r6.x;
        r9 = r1.isChat;
        if (r9 == 0) goto L_0x0f0b;
    L_0x0efc:
        r9 = r59.needDrawAvatar();
        if (r9 == 0) goto L_0x0f0b;
    L_0x0var_:
        r9 = r59.isOutOwner();
        if (r9 != 0) goto L_0x0f0b;
    L_0x0var_:
        r10 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        goto L_0x0f0c;
    L_0x0f0b:
        r10 = 0;
    L_0x0f0c:
        r9 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r6 = r6 - r9;
        r9 = NUM; // 0x435CLASSNAME float:220.0 double:5.58344962E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = java.lang.Math.min(r6, r9);
        r9 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = r6 - r9;
        r6 = r6 + r4;
        r3 = java.lang.Math.max(r3, r6);
    L_0x0var_:
        r1.calcBackgroundWidth(r0, r11, r3);
        goto L_0x0fbb;
    L_0x0f2c:
        r0 = org.telegram.messenger.MessageObject.isMusicDocument(r13);
        if (r0 == 0) goto L_0x0fc1;
    L_0x0var_:
        r0 = r1.backgroundWidth;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r0 = r0 - r9;
        r0 = r1.createDocumentLayout(r0, r14);
        r9 = r1.currentMessageObject;
        r9 = r9.textHeight;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r9 = r9 + r10;
        r10 = r1.linkPreviewHeight;
        r9 = r9 + r10;
        r1.mediaOffsetY = r9;
        r9 = r1.totalHeight;
        r10 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r9 = r9 + r10;
        r1.totalHeight = r9;
        r9 = r1.linkPreviewHeight;
        r10 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r9 = r9 + r10;
        r1.linkPreviewHeight = r9;
        r9 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = r6 - r9;
        r0 = r0 + r4;
        r9 = NUM; // 0x42bCLASSNAME float:94.0 double:5.53164308E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r0 + r9;
        r0 = java.lang.Math.max(r3, r0);
        r3 = r1.songLayout;
        if (r3 == 0) goto L_0x0var_;
    L_0x0var_:
        r3 = r3.getLineCount();
        if (r3 <= 0) goto L_0x0var_;
    L_0x0f7e:
        r0 = (float) r0;
        r3 = r1.songLayout;
        r9 = 0;
        r3 = r3.getLineWidth(r9);
        r9 = (float) r4;
        r3 = r3 + r9;
        r9 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (float) r9;
        r3 = r3 + r9;
        r0 = java.lang.Math.max(r0, r3);
        r0 = (int) r0;
    L_0x0var_:
        r3 = r1.performerLayout;
        if (r3 == 0) goto L_0x0fb6;
    L_0x0var_:
        r3 = r3.getLineCount();
        if (r3 <= 0) goto L_0x0fb6;
    L_0x0f9f:
        r0 = (float) r0;
        r3 = r1.performerLayout;
        r9 = 0;
        r3 = r3.getLineWidth(r9);
        r9 = (float) r4;
        r3 = r3 + r9;
        r9 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = (float) r9;
        r3 = r3 + r9;
        r0 = java.lang.Math.max(r0, r3);
        r0 = (int) r0;
    L_0x0fb6:
        r3 = r0;
        r1.calcBackgroundWidth(r6, r11, r3);
        r0 = r6;
    L_0x0fbb:
        r8 = r29;
        r9 = r41;
        goto L_0x112e;
    L_0x0fc1:
        r0 = r1.backgroundWidth;
        r9 = NUM; // 0x43280000 float:168.0 double:5.566612494E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r0 - r9;
        r1.createDocumentLayout(r0, r14);
        r9 = 1;
        r1.drawImageButton = r9;
        r0 = r1.drawPhotoImage;
        if (r0 == 0) goto L_0x1003;
    L_0x0fd4:
        r0 = r1.totalHeight;
        r9 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r0 + r9;
        r1.totalHeight = r0;
        r0 = r1.linkPreviewHeight;
        r9 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r0 + r9;
        r1.linkPreviewHeight = r0;
        r0 = r1.photoImage;
        r9 = r1.totalHeight;
        r10 = r1.namesOffset;
        r9 = r9 + r10;
        r10 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r12 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r8 = 0;
        r0.setImageCoords(r8, r9, r10, r12);
        goto L_0x10c4;
    L_0x1003:
        r0 = r1.currentMessageObject;
        r0 = r0.textHeight;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r0 = r0 + r8;
        r8 = r1.linkPreviewHeight;
        r0 = r0 + r8;
        r1.mediaOffsetY = r0;
        r0 = r1.photoImage;
        r8 = r1.totalHeight;
        r9 = r1.namesOffset;
        r8 = r8 + r9;
        r9 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r8 = r8 - r9;
        r9 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r12 = 0;
        r0.setImageCoords(r12, r8, r9, r10);
        r0 = r1.totalHeight;
        r8 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 + r8;
        r1.totalHeight = r0;
        r0 = r1.linkPreviewHeight;
        r8 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 + r9;
        r1.linkPreviewHeight = r0;
        r0 = r1.docTitleLayout;
        if (r0 == 0) goto L_0x10c4;
    L_0x1049:
        r0 = r0.getLineCount();
        r8 = 1;
        if (r0 <= r8) goto L_0x10c4;
    L_0x1050:
        r0 = r1.docTitleLayout;
        r0 = r0.getLineCount();
        r0 = r0 - r8;
        r8 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0 = r0 * r8;
        r8 = r1.totalHeight;
        r8 = r8 + r0;
        r1.totalHeight = r8;
        r8 = r1.linkPreviewHeight;
        r8 = r8 + r0;
        r1.linkPreviewHeight = r8;
        goto L_0x10c4;
    L_0x106a:
        r11 = r39;
        r6 = r42;
        r0 = r13.thumbs;
        r8 = 90;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r8);
        r1.currentPhotoObject = r0;
        r1.photoParentObject = r13;
        r0 = r1.currentPhotoObject;
        if (r0 == 0) goto L_0x10bf;
    L_0x107e:
        r8 = r0.w;
        if (r8 == 0) goto L_0x1086;
    L_0x1082:
        r0 = r0.h;
        if (r0 != 0) goto L_0x10bf;
    L_0x1086:
        r0 = 0;
    L_0x1087:
        r8 = r13.attributes;
        r8 = r8.size();
        if (r0 >= r8) goto L_0x10a9;
    L_0x108f:
        r8 = r13.attributes;
        r8 = r8.get(r0);
        r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8;
        r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r9 == 0) goto L_0x10a6;
    L_0x109b:
        r0 = r1.currentPhotoObject;
        r9 = r8.w;
        r0.w = r9;
        r8 = r8.h;
        r0.h = r8;
        goto L_0x10a9;
    L_0x10a6:
        r0 = r0 + 1;
        goto L_0x1087;
    L_0x10a9:
        r0 = r1.currentPhotoObject;
        r8 = r0.w;
        if (r8 == 0) goto L_0x10b3;
    L_0x10af:
        r0 = r0.h;
        if (r0 != 0) goto L_0x10bf;
    L_0x10b3:
        r0 = r1.currentPhotoObject;
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r0.h = r8;
        r0.w = r8;
    L_0x10bf:
        r1.documentAttach = r13;
        r8 = 6;
        r1.documentAttachType = r8;
    L_0x10c4:
        r8 = r29;
        goto L_0x112b;
    L_0x10c8:
        r14 = r59;
        r11 = r39;
        r6 = r42;
        r5 = r43;
        r13 = r44;
        if (r5 == 0) goto L_0x1116;
    L_0x10d4:
        if (r29 == 0) goto L_0x10e2;
    L_0x10d6:
        r0 = "photo";
        r8 = r29;
        r0 = r8.equals(r0);
        if (r0 == 0) goto L_0x10e4;
    L_0x10e0:
        r0 = 1;
        goto L_0x10e5;
    L_0x10e2:
        r8 = r29;
    L_0x10e4:
        r0 = 0;
    L_0x10e5:
        r9 = r14.photoThumbs;
        if (r0 != 0) goto L_0x10ee;
    L_0x10e9:
        if (r15 != 0) goto L_0x10ec;
    L_0x10eb:
        goto L_0x10ee;
    L_0x10ec:
        r10 = r2;
        goto L_0x10f2;
    L_0x10ee:
        r10 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x10f2:
        r12 = r0 ^ 1;
        r9 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r9, r10, r12);
        r1.currentPhotoObject = r9;
        r9 = r14.photoThumbsObject;
        r1.photoParentObject = r9;
        r9 = 1;
        r0 = r0 ^ r9;
        r1.checkOnlyButtonPressed = r0;
        r0 = r14.photoThumbs;
        r9 = 40;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r9);
        r1.currentPhotoObjectThumb = r0;
        r0 = r1.currentPhotoObjectThumb;
        r9 = r1.currentPhotoObject;
        if (r0 != r9) goto L_0x112b;
    L_0x1112:
        r9 = 0;
        r1.currentPhotoObjectThumb = r9;
        goto L_0x112b;
    L_0x1116:
        r8 = r29;
        if (r41 == 0) goto L_0x112b;
    L_0x111a:
        r9 = r41;
        r0 = r9.mime_type;
        r10 = "image/";
        r0 = r0.startsWith(r10);
        if (r0 != 0) goto L_0x1127;
    L_0x1126:
        r9 = 0;
    L_0x1127:
        r10 = 0;
        r1.drawImageButton = r10;
        goto L_0x112d;
    L_0x112b:
        r9 = r41;
    L_0x112d:
        r0 = r6;
    L_0x112e:
        r6 = r1.documentAttachType;
        r10 = 5;
        if (r6 == r10) goto L_0x17b1;
    L_0x1133:
        r10 = 3;
        if (r6 == r10) goto L_0x17b1;
    L_0x1136:
        r10 = 1;
        if (r6 == r10) goto L_0x17b1;
    L_0x1139:
        r6 = r1.currentPhotoObject;
        if (r6 != 0) goto L_0x1165;
    L_0x113d:
        if (r9 == 0) goto L_0x1140;
    L_0x113f:
        goto L_0x1165;
    L_0x1140:
        r2 = r1.photoImage;
        r4 = 0;
        r2.setImageBitmap(r4);
        r2 = r1.linkPreviewHeight;
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r4;
        r1.linkPreviewHeight = r2;
        r2 = r1.totalHeight;
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r1.totalHeight = r2;
        r55 = r11;
        r61 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = 0;
        r13 = 2;
        r15 = 1;
        goto L_0x16a7;
    L_0x1165:
        if (r5 == 0) goto L_0x1169;
    L_0x1167:
        if (r15 == 0) goto L_0x1191;
    L_0x1169:
        if (r8 == 0) goto L_0x1193;
    L_0x116b:
        r5 = "photo";
        r5 = r8.equals(r5);
        if (r5 != 0) goto L_0x1191;
    L_0x1173:
        r5 = "document";
        r5 = r8.equals(r5);
        if (r5 == 0) goto L_0x1180;
    L_0x117b:
        r5 = r1.documentAttachType;
        r6 = 6;
        if (r5 != r6) goto L_0x1191;
    L_0x1180:
        r5 = "gif";
        r5 = r8.equals(r5);
        if (r5 != 0) goto L_0x1191;
    L_0x1188:
        r5 = r1.documentAttachType;
        r6 = 4;
        if (r5 == r6) goto L_0x1191;
    L_0x118d:
        r6 = 8;
        if (r5 != r6) goto L_0x1193;
    L_0x1191:
        r5 = 1;
        goto L_0x1194;
    L_0x1193:
        r5 = 0;
    L_0x1194:
        r1.drawImageButton = r5;
        r5 = r1.linkPreviewHeight;
        if (r5 == 0) goto L_0x11aa;
    L_0x119a:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r5 = r5 + r6;
        r1.linkPreviewHeight = r5;
        r5 = r1.totalHeight;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r5 = r5 + r6;
        r1.totalHeight = r5;
    L_0x11aa:
        r5 = r1.imageBackgroundSideColor;
        if (r5 == 0) goto L_0x11b5;
    L_0x11ae:
        r2 = NUM; // 0x43500000 float:208.0 double:5.57956413E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x11ec;
    L_0x11b5:
        r5 = r1.currentPhotoObject;
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
        if (r6 == 0) goto L_0x11c1;
    L_0x11bb:
        r5 = r5.w;
        if (r5 == 0) goto L_0x11c1;
    L_0x11bf:
        r2 = r5;
        goto L_0x11ec;
    L_0x11c1:
        r5 = r1.documentAttachType;
        r6 = 6;
        if (r5 == r6) goto L_0x11d7;
    L_0x11c6:
        r6 = 8;
        if (r5 != r6) goto L_0x11cb;
    L_0x11ca:
        goto L_0x11d7;
    L_0x11cb:
        r6 = 7;
        if (r5 != r6) goto L_0x11ec;
    L_0x11ce:
        r2 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r5 = r1.photoImage;
        r6 = 1;
        r5.setAllowDecodeSingleFrame(r6);
        goto L_0x11ec;
    L_0x11d7:
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x11e2;
    L_0x11dd:
        r2 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        goto L_0x11e6;
    L_0x11e2:
        r2 = org.telegram.messenger.AndroidUtilities.displaySize;
        r2 = r2.x;
    L_0x11e6:
        r2 = (float) r2;
        r5 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r2 = r2 * r5;
        r2 = (int) r2;
    L_0x11ec:
        r5 = r1.hasInvoicePreview;
        if (r5 == 0) goto L_0x11f7;
    L_0x11f0:
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r5);
        goto L_0x11f8;
    L_0x11f7:
        r12 = 0;
    L_0x11f8:
        r5 = r2 - r12;
        r5 = r5 + r4;
        r29 = java.lang.Math.max(r3, r5);
        r3 = r1.currentPhotoObject;
        if (r3 == 0) goto L_0x120d;
    L_0x1203:
        r10 = -1;
        r3.size = r10;
        r3 = r1.currentPhotoObjectThumb;
        if (r3 == 0) goto L_0x1210;
    L_0x120a:
        r3.size = r10;
        goto L_0x1210;
    L_0x120d:
        r10 = -1;
        r9.size = r10;
    L_0x1210:
        r3 = r1.imageBackgroundSideColor;
        if (r3 == 0) goto L_0x121e;
    L_0x1214:
        r3 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r29 - r3;
        r1.imageBackgroundSideWidth = r3;
    L_0x121e:
        if (r15 != 0) goto L_0x12a6;
    L_0x1220:
        r3 = r1.documentAttachType;
        r4 = 7;
        if (r3 != r4) goto L_0x1227;
    L_0x1225:
        goto L_0x12a6;
    L_0x1227:
        r3 = r1.hasGamePreview;
        if (r3 != 0) goto L_0x1292;
    L_0x122b:
        r3 = r1.hasInvoicePreview;
        if (r3 == 0) goto L_0x1230;
    L_0x122f:
        goto L_0x1292;
    L_0x1230:
        r3 = r1.currentPhotoObject;
        r4 = r3.w;
        r3 = r3.h;
        r4 = (float) r4;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r2 = r2 - r5;
        r2 = (float) r2;
        r2 = r4 / r2;
        r4 = r4 / r2;
        r4 = (int) r4;
        r3 = (float) r3;
        r3 = r3 / r2;
        r2 = (int) r3;
        if (r7 == 0) goto L_0x1265;
    L_0x1246:
        if (r7 == 0) goto L_0x1259;
    L_0x1248:
        r3 = r7.toLowerCase();
        r5 = "instagram";
        r3 = r3.equals(r5);
        if (r3 != 0) goto L_0x1259;
    L_0x1254:
        r3 = r1.documentAttachType;
        if (r3 != 0) goto L_0x1259;
    L_0x1258:
        goto L_0x1265;
    L_0x1259:
        r3 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r5 = r3 / 2;
        if (r2 <= r5) goto L_0x1270;
    L_0x1261:
        r5 = 2;
        r2 = r3 / 2;
        goto L_0x1270;
    L_0x1265:
        r3 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r3.y;
        r5 = r3 / 3;
        if (r2 <= r5) goto L_0x1270;
    L_0x126d:
        r5 = 3;
        r2 = r3 / 3;
    L_0x1270:
        r3 = r1.imageBackgroundSideColor;
        if (r3 == 0) goto L_0x1283;
    L_0x1274:
        r2 = (float) r2;
        r3 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r3 = r2 / r3;
        r4 = (float) r4;
        r4 = r4 / r3;
        r4 = (int) r4;
        r2 = r2 / r3;
        r2 = (int) r2;
    L_0x1283:
        r3 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        if (r2 >= r3) goto L_0x12a7;
    L_0x128b:
        r2 = NUM; // 0x42700000 float:60.0 double:5.507034975E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x12a7;
    L_0x1292:
        r3 = 640; // 0x280 float:8.97E-43 double:3.16E-321;
        r4 = 360; // 0x168 float:5.04E-43 double:1.78E-321;
        r3 = (float) r3;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r2 = r2 - r5;
        r2 = (float) r2;
        r2 = r3 / r2;
        r3 = r3 / r2;
        r3 = (int) r3;
        r4 = (float) r4;
        r4 = r4 / r2;
        r2 = (int) r4;
        r4 = r3;
        goto L_0x12a7;
    L_0x12a6:
        r4 = r2;
    L_0x12a7:
        r3 = r1.isSmallImage;
        if (r3 == 0) goto L_0x12dc;
    L_0x12ab:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = r5 + r38;
        r6 = r1.linkPreviewHeight;
        if (r5 <= r6) goto L_0x12d2;
    L_0x12b7:
        r5 = r1.totalHeight;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r6 = r6 + r38;
        r7 = r1.linkPreviewHeight;
        r6 = r6 - r7;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r6 = r6 + r7;
        r5 = r5 + r6;
        r1.totalHeight = r5;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r38;
        r1.linkPreviewHeight = r3;
    L_0x12d2:
        r3 = r1.linkPreviewHeight;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = r3 - r5;
        r1.linkPreviewHeight = r3;
        goto L_0x12ed;
    L_0x12dc:
        r3 = r1.totalHeight;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = r5 + r2;
        r3 = r3 + r5;
        r1.totalHeight = r3;
        r3 = r1.linkPreviewHeight;
        r3 = r3 + r2;
        r1.linkPreviewHeight = r3;
    L_0x12ed:
        r3 = r1.documentAttachType;
        r5 = 8;
        if (r3 != r5) goto L_0x130a;
    L_0x12f3:
        r3 = r1.imageBackgroundSideColor;
        if (r3 != 0) goto L_0x130a;
    L_0x12f7:
        r3 = r1.photoImage;
        r5 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = r29 - r5;
        r5 = java.lang.Math.max(r5, r4);
        r6 = 0;
        r3.setImageCoords(r6, r6, r5, r2);
        goto L_0x1310;
    L_0x130a:
        r6 = 0;
        r3 = r1.photoImage;
        r3.setImageCoords(r6, r6, r4, r2);
    L_0x1310:
        r3 = java.util.Locale.US;
        r12 = 2;
        r5 = new java.lang.Object[r12];
        r7 = java.lang.Integer.valueOf(r4);
        r5[r6] = r7;
        r7 = java.lang.Integer.valueOf(r2);
        r15 = 1;
        r5[r15] = r7;
        r7 = "%d_%d";
        r3 = java.lang.String.format(r3, r7, r5);
        r1.currentPhotoFilter = r3;
        r3 = java.util.Locale.US;
        r5 = new java.lang.Object[r12];
        r7 = java.lang.Integer.valueOf(r4);
        r5[r6] = r7;
        r6 = java.lang.Integer.valueOf(r2);
        r5[r15] = r6;
        r6 = "%d_%d_b";
        r3 = java.lang.String.format(r3, r6, r5);
        r1.currentPhotoFilterThumb = r3;
        if (r9 == 0) goto L_0x1365;
    L_0x1344:
        r2 = r1.photoImage;
        r3 = org.telegram.messenger.ImageLocation.getForWebFile(r9);
        r4 = r1.currentPhotoFilter;
        r5 = 0;
        r6 = 0;
        r7 = r9.size;
        r9 = 0;
        r13 = 1;
        r15 = r8;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r61 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r8 = r9;
        r9 = r59;
        r10 = r13;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
    L_0x135e:
        r55 = r11;
        r60 = r15;
        r15 = 1;
        goto L_0x1625;
    L_0x1365:
        r15 = r8;
        r61 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = r1.documentAttachType;
        r5 = 8;
        if (r3 != r5) goto L_0x13a4;
    L_0x136e:
        r2 = r14.mediaExists;
        if (r2 == 0) goto L_0x138e;
    L_0x1372:
        r2 = r1.photoImage;
        r3 = r1.documentAttach;
        r3 = org.telegram.messenger.ImageLocation.getForDocument(r3);
        r4 = r1.currentPhotoFilter;
        r5 = r1.currentPhotoObject;
        r5 = org.telegram.messenger.ImageLocation.getForDocument(r5, r13);
        r7 = 0;
        r10 = 1;
        r6 = "b1";
        r8 = "jpg";
        r9 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x135e;
    L_0x138e:
        r2 = r1.photoImage;
        r3 = 0;
        r4 = 0;
        r5 = r1.currentPhotoObject;
        r5 = org.telegram.messenger.ImageLocation.getForDocument(r5, r13);
        r7 = 0;
        r10 = 1;
        r6 = "b1";
        r8 = "jpg";
        r9 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x135e;
    L_0x13a4:
        r5 = 6;
        if (r3 != r5) goto L_0x1410;
    L_0x13a7:
        r3 = r59.isSticker();
        r5 = org.telegram.messenger.SharedConfig.loopStickers;
        if (r5 != 0) goto L_0x13e5;
    L_0x13af:
        if (r3 == 0) goto L_0x13b2;
    L_0x13b1:
        goto L_0x13e5;
    L_0x13b2:
        r3 = java.util.Locale.US;
        r13 = 3;
        r5 = new java.lang.Object[r13];
        r4 = java.lang.Integer.valueOf(r4);
        r21 = 0;
        r5[r21] = r4;
        r2 = java.lang.Integer.valueOf(r2);
        r4 = 1;
        r5[r4] = r2;
        r2 = r59.toString();
        r5[r12] = r2;
        r2 = "%d_%d_nr_%s";
        r2 = java.lang.String.format(r3, r2, r5);
        r1.currentPhotoFilter = r2;
        r2 = r1.photoImage;
        r3 = r1.delegate;
        r3 = r3.shouldRepeatSticker(r14);
        if (r3 == 0) goto L_0x13e0;
    L_0x13de:
        r3 = 2;
        goto L_0x13e1;
    L_0x13e0:
        r3 = 3;
    L_0x13e1:
        r2.setAutoRepeat(r3);
        goto L_0x13ee;
    L_0x13e5:
        r13 = 3;
        r21 = 0;
        r2 = r1.photoImage;
        r3 = 1;
        r2.setAutoRepeat(r3);
    L_0x13ee:
        r2 = r1.photoImage;
        r3 = r1.documentAttach;
        r3 = org.telegram.messenger.ImageLocation.getForDocument(r3);
        r4 = r1.currentPhotoFilter;
        r5 = r1.currentPhotoObject;
        r6 = r1.documentAttach;
        r5 = org.telegram.messenger.ImageLocation.getForDocument(r5, r6);
        r6 = r1.documentAttach;
        r7 = r6.size;
        r10 = 1;
        r6 = "b1";
        r8 = "webp";
        r9 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x135e;
    L_0x1410:
        r21 = 0;
        r22 = 3;
        r5 = 4;
        if (r3 != r5) goto L_0x14de;
    L_0x1417:
        r2 = r1.photoImage;
        r3 = 1;
        r2.setNeedsQualityThumb(r3);
        r2 = r1.photoImage;
        r2.setShouldGenerateQualityThumb(r3);
        r2 = org.telegram.messenger.SharedConfig.autoplayVideo;
        if (r2 == 0) goto L_0x1489;
    L_0x1426:
        r2 = r1.currentMessageObject;
        r2 = r2.mediaExists;
        if (r2 != 0) goto L_0x1440;
    L_0x142c:
        r2 = r59.canStreamVideo();
        if (r2 == 0) goto L_0x1489;
    L_0x1432:
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.DownloadController.getInstance(r2);
        r3 = r1.currentMessageObject;
        r2 = r2.canDownloadMedia(r3);
        if (r2 == 0) goto L_0x1489;
    L_0x1440:
        r2 = r1.photoImage;
        r13 = 1;
        r2.setAllowDecodeSingleFrame(r13);
        r2 = r1.photoImage;
        r2.setAllowStartAnimation(r13);
        r2 = r1.photoImage;
        r2.startAnimation();
        r2 = r1.photoImage;
        r3 = r1.documentAttach;
        r3 = org.telegram.messenger.ImageLocation.getForDocument(r3);
        r4 = r1.currentPhotoObject;
        r5 = r1.documentAttach;
        r5 = org.telegram.messenger.ImageLocation.getForDocument(r4, r5);
        r6 = r1.currentPhotoFilter;
        r4 = r1.currentPhotoObjectThumb;
        r7 = r1.documentAttach;
        r7 = org.telegram.messenger.ImageLocation.getForDocument(r4, r7);
        r8 = r1.currentPhotoFilterThumb;
        r9 = 0;
        r4 = r1.documentAttach;
        r10 = r4.size;
        r23 = 0;
        r24 = 0;
        r4 = "g";
        r55 = r11;
        r11 = r23;
        r12 = r59;
        r60 = r15;
        r15 = 1;
        r13 = r24;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        r1.autoPlayingMedia = r15;
        goto L_0x1625;
    L_0x1489:
        r55 = r11;
        r60 = r15;
        r15 = 1;
        r2 = r1.currentPhotoObjectThumb;
        if (r2 == 0) goto L_0x14b2;
    L_0x1492:
        r2 = r1.photoImage;
        r3 = r1.currentPhotoObject;
        r4 = r1.documentAttach;
        r3 = org.telegram.messenger.ImageLocation.getForDocument(r3, r4);
        r4 = r1.currentPhotoFilter;
        r5 = r1.currentPhotoObjectThumb;
        r6 = r1.documentAttach;
        r5 = org.telegram.messenger.ImageLocation.getForDocument(r5, r6);
        r6 = r1.currentPhotoFilterThumb;
        r7 = 0;
        r8 = 0;
        r10 = 0;
        r9 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x1625;
    L_0x14b2:
        r2 = r1.photoImage;
        r3 = 0;
        r4 = 0;
        r5 = r1.currentPhotoObject;
        r6 = r1.documentAttach;
        r5 = org.telegram.messenger.ImageLocation.getForDocument(r5, r6);
        r6 = r1.currentPhotoObject;
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r7 != 0) goto L_0x14d2;
    L_0x14c4:
        r6 = r6.type;
        r7 = "s";
        r6 = r7.equals(r6);
        if (r6 == 0) goto L_0x14cf;
    L_0x14ce:
        goto L_0x14d2;
    L_0x14cf:
        r6 = r1.currentPhotoFilter;
        goto L_0x14d4;
    L_0x14d2:
        r6 = r1.currentPhotoFilterThumb;
    L_0x14d4:
        r7 = 0;
        r8 = 0;
        r10 = 0;
        r9 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x1625;
    L_0x14de:
        r55 = r11;
        r60 = r15;
        r15 = 1;
        if (r3 == r12) goto L_0x157a;
    L_0x14e5:
        r5 = 7;
        if (r3 != r5) goto L_0x14ea;
    L_0x14e8:
        goto L_0x157a;
    L_0x14ea:
        r3 = r14.mediaExists;
        r5 = r1.currentPhotoObject;
        r5 = org.telegram.messenger.FileLoader.getAttachFileName(r5);
        r6 = r1.hasGamePreview;
        if (r6 != 0) goto L_0x1557;
    L_0x14f6:
        if (r3 != 0) goto L_0x1557;
    L_0x14f8:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r6 = r1.currentMessageObject;
        r3 = r3.canDownloadMedia(r6);
        if (r3 != 0) goto L_0x1557;
    L_0x1506:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.FileLoader.getInstance(r3);
        r3 = r3.isLoadingFile(r5);
        if (r3 == 0) goto L_0x1513;
    L_0x1512:
        goto L_0x1557;
    L_0x1513:
        r1.photoNotSet = r15;
        r3 = r1.currentPhotoObjectThumb;
        if (r3 == 0) goto L_0x154e;
    L_0x1519:
        r5 = r1.photoImage;
        r6 = 0;
        r7 = 0;
        r8 = r1.photoParentObject;
        r8 = org.telegram.messenger.ImageLocation.getForObject(r3, r8);
        r3 = java.util.Locale.US;
        r9 = new java.lang.Object[r12];
        r4 = java.lang.Integer.valueOf(r4);
        r11 = 0;
        r9[r11] = r4;
        r2 = java.lang.Integer.valueOf(r2);
        r9[r15] = r2;
        r2 = "%d_%d_b";
        r9 = java.lang.String.format(r3, r2, r9);
        r10 = 0;
        r13 = 0;
        r21 = 0;
        r2 = r5;
        r3 = r6;
        r4 = r7;
        r5 = r8;
        r6 = r9;
        r7 = r10;
        r8 = r13;
        r9 = r59;
        r10 = r21;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x1625;
    L_0x154e:
        r11 = 0;
        r2 = r1.photoImage;
        r3 = 0;
        r2.setImageBitmap(r3);
        goto L_0x1625;
    L_0x1557:
        r11 = 0;
        r1.photoNotSet = r11;
        r2 = r1.photoImage;
        r3 = r1.currentPhotoObject;
        r4 = r1.photoParentObject;
        r3 = org.telegram.messenger.ImageLocation.getForObject(r3, r4);
        r4 = r1.currentPhotoFilter;
        r5 = r1.currentPhotoObjectThumb;
        r6 = r1.photoParentObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6);
        r6 = r1.currentPhotoFilterThumb;
        r7 = 0;
        r8 = 0;
        r10 = 0;
        r9 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x1625;
    L_0x157a:
        r11 = 0;
        r2 = r1.photoImage;
        r2.setAllowDecodeSingleFrame(r15);
        org.telegram.messenger.FileLoader.getAttachFileName(r13);
        r2 = org.telegram.messenger.MessageObject.isRoundVideoDocument(r13);
        if (r2 == 0) goto L_0x159e;
    L_0x1589:
        r2 = r1.photoImage;
        r3 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r3 = r3 / r12;
        r2.setRoundRadius(r3);
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.DownloadController.getInstance(r2);
        r3 = r1.currentMessageObject;
        r2 = r2.canDownloadMedia(r3);
        goto L_0x15b2;
    L_0x159e:
        r2 = org.telegram.messenger.MessageObject.isGifDocument(r13);
        if (r2 == 0) goto L_0x15b1;
    L_0x15a4:
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.DownloadController.getInstance(r2);
        r3 = r1.currentMessageObject;
        r2 = r2.canDownloadMedia(r3);
        goto L_0x15b2;
    L_0x15b1:
        r2 = 0;
    L_0x15b2:
        r3 = r1.currentPhotoObject;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r4 != 0) goto L_0x15c6;
    L_0x15b8:
        r3 = r3.type;
        r4 = "s";
        r3 = r4.equals(r3);
        if (r3 == 0) goto L_0x15c3;
    L_0x15c2:
        goto L_0x15c6;
    L_0x15c3:
        r3 = r1.currentPhotoFilter;
        goto L_0x15c8;
    L_0x15c6:
        r3 = r1.currentPhotoFilterThumb;
    L_0x15c8:
        r34 = r3;
        r3 = r14.mediaExists;
        if (r3 != 0) goto L_0x15ef;
    L_0x15ce:
        if (r2 == 0) goto L_0x15d1;
    L_0x15d0:
        goto L_0x15ef;
    L_0x15d1:
        r2 = r1.photoImage;
        r31 = 0;
        r32 = 0;
        r3 = r1.currentPhotoObject;
        r4 = r1.documentAttach;
        r33 = org.telegram.messenger.ImageLocation.getForDocument(r3, r4);
        r35 = 0;
        r36 = 0;
        r3 = r1.currentMessageObject;
        r38 = 0;
        r30 = r2;
        r37 = r3;
        r30.setImage(r31, r32, r33, r34, r35, r36, r37, r38);
        goto L_0x1625;
    L_0x15ef:
        r1.autoPlayingMedia = r15;
        r2 = r1.photoImage;
        r3 = org.telegram.messenger.ImageLocation.getForDocument(r13);
        r4 = r13.size;
        r5 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        if (r4 >= r5) goto L_0x1600;
    L_0x15fe:
        r4 = 0;
        goto L_0x1602;
    L_0x1600:
        r4 = "g";
    L_0x1602:
        r5 = r1.currentPhotoObject;
        r6 = r1.documentAttach;
        r5 = org.telegram.messenger.ImageLocation.getForDocument(r5, r6);
        r6 = r1.currentPhotoObjectThumb;
        r7 = r1.documentAttach;
        r7 = org.telegram.messenger.ImageLocation.getForDocument(r6, r7);
        r8 = r1.currentPhotoFilterThumb;
        r9 = 0;
        r10 = r13.size;
        r13 = 0;
        r21 = 0;
        r6 = r34;
        r11 = r13;
        r13 = 2;
        r12 = r59;
        r13 = r21;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
    L_0x1625:
        r1.drawPhotoImage = r15;
        if (r60 == 0) goto L_0x1672;
    L_0x1629:
        r2 = "video";
        r4 = r60;
        r2 = r4.equals(r2);
        if (r2 == 0) goto L_0x1672;
    L_0x1633:
        if (r40 == 0) goto L_0x1672;
    L_0x1635:
        r2 = r40 / 60;
        r3 = r2 * 60;
        r40 = r40 - r3;
        r13 = 2;
        r3 = new java.lang.Object[r13];
        r2 = java.lang.Integer.valueOf(r2);
        r12 = 0;
        r3[r12] = r2;
        r2 = java.lang.Integer.valueOf(r40);
        r3[r15] = r2;
        r2 = "%d:%02d";
        r5 = java.lang.String.format(r2, r3);
        r2 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r2 = r2.measureText(r5);
        r2 = (double) r2;
        r2 = java.lang.Math.ceil(r2);
        r2 = (int) r2;
        r1.durationWidth = r2;
        r2 = new android.text.StaticLayout;
        r6 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r7 = r1.durationWidth;
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10 = 0;
        r11 = 0;
        r4 = r2;
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);
        r1.videoInfoLayout = r2;
        goto L_0x16a5;
    L_0x1672:
        r12 = 0;
        r13 = 2;
        r2 = r1.hasGamePreview;
        if (r2 == 0) goto L_0x16a5;
    L_0x1678:
        r2 = NUM; // 0x7f0d0141 float:1.8742766E38 double:1.053129936E-314;
        r3 = "AttachGame";
        r2 = org.telegram.messenger.LocaleController.getString(r3, r2);
        r4 = r2.toUpperCase();
        r2 = org.telegram.ui.ActionBar.Theme.chat_gamePaint;
        r2 = r2.measureText(r4);
        r2 = (double) r2;
        r2 = java.lang.Math.ceil(r2);
        r2 = (int) r2;
        r1.durationWidth = r2;
        r2 = new android.text.StaticLayout;
        r5 = org.telegram.ui.ActionBar.Theme.chat_gamePaint;
        r6 = r1.durationWidth;
        r7 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r8 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = 0;
        r10 = 0;
        r3 = r2;
        r3.<init>(r4, r5, r6, r7, r8, r9, r10);
        r1.videoInfoLayout = r2;
    L_0x16a5:
        r3 = r29;
    L_0x16a7:
        r2 = r1.hasInvoicePreview;
        if (r2 == 0) goto L_0x178c;
    L_0x16ab:
        r2 = r14.messageOwner;
        r2 = r2.media;
        r4 = r2.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x16c3;
    L_0x16b5:
        r2 = NUM; // 0x7f0d0811 float:1.8746303E38 double:1.053130798E-314;
        r4 = "PaymentReceipt";
        r2 = org.telegram.messenger.LocaleController.getString(r4, r2);
        r2 = r2.toUpperCase();
        goto L_0x16e2;
    L_0x16c3:
        r2 = r2.test;
        if (r2 == 0) goto L_0x16d5;
    L_0x16c7:
        r2 = NUM; // 0x7f0d0823 float:1.874634E38 double:1.0531308067E-314;
        r4 = "PaymentTestInvoice";
        r2 = org.telegram.messenger.LocaleController.getString(r4, r2);
        r2 = r2.toUpperCase();
        goto L_0x16e2;
    L_0x16d5:
        r2 = NUM; // 0x7f0d0804 float:1.8746277E38 double:1.0531307914E-314;
        r4 = "PaymentInvoice";
        r2 = org.telegram.messenger.LocaleController.getString(r4, r2);
        r2 = r2.toUpperCase();
    L_0x16e2:
        r4 = org.telegram.messenger.LocaleController.getInstance();
        r5 = r14.messageOwner;
        r5 = r5.media;
        r6 = r5.total_amount;
        r5 = r5.currency;
        r4 = r4.formatCurrencyString(r6, r5);
        r5 = new android.text.SpannableStringBuilder;
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r4);
        r7 = " ";
        r6.append(r7);
        r6.append(r2);
        r2 = r6.toString();
        r5.<init>(r2);
        r2 = new org.telegram.ui.Components.TypefaceSpan;
        r6 = "fonts/rmedium.ttf";
        r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r6);
        r2.<init>(r6);
        r4 = r4.length();
        r6 = 33;
        r5.setSpan(r2, r12, r4, r6);
        r2 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
        r4 = r5.length();
        r2 = r2.measureText(r5, r12, r4);
        r6 = (double) r2;
        r6 = java.lang.Math.ceil(r6);
        r2 = (int) r6;
        r1.durationWidth = r2;
        r2 = new android.text.StaticLayout;
        r28 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
        r4 = r1.durationWidth;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r29 = r4 + r6;
        r30 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r31 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r32 = 0;
        r33 = 0;
        r26 = r2;
        r27 = r5;
        r26.<init>(r27, r28, r29, r30, r31, r32, r33);
        r1.videoInfoLayout = r2;
        r2 = r1.drawPhotoImage;
        if (r2 != 0) goto L_0x178c;
    L_0x1752:
        r2 = r1.totalHeight;
        r4 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r1.totalHeight = r2;
        r2 = r1.timeWidth;
        r4 = r59.isOutOwner();
        if (r4 == 0) goto L_0x1768;
    L_0x1765:
        r4 = 20;
        goto L_0x1769;
    L_0x1768:
        r4 = 0;
    L_0x1769:
        r4 = r4 + 14;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r4 = r1.durationWidth;
        r5 = r4 + r2;
        if (r5 <= r0) goto L_0x1787;
    L_0x1777:
        r3 = java.lang.Math.max(r4, r3);
        r2 = r1.totalHeight;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r1.totalHeight = r2;
        goto L_0x178c;
    L_0x1787:
        r4 = r4 + r2;
        r3 = java.lang.Math.max(r4, r3);
    L_0x178c:
        r2 = r1.hasGamePreview;
        if (r2 == 0) goto L_0x17ab;
    L_0x1790:
        r2 = r14.textHeight;
        if (r2 == 0) goto L_0x17ab;
    L_0x1794:
        r4 = r1.linkPreviewHeight;
        r5 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 + r5;
        r4 = r4 + r2;
        r1.linkPreviewHeight = r4;
        r2 = r1.totalHeight;
        r4 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r1.totalHeight = r2;
    L_0x17ab:
        r2 = r55;
        r1.calcBackgroundWidth(r0, r2, r3);
        goto L_0x17b6;
    L_0x17b1:
        r61 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = 0;
        r13 = 2;
        r15 = 1;
    L_0x17b6:
        r58.createInstantViewButton();
        goto L_0x2047;
    L_0x17bb:
        r61 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = 2;
        r15 = 1;
        r2 = 16;
        r3 = NUM; // 0x42cCLASSNAME float:102.0 double:5.536823734E-315;
        if (r0 != r2) goto L_0x1918;
    L_0x17c5:
        r1.drawName = r12;
        r1.drawForwardedName = r12;
        r1.drawPhotoImage = r12;
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x17fa;
    L_0x17d1:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x17e6;
    L_0x17d9:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x17e6;
    L_0x17df:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x17e6;
    L_0x17e5:
        goto L_0x17e8;
    L_0x17e6:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x17e8:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r2;
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = java.lang.Math.min(r0, r2);
        r1.backgroundWidth = r0;
        goto L_0x1822;
    L_0x17fa:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r0.x;
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x180f;
    L_0x1802:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x180f;
    L_0x1808:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x180f;
    L_0x180e:
        goto L_0x1811;
    L_0x180f:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x1811:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r2;
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = java.lang.Math.min(r0, r2);
        r1.backgroundWidth = r0;
    L_0x1822:
        r0 = r1.backgroundWidth;
        r2 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r1.availableTimeWidth = r0;
        r0 = r58.getMaxNameWidth();
        r2 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        if (r0 >= 0) goto L_0x183e;
    L_0x183a:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r19);
    L_0x183e:
        r2 = org.telegram.messenger.LocaleController.getInstance();
        r2 = r2.formatterDay;
        r3 = r14.messageOwner;
        r3 = r3.date;
        r3 = (long) r3;
        r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r3 = r3 * r5;
        r2 = r2.format(r3);
        r3 = r14.messageOwner;
        r3 = r3.action;
        r3 = (org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall) r3;
        r4 = r3.reason;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        r5 = r59.isOutOwner();
        if (r5 == 0) goto L_0x1877;
    L_0x1861:
        if (r4 == 0) goto L_0x186d;
    L_0x1863:
        r4 = NUM; // 0x7f0d01ea float:1.8743109E38 double:1.0531300196E-314;
        r5 = "CallMessageOutgoingMissed";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        goto L_0x189c;
    L_0x186d:
        r4 = NUM; // 0x7f0d01e9 float:1.8743107E38 double:1.053130019E-314;
        r5 = "CallMessageOutgoing";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        goto L_0x189c;
    L_0x1877:
        if (r4 == 0) goto L_0x1883;
    L_0x1879:
        r4 = NUM; // 0x7f0d01e8 float:1.8743105E38 double:1.0531300186E-314;
        r5 = "CallMessageIncomingMissed";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        goto L_0x189c;
    L_0x1883:
        r4 = r3.reason;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
        if (r4 == 0) goto L_0x1893;
    L_0x1889:
        r4 = NUM; // 0x7f0d01e7 float:1.8743102E38 double:1.053130018E-314;
        r5 = "CallMessageIncomingDeclined";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        goto L_0x189c;
    L_0x1893:
        r4 = NUM; // 0x7f0d01e6 float:1.87431E38 double:1.0531300177E-314;
        r5 = "CallMessageIncoming";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
    L_0x189c:
        r5 = r3.duration;
        if (r5 <= 0) goto L_0x18ba;
    L_0x18a0:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r5.append(r2);
        r2 = ", ";
        r5.append(r2);
        r2 = r3.duration;
        r2 = org.telegram.messenger.LocaleController.formatCallDuration(r2);
        r5.append(r2);
        r2 = r5.toString();
    L_0x18ba:
        r3 = new android.text.StaticLayout;
        r5 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r6 = (float) r0;
        r7 = android.text.TextUtils.TruncateAt.END;
        r27 = android.text.TextUtils.ellipsize(r4, r5, r6, r7);
        r28 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r29 = r0 + r4;
        r30 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r31 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r32 = 0;
        r33 = 0;
        r26 = r3;
        r26.<init>(r27, r28, r29, r30, r31, r32, r33);
        r1.titleLayout = r3;
        r3 = new android.text.StaticLayout;
        r4 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r5 = android.text.TextUtils.TruncateAt.END;
        r35 = android.text.TextUtils.ellipsize(r2, r4, r6, r5);
        r36 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r37 = r0 + r2;
        r38 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r39 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r40 = 0;
        r41 = 0;
        r34 = r3;
        r34.<init>(r35, r36, r37, r38, r39, r40, r41);
        r1.docTitleLayout = r3;
        r58.setMessageObjectInternal(r59);
        r0 = NUM; // 0x42820000 float:65.0 double:5.51286321E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = r1.namesOffset;
        r0 = r0 + r2;
        r1.totalHeight = r0;
        r0 = r1.drawPinnedTop;
        if (r0 == 0) goto L_0x2047;
    L_0x190f:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r61);
        r2 = r2 - r0;
        r1.namesOffset = r2;
        goto L_0x2047;
    L_0x1918:
        r2 = 12;
        if (r0 != r2) goto L_0x1b27;
    L_0x191c:
        r1.drawName = r12;
        r1.drawForwardedName = r15;
        r1.drawPhotoImage = r15;
        r0 = r1.photoImage;
        r2 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.setRoundRadius(r2);
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x195c;
    L_0x1933:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x1948;
    L_0x193b:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x1948;
    L_0x1941:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x1948;
    L_0x1947:
        goto L_0x194a;
    L_0x1948:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x194a:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r2;
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = java.lang.Math.min(r0, r2);
        r1.backgroundWidth = r0;
        goto L_0x1984;
    L_0x195c:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r0.x;
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x1971;
    L_0x1964:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x1971;
    L_0x196a:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x1971;
    L_0x1970:
        goto L_0x1973;
    L_0x1971:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x1973:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r2;
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = java.lang.Math.min(r0, r2);
        r1.backgroundWidth = r0;
    L_0x1984:
        r0 = r1.backgroundWidth;
        r2 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r1.availableTimeWidth = r0;
        r0 = r14.messageOwner;
        r0 = r0.media;
        r0 = r0.user_id;
        r2 = r1.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r0 = java.lang.Integer.valueOf(r0);
        r0 = r2.getUser(r0);
        r2 = r58.getMaxNameWidth();
        r3 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        if (r2 >= 0) goto L_0x19b4;
    L_0x19b0:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r19);
    L_0x19b4:
        r9 = r2;
        if (r0 == 0) goto L_0x19bc;
    L_0x19b7:
        r2 = r1.contactAvatarDrawable;
        r2.setInfo(r0);
    L_0x19bc:
        r2 = r1.photoImage;
        r3 = org.telegram.messenger.ImageLocation.getForUser(r0, r12);
        if (r0 == 0) goto L_0x19c8;
    L_0x19c4:
        r4 = r1.contactAvatarDrawable;
    L_0x19c6:
        r5 = r4;
        goto L_0x19d1;
    L_0x19c8:
        r4 = org.telegram.ui.ActionBar.Theme.chat_contactDrawable;
        r5 = r59.isOutOwner();
        r4 = r4[r5];
        goto L_0x19c6;
    L_0x19d1:
        r6 = 0;
        r8 = 0;
        r4 = "50_50";
        r7 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8);
        r2 = r14.vCardData;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x19ec;
    L_0x19e2:
        r0 = r14.vCardData;
        r1.drawInstantView = r15;
        r2 = 5;
        r1.drawInstantViewType = r2;
    L_0x19e9:
        r27 = r0;
        goto L_0x1a31;
    L_0x19ec:
        if (r0 == 0) goto L_0x1a12;
    L_0x19ee:
        r2 = r0.phone;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x1a12;
    L_0x19f6:
        r2 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "+";
        r3.append(r4);
        r0 = r0.phone;
        r3.append(r0);
        r0 = r3.toString();
        r0 = r2.format(r0);
        goto L_0x19e9;
    L_0x1a12:
        r0 = r14.messageOwner;
        r0 = r0.media;
        r0 = r0.phone_number;
        r2 = android.text.TextUtils.isEmpty(r0);
        if (r2 != 0) goto L_0x1a27;
    L_0x1a1e:
        r2 = org.telegram.PhoneFormat.PhoneFormat.getInstance();
        r0 = r2.format(r0);
        goto L_0x19e9;
    L_0x1a27:
        r0 = NUM; // 0x7f0d06ed float:1.874571E38 double:1.0531306535E-314;
        r2 = "NumberUnknown";
        r0 = org.telegram.messenger.LocaleController.getString(r2, r0);
        goto L_0x19e9;
    L_0x1a31:
        r0 = r14.messageOwner;
        r0 = r0.media;
        r2 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r2, r0);
        r2 = 10;
        r3 = 32;
        r0 = r0.replace(r2, r3);
        r2 = r0.length();
        if (r2 != 0) goto L_0x1a55;
    L_0x1a4b:
        r0 = r14.messageOwner;
        r0 = r0.media;
        r0 = r0.phone_number;
        if (r0 != 0) goto L_0x1a55;
    L_0x1a53:
        r0 = "";
    L_0x1a55:
        r2 = new android.text.StaticLayout;
        r3 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
        r4 = (float) r9;
        r5 = android.text.TextUtils.TruncateAt.END;
        r29 = android.text.TextUtils.ellipsize(r0, r3, r4, r5);
        r30 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
        r0 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r31 = r9 + r0;
        r32 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r33 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r34 = 0;
        r35 = 0;
        r28 = r2;
        r28.<init>(r29, r30, r31, r32, r33, r34, r35);
        r1.titleLayout = r2;
        r0 = new android.text.StaticLayout;
        r28 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r29 = r9 + r2;
        r30 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r31 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r61);
        r2 = (float) r2;
        r33 = 0;
        r26 = r0;
        r32 = r2;
        r26.<init>(r27, r28, r29, r30, r31, r32, r33);
        r1.docTitleLayout = r0;
        r58.setMessageObjectInternal(r59);
        r0 = r1.drawForwardedName;
        if (r0 == 0) goto L_0x1ab8;
    L_0x1a9e:
        r0 = r59.needDrawForwarded();
        if (r0 == 0) goto L_0x1ab8;
    L_0x1aa4:
        r0 = r1.currentPosition;
        if (r0 == 0) goto L_0x1aac;
    L_0x1aa8:
        r0 = r0.minY;
        if (r0 != 0) goto L_0x1ab8;
    L_0x1aac:
        r0 = r1.namesOffset;
        r2 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r2;
        r1.namesOffset = r0;
        goto L_0x1acd;
    L_0x1ab8:
        r0 = r1.drawNameLayout;
        if (r0 == 0) goto L_0x1acd;
    L_0x1abc:
        r0 = r14.messageOwner;
        r0 = r0.reply_to_msg_id;
        if (r0 != 0) goto L_0x1acd;
    L_0x1ac2:
        r0 = r1.namesOffset;
        r2 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r2;
        r1.namesOffset = r0;
    L_0x1acd:
        r0 = NUM; // 0x425CLASSNAME float:55.0 double:5.50055916E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = r1.namesOffset;
        r0 = r0 + r2;
        r2 = r1.docTitleLayout;
        r2 = r2.getHeight();
        r0 = r0 + r2;
        r1.totalHeight = r0;
        r0 = r1.drawPinnedTop;
        if (r0 == 0) goto L_0x1aec;
    L_0x1ae3:
        r0 = r1.namesOffset;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r61);
        r0 = r0 - r2;
        r1.namesOffset = r0;
    L_0x1aec:
        r0 = r1.drawInstantView;
        if (r0 == 0) goto L_0x1af5;
    L_0x1af0:
        r58.createInstantViewButton();
        goto L_0x2047;
    L_0x1af5:
        r0 = r1.docTitleLayout;
        r0 = r0.getLineCount();
        if (r0 <= 0) goto L_0x2047;
    L_0x1afd:
        r0 = r1.backgroundWidth;
        r2 = NUM; // 0x42dCLASSNAME float:110.0 double:5.54200439E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r2 = r1.docTitleLayout;
        r3 = r2.getLineCount();
        r3 = r3 - r15;
        r2 = r2.getLineWidth(r3);
        r2 = (double) r2;
        r2 = java.lang.Math.ceil(r2);
        r2 = (int) r2;
        r0 = r0 - r2;
        r2 = r1.timeWidth;
        if (r0 >= r2) goto L_0x2047;
    L_0x1b1c:
        r0 = r1.totalHeight;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r0 = r0 + r2;
        r1.totalHeight = r0;
        goto L_0x2047;
    L_0x1b27:
        if (r0 != r13) goto L_0x1ba2;
    L_0x1b29:
        r1.drawForwardedName = r15;
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x1b5a;
    L_0x1b31:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x1b46;
    L_0x1b39:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x1b46;
    L_0x1b3f:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x1b46;
    L_0x1b45:
        goto L_0x1b48;
    L_0x1b46:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x1b48:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r2;
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = java.lang.Math.min(r0, r2);
        r1.backgroundWidth = r0;
        goto L_0x1b82;
    L_0x1b5a:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r0.x;
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x1b6f;
    L_0x1b62:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x1b6f;
    L_0x1b68:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x1b6f;
    L_0x1b6e:
        goto L_0x1b71;
    L_0x1b6f:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x1b71:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r2;
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = java.lang.Math.min(r0, r2);
        r1.backgroundWidth = r0;
    L_0x1b82:
        r0 = r1.backgroundWidth;
        r1.createDocumentLayout(r0, r14);
        r58.setMessageObjectInternal(r59);
        r0 = NUM; // 0x428CLASSNAME float:70.0 double:5.51610112E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = r1.namesOffset;
        r0 = r0 + r2;
        r1.totalHeight = r0;
        r0 = r1.drawPinnedTop;
        if (r0 == 0) goto L_0x2047;
    L_0x1b99:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r61);
        r2 = r2 - r0;
        r1.namesOffset = r2;
        goto L_0x2047;
    L_0x1ba2:
        r2 = 14;
        if (r0 != r2) goto L_0x1c1d;
    L_0x1ba6:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x1bd5;
    L_0x1bac:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x1bc1;
    L_0x1bb4:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x1bc1;
    L_0x1bba:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x1bc1;
    L_0x1bc0:
        goto L_0x1bc3;
    L_0x1bc1:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x1bc3:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r2;
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = java.lang.Math.min(r0, r2);
        r1.backgroundWidth = r0;
        goto L_0x1bfd;
    L_0x1bd5:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r0.x;
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x1bea;
    L_0x1bdd:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x1bea;
    L_0x1be3:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x1bea;
    L_0x1be9:
        goto L_0x1bec;
    L_0x1bea:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x1bec:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r2;
        r2 = NUM; // 0x43870000 float:270.0 double:5.597372625E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = java.lang.Math.min(r0, r2);
        r1.backgroundWidth = r0;
    L_0x1bfd:
        r0 = r1.backgroundWidth;
        r1.createDocumentLayout(r0, r14);
        r58.setMessageObjectInternal(r59);
        r0 = NUM; // 0x42a40000 float:82.0 double:5.5238721E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = r1.namesOffset;
        r0 = r0 + r2;
        r1.totalHeight = r0;
        r0 = r1.drawPinnedTop;
        if (r0 == 0) goto L_0x2047;
    L_0x1CLASSNAME:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r61);
        r2 = r2 - r0;
        r1.namesOffset = r2;
        goto L_0x2047;
    L_0x1c1d:
        r2 = 17;
        if (r0 != r2) goto L_0x2052;
    L_0x1CLASSNAME:
        r58.createSelectorDrawable();
        r1.drawName = r15;
        r1.drawForwardedName = r15;
        r1.drawPhotoImage = r12;
        r0 = NUM; // 0x43fa0000 float:500.0 double:5.634608575E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = r59.getMaxMessageTextWidth();
        r0 = java.lang.Math.min(r0, r2);
        r1.availableTimeWidth = r0;
        r2 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r0;
        r1.backgroundWidth = r2;
        r2 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1.availableTimeWidth = r2;
        r58.measureTime(r59);
        r2 = r14.messageOwner;
        r2 = r2.media;
        r2 = (org.telegram.tgnet.TLRPC.TL_messageMediaPoll) r2;
        r3 = r2.poll;
        r3 = r3.closed;
        r1.pollClosed = r3;
        r3 = r59.isVoted();
        r1.pollVoted = r3;
        r3 = new android.text.StaticLayout;
        r4 = r2.poll;
        r4 = r4.question;
        r5 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r5 = r5.getFontMetricsInt();
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = org.telegram.messenger.Emoji.replaceEmoji(r4, r5, r6, r12);
        r6 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r7 = r0 + r4;
        r8 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10 = 0;
        r11 = 0;
        r4 = r3;
        r4.<init>(r5, r6, r7, r8, r9, r10, r11);
        r1.titleLayout = r3;
        r3 = r1.titleLayout;
        if (r3 == 0) goto L_0x1ca5;
    L_0x1c8e:
        r3 = r3.getLineCount();
        r4 = 0;
    L_0x1CLASSNAME:
        if (r4 >= r3) goto L_0x1ca5;
    L_0x1CLASSNAME:
        r5 = r1.titleLayout;
        r5 = r5.getLineLeft(r4);
        r11 = 0;
        r5 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
        if (r5 == 0) goto L_0x1ca2;
    L_0x1ca0:
        r3 = 1;
        goto L_0x1ca7;
    L_0x1ca2:
        r4 = r4 + 1;
        goto L_0x1CLASSNAME;
    L_0x1ca5:
        r11 = 0;
        r3 = 0;
    L_0x1ca7:
        r4 = new android.text.StaticLayout;
        r5 = r2.poll;
        r5 = r5.closed;
        if (r5 == 0) goto L_0x1cb5;
    L_0x1caf:
        r5 = NUM; // 0x7f0d0472 float:1.8744423E38 double:1.05313034E-314;
        r6 = "FinalResults";
        goto L_0x1cba;
    L_0x1cb5:
        r5 = NUM; // 0x7f0d00ef float:1.87426E38 double:1.0531298956E-314;
        r6 = "AnonymousPoll";
    L_0x1cba:
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        r6 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r7 = (float) r0;
        r8 = android.text.TextUtils.TruncateAt.END;
        r27 = android.text.TextUtils.ellipsize(r5, r6, r7, r8);
        r28 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r29 = r0 + r5;
        r30 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r31 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r32 = 0;
        r33 = 0;
        r26 = r4;
        r26.<init>(r27, r28, r29, r30, r31, r32, r33);
        r1.docTitleLayout = r4;
        r4 = r1.docTitleLayout;
        if (r4 == 0) goto L_0x1d13;
    L_0x1ce2:
        r4 = r4.getLineCount();
        if (r4 <= 0) goto L_0x1d13;
    L_0x1ce8:
        if (r3 == 0) goto L_0x1cfe;
    L_0x1cea:
        r4 = org.telegram.messenger.LocaleController.isRTL;
        if (r4 != 0) goto L_0x1cfe;
    L_0x1cee:
        r3 = r1.docTitleLayout;
        r3 = r3.getLineWidth(r12);
        r7 = r7 - r3;
        r3 = (double) r7;
        r3 = java.lang.Math.ceil(r3);
        r3 = (int) r3;
        r1.docTitleOffsetX = r3;
        goto L_0x1d13;
    L_0x1cfe:
        if (r3 != 0) goto L_0x1d13;
    L_0x1d00:
        r3 = org.telegram.messenger.LocaleController.isRTL;
        if (r3 == 0) goto L_0x1d13;
    L_0x1d04:
        r3 = r1.docTitleLayout;
        r3 = r3.getLineLeft(r12);
        r3 = (double) r3;
        r3 = java.lang.Math.ceil(r3);
        r3 = (int) r3;
        r3 = -r3;
        r1.docTitleOffsetX = r3;
    L_0x1d13:
        r3 = r1.timeWidth;
        r3 = r0 - r3;
        r4 = r59.isOutOwner();
        if (r4 == 0) goto L_0x1d20;
    L_0x1d1d:
        r4 = NUM; // 0x41e00000 float:28.0 double:5.46040909E-315;
        goto L_0x1d22;
    L_0x1d20:
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
    L_0x1d22:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r4 = new android.text.StaticLayout;
        r5 = r2.results;
        r5 = r5.total_voters;
        if (r5 != 0) goto L_0x1d39;
    L_0x1d2f:
        r5 = NUM; // 0x7f0d065d float:1.8745419E38 double:1.0531305824E-314;
        r6 = "NoVotes";
        r5 = org.telegram.messenger.LocaleController.getString(r6, r5);
        goto L_0x1d3f;
    L_0x1d39:
        r6 = "Vote";
        r5 = org.telegram.messenger.LocaleController.formatPluralString(r6, r5);
    L_0x1d3f:
        r6 = org.telegram.ui.ActionBar.Theme.chat_livePaint;
        r7 = (float) r3;
        r8 = android.text.TextUtils.TruncateAt.END;
        r27 = android.text.TextUtils.ellipsize(r5, r6, r7, r8);
        r28 = org.telegram.ui.ActionBar.Theme.chat_livePaint;
        r30 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r31 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r32 = 0;
        r33 = 0;
        r26 = r4;
        r29 = r3;
        r26.<init>(r27, r28, r29, r30, r31, r32, r33);
        r1.infoLayout = r4;
        r3 = r1.infoLayout;
        if (r3 == 0) goto L_0x1d6e;
    L_0x1d5f:
        r3 = r3.getLineCount();
        if (r3 <= 0) goto L_0x1d6e;
    L_0x1d65:
        r3 = r1.infoLayout;
        r3 = r3.getLineLeft(r12);
        r3 = -r3;
        r3 = (double) r3;
        goto L_0x1d70;
    L_0x1d6e:
        r3 = 0;
    L_0x1d70:
        r3 = java.lang.Math.ceil(r3);
        r3 = (int) r3;
        r1.infoX = r3;
        r3 = r2.poll;
        r1.lastPoll = r3;
        r3 = r2.results;
        r4 = r3.results;
        r1.lastPollResults = r4;
        r3 = r3.total_voters;
        r1.lastPollResultsVoters = r3;
        r3 = r1.animatePollAnswer;
        if (r3 != 0) goto L_0x1d92;
    L_0x1d89:
        r3 = r1.pollVoteInProgress;
        if (r3 == 0) goto L_0x1d92;
    L_0x1d8d:
        r10 = 3;
        r1.performHapticFeedback(r10, r13);
        goto L_0x1d93;
    L_0x1d92:
        r10 = 3;
    L_0x1d93:
        r3 = r1.attachedToWindow;
        if (r3 == 0) goto L_0x1da1;
    L_0x1d97:
        r3 = r1.pollVoteInProgress;
        if (r3 != 0) goto L_0x1d9f;
    L_0x1d9b:
        r3 = r1.pollUnvoteInProgress;
        if (r3 == 0) goto L_0x1da1;
    L_0x1d9f:
        r3 = 1;
        goto L_0x1da2;
    L_0x1da1:
        r3 = 0;
    L_0x1da2:
        r1.animatePollAnswer = r3;
        r1.animatePollAnswerAlpha = r3;
        r3 = new java.util.ArrayList;
        r3.<init>();
        r4 = r1.pollButtons;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x1e27;
    L_0x1db3:
        r4 = new java.util.ArrayList;
        r5 = r1.pollButtons;
        r4.<init>(r5);
        r5 = r1.pollButtons;
        r5.clear();
        r5 = r1.animatePollAnswer;
        if (r5 != 0) goto L_0x1dd4;
    L_0x1dc3:
        r5 = r1.attachedToWindow;
        if (r5 == 0) goto L_0x1dd1;
    L_0x1dc7:
        r5 = r1.pollVoted;
        if (r5 != 0) goto L_0x1dcf;
    L_0x1dcb:
        r5 = r1.pollClosed;
        if (r5 == 0) goto L_0x1dd1;
    L_0x1dcf:
        r5 = 1;
        goto L_0x1dd2;
    L_0x1dd1:
        r5 = 0;
    L_0x1dd2:
        r1.animatePollAnswer = r5;
    L_0x1dd4:
        r5 = r1.pollAnimationProgress;
        r6 = (r5 > r11 ? 1 : (r5 == r11 ? 0 : -1));
        if (r6 <= 0) goto L_0x1e28;
    L_0x1dda:
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1));
        if (r5 >= 0) goto L_0x1e28;
    L_0x1de0:
        r5 = r4.size();
        r6 = 0;
    L_0x1de5:
        if (r6 >= r5) goto L_0x1e28;
    L_0x1de7:
        r7 = r4.get(r6);
        r7 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r7;
        r8 = r7.prevPercent;
        r8 = (float) r8;
        r21 = r7.percent;
        r22 = r7.prevPercent;
        r10 = r21 - r22;
        r10 = (float) r10;
        r13 = r1.pollAnimationProgress;
        r10 = r10 * r13;
        r8 = r8 + r10;
        r9 = (double) r8;
        r8 = java.lang.Math.ceil(r9);
        r8 = (int) r8;
        r7.percent = r8;
        r8 = r7.prevPercentProgress;
        r9 = r7.percentProgress;
        r10 = r7.prevPercentProgress;
        r9 = r9 - r10;
        r10 = r1.pollAnimationProgress;
        r9 = r9 * r10;
        r8 = r8 + r9;
        r7.percentProgress = r8;
        r6 = r6 + 1;
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r10 = 3;
        r13 = 2;
        goto L_0x1de5;
    L_0x1e27:
        r4 = 0;
    L_0x1e28:
        r5 = r1.animatePollAnswer;
        if (r5 == 0) goto L_0x1e2e;
    L_0x1e2c:
        r5 = 0;
        goto L_0x1e30;
    L_0x1e2e:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x1e30:
        r1.pollAnimationProgress = r5;
        r5 = r1.animatePollAnswerAlpha;
        if (r5 != 0) goto L_0x1e48;
    L_0x1e36:
        r1.pollVoteInProgress = r12;
        r13 = -1;
        r1.pollVoteInProgressNum = r13;
        r5 = r1.currentAccount;
        r5 = org.telegram.messenger.SendMessagesHelper.getInstance(r5);
        r6 = r1.currentMessageObject;
        r5 = r5.isSendingVote(r6);
        goto L_0x1e4a;
    L_0x1e48:
        r13 = -1;
        r5 = 0;
    L_0x1e4a:
        r6 = r1.titleLayout;
        if (r6 == 0) goto L_0x1e53;
    L_0x1e4e:
        r6 = r6.getHeight();
        goto L_0x1e54;
    L_0x1e53:
        r6 = 0;
    L_0x1e54:
        r7 = 100;
        r8 = r2.poll;
        r8 = r8.answers;
        r8 = r8.size();
        r15 = r5;
        r7 = r6;
        r5 = 0;
        r6 = 0;
        r9 = 100;
        r10 = 0;
        r13 = 0;
    L_0x1e66:
        if (r5 >= r8) goto L_0x1fcb;
    L_0x1e68:
        r11 = new org.telegram.ui.Cells.ChatMessageCell$PollButton;
        r12 = 0;
        r11.<init>(r1, r12);
        r12 = r2.poll;
        r12 = r12.answers;
        r12 = r12.get(r5);
        r12 = (org.telegram.tgnet.TLRPC.TL_pollAnswer) r12;
        r11.answer = r12;
        r12 = new android.text.StaticLayout;
        r60 = r8;
        r8 = r11.answer;
        r8 = r8.text;
        r21 = org.telegram.ui.ActionBar.Theme.chat_audioPerformerPaint;
        r14 = r21.getFontMetricsInt();
        r21 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r62 = r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r21 = r5;
        r5 = 0;
        r28 = org.telegram.messenger.Emoji.replaceEmoji(r8, r14, r6, r5);
        r29 = org.telegram.ui.ActionBar.Theme.chat_audioPerformerPaint;
        r5 = NUM; // 0x42040000 float:33.0 double:5.47206556E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r30 = r0 - r5;
        r31 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r32 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r33 = 0;
        r34 = 0;
        r27 = r12;
        r27.<init>(r28, r29, r30, r31, r32, r33, r34);
        r11.title = r12;
        r5 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = r5 + r7;
        r11.y = r5;
        r5 = r11.title;
        r5 = r5.getHeight();
        r11.height = r5;
        r5 = r1.pollButtons;
        r5.add(r11);
        r3.add(r11);
        r5 = r11.height;
        r6 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 + r6;
        r7 = r7 + r5;
        r5 = r2.results;
        r5 = r5.results;
        r5 = r5.isEmpty();
        if (r5 != 0) goto L_0x1f6b;
    L_0x1ee7:
        r5 = r2.results;
        r5 = r5.results;
        r5 = r5.size();
        r6 = 0;
    L_0x1ef0:
        if (r6 >= r5) goto L_0x1f6b;
    L_0x1ef2:
        r8 = r2.results;
        r8 = r8.results;
        r8 = r8.get(r6);
        r8 = (org.telegram.tgnet.TLRPC.TL_pollAnswerVoters) r8;
        r12 = r11.answer;
        r12 = r12.option;
        r14 = r8.option;
        r12 = java.util.Arrays.equals(r12, r14);
        if (r12 == 0) goto L_0x1var_;
    L_0x1f0a:
        r5 = r1.pollVoted;
        if (r5 != 0) goto L_0x1var_;
    L_0x1f0e:
        r5 = r1.pollClosed;
        if (r5 == 0) goto L_0x1f3a;
    L_0x1var_:
        r5 = r2.results;
        r5 = r5.total_voters;
        if (r5 <= 0) goto L_0x1f3a;
    L_0x1var_:
        r6 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r8 = r8.voters;
        r8 = (float) r8;
        r5 = (float) r5;
        r8 = r8 / r5;
        r8 = r8 * r6;
        r11.decimal = r8;
        r5 = r11.decimal;
        r5 = (int) r5;
        r11.percent = r5;
        r5 = r11.decimal;
        r6 = r11.percent;
        r6 = (float) r6;
        r5 = r5 - r6;
        r11.decimal = r5;
        goto L_0x1var_;
    L_0x1f3a:
        r5 = 0;
        r11.percent = r5;
        r5 = 0;
        r11.decimal = r5;
    L_0x1var_:
        if (r13 != 0) goto L_0x1var_;
    L_0x1var_:
        r13 = r11.percent;
        goto L_0x1var_;
    L_0x1var_:
        r5 = r11.percent;
        if (r5 == 0) goto L_0x1var_;
    L_0x1f4f:
        r5 = r11.percent;
        if (r13 == r5) goto L_0x1var_;
    L_0x1var_:
        r6 = 1;
        goto L_0x1var_;
    L_0x1var_:
        r6 = r62;
    L_0x1var_:
        r5 = r11.percent;
        r9 = r9 - r5;
        r5 = r11.percent;
        r5 = java.lang.Math.max(r5, r10);
        r10 = r5;
        goto L_0x1f6d;
    L_0x1var_:
        r6 = r6 + 1;
        goto L_0x1ef0;
    L_0x1f6b:
        r6 = r62;
    L_0x1f6d:
        if (r4 == 0) goto L_0x1fa4;
    L_0x1f6f:
        r5 = r4.size();
        r8 = 0;
    L_0x1var_:
        if (r8 >= r5) goto L_0x1fa4;
    L_0x1var_:
        r12 = r4.get(r8);
        r12 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r12;
        r14 = r11.answer;
        r14 = r14.option;
        r22 = r0;
        r0 = r12.answer;
        r0 = r0.option;
        r0 = java.util.Arrays.equals(r14, r0);
        if (r0 == 0) goto L_0x1f9f;
    L_0x1var_:
        r0 = r12.percent;
        r11.prevPercent = r0;
        r0 = r12.percentProgress;
        r11.prevPercentProgress = r0;
        goto L_0x1fa6;
    L_0x1f9f:
        r8 = r8 + 1;
        r0 = r22;
        goto L_0x1var_;
    L_0x1fa4:
        r22 = r0;
    L_0x1fa6:
        if (r15 == 0) goto L_0x1fbd;
    L_0x1fa8:
        r0 = r11.answer;
        r0 = r0.option;
        r0 = java.util.Arrays.equals(r0, r15);
        if (r0 == 0) goto L_0x1fbd;
    L_0x1fb4:
        r12 = r21;
        r1.pollVoteInProgressNum = r12;
        r5 = 1;
        r1.pollVoteInProgress = r5;
        r15 = 0;
        goto L_0x1fbf;
    L_0x1fbd:
        r12 = r21;
    L_0x1fbf:
        r5 = r12 + 1;
        r14 = r59;
        r8 = r60;
        r0 = r22;
        r11 = 0;
        r12 = 0;
        goto L_0x1e66;
    L_0x1fcb:
        r62 = r6;
        if (r62 == 0) goto L_0x1ff3;
    L_0x1fcf:
        if (r9 == 0) goto L_0x1ff3;
    L_0x1fd1:
        r0 = org.telegram.ui.Cells.-$$Lambda$ChatMessageCell$hzMG4njhE1StYhHOT542pSi6Cf0.INSTANCE;
        java.util.Collections.sort(r3, r0);
        r0 = r3.size();
        r0 = java.lang.Math.min(r9, r0);
        r2 = 0;
    L_0x1fdf:
        if (r2 >= r0) goto L_0x1ff3;
    L_0x1fe1:
        r4 = r3.get(r2);
        r4 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r4;
        r5 = r4.percent;
        r6 = 1;
        r5 = r5 + r6;
        r4.percent = r5;
        r2 = r2 + 1;
        goto L_0x1fdf;
    L_0x1ff3:
        r0 = r1.backgroundWidth;
        r2 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r2 = r1.pollButtons;
        r2 = r2.size();
        r3 = 0;
    L_0x2003:
        if (r3 >= r2) goto L_0x202b;
    L_0x2005:
        r4 = r1.pollButtons;
        r4 = r4.get(r3);
        r4 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r4;
        r5 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = (float) r5;
        r6 = (float) r0;
        r5 = r5 / r6;
        if (r10 == 0) goto L_0x2020;
    L_0x2018:
        r6 = r4.percent;
        r6 = (float) r6;
        r8 = (float) r10;
        r6 = r6 / r8;
        goto L_0x2021;
    L_0x2020:
        r6 = 0;
    L_0x2021:
        r5 = java.lang.Math.max(r5, r6);
        r4.percentProgress = r5;
        r3 = r3 + 1;
        goto L_0x2003;
    L_0x202b:
        r58.setMessageObjectInternal(r59);
        r0 = NUM; // 0x42920000 float:73.0 double:5.518043864E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = r1.namesOffset;
        r0 = r0 + r2;
        r0 = r0 + r7;
        r1.totalHeight = r0;
        r0 = r1.drawPinnedTop;
        if (r0 == 0) goto L_0x2047;
    L_0x203e:
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r0;
        r1.namesOffset = r2;
    L_0x2047:
        r14 = r59;
        r4 = 0;
        r9 = 0;
        r15 = -1;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r30 = 0;
        goto L_0x35b4;
    L_0x2052:
        r0 = r14.messageOwner;
        r0 = r0.fwd_from;
        if (r0 == 0) goto L_0x2060;
    L_0x2058:
        r0 = r59.isAnyKindOfSticker();
        if (r0 != 0) goto L_0x2060;
    L_0x205e:
        r0 = 1;
        goto L_0x2061;
    L_0x2060:
        r0 = 0;
    L_0x2061:
        r1.drawForwardedName = r0;
        r0 = r14.type;
        r2 = 9;
        if (r0 == r2) goto L_0x206b;
    L_0x2069:
        r0 = 1;
        goto L_0x206c;
    L_0x206b:
        r0 = 0;
    L_0x206c:
        r1.mediaBackground = r0;
        r2 = 1;
        r1.drawImageButton = r2;
        r1.drawPhotoImage = r2;
        r0 = r14.gifState;
        r0 = (r0 > r20 ? 1 : (r0 == r20 ? 0 : -1));
        if (r0 == 0) goto L_0x208b;
    L_0x2079:
        r0 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r0 != 0) goto L_0x208b;
    L_0x207d:
        r0 = r14.type;
        r6 = 8;
        if (r0 == r6) goto L_0x2086;
    L_0x2083:
        r2 = 5;
        if (r0 != r2) goto L_0x208d;
    L_0x2086:
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r14.gifState = r9;
        goto L_0x208f;
    L_0x208b:
        r6 = 8;
    L_0x208d:
        r9 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x208f:
        r0 = r1.photoImage;
        r2 = 1;
        r0.setAllowDecodeSingleFrame(r2);
        r0 = r59.isVideo();
        if (r0 == 0) goto L_0x20a1;
    L_0x209b:
        r0 = r1.photoImage;
        r0.setAllowStartAnimation(r2);
        goto L_0x20d0;
    L_0x20a1:
        r0 = r59.isRoundVideo();
        if (r0 == 0) goto L_0x20c1;
    L_0x20a7:
        r0 = org.telegram.messenger.MediaController.getInstance();
        r0 = r0.getPlayingMessageObject();
        r2 = r1.photoImage;
        if (r0 == 0) goto L_0x20bc;
    L_0x20b3:
        r0 = r0.isRoundVideo();
        if (r0 != 0) goto L_0x20ba;
    L_0x20b9:
        goto L_0x20bc;
    L_0x20ba:
        r0 = 0;
        goto L_0x20bd;
    L_0x20bc:
        r0 = 1;
    L_0x20bd:
        r2.setAllowStartAnimation(r0);
        goto L_0x20d0;
    L_0x20c1:
        r0 = r1.photoImage;
        r2 = r14.gifState;
        r4 = 0;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x20cc;
    L_0x20ca:
        r2 = 1;
        goto L_0x20cd;
    L_0x20cc:
        r2 = 0;
    L_0x20cd:
        r0.setAllowStartAnimation(r2);
    L_0x20d0:
        r0 = r1.photoImage;
        r2 = r59.needDrawBluredPreview();
        r0.setForcePreview(r2);
        r0 = r14.type;
        r2 = 9;
        if (r0 != r2) goto L_0x22eb;
    L_0x20df:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x210e;
    L_0x20e5:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x20fa;
    L_0x20ed:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x20fa;
    L_0x20f3:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x20fa;
    L_0x20f9:
        goto L_0x20fc;
    L_0x20fa:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x20fc:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r2;
        r2 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = java.lang.Math.min(r0, r2);
        r1.backgroundWidth = r0;
        goto L_0x2136;
    L_0x210e:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r0.x;
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x2123;
    L_0x2116:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x2123;
    L_0x211c:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x2123;
    L_0x2122:
        goto L_0x2125;
    L_0x2123:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x2125:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r2;
        r2 = NUM; // 0x43960000 float:300.0 double:5.60222949E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = java.lang.Math.min(r0, r2);
        r1.backgroundWidth = r0;
    L_0x2136:
        r0 = r58.checkNeedDrawShareButton(r59);
        if (r0 == 0) goto L_0x2147;
    L_0x213c:
        r0 = r1.backgroundWidth;
        r2 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r1.backgroundWidth = r0;
    L_0x2147:
        r0 = r1.backgroundWidth;
        r2 = NUM; // 0x430a0000 float:138.0 double:5.55689877E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r0 - r2;
        r1.createDocumentLayout(r2, r14);
        r0 = r14.caption;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x21be;
    L_0x215c:
        r0 = r14.caption;	 Catch:{ Exception -> 0x21b8 }
        r1.currentCaption = r0;	 Catch:{ Exception -> 0x21b8 }
        r0 = r1.backgroundWidth;	 Catch:{ Exception -> 0x21b8 }
        r3 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x21b8 }
        r0 = r0 - r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r19);	 Catch:{ Exception -> 0x21b8 }
        r3 = r0 - r3;
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x21b5 }
        r4 = 24;
        if (r0 < r4) goto L_0x219a;
    L_0x2175:
        r0 = r14.caption;	 Catch:{ Exception -> 0x21b5 }
        r4 = r14.caption;	 Catch:{ Exception -> 0x21b5 }
        r4 = r4.length();	 Catch:{ Exception -> 0x21b5 }
        r5 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x21b5 }
        r7 = 0;
        r0 = android.text.StaticLayout.Builder.obtain(r0, r7, r4, r5, r3);	 Catch:{ Exception -> 0x21b5 }
        r4 = 1;
        r0 = r0.setBreakStrategy(r4);	 Catch:{ Exception -> 0x21b5 }
        r0 = r0.setHyphenationFrequency(r7);	 Catch:{ Exception -> 0x21b5 }
        r4 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x21b5 }
        r0 = r0.setAlignment(r4);	 Catch:{ Exception -> 0x21b5 }
        r0 = r0.build();	 Catch:{ Exception -> 0x21b5 }
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x21b5 }
        goto L_0x21b3;
    L_0x219a:
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x21b5 }
        r4 = r14.caption;	 Catch:{ Exception -> 0x21b5 }
        r29 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x21b5 }
        r31 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x21b5 }
        r32 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r33 = 0;
        r34 = 0;
        r27 = r0;
        r28 = r4;
        r30 = r3;
        r27.<init>(r28, r29, r30, r31, r32, r33, r34);	 Catch:{ Exception -> 0x21b5 }
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x21b5 }
    L_0x21b3:
        r12 = r3;
        goto L_0x21bf;
    L_0x21b5:
        r0 = move-exception;
        r12 = r3;
        goto L_0x21ba;
    L_0x21b8:
        r0 = move-exception;
        r12 = 0;
    L_0x21ba:
        org.telegram.messenger.FileLog.e(r0);
        goto L_0x21bf;
    L_0x21be:
        r12 = 0;
    L_0x21bf:
        r0 = r1.docTitleLayout;
        if (r0 == 0) goto L_0x21f6;
    L_0x21c3:
        r0 = r0.getLineCount();
        r3 = 0;
        r4 = 0;
    L_0x21c9:
        if (r3 >= r0) goto L_0x21f7;
    L_0x21cb:
        r5 = r1.docTitleLayout;
        r5 = r5.getLineWidth(r3);
        r7 = r1.docTitleLayout;
        r7 = r7.getLineLeft(r3);
        r5 = r5 + r7;
        r7 = (double) r5;
        r7 = java.lang.Math.ceil(r7);
        r5 = (int) r7;
        r7 = r1.drawPhotoImage;
        if (r7 == 0) goto L_0x21e5;
    L_0x21e2:
        r7 = 52;
        goto L_0x21e7;
    L_0x21e5:
        r7 = 22;
    L_0x21e7:
        r7 = r7 + 86;
        r7 = (float) r7;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r5 = r5 + r7;
        r4 = java.lang.Math.max(r4, r5);
        r3 = r3 + 1;
        goto L_0x21c9;
    L_0x21f6:
        r4 = 0;
    L_0x21f7:
        r0 = r1.infoLayout;
        if (r0 == 0) goto L_0x2226;
    L_0x21fb:
        r0 = r0.getLineCount();
        r3 = 0;
    L_0x2200:
        if (r3 >= r0) goto L_0x2226;
    L_0x2202:
        r5 = r1.infoLayout;
        r5 = r5.getLineWidth(r3);
        r7 = (double) r5;
        r7 = java.lang.Math.ceil(r7);
        r5 = (int) r7;
        r7 = r1.drawPhotoImage;
        if (r7 == 0) goto L_0x2215;
    L_0x2212:
        r7 = 52;
        goto L_0x2217;
    L_0x2215:
        r7 = 22;
    L_0x2217:
        r7 = r7 + 86;
        r7 = (float) r7;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r5 = r5 + r7;
        r4 = java.lang.Math.max(r4, r5);
        r3 = r3 + 1;
        goto L_0x2200;
    L_0x2226:
        r0 = r1.captionLayout;
        if (r0 == 0) goto L_0x2256;
    L_0x222a:
        r0 = r0.getLineCount();
        r3 = 0;
    L_0x222f:
        if (r3 >= r0) goto L_0x2256;
    L_0x2231:
        r5 = (float) r12;
        r7 = r1.captionLayout;
        r7 = r7.getLineWidth(r3);
        r8 = r1.captionLayout;
        r8 = r8.getLineLeft(r3);
        r7 = r7 + r8;
        r5 = java.lang.Math.min(r5, r7);
        r7 = (double) r5;
        r7 = java.lang.Math.ceil(r7);
        r5 = (int) r7;
        r7 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r5 = r5 + r7;
        if (r5 <= r4) goto L_0x2253;
    L_0x2252:
        r4 = r5;
    L_0x2253:
        r3 = r3 + 1;
        goto L_0x222f;
    L_0x2256:
        if (r4 <= 0) goto L_0x2262;
    L_0x2258:
        r1.backgroundWidth = r4;
        r0 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = r4 - r0;
    L_0x2262:
        r0 = r1.drawPhotoImage;
        if (r0 == 0) goto L_0x2273;
    L_0x2266:
        r0 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r3 = NUM; // 0x42aCLASSNAME float:86.0 double:5.526462427E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        goto L_0x229a;
    L_0x2273:
        r0 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r3 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.docTitleLayout;
        if (r4 == 0) goto L_0x229a;
    L_0x2283:
        r4 = r4.getLineCount();
        r5 = 1;
        if (r4 <= r5) goto L_0x229a;
    L_0x228a:
        r4 = r1.docTitleLayout;
        r4 = r4.getLineCount();
        r4 = r4 - r5;
        r5 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 * r5;
        r3 = r3 + r4;
    L_0x229a:
        r1.availableTimeWidth = r2;
        r2 = r1.drawPhotoImage;
        if (r2 != 0) goto L_0x22e0;
    L_0x22a0:
        r2 = r14.caption;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 == 0) goto L_0x22e0;
    L_0x22a8:
        r2 = r1.infoLayout;
        if (r2 == 0) goto L_0x22e0;
    L_0x22ac:
        r2 = r2.getLineCount();
        r58.measureTime(r59);
        r4 = r1.backgroundWidth;
        r5 = NUM; // 0x42var_ float:122.0 double:5.54977537E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 - r5;
        r5 = r1.infoLayout;
        r7 = 0;
        r5 = r5.getLineWidth(r7);
        r7 = (double) r5;
        r7 = java.lang.Math.ceil(r7);
        r5 = (int) r7;
        r4 = r4 - r5;
        r5 = r1.timeWidth;
        if (r4 >= r5) goto L_0x22d6;
    L_0x22ce:
        r2 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
    L_0x22d4:
        r3 = r3 + r2;
        goto L_0x22e0;
    L_0x22d6:
        r4 = 1;
        if (r2 != r4) goto L_0x22e0;
    L_0x22d9:
        r2 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x22d4;
    L_0x22e0:
        r12 = r3;
        r2 = 0;
        r15 = -1;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
    L_0x22e5:
        r30 = 0;
        r56 = 0;
        goto L_0x34b7;
    L_0x22eb:
        r2 = 4;
        if (r0 != r2) goto L_0x27f6;
    L_0x22ee:
        r0 = r14.messageOwner;
        r0 = r0.media;
        r2 = r0.geo;
        r4 = r2.lat;
        r7 = r2._long;
        r10 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r10 == 0) goto L_0x2529;
    L_0x22fc:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x232c;
    L_0x2302:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r10 = r1.isChat;
        if (r10 == 0) goto L_0x2317;
    L_0x230a:
        r10 = r59.needDrawAvatar();
        if (r10 == 0) goto L_0x2317;
    L_0x2310:
        r10 = r59.isOutOwner();
        if (r10 != 0) goto L_0x2317;
    L_0x2316:
        goto L_0x2319;
    L_0x2317:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x2319:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = java.lang.Math.min(r0, r3);
        r1.backgroundWidth = r0;
        goto L_0x2355;
    L_0x232c:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r0.x;
        r10 = r1.isChat;
        if (r10 == 0) goto L_0x2341;
    L_0x2334:
        r10 = r59.needDrawAvatar();
        if (r10 == 0) goto L_0x2341;
    L_0x233a:
        r10 = r59.isOutOwner();
        if (r10 != 0) goto L_0x2341;
    L_0x2340:
        goto L_0x2343;
    L_0x2341:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x2343:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = java.lang.Math.min(r0, r3);
        r1.backgroundWidth = r0;
    L_0x2355:
        r0 = r1.backgroundWidth;
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.backgroundWidth = r0;
        r0 = r58.checkNeedDrawShareButton(r59);
        if (r0 == 0) goto L_0x2371;
    L_0x2366:
        r0 = r1.backgroundWidth;
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.backgroundWidth = r0;
    L_0x2371:
        r0 = r1.backgroundWidth;
        r3 = NUM; // 0x42140000 float:37.0 double:5.477246216E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.availableTimeWidth = r0;
        r3 = NUM; // 0x42580000 float:54.0 double:5.499263994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = r1.backgroundWidth;
        r10 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r3 = r3 - r10;
        r10 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = NUM; // 0x10000000 float:2.5243549E-29 double:1.32624737E-315;
        r11 = (double) r11;
        r21 = NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.NUM;
        java.lang.Double.isNaN(r11);
        r21 = r11 / r21;
        r28 = NUM; // 0x3ffNUM float:0.0 double:1.0;
        r30 = NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.NUM;
        r4 = r4 * r30;
        r30 = NUM; // 0xNUM float:0.0 double:180.0;
        r4 = r4 / r30;
        r30 = java.lang.Math.sin(r4);
        r30 = r30 + r28;
        r4 = java.lang.Math.sin(r4);
        r28 = r28 - r4;
        r30 = r30 / r28;
        r4 = java.lang.Math.log(r30);
        r4 = r4 * r21;
        r28 = NUM; // 0xNUM float:0.0 double:2.0;
        r4 = r4 / r28;
        java.lang.Double.isNaN(r11);
        r4 = r11 - r4;
        r4 = java.lang.Math.round(r4);
        r13 = NUM; // 0x4124cccd float:10.3 double:5.399795443E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r15 = 6;
        r13 = r13 << r15;
        r37 = r7;
        r6 = (long) r13;
        r4 = r4 - r6;
        r4 = (double) r4;
        r6 = NUM; // 0x3fvar_fb54442d18 float:3.37028055E12 double:1.NUM;
        r27 = NUM; // 0xNUM float:0.0 double:2.0;
        java.lang.Double.isNaN(r4);
        java.lang.Double.isNaN(r11);
        r4 = r4 - r11;
        r4 = r4 / r21;
        r4 = java.lang.Math.exp(r4);
        r4 = java.lang.Math.atan(r4);
        r4 = r4 * r27;
        r6 = r6 - r4;
        r4 = NUM; // 0xNUM float:0.0 double:180.0;
        r6 = r6 * r4;
        r4 = NUM; // 0x400921fb54442d18 float:3.37028055E12 double:3.NUM;
        r4 = r6 / r4;
        r6 = r1.currentAccount;
        r7 = (float) r3;
        r8 = org.telegram.messenger.AndroidUtilities.density;
        r11 = r7 / r8;
        r11 = (int) r11;
        r12 = (float) r10;
        r8 = r12 / r8;
        r8 = (int) r8;
        r35 = 0;
        r36 = 15;
        r28 = r6;
        r29 = r4;
        r31 = r37;
        r33 = r11;
        r34 = r8;
        r6 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r28, r29, r31, r33, r34, r35, r36);
        r1.currentUrl = r6;
        r62 = r10;
        r9 = r2.access_hash;
        r2 = org.telegram.messenger.AndroidUtilities.density;
        r7 = r7 / r2;
        r6 = (int) r7;
        r12 = r12 / r2;
        r7 = (int) r12;
        r11 = (double) r2;
        r11 = java.lang.Math.ceil(r11);
        r2 = (int) r11;
        r8 = 2;
        r2 = java.lang.Math.min(r8, r2);
        r28 = r4;
        r30 = r37;
        r32 = r9;
        r34 = r6;
        r35 = r7;
        r37 = r2;
        r2 = org.telegram.messenger.WebFile.createWithGeoPoint(r28, r30, r32, r34, r35, r36, r37);
        r1.currentWebFile = r2;
        r2 = r58.isCurrentLocationTimeExpired(r59);
        r1.locationExpired = r2;
        if (r2 != 0) goto L_0x246f;
    L_0x2456:
        r2 = r1.photoImage;
        r4 = 1;
        r2.setCrossfadeWithOldImage(r4);
        r2 = 0;
        r1.mediaBackground = r2;
        r2 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r1.invalidateRunnable;
        r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r2, r5);
        r1.scheduledInvalidate = r4;
        goto L_0x247b;
    L_0x246f:
        r2 = r1.backgroundWidth;
        r4 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r4;
        r1.backgroundWidth = r2;
        r12 = 0;
    L_0x247b:
        r2 = new android.text.StaticLayout;
        r4 = NUM; // 0x7f0d0147 float:1.8742778E38 double:1.053129939E-314;
        r5 = "AttachLiveLocation";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        r5 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
        r6 = (float) r0;
        r7 = android.text.TextUtils.TruncateAt.END;
        r29 = android.text.TextUtils.ellipsize(r4, r5, r6, r7);
        r30 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
        r32 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r33 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r34 = 0;
        r35 = 0;
        r28 = r2;
        r31 = r0;
        r28.<init>(r29, r30, r31, r32, r33, r34, r35);
        r1.docTitleLayout = r2;
        r58.updateCurrentUserAndChat();
        r2 = r1.currentUser;
        if (r2 == 0) goto L_0x24c3;
    L_0x24a9:
        r4 = r1.contactAvatarDrawable;
        r4.setInfo(r2);
        r5 = r1.locationImageReceiver;
        r2 = r1.currentUser;
        r4 = 0;
        r6 = org.telegram.messenger.ImageLocation.getForUser(r2, r4);
        r8 = r1.contactAvatarDrawable;
        r9 = 0;
        r10 = r1.currentUser;
        r11 = 0;
        r7 = "50_50";
        r5.setImage(r6, r7, r8, r9, r10, r11);
        goto L_0x2500;
    L_0x24c3:
        r2 = r1.currentChat;
        if (r2 == 0) goto L_0x24eb;
    L_0x24c7:
        r2 = r2.photo;
        if (r2 == 0) goto L_0x24cf;
    L_0x24cb:
        r2 = r2.photo_small;
        r1.currentPhoto = r2;
    L_0x24cf:
        r2 = r1.contactAvatarDrawable;
        r4 = r1.currentChat;
        r2.setInfo(r4);
        r5 = r1.locationImageReceiver;
        r2 = r1.currentChat;
        r4 = 0;
        r6 = org.telegram.messenger.ImageLocation.getForChat(r2, r4);
        r8 = r1.contactAvatarDrawable;
        r9 = 0;
        r10 = r1.currentChat;
        r11 = 0;
        r7 = "50_50";
        r5.setImage(r6, r7, r8, r9, r10, r11);
        goto L_0x2500;
    L_0x24eb:
        r2 = r1.locationImageReceiver;
        r28 = 0;
        r29 = 0;
        r4 = r1.contactAvatarDrawable;
        r31 = 0;
        r32 = 0;
        r33 = 0;
        r27 = r2;
        r30 = r4;
        r27.setImage(r28, r29, r30, r31, r32, r33);
    L_0x2500:
        r2 = new android.text.StaticLayout;
        r4 = r14.messageOwner;
        r5 = r4.edit_date;
        if (r5 == 0) goto L_0x250a;
    L_0x2508:
        r4 = (long) r5;
        goto L_0x250d;
    L_0x250a:
        r4 = r4.date;
        r4 = (long) r4;
    L_0x250d:
        r29 = org.telegram.messenger.LocaleController.formatLocationUpdateDate(r4);
        r30 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
        r32 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r33 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r34 = 0;
        r35 = 0;
        r28 = r2;
        r31 = r0;
        r28.<init>(r29, r30, r31, r32, r33, r34, r35);
        r1.infoLayout = r2;
        r9 = r62;
        r0 = r3;
        goto L_0x2693;
    L_0x2529:
        r37 = r7;
        r0 = r0.title;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x2696;
    L_0x2533:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x2563;
    L_0x2539:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = r1.isChat;
        if (r6 == 0) goto L_0x254e;
    L_0x2541:
        r6 = r59.needDrawAvatar();
        if (r6 == 0) goto L_0x254e;
    L_0x2547:
        r6 = r59.isOutOwner();
        if (r6 != 0) goto L_0x254e;
    L_0x254d:
        goto L_0x2550;
    L_0x254e:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x2550:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = java.lang.Math.min(r0, r3);
        r1.backgroundWidth = r0;
        goto L_0x258c;
    L_0x2563:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r0.x;
        r6 = r1.isChat;
        if (r6 == 0) goto L_0x2578;
    L_0x256b:
        r6 = r59.needDrawAvatar();
        if (r6 == 0) goto L_0x2578;
    L_0x2571:
        r6 = r59.isOutOwner();
        if (r6 != 0) goto L_0x2578;
    L_0x2577:
        goto L_0x257a;
    L_0x2578:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x257a:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = java.lang.Math.min(r0, r3);
        r1.backgroundWidth = r0;
    L_0x258c:
        r0 = r1.backgroundWidth;
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.backgroundWidth = r0;
        r0 = r58.checkNeedDrawShareButton(r59);
        if (r0 == 0) goto L_0x25a8;
    L_0x259d:
        r0 = r1.backgroundWidth;
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.backgroundWidth = r0;
    L_0x25a8:
        r0 = r1.backgroundWidth;
        r3 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.availableTimeWidth = r0;
        r3 = r1.backgroundWidth;
        r6 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r3 = r3 - r6;
        r6 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = 0;
        r1.mediaBackground = r7;
        r7 = r1.currentAccount;
        r8 = (float) r3;
        r9 = org.telegram.messenger.AndroidUtilities.density;
        r10 = r8 / r9;
        r10 = (int) r10;
        r11 = (float) r6;
        r9 = r11 / r9;
        r9 = (int) r9;
        r35 = 1;
        r36 = 15;
        r28 = r7;
        r29 = r4;
        r31 = r37;
        r33 = r10;
        r34 = r9;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r28, r29, r31, r33, r34, r35, r36);
        r1.currentUrl = r4;
        r4 = org.telegram.messenger.AndroidUtilities.density;
        r8 = r8 / r4;
        r5 = (int) r8;
        r11 = r11 / r4;
        r7 = (int) r11;
        r8 = 15;
        r9 = (double) r4;
        r9 = java.lang.Math.ceil(r9);
        r4 = (int) r9;
        r9 = 2;
        r4 = java.lang.Math.min(r9, r4);
        r2 = org.telegram.messenger.WebFile.createWithGeoPoint(r2, r5, r7, r8, r4);
        r1.currentWebFile = r2;
        r2 = r14.messageOwner;
        r2 = r2.media;
        r2 = r2.title;
        r28 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
        r30 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r31 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r32 = 0;
        r33 = 0;
        r34 = android.text.TextUtils.TruncateAt.END;
        r36 = 1;
        r27 = r2;
        r29 = r0;
        r35 = r0;
        r2 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r27, r28, r29, r30, r31, r32, r33, r34, r35, r36);
        r1.docTitleLayout = r2;
        r9 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r4 = 0;
        r12 = r4 + r2;
        r2 = r1.docTitleLayout;
        r2.getLineCount();
        r2 = r14.messageOwner;
        r2 = r2.media;
        r2 = r2.address;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x268e;
    L_0x2638:
        r2 = r14.messageOwner;
        r2 = r2.media;
        r2 = r2.address;
        r28 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
        r30 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r31 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r32 = 0;
        r33 = 0;
        r34 = android.text.TextUtils.TruncateAt.END;
        r36 = 1;
        r27 = r2;
        r29 = r0;
        r35 = r0;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r27, r28, r29, r30, r31, r32, r33, r34, r35, r36);
        r1.infoLayout = r0;
        r58.measureTime(r59);
        r0 = r1.backgroundWidth;
        r2 = r1.infoLayout;
        r4 = 0;
        r2 = r2.getLineWidth(r4);
        r4 = (double) r2;
        r4 = java.lang.Math.ceil(r4);
        r2 = (int) r4;
        r0 = r0 - r2;
        r2 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r2 = r1.timeWidth;
        r4 = r59.isOutOwner();
        if (r4 == 0) goto L_0x267d;
    L_0x267a:
        r4 = 20;
        goto L_0x267e;
    L_0x267d:
        r4 = 0;
    L_0x267e:
        r4 = r4 + 20;
        r4 = (float) r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        if (r0 >= r2) goto L_0x2691;
    L_0x2688:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r12 = r12 + r0;
        goto L_0x2691;
    L_0x268e:
        r2 = 0;
        r1.infoLayout = r2;
    L_0x2691:
        r0 = r3;
        r9 = r6;
    L_0x2693:
        r8 = 2;
        goto L_0x2760;
    L_0x2696:
        r9 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x26c8;
    L_0x269e:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r6 = r1.isChat;
        if (r6 == 0) goto L_0x26b3;
    L_0x26a6:
        r6 = r59.needDrawAvatar();
        if (r6 == 0) goto L_0x26b3;
    L_0x26ac:
        r6 = r59.isOutOwner();
        if (r6 != 0) goto L_0x26b3;
    L_0x26b2:
        goto L_0x26b5;
    L_0x26b3:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x26b5:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = java.lang.Math.min(r0, r3);
        r1.backgroundWidth = r0;
        goto L_0x26f1;
    L_0x26c8:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r0.x;
        r6 = r1.isChat;
        if (r6 == 0) goto L_0x26dd;
    L_0x26d0:
        r6 = r59.needDrawAvatar();
        if (r6 == 0) goto L_0x26dd;
    L_0x26d6:
        r6 = r59.isOutOwner();
        if (r6 != 0) goto L_0x26dd;
    L_0x26dc:
        goto L_0x26df;
    L_0x26dd:
        r3 = NUM; // 0x42480000 float:50.0 double:5.49408334E-315;
    L_0x26df:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = NUM; // 0x43908000 float:289.0 double:5.60044864E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = java.lang.Math.min(r0, r3);
        r1.backgroundWidth = r0;
    L_0x26f1:
        r0 = r1.backgroundWidth;
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.backgroundWidth = r0;
        r0 = r58.checkNeedDrawShareButton(r59);
        if (r0 == 0) goto L_0x270d;
    L_0x2702:
        r0 = r1.backgroundWidth;
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.backgroundWidth = r0;
    L_0x270d:
        r0 = r1.backgroundWidth;
        r3 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.availableTimeWidth = r0;
        r0 = r1.backgroundWidth;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r0 = r0 - r3;
        r3 = NUM; // 0x43430000 float:195.0 double:5.575354847E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r6 = r1.currentAccount;
        r7 = (float) r0;
        r8 = org.telegram.messenger.AndroidUtilities.density;
        r9 = r7 / r8;
        r9 = (int) r9;
        r10 = (float) r3;
        r8 = r10 / r8;
        r8 = (int) r8;
        r35 = 1;
        r36 = 15;
        r28 = r6;
        r29 = r4;
        r31 = r37;
        r33 = r9;
        r34 = r8;
        r4 = org.telegram.messenger.AndroidUtilities.formapMapUrl(r28, r29, r31, r33, r34, r35, r36);
        r1.currentUrl = r4;
        r4 = org.telegram.messenger.AndroidUtilities.density;
        r7 = r7 / r4;
        r5 = (int) r7;
        r10 = r10 / r4;
        r6 = (int) r10;
        r7 = 15;
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r8 = 2;
        r4 = java.lang.Math.min(r8, r4);
        r2 = org.telegram.messenger.WebFile.createWithGeoPoint(r2, r5, r6, r7, r4);
        r1.currentWebFile = r2;
        r9 = r3;
        r12 = 0;
    L_0x2760:
        r2 = r59.getDialogId();
        r3 = (int) r2;
        if (r3 != 0) goto L_0x2779;
    L_0x2767:
        r2 = org.telegram.messenger.SharedConfig.mapPreviewType;
        if (r2 != 0) goto L_0x276f;
    L_0x276b:
        r1.currentMapProvider = r8;
    L_0x276d:
        r2 = -1;
        goto L_0x2784;
    L_0x276f:
        r3 = 1;
        if (r2 != r3) goto L_0x2775;
    L_0x2772:
        r1.currentMapProvider = r3;
        goto L_0x276d;
    L_0x2775:
        r2 = -1;
        r1.currentMapProvider = r2;
        goto L_0x2784;
    L_0x2779:
        r2 = -1;
        r3 = r14.currentAccount;
        r3 = org.telegram.messenger.MessagesController.getInstance(r3);
        r3 = r3.mapProvider;
        r1.currentMapProvider = r3;
    L_0x2784:
        r3 = r1.currentMapProvider;
        if (r3 != r2) goto L_0x279e;
    L_0x2788:
        r2 = r1.photoImage;
        r3 = 0;
        r4 = 0;
        r5 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r6 = r59.isOutOwner();
        r5 = r5[r6];
        r6 = 0;
        r8 = 0;
        r15 = 8;
        r7 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8);
        goto L_0x27eb;
    L_0x279e:
        r2 = 2;
        r15 = 8;
        if (r3 != r2) goto L_0x27c3;
    L_0x27a3:
        r2 = r1.currentWebFile;
        if (r2 == 0) goto L_0x27eb;
    L_0x27a7:
        r3 = r1.photoImage;
        r4 = org.telegram.messenger.ImageLocation.getForWebFile(r2);
        r5 = 0;
        r2 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r6 = r59.isOutOwner();
        r6 = r2[r6];
        r7 = 0;
        r8 = 0;
        r2 = r3;
        r3 = r4;
        r4 = r5;
        r5 = r6;
        r6 = r7;
        r7 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8);
        goto L_0x27eb;
    L_0x27c3:
        r2 = 3;
        if (r3 == r2) goto L_0x27c9;
    L_0x27c6:
        r2 = 4;
        if (r3 != r2) goto L_0x27d7;
    L_0x27c9:
        r2 = org.telegram.messenger.ImageLoader.getInstance();
        r3 = r1.currentUrl;
        r4 = r1.currentWebFile;
        r2.addTestWebFile(r3, r4);
        r2 = 1;
        r1.addedForTest = r2;
    L_0x27d7:
        r4 = r1.currentUrl;
        if (r4 == 0) goto L_0x27eb;
    L_0x27db:
        r3 = r1.photoImage;
        r5 = 0;
        r2 = org.telegram.ui.ActionBar.Theme.chat_locationDrawable;
        r6 = r59.isOutOwner();
        r6 = r2[r6];
        r7 = 0;
        r8 = 0;
        r3.setImage(r4, r5, r6, r7, r8);
    L_0x27eb:
        r56 = r12;
        r2 = 0;
        r15 = -1;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r30 = 0;
        r12 = r9;
        goto L_0x34b7;
    L_0x27f6:
        r15 = 8;
        r0 = r59.isAnyKindOfSticker();
        if (r0 == 0) goto L_0x298e;
    L_0x27fe:
        r2 = 0;
        r1.drawBackground = r2;
        r0 = r14.type;
        r2 = 13;
        if (r0 != r2) goto L_0x2809;
    L_0x2807:
        r0 = 1;
        goto L_0x280a;
    L_0x2809:
        r0 = 0;
    L_0x280a:
        r2 = 0;
    L_0x280b:
        r3 = r59.getDocument();
        r3 = r3.attributes;
        r3 = r3.size();
        if (r2 >= r3) goto L_0x282f;
    L_0x2817:
        r3 = r59.getDocument();
        r3 = r3.attributes;
        r3 = r3.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r3;
        r4 = r3 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r4 == 0) goto L_0x282c;
    L_0x2827:
        r12 = r3.w;
        r2 = r3.h;
        goto L_0x2831;
    L_0x282c:
        r2 = r2 + 1;
        goto L_0x280b;
    L_0x282f:
        r2 = 0;
        r12 = 0;
    L_0x2831:
        r3 = r59.isAnimatedSticker();
        if (r3 == 0) goto L_0x283f;
    L_0x2837:
        if (r12 != 0) goto L_0x283f;
    L_0x2839:
        if (r2 != 0) goto L_0x283f;
    L_0x283b:
        r12 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
        r2 = 512; // 0x200 float:7.175E-43 double:2.53E-321;
    L_0x283f:
        r3 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r3 == 0) goto L_0x284e;
    L_0x2845:
        r3 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        r3 = (float) r3;
        r4 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
        goto L_0x285b;
    L_0x284e:
        r3 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r3.x;
        r3 = r3.y;
        r3 = java.lang.Math.min(r4, r3);
        r3 = (float) r3;
        r4 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
    L_0x285b:
        r3 = r3 * r4;
        r4 = r59.isAnimatedEmoji();
        if (r4 == 0) goto L_0x287e;
    L_0x2863:
        r4 = r1.currentAccount;
        r4 = org.telegram.messenger.MessagesController.getInstance(r4);
        r4 = r4.animatedEmojisZoom;
        r5 = (float) r12;
        r6 = NUM; // 0x44000000 float:512.0 double:5.63655132E-315;
        r5 = r5 / r6;
        r5 = r5 * r3;
        r5 = r5 * r4;
        r5 = (int) r5;
        r2 = (float) r2;
        r2 = r2 / r6;
        r2 = r2 * r3;
        r2 = r2 * r4;
        r2 = (int) r2;
        r12 = r2;
        r11 = r5;
        goto L_0x28a0;
    L_0x287e:
        if (r12 != 0) goto L_0x2889;
    L_0x2880:
        r2 = (int) r3;
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r12 = r2 + r4;
    L_0x2889:
        r2 = (float) r2;
        r4 = (float) r12;
        r4 = r3 / r4;
        r2 = r2 * r4;
        r2 = (int) r2;
        r4 = (int) r3;
        r5 = (float) r2;
        r6 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1));
        if (r6 <= 0) goto L_0x289e;
    L_0x2896:
        r2 = (float) r4;
        r3 = r3 / r5;
        r2 = r2 * r3;
        r2 = (int) r2;
        r11 = r2;
        r12 = r4;
        goto L_0x28a0;
    L_0x289e:
        r12 = r2;
        r11 = r4;
    L_0x28a0:
        r2 = r59.isAnimatedEmoji();
        if (r2 != 0) goto L_0x28ce;
    L_0x28a6:
        r2 = org.telegram.messenger.SharedConfig.loopStickers;
        if (r2 != 0) goto L_0x28ac;
    L_0x28aa:
        if (r0 == 0) goto L_0x28ce;
    L_0x28ac:
        r2 = java.util.Locale.US;
        r3 = 2;
        r4 = new java.lang.Object[r3];
        r3 = java.lang.Integer.valueOf(r11);
        r5 = 0;
        r4[r5] = r3;
        r3 = java.lang.Integer.valueOf(r12);
        r5 = 1;
        r4[r5] = r3;
        r3 = "%d_%d";
        r2 = java.lang.String.format(r2, r3, r4);
        r3 = r1.photoImage;
        r3.setAutoRepeat(r5);
        r4 = r2;
        r2 = 6;
        r10 = 3;
        goto L_0x2900;
    L_0x28ce:
        r5 = 1;
        r2 = java.util.Locale.US;
        r10 = 3;
        r3 = new java.lang.Object[r10];
        r4 = java.lang.Integer.valueOf(r11);
        r6 = 0;
        r3[r6] = r4;
        r4 = java.lang.Integer.valueOf(r12);
        r3[r5] = r4;
        r4 = r59.toString();
        r5 = 2;
        r3[r5] = r4;
        r4 = "%d_%d_nr_%s";
        r2 = java.lang.String.format(r2, r4, r3);
        r3 = r1.photoImage;
        r4 = r1.delegate;
        r4 = r4.shouldRepeatSticker(r14);
        if (r4 == 0) goto L_0x28fa;
    L_0x28f8:
        r4 = 2;
        goto L_0x28fb;
    L_0x28fa:
        r4 = 3;
    L_0x28fb:
        r3.setAutoRepeat(r4);
        r4 = r2;
        r2 = 6;
    L_0x2900:
        r1.documentAttachType = r2;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r11 - r2;
        r1.availableTimeWidth = r2;
        r2 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r11;
        r1.backgroundWidth = r2;
        r2 = r14.photoThumbs;
        r3 = 40;
        r2 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r2, r3);
        r1.currentPhotoObjectThumb = r2;
        r2 = r14.photoThumbsObject;
        r1.photoParentObject = r2;
        r2 = r14.attachPathExists;
        if (r2 == 0) goto L_0x2953;
    L_0x2927:
        r2 = r1.photoImage;
        r3 = r14.messageOwner;
        r3 = r3.attachPath;
        r3 = org.telegram.messenger.ImageLocation.getForPath(r3);
        r5 = r1.currentPhotoObjectThumb;
        r6 = r1.photoParentObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6);
        r6 = r59.getDocument();
        r7 = r6.size;
        if (r0 == 0) goto L_0x2945;
    L_0x2941:
        r0 = "webp";
        r8 = r0;
        goto L_0x2946;
    L_0x2945:
        r8 = 0;
    L_0x2946:
        r0 = 1;
        r6 = "b1";
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = r59;
        r13 = 3;
        r10 = r0;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x2989;
    L_0x2953:
        r13 = 3;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = r59.getDocument();
        r2 = r2.id;
        r5 = 0;
        r7 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1));
        if (r7 == 0) goto L_0x2989;
    L_0x2962:
        r2 = r1.photoImage;
        r3 = r59.getDocument();
        r3 = org.telegram.messenger.ImageLocation.getForDocument(r3);
        r5 = r1.currentPhotoObjectThumb;
        r6 = r1.photoParentObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6);
        r6 = r59.getDocument();
        r7 = r6.size;
        if (r0 == 0) goto L_0x2980;
    L_0x297c:
        r0 = "webp";
        r8 = r0;
        goto L_0x2981;
    L_0x2980:
        r8 = 0;
    L_0x2981:
        r10 = 1;
        r6 = "b1";
        r9 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
    L_0x2989:
        r0 = r11;
        r2 = 0;
        r15 = -1;
        goto L_0x22e5;
    L_0x298e:
        r13 = 3;
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0 = r14.photoThumbs;
        r2 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2);
        r1.currentPhotoObject = r0;
        r0 = r14.photoThumbsObject;
        r1.photoParentObject = r0;
        r0 = r14.type;
        r2 = 5;
        if (r0 != r2) goto L_0x29b3;
    L_0x29a6:
        r0 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r2 = r59.getDocument();
        r1.documentAttach = r2;
        r2 = 7;
        r1.documentAttachType = r2;
    L_0x29b1:
        r2 = 0;
        goto L_0x29f8;
    L_0x29b3:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x29c5;
    L_0x29b9:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
    L_0x29bd:
        r0 = (float) r0;
        r2 = NUM; // 0x3var_ float:0.7 double:5.23867711E-315;
        r0 = r0 * r2;
        r0 = (int) r0;
        goto L_0x29b1;
    L_0x29c5:
        r0 = r1.currentPhotoObject;
        if (r0 == 0) goto L_0x29ed;
    L_0x29c9:
        r0 = r14.type;
        r2 = 1;
        if (r0 == r2) goto L_0x29d2;
    L_0x29ce:
        if (r0 == r13) goto L_0x29d2;
    L_0x29d0:
        if (r0 != r15) goto L_0x29ed;
    L_0x29d2:
        r0 = r1.currentPhotoObject;
        r2 = r0.w;
        r0 = r0.h;
        if (r2 < r0) goto L_0x29ed;
    L_0x29da:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r2 = r0.x;
        r0 = r0.y;
        r0 = java.lang.Math.min(r2, r0);
        r2 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r2 = 1;
        goto L_0x29f8;
    L_0x29ed:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r2 = r0.x;
        r0 = r0.y;
        r0 = java.lang.Math.min(r2, r0);
        goto L_0x29bd;
    L_0x29f8:
        r3 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r0;
        if (r2 != 0) goto L_0x2a32;
    L_0x2a01:
        r2 = r14.type;
        r4 = 5;
        if (r2 == r4) goto L_0x2a1c;
    L_0x2a06:
        r2 = r58.checkNeedDrawShareButton(r59);
        if (r2 == 0) goto L_0x2a1c;
    L_0x2a0c:
        r2 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r0 - r2;
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 - r4;
        goto L_0x2a1d;
    L_0x2a1c:
        r2 = r0;
    L_0x2a1d:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        if (r0 <= r4) goto L_0x2a27;
    L_0x2a23:
        r0 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
    L_0x2a27:
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        if (r3 <= r4) goto L_0x2a51;
    L_0x2a2d:
        r3 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        goto L_0x2a51;
    L_0x2a32:
        r2 = r1.isChat;
        if (r2 == 0) goto L_0x2a50;
    L_0x2a36:
        r2 = r59.needDrawAvatar();
        if (r2 == 0) goto L_0x2a50;
    L_0x2a3c:
        r2 = r59.isOutOwner();
        if (r2 != 0) goto L_0x2a50;
    L_0x2a42:
        r2 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r0 - r2;
        r57 = r2;
        r2 = r0;
        r0 = r57;
        goto L_0x2a51;
    L_0x2a50:
        r2 = r0;
    L_0x2a51:
        r4 = r14.type;
        r5 = 1;
        if (r4 != r5) goto L_0x2a64;
    L_0x2a56:
        r58.updateSecretTimeText(r59);
        r4 = r14.photoThumbs;
        r5 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5);
        r1.currentPhotoObjectThumb = r4;
        goto L_0x2a77;
    L_0x2a64:
        if (r4 == r13) goto L_0x2a79;
    L_0x2a66:
        if (r4 != r15) goto L_0x2a69;
    L_0x2a68:
        goto L_0x2a79;
    L_0x2a69:
        r5 = 5;
        if (r4 != r5) goto L_0x2a77;
    L_0x2a6c:
        r4 = r14.photoThumbs;
        r5 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5);
        r1.currentPhotoObjectThumb = r4;
        goto L_0x2a8a;
    L_0x2a77:
        r4 = 0;
        goto L_0x2a8b;
    L_0x2a79:
        r4 = 0;
        r1.createDocumentLayout(r4, r14);
        r4 = r14.photoThumbs;
        r5 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r5);
        r1.currentPhotoObjectThumb = r4;
        r58.updateSecretTimeText(r59);
    L_0x2a8a:
        r4 = 1;
    L_0x2a8b:
        r5 = r14.type;
        r6 = 5;
        if (r5 != r6) goto L_0x2a95;
    L_0x2a90:
        r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r8 = r5;
        goto L_0x2b08;
    L_0x2a95:
        r5 = r1.currentPhotoObject;
        if (r5 == 0) goto L_0x2a9a;
    L_0x2a99:
        goto L_0x2a9c;
    L_0x2a9a:
        r5 = r1.currentPhotoObjectThumb;
    L_0x2a9c:
        if (r5 == 0) goto L_0x2aa3;
    L_0x2a9e:
        r12 = r5.w;
        r5 = r5.h;
        goto L_0x2acb;
    L_0x2aa3:
        r5 = r1.documentAttach;
        if (r5 == 0) goto L_0x2ac9;
    L_0x2aa7:
        r5 = r5.attributes;
        r5 = r5.size();
        r6 = 0;
        r7 = 0;
        r12 = 0;
    L_0x2ab0:
        if (r6 >= r5) goto L_0x2ac7;
    L_0x2ab2:
        r8 = r1.documentAttach;
        r8 = r8.attributes;
        r8 = r8.get(r6);
        r8 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r8;
        r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r9 == 0) goto L_0x2ac4;
    L_0x2ac0:
        r12 = r8.w;
        r7 = r8.h;
    L_0x2ac4:
        r6 = r6 + 1;
        goto L_0x2ab0;
    L_0x2ac7:
        r5 = r7;
        goto L_0x2acb;
    L_0x2ac9:
        r5 = 0;
        r12 = 0;
    L_0x2acb:
        r6 = (float) r12;
        r7 = (float) r0;
        r8 = r6 / r7;
        r9 = r6 / r8;
        r9 = (int) r9;
        r5 = (float) r5;
        r8 = r5 / r8;
        r8 = (int) r8;
        if (r9 != 0) goto L_0x2ade;
    L_0x2ad8:
        r9 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
    L_0x2ade:
        if (r8 != 0) goto L_0x2ae6;
    L_0x2ae0:
        r8 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
    L_0x2ae6:
        if (r8 <= r3) goto L_0x2af0;
    L_0x2ae8:
        r5 = (float) r8;
        r6 = (float) r3;
        r5 = r5 / r6;
        r6 = (float) r9;
        r6 = r6 / r5;
        r5 = (int) r6;
        r8 = r3;
        goto L_0x2b08;
    L_0x2af0:
        r10 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        if (r8 >= r10) goto L_0x2b07;
    L_0x2af8:
        r8 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r10 = (float) r8;
        r5 = r5 / r10;
        r6 = r6 / r5;
        r5 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
        if (r5 >= 0) goto L_0x2b07;
    L_0x2b05:
        r5 = (int) r6;
        goto L_0x2b08;
    L_0x2b07:
        r5 = r9;
    L_0x2b08:
        r6 = r1.currentPhotoObject;
        if (r6 == 0) goto L_0x2b1a;
    L_0x2b0c:
        r6 = r6.type;
        r7 = "s";
        r6 = r7.equals(r6);
        if (r6 == 0) goto L_0x2b1a;
    L_0x2b16:
        r6 = 0;
        r1.currentPhotoObject = r6;
        goto L_0x2b1b;
    L_0x2b1a:
        r6 = 0;
    L_0x2b1b:
        r7 = r1.currentPhotoObject;
        if (r7 == 0) goto L_0x2b2d;
    L_0x2b1f:
        r9 = r1.currentPhotoObjectThumb;
        if (r7 != r9) goto L_0x2b2d;
    L_0x2b23:
        r7 = r14.type;
        r9 = 1;
        if (r7 != r9) goto L_0x2b2b;
    L_0x2b28:
        r1.currentPhotoObjectThumb = r6;
        goto L_0x2b2d;
    L_0x2b2b:
        r1.currentPhotoObject = r6;
    L_0x2b2d:
        if (r4 == 0) goto L_0x2b56;
    L_0x2b2f:
        r4 = r59.needDrawBluredPreview();
        if (r4 != 0) goto L_0x2b56;
    L_0x2b35:
        r4 = r1.currentPhotoObject;
        if (r4 == 0) goto L_0x2b3d;
    L_0x2b39:
        r6 = r1.currentPhotoObjectThumb;
        if (r4 != r6) goto L_0x2b56;
    L_0x2b3d:
        r4 = r1.currentPhotoObjectThumb;
        if (r4 == 0) goto L_0x2b4b;
    L_0x2b41:
        r4 = r4.type;
        r6 = "m";
        r4 = r6.equals(r4);
        if (r4 != 0) goto L_0x2b56;
    L_0x2b4b:
        r4 = r1.photoImage;
        r6 = 1;
        r4.setNeedsQualityThumb(r6);
        r4 = r1.photoImage;
        r4.setShouldGenerateQualityThumb(r6);
    L_0x2b56:
        r4 = r1.currentMessagesGroup;
        if (r4 != 0) goto L_0x2b61;
    L_0x2b5a:
        r4 = r14.caption;
        if (r4 == 0) goto L_0x2b61;
    L_0x2b5e:
        r4 = 0;
        r1.mediaBackground = r4;
    L_0x2b61:
        if (r5 == 0) goto L_0x2b65;
    L_0x2b63:
        if (r8 != 0) goto L_0x2bc7;
    L_0x2b65:
        r4 = r14.type;
        if (r4 != r15) goto L_0x2bc7;
    L_0x2b69:
        r4 = 0;
    L_0x2b6a:
        r6 = r59.getDocument();
        r6 = r6.attributes;
        r6 = r6.size();
        if (r4 >= r6) goto L_0x2bc7;
    L_0x2b76:
        r6 = r59.getDocument();
        r6 = r6.attributes;
        r6 = r6.get(r4);
        r6 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r6;
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
        if (r7 != 0) goto L_0x2b8e;
    L_0x2b86:
        r7 = r6 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r7 == 0) goto L_0x2b8b;
    L_0x2b8a:
        goto L_0x2b8e;
    L_0x2b8b:
        r4 = r4 + 1;
        goto L_0x2b6a;
    L_0x2b8e:
        r4 = r6.w;
        r5 = (float) r4;
        r0 = (float) r0;
        r5 = r5 / r0;
        r4 = (float) r4;
        r4 = r4 / r5;
        r4 = (int) r4;
        r7 = r6.h;
        r7 = (float) r7;
        r7 = r7 / r5;
        r5 = (int) r7;
        if (r5 <= r3) goto L_0x2ba4;
    L_0x2b9d:
        r0 = (float) r5;
        r5 = (float) r3;
        r0 = r0 / r5;
        r4 = (float) r4;
        r4 = r4 / r0;
        r5 = (int) r4;
        goto L_0x2bc8;
    L_0x2ba4:
        r3 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        if (r5 >= r3) goto L_0x2bc4;
    L_0x2bac:
        r3 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = r6.h;
        r5 = (float) r5;
        r7 = (float) r3;
        r5 = r5 / r7;
        r6 = r6.w;
        r7 = (float) r6;
        r7 = r7 / r5;
        r0 = (r7 > r0 ? 1 : (r7 == r0 ? 0 : -1));
        if (r0 >= 0) goto L_0x2bc5;
    L_0x2bbf:
        r0 = (float) r6;
        r0 = r0 / r5;
        r0 = (int) r0;
        r5 = r0;
        goto L_0x2bc8;
    L_0x2bc4:
        r3 = r5;
    L_0x2bc5:
        r5 = r4;
        goto L_0x2bc8;
    L_0x2bc7:
        r3 = r8;
    L_0x2bc8:
        if (r5 == 0) goto L_0x2bcc;
    L_0x2bca:
        if (r3 != 0) goto L_0x2bd3;
    L_0x2bcc:
        r0 = NUM; // 0x43160000 float:150.0 double:5.56078426E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r3 = r5;
    L_0x2bd3:
        r0 = r14.type;
        if (r0 != r13) goto L_0x2bec;
    L_0x2bd7:
        r0 = r1.infoWidth;
        r4 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 + r4;
        if (r5 >= r0) goto L_0x2bec;
    L_0x2be2:
        r0 = r1.infoWidth;
        r4 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = r0 + r4;
    L_0x2bec:
        r0 = r1.currentMessagesGroup;
        if (r0 == 0) goto L_0x2CLASSNAME;
    L_0x2bf0:
        r0 = r58.getGroupPhotosWidth();
        r2 = 0;
        r4 = 0;
    L_0x2bf6:
        r6 = r1.currentMessagesGroup;
        r6 = r6.posArray;
        r6 = r6.size();
        if (r2 >= r6) goto L_0x2CLASSNAME;
    L_0x2CLASSNAME:
        r6 = r1.currentMessagesGroup;
        r6 = r6.posArray;
        r6 = r6.get(r2);
        r6 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r6;
        r7 = r6.minY;
        if (r7 != 0) goto L_0x2CLASSNAME;
    L_0x2c0e:
        r7 = (double) r4;
        r4 = r6.pw;
        r6 = r6.leftSpanOffset;
        r4 = r4 + r6;
        r4 = (float) r4;
        r6 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r6;
        r6 = (float) r0;
        r4 = r4 * r6;
        r9 = (double) r4;
        r9 = java.lang.Math.ceil(r9);
        java.lang.Double.isNaN(r7);
        r7 = r7 + r9;
        r4 = (int) r7;
        r2 = r2 + 1;
        goto L_0x2bf6;
    L_0x2CLASSNAME:
        r0 = NUM; // 0x420CLASSNAME float:35.0 double:5.47465589E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r4 = r4 - r0;
        r1.availableTimeWidth = r4;
        goto L_0x2c3b;
    L_0x2CLASSNAME:
        r0 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r2 = r2 - r0;
        r1.availableTimeWidth = r2;
    L_0x2c3b:
        r0 = r14.type;
        r2 = 5;
        if (r0 != r2) goto L_0x2CLASSNAME;
    L_0x2CLASSNAME:
        r0 = r1.availableTimeWidth;
        r6 = (double) r0;
        r0 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r2 = "00:00";
        r0 = r0.measureText(r2);
        r8 = (double) r0;
        r8 = java.lang.Math.ceil(r8);
        r0 = NUM; // 0x41d00000 float:26.0 double:5.455228437E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r10 = (double) r0;
        java.lang.Double.isNaN(r10);
        r8 = r8 + r10;
        java.lang.Double.isNaN(r6);
        r6 = r6 - r8;
        r0 = (int) r6;
        r1.availableTimeWidth = r0;
    L_0x2CLASSNAME:
        r58.measureTime(r59);
        r0 = r1.timeWidth;
        r2 = r59.isOutOwner();
        if (r2 == 0) goto L_0x2CLASSNAME;
    L_0x2c6d:
        r12 = 20;
        goto L_0x2CLASSNAME;
    L_0x2CLASSNAME:
        r12 = 0;
    L_0x2CLASSNAME:
        r12 = r12 + 14;
        r2 = (float) r12;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r2;
        if (r5 >= r0) goto L_0x2c7c;
    L_0x2c7b:
        r5 = r0;
    L_0x2c7c:
        r2 = r59.isRoundVideo();
        if (r2 == 0) goto L_0x2CLASSNAME;
    L_0x2CLASSNAME:
        r3 = java.lang.Math.min(r5, r3);
        r2 = 0;
        r1.drawBackground = r2;
        r2 = r1.photoImage;
        r4 = r3 / 2;
        r2.setRoundRadius(r4);
    L_0x2CLASSNAME:
        r5 = r3;
        goto L_0x2cb4;
    L_0x2CLASSNAME:
        r2 = r59.needDrawBluredPreview();
        if (r2 == 0) goto L_0x2cb4;
    L_0x2CLASSNAME:
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x2ca3;
    L_0x2c9e:
        r2 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        goto L_0x2cad;
    L_0x2ca3:
        r2 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r2.x;
        r2 = r2.y;
        r2 = java.lang.Math.min(r3, r2);
    L_0x2cad:
        r2 = (float) r2;
        r3 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r2 = r2 * r3;
        r3 = (int) r2;
        goto L_0x2CLASSNAME;
    L_0x2cb4:
        r2 = r1.currentMessagesGroup;
        if (r2 == 0) goto L_0x2fd4;
    L_0x2cb8:
        r2 = org.telegram.messenger.AndroidUtilities.displaySize;
        r3 = r2.x;
        r2 = r2.y;
        r2 = java.lang.Math.max(r3, r2);
        r2 = (float) r2;
        r3 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
        r2 = r2 * r3;
        r3 = r58.getGroupPhotosWidth();
        r4 = r1.currentPosition;
        r4 = r4.pw;
        r4 = (float) r4;
        r5 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r4 = r4 / r5;
        r3 = (float) r3;
        r4 = r4 * r3;
        r4 = (double) r4;
        r4 = java.lang.Math.ceil(r4);
        r4 = (int) r4;
        r5 = r1.currentPosition;
        r5 = r5.minY;
        if (r5 == 0) goto L_0x2d78;
    L_0x2ce2:
        r5 = r59.isOutOwner();
        if (r5 == 0) goto L_0x2cf0;
    L_0x2ce8:
        r5 = r1.currentPosition;
        r5 = r5.flags;
        r6 = 1;
        r5 = r5 & r6;
        if (r5 != 0) goto L_0x2cfe;
    L_0x2cf0:
        r5 = r59.isOutOwner();
        if (r5 != 0) goto L_0x2d78;
    L_0x2cf6:
        r5 = r1.currentPosition;
        r5 = r5.flags;
        r6 = 2;
        r5 = r5 & r6;
        if (r5 == 0) goto L_0x2d78;
    L_0x2cfe:
        r5 = 0;
        r6 = 0;
        r7 = 0;
    L_0x2d01:
        r8 = r1.currentMessagesGroup;
        r8 = r8.posArray;
        r8 = r8.size();
        if (r5 >= r8) goto L_0x2d76;
    L_0x2d0b:
        r8 = r1.currentMessagesGroup;
        r8 = r8.posArray;
        r8 = r8.get(r5);
        r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8;
        r9 = r8.minY;
        if (r9 != 0) goto L_0x2d40;
    L_0x2d19:
        r9 = (double) r6;
        r6 = r8.pw;
        r6 = (float) r6;
        r11 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r6 = r6 / r11;
        r6 = r6 * r3;
        r11 = (double) r6;
        r11 = java.lang.Math.ceil(r11);
        r6 = r8.leftSpanOffset;
        if (r6 == 0) goto L_0x2d37;
    L_0x2d2b:
        r6 = (float) r6;
        r8 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r6 = r6 / r8;
        r6 = r6 * r3;
        r13 = (double) r6;
        r13 = java.lang.Math.ceil(r13);
        goto L_0x2d39;
    L_0x2d37:
        r13 = 0;
    L_0x2d39:
        r11 = r11 + r13;
        java.lang.Double.isNaN(r9);
        r9 = r9 + r11;
        r6 = (int) r9;
        goto L_0x2d70;
    L_0x2d40:
        r10 = r1.currentPosition;
        r10 = r10.minY;
        if (r9 != r10) goto L_0x2d6d;
    L_0x2d46:
        r9 = (double) r7;
        r7 = r8.pw;
        r7 = (float) r7;
        r11 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r7 = r7 / r11;
        r7 = r7 * r3;
        r11 = (double) r7;
        r11 = java.lang.Math.ceil(r11);
        r7 = r8.leftSpanOffset;
        if (r7 == 0) goto L_0x2d64;
    L_0x2d58:
        r7 = (float) r7;
        r8 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r7 = r7 / r8;
        r7 = r7 * r3;
        r7 = (double) r7;
        r7 = java.lang.Math.ceil(r7);
        goto L_0x2d66;
    L_0x2d64:
        r7 = 0;
    L_0x2d66:
        r11 = r11 + r7;
        java.lang.Double.isNaN(r9);
        r9 = r9 + r11;
        r7 = (int) r9;
        goto L_0x2d70;
    L_0x2d6d:
        if (r9 <= r10) goto L_0x2d70;
    L_0x2d6f:
        goto L_0x2d76;
    L_0x2d70:
        r5 = r5 + 1;
        r14 = r59;
        r13 = 3;
        goto L_0x2d01;
    L_0x2d76:
        r6 = r6 - r7;
        r4 = r4 + r6;
    L_0x2d78:
        r5 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 - r5;
        r5 = r1.isAvatarVisible;
        if (r5 == 0) goto L_0x2d8a;
    L_0x2d83:
        r5 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 - r5;
    L_0x2d8a:
        r5 = r1.currentPosition;
        r6 = r5.siblingHeights;
        if (r6 == 0) goto L_0x2dba;
    L_0x2d90:
        r5 = 0;
        r6 = 0;
    L_0x2d92:
        r7 = r1.currentPosition;
        r8 = r7.siblingHeights;
        r9 = r8.length;
        if (r5 >= r9) goto L_0x2da7;
    L_0x2d99:
        r7 = r8[r5];
        r7 = r7 * r2;
        r7 = (double) r7;
        r7 = java.lang.Math.ceil(r7);
        r7 = (int) r7;
        r6 = r6 + r7;
        r5 = r5 + 1;
        goto L_0x2d92;
    L_0x2da7:
        r2 = r7.maxY;
        r5 = r7.minY;
        r2 = r2 - r5;
        r5 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r7 = org.telegram.messenger.AndroidUtilities.density;
        r7 = r7 * r5;
        r5 = java.lang.Math.round(r7);
        r2 = r2 * r5;
        r6 = r6 + r2;
        goto L_0x2dc4;
    L_0x2dba:
        r5 = r5.ph;
        r2 = r2 * r5;
        r5 = (double) r2;
        r5 = java.lang.Math.ceil(r5);
        r6 = (int) r5;
    L_0x2dc4:
        r1.backgroundWidth = r4;
        r2 = r1.currentPosition;
        r2 = r2.flags;
        r5 = r2 & 2;
        if (r5 == 0) goto L_0x2dd8;
    L_0x2dce:
        r5 = 1;
        r2 = r2 & r5;
        if (r2 == 0) goto L_0x2dd8;
    L_0x2dd2:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r18);
    L_0x2dd6:
        r4 = r4 - r2;
        goto L_0x2dff;
    L_0x2dd8:
        r2 = r1.currentPosition;
        r2 = r2.flags;
        r5 = r2 & 2;
        if (r5 != 0) goto L_0x2deb;
    L_0x2de0:
        r5 = 1;
        r2 = r2 & r5;
        if (r2 != 0) goto L_0x2deb;
    L_0x2de4:
        r2 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x2dd6;
    L_0x2deb:
        r2 = r1.currentPosition;
        r2 = r2.flags;
        r5 = 2;
        r2 = r2 & r5;
        if (r2 == 0) goto L_0x2df8;
    L_0x2df3:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r19);
        goto L_0x2dd6;
    L_0x2df8:
        r2 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        goto L_0x2dd6;
    L_0x2dff:
        r2 = r1.currentPosition;
        r2 = r2.edge;
        if (r2 != 0) goto L_0x2e0c;
    L_0x2e05:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 + r4;
        r5 = r2;
        goto L_0x2e0d;
    L_0x2e0c:
        r5 = r4;
    L_0x2e0d:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r5 - r2;
        r7 = 0;
        r12 = r7 + r2;
        r2 = r1.currentPosition;
        r2 = r2.flags;
        r7 = r2 & 8;
        if (r7 != 0) goto L_0x2e30;
    L_0x2e1e:
        r7 = r1.currentMessagesGroup;
        r7 = r7.hasSibling;
        if (r7 == 0) goto L_0x2e29;
    L_0x2e24:
        r2 = r2 & 4;
        if (r2 != 0) goto L_0x2e29;
    L_0x2e28:
        goto L_0x2e30;
    L_0x2e29:
        r62 = r5;
        r61 = r6;
        r5 = r4;
        goto L_0x2fcb;
    L_0x2e30:
        r2 = r1.currentPosition;
        r2 = r1.getAdditionalWidthForPosition(r2);
        r12 = r12 + r2;
        r2 = r1.currentMessagesGroup;
        r2 = r2.messages;
        r2 = r2.size();
        r7 = r4;
        r4 = 0;
    L_0x2e41:
        if (r4 >= r2) goto L_0x2fc4;
    L_0x2e43:
        r8 = r1.currentMessagesGroup;
        r8 = r8.messages;
        r8 = r8.get(r4);
        r8 = (org.telegram.messenger.MessageObject) r8;
        r9 = r1.currentMessagesGroup;
        r9 = r9.posArray;
        r9 = r9.get(r4);
        r9 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r9;
        r10 = r1.currentPosition;
        if (r9 == r10) goto L_0x2fa4;
    L_0x2e5b:
        r10 = r9.flags;
        r10 = r10 & r15;
        if (r10 == 0) goto L_0x2fa4;
    L_0x2e60:
        r7 = r9.pw;
        r7 = (float) r7;
        r10 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r7 = r7 / r10;
        r7 = r7 * r3;
        r10 = (double) r7;
        r10 = java.lang.Math.ceil(r10);
        r7 = (int) r10;
        r10 = r9.minY;
        if (r10 == 0) goto L_0x2f1d;
    L_0x2e72:
        r10 = r59.isOutOwner();
        if (r10 == 0) goto L_0x2e7e;
    L_0x2e78:
        r10 = r9.flags;
        r11 = 1;
        r10 = r10 & r11;
        if (r10 != 0) goto L_0x2e8a;
    L_0x2e7e:
        r10 = r59.isOutOwner();
        if (r10 != 0) goto L_0x2f1d;
    L_0x2e84:
        r10 = r9.flags;
        r11 = 2;
        r10 = r10 & r11;
        if (r10 == 0) goto L_0x2f1d;
    L_0x2e8a:
        r10 = 0;
        r11 = 0;
        r13 = 0;
    L_0x2e8d:
        r14 = r1.currentMessagesGroup;
        r14 = r14.posArray;
        r14 = r14.size();
        if (r10 >= r14) goto L_0x2var_;
    L_0x2e97:
        r14 = r1.currentMessagesGroup;
        r14 = r14.posArray;
        r14 = r14.get(r10);
        r14 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r14;
        r15 = r14.minY;
        if (r15 != 0) goto L_0x2ed3;
    L_0x2ea5:
        r62 = r5;
        r61 = r6;
        r5 = (double) r11;
        r11 = r14.pw;
        r11 = (float) r11;
        r15 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r11 = r11 / r15;
        r11 = r11 * r3;
        r22 = r12;
        r11 = (double) r11;
        r11 = java.lang.Math.ceil(r11);
        r14 = r14.leftSpanOffset;
        if (r14 == 0) goto L_0x2ec9;
    L_0x2ebd:
        r14 = (float) r14;
        r15 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r14 = r14 / r15;
        r14 = r14 * r3;
        r14 = (double) r14;
        r14 = java.lang.Math.ceil(r14);
        goto L_0x2ecb;
    L_0x2ec9:
        r14 = 0;
    L_0x2ecb:
        r11 = r11 + r14;
        java.lang.Double.isNaN(r5);
        r5 = r5 + r11;
        r5 = (int) r5;
        r11 = r5;
        goto L_0x2var_;
    L_0x2ed3:
        r62 = r5;
        r61 = r6;
        r22 = r12;
        r5 = r9.minY;
        if (r15 != r5) goto L_0x2var_;
    L_0x2edd:
        r5 = (double) r13;
        r12 = r14.pw;
        r12 = (float) r12;
        r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r12 = r12 / r13;
        r12 = r12 * r3;
        r12 = (double) r12;
        r12 = java.lang.Math.ceil(r12);
        r14 = r14.leftSpanOffset;
        if (r14 == 0) goto L_0x2efb;
    L_0x2eef:
        r14 = (float) r14;
        r15 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r14 = r14 / r15;
        r14 = r14 * r3;
        r14 = (double) r14;
        r14 = java.lang.Math.ceil(r14);
        goto L_0x2efd;
    L_0x2efb:
        r14 = 0;
    L_0x2efd:
        r12 = r12 + r14;
        java.lang.Double.isNaN(r5);
        r5 = r5 + r12;
        r5 = (int) r5;
        r13 = r5;
        goto L_0x2var_;
    L_0x2var_:
        if (r15 <= r5) goto L_0x2var_;
    L_0x2var_:
        goto L_0x2f1a;
    L_0x2var_:
        r10 = r10 + 1;
        r6 = r61;
        r5 = r62;
        r12 = r22;
        r15 = 8;
        goto L_0x2e8d;
    L_0x2var_:
        r62 = r5;
        r61 = r6;
        r22 = r12;
    L_0x2f1a:
        r11 = r11 - r13;
        r7 = r7 + r11;
        goto L_0x2var_;
    L_0x2f1d:
        r62 = r5;
        r61 = r6;
        r22 = r12;
    L_0x2var_:
        r5 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r7 = r7 - r5;
        r5 = r9.flags;
        r6 = r5 & 2;
        if (r6 == 0) goto L_0x2f3a;
    L_0x2var_:
        r5 = r5 & 1;
        if (r5 == 0) goto L_0x2f3a;
    L_0x2var_:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r18);
    L_0x2var_:
        r7 = r7 - r5;
        goto L_0x2f5d;
    L_0x2f3a:
        r5 = r9.flags;
        r6 = r5 & 2;
        if (r6 != 0) goto L_0x2f4b;
    L_0x2var_:
        r5 = r5 & 1;
        if (r5 != 0) goto L_0x2f4b;
    L_0x2var_:
        r5 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        goto L_0x2var_;
    L_0x2f4b:
        r5 = r9.flags;
        r6 = 2;
        r5 = r5 & r6;
        if (r5 == 0) goto L_0x2var_;
    L_0x2var_:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r19);
        goto L_0x2var_;
    L_0x2var_:
        r5 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        goto L_0x2var_;
    L_0x2f5d:
        r5 = r1.isChat;
        if (r5 == 0) goto L_0x2f7a;
    L_0x2var_:
        r5 = r8.isOutOwner();
        if (r5 != 0) goto L_0x2f7a;
    L_0x2var_:
        r5 = r8.needDrawAvatar();
        if (r5 == 0) goto L_0x2f7a;
    L_0x2f6d:
        if (r9 == 0) goto L_0x2var_;
    L_0x2f6f:
        r5 = r9.edge;
        if (r5 == 0) goto L_0x2f7a;
    L_0x2var_:
        r5 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r7 = r7 - r5;
    L_0x2f7a:
        r5 = r1.getAdditionalWidthForPosition(r9);
        r7 = r7 + r5;
        r5 = r9.edge;
        if (r5 != 0) goto L_0x2var_;
    L_0x2var_:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r7 = r7 + r5;
    L_0x2var_:
        r12 = r22 + r7;
        r5 = r9.minX;
        r6 = r1.currentPosition;
        r6 = r6.minX;
        if (r5 < r6) goto L_0x2f9e;
    L_0x2var_:
        r5 = r1.currentMessagesGroup;
        r5 = r5.hasSibling;
        if (r5 == 0) goto L_0x2fac;
    L_0x2var_:
        r5 = r9.minY;
        r6 = r9.maxY;
        if (r5 == r6) goto L_0x2fac;
    L_0x2f9e:
        r5 = r1.captionOffsetX;
        r5 = r5 - r7;
        r1.captionOffsetX = r5;
        goto L_0x2fac;
    L_0x2fa4:
        r62 = r5;
        r61 = r6;
        r22 = r12;
        r12 = r22;
    L_0x2fac:
        r5 = r8.caption;
        if (r5 == 0) goto L_0x2fba;
    L_0x2fb0:
        r6 = r1.currentCaption;
        if (r6 == 0) goto L_0x2fb8;
    L_0x2fb4:
        r6 = 0;
        r1.currentCaption = r6;
        goto L_0x2fca;
    L_0x2fb8:
        r1.currentCaption = r5;
    L_0x2fba:
        r4 = r4 + 1;
        r6 = r61;
        r5 = r62;
        r15 = 8;
        goto L_0x2e41;
    L_0x2fc4:
        r62 = r5;
        r61 = r6;
        r22 = r12;
    L_0x2fca:
        r5 = r7;
    L_0x2fcb:
        r14 = r59;
        r15 = r61;
        r2 = r5;
        r3 = 0;
        r5 = r62;
        goto L_0x3024;
    L_0x2fd4:
        r2 = r14.caption;
        r1.currentCaption = r2;
        r2 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r2 == 0) goto L_0x2fe3;
    L_0x2fde:
        r2 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        goto L_0x2fed;
    L_0x2fe3:
        r2 = org.telegram.messenger.AndroidUtilities.displaySize;
        r4 = r2.x;
        r2 = r2.y;
        r2 = java.lang.Math.min(r4, r2);
    L_0x2fed:
        r2 = (float) r2;
        r4 = NUM; // 0x3var_ float:0.65 double:5.234532584E-315;
        r2 = r2 * r4;
        r2 = (int) r2;
        r4 = r59.needDrawBluredPreview();
        if (r4 != 0) goto L_0x3003;
    L_0x2ffa:
        r4 = r1.currentCaption;
        if (r4 == 0) goto L_0x3003;
    L_0x2ffe:
        if (r5 >= r2) goto L_0x3003;
    L_0x3000:
        r12 = r2;
        r2 = 1;
        goto L_0x300b;
    L_0x3003:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r5 - r2;
        r12 = r2;
        r2 = 0;
    L_0x300b:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r4 = r4 + r5;
        r1.backgroundWidth = r4;
        r4 = r1.mediaBackground;
        if (r4 != 0) goto L_0x3021;
    L_0x3016:
        r4 = r1.backgroundWidth;
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + r6;
        r1.backgroundWidth = r4;
    L_0x3021:
        r15 = r3;
        r3 = r2;
        r2 = r5;
    L_0x3024:
        r4 = r1.currentCaption;
        if (r4 == 0) goto L_0x3135;
    L_0x3028:
        r6 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x312b }
        r7 = 24;
        if (r6 < r7) goto L_0x304f;
    L_0x302e:
        r6 = r4.length();	 Catch:{ Exception -> 0x312b }
        r7 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x312b }
        r8 = 0;
        r4 = android.text.StaticLayout.Builder.obtain(r4, r8, r6, r7, r12);	 Catch:{ Exception -> 0x312b }
        r6 = 1;
        r4 = r4.setBreakStrategy(r6);	 Catch:{ Exception -> 0x312b }
        r4 = r4.setHyphenationFrequency(r8);	 Catch:{ Exception -> 0x312b }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x312b }
        r4 = r4.setAlignment(r6);	 Catch:{ Exception -> 0x312b }
        r4 = r4.build();	 Catch:{ Exception -> 0x312b }
        r1.captionLayout = r4;	 Catch:{ Exception -> 0x312b }
        goto L_0x3066;
    L_0x304f:
        r6 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x312b }
        r29 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x312b }
        r31 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x312b }
        r32 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r33 = 0;
        r34 = 0;
        r27 = r6;
        r28 = r4;
        r30 = r12;
        r27.<init>(r28, r29, r30, r31, r32, r33, r34);	 Catch:{ Exception -> 0x312b }
        r1.captionLayout = r6;	 Catch:{ Exception -> 0x312b }
    L_0x3066:
        r4 = r1.captionLayout;	 Catch:{ Exception -> 0x312b }
        r4 = r4.getLineCount();	 Catch:{ Exception -> 0x312b }
        if (r4 <= 0) goto L_0x3124;
    L_0x306e:
        if (r3 == 0) goto L_0x30a4;
    L_0x3070:
        r6 = 0;
        r1.captionWidth = r6;	 Catch:{ Exception -> 0x312b }
        r6 = 0;
    L_0x3074:
        if (r6 >= r4) goto L_0x309c;
    L_0x3076:
        r7 = r1.captionWidth;	 Catch:{ Exception -> 0x312b }
        r7 = (double) r7;	 Catch:{ Exception -> 0x312b }
        r9 = r1.captionLayout;	 Catch:{ Exception -> 0x312b }
        r9 = r9.getLineWidth(r6);	 Catch:{ Exception -> 0x312b }
        r9 = (double) r9;	 Catch:{ Exception -> 0x312b }
        r9 = java.lang.Math.ceil(r9);	 Catch:{ Exception -> 0x312b }
        r7 = java.lang.Math.max(r7, r9);	 Catch:{ Exception -> 0x312b }
        r7 = (int) r7;	 Catch:{ Exception -> 0x312b }
        r1.captionWidth = r7;	 Catch:{ Exception -> 0x312b }
        r7 = r1.captionLayout;	 Catch:{ Exception -> 0x312b }
        r7 = r7.getLineLeft(r6);	 Catch:{ Exception -> 0x312b }
        r11 = 0;
        r7 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1));
        if (r7 == 0) goto L_0x3099;
    L_0x3096:
        r1.captionWidth = r12;	 Catch:{ Exception -> 0x3122 }
        goto L_0x309d;
    L_0x3099:
        r6 = r6 + 1;
        goto L_0x3074;
    L_0x309c:
        r11 = 0;
    L_0x309d:
        r4 = r1.captionWidth;	 Catch:{ Exception -> 0x3122 }
        if (r4 <= r12) goto L_0x30a7;
    L_0x30a1:
        r1.captionWidth = r12;	 Catch:{ Exception -> 0x3122 }
        goto L_0x30a7;
    L_0x30a4:
        r11 = 0;
        r1.captionWidth = r12;	 Catch:{ Exception -> 0x3122 }
    L_0x30a7:
        r4 = r1.captionLayout;	 Catch:{ Exception -> 0x3122 }
        r4 = r4.getHeight();	 Catch:{ Exception -> 0x3122 }
        r1.captionHeight = r4;	 Catch:{ Exception -> 0x3122 }
        r4 = r1.captionHeight;	 Catch:{ Exception -> 0x3122 }
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x3122 }
        r4 = r4 + r6;
        r1.addedCaptionHeight = r4;	 Catch:{ Exception -> 0x3122 }
        r4 = r1.currentPosition;	 Catch:{ Exception -> 0x3122 }
        if (r4 == 0) goto L_0x30cc;
    L_0x30be:
        r4 = r1.currentPosition;	 Catch:{ Exception -> 0x3122 }
        r4 = r4.flags;	 Catch:{ Exception -> 0x3122 }
        r6 = 8;
        r4 = r4 & r6;
        if (r4 == 0) goto L_0x30c8;
    L_0x30c7:
        goto L_0x30cc;
    L_0x30c8:
        r4 = 0;
        r1.captionLayout = r4;	 Catch:{ Exception -> 0x3122 }
        goto L_0x3125;
    L_0x30cc:
        r4 = r1.addedCaptionHeight;	 Catch:{ Exception -> 0x3122 }
        r6 = 0;
        r12 = r6 + r4;
        r4 = r1.captionWidth;	 Catch:{ Exception -> 0x3120 }
        r6 = org.telegram.messenger.AndroidUtilities.dp(r19);	 Catch:{ Exception -> 0x3120 }
        r6 = r5 - r6;
        r4 = java.lang.Math.max(r4, r6);	 Catch:{ Exception -> 0x3120 }
        r6 = r1.captionLayout;	 Catch:{ Exception -> 0x3120 }
        r7 = r1.captionLayout;	 Catch:{ Exception -> 0x3120 }
        r7 = r7.getLineCount();	 Catch:{ Exception -> 0x3120 }
        r8 = 1;
        r7 = r7 - r8;
        r6 = r6.getLineWidth(r7);	 Catch:{ Exception -> 0x3120 }
        r7 = r1.captionLayout;	 Catch:{ Exception -> 0x3120 }
        r9 = r1.captionLayout;	 Catch:{ Exception -> 0x3120 }
        r9 = r9.getLineCount();	 Catch:{ Exception -> 0x3120 }
        r9 = r9 - r8;
        r7 = r7.getLineLeft(r9);	 Catch:{ Exception -> 0x3120 }
        r6 = r6 + r7;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r20);	 Catch:{ Exception -> 0x3120 }
        r4 = r4 + r7;
        r4 = (float) r4;	 Catch:{ Exception -> 0x3120 }
        r4 = r4 - r6;
        r0 = (float) r0;	 Catch:{ Exception -> 0x3120 }
        r0 = (r4 > r0 ? 1 : (r4 == r0 ? 0 : -1));
        if (r0 >= 0) goto L_0x3119;
    L_0x3105:
        r0 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Exception -> 0x3120 }
        r12 = r12 + r0;
        r0 = r1.addedCaptionHeight;	 Catch:{ Exception -> 0x3120 }
        r4 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x3120 }
        r0 = r0 + r4;
        r1.addedCaptionHeight = r0;	 Catch:{ Exception -> 0x3120 }
        r0 = 1;
        goto L_0x311a;
    L_0x3119:
        r0 = 0;
    L_0x311a:
        r57 = r12;
        r12 = r0;
        r0 = r57;
        goto L_0x3127;
    L_0x3120:
        r0 = move-exception;
        goto L_0x312e;
    L_0x3122:
        r0 = move-exception;
        goto L_0x312d;
    L_0x3124:
        r11 = 0;
    L_0x3125:
        r0 = 0;
        r12 = 0;
    L_0x3127:
        r22 = r0;
        r0 = r12;
        goto L_0x3139;
    L_0x312b:
        r0 = move-exception;
        r11 = 0;
    L_0x312d:
        r12 = 0;
    L_0x312e:
        org.telegram.messenger.FileLog.e(r0);
        r22 = r12;
        r0 = 0;
        goto L_0x3139;
    L_0x3135:
        r11 = 0;
        r0 = 0;
        r22 = 0;
    L_0x3139:
        if (r3 == 0) goto L_0x3164;
    L_0x313b:
        r3 = r1.captionWidth;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r3 = r3 + r4;
        if (r5 >= r3) goto L_0x3164;
    L_0x3144:
        r3 = r1.captionWidth;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r3 = r3 + r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r4 = r4 + r3;
        r1.backgroundWidth = r4;
        r4 = r1.mediaBackground;
        if (r4 != 0) goto L_0x3161;
    L_0x3156:
        r4 = r1.backgroundWidth;
        r5 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 + r5;
        r1.backgroundWidth = r4;
    L_0x3161:
        r27 = r3;
        goto L_0x3166;
    L_0x3164:
        r27 = r5;
    L_0x3166:
        r3 = java.util.Locale.US;
        r4 = 2;
        r5 = new java.lang.Object[r4];
        r2 = (float) r2;
        r4 = org.telegram.messenger.AndroidUtilities.density;
        r2 = r2 / r4;
        r2 = (int) r2;
        r2 = java.lang.Integer.valueOf(r2);
        r12 = 0;
        r5[r12] = r2;
        r2 = (float) r15;
        r4 = org.telegram.messenger.AndroidUtilities.density;
        r2 = r2 / r4;
        r2 = (int) r2;
        r2 = java.lang.Integer.valueOf(r2);
        r4 = 1;
        r5[r4] = r2;
        r2 = "%d_%d";
        r2 = java.lang.String.format(r3, r2, r5);
        r1.currentPhotoFilterThumb = r2;
        r1.currentPhotoFilter = r2;
        r2 = r14.photoThumbs;
        if (r2 == 0) goto L_0x3197;
    L_0x3191:
        r2 = r2.size();
        if (r2 > r4) goto L_0x31a3;
    L_0x3197:
        r2 = r14.type;
        r3 = 3;
        if (r2 == r3) goto L_0x31a3;
    L_0x319c:
        r3 = 8;
        if (r2 == r3) goto L_0x31a3;
    L_0x31a0:
        r3 = 5;
        if (r2 != r3) goto L_0x31e9;
    L_0x31a3:
        r2 = r59.needDrawBluredPreview();
        if (r2 == 0) goto L_0x31d4;
    L_0x31a9:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = r1.currentPhotoFilter;
        r2.append(r3);
        r3 = "_b2";
        r2.append(r3);
        r2 = r2.toString();
        r1.currentPhotoFilter = r2;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = r1.currentPhotoFilterThumb;
        r2.append(r3);
        r3 = "_b2";
        r2.append(r3);
        r2 = r2.toString();
        r1.currentPhotoFilterThumb = r2;
        goto L_0x31e9;
    L_0x31d4:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = r1.currentPhotoFilterThumb;
        r2.append(r3);
        r3 = "_b";
        r2.append(r3);
        r2 = r2.toString();
        r1.currentPhotoFilterThumb = r2;
    L_0x31e9:
        r2 = r14.type;
        r3 = 3;
        if (r2 == r3) goto L_0x31f8;
    L_0x31ee:
        r3 = 8;
        if (r2 == r3) goto L_0x31f8;
    L_0x31f2:
        r3 = 5;
        if (r2 != r3) goto L_0x31f6;
    L_0x31f5:
        goto L_0x31f8;
    L_0x31f6:
        r2 = 0;
        goto L_0x31f9;
    L_0x31f8:
        r2 = 1;
    L_0x31f9:
        r3 = r1.currentPhotoObject;
        if (r3 == 0) goto L_0x3207;
    L_0x31fd:
        if (r2 != 0) goto L_0x3207;
    L_0x31ff:
        r4 = r3.size;
        if (r4 != 0) goto L_0x3207;
    L_0x3203:
        r13 = -1;
        r3.size = r13;
        goto L_0x3208;
    L_0x3207:
        r13 = -1;
    L_0x3208:
        r3 = r1.currentPhotoObjectThumb;
        if (r3 == 0) goto L_0x3214;
    L_0x320c:
        if (r2 != 0) goto L_0x3214;
    L_0x320e:
        r4 = r3.size;
        if (r4 != 0) goto L_0x3214;
    L_0x3212:
        r3.size = r13;
    L_0x3214:
        r3 = org.telegram.messenger.SharedConfig.autoplayVideo;
        if (r3 == 0) goto L_0x325e;
    L_0x3218:
        r3 = r14.type;
        r10 = 3;
        if (r3 != r10) goto L_0x325c;
    L_0x321d:
        r3 = r59.needDrawBluredPreview();
        if (r3 != 0) goto L_0x325c;
    L_0x3223:
        r3 = r1.currentMessageObject;
        r3 = r3.mediaExists;
        if (r3 != 0) goto L_0x323d;
    L_0x3229:
        r3 = r59.canStreamVideo();
        if (r3 == 0) goto L_0x325c;
    L_0x322f:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r4 = r1.currentMessageObject;
        r3 = r3.canDownloadMedia(r4);
        if (r3 == 0) goto L_0x325c;
    L_0x323d:
        r3 = r1.currentPosition;
        if (r3 == 0) goto L_0x3256;
    L_0x3241:
        r3 = r3.flags;
        r4 = r3 & 1;
        if (r4 == 0) goto L_0x324f;
    L_0x3247:
        r23 = 2;
        r3 = r3 & 2;
        if (r3 == 0) goto L_0x3251;
    L_0x324d:
        r3 = 1;
        goto L_0x3252;
    L_0x324f:
        r23 = 2;
    L_0x3251:
        r3 = 0;
    L_0x3252:
        r1.autoPlayingMedia = r3;
        r3 = 1;
        goto L_0x3262;
    L_0x3256:
        r3 = 1;
        r23 = 2;
        r1.autoPlayingMedia = r3;
        goto L_0x3262;
    L_0x325c:
        r3 = 1;
        goto L_0x3260;
    L_0x325e:
        r3 = 1;
        r10 = 3;
    L_0x3260:
        r23 = 2;
    L_0x3262:
        r4 = r1.autoPlayingMedia;
        if (r4 == 0) goto L_0x32b3;
    L_0x3266:
        r2 = r1.photoImage;
        r2.setAllowStartAnimation(r3);
        r2 = r1.photoImage;
        r2.startAnimation();
        r2 = r59.getDocument();
        r3 = r1.photoImage;
        r4 = org.telegram.messenger.ImageLocation.getForDocument(r2);
        r5 = r1.currentPhotoObject;
        r6 = r1.photoParentObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6);
        r6 = r1.currentPhotoFilter;
        r7 = r1.currentPhotoObjectThumb;
        r7 = org.telegram.messenger.ImageLocation.getForDocument(r7, r2);
        r8 = r1.currentPhotoFilterThumb;
        r9 = 0;
        r2 = r59.getDocument();
        r2 = r2.size;
        r24 = 0;
        r28 = 0;
        r29 = "g";
        r30 = r2;
        r2 = r3;
        r3 = r4;
        r4 = r29;
        r29 = 3;
        r10 = r30;
        r30 = 0;
        r11 = r24;
        r12 = r59;
        r24 = r15;
        r15 = -1;
        r13 = r28;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        goto L_0x34b0;
    L_0x32b3:
        r24 = r15;
        r15 = -1;
        r30 = 0;
        r3 = r14.type;
        r4 = 1;
        if (r3 != r4) goto L_0x336d;
    L_0x32bd:
        r3 = r14.useCustomPhoto;
        if (r3 == 0) goto L_0x32d3;
    L_0x32c1:
        r2 = r1.photoImage;
        r3 = r58.getResources();
        r4 = NUM; // 0x7var_c float:1.7945835E38 double:1.0529358093E-314;
        r3 = r3.getDrawable(r4);
        r2.setImageBitmap(r3);
        goto L_0x34b0;
    L_0x32d3:
        r3 = r1.currentPhotoObject;
        if (r3 == 0) goto L_0x3365;
    L_0x32d7:
        r3 = org.telegram.messenger.FileLoader.getAttachFileName(r3);
        r4 = r14.mediaExists;
        if (r4 == 0) goto L_0x32ea;
    L_0x32df:
        r4 = r1.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r4.removeLoadingFileObserver(r1);
        r4 = 1;
        goto L_0x32eb;
    L_0x32ea:
        r4 = 0;
    L_0x32eb:
        if (r4 != 0) goto L_0x3335;
    L_0x32ed:
        r4 = r1.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r5 = r1.currentMessageObject;
        r4 = r4.canDownloadMedia(r5);
        if (r4 != 0) goto L_0x3335;
    L_0x32fb:
        r4 = r1.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r3 = r4.isLoadingFile(r3);
        if (r3 == 0) goto L_0x3308;
    L_0x3307:
        goto L_0x3335;
    L_0x3308:
        r3 = 1;
        r1.photoNotSet = r3;
        r2 = r1.currentPhotoObjectThumb;
        if (r2 == 0) goto L_0x332d;
    L_0x330f:
        r3 = r1.photoImage;
        r4 = 0;
        r5 = 0;
        r6 = r1.photoParentObject;
        r6 = org.telegram.messenger.ImageLocation.getForObject(r2, r6);
        r7 = r1.currentPhotoFilterThumb;
        r8 = 0;
        r9 = 0;
        r10 = r1.currentMessageObject;
        r2 = r10.shouldEncryptPhotoOrVideo();
        if (r2 == 0) goto L_0x3327;
    L_0x3325:
        r11 = 2;
        goto L_0x3328;
    L_0x3327:
        r11 = 0;
    L_0x3328:
        r3.setImage(r4, r5, r6, r7, r8, r9, r10, r11);
        goto L_0x34b0;
    L_0x332d:
        r2 = r1.photoImage;
        r3 = 0;
        r2.setImageBitmap(r3);
        goto L_0x34b0;
    L_0x3335:
        r4 = r1.photoImage;
        r3 = r1.currentPhotoObject;
        r5 = r1.photoParentObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r3, r5);
        r6 = r1.currentPhotoFilter;
        r3 = r1.currentPhotoObjectThumb;
        r7 = r1.photoParentObject;
        r7 = org.telegram.messenger.ImageLocation.getForObject(r3, r7);
        r8 = r1.currentPhotoFilterThumb;
        if (r2 == 0) goto L_0x334f;
    L_0x334d:
        r9 = 0;
        goto L_0x3354;
    L_0x334f:
        r2 = r1.currentPhotoObject;
        r12 = r2.size;
        r9 = r12;
    L_0x3354:
        r10 = 0;
        r11 = r1.currentMessageObject;
        r2 = r11.shouldEncryptPhotoOrVideo();
        if (r2 == 0) goto L_0x335f;
    L_0x335d:
        r12 = 2;
        goto L_0x3360;
    L_0x335f:
        r12 = 0;
    L_0x3360:
        r4.setImage(r5, r6, r7, r8, r9, r10, r11, r12);
        goto L_0x34b0;
    L_0x3365:
        r2 = r1.photoImage;
        r3 = 0;
        r2.setImageBitmap(r3);
        goto L_0x34b0;
    L_0x336d:
        r2 = 8;
        if (r3 == r2) goto L_0x339f;
    L_0x3371:
        r2 = 5;
        if (r3 != r2) goto L_0x3375;
    L_0x3374:
        goto L_0x339f;
    L_0x3375:
        r2 = r1.photoImage;
        r3 = r1.currentPhotoObject;
        r4 = r1.photoParentObject;
        r3 = org.telegram.messenger.ImageLocation.getForObject(r3, r4);
        r4 = r1.currentPhotoFilter;
        r5 = r1.currentPhotoObjectThumb;
        r6 = r1.photoParentObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6);
        r6 = r1.currentPhotoFilterThumb;
        r7 = 0;
        r8 = 0;
        r9 = r1.currentMessageObject;
        r9 = r9.shouldEncryptPhotoOrVideo();
        if (r9 == 0) goto L_0x3397;
    L_0x3395:
        r10 = 2;
        goto L_0x3398;
    L_0x3397:
        r10 = 0;
    L_0x3398:
        r9 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x34b0;
    L_0x339f:
        r2 = r59.getDocument();
        r2 = org.telegram.messenger.FileLoader.getAttachFileName(r2);
        r3 = r14.attachPathExists;
        if (r3 == 0) goto L_0x33b6;
    L_0x33ab:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r3.removeLoadingFileObserver(r1);
        r3 = 1;
        goto L_0x33bd;
    L_0x33b6:
        r3 = r14.mediaExists;
        if (r3 == 0) goto L_0x33bc;
    L_0x33ba:
        r3 = 2;
        goto L_0x33bd;
    L_0x33bc:
        r3 = 0;
    L_0x33bd:
        r4 = r59.getDocument();
        r4 = org.telegram.messenger.MessageObject.isGifDocument(r4);
        if (r4 != 0) goto L_0x33cf;
    L_0x33c7:
        r4 = r14.type;
        r5 = 5;
        if (r4 != r5) goto L_0x33cd;
    L_0x33cc:
        goto L_0x33cf;
    L_0x33cd:
        r12 = 0;
        goto L_0x33db;
    L_0x33cf:
        r4 = r1.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r5 = r1.currentMessageObject;
        r12 = r4.canDownloadMedia(r5);
    L_0x33db:
        r4 = r59.isSending();
        if (r4 != 0) goto L_0x3492;
    L_0x33e1:
        r4 = r59.isEditing();
        if (r4 != 0) goto L_0x3492;
    L_0x33e7:
        if (r3 != 0) goto L_0x33f7;
    L_0x33e9:
        r4 = r1.currentAccount;
        r4 = org.telegram.messenger.FileLoader.getInstance(r4);
        r2 = r4.isLoadingFile(r2);
        if (r2 != 0) goto L_0x33f7;
    L_0x33f5:
        if (r12 == 0) goto L_0x3492;
    L_0x33f7:
        r2 = 1;
        if (r3 == r2) goto L_0x343c;
    L_0x33fa:
        r4 = r59.needDrawBluredPreview();
        if (r4 != 0) goto L_0x343c;
    L_0x3400:
        if (r3 != 0) goto L_0x340a;
    L_0x3402:
        r4 = r59.canStreamVideo();
        if (r4 == 0) goto L_0x343c;
    L_0x3408:
        if (r12 == 0) goto L_0x343c;
    L_0x340a:
        r1.autoPlayingMedia = r2;
        r2 = r1.photoImage;
        r3 = r59.getDocument();
        r3 = org.telegram.messenger.ImageLocation.getForDocument(r3);
        r4 = r1.currentPhotoObject;
        r5 = r1.photoParentObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r4, r5);
        r6 = r1.currentPhotoFilter;
        r4 = r1.currentPhotoObjectThumb;
        r7 = r1.photoParentObject;
        r7 = org.telegram.messenger.ImageLocation.getForObject(r4, r7);
        r8 = r1.currentPhotoFilterThumb;
        r9 = 0;
        r4 = r59.getDocument();
        r10 = r4.size;
        r11 = 0;
        r13 = 0;
        r4 = "g";
        r12 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        goto L_0x34b0;
    L_0x343c:
        if (r3 != r2) goto L_0x3464;
    L_0x343e:
        r2 = r1.photoImage;
        r3 = r59.isSendError();
        if (r3 == 0) goto L_0x3448;
    L_0x3446:
        r3 = 0;
        goto L_0x344c;
    L_0x3448:
        r3 = r14.messageOwner;
        r3 = r3.attachPath;
    L_0x344c:
        r3 = org.telegram.messenger.ImageLocation.getForPath(r3);
        r4 = 0;
        r5 = r1.currentPhotoObjectThumb;
        r6 = r1.photoParentObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6);
        r6 = r1.currentPhotoFilterThumb;
        r7 = 0;
        r8 = 0;
        r10 = 0;
        r9 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
        goto L_0x34b0;
    L_0x3464:
        r2 = r1.photoImage;
        r3 = r59.getDocument();
        r3 = org.telegram.messenger.ImageLocation.getForDocument(r3);
        r4 = 0;
        r5 = r1.currentPhotoObject;
        r6 = r1.photoParentObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6);
        r6 = r1.currentPhotoFilter;
        r7 = r1.currentPhotoObjectThumb;
        r8 = r1.photoParentObject;
        r7 = org.telegram.messenger.ImageLocation.getForObject(r7, r8);
        r8 = r1.currentPhotoFilterThumb;
        r9 = 0;
        r10 = r59.getDocument();
        r10 = r10.size;
        r11 = 0;
        r13 = 0;
        r12 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        goto L_0x34b0;
    L_0x3492:
        r2 = r1.photoImage;
        r3 = r1.currentPhotoObject;
        r4 = r1.photoParentObject;
        r3 = org.telegram.messenger.ImageLocation.getForObject(r3, r4);
        r4 = r1.currentPhotoFilter;
        r5 = r1.currentPhotoObjectThumb;
        r6 = r1.photoParentObject;
        r5 = org.telegram.messenger.ImageLocation.getForObject(r5, r6);
        r6 = r1.currentPhotoFilterThumb;
        r7 = 0;
        r8 = 0;
        r10 = 0;
        r9 = r59;
        r2.setImage(r3, r4, r5, r6, r7, r8, r9, r10);
    L_0x34b0:
        r2 = r0;
        r56 = r22;
        r12 = r24;
        r0 = r27;
    L_0x34b7:
        r58.setMessageObjectInternal(r59);
        r3 = r1.drawForwardedName;
        if (r3 == 0) goto L_0x34dd;
    L_0x34be:
        r3 = r59.needDrawForwarded();
        if (r3 == 0) goto L_0x34dd;
    L_0x34c4:
        r3 = r1.currentPosition;
        if (r3 == 0) goto L_0x34cc;
    L_0x34c8:
        r3 = r3.minY;
        if (r3 != 0) goto L_0x34dd;
    L_0x34cc:
        r3 = r14.type;
        r4 = 5;
        if (r3 == r4) goto L_0x34f2;
    L_0x34d1:
        r3 = r1.namesOffset;
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r4;
        r1.namesOffset = r3;
        goto L_0x34f2;
    L_0x34dd:
        r3 = r1.drawNameLayout;
        if (r3 == 0) goto L_0x34f2;
    L_0x34e1:
        r3 = r14.messageOwner;
        r3 = r3.reply_to_msg_id;
        if (r3 != 0) goto L_0x34f2;
    L_0x34e7:
        r3 = r1.namesOffset;
        r4 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r4;
        r1.namesOffset = r3;
    L_0x34f2:
        r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r12;
        r4 = r1.namesOffset;
        r3 = r3 + r4;
        r3 = r3 + r56;
        r1.totalHeight = r3;
        r3 = r1.currentPosition;
        if (r3 == 0) goto L_0x3516;
    L_0x3504:
        r3 = r3.flags;
        r4 = 8;
        r3 = r3 & r4;
        if (r3 != 0) goto L_0x3516;
    L_0x350b:
        r3 = r1.totalHeight;
        r4 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r1.totalHeight = r3;
    L_0x3516:
        r3 = r1.currentPosition;
        if (r3 == 0) goto L_0x3549;
    L_0x351a:
        r3 = r1.getAdditionalWidthForPosition(r3);
        r0 = r0 + r3;
        r3 = r1.currentPosition;
        r3 = r3.flags;
        r3 = r3 & 4;
        if (r3 != 0) goto L_0x3538;
    L_0x3527:
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r12 = r12 + r3;
        r3 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = 0;
        r3 = 0 - r3;
        goto L_0x353a;
    L_0x3538:
        r4 = 0;
        r3 = 0;
    L_0x353a:
        r5 = r1.currentPosition;
        r5 = r5.flags;
        r6 = 8;
        r5 = r5 & r6;
        if (r5 != 0) goto L_0x354b;
    L_0x3543:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r12 = r12 + r5;
        goto L_0x354b;
    L_0x3549:
        r4 = 0;
        r3 = 0;
    L_0x354b:
        r5 = r1.drawPinnedTop;
        if (r5 == 0) goto L_0x3558;
    L_0x354f:
        r5 = r1.namesOffset;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r5 = r5 - r6;
        r1.namesOffset = r5;
    L_0x3558:
        r5 = r1.currentPosition;
        if (r5 == 0) goto L_0x3582;
    L_0x355c:
        r5 = r1.namesOffset;
        if (r5 <= 0) goto L_0x3570;
    L_0x3560:
        r5 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = r1.totalHeight;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r6 = r6 - r7;
        r1.totalHeight = r6;
        goto L_0x35a7;
    L_0x3570:
        r5 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = r1.totalHeight;
        r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 - r7;
        r1.totalHeight = r6;
        goto L_0x35a7;
    L_0x3582:
        r5 = r1.namesOffset;
        if (r5 <= 0) goto L_0x3596;
    L_0x3586:
        r5 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = r1.totalHeight;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r6 = r6 - r7;
        r1.totalHeight = r6;
        goto L_0x35a7;
    L_0x3596:
        r5 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = r1.totalHeight;
        r7 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 - r7;
        r1.totalHeight = r6;
    L_0x35a7:
        r6 = r1.photoImage;
        r7 = r1.namesOffset;
        r5 = r5 + r7;
        r5 = r5 + r3;
        r6.setImageCoords(r4, r5, r0, r12);
        r58.invalidate();
        r9 = r2;
    L_0x35b4:
        r0 = r1.currentPosition;
        if (r0 != 0) goto L_0x36ae;
    L_0x35b8:
        r0 = r59.isAnyKindOfSticker();
        if (r0 != 0) goto L_0x36ae;
    L_0x35be:
        r0 = r1.addedCaptionHeight;
        if (r0 != 0) goto L_0x36ae;
    L_0x35c2:
        r0 = r1.captionLayout;
        if (r0 != 0) goto L_0x3622;
    L_0x35c6:
        r0 = r14.caption;
        if (r0 == 0) goto L_0x3622;
    L_0x35ca:
        r1.currentCaption = r0;	 Catch:{ Exception -> 0x361e }
        r0 = r1.backgroundWidth;	 Catch:{ Exception -> 0x361e }
        r2 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x361e }
        r0 = r0 - r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r19);	 Catch:{ Exception -> 0x361e }
        r0 = r0 - r2;
        r2 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x361e }
        r3 = 24;
        if (r2 < r3) goto L_0x3604;
    L_0x35e0:
        r2 = r14.caption;	 Catch:{ Exception -> 0x361e }
        r3 = r14.caption;	 Catch:{ Exception -> 0x361e }
        r3 = r3.length();	 Catch:{ Exception -> 0x361e }
        r5 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x361e }
        r0 = android.text.StaticLayout.Builder.obtain(r2, r4, r3, r5, r0);	 Catch:{ Exception -> 0x361e }
        r2 = 1;
        r0 = r0.setBreakStrategy(r2);	 Catch:{ Exception -> 0x361e }
        r0 = r0.setHyphenationFrequency(r4);	 Catch:{ Exception -> 0x361e }
        r2 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x361e }
        r0 = r0.setAlignment(r2);	 Catch:{ Exception -> 0x361e }
        r0 = r0.build();	 Catch:{ Exception -> 0x361e }
        r1.captionLayout = r0;	 Catch:{ Exception -> 0x361e }
        goto L_0x3622;
    L_0x3604:
        r2 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x361e }
        r3 = r14.caption;	 Catch:{ Exception -> 0x361e }
        r33 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;	 Catch:{ Exception -> 0x361e }
        r35 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x361e }
        r36 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r37 = 0;
        r38 = 0;
        r31 = r2;
        r32 = r3;
        r34 = r0;
        r31.<init>(r32, r33, r34, r35, r36, r37, r38);	 Catch:{ Exception -> 0x361e }
        r1.captionLayout = r2;	 Catch:{ Exception -> 0x361e }
        goto L_0x3622;
    L_0x361e:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x3622:
        r0 = r1.captionLayout;
        if (r0 == 0) goto L_0x36ae;
    L_0x3626:
        r0 = r1.backgroundWidth;	 Catch:{ Exception -> 0x36aa }
        r2 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x36aa }
        r0 = r0 - r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r19);	 Catch:{ Exception -> 0x36aa }
        r2 = r0 - r2;
        r3 = r1.captionLayout;	 Catch:{ Exception -> 0x36aa }
        if (r3 == 0) goto L_0x36ae;
    L_0x3639:
        r3 = r1.captionLayout;	 Catch:{ Exception -> 0x36aa }
        r3 = r3.getLineCount();	 Catch:{ Exception -> 0x36aa }
        if (r3 <= 0) goto L_0x36ae;
    L_0x3641:
        r1.captionWidth = r2;	 Catch:{ Exception -> 0x36aa }
        r2 = r1.timeWidth;	 Catch:{ Exception -> 0x36aa }
        r3 = r59.isOutOwner();	 Catch:{ Exception -> 0x36aa }
        if (r3 == 0) goto L_0x3652;
    L_0x364b:
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x36aa }
        goto L_0x3653;
    L_0x3652:
        r12 = 0;
    L_0x3653:
        r2 = r2 + r12;
        r3 = r1.captionLayout;	 Catch:{ Exception -> 0x36aa }
        r3 = r3.getHeight();	 Catch:{ Exception -> 0x36aa }
        r1.captionHeight = r3;	 Catch:{ Exception -> 0x36aa }
        r3 = r1.totalHeight;	 Catch:{ Exception -> 0x36aa }
        r5 = r1.captionHeight;	 Catch:{ Exception -> 0x36aa }
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x36aa }
        r5 = r5 + r6;
        r3 = r3 + r5;
        r1.totalHeight = r3;	 Catch:{ Exception -> 0x36aa }
        r3 = r1.captionLayout;	 Catch:{ Exception -> 0x36aa }
        r5 = r1.captionLayout;	 Catch:{ Exception -> 0x36aa }
        r5 = r5.getLineCount();	 Catch:{ Exception -> 0x36aa }
        r6 = 1;
        r5 = r5 - r6;
        r3 = r3.getLineWidth(r5);	 Catch:{ Exception -> 0x36aa }
        r5 = r1.captionLayout;	 Catch:{ Exception -> 0x36aa }
        r7 = r1.captionLayout;	 Catch:{ Exception -> 0x36aa }
        r7 = r7.getLineCount();	 Catch:{ Exception -> 0x36aa }
        r7 = r7 - r6;
        r5 = r5.getLineLeft(r7);	 Catch:{ Exception -> 0x36aa }
        r3 = r3 + r5;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r18);	 Catch:{ Exception -> 0x36aa }
        r0 = r0 - r5;
        r0 = (float) r0;	 Catch:{ Exception -> 0x36aa }
        r0 = r0 - r3;
        r2 = (float) r2;	 Catch:{ Exception -> 0x36aa }
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 >= 0) goto L_0x36ae;
    L_0x3692:
        r0 = r1.totalHeight;	 Catch:{ Exception -> 0x36aa }
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x36aa }
        r0 = r0 + r2;
        r1.totalHeight = r0;	 Catch:{ Exception -> 0x36aa }
        r0 = r1.captionHeight;	 Catch:{ Exception -> 0x36aa }
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x36aa }
        r0 = r0 + r2;
        r1.captionHeight = r0;	 Catch:{ Exception -> 0x36aa }
        r9 = 2;
        goto L_0x36ae;
    L_0x36aa:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x36ae:
        r0 = r1.captionLayout;
        if (r0 != 0) goto L_0x36c8;
    L_0x36b2:
        r0 = r1.widthBeforeNewTimeLine;
        if (r0 == r15) goto L_0x36c8;
    L_0x36b6:
        r2 = r1.availableTimeWidth;
        r2 = r2 - r0;
        r0 = r1.timeWidth;
        if (r2 >= r0) goto L_0x36c8;
    L_0x36bd:
        r0 = r1.totalHeight;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r2;
        r1.totalHeight = r0;
    L_0x36c8:
        r0 = r1.currentMessageObject;
        r2 = r0.eventId;
        r5 = 0;
        r7 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1));
        if (r7 == 0) goto L_0x37f0;
    L_0x36d2:
        r0 = r0.isMediaEmpty();
        if (r0 != 0) goto L_0x37f0;
    L_0x36d8:
        r0 = r1.currentMessageObject;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0.webpage;
        if (r0 == 0) goto L_0x37f0;
    L_0x36e2:
        r0 = r1.backgroundWidth;
        r2 = NUM; // 0x42240000 float:41.0 double:5.48242687E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r0 - r2;
        r3 = 1;
        r1.hasOldCaptionPreview = r3;
        r1.linkPreviewHeight = r4;
        r0 = r1.currentMessageObject;
        r0 = r0.messageOwner;
        r0 = r0.media;
        r3 = r0.webpage;
        r0 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x374e }
        r5 = r3.site_name;	 Catch:{ Exception -> 0x374e }
        r0 = r0.measureText(r5);	 Catch:{ Exception -> 0x374e }
        r0 = r0 + r21;
        r5 = (double) r0;	 Catch:{ Exception -> 0x374e }
        r5 = java.lang.Math.ceil(r5);	 Catch:{ Exception -> 0x374e }
        r0 = (int) r5;	 Catch:{ Exception -> 0x374e }
        r1.siteNameWidth = r0;	 Catch:{ Exception -> 0x374e }
        r5 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x374e }
        r6 = r3.site_name;	 Catch:{ Exception -> 0x374e }
        r33 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x374e }
        r34 = java.lang.Math.min(r0, r2);	 Catch:{ Exception -> 0x374e }
        r35 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x374e }
        r36 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r37 = 0;
        r38 = 0;
        r31 = r5;
        r32 = r6;
        r31.<init>(r32, r33, r34, r35, r36, r37, r38);	 Catch:{ Exception -> 0x374e }
        r1.siteNameLayout = r5;	 Catch:{ Exception -> 0x374e }
        r0 = r1.siteNameLayout;	 Catch:{ Exception -> 0x374e }
        r0 = r0.getLineLeft(r4);	 Catch:{ Exception -> 0x374e }
        r0 = (r0 > r30 ? 1 : (r0 == r30 ? 0 : -1));
        if (r0 == 0) goto L_0x3732;
    L_0x3730:
        r0 = 1;
        goto L_0x3733;
    L_0x3732:
        r0 = 0;
    L_0x3733:
        r1.siteNameRtl = r0;	 Catch:{ Exception -> 0x374e }
        r0 = r1.siteNameLayout;	 Catch:{ Exception -> 0x374e }
        r5 = r1.siteNameLayout;	 Catch:{ Exception -> 0x374e }
        r5 = r5.getLineCount();	 Catch:{ Exception -> 0x374e }
        r6 = 1;
        r5 = r5 - r6;
        r0 = r0.getLineBottom(r5);	 Catch:{ Exception -> 0x374e }
        r5 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x374e }
        r5 = r5 + r0;
        r1.linkPreviewHeight = r5;	 Catch:{ Exception -> 0x374e }
        r5 = r1.totalHeight;	 Catch:{ Exception -> 0x374e }
        r5 = r5 + r0;
        r1.totalHeight = r5;	 Catch:{ Exception -> 0x374e }
        goto L_0x3752;
    L_0x374e:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x3752:
        r1.descriptionX = r4;	 Catch:{ Exception -> 0x37c5 }
        r0 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x37c5 }
        if (r0 == 0) goto L_0x3761;
    L_0x3758:
        r0 = r1.totalHeight;	 Catch:{ Exception -> 0x37c5 }
        r5 = org.telegram.messenger.AndroidUtilities.dp(r20);	 Catch:{ Exception -> 0x37c5 }
        r0 = r0 + r5;
        r1.totalHeight = r0;	 Catch:{ Exception -> 0x37c5 }
    L_0x3761:
        r0 = r3.description;	 Catch:{ Exception -> 0x37c5 }
        r32 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x37c5 }
        r34 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x37c5 }
        r35 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r21);	 Catch:{ Exception -> 0x37c5 }
        r3 = (float) r3;	 Catch:{ Exception -> 0x37c5 }
        r37 = 0;
        r38 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x37c5 }
        r40 = 6;
        r31 = r0;
        r33 = r2;
        r36 = r3;
        r39 = r2;
        r0 = org.telegram.ui.Components.StaticLayoutEx.createStaticLayout(r31, r32, r33, r34, r35, r36, r37, r38, r39, r40);	 Catch:{ Exception -> 0x37c5 }
        r1.descriptionLayout = r0;	 Catch:{ Exception -> 0x37c5 }
        r0 = r1.descriptionLayout;	 Catch:{ Exception -> 0x37c5 }
        r2 = r1.descriptionLayout;	 Catch:{ Exception -> 0x37c5 }
        r2 = r2.getLineCount();	 Catch:{ Exception -> 0x37c5 }
        r3 = 1;
        r2 = r2 - r3;
        r0 = r0.getLineBottom(r2);	 Catch:{ Exception -> 0x37c5 }
        r2 = r1.linkPreviewHeight;	 Catch:{ Exception -> 0x37c5 }
        r2 = r2 + r0;
        r1.linkPreviewHeight = r2;	 Catch:{ Exception -> 0x37c5 }
        r2 = r1.totalHeight;	 Catch:{ Exception -> 0x37c5 }
        r2 = r2 + r0;
        r1.totalHeight = r2;	 Catch:{ Exception -> 0x37c5 }
        r0 = 0;
    L_0x379b:
        r2 = r1.descriptionLayout;	 Catch:{ Exception -> 0x37c5 }
        r2 = r2.getLineCount();	 Catch:{ Exception -> 0x37c5 }
        if (r0 >= r2) goto L_0x37c9;
    L_0x37a3:
        r2 = r1.descriptionLayout;	 Catch:{ Exception -> 0x37c5 }
        r2 = r2.getLineLeft(r0);	 Catch:{ Exception -> 0x37c5 }
        r2 = (double) r2;	 Catch:{ Exception -> 0x37c5 }
        r2 = java.lang.Math.ceil(r2);	 Catch:{ Exception -> 0x37c5 }
        r2 = (int) r2;	 Catch:{ Exception -> 0x37c5 }
        if (r2 == 0) goto L_0x37c2;
    L_0x37b1:
        r3 = r1.descriptionX;	 Catch:{ Exception -> 0x37c5 }
        if (r3 != 0) goto L_0x37b9;
    L_0x37b5:
        r2 = -r2;
        r1.descriptionX = r2;	 Catch:{ Exception -> 0x37c5 }
        goto L_0x37c2;
    L_0x37b9:
        r3 = r1.descriptionX;	 Catch:{ Exception -> 0x37c5 }
        r2 = -r2;
        r2 = java.lang.Math.max(r3, r2);	 Catch:{ Exception -> 0x37c5 }
        r1.descriptionX = r2;	 Catch:{ Exception -> 0x37c5 }
    L_0x37c2:
        r0 = r0 + 1;
        goto L_0x379b;
    L_0x37c5:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x37c9:
        r0 = r1.totalHeight;
        r2 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r2;
        r1.totalHeight = r0;
        if (r9 == 0) goto L_0x37f0;
    L_0x37d6:
        r0 = r1.totalHeight;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r1.totalHeight = r0;
        r2 = 2;
        if (r9 != r2) goto L_0x37f1;
    L_0x37e4:
        r0 = r1.captionHeight;
        r3 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.captionHeight = r0;
        goto L_0x37f1;
    L_0x37f0:
        r2 = 2;
    L_0x37f1:
        r0 = r1.botButtons;
        r0.clear();
        if (r16 == 0) goto L_0x3805;
    L_0x37f8:
        r0 = r1.botButtonsByData;
        r0.clear();
        r0 = r1.botButtonsByPosition;
        r0.clear();
        r3 = 0;
        r1.botButtonsLayout = r3;
    L_0x3805:
        r0 = r1.currentPosition;
        if (r0 != 0) goto L_0x3a0e;
    L_0x3809:
        r0 = r14.messageOwner;
        r0 = r0.reply_markup;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
        if (r3 == 0) goto L_0x3a0e;
    L_0x3811:
        r0 = r0.rows;
        r0 = r0.size();
        r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 * r0;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r3 = r3 + r5;
        r1.keyboardHeight = r3;
        r1.substractBackgroundHeight = r3;
        r3 = r1.backgroundWidth;
        r5 = r1.mediaBackground;
        if (r5 == 0) goto L_0x382f;
    L_0x382e:
        goto L_0x3833;
    L_0x382f:
        r10 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r30 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
    L_0x3833:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r30);
        r3 = r3 - r5;
        r1.widthForButtons = r3;
        r3 = r14.wantedBotKeyboardWidth;
        r5 = r1.widthForButtons;
        if (r3 <= r5) goto L_0x3885;
    L_0x3840:
        r3 = r1.isChat;
        if (r3 == 0) goto L_0x3853;
    L_0x3844:
        r3 = r59.needDrawAvatar();
        if (r3 == 0) goto L_0x3853;
    L_0x384a:
        r3 = r59.isOutOwner();
        if (r3 != 0) goto L_0x3853;
    L_0x3850:
        r3 = NUM; // 0x42780000 float:62.0 double:5.5096253E-315;
        goto L_0x3855;
    L_0x3853:
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x3855:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = -r3;
        r5 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r5 == 0) goto L_0x3865;
    L_0x3860:
        r5 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        goto L_0x3876;
    L_0x3865:
        r5 = org.telegram.messenger.AndroidUtilities.displaySize;
        r6 = r5.x;
        r5 = r5.y;
        r5 = java.lang.Math.min(r6, r5);
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
    L_0x3876:
        r3 = r3 + r5;
        r5 = r1.backgroundWidth;
        r6 = r14.wantedBotKeyboardWidth;
        r3 = java.lang.Math.min(r6, r3);
        r3 = java.lang.Math.max(r5, r3);
        r1.widthForButtons = r3;
    L_0x3885:
        r3 = new java.util.HashMap;
        r5 = r1.botButtonsByData;
        r3.<init>(r5);
        r5 = r14.botButtonsLayout;
        if (r5 == 0) goto L_0x38a6;
    L_0x3890:
        r6 = r1.botButtonsLayout;
        if (r6 == 0) goto L_0x38a6;
    L_0x3894:
        r5 = r5.toString();
        r5 = r6.equals(r5);
        if (r5 == 0) goto L_0x38a6;
    L_0x389e:
        r15 = new java.util.HashMap;
        r5 = r1.botButtonsByPosition;
        r15.<init>(r5);
        goto L_0x38b1;
    L_0x38a6:
        r5 = r14.botButtonsLayout;
        if (r5 == 0) goto L_0x38b0;
    L_0x38aa:
        r5 = r5.toString();
        r1.botButtonsLayout = r5;
    L_0x38b0:
        r15 = 0;
    L_0x38b1:
        r5 = r1.botButtonsByData;
        r5.clear();
        r5 = 0;
        r6 = 0;
    L_0x38b8:
        if (r5 >= r0) goto L_0x3a0b;
    L_0x38ba:
        r7 = r14.messageOwner;
        r7 = r7.reply_markup;
        r7 = r7.rows;
        r7 = r7.get(r5);
        r7 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r7;
        r8 = r7.buttons;
        r8 = r8.size();
        if (r8 != 0) goto L_0x38d1;
    L_0x38ce:
        r2 = r5;
        goto L_0x3a05;
    L_0x38d1:
        r9 = r1.widthForButtons;
        r10 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r11 = r8 + -1;
        r10 = r10 * r11;
        r9 = r9 - r10;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r9 = r9 - r10;
        r9 = r9 / r8;
        r8 = r6;
        r6 = 0;
    L_0x38e6:
        r10 = r7.buttons;
        r10 = r10.size();
        if (r6 >= r10) goto L_0x3a03;
    L_0x38ee:
        r10 = new org.telegram.ui.Cells.ChatMessageCell$BotButton;
        r11 = 0;
        r10.<init>(r1, r11);
        r11 = r7.buttons;
        r11 = r11.get(r6);
        r11 = (org.telegram.tgnet.TLRPC.KeyboardButton) r11;
        r10.button = r11;
        r11 = r10.button;
        r11 = r11.data;
        r11 = org.telegram.messenger.Utilities.bytesToHex(r11);
        r12 = new java.lang.StringBuilder;
        r12.<init>();
        r12.append(r5);
        r13 = "";
        r12.append(r13);
        r12.append(r6);
        r12 = r12.toString();
        if (r15 == 0) goto L_0x3926;
    L_0x391f:
        r13 = r15.get(r12);
        r13 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r13;
        goto L_0x392c;
    L_0x3926:
        r13 = r3.get(r11);
        r13 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r13;
    L_0x392c:
        if (r13 == 0) goto L_0x3945;
    L_0x392e:
        r2 = r13.progressAlpha;
        r10.progressAlpha = r2;
        r2 = r13.angle;
        r10.angle = r2;
        r2 = r5;
        r4 = r13.lastUpdateTime;
        r10.lastUpdateTime = r4;
        goto L_0x394d;
    L_0x3945:
        r2 = r5;
        r4 = java.lang.System.currentTimeMillis();
        r10.lastUpdateTime = r4;
    L_0x394d:
        r4 = r1.botButtonsByData;
        r4.put(r11, r10);
        r4 = r1.botButtonsByPosition;
        r4.put(r12, r10);
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r4 + r9;
        r4 = r4 * r6;
        r10.x = r4;
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = r2 * r4;
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = r5 + r4;
        r10.y = r5;
        r10.width = r9;
        r4 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r10.height = r4;
        r4 = r10.button;
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r4 == 0) goto L_0x399d;
    L_0x3989:
        r4 = r14.messageOwner;
        r4 = r4.media;
        r4 = r4.flags;
        r4 = r4 & 4;
        if (r4 == 0) goto L_0x399d;
    L_0x3993:
        r4 = NUM; // 0x7f0d0811 float:1.8746303E38 double:1.053130798E-314;
        r5 = "PaymentReceipt";
        r4 = org.telegram.messenger.LocaleController.getString(r5, r4);
        goto L_0x39c3;
    L_0x399d:
        r4 = r10.button;
        r4 = r4.text;
        r5 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
        r5 = r5.getFontMetricsInt();
        r11 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r12 = 0;
        r4 = org.telegram.messenger.Emoji.replaceEmoji(r4, r5, r11, r12);
        r5 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r11 = r9 - r11;
        r11 = (float) r11;
        r12 = android.text.TextUtils.TruncateAt.END;
        r4 = android.text.TextUtils.ellipsize(r4, r5, r11, r12);
    L_0x39c3:
        r28 = r4;
        r4 = new android.text.StaticLayout;
        r29 = org.telegram.ui.ActionBar.Theme.chat_botButtonPaint;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r30 = r9 - r5;
        r31 = android.text.Layout.Alignment.ALIGN_CENTER;
        r32 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r33 = 0;
        r34 = 0;
        r27 = r4;
        r27.<init>(r28, r29, r30, r31, r32, r33, r34);
        r10.title = r4;
        r4 = r1.botButtons;
        r4.add(r10);
        r4 = r7.buttons;
        r4 = r4.size();
        r5 = 1;
        r4 = r4 - r5;
        if (r6 != r4) goto L_0x39fc;
    L_0x39ee:
        r4 = r10.x;
        r5 = r10.width;
        r4 = r4 + r5;
        r4 = java.lang.Math.max(r8, r4);
        r8 = r4;
    L_0x39fc:
        r6 = r6 + 1;
        r5 = r2;
        r2 = 2;
        r4 = 0;
        goto L_0x38e6;
    L_0x3a03:
        r2 = r5;
        r6 = r8;
    L_0x3a05:
        r5 = r2 + 1;
        r2 = 2;
        r4 = 0;
        goto L_0x38b8;
    L_0x3a0b:
        r1.widthForButtons = r6;
        goto L_0x3a13;
    L_0x3a0e:
        r2 = 0;
        r1.substractBackgroundHeight = r2;
        r1.keyboardHeight = r2;
    L_0x3a13:
        r0 = r1.drawPinnedBottom;
        if (r0 == 0) goto L_0x3a25;
    L_0x3a17:
        r0 = r1.drawPinnedTop;
        if (r0 == 0) goto L_0x3a25;
    L_0x3a1b:
        r0 = r1.totalHeight;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 - r2;
        r1.totalHeight = r0;
        goto L_0x3a4c;
    L_0x3a25:
        r0 = r1.drawPinnedBottom;
        if (r0 == 0) goto L_0x3a33;
    L_0x3a29:
        r0 = r1.totalHeight;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r0 = r0 - r2;
        r1.totalHeight = r0;
        goto L_0x3a4c;
    L_0x3a33:
        r0 = r1.drawPinnedTop;
        if (r0 == 0) goto L_0x3a4c;
    L_0x3a37:
        r0 = r1.pinnedBottom;
        if (r0 == 0) goto L_0x3a4c;
    L_0x3a3b:
        r0 = r1.currentPosition;
        if (r0 == 0) goto L_0x3a4c;
    L_0x3a3f:
        r0 = r0.siblingHeights;
        if (r0 != 0) goto L_0x3a4c;
    L_0x3a43:
        r0 = r1.totalHeight;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r0 = r0 - r2;
        r1.totalHeight = r0;
    L_0x3a4c:
        r0 = r59.isAnyKindOfSticker();
        if (r0 == 0) goto L_0x3a64;
    L_0x3a52:
        r0 = r1.totalHeight;
        r2 = NUM; // 0x428CLASSNAME float:70.0 double:5.51610112E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        if (r0 >= r2) goto L_0x3a64;
    L_0x3a5c:
        r0 = NUM; // 0x428CLASSNAME float:70.0 double:5.51610112E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r1.totalHeight = r0;
    L_0x3a64:
        r0 = r1.drawPhotoImage;
        if (r0 != 0) goto L_0x3a6e;
    L_0x3a68:
        r0 = r1.photoImage;
        r2 = 0;
        r0.setImageBitmap(r2);
    L_0x3a6e:
        r0 = r1.documentAttachType;
        r2 = 5;
        if (r0 != r2) goto L_0x3aa5;
    L_0x3a73:
        r0 = r1.documentAttach;
        r0 = org.telegram.messenger.MessageObject.isDocumentHasThumb(r0);
        if (r0 == 0) goto L_0x3a8d;
    L_0x3a7b:
        r0 = r1.documentAttach;
        r0 = r0.thumbs;
        r2 = 90;
        r0 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r0, r2);
        r2 = r1.radialProgress;
        r3 = r1.documentAttach;
        r2.setImageOverlay(r0, r3, r14);
        goto L_0x3aab;
    L_0x3a8d:
        r2 = 1;
        r0 = r14.getArtworkUrl(r2);
        r2 = android.text.TextUtils.isEmpty(r0);
        if (r2 != 0) goto L_0x3a9e;
    L_0x3a98:
        r2 = r1.radialProgress;
        r2.setImageOverlay(r0);
        goto L_0x3aab;
    L_0x3a9e:
        r0 = r1.radialProgress;
        r2 = 0;
        r0.setImageOverlay(r2, r2, r2);
        goto L_0x3aab;
    L_0x3aa5:
        r2 = 0;
        r0 = r1.radialProgress;
        r0.setImageOverlay(r2, r2, r2);
    L_0x3aab:
        r58.updateWaveform();
        if (r17 == 0) goto L_0x3ab6;
    L_0x3ab0:
        r0 = r14.cancelEditing;
        if (r0 != 0) goto L_0x3ab6;
    L_0x3ab4:
        r0 = 1;
        goto L_0x3ab7;
    L_0x3ab6:
        r0 = 0;
    L_0x3ab7:
        r2 = 1;
        r3 = 0;
        r1.updateButtonState(r3, r0, r2);
        r0 = r1.buttonState;
        r2 = 2;
        if (r0 != r2) goto L_0x3aed;
    L_0x3ac1:
        r0 = r1.documentAttachType;
        r2 = 3;
        if (r0 != r2) goto L_0x3aed;
    L_0x3ac6:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.DownloadController.getInstance(r0);
        r0 = r0.canDownloadMedia(r14);
        if (r0 == 0) goto L_0x3aed;
    L_0x3ad2:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.FileLoader.getInstance(r0);
        r2 = r1.documentAttach;
        r3 = r1.currentMessageObject;
        r4 = 1;
        r5 = 0;
        r0.loadFile(r2, r3, r4, r5);
        r0 = 4;
        r1.buttonState = r0;
        r0 = r1.radialProgress;
        r2 = r58.getIconForCurrentState();
        r0.setIcon(r2, r5, r5);
    L_0x3aed:
        r0 = r1.accessibilityVirtualViewBounds;
        r0.clear();
        return;
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

    /* Access modifiers changed, original: protected */
    public void onLongPress() {
        CharacterStyle characterStyle = this.pressedLink;
        if (characterStyle instanceof URLSpanMono) {
            this.delegate.didPressUrl(this.currentMessageObject, characterStyle, true);
            return;
        }
        if (characterStyle instanceof URLSpanNoUnderline) {
            if (((URLSpanNoUnderline) characterStyle).getURL().startsWith("/")) {
                this.delegate.didPressUrl(this.currentMessageObject, this.pressedLink, true);
                return;
            }
        } else if (characterStyle instanceof URLSpan) {
            this.delegate.didPressUrl(this.currentMessageObject, characterStyle, true);
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
            if (VERSION.SDK_INT >= 21) {
                Drawable drawable = this.selectorDrawable;
                if (drawable != null) {
                    drawable.setState(StateSet.NOTHING);
                }
            }
            invalidate();
        }
        if (this.pressedVoteButton != -1) {
            this.pressedVoteButton = -1;
            if (VERSION.SDK_INT >= 21) {
                Drawable drawable2 = this.selectorDrawable;
                if (drawable2 != null) {
                    drawable2.setState(StateSet.NOTHING);
                }
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
            Object obj = ((this.isHighlighted || this.isPressed || isPressed()) && !(this.drawPhotoImage && this.photoImage.hasBitmapImage())) ? 1 : null;
            RadialProgress2 radialProgress2 = this.radialProgress;
            boolean z2 = (obj == null && this.buttonPressed == 0) ? false : true;
            radialProgress2.setPressed(z2, false);
            if (this.hasMiniProgress != 0) {
                radialProgress2 = this.radialProgress;
                z2 = (obj == null && this.miniButtonPressed == 0) ? false : true;
                radialProgress2.setPressed(z2, true);
            }
            radialProgress2 = this.videoRadialProgress;
            if (obj == null && this.videoButtonPressed == 0) {
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
                DocumentAttribute documentAttribute = (DocumentAttribute) this.documentAttach.attributes.get(i);
                if (documentAttribute instanceof TL_documentAttributeAudio) {
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
        int i2 = i;
        MessageObject messageObject2 = messageObject;
        if (messageObject2.type == 0) {
            this.documentAttach = messageObject2.messageOwner.media.webpage.document;
        } else {
            this.documentAttach = messageObject.getDocument();
        }
        Document document = this.documentAttach;
        int i3 = 0;
        if (document == null) {
            return 0;
        }
        int i4;
        int dp;
        if (MessageObject.isVoiceDocument(document)) {
            this.documentAttachType = 3;
            for (i4 = 0; i4 < this.documentAttach.attributes.size(); i4++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) this.documentAttach.attributes.get(i4);
                if (documentAttribute instanceof TL_documentAttributeAudio) {
                    i4 = documentAttribute.duration;
                    break;
                }
            }
            i4 = 0;
            this.widthBeforeNewTimeLine = (i2 - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil((double) Theme.chat_audioTimePaint.measureText("00:00")));
            this.availableTimeWidth = i2 - AndroidUtilities.dp(18.0f);
            measureTime(messageObject2);
            dp = AndroidUtilities.dp(174.0f) + this.timeWidth;
            if (!this.hasLinkPreview) {
                this.backgroundWidth = Math.min(i2, dp + (i4 * AndroidUtilities.dp(10.0f)));
            }
            this.seekBarWaveform.setMessageObject(messageObject2);
            return 0;
        } else if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
            i2 -= AndroidUtilities.dp(86.0f);
            if (i2 < 0) {
                i2 = AndroidUtilities.dp(100.0f);
            }
            this.songLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicTitle().replace(10, ' '), Theme.chat_audioTitlePaint, (float) (i2 - AndroidUtilities.dp(12.0f)), TruncateAt.END), Theme.chat_audioTitlePaint, i2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.songLayout.getLineCount() > 0) {
                this.songX = -((int) Math.ceil((double) this.songLayout.getLineLeft(0)));
            }
            this.performerLayout = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicAuthor().replace(10, ' '), Theme.chat_audioPerformerPaint, (float) i2, TruncateAt.END), Theme.chat_audioPerformerPaint, i2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            if (this.performerLayout.getLineCount() > 0) {
                this.performerX = -((int) Math.ceil((double) this.performerLayout.getLineLeft(0)));
            }
            for (i2 = 0; i2 < this.documentAttach.attributes.size(); i2++) {
                DocumentAttribute documentAttribute2 = (DocumentAttribute) this.documentAttach.attributes.get(i2);
                if (documentAttribute2 instanceof TL_documentAttributeAudio) {
                    i2 = documentAttribute2.duration;
                    break;
                }
            }
            i2 = 0;
            TextPaint textPaint = Theme.chat_audioTimePaint;
            r4 = new Object[4];
            dp = i2 / 60;
            r4[0] = Integer.valueOf(dp);
            i2 %= 60;
            r4[1] = Integer.valueOf(i2);
            r4[2] = Integer.valueOf(dp);
            r4[3] = Integer.valueOf(i2);
            i2 = (int) Math.ceil((double) textPaint.measureText(String.format("%d:%02d / %d:%02d", r4)));
            this.widthBeforeNewTimeLine = (this.backgroundWidth - AndroidUtilities.dp(86.0f)) - i2;
            this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.dp(28.0f);
            return i2;
        } else {
            String str = "%s";
            String format;
            if (MessageObject.isVideoDocument(this.documentAttach)) {
                this.documentAttachType = 4;
                if (!messageObject.needDrawBluredPreview()) {
                    updatePlayingMessageProgress();
                    format = String.format(str, new Object[]{AndroidUtilities.formatFileSize((long) this.documentAttach.size)});
                    this.docTitleWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(format));
                    this.docTitleLayout = new StaticLayout(format, Theme.chat_infoPaint, this.docTitleWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
                return 0;
            } else if (MessageObject.isGifDocument(this.documentAttach)) {
                this.documentAttachType = 2;
                if (!messageObject.needDrawBluredPreview()) {
                    String string = LocaleController.getString("AttachGif", NUM);
                    this.infoWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(string));
                    this.infoLayout = new StaticLayout(string, Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    format = String.format(str, new Object[]{AndroidUtilities.formatFileSize((long) this.documentAttach.size)});
                    this.docTitleWidth = (int) Math.ceil((double) Theme.chat_infoPaint.measureText(format));
                    this.docTitleLayout = new StaticLayout(format, Theme.chat_infoPaint, this.docTitleWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
                return 0;
            } else {
                int i5;
                String str2 = this.documentAttach.mime_type;
                boolean z = (str2 != null && str2.toLowerCase().startsWith("image/")) || MessageObject.isDocumentHasThumb(this.documentAttach);
                this.drawPhotoImage = z;
                if (!this.drawPhotoImage) {
                    i2 += AndroidUtilities.dp(30.0f);
                }
                this.documentAttachType = 1;
                str2 = FileLoader.getDocumentFileName(this.documentAttach);
                if (str2 == null || str2.length() == 0) {
                    str2 = LocaleController.getString("AttachDocument", NUM);
                }
                this.docTitleLayout = StaticLayoutEx.createStaticLayout(str2, Theme.chat_docNamePaint, i2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TruncateAt.MIDDLE, i2, 2, false);
                this.docTitleOffsetX = Integer.MIN_VALUE;
                StaticLayout staticLayout = this.docTitleLayout;
                if (staticLayout == null || staticLayout.getLineCount() <= 0) {
                    this.docTitleOffsetX = 0;
                    i5 = i2;
                } else {
                    i4 = 0;
                    while (i3 < this.docTitleLayout.getLineCount()) {
                        i4 = Math.max(i4, (int) Math.ceil((double) this.docTitleLayout.getLineWidth(i3)));
                        this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int) Math.ceil((double) (-this.docTitleLayout.getLineLeft(i3))));
                        i3++;
                    }
                    i5 = Math.min(i2, i4);
                }
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(AndroidUtilities.formatFileSize((long) this.documentAttach.size));
                stringBuilder.append(" ");
                stringBuilder.append(FileLoader.getDocumentExtension(this.documentAttach));
                str2 = stringBuilder.toString();
                this.infoWidth = Math.min(i2 - AndroidUtilities.dp(30.0f), (int) Math.ceil((double) Theme.chat_infoPaint.measureText(str2)));
                CharSequence ellipsize = TextUtils.ellipsize(str2, Theme.chat_infoPaint, (float) this.infoWidth, TruncateAt.END);
                try {
                    if (this.infoWidth < 0) {
                        this.infoWidth = AndroidUtilities.dp(10.0f);
                    }
                    this.infoLayout = new StaticLayout(ellipsize, Theme.chat_infoPaint, this.infoWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (this.drawPhotoImage) {
                    this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 320);
                    this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject2.photoThumbs, 40);
                    if ((DownloadController.getInstance(this.currentAccount).getAutodownloadMask() & 1) == 0) {
                        this.currentPhotoObject = null;
                    }
                    PhotoSize photoSize = this.currentPhotoObject;
                    if (photoSize == null || photoSize == this.currentPhotoObjectThumb) {
                        this.currentPhotoObject = null;
                        this.photoImage.setNeedsQualityThumb(true);
                        this.photoImage.setShouldGenerateQualityThumb(true);
                    }
                    this.currentPhotoFilter = "86_86_b";
                    this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, messageObject2.photoThumbsObject), "86_86", ImageLocation.getForObject(this.currentPhotoObjectThumb, messageObject2.photoThumbsObject), this.currentPhotoFilter, 0, null, messageObject, 1);
                }
                return i5;
            }
        }
    }

    private void calcBackgroundWidth(int i, int i2, int i3) {
        if (!(this.hasLinkPreview || this.hasOldCaptionPreview || this.hasGamePreview || this.hasInvoicePreview)) {
            MessageObject messageObject = this.currentMessageObject;
            int i4 = messageObject.lastLineWidth;
            if (i - i4 >= i2 && !messageObject.hasRtl) {
                i = i3 - i4;
                if (i < 0 || i > i2) {
                    this.backgroundWidth = Math.max(i3, this.currentMessageObject.lastLineWidth + i2) + AndroidUtilities.dp(31.0f);
                    return;
                } else {
                    this.backgroundWidth = ((i3 + i2) - i) + AndroidUtilities.dp(31.0f);
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
        if (messageObject == null || messageObject.messageOwner.message == null || TextUtils.isEmpty(str)) {
            if (!this.urlPathSelection.isEmpty()) {
                this.linkSelectionBlockNum = -1;
                resetUrlPaths(true);
                invalidate();
            }
            return;
        }
        String toLowerCase = str.toLowerCase();
        String toLowerCase2 = messageObject.messageOwner.message.toLowerCase();
        String str2 = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~\n";
        int length = toLowerCase2.length();
        int i = 0;
        int i2 = -1;
        int i3 = -1;
        while (i < length) {
            int min = Math.min(toLowerCase.length(), length - i);
            int i4 = 0;
            int i5 = 0;
            while (i4 < min) {
                Object obj = toLowerCase2.charAt(i + i4) == toLowerCase.charAt(i4) ? 1 : null;
                if (obj != null) {
                    if (i5 != 0 || i == 0 || str2.indexOf(toLowerCase2.charAt(i - 1)) >= 0) {
                        i5++;
                    } else {
                        obj = null;
                    }
                }
                if (obj == null || i4 == min - 1) {
                    if (i5 > 0 && i5 > i3) {
                        i2 = i;
                        i3 = i5;
                    }
                    i++;
                } else {
                    i4++;
                }
            }
            i++;
        }
        if (i2 == -1) {
            if (!this.urlPathSelection.isEmpty()) {
                this.linkSelectionBlockNum = -1;
                resetUrlPaths(true);
                invalidate();
            }
            return;
        }
        int i6 = i2 + i3;
        int length2 = toLowerCase2.length();
        while (i6 < length2 && str2.indexOf(toLowerCase2.charAt(i6)) < 0) {
            i3++;
            i6++;
        }
        i6 = i2 + i3;
        if (this.captionLayout != null && !TextUtils.isEmpty(messageObject.caption)) {
            resetUrlPaths(true);
            try {
                LinkPath obtainNewUrlPath = obtainNewUrlPath(true);
                obtainNewUrlPath.setCurrentLayout(this.captionLayout, i2, 0.0f);
                this.captionLayout.getSelectionPath(i2, i6, obtainNewUrlPath);
            } catch (Exception e) {
                FileLog.e(e);
            }
            invalidate();
        } else if (messageObject.textLayoutBlocks != null) {
            length2 = 0;
            while (length2 < messageObject.textLayoutBlocks.size()) {
                TextLayoutBlock textLayoutBlock = (TextLayoutBlock) messageObject.textLayoutBlocks.get(length2);
                length = textLayoutBlock.charactersOffset;
                if (i2 < length || i2 >= length + textLayoutBlock.textLayout.getText().length()) {
                    length2++;
                } else {
                    this.linkSelectionBlockNum = length2;
                    resetUrlPaths(true);
                    try {
                        LinkPath obtainNewUrlPath2 = obtainNewUrlPath(true);
                        obtainNewUrlPath2.setCurrentLayout(textLayoutBlock.textLayout, i2, 0.0f);
                        textLayoutBlock.textLayout.getSelectionPath(i2, i6 - textLayoutBlock.charactersOffset, obtainNewUrlPath2);
                        if (i6 >= textLayoutBlock.charactersOffset + i3) {
                            for (length2++; length2 < messageObject.textLayoutBlocks.size(); length2++) {
                                TextLayoutBlock textLayoutBlock2 = (TextLayoutBlock) messageObject.textLayoutBlocks.get(length2);
                                length = textLayoutBlock2.textLayout.getText().length();
                                LinkPath obtainNewUrlPath3 = obtainNewUrlPath(true);
                                obtainNewUrlPath3.setCurrentLayout(textLayoutBlock2.textLayout, 0, (float) textLayoutBlock2.height);
                                textLayoutBlock2.textLayout.getSelectionPath(0, i6 - textLayoutBlock2.charactersOffset, obtainNewUrlPath3);
                                if (i6 < (textLayoutBlock.charactersOffset + length) - 1) {
                                    break;
                                }
                            }
                        }
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                    invalidate();
                }
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public boolean verifyDrawable(Drawable drawable) {
        return super.verifyDrawable(drawable) || drawable == this.selectorDrawable;
    }

    private boolean isCurrentLocationTimeExpired(MessageObject messageObject) {
        boolean z = true;
        if (this.currentMessageObject.messageOwner.media.period % 60 == 0) {
            if (Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) <= messageObject.messageOwner.media.period) {
                z = false;
            }
            return z;
        }
        if (Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) <= messageObject.messageOwner.media.period - 5) {
            z = false;
        }
        return z;
    }

    private void checkLocationExpired() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            boolean isCurrentLocationTimeExpired = isCurrentLocationTimeExpired(messageObject);
            if (isCurrentLocationTimeExpired != this.locationExpired) {
                this.locationExpired = isCurrentLocationTimeExpired;
                if (this.locationExpired) {
                    messageObject = this.currentMessageObject;
                    this.currentMessageObject = null;
                    setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
                } else {
                    AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000);
                    this.scheduledInvalidate = true;
                    int dp = this.backgroundWidth - AndroidUtilities.dp(91.0f);
                    this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(LocaleController.getString("AttachLiveLocation", NUM), Theme.chat_locationTitlePaint, (float) dp, TruncateAt.END), Theme.chat_locationTitlePaint, dp, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                }
            }
        }
    }

    public void setMessageObject(MessageObject messageObject, GroupedMessages groupedMessages, boolean z, boolean z2) {
        if (this.attachedToWindow) {
            setMessageContent(messageObject, groupedMessages, z, z2);
            return;
        }
        this.messageObjectToSet = messageObject;
        this.groupedMessagesToSet = groupedMessages;
        this.bottomNearToSet = z;
        this.topNearToSet = z2;
    }

    private int getAdditionalWidthForPosition(GroupedMessagePosition groupedMessagePosition) {
        int i = 0;
        if (groupedMessagePosition == null) {
            return 0;
        }
        if ((groupedMessagePosition.flags & 2) == 0) {
            i = 0 + AndroidUtilities.dp(4.0f);
        }
        return (groupedMessagePosition.flags & 1) == 0 ? i + AndroidUtilities.dp(4.0f) : i;
    }

    private void createSelectorDrawable() {
        if (VERSION.SDK_INT >= 21) {
            Drawable drawable = this.selectorDrawable;
            String str = "chat_outPreviewInstantText";
            String str2 = "chat_inPreviewInstantText";
            if (drawable == null) {
                final Paint paint = new Paint(1);
                paint.setColor(-1);
                AnonymousClass2 anonymousClass2 = new Drawable() {
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
                int[][] iArr = new int[][]{StateSet.WILD_CARD};
                int[] iArr2 = new int[1];
                if (!this.currentMessageObject.isOutOwner()) {
                    str = str2;
                }
                iArr2[0] = NUM & Theme.getColor(str);
                this.selectorDrawable = new RippleDrawable(new ColorStateList(iArr, iArr2), null, anonymousClass2);
                this.selectorDrawable.setCallback(this);
            } else {
                if (!this.currentMessageObject.isOutOwner()) {
                    str = str2;
                }
                Theme.setSelectorDrawableColor(drawable, NUM & Theme.getColor(str), true);
            }
            this.selectorDrawable.setVisible(true, false);
        }
    }

    private void createInstantViewButton() {
        if (VERSION.SDK_INT >= 21 && this.drawInstantView) {
            createSelectorDrawable();
        }
        if (this.drawInstantView && this.instantViewLayout == null) {
            CharSequence string;
            this.instantWidth = AndroidUtilities.dp(33.0f);
            int i = this.drawInstantViewType;
            if (i == 1) {
                string = LocaleController.getString("OpenChannel", NUM);
            } else if (i == 2) {
                string = LocaleController.getString("OpenGroup", NUM);
            } else if (i == 3) {
                string = LocaleController.getString("OpenMessage", NUM);
            } else if (i == 5) {
                string = LocaleController.getString("ViewContact", NUM);
            } else if (i == 6) {
                string = LocaleController.getString("OpenBackground", NUM);
            } else {
                string = LocaleController.getString("InstantView", NUM);
            }
            int dp = this.backgroundWidth - AndroidUtilities.dp(75.0f);
            this.instantViewLayout = new StaticLayout(TextUtils.ellipsize(string, Theme.chat_instantViewPaint, (float) dp, TruncateAt.END), Theme.chat_instantViewPaint, dp + AndroidUtilities.dp(2.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
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

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && (messageObject.checkLayout() || this.lastHeight != AndroidUtilities.displaySize.y)) {
            this.inLayout = true;
            messageObject = this.currentMessageObject;
            this.currentMessageObject = null;
            setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
            this.inLayout = false;
        }
        setMeasuredDimension(MeasureSpec.getSize(i), this.totalHeight + this.keyboardHeight);
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

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0461  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0461  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0489  */
    /* JADX WARNING: Removed duplicated region for block: B:157:0x0461  */
    /* JADX WARNING: Removed duplicated region for block: B:165:0x0489  */
    @android.annotation.SuppressLint({"DrawAllocation"})
    public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
        /*
        r10 = this;
        r12 = r10.currentMessageObject;
        if (r12 != 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r12 = 1;
        r13 = 0;
        r14 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r15 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        if (r11 != 0) goto L_0x0011;
    L_0x000d:
        r11 = r10.wasLayout;
        if (r11 != 0) goto L_0x0109;
    L_0x0011:
        r11 = r10.getMeasuredWidth();
        r10.layoutWidth = r11;
        r11 = r10.getMeasuredHeight();
        r0 = r10.substractBackgroundHeight;
        r11 = r11 - r0;
        r10.layoutHeight = r11;
        r11 = r10.timeTextWidth;
        if (r11 >= 0) goto L_0x002a;
    L_0x0024:
        r11 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r10.timeTextWidth = r11;
    L_0x002a:
        r11 = new android.text.StaticLayout;
        r1 = r10.currentTimeString;
        r2 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r0 = r10.timeTextWidth;
        r3 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r0;
        r4 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r6 = 0;
        r7 = 0;
        r0 = r11;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7);
        r10.timeLayout = r11;
        r11 = r10.mediaBackground;
        r0 = NUM; // 0x42280000 float:42.0 double:5.483722033E-315;
        if (r11 != 0) goto L_0x007c;
    L_0x004b:
        r11 = r10.currentMessageObject;
        r11 = r11.isOutOwner();
        if (r11 != 0) goto L_0x006d;
    L_0x0053:
        r11 = r10.backgroundWidth;
        r1 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r11 = r11 - r1;
        r1 = r10.timeWidth;
        r11 = r11 - r1;
        r1 = r10.isAvatarVisible;
        if (r1 == 0) goto L_0x0068;
    L_0x0063:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
        goto L_0x0069;
    L_0x0068:
        r1 = 0;
    L_0x0069:
        r11 = r11 + r1;
        r10.timeX = r11;
        goto L_0x00c8;
    L_0x006d:
        r11 = r10.layoutWidth;
        r1 = r10.timeWidth;
        r11 = r11 - r1;
        r1 = NUM; // 0x421a0000 float:38.5 double:5.47918896E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r11 = r11 - r1;
        r10.timeX = r11;
        goto L_0x00c8;
    L_0x007c:
        r11 = r10.currentMessageObject;
        r11 = r11.isOutOwner();
        if (r11 != 0) goto L_0x00bc;
    L_0x0084:
        r11 = r10.backgroundWidth;
        r1 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r11 = r11 - r1;
        r1 = r10.timeWidth;
        r11 = r11 - r1;
        r1 = r10.isAvatarVisible;
        if (r1 == 0) goto L_0x0099;
    L_0x0094:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r14);
        goto L_0x009a;
    L_0x0099:
        r1 = 0;
    L_0x009a:
        r11 = r11 + r1;
        r10.timeX = r11;
        r11 = r10.currentPosition;
        if (r11 == 0) goto L_0x00c8;
    L_0x00a1:
        r11 = r11.leftSpanOffset;
        if (r11 == 0) goto L_0x00c8;
    L_0x00a5:
        r1 = r10.timeX;
        r11 = (float) r11;
        r2 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r11 = r11 / r2;
        r2 = r10.getGroupPhotosWidth();
        r2 = (float) r2;
        r11 = r11 * r2;
        r2 = (double) r11;
        r2 = java.lang.Math.ceil(r2);
        r11 = (int) r2;
        r1 = r1 + r11;
        r10.timeX = r1;
        goto L_0x00c8;
    L_0x00bc:
        r11 = r10.layoutWidth;
        r1 = r10.timeWidth;
        r11 = r11 - r1;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r11 = r11 - r1;
        r10.timeX = r11;
    L_0x00c8:
        r11 = r10.currentMessageObject;
        r11 = r11.messageOwner;
        r11 = r11.flags;
        r11 = r11 & 1024;
        if (r11 == 0) goto L_0x00e7;
    L_0x00d2:
        r11 = new android.text.StaticLayout;
        r2 = r10.currentViewsString;
        r3 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r4 = r10.viewsTextWidth;
        r5 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r7 = 0;
        r8 = 0;
        r1 = r11;
        r1.<init>(r2, r3, r4, r5, r6, r7, r8);
        r10.viewsLayout = r11;
        goto L_0x00ea;
    L_0x00e7:
        r11 = 0;
        r10.viewsLayout = r11;
    L_0x00ea:
        r11 = r10.isAvatarVisible;
        if (r11 == 0) goto L_0x0107;
    L_0x00ee:
        r11 = r10.avatarImage;
        r1 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r10.avatarImage;
        r2 = r2.getImageY();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r11.setImageCoords(r1, r2, r3, r0);
    L_0x0107:
        r10.wasLayout = r12;
    L_0x0109:
        r11 = r10.currentMessageObject;
        r11 = r11.type;
        if (r11 != 0) goto L_0x0118;
    L_0x010f:
        r11 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r0 = r10.namesOffset;
        r11 = r11 + r0;
        r10.textY = r11;
    L_0x0118:
        r11 = r10.currentMessageObject;
        r11 = r11.isRoundVideo();
        if (r11 == 0) goto L_0x0123;
    L_0x0120:
        r10.updatePlayingMessageProgress();
    L_0x0123:
        r11 = r10.documentAttachType;
        r0 = 3;
        r1 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
        r2 = NUM; // 0x42860000 float:67.0 double:5.514158374E-315;
        r3 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r4 = 10;
        r5 = NUM; // 0x428e0000 float:71.0 double:5.5167487E-315;
        r6 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r7 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r8 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r9 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        if (r11 != r0) goto L_0x0225;
    L_0x013a:
        r11 = r10.currentMessageObject;
        r11 = r11.isOutOwner();
        if (r11 == 0) goto L_0x0169;
    L_0x0142:
        r11 = r10.layoutWidth;
        r12 = r10.backgroundWidth;
        r11 = r11 - r12;
        r12 = NUM; // 0x42640000 float:57.0 double:5.503149485E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r11 = r11 + r12;
        r10.seekBarX = r11;
        r11 = r10.layoutWidth;
        r12 = r10.backgroundWidth;
        r11 = r11 - r12;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r11 = r11 + r12;
        r10.buttonX = r11;
        r11 = r10.layoutWidth;
        r12 = r10.backgroundWidth;
        r11 = r11 - r12;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r11 = r11 + r12;
        r10.timeAudioX = r11;
        goto L_0x01a0;
    L_0x0169:
        r11 = r10.isChat;
        if (r11 == 0) goto L_0x018c;
    L_0x016d:
        r11 = r10.currentMessageObject;
        r11 = r11.needDrawAvatar();
        if (r11 == 0) goto L_0x018c;
    L_0x0175:
        r11 = NUM; // 0x42e40000 float:114.0 double:5.544594715E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r10.seekBarX = r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r10.buttonX = r11;
        r11 = NUM; // 0x42var_ float:124.0 double:5.55107053E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r10.timeAudioX = r11;
        goto L_0x01a0;
    L_0x018c:
        r11 = NUM; // 0x42840000 float:66.0 double:5.51351079E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r10.seekBarX = r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10.buttonX = r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r10.timeAudioX = r11;
    L_0x01a0:
        r11 = r10.hasLinkPreview;
        if (r11 == 0) goto L_0x01bf;
    L_0x01a4:
        r11 = r10.seekBarX;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11 = r11 + r12;
        r10.seekBarX = r11;
        r11 = r10.buttonX;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11 = r11 + r12;
        r10.buttonX = r11;
        r11 = r10.timeAudioX;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11 = r11 + r12;
        r10.timeAudioX = r11;
    L_0x01bf:
        r11 = r10.seekBarWaveform;
        r12 = r10.backgroundWidth;
        r14 = r10.hasLinkPreview;
        if (r14 == 0) goto L_0x01ca;
    L_0x01c7:
        r14 = 10;
        goto L_0x01cb;
    L_0x01ca:
        r14 = 0;
    L_0x01cb:
        r14 = r14 + 92;
        r14 = (float) r14;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r12 = r12 - r14;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r11.setSize(r12, r14);
        r11 = r10.seekBar;
        r12 = r10.backgroundWidth;
        r14 = r10.hasLinkPreview;
        if (r14 == 0) goto L_0x01e4;
    L_0x01e2:
        r13 = 10;
    L_0x01e4:
        r13 = r13 + 72;
        r13 = (float) r13;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r12 = r12 - r13;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r11.setSize(r12, r13);
        r11 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r12 = r10.namesOffset;
        r11 = r11 + r12;
        r12 = r10.mediaOffsetY;
        r11 = r11 + r12;
        r10.seekBarY = r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r12 = r10.namesOffset;
        r11 = r11 + r12;
        r12 = r10.mediaOffsetY;
        r11 = r11 + r12;
        r10.buttonY = r11;
        r11 = r10.radialProgress;
        r12 = r10.buttonX;
        r13 = r10.buttonY;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r14 = r14 + r12;
        r15 = r10.buttonY;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r15 = r15 + r0;
        r11.setProgressRect(r12, r13, r14, r15);
        r10.updatePlayingMessageProgress();
        goto L_0x054c;
    L_0x0225:
        r0 = 5;
        if (r11 != r0) goto L_0x02fa;
    L_0x0228:
        r11 = r10.currentMessageObject;
        r11 = r11.isOutOwner();
        if (r11 == 0) goto L_0x0257;
    L_0x0230:
        r11 = r10.layoutWidth;
        r12 = r10.backgroundWidth;
        r11 = r11 - r12;
        r12 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r11 = r11 + r12;
        r10.seekBarX = r11;
        r11 = r10.layoutWidth;
        r12 = r10.backgroundWidth;
        r11 = r11 - r12;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r11 = r11 + r12;
        r10.buttonX = r11;
        r11 = r10.layoutWidth;
        r12 = r10.backgroundWidth;
        r11 = r11 - r12;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r11 = r11 + r12;
        r10.timeAudioX = r11;
        goto L_0x028e;
    L_0x0257:
        r11 = r10.isChat;
        if (r11 == 0) goto L_0x027a;
    L_0x025b:
        r11 = r10.currentMessageObject;
        r11 = r11.needDrawAvatar();
        if (r11 == 0) goto L_0x027a;
    L_0x0263:
        r11 = NUM; // 0x42e20000 float:113.0 double:5.543947133E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r10.seekBarX = r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r10.buttonX = r11;
        r11 = NUM; // 0x42var_ float:124.0 double:5.55107053E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r10.timeAudioX = r11;
        goto L_0x028e;
    L_0x027a:
        r11 = NUM; // 0x42820000 float:65.0 double:5.51286321E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r10.seekBarX = r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10.buttonX = r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r10.timeAudioX = r11;
    L_0x028e:
        r11 = r10.hasLinkPreview;
        if (r11 == 0) goto L_0x02ad;
    L_0x0292:
        r11 = r10.seekBarX;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11 = r11 + r12;
        r10.seekBarX = r11;
        r11 = r10.buttonX;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11 = r11 + r12;
        r10.buttonX = r11;
        r11 = r10.timeAudioX;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11 = r11 + r12;
        r10.timeAudioX = r11;
    L_0x02ad:
        r11 = r10.seekBar;
        r12 = r10.backgroundWidth;
        r14 = r10.hasLinkPreview;
        if (r14 == 0) goto L_0x02b7;
    L_0x02b5:
        r13 = 10;
    L_0x02b7:
        r13 = r13 + 65;
        r13 = (float) r13;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r12 = r12 - r13;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r11.setSize(r12, r13);
        r11 = NUM; // 0x41e80000 float:29.0 double:5.46299942E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r12 = r10.namesOffset;
        r11 = r11 + r12;
        r12 = r10.mediaOffsetY;
        r11 = r11 + r12;
        r10.seekBarY = r11;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r12 = r10.namesOffset;
        r11 = r11 + r12;
        r12 = r10.mediaOffsetY;
        r11 = r11 + r12;
        r10.buttonY = r11;
        r11 = r10.radialProgress;
        r12 = r10.buttonX;
        r13 = r10.buttonY;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r14 = r14 + r12;
        r15 = r10.buttonY;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r15 = r15 + r0;
        r11.setProgressRect(r12, r13, r14, r15);
        r10.updatePlayingMessageProgress();
        goto L_0x054c;
    L_0x02fa:
        if (r11 != r12) goto L_0x037d;
    L_0x02fc:
        r11 = r10.drawPhotoImage;
        if (r11 != 0) goto L_0x037d;
    L_0x0300:
        r11 = r10.currentMessageObject;
        r11 = r11.isOutOwner();
        if (r11 == 0) goto L_0x0315;
    L_0x0308:
        r11 = r10.layoutWidth;
        r12 = r10.backgroundWidth;
        r11 = r11 - r12;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r11 = r11 + r12;
        r10.buttonX = r11;
        goto L_0x032e;
    L_0x0315:
        r11 = r10.isChat;
        if (r11 == 0) goto L_0x0328;
    L_0x0319:
        r11 = r10.currentMessageObject;
        r11 = r11.needDrawAvatar();
        if (r11 == 0) goto L_0x0328;
    L_0x0321:
        r11 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r10.buttonX = r11;
        goto L_0x032e;
    L_0x0328:
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r10.buttonX = r11;
    L_0x032e:
        r11 = r10.hasLinkPreview;
        if (r11 == 0) goto L_0x033b;
    L_0x0332:
        r11 = r10.buttonX;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11 = r11 + r12;
        r10.buttonX = r11;
    L_0x033b:
        r11 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r12 = r10.namesOffset;
        r11 = r11 + r12;
        r12 = r10.mediaOffsetY;
        r11 = r11 + r12;
        r10.buttonY = r11;
        r11 = r10.radialProgress;
        r12 = r10.buttonX;
        r13 = r10.buttonY;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r14 = r14 + r12;
        r0 = r10.buttonY;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = r0 + r1;
        r11.setProgressRect(r12, r13, r14, r0);
        r11 = r10.photoImage;
        r12 = r10.buttonX;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r12 = r12 - r13;
        r13 = r10.buttonY;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r13 = r13 - r14;
        r14 = r10.photoImage;
        r14 = r14.getImageWidth();
        r15 = r10.photoImage;
        r15 = r15.getImageHeight();
        r11.setImageCoords(r12, r13, r14, r15);
        goto L_0x054c;
    L_0x037d:
        r11 = r10.currentMessageObject;
        r13 = r11.type;
        r0 = 12;
        if (r13 != r0) goto L_0x03c3;
    L_0x0385:
        r11 = r11.isOutOwner();
        if (r11 == 0) goto L_0x0396;
    L_0x038b:
        r11 = r10.layoutWidth;
        r12 = r10.backgroundWidth;
        r11 = r11 - r12;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r11 = r11 + r12;
        goto L_0x03ad;
    L_0x0396:
        r11 = r10.isChat;
        if (r11 == 0) goto L_0x03a9;
    L_0x039a:
        r11 = r10.currentMessageObject;
        r11 = r11.needDrawAvatar();
        if (r11 == 0) goto L_0x03a9;
    L_0x03a2:
        r11 = NUM; // 0x42900000 float:72.0 double:5.517396283E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        goto L_0x03ad;
    L_0x03a9:
        r11 = org.telegram.messenger.AndroidUtilities.dp(r6);
    L_0x03ad:
        r12 = r10.photoImage;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r14 = r10.namesOffset;
        r13 = r13 + r14;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r15 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r12.setImageCoords(r11, r13, r14, r15);
        goto L_0x054c;
    L_0x03c3:
        if (r13 != 0) goto L_0x0413;
    L_0x03c5:
        r11 = r10.hasLinkPreview;
        if (r11 != 0) goto L_0x03d1;
    L_0x03c9:
        r11 = r10.hasGamePreview;
        if (r11 != 0) goto L_0x03d1;
    L_0x03cd:
        r11 = r10.hasInvoicePreview;
        if (r11 == 0) goto L_0x0413;
    L_0x03d1:
        r11 = r10.hasGamePreview;
        if (r11 == 0) goto L_0x03dd;
    L_0x03d5:
        r11 = r10.unmovedTextX;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11 = r11 - r13;
        goto L_0x03f3;
    L_0x03dd:
        r11 = r10.hasInvoicePreview;
        if (r11 == 0) goto L_0x03ea;
    L_0x03e1:
        r11 = r10.unmovedTextX;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        goto L_0x03f2;
    L_0x03ea:
        r11 = r10.unmovedTextX;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
    L_0x03f2:
        r11 = r11 + r13;
    L_0x03f3:
        r13 = r10.isSmallImage;
        if (r13 == 0) goto L_0x0401;
    L_0x03f7:
        r13 = r10.backgroundWidth;
        r11 = r11 + r13;
        r13 = NUM; // 0x42a20000 float:81.0 double:5.52322452E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        goto L_0x042a;
    L_0x0401:
        r13 = r10.hasInvoicePreview;
        if (r13 == 0) goto L_0x040e;
    L_0x0405:
        r13 = NUM; // 0x40CLASSNAMEa float:6.3 double:5.370265717E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r13 = -r13;
        goto L_0x0437;
    L_0x040e:
        r13 = org.telegram.messenger.AndroidUtilities.dp(r15);
        goto L_0x0437;
    L_0x0413:
        r11 = r10.currentMessageObject;
        r11 = r11.isOutOwner();
        if (r11 == 0) goto L_0x0439;
    L_0x041b:
        r11 = r10.mediaBackground;
        if (r11 == 0) goto L_0x042c;
    L_0x041f:
        r11 = r10.layoutWidth;
        r13 = r10.backgroundWidth;
        r11 = r11 - r13;
        r13 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
    L_0x042a:
        r11 = r11 - r13;
        goto L_0x045b;
    L_0x042c:
        r11 = r10.layoutWidth;
        r13 = r10.backgroundWidth;
        r11 = r11 - r13;
        r13 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
    L_0x0437:
        r11 = r11 + r13;
        goto L_0x045b;
    L_0x0439:
        r11 = r10.isChat;
        if (r11 == 0) goto L_0x0448;
    L_0x043d:
        r11 = r10.isAvatarVisible;
        if (r11 == 0) goto L_0x0448;
    L_0x0441:
        r11 = NUM; // 0x427CLASSNAME float:63.0 double:5.510920465E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        goto L_0x044e;
    L_0x0448:
        r11 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
    L_0x044e:
        r13 = r10.currentPosition;
        if (r13 == 0) goto L_0x045b;
    L_0x0452:
        r13 = r13.edge;
        if (r13 != 0) goto L_0x045b;
    L_0x0456:
        r13 = org.telegram.messenger.AndroidUtilities.dp(r15);
        goto L_0x042a;
    L_0x045b:
        r13 = r10.currentPosition;
        r15 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        if (r13 == 0) goto L_0x0483;
    L_0x0461:
        r13 = r13.flags;
        r12 = r12 & r13;
        if (r12 != 0) goto L_0x046b;
    L_0x0466:
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11 = r11 - r12;
    L_0x046b:
        r12 = r10.currentPosition;
        r12 = r12.leftSpanOffset;
        if (r12 == 0) goto L_0x0483;
    L_0x0471:
        r12 = (float) r12;
        r13 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r12 = r12 / r13;
        r13 = r10.getGroupPhotosWidth();
        r13 = (float) r13;
        r12 = r12 * r13;
        r12 = (double) r12;
        r12 = java.lang.Math.ceil(r12);
        r12 = (int) r12;
        r11 = r11 + r12;
    L_0x0483:
        r12 = r10.currentMessageObject;
        r12 = r12.type;
        if (r12 == 0) goto L_0x048e;
    L_0x0489:
        r12 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r11 = r11 - r12;
    L_0x048e:
        r12 = r10.photoImage;
        r13 = r12.getImageY();
        r0 = r10.photoImage;
        r0 = r0.getImageWidth();
        r1 = r10.photoImage;
        r1 = r1.getImageHeight();
        r12.setImageCoords(r11, r13, r0, r1);
        r11 = (float) r11;
        r12 = r10.photoImage;
        r12 = r12.getImageWidth();
        r13 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r12 = r12 - r13;
        r12 = (float) r12;
        r12 = r12 / r15;
        r11 = r11 + r12;
        r11 = (int) r11;
        r10.buttonX = r11;
        r11 = r10.photoImage;
        r11 = r11.getImageY();
        r12 = r10.photoImage;
        r12 = r12.getImageHeight();
        r13 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r12 = r12 - r13;
        r12 = r12 / 2;
        r11 = r11 + r12;
        r10.buttonY = r11;
        r11 = r10.radialProgress;
        r12 = r10.buttonX;
        r13 = r10.buttonY;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r15 = r15 + r12;
        r0 = r10.buttonY;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r0 = r0 + r14;
        r11.setProgressRect(r12, r13, r15, r0);
        r11 = r10.deleteProgressRect;
        r12 = r10.buttonX;
        r13 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r12 = r12 + r13;
        r12 = (float) r12;
        r13 = r10.buttonY;
        r14 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r13 = r13 + r14;
        r13 = (float) r13;
        r14 = r10.buttonX;
        r15 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        r14 = r14 + r15;
        r14 = (float) r14;
        r15 = r10.buttonY;
        r0 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r15 = r15 + r0;
        r15 = (float) r15;
        r11.set(r12, r13, r14, r15);
        r11 = r10.documentAttachType;
        r12 = 4;
        if (r11 == r12) goto L_0x0515;
    L_0x0512:
        r12 = 2;
        if (r11 != r12) goto L_0x054c;
    L_0x0515:
        r11 = r10.photoImage;
        r11 = r11.getImageX();
        r12 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r11 = r11 + r12;
        r10.videoButtonX = r11;
        r11 = r10.photoImage;
        r11 = r11.getImageY();
        r12 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r11 = r11 + r12;
        r10.videoButtonY = r11;
        r11 = r10.videoRadialProgress;
        r12 = r10.videoButtonX;
        r13 = r10.videoButtonY;
        r14 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = r14 + r12;
        r15 = r10.videoButtonY;
        r0 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r15 = r15 + r0;
        r11.setProgressRect(r12, r13, r14, r15);
    L_0x054c:
        return;
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
                this.voteRisingCircleLength ^= 1;
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

    /* JADX WARNING: Removed duplicated region for block: B:370:0x0ac5  */
    /* JADX WARNING: Removed duplicated region for block: B:561:0x0fc1  */
    /* JADX WARNING: Removed duplicated region for block: B:560:0x0fbe  */
    private void drawContent(android.graphics.Canvas r29) {
        /*
        r28 = this;
        r1 = r28;
        r8 = r29;
        r0 = r1.needNewVisiblePart;
        r9 = 0;
        if (r0 == 0) goto L_0x0020;
    L_0x0009:
        r0 = r1.currentMessageObject;
        r0 = r0.type;
        if (r0 != 0) goto L_0x0020;
    L_0x000f:
        r0 = r1.scrollRect;
        r1.getLocalVisibleRect(r0);
        r0 = r1.scrollRect;
        r2 = r0.top;
        r0 = r0.bottom;
        r0 = r0 - r2;
        r1.setVisiblePart(r2, r0);
        r1.needNewVisiblePart = r9;
    L_0x0020:
        r0 = r1.currentMessagesGroup;
        r10 = 1;
        if (r0 == 0) goto L_0x0027;
    L_0x0025:
        r0 = 1;
        goto L_0x0028;
    L_0x0027:
        r0 = 0;
    L_0x0028:
        r1.forceNotDrawTime = r0;
        r0 = r1.photoImage;
        r2 = r1.currentMessageObject;
        r2 = org.telegram.ui.PhotoViewer.isShowingImage(r2);
        if (r2 != 0) goto L_0x0042;
    L_0x0034:
        r2 = org.telegram.ui.SecretMediaViewer.getInstance();
        r3 = r1.currentMessageObject;
        r2 = r2.isShowingImage(r3);
        if (r2 != 0) goto L_0x0042;
    L_0x0040:
        r2 = 1;
        goto L_0x0043;
    L_0x0042:
        r2 = 0;
    L_0x0043:
        r0.setVisible(r2, r9);
        r0 = r1.photoImage;
        r0 = r0.getVisible();
        r11 = 0;
        r12 = 2;
        r13 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r0 != 0) goto L_0x0066;
    L_0x0052:
        r1.mediaWasInvisible = r10;
        r1.timeWasInvisible = r10;
        r0 = r1.animatingNoSound;
        if (r0 != r10) goto L_0x005f;
    L_0x005a:
        r1.animatingNoSoundProgress = r11;
        r1.animatingNoSound = r9;
        goto L_0x008f;
    L_0x005f:
        if (r0 != r12) goto L_0x008f;
    L_0x0061:
        r1.animatingNoSoundProgress = r13;
        r1.animatingNoSound = r9;
        goto L_0x008f;
    L_0x0066:
        r0 = r1.groupPhotoInvisible;
        if (r0 == 0) goto L_0x006d;
    L_0x006a:
        r1.timeWasInvisible = r10;
        goto L_0x008f;
    L_0x006d:
        r0 = r1.mediaWasInvisible;
        if (r0 != 0) goto L_0x0075;
    L_0x0071:
        r0 = r1.timeWasInvisible;
        if (r0 == 0) goto L_0x008f;
    L_0x0075:
        r0 = r1.mediaWasInvisible;
        if (r0 == 0) goto L_0x007d;
    L_0x0079:
        r1.controlsAlpha = r11;
        r1.mediaWasInvisible = r9;
    L_0x007d:
        r0 = r1.timeWasInvisible;
        if (r0 == 0) goto L_0x0085;
    L_0x0081:
        r1.timeAlpha = r11;
        r1.timeWasInvisible = r9;
    L_0x0085:
        r2 = java.lang.System.currentTimeMillis();
        r1.lastControlsAlphaChangeTime = r2;
        r2 = 0;
        r1.totalChangeTime = r2;
    L_0x008f:
        r0 = r1.radialProgress;
        r2 = "chat_mediaProgress";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setProgressColor(r2);
        r0 = r1.videoRadialProgress;
        r2 = "chat_mediaProgress";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setProgressColor(r2);
        r0 = r1.currentMessageObject;
        r2 = r0.type;
        r14 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r16 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r7 = 4;
        r17 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r18 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r19 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r20 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r21 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r22 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        if (r2 != 0) goto L_0x093c;
    L_0x00bc:
        r0 = r0.isOutOwner();
        if (r0 == 0) goto L_0x00d2;
    L_0x00c2:
        r0 = r1.currentBackgroundDrawable;
        r0 = r0.getBounds();
        r0 = r0.left;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0 = r0 + r2;
        r1.textX = r0;
        goto L_0x00ee;
    L_0x00d2:
        r0 = r1.currentBackgroundDrawable;
        r0 = r0.getBounds();
        r0 = r0.left;
        r2 = r1.mediaBackground;
        if (r2 != 0) goto L_0x00e5;
    L_0x00de:
        r2 = r1.drawPinnedBottom;
        if (r2 == 0) goto L_0x00e5;
    L_0x00e2:
        r2 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        goto L_0x00e7;
    L_0x00e5:
        r2 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
    L_0x00e7:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r2;
        r1.textX = r0;
    L_0x00ee:
        r0 = r1.hasGamePreview;
        if (r0 == 0) goto L_0x0117;
    L_0x00f2:
        r0 = r1.textX;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r0 = r0 + r2;
        r1.textX = r0;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r2 = r1.namesOffset;
        r0 = r0 + r2;
        r1.textY = r0;
        r0 = r1.siteNameLayout;
        if (r0 == 0) goto L_0x0140;
    L_0x0108:
        r2 = r1.textY;
        r3 = r0.getLineCount();
        r3 = r3 - r10;
        r0 = r0.getLineBottom(r3);
        r2 = r2 + r0;
        r1.textY = r2;
        goto L_0x0140;
    L_0x0117:
        r0 = r1.hasInvoicePreview;
        if (r0 == 0) goto L_0x0137;
    L_0x011b:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r2 = r1.namesOffset;
        r0 = r0 + r2;
        r1.textY = r0;
        r0 = r1.siteNameLayout;
        if (r0 == 0) goto L_0x0140;
    L_0x0128:
        r2 = r1.textY;
        r3 = r0.getLineCount();
        r3 = r3 - r10;
        r0 = r0.getLineBottom(r3);
        r2 = r2 + r0;
        r1.textY = r2;
        goto L_0x0140;
    L_0x0137:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r2 = r1.namesOffset;
        r0 = r0 + r2;
        r1.textY = r0;
    L_0x0140:
        r0 = r1.textX;
        r1.unmovedTextX = r0;
        r0 = r1.currentMessageObject;
        r0 = r0.textXOffset;
        r0 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1));
        if (r0 == 0) goto L_0x017d;
    L_0x014c:
        r0 = r1.replyNameLayout;
        if (r0 == 0) goto L_0x017d;
    L_0x0150:
        r0 = r1.backgroundWidth;
        r2 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r2;
        r2 = r1.currentMessageObject;
        r3 = r2.textWidth;
        r0 = r0 - r3;
        r3 = r1.hasNewLineForTime;
        if (r3 != 0) goto L_0x0176;
    L_0x0162:
        r3 = r1.timeWidth;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x016d;
    L_0x016a:
        r2 = 20;
        goto L_0x016e;
    L_0x016d:
        r2 = 0;
    L_0x016e:
        r2 = r2 + r7;
        r2 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r3 + r2;
        r0 = r0 - r3;
    L_0x0176:
        if (r0 <= 0) goto L_0x017d;
    L_0x0178:
        r2 = r1.textX;
        r2 = r2 + r0;
        r1.textX = r2;
    L_0x017d:
        r0 = r1.currentMessageObject;
        r0 = r0.textLayoutBlocks;
        if (r0 == 0) goto L_0x0231;
    L_0x0183:
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x0231;
    L_0x0189:
        r0 = r1.fullyDraw;
        if (r0 == 0) goto L_0x0199;
    L_0x018d:
        r1.firstVisibleBlockNum = r9;
        r0 = r1.currentMessageObject;
        r0 = r0.textLayoutBlocks;
        r0 = r0.size();
        r1.lastVisibleBlockNum = r0;
    L_0x0199:
        r0 = r1.firstVisibleBlockNum;
        if (r0 < 0) goto L_0x0231;
    L_0x019d:
        r2 = r0;
    L_0x019e:
        r0 = r1.lastVisibleBlockNum;
        if (r2 > r0) goto L_0x0231;
    L_0x01a2:
        r0 = r1.currentMessageObject;
        r0 = r0.textLayoutBlocks;
        r0 = r0.size();
        if (r2 < r0) goto L_0x01ae;
    L_0x01ac:
        goto L_0x0231;
    L_0x01ae:
        r0 = r1.currentMessageObject;
        r0 = r0.textLayoutBlocks;
        r0 = r0.get(r2);
        r0 = (org.telegram.messenger.MessageObject.TextLayoutBlock) r0;
        r29.save();
        r3 = r1.textX;
        r4 = r0.isRtl();
        if (r4 == 0) goto L_0x01ce;
    L_0x01c3:
        r4 = r1.currentMessageObject;
        r4 = r4.textXOffset;
        r4 = (double) r4;
        r4 = java.lang.Math.ceil(r4);
        r4 = (int) r4;
        goto L_0x01cf;
    L_0x01ce:
        r4 = 0;
    L_0x01cf:
        r3 = r3 - r4;
        r3 = (float) r3;
        r4 = r1.textY;
        r4 = (float) r4;
        r5 = r0.textYOffset;
        r4 = r4 + r5;
        r8.translate(r3, r4);
        r3 = r1.pressedLink;
        if (r3 == 0) goto L_0x01fb;
    L_0x01de:
        r3 = r1.linkBlockNum;
        if (r2 != r3) goto L_0x01fb;
    L_0x01e2:
        r3 = 0;
    L_0x01e3:
        r4 = r1.urlPath;
        r4 = r4.size();
        if (r3 >= r4) goto L_0x01fb;
    L_0x01eb:
        r4 = r1.urlPath;
        r4 = r4.get(r3);
        r4 = (android.graphics.Path) r4;
        r5 = org.telegram.ui.ActionBar.Theme.chat_urlPaint;
        r8.drawPath(r4, r5);
        r3 = r3 + 1;
        goto L_0x01e3;
    L_0x01fb:
        r3 = r1.linkSelectionBlockNum;
        if (r2 != r3) goto L_0x0220;
    L_0x01ff:
        r3 = r1.urlPathSelection;
        r3 = r3.isEmpty();
        if (r3 != 0) goto L_0x0220;
    L_0x0207:
        r3 = 0;
    L_0x0208:
        r4 = r1.urlPathSelection;
        r4 = r4.size();
        if (r3 >= r4) goto L_0x0220;
    L_0x0210:
        r4 = r1.urlPathSelection;
        r4 = r4.get(r3);
        r4 = (android.graphics.Path) r4;
        r5 = org.telegram.ui.ActionBar.Theme.chat_textSearchSelectionPaint;
        r8.drawPath(r4, r5);
        r3 = r3 + 1;
        goto L_0x0208;
    L_0x0220:
        r0 = r0.textLayout;	 Catch:{ Exception -> 0x0226 }
        r0.draw(r8);	 Catch:{ Exception -> 0x0226 }
        goto L_0x022a;
    L_0x0226:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x022a:
        r29.restore();
        r2 = r2 + 1;
        goto L_0x019e;
    L_0x0231:
        r0 = r1.hasLinkPreview;
        if (r0 != 0) goto L_0x0241;
    L_0x0235:
        r0 = r1.hasGamePreview;
        if (r0 != 0) goto L_0x0241;
    L_0x0239:
        r0 = r1.hasInvoicePreview;
        if (r0 == 0) goto L_0x023e;
    L_0x023d:
        goto L_0x0241;
    L_0x023e:
        r3 = 0;
        goto L_0x0937;
    L_0x0241:
        r0 = r1.hasGamePreview;
        if (r0 == 0) goto L_0x0255;
    L_0x0245:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r2 = r1.namesOffset;
        r0 = r0 + r2;
        r2 = r1.unmovedTextX;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r2 = r2 - r3;
    L_0x0253:
        r6 = r2;
        goto L_0x027b;
    L_0x0255:
        r0 = r1.hasInvoicePreview;
        if (r0 == 0) goto L_0x0267;
    L_0x0259:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r2 = r1.namesOffset;
        r0 = r0 + r2;
        r2 = r1.unmovedTextX;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r13);
        goto L_0x0279;
    L_0x0267:
        r0 = r1.textY;
        r2 = r1.currentMessageObject;
        r2 = r2.textHeight;
        r0 = r0 + r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r0 = r0 + r2;
        r2 = r1.unmovedTextX;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r13);
    L_0x0279:
        r2 = r2 + r3;
        goto L_0x0253;
    L_0x027b:
        r2 = r1.hasInvoicePreview;
        if (r2 != 0) goto L_0x02bd;
    L_0x027f:
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r3 = r1.currentMessageObject;
        r3 = r3.isOutOwner();
        if (r3 == 0) goto L_0x028c;
    L_0x0289:
        r3 = "chat_outPreviewLine";
        goto L_0x028e;
    L_0x028c:
        r3 = "chat_inPreviewLine";
    L_0x028e:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r3 = (float) r6;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r2 = r0 - r2;
        r4 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 + r6;
        r5 = (float) r2;
        r2 = r1.linkPreviewHeight;
        r2 = r2 + r0;
        r23 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r2 = r2 + r23;
        r2 = (float) r2;
        r23 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r24 = r2;
        r2 = r29;
        r15 = r6;
        r6 = r24;
        r14 = 4;
        r7 = r23;
        r2.drawRect(r3, r4, r5, r6, r7);
        goto L_0x02bf;
    L_0x02bd:
        r15 = r6;
        r14 = 4;
    L_0x02bf:
        r2 = r1.siteNameLayout;
        if (r2 == 0) goto L_0x0319;
    L_0x02c3:
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r3 = r1.currentMessageObject;
        r3 = r3.isOutOwner();
        if (r3 == 0) goto L_0x02d0;
    L_0x02cd:
        r3 = "chat_outSiteNameText";
        goto L_0x02d2;
    L_0x02d0:
        r3 = "chat_inSiteNameText";
    L_0x02d2:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r29.save();
        r2 = r1.siteNameRtl;
        if (r2 == 0) goto L_0x02ed;
    L_0x02e0:
        r2 = r1.backgroundWidth;
        r3 = r1.siteNameWidth;
        r2 = r2 - r3;
        r3 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        goto L_0x02f7;
    L_0x02ed:
        r2 = r1.hasInvoicePreview;
        if (r2 == 0) goto L_0x02f3;
    L_0x02f1:
        r2 = 0;
        goto L_0x02f7;
    L_0x02f3:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
    L_0x02f7:
        r6 = r15 + r2;
        r2 = (float) r6;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r3 = r0 - r3;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.siteNameLayout;
        r2.draw(r8);
        r29.restore();
        r2 = r1.siteNameLayout;
        r3 = r2.getLineCount();
        r3 = r3 - r10;
        r2 = r2.getLineBottom(r3);
        r2 = r2 + r0;
        goto L_0x031a;
    L_0x0319:
        r2 = r0;
    L_0x031a:
        r3 = r1.hasGamePreview;
        if (r3 != 0) goto L_0x0322;
    L_0x031e:
        r3 = r1.hasInvoicePreview;
        if (r3 == 0) goto L_0x0338;
    L_0x0322:
        r3 = r1.currentMessageObject;
        r3 = r3.textHeight;
        if (r3 == 0) goto L_0x0338;
    L_0x0328:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r3 = r3 + r4;
        r0 = r0 + r3;
        r3 = r1.currentMessageObject;
        r3 = r3.textHeight;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r3 = r3 + r4;
        r2 = r2 + r3;
    L_0x0338:
        r3 = r1.drawPhotoImage;
        if (r3 == 0) goto L_0x0340;
    L_0x033c:
        r3 = r1.drawInstantView;
        if (r3 != 0) goto L_0x0349;
    L_0x0340:
        r3 = r1.drawInstantViewType;
        r4 = 6;
        if (r3 != r4) goto L_0x048c;
    L_0x0345:
        r3 = r1.imageBackgroundColor;
        if (r3 == 0) goto L_0x048c;
    L_0x0349:
        if (r2 == r0) goto L_0x0350;
    L_0x034b:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 + r3;
    L_0x0350:
        r7 = r2;
        r2 = r1.imageBackgroundSideColor;
        if (r2 == 0) goto L_0x03a6;
    L_0x0355:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r6 = r15 + r2;
        r2 = r1.photoImage;
        r3 = r1.imageBackgroundSideWidth;
        r4 = r2.getImageWidth();
        r3 = r3 - r4;
        r3 = r3 / r12;
        r3 = r3 + r6;
        r4 = r1.photoImage;
        r4 = r4.getImageWidth();
        r5 = r1.photoImage;
        r5 = r5.getImageHeight();
        r2.setImageCoords(r3, r7, r4, r5);
        r2 = r1.rect;
        r3 = (float) r6;
        r4 = r1.photoImage;
        r4 = r4.getImageY();
        r4 = (float) r4;
        r5 = r1.imageBackgroundSideWidth;
        r6 = r6 + r5;
        r5 = (float) r6;
        r6 = r1.photoImage;
        r6 = r6.getImageY2();
        r6 = (float) r6;
        r2.set(r3, r4, r5, r6);
        r2 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r3 = r1.imageBackgroundSideColor;
        r2.setColor(r3);
        r2 = r1.rect;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r4 = (float) r4;
        r5 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r8.drawRoundRect(r2, r3, r4, r5);
        goto L_0x03bd;
    L_0x03a6:
        r2 = r1.photoImage;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r6 = r15 + r3;
        r3 = r1.photoImage;
        r3 = r3.getImageWidth();
        r4 = r1.photoImage;
        r4 = r4.getImageHeight();
        r2.setImageCoords(r6, r7, r3, r4);
    L_0x03bd:
        r2 = r1.imageBackgroundColor;
        if (r2 == 0) goto L_0x0427;
    L_0x03c1:
        r3 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r3.setColor(r2);
        r2 = r1.rect;
        r3 = r1.photoImage;
        r3 = r3.getImageX();
        r3 = (float) r3;
        r4 = r1.photoImage;
        r4 = r4.getImageY();
        r4 = (float) r4;
        r5 = r1.photoImage;
        r5 = r5.getImageX2();
        r5 = (float) r5;
        r6 = r1.photoImage;
        r6 = r6.getImageY2();
        r6 = (float) r6;
        r2.set(r3, r4, r5, r6);
        r2 = r1.imageBackgroundSideColor;
        if (r2 == 0) goto L_0x0413;
    L_0x03eb:
        r2 = r1.photoImage;
        r2 = r2.getImageX();
        r3 = (float) r2;
        r2 = r1.photoImage;
        r2 = r2.getImageY();
        r4 = (float) r2;
        r2 = r1.photoImage;
        r2 = r2.getImageX2();
        r5 = (float) r2;
        r2 = r1.photoImage;
        r2 = r2.getImageY2();
        r6 = (float) r2;
        r23 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r2 = r29;
        r25 = r7;
        r7 = r23;
        r2.drawRect(r3, r4, r5, r6, r7);
        goto L_0x0429;
    L_0x0413:
        r25 = r7;
        r2 = r1.rect;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r4 = (float) r4;
        r5 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r8.drawRoundRect(r2, r3, r4, r5);
        goto L_0x0429;
    L_0x0427:
        r25 = r7;
    L_0x0429:
        r2 = r1.drawPhotoImage;
        if (r2 == 0) goto L_0x0478;
    L_0x042d:
        r2 = r1.drawInstantView;
        if (r2 == 0) goto L_0x0478;
    L_0x0431:
        r2 = r1.drawImageButton;
        if (r2 == 0) goto L_0x0471;
    L_0x0435:
        r2 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r1.photoImage;
        r3 = r3.getImageX();
        r3 = (float) r3;
        r4 = r1.photoImage;
        r4 = r4.getImageWidth();
        r4 = r4 - r2;
        r4 = (float) r4;
        r4 = r4 / r19;
        r3 = r3 + r4;
        r3 = (int) r3;
        r1.buttonX = r3;
        r3 = r1.photoImage;
        r3 = r3.getImageY();
        r3 = (float) r3;
        r4 = r1.photoImage;
        r4 = r4.getImageHeight();
        r4 = r4 - r2;
        r4 = (float) r4;
        r4 = r4 / r19;
        r3 = r3 + r4;
        r3 = (int) r3;
        r1.buttonY = r3;
        r3 = r1.radialProgress;
        r4 = r1.buttonX;
        r5 = r1.buttonY;
        r6 = r4 + r2;
        r2 = r2 + r5;
        r3.setProgressRect(r4, r5, r6, r2);
    L_0x0471:
        r2 = r1.photoImage;
        r2 = r2.draw(r8);
        goto L_0x0479;
    L_0x0478:
        r2 = 0;
    L_0x0479:
        r3 = r1.photoImage;
        r3 = r3.getImageHeight();
        r4 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r3 = r3 + r4;
        r3 = r25 + r3;
        r27 = r3;
        r3 = r2;
        r2 = r27;
        goto L_0x048d;
    L_0x048c:
        r3 = 0;
    L_0x048d:
        r4 = r1.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 == 0) goto L_0x04ac;
    L_0x0495:
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r5 = "chat_messageTextOut";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setColor(r5);
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r5 = "chat_messageTextOut";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setColor(r5);
        goto L_0x04c2;
    L_0x04ac:
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r5 = "chat_messageTextIn";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setColor(r5);
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r5 = "chat_messageTextIn";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setColor(r5);
    L_0x04c2:
        r4 = r1.titleLayout;
        if (r4 == 0) goto L_0x04ff;
    L_0x04c6:
        if (r2 == r0) goto L_0x04cd;
    L_0x04c8:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 + r4;
    L_0x04cd:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r4 = r2 - r4;
        r29.save();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r6 = r15 + r5;
        r5 = r1.titleX;
        r6 = r6 + r5;
        r5 = (float) r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r6 = r2 - r6;
        r6 = (float) r6;
        r8.translate(r5, r6);
        r5 = r1.titleLayout;
        r5.draw(r8);
        r29.restore();
        r5 = r1.titleLayout;
        r6 = r5.getLineCount();
        r6 = r6 - r10;
        r5 = r5.getLineBottom(r6);
        r2 = r2 + r5;
        goto L_0x0500;
    L_0x04ff:
        r4 = 0;
    L_0x0500:
        r5 = r1.authorLayout;
        if (r5 == 0) goto L_0x053e;
    L_0x0504:
        if (r2 == r0) goto L_0x050b;
    L_0x0506:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 + r5;
    L_0x050b:
        if (r4 != 0) goto L_0x0513;
    L_0x050d:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r4 = r2 - r4;
    L_0x0513:
        r29.save();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r6 = r15 + r5;
        r5 = r1.authorX;
        r6 = r6 + r5;
        r5 = (float) r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r6 = r2 - r6;
        r6 = (float) r6;
        r8.translate(r5, r6);
        r5 = r1.authorLayout;
        r5.draw(r8);
        r29.restore();
        r5 = r1.authorLayout;
        r6 = r5.getLineCount();
        r6 = r6 - r10;
        r5 = r5.getLineBottom(r6);
        r2 = r2 + r5;
    L_0x053e:
        r5 = r1.descriptionLayout;
        if (r5 == 0) goto L_0x05a9;
    L_0x0542:
        if (r2 == r0) goto L_0x0549;
    L_0x0544:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 + r5;
    L_0x0549:
        if (r4 != 0) goto L_0x0551;
    L_0x054b:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r4 = r2 - r4;
    L_0x0551:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r5 = r2 - r5;
        r1.descriptionY = r5;
        r29.save();
        r5 = r1.hasInvoicePreview;
        if (r5 == 0) goto L_0x0562;
    L_0x0560:
        r5 = 0;
        goto L_0x0566;
    L_0x0562:
        r5 = org.telegram.messenger.AndroidUtilities.dp(r20);
    L_0x0566:
        r6 = r15 + r5;
        r5 = r1.descriptionX;
        r6 = r6 + r5;
        r5 = (float) r6;
        r6 = r1.descriptionY;
        r6 = (float) r6;
        r8.translate(r5, r6);
        r5 = r1.pressedLink;
        if (r5 == 0) goto L_0x0595;
    L_0x0576:
        r5 = r1.linkBlockNum;
        r6 = -10;
        if (r5 != r6) goto L_0x0595;
    L_0x057c:
        r5 = 0;
    L_0x057d:
        r6 = r1.urlPath;
        r6 = r6.size();
        if (r5 >= r6) goto L_0x0595;
    L_0x0585:
        r6 = r1.urlPath;
        r6 = r6.get(r5);
        r6 = (android.graphics.Path) r6;
        r7 = org.telegram.ui.ActionBar.Theme.chat_urlPaint;
        r8.drawPath(r6, r7);
        r5 = r5 + 1;
        goto L_0x057d;
    L_0x0595:
        r5 = r1.descriptionLayout;
        r5.draw(r8);
        r29.restore();
        r5 = r1.descriptionLayout;
        r6 = r5.getLineCount();
        r6 = r6 - r10;
        r5 = r5.getLineBottom(r6);
        r2 = r2 + r5;
    L_0x05a9:
        r5 = r1.drawPhotoImage;
        if (r5 == 0) goto L_0x0665;
    L_0x05ad:
        r5 = r1.drawInstantView;
        if (r5 != 0) goto L_0x0665;
    L_0x05b1:
        if (r2 == r0) goto L_0x05b8;
    L_0x05b3:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 + r3;
    L_0x05b8:
        r3 = r1.isSmallImage;
        if (r3 == 0) goto L_0x05d9;
    L_0x05bc:
        r3 = r1.photoImage;
        r5 = r1.backgroundWidth;
        r6 = r15 + r5;
        r5 = NUM; // 0x42a20000 float:81.0 double:5.52322452E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = r6 - r5;
        r5 = r1.photoImage;
        r5 = r5.getImageWidth();
        r7 = r1.photoImage;
        r7 = r7.getImageHeight();
        r3.setImageCoords(r6, r4, r5, r7);
        goto L_0x063d;
    L_0x05d9:
        r3 = r1.photoImage;
        r4 = r1.hasInvoicePreview;
        if (r4 == 0) goto L_0x05e8;
    L_0x05df:
        r4 = NUM; // 0x40CLASSNAMEa float:6.3 double:5.370265717E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = -r4;
        goto L_0x05ec;
    L_0x05e8:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r20);
    L_0x05ec:
        r6 = r15 + r4;
        r4 = r1.photoImage;
        r4 = r4.getImageWidth();
        r5 = r1.photoImage;
        r5 = r5.getImageHeight();
        r3.setImageCoords(r6, r2, r4, r5);
        r3 = r1.drawImageButton;
        if (r3 == 0) goto L_0x063d;
    L_0x0601:
        r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.photoImage;
        r4 = r4.getImageX();
        r4 = (float) r4;
        r5 = r1.photoImage;
        r5 = r5.getImageWidth();
        r5 = r5 - r3;
        r5 = (float) r5;
        r5 = r5 / r19;
        r4 = r4 + r5;
        r4 = (int) r4;
        r1.buttonX = r4;
        r4 = r1.photoImage;
        r4 = r4.getImageY();
        r4 = (float) r4;
        r5 = r1.photoImage;
        r5 = r5.getImageHeight();
        r5 = r5 - r3;
        r5 = (float) r5;
        r5 = r5 / r19;
        r4 = r4 + r5;
        r4 = (int) r4;
        r1.buttonY = r4;
        r4 = r1.radialProgress;
        r5 = r1.buttonX;
        r6 = r1.buttonY;
        r7 = r5 + r3;
        r3 = r3 + r6;
        r4.setProgressRect(r5, r6, r7, r3);
    L_0x063d:
        r3 = r1.currentMessageObject;
        r3 = r3.isRoundVideo();
        if (r3 == 0) goto L_0x065f;
    L_0x0645:
        r3 = org.telegram.messenger.MediaController.getInstance();
        r4 = r1.currentMessageObject;
        r3 = r3.isPlayingMessage(r4);
        if (r3 == 0) goto L_0x065f;
    L_0x0651:
        r3 = org.telegram.messenger.MediaController.getInstance();
        r3 = r3.isVideoDrawingReady();
        if (r3 == 0) goto L_0x065f;
    L_0x065b:
        r1.drawTime = r10;
        r3 = 1;
        goto L_0x0665;
    L_0x065f:
        r3 = r1.photoImage;
        r3 = r3.draw(r8);
    L_0x0665:
        r4 = r1.documentAttachType;
        if (r4 == r14) goto L_0x066b;
    L_0x0669:
        if (r4 != r12) goto L_0x069f;
    L_0x066b:
        r4 = r1.photoImage;
        r4 = r4.getImageX();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r4 = r4 + r5;
        r1.videoButtonX = r4;
        r4 = r1.photoImage;
        r4 = r4.getImageY();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r4 = r4 + r5;
        r1.videoButtonY = r4;
        r4 = r1.videoRadialProgress;
        r5 = r1.videoButtonX;
        r6 = r1.videoButtonY;
        r7 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = r7 + r5;
        r14 = r1.videoButtonY;
        r25 = NUM; // 0x41CLASSNAME float:24.0 double:5.450047783E-315;
        r25 = org.telegram.messenger.AndroidUtilities.dp(r25);
        r14 = r14 + r25;
        r4.setProgressRect(r5, r6, r7, r14);
    L_0x069f:
        r4 = r1.photosCountLayout;
        if (r4 == 0) goto L_0x0746;
    L_0x06a3:
        r4 = r1.photoImage;
        r4 = r4.getVisible();
        if (r4 == 0) goto L_0x0746;
    L_0x06ab:
        r4 = r1.photoImage;
        r4 = r4.getImageX();
        r5 = r1.photoImage;
        r5 = r5.getImageWidth();
        r4 = r4 + r5;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r4 = r4 - r5;
        r5 = r1.photosCountWidth;
        r4 = r4 - r5;
        r5 = r1.photoImage;
        r5 = r5.getImageY();
        r6 = r1.photoImage;
        r6 = r6.getImageHeight();
        r5 = r5 + r6;
        r6 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r6;
        r6 = r1.rect;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r7 = r4 - r7;
        r7 = (float) r7;
        r14 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r14);
        r14 = r5 - r14;
        r14 = (float) r14;
        r11 = r1.photosCountWidth;
        r11 = r11 + r4;
        r26 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r11 = r11 + r26;
        r11 = (float) r11;
        r26 = NUM; // 0x41680000 float:14.5 double:5.42155419E-315;
        r26 = org.telegram.messenger.AndroidUtilities.dp(r26);
        r12 = r5 + r26;
        r12 = (float) r12;
        r6.set(r7, r14, r11, r12);
        r6 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint;
        r6 = r6.getAlpha();
        r7 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint;
        r11 = (float) r6;
        r12 = r1.controlsAlpha;
        r11 = r11 * r12;
        r11 = (int) r11;
        r7.setAlpha(r11);
        r7 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r11 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r12 = r1.controlsAlpha;
        r12 = r12 * r11;
        r11 = (int) r12;
        r7.setAlpha(r11);
        r7 = r1.rect;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r11 = (float) r11;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r12 = (float) r12;
        r14 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint;
        r8.drawRoundRect(r7, r11, r12, r14);
        r7 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint;
        r7.setAlpha(r6);
        r29.save();
        r4 = (float) r4;
        r5 = (float) r5;
        r8.translate(r4, r5);
        r4 = r1.photosCountLayout;
        r4.draw(r8);
        r29.restore();
        r4 = org.telegram.ui.ActionBar.Theme.chat_durationPaint;
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r4.setAlpha(r5);
    L_0x0746:
        r4 = r1.videoInfoLayout;
        if (r4 == 0) goto L_0x086d;
    L_0x074a:
        r4 = r1.drawPhotoImage;
        if (r4 == 0) goto L_0x0756;
    L_0x074e:
        r4 = r1.photoImage;
        r4 = r4.getVisible();
        if (r4 == 0) goto L_0x086d;
    L_0x0756:
        r4 = r1.imageBackgroundSideColor;
        if (r4 != 0) goto L_0x086d;
    L_0x075a:
        r4 = r1.hasGamePreview;
        if (r4 != 0) goto L_0x07cb;
    L_0x075e:
        r4 = r1.hasInvoicePreview;
        if (r4 != 0) goto L_0x07cb;
    L_0x0762:
        r4 = r1.documentAttachType;
        r5 = 8;
        if (r4 != r5) goto L_0x0769;
    L_0x0768:
        goto L_0x07cb;
    L_0x0769:
        r2 = r1.photoImage;
        r2 = r2.getImageX();
        r4 = r1.photoImage;
        r4 = r4.getImageWidth();
        r2 = r2 + r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r2 = r2 - r4;
        r4 = r1.durationWidth;
        r6 = r2 - r4;
        r2 = r1.photoImage;
        r2 = r2.getImageY();
        r4 = r1.photoImage;
        r4 = r4.getImageHeight();
        r2 = r2 + r4;
        r4 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r4;
        r4 = r1.rect;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r5 = r6 - r5;
        r5 = (float) r5;
        r7 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = r2 - r7;
        r7 = (float) r7;
        r11 = r1.durationWidth;
        r11 = r11 + r6;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r11 = r11 + r12;
        r11 = (float) r11;
        r12 = NUM; // 0x41680000 float:14.5 double:5.42155419E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = r12 + r2;
        r12 = (float) r12;
        r4.set(r5, r7, r11, r12);
        r4 = r1.rect;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r5 = (float) r5;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r7 = (float) r7;
        r11 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint;
        r8.drawRoundRect(r4, r5, r7, r11);
        goto L_0x082a;
    L_0x07cb:
        r4 = r1.drawPhotoImage;
        if (r4 == 0) goto L_0x0829;
    L_0x07cf:
        r2 = r1.photoImage;
        r2 = r2.getImageX();
        r4 = NUM; // 0x41080000 float:8.5 double:5.390470265E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r6 = r2 + r4;
        r2 = r1.photoImage;
        r2 = r2.getImageY();
        r4 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = r2 + r4;
        r4 = r1.documentAttachType;
        r5 = 8;
        if (r4 != r5) goto L_0x07f1;
    L_0x07ee:
        r4 = NUM; // 0x41680000 float:14.5 double:5.42155419E-315;
        goto L_0x07f3;
    L_0x07f1:
        r4 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
    L_0x07f3:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = r1.rect;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r7 = r6 - r7;
        r7 = (float) r7;
        r11 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r11 = r2 - r11;
        r11 = (float) r11;
        r12 = r1.durationWidth;
        r12 = r12 + r6;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r12 = r12 + r14;
        r12 = (float) r12;
        r4 = r4 + r2;
        r4 = (float) r4;
        r5.set(r7, r11, r12, r4);
        r4 = r1.rect;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r5 = (float) r5;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r7 = (float) r7;
        r11 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint;
        r8.drawRoundRect(r4, r5, r7, r11);
        goto L_0x082a;
    L_0x0829:
        r6 = r15;
    L_0x082a:
        r29.save();
        r4 = (float) r6;
        r2 = (float) r2;
        r8.translate(r4, r2);
        r2 = r1.hasInvoicePreview;
        if (r2 == 0) goto L_0x0865;
    L_0x0836:
        r2 = r1.drawPhotoImage;
        if (r2 == 0) goto L_0x0846;
    L_0x083a:
        r2 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
        r4 = "chat_previewGameText";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r2.setColor(r4);
        goto L_0x0865;
    L_0x0846:
        r2 = r1.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x085a;
    L_0x084e:
        r2 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
        r4 = "chat_messageTextOut";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r2.setColor(r4);
        goto L_0x0865;
    L_0x085a:
        r2 = org.telegram.ui.ActionBar.Theme.chat_shipmentPaint;
        r4 = "chat_messageTextIn";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r2.setColor(r4);
    L_0x0865:
        r2 = r1.videoInfoLayout;
        r2.draw(r8);
        r29.restore();
    L_0x086d:
        r2 = r1.drawInstantView;
        if (r2 == 0) goto L_0x0937;
    L_0x0871:
        r2 = r1.linkPreviewHeight;
        r0 = r0 + r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 + r2;
        r2 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r4 = r1.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 == 0) goto L_0x089a;
    L_0x0883:
        r4 = org.telegram.ui.ActionBar.Theme.chat_msgOutInstantDrawable;
        r5 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r6 = "chat_outPreviewInstantText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setColor(r6);
        r5 = "chat_outPreviewInstantText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r2.setColor(r5);
        goto L_0x08b0;
    L_0x089a:
        r4 = org.telegram.ui.ActionBar.Theme.chat_msgInInstantDrawable;
        r5 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r6 = "chat_inPreviewInstantText";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r5.setColor(r6);
        r5 = "chat_inPreviewInstantText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r2.setColor(r5);
    L_0x08b0:
        r5 = android.os.Build.VERSION.SDK_INT;
        r6 = 21;
        if (r5 < r6) goto L_0x08cc;
    L_0x08b6:
        r1.selectorDrawableMaskType = r9;
        r5 = r1.selectorDrawable;
        r6 = r1.instantWidth;
        r6 = r6 + r15;
        r7 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = r7 + r0;
        r5.setBounds(r15, r0, r6, r7);
        r5 = r1.selectorDrawable;
        r5.draw(r8);
    L_0x08cc:
        r5 = r1.rect;
        r6 = (float) r15;
        r7 = (float) r0;
        r11 = r1.instantWidth;
        r11 = r11 + r15;
        r11 = (float) r11;
        r12 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r12 = r12 + r0;
        r12 = (float) r12;
        r5.set(r6, r7, r11, r12);
        r5 = r1.rect;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r6 = (float) r6;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r7 = (float) r7;
        r8.drawRoundRect(r5, r6, r7, r2);
        r2 = r1.drawInstantViewType;
        if (r2 != 0) goto L_0x0918;
    L_0x08f2:
        r2 = r1.instantTextLeftX;
        r5 = r1.instantTextX;
        r2 = r2 + r5;
        r2 = r2 + r15;
        r5 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 - r5;
        r5 = NUM; // 0x41380000 float:11.5 double:5.406012226E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = r5 + r0;
        r6 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r7 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r4, r2, r5, r6, r7);
        r4.draw(r8);
    L_0x0918:
        r2 = r1.instantViewLayout;
        if (r2 == 0) goto L_0x0937;
    L_0x091c:
        r29.save();
        r2 = r1.instantTextX;
        r6 = r15 + r2;
        r2 = (float) r6;
        r4 = NUM; // 0x41280000 float:10.5 double:5.40083157E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r0 = r0 + r4;
        r0 = (float) r0;
        r8.translate(r2, r0);
        r0 = r1.instantViewLayout;
        r0.draw(r8);
        r29.restore();
    L_0x0937:
        r1.drawTime = r10;
        r0 = r3;
        goto L_0x0b1c;
    L_0x093c:
        r2 = r1.drawPhotoImage;
        if (r2 == 0) goto L_0x0b1b;
    L_0x0940:
        r0 = r0.isRoundVideo();
        if (r0 == 0) goto L_0x0961;
    L_0x0946:
        r0 = org.telegram.messenger.MediaController.getInstance();
        r2 = r1.currentMessageObject;
        r0 = r0.isPlayingMessage(r2);
        if (r0 == 0) goto L_0x0961;
    L_0x0952:
        r0 = org.telegram.messenger.MediaController.getInstance();
        r0 = r0.isVideoDrawingReady();
        if (r0 == 0) goto L_0x0961;
    L_0x095c:
        r1.drawTime = r10;
        r0 = 1;
        goto L_0x0b1c;
    L_0x0961:
        r0 = r1.currentMessageObject;
        r0 = r0.type;
        r2 = 5;
        if (r0 != r2) goto L_0x09e5;
    L_0x0968:
        r0 = org.telegram.ui.ActionBar.Theme.chat_roundVideoShadow;
        if (r0 == 0) goto L_0x09e5;
    L_0x096c:
        r0 = r1.photoImage;
        r0 = r0.getImageX();
        r2 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r0 = r0 - r2;
        r2 = r1.photoImage;
        r2 = r2.getImageY();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 - r3;
        r3 = org.telegram.ui.ActionBar.Theme.chat_roundVideoShadow;
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r3.setAlpha(r4);
        r3 = org.telegram.ui.ActionBar.Theme.chat_roundVideoShadow;
        r4 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r4 = r4 + r0;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r4 = r4 + r5;
        r5 = org.telegram.messenger.AndroidUtilities.roundMessageSize;
        r5 = r5 + r2;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r5 = r5 + r6;
        r3.setBounds(r0, r2, r4, r5);
        r0 = org.telegram.ui.ActionBar.Theme.chat_roundVideoShadow;
        r0.draw(r8);
        r0 = r1.photoImage;
        r0 = r0.hasBitmapImage();
        if (r0 == 0) goto L_0x09b5;
    L_0x09ab:
        r0 = r1.photoImage;
        r0 = r0.getCurrentAlpha();
        r0 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1));
        if (r0 == 0) goto L_0x09e5;
    L_0x09b5:
        r0 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r2 = r1.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x09c2;
    L_0x09bf:
        r2 = "chat_outBubble";
        goto L_0x09c4;
    L_0x09c2:
        r2 = "chat_inBubble";
    L_0x09c4:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setColor(r2);
        r0 = r1.photoImage;
        r0 = r0.getCenterX();
        r2 = r1.photoImage;
        r2 = r2.getCenterY();
        r3 = r1.photoImage;
        r3 = r3.getImageWidth();
        r4 = 2;
        r3 = r3 / r4;
        r3 = (float) r3;
        r4 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r8.drawCircle(r0, r2, r3, r4);
    L_0x09e5:
        r0 = r1.photoCheckBox;
        if (r0 == 0) goto L_0x0a08;
    L_0x09e9:
        r2 = r1.checkBoxVisible;
        if (r2 != 0) goto L_0x09fa;
    L_0x09ed:
        r0 = r0.getProgress();
        r2 = 0;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 != 0) goto L_0x09fa;
    L_0x09f6:
        r0 = r1.checkBoxAnimationInProgress;
        if (r0 == 0) goto L_0x0a08;
    L_0x09fa:
        r0 = r1.currentMessagesGroup;
        if (r0 == 0) goto L_0x0a08;
    L_0x09fe:
        r0 = r0.messages;
        r0 = r0.size();
        if (r0 <= r10) goto L_0x0a08;
    L_0x0a06:
        r0 = 1;
        goto L_0x0a09;
    L_0x0a08:
        r0 = 0;
    L_0x0a09:
        r1.drawPhotoCheckBox = r0;
        r0 = r1.drawPhotoCheckBox;
        if (r0 == 0) goto L_0x0a9f;
    L_0x0a0f:
        r0 = r1.photoCheckBox;
        r0 = r0.isChecked();
        if (r0 != 0) goto L_0x0a26;
    L_0x0a17:
        r0 = r1.photoCheckBox;
        r0 = r0.getProgress();
        r2 = 0;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 != 0) goto L_0x0a26;
    L_0x0a22:
        r0 = r1.checkBoxAnimationInProgress;
        if (r0 == 0) goto L_0x0a9f;
    L_0x0a26:
        r0 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r2 = r1.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x0a33;
    L_0x0a30:
        r2 = "chat_outBubbleSelected";
        goto L_0x0a35;
    L_0x0a33:
        r2 = "chat_inBubbleSelected";
    L_0x0a35:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r0.setColor(r2);
        r0 = r1.rect;
        r2 = r1.photoImage;
        r2 = r2.getImageX();
        r2 = (float) r2;
        r3 = r1.photoImage;
        r3 = r3.getImageY();
        r3 = (float) r3;
        r4 = r1.photoImage;
        r4 = r4.getImageX2();
        r4 = (float) r4;
        r5 = r1.photoImage;
        r5 = r5.getImageY2();
        r5 = (float) r5;
        r0.set(r2, r3, r4, r5);
        r0 = r1.rect;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r3 = (float) r3;
        r4 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r8.drawRoundRect(r0, r2, r3, r4);
        r0 = r1.photoImage;
        r2 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r3;
        r3 = r1.photoCheckBox;
        r3 = r3.getProgress();
        r2 = r2 * r3;
        r0.setSideClip(r2);
        r0 = r1.checkBoxAnimationInProgress;
        if (r0 == 0) goto L_0x0a8e;
    L_0x0a86:
        r0 = r1.photoCheckBox;
        r2 = r1.checkBoxAnimationProgress;
        r0.setBackgroundAlpha(r2);
        goto L_0x0aa5;
    L_0x0a8e:
        r0 = r1.photoCheckBox;
        r2 = r1.checkBoxVisible;
        if (r2 == 0) goto L_0x0a97;
    L_0x0a94:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x0a9b;
    L_0x0a97:
        r2 = r0.getProgress();
    L_0x0a9b:
        r0.setBackgroundAlpha(r2);
        goto L_0x0aa5;
    L_0x0a9f:
        r0 = r1.photoImage;
        r2 = 0;
        r0.setSideClip(r2);
    L_0x0aa5:
        r0 = r1.photoImage;
        r0 = r0.draw(r8);
        r2 = r1.drawTime;
        r3 = r1.photoImage;
        r3 = r3.getVisible();
        r1.drawTime = r3;
        r3 = r1.currentPosition;
        if (r3 == 0) goto L_0x0b1c;
    L_0x0ab9:
        r3 = r1.drawTime;
        if (r2 == r3) goto L_0x0b1c;
    L_0x0abd:
        r2 = r28.getParent();
        r2 = (android.view.ViewGroup) r2;
        if (r2 == 0) goto L_0x0b1c;
    L_0x0ac5:
        r3 = r1.currentPosition;
        r3 = r3.last;
        if (r3 != 0) goto L_0x0b17;
    L_0x0acb:
        r3 = r2.getChildCount();
        r4 = 0;
    L_0x0ad0:
        if (r4 >= r3) goto L_0x0b1c;
    L_0x0ad2:
        r5 = r2.getChildAt(r4);
        if (r5 == r1) goto L_0x0b14;
    L_0x0ad8:
        r6 = r5 instanceof org.telegram.ui.Cells.ChatMessageCell;
        if (r6 != 0) goto L_0x0add;
    L_0x0adc:
        goto L_0x0b14;
    L_0x0add:
        r5 = (org.telegram.ui.Cells.ChatMessageCell) r5;
        r6 = r5.getCurrentMessagesGroup();
        r7 = r1.currentMessagesGroup;
        if (r6 != r7) goto L_0x0b14;
    L_0x0ae7:
        r6 = r5.getCurrentPosition();
        r7 = r6.last;
        if (r7 == 0) goto L_0x0b14;
    L_0x0aef:
        r6 = r6.maxY;
        r7 = r1.currentPosition;
        r7 = r7.maxY;
        if (r6 != r7) goto L_0x0b14;
    L_0x0af7:
        r6 = r5.timeX;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r6 = r6 - r7;
        r7 = r5.getLeft();
        r6 = r6 + r7;
        r7 = r28.getRight();
        if (r6 >= r7) goto L_0x0b14;
    L_0x0b09:
        r6 = r1.drawTime;
        r6 = r6 ^ r10;
        r5.groupPhotoInvisible = r6;
        r5.invalidate();
        r2.invalidate();
    L_0x0b14:
        r4 = r4 + 1;
        goto L_0x0ad0;
    L_0x0b17:
        r2.invalidate();
        goto L_0x0b1c;
    L_0x0b1b:
        r0 = 0;
    L_0x0b1c:
        r2 = r1.documentAttachType;
        r3 = 2;
        if (r2 != r3) goto L_0x0b83;
    L_0x0b21:
        r2 = r1.photoImage;
        r2 = r2.getVisible();
        if (r2 == 0) goto L_0x0var_;
    L_0x0b29:
        r2 = r1.hasGamePreview;
        if (r2 != 0) goto L_0x0var_;
    L_0x0b2d:
        r2 = r1.currentMessageObject;
        r2 = r2.needDrawBluredPreview();
        if (r2 != 0) goto L_0x0var_;
    L_0x0b35:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r2 = (android.graphics.drawable.BitmapDrawable) r2;
        r2 = r2.getPaint();
        r2 = r2.getAlpha();
        r3 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r4 = (float) r2;
        r5 = r1.controlsAlpha;
        r4 = r4 * r5;
        r4 = (int) r4;
        r3.setAlpha(r4);
        r3 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r4 = r1.photoImage;
        r4 = r4.getImageX();
        r5 = r1.photoImage;
        r5 = r5.getImageWidth();
        r4 = r4 + r5;
        r5 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 - r5;
        r1.otherX = r4;
        r5 = r1.photoImage;
        r5 = r5.getImageY();
        r6 = NUM; // 0x4101999a float:8.1 double:5.388398005E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 + r6;
        r1.otherY = r5;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r3, r4, r5);
        r3 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r3.draw(r8);
        r3 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r3.setAlpha(r2);
        goto L_0x0var_;
    L_0x0b83:
        r3 = 7;
        if (r2 != r3) goto L_0x0cd9;
    L_0x0b86:
        r2 = r1.durationLayout;
        if (r2 == 0) goto L_0x0var_;
    L_0x0b8a:
        r2 = org.telegram.messenger.MediaController.getInstance();
        r3 = r1.currentMessageObject;
        r2 = r2.isPlayingMessage(r3);
        if (r2 == 0) goto L_0x0ba3;
    L_0x0b96:
        r3 = r1.currentMessageObject;
        r3 = r3.type;
        r4 = 5;
        if (r3 != r4) goto L_0x0ba3;
    L_0x0b9d:
        r28.drawRoundProgress(r29);
        r28.drawOverlays(r29);
    L_0x0ba3:
        r3 = r1.currentMessageObject;
        r4 = r3.type;
        r5 = 5;
        if (r4 != r5) goto L_0x0CLASSNAME;
    L_0x0baa:
        r3 = r1.backgroundDrawableLeft;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = r3 + r4;
        r4 = r1.layoutHeight;
        r5 = r1.drawPinnedBottom;
        if (r5 == 0) goto L_0x0bb9;
    L_0x0bb7:
        r5 = 2;
        goto L_0x0bba;
    L_0x0bb9:
        r5 = 0;
    L_0x0bba:
        r5 = 28 - r5;
        r5 = (float) r5;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 - r5;
        r5 = r1.rect;
        r6 = (float) r3;
        r7 = (float) r4;
        r11 = r1.timeWidthAudio;
        r11 = r11 + r3;
        r12 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r11 = r11 + r12;
        r11 = (float) r11;
        r12 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r14 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r14 = r14 + r4;
        r12 = (float) r14;
        r5.set(r6, r7, r11, r12);
        r5 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r5 = r5.getAlpha();
        r6 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r7 = (float) r5;
        r11 = r1.timeAlpha;
        r7 = r7 * r11;
        r7 = (int) r7;
        r6.setAlpha(r7);
        r6 = r1.rect;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r7 = (float) r7;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r11 = (float) r11;
        r12 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r8.drawRoundRect(r6, r7, r11, r12);
        r6 = org.telegram.ui.ActionBar.Theme.chat_actionBackgroundPaint;
        r6.setAlpha(r5);
        if (r2 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r5 = r1.currentMessageObject;
        r5 = r5.isContentUnread();
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0c0d:
        r2 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r5 = "chat_mediaTimeText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r2.setColor(r5);
        r2 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r5 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r6 = r1.timeAlpha;
        r6 = r6 * r5;
        r5 = (int) r6;
        r2.setAlpha(r5);
        r2 = r1.timeWidthAudio;
        r2 = r2 + r3;
        r5 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 + r5;
        r2 = (float) r2;
        r5 = NUM; // 0x4104cccd float:8.3 double:5.389434135E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = r5 + r4;
        r5 = (float) r5;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r6 = (float) r6;
        r7 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r8.drawCircle(r2, r5, r6, r7);
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        if (r2 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r2 = org.telegram.messenger.MediaController.getInstance();
        r2 = r2.isMessagePaused();
        if (r2 != 0) goto L_0x0CLASSNAME;
    L_0x0c4f:
        r2 = r1.roundVideoPlayingDrawable;
        r2.start();
        goto L_0x0c5a;
    L_0x0CLASSNAME:
        r2 = r1.roundVideoPlayingDrawable;
        r2.stop();
    L_0x0c5a:
        r2 = r1.roundVideoPlayingDrawable;
        r5 = r1.timeWidthAudio;
        r5 = r5 + r3;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r5 = r5 + r6;
        r6 = NUM; // 0x40133333 float:2.3 double:5.31120626E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r4;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r2, r5, r6);
        r2 = r1.roundVideoPlayingDrawable;
        r2.draw(r8);
    L_0x0CLASSNAME:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r3 = r3 + r2;
        r2 = NUM; // 0x3fd9999a float:1.7 double:5.29255591E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r4 = r4 + r2;
        goto L_0x0cb4;
    L_0x0CLASSNAME:
        r2 = r1.backgroundDrawableLeft;
        r3 = r3.isOutOwner();
        if (r3 != 0) goto L_0x0CLASSNAME;
    L_0x0c8a:
        r3 = r1.drawPinnedBottom;
        if (r3 == 0) goto L_0x0c8f;
    L_0x0c8e:
        goto L_0x0CLASSNAME;
    L_0x0c8f:
        r3 = NUM; // 0x41900000 float:18.0 double:5.43450582E-315;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
    L_0x0CLASSNAME:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r2;
        r2 = r1.layoutHeight;
        r4 = NUM; // 0x40CLASSNAMEa float:6.3 double:5.370265717E-315;
        r5 = r1.drawPinnedBottom;
        if (r5 == 0) goto L_0x0ca4;
    L_0x0ca2:
        r5 = 2;
        goto L_0x0ca5;
    L_0x0ca4:
        r5 = 0;
    L_0x0ca5:
        r5 = (float) r5;
        r4 = r4 - r5;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 - r4;
        r4 = r1.timeLayout;
        r4 = r4.getHeight();
        r4 = r2 - r4;
    L_0x0cb4:
        r2 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r5 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r6 = r1.timeAlpha;
        r6 = r6 * r5;
        r5 = (int) r6;
        r2.setAlpha(r5);
        r29.save();
        r2 = (float) r3;
        r3 = (float) r4;
        r8.translate(r2, r3);
        r2 = r1.durationLayout;
        r2.draw(r8);
        r29.restore();
        r2 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r3 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r2.setAlpha(r3);
        goto L_0x0var_;
    L_0x0cd9:
        r3 = 5;
        if (r2 != r3) goto L_0x0e46;
    L_0x0cdc:
        r2 = r1.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x0d31;
    L_0x0ce4:
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r3 = "chat_outAudioTitleText";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioPerformerPaint;
        r3 = r28.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x0cfa;
    L_0x0cf7:
        r3 = "chat_outAudioPerfomerSelectedText";
        goto L_0x0cfc;
    L_0x0cfa:
        r3 = "chat_outAudioPerfomerText";
    L_0x0cfc:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r3 = r28.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x0d0e;
    L_0x0d0b:
        r3 = "chat_outAudioDurationSelectedText";
        goto L_0x0d10;
    L_0x0d0e:
        r3 = "chat_outAudioDurationText";
    L_0x0d10:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = r1.radialProgress;
        r3 = r28.isDrawSelectionBackground();
        if (r3 != 0) goto L_0x0d27;
    L_0x0d1f:
        r3 = r1.buttonPressed;
        if (r3 == 0) goto L_0x0d24;
    L_0x0d23:
        goto L_0x0d27;
    L_0x0d24:
        r3 = "chat_outAudioProgress";
        goto L_0x0d29;
    L_0x0d27:
        r3 = "chat_outAudioSelectedProgress";
    L_0x0d29:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setProgressColor(r3);
        goto L_0x0d7d;
    L_0x0d31:
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r3 = "chat_inAudioTitleText";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioPerformerPaint;
        r3 = r28.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x0d47;
    L_0x0d44:
        r3 = "chat_inAudioPerfomerSelectedText";
        goto L_0x0d49;
    L_0x0d47:
        r3 = "chat_inAudioPerfomerText";
    L_0x0d49:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r3 = r28.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x0d5b;
    L_0x0d58:
        r3 = "chat_inAudioDurationSelectedText";
        goto L_0x0d5d;
    L_0x0d5b:
        r3 = "chat_inAudioDurationText";
    L_0x0d5d:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = r1.radialProgress;
        r3 = r28.isDrawSelectionBackground();
        if (r3 != 0) goto L_0x0d74;
    L_0x0d6c:
        r3 = r1.buttonPressed;
        if (r3 == 0) goto L_0x0d71;
    L_0x0d70:
        goto L_0x0d74;
    L_0x0d71:
        r3 = "chat_inAudioProgress";
        goto L_0x0d76;
    L_0x0d74:
        r3 = "chat_inAudioSelectedProgress";
    L_0x0d76:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setProgressColor(r3);
    L_0x0d7d:
        r2 = r1.radialProgress;
        r2.draw(r8);
        r29.save();
        r2 = r1.timeAudioX;
        r3 = r1.songX;
        r2 = r2 + r3;
        r2 = (float) r2;
        r3 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.namesOffset;
        r3 = r3 + r4;
        r4 = r1.mediaOffsetY;
        r3 = r3 + r4;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.songLayout;
        r2.draw(r8);
        r29.restore();
        r29.save();
        r2 = org.telegram.messenger.MediaController.getInstance();
        r3 = r1.currentMessageObject;
        r2 = r2.isPlayingMessage(r3);
        if (r2 == 0) goto L_0x0dc1;
    L_0x0db2:
        r2 = r1.seekBarX;
        r2 = (float) r2;
        r3 = r1.seekBarY;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.seekBar;
        r2.draw(r8);
        goto L_0x0ddc;
    L_0x0dc1:
        r2 = r1.timeAudioX;
        r3 = r1.performerX;
        r2 = r2 + r3;
        r2 = (float) r2;
        r3 = NUM; // 0x420CLASSNAME float:35.0 double:5.47465589E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.namesOffset;
        r3 = r3 + r4;
        r4 = r1.mediaOffsetY;
        r3 = r3 + r4;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.performerLayout;
        r2.draw(r8);
    L_0x0ddc:
        r29.restore();
        r29.save();
        r2 = r1.timeAudioX;
        r2 = (float) r2;
        r3 = NUM; // 0x42640000 float:57.0 double:5.503149485E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.namesOffset;
        r3 = r3 + r4;
        r4 = r1.mediaOffsetY;
        r3 = r3 + r4;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.durationLayout;
        r2.draw(r8);
        r29.restore();
        r2 = r1.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x0e11;
    L_0x0e05:
        r2 = r28.isDrawSelectionBackground();
        if (r2 == 0) goto L_0x0e0e;
    L_0x0e0b:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgOutMenuSelectedDrawable;
        goto L_0x0e1c;
    L_0x0e0e:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgOutMenuDrawable;
        goto L_0x0e1c;
    L_0x0e11:
        r2 = r28.isDrawSelectionBackground();
        if (r2 == 0) goto L_0x0e1a;
    L_0x0e17:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgInMenuSelectedDrawable;
        goto L_0x0e1c;
    L_0x0e1a:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgInMenuDrawable;
    L_0x0e1c:
        r3 = r1.buttonX;
        r4 = r1.backgroundWidth;
        r3 = r3 + r4;
        r4 = r1.currentMessageObject;
        r4 = r4.type;
        if (r4 != 0) goto L_0x0e2a;
    L_0x0e27:
        r4 = NUM; // 0x42680000 float:58.0 double:5.50444465E-315;
        goto L_0x0e2c;
    L_0x0e2a:
        r4 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
    L_0x0e2c:
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r1.otherX = r3;
        r4 = r1.buttonY;
        r5 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 - r5;
        r1.otherY = r4;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r2, r3, r4);
        r2.draw(r8);
        goto L_0x0var_;
    L_0x0e46:
        r3 = 3;
        if (r2 != r3) goto L_0x0var_;
    L_0x0e49:
        r2 = r1.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x0e7f;
    L_0x0e51:
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r3 = r28.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x0e5c;
    L_0x0e59:
        r3 = "chat_outAudioDurationSelectedText";
        goto L_0x0e5e;
    L_0x0e5c:
        r3 = "chat_outAudioDurationText";
    L_0x0e5e:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = r1.radialProgress;
        r3 = r28.isDrawSelectionBackground();
        if (r3 != 0) goto L_0x0e75;
    L_0x0e6d:
        r3 = r1.buttonPressed;
        if (r3 == 0) goto L_0x0e72;
    L_0x0e71:
        goto L_0x0e75;
    L_0x0e72:
        r3 = "chat_outAudioProgress";
        goto L_0x0e77;
    L_0x0e75:
        r3 = "chat_outAudioSelectedProgress";
    L_0x0e77:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setProgressColor(r3);
        goto L_0x0eac;
    L_0x0e7f:
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioTimePaint;
        r3 = r28.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x0e8a;
    L_0x0e87:
        r3 = "chat_inAudioDurationSelectedText";
        goto L_0x0e8c;
    L_0x0e8a:
        r3 = "chat_inAudioDurationText";
    L_0x0e8c:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = r1.radialProgress;
        r3 = r28.isDrawSelectionBackground();
        if (r3 != 0) goto L_0x0ea3;
    L_0x0e9b:
        r3 = r1.buttonPressed;
        if (r3 == 0) goto L_0x0ea0;
    L_0x0e9f:
        goto L_0x0ea3;
    L_0x0ea0:
        r3 = "chat_inAudioProgress";
        goto L_0x0ea5;
    L_0x0ea3:
        r3 = "chat_inAudioSelectedProgress";
    L_0x0ea5:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setProgressColor(r3);
    L_0x0eac:
        r2 = r1.radialProgress;
        r2.draw(r8);
        r29.save();
        r2 = r1.useSeekBarWaweform;
        if (r2 == 0) goto L_0x0ece;
    L_0x0eb8:
        r2 = r1.seekBarX;
        r3 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r2 = (float) r2;
        r3 = r1.seekBarY;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.seekBarWaveform;
        r2.draw(r8);
        goto L_0x0edc;
    L_0x0ece:
        r2 = r1.seekBarX;
        r2 = (float) r2;
        r3 = r1.seekBarY;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.seekBar;
        r2.draw(r8);
    L_0x0edc:
        r29.restore();
        r29.save();
        r2 = r1.timeAudioX;
        r2 = (float) r2;
        r3 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.namesOffset;
        r3 = r3 + r4;
        r4 = r1.mediaOffsetY;
        r3 = r3 + r4;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.durationLayout;
        r2.draw(r8);
        r29.restore();
        r2 = r1.currentMessageObject;
        r3 = r2.type;
        if (r3 == 0) goto L_0x0var_;
    L_0x0var_:
        r2 = r2.isContentUnread();
        if (r2 == 0) goto L_0x0var_;
    L_0x0var_:
        r2 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r3 = r1.currentMessageObject;
        r3 = r3.isOutOwner();
        if (r3 == 0) goto L_0x0var_;
    L_0x0var_:
        r3 = "chat_outVoiceSeekbarFill";
        goto L_0x0var_;
    L_0x0var_:
        r3 = "chat_inVoiceSeekbarFill";
    L_0x0var_:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = r1.timeAudioX;
        r3 = r1.timeWidthAudio;
        r2 = r2 + r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = r2 + r3;
        r2 = (float) r2;
        r3 = NUM; // 0x424CLASSNAME float:51.0 double:5.495378504E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r1.namesOffset;
        r3 = r3 + r4;
        r4 = r1.mediaOffsetY;
        r3 = r3 + r4;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r4 = (float) r4;
        r5 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r8.drawCircle(r2, r3, r4, r5);
    L_0x0var_:
        r2 = r1.captionLayout;
        if (r2 == 0) goto L_0x0ff4;
    L_0x0var_:
        r2 = r1.currentMessageObject;
        r3 = r2.type;
        if (r3 == r10) goto L_0x0fcb;
    L_0x0f4b:
        r4 = r1.documentAttachType;
        r5 = 4;
        if (r4 == r5) goto L_0x0fcb;
    L_0x0var_:
        r4 = 8;
        if (r3 != r4) goto L_0x0var_;
    L_0x0var_:
        goto L_0x0fcb;
    L_0x0var_:
        r3 = r1.hasOldCaptionPreview;
        if (r3 == 0) goto L_0x0var_;
    L_0x0f5a:
        r3 = r1.backgroundDrawableLeft;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x0var_;
    L_0x0var_:
        r2 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        goto L_0x0var_;
    L_0x0var_:
        r2 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
    L_0x0var_:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r3 + r2;
        r2 = r1.captionOffsetX;
        r3 = r3 + r2;
        r1.captionX = r3;
        r2 = r1.totalHeight;
        r3 = r1.captionHeight;
        r2 = r2 - r3;
        r3 = r1.drawPinnedTop;
        if (r3 == 0) goto L_0x0f7d;
    L_0x0f7a:
        r3 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        goto L_0x0f7f;
    L_0x0f7d:
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x0f7f:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = r1.linkPreviewHeight;
        r2 = r2 - r3;
        r12 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r2 = r2 - r3;
        r1.captionY = r2;
        goto L_0x0ff6;
    L_0x0var_:
        r12 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r3 = r1.backgroundDrawableLeft;
        r2 = r2.isOutOwner();
        if (r2 != 0) goto L_0x0fa9;
    L_0x0f9b:
        r2 = r1.mediaBackground;
        if (r2 != 0) goto L_0x0fa9;
    L_0x0f9f:
        if (r2 != 0) goto L_0x0fa6;
    L_0x0fa1:
        r2 = r1.drawPinnedBottom;
        if (r2 == 0) goto L_0x0fa6;
    L_0x0fa5:
        goto L_0x0fa9;
    L_0x0fa6:
        r2 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        goto L_0x0fab;
    L_0x0fa9:
        r2 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
    L_0x0fab:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r3 + r2;
        r2 = r1.captionOffsetX;
        r3 = r3 + r2;
        r1.captionX = r3;
        r2 = r1.totalHeight;
        r3 = r1.captionHeight;
        r2 = r2 - r3;
        r3 = r1.drawPinnedTop;
        if (r3 == 0) goto L_0x0fc1;
    L_0x0fbe:
        r3 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        goto L_0x0fc3;
    L_0x0fc1:
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x0fc3:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r1.captionY = r2;
        goto L_0x0ff6;
    L_0x0fcb:
        r12 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
        r2 = r1.photoImage;
        r2 = r2.getImageX();
        r3 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r3 = r1.captionOffsetX;
        r2 = r2 + r3;
        r1.captionX = r2;
        r2 = r1.photoImage;
        r2 = r2.getImageY();
        r3 = r1.photoImage;
        r3 = r3.getImageHeight();
        r2 = r2 + r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r2 = r2 + r3;
        r1.captionY = r2;
        goto L_0x0ff6;
    L_0x0ff4:
        r12 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
    L_0x0ff6:
        r2 = r1.currentPosition;
        if (r2 != 0) goto L_0x0ffd;
    L_0x0ffa:
        r1.drawCaptionLayout(r8, r9);
    L_0x0ffd:
        r2 = r1.hasOldCaptionPreview;
        if (r2 == 0) goto L_0x111f;
    L_0x1001:
        r2 = r1.currentMessageObject;
        r3 = r2.type;
        if (r3 == r10) goto L_0x1022;
    L_0x1007:
        r4 = r1.documentAttachType;
        r5 = 4;
        if (r4 == r5) goto L_0x1022;
    L_0x100c:
        r4 = 8;
        if (r3 != r4) goto L_0x1011;
    L_0x1010:
        goto L_0x1022;
    L_0x1011:
        r3 = r1.backgroundDrawableLeft;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x101a;
    L_0x1019:
        goto L_0x101c;
    L_0x101a:
        r16 = NUM; // 0x41880000 float:17.0 double:5.431915495E-315;
    L_0x101c:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = r3 + r2;
        goto L_0x102f;
    L_0x1022:
        r2 = r1.photoImage;
        r2 = r2.getImageX();
        r3 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r2;
    L_0x102f:
        r11 = r3;
        r2 = r1.totalHeight;
        r3 = r1.drawPinnedTop;
        if (r3 == 0) goto L_0x1039;
    L_0x1036:
        r3 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        goto L_0x103b;
    L_0x1039:
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
    L_0x103b:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = r1.linkPreviewHeight;
        r2 = r2 - r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r12 = r2 - r3;
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r3 = r1.currentMessageObject;
        r3 = r3.isOutOwner();
        if (r3 == 0) goto L_0x1056;
    L_0x1053:
        r3 = "chat_outPreviewLine";
        goto L_0x1058;
    L_0x1056:
        r3 = "chat_inPreviewLine";
    L_0x1058:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r3 = (float) r11;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r2 = r12 - r2;
        r4 = (float) r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 + r11;
        r5 = (float) r2;
        r2 = r1.linkPreviewHeight;
        r2 = r2 + r12;
        r6 = (float) r2;
        r7 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r2 = r29;
        r2.drawRect(r3, r4, r5, r6, r7);
        r2 = r1.siteNameLayout;
        if (r2 == 0) goto L_0x10d1;
    L_0x107c:
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r3 = r1.currentMessageObject;
        r3 = r3.isOutOwner();
        if (r3 == 0) goto L_0x1089;
    L_0x1086:
        r3 = "chat_outSiteNameText";
        goto L_0x108b;
    L_0x1089:
        r3 = "chat_inSiteNameText";
    L_0x108b:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r29.save();
        r2 = r1.siteNameRtl;
        if (r2 == 0) goto L_0x10a6;
    L_0x1099:
        r2 = r1.backgroundWidth;
        r3 = r1.siteNameWidth;
        r2 = r2 - r3;
        r3 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        goto L_0x10b0;
    L_0x10a6:
        r2 = r1.hasInvoicePreview;
        if (r2 == 0) goto L_0x10ac;
    L_0x10aa:
        r2 = 0;
        goto L_0x10b0;
    L_0x10ac:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
    L_0x10b0:
        r2 = r2 + r11;
        r2 = (float) r2;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r3 = r12 - r3;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.siteNameLayout;
        r2.draw(r8);
        r29.restore();
        r2 = r1.siteNameLayout;
        r3 = r2.getLineCount();
        r3 = r3 - r10;
        r2 = r2.getLineBottom(r3);
        r2 = r2 + r12;
        goto L_0x10d2;
    L_0x10d1:
        r2 = r12;
    L_0x10d2:
        r3 = r1.currentMessageObject;
        r3 = r3.isOutOwner();
        if (r3 == 0) goto L_0x10e6;
    L_0x10da:
        r3 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r4 = "chat_messageTextOut";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setColor(r4);
        goto L_0x10f1;
    L_0x10e6:
        r3 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r4 = "chat_messageTextIn";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setColor(r4);
    L_0x10f1:
        r3 = r1.descriptionLayout;
        if (r3 == 0) goto L_0x111d;
    L_0x10f5:
        if (r2 == r12) goto L_0x10fc;
    L_0x10f7:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 + r3;
    L_0x10fc:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r2 = r2 - r3;
        r1.descriptionY = r2;
        r29.save();
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r11 = r11 + r2;
        r2 = r1.descriptionX;
        r11 = r11 + r2;
        r2 = (float) r11;
        r3 = r1.descriptionY;
        r3 = (float) r3;
        r8.translate(r2, r3);
        r2 = r1.descriptionLayout;
        r2.draw(r8);
        r29.restore();
    L_0x111d:
        r1.drawTime = r10;
    L_0x111f:
        r2 = r1.documentAttachType;
        if (r2 != r10) goto L_0x144d;
    L_0x1123:
        r2 = r1.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x116a;
    L_0x112b:
        r2 = org.telegram.ui.ActionBar.Theme.chat_docNamePaint;
        r3 = "chat_outFileNameText";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
        r3 = r28.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x1141;
    L_0x113e:
        r3 = "chat_outFileInfoSelectedText";
        goto L_0x1143;
    L_0x1141:
        r3 = "chat_outFileInfoText";
    L_0x1143:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r3 = r28.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x1155;
    L_0x1152:
        r3 = "chat_outFileBackgroundSelected";
        goto L_0x1157;
    L_0x1155:
        r3 = "chat_outFileBackground";
    L_0x1157:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = r28.isDrawSelectionBackground();
        if (r2 == 0) goto L_0x1167;
    L_0x1164:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgOutMenuSelectedDrawable;
        goto L_0x11a8;
    L_0x1167:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgOutMenuDrawable;
        goto L_0x11a8;
    L_0x116a:
        r2 = org.telegram.ui.ActionBar.Theme.chat_docNamePaint;
        r3 = "chat_inFileNameText";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
        r3 = r28.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x1180;
    L_0x117d:
        r3 = "chat_inFileInfoSelectedText";
        goto L_0x1182;
    L_0x1180:
        r3 = "chat_inFileInfoText";
    L_0x1182:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r3 = r28.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x1194;
    L_0x1191:
        r3 = "chat_inFileBackgroundSelected";
        goto L_0x1196;
    L_0x1194:
        r3 = "chat_inFileBackground";
    L_0x1196:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setColor(r3);
        r2 = r28.isDrawSelectionBackground();
        if (r2 == 0) goto L_0x11a6;
    L_0x11a3:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgInMenuSelectedDrawable;
        goto L_0x11a8;
    L_0x11a6:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgInMenuDrawable;
    L_0x11a8:
        r3 = r1.drawPhotoImage;
        if (r3 == 0) goto L_0x134a;
    L_0x11ac:
        r3 = r1.currentMessageObject;
        r3 = r3.type;
        if (r3 != 0) goto L_0x11d5;
    L_0x11b2:
        r3 = r1.photoImage;
        r3 = r3.getImageX();
        r4 = r1.backgroundWidth;
        r3 = r3 + r4;
        r4 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r1.otherX = r3;
        r4 = r1.photoImage;
        r4 = r4.getImageY();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r4 = r4 + r5;
        r1.otherY = r4;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r2, r3, r4);
        goto L_0x11f7;
    L_0x11d5:
        r3 = r1.photoImage;
        r3 = r3.getImageX();
        r4 = r1.backgroundWidth;
        r3 = r3 + r4;
        r4 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r1.otherX = r3;
        r4 = r1.photoImage;
        r4 = r4.getImageY();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r4 = r4 + r5;
        r1.otherY = r4;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r2, r3, r4);
    L_0x11f7:
        r3 = r1.photoImage;
        r3 = r3.getImageX();
        r4 = r1.photoImage;
        r4 = r4.getImageWidth();
        r3 = r3 + r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r3 = r3 + r4;
        r4 = r1.photoImage;
        r4 = r4.getImageY();
        r5 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r4 = r4 + r5;
        r5 = r1.photoImage;
        r5 = r5.getImageY();
        r6 = r1.docTitleLayout;
        if (r6 == 0) goto L_0x122f;
    L_0x121e:
        r7 = r6.getLineCount();
        r7 = r7 - r10;
        r6 = r6.getLineBottom(r7);
        r7 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r6 = r6 + r7;
        goto L_0x1233;
    L_0x122f:
        r6 = org.telegram.messenger.AndroidUtilities.dp(r18);
    L_0x1233:
        r5 = r5 + r6;
        if (r0 != 0) goto L_0x1305;
    L_0x1236:
        r0 = r1.currentMessageObject;
        r0 = r0.isOutOwner();
        if (r0 == 0) goto L_0x1281;
    L_0x123e:
        r0 = r1.radialProgress;
        r6 = "chat_outLoader";
        r7 = "chat_outLoaderSelected";
        r11 = "chat_outMediaIcon";
        r12 = "chat_outMediaIconSelected";
        r0.setColors(r6, r7, r11, r12);
        r0 = r1.radialProgress;
        r6 = r28.isDrawSelectionBackground();
        if (r6 == 0) goto L_0x1256;
    L_0x1253:
        r6 = "chat_outFileProgressSelected";
        goto L_0x1258;
    L_0x1256:
        r6 = "chat_outFileProgress";
    L_0x1258:
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setProgressColor(r6);
        r0 = r1.videoRadialProgress;
        r6 = "chat_outLoader";
        r7 = "chat_outLoaderSelected";
        r11 = "chat_outMediaIcon";
        r12 = "chat_outMediaIconSelected";
        r0.setColors(r6, r7, r11, r12);
        r0 = r1.videoRadialProgress;
        r6 = r28.isDrawSelectionBackground();
        if (r6 == 0) goto L_0x1277;
    L_0x1274:
        r6 = "chat_outFileProgressSelected";
        goto L_0x1279;
    L_0x1277:
        r6 = "chat_outFileProgress";
    L_0x1279:
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setProgressColor(r6);
        goto L_0x12c3;
    L_0x1281:
        r0 = r1.radialProgress;
        r6 = "chat_inLoader";
        r7 = "chat_inLoaderSelected";
        r11 = "chat_inMediaIcon";
        r12 = "chat_inMediaIconSelected";
        r0.setColors(r6, r7, r11, r12);
        r0 = r1.radialProgress;
        r6 = r28.isDrawSelectionBackground();
        if (r6 == 0) goto L_0x1299;
    L_0x1296:
        r6 = "chat_inFileProgressSelected";
        goto L_0x129b;
    L_0x1299:
        r6 = "chat_inFileProgress";
    L_0x129b:
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setProgressColor(r6);
        r0 = r1.videoRadialProgress;
        r6 = "chat_inLoader";
        r7 = "chat_inLoaderSelected";
        r11 = "chat_inMediaIcon";
        r12 = "chat_inMediaIconSelected";
        r0.setColors(r6, r7, r11, r12);
        r0 = r1.videoRadialProgress;
        r6 = r28.isDrawSelectionBackground();
        if (r6 == 0) goto L_0x12ba;
    L_0x12b7:
        r6 = "chat_inFileProgressSelected";
        goto L_0x12bc;
    L_0x12ba:
        r6 = "chat_inFileProgress";
    L_0x12bc:
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setProgressColor(r6);
    L_0x12c3:
        r0 = r1.rect;
        r6 = r1.photoImage;
        r6 = r6.getImageX();
        r6 = (float) r6;
        r7 = r1.photoImage;
        r7 = r7.getImageY();
        r7 = (float) r7;
        r11 = r1.photoImage;
        r11 = r11.getImageX();
        r12 = r1.photoImage;
        r12 = r12.getImageWidth();
        r11 = r11 + r12;
        r11 = (float) r11;
        r12 = r1.photoImage;
        r12 = r12.getImageY();
        r14 = r1.photoImage;
        r14 = r14.getImageHeight();
        r12 = r12 + r14;
        r12 = (float) r12;
        r0.set(r6, r7, r11, r12);
        r0 = r1.rect;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r6 = (float) r6;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r7 = (float) r7;
        r11 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r8.drawRoundRect(r0, r6, r7, r11);
        goto L_0x1415;
    L_0x1305:
        r0 = r1.radialProgress;
        r6 = "chat_mediaLoaderPhoto";
        r7 = "chat_mediaLoaderPhotoSelected";
        r11 = "chat_mediaLoaderPhotoIcon";
        r12 = "chat_mediaLoaderPhotoIconSelected";
        r0.setColors(r6, r7, r11, r12);
        r0 = r1.radialProgress;
        r6 = "chat_mediaProgress";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setProgressColor(r6);
        r0 = r1.videoRadialProgress;
        r6 = "chat_mediaLoaderPhoto";
        r7 = "chat_mediaLoaderPhotoSelected";
        r11 = "chat_mediaLoaderPhotoIcon";
        r12 = "chat_mediaLoaderPhotoIconSelected";
        r0.setColors(r6, r7, r11, r12);
        r0 = r1.videoRadialProgress;
        r6 = "chat_mediaProgress";
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setProgressColor(r6);
        r0 = r1.buttonState;
        r6 = -1;
        if (r0 != r6) goto L_0x1415;
    L_0x133a:
        r0 = r1.radialProgress;
        r0 = r0.getIcon();
        r6 = 4;
        if (r0 == r6) goto L_0x1415;
    L_0x1343:
        r0 = r1.radialProgress;
        r0.setIcon(r6, r10, r10);
        goto L_0x1415;
    L_0x134a:
        r0 = r1.buttonX;
        r3 = r1.backgroundWidth;
        r0 = r0 + r3;
        r3 = r1.currentMessageObject;
        r3 = r3.type;
        if (r3 != 0) goto L_0x1358;
    L_0x1355:
        r3 = NUM; // 0x42680000 float:58.0 double:5.50444465E-315;
        goto L_0x135a;
    L_0x1358:
        r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
    L_0x135a:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r1.otherX = r0;
        r3 = r1.buttonY;
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r1.otherY = r3;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r2, r0, r3);
        r0 = r1.buttonX;
        r3 = NUM; // 0x42540000 float:53.0 double:5.49796883E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r3 + r0;
        r0 = r1.buttonY;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r4 = r4 + r0;
        r0 = r1.buttonY;
        r5 = NUM; // 0x41d80000 float:27.0 double:5.457818764E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r0 = r0 + r5;
        r5 = r1.docTitleLayout;
        if (r5 == 0) goto L_0x13a7;
    L_0x138c:
        r5 = r5.getLineCount();
        if (r5 <= r10) goto L_0x13a7;
    L_0x1392:
        r5 = r1.docTitleLayout;
        r5 = r5.getLineCount();
        r5 = r5 - r10;
        r6 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 * r6;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r5 = r5 + r6;
        r0 = r0 + r5;
    L_0x13a7:
        r5 = r0;
        r0 = r1.currentMessageObject;
        r0 = r0.isOutOwner();
        if (r0 == 0) goto L_0x13e3;
    L_0x13b0:
        r0 = r1.radialProgress;
        r6 = r28.isDrawSelectionBackground();
        if (r6 != 0) goto L_0x13c0;
    L_0x13b8:
        r6 = r1.buttonPressed;
        if (r6 == 0) goto L_0x13bd;
    L_0x13bc:
        goto L_0x13c0;
    L_0x13bd:
        r6 = "chat_outAudioProgress";
        goto L_0x13c2;
    L_0x13c0:
        r6 = "chat_outAudioSelectedProgress";
    L_0x13c2:
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setProgressColor(r6);
        r0 = r1.videoRadialProgress;
        r6 = r28.isDrawSelectionBackground();
        if (r6 != 0) goto L_0x13d9;
    L_0x13d1:
        r6 = r1.videoButtonPressed;
        if (r6 == 0) goto L_0x13d6;
    L_0x13d5:
        goto L_0x13d9;
    L_0x13d6:
        r6 = "chat_outAudioProgress";
        goto L_0x13db;
    L_0x13d9:
        r6 = "chat_outAudioSelectedProgress";
    L_0x13db:
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setProgressColor(r6);
        goto L_0x1415;
    L_0x13e3:
        r0 = r1.radialProgress;
        r6 = r28.isDrawSelectionBackground();
        if (r6 != 0) goto L_0x13f3;
    L_0x13eb:
        r6 = r1.buttonPressed;
        if (r6 == 0) goto L_0x13f0;
    L_0x13ef:
        goto L_0x13f3;
    L_0x13f0:
        r6 = "chat_inAudioProgress";
        goto L_0x13f5;
    L_0x13f3:
        r6 = "chat_inAudioSelectedProgress";
    L_0x13f5:
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setProgressColor(r6);
        r0 = r1.videoRadialProgress;
        r6 = r28.isDrawSelectionBackground();
        if (r6 != 0) goto L_0x140c;
    L_0x1404:
        r6 = r1.videoButtonPressed;
        if (r6 == 0) goto L_0x1409;
    L_0x1408:
        goto L_0x140c;
    L_0x1409:
        r6 = "chat_inAudioProgress";
        goto L_0x140e;
    L_0x140c:
        r6 = "chat_inAudioSelectedProgress";
    L_0x140e:
        r6 = org.telegram.ui.ActionBar.Theme.getColor(r6);
        r0.setProgressColor(r6);
    L_0x1415:
        r2.draw(r8);
        r0 = r1.docTitleLayout;	 Catch:{ Exception -> 0x1430 }
        if (r0 == 0) goto L_0x1434;
    L_0x141c:
        r29.save();	 Catch:{ Exception -> 0x1430 }
        r0 = r1.docTitleOffsetX;	 Catch:{ Exception -> 0x1430 }
        r0 = r0 + r3;
        r0 = (float) r0;	 Catch:{ Exception -> 0x1430 }
        r2 = (float) r4;	 Catch:{ Exception -> 0x1430 }
        r8.translate(r0, r2);	 Catch:{ Exception -> 0x1430 }
        r0 = r1.docTitleLayout;	 Catch:{ Exception -> 0x1430 }
        r0.draw(r8);	 Catch:{ Exception -> 0x1430 }
        r29.restore();	 Catch:{ Exception -> 0x1430 }
        goto L_0x1434;
    L_0x1430:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x1434:
        r0 = r1.infoLayout;	 Catch:{ Exception -> 0x1449 }
        if (r0 == 0) goto L_0x144d;
    L_0x1438:
        r29.save();	 Catch:{ Exception -> 0x1449 }
        r0 = (float) r3;	 Catch:{ Exception -> 0x1449 }
        r2 = (float) r5;	 Catch:{ Exception -> 0x1449 }
        r8.translate(r0, r2);	 Catch:{ Exception -> 0x1449 }
        r0 = r1.infoLayout;	 Catch:{ Exception -> 0x1449 }
        r0.draw(r8);	 Catch:{ Exception -> 0x1449 }
        r29.restore();	 Catch:{ Exception -> 0x1449 }
        goto L_0x144d;
    L_0x1449:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x144d:
        r0 = r1.buttonState;
        r2 = -1;
        if (r0 != r2) goto L_0x14ef;
    L_0x1452:
        r0 = r1.currentMessageObject;
        r0 = r0.needDrawBluredPreview();
        if (r0 == 0) goto L_0x14ef;
    L_0x145a:
        r0 = org.telegram.messenger.MediaController.getInstance();
        r2 = r1.currentMessageObject;
        r0 = r0.isPlayingMessage(r2);
        if (r0 != 0) goto L_0x14ef;
    L_0x1466:
        r0 = r1.photoImage;
        r0 = r0.getVisible();
        if (r0 == 0) goto L_0x14ef;
    L_0x146e:
        r0 = r1.currentMessageObject;
        r2 = r0.messageOwner;
        r2 = r2.destroyTime;
        if (r2 == 0) goto L_0x14ef;
    L_0x1476:
        r0 = r0.isOutOwner();
        if (r0 != 0) goto L_0x14ea;
    L_0x147c:
        r2 = java.lang.System.currentTimeMillis();
        r0 = r1.currentAccount;
        r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0);
        r0 = r0.getTimeDifference();
        r0 = r0 * 1000;
        r4 = (long) r0;
        r2 = r2 + r4;
        r4 = 0;
        r0 = r1.currentMessageObject;
        r0 = r0.messageOwner;
        r0 = r0.destroyTime;
        r6 = (long) r0;
        r11 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r6 = r6 * r11;
        r6 = r6 - r2;
        r2 = java.lang.Math.max(r4, r6);
        r0 = (float) r2;
        r2 = r1.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.ttl;
        r2 = (float) r2;
        r3 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r2 = r2 * r3;
        r0 = r0 / r2;
        r2 = org.telegram.ui.ActionBar.Theme.chat_deleteProgressPaint;
        r3 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r4 = r1.controlsAlpha;
        r4 = r4 * r3;
        r3 = (int) r4;
        r2.setAlpha(r3);
        r3 = r1.deleteProgressRect;
        r4 = -NUM; // 0xffffffffc2b40000 float:-90.0 double:NaN;
        r2 = -NUM; // 0xffffffffc3b40000 float:-360.0 double:NaN;
        r5 = r0 * r2;
        r6 = 1;
        r7 = org.telegram.ui.ActionBar.Theme.chat_deleteProgressPaint;
        r2 = r29;
        r2.drawArc(r3, r4, r5, r6, r7);
        r2 = 0;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 == 0) goto L_0x14ea;
    L_0x14ce:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r1.deleteProgressRect;
        r3 = r2.left;
        r3 = (int) r3;
        r3 = r3 - r0;
        r4 = r2.top;
        r4 = (int) r4;
        r4 = r4 - r0;
        r5 = r2.right;
        r5 = (int) r5;
        r6 = 2;
        r0 = r0 * 2;
        r5 = r5 + r0;
        r2 = r2.bottom;
        r2 = (int) r2;
        r2 = r2 + r0;
        r1.invalidate(r3, r4, r5, r2);
    L_0x14ea:
        r0 = r1.currentMessageObject;
        r1.updateSecretTimeText(r0);
    L_0x14ef:
        r0 = r1.currentMessageObject;
        r2 = r0.type;
        r3 = 4;
        if (r2 != r3) goto L_0x1560;
    L_0x14f6:
        r0 = r0.messageOwner;
        r0 = r0.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r0 != 0) goto L_0x1560;
    L_0x14fe:
        r0 = r1.currentMapProvider;
        r2 = 2;
        if (r0 != r2) goto L_0x1560;
    L_0x1503:
        r0 = r1.photoImage;
        r0 = r0.hasNotThumb();
        if (r0 == 0) goto L_0x1560;
    L_0x150b:
        r0 = org.telegram.ui.ActionBar.Theme.chat_redLocationIcon;
        r0 = r0.getIntrinsicWidth();
        r0 = (float) r0;
        r2 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
        r0 = r0 * r2;
        r0 = (int) r0;
        r2 = org.telegram.ui.ActionBar.Theme.chat_redLocationIcon;
        r2 = r2.getIntrinsicHeight();
        r2 = (float) r2;
        r3 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
        r2 = r2 * r3;
        r2 = (int) r2;
        r3 = r1.photoImage;
        r3 = r3.getImageX();
        r4 = r1.photoImage;
        r4 = r4.getImageWidth();
        r4 = r4 - r0;
        r5 = 2;
        r4 = r4 / r5;
        r3 = r3 + r4;
        r4 = r1.photoImage;
        r4 = r4.getImageY();
        r6 = r1.photoImage;
        r6 = r6.getImageHeight();
        r6 = r6 / r5;
        r6 = r6 - r2;
        r4 = r4 + r6;
        r5 = org.telegram.ui.ActionBar.Theme.chat_redLocationIcon;
        r6 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r7 = r1.photoImage;
        r7 = r7.getCurrentAlpha();
        r7 = r7 * r6;
        r6 = (int) r7;
        r5.setAlpha(r6);
        r5 = org.telegram.ui.ActionBar.Theme.chat_redLocationIcon;
        r0 = r0 + r3;
        r2 = r2 + r4;
        r5.setBounds(r3, r4, r0, r2);
        r0 = org.telegram.ui.ActionBar.Theme.chat_redLocationIcon;
        r0.draw(r8);
    L_0x1560:
        r0 = r1.botButtons;
        r0 = r0.isEmpty();
        if (r0 != 0) goto L_0x17d0;
    L_0x1568:
        r0 = r1.currentMessageObject;
        r0 = r0.isOutOwner();
        if (r0 == 0) goto L_0x157d;
    L_0x1570:
        r0 = r28.getMeasuredWidth();
        r2 = r1.widthForButtons;
        r0 = r0 - r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r0 = r0 - r2;
        goto L_0x158d;
    L_0x157d:
        r0 = r1.backgroundDrawableLeft;
        r2 = r1.mediaBackground;
        if (r2 == 0) goto L_0x1586;
    L_0x1583:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x1588;
    L_0x1586:
        r2 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
    L_0x1588:
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 + r2;
    L_0x158d:
        r11 = 0;
    L_0x158e:
        r2 = r1.botButtons;
        r2 = r2.size();
        if (r11 >= r2) goto L_0x17d0;
    L_0x1596:
        r2 = r1.botButtons;
        r2 = r2.get(r11);
        r12 = r2;
        r12 = (org.telegram.ui.Cells.ChatMessageCell.BotButton) r12;
        r2 = r12.y;
        r3 = r1.layoutHeight;
        r2 = r2 + r3;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r19);
        r2 = r2 - r3;
        r3 = org.telegram.ui.ActionBar.Theme.chat_systemDrawable;
        r4 = r1.pressedBotButton;
        if (r11 != r4) goto L_0x15b4;
    L_0x15b1:
        r4 = org.telegram.ui.ActionBar.Theme.colorPressedFilter;
        goto L_0x15b6;
    L_0x15b4:
        r4 = org.telegram.ui.ActionBar.Theme.colorFilter;
    L_0x15b6:
        r3.setColorFilter(r4);
        r3 = org.telegram.ui.ActionBar.Theme.chat_systemDrawable;
        r4 = r12.x;
        r4 = r4 + r0;
        r5 = r12.x;
        r5 = r5 + r0;
        r6 = r12.width;
        r5 = r5 + r6;
        r6 = r12.height;
        r6 = r6 + r2;
        r3.setBounds(r4, r2, r5, r6);
        r3 = org.telegram.ui.ActionBar.Theme.chat_systemDrawable;
        r3.draw(r8);
        r29.save();
        r3 = r12.x;
        r3 = r3 + r0;
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 + r4;
        r3 = (float) r3;
        r4 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = r12.title;
        r6 = r12.title;
        r6 = r6.getLineCount();
        r6 = r6 - r10;
        r5 = r5.getLineBottom(r6);
        r4 = r4 - r5;
        r14 = 2;
        r4 = r4 / r14;
        r4 = r4 + r2;
        r4 = (float) r4;
        r8.translate(r3, r4);
        r3 = r12.title;
        r3.draw(r8);
        r29.restore();
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrl;
        if (r3 == 0) goto L_0x1640;
    L_0x1618:
        r3 = r12.x;
        r4 = r12.width;
        r3 = r3 + r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r3 = r3 - r4;
        r4 = org.telegram.ui.ActionBar.Theme.chat_botLinkDrawalbe;
        r4 = r4.getIntrinsicWidth();
        r3 = r3 - r4;
        r3 = r3 + r0;
        r4 = org.telegram.ui.ActionBar.Theme.chat_botLinkDrawalbe;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r2 = r2 + r5;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r4, r3, r2);
        r2 = org.telegram.ui.ActionBar.Theme.chat_botLinkDrawalbe;
        r2.draw(r8);
    L_0x163d:
        r7 = 0;
        goto L_0x17cc;
    L_0x1640:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
        if (r3 == 0) goto L_0x166e;
    L_0x1648:
        r3 = r12.x;
        r4 = r12.width;
        r3 = r3 + r4;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r3 = r3 - r4;
        r4 = org.telegram.ui.ActionBar.Theme.chat_botInlineDrawable;
        r4 = r4.getIntrinsicWidth();
        r3 = r3 - r4;
        r3 = r3 + r0;
        r4 = org.telegram.ui.ActionBar.Theme.chat_botInlineDrawable;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r21);
        r2 = r2 + r5;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r4, r3, r2);
        r2 = org.telegram.ui.ActionBar.Theme.chat_botInlineDrawable;
        r2.draw(r8);
        goto L_0x163d;
    L_0x166e:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
        if (r3 != 0) goto L_0x1696;
    L_0x1676:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
        if (r3 != 0) goto L_0x1696;
    L_0x167e:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
        if (r3 != 0) goto L_0x1696;
    L_0x1686:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r3 != 0) goto L_0x1696;
    L_0x168e:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth;
        if (r3 == 0) goto L_0x163d;
    L_0x1696:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonCallback;
        if (r3 != 0) goto L_0x16b6;
    L_0x169e:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonGame;
        if (r3 != 0) goto L_0x16b6;
    L_0x16a6:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r3 != 0) goto L_0x16b6;
    L_0x16ae:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonUrlAuth;
        if (r3 == 0) goto L_0x16c8;
    L_0x16b6:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.SendMessagesHelper.getInstance(r3);
        r4 = r1.currentMessageObject;
        r5 = r12.button;
        r3 = r3.isSendingCallback(r4, r5);
        if (r3 != 0) goto L_0x16e5;
    L_0x16c8:
        r3 = r12.button;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
        if (r3 == 0) goto L_0x16e3;
    L_0x16d0:
        r3 = r1.currentAccount;
        r3 = org.telegram.messenger.SendMessagesHelper.getInstance(r3);
        r4 = r1.currentMessageObject;
        r5 = r12.button;
        r3 = r3.isSendingCurrentLocation(r4, r5);
        if (r3 == 0) goto L_0x16e3;
    L_0x16e2:
        goto L_0x16e5;
    L_0x16e3:
        r15 = 0;
        goto L_0x16e6;
    L_0x16e5:
        r15 = 1;
    L_0x16e6:
        if (r15 != 0) goto L_0x16f3;
    L_0x16e8:
        if (r15 != 0) goto L_0x163d;
    L_0x16ea:
        r3 = r12.progressAlpha;
        r4 = 0;
        r3 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1));
        if (r3 == 0) goto L_0x163d;
    L_0x16f3:
        r3 = org.telegram.ui.ActionBar.Theme.chat_botProgressPaint;
        r4 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r5 = r12.progressAlpha;
        r6 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r5 = r5 * r6;
        r5 = (int) r5;
        r4 = java.lang.Math.min(r4, r5);
        r3.setAlpha(r4);
        r3 = r12.x;
        r4 = r12.width;
        r3 = r3 + r4;
        r4 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r3 = r3 + r0;
        r4 = r1.rect;
        r5 = (float) r3;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r22);
        r6 = r6 + r2;
        r6 = (float) r6;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r3 = r3 + r7;
        r3 = (float) r3;
        r7 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r2 = r2 + r7;
        r2 = (float) r2;
        r4.set(r5, r6, r3, r2);
        r3 = r1.rect;
        r2 = r12.angle;
        r4 = (float) r2;
        r5 = NUM; // 0x435CLASSNAME float:220.0 double:5.58344962E-315;
        r6 = 0;
        r7 = org.telegram.ui.ActionBar.Theme.chat_botProgressPaint;
        r2 = r29;
        r2.drawArc(r3, r4, r5, r6, r7);
        r28.invalidate();
        r2 = java.lang.System.currentTimeMillis();
        r4 = r12.lastUpdateTime;
        r6 = java.lang.System.currentTimeMillis();
        r4 = r4 - r6;
        r4 = java.lang.Math.abs(r4);
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r16 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1));
        if (r16 >= 0) goto L_0x17c8;
    L_0x175d:
        r4 = r12.lastUpdateTime;
        r4 = r2 - r4;
        r6 = 360; // 0x168 float:5.04E-43 double:1.78E-321;
        r6 = r6 * r4;
        r6 = (float) r6;
        r7 = NUM; // 0x44fa0000 float:2000.0 double:5.717499035E-315;
        r6 = r6 / r7;
        r7 = r12.angle;
        r7 = (float) r7;
        r7 = r7 + r6;
        r6 = (int) r7;
        r12.angle = r6;
        r6 = r12.angle;
        r7 = r12.angle;
        r7 = r7 / 360;
        r7 = r7 * 360;
        r6 = r6 - r7;
        r12.angle = r6;
        if (r15 == 0) goto L_0x17a7;
    L_0x1787:
        r6 = r12.progressAlpha;
        r6 = (r6 > r13 ? 1 : (r6 == r13 ? 0 : -1));
        if (r6 >= 0) goto L_0x17c8;
    L_0x178f:
        r6 = r12.progressAlpha;
        r4 = (float) r4;
        r5 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r4 = r4 / r5;
        r6 = r6 + r4;
        r12.progressAlpha = r6;
        r4 = r12.progressAlpha;
        r4 = (r4 > r13 ? 1 : (r4 == r13 ? 0 : -1));
        if (r4 <= 0) goto L_0x17c8;
    L_0x17a3:
        r12.progressAlpha = r13;
        goto L_0x17c8;
    L_0x17a7:
        r6 = r12.progressAlpha;
        r7 = 0;
        r6 = (r6 > r7 ? 1 : (r6 == r7 ? 0 : -1));
        if (r6 <= 0) goto L_0x17c9;
    L_0x17b0:
        r6 = r12.progressAlpha;
        r4 = (float) r4;
        r5 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r4 = r4 / r5;
        r6 = r6 - r4;
        r12.progressAlpha = r6;
        r4 = r12.progressAlpha;
        r4 = (r4 > r7 ? 1 : (r4 == r7 ? 0 : -1));
        if (r4 >= 0) goto L_0x17c9;
    L_0x17c4:
        r12.progressAlpha = r7;
        goto L_0x17c9;
    L_0x17c8:
        r7 = 0;
    L_0x17c9:
        r12.lastUpdateTime = r2;
    L_0x17cc:
        r11 = r11 + 1;
        goto L_0x158e;
    L_0x17d0:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawContent(android.graphics.Canvas):void");
    }

    private int getMiniIconForCurrentState() {
        int i = this.miniButtonState;
        if (i < 0) {
            return 4;
        }
        return i == 0 ? 2 : 3;
    }

    /* JADX WARNING: Missing block: B:47:0x0097, code skipped:
            if (r0.attachPathExists == false) goto L_0x009a;
     */
    private int getIconForCurrentState() {
        /*
        r15 = this;
        r0 = r15.documentAttachType;
        r1 = "chat_outMediaIconSelected";
        r2 = "chat_outMediaIcon";
        r3 = "chat_outLoaderSelected";
        r4 = "chat_outLoader";
        r5 = "chat_inMediaIconSelected";
        r6 = "chat_inMediaIcon";
        r7 = "chat_inLoaderSelected";
        r8 = "chat_inLoader";
        r9 = 0;
        r10 = 4;
        r11 = 2;
        r12 = 3;
        r13 = 1;
        if (r0 == r12) goto L_0x00c0;
    L_0x0019:
        r14 = 5;
        if (r0 != r14) goto L_0x001e;
    L_0x001c:
        goto L_0x00c0;
    L_0x001e:
        if (r0 != r13) goto L_0x0043;
    L_0x0020:
        r0 = r15.drawPhotoImage;
        if (r0 != 0) goto L_0x0043;
    L_0x0024:
        r0 = r15.currentMessageObject;
        r0 = r0.isOutOwner();
        if (r0 == 0) goto L_0x0032;
    L_0x002c:
        r0 = r15.radialProgress;
        r0.setColors(r4, r3, r2, r1);
        goto L_0x0037;
    L_0x0032:
        r0 = r15.radialProgress;
        r0.setColors(r8, r7, r6, r5);
    L_0x0037:
        r0 = r15.buttonState;
        r1 = -1;
        if (r0 != r1) goto L_0x003d;
    L_0x003c:
        return r14;
    L_0x003d:
        if (r0 != 0) goto L_0x0040;
    L_0x003f:
        return r11;
    L_0x0040:
        if (r0 != r13) goto L_0x00bf;
    L_0x0042:
        return r12;
    L_0x0043:
        r0 = r15.radialProgress;
        r1 = "chat_mediaLoaderPhoto";
        r2 = "chat_mediaLoaderPhotoSelected";
        r3 = "chat_mediaLoaderPhotoIcon";
        r4 = "chat_mediaLoaderPhotoIconSelected";
        r0.setColors(r1, r2, r3, r4);
        r0 = r15.videoRadialProgress;
        r2 = "chat_mediaLoaderPhotoSelected";
        r3 = "chat_mediaLoaderPhotoIcon";
        r4 = "chat_mediaLoaderPhotoIconSelected";
        r0.setColors(r1, r2, r3, r4);
        r0 = r15.buttonState;
        if (r0 < 0) goto L_0x0072;
    L_0x005f:
        if (r0 >= r10) goto L_0x0072;
    L_0x0061:
        if (r0 != 0) goto L_0x0064;
    L_0x0063:
        return r11;
    L_0x0064:
        if (r0 != r13) goto L_0x0067;
    L_0x0066:
        return r12;
    L_0x0067:
        if (r0 != r11) goto L_0x006a;
    L_0x0069:
        return r9;
    L_0x006a:
        if (r0 != r12) goto L_0x00bf;
    L_0x006c:
        r0 = r15.autoPlayingMedia;
        if (r0 == 0) goto L_0x0071;
    L_0x0070:
        r9 = 4;
    L_0x0071:
        return r9;
    L_0x0072:
        r0 = r15.buttonState;
        r1 = -1;
        if (r0 != r1) goto L_0x00bf;
    L_0x0077:
        r0 = r15.documentAttachType;
        if (r0 != r13) goto L_0x009c;
    L_0x007b:
        r0 = r15.drawPhotoImage;
        if (r0 == 0) goto L_0x009a;
    L_0x007f:
        r0 = r15.currentPhotoObject;
        if (r0 != 0) goto L_0x0087;
    L_0x0083:
        r0 = r15.currentPhotoObjectThumb;
        if (r0 == 0) goto L_0x009a;
    L_0x0087:
        r0 = r15.photoImage;
        r0 = r0.hasBitmapImage();
        if (r0 != 0) goto L_0x009b;
    L_0x008f:
        r0 = r15.currentMessageObject;
        r1 = r0.mediaExists;
        if (r1 != 0) goto L_0x009b;
    L_0x0095:
        r0 = r0.attachPathExists;
        if (r0 == 0) goto L_0x009a;
    L_0x0099:
        goto L_0x009b;
    L_0x009a:
        r10 = 5;
    L_0x009b:
        return r10;
    L_0x009c:
        r0 = r15.currentMessageObject;
        r0 = r0.needDrawBluredPreview();
        if (r0 == 0) goto L_0x00ba;
    L_0x00a4:
        r0 = r15.currentMessageObject;
        r1 = r0.messageOwner;
        r1 = r1.destroyTime;
        if (r1 == 0) goto L_0x00b8;
    L_0x00ac:
        r0 = r0.isOutOwner();
        if (r0 == 0) goto L_0x00b5;
    L_0x00b2:
        r0 = 9;
        return r0;
    L_0x00b5:
        r0 = 11;
        return r0;
    L_0x00b8:
        r0 = 7;
        return r0;
    L_0x00ba:
        r0 = r15.hasEmbed;
        if (r0 == 0) goto L_0x00bf;
    L_0x00be:
        return r9;
    L_0x00bf:
        return r10;
    L_0x00c0:
        r0 = r15.currentMessageObject;
        r0 = r0.isOutOwner();
        if (r0 == 0) goto L_0x00ce;
    L_0x00c8:
        r0 = r15.radialProgress;
        r0.setColors(r4, r3, r2, r1);
        goto L_0x00d3;
    L_0x00ce:
        r0 = r15.radialProgress;
        r0.setColors(r8, r7, r6, r5);
    L_0x00d3:
        r0 = r15.buttonState;
        if (r0 != r13) goto L_0x00d8;
    L_0x00d7:
        return r13;
    L_0x00d8:
        if (r0 != r11) goto L_0x00db;
    L_0x00da:
        return r11;
    L_0x00db:
        if (r0 != r10) goto L_0x00de;
    L_0x00dd:
        return r12;
    L_0x00de:
        return r9;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getIconForCurrentState():int");
    }

    private int getMaxNameWidth() {
        int dp;
        int i = this.documentAttachType;
        if (i == 6 || i == 8 || this.currentMessageObject.type == 5) {
            Point point;
            if (AndroidUtilities.isTablet()) {
                if (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.needDrawAvatar()) {
                    i = AndroidUtilities.getMinTabletSide();
                    dp = AndroidUtilities.dp(42.0f);
                } else {
                    i = AndroidUtilities.getMinTabletSide();
                    i -= this.backgroundWidth;
                    dp = AndroidUtilities.dp(57.0f);
                }
            } else if (this.isChat && !this.currentMessageObject.isOutOwner() && this.currentMessageObject.needDrawAvatar()) {
                point = AndroidUtilities.displaySize;
                i = Math.min(point.x, point.y);
                dp = AndroidUtilities.dp(42.0f);
            } else {
                point = AndroidUtilities.displaySize;
                i = Math.min(point.x, point.y);
                i -= this.backgroundWidth;
                dp = AndroidUtilities.dp(57.0f);
            }
            i -= dp;
            i -= this.backgroundWidth;
            dp = AndroidUtilities.dp(57.0f);
        } else if (this.currentMessagesGroup != null) {
            if (AndroidUtilities.isTablet()) {
                i = AndroidUtilities.getMinTabletSide();
            } else {
                i = AndroidUtilities.displaySize.x;
            }
            dp = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < this.currentMessagesGroup.posArray.size(); i3++) {
                GroupedMessagePosition groupedMessagePosition = (GroupedMessagePosition) this.currentMessagesGroup.posArray.get(i3);
                if (groupedMessagePosition.minY != (byte) 0) {
                    break;
                }
                double d = (double) i2;
                double ceil = Math.ceil((double) ((((float) (groupedMessagePosition.pw + groupedMessagePosition.leftSpanOffset)) / 1000.0f) * ((float) i)));
                Double.isNaN(d);
                i2 = (int) (d + ceil);
            }
            if (this.isAvatarVisible) {
                dp = 48;
            }
            return i2 - AndroidUtilities.dp((float) (dp + 31));
        } else {
            i = this.backgroundWidth;
            dp = AndroidUtilities.dp(this.mediaBackground ? 22.0f : 31.0f);
        }
        return i - dp;
    }

    /* JADX WARNING: Removed duplicated region for block: B:78:0x0114  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00e3  */
    /* JADX WARNING: Missing block: B:42:0x009c, code skipped:
            if (r5 != 5) goto L_0x009f;
     */
    /* JADX WARNING: Missing block: B:73:0x0109, code skipped:
            if ((r2 & 2) != 0) goto L_0x0116;
     */
    public void updateButtonState(boolean r17, boolean r18, boolean r19) {
        /*
        r16 = this;
        r0 = r16;
        r1 = r17;
        r2 = 0;
        if (r18 == 0) goto L_0x0015;
    L_0x0007:
        r3 = r0.currentMessageObject;
        r3 = org.telegram.ui.PhotoViewer.isShowingImage(r3);
        if (r3 != 0) goto L_0x0013;
    L_0x000f:
        r3 = r0.attachedToWindow;
        if (r3 != 0) goto L_0x0015;
    L_0x0013:
        r3 = 0;
        goto L_0x0017;
    L_0x0015:
        r3 = r18;
    L_0x0017:
        r0.drawRadialCheckBackground = r2;
        r4 = 0;
        r5 = r0.currentMessageObject;
        r5 = r5.type;
        r6 = 5;
        r7 = 8;
        r8 = 3;
        r9 = 7;
        r10 = 4;
        r11 = 1;
        if (r5 != r11) goto L_0x003b;
    L_0x0027:
        r4 = r0.currentPhotoObject;
        if (r4 != 0) goto L_0x0031;
    L_0x002b:
        r2 = r0.radialProgress;
        r2.setIcon(r10, r1, r3);
        return;
    L_0x0031:
        r4 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r5 = r0.currentMessageObject;
        r5 = r5.mediaExists;
        goto L_0x00ab;
    L_0x003b:
        if (r5 == r7) goto L_0x0068;
    L_0x003d:
        r12 = r0.documentAttachType;
        if (r12 == r9) goto L_0x0068;
    L_0x0041:
        if (r12 == r10) goto L_0x0068;
    L_0x0043:
        if (r12 == r7) goto L_0x0068;
    L_0x0045:
        r13 = 9;
        if (r5 == r13) goto L_0x0068;
    L_0x0049:
        if (r12 == r8) goto L_0x0068;
    L_0x004b:
        if (r12 != r6) goto L_0x004e;
    L_0x004d:
        goto L_0x0068;
    L_0x004e:
        if (r12 == 0) goto L_0x005b;
    L_0x0050:
        r4 = r0.documentAttach;
        r4 = org.telegram.messenger.FileLoader.getAttachFileName(r4);
        r5 = r0.currentMessageObject;
        r5 = r5.mediaExists;
        goto L_0x00ab;
    L_0x005b:
        r5 = r0.currentPhotoObject;
        if (r5 == 0) goto L_0x009f;
    L_0x005f:
        r4 = org.telegram.messenger.FileLoader.getAttachFileName(r5);
        r5 = r0.currentMessageObject;
        r5 = r5.mediaExists;
        goto L_0x00ab;
    L_0x0068:
        r5 = r0.currentMessageObject;
        r12 = r5.useCustomPhoto;
        if (r12 == 0) goto L_0x007a;
    L_0x006e:
        r0.buttonState = r11;
        r2 = r0.radialProgress;
        r4 = r16.getIconForCurrentState();
        r2.setIcon(r4, r1, r3);
        return;
    L_0x007a:
        r12 = r5.attachPathExists;
        if (r12 == 0) goto L_0x0090;
    L_0x007e:
        r5 = r5.messageOwner;
        r5 = r5.attachPath;
        r5 = android.text.TextUtils.isEmpty(r5);
        if (r5 != 0) goto L_0x0090;
    L_0x0088:
        r4 = r0.currentMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.attachPath;
        r5 = 1;
        goto L_0x00ab;
    L_0x0090:
        r5 = r0.currentMessageObject;
        r5 = r5.isSendError();
        if (r5 == 0) goto L_0x00a1;
    L_0x0098:
        r5 = r0.documentAttachType;
        if (r5 == r8) goto L_0x00a1;
    L_0x009c:
        if (r5 != r6) goto L_0x009f;
    L_0x009e:
        goto L_0x00a1;
    L_0x009f:
        r5 = 0;
        goto L_0x00ab;
    L_0x00a1:
        r4 = r0.currentMessageObject;
        r4 = r4.getFileName();
        r5 = r0.currentMessageObject;
        r5 = r5.mediaExists;
    L_0x00ab:
        r12 = r0.currentAccount;
        r12 = org.telegram.messenger.DownloadController.getInstance(r12);
        r13 = r0.currentMessageObject;
        r12 = r12.canDownloadMedia(r13);
        r13 = r0.currentMessageObject;
        r13 = r13.isSent();
        r14 = 2;
        if (r13 == 0) goto L_0x00dc;
    L_0x00c0:
        r13 = r0.documentAttachType;
        if (r13 == r10) goto L_0x00ca;
    L_0x00c4:
        if (r13 == r9) goto L_0x00ca;
    L_0x00c6:
        if (r13 != r14) goto L_0x00dc;
    L_0x00c8:
        if (r12 == 0) goto L_0x00dc;
    L_0x00ca:
        r13 = r0.currentMessageObject;
        r13 = r13.canStreamVideo();
        if (r13 == 0) goto L_0x00dc;
    L_0x00d2:
        r13 = r0.currentMessageObject;
        r13 = r13.needDrawBluredPreview();
        if (r13 != 0) goto L_0x00dc;
    L_0x00da:
        r13 = 1;
        goto L_0x00dd;
    L_0x00dc:
        r13 = 0;
    L_0x00dd:
        r0.canStreamVideo = r13;
        r13 = org.telegram.messenger.SharedConfig.streamMedia;
        if (r13 == 0) goto L_0x0114;
    L_0x00e3:
        r13 = r0.currentMessageObject;
        r18 = r3;
        r2 = r13.getDialogId();
        r3 = (int) r2;
        if (r3 == 0) goto L_0x0116;
    L_0x00ee:
        r2 = r0.currentMessageObject;
        r2 = r2.isSecretMedia();
        if (r2 != 0) goto L_0x0116;
    L_0x00f6:
        r2 = r0.documentAttachType;
        if (r2 == r6) goto L_0x010b;
    L_0x00fa:
        r2 = r0.canStreamVideo;
        if (r2 == 0) goto L_0x0116;
    L_0x00fe:
        r2 = r0.currentPosition;
        if (r2 == 0) goto L_0x0116;
    L_0x0102:
        r2 = r2.flags;
        r3 = r2 & 1;
        if (r3 == 0) goto L_0x010b;
    L_0x0108:
        r2 = r2 & r14;
        if (r2 != 0) goto L_0x0116;
    L_0x010b:
        if (r5 == 0) goto L_0x010f;
    L_0x010d:
        r2 = 1;
        goto L_0x0110;
    L_0x010f:
        r2 = 2;
    L_0x0110:
        r0.hasMiniProgress = r2;
        r5 = 1;
        goto L_0x0116;
    L_0x0114:
        r18 = r3;
    L_0x0116:
        r2 = r0.currentMessageObject;
        r2 = r2.isSendError();
        if (r2 != 0) goto L_0x072f;
    L_0x011e:
        r2 = android.text.TextUtils.isEmpty(r4);
        if (r2 == 0) goto L_0x0136;
    L_0x0124:
        r2 = r0.currentMessageObject;
        r2 = r2.isSending();
        if (r2 != 0) goto L_0x0136;
    L_0x012c:
        r2 = r0.currentMessageObject;
        r2 = r2.isEditing();
        if (r2 != 0) goto L_0x0136;
    L_0x0134:
        goto L_0x072f;
    L_0x0136:
        r2 = r0.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.params;
        if (r2 == 0) goto L_0x0148;
    L_0x013e:
        r3 = "query_id";
        r2 = r2.containsKey(r3);
        if (r2 == 0) goto L_0x0148;
    L_0x0146:
        r2 = 1;
        goto L_0x0149;
    L_0x0148:
        r2 = 0;
    L_0x0149:
        r3 = r0.documentAttachType;
        r13 = -1;
        r15 = 0;
        if (r3 == r8) goto L_0x0573;
    L_0x014f:
        if (r3 != r6) goto L_0x0153;
    L_0x0151:
        goto L_0x0573;
    L_0x0153:
        r2 = r0.currentMessageObject;
        r2 = r2.type;
        if (r2 != 0) goto L_0x01f7;
    L_0x0159:
        if (r3 == r11) goto L_0x01f7;
    L_0x015b:
        if (r3 == r14) goto L_0x01f7;
    L_0x015d:
        if (r3 == r9) goto L_0x01f7;
    L_0x015f:
        if (r3 == r10) goto L_0x01f7;
    L_0x0161:
        if (r3 == r7) goto L_0x01f7;
    L_0x0163:
        r2 = r0.currentPhotoObject;
        if (r2 == 0) goto L_0x01f6;
    L_0x0167:
        r2 = r0.drawImageButton;
        if (r2 != 0) goto L_0x016d;
    L_0x016b:
        goto L_0x01f6;
    L_0x016d:
        if (r5 != 0) goto L_0x01cc;
    L_0x016f:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.DownloadController.getInstance(r2);
        r3 = r0.currentMessageObject;
        r2.addLoadingFileObserver(r4, r3, r0);
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.FileLoader.getInstance(r2);
        r2 = r2.isLoadingFile(r4);
        if (r2 != 0) goto L_0x01a5;
    L_0x0186:
        r2 = r0.cancelLoading;
        if (r2 != 0) goto L_0x01a1;
    L_0x018a:
        r2 = r0.documentAttachType;
        if (r2 != 0) goto L_0x0190;
    L_0x018e:
        if (r12 != 0) goto L_0x019e;
    L_0x0190:
        r2 = r0.documentAttachType;
        if (r2 != r14) goto L_0x01a1;
    L_0x0194:
        r2 = r0.documentAttach;
        r2 = org.telegram.messenger.MessageObject.isGifDocument(r2);
        if (r2 == 0) goto L_0x01a1;
    L_0x019c:
        if (r12 == 0) goto L_0x01a1;
    L_0x019e:
        r0.buttonState = r11;
        goto L_0x01b6;
    L_0x01a1:
        r2 = 0;
        r0.buttonState = r2;
        goto L_0x01b6;
    L_0x01a5:
        r0.buttonState = r11;
        r3 = org.telegram.messenger.ImageLoader.getInstance();
        r3 = r3.getFileProgress(r4);
        if (r3 == 0) goto L_0x01b6;
    L_0x01b1:
        r3 = r3.floatValue();
        r15 = r3;
    L_0x01b6:
        r3 = r0.radialProgress;
        r2 = 0;
        r3.setProgress(r15, r2);
        r3 = r0.radialProgress;
        r4 = r16.getIconForCurrentState();
        r6 = r18;
        r3.setIcon(r4, r1, r6);
        r16.invalidate();
        goto L_0x0724;
    L_0x01cc:
        r6 = r18;
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r3.removeLoadingFileObserver(r0);
        r3 = r0.documentAttachType;
        if (r3 != r14) goto L_0x01e6;
    L_0x01db:
        r3 = r0.photoImage;
        r3 = r3.isAllowStartAnimation();
        if (r3 != 0) goto L_0x01e6;
    L_0x01e3:
        r0.buttonState = r14;
        goto L_0x01e8;
    L_0x01e6:
        r0.buttonState = r13;
    L_0x01e8:
        r3 = r0.radialProgress;
        r4 = r16.getIconForCurrentState();
        r3.setIcon(r4, r1, r6);
        r16.invalidate();
        goto L_0x0724;
    L_0x01f6:
        return;
    L_0x01f7:
        r6 = r18;
        r3 = r0.currentMessageObject;
        r3 = r3.isOut();
        if (r3 == 0) goto L_0x02f9;
    L_0x0201:
        r3 = r0.currentMessageObject;
        r3 = r3.isSending();
        if (r3 != 0) goto L_0x0211;
    L_0x0209:
        r3 = r0.currentMessageObject;
        r3 = r3.isEditing();
        if (r3 == 0) goto L_0x02f9;
    L_0x0211:
        r3 = r0.currentMessageObject;
        r3 = r3.messageOwner;
        r3 = r3.attachPath;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x02c5;
    L_0x021d:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r4 = r0.currentMessageObject;
        r5 = r4.messageOwner;
        r5 = r5.attachPath;
        r3.addLoadingFileObserver(r5, r4, r0);
        r0.wasSending = r11;
        r3 = r0.currentMessageObject;
        r3 = r3.messageOwner;
        r3 = r3.attachPath;
        if (r3 == 0) goto L_0x0241;
    L_0x0236:
        r4 = "http";
        r3 = r3.startsWith(r4);
        if (r3 != 0) goto L_0x023f;
    L_0x023e:
        goto L_0x0241;
    L_0x023f:
        r3 = 0;
        goto L_0x0242;
    L_0x0241:
        r3 = 1;
    L_0x0242:
        r4 = r0.currentMessageObject;
        r4 = r4.messageOwner;
        r5 = r4.params;
        r4 = r4.message;
        if (r4 == 0) goto L_0x0262;
    L_0x024c:
        if (r5 == 0) goto L_0x0262;
    L_0x024e:
        r4 = "url";
        r4 = r5.containsKey(r4);
        if (r4 != 0) goto L_0x025e;
    L_0x0256:
        r4 = "bot";
        r4 = r5.containsKey(r4);
        if (r4 == 0) goto L_0x0262;
    L_0x025e:
        r0.buttonState = r13;
        r3 = 0;
        goto L_0x0264;
    L_0x0262:
        r0.buttonState = r11;
    L_0x0264:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.SendMessagesHelper.getInstance(r4);
        r5 = r0.currentMessageObject;
        r5 = r5.getId();
        r4 = r4.isSendingMessage(r5);
        r5 = r0.currentPosition;
        if (r5 == 0) goto L_0x028a;
    L_0x0278:
        if (r4 == 0) goto L_0x028a;
    L_0x027a:
        r5 = r0.buttonState;
        if (r5 != r11) goto L_0x028a;
    L_0x027e:
        r0.drawRadialCheckBackground = r11;
        r16.getIconForCurrentState();
        r5 = r0.radialProgress;
        r7 = 6;
        r5.setIcon(r7, r1, r6);
        goto L_0x0293;
    L_0x028a:
        r5 = r0.radialProgress;
        r7 = r16.getIconForCurrentState();
        r5.setIcon(r7, r1, r6);
    L_0x0293:
        if (r3 == 0) goto L_0x02ba;
    L_0x0295:
        r3 = org.telegram.messenger.ImageLoader.getInstance();
        r5 = r0.currentMessageObject;
        r5 = r5.messageOwner;
        r5 = r5.attachPath;
        r3 = r3.getFileProgress(r5);
        if (r3 != 0) goto L_0x02ad;
    L_0x02a5:
        if (r4 == 0) goto L_0x02ad;
    L_0x02a7:
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = java.lang.Float.valueOf(r4);
    L_0x02ad:
        r4 = r0.radialProgress;
        if (r3 == 0) goto L_0x02b5;
    L_0x02b1:
        r15 = r3.floatValue();
    L_0x02b5:
        r2 = 0;
        r4.setProgress(r15, r2);
        goto L_0x02c0;
    L_0x02ba:
        r2 = 0;
        r3 = r0.radialProgress;
        r3.setProgress(r15, r2);
    L_0x02c0:
        r16.invalidate();
        r4 = 0;
        goto L_0x02f2;
    L_0x02c5:
        r0.buttonState = r13;
        r16.getIconForCurrentState();
        r3 = r0.radialProgress;
        r4 = r0.currentMessageObject;
        r4 = r4.isSticker();
        if (r4 != 0) goto L_0x02e8;
    L_0x02d4:
        r4 = r0.currentMessageObject;
        r4 = r4.isAnimatedSticker();
        if (r4 != 0) goto L_0x02e8;
    L_0x02dc:
        r4 = r0.currentMessageObject;
        r4 = r4.isLocation();
        if (r4 == 0) goto L_0x02e5;
    L_0x02e4:
        goto L_0x02e8;
    L_0x02e5:
        r2 = 12;
        goto L_0x02e9;
    L_0x02e8:
        r2 = 4;
    L_0x02e9:
        r4 = 0;
        r3.setIcon(r2, r1, r4);
        r2 = r0.radialProgress;
        r2.setProgress(r15, r4);
    L_0x02f2:
        r2 = r0.videoRadialProgress;
        r2.setIcon(r10, r1, r4);
        goto L_0x0724;
    L_0x02f9:
        r3 = r0.wasSending;
        if (r3 == 0) goto L_0x0312;
    L_0x02fd:
        r3 = r0.currentMessageObject;
        r3 = r3.messageOwner;
        r3 = r3.attachPath;
        r3 = android.text.TextUtils.isEmpty(r3);
        if (r3 != 0) goto L_0x0312;
    L_0x0309:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r3.removeLoadingFileObserver(r0);
    L_0x0312:
        r3 = r0.documentAttachType;
        if (r3 == r10) goto L_0x031a;
    L_0x0316:
        if (r3 == r14) goto L_0x031a;
    L_0x0318:
        if (r3 != r9) goto L_0x0367;
    L_0x031a:
        r3 = r0.autoPlayingMedia;
        if (r3 == 0) goto L_0x0367;
    L_0x031e:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.FileLoader.getInstance(r3);
        r2 = r0.documentAttach;
        r7 = org.telegram.messenger.MediaController.getInstance();
        r9 = r0.currentMessageObject;
        r7 = r7.isPlayingMessage(r9);
        r2 = r3.isLoadingVideo(r2, r7);
        r3 = r0.photoImage;
        r3 = r3.getAnimation();
        if (r3 == 0) goto L_0x035c;
    L_0x033c:
        r7 = r0.currentMessageObject;
        r9 = r7.hadAnimationNotReadyLoading;
        if (r9 == 0) goto L_0x034e;
    L_0x0342:
        r3 = r3.hasBitmap();
        if (r3 == 0) goto L_0x0368;
    L_0x0348:
        r3 = r0.currentMessageObject;
        r7 = 0;
        r3.hadAnimationNotReadyLoading = r7;
        goto L_0x0368;
    L_0x034e:
        if (r2 == 0) goto L_0x0358;
    L_0x0350:
        r3 = r3.hasBitmap();
        if (r3 != 0) goto L_0x0358;
    L_0x0356:
        r3 = 1;
        goto L_0x0359;
    L_0x0358:
        r3 = 0;
    L_0x0359:
        r7.hadAnimationNotReadyLoading = r3;
        goto L_0x0368;
    L_0x035c:
        r3 = r0.documentAttachType;
        if (r3 != r14) goto L_0x0368;
    L_0x0360:
        if (r5 != 0) goto L_0x0368;
    L_0x0362:
        r3 = r0.currentMessageObject;
        r3.hadAnimationNotReadyLoading = r11;
        goto L_0x0368;
    L_0x0367:
        r2 = 0;
    L_0x0368:
        r3 = r0.hasMiniProgress;
        if (r3 == 0) goto L_0x03d3;
    L_0x036c:
        r2 = r0.radialProgress;
        r3 = "chat_inLoaderPhoto";
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r2.setMiniProgressBackgroundColor(r3);
        r0.buttonState = r8;
        r2 = r0.radialProgress;
        r3 = r16.getIconForCurrentState();
        r2.setIcon(r3, r1, r6);
        r2 = r0.hasMiniProgress;
        if (r2 != r11) goto L_0x0392;
    L_0x0386:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.DownloadController.getInstance(r2);
        r2.removeLoadingFileObserver(r0);
        r0.miniButtonState = r13;
        goto L_0x03c8;
    L_0x0392:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.DownloadController.getInstance(r2);
        r3 = r0.currentMessageObject;
        r2.addLoadingFileObserver(r4, r3, r0);
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.FileLoader.getInstance(r2);
        r2 = r2.isLoadingFile(r4);
        if (r2 != 0) goto L_0x03ad;
    L_0x03a9:
        r2 = 0;
        r0.miniButtonState = r2;
        goto L_0x03c8;
    L_0x03ad:
        r0.miniButtonState = r11;
        r2 = org.telegram.messenger.ImageLoader.getInstance();
        r2 = r2.getFileProgress(r4);
        if (r2 == 0) goto L_0x03c3;
    L_0x03b9:
        r4 = r0.radialProgress;
        r2 = r2.floatValue();
        r4.setProgress(r2, r6);
        goto L_0x03c8;
    L_0x03c3:
        r2 = r0.radialProgress;
        r2.setProgress(r15, r6);
    L_0x03c8:
        r2 = r0.radialProgress;
        r4 = r16.getMiniIconForCurrentState();
        r2.setMiniIcon(r4, r1, r6);
        goto L_0x0724;
    L_0x03d3:
        if (r5 != 0) goto L_0x04f5;
    L_0x03d5:
        r5 = r0.documentAttachType;
        if (r5 == r10) goto L_0x03de;
    L_0x03d9:
        if (r5 == r14) goto L_0x03de;
    L_0x03db:
        r7 = 7;
        if (r5 != r7) goto L_0x03ec;
    L_0x03de:
        r5 = r0.autoPlayingMedia;
        if (r5 == 0) goto L_0x03ec;
    L_0x03e2:
        r5 = r0.currentMessageObject;
        r5 = r5.hadAnimationNotReadyLoading;
        if (r5 != 0) goto L_0x03ec;
    L_0x03e8:
        if (r2 != 0) goto L_0x03ec;
    L_0x03ea:
        goto L_0x04f5;
    L_0x03ec:
        r2 = r0.documentAttachType;
        if (r2 == r10) goto L_0x03f5;
    L_0x03f0:
        if (r2 != r14) goto L_0x03f3;
    L_0x03f2:
        goto L_0x03f5;
    L_0x03f3:
        r2 = 0;
        goto L_0x03f6;
    L_0x03f5:
        r2 = 1;
    L_0x03f6:
        r0.drawVideoSize = r2;
        r2 = r0.documentAttachType;
        if (r2 == r10) goto L_0x0401;
    L_0x03fc:
        if (r2 == r14) goto L_0x0401;
    L_0x03fe:
        r5 = 7;
        if (r2 != r5) goto L_0x041e;
    L_0x0401:
        r2 = r0.canStreamVideo;
        if (r2 == 0) goto L_0x041e;
    L_0x0405:
        r2 = r0.drawVideoImageButton;
        if (r2 != 0) goto L_0x041e;
    L_0x0409:
        if (r6 == 0) goto L_0x041e;
    L_0x040b:
        r2 = r0.animatingDrawVideoImageButton;
        if (r2 == r14) goto L_0x0426;
    L_0x040f:
        r5 = r0.animatingDrawVideoImageButtonProgress;
        r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r5 >= 0) goto L_0x0426;
    L_0x0417:
        if (r2 != 0) goto L_0x041b;
    L_0x0419:
        r0.animatingDrawVideoImageButtonProgress = r15;
    L_0x041b:
        r0.animatingDrawVideoImageButton = r14;
        goto L_0x0426;
    L_0x041e:
        r2 = r0.animatingDrawVideoImageButton;
        if (r2 != 0) goto L_0x0426;
    L_0x0422:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0.animatingDrawVideoImageButtonProgress = r2;
    L_0x0426:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.DownloadController.getInstance(r2);
        r5 = r0.currentMessageObject;
        r2.addLoadingFileObserver(r4, r5, r0);
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.FileLoader.getInstance(r2);
        r2 = r2.isLoadingFile(r4);
        if (r2 != 0) goto L_0x0489;
    L_0x043d:
        r2 = r0.cancelLoading;
        if (r2 != 0) goto L_0x0446;
    L_0x0441:
        if (r12 == 0) goto L_0x0446;
    L_0x0443:
        r0.buttonState = r11;
        goto L_0x0449;
    L_0x0446:
        r2 = 0;
        r0.buttonState = r2;
    L_0x0449:
        r3 = r0.documentAttachType;
        if (r3 == r10) goto L_0x0451;
    L_0x044d:
        if (r3 != r14) goto L_0x046d;
    L_0x044f:
        if (r12 == 0) goto L_0x046d;
    L_0x0451:
        r3 = r0.canStreamVideo;
        if (r3 == 0) goto L_0x046d;
    L_0x0455:
        r0.drawVideoImageButton = r11;
        r16.getIconForCurrentState();
        r3 = r0.radialProgress;
        r4 = r0.autoPlayingMedia;
        if (r4 == 0) goto L_0x0462;
    L_0x0460:
        r4 = 4;
        goto L_0x0463;
    L_0x0462:
        r4 = 0;
    L_0x0463:
        r3.setIcon(r4, r1, r6);
        r3 = r0.videoRadialProgress;
        r3.setIcon(r14, r1, r6);
        goto L_0x04f0;
    L_0x046d:
        r2 = 0;
        r0.drawVideoImageButton = r2;
        r3 = r0.radialProgress;
        r4 = r16.getIconForCurrentState();
        r3.setIcon(r4, r1, r6);
        r3 = r0.videoRadialProgress;
        r3.setIcon(r10, r1, r2);
        r1 = r0.drawVideoSize;
        if (r1 != 0) goto L_0x04f0;
    L_0x0482:
        r1 = r0.animatingDrawVideoImageButton;
        if (r1 != 0) goto L_0x04f0;
    L_0x0486:
        r0.animatingDrawVideoImageButtonProgress = r15;
        goto L_0x04f0;
    L_0x0489:
        r0.buttonState = r11;
        r3 = org.telegram.messenger.ImageLoader.getInstance();
        r3 = r3.getFileProgress(r4);
        r4 = r0.documentAttachType;
        if (r4 == r10) goto L_0x049b;
    L_0x0497:
        if (r4 != r14) goto L_0x04c8;
    L_0x0499:
        if (r12 == 0) goto L_0x04c8;
    L_0x049b:
        r4 = r0.canStreamVideo;
        if (r4 == 0) goto L_0x04c8;
    L_0x049f:
        r0.drawVideoImageButton = r11;
        r16.getIconForCurrentState();
        r4 = r0.radialProgress;
        r5 = r0.autoPlayingMedia;
        if (r5 != 0) goto L_0x04b1;
    L_0x04aa:
        r5 = r0.documentAttachType;
        if (r5 != r14) goto L_0x04af;
    L_0x04ae:
        goto L_0x04b1;
    L_0x04af:
        r5 = 0;
        goto L_0x04b2;
    L_0x04b1:
        r5 = 4;
    L_0x04b2:
        r4.setIcon(r5, r1, r6);
        r4 = r0.videoRadialProgress;
        if (r3 == 0) goto L_0x04bd;
    L_0x04b9:
        r15 = r3.floatValue();
    L_0x04bd:
        r4.setProgress(r15, r6);
        r3 = r0.videoRadialProgress;
        r4 = 14;
        r3.setIcon(r4, r1, r6);
        goto L_0x04f0;
    L_0x04c8:
        r2 = 0;
        r0.drawVideoImageButton = r2;
        r4 = r0.radialProgress;
        if (r3 == 0) goto L_0x04d4;
    L_0x04cf:
        r3 = r3.floatValue();
        goto L_0x04d5;
    L_0x04d4:
        r3 = 0;
    L_0x04d5:
        r4.setProgress(r3, r6);
        r3 = r0.radialProgress;
        r4 = r16.getIconForCurrentState();
        r3.setIcon(r4, r1, r6);
        r3 = r0.videoRadialProgress;
        r3.setIcon(r10, r1, r2);
        r1 = r0.drawVideoSize;
        if (r1 != 0) goto L_0x04f0;
    L_0x04ea:
        r1 = r0.animatingDrawVideoImageButton;
        if (r1 != 0) goto L_0x04f0;
    L_0x04ee:
        r0.animatingDrawVideoImageButtonProgress = r15;
    L_0x04f0:
        r16.invalidate();
        goto L_0x0724;
    L_0x04f5:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r3.removeLoadingFileObserver(r0);
        r3 = r0.drawVideoImageButton;
        if (r3 == 0) goto L_0x0517;
    L_0x0502:
        if (r6 == 0) goto L_0x0517;
    L_0x0504:
        r3 = r0.animatingDrawVideoImageButton;
        if (r3 == r11) goto L_0x051d;
    L_0x0508:
        r4 = r0.animatingDrawVideoImageButtonProgress;
        r4 = (r4 > r15 ? 1 : (r4 == r15 ? 0 : -1));
        if (r4 <= 0) goto L_0x051d;
    L_0x050e:
        if (r3 != 0) goto L_0x0514;
    L_0x0510:
        r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r0.animatingDrawVideoImageButtonProgress = r3;
    L_0x0514:
        r0.animatingDrawVideoImageButton = r11;
        goto L_0x051d;
    L_0x0517:
        r3 = r0.animatingDrawVideoImageButton;
        if (r3 != 0) goto L_0x051d;
    L_0x051b:
        r0.animatingDrawVideoImageButtonProgress = r15;
    L_0x051d:
        r2 = 0;
        r0.drawVideoImageButton = r2;
        r0.drawVideoSize = r2;
        r2 = r0.currentMessageObject;
        r2 = r2.needDrawBluredPreview();
        if (r2 == 0) goto L_0x052d;
    L_0x052a:
        r0.buttonState = r13;
        goto L_0x0549;
    L_0x052d:
        r2 = r0.currentMessageObject;
        r4 = r2.type;
        r5 = 8;
        if (r4 != r5) goto L_0x0540;
    L_0x0535:
        r2 = r2.gifState;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x0540;
    L_0x053d:
        r0.buttonState = r14;
        goto L_0x0549;
    L_0x0540:
        r2 = r0.documentAttachType;
        if (r2 != r10) goto L_0x0547;
    L_0x0544:
        r0.buttonState = r8;
        goto L_0x0549;
    L_0x0547:
        r0.buttonState = r13;
    L_0x0549:
        r2 = r0.videoRadialProgress;
        r4 = r0.animatingDrawVideoImageButton;
        if (r4 == 0) goto L_0x0550;
    L_0x054f:
        goto L_0x0551;
    L_0x0550:
        r11 = 0;
    L_0x0551:
        r2.setIcon(r10, r1, r11);
        r2 = r0.radialProgress;
        r4 = r16.getIconForCurrentState();
        r2.setIcon(r4, r1, r6);
        if (r19 != 0) goto L_0x056e;
    L_0x055f:
        r1 = r0.photoNotSet;
        if (r1 == 0) goto L_0x056e;
    L_0x0563:
        r1 = r0.currentMessageObject;
        r2 = r0.currentMessagesGroup;
        r4 = r0.pinnedBottom;
        r5 = r0.pinnedTop;
        r0.setMessageObject(r1, r2, r4, r5);
    L_0x056e:
        r16.invalidate();
        goto L_0x0724;
    L_0x0573:
        r6 = r18;
        r7 = r0.currentMessageObject;
        r7 = r7.isOut();
        if (r7 == 0) goto L_0x058d;
    L_0x057d:
        r7 = r0.currentMessageObject;
        r7 = r7.isSending();
        if (r7 != 0) goto L_0x0597;
    L_0x0585:
        r7 = r0.currentMessageObject;
        r7 = r7.isEditing();
        if (r7 != 0) goto L_0x0597;
    L_0x058d:
        r7 = r0.currentMessageObject;
        r7 = r7.isSendError();
        if (r7 == 0) goto L_0x0613;
    L_0x0595:
        if (r2 == 0) goto L_0x0613;
    L_0x0597:
        r4 = r0.currentMessageObject;
        r4 = r4.messageOwner;
        r4 = r4.attachPath;
        r4 = android.text.TextUtils.isEmpty(r4);
        if (r4 != 0) goto L_0x05ff;
    L_0x05a3:
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DownloadController.getInstance(r4);
        r5 = r0.currentMessageObject;
        r7 = r5.messageOwner;
        r7 = r7.attachPath;
        r4.addLoadingFileObserver(r7, r5, r0);
        r0.wasSending = r11;
        r0.buttonState = r10;
        r4 = r0.radialProgress;
        r5 = r16.getIconForCurrentState();
        r4.setIcon(r5, r1, r6);
        if (r2 != 0) goto L_0x05f7;
    L_0x05c1:
        r1 = org.telegram.messenger.ImageLoader.getInstance();
        r2 = r0.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.attachPath;
        r1 = r1.getFileProgress(r2);
        if (r1 != 0) goto L_0x05e9;
    L_0x05d1:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.SendMessagesHelper.getInstance(r2);
        r4 = r0.currentMessageObject;
        r4 = r4.getId();
        r2 = r2.isSendingMessage(r4);
        if (r2 == 0) goto L_0x05e9;
    L_0x05e3:
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = java.lang.Float.valueOf(r2);
    L_0x05e9:
        r2 = r0.radialProgress;
        if (r1 == 0) goto L_0x05f1;
    L_0x05ed:
        r15 = r1.floatValue();
    L_0x05f1:
        r3 = 0;
        r2.setProgress(r15, r3);
        goto L_0x0721;
    L_0x05f7:
        r3 = 0;
        r1 = r0.radialProgress;
        r1.setProgress(r15, r3);
        goto L_0x0721;
    L_0x05ff:
        r3 = 0;
        r0.buttonState = r13;
        r16.getIconForCurrentState();
        r2 = r0.radialProgress;
        r4 = 12;
        r2.setIcon(r4, r1, r3);
        r1 = r0.radialProgress;
        r1.setProgress(r15, r3);
        goto L_0x0721;
    L_0x0613:
        r3 = r0.hasMiniProgress;
        if (r3 == 0) goto L_0x06a6;
    L_0x0617:
        r3 = r0.radialProgress;
        r5 = r0.currentMessageObject;
        r5 = r5.isOutOwner();
        if (r5 == 0) goto L_0x0624;
    L_0x0621:
        r5 = "chat_outLoader";
        goto L_0x0626;
    L_0x0624:
        r5 = "chat_inLoader";
    L_0x0626:
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r3.setMiniProgressBackgroundColor(r5);
        r3 = org.telegram.messenger.MediaController.getInstance();
        r5 = r0.currentMessageObject;
        r3 = r3.isPlayingMessage(r5);
        if (r3 == 0) goto L_0x0649;
    L_0x0639:
        if (r3 == 0) goto L_0x0646;
    L_0x063b:
        r3 = org.telegram.messenger.MediaController.getInstance();
        r3 = r3.isMessagePaused();
        if (r3 == 0) goto L_0x0646;
    L_0x0645:
        goto L_0x0649;
    L_0x0646:
        r0.buttonState = r11;
        goto L_0x064c;
    L_0x0649:
        r2 = 0;
        r0.buttonState = r2;
    L_0x064c:
        r3 = r0.radialProgress;
        r5 = r16.getIconForCurrentState();
        r3.setIcon(r5, r1, r6);
        r3 = r0.hasMiniProgress;
        if (r3 != r11) goto L_0x0665;
    L_0x0659:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r3.removeLoadingFileObserver(r0);
        r0.miniButtonState = r13;
        goto L_0x069b;
    L_0x0665:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r5 = r0.currentMessageObject;
        r3.addLoadingFileObserver(r4, r5, r0);
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.FileLoader.getInstance(r3);
        r3 = r3.isLoadingFile(r4);
        if (r3 != 0) goto L_0x0680;
    L_0x067c:
        r2 = 0;
        r0.miniButtonState = r2;
        goto L_0x069b;
    L_0x0680:
        r0.miniButtonState = r11;
        r3 = org.telegram.messenger.ImageLoader.getInstance();
        r3 = r3.getFileProgress(r4);
        if (r3 == 0) goto L_0x0696;
    L_0x068c:
        r4 = r0.radialProgress;
        r3 = r3.floatValue();
        r4.setProgress(r3, r6);
        goto L_0x069b;
    L_0x0696:
        r3 = r0.radialProgress;
        r3.setProgress(r15, r6);
    L_0x069b:
        r3 = r0.radialProgress;
        r4 = r16.getMiniIconForCurrentState();
        r3.setMiniIcon(r4, r1, r6);
        goto L_0x0721;
    L_0x06a6:
        if (r5 == 0) goto L_0x06da;
    L_0x06a8:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r3.removeLoadingFileObserver(r0);
        r3 = org.telegram.messenger.MediaController.getInstance();
        r4 = r0.currentMessageObject;
        r3 = r3.isPlayingMessage(r4);
        if (r3 == 0) goto L_0x06cd;
    L_0x06bd:
        if (r3 == 0) goto L_0x06ca;
    L_0x06bf:
        r3 = org.telegram.messenger.MediaController.getInstance();
        r3 = r3.isMessagePaused();
        if (r3 == 0) goto L_0x06ca;
    L_0x06c9:
        goto L_0x06cd;
    L_0x06ca:
        r0.buttonState = r11;
        goto L_0x06d0;
    L_0x06cd:
        r2 = 0;
        r0.buttonState = r2;
    L_0x06d0:
        r3 = r0.radialProgress;
        r4 = r16.getIconForCurrentState();
        r3.setIcon(r4, r1, r6);
        goto L_0x0721;
    L_0x06da:
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.DownloadController.getInstance(r3);
        r5 = r0.currentMessageObject;
        r3.addLoadingFileObserver(r4, r5, r0);
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.FileLoader.getInstance(r3);
        r3 = r3.isLoadingFile(r4);
        if (r3 != 0) goto L_0x06fd;
    L_0x06f1:
        r0.buttonState = r14;
        r3 = r0.radialProgress;
        r4 = r16.getIconForCurrentState();
        r3.setIcon(r4, r1, r6);
        goto L_0x0721;
    L_0x06fd:
        r0.buttonState = r10;
        r3 = org.telegram.messenger.ImageLoader.getInstance();
        r3 = r3.getFileProgress(r4);
        if (r3 == 0) goto L_0x0713;
    L_0x0709:
        r4 = r0.radialProgress;
        r3 = r3.floatValue();
        r4.setProgress(r3, r6);
        goto L_0x0718;
    L_0x0713:
        r3 = r0.radialProgress;
        r3.setProgress(r15, r6);
    L_0x0718:
        r3 = r0.radialProgress;
        r4 = r16.getIconForCurrentState();
        r3.setIcon(r4, r1, r6);
    L_0x0721:
        r16.updatePlayingMessageProgress();
    L_0x0724:
        r1 = r0.hasMiniProgress;
        if (r1 != 0) goto L_0x072e;
    L_0x0728:
        r1 = r0.radialProgress;
        r2 = 0;
        r1.setMiniIcon(r10, r2, r6);
    L_0x072e:
        return;
    L_0x072f:
        r2 = 0;
        r3 = r0.radialProgress;
        r3.setIcon(r10, r1, r2);
        r3 = r0.radialProgress;
        r3.setMiniIcon(r10, r1, r2);
        r3 = r0.videoRadialProgress;
        r3.setIcon(r10, r1, r2);
        r3 = r0.videoRadialProgress;
        r3.setMiniIcon(r10, r1, r2);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateButtonState(boolean, boolean, boolean):void");
    }

    private void didPressMiniButton(boolean z) {
        int i = this.miniButtonState;
        if (i == 0) {
            this.miniButtonState = 1;
            this.radialProgress.setProgress(0.0f, false);
            i = this.documentAttachType;
            if (i == 3 || i == 5) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
            } else if (i == 4) {
                FileLoader instance = FileLoader.getInstance(this.currentAccount);
                Document document = this.documentAttach;
                MessageObject messageObject = this.currentMessageObject;
                instance.loadFile(document, messageObject, 1, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
            }
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        } else if (i == 1) {
            i = this.documentAttachType;
            if ((i == 3 || i == 5) && MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                MediaController.getInstance().cleanupPlayer(true, true);
            }
            this.miniButtonState = 0;
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
            this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, true);
            invalidate();
        }
    }

    private void didPressButton(boolean z, boolean z2) {
        boolean z3 = z;
        int i = 2;
        int i2;
        if (this.buttonState == 0 && (!this.drawVideoImageButton || z2)) {
            i2 = this.documentAttachType;
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
            PhotoSize photoSize;
            String str;
            this.cancelLoading = false;
            if (z2) {
                this.videoRadialProgress.setProgress(0.0f, false);
            } else {
                this.radialProgress.setProgress(0.0f, false);
            }
            if (this.currentPhotoObject == null || !(this.photoImage.hasNotThumb() || this.currentPhotoObjectThumb == null)) {
                photoSize = this.currentPhotoObjectThumb;
                str = this.currentPhotoFilterThumb;
            } else {
                photoSize = this.currentPhotoObject;
                if (!(photoSize instanceof TL_photoStrippedSize)) {
                    if (!"s".equals(photoSize.type)) {
                        str = this.currentPhotoFilter;
                    }
                }
                str = this.currentPhotoFilterThumb;
            }
            String str2 = str;
            MessageObject messageObject = this.currentMessageObject;
            int i3 = messageObject.type;
            int i4;
            MessageObject messageObject2;
            Document document;
            if (i3 == 1) {
                this.photoImage.setForceLoading(true);
                ImageReceiver imageReceiver = this.photoImage;
                ImageLocation forObject = ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject);
                String str3 = this.currentPhotoFilter;
                ImageLocation forObject2 = ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject);
                String str4 = this.currentPhotoFilterThumb;
                i4 = this.currentPhotoObject.size;
                messageObject2 = this.currentMessageObject;
                imageReceiver.setImage(forObject, str3, forObject2, str4, i4, null, messageObject2, messageObject2.shouldEncryptPhotoOrVideo() ? 2 : 0);
            } else if (i3 == 8) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 1, 0);
            } else if (!messageObject.isRoundVideo()) {
                i2 = this.currentMessageObject.type;
                if (i2 == 9) {
                    FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 0, 0);
                } else {
                    i4 = this.documentAttachType;
                    if (i4 == 4) {
                        FileLoader instance = FileLoader.getInstance(this.currentAccount);
                        document = this.documentAttach;
                        messageObject2 = this.currentMessageObject;
                        if (!messageObject2.shouldEncryptPhotoOrVideo()) {
                            i = 0;
                        }
                        instance.loadFile(document, messageObject2, 1, i);
                    } else if (i2 != 0 || i4 == 0) {
                        this.photoImage.setForceLoading(true);
                        this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, this.photoParentObject), this.currentPhotoFilter, ImageLocation.getForObject(this.currentPhotoObjectThumb, this.photoParentObject), this.currentPhotoFilterThumb, 0, null, this.currentMessageObject, 0);
                    } else if (i4 == 2) {
                        this.photoImage.setForceLoading(true);
                        this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), null, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), this.currentPhotoFilterThumb, this.documentAttach.size, null, this.currentMessageObject, 0);
                        this.currentMessageObject.gifState = 2.0f;
                    } else if (i4 == 1) {
                        FileLoader.getInstance(this.currentAccount).loadFile(this.documentAttach, this.currentMessageObject, 0, 0);
                    } else if (i4 == 8) {
                        this.photoImage.setImage(ImageLocation.getForDocument(this.documentAttach), this.currentPhotoFilter, ImageLocation.getForDocument(this.currentPhotoObject, this.documentAttach), "b1", 0, "jpg", this.currentMessageObject, 1);
                    }
                }
            } else if (this.currentMessageObject.isSecretMedia()) {
                FileLoader.getInstance(this.currentAccount).loadFile(this.currentMessageObject.getDocument(), this.currentMessageObject, 1, 1);
            } else {
                MessageObject messageObject3 = this.currentMessageObject;
                messageObject3.gifState = 2.0f;
                document = messageObject3.getDocument();
                this.photoImage.setForceLoading(true);
                this.photoImage.setImage(ImageLocation.getForDocument(document), null, ImageLocation.getForObject(photoSize, document), str2, document.size, null, this.currentMessageObject, 0);
            }
            this.buttonState = 1;
            if (z2) {
                this.videoRadialProgress.setIcon(14, false, z3);
            } else {
                this.radialProgress.setIcon(getIconForCurrentState(), false, z3);
            }
            invalidate();
        } else if (this.buttonState != 1 || (this.drawVideoImageButton && !z2)) {
            i2 = this.buttonState;
            if (i2 == 2) {
                i2 = this.documentAttachType;
                if (i2 == 3 || i2 == 5) {
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
            } else if (i2 == 3 || (i2 == 0 && this.drawVideoImageButton)) {
                if (this.hasMiniProgress == 2 && this.miniButtonState != 1) {
                    this.miniButtonState = 1;
                    this.radialProgress.setProgress(0.0f, false);
                    this.radialProgress.setMiniIcon(getMiniIconForCurrentState(), false, z3);
                }
                this.delegate.didPressImage(this, 0.0f, 0.0f);
            } else if (this.buttonState == 4) {
                i2 = this.documentAttachType;
                if (i2 != 3 && i2 != 5) {
                    return;
                }
                if ((this.currentMessageObject.isOut() && (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing())) || this.currentMessageObject.isSendError()) {
                    ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                    if (chatMessageCellDelegate != null) {
                        chatMessageCellDelegate.didPressCancelSendButton(this);
                        return;
                    }
                    return;
                }
                FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                this.buttonState = 2;
                this.radialProgress.setIcon(getIconForCurrentState(), false, z3);
                invalidate();
            }
        } else {
            this.photoImage.setForceLoading(false);
            i2 = this.documentAttachType;
            if (i2 == 3 || i2 == 5) {
                if (MediaController.getInstance().lambda$startAudioAgain$5$MediaController(this.currentMessageObject)) {
                    this.buttonState = 0;
                    this.radialProgress.setIcon(getIconForCurrentState(), false, z3);
                    invalidate();
                }
            } else if (!this.currentMessageObject.isOut() || (!this.currentMessageObject.isSending() && !this.currentMessageObject.isEditing())) {
                this.cancelLoading = true;
                i2 = this.documentAttachType;
                if (i2 == 2 || i2 == 4 || i2 == 1 || i2 == 8) {
                    FileLoader.getInstance(this.currentAccount).cancelLoadFile(this.documentAttach);
                } else {
                    i2 = this.currentMessageObject.type;
                    if (i2 == 0 || i2 == 1 || i2 == 8 || i2 == 5) {
                        ImageLoader.getInstance().cancelForceLoadingForImageReceiver(this.photoImage);
                        this.photoImage.cancelLoadImage();
                    } else if (i2 == 9) {
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
        boolean z2 = i == 3 || i == 5;
        updateButtonState(true, z2, false);
    }

    /* JADX WARNING: Missing block: B:33:0x009b, code skipped:
            if ((r1 & 2) != 0) goto L_0x009d;
     */
    public void onSuccessDownload(java.lang.String r22) {
        /*
        r21 = this;
        r0 = r21;
        r1 = r0.documentAttachType;
        r2 = 0;
        r3 = 1;
        r4 = 3;
        if (r1 == r4) goto L_0x01aa;
    L_0x0009:
        r4 = 5;
        if (r1 != r4) goto L_0x000e;
    L_0x000c:
        goto L_0x01aa;
    L_0x000e:
        r1 = r0.drawVideoImageButton;
        r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r1 == 0) goto L_0x001a;
    L_0x0014:
        r1 = r0.videoRadialProgress;
        r1.setProgress(r4, r3);
        goto L_0x001f;
    L_0x001a:
        r1 = r0.radialProgress;
        r1.setProgress(r4, r3);
    L_0x001f:
        r1 = r0.currentMessageObject;
        r1 = r1.needDrawBluredPreview();
        r5 = 2;
        if (r1 != 0) goto L_0x0165;
    L_0x0028:
        r1 = r0.autoPlayingMedia;
        if (r1 != 0) goto L_0x0165;
    L_0x002c:
        r1 = r0.documentAttach;
        if (r1 == 0) goto L_0x0165;
    L_0x0030:
        r6 = r0.documentAttachType;
        r7 = 7;
        r8 = "s";
        if (r6 != r7) goto L_0x0089;
    L_0x0037:
        r9 = r0.photoImage;
        r10 = org.telegram.messenger.ImageLocation.getForDocument(r1);
        r1 = r0.currentPhotoObject;
        r6 = r0.photoParentObject;
        r12 = org.telegram.messenger.ImageLocation.getForObject(r1, r6);
        r1 = r0.currentPhotoObject;
        r6 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r6 != 0) goto L_0x0059;
    L_0x004b:
        if (r1 == 0) goto L_0x0056;
    L_0x004d:
        r1 = r1.type;
        r1 = r8.equals(r1);
        if (r1 == 0) goto L_0x0056;
    L_0x0055:
        goto L_0x0059;
    L_0x0056:
        r1 = r0.currentPhotoFilter;
        goto L_0x005b;
    L_0x0059:
        r1 = r0.currentPhotoFilterThumb;
    L_0x005b:
        r13 = r1;
        r1 = r0.currentPhotoObjectThumb;
        r6 = r0.photoParentObject;
        r14 = org.telegram.messenger.ImageLocation.getForObject(r1, r6);
        r15 = r0.currentPhotoFilterThumb;
        r16 = 0;
        r1 = r0.documentAttach;
        r1 = r1.size;
        r18 = 0;
        r6 = r0.currentMessageObject;
        r20 = 0;
        r11 = "g";
        r17 = r1;
        r19 = r6;
        r9.setImage(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        r1 = r0.photoImage;
        r1.setAllowStartAnimation(r3);
        r1 = r0.photoImage;
        r1.startAnimation();
        r0.autoPlayingMedia = r3;
        goto L_0x0165;
    L_0x0089:
        r1 = org.telegram.messenger.SharedConfig.autoplayVideo;
        if (r1 == 0) goto L_0x0100;
    L_0x008d:
        r1 = 4;
        if (r6 != r1) goto L_0x0100;
    L_0x0090:
        r1 = r0.currentPosition;
        if (r1 == 0) goto L_0x009d;
    L_0x0094:
        r1 = r1.flags;
        r6 = r1 & 1;
        if (r6 == 0) goto L_0x0100;
    L_0x009a:
        r1 = r1 & r5;
        if (r1 == 0) goto L_0x0100;
    L_0x009d:
        r0.animatingNoSound = r5;
        r9 = r0.photoImage;
        r1 = r0.documentAttach;
        r10 = org.telegram.messenger.ImageLocation.getForDocument(r1);
        r1 = r0.currentPhotoObject;
        r6 = r0.photoParentObject;
        r12 = org.telegram.messenger.ImageLocation.getForObject(r1, r6);
        r1 = r0.currentPhotoObject;
        r6 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r6 != 0) goto L_0x00c3;
    L_0x00b5:
        if (r1 == 0) goto L_0x00c0;
    L_0x00b7:
        r1 = r1.type;
        r1 = r8.equals(r1);
        if (r1 == 0) goto L_0x00c0;
    L_0x00bf:
        goto L_0x00c3;
    L_0x00c0:
        r1 = r0.currentPhotoFilter;
        goto L_0x00c5;
    L_0x00c3:
        r1 = r0.currentPhotoFilterThumb;
    L_0x00c5:
        r13 = r1;
        r1 = r0.currentPhotoObjectThumb;
        r6 = r0.photoParentObject;
        r14 = org.telegram.messenger.ImageLocation.getForObject(r1, r6);
        r15 = r0.currentPhotoFilterThumb;
        r16 = 0;
        r1 = r0.documentAttach;
        r1 = r1.size;
        r18 = 0;
        r6 = r0.currentMessageObject;
        r20 = 0;
        r11 = "g";
        r17 = r1;
        r19 = r6;
        r9.setImage(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        r1 = r0.currentMessageObject;
        r1 = org.telegram.ui.PhotoViewer.isPlayingMessage(r1);
        if (r1 != 0) goto L_0x00f8;
    L_0x00ed:
        r1 = r0.photoImage;
        r1.setAllowStartAnimation(r3);
        r1 = r0.photoImage;
        r1.startAnimation();
        goto L_0x00fd;
    L_0x00f8:
        r1 = r0.photoImage;
        r1.setAllowStartAnimation(r2);
    L_0x00fd:
        r0.autoPlayingMedia = r3;
        goto L_0x0165;
    L_0x0100:
        r1 = r0.documentAttachType;
        if (r1 != r5) goto L_0x0165;
    L_0x0104:
        r9 = r0.photoImage;
        r1 = r0.documentAttach;
        r10 = org.telegram.messenger.ImageLocation.getForDocument(r1);
        r1 = r0.currentPhotoObject;
        r6 = r0.photoParentObject;
        r12 = org.telegram.messenger.ImageLocation.getForObject(r1, r6);
        r1 = r0.currentPhotoObject;
        r6 = r1 instanceof org.telegram.tgnet.TLRPC.TL_photoStrippedSize;
        if (r6 != 0) goto L_0x0128;
    L_0x011a:
        if (r1 == 0) goto L_0x0125;
    L_0x011c:
        r1 = r1.type;
        r1 = r8.equals(r1);
        if (r1 == 0) goto L_0x0125;
    L_0x0124:
        goto L_0x0128;
    L_0x0125:
        r1 = r0.currentPhotoFilter;
        goto L_0x012a;
    L_0x0128:
        r1 = r0.currentPhotoFilterThumb;
    L_0x012a:
        r13 = r1;
        r1 = r0.currentPhotoObjectThumb;
        r6 = r0.photoParentObject;
        r14 = org.telegram.messenger.ImageLocation.getForObject(r1, r6);
        r15 = r0.currentPhotoFilterThumb;
        r16 = 0;
        r1 = r0.documentAttach;
        r1 = r1.size;
        r18 = 0;
        r6 = r0.currentMessageObject;
        r20 = 0;
        r11 = "g";
        r17 = r1;
        r19 = r6;
        r9.setImage(r10, r11, r12, r13, r14, r15, r16, r17, r18, r19, r20);
        r1 = org.telegram.messenger.SharedConfig.autoplayGifs;
        if (r1 == 0) goto L_0x0159;
    L_0x014e:
        r1 = r0.photoImage;
        r1.setAllowStartAnimation(r3);
        r1 = r0.photoImage;
        r1.startAnimation();
        goto L_0x0163;
    L_0x0159:
        r1 = r0.photoImage;
        r1.setAllowStartAnimation(r2);
        r1 = r0.photoImage;
        r1.stopAnimation();
    L_0x0163:
        r0.autoPlayingMedia = r3;
    L_0x0165:
        r1 = r0.currentMessageObject;
        r6 = r1.type;
        if (r6 != 0) goto L_0x0193;
    L_0x016b:
        r6 = r0.autoPlayingMedia;
        if (r6 != 0) goto L_0x017f;
    L_0x016f:
        r6 = r0.documentAttachType;
        if (r6 != r5) goto L_0x017f;
    L_0x0173:
        r1 = r1.gifState;
        r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1));
        if (r1 == 0) goto L_0x017f;
    L_0x0179:
        r0.buttonState = r5;
        r0.didPressButton(r3, r2);
        goto L_0x01b0;
    L_0x017f:
        r1 = r0.photoNotSet;
        if (r1 != 0) goto L_0x0187;
    L_0x0183:
        r0.updateButtonState(r2, r3, r2);
        goto L_0x01b0;
    L_0x0187:
        r1 = r0.currentMessageObject;
        r2 = r0.currentMessagesGroup;
        r3 = r0.pinnedBottom;
        r4 = r0.pinnedTop;
        r0.setMessageObject(r1, r2, r3, r4);
        goto L_0x01b0;
    L_0x0193:
        r1 = r0.photoNotSet;
        if (r1 != 0) goto L_0x019a;
    L_0x0197:
        r0.updateButtonState(r2, r3, r2);
    L_0x019a:
        r1 = r0.photoNotSet;
        if (r1 == 0) goto L_0x01b0;
    L_0x019e:
        r1 = r0.currentMessageObject;
        r2 = r0.currentMessagesGroup;
        r3 = r0.pinnedBottom;
        r4 = r0.pinnedTop;
        r0.setMessageObject(r1, r2, r3, r4);
        goto L_0x01b0;
    L_0x01aa:
        r0.updateButtonState(r2, r3, r2);
        r21.updateWaveform();
    L_0x01b0:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onSuccessDownload(java.lang.String):void");
    }

    /* JADX WARNING: Missing block: B:14:0x001e, code skipped:
            if (r1 != 6) goto L_0x0020;
     */
    public void didSetImage(org.telegram.messenger.ImageReceiver r1, boolean r2, boolean r3) {
        /*
        r0 = this;
        r1 = r0.currentMessageObject;
        if (r1 == 0) goto L_0x002e;
    L_0x0004:
        if (r2 == 0) goto L_0x002e;
    L_0x0006:
        if (r3 != 0) goto L_0x002e;
    L_0x0008:
        r2 = r1.mediaExists;
        if (r2 != 0) goto L_0x002e;
    L_0x000c:
        r2 = r1.attachPathExists;
        if (r2 != 0) goto L_0x002e;
    L_0x0010:
        r1 = r1.type;
        r2 = 1;
        if (r1 != 0) goto L_0x0020;
    L_0x0015:
        r1 = r0.documentAttachType;
        r3 = 8;
        if (r1 == r3) goto L_0x0026;
    L_0x001b:
        if (r1 == 0) goto L_0x0026;
    L_0x001d:
        r3 = 6;
        if (r1 == r3) goto L_0x0026;
    L_0x0020:
        r1 = r0.currentMessageObject;
        r1 = r1.type;
        if (r1 != r2) goto L_0x002e;
    L_0x0026:
        r1 = r0.currentMessageObject;
        r1.mediaExists = r2;
        r1 = 0;
        r0.updateButtonState(r1, r2, r1);
    L_0x002e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.didSetImage(org.telegram.messenger.ImageReceiver, boolean, boolean):void");
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
        if (this.allowAssistant && VERSION.SDK_INT >= 23) {
            CharSequence charSequence = this.currentMessageObject.messageText;
            if (charSequence == null || charSequence.length() <= 0) {
                charSequence = this.currentMessageObject.caption;
                if (charSequence != null && charSequence.length() > 0) {
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

    public void setAllowAssistant(boolean z) {
        this.allowAssistant = z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:53:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x012e  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x011a  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x014a  */
    /* JADX WARNING: Removed duplicated region for block: B:82:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0187  */
    /* JADX WARNING: Missing block: B:48:0x00d0, code skipped:
            if (r12.isEditing() == false) goto L_0x00d3;
     */
    private void measureTime(org.telegram.messenger.MessageObject r12) {
        /*
        r11 = this;
        r0 = r12.messageOwner;
        r1 = r0.post_author;
        r2 = "\n";
        r3 = "";
        r4 = 0;
        if (r1 == 0) goto L_0x0010;
    L_0x000b:
        r0 = r1.replace(r2, r3);
        goto L_0x0053;
    L_0x0010:
        r0 = r0.fwd_from;
        if (r0 == 0) goto L_0x001d;
    L_0x0014:
        r0 = r0.post_author;
        if (r0 == 0) goto L_0x001d;
    L_0x0018:
        r0 = r0.replace(r2, r3);
        goto L_0x0053;
    L_0x001d:
        r0 = r12.isOutOwner();
        if (r0 != 0) goto L_0x0052;
    L_0x0023:
        r0 = r12.messageOwner;
        r1 = r0.from_id;
        if (r1 <= 0) goto L_0x0052;
    L_0x0029:
        r0 = r0.post;
        if (r0 == 0) goto L_0x0052;
    L_0x002d:
        r0 = r11.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r1 = r12.messageOwner;
        r1 = r1.from_id;
        r1 = java.lang.Integer.valueOf(r1);
        r0 = r0.getUser(r1);
        if (r0 == 0) goto L_0x0052;
    L_0x0041:
        r1 = r0.first_name;
        r0 = r0.last_name;
        r0 = org.telegram.messenger.ContactsController.formatName(r1, r0);
        r1 = 10;
        r2 = 32;
        r0 = r0.replace(r1, r2);
        goto L_0x0053;
    L_0x0052:
        r0 = r4;
    L_0x0053:
        r1 = r11.currentMessageObject;
        r1 = r1.isFromUser();
        if (r1 == 0) goto L_0x006e;
    L_0x005b:
        r1 = r11.currentAccount;
        r1 = org.telegram.messenger.MessagesController.getInstance(r1);
        r2 = r12.messageOwner;
        r2 = r2.from_id;
        r2 = java.lang.Integer.valueOf(r2);
        r1 = r1.getUser(r2);
        goto L_0x006f;
    L_0x006e:
        r1 = r4;
    L_0x006f:
        r2 = r12.isLiveLocation();
        r5 = 1;
        r6 = 0;
        if (r2 != 0) goto L_0x00d3;
    L_0x0077:
        r7 = r12.getDialogId();
        r9 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r2 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
        if (r2 == 0) goto L_0x00d3;
    L_0x0082:
        r2 = r12.messageOwner;
        r7 = r2.via_bot_id;
        if (r7 != 0) goto L_0x00d3;
    L_0x0088:
        r2 = r2.via_bot_name;
        if (r2 != 0) goto L_0x00d3;
    L_0x008c:
        if (r1 == 0) goto L_0x0093;
    L_0x008e:
        r1 = r1.bot;
        if (r1 == 0) goto L_0x0093;
    L_0x0092:
        goto L_0x00d3;
    L_0x0093:
        r1 = r11.currentPosition;
        r2 = 32768; // 0x8000 float:4.5918E-41 double:1.61895E-319;
        if (r1 == 0) goto L_0x00c5;
    L_0x009a:
        r1 = r11.currentMessagesGroup;
        if (r1 != 0) goto L_0x009f;
    L_0x009e:
        goto L_0x00c5;
    L_0x009f:
        r1 = r1.messages;
        r1 = r1.size();
        r7 = 0;
    L_0x00a6:
        if (r7 >= r1) goto L_0x00d3;
    L_0x00a8:
        r8 = r11.currentMessagesGroup;
        r8 = r8.messages;
        r8 = r8.get(r7);
        r8 = (org.telegram.messenger.MessageObject) r8;
        r9 = r8.messageOwner;
        r9 = r9.flags;
        r9 = r9 & r2;
        if (r9 != 0) goto L_0x00c3;
    L_0x00b9:
        r8 = r8.isEditing();
        if (r8 == 0) goto L_0x00c0;
    L_0x00bf:
        goto L_0x00c3;
    L_0x00c0:
        r7 = r7 + 1;
        goto L_0x00a6;
    L_0x00c3:
        r1 = 1;
        goto L_0x00d4;
    L_0x00c5:
        r1 = r12.messageOwner;
        r1 = r1.flags;
        r1 = r1 & r2;
        if (r1 != 0) goto L_0x00c3;
    L_0x00cc:
        r1 = r12.isEditing();
        if (r1 == 0) goto L_0x00d3;
    L_0x00d2:
        goto L_0x00c3;
    L_0x00d3:
        r1 = 0;
    L_0x00d4:
        r7 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r1 == 0) goto L_0x0107;
    L_0x00d8:
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = NUM; // 0x7f0d03c8 float:1.8744078E38 double:1.053130256E-314;
        r9 = "EditedMessage";
        r2 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r1.append(r2);
        r2 = " ";
        r1.append(r2);
        r2 = org.telegram.messenger.LocaleController.getInstance();
        r2 = r2.formatterDay;
        r9 = r12.messageOwner;
        r9 = r9.date;
        r9 = (long) r9;
        r9 = r9 * r7;
        r2 = r2.format(r9);
        r1.append(r2);
        r1 = r1.toString();
        goto L_0x0118;
    L_0x0107:
        r1 = org.telegram.messenger.LocaleController.getInstance();
        r1 = r1.formatterDay;
        r2 = r12.messageOwner;
        r2 = r2.date;
        r9 = (long) r2;
        r9 = r9 * r7;
        r1 = r1.format(r9);
    L_0x0118:
        if (r0 == 0) goto L_0x012e;
    L_0x011a:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r7 = ", ";
        r2.append(r7);
        r2.append(r1);
        r1 = r2.toString();
        r11.currentTimeString = r1;
        goto L_0x0130;
    L_0x012e:
        r11.currentTimeString = r1;
    L_0x0130:
        r1 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r2 = r11.currentTimeString;
        r1 = r1.measureText(r2);
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r1 = (int) r1;
        r11.timeWidth = r1;
        r11.timeTextWidth = r1;
        r1 = r12.messageOwner;
        r2 = r1.flags;
        r2 = r2 & 1024;
        if (r2 == 0) goto L_0x0185;
    L_0x014a:
        r2 = new java.lang.Object[r5];
        r1 = r1.views;
        r1 = java.lang.Math.max(r5, r1);
        r1 = org.telegram.messenger.LocaleController.formatShortNumber(r1, r4);
        r2[r6] = r1;
        r1 = "%s";
        r1 = java.lang.String.format(r1, r2);
        r11.currentViewsString = r1;
        r1 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r2 = r11.currentViewsString;
        r1 = r1.measureText(r2);
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r1 = (int) r1;
        r11.viewsTextWidth = r1;
        r1 = r11.timeWidth;
        r2 = r11.viewsTextWidth;
        r4 = org.telegram.ui.ActionBar.Theme.chat_msgInViewsDrawable;
        r4 = r4.getIntrinsicWidth();
        r2 = r2 + r4;
        r4 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r1 = r1 + r2;
        r11.timeWidth = r1;
    L_0x0185:
        if (r0 == 0) goto L_0x01f0;
    L_0x0187:
        r1 = r11.availableTimeWidth;
        if (r1 != 0) goto L_0x0193;
    L_0x018b:
        r1 = NUM; // 0x447a0000 float:1000.0 double:5.676053805E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r11.availableTimeWidth = r1;
    L_0x0193:
        r1 = r11.availableTimeWidth;
        r2 = r11.timeWidth;
        r1 = r1 - r2;
        r2 = r12.isOutOwner();
        if (r2 == 0) goto L_0x01b1;
    L_0x019e:
        r12 = r12.type;
        r2 = 5;
        if (r12 != r2) goto L_0x01aa;
    L_0x01a3:
        r12 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
        goto L_0x01b0;
    L_0x01aa:
        r12 = NUM; // 0x42CLASSNAME float:96.0 double:5.532938244E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r12);
    L_0x01b0:
        r1 = r1 - r12;
    L_0x01b1:
        r12 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r2 = r0.length();
        r12 = r12.measureText(r0, r6, r2);
        r4 = (double) r12;
        r4 = java.lang.Math.ceil(r4);
        r12 = (int) r4;
        if (r12 <= r1) goto L_0x01d2;
    L_0x01c3:
        if (r1 > 0) goto L_0x01c7;
    L_0x01c5:
        r12 = 0;
        goto L_0x01d3;
    L_0x01c7:
        r12 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r2 = (float) r1;
        r3 = android.text.TextUtils.TruncateAt.END;
        r3 = android.text.TextUtils.ellipsize(r0, r12, r2, r3);
        r12 = r1;
        goto L_0x01d3;
    L_0x01d2:
        r3 = r0;
    L_0x01d3:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r3);
        r1 = r11.currentTimeString;
        r0.append(r1);
        r0 = r0.toString();
        r11.currentTimeString = r0;
        r0 = r11.timeTextWidth;
        r0 = r0 + r12;
        r11.timeTextWidth = r0;
        r0 = r11.timeWidth;
        r0 = r0 + r12;
        r11.timeWidth = r0;
    L_0x01f0:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.measureTime(org.telegram.messenger.MessageObject):void");
    }

    private boolean isDrawSelectionBackground() {
        return (isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted);
    }

    private boolean isOpenChatByShare(MessageObject messageObject) {
        MessageFwdHeader messageFwdHeader = messageObject.messageOwner.fwd_from;
        return (messageFwdHeader == null || messageFwdHeader.saved_from_peer == null) ? false : true;
    }

    private boolean checkNeedDrawShareButton(MessageObject messageObject) {
        GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null && !groupedMessagePosition.last) {
            return false;
        }
        if (!(messageObject.messageOwner.fwd_from == null || messageObject.isOutOwner() || messageObject.messageOwner.fwd_from.saved_from_peer == null || messageObject.getDialogId() != ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()))) {
            this.drwaShareGoIcon = true;
        }
        return messageObject.needDrawShareButton();
    }

    public boolean isInsideBackground(float f, float f2) {
        if (this.currentBackgroundDrawable != null) {
            int i = this.backgroundDrawableLeft;
            if (f >= ((float) i) && f <= ((float) (i + this.backgroundDrawableRight))) {
                return true;
            }
        }
        return false;
    }

    private void updateCurrentUserAndChat() {
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        MessageFwdHeader messageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (messageFwdHeader == null || messageFwdHeader.channel_id == 0 || this.currentMessageObject.getDialogId() != ((long) clientUserId)) {
            if (messageFwdHeader != null) {
                Peer peer = messageFwdHeader.saved_from_peer;
                if (peer != null) {
                    clientUserId = peer.user_id;
                    int i;
                    if (clientUserId != 0) {
                        i = messageFwdHeader.from_id;
                        if (i != 0) {
                            this.currentUser = instance.getUser(Integer.valueOf(i));
                            return;
                        } else {
                            this.currentUser = instance.getUser(Integer.valueOf(clientUserId));
                            return;
                        }
                    } else if (peer.channel_id != 0) {
                        if (this.currentMessageObject.isSavedFromMegagroup()) {
                            clientUserId = messageFwdHeader.from_id;
                            if (clientUserId != 0) {
                                this.currentUser = instance.getUser(Integer.valueOf(clientUserId));
                                return;
                            }
                        }
                        this.currentChat = instance.getChat(Integer.valueOf(messageFwdHeader.saved_from_peer.channel_id));
                        return;
                    } else {
                        clientUserId = peer.chat_id;
                        if (clientUserId != 0) {
                            i = messageFwdHeader.from_id;
                            if (i != 0) {
                                this.currentUser = instance.getUser(Integer.valueOf(i));
                                return;
                            } else {
                                this.currentChat = instance.getChat(Integer.valueOf(clientUserId));
                                return;
                            }
                        }
                        return;
                    }
                }
            }
            if (messageFwdHeader != null && messageFwdHeader.from_id != 0 && messageFwdHeader.channel_id == 0 && this.currentMessageObject.getDialogId() == ((long) clientUserId)) {
                this.currentUser = instance.getUser(Integer.valueOf(messageFwdHeader.from_id));
                return;
            } else if (messageFwdHeader != null && !TextUtils.isEmpty(messageFwdHeader.from_name) && this.currentMessageObject.getDialogId() == ((long) clientUserId)) {
                this.currentUser = new TL_user();
                this.currentUser.first_name = messageFwdHeader.from_name;
                return;
            } else if (this.currentMessageObject.isFromUser()) {
                this.currentUser = instance.getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
                return;
            } else {
                Message message = this.currentMessageObject.messageOwner;
                clientUserId = message.from_id;
                if (clientUserId < 0) {
                    this.currentChat = instance.getChat(Integer.valueOf(-clientUserId));
                    return;
                } else if (message.post) {
                    this.currentChat = instance.getChat(Integer.valueOf(message.to_id.channel_id));
                    return;
                } else {
                    return;
                }
            }
        }
        this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(messageFwdHeader.channel_id));
    }

    /* JADX WARNING: Removed duplicated region for block: B:59:0x0191  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01f0  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0236  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0249  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0246  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0344  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0254  */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x03ce A:{Catch:{ Exception -> 0x03d2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x039d A:{Catch:{ Exception -> 0x03d2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x03e2  */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x042c  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x053a  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x04f1  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x061c A:{Catch:{ Exception -> 0x0628 }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x0632  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0150  */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0165  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0179 A:{SKIP} */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0191  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x019d  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x01f0  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x021d  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0236  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x0246  */
    /* JADX WARNING: Removed duplicated region for block: B:94:0x0249  */
    /* JADX WARNING: Removed duplicated region for block: B:97:0x0254  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x0344  */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x0369 A:{Catch:{ Exception -> 0x03d2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:127:0x039d A:{Catch:{ Exception -> 0x03d2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x03ce A:{Catch:{ Exception -> 0x03d2 }} */
    /* JADX WARNING: Removed duplicated region for block: B:133:0x03de  */
    /* JADX WARNING: Removed duplicated region for block: B:134:0x03e2  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x040e  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x042c  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x044a  */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x045c  */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x047c  */
    /* JADX WARNING: Removed duplicated region for block: B:176:0x04f1  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x053a  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x061c A:{Catch:{ Exception -> 0x0628 }} */
    /* JADX WARNING: Removed duplicated region for block: B:194:0x0632  */
    private void setMessageObjectInternal(org.telegram.messenger.MessageObject r39) {
        /*
        r38 = this;
        r1 = r38;
        r2 = r39;
        r0 = r2.messageOwner;
        r0 = r0.flags;
        r0 = r0 & 1024;
        r3 = 1;
        if (r0 == 0) goto L_0x0022;
    L_0x000d:
        r0 = r1.currentMessageObject;
        r0 = r0.viewsReloaded;
        if (r0 != 0) goto L_0x0022;
    L_0x0013:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r4 = r1.currentMessageObject;
        r0.addToViewsQueue(r4);
        r0 = r1.currentMessageObject;
        r0.viewsReloaded = r3;
    L_0x0022:
        r38.updateCurrentUserAndChat();
        r0 = r1.isAvatarVisible;
        r4 = 0;
        r5 = 0;
        if (r0 == 0) goto L_0x0097;
    L_0x002b:
        r0 = r1.currentUser;
        if (r0 == 0) goto L_0x0055;
    L_0x002f:
        r0 = r0.photo;
        if (r0 == 0) goto L_0x0038;
    L_0x0033:
        r0 = r0.photo_small;
        r1.currentPhoto = r0;
        goto L_0x003a;
    L_0x0038:
        r1.currentPhoto = r4;
    L_0x003a:
        r0 = r1.avatarDrawable;
        r6 = r1.currentUser;
        r0.setInfo(r6);
        r7 = r1.avatarImage;
        r0 = r1.currentUser;
        r8 = org.telegram.messenger.ImageLocation.getForUser(r0, r5);
        r10 = r1.avatarDrawable;
        r11 = 0;
        r12 = r1.currentUser;
        r13 = 0;
        r9 = "50_50";
        r7.setImage(r8, r9, r10, r11, r12, r13);
        goto L_0x0099;
    L_0x0055:
        r0 = r1.currentChat;
        if (r0 == 0) goto L_0x007f;
    L_0x0059:
        r0 = r0.photo;
        if (r0 == 0) goto L_0x0062;
    L_0x005d:
        r0 = r0.photo_small;
        r1.currentPhoto = r0;
        goto L_0x0064;
    L_0x0062:
        r1.currentPhoto = r4;
    L_0x0064:
        r0 = r1.avatarDrawable;
        r6 = r1.currentChat;
        r0.setInfo(r6);
        r7 = r1.avatarImage;
        r0 = r1.currentChat;
        r8 = org.telegram.messenger.ImageLocation.getForChat(r0, r5);
        r10 = r1.avatarDrawable;
        r11 = 0;
        r12 = r1.currentChat;
        r13 = 0;
        r9 = "50_50";
        r7.setImage(r8, r9, r10, r11, r12, r13);
        goto L_0x0099;
    L_0x007f:
        r1.currentPhoto = r4;
        r0 = r1.avatarDrawable;
        r6 = r2.messageOwner;
        r6 = r6.from_id;
        r0.setInfo(r6, r4, r4, r5);
        r7 = r1.avatarImage;
        r8 = 0;
        r9 = 0;
        r10 = r1.avatarDrawable;
        r11 = 0;
        r12 = 0;
        r13 = 0;
        r7.setImage(r8, r9, r10, r11, r12, r13);
        goto L_0x0099;
    L_0x0097:
        r1.currentPhoto = r4;
    L_0x0099:
        r38.measureTime(r39);
        r1.namesOffset = r5;
        r0 = r2.messageOwner;
        r6 = r0.via_bot_id;
        r7 = "@";
        r8 = NUM; // 0x7f0d0aca float:1.8747717E38 double:1.053131142E-314;
        r9 = "ViaBot";
        r10 = 2;
        if (r6 == 0) goto L_0x0104;
    L_0x00ac:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = r2.messageOwner;
        r6 = r6.via_bot_id;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r0.getUser(r6);
        if (r0 == 0) goto L_0x014a;
    L_0x00c0:
        r6 = r0.username;
        if (r6 == 0) goto L_0x014a;
    L_0x00c4:
        r6 = r6.length();
        if (r6 <= 0) goto L_0x014a;
    L_0x00ca:
        r6 = new java.lang.StringBuilder;
        r6.<init>();
        r6.append(r7);
        r7 = r0.username;
        r6.append(r7);
        r6 = r6.toString();
        r7 = new java.lang.Object[r10];
        r11 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r7[r5] = r11;
        r7[r3] = r6;
        r11 = " %s <b>%s</b>";
        r7 = java.lang.String.format(r11, r7);
        r7 = org.telegram.messenger.AndroidUtilities.replaceTags(r7);
        r11 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r12 = r7.length();
        r11 = r11.measureText(r7, r5, r12);
        r11 = (double) r11;
        r11 = java.lang.Math.ceil(r11);
        r11 = (int) r11;
        r1.viaWidth = r11;
        r1.currentViaBotUser = r0;
        goto L_0x014c;
    L_0x0104:
        r0 = r0.via_bot_name;
        if (r0 == 0) goto L_0x014a;
    L_0x0108:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x014a;
    L_0x010e:
        r0 = new java.lang.StringBuilder;
        r0.<init>();
        r0.append(r7);
        r6 = r2.messageOwner;
        r6 = r6.via_bot_name;
        r0.append(r6);
        r0 = r0.toString();
        r6 = new java.lang.Object[r10];
        r7 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r6[r5] = r7;
        r6[r3] = r0;
        r7 = " %s <b>%s</b>";
        r6 = java.lang.String.format(r7, r6);
        r6 = org.telegram.messenger.AndroidUtilities.replaceTags(r6);
        r7 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r11 = r6.length();
        r7 = r7.measureText(r6, r5, r11);
        r11 = (double) r7;
        r11 = java.lang.Math.ceil(r11);
        r7 = (int) r11;
        r1.viaWidth = r7;
        r7 = r6;
        r6 = r0;
        goto L_0x014c;
    L_0x014a:
        r6 = r4;
        r7 = r6;
    L_0x014c:
        r0 = r1.drawName;
        if (r0 == 0) goto L_0x015e;
    L_0x0150:
        r0 = r1.isChat;
        if (r0 == 0) goto L_0x015e;
    L_0x0154:
        r0 = r1.currentMessageObject;
        r0 = r0.isOutOwner();
        if (r0 != 0) goto L_0x015e;
    L_0x015c:
        r0 = 1;
        goto L_0x015f;
    L_0x015e:
        r0 = 0;
    L_0x015f:
        r11 = r2.messageOwner;
        r11 = r11.fwd_from;
        if (r11 == 0) goto L_0x016b;
    L_0x0165:
        r11 = r2.type;
        r12 = 14;
        if (r11 != r12) goto L_0x016f;
    L_0x016b:
        if (r6 == 0) goto L_0x016f;
    L_0x016d:
        r11 = 1;
        goto L_0x0170;
    L_0x016f:
        r11 = 0;
    L_0x0170:
        r13 = "fonts/rmedium.ttf";
        r15 = 32;
        r12 = 10;
        r14 = 5;
        if (r0 != 0) goto L_0x0185;
    L_0x0179:
        if (r11 == 0) goto L_0x017c;
    L_0x017b:
        goto L_0x0185;
    L_0x017c:
        r1.currentNameString = r4;
        r1.nameLayout = r4;
        r1.nameWidth = r5;
        r3 = r4;
        goto L_0x03e3;
    L_0x0185:
        r1.drawNameLayout = r3;
        r4 = r38.getMaxNameWidth();
        r1.nameWidth = r4;
        r4 = r1.nameWidth;
        if (r4 >= 0) goto L_0x0199;
    L_0x0191:
        r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r1.nameWidth = r4;
    L_0x0199:
        r4 = r1.isMegagroup;
        if (r4 == 0) goto L_0x01ca;
    L_0x019d:
        r4 = r1.currentChat;
        if (r4 == 0) goto L_0x01ca;
    L_0x01a1:
        r4 = r1.currentMessageObject;
        r4 = r4.isForwardedChannelPost();
        if (r4 == 0) goto L_0x01ca;
    L_0x01a9:
        r4 = NUM; // 0x7f0d0384 float:1.874394E38 double:1.053130222E-314;
        r10 = "DiscussChannel";
        r4 = org.telegram.messenger.LocaleController.getString(r10, r4);
        r10 = org.telegram.ui.ActionBar.Theme.chat_adminPaint;
        r10 = r10.measureText(r4);
        r21 = r4;
        r3 = (double) r10;
        r3 = java.lang.Math.ceil(r3);
        r3 = (int) r3;
        r4 = r1.nameWidth;
        r4 = r4 - r3;
        r1.nameWidth = r4;
        r23 = r21;
        r21 = r9;
        goto L_0x021b;
    L_0x01ca:
        r3 = r1.currentUser;
        if (r3 == 0) goto L_0x0216;
    L_0x01ce:
        r3 = r1.currentMessageObject;
        r3 = r3.isOutOwner();
        if (r3 != 0) goto L_0x0216;
    L_0x01d6:
        r3 = r1.currentMessageObject;
        r3 = r3.isAnyKindOfSticker();
        if (r3 != 0) goto L_0x0216;
    L_0x01de:
        r3 = r1.currentMessageObject;
        r3 = r3.type;
        if (r3 == r14) goto L_0x0216;
    L_0x01e4:
        r3 = r1.delegate;
        r4 = r1.currentUser;
        r4 = r4.id;
        r3 = r3.getAdminRank(r4);
        if (r3 == 0) goto L_0x0216;
    L_0x01f0:
        r4 = r3.length();
        if (r4 != 0) goto L_0x01ff;
    L_0x01f6:
        r3 = NUM; // 0x7f0d027e float:1.8743409E38 double:1.053130093E-314;
        r4 = "ChatAdmin";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
    L_0x01ff:
        r4 = r3;
        r3 = org.telegram.ui.ActionBar.Theme.chat_adminPaint;
        r3 = r3.measureText(r4);
        r21 = r9;
        r8 = (double) r3;
        r8 = java.lang.Math.ceil(r8);
        r3 = (int) r8;
        r8 = r1.nameWidth;
        r8 = r8 - r3;
        r1.nameWidth = r8;
        r23 = r4;
        goto L_0x021b;
    L_0x0216:
        r21 = r9;
        r3 = 0;
        r23 = 0;
    L_0x021b:
        if (r0 == 0) goto L_0x0236;
    L_0x021d:
        r0 = r1.currentUser;
        if (r0 == 0) goto L_0x0228;
    L_0x0221:
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r1.currentNameString = r0;
        goto L_0x023a;
    L_0x0228:
        r0 = r1.currentChat;
        if (r0 == 0) goto L_0x0231;
    L_0x022c:
        r0 = r0.title;
        r1.currentNameString = r0;
        goto L_0x023a;
    L_0x0231:
        r0 = "DELETED";
        r1.currentNameString = r0;
        goto L_0x023a;
    L_0x0236:
        r0 = "";
        r1.currentNameString = r0;
    L_0x023a:
        r0 = r1.currentNameString;
        r0 = r0.replace(r12, r15);
        r4 = org.telegram.ui.ActionBar.Theme.chat_namePaint;
        r8 = r1.nameWidth;
        if (r11 == 0) goto L_0x0249;
    L_0x0246:
        r9 = r1.viaWidth;
        goto L_0x024a;
    L_0x0249:
        r9 = 0;
    L_0x024a:
        r8 = r8 - r9;
        r8 = (float) r8;
        r9 = android.text.TextUtils.TruncateAt.END;
        r0 = android.text.TextUtils.ellipsize(r0, r4, r8, r9);
        if (r11 == 0) goto L_0x0344;
    L_0x0254:
        r4 = org.telegram.ui.ActionBar.Theme.chat_namePaint;
        r8 = r0.length();
        r4 = r4.measureText(r0, r5, r8);
        r8 = (double) r4;
        r8 = java.lang.Math.ceil(r8);
        r4 = (int) r8;
        r1.viaNameWidth = r4;
        r4 = r1.viaNameWidth;
        if (r4 == 0) goto L_0x0273;
    L_0x026a:
        r8 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r4 = r4 + r8;
        r1.viaNameWidth = r4;
    L_0x0273:
        r4 = r1.currentMessageObject;
        r4 = r4.shouldDrawWithoutBackground();
        if (r4 == 0) goto L_0x0287;
    L_0x027b:
        r4 = "chat_stickerViaBotNameText";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
    L_0x0281:
        r9 = r21;
        r8 = NUM; // 0x7f0d0aca float:1.8747717E38 double:1.053131142E-314;
        goto L_0x0299;
    L_0x0287:
        r4 = r1.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 == 0) goto L_0x0292;
    L_0x028f:
        r4 = "chat_outViaBotNameText";
        goto L_0x0294;
    L_0x0292:
        r4 = "chat_inViaBotNameText";
    L_0x0294:
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        goto L_0x0281;
    L_0x0299:
        r11 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r8 = r1.currentNameString;
        r8 = r8.length();
        if (r8 <= 0) goto L_0x02fb;
    L_0x02a5:
        r8 = new android.text.SpannableStringBuilder;
        r10 = 3;
        r10 = new java.lang.Object[r10];
        r10[r5] = r0;
        r20 = 1;
        r10[r20] = r11;
        r17 = 2;
        r10[r17] = r6;
        r14 = "%s %s %s";
        r10 = java.lang.String.format(r14, r10);
        r8.<init>(r10);
        r10 = new org.telegram.ui.Components.TypefaceSpan;
        r14 = android.graphics.Typeface.DEFAULT;
        r10.<init>(r14, r5, r4);
        r1.viaSpan1 = r10;
        r14 = r0.length();
        r14 = r14 + 1;
        r22 = r0.length();
        r22 = r22 + 1;
        r24 = r11.length();
        r12 = r22 + r24;
        r15 = 33;
        r8.setSpan(r10, r14, r12, r15);
        r10 = new org.telegram.ui.Components.TypefaceSpan;
        r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r13);
        r10.<init>(r12, r5, r4);
        r1.viaSpan2 = r10;
        r0 = r0.length();
        r12 = 2;
        r0 = r0 + r12;
        r4 = r11.length();
        r0 = r0 + r4;
        r4 = r8.length();
        r8.setSpan(r10, r0, r4, r15);
        goto L_0x0338;
    L_0x02fb:
        r12 = 2;
        r8 = new android.text.SpannableStringBuilder;
        r0 = new java.lang.Object[r12];
        r0[r5] = r11;
        r10 = 1;
        r0[r10] = r6;
        r12 = "%s %s";
        r0 = java.lang.String.format(r12, r0);
        r8.<init>(r0);
        r0 = new org.telegram.ui.Components.TypefaceSpan;
        r12 = android.graphics.Typeface.DEFAULT;
        r0.<init>(r12, r5, r4);
        r1.viaSpan1 = r0;
        r12 = r11.length();
        r12 = r12 + r10;
        r14 = 33;
        r8.setSpan(r0, r5, r12, r14);
        r0 = new org.telegram.ui.Components.TypefaceSpan;
        r12 = org.telegram.messenger.AndroidUtilities.getTypeface(r13);
        r0.<init>(r12, r5, r4);
        r1.viaSpan2 = r0;
        r4 = r11.length();
        r4 = r4 + r10;
        r10 = r8.length();
        r8.setSpan(r0, r4, r10, r14);
    L_0x0338:
        r0 = org.telegram.ui.ActionBar.Theme.chat_namePaint;
        r4 = r1.nameWidth;
        r4 = (float) r4;
        r10 = android.text.TextUtils.TruncateAt.END;
        r0 = android.text.TextUtils.ellipsize(r8, r0, r4, r10);
        goto L_0x0346;
    L_0x0344:
        r9 = r21;
    L_0x0346:
        r31 = r0;
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x03d2 }
        r32 = org.telegram.ui.ActionBar.Theme.chat_namePaint;	 Catch:{ Exception -> 0x03d2 }
        r4 = r1.nameWidth;	 Catch:{ Exception -> 0x03d2 }
        r8 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x03d2 }
        r33 = r4 + r10;
        r34 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x03d2 }
        r35 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r36 = 0;
        r37 = 0;
        r30 = r0;
        r30.<init>(r31, r32, r33, r34, r35, r36, r37);	 Catch:{ Exception -> 0x03d2 }
        r1.nameLayout = r0;	 Catch:{ Exception -> 0x03d2 }
        r0 = r1.nameLayout;	 Catch:{ Exception -> 0x03d2 }
        if (r0 == 0) goto L_0x0399;
    L_0x0369:
        r0 = r1.nameLayout;	 Catch:{ Exception -> 0x03d2 }
        r0 = r0.getLineCount();	 Catch:{ Exception -> 0x03d2 }
        if (r0 <= 0) goto L_0x0399;
    L_0x0371:
        r0 = r1.nameLayout;	 Catch:{ Exception -> 0x03d2 }
        r0 = r0.getLineWidth(r5);	 Catch:{ Exception -> 0x03d2 }
        r10 = (double) r0;	 Catch:{ Exception -> 0x03d2 }
        r10 = java.lang.Math.ceil(r10);	 Catch:{ Exception -> 0x03d2 }
        r0 = (int) r10;	 Catch:{ Exception -> 0x03d2 }
        r1.nameWidth = r0;	 Catch:{ Exception -> 0x03d2 }
        r0 = r39.isAnyKindOfSticker();	 Catch:{ Exception -> 0x03d2 }
        if (r0 != 0) goto L_0x0390;
    L_0x0385:
        r0 = r1.namesOffset;	 Catch:{ Exception -> 0x03d2 }
        r4 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x03d2 }
        r0 = r0 + r4;
        r1.namesOffset = r0;	 Catch:{ Exception -> 0x03d2 }
    L_0x0390:
        r0 = r1.nameLayout;	 Catch:{ Exception -> 0x03d2 }
        r0 = r0.getLineLeft(r5);	 Catch:{ Exception -> 0x03d2 }
        r1.nameOffsetX = r0;	 Catch:{ Exception -> 0x03d2 }
        goto L_0x039b;
    L_0x0399:
        r1.nameWidth = r5;	 Catch:{ Exception -> 0x03d2 }
    L_0x039b:
        if (r23 == 0) goto L_0x03ce;
    L_0x039d:
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x03d2 }
        r24 = org.telegram.ui.ActionBar.Theme.chat_adminPaint;	 Catch:{ Exception -> 0x03d2 }
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x03d2 }
        r25 = r3 + r8;
        r26 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x03d2 }
        r27 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r28 = 0;
        r29 = 0;
        r22 = r0;
        r22.<init>(r23, r24, r25, r26, r27, r28, r29);	 Catch:{ Exception -> 0x03d2 }
        r1.adminLayout = r0;	 Catch:{ Exception -> 0x03d2 }
        r0 = r1.nameWidth;	 Catch:{ Exception -> 0x03d2 }
        r0 = (float) r0;	 Catch:{ Exception -> 0x03d2 }
        r3 = r1.adminLayout;	 Catch:{ Exception -> 0x03d2 }
        r3 = r3.getLineWidth(r5);	 Catch:{ Exception -> 0x03d2 }
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x03d2 }
        r4 = (float) r8;	 Catch:{ Exception -> 0x03d2 }
        r3 = r3 + r4;
        r0 = r0 + r3;
        r0 = (int) r0;	 Catch:{ Exception -> 0x03d2 }
        r1.nameWidth = r0;	 Catch:{ Exception -> 0x03d2 }
        goto L_0x03d6;
    L_0x03ce:
        r3 = 0;
        r1.adminLayout = r3;	 Catch:{ Exception -> 0x03d2 }
        goto L_0x03d6;
    L_0x03d2:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x03d6:
        r0 = r1.currentNameString;
        r0 = r0.length();
        if (r0 != 0) goto L_0x03e2;
    L_0x03de:
        r3 = 0;
        r1.currentNameString = r3;
        goto L_0x03e3;
    L_0x03e2:
        r3 = 0;
    L_0x03e3:
        r1.currentForwardUser = r3;
        r1.currentForwardNameString = r3;
        r1.currentForwardChannel = r3;
        r1.currentForwardName = r3;
        r0 = r1.forwardedNameLayout;
        r0[r5] = r3;
        r4 = 1;
        r0[r4] = r3;
        r1.forwardedNameWidth = r5;
        r0 = r1.drawForwardedName;
        if (r0 == 0) goto L_0x062c;
    L_0x03f8:
        r0 = r39.needDrawForwarded();
        if (r0 == 0) goto L_0x062c;
    L_0x03fe:
        r0 = r1.currentPosition;
        if (r0 == 0) goto L_0x0406;
    L_0x0402:
        r0 = r0.minY;
        if (r0 != 0) goto L_0x062c;
    L_0x0406:
        r0 = r2.messageOwner;
        r0 = r0.fwd_from;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x0424;
    L_0x040e:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r3 = r2.messageOwner;
        r3 = r3.fwd_from;
        r3 = r3.channel_id;
        r3 = java.lang.Integer.valueOf(r3);
        r0 = r0.getChat(r3);
        r1.currentForwardChannel = r0;
    L_0x0424:
        r0 = r2.messageOwner;
        r0 = r0.fwd_from;
        r0 = r0.from_id;
        if (r0 == 0) goto L_0x0442;
    L_0x042c:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r3 = r2.messageOwner;
        r3 = r3.fwd_from;
        r3 = r3.from_id;
        r3 = java.lang.Integer.valueOf(r3);
        r0 = r0.getUser(r3);
        r1.currentForwardUser = r0;
    L_0x0442:
        r0 = r2.messageOwner;
        r0 = r0.fwd_from;
        r0 = r0.from_name;
        if (r0 == 0) goto L_0x044c;
    L_0x044a:
        r1.currentForwardName = r0;
    L_0x044c:
        r0 = r1.currentForwardUser;
        if (r0 != 0) goto L_0x0458;
    L_0x0450:
        r0 = r1.currentForwardChannel;
        if (r0 != 0) goto L_0x0458;
    L_0x0454:
        r0 = r1.currentForwardName;
        if (r0 == 0) goto L_0x062c;
    L_0x0458:
        r0 = r1.currentForwardChannel;
        if (r0 == 0) goto L_0x047c;
    L_0x045c:
        r3 = r1.currentForwardUser;
        if (r3 == 0) goto L_0x0477;
    L_0x0460:
        r4 = 2;
        r8 = new java.lang.Object[r4];
        r0 = r0.title;
        r8[r5] = r0;
        r0 = org.telegram.messenger.UserObject.getUserName(r3);
        r3 = 1;
        r8[r3] = r0;
        r0 = "%s (%s)";
        r0 = java.lang.String.format(r0, r8);
        r1.currentForwardNameString = r0;
        goto L_0x048d;
    L_0x0477:
        r0 = r0.title;
        r1.currentForwardNameString = r0;
        goto L_0x048d;
    L_0x047c:
        r0 = r1.currentForwardUser;
        if (r0 == 0) goto L_0x0487;
    L_0x0480:
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        r1.currentForwardNameString = r0;
        goto L_0x048d;
    L_0x0487:
        r0 = r1.currentForwardName;
        if (r0 == 0) goto L_0x048d;
    L_0x048b:
        r1.currentForwardNameString = r0;
    L_0x048d:
        r0 = r38.getMaxNameWidth();
        r1.forwardedNameWidth = r0;
        r0 = NUM; // 0x7f0d04c7 float:1.8744595E38 double:1.053130382E-314;
        r3 = "From";
        r0 = org.telegram.messenger.LocaleController.getString(r3, r0);
        r3 = NUM; // 0x7f0d04cf float:1.8744612E38 double:1.0531303857E-314;
        r4 = "FromFormatted";
        r3 = org.telegram.messenger.LocaleController.getString(r4, r3);
        r4 = "%1$s";
        r4 = r3.indexOf(r4);
        r8 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r0);
        r0 = " ";
        r10.append(r0);
        r0 = r10.toString();
        r0 = r8.measureText(r0);
        r10 = (double) r0;
        r10 = java.lang.Math.ceil(r10);
        r0 = (int) r10;
        r8 = r1.currentForwardNameString;
        r10 = 32;
        r11 = 10;
        r8 = r8.replace(r11, r10);
        r10 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r11 = r1.forwardedNameWidth;
        r11 = r11 - r0;
        r12 = r1.viaWidth;
        r11 = r11 - r12;
        r11 = (float) r11;
        r12 = android.text.TextUtils.TruncateAt.END;
        r8 = android.text.TextUtils.ellipsize(r8, r10, r11, r12);
        r10 = 1;
        r11 = new java.lang.Object[r10];	 Catch:{ Exception -> 0x04eb }
        r11[r5] = r8;	 Catch:{ Exception -> 0x04eb }
        r10 = java.lang.String.format(r3, r11);	 Catch:{ Exception -> 0x04eb }
        goto L_0x04ef;
    L_0x04eb:
        r10 = r8.toString();
    L_0x04ef:
        if (r7 == 0) goto L_0x053a;
    L_0x04f1:
        r3 = new android.text.SpannableStringBuilder;
        r7 = 3;
        r7 = new java.lang.Object[r7];
        r7[r5] = r10;
        r11 = NUM; // 0x7f0d0aca float:1.8747717E38 double:1.053131142E-314;
        r9 = org.telegram.messenger.LocaleController.getString(r9, r11);
        r11 = 1;
        r7[r11] = r9;
        r9 = 2;
        r7[r9] = r6;
        r9 = "%s %s %s";
        r7 = java.lang.String.format(r9, r7);
        r3.<init>(r7);
        r7 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;
        r7 = r7.measureText(r10);
        r9 = (double) r7;
        r9 = java.lang.Math.ceil(r9);
        r7 = (int) r9;
        r1.viaNameWidth = r7;
        r7 = new org.telegram.ui.Components.TypefaceSpan;
        r9 = org.telegram.messenger.AndroidUtilities.getTypeface(r13);
        r7.<init>(r9);
        r9 = r3.length();
        r6 = r6.length();
        r9 = r9 - r6;
        r6 = 1;
        r9 = r9 - r6;
        r10 = r3.length();
        r11 = 33;
        r3.setSpan(r7, r9, r10, r11);
        goto L_0x0549;
    L_0x053a:
        r6 = 1;
        r7 = new android.text.SpannableStringBuilder;
        r9 = new java.lang.Object[r6];
        r9[r5] = r8;
        r3 = java.lang.String.format(r3, r9);
        r7.<init>(r3);
        r3 = r7;
    L_0x0549:
        r6 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;
        r7 = r8.length();
        r6 = r6.measureText(r8, r5, r7);
        r6 = (double) r6;
        r6 = java.lang.Math.ceil(r6);
        r6 = (int) r6;
        r7 = 2;
        r6 = r6 / r7;
        r0 = r0 + r6;
        r1.forwardNameCenterX = r0;
        if (r4 < 0) goto L_0x057f;
    L_0x0560:
        r0 = r1.currentForwardName;
        if (r0 == 0) goto L_0x056c;
    L_0x0564:
        r0 = r2.messageOwner;
        r0 = r0.fwd_from;
        r0 = r0.from_id;
        if (r0 == 0) goto L_0x057f;
    L_0x056c:
        r0 = new org.telegram.ui.Components.TypefaceSpan;
        r6 = org.telegram.messenger.AndroidUtilities.getTypeface(r13);
        r0.<init>(r6);
        r6 = r8.length();
        r6 = r6 + r4;
        r7 = 33;
        r3.setSpan(r0, r4, r6, r7);
    L_0x057f:
        r0 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;
        r4 = r1.forwardedNameWidth;
        r4 = (float) r4;
        r6 = android.text.TextUtils.TruncateAt.END;
        r8 = android.text.TextUtils.ellipsize(r3, r0, r4, r6);
        r0 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x0628 }
        r3 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0628 }
        r9 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;	 Catch:{ Exception -> 0x0628 }
        r4 = r1.forwardedNameWidth;	 Catch:{ Exception -> 0x0628 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0628 }
        r10 = r4 + r7;
        r11 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0628 }
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = 0;
        r14 = 0;
        r7 = r3;
        r7.<init>(r8, r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x0628 }
        r4 = 1;
        r0[r4] = r3;	 Catch:{ Exception -> 0x0628 }
        r0 = "ForwardedMessage";
        r3 = NUM; // 0x7f0d049b float:1.8744506E38 double:1.05313036E-314;
        r0 = org.telegram.messenger.LocaleController.getString(r0, r3);	 Catch:{ Exception -> 0x0628 }
        r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0);	 Catch:{ Exception -> 0x0628 }
        r3 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;	 Catch:{ Exception -> 0x0628 }
        r4 = r1.forwardedNameWidth;	 Catch:{ Exception -> 0x0628 }
        r4 = (float) r4;	 Catch:{ Exception -> 0x0628 }
        r6 = android.text.TextUtils.TruncateAt.END;	 Catch:{ Exception -> 0x0628 }
        r8 = android.text.TextUtils.ellipsize(r0, r3, r4, r6);	 Catch:{ Exception -> 0x0628 }
        r0 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x0628 }
        r3 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0628 }
        r9 = org.telegram.ui.ActionBar.Theme.chat_forwardNamePaint;	 Catch:{ Exception -> 0x0628 }
        r4 = r1.forwardedNameWidth;	 Catch:{ Exception -> 0x0628 }
        r6 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);	 Catch:{ Exception -> 0x0628 }
        r10 = r4 + r6;
        r11 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0628 }
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = 0;
        r14 = 0;
        r7 = r3;
        r7.<init>(r8, r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x0628 }
        r0[r5] = r3;	 Catch:{ Exception -> 0x0628 }
        r0 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x0628 }
        r0 = r0[r5];	 Catch:{ Exception -> 0x0628 }
        r0 = r0.getLineWidth(r5);	 Catch:{ Exception -> 0x0628 }
        r3 = (double) r0;	 Catch:{ Exception -> 0x0628 }
        r3 = java.lang.Math.ceil(r3);	 Catch:{ Exception -> 0x0628 }
        r0 = (int) r3;	 Catch:{ Exception -> 0x0628 }
        r3 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x0628 }
        r4 = 1;
        r3 = r3[r4];	 Catch:{ Exception -> 0x0628 }
        r3 = r3.getLineWidth(r5);	 Catch:{ Exception -> 0x0628 }
        r3 = (double) r3;	 Catch:{ Exception -> 0x0628 }
        r3 = java.lang.Math.ceil(r3);	 Catch:{ Exception -> 0x0628 }
        r3 = (int) r3;	 Catch:{ Exception -> 0x0628 }
        r0 = java.lang.Math.max(r0, r3);	 Catch:{ Exception -> 0x0628 }
        r1.forwardedNameWidth = r0;	 Catch:{ Exception -> 0x0628 }
        r0 = r1.forwardNameOffsetX;	 Catch:{ Exception -> 0x0628 }
        r3 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x0628 }
        r3 = r3[r5];	 Catch:{ Exception -> 0x0628 }
        r3 = r3.getLineLeft(r5);	 Catch:{ Exception -> 0x0628 }
        r0[r5] = r3;	 Catch:{ Exception -> 0x0628 }
        r0 = r1.forwardNameOffsetX;	 Catch:{ Exception -> 0x0628 }
        r3 = r1.forwardedNameLayout;	 Catch:{ Exception -> 0x0628 }
        r4 = 1;
        r3 = r3[r4];	 Catch:{ Exception -> 0x0628 }
        r3 = r3.getLineLeft(r5);	 Catch:{ Exception -> 0x0628 }
        r0[r4] = r3;	 Catch:{ Exception -> 0x0628 }
        r0 = r2.type;	 Catch:{ Exception -> 0x0628 }
        r3 = 5;
        if (r0 == r3) goto L_0x062c;
    L_0x061c:
        r0 = r1.namesOffset;	 Catch:{ Exception -> 0x0628 }
        r3 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x0628 }
        r0 = r0 + r3;
        r1.namesOffset = r0;	 Catch:{ Exception -> 0x0628 }
        goto L_0x062c;
    L_0x0628:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x062c:
        r0 = r39.hasValidReplyMessageObject();
        if (r0 == 0) goto L_0x08f5;
    L_0x0632:
        r0 = r1.currentPosition;
        if (r0 == 0) goto L_0x063a;
    L_0x0636:
        r0 = r0.minY;
        if (r0 != 0) goto L_0x08f5;
    L_0x063a:
        r0 = r39.isAnyKindOfSticker();
        if (r0 != 0) goto L_0x065f;
    L_0x0640:
        r0 = r2.type;
        r3 = 5;
        if (r0 == r3) goto L_0x065f;
    L_0x0645:
        r0 = r1.namesOffset;
        r3 = NUM; // 0x42280000 float:42.0 double:5.483722033E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 + r3;
        r1.namesOffset = r0;
        r0 = r2.type;
        if (r0 == 0) goto L_0x065f;
    L_0x0654:
        r0 = r1.namesOffset;
        r3 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 + r3;
        r1.namesOffset = r0;
    L_0x065f:
        r0 = r38.getMaxNameWidth();
        r3 = r39.shouldDrawWithoutBackground();
        if (r3 != 0) goto L_0x0671;
    L_0x0669:
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        goto L_0x067d;
    L_0x0671:
        r3 = r2.type;
        r4 = 5;
        if (r3 != r4) goto L_0x067d;
    L_0x0676:
        r3 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 + r3;
    L_0x067d:
        r3 = r2.replyMessageObject;
        r3 = r3.photoThumbs2;
        r4 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4);
        r4 = r2.replyMessageObject;
        r4 = r4.photoThumbs2;
        r6 = 40;
        r4 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r4, r6);
        r6 = r2.replyMessageObject;
        r7 = r6.photoThumbsObject2;
        if (r3 != 0) goto L_0x06cc;
    L_0x0697:
        r3 = r6.mediaExists;
        if (r3 == 0) goto L_0x06ad;
    L_0x069b:
        r3 = r6.photoThumbs;
        r4 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4);
        if (r3 == 0) goto L_0x06aa;
    L_0x06a7:
        r4 = r3.size;
        goto L_0x06ab;
    L_0x06aa:
        r4 = 0;
    L_0x06ab:
        r6 = 0;
        goto L_0x06b7;
    L_0x06ad:
        r3 = r6.photoThumbs;
        r4 = 320; // 0x140 float:4.48E-43 double:1.58E-321;
        r3 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r3, r4);
        r4 = 0;
        r6 = 1;
    L_0x06b7:
        r7 = r2.replyMessageObject;
        r7 = r7.photoThumbs;
        r8 = 40;
        r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8);
        r8 = r2.replyMessageObject;
        r8 = r8.photoThumbsObject;
        r26 = r4;
        r29 = r6;
        r4 = r7;
        r7 = r8;
        goto L_0x06d0;
    L_0x06cc:
        r26 = 0;
        r29 = 1;
    L_0x06d0:
        if (r4 != r3) goto L_0x06d3;
    L_0x06d2:
        r4 = 0;
    L_0x06d3:
        if (r3 == 0) goto L_0x0733;
    L_0x06d5:
        r6 = r2.replyMessageObject;
        r6 = r6.isAnyKindOfSticker();
        if (r6 != 0) goto L_0x0733;
    L_0x06dd:
        r6 = r39.isAnyKindOfSticker();
        if (r6 == 0) goto L_0x06e9;
    L_0x06e3:
        r6 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r6 == 0) goto L_0x0733;
    L_0x06e9:
        r6 = r2.replyMessageObject;
        r6 = r6.isSecretMedia();
        if (r6 == 0) goto L_0x06f2;
    L_0x06f1:
        goto L_0x0733;
    L_0x06f2:
        r6 = r2.replyMessageObject;
        r6 = r6.isRoundVideo();
        if (r6 == 0) goto L_0x0706;
    L_0x06fa:
        r6 = r1.replyImageReceiver;
        r8 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r8 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r6.setRoundRadius(r8);
        goto L_0x070b;
    L_0x0706:
        r6 = r1.replyImageReceiver;
        r6.setRoundRadius(r5);
    L_0x070b:
        r1.currentReplyPhoto = r3;
        r6 = r1.replyImageReceiver;
        r22 = org.telegram.messenger.ImageLocation.getForObject(r3, r7);
        r24 = org.telegram.messenger.ImageLocation.getForObject(r4, r7);
        r27 = 0;
        r3 = r2.replyMessageObject;
        r23 = "50_50";
        r25 = "50_50_b";
        r21 = r6;
        r28 = r3;
        r21.setImage(r22, r23, r24, r25, r26, r27, r28, r29);
        r3 = 1;
        r1.needReplyImage = r3;
        r3 = NUM; // 0x42300000 float:44.0 double:5.48631236E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r0 = r0 - r3;
        r3 = r0;
        r4 = 0;
        goto L_0x073c;
    L_0x0733:
        r3 = r1.replyImageReceiver;
        r4 = 0;
        r3.setImageBitmap(r4);
        r1.needReplyImage = r5;
        r3 = r0;
    L_0x073c:
        r0 = r2.customReplyName;
        if (r0 == 0) goto L_0x0741;
    L_0x0740:
        goto L_0x07a2;
    L_0x0741:
        r0 = r2.replyMessageObject;
        r0 = r0.isFromUser();
        if (r0 == 0) goto L_0x0764;
    L_0x0749:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = r2.replyMessageObject;
        r6 = r6.messageOwner;
        r6 = r6.from_id;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r0.getUser(r6);
        if (r0 == 0) goto L_0x07a1;
    L_0x075f:
        r0 = org.telegram.messenger.UserObject.getUserName(r0);
        goto L_0x07a2;
    L_0x0764:
        r0 = r2.replyMessageObject;
        r0 = r0.messageOwner;
        r0 = r0.from_id;
        if (r0 >= 0) goto L_0x0786;
    L_0x076c:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = r2.replyMessageObject;
        r6 = r6.messageOwner;
        r6 = r6.from_id;
        r6 = -r6;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r0.getChat(r6);
        if (r0 == 0) goto L_0x07a1;
    L_0x0783:
        r0 = r0.title;
        goto L_0x07a2;
    L_0x0786:
        r0 = r1.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r6 = r2.replyMessageObject;
        r6 = r6.messageOwner;
        r6 = r6.to_id;
        r6 = r6.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r0.getChat(r6);
        if (r0 == 0) goto L_0x07a1;
    L_0x079e:
        r0 = r0.title;
        goto L_0x07a2;
    L_0x07a1:
        r0 = r4;
    L_0x07a2:
        if (r0 != 0) goto L_0x07ad;
    L_0x07a4:
        r0 = NUM; // 0x7f0d058e float:1.8744999E38 double:1.05313048E-314;
        r6 = "Loading";
        r0 = org.telegram.messenger.LocaleController.getString(r6, r0);
    L_0x07ad:
        r6 = 32;
        r7 = 10;
        r0 = r0.replace(r7, r6);
        r6 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;
        r7 = (float) r3;
        r8 = android.text.TextUtils.TruncateAt.END;
        r17 = android.text.TextUtils.ellipsize(r0, r6, r7, r8);
        r0 = r2.replyMessageObject;
        r6 = r0.messageOwner;
        r6 = r6.media;
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        r9 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        if (r8 == 0) goto L_0x07e6;
    L_0x07ca:
        r0 = r6.game;
        r0 = r0.title;
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r2 = r2.getFontMetricsInt();
        r4 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r4, r5);
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r4 = android.text.TextUtils.TruncateAt.END;
        r4 = android.text.TextUtils.ellipsize(r0, r2, r7, r4);
    L_0x07e4:
        r7 = r4;
        goto L_0x0842;
    L_0x07e6:
        r8 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
        if (r8 == 0) goto L_0x0803;
    L_0x07ea:
        r0 = r6.title;
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r2 = r2.getFontMetricsInt();
        r4 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r4, r5);
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r4 = android.text.TextUtils.TruncateAt.END;
        r4 = android.text.TextUtils.ellipsize(r0, r2, r7, r4);
        goto L_0x07e4;
    L_0x0803:
        r0 = r0.messageText;
        if (r0 == 0) goto L_0x07e4;
    L_0x0807:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x07e4;
    L_0x080d:
        r0 = r2.replyMessageObject;
        r0 = r0.messageText;
        r0 = r0.toString();
        r2 = r0.length();
        r4 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        if (r2 <= r4) goto L_0x0823;
    L_0x081d:
        r2 = 150; // 0x96 float:2.1E-43 double:7.4E-322;
        r0 = r0.substring(r5, r2);
    L_0x0823:
        r2 = 32;
        r4 = 10;
        r0 = r0.replace(r4, r2);
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r2 = r2.getFontMetricsInt();
        r4 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r2, r4, r5);
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;
        r4 = android.text.TextUtils.TruncateAt.END;
        r4 = android.text.TextUtils.ellipsize(r0, r2, r7, r4);
        goto L_0x07e4;
    L_0x0842:
        r0 = 4;
        r2 = r1.needReplyImage;	 Catch:{ Exception -> 0x0899 }
        if (r2 == 0) goto L_0x084a;
    L_0x0847:
        r2 = 44;
        goto L_0x084b;
    L_0x084a:
        r2 = 0;
    L_0x084b:
        r0 = r0 + r2;
        r0 = (float) r0;	 Catch:{ Exception -> 0x0899 }
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Exception -> 0x0899 }
        r1.replyNameWidth = r0;	 Catch:{ Exception -> 0x0899 }
        if (r17 == 0) goto L_0x089d;
    L_0x0855:
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0899 }
        r18 = org.telegram.ui.ActionBar.Theme.chat_replyNamePaint;	 Catch:{ Exception -> 0x0899 }
        r2 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x0899 }
        r19 = r3 + r2;
        r20 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0899 }
        r21 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r22 = 0;
        r23 = 0;
        r16 = r0;
        r16.<init>(r17, r18, r19, r20, r21, r22, r23);	 Catch:{ Exception -> 0x0899 }
        r1.replyNameLayout = r0;	 Catch:{ Exception -> 0x0899 }
        r0 = r1.replyNameLayout;	 Catch:{ Exception -> 0x0899 }
        r0 = r0.getLineCount();	 Catch:{ Exception -> 0x0899 }
        if (r0 <= 0) goto L_0x089d;
    L_0x0878:
        r0 = r1.replyNameWidth;	 Catch:{ Exception -> 0x0899 }
        r2 = r1.replyNameLayout;	 Catch:{ Exception -> 0x0899 }
        r2 = r2.getLineWidth(r5);	 Catch:{ Exception -> 0x0899 }
        r8 = (double) r2;	 Catch:{ Exception -> 0x0899 }
        r8 = java.lang.Math.ceil(r8);	 Catch:{ Exception -> 0x0899 }
        r2 = (int) r8;	 Catch:{ Exception -> 0x0899 }
        r4 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r4);	 Catch:{ Exception -> 0x0899 }
        r2 = r2 + r6;
        r0 = r0 + r2;
        r1.replyNameWidth = r0;	 Catch:{ Exception -> 0x0899 }
        r0 = r1.replyNameLayout;	 Catch:{ Exception -> 0x0899 }
        r0 = r0.getLineLeft(r5);	 Catch:{ Exception -> 0x0899 }
        r1.replyNameOffset = r0;	 Catch:{ Exception -> 0x0899 }
        goto L_0x089d;
    L_0x0899:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x089d:
        r0 = 4;
        r2 = r1.needReplyImage;	 Catch:{ Exception -> 0x08f1 }
        if (r2 == 0) goto L_0x08a5;
    L_0x08a2:
        r2 = 44;
        goto L_0x08a6;
    L_0x08a5:
        r2 = 0;
    L_0x08a6:
        r0 = r0 + r2;
        r0 = (float) r0;	 Catch:{ Exception -> 0x08f1 }
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Exception -> 0x08f1 }
        r1.replyTextWidth = r0;	 Catch:{ Exception -> 0x08f1 }
        if (r7 == 0) goto L_0x08f5;
    L_0x08b0:
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x08f1 }
        r8 = org.telegram.ui.ActionBar.Theme.chat_replyTextPaint;	 Catch:{ Exception -> 0x08f1 }
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x08f1 }
        r9 = r3 + r2;
        r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x08f1 }
        r11 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r12 = 0;
        r13 = 0;
        r6 = r0;
        r6.<init>(r7, r8, r9, r10, r11, r12, r13);	 Catch:{ Exception -> 0x08f1 }
        r1.replyTextLayout = r0;	 Catch:{ Exception -> 0x08f1 }
        r0 = r1.replyTextLayout;	 Catch:{ Exception -> 0x08f1 }
        r0 = r0.getLineCount();	 Catch:{ Exception -> 0x08f1 }
        if (r0 <= 0) goto L_0x08f5;
    L_0x08d0:
        r0 = r1.replyTextWidth;	 Catch:{ Exception -> 0x08f1 }
        r2 = r1.replyTextLayout;	 Catch:{ Exception -> 0x08f1 }
        r2 = r2.getLineWidth(r5);	 Catch:{ Exception -> 0x08f1 }
        r2 = (double) r2;	 Catch:{ Exception -> 0x08f1 }
        r2 = java.lang.Math.ceil(r2);	 Catch:{ Exception -> 0x08f1 }
        r2 = (int) r2;	 Catch:{ Exception -> 0x08f1 }
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);	 Catch:{ Exception -> 0x08f1 }
        r2 = r2 + r3;
        r0 = r0 + r2;
        r1.replyTextWidth = r0;	 Catch:{ Exception -> 0x08f1 }
        r0 = r1.replyTextLayout;	 Catch:{ Exception -> 0x08f1 }
        r0 = r0.getLineLeft(r5);	 Catch:{ Exception -> 0x08f1 }
        r1.replyTextOffset = r0;	 Catch:{ Exception -> 0x08f1 }
        goto L_0x08f5;
    L_0x08f1:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x08f5:
        r38.requestLayout();
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

    public float getCheckBoxTranslation() {
        return (float) this.checkBoxTranslation;
    }

    public void drawCheckBox(Canvas canvas) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.isSending() && !this.currentMessageObject.isSendError() && this.checkBox != null) {
            if (this.checkBoxVisible || this.checkBoxAnimationInProgress) {
                GroupedMessagePosition groupedMessagePosition = this.currentPosition;
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

    /* Access modifiers changed, original: protected */
    public void onDraw(Canvas canvas) {
        Canvas canvas2 = canvas;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            if (this.wasLayout) {
                int i;
                Drawable drawable;
                Drawable drawable2;
                Drawable drawable3;
                int i2;
                float f;
                int i3;
                String str;
                if (messageObject.isOutOwner()) {
                    Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
                    str = "chat_messageLinkOut";
                    Theme.chat_msgTextPaint.linkColor = Theme.getColor(str);
                    Theme.chat_msgGameTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
                    Theme.chat_msgGameTextPaint.linkColor = Theme.getColor(str);
                    Theme.chat_replyTextPaint.linkColor = Theme.getColor(str);
                } else {
                    Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
                    str = "chat_messageLinkIn";
                    Theme.chat_msgTextPaint.linkColor = Theme.getColor(str);
                    Theme.chat_msgGameTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
                    Theme.chat_msgGameTextPaint.linkColor = Theme.getColor(str);
                    Theme.chat_replyTextPaint.linkColor = Theme.getColor(str);
                }
                if (this.documentAttach != null) {
                    i = this.documentAttachType;
                    String str2 = "chat_outAudioSeekbarFill";
                    String str3 = "chat_inAudioSeekbarFill";
                    if (i == 3) {
                        if (this.currentMessageObject.isOutOwner()) {
                            this.seekBarWaveform.setColors(Theme.getColor("chat_outVoiceSeekbar"), Theme.getColor("chat_outVoiceSeekbarFill"), Theme.getColor("chat_outVoiceSeekbarSelected"));
                            this.seekBar.setColors(Theme.getColor("chat_outAudioSeekbar"), Theme.getColor("chat_outAudioCacheSeekbar"), Theme.getColor(str2), Theme.getColor(str2), Theme.getColor("chat_outAudioSeekbarSelected"));
                        } else {
                            this.seekBarWaveform.setColors(Theme.getColor("chat_inVoiceSeekbar"), Theme.getColor("chat_inVoiceSeekbarFill"), Theme.getColor("chat_inVoiceSeekbarSelected"));
                            this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor(str3), Theme.getColor(str3), Theme.getColor("chat_inAudioSeekbarSelected"));
                        }
                    } else if (i == 5) {
                        this.documentAttachType = 5;
                        if (this.currentMessageObject.isOutOwner()) {
                            this.seekBar.setColors(Theme.getColor("chat_outAudioSeekbar"), Theme.getColor("chat_outAudioCacheSeekbar"), Theme.getColor(str2), Theme.getColor(str2), Theme.getColor("chat_outAudioSeekbarSelected"));
                        } else {
                            this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioCacheSeekbar"), Theme.getColor(str3), Theme.getColor(str3), Theme.getColor("chat_inAudioSeekbarSelected"));
                        }
                    }
                }
                messageObject = this.currentMessageObject;
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
                int i4 = 0;
                int i5;
                int dp;
                int dp2;
                if (this.currentMessageObject.isOutOwner()) {
                    int i6;
                    if (this.mediaBackground || this.drawPinnedBottom) {
                        this.currentBackgroundDrawable = Theme.chat_msgOutMediaDrawable;
                        drawable = Theme.chat_msgOutMediaSelectedDrawable;
                        drawable2 = Theme.chat_msgOutMediaShadowDrawable;
                    } else {
                        this.currentBackgroundDrawable = Theme.chat_msgOutDrawable;
                        drawable = Theme.chat_msgOutSelectedDrawable;
                        drawable2 = Theme.chat_msgOutShadowDrawable;
                    }
                    this.backgroundDrawableLeft = (this.layoutWidth - this.backgroundWidth) - (!this.mediaBackground ? 0 : AndroidUtilities.dp(9.0f));
                    this.backgroundDrawableRight = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
                    if (!(this.currentMessagesGroup == null || this.currentPosition.edge)) {
                        this.backgroundDrawableRight += AndroidUtilities.dp(10.0f);
                    }
                    i5 = this.backgroundDrawableLeft;
                    if (!this.mediaBackground && this.drawPinnedBottom) {
                        this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                    }
                    GroupedMessagePosition groupedMessagePosition = this.currentPosition;
                    if (groupedMessagePosition != null) {
                        if ((groupedMessagePosition.flags & 2) == 0) {
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 1) == 0) {
                            i5 -= AndroidUtilities.dp(8.0f);
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 4) == 0) {
                            dp = 0 - AndroidUtilities.dp(9.0f);
                            dp2 = AndroidUtilities.dp(9.0f) + 0;
                        } else {
                            dp = 0;
                            dp2 = 0;
                        }
                        if ((this.currentPosition.flags & 8) == 0) {
                            dp2 += AndroidUtilities.dp(9.0f);
                        }
                    } else {
                        dp = 0;
                        dp2 = 0;
                    }
                    if (this.drawPinnedBottom && this.drawPinnedTop) {
                        i6 = 0;
                    } else if (this.drawPinnedBottom) {
                        i6 = AndroidUtilities.dp(1.0f);
                    } else {
                        i6 = AndroidUtilities.dp(2.0f);
                    }
                    boolean z = this.drawPinnedTop;
                    int dp3 = (z || (z && this.drawPinnedBottom)) ? 0 : AndroidUtilities.dp(1.0f);
                    dp += dp3;
                    BaseCell.setDrawableBounds(this.currentBackgroundDrawable, i5, dp, this.backgroundDrawableRight, (this.layoutHeight - i6) + dp2);
                    BaseCell.setDrawableBounds(drawable, i5, dp, this.backgroundDrawableRight, (this.layoutHeight - i6) + dp2);
                    BaseCell.setDrawableBounds(drawable2, i5, dp, this.backgroundDrawableRight, (this.layoutHeight - i6) + dp2);
                } else {
                    if (this.mediaBackground || this.drawPinnedBottom) {
                        this.currentBackgroundDrawable = Theme.chat_msgInMediaDrawable;
                        drawable = Theme.chat_msgInMediaSelectedDrawable;
                        drawable3 = Theme.chat_msgInMediaShadowDrawable;
                    } else {
                        this.currentBackgroundDrawable = Theme.chat_msgInDrawable;
                        drawable = Theme.chat_msgInSelectedDrawable;
                        drawable3 = Theme.chat_msgInShadowDrawable;
                    }
                    drawable2 = drawable3;
                    i2 = (this.isChat && this.isAvatarVisible) ? 48 : 0;
                    this.backgroundDrawableLeft = AndroidUtilities.dp((float) (i2 + (!this.mediaBackground ? 3 : 9)));
                    this.backgroundDrawableRight = this.backgroundWidth - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
                    if (this.currentMessagesGroup != null) {
                        if (!this.currentPosition.edge) {
                            this.backgroundDrawableLeft -= AndroidUtilities.dp(10.0f);
                            this.backgroundDrawableRight += AndroidUtilities.dp(10.0f);
                        }
                        i2 = this.currentPosition.leftSpanOffset;
                        if (i2 != 0) {
                            this.backgroundDrawableLeft += (int) Math.ceil((double) ((((float) i2) / 1000.0f) * ((float) getGroupPhotosWidth())));
                        }
                    }
                    if (!this.mediaBackground && this.drawPinnedBottom) {
                        this.backgroundDrawableRight -= AndroidUtilities.dp(6.0f);
                        this.backgroundDrawableLeft += AndroidUtilities.dp(6.0f);
                    }
                    GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
                    if (groupedMessagePosition2 != null) {
                        if ((groupedMessagePosition2.flags & 2) == 0) {
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 1) == 0) {
                            this.backgroundDrawableLeft -= AndroidUtilities.dp(8.0f);
                            this.backgroundDrawableRight += AndroidUtilities.dp(8.0f);
                        }
                        if ((this.currentPosition.flags & 4) == 0) {
                            i2 = 0 - AndroidUtilities.dp(9.0f);
                            dp = AndroidUtilities.dp(9.0f) + 0;
                        } else {
                            i2 = 0;
                            dp = 0;
                        }
                        if ((this.currentPosition.flags & 8) == 0) {
                            dp += AndroidUtilities.dp(10.0f);
                        }
                    } else {
                        i2 = 0;
                        dp = 0;
                    }
                    if (this.drawPinnedBottom && this.drawPinnedTop) {
                        i5 = 0;
                    } else if (this.drawPinnedBottom) {
                        i5 = AndroidUtilities.dp(1.0f);
                    } else {
                        i5 = AndroidUtilities.dp(2.0f);
                    }
                    boolean z2 = this.drawPinnedTop;
                    dp2 = (z2 || (z2 && this.drawPinnedBottom)) ? 0 : AndroidUtilities.dp(1.0f);
                    i2 += dp2;
                    BaseCell.setDrawableBounds(this.currentBackgroundDrawable, this.backgroundDrawableLeft, i2, this.backgroundDrawableRight, (this.layoutHeight - i5) + dp);
                    BaseCell.setDrawableBounds(drawable, this.backgroundDrawableLeft, i2, this.backgroundDrawableRight, (this.layoutHeight - i5) + dp);
                    BaseCell.setDrawableBounds(drawable2, this.backgroundDrawableLeft, i2, this.backgroundDrawableRight, (this.layoutHeight - i5) + dp);
                }
                if (this.checkBoxVisible || this.checkBoxAnimationInProgress) {
                    if ((this.checkBoxVisible && this.checkBoxAnimationProgress == 1.0f) || (!this.checkBoxVisible && this.checkBoxAnimationProgress == 0.0f)) {
                        this.checkBoxAnimationInProgress = false;
                    }
                    this.checkBoxTranslation = (int) Math.ceil((double) ((this.checkBoxVisible ? CubicBezierInterpolator.EASE_OUT : CubicBezierInterpolator.EASE_IN).getInterpolation(this.checkBoxAnimationProgress) * ((float) AndroidUtilities.dp(35.0f))));
                    if (!this.currentMessageObject.isOutOwner()) {
                        setTranslationX((float) this.checkBoxTranslation);
                    }
                    i2 = AndroidUtilities.dp(21.0f);
                    this.checkBox.setBounds(AndroidUtilities.dp(-27.0f) + this.checkBoxTranslation, (this.currentBackgroundDrawable.getBounds().bottom - AndroidUtilities.dp(8.0f)) - i2, i2, i2);
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
                if (this.drawBackground) {
                    drawable3 = this.currentBackgroundDrawable;
                    if (drawable3 != null) {
                        if (this.isHighlightedAnimated) {
                            drawable3.draw(canvas2);
                            i2 = this.highlightProgress;
                            f = i2 >= 300 ? 1.0f : ((float) i2) / 300.0f;
                            if (this.currentPosition == null) {
                                drawable.setAlpha((int) (f * 255.0f));
                                drawable.draw(canvas2);
                            }
                        } else if (!isDrawSelectionBackground() || (this.currentPosition != null && getBackground() == null)) {
                            this.currentBackgroundDrawable.draw(canvas2);
                        } else {
                            drawable.setAlpha(255);
                            drawable.draw(canvas2);
                        }
                        GroupedMessagePosition groupedMessagePosition3 = this.currentPosition;
                        if (groupedMessagePosition3 == null || groupedMessagePosition3.flags != 0) {
                            drawable2.draw(canvas2);
                        }
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
                f = 12.0f;
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
                    drawable = Theme.chat_shareDrawable;
                    i3 = this.shareStartX;
                    int dp4 = this.layoutHeight - AndroidUtilities.dp(41.0f);
                    this.shareStartY = dp4;
                    BaseCell.setDrawableBounds(drawable, i3, dp4);
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
                            this.replyStartX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.dp(4.0f);
                        } else {
                            this.replyStartX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.dp(17.0f);
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
                                i3 = this.backgroundDrawableLeft;
                                if (z3 || !this.drawPinnedBottom) {
                                    f = 18.0f;
                                }
                                this.replyStartX = i3 + AndroidUtilities.dp(f);
                            }
                        }
                        i2 = (!this.drawForwardedName || this.forwardedNameLayout[0] == null) ? 0 : 36;
                        i = 12 + i2;
                        if (this.drawNameLayout && this.nameLayout != null) {
                            i4 = 20;
                        }
                        this.replyStartY = AndroidUtilities.dp((float) (i + i4));
                    }
                }
                if (this.currentPosition == null) {
                    drawNamesLayout(canvas);
                }
                if (!(this.autoPlayingMedia && MediaController.getInstance().isPlayingMessageAndReadyToDraw(this.currentMessageObject))) {
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
                    if (this.forceNotDrawTime) {
                        GroupedMessagePosition groupedMessagePosition4 = this.currentPosition;
                        if (!(groupedMessagePosition4 == null || !groupedMessagePosition4.last || getParent() == null)) {
                            ((View) getParent()).invalidate();
                        }
                    }
                }
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
                GroupedMessagePosition groupedMessagePosition = this.currentPosition;
                if (groupedMessagePosition == null) {
                    return true;
                }
                if (groupedMessagePosition.minY == (byte) 0 && groupedMessagePosition.minX == (byte) 0) {
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
        float f;
        int alpha;
        GroupedMessagePosition groupedMessagePosition;
        float f2 = 17.0f;
        int i = 0;
        if (this.drawNameLayout && this.nameLayout != null) {
            canvas.save();
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                Theme.chat_namePaint.setColor(Theme.getColor("chat_stickerNameText"));
                if (this.currentMessageObject.isOutOwner()) {
                    this.nameX = (float) AndroidUtilities.dp(28.0f);
                } else {
                    this.nameX = (float) ((this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.dp(22.0f));
                }
                this.nameY = (float) (this.layoutHeight - AndroidUtilities.dp(38.0f));
                f = (this.currentMessageObject.isOut() && (this.checkBoxVisible || this.checkBoxAnimationInProgress)) ? 1.0f - this.checkBoxAnimationProgress : 1.0f;
                Theme.chat_systemDrawable.setAlpha((int) (255.0f * f));
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(((int) this.nameX) - AndroidUtilities.dp(12.0f), ((int) this.nameY) - AndroidUtilities.dp(5.0f), (((int) this.nameX) + AndroidUtilities.dp(12.0f)) + this.nameWidth, ((int) this.nameY) + AndroidUtilities.dp(22.0f));
                Theme.chat_systemDrawable.draw(canvas);
                if (this.checkBoxVisible || this.checkBoxAnimationInProgress) {
                    Theme.chat_systemDrawable.setAlpha(255);
                }
                this.nameX -= this.nameOffsetX;
                alpha = (((int) (((float) Color.alpha(Theme.getColor("chat_stickerViaBotNameText"))) * f)) << 24) | (Theme.getColor("chat_stickerViaBotNameText") & 16777215);
                TypefaceSpan typefaceSpan = this.viaSpan1;
                if (typefaceSpan != null) {
                    typefaceSpan.setColor(alpha);
                }
                typefaceSpan = this.viaSpan2;
                if (typefaceSpan != null) {
                    typefaceSpan.setColor(alpha);
                }
                Theme.chat_systemDrawable.setAlpha(255);
            } else {
                if (this.mediaBackground || this.currentMessageObject.isOutOwner()) {
                    this.nameX = ((float) (this.backgroundDrawableLeft + AndroidUtilities.dp(11.0f))) - this.nameOffsetX;
                } else {
                    alpha = this.backgroundDrawableLeft;
                    float f3 = (this.mediaBackground || !this.drawPinnedBottom) ? 17.0f : 11.0f;
                    this.nameX = ((float) (alpha + AndroidUtilities.dp(f3))) - this.nameOffsetX;
                }
                User user = this.currentUser;
                if (user != null) {
                    Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(user.id));
                } else {
                    Chat chat = this.currentChat;
                    if (chat == null) {
                        Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(0));
                    } else if (!ChatObject.isChannel(chat) || this.currentChat.megagroup) {
                        Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentChat.id));
                    } else {
                        Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(5));
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
        String str = "chat_stickerReplyNameText";
        if (this.drawForwardedName) {
            StaticLayout[] staticLayoutArr = this.forwardedNameLayout;
            if (!(staticLayoutArr[0] == null || staticLayoutArr[1] == null)) {
                groupedMessagePosition = this.currentPosition;
                if (groupedMessagePosition == null || (groupedMessagePosition.minY == (byte) 0 && groupedMessagePosition.minX == (byte) 0)) {
                    if (this.currentMessageObject.type == 5) {
                        Theme.chat_forwardNamePaint.setColor(Theme.getColor(str));
                        if (this.currentMessageObject.isOutOwner()) {
                            this.forwardNameX = AndroidUtilities.dp(23.0f);
                        } else {
                            this.forwardNameX = (this.backgroundDrawableLeft + this.backgroundDrawableRight) + AndroidUtilities.dp(17.0f);
                        }
                        this.forwardNameY = AndroidUtilities.dp(12.0f);
                        alpha = this.forwardedNameWidth + AndroidUtilities.dp(14.0f);
                        Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                        Theme.chat_systemDrawable.setBounds(this.forwardNameX - AndroidUtilities.dp(7.0f), this.forwardNameY - AndroidUtilities.dp(6.0f), (this.forwardNameX - AndroidUtilities.dp(7.0f)) + alpha, this.forwardNameY + AndroidUtilities.dp(38.0f));
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
                                    f2 = 11.0f;
                                }
                                this.forwardNameX = i2 + AndroidUtilities.dp(f2);
                            }
                        }
                    }
                    for (alpha = 0; alpha < 2; alpha++) {
                        canvas.save();
                        canvas.translate(((float) this.forwardNameX) - this.forwardNameOffsetX[alpha], (float) (this.forwardNameY + (AndroidUtilities.dp(16.0f) * alpha)));
                        this.forwardedNameLayout[alpha].draw(canvas);
                        canvas.restore();
                    }
                }
            }
        }
        if (this.replyNameLayout != null) {
            MessageObject messageObject;
            MessageMedia messageMedia;
            if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_stickerReplyLine"));
                Theme.chat_replyNamePaint.setColor(Theme.getColor(str));
                Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_stickerReplyMessageText"));
                alpha = Math.max(this.replyNameWidth, this.replyTextWidth) + AndroidUtilities.dp(14.0f);
                Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
                Theme.chat_systemDrawable.setBounds(this.replyStartX - AndroidUtilities.dp(7.0f), this.replyStartY - AndroidUtilities.dp(6.0f), (this.replyStartX - AndroidUtilities.dp(7.0f)) + alpha, this.replyStartY + AndroidUtilities.dp(41.0f));
                Theme.chat_systemDrawable.draw(canvas);
            } else if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_outReplyLine"));
                Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_outReplyNameText"));
                if (this.currentMessageObject.hasValidReplyMessageObject()) {
                    messageObject = this.currentMessageObject.replyMessageObject;
                    if (messageObject.type == 0) {
                        messageMedia = messageObject.messageOwner.media;
                        if (!((messageMedia instanceof TL_messageMediaGame) || (messageMedia instanceof TL_messageMediaInvoice))) {
                            Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_outReplyMessageText"));
                        }
                    }
                }
                Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outReplyMediaMessageSelectedText" : "chat_outReplyMediaMessageText"));
            } else {
                Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_inReplyLine"));
                Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_inReplyNameText"));
                if (this.currentMessageObject.hasValidReplyMessageObject()) {
                    messageObject = this.currentMessageObject.replyMessageObject;
                    if (messageObject.type == 0) {
                        messageMedia = messageObject.messageOwner.media;
                        if (!((messageMedia instanceof TL_messageMediaGame) || (messageMedia instanceof TL_messageMediaInvoice))) {
                            Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_inReplyMessageText"));
                        }
                    }
                }
                Theme.chat_replyTextPaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inReplyMediaMessageSelectedText" : "chat_inReplyMediaMessageText"));
            }
            groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition == null || (groupedMessagePosition.minY == (byte) 0 && groupedMessagePosition.minX == (byte) 0)) {
                alpha = this.replyStartX;
                canvas.drawRect((float) alpha, (float) this.replyStartY, (float) (alpha + AndroidUtilities.dp(2.0f)), (float) (this.replyStartY + AndroidUtilities.dp(35.0f)), Theme.chat_replyLinePaint);
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

    public void setDrawSelectionBackground(boolean z) {
        this.drawSelectionBackground = z;
        invalidate();
    }

    public boolean isDrawingSelectionBackground() {
        return this.drawSelectionBackground || this.isHighlightedAnimated || this.isHighlighted;
    }

    public float getHightlightAlpha() {
        if (this.drawSelectionBackground || !this.isHighlightedAnimated) {
            return 1.0f;
        }
        int i = this.highlightProgress;
        if (i >= 300) {
            return 1.0f;
        }
        return ((float) i) / 300.0f;
    }

    public void setCheckBoxVisible(boolean z, boolean z2) {
        if (z && this.checkBox == null) {
            this.checkBox = new CheckBoxBase(this, 21);
            if (this.attachedToWindow) {
                this.checkBox.onAttachedToWindow();
            }
        }
        if (z && this.photoCheckBox == null) {
            GroupedMessages groupedMessages = this.currentMessagesGroup;
            if (groupedMessages != null && groupedMessages.messages.size() > 1) {
                this.photoCheckBox = new CheckBoxBase(this, 21);
                this.photoCheckBox.setUseDefaultCheck(true);
                if (this.attachedToWindow) {
                    this.photoCheckBox.onAttachedToWindow();
                }
            }
        }
        float f = 1.0f;
        if (this.checkBoxVisible == z) {
            if (!(z2 == this.checkBoxAnimationInProgress || z2)) {
                if (!z) {
                    f = 0.0f;
                }
                this.checkBoxAnimationProgress = f;
                invalidate();
            }
            return;
        }
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
                    canvas.drawPath((Path) this.urlPath.get(i), Theme.chat_urlPaint);
                }
            }
            if (!this.urlPathSelection.isEmpty()) {
                for (int i2 = 0; i2 < this.urlPathSelection.size(); i2++) {
                    canvas.drawPath((Path) this.urlPathSelection.get(i2), Theme.chat_textSearchSelectionPaint);
                }
            }
            if (!z) {
                try {
                    this.captionLayout.draw(canvas);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            canvas.restore();
        }
    }

    public boolean needDrawTime() {
        return this.forceNotDrawTime ^ 1;
    }

    public void drawTime(Canvas canvas) {
        Canvas canvas2 = canvas;
        if (((this.drawTime && !this.groupPhotoInvisible) || !this.mediaBackground || this.captionLayout != null) && this.timeLayout != null) {
            int i;
            int alpha;
            int dp;
            MessageObject messageObject = this.currentMessageObject;
            String str = "chat_mediaTimeText";
            if (messageObject.type == 5) {
                Theme.chat_timePaint.setColor(Theme.getColor(str));
            } else if (this.mediaBackground && this.captionLayout == null) {
                if (messageObject.shouldDrawWithoutBackground()) {
                    Theme.chat_timePaint.setColor(Theme.getColor("chat_serviceText"));
                } else {
                    Theme.chat_timePaint.setColor(Theme.getColor(str));
                }
            } else if (this.currentMessageObject.isOutOwner()) {
                Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_outTimeSelectedText" : "chat_outTimeText"));
            } else {
                Theme.chat_timePaint.setColor(Theme.getColor(isDrawSelectionBackground() ? "chat_inTimeSelectedText" : "chat_inTimeText"));
            }
            if (this.drawPinnedBottom) {
                canvas2.translate(0.0f, (float) AndroidUtilities.dp(2.0f));
            }
            int i2 = 0;
            Drawable drawable;
            if (this.mediaBackground && this.captionLayout == null) {
                Paint paint;
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    paint = Theme.chat_actionBackgroundPaint;
                } else {
                    paint = Theme.chat_timeBackgroundPaint;
                }
                int alpha2 = paint.getAlpha();
                paint.setAlpha((int) (((float) alpha2) * this.timeAlpha));
                Theme.chat_timePaint.setAlpha((int) (this.timeAlpha * 255.0f));
                int dp2 = this.timeX - AndroidUtilities.dp(4.0f);
                int dp3 = this.layoutHeight - AndroidUtilities.dp(28.0f);
                this.rect.set((float) dp2, (float) dp3, (float) ((dp2 + this.timeWidth) + AndroidUtilities.dp((float) ((this.currentMessageObject.isOutOwner() ? 20 : 0) + 8))), (float) (dp3 + AndroidUtilities.dp(17.0f)));
                canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f), paint);
                paint.setAlpha(alpha2);
                i = (int) (-this.timeLayout.getLineLeft(0));
                if ((this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                    i += (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(0));
                    if (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing()) {
                        if (!this.currentMessageObject.isOutOwner()) {
                            BaseCell.setDrawableBounds(Theme.chat_msgMediaClockDrawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(14.0f)) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
                            Theme.chat_msgMediaClockDrawable.draw(canvas2);
                        }
                    } else if (!this.currentMessageObject.isSendError()) {
                        if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                            drawable = Theme.chat_msgStickerViewsDrawable;
                        } else {
                            drawable = Theme.chat_msgMediaViewsDrawable;
                        }
                        alpha = ((BitmapDrawable) drawable).getPaint().getAlpha();
                        drawable.setAlpha((int) (this.timeAlpha * ((float) alpha)));
                        BaseCell.setDrawableBounds(drawable, this.timeX, (this.layoutHeight - AndroidUtilities.dp(10.5f)) - this.timeLayout.getHeight());
                        drawable.draw(canvas2);
                        drawable.setAlpha(alpha);
                        if (this.viewsLayout != null) {
                            canvas.save();
                            canvas2.translate((float) ((this.timeX + drawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.dp(12.3f)) - this.timeLayout.getHeight()));
                            this.viewsLayout.draw(canvas2);
                            canvas.restore();
                        }
                    } else if (!this.currentMessageObject.isOutOwner()) {
                        dp = this.timeX + AndroidUtilities.dp(11.0f);
                        alpha = this.layoutHeight - AndroidUtilities.dp(27.5f);
                        this.rect.set((float) dp, (float) alpha, (float) (AndroidUtilities.dp(14.0f) + dp), (float) (AndroidUtilities.dp(14.0f) + alpha));
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                        BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, dp + AndroidUtilities.dp(6.0f), alpha + AndroidUtilities.dp(2.0f));
                        Theme.chat_msgErrorDrawable.draw(canvas2);
                    }
                }
                canvas.save();
                canvas2.translate((float) (this.timeX + i), (float) ((this.layoutHeight - AndroidUtilities.dp(12.3f)) - this.timeLayout.getHeight()));
                this.timeLayout.draw(canvas2);
                canvas.restore();
                Theme.chat_timePaint.setAlpha(255);
            } else {
                i = (int) (-this.timeLayout.getLineLeft(0));
                if ((this.currentMessageObject.messageOwner.flags & 1024) != 0) {
                    i += (int) (((float) this.timeWidth) - this.timeLayout.getLineWidth(0));
                    if (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing()) {
                        if (!this.currentMessageObject.isOutOwner()) {
                            drawable = isDrawSelectionBackground() ? Theme.chat_msgInSelectedClockDrawable : Theme.chat_msgInClockDrawable;
                            BaseCell.setDrawableBounds(drawable, this.timeX + AndroidUtilities.dp(11.0f), (this.layoutHeight - AndroidUtilities.dp(8.5f)) - drawable.getIntrinsicHeight());
                            drawable.draw(canvas2);
                        }
                    } else if (!this.currentMessageObject.isSendError()) {
                        if (this.currentMessageObject.isOutOwner()) {
                            drawable = isDrawSelectionBackground() ? Theme.chat_msgOutViewsSelectedDrawable : Theme.chat_msgOutViewsDrawable;
                            BaseCell.setDrawableBounds(drawable, this.timeX, (this.layoutHeight - AndroidUtilities.dp(4.5f)) - this.timeLayout.getHeight());
                            drawable.draw(canvas2);
                        } else {
                            drawable = isDrawSelectionBackground() ? Theme.chat_msgInViewsSelectedDrawable : Theme.chat_msgInViewsDrawable;
                            BaseCell.setDrawableBounds(drawable, this.timeX, (this.layoutHeight - AndroidUtilities.dp(4.5f)) - this.timeLayout.getHeight());
                            drawable.draw(canvas2);
                        }
                        if (this.viewsLayout != null) {
                            canvas.save();
                            canvas2.translate((float) ((this.timeX + Theme.chat_msgInViewsDrawable.getIntrinsicWidth()) + AndroidUtilities.dp(3.0f)), (float) ((this.layoutHeight - AndroidUtilities.dp(6.5f)) - this.timeLayout.getHeight()));
                            this.viewsLayout.draw(canvas2);
                            canvas.restore();
                        }
                    } else if (!this.currentMessageObject.isOutOwner()) {
                        dp = this.timeX + AndroidUtilities.dp(11.0f);
                        alpha = this.layoutHeight - AndroidUtilities.dp(20.5f);
                        this.rect.set((float) dp, (float) alpha, (float) (AndroidUtilities.dp(14.0f) + dp), (float) (AndroidUtilities.dp(14.0f) + alpha));
                        canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                        BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, dp + AndroidUtilities.dp(6.0f), alpha + AndroidUtilities.dp(2.0f));
                        Theme.chat_msgErrorDrawable.draw(canvas2);
                    }
                }
                canvas.save();
                canvas2.translate((float) (this.timeX + i), (float) ((this.layoutHeight - AndroidUtilities.dp(6.5f)) - this.timeLayout.getHeight()));
                this.timeLayout.draw(canvas2);
                canvas.restore();
            }
            if (this.currentMessageObject.isOutOwner()) {
                Object obj;
                Object obj2;
                Object obj3 = 1;
                Object obj4 = ((int) (this.currentMessageObject.getDialogId() >> 32)) == 1 ? 1 : null;
                if (this.currentMessageObject.isSending() || this.currentMessageObject.isEditing()) {
                    obj3 = null;
                    obj = null;
                    obj2 = null;
                    i2 = 1;
                } else if (this.currentMessageObject.isSendError()) {
                    obj3 = null;
                    obj = null;
                    obj2 = 1;
                } else {
                    if (!this.currentMessageObject.isSent()) {
                        obj3 = null;
                    } else if (!this.currentMessageObject.isUnread()) {
                        obj = 1;
                        obj2 = null;
                    }
                    obj = null;
                    obj2 = null;
                }
                if (i2 != 0) {
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
                if (obj4 == null) {
                    Drawable drawable2;
                    if (obj3 != null) {
                        if (!this.mediaBackground || this.captionLayout != null) {
                            drawable2 = isDrawSelectionBackground() ? Theme.chat_msgOutCheckSelectedDrawable : Theme.chat_msgOutCheckDrawable;
                            if (obj != null) {
                                BaseCell.setDrawableBounds(drawable2, (this.layoutWidth - AndroidUtilities.dp(22.5f)) - drawable2.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable2.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(drawable2, (this.layoutWidth - AndroidUtilities.dp(18.5f)) - drawable2.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable2.getIntrinsicHeight());
                            }
                            drawable2.draw(canvas2);
                        } else if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                            if (obj != null) {
                                BaseCell.setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(Theme.chat_msgStickerCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgStickerCheckDrawable.draw(canvas2);
                        } else {
                            if (obj != null) {
                                BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(26.3f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                            } else {
                                BaseCell.setDrawableBounds(Theme.chat_msgMediaCheckDrawable, (this.layoutWidth - AndroidUtilities.dp(21.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(13.5f)) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
                            }
                            Theme.chat_msgMediaCheckDrawable.setAlpha((int) (this.timeAlpha * 255.0f));
                            Theme.chat_msgMediaCheckDrawable.draw(canvas2);
                            Theme.chat_msgMediaCheckDrawable.setAlpha(255);
                        }
                    }
                    if (obj != null) {
                        if (!this.mediaBackground || this.captionLayout != null) {
                            drawable2 = isDrawSelectionBackground() ? Theme.chat_msgOutHalfCheckSelectedDrawable : Theme.chat_msgOutHalfCheckDrawable;
                            BaseCell.setDrawableBounds(drawable2, (this.layoutWidth - AndroidUtilities.dp(18.0f)) - drawable2.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - drawable2.getIntrinsicHeight());
                            drawable2.draw(canvas2);
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
                } else if (!(obj == null && obj3 == null)) {
                    if (this.mediaBackground && this.captionLayout == null) {
                        BaseCell.setDrawableBounds(Theme.chat_msgBroadcastMediaDrawable, (this.layoutWidth - AndroidUtilities.dp(24.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(14.0f)) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicHeight());
                        Theme.chat_msgBroadcastMediaDrawable.draw(canvas2);
                    } else {
                        BaseCell.setDrawableBounds(Theme.chat_msgBroadcastDrawable, (this.layoutWidth - AndroidUtilities.dp(20.5f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicWidth(), (this.layoutHeight - AndroidUtilities.dp(8.0f)) - Theme.chat_msgBroadcastDrawable.getIntrinsicHeight());
                        Theme.chat_msgBroadcastDrawable.draw(canvas2);
                    }
                }
                if (obj2 != null) {
                    if (this.mediaBackground && this.captionLayout == null) {
                        i = this.layoutWidth - AndroidUtilities.dp(34.5f);
                        dp = this.layoutHeight;
                        alpha = AndroidUtilities.dp(26.5f);
                    } else {
                        i = this.layoutWidth - AndroidUtilities.dp(32.0f);
                        dp = this.layoutHeight;
                        alpha = AndroidUtilities.dp(21.0f);
                    }
                    dp -= alpha;
                    this.rect.set((float) i, (float) dp, (float) (AndroidUtilities.dp(14.0f) + i), (float) (AndroidUtilities.dp(14.0f) + dp));
                    canvas2.drawRoundRect(this.rect, (float) AndroidUtilities.dp(1.0f), (float) AndroidUtilities.dp(1.0f), Theme.chat_msgErrorPaint);
                    BaseCell.setDrawableBounds(Theme.chat_msgErrorDrawable, i + AndroidUtilities.dp(6.0f), dp + AndroidUtilities.dp(2.0f));
                    Theme.chat_msgErrorDrawable.draw(canvas2);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:374:0x0c0c  */
    /* JADX WARNING: Removed duplicated region for block: B:384:0x0c2e  */
    /* JADX WARNING: Removed duplicated region for block: B:397:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0c3c  */
    public void drawOverlays(android.graphics.Canvas r24) {
        /*
        r23 = this;
        r0 = r23;
        r7 = r24;
        r1 = android.os.SystemClock.uptimeMillis();
        r3 = r0.lastAnimationTime;
        r3 = r1 - r3;
        r5 = 17;
        r8 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r8 <= 0) goto L_0x0014;
    L_0x0012:
        r3 = 17;
    L_0x0014:
        r0.lastAnimationTime = r1;
        r1 = r0.currentMessageObject;
        r1 = r1.hadAnimationNotReadyLoading;
        r2 = 4;
        r8 = 2;
        r9 = 1;
        r10 = 0;
        if (r1 == 0) goto L_0x004e;
    L_0x0020:
        r1 = r0.photoImage;
        r1 = r1.getVisible();
        if (r1 == 0) goto L_0x004e;
    L_0x0028:
        r1 = r0.currentMessageObject;
        r1 = r1.needDrawBluredPreview();
        if (r1 != 0) goto L_0x004e;
    L_0x0030:
        r1 = r0.documentAttachType;
        r5 = 7;
        if (r1 == r5) goto L_0x0039;
    L_0x0035:
        if (r1 == r2) goto L_0x0039;
    L_0x0037:
        if (r1 != r8) goto L_0x004e;
    L_0x0039:
        r1 = r0.photoImage;
        r1 = r1.getAnimation();
        if (r1 == 0) goto L_0x004e;
    L_0x0041:
        r1 = r1.hasBitmap();
        if (r1 == 0) goto L_0x004e;
    L_0x0047:
        r1 = r0.currentMessageObject;
        r1.hadAnimationNotReadyLoading = r10;
        r0.updateButtonState(r10, r9, r10);
    L_0x004e:
        r1 = r0.currentMessageObject;
        r5 = r1.type;
        r11 = NUM; // 0x41600000 float:14.0 double:5.41896386E-315;
        r12 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r13 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r14 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        if (r5 == r9) goto L_0x0934;
    L_0x005e:
        r6 = r0.documentAttachType;
        if (r6 == r2) goto L_0x0934;
    L_0x0062:
        if (r6 != r8) goto L_0x0066;
    L_0x0064:
        goto L_0x0934;
    L_0x0066:
        r3 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        r17 = NUM; // 0x40CLASSNAME float:6.0 double:5.367157323E-315;
        r18 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        if (r5 != r2) goto L_0x029a;
    L_0x006e:
        r2 = r0.docTitleLayout;
        if (r2 == 0) goto L_0x0bf8;
    L_0x0072:
        r1 = r1.isOutOwner();
        if (r1 == 0) goto L_0x0098;
    L_0x0078:
        r1 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
        r2 = "chat_messageTextOut";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        r1 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
        r2 = r23.isDrawSelectionBackground();
        if (r2 == 0) goto L_0x008e;
    L_0x008b:
        r2 = "chat_outVenueInfoSelectedText";
        goto L_0x0090;
    L_0x008e:
        r2 = "chat_outVenueInfoText";
    L_0x0090:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        goto L_0x00b7;
    L_0x0098:
        r1 = org.telegram.ui.ActionBar.Theme.chat_locationTitlePaint;
        r2 = "chat_messageTextIn";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        r1 = org.telegram.ui.ActionBar.Theme.chat_locationAddressPaint;
        r2 = r23.isDrawSelectionBackground();
        if (r2 == 0) goto L_0x00ae;
    L_0x00ab:
        r2 = "chat_inVenueInfoSelectedText";
        goto L_0x00b0;
    L_0x00ae:
        r2 = "chat_inVenueInfoText";
    L_0x00b0:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
    L_0x00b7:
        r1 = r0.currentMessageObject;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
        if (r1 == 0) goto L_0x025c;
    L_0x00c1:
        r1 = r0.photoImage;
        r1 = r1.getImageY2();
        r2 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r10 = r1 + r2;
        r1 = r0.locationExpired;
        if (r1 != 0) goto L_0x0202;
    L_0x00d3:
        r0.forceNotDrawTime = r9;
        r1 = r0.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getCurrentTime();
        r2 = r0.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.date;
        r1 = r1 - r2;
        r1 = java.lang.Math.abs(r1);
        r1 = (float) r1;
        r2 = r0.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.media;
        r2 = r2.period;
        r2 = (float) r2;
        r1 = r1 / r2;
        r1 = r15 - r1;
        r2 = r0.rect;
        r4 = r0.photoImage;
        r4 = r4.getImageX2();
        r5 = NUM; // 0x422CLASSNAME float:43.0 double:5.485017196E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 - r5;
        r4 = (float) r4;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r5 = r10 - r5;
        r5 = (float) r5;
        r6 = r0.photoImage;
        r6 = r6.getImageX2();
        r9 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = r6 - r9;
        r6 = (float) r6;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r9 = r9 + r10;
        r9 = (float) r9;
        r2.set(r4, r5, r6, r9);
        r2 = r0.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x0144;
    L_0x012d:
        r2 = org.telegram.ui.ActionBar.Theme.chat_radialProgress2Paint;
        r4 = "chat_outInstant";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r2.setColor(r4);
        r2 = org.telegram.ui.ActionBar.Theme.chat_livePaint;
        r4 = "chat_outInstant";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r2.setColor(r4);
        goto L_0x015a;
    L_0x0144:
        r2 = org.telegram.ui.ActionBar.Theme.chat_radialProgress2Paint;
        r4 = "chat_inInstant";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r2.setColor(r4);
        r2 = org.telegram.ui.ActionBar.Theme.chat_livePaint;
        r4 = "chat_inInstant";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r2.setColor(r4);
    L_0x015a:
        r2 = org.telegram.ui.ActionBar.Theme.chat_radialProgress2Paint;
        r4 = 50;
        r2.setAlpha(r4);
        r2 = r0.rect;
        r2 = r2.centerX();
        r4 = r0.rect;
        r4 = r4.centerY();
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = (float) r3;
        r5 = org.telegram.ui.ActionBar.Theme.chat_radialProgress2Paint;
        r7.drawCircle(r2, r4, r3, r5);
        r2 = org.telegram.ui.ActionBar.Theme.chat_radialProgress2Paint;
        r2.setAlpha(r12);
        r2 = r0.rect;
        r3 = -NUM; // 0xffffffffc2b40000 float:-90.0 double:NaN;
        r4 = -NUM; // 0xffffffffc3b40000 float:-360.0 double:NaN;
        r4 = r4 * r1;
        r5 = 0;
        r6 = org.telegram.ui.ActionBar.Theme.chat_radialProgress2Paint;
        r1 = r24;
        r9 = 0;
        r1.drawArc(r2, r3, r4, r5, r6);
        r1 = r0.currentMessageObject;
        r1 = r1.messageOwner;
        r1 = r1.media;
        r1 = r1.period;
        r2 = r0.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getCurrentTime();
        r3 = r0.currentMessageObject;
        r3 = r3.messageOwner;
        r3 = r3.date;
        r2 = r2 - r3;
        r1 = r1 - r2;
        r1 = java.lang.Math.abs(r1);
        r1 = org.telegram.messenger.LocaleController.formatLocationLeftTime(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_livePaint;
        r2 = r2.measureText(r1);
        r3 = r0.rect;
        r3 = r3.centerX();
        r2 = r2 / r18;
        r3 = r3 - r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r10 = r10 + r2;
        r2 = (float) r10;
        r4 = org.telegram.ui.ActionBar.Theme.chat_livePaint;
        r7.drawText(r1, r3, r2, r4);
        r24.save();
        r1 = r0.photoImage;
        r1 = r1.getImageX();
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 + r2;
        r1 = (float) r1;
        r2 = r0.photoImage;
        r2 = r2.getImageY2();
        r3 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = r0.docTitleLayout;
        r1.draw(r7);
        r1 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r7.translate(r9, r1);
        r1 = r0.infoLayout;
        r1.draw(r7);
        r24.restore();
    L_0x0202:
        r1 = r0.photoImage;
        r1 = r1.getImageX();
        r2 = r0.photoImage;
        r2 = r2.getImageWidth();
        r2 = r2 / r8;
        r1 = r1 + r2;
        r2 = NUM; // 0x41var_ float:31.0 double:5.46818007E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 - r2;
        r2 = r0.photoImage;
        r2 = r2.getImageY();
        r3 = r0.photoImage;
        r3 = r3.getImageHeight();
        r3 = r3 / r8;
        r2 = r2 + r3;
        r3 = NUM; // 0x42180000 float:38.0 double:5.47854138E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = org.telegram.ui.ActionBar.Theme.chat_msgAvatarLiveLocationDrawable;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r3, r1, r2);
        r3 = org.telegram.ui.ActionBar.Theme.chat_msgAvatarLiveLocationDrawable;
        r3.draw(r7);
        r3 = r0.locationImageReceiver;
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r1 = r1 + r4;
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r2 = r2 + r4;
        r4 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r5 = NUM; // 0x42500000 float:52.0 double:5.496673668E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r3.setImageCoords(r1, r2, r4, r5);
        r1 = r0.locationImageReceiver;
        r1.draw(r7);
        goto L_0x0bf8;
    L_0x025c:
        r9 = 0;
        r24.save();
        r1 = r0.photoImage;
        r1 = r1.getImageX();
        r2 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r1 = r1 + r2;
        r1 = (float) r1;
        r2 = r0.photoImage;
        r2 = r2.getImageY2();
        r3 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = r0.docTitleLayout;
        r1.draw(r7);
        r1 = r0.infoLayout;
        if (r1 == 0) goto L_0x0295;
    L_0x0286:
        r1 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = (float) r1;
        r7.translate(r9, r1);
        r1 = r0.infoLayout;
        r1.draw(r7);
    L_0x0295:
        r24.restore();
        goto L_0x0bf8;
    L_0x029a:
        r8 = 0;
        r2 = 16;
        if (r5 != r2) goto L_0x03c6;
    L_0x029f:
        r1 = r1.isOutOwner();
        if (r1 == 0) goto L_0x02c5;
    L_0x02a5:
        r1 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r2 = "chat_messageTextOut";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        r1 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r2 = r23.isDrawSelectionBackground();
        if (r2 == 0) goto L_0x02bb;
    L_0x02b8:
        r2 = "chat_outTimeSelectedText";
        goto L_0x02bd;
    L_0x02bb:
        r2 = "chat_outTimeText";
    L_0x02bd:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        goto L_0x02e4;
    L_0x02c5:
        r1 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r2 = "chat_messageTextIn";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        r1 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r2 = r23.isDrawSelectionBackground();
        if (r2 == 0) goto L_0x02db;
    L_0x02d8:
        r2 = "chat_inTimeSelectedText";
        goto L_0x02dd;
    L_0x02db:
        r2 = "chat_inTimeText";
    L_0x02dd:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
    L_0x02e4:
        r0.forceNotDrawTime = r9;
        r1 = r0.currentMessageObject;
        r1 = r1.isOutOwner();
        if (r1 == 0) goto L_0x02fb;
    L_0x02ee:
        r1 = r0.layoutWidth;
        r2 = r0.backgroundWidth;
        r1 = r1 - r2;
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 + r2;
        goto L_0x0314;
    L_0x02fb:
        r1 = r0.isChat;
        if (r1 == 0) goto L_0x030e;
    L_0x02ff:
        r1 = r0.currentMessageObject;
        r1 = r1.needDrawAvatar();
        if (r1 == 0) goto L_0x030e;
    L_0x0307:
        r1 = NUM; // 0x42940000 float:74.0 double:5.518691446E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        goto L_0x0314;
    L_0x030e:
        r1 = NUM; // 0x41CLASSNAME float:25.0 double:5.45263811E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
    L_0x0314:
        r0.otherX = r1;
        r2 = r0.titleLayout;
        if (r2 == 0) goto L_0x0333;
    L_0x031a:
        r24.save();
        r2 = (float) r1;
        r3 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r0.namesOffset;
        r3 = r3 + r4;
        r3 = (float) r3;
        r7.translate(r2, r3);
        r2 = r0.titleLayout;
        r2.draw(r7);
        r24.restore();
    L_0x0333:
        r2 = r0.docTitleLayout;
        if (r2 == 0) goto L_0x0357;
    L_0x0337:
        r24.save();
        r2 = NUM; // 0x41980000 float:19.0 double:5.43709615E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r2 + r1;
        r2 = (float) r2;
        r3 = NUM; // 0x42140000 float:37.0 double:5.477246216E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r4 = r0.namesOffset;
        r3 = r3 + r4;
        r3 = (float) r3;
        r7.translate(r2, r3);
        r2 = r0.docTitleLayout;
        r2.draw(r7);
        r24.restore();
    L_0x0357:
        r2 = r0.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x0372;
    L_0x035f:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgCallUpGreenDrawable;
        r3 = r23.isDrawSelectionBackground();
        if (r3 != 0) goto L_0x036f;
    L_0x0367:
        r3 = r0.otherPressed;
        if (r3 == 0) goto L_0x036c;
    L_0x036b:
        goto L_0x036f;
    L_0x036c:
        r3 = org.telegram.ui.ActionBar.Theme.chat_msgOutCallDrawable;
        goto L_0x0398;
    L_0x036f:
        r3 = org.telegram.ui.ActionBar.Theme.chat_msgOutCallSelectedDrawable;
        goto L_0x0398;
    L_0x0372:
        r2 = r0.currentMessageObject;
        r2 = r2.messageOwner;
        r2 = r2.action;
        r2 = r2.reason;
        r3 = r2 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
        if (r3 != 0) goto L_0x0386;
    L_0x037e:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
        if (r2 == 0) goto L_0x0383;
    L_0x0382:
        goto L_0x0386;
    L_0x0383:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgCallDownGreenDrawable;
        goto L_0x0388;
    L_0x0386:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgCallDownRedDrawable;
    L_0x0388:
        r3 = r23.isDrawSelectionBackground();
        if (r3 != 0) goto L_0x0396;
    L_0x038e:
        r3 = r0.otherPressed;
        if (r3 == 0) goto L_0x0393;
    L_0x0392:
        goto L_0x0396;
    L_0x0393:
        r3 = org.telegram.ui.ActionBar.Theme.chat_msgInCallDrawable;
        goto L_0x0398;
    L_0x0396:
        r3 = org.telegram.ui.ActionBar.Theme.chat_msgInCallSelectedDrawable;
    L_0x0398:
        r4 = NUM; // 0x40400000 float:3.0 double:5.325712093E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r4 = r1 - r4;
        r5 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r6 = r0.namesOffset;
        r5 = r5 + r6;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r2, r4, r5);
        r2.draw(r7);
        r2 = NUM; // 0x434d0000 float:205.0 double:5.578592756E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 + r2;
        r2 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0.otherY = r2;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r3, r1, r2);
        r3.draw(r7);
        goto L_0x0bf8;
    L_0x03c6:
        r2 = 17;
        r16 = NUM; // 0x41100000 float:9.0 double:5.39306059E-315;
        if (r5 != r2) goto L_0x079b;
    L_0x03cc:
        r1 = r1.isOutOwner();
        if (r1 == 0) goto L_0x0401;
    L_0x03d2:
        r1 = "chat_messageTextOut";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r2.setColor(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioPerformerPaint;
        r2.setColor(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r2.setColor(r1);
        r1 = r23.isDrawSelectionBackground();
        if (r1 == 0) goto L_0x03f0;
    L_0x03ed:
        r1 = "chat_outTimeSelectedText";
        goto L_0x03f2;
    L_0x03f0:
        r1 = "chat_outTimeText";
    L_0x03f2:
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r2.setColor(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_livePaint;
        r2.setColor(r1);
        goto L_0x042f;
    L_0x0401:
        r1 = "chat_messageTextIn";
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioTitlePaint;
        r2.setColor(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_audioPerformerPaint;
        r2.setColor(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r2.setColor(r1);
        r1 = r23.isDrawSelectionBackground();
        if (r1 == 0) goto L_0x041f;
    L_0x041c:
        r1 = "chat_inTimeSelectedText";
        goto L_0x0421;
    L_0x041f:
        r1 = "chat_inTimeText";
    L_0x0421:
        r1 = org.telegram.ui.ActionBar.Theme.getColor(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_timePaint;
        r2.setColor(r1);
        r2 = org.telegram.ui.ActionBar.Theme.chat_livePaint;
        r2.setColor(r1);
    L_0x042f:
        r1 = r0.currentMessageObject;
        r1 = r1.isOutOwner();
        if (r1 == 0) goto L_0x0445;
    L_0x0437:
        r1 = r0.layoutWidth;
        r2 = r0.backgroundWidth;
        r1 = r1 - r2;
        r2 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 + r2;
    L_0x0443:
        r6 = r1;
        goto L_0x045f;
    L_0x0445:
        r1 = r0.isChat;
        if (r1 == 0) goto L_0x0458;
    L_0x0449:
        r1 = r0.currentMessageObject;
        r1 = r1.needDrawAvatar();
        if (r1 == 0) goto L_0x0458;
    L_0x0451:
        r1 = NUM; // 0x42880000 float:68.0 double:5.514805956E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        goto L_0x0443;
    L_0x0458:
        r1 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        goto L_0x0443;
    L_0x045f:
        r1 = r0.titleLayout;
        if (r1 == 0) goto L_0x047a;
    L_0x0463:
        r24.save();
        r1 = (float) r6;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = r0.namesOffset;
        r2 = r2 + r3;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = r0.titleLayout;
        r1.draw(r7);
        r24.restore();
    L_0x047a:
        r1 = r0.docTitleLayout;
        if (r1 == 0) goto L_0x04a5;
    L_0x047e:
        r24.save();
        r1 = r0.docTitleOffsetX;
        r1 = r1 + r6;
        r1 = (float) r1;
        r2 = r0.titleLayout;
        if (r2 == 0) goto L_0x048e;
    L_0x0489:
        r2 = r2.getHeight();
        goto L_0x048f;
    L_0x048e:
        r2 = 0;
    L_0x048f:
        r3 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r3 = r0.namesOffset;
        r2 = r2 + r3;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = r0.docTitleLayout;
        r1.draw(r7);
        r24.restore();
    L_0x04a5:
        r1 = android.os.Build.VERSION.SDK_INT;
        r2 = 21;
        if (r1 < r2) goto L_0x04b4;
    L_0x04ab:
        r1 = r0.selectorDrawable;
        if (r1 == 0) goto L_0x04b4;
    L_0x04af:
        r0.selectorDrawableMaskType = r9;
        r1.draw(r7);
    L_0x04b4:
        r1 = r0.pollButtons;
        r5 = r1.size();
        r4 = 0;
        r19 = 0;
    L_0x04bd:
        if (r4 >= r5) goto L_0x0774;
    L_0x04bf:
        r1 = r0.pollButtons;
        r1 = r1.get(r4);
        r3 = r1;
        r3 = (org.telegram.ui.Cells.ChatMessageCell.PollButton) r3;
        r3.x = r6;
        r24.save();
        r1 = NUM; // 0x42080000 float:34.0 double:5.473360725E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = r1 + r6;
        r1 = (float) r1;
        r2 = r3.y;
        r13 = r0.namesOffset;
        r2 = r2 + r13;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = r3.title;
        r1.draw(r7);
        r1 = r0.animatePollAnswerAlpha;
        if (r1 == 0) goto L_0x0502;
    L_0x04ec:
        r1 = r0.pollUnvoteInProgress;
        if (r1 == 0) goto L_0x04f5;
    L_0x04f0:
        r1 = r0.pollAnimationProgress;
        r1 = r15 - r1;
        goto L_0x04f7;
    L_0x04f5:
        r1 = r0.pollAnimationProgress;
    L_0x04f7:
        r2 = NUM; // 0x3e99999a float:0.3 double:5.188942835E-315;
        r1 = r1 / r2;
        r1 = java.lang.Math.min(r1, r15);
        r1 = r1 * r14;
        goto L_0x0504;
    L_0x0502:
        r1 = NUM; // 0x437var_ float:255.0 double:5.5947823E-315;
    L_0x0504:
        r13 = (int) r1;
        r1 = r0.pollVoted;
        if (r1 != 0) goto L_0x0511;
    L_0x0509:
        r1 = r0.pollClosed;
        if (r1 != 0) goto L_0x0511;
    L_0x050d:
        r1 = r0.animatePollAnswerAlpha;
        if (r1 == 0) goto L_0x05df;
    L_0x0511:
        r1 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r2 = r0.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x051e;
    L_0x051b:
        r2 = "chat_outAudioSeekbarFill";
        goto L_0x0520;
    L_0x051e:
        r2 = "chat_inAudioSeekbarFill";
    L_0x0520:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        r1 = r0.animatePollAnswerAlpha;
        if (r1 == 0) goto L_0x054c;
    L_0x052b:
        r1 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r1 = r1.getAlpha();
        r1 = (float) r1;
        r1 = r1 / r14;
        r2 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r15 = (float) r13;
        r1 = r1 * r15;
        r1 = (int) r1;
        r2.setAlpha(r1);
        r1 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r1 = r1.getAlpha();
        r1 = (float) r1;
        r1 = r1 / r14;
        r2 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r15 = r15 * r1;
        r1 = (int) r15;
        r2.setAlpha(r1);
    L_0x054c:
        r1 = r3.prevPercent;
        r1 = (float) r1;
        r2 = r3.percent;
        r15 = r3.prevPercent;
        r2 = r2 - r15;
        r2 = (float) r2;
        r15 = r0.pollAnimationProgress;
        r2 = r2 * r15;
        r1 = r1 + r2;
        r1 = (double) r1;
        r1 = java.lang.Math.ceil(r1);
        r1 = (int) r1;
        r2 = new java.lang.Object[r9];
        r1 = java.lang.Integer.valueOf(r1);
        r2[r10] = r1;
        r1 = "%d%%";
        r1 = java.lang.String.format(r1, r2);
        r2 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r2 = r2.measureText(r1);
        r9 = (double) r2;
        r9 = java.lang.Math.ceil(r9);
        r2 = (int) r9;
        r9 = NUM; // 0x40e00000 float:7.0 double:5.37751863E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = -r9;
        r9 = r9 - r2;
        r2 = (float) r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r9 = (float) r9;
        r10 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r7.drawText(r1, r2, r9, r10);
        r1 = r0.backgroundWidth;
        r2 = NUM; // 0x42980000 float:76.0 double:5.51998661E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r1 = r1 - r2;
        r2 = r3.prevPercentProgress;
        r9 = r3.percentProgress;
        r10 = r3.prevPercentProgress;
        r9 = r9 - r10;
        r10 = r0.pollAnimationProgress;
        r9 = r9 * r10;
        r2 = r2 + r9;
        r9 = r0.instantButtonRect;
        r10 = r3.height;
        r20 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r10 = r10 + r20;
        r10 = (float) r10;
        r1 = (float) r1;
        r1 = r1 * r2;
        r2 = r3.height;
        r20 = NUM; // 0x41300000 float:11.0 double:5.4034219E-315;
        r20 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r2 = r2 + r20;
        r2 = (float) r2;
        r9.set(r8, r10, r1, r2);
        r1 = r0.instantButtonRect;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r2 = (float) r2;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r9 = (float) r9;
        r10 = org.telegram.ui.ActionBar.Theme.chat_docBackPaint;
        r7.drawRoundRect(r1, r2, r9, r10);
    L_0x05df:
        r1 = r0.pollVoted;
        if (r1 != 0) goto L_0x05e7;
    L_0x05e3:
        r1 = r0.pollClosed;
        if (r1 == 0) goto L_0x05eb;
    L_0x05e7:
        r1 = r0.animatePollAnswerAlpha;
        if (r1 == 0) goto L_0x074c;
    L_0x05eb:
        r1 = r23.isDrawSelectionBackground();
        if (r1 == 0) goto L_0x0608;
    L_0x05f1:
        r1 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r2 = r0.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x05fe;
    L_0x05fb:
        r2 = "chat_outVoiceSeekbarSelected";
        goto L_0x0600;
    L_0x05fe:
        r2 = "chat_inVoiceSeekbarSelected";
    L_0x0600:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        goto L_0x061e;
    L_0x0608:
        r1 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r2 = r0.currentMessageObject;
        r2 = r2.isOutOwner();
        if (r2 == 0) goto L_0x0615;
    L_0x0612:
        r2 = "chat_outVoiceSeekbar";
        goto L_0x0617;
    L_0x0615:
        r2 = "chat_inVoiceSeekbar";
    L_0x0617:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
    L_0x061e:
        r1 = r0.animatePollAnswerAlpha;
        if (r1 == 0) goto L_0x0635;
    L_0x0622:
        r1 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r1 = r1.getAlpha();
        r1 = (float) r1;
        r1 = r1 / r14;
        r2 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r9 = 255 - r13;
        r9 = (float) r9;
        r9 = r9 * r1;
        r1 = (int) r9;
        r2.setAlpha(r1);
    L_0x0635:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = -r1;
        r2 = (float) r1;
        r1 = r3.height;
        r9 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r1 = r1 + r9;
        r9 = (float) r1;
        r1 = r0.backgroundWidth;
        r10 = NUM; // 0x42600000 float:56.0 double:5.50185432E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r1 = r1 - r10;
        r10 = (float) r1;
        r1 = r3.height;
        r20 = NUM; // 0x41500000 float:13.0 double:5.413783207E-315;
        r20 = org.telegram.messenger.AndroidUtilities.dp(r20);
        r1 = r1 + r20;
        r1 = (float) r1;
        r20 = org.telegram.ui.ActionBar.Theme.chat_replyLinePaint;
        r21 = r1;
        r1 = r24;
        r22 = r3;
        r3 = r9;
        r9 = r4;
        r4 = r10;
        r10 = r5;
        r5 = r21;
        r21 = r6;
        r6 = r20;
        r1.drawLine(r2, r3, r4, r5, r6);
        r1 = r0.pollVoteInProgress;
        r2 = NUM; // 0x41080000 float:8.5 double:5.390470265E-315;
        if (r1 == 0) goto L_0x06ec;
    L_0x0679:
        r1 = r0.pollVoteInProgressNum;
        if (r9 != r1) goto L_0x06ec;
    L_0x067d:
        r1 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r3 = r0.currentMessageObject;
        r3 = r3.isOutOwner();
        if (r3 == 0) goto L_0x068a;
    L_0x0687:
        r3 = "chat_outAudioSeekbarFill";
        goto L_0x068c;
    L_0x068a:
        r3 = "chat_inAudioSeekbarFill";
    L_0x068c:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r1.setColor(r3);
        r1 = r0.animatePollAnswerAlpha;
        if (r1 == 0) goto L_0x06aa;
    L_0x0697:
        r1 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r1 = r1.getAlpha();
        r1 = (float) r1;
        r1 = r1 / r14;
        r3 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r4 = 255 - r13;
        r4 = (float) r4;
        r4 = r4 * r1;
        r1 = (int) r4;
        r3.setAlpha(r1);
    L_0x06aa:
        r1 = r0.instantButtonRect;
        r3 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r3 = -r3;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r3 - r4;
        r3 = (float) r3;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r4 = r4 - r5;
        r4 = (float) r4;
        r5 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r5 = -r5;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r5 = r5 + r6;
        r5 = (float) r5;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r6 = r6 + r2;
        r2 = (float) r6;
        r1.set(r3, r4, r5, r2);
        r2 = r0.instantButtonRect;
        r3 = r0.voteRadOffset;
        r4 = r0.voteCurrentCircleLength;
        r5 = 0;
        r6 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r1 = r24;
        r1.drawArc(r2, r3, r4, r5, r6);
        goto L_0x0752;
    L_0x06ec:
        r1 = r0.currentMessageObject;
        r1 = r1.isOutOwner();
        if (r1 == 0) goto L_0x0709;
    L_0x06f4:
        r1 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r3 = r23.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x06ff;
    L_0x06fc:
        r3 = "chat_outMenuSelected";
        goto L_0x0701;
    L_0x06ff:
        r3 = "chat_outMenu";
    L_0x0701:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r1.setColor(r3);
        goto L_0x071d;
    L_0x0709:
        r1 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r3 = r23.isDrawSelectionBackground();
        if (r3 == 0) goto L_0x0714;
    L_0x0711:
        r3 = "chat_inMenuSelected";
        goto L_0x0716;
    L_0x0714:
        r3 = "chat_inMenu";
    L_0x0716:
        r3 = org.telegram.ui.ActionBar.Theme.getColor(r3);
        r1.setColor(r3);
    L_0x071d:
        r1 = r0.animatePollAnswerAlpha;
        if (r1 == 0) goto L_0x0734;
    L_0x0721:
        r1 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r1 = r1.getAlpha();
        r1 = (float) r1;
        r1 = r1 / r14;
        r3 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r4 = 255 - r13;
        r4 = (float) r4;
        r4 = r4 * r1;
        r1 = (int) r4;
        r3.setAlpha(r1);
    L_0x0734:
        r1 = NUM; // 0x41b80000 float:23.0 double:5.447457457E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r1 = -r1;
        r1 = (float) r1;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r3 = (float) r3;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = (float) r2;
        r4 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r7.drawCircle(r1, r3, r2, r4);
        goto L_0x0752;
    L_0x074c:
        r22 = r3;
        r9 = r4;
        r10 = r5;
        r21 = r6;
    L_0x0752:
        r24.restore();
        r5 = r10 + -1;
        if (r9 != r5) goto L_0x0767;
    L_0x0759:
        r1 = r22.y;
        r2 = r0.namesOffset;
        r1 = r1 + r2;
        r2 = r22.height;
        r1 = r1 + r2;
        r19 = r1;
    L_0x0767:
        r4 = r9 + 1;
        r5 = r10;
        r6 = r21;
        r9 = 1;
        r10 = 0;
        r13 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r15 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x04bd;
    L_0x0774:
        r21 = r6;
        r1 = r0.infoLayout;
        if (r1 == 0) goto L_0x0796;
    L_0x077a:
        r24.save();
        r1 = r0.infoX;
        r6 = r21 + r1;
        r1 = (float) r6;
        r2 = NUM; // 0x41b00000 float:22.0 double:5.44486713E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r2 = r19 + r2;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = r0.infoLayout;
        r1.draw(r7);
        r24.restore();
    L_0x0796:
        r23.updatePollAnimations();
        goto L_0x0bf8;
    L_0x079b:
        r2 = 12;
        if (r5 != r2) goto L_0x0bf8;
    L_0x079f:
        r1 = r1.isOutOwner();
        if (r1 == 0) goto L_0x07c5;
    L_0x07a5:
        r1 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
        r2 = "chat_outContactNameText";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        r1 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r2 = r23.isDrawSelectionBackground();
        if (r2 == 0) goto L_0x07bb;
    L_0x07b8:
        r2 = "chat_outContactPhoneSelectedText";
        goto L_0x07bd;
    L_0x07bb:
        r2 = "chat_outContactPhoneText";
    L_0x07bd:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        goto L_0x07e4;
    L_0x07c5:
        r1 = org.telegram.ui.ActionBar.Theme.chat_contactNamePaint;
        r2 = "chat_inContactNameText";
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
        r1 = org.telegram.ui.ActionBar.Theme.chat_contactPhonePaint;
        r2 = r23.isDrawSelectionBackground();
        if (r2 == 0) goto L_0x07db;
    L_0x07d8:
        r2 = "chat_inContactPhoneSelectedText";
        goto L_0x07dd;
    L_0x07db:
        r2 = "chat_inContactPhoneText";
    L_0x07dd:
        r2 = org.telegram.ui.ActionBar.Theme.getColor(r2);
        r1.setColor(r2);
    L_0x07e4:
        r1 = r0.titleLayout;
        if (r1 == 0) goto L_0x0813;
    L_0x07e8:
        r24.save();
        r1 = r0.photoImage;
        r1 = r1.getImageX();
        r2 = r0.photoImage;
        r2 = r2.getImageWidth();
        r1 = r1 + r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1 = r1 + r2;
        r1 = (float) r1;
        r2 = NUM; // 0x41800000 float:16.0 double:5.42932517E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r0.namesOffset;
        r2 = r2 + r3;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = r0.titleLayout;
        r1.draw(r7);
        r24.restore();
    L_0x0813:
        r1 = r0.docTitleLayout;
        if (r1 == 0) goto L_0x0842;
    L_0x0817:
        r24.save();
        r1 = r0.photoImage;
        r1 = r1.getImageX();
        r2 = r0.photoImage;
        r2 = r2.getImageWidth();
        r1 = r1 + r2;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r1 = r1 + r2;
        r1 = (float) r1;
        r2 = NUM; // 0x421CLASSNAME float:39.0 double:5.479836543E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r0.namesOffset;
        r2 = r2 + r3;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = r0.docTitleLayout;
        r1.draw(r7);
        r24.restore();
    L_0x0842:
        r1 = r0.currentMessageObject;
        r1 = r1.isOutOwner();
        if (r1 == 0) goto L_0x0856;
    L_0x084a:
        r1 = r23.isDrawSelectionBackground();
        if (r1 == 0) goto L_0x0853;
    L_0x0850:
        r1 = org.telegram.ui.ActionBar.Theme.chat_msgOutMenuSelectedDrawable;
        goto L_0x0861;
    L_0x0853:
        r1 = org.telegram.ui.ActionBar.Theme.chat_msgOutMenuDrawable;
        goto L_0x0861;
    L_0x0856:
        r1 = r23.isDrawSelectionBackground();
        if (r1 == 0) goto L_0x085f;
    L_0x085c:
        r1 = org.telegram.ui.ActionBar.Theme.chat_msgInMenuSelectedDrawable;
        goto L_0x0861;
    L_0x085f:
        r1 = org.telegram.ui.ActionBar.Theme.chat_msgInMenuDrawable;
    L_0x0861:
        r2 = r0.photoImage;
        r2 = r2.getImageX();
        r3 = r0.backgroundWidth;
        r2 = r2 + r3;
        r3 = NUM; // 0x42400000 float:48.0 double:5.491493014E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r0.otherX = r2;
        r3 = r0.photoImage;
        r3 = r3.getImageY();
        r4 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r0.otherY = r3;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r1, r2, r3);
        r1.draw(r7);
        r1 = r0.drawInstantView;
        if (r1 == 0) goto L_0x0bf8;
    L_0x088c:
        r1 = r0.photoImage;
        r1 = r1.getImageX();
        r2 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r1 = r1 - r2;
        r2 = r23.getMeasuredHeight();
        r3 = NUM; // 0x42800000 float:64.0 double:5.51221563E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 - r3;
        r3 = org.telegram.ui.ActionBar.Theme.chat_instantViewRectPaint;
        r4 = r0.currentMessageObject;
        r4 = r4.isOutOwner();
        if (r4 == 0) goto L_0x08c1;
    L_0x08ac:
        r4 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r5 = "chat_outPreviewInstantText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setColor(r5);
        r4 = "chat_outPreviewInstantText";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setColor(r4);
        goto L_0x08d5;
    L_0x08c1:
        r4 = org.telegram.ui.ActionBar.Theme.chat_instantViewPaint;
        r5 = "chat_inPreviewInstantText";
        r5 = org.telegram.ui.ActionBar.Theme.getColor(r5);
        r4.setColor(r5);
        r4 = "chat_inPreviewInstantText";
        r4 = org.telegram.ui.ActionBar.Theme.getColor(r4);
        r3.setColor(r4);
    L_0x08d5:
        r4 = android.os.Build.VERSION.SDK_INT;
        r5 = 21;
        if (r4 < r5) goto L_0x08f2;
    L_0x08db:
        r4 = 0;
        r0.selectorDrawableMaskType = r4;
        r4 = r0.selectorDrawable;
        r5 = r0.instantWidth;
        r5 = r5 + r1;
        r6 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = r6 + r2;
        r4.setBounds(r1, r2, r5, r6);
        r4 = r0.selectorDrawable;
        r4.draw(r7);
    L_0x08f2:
        r4 = r0.instantButtonRect;
        r5 = (float) r1;
        r6 = (float) r2;
        r8 = r0.instantWidth;
        r8 = r8 + r1;
        r8 = (float) r8;
        r9 = NUM; // 0x42100000 float:36.0 double:5.47595105E-315;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r9 = r9 + r2;
        r9 = (float) r9;
        r4.set(r5, r6, r8, r9);
        r4 = r0.instantButtonRect;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r5 = (float) r5;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r17);
        r6 = (float) r6;
        r7.drawRoundRect(r4, r5, r6, r3);
        r3 = r0.instantViewLayout;
        if (r3 == 0) goto L_0x0bf8;
    L_0x0918:
        r24.save();
        r3 = r0.instantTextX;
        r1 = r1 + r3;
        r1 = (float) r1;
        r3 = NUM; // 0x41280000 float:10.5 double:5.40083157E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r3);
        r2 = r2 + r3;
        r2 = (float) r2;
        r7.translate(r1, r2);
        r1 = r0.instantViewLayout;
        r1.draw(r7);
        r24.restore();
        goto L_0x0bf8;
    L_0x0934:
        r1 = 0;
        r5 = r0.photoImage;
        r5 = r5.getVisible();
        if (r5 == 0) goto L_0x0bf8;
    L_0x093d:
        r5 = r0.currentMessageObject;
        r5 = r5.needDrawBluredPreview();
        if (r5 != 0) goto L_0x09ab;
    L_0x0945:
        r5 = r0.documentAttachType;
        if (r5 != r2) goto L_0x09ab;
    L_0x0949:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r2 = (android.graphics.drawable.BitmapDrawable) r2;
        r2 = r2.getPaint();
        r2 = r2.getAlpha();
        r5 = r0.drawPhotoCheckBox;
        if (r5 == 0) goto L_0x096d;
    L_0x0959:
        r5 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r6 = (float) r2;
        r9 = r0.controlsAlpha;
        r6 = r6 * r9;
        r9 = r0.checkBoxAnimationProgress;
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r9 = r10 - r9;
        r6 = r6 * r9;
        r6 = (int) r6;
        r5.setAlpha(r6);
        goto L_0x0978;
    L_0x096d:
        r5 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r6 = (float) r2;
        r9 = r0.controlsAlpha;
        r6 = r6 * r9;
        r6 = (int) r6;
        r5.setAlpha(r6);
    L_0x0978:
        r5 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r6 = r0.photoImage;
        r6 = r6.getImageX();
        r9 = r0.photoImage;
        r9 = r9.getImageWidth();
        r6 = r6 + r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r6 = r6 - r9;
        r0.otherX = r6;
        r9 = r0.photoImage;
        r9 = r9.getImageY();
        r10 = NUM; // 0x4101999a float:8.1 double:5.388398005E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r9 = r9 + r10;
        r0.otherY = r9;
        org.telegram.ui.Cells.BaseCell.setDrawableBounds(r5, r6, r9);
        r5 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r5.draw(r7);
        r5 = org.telegram.ui.ActionBar.Theme.chat_msgMediaMenuDrawable;
        r5.setAlpha(r2);
    L_0x09ab:
        r2 = org.telegram.messenger.MediaController.getInstance();
        r5 = r0.currentMessageObject;
        r2 = r2.isPlayingMessage(r5);
        r5 = r0.animatingNoSoundPlaying;
        if (r5 == r2) goto L_0x09ca;
    L_0x09b9:
        r0.animatingNoSoundPlaying = r2;
        if (r2 == 0) goto L_0x09bf;
    L_0x09bd:
        r5 = 1;
        goto L_0x09c0;
    L_0x09bf:
        r5 = 2;
    L_0x09c0:
        r0.animatingNoSound = r5;
        if (r2 == 0) goto L_0x09c7;
    L_0x09c4:
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        goto L_0x09c8;
    L_0x09c7:
        r5 = 0;
    L_0x09c8:
        r0.animatingNoSoundProgress = r5;
    L_0x09ca:
        r5 = r0.buttonState;
        r6 = 1;
        if (r5 == r6) goto L_0x09e1;
    L_0x09cf:
        if (r5 == r8) goto L_0x09e1;
    L_0x09d1:
        if (r5 == 0) goto L_0x09e1;
    L_0x09d3:
        r6 = 3;
        if (r5 == r6) goto L_0x09e1;
    L_0x09d6:
        r6 = -1;
        if (r5 == r6) goto L_0x09e1;
    L_0x09d9:
        r5 = r0.currentMessageObject;
        r5 = r5.needDrawBluredPreview();
        if (r5 == 0) goto L_0x0b88;
    L_0x09e1:
        r5 = r0.autoPlayingMedia;
        if (r5 == 0) goto L_0x09e8;
    L_0x09e5:
        r23.updatePlayingMessageProgress();
    L_0x09e8:
        r5 = r0.infoLayout;
        if (r5 == 0) goto L_0x0b88;
    L_0x09ec:
        r5 = r0.forceNotDrawTime;
        if (r5 == 0) goto L_0x09f8;
    L_0x09f0:
        r5 = r0.autoPlayingMedia;
        if (r5 != 0) goto L_0x09f8;
    L_0x09f4:
        r5 = r0.drawVideoImageButton;
        if (r5 == 0) goto L_0x0b88;
    L_0x09f8:
        r5 = r0.currentMessageObject;
        r5 = r5.needDrawBluredPreview();
        if (r5 == 0) goto L_0x0a06;
    L_0x0a00:
        r5 = r0.docTitleLayout;
        if (r5 != 0) goto L_0x0a06;
    L_0x0a04:
        r6 = 0;
        goto L_0x0a08;
    L_0x0a06:
        r6 = r0.animatingDrawVideoImageButtonProgress;
    L_0x0a08:
        r5 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
        r9 = "chat_mediaInfoText";
        r9 = org.telegram.ui.ActionBar.Theme.getColor(r9);
        r5.setColor(r9);
        r5 = r0.photoImage;
        r5 = r5.getImageX();
        r9 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r5 = r5 + r10;
        r10 = r0.photoImage;
        r10 = r10.getImageY();
        r13 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r10 = r10 + r13;
        r9 = r0.autoPlayingMedia;
        if (r9 == 0) goto L_0x0a49;
    L_0x0a2f:
        if (r2 == 0) goto L_0x0a35;
    L_0x0a31:
        r2 = r0.animatingNoSound;
        if (r2 == 0) goto L_0x0a49;
    L_0x0a35:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgNoSoundDrawable;
        r2 = r2.getIntrinsicWidth();
        r9 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r2 = r2 + r13;
        r2 = (float) r2;
        r9 = r0.animatingNoSoundProgress;
        r2 = r2 * r9;
        r2 = (int) r2;
        goto L_0x0a4a;
    L_0x0a49:
        r2 = 0;
    L_0x0a4a:
        r9 = r0.infoWidth;
        r13 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r13);
        r9 = r9 + r13;
        r9 = r9 + r2;
        r9 = (float) r9;
        r13 = r0.infoWidth;
        r13 = r13 + r2;
        r15 = r0.docTitleWidth;
        r13 = java.lang.Math.max(r13, r15);
        r15 = r0.canStreamVideo;
        if (r15 == 0) goto L_0x0a69;
    L_0x0a62:
        r15 = NUM; // 0x42000000 float:32.0 double:5.4707704E-315;
        r15 = org.telegram.messenger.AndroidUtilities.dp(r15);
        goto L_0x0a6a;
    L_0x0a69:
        r15 = 0;
    L_0x0a6a:
        r13 = r13 + r15;
        r15 = r0.infoWidth;
        r13 = r13 - r15;
        r13 = r13 - r2;
        r13 = (float) r13;
        r13 = r13 * r6;
        r9 = r9 + r13;
        r12 = (double) r9;
        r12 = java.lang.Math.ceil(r12);
        r9 = (int) r12;
        r12 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1));
        if (r12 == 0) goto L_0x0a82;
    L_0x0a7d:
        r12 = r0.docTitleLayout;
        if (r12 != 0) goto L_0x0a82;
    L_0x0a81:
        r6 = 0;
    L_0x0a82:
        r12 = r0.rect;
        r13 = (float) r5;
        r15 = (float) r10;
        r5 = r5 + r9;
        r5 = (float) r5;
        r9 = NUM; // 0x41840000 float:16.5 double:5.43062033E-315;
        r18 = NUM; // 0x41780000 float:15.5 double:5.42673484E-315;
        r18 = r18 * r6;
        r18 = r18 + r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r18);
        r10 = r10 + r9;
        r9 = (float) r10;
        r12.set(r13, r15, r5, r9);
        r5 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint;
        r5 = r5.getAlpha();
        r9 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint;
        r10 = (float) r5;
        r12 = r0.controlsAlpha;
        r10 = r10 * r12;
        r10 = (int) r10;
        r9.setAlpha(r10);
        r9 = r0.rect;
        r10 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r12 = (float) r12;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r10 = (float) r13;
        r13 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint;
        r7.drawRoundRect(r9, r12, r10, r13);
        r9 = org.telegram.ui.ActionBar.Theme.chat_timeBackgroundPaint;
        r9.setAlpha(r5);
        r5 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
        r9 = r0.controlsAlpha;
        r9 = r9 * r14;
        r9 = (int) r9;
        r5.setAlpha(r9);
        r24.save();
        r5 = r0.photoImage;
        r5 = r5.getImageX();
        r9 = NUM; // 0x41000000 float:8.0 double:5.38787994E-315;
        r10 = r0.canStreamVideo;
        if (r10 == 0) goto L_0x0ae0;
    L_0x0adb:
        r10 = NUM; // 0x41var_ float:30.0 double:5.465589745E-315;
        r10 = r10 * r6;
        goto L_0x0ae1;
    L_0x0ae0:
        r10 = 0;
    L_0x0ae1:
        r10 = r10 + r9;
        r9 = org.telegram.messenger.AndroidUtilities.dp(r10);
        r5 = r5 + r9;
        r0.noSoundCenterX = r5;
        r5 = (float) r5;
        r9 = r0.photoImage;
        r9 = r9.getImageY();
        r10 = NUM; // 0x40b00000 float:5.5 double:5.36197667E-315;
        r12 = NUM; // 0x3e4ccccd float:0.2 double:5.164075695E-315;
        r12 = r12 * r6;
        r12 = r12 + r10;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r12);
        r9 = r9 + r10;
        r9 = (float) r9;
        r7.translate(r5, r9);
        r5 = r0.infoLayout;
        if (r5 == 0) goto L_0x0b08;
    L_0x0b05:
        r5.draw(r7);
    L_0x0b08:
        r5 = (r6 > r1 ? 1 : (r6 == r1 ? 0 : -1));
        if (r5 <= 0) goto L_0x0b34;
    L_0x0b0c:
        r5 = r0.docTitleLayout;
        if (r5 == 0) goto L_0x0b34;
    L_0x0b10:
        r24.save();
        r5 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
        r9 = r0.controlsAlpha;
        r9 = r9 * r14;
        r9 = r9 * r6;
        r9 = (int) r9;
        r5.setAlpha(r9);
        r5 = NUM; // 0x4164cccd float:14.3 double:5.42051806E-315;
        r6 = r6 * r5;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = (float) r5;
        r7.translate(r1, r5);
        r5 = r0.docTitleLayout;
        r5.draw(r7);
        r24.restore();
    L_0x0b34:
        if (r2 == 0) goto L_0x0b7e;
    L_0x0b36:
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgNoSoundDrawable;
        r5 = r0.animatingNoSoundProgress;
        r14 = r14 * r5;
        r14 = r14 * r5;
        r5 = r0.controlsAlpha;
        r14 = r14 * r5;
        r5 = (int) r14;
        r2.setAlpha(r5);
        r2 = r0.infoWidth;
        r5 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r2 = r2 + r6;
        r2 = (float) r2;
        r7.translate(r2, r1);
        r2 = r0.animatingNoSoundProgress;
        r2 = r2 * r11;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r5 = org.telegram.messenger.AndroidUtilities.dp(r11);
        r5 = r5 - r2;
        r5 = r5 / r8;
        r6 = org.telegram.ui.ActionBar.Theme.chat_msgNoSoundDrawable;
        r9 = r5 + r2;
        r10 = 0;
        r6.setBounds(r10, r5, r2, r9);
        r5 = org.telegram.ui.ActionBar.Theme.chat_msgNoSoundDrawable;
        r5.draw(r7);
        r5 = r0.noSoundCenterX;
        r6 = r0.infoWidth;
        r9 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r6 = r6 + r10;
        r2 = r2 / r8;
        r6 = r6 + r2;
        r5 = r5 + r6;
        r0.noSoundCenterX = r5;
    L_0x0b7e:
        r24.restore();
        r2 = org.telegram.ui.ActionBar.Theme.chat_infoPaint;
        r5 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        r2.setAlpha(r5);
    L_0x0b88:
        r2 = r0.animatingDrawVideoImageButton;
        r5 = 1;
        if (r2 != r5) goto L_0x0ba5;
    L_0x0b8d:
        r2 = r0.animatingDrawVideoImageButtonProgress;
        r5 = (float) r3;
        r6 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r5 = r5 / r6;
        r2 = r2 - r5;
        r0.animatingDrawVideoImageButtonProgress = r2;
        r2 = r0.animatingDrawVideoImageButtonProgress;
        r2 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1));
        if (r2 > 0) goto L_0x0ba1;
    L_0x0b9c:
        r0.animatingDrawVideoImageButtonProgress = r1;
        r2 = 0;
        r0.animatingDrawVideoImageButton = r2;
    L_0x0ba1:
        r23.invalidate();
        goto L_0x0bc0;
    L_0x0ba5:
        if (r2 != r8) goto L_0x0bc0;
    L_0x0ba7:
        r2 = r0.animatingDrawVideoImageButtonProgress;
        r5 = (float) r3;
        r6 = NUM; // 0x43200000 float:160.0 double:5.564022167E-315;
        r5 = r5 / r6;
        r2 = r2 + r5;
        r0.animatingDrawVideoImageButtonProgress = r2;
        r2 = r0.animatingDrawVideoImageButtonProgress;
        r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r2 > r5 ? 1 : (r2 == r5 ? 0 : -1));
        if (r2 < 0) goto L_0x0bbd;
    L_0x0bb8:
        r0.animatingDrawVideoImageButtonProgress = r5;
        r2 = 0;
        r0.animatingDrawVideoImageButton = r2;
    L_0x0bbd:
        r23.invalidate();
    L_0x0bc0:
        r2 = r0.animatingNoSound;
        r5 = 1;
        if (r2 != r5) goto L_0x0bdd;
    L_0x0bc5:
        r2 = r0.animatingNoSoundProgress;
        r3 = (float) r3;
        r4 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r3 = r3 / r4;
        r2 = r2 - r3;
        r0.animatingNoSoundProgress = r2;
        r2 = r0.animatingNoSoundProgress;
        r2 = (r2 > r1 ? 1 : (r2 == r1 ? 0 : -1));
        if (r2 > 0) goto L_0x0bd9;
    L_0x0bd4:
        r0.animatingNoSoundProgress = r1;
        r1 = 0;
        r0.animatingNoSound = r1;
    L_0x0bd9:
        r23.invalidate();
        goto L_0x0bf8;
    L_0x0bdd:
        if (r2 != r8) goto L_0x0bf8;
    L_0x0bdf:
        r1 = r0.animatingNoSoundProgress;
        r2 = (float) r3;
        r3 = NUM; // 0x43340000 float:180.0 double:5.570497984E-315;
        r2 = r2 / r3;
        r1 = r1 + r2;
        r0.animatingNoSoundProgress = r1;
        r1 = r0.animatingNoSoundProgress;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r1 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r1 < 0) goto L_0x0bf5;
    L_0x0bf0:
        r0.animatingNoSoundProgress = r2;
        r1 = 0;
        r0.animatingNoSound = r1;
    L_0x0bf5:
        r23.invalidate();
    L_0x0bf8:
        r1 = r0.drawImageButton;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0bfc:
        r1 = r0.photoImage;
        r1 = r1.getVisible();
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r0.controlsAlpha;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r3 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r3 == 0) goto L_0x0CLASSNAME;
    L_0x0c0c:
        r2 = r0.radialProgress;
        r2.setOverrideAlpha(r1);
    L_0x0CLASSNAME:
        r1 = r0.radialProgress;
        r1.draw(r7);
    L_0x0CLASSNAME:
        r1 = r0.drawVideoImageButton;
        if (r1 != 0) goto L_0x0c1e;
    L_0x0c1a:
        r1 = r0.animatingDrawVideoImageButton;
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0c1e:
        r1 = r0.photoImage;
        r1 = r1.getVisible();
        if (r1 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r1 = r0.controlsAlpha;
        r2 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r2 = (r1 > r2 ? 1 : (r1 == r2 ? 0 : -1));
        if (r2 == 0) goto L_0x0CLASSNAME;
    L_0x0c2e:
        r2 = r0.videoRadialProgress;
        r2.setOverrideAlpha(r1);
    L_0x0CLASSNAME:
        r1 = r0.videoRadialProgress;
        r1.draw(r7);
    L_0x0CLASSNAME:
        r1 = r0.drawPhotoCheckBox;
        if (r1 == 0) goto L_0x0c7a;
    L_0x0c3c:
        r1 = NUM; // 0x41a80000 float:21.0 double:5.442276803E-315;
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r2 = r0.photoCheckBox;
        r3 = 0;
        r4 = 0;
        r5 = r0.currentMessageObject;
        r5 = r5.isOutOwner();
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0c4e:
        r5 = "chat_outBubbleSelected";
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r5 = "chat_inBubbleSelected";
    L_0x0CLASSNAME:
        r2.setColor(r3, r4, r5);
        r2 = r0.photoCheckBox;
        r3 = r0.photoImage;
        r3 = r3.getImageX2();
        r4 = NUM; // 0x41CLASSNAME float:25.0 double:5.45263811E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r3 = r3 - r4;
        r4 = r0.photoImage;
        r4 = r4.getImageY();
        r5 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r5 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r4 = r4 + r5;
        r2.setBounds(r3, r4, r1, r1);
        r1 = r0.photoCheckBox;
        r1.draw(r7);
    L_0x0c7a:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawOverlays(android.graphics.Canvas):void");
    }

    public int getObserverTag() {
        return this.TAG;
    }

    public MessageObject getMessageObject() {
        MessageObject messageObject = this.messageObjectToSet;
        return messageObject != null ? messageObject : this.currentMessageObject;
    }

    public Document getStreamingMedia() {
        int i = this.documentAttachType;
        return (i == 4 || i == 7 || i == 2) ? this.documentAttach : null;
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

    public boolean performAccessibilityAction(int i, Bundle bundle) {
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
        } else if (i == NUM) {
            ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
            if (chatMessageCellDelegate != null) {
                if (this.currentMessageObject.type == 16) {
                    chatMessageCellDelegate.didLongPress(this, 0.0f, 0.0f);
                } else {
                    chatMessageCellDelegate.didPressOther(this, (float) this.otherX, (float) this.otherY);
                }
            }
        }
        return super.performAccessibilityAction(i, bundle);
    }

    public boolean onHoverEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int i = 0;
        if (motionEvent.getAction() == 9 || motionEvent.getAction() == 7) {
            while (i < this.accessibilityVirtualViewBounds.size()) {
                if (((Rect) this.accessibilityVirtualViewBounds.valueAt(i)).contains(x, y)) {
                    int keyAt = this.accessibilityVirtualViewBounds.keyAt(i);
                    if (keyAt != this.currentFocusedVirtualView) {
                        this.currentFocusedVirtualView = keyAt;
                        sendAccessibilityEventForVirtualView(keyAt, 32768);
                    }
                    return true;
                }
                i++;
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
        return new MessageAccessibilityNodeProvider(this, null);
    }

    private void sendAccessibilityEventForVirtualView(int i, int i2) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(i2);
            obtain.setPackageName(getContext().getPackageName());
            obtain.setSource(this, i);
            getParent().requestSendAccessibilityEvent(this, obtain);
        }
    }
}
