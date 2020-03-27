package org.telegram.messenger;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.Emoji;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$BotInlineResult;
import org.telegram.tgnet.TLRPC$Chat;
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
import org.telegram.tgnet.TLRPC$Page;
import org.telegram.tgnet.TLRPC$PageBlock;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$Photo;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$PollResults;
import org.telegram.tgnet.TLRPC$SecureValueType;
import org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC$TL_chatAdminRights;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC$TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC$TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC$TL_documentEmpty;
import org.telegram.tgnet.TLRPC$TL_documentEncrypted;
import org.telegram.tgnet.TLRPC$TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC$TL_game;
import org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC$TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC$TL_message;
import org.telegram.tgnet.TLRPC$TL_messageActionBotAllowed;
import org.telegram.tgnet.TLRPC$TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC$TL_messageActionChannelMigrateFrom;
import org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC$TL_messageActionChatCreate;
import org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto;
import org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC$TL_messageActionChatEditTitle;
import org.telegram.tgnet.TLRPC$TL_messageActionChatJoinedByLink;
import org.telegram.tgnet.TLRPC$TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC$TL_messageActionContactSignUp;
import org.telegram.tgnet.TLRPC$TL_messageActionCreatedBroadcastList;
import org.telegram.tgnet.TLRPC$TL_messageActionCustomAction;
import org.telegram.tgnet.TLRPC$TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC$TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC$TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC$TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC$TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC$TL_messageActionScreenshotTaken;
import org.telegram.tgnet.TLRPC$TL_messageActionSecureValuesSent;
import org.telegram.tgnet.TLRPC$TL_messageActionTTLChange;
import org.telegram.tgnet.TLRPC$TL_messageActionUserJoined;
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
import org.telegram.tgnet.TLRPC$TL_messageReactions;
import org.telegram.tgnet.TLRPC$TL_messageService;
import org.telegram.tgnet.TLRPC$TL_message_secret;
import org.telegram.tgnet.TLRPC$TL_pageBlockCollage;
import org.telegram.tgnet.TLRPC$TL_pageBlockPhoto;
import org.telegram.tgnet.TLRPC$TL_pageBlockSlideshow;
import org.telegram.tgnet.TLRPC$TL_pageBlockVideo;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC$TL_photo;
import org.telegram.tgnet.TLRPC$TL_photoCachedSize;
import org.telegram.tgnet.TLRPC$TL_photoEmpty;
import org.telegram.tgnet.TLRPC$TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC$TL_pollAnswer;
import org.telegram.tgnet.TLRPC$TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.tgnet.TLRPC$TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeAddress;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeBankStatement;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeDriverLicense;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeEmail;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeIdentityCard;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeInternalPassport;
import org.telegram.tgnet.TLRPC$TL_secureValueTypePassport;
import org.telegram.tgnet.TLRPC$TL_secureValueTypePassportRegistration;
import org.telegram.tgnet.TLRPC$TL_secureValueTypePersonalDetails;
import org.telegram.tgnet.TLRPC$TL_secureValueTypePhone;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeRentalAgreement;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeTemporaryRegistration;
import org.telegram.tgnet.TLRPC$TL_secureValueTypeUtilityBill;
import org.telegram.tgnet.TLRPC$TL_webPage;
import org.telegram.tgnet.TLRPC$TL_webPageAttributeTheme;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebDocument;
import org.telegram.tgnet.TLRPC$WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;

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
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_POLL = 17;
    public static final int TYPE_ROUND_VIDEO = 5;
    public static final int TYPE_STICKER = 13;
    public static final int TYPE_VIDEO = 3;
    static final String[] excludeWords = {" vs. ", " vs ", " versus ", " ft. ", " ft ", " featuring ", " feat. ", " feat ", " presents ", " pres. ", " pres ", " and ", " & ", " . "};
    public static Pattern instagramUrlPattern;
    public static Pattern urlPattern;
    public static Pattern videoTimeUrlPattern;
    public boolean attachPathExists;
    public int audioPlayerDuration;
    public float audioProgress;
    public int audioProgressMs;
    public int audioProgressSec;
    public StringBuilder botButtonsLayout;
    public float bufferedProgress;
    public boolean cancelEditing;
    public CharSequence caption;
    public ArrayList<TLRPC$TL_pollAnswer> checkedVotes;
    public int contentType;
    public int currentAccount;
    public TLRPC$TL_channelAdminLogEvent currentEvent;
    public String customReplyName;
    public String dateKey;
    public boolean deleted;
    public CharSequence editingMessage;
    public ArrayList<TLRPC$MessageEntity> editingMessageEntities;
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
    public boolean isDateObject;
    public boolean isRestrictedMessage;
    private int isRoundVideoCached;
    public int lastLineWidth;
    private boolean layoutCreated;
    public int linesCount;
    public CharSequence linkDescription;
    public long loadedFileSize;
    public boolean localChannel;
    public boolean localEdit;
    public long localGroupId;
    public String localName;
    public long localSentGroupId;
    public int localType;
    public String localUserName;
    public boolean mediaExists;
    public TLRPC$Message messageOwner;
    public CharSequence messageText;
    public String monthKey;
    public ArrayList<TLRPC$PhotoSize> photoThumbs;
    public ArrayList<TLRPC$PhotoSize> photoThumbs2;
    public TLObject photoThumbsObject;
    public TLObject photoThumbsObject2;
    public long pollLastCheckTime;
    public boolean pollVisibleOnScreen;
    public String previousAttachPath;
    public String previousCaption;
    public ArrayList<TLRPC$MessageEntity> previousCaptionEntities;
    public TLRPC$MessageMedia previousMedia;
    public MessageObject replyMessageObject;
    public boolean resendAsIs;
    public boolean scheduled;
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
    public boolean wasUnread;

    public void checkForScam() {
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
        public int charactersEnd;
        public int charactersOffset;
        public byte directionFlags;
        public int height;
        public int heightByOffset;
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
        private int firstSpanAdditionalSize = 200;
        public long groupId;
        public boolean hasSibling;
        private int maxSizeWidth = 800;
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public ArrayList<GroupedMessagePosition> posArray = new ArrayList<>();
        public HashMap<MessageObject, GroupedMessagePosition> positions = new HashMap<>();

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

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v15, resolved type: byte} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v0, resolved type: byte} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v20, resolved type: byte} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r17v1, resolved type: byte} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v25, resolved type: byte} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v25, resolved type: byte} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v26, resolved type: byte} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v45, resolved type: byte} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v73, resolved type: byte} */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x005e, code lost:
            if ((r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice) == false) goto L_0x0062;
         */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void calculate() {
            /*
                r36 = this;
                r0 = r36
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r0.posArray
                r1.clear()
                java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r1 = r0.positions
                r1.clear()
                java.util.ArrayList<org.telegram.messenger.MessageObject> r1 = r0.messages
                int r1 = r1.size()
                r2 = 1
                if (r1 > r2) goto L_0x0016
                return
            L_0x0016:
                r3 = 1145798656(0x444b8000, float:814.0)
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                r5 = 0
                r0.hasSibling = r5
                r7 = 0
                r8 = 0
                r9 = 1065353216(0x3var_, float:1.0)
                r10 = 0
                r11 = 0
            L_0x0027:
                r12 = 1067030938(0x3var_a, float:1.2)
                if (r7 >= r1) goto L_0x00be
                java.util.ArrayList<org.telegram.messenger.MessageObject> r13 = r0.messages
                java.lang.Object r13 = r13.get(r7)
                org.telegram.messenger.MessageObject r13 = (org.telegram.messenger.MessageObject) r13
                if (r7 != 0) goto L_0x0063
                boolean r11 = r13.isOutOwner()
                if (r11 != 0) goto L_0x0062
                org.telegram.tgnet.TLRPC$Message r8 = r13.messageOwner
                org.telegram.tgnet.TLRPC$MessageFwdHeader r8 = r8.fwd_from
                if (r8 == 0) goto L_0x0046
                org.telegram.tgnet.TLRPC$Peer r8 = r8.saved_from_peer
                if (r8 != 0) goto L_0x0060
            L_0x0046:
                org.telegram.tgnet.TLRPC$Message r8 = r13.messageOwner
                int r14 = r8.from_id
                if (r14 <= 0) goto L_0x0062
                org.telegram.tgnet.TLRPC$Peer r14 = r8.to_id
                int r15 = r14.channel_id
                if (r15 != 0) goto L_0x0060
                int r14 = r14.chat_id
                if (r14 != 0) goto L_0x0060
                org.telegram.tgnet.TLRPC$MessageMedia r8 = r8.media
                boolean r14 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
                if (r14 != 0) goto L_0x0060
                boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
                if (r8 == 0) goto L_0x0062
            L_0x0060:
                r8 = 1
                goto L_0x0063
            L_0x0062:
                r8 = 0
            L_0x0063:
                java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r14 = r13.photoThumbs
                int r15 = org.telegram.messenger.AndroidUtilities.getPhotoSize()
                org.telegram.tgnet.TLRPC$PhotoSize r14 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r14, r15)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r15 = new org.telegram.messenger.MessageObject$GroupedMessagePosition
                r15.<init>()
                int r6 = r1 + -1
                if (r7 != r6) goto L_0x0078
                r6 = 1
                goto L_0x0079
            L_0x0078:
                r6 = 0
            L_0x0079:
                r15.last = r6
                if (r14 != 0) goto L_0x0080
                r6 = 1065353216(0x3var_, float:1.0)
                goto L_0x0087
            L_0x0080:
                int r6 = r14.w
                float r6 = (float) r6
                int r14 = r14.h
                float r14 = (float) r14
                float r6 = r6 / r14
            L_0x0087:
                r15.aspectRatio = r6
                int r12 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
                if (r12 <= 0) goto L_0x0094
                java.lang.String r6 = "w"
                r4.append(r6)
                goto L_0x00a6
            L_0x0094:
                r12 = 1061997773(0x3f4ccccd, float:0.8)
                int r6 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
                if (r6 >= 0) goto L_0x00a1
                java.lang.String r6 = "n"
                r4.append(r6)
                goto L_0x00a6
            L_0x00a1:
                java.lang.String r6 = "q"
                r4.append(r6)
            L_0x00a6:
                float r6 = r15.aspectRatio
                float r9 = r9 + r6
                r12 = 1073741824(0x40000000, float:2.0)
                int r6 = (r6 > r12 ? 1 : (r6 == r12 ? 0 : -1))
                if (r6 <= 0) goto L_0x00b0
                r10 = 1
            L_0x00b0:
                java.util.HashMap<org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessagePosition> r6 = r0.positions
                r6.put(r13, r15)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r6 = r0.posArray
                r6.add(r15)
                int r7 = r7 + 1
                goto L_0x0027
            L_0x00be:
                if (r8 == 0) goto L_0x00cc
                int r6 = r0.maxSizeWidth
                int r6 = r6 + -50
                r0.maxSizeWidth = r6
                int r6 = r0.firstSpanAdditionalSize
                int r6 = r6 + 50
                r0.firstSpanAdditionalSize = r6
            L_0x00cc:
                r6 = 1123024896(0x42var_, float:120.0)
                int r7 = org.telegram.messenger.AndroidUtilities.dp(r6)
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
                float r6 = (float) r6
                android.graphics.Point r8 = org.telegram.messenger.AndroidUtilities.displaySize
                int r13 = r8.x
                int r8 = r8.y
                int r8 = java.lang.Math.min(r13, r8)
                float r8 = (float) r8
                int r13 = r0.maxSizeWidth
                float r13 = (float) r13
                float r8 = r8 / r13
                float r6 = r6 / r8
                int r6 = (int) r6
                r8 = 1109393408(0x42200000, float:40.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r8)
                float r8 = (float) r8
                android.graphics.Point r13 = org.telegram.messenger.AndroidUtilities.displaySize
                int r14 = r13.x
                int r13 = r13.y
                int r13 = java.lang.Math.min(r14, r13)
                float r13 = (float) r13
                int r14 = r0.maxSizeWidth
                float r15 = (float) r14
                float r13 = r13 / r15
                float r8 = r8 / r13
                int r8 = (int) r8
                float r13 = (float) r14
                float r13 = r13 / r3
                float r14 = (float) r1
                float r9 = r9 / r14
                r14 = 1120403456(0x42CLASSNAME, float:100.0)
                int r14 = org.telegram.messenger.AndroidUtilities.dp(r14)
                float r14 = (float) r14
                float r14 = r14 / r3
                r15 = 4
                r12 = 3
                r3 = 2
                if (r10 != 0) goto L_0x0521
                if (r1 == r3) goto L_0x0117
                if (r1 == r12) goto L_0x0117
                if (r1 != r15) goto L_0x0521
            L_0x0117:
                r10 = 1053609165(0x3ecccccd, float:0.4)
                r15 = 1137410048(0x43cb8000, float:407.0)
                if (r1 != r3) goto L_0x0245
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r7 = r0.posArray
                java.lang.Object r7 = r7.get(r5)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r7 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r7
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r8 = r0.posArray
                java.lang.Object r8 = r8.get(r2)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8
                java.lang.String r4 = r4.toString()
                java.lang.String r12 = "ww"
                boolean r14 = r4.equals(r12)
                if (r14 == 0) goto L_0x0199
                double r2 = (double) r9
                r18 = 4608983858650965606(0x3ffNUM, double:1.4)
                double r13 = (double) r13
                java.lang.Double.isNaN(r13)
                double r13 = r13 * r18
                int r9 = (r2 > r13 ? 1 : (r2 == r13 ? 0 : -1))
                if (r9 <= 0) goto L_0x0199
                float r2 = r7.aspectRatio
                float r3 = r8.aspectRatio
                float r9 = r2 - r3
                double r13 = (double) r9
                r18 = 4596373779694328218(0x3fCLASSNAMEa, double:0.2)
                int r9 = (r13 > r18 ? 1 : (r13 == r18 ? 0 : -1))
                if (r9 >= 0) goto L_0x0199
                int r4 = r0.maxSizeWidth
                float r6 = (float) r4
                float r6 = r6 / r2
                float r2 = (float) r4
                float r2 = r2 / r3
                float r2 = java.lang.Math.min(r2, r15)
                float r2 = java.lang.Math.min(r6, r2)
                int r2 = java.lang.Math.round(r2)
                float r2 = (float) r2
                r3 = 1145798656(0x444b8000, float:814.0)
                float r2 = r2 / r3
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 0
                int r3 = r0.maxSizeWidth
                r25 = 7
                r18 = r7
                r23 = r3
                r24 = r2
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r21 = 1
                r22 = 1
                int r3 = r0.maxSizeWidth
                r25 = 11
                r18 = r8
                r23 = r3
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                goto L_0x051e
            L_0x0199:
                boolean r2 = r4.equals(r12)
                if (r2 != 0) goto L_0x0208
                java.lang.String r2 = "qq"
                boolean r2 = r4.equals(r2)
                if (r2 == 0) goto L_0x01a8
                goto L_0x0208
            L_0x01a8:
                int r2 = r0.maxSizeWidth
                float r3 = (float) r2
                float r3 = r3 * r10
                float r2 = (float) r2
                float r4 = r7.aspectRatio
                float r2 = r2 / r4
                r9 = 1065353216(0x3var_, float:1.0)
                float r4 = r9 / r4
                float r10 = r8.aspectRatio
                float r9 = r9 / r10
                float r4 = r4 + r9
                float r2 = r2 / r4
                int r2 = java.lang.Math.round(r2)
                float r2 = (float) r2
                float r2 = java.lang.Math.max(r3, r2)
                int r2 = (int) r2
                int r3 = r0.maxSizeWidth
                int r3 = r3 - r2
                if (r3 >= r6) goto L_0x01cd
                int r3 = r6 - r3
                int r2 = r2 - r3
                r3 = r6
            L_0x01cd:
                float r4 = (float) r3
                float r6 = r7.aspectRatio
                float r4 = r4 / r6
                float r6 = (float) r2
                float r9 = r8.aspectRatio
                float r6 = r6 / r9
                float r4 = java.lang.Math.min(r4, r6)
                int r4 = java.lang.Math.round(r4)
                float r4 = (float) r4
                r6 = 1145798656(0x444b8000, float:814.0)
                float r4 = java.lang.Math.min(r6, r4)
                float r4 = r4 / r6
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 0
                r25 = 13
                r18 = r7
                r23 = r3
                r24 = r4
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r25 = 14
                r18 = r8
                r23 = r2
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                goto L_0x037d
            L_0x0208:
                int r2 = r0.maxSizeWidth
                r3 = 2
                int r2 = r2 / r3
                float r3 = (float) r2
                float r4 = r7.aspectRatio
                float r4 = r3 / r4
                float r6 = r8.aspectRatio
                float r3 = r3 / r6
                r6 = 1145798656(0x444b8000, float:814.0)
                float r3 = java.lang.Math.min(r3, r6)
                float r3 = java.lang.Math.min(r4, r3)
                int r3 = java.lang.Math.round(r3)
                float r3 = (float) r3
                float r3 = r3 / r6
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 0
                r25 = 13
                r18 = r7
                r23 = r2
                r24 = r3
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r25 = 14
                r18 = r8
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                goto L_0x037d
            L_0x0245:
                r2 = 1141264221(0x44064f5d, float:537.24005)
                if (r1 != r12) goto L_0x0380
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r0.posArray
                java.lang.Object r3 = r3.get(r5)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r7 = r0.posArray
                r9 = 1
                java.lang.Object r7 = r7.get(r9)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r7 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r7
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r9 = r0.posArray
                r10 = 2
                java.lang.Object r9 = r9.get(r10)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r9 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r9
                char r4 = r4.charAt(r5)
                r10 = 110(0x6e, float:1.54E-43)
                if (r4 != r10) goto L_0x0313
                float r2 = r7.aspectRatio
                int r4 = r0.maxSizeWidth
                float r4 = (float) r4
                float r4 = r4 * r2
                float r10 = r9.aspectRatio
                float r10 = r10 + r2
                float r4 = r4 / r10
                int r2 = java.lang.Math.round(r4)
                float r2 = (float) r2
                float r2 = java.lang.Math.min(r15, r2)
                r4 = 1145798656(0x444b8000, float:814.0)
                float r10 = r4 - r2
                float r4 = (float) r6
                int r6 = r0.maxSizeWidth
                float r6 = (float) r6
                r12 = 1056964608(0x3var_, float:0.5)
                float r6 = r6 * r12
                float r12 = r9.aspectRatio
                float r12 = r12 * r2
                float r13 = r7.aspectRatio
                float r13 = r13 * r10
                float r12 = java.lang.Math.min(r12, r13)
                int r12 = java.lang.Math.round(r12)
                float r12 = (float) r12
                float r6 = java.lang.Math.min(r6, r12)
                float r4 = java.lang.Math.max(r4, r6)
                int r4 = (int) r4
                float r6 = r3.aspectRatio
                r12 = 1145798656(0x444b8000, float:814.0)
                float r6 = r6 * r12
                float r8 = (float) r8
                float r6 = r6 + r8
                int r8 = r0.maxSizeWidth
                int r8 = r8 - r4
                float r8 = (float) r8
                float r6 = java.lang.Math.min(r6, r8)
                int r6 = java.lang.Math.round(r6)
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 1
                r24 = 1065353216(0x3var_, float:1.0)
                r25 = 13
                r18 = r3
                r23 = r6
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r22 = 0
                r8 = 1145798656(0x444b8000, float:814.0)
                float r10 = r10 / r8
                r25 = 6
                r18 = r7
                r23 = r4
                r24 = r10
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 0
                r21 = 1
                r22 = 1
                r8 = 1145798656(0x444b8000, float:814.0)
                float r2 = r2 / r8
                r25 = 10
                r18 = r9
                r24 = r2
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                int r8 = r0.maxSizeWidth
                r9.spanSize = r8
                r12 = 2
                float[] r13 = new float[r12]
                r13[r5] = r2
                r2 = 1
                r13[r2] = r10
                r3.siblingHeights = r13
                if (r11 == 0) goto L_0x030b
                int r8 = r8 - r4
                r3.spanSize = r8
                goto L_0x0310
            L_0x030b:
                int r8 = r8 - r6
                r7.spanSize = r8
                r9.leftSpanOffset = r6
            L_0x0310:
                r0.hasSibling = r2
                goto L_0x037d
            L_0x0313:
                int r4 = r0.maxSizeWidth
                float r4 = (float) r4
                float r6 = r3.aspectRatio
                float r4 = r4 / r6
                float r2 = java.lang.Math.min(r4, r2)
                int r2 = java.lang.Math.round(r2)
                float r2 = (float) r2
                r4 = 1145798656(0x444b8000, float:814.0)
                float r2 = r2 / r4
                r19 = 0
                r20 = 1
                r21 = 0
                r22 = 0
                int r4 = r0.maxSizeWidth
                r25 = 7
                r18 = r3
                r23 = r4
                r24 = r2
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                int r3 = r0.maxSizeWidth
                r4 = 2
                int r3 = r3 / r4
                r4 = 1145798656(0x444b8000, float:814.0)
                float r2 = r4 - r2
                float r6 = (float) r3
                float r8 = r7.aspectRatio
                float r8 = r6 / r8
                float r10 = r9.aspectRatio
                float r6 = r6 / r10
                float r6 = java.lang.Math.min(r8, r6)
                int r6 = java.lang.Math.round(r6)
                float r6 = (float) r6
                float r2 = java.lang.Math.min(r2, r6)
                float r2 = r2 / r4
                int r4 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
                if (r4 >= 0) goto L_0x035f
                r2 = r14
            L_0x035f:
                r19 = 0
                r20 = 0
                r21 = 1
                r22 = 1
                r25 = 9
                r18 = r7
                r23 = r3
                r24 = r2
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r25 = 10
                r18 = r9
                r18.set(r19, r20, r21, r22, r23, r24, r25)
            L_0x037d:
                r2 = 1
                goto L_0x073c
            L_0x0380:
                r3 = 4
                if (r1 != r3) goto L_0x051e
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r0.posArray
                java.lang.Object r3 = r3.get(r5)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r9 = r0.posArray
                r13 = 1
                java.lang.Object r9 = r9.get(r13)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r9 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r9
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r13 = r0.posArray
                r15 = 2
                java.lang.Object r13 = r13.get(r15)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r13 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r13
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r15 = r0.posArray
                java.lang.Object r15 = r15.get(r12)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r15 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r15
                char r4 = r4.charAt(r5)
                r5 = 119(0x77, float:1.67E-43)
                r12 = 1051260355(0x3ea8f5c3, float:0.33)
                if (r4 != r5) goto L_0x0467
                int r4 = r0.maxSizeWidth
                float r4 = (float) r4
                float r5 = r3.aspectRatio
                float r4 = r4 / r5
                float r2 = java.lang.Math.min(r4, r2)
                int r2 = java.lang.Math.round(r2)
                float r2 = (float) r2
                r4 = 1145798656(0x444b8000, float:814.0)
                float r2 = r2 / r4
                r19 = 0
                r20 = 2
                r21 = 0
                r22 = 0
                int r4 = r0.maxSizeWidth
                r25 = 7
                r18 = r3
                r23 = r4
                r24 = r2
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                int r3 = r0.maxSizeWidth
                float r3 = (float) r3
                float r4 = r9.aspectRatio
                float r5 = r13.aspectRatio
                float r4 = r4 + r5
                float r5 = r15.aspectRatio
                float r4 = r4 + r5
                float r3 = r3 / r4
                int r3 = java.lang.Math.round(r3)
                float r3 = (float) r3
                float r4 = (float) r6
                int r5 = r0.maxSizeWidth
                float r5 = (float) r5
                float r5 = r5 * r10
                float r6 = r9.aspectRatio
                float r6 = r6 * r3
                float r5 = java.lang.Math.min(r5, r6)
                float r5 = java.lang.Math.max(r4, r5)
                int r5 = (int) r5
                int r6 = r0.maxSizeWidth
                float r6 = (float) r6
                float r6 = r6 * r12
                float r4 = java.lang.Math.max(r4, r6)
                float r6 = r15.aspectRatio
                float r6 = r6 * r3
                float r4 = java.lang.Math.max(r4, r6)
                int r4 = (int) r4
                int r6 = r0.maxSizeWidth
                int r6 = r6 - r5
                int r6 = r6 - r4
                r7 = 1114112000(0x42680000, float:58.0)
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                if (r6 >= r8) goto L_0x0428
                int r8 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r8 = r8 - r6
                int r6 = org.telegram.messenger.AndroidUtilities.dp(r7)
                int r7 = r8 / 2
                int r5 = r5 - r7
                int r8 = r8 - r7
                int r4 = r4 - r8
            L_0x0428:
                r23 = r5
                r5 = 1145798656(0x444b8000, float:814.0)
                float r2 = r5 - r2
                float r2 = java.lang.Math.min(r2, r3)
                float r2 = r2 / r5
                int r3 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
                if (r3 >= 0) goto L_0x0439
                r2 = r14
            L_0x0439:
                r19 = 0
                r20 = 0
                r21 = 1
                r22 = 1
                r25 = 9
                r18 = r9
                r24 = r2
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r25 = 8
                r18 = r13
                r23 = r6
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 2
                r20 = 2
                r25 = 10
                r18 = r15
                r23 = r4
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r2 = 2
                goto L_0x073c
            L_0x0467:
                float r2 = r9.aspectRatio
                r4 = 1065353216(0x3var_, float:1.0)
                float r2 = r4 / r2
                float r5 = r13.aspectRatio
                float r5 = r4 / r5
                float r2 = r2 + r5
                float r5 = r15.aspectRatio
                float r5 = r4 / r5
                float r2 = r2 + r5
                r4 = 1145798656(0x444b8000, float:814.0)
                float r2 = r4 / r2
                int r2 = java.lang.Math.round(r2)
                int r2 = java.lang.Math.max(r6, r2)
                float r5 = (float) r7
                float r6 = (float) r2
                float r7 = r9.aspectRatio
                float r7 = r6 / r7
                float r7 = java.lang.Math.max(r5, r7)
                float r7 = r7 / r4
                float r7 = java.lang.Math.min(r12, r7)
                float r10 = r13.aspectRatio
                float r6 = r6 / r10
                float r5 = java.lang.Math.max(r5, r6)
                float r5 = r5 / r4
                float r5 = java.lang.Math.min(r12, r5)
                r6 = 1065353216(0x3var_, float:1.0)
                float r6 = r6 - r7
                float r6 = r6 - r5
                float r10 = r3.aspectRatio
                float r4 = r4 * r10
                float r8 = (float) r8
                float r4 = r4 + r8
                int r8 = r0.maxSizeWidth
                int r8 = r8 - r2
                float r8 = (float) r8
                float r4 = java.lang.Math.min(r4, r8)
                int r4 = java.lang.Math.round(r4)
                r19 = 0
                r20 = 0
                r21 = 0
                r22 = 2
                float r8 = r7 + r5
                float r24 = r8 + r6
                r25 = 13
                r18 = r3
                r23 = r4
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 1
                r20 = 1
                r22 = 0
                r25 = 6
                r18 = r9
                r23 = r2
                r24 = r7
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                r19 = 0
                r21 = 1
                r22 = 1
                r25 = 2
                r18 = r13
                r24 = r5
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                int r8 = r0.maxSizeWidth
                r13.spanSize = r8
                r21 = 2
                r22 = 2
                r25 = 10
                r18 = r15
                r24 = r6
                r18.set(r19, r20, r21, r22, r23, r24, r25)
                int r8 = r0.maxSizeWidth
                r15.spanSize = r8
                if (r11 == 0) goto L_0x0505
                int r8 = r8 - r2
                r3.spanSize = r8
                goto L_0x050c
            L_0x0505:
                int r8 = r8 - r4
                r9.spanSize = r8
                r13.leftSpanOffset = r4
                r15.leftSpanOffset = r4
            L_0x050c:
                r2 = 3
                float[] r2 = new float[r2]
                r4 = 0
                r2[r4] = r7
                r4 = 1
                r2[r4] = r5
                r5 = 2
                r2[r5] = r6
                r3.siblingHeights = r2
                r0.hasSibling = r4
                goto L_0x037d
            L_0x051e:
                r2 = 0
                goto L_0x073c
            L_0x0521:
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r2 = r0.posArray
                int r2 = r2.size()
                float[] r3 = new float[r2]
                r4 = 0
            L_0x052a:
                if (r4 >= r1) goto L_0x056d
                r5 = 1066192077(0x3f8ccccd, float:1.1)
                int r5 = (r9 > r5 ? 1 : (r9 == r5 ? 0 : -1))
                if (r5 <= 0) goto L_0x0546
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r0.posArray
                java.lang.Object r5 = r5.get(r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5
                float r5 = r5.aspectRatio
                r7 = 1065353216(0x3var_, float:1.0)
                float r5 = java.lang.Math.max(r7, r5)
                r3[r4] = r5
                goto L_0x0558
            L_0x0546:
                r7 = 1065353216(0x3var_, float:1.0)
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r5 = r0.posArray
                java.lang.Object r5 = r5.get(r4)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5
                float r5 = r5.aspectRatio
                float r5 = java.lang.Math.min(r7, r5)
                r3[r4] = r5
            L_0x0558:
                r5 = 1059760867(0x3f2aaae3, float:0.66667)
                r8 = 1071225242(0x3fd9999a, float:1.7)
                r10 = r3[r4]
                float r8 = java.lang.Math.min(r8, r10)
                float r5 = java.lang.Math.max(r5, r8)
                r3[r4] = r5
                int r4 = r4 + 1
                goto L_0x052a
            L_0x056d:
                java.util.ArrayList r4 = new java.util.ArrayList
                r4.<init>()
                r5 = 1
            L_0x0573:
                if (r5 >= r2) goto L_0x0591
                int r7 = r2 - r5
                r8 = 3
                if (r5 > r8) goto L_0x058e
                if (r7 <= r8) goto L_0x057d
                goto L_0x058e
            L_0x057d:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r8 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r10 = 0
                float r12 = r0.multiHeight(r3, r10, r5)
                float r10 = r0.multiHeight(r3, r5, r2)
                r8.<init>(r5, r7, r12, r10)
                r4.add(r8)
            L_0x058e:
                int r5 = r5 + 1
                goto L_0x0573
            L_0x0591:
                r5 = 1
            L_0x0592:
                int r7 = r2 + -1
                if (r5 >= r7) goto L_0x05d3
                r7 = 1
            L_0x0597:
                int r8 = r2 - r5
                if (r7 >= r8) goto L_0x05d0
                int r8 = r8 - r7
                r10 = 3
                if (r5 > r10) goto L_0x05cd
                r12 = 1062836634(0x3var_a, float:0.85)
                int r12 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
                if (r12 >= 0) goto L_0x05a8
                r12 = 4
                goto L_0x05a9
            L_0x05a8:
                r12 = 3
            L_0x05a9:
                if (r7 > r12) goto L_0x05cd
                if (r8 <= r10) goto L_0x05ae
                goto L_0x05cd
            L_0x05ae:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r10 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r12 = 0
                float r23 = r0.multiHeight(r3, r12, r5)
                int r12 = r5 + r7
                float r24 = r0.multiHeight(r3, r5, r12)
                float r25 = r0.multiHeight(r3, r12, r2)
                r19 = r10
                r20 = r5
                r21 = r7
                r22 = r8
                r19.<init>(r20, r21, r22, r23, r24, r25)
                r4.add(r10)
            L_0x05cd:
                int r7 = r7 + 1
                goto L_0x0597
            L_0x05d0:
                int r5 = r5 + 1
                goto L_0x0592
            L_0x05d3:
                r5 = 1
            L_0x05d4:
                int r7 = r2 + -2
                if (r5 >= r7) goto L_0x061d
                r7 = 1
            L_0x05d9:
                int r8 = r2 - r5
                if (r7 >= r8) goto L_0x061a
                r9 = 1
            L_0x05de:
                int r10 = r8 - r7
                if (r9 >= r10) goto L_0x0617
                int r10 = r10 - r9
                r12 = 3
                if (r5 > r12) goto L_0x0614
                if (r7 > r12) goto L_0x0614
                if (r9 > r12) goto L_0x0614
                if (r10 <= r12) goto L_0x05ed
                goto L_0x0614
            L_0x05ed:
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r12 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt
                r13 = 0
                float r32 = r0.multiHeight(r3, r13, r5)
                int r13 = r5 + r7
                float r33 = r0.multiHeight(r3, r5, r13)
                int r15 = r13 + r9
                float r34 = r0.multiHeight(r3, r13, r15)
                float r35 = r0.multiHeight(r3, r15, r2)
                r27 = r12
                r28 = r5
                r29 = r7
                r30 = r9
                r31 = r10
                r27.<init>(r28, r29, r30, r31, r32, r33, r34, r35)
                r4.add(r12)
            L_0x0614:
                int r9 = r9 + 1
                goto L_0x05de
            L_0x0617:
                int r7 = r7 + 1
                goto L_0x05d9
            L_0x061a:
                int r5 = r5 + 1
                goto L_0x05d4
            L_0x061d:
                int r2 = r0.maxSizeWidth
                r5 = 3
                int r2 = r2 / r5
                r5 = 4
                int r2 = r2 * 4
                float r2 = (float) r2
                r9 = 0
                r10 = 0
                r12 = 0
            L_0x0628:
                int r13 = r4.size()
                if (r9 >= r13) goto L_0x06ac
                java.lang.Object r13 = r4.get(r9)
                org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt r13 = (org.telegram.messenger.MessageObject.GroupedMessages.MessageGroupedLayoutAttempt) r13
                r15 = 2139095039(0x7f7fffff, float:3.4028235E38)
                r15 = 0
                r16 = 2139095039(0x7f7fffff, float:3.4028235E38)
                r18 = 0
            L_0x063d:
                float[] r5 = r13.heights
                int r7 = r5.length
                if (r15 >= r7) goto L_0x0653
                r7 = r5[r15]
                float r18 = r18 + r7
                r7 = r5[r15]
                int r7 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1))
                if (r7 >= 0) goto L_0x0650
                r5 = r5[r15]
                r16 = r5
            L_0x0650:
                int r15 = r15 + 1
                goto L_0x063d
            L_0x0653:
                float r18 = r18 - r2
                float r5 = java.lang.Math.abs(r18)
                int[] r7 = r13.lineCounts
                int r15 = r7.length
                r8 = 1
                r21 = r2
                if (r15 <= r8) goto L_0x0690
                r15 = 0
                r2 = r7[r15]
                r15 = r7[r8]
                if (r2 > r15) goto L_0x0689
                int r2 = r7.length
                r15 = 2
                if (r2 <= r15) goto L_0x0678
                r2 = r7[r8]
                r7 = r7[r15]
                if (r2 > r7) goto L_0x0673
                goto L_0x0678
            L_0x0673:
                r2 = 1067030938(0x3var_a, float:1.2)
                r8 = 3
                goto L_0x068d
            L_0x0678:
                int[] r2 = r13.lineCounts
                int r7 = r2.length
                r8 = 3
                if (r7 <= r8) goto L_0x0685
                r7 = r2[r15]
                r2 = r2[r8]
                if (r7 <= r2) goto L_0x0685
                goto L_0x068a
            L_0x0685:
                r2 = 1067030938(0x3var_a, float:1.2)
                goto L_0x0694
            L_0x0689:
                r8 = 3
            L_0x068a:
                r2 = 1067030938(0x3var_a, float:1.2)
            L_0x068d:
                float r5 = r5 * r2
                goto L_0x0694
            L_0x0690:
                r2 = 1067030938(0x3var_a, float:1.2)
                r8 = 3
            L_0x0694:
                float r7 = (float) r6
                int r7 = (r16 > r7 ? 1 : (r16 == r7 ? 0 : -1))
                if (r7 >= 0) goto L_0x069d
                r7 = 1069547520(0x3fCLASSNAME, float:1.5)
                float r5 = r5 * r7
            L_0x069d:
                if (r10 == 0) goto L_0x06a3
                int r7 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
                if (r7 >= 0) goto L_0x06a5
            L_0x06a3:
                r12 = r5
                r10 = r13
            L_0x06a5:
                int r9 = r9 + 1
                r2 = r21
                r5 = 4
                goto L_0x0628
            L_0x06ac:
                if (r10 != 0) goto L_0x06af
                return
            L_0x06af:
                r2 = 0
                r4 = 0
                r5 = 0
            L_0x06b2:
                int[] r6 = r10.lineCounts
                int r7 = r6.length
                if (r4 >= r7) goto L_0x073b
                r6 = r6[r4]
                float[] r7 = r10.heights
                r7 = r7[r4]
                int r8 = r0.maxSizeWidth
                int r9 = r6 + -1
                int r5 = java.lang.Math.max(r5, r9)
                r12 = r8
                r8 = 0
                r13 = 0
            L_0x06c8:
                if (r8 >= r6) goto L_0x0722
                r15 = r3[r2]
                float r15 = r15 * r7
                int r15 = (int) r15
                int r12 = r12 - r15
                r16 = r3
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r0.posArray
                java.lang.Object r3 = r3.get(r2)
                r26 = r3
                org.telegram.messenger.MessageObject$GroupedMessagePosition r26 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r26
                r17 = r5
                if (r4 != 0) goto L_0x06e2
                r3 = 4
                goto L_0x06e3
            L_0x06e2:
                r3 = 0
            L_0x06e3:
                int[] r5 = r10.lineCounts
                int r5 = r5.length
                r20 = 1
                int r5 = r5 + -1
                if (r4 != r5) goto L_0x06ee
                r3 = r3 | 8
            L_0x06ee:
                if (r8 != 0) goto L_0x06f6
                r3 = r3 | 1
                if (r11 == 0) goto L_0x06f6
                r13 = r26
            L_0x06f6:
                if (r8 != r9) goto L_0x0701
                r3 = r3 | 2
                if (r11 != 0) goto L_0x0701
                r33 = r3
                r13 = r26
                goto L_0x0703
            L_0x0701:
                r33 = r3
            L_0x0703:
                r3 = 1145798656(0x444b8000, float:814.0)
                float r5 = r7 / r3
                float r32 = java.lang.Math.max(r14, r5)
                r27 = r8
                r28 = r8
                r29 = r4
                r30 = r4
                r31 = r15
                r26.set(r27, r28, r29, r30, r31, r32, r33)
                int r2 = r2 + 1
                int r8 = r8 + 1
                r3 = r16
                r5 = r17
                goto L_0x06c8
            L_0x0722:
                r16 = r3
                r17 = r5
                r3 = 1145798656(0x444b8000, float:814.0)
                int r5 = r13.pw
                int r5 = r5 + r12
                r13.pw = r5
                int r5 = r13.spanSize
                int r5 = r5 + r12
                r13.spanSize = r5
                int r4 = r4 + 1
                r3 = r16
                r5 = r17
                goto L_0x06b2
            L_0x073b:
                r2 = r5
            L_0x073c:
                r5 = 0
            L_0x073d:
                if (r5 >= r1) goto L_0x07b9
                java.util.ArrayList<org.telegram.messenger.MessageObject$GroupedMessagePosition> r3 = r0.posArray
                java.lang.Object r3 = r3.get(r5)
                org.telegram.messenger.MessageObject$GroupedMessagePosition r3 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r3
                if (r11 == 0) goto L_0x075f
                byte r4 = r3.minX
                if (r4 != 0) goto L_0x0754
                int r4 = r3.spanSize
                int r6 = r0.firstSpanAdditionalSize
                int r4 = r4 + r6
                r3.spanSize = r4
            L_0x0754:
                int r4 = r3.flags
                r6 = 2
                r4 = r4 & r6
                if (r4 == 0) goto L_0x075d
                r4 = 1
                r3.edge = r4
            L_0x075d:
                r6 = 1
                goto L_0x0778
            L_0x075f:
                r6 = 2
                byte r4 = r3.maxX
                if (r4 == r2) goto L_0x0769
                int r4 = r3.flags
                r4 = r4 & r6
                if (r4 == 0) goto L_0x0770
            L_0x0769:
                int r4 = r3.spanSize
                int r6 = r0.firstSpanAdditionalSize
                int r4 = r4 + r6
                r3.spanSize = r4
            L_0x0770:
                int r4 = r3.flags
                r6 = 1
                r4 = r4 & r6
                if (r4 == 0) goto L_0x0778
                r3.edge = r6
            L_0x0778:
                java.util.ArrayList<org.telegram.messenger.MessageObject> r4 = r0.messages
                java.lang.Object r4 = r4.get(r5)
                org.telegram.messenger.MessageObject r4 = (org.telegram.messenger.MessageObject) r4
                if (r11 != 0) goto L_0x07b5
                boolean r4 = r4.needDrawAvatarInternal()
                if (r4 == 0) goto L_0x07b5
                boolean r4 = r3.edge
                r7 = 1000(0x3e8, float:1.401E-42)
                if (r4 == 0) goto L_0x079d
                int r4 = r3.spanSize
                if (r4 == r7) goto L_0x0796
                int r4 = r4 + 108
                r3.spanSize = r4
            L_0x0796:
                int r4 = r3.pw
                int r4 = r4 + 108
                r3.pw = r4
                goto L_0x07b5
            L_0x079d:
                int r4 = r3.flags
                r8 = 2
                r4 = r4 & r8
                if (r4 == 0) goto L_0x07b6
                int r4 = r3.spanSize
                if (r4 == r7) goto L_0x07ac
                int r4 = r4 + -108
                r3.spanSize = r4
                goto L_0x07b6
            L_0x07ac:
                int r4 = r3.leftSpanOffset
                if (r4 == 0) goto L_0x07b6
                int r4 = r4 + 108
                r3.leftSpanOffset = r4
                goto L_0x07b6
            L_0x07b5:
                r8 = 2
            L_0x07b6:
                int r5 = r5 + 1
                goto L_0x073d
            L_0x07b9:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.GroupedMessages.calculate():void");
        }

        public MessageObject findPrimaryMessageObject() {
            if (!this.messages.isEmpty() && this.positions.isEmpty()) {
                calculate();
            }
            for (int i = 0; i < this.messages.size(); i++) {
                MessageObject messageObject = this.messages.get(i);
                GroupedMessagePosition groupedMessagePosition = this.positions.get(messageObject);
                if (groupedMessagePosition != null && (groupedMessagePosition.flags & 5) != 0) {
                    return messageObject;
                }
            }
            return null;
        }
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, String str, String str2, String str3, boolean z, boolean z2, boolean z3) {
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.localType = z ? 2 : 1;
        this.currentAccount = i;
        this.localName = str2;
        this.localUserName = str3;
        this.messageText = str;
        this.messageOwner = tLRPC$Message;
        this.localChannel = z2;
        this.localEdit = z3;
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, AbstractMap<Integer, TLRPC$User> abstractMap, boolean z) {
        this(i, tLRPC$Message, abstractMap, (AbstractMap<Integer, TLRPC$Chat>) null, z);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, SparseArray<TLRPC$User> sparseArray, boolean z) {
        this(i, tLRPC$Message, sparseArray, (SparseArray<TLRPC$Chat>) null, z);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, boolean z) {
        this(i, tLRPC$Message, (MessageObject) null, (AbstractMap<Integer, TLRPC$User>) null, (AbstractMap<Integer, TLRPC$Chat>) null, (SparseArray<TLRPC$User>) null, (SparseArray<TLRPC$Chat>) null, z, 0);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, MessageObject messageObject, boolean z) {
        this(i, tLRPC$Message, messageObject, (AbstractMap<Integer, TLRPC$User>) null, (AbstractMap<Integer, TLRPC$Chat>) null, (SparseArray<TLRPC$User>) null, (SparseArray<TLRPC$Chat>) null, z, 0);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, AbstractMap<Integer, TLRPC$User> abstractMap, AbstractMap<Integer, TLRPC$Chat> abstractMap2, boolean z) {
        this(i, tLRPC$Message, abstractMap, abstractMap2, z, 0);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, SparseArray<TLRPC$User> sparseArray, SparseArray<TLRPC$Chat> sparseArray2, boolean z) {
        this(i, tLRPC$Message, (MessageObject) null, (AbstractMap<Integer, TLRPC$User>) null, (AbstractMap<Integer, TLRPC$Chat>) null, sparseArray, sparseArray2, z, 0);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, AbstractMap<Integer, TLRPC$User> abstractMap, AbstractMap<Integer, TLRPC$Chat> abstractMap2, boolean z, long j) {
        this(i, tLRPC$Message, (MessageObject) null, abstractMap, abstractMap2, (SparseArray<TLRPC$User>) null, (SparseArray<TLRPC$Chat>) null, z, j);
    }

    public MessageObject(int i, TLRPC$Message tLRPC$Message, MessageObject messageObject, AbstractMap<Integer, TLRPC$User> abstractMap, AbstractMap<Integer, TLRPC$Chat> abstractMap2, SparseArray<TLRPC$User> sparseArray, SparseArray<TLRPC$Chat> sparseArray2, boolean z, long j) {
        TLRPC$User tLRPC$User;
        SparseArray<TLRPC$Chat> sparseArray3;
        AbstractMap<Integer, TLRPC$Chat> abstractMap3;
        TextPaint textPaint;
        TLRPC$Message tLRPC$Message2 = tLRPC$Message;
        AbstractMap<Integer, TLRPC$User> abstractMap4 = abstractMap;
        SparseArray<TLRPC$User> sparseArray4 = sparseArray;
        boolean z2 = z;
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        Theme.createChatResources((Context) null, true);
        this.currentAccount = i;
        this.messageOwner = tLRPC$Message2;
        this.replyMessageObject = messageObject;
        this.eventId = j;
        this.wasUnread = !tLRPC$Message2.out && tLRPC$Message2.unread;
        TLRPC$Message tLRPC$Message3 = tLRPC$Message2.replyMessage;
        if (tLRPC$Message3 != null) {
            MessageObject messageObject2 = r2;
            MessageObject messageObject3 = new MessageObject(this.currentAccount, tLRPC$Message3, (MessageObject) null, abstractMap, abstractMap2, sparseArray, sparseArray2, false, j);
            this.replyMessageObject = messageObject2;
        }
        int i2 = tLRPC$Message2.from_id;
        if (i2 > 0) {
            if (abstractMap4 != null) {
                tLRPC$User = abstractMap4.get(Integer.valueOf(i2));
            } else {
                tLRPC$User = sparseArray4 != null ? sparseArray4.get(i2) : null;
            }
            tLRPC$User = tLRPC$User == null ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(tLRPC$Message2.from_id)) : tLRPC$User;
            abstractMap3 = abstractMap2;
            sparseArray3 = sparseArray2;
        } else {
            abstractMap3 = abstractMap2;
            sparseArray3 = sparseArray2;
            tLRPC$User = null;
        }
        updateMessageText(abstractMap4, abstractMap3, sparseArray4, sparseArray3);
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
        if (z2) {
            if (this.messageOwner.media instanceof TLRPC$TL_messageMediaGame) {
                textPaint = Theme.chat_msgGameTextPaint;
            } else {
                textPaint = Theme.chat_msgTextPaint;
            }
            int[] iArr = allowsBigEmoji() ? new int[1] : null;
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            checkEmojiOnly(iArr);
            this.emojiAnimatedSticker = null;
            if (this.emojiOnlyCount == 1 && !(tLRPC$Message2.media instanceof TLRPC$TL_messageMediaWebPage) && tLRPC$Message2.entities.isEmpty()) {
                CharSequence charSequence = this.messageText;
                int indexOf = TextUtils.indexOf(charSequence, "🏻");
                if (indexOf >= 0) {
                    this.emojiAnimatedStickerColor = "_c1";
                    charSequence = charSequence.subSequence(0, indexOf);
                } else {
                    int indexOf2 = TextUtils.indexOf(charSequence, "🏼");
                    if (indexOf2 >= 0) {
                        this.emojiAnimatedStickerColor = "_c2";
                        charSequence = charSequence.subSequence(0, indexOf2);
                    } else {
                        int indexOf3 = TextUtils.indexOf(charSequence, "🏽");
                        if (indexOf3 >= 0) {
                            this.emojiAnimatedStickerColor = "_c3";
                            charSequence = charSequence.subSequence(0, indexOf3);
                        } else {
                            int indexOf4 = TextUtils.indexOf(charSequence, "🏾");
                            if (indexOf4 >= 0) {
                                this.emojiAnimatedStickerColor = "_c4";
                                charSequence = charSequence.subSequence(0, indexOf4);
                            } else {
                                int indexOf5 = TextUtils.indexOf(charSequence, "🏿");
                                if (indexOf5 >= 0) {
                                    this.emojiAnimatedStickerColor = "_c5";
                                    charSequence = charSequence.subSequence(0, indexOf5);
                                } else {
                                    this.emojiAnimatedStickerColor = "";
                                }
                            }
                        }
                    }
                }
                if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) || EmojiData.emojiColoredMap.contains(charSequence.toString())) {
                    this.emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(charSequence);
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
        }
        this.layoutCreated = z2;
        generateThumbs(false);
        checkMediaExistance();
    }

    private void createDateArray(int i, TLRPC$TL_channelAdminLogEvent tLRPC$TL_channelAdminLogEvent, ArrayList<MessageObject> arrayList, HashMap<String, ArrayList<MessageObject>> hashMap) {
        if (hashMap.get(this.dateKey) == null) {
            hashMap.put(this.dateKey, new ArrayList());
            TLRPC$TL_message tLRPC$TL_message = new TLRPC$TL_message();
            tLRPC$TL_message.message = LocaleController.formatDateChat((long) tLRPC$TL_channelAdminLogEvent.date);
            tLRPC$TL_message.id = 0;
            tLRPC$TL_message.date = tLRPC$TL_channelAdminLogEvent.date;
            MessageObject messageObject = new MessageObject(i, tLRPC$TL_message, false);
            messageObject.type = 10;
            messageObject.contentType = 1;
            messageObject.isDateObject = true;
            arrayList.add(messageObject);
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

    /* JADX WARNING: Removed duplicated region for block: B:240:0x0668  */
    /* JADX WARNING: Removed duplicated region for block: B:245:0x067c  */
    /* JADX WARNING: Removed duplicated region for block: B:248:0x0682 A[LOOP:0: B:226:0x063b->B:248:0x0682, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:518:0x0e4e  */
    /* JADX WARNING: Removed duplicated region for block: B:521:0x0e8f  */
    /* JADX WARNING: Removed duplicated region for block: B:528:0x0ea5  */
    /* JADX WARNING: Removed duplicated region for block: B:538:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:541:0x0f0c  */
    /* JADX WARNING: Removed duplicated region for block: B:557:0x069c A[EDGE_INSN: B:557:0x069c->B:250:0x069c ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:559:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public MessageObject(int r27, org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent r28, java.util.ArrayList<org.telegram.messenger.MessageObject> r29, java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.MessageObject>> r30, org.telegram.tgnet.TLRPC$Chat r31, int[] r32) {
        /*
            r26 = this;
            r0 = r26
            r1 = r28
            r2 = r29
            r3 = r30
            r4 = r31
            r26.<init>()
            r5 = 1000(0x3e8, float:1.401E-42)
            r0.type = r5
            r5 = -1082130432(0xffffffffbvar_, float:-1.0)
            r0.forceSeekTo = r5
            r0.currentEvent = r1
            r5 = r27
            r0.currentAccount = r5
            int r6 = r1.user_id
            if (r6 <= 0) goto L_0x002e
            org.telegram.messenger.MessagesController r5 = org.telegram.messenger.MessagesController.getInstance(r27)
            int r6 = r1.user_id
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$User r5 = r5.getUser(r6)
            goto L_0x002f
        L_0x002e:
            r5 = 0
        L_0x002f:
            java.util.GregorianCalendar r6 = new java.util.GregorianCalendar
            r6.<init>()
            int r8 = r1.date
            long r8 = (long) r8
            r10 = 1000(0x3e8, double:4.94E-321)
            long r8 = r8 * r10
            r6.setTimeInMillis(r8)
            r8 = 6
            int r8 = r6.get(r8)
            r9 = 1
            int r10 = r6.get(r9)
            r11 = 2
            int r6 = r6.get(r11)
            r12 = 3
            java.lang.Object[] r13 = new java.lang.Object[r12]
            java.lang.Integer r14 = java.lang.Integer.valueOf(r10)
            r15 = 0
            r13[r15] = r14
            java.lang.Integer r14 = java.lang.Integer.valueOf(r6)
            r13[r9] = r14
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            r13[r11] = r8
            java.lang.String r8 = "%d_%02d_%02d"
            java.lang.String r8 = java.lang.String.format(r8, r13)
            r0.dateKey = r8
            java.lang.Object[] r8 = new java.lang.Object[r11]
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            r8[r15] = r10
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r8[r9] = r6
            java.lang.String r6 = "%d_%02d"
            java.lang.String r6 = java.lang.String.format(r6, r8)
            r0.monthKey = r6
            org.telegram.tgnet.TLRPC$TL_peerChannel r6 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r6.<init>()
            int r8 = r4.id
            r6.channel_id = r8
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle
            java.lang.String r13 = ""
            java.lang.String r14 = "un1"
            if (r10 == 0) goto L_0x00c6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeTitle) r8
            java.lang.String r6 = r8.new_value
            boolean r8 = r4.megagroup
            if (r8 == 0) goto L_0x00b1
            r8 = 2131625095(0x7f0e0487, float:1.8877388E38)
            java.lang.Object[] r10 = new java.lang.Object[r9]
            r10[r15] = r6
            java.lang.String r6 = "EventLogEditedGroupTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r8, r10)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x00b1:
            r8 = 2131625092(0x7f0e0484, float:1.8877382E38)
            java.lang.Object[] r10 = new java.lang.Object[r9]
            r10[r15] = r6
            java.lang.String r6 = "EventLogEditedChannelTitle"
            java.lang.String r6 = org.telegram.messenger.LocaleController.formatString(r6, r8, r10)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x00c6:
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangePhoto
            if (r10 == 0) goto L_0x013d
            org.telegram.tgnet.TLRPC$TL_messageService r6 = new org.telegram.tgnet.TLRPC$TL_messageService
            r6.<init>()
            r0.messageOwner = r6
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$Photo r8 = r8.new_photo
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_photoEmpty
            if (r8 == 0) goto L_0x0106
            org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto r8 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto
            r8.<init>()
            r6.action = r8
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x00f5
            r6 = 2131625144(0x7f0e04b8, float:1.8877488E38)
            java.lang.String r8 = "EventLogRemovedWGroupPhoto"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x00f5:
            r6 = 2131625138(0x7f0e04b2, float:1.8877475E38)
            java.lang.String r8 = "EventLogRemovedChannelPhoto"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x0106:
            org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto r8 = new org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto
            r8.<init>()
            r6.action = r8
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$Photo r8 = r8.new_photo
            r6.photo = r8
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x012c
            r6 = 2131625094(0x7f0e0486, float:1.8877386E38)
            java.lang.String r8 = "EventLogEditedGroupPhoto"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x012c:
            r6 = 2131625091(0x7f0e0483, float:1.887738E38)
            java.lang.String r8 = "EventLogEditedChannelPhoto"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x013d:
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantJoin
            r7 = 2131625113(0x7f0e0499, float:1.8877425E38)
            java.lang.String r11 = "EventLogGroupJoined"
            r12 = 2131625084(0x7f0e047c, float:1.8877366E38)
            java.lang.String r15 = "EventLogChannelJoined"
            if (r10 == 0) goto L_0x0167
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x015b
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r11, r7)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x015b:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r15, r12)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x0167:
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantLeave
            if (r10 == 0) goto L_0x01a7
            org.telegram.tgnet.TLRPC$TL_messageService r6 = new org.telegram.tgnet.TLRPC$TL_messageService
            r6.<init>()
            r0.messageOwner = r6
            org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser r7 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser
            r7.<init>()
            r6.action = r7
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageAction r6 = r6.action
            int r7 = r1.user_id
            r6.user_id = r7
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x0196
            r6 = 2131625118(0x7f0e049e, float:1.8877435E38)
            java.lang.String r7 = "EventLogLeftGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x0196:
            r6 = 2131625117(0x7f0e049d, float:1.8877433E38)
            java.lang.String r7 = "EventLogLeftChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x01a7:
            boolean r10 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantInvite
            java.lang.String r9 = "un2"
            if (r10 == 0) goto L_0x020e
            org.telegram.tgnet.TLRPC$TL_messageService r6 = new org.telegram.tgnet.TLRPC$TL_messageService
            r6.<init>()
            r0.messageOwner = r6
            org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser r8 = new org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser
            r8.<init>()
            r6.action = r8
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r8.participant
            int r8 = r8.user_id
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r8)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r8.participant
            int r8 = r8.user_id
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            int r10 = r10.from_id
            if (r8 != r10) goto L_0x01f7
            boolean r6 = r4.megagroup
            if (r6 == 0) goto L_0x01eb
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r11, r7)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x01eb:
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r15, r12)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x01f7:
            r7 = 2131625074(0x7f0e0472, float:1.8877346E38)
            java.lang.String r8 = "EventLogAdded"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.CharSequence r6 = r0.replaceWithLink(r7, r9, r6)
            r0.messageText = r6
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            goto L_0x0e49
        L_0x020e:
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleAdmin
            java.lang.String r10 = "%1$s"
            r11 = 45
            r15 = 32
            r12 = 10
            if (r7 == 0) goto L_0x043b
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
            r6.<init>()
            r0.messageOwner = r6
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r7 = r7.prev_participant
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r7.prev_participant
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r8 != 0) goto L_0x0269
            org.telegram.tgnet.TLRPC$ChannelParticipant r7 = r7.new_participant
            boolean r7 = r7 instanceof org.telegram.tgnet.TLRPC$TL_channelParticipantCreator
            if (r7 == 0) goto L_0x0269
            r7 = 2131625082(0x7f0e047a, float:1.8877362E38)
            java.lang.String r8 = "EventLogChangedOwnership"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            int r8 = r7.indexOf(r10)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r10 = 1
            java.lang.Object[] r11 = new java.lang.Object[r10]
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            java.lang.String r6 = r0.getUserName(r6, r10, r8)
            r8 = 0
            r11[r8] = r6
            java.lang.String r6 = java.lang.String.format(r7, r11)
            r9.<init>(r6)
            goto L_0x0433
        L_0x0269:
            r7 = 2131625125(0x7f0e04a5, float:1.887745E38)
            java.lang.String r8 = "EventLogPromoted"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            int r8 = r7.indexOf(r10)
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r10 = 1
            java.lang.Object[] r14 = new java.lang.Object[r10]
            org.telegram.tgnet.TLRPC$Message r10 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r10 = r10.entities
            java.lang.String r6 = r0.getUserName(r6, r10, r8)
            r8 = 0
            r14[r8] = r6
            java.lang.String r6 = java.lang.String.format(r7, r14)
            r9.<init>(r6)
            java.lang.String r6 = "\n"
            r9.append(r6)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r6 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r7 = r6.prev_participant
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r7 = r7.admin_rights
            org.telegram.tgnet.TLRPC$ChannelParticipant r6 = r6.new_participant
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r6 = r6.admin_rights
            if (r7 != 0) goto L_0x02a3
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r7 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r7.<init>()
        L_0x02a3:
            if (r6 != 0) goto L_0x02aa
            org.telegram.tgnet.TLRPC$TL_chatAdminRights r6 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights
            r6.<init>()
        L_0x02aa:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r10 = r8.prev_participant
            java.lang.String r10 = r10.rank
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r8.new_participant
            java.lang.String r8 = r8.rank
            boolean r8 = android.text.TextUtils.equals(r10, r8)
            if (r8 != 0) goto L_0x0300
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r8.new_participant
            java.lang.String r8 = r8.rank
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x02dc
            r9.append(r12)
            r9.append(r11)
            r9.append(r15)
            r8 = 2131625135(0x7f0e04af, float:1.887747E38)
            java.lang.String r10 = "EventLogPromotedRemovedTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r9.append(r8)
            goto L_0x0300
        L_0x02dc:
            r9.append(r12)
            r8 = 43
            r9.append(r8)
            r9.append(r15)
            r10 = 2131625136(0x7f0e04b0, float:1.8877471E38)
            r14 = 1
            java.lang.Object[] r8 = new java.lang.Object[r14]
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r14 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r14 = r14.new_participant
            java.lang.String r14 = r14.rank
            r16 = 0
            r8[r16] = r14
            java.lang.String r14 = "EventLogPromotedTitle"
            java.lang.String r8 = org.telegram.messenger.LocaleController.formatString(r14, r10, r8)
            r9.append(r8)
        L_0x0300:
            boolean r8 = r7.change_info
            boolean r10 = r6.change_info
            if (r8 == r10) goto L_0x032e
            r9.append(r12)
            boolean r8 = r6.change_info
            if (r8 == 0) goto L_0x0310
            r8 = 43
            goto L_0x0312
        L_0x0310:
            r8 = 45
        L_0x0312:
            r9.append(r8)
            r9.append(r15)
            boolean r8 = r4.megagroup
            if (r8 == 0) goto L_0x0322
            r8 = 2131625130(0x7f0e04aa, float:1.887746E38)
            java.lang.String r10 = "EventLogPromotedChangeGroupInfo"
            goto L_0x0327
        L_0x0322:
            r8 = 2131625129(0x7f0e04a9, float:1.8877457E38)
            java.lang.String r10 = "EventLogPromotedChangeChannelInfo"
        L_0x0327:
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r9.append(r8)
        L_0x032e:
            boolean r8 = r4.megagroup
            if (r8 != 0) goto L_0x037a
            boolean r8 = r7.post_messages
            boolean r10 = r6.post_messages
            if (r8 == r10) goto L_0x0356
            r9.append(r12)
            boolean r8 = r6.post_messages
            if (r8 == 0) goto L_0x0342
            r8 = 43
            goto L_0x0344
        L_0x0342:
            r8 = 45
        L_0x0344:
            r9.append(r8)
            r9.append(r15)
            r8 = 2131625134(0x7f0e04ae, float:1.8877467E38)
            java.lang.String r10 = "EventLogPromotedPostMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r9.append(r8)
        L_0x0356:
            boolean r8 = r7.edit_messages
            boolean r10 = r6.edit_messages
            if (r8 == r10) goto L_0x037a
            r9.append(r12)
            boolean r8 = r6.edit_messages
            if (r8 == 0) goto L_0x0366
            r8 = 43
            goto L_0x0368
        L_0x0366:
            r8 = 45
        L_0x0368:
            r9.append(r8)
            r9.append(r15)
            r8 = 2131625132(0x7f0e04ac, float:1.8877463E38)
            java.lang.String r10 = "EventLogPromotedEditMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r9.append(r8)
        L_0x037a:
            boolean r8 = r7.delete_messages
            boolean r10 = r6.delete_messages
            if (r8 == r10) goto L_0x039e
            r9.append(r12)
            boolean r8 = r6.delete_messages
            if (r8 == 0) goto L_0x038a
            r8 = 43
            goto L_0x038c
        L_0x038a:
            r8 = 45
        L_0x038c:
            r9.append(r8)
            r9.append(r15)
            r8 = 2131625131(0x7f0e04ab, float:1.8877461E38)
            java.lang.String r10 = "EventLogPromotedDeleteMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r9.append(r8)
        L_0x039e:
            boolean r8 = r7.add_admins
            boolean r10 = r6.add_admins
            if (r8 == r10) goto L_0x03c2
            r9.append(r12)
            boolean r8 = r6.add_admins
            if (r8 == 0) goto L_0x03ae
            r8 = 43
            goto L_0x03b0
        L_0x03ae:
            r8 = 45
        L_0x03b0:
            r9.append(r8)
            r9.append(r15)
            r8 = 2131625126(0x7f0e04a6, float:1.8877451E38)
            java.lang.String r10 = "EventLogPromotedAddAdmins"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r9.append(r8)
        L_0x03c2:
            boolean r8 = r4.megagroup
            if (r8 == 0) goto L_0x03ea
            boolean r8 = r7.ban_users
            boolean r10 = r6.ban_users
            if (r8 == r10) goto L_0x03ea
            r9.append(r12)
            boolean r8 = r6.ban_users
            if (r8 == 0) goto L_0x03d6
            r8 = 43
            goto L_0x03d8
        L_0x03d6:
            r8 = 45
        L_0x03d8:
            r9.append(r8)
            r9.append(r15)
            r8 = 2131625128(0x7f0e04a8, float:1.8877455E38)
            java.lang.String r10 = "EventLogPromotedBanUsers"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r9.append(r8)
        L_0x03ea:
            boolean r8 = r7.invite_users
            boolean r10 = r6.invite_users
            if (r8 == r10) goto L_0x040e
            r9.append(r12)
            boolean r8 = r6.invite_users
            if (r8 == 0) goto L_0x03fa
            r8 = 43
            goto L_0x03fc
        L_0x03fa:
            r8 = 45
        L_0x03fc:
            r9.append(r8)
            r9.append(r15)
            r8 = 2131625127(0x7f0e04a7, float:1.8877453E38)
            java.lang.String r10 = "EventLogPromotedAddUsers"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r10, r8)
            r9.append(r8)
        L_0x040e:
            boolean r8 = r4.megagroup
            if (r8 == 0) goto L_0x0433
            boolean r7 = r7.pin_messages
            boolean r8 = r6.pin_messages
            if (r7 == r8) goto L_0x0433
            r9.append(r12)
            boolean r6 = r6.pin_messages
            if (r6 == 0) goto L_0x0421
            r11 = 43
        L_0x0421:
            r9.append(r11)
            r9.append(r15)
            r6 = 2131625133(0x7f0e04ad, float:1.8877465E38)
            java.lang.String r7 = "EventLogPromotedPinMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r9.append(r6)
        L_0x0433:
            java.lang.String r6 = r9.toString()
            r0.messageText = r6
            goto L_0x0e49
        L_0x043b:
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights
            if (r7 == 0) goto L_0x05ce
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDefaultBannedRights) r8
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
            r6.<init>()
            r0.messageOwner = r6
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r6 = r8.prev_banned_rights
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = r8.new_banned_rights
            java.lang.StringBuilder r8 = new java.lang.StringBuilder
            r9 = 2131625087(0x7f0e047f, float:1.8877372E38)
            java.lang.String r10 = "EventLogDefaultPermissions"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.<init>(r9)
            if (r6 != 0) goto L_0x0461
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r6 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r6.<init>()
        L_0x0461:
            if (r7 != 0) goto L_0x0468
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r7.<init>()
        L_0x0468:
            boolean r9 = r6.send_messages
            boolean r10 = r7.send_messages
            if (r9 == r10) goto L_0x0491
            r8.append(r12)
            r8.append(r12)
            boolean r9 = r7.send_messages
            if (r9 != 0) goto L_0x047b
            r9 = 43
            goto L_0x047d
        L_0x047b:
            r9 = 45
        L_0x047d:
            r8.append(r9)
            r8.append(r15)
            r9 = 2131625151(0x7f0e04bf, float:1.8877502E38)
            java.lang.String r10 = "EventLogRestrictedSendMessages"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.append(r9)
            r9 = 1
            goto L_0x0492
        L_0x0491:
            r9 = 0
        L_0x0492:
            boolean r10 = r6.send_stickers
            boolean r14 = r7.send_stickers
            if (r10 != r14) goto L_0x04aa
            boolean r10 = r6.send_inline
            boolean r14 = r7.send_inline
            if (r10 != r14) goto L_0x04aa
            boolean r10 = r6.send_gifs
            boolean r14 = r7.send_gifs
            if (r10 != r14) goto L_0x04aa
            boolean r10 = r6.send_games
            boolean r14 = r7.send_games
            if (r10 == r14) goto L_0x04ce
        L_0x04aa:
            if (r9 != 0) goto L_0x04b0
            r8.append(r12)
            r9 = 1
        L_0x04b0:
            r8.append(r12)
            boolean r10 = r7.send_stickers
            if (r10 != 0) goto L_0x04ba
            r10 = 43
            goto L_0x04bc
        L_0x04ba:
            r10 = 45
        L_0x04bc:
            r8.append(r10)
            r8.append(r15)
            r10 = 2131625153(0x7f0e04c1, float:1.8877506E38)
            java.lang.String r14 = "EventLogRestrictedSendStickers"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r8.append(r10)
        L_0x04ce:
            boolean r10 = r6.send_media
            boolean r14 = r7.send_media
            if (r10 == r14) goto L_0x04f8
            if (r9 != 0) goto L_0x04da
            r8.append(r12)
            r9 = 1
        L_0x04da:
            r8.append(r12)
            boolean r10 = r7.send_media
            if (r10 != 0) goto L_0x04e4
            r10 = 43
            goto L_0x04e6
        L_0x04e4:
            r10 = 45
        L_0x04e6:
            r8.append(r10)
            r8.append(r15)
            r10 = 2131625150(0x7f0e04be, float:1.88775E38)
            java.lang.String r14 = "EventLogRestrictedSendMedia"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r8.append(r10)
        L_0x04f8:
            boolean r10 = r6.send_polls
            boolean r14 = r7.send_polls
            if (r10 == r14) goto L_0x0522
            if (r9 != 0) goto L_0x0504
            r8.append(r12)
            r9 = 1
        L_0x0504:
            r8.append(r12)
            boolean r10 = r7.send_polls
            if (r10 != 0) goto L_0x050e
            r10 = 43
            goto L_0x0510
        L_0x050e:
            r10 = 45
        L_0x0510:
            r8.append(r10)
            r8.append(r15)
            r10 = 2131625152(0x7f0e04c0, float:1.8877504E38)
            java.lang.String r14 = "EventLogRestrictedSendPolls"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r8.append(r10)
        L_0x0522:
            boolean r10 = r6.embed_links
            boolean r14 = r7.embed_links
            if (r10 == r14) goto L_0x054c
            if (r9 != 0) goto L_0x052e
            r8.append(r12)
            r9 = 1
        L_0x052e:
            r8.append(r12)
            boolean r10 = r7.embed_links
            if (r10 != 0) goto L_0x0538
            r10 = 43
            goto L_0x053a
        L_0x0538:
            r10 = 45
        L_0x053a:
            r8.append(r10)
            r8.append(r15)
            r10 = 2131625149(0x7f0e04bd, float:1.8877498E38)
            java.lang.String r14 = "EventLogRestrictedSendEmbed"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r8.append(r10)
        L_0x054c:
            boolean r10 = r6.change_info
            boolean r14 = r7.change_info
            if (r10 == r14) goto L_0x0576
            if (r9 != 0) goto L_0x0558
            r8.append(r12)
            r9 = 1
        L_0x0558:
            r8.append(r12)
            boolean r10 = r7.change_info
            if (r10 != 0) goto L_0x0562
            r10 = 43
            goto L_0x0564
        L_0x0562:
            r10 = 45
        L_0x0564:
            r8.append(r10)
            r8.append(r15)
            r10 = 2131625145(0x7f0e04b9, float:1.887749E38)
            java.lang.String r14 = "EventLogRestrictedChangeInfo"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r8.append(r10)
        L_0x0576:
            boolean r10 = r6.invite_users
            boolean r14 = r7.invite_users
            if (r10 == r14) goto L_0x05a0
            if (r9 != 0) goto L_0x0582
            r8.append(r12)
            r9 = 1
        L_0x0582:
            r8.append(r12)
            boolean r10 = r7.invite_users
            if (r10 != 0) goto L_0x058c
            r10 = 43
            goto L_0x058e
        L_0x058c:
            r10 = 45
        L_0x058e:
            r8.append(r10)
            r8.append(r15)
            r10 = 2131625146(0x7f0e04ba, float:1.8877492E38)
            java.lang.String r14 = "EventLogRestrictedInviteUsers"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r14, r10)
            r8.append(r10)
        L_0x05a0:
            boolean r6 = r6.pin_messages
            boolean r10 = r7.pin_messages
            if (r6 == r10) goto L_0x05c6
            if (r9 != 0) goto L_0x05ab
            r8.append(r12)
        L_0x05ab:
            r8.append(r12)
            boolean r6 = r7.pin_messages
            if (r6 != 0) goto L_0x05b4
            r11 = 43
        L_0x05b4:
            r8.append(r11)
            r8.append(r15)
            r6 = 2131625147(0x7f0e04bb, float:1.8877494E38)
            java.lang.String r7 = "EventLogRestrictedPinMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            r8.append(r6)
        L_0x05c6:
            java.lang.String r6 = r8.toString()
            r0.messageText = r6
            goto L_0x0e49
        L_0x05ce:
            boolean r7 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionParticipantToggleBan
            r11 = 60
            if (r7 == 0) goto L_0x08c4
            org.telegram.tgnet.TLRPC$TL_message r6 = new org.telegram.tgnet.TLRPC$TL_message
            r6.<init>()
            r0.messageOwner = r6
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r7 = r7.prev_participant
            int r7 = r7.user_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$ChannelParticipant r8 = r7.prev_participant
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r8 = r8.banned_rights
            org.telegram.tgnet.TLRPC$ChannelParticipant r7 = r7.new_participant
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = r7.banned_rights
            boolean r9 = r4.megagroup
            if (r9 == 0) goto L_0x088f
            if (r7 == 0) goto L_0x060d
            boolean r9 = r7.view_messages
            if (r9 == 0) goto L_0x060d
            if (r7 == 0) goto L_0x088f
            if (r8 == 0) goto L_0x088f
            int r9 = r7.until_date
            int r14 = r8.until_date
            if (r9 == r14) goto L_0x088f
        L_0x060d:
            if (r7 == 0) goto L_0x068e
            boolean r9 = org.telegram.messenger.AndroidUtilities.isBannedForever(r7)
            if (r9 != 0) goto L_0x068e
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            int r14 = r7.until_date
            int r15 = r1.date
            int r14 = r14 - r15
            int r15 = r14 / 60
            int r15 = r15 / r11
            int r15 = r15 / 24
            int r20 = r15 * 60
            int r20 = r20 * 60
            int r20 = r20 * 24
            int r14 = r14 - r20
            int r20 = r14 / 60
            int r12 = r20 / 60
            int r20 = r12 * 60
            int r20 = r20 * 60
            int r14 = r14 - r20
            int r14 = r14 / r11
            r2 = 3
            r11 = 0
            r16 = 0
        L_0x063b:
            if (r11 >= r2) goto L_0x069c
            if (r11 != 0) goto L_0x064a
            if (r15 == 0) goto L_0x065f
            java.lang.String r2 = "Days"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r15)
        L_0x0647:
            int r16 = r16 + 1
            goto L_0x0660
        L_0x064a:
            r2 = 1
            if (r11 != r2) goto L_0x0656
            if (r12 == 0) goto L_0x065f
            java.lang.String r2 = "Hours"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r12)
            goto L_0x0647
        L_0x0656:
            if (r14 == 0) goto L_0x065f
            java.lang.String r2 = "Minutes"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r14)
            goto L_0x0647
        L_0x065f:
            r2 = 0
        L_0x0660:
            r25 = r16
            r16 = r12
            r12 = r25
            if (r2 == 0) goto L_0x067c
            int r22 = r9.length()
            if (r22 <= 0) goto L_0x0676
            r22 = r14
            java.lang.String r14 = ", "
            r9.append(r14)
            goto L_0x0678
        L_0x0676:
            r22 = r14
        L_0x0678:
            r9.append(r2)
            goto L_0x067e
        L_0x067c:
            r22 = r14
        L_0x067e:
            r2 = 2
            if (r12 != r2) goto L_0x0682
            goto L_0x069c
        L_0x0682:
            int r11 = r11 + 1
            r14 = r22
            r2 = 3
            r25 = r16
            r16 = r12
            r12 = r25
            goto L_0x063b
        L_0x068e:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r2 = 2131627113(0x7f0e0CLASSNAME, float:1.8881481E38)
            java.lang.String r11 = "UserRestrictionsUntilForever"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r2)
            r9.<init>(r2)
        L_0x069c:
            r2 = 2131625154(0x7f0e04c2, float:1.8877508E38)
            java.lang.String r11 = "EventLogRestrictedUntil"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r11, r2)
            int r10 = r2.indexOf(r10)
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r12 = 2
            java.lang.Object[] r12 = new java.lang.Object[r12]
            org.telegram.tgnet.TLRPC$Message r14 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r14 = r14.entities
            java.lang.String r6 = r0.getUserName(r6, r14, r10)
            r10 = 0
            r12[r10] = r6
            java.lang.String r6 = r9.toString()
            r9 = 1
            r12[r9] = r6
            java.lang.String r2 = java.lang.String.format(r2, r12)
            r11.<init>(r2)
            if (r8 != 0) goto L_0x06ce
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r8 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r8.<init>()
        L_0x06ce:
            if (r7 != 0) goto L_0x06d5
            org.telegram.tgnet.TLRPC$TL_chatBannedRights r7 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights
            r7.<init>()
        L_0x06d5:
            boolean r2 = r8.view_messages
            boolean r6 = r7.view_messages
            if (r2 == r6) goto L_0x0702
            r2 = 10
            r11.append(r2)
            r11.append(r2)
            boolean r2 = r7.view_messages
            if (r2 != 0) goto L_0x06ea
            r2 = 43
            goto L_0x06ec
        L_0x06ea:
            r2 = 45
        L_0x06ec:
            r11.append(r2)
            r2 = 32
            r11.append(r2)
            r2 = 2131625148(0x7f0e04bc, float:1.8877496E38)
            java.lang.String r6 = "EventLogRestrictedReadMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r11.append(r2)
            r2 = 1
            goto L_0x0703
        L_0x0702:
            r2 = 0
        L_0x0703:
            boolean r6 = r8.send_messages
            boolean r9 = r7.send_messages
            if (r6 == r9) goto L_0x0731
            r6 = 10
            if (r2 != 0) goto L_0x0711
            r11.append(r6)
            r2 = 1
        L_0x0711:
            r11.append(r6)
            boolean r6 = r7.send_messages
            if (r6 != 0) goto L_0x071b
            r6 = 43
            goto L_0x071d
        L_0x071b:
            r6 = 45
        L_0x071d:
            r11.append(r6)
            r6 = 32
            r11.append(r6)
            r6 = 2131625151(0x7f0e04bf, float:1.8877502E38)
            java.lang.String r9 = "EventLogRestrictedSendMessages"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r11.append(r6)
        L_0x0731:
            boolean r6 = r8.send_stickers
            boolean r9 = r7.send_stickers
            if (r6 != r9) goto L_0x0749
            boolean r6 = r8.send_inline
            boolean r9 = r7.send_inline
            if (r6 != r9) goto L_0x0749
            boolean r6 = r8.send_gifs
            boolean r9 = r7.send_gifs
            if (r6 != r9) goto L_0x0749
            boolean r6 = r8.send_games
            boolean r9 = r7.send_games
            if (r6 == r9) goto L_0x0771
        L_0x0749:
            r6 = 10
            if (r2 != 0) goto L_0x0751
            r11.append(r6)
            r2 = 1
        L_0x0751:
            r11.append(r6)
            boolean r6 = r7.send_stickers
            if (r6 != 0) goto L_0x075b
            r6 = 43
            goto L_0x075d
        L_0x075b:
            r6 = 45
        L_0x075d:
            r11.append(r6)
            r6 = 32
            r11.append(r6)
            r6 = 2131625153(0x7f0e04c1, float:1.8877506E38)
            java.lang.String r9 = "EventLogRestrictedSendStickers"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r11.append(r6)
        L_0x0771:
            boolean r6 = r8.send_media
            boolean r9 = r7.send_media
            if (r6 == r9) goto L_0x079f
            r6 = 10
            if (r2 != 0) goto L_0x077f
            r11.append(r6)
            r2 = 1
        L_0x077f:
            r11.append(r6)
            boolean r6 = r7.send_media
            if (r6 != 0) goto L_0x0789
            r6 = 43
            goto L_0x078b
        L_0x0789:
            r6 = 45
        L_0x078b:
            r11.append(r6)
            r6 = 32
            r11.append(r6)
            r6 = 2131625150(0x7f0e04be, float:1.88775E38)
            java.lang.String r9 = "EventLogRestrictedSendMedia"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r11.append(r6)
        L_0x079f:
            boolean r6 = r8.send_polls
            boolean r9 = r7.send_polls
            if (r6 == r9) goto L_0x07cd
            r6 = 10
            if (r2 != 0) goto L_0x07ad
            r11.append(r6)
            r2 = 1
        L_0x07ad:
            r11.append(r6)
            boolean r6 = r7.send_polls
            if (r6 != 0) goto L_0x07b7
            r6 = 43
            goto L_0x07b9
        L_0x07b7:
            r6 = 45
        L_0x07b9:
            r11.append(r6)
            r6 = 32
            r11.append(r6)
            r6 = 2131625152(0x7f0e04c0, float:1.8877504E38)
            java.lang.String r9 = "EventLogRestrictedSendPolls"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r11.append(r6)
        L_0x07cd:
            boolean r6 = r8.embed_links
            boolean r9 = r7.embed_links
            if (r6 == r9) goto L_0x07fb
            r6 = 10
            if (r2 != 0) goto L_0x07db
            r11.append(r6)
            r2 = 1
        L_0x07db:
            r11.append(r6)
            boolean r6 = r7.embed_links
            if (r6 != 0) goto L_0x07e5
            r6 = 43
            goto L_0x07e7
        L_0x07e5:
            r6 = 45
        L_0x07e7:
            r11.append(r6)
            r6 = 32
            r11.append(r6)
            r6 = 2131625149(0x7f0e04bd, float:1.8877498E38)
            java.lang.String r9 = "EventLogRestrictedSendEmbed"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r11.append(r6)
        L_0x07fb:
            boolean r6 = r8.change_info
            boolean r9 = r7.change_info
            if (r6 == r9) goto L_0x0829
            r6 = 10
            if (r2 != 0) goto L_0x0809
            r11.append(r6)
            r2 = 1
        L_0x0809:
            r11.append(r6)
            boolean r6 = r7.change_info
            if (r6 != 0) goto L_0x0813
            r6 = 43
            goto L_0x0815
        L_0x0813:
            r6 = 45
        L_0x0815:
            r11.append(r6)
            r6 = 32
            r11.append(r6)
            r6 = 2131625145(0x7f0e04b9, float:1.887749E38)
            java.lang.String r9 = "EventLogRestrictedChangeInfo"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r11.append(r6)
        L_0x0829:
            boolean r6 = r8.invite_users
            boolean r9 = r7.invite_users
            if (r6 == r9) goto L_0x0857
            r6 = 10
            if (r2 != 0) goto L_0x0837
            r11.append(r6)
            r2 = 1
        L_0x0837:
            r11.append(r6)
            boolean r6 = r7.invite_users
            if (r6 != 0) goto L_0x0841
            r6 = 43
            goto L_0x0843
        L_0x0841:
            r6 = 45
        L_0x0843:
            r11.append(r6)
            r6 = 32
            r11.append(r6)
            r6 = 2131625146(0x7f0e04ba, float:1.8877492E38)
            java.lang.String r9 = "EventLogRestrictedInviteUsers"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r9, r6)
            r11.append(r6)
        L_0x0857:
            boolean r6 = r8.pin_messages
            boolean r8 = r7.pin_messages
            if (r6 == r8) goto L_0x0887
            if (r2 != 0) goto L_0x0865
            r2 = 10
            r11.append(r2)
            goto L_0x0867
        L_0x0865:
            r2 = 10
        L_0x0867:
            r11.append(r2)
            boolean r2 = r7.pin_messages
            if (r2 != 0) goto L_0x0871
            r2 = 43
            goto L_0x0873
        L_0x0871:
            r2 = 45
        L_0x0873:
            r11.append(r2)
            r2 = 32
            r11.append(r2)
            r2 = 2131625147(0x7f0e04bb, float:1.8877494E38)
            java.lang.String r6 = "EventLogRestrictedPinMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            r11.append(r2)
        L_0x0887:
            java.lang.String r2 = r11.toString()
            r0.messageText = r2
            goto L_0x0e49
        L_0x088f:
            if (r7 == 0) goto L_0x08a1
            if (r8 == 0) goto L_0x0897
            boolean r2 = r7.view_messages
            if (r2 == 0) goto L_0x08a1
        L_0x0897:
            r2 = 2131625085(0x7f0e047d, float:1.8877368E38)
            java.lang.String r7 = "EventLogChannelRestricted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)
            goto L_0x08aa
        L_0x08a1:
            r2 = 2131625086(0x7f0e047e, float:1.887737E38)
            java.lang.String r7 = "EventLogChannelUnrestricted"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)
        L_0x08aa:
            int r7 = r2.indexOf(r10)
            r8 = 1
            java.lang.Object[] r9 = new java.lang.Object[r8]
            org.telegram.tgnet.TLRPC$Message r8 = r0.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r8 = r8.entities
            java.lang.String r6 = r0.getUserName(r6, r8, r7)
            r7 = 0
            r9[r7] = r6
            java.lang.String r2 = java.lang.String.format(r2, r9)
            r0.messageText = r2
            goto L_0x0e49
        L_0x08c4:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionUpdatePinned
            if (r2 == 0) goto L_0x093a
            java.lang.String r2 = "EventLogUnpinnedMessages"
            r6 = 2131625122(0x7f0e04a2, float:1.8877443E38)
            java.lang.String r7 = "EventLogPinnedMessages"
            if (r5 == 0) goto L_0x0917
            int r9 = r5.id
            r10 = 136817688(0x827aCLASSNAME, float:5.045703E-34)
            if (r9 != r10) goto L_0x0917
            org.telegram.tgnet.TLRPC$Message r8 = r8.message
            org.telegram.tgnet.TLRPC$MessageFwdHeader r8 = r8.fwd_from
            if (r8 == 0) goto L_0x0917
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r9 = r1.action
            org.telegram.tgnet.TLRPC$Message r9 = r9.message
            org.telegram.tgnet.TLRPC$MessageFwdHeader r9 = r9.fwd_from
            int r9 = r9.channel_id
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)
            org.telegram.tgnet.TLRPC$Chat r8 = r8.getChat(r9)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r9 = r1.action
            org.telegram.tgnet.TLRPC$Message r9 = r9.message
            boolean r9 = r9 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r9 == 0) goto L_0x090b
            r6 = 2131625166(0x7f0e04ce, float:1.8877532E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r8)
            r0.messageText = r2
            goto L_0x0e49
        L_0x090b:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r8)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0917:
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$Message r8 = r8.message
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r8 == 0) goto L_0x092e
            r6 = 2131625166(0x7f0e04ce, float:1.8877532E38)
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r2, r6)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x092e:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x093a:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionStopPoll
            if (r2 == 0) goto L_0x0970
            org.telegram.tgnet.TLRPC$Message r2 = r8.message
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r2.media
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r6 == 0) goto L_0x095f
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r2 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r2
            org.telegram.tgnet.TLRPC$TL_poll r2 = r2.poll
            boolean r2 = r2.quiz
            if (r2 == 0) goto L_0x095f
            r2 = 2131625157(0x7f0e04c5, float:1.8877514E38)
            java.lang.String r6 = "EventLogStopQuiz"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x095f:
            r2 = 2131625156(0x7f0e04c4, float:1.8877512E38)
            java.lang.String r6 = "EventLogStopPoll"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0970:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures
            if (r2 == 0) goto L_0x099c
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSignatures) r8
            boolean r2 = r8.new_value
            if (r2 == 0) goto L_0x098b
            r2 = 2131625163(0x7f0e04cb, float:1.8877526E38)
            java.lang.String r6 = "EventLogToggledSignaturesOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x098b:
            r2 = 2131625162(0x7f0e04ca, float:1.8877524E38)
            java.lang.String r6 = "EventLogToggledSignaturesOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x099c:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites
            if (r2 == 0) goto L_0x09c8
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleInvites) r8
            boolean r2 = r8.new_value
            if (r2 == 0) goto L_0x09b7
            r2 = 2131625161(0x7f0e04c9, float:1.8877522E38)
            java.lang.String r6 = "EventLogToggledInvitesOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x09b7:
            r2 = 2131625160(0x7f0e04c8, float:1.887752E38)
            java.lang.String r6 = "EventLogToggledInvitesOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x09c8:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionDeleteMessage
            if (r2 == 0) goto L_0x09dd
            r2 = 2131625088(0x7f0e0480, float:1.8877374E38)
            java.lang.String r6 = "EventLogDeletedMessages"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x09dd:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat
            if (r2 == 0) goto L_0x0a86
            r2 = r8
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat) r2
            int r2 = r2.new_value
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLinkedChat) r8
            int r6 = r8.prev_value
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x0a3a
            if (r2 != 0) goto L_0x0a15
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r6)
            r6 = 2131625140(0x7f0e04b4, float:1.887748E38)
            java.lang.String r7 = "EventLogRemovedLinkedChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            java.lang.CharSequence r2 = r0.replaceWithLink(r6, r9, r2)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0a15:
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r6.getChat(r2)
            r6 = 2131625079(0x7f0e0477, float:1.8877356E38)
            java.lang.String r7 = "EventLogChangedLinkedChannel"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            java.lang.CharSequence r2 = r0.replaceWithLink(r6, r9, r2)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0a3a:
            if (r2 != 0) goto L_0x0a61
            int r2 = r0.currentAccount
            org.telegram.messenger.MessagesController r2 = org.telegram.messenger.MessagesController.getInstance(r2)
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            org.telegram.tgnet.TLRPC$Chat r2 = r2.getChat(r6)
            r6 = 2131625141(0x7f0e04b5, float:1.8877482E38)
            java.lang.String r7 = "EventLogRemovedLinkedGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            java.lang.CharSequence r2 = r0.replaceWithLink(r6, r9, r2)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0a61:
            int r6 = r0.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            org.telegram.tgnet.TLRPC$Chat r2 = r6.getChat(r2)
            r6 = 2131625080(0x7f0e0478, float:1.8877358E38)
            java.lang.String r7 = "EventLogChangedLinkedGroup"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r7, r6)
            java.lang.CharSequence r6 = r0.replaceWithLink(r6, r14, r5)
            r0.messageText = r6
            java.lang.CharSequence r2 = r0.replaceWithLink(r6, r9, r2)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0a86:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden
            if (r2 == 0) goto L_0x0ab2
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionTogglePreHistoryHidden) r8
            boolean r2 = r8.new_value
            if (r2 == 0) goto L_0x0aa1
            r2 = 2131625158(0x7f0e04c6, float:1.8877516E38)
            java.lang.String r6 = "EventLogToggledInvitesHistoryOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0aa1:
            r2 = 2131625159(0x7f0e04c7, float:1.8877518E38)
            java.lang.String r6 = "EventLogToggledInvitesHistoryOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0ab2:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout
            if (r2 == 0) goto L_0x0b32
            boolean r2 = r4.megagroup
            if (r2 == 0) goto L_0x0ac0
            r2 = 2131625093(0x7f0e0485, float:1.8877384E38)
            java.lang.String r7 = "EventLogEditedGroupDescription"
            goto L_0x0ac5
        L_0x0ac0:
            r2 = 2131625090(0x7f0e0482, float:1.8877378E38)
            java.lang.String r7 = "EventLogEditedChannelDescription"
        L_0x0ac5:
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r7, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            org.telegram.tgnet.TLRPC$TL_message r2 = new org.telegram.tgnet.TLRPC$TL_message
            r2.<init>()
            r7 = 0
            r2.out = r7
            r2.unread = r7
            int r7 = r1.user_id
            r2.from_id = r7
            r2.to_id = r6
            int r6 = r1.date
            r2.date = r6
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r6 = r1.action
            r7 = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r7 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout) r7
            java.lang.String r7 = r7.new_value
            r2.message = r7
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r6 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout) r6
            java.lang.String r6 = r6.prev_value
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x0b29
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r6.<init>()
            r2.media = r6
            org.telegram.tgnet.TLRPC$TL_webPage r7 = new org.telegram.tgnet.TLRPC$TL_webPage
            r7.<init>()
            r6.webpage = r7
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            r7 = 10
            r6.flags = r7
            r6.display_url = r13
            r6.url = r13
            r7 = 2131625123(0x7f0e04a3, float:1.8877445E38)
            java.lang.String r8 = "EventLogPreviousGroupDescription"
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            r6.site_name = r7
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout r7 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeAbout) r7
            java.lang.String r7 = r7.prev_value
            r6.description = r7
            goto L_0x0e4a
        L_0x0b29:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r6.<init>()
            r2.media = r6
            goto L_0x0e4a
        L_0x0b32:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername
            if (r2 == 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername) r8
            java.lang.String r2 = r8.new_value
            boolean r7 = android.text.TextUtils.isEmpty(r2)
            if (r7 != 0) goto L_0x0b5a
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x0b4a
            r7 = 2131625078(0x7f0e0476, float:1.8877354E38)
            java.lang.String r8 = "EventLogChangedGroupLink"
            goto L_0x0b4f
        L_0x0b4a:
            r7 = 2131625077(0x7f0e0475, float:1.8877352E38)
            java.lang.String r8 = "EventLogChangedChannelLink"
        L_0x0b4f:
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r14, r5)
            r0.messageText = r7
            goto L_0x0b73
        L_0x0b5a:
            boolean r7 = r4.megagroup
            if (r7 == 0) goto L_0x0b64
            r7 = 2131625139(0x7f0e04b3, float:1.8877478E38)
            java.lang.String r8 = "EventLogRemovedGroupLink"
            goto L_0x0b69
        L_0x0b64:
            r7 = 2131625137(0x7f0e04b1, float:1.8877473E38)
            java.lang.String r8 = "EventLogRemovedChannelLink"
        L_0x0b69:
            java.lang.String r7 = org.telegram.messenger.LocaleController.getString(r8, r7)
            java.lang.CharSequence r7 = r0.replaceWithLink(r7, r14, r5)
            r0.messageText = r7
        L_0x0b73:
            org.telegram.tgnet.TLRPC$TL_message r7 = new org.telegram.tgnet.TLRPC$TL_message
            r7.<init>()
            r8 = 0
            r7.out = r8
            r7.unread = r8
            int r8 = r1.user_id
            r7.from_id = r8
            r7.to_id = r6
            int r6 = r1.date
            r7.date = r6
            boolean r6 = android.text.TextUtils.isEmpty(r2)
            if (r6 != 0) goto L_0x0bb1
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "https://"
            r6.append(r8)
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            java.lang.String r8 = r8.linkPrefix
            r6.append(r8)
            java.lang.String r8 = "/"
            r6.append(r8)
            r6.append(r2)
            java.lang.String r2 = r6.toString()
            r7.message = r2
            goto L_0x0bb3
        L_0x0bb1:
            r7.message = r13
        L_0x0bb3:
            org.telegram.tgnet.TLRPC$TL_messageEntityUrl r2 = new org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            r2.<init>()
            r6 = 0
            r2.offset = r6
            java.lang.String r6 = r7.message
            int r6 = r6.length()
            r2.length = r6
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r6 = r7.entities
            r6.add(r2)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r2 = r1.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername) r2
            java.lang.String r2 = r2.prev_value
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x0CLASSNAME
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r2.<init>()
            r7.media = r2
            org.telegram.tgnet.TLRPC$TL_webPage r6 = new org.telegram.tgnet.TLRPC$TL_webPage
            r6.<init>()
            r2.webpage = r6
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            r6 = 10
            r2.flags = r6
            r2.display_url = r13
            r2.url = r13
            r6 = 2131625124(0x7f0e04a4, float:1.8877447E38)
            java.lang.String r8 = "EventLogPreviousLink"
            java.lang.String r6 = org.telegram.messenger.LocaleController.getString(r8, r6)
            r2.site_name = r6
            org.telegram.tgnet.TLRPC$MessageMedia r2 = r7.media
            org.telegram.tgnet.TLRPC$WebPage r2 = r2.webpage
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r8 = "https://"
            r6.append(r8)
            int r8 = r0.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            java.lang.String r8 = r8.linkPrefix
            r6.append(r8)
            java.lang.String r8 = "/"
            r6.append(r8)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r8 = r1.action
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeUsername) r8
            java.lang.String r8 = r8.prev_value
            r6.append(r8)
            java.lang.String r6 = r6.toString()
            r2.description = r6
            goto L_0x0c2e
        L_0x0CLASSNAME:
            org.telegram.tgnet.TLRPC$TL_messageMediaEmpty r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            r2.<init>()
            r7.media = r2
        L_0x0c2e:
            r2 = r7
            goto L_0x0e4a
        L_0x0CLASSNAME:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage
            if (r2 == 0) goto L_0x0d7a
            org.telegram.tgnet.TLRPC$TL_message r2 = new org.telegram.tgnet.TLRPC$TL_message
            r2.<init>()
            r7 = 0
            r2.out = r7
            r2.unread = r7
            int r7 = r1.user_id
            r2.from_id = r7
            r2.to_id = r6
            int r6 = r1.date
            r2.date = r6
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r6 = r1.action
            r7 = r6
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage r7 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage) r7
            org.telegram.tgnet.TLRPC$Message r7 = r7.new_message
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage r6 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionEditMessage) r6
            org.telegram.tgnet.TLRPC$Message r6 = r6.prev_message
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r7.media
            if (r8 == 0) goto L_0x0d16
            boolean r9 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaEmpty
            if (r9 != 0) goto L_0x0d16
            boolean r8 = r8 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            if (r8 != 0) goto L_0x0d16
            java.lang.String r8 = r7.message
            java.lang.String r9 = r6.message
            boolean r8 = android.text.TextUtils.equals(r8, r9)
            r9 = 1
            r8 = r8 ^ r9
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r7.media
            java.lang.Class r9 = r9.getClass()
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r6.media
            java.lang.Class r10 = r10.getClass()
            if (r9 != r10) goto L_0x0ca3
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r7.media
            org.telegram.tgnet.TLRPC$Photo r9 = r9.photo
            if (r9 == 0) goto L_0x0c8c
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r6.media
            org.telegram.tgnet.TLRPC$Photo r10 = r10.photo
            if (r10 == 0) goto L_0x0c8c
            long r11 = r9.id
            long r9 = r10.id
            int r15 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r15 != 0) goto L_0x0ca3
        L_0x0c8c:
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r7.media
            org.telegram.tgnet.TLRPC$Document r9 = r9.document
            if (r9 == 0) goto L_0x0ca1
            org.telegram.tgnet.TLRPC$MessageMedia r10 = r6.media
            org.telegram.tgnet.TLRPC$Document r10 = r10.document
            if (r10 == 0) goto L_0x0ca1
            long r11 = r9.id
            long r9 = r10.id
            int r15 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r15 == 0) goto L_0x0ca1
            goto L_0x0ca3
        L_0x0ca1:
            r9 = 0
            goto L_0x0ca4
        L_0x0ca3:
            r9 = 1
        L_0x0ca4:
            if (r9 == 0) goto L_0x0cb8
            if (r8 == 0) goto L_0x0cb8
            r9 = 2131625097(0x7f0e0489, float:1.8877392E38)
            java.lang.String r10 = "EventLogEditedMediaCaption"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.CharSequence r9 = r0.replaceWithLink(r9, r14, r5)
            r0.messageText = r9
            goto L_0x0cd9
        L_0x0cb8:
            if (r8 == 0) goto L_0x0cca
            r9 = 2131625089(0x7f0e0481, float:1.8877376E38)
            java.lang.String r10 = "EventLogEditedCaption"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.CharSequence r9 = r0.replaceWithLink(r9, r14, r5)
            r0.messageText = r9
            goto L_0x0cd9
        L_0x0cca:
            r9 = 2131625096(0x7f0e0488, float:1.887739E38)
            java.lang.String r10 = "EventLogEditedMedia"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            java.lang.CharSequence r9 = r0.replaceWithLink(r9, r14, r5)
            r0.messageText = r9
        L_0x0cd9:
            org.telegram.tgnet.TLRPC$MessageMedia r9 = r7.media
            r2.media = r9
            if (r8 == 0) goto L_0x0d66
            org.telegram.tgnet.TLRPC$TL_webPage r8 = new org.telegram.tgnet.TLRPC$TL_webPage
            r8.<init>()
            r9.webpage = r8
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            r9 = 2131625119(0x7f0e049f, float:1.8877437E38)
            java.lang.String r10 = "EventLogOriginalCaption"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.site_name = r9
            java.lang.String r8 = r6.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0d0d
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            r8 = 2131625120(0x7f0e04a0, float:1.8877439E38)
            java.lang.String r9 = "EventLogOriginalCaptionEmpty"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r6.description = r8
            goto L_0x0d66
        L_0x0d0d:
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            java.lang.String r6 = r6.message
            r8.description = r6
            goto L_0x0d66
        L_0x0d16:
            r8 = 2131625098(0x7f0e048a, float:1.8877394E38)
            java.lang.String r9 = "EventLogEditedMessages"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            java.lang.CharSequence r8 = r0.replaceWithLink(r8, r14, r5)
            r0.messageText = r8
            java.lang.String r8 = r7.message
            r2.message = r8
            org.telegram.tgnet.TLRPC$TL_messageMediaWebPage r8 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage
            r8.<init>()
            r2.media = r8
            org.telegram.tgnet.TLRPC$TL_webPage r9 = new org.telegram.tgnet.TLRPC$TL_webPage
            r9.<init>()
            r8.webpage = r9
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            r9 = 2131625121(0x7f0e04a1, float:1.887744E38)
            java.lang.String r10 = "EventLogOriginalMessages"
            java.lang.String r9 = org.telegram.messenger.LocaleController.getString(r10, r9)
            r8.site_name = r9
            java.lang.String r8 = r6.message
            boolean r8 = android.text.TextUtils.isEmpty(r8)
            if (r8 == 0) goto L_0x0d5e
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            r8 = 2131625120(0x7f0e04a0, float:1.8877439E38)
            java.lang.String r9 = "EventLogOriginalCaptionEmpty"
            java.lang.String r8 = org.telegram.messenger.LocaleController.getString(r9, r8)
            r6.description = r8
            goto L_0x0d66
        L_0x0d5e:
            org.telegram.tgnet.TLRPC$MessageMedia r8 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r8 = r8.webpage
            java.lang.String r6 = r6.message
            r8.description = r6
        L_0x0d66:
            org.telegram.tgnet.TLRPC$ReplyMarkup r6 = r7.reply_markup
            r2.reply_markup = r6
            org.telegram.tgnet.TLRPC$MessageMedia r6 = r2.media
            org.telegram.tgnet.TLRPC$WebPage r6 = r6.webpage
            if (r6 == 0) goto L_0x0e4a
            r7 = 10
            r6.flags = r7
            r6.display_url = r13
            r6.url = r13
            goto L_0x0e4a
        L_0x0d7a:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet
            if (r2 == 0) goto L_0x0db0
            r2 = r8
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r2 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet) r2
            org.telegram.tgnet.TLRPC$InputStickerSet r2 = r2.new_stickerset
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeStickerSet) r8
            org.telegram.tgnet.TLRPC$InputStickerSet r6 = r8.new_stickerset
            if (r2 == 0) goto L_0x0d9f
            boolean r2 = r2 instanceof org.telegram.tgnet.TLRPC$TL_inputStickerSetEmpty
            if (r2 == 0) goto L_0x0d8e
            goto L_0x0d9f
        L_0x0d8e:
            r2 = 2131625083(0x7f0e047b, float:1.8877364E38)
            java.lang.String r6 = "EventLogChangedStickersSet"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0d9f:
            r2 = 2131625143(0x7f0e04b7, float:1.8877486E38)
            java.lang.String r6 = "EventLogRemovedStickersSet"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0db0:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation
            if (r2 == 0) goto L_0x0de7
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionChangeLocation) r8
            org.telegram.tgnet.TLRPC$ChannelLocation r2 = r8.new_value
            boolean r6 = r2 instanceof org.telegram.tgnet.TLRPC$TL_channelLocationEmpty
            if (r6 == 0) goto L_0x0dcd
            r2 = 2131625142(0x7f0e04b6, float:1.8877484E38)
            java.lang.String r6 = "EventLogRemovedLocation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0dcd:
            org.telegram.tgnet.TLRPC$TL_channelLocation r2 = (org.telegram.tgnet.TLRPC$TL_channelLocation) r2
            r6 = 2131625081(0x7f0e0479, float:1.887736E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r2 = r2.address
            r7 = 0
            r8[r7] = r2
            java.lang.String r2 = "EventLogChangedLocation"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r8)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0de7:
            boolean r2 = r8 instanceof org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode
            if (r2 == 0) goto L_0x0e34
            org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode r8 = (org.telegram.tgnet.TLRPC$TL_channelAdminLogEventActionToggleSlowMode) r8
            int r2 = r8.new_value
            if (r2 != 0) goto L_0x0e01
            r2 = 2131625164(0x7f0e04cc, float:1.8877528E38)
            java.lang.String r6 = "EventLogToggledSlowmodeOff"
            java.lang.String r2 = org.telegram.messenger.LocaleController.getString(r6, r2)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0e01:
            if (r2 >= r11) goto L_0x0e0a
            java.lang.String r6 = "Seconds"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
            goto L_0x0e1e
        L_0x0e0a:
            r6 = 3600(0xe10, float:5.045E-42)
            if (r2 >= r6) goto L_0x0e16
            int r2 = r2 / r11
            java.lang.String r6 = "Minutes"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
            goto L_0x0e1e
        L_0x0e16:
            int r2 = r2 / r11
            int r2 = r2 / r11
            java.lang.String r6 = "Hours"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2)
        L_0x0e1e:
            r6 = 2131625165(0x7f0e04cd, float:1.887753E38)
            r7 = 1
            java.lang.Object[] r8 = new java.lang.Object[r7]
            r7 = 0
            r8[r7] = r2
            java.lang.String r2 = "EventLogToggledSlowmodeOn"
            java.lang.String r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r8)
            java.lang.CharSequence r2 = r0.replaceWithLink(r2, r14, r5)
            r0.messageText = r2
            goto L_0x0e49
        L_0x0e34:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = "unsupported "
            r2.append(r6)
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r6 = r1.action
            r2.append(r6)
            java.lang.String r2 = r2.toString()
            r0.messageText = r2
        L_0x0e49:
            r2 = 0
        L_0x0e4a:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            if (r6 != 0) goto L_0x0e55
            org.telegram.tgnet.TLRPC$TL_messageService r6 = new org.telegram.tgnet.TLRPC$TL_messageService
            r6.<init>()
            r0.messageOwner = r6
        L_0x0e55:
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            java.lang.CharSequence r7 = r0.messageText
            java.lang.String r7 = r7.toString()
            r6.message = r7
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            int r7 = r1.user_id
            r6.from_id = r7
            int r7 = r1.date
            r6.date = r7
            r7 = 0
            r8 = r32[r7]
            int r9 = r8 + 1
            r32[r7] = r9
            r6.id = r8
            long r8 = r1.id
            r0.eventId = r8
            r6.out = r7
            org.telegram.tgnet.TLRPC$TL_peerChannel r8 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r8.<init>()
            r6.to_id = r8
            org.telegram.tgnet.TLRPC$Message r6 = r0.messageOwner
            org.telegram.tgnet.TLRPC$Peer r8 = r6.to_id
            int r9 = r4.id
            r8.channel_id = r9
            r6.unread = r7
            boolean r7 = r4.megagroup
            r8 = -2147483648(0xfffffffvar_, float:-0.0)
            if (r7 == 0) goto L_0x0e94
            int r7 = r6.flags
            r7 = r7 | r8
            r6.flags = r7
        L_0x0e94:
            org.telegram.messenger.MediaController r6 = org.telegram.messenger.MediaController.getInstance()
            org.telegram.tgnet.TLRPC$ChannelAdminLogEventAction r7 = r1.action
            org.telegram.tgnet.TLRPC$Message r7 = r7.message
            if (r7 == 0) goto L_0x0ea3
            boolean r9 = r7 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r9 != 0) goto L_0x0ea3
            r2 = r7
        L_0x0ea3:
            if (r2 == 0) goto L_0x0var_
            r7 = 0
            r2.out = r7
            r9 = r32[r7]
            int r10 = r9 + 1
            r32[r7] = r10
            r2.id = r9
            r2.reply_to_msg_id = r7
            int r7 = r2.flags
            r9 = -32769(0xffffffffffff7fff, float:NaN)
            r7 = r7 & r9
            r2.flags = r7
            boolean r4 = r4.megagroup
            if (r4 == 0) goto L_0x0ec2
            r4 = r7 | r8
            r2.flags = r4
        L_0x0ec2:
            org.telegram.messenger.MessageObject r4 = new org.telegram.messenger.MessageObject
            int r7 = r0.currentAccount
            r20 = 0
            r21 = 0
            r22 = 1
            long r8 = r0.eventId
            r17 = r4
            r18 = r7
            r19 = r2
            r23 = r8
            r17.<init>((int) r18, (org.telegram.tgnet.TLRPC$Message) r19, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC$User>) r20, (java.util.AbstractMap<java.lang.Integer, org.telegram.tgnet.TLRPC$Chat>) r21, (boolean) r22, (long) r23)
            int r2 = r4.contentType
            if (r2 < 0) goto L_0x0var_
            boolean r2 = r6.isPlayingMessage(r4)
            if (r2 == 0) goto L_0x0eef
            org.telegram.messenger.MessageObject r2 = r6.getPlayingMessageObject()
            float r7 = r2.audioProgress
            r4.audioProgress = r7
            int r2 = r2.audioProgressSec
            r4.audioProgressSec = r2
        L_0x0eef:
            int r2 = r0.currentAccount
            r7 = r29
            r0.createDateArray(r2, r1, r7, r3)
            int r2 = r29.size()
            r8 = 1
            int r2 = r2 - r8
            r7.add(r2, r4)
            goto L_0x0var_
        L_0x0var_:
            r7 = r29
            r2 = -1
            r0.contentType = r2
            goto L_0x0var_
        L_0x0var_:
            r7 = r29
        L_0x0var_:
            int r2 = r0.contentType
            if (r2 < 0) goto L_0x0var_
            int r2 = r0.currentAccount
            r0.createDateArray(r2, r1, r7, r3)
            int r1 = r29.size()
            r2 = 1
            int r1 = r1 - r2
            r7.add(r1, r0)
            java.lang.CharSequence r1 = r0.messageText
            if (r1 != 0) goto L_0x0var_
            r0.messageText = r13
        L_0x0var_:
            r26.setType()
            r26.measureInlineBotButtons()
            r26.generateCaption()
            org.telegram.tgnet.TLRPC$Message r1 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0var_
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint
            goto L_0x0var_
        L_0x0var_:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
        L_0x0var_:
            boolean r2 = r26.allowsBigEmoji()
            if (r2 == 0) goto L_0x0var_
            r2 = 1
            int[] r7 = new int[r2]
            goto L_0x0var_
        L_0x0var_:
            r7 = 0
        L_0x0var_:
            java.lang.CharSequence r2 = r0.messageText
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            r3 = 1101004800(0x41a00000, float:20.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 0
            java.lang.CharSequence r1 = org.telegram.messenger.Emoji.replaceEmoji(r2, r1, r3, r4, r7)
            r0.messageText = r1
            r0.checkEmojiOnly(r7)
            boolean r1 = r6.isPlayingMessage(r0)
            if (r1 == 0) goto L_0x0var_
            org.telegram.messenger.MessageObject r1 = r6.getPlayingMessageObject()
            float r2 = r1.audioProgress
            r0.audioProgress = r2
            int r1 = r1.audioProgressSec
            r0.audioProgressSec = r1
        L_0x0var_:
            r0.generateLayout(r5)
            r1 = 1
            r0.layoutCreated = r1
            r1 = 0
            r0.generateThumbs(r1)
            r26.checkMediaExistance()
        L_0x0var_:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.<init>(int, org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent, java.util.ArrayList, java.util.HashMap, org.telegram.tgnet.TLRPC$Chat, int[]):void");
    }

    private String getUserName(TLRPC$User tLRPC$User, ArrayList<TLRPC$MessageEntity> arrayList, int i) {
        String formatName = tLRPC$User == null ? "" : ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name);
        if (i >= 0) {
            TLRPC$TL_messageEntityMentionName tLRPC$TL_messageEntityMentionName = new TLRPC$TL_messageEntityMentionName();
            tLRPC$TL_messageEntityMentionName.user_id = tLRPC$User.id;
            tLRPC$TL_messageEntityMentionName.offset = i;
            tLRPC$TL_messageEntityMentionName.length = formatName.length();
            arrayList.add(tLRPC$TL_messageEntityMentionName);
        }
        if (TextUtils.isEmpty(tLRPC$User.username)) {
            return formatName;
        }
        if (i >= 0) {
            TLRPC$TL_messageEntityMentionName tLRPC$TL_messageEntityMentionName2 = new TLRPC$TL_messageEntityMentionName();
            tLRPC$TL_messageEntityMentionName2.user_id = tLRPC$User.id;
            tLRPC$TL_messageEntityMentionName2.offset = i + formatName.length() + 2;
            tLRPC$TL_messageEntityMentionName2.length = tLRPC$User.username.length() + 1;
            arrayList.add(tLRPC$TL_messageEntityMentionName2);
        }
        return String.format("%1$s (@%2$s)", new Object[]{formatName, tLRPC$User.username});
    }

    public void applyNewText() {
        TextPaint textPaint;
        if (!TextUtils.isEmpty(this.messageOwner.message)) {
            int[] iArr = null;
            TLRPC$User user = isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id)) : null;
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
        if (tLRPC$Message == null || (tLRPC$Peer = tLRPC$Message.to_id) == null || (tLRPC$Peer.channel_id == 0 && tLRPC$Peer.chat_id == 0)) {
            return true;
        }
        MessagesController instance = MessagesController.getInstance(this.currentAccount);
        TLRPC$Peer tLRPC$Peer2 = this.messageOwner.to_id;
        int i = tLRPC$Peer2.channel_id;
        if (i == 0) {
            i = tLRPC$Peer2.chat_id;
        }
        return !ChatObject.isActionBanned(instance.getChat(Integer.valueOf(i)), 8);
    }

    public void generateGameMessageText(TLRPC$User tLRPC$User) {
        TLRPC$MessageMedia tLRPC$MessageMedia;
        TLRPC$TL_game tLRPC$TL_game;
        if (tLRPC$User == null && this.messageOwner.from_id > 0) {
            tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
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
            tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int) getDialogId()));
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

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v4, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v13, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v17, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v20, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v23, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v26, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v29, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v32, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v35, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v38, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v41, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v44, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v47, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v50, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v53, resolved type: org.telegram.tgnet.TLRPC$Chat} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v57, resolved type: org.telegram.tgnet.TLRPC$User} */
    /* JADX WARNING: Failed to insert additional move for type inference */
    /* JADX WARNING: Incorrect type for immutable var: ssa=org.telegram.tgnet.TLRPC$User, code=org.telegram.tgnet.TLRPC$Chat, for r9v0, types: [org.telegram.tgnet.TLRPC$User] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generatePinMessageText(org.telegram.tgnet.TLRPC$Chat r9, org.telegram.tgnet.TLRPC$Chat r10) {
        /*
            r8 = this;
            if (r9 != 0) goto L_0x0032
            if (r10 != 0) goto L_0x0032
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            int r0 = r0.from_id
            if (r0 <= 0) goto L_0x001c
            int r9 = r8.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            int r0 = r0.from_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$User r9 = r9.getUser(r0)
        L_0x001c:
            if (r9 != 0) goto L_0x0032
            int r10 = r8.currentAccount
            org.telegram.messenger.MessagesController r10 = org.telegram.messenger.MessagesController.getInstance(r10)
            org.telegram.tgnet.TLRPC$Message r0 = r8.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            int r0 = r0.channel_id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            org.telegram.tgnet.TLRPC$Chat r10 = r10.getChat(r0)
        L_0x0032:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            r1 = 2131624074(0x7f0e008a, float:1.8875317E38)
            java.lang.String r2 = "ActionPinnedNoText"
            java.lang.String r3 = "un1"
            if (r0 == 0) goto L_0x0266
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageEmpty
            if (r5 != 0) goto L_0x0266
            org.telegram.tgnet.TLRPC$MessageAction r4 = r4.action
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear
            if (r4 == 0) goto L_0x004b
            goto L_0x0266
        L_0x004b:
            boolean r0 = r0.isMusic()
            if (r0 == 0) goto L_0x0066
            r0 = 2131624073(0x7f0e0089, float:1.8875315E38)
            java.lang.String r1 = "ActionPinnedMusic"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x005d
            goto L_0x005e
        L_0x005d:
            r9 = r10
        L_0x005e:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x0066:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x0083
            r0 = 2131624081(0x7f0e0091, float:1.8875332E38)
            java.lang.String r1 = "ActionPinnedVideo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x007a
            goto L_0x007b
        L_0x007a:
            r9 = r10
        L_0x007b:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x0083:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isGif()
            if (r0 == 0) goto L_0x00a0
            r0 = 2131624072(0x7f0e0088, float:1.8875313E38)
            java.lang.String r1 = "ActionPinnedGif"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0097
            goto L_0x0098
        L_0x0097:
            r9 = r10
        L_0x0098:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x00a0:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x00bd
            r0 = 2131624082(0x7f0e0092, float:1.8875334E38)
            java.lang.String r1 = "ActionPinnedVoice"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x00b4
            goto L_0x00b5
        L_0x00b4:
            r9 = r10
        L_0x00b5:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x00bd:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isRoundVideo()
            if (r0 == 0) goto L_0x00da
            r0 = 2131624078(0x7f0e008e, float:1.8875326E38)
            java.lang.String r1 = "ActionPinnedRound"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x00d1
            goto L_0x00d2
        L_0x00d1:
            r9 = r10
        L_0x00d2:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x00da:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isSticker()
            if (r0 != 0) goto L_0x0252
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            boolean r0 = r0.isAnimatedSticker()
            if (r0 == 0) goto L_0x00ec
            goto L_0x0252
        L_0x00ec:
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            org.telegram.tgnet.TLRPC$Message r4 = r0.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r4 = r4.media
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument
            if (r5 == 0) goto L_0x010b
            r0 = 2131624068(0x7f0e0084, float:1.8875305E38)
            java.lang.String r1 = "ActionPinnedFile"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0102
            goto L_0x0103
        L_0x0102:
            r9 = r10
        L_0x0103:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x010b:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeo
            if (r5 == 0) goto L_0x0124
            r0 = 2131624070(0x7f0e0086, float:1.887531E38)
            java.lang.String r1 = "ActionPinnedGeo"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x011b
            goto L_0x011c
        L_0x011b:
            r9 = r10
        L_0x011c:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x0124:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGeoLive
            if (r5 == 0) goto L_0x013d
            r0 = 2131624071(0x7f0e0087, float:1.8875311E38)
            java.lang.String r1 = "ActionPinnedGeoLive"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0134
            goto L_0x0135
        L_0x0134:
            r9 = r10
        L_0x0135:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x013d:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaContact
            if (r5 == 0) goto L_0x0156
            r0 = 2131624067(0x7f0e0083, float:1.8875303E38)
            java.lang.String r1 = "ActionPinnedContact"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x014d
            goto L_0x014e
        L_0x014d:
            r9 = r10
        L_0x014e:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x0156:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPoll
            if (r5 == 0) goto L_0x018c
            org.telegram.tgnet.TLRPC$TL_messageMediaPoll r4 = (org.telegram.tgnet.TLRPC$TL_messageMediaPoll) r4
            org.telegram.tgnet.TLRPC$TL_poll r0 = r4.poll
            boolean r0 = r0.quiz
            if (r0 == 0) goto L_0x0177
            r0 = 2131624077(0x7f0e008d, float:1.8875324E38)
            java.lang.String r1 = "ActionPinnedQuiz"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x016e
            goto L_0x016f
        L_0x016e:
            r9 = r10
        L_0x016f:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x0177:
            r0 = 2131624076(0x7f0e008c, float:1.8875321E38)
            java.lang.String r1 = "ActionPinnedPoll"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x0183
            goto L_0x0184
        L_0x0183:
            r9 = r10
        L_0x0184:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x018c:
            boolean r5 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto
            if (r5 == 0) goto L_0x01a5
            r0 = 2131624075(0x7f0e008b, float:1.887532E38)
            java.lang.String r1 = "ActionPinnedPhoto"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x019c
            goto L_0x019d
        L_0x019c:
            r9 = r10
        L_0x019d:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x01a5:
            boolean r4 = r4 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            r5 = 1101004800(0x41a00000, float:20.0)
            r6 = 1
            r7 = 0
            if (r4 == 0) goto L_0x01f2
            r0 = 2131624069(0x7f0e0085, float:1.8875307E38)
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
            if (r9 == 0) goto L_0x01d9
            goto L_0x01da
        L_0x01d9:
            r9 = r10
        L_0x01da:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            android.text.TextPaint r10 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r10 = r10.getFontMetricsInt()
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r9 = org.telegram.messenger.Emoji.replaceEmoji(r9, r10, r0, r7)
            r8.messageText = r9
            goto L_0x0274
        L_0x01f2:
            java.lang.CharSequence r0 = r0.messageText
            if (r0 == 0) goto L_0x0243
            int r0 = r0.length()
            if (r0 <= 0) goto L_0x0243
            org.telegram.messenger.MessageObject r0 = r8.replyMessageObject
            java.lang.CharSequence r0 = r0.messageText
            int r1 = r0.length()
            r2 = 20
            if (r1 <= r2) goto L_0x021d
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.CharSequence r0 = r0.subSequence(r7, r2)
            r1.append(r0)
            java.lang.String r0 = "..."
            r1.append(r0)
            java.lang.String r0 = r1.toString()
        L_0x021d:
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r5)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r1, r2, r7)
            r1 = 2131624080(0x7f0e0090, float:1.887533E38)
            java.lang.Object[] r2 = new java.lang.Object[r6]
            r2[r7] = r0
            java.lang.String r0 = "ActionPinnedText"
            java.lang.String r0 = org.telegram.messenger.LocaleController.formatString(r0, r1, r2)
            if (r9 == 0) goto L_0x023b
            goto L_0x023c
        L_0x023b:
            r9 = r10
        L_0x023c:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x0243:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r9 == 0) goto L_0x024a
            goto L_0x024b
        L_0x024a:
            r9 = r10
        L_0x024b:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x0252:
            r0 = 2131624079(0x7f0e008f, float:1.8875328E38)
            java.lang.String r1 = "ActionPinnedSticker"
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r1, r0)
            if (r9 == 0) goto L_0x025e
            goto L_0x025f
        L_0x025e:
            r9 = r10
        L_0x025f:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
            goto L_0x0274
        L_0x0266:
            java.lang.String r0 = org.telegram.messenger.LocaleController.getString(r2, r1)
            if (r9 == 0) goto L_0x026d
            goto L_0x026e
        L_0x026d:
            r9 = r10
        L_0x026e:
            java.lang.CharSequence r9 = r8.replaceWithLink(r0, r3, r9)
            r8.messageText = r9
        L_0x0274:
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
                tLRPC$TL_messageMediaPoll.results.results = tLRPC$PollResults.results;
                if (arrayList != null || bArr != null) {
                    int size2 = tLRPC$TL_messageMediaPoll.results.results.size();
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
                TLRPC$PollResults tLRPC$PollResults2 = tLRPC$TL_messageMediaPoll.results;
                tLRPC$PollResults2.total_voters = tLRPC$PollResults.total_voters;
                tLRPC$PollResults2.flags |= 4;
            }
            if ((tLRPC$PollResults.flags & 8) != 0) {
                TLRPC$PollResults tLRPC$PollResults3 = tLRPC$TL_messageMediaPoll.results;
                tLRPC$PollResults3.recent_voters = tLRPC$PollResults.recent_voters;
                tLRPC$PollResults3.flags |= 8;
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
            org.telegram.tgnet.TLRPC$TL_poll r2 = r0.poll
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
        tLRPC$TL_message.to_id = tLRPC$Message.to_id;
        tLRPC$TL_message.out = tLRPC$Message.out;
        tLRPC$TL_message.from_id = tLRPC$Message.from_id;
        return new MessageObject(this.currentAccount, tLRPC$TL_message, false);
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
                this.previousCaption = serializedData.readString(false);
                this.previousAttachPath = serializedData.readString(false);
                int readInt32 = serializedData.readInt32(false);
                this.previousCaptionEntities = new ArrayList<>(readInt32);
                for (int i = 0; i < readInt32; i++) {
                    this.previousCaptionEntities.add(TLRPC$MessageEntity.TLdeserialize(serializedData, serializedData.readInt32(false), false));
                }
                serializedData.cleanup();
            }
        }
    }

    public void measureInlineBotButtons() {
        int i;
        CharSequence charSequence;
        TLRPC$TL_messageReactions tLRPC$TL_messageReactions;
        this.wantedBotKeyboardWidth = 0;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if ((tLRPC$Message.reply_markup instanceof TLRPC$TL_replyInlineMarkup) || ((tLRPC$TL_messageReactions = tLRPC$Message.reactions) != null && !tLRPC$TL_messageReactions.results.isEmpty())) {
            Theme.createChatResources((Context) null, true);
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

    public boolean isFcmMessage() {
        return this.localType != 0;
    }

    private void updateMessageText(AbstractMap<Integer, TLRPC$User> abstractMap, AbstractMap<Integer, TLRPC$Chat> abstractMap2, SparseArray<TLRPC$User> sparseArray, SparseArray<TLRPC$Chat> sparseArray2) {
        TLRPC$User tLRPC$User;
        CharSequence charSequence;
        TLRPC$User tLRPC$User2;
        TLRPC$User tLRPC$User3;
        TLRPC$Chat tLRPC$Chat;
        String str;
        String str2;
        TLRPC$User tLRPC$User4;
        TLRPC$User tLRPC$User5;
        TLRPC$User tLRPC$User6;
        AbstractMap<Integer, TLRPC$User> abstractMap3 = abstractMap;
        AbstractMap<Integer, TLRPC$Chat> abstractMap4 = abstractMap2;
        SparseArray<TLRPC$User> sparseArray3 = sparseArray;
        SparseArray<TLRPC$Chat> sparseArray4 = sparseArray2;
        int i = this.messageOwner.from_id;
        if (i > 0) {
            if (abstractMap3 != null) {
                tLRPC$User6 = abstractMap3.get(Integer.valueOf(i));
            } else {
                tLRPC$User6 = sparseArray3 != null ? sparseArray3.get(i) : null;
            }
            if (tLRPC$User6 == null) {
                tLRPC$User6 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            tLRPC$User = tLRPC$User6;
        } else {
            tLRPC$User = null;
        }
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message instanceof TLRPC$TL_messageService) {
            TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
            if (tLRPC$MessageAction != null) {
                if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionCustomAction) {
                    this.messageText = tLRPC$MessageAction.message;
                } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatCreate) {
                    if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouCreateGroup", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", NUM), "un1", tLRPC$User);
                    }
                } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatDeleteUser) {
                    int i2 = tLRPC$MessageAction.user_id;
                    if (i2 != tLRPC$Message.from_id) {
                        if (abstractMap3 != null) {
                            tLRPC$User5 = abstractMap3.get(Integer.valueOf(i2));
                        } else {
                            tLRPC$User5 = sparseArray3 != null ? sparseArray3.get(i2) : null;
                        }
                        if (tLRPC$User5 == null) {
                            tLRPC$User5 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.action.user_id));
                        }
                        if (isOut()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", NUM), "un2", tLRPC$User5);
                        } else if (this.messageOwner.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", NUM), "un1", tLRPC$User);
                        } else {
                            CharSequence replaceWithLink = replaceWithLink(LocaleController.getString("ActionKickUser", NUM), "un2", tLRPC$User5);
                            this.messageText = replaceWithLink;
                            this.messageText = replaceWithLink(replaceWithLink, "un1", tLRPC$User);
                        }
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouLeftUser", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", NUM), "un1", tLRPC$User);
                    }
                } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatAddUser) {
                    int i3 = tLRPC$MessageAction.user_id;
                    if (i3 == 0 && tLRPC$MessageAction.users.size() == 1) {
                        i3 = this.messageOwner.action.users.get(0).intValue();
                    }
                    if (i3 != 0) {
                        if (abstractMap3 != null) {
                            tLRPC$User4 = abstractMap3.get(Integer.valueOf(i3));
                        } else {
                            tLRPC$User4 = sparseArray3 != null ? sparseArray3.get(i3) : null;
                        }
                        if (tLRPC$User4 == null) {
                            tLRPC$User4 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i3));
                        }
                        TLRPC$Message tLRPC$Message2 = this.messageOwner;
                        if (i3 == tLRPC$Message2.from_id) {
                            if (tLRPC$Message2.to_id.channel_id != 0 && !isMegagroup()) {
                                this.messageText = LocaleController.getString("ChannelJoined", NUM);
                            } else if (this.messageOwner.to_id.channel_id == 0 || !isMegagroup()) {
                                if (isOut()) {
                                    this.messageText = LocaleController.getString("ActionAddUserSelfYou", NUM);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", NUM), "un1", tLRPC$User);
                                }
                            } else if (i3 == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                this.messageText = LocaleController.getString("ChannelMegaJoined", NUM);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", NUM), "un1", tLRPC$User);
                            }
                        } else if (isOut()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", NUM), "un2", tLRPC$User4);
                        } else if (i3 != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            CharSequence replaceWithLink2 = replaceWithLink(LocaleController.getString("ActionAddUser", NUM), "un2", tLRPC$User4);
                            this.messageText = replaceWithLink2;
                            this.messageText = replaceWithLink(replaceWithLink2, "un1", tLRPC$User);
                        } else if (this.messageOwner.to_id.channel_id == 0) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", NUM), "un1", tLRPC$User);
                        } else if (isMegagroup()) {
                            this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", NUM), "un1", tLRPC$User);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", NUM), "un1", tLRPC$User);
                        }
                    } else if (isOut()) {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", NUM), "un2", this.messageOwner.action.users, abstractMap, sparseArray);
                    } else {
                        CharSequence replaceWithLink3 = replaceWithLink(LocaleController.getString("ActionAddUser", NUM), "un2", this.messageOwner.action.users, abstractMap, sparseArray);
                        this.messageText = replaceWithLink3;
                        this.messageText = replaceWithLink(replaceWithLink3, "un1", tLRPC$User);
                    }
                } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatJoinedByLink) {
                    if (isOut()) {
                        this.messageText = LocaleController.getString("ActionInviteYou", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", NUM), "un1", tLRPC$User);
                    }
                } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatEditPhoto) {
                    if (tLRPC$Message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelChangedPhoto", NUM);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouChangedPhoto", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", NUM), "un1", tLRPC$User);
                    }
                } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatEditTitle) {
                    if (tLRPC$Message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelChangedTitle", NUM).replace("un2", this.messageOwner.action.title);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouChangedTitle", NUM).replace("un2", this.messageOwner.action.title);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", NUM).replace("un2", this.messageOwner.action.title), "un1", tLRPC$User);
                    }
                } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatDeletePhoto) {
                    if (tLRPC$Message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", NUM);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouRemovedPhoto", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", NUM), "un1", tLRPC$User);
                    }
                } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionTTLChange) {
                    if (tLRPC$MessageAction.ttl != 0) {
                        if (isOut()) {
                            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", NUM, LocaleController.formatTTLString(this.messageOwner.action.ttl));
                        } else {
                            this.messageText = LocaleController.formatString("MessageLifetimeChanged", NUM, UserObject.getFirstName(tLRPC$User), LocaleController.formatTTLString(this.messageOwner.action.ttl));
                        }
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", NUM);
                    } else {
                        this.messageText = LocaleController.formatString("MessageLifetimeRemoved", NUM, UserObject.getFirstName(tLRPC$User));
                    }
                } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionLoginUnknownLocation) {
                    long j = ((long) tLRPC$Message.date) * 1000;
                    if (LocaleController.getInstance().formatterDay == null || LocaleController.getInstance().formatterYear == null) {
                        str = "" + this.messageOwner.date;
                    } else {
                        str = LocaleController.formatString("formatDateAtTime", NUM, LocaleController.getInstance().formatterYear.format(j), LocaleController.getInstance().formatterDay.format(j));
                    }
                    TLRPC$User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                    if (currentUser == null) {
                        if (abstractMap3 != null) {
                            currentUser = abstractMap3.get(Integer.valueOf(this.messageOwner.to_id.user_id));
                        } else if (sparseArray3 != null) {
                            currentUser = sparseArray3.get(this.messageOwner.to_id.user_id);
                        }
                        if (currentUser == null) {
                            currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
                        }
                    }
                    if (currentUser != null) {
                        str2 = UserObject.getFirstName(currentUser);
                    } else {
                        str2 = "";
                    }
                    TLRPC$MessageAction tLRPC$MessageAction2 = this.messageOwner.action;
                    this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", NUM, str2, str, tLRPC$MessageAction2.title, tLRPC$MessageAction2.address);
                } else if ((tLRPC$MessageAction instanceof TLRPC$TL_messageActionUserJoined) || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionContactSignUp)) {
                    charSequence = "";
                    this.messageText = LocaleController.formatString("NotificationContactJoined", NUM, UserObject.getUserName(tLRPC$User));
                } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionUserUpdatedPhoto) {
                    this.messageText = LocaleController.formatString("NotificationContactNewPhoto", NUM, UserObject.getUserName(tLRPC$User));
                } else {
                    charSequence = "";
                    if (tLRPC$MessageAction instanceof TLRPC$TL_messageEncryptedAction) {
                        TLRPC$DecryptedMessageAction tLRPC$DecryptedMessageAction = tLRPC$MessageAction.encryptedAction;
                        if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionScreenshotMessages) {
                            if (isOut()) {
                                this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", NUM, new Object[0]);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", NUM), "un1", tLRPC$User);
                            }
                        } else if (tLRPC$DecryptedMessageAction instanceof TLRPC$TL_decryptedMessageActionSetMessageTTL) {
                            TLRPC$TL_decryptedMessageActionSetMessageTTL tLRPC$TL_decryptedMessageActionSetMessageTTL = (TLRPC$TL_decryptedMessageActionSetMessageTTL) tLRPC$DecryptedMessageAction;
                            if (tLRPC$TL_decryptedMessageActionSetMessageTTL.ttl_seconds != 0) {
                                if (isOut()) {
                                    this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", NUM, LocaleController.formatTTLString(tLRPC$TL_decryptedMessageActionSetMessageTTL.ttl_seconds));
                                } else {
                                    this.messageText = LocaleController.formatString("MessageLifetimeChanged", NUM, UserObject.getFirstName(tLRPC$User), LocaleController.formatTTLString(tLRPC$TL_decryptedMessageActionSetMessageTTL.ttl_seconds));
                                }
                            } else if (isOut()) {
                                this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", NUM);
                            } else {
                                this.messageText = LocaleController.formatString("MessageLifetimeRemoved", NUM, UserObject.getFirstName(tLRPC$User));
                            }
                        }
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionScreenshotTaken) {
                        if (isOut()) {
                            this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", NUM, new Object[0]);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", NUM), "un1", tLRPC$User);
                        }
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionCreatedBroadcastList) {
                        this.messageText = LocaleController.formatString("YouCreatedBroadcastList", NUM, new Object[0]);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChannelCreate) {
                        if (isMegagroup()) {
                            this.messageText = LocaleController.getString("ActionCreateMega", NUM);
                        } else {
                            this.messageText = LocaleController.getString("ActionCreateChannel", NUM);
                        }
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatMigrateTo) {
                        this.messageText = LocaleController.getString("ActionMigrateFromGroup", NUM);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChannelMigrateFrom) {
                        this.messageText = LocaleController.getString("ActionMigrateFromGroup", NUM);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPinMessage) {
                        if (tLRPC$User == null) {
                            if (abstractMap4 != null) {
                                tLRPC$Chat = abstractMap4.get(Integer.valueOf(tLRPC$Message.to_id.channel_id));
                            } else if (sparseArray4 != null) {
                                tLRPC$Chat = sparseArray4.get(tLRPC$Message.to_id.channel_id);
                            }
                            generatePinMessageText(tLRPC$User, tLRPC$Chat);
                        }
                        tLRPC$Chat = null;
                        generatePinMessageText(tLRPC$User, tLRPC$Chat);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionHistoryClear) {
                        this.messageText = LocaleController.getString("HistoryCleared", NUM);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionGameScore) {
                        generateGameMessageText(tLRPC$User);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPhoneCall) {
                        TLRPC$TL_messageActionPhoneCall tLRPC$TL_messageActionPhoneCall = (TLRPC$TL_messageActionPhoneCall) tLRPC$MessageAction;
                        boolean z = tLRPC$TL_messageActionPhoneCall.reason instanceof TLRPC$TL_phoneCallDiscardReasonMissed;
                        if (tLRPC$Message.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            if (z) {
                                this.messageText = LocaleController.getString("CallMessageOutgoingMissed", NUM);
                            } else {
                                this.messageText = LocaleController.getString("CallMessageOutgoing", NUM);
                            }
                        } else if (z) {
                            this.messageText = LocaleController.getString("CallMessageIncomingMissed", NUM);
                        } else if (tLRPC$TL_messageActionPhoneCall.reason instanceof TLRPC$TL_phoneCallDiscardReasonBusy) {
                            this.messageText = LocaleController.getString("CallMessageIncomingDeclined", NUM);
                        } else {
                            this.messageText = LocaleController.getString("CallMessageIncoming", NUM);
                        }
                        int i4 = tLRPC$TL_messageActionPhoneCall.duration;
                        if (i4 > 0) {
                            String formatCallDuration = LocaleController.formatCallDuration(i4);
                            String formatString = LocaleController.formatString("CallMessageWithDuration", NUM, this.messageText, formatCallDuration);
                            this.messageText = formatString;
                            String charSequence2 = formatString.toString();
                            int indexOf = charSequence2.indexOf(formatCallDuration);
                            if (indexOf != -1) {
                                SpannableString spannableString = new SpannableString(this.messageText);
                                int length = formatCallDuration.length() + indexOf;
                                if (indexOf > 0 && charSequence2.charAt(indexOf - 1) == '(') {
                                    indexOf--;
                                }
                                if (length < charSequence2.length() && charSequence2.charAt(length) == ')') {
                                    length++;
                                }
                                spannableString.setSpan(new TypefaceSpan(Typeface.DEFAULT), indexOf, length, 0);
                                this.messageText = spannableString;
                            }
                        }
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionPaymentSent) {
                        int dialogId = (int) getDialogId();
                        if (abstractMap3 != null) {
                            tLRPC$User3 = abstractMap3.get(Integer.valueOf(dialogId));
                        } else {
                            tLRPC$User3 = sparseArray3 != null ? sparseArray3.get(dialogId) : null;
                        }
                        if (tLRPC$User3 == null) {
                            tLRPC$User3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(dialogId));
                        }
                        generatePaymentSentMessageText(tLRPC$User3);
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionBotAllowed) {
                        String str3 = ((TLRPC$TL_messageActionBotAllowed) tLRPC$MessageAction).domain;
                        String string = LocaleController.getString("ActionBotAllowed", NUM);
                        int indexOf2 = string.indexOf("%1$s");
                        SpannableString spannableString2 = new SpannableString(String.format(string, new Object[]{str3}));
                        if (indexOf2 >= 0) {
                            spannableString2.setSpan(new URLSpanNoUnderlineBold("http://" + str3), indexOf2, str3.length() + indexOf2, 33);
                        }
                        this.messageText = spannableString2;
                    } else if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionSecureValuesSent) {
                        TLRPC$TL_messageActionSecureValuesSent tLRPC$TL_messageActionSecureValuesSent = (TLRPC$TL_messageActionSecureValuesSent) tLRPC$MessageAction;
                        StringBuilder sb = new StringBuilder();
                        int size = tLRPC$TL_messageActionSecureValuesSent.types.size();
                        for (int i5 = 0; i5 < size; i5++) {
                            TLRPC$SecureValueType tLRPC$SecureValueType = tLRPC$TL_messageActionSecureValuesSent.types.get(i5);
                            if (sb.length() > 0) {
                                sb.append(", ");
                            }
                            if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePhone) {
                                sb.append(LocaleController.getString("ActionBotDocumentPhone", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeEmail) {
                                sb.append(LocaleController.getString("ActionBotDocumentEmail", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeAddress) {
                                sb.append(LocaleController.getString("ActionBotDocumentAddress", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePersonalDetails) {
                                sb.append(LocaleController.getString("ActionBotDocumentIdentity", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassport) {
                                sb.append(LocaleController.getString("ActionBotDocumentPassport", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeDriverLicense) {
                                sb.append(LocaleController.getString("ActionBotDocumentDriverLicence", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeIdentityCard) {
                                sb.append(LocaleController.getString("ActionBotDocumentIdentityCard", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeUtilityBill) {
                                sb.append(LocaleController.getString("ActionBotDocumentUtilityBill", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeBankStatement) {
                                sb.append(LocaleController.getString("ActionBotDocumentBankStatement", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeRentalAgreement) {
                                sb.append(LocaleController.getString("ActionBotDocumentRentalAgreement", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeInternalPassport) {
                                sb.append(LocaleController.getString("ActionBotDocumentInternalPassport", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypePassportRegistration) {
                                sb.append(LocaleController.getString("ActionBotDocumentPassportRegistration", NUM));
                            } else if (tLRPC$SecureValueType instanceof TLRPC$TL_secureValueTypeTemporaryRegistration) {
                                sb.append(LocaleController.getString("ActionBotDocumentTemporaryRegistration", NUM));
                            }
                        }
                        TLRPC$Peer tLRPC$Peer = this.messageOwner.to_id;
                        if (tLRPC$Peer != null) {
                            if (abstractMap3 != null) {
                                tLRPC$User2 = abstractMap3.get(Integer.valueOf(tLRPC$Peer.user_id));
                            } else {
                                tLRPC$User2 = sparseArray3 != null ? sparseArray3.get(tLRPC$Peer.user_id) : null;
                            }
                            if (tLRPC$User2 == null) {
                                tLRPC$User2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
                            }
                        } else {
                            tLRPC$User2 = null;
                        }
                        this.messageText = LocaleController.formatString("ActionBotDocuments", NUM, UserObject.getFirstName(tLRPC$User2), sb.toString());
                    }
                }
            }
            charSequence = "";
        } else {
            charSequence = "";
            this.isRestrictedMessage = false;
            String restrictionReason = MessagesController.getRestrictionReason(tLRPC$Message.restriction_reason);
            if (!TextUtils.isEmpty(restrictionReason)) {
                this.messageText = restrictionReason;
                this.isRestrictedMessage = true;
            } else if (!isMediaEmpty()) {
                TLRPC$Message tLRPC$Message3 = this.messageOwner;
                TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message3.media;
                if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDice) {
                    this.messageText = "🎲";
                } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPoll) {
                    if (((TLRPC$TL_messageMediaPoll) tLRPC$MessageMedia).poll.quiz) {
                        this.messageText = LocaleController.getString("QuizPoll", NUM);
                    } else {
                        this.messageText = LocaleController.getString("Poll", NUM);
                    }
                } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) {
                    if (tLRPC$MessageMedia.ttl_seconds == 0 || (tLRPC$Message3 instanceof TLRPC$TL_message_secret)) {
                        this.messageText = LocaleController.getString("AttachPhoto", NUM);
                    } else {
                        this.messageText = LocaleController.getString("AttachDestructingPhoto", NUM);
                    }
                } else if (isVideo() || ((this.messageOwner.media instanceof TLRPC$TL_messageMediaDocument) && (getDocument() instanceof TLRPC$TL_documentEmpty) && this.messageOwner.media.ttl_seconds != 0)) {
                    TLRPC$Message tLRPC$Message4 = this.messageOwner;
                    if (tLRPC$Message4.media.ttl_seconds == 0 || (tLRPC$Message4 instanceof TLRPC$TL_message_secret)) {
                        this.messageText = LocaleController.getString("AttachVideo", NUM);
                    } else {
                        this.messageText = LocaleController.getString("AttachDestructingVideo", NUM);
                    }
                } else if (isVoice()) {
                    this.messageText = LocaleController.getString("AttachAudio", NUM);
                } else if (isRoundVideo()) {
                    this.messageText = LocaleController.getString("AttachRound", NUM);
                } else {
                    TLRPC$Message tLRPC$Message5 = this.messageOwner;
                    TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message5.media;
                    if ((tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaGeo) || (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaVenue)) {
                        this.messageText = LocaleController.getString("AttachLocation", NUM);
                    } else if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaGeoLive) {
                        this.messageText = LocaleController.getString("AttachLiveLocation", NUM);
                    } else if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaContact) {
                        this.messageText = LocaleController.getString("AttachContact", NUM);
                        if (!TextUtils.isEmpty(this.messageOwner.media.vcard)) {
                            this.vCardData = VCardData.parse(this.messageOwner.media.vcard);
                        }
                    } else if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaGame) {
                        this.messageText = tLRPC$Message5.message;
                    } else if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaInvoice) {
                        this.messageText = tLRPC$MessageMedia2.description;
                    } else if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaUnsupported) {
                        this.messageText = LocaleController.getString("UnsupportedMedia", NUM);
                    } else if (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaDocument) {
                        if (isSticker() || isAnimatedSticker()) {
                            String strickerChar = getStrickerChar();
                            if (strickerChar == null || strickerChar.length() <= 0) {
                                this.messageText = LocaleController.getString("AttachSticker", NUM);
                            } else {
                                this.messageText = String.format("%s %s", new Object[]{strickerChar, LocaleController.getString("AttachSticker", NUM)});
                            }
                        } else if (isMusic()) {
                            this.messageText = LocaleController.getString("AttachMusic", NUM);
                        } else if (isGif()) {
                            this.messageText = LocaleController.getString("AttachGif", NUM);
                        } else {
                            String documentFileName = FileLoader.getDocumentFileName(getDocument());
                            if (documentFileName == null || documentFileName.length() <= 0) {
                                this.messageText = LocaleController.getString("AttachDocument", NUM);
                            } else {
                                this.messageText = documentFileName;
                            }
                        }
                    }
                }
            } else {
                this.messageText = this.messageOwner.message;
            }
        }
        if (this.messageText == null) {
            this.messageText = charSequence;
        }
    }

    public void setType() {
        int i = this.type;
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
                            } else if (isGifDocument(document)) {
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
            updateMessageText(MessagesController.getInstance(this.currentAccount).getUsers(), MessagesController.getInstance(this.currentAccount).getChats(), (SparseArray<TLRPC$User>) null, (SparseArray<TLRPC$Chat>) null);
            generateThumbs(false);
        }
    }

    public boolean checkLayout() {
        CharSequence charSequence;
        TextPaint textPaint;
        if (!(this.type != 0 || this.messageOwner.to_id == null || (charSequence = this.messageText) == null || charSequence.length() == 0)) {
            if (this.layoutCreated) {
                if (Math.abs(this.generatedWithMinSize - (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) > AndroidUtilities.dp(52.0f) || this.generatedWithDensity != AndroidUtilities.density) {
                    this.layoutCreated = false;
                }
            }
            if (!this.layoutCreated) {
                this.layoutCreated = true;
                int[] iArr = null;
                TLRPC$User user = isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id)) : null;
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

    public static boolean isGifDocument(WebFile webFile) {
        return webFile != null && (webFile.mime_type.equals("image/gif") || isNewGifDocument(webFile));
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = r2.mime_type;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isGifDocument(org.telegram.tgnet.TLRPC$Document r2) {
        /*
            if (r2 == 0) goto L_0x0016
            java.lang.String r0 = r2.mime_type
            if (r0 == 0) goto L_0x0016
            java.lang.String r1 = "image/gif"
            boolean r0 = r0.equals(r1)
            if (r0 != 0) goto L_0x0014
            boolean r2 = isNewGifDocument((org.telegram.tgnet.TLRPC$Document) r2)
            if (r2 == 0) goto L_0x0016
        L_0x0014:
            r2 = 1
            goto L_0x0017
        L_0x0016:
            r2 = 0
        L_0x0017:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isGifDocument(org.telegram.tgnet.TLRPC$Document):boolean");
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
            if (isDocumentHasThumb(tLRPC$Document) && (lowerCase.equals("image/png") || lowerCase.equals("image/jpg") || lowerCase.equals("image/jpeg"))) {
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
                    z = tLRPC$DocumentAttribute.round_message;
                    i2 = i;
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

    public void generateThumbs(boolean z) {
        ArrayList<TLRPC$PhotoSize> arrayList;
        ArrayList<TLRPC$PhotoSize> arrayList2;
        ArrayList<TLRPC$PhotoSize> arrayList3;
        ArrayList<TLRPC$PhotoSize> arrayList4;
        ArrayList<TLRPC$PhotoSize> arrayList5;
        ArrayList<TLRPC$PhotoSize> arrayList6;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message instanceof TLRPC$TL_messageService) {
            TLRPC$MessageAction tLRPC$MessageAction = tLRPC$Message.action;
            if (tLRPC$MessageAction instanceof TLRPC$TL_messageActionChatEditPhoto) {
                TLRPC$Photo tLRPC$Photo = tLRPC$MessageAction.photo;
                if (!z) {
                    this.photoThumbs = new ArrayList<>(tLRPC$Photo.sizes);
                } else {
                    ArrayList<TLRPC$PhotoSize> arrayList7 = this.photoThumbs;
                    if (arrayList7 != null && !arrayList7.isEmpty()) {
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
                if (tLRPC$Photo.dc_id != 0) {
                    int size = this.photoThumbs.size();
                    for (int i3 = 0; i3 < size; i3++) {
                        TLRPC$FileLocation tLRPC$FileLocation = this.photoThumbs.get(i3).location;
                        tLRPC$FileLocation.dc_id = tLRPC$Photo.dc_id;
                        tLRPC$FileLocation.file_reference = tLRPC$Photo.file_reference;
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
                        ArrayList<TLRPC$PhotoSize> arrayList8 = this.photoThumbs;
                        if (arrayList8 != null && !arrayList8.isEmpty()) {
                            for (int i4 = 0; i4 < this.photoThumbs.size(); i4++) {
                                TLRPC$PhotoSize tLRPC$PhotoSize3 = this.photoThumbs.get(i4);
                                if (tLRPC$PhotoSize3 != null) {
                                    int i5 = 0;
                                    while (true) {
                                        if (i5 < tLRPC$Photo2.sizes.size()) {
                                            TLRPC$PhotoSize tLRPC$PhotoSize4 = tLRPC$Photo2.sizes.get(i5);
                                            if (tLRPC$PhotoSize4 != null && !(tLRPC$PhotoSize4 instanceof TLRPC$TL_photoSizeEmpty) && tLRPC$PhotoSize4.type.equals(tLRPC$PhotoSize3.type)) {
                                                tLRPC$PhotoSize3.location = tLRPC$PhotoSize4.location;
                                                break;
                                            }
                                            i5++;
                                        } else {
                                            break;
                                        }
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
                            ArrayList<TLRPC$PhotoSize> arrayList9 = new ArrayList<>();
                            this.photoThumbs = arrayList9;
                            arrayList9.addAll(document.thumbs);
                        } else if (arrayList4 != null && !arrayList4.isEmpty()) {
                            updatePhotoSizeLocations(this.photoThumbs, document.thumbs);
                        }
                        this.photoThumbsObject = document;
                    }
                } else if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaGame) {
                    TLRPC$Document tLRPC$Document = tLRPC$MessageMedia.game.document;
                    if (tLRPC$Document != null && isDocumentHasThumb(tLRPC$Document)) {
                        if (!z) {
                            ArrayList<TLRPC$PhotoSize> arrayList10 = new ArrayList<>();
                            this.photoThumbs = arrayList10;
                            arrayList10.addAll(tLRPC$Document.thumbs);
                        } else {
                            ArrayList<TLRPC$PhotoSize> arrayList11 = this.photoThumbs;
                            if (arrayList11 != null && !arrayList11.isEmpty()) {
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
                            ArrayList<TLRPC$PhotoSize> arrayList12 = new ArrayList<>();
                            this.photoThumbs = arrayList12;
                            arrayList12.addAll(tLRPC$Document2.thumbs);
                        } else {
                            ArrayList<TLRPC$PhotoSize> arrayList13 = this.photoThumbs;
                            if (arrayList13 != null && !arrayList13.isEmpty()) {
                                updatePhotoSizeLocations(this.photoThumbs, tLRPC$Document2.thumbs);
                            }
                        }
                        this.photoThumbsObject = tLRPC$Document2;
                    }
                }
            }
        } else if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) && isDocumentHasThumb(this.emojiAnimatedSticker)) {
            if (!z || (arrayList6 = this.photoThumbs) == null) {
                ArrayList<TLRPC$PhotoSize> arrayList14 = new ArrayList<>();
                this.photoThumbs = arrayList14;
                arrayList14.addAll(this.emojiAnimatedSticker.thumbs);
            } else if (arrayList6 != null && !arrayList6.isEmpty()) {
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

    public CharSequence replaceWithLink(CharSequence charSequence, String str, ArrayList<Integer> arrayList, AbstractMap<Integer, TLRPC$User> abstractMap, SparseArray<TLRPC$User> sparseArray) {
        if (TextUtils.indexOf(charSequence, str) < 0) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("");
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC$User tLRPC$User = null;
            if (abstractMap != null) {
                tLRPC$User = abstractMap.get(arrayList.get(i));
            } else if (sparseArray != null) {
                tLRPC$User = sparseArray.get(arrayList.get(i).intValue());
            }
            if (tLRPC$User == null) {
                tLRPC$User = MessagesController.getInstance(this.currentAccount).getUser(arrayList.get(i));
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
        return TextUtils.replace(charSequence, new String[]{str}, new CharSequence[]{spannableStringBuilder});
    }

    public CharSequence replaceWithLink(CharSequence charSequence, String str, TLObject tLObject) {
        String str2;
        String str3;
        int indexOf = TextUtils.indexOf(charSequence, str);
        if (indexOf < 0) {
            return charSequence;
        }
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
        } else {
            str2 = "0";
            str3 = "";
        }
        String replace = str3.replace(10, ' ');
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(TextUtils.replace(charSequence, new String[]{str}, new String[]{replace}));
        spannableStringBuilder.setSpan(new URLSpanNoUnderlineBold("" + str2), indexOf, replace.length() + indexOf, 33);
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
        TLRPC$PhotoSize closestPhotoSizeWithSize;
        TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(getDocument());
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

    /* JADX WARNING: Removed duplicated region for block: B:33:0x0096  */
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
            org.telegram.tgnet.TLRPC$WebPage r0 = r0.webpage
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_webPage
            if (r1 == 0) goto L_0x0050
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x0050
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
            goto L_0x008e
        L_0x0050:
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 == 0) goto L_0x0071
            org.telegram.tgnet.TLRPC$TL_game r0 = r0.game
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x0071
            android.text.Spannable$Factory r0 = android.text.Spannable.Factory.getInstance()
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            org.telegram.tgnet.TLRPC$TL_game r1 = r1.game
            java.lang.String r1 = r1.description
            android.text.Spannable r0 = r0.newSpannable(r1)
            r10.linkDescription = r0
            goto L_0x008d
        L_0x0071:
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaInvoice
            if (r1 == 0) goto L_0x008d
            java.lang.String r0 = r0.description
            if (r0 == 0) goto L_0x008d
            android.text.Spannable$Factory r0 = android.text.Spannable.Factory.getInstance()
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r1.media
            java.lang.String r1 = r1.description
            android.text.Spannable r0 = r0.newSpannable(r1)
            r10.linkDescription = r0
        L_0x008d:
            r7 = 0
        L_0x008e:
            java.lang.CharSequence r0 = r10.linkDescription
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x00d9
            java.lang.CharSequence r0 = r10.linkDescription
            boolean r0 = containsUrls(r0)
            if (r0 == 0) goto L_0x00aa
            java.lang.CharSequence r0 = r10.linkDescription     // Catch:{ Exception -> 0x00a6 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x00a6 }
            org.telegram.messenger.AndroidUtilities.addLinks(r0, r2)     // Catch:{ Exception -> 0x00a6 }
            goto L_0x00aa
        L_0x00a6:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00aa:
            java.lang.CharSequence r0 = r10.linkDescription
            android.text.TextPaint r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
            android.graphics.Paint$FontMetricsInt r1 = r1.getFontMetricsInt()
            r2 = 1101004800(0x41a00000, float:20.0)
            int r2 = org.telegram.messenger.AndroidUtilities.dp(r2)
            java.lang.CharSequence r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r1, r2, r3)
            r10.linkDescription = r0
            if (r7 == 0) goto L_0x00d9
            boolean r0 = r0 instanceof android.text.Spannable
            if (r0 != 0) goto L_0x00cd
            android.text.SpannableStringBuilder r0 = new android.text.SpannableStringBuilder
            java.lang.CharSequence r1 = r10.linkDescription
            r0.<init>(r1)
            r10.linkDescription = r0
        L_0x00cd:
            boolean r4 = r10.isOutOwner()
            java.lang.CharSequence r5 = r10.linkDescription
            r6 = 0
            r8 = 0
            r9 = 0
            addUrlsByPattern(r4, r5, r6, r7, r8, r9)
        L_0x00d9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLinkDescription():void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0095, code lost:
        if (r10.messageOwner.send_state == 0) goto L_0x0097;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x009b, code lost:
        if (r10.messageOwner.id >= 0) goto L_0x009e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateCaption() {
        /*
            r10 = this;
            java.lang.CharSequence r0 = r10.caption
            if (r0 != 0) goto L_0x00fa
            boolean r0 = r10.isRoundVideo()
            if (r0 == 0) goto L_0x000c
            goto L_0x00fa
        L_0x000c:
            boolean r0 = r10.isMediaEmpty()
            if (r0 != 0) goto L_0x00fa
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r1 = r0.media
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r1 != 0) goto L_0x00fa
            java.lang.String r0 = r0.message
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 != 0) goto L_0x00fa
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
            if (r1 == 0) goto L_0x005e
            r0 = 0
        L_0x0041:
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r1.entities
            int r1 = r1.size()
            if (r0 >= r1) goto L_0x005c
            org.telegram.tgnet.TLRPC$Message r1 = r10.messageOwner
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r1 = r1.entities
            java.lang.Object r1 = r1.get(r0)
            boolean r1 = r1 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            if (r1 != 0) goto L_0x0059
            r0 = 1
            goto L_0x0065
        L_0x0059:
            int r0 = r0 + 1
            goto L_0x0041
        L_0x005c:
            r0 = 0
            goto L_0x0065
        L_0x005e:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r0 = r0.entities
            boolean r0 = r0.isEmpty()
            r0 = r0 ^ r2
        L_0x0065:
            if (r0 != 0) goto L_0x009e
            long r0 = r10.eventId
            r4 = 0
            int r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x009d
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_old
            if (r1 != 0) goto L_0x009d
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_layer68
            if (r1 != 0) goto L_0x009d
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaPhoto_layer74
            if (r1 != 0) goto L_0x009d
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument_old
            if (r1 != 0) goto L_0x009d
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument_layer68
            if (r1 != 0) goto L_0x009d
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaDocument_layer74
            if (r0 != 0) goto L_0x009d
            boolean r0 = r10.isOut()
            if (r0 == 0) goto L_0x0097
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            int r0 = r0.send_state
            if (r0 != 0) goto L_0x009d
        L_0x0097:
            org.telegram.tgnet.TLRPC$Message r0 = r10.messageOwner
            int r0 = r0.id
            if (r0 >= 0) goto L_0x009e
        L_0x009d:
            r3 = 1
        L_0x009e:
            if (r3 == 0) goto L_0x00c2
            java.lang.CharSequence r0 = r10.caption
            boolean r0 = containsUrls(r0)
            if (r0 == 0) goto L_0x00b5
            java.lang.CharSequence r0 = r10.caption     // Catch:{ Exception -> 0x00b1 }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ Exception -> 0x00b1 }
            r1 = 5
            org.telegram.messenger.AndroidUtilities.addLinks(r0, r1)     // Catch:{ Exception -> 0x00b1 }
            goto L_0x00b5
        L_0x00b1:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00b5:
            boolean r4 = r10.isOutOwner()
            java.lang.CharSequence r5 = r10.caption
            r6 = 1
            r7 = 0
            r8 = 0
            r9 = 1
            addUrlsByPattern(r4, r5, r6, r7, r8, r9)
        L_0x00c2:
            java.lang.CharSequence r0 = r10.caption
            r10.addEntitiesToText(r0, r3)
            boolean r0 = r10.isVideo()
            if (r0 == 0) goto L_0x00de
            boolean r1 = r10.isOutOwner()
            java.lang.CharSequence r2 = r10.caption
            r3 = 1
            r4 = 3
            int r5 = r10.getDuration()
            r6 = 0
            addUrlsByPattern(r1, r2, r3, r4, r5, r6)
            goto L_0x00fa
        L_0x00de:
            boolean r0 = r10.isMusic()
            if (r0 != 0) goto L_0x00ea
            boolean r0 = r10.isVoice()
            if (r0 == 0) goto L_0x00fa
        L_0x00ea:
            boolean r1 = r10.isOutOwner()
            java.lang.CharSequence r2 = r10.caption
            r3 = 1
            r4 = 4
            int r5 = r10.getDuration()
            r6 = 0
            addUrlsByPattern(r1, r2, r3, r4, r5, r6)
        L_0x00fa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateCaption():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:79:0x01d9 A[ADDED_TO_REGION, Catch:{ Exception -> 0x01f2 }] */
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
            java.util.regex.Pattern r5 = instagramUrlPattern     // Catch:{ Exception -> 0x01f2 }
            if (r5 != 0) goto L_0x001a
            java.lang.String r5 = "(^|\\s|\\()@[a-zA-Z\\d_.]{1,32}|(^|\\s|\\()#[\\w.]+"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x01f2 }
            instagramUrlPattern = r5     // Catch:{ Exception -> 0x01f2 }
        L_0x001a:
            java.util.regex.Pattern r5 = instagramUrlPattern     // Catch:{ Exception -> 0x01f2 }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x01f2 }
            goto L_0x0046
        L_0x0021:
            java.util.regex.Pattern r5 = urlPattern     // Catch:{ Exception -> 0x01f2 }
            if (r5 != 0) goto L_0x002d
            java.lang.String r5 = "(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s|\\()@[a-zA-Z\\d_]{1,32}|(^|\\s|\\()#[^0-9][\\w.]+|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x01f2 }
            urlPattern = r5     // Catch:{ Exception -> 0x01f2 }
        L_0x002d:
            java.util.regex.Pattern r5 = urlPattern     // Catch:{ Exception -> 0x01f2 }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x01f2 }
            goto L_0x0046
        L_0x0034:
            java.util.regex.Pattern r5 = videoTimeUrlPattern     // Catch:{ Exception -> 0x01f2 }
            if (r5 != 0) goto L_0x0040
            java.lang.String r5 = "\\b(?:(\\d{1,2}):)?(\\d{1,3}):([0-5][0-9])\\b"
            java.util.regex.Pattern r5 = java.util.regex.Pattern.compile(r5)     // Catch:{ Exception -> 0x01f2 }
            videoTimeUrlPattern = r5     // Catch:{ Exception -> 0x01f2 }
        L_0x0040:
            java.util.regex.Pattern r5 = videoTimeUrlPattern     // Catch:{ Exception -> 0x01f2 }
            java.util.regex.Matcher r5 = r5.matcher(r0)     // Catch:{ Exception -> 0x01f2 }
        L_0x0046:
            r6 = r0
            android.text.Spannable r6 = (android.text.Spannable) r6     // Catch:{ Exception -> 0x01f2 }
        L_0x0049:
            boolean r7 = r5.find()     // Catch:{ Exception -> 0x01f2 }
            if (r7 == 0) goto L_0x01f6
            int r7 = r5.start()     // Catch:{ Exception -> 0x01f2 }
            int r8 = r5.end()     // Catch:{ Exception -> 0x01f2 }
            r9 = 0
            r10 = 0
            r11 = 2
            if (r1 == r3) goto L_0x0144
            if (r1 != r2) goto L_0x0060
            goto L_0x0144
        L_0x0060:
            char r12 = r0.charAt(r7)     // Catch:{ Exception -> 0x01f2 }
            r13 = 47
            r14 = 35
            r15 = 64
            if (r1 == 0) goto L_0x007b
            if (r12 == r15) goto L_0x0072
            if (r12 == r14) goto L_0x0072
            int r7 = r7 + 1
        L_0x0072:
            char r12 = r0.charAt(r7)     // Catch:{ Exception -> 0x01f2 }
            if (r12 == r15) goto L_0x0087
            if (r12 == r14) goto L_0x0087
            goto L_0x0049
        L_0x007b:
            if (r12 == r15) goto L_0x0087
            if (r12 == r14) goto L_0x0087
            if (r12 == r13) goto L_0x0087
            r2 = 36
            if (r12 == r2) goto L_0x0087
            int r7 = r7 + 1
        L_0x0087:
            if (r1 != r4) goto L_0x00d0
            if (r12 != r15) goto L_0x00ad
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f2 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f2 }
            r2.<init>()     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r11 = "https://instagram.com/"
            r2.append(r11)     // Catch:{ Exception -> 0x01f2 }
            int r11 = r7 + 1
            java.lang.CharSequence r11 = r0.subSequence(r11, r8)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f2 }
            r2.append(r11)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f2 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x01f2 }
            goto L_0x0132
        L_0x00ad:
            if (r12 != r14) goto L_0x0132
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f2 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f2 }
            r2.<init>()     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r11 = "https://www.instagram.com/explore/tags/"
            r2.append(r11)     // Catch:{ Exception -> 0x01f2 }
            int r11 = r7 + 1
            java.lang.CharSequence r11 = r0.subSequence(r11, r8)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f2 }
            r2.append(r11)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f2 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x01f2 }
            goto L_0x0132
        L_0x00d0:
            if (r1 != r11) goto L_0x0118
            if (r12 != r15) goto L_0x00f5
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f2 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f2 }
            r2.<init>()     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r11 = "https://twitter.com/"
            r2.append(r11)     // Catch:{ Exception -> 0x01f2 }
            int r11 = r7 + 1
            java.lang.CharSequence r11 = r0.subSequence(r11, r8)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f2 }
            r2.append(r11)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f2 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x01f2 }
            goto L_0x0132
        L_0x00f5:
            if (r12 != r14) goto L_0x0132
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f2 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f2 }
            r2.<init>()     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r11 = "https://twitter.com/hashtag/"
            r2.append(r11)     // Catch:{ Exception -> 0x01f2 }
            int r11 = r7 + 1
            java.lang.CharSequence r11 = r0.subSequence(r11, r8)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f2 }
            r2.append(r11)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f2 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x01f2 }
            goto L_0x0132
        L_0x0118:
            char r2 = r0.charAt(r7)     // Catch:{ Exception -> 0x01f2 }
            if (r2 != r13) goto L_0x0136
            if (r18 == 0) goto L_0x0132
            org.telegram.ui.Components.URLSpanBotCommand r9 = new org.telegram.ui.Components.URLSpanBotCommand     // Catch:{ Exception -> 0x01f2 }
            java.lang.CharSequence r2 = r0.subSequence(r7, r8)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f2 }
            if (r16 == 0) goto L_0x012e
            r11 = 1
            goto L_0x012f
        L_0x012e:
            r11 = 0
        L_0x012f:
            r9.<init>(r2, r11)     // Catch:{ Exception -> 0x01f2 }
        L_0x0132:
            r2 = r20
            goto L_0x01d7
        L_0x0136:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f2 }
            java.lang.CharSequence r2 = r0.subSequence(r7, r8)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x01f2 }
            r9.<init>(r2)     // Catch:{ Exception -> 0x01f2 }
            goto L_0x0132
        L_0x0144:
            java.lang.Class<android.text.style.URLSpan> r2 = android.text.style.URLSpan.class
            java.lang.Object[] r2 = r6.getSpans(r7, r8, r2)     // Catch:{ Exception -> 0x01f2 }
            android.text.style.URLSpan[] r2 = (android.text.style.URLSpan[]) r2     // Catch:{ Exception -> 0x01f2 }
            if (r2 == 0) goto L_0x0154
            int r2 = r2.length     // Catch:{ Exception -> 0x01f2 }
            if (r2 <= 0) goto L_0x0154
        L_0x0151:
            r2 = 4
            goto L_0x0049
        L_0x0154:
            r5.groupCount()     // Catch:{ Exception -> 0x01f2 }
            int r2 = r5.start(r4)     // Catch:{ Exception -> 0x01f2 }
            int r9 = r5.end(r4)     // Catch:{ Exception -> 0x01f2 }
            int r12 = r5.start(r11)     // Catch:{ Exception -> 0x01f2 }
            int r11 = r5.end(r11)     // Catch:{ Exception -> 0x01f2 }
            int r13 = r5.start(r3)     // Catch:{ Exception -> 0x01f2 }
            int r14 = r5.end(r3)     // Catch:{ Exception -> 0x01f2 }
            java.lang.CharSequence r11 = r0.subSequence(r12, r11)     // Catch:{ Exception -> 0x01f2 }
            java.lang.Integer r11 = org.telegram.messenger.Utilities.parseInt(r11)     // Catch:{ Exception -> 0x01f2 }
            int r11 = r11.intValue()     // Catch:{ Exception -> 0x01f2 }
            java.lang.CharSequence r12 = r0.subSequence(r13, r14)     // Catch:{ Exception -> 0x01f2 }
            java.lang.Integer r12 = org.telegram.messenger.Utilities.parseInt(r12)     // Catch:{ Exception -> 0x01f2 }
            int r12 = r12.intValue()     // Catch:{ Exception -> 0x01f2 }
            if (r2 < 0) goto L_0x0198
            if (r9 < 0) goto L_0x0198
            java.lang.CharSequence r2 = r0.subSequence(r2, r9)     // Catch:{ Exception -> 0x01f2 }
            java.lang.Integer r2 = org.telegram.messenger.Utilities.parseInt(r2)     // Catch:{ Exception -> 0x01f2 }
            int r2 = r2.intValue()     // Catch:{ Exception -> 0x01f2 }
            goto L_0x0199
        L_0x0198:
            r2 = -1
        L_0x0199:
            int r11 = r11 * 60
            int r12 = r12 + r11
            if (r2 <= 0) goto L_0x01a3
            int r2 = r2 * 60
            int r2 = r2 * 60
            int r12 = r12 + r2
        L_0x01a3:
            r2 = r20
            if (r12 <= r2) goto L_0x01a8
            goto L_0x0151
        L_0x01a8:
            if (r1 != r3) goto L_0x01c1
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f2 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f2 }
            r11.<init>()     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r13 = "video?"
            r11.append(r13)     // Catch:{ Exception -> 0x01f2 }
            r11.append(r12)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f2 }
            r9.<init>(r11)     // Catch:{ Exception -> 0x01f2 }
            goto L_0x01d7
        L_0x01c1:
            org.telegram.ui.Components.URLSpanNoUnderline r9 = new org.telegram.ui.Components.URLSpanNoUnderline     // Catch:{ Exception -> 0x01f2 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f2 }
            r11.<init>()     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r13 = "audio?"
            r11.append(r13)     // Catch:{ Exception -> 0x01f2 }
            r11.append(r12)     // Catch:{ Exception -> 0x01f2 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01f2 }
            r9.<init>(r11)     // Catch:{ Exception -> 0x01f2 }
        L_0x01d7:
            if (r9 == 0) goto L_0x0151
            if (r21 == 0) goto L_0x01ed
            java.lang.Class<android.text.style.ClickableSpan> r11 = android.text.style.ClickableSpan.class
            java.lang.Object[] r11 = r6.getSpans(r7, r8, r11)     // Catch:{ Exception -> 0x01f2 }
            android.text.style.ClickableSpan[] r11 = (android.text.style.ClickableSpan[]) r11     // Catch:{ Exception -> 0x01f2 }
            if (r11 == 0) goto L_0x01ed
            int r12 = r11.length     // Catch:{ Exception -> 0x01f2 }
            if (r12 <= 0) goto L_0x01ed
            r11 = r11[r10]     // Catch:{ Exception -> 0x01f2 }
            r6.removeSpan(r11)     // Catch:{ Exception -> 0x01f2 }
        L_0x01ed:
            r6.setSpan(r9, r7, r8, r10)     // Catch:{ Exception -> 0x01f2 }
            goto L_0x0151
        L_0x01f2:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x01f6:
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
            if (r4 == 0) goto L_0x0016
            java.util.ArrayList<org.telegram.tgnet.TLRPC$PhotoSize> r0 = r5.photoThumbs
            if (r0 == 0) goto L_0x0016
            boolean r0 = r0.isEmpty()
            if (r0 != 0) goto L_0x0016
            r0 = 1
            goto L_0x0017
        L_0x0016:
            r0 = 0
        L_0x0017:
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
        if ((charSequence instanceof Spannable) && containsUrls(charSequence)) {
            if (charSequence.length() < 1000) {
                try {
                    AndroidUtilities.addLinks((Spannable) charSequence, 5);
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else {
                try {
                    AndroidUtilities.addLinks((Spannable) charSequence, 1);
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
            return addEntitiesToText(charSequence, arrayList, isOutOwner(), this.type, true, z, z2);
        }
        return addEntitiesToText(charSequence, this.messageOwner.entities, isOutOwner(), this.type, true, z, z2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:112:0x0166  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0201  */
    /* JADX WARNING: Removed duplicated region for block: B:191:0x0206 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean addEntitiesToText(java.lang.CharSequence r16, java.util.ArrayList<org.telegram.tgnet.TLRPC$MessageEntity> r17, boolean r18, int r19, boolean r20, boolean r21, boolean r22) {
        /*
            r0 = r16
            boolean r1 = r0 instanceof android.text.Spannable
            r2 = 0
            if (r1 != 0) goto L_0x0008
            return r2
        L_0x0008:
            r1 = r0
            android.text.Spannable r1 = (android.text.Spannable) r1
            int r3 = r16.length()
            java.lang.Class<android.text.style.URLSpan> r4 = android.text.style.URLSpan.class
            java.lang.Object[] r3 = r1.getSpans(r2, r3, r4)
            android.text.style.URLSpan[] r3 = (android.text.style.URLSpan[]) r3
            if (r3 == 0) goto L_0x001e
            int r5 = r3.length
            if (r5 <= 0) goto L_0x001e
            r5 = 1
            goto L_0x001f
        L_0x001e:
            r5 = 0
        L_0x001f:
            boolean r6 = r17.isEmpty()
            if (r6 == 0) goto L_0x0026
            return r5
        L_0x0026:
            if (r21 == 0) goto L_0x002a
            r7 = 2
            goto L_0x002f
        L_0x002a:
            if (r18 == 0) goto L_0x002e
            r7 = 1
            goto L_0x002f
        L_0x002e:
            r7 = 0
        L_0x002f:
            java.util.ArrayList r8 = new java.util.ArrayList
            r8.<init>()
            java.util.ArrayList r9 = new java.util.ArrayList
            r10 = r17
            r9.<init>(r10)
            org.telegram.messenger.-$$Lambda$MessageObject$TayxkIvYR-DxCFsN9JUXpTKGe7s r10 = org.telegram.messenger.$$Lambda$MessageObject$TayxkIvYRDxCFsN9JUXpTKGe7s.INSTANCE
            java.util.Collections.sort(r9, r10)
            int r10 = r9.size()
            r11 = 0
        L_0x0045:
            r13 = 0
            if (r11 >= r10) goto L_0x020b
            java.lang.Object r14 = r9.get(r11)
            org.telegram.tgnet.TLRPC$MessageEntity r14 = (org.telegram.tgnet.TLRPC$MessageEntity) r14
            int r15 = r14.length
            if (r15 <= 0) goto L_0x0205
            int r15 = r14.offset
            if (r15 < 0) goto L_0x0205
            int r2 = r16.length()
            if (r15 < r2) goto L_0x005e
            goto L_0x0205
        L_0x005e:
            int r2 = r14.offset
            int r15 = r14.length
            int r2 = r2 + r15
            int r15 = r16.length()
            if (r2 <= r15) goto L_0x0072
            int r2 = r16.length()
            int r15 = r14.offset
            int r2 = r2 - r15
            r14.length = r2
        L_0x0072:
            if (r22 == 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBold
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityItalic
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityStrike
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUnderline
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCode
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPre
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMentionName
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            if (r2 != 0) goto L_0x009c
            boolean r2 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r2 == 0) goto L_0x00d2
        L_0x009c:
            if (r3 == 0) goto L_0x00d2
            int r2 = r3.length
            if (r2 <= 0) goto L_0x00d2
            r2 = 0
        L_0x00a2:
            int r15 = r3.length
            if (r2 >= r15) goto L_0x00d2
            r15 = r3[r2]
            if (r15 != 0) goto L_0x00aa
            goto L_0x00cf
        L_0x00aa:
            r15 = r3[r2]
            int r15 = r1.getSpanStart(r15)
            r12 = r3[r2]
            int r12 = r1.getSpanEnd(r12)
            int r6 = r14.offset
            if (r6 > r15) goto L_0x00bf
            int r4 = r14.length
            int r6 = r6 + r4
            if (r6 >= r15) goto L_0x00c8
        L_0x00bf:
            int r4 = r14.offset
            if (r4 > r12) goto L_0x00cf
            int r6 = r14.length
            int r4 = r4 + r6
            if (r4 < r12) goto L_0x00cf
        L_0x00c8:
            r4 = r3[r2]
            r1.removeSpan(r4)
            r3[r2] = r13
        L_0x00cf:
            int r2 = r2 + 1
            goto L_0x00a2
        L_0x00d2:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r2 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r2.<init>()
            int r4 = r14.offset
            r2.start = r4
            int r6 = r14.length
            int r4 = r4 + r6
            r2.end = r4
            boolean r4 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityStrike
            if (r4 == 0) goto L_0x00eb
            r4 = 8
            r2.flags = r4
        L_0x00e8:
            r4 = 2
            goto L_0x015f
        L_0x00eb:
            boolean r4 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUnderline
            if (r4 == 0) goto L_0x00f4
            r4 = 16
            r2.flags = r4
            goto L_0x00e8
        L_0x00f4:
            boolean r4 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBlockquote
            if (r4 == 0) goto L_0x00fd
            r4 = 32
            r2.flags = r4
            goto L_0x00e8
        L_0x00fd:
            boolean r4 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBold
            if (r4 == 0) goto L_0x0105
            r4 = 1
            r2.flags = r4
            goto L_0x00e8
        L_0x0105:
            boolean r4 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityItalic
            if (r4 == 0) goto L_0x010d
            r4 = 2
            r2.flags = r4
            goto L_0x015f
        L_0x010d:
            r4 = 2
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCode
            if (r6 != 0) goto L_0x015c
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPre
            if (r6 == 0) goto L_0x0117
            goto L_0x015c
        L_0x0117:
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMentionName
            r12 = 64
            if (r6 == 0) goto L_0x0126
            if (r20 != 0) goto L_0x0121
            goto L_0x0205
        L_0x0121:
            r2.flags = r12
            r2.urlEntity = r14
            goto L_0x015f
        L_0x0126:
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            if (r6 == 0) goto L_0x0133
            if (r20 != 0) goto L_0x012e
            goto L_0x0205
        L_0x012e:
            r2.flags = r12
            r2.urlEntity = r14
            goto L_0x015f
        L_0x0133:
            if (r22 == 0) goto L_0x013b
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r6 != 0) goto L_0x013b
            goto L_0x0205
        L_0x013b:
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            if (r6 != 0) goto L_0x0143
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r6 == 0) goto L_0x014d
        L_0x0143:
            java.lang.String r6 = r14.url
            boolean r6 = org.telegram.messenger.browser.Browser.isPassportUrl(r6)
            if (r6 == 0) goto L_0x014d
            goto L_0x0205
        L_0x014d:
            boolean r6 = r14 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMention
            if (r6 == 0) goto L_0x0155
            if (r20 != 0) goto L_0x0155
            goto L_0x0205
        L_0x0155:
            r6 = 128(0x80, float:1.794E-43)
            r2.flags = r6
            r2.urlEntity = r14
            goto L_0x015f
        L_0x015c:
            r6 = 4
            r2.flags = r6
        L_0x015f:
            int r6 = r8.size()
            r12 = 0
        L_0x0164:
            if (r12 >= r6) goto L_0x01fa
            java.lang.Object r13 = r8.get(r12)
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r13 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r13
            int r14 = r2.start
            int r15 = r13.start
            if (r14 <= r15) goto L_0x01b8
            int r15 = r13.end
            if (r14 < r15) goto L_0x0177
            goto L_0x01bc
        L_0x0177:
            int r14 = r2.end
            if (r14 >= r15) goto L_0x019a
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r14.<init>(r2)
            r14.merge(r13)
            int r12 = r12 + 1
            int r6 = r6 + 1
            r8.add(r12, r14)
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r14.<init>(r13)
            int r15 = r2.end
            r14.start = r15
            r15 = 1
            int r12 = r12 + r15
            int r6 = r6 + r15
            r8.add(r12, r14)
            goto L_0x01af
        L_0x019a:
            if (r14 < r15) goto L_0x01af
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r14.<init>(r2)
            r14.merge(r13)
            int r15 = r13.end
            r14.end = r15
            int r12 = r12 + 1
            int r6 = r6 + 1
            r8.add(r12, r14)
        L_0x01af:
            int r14 = r2.start
            int r15 = r13.end
            r2.start = r15
            r13.end = r14
            goto L_0x01bc
        L_0x01b8:
            int r14 = r2.end
            if (r15 < r14) goto L_0x01be
        L_0x01bc:
            r4 = 1
            goto L_0x01f6
        L_0x01be:
            int r4 = r13.end
            if (r14 != r4) goto L_0x01c6
            r13.merge(r2)
            goto L_0x01f3
        L_0x01c6:
            if (r14 >= r4) goto L_0x01e0
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r4.<init>(r13)
            r4.merge(r2)
            int r14 = r2.end
            r4.end = r14
            int r12 = r12 + 1
            int r6 = r6 + 1
            r8.add(r12, r4)
            int r4 = r2.end
            r13.start = r4
            goto L_0x01f3
        L_0x01e0:
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun
            r4.<init>(r2)
            int r14 = r13.end
            r4.start = r14
            int r12 = r12 + 1
            int r6 = r6 + 1
            r8.add(r12, r4)
            r13.merge(r2)
        L_0x01f3:
            r2.end = r15
            goto L_0x01bc
        L_0x01f6:
            int r12 = r12 + r4
            r4 = 2
            goto L_0x0164
        L_0x01fa:
            r4 = 1
            int r6 = r2.start
            int r12 = r2.end
            if (r6 >= r12) goto L_0x0206
            r8.add(r2)
            goto L_0x0206
        L_0x0205:
            r4 = 1
        L_0x0206:
            int r11 = r11 + 1
            r2 = 0
            goto L_0x0045
        L_0x020b:
            r4 = 1
            int r2 = r8.size()
            r3 = 0
        L_0x0211:
            if (r3 >= r2) goto L_0x03b2
            java.lang.Object r6 = r8.get(r3)
            org.telegram.ui.Components.TextStyleSpan$TextStyleRun r6 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r6
            org.telegram.tgnet.TLRPC$MessageEntity r9 = r6.urlEntity
            if (r9 == 0) goto L_0x0227
            int r10 = r9.offset
            int r9 = r9.length
            int r9 = r9 + r10
            java.lang.String r9 = android.text.TextUtils.substring(r0, r10, r9)
            goto L_0x0228
        L_0x0227:
            r9 = r13
        L_0x0228:
            org.telegram.tgnet.TLRPC$MessageEntity r10 = r6.urlEntity
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBotCommand
            r12 = 33
            if (r11 == 0) goto L_0x023e
            org.telegram.ui.Components.URLSpanBotCommand r10 = new org.telegram.ui.Components.URLSpanBotCommand
            r10.<init>(r9, r7, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r10, r9, r6, r12)
            goto L_0x02cb
        L_0x023e:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityHashtag
            if (r11 != 0) goto L_0x03a1
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMention
            if (r11 != 0) goto L_0x03a1
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityCashtag
            if (r11 == 0) goto L_0x024c
            goto L_0x03a1
        L_0x024c:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityEmail
            if (r11 == 0) goto L_0x026e
            org.telegram.ui.Components.URLSpanReplacement r10 = new org.telegram.ui.Components.URLSpanReplacement
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r14 = "mailto:"
            r11.append(r14)
            r11.append(r9)
            java.lang.String r9 = r11.toString()
            r10.<init>(r9, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r10, r9, r6, r12)
            goto L_0x02cb
        L_0x026e:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityUrl
            if (r11 == 0) goto L_0x02a9
            java.lang.String r5 = r9.toLowerCase()
            java.lang.String r10 = "://"
            boolean r5 = r5.contains(r10)
            if (r5 != 0) goto L_0x029c
            org.telegram.ui.Components.URLSpanBrowser r5 = new org.telegram.ui.Components.URLSpanBrowser
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "http://"
            r10.append(r11)
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            r5.<init>(r9, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r5, r9, r6, r12)
            goto L_0x02ca
        L_0x029c:
            org.telegram.ui.Components.URLSpanBrowser r5 = new org.telegram.ui.Components.URLSpanBrowser
            r5.<init>(r9, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r5, r9, r6, r12)
            goto L_0x02ca
        L_0x02a9:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityBankCard
            if (r11 == 0) goto L_0x02ce
            org.telegram.ui.Components.URLSpanNoUnderline r5 = new org.telegram.ui.Components.URLSpanNoUnderline
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "card:"
            r10.append(r11)
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            r5.<init>(r9, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r5, r9, r6, r12)
        L_0x02ca:
            r5 = 1
        L_0x02cb:
            r10 = 4
            goto L_0x03ae
        L_0x02ce:
            boolean r11 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityPhone
            if (r11 == 0) goto L_0x030b
            java.lang.String r5 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r9)
            java.lang.String r10 = "+"
            boolean r9 = r9.startsWith(r10)
            if (r9 == 0) goto L_0x02ed
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            r9.append(r10)
            r9.append(r5)
            java.lang.String r5 = r9.toString()
        L_0x02ed:
            org.telegram.ui.Components.URLSpanBrowser r9 = new org.telegram.ui.Components.URLSpanBrowser
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "tel:"
            r10.append(r11)
            r10.append(r5)
            java.lang.String r5 = r10.toString()
            r9.<init>(r5, r6)
            int r5 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r5, r6, r12)
            goto L_0x02ca
        L_0x030b:
            boolean r9 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityTextUrl
            if (r9 == 0) goto L_0x0320
            org.telegram.ui.Components.URLSpanReplacement r9 = new org.telegram.ui.Components.URLSpanReplacement
            org.telegram.tgnet.TLRPC$MessageEntity r10 = r6.urlEntity
            java.lang.String r10 = r10.url
            r9.<init>(r10, r6)
            int r10 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r10, r6, r12)
            goto L_0x02cb
        L_0x0320:
            boolean r9 = r10 instanceof org.telegram.tgnet.TLRPC$TL_messageEntityMentionName
            java.lang.String r11 = ""
            if (r9 == 0) goto L_0x0348
            org.telegram.ui.Components.URLSpanUserMention r9 = new org.telegram.ui.Components.URLSpanUserMention
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r11)
            org.telegram.tgnet.TLRPC$MessageEntity r11 = r6.urlEntity
            org.telegram.tgnet.TLRPC$TL_messageEntityMentionName r11 = (org.telegram.tgnet.TLRPC$TL_messageEntityMentionName) r11
            int r11 = r11.user_id
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r9.<init>(r10, r7, r6)
            int r10 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r10, r6, r12)
            goto L_0x02cb
        L_0x0348:
            boolean r9 = r10 instanceof org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName
            if (r9 == 0) goto L_0x0371
            org.telegram.ui.Components.URLSpanUserMention r9 = new org.telegram.ui.Components.URLSpanUserMention
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r11)
            org.telegram.tgnet.TLRPC$MessageEntity r11 = r6.urlEntity
            org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName r11 = (org.telegram.tgnet.TLRPC$TL_inputMessageEntityMentionName) r11
            org.telegram.tgnet.TLRPC$InputUser r11 = r11.user_id
            int r11 = r11.user_id
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r9.<init>(r10, r7, r6)
            int r10 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r10, r6, r12)
            goto L_0x02cb
        L_0x0371:
            int r9 = r6.flags
            r10 = 4
            r9 = r9 & r10
            if (r9 == 0) goto L_0x0394
            org.telegram.ui.Components.URLSpanMono r9 = new org.telegram.ui.Components.URLSpanMono
            int r11 = r6.start
            int r14 = r6.end
            r17 = r9
            r18 = r1
            r19 = r11
            r20 = r14
            r21 = r7
            r22 = r6
            r17.<init>(r18, r19, r20, r21, r22)
            int r11 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r11, r6, r12)
            goto L_0x03ae
        L_0x0394:
            org.telegram.ui.Components.TextStyleSpan r9 = new org.telegram.ui.Components.TextStyleSpan
            r9.<init>(r6)
            int r11 = r6.start
            int r6 = r6.end
            r1.setSpan(r9, r11, r6, r12)
            goto L_0x03ae
        L_0x03a1:
            r10 = 4
            org.telegram.ui.Components.URLSpanNoUnderline r11 = new org.telegram.ui.Components.URLSpanNoUnderline
            r11.<init>(r9, r6)
            int r9 = r6.start
            int r6 = r6.end
            r1.setSpan(r11, r9, r6, r12)
        L_0x03ae:
            int r3 = r3 + 1
            goto L_0x0211
        L_0x03b2:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addEntitiesToText(java.lang.CharSequence, java.util.ArrayList, boolean, int, boolean, boolean, boolean):boolean");
    }

    static /* synthetic */ int lambda$addEntitiesToText$0(TLRPC$MessageEntity tLRPC$MessageEntity, TLRPC$MessageEntity tLRPC$MessageEntity2) {
        int i = tLRPC$MessageEntity.offset;
        int i2 = tLRPC$MessageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public boolean needDrawShareButton() {
        int i;
        TLRPC$Chat chat;
        String str;
        if (this.scheduled || this.eventId != 0) {
            return false;
        }
        if (this.messageOwner.fwd_from != null && !isOutOwner() && this.messageOwner.fwd_from.saved_from_peer != null && getDialogId() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId())) {
            return true;
        }
        int i2 = this.type;
        if (!(i2 == 13 || i2 == 15)) {
            TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
            if (tLRPC$MessageFwdHeader != null && tLRPC$MessageFwdHeader.channel_id != 0 && !isOutOwner()) {
                return true;
            }
            if (isFromUser()) {
                TLRPC$MessageMedia tLRPC$MessageMedia = this.messageOwner.media;
                if ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaEmpty) || tLRPC$MessageMedia == null || ((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) && !(tLRPC$MessageMedia.webpage instanceof TLRPC$TL_webPage))) {
                    return false;
                }
                TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                if (user != null && user.bot) {
                    return true;
                }
                if (!isOut()) {
                    TLRPC$MessageMedia tLRPC$MessageMedia2 = this.messageOwner.media;
                    if ((tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaGame) || (tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaInvoice)) {
                        return true;
                    }
                    if (!isMegagroup() || (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id))) == null || (str = chat.username) == null || str.length() <= 0) {
                        return false;
                    }
                    TLRPC$MessageMedia tLRPC$MessageMedia3 = this.messageOwner.media;
                    if ((tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaContact) || (tLRPC$MessageMedia3 instanceof TLRPC$TL_messageMediaGeo)) {
                        return false;
                    }
                    return true;
                }
            } else {
                TLRPC$Message tLRPC$Message = this.messageOwner;
                if (tLRPC$Message.from_id < 0 || tLRPC$Message.post) {
                    TLRPC$Message tLRPC$Message2 = this.messageOwner;
                    if (tLRPC$Message2.to_id.channel_id == 0 || ((tLRPC$Message2.via_bot_id != 0 || tLRPC$Message2.reply_to_msg_id != 0) && ((i = this.type) == 13 || i == 15))) {
                        return false;
                    }
                    return true;
                }
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
            this.generatedWithMinSize = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x;
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
        int dp = this.generatedWithMinSize - AndroidUtilities.dp((!needDrawAvatarInternal() || isOutOwner()) ? 80.0f : 132.0f);
        if (needDrawShareButton() && !isOutOwner()) {
            dp -= AndroidUtilities.dp(10.0f);
        }
        int i2 = dp;
        return this.messageOwner.media instanceof TLRPC$TL_messageMediaGame ? i2 - AndroidUtilities.dp(10.0f) : i2;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0077, code lost:
        if ((r0.media instanceof org.telegram.tgnet.TLRPC$TL_messageMediaUnsupported) == false) goto L_0x007b;
     */
    /* JADX WARNING: Removed duplicated region for block: B:115:0x029d A[Catch:{ Exception -> 0x0427 }] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x02c0  */
    /* JADX WARNING: Removed duplicated region for block: B:124:0x02e3  */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0321  */
    /* JADX WARNING: Removed duplicated region for block: B:150:0x0326  */
    /* JADX WARNING: Removed duplicated region for block: B:153:0x0332  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x03e5  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00b4  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x00f4  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x0119  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x0122 A[Catch:{ Exception -> 0x045f }] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0141 A[Catch:{ Exception -> 0x045f }] */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x0162  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x0164  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x0176  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void generateLayout(org.telegram.tgnet.TLRPC$User r31) {
        /*
            r30 = this;
            r1 = r30
            int r0 = r1.type
            if (r0 != 0) goto L_0x0463
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            if (r0 == 0) goto L_0x0463
            java.lang.CharSequence r0 = r1.messageText
            boolean r0 = android.text.TextUtils.isEmpty(r0)
            if (r0 == 0) goto L_0x0016
            goto L_0x0463
        L_0x0016:
            r30.generateLinkDescription()
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
            boolean r0 = r30.isOut()
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
            r3 = 1
            goto L_0x007c
        L_0x007b:
            r3 = 0
        L_0x007c:
            if (r3 == 0) goto L_0x0088
            boolean r0 = r30.isOutOwner()
            java.lang.CharSequence r5 = r1.messageText
            addLinks(r0, r5, r4, r4)
            goto L_0x00a3
        L_0x0088:
            java.lang.CharSequence r0 = r1.messageText
            boolean r5 = r0 instanceof android.text.Spannable
            if (r5 == 0) goto L_0x00a3
            int r0 = r0.length()
            r5 = 1000(0x3e8, float:1.401E-42)
            if (r0 >= r5) goto L_0x00a3
            java.lang.CharSequence r0 = r1.messageText     // Catch:{ all -> 0x009f }
            android.text.Spannable r0 = (android.text.Spannable) r0     // Catch:{ all -> 0x009f }
            r5 = 4
            org.telegram.messenger.AndroidUtilities.addLinks(r0, r5)     // Catch:{ all -> 0x009f }
            goto L_0x00a3
        L_0x009f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x00a3:
            boolean r0 = r30.isYouTubeVideo()
            if (r0 != 0) goto L_0x00f4
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            if (r0 == 0) goto L_0x00b4
            boolean r0 = r0.isYouTubeVideo()
            if (r0 == 0) goto L_0x00b4
            goto L_0x00f4
        L_0x00b4:
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            if (r0 == 0) goto L_0x0104
            boolean r0 = r0.isVideo()
            if (r0 == 0) goto L_0x00d1
            boolean r5 = r30.isOutOwner()
            java.lang.CharSequence r6 = r1.messageText
            r7 = 0
            r8 = 3
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            int r9 = r0.getDuration()
            r10 = 0
            addUrlsByPattern(r5, r6, r7, r8, r9, r10)
            goto L_0x0104
        L_0x00d1:
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            boolean r0 = r0.isMusic()
            if (r0 != 0) goto L_0x00e1
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            boolean r0 = r0.isVoice()
            if (r0 == 0) goto L_0x0104
        L_0x00e1:
            boolean r5 = r30.isOutOwner()
            java.lang.CharSequence r6 = r1.messageText
            r7 = 0
            r8 = 4
            org.telegram.messenger.MessageObject r0 = r1.replyMessageObject
            int r9 = r0.getDuration()
            r10 = 0
            addUrlsByPattern(r5, r6, r7, r8, r9, r10)
            goto L_0x0104
        L_0x00f4:
            boolean r11 = r30.isOutOwner()
            java.lang.CharSequence r12 = r1.messageText
            r13 = 0
            r14 = 3
            r15 = 2147483647(0x7fffffff, float:NaN)
            r16 = 0
            addUrlsByPattern(r11, r12, r13, r14, r15, r16)
        L_0x0104:
            java.lang.CharSequence r0 = r1.messageText
            boolean r3 = r1.addEntitiesToText(r0, r3)
            int r15 = r30.getMaxMessageTextWidth()
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            boolean r0 = r0 instanceof org.telegram.tgnet.TLRPC$TL_messageMediaGame
            if (r0 == 0) goto L_0x0119
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint
            goto L_0x011b
        L_0x0119:
            android.text.TextPaint r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint
        L_0x011b:
            r14 = r0
            int r0 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x045f }
            r13 = 24
            if (r0 < r13) goto L_0x0141
            java.lang.CharSequence r0 = r1.messageText     // Catch:{ Exception -> 0x045f }
            java.lang.CharSequence r5 = r1.messageText     // Catch:{ Exception -> 0x045f }
            int r5 = r5.length()     // Catch:{ Exception -> 0x045f }
            android.text.StaticLayout$Builder r0 = android.text.StaticLayout.Builder.obtain(r0, r2, r5, r14, r15)     // Catch:{ Exception -> 0x045f }
            android.text.StaticLayout$Builder r0 = r0.setBreakStrategy(r4)     // Catch:{ Exception -> 0x045f }
            android.text.StaticLayout$Builder r0 = r0.setHyphenationFrequency(r2)     // Catch:{ Exception -> 0x045f }
            android.text.Layout$Alignment r5 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x045f }
            android.text.StaticLayout$Builder r0 = r0.setAlignment(r5)     // Catch:{ Exception -> 0x045f }
            android.text.StaticLayout r0 = r0.build()     // Catch:{ Exception -> 0x045f }
            goto L_0x0151
        L_0x0141:
            android.text.StaticLayout r0 = new android.text.StaticLayout     // Catch:{ Exception -> 0x045f }
            java.lang.CharSequence r6 = r1.messageText     // Catch:{ Exception -> 0x045f }
            android.text.Layout$Alignment r9 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x045f }
            r10 = 1065353216(0x3var_, float:1.0)
            r11 = 0
            r12 = 0
            r5 = r0
            r7 = r14
            r8 = r15
            r5.<init>(r6, r7, r8, r9, r10, r11, r12)     // Catch:{ Exception -> 0x045f }
        L_0x0151:
            r12 = r0
            int r0 = r12.getHeight()
            r1.textHeight = r0
            int r0 = r12.getLineCount()
            r1.linesCount = r0
            int r5 = android.os.Build.VERSION.SDK_INT
            if (r5 < r13) goto L_0x0164
            r11 = 1
            goto L_0x016f
        L_0x0164:
            float r0 = (float) r0
            r5 = 1092616192(0x41200000, float:10.0)
            float r0 = r0 / r5
            double r5 = (double) r0
            double r5 = java.lang.Math.ceil(r5)
            int r0 = (int) r5
            r11 = r0
        L_0x016f:
            r10 = 0
            r8 = 0
            r9 = 0
            r16 = 0
        L_0x0174:
            if (r9 >= r11) goto L_0x045e
            int r0 = android.os.Build.VERSION.SDK_INT
            if (r0 < r13) goto L_0x017d
            int r0 = r1.linesCount
            goto L_0x0186
        L_0x017d:
            r0 = 10
            int r5 = r1.linesCount
            int r5 = r5 - r8
            int r0 = java.lang.Math.min(r0, r5)
        L_0x0186:
            org.telegram.messenger.MessageObject$TextLayoutBlock r7 = new org.telegram.messenger.MessageObject$TextLayoutBlock
            r7.<init>()
            r6 = 2
            if (r11 != r4) goto L_0x0202
            r7.textLayout = r12
            r7.textYOffset = r10
            r7.charactersOffset = r2
            java.lang.CharSequence r5 = r12.getText()
            int r5 = r5.length()
            r7.charactersEnd = r5
            int r5 = r1.emojiOnlyCount
            if (r5 == 0) goto L_0x01f0
            if (r5 == r4) goto L_0x01d9
            if (r5 == r6) goto L_0x01c2
            r6 = 3
            if (r5 == r6) goto L_0x01aa
            goto L_0x01f0
        L_0x01aa:
            int r5 = r1.textHeight
            r6 = 1082549862(0x40866666, float:4.2)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r17
            r1.textHeight = r5
            float r5 = r7.textYOffset
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 - r6
            r7.textYOffset = r5
            goto L_0x01f0
        L_0x01c2:
            int r5 = r1.textHeight
            r6 = 1083179008(0x40900000, float:4.5)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r17
            r1.textHeight = r5
            float r5 = r7.textYOffset
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 - r6
            r7.textYOffset = r5
            goto L_0x01f0
        L_0x01d9:
            int r5 = r1.textHeight
            r6 = 1084856730(0x40a9999a, float:5.3)
            int r17 = org.telegram.messenger.AndroidUtilities.dp(r6)
            int r5 = r5 - r17
            r1.textHeight = r5
            float r5 = r7.textYOffset
            int r6 = org.telegram.messenger.AndroidUtilities.dp(r6)
            float r6 = (float) r6
            float r5 = r5 - r6
            r7.textYOffset = r5
        L_0x01f0:
            int r5 = r1.textHeight
            r7.height = r5
            r5 = r7
            r2 = r8
            r4 = r9
            r8 = r11
            r6 = r12
            r18 = r14
            r17 = 24
            r25 = 2
            r9 = r0
            goto L_0x02e6
        L_0x0202:
            int r6 = r12.getLineStart(r8)
            int r5 = r8 + r0
            int r5 = r5 - r4
            int r5 = r12.getLineEnd(r5)
            if (r5 >= r6) goto L_0x021f
            r20 = r3
            r21 = r8
            r4 = r9
            r8 = r11
            r28 = r12
            r18 = r14
            r2 = 0
            r10 = 1
            r17 = 24
            goto L_0x044b
        L_0x021f:
            r7.charactersOffset = r6
            r7.charactersEnd = r5
            if (r3 == 0) goto L_0x0258
            int r10 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x0439 }
            if (r10 < r13) goto L_0x0258
            java.lang.CharSequence r10 = r1.messageText     // Catch:{ Exception -> 0x0439 }
            r18 = 1073741824(0x40000000, float:2.0)
            int r18 = org.telegram.messenger.AndroidUtilities.dp(r18)     // Catch:{ Exception -> 0x0439 }
            int r13 = r15 + r18
            android.text.StaticLayout$Builder r5 = android.text.StaticLayout.Builder.obtain(r10, r6, r5, r14, r13)     // Catch:{ Exception -> 0x0439 }
            android.text.StaticLayout$Builder r5 = r5.setBreakStrategy(r4)     // Catch:{ Exception -> 0x0439 }
            android.text.StaticLayout$Builder r5 = r5.setHyphenationFrequency(r2)     // Catch:{ Exception -> 0x0439 }
            android.text.Layout$Alignment r6 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0439 }
            android.text.StaticLayout$Builder r5 = r5.setAlignment(r6)     // Catch:{ Exception -> 0x0439 }
            android.text.StaticLayout r5 = r5.build()     // Catch:{ Exception -> 0x0439 }
            r7.textLayout = r5     // Catch:{ Exception -> 0x0439 }
            r5 = r7
            r2 = r8
            r4 = r9
            r27 = r11
            r6 = r12
            r18 = r14
            r17 = 24
            r25 = 2
            goto L_0x0294
        L_0x0258:
            android.text.StaticLayout r13 = new android.text.StaticLayout     // Catch:{ Exception -> 0x0439 }
            java.lang.CharSequence r10 = r1.messageText     // Catch:{ Exception -> 0x0439 }
            android.text.Layout$Alignment r18 = android.text.Layout.Alignment.ALIGN_NORMAL     // Catch:{ Exception -> 0x0439 }
            r20 = 1065353216(0x3var_, float:1.0)
            r21 = 0
            r22 = 0
            r23 = r5
            r5 = r13
            r24 = r6
            r25 = 2
            r6 = r10
            r10 = r7
            r7 = r24
            r2 = r8
            r8 = r23
            r4 = r9
            r9 = r14
            r26 = r10
            r10 = r15
            r27 = r11
            r11 = r18
            r28 = r12
            r12 = r20
            r29 = r13
            r17 = 24
            r13 = r21
            r18 = r14
            r14 = r22
            r5.<init>(r6, r7, r8, r9, r10, r11, r12, r13, r14)     // Catch:{ Exception -> 0x042f }
            r5 = r26
            r6 = r29
            r5.textLayout = r6     // Catch:{ Exception -> 0x042f }
            r6 = r28
        L_0x0294:
            int r7 = r6.getLineTop(r2)     // Catch:{ Exception -> 0x0427 }
            float r7 = (float) r7     // Catch:{ Exception -> 0x0427 }
            r5.textYOffset = r7     // Catch:{ Exception -> 0x0427 }
            if (r4 == 0) goto L_0x02a2
            float r7 = r7 - r16
            int r7 = (int) r7     // Catch:{ Exception -> 0x0427 }
            r5.height = r7     // Catch:{ Exception -> 0x0427 }
        L_0x02a2:
            int r7 = r5.height     // Catch:{ Exception -> 0x0427 }
            android.text.StaticLayout r8 = r5.textLayout     // Catch:{ Exception -> 0x0427 }
            android.text.StaticLayout r9 = r5.textLayout     // Catch:{ Exception -> 0x0427 }
            int r9 = r9.getLineCount()     // Catch:{ Exception -> 0x0427 }
            r10 = 1
            int r9 = r9 - r10
            int r8 = r8.getLineBottom(r9)     // Catch:{ Exception -> 0x0427 }
            int r7 = java.lang.Math.max(r7, r8)     // Catch:{ Exception -> 0x0427 }
            r5.height = r7     // Catch:{ Exception -> 0x0427 }
            float r7 = r5.textYOffset     // Catch:{ Exception -> 0x0427 }
            r8 = r27
            int r11 = r8 + -1
            if (r4 != r11) goto L_0x02e3
            android.text.StaticLayout r9 = r5.textLayout
            int r9 = r9.getLineCount()
            int r9 = java.lang.Math.max(r0, r9)
            int r0 = r1.textHeight     // Catch:{ Exception -> 0x02de }
            float r10 = r5.textYOffset     // Catch:{ Exception -> 0x02de }
            android.text.StaticLayout r11 = r5.textLayout     // Catch:{ Exception -> 0x02de }
            int r11 = r11.getHeight()     // Catch:{ Exception -> 0x02de }
            float r11 = (float) r11     // Catch:{ Exception -> 0x02de }
            float r10 = r10 + r11
            int r10 = (int) r10     // Catch:{ Exception -> 0x02de }
            int r0 = java.lang.Math.max(r0, r10)     // Catch:{ Exception -> 0x02de }
            r1.textHeight = r0     // Catch:{ Exception -> 0x02de }
            goto L_0x02e4
        L_0x02de:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            goto L_0x02e4
        L_0x02e3:
            r9 = r0
        L_0x02e4:
            r16 = r7
        L_0x02e6:
            java.util.ArrayList<org.telegram.messenger.MessageObject$TextLayoutBlock> r0 = r1.textLayoutBlocks
            r0.add(r5)
            android.text.StaticLayout r0 = r5.textLayout     // Catch:{ Exception -> 0x02ff }
            int r7 = r9 + -1
            float r10 = r0.getLineLeft(r7)     // Catch:{ Exception -> 0x02ff }
            r7 = 0
            if (r4 != 0) goto L_0x0309
            int r0 = (r10 > r7 ? 1 : (r10 == r7 ? 0 : -1))
            if (r0 < 0) goto L_0x0309
            r1.textXOffset = r10     // Catch:{ Exception -> 0x02fd }
            goto L_0x0309
        L_0x02fd:
            r0 = move-exception
            goto L_0x0301
        L_0x02ff:
            r0 = move-exception
            r7 = 0
        L_0x0301:
            if (r4 != 0) goto L_0x0305
            r1.textXOffset = r7
        L_0x0305:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r10 = 0
        L_0x0309:
            android.text.StaticLayout r0 = r5.textLayout     // Catch:{ Exception -> 0x0312 }
            int r11 = r9 + -1
            float r0 = r0.getLineWidth(r11)     // Catch:{ Exception -> 0x0312 }
            goto L_0x0317
        L_0x0312:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x0317:
            double r11 = (double) r0
            double r11 = java.lang.Math.ceil(r11)
            int r11 = (int) r11
            int r12 = r15 + 80
            if (r11 <= r12) goto L_0x0322
            r11 = r15
        L_0x0322:
            int r12 = r8 + -1
            if (r4 != r12) goto L_0x0328
            r1.lastLineWidth = r11
        L_0x0328:
            float r0 = r0 + r10
            double r13 = (double) r0
            double r13 = java.lang.Math.ceil(r13)
            int r13 = (int) r13
            r14 = 1
            if (r9 <= r14) goto L_0x03e5
            r20 = r3
            r14 = r11
            r3 = r13
            r7 = 0
            r10 = 0
            r11 = 0
            r19 = 0
        L_0x033b:
            if (r7 >= r9) goto L_0x03c2
            android.text.StaticLayout r0 = r5.textLayout     // Catch:{ Exception -> 0x0344 }
            float r0 = r0.getLineWidth(r7)     // Catch:{ Exception -> 0x0344 }
            goto L_0x0349
        L_0x0344:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x0349:
            r28 = r6
            int r6 = r15 + 20
            float r6 = (float) r6
            int r6 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x0353
            float r0 = (float) r15
        L_0x0353:
            r6 = r0
            android.text.StaticLayout r0 = r5.textLayout     // Catch:{ Exception -> 0x035b }
            float r0 = r0.getLineLeft(r7)     // Catch:{ Exception -> 0x035b }
            goto L_0x0360
        L_0x035b:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
            r0 = 0
        L_0x0360:
            r21 = 0
            int r22 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1))
            if (r22 <= 0) goto L_0x037c
            r21 = r2
            float r2 = r1.textXOffset
            float r2 = java.lang.Math.min(r2, r0)
            r1.textXOffset = r2
            byte r2 = r5.directionFlags
            r22 = r9
            r9 = 1
            r2 = r2 | r9
            byte r2 = (byte) r2
            r5.directionFlags = r2
            r1.hasRtl = r9
            goto L_0x0387
        L_0x037c:
            r21 = r2
            r22 = r9
            byte r2 = r5.directionFlags
            r2 = r2 | 2
            byte r2 = (byte) r2
            r5.directionFlags = r2
        L_0x0387:
            if (r19 != 0) goto L_0x0399
            r2 = 0
            int r9 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r9 != 0) goto L_0x0399
            android.text.StaticLayout r2 = r5.textLayout     // Catch:{ Exception -> 0x0397 }
            int r2 = r2.getParagraphDirection(r7)     // Catch:{ Exception -> 0x0397 }
            r9 = 1
            if (r2 != r9) goto L_0x0399
        L_0x0397:
            r19 = 1
        L_0x0399:
            float r11 = java.lang.Math.max(r11, r6)
            float r0 = r0 + r6
            float r10 = java.lang.Math.max(r10, r0)
            r2 = r10
            double r9 = (double) r6
            double r9 = java.lang.Math.ceil(r9)
            int r6 = (int) r9
            int r14 = java.lang.Math.max(r14, r6)
            double r9 = (double) r0
            double r9 = java.lang.Math.ceil(r9)
            int r0 = (int) r9
            int r3 = java.lang.Math.max(r3, r0)
            int r7 = r7 + 1
            r10 = r2
            r2 = r21
            r9 = r22
            r6 = r28
            goto L_0x033b
        L_0x03c2:
            r21 = r2
            r28 = r6
            r22 = r9
            if (r19 == 0) goto L_0x03cf
            if (r4 != r12) goto L_0x03d4
            r1.lastLineWidth = r13
            goto L_0x03d4
        L_0x03cf:
            if (r4 != r12) goto L_0x03d3
            r1.lastLineWidth = r14
        L_0x03d3:
            r10 = r11
        L_0x03d4:
            int r0 = r1.textWidth
            double r2 = (double) r10
            double r2 = java.lang.Math.ceil(r2)
            int r2 = (int) r2
            int r0 = java.lang.Math.max(r0, r2)
            r1.textWidth = r0
            r2 = 0
            r10 = 1
            goto L_0x0424
        L_0x03e5:
            r21 = r2
            r20 = r3
            r28 = r6
            r22 = r9
            r2 = 0
            int r0 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1))
            if (r0 <= 0) goto L_0x0410
            float r0 = r1.textXOffset
            float r0 = java.lang.Math.min(r0, r10)
            r1.textXOffset = r0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x0401
            float r0 = (float) r11
            float r0 = r0 + r10
            int r11 = (int) r0
        L_0x0401:
            r10 = 1
            if (r8 == r10) goto L_0x0406
            r0 = 1
            goto L_0x0407
        L_0x0406:
            r0 = 0
        L_0x0407:
            r1.hasRtl = r0
            byte r0 = r5.directionFlags
            r0 = r0 | r10
            byte r0 = (byte) r0
            r5.directionFlags = r0
            goto L_0x0418
        L_0x0410:
            r10 = 1
            byte r0 = r5.directionFlags
            r0 = r0 | 2
            byte r0 = (byte) r0
            r5.directionFlags = r0
        L_0x0418:
            int r0 = r1.textWidth
            int r3 = java.lang.Math.min(r15, r11)
            int r0 = java.lang.Math.max(r0, r3)
            r1.textWidth = r0
        L_0x0424:
            int r0 = r21 + r22
            goto L_0x044d
        L_0x0427:
            r0 = move-exception
            r21 = r2
            r20 = r3
            r28 = r6
            goto L_0x0434
        L_0x042f:
            r0 = move-exception
            r21 = r2
            r20 = r3
        L_0x0434:
            r8 = r27
            r2 = 0
            r10 = 1
            goto L_0x0448
        L_0x0439:
            r0 = move-exception
            r20 = r3
            r21 = r8
            r4 = r9
            r8 = r11
            r28 = r12
            r18 = r14
            r2 = 0
            r10 = 1
            r17 = 24
        L_0x0448:
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x044b:
            r0 = r21
        L_0x044d:
            int r9 = r4 + 1
            r11 = r8
            r14 = r18
            r3 = r20
            r12 = r28
            r2 = 0
            r4 = 1
            r10 = 0
            r13 = 24
            r8 = r0
            goto L_0x0174
        L_0x045e:
            return
        L_0x045f:
            r0 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x0463:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLayout(org.telegram.tgnet.TLRPC$User):void");
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    public boolean isOutOwner() {
        TLRPC$Peer tLRPC$Peer;
        TLRPC$Peer tLRPC$Peer2;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (!tLRPC$Message.out || tLRPC$Message.from_id <= 0 || tLRPC$Message.post) {
            return false;
        }
        if (tLRPC$Message.fwd_from == null) {
            return true;
        }
        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (getDialogId() == ((long) clientUserId)) {
            TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
            if ((tLRPC$MessageFwdHeader.from_id != clientUserId || ((tLRPC$Peer2 = tLRPC$MessageFwdHeader.saved_from_peer) != null && tLRPC$Peer2.user_id != clientUserId)) && ((tLRPC$Peer = this.messageOwner.fwd_from.saved_from_peer) == null || tLRPC$Peer.user_id != clientUserId)) {
                return false;
            }
            return true;
        }
        TLRPC$Peer tLRPC$Peer3 = this.messageOwner.fwd_from.saved_from_peer;
        if (tLRPC$Peer3 == null || tLRPC$Peer3.user_id == clientUserId) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000e, code lost:
        r0 = r5.messageOwner.fwd_from;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean needDrawAvatar() {
        /*
            r5 = this;
            boolean r0 = r5.isFromUser()
            if (r0 != 0) goto L_0x001b
            long r0 = r5.eventId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x001b
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x0019
            org.telegram.tgnet.TLRPC$Peer r0 = r0.saved_from_peer
            if (r0 == 0) goto L_0x0019
            goto L_0x001b
        L_0x0019:
            r0 = 0
            goto L_0x001c
        L_0x001b:
            r0 = 1
        L_0x001c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.needDrawAvatar():boolean");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0014, code lost:
        r0 = r5.messageOwner.fwd_from;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean needDrawAvatarInternal() {
        /*
            r5 = this;
            boolean r0 = r5.isFromChat()
            if (r0 == 0) goto L_0x000c
            boolean r0 = r5.isFromUser()
            if (r0 != 0) goto L_0x0021
        L_0x000c:
            long r0 = r5.eventId
            r2 = 0
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0021
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x001f
            org.telegram.tgnet.TLRPC$Peer r0 = r0.saved_from_peer
            if (r0 == 0) goto L_0x001f
            goto L_0x0021
        L_0x001f:
            r0 = 0
            goto L_0x0022
        L_0x0021:
            r0 = 1
        L_0x0022:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.needDrawAvatarInternal():boolean");
    }

    public boolean isFromChat() {
        TLRPC$Peer tLRPC$Peer;
        TLRPC$Chat chat;
        if (getDialogId() == ((long) UserConfig.getInstance(this.currentAccount).clientUserId) || isMegagroup() || ((tLRPC$Peer = this.messageOwner.to_id) != null && tLRPC$Peer.chat_id != 0)) {
            return true;
        }
        TLRPC$Peer tLRPC$Peer2 = this.messageOwner.to_id;
        if (tLRPC$Peer2 == null || tLRPC$Peer2.channel_id == 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id))) == null || !chat.megagroup) {
            return false;
        }
        return true;
    }

    public boolean isFromUser() {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        return tLRPC$Message.from_id > 0 && !tLRPC$Message.post;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r0.fwd_from;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isForwardedChannelPost() {
        /*
            r2 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r2.messageOwner
            int r1 = r0.from_id
            if (r1 > 0) goto L_0x0010
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x0010
            int r0 = r0.channel_post
            if (r0 == 0) goto L_0x0010
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isForwardedChannelPost():boolean");
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
        int i = !tLRPC$Message.unread ? 1 : 0;
        return !tLRPC$Message.media_unread ? i | 2 : i;
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

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0009, code lost:
        r0 = r0.channel_id;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public long getIdWithChannel() {
        /*
            r5 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            int r1 = r0.id
            long r1 = (long) r1
            org.telegram.tgnet.TLRPC$Peer r0 = r0.to_id
            if (r0 == 0) goto L_0x0012
            int r0 = r0.channel_id
            if (r0 == 0) goto L_0x0012
            long r3 = (long) r0
            r0 = 32
            long r3 = r3 << r0
            long r1 = r1 | r3
        L_0x0012:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getIdWithChannel():long");
    }

    public int getChannelId() {
        TLRPC$Peer tLRPC$Peer = this.messageOwner.to_id;
        if (tLRPC$Peer != null) {
            return tLRPC$Peer.channel_id;
        }
        return 0;
    }

    public static boolean shouldEncryptPhotoOrVideo(TLRPC$Message tLRPC$Message) {
        int i;
        if (!(tLRPC$Message instanceof TLRPC$TL_message_secret)) {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && tLRPC$Message.media.ttl_seconds != 0) {
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
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && tLRPC$Message.media.ttl_seconds != 0) {
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
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && tLRPC$Message.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean needDrawBluredPreview() {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            int max = Math.max(tLRPC$Message.ttl, tLRPC$Message.media.ttl_seconds);
            if (max <= 0 || (((!(this.messageOwner.media instanceof TLRPC$TL_messageMediaPhoto) && !isVideo() && !isGif()) || max > 60) && !isRoundVideo())) {
                return false;
            }
            return true;
        } else if (!(tLRPC$Message instanceof TLRPC$TL_message)) {
            return false;
        } else {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && this.messageOwner.media.ttl_seconds != 0) {
                return true;
            }
            return false;
        }
    }

    public boolean isSecretMedia() {
        int i;
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message instanceof TLRPC$TL_message_secret) {
            if ((((tLRPC$Message.media instanceof TLRPC$TL_messageMediaPhoto) || isGif()) && (i = this.messageOwner.ttl) > 0 && i <= 60) || isVoice() || isRoundVideo() || isVideo()) {
                return true;
            }
            return false;
        } else if (!(tLRPC$Message instanceof TLRPC$TL_message)) {
            return false;
        } else {
            TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
            if (((tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaPhoto) || (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaDocument)) && this.messageOwner.media.ttl_seconds != 0) {
                return true;
            }
            return false;
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

    public boolean isMegagroup() {
        return isMegagroup(this.messageOwner);
    }

    public boolean isSavedFromMegagroup() {
        TLRPC$Peer tLRPC$Peer;
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
        if (tLRPC$MessageFwdHeader == null || (tLRPC$Peer = tLRPC$MessageFwdHeader.saved_from_peer) == null || tLRPC$Peer.channel_id == 0) {
            return false;
        }
        return ChatObject.isMegagroup(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.saved_from_peer.channel_id)));
    }

    public static boolean isMegagroup(TLRPC$Message tLRPC$Message) {
        return (tLRPC$Message.flags & Integer.MIN_VALUE) != 0;
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
        if (tLRPC$Message.dialog_id == 0 && (tLRPC$Peer = tLRPC$Message.to_id) != null) {
            int i = tLRPC$Peer.chat_id;
            if (i != 0) {
                tLRPC$Message.dialog_id = (long) (-i);
            } else {
                int i2 = tLRPC$Peer.channel_id;
                if (i2 != 0) {
                    tLRPC$Message.dialog_id = (long) (-i2);
                } else if (isOut(tLRPC$Message)) {
                    tLRPC$Message.dialog_id = (long) tLRPC$Message.to_id.user_id;
                } else {
                    tLRPC$Message.dialog_id = (long) tLRPC$Message.from_id;
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

    public boolean isSendError() {
        TLRPC$Message tLRPC$Message = this.messageOwner;
        if (tLRPC$Message.send_state != 2 || tLRPC$Message.id >= 0) {
            if (this.scheduled) {
                TLRPC$Message tLRPC$Message2 = this.messageOwner;
                if (tLRPC$Message2.id <= 0 || tLRPC$Message2.date >= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - 60) {
                    return false;
                }
            }
            return false;
        }
        return true;
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

    public static boolean isStickerDocument(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            for (int i = 0; i < tLRPC$Document.attributes.size(); i++) {
                if (tLRPC$Document.attributes.get(i) instanceof TLRPC$TL_documentAttributeSticker) {
                    return "image/webp".equals(tLRPC$Document.mime_type);
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
        TLRPC$InputStickerSet tLRPC$InputStickerSet;
        if (tLRPC$Document != null && (("application/x-tgsticker".equals(tLRPC$Document.mime_type) && !tLRPC$Document.thumbs.isEmpty()) || "application/x-tgsdice".equals(tLRPC$Document.mime_type))) {
            if (z) {
                return true;
            }
            int size = tLRPC$Document.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i);
                if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) && (tLRPC$InputStickerSet = tLRPC$DocumentAttribute.stickerset) != null && !(tLRPC$InputStickerSet instanceof TLRPC$TL_inputStickerSetEmpty)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canAutoplayAnimatedSticker(TLRPC$Document tLRPC$Document) {
        return isAnimatedStickerDocument(tLRPC$Document, true) && SharedConfig.getDevicePerfomanceClass() != 0;
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
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        return tLRPC$MessageMedia != null && isAnimatedStickerDocument(tLRPC$MessageMedia.document, DialogObject.isSecretDialogId(tLRPC$Message.dialog_id) ^ true);
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
        return tLRPC$MessageMedia != null && isGifDocument(tLRPC$MessageMedia.document);
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
        if (tLRPC$MessageMedia instanceof TLRPC$TL_messageMediaWebPage) {
            return isVideoDocument(tLRPC$MessageMedia.webpage.document);
        }
        return tLRPC$MessageMedia != null && isVideoDocument(tLRPC$MessageMedia.document);
    }

    public static boolean isGameMessage(TLRPC$Message tLRPC$Message) {
        return tLRPC$Message.media instanceof TLRPC$TL_messageMediaGame;
    }

    public static boolean isInvoiceMessage(TLRPC$Message tLRPC$Message) {
        return tLRPC$Message.media instanceof TLRPC$TL_messageMediaInvoice;
    }

    public static TLRPC$InputStickerSet getInputStickerSet(TLRPC$Message tLRPC$Message) {
        TLRPC$Document tLRPC$Document;
        TLRPC$MessageMedia tLRPC$MessageMedia = tLRPC$Message.media;
        if (tLRPC$MessageMedia == null || (tLRPC$Document = tLRPC$MessageMedia.document) == null) {
            return null;
        }
        return getInputStickerSet(tLRPC$Document);
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

    public String getStrickerChar() {
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

    public boolean isAnimatedEmoji() {
        return this.emojiAnimatedSticker != null;
    }

    public boolean isDice() {
        return this.messageOwner.media instanceof TLRPC$TL_messageMediaDice;
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
        return isStickerDocument(getDocument());
    }

    public boolean isAnimatedSticker() {
        int i = this.type;
        if (i != 1000) {
            return i == 15;
        }
        return isAnimatedStickerDocument(getDocument(), true ^ DialogObject.isSecretDialogId(getDialogId()));
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

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0006, code lost:
        r0 = r0.photo;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean hasPhotoStickers() {
        /*
            r1 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r1.messageOwner
            org.telegram.tgnet.TLRPC$MessageMedia r0 = r0.media
            if (r0 == 0) goto L_0x0010
            org.telegram.tgnet.TLRPC$Photo r0 = r0.photo
            if (r0 == 0) goto L_0x0010
            boolean r0 = r0.has_stickers
            if (r0 == 0) goto L_0x0010
            r0 = 1
            goto L_0x0011
        L_0x0010:
            r0 = 0
        L_0x0011:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.hasPhotoStickers():boolean");
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
                        return LocaleController.formatDateAudio((long) this.messageOwner.date);
                    }
                } else if ((tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeVideo) && tLRPC$DocumentAttribute.round_message) {
                    return LocaleController.formatDateAudio((long) this.messageOwner.date);
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
        if (document != null) {
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
    /* JADX WARNING: Removed duplicated region for block: B:71:0x010f A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.lang.String getMusicAuthor(boolean r10) {
        /*
            r9 = this;
            org.telegram.tgnet.TLRPC$Document r0 = r9.getDocument()
            r1 = 2131624305(0x7f0e0171, float:1.8875786E38)
            java.lang.String r2 = "AudioUnknownArtist"
            if (r0 == 0) goto L_0x0113
            r3 = 0
            r4 = 0
        L_0x000d:
            java.util.ArrayList<org.telegram.tgnet.TLRPC$DocumentAttribute> r5 = r0.attributes
            int r5 = r5.size()
            if (r3 >= r5) goto L_0x0113
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
            if (r10 == 0) goto L_0x0036
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
            if (r4 == 0) goto L_0x010f
            r5 = 0
            if (r10 != 0) goto L_0x0046
            return r5
        L_0x0046:
            boolean r6 = r9.isOutOwner()
            if (r6 != 0) goto L_0x0105
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x0062
            int r6 = r6.from_id
            int r7 = r9.currentAccount
            org.telegram.messenger.UserConfig r7 = org.telegram.messenger.UserConfig.getInstance(r7)
            int r7 = r7.getClientUserId()
            if (r6 != r7) goto L_0x0062
            goto L_0x0105
        L_0x0062:
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x0082
            int r6 = r6.channel_id
            if (r6 == 0) goto L_0x0082
            int r6 = r9.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r7 = r7.fwd_from
            int r7 = r7.channel_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x00f9
        L_0x0082:
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x00a4
            int r6 = r6.from_id
            if (r6 == 0) goto L_0x00a4
            int r6 = r9.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r7 = r7.fwd_from
            int r7 = r7.from_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
        L_0x00a0:
            r8 = r6
            r6 = r5
            r5 = r8
            goto L_0x00f9
        L_0x00a4:
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            org.telegram.tgnet.TLRPC$MessageFwdHeader r6 = r6.fwd_from
            if (r6 == 0) goto L_0x00af
            java.lang.String r6 = r6.from_name
            if (r6 == 0) goto L_0x00af
            return r6
        L_0x00af:
            org.telegram.tgnet.TLRPC$Message r6 = r9.messageOwner
            int r7 = r6.from_id
            if (r7 >= 0) goto L_0x00c9
            int r6 = r9.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            int r7 = r7.from_id
            int r7 = -r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x00f9
        L_0x00c9:
            if (r7 != 0) goto L_0x00e6
            org.telegram.tgnet.TLRPC$Peer r6 = r6.to_id
            int r6 = r6.channel_id
            if (r6 == 0) goto L_0x00e6
            int r6 = r9.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            org.telegram.tgnet.TLRPC$Peer r7 = r7.to_id
            int r7 = r7.channel_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$Chat r6 = r6.getChat(r7)
            goto L_0x00f9
        L_0x00e6:
            int r6 = r9.currentAccount
            org.telegram.messenger.MessagesController r6 = org.telegram.messenger.MessagesController.getInstance(r6)
            org.telegram.tgnet.TLRPC$Message r7 = r9.messageOwner
            int r7 = r7.from_id
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            org.telegram.tgnet.TLRPC$User r6 = r6.getUser(r7)
            goto L_0x00a0
        L_0x00f9:
            if (r5 == 0) goto L_0x0100
            java.lang.String r10 = org.telegram.messenger.UserObject.getUserName(r5)
            return r10
        L_0x0100:
            if (r6 == 0) goto L_0x010f
            java.lang.String r10 = r6.title
            return r10
        L_0x0105:
            r10 = 2131625352(0x7f0e0588, float:1.887791E38)
            java.lang.String r0 = "FromYou"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r0, r10)
            return r10
        L_0x010f:
            int r3 = r3 + 1
            goto L_0x000d
        L_0x0113:
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r2, r1)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getMusicAuthor(boolean):java.lang.String");
    }

    public TLRPC$InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x000c, code lost:
        r1 = (r0 = r0.fwd_from).saved_from_peer;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean needDrawForwarded() {
        /*
            r5 = this;
            org.telegram.tgnet.TLRPC$Message r0 = r5.messageOwner
            int r1 = r0.flags
            r1 = r1 & 4
            if (r1 == 0) goto L_0x002b
            org.telegram.tgnet.TLRPC$MessageFwdHeader r0 = r0.fwd_from
            if (r0 == 0) goto L_0x002b
            org.telegram.tgnet.TLRPC$Peer r1 = r0.saved_from_peer
            if (r1 == 0) goto L_0x0016
            int r1 = r1.channel_id
            int r0 = r0.channel_id
            if (r1 == r0) goto L_0x002b
        L_0x0016:
            int r0 = r5.currentAccount
            org.telegram.messenger.UserConfig r0 = org.telegram.messenger.UserConfig.getInstance(r0)
            int r0 = r0.getClientUserId()
            long r0 = (long) r0
            long r2 = r5.getDialogId()
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 == 0) goto L_0x002b
            r0 = 1
            goto L_0x002c
        L_0x002b:
            r0 = 0
        L_0x002c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.needDrawForwarded():boolean");
    }

    public static boolean isForwardedMessage(TLRPC$Message tLRPC$Message) {
        return ((tLRPC$Message.flags & 4) == 0 || tLRPC$Message.fwd_from == null) ? false : true;
    }

    public boolean isReply() {
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject == null || !(messageObject.messageOwner instanceof TLRPC$TL_messageEmpty)) {
            TLRPC$Message tLRPC$Message = this.messageOwner;
            if (!((tLRPC$Message.reply_to_msg_id == 0 && tLRPC$Message.reply_to_random_id == 0) || (this.messageOwner.flags & 8) == 0)) {
                return true;
            }
        }
        return false;
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

    public boolean canEditMessage(TLRPC$Chat tLRPC$Chat) {
        return canEditMessage(this.currentAccount, this.messageOwner, tLRPC$Chat, this.scheduled);
    }

    public boolean canEditMessageScheduleTime(TLRPC$Chat tLRPC$Chat) {
        return canEditMessageScheduleTime(this.currentAccount, this.messageOwner, tLRPC$Chat);
    }

    public boolean canForwardMessage() {
        return !(this.messageOwner instanceof TLRPC$TL_message_secret) && !needDrawBluredPreview() && !isLiveLocation() && this.type != 16;
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

    public static boolean canEditMessageAnytime(int i, TLRPC$Message tLRPC$Message, TLRPC$Chat tLRPC$Chat) {
        TLRPC$MessageMedia tLRPC$MessageMedia;
        TLRPC$MessageAction tLRPC$MessageAction;
        TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights;
        if (!(tLRPC$Message == null || tLRPC$Message.to_id == null || (((tLRPC$MessageMedia = tLRPC$Message.media) != null && (isRoundVideoDocument(tLRPC$MessageMedia.document) || isStickerDocument(tLRPC$Message.media.document) || isAnimatedStickerDocument(tLRPC$Message.media.document, true))) || (((tLRPC$MessageAction = tLRPC$Message.action) != null && !(tLRPC$MessageAction instanceof TLRPC$TL_messageActionEmpty)) || isForwardedMessage(tLRPC$Message) || tLRPC$Message.via_bot_id != 0 || tLRPC$Message.id < 0)))) {
            int i2 = tLRPC$Message.from_id;
            if (i2 == tLRPC$Message.to_id.user_id && i2 == UserConfig.getInstance(i).getClientUserId() && !isLiveLocationMessage(tLRPC$Message)) {
                return true;
            }
            if (!(tLRPC$Chat == null && tLRPC$Message.to_id.channel_id != 0 && (tLRPC$Chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(tLRPC$Message.to_id.channel_id))) == null) && tLRPC$Message.out && tLRPC$Chat != null && tLRPC$Chat.megagroup && (tLRPC$Chat.creator || ((tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) != null && tLRPC$TL_chatAdminRights.pin_messages))) {
                return true;
            }
        }
        return false;
    }

    public static boolean canEditMessageScheduleTime(int i, TLRPC$Message tLRPC$Message, TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Chat == null && tLRPC$Message.to_id.channel_id != 0 && (tLRPC$Chat = MessagesController.getInstance(i).getChat(Integer.valueOf(tLRPC$Message.to_id.channel_id))) == null) {
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
        if (z && tLRPC$Message.date < ConnectionsManager.getInstance(i).getCurrentTime() - 60) {
            return false;
        }
        if ((tLRPC$Chat == null || (!tLRPC$Chat.left && !tLRPC$Chat.kicked)) && tLRPC$Message != null && tLRPC$Message.to_id != null && (((tLRPC$MessageMedia = tLRPC$Message.media) == null || (!isRoundVideoDocument(tLRPC$MessageMedia.document) && !isStickerDocument(tLRPC$Message.media.document) && !isAnimatedStickerDocument(tLRPC$Message.media.document, true) && !isLocationMessage(tLRPC$Message))) && (((tLRPC$MessageAction = tLRPC$Message.action) == null || (tLRPC$MessageAction instanceof TLRPC$TL_messageActionEmpty)) && !isForwardedMessage(tLRPC$Message) && tLRPC$Message.via_bot_id == 0 && tLRPC$Message.id >= 0))) {
            int i2 = tLRPC$Message.from_id;
            if (i2 == tLRPC$Message.to_id.user_id && i2 == UserConfig.getInstance(i).getClientUserId() && !isLiveLocationMessage(tLRPC$Message) && !(tLRPC$Message.media instanceof TLRPC$TL_messageMediaContact)) {
                return true;
            }
            if (tLRPC$Chat == null && tLRPC$Message.to_id.channel_id != 0 && (tLRPC$Chat = MessagesController.getInstance(i).getChat(Integer.valueOf(tLRPC$Message.to_id.channel_id))) == null) {
                return false;
            }
            TLRPC$MessageMedia tLRPC$MessageMedia2 = tLRPC$Message.media;
            if (tLRPC$MessageMedia2 != null && !(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaEmpty) && !(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaPhoto) && !(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaDocument) && !(tLRPC$MessageMedia2 instanceof TLRPC$TL_messageMediaWebPage)) {
                return false;
            }
            if (tLRPC$Message.out && tLRPC$Chat != null && tLRPC$Chat.megagroup && (tLRPC$Chat.creator || ((tLRPC$TL_chatAdminRights2 = tLRPC$Chat.admin_rights) != null && tLRPC$TL_chatAdminRights2.pin_messages))) {
                return true;
            }
            if (!z && Math.abs(tLRPC$Message.date - ConnectionsManager.getInstance(i).getCurrentTime()) > MessagesController.getInstance(i).maxEditTime) {
                return false;
            }
            if (tLRPC$Message.to_id.channel_id == 0) {
                if (!tLRPC$Message.out && tLRPC$Message.from_id != UserConfig.getInstance(i).getClientUserId()) {
                    return false;
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
            } else if ((tLRPC$Chat.megagroup && tLRPC$Message.out) || (!tLRPC$Chat.megagroup && ((tLRPC$Chat.creator || ((tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights) != null && (tLRPC$TL_chatAdminRights.edit_messages || (tLRPC$Message.out && tLRPC$TL_chatAdminRights.post_messages)))) && tLRPC$Message.post))) {
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
        return this.eventId == 0 && canDeleteMessage(this.currentAccount, z, this.messageOwner, tLRPC$Chat);
    }

    public static boolean canDeleteMessage(int i, boolean z, TLRPC$Message tLRPC$Message, TLRPC$Chat tLRPC$Chat) {
        if (tLRPC$Message.id < 0) {
            return true;
        }
        if (tLRPC$Chat == null && tLRPC$Message.to_id.channel_id != 0) {
            tLRPC$Chat = MessagesController.getInstance(i).getChat(Integer.valueOf(tLRPC$Message.to_id.channel_id));
        }
        if (ChatObject.isChannel(tLRPC$Chat)) {
            if (!z || tLRPC$Chat.megagroup) {
                if (z) {
                    return true;
                }
                if (tLRPC$Message.id != 1) {
                    if (tLRPC$Chat.creator) {
                        return true;
                    }
                    TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights = tLRPC$Chat.admin_rights;
                    if (tLRPC$TL_chatAdminRights != null) {
                        if (tLRPC$TL_chatAdminRights.delete_messages) {
                            return true;
                        }
                        if (tLRPC$Message.out && (tLRPC$Chat.megagroup || tLRPC$TL_chatAdminRights.post_messages)) {
                            return true;
                        }
                    }
                    if (!tLRPC$Chat.megagroup || !tLRPC$Message.out || tLRPC$Message.from_id <= 0) {
                        return false;
                    }
                    return true;
                }
                return false;
            } else if (tLRPC$Chat.creator) {
                return true;
            } else {
                TLRPC$TL_chatAdminRights tLRPC$TL_chatAdminRights2 = tLRPC$Chat.admin_rights;
                if (tLRPC$TL_chatAdminRights2 == null || (!tLRPC$TL_chatAdminRights2.delete_messages && !tLRPC$Message.out)) {
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
        if (tLRPC$MessageFwdHeader.channel_id != 0) {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
            if (chat != null) {
                return chat.title;
            }
            return null;
        } else if (tLRPC$MessageFwdHeader.from_id != 0) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
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

    public int getFromId() {
        TLRPC$Peer tLRPC$Peer;
        int i;
        TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader = this.messageOwner.fwd_from;
        if (tLRPC$MessageFwdHeader == null || (tLRPC$Peer = tLRPC$MessageFwdHeader.saved_from_peer) == null) {
            TLRPC$Message tLRPC$Message = this.messageOwner;
            int i2 = tLRPC$Message.from_id;
            if (i2 != 0) {
                return i2;
            }
            if (tLRPC$Message.post) {
                return tLRPC$Message.to_id.channel_id;
            }
            return 0;
        }
        int i3 = tLRPC$Peer.user_id;
        if (i3 != 0) {
            int i4 = tLRPC$MessageFwdHeader.from_id;
            return i4 != 0 ? i4 : i3;
        } else if (tLRPC$Peer.channel_id == 0) {
            int i5 = tLRPC$Peer.chat_id;
            if (i5 == 0) {
                return 0;
            }
            int i6 = tLRPC$MessageFwdHeader.from_id;
            if (i6 != 0) {
                return i6;
            }
            int i7 = tLRPC$MessageFwdHeader.channel_id;
            return i7 != 0 ? -i7 : -i5;
        } else if (isSavedFromMegagroup() && (i = this.messageOwner.fwd_from.from_id) != 0) {
            return i;
        } else {
            TLRPC$MessageFwdHeader tLRPC$MessageFwdHeader2 = this.messageOwner.fwd_from;
            int i8 = tLRPC$MessageFwdHeader2.channel_id;
            if (i8 != 0) {
                return -i8;
            }
            return -tLRPC$MessageFwdHeader2.saved_from_peer.channel_id;
        }
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
        int i = this.attachPathExists ? 1 : 0;
        return this.mediaExists ? i | 2 : i;
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
        int i;
        boolean z = false;
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
            if (document != null) {
                if (isWallpaper()) {
                    this.mediaExists = FileLoader.getPathToAttach(document, true).exists();
                } else {
                    this.mediaExists = FileLoader.getPathToAttach(document).exists();
                }
            } else if (this.type == 0) {
                TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize != null) {
                    if (closestPhotoSizeWithSize != null) {
                        this.mediaExists = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true).exists();
                    }
                } else {
                    return;
                }
            }
            if (!this.mediaExists && document != null) {
                if (isVideoDocument(document) && shouldEncryptPhotoOrVideo()) {
                    z = true;
                }
                this.loadedFileSize = FileLoader.getTempFileSize(document, z);
            }
        }
    }

    public boolean equals(MessageObject messageObject) {
        return getId() == messageObject.getId() && getDialogId() == messageObject.getDialogId();
    }
}
