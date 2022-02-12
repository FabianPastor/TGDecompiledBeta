package org.telegram.messenger;

import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatInvite;
import org.telegram.tgnet.TLRPC$DecryptedMessageAction;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$InputStickerSet;
import org.telegram.tgnet.TLRPC$KeyboardButton;
import org.telegram.tgnet.TLRPC$Message;
import org.telegram.tgnet.TLRPC$MessageAction;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$MessageFwdHeader;
import org.telegram.tgnet.TLRPC$MessageMedia;
import org.telegram.tgnet.TLRPC$MessageReplies;
import org.telegram.tgnet.TLRPC$Page;
import org.telegram.tgnet.TLRPC$PageBlock;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$PollResults;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_chatBannedRights;
import org.telegram.tgnet.TLRPC$TL_chatInviteExported;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
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
import org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC$TL_messageEmpty;
import org.telegram.tgnet.TLRPC$TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC$TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC$TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC$TL_messageForwarded_old2;
import org.telegram.tgnet.TLRPC$TL_messageMediaContact;
import org.telegram.tgnet.TLRPC$TL_messageMediaDice;
import org.telegram.tgnet.TLRPC$TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC$TL_messageMediaGame;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC$TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC$TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC$TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC$TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC$TL_messagePeerReaction;
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_messageReplyHeader;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_message_secret;
import org.telegram.tgnet.TLRPC$TL_pageBlockCollage;
import org.telegram.tgnet.TLRPC$TL_pageBlockPhoto;
import org.telegram.tgnet.TLRPC$TL_pageBlockSlideshow;
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
import org.telegram.tgnet.TLRPC$TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC$TL_webPage;
import org.telegram.tgnet.TLRPC$TL_webPageAttributeTheme;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$VideoSize;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;
import org.telegram.ui.Components.spoilers.SpoilerEffect;

public class MessageObject {
    private static final int LINES_PER_BLOCK = 10;
    public static final int MESSAGE_SEND_STATE_EDITING = 3;
    public static final int MESSAGE_SEND_STATE_SENDING = 1;
    public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
    public static final int MESSAGE_SEND_STATE_SENT = 0;
    public static final int POSITION_FLAG_BOTTOM = 8;
    public static final int POSITION_FLAG_LEFT = 1;
    public static final int POSITION_FLAG_RIGHT = 2;
    public static final int POSITION_FLAG_TOP = 4;
    public static final int TYPE_ANIMATED_STICKER = 15;
    public static final int TYPE_GEO = 4;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_POLL = 17;
    public static final int TYPE_ROUND_VIDEO = 5;
    public static final int TYPE_STICKER = 13;
    public static final int TYPE_VIDEO = 3;
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
    private int emojiOnlyCount;
    public long eventId;
    public float forceSeekTo;
    public boolean forceUpdate;
    private float generatedWithDensity;
    private int generatedWithMinSize;
    public float gifState;
    public boolean hadAnimationNotReadyLoading;
    public boolean hasRtl;
    public ArrayList<String> highlightedWords;
    public boolean isDateObject;
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
    public long pollLastCheckTime;
    public boolean pollVisibleOnScreen;
    public boolean preview;
    public String previousAttachPath;
    public TLRPC$MessageMedia previousMedia;
    public String previousMessage;
    public ArrayList<TLRPC$MessageEntity> previousMessageEntities;
    public boolean reactionsChanged;
    public long reactionsLastCheckTime;
    public MessageObject replyMessageObject;
    public boolean resendAsIs;
    public boolean scheduled;
    public SendAnimationData sendAnimationData;
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
    public int type;
    public boolean useCustomPhoto;
    public CharSequence vCardData;
    public VideoEditedInfo videoEditedInfo;
    public boolean viewsReloaded;
    public int wantedBotKeyboardWidth;
    public boolean wasJustSent;
    public boolean wasUnread;

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

    public int getEmojiOnlyCount() {
        return this.emojiOnlyCount;
    }

    public boolean shouldDrawReactionsInLayout() {
        return getDialogId() < 0;
    }

