package org.telegram.messenger;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Base64;
import androidx.collection.LongSparseArray;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatInvite;
import org.telegram.tgnet.TLRPC$ChatReactions;
import org.telegram.tgnet.TLRPC$DecryptedMessageAction;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageExtendedMedia;
import org.telegram.tgnet.TLRPC$MessageFwdHeader;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$MessagePeerReaction;
import org.telegram.tgnet.TLRPC$MessageReplies;
import org.telegram.tgnet.TLRPC$PageBlock;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$PollResults;
import org.telegram.tgnet.TLRPC$Reaction;
import org.telegram.tgnet.TLRPC$ReactionCount;
import org.telegram.tgnet.TLRPC$StickerSet;
import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_chatReactionsAll;
import org.telegram.tgnet.TLRPC$TL_chatReactionsSome;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_documentAttributeHasStickers;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_documentEmpty;
import org.telegram.tgnet.TLRPC$TL_documentEncrypted;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_game;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest;
import org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp;
import org.telegram.tgnet.TLRPC$TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC$TL_messageActionGiftPremium;
import org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC$TL_messageEmpty;
import org.telegram.tgnet.TLRPC$TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC$TL_messageEntityCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC$TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_messageExtendedMedia;
import org.telegram.tgnet.TLRPC$TL_messageExtendedMediaPreview;
import org.telegram.tgnet.TLRPC$TL_messageForwarded_old2;
import org.telegram.tgnet.TLRPC$TL_messageMediaContact;
import org.telegram.tgnet.TLRPC$TL_messageMediaDice;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument_layer68;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument_layer74;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument_old;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaGame;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_layer68;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_layer74;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_old;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messagePeerReaction;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_message_secret;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.tgnet.TLRPC$TL_pageBlockPhoto;
import org.telegram.tgnet.TLRPC$TL_pageBlockVideo;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoCachedSize;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC$TL_photoStrippedSize;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.tgnet.TLRPC$TL_reactionCustomEmoji;
import org.telegram.tgnet.TLRPC$TL_reactionEmoji;
import org.telegram.tgnet.TLRPC$TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC$TL_stickerSetFullCovered;
import org.telegram.tgnet.TLRPC$TL_webPage;
import org.telegram.tgnet.TLRPC$TL_webPageAttributeTheme;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.Reactions.ReactionsUtils;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;
import org.telegram.ui.Components.spoilers.SpoilerEffect;

public class MessageObject {
    private static final int LINES_PER_BLOCK = 10;
    private static final int LINES_PER_BLOCK_WITH_EMOJI = 5;
    public static final int MESSAGE_SEND_STATE_EDITING = 3;
    public static final int MESSAGE_SEND_STATE_SENDING = 1;
    public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
    public static final int MESSAGE_SEND_STATE_SENT = 0;
    public static final int POSITION_FLAG_BOTTOM = 8;
    public static final int POSITION_FLAG_LEFT = 1;
    public static final int POSITION_FLAG_RIGHT = 2;
    public static final int POSITION_FLAG_TOP = 4;
    public static final int TYPE_ANIMATED_STICKER = 15;
    public static final int TYPE_EMOJIS = 19;
    public static final int TYPE_EXTENDED_MEDIA_PREVIEW = 20;
    public static final int TYPE_GEO = 4;
    public static final int TYPE_GIFT_PREMIUM = 18;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_POLL = 17;
    public static final int TYPE_ROUND_VIDEO = 5;
    public static final int TYPE_STICKER = 13;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_VOICE = 2;
    static final String[] excludeWords = {" vs. ", " vs ", " versus ", " ft. ", " ft ", " featuring ", " feat. ", " feat ", " presents ", " pres. ", " pres ", " and ", " & ", " . "};
    public static Pattern instagramUrlPattern;
    public static Pattern urlPattern;
    public static Pattern videoTimeUrlPattern;
    public boolean animateComments;
    public boolean attachPathExists;
    public int audioPlayerDuration;
    public float audioProgress;
    public int audioProgressMs;
    public int audioProgressSec;
    public StringBuilder botButtonsLayout;
    public String botStartParam;
    public float bufferedProgress;
    public Boolean cachedIsSupergroup;
    public boolean cancelEditing;
    public CharSequence caption;
    public ArrayList<TLRPC$TL_pollAnswer> checkedVotes;
    public int contentType;
    public int currentAccount;
    public TLRPC$TL_channelAdminLogEvent currentEvent;
    public Drawable customAvatarDrawable;
    public String customName;
    public String customReplyName;
    public String dateKey;
    public boolean deleted;
    public CharSequence editingMessage;
    public ArrayList<TLRPC$MessageEntity> editingMessageEntities;
    public boolean editingMessageSearchWebPage;
    public TLRPC$Document emojiAnimatedSticker;
    public String emojiAnimatedStickerColor;
    public Long emojiAnimatedStickerId;
    private boolean emojiAnimatedStickerLoading;
    private int emojiOnlyCount;
    public long eventId;
    public long extendedMediaLastCheckTime;
    public boolean forcePlayEffect;
    public float forceSeekTo;
    public boolean forceUpdate;
    private float generatedWithDensity;
    private int generatedWithMinSize;
    public float gifState;
    public boolean hadAnimationNotReadyLoading;
    public boolean hasRtl;
    private boolean hasUnwrappedEmoji;
    public boolean hideSendersName;
    public ArrayList<String> highlightedWords;
    public boolean isDateObject;
    public boolean isDownloadingFile;
    public boolean isReactionPush;
    public boolean isRestrictedMessage;
    private int isRoundVideoCached;
    public boolean isSpoilersRevealed;
    public int lastLineWidth;
    private boolean layoutCreated;
    public int linesCount;
    public CharSequence linkDescription;
    public long loadedFileSize;
    public boolean loadingCancelled;
    public boolean localChannel;
    public boolean localEdit;
    public long localGroupId;
    public String localName;
    public long localSentGroupId;
    public boolean localSupergroup;
    public int localType;
    public String localUserName;
    public boolean mediaExists;
    public ImageLocation mediaSmallThumb;
    public ImageLocation mediaThumb;
    public TLRPC$Message messageOwner;
    public CharSequence messageText;
    public String messageTrimmedToHighlight;
    public String monthKey;
    public int parentWidth;
    public SvgHelper.SvgDrawable pathThumb;
    public ArrayList<TLRPC$PhotoSize> photoThumbs;
    public ArrayList<TLRPC$PhotoSize> photoThumbs2;
    public TLObject photoThumbsObject;
    public TLObject photoThumbsObject2;
    public boolean playedGiftAnimation;
    public long pollLastCheckTime;
    public boolean pollVisibleOnScreen;
    public boolean preview;
    public String previousAttachPath;
    public TLRPC$MessageMedia previousMedia;
    public String previousMessage;
    public ArrayList<TLRPC$MessageEntity> previousMessageEntities;
    public boolean putInDownloadsStore;
    public boolean reactionsChanged;
    public long reactionsLastCheckTime;
    public MessageObject replyMessageObject;
    public boolean resendAsIs;
    public boolean scheduled;
    public SendAnimationData sendAnimationData;
    public TLRPC$Peer sendAsPeer;
    public boolean shouldRemoveVideoEditedInfo;
    public int sponsoredChannelPost;
    public TLRPC$ChatInvite sponsoredChatInvite;
    public String sponsoredChatInviteHash;
    public byte[] sponsoredId;
    public int stableId;
    public BitmapDrawable strippedThumb;
    public int textHeight;
    public ArrayList<TextLayoutBlock> textLayoutBlocks;
    public int textWidth;
    public float textXOffset;
    private int totalAnimatedEmojiCount;
    public int type;
    public boolean useCustomPhoto;
    public CharSequence vCardData;
    public VideoEditedInfo videoEditedInfo;
    public boolean viewsReloaded;
    public int wantedBotKeyboardWidth;
    public boolean wasJustSent;
    public boolean wasUnread;
    public ArrayList<TLRPC$MessageEntity> webPageDescriptionEntities;

    public static class SendAnimationData {
        public float currentScale;
        public float currentX;
        public float currentY;
        public float height;
        public float timeAlpha;
        public float width;
        public float x;
        public float y;
    }

    public void checkForScam() {
    }

    public static boolean hasUnreadReactions(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message == null) {
            return false;
        }
        return hasUnreadReactions(tLRPC$Message.reactions);
    }

    public static boolean hasUnreadReactions(TLRPC$TL_messageReactions tLRPC$TL_messageReactions) {
        if (tLRPC$TL_messageReactions == null) {
            return false;
        }
        for (int i = 0; i < tLRPC$TL_messageReactions.recent_reactions.size(); i++) {
            if (tLRPC$TL_messageReactions.recent_reactions.get(i).unread) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPremiumSticker(TLRPC$Document tLRPC$Document) {
        if (!(tLRPC$Document == null || tLRPC$Document.thumbs == null)) {
            for (int i = 0; i < tLRPC$Document.video_thumbs.size(); i++) {
                if ("f".equals(tLRPC$Document.video_thumbs.get(i).type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getEmojiOnlyCount() {
        return this.emojiOnlyCount;
    }

    public boolean shouldDrawReactionsInLayout() {
        if (getDialogId() < 0 || UserConfig.getInstance(this.currentAccount).isPremium()) {
            return true;
        }
        TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(getDialogId()));
        if (user == null || !user.premium) {
            return false;
        }
        return true;
    }

    public TLRPC$MessagePeerReaction getRandomUnreadReaction() {
        ArrayList<TLRPC$MessagePeerReaction> arrayList;
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions = this.messageOwner.reactions;
        if (tLRPC$TL_messageReactions == null || (arrayList = tLRPC$TL_messageReactions.recent_reactions) == null || arrayList.isEmpty()) {
            return null;
        }
        return this.messageOwner.reactions.recent_reactions.get(0);
    }

    public void markReactionsAsRead() {
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions = this.messageOwner.reactions;
        if (tLRPC$TL_messageReactions != null && tLRPC$TL_messageReactions.recent_reactions != null) {
            boolean z = false;
            for (int i = 0; i < this.messageOwner.reactions.recent_reactions.size(); i++) {
                if (this.messageOwner.reactions.recent_reactions.get(i).unread) {
                    this.messageOwner.reactions.recent_reactions.get(i).unread = false;
                    z = true;
                }
            }
            if (z) {
                MessagesStorage instance = MessagesStorage.getInstance(this.currentAccount);
                TLRPC$Message tLRPC$Message = this.messageOwner;
                instance.markMessageReactionsAsRead(tLRPC$Message.dialog_id, tLRPC$Message.id, true);
            }
        }
    }

    public boolean isPremiumSticker() {
        if (getMedia(this.messageOwner) == null || !getMedia(this.messageOwner).nopremium) {
            return isPremiumSticker(getDocument());
        }
        return false;
    }

    public TLRPC$VideoSize getPremiumStickerAnimation() {
        return getPremiumStickerAnimation(getDocument());
    }

    public static TLRPC$VideoSize getPremiumStickerAnimation(TLRPC$Document tLRPC$Document) {
        if (!(tLRPC$Document == null || tLRPC$Document.thumbs == null)) {
            for (int i = 0; i < tLRPC$Document.video_thumbs.size(); i++) {
                if ("f".equals(tLRPC$Document.video_thumbs.get(i).type)) {
                    return tLRPC$Document.video_thumbs.get(i);
                }
            }
        }
        return null;
    }

    public void copyStableParams(MessageObject messageObject) {
        ArrayList<TLRPC$ReactionCount> arrayList;
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions;
        this.stableId = messageObject.stableId;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        tLRPC$Message.premiumEffectWasPlayed = messageObject.messageOwner.premiumEffectWasPlayed;
        this.forcePlayEffect = messageObject.forcePlayEffect;
        this.wasJustSent = messageObject.wasJustSent;
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions2 = tLRPC$Message.reactions;
        if (tLRPC$TL_messageReactions2 != null && (arrayList = tLRPC$TL_messageReactions2.results) != null && !arrayList.isEmpty() && (tLRPC$TL_messageReactions = messageObject.messageOwner.reactions) != null && tLRPC$TL_messageReactions.results != null) {
            for (int i = 0; i < this.messageOwner.reactions.results.size(); i++) {
                TLRPC$ReactionCount tLRPC$ReactionCount = this.messageOwner.reactions.results.get(i);
                for (int i2 = 0; i2 < messageObject.messageOwner.reactions.results.size(); i2++) {
                    TLRPC$ReactionCount tLRPC$ReactionCount2 = messageObject.messageOwner.reactions.results.get(i2);
                    if (ReactionsLayoutInBubble.equalsTLReaction(tLRPC$ReactionCount.reaction, tLRPC$ReactionCount2.reaction)) {
                        tLRPC$ReactionCount.lastDrawnPosition = tLRPC$ReactionCount2.lastDrawnPosition;
                    }
                }
            }
        }
    }

    public ArrayList<ReactionsLayoutInBubble.VisibleReaction> getChoosenReactions() {
        ArrayList<ReactionsLayoutInBubble.VisibleReaction> arrayList = new ArrayList<>();
        for (int i = 0; i < this.messageOwner.reactions.results.size(); i++) {
            if (this.messageOwner.reactions.results.get(i).chosen) {
                arrayList.add(ReactionsLayoutInBubble.VisibleReaction.fromTLReaction(this.messageOwner.reactions.results.get(i).reaction));
            }
        }
        return arrayList;
    }

    public static class VCardData {
        private String company;
        private ArrayList<String> emails = new ArrayList<>();
        private ArrayList<String> phones = new ArrayList<>();

        public static CharSequence parse(String str) {
            int i;
            boolean z;
            VCardData vCardData;
            byte[] decodeQuotedPrintable;
            try {
                BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
                z = false;
                vCardData = null;
                String str2 = null;
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        break;
                    } else if (!readLine.startsWith("PHOTO")) {
                        if (readLine.indexOf(58) >= 0) {
                            if (readLine.startsWith("BEGIN:VCARD")) {
                                vCardData = new VCardData();
                            } else if (readLine.startsWith("END:VCARD") && vCardData != null) {
                                z = true;
                            }
                        }
                        if (str2 != null) {
                            readLine = str2 + readLine;
                            str2 = null;
                        }
                        if (readLine.contains("=QUOTED-PRINTABLE")) {
                            if (readLine.endsWith("=")) {
                                str2 = readLine.substring(0, readLine.length() - 1);
                            }
                        }
                        int indexOf = readLine.indexOf(":");
                        int i2 = 2;
                        String[] strArr = indexOf >= 0 ? new String[]{readLine.substring(0, indexOf), readLine.substring(indexOf + 1).trim()} : new String[]{readLine.trim()};
                        if (strArr.length >= 2) {
                            if (vCardData != null) {
                                if (strArr[0].startsWith("ORG")) {
                                    String[] split = strArr[0].split(";");
                                    int length = split.length;
                                    int i3 = 0;
                                    String str3 = null;
                                    String str4 = null;
                                    while (i3 < length) {
                                        String[] split2 = split[i3].split("=");
                                        if (split2.length == i2) {
                                            if (split2[0].equals("CHARSET")) {
                                                str4 = split2[1];
                                            } else if (split2[0].equals("ENCODING")) {
                                                str3 = split2[1];
                                            }
                                        }
                                        i3++;
                                        i2 = 2;
                                    }
                                    vCardData.company = strArr[1];
                                    if (!(str3 == null || !str3.equalsIgnoreCase("QUOTED-PRINTABLE") || (decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(vCardData.company))) == null || decodeQuotedPrintable.length == 0)) {
                                        vCardData.company = new String(decodeQuotedPrintable, str4);
                                    }
                                    vCardData.company = vCardData.company.replace(';', ' ');
                                } else if (strArr[0].startsWith("TEL")) {
                                    if (strArr[1].length() > 0) {
                                        vCardData.phones.add(strArr[1]);
                                    }
                                } else if (strArr[0].startsWith("EMAIL")) {
                                    String str5 = strArr[1];
                                    if (str5.length() > 0) {
                                        vCardData.emails.add(str5);
                                    }
                                }
                            }
                        }
                    }
                }
                bufferedReader.close();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            } catch (Throwable unused) {
                return null;
            }
            if (!z) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (int i4 = 0; i4 < vCardData.phones.size(); i4++) {
                if (sb.length() > 0) {
                    sb.append(10);
                }
                String str6 = vCardData.phones.get(i4);
                if (!str6.contains("#")) {
                    if (!str6.contains("*")) {
                        sb.append(PhoneFormat.getInstance().format(str6));
                    }
                }
                sb.append(str6);
            }
            for (i = 0; i < vCardData.emails.size(); i++) {
                if (sb.length() > 0) {
                    sb.append(10);
                }
                sb.append(PhoneFormat.getInstance().format(vCardData.emails.get(i)));
            }
            if (!TextUtils.isEmpty(vCardData.company)) {
                if (sb.length() > 0) {
                    sb.append(10);
                }
                sb.append(vCardData.company);
            }
            return sb;
        }
    }

    public static class TextLayoutBlock {
        public static final int FLAG_NOT_RTL = 2;
        public static final int FLAG_RTL = 1;
        public int charactersEnd;
        public int charactersOffset;
        public byte directionFlags;
        public int height;
        public int heightByOffset;
        public List<SpoilerEffect> spoilers = new ArrayList();
        public AtomicReference<Layout> spoilersPatchedTextLayout = new AtomicReference<>();
        public StaticLayout textLayout;
        public float textYOffset;

        public boolean isRtl() {
            byte b = this.directionFlags;
            return (b & 1) != 0 && (b & 2) == 0;
        }
    }

    public static class GroupedMessagePosition {
        public float aspectRatio;
        public boolean edge;
        public int flags;
        public boolean last;
        public float left;
        public int leftSpanOffset;
        public byte maxX;
        public byte maxY;
        public byte minX;
        public byte minY;
        public float ph;
        public int pw;
        public float[] siblingHeights;
        public int spanSize;
        public float top;

        public void set(int i, int i2, int i3, int i4, int i5, float f, int i6) {
            this.minX = (byte) i;
            this.maxX = (byte) i2;
            this.minY = (byte) i3;
            this.maxY = (byte) i4;
            this.pw = i5;
            this.spanSize = i5;
            this.ph = f;
            this.flags = (byte) i6;
        }
    }

    public static class GroupedMessages {
        public long groupId;
        public boolean hasCaption;
        public boolean hasSibling;
        public boolean isDocuments;
        private int maxSizeWidth = 800;
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public ArrayList<GroupedMessagePosition> posArray = new ArrayList<>();
        public HashMap<MessageObject, GroupedMessagePosition> positions = new HashMap<>();
        public final TransitionParams transitionParams = new TransitionParams();

        private static class MessageGroupedLayoutAttempt {
            public float[] heights;
            public int[] lineCounts;

            public MessageGroupedLayoutAttempt(int i, int i2, float f, float f2) {
                this.lineCounts = new int[]{i, i2};
                this.heights = new float[]{f, f2};
            }

            public MessageGroupedLayoutAttempt(int i, int i2, int i3, float f, float f2, float f3) {
                this.lineCounts = new int[]{i, i2, i3};
                this.heights = new float[]{f, f2, f3};
            }

            public MessageGroupedLayoutAttempt(int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
                this.lineCounts = new int[]{i, i2, i3, i4};
                this.heights = new float[]{f, f2, f3, f4};
            }
        }

        private float multiHeight(float[] fArr, int i, int i2) {
            float f = 0.0f;
            while (i < i2) {
                f += fArr[i];
                i++;
            }
            return ((float) this.maxSizeWidth) / f;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v35, resolved type: int} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v36, resolved type: byte} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v113, resolved type: byte} */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0072, code lost:
            if ((org.telegram.messenger.MessageObject.getMedia(r13.messageOwner) instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice) == false) goto L_0x0076;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:88:0x0219  */
        /* JADX WARNING: Removed duplicated region for block: B:92:0x0279  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void calculate() {
            /*
                r39 = this;
                r0 = r39
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r0.posArray
                r1.clear()
                java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r0.positions
                r1.clear()
                r1 = 800(0x320, float:1.121E-42)
                r0.maxSizeWidth = r1
                java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.messages
                int r1 = r1.size()
                r2 = 1
                if (r1 > r2) goto L_0x001a
                return
            L_0x001a:
                r3 = 1145798656(0x444b8000, float:814.0)
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r5 = 0
                r0.hasSibling = r5
                r0.hasCaption = r5
                r7 = 0
                r8 = 0
                r9 = 1065353216(0x3var_, float:1.0)
                r10 = 0
                r11 = 0
            L_0x002d:
                r12 = 1067030938(0x3var_a, float:1.2)
                if (r7 >= r1) goto L_0x00e7
                java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.messages
                java.lang.Object r13 = r13.get(r7)
                org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
                if (r7 != 0) goto L_0x0085
                boolean r11 = r13.isOutOwner()
                if (r11 != 0) goto L_0x0076
                org.telegram.tgnet.TLRPC$Message r8 = r13.messageOwner
                org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r8.fwd_from
                if (r14 == 0) goto L_0x004c
                org.telegram.tgnet.TLRPC$Peer r14 = r14.saved_from_peer
                if (r14 != 0) goto L_0x0074
            L_0x004c:
                org.telegram.tgnet.TLRPC$Peer r14 = r8.from_id
                boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
                if (r14 == 0) goto L_0x0076
                org.telegram.tgnet.TLRPC$Peer r14 = r8.peer_id
                long r5 = r14.channel_id
                r17 = 0
                int r19 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
                if (r19 != 0) goto L_0x0074
                long r5 = r14.chat_id
                int r14 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
                if (r14 != 0) goto L_0x0074
                org.telegram.tgnet.TLRPC$MessageMedia r5 = org.telegram.messenger.MessageObject.getMedia(r8)
                boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r5 != 0) goto L_0x0074
                org.telegram.tgnet.TLRPC$Message r5 = r13.messageOwner
                org.telegram.tgnet.TLRPC$MessageMedia r5 = org.telegram.messenger.MessageObject.getMedia(r5)
                boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
                if (r5 == 0) goto L_0x0076
            L_0x0074:
                r8 = 1
                goto L_0x0077
            L_0x0076:
                r8 = 0
            L_0x0077:
                boolean r5 = r13.isMusic()
                if (r5 != 0) goto L_0x0083
                boolean r5 = r13.isDocument()
                if (r5 == 0) goto L_0x0085
            L_0x0083:
                r0.isDocuments = r2
            L_0x0085:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r13.photoThumbs
                int r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
                org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r6 = new org.telegram.messenger.MessageObject$GroupedMessagePosition
                r6.<init>()
                int r14 = r1 + -1
                if (r7 != r14) goto L_0x009a
                r14 = 1
                goto L_0x009b
            L_0x009a:
                r14 = 0
            L_0x009b:
                r6.last = r14
                if (r5 != 0) goto L_0x00a2
                r5 = 1065353216(0x3var_, float:1.0)
                goto L_0x00aa
            L_0x00a2:
                int r14 = r5.w
                float r14 = (float) r14
                int r5 = r5.h
                float r5 = (float) r5
                float r5 = r14 / r5
            L_0x00aa:
                r6.aspectRatio = r5
                int r12 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r12 <= 0) goto L_0x00b6
                java.lang.String r5 = "w"
                r4.append(r5)
                goto L_0x00c8
            L_0x00b6:
                r12 = 1061997773(0x3f4ccccd, float:0.8)
                int r5 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r5 >= 0) goto L_0x00c3
                java.lang.String r5 = "n"
                r4.append(r5)
                goto L_0x00c8
            L_0x00c3:
                java.lang.String r5 = "q"
                r4.append(r5)
            L_0x00c8:
                float r5 = r6.aspectRatio
                float r9 = r9 + r5
                r12 = 1073741824(0x40000000, float:2.0)
                int r5 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r5 <= 0) goto L_0x00d2
                r10 = 1
            L_0x00d2:
                java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r0.positions
                r5.put(r13, r6)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r0.posArray
                r5.add(r6)
                java.lang.CharSequence r5 = r13.caption
                if (r5 == 0) goto L_0x00e2
                r0.hasCaption = r2
            L_0x00e2:
                int r7 = r7 + 1
                r5 = 0
                goto L_0x002d
            L_0x00e7:
                boolean r5 = r0.isDocuments
                r6 = 1120403456(0x42CLASSNAME, float:100.0)
                r7 = 1000(0x3e8, float:1.401E-42)
                r13 = 3
                if (r5 == 0) goto L_0x012d
                r3 = 0
            L_0x00f1:
                if (r3 >= r1) goto L_0x012c
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r0.posArray
                java.lang.Object r4 = r4.get(r3)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4
                int r5 = r4.flags
                r5 = r5 | r13
                r4.flags = r5
                if (r3 != 0) goto L_0x0107
                r5 = r5 | 4
                r4.flags = r5
                goto L_0x0111
            L_0x0107:
                int r8 = r1 + -1
                if (r3 != r8) goto L_0x0111
                r5 = r5 | 8
                r4.flags = r5
                r4.last = r2
            L_0x0111:
                r4.edge = r2
                r5 = 1065353216(0x3var_, float:1.0)
                r4.aspectRatio = r5
                r5 = 0
                r4.minX = r5
                r4.maxX = r5
                byte r5 = (byte) r3
                r4.minY = r5
                r4.maxY = r5
                r4.spanSize = r7
                int r5 = r0.maxSizeWidth
                r4.pw = r5
                r4.ph = r6
                int r3 = r3 + 1
                goto L_0x00f1
            L_0x012c:
                return
            L_0x012d:
                if (r8 == 0) goto L_0x0138
                int r5 = r0.maxSizeWidth
                int r5 = r5 + -50
                r0.maxSizeWidth = r5
                r5 = 250(0xfa, float:3.5E-43)
                goto L_0x013a
            L_0x0138:
                r5 = 200(0xc8, float:2.8E-43)
            L_0x013a:
                r8 = 1123024896(0x42var_, float:120.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r8)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r8
                android.graphics.Point r15 = org.telegram.messenger.AndroidUtilities.displaySize
                int r7 = r15.x
                int r15 = r15.y
                int r7 = java.lang.Math.min(r7, r15)
                float r7 = (float) r7
                int r15 = r0.maxSizeWidth
                float r15 = (float) r15
                float r7 = r7 / r15
                float r8 = r8 / r7
                int r7 = (int) r8
                r8 = 1109393408(0x42200000, float:40.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r8
                android.graphics.Point r15 = org.telegram.messenger.AndroidUtilities.displaySize
                int r12 = r15.x
                int r15 = r15.y
                int r12 = java.lang.Math.min(r12, r15)
                float r12 = (float) r12
                int r15 = r0.maxSizeWidth
                float r2 = (float) r15
                float r12 = r12 / r2
                float r8 = r8 / r12
                int r2 = (int) r8
                float r8 = (float) r15
                float r8 = r8 / r3
                float r12 = (float) r1
                float r9 = r9 / r12
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                float r6 = r6 / r3
                r12 = 4
                r15 = 2
                if (r10 != 0) goto L_0x057f
                if (r1 == r15) goto L_0x0182
                if (r1 == r13) goto L_0x0182
                if (r1 != r12) goto L_0x057f
            L_0x0182:
                r10 = 1137410048(0x43cb8000, float:407.0)
                r12 = 1053609165(0x3ecccccd, float:0.4)
                if (r1 != r15) goto L_0x02b6
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r0.posArray
                r6 = 0
                java.lang.Object r2 = r2.get(r6)
                r6 = 2
                org.telegram.messenger.MessageObject$GroupedMessagePosition r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r13 = r0.posArray
                r14 = 1
                java.lang.Object r13 = r13.get(r14)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r13 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r13
                java.lang.String r4 = r4.toString()
                java.lang.String r14 = "ww"
                boolean r17 = r4.equals(r14)
                if (r17 == 0) goto L_0x0208
                r17 = r7
                double r6 = (double) r9
                r21 = 4608983858650965606(0x3ffNUM, double:1.4)
                double r8 = (double) r8
                java.lang.Double.isNaN(r8)
                double r8 = r8 * r21
                int r21 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r21 <= 0) goto L_0x020a
                float r6 = r2.aspectRatio
                float r7 = r13.aspectRatio
                float r8 = r6 - r7
                double r8 = (double) r8
                r21 = 4596373779694328218(0x3fCLASSNAMEa, double:0.2)
                int r23 = (r8 > r21 ? 1 : (r8 == r21 ? 0 : -1))
                if (r23 >= 0) goto L_0x020a
                int r4 = r0.maxSizeWidth
                float r8 = (float) r4
                float r8 = r8 / r6
                float r4 = (float) r4
                float r4 = r4 / r7
                float r4 = java.lang.Math.min(r4, r10)
                float r4 = java.lang.Math.min(r8, r4)
                int r4 = java.lang.Math.round(r4)
                float r4 = (float) r4
                float r3 = r4 / r3
                r22 = 0
                r23 = 0
                r24 = 0
                r25 = 0
                int r4 = r0.maxSizeWidth
                r28 = 7
                r21 = r2
                r26 = r4
                r27 = r3
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r24 = 1
                r25 = 1
                int r2 = r0.maxSizeWidth
                r28 = 11
                r21 = r13
                r26 = r2
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r2 = 0
            L_0x0205:
                r6 = 2
                goto L_0x02b3
            L_0x0208:
                r17 = r7
            L_0x020a:
                boolean r6 = r4.equals(r14)
                if (r6 != 0) goto L_0x0279
                java.lang.String r6 = "qq"
                boolean r4 = r4.equals(r6)
                if (r4 == 0) goto L_0x0219
                goto L_0x0279
            L_0x0219:
                int r4 = r0.maxSizeWidth
                float r6 = (float) r4
                float r6 = r6 * r12
                float r4 = (float) r4
                float r7 = r2.aspectRatio
                float r4 = r4 / r7
                r8 = 1065353216(0x3var_, float:1.0)
                float r7 = r8 / r7
                float r9 = r13.aspectRatio
                float r8 = r8 / r9
                float r7 = r7 + r8
                float r4 = r4 / r7
                int r4 = java.lang.Math.round(r4)
                float r4 = (float) r4
                float r4 = java.lang.Math.max(r6, r4)
                int r4 = (int) r4
                int r6 = r0.maxSizeWidth
                int r6 = r6 - r4
                r7 = r17
                if (r6 >= r7) goto L_0x0240
                int r6 = r7 - r6
                int r4 = r4 - r6
                r6 = r7
            L_0x0240:
                float r7 = (float) r6
                float r8 = r2.aspectRatio
                float r7 = r7 / r8
                float r8 = (float) r4
                float r9 = r13.aspectRatio
                float r8 = r8 / r9
                float r7 = java.lang.Math.min(r7, r8)
                int r7 = java.lang.Math.round(r7)
                float r7 = (float) r7
                float r7 = java.lang.Math.min(r3, r7)
                float r3 = r7 / r3
                r22 = 0
                r23 = 0
                r24 = 0
                r25 = 0
                r28 = 13
                r21 = r2
                r26 = r6
                r27 = r3
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r22 = 1
                r23 = 1
                r28 = 14
                r21 = r13
                r26 = r4
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r2 = 1
                goto L_0x0205
            L_0x0279:
                int r4 = r0.maxSizeWidth
                r6 = 2
                int r4 = r4 / r6
                float r7 = (float) r4
                float r8 = r2.aspectRatio
                float r8 = r7 / r8
                float r9 = r13.aspectRatio
                float r7 = r7 / r9
                float r7 = java.lang.Math.min(r7, r3)
                float r7 = java.lang.Math.min(r8, r7)
                int r7 = java.lang.Math.round(r7)
                float r7 = (float) r7
                float r3 = r7 / r3
                r22 = 0
                r23 = 0
                r24 = 0
                r25 = 0
                r28 = 13
                r21 = r2
                r26 = r4
                r27 = r3
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r22 = 1
                r23 = 1
                r28 = 14
                r21 = r13
                r21.set(r22, r23, r24, r25, r26, r27, r28)
            L_0x02b2:
                r2 = 1
            L_0x02b3:
                r14 = r2
                goto L_0x07a9
            L_0x02b6:
                r8 = 2
                r9 = 1141264221(0x44064f5d, float:537.24005)
                if (r1 != r13) goto L_0x03e0
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r12 = r0.posArray
                r13 = 0
                java.lang.Object r12 = r12.get(r13)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r12 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r12
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r14 = r0.posArray
                r15 = 1
                java.lang.Object r14 = r14.get(r15)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r14 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r14
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r15 = r0.posArray
                java.lang.Object r15 = r15.get(r8)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r15 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r15
                char r4 = r4.charAt(r13)
                r13 = r15
                r15 = 110(0x6e, float:1.54E-43)
                if (r4 != r15) goto L_0x037a
                float r4 = r14.aspectRatio
                int r6 = r0.maxSizeWidth
                float r6 = (float) r6
                float r6 = r6 * r4
                float r9 = r13.aspectRatio
                float r9 = r9 + r4
                float r6 = r6 / r9
                int r4 = java.lang.Math.round(r6)
                float r4 = (float) r4
                float r4 = java.lang.Math.min(r10, r4)
                float r6 = r3 - r4
                float r7 = (float) r7
                int r9 = r0.maxSizeWidth
                float r9 = (float) r9
                r10 = 1056964608(0x3var_, float:0.5)
                float r9 = r9 * r10
                float r10 = r13.aspectRatio
                float r10 = r10 * r4
                float r15 = r14.aspectRatio
                float r15 = r15 * r6
                float r10 = java.lang.Math.min(r10, r15)
                int r10 = java.lang.Math.round(r10)
                float r10 = (float) r10
                float r9 = java.lang.Math.min(r9, r10)
                float r7 = java.lang.Math.max(r7, r9)
                int r7 = (int) r7
                float r9 = r12.aspectRatio
                float r9 = r9 * r3
                float r2 = (float) r2
                float r9 = r9 + r2
                int r2 = r0.maxSizeWidth
                int r2 = r2 - r7
                float r2 = (float) r2
                float r2 = java.lang.Math.min(r9, r2)
                int r2 = java.lang.Math.round(r2)
                r22 = 0
                r23 = 0
                r24 = 0
                r25 = 1
                r27 = 1065353216(0x3var_, float:1.0)
                r28 = 13
                r21 = r12
                r26 = r2
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r22 = 1
                r23 = 1
                r25 = 0
                float r6 = r6 / r3
                r28 = 6
                r21 = r14
                r26 = r7
                r27 = r6
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r22 = 0
                r24 = 1
                r25 = 1
                float r4 = r4 / r3
                r28 = 10
                r21 = r13
                r27 = r4
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                int r3 = r0.maxSizeWidth
                r13.spanSize = r3
                float[] r9 = new float[r8]
                r10 = 0
                r9[r10] = r4
                r4 = 1
                r9[r4] = r6
                r12.siblingHeights = r9
                if (r11 == 0) goto L_0x0372
                int r3 = r3 - r7
                r12.spanSize = r3
                goto L_0x0377
            L_0x0372:
                int r3 = r3 - r2
                r14.spanSize = r3
                r13.leftSpanOffset = r2
            L_0x0377:
                r0.hasSibling = r4
                goto L_0x03dd
            L_0x037a:
                int r2 = r0.maxSizeWidth
                float r2 = (float) r2
                float r4 = r12.aspectRatio
                float r2 = r2 / r4
                float r2 = java.lang.Math.min(r2, r9)
                int r2 = java.lang.Math.round(r2)
                float r2 = (float) r2
                float r2 = r2 / r3
                r22 = 0
                r23 = 1
                r24 = 0
                r25 = 0
                int r4 = r0.maxSizeWidth
                r28 = 7
                r21 = r12
                r26 = r4
                r27 = r2
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                int r4 = r0.maxSizeWidth
                int r4 = r4 / r8
                float r2 = r3 - r2
                float r7 = (float) r4
                float r9 = r14.aspectRatio
                float r9 = r7 / r9
                float r10 = r13.aspectRatio
                float r7 = r7 / r10
                float r7 = java.lang.Math.min(r9, r7)
                int r7 = java.lang.Math.round(r7)
                float r7 = (float) r7
                float r2 = java.lang.Math.min(r2, r7)
                float r2 = r2 / r3
                int r3 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
                if (r3 >= 0) goto L_0x03bf
                r2 = r6
            L_0x03bf:
                r22 = 0
                r23 = 0
                r24 = 1
                r25 = 1
                r28 = 9
                r21 = r14
                r26 = r4
                r27 = r2
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r22 = 1
                r23 = 1
                r28 = 10
                r21 = r13
                r21.set(r22, r23, r24, r25, r26, r27, r28)
            L_0x03dd:
                r14 = 1
                goto L_0x07a9
            L_0x03e0:
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r10 = r0.posArray
                r15 = 0
                java.lang.Object r10 = r10.get(r15)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r10 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r10
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r12 = r0.posArray
                r3 = 1
                java.lang.Object r12 = r12.get(r3)
                r3 = r12
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r12 = r0.posArray
                java.lang.Object r12 = r12.get(r8)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r12 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r12
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r0.posArray
                java.lang.Object r8 = r8.get(r13)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                char r4 = r4.charAt(r15)
                r15 = 119(0x77, float:1.67E-43)
                r13 = 1051260355(0x3ea8f5c3, float:0.33)
                if (r4 != r15) goto L_0x04c8
                int r2 = r0.maxSizeWidth
                float r2 = (float) r2
                float r4 = r10.aspectRatio
                float r2 = r2 / r4
                float r2 = java.lang.Math.min(r2, r9)
                int r2 = java.lang.Math.round(r2)
                float r2 = (float) r2
                r4 = 1145798656(0x444b8000, float:814.0)
                float r2 = r2 / r4
                r22 = 0
                r23 = 2
                r24 = 0
                r25 = 0
                int r4 = r0.maxSizeWidth
                r28 = 7
                r21 = r10
                r26 = r4
                r27 = r2
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                int r4 = r0.maxSizeWidth
                float r4 = (float) r4
                float r9 = r3.aspectRatio
                float r10 = r12.aspectRatio
                float r9 = r9 + r10
                float r10 = r8.aspectRatio
                float r9 = r9 + r10
                float r4 = r4 / r9
                int r4 = java.lang.Math.round(r4)
                float r4 = (float) r4
                float r7 = (float) r7
                int r9 = r0.maxSizeWidth
                float r9 = (float) r9
                r10 = 1053609165(0x3ecccccd, float:0.4)
                float r9 = r9 * r10
                float r10 = r3.aspectRatio
                float r10 = r10 * r4
                float r9 = java.lang.Math.min(r9, r10)
                float r9 = java.lang.Math.max(r7, r9)
                int r9 = (int) r9
                int r10 = r0.maxSizeWidth
                float r10 = (float) r10
                float r10 = r10 * r13
                float r7 = java.lang.Math.max(r7, r10)
                float r10 = r8.aspectRatio
                float r10 = r10 * r4
                float r7 = java.lang.Math.max(r7, r10)
                int r7 = (int) r7
                int r10 = r0.maxSizeWidth
                int r10 = r10 - r9
                int r10 = r10 - r7
                r13 = 1114112000(0x42680000, float:58.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
                if (r10 >= r14) goto L_0x0489
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r14 = r14 - r10
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r13 = r14 / 2
                int r9 = r9 - r13
                int r14 = r14 - r13
                int r7 = r7 - r14
            L_0x0489:
                r26 = r9
                r9 = 1145798656(0x444b8000, float:814.0)
                float r2 = r9 - r2
                float r2 = java.lang.Math.min(r2, r4)
                float r2 = r2 / r9
                int r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
                if (r4 >= 0) goto L_0x049a
                r2 = r6
            L_0x049a:
                r22 = 0
                r23 = 0
                r24 = 1
                r25 = 1
                r28 = 9
                r21 = r3
                r27 = r2
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r22 = 1
                r23 = 1
                r28 = 8
                r21 = r12
                r26 = r10
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r22 = 2
                r23 = 2
                r28 = 10
                r21 = r8
                r26 = r7
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r2 = 2
                goto L_0x02b3
            L_0x04c8:
                float r4 = r3.aspectRatio
                r6 = 1065353216(0x3var_, float:1.0)
                float r4 = r6 / r4
                float r9 = r12.aspectRatio
                float r9 = r6 / r9
                float r4 = r4 + r9
                float r9 = r8.aspectRatio
                float r9 = r6 / r9
                float r4 = r4 + r9
                r6 = 1145798656(0x444b8000, float:814.0)
                float r4 = r6 / r4
                int r4 = java.lang.Math.round(r4)
                int r4 = java.lang.Math.max(r7, r4)
                float r7 = (float) r14
                float r9 = (float) r4
                float r14 = r3.aspectRatio
                float r14 = r9 / r14
                float r14 = java.lang.Math.max(r7, r14)
                float r14 = r14 / r6
                float r14 = java.lang.Math.min(r13, r14)
                float r15 = r12.aspectRatio
                float r9 = r9 / r15
                float r7 = java.lang.Math.max(r7, r9)
                float r7 = r7 / r6
                float r7 = java.lang.Math.min(r13, r7)
                r9 = 1065353216(0x3var_, float:1.0)
                float r9 = r9 - r14
                float r9 = r9 - r7
                float r13 = r10.aspectRatio
                float r6 = r6 * r13
                float r2 = (float) r2
                float r6 = r6 + r2
                int r2 = r0.maxSizeWidth
                int r2 = r2 - r4
                float r2 = (float) r2
                float r2 = java.lang.Math.min(r6, r2)
                int r2 = java.lang.Math.round(r2)
                r22 = 0
                r23 = 0
                r24 = 0
                r25 = 2
                float r6 = r14 + r7
                float r27 = r6 + r9
                r28 = 13
                r21 = r10
                r26 = r2
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r22 = 1
                r23 = 1
                r25 = 0
                r28 = 6
                r21 = r3
                r26 = r4
                r27 = r14
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                r22 = 0
                r24 = 1
                r25 = 1
                r28 = 2
                r21 = r12
                r27 = r7
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                int r6 = r0.maxSizeWidth
                r12.spanSize = r6
                r24 = 2
                r25 = 2
                r28 = 10
                r21 = r8
                r27 = r9
                r21.set(r22, r23, r24, r25, r26, r27, r28)
                int r6 = r0.maxSizeWidth
                r8.spanSize = r6
                if (r11 == 0) goto L_0x0566
                int r6 = r6 - r4
                r10.spanSize = r6
                goto L_0x056d
            L_0x0566:
                int r6 = r6 - r2
                r3.spanSize = r6
                r12.leftSpanOffset = r2
                r8.leftSpanOffset = r2
            L_0x056d:
                r2 = 3
                float[] r2 = new float[r2]
                r3 = 0
                r2[r3] = r14
                r3 = 1
                r2[r3] = r7
                r4 = 2
                r2[r4] = r9
                r10.siblingHeights = r2
                r0.hasSibling = r3
                goto L_0x02b2
            L_0x057f:
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r0.posArray
                int r2 = r2.size()
                float[] r3 = new float[r2]
                r4 = 0
            L_0x0588:
                if (r4 >= r1) goto L_0x05cb
                r8 = 1066192077(0x3f8ccccd, float:1.1)
                int r8 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
                if (r8 <= 0) goto L_0x05a4
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r0.posArray
                java.lang.Object r8 = r8.get(r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                float r8 = r8.aspectRatio
                r10 = 1065353216(0x3var_, float:1.0)
                float r8 = java.lang.Math.max(r10, r8)
                r3[r4] = r8
                goto L_0x05b6
            L_0x05a4:
                r10 = 1065353216(0x3var_, float:1.0)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r0.posArray
                java.lang.Object r8 = r8.get(r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                float r8 = r8.aspectRatio
                float r8 = java.lang.Math.min(r10, r8)
                r3[r4] = r8
            L_0x05b6:
                r8 = 1059760867(0x3f2aaae3, float:0.66667)
                r13 = 1071225242(0x3fd9999a, float:1.7)
                r14 = r3[r4]
                float r13 = java.lang.Math.min(r13, r14)
                float r8 = java.lang.Math.max(r8, r13)
                r3[r4] = r8
                int r4 = r4 + 1
                goto L_0x0588
            L_0x05cb:
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r8 = 1
            L_0x05d1:
                if (r8 >= r2) goto L_0x05f0
                int r10 = r2 - r8
                r13 = 3
                if (r8 > r13) goto L_0x05ed
                if (r10 <= r13) goto L_0x05db
                goto L_0x05ed
            L_0x05db:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r13 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r14 = 0
                float r15 = r0.multiHeight(r3, r14, r8)
                r14 = r15
                float r15 = r0.multiHeight(r3, r8, r2)
                r13.<init>(r8, r10, r14, r15)
                r4.add(r13)
            L_0x05ed:
                int r8 = r8 + 1
                goto L_0x05d1
            L_0x05f0:
                r8 = 1
            L_0x05f1:
                int r10 = r2 + -1
                if (r8 >= r10) goto L_0x0632
                r10 = 1
            L_0x05f6:
                int r13 = r2 - r8
                if (r10 >= r13) goto L_0x062f
                int r13 = r13 - r10
                r14 = 3
                if (r8 > r14) goto L_0x062c
                r15 = 1062836634(0x3var_a, float:0.85)
                int r15 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
                if (r15 >= 0) goto L_0x0607
                r15 = 4
                goto L_0x0608
            L_0x0607:
                r15 = 3
            L_0x0608:
                if (r10 > r15) goto L_0x062c
                if (r13 <= r14) goto L_0x060d
                goto L_0x062c
            L_0x060d:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r14 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r15 = 0
                float r25 = r0.multiHeight(r3, r15, r8)
                int r15 = r8 + r10
                float r26 = r0.multiHeight(r3, r8, r15)
                float r27 = r0.multiHeight(r3, r15, r2)
                r21 = r14
                r22 = r8
                r23 = r10
                r24 = r13
                r21.<init>(r22, r23, r24, r25, r26, r27)
                r4.add(r14)
            L_0x062c:
                int r10 = r10 + 1
                goto L_0x05f6
            L_0x062f:
                int r8 = r8 + 1
                goto L_0x05f1
            L_0x0632:
                r8 = 1
            L_0x0633:
                int r9 = r2 + -2
                if (r8 >= r9) goto L_0x0687
                r9 = 1
            L_0x0638:
                int r10 = r2 - r8
                if (r9 >= r10) goto L_0x0683
                r13 = 1
            L_0x063d:
                int r14 = r10 - r9
                if (r13 >= r14) goto L_0x067f
                int r14 = r14 - r13
                r15 = 3
                if (r8 > r15) goto L_0x0677
                if (r9 > r15) goto L_0x0677
                if (r13 > r15) goto L_0x0677
                if (r14 <= r15) goto L_0x064c
                goto L_0x0677
            L_0x064c:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r15 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r12 = 0
                float r35 = r0.multiHeight(r3, r12, r8)
                r12 = r15
                int r15 = r8 + r9
                float r36 = r0.multiHeight(r3, r8, r15)
                r21 = r10
                int r10 = r15 + r13
                float r37 = r0.multiHeight(r3, r15, r10)
                float r38 = r0.multiHeight(r3, r10, r2)
                r30 = r12
                r31 = r8
                r32 = r9
                r33 = r13
                r34 = r14
                r30.<init>(r31, r32, r33, r34, r35, r36, r37, r38)
                r4.add(r12)
                goto L_0x0679
            L_0x0677:
                r21 = r10
            L_0x0679:
                int r13 = r13 + 1
                r10 = r21
                r12 = 4
                goto L_0x063d
            L_0x067f:
                int r9 = r9 + 1
                r12 = 4
                goto L_0x0638
            L_0x0683:
                int r8 = r8 + 1
                r12 = 4
                goto L_0x0633
            L_0x0687:
                int r2 = r0.maxSizeWidth
                r8 = 3
                int r2 = r2 / r8
                r8 = 4
                int r2 = r2 * 4
                float r2 = (float) r2
                r12 = 0
                r13 = 0
                r14 = 0
            L_0x0692:
                int r15 = r4.size()
                if (r12 >= r15) goto L_0x0715
                java.lang.Object r15 = r4.get(r12)
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r15 = (org.telegram.messenger.MessageObject.GroupedMessages.MessageGroupedLayoutAttempt) r15
                r16 = 2139095039(0x7f7fffff, float:3.4028235E38)
                r8 = 0
                r22 = 0
            L_0x06a4:
                float[] r9 = r15.heights
                int r10 = r9.length
                if (r8 >= r10) goto L_0x06ba
                r10 = r9[r8]
                float r22 = r22 + r10
                r10 = r9[r8]
                int r10 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
                if (r10 >= 0) goto L_0x06b7
                r9 = r9[r8]
                r16 = r9
            L_0x06b7:
                int r8 = r8 + 1
                goto L_0x06a4
            L_0x06ba:
                float r22 = r22 - r2
                float r8 = java.lang.Math.abs(r22)
                int[] r9 = r15.lineCounts
                int r10 = r9.length
                r22 = r2
                r2 = 1
                r17 = r4
                if (r10 <= r2) goto L_0x06f7
                r10 = 0
                r4 = r9[r10]
                r10 = r9[r2]
                if (r4 > r10) goto L_0x06f0
                int r4 = r9.length
                r10 = 2
                if (r4 <= r10) goto L_0x06e1
                r4 = r9[r2]
                r2 = r9[r10]
                if (r4 > r2) goto L_0x06dc
                goto L_0x06e1
            L_0x06dc:
                r2 = 1067030938(0x3var_a, float:1.2)
                r4 = 3
                goto L_0x06f4
            L_0x06e1:
                int r2 = r9.length
                r4 = 3
                if (r2 <= r4) goto L_0x06ec
                r2 = r9[r10]
                r9 = r9[r4]
                if (r2 <= r9) goto L_0x06ec
                goto L_0x06f1
            L_0x06ec:
                r2 = 1067030938(0x3var_a, float:1.2)
                goto L_0x06fb
            L_0x06f0:
                r4 = 3
            L_0x06f1:
                r2 = 1067030938(0x3var_a, float:1.2)
            L_0x06f4:
                float r8 = r8 * r2
                goto L_0x06fb
            L_0x06f7:
                r2 = 1067030938(0x3var_a, float:1.2)
                r4 = 3
            L_0x06fb:
                float r9 = (float) r7
                int r9 = (r16 > r9 ? 1 : (r16 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x0704
                r9 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r8 = r8 * r9
            L_0x0704:
                if (r13 == 0) goto L_0x070a
                int r9 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
                if (r9 >= 0) goto L_0x070c
            L_0x070a:
                r14 = r8
                r13 = r15
            L_0x070c:
                int r12 = r12 + 1
                r4 = r17
                r2 = r22
                r8 = 4
                goto L_0x0692
            L_0x0715:
                if (r13 != 0) goto L_0x0718
                return
            L_0x0718:
                r2 = 0
                r4 = 0
                r7 = 0
            L_0x071b:
                int[] r8 = r13.lineCounts
                int r9 = r8.length
                if (r2 >= r9) goto L_0x07a8
                r8 = r8[r2]
                float[] r9 = r13.heights
                r9 = r9[r2]
                int r10 = r0.maxSizeWidth
                int r12 = r8 + -1
                int r4 = java.lang.Math.max(r4, r12)
                r14 = r10
                r15 = 0
                r10 = r7
                r7 = 0
            L_0x0732:
                if (r7 >= r8) goto L_0x0790
                r16 = r3[r10]
                r17 = r3
                float r3 = r16 * r9
                int r3 = (int) r3
                int r14 = r14 - r3
                r16 = r4
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r0.posArray
                java.lang.Object r4 = r4.get(r10)
                r29 = r4
                org.telegram.messenger.MessageObject$GroupedMessagePosition r29 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r29
                r19 = r8
                if (r2 != 0) goto L_0x074e
                r4 = 4
                goto L_0x074f
            L_0x074e:
                r4 = 0
            L_0x074f:
                int[] r8 = r13.lineCounts
                int r8 = r8.length
                r20 = 1
                int r8 = r8 + -1
                if (r2 != r8) goto L_0x075a
                r4 = r4 | 8
            L_0x075a:
                if (r7 != 0) goto L_0x0762
                r4 = r4 | 1
                if (r11 == 0) goto L_0x0762
                r15 = r29
            L_0x0762:
                if (r7 != r12) goto L_0x076d
                r4 = r4 | 2
                if (r11 != 0) goto L_0x076d
                r36 = r4
                r15 = r29
                goto L_0x076f
            L_0x076d:
                r36 = r4
            L_0x076f:
                r8 = 1145798656(0x444b8000, float:814.0)
                float r4 = r9 / r8
                float r35 = java.lang.Math.max(r6, r4)
                r30 = r7
                r31 = r7
                r32 = r2
                r33 = r2
                r34 = r3
                r29.set(r30, r31, r32, r33, r34, r35, r36)
                int r10 = r10 + 1
                int r7 = r7 + 1
                r4 = r16
                r3 = r17
                r8 = r19
                goto L_0x0732
            L_0x0790:
                r17 = r3
                r16 = r4
                r8 = 1145798656(0x444b8000, float:814.0)
                int r3 = r15.pw
                int r3 = r3 + r14
                r15.pw = r3
                int r3 = r15.spanSize
                int r3 = r3 + r14
                r15.spanSize = r3
                int r2 = r2 + 1
                r7 = r10
                r3 = r17
                goto L_0x071b
            L_0x07a8:
                r14 = r4
            L_0x07a9:
                r2 = 0
            L_0x07aa:
                if (r2 >= r1) goto L_0x0826
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r0.posArray
                java.lang.Object r3 = r3.get(r2)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                if (r11 == 0) goto L_0x07ca
                byte r4 = r3.minX
                if (r4 != 0) goto L_0x07bf
                int r4 = r3.spanSize
                int r4 = r4 + r5
                r3.spanSize = r4
            L_0x07bf:
                int r4 = r3.flags
                r6 = 2
                r4 = r4 & r6
                if (r4 == 0) goto L_0x07c8
                r4 = 1
                r3.edge = r4
            L_0x07c8:
                r6 = 1
                goto L_0x07e1
            L_0x07ca:
                r6 = 2
                byte r4 = r3.maxX
                if (r4 == r14) goto L_0x07d4
                int r4 = r3.flags
                r4 = r4 & r6
                if (r4 == 0) goto L_0x07d9
            L_0x07d4:
                int r4 = r3.spanSize
                int r4 = r4 + r5
                r3.spanSize = r4
            L_0x07d9:
                int r4 = r3.flags
                r6 = 1
                r4 = r4 & r6
                if (r4 == 0) goto L_0x07e1
                r3.edge = r6
            L_0x07e1:
                java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.messages
                java.lang.Object r4 = r4.get(r2)
                org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
                if (r11 != 0) goto L_0x0820
                boolean r4 = r4.needDrawAvatarInternal()
                if (r4 == 0) goto L_0x0820
                boolean r4 = r3.edge
                if (r4 == 0) goto L_0x0806
                int r4 = r3.spanSize
                r7 = 1000(0x3e8, float:1.401E-42)
                if (r4 == r7) goto L_0x07ff
                int r4 = r4 + 108
                r3.spanSize = r4
            L_0x07ff:
                int r4 = r3.pw
                int r4 = r4 + 108
                r3.pw = r4
                goto L_0x0820
            L_0x0806:
                int r4 = r3.flags
                r7 = 2
                r4 = r4 & r7
                if (r4 == 0) goto L_0x0821
                int r4 = r3.spanSize
                r8 = 1000(0x3e8, float:1.401E-42)
                if (r4 == r8) goto L_0x0817
                int r4 = r4 + -108
                r3.spanSize = r4
                goto L_0x0823
            L_0x0817:
                int r4 = r3.leftSpanOffset
                if (r4 == 0) goto L_0x0823
                int r4 = r4 + 108
                r3.leftSpanOffset = r4
                goto L_0x0823
            L_0x0820:
                r7 = 2
            L_0x0821:
                r8 = 1000(0x3e8, float:1.401E-42)
            L_0x0823:
                int r2 = r2 + 1
                goto L_0x07aa
            L_0x0826:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.GroupedMessages.calculate():void");
        }

        public MessageObject findPrimaryMessageObject() {
            return findMessageWithFlags(5);
        }

        public MessageObject findMessageWithFlags(int i) {
            if (!this.messages.isEmpty() && this.positions.isEmpty()) {
                calculate();
            }
            for (int i2 = 0; i2 < this.messages.size(); i2++) {
                MessageObject messageObject = this.messages.get(i2);
                GroupedMessagePosition groupedMessagePosition = this.positions.get(messageObject);
                if (groupedMessagePosition != null && (groupedMessagePosition.flags & i) == i) {
                    return messageObject;
                }
            }
            return null;
        }

        public static class TransitionParams {
            public boolean backgroundChangeBounds;
            public int bottom;
            public float captionEnterProgress = 1.0f;
            public ChatMessageCell cell;
            public boolean drawBackgroundForDeletedItems;
            public boolean drawCaptionLayout;
            public boolean isNewGroup;
            public int left;
            public float offsetBottom;
            public float offsetLeft;
            public float offsetRight;
            public float offsetTop;
            public boolean pinnedBotton;
            public boolean pinnedTop;
            public int right;
            public int top;

            public void reset() {
                this.captionEnterProgress = 1.0f;
                this.offsetBottom = 0.0f;
                this.offsetTop = 0.0f;
                this.offsetRight = 0.0f;
                this.offsetLeft = 0.0f;
                this.backgroundChangeBounds = false;
            }
        }
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, String str, String str2, String str3, boolean z, boolean z2, boolean z3, boolean z4) {
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.localType = z ? 2 : 1;
        this.currentAccount = i;
        this.localName = str2;
        this.localUserName = str3;
        this.messageText = str;
        this.messageOwner = tLRPC$Message;
        this.localChannel = z2;
        this.localSupergroup = z3;
        this.localEdit = z4;
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, AbstractMap<Long, TLRPC$User> abstractMap, boolean z, boolean z2) {
        this(i, tLRPC$Message, abstractMap, (AbstractMap<Long, TLRPC$Chat>) null, z, z2);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, LongSparseArray<TLRPC$User> longSparseArray, boolean z, boolean z2) {
        this(i, tLRPC$Message, longSparseArray, (LongSparseArray<TLRPC$Chat>) null, z, z2);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, boolean z, boolean z2) {
        this(i, tLRPC$Message, (MessageObject) null, (AbstractMap<Long, TLRPC$User>) null, (AbstractMap<Long, TLRPC$Chat>) null, (LongSparseArray<TLRPC$User>) null, (LongSparseArray<TLRPC$Chat>) null, z, z2, 0);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, MessageObject messageObject, boolean z, boolean z2) {
        this(i, tLRPC$Message, messageObject, (AbstractMap<Long, TLRPC$User>) null, (AbstractMap<Long, TLRPC$Chat>) null, (LongSparseArray<TLRPC$User>) null, (LongSparseArray<TLRPC$Chat>) null, z, z2, 0);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, AbstractMap<Long, TLRPC$User> abstractMap, AbstractMap<Long, TLRPC$Chat> abstractMap2, boolean z, boolean z2) {
        this(i, tLRPC$Message, abstractMap, abstractMap2, z, z2, 0);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, LongSparseArray<TLRPC$User> longSparseArray, LongSparseArray<TLRPC$Chat> longSparseArray2, boolean z, boolean z2) {
        this(i, tLRPC$Message, (MessageObject) null, (AbstractMap<Long, TLRPC$User>) null, (AbstractMap<Long, TLRPC$Chat>) null, longSparseArray, longSparseArray2, z, z2, 0);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, AbstractMap<Long, TLRPC$User> abstractMap, AbstractMap<Long, TLRPC$Chat> abstractMap2, boolean z, boolean z2, long j) {
        this(i, tLRPC$Message, (MessageObject) null, abstractMap, abstractMap2, (LongSparseArray<TLRPC$User>) null, (LongSparseArray<TLRPC$Chat>) null, z, z2, j);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, MessageObject messageObject, AbstractMap<Long, TLRPC$User> abstractMap, AbstractMap<Long, TLRPC$Chat> abstractMap2, LongSparseArray<TLRPC$User> longSparseArray, LongSparseArray<TLRPC$Chat> longSparseArray2, boolean z, boolean z2, long j) {
        TextPaint textPaint;
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        AbstractMap<Long, TLRPC$User> abstractMap3 = abstractMap;
        LongSparseArray<TLRPC$User> longSparseArray3 = longSparseArray;
        boolean z3 = z;
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        Theme.createCommonMessageResources();
        this.currentAccount = i;
        this.messageOwner = tLRPC$Message2;
        this.replyMessageObject = messageObject;
        this.eventId = j;
        this.wasUnread = !tLRPC$Message2.out && tLRPC$Message2.unread;
        TLRPC$Message tLRPC$Message3 = tLRPC$Message2.replyMessage;
        if (tLRPC$Message3 != null) {
            MessageObject messageObject2 = r2;
            MessageObject messageObject3 = new MessageObject(i, tLRPC$Message3, (MessageObject) null, abstractMap, abstractMap2, longSparseArray, longSparseArray2, false, z2, j);
            this.replyMessageObject = messageObject2;
        }
        TLRPC$Peer tLRPC$Peer = tLRPC$Message2.from_id;
        if (tLRPC$Peer instanceof TLRPC$TL_peerUser) {
            getUser(abstractMap3, longSparseArray3, tLRPC$Peer.user_id);
        }
        updateMessageText(abstractMap3, abstractMap2, longSparseArray3, longSparseArray2);
        setType();
        measureInlineBotButtons();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(((long) this.messageOwner.date) * 1000);
        int i2 = gregorianCalendar.get(6);
        int i3 = gregorianCalendar.get(1);
        int i4 = gregorianCalendar.get(2);
        this.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i2)});
        this.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(i3), Integer.valueOf(i4)});
        createMessageSendInfo();
        generateCaption();
        boolean z4 = z;
        if (z4) {
            if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGame) {
                textPaint = Theme.chat_msgGameTextPaint;
            } else {
                textPaint = Theme.chat_msgTextPaint;
            }
            int[] iArr = allowsBigEmoji() ? new int[1] : null;
            CharSequence replaceEmoji = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            this.messageText = replaceEmoji;
            Spannable replaceAnimatedEmoji = replaceAnimatedEmoji(replaceEmoji, this.messageOwner.entities, textPaint.getFontMetricsInt());
            this.messageText = replaceAnimatedEmoji;
            if (iArr != null && iArr[0] > 1) {
                replaceEmojiToLottieFrame(replaceAnimatedEmoji, iArr);
            }
            checkEmojiOnly(iArr);
            checkBigAnimatedEmoji();
            setType();
            createPathThumb();
        }
        this.layoutCreated = z4;
        generateThumbs(false);
        if (z2) {
            checkMediaExistance();
        }
    }

    private void checkBigAnimatedEmoji() {
        AnimatedEmojiSpan[] animatedEmojiSpanArr;
        int i;
        this.emojiAnimatedSticker = null;
        this.emojiAnimatedStickerId = null;
        if (this.emojiOnlyCount == 1 && !(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && !(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaInvoice) && ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaEmpty) || getMedia(this.messageOwner) == null)) {
            TLRPC$Message tLRPC$Message = this.messageOwner;
            if (tLRPC$Message.grouped_id == 0) {
                if (tLRPC$Message.entities.isEmpty()) {
                    CharSequence charSequence = this.messageText;
                    int indexOf = TextUtils.indexOf(charSequence, "🏻");
                    if (indexOf >= 0) {
                        this.emojiAnimatedStickerColor = "_c1";
                        charSequence = charSequence.subSequence(0, indexOf);
                    } else {
                        indexOf = TextUtils.indexOf(charSequence, "🏼");
                        if (indexOf >= 0) {
                            this.emojiAnimatedStickerColor = "_c2";
                            charSequence = charSequence.subSequence(0, indexOf);
                        } else {
                            indexOf = TextUtils.indexOf(charSequence, "🏽");
                            if (indexOf >= 0) {
                                this.emojiAnimatedStickerColor = "_c3";
                                charSequence = charSequence.subSequence(0, indexOf);
                            } else {
                                indexOf = TextUtils.indexOf(charSequence, "🏾");
                                if (indexOf >= 0) {
                                    this.emojiAnimatedStickerColor = "_c4";
                                    charSequence = charSequence.subSequence(0, indexOf);
                                } else {
                                    indexOf = TextUtils.indexOf(charSequence, "🏿");
                                    if (indexOf >= 0) {
                                        this.emojiAnimatedStickerColor = "_c5";
                                        charSequence = charSequence.subSequence(0, indexOf);
                                    } else {
                                        this.emojiAnimatedStickerColor = "";
                                    }
                                }
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(this.emojiAnimatedStickerColor) && (i = indexOf + 2) < this.messageText.length()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(charSequence.toString());
                        CharSequence charSequence2 = this.messageText;
                        sb.append(charSequence2.subSequence(i, charSequence2.length()).toString());
                        charSequence = sb.toString();
                    }
                    if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) || EmojiData.emojiColoredMap.contains(charSequence.toString())) {
                        this.emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(charSequence);
                    }
                } else if (this.messageOwner.entities.size() == 1 && (this.messageOwner.entities.get(0) instanceof TLRPC$TL_messageEntityCustomEmoji)) {
                    try {
                        Long valueOf = Long.valueOf(((TLRPC$TL_messageEntityCustomEmoji) this.messageOwner.entities.get(0)).document_id);
                        this.emojiAnimatedStickerId = valueOf;
                        TLRPC$Document findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, valueOf.longValue());
                        this.emojiAnimatedSticker = findDocument;
                        if (findDocument == null) {
                            CharSequence charSequence3 = this.messageText;
                            if ((charSequence3 instanceof Spanned) && (animatedEmojiSpanArr = (AnimatedEmojiSpan[]) ((Spanned) charSequence3).getSpans(0, charSequence3.length(), AnimatedEmojiSpan.class)) != null && animatedEmojiSpanArr.length == 1) {
                                this.emojiAnimatedSticker = animatedEmojiSpanArr[0].document;
                            }
                        }
                    } catch (Exception unused) {
                    }
                }
            }
        }
        if (this.emojiAnimatedSticker == null && this.emojiAnimatedStickerId == null) {
            generateLayout((TLRPC$User) null);
            return;
        }
        this.type = 1000;
        if (isSticker()) {
            this.type = 13;
        } else if (isAnimatedSticker()) {
            this.type = 15;
        }
    }

    private void createPathThumb() {
        TLRPC$Document document = getDocument();
        if (document != null) {
            this.pathThumb = DocumentObject.getSvgThumb(document, "chat_serviceBackground", 1.0f);
        }
    }

    public void createStrippedThumb() {
        if (this.photoThumbs == null) {
            return;
        }
        if (SharedConfig.getDevicePerformanceClass() == 2 || hasExtendedMediaPreview()) {
            try {
                int size = this.photoThumbs.size();
                for (int i = 0; i < size; i++) {
                    TLRPC$PhotoSize tLRPC$PhotoSize = this.photoThumbs.get(i);
                    if (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) {
                        this.strippedThumb = new BitmapDrawable(ApplicationLoader.applicationContext.getResources(), ImageLoader.getStrippedPhotoBitmap(tLRPC$PhotoSize.bytes, "b"));
                        return;
                    }
                }
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
    }

    private void createDateArray(int i, TLRPC$TL_channelAdminLogEvent tLRPC$TL_channelAdminLogEvent, ArrayList<MessageObject> arrayList, HashMap<String, ArrayList<MessageObject>> hashMap, boolean z) {
        if (hashMap.get(this.dateKey) == null) {
            hashMap.put(this.dateKey, new ArrayList());
            TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
            tLRPC$TL_message.message = LocaleController.formatDateChat((long) tLRPC$TL_channelAdminLogEvent.date);
            tLRPC$TL_message.id = 0;
            tLRPC$TL_message.date = tLRPC$TL_channelAdminLogEvent.date;
            MessageObject messageObject = new MessageObject(i, tLRPC$TL_message, false, false);
            messageObject.type = 10;
            messageObject.contentType = 1;
            messageObject.isDateObject = true;
            if (z) {
                arrayList.add(0, messageObject);
            } else {
                arrayList.add(messageObject);
            }
        }
    }

    private void checkEmojiOnly(int[] iArr) {
        checkEmojiOnly(iArr == null ? null : Integer.valueOf(iArr[0]));
    }

    private void checkEmojiOnly(Integer num) {
        int i;
        int i2;
        TextPaint textPaint;
        int i3 = -1;
        if (num == null || num.intValue() < 1) {
            CharSequence charSequence = this.messageText;
            AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) ((Spannable) charSequence).getSpans(0, charSequence.length(), AnimatedEmojiSpan.class);
            if (animatedEmojiSpanArr == null || animatedEmojiSpanArr.length <= 0) {
                this.totalAnimatedEmojiCount = 0;
                return;
            }
            this.totalAnimatedEmojiCount = animatedEmojiSpanArr.length;
            for (int i4 = 0; i4 < animatedEmojiSpanArr.length; i4++) {
                animatedEmojiSpanArr[i4].replaceFontMetrics(Theme.chat_msgTextPaint.getFontMetricsInt(), (int) (Theme.chat_msgTextPaint.getTextSize() + ((float) AndroidUtilities.dp(4.0f))), -1);
                animatedEmojiSpanArr[i4].full = false;
            }
            return;
        }
        CharSequence charSequence2 = this.messageText;
        Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) ((Spannable) charSequence2).getSpans(0, charSequence2.length(), Emoji.EmojiSpan.class);
        CharSequence charSequence3 = this.messageText;
        AnimatedEmojiSpan[] animatedEmojiSpanArr2 = (AnimatedEmojiSpan[]) ((Spannable) charSequence3).getSpans(0, charSequence3.length(), AnimatedEmojiSpan.class);
        this.emojiOnlyCount = Math.max(num.intValue(), (emojiSpanArr == null ? 0 : emojiSpanArr.length) + (animatedEmojiSpanArr2 == null ? 0 : animatedEmojiSpanArr2.length));
        if (animatedEmojiSpanArr2 == null) {
            i = 0;
        } else {
            i = animatedEmojiSpanArr2.length;
        }
        this.totalAnimatedEmojiCount = i;
        if (animatedEmojiSpanArr2 != null) {
            i2 = 0;
            for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr2) {
                if (!animatedEmojiSpan.standard) {
                    i2++;
                }
            }
        } else {
            i2 = 0;
        }
        int i5 = this.emojiOnlyCount;
        boolean z = (i5 - (emojiSpanArr == null ? 0 : emojiSpanArr.length)) - (animatedEmojiSpanArr2 == null ? 0 : animatedEmojiSpanArr2.length) > 0;
        this.hasUnwrappedEmoji = z;
        if (i5 != 0 && !z) {
            boolean z2 = i5 == i2;
            int i6 = 2;
            switch (i5) {
                case 0:
                case 1:
                    TextPaint[] textPaintArr = Theme.chat_msgTextPaintEmoji;
                    if (!z2) {
                        textPaint = textPaintArr[2];
                        break;
                    } else {
                        textPaint = textPaintArr[0];
                        break;
                    }
                case 2:
                    TextPaint[] textPaintArr2 = Theme.chat_msgTextPaintEmoji;
                    if (!z2) {
                        textPaint = textPaintArr2[2];
                        break;
                    } else {
                        textPaint = textPaintArr2[0];
                        break;
                    }
                case 3:
                    TextPaint[] textPaintArr3 = Theme.chat_msgTextPaintEmoji;
                    if (!z2) {
                        textPaint = textPaintArr3[3];
                        break;
                    } else {
                        textPaint = textPaintArr3[1];
                        break;
                    }
                case 4:
                    TextPaint[] textPaintArr4 = Theme.chat_msgTextPaintEmoji;
                    if (!z2) {
                        textPaint = textPaintArr4[4];
                        break;
                    } else {
                        textPaint = textPaintArr4[2];
                        break;
                    }
                case 5:
                    TextPaint[] textPaintArr5 = Theme.chat_msgTextPaintEmoji;
                    if (!z2) {
                        textPaint = textPaintArr5[5];
                        break;
                    } else {
                        textPaint = textPaintArr5[3];
                        break;
                    }
                case 6:
                    TextPaint[] textPaintArr6 = Theme.chat_msgTextPaintEmoji;
                    if (!z2) {
                        textPaint = textPaintArr6[5];
                        break;
                    } else {
                        textPaint = textPaintArr6[4];
                        break;
                    }
                default:
                    if (i5 > 9) {
                        i3 = 0;
                    }
                    textPaint = Theme.chat_msgTextPaintEmoji[5];
                    i6 = i3;
                    break;
            }
            i6 = 1;
            int textSize = (int) (textPaint.getTextSize() + ((float) AndroidUtilities.dp(4.0f)));
            if (emojiSpanArr != null && emojiSpanArr.length > 0) {
                for (Emoji.EmojiSpan replaceFontMetrics : emojiSpanArr) {
                    replaceFontMetrics.replaceFontMetrics(textPaint.getFontMetricsInt(), textSize);
                }
            }
            if (animatedEmojiSpanArr2 != null && animatedEmojiSpanArr2.length > 0) {
                for (int i7 = 0; i7 < animatedEmojiSpanArr2.length; i7++) {
                    animatedEmojiSpanArr2[i7].replaceFontMetrics(textPaint.getFontMetricsInt(), textSize, i6);
                    animatedEmojiSpanArr2[i7].full = true;
                }
            }
        } else if (animatedEmojiSpanArr2 != null && animatedEmojiSpanArr2.length > 0) {
            for (int i8 = 0; i8 < animatedEmojiSpanArr2.length; i8++) {
                animatedEmojiSpanArr2[i8].replaceFontMetrics(Theme.chat_msgTextPaint.getFontMetricsInt(), (int) (Theme.chat_msgTextPaint.getTextSize() + ((float) AndroidUtilities.dp(4.0f))), -1);
                animatedEmojiSpanArr2[i8].full = false;
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:182:0x04b5  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x04c9  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x04cf A[LOOP:0: B:167:0x0482->B:190:0x04cf, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0ca0  */
    /* JADX WARNING: Removed duplicated region for block: B:734:0x1437  */
    /* JADX WARNING: Removed duplicated region for block: B:737:0x1485  */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x1488  */
    /* JADX WARNING: Removed duplicated region for block: B:751:0x1504  */
    /* JADX WARNING: Removed duplicated region for block: B:755:0x150b  */
    /* JADX WARNING: Removed duplicated region for block: B:779:0x04e8 A[EDGE_INSN: B:779:0x04e8->B:192:0x04e8 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:781:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MessageObject(int r26, org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent r27, java.util.ArrayList<org.telegram.messenger.MessageObject> r28, java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.MessageObject>> r29, org.telegram.tgnet.TLRPC$Chat r30, int[] r31, boolean r32) {
        /*
            r25 = this;
            r6 = r25
            r7 = r27
            r8 = r28
            r0 = r30
            r25.<init>()
            r1 = 1000(0x3e8, float:1.401E-42)
            r6.type = r1
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6.forceSeekTo = r1
            r6.currentEvent = r7
            r1 = r26
            r6.currentAccount = r1
            long r2 = r7.user_id
            r4 = 0
            int r10 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r10 <= 0) goto L_0x0031
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r26)
            long r2 = r7.user_id
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r2)
            r10 = r1
            goto L_0x0032
        L_0x0031:
            r10 = 0
        L_0x0032:
            java.util.GregorianCalendar r1 = new java.util.GregorianCalendar
            r1.<init>()
            int r2 = r7.date
            long r2 = (long) r2
            r11 = 1000(0x3e8, double:4.94E-321)
            long r2 = r2 * r11
            r1.setTimeInMillis(r2)
            r2 = 6
            int r2 = r1.get(r2)
            r11 = 1
            int r3 = r1.get(r11)
            r12 = 2
            int r1 = r1.get(r12)
            r13 = 3
            java.lang.Object[] r14 = new java.lang.Object[r13]
            java.lang.Integer r15 = java.lang.Integer.valueOf(r3)
            r9 = 0
            r14[r9] = r15
            java.lang.Integer r15 = java.lang.Integer.valueOf(r1)
            r14[r11] = r15
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r14[r12] = r2
            java.lang.String r2 = "%d_%02d_%02d"
            java.lang.String r2 = java.lang.String.format(r2, r14)
            r6.dateKey = r2
            java.lang.Object[] r2 = new java.lang.Object[r12]
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)
            r2[r9] = r3
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)
            r2[r11] = r1
            java.lang.String r1 = "%d_%02d"
            java.lang.String r1 = java.lang.String.format(r1, r2)
            r6.monthKey = r1
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r1.<init>()
            long r2 = r0.id
            r1.channel_id = r2
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r7.action
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle
            java.lang.String r14 = ""
            java.lang.String r15 = "un1"
            if (r3 == 0) goto L_0x00c7
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle) r2
            java.lang.String r1 = r2.new_value
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x00b2
            int r2 = org.telegram.messenger.R.string.EventLogEditedGroupTitle
            java.lang.Object[] r3 = new java.lang.Object[r11]
            r3[r9] = r1
            java.lang.String r1 = "EventLogEditedGroupTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x00b2:
            int r2 = org.telegram.messenger.R.string.EventLogEditedChannelTitle
            java.lang.Object[] r3 = new java.lang.Object[r11]
            r3[r9] = r1
            java.lang.String r1 = "EventLogEditedChannelTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
        L_0x00c4:
            r8 = r14
            goto L_0x1431
        L_0x00c7:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangePhoto
            if (r3 == 0) goto L_0x0160
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangePhoto r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangePhoto) r2
            org.telegram.tgnet.TLRPC$TL_messageService r1 = new org.telegram.tgnet.TLRPC$TL_messageService
            r1.<init>()
            r6.messageOwner = r1
            org.telegram.tgnet.TLRPC$Photo r3 = r2.new_photo
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r3 == 0) goto L_0x0103
            org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto r2 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            r2.<init>()
            r1.action = r2
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x00f4
            int r1 = org.telegram.messenger.R.string.EventLogRemovedWGroupPhoto
            java.lang.String r2 = "EventLogRemovedWGroupPhoto"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x00f4:
            int r1 = org.telegram.messenger.R.string.EventLogRemovedChannelPhoto
            java.lang.String r2 = "EventLogRemovedChannelPhoto"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x0103:
            org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto r3 = new org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            r3.<init>()
            r1.action = r3
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            org.telegram.tgnet.TLRPC$Photo r2 = r2.new_photo
            r1.photo = r2
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x013a
            boolean r1 = r25.isVideoAvatar()
            if (r1 == 0) goto L_0x012b
            int r1 = org.telegram.messenger.R.string.EventLogEditedGroupVideo
            java.lang.String r2 = "EventLogEditedGroupVideo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x012b:
            int r1 = org.telegram.messenger.R.string.EventLogEditedGroupPhoto
            java.lang.String r2 = "EventLogEditedGroupPhoto"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x013a:
            boolean r1 = r25.isVideoAvatar()
            if (r1 == 0) goto L_0x0150
            int r1 = org.telegram.messenger.R.string.EventLogEditedChannelVideo
            java.lang.String r2 = "EventLogEditedChannelVideo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x0150:
            int r1 = org.telegram.messenger.R.string.EventLogEditedChannelPhoto
            java.lang.String r2 = "EventLogEditedChannelPhoto"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x0160:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoin
            java.lang.String r12 = "EventLogGroupJoined"
            java.lang.String r11 = "EventLogChannelJoined"
            if (r3 == 0) goto L_0x0188
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x017a
            int r1 = org.telegram.messenger.R.string.EventLogGroupJoined
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r12, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x017a:
            int r1 = org.telegram.messenger.R.string.EventLogChannelJoined
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x0188:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantLeave
            if (r3 == 0) goto L_0x01c6
            org.telegram.tgnet.TLRPC$TL_messageService r1 = new org.telegram.tgnet.TLRPC$TL_messageService
            r1.<init>()
            r6.messageOwner = r1
            org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser r2 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            r2.<init>()
            r1.action = r2
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            long r2 = r7.user_id
            r1.user_id = r2
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x01b6
            int r1 = org.telegram.messenger.R.string.EventLogLeftGroup
            java.lang.String r2 = "EventLogLeftGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x01b6:
            int r1 = org.telegram.messenger.R.string.EventLogLeftChannel
            java.lang.String r2 = "EventLogLeftChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x01c6:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantInvite
            java.lang.String r9 = "un2"
            if (r3 == 0) goto L_0x024a
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantInvite r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantInvite) r2
            org.telegram.tgnet.TLRPC$TL_messageService r1 = new org.telegram.tgnet.TLRPC$TL_messageService
            r1.<init>()
            r6.messageOwner = r1
            org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser r3 = new org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            r3.<init>()
            r1.action = r3
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r1 = getPeerId(r1)
            int r3 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x01f7
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            goto L_0x0206
        L_0x01f7:
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = -r1
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
        L_0x0206:
            org.telegram.tgnet.TLRPC$Message r4 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.from_id
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r5 == 0) goto L_0x0234
            long r4 = r4.user_id
            int r13 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r13 != 0) goto L_0x0234
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x0226
            int r1 = org.telegram.messenger.R.string.EventLogGroupJoined
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r12, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x0226:
            int r1 = org.telegram.messenger.R.string.EventLogChannelJoined
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x0234:
            int r1 = org.telegram.messenger.R.string.EventLogAdded
            java.lang.String r2 = "EventLogAdded"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r9, r3)
            r6.messageText = r1
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c4
        L_0x024a:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin
            java.lang.String r11 = "%1$s"
            r12 = 32
            r13 = 10
            if (r3 != 0) goto L_0x1182
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan
            if (r3 == 0) goto L_0x026c
            r3 = r2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan) r3
            org.telegram.tgnet.TLRPC$ChannelParticipant r3 = r3.prev_participant
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r3 == 0) goto L_0x026c
            r3 = r2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan) r3
            org.telegram.tgnet.TLRPC$ChannelParticipant r3 = r3.new_participant
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipant
            if (r3 == 0) goto L_0x026c
            goto L_0x1182
        L_0x026c:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights
            if (r3 == 0) goto L_0x03f9
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights) r2
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message
            r1.<init>()
            r6.messageOwner = r1
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r2.prev_banned_rights
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.new_banned_rights
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            int r4 = org.telegram.messenger.R.string.EventLogDefaultPermissions
            java.lang.String r5 = "EventLogDefaultPermissions"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.<init>(r4)
            if (r1 != 0) goto L_0x0291
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r1.<init>()
        L_0x0291:
            if (r2 != 0) goto L_0x0298
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r2.<init>()
        L_0x0298:
            boolean r4 = r1.send_messages
            boolean r5 = r2.send_messages
            if (r4 == r5) goto L_0x02c0
            r3.append(r13)
            r3.append(r13)
            boolean r4 = r2.send_messages
            if (r4 != 0) goto L_0x02ab
            r4 = 43
            goto L_0x02ad
        L_0x02ab:
            r4 = 45
        L_0x02ad:
            r3.append(r4)
            r3.append(r12)
            int r4 = org.telegram.messenger.R.string.EventLogRestrictedSendMessages
            java.lang.String r5 = "EventLogRestrictedSendMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.append(r4)
            r4 = 1
            goto L_0x02c1
        L_0x02c0:
            r4 = 0
        L_0x02c1:
            boolean r5 = r1.send_stickers
            boolean r9 = r2.send_stickers
            if (r5 != r9) goto L_0x02d9
            boolean r5 = r1.send_inline
            boolean r9 = r2.send_inline
            if (r5 != r9) goto L_0x02d9
            boolean r5 = r1.send_gifs
            boolean r9 = r2.send_gifs
            if (r5 != r9) goto L_0x02d9
            boolean r5 = r1.send_games
            boolean r9 = r2.send_games
            if (r5 == r9) goto L_0x02fc
        L_0x02d9:
            if (r4 != 0) goto L_0x02df
            r3.append(r13)
            r4 = 1
        L_0x02df:
            r3.append(r13)
            boolean r5 = r2.send_stickers
            if (r5 != 0) goto L_0x02e9
            r5 = 43
            goto L_0x02eb
        L_0x02e9:
            r5 = 45
        L_0x02eb:
            r3.append(r5)
            r3.append(r12)
            int r5 = org.telegram.messenger.R.string.EventLogRestrictedSendStickers
            java.lang.String r9 = "EventLogRestrictedSendStickers"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x02fc:
            boolean r5 = r1.send_media
            boolean r9 = r2.send_media
            if (r5 == r9) goto L_0x0325
            if (r4 != 0) goto L_0x0308
            r3.append(r13)
            r4 = 1
        L_0x0308:
            r3.append(r13)
            boolean r5 = r2.send_media
            if (r5 != 0) goto L_0x0312
            r5 = 43
            goto L_0x0314
        L_0x0312:
            r5 = 45
        L_0x0314:
            r3.append(r5)
            r3.append(r12)
            int r5 = org.telegram.messenger.R.string.EventLogRestrictedSendMedia
            java.lang.String r9 = "EventLogRestrictedSendMedia"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x0325:
            boolean r5 = r1.send_polls
            boolean r9 = r2.send_polls
            if (r5 == r9) goto L_0x034e
            if (r4 != 0) goto L_0x0331
            r3.append(r13)
            r4 = 1
        L_0x0331:
            r3.append(r13)
            boolean r5 = r2.send_polls
            if (r5 != 0) goto L_0x033b
            r5 = 43
            goto L_0x033d
        L_0x033b:
            r5 = 45
        L_0x033d:
            r3.append(r5)
            r3.append(r12)
            int r5 = org.telegram.messenger.R.string.EventLogRestrictedSendPolls
            java.lang.String r9 = "EventLogRestrictedSendPolls"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x034e:
            boolean r5 = r1.embed_links
            boolean r9 = r2.embed_links
            if (r5 == r9) goto L_0x0377
            if (r4 != 0) goto L_0x035a
            r3.append(r13)
            r4 = 1
        L_0x035a:
            r3.append(r13)
            boolean r5 = r2.embed_links
            if (r5 != 0) goto L_0x0364
            r5 = 43
            goto L_0x0366
        L_0x0364:
            r5 = 45
        L_0x0366:
            r3.append(r5)
            r3.append(r12)
            int r5 = org.telegram.messenger.R.string.EventLogRestrictedSendEmbed
            java.lang.String r9 = "EventLogRestrictedSendEmbed"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x0377:
            boolean r5 = r1.change_info
            boolean r9 = r2.change_info
            if (r5 == r9) goto L_0x03a0
            if (r4 != 0) goto L_0x0383
            r3.append(r13)
            r4 = 1
        L_0x0383:
            r3.append(r13)
            boolean r5 = r2.change_info
            if (r5 != 0) goto L_0x038d
            r5 = 43
            goto L_0x038f
        L_0x038d:
            r5 = 45
        L_0x038f:
            r3.append(r5)
            r3.append(r12)
            int r5 = org.telegram.messenger.R.string.EventLogRestrictedChangeInfo
            java.lang.String r9 = "EventLogRestrictedChangeInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x03a0:
            boolean r5 = r1.invite_users
            boolean r9 = r2.invite_users
            if (r5 == r9) goto L_0x03c9
            if (r4 != 0) goto L_0x03ac
            r3.append(r13)
            r4 = 1
        L_0x03ac:
            r3.append(r13)
            boolean r5 = r2.invite_users
            if (r5 != 0) goto L_0x03b6
            r5 = 43
            goto L_0x03b8
        L_0x03b6:
            r5 = 45
        L_0x03b8:
            r3.append(r5)
            r3.append(r12)
            int r5 = org.telegram.messenger.R.string.EventLogRestrictedInviteUsers
            java.lang.String r9 = "EventLogRestrictedInviteUsers"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x03c9:
            boolean r1 = r1.pin_messages
            boolean r5 = r2.pin_messages
            if (r1 == r5) goto L_0x03f1
            if (r4 != 0) goto L_0x03d4
            r3.append(r13)
        L_0x03d4:
            r3.append(r13)
            boolean r1 = r2.pin_messages
            if (r1 != 0) goto L_0x03de
            r1 = 43
            goto L_0x03e0
        L_0x03de:
            r1 = 45
        L_0x03e0:
            r3.append(r1)
            r3.append(r12)
            int r1 = org.telegram.messenger.R.string.EventLogRestrictedPinMessages
            java.lang.String r2 = "EventLogRestrictedPinMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r3.append(r1)
        L_0x03f1:
            java.lang.String r1 = r3.toString()
            r6.messageText = r1
            goto L_0x00c4
        L_0x03f9:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan
            java.lang.String r12 = "Hours"
            java.lang.String r13 = "Minutes"
            if (r3 == 0) goto L_0x0706
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan) r2
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message
            r1.<init>()
            r6.messageOwner = r1
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = r2.prev_participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r4 = getPeerId(r1)
            r21 = 0
            int r1 = (r4 > r21 ? 1 : (r4 == r21 ? 0 : -1))
            if (r1 <= 0) goto L_0x0427
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r4)
            goto L_0x0436
        L_0x0427:
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r4 = -r4
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r4)
        L_0x0436:
            org.telegram.tgnet.TLRPC$ChannelParticipant r4 = r2.prev_participant
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = r4.banned_rights
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r2.new_participant
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.banned_rights
            boolean r5 = r0.megagroup
            if (r5 == 0) goto L_0x06d1
            if (r2 == 0) goto L_0x0450
            boolean r5 = r2.view_messages
            if (r5 == 0) goto L_0x0450
            if (r4 == 0) goto L_0x06d1
            int r5 = r2.until_date
            int r9 = r4.until_date
            if (r5 == r9) goto L_0x06d1
        L_0x0450:
            if (r2 == 0) goto L_0x04d9
            boolean r5 = org.telegram.messenger.AndroidUtilities.isBannedForever(r2)
            if (r5 != 0) goto L_0x04d9
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            int r9 = r2.until_date
            int r15 = r7.date
            int r9 = r9 - r15
            int r15 = r9 / 60
            r3 = 60
            int r15 = r15 / r3
            int r15 = r15 / 24
            int r21 = r15 * 60
            int r21 = r21 * 60
            int r21 = r21 * 24
            int r9 = r9 - r21
            int r21 = r9 / 60
            int r8 = r21 / 60
            int r21 = r8 * 60
            int r21 = r21 * 60
            int r9 = r9 - r21
            int r9 = r9 / r3
            r23 = r14
            r3 = 0
            r14 = 3
            r18 = 0
        L_0x0482:
            if (r3 >= r14) goto L_0x04e8
            if (r3 != 0) goto L_0x0496
            if (r15 == 0) goto L_0x04b0
            r14 = 0
            java.lang.Object[] r7 = new java.lang.Object[r14]
            java.lang.String r14 = "Days"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r14, r15, r7)
        L_0x0491:
            int r18 = r18 + 1
        L_0x0493:
            r14 = r18
            goto L_0x04b3
        L_0x0496:
            r7 = 1
            if (r3 != r7) goto L_0x04a6
            if (r8 == 0) goto L_0x04b0
            r7 = 0
            java.lang.Object[] r14 = new java.lang.Object[r7]
            java.lang.String r14 = org.telegram.messenger.LocaleController.formatPluralString(r12, r8, r14)
            int r18 = r18 + 1
            r7 = r14
            goto L_0x0493
        L_0x04a6:
            r7 = 0
            if (r9 == 0) goto L_0x04b0
            java.lang.Object[] r14 = new java.lang.Object[r7]
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r13, r9, r14)
            goto L_0x0491
        L_0x04b0:
            r14 = r18
            r7 = 0
        L_0x04b3:
            if (r7 == 0) goto L_0x04c9
            int r18 = r5.length()
            if (r18 <= 0) goto L_0x04c3
            r18 = r8
            java.lang.String r8 = ", "
            r5.append(r8)
            goto L_0x04c5
        L_0x04c3:
            r18 = r8
        L_0x04c5:
            r5.append(r7)
            goto L_0x04cb
        L_0x04c9:
            r18 = r8
        L_0x04cb:
            r7 = 2
            if (r14 != r7) goto L_0x04cf
            goto L_0x04e8
        L_0x04cf:
            int r3 = r3 + 1
            r7 = r27
            r8 = r18
            r18 = r14
            r14 = 3
            goto L_0x0482
        L_0x04d9:
            r23 = r14
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            int r3 = org.telegram.messenger.R.string.UserRestrictionsUntilForever
            java.lang.String r7 = "UserRestrictionsUntilForever"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r5.<init>(r3)
        L_0x04e8:
            int r3 = org.telegram.messenger.R.string.EventLogRestrictedUntil
            java.lang.String r7 = "EventLogRestrictedUntil"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            int r7 = r3.indexOf(r11)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$Message r11 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r11 = r11.entities
            java.lang.String r1 = r6.getUserName(r1, r11, r7)
            r7 = 0
            r9[r7] = r1
            java.lang.String r1 = r5.toString()
            r5 = 1
            r9[r5] = r1
            java.lang.String r1 = java.lang.String.format(r3, r9)
            r8.<init>(r1)
            if (r4 != 0) goto L_0x0519
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r4 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r4.<init>()
        L_0x0519:
            if (r2 != 0) goto L_0x0520
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r2.<init>()
        L_0x0520:
            boolean r1 = r4.view_messages
            boolean r3 = r2.view_messages
            if (r1 == r3) goto L_0x054c
            r1 = 10
            r8.append(r1)
            r8.append(r1)
            boolean r1 = r2.view_messages
            if (r1 != 0) goto L_0x0535
            r1 = 43
            goto L_0x0537
        L_0x0535:
            r1 = 45
        L_0x0537:
            r8.append(r1)
            r1 = 32
            r8.append(r1)
            int r1 = org.telegram.messenger.R.string.EventLogRestrictedReadMessages
            java.lang.String r3 = "EventLogRestrictedReadMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            r8.append(r1)
            r1 = 1
            goto L_0x054d
        L_0x054c:
            r1 = 0
        L_0x054d:
            boolean r3 = r4.send_messages
            boolean r5 = r2.send_messages
            if (r3 == r5) goto L_0x057a
            r3 = 10
            if (r1 != 0) goto L_0x055b
            r8.append(r3)
            r1 = 1
        L_0x055b:
            r8.append(r3)
            boolean r3 = r2.send_messages
            if (r3 != 0) goto L_0x0565
            r3 = 43
            goto L_0x0567
        L_0x0565:
            r3 = 45
        L_0x0567:
            r8.append(r3)
            r3 = 32
            r8.append(r3)
            int r3 = org.telegram.messenger.R.string.EventLogRestrictedSendMessages
            java.lang.String r5 = "EventLogRestrictedSendMessages"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r8.append(r3)
        L_0x057a:
            boolean r3 = r4.send_stickers
            boolean r5 = r2.send_stickers
            if (r3 != r5) goto L_0x0592
            boolean r3 = r4.send_inline
            boolean r5 = r2.send_inline
            if (r3 != r5) goto L_0x0592
            boolean r3 = r4.send_gifs
            boolean r5 = r2.send_gifs
            if (r3 != r5) goto L_0x0592
            boolean r3 = r4.send_games
            boolean r5 = r2.send_games
            if (r3 == r5) goto L_0x05b9
        L_0x0592:
            r3 = 10
            if (r1 != 0) goto L_0x059a
            r8.append(r3)
            r1 = 1
        L_0x059a:
            r8.append(r3)
            boolean r3 = r2.send_stickers
            if (r3 != 0) goto L_0x05a4
            r3 = 43
            goto L_0x05a6
        L_0x05a4:
            r3 = 45
        L_0x05a6:
            r8.append(r3)
            r3 = 32
            r8.append(r3)
            int r3 = org.telegram.messenger.R.string.EventLogRestrictedSendStickers
            java.lang.String r5 = "EventLogRestrictedSendStickers"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r8.append(r3)
        L_0x05b9:
            boolean r3 = r4.send_media
            boolean r5 = r2.send_media
            if (r3 == r5) goto L_0x05e6
            r3 = 10
            if (r1 != 0) goto L_0x05c7
            r8.append(r3)
            r1 = 1
        L_0x05c7:
            r8.append(r3)
            boolean r3 = r2.send_media
            if (r3 != 0) goto L_0x05d1
            r3 = 43
            goto L_0x05d3
        L_0x05d1:
            r3 = 45
        L_0x05d3:
            r8.append(r3)
            r3 = 32
            r8.append(r3)
            int r3 = org.telegram.messenger.R.string.EventLogRestrictedSendMedia
            java.lang.String r5 = "EventLogRestrictedSendMedia"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r8.append(r3)
        L_0x05e6:
            boolean r3 = r4.send_polls
            boolean r5 = r2.send_polls
            if (r3 == r5) goto L_0x0613
            r3 = 10
            if (r1 != 0) goto L_0x05f4
            r8.append(r3)
            r1 = 1
        L_0x05f4:
            r8.append(r3)
            boolean r3 = r2.send_polls
            if (r3 != 0) goto L_0x05fe
            r3 = 43
            goto L_0x0600
        L_0x05fe:
            r3 = 45
        L_0x0600:
            r8.append(r3)
            r3 = 32
            r8.append(r3)
            int r3 = org.telegram.messenger.R.string.EventLogRestrictedSendPolls
            java.lang.String r5 = "EventLogRestrictedSendPolls"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r8.append(r3)
        L_0x0613:
            boolean r3 = r4.embed_links
            boolean r5 = r2.embed_links
            if (r3 == r5) goto L_0x0640
            r3 = 10
            if (r1 != 0) goto L_0x0621
            r8.append(r3)
            r1 = 1
        L_0x0621:
            r8.append(r3)
            boolean r3 = r2.embed_links
            if (r3 != 0) goto L_0x062b
            r3 = 43
            goto L_0x062d
        L_0x062b:
            r3 = 45
        L_0x062d:
            r8.append(r3)
            r3 = 32
            r8.append(r3)
            int r3 = org.telegram.messenger.R.string.EventLogRestrictedSendEmbed
            java.lang.String r5 = "EventLogRestrictedSendEmbed"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r8.append(r3)
        L_0x0640:
            boolean r3 = r4.change_info
            boolean r5 = r2.change_info
            if (r3 == r5) goto L_0x066d
            r3 = 10
            if (r1 != 0) goto L_0x064e
            r8.append(r3)
            r1 = 1
        L_0x064e:
            r8.append(r3)
            boolean r3 = r2.change_info
            if (r3 != 0) goto L_0x0658
            r3 = 43
            goto L_0x065a
        L_0x0658:
            r3 = 45
        L_0x065a:
            r8.append(r3)
            r3 = 32
            r8.append(r3)
            int r3 = org.telegram.messenger.R.string.EventLogRestrictedChangeInfo
            java.lang.String r5 = "EventLogRestrictedChangeInfo"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r8.append(r3)
        L_0x066d:
            boolean r3 = r4.invite_users
            boolean r5 = r2.invite_users
            if (r3 == r5) goto L_0x069a
            r3 = 10
            if (r1 != 0) goto L_0x067b
            r8.append(r3)
            r1 = 1
        L_0x067b:
            r8.append(r3)
            boolean r3 = r2.invite_users
            if (r3 != 0) goto L_0x0685
            r3 = 43
            goto L_0x0687
        L_0x0685:
            r3 = 45
        L_0x0687:
            r8.append(r3)
            r3 = 32
            r8.append(r3)
            int r3 = org.telegram.messenger.R.string.EventLogRestrictedInviteUsers
            java.lang.String r5 = "EventLogRestrictedInviteUsers"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r5, r3)
            r8.append(r3)
        L_0x069a:
            boolean r3 = r4.pin_messages
            boolean r4 = r2.pin_messages
            if (r3 == r4) goto L_0x06c9
            if (r1 != 0) goto L_0x06a8
            r1 = 10
            r8.append(r1)
            goto L_0x06aa
        L_0x06a8:
            r1 = 10
        L_0x06aa:
            r8.append(r1)
            boolean r1 = r2.pin_messages
            if (r1 != 0) goto L_0x06b4
            r12 = 43
            goto L_0x06b6
        L_0x06b4:
            r12 = 45
        L_0x06b6:
            r8.append(r12)
            r1 = 32
            r8.append(r1)
            int r1 = org.telegram.messenger.R.string.EventLogRestrictedPinMessages
            java.lang.String r2 = "EventLogRestrictedPinMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r8.append(r1)
        L_0x06c9:
            java.lang.String r1 = r8.toString()
            r6.messageText = r1
            goto L_0x07ed
        L_0x06d1:
            r23 = r14
            if (r2 == 0) goto L_0x06e4
            if (r4 == 0) goto L_0x06db
            boolean r2 = r2.view_messages
            if (r2 == 0) goto L_0x06e4
        L_0x06db:
            int r2 = org.telegram.messenger.R.string.EventLogChannelRestricted
            java.lang.String r3 = "EventLogChannelRestricted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x06ec
        L_0x06e4:
            int r2 = org.telegram.messenger.R.string.EventLogChannelUnrestricted
            java.lang.String r3 = "EventLogChannelUnrestricted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x06ec:
            int r3 = r2.indexOf(r11)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$Message r4 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r4.entities
            java.lang.String r1 = r6.getUserName(r1, r4, r3)
            r3 = 0
            r5[r3] = r1
            java.lang.String r1 = java.lang.String.format(r2, r5)
            r6.messageText = r1
            goto L_0x07ed
        L_0x0706:
            r23 = r14
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionUpdatePinned
            if (r4 == 0) goto L_0x0787
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionUpdatePinned r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionUpdatePinned) r2
            org.telegram.tgnet.TLRPC$Message r1 = r2.message
            java.lang.String r3 = "EventLogPinnedMessages"
            java.lang.String r4 = "EventLogUnpinnedMessages"
            if (r10 == 0) goto L_0x0764
            long r7 = r10.id
            r11 = 136817688(0x827aCLASSNAME, double:6.75969194E-316)
            int r5 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r5 != 0) goto L_0x0764
            org.telegram.tgnet.TLRPC$MessageFwdHeader r5 = r1.fwd_from
            if (r5 == 0) goto L_0x0764
            org.telegram.tgnet.TLRPC$Peer r5 = r5.from_id
            boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r5 == 0) goto L_0x0764
            int r5 = r6.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r7 = r2.message
            org.telegram.tgnet.TLRPC$MessageFwdHeader r7 = r7.fwd_from
            org.telegram.tgnet.TLRPC$Peer r7 = r7.from_id
            long r7 = r7.channel_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r7)
            org.telegram.tgnet.TLRPC$Message r2 = r2.message
            boolean r7 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r7 != 0) goto L_0x0757
            boolean r2 = r2.pinned
            if (r2 != 0) goto L_0x074a
            goto L_0x0757
        L_0x074a:
            int r2 = org.telegram.messenger.R.string.EventLogPinnedMessages
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r5)
            r6.messageText = r2
            goto L_0x07c0
        L_0x0757:
            int r2 = org.telegram.messenger.R.string.EventLogUnpinnedMessages
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r5)
            r6.messageText = r2
            goto L_0x07c0
        L_0x0764:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r2 != 0) goto L_0x077a
            boolean r2 = r1.pinned
            if (r2 != 0) goto L_0x076d
            goto L_0x077a
        L_0x076d:
            int r2 = org.telegram.messenger.R.string.EventLogPinnedMessages
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            goto L_0x07c0
        L_0x077a:
            int r2 = org.telegram.messenger.R.string.EventLogUnpinnedMessages
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            goto L_0x07c0
        L_0x0787:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionStopPoll
            if (r4 == 0) goto L_0x07c6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionStopPoll r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionStopPoll) r2
            org.telegram.tgnet.TLRPC$Message r1 = r2.message
            org.telegram.tgnet.TLRPC$MessageMedia r2 = getMedia(r1)
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x07b2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = getMedia(r1)
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            boolean r2 = r2.quiz
            if (r2 == 0) goto L_0x07b2
            int r2 = org.telegram.messenger.R.string.EventLogStopQuiz
            java.lang.String r3 = "EventLogStopQuiz"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            goto L_0x07c0
        L_0x07b2:
            int r2 = org.telegram.messenger.R.string.EventLogStopPoll
            java.lang.String r3 = "EventLogStopPoll"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
        L_0x07c0:
            r7 = r27
            r8 = r23
            goto L_0x1432
        L_0x07c6:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures
            if (r4 == 0) goto L_0x07f3
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures) r2
            boolean r1 = r2.new_value
            if (r1 == 0) goto L_0x07df
            int r1 = org.telegram.messenger.R.string.EventLogToggledSignaturesOn
            java.lang.String r2 = "EventLogToggledSignaturesOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x07ed
        L_0x07df:
            int r1 = org.telegram.messenger.R.string.EventLogToggledSignaturesOff
            java.lang.String r2 = "EventLogToggledSignaturesOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
        L_0x07ed:
            r7 = r27
            r8 = r23
            goto L_0x1431
        L_0x07f3:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites
            if (r4 == 0) goto L_0x081b
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites) r2
            boolean r1 = r2.new_value
            if (r1 == 0) goto L_0x080c
            int r1 = org.telegram.messenger.R.string.EventLogToggledInvitesOn
            java.lang.String r2 = "EventLogToggledInvitesOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x07ed
        L_0x080c:
            int r1 = org.telegram.messenger.R.string.EventLogToggledInvitesOff
            java.lang.String r2 = "EventLogToggledInvitesOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x07ed
        L_0x081b:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDeleteMessage
            if (r4 == 0) goto L_0x0832
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDeleteMessage r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDeleteMessage) r2
            org.telegram.tgnet.TLRPC$Message r1 = r2.message
            int r2 = org.telegram.messenger.R.string.EventLogDeletedMessages
            java.lang.String r3 = "EventLogDeletedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            goto L_0x07c0
        L_0x0832:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat
            if (r4 == 0) goto L_0x08de
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r1 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat) r1
            long r3 = r1.new_value
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat) r2
            long r1 = r2.prev_value
            boolean r5 = r0.megagroup
            if (r5 == 0) goto L_0x0890
            r7 = 0
            int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x086c
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            int r2 = org.telegram.messenger.R.string.EventLogRemovedLinkedChannel
            java.lang.String r3 = "EventLogRemovedLinkedChannel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r9, r1)
            r6.messageText = r1
            goto L_0x07ed
        L_0x086c:
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r2 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            int r2 = org.telegram.messenger.R.string.EventLogChangedLinkedChannel
            java.lang.String r3 = "EventLogChangedLinkedChannel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r9, r1)
            r6.messageText = r1
            goto L_0x07ed
        L_0x0890:
            r7 = 0
            int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x08ba
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            int r2 = org.telegram.messenger.R.string.EventLogRemovedLinkedGroup
            java.lang.String r3 = "EventLogRemovedLinkedGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r9, r1)
            r6.messageText = r1
            goto L_0x07ed
        L_0x08ba:
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r2 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            int r2 = org.telegram.messenger.R.string.EventLogChangedLinkedGroup
            java.lang.String r3 = "EventLogChangedLinkedGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r9, r1)
            r6.messageText = r1
            goto L_0x07ed
        L_0x08de:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden
            if (r4 == 0) goto L_0x0908
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden) r2
            boolean r1 = r2.new_value
            if (r1 == 0) goto L_0x08f8
            int r1 = org.telegram.messenger.R.string.EventLogToggledInvitesHistoryOff
            java.lang.String r2 = "EventLogToggledInvitesHistoryOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x07ed
        L_0x08f8:
            int r1 = org.telegram.messenger.R.string.EventLogToggledInvitesHistoryOn
            java.lang.String r2 = "EventLogToggledInvitesHistoryOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x07ed
        L_0x0908:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout
            if (r4 == 0) goto L_0x0992
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x0915
            int r2 = org.telegram.messenger.R.string.EventLogEditedGroupDescription
            java.lang.String r3 = "EventLogEditedGroupDescription"
            goto L_0x0919
        L_0x0915:
            int r2 = org.telegram.messenger.R.string.EventLogEditedChannelDescription
            java.lang.String r3 = "EventLogEditedChannelDescription"
        L_0x0919:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            org.telegram.tgnet.TLRPC$TL_message r2 = new org.telegram.tgnet.TLRPC$TL_message
            r2.<init>()
            r3 = 0
            r2.out = r3
            r2.unread = r3
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r3.<init>()
            r2.from_id = r3
            r7 = r27
            long r4 = r7.user_id
            r3.user_id = r4
            r2.peer_id = r1
            int r1 = r7.date
            r2.date = r1
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            r3 = r1
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout) r3
            java.lang.String r3 = r3.new_value
            r2.message = r3
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r1 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout) r1
            java.lang.String r1 = r1.prev_value
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0986
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r1.<init>()
            r2.media = r1
            org.telegram.tgnet.TLRPC$TL_webPage r3 = new org.telegram.tgnet.TLRPC$TL_webPage
            r3.<init>()
            r1.webpage = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r3 = 10
            r1.flags = r3
            r8 = r23
            r1.display_url = r8
            r1.url = r8
            int r3 = org.telegram.messenger.R.string.EventLogPreviousGroupDescription
            java.lang.String r4 = "EventLogPreviousGroupDescription"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.site_name = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r3 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout) r3
            java.lang.String r3 = r3.prev_value
            r1.description = r3
            goto L_0x098f
        L_0x0986:
            r8 = r23
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r2.media = r1
        L_0x098f:
            r1 = r2
            goto L_0x1432
        L_0x0992:
            r7 = r27
            r8 = r23
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme
            if (r4 == 0) goto L_0x0a1a
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x09a3
            int r2 = org.telegram.messenger.R.string.EventLogEditedGroupTheme
            java.lang.String r3 = "EventLogEditedGroupTheme"
            goto L_0x09a7
        L_0x09a3:
            int r2 = org.telegram.messenger.R.string.EventLogEditedChannelTheme
            java.lang.String r3 = "EventLogEditedChannelTheme"
        L_0x09a7:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            org.telegram.tgnet.TLRPC$TL_message r2 = new org.telegram.tgnet.TLRPC$TL_message
            r2.<init>()
            r3 = 0
            r2.out = r3
            r2.unread = r3
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r3.<init>()
            r2.from_id = r3
            long r4 = r7.user_id
            r3.user_id = r4
            r2.peer_id = r1
            int r1 = r7.date
            r2.date = r1
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            r3 = r1
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme) r3
            java.lang.String r3 = r3.new_value
            r2.message = r3
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme r1 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme) r1
            java.lang.String r1 = r1.prev_value
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0a11
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r1.<init>()
            r2.media = r1
            org.telegram.tgnet.TLRPC$TL_webPage r3 = new org.telegram.tgnet.TLRPC$TL_webPage
            r3.<init>()
            r1.webpage = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r3 = 10
            r1.flags = r3
            r1.display_url = r8
            r1.url = r8
            int r3 = org.telegram.messenger.R.string.EventLogPreviousGroupTheme
            java.lang.String r4 = "EventLogPreviousGroupTheme"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.site_name = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r3 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme) r3
            java.lang.String r3 = r3.prev_value
            r1.description = r3
            goto L_0x098f
        L_0x0a11:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r2.media = r1
            goto L_0x098f
        L_0x0a1a:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername
            if (r4 == 0) goto L_0x0b1b
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername) r2
            java.lang.String r2 = r2.new_value
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0a40
            boolean r3 = r0.megagroup
            if (r3 == 0) goto L_0x0a31
            int r3 = org.telegram.messenger.R.string.EventLogChangedGroupLink
            java.lang.String r4 = "EventLogChangedGroupLink"
            goto L_0x0a35
        L_0x0a31:
            int r3 = org.telegram.messenger.R.string.EventLogChangedChannelLink
            java.lang.String r4 = "EventLogChangedChannelLink"
        L_0x0a35:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.CharSequence r3 = replaceWithLink(r3, r15, r10)
            r6.messageText = r3
            goto L_0x0a57
        L_0x0a40:
            boolean r3 = r0.megagroup
            if (r3 == 0) goto L_0x0a49
            int r3 = org.telegram.messenger.R.string.EventLogRemovedGroupLink
            java.lang.String r4 = "EventLogRemovedGroupLink"
            goto L_0x0a4d
        L_0x0a49:
            int r3 = org.telegram.messenger.R.string.EventLogRemovedChannelLink
            java.lang.String r4 = "EventLogRemovedChannelLink"
        L_0x0a4d:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.CharSequence r3 = replaceWithLink(r3, r15, r10)
            r6.messageText = r3
        L_0x0a57:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message
            r3.<init>()
            r4 = 0
            r3.out = r4
            r3.unread = r4
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r4.<init>()
            r3.from_id = r4
            long r11 = r7.user_id
            r4.user_id = r11
            r3.peer_id = r1
            int r1 = r7.date
            r3.date = r1
            boolean r1 = android.text.TextUtils.isEmpty(r2)
            if (r1 != 0) goto L_0x0a9c
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r4 = "https://"
            r1.append(r4)
            int r4 = r6.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.String r4 = r4.linkPrefix
            r1.append(r4)
            java.lang.String r4 = "/"
            r1.append(r4)
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r3.message = r1
            goto L_0x0a9e
        L_0x0a9c:
            r3.message = r8
        L_0x0a9e:
            org.telegram.tgnet.TLRPC$TL_messageEntityUrl r1 = new org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            r1.<init>()
            r2 = 0
            r1.offset = r2
            java.lang.String r2 = r3.message
            int r2 = r2.length()
            r1.length = r2
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r3.entities
            r2.add(r1)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r1 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername) r1
            java.lang.String r1 = r1.prev_value
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 != 0) goto L_0x0b11
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r1.<init>()
            r3.media = r1
            org.telegram.tgnet.TLRPC$TL_webPage r2 = new org.telegram.tgnet.TLRPC$TL_webPage
            r2.<init>()
            r1.webpage = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r3.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r2 = 10
            r1.flags = r2
            r1.display_url = r8
            r1.url = r8
            int r2 = org.telegram.messenger.R.string.EventLogPreviousLink
            java.lang.String r4 = "EventLogPreviousLink"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            r1.site_name = r2
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r3.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "https://"
            r2.append(r4)
            int r4 = r6.currentAccount
            org.telegram.messenger.MessagesController r4 = org.telegram.messenger.MessagesController.getInstance(r4)
            java.lang.String r4 = r4.linkPrefix
            r2.append(r4)
            java.lang.String r4 = "/"
            r2.append(r4)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r4 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r4 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername) r4
            java.lang.String r4 = r4.prev_value
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r1.description = r2
            goto L_0x0b18
        L_0x0b11:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r3.media = r1
        L_0x0b18:
            r1 = r3
            goto L_0x1432
        L_0x0b1b:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage
            if (r4 == 0) goto L_0x0cac
            org.telegram.tgnet.TLRPC$TL_message r2 = new org.telegram.tgnet.TLRPC$TL_message
            r2.<init>()
            r3 = 0
            r2.out = r3
            r2.unread = r3
            r2.peer_id = r1
            int r1 = r7.date
            r2.date = r1
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r1 = r7.action
            r3 = r1
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage) r3
            org.telegram.tgnet.TLRPC$Message r3 = r3.new_message
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage r1 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage) r1
            org.telegram.tgnet.TLRPC$Message r1 = r1.prev_message
            if (r3 == 0) goto L_0x0b43
            org.telegram.tgnet.TLRPC$Peer r4 = r3.from_id
            if (r4 == 0) goto L_0x0b43
            r2.from_id = r4
            goto L_0x0b4e
        L_0x0b43:
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r4.<init>()
            r2.from_id = r4
            long r11 = r7.user_id
            r4.user_id = r11
        L_0x0b4e:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = getMedia(r3)
            if (r4 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$MessageMedia r4 = getMedia(r3)
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            if (r4 != 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$MessageMedia r4 = getMedia(r3)
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r4 != 0) goto L_0x0CLASSNAME
            java.lang.String r4 = r3.message
            java.lang.String r5 = r1.message
            boolean r4 = android.text.TextUtils.equals(r4, r5)
            r5 = 1
            r4 = r4 ^ r5
            org.telegram.tgnet.TLRPC$MessageMedia r5 = getMedia(r3)
            java.lang.Class r5 = r5.getClass()
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r1.media
            java.lang.Class r9 = r9.getClass()
            if (r5 != r9) goto L_0x0bc1
            org.telegram.tgnet.TLRPC$MessageMedia r5 = getMedia(r3)
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            if (r5 == 0) goto L_0x0b9e
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            if (r5 == 0) goto L_0x0b9e
            org.telegram.tgnet.TLRPC$MessageMedia r5 = getMedia(r3)
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            long r11 = r5.id
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$Photo r5 = r5.photo
            long r13 = r5.id
            int r5 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r5 != 0) goto L_0x0bc1
        L_0x0b9e:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = getMedia(r3)
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            if (r5 == 0) goto L_0x0bbf
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            if (r5 == 0) goto L_0x0bbf
            org.telegram.tgnet.TLRPC$MessageMedia r5 = getMedia(r3)
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            long r11 = r5.id
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r1.media
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            long r13 = r5.id
            int r5 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r5 == 0) goto L_0x0bbf
            goto L_0x0bc1
        L_0x0bbf:
            r5 = 0
            goto L_0x0bc2
        L_0x0bc1:
            r5 = 1
        L_0x0bc2:
            if (r5 == 0) goto L_0x0bd5
            if (r4 == 0) goto L_0x0bd5
            int r5 = org.telegram.messenger.R.string.EventLogEditedMediaCaption
            java.lang.String r9 = "EventLogEditedMediaCaption"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            java.lang.CharSequence r5 = replaceWithLink(r5, r15, r10)
            r6.messageText = r5
            goto L_0x0bf4
        L_0x0bd5:
            if (r4 == 0) goto L_0x0be6
            int r5 = org.telegram.messenger.R.string.EventLogEditedCaption
            java.lang.String r9 = "EventLogEditedCaption"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            java.lang.CharSequence r5 = replaceWithLink(r5, r15, r10)
            r6.messageText = r5
            goto L_0x0bf4
        L_0x0be6:
            int r5 = org.telegram.messenger.R.string.EventLogEditedMedia
            java.lang.String r9 = "EventLogEditedMedia"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            java.lang.CharSequence r5 = replaceWithLink(r5, r15, r10)
            r6.messageText = r5
        L_0x0bf4:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = getMedia(r3)
            r2.media = r5
            if (r4 == 0) goto L_0x0c4f
            org.telegram.tgnet.TLRPC$TL_webPage r4 = new org.telegram.tgnet.TLRPC$TL_webPage
            r4.<init>()
            r5.webpage = r4
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            int r5 = org.telegram.messenger.R.string.EventLogOriginalCaption
            java.lang.String r9 = "EventLogOriginalCaption"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r4.site_name = r5
            java.lang.String r4 = r1.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            int r4 = org.telegram.messenger.R.string.EventLogOriginalCaptionEmpty
            java.lang.String r5 = "EventLogOriginalCaptionEmpty"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.description = r4
            goto L_0x0c4f
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            java.lang.String r5 = r1.message
            r4.description = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r1.entities
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            int r4 = org.telegram.messenger.R.string.EventLogEditedMessages
            java.lang.String r5 = "EventLogEditedMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            java.lang.CharSequence r4 = replaceWithLink(r4, r15, r10)
            r6.messageText = r4
            org.telegram.tgnet.TLRPC$MessageAction r4 = r3.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            if (r4 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r3.media = r1
            r2 = r3
        L_0x0c4f:
            r1 = 0
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            java.lang.String r4 = r3.message
            r2.message = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r3.entities
            r2.entities = r4
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r4.<init>()
            r2.media = r4
            org.telegram.tgnet.TLRPC$TL_webPage r5 = new org.telegram.tgnet.TLRPC$TL_webPage
            r5.<init>()
            r4.webpage = r5
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            int r5 = org.telegram.messenger.R.string.EventLogOriginalMessages
            java.lang.String r9 = "EventLogOriginalMessages"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r4.site_name = r5
            java.lang.String r4 = r1.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x0c8c
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            int r4 = org.telegram.messenger.R.string.EventLogOriginalCaptionEmpty
            java.lang.String r5 = "EventLogOriginalCaptionEmpty"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.description = r4
            goto L_0x0c4f
        L_0x0c8c:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            java.lang.String r5 = r1.message
            r4.description = r5
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r1.entities
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$ReplyMarkup r3 = r3.reply_markup
            r2.reply_markup = r3
            org.telegram.tgnet.TLRPC$MessageMedia r3 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r3 = r3.webpage
            if (r3 == 0) goto L_0x0ca8
            r4 = 10
            r3.flags = r4
            r3.display_url = r8
            r3.url = r8
        L_0x0ca8:
            r9 = r1
            r1 = r2
            goto L_0x1433
        L_0x0cac:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet
            if (r1 == 0) goto L_0x0ce0
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r1 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet) r1
            org.telegram.tgnet.TLRPC$InputStickerSet r1 = r1.new_stickerset
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet) r2
            org.telegram.tgnet.TLRPC$InputStickerSet r2 = r2.new_stickerset
            if (r1 == 0) goto L_0x0cd0
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            if (r1 == 0) goto L_0x0cc0
            goto L_0x0cd0
        L_0x0cc0:
            int r1 = org.telegram.messenger.R.string.EventLogChangedStickersSet
            java.lang.String r2 = "EventLogChangedStickersSet"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0cd0:
            int r1 = org.telegram.messenger.R.string.EventLogRemovedStickersSet
            java.lang.String r2 = "EventLogRemovedStickersSet"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0ce0:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation
            if (r1 == 0) goto L_0x0d16
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation) r2
            org.telegram.tgnet.TLRPC$ChannelLocation r1 = r2.new_value
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelLocationEmpty
            if (r2 == 0) goto L_0x0cfc
            int r1 = org.telegram.messenger.R.string.EventLogRemovedLocation
            java.lang.String r2 = "EventLogRemovedLocation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0cfc:
            org.telegram.tgnet.TLRPC$TL_channelLocation r1 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r1
            int r2 = org.telegram.messenger.R.string.EventLogChangedLocation
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = r1.address
            r3 = 0
            r4[r3] = r1
            java.lang.String r1 = "EventLogChangedLocation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0d16:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode
            r4 = 3600(0xe10, float:5.045E-42)
            if (r1 == 0) goto L_0x0d68
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode) r2
            int r1 = r2.new_value
            if (r1 != 0) goto L_0x0d32
            int r1 = org.telegram.messenger.R.string.EventLogToggledSlowmodeOff
            java.lang.String r2 = "EventLogToggledSlowmodeOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0d32:
            r2 = 60
            if (r1 >= r2) goto L_0x0d40
            r3 = 0
            java.lang.Object[] r2 = new java.lang.Object[r3]
            java.lang.String r4 = "Seconds"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r4, r1, r2)
            goto L_0x0d53
        L_0x0d40:
            r3 = 0
            if (r1 >= r4) goto L_0x0d4b
            int r1 = r1 / r2
            java.lang.Object[] r2 = new java.lang.Object[r3]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r13, r1, r2)
            goto L_0x0d53
        L_0x0d4b:
            int r1 = r1 / r2
            int r1 = r1 / r2
            java.lang.Object[] r2 = new java.lang.Object[r3]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1, r2)
        L_0x0d53:
            int r2 = org.telegram.messenger.R.string.EventLogToggledSlowmodeOn
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r5[r3] = r1
            java.lang.String r1 = "EventLogToggledSlowmodeOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r5)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0d68:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionStartGroupCall
            if (r1 == 0) goto L_0x0d9a
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r30)
            if (r1 == 0) goto L_0x0d8a
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x0d7a
            boolean r1 = r0.gigagroup
            if (r1 == 0) goto L_0x0d8a
        L_0x0d7a:
            int r1 = org.telegram.messenger.R.string.EventLogStartedLiveStream
            java.lang.String r2 = "EventLogStartedLiveStream"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0d8a:
            int r1 = org.telegram.messenger.R.string.EventLogStartedVoiceChat
            java.lang.String r2 = "EventLogStartedVoiceChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0d9a:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDiscardGroupCall
            if (r1 == 0) goto L_0x0dcc
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r30)
            if (r1 == 0) goto L_0x0dbc
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x0dac
            boolean r1 = r0.gigagroup
            if (r1 == 0) goto L_0x0dbc
        L_0x0dac:
            int r1 = org.telegram.messenger.R.string.EventLogEndedLiveStream
            java.lang.String r2 = "EventLogEndedLiveStream"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0dbc:
            int r1 = org.telegram.messenger.R.string.EventLogEndedVoiceChat
            java.lang.String r2 = "EventLogEndedVoiceChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0dcc:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantMute
            if (r1 == 0) goto L_0x0e14
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantMute r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantMute) r2
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r1 = getPeerId(r1)
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x0def
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r3.getUser(r1)
            goto L_0x0dfe
        L_0x0def:
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r1 = -r1
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
        L_0x0dfe:
            int r2 = org.telegram.messenger.R.string.EventLogVoiceChatMuted
            java.lang.String r3 = "EventLogVoiceChatMuted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r9, r1)
            r6.messageText = r1
            goto L_0x1431
        L_0x0e14:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantUnmute
            if (r1 == 0) goto L_0x0e5c
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantUnmute r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantUnmute) r2
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r1 = getPeerId(r1)
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x0e37
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r3.getUser(r1)
            goto L_0x0e46
        L_0x0e37:
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r1 = -r1
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
        L_0x0e46:
            int r2 = org.telegram.messenger.R.string.EventLogVoiceChatUnmuted
            java.lang.String r3 = "EventLogVoiceChatUnmuted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r9, r1)
            r6.messageText = r1
            goto L_0x1431
        L_0x0e5c:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleGroupCallSetting
            if (r1 == 0) goto L_0x0e86
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleGroupCallSetting r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleGroupCallSetting) r2
            boolean r1 = r2.join_muted
            if (r1 == 0) goto L_0x0e76
            int r1 = org.telegram.messenger.R.string.EventLogVoiceChatNotAllowedToSpeak
            java.lang.String r2 = "EventLogVoiceChatNotAllowedToSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0e76:
            int r1 = org.telegram.messenger.R.string.EventLogVoiceChatAllowedToSpeak
            java.lang.String r2 = "EventLogVoiceChatAllowedToSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0e86:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByInvite
            if (r1 == 0) goto L_0x0e9c
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByInvite r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByInvite) r2
            int r1 = org.telegram.messenger.R.string.ActionInviteUser
            java.lang.String r2 = "ActionInviteUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0e9c:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleNoForwards
            if (r1 == 0) goto L_0x0ef7
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleNoForwards r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleNoForwards) r2
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r30)
            if (r1 == 0) goto L_0x0eae
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x0eae
            r1 = 1
            goto L_0x0eaf
        L_0x0eae:
            r1 = 0
        L_0x0eaf:
            boolean r2 = r2.new_value
            if (r2 == 0) goto L_0x0ed5
            if (r1 == 0) goto L_0x0ec5
            int r1 = org.telegram.messenger.R.string.ActionForwardsRestrictedChannel
            java.lang.String r2 = "ActionForwardsRestrictedChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0ec5:
            int r1 = org.telegram.messenger.R.string.ActionForwardsRestrictedGroup
            java.lang.String r2 = "ActionForwardsRestrictedGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0ed5:
            if (r1 == 0) goto L_0x0ee7
            int r1 = org.telegram.messenger.R.string.ActionForwardsEnabledChannel
            java.lang.String r2 = "ActionForwardsEnabledChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0ee7:
            int r1 = org.telegram.messenger.R.string.ActionForwardsEnabledGroup
            java.lang.String r2 = "ActionForwardsEnabledGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x0ef7:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteDelete
            if (r1 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteDelete r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteDelete) r2
            int r1 = org.telegram.messenger.R.string.ActionDeletedInviteLinkClickable
            r3 = 0
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r3 = "ActionDeletedInviteLinkClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r2.invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r9, r2)
            r6.messageText = r1
            goto L_0x1431
        L_0x0var_:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteRevoke
            if (r1 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteRevoke r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteRevoke) r2
            int r1 = org.telegram.messenger.R.string.ActionRevokedInviteLinkClickable
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = r2.invite
            java.lang.String r3 = r3.link
            r5 = 0
            r4[r5] = r3
            java.lang.String r3 = "ActionRevokedInviteLinkClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r2.invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r9, r2)
            r6.messageText = r1
            goto L_0x1431
        L_0x0var_:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteEdit
            if (r1 == 0) goto L_0x0f8f
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteEdit r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteEdit) r2
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r2.prev_invite
            java.lang.String r1 = r1.link
            if (r1 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = r2.new_invite
            java.lang.String r3 = r3.link
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x0var_
            int r1 = org.telegram.messenger.R.string.ActionEditedInviteLinkToSameClickable
            r3 = 0
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r5 = "ActionEditedInviteLinkToSameClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r1, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0var_
        L_0x0var_:
            r3 = 0
            int r1 = org.telegram.messenger.R.string.ActionEditedInviteLinkClickable
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r3 = "ActionEditedInviteLinkClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
        L_0x0var_:
            java.lang.CharSequence r1 = r6.messageText
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = r2.prev_invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r9, r3)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r2.new_invite
            java.lang.String r3 = "un3"
            java.lang.CharSequence r1 = replaceWithLink(r1, r3, r2)
            r6.messageText = r1
            goto L_0x1431
        L_0x0f8f:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantVolume
            if (r1 == 0) goto L_0x0ffe
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantVolume r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantVolume) r2
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r3 = getPeerId(r1)
            r11 = 0
            int r1 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r1 <= 0) goto L_0x0fb2
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r3)
            goto L_0x0fc1
        L_0x0fb2:
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r3 = -r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)
        L_0x0fc1:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            int r2 = org.telegram.messenger.ChatObject.getParticipantVolume(r2)
            double r2 = (double) r2
            r4 = 4636737291354636288(0xNUM, double:100.0)
            java.lang.Double.isNaN(r2)
            double r2 = r2 / r4
            int r4 = org.telegram.messenger.R.string.ActionVolumeChanged
            r5 = 1
            java.lang.Object[] r11 = new java.lang.Object[r5]
            r12 = 0
            int r5 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r5 <= 0) goto L_0x0fe0
            r12 = 4607182418800017408(0x3ffNUM, double:1.0)
            double r2 = java.lang.Math.max(r2, r12)
            goto L_0x0fe2
        L_0x0fe0:
            r2 = 0
        L_0x0fe2:
            int r2 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r3 = 0
            r11[r3] = r2
            java.lang.String r2 = "ActionVolumeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r4, r11)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r9, r1)
            r6.messageText = r1
            goto L_0x1431
        L_0x0ffe:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL
            if (r1 == 0) goto L_0x1089
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL) r2
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x102e
            int r1 = r2.new_value
            if (r1 == 0) goto L_0x1022
            int r2 = org.telegram.messenger.R.string.ActionTTLChannelChanged
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatTTLString(r1)
            r3 = 0
            r4[r3] = r1
            java.lang.String r1 = "ActionTTLChannelChanged"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            r6.messageText = r1
            goto L_0x1431
        L_0x1022:
            int r1 = org.telegram.messenger.R.string.ActionTTLChannelDisabled
            java.lang.String r2 = "ActionTTLChannelDisabled"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x1431
        L_0x102e:
            int r1 = r2.new_value
            if (r1 != 0) goto L_0x1042
            int r1 = org.telegram.messenger.R.string.ActionTTLDisabled
            java.lang.String r2 = "ActionTTLDisabled"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x1042:
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r1 <= r2) goto L_0x1055
            r2 = 86400(0x15180, float:1.21072E-40)
            int r1 = r1 / r2
            r2 = 0
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r4 = "Days"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r4, r1, r3)
            goto L_0x1074
        L_0x1055:
            r2 = 0
            if (r1 < r4) goto L_0x1060
            int r1 = r1 / r4
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1, r3)
            goto L_0x1074
        L_0x1060:
            r3 = 60
            if (r1 < r3) goto L_0x106c
            int r1 = r1 / r3
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r13, r1, r3)
            goto L_0x1074
        L_0x106c:
            java.lang.Object[] r3 = new java.lang.Object[r2]
            java.lang.String r4 = "Seconds"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r4, r1, r3)
        L_0x1074:
            int r3 = org.telegram.messenger.R.string.ActionTTLChanged
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            r5[r2] = r1
            java.lang.String r1 = "ActionTTLChanged"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r5)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1431
        L_0x1089:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByRequest
            if (r1 == 0) goto L_0x10fd
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByRequest r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByRequest) r2
            org.telegram.tgnet.TLRPC$ExportedChatInvite r1 = r2.invite
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_chatInviteExported
            if (r3 == 0) goto L_0x10a1
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = (org.telegram.tgnet.TLRPC$TL_chatInviteExported) r1
            java.lang.String r1 = r1.link
            java.lang.String r3 = "https://t.me/+PublicChat"
            boolean r1 = r3.equals(r1)
            if (r1 != 0) goto L_0x10a7
        L_0x10a1:
            org.telegram.tgnet.TLRPC$ExportedChatInvite r1 = r2.invite
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_chatInvitePublicJoinRequests
            if (r1 == 0) goto L_0x10cd
        L_0x10a7:
            int r1 = org.telegram.messenger.R.string.JoinedViaRequestApproved
            java.lang.String r3 = "JoinedViaRequestApproved"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = r2.approved_by
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r3.getUser(r2)
            java.lang.CharSequence r1 = replaceWithLink(r1, r9, r2)
            r6.messageText = r1
            goto L_0x1431
        L_0x10cd:
            int r1 = org.telegram.messenger.R.string.JoinedViaInviteLinkApproved
            java.lang.String r3 = "JoinedViaInviteLinkApproved"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$ExportedChatInvite r3 = r2.invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r9, r3)
            r6.messageText = r1
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = r2.approved_by
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r3.getUser(r2)
            java.lang.String r3 = "un3"
            java.lang.CharSequence r1 = replaceWithLink(r1, r3, r2)
            r6.messageText = r1
            goto L_0x1431
        L_0x10fd:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionSendMessage
            if (r1 == 0) goto L_0x1115
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionSendMessage r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionSendMessage) r2
            org.telegram.tgnet.TLRPC$Message r1 = r2.message
            int r2 = org.telegram.messenger.R.string.EventLogSendMessages
            java.lang.String r3 = "EventLogSendMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            goto L_0x1432
        L_0x1115:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAvailableReactions
            if (r1 == 0) goto L_0x116b
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAvailableReactions r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAvailableReactions) r2
            org.telegram.tgnet.TLRPC$ChatReactions r1 = r2.prev_value
            java.lang.CharSequence r1 = r6.getStringFrom(r1)
            org.telegram.tgnet.TLRPC$ChatReactions r2 = r2.new_value
            java.lang.CharSequence r2 = r6.getStringFrom(r2)
            android.text.SpannableStringBuilder r3 = new android.text.SpannableStringBuilder
            int r4 = org.telegram.messenger.R.string.ActionReactionsChanged
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r9 = "**old**"
            r11 = 0
            r5[r11] = r9
            java.lang.String r9 = "**new**"
            r11 = 1
            r5[r11] = r9
            java.lang.String r9 = "ActionReactionsChanged"
            java.lang.String r4 = org.telegram.messenger.LocaleController.formatString(r9, r4, r5)
            java.lang.CharSequence r4 = replaceWithLink(r4, r15, r10)
            r3.<init>(r4)
            java.lang.String r4 = r3.toString()
            java.lang.String r5 = "**old**"
            int r4 = r4.indexOf(r5)
            if (r4 <= 0) goto L_0x1156
            int r5 = r4 + 7
            r3.replace(r4, r5, r1)
        L_0x1156:
            java.lang.String r1 = r3.toString()
            java.lang.String r4 = "**new**"
            int r1 = r1.indexOf(r4)
            if (r1 <= 0) goto L_0x1167
            int r4 = r1 + 7
            r3.replace(r1, r4, r2)
        L_0x1167:
            r6.messageText = r3
            goto L_0x1431
        L_0x116b:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "unsupported "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r7.action
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r6.messageText = r1
            goto L_0x1431
        L_0x1182:
            r8 = r14
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin
            if (r1 == 0) goto L_0x118e
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin) r2
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = r2.prev_participant
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r2.new_participant
            goto L_0x1194
        L_0x118e:
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan) r2
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = r2.prev_participant
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r2.new_participant
        L_0x1194:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message
            r3.<init>()
            r6.messageOwner = r3
            org.telegram.tgnet.TLRPC$Peer r3 = r1.peer
            long r3 = getPeerId(r3)
            r12 = 0
            int r5 = (r3 > r12 ? 1 : (r3 == r12 ? 0 : -1))
            if (r5 <= 0) goto L_0x11b6
            int r5 = r6.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r3 = r5.getUser(r3)
            goto L_0x11c5
        L_0x11b6:
            int r5 = r6.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            long r3 = -r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r3 = r5.getUser(r3)
        L_0x11c5:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r4 != 0) goto L_0x11f2
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r4 == 0) goto L_0x11f2
            int r1 = org.telegram.messenger.R.string.EventLogChangedOwnership
            java.lang.String r2 = "EventLogChangedOwnership"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            int r2 = r1.indexOf(r11)
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r5 = 1
            java.lang.Object[] r9 = new java.lang.Object[r5]
            org.telegram.tgnet.TLRPC$Message r5 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r5 = r5.entities
            java.lang.String r2 = r6.getUserName(r3, r5, r2)
            r3 = 0
            r9[r3] = r2
            java.lang.String r1 = java.lang.String.format(r1, r9)
            r4.<init>(r1)
            goto L_0x142b
        L_0x11f2:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r4 = r1.admin_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r2.admin_rights
            if (r4 != 0) goto L_0x11fd
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r4 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r4.<init>()
        L_0x11fd:
            if (r5 != 0) goto L_0x1204
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r5.<init>()
        L_0x1204:
            boolean r9 = r5.other
            if (r9 == 0) goto L_0x1211
            int r9 = org.telegram.messenger.R.string.EventLogPromotedNoRights
            java.lang.String r12 = "EventLogPromotedNoRights"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r12, r9)
            goto L_0x1219
        L_0x1211:
            int r9 = org.telegram.messenger.R.string.EventLogPromoted
            java.lang.String r12 = "EventLogPromoted"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r12, r9)
        L_0x1219:
            int r11 = r9.indexOf(r11)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r13 = 1
            java.lang.Object[] r14 = new java.lang.Object[r13]
            org.telegram.tgnet.TLRPC$Message r13 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r13 = r13.entities
            java.lang.String r3 = r6.getUserName(r3, r13, r11)
            r11 = 0
            r14[r11] = r3
            java.lang.String r3 = java.lang.String.format(r9, r14)
            r12.<init>(r3)
            java.lang.String r3 = "\n"
            r12.append(r3)
            java.lang.String r1 = r1.rank
            java.lang.String r3 = r2.rank
            boolean r1 = android.text.TextUtils.equals(r1, r3)
            if (r1 != 0) goto L_0x128d
            java.lang.String r1 = r2.rank
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x1268
            r1 = 10
            r12.append(r1)
            r3 = 45
            r12.append(r3)
            r9 = 32
            r12.append(r9)
            int r2 = org.telegram.messenger.R.string.EventLogPromotedRemovedTitle
            java.lang.String r11 = "EventLogPromotedRemovedTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r2)
            r12.append(r2)
            r1 = 43
            goto L_0x1291
        L_0x1268:
            r1 = 10
            r3 = 45
            r9 = 32
            r12.append(r1)
            r1 = 43
            r12.append(r1)
            r12.append(r9)
            int r9 = org.telegram.messenger.R.string.EventLogPromotedTitle
            r11 = 1
            java.lang.Object[] r13 = new java.lang.Object[r11]
            java.lang.String r2 = r2.rank
            r11 = 0
            r13[r11] = r2
            java.lang.String r2 = "EventLogPromotedTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r9, r13)
            r12.append(r2)
            goto L_0x1291
        L_0x128d:
            r1 = 43
            r3 = 45
        L_0x1291:
            boolean r2 = r4.change_info
            boolean r9 = r5.change_info
            if (r2 == r9) goto L_0x12c1
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.change_info
            if (r2 == 0) goto L_0x12a3
            r2 = 43
            goto L_0x12a5
        L_0x12a3:
            r2 = 45
        L_0x12a5:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x12b6
            int r2 = org.telegram.messenger.R.string.EventLogPromotedChangeGroupInfo
            java.lang.String r9 = "EventLogPromotedChangeGroupInfo"
            goto L_0x12ba
        L_0x12b6:
            int r2 = org.telegram.messenger.R.string.EventLogPromotedChangeChannelInfo
            java.lang.String r9 = "EventLogPromotedChangeChannelInfo"
        L_0x12ba:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x12c1:
            boolean r2 = r0.megagroup
            if (r2 != 0) goto L_0x1313
            boolean r2 = r4.post_messages
            boolean r9 = r5.post_messages
            if (r2 == r9) goto L_0x12ec
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.post_messages
            if (r2 == 0) goto L_0x12d7
            r2 = 43
            goto L_0x12d9
        L_0x12d7:
            r2 = 45
        L_0x12d9:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            int r2 = org.telegram.messenger.R.string.EventLogPromotedPostMessages
            java.lang.String r9 = "EventLogPromotedPostMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x12ec:
            boolean r2 = r4.edit_messages
            boolean r9 = r5.edit_messages
            if (r2 == r9) goto L_0x1313
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.edit_messages
            if (r2 == 0) goto L_0x12fe
            r2 = 43
            goto L_0x1300
        L_0x12fe:
            r2 = 45
        L_0x1300:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            int r2 = org.telegram.messenger.R.string.EventLogPromotedEditMessages
            java.lang.String r9 = "EventLogPromotedEditMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x1313:
            boolean r2 = r4.delete_messages
            boolean r9 = r5.delete_messages
            if (r2 == r9) goto L_0x133a
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.delete_messages
            if (r2 == 0) goto L_0x1325
            r2 = 43
            goto L_0x1327
        L_0x1325:
            r2 = 45
        L_0x1327:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            int r2 = org.telegram.messenger.R.string.EventLogPromotedDeleteMessages
            java.lang.String r9 = "EventLogPromotedDeleteMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x133a:
            boolean r2 = r4.add_admins
            boolean r9 = r5.add_admins
            if (r2 == r9) goto L_0x1361
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.add_admins
            if (r2 == 0) goto L_0x134c
            r2 = 43
            goto L_0x134e
        L_0x134c:
            r2 = 45
        L_0x134e:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            int r2 = org.telegram.messenger.R.string.EventLogPromotedAddAdmins
            java.lang.String r9 = "EventLogPromotedAddAdmins"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x1361:
            boolean r2 = r4.anonymous
            boolean r9 = r5.anonymous
            if (r2 == r9) goto L_0x1388
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.anonymous
            if (r2 == 0) goto L_0x1373
            r2 = 43
            goto L_0x1375
        L_0x1373:
            r2 = 45
        L_0x1375:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            int r2 = org.telegram.messenger.R.string.EventLogPromotedSendAnonymously
            java.lang.String r9 = "EventLogPromotedSendAnonymously"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x1388:
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x13da
            boolean r2 = r4.ban_users
            boolean r9 = r5.ban_users
            if (r2 == r9) goto L_0x13b3
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.ban_users
            if (r2 == 0) goto L_0x139e
            r2 = 43
            goto L_0x13a0
        L_0x139e:
            r2 = 45
        L_0x13a0:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            int r2 = org.telegram.messenger.R.string.EventLogPromotedBanUsers
            java.lang.String r9 = "EventLogPromotedBanUsers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x13b3:
            boolean r2 = r4.manage_call
            boolean r9 = r5.manage_call
            if (r2 == r9) goto L_0x13da
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.manage_call
            if (r2 == 0) goto L_0x13c5
            r2 = 43
            goto L_0x13c7
        L_0x13c5:
            r2 = 45
        L_0x13c7:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            int r2 = org.telegram.messenger.R.string.EventLogPromotedManageCall
            java.lang.String r9 = "EventLogPromotedManageCall"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x13da:
            boolean r2 = r4.invite_users
            boolean r9 = r5.invite_users
            if (r2 == r9) goto L_0x1401
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.invite_users
            if (r2 == 0) goto L_0x13ec
            r2 = 43
            goto L_0x13ee
        L_0x13ec:
            r2 = 45
        L_0x13ee:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            int r2 = org.telegram.messenger.R.string.EventLogPromotedAddUsers
            java.lang.String r9 = "EventLogPromotedAddUsers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x1401:
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x142a
            boolean r2 = r4.pin_messages
            boolean r4 = r5.pin_messages
            if (r2 == r4) goto L_0x142a
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.pin_messages
            if (r2 == 0) goto L_0x1415
            goto L_0x1417
        L_0x1415:
            r1 = 45
        L_0x1417:
            r12.append(r1)
            r1 = 32
            r12.append(r1)
            int r1 = org.telegram.messenger.R.string.EventLogPromotedPinMessages
            java.lang.String r2 = "EventLogPromotedPinMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r12.append(r1)
        L_0x142a:
            r4 = r12
        L_0x142b:
            java.lang.String r1 = r4.toString()
            r6.messageText = r1
        L_0x1431:
            r1 = 0
        L_0x1432:
            r9 = 0
        L_0x1433:
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            if (r2 != 0) goto L_0x143e
            org.telegram.tgnet.TLRPC$TL_messageService r2 = new org.telegram.tgnet.TLRPC$TL_messageService
            r2.<init>()
            r6.messageOwner = r2
        L_0x143e:
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            java.lang.CharSequence r3 = r6.messageText
            java.lang.String r3 = r3.toString()
            r2.message = r3
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$TL_peerUser r3 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r3.<init>()
            r2.from_id = r3
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r3 = r2.from_id
            long r4 = r7.user_id
            r3.user_id = r4
            int r3 = r7.date
            r2.date = r3
            r3 = 0
            r4 = r31[r3]
            int r5 = r4 + 1
            r31[r3] = r5
            r2.id = r4
            long r4 = r7.id
            r6.eventId = r4
            r2.out = r3
            org.telegram.tgnet.TLRPC$TL_peerChannel r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r4.<init>()
            r2.peer_id = r4
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r2.peer_id
            long r11 = r0.id
            r4.channel_id = r11
            r2.unread = r3
            org.telegram.messenger.MediaController r11 = org.telegram.messenger.MediaController.getInstance()
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r0 == 0) goto L_0x1486
            r1 = 0
        L_0x1486:
            if (r1 == 0) goto L_0x1504
            r1.out = r3
            r0 = r31[r3]
            int r2 = r0 + 1
            r31[r3] = r2
            r1.id = r0
            int r0 = r1.flags
            r0 = r0 & -9
            r1.flags = r0
            r2 = 0
            r1.reply_to = r2
            r2 = -32769(0xffffffffffff7fff, float:NaN)
            r0 = r0 & r2
            r1.flags = r0
            org.telegram.messenger.MessageObject r12 = new org.telegram.messenger.MessageObject
            int r0 = r6.currentAccount
            r19 = 0
            r20 = 0
            r21 = 1
            r22 = 1
            long r2 = r6.eventId
            r16 = r12
            r17 = r0
            r18 = r1
            r23 = r2
            r16.<init>((int) r17, (org.telegram.tgnet.TLRPC$Message) r18, (java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC$User>) r19, (java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC$Chat>) r20, (boolean) r21, (boolean) r22, (long) r23)
            int r0 = r12.contentType
            if (r0 < 0) goto L_0x14f4
            boolean r0 = r11.isPlayingMessage(r12)
            if (r0 == 0) goto L_0x14d0
            org.telegram.messenger.MessageObject r0 = r11.getPlayingMessageObject()
            float r1 = r0.audioProgress
            r12.audioProgress = r1
            int r0 = r0.audioProgressSec
            r12.audioProgressSec = r0
        L_0x14d0:
            int r1 = r6.currentAccount
            r0 = r25
            r2 = r27
            r3 = r28
            r4 = r29
            r5 = r32
            r0.createDateArray(r1, r2, r3, r4, r5)
            if (r32 == 0) goto L_0x14e8
            r13 = r28
            r0 = 0
            r13.add(r0, r12)
            goto L_0x14f9
        L_0x14e8:
            r13 = r28
            int r0 = r28.size()
            r1 = 1
            int r0 = r0 - r1
            r13.add(r0, r12)
            goto L_0x14f9
        L_0x14f4:
            r13 = r28
            r0 = -1
            r6.contentType = r0
        L_0x14f9:
            if (r9 == 0) goto L_0x1506
            r12.webPageDescriptionEntities = r9
            r9 = 0
            r12.linkDescription = r9
            r12.generateLinkDescription()
            goto L_0x1507
        L_0x1504:
            r13 = r28
        L_0x1506:
            r9 = 0
        L_0x1507:
            int r0 = r6.contentType
            if (r0 < 0) goto L_0x159e
            int r1 = r6.currentAccount
            r0 = r25
            r2 = r27
            r3 = r28
            r4 = r29
            r5 = r32
            r0.createDateArray(r1, r2, r3, r4, r5)
            if (r32 == 0) goto L_0x1521
            r0 = 0
            r13.add(r0, r6)
            goto L_0x152a
        L_0x1521:
            int r0 = r28.size()
            r1 = 1
            int r0 = r0 - r1
            r13.add(r0, r6)
        L_0x152a:
            java.lang.CharSequence r0 = r6.messageText
            if (r0 != 0) goto L_0x1530
            r6.messageText = r8
        L_0x1530:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x153d
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint
            goto L_0x153f
        L_0x153d:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
        L_0x153f:
            boolean r1 = r25.allowsBigEmoji()
            if (r1 == 0) goto L_0x1548
            r1 = 1
            int[] r9 = new int[r1]
        L_0x1548:
            java.lang.CharSequence r1 = r6.messageText
            android.graphics.Paint$FontMetricsInt r2 = r0.getFontMetricsInt()
            r3 = 1101004800(0x41a00000, float:20.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 0
            java.lang.CharSequence r1 = org.telegram.messenger.Emoji.replaceEmoji(r1, r2, r3, r4, r9)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r2 = r2.entities
            android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
            android.text.Spannable r0 = replaceAnimatedEmoji(r1, r2, r0)
            r6.messageText = r0
            if (r9 == 0) goto L_0x1573
            r1 = r9[r4]
            r2 = 1
            if (r1 <= r2) goto L_0x1573
            r6.replaceEmojiToLottieFrame(r0, r9)
        L_0x1573:
            r6.checkEmojiOnly((int[]) r9)
            r25.setType()
            r25.measureInlineBotButtons()
            r25.generateCaption()
            boolean r0 = r11.isPlayingMessage(r6)
            if (r0 == 0) goto L_0x1591
            org.telegram.messenger.MessageObject r0 = r11.getPlayingMessageObject()
            float r1 = r0.audioProgress
            r6.audioProgress = r1
            int r0 = r0.audioProgressSec
            r6.audioProgressSec = r0
        L_0x1591:
            r6.generateLayout(r10)
            r0 = 1
            r6.layoutCreated = r0
            r0 = 0
            r6.generateThumbs(r0)
            r25.checkMediaExistance()
        L_0x159e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.<init>(int, org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent, java.util.ArrayList, java.util.HashMap, org.telegram.tgnet.TLRPC$Chat, int[], boolean):void");
    }

    private CharSequence getStringFrom(TLRPC$ChatReactions tLRPC$ChatReactions) {
        if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsAll) {
            return LocaleController.getString("AllReactions", R.string.AllReactions);
        }
        if (tLRPC$ChatReactions instanceof TLRPC$TL_chatReactionsSome) {
            TLRPC$TL_chatReactionsSome tLRPC$TL_chatReactionsSome = (TLRPC$TL_chatReactionsSome) tLRPC$ChatReactions;
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            for (int i = 0; i < tLRPC$TL_chatReactionsSome.reactions.size(); i++) {
                if (i != 0) {
                    spannableStringBuilder.append(", ");
                }
                spannableStringBuilder.append(ReactionsUtils.reactionToCharSequence(tLRPC$TL_chatReactionsSome.reactions.get(i)));
            }
        }
        return LocaleController.getString("NoReactions", R.string.NoReactions);
    }

    private String getUserName(TLObject tLObject, ArrayList<TLRPC$MessageEntity> arrayList, int i) {
        String str;
        String str2;
        long j;
        long j2;
        String str3;
        String str4;
        if (tLObject == null) {
            str = null;
            j = 0;
            str2 = "";
        } else {
            if (tLObject instanceof TLRPC$User) {
                TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
                if (tLRPC$User.deleted) {
                    str4 = LocaleController.getString("HiddenName", R.string.HiddenName);
                } else {
                    str4 = ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name);
                }
                str2 = str4;
                str3 = tLRPC$User.username;
                j2 = tLRPC$User.id;
            } else {
                TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
                str2 = tLRPC$Chat.title;
                str3 = tLRPC$Chat.username;
                j2 = -tLRPC$Chat.id;
            }
            str = str3;
            j = j2;
        }
        if (i >= 0) {
            TLRPC$TL_messageEntityMentionName tLRPC$TL_messageEntityMentionName = new TLRPC$TL_messageEntityMentionName();
            tLRPC$TL_messageEntityMentionName.user_id = j;
            tLRPC$TL_messageEntityMentionName.offset = i;
            tLRPC$TL_messageEntityMentionName.length = str2.length();
            arrayList.add(tLRPC$TL_messageEntityMentionName);
        }
        if (TextUtils.isEmpty(str)) {
            return str2;
        }
        if (i >= 0) {
            TLRPC$TL_messageEntityMentionName tLRPC$TL_messageEntityMentionName2 = new TLRPC$TL_messageEntityMentionName();
            tLRPC$TL_messageEntityMentionName2.user_id = j;
            tLRPC$TL_messageEntityMentionName2.offset = i + str2.length() + 2;
            tLRPC$TL_messageEntityMentionName2.length = str.length() + 1;
            arrayList.add(tLRPC$TL_messageEntityMentionName2);
        }
        return String.format("%1$s (@%2$s)", new Object[]{str2, str});
    }

    public void applyNewText() {
        applyNewText(this.messageOwner.message);
    }

    public void applyNewText(CharSequence charSequence) {
        TextPaint textPaint;
        if (!TextUtils.isEmpty(charSequence)) {
            int[] iArr = null;
            TLRPC$User user = isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id)) : null;
            this.messageText = charSequence;
            if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGame) {
                textPaint = Theme.chat_msgGameTextPaint;
            } else {
                textPaint = Theme.chat_msgTextPaint;
            }
            if (allowsBigEmoji()) {
                iArr = new int[1];
            }
            CharSequence replaceEmoji = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            this.messageText = replaceEmoji;
            Spannable replaceAnimatedEmoji = replaceAnimatedEmoji(replaceEmoji, this.messageOwner.entities, textPaint.getFontMetricsInt());
            this.messageText = replaceAnimatedEmoji;
            if (iArr != null && iArr[0] > 1) {
                replaceEmojiToLottieFrame(replaceAnimatedEmoji, iArr);
            }
            checkEmojiOnly(iArr);
            generateLayout(user);
            setType();
        }
    }

    private boolean allowsBigEmoji() {
        TLRPC$Peer tLRPC$Peer;
        if (!SharedConfig.allowBigEmoji) {
            return false;
        }
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message == null || (tLRPC$Peer = tLRPC$Message.peer_id) == null || (tLRPC$Peer.channel_id == 0 && tLRPC$Peer.chat_id == 0)) {
            return true;
        }
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        TLRPC$Peer tLRPC$Peer2 = this.messageOwner.peer_id;
        long j = tLRPC$Peer2.channel_id;
        if (j == 0) {
            j = tLRPC$Peer2.chat_id;
        }
        TLRPC$Chat chat = instance.getChat(Long.valueOf(j));
        if ((chat == null || !chat.gigagroup) && ChatObject.isActionBanned(chat, 8) && !ChatObject.hasAdminRights(chat)) {
            return false;
        }
        return true;
    }

    public void generateGameMessageText(TLRPC$User tLRPC$User) {
        if (tLRPC$User == null && isFromUser()) {
            tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
        }
        TLRPC$TL_game tLRPC$TL_game = null;
        if (!(this.replyMessageObject == null || getMedia(this.messageOwner) == null || getMedia(this.messageOwner).game == null)) {
            tLRPC$TL_game = getMedia(this.messageOwner).game;
        }
        if (tLRPC$TL_game != null) {
            if (tLRPC$User == null || tLRPC$User.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", R.string.ActionUserScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0])), "un1", tLRPC$User);
            } else {
                this.messageText = LocaleController.formatString("ActionYouScoredInGame", R.string.ActionYouScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0]));
            }
            this.messageText = replaceWithLink(this.messageText, "un2", tLRPC$TL_game);
        } else if (tLRPC$User == null || tLRPC$User.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", R.string.ActionUserScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0])), "un1", tLRPC$User);
        } else {
            this.messageText = LocaleController.formatString("ActionYouScored", R.string.ActionYouScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0]));
        }
    }

    public boolean hasValidReplyMessageObject() {
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject != null) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            return !(tLRPC$Message instanceof TLRPC$TL_messageEmpty) && !(tLRPC$Message.action instanceof TLRPC$TL_messageActionHistoryClear);
        }
    }

    public void generatePaymentSentMessageText(TLRPC$User tLRPC$User) {
        String str;
        if (tLRPC$User == null) {
            tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(getDialogId()));
        }
        String firstName = tLRPC$User != null ? UserObject.getFirstName(tLRPC$User) : "";
        try {
            LocaleController instance = LocaleController.getInstance();
            TLRPC$MessageAction tLRPC$MessageAction = this.messageOwner.action;
            str = instance.formatCurrencyString(tLRPC$MessageAction.total_amount, tLRPC$MessageAction.currency);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            str = "<error>";
        }
        if (this.replyMessageObject != null && (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaInvoice)) {
            TLRPC$Message tLRPC$Message = this.messageOwner;
            if (tLRPC$Message.action.recurring_init) {
                this.messageText = LocaleController.formatString(R.string.PaymentSuccessfullyPaidRecurrent, str, firstName, getMedia(tLRPC$Message).title);
            } else {
                this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", R.string.PaymentSuccessfullyPaid, str, firstName, getMedia(tLRPC$Message).title);
            }
        } else if (this.messageOwner.action.recurring_init) {
            this.messageText = LocaleController.formatString(R.string.PaymentSuccessfullyPaidNoItemRecurrent, str, firstName);
        } else {
            this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", R.string.PaymentSuccessfullyPaidNoItem, str, firstName);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:124:0x02a4, code lost:
        if (r0 == null) goto L_0x02af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x02a6, code lost:
        r0 = new android.text.SpannableStringBuilder(r0).append("...");
     */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Incorrect type for immutable var: ssa=org.telegram.tgnet.TLRPC$User, code=org.telegram.tgnet.TLRPC$Chat, for r8v0, types: [org.telegram.tgnet.TLRPC$User] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generatePinMessageText(org.telegram.tgnet.TLRPC$Chat r8, org.telegram.tgnet.TLRPC$Chat r9) {
        /*
            r7 = this;
            if (r8 != 0) goto L_0x0055
            if (r9 != 0) goto L_0x0055
            boolean r0 = r7.isFromUser()
            if (r0 == 0) goto L_0x001e
            int r8 = r7.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.from_id
            long r0 = r0.user_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r8 = r8.getUser(r0)
        L_0x001e:
            if (r8 != 0) goto L_0x0055
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r1 == 0) goto L_0x003d
            int r9 = r7.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r0 = r0.channel_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getChat(r0)
            goto L_0x0055
        L_0x003d:
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r0 == 0) goto L_0x0055
            int r9 = r7.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r0 = r0.chat_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r9 = r9.getChat(r0)
        L_0x0055:
            org.telegram.messenger.MessageObject r0 = r7.replyMessageObject
            java.lang.String r1 = "ActionPinnedNoText"
            java.lang.String r2 = "un1"
            if (r0 == 0) goto L_0x02db
            org.telegram.tgnet.TLRPC$Message r3 = r0.messageOwner
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r4 != 0) goto L_0x02db
            org.telegram.tgnet.TLRPC$MessageAction r3 = r3.action
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r3 == 0) goto L_0x006b
            goto L_0x02db
        L_0x006b:
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0085
            int r0 = org.telegram.messenger.R.string.ActionPinnedMusic
            java.lang.String r1 = "ActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x007c
            goto L_0x007d
        L_0x007c:
            r8 = r9
        L_0x007d:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x0085:
            org.telegram.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x00a1
            int r0 = org.telegram.messenger.R.string.ActionPinnedVideo
            java.lang.String r1 = "ActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x0098
            goto L_0x0099
        L_0x0098:
            r8 = r9
        L_0x0099:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x00a1:
            org.telegram.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isGif()
            if (r0 == 0) goto L_0x00bd
            int r0 = org.telegram.messenger.R.string.ActionPinnedGif
            java.lang.String r1 = "ActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x00b4
            goto L_0x00b5
        L_0x00b4:
            r8 = r9
        L_0x00b5:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x00bd:
            org.telegram.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x00d9
            int r0 = org.telegram.messenger.R.string.ActionPinnedVoice
            java.lang.String r1 = "ActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x00d0
            goto L_0x00d1
        L_0x00d0:
            r8 = r9
        L_0x00d1:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x00d9:
            org.telegram.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isRoundVideo()
            if (r0 == 0) goto L_0x00f5
            int r0 = org.telegram.messenger.R.string.ActionPinnedRound
            java.lang.String r1 = "ActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x00ec
            goto L_0x00ed
        L_0x00ec:
            r8 = r9
        L_0x00ed:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x00f5:
            org.telegram.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isSticker()
            if (r0 != 0) goto L_0x0105
            org.telegram.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isAnimatedSticker()
            if (r0 == 0) goto L_0x0121
        L_0x0105:
            org.telegram.messenger.MessageObject r0 = r7.replyMessageObject
            boolean r0 = r0.isAnimatedEmoji()
            if (r0 != 0) goto L_0x0121
            int r0 = org.telegram.messenger.R.string.ActionPinnedSticker
            java.lang.String r1 = "ActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x0118
            goto L_0x0119
        L_0x0118:
            r8 = r9
        L_0x0119:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x0121:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r0 == 0) goto L_0x013f
            int r0 = org.telegram.messenger.R.string.ActionPinnedFile
            java.lang.String r1 = "ActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x0136
            goto L_0x0137
        L_0x0136:
            r8 = r9
        L_0x0137:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x013f:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r0 == 0) goto L_0x015d
            int r0 = org.telegram.messenger.R.string.ActionPinnedGeo
            java.lang.String r1 = "ActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x0154
            goto L_0x0155
        L_0x0154:
            r8 = r9
        L_0x0155:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x015d:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r0 == 0) goto L_0x017b
            int r0 = org.telegram.messenger.R.string.ActionPinnedGeoLive
            java.lang.String r1 = "ActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x0172
            goto L_0x0173
        L_0x0172:
            r8 = r9
        L_0x0173:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x017b:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r0 == 0) goto L_0x0199
            int r0 = org.telegram.messenger.R.string.ActionPinnedContact
            java.lang.String r1 = "ActionPinnedContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x0190
            goto L_0x0191
        L_0x0190:
            r8 = r9
        L_0x0191:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x0199:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x01d9
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x01c5
            int r0 = org.telegram.messenger.R.string.ActionPinnedQuiz
            java.lang.String r1 = "ActionPinnedQuiz"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x01bc
            goto L_0x01bd
        L_0x01bc:
            r8 = r9
        L_0x01bd:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x01c5:
            int r0 = org.telegram.messenger.R.string.ActionPinnedPoll
            java.lang.String r1 = "ActionPinnedPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x01d0
            goto L_0x01d1
        L_0x01d0:
            r8 = r9
        L_0x01d1:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x01d9:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x01f7
            int r0 = org.telegram.messenger.R.string.ActionPinnedPhoto
            java.lang.String r1 = "ActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x01ee
            goto L_0x01ef
        L_0x01ee:
            r8 = r9
        L_0x01ef:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x01f7:
            org.telegram.tgnet.TLRPC$Message r0 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            r3 = 1101004800(0x41a00000, float:20.0)
            r4 = 1
            r5 = 0
            if (r0 == 0) goto L_0x0248
            int r0 = org.telegram.messenger.R.string.ActionPinnedGame
            java.lang.Object[] r1 = new java.lang.Object[r4]
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r6 = "🎮 "
            r4.append(r6)
            org.telegram.tgnet.TLRPC$Message r6 = r7.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r6 = getMedia(r6)
            org.telegram.tgnet.TLRPC$TL_game r6 = r6.game
            java.lang.String r6 = r6.title
            r4.append(r6)
            java.lang.String r4 = r4.toString()
            r1[r5] = r4
            java.lang.String r4 = "ActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r4, r0, r1)
            if (r8 == 0) goto L_0x022f
            goto L_0x0230
        L_0x022f:
            r8 = r9
        L_0x0230:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            android.text.TextPaint r9 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r9 = r9.getFontMetricsInt()
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.CharSequence r8 = org.telegram.messenger.Emoji.replaceEmoji(r8, r9, r0, r5)
            r7.messageText = r8
            goto L_0x02eb
        L_0x0248:
            org.telegram.messenger.MessageObject r0 = r7.replyMessageObject
            java.lang.CharSequence r0 = r0.messageText
            if (r0 == 0) goto L_0x02ca
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x02ca
            org.telegram.messenger.MessageObject r0 = r7.replyMessageObject
            java.lang.CharSequence r0 = r0.messageText
            java.lang.CharSequence r0 = org.telegram.ui.Components.AnimatedEmojiSpan.cloneSpans(r0)
            int r1 = r0.length()
            r6 = 20
            if (r1 <= r6) goto L_0x026a
            java.lang.CharSequence r0 = r0.subSequence(r5, r6)
            r1 = 1
            goto L_0x026b
        L_0x026a:
            r1 = 0
        L_0x026b:
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r6, r3, r5)
            org.telegram.messenger.MessageObject r3 = r7.replyMessageObject
            if (r3 == 0) goto L_0x028d
            org.telegram.tgnet.TLRPC$Message r3 = r3.messageOwner
            if (r3 == 0) goto L_0x028d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r3 = r3.entities
            android.text.TextPaint r6 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r6 = r6.getFontMetricsInt()
            android.text.Spannable r0 = replaceAnimatedEmoji(r0, r3, r6)
        L_0x028d:
            org.telegram.messenger.MessageObject r3 = r7.replyMessageObject
            r6 = r0
            android.text.Spannable r6 = (android.text.Spannable) r6
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r3, r6)
            if (r1 == 0) goto L_0x02af
            boolean r1 = r0 instanceof android.text.SpannableStringBuilder
            java.lang.String r3 = "..."
            if (r1 == 0) goto L_0x02a4
            r1 = r0
            android.text.SpannableStringBuilder r1 = (android.text.SpannableStringBuilder) r1
            r1.append(r3)
            goto L_0x02af
        L_0x02a4:
            if (r0 == 0) goto L_0x02af
            android.text.SpannableStringBuilder r1 = new android.text.SpannableStringBuilder
            r1.<init>(r0)
            android.text.SpannableStringBuilder r0 = r1.append(r3)
        L_0x02af:
            int r1 = org.telegram.messenger.R.string.ActionPinnedText
            java.lang.String r3 = "ActionPinnedText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            java.lang.CharSequence[] r3 = new java.lang.CharSequence[r4]
            r3[r5] = r0
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r1, r3)
            if (r8 == 0) goto L_0x02c2
            goto L_0x02c3
        L_0x02c2:
            r8 = r9
        L_0x02c3:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x02ca:
            int r0 = org.telegram.messenger.R.string.ActionPinnedNoText
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x02d3
            goto L_0x02d4
        L_0x02d3:
            r8 = r9
        L_0x02d4:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
            goto L_0x02eb
        L_0x02db:
            int r0 = org.telegram.messenger.R.string.ActionPinnedNoText
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r8 == 0) goto L_0x02e4
            goto L_0x02e5
        L_0x02e4:
            r8 = r9
        L_0x02e5:
            java.lang.CharSequence r8 = replaceWithLink(r0, r2, r8)
            r7.messageText = r8
        L_0x02eb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generatePinMessageText(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat):void");
    }

    public static void updateReactions(TLRPC$Message tLRPC$Message, TLRPC$TL_messageReactions tLRPC$TL_messageReactions) {
        if (tLRPC$Message != null && tLRPC$TL_messageReactions != null) {
            TLRPC$TL_messageReactions tLRPC$TL_messageReactions2 = tLRPC$Message.reactions;
            if (tLRPC$TL_messageReactions2 != null) {
                int size = tLRPC$TL_messageReactions2.results.size();
                boolean z = false;
                for (int i = 0; i < size; i++) {
                    TLRPC$ReactionCount tLRPC$ReactionCount = tLRPC$Message.reactions.results.get(i);
                    int size2 = tLRPC$TL_messageReactions.results.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        TLRPC$ReactionCount tLRPC$ReactionCount2 = tLRPC$TL_messageReactions.results.get(i2);
                        if (ReactionsLayoutInBubble.equalsTLReaction(tLRPC$ReactionCount.reaction, tLRPC$ReactionCount2.reaction)) {
                            if (!z && tLRPC$TL_messageReactions.min && tLRPC$ReactionCount.chosen) {
                                tLRPC$ReactionCount2.chosen = true;
                                z = true;
                            }
                            tLRPC$ReactionCount2.lastDrawnPosition = tLRPC$ReactionCount.lastDrawnPosition;
                        }
                    }
                    if (tLRPC$ReactionCount.chosen) {
                        z = true;
                    }
                }
            }
            tLRPC$Message.reactions = tLRPC$TL_messageReactions;
            tLRPC$Message.flags |= 1048576;
        }
    }

    public boolean hasReactions() {
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions = this.messageOwner.reactions;
        return tLRPC$TL_messageReactions != null && !tLRPC$TL_messageReactions.results.isEmpty();
    }

    public static void updatePollResults(TLRPC$TL_messageMediaPoll tLRPC$TL_messageMediaPoll, TLRPC$PollResults tLRPC$PollResults) {
        byte[] bArr;
        ArrayList arrayList;
        ArrayList<TLRPC$TL_pollAnswerVoters> arrayList2;
        if (tLRPC$TL_messageMediaPoll != null && tLRPC$PollResults != null) {
            if ((tLRPC$PollResults.flags & 2) != 0) {
                if (!tLRPC$PollResults.min || (arrayList2 = tLRPC$TL_messageMediaPoll.results.results) == null) {
                    arrayList = null;
                    bArr = null;
                } else {
                    int size = arrayList2.size();
                    arrayList = null;
                    bArr = null;
                    for (int i = 0; i < size; i++) {
                        TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters = tLRPC$TL_messageMediaPoll.results.results.get(i);
                        if (tLRPC$TL_pollAnswerVoters.chosen) {
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                            }
                            arrayList.add(tLRPC$TL_pollAnswerVoters.option);
                        }
                        if (tLRPC$TL_pollAnswerVoters.correct) {
                            bArr = tLRPC$TL_pollAnswerVoters.option;
                        }
                    }
                }
                TLRPC$PollResults tLRPC$PollResults2 = tLRPC$TL_messageMediaPoll.results;
                ArrayList<TLRPC$TL_pollAnswerVoters> arrayList3 = tLRPC$PollResults.results;
                tLRPC$PollResults2.results = arrayList3;
                if (arrayList != null || bArr != null) {
                    int size2 = arrayList3.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        TLRPC$TL_pollAnswerVoters tLRPC$TL_pollAnswerVoters2 = tLRPC$TL_messageMediaPoll.results.results.get(i2);
                        if (arrayList != null) {
                            int size3 = arrayList.size();
                            int i3 = 0;
                            while (true) {
                                if (i3 >= size3) {
                                    break;
                                } else if (Arrays.equals(tLRPC$TL_pollAnswerVoters2.option, (byte[]) arrayList.get(i3))) {
                                    tLRPC$TL_pollAnswerVoters2.chosen = true;
                                    arrayList.remove(i3);
                                    break;
                                } else {
                                    i3++;
                                }
                            }
                            if (arrayList.isEmpty()) {
                                arrayList = null;
                            }
                        }
                        if (bArr != null && Arrays.equals(tLRPC$TL_pollAnswerVoters2.option, bArr)) {
                            tLRPC$TL_pollAnswerVoters2.correct = true;
                            bArr = null;
                        }
                        if (arrayList == null && bArr == null) {
                            break;
                        }
                    }
                }
                tLRPC$TL_messageMediaPoll.results.flags |= 2;
            }
            if ((tLRPC$PollResults.flags & 4) != 0) {
                TLRPC$PollResults tLRPC$PollResults3 = tLRPC$TL_messageMediaPoll.results;
                tLRPC$PollResults3.total_voters = tLRPC$PollResults.total_voters;
                tLRPC$PollResults3.flags |= 4;
            }
            if ((tLRPC$PollResults.flags & 8) != 0) {
                TLRPC$PollResults tLRPC$PollResults4 = tLRPC$TL_messageMediaPoll.results;
                tLRPC$PollResults4.recent_voters = tLRPC$PollResults.recent_voters;
                tLRPC$PollResults4.flags |= 8;
            }
            if ((tLRPC$PollResults.flags & 16) != 0) {
                TLRPC$PollResults tLRPC$PollResults5 = tLRPC$TL_messageMediaPoll.results;
                tLRPC$PollResults5.solution = tLRPC$PollResults.solution;
                tLRPC$PollResults5.solution_entities = tLRPC$PollResults.solution_entities;
                tLRPC$PollResults5.flags |= 16;
            }
        }
    }

    public void loadAnimatedEmojiDocument() {
        if (this.emojiAnimatedSticker == null && this.emojiAnimatedStickerId != null && !this.emojiAnimatedStickerLoading) {
            this.emojiAnimatedStickerLoading = true;
            AnimatedEmojiDrawable.getDocumentFetcher(this.currentAccount).fetchDocument(this.emojiAnimatedStickerId.longValue(), new MessageObject$$ExternalSyntheticLambda3(this));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAnimatedEmojiDocument$1(TLRPC$Document tLRPC$Document) {
        AndroidUtilities.runOnUIThread(new MessageObject$$ExternalSyntheticLambda0(this, tLRPC$Document));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$loadAnimatedEmojiDocument$0(TLRPC$Document tLRPC$Document) {
        this.emojiAnimatedSticker = tLRPC$Document;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.animatedEmojiDocumentLoaded, this);
    }

    public boolean isPollClosed() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC$TL_messageMediaPoll) getMedia(this.messageOwner)).poll.closed;
    }

    public boolean isQuiz() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC$TL_messageMediaPoll) getMedia(this.messageOwner)).poll.quiz;
    }

    public boolean isPublicPoll() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC$TL_messageMediaPoll) getMedia(this.messageOwner)).poll.public_voters;
    }

    public boolean isPoll() {
        return this.type == 17;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) getMedia(r5.messageOwner);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean canUnvote() {
        /*
            r5 = this;
            int r0 = r5.type
            r1 = 0
            r2 = 17
            if (r0 == r2) goto L_0x0008
            return r1
        L_0x0008:
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            if (r2 == 0) goto L_0x0041
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x0041
            org.telegram.tgnet.TLRPC$Poll r2 = r0.poll
            boolean r2 = r2.quiz
            if (r2 == 0) goto L_0x0023
            goto L_0x0041
        L_0x0023:
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            int r2 = r2.size()
            r3 = 0
        L_0x002c:
            if (r3 >= r2) goto L_0x0041
            org.telegram.tgnet.TLRPC$PollResults r4 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r4 = r4.results
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r4 = (org.telegram.tgnet.TLRPC$TL_pollAnswerVoters) r4
            boolean r4 = r4.chosen
            if (r4 == 0) goto L_0x003e
            r0 = 1
            return r0
        L_0x003e:
            int r3 = r3 + 1
            goto L_0x002c
        L_0x0041:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.canUnvote():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) getMedia(r5.messageOwner);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isVoted() {
        /*
            r5 = this;
            int r0 = r5.type
            r1 = 0
            r2 = 17
            if (r0 == r2) goto L_0x0008
            return r1
        L_0x0008:
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            if (r2 == 0) goto L_0x003b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x001d
            goto L_0x003b
        L_0x001d:
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            int r2 = r2.size()
            r3 = 0
        L_0x0026:
            if (r3 >= r2) goto L_0x003b
            org.telegram.tgnet.TLRPC$PollResults r4 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r4 = r4.results
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r4 = (org.telegram.tgnet.TLRPC$TL_pollAnswerVoters) r4
            boolean r4 = r4.chosen
            if (r4 == 0) goto L_0x0038
            r0 = 1
            return r0
        L_0x0038:
            int r3 = r3 + 1
            goto L_0x0026
        L_0x003b:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isVoted():boolean");
    }

    public boolean isSponsored() {
        return this.sponsoredId != null;
    }

    public long getPollId() {
        if (this.type != 17) {
            return 0;
        }
        return ((TLRPC$TL_messageMediaPoll) getMedia(this.messageOwner)).poll.id;
    }

    private TLRPC$Photo getPhotoWithId(TLRPC$WebPage tLRPC$WebPage, long j) {
        if (!(tLRPC$WebPage == null || tLRPC$WebPage.cached_page == null)) {
            TLRPC$Photo tLRPC$Photo = tLRPC$WebPage.photo;
            if (tLRPC$Photo != null && tLRPC$Photo.id == j) {
                return tLRPC$Photo;
            }
            for (int i = 0; i < tLRPC$WebPage.cached_page.photos.size(); i++) {
                TLRPC$Photo tLRPC$Photo2 = tLRPC$WebPage.cached_page.photos.get(i);
                if (tLRPC$Photo2.id == j) {
                    return tLRPC$Photo2;
                }
            }
        }
        return null;
    }

    private TLRPC$Document getDocumentWithId(TLRPC$WebPage tLRPC$WebPage, long j) {
        if (!(tLRPC$WebPage == null || tLRPC$WebPage.cached_page == null)) {
            TLRPC$Document tLRPC$Document = tLRPC$WebPage.document;
            if (tLRPC$Document != null && tLRPC$Document.id == j) {
                return tLRPC$Document;
            }
            for (int i = 0; i < tLRPC$WebPage.cached_page.documents.size(); i++) {
                TLRPC$Document tLRPC$Document2 = tLRPC$WebPage.cached_page.documents.get(i);
                if (tLRPC$Document2.id == j) {
                    return tLRPC$Document2;
                }
            }
        }
        return null;
    }

    public boolean isSupergroup() {
        if (this.localSupergroup) {
            return true;
        }
        Boolean bool = this.cachedIsSupergroup;
        if (bool != null) {
            return bool.booleanValue();
        }
        TLRPC$Peer tLRPC$Peer = this.messageOwner.peer_id;
        if (tLRPC$Peer != null) {
            long j = tLRPC$Peer.channel_id;
            if (j != 0) {
                TLRPC$Chat chat = getChat((AbstractMap<Long, TLRPC$Chat>) null, (LongSparseArray<TLRPC$Chat>) null, j);
                if (chat == null) {
                    return false;
                }
                Boolean valueOf = Boolean.valueOf(chat.megagroup);
                this.cachedIsSupergroup = valueOf;
                return valueOf.booleanValue();
            }
        }
        this.cachedIsSupergroup = Boolean.FALSE;
        return false;
    }

    private MessageObject getMessageObjectForBlock(TLRPC$WebPage tLRPC$WebPage, TLRPC$PageBlock tLRPC$PageBlock) {
        TLRPC$TL_message tLRPC$TL_message;
        if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockPhoto) {
            TLRPC$Photo photoWithId = getPhotoWithId(tLRPC$WebPage, ((TLRPC$TL_pageBlockPhoto) tLRPC$PageBlock).photo_id);
            if (photoWithId == tLRPC$WebPage.photo) {
                return this;
            }
            tLRPC$TL_message = new TLRPC$TL_message();
            TLRPC$TL_messageMediaPhoto tLRPC$TL_messageMediaPhoto = new TLRPC$TL_messageMediaPhoto();
            tLRPC$TL_message.media = tLRPC$TL_messageMediaPhoto;
            tLRPC$TL_messageMediaPhoto.photo = photoWithId;
        } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockVideo) {
            TLRPC$TL_pageBlockVideo tLRPC$TL_pageBlockVideo = (TLRPC$TL_pageBlockVideo) tLRPC$PageBlock;
            if (getDocumentWithId(tLRPC$WebPage, tLRPC$TL_pageBlockVideo.video_id) == tLRPC$WebPage.document) {
                return this;
            }
            TLRPC$TL_message tLRPC$TL_message2 = new TLRPC$TL_message();
            TLRPC$TL_messageMediaDocument tLRPC$TL_messageMediaDocument = new TLRPC$TL_messageMediaDocument();
            tLRPC$TL_message2.media = tLRPC$TL_messageMediaDocument;
            tLRPC$TL_messageMediaDocument.document = getDocumentWithId(tLRPC$WebPage, tLRPC$TL_pageBlockVideo.video_id);
            tLRPC$TL_message = tLRPC$TL_message2;
        } else {
            tLRPC$TL_message = null;
        }
        tLRPC$TL_message.message = "";
        tLRPC$TL_message.realId = getId();
        tLRPC$TL_message.id = Utilities.random.nextInt();
        TLRPC$Message tLRPC$Message = this.messageOwner;
        tLRPC$TL_message.date = tLRPC$Message.date;
        tLRPC$TL_message.peer_id = tLRPC$Message.peer_id;
        tLRPC$TL_message.out = tLRPC$Message.out;
        tLRPC$TL_message.from_id = tLRPC$Message.from_id;
        return new MessageObject(this.currentAccount, tLRPC$TL_message, false, true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x001a, code lost:
        r0 = getMedia(r6.messageOwner).webpage;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<org.telegram.messenger.MessageObject> getWebPagePhotos(java.util.ArrayList<org.telegram.messenger.MessageObject> r7, java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r8) {
        /*
            r6 = this;
            if (r7 != 0) goto L_0x0007
            java.util.ArrayList r7 = new java.util.ArrayList
            r7.<init>()
        L_0x0007:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            if (r0 == 0) goto L_0x007e
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 != 0) goto L_0x001a
            goto L_0x007e
        L_0x001a:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            org.telegram.tgnet.TLRPC$Page r1 = r0.cached_page
            if (r1 != 0) goto L_0x0027
            return r7
        L_0x0027:
            if (r8 != 0) goto L_0x002b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r8 = r1.blocks
        L_0x002b:
            r1 = 0
            r2 = 0
        L_0x002d:
            int r3 = r8.size()
            if (r2 >= r3) goto L_0x007e
            java.lang.Object r3 = r8.get(r2)
            org.telegram.tgnet.TLRPC$PageBlock r3 = (org.telegram.tgnet.TLRPC$PageBlock) r3
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockSlideshow
            if (r4 == 0) goto L_0x005a
            org.telegram.tgnet.TLRPC$TL_pageBlockSlideshow r3 = (org.telegram.tgnet.TLRPC$TL_pageBlockSlideshow) r3
            r4 = 0
        L_0x0040:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r5 = r3.items
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x007b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r5 = r3.items
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$PageBlock r5 = (org.telegram.tgnet.TLRPC$PageBlock) r5
            org.telegram.messenger.MessageObject r5 = r6.getMessageObjectForBlock(r0, r5)
            r7.add(r5)
            int r4 = r4 + 1
            goto L_0x0040
        L_0x005a:
            boolean r4 = r3 instanceof org.telegram.tgnet.TLRPC$TL_pageBlockCollage
            if (r4 == 0) goto L_0x007b
            org.telegram.tgnet.TLRPC$TL_pageBlockCollage r3 = (org.telegram.tgnet.TLRPC$TL_pageBlockCollage) r3
            r4 = 0
        L_0x0061:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r5 = r3.items
            int r5 = r5.size()
            if (r4 >= r5) goto L_0x007b
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PageBlock> r5 = r3.items
            java.lang.Object r5 = r5.get(r4)
            org.telegram.tgnet.TLRPC$PageBlock r5 = (org.telegram.tgnet.TLRPC$PageBlock) r5
            org.telegram.messenger.MessageObject r5 = r6.getMessageObjectForBlock(r0, r5)
            r7.add(r5)
            int r4 = r4 + 1
            goto L_0x0061
        L_0x007b:
            int r2 = r2 + 1
            goto L_0x002d
        L_0x007e:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getWebPagePhotos(java.util.ArrayList, java.util.ArrayList):java.util.ArrayList");
    }

    public void createMessageSendInfo() {
        HashMap<String, String> hashMap;
        String str;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message.message == null) {
            return;
        }
        if ((tLRPC$Message.id < 0 || isEditing()) && (hashMap = this.messageOwner.params) != null) {
            String str2 = hashMap.get("ve");
            if (str2 != null && (isVideo() || isNewGif() || isRoundVideo())) {
                VideoEditedInfo videoEditedInfo2 = new VideoEditedInfo();
                this.videoEditedInfo = videoEditedInfo2;
                if (!videoEditedInfo2.parseString(str2)) {
                    this.videoEditedInfo = null;
                } else {
                    this.videoEditedInfo.roundVideo = isRoundVideo();
                }
            }
            TLRPC$Message tLRPC$Message2 = this.messageOwner;
            if (tLRPC$Message2.send_state == 3 && (str = tLRPC$Message2.params.get("prevMedia")) != null) {
                SerializedData serializedData = new SerializedData(Base64.decode(str, 0));
                this.previousMedia = TLRPC$MessageMedia.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                this.previousMessage = serializedData.readString(false);
                this.previousAttachPath = serializedData.readString(false);
                int readInt32 = serializedData.readInt32(false);
                this.previousMessageEntities = new ArrayList<>(readInt32);
                for (int i = 0; i < readInt32; i++) {
                    this.previousMessageEntities.add(TLRPC$MessageEntity.TLdeserialize(serializedData, serializedData.readInt32(false), false));
                }
                serializedData.cleanup();
            }
        }
    }

    public void measureInlineBotButtons() {
        int i;
        CharSequence charSequence;
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions;
        if (!this.isRestrictedMessage) {
            this.wantedBotKeyboardWidth = 0;
            if (((this.messageOwner.reply_markup instanceof TLRPC$TL_replyInlineMarkup) && !hasExtendedMedia()) || ((tLRPC$TL_messageReactions = this.messageOwner.reactions) != null && !tLRPC$TL_messageReactions.results.isEmpty())) {
                Theme.createCommonMessageResources();
                StringBuilder sb = this.botButtonsLayout;
                if (sb == null) {
                    this.botButtonsLayout = new StringBuilder();
                } else {
                    sb.setLength(0);
                }
            }
            if (!(this.messageOwner.reply_markup instanceof TLRPC$TL_replyInlineMarkup) || hasExtendedMedia()) {
                TLRPC$TL_messageReactions tLRPC$TL_messageReactions2 = this.messageOwner.reactions;
                if (tLRPC$TL_messageReactions2 != null) {
                    int size = tLRPC$TL_messageReactions2.results.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        TLRPC$ReactionCount tLRPC$ReactionCount = this.messageOwner.reactions.results.get(i2);
                        StringBuilder sb2 = this.botButtonsLayout;
                        sb2.append(0);
                        sb2.append(i2);
                        StaticLayout staticLayout = new StaticLayout(Emoji.replaceEmoji(String.format("%d %s", new Object[]{Integer.valueOf(tLRPC$ReactionCount.count), tLRPC$ReactionCount.reaction}), Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        if (staticLayout.getLineCount() > 0) {
                            float lineWidth = staticLayout.getLineWidth(0);
                            float lineLeft = staticLayout.getLineLeft(0);
                            if (lineLeft < lineWidth) {
                                lineWidth -= lineLeft;
                            }
                            i = Math.max(0, ((int) Math.ceil((double) lineWidth)) + AndroidUtilities.dp(4.0f));
                        } else {
                            i = 0;
                        }
                        this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((i + AndroidUtilities.dp(12.0f)) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
                    }
                    return;
                }
                return;
            }
            for (int i3 = 0; i3 < this.messageOwner.reply_markup.rows.size(); i3++) {
                TLRPC$TL_keyboardButtonRow tLRPC$TL_keyboardButtonRow = this.messageOwner.reply_markup.rows.get(i3);
                int size2 = tLRPC$TL_keyboardButtonRow.buttons.size();
                int i4 = 0;
                for (int i5 = 0; i5 < size2; i5++) {
                    TLRPC$KeyboardButton tLRPC$KeyboardButton = tLRPC$TL_keyboardButtonRow.buttons.get(i5);
                    StringBuilder sb3 = this.botButtonsLayout;
                    sb3.append(i3);
                    sb3.append(i5);
                    if (!(tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonBuy) || (getMedia(this.messageOwner).flags & 4) == 0) {
                        String str = tLRPC$KeyboardButton.text;
                        if (str == null) {
                            str = "";
                        }
                        charSequence = Emoji.replaceEmoji(str, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                    } else {
                        charSequence = LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt);
                    }
                    StaticLayout staticLayout2 = new StaticLayout(charSequence, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (staticLayout2.getLineCount() > 0) {
                        float lineWidth2 = staticLayout2.getLineWidth(0);
                        float lineLeft2 = staticLayout2.getLineLeft(0);
                        if (lineLeft2 < lineWidth2) {
                            lineWidth2 -= lineLeft2;
                        }
                        i4 = Math.max(i4, ((int) Math.ceil((double) lineWidth2)) + AndroidUtilities.dp(4.0f));
                    }
                }
                this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((i4 + AndroidUtilities.dp(12.0f)) * size2) + (AndroidUtilities.dp(5.0f) * (size2 - 1)));
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r0.photo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isVideoAvatar() {
        /*
            r1 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            if (r0 == 0) goto L_0x0014
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            if (r0 == 0) goto L_0x0014
            java.util.ArrayList<org.telegram.tgnet.TLRPC$VideoSize> r0 = r0.video_sizes
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0014
            r0 = 1
            goto L_0x0015
        L_0x0014:
            r0 = 0
        L_0x0015:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isVideoAvatar():boolean");
    }

    public boolean isFcmMessage() {
        return this.localType != 0;
    }

    private TLRPC$User getUser(AbstractMap<Long, TLRPC$User> abstractMap, LongSparseArray<TLRPC$User> longSparseArray, long j) {
        TLRPC$User tLRPC$User;
        if (abstractMap != null) {
            tLRPC$User = abstractMap.get(Long.valueOf(j));
        } else {
            tLRPC$User = longSparseArray != null ? longSparseArray.get(j) : null;
        }
        return tLRPC$User == null ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j)) : tLRPC$User;
    }

    private TLRPC$Chat getChat(AbstractMap<Long, TLRPC$Chat> abstractMap, LongSparseArray<TLRPC$Chat> longSparseArray, long j) {
        TLRPC$Chat tLRPC$Chat;
        if (abstractMap != null) {
            tLRPC$Chat = abstractMap.get(Long.valueOf(j));
        } else {
            tLRPC$Chat = longSparseArray != null ? longSparseArray.get(j) : null;
        }
        return tLRPC$Chat == null ? MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j)) : tLRPC$Chat;
    }

    /* JADX WARNING: type inference failed for: r2v120, types: [org.telegram.tgnet.TLRPC$Chat] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:10:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x0574  */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x0592  */
    /* JADX WARNING: Removed duplicated region for block: B:223:0x05fa  */
    /* JADX WARNING: Removed duplicated region for block: B:224:0x0610  */
    /* JADX WARNING: Removed duplicated region for block: B:240:0x0664  */
    /* JADX WARNING: Removed duplicated region for block: B:241:0x0670  */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x0723  */
    /* JADX WARNING: Removed duplicated region for block: B:272:0x0749  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0950  */
    /* JADX WARNING: Removed duplicated region for block: B:346:0x095c  */
    /* JADX WARNING: Removed duplicated region for block: B:499:0x0d23  */
    /* JADX WARNING: Removed duplicated region for block: B:599:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:616:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x002a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateMessageText(java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC$User> r21, java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC$Chat> r22, androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$User> r23, androidx.collection.LongSparseArray<org.telegram.tgnet.TLRPC$Chat> r24) {
        /*
            r20 = this;
            r6 = r20
            r4 = r21
            r0 = r22
            r5 = r23
            r1 = r24
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r2 = r2.from_id
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r3 == 0) goto L_0x0019
            long r2 = r2.user_id
            org.telegram.tgnet.TLRPC$User r2 = r6.getUser(r4, r5, r2)
            goto L_0x0027
        L_0x0019:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r3 == 0) goto L_0x0026
            long r2 = r2.channel_id
            org.telegram.tgnet.TLRPC$Chat r2 = r6.getChat(r0, r1, r2)
            r3 = r2
            r2 = 0
            goto L_0x0028
        L_0x0026:
            r2 = 0
        L_0x0027:
            r3 = 0
        L_0x0028:
            if (r2 == 0) goto L_0x002c
            r8 = r2
            goto L_0x002d
        L_0x002c:
            r8 = r3
        L_0x002d:
            org.telegram.tgnet.TLRPC$Message r3 = r6.messageOwner
            boolean r9 = r3 instanceof org.telegram.tgnet.TLRPC$TL_messageService
            java.lang.String r10 = ""
            r12 = 1
            r13 = 0
            if (r9 == 0) goto L_0x0d23
            org.telegram.tgnet.TLRPC$MessageAction r9 = r3.action
            if (r9 == 0) goto L_0x0d1f
            boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled
            r15 = 3
            if (r14 == 0) goto L_0x007d
            org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled r9 = (org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) r9
            org.telegram.tgnet.TLRPC$Peer r0 = r3.peer_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r0 != 0) goto L_0x0066
            boolean r0 = r20.isSupergroup()
            if (r0 == 0) goto L_0x004f
            goto L_0x0066
        L_0x004f:
            int r0 = org.telegram.messenger.R.string.ActionChannelCallScheduled
            java.lang.Object[] r1 = new java.lang.Object[r12]
            int r2 = r9.schedule_date
            long r2 = (long) r2
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatStartsTime(r2, r15, r13)
            r1[r13] = r2
            java.lang.String r2 = "ActionChannelCallScheduled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0066:
            int r0 = org.telegram.messenger.R.string.ActionGroupCallScheduled
            java.lang.Object[] r1 = new java.lang.Object[r12]
            int r2 = r9.schedule_date
            long r2 = (long) r2
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatStartsTime(r2, r15, r13)
            r1[r13] = r2
            java.lang.String r2 = "ActionGroupCallScheduled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x007d:
            boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            java.lang.String r7 = "un1"
            if (r14 == 0) goto L_0x013d
            int r0 = r9.duration
            if (r0 == 0) goto L_0x0102
            r1 = 86400(0x15180, float:1.21072E-40)
            int r1 = r0 / r1
            if (r1 <= 0) goto L_0x0097
            java.lang.Object[] r0 = new java.lang.Object[r13]
            java.lang.String r2 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1, r0)
            goto L_0x00b9
        L_0x0097:
            int r1 = r0 / 3600
            if (r1 <= 0) goto L_0x00a4
            java.lang.Object[] r0 = new java.lang.Object[r13]
            java.lang.String r2 = "Hours"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1, r0)
            goto L_0x00b9
        L_0x00a4:
            int r1 = r0 / 60
            if (r1 <= 0) goto L_0x00b1
            java.lang.Object[] r0 = new java.lang.Object[r13]
            java.lang.String r2 = "Minutes"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1, r0)
            goto L_0x00b9
        L_0x00b1:
            java.lang.Object[] r1 = new java.lang.Object[r13]
            java.lang.String r2 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r2, r0, r1)
        L_0x00b9:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r1 != 0) goto L_0x00d8
            boolean r1 = r20.isSupergroup()
            if (r1 == 0) goto L_0x00c8
            goto L_0x00d8
        L_0x00c8:
            int r1 = org.telegram.messenger.R.string.ActionChannelCallEnded
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r2[r13] = r0
            java.lang.String r0 = "ActionChannelCallEnded"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x00d8:
            boolean r1 = r20.isOut()
            if (r1 == 0) goto L_0x00ee
            int r1 = org.telegram.messenger.R.string.ActionGroupCallEndedByYou
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r2[r13] = r0
            java.lang.String r0 = "ActionGroupCallEndedByYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x00ee:
            int r1 = org.telegram.messenger.R.string.ActionGroupCallEndedBy
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r2[r13] = r0
            java.lang.String r0 = "ActionGroupCallEndedBy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0102:
            org.telegram.tgnet.TLRPC$Peer r0 = r3.peer_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r0 != 0) goto L_0x011b
            boolean r0 = r20.isSupergroup()
            if (r0 == 0) goto L_0x010f
            goto L_0x011b
        L_0x010f:
            int r0 = org.telegram.messenger.R.string.ActionChannelCallJustStarted
            java.lang.String r1 = "ActionChannelCallJustStarted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x011b:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x012d
            int r0 = org.telegram.messenger.R.string.ActionGroupCallStartedByYou
            java.lang.String r1 = "ActionGroupCallStartedByYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x012d:
            int r0 = org.telegram.messenger.R.string.ActionGroupCallStarted
            java.lang.String r1 = "ActionGroupCallStarted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x013d:
            boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            java.lang.String r15 = "un2"
            r18 = 0
            if (r14 == 0) goto L_0x01f9
            long r0 = r9.user_id
            int r2 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1))
            if (r2 != 0) goto L_0x0163
            java.util.ArrayList<java.lang.Long> r2 = r9.users
            int r2 = r2.size()
            if (r2 != r12) goto L_0x0163
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.util.ArrayList<java.lang.Long> r0 = r0.users
            java.lang.Object r0 = r0.get(r13)
            java.lang.Long r0 = (java.lang.Long) r0
            long r0 = r0.longValue()
        L_0x0163:
            java.lang.String r2 = "ActionGroupCallYouInvited"
            java.lang.String r3 = "ActionGroupCallInvited"
            int r9 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1))
            if (r9 == 0) goto L_0x01b5
            org.telegram.tgnet.TLRPC$User r4 = r6.getUser(r4, r5, r0)
            boolean r5 = r20.isOut()
            if (r5 == 0) goto L_0x0183
            int r0 = org.telegram.messenger.R.string.ActionGroupCallYouInvited
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r15, r4)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0183:
            int r2 = r6.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            long r11 = r2.getClientUserId()
            int r2 = (r0 > r11 ? 1 : (r0 == r11 ? 0 : -1))
            if (r2 != 0) goto L_0x01a1
            int r0 = org.telegram.messenger.R.string.ActionGroupCallInvitedYou
            java.lang.String r1 = "ActionGroupCallInvitedYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x01a1:
            int r0 = org.telegram.messenger.R.string.ActionGroupCallInvited
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r15, r4)
            r6.messageText = r0
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x01b5:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x01d7
            int r0 = org.telegram.messenger.R.string.ActionGroupCallYouInvited
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r0)
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.util.ArrayList<java.lang.Long> r3 = r0.users
            java.lang.String r2 = "un2"
            r0 = r20
            r4 = r21
            r5 = r23
            java.lang.CharSequence r0 = r0.replaceWithLink(r1, r2, r3, r4, r5)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x01d7:
            int r0 = org.telegram.messenger.R.string.ActionGroupCallInvited
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r0)
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.util.ArrayList<java.lang.Long> r3 = r0.users
            java.lang.String r2 = "un2"
            r0 = r20
            r4 = r21
            r5 = r23
            java.lang.CharSequence r0 = r0.replaceWithLink(r1, r2, r3, r4, r5)
            r6.messageText = r0
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x01f9:
            boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r14 == 0) goto L_0x0296
            org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached r9 = (org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached) r9
            org.telegram.tgnet.TLRPC$Peer r2 = r9.from_id
            long r2 = getPeerId(r2)
            int r8 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r8 <= 0) goto L_0x020e
            org.telegram.tgnet.TLRPC$User r8 = r6.getUser(r4, r5, r2)
            goto L_0x0213
        L_0x020e:
            long r13 = -r2
            org.telegram.tgnet.TLRPC$Chat r8 = r6.getChat(r0, r1, r13)
        L_0x0213:
            org.telegram.tgnet.TLRPC$Peer r13 = r9.to_id
            long r13 = getPeerId(r13)
            int r11 = r6.currentAccount
            org.telegram.messenger.UserConfig r11 = org.telegram.messenger.UserConfig.getInstance(r11)
            long r16 = r11.getClientUserId()
            int r11 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r11 != 0) goto L_0x0244
            int r0 = org.telegram.messenger.R.string.ActionUserWithinRadius
            java.lang.Object[] r1 = new java.lang.Object[r12]
            int r2 = r9.distance
            float r2 = (float) r2
            r3 = 2
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatDistance(r2, r3)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ActionUserWithinRadius"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0244:
            int r11 = (r13 > r18 ? 1 : (r13 == r18 ? 0 : -1))
            if (r11 <= 0) goto L_0x024d
            org.telegram.tgnet.TLRPC$User r0 = r6.getUser(r4, r5, r13)
            goto L_0x0252
        L_0x024d:
            long r4 = -r13
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r4)
        L_0x0252:
            int r1 = (r2 > r16 ? 1 : (r2 == r16 ? 0 : -1))
            if (r1 != 0) goto L_0x0273
            int r1 = org.telegram.messenger.R.string.ActionUserWithinYouRadius
            java.lang.Object[] r2 = new java.lang.Object[r12]
            int r3 = r9.distance
            float r3 = (float) r3
            r4 = 2
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatDistance(r3, r4)
            r5 = 0
            r2[r5] = r3
            java.lang.String r3 = "ActionUserWithinYouRadius"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            java.lang.CharSequence r0 = replaceWithLink(r1, r7, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0273:
            r4 = 2
            r5 = 0
            int r1 = org.telegram.messenger.R.string.ActionUserWithinOtherRadius
            java.lang.Object[] r2 = new java.lang.Object[r12]
            int r3 = r9.distance
            float r3 = (float) r3
            java.lang.String r3 = org.telegram.messenger.LocaleController.formatDistance(r3, r4)
            r2[r5] = r3
            java.lang.String r3 = "ActionUserWithinOtherRadius"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            java.lang.CharSequence r0 = replaceWithLink(r1, r15, r0)
            r6.messageText = r0
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0296:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionCustomAction
            if (r11 == 0) goto L_0x02a0
            java.lang.String r0 = r9.message
            r6.messageText = r0
            goto L_0x0d1f
        L_0x02a0:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r11 == 0) goto L_0x02c6
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x02b6
            int r0 = org.telegram.messenger.R.string.ActionYouCreateGroup
            java.lang.String r1 = "ActionYouCreateGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x02b6:
            int r0 = org.telegram.messenger.R.string.ActionCreateGroup
            java.lang.String r1 = "ActionCreateGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x02c6:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r11 == 0) goto L_0x035a
            boolean r0 = r20.isFromUser()
            if (r0 == 0) goto L_0x0300
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r0.action
            long r1 = r1.user_id
            org.telegram.tgnet.TLRPC$Peer r0 = r0.from_id
            long r11 = r0.user_id
            int r0 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x0300
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x02f0
            int r0 = org.telegram.messenger.R.string.ActionYouLeftUser
            java.lang.String r1 = "ActionYouLeftUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x02f0:
            int r0 = org.telegram.messenger.R.string.ActionLeftUser
            java.lang.String r1 = "ActionLeftUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0300:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            long r0 = r0.user_id
            org.telegram.tgnet.TLRPC$User r0 = r6.getUser(r4, r5, r0)
            boolean r1 = r20.isOut()
            if (r1 == 0) goto L_0x0320
            int r1 = org.telegram.messenger.R.string.ActionYouKickUser
            java.lang.String r2 = "ActionYouKickUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r0 = replaceWithLink(r1, r15, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0320:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            long r1 = r1.user_id
            int r3 = r6.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r3 = r3.getClientUserId()
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x0344
            int r0 = org.telegram.messenger.R.string.ActionKickUserYou
            java.lang.String r1 = "ActionKickUserYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0344:
            int r1 = org.telegram.messenger.R.string.ActionKickUser
            java.lang.String r2 = "ActionKickUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r0 = replaceWithLink(r1, r15, r0)
            r6.messageText = r0
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x035a:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r11 == 0) goto L_0x04cb
            long r2 = r9.user_id
            int r11 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r11 != 0) goto L_0x037d
            java.util.ArrayList<java.lang.Long> r9 = r9.users
            int r9 = r9.size()
            if (r9 != r12) goto L_0x037d
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Long> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
        L_0x037d:
            java.lang.String r9 = "ActionYouAddUser"
            java.lang.String r11 = "ActionAddUser"
            int r12 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r12 == 0) goto L_0x0487
            org.telegram.tgnet.TLRPC$User r4 = r6.getUser(r4, r5, r2)
            org.telegram.tgnet.TLRPC$Message r5 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            long r12 = r5.channel_id
            int r5 = (r12 > r18 ? 1 : (r12 == r18 ? 0 : -1))
            if (r5 == 0) goto L_0x0398
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r12)
            goto L_0x0399
        L_0x0398:
            r0 = 0
        L_0x0399:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.from_id
            if (r1 == 0) goto L_0x0411
            long r12 = r1.user_id
            int r1 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r1 != 0) goto L_0x0411
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x03bb
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x03bb
            int r0 = org.telegram.messenger.R.string.ChannelJoined
            java.lang.String r1 = "ChannelJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x03bb:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r0 = r0.channel_id
            int r4 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x03ef
            int r0 = r6.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r0 = r0.getClientUserId()
            int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r4 != 0) goto L_0x03df
            int r0 = org.telegram.messenger.R.string.ChannelMegaJoined
            java.lang.String r1 = "ChannelMegaJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x03df:
            int r0 = org.telegram.messenger.R.string.ActionAddUserSelfMega
            java.lang.String r1 = "ActionAddUserSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x03ef:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x0401
            int r0 = org.telegram.messenger.R.string.ActionAddUserSelfYou
            java.lang.String r1 = "ActionAddUserSelfYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0401:
            int r0 = org.telegram.messenger.R.string.ActionAddUserSelf
            java.lang.String r1 = "ActionAddUserSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0411:
            boolean r1 = r20.isOut()
            if (r1 == 0) goto L_0x0425
            int r0 = org.telegram.messenger.R.string.ActionYouAddUser
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r9, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r15, r4)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0425:
            int r1 = r6.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r12 = r1.getClientUserId()
            int r1 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r1 != 0) goto L_0x0473
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r1 = r1.channel_id
            int r3 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1))
            if (r3 == 0) goto L_0x0463
            if (r0 == 0) goto L_0x0453
            boolean r0 = r0.megagroup
            if (r0 == 0) goto L_0x0453
            int r0 = org.telegram.messenger.R.string.MegaAddedBy
            java.lang.String r1 = "MegaAddedBy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0453:
            int r0 = org.telegram.messenger.R.string.ChannelAddedBy
            java.lang.String r1 = "ChannelAddedBy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0463:
            int r0 = org.telegram.messenger.R.string.ActionAddUserYou
            java.lang.String r1 = "ActionAddUserYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0473:
            int r0 = org.telegram.messenger.R.string.ActionAddUser
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r11, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r15, r4)
            r6.messageText = r0
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0487:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x04a9
            int r0 = org.telegram.messenger.R.string.ActionYouAddUser
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r0)
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.util.ArrayList<java.lang.Long> r3 = r0.users
            java.lang.String r2 = "un2"
            r0 = r20
            r4 = r21
            r5 = r23
            java.lang.CharSequence r0 = r0.replaceWithLink(r1, r2, r3, r4, r5)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x04a9:
            int r0 = org.telegram.messenger.R.string.ActionAddUser
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r0)
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.util.ArrayList<java.lang.Long> r3 = r0.users
            java.lang.String r2 = "un2"
            r0 = r20
            r4 = r21
            r5 = r23
            java.lang.CharSequence r0 = r0.replaceWithLink(r1, r2, r3, r4, r5)
            r6.messageText = r0
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x04cb:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r11 == 0) goto L_0x04f1
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x04e1
            int r0 = org.telegram.messenger.R.string.ActionInviteYou
            java.lang.String r1 = "ActionInviteYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x04e1:
            int r0 = org.telegram.messenger.R.string.ActionInviteUser
            java.lang.String r1 = "ActionInviteUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x04f1:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGiftPremium
            r13 = -1
            if (r11 == 0) goto L_0x0556
            boolean r0 = r8 instanceof org.telegram.tgnet.TLRPC$User
            if (r0 == 0) goto L_0x051a
            r0 = r8
            org.telegram.tgnet.TLRPC$User r0 = (org.telegram.tgnet.TLRPC$User) r0
            boolean r0 = r0.self
            if (r0 == 0) goto L_0x051a
            org.telegram.tgnet.TLRPC$Peer r0 = r3.peer_id
            long r0 = r0.user_id
            org.telegram.tgnet.TLRPC$User r0 = r6.getUser(r4, r5, r0)
            int r1 = org.telegram.messenger.R.string.ActionGiftOutbound
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString((int) r1)
            android.text.SpannableStringBuilder r1 = org.telegram.messenger.AndroidUtilities.replaceTags(r1)
            java.lang.CharSequence r0 = replaceWithLink(r1, r7, r0)
            r6.messageText = r0
            goto L_0x052a
        L_0x051a:
            int r0 = org.telegram.messenger.R.string.ActionGiftInbound
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString((int) r0)
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.replaceTags(r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
        L_0x052a:
            java.lang.CharSequence r0 = r6.messageText
            java.lang.String r0 = r0.toString()
            int r0 = r0.indexOf(r15)
            if (r0 == r13) goto L_0x0d1f
            java.lang.CharSequence r1 = r6.messageText
            android.text.SpannableStringBuilder r1 = android.text.SpannableStringBuilder.valueOf(r1)
            int r2 = r0 + 3
            org.telegram.messenger.BillingController r3 = org.telegram.messenger.BillingController.getInstance()
            org.telegram.tgnet.TLRPC$Message r4 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            long r7 = r4.amount
            java.lang.String r4 = r4.currency
            java.lang.String r3 = r3.formatCurrency(r7, r4)
            android.text.SpannableStringBuilder r0 = r1.replace(r0, r2, r3)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0556:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r11 == 0) goto L_0x05dc
            org.telegram.tgnet.TLRPC$Peer r2 = r3.peer_id
            if (r2 == 0) goto L_0x0569
            long r2 = r2.channel_id
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x0569
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r2)
            goto L_0x056a
        L_0x0569:
            r0 = 0
        L_0x056a:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x0592
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0592
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0586
            int r0 = org.telegram.messenger.R.string.ActionChannelChangedVideo
            java.lang.String r1 = "ActionChannelChangedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0586:
            int r0 = org.telegram.messenger.R.string.ActionChannelChangedPhoto
            java.lang.String r1 = "ActionChannelChangedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0592:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x05b6
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x05aa
            int r0 = org.telegram.messenger.R.string.ActionYouChangedVideo
            java.lang.String r1 = "ActionYouChangedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x05aa:
            int r0 = org.telegram.messenger.R.string.ActionYouChangedPhoto
            java.lang.String r1 = "ActionYouChangedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x05b6:
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x05cc
            int r0 = org.telegram.messenger.R.string.ActionChangedVideo
            java.lang.String r1 = "ActionChangedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x05cc:
            int r0 = org.telegram.messenger.R.string.ActionChangedPhoto
            java.lang.String r1 = "ActionChangedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x05dc:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r11 == 0) goto L_0x0646
            org.telegram.tgnet.TLRPC$Peer r2 = r3.peer_id
            if (r2 == 0) goto L_0x05ef
            long r2 = r2.channel_id
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x05ef
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r2)
            goto L_0x05f0
        L_0x05ef:
            r0 = 0
        L_0x05f0:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x0610
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0610
            int r0 = org.telegram.messenger.R.string.ActionChannelChangedTitle
            java.lang.String r1 = "ActionChannelChangedTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.lang.String r1 = r1.title
            java.lang.String r0 = r0.replace(r15, r1)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0610:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x062c
            int r0 = org.telegram.messenger.R.string.ActionYouChangedTitle
            java.lang.String r1 = "ActionYouChangedTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.lang.String r1 = r1.title
            java.lang.String r0 = r0.replace(r15, r1)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x062c:
            int r0 = org.telegram.messenger.R.string.ActionChangedTitle
            java.lang.String r1 = "ActionChangedTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.lang.String r1 = r1.title
            java.lang.String r0 = r0.replace(r15, r1)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0646:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r11 == 0) goto L_0x0692
            org.telegram.tgnet.TLRPC$Peer r2 = r3.peer_id
            if (r2 == 0) goto L_0x0659
            long r2 = r2.channel_id
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x0659
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r2)
            goto L_0x065a
        L_0x0659:
            r0 = 0
        L_0x065a:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x0670
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0670
            int r0 = org.telegram.messenger.R.string.ActionChannelRemovedPhoto
            java.lang.String r1 = "ActionChannelRemovedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0670:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x0682
            int r0 = org.telegram.messenger.R.string.ActionYouRemovedPhoto
            java.lang.String r1 = "ActionYouRemovedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0682:
            int r0 = org.telegram.messenger.R.string.ActionRemovedPhoto
            java.lang.String r1 = "ActionRemovedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0692:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionTTLChange
            java.lang.String r14 = "MessageLifetimeChangedOutgoing"
            java.lang.String r15 = "MessageLifetimeChanged"
            java.lang.String r13 = "MessageLifetimeYouRemoved"
            java.lang.String r12 = "MessageLifetimeRemoved"
            if (r11 == 0) goto L_0x0707
            int r0 = r9.ttl
            if (r0 == 0) goto L_0x06e3
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x06c2
            int r0 = org.telegram.messenger.R.string.MessageLifetimeChangedOutgoing
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.ttl
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            r3 = 0
            r1[r3] = r2
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r14, r0, r1)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x06c2:
            r3 = 0
            int r0 = org.telegram.messenger.R.string.MessageLifetimeChanged
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r1[r3] = r2
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.ttl
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            r3 = 1
            r1[r3] = r2
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r15, r0, r1)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x06e3:
            r3 = 1
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x06f4
            int r0 = org.telegram.messenger.R.string.MessageLifetimeYouRemoved
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x06f4:
            int r0 = org.telegram.messenger.R.string.MessageLifetimeRemoved
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r3 = 0
            r1[r3] = r2
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r0, r1)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0707:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL
            if (r11 == 0) goto L_0x07a9
            org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL r9 = (org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL) r9
            org.telegram.tgnet.TLRPC$Peer r2 = r3.peer_id
            if (r2 == 0) goto L_0x071c
            long r2 = r2.channel_id
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x071c
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r2)
            goto L_0x071d
        L_0x071c:
            r0 = 0
        L_0x071d:
            if (r0 == 0) goto L_0x0749
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0749
            int r0 = r9.period
            if (r0 == 0) goto L_0x073d
            int r1 = org.telegram.messenger.R.string.ActionTTLChannelChanged
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatTTLString(r0)
            r3 = 0
            r2[r3] = r0
            java.lang.String r0 = "ActionTTLChannelChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x073d:
            int r0 = org.telegram.messenger.R.string.ActionTTLChannelDisabled
            java.lang.String r1 = "ActionTTLChannelDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0749:
            int r0 = r9.period
            if (r0 == 0) goto L_0x0787
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x076b
            int r0 = org.telegram.messenger.R.string.ActionTTLYouChanged
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            int r2 = r9.period
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ActionTTLYouChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x076b:
            r1 = 1
            r3 = 0
            int r0 = org.telegram.messenger.R.string.ActionTTLChanged
            java.lang.Object[] r1 = new java.lang.Object[r1]
            int r2 = r9.period
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            r1[r3] = r2
            java.lang.String r2 = "ActionTTLChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0787:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x0799
            int r0 = org.telegram.messenger.R.string.ActionTTLYouDisabled
            java.lang.String r1 = "ActionTTLYouDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0799:
            int r0 = org.telegram.messenger.R.string.ActionTTLDisabled
            java.lang.String r1 = "ActionTTLDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x07a9:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r11 == 0) goto L_0x083e
            int r0 = r3.date
            long r0 = (long) r0
            r2 = 1000(0x3e8, double:4.94E-321)
            long r0 = r0 * r2
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterDay
            if (r2 == 0) goto L_0x07ea
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterYear
            if (r2 == 0) goto L_0x07ea
            int r2 = org.telegram.messenger.R.string.formatDateAtTime
            r3 = 2
            java.lang.Object[] r7 = new java.lang.Object[r3]
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r3 = r3.formatterYear
            java.lang.String r3 = r3.format((long) r0)
            r8 = 0
            r7[r8] = r3
            org.telegram.messenger.LocaleController r3 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r3 = r3.formatterDay
            java.lang.String r0 = r3.format((long) r0)
            r1 = 1
            r7[r1] = r0
            java.lang.String r0 = "formatDateAtTime"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r7)
            goto L_0x07fd
        L_0x07ea:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r10)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            int r1 = r1.date
            r0.append(r1)
            java.lang.String r0 = r0.toString()
        L_0x07fd:
            int r1 = r6.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            if (r1 != 0) goto L_0x0813
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r1 = r1.user_id
            org.telegram.tgnet.TLRPC$User r1 = r6.getUser(r4, r5, r1)
        L_0x0813:
            if (r1 == 0) goto L_0x081a
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x081b
        L_0x081a:
            r1 = r10
        L_0x081b:
            int r2 = org.telegram.messenger.R.string.NotificationUnrecognizedDevice
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r1
            r1 = 1
            r3[r1] = r0
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.lang.String r1 = r0.title
            r4 = 2
            r3[r4] = r1
            java.lang.String r0 = r0.address
            r1 = 3
            r3[r1] = r0
            java.lang.String r0 = "NotificationUnrecognizedDevice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r3)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x083e:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r11 != 0) goto L_0x0d07
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r11 == 0) goto L_0x0848
            goto L_0x0d07
        L_0x0848:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r11 == 0) goto L_0x0862
            int r0 = org.telegram.messenger.R.string.NotificationContactNewPhoto
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r2)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0d1f
        L_0x0862:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageEncryptedAction
            r17 = r10
            java.lang.String r10 = "ActionTakeScreenshootYou"
            java.lang.String r4 = "ActionTakeScreenshoot"
            if (r11 == 0) goto L_0x08fa
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r0 = r9.encryptedAction
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionScreenshotMessages
            if (r1 == 0) goto L_0x0893
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x0885
            int r0 = org.telegram.messenger.R.string.ActionTakeScreenshootYou
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r0, r1)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0885:
            int r0 = org.telegram.messenger.R.string.ActionTakeScreenshoot
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0893:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL
            if (r1 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL r0 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL) r0
            int r1 = r0.ttl_seconds
            if (r1 == 0) goto L_0x08d6
            boolean r1 = r20.isOut()
            if (r1 == 0) goto L_0x08b9
            int r1 = org.telegram.messenger.R.string.MessageLifetimeChangedOutgoing
            r2 = 1
            java.lang.Object[] r2 = new java.lang.Object[r2]
            int r0 = r0.ttl_seconds
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatTTLString(r0)
            r3 = 0
            r2[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r14, r1, r2)
            r6.messageText = r0
            goto L_0x0var_
        L_0x08b9:
            r3 = 0
            int r1 = org.telegram.messenger.R.string.MessageLifetimeChanged
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r3] = r2
            int r0 = r0.ttl_seconds
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatTTLString(r0)
            r3 = 1
            r4[r3] = r0
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r15, r1, r4)
            r6.messageText = r0
            goto L_0x0var_
        L_0x08d6:
            r3 = 1
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x08e7
            int r0 = org.telegram.messenger.R.string.MessageLifetimeYouRemoved
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r13, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x08e7:
            int r0 = org.telegram.messenger.R.string.MessageLifetimeRemoved
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r11 = 0
            r1[r11] = r2
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r12, r0, r1)
            r6.messageText = r0
            goto L_0x0var_
        L_0x08fa:
            r11 = 0
            boolean r12 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r12 == 0) goto L_0x091f
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x0911
            int r0 = org.telegram.messenger.R.string.ActionTakeScreenshootYou
            java.lang.Object[] r1 = new java.lang.Object[r11]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r10, r0, r1)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0911:
            int r0 = org.telegram.messenger.R.string.ActionTakeScreenshoot
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r4, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0var_
        L_0x091f:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionCreatedBroadcastList
            if (r4 == 0) goto L_0x0932
            int r0 = org.telegram.messenger.R.string.YouCreatedBroadcastList
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = "YouCreatedBroadcastList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0932:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r4 == 0) goto L_0x0968
            org.telegram.tgnet.TLRPC$Peer r2 = r3.peer_id
            if (r2 == 0) goto L_0x0945
            long r2 = r2.channel_id
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x0945
            org.telegram.tgnet.TLRPC$Chat r7 = r6.getChat(r0, r1, r2)
            goto L_0x0946
        L_0x0945:
            r7 = 0
        L_0x0946:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r0 == 0) goto L_0x095c
            boolean r0 = r7.megagroup
            if (r0 == 0) goto L_0x095c
            int r0 = org.telegram.messenger.R.string.ActionCreateMega
            java.lang.String r1 = "ActionCreateMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x095c:
            int r0 = org.telegram.messenger.R.string.ActionCreateChannel
            java.lang.String r1 = "ActionCreateChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0968:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r4 == 0) goto L_0x0978
            int r0 = org.telegram.messenger.R.string.ActionMigrateFromGroup
            java.lang.String r1 = "ActionMigrateFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0978:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r4 == 0) goto L_0x0988
            int r0 = org.telegram.messenger.R.string.ActionMigrateFromGroup
            java.lang.String r1 = "ActionMigrateFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0988:
            boolean r4 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r4 == 0) goto L_0x099d
            if (r2 != 0) goto L_0x0997
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            long r3 = r3.channel_id
            org.telegram.tgnet.TLRPC$Chat r7 = r6.getChat(r0, r1, r3)
            goto L_0x0998
        L_0x0997:
            r7 = 0
        L_0x0998:
            r6.generatePinMessageText(r2, r7)
            goto L_0x0var_
        L_0x099d:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r0 == 0) goto L_0x09ad
            int r0 = org.telegram.messenger.R.string.HistoryCleared
            java.lang.String r1 = "HistoryCleared"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x09ad:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r0 == 0) goto L_0x09b6
            r6.generateGameMessageText(r2)
            goto L_0x0var_
        L_0x09b6:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x0ac8
            org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall r9 = (org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) r9
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r0 = r9.reason
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonMissed
            boolean r1 = r20.isFromUser()
            if (r1 == 0) goto L_0x0a12
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.from_id
            long r1 = r1.user_id
            int r3 = r6.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r3 = r3.getClientUserId()
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x0a12
            if (r0 == 0) goto L_0x09f8
            boolean r0 = r9.video
            if (r0 == 0) goto L_0x09ec
            int r0 = org.telegram.messenger.R.string.CallMessageVideoOutgoingMissed
            java.lang.String r1 = "CallMessageVideoOutgoingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a67
        L_0x09ec:
            int r0 = org.telegram.messenger.R.string.CallMessageOutgoingMissed
            java.lang.String r1 = "CallMessageOutgoingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a67
        L_0x09f8:
            boolean r0 = r9.video
            if (r0 == 0) goto L_0x0a07
            int r0 = org.telegram.messenger.R.string.CallMessageVideoOutgoing
            java.lang.String r1 = "CallMessageVideoOutgoing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a67
        L_0x0a07:
            int r0 = org.telegram.messenger.R.string.CallMessageOutgoing
            java.lang.String r1 = "CallMessageOutgoing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a67
        L_0x0a12:
            if (r0 == 0) goto L_0x0a2e
            boolean r0 = r9.video
            if (r0 == 0) goto L_0x0a23
            int r0 = org.telegram.messenger.R.string.CallMessageVideoIncomingMissed
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a67
        L_0x0a23:
            int r0 = org.telegram.messenger.R.string.CallMessageIncomingMissed
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a67
        L_0x0a2e:
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r0 = r9.reason
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy
            if (r0 == 0) goto L_0x0a4e
            boolean r0 = r9.video
            if (r0 == 0) goto L_0x0a43
            int r0 = org.telegram.messenger.R.string.CallMessageVideoIncomingDeclined
            java.lang.String r1 = "CallMessageVideoIncomingDeclined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a67
        L_0x0a43:
            int r0 = org.telegram.messenger.R.string.CallMessageIncomingDeclined
            java.lang.String r1 = "CallMessageIncomingDeclined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a67
        L_0x0a4e:
            boolean r0 = r9.video
            if (r0 == 0) goto L_0x0a5d
            int r0 = org.telegram.messenger.R.string.CallMessageVideoIncoming
            java.lang.String r1 = "CallMessageVideoIncoming"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a67
        L_0x0a5d:
            int r0 = org.telegram.messenger.R.string.CallMessageIncoming
            java.lang.String r1 = "CallMessageIncoming"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
        L_0x0a67:
            int r0 = r9.duration
            if (r0 <= 0) goto L_0x0var_
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatCallDuration(r0)
            int r1 = org.telegram.messenger.R.string.CallMessageWithDuration
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.CharSequence r3 = r6.messageText
            r4 = 0
            r2[r4] = r3
            r3 = 1
            r2[r3] = r0
            java.lang.String r3 = "CallMessageWithDuration"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            r6.messageText = r1
            java.lang.String r1 = r1.toString()
            int r2 = r1.indexOf(r0)
            r3 = -1
            if (r2 == r3) goto L_0x0var_
            android.text.SpannableString r3 = new android.text.SpannableString
            java.lang.CharSequence r4 = r6.messageText
            r3.<init>(r4)
            int r0 = r0.length()
            int r0 = r0 + r2
            if (r2 <= 0) goto L_0x0aa9
            int r4 = r2 + -1
            char r4 = r1.charAt(r4)
            r5 = 40
            if (r4 != r5) goto L_0x0aa9
            int r2 = r2 + -1
        L_0x0aa9:
            int r4 = r1.length()
            if (r0 >= r4) goto L_0x0ab9
            char r1 = r1.charAt(r0)
            r4 = 41
            if (r1 != r4) goto L_0x0ab9
            int r0 = r0 + 1
        L_0x0ab9:
            org.telegram.ui.Components.TypefaceSpan r1 = new org.telegram.ui.Components.TypefaceSpan
            android.graphics.Typeface r4 = android.graphics.Typeface.DEFAULT
            r1.<init>(r4)
            r4 = 0
            r3.setSpan(r1, r2, r0, r4)
            r6.messageText = r3
            goto L_0x0var_
        L_0x0ac8:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r0 == 0) goto L_0x0adb
            long r0 = r20.getDialogId()
            r3 = r21
            org.telegram.tgnet.TLRPC$User r0 = r6.getUser(r3, r5, r0)
            r6.generatePaymentSentMessageText(r0)
            goto L_0x0var_
        L_0x0adb:
            r3 = r21
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionBotAllowed
            if (r0 == 0) goto L_0x0b28
            org.telegram.tgnet.TLRPC$TL_messageActionBotAllowed r9 = (org.telegram.tgnet.TLRPC$TL_messageActionBotAllowed) r9
            java.lang.String r0 = r9.domain
            int r1 = org.telegram.messenger.R.string.ActionBotAllowed
            java.lang.String r2 = "ActionBotAllowed"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = "%1$s"
            int r2 = r1.indexOf(r2)
            android.text.SpannableString r3 = new android.text.SpannableString
            r4 = 1
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r0
            java.lang.String r1 = java.lang.String.format(r1, r4)
            r3.<init>(r1)
            if (r2 < 0) goto L_0x0b24
            org.telegram.ui.Components.URLSpanNoUnderlineBold r1 = new org.telegram.ui.Components.URLSpanNoUnderlineBold
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "http://"
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            r1.<init>(r4)
            int r0 = r0.length()
            int r0 = r0 + r2
            r4 = 33
            r3.setSpan(r1, r2, r0, r4)
        L_0x0b24:
            r6.messageText = r3
            goto L_0x0var_
        L_0x0b28:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSecureValuesSent
            if (r0 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageActionSecureValuesSent r9 = (org.telegram.tgnet.TLRPC$TL_messageActionSecureValuesSent) r9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureValueType> r1 = r9.types
            int r1 = r1.size()
            r2 = 0
        L_0x0b3a:
            if (r2 >= r1) goto L_0x0CLASSNAME
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureValueType> r4 = r9.types
            java.lang.Object r4 = r4.get(r2)
            org.telegram.tgnet.TLRPC$SecureValueType r4 = (org.telegram.tgnet.TLRPC$SecureValueType) r4
            int r7 = r0.length()
            if (r7 <= 0) goto L_0x0b4f
            java.lang.String r7 = ", "
            r0.append(r7)
        L_0x0b4f:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePhone
            if (r7 == 0) goto L_0x0b60
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentPhone
            java.lang.String r7 = "ActionBotDocumentPhone"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0b60:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeEmail
            if (r7 == 0) goto L_0x0b71
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentEmail
            java.lang.String r7 = "ActionBotDocumentEmail"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0b71:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            if (r7 == 0) goto L_0x0b82
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentAddress
            java.lang.String r7 = "ActionBotDocumentAddress"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0b82:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            if (r7 == 0) goto L_0x0b93
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentIdentity
            java.lang.String r7 = "ActionBotDocumentIdentity"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0b93:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePassport
            if (r7 == 0) goto L_0x0ba4
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentPassport
            java.lang.String r7 = "ActionBotDocumentPassport"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0ba4:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeDriverLicense
            if (r7 == 0) goto L_0x0bb5
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentDriverLicence
            java.lang.String r7 = "ActionBotDocumentDriverLicence"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0bb5:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeIdentityCard
            if (r7 == 0) goto L_0x0bc5
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentIdentityCard
            java.lang.String r7 = "ActionBotDocumentIdentityCard"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0bc5:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeUtilityBill
            if (r7 == 0) goto L_0x0bd5
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentUtilityBill
            java.lang.String r7 = "ActionBotDocumentUtilityBill"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0bd5:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeBankStatement
            if (r7 == 0) goto L_0x0be5
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentBankStatement
            java.lang.String r7 = "ActionBotDocumentBankStatement"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0be5:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeRentalAgreement
            if (r7 == 0) goto L_0x0bf5
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentRentalAgreement
            java.lang.String r7 = "ActionBotDocumentRentalAgreement"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0bf5:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeInternalPassport
            if (r7 == 0) goto L_0x0CLASSNAME
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentInternalPassport
            java.lang.String r7 = "ActionBotDocumentInternalPassport"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            boolean r7 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePassportRegistration
            if (r7 == 0) goto L_0x0CLASSNAME
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentPassportRegistration
            java.lang.String r7 = "ActionBotDocumentPassportRegistration"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeTemporaryRegistration
            if (r4 == 0) goto L_0x0CLASSNAME
            int r4 = org.telegram.messenger.R.string.ActionBotDocumentTemporaryRegistration
            java.lang.String r7 = "ActionBotDocumentTemporaryRegistration"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r7, r4)
            r0.append(r4)
        L_0x0CLASSNAME:
            int r2 = r2 + 1
            goto L_0x0b3a
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            if (r1 == 0) goto L_0x0CLASSNAME
            long r1 = r1.user_id
            org.telegram.tgnet.TLRPC$User r7 = r6.getUser(r3, r5, r1)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r7 = 0
        L_0x0CLASSNAME:
            int r1 = org.telegram.messenger.R.string.ActionBotDocuments
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r7)
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.toString()
            r3 = 1
            r2[r3] = r0
            java.lang.String r0 = "ActionBotDocuments"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0CLASSNAME:
            r3 = 1
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionWebViewDataSent
            if (r0 == 0) goto L_0x0c6d
            org.telegram.tgnet.TLRPC$TL_messageActionWebViewDataSent r9 = (org.telegram.tgnet.TLRPC$TL_messageActionWebViewDataSent) r9
            int r0 = org.telegram.messenger.R.string.ActionBotWebViewData
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = r9.text
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ActionBotWebViewData"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0c6d:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme
            if (r0 == 0) goto L_0x0cca
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r9 = (org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r9
            java.lang.String r0 = r9.emoticon
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r2)
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 == 0) goto L_0x0ca6
            if (r2 == 0) goto L_0x0CLASSNAME
            int r0 = org.telegram.messenger.R.string.ChatThemeDisabledYou
            r3 = 0
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = "ChatThemeDisabledYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0ca2
        L_0x0CLASSNAME:
            r3 = 0
            int r2 = org.telegram.messenger.R.string.ChatThemeDisabled
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r3] = r1
            r5 = 1
            r4[r5] = r0
            java.lang.String r0 = "ChatThemeDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
        L_0x0ca2:
            r6.messageText = r0
            goto L_0x0var_
        L_0x0ca6:
            r3 = 0
            r5 = 1
            if (r2 == 0) goto L_0x0cb7
            int r1 = org.telegram.messenger.R.string.ChatThemeChangedYou
            java.lang.Object[] r2 = new java.lang.Object[r5]
            r2[r3] = r0
            java.lang.String r0 = "ChatThemeChangedYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0cc6
        L_0x0cb7:
            int r2 = org.telegram.messenger.R.string.ChatThemeChangedTo
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r3] = r1
            r4[r5] = r0
            java.lang.String r0 = "ChatThemeChangedTo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
        L_0x0cc6:
            r6.messageText = r0
            goto L_0x0var_
        L_0x0cca:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest
            if (r0 == 0) goto L_0x0var_
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r2)
            if (r0 == 0) goto L_0x0cf7
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r0 = r0.channel_id
            int r2 = r6.currentAccount
            boolean r0 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r0, r2)
            if (r0 == 0) goto L_0x0ceb
            int r0 = org.telegram.messenger.R.string.RequestToJoinChannelApproved
            java.lang.String r1 = "RequestToJoinChannelApproved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x0cf3
        L_0x0ceb:
            int r0 = org.telegram.messenger.R.string.RequestToJoinGroupApproved
            java.lang.String r1 = "RequestToJoinGroupApproved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
        L_0x0cf3:
            r6.messageText = r0
            goto L_0x0var_
        L_0x0cf7:
            int r0 = org.telegram.messenger.R.string.UserAcceptedToGroupAction
            java.lang.String r1 = "UserAcceptedToGroupAction"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0d07:
            r17 = r10
            int r0 = org.telegram.messenger.R.string.NotificationContactJoined
            r1 = 1
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r2)
            r4 = 0
            r1[r4] = r2
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0d1f:
            r17 = r10
            goto L_0x0var_
        L_0x0d23:
            r17 = r10
            r4 = 0
            r6.isRestrictedMessage = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r3.restriction_reason
            java.lang.String r0 = org.telegram.messenger.MessagesController.getRestrictionReason(r0)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x0d3b
            r6.messageText = r0
            r0 = 1
            r6.isRestrictedMessage = r0
            goto L_0x0var_
        L_0x0d3b:
            boolean r0 = r20.isMediaEmpty()
            if (r0 != 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDice
            if (r0 == 0) goto L_0x0d53
            java.lang.String r0 = r20.getDiceEmoji()
            r6.messageText = r0
            goto L_0x0var_
        L_0x0d53:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r0 == 0) goto L_0x0d83
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            org.telegram.tgnet.TLRPC$Poll r0 = r0.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0d77
            int r0 = org.telegram.messenger.R.string.QuizPoll
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0d77:
            int r0 = org.telegram.messenger.R.string.Poll
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0d83:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r0 == 0) goto L_0x0db5
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0da9
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_secret
            if (r0 != 0) goto L_0x0da9
            int r0 = org.telegram.messenger.R.string.AttachDestructingPhoto
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0da9:
            int r0 = org.telegram.messenger.R.string.AttachPhoto
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0db5:
            boolean r0 = r20.isVideo()
            if (r0 != 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r0 == 0) goto L_0x0dd9
            org.telegram.tgnet.TLRPC$Document r0 = r20.getDocument()
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r0 == 0) goto L_0x0dd9
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0dd9
            goto L_0x0var_
        L_0x0dd9:
            boolean r0 = r20.isVoice()
            if (r0 == 0) goto L_0x0deb
            int r0 = org.telegram.messenger.R.string.AttachAudio
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0deb:
            boolean r0 = r20.isRoundVideo()
            if (r0 == 0) goto L_0x0dfd
            int r0 = org.telegram.messenger.R.string.AttachRound
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0dfd:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r0 != 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r0 == 0) goto L_0x0e13
            goto L_0x0var_
        L_0x0e13:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r0 == 0) goto L_0x0e29
            int r0 = org.telegram.messenger.R.string.AttachLiveLocation
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0e29:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r0 == 0) goto L_0x0e5b
            int r0 = org.telegram.messenger.R.string.AttachContact
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            java.lang.String r0 = r0.vcard
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            java.lang.String r0 = r0.vcard
            java.lang.CharSequence r0 = org.telegram.messenger.MessageObject.VCardData.parse(r0)
            r6.vCardData = r0
            goto L_0x0var_
        L_0x0e5b:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0e6d
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            java.lang.String r0 = r0.message
            r6.messageText = r0
            goto L_0x0var_
        L_0x0e6d:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r0 == 0) goto L_0x0e83
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            java.lang.String r0 = r0.description
            r6.messageText = r0
            goto L_0x0var_
        L_0x0e83:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported
            if (r0 == 0) goto L_0x0e99
            int r0 = org.telegram.messenger.R.string.UnsupportedMedia
            java.lang.String r1 = "UnsupportedMedia"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0e99:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r0 == 0) goto L_0x0var_
            boolean r0 = r20.isSticker()
            if (r0 != 0) goto L_0x0ef7
            org.telegram.tgnet.TLRPC$Document r0 = r20.getDocument()
            r1 = 1
            boolean r0 = isAnimatedStickerDocument(r0, r1)
            if (r0 == 0) goto L_0x0eb5
            goto L_0x0ef7
        L_0x0eb5:
            boolean r0 = r20.isMusic()
            if (r0 == 0) goto L_0x0ec7
            int r0 = org.telegram.messenger.R.string.AttachMusic
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0ec7:
            boolean r0 = r20.isGif()
            if (r0 == 0) goto L_0x0ed9
            int r0 = org.telegram.messenger.R.string.AttachGif
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0ed9:
            org.telegram.tgnet.TLRPC$Document r0 = r20.getDocument()
            java.lang.String r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r0)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x0eeb
            r6.messageText = r0
            goto L_0x0var_
        L_0x0eeb:
            int r0 = org.telegram.messenger.R.string.AttachDocument
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0ef7:
            java.lang.String r0 = r20.getStickerChar()
            java.lang.String r1 = "AttachSticker"
            if (r0 == 0) goto L_0x0f1e
            int r2 = r0.length()
            if (r2 <= 0) goto L_0x0f1e
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            r3 = 0
            r2[r3] = r0
            int r0 = org.telegram.messenger.R.string.AttachSticker
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r1 = 1
            r2[r1] = r0
            java.lang.String r0 = "%s %s"
            java.lang.String r0 = java.lang.String.format(r0, r2)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0f1e:
            int r0 = org.telegram.messenger.R.string.AttachSticker
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0var_:
            int r0 = org.telegram.messenger.R.string.AttachLocation
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0f4d
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_secret
            if (r0 != 0) goto L_0x0f4d
            int r0 = org.telegram.messenger.R.string.AttachDestructingVideo
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0f4d:
            int r0 = org.telegram.messenger.R.string.AttachVideo
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0var_
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x0var_
            int r0 = r0.length()     // Catch:{ all -> 0x0f8a }
            r1 = 200(0xc8, float:2.8E-43)
            java.lang.String r2 = "‌"
            if (r0 <= r1) goto L_0x0var_
            java.util.regex.Pattern r0 = org.telegram.messenger.AndroidUtilities.BAD_CHARS_MESSAGE_LONG_PATTERN     // Catch:{ all -> 0x0f8a }
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner     // Catch:{ all -> 0x0f8a }
            java.lang.String r1 = r1.message     // Catch:{ all -> 0x0f8a }
            java.util.regex.Matcher r0 = r0.matcher(r1)     // Catch:{ all -> 0x0f8a }
            java.lang.String r0 = r0.replaceAll(r2)     // Catch:{ all -> 0x0f8a }
            r6.messageText = r0     // Catch:{ all -> 0x0f8a }
            goto L_0x0var_
        L_0x0var_:
            java.util.regex.Pattern r0 = org.telegram.messenger.AndroidUtilities.BAD_CHARS_MESSAGE_PATTERN     // Catch:{ all -> 0x0f8a }
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner     // Catch:{ all -> 0x0f8a }
            java.lang.String r1 = r1.message     // Catch:{ all -> 0x0f8a }
            java.util.regex.Matcher r0 = r0.matcher(r1)     // Catch:{ all -> 0x0f8a }
            java.lang.String r0 = r0.replaceAll(r2)     // Catch:{ all -> 0x0f8a }
            r6.messageText = r0     // Catch:{ all -> 0x0f8a }
            goto L_0x0var_
        L_0x0f8a:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            java.lang.String r0 = r0.message
            r6.messageText = r0
            goto L_0x0var_
        L_0x0var_:
            r6.messageText = r0
        L_0x0var_:
            java.lang.CharSequence r0 = r6.messageText
            if (r0 != 0) goto L_0x0f9b
            r0 = r17
            r6.messageText = r0
        L_0x0f9b:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.updateMessageText(java.util.AbstractMap, java.util.AbstractMap, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    public static TLRPC$MessageMedia getMedia(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia == null) {
            return tLRPC$MessageMedia;
        }
        TLRPC$MessageExtendedMedia tLRPC$MessageExtendedMedia = tLRPC$MessageMedia.extended_media;
        return tLRPC$MessageExtendedMedia instanceof TLRPC$TL_messageExtendedMedia ? ((TLRPC$TL_messageExtendedMedia) tLRPC$MessageExtendedMedia).media : tLRPC$MessageMedia;
    }

    public boolean hasRevealedExtendedMedia() {
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        return tLRPC$MessageMedia != null && (tLRPC$MessageMedia.extended_media instanceof TLRPC$TL_messageExtendedMedia);
    }

    public boolean hasExtendedMedia() {
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        return (tLRPC$MessageMedia == null || tLRPC$MessageMedia.extended_media == null) ? false : true;
    }

    public boolean hasExtendedMediaPreview() {
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        return tLRPC$MessageMedia != null && (tLRPC$MessageMedia.extended_media instanceof TLRPC$TL_messageExtendedMediaPreview);
    }

    public void setType() {
        int i;
        int i2 = this.type;
        this.type = 1000;
        this.isRoundVideoCached = 0;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if ((tLRPC$Message instanceof TLRPC$TL_message) || (tLRPC$Message instanceof TLRPC$TL_messageForwarded_old2)) {
            if (this.isRestrictedMessage) {
                this.type = 0;
            } else if (this.emojiAnimatedSticker == null && this.emojiAnimatedStickerId == null) {
                if (!isDice() && this.emojiOnlyCount >= 1 && !this.hasUnwrappedEmoji) {
                    this.type = 19;
                } else if (isMediaEmpty()) {
                    this.type = 0;
                    if (TextUtils.isEmpty(this.messageText) && this.eventId == 0) {
                        this.messageText = "Empty message";
                    }
                } else if (hasExtendedMediaPreview()) {
                    this.type = 20;
                } else if (getMedia(this.messageOwner).ttl_seconds != 0 && ((getMedia(this.messageOwner).photo instanceof TLRPC$TL_photoEmpty) || (getDocument() instanceof TLRPC$TL_documentEmpty))) {
                    this.contentType = 1;
                    this.type = 10;
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDice) {
                    this.type = 15;
                    if (getMedia(this.messageOwner).document == null) {
                        getMedia(this.messageOwner).document = new TLRPC$TL_document();
                        getMedia(this.messageOwner).document.file_reference = new byte[0];
                        getMedia(this.messageOwner).document.mime_type = "application/x-tgsdice";
                        getMedia(this.messageOwner).document.dc_id = Integer.MIN_VALUE;
                        getMedia(this.messageOwner).document.id = -2147483648L;
                        TLRPC$TL_documentAttributeImageSize tLRPC$TL_documentAttributeImageSize = new TLRPC$TL_documentAttributeImageSize();
                        tLRPC$TL_documentAttributeImageSize.w = 512;
                        tLRPC$TL_documentAttributeImageSize.h = 512;
                        getMedia(this.messageOwner).document.attributes.add(tLRPC$TL_documentAttributeImageSize);
                    }
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto) {
                    this.type = 1;
                } else if ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGeo) || (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaVenue) || (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGeoLive)) {
                    this.type = 4;
                } else if (isRoundVideo()) {
                    this.type = 5;
                } else if (isVideo()) {
                    this.type = 3;
                } else if (isVoice()) {
                    this.type = 2;
                } else if (isMusic()) {
                    this.type = 14;
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaContact) {
                    this.type = 12;
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPoll) {
                    this.type = 17;
                    this.checkedVotes = new ArrayList<>();
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaUnsupported) {
                    this.type = 0;
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument) {
                    TLRPC$Document document = getDocument();
                    if (document == null || document.mime_type == null) {
                        this.type = 9;
                    } else if (isGifDocument(document, hasValidGroupId())) {
                        this.type = 8;
                    } else if (isSticker()) {
                        this.type = 13;
                    } else if (isAnimatedSticker()) {
                        this.type = 15;
                    } else {
                        this.type = 9;
                    }
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGame) {
                    this.type = 0;
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaInvoice) {
                    this.type = 0;
                }
            } else if (isSticker()) {
                this.type = 13;
            } else {
                this.type = 15;
            }
        } else if (tLRPC$Message instanceof TLRPC$TL_messageService) {
            TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
            if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionLoginUnknownLocation) {
                this.type = 0;
            } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionGiftPremium) {
                this.contentType = 1;
                this.type = 18;
            } else if ((tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatEditPhoto) || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionUserUpdatedPhoto)) {
                this.contentType = 1;
                this.type = 11;
            } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageEncryptedAction) {
                TLRPC$DecryptedMessageAction tLRPC$DecryptedMessageAction = tLRPC$MessageAction.encryptedAction;
                if ((tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionScreenshotMessages) || (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionSetMessageTTL)) {
                    this.contentType = 1;
                    this.type = 10;
                } else {
                    this.contentType = -1;
                    this.type = -1;
                }
            } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionHistoryClear) {
                this.contentType = -1;
                this.type = -1;
            } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPhoneCall) {
                this.type = 16;
            } else {
                this.contentType = 1;
                this.type = 10;
            }
        }
        if (i2 != 1000 && i2 != (i = this.type) && i != 19) {
            updateMessageText(MessagesController.getInstance(this.currentAccount).getUsers(), MessagesController.getInstance(this.currentAccount).getChats(), (LongSparseArray<TLRPC$User>) null, (LongSparseArray<TLRPC$Chat>) null);
            generateThumbs(false);
        }
    }

    public boolean checkLayout() {
        CharSequence charSequence;
        TextPaint textPaint;
        int i = this.type;
        if (!((i != 0 && i != 19) || this.messageOwner.peer_id == null || (charSequence = this.messageText) == null || charSequence.length() == 0)) {
            if (this.layoutCreated) {
                if (Math.abs(this.generatedWithMinSize - (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) > AndroidUtilities.dp(52.0f) || this.generatedWithDensity != AndroidUtilities.density) {
                    this.layoutCreated = false;
                }
            }
            if (!this.layoutCreated) {
                this.layoutCreated = true;
                if (isFromUser()) {
                    MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
                }
                if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGame) {
                    textPaint = Theme.chat_msgGameTextPaint;
                } else {
                    textPaint = Theme.chat_msgTextPaint;
                }
                int[] iArr = allowsBigEmoji() ? new int[1] : null;
                CharSequence replaceEmoji = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
                this.messageText = replaceEmoji;
                Spannable replaceAnimatedEmoji = replaceAnimatedEmoji(replaceEmoji, this.messageOwner.entities, textPaint.getFontMetricsInt());
                this.messageText = replaceAnimatedEmoji;
                if (iArr != null && iArr[0] > 1) {
                    replaceEmojiToLottieFrame(replaceAnimatedEmoji, iArr);
                }
                checkEmojiOnly(iArr);
                checkBigAnimatedEmoji();
                setType();
                return true;
            }
        }
        return false;
    }

    public void resetLayout() {
        this.layoutCreated = false;
    }

    public String getMimeType() {
        TLRPC$Document document = getDocument();
        if (document != null) {
            return document.mime_type;
        }
        if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaInvoice) {
            TLRPC$WebDocument tLRPC$WebDocument = ((TLRPC$TL_messageMediaInvoice) getMedia(this.messageOwner)).photo;
            if (tLRPC$WebDocument != null) {
                return tLRPC$WebDocument.mime_type;
            }
            return "";
        } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto) {
            return "image/jpeg";
        } else {
            if (!(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) || getMedia(this.messageOwner).webpage.photo == null) {
                return "";
            }
            return "image/jpeg";
        }
    }

    public boolean canPreviewDocument() {
        return canPreviewDocument(getDocument());
    }

    public static boolean isAnimatedStickerDocument(TLRPC$Document tLRPC$Document) {
        return tLRPC$Document != null && tLRPC$Document.mime_type.equals("video/webm");
    }

    public static boolean isGifDocument(WebFile webFile) {
        return webFile != null && (webFile.mime_type.equals("image/gif") || isNewGifDocument(webFile));
    }

    public static boolean isGifDocument(TLRPC$Document tLRPC$Document) {
        return isGifDocument(tLRPC$Document, false);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = r2.mime_type;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isGifDocument(org.telegram.tgnet.TLRPC$Document r2, boolean r3) {
        /*
            if (r2 == 0) goto L_0x0018
            java.lang.String r0 = r2.mime_type
            if (r0 == 0) goto L_0x0018
            java.lang.String r1 = "image/gif"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0010
            if (r3 == 0) goto L_0x0016
        L_0x0010:
            boolean r2 = isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r2)
            if (r2 == 0) goto L_0x0018
        L_0x0016:
            r2 = 1
            goto L_0x0019
        L_0x0018:
            r2 = 0
        L_0x0019:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isGifDocument(org.telegram.tgnet.TLRPC$Document, boolean):boolean");
    }

    public static boolean isDocumentHasThumb(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null && !tLRPC$Document.thumbs.isEmpty()) {
            int size = tLRPC$Document.thumbs.size();
            for (int i = 0; i < size; i++) {
                TLRPC$PhotoSize tLRPC$PhotoSize = tLRPC$Document.thumbs.get(i);
                if (tLRPC$PhotoSize != null && !(tLRPC$PhotoSize instanceof TLRPC$TL_photoSizeEmpty) && !(tLRPC$PhotoSize.location instanceof TLRPC$TL_fileLocationUnavailable)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canPreviewDocument(TLRPC$Document tLRPC$Document) {
        String str;
        if (!(tLRPC$Document == null || (str = tLRPC$Document.mime_type) == null)) {
            String lowerCase = str.toLowerCase();
            if ((isDocumentHasThumb(tLRPC$Document) && (lowerCase.equals("image/png") || lowerCase.equals("image/jpg") || lowerCase.equals("image/jpeg"))) || (Build.VERSION.SDK_INT >= 26 && lowerCase.equals("image/heic"))) {
                for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                    if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) {
                        TLRPC$TL_documentAttributeImageSize tLRPC$TL_documentAttributeImageSize = (TLRPC$TL_documentAttributeImageSize) tLRPC$DocumentAttribute;
                        if (tLRPC$TL_documentAttributeImageSize.w >= 6000 || tLRPC$TL_documentAttributeImageSize.h >= 6000) {
                            return false;
                        }
                        return true;
                    }
                }
            } else if (BuildVars.DEBUG_PRIVATE_VERSION) {
                String documentFileName = FileLoader.getDocumentFileName(tLRPC$Document);
                if ((!documentFileName.startsWith("tg_secret_sticker") || !documentFileName.endsWith("json")) && !documentFileName.endsWith(".svg")) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isRoundVideoDocument(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null && "video/mp4".equals(tLRPC$Document.mime_type)) {
            boolean z = false;
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < tLRPC$Document.attributes.size(); i3++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i3);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                    i = tLRPC$DocumentAttribute.w;
                    i2 = tLRPC$DocumentAttribute.h;
                    z = tLRPC$DocumentAttribute.round_message;
                }
            }
            if (!z || i > 1280 || i2 > 1280) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isNewGifDocument(WebFile webFile) {
        if (webFile != null && "video/mp4".equals(webFile.mime_type)) {
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < webFile.attributes.size(); i3++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = webFile.attributes.get(i3);
                if (!(tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAnimated) && (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo)) {
                    i = tLRPC$DocumentAttribute.w;
                    i2 = tLRPC$DocumentAttribute.h;
                }
            }
            if (i > 1280 || i2 > 1280) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isNewGifDocument(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null && "video/mp4".equals(tLRPC$Document.mime_type)) {
            boolean z = false;
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < tLRPC$Document.attributes.size(); i3++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i3);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAnimated) {
                    z = true;
                } else if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                    i = tLRPC$DocumentAttribute.w;
                    i2 = tLRPC$DocumentAttribute.h;
                }
            }
            if (!z || i > 1280 || i2 > 1280) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static boolean isSystemSignUp(MessageObject messageObject) {
        if (messageObject != null) {
            TLRPC$Message tLRPC$Message = messageObject.messageOwner;
            return (tLRPC$Message instanceof TLRPC$TL_messageService) && (((TLRPC$TL_messageService) tLRPC$Message).action instanceof TLRPC$TL_messageActionContactSignUp);
        }
    }

    public void generateThumbs(boolean z) {
        ArrayList<TLRPC$PhotoSize> arrayList;
        ArrayList<TLRPC$PhotoSize> arrayList2;
        ArrayList<TLRPC$PhotoSize> arrayList3;
        ArrayList<TLRPC$PhotoSize> arrayList4;
        ArrayList<TLRPC$PhotoSize> arrayList5;
        ArrayList<TLRPC$PhotoSize> arrayList6;
        ArrayList<TLRPC$PhotoSize> arrayList7;
        if (hasExtendedMediaPreview()) {
            TLRPC$TL_messageExtendedMediaPreview tLRPC$TL_messageExtendedMediaPreview = (TLRPC$TL_messageExtendedMediaPreview) this.messageOwner.media.extended_media;
            if (!z) {
                this.photoThumbs = new ArrayList<>(Collections.singletonList(tLRPC$TL_messageExtendedMediaPreview.thumb));
            } else {
                updatePhotoSizeLocations(this.photoThumbs, Collections.singletonList(tLRPC$TL_messageExtendedMediaPreview.thumb));
            }
            this.photoThumbsObject = this.messageOwner;
            if (this.strippedThumb == null) {
                createStrippedThumb();
                return;
            }
            return;
        }
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message instanceof TLRPC$TL_messageService) {
            TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
            if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatEditPhoto) {
                TLRPC$Photo tLRPC$Photo = tLRPC$MessageAction.photo;
                if (!z) {
                    this.photoThumbs = new ArrayList<>(tLRPC$Photo.sizes);
                } else {
                    ArrayList<TLRPC$PhotoSize> arrayList8 = this.photoThumbs;
                    if (arrayList8 != null && !arrayList8.isEmpty()) {
                        for (int i = 0; i < this.photoThumbs.size(); i++) {
                            TLRPC$PhotoSize tLRPC$PhotoSize = this.photoThumbs.get(i);
                            int i2 = 0;
                            while (true) {
                                if (i2 >= tLRPC$Photo.sizes.size()) {
                                    break;
                                }
                                TLRPC$PhotoSize tLRPC$PhotoSize2 = tLRPC$Photo.sizes.get(i2);
                                if (!(tLRPC$PhotoSize2 instanceof TLRPC$TL_photoSizeEmpty) && tLRPC$PhotoSize2.type.equals(tLRPC$PhotoSize.type)) {
                                    tLRPC$PhotoSize.location = tLRPC$PhotoSize2.location;
                                    break;
                                }
                                i2++;
                            }
                        }
                    }
                }
                if (!(tLRPC$Photo.dc_id == 0 || (arrayList7 = this.photoThumbs) == null)) {
                    int size = arrayList7.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        TLRPC$FileLocation tLRPC$FileLocation = this.photoThumbs.get(i3).location;
                        if (tLRPC$FileLocation != null) {
                            tLRPC$FileLocation.dc_id = tLRPC$Photo.dc_id;
                            tLRPC$FileLocation.file_reference = tLRPC$Photo.file_reference;
                        }
                    }
                }
                this.photoThumbsObject = this.messageOwner.action.photo;
            }
        } else if (this.emojiAnimatedSticker == null && this.emojiAnimatedStickerId == null) {
            if (getMedia(tLRPC$Message) != null && !(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaEmpty)) {
                if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto) {
                    TLRPC$Photo tLRPC$Photo2 = getMedia(this.messageOwner).photo;
                    if (!z || !((arrayList6 = this.photoThumbs) == null || arrayList6.size() == tLRPC$Photo2.sizes.size())) {
                        this.photoThumbs = new ArrayList<>(tLRPC$Photo2.sizes);
                    } else {
                        ArrayList<TLRPC$PhotoSize> arrayList9 = this.photoThumbs;
                        if (arrayList9 != null && !arrayList9.isEmpty()) {
                            for (int i4 = 0; i4 < this.photoThumbs.size(); i4++) {
                                TLRPC$PhotoSize tLRPC$PhotoSize3 = this.photoThumbs.get(i4);
                                if (tLRPC$PhotoSize3 != null) {
                                    int i5 = 0;
                                    while (true) {
                                        if (i5 >= tLRPC$Photo2.sizes.size()) {
                                            break;
                                        }
                                        TLRPC$PhotoSize tLRPC$PhotoSize4 = tLRPC$Photo2.sizes.get(i5);
                                        if (tLRPC$PhotoSize4 != null && !(tLRPC$PhotoSize4 instanceof TLRPC$TL_photoSizeEmpty)) {
                                            if (!tLRPC$PhotoSize4.type.equals(tLRPC$PhotoSize3.type)) {
                                                if ("s".equals(tLRPC$PhotoSize3.type) && (tLRPC$PhotoSize4 instanceof TLRPC$TL_photoStrippedSize)) {
                                                    this.photoThumbs.set(i4, tLRPC$PhotoSize4);
                                                    break;
                                                }
                                            } else {
                                                tLRPC$PhotoSize3.location = tLRPC$PhotoSize4.location;
                                                break;
                                            }
                                        }
                                        i5++;
                                    }
                                }
                            }
                        }
                    }
                    this.photoThumbsObject = getMedia(this.messageOwner).photo;
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument) {
                    TLRPC$Document document = getDocument();
                    if (isDocumentHasThumb(document)) {
                        if (!z || (arrayList5 = this.photoThumbs) == null) {
                            ArrayList<TLRPC$PhotoSize> arrayList10 = new ArrayList<>();
                            this.photoThumbs = arrayList10;
                            arrayList10.addAll(document.thumbs);
                        } else if (!arrayList5.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs, document.thumbs);
                        }
                        this.photoThumbsObject = document;
                    }
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGame) {
                    TLRPC$Document tLRPC$Document = getMedia(this.messageOwner).game.document;
                    if (tLRPC$Document != null && isDocumentHasThumb(tLRPC$Document)) {
                        if (!z) {
                            ArrayList<TLRPC$PhotoSize> arrayList11 = new ArrayList<>();
                            this.photoThumbs = arrayList11;
                            arrayList11.addAll(tLRPC$Document.thumbs);
                        } else {
                            ArrayList<TLRPC$PhotoSize> arrayList12 = this.photoThumbs;
                            if (arrayList12 != null && !arrayList12.isEmpty()) {
                                updatePhotoSizeLocations(this.photoThumbs, tLRPC$Document.thumbs);
                            }
                        }
                        this.photoThumbsObject = tLRPC$Document;
                    }
                    TLRPC$Photo tLRPC$Photo3 = getMedia(this.messageOwner).game.photo;
                    if (tLRPC$Photo3 != null) {
                        if (!z || (arrayList4 = this.photoThumbs2) == null) {
                            this.photoThumbs2 = new ArrayList<>(tLRPC$Photo3.sizes);
                        } else if (!arrayList4.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs2, tLRPC$Photo3.sizes);
                        }
                        this.photoThumbsObject2 = tLRPC$Photo3;
                    }
                    if (this.photoThumbs == null && (arrayList3 = this.photoThumbs2) != null) {
                        this.photoThumbs = arrayList3;
                        this.photoThumbs2 = null;
                        this.photoThumbsObject = this.photoThumbsObject2;
                        this.photoThumbsObject2 = null;
                    }
                } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) {
                    TLRPC$Photo tLRPC$Photo4 = getMedia(this.messageOwner).webpage.photo;
                    TLRPC$Document tLRPC$Document2 = getMedia(this.messageOwner).webpage.document;
                    if (tLRPC$Photo4 != null) {
                        if (!z || (arrayList2 = this.photoThumbs) == null) {
                            this.photoThumbs = new ArrayList<>(tLRPC$Photo4.sizes);
                        } else if (!arrayList2.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs, tLRPC$Photo4.sizes);
                        }
                        this.photoThumbsObject = tLRPC$Photo4;
                    } else if (tLRPC$Document2 != null && isDocumentHasThumb(tLRPC$Document2)) {
                        if (!z) {
                            ArrayList<TLRPC$PhotoSize> arrayList13 = new ArrayList<>();
                            this.photoThumbs = arrayList13;
                            arrayList13.addAll(tLRPC$Document2.thumbs);
                        } else {
                            ArrayList<TLRPC$PhotoSize> arrayList14 = this.photoThumbs;
                            if (arrayList14 != null && !arrayList14.isEmpty()) {
                                updatePhotoSizeLocations(this.photoThumbs, tLRPC$Document2.thumbs);
                            }
                        }
                        this.photoThumbsObject = tLRPC$Document2;
                    }
                }
            }
        } else if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) && isDocumentHasThumb(this.emojiAnimatedSticker)) {
            if (!z || (arrayList = this.photoThumbs) == null) {
                ArrayList<TLRPC$PhotoSize> arrayList15 = new ArrayList<>();
                this.photoThumbs = arrayList15;
                arrayList15.addAll(this.emojiAnimatedSticker.thumbs);
            } else if (!arrayList.isEmpty()) {
                updatePhotoSizeLocations(this.photoThumbs, this.emojiAnimatedSticker.thumbs);
            }
            this.photoThumbsObject = this.emojiAnimatedSticker;
        }
    }

    private static void updatePhotoSizeLocations(ArrayList<TLRPC$PhotoSize> arrayList, List<TLRPC$PhotoSize> list) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC$PhotoSize tLRPC$PhotoSize = arrayList.get(i);
            if (tLRPC$PhotoSize != null) {
                int size2 = list.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= size2) {
                        break;
                    }
                    TLRPC$PhotoSize tLRPC$PhotoSize2 = list.get(i2);
                    if (!(tLRPC$PhotoSize2 instanceof TLRPC$TL_photoSizeEmpty) && !(tLRPC$PhotoSize2 instanceof TLRPC$TL_photoCachedSize) && tLRPC$PhotoSize2 != null && tLRPC$PhotoSize2.type.equals(tLRPC$PhotoSize.type)) {
                        tLRPC$PhotoSize.location = tLRPC$PhotoSize2.location;
                        break;
                    }
                    i2++;
                }
            }
        }
    }

    public CharSequence replaceWithLink(CharSequence charSequence, String str, ArrayList<Long> arrayList, AbstractMap<Long, TLRPC$User> abstractMap, LongSparseArray<TLRPC$User> longSparseArray) {
        CharSequence charSequence2 = charSequence;
        ArrayList<Long> arrayList2 = arrayList;
        AbstractMap<Long, TLRPC$User> abstractMap2 = abstractMap;
        LongSparseArray<TLRPC$User> longSparseArray2 = longSparseArray;
        if (TextUtils.indexOf(charSequence, str) >= 0) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("");
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$User tLRPC$User = null;
                if (abstractMap2 != null) {
                    tLRPC$User = abstractMap2.get(arrayList2.get(i));
                } else if (longSparseArray2 != null) {
                    tLRPC$User = longSparseArray2.get(arrayList2.get(i).longValue());
                }
                if (tLRPC$User == null) {
                    tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(arrayList2.get(i));
                }
                if (tLRPC$User != null) {
                    String userName = UserObject.getUserName(tLRPC$User);
                    int length = spannableStringBuilder.length();
                    if (spannableStringBuilder.length() != 0) {
                        spannableStringBuilder.append(", ");
                    }
                    spannableStringBuilder.append(userName);
                    spannableStringBuilder.setSpan(new URLSpanNoUnderlineBold("" + tLRPC$User.id), length, userName.length() + length, 33);
                }
            }
            return TextUtils.replace(charSequence2, new String[]{str}, new CharSequence[]{spannableStringBuilder});
        }
        return charSequence2;
    }

    public static CharSequence replaceWithLink(CharSequence charSequence, String str, TLObject tLObject) {
        String str2;
        String str3;
        int indexOf = TextUtils.indexOf(charSequence, str);
        if (indexOf < 0) {
            return charSequence;
        }
        TLRPC$TL_chatInviteExported tLRPC$TL_chatInviteExported = null;
        if (tLObject instanceof TLRPC$User) {
            TLRPC$User tLRPC$User = (TLRPC$User) tLObject;
            str3 = UserObject.getUserName(tLRPC$User);
            str2 = "" + tLRPC$User.id;
        } else if (tLObject instanceof TLRPC$Chat) {
            TLRPC$Chat tLRPC$Chat = (TLRPC$Chat) tLObject;
            str3 = tLRPC$Chat.title;
            str2 = "" + (-tLRPC$Chat.id);
        } else if (tLObject instanceof TLRPC$TL_game) {
            str3 = ((TLRPC$TL_game) tLObject).title;
            str2 = "game";
        } else if (tLObject instanceof TLRPC$TL_chatInviteExported) {
            tLRPC$TL_chatInviteExported = (TLRPC$TL_chatInviteExported) tLObject;
            str3 = tLRPC$TL_chatInviteExported.link;
            str2 = "invite";
        } else {
            str2 = "0";
            str3 = "";
        }
        String replace = str3.replace(10, ' ');
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(TextUtils.replace(charSequence, new String[]{str}, new String[]{replace}));
        URLSpanNoUnderlineBold uRLSpanNoUnderlineBold = new URLSpanNoUnderlineBold("" + str2);
        uRLSpanNoUnderlineBold.setObject(tLRPC$TL_chatInviteExported);
        spannableStringBuilder.setSpan(uRLSpanNoUnderlineBold, indexOf, replace.length() + indexOf, 33);
        return spannableStringBuilder;
    }

    public String getExtension() {
        String fileName = getFileName();
        int lastIndexOf = fileName.lastIndexOf(46);
        String substring = lastIndexOf != -1 ? fileName.substring(lastIndexOf + 1) : null;
        if (substring == null || substring.length() == 0) {
            substring = getDocument().mime_type;
        }
        if (substring == null) {
            substring = "";
        }
        return substring.toUpperCase();
    }

    public String getFileName() {
        return getFileName(this.messageOwner);
    }

    public static String getFileName(TLRPC$Message tLRPC$Message) {
        TLRPC$PhotoSize closestPhotoSizeWithSize;
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(getDocument(tLRPC$Message));
        }
        if (!(getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto)) {
            return getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage ? FileLoader.getAttachFileName(getMedia(tLRPC$Message).webpage.document) : "";
        }
        ArrayList<TLRPC$PhotoSize> arrayList = getMedia(tLRPC$Message).photo.sizes;
        if (arrayList.size() <= 0 || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize())) == null) {
            return "";
        }
        return FileLoader.getAttachFileName(closestPhotoSizeWithSize);
    }

    public int getMediaType() {
        if (isVideo()) {
            return 2;
        }
        if (isVoice()) {
            return 1;
        }
        if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument) {
            return 3;
        }
        return getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto ? 0 : 4;
    }

    private static boolean containsUrls(CharSequence charSequence) {
        if (charSequence != null && charSequence.length() >= 2 && charSequence.length() <= 20480) {
            int length = charSequence.length();
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            char c = 0;
            while (i < length) {
                char charAt = charSequence.charAt(i);
                if (charAt >= '0' && charAt <= '9') {
                    i2++;
                    if (i2 >= 6) {
                        return true;
                    }
                    i3 = 0;
                    i4 = 0;
                } else if (charAt == ' ' || i2 <= 0) {
                    i2 = 0;
                }
                if ((charAt != '@' && charAt != '#' && charAt != '/' && charAt != '$') || i != 0) {
                    if (i != 0) {
                        int i5 = i - 1;
                        if (charSequence.charAt(i5) != ' ') {
                            if (charSequence.charAt(i5) == 10) {
                            }
                        }
                    }
                    if (charAt != ':') {
                        if (charAt != '/') {
                            if (charAt == '.') {
                                if (i4 == 0 && c != ' ') {
                                    i4++;
                                }
                            } else if (charAt != ' ' && c == '.' && i4 == 1) {
                                return true;
                            }
                            i4 = 0;
                        } else if (i3 == 2) {
                            return true;
                        } else {
                            if (i3 == 1) {
                                i3++;
                            }
                        }
                        i++;
                        c = charAt;
                    } else if (i3 == 0) {
                        i3 = 1;
                        i++;
                        c = charAt;
                    }
                    i3 = 0;
                    i++;
                    c = charAt;
                }
                return true;
            }
        }
        return false;
    }

    public void generateLinkDescription() {
        int i;
        int i2;
        if (this.linkDescription == null) {
            if (!(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) || !(getMedia(this.messageOwner).webpage instanceof TLRPC$TL_webPage) || getMedia(this.messageOwner).webpage.description == null) {
                if ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGame) && getMedia(this.messageOwner).game.description != null) {
                    this.linkDescription = Spannable.Factory.getInstance().newSpannable(getMedia(this.messageOwner).game.description);
                } else if ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaInvoice) && getMedia(this.messageOwner).description != null) {
                    this.linkDescription = Spannable.Factory.getInstance().newSpannable(getMedia(this.messageOwner).description);
                }
                i = 0;
            } else {
                this.linkDescription = Spannable.Factory.getInstance().newSpannable(getMedia(this.messageOwner).webpage.description);
                String str = getMedia(this.messageOwner).webpage.site_name;
                if (str != null) {
                    str = str.toLowerCase();
                }
                if ("instagram".equals(str)) {
                    i2 = 1;
                } else {
                    i2 = "twitter".equals(str) ? 2 : 0;
                }
                i = i2;
            }
            if (!TextUtils.isEmpty(this.linkDescription)) {
                if (containsUrls(this.linkDescription)) {
                    try {
                        AndroidUtilities.addLinks((Spannable) this.linkDescription, 1);
                    } catch (Exception e) {
                        FileLog.e((Throwable) e);
                    }
                }
                CharSequence replaceEmoji = Emoji.replaceEmoji(this.linkDescription, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                this.linkDescription = replaceEmoji;
                ArrayList<TLRPC$MessageEntity> arrayList = this.webPageDescriptionEntities;
                if (arrayList != null) {
                    addEntitiesToText(replaceEmoji, arrayList, isOut(), false, false, true);
                    replaceAnimatedEmoji(this.linkDescription, this.webPageDescriptionEntities, Theme.chat_msgTextPaint.getFontMetricsInt());
                }
                if (i != 0) {
                    if (!(this.linkDescription instanceof Spannable)) {
                        this.linkDescription = new SpannableStringBuilder(this.linkDescription);
                    }
                    addUrlsByPattern(isOutOwner(), this.linkDescription, false, i, 0, false);
                }
            }
        }
    }

    public CharSequence getVoiceTranscription() {
        String str;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message == null || (str = tLRPC$Message.voiceTranscription) == null) {
            return null;
        }
        if (TextUtils.isEmpty(str)) {
            SpannableString spannableString = new SpannableString(LocaleController.getString("NoWordsRecognized", R.string.NoWordsRecognized));
            spannableString.setSpan(new CharacterStyle() {
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setTextSize(textPaint.getTextSize() * 0.8f);
                    textPaint.setColor(Theme.chat_timePaint.getColor());
                }
            }, 0, spannableString.length(), 33);
            return spannableString;
        }
        String str2 = this.messageOwner.voiceTranscription;
        return !TextUtils.isEmpty(str2) ? Emoji.replaceEmoji(str2, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false) : str2;
    }

    public float measureVoiceTranscriptionHeight() {
        StaticLayout staticLayout;
        CharSequence voiceTranscription = getVoiceTranscription();
        if (voiceTranscription == null) {
            return 0.0f;
        }
        int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(needDrawAvatar() ? 147.0f : 95.0f);
        if (Build.VERSION.SDK_INT >= 24) {
            staticLayout = StaticLayout.Builder.obtain(voiceTranscription, 0, voiceTranscription.length(), Theme.chat_msgTextPaint, dp).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Layout.Alignment.ALIGN_NORMAL).build();
        } else {
            staticLayout = new StaticLayout(voiceTranscription, Theme.chat_msgTextPaint, dp, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        return (float) staticLayout.getHeight();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r2.messageOwner;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isVoiceTranscriptionOpen() {
        /*
            r2 = this;
            boolean r0 = r2.isVoice()
            if (r0 == 0) goto L_0x002a
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            if (r0 == 0) goto L_0x002a
            boolean r1 = r0.voiceTranscriptionOpen
            if (r1 == 0) goto L_0x002a
            java.lang.String r1 = r0.voiceTranscription
            if (r1 == 0) goto L_0x002a
            boolean r0 = r0.voiceTranscriptionFinal
            if (r0 != 0) goto L_0x001c
            boolean r0 = org.telegram.ui.Components.TranscribeButton.isTranscribing(r2)
            if (r0 == 0) goto L_0x002a
        L_0x001c:
            int r0 = r2.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            boolean r0 = r0.isPremium()
            if (r0 == 0) goto L_0x002a
            r0 = 1
            goto L_0x002b
        L_0x002a:
            r0 = 0
        L_0x002b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isVoiceTranscriptionOpen():boolean");
    }

    public void generateCaption() {
        boolean z;
        if (this.caption == null && !isRoundVideo()) {
            if (hasExtendedMedia()) {
                TLRPC$Message tLRPC$Message = this.messageOwner;
                tLRPC$Message.message = tLRPC$Message.media.description;
            }
            if (!isMediaEmpty() && !(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGame) && !TextUtils.isEmpty(this.messageOwner.message)) {
                boolean z2 = false;
                CharSequence replaceEmoji = Emoji.replaceEmoji(this.messageOwner.message, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                this.caption = replaceEmoji;
                this.caption = replaceAnimatedEmoji(replaceEmoji, this.messageOwner.entities, Theme.chat_msgTextPaint.getFontMetricsInt());
                TLRPC$Message tLRPC$Message2 = this.messageOwner;
                if (tLRPC$Message2.send_state != 0) {
                    z = false;
                } else {
                    z = !tLRPC$Message2.entities.isEmpty();
                }
                if (!z && (this.eventId != 0 || (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto_old) || (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto_layer68) || (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto_layer74) || (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument_old) || (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument_layer68) || (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument_layer74) || ((isOut() && this.messageOwner.send_state != 0) || this.messageOwner.id < 0))) {
                    z2 = true;
                }
                if (z2) {
                    if (containsUrls(this.caption)) {
                        try {
                            AndroidUtilities.addLinks((Spannable) this.caption, 5);
                        } catch (Exception e) {
                            FileLog.e((Throwable) e);
                        }
                    }
                    addUrlsByPattern(isOutOwner(), this.caption, true, 0, 0, true);
                }
                addEntitiesToText(this.caption, z2);
                if (isVideo()) {
                    addUrlsByPattern(isOutOwner(), this.caption, true, 3, getDuration(), false);
                } else if (isMusic() || isVoice()) {
                    addUrlsByPattern(isOutOwner(), this.caption, true, 4, getDuration(), false);
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:78:0x01b5 A[Catch:{ Exception -> 0x0214 }] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x01b6 A[Catch:{ Exception -> 0x0214 }] */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x01f8 A[ADDED_TO_REGION, Catch:{ Exception -> 0x0214 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void addUrlsByPattern(boolean r16, java.lang.CharSequence r17, boolean r18, int r19, int r20, boolean r21) {
        /*
            r0 = r17
            r1 = r19
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            r2 = 4
            r3 = 3
            r4 = 1
            if (r1 == r3) goto L_0x0037
            if (r1 != r2) goto L_0x000f
            goto L_0x0037
        L_0x000f:
            if (r1 != r4) goto L_0x0024
            java.util.regex.Pattern r5 = instagramUrlPattern     // Catch:{ Exception -> 0x0214 }
            if (r5 != 0) goto L_0x001d
            java.lang.String r5 = "(^|\\s|\\()@[a-zA-Z\\d_.]{1,32}|(^|\\s|\\()#[\\w.]+"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x0214 }
            instagramUrlPattern = r5     // Catch:{ Exception -> 0x0214 }
        L_0x001d:
            java.util.regex.Pattern r5 = instagramUrlPattern     // Catch:{ Exception -> 0x0214 }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x0214 }
            goto L_0x0049
        L_0x0024:
            java.util.regex.Pattern r5 = urlPattern     // Catch:{ Exception -> 0x0214 }
            if (r5 != 0) goto L_0x0030
            java.lang.String r5 = "(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s|\\()@[a-zA-Z\\d_]{1,32}|(^|\\s|\\()#[^0-9][\\w.]+|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x0214 }
            urlPattern = r5     // Catch:{ Exception -> 0x0214 }
        L_0x0030:
            java.util.regex.Pattern r5 = urlPattern     // Catch:{ Exception -> 0x0214 }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x0214 }
            goto L_0x0049
        L_0x0037:
            java.util.regex.Pattern r5 = videoTimeUrlPattern     // Catch:{ Exception -> 0x0214 }
            if (r5 != 0) goto L_0x0043
            java.lang.String r5 = "\\b(?:(\\d{1,2}):)?(\\d{1,3}):([0-5][0-9])\\b([^\\n]*)"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x0214 }
            videoTimeUrlPattern = r5     // Catch:{ Exception -> 0x0214 }
        L_0x0043:
            java.util.regex.Pattern r5 = videoTimeUrlPattern     // Catch:{ Exception -> 0x0214 }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x0214 }
        L_0x0049:
            r6 = r0
            android.text.Spannable r6 = (android.text.Spannable) r6     // Catch:{ Exception -> 0x0214 }
        L_0x004c:
            boolean r7 = r5.find()     // Catch:{ Exception -> 0x0214 }
            if (r7 == 0) goto L_0x0218
            int r7 = r5.start()     // Catch:{ Exception -> 0x0214 }
            int r8 = r5.end()     // Catch:{ Exception -> 0x0214 }
            r11 = 2
            if (r1 == r3) goto L_0x0147
            if (r1 != r2) goto L_0x0061
            goto L_0x0147
        L_0x0061:
            char r12 = r0.charAt(r7)     // Catch:{ Exception -> 0x0214 }
            r13 = 47
            r14 = 35
            r15 = 64
            if (r1 == 0) goto L_0x007c
            if (r12 == r15) goto L_0x0073
            if (r12 == r14) goto L_0x0073
            int r7 = r7 + 1
        L_0x0073:
            char r12 = r0.charAt(r7)     // Catch:{ Exception -> 0x0214 }
            if (r12 == r15) goto L_0x0088
            if (r12 == r14) goto L_0x0088
            goto L_0x004c
        L_0x007c:
            if (r12 == r15) goto L_0x0088
            if (r12 == r14) goto L_0x0088
            if (r12 == r13) goto L_0x0088
            r14 = 36
            if (r12 == r14) goto L_0x0088
            int r7 = r7 + 1
        L_0x0088:
            if (r1 != r4) goto L_0x00d0
            if (r12 != r15) goto L_0x00ae
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0214 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0214 }
            r11.<init>()     // Catch:{ Exception -> 0x0214 }
            java.lang.String r12 = "https://instagram.com/"
            r11.append(r12)     // Catch:{ Exception -> 0x0214 }
            int r12 = r7 + 1
            java.lang.CharSequence r12 = r0.subSequence(r12, r8)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0214 }
            r11.append(r12)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0214 }
            r9.<init>(r11)     // Catch:{ Exception -> 0x0214 }
            goto L_0x0143
        L_0x00ae:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0214 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0214 }
            r11.<init>()     // Catch:{ Exception -> 0x0214 }
            java.lang.String r12 = "https://www.instagram.com/explore/tags/"
            r11.append(r12)     // Catch:{ Exception -> 0x0214 }
            int r12 = r7 + 1
            java.lang.CharSequence r12 = r0.subSequence(r12, r8)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0214 }
            r11.append(r12)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0214 }
            r9.<init>(r11)     // Catch:{ Exception -> 0x0214 }
            goto L_0x0143
        L_0x00d0:
            if (r1 != r11) goto L_0x0116
            if (r12 != r15) goto L_0x00f5
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0214 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0214 }
            r11.<init>()     // Catch:{ Exception -> 0x0214 }
            java.lang.String r12 = "https://twitter.com/"
            r11.append(r12)     // Catch:{ Exception -> 0x0214 }
            int r12 = r7 + 1
            java.lang.CharSequence r12 = r0.subSequence(r12, r8)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0214 }
            r11.append(r12)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0214 }
            r9.<init>(r11)     // Catch:{ Exception -> 0x0214 }
            goto L_0x0143
        L_0x00f5:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0214 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0214 }
            r11.<init>()     // Catch:{ Exception -> 0x0214 }
            java.lang.String r12 = "https://twitter.com/hashtag/"
            r11.append(r12)     // Catch:{ Exception -> 0x0214 }
            int r12 = r7 + 1
            java.lang.CharSequence r12 = r0.subSequence(r12, r8)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0214 }
            r11.append(r12)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0214 }
            r9.<init>(r11)     // Catch:{ Exception -> 0x0214 }
            goto L_0x0143
        L_0x0116:
            char r11 = r0.charAt(r7)     // Catch:{ Exception -> 0x0214 }
            if (r11 != r13) goto L_0x0136
            if (r18 == 0) goto L_0x0131
            org.telegram.ui.Components.URLSpanBotCommand r9 = new org.telegram.ui.Components.URLSpanBotCommand     // Catch:{ Exception -> 0x0214 }
            java.lang.CharSequence r11 = r0.subSequence(r7, r8)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0214 }
            if (r16 == 0) goto L_0x012c
            r12 = 1
            goto L_0x012d
        L_0x012c:
            r12 = 0
        L_0x012d:
            r9.<init>(r11, r12)     // Catch:{ Exception -> 0x0214 }
            goto L_0x0143
        L_0x0131:
            r4 = r20
            r9 = 0
            goto L_0x01f6
        L_0x0136:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0214 }
            java.lang.CharSequence r11 = r0.subSequence(r7, r8)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0214 }
            r9.<init>(r11)     // Catch:{ Exception -> 0x0214 }
        L_0x0143:
            r4 = r20
            goto L_0x01f6
        L_0x0147:
            r5.groupCount()     // Catch:{ Exception -> 0x0214 }
            int r12 = r5.start(r4)     // Catch:{ Exception -> 0x0214 }
            int r13 = r5.end(r4)     // Catch:{ Exception -> 0x0214 }
            int r14 = r5.start(r11)     // Catch:{ Exception -> 0x0214 }
            int r11 = r5.end(r11)     // Catch:{ Exception -> 0x0214 }
            int r15 = r5.start(r3)     // Catch:{ Exception -> 0x0214 }
            int r4 = r5.end(r3)     // Catch:{ Exception -> 0x0214 }
            int r9 = r5.start(r2)     // Catch:{ Exception -> 0x0214 }
            int r10 = r5.end(r2)     // Catch:{ Exception -> 0x0214 }
            java.lang.CharSequence r11 = r0.subSequence(r14, r11)     // Catch:{ Exception -> 0x0214 }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r11)     // Catch:{ Exception -> 0x0214 }
            int r11 = r11.intValue()     // Catch:{ Exception -> 0x0214 }
            java.lang.CharSequence r14 = r0.subSequence(r15, r4)     // Catch:{ Exception -> 0x0214 }
            java.lang.Integer r14 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r14)     // Catch:{ Exception -> 0x0214 }
            int r14 = r14.intValue()     // Catch:{ Exception -> 0x0214 }
            if (r12 < 0) goto L_0x0193
            if (r13 < 0) goto L_0x0193
            java.lang.CharSequence r12 = r0.subSequence(r12, r13)     // Catch:{ Exception -> 0x0214 }
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt((java.lang.CharSequence) r12)     // Catch:{ Exception -> 0x0214 }
            int r12 = r12.intValue()     // Catch:{ Exception -> 0x0214 }
            goto L_0x0194
        L_0x0193:
            r12 = -1
        L_0x0194:
            if (r9 < 0) goto L_0x01a2
            if (r10 >= 0) goto L_0x0199
            goto L_0x01a2
        L_0x0199:
            java.lang.CharSequence r13 = r0.subSequence(r9, r10)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r13 = r13.toString()     // Catch:{ Exception -> 0x0214 }
            goto L_0x01a3
        L_0x01a2:
            r13 = 0
        L_0x01a3:
            if (r9 >= 0) goto L_0x01a7
            if (r10 < 0) goto L_0x01a8
        L_0x01a7:
            r8 = r4
        L_0x01a8:
            java.lang.Class<android.text.style.URLSpan> r4 = android.text.style.URLSpan.class
            java.lang.Object[] r4 = r6.getSpans(r7, r8, r4)     // Catch:{ Exception -> 0x0214 }
            android.text.style.URLSpan[] r4 = (android.text.style.URLSpan[]) r4     // Catch:{ Exception -> 0x0214 }
            if (r4 == 0) goto L_0x01b6
            int r4 = r4.length     // Catch:{ Exception -> 0x0214 }
            if (r4 <= 0) goto L_0x01b6
            goto L_0x0211
        L_0x01b6:
            int r11 = r11 * 60
            int r14 = r14 + r11
            if (r12 <= 0) goto L_0x01c0
            int r12 = r12 * 60
            int r12 = r12 * 60
            int r14 = r14 + r12
        L_0x01c0:
            r4 = r20
            if (r14 <= r4) goto L_0x01c5
            goto L_0x0211
        L_0x01c5:
            if (r1 != r3) goto L_0x01de
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0214 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0214 }
            r10.<init>()     // Catch:{ Exception -> 0x0214 }
            java.lang.String r11 = "video?"
            r10.append(r11)     // Catch:{ Exception -> 0x0214 }
            r10.append(r14)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0214 }
            r9.<init>(r10)     // Catch:{ Exception -> 0x0214 }
            goto L_0x01f4
        L_0x01de:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x0214 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0214 }
            r10.<init>()     // Catch:{ Exception -> 0x0214 }
            java.lang.String r11 = "audio?"
            r10.append(r11)     // Catch:{ Exception -> 0x0214 }
            r10.append(r14)     // Catch:{ Exception -> 0x0214 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0214 }
            r9.<init>(r10)     // Catch:{ Exception -> 0x0214 }
        L_0x01f4:
            r9.label = r13     // Catch:{ Exception -> 0x0214 }
        L_0x01f6:
            if (r9 == 0) goto L_0x0211
            if (r21 == 0) goto L_0x020d
            java.lang.Class<android.text.style.ClickableSpan> r10 = android.text.style.ClickableSpan.class
            java.lang.Object[] r10 = r6.getSpans(r7, r8, r10)     // Catch:{ Exception -> 0x0214 }
            android.text.style.ClickableSpan[] r10 = (android.text.style.ClickableSpan[]) r10     // Catch:{ Exception -> 0x0214 }
            if (r10 == 0) goto L_0x020d
            int r11 = r10.length     // Catch:{ Exception -> 0x0214 }
            if (r11 <= 0) goto L_0x020d
            r11 = 0
            r10 = r10[r11]     // Catch:{ Exception -> 0x0214 }
            r6.removeSpan(r10)     // Catch:{ Exception -> 0x0214 }
        L_0x020d:
            r10 = 0
            r6.setSpan(r9, r7, r8, r10)     // Catch:{ Exception -> 0x0214 }
        L_0x0211:
            r4 = 1
            goto L_0x004c
        L_0x0214:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0218:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addUrlsByPattern(boolean, java.lang.CharSequence, boolean, int, int, boolean):void");
    }

    public static int[] getWebDocumentWidthAndHeight(TLRPC$WebDocument tLRPC$WebDocument) {
        if (tLRPC$WebDocument == null) {
            return null;
        }
        int size = tLRPC$WebDocument.attributes.size();
        int i = 0;
        while (i < size) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$WebDocument.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) {
                return new int[]{tLRPC$DocumentAttribute.w, tLRPC$DocumentAttribute.h};
            } else if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                return new int[]{tLRPC$DocumentAttribute.w, tLRPC$DocumentAttribute.h};
            } else {
                i++;
            }
        }
        return null;
    }

    public static int getWebDocumentDuration(TLRPC$WebDocument tLRPC$WebDocument) {
        if (tLRPC$WebDocument == null) {
            return 0;
        }
        int size = tLRPC$WebDocument.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$WebDocument.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                return tLRPC$DocumentAttribute.duration;
            }
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                return tLRPC$DocumentAttribute.duration;
            }
        }
        return 0;
    }

    public static int[] getInlineResultWidthAndHeight(TLRPC$BotInlineResult tLRPC$BotInlineResult) {
        int[] webDocumentWidthAndHeight = getWebDocumentWidthAndHeight(tLRPC$BotInlineResult.content);
        if (webDocumentWidthAndHeight != null) {
            return webDocumentWidthAndHeight;
        }
        int[] webDocumentWidthAndHeight2 = getWebDocumentWidthAndHeight(tLRPC$BotInlineResult.thumb);
        if (webDocumentWidthAndHeight2 == null) {
            return new int[]{0, 0};
        }
        return webDocumentWidthAndHeight2;
    }

    public static int getInlineResultDuration(TLRPC$BotInlineResult tLRPC$BotInlineResult) {
        int webDocumentDuration = getWebDocumentDuration(tLRPC$BotInlineResult.content);
        return webDocumentDuration == 0 ? getWebDocumentDuration(tLRPC$BotInlineResult.thumb) : webDocumentDuration;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r0 = r5.photoThumbs;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasValidGroupId() {
        /*
            r5 = this;
            long r0 = r5.getGroupId()
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0022
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r5.photoThumbs
            if (r0 == 0) goto L_0x0014
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0020
        L_0x0014:
            boolean r0 = r5.isMusic()
            if (r0 != 0) goto L_0x0020
            boolean r0 = r5.isDocument()
            if (r0 == 0) goto L_0x0022
        L_0x0020:
            r0 = 1
            goto L_0x0023
        L_0x0022:
            r0 = 0
        L_0x0023:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.hasValidGroupId():boolean");
    }

    public long getGroupIdForUse() {
        long j = this.localSentGroupId;
        return j != 0 ? j : this.messageOwner.grouped_id;
    }

    public long getGroupId() {
        long j = this.localGroupId;
        return j != 0 ? j : getGroupIdForUse();
    }

    public static void addLinks(boolean z, CharSequence charSequence) {
        addLinks(z, charSequence, true, false);
    }

    public static void addLinks(boolean z, CharSequence charSequence, boolean z2, boolean z3) {
        addLinks(z, charSequence, z2, z3, false);
    }

    public static void addLinks(boolean z, CharSequence charSequence, boolean z2, boolean z3, boolean z4) {
        if ((charSequence instanceof Spannable) && containsUrls(charSequence)) {
            if (charSequence.length() < 1000) {
                try {
                    AndroidUtilities.addLinks((Spannable) charSequence, 5, z4);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                try {
                    AndroidUtilities.addLinks((Spannable) charSequence, 1, z4);
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2);
                }
            }
            addUrlsByPattern(z, charSequence, z2, 0, 0, z3);
        }
    }

    public void resetPlayingProgress() {
        this.audioProgress = 0.0f;
        this.audioProgressSec = 0;
        this.bufferedProgress = 0.0f;
    }

    private boolean addEntitiesToText(CharSequence charSequence, boolean z) {
        return addEntitiesToText(charSequence, false, z);
    }

    public boolean addEntitiesToText(CharSequence charSequence, boolean z, boolean z2) {
        if (charSequence == null) {
            return false;
        }
        if (this.isRestrictedMessage) {
            ArrayList arrayList = new ArrayList();
            TLRPC$TL_messageEntityItalic tLRPC$TL_messageEntityItalic = new TLRPC$TL_messageEntityItalic();
            tLRPC$TL_messageEntityItalic.offset = 0;
            tLRPC$TL_messageEntityItalic.length = charSequence.length();
            arrayList.add(tLRPC$TL_messageEntityItalic);
            return addEntitiesToText(charSequence, arrayList, isOutOwner(), true, z, z2);
        }
        return addEntitiesToText(charSequence, this.messageOwner.entities, isOutOwner(), true, z, z2);
    }

    public void replaceEmojiToLottieFrame(CharSequence charSequence, int[] iArr) {
        int i;
        if (charSequence instanceof Spannable) {
            Spannable spannable = (Spannable) charSequence;
            Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) spannable.getSpans(0, spannable.length(), Emoji.EmojiSpan.class);
            AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannable.getSpans(0, spannable.length(), AnimatedEmojiSpan.class);
            if (emojiSpanArr != null) {
                if (iArr == null) {
                    i = 0;
                } else {
                    i = iArr[0];
                }
                if ((i - emojiSpanArr.length) - (animatedEmojiSpanArr == null ? 0 : animatedEmojiSpanArr.length) <= 0) {
                    for (int i2 = 0; i2 < emojiSpanArr.length; i2++) {
                        TLRPC$Document emojiAnimatedSticker2 = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emojiSpanArr[i2].emoji);
                        if (emojiAnimatedSticker2 != null) {
                            int spanStart = spannable.getSpanStart(emojiSpanArr[i2]);
                            int spanEnd = spannable.getSpanEnd(emojiSpanArr[i2]);
                            spannable.removeSpan(emojiSpanArr[i2]);
                            AnimatedEmojiSpan animatedEmojiSpan = new AnimatedEmojiSpan(emojiAnimatedSticker2, emojiSpanArr[i2].fontMetrics);
                            animatedEmojiSpan.standard = true;
                            spannable.setSpan(animatedEmojiSpan, spanStart, spanEnd, 33);
                        }
                    }
                }
            }
        }
    }

    public static Spannable replaceAnimatedEmoji(CharSequence charSequence, ArrayList<TLRPC$MessageEntity> arrayList, Paint.FontMetricsInt fontMetricsInt) {
        AnimatedEmojiSpan animatedEmojiSpan;
        Spannable spannableString = charSequence instanceof Spannable ? (Spannable) charSequence : new SpannableString(charSequence);
        if (arrayList == null) {
            return spannableString;
        }
        Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) spannableString.getSpans(0, spannableString.length(), Emoji.EmojiSpan.class);
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$MessageEntity tLRPC$MessageEntity = arrayList.get(i);
            if (tLRPC$MessageEntity instanceof TLRPC$TL_messageEntityCustomEmoji) {
                TLRPC$TL_messageEntityCustomEmoji tLRPC$TL_messageEntityCustomEmoji = (TLRPC$TL_messageEntityCustomEmoji) tLRPC$MessageEntity;
                for (int i2 = 0; i2 < emojiSpanArr.length; i2++) {
                    Emoji.EmojiSpan emojiSpan = emojiSpanArr[i2];
                    if (emojiSpan != null) {
                        int spanStart = spannableString.getSpanStart(emojiSpan);
                        int spanEnd = spannableString.getSpanEnd(emojiSpan);
                        if (tLRPC$TL_messageEntityCustomEmoji.offset == spanStart && tLRPC$TL_messageEntityCustomEmoji.length == spanEnd - spanStart) {
                            spannableString.removeSpan(emojiSpan);
                            emojiSpanArr[i2] = null;
                        }
                    }
                }
                if (tLRPC$MessageEntity.offset + tLRPC$MessageEntity.length <= spannableString.length()) {
                    int i3 = tLRPC$MessageEntity.offset;
                    AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannableString.getSpans(i3, tLRPC$MessageEntity.length + i3, AnimatedEmojiSpan.class);
                    if (animatedEmojiSpanArr != null && animatedEmojiSpanArr.length > 0) {
                        for (AnimatedEmojiSpan removeSpan : animatedEmojiSpanArr) {
                            spannableString.removeSpan(removeSpan);
                        }
                    }
                    if (tLRPC$TL_messageEntityCustomEmoji.document != null) {
                        animatedEmojiSpan = new AnimatedEmojiSpan(tLRPC$TL_messageEntityCustomEmoji.document, fontMetricsInt);
                    } else {
                        animatedEmojiSpan = new AnimatedEmojiSpan(tLRPC$TL_messageEntityCustomEmoji.document_id, fontMetricsInt);
                    }
                    int i4 = tLRPC$MessageEntity.offset;
                    spannableString.setSpan(animatedEmojiSpan, i4, tLRPC$MessageEntity.length + i4, 33);
                }
            }
        }
        return spannableString;
    }

    /* JADX WARNING: Removed duplicated region for block: B:120:0x017b  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x022a  */
    /* JADX WARNING: Removed duplicated region for block: B:202:0x03e9  */
    /* JADX WARNING: Removed duplicated region for block: B:205:0x03fd  */
    /* JADX WARNING: Removed duplicated region for block: B:210:0x022f A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean addEntitiesToText(java.lang.CharSequence r17, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r18, boolean r19, boolean r20, boolean r21, boolean r22) {
        /*
            r0 = r17
            boolean r1 = r0 instanceof android.text.Spannable
            r2 = 0
            if (r1 != 0) goto L_0x0008
            return r2
        L_0x0008:
            r1 = r0
            android.text.Spannable r1 = (android.text.Spannable) r1
            int r3 = r17.length()
            java.lang.Class<android.text.style.URLSpan> r4 = android.text.style.URLSpan.class
            java.lang.Object[] r3 = r1.getSpans(r2, r3, r4)
            android.text.style.URLSpan[] r3 = (android.text.style.URLSpan[]) r3
            if (r3 == 0) goto L_0x001e
            int r4 = r3.length
            if (r4 <= 0) goto L_0x001e
            r4 = 1
            goto L_0x001f
        L_0x001e:
            r4 = 0
        L_0x001f:
            boolean r5 = r18.isEmpty()
            if (r5 == 0) goto L_0x0026
            return r4
        L_0x0026:
            if (r21 == 0) goto L_0x002a
            r10 = 2
            goto L_0x002f
        L_0x002a:
            if (r19 == 0) goto L_0x002e
            r10 = 1
            goto L_0x002f
        L_0x002e:
            r10 = 0
        L_0x002f:
            java.util.ArrayList r11 = new java.util.ArrayList
            r11.<init>()
            java.util.ArrayList r6 = new java.util.ArrayList
            r7 = r18
            r6.<init>(r7)
            org.telegram.messenger.MessageObject$$ExternalSyntheticLambda2 r7 = org.telegram.messenger.MessageObject$$ExternalSyntheticLambda2.INSTANCE
            java.util.Collections.sort(r6, r7)
            int r7 = r6.size()
            r8 = 0
        L_0x0045:
            r13 = 0
            if (r8 >= r7) goto L_0x0234
            java.lang.Object r15 = r6.get(r8)
            org.telegram.tgnet.TLRPC$MessageEntity r15 = (org.telegram.tgnet.TLRPC$MessageEntity) r15
            int r2 = r15.length
            if (r2 <= 0) goto L_0x022e
            int r2 = r15.offset
            if (r2 < 0) goto L_0x022e
            int r12 = r17.length()
            if (r2 < r12) goto L_0x005e
            goto L_0x022e
        L_0x005e:
            int r2 = r15.offset
            int r12 = r15.length
            int r2 = r2 + r12
            int r12 = r17.length()
            if (r2 <= r12) goto L_0x0072
            int r2 = r17.length()
            int r12 = r15.offset
            int r2 = r2 - r12
            r15.length = r2
        L_0x0072:
            if (r22 == 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBold
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityItalic
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityStrike
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUnderline
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCode
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPre
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMentionName
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler
            if (r2 != 0) goto L_0x00a4
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCustomEmoji
            if (r2 == 0) goto L_0x00d8
        L_0x00a4:
            if (r3 == 0) goto L_0x00d8
            int r2 = r3.length
            if (r2 <= 0) goto L_0x00d8
            r2 = 0
        L_0x00aa:
            int r12 = r3.length
            if (r2 >= r12) goto L_0x00d8
            r12 = r3[r2]
            if (r12 != 0) goto L_0x00b2
            goto L_0x00d5
        L_0x00b2:
            r12 = r3[r2]
            int r12 = r1.getSpanStart(r12)
            r5 = r3[r2]
            int r5 = r1.getSpanEnd(r5)
            int r9 = r15.offset
            if (r9 > r12) goto L_0x00c7
            int r14 = r15.length
            int r14 = r14 + r9
            if (r14 >= r12) goto L_0x00ce
        L_0x00c7:
            if (r9 > r5) goto L_0x00d5
            int r12 = r15.length
            int r9 = r9 + r12
            if (r9 < r5) goto L_0x00d5
        L_0x00ce:
            r5 = r3[r2]
            r1.removeSpan(r5)
            r3[r2] = r13
        L_0x00d5:
            int r2 = r2 + 1
            goto L_0x00aa
        L_0x00d8:
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCustomEmoji
            if (r2 == 0) goto L_0x00de
            goto L_0x022e
        L_0x00de:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r2 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r2.<init>()
            int r5 = r15.offset
            r2.start = r5
            int r9 = r15.length
            int r5 = r5 + r9
            r2.end = r5
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler
            if (r5 == 0) goto L_0x00f7
            r5 = 256(0x100, float:3.59E-43)
            r2.flags = r5
        L_0x00f4:
            r5 = 2
            goto L_0x0174
        L_0x00f7:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityStrike
            if (r5 == 0) goto L_0x0100
            r5 = 8
            r2.flags = r5
            goto L_0x00f4
        L_0x0100:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUnderline
            if (r5 == 0) goto L_0x0109
            r5 = 16
            r2.flags = r5
            goto L_0x00f4
        L_0x0109:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote
            if (r5 == 0) goto L_0x0112
            r5 = 32
            r2.flags = r5
            goto L_0x00f4
        L_0x0112:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBold
            if (r5 == 0) goto L_0x011a
            r5 = 1
            r2.flags = r5
            goto L_0x00f4
        L_0x011a:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityItalic
            if (r5 == 0) goto L_0x0122
            r5 = 2
            r2.flags = r5
            goto L_0x0174
        L_0x0122:
            r5 = 2
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCode
            if (r9 != 0) goto L_0x0171
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPre
            if (r9 == 0) goto L_0x012c
            goto L_0x0171
        L_0x012c:
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMentionName
            r12 = 64
            if (r9 == 0) goto L_0x013b
            if (r20 != 0) goto L_0x0136
            goto L_0x022e
        L_0x0136:
            r2.flags = r12
            r2.urlEntity = r15
            goto L_0x0174
        L_0x013b:
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            if (r9 == 0) goto L_0x0148
            if (r20 != 0) goto L_0x0143
            goto L_0x022e
        L_0x0143:
            r2.flags = r12
            r2.urlEntity = r15
            goto L_0x0174
        L_0x0148:
            if (r22 == 0) goto L_0x0150
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r9 != 0) goto L_0x0150
            goto L_0x022e
        L_0x0150:
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            if (r9 != 0) goto L_0x0158
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r9 == 0) goto L_0x0162
        L_0x0158:
            java.lang.String r9 = r15.url
            boolean r9 = org.telegram.messenger.browser.Browser.isPassportUrl(r9)
            if (r9 == 0) goto L_0x0162
            goto L_0x022e
        L_0x0162:
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMention
            if (r9 == 0) goto L_0x016a
            if (r20 != 0) goto L_0x016a
            goto L_0x022e
        L_0x016a:
            r9 = 128(0x80, float:1.794E-43)
            r2.flags = r9
            r2.urlEntity = r15
            goto L_0x0174
        L_0x0171:
            r9 = 4
            r2.flags = r9
        L_0x0174:
            int r9 = r11.size()
            r12 = 0
        L_0x0179:
            if (r12 >= r9) goto L_0x0223
            java.lang.Object r13 = r11.get(r12)
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r13 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r13
            int r14 = r13.flags
            r15 = 256(0x100, float:3.59E-43)
            r14 = r14 & r15
            if (r14 == 0) goto L_0x0195
            int r14 = r2.start
            int r15 = r13.start
            if (r14 < r15) goto L_0x0195
            int r14 = r2.end
            int r15 = r13.end
            if (r14 > r15) goto L_0x0195
            goto L_0x01e3
        L_0x0195:
            int r14 = r2.start
            int r15 = r13.start
            if (r14 <= r15) goto L_0x01df
            int r15 = r13.end
            if (r14 < r15) goto L_0x01a0
            goto L_0x01e3
        L_0x01a0:
            int r14 = r2.end
            if (r14 >= r15) goto L_0x01c3
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r14.<init>(r2)
            r14.merge(r13)
            int r12 = r12 + 1
            int r9 = r9 + 1
            r11.add(r12, r14)
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r14.<init>(r13)
            int r15 = r2.end
            r14.start = r15
            r15 = 1
            int r12 = r12 + r15
            int r9 = r9 + r15
            r11.add(r12, r14)
            goto L_0x01d6
        L_0x01c3:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r14.<init>(r2)
            r14.merge(r13)
            int r15 = r13.end
            r14.end = r15
            int r12 = r12 + 1
            int r9 = r9 + 1
            r11.add(r12, r14)
        L_0x01d6:
            int r14 = r2.start
            int r15 = r13.end
            r2.start = r15
            r13.end = r14
            goto L_0x01e3
        L_0x01df:
            int r14 = r2.end
            if (r15 < r14) goto L_0x01e6
        L_0x01e3:
            r5 = r9
            r9 = 1
            goto L_0x021e
        L_0x01e6:
            int r5 = r13.end
            if (r14 != r5) goto L_0x01ee
            r13.merge(r2)
            goto L_0x021b
        L_0x01ee:
            if (r14 >= r5) goto L_0x0208
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r5.<init>(r13)
            r5.merge(r2)
            int r14 = r2.end
            r5.end = r14
            int r12 = r12 + 1
            int r9 = r9 + 1
            r11.add(r12, r5)
            int r5 = r2.end
            r13.start = r5
            goto L_0x021b
        L_0x0208:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r5.<init>(r2)
            int r14 = r13.end
            r5.start = r14
            int r12 = r12 + 1
            int r9 = r9 + 1
            r11.add(r12, r5)
            r13.merge(r2)
        L_0x021b:
            r2.end = r15
            goto L_0x01e3
        L_0x021e:
            int r12 = r12 + r9
            r9 = r5
            r5 = 2
            goto L_0x0179
        L_0x0223:
            r9 = 1
            int r5 = r2.start
            int r12 = r2.end
            if (r5 >= r12) goto L_0x022f
            r11.add(r2)
            goto L_0x022f
        L_0x022e:
            r9 = 1
        L_0x022f:
            int r8 = r8 + 1
            r2 = 0
            goto L_0x0045
        L_0x0234:
            r9 = 1
            int r2 = r11.size()
            r12 = r4
            r14 = 0
        L_0x023b:
            if (r14 >= r2) goto L_0x0405
            java.lang.Object r3 = r11.get(r14)
            r15 = r3
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r15 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r15
            org.telegram.tgnet.TLRPC$MessageEntity r3 = r15.urlEntity
            if (r3 == 0) goto L_0x0252
            int r4 = r3.offset
            int r3 = r3.length
            int r3 = r3 + r4
            java.lang.String r3 = android.text.TextUtils.substring(r0, r4, r3)
            goto L_0x0253
        L_0x0252:
            r3 = r13
        L_0x0253:
            org.telegram.tgnet.TLRPC$MessageEntity r4 = r15.urlEntity
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBotCommand
            r8 = 33
            if (r5 == 0) goto L_0x026d
            org.telegram.ui.Components.URLSpanBotCommand r4 = new org.telegram.ui.Components.URLSpanBotCommand
            r4.<init>(r3, r10, r15)
            int r3 = r15.start
            int r5 = r15.end
            r1.setSpan(r4, r3, r5, r8)
        L_0x0267:
            r13 = 33
            r16 = 4
            goto L_0x03e6
        L_0x026d:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityHashtag
            if (r5 != 0) goto L_0x03d6
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMention
            if (r5 != 0) goto L_0x03d6
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCashtag
            if (r5 == 0) goto L_0x027b
            goto L_0x03d6
        L_0x027b:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityEmail
            if (r5 == 0) goto L_0x029d
            org.telegram.ui.Components.URLSpanReplacement r4 = new org.telegram.ui.Components.URLSpanReplacement
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "mailto:"
            r5.append(r6)
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            r4.<init>(r3, r15)
            int r3 = r15.start
            int r5 = r15.end
            r1.setSpan(r4, r3, r5, r8)
            goto L_0x0267
        L_0x029d:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            if (r5 == 0) goto L_0x02d8
            java.lang.String r4 = r3.toLowerCase()
            java.lang.String r5 = "://"
            boolean r4 = r4.contains(r5)
            if (r4 != 0) goto L_0x02cb
            org.telegram.ui.Components.URLSpanBrowser r4 = new org.telegram.ui.Components.URLSpanBrowser
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "http://"
            r5.append(r6)
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            r4.<init>(r3, r15)
            int r3 = r15.start
            int r5 = r15.end
            r1.setSpan(r4, r3, r5, r8)
            goto L_0x02f9
        L_0x02cb:
            org.telegram.ui.Components.URLSpanBrowser r4 = new org.telegram.ui.Components.URLSpanBrowser
            r4.<init>(r3, r15)
            int r3 = r15.start
            int r5 = r15.end
            r1.setSpan(r4, r3, r5, r8)
            goto L_0x02f9
        L_0x02d8:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBankCard
            if (r5 == 0) goto L_0x0301
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "card:"
            r5.append(r6)
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            r4.<init>((java.lang.String) r3, (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r15)
            int r3 = r15.start
            int r5 = r15.end
            r1.setSpan(r4, r3, r5, r8)
        L_0x02f9:
            r5 = 0
            r12 = 1
            r13 = 33
            r16 = 4
            goto L_0x03e7
        L_0x0301:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPhone
            if (r5 == 0) goto L_0x033e
            java.lang.String r4 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r3)
            java.lang.String r5 = "+"
            boolean r3 = r3.startsWith(r5)
            if (r3 == 0) goto L_0x0320
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r5)
            r3.append(r4)
            java.lang.String r4 = r3.toString()
        L_0x0320:
            org.telegram.ui.Components.URLSpanBrowser r3 = new org.telegram.ui.Components.URLSpanBrowser
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "tel:"
            r5.append(r6)
            r5.append(r4)
            java.lang.String r4 = r5.toString()
            r3.<init>(r4, r15)
            int r4 = r15.start
            int r5 = r15.end
            r1.setSpan(r3, r4, r5, r8)
            goto L_0x02f9
        L_0x033e:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r3 == 0) goto L_0x0354
            org.telegram.ui.Components.URLSpanReplacement r3 = new org.telegram.ui.Components.URLSpanReplacement
            org.telegram.tgnet.TLRPC$MessageEntity r4 = r15.urlEntity
            java.lang.String r4 = r4.url
            r3.<init>(r4, r15)
            int r4 = r15.start
            int r5 = r15.end
            r1.setSpan(r3, r4, r5, r8)
            goto L_0x0267
        L_0x0354:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMentionName
            java.lang.String r5 = ""
            if (r3 == 0) goto L_0x037d
            org.telegram.ui.Components.URLSpanUserMention r3 = new org.telegram.ui.Components.URLSpanUserMention
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$MessageEntity r5 = r15.urlEntity
            org.telegram.tgnet.TLRPC$TL_messageEntityMentionName r5 = (org.telegram.tgnet.TLRPC$TL_messageEntityMentionName) r5
            long r5 = r5.user_id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4, r10, r15)
            int r4 = r15.start
            int r5 = r15.end
            r1.setSpan(r3, r4, r5, r8)
            goto L_0x0267
        L_0x037d:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            if (r3 == 0) goto L_0x03a6
            org.telegram.ui.Components.URLSpanUserMention r3 = new org.telegram.ui.Components.URLSpanUserMention
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r5)
            org.telegram.tgnet.TLRPC$MessageEntity r5 = r15.urlEntity
            org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName r5 = (org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName) r5
            org.telegram.tgnet.TLRPC$InputUser r5 = r5.user_id
            long r5 = r5.user_id
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            r3.<init>(r4, r10, r15)
            int r4 = r15.start
            int r5 = r15.end
            r1.setSpan(r3, r4, r5, r8)
            goto L_0x0267
        L_0x03a6:
            int r3 = r15.flags
            r16 = 4
            r3 = r3 & 4
            if (r3 == 0) goto L_0x03c6
            org.telegram.ui.Components.URLSpanMono r7 = new org.telegram.ui.Components.URLSpanMono
            int r5 = r15.start
            int r6 = r15.end
            r3 = r7
            r4 = r1
            r9 = r7
            r7 = r10
            r13 = 33
            r8 = r15
            r3.<init>(r4, r5, r6, r7, r8)
            int r3 = r15.start
            int r4 = r15.end
            r1.setSpan(r9, r3, r4, r13)
            goto L_0x03e6
        L_0x03c6:
            r13 = 33
            org.telegram.ui.Components.TextStyleSpan r3 = new org.telegram.ui.Components.TextStyleSpan
            r3.<init>(r15)
            int r4 = r15.start
            int r5 = r15.end
            r1.setSpan(r3, r4, r5, r13)
            r5 = 1
            goto L_0x03e7
        L_0x03d6:
            r13 = 33
            r16 = 4
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            r4.<init>((java.lang.String) r3, (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r15)
            int r3 = r15.start
            int r5 = r15.end
            r1.setSpan(r4, r3, r5, r13)
        L_0x03e6:
            r5 = 0
        L_0x03e7:
            if (r5 != 0) goto L_0x03fd
            int r3 = r15.flags
            r4 = 256(0x100, float:3.59E-43)
            r3 = r3 & r4
            if (r3 == 0) goto L_0x03ff
            org.telegram.ui.Components.TextStyleSpan r3 = new org.telegram.ui.Components.TextStyleSpan
            r3.<init>(r15)
            int r5 = r15.start
            int r6 = r15.end
            r1.setSpan(r3, r5, r6, r13)
            goto L_0x03ff
        L_0x03fd:
            r4 = 256(0x100, float:3.59E-43)
        L_0x03ff:
            int r14 = r14 + 1
            r9 = 1
            r13 = 0
            goto L_0x023b
        L_0x0405:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addEntitiesToText(java.lang.CharSequence, java.util.ArrayList, boolean, boolean, boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$addEntitiesToText$2(TLRPC$MessageEntity tLRPC$MessageEntity, TLRPC$MessageEntity tLRPC$MessageEntity2) {
        int i = tLRPC$MessageEntity.offset;
        int i2 = tLRPC$MessageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public boolean needDrawShareButton() {
        int i;
        String str;
        if (this.preview || this.scheduled || this.eventId != 0) {
            return false;
        }
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message.noforwards) {
            return false;
        }
        if (tLRPC$Message.fwd_from != null && !isOutOwner() && this.messageOwner.fwd_from.saved_from_peer != null && getDialogId() == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return true;
        }
        int i2 = this.type;
        if (!(i2 == 13 || i2 == 15 || i2 == 19)) {
            TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
            if (tLRPC$MessageFwdHeader != null && (tLRPC$MessageFwdHeader.from_id instanceof TLRPC$TL_peerChannel) && !isOutOwner()) {
                return true;
            }
            if (!isFromUser()) {
                TLRPC$Message tLRPC$Message2 = this.messageOwner;
                if ((!(tLRPC$Message2.from_id instanceof TLRPC$TL_peerChannel) && !tLRPC$Message2.post) || isSupergroup()) {
                    return false;
                }
                TLRPC$Message tLRPC$Message3 = this.messageOwner;
                if (tLRPC$Message3.peer_id.channel_id == 0 || ((tLRPC$Message3.via_bot_id != 0 || tLRPC$Message3.reply_to != null) && ((i = this.type) == 13 || i == 15))) {
                    return false;
                }
                return true;
            } else if ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaEmpty) || getMedia(this.messageOwner) == null || ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && !(getMedia(this.messageOwner).webpage instanceof TLRPC$TL_webPage))) {
                return false;
            } else {
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
                if (user != null && user.bot && !hasExtendedMedia()) {
                    return true;
                }
                if (!isOut()) {
                    if ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGame) || ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaInvoice) && !hasExtendedMedia())) {
                        return true;
                    }
                    TLRPC$Peer tLRPC$Peer = this.messageOwner.peer_id;
                    TLRPC$Chat tLRPC$Chat = null;
                    if (tLRPC$Peer != null) {
                        long j = tLRPC$Peer.channel_id;
                        if (j != 0) {
                            tLRPC$Chat = getChat((AbstractMap<Long, TLRPC$Chat>) null, (LongSparseArray<TLRPC$Chat>) null, j);
                        }
                    }
                    if (!ChatObject.isChannel(tLRPC$Chat) || !tLRPC$Chat.megagroup || (str = tLRPC$Chat.username) == null || str.length() <= 0 || (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaContact) || (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGeo)) {
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isYouTubeVideo() {
        return (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && getMedia(this.messageOwner).webpage != null && !TextUtils.isEmpty(getMedia(this.messageOwner).webpage.embed_url) && "YouTube".equals(getMedia(this.messageOwner).webpage.site_name);
    }

    public int getMaxMessageTextWidth() {
        if (!AndroidUtilities.isTablet() || this.eventId == 0) {
            this.generatedWithMinSize = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : getParentWidth();
        } else {
            this.generatedWithMinSize = AndroidUtilities.dp(530.0f);
        }
        this.generatedWithDensity = AndroidUtilities.density;
        int i = 0;
        if ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && getMedia(this.messageOwner).webpage != null && "telegram_background".equals(getMedia(this.messageOwner).webpage.type)) {
            try {
                Uri parse = Uri.parse(getMedia(this.messageOwner).webpage.url);
                String lastPathSegment = parse.getLastPathSegment();
                if (parse.getQueryParameter("bg_color") != null) {
                    i = AndroidUtilities.dp(220.0f);
                } else if (lastPathSegment.length() == 6 || (lastPathSegment.length() == 13 && lastPathSegment.charAt(6) == '-')) {
                    i = AndroidUtilities.dp(200.0f);
                }
            } catch (Exception unused) {
            }
        } else if (isAndroidTheme()) {
            i = AndroidUtilities.dp(200.0f);
        }
        if (i == 0) {
            int dp = this.generatedWithMinSize - AndroidUtilities.dp((!needDrawAvatarInternal() || isOutOwner() || this.messageOwner.isThreadMessage) ? 80.0f : 132.0f);
            if (needDrawShareButton() && !isOutOwner()) {
                dp -= AndroidUtilities.dp(10.0f);
            }
            i = getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaGame ? dp - AndroidUtilities.dp(10.0f) : dp;
        }
        int i2 = this.emojiOnlyCount;
        if (i2 < 1) {
            return i;
        }
        int i3 = this.totalAnimatedEmojiCount;
        int i4 = 100;
        if (i3 > 100) {
            return i;
        }
        int i5 = i2 - i3;
        if (SharedConfig.getDevicePerformanceClass() < 2) {
            i4 = 50;
        }
        if (i5 < i4) {
            return (hasValidReplyMessageObject() || isForwarded()) ? Math.min(i, (int) (((float) this.generatedWithMinSize) * 0.65f)) : i;
        }
        return i;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:38:0x007f, code lost:
        if ((getMedia(r0) instanceof org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported) == false) goto L_0x0083;
     */
    /* JADX WARNING: Removed duplicated region for block: B:159:0x034f A[SYNTHETIC, Splitter:B:159:0x034f] */
    /* JADX WARNING: Removed duplicated region for block: B:166:0x0374  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0397  */
    /* JADX WARNING: Removed duplicated region for block: B:197:0x03e6  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x03eb  */
    /* JADX WARNING: Removed duplicated region for block: B:203:0x03fe  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x04c2  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00a0  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x00e0  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0104  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0107  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0122 A[Catch:{ Exception -> 0x056e }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0154 A[Catch:{ Exception -> 0x056e }] */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0183  */
    /* JADX WARNING: Removed duplicated region for block: B:83:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0195  */
    /* JADX WARNING: Removed duplicated region for block: B:90:0x0197  */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x01a7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateLayout(org.telegram.tgnet.TLRPC$User r32) {
        /*
            r31 = this;
            r1 = r31
            int r0 = r1.type
            if (r0 == 0) goto L_0x000a
            r2 = 19
            if (r0 != r2) goto L_0x0572
        L_0x000a:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            if (r0 == 0) goto L_0x0572
            java.lang.CharSequence r0 = r1.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x001a
            goto L_0x0572
        L_0x001a:
            r31.generateLinkDescription()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1.textLayoutBlocks = r0
            r2 = 0
            r1.textWidth = r2
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r3 = r0.send_state
            r4 = 1
            if (r3 == 0) goto L_0x0030
            r0 = 0
            goto L_0x0037
        L_0x0030:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r4
        L_0x0037:
            if (r0 != 0) goto L_0x0083
            long r5 = r1.eventId
            r7 = 0
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x0081
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_old
            if (r3 != 0) goto L_0x0081
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_old2
            if (r3 != 0) goto L_0x0081
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_old3
            if (r3 != 0) goto L_0x0081
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_old4
            if (r3 != 0) goto L_0x0081
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageForwarded_old
            if (r3 != 0) goto L_0x0081
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageForwarded_old2
            if (r3 != 0) goto L_0x0081
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_secret
            if (r3 != 0) goto L_0x0081
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r0 != 0) goto L_0x0081
            boolean r0 = r31.isOut()
            if (r0 == 0) goto L_0x0073
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.send_state
            if (r0 != 0) goto L_0x0081
        L_0x0073:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r3 = r0.id
            if (r3 < 0) goto L_0x0081
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported
            if (r0 == 0) goto L_0x0083
        L_0x0081:
            r0 = 1
            goto L_0x0084
        L_0x0083:
            r0 = 0
        L_0x0084:
            if (r0 == 0) goto L_0x008f
            boolean r3 = r31.isOutOwner()
            java.lang.CharSequence r5 = r1.messageText
            addLinks(r3, r5, r4, r4)
        L_0x008f:
            boolean r3 = r31.isYouTubeVideo()
            if (r3 != 0) goto L_0x00e0
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            if (r3 == 0) goto L_0x00a0
            boolean r3 = r3.isYouTubeVideo()
            if (r3 == 0) goto L_0x00a0
            goto L_0x00e0
        L_0x00a0:
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            if (r3 == 0) goto L_0x00f0
            boolean r3 = r3.isVideo()
            if (r3 == 0) goto L_0x00bd
            boolean r5 = r31.isOutOwner()
            java.lang.CharSequence r6 = r1.messageText
            r7 = 0
            r8 = 3
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            int r9 = r3.getDuration()
            r10 = 0
            addUrlsByPattern(r5, r6, r7, r8, r9, r10)
            goto L_0x00f0
        L_0x00bd:
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            boolean r3 = r3.isMusic()
            if (r3 != 0) goto L_0x00cd
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            boolean r3 = r3.isVoice()
            if (r3 == 0) goto L_0x00f0
        L_0x00cd:
            boolean r5 = r31.isOutOwner()
            java.lang.CharSequence r6 = r1.messageText
            r7 = 0
            r8 = 4
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            int r9 = r3.getDuration()
            r10 = 0
            addUrlsByPattern(r5, r6, r7, r8, r9, r10)
            goto L_0x00f0
        L_0x00e0:
            boolean r11 = r31.isOutOwner()
            java.lang.CharSequence r12 = r1.messageText
            r13 = 0
            r14 = 3
            r15 = 2147483647(0x7fffffff, float:NaN)
            r16 = 0
            addUrlsByPattern(r11, r12, r13, r14, r15, r16)
        L_0x00f0:
            java.lang.CharSequence r3 = r1.messageText
            boolean r3 = r1.addEntitiesToText(r3, r0)
            int r15 = r31.getMaxMessageTextWidth()
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = getMedia(r0)
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0107
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint
            goto L_0x0109
        L_0x0107:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
        L_0x0109:
            r14 = r0
            int r0 = r1.totalAnimatedEmojiCount
            r5 = 4
            r13 = 0
            if (r0 < r5) goto L_0x0115
            r0 = -1082130432(0xffffffffbvar_, float:-1.0)
            r12 = -1082130432(0xffffffffbvar_, float:-1.0)
            goto L_0x0116
        L_0x0115:
            r12 = 0
        L_0x0116:
            android.text.Layout$Alignment r11 = android.text.Layout.Alignment.ALIGN_NORMAL
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x056e }
            r10 = 28
            r9 = 1065353216(0x3var_, float:1.0)
            r8 = 24
            if (r0 < r8) goto L_0x0154
            java.lang.CharSequence r5 = r1.messageText     // Catch:{ Exception -> 0x056e }
            int r6 = r5.length()     // Catch:{ Exception -> 0x056e }
            android.text.StaticLayout$Builder r5 = android.text.StaticLayout.Builder.obtain(r5, r2, r6, r14, r15)     // Catch:{ Exception -> 0x056e }
            android.text.StaticLayout$Builder r5 = r5.setLineSpacing(r12, r9)     // Catch:{ Exception -> 0x056e }
            android.text.StaticLayout$Builder r5 = r5.setBreakStrategy(r4)     // Catch:{ Exception -> 0x056e }
            android.text.StaticLayout$Builder r5 = r5.setHyphenationFrequency(r2)     // Catch:{ Exception -> 0x056e }
            android.text.StaticLayout$Builder r5 = r5.setAlignment(r11)     // Catch:{ Exception -> 0x056e }
            int r6 = r1.emojiOnlyCount     // Catch:{ Exception -> 0x056e }
            if (r6 <= 0) goto L_0x0148
            r5.setIncludePad(r2)     // Catch:{ Exception -> 0x056e }
            if (r0 < r10) goto L_0x0148
            r5.setUseLineSpacingFromFallbacks(r2)     // Catch:{ Exception -> 0x056e }
        L_0x0148:
            android.text.StaticLayout r5 = r5.build()     // Catch:{ Exception -> 0x056e }
            r21 = r11
            r22 = r12
            r2 = 24
            r12 = r5
            goto L_0x0171
        L_0x0154:
            android.text.StaticLayout r16 = new android.text.StaticLayout     // Catch:{ Exception -> 0x056e }
            java.lang.CharSequence r6 = r1.messageText     // Catch:{ Exception -> 0x056e }
            r17 = 1065353216(0x3var_, float:1.0)
            r18 = 0
            r5 = r16
            r7 = r14
            r2 = 24
            r8 = r15
            r9 = r11
            r10 = r17
            r21 = r11
            r11 = r12
            r22 = r12
            r12 = r18
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x056e }
            r12 = r16
        L_0x0171:
            int r5 = r12.getHeight()
            r1.textHeight = r5
            int r5 = r12.getLineCount()
            r1.linesCount = r5
            int r6 = r1.totalAnimatedEmojiCount
            r7 = 50
            if (r6 < r7) goto L_0x0186
            r8 = 5
            r11 = 5
            goto L_0x018a
        L_0x0186:
            r8 = 10
            r11 = 10
        L_0x018a:
            if (r0 < r2) goto L_0x0191
            if (r6 >= r7) goto L_0x0191
            r16 = 1
            goto L_0x0193
        L_0x0191:
            r16 = 0
        L_0x0193:
            if (r16 == 0) goto L_0x0197
            r10 = 1
            goto L_0x01a1
        L_0x0197:
            float r0 = (float) r5
            float r5 = (float) r11
            float r0 = r0 / r5
            double r5 = (double) r0
            double r5 = java.lang.Math.ceil(r5)
            int r0 = (int) r5
            r10 = r0
        L_0x01a1:
            r8 = 0
            r9 = 0
            r17 = 0
        L_0x01a5:
            if (r9 >= r10) goto L_0x056d
            if (r16 == 0) goto L_0x01ac
            int r0 = r1.linesCount
            goto L_0x01b3
        L_0x01ac:
            int r0 = r1.linesCount
            int r0 = r0 - r8
            int r0 = java.lang.Math.min(r11, r0)
        L_0x01b3:
            org.telegram.messenger.MessageObject$TextLayoutBlock r7 = new org.telegram.messenger.MessageObject$TextLayoutBlock
            r7.<init>()
            r6 = 2
            if (r10 != r4) goto L_0x0236
            r7.textLayout = r12
            r7.textYOffset = r13
            r5 = 0
            r7.charactersOffset = r5
            java.lang.CharSequence r5 = r12.getText()
            int r5 = r5.length()
            r7.charactersEnd = r5
            int r5 = r1.emojiOnlyCount
            if (r5 == 0) goto L_0x021e
            if (r5 == r4) goto L_0x0207
            if (r5 == r6) goto L_0x01f0
            r6 = 3
            if (r5 == r6) goto L_0x01d8
            goto L_0x021e
        L_0x01d8:
            int r5 = r1.textHeight
            r6 = 1082549862(0x40866666, float:4.2)
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r23
            r1.textHeight = r5
            float r5 = r7.textYOffset
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 - r6
            r7.textYOffset = r5
            goto L_0x021e
        L_0x01f0:
            int r5 = r1.textHeight
            r6 = 1083179008(0x40900000, float:4.5)
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r23
            r1.textHeight = r5
            float r5 = r7.textYOffset
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 - r6
            r7.textYOffset = r5
            goto L_0x021e
        L_0x0207:
            int r5 = r1.textHeight
            r6 = 1084856730(0x40a9999a, float:5.3)
            int r23 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r23
            r1.textHeight = r5
            float r5 = r7.textYOffset
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 - r6
            r7.textYOffset = r5
        L_0x021e:
            int r5 = r1.textHeight
            r7.height = r5
            r2 = r7
            r6 = r8
            r8 = r9
            r9 = r10
            r20 = r11
            r5 = r12
            r23 = r14
            r4 = r21
            r18 = 2
            r19 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r10 = r0
            goto L_0x039a
        L_0x0236:
            int r5 = r12.getLineStart(r8)
            int r6 = r8 + r0
            int r6 = r6 - r4
            int r6 = r12.getLineEnd(r6)
            if (r6 >= r5) goto L_0x0259
            r24 = r3
            r27 = r8
            r4 = r9
            r9 = r10
            r20 = r11
            r30 = r12
            r23 = r14
            r25 = r21
            r3 = 0
            r5 = 1
            r19 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            goto L_0x0557
        L_0x0259:
            r7.charactersOffset = r5
            r7.charactersEnd = r6
            java.lang.CharSequence r13 = r1.messageText     // Catch:{ Exception -> 0x053f }
            java.lang.CharSequence r5 = r13.subSequence(r5, r6)     // Catch:{ Exception -> 0x053f }
            android.text.SpannableStringBuilder r6 = android.text.SpannableStringBuilder.valueOf(r5)     // Catch:{ Exception -> 0x053f }
            if (r3 == 0) goto L_0x0304
            int r5 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x02f2 }
            if (r5 < r2) goto L_0x0304
            int r13 = r6.length()     // Catch:{ Exception -> 0x02dd }
            r24 = 1073741824(0x40000000, float:2.0)
            int r24 = org.telegram.messenger.AndroidUtilities.dp(r24)     // Catch:{ Exception -> 0x02dd }
            int r2 = r15 + r24
            r24 = r12
            r12 = 0
            android.text.StaticLayout$Builder r2 = android.text.StaticLayout.Builder.obtain(r6, r12, r13, r14, r2)     // Catch:{ Exception -> 0x02db }
            r13 = r22
            r6 = 1065353216(0x3var_, float:1.0)
            android.text.StaticLayout$Builder r2 = r2.setLineSpacing(r13, r6)     // Catch:{ Exception -> 0x02d1 }
            android.text.StaticLayout$Builder r2 = r2.setBreakStrategy(r4)     // Catch:{ Exception -> 0x02d1 }
            android.text.StaticLayout$Builder r2 = r2.setHyphenationFrequency(r12)     // Catch:{ Exception -> 0x02d1 }
            r4 = r21
            android.text.StaticLayout$Builder r2 = r2.setAlignment(r4)     // Catch:{ Exception -> 0x02c3 }
            int r6 = r1.emojiOnlyCount     // Catch:{ Exception -> 0x02c3 }
            if (r6 <= 0) goto L_0x02a5
            r2.setIncludePad(r12)     // Catch:{ Exception -> 0x02c3 }
            r6 = 28
            if (r5 < r6) goto L_0x02a7
            r2.setUseLineSpacingFromFallbacks(r12)     // Catch:{ Exception -> 0x02c3 }
            goto L_0x02a7
        L_0x02a5:
            r6 = 28
        L_0x02a7:
            android.text.StaticLayout r2 = r2.build()     // Catch:{ Exception -> 0x02c3 }
            r7.textLayout = r2     // Catch:{ Exception -> 0x02c3 }
            r2 = r7
            r6 = r8
            r28 = r9
            r29 = r10
            r20 = r11
            r22 = r13
            r23 = r14
            r5 = r24
            r18 = 2
            r19 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            goto L_0x0344
        L_0x02c3:
            r0 = move-exception
            r25 = r4
            r27 = r8
            r4 = r9
            r9 = r10
            r20 = r11
            r22 = r13
            r23 = r14
            goto L_0x02eb
        L_0x02d1:
            r0 = move-exception
            r27 = r8
            r4 = r9
            r9 = r10
            r20 = r11
            r22 = r13
            goto L_0x02e7
        L_0x02db:
            r0 = move-exception
            goto L_0x02e1
        L_0x02dd:
            r0 = move-exception
            r24 = r12
            r12 = 0
        L_0x02e1:
            r27 = r8
            r4 = r9
            r9 = r10
            r20 = r11
        L_0x02e7:
            r23 = r14
            r25 = r21
        L_0x02eb:
            r30 = r24
            r5 = 1
            r19 = 1065353216(0x3var_, float:1.0)
            goto L_0x0539
        L_0x02f2:
            r0 = move-exception
            r24 = r12
            r12 = 0
            r19 = 1065353216(0x3var_, float:1.0)
            r27 = r8
            r4 = r9
            r9 = r10
            r20 = r11
            r23 = r14
            r25 = r21
            goto L_0x0536
        L_0x0304:
            r24 = r12
            r4 = r21
            r13 = r22
            r2 = 28
            r12 = 0
            r19 = 1065353216(0x3var_, float:1.0)
            android.text.StaticLayout r5 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0529 }
            r20 = 0
            int r21 = r6.length()     // Catch:{ Exception -> 0x0529 }
            r22 = 1065353216(0x3var_, float:1.0)
            r25 = 0
            r26 = r5
            r18 = 2
            r2 = r7
            r7 = r20
            r27 = r8
            r8 = r21
            r28 = r9
            r9 = r14
            r29 = r10
            r10 = r15
            r20 = r11
            r11 = r4
            r30 = r24
            r21 = 0
            r12 = r22
            r22 = r13
            r23 = r14
            r14 = r25
            r5.<init>(r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x051d }
            r2.textLayout = r5     // Catch:{ Exception -> 0x051d }
            r6 = r27
            r5 = r30
        L_0x0344:
            int r7 = r5.getLineTop(r6)     // Catch:{ Exception -> 0x0513 }
            float r7 = (float) r7     // Catch:{ Exception -> 0x0513 }
            r2.textYOffset = r7     // Catch:{ Exception -> 0x0513 }
            r8 = r28
            if (r8 == 0) goto L_0x0358
            int r9 = r1.emojiOnlyCount     // Catch:{ Exception -> 0x0508 }
            if (r9 > 0) goto L_0x0358
            float r7 = r7 - r17
            int r7 = (int) r7     // Catch:{ Exception -> 0x0508 }
            r2.height = r7     // Catch:{ Exception -> 0x0508 }
        L_0x0358:
            int r7 = r2.height     // Catch:{ Exception -> 0x0508 }
            android.text.StaticLayout r9 = r2.textLayout     // Catch:{ Exception -> 0x0508 }
            int r10 = r9.getLineCount()     // Catch:{ Exception -> 0x0508 }
            r11 = 1
            int r10 = r10 - r11
            int r9 = r9.getLineBottom(r10)     // Catch:{ Exception -> 0x0508 }
            int r7 = java.lang.Math.max(r7, r9)     // Catch:{ Exception -> 0x0508 }
            r2.height = r7     // Catch:{ Exception -> 0x0508 }
            float r7 = r2.textYOffset     // Catch:{ Exception -> 0x0508 }
            r9 = r29
            int r10 = r9 + -1
            if (r8 != r10) goto L_0x0397
            android.text.StaticLayout r10 = r2.textLayout
            int r10 = r10.getLineCount()
            int r10 = java.lang.Math.max(r0, r10)
            int r0 = r1.textHeight     // Catch:{ Exception -> 0x0392 }
            float r11 = r2.textYOffset     // Catch:{ Exception -> 0x0392 }
            android.text.StaticLayout r12 = r2.textLayout     // Catch:{ Exception -> 0x0392 }
            int r12 = r12.getHeight()     // Catch:{ Exception -> 0x0392 }
            float r12 = (float) r12     // Catch:{ Exception -> 0x0392 }
            float r11 = r11 + r12
            int r11 = (int) r11     // Catch:{ Exception -> 0x0392 }
            int r0 = java.lang.Math.max(r0, r11)     // Catch:{ Exception -> 0x0392 }
            r1.textHeight = r0     // Catch:{ Exception -> 0x0392 }
            goto L_0x0398
        L_0x0392:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x0398
        L_0x0397:
            r10 = r0
        L_0x0398:
            r17 = r7
        L_0x039a:
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r2.spoilers
            r0.clear()
            boolean r0 = r1.isSpoilersRevealed
            if (r0 != 0) goto L_0x03ab
            android.text.StaticLayout r0 = r2.textLayout
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r7 = r2.spoilers
            r11 = 0
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r11, r0, r11, r7)
        L_0x03ab:
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r1.textLayoutBlocks
            r0.add(r2)
            android.text.StaticLayout r0 = r2.textLayout     // Catch:{ Exception -> 0x03c4 }
            int r7 = r10 + -1
            float r13 = r0.getLineLeft(r7)     // Catch:{ Exception -> 0x03c4 }
            r7 = 0
            if (r8 != 0) goto L_0x03ce
            int r0 = (r13 > r7 ? 1 : (r13 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x03ce
            r1.textXOffset = r13     // Catch:{ Exception -> 0x03c2 }
            goto L_0x03ce
        L_0x03c2:
            r0 = move-exception
            goto L_0x03c6
        L_0x03c4:
            r0 = move-exception
            r7 = 0
        L_0x03c6:
            if (r8 != 0) goto L_0x03ca
            r1.textXOffset = r7
        L_0x03ca:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r13 = 0
        L_0x03ce:
            android.text.StaticLayout r0 = r2.textLayout     // Catch:{ Exception -> 0x03d7 }
            int r11 = r10 + -1
            float r0 = r0.getLineWidth(r11)     // Catch:{ Exception -> 0x03d7 }
            goto L_0x03dc
        L_0x03d7:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x03dc:
            double r11 = (double) r0
            double r11 = java.lang.Math.ceil(r11)
            int r0 = (int) r11
            int r11 = r15 + 80
            if (r0 <= r11) goto L_0x03e7
            r0 = r15
        L_0x03e7:
            int r11 = r9 + -1
            if (r8 != r11) goto L_0x03ed
            r1.lastLineWidth = r0
        L_0x03ed:
            float r12 = (float) r0
            float r14 = java.lang.Math.max(r7, r13)
            float r14 = r14 + r12
            r28 = r8
            double r7 = (double) r14
            double r7 = java.lang.Math.ceil(r7)
            int r7 = (int) r7
            r8 = 1
            if (r10 <= r8) goto L_0x04c2
            r8 = r0
            r24 = r3
            r25 = r4
            r4 = r7
            r3 = 0
            r12 = 0
            r13 = 0
            r14 = 0
        L_0x0408:
            if (r12 >= r10) goto L_0x049b
            android.text.StaticLayout r0 = r2.textLayout     // Catch:{ Exception -> 0x0413 }
            float r0 = r0.getLineWidth(r12)     // Catch:{ Exception -> 0x0413 }
            r26 = r0
            goto L_0x0419
        L_0x0413:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r26 = 0
        L_0x0419:
            android.text.StaticLayout r0 = r2.textLayout     // Catch:{ Exception -> 0x0420 }
            float r0 = r0.getLineLeft(r12)     // Catch:{ Exception -> 0x0420 }
            goto L_0x0425
        L_0x0420:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x0425:
            r27 = r0
            int r0 = r15 + 20
            float r0 = (float) r0
            int r0 = (r26 > r0 ? 1 : (r26 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0436
            float r0 = (float) r15
            r30 = r5
            r26 = 0
            r5 = r0
            r0 = 0
            goto L_0x043e
        L_0x0436:
            r30 = r5
            r5 = r26
            r0 = r27
            r26 = 0
        L_0x043e:
            int r27 = (r0 > r26 ? 1 : (r0 == r26 ? 0 : -1))
            if (r27 <= 0) goto L_0x0458
            r27 = r6
            float r6 = r1.textXOffset
            float r6 = java.lang.Math.min(r6, r0)
            r1.textXOffset = r6
            byte r6 = r2.directionFlags
            r26 = r10
            r10 = 1
            r6 = r6 | r10
            byte r6 = (byte) r6
            r2.directionFlags = r6
            r1.hasRtl = r10
            goto L_0x0463
        L_0x0458:
            r27 = r6
            r26 = r10
            byte r6 = r2.directionFlags
            r6 = r6 | 2
            byte r6 = (byte) r6
            r2.directionFlags = r6
        L_0x0463:
            if (r13 != 0) goto L_0x0474
            r6 = 0
            int r10 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r10 != 0) goto L_0x0474
            android.text.StaticLayout r6 = r2.textLayout     // Catch:{ Exception -> 0x0473 }
            int r6 = r6.getParagraphDirection(r12)     // Catch:{ Exception -> 0x0473 }
            r10 = 1
            if (r6 != r10) goto L_0x0474
        L_0x0473:
            r13 = 1
        L_0x0474:
            float r3 = java.lang.Math.max(r3, r5)
            float r0 = r0 + r5
            float r14 = java.lang.Math.max(r14, r0)
            double r5 = (double) r5
            double r5 = java.lang.Math.ceil(r5)
            int r5 = (int) r5
            int r8 = java.lang.Math.max(r8, r5)
            double r5 = (double) r0
            double r5 = java.lang.Math.ceil(r5)
            int r0 = (int) r5
            int r4 = java.lang.Math.max(r4, r0)
            int r12 = r12 + 1
            r10 = r26
            r6 = r27
            r5 = r30
            goto L_0x0408
        L_0x049b:
            r30 = r5
            r27 = r6
            r26 = r10
            if (r13 == 0) goto L_0x04aa
            r4 = r28
            if (r4 != r11) goto L_0x04b1
            r1.lastLineWidth = r7
            goto L_0x04b1
        L_0x04aa:
            r4 = r28
            if (r4 != r11) goto L_0x04b0
            r1.lastLineWidth = r8
        L_0x04b0:
            r14 = r3
        L_0x04b1:
            int r0 = r1.textWidth
            double r2 = (double) r14
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            int r0 = java.lang.Math.max(r0, r2)
            r1.textWidth = r0
            r3 = 0
            r5 = 1
            goto L_0x0504
        L_0x04c2:
            r24 = r3
            r25 = r4
            r30 = r5
            r27 = r6
            r26 = r10
            r4 = r28
            r3 = 0
            int r5 = (r13 > r3 ? 1 : (r13 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x04f0
            float r5 = r1.textXOffset
            float r5 = java.lang.Math.min(r5, r13)
            r1.textXOffset = r5
            int r5 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x04e1
            float r12 = r12 + r13
            int r0 = (int) r12
        L_0x04e1:
            r5 = 1
            if (r9 == r5) goto L_0x04e6
            r6 = 1
            goto L_0x04e7
        L_0x04e6:
            r6 = 0
        L_0x04e7:
            r1.hasRtl = r6
            byte r6 = r2.directionFlags
            r6 = r6 | r5
            byte r6 = (byte) r6
            r2.directionFlags = r6
            goto L_0x04f8
        L_0x04f0:
            r5 = 1
            byte r6 = r2.directionFlags
            r6 = r6 | 2
            byte r6 = (byte) r6
            r2.directionFlags = r6
        L_0x04f8:
            int r2 = r1.textWidth
            int r0 = java.lang.Math.min(r15, r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.textWidth = r0
        L_0x0504:
            int r8 = r27 + r26
            goto L_0x0559
        L_0x0508:
            r0 = move-exception
            r24 = r3
            r25 = r4
            r30 = r5
            r27 = r6
            r4 = r8
            goto L_0x0524
        L_0x0513:
            r0 = move-exception
            r24 = r3
            r25 = r4
            r30 = r5
            r27 = r6
            goto L_0x0522
        L_0x051d:
            r0 = move-exception
            r24 = r3
            r25 = r4
        L_0x0522:
            r4 = r28
        L_0x0524:
            r9 = r29
            r3 = 0
            r5 = 1
            goto L_0x0554
        L_0x0529:
            r0 = move-exception
            r25 = r4
            r27 = r8
            r4 = r9
            r9 = r10
            r20 = r11
            r22 = r13
            r23 = r14
        L_0x0536:
            r30 = r24
            r5 = 1
        L_0x0539:
            r21 = 0
            r24 = r3
            r3 = 0
            goto L_0x0554
        L_0x053f:
            r0 = move-exception
            r24 = r3
            r27 = r8
            r4 = r9
            r9 = r10
            r20 = r11
            r30 = r12
            r23 = r14
            r25 = r21
            r3 = 0
            r5 = 1
            r19 = 1065353216(0x3var_, float:1.0)
            r21 = 0
        L_0x0554:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0557:
            r8 = r27
        L_0x0559:
            int r0 = r4 + 1
            r10 = r9
            r11 = r20
            r14 = r23
            r3 = r24
            r21 = r25
            r12 = r30
            r2 = 24
            r4 = 1
            r13 = 0
            r9 = r0
            goto L_0x01a5
        L_0x056d:
            return
        L_0x056e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0572:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLayout(org.telegram.tgnet.TLRPC$User):void");
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    public boolean isOutOwner() {
        TLRPC$Peer tLRPC$Peer;
        if (this.preview) {
            return true;
        }
        TLRPC$Peer tLRPC$Peer2 = this.messageOwner.peer_id;
        TLRPC$Chat tLRPC$Chat = null;
        if (tLRPC$Peer2 != null) {
            long j = tLRPC$Peer2.channel_id;
            if (j != 0) {
                tLRPC$Chat = getChat((AbstractMap<Long, TLRPC$Chat>) null, (LongSparseArray<TLRPC$Chat>) null, j);
            }
        }
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message.out) {
            TLRPC$Peer tLRPC$Peer3 = tLRPC$Message.from_id;
            if ((tLRPC$Peer3 instanceof TLRPC$TL_peerUser) || ((tLRPC$Peer3 instanceof TLRPC$TL_peerChannel) && (!ChatObject.isChannel(tLRPC$Chat) || tLRPC$Chat.megagroup))) {
                TLRPC$Message tLRPC$Message2 = this.messageOwner;
                if (!tLRPC$Message2.post) {
                    if (tLRPC$Message2.fwd_from == null) {
                        return true;
                    }
                    long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    if (getDialogId() == clientUserId) {
                        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
                        TLRPC$Peer tLRPC$Peer4 = tLRPC$MessageFwdHeader.from_id;
                        if ((tLRPC$Peer4 instanceof TLRPC$TL_peerUser) && tLRPC$Peer4.user_id == clientUserId && ((tLRPC$Peer = tLRPC$MessageFwdHeader.saved_from_peer) == null || tLRPC$Peer.user_id == clientUserId)) {
                            return true;
                        }
                        TLRPC$Peer tLRPC$Peer5 = tLRPC$MessageFwdHeader.saved_from_peer;
                        if (tLRPC$Peer5 != null && tLRPC$Peer5.user_id == clientUserId && (tLRPC$Peer4 == null || tLRPC$Peer4.user_id == clientUserId)) {
                            return true;
                        }
                        return false;
                    }
                    TLRPC$Peer tLRPC$Peer6 = this.messageOwner.fwd_from.saved_from_peer;
                    if (tLRPC$Peer6 == null || tLRPC$Peer6.user_id == clientUserId) {
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    public boolean needDrawAvatar() {
        if (this.customAvatarDrawable != null) {
            return true;
        }
        if (isSponsored() && isFromChat()) {
            return true;
        }
        if (!isSponsored()) {
            if (isFromUser() || isFromGroup() || this.eventId != 0) {
                return true;
            }
            TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
            if (tLRPC$MessageFwdHeader == null || tLRPC$MessageFwdHeader.saved_from_peer == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean needDrawAvatarInternal() {
        if (this.customAvatarDrawable != null) {
            return true;
        }
        if (!isSponsored()) {
            if ((isFromChat() && isFromUser()) || isFromGroup() || this.eventId != 0) {
                return true;
            }
            TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
            if (tLRPC$MessageFwdHeader == null || tLRPC$MessageFwdHeader.saved_from_peer == null) {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isFromChat() {
        TLRPC$Peer tLRPC$Peer;
        if (getDialogId() == UserConfig.getInstance(this.currentAccount).clientUserId) {
            return true;
        }
        TLRPC$Peer tLRPC$Peer2 = this.messageOwner.peer_id;
        TLRPC$Chat tLRPC$Chat = null;
        if (tLRPC$Peer2 != null) {
            long j = tLRPC$Peer2.channel_id;
            if (j != 0) {
                tLRPC$Chat = getChat((AbstractMap<Long, TLRPC$Chat>) null, (LongSparseArray<TLRPC$Chat>) null, j);
            }
        }
        if ((ChatObject.isChannel(tLRPC$Chat) && tLRPC$Chat.megagroup) || ((tLRPC$Peer = this.messageOwner.peer_id) != null && tLRPC$Peer.chat_id != 0)) {
            return true;
        }
        if (tLRPC$Peer == null || tLRPC$Peer.channel_id == 0 || tLRPC$Chat == null || !tLRPC$Chat.megagroup) {
            return false;
        }
        return true;
    }

    public static long getFromChatId(TLRPC$Message tLRPC$Message) {
        return getPeerId(tLRPC$Message.from_id);
    }

    public static long getPeerId(TLRPC$Peer tLRPC$Peer) {
        long j;
        if (tLRPC$Peer == null) {
            return 0;
        }
        if (tLRPC$Peer instanceof TLRPC$TL_peerChat) {
            j = tLRPC$Peer.chat_id;
        } else if (!(tLRPC$Peer instanceof TLRPC$TL_peerChannel)) {
            return tLRPC$Peer.user_id;
        } else {
            j = tLRPC$Peer.channel_id;
        }
        return -j;
    }

    public long getFromChatId() {
        return getFromChatId(this.messageOwner);
    }

    public long getChatId() {
        TLRPC$Peer tLRPC$Peer = this.messageOwner.peer_id;
        if (tLRPC$Peer instanceof TLRPC$TL_peerChat) {
            return tLRPC$Peer.chat_id;
        }
        if (tLRPC$Peer instanceof TLRPC$TL_peerChannel) {
            return tLRPC$Peer.channel_id;
        }
        return 0;
    }

    public boolean isFromUser() {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        return (tLRPC$Message.from_id instanceof TLRPC$TL_peerUser) && !tLRPC$Message.post;
    }

    public boolean isFromGroup() {
        TLRPC$Peer tLRPC$Peer = this.messageOwner.peer_id;
        TLRPC$Chat tLRPC$Chat = null;
        if (tLRPC$Peer != null) {
            long j = tLRPC$Peer.channel_id;
            if (j != 0) {
                tLRPC$Chat = getChat((AbstractMap<Long, TLRPC$Chat>) null, (LongSparseArray<TLRPC$Chat>) null, j);
            }
        }
        return (this.messageOwner.from_id instanceof TLRPC$TL_peerChannel) && ChatObject.isChannel(tLRPC$Chat) && tLRPC$Chat.megagroup;
    }

    public boolean isForwardedChannelPost() {
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        TLRPC$Peer tLRPC$Peer = tLRPC$Message.from_id;
        if (!(!(tLRPC$Peer instanceof TLRPC$TL_peerChannel) || (tLRPC$MessageFwdHeader = tLRPC$Message.fwd_from) == null || tLRPC$MessageFwdHeader.channel_post == 0)) {
            TLRPC$Peer tLRPC$Peer2 = tLRPC$MessageFwdHeader.saved_from_peer;
            return (tLRPC$Peer2 instanceof TLRPC$TL_peerChannel) && tLRPC$Peer.channel_id == tLRPC$Peer2.channel_id;
        }
    }

    public boolean isUnread() {
        return this.messageOwner.unread;
    }

    public boolean isContentUnread() {
        return this.messageOwner.media_unread;
    }

    public void setIsRead() {
        this.messageOwner.unread = false;
    }

    public int getUnradFlags() {
        return getUnreadFlags(this.messageOwner);
    }

    public static int getUnreadFlags(TLRPC$Message tLRPC$Message) {
        boolean z = !tLRPC$Message.unread;
        return !tLRPC$Message.media_unread ? z | true ? 1 : 0 : z ? 1 : 0;
    }

    public void setContentIsRead() {
        this.messageOwner.media_unread = false;
    }

    public int getId() {
        return this.messageOwner.id;
    }

    public int getRealId() {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        int i = tLRPC$Message.realId;
        return i != 0 ? i : tLRPC$Message.id;
    }

    public static long getMessageSize(TLRPC$Message tLRPC$Message) {
        TLRPC$Document tLRPC$Document;
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            tLRPC$Document = getMedia(tLRPC$Message).webpage.document;
        } else if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaGame) {
            tLRPC$Document = getMedia(tLRPC$Message).game.document;
        } else {
            tLRPC$Document = getMedia(tLRPC$Message) != null ? getMedia(tLRPC$Message).document : null;
        }
        if (tLRPC$Document != null) {
            return tLRPC$Document.size;
        }
        return 0;
    }

    public long getSize() {
        return getMessageSize(this.messageOwner);
    }

    public static void fixMessagePeer(ArrayList<TLRPC$Message> arrayList, long j) {
        if (arrayList != null && !arrayList.isEmpty() && j != 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC$Message tLRPC$Message = arrayList.get(i);
                if (tLRPC$Message instanceof TLRPC$TL_messageEmpty) {
                    TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                    tLRPC$Message.peer_id = tLRPC$TL_peerChannel;
                    tLRPC$TL_peerChannel.channel_id = j;
                }
            }
        }
    }

    public long getChannelId() {
        return getChannelId(this.messageOwner);
    }

    public static long getChannelId(TLRPC$Message tLRPC$Message) {
        TLRPC$Peer tLRPC$Peer = tLRPC$Message.peer_id;
        if (tLRPC$Peer != null) {
            return tLRPC$Peer.channel_id;
        }
        return 0;
    }

    public static boolean shouldEncryptPhotoOrVideo(TLRPC$Message tLRPC$Message) {
        int i;
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            if (((getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) || isVideoMessage(tLRPC$Message)) && (i = tLRPC$Message.ttl) > 0 && i <= 60) {
                return true;
            }
            return false;
        } else if (((getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) || (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument)) && getMedia(tLRPC$Message).ttl_seconds != 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean shouldEncryptPhotoOrVideo() {
        return shouldEncryptPhotoOrVideo(this.messageOwner);
    }

    public static boolean isSecretPhotoOrVideo(TLRPC$Message tLRPC$Message) {
        int i;
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            if (((getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) || isRoundVideoMessage(tLRPC$Message) || isVideoMessage(tLRPC$Message)) && (i = tLRPC$Message.ttl) > 0 && i <= 60) {
                return true;
            }
            return false;
        } else if (!(tLRPC$Message instanceof TLRPC$TL_message)) {
            return false;
        } else {
            if (((getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) || (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument)) && getMedia(tLRPC$Message).ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public static boolean isSecretMedia(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            if (((getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) || isRoundVideoMessage(tLRPC$Message) || isVideoMessage(tLRPC$Message)) && getMedia(tLRPC$Message).ttl_seconds != 0) {
                return true;
            }
            return false;
        } else if (!(tLRPC$Message instanceof TLRPC$TL_message)) {
            return false;
        } else {
            if (((getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) || (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument)) && getMedia(tLRPC$Message).ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean needDrawBluredPreview() {
        if (hasExtendedMediaPreview()) {
            return true;
        }
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            int max = Math.max(tLRPC$Message.ttl, getMedia(tLRPC$Message).ttl_seconds);
            if (max <= 0 || (((!(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto) && !isVideo() && !isGif()) || max > 60) && !isRoundVideo())) {
                return false;
            }
            return true;
        } else if (!(tLRPC$Message instanceof TLRPC$TL_message) || getMedia(tLRPC$Message) == null || getMedia(this.messageOwner).ttl_seconds == 0 || (!(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto) && !(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument))) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSecretMedia() {
        int i;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            if ((((getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) || isGif()) && (i = this.messageOwner.ttl) > 0 && i <= 60) || isVoice() || isRoundVideo() || isVideo()) {
                return true;
            }
            return false;
        } else if (!(tLRPC$Message instanceof TLRPC$TL_message) || getMedia(tLRPC$Message) == null || getMedia(this.messageOwner).ttl_seconds == 0 || (!(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto) && !(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument))) {
            return false;
        } else {
            return true;
        }
    }

    public static void setUnreadFlags(TLRPC$Message tLRPC$Message, int i) {
        boolean z = false;
        tLRPC$Message.unread = (i & 1) == 0;
        if ((i & 2) == 0) {
            z = true;
        }
        tLRPC$Message.media_unread = z;
    }

    public static boolean isUnread(TLRPC$Message tLRPC$Message) {
        return tLRPC$Message.unread;
    }

    public static boolean isContentUnread(TLRPC$Message tLRPC$Message) {
        return tLRPC$Message.media_unread;
    }

    public boolean isSavedFromMegagroup() {
        TLRPC$Peer tLRPC$Peer;
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
        if (tLRPC$MessageFwdHeader == null || (tLRPC$Peer = tLRPC$MessageFwdHeader.saved_from_peer) == null || tLRPC$Peer.channel_id == 0) {
            return false;
        }
        return ChatObject.isMegagroup(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.saved_from_peer.channel_id)));
    }

    public static boolean isOut(TLRPC$Message tLRPC$Message) {
        return tLRPC$Message.out;
    }

    public long getDialogId() {
        return getDialogId(this.messageOwner);
    }

    public boolean canStreamVideo() {
        TLRPC$Document document = getDocument();
        if (document != null && !(document instanceof TLRPC$TL_documentEncrypted)) {
            if (SharedConfig.streamAllVideo) {
                return true;
            }
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = document.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                    return tLRPC$DocumentAttribute.supports_streaming;
                }
            }
            if (!SharedConfig.streamMkv || !"video/x-matroska".equals(document.mime_type)) {
                return false;
            }
            return true;
        }
        return false;
    }

    public static long getDialogId(TLRPC$Message tLRPC$Message) {
        TLRPC$Peer tLRPC$Peer;
        if (tLRPC$Message.dialog_id == 0 && (tLRPC$Peer = tLRPC$Message.peer_id) != null) {
            long j = tLRPC$Peer.chat_id;
            if (j != 0) {
                tLRPC$Message.dialog_id = -j;
            } else {
                long j2 = tLRPC$Peer.channel_id;
                if (j2 != 0) {
                    tLRPC$Message.dialog_id = -j2;
                } else if (tLRPC$Message.from_id == null || isOut(tLRPC$Message)) {
                    tLRPC$Message.dialog_id = tLRPC$Message.peer_id.user_id;
                } else {
                    tLRPC$Message.dialog_id = tLRPC$Message.from_id.user_id;
                }
            }
        }
        return tLRPC$Message.dialog_id;
    }

    public boolean isSending() {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        return tLRPC$Message.send_state == 1 && tLRPC$Message.id < 0;
    }

    public boolean isEditing() {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        return tLRPC$Message.send_state == 3 && tLRPC$Message.id > 0;
    }

    public boolean isEditingMedia() {
        if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto) {
            if (getMedia(this.messageOwner).photo.id == 0) {
                return true;
            }
            return false;
        } else if (!(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument) || getMedia(this.messageOwner).document.dc_id != 0) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSendError() {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        return (tLRPC$Message.send_state == 2 && tLRPC$Message.id < 0) || (this.scheduled && tLRPC$Message.id > 0 && tLRPC$Message.date < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + -60);
    }

    public boolean isSent() {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        return tLRPC$Message.send_state == 0 || tLRPC$Message.id > 0;
    }

    public int getSecretTimeLeft() {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        int i = tLRPC$Message.ttl;
        int i2 = tLRPC$Message.destroyTime;
        return i2 != 0 ? Math.max(0, i2 - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) : i;
    }

    public String getSecretTimeString() {
        if (!isSecretMedia()) {
            return null;
        }
        int secretTimeLeft = getSecretTimeLeft();
        if (secretTimeLeft < 60) {
            return secretTimeLeft + "s";
        }
        return (secretTimeLeft / 60) + "m";
    }

    public String getDocumentName() {
        return FileLoader.getDocumentFileName(getDocument());
    }

    public static boolean isWebM(TLRPC$Document tLRPC$Document) {
        return tLRPC$Document != null && "video/webm".equals(tLRPC$Document.mime_type);
    }

    public static boolean isVideoSticker(TLRPC$Document tLRPC$Document) {
        return tLRPC$Document != null && isVideoStickerDocument(tLRPC$Document);
    }

    public boolean isVideoSticker() {
        return getDocument() != null && isVideoStickerDocument(getDocument());
    }

    public static boolean isStickerDocument(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            int i = 0;
            while (i < tLRPC$Document.attributes.size()) {
                if (!(tLRPC$Document.attributes.get(i) instanceof TLRPC$TL_documentAttributeSticker)) {
                    i++;
                } else if ("image/webp".equals(tLRPC$Document.mime_type) || "video/webm".equals(tLRPC$Document.mime_type)) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean isVideoStickerDocument(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeCustomEmoji)) {
                    return "video/webm".equals(tLRPC$Document.mime_type);
                }
            }
        }
        return false;
    }

    public static boolean isStickerHasSet(TLRPC$Document tLRPC$Document) {
        TLRPC$InputStickerSet tLRPC$InputStickerSet;
        if (tLRPC$Document != null) {
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) && (tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset) != null && !(tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetEmpty)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAnimatedStickerDocument(TLRPC$Document tLRPC$Document, boolean z) {
        if (tLRPC$Document != null && (("application/x-tgsticker".equals(tLRPC$Document.mime_type) && !tLRPC$Document.thumbs.isEmpty()) || "application/x-tgsdice".equals(tLRPC$Document.mime_type))) {
            if (z) {
                return true;
            }
            int size = tLRPC$Document.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                    return tLRPC$DocumentAttribute.stickerset instanceof TLRPC$TL_inputStickerSetShortName;
                }
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeCustomEmoji) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canAutoplayAnimatedSticker(TLRPC$Document tLRPC$Document) {
        return (isAnimatedStickerDocument(tLRPC$Document, true) || isVideoStickerDocument(tLRPC$Document)) && SharedConfig.getDevicePerformanceClass() != 0;
    }

    public static boolean isMaskDocument(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) && tLRPC$DocumentAttribute.mask) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceDocument(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                    return tLRPC$DocumentAttribute.voice;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceWebDocument(WebFile webFile) {
        return webFile != null && webFile.mime_type.equals("audio/ogg");
    }

    public static boolean isImageWebDocument(WebFile webFile) {
        return webFile != null && !isGifDocument(webFile) && webFile.mime_type.startsWith("image/");
    }

    public static boolean isVideoWebDocument(WebFile webFile) {
        return webFile != null && webFile.mime_type.startsWith("video/");
    }

    public static boolean isMusicDocument(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                    return !tLRPC$DocumentAttribute.voice;
                }
            }
            if (!TextUtils.isEmpty(tLRPC$Document.mime_type)) {
                String lowerCase = tLRPC$Document.mime_type.toLowerCase();
                if (lowerCase.equals("audio/flac") || lowerCase.equals("audio/ogg") || lowerCase.equals("audio/opus") || lowerCase.equals("audio/x-opus+ogg") || (lowerCase.equals("application/octet-stream") && FileLoader.getDocumentFileName(tLRPC$Document).endsWith(".opus"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static TLRPC$VideoSize getDocumentVideoThumb(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null || tLRPC$Document.video_thumbs.isEmpty()) {
            return null;
        }
        return tLRPC$Document.video_thumbs.get(0);
    }

    public static boolean isVideoDocument(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return false;
        }
        boolean z = false;
        int i = 0;
        int i2 = 0;
        boolean z2 = false;
        for (int i3 = 0; i3 < tLRPC$Document.attributes.size(); i3++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i3);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                if (tLRPC$DocumentAttribute.round_message) {
                    return false;
                }
                i = tLRPC$DocumentAttribute.w;
                i2 = tLRPC$DocumentAttribute.h;
                z2 = true;
            } else if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAnimated) {
                z = true;
            }
        }
        if (z && (i > 1280 || i2 > 1280)) {
            z = false;
        }
        if (SharedConfig.streamMkv && !z2 && "video/x-matroska".equals(tLRPC$Document.mime_type)) {
            z2 = true;
        }
        if (!z2 || z) {
            return false;
        }
        return true;
    }

    public TLRPC$Document getDocument() {
        TLRPC$Document tLRPC$Document = this.emojiAnimatedSticker;
        if (tLRPC$Document != null) {
            return tLRPC$Document;
        }
        return getDocument(this.messageOwner);
    }

    public static TLRPC$Document getDocument(TLRPC$Message tLRPC$Message) {
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            return getMedia(tLRPC$Message).webpage.document;
        }
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaGame) {
            return getMedia(tLRPC$Message).game.document;
        }
        if (getMedia(tLRPC$Message) != null) {
            return getMedia(tLRPC$Message).document;
        }
        return null;
    }

    public static TLRPC$Photo getPhoto(TLRPC$Message tLRPC$Message) {
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            return getMedia(tLRPC$Message).webpage.photo;
        }
        if (getMedia(tLRPC$Message) != null) {
            return getMedia(tLRPC$Message).photo;
        }
        return null;
    }

    public static boolean isStickerMessage(TLRPC$Message tLRPC$Message) {
        return getMedia(tLRPC$Message) != null && isStickerDocument(getMedia(tLRPC$Message).document);
    }

    public static boolean isAnimatedStickerMessage(TLRPC$Message tLRPC$Message) {
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(tLRPC$Message.dialog_id);
        if ((isEncryptedDialog && tLRPC$Message.stickerVerified != 1) || getMedia(tLRPC$Message) == null) {
            return false;
        }
        if (isAnimatedStickerDocument(getMedia(tLRPC$Message).document, !isEncryptedDialog || tLRPC$Message.out)) {
            return true;
        }
        return false;
    }

    public static boolean isLocationMessage(TLRPC$Message tLRPC$Message) {
        return (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaGeo) || (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaGeoLive) || (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaVenue);
    }

    public static boolean isMaskMessage(TLRPC$Message tLRPC$Message) {
        return getMedia(tLRPC$Message) != null && isMaskDocument(getMedia(tLRPC$Message).document);
    }

    public static boolean isMusicMessage(TLRPC$Message tLRPC$Message) {
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            return isMusicDocument(getMedia(tLRPC$Message).webpage.document);
        }
        return getMedia(tLRPC$Message) != null && isMusicDocument(getMedia(tLRPC$Message).document);
    }

    public static boolean isGifMessage(TLRPC$Message tLRPC$Message) {
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            return isGifDocument(getMedia(tLRPC$Message).webpage.document);
        }
        if (getMedia(tLRPC$Message) != null) {
            if (isGifDocument(getMedia(tLRPC$Message).document, tLRPC$Message.grouped_id != 0)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRoundVideoMessage(TLRPC$Message tLRPC$Message) {
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            return isRoundVideoDocument(getMedia(tLRPC$Message).webpage.document);
        }
        return getMedia(tLRPC$Message) != null && isRoundVideoDocument(getMedia(tLRPC$Message).document);
    }

    public static boolean isPhoto(TLRPC$Message tLRPC$Message) {
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            return (getMedia(tLRPC$Message).webpage.photo instanceof TLRPC$TL_photo) && !(getMedia(tLRPC$Message).webpage.document instanceof TLRPC$TL_document);
        }
        return getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto;
    }

    public static boolean isVoiceMessage(TLRPC$Message tLRPC$Message) {
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            return isVoiceDocument(getMedia(tLRPC$Message).webpage.document);
        }
        return getMedia(tLRPC$Message) != null && isVoiceDocument(getMedia(tLRPC$Message).document);
    }

    public static boolean isNewGifMessage(TLRPC$Message tLRPC$Message) {
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            return isNewGifDocument(getMedia(tLRPC$Message).webpage.document);
        }
        return getMedia(tLRPC$Message) != null && isNewGifDocument(getMedia(tLRPC$Message).document);
    }

    public static boolean isLiveLocationMessage(TLRPC$Message tLRPC$Message) {
        return getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaGeoLive;
    }

    public static boolean isVideoMessage(TLRPC$Message tLRPC$Message) {
        if (getMedia(tLRPC$Message) != null && isVideoSticker(getMedia(tLRPC$Message).document)) {
            return false;
        }
        if (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) {
            return isVideoDocument(getMedia(tLRPC$Message).webpage.document);
        }
        if (getMedia(tLRPC$Message) == null || !isVideoDocument(getMedia(tLRPC$Message).document)) {
            return false;
        }
        return true;
    }

    public static boolean isGameMessage(TLRPC$Message tLRPC$Message) {
        return getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaGame;
    }

    public static boolean isInvoiceMessage(TLRPC$Message tLRPC$Message) {
        return getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaInvoice;
    }

    public static TLRPC$InputStickerSet getInputStickerSet(TLRPC$Message tLRPC$Message) {
        TLRPC$Document document = getDocument(tLRPC$Message);
        if (document != null) {
            return getInputStickerSet(document);
        }
        return null;
    }

    public static TLRPC$InputStickerSet getInputStickerSet(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return null;
        }
        int size = tLRPC$Document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeCustomEmoji)) {
                TLRPC$InputStickerSet tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset;
                if (tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetEmpty) {
                    return null;
                }
                return tLRPC$InputStickerSet;
            }
        }
        return null;
    }

    public static String findAnimatedEmojiEmoticon(TLRPC$Document tLRPC$Document) {
        return findAnimatedEmojiEmoticon(tLRPC$Document, "😀");
    }

    public static String findAnimatedEmojiEmoticon(TLRPC$Document tLRPC$Document, String str) {
        if (tLRPC$Document == null) {
            return str;
        }
        int size = tLRPC$Document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeCustomEmoji) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker)) {
                return tLRPC$DocumentAttribute.alt;
            }
        }
        return str;
    }

    public static boolean isAnimatedEmoji(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return false;
        }
        int size = tLRPC$Document.attributes.size();
        for (int i = 0; i < size; i++) {
            if (tLRPC$Document.attributes.get(i) instanceof TLRPC$TL_documentAttributeCustomEmoji) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFreeEmoji(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return false;
        }
        int size = tLRPC$Document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeCustomEmoji) {
                return ((TLRPC$TL_documentAttributeCustomEmoji) tLRPC$DocumentAttribute).free;
            }
        }
        return false;
    }

    public static boolean isPremiumEmojiPack(TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        TLRPC$StickerSet tLRPC$StickerSet;
        if (!((tLRPC$TL_messages_stickerSet != null && (tLRPC$StickerSet = tLRPC$TL_messages_stickerSet.set) != null && !tLRPC$StickerSet.emojis) || tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents == null)) {
            for (int i = 0; i < tLRPC$TL_messages_stickerSet.documents.size(); i++) {
                if (!isFreeEmoji(tLRPC$TL_messages_stickerSet.documents.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPremiumEmojiPack(TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
        TLRPC$StickerSet tLRPC$StickerSet;
        if (tLRPC$StickerSetCovered != null && (tLRPC$StickerSet = tLRPC$StickerSetCovered.set) != null && !tLRPC$StickerSet.emojis) {
            return false;
        }
        ArrayList<TLRPC$Document> arrayList = tLRPC$StickerSetCovered instanceof TLRPC$TL_stickerSetFullCovered ? ((TLRPC$TL_stickerSetFullCovered) tLRPC$StickerSetCovered).documents : tLRPC$StickerSetCovered.covers;
        if (!(tLRPC$StickerSetCovered == null || arrayList == null)) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (!isFreeEmoji(arrayList.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static long getStickerSetId(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return -1;
        }
        for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                TLRPC$InputStickerSet tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset;
                if (tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetEmpty) {
                    return -1;
                }
                return tLRPC$InputStickerSet.id;
            }
        }
        return -1;
    }

    public static String getStickerSetName(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document == null) {
            return null;
        }
        for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                TLRPC$InputStickerSet tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset;
                if (tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetEmpty) {
                    return null;
                }
                return tLRPC$InputStickerSet.short_name;
            }
        }
        return null;
    }

    public String getStickerChar() {
        TLRPC$Document document = getDocument();
        if (document == null) {
            return null;
        }
        Iterator<TLRPC$DocumentAttribute> it = document.attributes.iterator();
        while (it.hasNext()) {
            TLRPC$DocumentAttribute next = it.next();
            if (next instanceof TLRPC$TL_documentAttributeSticker) {
                return next.alt;
            }
        }
        return null;
    }

    public int getApproximateHeight() {
        int i;
        int i2;
        int i3;
        int i4;
        int i5 = this.type;
        int i6 = 0;
        if (i5 == 0) {
            int i7 = this.textHeight;
            if ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && (getMedia(this.messageOwner).webpage instanceof TLRPC$TL_webPage)) {
                i6 = AndroidUtilities.dp(100.0f);
            }
            int i8 = i7 + i6;
            return isReply() ? i8 + AndroidUtilities.dp(42.0f) : i8;
        } else if (i5 == 20) {
            return AndroidUtilities.getPhotoSize();
        } else {
            if (i5 == 2) {
                return AndroidUtilities.dp(72.0f);
            }
            if (i5 == 12) {
                return AndroidUtilities.dp(71.0f);
            }
            if (i5 == 9) {
                return AndroidUtilities.dp(100.0f);
            }
            if (i5 == 4) {
                return AndroidUtilities.dp(114.0f);
            }
            if (i5 == 14) {
                return AndroidUtilities.dp(82.0f);
            }
            if (i5 == 10) {
                return AndroidUtilities.dp(30.0f);
            }
            if (i5 == 11 || i5 == 18) {
                return AndroidUtilities.dp(50.0f);
            }
            if (i5 == 5) {
                return AndroidUtilities.roundMessageSize;
            }
            if (i5 == 19) {
                return this.textHeight + AndroidUtilities.dp(30.0f);
            }
            if (i5 == 13 || i5 == 15) {
                float f = ((float) AndroidUtilities.displaySize.y) * 0.4f;
                if (AndroidUtilities.isTablet()) {
                    i = AndroidUtilities.getMinTabletSide();
                } else {
                    i = AndroidUtilities.displaySize.x;
                }
                float f2 = ((float) i) * 0.5f;
                TLRPC$Document document = getDocument();
                if (document != null) {
                    int size = document.attributes.size();
                    int i9 = 0;
                    while (true) {
                        if (i9 >= size) {
                            break;
                        }
                        TLRPC$DocumentAttribute tLRPC$DocumentAttribute = document.attributes.get(i9);
                        if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) {
                            i6 = tLRPC$DocumentAttribute.w;
                            i2 = tLRPC$DocumentAttribute.h;
                            break;
                        }
                        i9++;
                    }
                }
                i2 = 0;
                if (i6 == 0) {
                    i2 = (int) f;
                    i6 = AndroidUtilities.dp(100.0f) + i2;
                }
                float f3 = (float) i2;
                if (f3 > f) {
                    i6 = (int) (((float) i6) * (f / f3));
                    i2 = (int) f;
                }
                float f4 = (float) i6;
                if (f4 > f2) {
                    i2 = (int) (((float) i2) * (f2 / f4));
                }
                return i2 + AndroidUtilities.dp(14.0f);
            }
            if (AndroidUtilities.isTablet()) {
                i3 = AndroidUtilities.getMinTabletSide();
            } else {
                Point point = AndroidUtilities.displaySize;
                i3 = Math.min(point.x, point.y);
            }
            int i10 = (int) (((float) i3) * 0.7f);
            int dp = AndroidUtilities.dp(100.0f) + i10;
            if (i10 > AndroidUtilities.getPhotoSize()) {
                i10 = AndroidUtilities.getPhotoSize();
            }
            if (dp > AndroidUtilities.getPhotoSize()) {
                dp = AndroidUtilities.getPhotoSize();
            }
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
            if (closestPhotoSizeWithSize != null) {
                int i11 = (int) (((float) closestPhotoSizeWithSize.h) / (((float) closestPhotoSizeWithSize.w) / ((float) i10)));
                if (i11 == 0) {
                    i11 = AndroidUtilities.dp(100.0f);
                }
                if (i11 <= dp) {
                    dp = i11 < AndroidUtilities.dp(120.0f) ? AndroidUtilities.dp(120.0f) : i11;
                }
                if (needDrawBluredPreview()) {
                    if (AndroidUtilities.isTablet()) {
                        i4 = AndroidUtilities.getMinTabletSide();
                    } else {
                        Point point2 = AndroidUtilities.displaySize;
                        i4 = Math.min(point2.x, point2.y);
                    }
                    dp = (int) (((float) i4) * 0.5f);
                }
            }
            return dp + AndroidUtilities.dp(14.0f);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = r1.parentWidth;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int getParentWidth() {
        /*
            r1 = this;
            boolean r0 = r1.preview
            if (r0 == 0) goto L_0x0009
            int r0 = r1.parentWidth
            if (r0 <= 0) goto L_0x0009
            goto L_0x000d
        L_0x0009:
            android.graphics.Point r0 = org.telegram.messenger.AndroidUtilities.displaySize
            int r0 = r0.x
        L_0x000d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getParentWidth():int");
    }

    public String getStickerEmoji() {
        TLRPC$Document document = getDocument();
        if (document == null) {
            return null;
        }
        for (int i = 0; i < document.attributes.size(); i++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = document.attributes.get(i);
            if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) || (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeCustomEmoji)) {
                String str = tLRPC$DocumentAttribute.alt;
                if (str == null || str.length() <= 0) {
                    return null;
                }
                return tLRPC$DocumentAttribute.alt;
            }
        }
        return null;
    }

    public boolean isVideoCall() {
        TLRPC$MessageAction tLRPC$MessageAction = this.messageOwner.action;
        return (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPhoneCall) && tLRPC$MessageAction.video;
    }

    public boolean isAnimatedEmoji() {
        return (this.emojiAnimatedSticker == null && this.emojiAnimatedStickerId == null) ? false : true;
    }

    public boolean isAnimatedAnimatedEmoji() {
        return isAnimatedEmoji() && isAnimatedEmoji(getDocument());
    }

    public boolean isDice() {
        return getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDice;
    }

    public String getDiceEmoji() {
        if (!isDice()) {
            return null;
        }
        TLRPC$TL_messageMediaDice tLRPC$TL_messageMediaDice = (TLRPC$TL_messageMediaDice) getMedia(this.messageOwner);
        if (TextUtils.isEmpty(tLRPC$TL_messageMediaDice.emoticon)) {
            return "🎲";
        }
        return tLRPC$TL_messageMediaDice.emoticon.replace("️", "");
    }

    public int getDiceValue() {
        if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDice) {
            return ((TLRPC$TL_messageMediaDice) getMedia(this.messageOwner)).value;
        }
        return -1;
    }

    public boolean isSticker() {
        int i = this.type;
        if (i != 1000) {
            return i == 13;
        }
        if (isStickerDocument(getDocument()) || isVideoSticker(getDocument())) {
            return true;
        }
        return false;
    }

    public boolean isAnimatedSticker() {
        int i = this.type;
        boolean z = false;
        if (i != 1000) {
            return i == 15;
        }
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(getDialogId());
        if (isEncryptedDialog && this.messageOwner.stickerVerified != 1) {
            return false;
        }
        if (this.emojiAnimatedStickerId != null && this.emojiAnimatedSticker == null) {
            return true;
        }
        TLRPC$Document document = getDocument();
        if (this.emojiAnimatedSticker != null || !isEncryptedDialog || isOut()) {
            z = true;
        }
        return isAnimatedStickerDocument(document, z);
    }

    public boolean isAnyKindOfSticker() {
        int i = this.type;
        return i == 13 || i == 15 || i == 19;
    }

    public boolean shouldDrawWithoutBackground() {
        int i = this.type;
        return i == 13 || i == 15 || i == 5 || i == 19;
    }

    public boolean isAnimatedEmojiStickers() {
        return this.type == 19;
    }

    public boolean isAnimatedEmojiStickerSingle() {
        return this.emojiAnimatedStickerId != null;
    }

    public boolean isLocation() {
        return isLocationMessage(this.messageOwner);
    }

    public boolean isMask() {
        return isMaskMessage(this.messageOwner);
    }

    public boolean isMusic() {
        return isMusicMessage(this.messageOwner) && !isVideo();
    }

    public boolean isDocument() {
        return getDocument() != null && !isVideo() && !isMusic() && !isVoice() && !isAnyKindOfSticker();
    }

    public boolean isVoice() {
        return isVoiceMessage(this.messageOwner);
    }

    public boolean isVideo() {
        return isVideoMessage(this.messageOwner);
    }

    public boolean isPhoto() {
        return isPhoto(this.messageOwner);
    }

    public boolean isLiveLocation() {
        return isLiveLocationMessage(this.messageOwner);
    }

    public boolean isExpiredLiveLocation(int i) {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        return tLRPC$Message.date + getMedia(tLRPC$Message).period <= i;
    }

    public boolean isGame() {
        return isGameMessage(this.messageOwner);
    }

    public boolean isInvoice() {
        return isInvoiceMessage(this.messageOwner);
    }

    public boolean isRoundVideo() {
        if (this.isRoundVideoCached == 0) {
            this.isRoundVideoCached = (this.type == 5 || isRoundVideoMessage(this.messageOwner)) ? 1 : 2;
        }
        if (this.isRoundVideoCached == 1) {
            return true;
        }
        return false;
    }

    public boolean shouldAnimateSending() {
        return isSending() && (this.type == 5 || isVoice() || ((isAnyKindOfSticker() && this.sendAnimationData != null) || !(this.messageText == null || this.sendAnimationData == null)));
    }

    public boolean hasAttachedStickers() {
        if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto) {
            if (getMedia(this.messageOwner).photo == null || !getMedia(this.messageOwner).photo.has_stickers) {
                return false;
            }
            return true;
        } else if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument) {
            return isDocumentHasAttachedStickers(getMedia(this.messageOwner).document);
        } else {
            return false;
        }
    }

    public static boolean isDocumentHasAttachedStickers(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                if (tLRPC$Document.attributes.get(i) instanceof TLRPC$TL_documentAttributeHasStickers) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isGif() {
        return isGifMessage(this.messageOwner);
    }

    public boolean isWebpageDocument() {
        return (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && getMedia(this.messageOwner).webpage.document != null && !isGifDocument(getMedia(this.messageOwner).webpage.document);
    }

    public boolean isWebpage() {
        return getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage;
    }

    public boolean isNewGif() {
        return getMedia(this.messageOwner) != null && isNewGifDocument(getDocument());
    }

    public boolean isAndroidTheme() {
        if (!(getMedia(this.messageOwner) == null || getMedia(this.messageOwner).webpage == null || getMedia(this.messageOwner).webpage.attributes.isEmpty())) {
            int size = getMedia(this.messageOwner).webpage.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_webPageAttributeTheme tLRPC$TL_webPageAttributeTheme = getMedia(this.messageOwner).webpage.attributes.get(i);
                ArrayList<TLRPC$Document> arrayList = tLRPC$TL_webPageAttributeTheme.documents;
                int size2 = arrayList.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    if ("application/x-tgtheme-android".equals(arrayList.get(i2).mime_type)) {
                        return true;
                    }
                }
                if (tLRPC$TL_webPageAttributeTheme.settings != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getMusicTitle() {
        return getMusicTitle(true);
    }

    public String getMusicTitle(boolean z) {
        TLRPC$Document document = getDocument();
        if (document != null) {
            int i = 0;
            while (i < document.attributes.size()) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = document.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                    if (!tLRPC$DocumentAttribute.voice) {
                        String str = tLRPC$DocumentAttribute.title;
                        if (str != null && str.length() != 0) {
                            return str;
                        }
                        String documentFileName = FileLoader.getDocumentFileName(document);
                        return (!TextUtils.isEmpty(documentFileName) || !z) ? documentFileName : LocaleController.getString("AudioUnknownTitle", R.string.AudioUnknownTitle);
                    } else if (!z) {
                        return null;
                    } else {
                        return LocaleController.formatDateAudio((long) this.messageOwner.date, true);
                    }
                } else if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) && tLRPC$DocumentAttribute.round_message) {
                    return LocaleController.formatDateAudio((long) this.messageOwner.date, true);
                } else {
                    i++;
                }
            }
            String documentFileName2 = FileLoader.getDocumentFileName(document);
            if (!TextUtils.isEmpty(documentFileName2)) {
                return documentFileName2;
            }
        }
        return LocaleController.getString("AudioUnknownTitle", R.string.AudioUnknownTitle);
    }

    public int getDuration() {
        TLRPC$Document document = getDocument();
        if (document == null) {
            return 0;
        }
        int i = this.audioPlayerDuration;
        if (i > 0) {
            return i;
        }
        for (int i2 = 0; i2 < document.attributes.size(); i2++) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = document.attributes.get(i2);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) {
                return tLRPC$DocumentAttribute.duration;
            }
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) {
                return tLRPC$DocumentAttribute.duration;
            }
        }
        return this.audioPlayerDuration;
    }

    public String getArtworkUrl(boolean z) {
        TLRPC$Document document = getDocument();
        if (document == null || "audio/ogg".equals(document.mime_type)) {
            return null;
        }
        int size = document.attributes.size();
        int i = 0;
        while (i < size) {
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = document.attributes.get(i);
            if (!(tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio)) {
                i++;
            } else if (tLRPC$DocumentAttribute.voice) {
                return null;
            } else {
                String str = tLRPC$DocumentAttribute.performer;
                String str2 = tLRPC$DocumentAttribute.title;
                if (!TextUtils.isEmpty(str)) {
                    int i2 = 0;
                    while (true) {
                        String[] strArr = excludeWords;
                        if (i2 >= strArr.length) {
                            break;
                        }
                        str = str.replace(strArr[i2], " ");
                        i2++;
                    }
                }
                if (TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
                    return null;
                }
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append("athumb://itunes.apple.com/search?term=");
                    sb.append(URLEncoder.encode(str + " - " + str2, "UTF-8"));
                    sb.append("&entity=song&limit=4");
                    sb.append(z ? "&s=1" : "");
                    return sb.toString();
                } catch (Exception unused) {
                }
            }
        }
        return null;
    }

    public String getMusicAuthor() {
        return getMusicAuthor(true);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003c, code lost:
        if (r4.round_message != false) goto L_0x0023;
     */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0041  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0156 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getMusicAuthor(boolean r12) {
        /*
            r11 = this;
            org.telegram.tgnet.TLRPC$Document r0 = r11.getDocument()
            java.lang.String r1 = "AudioUnknownArtist"
            if (r0 == 0) goto L_0x015a
            r2 = 0
            r3 = 0
        L_0x000a:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r0.attributes
            int r4 = r4.size()
            if (r2 >= r4) goto L_0x015a
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r4 = r0.attributes
            java.lang.Object r4 = r4.get(r2)
            org.telegram.tgnet.TLRPC$DocumentAttribute r4 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r4
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r6 = 1
            if (r5 == 0) goto L_0x0036
            boolean r3 = r4.voice
            if (r3 == 0) goto L_0x0025
        L_0x0023:
            r3 = 1
            goto L_0x003f
        L_0x0025:
            java.lang.String r0 = r4.performer
            boolean r2 = android.text.TextUtils.isEmpty(r0)
            if (r2 == 0) goto L_0x0035
            if (r12 == 0) goto L_0x0035
            int r12 = org.telegram.messenger.R.string.AudioUnknownArtist
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r12)
        L_0x0035:
            return r0
        L_0x0036:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            if (r5 == 0) goto L_0x003f
            boolean r4 = r4.round_message
            if (r4 == 0) goto L_0x003f
            goto L_0x0023
        L_0x003f:
            if (r3 == 0) goto L_0x0156
            r4 = 0
            if (r12 != 0) goto L_0x0045
            return r4
        L_0x0045:
            boolean r5 = r11.isOutOwner()
            if (r5 != 0) goto L_0x014d
            org.telegram.tgnet.TLRPC$Message r5 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r5 = r5.fwd_from
            if (r5 == 0) goto L_0x0069
            org.telegram.tgnet.TLRPC$Peer r5 = r5.from_id
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r6 == 0) goto L_0x0069
            long r5 = r5.user_id
            int r7 = r11.currentAccount
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
            long r7 = r7.getClientUserId()
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0069
            goto L_0x014d
        L_0x0069:
            org.telegram.tgnet.TLRPC$Message r5 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r5.fwd_from
            if (r6 == 0) goto L_0x008d
            org.telegram.tgnet.TLRPC$Peer r7 = r6.from_id
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r7 == 0) goto L_0x008d
            int r5 = r11.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r6 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            org.telegram.tgnet.TLRPC$Peer r6 = r6.from_id
            long r6 = r6.channel_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
            goto L_0x0141
        L_0x008d:
            if (r6 == 0) goto L_0x00ad
            org.telegram.tgnet.TLRPC$Peer r7 = r6.from_id
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r7 == 0) goto L_0x00ad
            int r5 = r11.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r6 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            org.telegram.tgnet.TLRPC$Peer r6 = r6.from_id
            long r6 = r6.chat_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
            goto L_0x0141
        L_0x00ad:
            if (r6 == 0) goto L_0x00d0
            org.telegram.tgnet.TLRPC$Peer r7 = r6.from_id
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r7 == 0) goto L_0x00d0
            int r5 = r11.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r6 = r11.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            org.telegram.tgnet.TLRPC$Peer r6 = r6.from_id
            long r6 = r6.user_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
        L_0x00cb:
            r10 = r5
            r5 = r4
            r4 = r10
            goto L_0x0141
        L_0x00d0:
            if (r6 == 0) goto L_0x00d7
            java.lang.String r6 = r6.from_name
            if (r6 == 0) goto L_0x00d7
            return r6
        L_0x00d7:
            org.telegram.tgnet.TLRPC$Peer r6 = r5.from_id
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r7 == 0) goto L_0x00f2
            int r5 = r11.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r6 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.from_id
            long r6 = r6.chat_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
            goto L_0x0141
        L_0x00f2:
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r7 == 0) goto L_0x010b
            int r5 = r11.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r6 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.from_id
            long r6 = r6.channel_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
            goto L_0x0141
        L_0x010b:
            if (r6 != 0) goto L_0x012c
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            long r5 = r5.channel_id
            r7 = 0
            int r9 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r9 == 0) goto L_0x012c
            int r5 = r11.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r6 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            long r6 = r6.channel_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r5 = r5.getChat(r6)
            goto L_0x0141
        L_0x012c:
            int r5 = r11.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            org.telegram.tgnet.TLRPC$Message r6 = r11.messageOwner
            org.telegram.tgnet.TLRPC$Peer r6 = r6.from_id
            long r6 = r6.user_id
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            goto L_0x00cb
        L_0x0141:
            if (r4 == 0) goto L_0x0148
            java.lang.String r12 = org.telegram.messenger.UserObject.getUserName(r4)
            return r12
        L_0x0148:
            if (r5 == 0) goto L_0x0156
            java.lang.String r12 = r5.title
            return r12
        L_0x014d:
            int r12 = org.telegram.messenger.R.string.FromYou
            java.lang.String r0 = "FromYou"
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r0, r12)
            return r12
        L_0x0156:
            int r2 = r2 + 1
            goto L_0x000a
        L_0x015a:
            int r12 = org.telegram.messenger.R.string.AudioUnknownArtist
            java.lang.String r12 = org.telegram.messenger.LocaleController.getString(r1, r12)
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getMusicAuthor(boolean):java.lang.String");
    }

    public TLRPC$InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0020, code lost:
        if (r1.channel_id == r0.channel_id) goto L_0x0036;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean needDrawForwarded() {
        /*
            r5 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            int r1 = r0.flags
            r1 = r1 & 4
            if (r1 == 0) goto L_0x0036
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x0036
            boolean r1 = r0.imported
            if (r1 != 0) goto L_0x0036
            org.telegram.tgnet.TLRPC$Peer r1 = r0.saved_from_peer
            if (r1 == 0) goto L_0x0022
            org.telegram.tgnet.TLRPC$Peer r0 = r0.from_id
            boolean r2 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r2 == 0) goto L_0x0022
            long r1 = r1.channel_id
            long r3 = r0.channel_id
            int r0 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x0036
        L_0x0022:
            int r0 = r5.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r0 = r0.getClientUserId()
            long r2 = r5.getDialogId()
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x0036
            r0 = 1
            goto L_0x0037
        L_0x0036:
            r0 = 0
        L_0x0037:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.needDrawForwarded():boolean");
    }

    public static boolean isForwardedMessage(TLRPC$Message tLRPC$Message) {
        return ((tLRPC$Message.flags & 4) == 0 || tLRPC$Message.fwd_from == null) ? false : true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000a, code lost:
        r0 = r6.messageOwner;
        r1 = r0.reply_to;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isReply() {
        /*
            r6 = this;
            org.telegram.messenger.MessageObject r0 = r6.replyMessageObject
            if (r0 == 0) goto L_0x000a
            org.telegram.tgnet.TLRPC$Message r0 = r0.messageOwner
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r0 != 0) goto L_0x0024
        L_0x000a:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$TL_messageReplyHeader r1 = r0.reply_to
            if (r1 == 0) goto L_0x0024
            int r2 = r1.reply_to_msg_id
            if (r2 != 0) goto L_0x001c
            long r1 = r1.reply_to_random_id
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 == 0) goto L_0x0024
        L_0x001c:
            int r0 = r0.flags
            r0 = r0 & 8
            if (r0 == 0) goto L_0x0024
            r0 = 1
            goto L_0x0025
        L_0x0024:
            r0 = 0
        L_0x0025:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isReply():boolean");
    }

    public boolean isMediaEmpty() {
        return isMediaEmpty(this.messageOwner);
    }

    public boolean isMediaEmptyWebpage() {
        return isMediaEmptyWebpage(this.messageOwner);
    }

    public static boolean isMediaEmpty(TLRPC$Message tLRPC$Message) {
        return tLRPC$Message == null || getMedia(tLRPC$Message) == null || (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaEmpty) || (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage);
    }

    public static boolean isMediaEmptyWebpage(TLRPC$Message tLRPC$Message) {
        return tLRPC$Message == null || getMedia(tLRPC$Message) == null || (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaEmpty);
    }

    public boolean hasReplies() {
        TLRPC$MessageReplies tLRPC$MessageReplies = this.messageOwner.replies;
        return tLRPC$MessageReplies != null && tLRPC$MessageReplies.replies > 0;
    }

    public boolean canViewThread() {
        MessageObject messageObject;
        if (this.messageOwner.action != null) {
            return false;
        }
        if (hasReplies() || (((messageObject = this.replyMessageObject) != null && messageObject.messageOwner.replies != null) || getReplyTopMsgId() != 0)) {
            return true;
        }
        return false;
    }

    public boolean isComments() {
        TLRPC$MessageReplies tLRPC$MessageReplies = this.messageOwner.replies;
        return tLRPC$MessageReplies != null && tLRPC$MessageReplies.comments;
    }

    public boolean isLinkedToChat(long j) {
        TLRPC$MessageReplies tLRPC$MessageReplies = this.messageOwner.replies;
        return tLRPC$MessageReplies != null && (j == 0 || tLRPC$MessageReplies.channel_id == j);
    }

    public int getRepliesCount() {
        TLRPC$MessageReplies tLRPC$MessageReplies = this.messageOwner.replies;
        if (tLRPC$MessageReplies != null) {
            return tLRPC$MessageReplies.replies;
        }
        return 0;
    }

    public boolean canEditMessage(TLRPC$Chat tLRPC$Chat) {
        return canEditMessage(this.currentAccount, this.messageOwner, tLRPC$Chat, this.scheduled);
    }

    public boolean canEditMessageScheduleTime(TLRPC$Chat tLRPC$Chat) {
        return canEditMessageScheduleTime(this.currentAccount, this.messageOwner, tLRPC$Chat);
    }

    public boolean canForwardMessage() {
        return !(this.messageOwner instanceof TLRPC$TL_message_secret) && !needDrawBluredPreview() && !isLiveLocation() && this.type != 16 && !isSponsored() && !this.messageOwner.noforwards;
    }

    public boolean canEditMedia() {
        if (isSecretMedia()) {
            return false;
        }
        if (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto) {
            return true;
        }
        if (!(getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaDocument) || isVoice() || isSticker() || isAnimatedSticker() || isRoundVideo()) {
            return false;
        }
        return true;
    }

    public boolean canEditMessageAnytime(TLRPC$Chat tLRPC$Chat) {
        return canEditMessageAnytime(this.currentAccount, this.messageOwner, tLRPC$Chat);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00b5, code lost:
        r9 = r11.admin_rights;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00bd, code lost:
        r9 = r11.default_banned_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean canEditMessageAnytime(int r9, org.telegram.tgnet.TLRPC$Message r10, org.telegram.tgnet.TLRPC$Chat r11) {
        /*
            r0 = 0
            if (r10 == 0) goto L_0x00c6
            org.telegram.tgnet.TLRPC$Peer r1 = r10.peer_id
            if (r1 == 0) goto L_0x00c6
            org.telegram.tgnet.TLRPC$MessageMedia r1 = getMedia(r10)
            r2 = 1
            if (r1 == 0) goto L_0x0032
            org.telegram.tgnet.TLRPC$MessageMedia r1 = getMedia(r10)
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = isRoundVideoDocument(r1)
            if (r1 != 0) goto L_0x00c6
            org.telegram.tgnet.TLRPC$MessageMedia r1 = getMedia(r10)
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = isStickerDocument(r1)
            if (r1 != 0) goto L_0x00c6
            org.telegram.tgnet.TLRPC$MessageMedia r1 = getMedia(r10)
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = isAnimatedStickerDocument(r1, r2)
            if (r1 != 0) goto L_0x00c6
        L_0x0032:
            org.telegram.tgnet.TLRPC$MessageAction r1 = r10.action
            if (r1 == 0) goto L_0x003a
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r1 == 0) goto L_0x00c6
        L_0x003a:
            boolean r1 = isForwardedMessage(r10)
            if (r1 != 0) goto L_0x00c6
            long r3 = r10.via_bot_id
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 != 0) goto L_0x00c6
            int r1 = r10.id
            if (r1 >= 0) goto L_0x004e
            goto L_0x00c6
        L_0x004e:
            org.telegram.tgnet.TLRPC$Peer r1 = r10.from_id
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r3 == 0) goto L_0x0071
            long r3 = r1.user_id
            org.telegram.tgnet.TLRPC$Peer r1 = r10.peer_id
            long r7 = r1.user_id
            int r1 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r1 != 0) goto L_0x0071
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r9)
            long r7 = r9.getClientUserId()
            int r9 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0071
            boolean r9 = isLiveLocationMessage(r10)
            if (r9 != 0) goto L_0x0071
            return r2
        L_0x0071:
            if (r11 != 0) goto L_0x0090
            org.telegram.tgnet.TLRPC$Peer r9 = r10.peer_id
            long r3 = r9.channel_id
            int r9 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r9 == 0) goto L_0x0090
            int r9 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.tgnet.TLRPC$Peer r11 = r10.peer_id
            long r3 = r11.channel_id
            java.lang.Long r11 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r11 = r9.getChat(r11)
            if (r11 != 0) goto L_0x0090
            return r0
        L_0x0090:
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r9 == 0) goto L_0x00a7
            boolean r9 = r11.megagroup
            if (r9 != 0) goto L_0x00a7
            boolean r9 = r11.creator
            if (r9 != 0) goto L_0x00a6
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r9 = r11.admin_rights
            if (r9 == 0) goto L_0x00a7
            boolean r9 = r9.edit_messages
            if (r9 == 0) goto L_0x00a7
        L_0x00a6:
            return r2
        L_0x00a7:
            boolean r9 = r10.out
            if (r9 == 0) goto L_0x00c6
            if (r11 == 0) goto L_0x00c6
            boolean r9 = r11.megagroup
            if (r9 == 0) goto L_0x00c6
            boolean r9 = r11.creator
            if (r9 != 0) goto L_0x00c5
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r9 = r11.admin_rights
            if (r9 == 0) goto L_0x00bd
            boolean r9 = r9.pin_messages
            if (r9 != 0) goto L_0x00c5
        L_0x00bd:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r9 = r11.default_banned_rights
            if (r9 == 0) goto L_0x00c6
            boolean r9 = r9.pin_messages
            if (r9 != 0) goto L_0x00c6
        L_0x00c5:
            return r2
        L_0x00c6:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.canEditMessageAnytime(int, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public static boolean canEditMessageScheduleTime(int i, TLRPC$Message tLRPC$Message, TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat == null && tLRPC$Message.peer_id.channel_id != 0 && (tLRPC$Chat = MessagesController.getInstance(i).getChat(Long.valueOf(tLRPC$Message.peer_id.channel_id))) == null) {
            return false;
        }
        if (!ChatObject.isChannel(tLRPC$Chat) || tLRPC$Chat.megagroup || tLRPC$Chat.creator) {
            return true;
        }
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights;
        if (tLRPC$TL_chatAdminRights == null || (!tLRPC$TL_chatAdminRights.edit_messages && !tLRPC$Message.out)) {
            return false;
        }
        return true;
    }

    public static boolean canEditMessage(int i, TLRPC$Message tLRPC$Message, TLRPC$Chat tLRPC$Chat, boolean z) {
        TLRPC$MessageAction tLRPC$MessageAction;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights3;
        if (z && tLRPC$Message.date < ConnectionsManager.getInstance(i).getCurrentTime() - 60) {
            return false;
        }
        if ((tLRPC$Chat == null || ((!tLRPC$Chat.left && !tLRPC$Chat.kicked) || (tLRPC$Chat.megagroup && tLRPC$Chat.has_link))) && tLRPC$Message != null && tLRPC$Message.peer_id != null && ((getMedia(tLRPC$Message) == null || (!isRoundVideoDocument(getMedia(tLRPC$Message).document) && !isStickerDocument(getMedia(tLRPC$Message).document) && !isAnimatedStickerDocument(getMedia(tLRPC$Message).document, true) && !isLocationMessage(tLRPC$Message))) && (((tLRPC$MessageAction = tLRPC$Message.action) == null || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionEmpty)) && !isForwardedMessage(tLRPC$Message) && tLRPC$Message.via_bot_id == 0 && tLRPC$Message.id >= 0))) {
            TLRPC$Peer tLRPC$Peer = tLRPC$Message.from_id;
            if (tLRPC$Peer instanceof TLRPC$TL_peerUser) {
                long j = tLRPC$Peer.user_id;
                if (j == tLRPC$Message.peer_id.user_id && j == UserConfig.getInstance(i).getClientUserId() && !isLiveLocationMessage(tLRPC$Message) && !(getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaContact)) {
                    return true;
                }
            }
            if (tLRPC$Chat == null && tLRPC$Message.peer_id.channel_id != 0 && (tLRPC$Chat = MessagesController.getInstance(i).getChat(Long.valueOf(tLRPC$Message.peer_id.channel_id))) == null) {
                return false;
            }
            if (getMedia(tLRPC$Message) != null && !(getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaEmpty) && !(getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) && !(getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument) && !(getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage)) {
                return false;
            }
            if (ChatObject.isChannel(tLRPC$Chat) && !tLRPC$Chat.megagroup && (tLRPC$Chat.creator || ((tLRPC$TL_chatAdminRights3 = tLRPC$Chat.admin_rights) != null && tLRPC$TL_chatAdminRights3.edit_messages))) {
                return true;
            }
            if (tLRPC$Message.out && tLRPC$Chat != null && tLRPC$Chat.megagroup && (tLRPC$Chat.creator || (((tLRPC$TL_chatAdminRights2 = tLRPC$Chat.admin_rights) != null && tLRPC$TL_chatAdminRights2.pin_messages) || ((tLRPC$TL_chatBannedRights = tLRPC$Chat.default_banned_rights) != null && !tLRPC$TL_chatBannedRights.pin_messages)))) {
                return true;
            }
            if (!z && Math.abs(tLRPC$Message.date - ConnectionsManager.getInstance(i).getCurrentTime()) > MessagesController.getInstance(i).maxEditTime) {
                return false;
            }
            if (tLRPC$Message.peer_id.channel_id == 0) {
                if (!tLRPC$Message.out) {
                    TLRPC$Peer tLRPC$Peer2 = tLRPC$Message.from_id;
                    if (!(tLRPC$Peer2 instanceof TLRPC$TL_peerUser) || tLRPC$Peer2.user_id != UserConfig.getInstance(i).getClientUserId()) {
                        return false;
                    }
                }
                if ((getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) || (((getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument) && !isStickerMessage(tLRPC$Message) && !isAnimatedStickerMessage(tLRPC$Message)) || (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaEmpty) || (getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) || getMedia(tLRPC$Message) == null)) {
                    return true;
                }
                return false;
            } else if (((tLRPC$Chat == null || !tLRPC$Chat.megagroup || !tLRPC$Message.out) && (tLRPC$Chat == null || tLRPC$Chat.megagroup || ((!tLRPC$Chat.creator && ((tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) == null || (!tLRPC$TL_chatAdminRights.edit_messages && (!tLRPC$Message.out || !tLRPC$TL_chatAdminRights.post_messages)))) || !tLRPC$Message.post))) || (!(getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaPhoto) && ((!(getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaDocument) || isStickerMessage(tLRPC$Message) || isAnimatedStickerMessage(tLRPC$Message)) && !(getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaEmpty) && !(getMedia(tLRPC$Message) instanceof TLRPC$TL_messageMediaWebPage) && getMedia(tLRPC$Message) != null))) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public boolean canDeleteMessage(boolean z, TLRPC$Chat tLRPC$Chat) {
        return this.eventId == 0 && this.sponsoredId == null && canDeleteMessage(this.currentAccount, z, this.messageOwner, tLRPC$Chat);
    }

    public static boolean canDeleteMessage(int i, boolean z, TLRPC$Message tLRPC$Message, TLRPC$Chat tLRPC$Chat) {
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        if (tLRPC$Message == null) {
            return false;
        }
        if (ChatObject.isChannelAndNotMegaGroup(tLRPC$Chat) && (tLRPC$Message.action instanceof TLRPC$TL_messageActionChatJoinedByRequest)) {
            return false;
        }
        if (tLRPC$Message.id < 0) {
            return true;
        }
        if (tLRPC$Chat == null && tLRPC$Message.peer_id.channel_id != 0) {
            tLRPC$Chat = MessagesController.getInstance(i).getChat(Long.valueOf(tLRPC$Message.peer_id.channel_id));
        }
        if (ChatObject.isChannel(tLRPC$Chat)) {
            if (!z || tLRPC$Chat.megagroup) {
                boolean z2 = tLRPC$Message.out;
                if (!z2 || !(tLRPC$Message instanceof TLRPC$TL_messageService)) {
                    if (!z) {
                        if (tLRPC$Message.id == 1) {
                            return false;
                        }
                        if (tLRPC$Chat.creator || (((tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) != null && (tLRPC$TL_chatAdminRights.delete_messages || (z2 && (tLRPC$Chat.megagroup || tLRPC$TL_chatAdminRights.post_messages)))) || (tLRPC$Chat.megagroup && z2))) {
                            return true;
                        }
                        return false;
                    }
                    return true;
                } else if (tLRPC$Message.id == 1 || !ChatObject.canUserDoAdminAction(tLRPC$Chat, 13)) {
                    return false;
                } else {
                    return true;
                }
            } else {
                if (!tLRPC$Chat.creator) {
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2 = tLRPC$Chat.admin_rights;
                    if (tLRPC$TL_chatAdminRights2 == null) {
                        return false;
                    }
                    if (tLRPC$TL_chatAdminRights2.delete_messages || tLRPC$Message.out) {
                        return true;
                    }
                    return false;
                }
                return true;
            }
        } else if (z || isOut(tLRPC$Message) || !ChatObject.isChannel(tLRPC$Chat)) {
            return true;
        } else {
            return false;
        }
    }

    public String getForwardedName() {
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
        if (tLRPC$MessageFwdHeader == null) {
            return null;
        }
        TLRPC$Peer tLRPC$Peer = tLRPC$MessageFwdHeader.from_id;
        if (tLRPC$Peer instanceof TLRPC$TL_peerChannel) {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.from_id.channel_id));
            if (chat != null) {
                return chat.title;
            }
            return null;
        } else if (tLRPC$Peer instanceof TLRPC$TL_peerChat) {
            TLRPC$Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.from_id.chat_id));
            if (chat2 != null) {
                return chat2.title;
            }
            return null;
        } else if (tLRPC$Peer instanceof TLRPC$TL_peerUser) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.fwd_from.from_id.user_id));
            if (user != null) {
                return UserObject.getUserName(user);
            }
            return null;
        } else {
            String str = tLRPC$MessageFwdHeader.from_name;
            if (str != null) {
                return str;
            }
            return null;
        }
    }

    public int getReplyMsgId() {
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = this.messageOwner.reply_to;
        if (tLRPC$TL_messageReplyHeader != null) {
            return tLRPC$TL_messageReplyHeader.reply_to_msg_id;
        }
        return 0;
    }

    public int getReplyTopMsgId() {
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = this.messageOwner.reply_to;
        if (tLRPC$TL_messageReplyHeader != null) {
            return tLRPC$TL_messageReplyHeader.reply_to_top_id;
        }
        return 0;
    }

    public static long getReplyToDialogId(TLRPC$Message tLRPC$Message) {
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = tLRPC$Message.reply_to;
        if (tLRPC$TL_messageReplyHeader == null) {
            return 0;
        }
        TLRPC$Peer tLRPC$Peer = tLRPC$TL_messageReplyHeader.reply_to_peer_id;
        if (tLRPC$Peer != null) {
            return getPeerId(tLRPC$Peer);
        }
        return getDialogId(tLRPC$Message);
    }

    public int getReplyAnyMsgId() {
        TLRPC$TL_messageReplyHeader tLRPC$TL_messageReplyHeader = this.messageOwner.reply_to;
        if (tLRPC$TL_messageReplyHeader == null) {
            return 0;
        }
        int i = tLRPC$TL_messageReplyHeader.reply_to_top_id;
        if (i != 0) {
            return i;
        }
        return tLRPC$TL_messageReplyHeader.reply_to_msg_id;
    }

    public boolean isPrivateForward() {
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
        return tLRPC$MessageFwdHeader != null && !TextUtils.isEmpty(tLRPC$MessageFwdHeader.from_name);
    }

    public boolean isImportedForward() {
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
        return tLRPC$MessageFwdHeader != null && tLRPC$MessageFwdHeader.imported;
    }

    public long getSenderId() {
        TLRPC$Peer tLRPC$Peer;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = tLRPC$Message.fwd_from;
        if (tLRPC$MessageFwdHeader == null || (tLRPC$Peer = tLRPC$MessageFwdHeader.saved_from_peer) == null) {
            TLRPC$Peer tLRPC$Peer2 = tLRPC$Message.from_id;
            if (tLRPC$Peer2 instanceof TLRPC$TL_peerUser) {
                return tLRPC$Peer2.user_id;
            }
            if (tLRPC$Peer2 instanceof TLRPC$TL_peerChannel) {
                return -tLRPC$Peer2.channel_id;
            }
            if (tLRPC$Peer2 instanceof TLRPC$TL_peerChat) {
                return -tLRPC$Peer2.chat_id;
            }
            if (tLRPC$Message.post) {
                return tLRPC$Message.peer_id.channel_id;
            }
        } else {
            long j = tLRPC$Peer.user_id;
            if (j != 0) {
                TLRPC$Peer tLRPC$Peer3 = tLRPC$MessageFwdHeader.from_id;
                return tLRPC$Peer3 instanceof TLRPC$TL_peerUser ? tLRPC$Peer3.user_id : j;
            } else if (tLRPC$Peer.channel_id != 0) {
                if (isSavedFromMegagroup()) {
                    TLRPC$Peer tLRPC$Peer4 = this.messageOwner.fwd_from.from_id;
                    if (tLRPC$Peer4 instanceof TLRPC$TL_peerUser) {
                        return tLRPC$Peer4.user_id;
                    }
                }
                TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader2 = this.messageOwner.fwd_from;
                TLRPC$Peer tLRPC$Peer5 = tLRPC$MessageFwdHeader2.from_id;
                if (tLRPC$Peer5 instanceof TLRPC$TL_peerChannel) {
                    return -tLRPC$Peer5.channel_id;
                }
                if (tLRPC$Peer5 instanceof TLRPC$TL_peerChat) {
                    return -tLRPC$Peer5.chat_id;
                }
                return -tLRPC$MessageFwdHeader2.saved_from_peer.channel_id;
            } else {
                long j2 = tLRPC$Peer.chat_id;
                if (j2 != 0) {
                    TLRPC$Peer tLRPC$Peer6 = tLRPC$MessageFwdHeader.from_id;
                    if (tLRPC$Peer6 instanceof TLRPC$TL_peerUser) {
                        return tLRPC$Peer6.user_id;
                    }
                    if (tLRPC$Peer6 instanceof TLRPC$TL_peerChannel) {
                        return -tLRPC$Peer6.channel_id;
                    }
                    return tLRPC$Peer6 instanceof TLRPC$TL_peerChat ? -tLRPC$Peer6.chat_id : -j2;
                }
            }
        }
        return 0;
    }

    public boolean isWallpaper() {
        return (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && getMedia(this.messageOwner).webpage != null && "telegram_background".equals(getMedia(this.messageOwner).webpage.type);
    }

    public boolean isTheme() {
        return (getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaWebPage) && getMedia(this.messageOwner).webpage != null && "telegram_theme".equals(getMedia(this.messageOwner).webpage.type);
    }

    public int getMediaExistanceFlags() {
        boolean z = this.attachPathExists;
        return this.mediaExists ? z | true ? 1 : 0 : z ? 1 : 0;
    }

    public void applyMediaExistanceFlags(int i) {
        if (i == -1) {
            checkMediaExistance();
            return;
        }
        boolean z = false;
        this.attachPathExists = (i & 1) != 0;
        if ((i & 2) != 0) {
            z = true;
        }
        this.mediaExists = z;
    }

    public void checkMediaExistance() {
        checkMediaExistance(true);
    }

    public void checkMediaExistance(boolean z) {
        TLRPC$Photo tLRPC$Photo;
        int i;
        this.attachPathExists = false;
        this.mediaExists = false;
        int i2 = this.type;
        if (i2 == 20) {
            TLRPC$TL_messageExtendedMediaPreview tLRPC$TL_messageExtendedMediaPreview = (TLRPC$TL_messageExtendedMediaPreview) this.messageOwner.media.extended_media;
            if (tLRPC$TL_messageExtendedMediaPreview.thumb != null) {
                File pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$TL_messageExtendedMediaPreview.thumb);
                if (!this.mediaExists) {
                    this.mediaExists = pathToAttach.exists() || (tLRPC$TL_messageExtendedMediaPreview.thumb instanceof TLRPC$TL_photoStrippedSize);
                }
            }
        } else if (i2 == 1 && FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
            File pathToMessage = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.messageOwner, z);
            if (needDrawBluredPreview()) {
                this.mediaExists = new File(pathToMessage.getAbsolutePath() + ".enc").exists();
            }
            if (!this.mediaExists) {
                this.mediaExists = pathToMessage.exists();
            }
        }
        if ((!this.mediaExists && this.type == 8) || (i = this.type) == 3 || i == 9 || i == 2 || i == 14 || i == 5) {
            String str = this.messageOwner.attachPath;
            if (str != null && str.length() > 0) {
                this.attachPathExists = new File(this.messageOwner.attachPath).exists();
            }
            if (!this.attachPathExists) {
                File pathToMessage2 = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.messageOwner, z);
                if (this.type == 3 && needDrawBluredPreview()) {
                    this.mediaExists = new File(pathToMessage2.getAbsolutePath() + ".enc").exists();
                }
                if (!this.mediaExists) {
                    this.mediaExists = pathToMessage2.exists();
                }
            }
        }
        if (!this.mediaExists) {
            TLRPC$Document document = getDocument();
            if (document == null) {
                int i3 = this.type;
                if (i3 == 0) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        this.mediaExists = FileLoader.getInstance(this.currentAccount).getPathToAttach(closestPhotoSizeWithSize, (String) null, true, z).exists();
                    }
                } else if (i3 == 11 && (tLRPC$Photo = this.messageOwner.action.photo) != null && !tLRPC$Photo.video_sizes.isEmpty()) {
                    this.mediaExists = FileLoader.getInstance(this.currentAccount).getPathToAttach(tLRPC$Photo.video_sizes.get(0), (String) null, true, z).exists();
                }
            } else if (isWallpaper()) {
                this.mediaExists = FileLoader.getInstance(this.currentAccount).getPathToAttach(document, (String) null, true, z).exists();
            } else {
                this.mediaExists = FileLoader.getInstance(this.currentAccount).getPathToAttach(document, (String) null, false, z).exists();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:53:0x010d, code lost:
        r6 = (java.lang.String) r3.get(r5);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setQuery(java.lang.String r14) {
        /*
            r13 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r14)
            if (r0 == 0) goto L_0x000c
            r14 = 0
            r13.highlightedWords = r14
            r13.messageTrimmedToHighlight = r14
            return
        L_0x000c:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            java.lang.String r14 = r14.trim()
            java.lang.String r14 = r14.toLowerCase()
            java.lang.String r1 = "\\P{L}+"
            java.lang.String[] r2 = r14.split(r1)
            java.util.ArrayList r3 = new java.util.ArrayList
            r3.<init>()
            org.telegram.tgnet.TLRPC$Message r4 = r13.messageOwner
            java.lang.String r4 = r4.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 != 0) goto L_0x0058
            org.telegram.tgnet.TLRPC$Message r4 = r13.messageOwner
            java.lang.String r4 = r4.message
            java.lang.String r4 = r4.trim()
            java.lang.String r4 = r4.toLowerCase()
            boolean r5 = r4.contains(r14)
            if (r5 == 0) goto L_0x004d
            boolean r5 = r0.contains(r14)
            if (r5 != 0) goto L_0x004d
            r0.add(r14)
            r13.handleFoundWords(r0, r2)
            return
        L_0x004d:
            java.lang.String[] r4 = r4.split(r1)
            java.util.List r4 = java.util.Arrays.asList(r4)
            r3.addAll(r4)
        L_0x0058:
            org.telegram.tgnet.TLRPC$Document r4 = r13.getDocument()
            if (r4 == 0) goto L_0x0084
            org.telegram.tgnet.TLRPC$Document r4 = r13.getDocument()
            java.lang.String r4 = org.telegram.messenger.FileLoader.getDocumentFileName(r4)
            java.lang.String r4 = r4.toLowerCase()
            boolean r5 = r4.contains(r14)
            if (r5 == 0) goto L_0x0079
            boolean r5 = r0.contains(r14)
            if (r5 != 0) goto L_0x0079
            r0.add(r14)
        L_0x0079:
            java.lang.String[] r4 = r4.split(r1)
            java.util.List r4 = java.util.Arrays.asList(r4)
            r3.addAll(r4)
        L_0x0084:
            org.telegram.tgnet.TLRPC$Message r4 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = getMedia(r4)
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r4 == 0) goto L_0x00c8
            org.telegram.tgnet.TLRPC$Message r4 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = getMedia(r4)
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_webPage
            if (r4 == 0) goto L_0x00c8
            org.telegram.tgnet.TLRPC$Message r4 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = getMedia(r4)
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            java.lang.String r5 = r4.title
            if (r5 != 0) goto L_0x00a8
            java.lang.String r5 = r4.site_name
        L_0x00a8:
            if (r5 == 0) goto L_0x00c8
            java.lang.String r4 = r5.toLowerCase()
            boolean r5 = r4.contains(r14)
            if (r5 == 0) goto L_0x00bd
            boolean r5 = r0.contains(r14)
            if (r5 != 0) goto L_0x00bd
            r0.add(r14)
        L_0x00bd:
            java.lang.String[] r4 = r4.split(r1)
            java.util.List r4 = java.util.Arrays.asList(r4)
            r3.addAll(r4)
        L_0x00c8:
            java.lang.String r4 = r13.getMusicAuthor()
            if (r4 == 0) goto L_0x00ec
            java.lang.String r4 = r4.toLowerCase()
            boolean r5 = r4.contains(r14)
            if (r5 == 0) goto L_0x00e1
            boolean r5 = r0.contains(r14)
            if (r5 != 0) goto L_0x00e1
            r0.add(r14)
        L_0x00e1:
            java.lang.String[] r14 = r4.split(r1)
            java.util.List r14 = java.util.Arrays.asList(r14)
            r3.addAll(r14)
        L_0x00ec:
            r14 = 0
            r1 = 0
        L_0x00ee:
            int r4 = r2.length
            if (r1 >= r4) goto L_0x0168
            r4 = r2[r1]
            int r5 = r4.length()
            r6 = 2
            if (r5 >= r6) goto L_0x00fb
            goto L_0x0165
        L_0x00fb:
            r5 = 0
        L_0x00fc:
            int r6 = r3.size()
            if (r5 >= r6) goto L_0x0165
            java.lang.Object r6 = r3.get(r5)
            boolean r6 = r0.contains(r6)
            if (r6 == 0) goto L_0x010d
            goto L_0x0162
        L_0x010d:
            java.lang.Object r6 = r3.get(r5)
            java.lang.String r6 = (java.lang.String) r6
            char r7 = r4.charAt(r14)
            int r7 = r6.indexOf(r7)
            if (r7 >= 0) goto L_0x011e
            goto L_0x0162
        L_0x011e:
            int r8 = r4.length()
            int r9 = r6.length()
            int r8 = java.lang.Math.max(r8, r9)
            if (r7 == 0) goto L_0x0130
            java.lang.String r6 = r6.substring(r7)
        L_0x0130:
            int r7 = r4.length()
            int r9 = r6.length()
            int r7 = java.lang.Math.min(r7, r9)
            r9 = 0
            r10 = 0
        L_0x013e:
            if (r9 >= r7) goto L_0x014f
            char r11 = r6.charAt(r9)
            char r12 = r4.charAt(r9)
            if (r11 != r12) goto L_0x014f
            int r10 = r10 + 1
            int r9 = r9 + 1
            goto L_0x013e
        L_0x014f:
            float r6 = (float) r10
            float r7 = (float) r8
            float r6 = r6 / r7
            double r6 = (double) r6
            r8 = 4602678819172646912(0x3feNUM, double:0.5)
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 < 0) goto L_0x0162
            java.lang.Object r6 = r3.get(r5)
            java.lang.String r6 = (java.lang.String) r6
            r0.add(r6)
        L_0x0162:
            int r5 = r5 + 1
            goto L_0x00fc
        L_0x0165:
            int r1 = r1 + 1
            goto L_0x00ee
        L_0x0168:
            r13.handleFoundWords(r0, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.setQuery(java.lang.String):void");
    }

    private void handleFoundWords(ArrayList<String> arrayList, String[] strArr) {
        boolean z;
        if (!arrayList.isEmpty()) {
            boolean z2 = false;
            for (int i = 0; i < arrayList.size(); i++) {
                int i2 = 0;
                while (true) {
                    if (i2 >= strArr.length) {
                        break;
                    } else if (arrayList.get(i).contains(strArr[i2])) {
                        z2 = true;
                        break;
                    } else {
                        i2++;
                    }
                }
                if (z2) {
                    break;
                }
            }
            if (z2) {
                int i3 = 0;
                while (i3 < arrayList.size()) {
                    int i4 = 0;
                    while (true) {
                        if (i4 >= strArr.length) {
                            z = false;
                            break;
                        } else if (arrayList.get(i3).contains(strArr[i4])) {
                            z = true;
                            break;
                        } else {
                            i4++;
                        }
                    }
                    if (!z) {
                        arrayList.remove(i3);
                        i3--;
                    }
                    i3++;
                }
                if (arrayList.size() > 0) {
                    Collections.sort(arrayList, MessageObject$$ExternalSyntheticLambda1.INSTANCE);
                    arrayList.clear();
                    arrayList.add(arrayList.get(0));
                }
            }
            this.highlightedWords = arrayList;
            String str = this.messageOwner.message;
            if (str != null) {
                String trim = str.replace(10, ' ').replaceAll(" +", " ").trim();
                int length = trim.length();
                int indexOf = trim.toLowerCase().indexOf(arrayList.get(0));
                if (indexOf < 0) {
                    indexOf = 0;
                }
                if (length > 200) {
                    int max = Math.max(0, indexOf - 100);
                    trim = trim.substring(max, Math.min(length, (indexOf - max) + indexOf + 100));
                }
                this.messageTrimmedToHighlight = trim;
            }
        }
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$handleFoundWords$3(String str, String str2) {
        return str2.length() - str.length();
    }

    public void createMediaThumbs() {
        if (isVideo()) {
            TLRPC$Document document = getDocument();
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
            this.mediaThumb = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320), document);
            this.mediaSmallThumb = ImageLocation.getForDocument(closestPhotoSizeWithSize, document);
        } else if ((getMedia(this.messageOwner) instanceof TLRPC$TL_messageMediaPhoto) && getMedia(this.messageOwner).photo != null && !this.photoThumbs.isEmpty()) {
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, 50);
            this.mediaThumb = ImageLocation.getForObject(FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, 320, false, closestPhotoSizeWithSize2, false), this.photoThumbsObject);
            this.mediaSmallThumb = ImageLocation.getForObject(closestPhotoSizeWithSize2, this.photoThumbsObject);
        }
    }

    public boolean hasHighlightedWords() {
        ArrayList<String> arrayList = this.highlightedWords;
        return arrayList != null && !arrayList.isEmpty();
    }

    public boolean equals(MessageObject messageObject) {
        return getId() == messageObject.getId() && getDialogId() == messageObject.getDialogId();
    }

    public boolean isReactionsAvailable() {
        return !isEditing() && !isSponsored() && isSent() && this.messageOwner.action == null;
    }

    public boolean selectReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z, boolean z2) {
        ReactionsLayoutInBubble.VisibleReaction visibleReaction2 = visibleReaction;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message.reactions == null) {
            tLRPC$Message.reactions = new TLRPC$TL_messageReactions();
            this.messageOwner.reactions.can_see_list = isFromGroup() || isFromUser();
        }
        ArrayList arrayList = new ArrayList();
        TLRPC$ReactionCount tLRPC$ReactionCount = null;
        int i = 0;
        for (int i2 = 0; i2 < this.messageOwner.reactions.results.size(); i2++) {
            if (this.messageOwner.reactions.results.get(i2).chosen) {
                TLRPC$ReactionCount tLRPC$ReactionCount2 = this.messageOwner.reactions.results.get(i2);
                arrayList.add(tLRPC$ReactionCount2);
                int i3 = tLRPC$ReactionCount2.chosen_order;
                if (i3 > i) {
                    i = i3;
                }
            }
            TLRPC$Reaction tLRPC$Reaction = this.messageOwner.reactions.results.get(i2).reaction;
            if (tLRPC$Reaction instanceof TLRPC$TL_reactionEmoji) {
                String str = visibleReaction2.emojicon;
                if (str == null) {
                } else if (((TLRPC$TL_reactionEmoji) tLRPC$Reaction).emoticon.equals(str)) {
                    tLRPC$ReactionCount = this.messageOwner.reactions.results.get(i2);
                }
            }
            if (tLRPC$Reaction instanceof TLRPC$TL_reactionCustomEmoji) {
                long j = visibleReaction2.documentId;
                if (j != 0 && ((TLRPC$TL_reactionCustomEmoji) tLRPC$Reaction).document_id == j) {
                    tLRPC$ReactionCount = this.messageOwner.reactions.results.get(i2);
                }
            }
        }
        if (!arrayList.isEmpty() && arrayList.contains(tLRPC$ReactionCount) && z) {
            return true;
        }
        int maxUserReactionsCount = MessagesController.getInstance(this.currentAccount).getMaxUserReactionsCount();
        if (arrayList.isEmpty() || (!arrayList.contains(tLRPC$ReactionCount) && !z2)) {
            while (!arrayList.isEmpty() && arrayList.size() >= maxUserReactionsCount) {
                int i4 = 0;
                for (int i5 = 1; i5 < arrayList.size(); i5++) {
                    if (((TLRPC$ReactionCount) arrayList.get(i5)).chosen_order < ((TLRPC$ReactionCount) arrayList.get(i4)).chosen_order) {
                        i4 = i5;
                    }
                }
                TLRPC$ReactionCount tLRPC$ReactionCount3 = (TLRPC$ReactionCount) arrayList.get(i4);
                tLRPC$ReactionCount3.chosen = false;
                int i6 = tLRPC$ReactionCount3.count - 1;
                tLRPC$ReactionCount3.count = i6;
                if (i6 <= 0) {
                    this.messageOwner.reactions.results.remove(tLRPC$ReactionCount3);
                }
                arrayList.remove(tLRPC$ReactionCount3);
                if (this.messageOwner.reactions.can_see_list) {
                    int i7 = 0;
                    while (i7 < this.messageOwner.reactions.recent_reactions.size()) {
                        if (getPeerId(this.messageOwner.reactions.recent_reactions.get(i7).peer_id) == UserConfig.getInstance(this.currentAccount).getClientUserId() && ReactionsUtils.compare(this.messageOwner.reactions.recent_reactions.get(i7).reaction, visibleReaction2)) {
                            this.messageOwner.reactions.recent_reactions.remove(i7);
                            i7--;
                        }
                        i7++;
                    }
                }
            }
            if (tLRPC$ReactionCount == null) {
                tLRPC$ReactionCount = new TLRPC$TL_reactionCount();
                if (visibleReaction2.emojicon != null) {
                    TLRPC$TL_reactionEmoji tLRPC$TL_reactionEmoji = new TLRPC$TL_reactionEmoji();
                    tLRPC$ReactionCount.reaction = tLRPC$TL_reactionEmoji;
                    tLRPC$TL_reactionEmoji.emoticon = visibleReaction2.emojicon;
                    this.messageOwner.reactions.results.add(tLRPC$ReactionCount);
                } else {
                    TLRPC$TL_reactionCustomEmoji tLRPC$TL_reactionCustomEmoji = new TLRPC$TL_reactionCustomEmoji();
                    tLRPC$ReactionCount.reaction = tLRPC$TL_reactionCustomEmoji;
                    tLRPC$TL_reactionCustomEmoji.document_id = visibleReaction2.documentId;
                    this.messageOwner.reactions.results.add(tLRPC$ReactionCount);
                }
            }
            tLRPC$ReactionCount.chosen = true;
            tLRPC$ReactionCount.count++;
            tLRPC$ReactionCount.chosen_order = i + 1;
            TLRPC$Message tLRPC$Message2 = this.messageOwner;
            if (tLRPC$Message2.reactions.can_see_list || (tLRPC$Message2.dialog_id > 0 && maxUserReactionsCount > 1)) {
                TLRPC$TL_messagePeerReaction tLRPC$TL_messagePeerReaction = new TLRPC$TL_messagePeerReaction();
                this.messageOwner.reactions.recent_reactions.add(0, tLRPC$TL_messagePeerReaction);
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_messagePeerReaction.peer_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                if (visibleReaction2.emojicon != null) {
                    TLRPC$TL_reactionEmoji tLRPC$TL_reactionEmoji2 = new TLRPC$TL_reactionEmoji();
                    tLRPC$TL_messagePeerReaction.reaction = tLRPC$TL_reactionEmoji2;
                    tLRPC$TL_reactionEmoji2.emoticon = visibleReaction2.emojicon;
                } else {
                    TLRPC$TL_reactionCustomEmoji tLRPC$TL_reactionCustomEmoji2 = new TLRPC$TL_reactionCustomEmoji();
                    tLRPC$TL_messagePeerReaction.reaction = tLRPC$TL_reactionCustomEmoji2;
                    tLRPC$TL_reactionCustomEmoji2.document_id = visibleReaction2.documentId;
                }
            }
            this.reactionsChanged = true;
            return true;
        }
        tLRPC$ReactionCount.chosen = false;
        int i8 = tLRPC$ReactionCount.count - 1;
        tLRPC$ReactionCount.count = i8;
        if (i8 <= 0) {
            this.messageOwner.reactions.results.remove(tLRPC$ReactionCount);
        }
        if (this.messageOwner.reactions.can_see_list) {
            int i9 = 0;
            while (i9 < this.messageOwner.reactions.recent_reactions.size()) {
                if (getPeerId(this.messageOwner.reactions.recent_reactions.get(i9).peer_id) == UserConfig.getInstance(this.currentAccount).getClientUserId() && ReactionsUtils.compare(this.messageOwner.reactions.recent_reactions.get(i9).reaction, visibleReaction2)) {
                    this.messageOwner.reactions.recent_reactions.remove(i9);
                    i9--;
                }
                i9++;
            }
        }
        this.reactionsChanged = true;
        return false;
    }

    public boolean probablyRingtone() {
        if (getDocument() != null && RingtoneDataStore.ringtoneSupportedMimeType.contains(getDocument().mime_type) && getDocument().size < ((long) (MessagesController.getInstance(this.currentAccount).ringtoneSizeMax * 2))) {
            for (int i = 0; i < getDocument().attributes.size(); i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = getDocument().attributes.get(i);
                if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeAudio) && tLRPC$DocumentAttribute.duration < 60) {
                    return true;
                }
            }
        }
        return false;
    }
}