    public TLRPC$TL_messagePeerReaction getRandomUnreadReaction() {
        ArrayList<TLRPC$TL_messagePeerReaction> arrayList;
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
        public int leftSpanOffset;
        public byte maxX;
        public byte maxY;
        public byte minX;
        public byte minY;
        public float ph;
        public int pw;
        public float[] siblingHeights;
        public int spanSize;

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
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x006a, code lost:
            if ((r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice) == false) goto L_0x006e;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* JADX WARNING: Removed duplicated region for block: B:88:0x0211  */
        /* JADX WARNING: Removed duplicated region for block: B:92:0x0271  */
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
                if (r7 >= r1) goto L_0x00df
                java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.messages
                java.lang.Object r13 = r13.get(r7)
                org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
                if (r7 != 0) goto L_0x007d
                boolean r11 = r13.isOutOwner()
                if (r11 != 0) goto L_0x006e
                org.telegram.tgnet.TLRPC$Message r8 = r13.messageOwner
                org.telegram.tgnet.TLRPC$MessageFwdHeader r14 = r8.fwd_from
                if (r14 == 0) goto L_0x004c
                org.telegram.tgnet.TLRPC$Peer r14 = r14.saved_from_peer
                if (r14 != 0) goto L_0x006c
            L_0x004c:
                org.telegram.tgnet.TLRPC$Peer r14 = r8.from_id
                boolean r14 = r14 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
                if (r14 == 0) goto L_0x006e
                org.telegram.tgnet.TLRPC$Peer r14 = r8.peer_id
                long r5 = r14.channel_id
                r17 = 0
                int r19 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
                if (r19 != 0) goto L_0x006c
                long r5 = r14.chat_id
                int r14 = (r5 > r17 ? 1 : (r5 == r17 ? 0 : -1))
                if (r14 != 0) goto L_0x006c
                org.telegram.tgnet.TLRPC$MessageMedia r5 = r8.media
                boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r6 != 0) goto L_0x006c
                boolean r5 = r5 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
                if (r5 == 0) goto L_0x006e
            L_0x006c:
                r8 = 1
                goto L_0x006f
            L_0x006e:
                r8 = 0
            L_0x006f:
                boolean r5 = r13.isMusic()
                if (r5 != 0) goto L_0x007b
                boolean r5 = r13.isDocument()
                if (r5 == 0) goto L_0x007d
            L_0x007b:
                r0.isDocuments = r2
            L_0x007d:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r5 = r13.photoThumbs
                int r6 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
                org.telegram.tgnet.TLRPC$PhotoSize r5 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r5, r6)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r6 = new org.telegram.messenger.MessageObject$GroupedMessagePosition
                r6.<init>()
                int r14 = r1 + -1
                if (r7 != r14) goto L_0x0092
                r14 = 1
                goto L_0x0093
            L_0x0092:
                r14 = 0
            L_0x0093:
                r6.last = r14
                if (r5 != 0) goto L_0x009a
                r5 = 1065353216(0x3var_, float:1.0)
                goto L_0x00a2
            L_0x009a:
                int r14 = r5.w
                float r14 = (float) r14
                int r5 = r5.h
                float r5 = (float) r5
                float r5 = r14 / r5
            L_0x00a2:
                r6.aspectRatio = r5
                int r12 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r12 <= 0) goto L_0x00ae
                java.lang.String r5 = "w"
                r4.append(r5)
                goto L_0x00c0
            L_0x00ae:
                r12 = 1061997773(0x3f4ccccd, float:0.8)
                int r5 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r5 >= 0) goto L_0x00bb
                java.lang.String r5 = "n"
                r4.append(r5)
                goto L_0x00c0
            L_0x00bb:
                java.lang.String r5 = "q"
                r4.append(r5)
            L_0x00c0:
                float r5 = r6.aspectRatio
                float r9 = r9 + r5
                r12 = 1073741824(0x40000000, float:2.0)
                int r5 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r5 <= 0) goto L_0x00ca
                r10 = 1
            L_0x00ca:
                java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r0.positions
                r5.put(r13, r6)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r0.posArray
                r5.add(r6)
                java.lang.CharSequence r5 = r13.caption
                if (r5 == 0) goto L_0x00da
                r0.hasCaption = r2
            L_0x00da:
                int r7 = r7 + 1
                r5 = 0
                goto L_0x002d
            L_0x00df:
                boolean r5 = r0.isDocuments
                r6 = 1120403456(0x42CLASSNAME, float:100.0)
                r7 = 1000(0x3e8, float:1.401E-42)
                r13 = 3
                if (r5 == 0) goto L_0x0125
                r3 = 0
            L_0x00e9:
                if (r3 >= r1) goto L_0x0124
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r4 = r0.posArray
                java.lang.Object r4 = r4.get(r3)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4
                int r5 = r4.flags
                r5 = r5 | r13
                r4.flags = r5
                if (r3 != 0) goto L_0x00ff
                r5 = r5 | 4
                r4.flags = r5
                goto L_0x0109
            L_0x00ff:
                int r8 = r1 + -1
                if (r3 != r8) goto L_0x0109
                r5 = r5 | 8
                r4.flags = r5
                r4.last = r2
            L_0x0109:
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
                goto L_0x00e9
            L_0x0124:
                return
            L_0x0125:
                if (r8 == 0) goto L_0x0130
                int r5 = r0.maxSizeWidth
                int r5 = r5 + -50
                r0.maxSizeWidth = r5
                r5 = 250(0xfa, float:3.5E-43)
                goto L_0x0132
            L_0x0130:
                r5 = 200(0xc8, float:2.8E-43)
            L_0x0132:
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
                if (r10 != 0) goto L_0x0577
                if (r1 == r15) goto L_0x017a
                if (r1 == r13) goto L_0x017a
                if (r1 != r12) goto L_0x0577
            L_0x017a:
                r10 = 1137410048(0x43cb8000, float:407.0)
                r12 = 1053609165(0x3ecccccd, float:0.4)
                if (r1 != r15) goto L_0x02ae
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
                if (r17 == 0) goto L_0x0200
                r17 = r7
                double r6 = (double) r9
                r21 = 4608983858650965606(0x3ffNUM, double:1.4)
                double r8 = (double) r8
                java.lang.Double.isNaN(r8)
                double r8 = r8 * r21
                int r21 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
                if (r21 <= 0) goto L_0x0202
                float r6 = r2.aspectRatio
                float r7 = r13.aspectRatio
                float r8 = r6 - r7
                double r8 = (double) r8
                r21 = 4596373779694328218(0x3fCLASSNAMEa, double:0.2)
                int r23 = (r8 > r21 ? 1 : (r8 == r21 ? 0 : -1))
                if (r23 >= 0) goto L_0x0202
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
            L_0x01fd:
                r6 = 2
                goto L_0x02ab
            L_0x0200:
                r17 = r7
            L_0x0202:
                boolean r6 = r4.equals(r14)
                if (r6 != 0) goto L_0x0271
                java.lang.String r6 = "qq"
                boolean r4 = r4.equals(r6)
                if (r4 == 0) goto L_0x0211
                goto L_0x0271
            L_0x0211:
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
                if (r6 >= r7) goto L_0x0238
                int r6 = r7 - r6
                int r4 = r4 - r6
                r6 = r7
            L_0x0238:
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
                goto L_0x01fd
            L_0x0271:
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
            L_0x02aa:
                r2 = 1
            L_0x02ab:
                r14 = r2
                goto L_0x07a1
            L_0x02ae:
                r8 = 2
                r9 = 1141264221(0x44064f5d, float:537.24005)
                if (r1 != r13) goto L_0x03d8
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
                if (r4 != r15) goto L_0x0372
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
                if (r11 == 0) goto L_0x036a
                int r3 = r3 - r7
                r12.spanSize = r3
                goto L_0x036f
            L_0x036a:
                int r3 = r3 - r2
                r14.spanSize = r3
                r13.leftSpanOffset = r2
            L_0x036f:
                r0.hasSibling = r4
                goto L_0x03d5
            L_0x0372:
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
                if (r3 >= 0) goto L_0x03b7
                r2 = r6
            L_0x03b7:
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
            L_0x03d5:
                r14 = 1
                goto L_0x07a1
            L_0x03d8:
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
                if (r4 != r15) goto L_0x04c0
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
                if (r10 >= r14) goto L_0x0481
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r14 = r14 - r10
                int r10 = org.telegram.messenger.AndroidUtilities.dp(r13)
                int r13 = r14 / 2
                int r9 = r9 - r13
                int r14 = r14 - r13
                int r7 = r7 - r14
            L_0x0481:
                r26 = r9
                r9 = 1145798656(0x444b8000, float:814.0)
                float r2 = r9 - r2
                float r2 = java.lang.Math.min(r2, r4)
                float r2 = r2 / r9
                int r4 = (r2 > r6 ? 1 : (r2 == r6 ? 0 : -1))
                if (r4 >= 0) goto L_0x0492
                r2 = r6
            L_0x0492:
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
                goto L_0x02ab
            L_0x04c0:
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
                if (r11 == 0) goto L_0x055e
                int r6 = r6 - r4
                r10.spanSize = r6
                goto L_0x0565
            L_0x055e:
                int r6 = r6 - r2
                r3.spanSize = r6
                r12.leftSpanOffset = r2
                r8.leftSpanOffset = r2
            L_0x0565:
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
                goto L_0x02aa
            L_0x0577:
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r0.posArray
                int r2 = r2.size()
                float[] r3 = new float[r2]
                r4 = 0
            L_0x0580:
                if (r4 >= r1) goto L_0x05c3
                r8 = 1066192077(0x3f8ccccd, float:1.1)
                int r8 = (r9 > r8 ? 1 : (r9 == r8 ? 0 : -1))
                if (r8 <= 0) goto L_0x059c
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r0.posArray
                java.lang.Object r8 = r8.get(r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                float r8 = r8.aspectRatio
                r10 = 1065353216(0x3var_, float:1.0)
                float r8 = java.lang.Math.max(r10, r8)
                r3[r4] = r8
                goto L_0x05ae
            L_0x059c:
                r10 = 1065353216(0x3var_, float:1.0)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r0.posArray
                java.lang.Object r8 = r8.get(r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                float r8 = r8.aspectRatio
                float r8 = java.lang.Math.min(r10, r8)
                r3[r4] = r8
            L_0x05ae:
                r8 = 1059760867(0x3f2aaae3, float:0.66667)
                r13 = 1071225242(0x3fd9999a, float:1.7)
                r14 = r3[r4]
                float r13 = java.lang.Math.min(r13, r14)
                float r8 = java.lang.Math.max(r8, r13)
                r3[r4] = r8
                int r4 = r4 + 1
                goto L_0x0580
            L_0x05c3:
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r8 = 1
            L_0x05c9:
                if (r8 >= r2) goto L_0x05e8
                int r10 = r2 - r8
                r13 = 3
                if (r8 > r13) goto L_0x05e5
                if (r10 <= r13) goto L_0x05d3
                goto L_0x05e5
            L_0x05d3:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r13 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r14 = 0
                float r15 = r0.multiHeight(r3, r14, r8)
                r14 = r15
                float r15 = r0.multiHeight(r3, r8, r2)
                r13.<init>(r8, r10, r14, r15)
                r4.add(r13)
            L_0x05e5:
                int r8 = r8 + 1
                goto L_0x05c9
            L_0x05e8:
                r8 = 1
            L_0x05e9:
                int r10 = r2 + -1
                if (r8 >= r10) goto L_0x062a
                r10 = 1
            L_0x05ee:
                int r13 = r2 - r8
                if (r10 >= r13) goto L_0x0627
                int r13 = r13 - r10
                r14 = 3
                if (r8 > r14) goto L_0x0624
                r15 = 1062836634(0x3var_a, float:0.85)
                int r15 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
                if (r15 >= 0) goto L_0x05ff
                r15 = 4
                goto L_0x0600
            L_0x05ff:
                r15 = 3
            L_0x0600:
                if (r10 > r15) goto L_0x0624
                if (r13 <= r14) goto L_0x0605
                goto L_0x0624
            L_0x0605:
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
            L_0x0624:
                int r10 = r10 + 1
                goto L_0x05ee
            L_0x0627:
                int r8 = r8 + 1
                goto L_0x05e9
            L_0x062a:
                r8 = 1
            L_0x062b:
                int r9 = r2 + -2
                if (r8 >= r9) goto L_0x067f
                r9 = 1
            L_0x0630:
                int r10 = r2 - r8
                if (r9 >= r10) goto L_0x067b
                r13 = 1
            L_0x0635:
                int r14 = r10 - r9
                if (r13 >= r14) goto L_0x0677
                int r14 = r14 - r13
                r15 = 3
                if (r8 > r15) goto L_0x066f
                if (r9 > r15) goto L_0x066f
                if (r13 > r15) goto L_0x066f
                if (r14 <= r15) goto L_0x0644
                goto L_0x066f
            L_0x0644:
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
                goto L_0x0671
            L_0x066f:
                r21 = r10
            L_0x0671:
                int r13 = r13 + 1
                r10 = r21
                r12 = 4
                goto L_0x0635
            L_0x0677:
                int r9 = r9 + 1
                r12 = 4
                goto L_0x0630
            L_0x067b:
                int r8 = r8 + 1
                r12 = 4
                goto L_0x062b
            L_0x067f:
                int r2 = r0.maxSizeWidth
                r8 = 3
                int r2 = r2 / r8
                r8 = 4
                int r2 = r2 * 4
                float r2 = (float) r2
                r12 = 0
                r13 = 0
                r14 = 0
            L_0x068a:
                int r15 = r4.size()
                if (r12 >= r15) goto L_0x070d
                java.lang.Object r15 = r4.get(r12)
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r15 = (org.telegram.messenger.MessageObject.GroupedMessages.MessageGroupedLayoutAttempt) r15
                r16 = 2139095039(0x7f7fffff, float:3.4028235E38)
                r8 = 0
                r22 = 0
            L_0x069c:
                float[] r9 = r15.heights
                int r10 = r9.length
                if (r8 >= r10) goto L_0x06b2
                r10 = r9[r8]
                float r22 = r22 + r10
                r10 = r9[r8]
                int r10 = (r10 > r16 ? 1 : (r10 == r16 ? 0 : -1))
                if (r10 >= 0) goto L_0x06af
                r9 = r9[r8]
                r16 = r9
            L_0x06af:
                int r8 = r8 + 1
                goto L_0x069c
            L_0x06b2:
                float r22 = r22 - r2
                float r8 = java.lang.Math.abs(r22)
                int[] r9 = r15.lineCounts
                int r10 = r9.length
                r22 = r2
                r2 = 1
                r17 = r4
                if (r10 <= r2) goto L_0x06ef
                r10 = 0
                r4 = r9[r10]
                r10 = r9[r2]
                if (r4 > r10) goto L_0x06e8
                int r4 = r9.length
                r10 = 2
                if (r4 <= r10) goto L_0x06d9
                r4 = r9[r2]
                r2 = r9[r10]
                if (r4 > r2) goto L_0x06d4
                goto L_0x06d9
            L_0x06d4:
                r2 = 1067030938(0x3var_a, float:1.2)
                r4 = 3
                goto L_0x06ec
            L_0x06d9:
                int r2 = r9.length
                r4 = 3
                if (r2 <= r4) goto L_0x06e4
                r2 = r9[r10]
                r9 = r9[r4]
                if (r2 <= r9) goto L_0x06e4
                goto L_0x06e9
            L_0x06e4:
                r2 = 1067030938(0x3var_a, float:1.2)
                goto L_0x06f3
            L_0x06e8:
                r4 = 3
            L_0x06e9:
                r2 = 1067030938(0x3var_a, float:1.2)
            L_0x06ec:
                float r8 = r8 * r2
                goto L_0x06f3
            L_0x06ef:
                r2 = 1067030938(0x3var_a, float:1.2)
                r4 = 3
            L_0x06f3:
                float r9 = (float) r7
                int r9 = (r16 > r9 ? 1 : (r16 == r9 ? 0 : -1))
                if (r9 >= 0) goto L_0x06fc
                r9 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r8 = r8 * r9
            L_0x06fc:
                if (r13 == 0) goto L_0x0702
                int r9 = (r8 > r14 ? 1 : (r8 == r14 ? 0 : -1))
                if (r9 >= 0) goto L_0x0704
            L_0x0702:
                r14 = r8
                r13 = r15
            L_0x0704:
                int r12 = r12 + 1
                r4 = r17
                r2 = r22
                r8 = 4
                goto L_0x068a
            L_0x070d:
                if (r13 != 0) goto L_0x0710
                return
            L_0x0710:
                r2 = 0
                r4 = 0
                r7 = 0
            L_0x0713:
                int[] r8 = r13.lineCounts
                int r9 = r8.length
                if (r2 >= r9) goto L_0x07a0
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
            L_0x072a:
                if (r7 >= r8) goto L_0x0788
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
                if (r2 != 0) goto L_0x0746
                r4 = 4
                goto L_0x0747
            L_0x0746:
                r4 = 0
            L_0x0747:
                int[] r8 = r13.lineCounts
                int r8 = r8.length
                r20 = 1
                int r8 = r8 + -1
                if (r2 != r8) goto L_0x0752
                r4 = r4 | 8
            L_0x0752:
                if (r7 != 0) goto L_0x075a
                r4 = r4 | 1
                if (r11 == 0) goto L_0x075a
                r15 = r29
            L_0x075a:
                if (r7 != r12) goto L_0x0765
                r4 = r4 | 2
                if (r11 != 0) goto L_0x0765
                r36 = r4
                r15 = r29
                goto L_0x0767
            L_0x0765:
                r36 = r4
            L_0x0767:
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
                goto L_0x072a
            L_0x0788:
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
                goto L_0x0713
            L_0x07a0:
                r14 = r4
            L_0x07a1:
                r2 = 0
            L_0x07a2:
                if (r2 >= r1) goto L_0x081e
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r0.posArray
                java.lang.Object r3 = r3.get(r2)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                if (r11 == 0) goto L_0x07c2
                byte r4 = r3.minX
                if (r4 != 0) goto L_0x07b7
                int r4 = r3.spanSize
                int r4 = r4 + r5
                r3.spanSize = r4
            L_0x07b7:
                int r4 = r3.flags
                r6 = 2
                r4 = r4 & r6
                if (r4 == 0) goto L_0x07c0
                r4 = 1
                r3.edge = r4
            L_0x07c0:
                r6 = 1
                goto L_0x07d9
            L_0x07c2:
                r6 = 2
                byte r4 = r3.maxX
                if (r4 == r14) goto L_0x07cc
                int r4 = r3.flags
                r4 = r4 & r6
                if (r4 == 0) goto L_0x07d1
            L_0x07cc:
                int r4 = r3.spanSize
                int r4 = r4 + r5
                r3.spanSize = r4
            L_0x07d1:
                int r4 = r3.flags
                r6 = 1
                r4 = r4 & r6
                if (r4 == 0) goto L_0x07d9
                r3.edge = r6
            L_0x07d9:
                java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.messages
                java.lang.Object r4 = r4.get(r2)
                org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
                if (r11 != 0) goto L_0x0818
                boolean r4 = r4.needDrawAvatarInternal()
                if (r4 == 0) goto L_0x0818
                boolean r4 = r3.edge
                if (r4 == 0) goto L_0x07fe
                int r4 = r3.spanSize
                r7 = 1000(0x3e8, float:1.401E-42)
                if (r4 == r7) goto L_0x07f7
                int r4 = r4 + 108
                r3.spanSize = r4
            L_0x07f7:
                int r4 = r3.pw
                int r4 = r4 + 108
                r3.pw = r4
                goto L_0x0818
            L_0x07fe:
                int r4 = r3.flags
                r7 = 2
                r4 = r4 & r7
                if (r4 == 0) goto L_0x0819
                int r4 = r3.spanSize
                r8 = 1000(0x3e8, float:1.401E-42)
                if (r4 == r8) goto L_0x080f
                int r4 = r4 + -108
                r3.spanSize = r4
                goto L_0x081b
            L_0x080f:
                int r4 = r3.leftSpanOffset
                if (r4 == 0) goto L_0x081b
                int r4 = r4 + 108
                r3.leftSpanOffset = r4
                goto L_0x081b
            L_0x0818:
                r7 = 2
            L_0x0819:
                r8 = 1000(0x3e8, float:1.401E-42)
            L_0x081b:
                int r2 = r2 + 1
                goto L_0x07a2
            L_0x081e:
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
        LongSparseArray<TLRPC$Chat> longSparseArray3;
        AbstractMap<Long, TLRPC$Chat> abstractMap3;
        TLRPC$User tLRPC$User;
        TextPaint textPaint;
        int i2;
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        AbstractMap<Long, TLRPC$User> abstractMap4 = abstractMap;
        LongSparseArray<TLRPC$User> longSparseArray4 = longSparseArray;
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
            tLRPC$User = getUser(abstractMap4, longSparseArray4, tLRPC$Peer.user_id);
            abstractMap3 = abstractMap2;
            longSparseArray3 = longSparseArray2;
        } else {
            abstractMap3 = abstractMap2;
            longSparseArray3 = longSparseArray2;
            tLRPC$User = null;
        }
        updateMessageText(abstractMap4, abstractMap3, longSparseArray4, longSparseArray3);
        setType();
        measureInlineBotButtons();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(((long) this.messageOwner.date) * 1000);
        int i3 = gregorianCalendar.get(6);
        int i4 = gregorianCalendar.get(1);
        int i5 = gregorianCalendar.get(2);
        this.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i3)});
        this.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5)});
        createMessageSendInfo();
        generateCaption();
        boolean z4 = z;
        if (z4) {
            if (this.messageOwner.media instanceof TLRPC$TL_messageMediaGame) {
                textPaint = Theme.chat_msgGameTextPaint;
            } else {
                textPaint = Theme.chat_msgTextPaint;
            }
            int[] iArr = allowsBigEmoji() ? new int[1] : null;
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            checkEmojiOnly(iArr);
            this.emojiAnimatedSticker = null;
            if (this.emojiOnlyCount == 1) {
                TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message2.media;
                if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaInvoice) && tLRPC$Message2.entities.isEmpty()) {
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
                    if (!TextUtils.isEmpty(this.emojiAnimatedStickerColor) && (i2 = indexOf + 2) < this.messageText.length()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(charSequence.toString());
                        CharSequence charSequence2 = this.messageText;
                        sb.append(charSequence2.subSequence(i2, charSequence2.length()).toString());
                        charSequence = sb.toString();
                    }
                    if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) || EmojiData.emojiColoredMap.contains(charSequence.toString())) {
                        this.emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(charSequence);
                    }
                }
            }
            if (this.emojiAnimatedSticker == null) {
                generateLayout(tLRPC$User);
            } else {
                this.type = 1000;
                if (isSticker()) {
                    this.type = 13;
                } else if (isAnimatedSticker()) {
                    this.type = 15;
                }
            }
            createPathThumb();
        }
        this.layoutCreated = z4;
        generateThumbs(false);
        if (z2) {
            checkMediaExistance();
        }
    }

    private void createPathThumb() {
        TLRPC$Document document = getDocument();
        if (document != null) {
            this.pathThumb = DocumentObject.getSvgThumb(document, "chat_serviceBackground", 1.0f);
        }
    }

    public void createStrippedThumb() {
        if (this.photoThumbs != null && SharedConfig.getDevicePerformanceClass() == 2) {
            try {
                int size = this.photoThumbs.size();
                for (int i = 0; i < size; i++) {
                    TLRPC$PhotoSize tLRPC$PhotoSize = this.photoThumbs.get(i);
                    if (tLRPC$PhotoSize instanceof TLRPC$TL_photoStrippedSize) {
                        this.strippedThumb = new BitmapDrawable(ImageLoader.getStrippedPhotoBitmap(tLRPC$PhotoSize.bytes, "b"));
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
        TextPaint textPaint;
        int i;
        if (iArr != null) {
            if (iArr[0] >= 1 && iArr[0] <= 3) {
                int i2 = iArr[0];
                if (i2 == 1) {
                    textPaint = Theme.chat_msgTextPaintOneEmoji;
                    i = AndroidUtilities.dp(32.0f);
                    this.emojiOnlyCount = 1;
                } else if (i2 != 2) {
                    textPaint = Theme.chat_msgTextPaintThreeEmoji;
                    i = AndroidUtilities.dp(24.0f);
                    this.emojiOnlyCount = 3;
                } else {
                    textPaint = Theme.chat_msgTextPaintTwoEmoji;
                    int dp = AndroidUtilities.dp(28.0f);
                    this.emojiOnlyCount = 2;
                    i = dp;
                }
                CharSequence charSequence = this.messageText;
                Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) ((Spannable) charSequence).getSpans(0, charSequence.length(), Emoji.EmojiSpan.class);
                if (emojiSpanArr != null && emojiSpanArr.length > 0) {
                    for (Emoji.EmojiSpan replaceFontMetrics : emojiSpanArr) {
                        replaceFontMetrics.replaceFontMetrics(textPaint.getFontMetricsInt(), i);
                    }
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:428:0x0bc7, code lost:
        if (r9.id == r11.id) goto L_0x0bcc;
     */
    /* JADX WARNING: Removed duplicated region for block: B:181:0x04d1  */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x04e1 A[LOOP:0: B:167:0x04a8->B:187:0x04e1, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:443:0x0bfa  */
    /* JADX WARNING: Removed duplicated region for block: B:444:0x0c0a  */
    /* JADX WARNING: Removed duplicated region for block: B:447:0x0c1f  */
    /* JADX WARNING: Removed duplicated region for block: B:461:0x0cc2  */
    /* JADX WARNING: Removed duplicated region for block: B:462:0x0ccd  */
    /* JADX WARNING: Removed duplicated region for block: B:721:0x141d  */
    /* JADX WARNING: Removed duplicated region for block: B:724:0x146b  */
    /* JADX WARNING: Removed duplicated region for block: B:726:0x146e  */
    /* JADX WARNING: Removed duplicated region for block: B:736:0x14e0  */
    /* JADX WARNING: Removed duplicated region for block: B:739:0x14e7  */
    /* JADX WARNING: Removed duplicated region for block: B:758:0x04fb A[EDGE_INSN: B:758:0x04fb->B:189:0x04fb ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:760:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MessageObject(int r28, org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent r29, java.util.ArrayList<org.telegram.messenger.MessageObject> r30, java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.MessageObject>> r31, org.telegram.tgnet.TLRPC$Chat r32, int[] r33, boolean r34) {
        /*
            r27 = this;
            r6 = r27
            r7 = r29
            r8 = r30
            r0 = r32
            r27.<init>()
            r1 = 1000(0x3e8, float:1.401E-42)
            r6.type = r1
            r1 = -1082130432(0xffffffffbvar_, float:-1.0)
            r6.forceSeekTo = r1
            r6.currentEvent = r7
            r1 = r28
            r6.currentAccount = r1
            long r2 = r7.user_id
            r4 = 0
            int r10 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r10 <= 0) goto L_0x0031
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r28)
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
            if (r3 == 0) goto L_0x00ca
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle) r2
            java.lang.String r1 = r2.new_value
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x00b3
            r2 = 2131625484(0x7f0e060c, float:1.8878177E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            r3[r9] = r1
            java.lang.String r1 = "EventLogEditedGroupTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x00b3:
            r2 = 2131625479(0x7f0e0607, float:1.8878167E38)
            java.lang.Object[] r3 = new java.lang.Object[r11]
            r3[r9] = r1
            java.lang.String r1 = "EventLogEditedChannelTitle"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r3)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
        L_0x00c6:
            r8 = r7
            r7 = r14
            goto L_0x1418
        L_0x00ca:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangePhoto
            if (r3 == 0) goto L_0x0169
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangePhoto r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangePhoto) r2
            org.telegram.tgnet.TLRPC$TL_messageService r1 = new org.telegram.tgnet.TLRPC$TL_messageService
            r1.<init>()
            r6.messageOwner = r1
            org.telegram.tgnet.TLRPC$Photo r3 = r2.new_photo
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r3 == 0) goto L_0x0108
            org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto r2 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            r2.<init>()
            r1.action = r2
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x00f8
            r1 = 2131625542(0x7f0e0646, float:1.8878295E38)
            java.lang.String r2 = "EventLogRemovedWGroupPhoto"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x00f8:
            r1 = 2131625536(0x7f0e0640, float:1.8878283E38)
            java.lang.String r2 = "EventLogRemovedChannelPhoto"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x0108:
            org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto r3 = new org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            r3.<init>()
            r1.action = r3
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            org.telegram.tgnet.TLRPC$Photo r2 = r2.new_photo
            r1.photo = r2
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x0141
            boolean r1 = r27.isVideoAvatar()
            if (r1 == 0) goto L_0x0131
            r1 = 2131625485(0x7f0e060d, float:1.887818E38)
            java.lang.String r2 = "EventLogEditedGroupVideo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x0131:
            r1 = 2131625482(0x7f0e060a, float:1.8878173E38)
            java.lang.String r2 = "EventLogEditedGroupPhoto"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x0141:
            boolean r1 = r27.isVideoAvatar()
            if (r1 == 0) goto L_0x0158
            r1 = 2131625480(0x7f0e0608, float:1.887817E38)
            java.lang.String r2 = "EventLogEditedChannelVideo"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x0158:
            r1 = 2131625477(0x7f0e0605, float:1.8878163E38)
            java.lang.String r2 = "EventLogEditedChannelPhoto"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x0169:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoin
            java.lang.String r9 = "EventLogGroupJoined"
            r12 = 2131625470(0x7f0e05fe, float:1.8878149E38)
            java.lang.String r11 = "EventLogChannelJoined"
            if (r3 == 0) goto L_0x0193
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x0187
            r1 = 2131625507(0x7f0e0623, float:1.8878224E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x0187:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r12)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x0193:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantLeave
            if (r3 == 0) goto L_0x01d3
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
            if (r1 == 0) goto L_0x01c2
            r1 = 2131625512(0x7f0e0628, float:1.8878234E38)
            java.lang.String r2 = "EventLogLeftGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x01c2:
            r1 = 2131625511(0x7f0e0627, float:1.8878232E38)
            java.lang.String r2 = "EventLogLeftChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x01d3:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantInvite
            java.lang.String r13 = "un2"
            if (r3 == 0) goto L_0x0257
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
            if (r3 <= 0) goto L_0x0204
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r4 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r3 = r3.getUser(r4)
            goto L_0x0213
        L_0x0204:
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = -r1
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
        L_0x0213:
            org.telegram.tgnet.TLRPC$Message r4 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r4 = r4.from_id
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r5 == 0) goto L_0x0240
            long r4 = r4.user_id
            int r16 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r16 != 0) goto L_0x0240
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x0234
            r1 = 2131625507(0x7f0e0623, float:1.8878224E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r9, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x0234:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r12)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x0240:
            r1 = 2131625460(0x7f0e05f4, float:1.8878129E38)
            java.lang.String r2 = "EventLogAdded"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r13, r3)
            r6.messageText = r1
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x00c6
        L_0x0257:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin
            java.lang.String r9 = "%1$s"
            r11 = 32
            r12 = 10
            if (r3 != 0) goto L_0x1156
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan
            if (r3 == 0) goto L_0x0279
            r3 = r2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan) r3
            org.telegram.tgnet.TLRPC$ChannelParticipant r3 = r3.prev_participant
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantAdmin
            if (r3 == 0) goto L_0x0279
            r3 = r2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan) r3
            org.telegram.tgnet.TLRPC$ChannelParticipant r3 = r3.new_participant
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipant
            if (r3 == 0) goto L_0x0279
            goto L_0x1156
        L_0x0279:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights
            if (r3 == 0) goto L_0x040f
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights) r2
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message
            r1.<init>()
            r6.messageOwner = r1
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = r2.prev_banned_rights
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.new_banned_rights
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r4 = 2131625473(0x7f0e0601, float:1.8878155E38)
            java.lang.String r5 = "EventLogDefaultPermissions"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.<init>(r4)
            if (r1 != 0) goto L_0x029f
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r1 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r1.<init>()
        L_0x029f:
            if (r2 != 0) goto L_0x02a6
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r2.<init>()
        L_0x02a6:
            boolean r4 = r1.send_messages
            boolean r5 = r2.send_messages
            if (r4 == r5) goto L_0x02cf
            r3.append(r12)
            r3.append(r12)
            boolean r4 = r2.send_messages
            if (r4 != 0) goto L_0x02b9
            r4 = 43
            goto L_0x02bb
        L_0x02b9:
            r4 = 45
        L_0x02bb:
            r3.append(r4)
            r3.append(r11)
            r4 = 2131625549(0x7f0e064d, float:1.887831E38)
            java.lang.String r5 = "EventLogRestrictedSendMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r3.append(r4)
            r4 = 1
            goto L_0x02d0
        L_0x02cf:
            r4 = 0
        L_0x02d0:
            boolean r5 = r1.send_stickers
            boolean r9 = r2.send_stickers
            if (r5 != r9) goto L_0x02e8
            boolean r5 = r1.send_inline
            boolean r9 = r2.send_inline
            if (r5 != r9) goto L_0x02e8
            boolean r5 = r1.send_gifs
            boolean r9 = r2.send_gifs
            if (r5 != r9) goto L_0x02e8
            boolean r5 = r1.send_games
            boolean r9 = r2.send_games
            if (r5 == r9) goto L_0x030c
        L_0x02e8:
            if (r4 != 0) goto L_0x02ee
            r3.append(r12)
            r4 = 1
        L_0x02ee:
            r3.append(r12)
            boolean r5 = r2.send_stickers
            if (r5 != 0) goto L_0x02f8
            r5 = 43
            goto L_0x02fa
        L_0x02f8:
            r5 = 45
        L_0x02fa:
            r3.append(r5)
            r3.append(r11)
            r5 = 2131625551(0x7f0e064f, float:1.8878313E38)
            java.lang.String r9 = "EventLogRestrictedSendStickers"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x030c:
            boolean r5 = r1.send_media
            boolean r9 = r2.send_media
            if (r5 == r9) goto L_0x0336
            if (r4 != 0) goto L_0x0318
            r3.append(r12)
            r4 = 1
        L_0x0318:
            r3.append(r12)
            boolean r5 = r2.send_media
            if (r5 != 0) goto L_0x0322
            r5 = 43
            goto L_0x0324
        L_0x0322:
            r5 = 45
        L_0x0324:
            r3.append(r5)
            r3.append(r11)
            r5 = 2131625548(0x7f0e064c, float:1.8878307E38)
            java.lang.String r9 = "EventLogRestrictedSendMedia"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x0336:
            boolean r5 = r1.send_polls
            boolean r9 = r2.send_polls
            if (r5 == r9) goto L_0x0360
            if (r4 != 0) goto L_0x0342
            r3.append(r12)
            r4 = 1
        L_0x0342:
            r3.append(r12)
            boolean r5 = r2.send_polls
            if (r5 != 0) goto L_0x034c
            r5 = 43
            goto L_0x034e
        L_0x034c:
            r5 = 45
        L_0x034e:
            r3.append(r5)
            r3.append(r11)
            r5 = 2131625550(0x7f0e064e, float:1.8878311E38)
            java.lang.String r9 = "EventLogRestrictedSendPolls"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x0360:
            boolean r5 = r1.embed_links
            boolean r9 = r2.embed_links
            if (r5 == r9) goto L_0x038a
            if (r4 != 0) goto L_0x036c
            r3.append(r12)
            r4 = 1
        L_0x036c:
            r3.append(r12)
            boolean r5 = r2.embed_links
            if (r5 != 0) goto L_0x0376
            r5 = 43
            goto L_0x0378
        L_0x0376:
            r5 = 45
        L_0x0378:
            r3.append(r5)
            r3.append(r11)
            r5 = 2131625547(0x7f0e064b, float:1.8878305E38)
            java.lang.String r9 = "EventLogRestrictedSendEmbed"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x038a:
            boolean r5 = r1.change_info
            boolean r9 = r2.change_info
            if (r5 == r9) goto L_0x03b4
            if (r4 != 0) goto L_0x0396
            r3.append(r12)
            r4 = 1
        L_0x0396:
            r3.append(r12)
            boolean r5 = r2.change_info
            if (r5 != 0) goto L_0x03a0
            r5 = 43
            goto L_0x03a2
        L_0x03a0:
            r5 = 45
        L_0x03a2:
            r3.append(r5)
            r3.append(r11)
            r5 = 2131625543(0x7f0e0647, float:1.8878297E38)
            java.lang.String r9 = "EventLogRestrictedChangeInfo"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x03b4:
            boolean r5 = r1.invite_users
            boolean r9 = r2.invite_users
            if (r5 == r9) goto L_0x03de
            if (r4 != 0) goto L_0x03c0
            r3.append(r12)
            r4 = 1
        L_0x03c0:
            r3.append(r12)
            boolean r5 = r2.invite_users
            if (r5 != 0) goto L_0x03ca
            r5 = 43
            goto L_0x03cc
        L_0x03ca:
            r5 = 45
        L_0x03cc:
            r3.append(r5)
            r3.append(r11)
            r5 = 2131625544(0x7f0e0648, float:1.8878299E38)
            java.lang.String r9 = "EventLogRestrictedInviteUsers"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r9, r5)
            r3.append(r5)
        L_0x03de:
            boolean r1 = r1.pin_messages
            boolean r5 = r2.pin_messages
            if (r1 == r5) goto L_0x0407
            if (r4 != 0) goto L_0x03e9
            r3.append(r12)
        L_0x03e9:
            r3.append(r12)
            boolean r1 = r2.pin_messages
            if (r1 != 0) goto L_0x03f3
            r1 = 43
            goto L_0x03f5
        L_0x03f3:
            r1 = 45
        L_0x03f5:
            r3.append(r1)
            r3.append(r11)
            r1 = 2131625545(0x7f0e0649, float:1.88783E38)
            java.lang.String r2 = "EventLogRestrictedPinMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r3.append(r1)
        L_0x0407:
            java.lang.String r1 = r3.toString()
            r6.messageText = r1
            goto L_0x00c6
        L_0x040f:
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan
            java.lang.String r11 = ", "
            java.lang.String r12 = "Hours"
            java.lang.String r4 = "Minutes"
            if (r3 == 0) goto L_0x0725
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan) r2
            org.telegram.tgnet.TLRPC$TL_message r1 = new org.telegram.tgnet.TLRPC$TL_message
            r1.<init>()
            r6.messageOwner = r1
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = r2.prev_participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r5 = getPeerId(r1)
            r22 = 0
            int r1 = (r5 > r22 ? 1 : (r5 == r22 ? 0 : -1))
            if (r1 <= 0) goto L_0x0444
            r22 = r5
            r6 = r27
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r5 = java.lang.Long.valueOf(r22)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r5)
            r5 = r4
            goto L_0x045a
        L_0x0444:
            r22 = r5
            r6 = r27
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            r5 = r4
            r3 = r22
            long r3 = -r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)
        L_0x045a:
            org.telegram.tgnet.TLRPC$ChannelParticipant r3 = r2.prev_participant
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = r3.banned_rights
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r2.new_participant
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = r2.banned_rights
            boolean r4 = r0.megagroup
            if (r4 == 0) goto L_0x06ee
            if (r2 == 0) goto L_0x0474
            boolean r4 = r2.view_messages
            if (r4 == 0) goto L_0x0474
            if (r3 == 0) goto L_0x06ee
            int r4 = r2.until_date
            int r15 = r3.until_date
            if (r4 == r15) goto L_0x06ee
        L_0x0474:
            if (r2 == 0) goto L_0x04eb
            boolean r4 = org.telegram.messenger.AndroidUtilities.isBannedForever(r2)
            if (r4 != 0) goto L_0x04eb
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            int r15 = r2.until_date
            int r13 = r7.date
            int r15 = r15 - r13
            int r13 = r15 / 60
            r22 = 60
            int r13 = r13 / 60
            int r13 = r13 / 24
            int r23 = r13 * 60
            int r23 = r23 * 60
            int r23 = r23 * 24
            int r15 = r15 - r23
            int r23 = r15 / 60
            int r8 = r23 / 60
            int r23 = r8 * 60
            int r23 = r23 * 60
            int r15 = r15 - r23
            int r15 = r15 / 60
            r24 = r14
            r7 = 3
            r14 = 0
            r17 = 0
        L_0x04a8:
            if (r14 >= r7) goto L_0x04fb
            if (r14 != 0) goto L_0x04b7
            if (r13 == 0) goto L_0x04c8
            java.lang.String r7 = "Days"
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r7, r13)
        L_0x04b4:
            int r17 = r17 + 1
            goto L_0x04c9
        L_0x04b7:
            r7 = 1
            if (r14 != r7) goto L_0x04c1
            if (r8 == 0) goto L_0x04c8
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r12, r8)
            goto L_0x04b4
        L_0x04c1:
            if (r15 == 0) goto L_0x04c8
            java.lang.String r7 = org.telegram.messenger.LocaleController.formatPluralString(r5, r15)
            goto L_0x04b4
        L_0x04c8:
            r7 = 0
        L_0x04c9:
            r26 = r17
            r17 = r8
            r8 = r26
            if (r7 == 0) goto L_0x04dd
            int r23 = r4.length()
            if (r23 <= 0) goto L_0x04da
            r4.append(r11)
        L_0x04da:
            r4.append(r7)
        L_0x04dd:
            r7 = 2
            if (r8 != r7) goto L_0x04e1
            goto L_0x04fb
        L_0x04e1:
            int r14 = r14 + 1
            r7 = 3
            r26 = r17
            r17 = r8
            r8 = r26
            goto L_0x04a8
        L_0x04eb:
            r24 = r14
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r5 = 2131628393(0x7f0e1169, float:1.8884077E38)
            java.lang.String r7 = "UserRestrictionsUntilForever"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r4.<init>(r5)
        L_0x04fb:
            r5 = 2131625552(0x7f0e0650, float:1.8878315E38)
            java.lang.String r7 = "EventLogRestrictedUntil"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            int r7 = r5.indexOf(r9)
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r9 = 2
            java.lang.Object[] r9 = new java.lang.Object[r9]
            org.telegram.tgnet.TLRPC$Message r11 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r11 = r11.entities
            java.lang.String r1 = r6.getUserName(r1, r11, r7)
            r7 = 0
            r9[r7] = r1
            java.lang.String r1 = r4.toString()
            r4 = 1
            r9[r4] = r1
            java.lang.String r1 = java.lang.String.format(r5, r9)
            r8.<init>(r1)
            if (r3 != 0) goto L_0x052d
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r3 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r3.<init>()
        L_0x052d:
            if (r2 != 0) goto L_0x0534
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r2 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r2.<init>()
        L_0x0534:
            boolean r1 = r3.view_messages
            boolean r4 = r2.view_messages
            if (r1 == r4) goto L_0x0561
            r1 = 10
            r8.append(r1)
            r8.append(r1)
            boolean r1 = r2.view_messages
            if (r1 != 0) goto L_0x0549
            r1 = 43
            goto L_0x054b
        L_0x0549:
            r1 = 45
        L_0x054b:
            r8.append(r1)
            r1 = 32
            r8.append(r1)
            r1 = 2131625546(0x7f0e064a, float:1.8878303E38)
            java.lang.String r4 = "EventLogRestrictedReadMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r4, r1)
            r8.append(r1)
            r1 = 1
            goto L_0x0562
        L_0x0561:
            r1 = 0
        L_0x0562:
            boolean r4 = r3.send_messages
            boolean r5 = r2.send_messages
            if (r4 == r5) goto L_0x0590
            r4 = 10
            if (r1 != 0) goto L_0x0570
            r8.append(r4)
            r1 = 1
        L_0x0570:
            r8.append(r4)
            boolean r4 = r2.send_messages
            if (r4 != 0) goto L_0x057a
            r4 = 43
            goto L_0x057c
        L_0x057a:
            r4 = 45
        L_0x057c:
            r8.append(r4)
            r4 = 32
            r8.append(r4)
            r4 = 2131625549(0x7f0e064d, float:1.887831E38)
            java.lang.String r5 = "EventLogRestrictedSendMessages"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r8.append(r4)
        L_0x0590:
            boolean r4 = r3.send_stickers
            boolean r5 = r2.send_stickers
            if (r4 != r5) goto L_0x05a8
            boolean r4 = r3.send_inline
            boolean r5 = r2.send_inline
            if (r4 != r5) goto L_0x05a8
            boolean r4 = r3.send_gifs
            boolean r5 = r2.send_gifs
            if (r4 != r5) goto L_0x05a8
            boolean r4 = r3.send_games
            boolean r5 = r2.send_games
            if (r4 == r5) goto L_0x05d0
        L_0x05a8:
            r4 = 10
            if (r1 != 0) goto L_0x05b0
            r8.append(r4)
            r1 = 1
        L_0x05b0:
            r8.append(r4)
            boolean r4 = r2.send_stickers
            if (r4 != 0) goto L_0x05ba
            r4 = 43
            goto L_0x05bc
        L_0x05ba:
            r4 = 45
        L_0x05bc:
            r8.append(r4)
            r4 = 32
            r8.append(r4)
            r4 = 2131625551(0x7f0e064f, float:1.8878313E38)
            java.lang.String r5 = "EventLogRestrictedSendStickers"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r8.append(r4)
        L_0x05d0:
            boolean r4 = r3.send_media
            boolean r5 = r2.send_media
            if (r4 == r5) goto L_0x05fe
            r4 = 10
            if (r1 != 0) goto L_0x05de
            r8.append(r4)
            r1 = 1
        L_0x05de:
            r8.append(r4)
            boolean r4 = r2.send_media
            if (r4 != 0) goto L_0x05e8
            r4 = 43
            goto L_0x05ea
        L_0x05e8:
            r4 = 45
        L_0x05ea:
            r8.append(r4)
            r4 = 32
            r8.append(r4)
            r4 = 2131625548(0x7f0e064c, float:1.8878307E38)
            java.lang.String r5 = "EventLogRestrictedSendMedia"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r8.append(r4)
        L_0x05fe:
            boolean r4 = r3.send_polls
            boolean r5 = r2.send_polls
            if (r4 == r5) goto L_0x062c
            r4 = 10
            if (r1 != 0) goto L_0x060c
            r8.append(r4)
            r1 = 1
        L_0x060c:
            r8.append(r4)
            boolean r4 = r2.send_polls
            if (r4 != 0) goto L_0x0616
            r4 = 43
            goto L_0x0618
        L_0x0616:
            r4 = 45
        L_0x0618:
            r8.append(r4)
            r4 = 32
            r8.append(r4)
            r4 = 2131625550(0x7f0e064e, float:1.8878311E38)
            java.lang.String r5 = "EventLogRestrictedSendPolls"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r8.append(r4)
        L_0x062c:
            boolean r4 = r3.embed_links
            boolean r5 = r2.embed_links
            if (r4 == r5) goto L_0x065a
            r4 = 10
            if (r1 != 0) goto L_0x063a
            r8.append(r4)
            r1 = 1
        L_0x063a:
            r8.append(r4)
            boolean r4 = r2.embed_links
            if (r4 != 0) goto L_0x0644
            r4 = 43
            goto L_0x0646
        L_0x0644:
            r4 = 45
        L_0x0646:
            r8.append(r4)
            r4 = 32
            r8.append(r4)
            r4 = 2131625547(0x7f0e064b, float:1.8878305E38)
            java.lang.String r5 = "EventLogRestrictedSendEmbed"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r8.append(r4)
        L_0x065a:
            boolean r4 = r3.change_info
            boolean r5 = r2.change_info
            if (r4 == r5) goto L_0x0688
            r4 = 10
            if (r1 != 0) goto L_0x0668
            r8.append(r4)
            r1 = 1
        L_0x0668:
            r8.append(r4)
            boolean r4 = r2.change_info
            if (r4 != 0) goto L_0x0672
            r4 = 43
            goto L_0x0674
        L_0x0672:
            r4 = 45
        L_0x0674:
            r8.append(r4)
            r4 = 32
            r8.append(r4)
            r4 = 2131625543(0x7f0e0647, float:1.8878297E38)
            java.lang.String r5 = "EventLogRestrictedChangeInfo"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r8.append(r4)
        L_0x0688:
            boolean r4 = r3.invite_users
            boolean r5 = r2.invite_users
            if (r4 == r5) goto L_0x06b6
            r4 = 10
            if (r1 != 0) goto L_0x0696
            r8.append(r4)
            r1 = 1
        L_0x0696:
            r8.append(r4)
            boolean r4 = r2.invite_users
            if (r4 != 0) goto L_0x06a0
            r4 = 43
            goto L_0x06a2
        L_0x06a0:
            r4 = 45
        L_0x06a2:
            r8.append(r4)
            r4 = 32
            r8.append(r4)
            r4 = 2131625544(0x7f0e0648, float:1.8878299E38)
            java.lang.String r5 = "EventLogRestrictedInviteUsers"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r8.append(r4)
        L_0x06b6:
            boolean r3 = r3.pin_messages
            boolean r4 = r2.pin_messages
            if (r3 == r4) goto L_0x06e6
            if (r1 != 0) goto L_0x06c4
            r1 = 10
            r8.append(r1)
            goto L_0x06c6
        L_0x06c4:
            r1 = 10
        L_0x06c6:
            r8.append(r1)
            boolean r1 = r2.pin_messages
            if (r1 != 0) goto L_0x06d0
            r11 = 43
            goto L_0x06d2
        L_0x06d0:
            r11 = 45
        L_0x06d2:
            r8.append(r11)
            r1 = 32
            r8.append(r1)
            r1 = 2131625545(0x7f0e0649, float:1.88783E38)
            java.lang.String r2 = "EventLogRestrictedPinMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r8.append(r1)
        L_0x06e6:
            java.lang.String r1 = r8.toString()
            r6.messageText = r1
            goto L_0x0814
        L_0x06ee:
            r24 = r14
            if (r2 == 0) goto L_0x0702
            if (r3 == 0) goto L_0x06f8
            boolean r2 = r2.view_messages
            if (r2 == 0) goto L_0x0702
        L_0x06f8:
            r2 = 2131625471(0x7f0e05ff, float:1.887815E38)
            java.lang.String r3 = "EventLogChannelRestricted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            goto L_0x070b
        L_0x0702:
            r2 = 2131625472(0x7f0e0600, float:1.8878153E38)
            java.lang.String r3 = "EventLogChannelUnrestricted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
        L_0x070b:
            int r3 = r2.indexOf(r9)
            r4 = 1
            java.lang.Object[] r5 = new java.lang.Object[r4]
            org.telegram.tgnet.TLRPC$Message r4 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r4 = r4.entities
            java.lang.String r1 = r6.getUserName(r1, r4, r3)
            r3 = 0
            r5[r3] = r1
            java.lang.String r1 = java.lang.String.format(r2, r5)
            r6.messageText = r1
            goto L_0x0814
        L_0x0725:
            r5 = r4
            r24 = r14
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionUpdatePinned
            if (r4 == 0) goto L_0x07b0
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionUpdatePinned r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionUpdatePinned) r2
            org.telegram.tgnet.TLRPC$Message r1 = r2.message
            if (r10 == 0) goto L_0x0787
            long r3 = r10.id
            r7 = 136817688(0x827aCLASSNAME, double:6.75969194E-316)
            int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x0787
            org.telegram.tgnet.TLRPC$MessageFwdHeader r3 = r1.fwd_from
            if (r3 == 0) goto L_0x0787
            org.telegram.tgnet.TLRPC$Peer r3 = r3.from_id
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r3 == 0) goto L_0x0787
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            org.telegram.tgnet.TLRPC$Message r4 = r2.message
            org.telegram.tgnet.TLRPC$MessageFwdHeader r4 = r4.fwd_from
            org.telegram.tgnet.TLRPC$Peer r4 = r4.from_id
            long r4 = r4.channel_id
            java.lang.Long r4 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$Chat r3 = r3.getChat(r4)
            org.telegram.tgnet.TLRPC$Message r2 = r2.message
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r4 != 0) goto L_0x0777
            boolean r2 = r2.pinned
            if (r2 != 0) goto L_0x0766
            goto L_0x0777
        L_0x0766:
            r2 = 2131625516(0x7f0e062c, float:1.8878242E38)
            java.lang.String r4 = "EventLogPinnedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r3)
            r6.messageText = r2
            goto L_0x07e5
        L_0x0777:
            r2 = 2131625567(0x7f0e065f, float:1.8878346E38)
            java.lang.String r4 = "EventLogUnpinnedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r4, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r3)
            r6.messageText = r2
            goto L_0x07e5
        L_0x0787:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r2 != 0) goto L_0x07a0
            boolean r2 = r1.pinned
            if (r2 != 0) goto L_0x0790
            goto L_0x07a0
        L_0x0790:
            r2 = 2131625516(0x7f0e062c, float:1.8878242E38)
            java.lang.String r3 = "EventLogPinnedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            goto L_0x07e5
        L_0x07a0:
            r2 = 2131625567(0x7f0e065f, float:1.8878346E38)
            java.lang.String r3 = "EventLogUnpinnedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            goto L_0x07e5
        L_0x07b0:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionStopPoll
            if (r4 == 0) goto L_0x07eb
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionStopPoll r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionStopPoll) r2
            org.telegram.tgnet.TLRPC$Message r1 = r2.message
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media
            boolean r3 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r3 == 0) goto L_0x07d6
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$Poll r2 = r2.poll
            boolean r2 = r2.quiz
            if (r2 == 0) goto L_0x07d6
            r2 = 2131625558(0x7f0e0656, float:1.8878327E38)
            java.lang.String r3 = "EventLogStopQuiz"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            goto L_0x07e5
        L_0x07d6:
            r2 = 2131625557(0x7f0e0655, float:1.8878325E38)
            java.lang.String r3 = "EventLogStopPoll"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
        L_0x07e5:
            r8 = r29
            r7 = r24
            goto L_0x1419
        L_0x07eb:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures
            if (r4 == 0) goto L_0x081a
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures) r2
            boolean r1 = r2.new_value
            if (r1 == 0) goto L_0x0805
            r1 = 2131625564(0x7f0e065c, float:1.887834E38)
            java.lang.String r2 = "EventLogToggledSignaturesOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0814
        L_0x0805:
            r1 = 2131625563(0x7f0e065b, float:1.8878337E38)
            java.lang.String r2 = "EventLogToggledSignaturesOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
        L_0x0814:
            r8 = r29
            r7 = r24
            goto L_0x1418
        L_0x081a:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites
            if (r4 == 0) goto L_0x0844
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites) r2
            boolean r1 = r2.new_value
            if (r1 == 0) goto L_0x0834
            r1 = 2131625562(0x7f0e065a, float:1.8878335E38)
            java.lang.String r2 = "EventLogToggledInvitesOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0814
        L_0x0834:
            r1 = 2131625561(0x7f0e0659, float:1.8878333E38)
            java.lang.String r2 = "EventLogToggledInvitesOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0814
        L_0x0844:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDeleteMessage
            if (r4 == 0) goto L_0x085c
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDeleteMessage r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDeleteMessage) r2
            org.telegram.tgnet.TLRPC$Message r1 = r2.message
            r2 = 2131625474(0x7f0e0602, float:1.8878157E38)
            java.lang.String r3 = "EventLogDeletedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            goto L_0x07e5
        L_0x085c:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat
            if (r4 == 0) goto L_0x090d
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r1 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat) r1
            long r3 = r1.new_value
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat) r2
            long r1 = r2.prev_value
            boolean r5 = r0.megagroup
            if (r5 == 0) goto L_0x08bd
            r7 = 0
            int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x0898
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            r2 = 2131625538(0x7f0e0642, float:1.8878287E38)
            java.lang.String r3 = "EventLogRemovedLinkedChannel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r13, r1)
            r6.messageText = r1
            goto L_0x0814
        L_0x0898:
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r2 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            r2 = 2131625465(0x7f0e05f9, float:1.8878139E38)
            java.lang.String r3 = "EventLogChangedLinkedChannel"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r13, r1)
            r6.messageText = r1
            goto L_0x0814
        L_0x08bd:
            r7 = 0
            int r5 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r5 != 0) goto L_0x08e8
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
            r2 = 2131625539(0x7f0e0643, float:1.8878289E38)
            java.lang.String r3 = "EventLogRemovedLinkedGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r13, r1)
            r6.messageText = r1
            goto L_0x0814
        L_0x08e8:
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r2 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r2)
            r2 = 2131625466(0x7f0e05fa, float:1.887814E38)
            java.lang.String r3 = "EventLogChangedLinkedGroup"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r13, r1)
            r6.messageText = r1
            goto L_0x0814
        L_0x090d:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden
            if (r4 == 0) goto L_0x0939
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden) r2
            boolean r1 = r2.new_value
            if (r1 == 0) goto L_0x0928
            r1 = 2131625559(0x7f0e0657, float:1.887833E38)
            java.lang.String r2 = "EventLogToggledInvitesHistoryOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0814
        L_0x0928:
            r1 = 2131625560(0x7f0e0658, float:1.8878331E38)
            java.lang.String r2 = "EventLogToggledInvitesHistoryOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0814
        L_0x0939:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout
            if (r4 == 0) goto L_0x09cb
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x0947
            r2 = 2131625481(0x7f0e0609, float:1.8878171E38)
            java.lang.String r3 = "EventLogEditedGroupDescription"
            goto L_0x094c
        L_0x0947:
            r2 = 2131625476(0x7f0e0604, float:1.8878161E38)
            java.lang.String r3 = "EventLogEditedChannelDescription"
        L_0x094c:
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
            r7 = r29
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
            if (r1 != 0) goto L_0x09ba
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
            r8 = r24
            r1.display_url = r8
            r1.url = r8
            r3 = 2131625517(0x7f0e062d, float:1.8878244E38)
            java.lang.String r4 = "EventLogPreviousGroupDescription"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.site_name = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r3 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout) r3
            java.lang.String r3 = r3.prev_value
            r1.description = r3
            goto L_0x09c3
        L_0x09ba:
            r8 = r24
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r2.media = r1
        L_0x09c3:
            r1 = r2
        L_0x09c4:
            r26 = r8
            r8 = r7
            r7 = r26
            goto L_0x1419
        L_0x09cb:
            r7 = r29
            r8 = r24
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme
            if (r4 == 0) goto L_0x0a56
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x09dd
            r2 = 2131625483(0x7f0e060b, float:1.8878175E38)
            java.lang.String r3 = "EventLogEditedGroupTheme"
            goto L_0x09e2
        L_0x09dd:
            r2 = 2131625478(0x7f0e0606, float:1.8878165E38)
            java.lang.String r3 = "EventLogEditedChannelTheme"
        L_0x09e2:
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
            if (r1 != 0) goto L_0x0a4d
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
            r3 = 2131625518(0x7f0e062e, float:1.8878246E38)
            java.lang.String r4 = "EventLogPreviousGroupTheme"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            r1.site_name = r3
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r3 = r7.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme r3 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTheme) r3
            java.lang.String r3 = r3.prev_value
            r1.description = r3
            goto L_0x09c3
        L_0x0a4d:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r2.media = r1
            goto L_0x09c3
        L_0x0a56:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername
            if (r4 == 0) goto L_0x0b5c
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername) r2
            java.lang.String r2 = r2.new_value
            boolean r3 = android.text.TextUtils.isEmpty(r2)
            if (r3 != 0) goto L_0x0a7e
            boolean r3 = r0.megagroup
            if (r3 == 0) goto L_0x0a6e
            r3 = 2131625464(0x7f0e05f8, float:1.8878137E38)
            java.lang.String r4 = "EventLogChangedGroupLink"
            goto L_0x0a73
        L_0x0a6e:
            r3 = 2131625463(0x7f0e05f7, float:1.8878135E38)
            java.lang.String r4 = "EventLogChangedChannelLink"
        L_0x0a73:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.CharSequence r3 = replaceWithLink(r3, r15, r10)
            r6.messageText = r3
            goto L_0x0a97
        L_0x0a7e:
            boolean r3 = r0.megagroup
            if (r3 == 0) goto L_0x0a88
            r3 = 2131625537(0x7f0e0641, float:1.8878285E38)
            java.lang.String r4 = "EventLogRemovedGroupLink"
            goto L_0x0a8d
        L_0x0a88:
            r3 = 2131625535(0x7f0e063f, float:1.887828E38)
            java.lang.String r4 = "EventLogRemovedChannelLink"
        L_0x0a8d:
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r4, r3)
            java.lang.CharSequence r3 = replaceWithLink(r3, r15, r10)
            r6.messageText = r3
        L_0x0a97:
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
            if (r1 != 0) goto L_0x0adc
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
            goto L_0x0ade
        L_0x0adc:
            r3.message = r8
        L_0x0ade:
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
            if (r1 != 0) goto L_0x0b52
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
            r2 = 2131625519(0x7f0e062f, float:1.8878248E38)
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
            goto L_0x0b59
        L_0x0b52:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r1 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r1.<init>()
            r3.media = r1
        L_0x0b59:
            r1 = r3
            goto L_0x09c4
        L_0x0b5c:
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage
            if (r4 == 0) goto L_0x0cd3
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
            if (r3 == 0) goto L_0x0b84
            org.telegram.tgnet.TLRPC$Peer r4 = r3.from_id
            if (r4 == 0) goto L_0x0b84
            r2.from_id = r4
            goto L_0x0b8f
        L_0x0b84:
            org.telegram.tgnet.TLRPC$TL_peerUser r4 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r4.<init>()
            r2.from_id = r4
            long r11 = r7.user_id
            r4.user_id = r11
        L_0x0b8f:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r3.media
            if (r4 == 0) goto L_0x0CLASSNAME
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            if (r5 != 0) goto L_0x0CLASSNAME
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r4 != 0) goto L_0x0CLASSNAME
            java.lang.String r4 = r3.message
            java.lang.String r5 = r1.message
            boolean r4 = android.text.TextUtils.equals(r4, r5)
            r5 = 1
            r4 = r4 ^ r5
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media
            java.lang.Class r5 = r5.getClass()
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r1.media
            java.lang.Class r9 = r9.getClass()
            if (r5 != r9) goto L_0x0be1
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media
            org.telegram.tgnet.TLRPC$Photo r9 = r5.photo
            if (r9 == 0) goto L_0x0bca
            org.telegram.tgnet.TLRPC$MessageMedia r11 = r1.media
            org.telegram.tgnet.TLRPC$Photo r11 = r11.photo
            if (r11 == 0) goto L_0x0bca
            long r12 = r9.id
            r24 = r8
            long r7 = r11.id
            int r9 = (r12 > r7 ? 1 : (r12 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0be3
            goto L_0x0bcc
        L_0x0bca:
            r24 = r8
        L_0x0bcc:
            org.telegram.tgnet.TLRPC$Document r5 = r5.document
            if (r5 == 0) goto L_0x0bdf
            org.telegram.tgnet.TLRPC$MessageMedia r7 = r1.media
            org.telegram.tgnet.TLRPC$Document r7 = r7.document
            if (r7 == 0) goto L_0x0bdf
            long r8 = r5.id
            long r11 = r7.id
            int r5 = (r8 > r11 ? 1 : (r8 == r11 ? 0 : -1))
            if (r5 == 0) goto L_0x0bdf
            goto L_0x0be3
        L_0x0bdf:
            r5 = 0
            goto L_0x0be4
        L_0x0be1:
            r24 = r8
        L_0x0be3:
            r5 = 1
        L_0x0be4:
            if (r5 == 0) goto L_0x0bf8
            if (r4 == 0) goto L_0x0bf8
            r5 = 2131625487(0x7f0e060f, float:1.8878183E38)
            java.lang.String r7 = "EventLogEditedMediaCaption"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = replaceWithLink(r5, r15, r10)
            r6.messageText = r5
            goto L_0x0CLASSNAME
        L_0x0bf8:
            if (r4 == 0) goto L_0x0c0a
            r5 = 2131625475(0x7f0e0603, float:1.887816E38)
            java.lang.String r7 = "EventLogEditedCaption"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = replaceWithLink(r5, r15, r10)
            r6.messageText = r5
            goto L_0x0CLASSNAME
        L_0x0c0a:
            r5 = 2131625486(0x7f0e060e, float:1.8878181E38)
            java.lang.String r7 = "EventLogEditedMedia"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            java.lang.CharSequence r5 = replaceWithLink(r5, r15, r10)
            r6.messageText = r5
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$MessageMedia r5 = r3.media
            r2.media = r5
            if (r4 == 0) goto L_0x0cb7
            org.telegram.tgnet.TLRPC$TL_webPage r4 = new org.telegram.tgnet.TLRPC$TL_webPage
            r4.<init>()
            r5.webpage = r4
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r5 = 2131625513(0x7f0e0629, float:1.8878236E38)
            java.lang.String r7 = "EventLogOriginalCaption"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r4.site_name = r5
            java.lang.String r4 = r1.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x0c4d
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r4 = 2131625514(0x7f0e062a, float:1.8878238E38)
            java.lang.String r5 = "EventLogOriginalCaptionEmpty"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.description = r4
            goto L_0x0cb7
        L_0x0c4d:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            java.lang.String r1 = r1.message
            r4.description = r1
            goto L_0x0cb7
        L_0x0CLASSNAME:
            r24 = r8
            r4 = 2131625488(0x7f0e0610, float:1.8878185E38)
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
            r1 = r3
            goto L_0x0cb8
        L_0x0CLASSNAME:
            java.lang.String r4 = r3.message
            r2.message = r4
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r4.<init>()
            r2.media = r4
            org.telegram.tgnet.TLRPC$TL_webPage r5 = new org.telegram.tgnet.TLRPC$TL_webPage
            r5.<init>()
            r4.webpage = r5
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            r5 = 2131625515(0x7f0e062b, float:1.887824E38)
            java.lang.String r7 = "EventLogOriginalMessages"
            java.lang.String r5 = org.telegram.messenger.LocaleController.getString(r7, r5)
            r4.site_name = r5
            java.lang.String r4 = r1.message
            boolean r4 = android.text.TextUtils.isEmpty(r4)
            if (r4 == 0) goto L_0x0caf
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            r4 = 2131625514(0x7f0e062a, float:1.8878238E38)
            java.lang.String r5 = "EventLogOriginalCaptionEmpty"
            java.lang.String r4 = org.telegram.messenger.LocaleController.getString(r5, r4)
            r1.description = r4
            goto L_0x0cb7
        L_0x0caf:
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            java.lang.String r1 = r1.message
            r4.description = r1
        L_0x0cb7:
            r1 = r2
        L_0x0cb8:
            org.telegram.tgnet.TLRPC$ReplyMarkup r2 = r3.reply_markup
            r1.reply_markup = r2
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            if (r2 == 0) goto L_0x0ccd
            r3 = 10
            r2.flags = r3
            r7 = r24
            r2.display_url = r7
            r2.url = r7
            goto L_0x0ccf
        L_0x0ccd:
            r7 = r24
        L_0x0ccf:
            r8 = r29
            goto L_0x1419
        L_0x0cd3:
            r7 = r8
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet
            if (r1 == 0) goto L_0x0d0b
            r1 = r2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r1 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet) r1
            org.telegram.tgnet.TLRPC$InputStickerSet r1 = r1.new_stickerset
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet) r2
            org.telegram.tgnet.TLRPC$InputStickerSet r2 = r2.new_stickerset
            if (r1 == 0) goto L_0x0cf8
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            if (r1 == 0) goto L_0x0ce8
            goto L_0x0cf8
        L_0x0ce8:
            r1 = 2131625469(0x7f0e05fd, float:1.8878147E38)
            java.lang.String r2 = "EventLogChangedStickersSet"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0cf8:
            r1 = 2131625541(0x7f0e0645, float:1.8878293E38)
            java.lang.String r2 = "EventLogRemovedStickersSet"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
        L_0x0d07:
            r8 = r29
            goto L_0x1418
        L_0x0d0b:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation
            if (r1 == 0) goto L_0x0d41
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation) r2
            org.telegram.tgnet.TLRPC$ChannelLocation r1 = r2.new_value
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelLocationEmpty
            if (r2 == 0) goto L_0x0d27
            r1 = 2131625540(0x7f0e0644, float:1.887829E38)
            java.lang.String r2 = "EventLogRemovedLocation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0d27:
            org.telegram.tgnet.TLRPC$TL_channelLocation r1 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r1
            r2 = 2131625467(0x7f0e05fb, float:1.8878143E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = r1.address
            r3 = 0
            r4[r3] = r1
            java.lang.String r1 = "EventLogChangedLocation"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0d41:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode
            r4 = 3600(0xe10, float:5.045E-42)
            if (r1 == 0) goto L_0x0d8d
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode) r2
            int r1 = r2.new_value
            if (r1 != 0) goto L_0x0d5d
            r1 = 2131625565(0x7f0e065d, float:1.8878342E38)
            java.lang.String r2 = "EventLogToggledSlowmodeOff"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0d5d:
            r2 = 60
            if (r1 >= r2) goto L_0x0d68
            java.lang.String r2 = "Seconds"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            goto L_0x0d76
        L_0x0d68:
            if (r1 >= r4) goto L_0x0d70
            int r1 = r1 / r2
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1)
            goto L_0x0d76
        L_0x0d70:
            int r1 = r1 / r2
            int r1 = r1 / r2
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1)
        L_0x0d76:
            r2 = 2131625566(0x7f0e065e, float:1.8878344E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r3 = 0
            r4[r3] = r1
            java.lang.String r1 = "EventLogToggledSlowmodeOn"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0d8d:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionStartGroupCall
            if (r1 == 0) goto L_0x0dc1
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r32)
            if (r1 == 0) goto L_0x0db0
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x0d9f
            boolean r1 = r0.gigagroup
            if (r1 == 0) goto L_0x0db0
        L_0x0d9f:
            r1 = 2131625555(0x7f0e0653, float:1.8878321E38)
            java.lang.String r2 = "EventLogStartedLiveStream"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0db0:
            r1 = 2131625556(0x7f0e0654, float:1.8878323E38)
            java.lang.String r2 = "EventLogStartedVoiceChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0dc1:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDiscardGroupCall
            if (r1 == 0) goto L_0x0df5
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r32)
            if (r1 == 0) goto L_0x0de4
            boolean r1 = r0.megagroup
            if (r1 == 0) goto L_0x0dd3
            boolean r1 = r0.gigagroup
            if (r1 == 0) goto L_0x0de4
        L_0x0dd3:
            r1 = 2131625493(0x7f0e0615, float:1.8878196E38)
            java.lang.String r2 = "EventLogEndedLiveStream"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0de4:
            r1 = 2131625494(0x7f0e0616, float:1.8878198E38)
            java.lang.String r2 = "EventLogEndedVoiceChat"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0df5:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantMute
            if (r1 == 0) goto L_0x0e3e
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantMute r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantMute) r2
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r1 = getPeerId(r1)
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x0e18
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r3.getUser(r1)
            goto L_0x0e27
        L_0x0e18:
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r1 = -r1
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
        L_0x0e27:
            r2 = 2131625569(0x7f0e0661, float:1.887835E38)
            java.lang.String r3 = "EventLogVoiceChatMuted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r13, r1)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0e3e:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantUnmute
            if (r1 == 0) goto L_0x0e87
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantUnmute r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantUnmute) r2
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r1 = getPeerId(r1)
            r3 = 0
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 <= 0) goto L_0x0e61
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$User r1 = r3.getUser(r1)
            goto L_0x0e70
        L_0x0e61:
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r1 = -r1
            java.lang.Long r1 = java.lang.Long.valueOf(r1)
            org.telegram.tgnet.TLRPC$Chat r1 = r3.getChat(r1)
        L_0x0e70:
            r2 = 2131625571(0x7f0e0663, float:1.8878354E38)
            java.lang.String r3 = "EventLogVoiceChatUnmuted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r13, r1)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0e87:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleGroupCallSetting
            if (r1 == 0) goto L_0x0eb3
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleGroupCallSetting r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleGroupCallSetting) r2
            boolean r1 = r2.join_muted
            if (r1 == 0) goto L_0x0ea2
            r1 = 2131625570(0x7f0e0662, float:1.8878352E38)
            java.lang.String r2 = "EventLogVoiceChatNotAllowedToSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0ea2:
            r1 = 2131625568(0x7f0e0660, float:1.8878348E38)
            java.lang.String r2 = "EventLogVoiceChatAllowedToSpeak"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0eb3:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByInvite
            if (r1 == 0) goto L_0x0eca
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByInvite r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByInvite) r2
            r1 = 2131624142(0x7f0e00ce, float:1.8875455E38)
            java.lang.String r2 = "ActionInviteUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0eca:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleNoForwards
            if (r1 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleNoForwards r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleNoForwards) r2
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r32)
            if (r1 == 0) goto L_0x0edc
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x0edc
            r1 = 1
            goto L_0x0edd
        L_0x0edc:
            r1 = 0
        L_0x0edd:
            boolean r2 = r2.new_value
            if (r2 == 0) goto L_0x0var_
            if (r1 == 0) goto L_0x0ef4
            r1 = 2131624130(0x7f0e00c2, float:1.8875431E38)
            java.lang.String r2 = "ActionForwardsRestrictedChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0ef4:
            r1 = 2131624131(0x7f0e00c3, float:1.8875433E38)
            java.lang.String r2 = "ActionForwardsRestrictedGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0var_:
            if (r1 == 0) goto L_0x0var_
            r1 = 2131624128(0x7f0e00c0, float:1.8875427E38)
            java.lang.String r2 = "ActionForwardsEnabledChannel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0var_:
            r1 = 2131624129(0x7f0e00c1, float:1.887543E38)
            java.lang.String r2 = "ActionForwardsEnabledGroup"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0var_:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteDelete
            if (r1 == 0) goto L_0x0f4b
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteDelete r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteDelete) r2
            r1 = 2131624125(0x7f0e00bd, float:1.887542E38)
            r3 = 0
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r3 = "ActionDeletedInviteLinkClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r2.invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r13, r2)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0f4b:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteRevoke
            if (r1 == 0) goto L_0x0var_
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteRevoke r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteRevoke) r2
            r1 = 2131624167(0x7f0e00e7, float:1.8875506E38)
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
            java.lang.CharSequence r1 = replaceWithLink(r1, r13, r2)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0var_:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteEdit
            if (r1 == 0) goto L_0x0fc5
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteEdit r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionExportedInviteEdit) r2
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r1 = r2.prev_invite
            java.lang.String r1 = r1.link
            if (r1 == 0) goto L_0x0f9d
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = r2.new_invite
            java.lang.String r3 = r3.link
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x0f9d
            r1 = 2131624127(0x7f0e00bf, float:1.8875425E38)
            r3 = 0
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r5 = "ActionEditedInviteLinkToSameClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r5, r1, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0faf
        L_0x0f9d:
            r3 = 0
            r1 = 2131624126(0x7f0e00be, float:1.8875423E38)
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r3 = "ActionEditedInviteLinkClickable"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
        L_0x0faf:
            java.lang.CharSequence r1 = r6.messageText
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r3 = r2.prev_invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r13, r3)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$TL_chatInviteExported r2 = r2.new_invite
            java.lang.String r3 = "un3"
            java.lang.CharSequence r1 = replaceWithLink(r1, r3, r2)
            r6.messageText = r1
            goto L_0x0d07
        L_0x0fc5:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantVolume
            if (r1 == 0) goto L_0x1035
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantVolume r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantVolume) r2
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r1 = r2.participant
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer
            long r3 = getPeerId(r1)
            r8 = 0
            int r1 = (r3 > r8 ? 1 : (r3 == r8 ? 0 : -1))
            if (r1 <= 0) goto L_0x0fe8
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r1 = r1.getUser(r3)
            goto L_0x0ff7
        L_0x0fe8:
            int r1 = r6.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            long r3 = -r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r1 = r1.getChat(r3)
        L_0x0ff7:
            org.telegram.tgnet.TLRPC$TL_groupCallParticipant r2 = r2.participant
            int r2 = org.telegram.messenger.ChatObject.getParticipantVolume(r2)
            double r2 = (double) r2
            r4 = 4636737291354636288(0xNUM, double:100.0)
            java.lang.Double.isNaN(r2)
            double r2 = r2 / r4
            r4 = 2131624183(0x7f0e00f7, float:1.8875539E38)
            r5 = 1
            java.lang.Object[] r8 = new java.lang.Object[r5]
            r11 = 0
            int r5 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r5 <= 0) goto L_0x1017
            r11 = 4607182418800017408(0x3ffNUM, double:1.0)
            double r2 = java.lang.Math.max(r2, r11)
            goto L_0x1019
        L_0x1017:
            r2 = 0
        L_0x1019:
            int r2 = (int) r2
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r3 = 0
            r8[r3] = r2
            java.lang.String r2 = "ActionVolumeChanged"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r4, r8)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            java.lang.CharSequence r1 = replaceWithLink(r2, r13, r1)
            r6.messageText = r1
            goto L_0x0d07
        L_0x1035:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL
            if (r1 == 0) goto L_0x10bb
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeHistoryTTL) r2
            boolean r1 = r0.megagroup
            if (r1 != 0) goto L_0x1067
            int r1 = r2.new_value
            if (r1 == 0) goto L_0x105a
            r2 = 2131624169(0x7f0e00e9, float:1.887551E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatTTLString(r1)
            r3 = 0
            r4[r3] = r1
            java.lang.String r1 = "ActionTTLChannelChanged"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            r6.messageText = r1
            goto L_0x0d07
        L_0x105a:
            r1 = 2131624170(0x7f0e00ea, float:1.8875512E38)
            java.lang.String r2 = "ActionTTLChannelDisabled"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r6.messageText = r1
            goto L_0x0d07
        L_0x1067:
            int r1 = r2.new_value
            if (r1 != 0) goto L_0x107c
            r1 = 2131624171(0x7f0e00eb, float:1.8875514E38)
            java.lang.String r2 = "ActionTTLDisabled"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x107c:
            r2 = 86400(0x15180, float:1.21072E-40)
            if (r1 <= r2) goto L_0x108c
            r2 = 86400(0x15180, float:1.21072E-40)
            int r1 = r1 / r2
            java.lang.String r2 = "Days"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
            goto L_0x10a4
        L_0x108c:
            if (r1 < r4) goto L_0x1094
            int r1 = r1 / r4
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r12, r1)
            goto L_0x10a4
        L_0x1094:
            r2 = 60
            if (r1 < r2) goto L_0x109e
            int r1 = r1 / r2
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r5, r1)
            goto L_0x10a4
        L_0x109e:
            java.lang.String r2 = "Seconds"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatPluralString(r2, r1)
        L_0x10a4:
            r2 = 2131624168(0x7f0e00e8, float:1.8875508E38)
            r3 = 1
            java.lang.Object[] r4 = new java.lang.Object[r3]
            r3 = 0
            r4[r3] = r1
            java.lang.String r1 = "ActionTTLChanged"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r2, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x0d07
        L_0x10bb:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByRequest
            if (r1 == 0) goto L_0x10f2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByRequest r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoinByRequest) r2
            r1 = 2131626093(0x7f0e086d, float:1.8879412E38)
            java.lang.String r3 = "JoinedViaInviteLinkApproved"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r1)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            org.telegram.tgnet.TLRPC$ExportedChatInvite r3 = r2.invite
            java.lang.CharSequence r1 = replaceWithLink(r1, r13, r3)
            r6.messageText = r1
            int r3 = r6.currentAccount
            org.telegram.messenger.MessagesController r3 = org.telegram.messenger.MessagesController.getInstance(r3)
            long r4 = r2.approved_by
            java.lang.Long r2 = java.lang.Long.valueOf(r4)
            org.telegram.tgnet.TLRPC$User r2 = r3.getUser(r2)
            java.lang.String r3 = "un3"
            java.lang.CharSequence r1 = replaceWithLink(r1, r3, r2)
            r6.messageText = r1
            goto L_0x0d07
        L_0x10f2:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionSendMessage
            if (r1 == 0) goto L_0x110b
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionSendMessage r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionSendMessage) r2
            org.telegram.tgnet.TLRPC$Message r1 = r2.message
            r2 = 2131625554(0x7f0e0652, float:1.887832E38)
            java.lang.String r3 = "EventLogSendMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r2 = replaceWithLink(r2, r15, r10)
            r6.messageText = r2
            goto L_0x0ccf
        L_0x110b:
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAvailableReactions
            if (r1 == 0) goto L_0x113d
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAvailableReactions r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAvailableReactions) r2
            java.util.ArrayList<java.lang.String> r1 = r2.prev_value
            java.lang.String r1 = android.text.TextUtils.join(r11, r1)
            r8 = r29
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r8.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAvailableReactions r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAvailableReactions) r2
            java.util.ArrayList<java.lang.String> r2 = r2.new_value
            java.lang.String r2 = android.text.TextUtils.join(r11, r2)
            r3 = 2131624165(0x7f0e00e5, float:1.8875502E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r5 = 0
            r4[r5] = r1
            r1 = 1
            r4[r1] = r2
            java.lang.String r1 = "ActionReactionsChanged"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r1, r3, r4)
            java.lang.CharSequence r1 = replaceWithLink(r1, r15, r10)
            r6.messageText = r1
            goto L_0x1418
        L_0x113d:
            r8 = r29
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "unsupported "
            r1.append(r2)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r8.action
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r6.messageText = r1
            goto L_0x1418
        L_0x1156:
            r8 = r7
            r7 = r14
            boolean r1 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin
            if (r1 == 0) goto L_0x1163
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin) r2
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = r2.prev_participant
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r2.new_participant
            goto L_0x1169
        L_0x1163:
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan) r2
            org.telegram.tgnet.TLRPC$ChannelParticipant r1 = r2.prev_participant
            org.telegram.tgnet.TLRPC$ChannelParticipant r2 = r2.new_participant
        L_0x1169:
            org.telegram.tgnet.TLRPC$TL_message r3 = new org.telegram.tgnet.TLRPC$TL_message
            r3.<init>()
            r6.messageOwner = r3
            org.telegram.tgnet.TLRPC$Peer r3 = r1.peer
            long r3 = getPeerId(r3)
            r11 = 0
            int r5 = (r3 > r11 ? 1 : (r3 == r11 ? 0 : -1))
            if (r5 <= 0) goto L_0x118b
            int r5 = r6.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r3 = r5.getUser(r3)
            goto L_0x119a
        L_0x118b:
            int r5 = r6.currentAccount
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r5)
            long r3 = -r3
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$User r3 = r5.getUser(r3)
        L_0x119a:
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r4 != 0) goto L_0x11c8
            boolean r4 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r4 == 0) goto L_0x11c8
            r1 = 2131625468(0x7f0e05fc, float:1.8878145E38)
            java.lang.String r2 = "EventLogChangedOwnership"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            int r2 = r1.indexOf(r9)
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
            goto L_0x1412
        L_0x11c8:
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r4 = r1.admin_rights
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = r2.admin_rights
            if (r4 != 0) goto L_0x11d3
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r4 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r4.<init>()
        L_0x11d3:
            if (r5 != 0) goto L_0x11da
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r5 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r5.<init>()
        L_0x11da:
            boolean r11 = r5.other
            if (r11 == 0) goto L_0x11e8
            r11 = 2131625529(0x7f0e0639, float:1.8878269E38)
            java.lang.String r12 = "EventLogPromotedNoRights"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
            goto L_0x11f1
        L_0x11e8:
            r11 = 2131625520(0x7f0e0630, float:1.887825E38)
            java.lang.String r12 = "EventLogPromoted"
            java.lang.String r11 = org.telegram.messenger.LocaleController.getString(r12, r11)
        L_0x11f1:
            int r9 = r11.indexOf(r9)
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r13 = 1
            java.lang.Object[] r14 = new java.lang.Object[r13]
            org.telegram.tgnet.TLRPC$Message r13 = r6.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r13 = r13.entities
            java.lang.String r3 = r6.getUserName(r3, r13, r9)
            r9 = 0
            r14[r9] = r3
            java.lang.String r3 = java.lang.String.format(r11, r14)
            r12.<init>(r3)
            java.lang.String r3 = "\n"
            r12.append(r3)
            java.lang.String r1 = r1.rank
            java.lang.String r3 = r2.rank
            boolean r1 = android.text.TextUtils.equals(r1, r3)
            if (r1 != 0) goto L_0x1267
            java.lang.String r1 = r2.rank
            boolean r1 = android.text.TextUtils.isEmpty(r1)
            if (r1 == 0) goto L_0x1241
            r1 = 10
            r12.append(r1)
            r3 = 45
            r12.append(r3)
            r9 = 32
            r12.append(r9)
            r2 = 2131625532(0x7f0e063c, float:1.8878275E38)
            java.lang.String r11 = "EventLogPromotedRemovedTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r2)
            r12.append(r2)
            r1 = 43
            goto L_0x126b
        L_0x1241:
            r1 = 10
            r3 = 45
            r9 = 32
            r12.append(r1)
            r1 = 43
            r12.append(r1)
            r12.append(r9)
            r9 = 2131625534(0x7f0e063e, float:1.8878279E38)
            r11 = 1
            java.lang.Object[] r13 = new java.lang.Object[r11]
            java.lang.String r2 = r2.rank
            r11 = 0
            r13[r11] = r2
            java.lang.String r2 = "EventLogPromotedTitle"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r9, r13)
            r12.append(r2)
            goto L_0x126b
        L_0x1267:
            r1 = 43
            r3 = 45
        L_0x126b:
            boolean r2 = r4.change_info
            boolean r9 = r5.change_info
            if (r2 == r9) goto L_0x129d
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.change_info
            if (r2 == 0) goto L_0x127d
            r2 = 43
            goto L_0x127f
        L_0x127d:
            r2 = 45
        L_0x127f:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x1291
            r2 = 2131625525(0x7f0e0635, float:1.887826E38)
            java.lang.String r9 = "EventLogPromotedChangeGroupInfo"
            goto L_0x1296
        L_0x1291:
            r2 = 2131625524(0x7f0e0634, float:1.8878258E38)
            java.lang.String r9 = "EventLogPromotedChangeChannelInfo"
        L_0x1296:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x129d:
            boolean r2 = r0.megagroup
            if (r2 != 0) goto L_0x12f1
            boolean r2 = r4.post_messages
            boolean r9 = r5.post_messages
            if (r2 == r9) goto L_0x12c9
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.post_messages
            if (r2 == 0) goto L_0x12b3
            r2 = 43
            goto L_0x12b5
        L_0x12b3:
            r2 = 45
        L_0x12b5:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            r2 = 2131625531(0x7f0e063b, float:1.8878273E38)
            java.lang.String r9 = "EventLogPromotedPostMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x12c9:
            boolean r2 = r4.edit_messages
            boolean r9 = r5.edit_messages
            if (r2 == r9) goto L_0x12f1
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.edit_messages
            if (r2 == 0) goto L_0x12db
            r2 = 43
            goto L_0x12dd
        L_0x12db:
            r2 = 45
        L_0x12dd:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            r2 = 2131625527(0x7f0e0637, float:1.8878264E38)
            java.lang.String r9 = "EventLogPromotedEditMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x12f1:
            boolean r2 = r4.delete_messages
            boolean r9 = r5.delete_messages
            if (r2 == r9) goto L_0x1319
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.delete_messages
            if (r2 == 0) goto L_0x1303
            r2 = 43
            goto L_0x1305
        L_0x1303:
            r2 = 45
        L_0x1305:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            r2 = 2131625526(0x7f0e0636, float:1.8878262E38)
            java.lang.String r9 = "EventLogPromotedDeleteMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x1319:
            boolean r2 = r4.add_admins
            boolean r9 = r5.add_admins
            if (r2 == r9) goto L_0x1341
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.add_admins
            if (r2 == 0) goto L_0x132b
            r2 = 43
            goto L_0x132d
        L_0x132b:
            r2 = 45
        L_0x132d:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            r2 = 2131625521(0x7f0e0631, float:1.8878252E38)
            java.lang.String r9 = "EventLogPromotedAddAdmins"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x1341:
            boolean r2 = r4.anonymous
            boolean r9 = r5.anonymous
            if (r2 == r9) goto L_0x1369
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.anonymous
            if (r2 == 0) goto L_0x1353
            r2 = 43
            goto L_0x1355
        L_0x1353:
            r2 = 45
        L_0x1355:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            r2 = 2131625533(0x7f0e063d, float:1.8878277E38)
            java.lang.String r9 = "EventLogPromotedSendAnonymously"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x1369:
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x13bd
            boolean r2 = r4.ban_users
            boolean r9 = r5.ban_users
            if (r2 == r9) goto L_0x1395
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.ban_users
            if (r2 == 0) goto L_0x137f
            r2 = 43
            goto L_0x1381
        L_0x137f:
            r2 = 45
        L_0x1381:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            r2 = 2131625523(0x7f0e0633, float:1.8878256E38)
            java.lang.String r9 = "EventLogPromotedBanUsers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x1395:
            boolean r2 = r4.manage_call
            boolean r9 = r5.manage_call
            if (r2 == r9) goto L_0x13bd
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.manage_call
            if (r2 == 0) goto L_0x13a7
            r2 = 43
            goto L_0x13a9
        L_0x13a7:
            r2 = 45
        L_0x13a9:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            r2 = 2131625528(0x7f0e0638, float:1.8878266E38)
            java.lang.String r9 = "EventLogPromotedManageCall"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x13bd:
            boolean r2 = r4.invite_users
            boolean r9 = r5.invite_users
            if (r2 == r9) goto L_0x13e5
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.invite_users
            if (r2 == 0) goto L_0x13cf
            r2 = 43
            goto L_0x13d1
        L_0x13cf:
            r2 = 45
        L_0x13d1:
            r12.append(r2)
            r2 = 32
            r12.append(r2)
            r2 = 2131625522(0x7f0e0632, float:1.8878254E38)
            java.lang.String r9 = "EventLogPromotedAddUsers"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r9, r2)
            r12.append(r2)
        L_0x13e5:
            boolean r2 = r0.megagroup
            if (r2 == 0) goto L_0x1411
            boolean r2 = r4.pin_messages
            boolean r4 = r5.pin_messages
            if (r2 == r4) goto L_0x1411
            r2 = 10
            r12.append(r2)
            boolean r2 = r5.pin_messages
            if (r2 == 0) goto L_0x13fb
            r11 = 43
            goto L_0x13fd
        L_0x13fb:
            r11 = 45
        L_0x13fd:
            r12.append(r11)
            r1 = 32
            r12.append(r1)
            r1 = 2131625530(0x7f0e063a, float:1.887827E38)
            java.lang.String r2 = "EventLogPromotedPinMessages"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r12.append(r1)
        L_0x1411:
            r4 = r12
        L_0x1412:
            java.lang.String r1 = r4.toString()
            r6.messageText = r1
        L_0x1418:
            r1 = 0
        L_0x1419:
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            if (r2 != 0) goto L_0x1424
            org.telegram.tgnet.TLRPC$TL_messageService r2 = new org.telegram.tgnet.TLRPC$TL_messageService
            r2.<init>()
            r6.messageOwner = r2
        L_0x1424:
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
            long r4 = r8.user_id
            r3.user_id = r4
            int r3 = r8.date
            r2.date = r3
            r3 = 0
            r4 = r33[r3]
            int r5 = r4 + 1
            r33[r3] = r5
            r2.id = r4
            long r4 = r8.id
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
            org.telegram.messenger.MediaController r9 = org.telegram.messenger.MediaController.getInstance()
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r0 == 0) goto L_0x146c
            r1 = 0
        L_0x146c:
            if (r1 == 0) goto L_0x14e0
            r1.out = r3
            r0 = r33[r3]
            int r2 = r0 + 1
            r33[r3] = r2
            r1.id = r0
            int r0 = r1.flags
            r0 = r0 & -9
            r1.flags = r0
            r11 = 0
            r1.reply_to = r11
            r2 = -32769(0xffffffffffff7fff, float:NaN)
            r0 = r0 & r2
            r1.flags = r0
            org.telegram.messenger.MessageObject r12 = new org.telegram.messenger.MessageObject
            int r0 = r6.currentAccount
            r20 = 0
            r21 = 0
            r22 = 1
            r23 = 1
            long r2 = r6.eventId
            r17 = r12
            r18 = r0
            r19 = r1
            r24 = r2
            r17.<init>((int) r18, (org.telegram.tgnet.TLRPC$Message) r19, (java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC$User>) r20, (java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC$Chat>) r21, (boolean) r22, (boolean) r23, (long) r24)
            int r0 = r12.contentType
            if (r0 < 0) goto L_0x14da
            boolean r0 = r9.isPlayingMessage(r12)
            if (r0 == 0) goto L_0x14b6
            org.telegram.messenger.MessageObject r0 = r9.getPlayingMessageObject()
            float r1 = r0.audioProgress
            r12.audioProgress = r1
            int r0 = r0.audioProgressSec
            r12.audioProgressSec = r0
        L_0x14b6:
            int r1 = r6.currentAccount
            r0 = r27
            r2 = r29
            r3 = r30
            r4 = r31
            r5 = r34
            r0.createDateArray(r1, r2, r3, r4, r5)
            if (r34 == 0) goto L_0x14ce
            r13 = r30
            r0 = 0
            r13.add(r0, r12)
            goto L_0x14e3
        L_0x14ce:
            r13 = r30
            int r0 = r30.size()
            r1 = 1
            int r0 = r0 - r1
            r13.add(r0, r12)
            goto L_0x14e3
        L_0x14da:
            r13 = r30
            r0 = -1
            r6.contentType = r0
            goto L_0x14e3
        L_0x14e0:
            r13 = r30
            r11 = 0
        L_0x14e3:
            int r0 = r6.contentType
            if (r0 < 0) goto L_0x1561
            int r1 = r6.currentAccount
            r0 = r27
            r2 = r29
            r3 = r30
            r4 = r31
            r5 = r34
            r0.createDateArray(r1, r2, r3, r4, r5)
            if (r34 == 0) goto L_0x14fd
            r0 = 0
            r13.add(r0, r6)
            goto L_0x1506
        L_0x14fd:
            int r0 = r30.size()
            r1 = 1
            int r0 = r0 - r1
            r13.add(r0, r6)
        L_0x1506:
            java.lang.CharSequence r0 = r6.messageText
            if (r0 != 0) goto L_0x150c
            r6.messageText = r7
        L_0x150c:
            r27.setType()
            r27.measureInlineBotButtons()
            r27.generateCaption()
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x1520
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint
            goto L_0x1522
        L_0x1520:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
        L_0x1522:
            boolean r1 = r27.allowsBigEmoji()
            if (r1 == 0) goto L_0x152c
            r1 = 1
            int[] r2 = new int[r1]
            r11 = r2
        L_0x152c:
            java.lang.CharSequence r1 = r6.messageText
            android.graphics.Paint$FontMetricsInt r0 = r0.getFontMetricsInt()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r1, r0, r2, r3, r11)
            r6.messageText = r0
            r6.checkEmojiOnly(r11)
            boolean r0 = r9.isPlayingMessage(r6)
            if (r0 == 0) goto L_0x1554
            org.telegram.messenger.MessageObject r0 = r9.getPlayingMessageObject()
            float r1 = r0.audioProgress
            r6.audioProgress = r1
            int r0 = r0.audioProgressSec
            r6.audioProgressSec = r0
        L_0x1554:
            r6.generateLayout(r10)
            r0 = 1
            r6.layoutCreated = r0
            r0 = 0
            r6.generateThumbs(r0)
            r27.checkMediaExistance()
        L_0x1561:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.<init>(int, org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent, java.util.ArrayList, java.util.HashMap, org.telegram.tgnet.TLRPC$Chat, int[], boolean):void");
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
                    str4 = LocaleController.getString("HiddenName", NUM);
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
        TextPaint textPaint;
        if (!TextUtils.isEmpty(this.messageOwner.message)) {
            int[] iArr = null;
            TLRPC$User user = isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id)) : null;
            TLRPC$Message tLRPC$Message = this.messageOwner;
            this.messageText = tLRPC$Message.message;
            if (tLRPC$Message.media instanceof TLRPC$TL_messageMediaGame) {
                textPaint = Theme.chat_msgGameTextPaint;
            } else {
                textPaint = Theme.chat_msgTextPaint;
            }
            if (allowsBigEmoji()) {
                iArr = new int[1];
            }
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            checkEmojiOnly(iArr);
            generateLayout(user);
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
        TLRPC$MessageMedia tLRPC$MessageMedia;
        TLRPC$TL_game tLRPC$TL_game;
        if (tLRPC$User == null && isFromUser()) {
            tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
        }
        TLRPC$TL_game tLRPC$TL_game2 = null;
        MessageObject messageObject = this.replyMessageObject;
        if (!(messageObject == null || (tLRPC$MessageMedia = messageObject.messageOwner.media) == null || (tLRPC$TL_game = tLRPC$MessageMedia.game) == null)) {
            tLRPC$TL_game2 = tLRPC$TL_game;
        }
        if (tLRPC$TL_game2 != null) {
            if (tLRPC$User == null || tLRPC$User.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", tLRPC$User);
            } else {
                this.messageText = LocaleController.formatString("ActionYouScoredInGame", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
            }
            this.messageText = replaceWithLink(this.messageText, "un2", tLRPC$TL_game2);
        } else if (tLRPC$User == null || tLRPC$User.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", tLRPC$User);
        } else {
            this.messageText = LocaleController.formatString("ActionYouScored", NUM, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
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
        if (tLRPC$User == null) {
            tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(getDialogId()));
        }
        String firstName = tLRPC$User != null ? UserObject.getFirstName(tLRPC$User) : "";
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject == null || !(messageObject.messageOwner.media instanceof TLRPC$TL_messageMediaInvoice)) {
            LocaleController instance = LocaleController.getInstance();
            TLRPC$MessageAction tLRPC$MessageAction = this.messageOwner.action;
            this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", NUM, instance.formatCurrencyString(tLRPC$MessageAction.total_amount, tLRPC$MessageAction.currency), firstName);
            return;
        }
        LocaleController instance2 = LocaleController.getInstance();
        TLRPC$MessageAction tLRPC$MessageAction2 = this.messageOwner.action;
        this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", NUM, instance2.formatCurrencyString(tLRPC$MessageAction2.total_amount, tLRPC$MessageAction2.currency), firstName, this.replyMessageObject.messageOwner.media.title);
    }

    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Incorrect type for immutable var: ssa=org.telegram.tgnet.TLRPC$User, code=org.telegram.tgnet.TLRPC$Chat, for r9v0, types: [org.telegram.tgnet.TLRPC$User] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generatePinMessageText(org.telegram.tgnet.TLRPC$Chat r9, org.telegram.tgnet.TLRPC$Chat r10) {
        /*
            r8 = this;
            if (r9 != 0) goto L_0x0055
            if (r10 != 0) goto L_0x0055
            boolean r0 = r8.isFromUser()
            if (r0 == 0) goto L_0x001e
            int r9 = r8.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.from_id
            long r0 = r0.user_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r0)
        L_0x001e:
            if (r9 != 0) goto L_0x0055
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r1 == 0) goto L_0x003d
            int r10 = r8.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r0 = r0.channel_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r0)
            goto L_0x0055
        L_0x003d:
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r0 == 0) goto L_0x0055
            int r10 = r8.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r0 = r0.chat_id
            java.lang.Long r0 = java.lang.Long.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r0)
        L_0x0055:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            r1 = 2131624156(0x7f0e00dc, float:1.8875484E38)
            java.lang.String r2 = "ActionPinnedNoText"
            java.lang.String r3 = "un1"
            if (r0 == 0) goto L_0x029b
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r5 != 0) goto L_0x029b
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r4 == 0) goto L_0x006e
            goto L_0x029b
        L_0x006e:
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0089
            r0 = 2131624155(0x7f0e00db, float:1.8875482E38)
            java.lang.String r1 = "ActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0080
            goto L_0x0081
        L_0x0080:
            r9 = r10
        L_0x0081:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x0089:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x00a6
            r0 = 2131624163(0x7f0e00e3, float:1.8875498E38)
            java.lang.String r1 = "ActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x009d
            goto L_0x009e
        L_0x009d:
            r9 = r10
        L_0x009e:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x00a6:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isGif()
            if (r0 == 0) goto L_0x00c3
            r0 = 2131624154(0x7f0e00da, float:1.887548E38)
            java.lang.String r1 = "ActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x00ba
            goto L_0x00bb
        L_0x00ba:
            r9 = r10
        L_0x00bb:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x00c3:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x00e0
            r0 = 2131624164(0x7f0e00e4, float:1.88755E38)
            java.lang.String r1 = "ActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x00d7
            goto L_0x00d8
        L_0x00d7:
            r9 = r10
        L_0x00d8:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x00e0:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isRoundVideo()
            if (r0 == 0) goto L_0x00fd
            r0 = 2131624160(0x7f0e00e0, float:1.8875492E38)
            java.lang.String r1 = "ActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x00f4
            goto L_0x00f5
        L_0x00f4:
            r9 = r10
        L_0x00f5:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x00fd:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isSticker()
            if (r0 != 0) goto L_0x010d
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isAnimatedSticker()
            if (r0 == 0) goto L_0x012a
        L_0x010d:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isAnimatedEmoji()
            if (r0 != 0) goto L_0x012a
            r0 = 2131624161(0x7f0e00e1, float:1.8875494E38)
            java.lang.String r1 = "ActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0121
            goto L_0x0122
        L_0x0121:
            r9 = r10
        L_0x0122:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x012a:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x0149
            r0 = 2131624150(0x7f0e00d6, float:1.8875472E38)
            java.lang.String r1 = "ActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0140
            goto L_0x0141
        L_0x0140:
            r9 = r10
        L_0x0141:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x0149:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 == 0) goto L_0x0162
            r0 = 2131624152(0x7f0e00d8, float:1.8875476E38)
            java.lang.String r1 = "ActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0159
            goto L_0x015a
        L_0x0159:
            r9 = r10
        L_0x015a:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x0162:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x017b
            r0 = 2131624153(0x7f0e00d9, float:1.8875478E38)
            java.lang.String r1 = "ActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0172
            goto L_0x0173
        L_0x0172:
            r9 = r10
        L_0x0173:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x017b:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r5 == 0) goto L_0x0194
            r0 = 2131624149(0x7f0e00d5, float:1.887547E38)
            java.lang.String r1 = "ActionPinnedContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x018b
            goto L_0x018c
        L_0x018b:
            r9 = r10
        L_0x018c:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x0194:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x01ca
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$Poll r0 = r4.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x01b5
            r0 = 2131624159(0x7f0e00df, float:1.887549E38)
            java.lang.String r1 = "ActionPinnedQuiz"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x01ac
            goto L_0x01ad
        L_0x01ac:
            r9 = r10
        L_0x01ad:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x01b5:
            r0 = 2131624158(0x7f0e00de, float:1.8875488E38)
            java.lang.String r1 = "ActionPinnedPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x01c1
            goto L_0x01c2
        L_0x01c1:
            r9 = r10
        L_0x01c2:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x01ca:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x01e3
            r0 = 2131624157(0x7f0e00dd, float:1.8875486E38)
            java.lang.String r1 = "ActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x01da
            goto L_0x01db
        L_0x01da:
            r9 = r10
        L_0x01db:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x01e3:
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            r5 = 1101004800(0x41a00000, float:20.0)
            r6 = 1
            r7 = 0
            if (r4 == 0) goto L_0x022f
            r0 = 2131624151(0x7f0e00d7, float:1.8875474E38)
            java.lang.Object[] r1 = new java.lang.Object[r6]
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "🎮 "
            r2.append(r4)
            org.telegram.messenger.MessageObject r4 = r8.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r4 = r4.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            org.telegram.tgnet.TLRPC$TL_game r4 = r4.game
            java.lang.String r4 = r4.title
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            r1[r7] = r2
            java.lang.String r2 = "ActionPinnedGame"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            if (r9 == 0) goto L_0x0216
            goto L_0x0217
        L_0x0216:
            r9 = r10
        L_0x0217:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r10 = r10.getFontMetricsInt()
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r9 = org.telegram.messenger.Emoji.replaceEmoji(r9, r10, r0, r7)
            r8.messageText = r9
            goto L_0x02a9
        L_0x022f:
            java.lang.CharSequence r0 = r0.messageText
            if (r0 == 0) goto L_0x028c
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x028c
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            java.lang.CharSequence r0 = r0.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x025a
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x025a:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r1, r2, r7)
            org.telegram.messenger.MessageObject r1 = r8.replyMessageObject
            r2 = r0
            android.text.Spannable r2 = (android.text.Spannable) r2
            org.telegram.messenger.MediaDataController.addTextStyleRuns(r1, r2)
            r1 = 2131624162(0x7f0e00e2, float:1.8875496E38)
            java.lang.String r2 = "ActionPinnedText"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence[] r2 = new java.lang.CharSequence[r6]
            r2[r7] = r0
            android.text.SpannableStringBuilder r0 = org.telegram.messenger.AndroidUtilities.formatSpannable(r1, r2)
            if (r9 == 0) goto L_0x0284
            goto L_0x0285
        L_0x0284:
            r9 = r10
        L_0x0285:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x028c:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r9 == 0) goto L_0x0293
            goto L_0x0294
        L_0x0293:
            r9 = r10
        L_0x0294:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x02a9
        L_0x029b:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r9 == 0) goto L_0x02a2
            goto L_0x02a3
        L_0x02a2:
            r9 = r10
        L_0x02a3:
            java.lang.CharSequence r9 = replaceWithLink(r0, r3, r9)
            r8.messageText = r9
        L_0x02a9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generatePinMessageText(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat):void");
    }

    public static void updateReactions(TLRPC$Message tLRPC$Message, TLRPC$TL_messageReactions tLRPC$TL_messageReactions) {
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions2;
        if (tLRPC$Message != null && tLRPC$TL_messageReactions != null) {
            if (tLRPC$TL_messageReactions.min && (tLRPC$TL_messageReactions2 = tLRPC$Message.reactions) != null) {
                int size = tLRPC$TL_messageReactions2.results.size();
                int i = 0;
                int i2 = 0;
                while (true) {
                    if (i2 >= size) {
                        break;
                    }
                    TLRPC$TL_reactionCount tLRPC$TL_reactionCount = tLRPC$Message.reactions.results.get(i2);
                    if (tLRPC$TL_reactionCount.chosen) {
                        int size2 = tLRPC$TL_messageReactions.results.size();
                        while (true) {
                            if (i >= size2) {
                                break;
                            }
                            TLRPC$TL_reactionCount tLRPC$TL_reactionCount2 = tLRPC$TL_messageReactions.results.get(i);
                            if (tLRPC$TL_reactionCount.reaction.equals(tLRPC$TL_reactionCount2.reaction)) {
                                tLRPC$TL_reactionCount2.chosen = true;
                                break;
                            }
                            i++;
                        }
                    } else {
                        i2++;
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

    public boolean isPollClosed() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC$TL_messageMediaPoll) this.messageOwner.media).poll.closed;
    }

    public boolean isQuiz() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC$TL_messageMediaPoll) this.messageOwner.media).poll.quiz;
    }

    public boolean isPublicPoll() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC$TL_messageMediaPoll) this.messageOwner.media).poll.public_voters;
    }

    public boolean isPoll() {
        return this.type == 17;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5.messageOwner.media;
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
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            if (r2 == 0) goto L_0x003f
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            boolean r2 = r2.isEmpty()
            if (r2 != 0) goto L_0x003f
            org.telegram.tgnet.TLRPC$Poll r2 = r0.poll
            boolean r2 = r2.quiz
            if (r2 == 0) goto L_0x0021
            goto L_0x003f
        L_0x0021:
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            int r2 = r2.size()
            r3 = 0
        L_0x002a:
            if (r3 >= r2) goto L_0x003f
            org.telegram.tgnet.TLRPC$PollResults r4 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r4 = r4.results
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r4 = (org.telegram.tgnet.TLRPC$TL_pollAnswerVoters) r4
            boolean r4 = r4.chosen
            if (r4 == 0) goto L_0x003c
            r0 = 1
            return r0
        L_0x003c:
            int r3 = r3 + 1
            goto L_0x002a
        L_0x003f:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.canUnvote():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0008, code lost:
        r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r5.messageOwner.media;
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
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r0 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r0
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            if (r2 == 0) goto L_0x0039
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            boolean r2 = r2.isEmpty()
            if (r2 == 0) goto L_0x001b
            goto L_0x0039
        L_0x001b:
            org.telegram.tgnet.TLRPC$PollResults r2 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r2 = r2.results
            int r2 = r2.size()
            r3 = 0
        L_0x0024:
            if (r3 >= r2) goto L_0x0039
            org.telegram.tgnet.TLRPC$PollResults r4 = r0.results
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_pollAnswerVoters> r4 = r4.results
            java.lang.Object r4 = r4.get(r3)
            org.telegram.tgnet.TLRPC$TL_pollAnswerVoters r4 = (org.telegram.tgnet.TLRPC$TL_pollAnswerVoters) r4
            boolean r4 = r4.chosen
            if (r4 == 0) goto L_0x0036
            r0 = 1
            return r0
        L_0x0036:
            int r3 = r3 + 1
            goto L_0x0024
        L_0x0039:
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
        return ((TLRPC$TL_messageMediaPoll) this.messageOwner.media).poll.id;
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

    public ArrayList<MessageObject> getWebPagePhotos(ArrayList<MessageObject> arrayList, ArrayList<TLRPC$PageBlock> arrayList2) {
        TLRPC$WebPage tLRPC$WebPage;
        TLRPC$Page tLRPC$Page;
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        if (tLRPC$MessageMedia == null || (tLRPC$WebPage = tLRPC$MessageMedia.webpage) == null || (tLRPC$Page = tLRPC$WebPage.cached_page) == null) {
            return arrayList;
        }
        if (arrayList2 == null) {
            arrayList2 = tLRPC$Page.blocks;
        }
        for (int i = 0; i < arrayList2.size(); i++) {
            TLRPC$PageBlock tLRPC$PageBlock = arrayList2.get(i);
            if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockSlideshow) {
                TLRPC$TL_pageBlockSlideshow tLRPC$TL_pageBlockSlideshow = (TLRPC$TL_pageBlockSlideshow) tLRPC$PageBlock;
                for (int i2 = 0; i2 < tLRPC$TL_pageBlockSlideshow.items.size(); i2++) {
                    arrayList.add(getMessageObjectForBlock(tLRPC$WebPage, tLRPC$TL_pageBlockSlideshow.items.get(i2)));
                }
            } else if (tLRPC$PageBlock instanceof TLRPC$TL_pageBlockCollage) {
                TLRPC$TL_pageBlockCollage tLRPC$TL_pageBlockCollage = (TLRPC$TL_pageBlockCollage) tLRPC$PageBlock;
                for (int i3 = 0; i3 < tLRPC$TL_pageBlockCollage.items.size(); i3++) {
                    arrayList.add(getMessageObjectForBlock(tLRPC$WebPage, tLRPC$TL_pageBlockCollage.items.get(i3)));
                }
            }
        }
        return arrayList;
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
            TLRPC$Message tLRPC$Message = this.messageOwner;
            if ((tLRPC$Message.reply_markup instanceof TLRPC$TL_replyInlineMarkup) || ((tLRPC$TL_messageReactions = tLRPC$Message.reactions) != null && !tLRPC$TL_messageReactions.results.isEmpty())) {
                Theme.createCommonMessageResources();
                StringBuilder sb = this.botButtonsLayout;
                if (sb == null) {
                    this.botButtonsLayout = new StringBuilder();
                } else {
                    sb.setLength(0);
                }
            }
            TLRPC$Message tLRPC$Message2 = this.messageOwner;
            if (tLRPC$Message2.reply_markup instanceof TLRPC$TL_replyInlineMarkup) {
                for (int i2 = 0; i2 < this.messageOwner.reply_markup.rows.size(); i2++) {
                    TLRPC$TL_keyboardButtonRow tLRPC$TL_keyboardButtonRow = this.messageOwner.reply_markup.rows.get(i2);
                    int size = tLRPC$TL_keyboardButtonRow.buttons.size();
                    int i3 = 0;
                    for (int i4 = 0; i4 < size; i4++) {
                        TLRPC$KeyboardButton tLRPC$KeyboardButton = tLRPC$TL_keyboardButtonRow.buttons.get(i4);
                        StringBuilder sb2 = this.botButtonsLayout;
                        sb2.append(i2);
                        sb2.append(i4);
                        if (!(tLRPC$KeyboardButton instanceof TLRPC$TL_keyboardButtonBuy) || (this.messageOwner.media.flags & 4) == 0) {
                            charSequence = Emoji.replaceEmoji(tLRPC$KeyboardButton.text, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                        } else {
                            charSequence = LocaleController.getString("PaymentReceipt", NUM);
                        }
                        StaticLayout staticLayout = new StaticLayout(charSequence, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        if (staticLayout.getLineCount() > 0) {
                            float lineWidth = staticLayout.getLineWidth(0);
                            float lineLeft = staticLayout.getLineLeft(0);
                            if (lineLeft < lineWidth) {
                                lineWidth -= lineLeft;
                            }
                            i3 = Math.max(i3, ((int) Math.ceil((double) lineWidth)) + AndroidUtilities.dp(4.0f));
                        }
                    }
                    this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((i3 + AndroidUtilities.dp(12.0f)) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
                }
                return;
            }
            TLRPC$TL_messageReactions tLRPC$TL_messageReactions2 = tLRPC$Message2.reactions;
            if (tLRPC$TL_messageReactions2 != null) {
                int size2 = tLRPC$TL_messageReactions2.results.size();
                for (int i5 = 0; i5 < size2; i5++) {
                    TLRPC$TL_reactionCount tLRPC$TL_reactionCount = this.messageOwner.reactions.results.get(i5);
                    StringBuilder sb3 = this.botButtonsLayout;
                    sb3.append(0);
                    sb3.append(i5);
                    StaticLayout staticLayout2 = new StaticLayout(Emoji.replaceEmoji(String.format("%d %s", new Object[]{Integer.valueOf(tLRPC$TL_reactionCount.count), tLRPC$TL_reactionCount.reaction}), Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false), Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (staticLayout2.getLineCount() > 0) {
                        float lineWidth2 = staticLayout2.getLineWidth(0);
                        float lineLeft2 = staticLayout2.getLineLeft(0);
                        if (lineLeft2 < lineWidth2) {
                            lineWidth2 -= lineLeft2;
                        }
                        i = Math.max(0, ((int) Math.ceil((double) lineWidth2)) + AndroidUtilities.dp(4.0f));
                    } else {
                        i = 0;
                    }
                    this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((i + AndroidUtilities.dp(12.0f)) * size2) + (AndroidUtilities.dp(5.0f) * (size2 - 1)));
                }
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

    /* JADX WARNING: Removed duplicated region for block: B:10:0x002c  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0528  */
    /* JADX WARNING: Removed duplicated region for block: B:190:0x0548  */
    /* JADX WARNING: Removed duplicated region for block: B:212:0x05b4  */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x05cb  */
    /* JADX WARNING: Removed duplicated region for block: B:229:0x0621  */
    /* JADX WARNING: Removed duplicated region for block: B:230:0x062e  */
    /* JADX WARNING: Removed duplicated region for block: B:257:0x06e4  */
    /* JADX WARNING: Removed duplicated region for block: B:261:0x070b  */
    /* JADX WARNING: Removed duplicated region for block: B:334:0x091d  */
    /* JADX WARNING: Removed duplicated region for block: B:335:0x092a  */
    /* JADX WARNING: Removed duplicated region for block: B:484:0x0ced  */
    /* JADX WARNING: Removed duplicated region for block: B:584:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:601:? A[RETURN, SYNTHETIC] */
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
            if (r9 == 0) goto L_0x0ced
            org.telegram.tgnet.TLRPC$MessageAction r9 = r3.action
            if (r9 == 0) goto L_0x0f0f
            boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled
            r15 = 3
            if (r14 == 0) goto L_0x007f
            org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled r9 = (org.telegram.tgnet.TLRPC$TL_messageActionGroupCallScheduled) r9
            org.telegram.tgnet.TLRPC$Peer r0 = r3.peer_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r0 != 0) goto L_0x0067
            boolean r0 = r20.isSupergroup()
            if (r0 == 0) goto L_0x004f
            goto L_0x0067
        L_0x004f:
            r0 = 2131624116(0x7f0e00b4, float:1.8875403E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            int r2 = r9.schedule_date
            long r2 = (long) r2
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatStartsTime(r2, r15, r13)
            r1[r13] = r2
            java.lang.String r2 = "ActionChannelCallScheduled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0067:
            r0 = 2131624138(0x7f0e00ca, float:1.8875447E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            int r2 = r9.schedule_date
            long r2 = (long) r2
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatStartsTime(r2, r15, r13)
            r1[r13] = r2
            java.lang.String r2 = "ActionGroupCallScheduled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x007f:
            boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGroupCall
            java.lang.String r7 = "un1"
            if (r14 == 0) goto L_0x013d
            int r0 = r9.duration
            if (r0 == 0) goto L_0x00ff
            r1 = 86400(0x15180, float:1.21072E-40)
            int r1 = r0 / r1
            if (r1 <= 0) goto L_0x0097
            java.lang.String r0 = "Days"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r0, r1)
            goto L_0x00b3
        L_0x0097:
            int r1 = r0 / 3600
            if (r1 <= 0) goto L_0x00a2
            java.lang.String r0 = "Hours"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r0, r1)
            goto L_0x00b3
        L_0x00a2:
            int r1 = r0 / 60
            if (r1 <= 0) goto L_0x00ad
            java.lang.String r0 = "Minutes"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r0, r1)
            goto L_0x00b3
        L_0x00ad:
            java.lang.String r1 = "Seconds"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatPluralString(r1, r0)
        L_0x00b3:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r1 != 0) goto L_0x00d3
            boolean r1 = r20.isSupergroup()
            if (r1 == 0) goto L_0x00c2
            goto L_0x00d3
        L_0x00c2:
            r1 = 2131624114(0x7f0e00b2, float:1.8875399E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r2[r13] = r0
            java.lang.String r0 = "ActionChannelCallEnded"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x00d3:
            boolean r1 = r20.isOut()
            if (r1 == 0) goto L_0x00ea
            r1 = 2131624134(0x7f0e00c6, float:1.887544E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r2[r13] = r0
            java.lang.String r0 = "ActionGroupCallEndedByYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x00ea:
            r1 = 2131624133(0x7f0e00c5, float:1.8875437E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r2[r13] = r0
            java.lang.String r0 = "ActionGroupCallEndedBy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x00ff:
            org.telegram.tgnet.TLRPC$Peer r0 = r3.peer_id
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r0 != 0) goto L_0x0119
            boolean r0 = r20.isSupergroup()
            if (r0 == 0) goto L_0x010c
            goto L_0x0119
        L_0x010c:
            r0 = 2131624115(0x7f0e00b3, float:1.88754E38)
            java.lang.String r1 = "ActionChannelCallJustStarted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0119:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x012c
            r0 = 2131624140(0x7f0e00cc, float:1.8875451E38)
            java.lang.String r1 = "ActionGroupCallStartedByYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x012c:
            r0 = 2131624139(0x7f0e00cb, float:1.887545E38)
            java.lang.String r1 = "ActionGroupCallStarted"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x013d:
            boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionInviteToGroupCall
            java.lang.String r15 = "un2"
            r18 = 0
            if (r14 == 0) goto L_0x01f8
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
            r2 = 2131624141(0x7f0e00cd, float:1.8875453E38)
            java.lang.String r3 = "ActionGroupCallYouInvited"
            r9 = 2131624135(0x7f0e00c7, float:1.8875441E38)
            java.lang.String r11 = "ActionGroupCallInvited"
            int r12 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1))
            if (r12 == 0) goto L_0x01b8
            org.telegram.tgnet.TLRPC$User r4 = r6.getUser(r4, r5, r0)
            boolean r5 = r20.isOut()
            if (r5 == 0) goto L_0x0187
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r3, r2)
            java.lang.CharSequence r0 = replaceWithLink(r0, r15, r4)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0187:
            int r2 = r6.currentAccount
            org.telegram.messenger.UserConfig r2 = org.telegram.messenger.UserConfig.getInstance(r2)
            long r2 = r2.getClientUserId()
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 != 0) goto L_0x01a6
            r0 = 2131624136(0x7f0e00c8, float:1.8875443E38)
            java.lang.String r1 = "ActionGroupCallInvitedYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x01a6:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r11, r9)
            java.lang.CharSequence r0 = replaceWithLink(r0, r15, r4)
            r6.messageText = r0
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x01b8:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x01d8
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r3, r2)
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.util.ArrayList<java.lang.Long> r3 = r0.users
            java.lang.String r2 = "un2"
            r0 = r20
            r4 = r21
            r5 = r23
            java.lang.CharSequence r0 = r0.replaceWithLink(r1, r2, r3, r4, r5)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x01d8:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r9)
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
            goto L_0x0f0f
        L_0x01f8:
            boolean r14 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached
            if (r14 == 0) goto L_0x0298
            org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached r9 = (org.telegram.tgnet.TLRPC$TL_messageActionGeoProximityReached) r9
            org.telegram.tgnet.TLRPC$Peer r2 = r9.from_id
            long r2 = getPeerId(r2)
            int r8 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r8 <= 0) goto L_0x020d
            org.telegram.tgnet.TLRPC$User r8 = r6.getUser(r4, r5, r2)
            goto L_0x0212
        L_0x020d:
            long r13 = -r2
            org.telegram.tgnet.TLRPC$Chat r8 = r6.getChat(r0, r1, r13)
        L_0x0212:
            org.telegram.tgnet.TLRPC$Peer r13 = r9.to_id
            long r13 = getPeerId(r13)
            int r11 = r6.currentAccount
            org.telegram.messenger.UserConfig r11 = org.telegram.messenger.UserConfig.getInstance(r11)
            long r16 = r11.getClientUserId()
            int r11 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r11 != 0) goto L_0x0244
            r0 = 2131624181(0x7f0e00f5, float:1.8875534E38)
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
            goto L_0x0f0f
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
            if (r1 != 0) goto L_0x0274
            r1 = 2131624182(0x7f0e00f6, float:1.8875536E38)
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
            goto L_0x0f0f
        L_0x0274:
            r4 = 2
            r5 = 0
            r1 = 2131624180(0x7f0e00f4, float:1.8875532E38)
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
            goto L_0x0f0f
        L_0x0298:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionCustomAction
            if (r11 == 0) goto L_0x02a2
            java.lang.String r0 = r9.message
            r6.messageText = r0
            goto L_0x0f0f
        L_0x02a2:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatCreate
            if (r11 == 0) goto L_0x02ca
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x02b9
            r0 = 2131624188(0x7f0e00fc, float:1.8875549E38)
            java.lang.String r1 = "ActionYouCreateGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x02b9:
            r0 = 2131624122(0x7f0e00ba, float:1.8875415E38)
            java.lang.String r1 = "ActionCreateGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x02ca:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            if (r11 == 0) goto L_0x0363
            boolean r0 = r20.isFromUser()
            if (r0 == 0) goto L_0x0306
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r0.action
            long r1 = r1.user_id
            org.telegram.tgnet.TLRPC$Peer r0 = r0.from_id
            long r11 = r0.user_id
            int r0 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r0 != 0) goto L_0x0306
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x02f5
            r0 = 2131624190(0x7f0e00fe, float:1.8875553E38)
            java.lang.String r1 = "ActionYouLeftUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x02f5:
            r0 = 2131624146(0x7f0e00d2, float:1.8875463E38)
            java.lang.String r1 = "ActionLeftUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0306:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            long r0 = r0.user_id
            org.telegram.tgnet.TLRPC$User r0 = r6.getUser(r4, r5, r0)
            boolean r1 = r20.isOut()
            if (r1 == 0) goto L_0x0327
            r1 = 2131624189(0x7f0e00fd, float:1.887555E38)
            java.lang.String r2 = "ActionYouKickUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r0 = replaceWithLink(r1, r15, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0327:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            long r1 = r1.user_id
            int r3 = r6.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r3 = r3.getClientUserId()
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x034c
            r0 = 2131624145(0x7f0e00d1, float:1.8875461E38)
            java.lang.String r1 = "ActionKickUserYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x034c:
            r1 = 2131624144(0x7f0e00d0, float:1.887546E38)
            java.lang.String r2 = "ActionKickUser"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.CharSequence r0 = replaceWithLink(r1, r15, r0)
            r6.messageText = r0
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0363:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            if (r11 == 0) goto L_0x04e2
            long r2 = r9.user_id
            int r11 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r11 != 0) goto L_0x0386
            java.util.ArrayList<java.lang.Long> r9 = r9.users
            int r9 = r9.size()
            if (r9 != r12) goto L_0x0386
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            java.util.ArrayList<java.lang.Long> r2 = r2.users
            r3 = 0
            java.lang.Object r2 = r2.get(r3)
            java.lang.Long r2 = (java.lang.Long) r2
            long r2 = r2.longValue()
        L_0x0386:
            r9 = 2131624184(0x7f0e00f8, float:1.887554E38)
            java.lang.String r11 = "ActionYouAddUser"
            java.lang.String r13 = "ActionAddUser"
            int r14 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r14 == 0) goto L_0x049e
            org.telegram.tgnet.TLRPC$User r4 = r6.getUser(r4, r5, r2)
            org.telegram.tgnet.TLRPC$Message r5 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r5 = r5.peer_id
            r17 = r13
            long r12 = r5.channel_id
            int r5 = (r12 > r18 ? 1 : (r12 == r18 ? 0 : -1))
            if (r5 == 0) goto L_0x03a6
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r12)
            goto L_0x03a7
        L_0x03a6:
            r0 = 0
        L_0x03a7:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.from_id
            if (r1 == 0) goto L_0x0424
            long r12 = r1.user_id
            int r1 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
            if (r1 != 0) goto L_0x0424
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x03ca
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x03ca
            r0 = 2131624781(0x7f0e034d, float:1.8876751E38)
            java.lang.String r1 = "ChannelJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x03ca:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r0 = r0.channel_id
            int r4 = (r0 > r18 ? 1 : (r0 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x0400
            int r0 = r6.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            long r0 = r0.getClientUserId()
            int r4 = (r2 > r0 ? 1 : (r2 == r0 ? 0 : -1))
            if (r4 != 0) goto L_0x03ef
            r0 = 2131624786(0x7f0e0352, float:1.8876762E38)
            java.lang.String r1 = "ChannelMegaJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x03ef:
            r0 = 2131624093(0x7f0e009d, float:1.8875356E38)
            java.lang.String r1 = "ActionAddUserSelfMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0400:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x0413
            r0 = 2131624094(0x7f0e009e, float:1.8875358E38)
            java.lang.String r1 = "ActionAddUserSelfYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0413:
            r0 = 2131624092(0x7f0e009c, float:1.8875354E38)
            java.lang.String r1 = "ActionAddUserSelf"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0424:
            boolean r1 = r20.isOut()
            if (r1 == 0) goto L_0x0436
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r11, r9)
            java.lang.CharSequence r0 = replaceWithLink(r0, r15, r4)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0436:
            int r1 = r6.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            long r11 = r1.getClientUserId()
            int r1 = (r2 > r11 ? 1 : (r2 == r11 ? 0 : -1))
            if (r1 != 0) goto L_0x0487
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r1 = r1.channel_id
            int r3 = (r1 > r18 ? 1 : (r1 == r18 ? 0 : -1))
            if (r3 == 0) goto L_0x0476
            if (r0 == 0) goto L_0x0465
            boolean r0 = r0.megagroup
            if (r0 == 0) goto L_0x0465
            r0 = 2131626300(0x7f0e093c, float:1.8879832E38)
            java.lang.String r1 = "MegaAddedBy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0465:
            r0 = 2131624746(0x7f0e032a, float:1.887668E38)
            java.lang.String r1 = "ChannelAddedBy"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0476:
            r0 = 2131624095(0x7f0e009f, float:1.887536E38)
            java.lang.String r1 = "ActionAddUserYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0487:
            r1 = r17
            r0 = 2131624091(0x7f0e009b, float:1.8875352E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r15, r4)
            r6.messageText = r0
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x049e:
            r1 = r13
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x04bf
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r11, r9)
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r0 = r0.action
            java.util.ArrayList<java.lang.Long> r3 = r0.users
            java.lang.String r2 = "un2"
            r0 = r20
            r4 = r21
            r5 = r23
            java.lang.CharSequence r0 = r0.replaceWithLink(r1, r2, r3, r4, r5)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x04bf:
            r0 = 2131624091(0x7f0e009b, float:1.8875352E38)
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r1, r0)
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
            goto L_0x0f0f
        L_0x04e2:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink
            if (r11 == 0) goto L_0x050a
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x04f9
            r0 = 2131624143(0x7f0e00cf, float:1.8875457E38)
            java.lang.String r1 = "ActionInviteYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x04f9:
            r0 = 2131624142(0x7f0e00ce, float:1.8875455E38)
            java.lang.String r1 = "ActionInviteUser"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x050a:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            if (r11 == 0) goto L_0x0596
            org.telegram.tgnet.TLRPC$Peer r2 = r3.peer_id
            if (r2 == 0) goto L_0x051d
            long r2 = r2.channel_id
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x051d
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r2)
            goto L_0x051e
        L_0x051d:
            r0 = 0
        L_0x051e:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x0548
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x0548
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x053b
            r0 = 2131624119(0x7f0e00b7, float:1.8875409E38)
            java.lang.String r1 = "ActionChannelChangedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x053b:
            r0 = 2131624117(0x7f0e00b5, float:1.8875405E38)
            java.lang.String r1 = "ActionChannelChangedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0548:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x056e
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0561
            r0 = 2131624187(0x7f0e00fb, float:1.8875547E38)
            java.lang.String r1 = "ActionYouChangedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0561:
            r0 = 2131624185(0x7f0e00f9, float:1.8875543E38)
            java.lang.String r1 = "ActionYouChangedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x056e:
            boolean r0 = r20.isVideoAvatar()
            if (r0 == 0) goto L_0x0585
            r0 = 2131624113(0x7f0e00b1, float:1.8875397E38)
            java.lang.String r1 = "ActionChangedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0585:
            r0 = 2131624111(0x7f0e00af, float:1.8875392E38)
            java.lang.String r1 = "ActionChangedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0596:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle
            if (r11 == 0) goto L_0x0603
            org.telegram.tgnet.TLRPC$Peer r2 = r3.peer_id
            if (r2 == 0) goto L_0x05a9
            long r2 = r2.channel_id
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x05a9
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r2)
            goto L_0x05aa
        L_0x05a9:
            r0 = 0
        L_0x05aa:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x05cb
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x05cb
            r0 = 2131624118(0x7f0e00b6, float:1.8875407E38)
            java.lang.String r1 = "ActionChannelChangedTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.lang.String r1 = r1.title
            java.lang.String r0 = r0.replace(r15, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x05cb:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x05e8
            r0 = 2131624186(0x7f0e00fa, float:1.8875545E38)
            java.lang.String r1 = "ActionYouChangedTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.lang.String r1 = r1.title
            java.lang.String r0 = r0.replace(r15, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x05e8:
            r0 = 2131624112(0x7f0e00b0, float:1.8875395E38)
            java.lang.String r1 = "ActionChangedTitle"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r1 = r1.action
            java.lang.String r1 = r1.title
            java.lang.String r0 = r0.replace(r15, r1)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0603:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            if (r11 == 0) goto L_0x0652
            org.telegram.tgnet.TLRPC$Peer r2 = r3.peer_id
            if (r2 == 0) goto L_0x0616
            long r2 = r2.channel_id
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x0616
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r2)
            goto L_0x0617
        L_0x0616:
            r0 = 0
        L_0x0617:
            boolean r1 = org.telegram.messenger.ChatObject.isChannel(r0)
            if (r1 == 0) goto L_0x062e
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x062e
            r0 = 2131624120(0x7f0e00b8, float:1.887541E38)
            java.lang.String r1 = "ActionChannelRemovedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x062e:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x0641
            r0 = 2131624191(0x7f0e00ff, float:1.8875555E38)
            java.lang.String r1 = "ActionYouRemovedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0641:
            r0 = 2131624166(0x7f0e00e6, float:1.8875504E38)
            java.lang.String r1 = "ActionRemovedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0652:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionTTLChange
            r13 = 2131626351(0x7f0e096f, float:1.8879936E38)
            java.lang.String r14 = "MessageLifetimeYouRemoved"
            java.lang.String r15 = "MessageLifetimeRemoved"
            if (r11 == 0) goto L_0x06c8
            int r0 = r9.ttl
            if (r0 == 0) goto L_0x06a6
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x0683
            r0 = 2131626347(0x7f0e096b, float:1.8879928E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.ttl
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "MessageLifetimeChangedOutgoing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0683:
            r3 = 0
            r0 = 2131626346(0x7f0e096a, float:1.8879926E38)
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r1[r3] = r2
            org.telegram.tgnet.TLRPC$Message r2 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r2 = r2.action
            int r2 = r2.ttl
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            r1[r12] = r2
            java.lang.String r2 = "MessageLifetimeChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x06a6:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x06b4
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x06b4:
            java.lang.Object[] r0 = new java.lang.Object[r12]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r2)
            r2 = 0
            r0[r2] = r1
            r1 = 2131626349(0x7f0e096d, float:1.8879932E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r15, r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x06c8:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL
            if (r11 == 0) goto L_0x076d
            org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL r9 = (org.telegram.tgnet.TLRPC$TL_messageActionSetMessagesTTL) r9
            org.telegram.tgnet.TLRPC$Peer r2 = r3.peer_id
            if (r2 == 0) goto L_0x06dd
            long r2 = r2.channel_id
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x06dd
            org.telegram.tgnet.TLRPC$Chat r0 = r6.getChat(r0, r1, r2)
            goto L_0x06de
        L_0x06dd:
            r0 = 0
        L_0x06de:
            if (r0 == 0) goto L_0x070b
            boolean r0 = r0.megagroup
            if (r0 != 0) goto L_0x070b
            int r0 = r9.period
            if (r0 == 0) goto L_0x06fe
            r1 = 2131624169(0x7f0e00e9, float:1.887551E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatTTLString(r0)
            r3 = 0
            r2[r3] = r0
            java.lang.String r0 = "ActionTTLChannelChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x06fe:
            r0 = 2131624170(0x7f0e00ea, float:1.8875512E38)
            java.lang.String r1 = "ActionTTLChannelDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x070b:
            int r0 = r9.period
            if (r0 == 0) goto L_0x0749
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x072d
            r0 = 2131624172(0x7f0e00ec, float:1.8875516E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            int r2 = r9.period
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "ActionTTLYouChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x072d:
            r3 = 0
            r0 = 2131624168(0x7f0e00e8, float:1.8875508E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            int r2 = r9.period
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatTTLString(r2)
            r1[r3] = r2
            java.lang.String r2 = "ActionTTLChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0749:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x075c
            r0 = 2131624173(0x7f0e00ed, float:1.8875518E38)
            java.lang.String r1 = "ActionTTLYouDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x075c:
            r0 = 2131624171(0x7f0e00eb, float:1.8875514E38)
            java.lang.String r1 = "ActionTTLDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x076d:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation
            if (r11 == 0) goto L_0x0802
            int r0 = r3.date
            long r0 = (long) r0
            r2 = 1000(0x3e8, double:4.94E-321)
            long r0 = r0 * r2
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterDay
            if (r2 == 0) goto L_0x07ae
            org.telegram.messenger.LocaleController r2 = org.telegram.messenger.LocaleController.getInstance()
            org.telegram.messenger.time.FastDateFormat r2 = r2.formatterYear
            if (r2 == 0) goto L_0x07ae
            r2 = 2131628860(0x7f0e133c, float:1.8885025E38)
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
            r7[r12] = r0
            java.lang.String r0 = "formatDateAtTime"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r7)
            goto L_0x07c1
        L_0x07ae:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r10)
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            int r1 = r1.date
            r0.append(r1)
            java.lang.String r0 = r0.toString()
        L_0x07c1:
            int r1 = r6.currentAccount
            org.telegram.messenger.UserConfig r1 = org.telegram.messenger.UserConfig.getInstance(r1)
            org.telegram.tgnet.TLRPC$User r1 = r1.getCurrentUser()
            if (r1 != 0) goto L_0x07d7
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            long r1 = r1.user_id
            org.telegram.tgnet.TLRPC$User r1 = r6.getUser(r4, r5, r1)
        L_0x07d7:
            if (r1 == 0) goto L_0x07de
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r1)
            goto L_0x07df
        L_0x07de:
            r1 = r10
        L_0x07df:
            r2 = 2131626721(0x7f0e0ae1, float:1.8880686E38)
            r3 = 4
            java.lang.Object[] r3 = new java.lang.Object[r3]
            r4 = 0
            r3[r4] = r1
            r3[r12] = r0
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
            goto L_0x0f0f
        L_0x0802:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserJoined
            if (r11 != 0) goto L_0x0cd7
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp
            if (r11 == 0) goto L_0x080c
            goto L_0x0cd7
        L_0x080c:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionUserUpdatedPhoto
            if (r11 == 0) goto L_0x0826
            r0 = 2131626652(0x7f0e0a9c, float:1.8880546E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r2)
            r3 = 0
            r1[r3] = r2
            java.lang.String r2 = "NotificationContactNewPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0826:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageEncryptedAction
            if (r11 == 0) goto L_0x08c0
            org.telegram.tgnet.TLRPC$DecryptedMessageAction r0 = r9.encryptedAction
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionScreenshotMessages
            if (r1 == 0) goto L_0x0857
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x0846
            r0 = 2131624175(0x7f0e00ef, float:1.8875522E38)
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = "ActionTakeScreenshootYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0846:
            r0 = 2131624174(0x7f0e00ee, float:1.887552E38)
            java.lang.String r1 = "ActionTakeScreenshoot"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0857:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL
            if (r1 == 0) goto L_0x0f0f
            org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL r0 = (org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL) r0
            int r1 = r0.ttl_seconds
            if (r1 == 0) goto L_0x089e
            boolean r1 = r20.isOut()
            if (r1 == 0) goto L_0x087f
            r1 = 2131626347(0x7f0e096b, float:1.8879928E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            int r0 = r0.ttl_seconds
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatTTLString(r0)
            r3 = 0
            r2[r3] = r0
            java.lang.String r0 = "MessageLifetimeChangedOutgoing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x087f:
            r3 = 0
            r1 = 2131626346(0x7f0e096a, float:1.8879926E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            java.lang.String r2 = org.telegram.messenger.UserObject.getFirstName(r2)
            r4[r3] = r2
            int r0 = r0.ttl_seconds
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatTTLString(r0)
            r4[r12] = r0
            java.lang.String r0 = "MessageLifetimeChanged"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r4)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x089e:
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x08ac
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r14, r13)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x08ac:
            java.lang.Object[] r0 = new java.lang.Object[r12]
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r2)
            r11 = 0
            r0[r11] = r1
            r1 = 2131626349(0x7f0e096d, float:1.8879932E38)
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r15, r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x08c0:
            r11 = 0
            boolean r13 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken
            if (r13 == 0) goto L_0x08eb
            boolean r0 = r20.isOut()
            if (r0 == 0) goto L_0x08da
            r0 = 2131624175(0x7f0e00ef, float:1.8875522E38)
            java.lang.Object[] r1 = new java.lang.Object[r11]
            java.lang.String r2 = "ActionTakeScreenshootYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x08da:
            r0 = 2131624174(0x7f0e00ee, float:1.887552E38)
            java.lang.String r1 = "ActionTakeScreenshoot"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x08eb:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionCreatedBroadcastList
            if (r11 == 0) goto L_0x08ff
            r0 = 2131628803(0x7f0e1303, float:1.888491E38)
            r1 = 0
            java.lang.Object[] r1 = new java.lang.Object[r1]
            java.lang.String r2 = "YouCreatedBroadcastList"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x08ff:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate
            if (r11 == 0) goto L_0x0937
            org.telegram.tgnet.TLRPC$Peer r2 = r3.peer_id
            if (r2 == 0) goto L_0x0912
            long r2 = r2.channel_id
            int r4 = (r2 > r18 ? 1 : (r2 == r18 ? 0 : -1))
            if (r4 == 0) goto L_0x0912
            org.telegram.tgnet.TLRPC$Chat r7 = r6.getChat(r0, r1, r2)
            goto L_0x0913
        L_0x0912:
            r7 = 0
        L_0x0913:
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r7)
            if (r0 == 0) goto L_0x092a
            boolean r0 = r7.megagroup
            if (r0 == 0) goto L_0x092a
            r0 = 2131624123(0x7f0e00bb, float:1.8875417E38)
            java.lang.String r1 = "ActionCreateMega"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x092a:
            r0 = 2131624121(0x7f0e00b9, float:1.8875413E38)
            java.lang.String r1 = "ActionCreateChannel"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0937:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo
            if (r11 == 0) goto L_0x0948
            r0 = 2131624147(0x7f0e00d3, float:1.8875465E38)
            java.lang.String r1 = "ActionMigrateFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0948:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom
            if (r11 == 0) goto L_0x0959
            r0 = 2131624147(0x7f0e00d3, float:1.8875465E38)
            java.lang.String r1 = "ActionMigrateFromGroup"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0959:
            boolean r11 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPinMessage
            if (r11 == 0) goto L_0x096e
            if (r2 != 0) goto L_0x0968
            org.telegram.tgnet.TLRPC$Peer r3 = r3.peer_id
            long r3 = r3.channel_id
            org.telegram.tgnet.TLRPC$Chat r7 = r6.getChat(r0, r1, r3)
            goto L_0x0969
        L_0x0968:
            r7 = 0
        L_0x0969:
            r6.generatePinMessageText(r2, r7)
            goto L_0x0f0f
        L_0x096e:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r0 == 0) goto L_0x097f
            r0 = 2131625917(0x7f0e07bd, float:1.8879055E38)
            java.lang.String r1 = "HistoryCleared"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x097f:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionGameScore
            if (r0 == 0) goto L_0x0988
            r6.generateGameMessageText(r2)
            goto L_0x0f0f
        L_0x0988:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall
            if (r0 == 0) goto L_0x0aa4
            org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall r9 = (org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall) r9
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r0 = r9.reason
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonMissed
            boolean r1 = r20.isFromUser()
            if (r1 == 0) goto L_0x09e8
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.from_id
            long r1 = r1.user_id
            int r3 = r6.currentAccount
            org.telegram.messenger.UserConfig r3 = org.telegram.messenger.UserConfig.getInstance(r3)
            long r3 = r3.getClientUserId()
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x09e8
            if (r0 == 0) goto L_0x09cc
            boolean r0 = r9.video
            if (r0 == 0) goto L_0x09bf
            r0 = 2131624674(0x7f0e02e2, float:1.8876534E38)
            java.lang.String r1 = "CallMessageVideoOutgoingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a43
        L_0x09bf:
            r0 = 2131624668(0x7f0e02dc, float:1.8876522E38)
            java.lang.String r1 = "CallMessageOutgoingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a43
        L_0x09cc:
            boolean r0 = r9.video
            if (r0 == 0) goto L_0x09dc
            r0 = 2131624673(0x7f0e02e1, float:1.8876532E38)
            java.lang.String r1 = "CallMessageVideoOutgoing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a43
        L_0x09dc:
            r0 = 2131624667(0x7f0e02db, float:1.887652E38)
            java.lang.String r1 = "CallMessageOutgoing"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a43
        L_0x09e8:
            if (r0 == 0) goto L_0x0a06
            boolean r0 = r9.video
            if (r0 == 0) goto L_0x09fa
            r0 = 2131624672(0x7f0e02e0, float:1.887653E38)
            java.lang.String r1 = "CallMessageVideoIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a43
        L_0x09fa:
            r0 = 2131624666(0x7f0e02da, float:1.8876518E38)
            java.lang.String r1 = "CallMessageIncomingMissed"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a43
        L_0x0a06:
            org.telegram.tgnet.TLRPC$PhoneCallDiscardReason r0 = r9.reason
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy
            if (r0 == 0) goto L_0x0a28
            boolean r0 = r9.video
            if (r0 == 0) goto L_0x0a1c
            r0 = 2131624671(0x7f0e02df, float:1.8876528E38)
            java.lang.String r1 = "CallMessageVideoIncomingDeclined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a43
        L_0x0a1c:
            r0 = 2131624665(0x7f0e02d9, float:1.8876516E38)
            java.lang.String r1 = "CallMessageIncomingDeclined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a43
        L_0x0a28:
            boolean r0 = r9.video
            if (r0 == 0) goto L_0x0a38
            r0 = 2131624670(0x7f0e02de, float:1.8876526E38)
            java.lang.String r1 = "CallMessageVideoIncoming"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0a43
        L_0x0a38:
            r0 = 2131624664(0x7f0e02d8, float:1.8876514E38)
            java.lang.String r1 = "CallMessageIncoming"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
        L_0x0a43:
            int r0 = r9.duration
            if (r0 <= 0) goto L_0x0f0f
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatCallDuration(r0)
            r1 = 2131624675(0x7f0e02e3, float:1.8876536E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.CharSequence r3 = r6.messageText
            r4 = 0
            r2[r4] = r3
            r2[r12] = r0
            java.lang.String r3 = "CallMessageWithDuration"
            java.lang.String r1 = org.telegram.messenger.LocaleController.formatString(r3, r1, r2)
            r6.messageText = r1
            java.lang.String r1 = r1.toString()
            int r2 = r1.indexOf(r0)
            r3 = -1
            if (r2 == r3) goto L_0x0f0f
            android.text.SpannableString r3 = new android.text.SpannableString
            java.lang.CharSequence r4 = r6.messageText
            r3.<init>(r4)
            int r0 = r0.length()
            int r0 = r0 + r2
            if (r2 <= 0) goto L_0x0a85
            int r4 = r2 + -1
            char r4 = r1.charAt(r4)
            r5 = 40
            if (r4 != r5) goto L_0x0a85
            int r2 = r2 + -1
        L_0x0a85:
            int r4 = r1.length()
            if (r0 >= r4) goto L_0x0a95
            char r1 = r1.charAt(r0)
            r4 = 41
            if (r1 != r4) goto L_0x0a95
            int r0 = r0 + 1
        L_0x0a95:
            org.telegram.ui.Components.TypefaceSpan r1 = new org.telegram.ui.Components.TypefaceSpan
            android.graphics.Typeface r4 = android.graphics.Typeface.DEFAULT
            r1.<init>(r4)
            r4 = 0
            r3.setSpan(r1, r2, r0, r4)
            r6.messageText = r3
            goto L_0x0f0f
        L_0x0aa4:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent
            if (r0 == 0) goto L_0x0ab5
            long r0 = r20.getDialogId()
            org.telegram.tgnet.TLRPC$User r0 = r6.getUser(r4, r5, r0)
            r6.generatePaymentSentMessageText(r0)
            goto L_0x0f0f
        L_0x0ab5:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionBotAllowed
            if (r0 == 0) goto L_0x0b00
            org.telegram.tgnet.TLRPC$TL_messageActionBotAllowed r9 = (org.telegram.tgnet.TLRPC$TL_messageActionBotAllowed) r9
            java.lang.String r0 = r9.domain
            r1 = 2131624096(0x7f0e00a0, float:1.8875362E38)
            java.lang.String r2 = "ActionBotAllowed"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            java.lang.String r2 = "%1$s"
            int r2 = r1.indexOf(r2)
            android.text.SpannableString r3 = new android.text.SpannableString
            java.lang.Object[] r4 = new java.lang.Object[r12]
            r5 = 0
            r4[r5] = r0
            java.lang.String r1 = java.lang.String.format(r1, r4)
            r3.<init>(r1)
            if (r2 < 0) goto L_0x0afc
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
        L_0x0afc:
            r6.messageText = r3
            goto L_0x0f0f
        L_0x0b00:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSecureValuesSent
            if (r0 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageActionSecureValuesSent r9 = (org.telegram.tgnet.TLRPC$TL_messageActionSecureValuesSent) r9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureValueType> r1 = r9.types
            int r1 = r1.size()
            r2 = 0
        L_0x0b12:
            if (r2 >= r1) goto L_0x0c0d
            java.util.ArrayList<org.telegram.tgnet.TLRPC$SecureValueType> r3 = r9.types
            java.lang.Object r3 = r3.get(r2)
            org.telegram.tgnet.TLRPC$SecureValueType r3 = (org.telegram.tgnet.TLRPC$SecureValueType) r3
            int r7 = r0.length()
            if (r7 <= 0) goto L_0x0b27
            java.lang.String r7 = ", "
            r0.append(r7)
        L_0x0b27:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePhone
            if (r7 == 0) goto L_0x0b39
            r3 = 2131624106(0x7f0e00aa, float:1.8875382E38)
            java.lang.String r7 = "ActionBotDocumentPhone"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0b39:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeEmail
            if (r7 == 0) goto L_0x0b4b
            r3 = 2131624100(0x7f0e00a4, float:1.887537E38)
            java.lang.String r7 = "ActionBotDocumentEmail"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0b4b:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress
            if (r7 == 0) goto L_0x0b5d
            r3 = 2131624097(0x7f0e00a1, float:1.8875364E38)
            java.lang.String r7 = "ActionBotDocumentAddress"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0b5d:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails
            if (r7 == 0) goto L_0x0b6f
            r3 = 2131624101(0x7f0e00a5, float:1.8875372E38)
            java.lang.String r7 = "ActionBotDocumentIdentity"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0b6f:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePassport
            if (r7 == 0) goto L_0x0b81
            r3 = 2131624104(0x7f0e00a8, float:1.8875378E38)
            java.lang.String r7 = "ActionBotDocumentPassport"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0b81:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeDriverLicense
            if (r7 == 0) goto L_0x0b93
            r3 = 2131624099(0x7f0e00a3, float:1.8875368E38)
            java.lang.String r7 = "ActionBotDocumentDriverLicence"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0b93:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeIdentityCard
            if (r7 == 0) goto L_0x0ba4
            r3 = 2131624102(0x7f0e00a6, float:1.8875374E38)
            java.lang.String r7 = "ActionBotDocumentIdentityCard"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0ba4:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeUtilityBill
            if (r7 == 0) goto L_0x0bb5
            r3 = 2131624109(0x7f0e00ad, float:1.8875388E38)
            java.lang.String r7 = "ActionBotDocumentUtilityBill"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0bb5:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeBankStatement
            if (r7 == 0) goto L_0x0bc6
            r3 = 2131624098(0x7f0e00a2, float:1.8875366E38)
            java.lang.String r7 = "ActionBotDocumentBankStatement"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0bc6:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeRentalAgreement
            if (r7 == 0) goto L_0x0bd7
            r3 = 2131624107(0x7f0e00ab, float:1.8875384E38)
            java.lang.String r7 = "ActionBotDocumentRentalAgreement"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0bd7:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeInternalPassport
            if (r7 == 0) goto L_0x0be8
            r3 = 2131624103(0x7f0e00a7, float:1.8875376E38)
            java.lang.String r7 = "ActionBotDocumentInternalPassport"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0be8:
            boolean r7 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypePassportRegistration
            if (r7 == 0) goto L_0x0bf9
            r3 = 2131624105(0x7f0e00a9, float:1.887538E38)
            java.lang.String r7 = "ActionBotDocumentPassportRegistration"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
            goto L_0x0CLASSNAME
        L_0x0bf9:
            boolean r3 = r3 instanceof org.telegram.tgnet.TLRPC$TL_secureValueTypeTemporaryRegistration
            if (r3 == 0) goto L_0x0CLASSNAME
            r3 = 2131624108(0x7f0e00ac, float:1.8875386E38)
            java.lang.String r7 = "ActionBotDocumentTemporaryRegistration"
            java.lang.String r3 = org.telegram.messenger.LocaleController.getString(r7, r3)
            r0.append(r3)
        L_0x0CLASSNAME:
            int r2 = r2 + 1
            goto L_0x0b12
        L_0x0c0d:
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r1 = r1.peer_id
            if (r1 == 0) goto L_0x0c1a
            long r1 = r1.user_id
            org.telegram.tgnet.TLRPC$User r7 = r6.getUser(r4, r5, r1)
            goto L_0x0c1b
        L_0x0c1a:
            r7 = 0
        L_0x0c1b:
            r1 = 2131624110(0x7f0e00ae, float:1.887539E38)
            r2 = 2
            java.lang.Object[] r2 = new java.lang.Object[r2]
            java.lang.String r3 = org.telegram.messenger.UserObject.getFirstName(r7)
            r4 = 0
            r2[r4] = r3
            java.lang.String r0 = r0.toString()
            r2[r12] = r0
            java.lang.String r0 = "ActionBotDocuments"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0CLASSNAME:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme
            if (r0 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme r9 = (org.telegram.tgnet.TLRPC$TL_messageActionSetChatTheme) r9
            java.lang.String r0 = r9.emoticon
            java.lang.String r1 = org.telegram.messenger.UserObject.getFirstName(r2)
            boolean r2 = org.telegram.messenger.UserObject.isUserSelf(r2)
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 == 0) goto L_0x0CLASSNAME
            if (r2 == 0) goto L_0x0c5d
            r0 = 2131624900(0x7f0e03c4, float:1.8876993E38)
            r3 = 0
            java.lang.Object[] r1 = new java.lang.Object[r3]
            java.lang.String r2 = "ChatThemeDisabledYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            goto L_0x0c6e
        L_0x0c5d:
            r3 = 0
            r2 = 2131624899(0x7f0e03c3, float:1.887699E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r3] = r1
            r4[r12] = r0
            java.lang.String r0 = "ChatThemeDisabled"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
        L_0x0c6e:
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0CLASSNAME:
            r3 = 0
            if (r2 == 0) goto L_0x0CLASSNAME
            r1 = 2131624897(0x7f0e03c1, float:1.8876987E38)
            java.lang.Object[] r2 = new java.lang.Object[r12]
            r2[r3] = r0
            java.lang.String r0 = "ChatThemeChangedYou"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            goto L_0x0CLASSNAME
        L_0x0CLASSNAME:
            r2 = 2131624896(0x7f0e03c0, float:1.8876985E38)
            r4 = 2
            java.lang.Object[] r4 = new java.lang.Object[r4]
            r4[r3] = r1
            r4[r12] = r0
            java.lang.String r0 = "ChatThemeChangedTo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r2, r4)
        L_0x0CLASSNAME:
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0CLASSNAME:
            boolean r0 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByRequest
            if (r0 == 0) goto L_0x0f0f
            boolean r0 = org.telegram.messenger.UserObject.isUserSelf(r2)
            if (r0 == 0) goto L_0x0cc6
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            long r0 = r0.channel_id
            int r2 = r6.currentAccount
            boolean r0 = org.telegram.messenger.ChatObject.isChannelAndNotMegaGroup(r0, r2)
            if (r0 == 0) goto L_0x0cb9
            r0 = 2131627589(0x7f0e0e45, float:1.8882447E38)
            java.lang.String r1 = "RequestToJoinChannelApproved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            goto L_0x0cc2
        L_0x0cb9:
            r0 = 2131627593(0x7f0e0e49, float:1.8882455E38)
            java.lang.String r1 = "RequestToJoinGroupApproved"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
        L_0x0cc2:
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0cc6:
            r0 = 2131628355(0x7f0e1143, float:1.8884E38)
            java.lang.String r1 = "UserAcceptedToGroupAction"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            java.lang.CharSequence r0 = replaceWithLink(r0, r7, r8)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0cd7:
            r0 = 2131626651(0x7f0e0a9b, float:1.8880544E38)
            java.lang.Object[] r1 = new java.lang.Object[r12]
            java.lang.String r2 = org.telegram.messenger.UserObject.getUserName(r2)
            r4 = 0
            r1[r4] = r2
            java.lang.String r2 = "NotificationContactJoined"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r2, r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0ced:
            r4 = 0
            r6.isRestrictedMessage = r4
            java.util.ArrayList<org.telegram.tgnet.TLRPC$TL_restrictionReason> r0 = r3.restriction_reason
            java.lang.String r0 = org.telegram.messenger.MessagesController.getRestrictionReason(r0)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x0d02
            r6.messageText = r0
            r6.isRestrictedMessage = r12
            goto L_0x0f0f
        L_0x0d02:
            boolean r0 = r20.isMediaEmpty()
            if (r0 != 0) goto L_0x0ed4
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDice
            if (r2 == 0) goto L_0x0d18
            java.lang.String r0 = r20.getDiceEmoji()
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0d18:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r2 == 0) goto L_0x0d3e
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r1 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r1
            org.telegram.tgnet.TLRPC$Poll r0 = r1.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0d31
            r0 = 2131627466(0x7f0e0dca, float:1.8882197E38)
            java.lang.String r1 = "QuizPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0d31:
            r0 = 2131627252(0x7f0e0cf4, float:1.8881763E38)
            java.lang.String r1 = "Poll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0d3e:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r2 == 0) goto L_0x0d64
            int r1 = r1.ttl_seconds
            if (r1 == 0) goto L_0x0d57
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_secret
            if (r0 != 0) goto L_0x0d57
            r0 = 2131624405(0x7f0e01d5, float:1.8875989E38)
            java.lang.String r1 = "AttachDestructingPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0d57:
            r0 = 2131624423(0x7f0e01e7, float:1.8876025E38)
            java.lang.String r1 = "AttachPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0d64:
            boolean r0 = r20.isVideo()
            if (r0 != 0) goto L_0x0eb0
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r0 == 0) goto L_0x0d84
            org.telegram.tgnet.TLRPC$Document r0 = r20.getDocument()
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_documentEmpty
            if (r0 == 0) goto L_0x0d84
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            int r0 = r0.ttl_seconds
            if (r0 == 0) goto L_0x0d84
            goto L_0x0eb0
        L_0x0d84:
            boolean r0 = r20.isVoice()
            if (r0 == 0) goto L_0x0d97
            r0 = 2131624402(0x7f0e01d2, float:1.8875983E38)
            java.lang.String r1 = "AttachAudio"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0d97:
            boolean r0 = r20.isRoundVideo()
            if (r0 == 0) goto L_0x0daa
            r0 = 2131624425(0x7f0e01e9, float:1.887603E38)
            java.lang.String r1 = "AttachRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0daa:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r2 != 0) goto L_0x0ea4
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaVenue
            if (r2 == 0) goto L_0x0db8
            goto L_0x0ea4
        L_0x0db8:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r2 == 0) goto L_0x0dc9
            r0 = 2131624414(0x7f0e01de, float:1.8876007E38)
            java.lang.String r1 = "AttachLiveLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0dc9:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r2 == 0) goto L_0x0df2
            r0 = 2131624404(0x7f0e01d4, float:1.8875987E38)
            java.lang.String r1 = "AttachContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r0 = r0.vcard
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0f0f
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            java.lang.String r0 = r0.vcard
            java.lang.CharSequence r0 = org.telegram.messenger.MessageObject.VCardData.parse(r0)
            r6.vCardData = r0
            goto L_0x0f0f
        L_0x0df2:
            boolean r2 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r2 == 0) goto L_0x0dfc
            java.lang.String r0 = r0.message
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0dfc:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r0 == 0) goto L_0x0e06
            java.lang.String r0 = r1.description
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0e06:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported
            if (r0 == 0) goto L_0x0e17
            r0 = 2131628312(0x7f0e1118, float:1.8883913E38)
            java.lang.String r1 = "UnsupportedMedia"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0e17:
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r0 == 0) goto L_0x0f0f
            boolean r0 = r20.isSticker()
            if (r0 != 0) goto L_0x0e71
            org.telegram.tgnet.TLRPC$Document r0 = r20.getDocument()
            boolean r0 = isAnimatedStickerDocument(r0, r12)
            if (r0 == 0) goto L_0x0e2c
            goto L_0x0e71
        L_0x0e2c:
            boolean r0 = r20.isMusic()
            if (r0 == 0) goto L_0x0e3f
            r0 = 2131624422(0x7f0e01e6, float:1.8876023E38)
            java.lang.String r1 = "AttachMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0e3f:
            boolean r0 = r20.isGif()
            if (r0 == 0) goto L_0x0e52
            r0 = 2131624409(0x7f0e01d9, float:1.8875997E38)
            java.lang.String r1 = "AttachGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0e52:
            org.telegram.tgnet.TLRPC$Document r0 = r20.getDocument()
            java.lang.String r0 = org.telegram.messenger.FileLoader.getDocumentFileName(r0)
            boolean r1 = android.text.TextUtils.isEmpty(r0)
            if (r1 != 0) goto L_0x0e64
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0e64:
            r0 = 2131624407(0x7f0e01d7, float:1.8875993E38)
            java.lang.String r1 = "AttachDocument"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0e71:
            java.lang.String r0 = r20.getStickerChar()
            if (r0 == 0) goto L_0x0e98
            int r1 = r0.length()
            if (r1 <= 0) goto L_0x0e98
            r1 = 2
            java.lang.Object[] r1 = new java.lang.Object[r1]
            r2 = 0
            r1[r2] = r0
            r0 = 2131624426(0x7f0e01ea, float:1.8876031E38)
            java.lang.String r2 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r0)
            r1[r12] = r0
            java.lang.String r0 = "%s %s"
            java.lang.String r0 = java.lang.String.format(r0, r1)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0e98:
            r0 = 2131624426(0x7f0e01ea, float:1.8876031E38)
            java.lang.String r1 = "AttachSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0ea4:
            r0 = 2131624418(0x7f0e01e2, float:1.8876015E38)
            java.lang.String r1 = "AttachLocation"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0eb0:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            int r1 = r1.ttl_seconds
            if (r1 == 0) goto L_0x0ec8
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_secret
            if (r0 != 0) goto L_0x0ec8
            r0 = 2131624406(0x7f0e01d6, float:1.887599E38)
            java.lang.String r1 = "AttachDestructingVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0ec8:
            r0 = 2131624429(0x7f0e01ed, float:1.8876037E38)
            java.lang.String r1 = "AttachVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0ed4:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            java.lang.String r0 = r0.message
            if (r0 == 0) goto L_0x0f0d
            int r0 = r0.length()     // Catch:{ all -> 0x0var_ }
            r1 = 200(0xc8, float:2.8E-43)
            java.lang.String r2 = "‌"
            if (r0 <= r1) goto L_0x0ef5
            java.util.regex.Pattern r0 = org.telegram.messenger.AndroidUtilities.BAD_CHARS_MESSAGE_LONG_PATTERN     // Catch:{ all -> 0x0var_ }
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner     // Catch:{ all -> 0x0var_ }
            java.lang.String r1 = r1.message     // Catch:{ all -> 0x0var_ }
            java.util.regex.Matcher r0 = r0.matcher(r1)     // Catch:{ all -> 0x0var_ }
            java.lang.String r0 = r0.replaceAll(r2)     // Catch:{ all -> 0x0var_ }
            r6.messageText = r0     // Catch:{ all -> 0x0var_ }
            goto L_0x0f0f
        L_0x0ef5:
            java.util.regex.Pattern r0 = org.telegram.messenger.AndroidUtilities.BAD_CHARS_MESSAGE_PATTERN     // Catch:{ all -> 0x0var_ }
            org.telegram.tgnet.TLRPC$Message r1 = r6.messageOwner     // Catch:{ all -> 0x0var_ }
            java.lang.String r1 = r1.message     // Catch:{ all -> 0x0var_ }
            java.util.regex.Matcher r0 = r0.matcher(r1)     // Catch:{ all -> 0x0var_ }
            java.lang.String r0 = r0.replaceAll(r2)     // Catch:{ all -> 0x0var_ }
            r6.messageText = r0     // Catch:{ all -> 0x0var_ }
            goto L_0x0f0f
        L_0x0var_:
            org.telegram.tgnet.TLRPC$Message r0 = r6.messageOwner
            java.lang.String r0 = r0.message
            r6.messageText = r0
            goto L_0x0f0f
        L_0x0f0d:
            r6.messageText = r0
        L_0x0f0f:
            java.lang.CharSequence r0 = r6.messageText
            if (r0 != 0) goto L_0x0var_
            r6.messageText = r10
        L_0x0var_:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.updateMessageText(java.util.AbstractMap, java.util.AbstractMap, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    public void setType() {
        int i = this.type;
        this.type = 1000;
        this.isRoundVideoCached = 0;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if ((tLRPC$Message instanceof TLRPC$TL_message) || (tLRPC$Message instanceof TLRPC$TL_messageForwarded_old2)) {
            if (this.isRestrictedMessage) {
                this.type = 0;
            } else if (this.emojiAnimatedSticker != null) {
                if (isSticker()) {
                    this.type = 13;
                } else {
                    this.type = 15;
                }
            } else if (isMediaEmpty()) {
                this.type = 0;
                if (TextUtils.isEmpty(this.messageText) && this.eventId == 0) {
                    this.messageText = "Empty message";
                }
            } else {
                TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
                if (tLRPC$MessageMedia.ttl_seconds == 0 || (!(tLRPC$MessageMedia.photo instanceof TLRPC$TL_photoEmpty) && !(getDocument() instanceof TLRPC$TL_documentEmpty))) {
                    TLRPC$MessageMedia tLRPC$MessageMedia2 = this.messageOwner.media;
                    if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaDice) {
                        this.type = 15;
                        if (tLRPC$MessageMedia2.document == null) {
                            tLRPC$MessageMedia2.document = new TLRPC$TL_document();
                            TLRPC$Document tLRPC$Document = this.messageOwner.media.document;
                            tLRPC$Document.file_reference = new byte[0];
                            tLRPC$Document.mime_type = "application/x-tgsdice";
                            tLRPC$Document.dc_id = Integer.MIN_VALUE;
                            tLRPC$Document.id = -2147483648L;
                            TLRPC$TL_documentAttributeImageSize tLRPC$TL_documentAttributeImageSize = new TLRPC$TL_documentAttributeImageSize();
                            tLRPC$TL_documentAttributeImageSize.w = 512;
                            tLRPC$TL_documentAttributeImageSize.h = 512;
                            this.messageOwner.media.document.attributes.add(tLRPC$TL_documentAttributeImageSize);
                        }
                    } else if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaPhoto) {
                        this.type = 1;
                    } else if ((tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaGeo) || (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaVenue) || (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaGeoLive)) {
                        this.type = 4;
                    } else if (isRoundVideo()) {
                        this.type = 5;
                    } else if (isVideo()) {
                        this.type = 3;
                    } else if (isVoice()) {
                        this.type = 2;
                    } else if (isMusic()) {
                        this.type = 14;
                    } else {
                        TLRPC$MessageMedia tLRPC$MessageMedia3 = this.messageOwner.media;
                        if (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaContact) {
                            this.type = 12;
                        } else if (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaPoll) {
                            this.type = 17;
                            this.checkedVotes = new ArrayList<>();
                        } else if (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaUnsupported) {
                            this.type = 0;
                        } else if (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaDocument) {
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
                        } else if (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaGame) {
                            this.type = 0;
                        } else if (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaInvoice) {
                            this.type = 0;
                        }
                    }
                } else {
                    this.contentType = 1;
                    this.type = 10;
                }
            }
        } else if (tLRPC$Message instanceof TLRPC$TL_messageService) {
            TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
            if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionLoginUnknownLocation) {
                this.type = 0;
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
        if (i != 1000 && i != this.type) {
            updateMessageText(MessagesController.getInstance(this.currentAccount).getUsers(), MessagesController.getInstance(this.currentAccount).getChats(), (LongSparseArray<TLRPC$User>) null, (LongSparseArray<TLRPC$Chat>) null);
            generateThumbs(false);
        }
    }

    public boolean checkLayout() {
        CharSequence charSequence;
        TextPaint textPaint;
        if (!(this.type != 0 || this.messageOwner.peer_id == null || (charSequence = this.messageText) == null || charSequence.length() == 0)) {
            if (this.layoutCreated) {
                if (Math.abs(this.generatedWithMinSize - (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) > AndroidUtilities.dp(52.0f) || this.generatedWithDensity != AndroidUtilities.density) {
                    this.layoutCreated = false;
                }
            }
            if (!this.layoutCreated) {
                this.layoutCreated = true;
                int[] iArr = null;
                TLRPC$User user = isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id)) : null;
                if (this.messageOwner.media instanceof TLRPC$TL_messageMediaGame) {
                    textPaint = Theme.chat_msgGameTextPaint;
                } else {
                    textPaint = Theme.chat_msgTextPaint;
                }
                if (allowsBigEmoji()) {
                    iArr = new int[1];
                }
                this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
                checkEmojiOnly(iArr);
                generateLayout(user);
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
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaInvoice) {
            TLRPC$WebDocument tLRPC$WebDocument = ((TLRPC$TL_messageMediaInvoice) tLRPC$MessageMedia).photo;
            if (tLRPC$WebDocument != null) {
                return tLRPC$WebDocument.mime_type;
            }
            return "";
        } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
            return "image/jpeg";
        } else {
            if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) || tLRPC$MessageMedia.webpage.photo == null) {
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
        } else if (this.emojiAnimatedSticker == null) {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (tLRPC$MessageMedia != null && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaEmpty)) {
                if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
                    TLRPC$Photo tLRPC$Photo2 = tLRPC$MessageMedia.photo;
                    if (!z || !((arrayList5 = this.photoThumbs) == null || arrayList5.size() == tLRPC$Photo2.sizes.size())) {
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
                    this.photoThumbsObject = this.messageOwner.media.photo;
                } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
                    TLRPC$Document document = getDocument();
                    if (isDocumentHasThumb(document)) {
                        if (!z || (arrayList4 = this.photoThumbs) == null) {
                            ArrayList<TLRPC$PhotoSize> arrayList10 = new ArrayList<>();
                            this.photoThumbs = arrayList10;
                            arrayList10.addAll(document.thumbs);
                        } else if (!arrayList4.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs, document.thumbs);
                        }
                        this.photoThumbsObject = document;
                    }
                } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaGame) {
                    TLRPC$Document tLRPC$Document = tLRPC$MessageMedia.game.document;
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
                    TLRPC$Photo tLRPC$Photo3 = this.messageOwner.media.game.photo;
                    if (tLRPC$Photo3 != null) {
                        if (!z || (arrayList3 = this.photoThumbs2) == null) {
                            this.photoThumbs2 = new ArrayList<>(tLRPC$Photo3.sizes);
                        } else if (!arrayList3.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs2, tLRPC$Photo3.sizes);
                        }
                        this.photoThumbsObject2 = tLRPC$Photo3;
                    }
                    if (this.photoThumbs == null && (arrayList2 = this.photoThumbs2) != null) {
                        this.photoThumbs = arrayList2;
                        this.photoThumbs2 = null;
                        this.photoThumbsObject = this.photoThumbsObject2;
                        this.photoThumbsObject2 = null;
                    }
                } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
                    TLRPC$WebPage tLRPC$WebPage = tLRPC$MessageMedia.webpage;
                    TLRPC$Photo tLRPC$Photo4 = tLRPC$WebPage.photo;
                    TLRPC$Document tLRPC$Document2 = tLRPC$WebPage.document;
                    if (tLRPC$Photo4 != null) {
                        if (!z || (arrayList = this.photoThumbs) == null) {
                            this.photoThumbs = new ArrayList<>(tLRPC$Photo4.sizes);
                        } else if (!arrayList.isEmpty()) {
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
            if (!z || (arrayList6 = this.photoThumbs) == null) {
                ArrayList<TLRPC$PhotoSize> arrayList15 = new ArrayList<>();
                this.photoThumbs = arrayList15;
                arrayList15.addAll(this.emojiAnimatedSticker.thumbs);
            } else if (!arrayList6.isEmpty()) {
                updatePhotoSizeLocations(this.photoThumbs, this.emojiAnimatedSticker.thumbs);
            }
            this.photoThumbsObject = this.emojiAnimatedSticker;
        }
    }

    private static void updatePhotoSizeLocations(ArrayList<TLRPC$PhotoSize> arrayList, ArrayList<TLRPC$PhotoSize> arrayList2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC$PhotoSize tLRPC$PhotoSize = arrayList.get(i);
            if (tLRPC$PhotoSize != null) {
                int size2 = arrayList2.size();
                int i2 = 0;
                while (true) {
                    if (i2 >= size2) {
                        break;
                    }
                    TLRPC$PhotoSize tLRPC$PhotoSize2 = arrayList2.get(i2);
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
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(getDocument(tLRPC$Message));
        }
        if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto)) {
            return tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage ? FileLoader.getAttachFileName(tLRPC$MessageMedia.webpage.document) : "";
        }
        ArrayList<TLRPC$PhotoSize> arrayList = tLRPC$MessageMedia.photo.sizes;
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
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
            return 3;
        }
        return tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto ? 0 : 4;
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

    /* JADX WARNING: Removed duplicated region for block: B:33:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:46:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateLinkDescription() {
        /*
            r10 = this;
            java.lang.CharSequence r0 = r10.linkDescription
            if (r0 == 0) goto L_0x0005
            return
        L_0x0005:
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0050
            org.telegram.tgnet.TLRPC$WebPage r1 = r0.webpage
            boolean r4 = r1 instanceof org.telegram.tgnet.TLRPC$TL_webPage
            if (r4 == 0) goto L_0x0050
            java.lang.String r1 = r1.description
            if (r1 == 0) goto L_0x0050
            android.text.Spannable$Factory r0 = android.text.Spannable.Factory.getInstance()
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            org.telegram.tgnet.TLRPC$WebPage r1 = r1.webpage
            java.lang.String r1 = r1.description
            android.text.Spannable r0 = r0.newSpannable(r1)
            r10.linkDescription = r0
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            java.lang.String r0 = r0.site_name
            if (r0 == 0) goto L_0x0039
            java.lang.String r0 = r0.toLowerCase()
        L_0x0039:
            java.lang.String r1 = "instagram"
            boolean r1 = r1.equals(r0)
            if (r1 == 0) goto L_0x0043
            r0 = 1
            goto L_0x004e
        L_0x0043:
            java.lang.String r1 = "twitter"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x004d
            r0 = 2
            goto L_0x004e
        L_0x004d:
            r0 = 0
        L_0x004e:
            r7 = r0
            goto L_0x0086
        L_0x0050:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x006d
            org.telegram.tgnet.TLRPC$TL_game r1 = r0.game
            java.lang.String r1 = r1.description
            if (r1 == 0) goto L_0x006d
            android.text.Spannable$Factory r0 = android.text.Spannable.Factory.getInstance()
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            org.telegram.tgnet.TLRPC$TL_game r1 = r1.game
            java.lang.String r1 = r1.description
            android.text.Spannable r0 = r0.newSpannable(r1)
            r10.linkDescription = r0
            goto L_0x0085
        L_0x006d:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r1 == 0) goto L_0x0085
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x0085
            android.text.Spannable$Factory r0 = android.text.Spannable.Factory.getInstance()
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            java.lang.String r1 = r1.description
            android.text.Spannable r0 = r0.newSpannable(r1)
            r10.linkDescription = r0
        L_0x0085:
            r7 = 0
        L_0x0086:
            java.lang.CharSequence r0 = r10.linkDescription
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x00d1
            java.lang.CharSequence r0 = r10.linkDescription
            boolean r0 = containsUrls(r0)
            if (r0 == 0) goto L_0x00a2
            java.lang.CharSequence r0 = r10.linkDescription     // Catch:{ Exception -> 0x009e }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x009e }
            org.telegram.messenger.AndroidUtilities.addLinks(r0, r2)     // Catch:{ Exception -> 0x009e }
            goto L_0x00a2
        L_0x009e:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00a2:
            java.lang.CharSequence r0 = r10.linkDescription
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r1, r2, r3)
            r10.linkDescription = r0
            if (r7 == 0) goto L_0x00d1
            boolean r0 = r0 instanceof android.text.Spannable
            if (r0 != 0) goto L_0x00c5
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            java.lang.CharSequence r1 = r10.linkDescription
            r0.<init>(r1)
            r10.linkDescription = r0
        L_0x00c5:
            boolean r4 = r10.isOutOwner()
            java.lang.CharSequence r5 = r10.linkDescription
            r6 = 0
            r8 = 0
            r9 = 0
            addUrlsByPattern(r4, r5, r6, r7, r8, r9)
        L_0x00d1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLinkDescription():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0079, code lost:
        if (r10.messageOwner.send_state == 0) goto L_0x007b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x007f, code lost:
        if (r10.messageOwner.id >= 0) goto L_0x0082;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateCaption() {
        /*
            r10 = this;
            java.lang.CharSequence r0 = r10.caption
            if (r0 != 0) goto L_0x00de
            boolean r0 = r10.isRoundVideo()
            if (r0 == 0) goto L_0x000c
            goto L_0x00de
        L_0x000c:
            boolean r0 = r10.isMediaEmpty()
            if (r0 != 0) goto L_0x00de
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 != 0) goto L_0x00de
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x00de
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            java.lang.String r0 = r0.message
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            r3 = 0
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r1, r2, r3)
            r10.caption = r0
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            int r1 = r0.send_state
            r2 = 1
            if (r1 == 0) goto L_0x0042
            r0 = 0
            goto L_0x0049
        L_0x0042:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r2
        L_0x0049:
            if (r0 != 0) goto L_0x0082
            long r0 = r10.eventId
            r4 = 0
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x0081
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_old
            if (r1 != 0) goto L_0x0081
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_layer68
            if (r1 != 0) goto L_0x0081
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_layer74
            if (r1 != 0) goto L_0x0081
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument_old
            if (r1 != 0) goto L_0x0081
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument_layer68
            if (r1 != 0) goto L_0x0081
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument_layer74
            if (r0 != 0) goto L_0x0081
            boolean r0 = r10.isOut()
            if (r0 == 0) goto L_0x007b
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            int r0 = r0.send_state
            if (r0 != 0) goto L_0x0081
        L_0x007b:
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            int r0 = r0.id
            if (r0 >= 0) goto L_0x0082
        L_0x0081:
            r3 = 1
        L_0x0082:
            if (r3 == 0) goto L_0x00a6
            java.lang.CharSequence r0 = r10.caption
            boolean r0 = containsUrls(r0)
            if (r0 == 0) goto L_0x0099
            java.lang.CharSequence r0 = r10.caption     // Catch:{ Exception -> 0x0095 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x0095 }
            r1 = 5
            org.telegram.messenger.AndroidUtilities.addLinks(r0, r1)     // Catch:{ Exception -> 0x0095 }
            goto L_0x0099
        L_0x0095:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0099:
            boolean r4 = r10.isOutOwner()
            java.lang.CharSequence r5 = r10.caption
            r6 = 1
            r7 = 0
            r8 = 0
            r9 = 1
            addUrlsByPattern(r4, r5, r6, r7, r8, r9)
        L_0x00a6:
            java.lang.CharSequence r0 = r10.caption
            r10.addEntitiesToText(r0, r3)
            boolean r0 = r10.isVideo()
            if (r0 == 0) goto L_0x00c2
            boolean r1 = r10.isOutOwner()
            java.lang.CharSequence r2 = r10.caption
            r3 = 1
            r4 = 3
            int r5 = r10.getDuration()
            r6 = 0
            addUrlsByPattern(r1, r2, r3, r4, r5, r6)
            goto L_0x00de
        L_0x00c2:
            boolean r0 = r10.isMusic()
            if (r0 != 0) goto L_0x00ce
            boolean r0 = r10.isVoice()
            if (r0 == 0) goto L_0x00de
        L_0x00ce:
            boolean r1 = r10.isOutOwner()
            java.lang.CharSequence r2 = r10.caption
            r3 = 1
            r4 = 4
            int r5 = r10.getDuration()
            r6 = 0
            addUrlsByPattern(r1, r2, r3, r4, r5, r6)
        L_0x00de:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateCaption():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:77:0x01d6 A[ADDED_TO_REGION, Catch:{ Exception -> 0x01ef }] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0049 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void addUrlsByPattern(boolean r16, java.lang.CharSequence r17, boolean r18, int r19, int r20, boolean r21) {
        /*
            r0 = r17
            r1 = r19
            r2 = 4
            r3 = 3
            r4 = 1
            if (r1 == r3) goto L_0x0034
            if (r1 != r2) goto L_0x000c
            goto L_0x0034
        L_0x000c:
            if (r1 != r4) goto L_0x0021
            java.util.regex.Pattern r5 = instagramUrlPattern     // Catch:{ Exception -> 0x01ef }
            if (r5 != 0) goto L_0x001a
            java.lang.String r5 = "(^|\\s|\\()@[a-zA-Z\\d_.]{1,32}|(^|\\s|\\()#[\\w.]+"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x01ef }
            instagramUrlPattern = r5     // Catch:{ Exception -> 0x01ef }
        L_0x001a:
            java.util.regex.Pattern r5 = instagramUrlPattern     // Catch:{ Exception -> 0x01ef }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x01ef }
            goto L_0x0046
        L_0x0021:
            java.util.regex.Pattern r5 = urlPattern     // Catch:{ Exception -> 0x01ef }
            if (r5 != 0) goto L_0x002d
            java.lang.String r5 = "(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s|\\()@[a-zA-Z\\d_]{1,32}|(^|\\s|\\()#[^0-9][\\w.]+|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x01ef }
            urlPattern = r5     // Catch:{ Exception -> 0x01ef }
        L_0x002d:
            java.util.regex.Pattern r5 = urlPattern     // Catch:{ Exception -> 0x01ef }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x01ef }
            goto L_0x0046
        L_0x0034:
            java.util.regex.Pattern r5 = videoTimeUrlPattern     // Catch:{ Exception -> 0x01ef }
            if (r5 != 0) goto L_0x0040
            java.lang.String r5 = "\\b(?:(\\d{1,2}):)?(\\d{1,3}):([0-5][0-9])\\b"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x01ef }
            videoTimeUrlPattern = r5     // Catch:{ Exception -> 0x01ef }
        L_0x0040:
            java.util.regex.Pattern r5 = videoTimeUrlPattern     // Catch:{ Exception -> 0x01ef }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x01ef }
        L_0x0046:
            r6 = r0
            android.text.Spannable r6 = (android.text.Spannable) r6     // Catch:{ Exception -> 0x01ef }
        L_0x0049:
            boolean r7 = r5.find()     // Catch:{ Exception -> 0x01ef }
            if (r7 == 0) goto L_0x01f3
            int r7 = r5.start()     // Catch:{ Exception -> 0x01ef }
            int r8 = r5.end()     // Catch:{ Exception -> 0x01ef }
            r9 = 0
            r10 = 0
            r11 = 2
            if (r1 == r3) goto L_0x0141
            if (r1 != r2) goto L_0x0060
            goto L_0x0141
        L_0x0060:
            char r12 = r0.charAt(r7)     // Catch:{ Exception -> 0x01ef }
            r13 = 47
            r14 = 35
            r15 = 64
            if (r1 == 0) goto L_0x007b
            if (r12 == r15) goto L_0x0072
            if (r12 == r14) goto L_0x0072
            int r7 = r7 + 1
        L_0x0072:
            char r12 = r0.charAt(r7)     // Catch:{ Exception -> 0x01ef }
            if (r12 == r15) goto L_0x0087
            if (r12 == r14) goto L_0x0087
            goto L_0x0049
        L_0x007b:
            if (r12 == r15) goto L_0x0087
            if (r12 == r14) goto L_0x0087
            if (r12 == r13) goto L_0x0087
            r14 = 36
            if (r12 == r14) goto L_0x0087
            int r7 = r7 + 1
        L_0x0087:
            if (r1 != r4) goto L_0x00ce
            if (r12 != r15) goto L_0x00ad
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01ef }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ef }
            r11.<init>()     // Catch:{ Exception -> 0x01ef }
            java.lang.String r12 = "https://instagram.com/"
            r11.append(r12)     // Catch:{ Exception -> 0x01ef }
            int r12 = r7 + 1
            java.lang.CharSequence r12 = r0.subSequence(r12, r8)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x01ef }
            r11.append(r12)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01ef }
            r9.<init>(r11)     // Catch:{ Exception -> 0x01ef }
            goto L_0x012e
        L_0x00ad:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01ef }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ef }
            r11.<init>()     // Catch:{ Exception -> 0x01ef }
            java.lang.String r12 = "https://www.instagram.com/explore/tags/"
            r11.append(r12)     // Catch:{ Exception -> 0x01ef }
            int r12 = r7 + 1
            java.lang.CharSequence r12 = r0.subSequence(r12, r8)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x01ef }
            r11.append(r12)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01ef }
            r9.<init>(r11)     // Catch:{ Exception -> 0x01ef }
            goto L_0x012e
        L_0x00ce:
            if (r1 != r11) goto L_0x0114
            if (r12 != r15) goto L_0x00f3
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01ef }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ef }
            r11.<init>()     // Catch:{ Exception -> 0x01ef }
            java.lang.String r12 = "https://twitter.com/"
            r11.append(r12)     // Catch:{ Exception -> 0x01ef }
            int r12 = r7 + 1
            java.lang.CharSequence r12 = r0.subSequence(r12, r8)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x01ef }
            r11.append(r12)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01ef }
            r9.<init>(r11)     // Catch:{ Exception -> 0x01ef }
            goto L_0x012e
        L_0x00f3:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01ef }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ef }
            r11.<init>()     // Catch:{ Exception -> 0x01ef }
            java.lang.String r12 = "https://twitter.com/hashtag/"
            r11.append(r12)     // Catch:{ Exception -> 0x01ef }
            int r12 = r7 + 1
            java.lang.CharSequence r12 = r0.subSequence(r12, r8)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x01ef }
            r11.append(r12)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01ef }
            r9.<init>(r11)     // Catch:{ Exception -> 0x01ef }
            goto L_0x012e
        L_0x0114:
            char r11 = r0.charAt(r7)     // Catch:{ Exception -> 0x01ef }
            if (r11 != r13) goto L_0x0133
            if (r18 == 0) goto L_0x012e
            org.telegram.ui.Components.URLSpanBotCommand r9 = new org.telegram.ui.Components.URLSpanBotCommand     // Catch:{ Exception -> 0x01ef }
            java.lang.CharSequence r11 = r0.subSequence(r7, r8)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01ef }
            if (r16 == 0) goto L_0x012a
            r12 = 1
            goto L_0x012b
        L_0x012a:
            r12 = 0
        L_0x012b:
            r9.<init>(r11, r12)     // Catch:{ Exception -> 0x01ef }
        L_0x012e:
            r11 = r9
            r9 = r20
            goto L_0x01d4
        L_0x0133:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01ef }
            java.lang.CharSequence r11 = r0.subSequence(r7, r8)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01ef }
            r9.<init>(r11)     // Catch:{ Exception -> 0x01ef }
            goto L_0x012e
        L_0x0141:
            java.lang.Class<android.text.style.URLSpan> r9 = android.text.style.URLSpan.class
            java.lang.Object[] r9 = r6.getSpans(r7, r8, r9)     // Catch:{ Exception -> 0x01ef }
            android.text.style.URLSpan[] r9 = (android.text.style.URLSpan[]) r9     // Catch:{ Exception -> 0x01ef }
            if (r9 == 0) goto L_0x0150
            int r9 = r9.length     // Catch:{ Exception -> 0x01ef }
            if (r9 <= 0) goto L_0x0150
            goto L_0x0049
        L_0x0150:
            r5.groupCount()     // Catch:{ Exception -> 0x01ef }
            int r9 = r5.start(r4)     // Catch:{ Exception -> 0x01ef }
            int r12 = r5.end(r4)     // Catch:{ Exception -> 0x01ef }
            int r13 = r5.start(r11)     // Catch:{ Exception -> 0x01ef }
            int r11 = r5.end(r11)     // Catch:{ Exception -> 0x01ef }
            int r14 = r5.start(r3)     // Catch:{ Exception -> 0x01ef }
            int r15 = r5.end(r3)     // Catch:{ Exception -> 0x01ef }
            java.lang.CharSequence r11 = r0.subSequence(r13, r11)     // Catch:{ Exception -> 0x01ef }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)     // Catch:{ Exception -> 0x01ef }
            int r11 = r11.intValue()     // Catch:{ Exception -> 0x01ef }
            java.lang.CharSequence r13 = r0.subSequence(r14, r15)     // Catch:{ Exception -> 0x01ef }
            java.lang.Integer r13 = org.telegram.messenger.Utilities.parseInt(r13)     // Catch:{ Exception -> 0x01ef }
            int r13 = r13.intValue()     // Catch:{ Exception -> 0x01ef }
            if (r9 < 0) goto L_0x0194
            if (r12 < 0) goto L_0x0194
            java.lang.CharSequence r9 = r0.subSequence(r9, r12)     // Catch:{ Exception -> 0x01ef }
            java.lang.Integer r9 = org.telegram.messenger.Utilities.parseInt(r9)     // Catch:{ Exception -> 0x01ef }
            int r9 = r9.intValue()     // Catch:{ Exception -> 0x01ef }
            goto L_0x0195
        L_0x0194:
            r9 = -1
        L_0x0195:
            int r11 = r11 * 60
            int r13 = r13 + r11
            if (r9 <= 0) goto L_0x019f
            int r9 = r9 * 60
            int r9 = r9 * 60
            int r13 = r13 + r9
        L_0x019f:
            r9 = r20
            if (r13 <= r9) goto L_0x01a5
            goto L_0x0049
        L_0x01a5:
            if (r1 != r3) goto L_0x01be
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01ef }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ef }
            r12.<init>()     // Catch:{ Exception -> 0x01ef }
            java.lang.String r14 = "video?"
            r12.append(r14)     // Catch:{ Exception -> 0x01ef }
            r12.append(r13)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x01ef }
            r11.<init>(r12)     // Catch:{ Exception -> 0x01ef }
            goto L_0x01d4
        L_0x01be:
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01ef }
            java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01ef }
            r12.<init>()     // Catch:{ Exception -> 0x01ef }
            java.lang.String r14 = "audio?"
            r12.append(r14)     // Catch:{ Exception -> 0x01ef }
            r12.append(r13)     // Catch:{ Exception -> 0x01ef }
            java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x01ef }
            r11.<init>(r12)     // Catch:{ Exception -> 0x01ef }
        L_0x01d4:
            if (r11 == 0) goto L_0x0049
            if (r21 == 0) goto L_0x01ea
            java.lang.Class<android.text.style.ClickableSpan> r12 = android.text.style.ClickableSpan.class
            java.lang.Object[] r12 = r6.getSpans(r7, r8, r12)     // Catch:{ Exception -> 0x01ef }
            android.text.style.ClickableSpan[] r12 = (android.text.style.ClickableSpan[]) r12     // Catch:{ Exception -> 0x01ef }
            if (r12 == 0) goto L_0x01ea
            int r13 = r12.length     // Catch:{ Exception -> 0x01ef }
            if (r13 <= 0) goto L_0x01ea
            r12 = r12[r10]     // Catch:{ Exception -> 0x01ef }
            r6.removeSpan(r12)     // Catch:{ Exception -> 0x01ef }
        L_0x01ea:
            r6.setSpan(r11, r7, r8, r10)     // Catch:{ Exception -> 0x01ef }
            goto L_0x0049
        L_0x01ef:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01f3:
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

    /* JADX WARNING: Removed duplicated region for block: B:116:0x0171  */
    /* JADX WARNING: Removed duplicated region for block: B:138:0x020c  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x03cb  */
    /* JADX WARNING: Removed duplicated region for block: B:195:0x03df  */
    /* JADX WARNING: Removed duplicated region for block: B:200:0x0211 A[SYNTHETIC] */
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
            org.telegram.messenger.MessageObject$$ExternalSyntheticLambda1 r7 = org.telegram.messenger.MessageObject$$ExternalSyntheticLambda1.INSTANCE
            java.util.Collections.sort(r6, r7)
            int r7 = r6.size()
            r8 = 0
        L_0x0045:
            r14 = 0
            if (r8 >= r7) goto L_0x0216
            java.lang.Object r15 = r6.get(r8)
            org.telegram.tgnet.TLRPC$MessageEntity r15 = (org.telegram.tgnet.TLRPC$MessageEntity) r15
            int r2 = r15.length
            if (r2 <= 0) goto L_0x0210
            int r2 = r15.offset
            if (r2 < 0) goto L_0x0210
            int r12 = r17.length()
            if (r2 < r12) goto L_0x005e
            goto L_0x0210
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
            if (r22 == 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBold
            if (r2 != 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityItalic
            if (r2 != 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityStrike
            if (r2 != 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUnderline
            if (r2 != 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote
            if (r2 != 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCode
            if (r2 != 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPre
            if (r2 != 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMentionName
            if (r2 != 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            if (r2 != 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r2 != 0) goto L_0x00a0
            boolean r2 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler
            if (r2 == 0) goto L_0x00d4
        L_0x00a0:
            if (r3 == 0) goto L_0x00d4
            int r2 = r3.length
            if (r2 <= 0) goto L_0x00d4
            r2 = 0
        L_0x00a6:
            int r12 = r3.length
            if (r2 >= r12) goto L_0x00d4
            r12 = r3[r2]
            if (r12 != 0) goto L_0x00ae
            goto L_0x00d1
        L_0x00ae:
            r12 = r3[r2]
            int r12 = r1.getSpanStart(r12)
            r5 = r3[r2]
            int r5 = r1.getSpanEnd(r5)
            int r9 = r15.offset
            if (r9 > r12) goto L_0x00c3
            int r13 = r15.length
            int r13 = r13 + r9
            if (r13 >= r12) goto L_0x00ca
        L_0x00c3:
            if (r9 > r5) goto L_0x00d1
            int r12 = r15.length
            int r9 = r9 + r12
            if (r9 < r5) goto L_0x00d1
        L_0x00ca:
            r5 = r3[r2]
            r1.removeSpan(r5)
            r3[r2] = r14
        L_0x00d1:
            int r2 = r2 + 1
            goto L_0x00a6
        L_0x00d4:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r2 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r2.<init>()
            int r5 = r15.offset
            r2.start = r5
            int r9 = r15.length
            int r5 = r5 + r9
            r2.end = r5
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntitySpoiler
            if (r5 == 0) goto L_0x00ed
            r5 = 256(0x100, float:3.59E-43)
            r2.flags = r5
        L_0x00ea:
            r5 = 2
            goto L_0x016a
        L_0x00ed:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityStrike
            if (r5 == 0) goto L_0x00f6
            r5 = 8
            r2.flags = r5
            goto L_0x00ea
        L_0x00f6:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUnderline
            if (r5 == 0) goto L_0x00ff
            r5 = 16
            r2.flags = r5
            goto L_0x00ea
        L_0x00ff:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote
            if (r5 == 0) goto L_0x0108
            r5 = 32
            r2.flags = r5
            goto L_0x00ea
        L_0x0108:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBold
            if (r5 == 0) goto L_0x0110
            r5 = 1
            r2.flags = r5
            goto L_0x00ea
        L_0x0110:
            boolean r5 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityItalic
            if (r5 == 0) goto L_0x0118
            r5 = 2
            r2.flags = r5
            goto L_0x016a
        L_0x0118:
            r5 = 2
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCode
            if (r9 != 0) goto L_0x0167
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPre
            if (r9 == 0) goto L_0x0122
            goto L_0x0167
        L_0x0122:
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMentionName
            r12 = 64
            if (r9 == 0) goto L_0x0131
            if (r20 != 0) goto L_0x012c
            goto L_0x0210
        L_0x012c:
            r2.flags = r12
            r2.urlEntity = r15
            goto L_0x016a
        L_0x0131:
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            if (r9 == 0) goto L_0x013e
            if (r20 != 0) goto L_0x0139
            goto L_0x0210
        L_0x0139:
            r2.flags = r12
            r2.urlEntity = r15
            goto L_0x016a
        L_0x013e:
            if (r22 == 0) goto L_0x0146
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r9 != 0) goto L_0x0146
            goto L_0x0210
        L_0x0146:
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            if (r9 != 0) goto L_0x014e
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r9 == 0) goto L_0x0158
        L_0x014e:
            java.lang.String r9 = r15.url
            boolean r9 = org.telegram.messenger.browser.Browser.isPassportUrl(r9)
            if (r9 == 0) goto L_0x0158
            goto L_0x0210
        L_0x0158:
            boolean r9 = r15 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMention
            if (r9 == 0) goto L_0x0160
            if (r20 != 0) goto L_0x0160
            goto L_0x0210
        L_0x0160:
            r9 = 128(0x80, float:1.794E-43)
            r2.flags = r9
            r2.urlEntity = r15
            goto L_0x016a
        L_0x0167:
            r9 = 4
            r2.flags = r9
        L_0x016a:
            int r9 = r11.size()
            r12 = 0
        L_0x016f:
            if (r12 >= r9) goto L_0x0205
            java.lang.Object r13 = r11.get(r12)
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r13 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r13
            int r14 = r2.start
            int r15 = r13.start
            if (r14 <= r15) goto L_0x01c1
            int r15 = r13.end
            if (r14 < r15) goto L_0x0182
            goto L_0x01c5
        L_0x0182:
            int r14 = r2.end
            if (r14 >= r15) goto L_0x01a5
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
            goto L_0x01b8
        L_0x01a5:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r14.<init>(r2)
            r14.merge(r13)
            int r15 = r13.end
            r14.end = r15
            int r12 = r12 + 1
            int r9 = r9 + 1
            r11.add(r12, r14)
        L_0x01b8:
            int r14 = r2.start
            int r15 = r13.end
            r2.start = r15
            r13.end = r14
            goto L_0x01c5
        L_0x01c1:
            int r14 = r2.end
            if (r15 < r14) goto L_0x01c8
        L_0x01c5:
            r5 = r9
            r9 = 1
            goto L_0x0200
        L_0x01c8:
            int r5 = r13.end
            if (r14 != r5) goto L_0x01d0
            r13.merge(r2)
            goto L_0x01fd
        L_0x01d0:
            if (r14 >= r5) goto L_0x01ea
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
            goto L_0x01fd
        L_0x01ea:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r5 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r5.<init>(r2)
            int r14 = r13.end
            r5.start = r14
            int r12 = r12 + 1
            int r9 = r9 + 1
            r11.add(r12, r5)
            r13.merge(r2)
        L_0x01fd:
            r2.end = r15
            goto L_0x01c5
        L_0x0200:
            int r12 = r12 + r9
            r9 = r5
            r5 = 2
            goto L_0x016f
        L_0x0205:
            r9 = 1
            int r5 = r2.start
            int r12 = r2.end
            if (r5 >= r12) goto L_0x0211
            r11.add(r2)
            goto L_0x0211
        L_0x0210:
            r9 = 1
        L_0x0211:
            int r8 = r8 + 1
            r2 = 0
            goto L_0x0045
        L_0x0216:
            r9 = 1
            int r2 = r11.size()
            r12 = r4
            r13 = 0
        L_0x021d:
            if (r13 >= r2) goto L_0x03e7
            java.lang.Object r3 = r11.get(r13)
            r15 = r3
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r15 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r15
            org.telegram.tgnet.TLRPC$MessageEntity r3 = r15.urlEntity
            if (r3 == 0) goto L_0x0234
            int r4 = r3.offset
            int r3 = r3.length
            int r3 = r3 + r4
            java.lang.String r3 = android.text.TextUtils.substring(r0, r4, r3)
            goto L_0x0235
        L_0x0234:
            r3 = r14
        L_0x0235:
            org.telegram.tgnet.TLRPC$MessageEntity r4 = r15.urlEntity
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBotCommand
            r8 = 33
            if (r5 == 0) goto L_0x024f
            org.telegram.ui.Components.URLSpanBotCommand r4 = new org.telegram.ui.Components.URLSpanBotCommand
            r4.<init>(r3, r10, r15)
            int r3 = r15.start
            int r5 = r15.end
            r1.setSpan(r4, r3, r5, r8)
        L_0x0249:
            r14 = 33
            r16 = 4
            goto L_0x03c8
        L_0x024f:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityHashtag
            if (r5 != 0) goto L_0x03b8
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMention
            if (r5 != 0) goto L_0x03b8
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCashtag
            if (r5 == 0) goto L_0x025d
            goto L_0x03b8
        L_0x025d:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityEmail
            if (r5 == 0) goto L_0x027f
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
            goto L_0x0249
        L_0x027f:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            if (r5 == 0) goto L_0x02ba
            java.lang.String r4 = r3.toLowerCase()
            java.lang.String r5 = "://"
            boolean r4 = r4.contains(r5)
            if (r4 != 0) goto L_0x02ad
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
            goto L_0x02db
        L_0x02ad:
            org.telegram.ui.Components.URLSpanBrowser r4 = new org.telegram.ui.Components.URLSpanBrowser
            r4.<init>(r3, r15)
            int r3 = r15.start
            int r5 = r15.end
            r1.setSpan(r4, r3, r5, r8)
            goto L_0x02db
        L_0x02ba:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBankCard
            if (r5 == 0) goto L_0x02e3
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "card:"
            r5.append(r6)
            r5.append(r3)
            java.lang.String r3 = r5.toString()
            r4.<init>(r3, r15)
            int r3 = r15.start
            int r5 = r15.end
            r1.setSpan(r4, r3, r5, r8)
        L_0x02db:
            r5 = 0
            r12 = 1
            r14 = 33
            r16 = 4
            goto L_0x03c9
        L_0x02e3:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPhone
            if (r5 == 0) goto L_0x0320
            java.lang.String r4 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r3)
            java.lang.String r5 = "+"
            boolean r3 = r3.startsWith(r5)
            if (r3 == 0) goto L_0x0302
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r3.append(r5)
            r3.append(r4)
            java.lang.String r4 = r3.toString()
        L_0x0302:
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
            goto L_0x02db
        L_0x0320:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r3 == 0) goto L_0x0336
            org.telegram.ui.Components.URLSpanReplacement r3 = new org.telegram.ui.Components.URLSpanReplacement
            org.telegram.tgnet.TLRPC$MessageEntity r4 = r15.urlEntity
            java.lang.String r4 = r4.url
            r3.<init>(r4, r15)
            int r4 = r15.start
            int r5 = r15.end
            r1.setSpan(r3, r4, r5, r8)
            goto L_0x0249
        L_0x0336:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMentionName
            java.lang.String r5 = ""
            if (r3 == 0) goto L_0x035f
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
            goto L_0x0249
        L_0x035f:
            boolean r3 = r4 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            if (r3 == 0) goto L_0x0388
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
            goto L_0x0249
        L_0x0388:
            int r3 = r15.flags
            r16 = 4
            r3 = r3 & 4
            if (r3 == 0) goto L_0x03a8
            org.telegram.ui.Components.URLSpanMono r7 = new org.telegram.ui.Components.URLSpanMono
            int r5 = r15.start
            int r6 = r15.end
            r3 = r7
            r4 = r1
            r9 = r7
            r7 = r10
            r14 = 33
            r8 = r15
            r3.<init>(r4, r5, r6, r7, r8)
            int r3 = r15.start
            int r4 = r15.end
            r1.setSpan(r9, r3, r4, r14)
            goto L_0x03c8
        L_0x03a8:
            r14 = 33
            org.telegram.ui.Components.TextStyleSpan r3 = new org.telegram.ui.Components.TextStyleSpan
            r3.<init>(r15)
            int r4 = r15.start
            int r5 = r15.end
            r1.setSpan(r3, r4, r5, r14)
            r5 = 1
            goto L_0x03c9
        L_0x03b8:
            r14 = 33
            r16 = 4
            org.telegram.ui.Components.URLSpanNoUnderline r4 = new org.telegram.ui.Components.URLSpanNoUnderline
            r4.<init>(r3, r15)
            int r3 = r15.start
            int r5 = r15.end
            r1.setSpan(r4, r3, r5, r14)
        L_0x03c8:
            r5 = 0
        L_0x03c9:
            if (r5 != 0) goto L_0x03df
            int r3 = r15.flags
            r4 = 256(0x100, float:3.59E-43)
            r3 = r3 & r4
            if (r3 == 0) goto L_0x03e1
            org.telegram.ui.Components.TextStyleSpan r3 = new org.telegram.ui.Components.TextStyleSpan
            r3.<init>(r15)
            int r5 = r15.start
            int r6 = r15.end
            r1.setSpan(r3, r5, r6, r14)
            goto L_0x03e1
        L_0x03df:
            r4 = 256(0x100, float:3.59E-43)
        L_0x03e1:
            int r13 = r13 + 1
            r9 = 1
            r14 = 0
            goto L_0x021d
        L_0x03e7:
            return r12
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addEntitiesToText(java.lang.CharSequence, java.util.ArrayList, boolean, boolean, boolean, boolean):boolean");
    }

    /* access modifiers changed from: private */
    public static /* synthetic */ int lambda$addEntitiesToText$0(TLRPC$MessageEntity tLRPC$MessageEntity, TLRPC$MessageEntity tLRPC$MessageEntity2) {
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
        if (!(i2 == 13 || i2 == 15)) {
            TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
            if (tLRPC$MessageFwdHeader != null && (tLRPC$MessageFwdHeader.from_id instanceof TLRPC$TL_peerChannel) && !isOutOwner()) {
                return true;
            }
            if (isFromUser()) {
                TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
                if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaEmpty) || tLRPC$MessageMedia == null || ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) && !(tLRPC$MessageMedia.webpage instanceof TLRPC$TL_webPage))) {
                    return false;
                }
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
                if (user != null && user.bot) {
                    return true;
                }
                if (!isOut()) {
                    TLRPC$Message tLRPC$Message2 = this.messageOwner;
                    TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message2.media;
                    if ((tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaGame) || (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaInvoice)) {
                        return true;
                    }
                    TLRPC$Peer tLRPC$Peer = tLRPC$Message2.peer_id;
                    TLRPC$Chat tLRPC$Chat = null;
                    if (tLRPC$Peer != null) {
                        long j = tLRPC$Peer.channel_id;
                        if (j != 0) {
                            tLRPC$Chat = getChat((AbstractMap<Long, TLRPC$Chat>) null, (LongSparseArray<TLRPC$Chat>) null, j);
                        }
                    }
                    if (!ChatObject.isChannel(tLRPC$Chat) || !tLRPC$Chat.megagroup || (str = tLRPC$Chat.username) == null || str.length() <= 0) {
                        return false;
                    }
                    TLRPC$MessageMedia tLRPC$MessageMedia3 = this.messageOwner.media;
                    if ((tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaContact) || (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaGeo)) {
                        return false;
                    }
                    return true;
                }
            } else {
                TLRPC$Message tLRPC$Message3 = this.messageOwner;
                if ((!(tLRPC$Message3.from_id instanceof TLRPC$TL_peerChannel) && !tLRPC$Message3.post) || isSupergroup()) {
                    return false;
                }
                TLRPC$Message tLRPC$Message4 = this.messageOwner;
                if (tLRPC$Message4.peer_id.channel_id == 0 || ((tLRPC$Message4.via_bot_id != 0 || tLRPC$Message4.reply_to != null) && ((i = this.type) == 13 || i == 15))) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r0.webpage;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isYouTubeVideo() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r1 == 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x0026
            java.lang.String r0 = r0.embed_url
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x0026
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            java.lang.String r0 = r0.site_name
            java.lang.String r1 = "YouTube"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0026
            r0 = 1
            goto L_0x0027
        L_0x0026:
            r0 = 0
        L_0x0027:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isYouTubeVideo():boolean");
    }

    public int getMaxMessageTextWidth() {
        TLRPC$WebPage tLRPC$WebPage;
        if (!AndroidUtilities.isTablet() || this.eventId == 0) {
            this.generatedWithMinSize = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : getParentWidth();
        } else {
            this.generatedWithMinSize = AndroidUtilities.dp(530.0f);
        }
        this.generatedWithDensity = AndroidUtilities.density;
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        int i = 0;
        if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) && (tLRPC$WebPage = tLRPC$MessageMedia.webpage) != null && "telegram_background".equals(tLRPC$WebPage.type)) {
            try {
                Uri parse = Uri.parse(this.messageOwner.media.webpage.url);
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
        if (i != 0) {
            return i;
        }
        int dp = this.generatedWithMinSize - AndroidUtilities.dp((!needDrawAvatarInternal() || isOutOwner() || this.messageOwner.isThreadMessage) ? 80.0f : 132.0f);
        if (needDrawShareButton() && !isOutOwner()) {
            dp -= AndroidUtilities.dp(10.0f);
        }
        int i2 = dp;
        return this.messageOwner.media instanceof TLRPC$TL_messageMediaGame ? i2 - AndroidUtilities.dp(10.0f) : i2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0077, code lost:
        if ((r0.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported) == false) goto L_0x007b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:152:0x032a  */
    /* JADX WARNING: Removed duplicated region for block: B:155:0x032f  */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0340  */
    /* JADX WARNING: Removed duplicated region for block: B:198:0x0407  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0098  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:66:0x0106 A[Catch:{ Exception -> 0x0487 }] */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x0124 A[Catch:{ Exception -> 0x0487 }] */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x0145  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0147  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0159  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateLayout(org.telegram.tgnet.TLRPC$User r30) {
        /*
            r29 = this;
            r1 = r29
            int r0 = r1.type
            if (r0 != 0) goto L_0x048b
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.peer_id
            if (r0 == 0) goto L_0x048b
            java.lang.CharSequence r0 = r1.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0016
            goto L_0x048b
        L_0x0016:
            r29.generateLinkDescription()
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r1.textLayoutBlocks = r0
            r2 = 0
            r1.textWidth = r2
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r3 = r0.send_state
            r4 = 1
            if (r3 == 0) goto L_0x002c
            r0 = 0
            goto L_0x0033
        L_0x002c:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r4
        L_0x0033:
            if (r0 != 0) goto L_0x007b
            long r5 = r1.eventId
            r7 = 0
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_old
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_old2
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_old3
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_old4
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageForwarded_old
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageForwarded_old2
            if (r3 != 0) goto L_0x0079
            boolean r3 = r0 instanceof org.telegram.tgnet.TLRPC$TL_message_secret
            if (r3 != 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r0 != 0) goto L_0x0079
            boolean r0 = r29.isOut()
            if (r0 == 0) goto L_0x006d
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r0 = r0.send_state
            if (r0 != 0) goto L_0x0079
        L_0x006d:
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            int r3 = r0.id
            if (r3 < 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported
            if (r0 == 0) goto L_0x007b
        L_0x0079:
            r0 = 1
            goto L_0x007c
        L_0x007b:
            r0 = 0
        L_0x007c:
            if (r0 == 0) goto L_0x0087
            boolean r3 = r29.isOutOwner()
            java.lang.CharSequence r5 = r1.messageText
            addLinks(r3, r5, r4, r4)
        L_0x0087:
            boolean r3 = r29.isYouTubeVideo()
            if (r3 != 0) goto L_0x00d8
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            if (r3 == 0) goto L_0x0098
            boolean r3 = r3.isYouTubeVideo()
            if (r3 == 0) goto L_0x0098
            goto L_0x00d8
        L_0x0098:
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            if (r3 == 0) goto L_0x00e8
            boolean r3 = r3.isVideo()
            if (r3 == 0) goto L_0x00b5
            boolean r5 = r29.isOutOwner()
            java.lang.CharSequence r6 = r1.messageText
            r7 = 0
            r8 = 3
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            int r9 = r3.getDuration()
            r10 = 0
            addUrlsByPattern(r5, r6, r7, r8, r9, r10)
            goto L_0x00e8
        L_0x00b5:
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            boolean r3 = r3.isMusic()
            if (r3 != 0) goto L_0x00c5
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            boolean r3 = r3.isVoice()
            if (r3 == 0) goto L_0x00e8
        L_0x00c5:
            boolean r5 = r29.isOutOwner()
            java.lang.CharSequence r6 = r1.messageText
            r7 = 0
            r8 = 4
            org.telegram.messenger.MessageObject r3 = r1.replyMessageObject
            int r9 = r3.getDuration()
            r10 = 0
            addUrlsByPattern(r5, r6, r7, r8, r9, r10)
            goto L_0x00e8
        L_0x00d8:
            boolean r11 = r29.isOutOwner()
            java.lang.CharSequence r12 = r1.messageText
            r13 = 0
            r14 = 3
            r15 = 2147483647(0x7fffffff, float:NaN)
            r16 = 0
            addUrlsByPattern(r11, r12, r13, r14, r15, r16)
        L_0x00e8:
            java.lang.CharSequence r3 = r1.messageText
            boolean r3 = r1.addEntitiesToText(r3, r0)
            int r15 = r29.getMaxMessageTextWidth()
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x00fd
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint
            goto L_0x00ff
        L_0x00fd:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
        L_0x00ff:
            r14 = r0
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0487 }
            r13 = 24
            if (r0 < r13) goto L_0x0124
            java.lang.CharSequence r5 = r1.messageText     // Catch:{ Exception -> 0x0487 }
            int r6 = r5.length()     // Catch:{ Exception -> 0x0487 }
            android.text.StaticLayout$Builder r5 = android.text.StaticLayout.Builder.obtain(r5, r2, r6, r14, r15)     // Catch:{ Exception -> 0x0487 }
            android.text.StaticLayout$Builder r5 = r5.setBreakStrategy(r4)     // Catch:{ Exception -> 0x0487 }
            android.text.StaticLayout$Builder r5 = r5.setHyphenationFrequency(r2)     // Catch:{ Exception -> 0x0487 }
            android.text.Layout$Alignment r6 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0487 }
            android.text.StaticLayout$Builder r5 = r5.setAlignment(r6)     // Catch:{ Exception -> 0x0487 }
            android.text.StaticLayout r5 = r5.build()     // Catch:{ Exception -> 0x0487 }
            r12 = r5
            goto L_0x0137
        L_0x0124:
            android.text.StaticLayout r16 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0487 }
            java.lang.CharSequence r6 = r1.messageText     // Catch:{ Exception -> 0x0487 }
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0487 }
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            r12 = 0
            r5 = r16
            r7 = r14
            r8 = r15
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x0487 }
            r12 = r16
        L_0x0137:
            int r5 = r12.getHeight()
            r1.textHeight = r5
            int r5 = r12.getLineCount()
            r1.linesCount = r5
            if (r0 < r13) goto L_0x0147
            r11 = 1
            goto L_0x0152
        L_0x0147:
            float r0 = (float) r5
            r5 = 1092616192(0x41200000, float:10.0)
            float r0 = r0 / r5
            double r5 = (double) r0
            double r5 = java.lang.Math.ceil(r5)
            int r0 = (int) r5
            r11 = r0
        L_0x0152:
            r10 = 0
            r8 = 0
            r9 = 0
            r16 = 0
        L_0x0157:
            if (r9 >= r11) goto L_0x0486
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r13) goto L_0x0160
            int r5 = r1.linesCount
            goto L_0x0169
        L_0x0160:
            r5 = 10
            int r6 = r1.linesCount
            int r6 = r6 - r8
            int r5 = java.lang.Math.min(r5, r6)
        L_0x0169:
            r7 = r5
            org.telegram.messenger.MessageObject$TextLayoutBlock r6 = new org.telegram.messenger.MessageObject$TextLayoutBlock
            r6.<init>()
            r5 = 2
            if (r11 != r4) goto L_0x01e6
            r6.textLayout = r12
            r6.textYOffset = r10
            r6.charactersOffset = r2
            java.lang.CharSequence r0 = r12.getText()
            int r0 = r0.length()
            r6.charactersEnd = r0
            int r0 = r1.emojiOnlyCount
            if (r0 == 0) goto L_0x01d4
            if (r0 == r4) goto L_0x01bd
            if (r0 == r5) goto L_0x01a6
            r5 = 3
            if (r0 == r5) goto L_0x018e
            goto L_0x01d4
        L_0x018e:
            int r0 = r1.textHeight
            r5 = 1082549862(0x40866666, float:4.2)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r17
            r1.textHeight = r0
            float r0 = r6.textYOffset
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r0 = r0 - r5
            r6.textYOffset = r0
            goto L_0x01d4
        L_0x01a6:
            int r0 = r1.textHeight
            r5 = 1083179008(0x40900000, float:4.5)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r17
            r1.textHeight = r0
            float r0 = r6.textYOffset
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r0 = r0 - r5
            r6.textYOffset = r0
            goto L_0x01d4
        L_0x01bd:
            int r0 = r1.textHeight
            r5 = 1084856730(0x40a9999a, float:5.3)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r0 = r0 - r17
            r1.textHeight = r0
            float r0 = r6.textYOffset
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            float r5 = (float) r5
            float r0 = r0 - r5
            r6.textYOffset = r0
        L_0x01d4:
            int r0 = r1.textHeight
            r6.height = r0
            r2 = r6
            r4 = r7
            r6 = r8
            r7 = r9
            r9 = r11
            r5 = r12
            r19 = r14
            r18 = 24
            r24 = 2
            goto L_0x02de
        L_0x01e6:
            int r5 = r12.getLineStart(r8)
            int r17 = r8 + r7
            int r10 = r17 + -1
            int r10 = r12.getLineEnd(r10)
            if (r10 >= r5) goto L_0x0203
            r21 = r3
            r26 = r8
            r7 = r9
            r9 = r11
            r28 = r12
            r19 = r14
            r3 = 0
            r18 = 24
            goto L_0x0474
        L_0x0203:
            r6.charactersOffset = r5
            r6.charactersEnd = r10
            java.lang.CharSequence r4 = r1.messageText     // Catch:{ Exception -> 0x0462 }
            java.lang.CharSequence r4 = r4.subSequence(r5, r10)     // Catch:{ Exception -> 0x0462 }
            android.text.SpannableStringBuilder r4 = android.text.SpannableStringBuilder.valueOf(r4)     // Catch:{ Exception -> 0x0462 }
            if (r3 == 0) goto L_0x0256
            if (r0 < r13) goto L_0x0256
            int r0 = r4.length()     // Catch:{ Exception -> 0x0462 }
            r5 = 1073741824(0x40000000, float:2.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)     // Catch:{ Exception -> 0x0462 }
            int r5 = r5 + r15
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r4, r2, r0, r14, r5)     // Catch:{ Exception -> 0x0462 }
            r4 = 1
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r4)     // Catch:{ Exception -> 0x0248 }
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r2)     // Catch:{ Exception -> 0x0462 }
            android.text.Layout$Alignment r4 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0462 }
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r4)     // Catch:{ Exception -> 0x0462 }
            android.text.StaticLayout r0 = r0.build()     // Catch:{ Exception -> 0x0462 }
            r6.textLayout = r0     // Catch:{ Exception -> 0x0462 }
            r2 = r6
            r4 = r7
            r6 = r8
            r25 = r9
            r27 = r11
            r5 = r12
            r19 = r14
            r18 = 24
            r24 = 2
            goto L_0x028e
        L_0x0248:
            r0 = move-exception
            r21 = r3
            r26 = r8
            r7 = r9
            r9 = r11
            r28 = r12
            r19 = r14
            r3 = 0
            goto L_0x046f
        L_0x0256:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0462 }
            r10 = 0
            int r19 = r4.length()     // Catch:{ Exception -> 0x0462 }
            android.text.Layout$Alignment r20 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0462 }
            r21 = 1065353216(0x3var_, float:1.0)
            r22 = 0
            r23 = 0
            r24 = 2
            r5 = r0
            r2 = r6
            r6 = r4
            r4 = r7
            r7 = r10
            r10 = r8
            r8 = r19
            r25 = r9
            r9 = r14
            r26 = r10
            r10 = r15
            r27 = r11
            r11 = r20
            r28 = r12
            r12 = r21
            r18 = 24
            r13 = r22
            r19 = r14
            r14 = r23
            r5.<init>(r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x0458 }
            r2.textLayout = r0     // Catch:{ Exception -> 0x0458 }
            r6 = r26
            r5 = r28
        L_0x028e:
            int r0 = r5.getLineTop(r6)     // Catch:{ Exception -> 0x0450 }
            float r0 = (float) r0     // Catch:{ Exception -> 0x0450 }
            r2.textYOffset = r0     // Catch:{ Exception -> 0x0450 }
            r7 = r25
            if (r7 == 0) goto L_0x029e
            float r0 = r0 - r16
            int r0 = (int) r0
            r2.height = r0     // Catch:{ Exception -> 0x0448 }
        L_0x029e:
            int r0 = r2.height     // Catch:{ Exception -> 0x0448 }
            android.text.StaticLayout r8 = r2.textLayout     // Catch:{ Exception -> 0x0448 }
            int r9 = r8.getLineCount()     // Catch:{ Exception -> 0x0448 }
            r10 = 1
            int r9 = r9 - r10
            int r8 = r8.getLineBottom(r9)     // Catch:{ Exception -> 0x0448 }
            int r0 = java.lang.Math.max(r0, r8)     // Catch:{ Exception -> 0x0448 }
            r2.height = r0     // Catch:{ Exception -> 0x0448 }
            float r8 = r2.textYOffset     // Catch:{ Exception -> 0x0448 }
            r9 = r27
            int r11 = r9 + -1
            if (r7 != r11) goto L_0x02dc
            android.text.StaticLayout r0 = r2.textLayout
            int r0 = r0.getLineCount()
            int r4 = java.lang.Math.max(r4, r0)
            int r0 = r1.textHeight     // Catch:{ Exception -> 0x02d8 }
            float r10 = r2.textYOffset     // Catch:{ Exception -> 0x02d8 }
            android.text.StaticLayout r11 = r2.textLayout     // Catch:{ Exception -> 0x02d8 }
            int r11 = r11.getHeight()     // Catch:{ Exception -> 0x02d8 }
            float r11 = (float) r11     // Catch:{ Exception -> 0x02d8 }
            float r10 = r10 + r11
            int r10 = (int) r10     // Catch:{ Exception -> 0x02d8 }
            int r0 = java.lang.Math.max(r0, r10)     // Catch:{ Exception -> 0x02d8 }
            r1.textHeight = r0     // Catch:{ Exception -> 0x02d8 }
            goto L_0x02dc
        L_0x02d8:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x02dc:
            r16 = r8
        L_0x02de:
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r0 = r2.spoilers
            r0.clear()
            boolean r0 = r1.isSpoilersRevealed
            if (r0 != 0) goto L_0x02ef
            android.text.StaticLayout r0 = r2.textLayout
            java.util.List<org.telegram.ui.Components.spoilers.SpoilerEffect> r8 = r2.spoilers
            r10 = 0
            org.telegram.ui.Components.spoilers.SpoilerEffect.addSpoilers(r10, r0, r10, r8)
        L_0x02ef:
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r1.textLayoutBlocks
            r0.add(r2)
            android.text.StaticLayout r0 = r2.textLayout     // Catch:{ Exception -> 0x0308 }
            int r8 = r4 + -1
            float r10 = r0.getLineLeft(r8)     // Catch:{ Exception -> 0x0308 }
            r8 = 0
            if (r7 != 0) goto L_0x0312
            int r0 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r0 < 0) goto L_0x0312
            r1.textXOffset = r10     // Catch:{ Exception -> 0x0306 }
            goto L_0x0312
        L_0x0306:
            r0 = move-exception
            goto L_0x030a
        L_0x0308:
            r0 = move-exception
            r8 = 0
        L_0x030a:
            if (r7 != 0) goto L_0x030e
            r1.textXOffset = r8
        L_0x030e:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r10 = 0
        L_0x0312:
            android.text.StaticLayout r0 = r2.textLayout     // Catch:{ Exception -> 0x031b }
            int r11 = r4 + -1
            float r0 = r0.getLineWidth(r11)     // Catch:{ Exception -> 0x031b }
            goto L_0x0320
        L_0x031b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x0320:
            double r11 = (double) r0
            double r11 = java.lang.Math.ceil(r11)
            int r0 = (int) r11
            int r11 = r15 + 80
            if (r0 <= r11) goto L_0x032b
            r0 = r15
        L_0x032b:
            int r11 = r9 + -1
            if (r7 != r11) goto L_0x0331
            r1.lastLineWidth = r0
        L_0x0331:
            float r12 = (float) r0
            float r13 = java.lang.Math.max(r8, r10)
            float r13 = r13 + r12
            double r13 = (double) r13
            double r13 = java.lang.Math.ceil(r13)
            int r13 = (int) r13
            r14 = 1
            if (r4 <= r14) goto L_0x0407
            r10 = r0
            r21 = r3
            r3 = r13
            r8 = 0
            r12 = 0
            r14 = 0
            r20 = 0
        L_0x0349:
            if (r8 >= r4) goto L_0x03e4
            android.text.StaticLayout r0 = r2.textLayout     // Catch:{ Exception -> 0x0354 }
            float r0 = r0.getLineWidth(r8)     // Catch:{ Exception -> 0x0354 }
            r22 = r0
            goto L_0x035a
        L_0x0354:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r22 = 0
        L_0x035a:
            android.text.StaticLayout r0 = r2.textLayout     // Catch:{ Exception -> 0x0361 }
            float r0 = r0.getLineLeft(r8)     // Catch:{ Exception -> 0x0361 }
            goto L_0x0366
        L_0x0361:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x0366:
            r23 = r0
            int r0 = r15 + 20
            float r0 = (float) r0
            int r0 = (r22 > r0 ? 1 : (r22 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0377
            float r0 = (float) r15
            r28 = r5
            r22 = 0
            r5 = r0
            r0 = 0
            goto L_0x037f
        L_0x0377:
            r28 = r5
            r5 = r22
            r0 = r23
            r22 = 0
        L_0x037f:
            int r23 = (r0 > r22 ? 1 : (r0 == r22 ? 0 : -1))
            if (r23 <= 0) goto L_0x0399
            r22 = r4
            float r4 = r1.textXOffset
            float r4 = java.lang.Math.min(r4, r0)
            r1.textXOffset = r4
            byte r4 = r2.directionFlags
            r26 = r6
            r6 = 1
            r4 = r4 | r6
            byte r4 = (byte) r4
            r2.directionFlags = r4
            r1.hasRtl = r6
            goto L_0x03a4
        L_0x0399:
            r22 = r4
            r26 = r6
            byte r4 = r2.directionFlags
            r4 = r4 | 2
            byte r4 = (byte) r4
            r2.directionFlags = r4
        L_0x03a4:
            if (r20 != 0) goto L_0x03b9
            r4 = 0
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x03b9
            android.text.StaticLayout r4 = r2.textLayout     // Catch:{ Exception -> 0x03b6 }
            int r4 = r4.getParagraphDirection(r8)     // Catch:{ Exception -> 0x03b6 }
            r6 = 1
            if (r4 != r6) goto L_0x03b9
            r4 = 1
            goto L_0x03bb
        L_0x03b6:
            r20 = 1
            goto L_0x03bd
        L_0x03b9:
            r4 = r20
        L_0x03bb:
            r20 = r4
        L_0x03bd:
            float r14 = java.lang.Math.max(r14, r5)
            float r0 = r0 + r5
            float r12 = java.lang.Math.max(r12, r0)
            double r4 = (double) r5
            double r4 = java.lang.Math.ceil(r4)
            int r4 = (int) r4
            int r10 = java.lang.Math.max(r10, r4)
            double r4 = (double) r0
            double r4 = java.lang.Math.ceil(r4)
            int r0 = (int) r4
            int r3 = java.lang.Math.max(r3, r0)
            int r8 = r8 + 1
            r4 = r22
            r6 = r26
            r5 = r28
            goto L_0x0349
        L_0x03e4:
            r22 = r4
            r28 = r5
            r26 = r6
            if (r20 == 0) goto L_0x03f1
            if (r7 != r11) goto L_0x03f6
            r1.lastLineWidth = r13
            goto L_0x03f6
        L_0x03f1:
            if (r7 != r11) goto L_0x03f5
            r1.lastLineWidth = r10
        L_0x03f5:
            r12 = r14
        L_0x03f6:
            int r0 = r1.textWidth
            double r2 = (double) r12
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            int r0 = java.lang.Math.max(r0, r2)
            r1.textWidth = r0
            r3 = 0
            r4 = 1
            goto L_0x0445
        L_0x0407:
            r21 = r3
            r22 = r4
            r28 = r5
            r26 = r6
            r3 = 0
            int r4 = (r10 > r3 ? 1 : (r10 == r3 ? 0 : -1))
            if (r4 <= 0) goto L_0x0431
            float r4 = r1.textXOffset
            float r4 = java.lang.Math.min(r4, r10)
            r1.textXOffset = r4
            int r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1))
            if (r4 != 0) goto L_0x0422
            float r12 = r12 + r10
            int r0 = (int) r12
        L_0x0422:
            r4 = 1
            if (r9 == r4) goto L_0x0427
            r5 = 1
            goto L_0x0428
        L_0x0427:
            r5 = 0
        L_0x0428:
            r1.hasRtl = r5
            byte r5 = r2.directionFlags
            r5 = r5 | r4
            byte r5 = (byte) r5
            r2.directionFlags = r5
            goto L_0x0439
        L_0x0431:
            r4 = 1
            byte r5 = r2.directionFlags
            r5 = r5 | 2
            byte r5 = (byte) r5
            r2.directionFlags = r5
        L_0x0439:
            int r2 = r1.textWidth
            int r0 = java.lang.Math.min(r15, r0)
            int r0 = java.lang.Math.max(r2, r0)
            r1.textWidth = r0
        L_0x0445:
            int r8 = r26 + r22
            goto L_0x0476
        L_0x0448:
            r0 = move-exception
            r21 = r3
            r28 = r5
            r26 = r6
            goto L_0x045d
        L_0x0450:
            r0 = move-exception
            r21 = r3
            r28 = r5
            r26 = r6
            goto L_0x045b
        L_0x0458:
            r0 = move-exception
            r21 = r3
        L_0x045b:
            r7 = r25
        L_0x045d:
            r9 = r27
            r3 = 0
            r4 = 1
            goto L_0x0471
        L_0x0462:
            r0 = move-exception
            r21 = r3
            r26 = r8
            r7 = r9
            r9 = r11
            r28 = r12
            r19 = r14
            r3 = 0
            r4 = 1
        L_0x046f:
            r18 = 24
        L_0x0471:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0474:
            r8 = r26
        L_0x0476:
            int r0 = r7 + 1
            r11 = r9
            r14 = r19
            r3 = r21
            r12 = r28
            r2 = 0
            r10 = 0
            r13 = 24
            r9 = r0
            goto L_0x0157
        L_0x0486:
            return
        L_0x0487:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x048b:
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

    public static int getMessageSize(TLRPC$Message tLRPC$Message) {
        TLRPC$Document tLRPC$Document;
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
            tLRPC$Document = tLRPC$MessageMedia.webpage.document;
        } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaGame) {
            tLRPC$Document = tLRPC$MessageMedia.game.document;
        } else {
            tLRPC$Document = tLRPC$MessageMedia != null ? tLRPC$MessageMedia.document : null;
        }
        if (tLRPC$Document != null) {
            return tLRPC$Document.size;
        }
        return 0;
    }

    public int getSize() {
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
        if (!(tLRPC$Message instanceof TLRPC$TL_message_secret)) {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && tLRPC$MessageMedia.ttl_seconds != 0) {
                return true;
            }
            return false;
        } else if (((tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) || isVideoMessage(tLRPC$Message)) && (i = tLRPC$Message.ttl) > 0 && i <= 60) {
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
            if (((tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) || isRoundVideoMessage(tLRPC$Message) || isVideoMessage(tLRPC$Message)) && (i = tLRPC$Message.ttl) > 0 && i <= 60) {
                return true;
            }
            return false;
        } else if (!(tLRPC$Message instanceof TLRPC$TL_message)) {
            return false;
        } else {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && tLRPC$MessageMedia.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public static boolean isSecretMedia(TLRPC$Message tLRPC$Message) {
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            if (((tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) || isRoundVideoMessage(tLRPC$Message) || isVideoMessage(tLRPC$Message)) && tLRPC$Message.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        } else if (!(tLRPC$Message instanceof TLRPC$TL_message)) {
            return false;
        } else {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && tLRPC$MessageMedia.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean needDrawBluredPreview() {
        TLRPC$MessageMedia tLRPC$MessageMedia;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            int max = Math.max(tLRPC$Message.ttl, tLRPC$Message.media.ttl_seconds);
            if (max <= 0 || (((!(this.messageOwner.media instanceof TLRPC$TL_messageMediaPhoto) && !isVideo() && !isGif()) || max > 60) && !isRoundVideo())) {
                return false;
            }
            return true;
        } else if (!(tLRPC$Message instanceof TLRPC$TL_message) || (tLRPC$MessageMedia = tLRPC$Message.media) == null || tLRPC$MessageMedia.ttl_seconds == 0 || (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument))) {
            return false;
        } else {
            return true;
        }
    }

    public boolean isSecretMedia() {
        TLRPC$MessageMedia tLRPC$MessageMedia;
        int i;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            if ((((tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) || isGif()) && (i = this.messageOwner.ttl) > 0 && i <= 60) || isVoice() || isRoundVideo() || isVideo()) {
                return true;
            }
            return false;
        } else if (!(tLRPC$Message instanceof TLRPC$TL_message) || (tLRPC$MessageMedia = tLRPC$Message.media) == null || tLRPC$MessageMedia.ttl_seconds == 0 || (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) && !(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument))) {
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
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
            if (tLRPC$MessageMedia.photo.id == 0) {
                return true;
            }
            return false;
        } else if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) || tLRPC$MessageMedia.document.dc_id != 0) {
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
                if (tLRPC$Document.attributes.get(i) instanceof TLRPC$TL_documentAttributeSticker) {
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
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
            return tLRPC$MessageMedia.webpage.document;
        }
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaGame) {
            return tLRPC$MessageMedia.game.document;
        }
        if (tLRPC$MessageMedia != null) {
            return tLRPC$MessageMedia.document;
        }
        return null;
    }

    public static TLRPC$Photo getPhoto(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
            return tLRPC$MessageMedia.webpage.photo;
        }
        if (tLRPC$MessageMedia != null) {
            return tLRPC$MessageMedia.photo;
        }
        return null;
    }

    public static boolean isStickerMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        return tLRPC$MessageMedia != null && isStickerDocument(tLRPC$MessageMedia.document);
    }

    public static boolean isAnimatedStickerMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia;
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(tLRPC$Message.dialog_id);
        if ((isEncryptedDialog && tLRPC$Message.stickerVerified != 1) || (tLRPC$MessageMedia = tLRPC$Message.media) == null) {
            return false;
        }
        if (isAnimatedStickerDocument(tLRPC$MessageMedia.document, !isEncryptedDialog || tLRPC$Message.out)) {
            return true;
        }
        return false;
    }

    public static boolean isLocationMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        return (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaGeo) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaGeoLive) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaVenue);
    }

    public static boolean isMaskMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        return tLRPC$MessageMedia != null && isMaskDocument(tLRPC$MessageMedia.document);
    }

    public static boolean isMusicMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
            return isMusicDocument(tLRPC$MessageMedia.webpage.document);
        }
        return tLRPC$MessageMedia != null && isMusicDocument(tLRPC$MessageMedia.document);
    }

    public static boolean isGifMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
            return isGifDocument(tLRPC$MessageMedia.webpage.document);
        }
        if (tLRPC$MessageMedia != null) {
            if (isGifDocument(tLRPC$MessageMedia.document, tLRPC$Message.grouped_id != 0)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRoundVideoMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
            return isRoundVideoDocument(tLRPC$MessageMedia.webpage.document);
        }
        return tLRPC$MessageMedia != null && isRoundVideoDocument(tLRPC$MessageMedia.document);
    }

    public static boolean isPhoto(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage)) {
            return tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto;
        }
        TLRPC$WebPage tLRPC$WebPage = tLRPC$MessageMedia.webpage;
        return (tLRPC$WebPage.photo instanceof TLRPC$TL_photo) && !(tLRPC$WebPage.document instanceof TLRPC$TL_document);
    }

    public static boolean isVoiceMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
            return isVoiceDocument(tLRPC$MessageMedia.webpage.document);
        }
        return tLRPC$MessageMedia != null && isVoiceDocument(tLRPC$MessageMedia.document);
    }

    public static boolean isNewGifMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
            return isNewGifDocument(tLRPC$MessageMedia.webpage.document);
        }
        return tLRPC$MessageMedia != null && isNewGifDocument(tLRPC$MessageMedia.document);
    }

    public static boolean isLiveLocationMessage(TLRPC$Message tLRPC$Message) {
        return tLRPC$Message.media instanceof TLRPC$TL_messageMediaGeoLive;
    }

    public static boolean isVideoMessage(TLRPC$Message tLRPC$Message) {
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia != null && isVideoSticker(tLRPC$MessageMedia.document)) {
            return false;
        }
        TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message.media;
        if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaWebPage) {
            return isVideoDocument(tLRPC$MessageMedia2.webpage.document);
        }
        if (tLRPC$MessageMedia2 == null || !isVideoDocument(tLRPC$MessageMedia2.document)) {
            return false;
        }
        return true;
    }

    public static boolean isGameMessage(TLRPC$Message tLRPC$Message) {
        return tLRPC$Message.media instanceof TLRPC$TL_messageMediaGame;
    }

    public static boolean isInvoiceMessage(TLRPC$Message tLRPC$Message) {
        return tLRPC$Message.media instanceof TLRPC$TL_messageMediaInvoice;
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
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                TLRPC$InputStickerSet tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset;
                if (tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetEmpty) {
                    return null;
                }
                return tLRPC$InputStickerSet;
            }
        }
        return null;
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
            TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
            if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) && (tLRPC$MessageMedia.webpage instanceof TLRPC$TL_webPage)) {
                i6 = AndroidUtilities.dp(100.0f);
            }
            int i8 = i7 + i6;
            return isReply() ? i8 + AndroidUtilities.dp(42.0f) : i8;
        } else if (i5 == 2) {
            return AndroidUtilities.dp(72.0f);
        } else {
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
            if (i5 == 11) {
                return AndroidUtilities.dp(50.0f);
            }
            if (i5 == 5) {
                return AndroidUtilities.roundMessageSize;
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
                int size = document.attributes.size();
                int i9 = 0;
                while (true) {
                    if (i9 >= size) {
                        i2 = 0;
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
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
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
        return this.emojiAnimatedSticker != null;
    }

    public boolean isDice() {
        return this.messageOwner.media instanceof TLRPC$TL_messageMediaDice;
    }

    public String getDiceEmoji() {
        if (!isDice()) {
            return null;
        }
        TLRPC$TL_messageMediaDice tLRPC$TL_messageMediaDice = (TLRPC$TL_messageMediaDice) this.messageOwner.media;
        if (TextUtils.isEmpty(tLRPC$TL_messageMediaDice.emoticon)) {
            return "🎲";
        }
        return tLRPC$TL_messageMediaDice.emoticon.replace("️", "");
    }

    public int getDiceValue() {
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDice) {
            return ((TLRPC$TL_messageMediaDice) tLRPC$MessageMedia).value;
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
        TLRPC$Document document = getDocument();
        if (this.emojiAnimatedSticker != null || !isEncryptedDialog || isOut()) {
            z = true;
        }
        return isAnimatedStickerDocument(document, z);
    }

    public boolean isAnyKindOfSticker() {
        int i = this.type;
        return i == 13 || i == 15;
    }

    public boolean shouldDrawWithoutBackground() {
        int i = this.type;
        return i == 13 || i == 15 || i == 5;
    }

    public boolean isLocation() {
        return isLocationMessage(this.messageOwner);
    }

    public boolean isMask() {
        return isMaskMessage(this.messageOwner);
    }

    public boolean isMusic() {
        return isMusicMessage(this.messageOwner);
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
        return tLRPC$Message.date + tLRPC$Message.media.period <= i;
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
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
            TLRPC$Photo tLRPC$Photo = tLRPC$MessageMedia.photo;
            if (tLRPC$Photo == null || !tLRPC$Photo.has_stickers) {
                return false;
            }
            return true;
        } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
            return isDocumentHasAttachedStickers(tLRPC$MessageMedia.document);
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

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r0.webpage.document;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isWebpageDocument() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r1 == 0) goto L_0x0016
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            org.telegram.tgnet.TLRPC$Document r0 = r0.document
            if (r0 == 0) goto L_0x0016
            boolean r0 = isGifDocument((org.telegram.tgnet.TLRPC$Document) r0)
            if (r0 != 0) goto L_0x0016
            r0 = 1
            goto L_0x0017
        L_0x0016:
            r0 = 0
        L_0x0017:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isWebpageDocument():boolean");
    }

    public boolean isWebpage() {
        return this.messageOwner.media instanceof TLRPC$TL_messageMediaWebPage;
    }

    public boolean isNewGif() {
        return this.messageOwner.media != null && isNewGifDocument(getDocument());
    }

    public boolean isAndroidTheme() {
        TLRPC$WebPage tLRPC$WebPage;
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        if (!(tLRPC$MessageMedia == null || (tLRPC$WebPage = tLRPC$MessageMedia.webpage) == null || tLRPC$WebPage.attributes.isEmpty())) {
            int size = this.messageOwner.media.webpage.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC$TL_webPageAttributeTheme tLRPC$TL_webPageAttributeTheme = this.messageOwner.media.webpage.attributes.get(i);
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
                        return (!TextUtils.isEmpty(documentFileName) || !z) ? documentFileName : LocaleController.getString("AudioUnknownTitle", NUM);
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
        return LocaleController.getString("AudioUnknownTitle", NUM);
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

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003d, code lost:
        if (r5.round_message != false) goto L_0x0026;
     */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0042  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0158 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getMusicAuthor(boolean r13) {
        /*
            r12 = this;
            org.telegram.tgnet.TLRPC$Document r0 = r12.getDocument()
            r1 = 2131624436(0x7f0e01f4, float:1.8876052E38)
            java.lang.String r2 = "AudioUnknownArtist"
            if (r0 == 0) goto L_0x015c
            r3 = 0
            r4 = 0
        L_0x000d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r0.attributes
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x015c
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r0.attributes
            java.lang.Object r5 = r5.get(r3)
            org.telegram.tgnet.TLRPC$DocumentAttribute r5 = (org.telegram.tgnet.TLRPC$DocumentAttribute) r5
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeAudio
            r7 = 1
            if (r6 == 0) goto L_0x0037
            boolean r4 = r5.voice
            if (r4 == 0) goto L_0x0028
        L_0x0026:
            r4 = 1
            goto L_0x0040
        L_0x0028:
            java.lang.String r0 = r5.performer
            boolean r3 = android.text.TextUtils.isEmpty(r0)
            if (r3 == 0) goto L_0x0036
            if (r13 == 0) goto L_0x0036
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
        L_0x0036:
            return r0
        L_0x0037:
            boolean r6 = r5 instanceof org.telegram.tgnet.TLRPC$TL_documentAttributeVideo
            if (r6 == 0) goto L_0x0040
            boolean r5 = r5.round_message
            if (r5 == 0) goto L_0x0040
            goto L_0x0026
        L_0x0040:
            if (r4 == 0) goto L_0x0158
            r5 = 0
            if (r13 != 0) goto L_0x0046
            return r5
        L_0x0046:
            boolean r6 = r12.isOutOwner()
            if (r6 != 0) goto L_0x014e
            org.telegram.tgnet.TLRPC$Message r6 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x006a
            org.telegram.tgnet.TLRPC$Peer r6 = r6.from_id
            boolean r7 = r6 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r7 == 0) goto L_0x006a
            long r6 = r6.user_id
            int r8 = r12.currentAccount
            org.telegram.messenger.UserConfig r8 = org.telegram.messenger.UserConfig.getInstance(r8)
            long r8 = r8.getClientUserId()
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 != 0) goto L_0x006a
            goto L_0x014e
        L_0x006a:
            org.telegram.tgnet.TLRPC$Message r6 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r7 = r6.fwd_from
            if (r7 == 0) goto L_0x008e
            org.telegram.tgnet.TLRPC$Peer r8 = r7.from_id
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r8 == 0) goto L_0x008e
            int r6 = r12.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r7 = r7.fwd_from
            org.telegram.tgnet.TLRPC$Peer r7 = r7.from_id
            long r7 = r7.channel_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x0142
        L_0x008e:
            if (r7 == 0) goto L_0x00ae
            org.telegram.tgnet.TLRPC$Peer r8 = r7.from_id
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r8 == 0) goto L_0x00ae
            int r6 = r12.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r7 = r7.fwd_from
            org.telegram.tgnet.TLRPC$Peer r7 = r7.from_id
            long r7 = r7.chat_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x0142
        L_0x00ae:
            if (r7 == 0) goto L_0x00d1
            org.telegram.tgnet.TLRPC$Peer r8 = r7.from_id
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r8 == 0) goto L_0x00d1
            int r6 = r12.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r12.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r7 = r7.fwd_from
            org.telegram.tgnet.TLRPC$Peer r7 = r7.from_id
            long r7 = r7.user_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
        L_0x00cc:
            r11 = r6
            r6 = r5
            r5 = r11
            goto L_0x0142
        L_0x00d1:
            if (r7 == 0) goto L_0x00d8
            java.lang.String r7 = r7.from_name
            if (r7 == 0) goto L_0x00d8
            return r7
        L_0x00d8:
            org.telegram.tgnet.TLRPC$Peer r7 = r6.from_id
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_peerChat
            if (r8 == 0) goto L_0x00f3
            int r6 = r12.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r12.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.from_id
            long r7 = r7.chat_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x0142
        L_0x00f3:
            boolean r8 = r7 instanceof org.telegram.tgnet.TLRPC$TL_peerChannel
            if (r8 == 0) goto L_0x010c
            int r6 = r12.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r12.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.from_id
            long r7 = r7.channel_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x0142
        L_0x010c:
            if (r7 != 0) goto L_0x012d
            org.telegram.tgnet.TLRPC$Peer r6 = r6.peer_id
            long r6 = r6.channel_id
            r8 = 0
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 == 0) goto L_0x012d
            int r6 = r12.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r12.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.peer_id
            long r7 = r7.channel_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x0142
        L_0x012d:
            int r6 = r12.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r12.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.from_id
            long r7 = r7.user_id
            java.lang.Long r7 = java.lang.Long.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            goto L_0x00cc
        L_0x0142:
            if (r5 == 0) goto L_0x0149
            java.lang.String r13 = org.telegram.messenger.UserObject.getUserName(r5)
            return r13
        L_0x0149:
            if (r6 == 0) goto L_0x0158
            java.lang.String r13 = r6.title
            return r13
        L_0x014e:
            r13 = 2131625811(0x7f0e0753, float:1.887884E38)
            java.lang.String r0 = "FromYou"
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r0, r13)
            return r13
        L_0x0158:
            int r3 = r3 + 1
            goto L_0x000d
        L_0x015c:
            java.lang.String r13 = org.telegram.messenger.LocaleController.getString(r2, r1)
            return r13
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
        if (r1.channel_id != r0.channel_id) goto L_0x0022;
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
            if (r2 == 0) goto L_0x0036
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

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r1 = r1.media;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isMediaEmpty(org.telegram.tgnet.TLRPC$Message r1) {
        /*
            if (r1 == 0) goto L_0x0011
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            if (r1 == 0) goto L_0x0011
            boolean r0 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            if (r0 != 0) goto L_0x0011
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r1 == 0) goto L_0x000f
            goto L_0x0011
        L_0x000f:
            r1 = 0
            goto L_0x0012
        L_0x0011:
            r1 = 1
        L_0x0012:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isMediaEmpty(org.telegram.tgnet.TLRPC$Message):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = r0.media;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isMediaEmptyWebpage(org.telegram.tgnet.TLRPC$Message r0) {
        /*
            if (r0 == 0) goto L_0x000d
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x000d
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            if (r0 == 0) goto L_0x000b
            goto L_0x000d
        L_0x000b:
            r0 = 0
            goto L_0x000e
        L_0x000d:
            r0 = 1
        L_0x000e:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isMediaEmptyWebpage(org.telegram.tgnet.TLRPC$Message):boolean");
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
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
            return true;
        }
        if (!(tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) || isVoice() || isSticker() || isAnimatedSticker() || isRoundVideo()) {
            return false;
        }
        return true;
    }

    public boolean canEditMessageAnytime(TLRPC$Chat tLRPC$Chat) {
        return canEditMessageAnytime(this.currentAccount, this.messageOwner, tLRPC$Chat);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00ab, code lost:
        r9 = r11.admin_rights;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00b3, code lost:
        r9 = r11.default_banned_rights;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean canEditMessageAnytime(int r9, org.telegram.tgnet.TLRPC$Message r10, org.telegram.tgnet.TLRPC$Chat r11) {
        /*
            r0 = 0
            if (r10 == 0) goto L_0x00bc
            org.telegram.tgnet.TLRPC$Peer r1 = r10.peer_id
            if (r1 == 0) goto L_0x00bc
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r10.media
            r2 = 1
            if (r1 == 0) goto L_0x0028
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = isRoundVideoDocument(r1)
            if (r1 != 0) goto L_0x00bc
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r10.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = isStickerDocument(r1)
            if (r1 != 0) goto L_0x00bc
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r10.media
            org.telegram.tgnet.TLRPC$Document r1 = r1.document
            boolean r1 = isAnimatedStickerDocument(r1, r2)
            if (r1 != 0) goto L_0x00bc
        L_0x0028:
            org.telegram.tgnet.TLRPC$MessageAction r1 = r10.action
            if (r1 == 0) goto L_0x0030
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageActionEmpty
            if (r1 == 0) goto L_0x00bc
        L_0x0030:
            boolean r1 = isForwardedMessage(r10)
            if (r1 != 0) goto L_0x00bc
            long r3 = r10.via_bot_id
            r5 = 0
            int r1 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r1 != 0) goto L_0x00bc
            int r1 = r10.id
            if (r1 >= 0) goto L_0x0044
            goto L_0x00bc
        L_0x0044:
            org.telegram.tgnet.TLRPC$Peer r1 = r10.from_id
            boolean r3 = r1 instanceof org.telegram.tgnet.TLRPC$TL_peerUser
            if (r3 == 0) goto L_0x0067
            long r3 = r1.user_id
            org.telegram.tgnet.TLRPC$Peer r1 = r10.peer_id
            long r7 = r1.user_id
            int r1 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r1 != 0) goto L_0x0067
            org.telegram.messenger.UserConfig r9 = org.telegram.messenger.UserConfig.getInstance(r9)
            long r7 = r9.getClientUserId()
            int r9 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r9 != 0) goto L_0x0067
            boolean r9 = isLiveLocationMessage(r10)
            if (r9 != 0) goto L_0x0067
            return r2
        L_0x0067:
            if (r11 != 0) goto L_0x0086
            org.telegram.tgnet.TLRPC$Peer r9 = r10.peer_id
            long r3 = r9.channel_id
            int r9 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r9 == 0) goto L_0x0086
            int r9 = org.telegram.messenger.UserConfig.selectedAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.tgnet.TLRPC$Peer r11 = r10.peer_id
            long r3 = r11.channel_id
            java.lang.Long r11 = java.lang.Long.valueOf(r3)
            org.telegram.tgnet.TLRPC$Chat r11 = r9.getChat(r11)
            if (r11 != 0) goto L_0x0086
            return r0
        L_0x0086:
            boolean r9 = org.telegram.messenger.ChatObject.isChannel(r11)
            if (r9 == 0) goto L_0x009d
            boolean r9 = r11.megagroup
            if (r9 != 0) goto L_0x009d
            boolean r9 = r11.creator
            if (r9 != 0) goto L_0x009c
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r9 = r11.admin_rights
            if (r9 == 0) goto L_0x009d
            boolean r9 = r9.edit_messages
            if (r9 == 0) goto L_0x009d
        L_0x009c:
            return r2
        L_0x009d:
            boolean r9 = r10.out
            if (r9 == 0) goto L_0x00bc
            if (r11 == 0) goto L_0x00bc
            boolean r9 = r11.megagroup
            if (r9 == 0) goto L_0x00bc
            boolean r9 = r11.creator
            if (r9 != 0) goto L_0x00bb
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r9 = r11.admin_rights
            if (r9 == 0) goto L_0x00b3
            boolean r9 = r9.pin_messages
            if (r9 != 0) goto L_0x00bb
        L_0x00b3:
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r9 = r11.default_banned_rights
            if (r9 == 0) goto L_0x00bc
            boolean r9 = r9.pin_messages
            if (r9 != 0) goto L_0x00bc
        L_0x00bb:
            return r2
        L_0x00bc:
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
        TLRPC$MessageMedia tLRPC$MessageMedia;
        TLRPC$MessageAction tLRPC$MessageAction;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2;
        TLRPC$TL_chatBannedRights tLRPC$TL_chatBannedRights;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights3;
        if (z && tLRPC$Message.date < ConnectionsManager.getInstance(i).getCurrentTime() - 60) {
            return false;
        }
        if ((tLRPC$Chat == null || ((!tLRPC$Chat.left && !tLRPC$Chat.kicked) || (tLRPC$Chat.megagroup && tLRPC$Chat.has_link))) && tLRPC$Message != null && tLRPC$Message.peer_id != null && (((tLRPC$MessageMedia = tLRPC$Message.media) == null || (!isRoundVideoDocument(tLRPC$MessageMedia.document) && !isStickerDocument(tLRPC$Message.media.document) && !isAnimatedStickerDocument(tLRPC$Message.media.document, true) && !isLocationMessage(tLRPC$Message))) && (((tLRPC$MessageAction = tLRPC$Message.action) == null || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionEmpty)) && !isForwardedMessage(tLRPC$Message) && tLRPC$Message.via_bot_id == 0 && tLRPC$Message.id >= 0))) {
            TLRPC$Peer tLRPC$Peer = tLRPC$Message.from_id;
            if (tLRPC$Peer instanceof TLRPC$TL_peerUser) {
                long j = tLRPC$Peer.user_id;
                if (j == tLRPC$Message.peer_id.user_id && j == UserConfig.getInstance(i).getClientUserId() && !isLiveLocationMessage(tLRPC$Message) && !(tLRPC$Message.media instanceof TLRPC$TL_messageMediaContact)) {
                    return true;
                }
            }
            if (tLRPC$Chat == null && tLRPC$Message.peer_id.channel_id != 0 && (tLRPC$Chat = MessagesController.getInstance(i).getChat(Long.valueOf(tLRPC$Message.peer_id.channel_id))) == null) {
                return false;
            }
            TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message.media;
            if (tLRPC$MessageMedia2 != null && !(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaEmpty) && !(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaPhoto) && !(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaDocument) && !(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaWebPage)) {
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
                TLRPC$MessageMedia tLRPC$MessageMedia3 = tLRPC$Message.media;
                if (!(tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaPhoto) && (!(tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaDocument) || isStickerMessage(tLRPC$Message) || isAnimatedStickerMessage(tLRPC$Message))) {
                    TLRPC$MessageMedia tLRPC$MessageMedia4 = tLRPC$Message.media;
                    if ((tLRPC$MessageMedia4 instanceof TLRPC$TL_messageMediaEmpty) || (tLRPC$MessageMedia4 instanceof TLRPC$TL_messageMediaWebPage) || tLRPC$MessageMedia4 == null) {
                        return true;
                    }
                    return false;
                }
                return true;
            } else if ((tLRPC$Chat != null && tLRPC$Chat.megagroup && tLRPC$Message.out) || (tLRPC$Chat != null && !tLRPC$Chat.megagroup && ((tLRPC$Chat.creator || ((tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) != null && (tLRPC$TL_chatAdminRights.edit_messages || (tLRPC$Message.out && tLRPC$TL_chatAdminRights.post_messages)))) && tLRPC$Message.post))) {
                TLRPC$MessageMedia tLRPC$MessageMedia5 = tLRPC$Message.media;
                if (!(tLRPC$MessageMedia5 instanceof TLRPC$TL_messageMediaPhoto) && (!(tLRPC$MessageMedia5 instanceof TLRPC$TL_messageMediaDocument) || isStickerMessage(tLRPC$Message) || isAnimatedStickerMessage(tLRPC$Message))) {
                    TLRPC$MessageMedia tLRPC$MessageMedia6 = tLRPC$Message.media;
                    if ((tLRPC$MessageMedia6 instanceof TLRPC$TL_messageMediaEmpty) || (tLRPC$MessageMedia6 instanceof TLRPC$TL_messageMediaWebPage) || tLRPC$MessageMedia6 == null) {
                        return true;
                    }
                }
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

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r0.webpage;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isWallpaper() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r1 == 0) goto L_0x0018
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x0018
            java.lang.String r0 = r0.type
            java.lang.String r1 = "telegram_background"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0018
            r0 = 1
            goto L_0x0019
        L_0x0018:
            r0 = 0
        L_0x0019:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isWallpaper():boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0008, code lost:
        r0 = r0.webpage;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isTheme() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r1 == 0) goto L_0x0018
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            if (r0 == 0) goto L_0x0018
            java.lang.String r0 = r0.type
            java.lang.String r1 = "telegram_theme"
            boolean r0 = r1.equals(r0)
            if (r0 == 0) goto L_0x0018
            r0 = 1
            goto L_0x0019
        L_0x0018:
            r0 = 0
        L_0x0019:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isTheme():boolean");
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
        TLRPC$Photo tLRPC$Photo;
        int i;
        this.attachPathExists = false;
        this.mediaExists = false;
        if (this.type == 1 && FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
            File pathToMessage = FileLoader.getPathToMessage(this.messageOwner);
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
                File pathToMessage2 = FileLoader.getPathToMessage(this.messageOwner);
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
                int i2 = this.type;
                if (i2 == 0) {
                    TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        this.mediaExists = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true).exists();
                    }
                } else if (i2 == 11 && (tLRPC$Photo = this.messageOwner.action.photo) != null && !tLRPC$Photo.video_sizes.isEmpty()) {
                    this.mediaExists = FileLoader.getPathToAttach(tLRPC$Photo.video_sizes.get(0), true).exists();
                }
            } else if (isWallpaper()) {
                this.mediaExists = FileLoader.getPathToAttach(document, true).exists();
            } else {
                this.mediaExists = FileLoader.getPathToAttach(document).exists();
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00f8, code lost:
        r6 = (java.lang.String) r3.get(r5);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setQuery(java.lang.String r14) {
        /*
            r13 = this;
            boolean r0 = android.text.TextUtils.isEmpty(r14)
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
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
            if (r4 != 0) goto L_0x0053
            org.telegram.tgnet.TLRPC$Message r4 = r13.messageOwner
            java.lang.String r4 = r4.message
            java.lang.String r4 = r4.trim()
            java.lang.String r4 = r4.toLowerCase()
            boolean r5 = r4.contains(r14)
            if (r5 == 0) goto L_0x0048
            boolean r5 = r0.contains(r14)
            if (r5 != 0) goto L_0x0048
            r0.add(r14)
            r13.handleFoundWords(r0, r2)
            return
        L_0x0048:
            java.lang.String[] r4 = r4.split(r1)
            java.util.List r4 = java.util.Arrays.asList(r4)
            r3.addAll(r4)
        L_0x0053:
            org.telegram.tgnet.TLRPC$Document r4 = r13.getDocument()
            if (r4 == 0) goto L_0x007f
            org.telegram.tgnet.TLRPC$Document r4 = r13.getDocument()
            java.lang.String r4 = org.telegram.messenger.FileLoader.getDocumentFileName(r4)
            java.lang.String r4 = r4.toLowerCase()
            boolean r5 = r4.contains(r14)
            if (r5 == 0) goto L_0x0074
            boolean r5 = r0.contains(r14)
            if (r5 != 0) goto L_0x0074
            r0.add(r14)
        L_0x0074:
            java.lang.String[] r4 = r4.split(r1)
            java.util.List r4 = java.util.Arrays.asList(r4)
            r3.addAll(r4)
        L_0x007f:
            org.telegram.tgnet.TLRPC$Message r4 = r13.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r5 == 0) goto L_0x00b3
            org.telegram.tgnet.TLRPC$WebPage r4 = r4.webpage
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_webPage
            if (r5 == 0) goto L_0x00b3
            java.lang.String r5 = r4.title
            if (r5 != 0) goto L_0x0093
            java.lang.String r5 = r4.site_name
        L_0x0093:
            if (r5 == 0) goto L_0x00b3
            java.lang.String r4 = r5.toLowerCase()
            boolean r5 = r4.contains(r14)
            if (r5 == 0) goto L_0x00a8
            boolean r5 = r0.contains(r14)
            if (r5 != 0) goto L_0x00a8
            r0.add(r14)
        L_0x00a8:
            java.lang.String[] r4 = r4.split(r1)
            java.util.List r4 = java.util.Arrays.asList(r4)
            r3.addAll(r4)
        L_0x00b3:
            java.lang.String r4 = r13.getMusicAuthor()
            if (r4 == 0) goto L_0x00d7
            java.lang.String r4 = r4.toLowerCase()
            boolean r5 = r4.contains(r14)
            if (r5 == 0) goto L_0x00cc
            boolean r5 = r0.contains(r14)
            if (r5 != 0) goto L_0x00cc
            r0.add(r14)
        L_0x00cc:
            java.lang.String[] r14 = r4.split(r1)
            java.util.List r14 = java.util.Arrays.asList(r14)
            r3.addAll(r14)
        L_0x00d7:
            r14 = 0
            r1 = 0
        L_0x00d9:
            int r4 = r2.length
            if (r1 >= r4) goto L_0x0153
            r4 = r2[r1]
            int r5 = r4.length()
            r6 = 2
            if (r5 >= r6) goto L_0x00e6
            goto L_0x0150
        L_0x00e6:
            r5 = 0
        L_0x00e7:
            int r6 = r3.size()
            if (r5 >= r6) goto L_0x0150
            java.lang.Object r6 = r3.get(r5)
            boolean r6 = r0.contains(r6)
            if (r6 == 0) goto L_0x00f8
            goto L_0x014d
        L_0x00f8:
            java.lang.Object r6 = r3.get(r5)
            java.lang.String r6 = (java.lang.String) r6
            char r7 = r4.charAt(r14)
            int r7 = r6.indexOf(r7)
            if (r7 >= 0) goto L_0x0109
            goto L_0x014d
        L_0x0109:
            int r8 = r4.length()
            int r9 = r6.length()
            int r8 = java.lang.Math.max(r8, r9)
            if (r7 == 0) goto L_0x011b
            java.lang.String r6 = r6.substring(r7)
        L_0x011b:
            int r7 = r4.length()
            int r9 = r6.length()
            int r7 = java.lang.Math.min(r7, r9)
            r9 = 0
            r10 = 0
        L_0x0129:
            if (r9 >= r7) goto L_0x013a
            char r11 = r6.charAt(r9)
            char r12 = r4.charAt(r9)
            if (r11 != r12) goto L_0x013a
            int r10 = r10 + 1
            int r9 = r9 + 1
            goto L_0x0129
        L_0x013a:
            float r6 = (float) r10
            float r7 = (float) r8
            float r6 = r6 / r7
            double r6 = (double) r6
            r8 = 4602678819172646912(0x3feNUM, double:0.5)
            int r10 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r10 < 0) goto L_0x014d
            java.lang.Object r6 = r3.get(r5)
            java.lang.String r6 = (java.lang.String) r6
            r0.add(r6)
        L_0x014d:
            int r5 = r5 + 1
            goto L_0x00e7
        L_0x0150:
            int r1 = r1 + 1
            goto L_0x00d9
        L_0x0153:
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
                    Collections.sort(arrayList, MessageObject$$ExternalSyntheticLambda0.INSTANCE);
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
    public static /* synthetic */ int lambda$handleFoundWords$1(String str, String str2) {
        return str2.length() - str.length();
    }

    public void createMediaThumbs() {
        if (isVideo()) {
            TLRPC$Document document = getDocument();
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
            this.mediaThumb = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320), document);
            this.mediaSmallThumb = ImageLocation.getForDocument(closestPhotoSizeWithSize, document);
            return;
        }
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) && tLRPC$MessageMedia.photo != null && !this.photoThumbs.isEmpty()) {
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

    public boolean selectReaction(String str, boolean z, boolean z2) {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message.reactions == null) {
            tLRPC$Message.reactions = new TLRPC$TL_messageReactions();
            this.messageOwner.reactions.can_see_list = isFromGroup() || isFromUser();
        }
        TLRPC$TL_reactionCount tLRPC$TL_reactionCount = null;
        TLRPC$TL_reactionCount tLRPC$TL_reactionCount2 = null;
        for (int i = 0; i < this.messageOwner.reactions.results.size(); i++) {
            if (this.messageOwner.reactions.results.get(i).chosen) {
                tLRPC$TL_reactionCount = this.messageOwner.reactions.results.get(i);
            }
            if (this.messageOwner.reactions.results.get(i).reaction.equals(str)) {
                tLRPC$TL_reactionCount2 = this.messageOwner.reactions.results.get(i);
            }
        }
        if (tLRPC$TL_reactionCount != null && tLRPC$TL_reactionCount == tLRPC$TL_reactionCount2 && z) {
            return true;
        }
        if (tLRPC$TL_reactionCount == null || (tLRPC$TL_reactionCount != tLRPC$TL_reactionCount2 && !z2)) {
            if (tLRPC$TL_reactionCount != null) {
                tLRPC$TL_reactionCount.chosen = false;
                int i2 = tLRPC$TL_reactionCount.count - 1;
                tLRPC$TL_reactionCount.count = i2;
                if (i2 <= 0) {
                    this.messageOwner.reactions.results.remove(tLRPC$TL_reactionCount);
                }
                if (this.messageOwner.reactions.can_see_list) {
                    int i3 = 0;
                    while (i3 < this.messageOwner.reactions.recent_reactions.size()) {
                        if (getPeerId(this.messageOwner.reactions.recent_reactions.get(i3).peer_id) == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            this.messageOwner.reactions.recent_reactions.remove(i3);
                            i3--;
                        }
                        i3++;
                    }
                }
            }
            if (tLRPC$TL_reactionCount2 == null) {
                tLRPC$TL_reactionCount2 = new TLRPC$TL_reactionCount();
                tLRPC$TL_reactionCount2.reaction = str;
                this.messageOwner.reactions.results.add(tLRPC$TL_reactionCount2);
            }
            tLRPC$TL_reactionCount2.chosen = true;
            tLRPC$TL_reactionCount2.count++;
            if (this.messageOwner.reactions.can_see_list) {
                TLRPC$TL_messagePeerReaction tLRPC$TL_messagePeerReaction = new TLRPC$TL_messagePeerReaction();
                this.messageOwner.reactions.recent_reactions.add(0, tLRPC$TL_messagePeerReaction);
                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                tLRPC$TL_messagePeerReaction.peer_id = tLRPC$TL_peerUser;
                tLRPC$TL_peerUser.user_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                tLRPC$TL_messagePeerReaction.reaction = str;
            }
            this.reactionsChanged = true;
            return true;
        }
        tLRPC$TL_reactionCount.chosen = false;
        int i4 = tLRPC$TL_reactionCount.count - 1;
        tLRPC$TL_reactionCount.count = i4;
        if (i4 <= 0) {
            this.messageOwner.reactions.results.remove(tLRPC$TL_reactionCount);
        }
        if (this.messageOwner.reactions.can_see_list) {
            int i5 = 0;
            while (i5 < this.messageOwner.reactions.recent_reactions.size()) {
                if (getPeerId(this.messageOwner.reactions.recent_reactions.get(i5).peer_id) == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    this.messageOwner.reactions.recent_reactions.remove(i5);
                    i5--;
                }
                i5++;
            }
        }
        this.reactionsChanged = true;
        return false;
    }
}
