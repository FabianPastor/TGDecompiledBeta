package org.telegram.messenger;

import android.graphics.Point;
import android.graphics.Typeface;
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
import org.telegram.messenger.Emoji.EmojiSpan;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.DecryptedMessageAction;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Page;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.SecureValueType;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_documentEncrypted;
import org.telegram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionBotAllowed;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditTitle;
import org.telegram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionContactSignUp;
import org.telegram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.telegram.tgnet.TLRPC.TL_messageActionCustomAction;
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionScreenshotTaken;
import org.telegram.tgnet.TLRPC.TL_messageActionSecureValuesSent;
import org.telegram.tgnet.TLRPC.TL_messageActionTTLChange;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageForwarded_old2;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaPoll;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageReactions;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
import org.telegram.tgnet.TLRPC.TL_pageBlockPhoto;
import org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
import org.telegram.tgnet.TLRPC.TL_pageBlockVideo;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoCachedSize;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_pollAnswerVoters;
import org.telegram.tgnet.TLRPC.TL_pollResults;
import org.telegram.tgnet.TLRPC.TL_reactionCount;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeAddress;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeBankStatement;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeDriverLicense;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeEmail;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeIdentityCard;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeInternalPassport;
import org.telegram.tgnet.TLRPC.TL_secureValueTypePassport;
import org.telegram.tgnet.TLRPC.TL_secureValueTypePassportRegistration;
import org.telegram.tgnet.TLRPC.TL_secureValueTypePersonalDetails;
import org.telegram.tgnet.TLRPC.TL_secureValueTypePhone;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeRentalAgreement;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeTemporaryRegistration;
import org.telegram.tgnet.TLRPC.TL_secureValueTypeUtilityBill;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.TL_webPageAttributeTheme;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.WebPage;
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
    public static final int TYPE_POLL = 17;
    public static final int TYPE_ROUND_VIDEO = 5;
    public static final int TYPE_STICKER = 13;
    static final String[] excludeWords = new String[]{" vs. ", " vs ", " versus ", " ft. ", " ft ", " featuring ", " feat. ", " feat ", " presents ", " pres. ", " pres ", " and ", " & ", " . "};
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
    public int contentType;
    public int currentAccount;
    public TL_channelAdminLogEvent currentEvent;
    public String customReplyName;
    public String dateKey;
    public boolean deleted;
    public CharSequence editingMessage;
    public ArrayList<MessageEntity> editingMessageEntities;
    public Document emojiAnimatedSticker;
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
    public boolean localChannel;
    public boolean localEdit;
    public long localGroupId;
    public String localName;
    public long localSentGroupId;
    public int localType;
    public String localUserName;
    public boolean mediaExists;
    public Message messageOwner;
    public CharSequence messageText;
    public String monthKey;
    public ArrayList<PhotoSize> photoThumbs;
    public ArrayList<PhotoSize> photoThumbs2;
    public TLObject photoThumbsObject;
    public TLObject photoThumbsObject2;
    public long pollLastCheckTime;
    public boolean pollVisibleOnScreen;
    public String previousAttachPath;
    public String previousCaption;
    public ArrayList<MessageEntity> previousCaptionEntities;
    public MessageMedia previousMedia;
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
        public ArrayList<MessageObject> messages = new ArrayList();
        public ArrayList<GroupedMessagePosition> posArray = new ArrayList();
        public HashMap<MessageObject, GroupedMessagePosition> positions = new HashMap();

        private class MessageGroupedLayoutAttempt {
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

        /* JADX WARNING: Missing block: B:23:0x005e, code skipped:
            if ((r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice) == false) goto L_0x0062;
     */
        /* JADX WARNING: Missing block: B:179:0x06db, code skipped:
            if (r6[2] > r6[3]) goto L_0x06df;
     */
        public void calculate() {
            /*
            r36 = this;
            r10 = r36;
            r0 = r10.posArray;
            r0.clear();
            r0 = r10.positions;
            r0.clear();
            r0 = r10.messages;
            r11 = r0.size();
            r12 = 1;
            if (r11 > r12) goto L_0x0016;
        L_0x0015:
            return;
        L_0x0016:
            r13 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r0 = new java.lang.StringBuilder;
            r0.<init>();
            r14 = 0;
            r10.hasSibling = r14;
            r2 = 0;
            r3 = 0;
            r4 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r5 = 0;
            r15 = 0;
        L_0x0027:
            r16 = NUM; // 0x3var_a float:1.2 double:5.271833295E-315;
            if (r2 >= r11) goto L_0x00c3;
        L_0x002c:
            r6 = r10.messages;
            r6 = r6.get(r2);
            r6 = (org.telegram.messenger.MessageObject) r6;
            if (r2 != 0) goto L_0x0065;
        L_0x0036:
            r3 = r6.isOutOwner();
            if (r3 != 0) goto L_0x0062;
        L_0x003c:
            r7 = r6.messageOwner;
            r7 = r7.fwd_from;
            if (r7 == 0) goto L_0x0046;
        L_0x0042:
            r7 = r7.saved_from_peer;
            if (r7 != 0) goto L_0x0060;
        L_0x0046:
            r7 = r6.messageOwner;
            r8 = r7.from_id;
            if (r8 <= 0) goto L_0x0062;
        L_0x004c:
            r8 = r7.to_id;
            r9 = r8.channel_id;
            if (r9 != 0) goto L_0x0060;
        L_0x0052:
            r8 = r8.chat_id;
            if (r8 != 0) goto L_0x0060;
        L_0x0056:
            r7 = r7.media;
            r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
            if (r8 != 0) goto L_0x0060;
        L_0x005c:
            r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
            if (r7 == 0) goto L_0x0062;
        L_0x0060:
            r7 = 1;
            goto L_0x0063;
        L_0x0062:
            r7 = 0;
        L_0x0063:
            r15 = r3;
            r3 = r7;
        L_0x0065:
            r7 = r6.photoThumbs;
            r8 = org.telegram.messenger.AndroidUtilities.getPhotoSize();
            r7 = org.telegram.messenger.FileLoader.getClosestPhotoSizeWithSize(r7, r8);
            r8 = new org.telegram.messenger.MessageObject$GroupedMessagePosition;
            r8.<init>();
            r9 = r11 + -1;
            if (r2 != r9) goto L_0x007a;
        L_0x0078:
            r9 = 1;
            goto L_0x007b;
        L_0x007a:
            r9 = 0;
        L_0x007b:
            r8.last = r9;
            if (r7 != 0) goto L_0x0082;
        L_0x007f:
            r7 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            goto L_0x008a;
        L_0x0082:
            r9 = r7.w;
            r9 = (float) r9;
            r7 = r7.h;
            r7 = (float) r7;
            r7 = r9 / r7;
        L_0x008a:
            r8.aspectRatio = r7;
            r7 = r8.aspectRatio;
            r9 = (r7 > r16 ? 1 : (r7 == r16 ? 0 : -1));
            if (r9 <= 0) goto L_0x0099;
        L_0x0092:
            r7 = "w";
            r0.append(r7);
            goto L_0x00ab;
        L_0x0099:
            r9 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
            r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
            if (r7 >= 0) goto L_0x00a6;
        L_0x00a0:
            r7 = "n";
            r0.append(r7);
            goto L_0x00ab;
        L_0x00a6:
            r7 = "q";
            r0.append(r7);
        L_0x00ab:
            r7 = r8.aspectRatio;
            r4 = r4 + r7;
            r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
            if (r7 <= 0) goto L_0x00b5;
        L_0x00b4:
            r5 = 1;
        L_0x00b5:
            r7 = r10.positions;
            r7.put(r6, r8);
            r6 = r10.posArray;
            r6.add(r8);
            r2 = r2 + 1;
            goto L_0x0027;
        L_0x00c3:
            if (r3 == 0) goto L_0x00d1;
        L_0x00c5:
            r2 = r10.maxSizeWidth;
            r2 = r2 + -50;
            r10.maxSizeWidth = r2;
            r2 = r10.firstSpanAdditionalSize;
            r2 = r2 + 50;
            r10.firstSpanAdditionalSize = r2;
        L_0x00d1:
            r2 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
            r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r2 = (float) r2;
            r6 = org.telegram.messenger.AndroidUtilities.displaySize;
            r7 = r6.x;
            r6 = r6.y;
            r6 = java.lang.Math.min(r7, r6);
            r6 = (float) r6;
            r7 = r10.maxSizeWidth;
            r7 = (float) r7;
            r6 = r6 / r7;
            r2 = r2 / r6;
            r9 = (int) r2;
            r2 = NUM; // 0x42200000 float:40.0 double:5.481131706E-315;
            r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
            r2 = (float) r2;
            r6 = org.telegram.messenger.AndroidUtilities.displaySize;
            r7 = r6.x;
            r6 = r6.y;
            r6 = java.lang.Math.min(r7, r6);
            r6 = (float) r6;
            r7 = r10.maxSizeWidth;
            r8 = (float) r7;
            r6 = r6 / r8;
            r2 = r2 / r6;
            r2 = (int) r2;
            r6 = (float) r7;
            r6 = r6 / r13;
            r7 = (float) r11;
            r8 = r4 / r7;
            r4 = NUM; // 0x42CLASSNAME float:100.0 double:5.53552857E-315;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
            r4 = (float) r4;
            r7 = r4 / r13;
            r4 = 4;
            r1 = 3;
            r13 = 2;
            if (r5 != 0) goto L_0x0532;
        L_0x0118:
            if (r11 == r13) goto L_0x011e;
        L_0x011a:
            if (r11 == r1) goto L_0x011e;
        L_0x011c:
            if (r11 != r4) goto L_0x0532;
        L_0x011e:
            r5 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
            r4 = NUM; // 0x43cb8000 float:407.0 double:5.6195523E-315;
            if (r11 != r13) goto L_0x0254;
        L_0x0126:
            r1 = r10.posArray;
            r1 = r1.get(r14);
            r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1;
            r2 = r10.posArray;
            r2 = r2.get(r12);
            r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2;
            r0 = r0.toString();
            r3 = "ww";
            r7 = r0.equals(r3);
            if (r7 == 0) goto L_0x01a3;
        L_0x0143:
            r7 = (double) r8;
            r18 = NUM; // 0x3ffNUM float:2.720083E23 double:1.4;
            r26 = r15;
            r14 = (double) r6;
            java.lang.Double.isNaN(r14);
            r14 = r14 * r18;
            r6 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1));
            if (r6 <= 0) goto L_0x01a5;
        L_0x0155:
            r6 = r1.aspectRatio;
            r7 = r2.aspectRatio;
            r8 = r6 - r7;
            r14 = (double) r8;
            r18 = NUM; // 0x3fCLASSNAMEa float:-1.5881868E-23 double:0.2;
            r8 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1));
            if (r8 >= 0) goto L_0x01a5;
        L_0x0165:
            r0 = r10.maxSizeWidth;
            r3 = (float) r0;
            r3 = r3 / r6;
            r0 = (float) r0;
            r0 = r0 / r7;
            r0 = java.lang.Math.min(r0, r4);
            r0 = java.lang.Math.min(r3, r0);
            r0 = java.lang.Math.round(r0);
            r0 = (float) r0;
            r3 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r0 = r0 / r3;
            r19 = 0;
            r20 = 0;
            r21 = 0;
            r22 = 0;
            r3 = r10.maxSizeWidth;
            r25 = 7;
            r18 = r1;
            r23 = r3;
            r24 = r0;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r21 = 1;
            r22 = 1;
            r1 = r10.maxSizeWidth;
            r25 = 11;
            r18 = r2;
            r23 = r1;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r0 = 0;
            goto L_0x024e;
        L_0x01a3:
            r26 = r15;
        L_0x01a5:
            r3 = r0.equals(r3);
            if (r3 != 0) goto L_0x0213;
        L_0x01ab:
            r3 = "qq";
            r0 = r0.equals(r3);
            if (r0 == 0) goto L_0x01b4;
        L_0x01b3:
            goto L_0x0213;
        L_0x01b4:
            r0 = r10.maxSizeWidth;
            r3 = (float) r0;
            r3 = r3 * r5;
            r0 = (float) r0;
            r4 = r1.aspectRatio;
            r0 = r0 / r4;
            r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r4 = r5 / r4;
            r6 = r2.aspectRatio;
            r5 = r5 / r6;
            r4 = r4 + r5;
            r0 = r0 / r4;
            r0 = java.lang.Math.round(r0);
            r0 = (float) r0;
            r0 = java.lang.Math.max(r3, r0);
            r0 = (int) r0;
            r3 = r10.maxSizeWidth;
            r3 = r3 - r0;
            if (r3 >= r9) goto L_0x01d9;
        L_0x01d5:
            r3 = r9 - r3;
            r0 = r0 - r3;
            r3 = r9;
        L_0x01d9:
            r4 = (float) r3;
            r5 = r1.aspectRatio;
            r4 = r4 / r5;
            r5 = (float) r0;
            r6 = r2.aspectRatio;
            r5 = r5 / r6;
            r4 = java.lang.Math.min(r4, r5);
            r4 = java.lang.Math.round(r4);
            r4 = (float) r4;
            r5 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r4 = java.lang.Math.min(r5, r4);
            r4 = r4 / r5;
            r19 = 0;
            r20 = 0;
            r21 = 0;
            r22 = 0;
            r25 = 13;
            r18 = r1;
            r23 = r3;
            r24 = r4;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r19 = 1;
            r20 = 1;
            r25 = 14;
            r18 = r2;
            r23 = r0;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            goto L_0x024d;
        L_0x0213:
            r0 = r10.maxSizeWidth;
            r0 = r0 / r13;
            r3 = (float) r0;
            r4 = r1.aspectRatio;
            r4 = r3 / r4;
            r5 = r2.aspectRatio;
            r3 = r3 / r5;
            r5 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r3 = java.lang.Math.min(r3, r5);
            r3 = java.lang.Math.min(r4, r3);
            r3 = java.lang.Math.round(r3);
            r3 = (float) r3;
            r3 = r3 / r5;
            r19 = 0;
            r20 = 0;
            r21 = 0;
            r22 = 0;
            r25 = 13;
            r18 = r1;
            r23 = r0;
            r24 = r3;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r19 = 1;
            r20 = 1;
            r25 = 14;
            r18 = r2;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
        L_0x024d:
            r0 = 1;
        L_0x024e:
            r12 = r0;
        L_0x024f:
            r17 = r11;
            r8 = 0;
            goto L_0x077f;
        L_0x0254:
            r26 = r15;
            r6 = NUM; // 0x44064f5d float:537.24005 double:5.638594444E-315;
            if (r11 != r1) goto L_0x038e;
        L_0x025b:
            r1 = r10.posArray;
            r3 = 0;
            r1 = r1.get(r3);
            r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1;
            r5 = r10.posArray;
            r5 = r5.get(r12);
            r5 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r5;
            r8 = r10.posArray;
            r8 = r8.get(r13);
            r8 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r8;
            r0 = r0.charAt(r3);
            r3 = 110; // 0x6e float:1.54E-43 double:5.43E-322;
            if (r0 != r3) goto L_0x0323;
        L_0x027c:
            r0 = r5.aspectRatio;
            r3 = r10.maxSizeWidth;
            r3 = (float) r3;
            r3 = r3 * r0;
            r6 = r8.aspectRatio;
            r6 = r6 + r0;
            r3 = r3 / r6;
            r0 = java.lang.Math.round(r3);
            r0 = (float) r0;
            r0 = java.lang.Math.min(r4, r0);
            r3 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r4 = r3 - r0;
            r3 = (float) r9;
            r6 = r10.maxSizeWidth;
            r6 = (float) r6;
            r7 = NUM; // 0x3var_ float:0.5 double:5.222099017E-315;
            r6 = r6 * r7;
            r7 = r8.aspectRatio;
            r7 = r7 * r0;
            r9 = r5.aspectRatio;
            r9 = r9 * r4;
            r7 = java.lang.Math.min(r7, r9);
            r7 = java.lang.Math.round(r7);
            r7 = (float) r7;
            r6 = java.lang.Math.min(r6, r7);
            r3 = java.lang.Math.max(r3, r6);
            r3 = (int) r3;
            r6 = r1.aspectRatio;
            r7 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r6 = r6 * r7;
            r2 = (float) r2;
            r6 = r6 + r2;
            r2 = r10.maxSizeWidth;
            r2 = r2 - r3;
            r2 = (float) r2;
            r2 = java.lang.Math.min(r6, r2);
            r2 = java.lang.Math.round(r2);
            r19 = 0;
            r20 = 0;
            r21 = 0;
            r22 = 1;
            r24 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r25 = 13;
            r18 = r1;
            r23 = r2;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r19 = 1;
            r20 = 1;
            r22 = 0;
            r6 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r4 = r4 / r6;
            r25 = 6;
            r18 = r5;
            r23 = r3;
            r24 = r4;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r19 = 0;
            r21 = 1;
            r22 = 1;
            r6 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r0 = r0 / r6;
            r25 = 10;
            r18 = r8;
            r24 = r0;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r6 = r10.maxSizeWidth;
            r8.spanSize = r6;
            r7 = new float[r13];
            r9 = 0;
            r7[r9] = r0;
            r7[r12] = r4;
            r1.siblingHeights = r7;
            if (r26 == 0) goto L_0x031a;
        L_0x0316:
            r6 = r6 - r3;
            r1.spanSize = r6;
            goto L_0x031f;
        L_0x031a:
            r6 = r6 - r2;
            r5.spanSize = r6;
            r8.leftSpanOffset = r2;
        L_0x031f:
            r10.hasSibling = r12;
            goto L_0x024f;
        L_0x0323:
            r0 = r10.maxSizeWidth;
            r0 = (float) r0;
            r2 = r1.aspectRatio;
            r0 = r0 / r2;
            r0 = java.lang.Math.min(r0, r6);
            r0 = java.lang.Math.round(r0);
            r0 = (float) r0;
            r2 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r0 = r0 / r2;
            r19 = 0;
            r20 = 1;
            r21 = 0;
            r22 = 0;
            r2 = r10.maxSizeWidth;
            r25 = 7;
            r18 = r1;
            r23 = r2;
            r24 = r0;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r1 = r10.maxSizeWidth;
            r1 = r1 / r13;
            r2 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r0 = r2 - r0;
            r3 = (float) r1;
            r4 = r5.aspectRatio;
            r4 = r3 / r4;
            r6 = r8.aspectRatio;
            r3 = r3 / r6;
            r3 = java.lang.Math.min(r4, r3);
            r3 = java.lang.Math.round(r3);
            r3 = (float) r3;
            r0 = java.lang.Math.min(r0, r3);
            r0 = r0 / r2;
            r2 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1));
            if (r2 >= 0) goto L_0x036e;
        L_0x036d:
            r0 = r7;
        L_0x036e:
            r19 = 0;
            r20 = 0;
            r21 = 1;
            r22 = 1;
            r25 = 9;
            r18 = r5;
            r23 = r1;
            r24 = r0;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r19 = 1;
            r20 = 1;
            r25 = 10;
            r18 = r8;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            goto L_0x024f;
        L_0x038e:
            r4 = 4;
            if (r11 != r4) goto L_0x052c;
        L_0x0391:
            r4 = r10.posArray;
            r8 = 0;
            r4 = r4.get(r8);
            r4 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r4;
            r14 = r10.posArray;
            r14 = r14.get(r12);
            r14 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r14;
            r15 = r10.posArray;
            r15 = r15.get(r13);
            r15 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r15;
            r13 = r10.posArray;
            r13 = r13.get(r1);
            r13 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r13;
            r0 = r0.charAt(r8);
            r8 = 119; // 0x77 float:1.67E-43 double:5.9E-322;
            r12 = NUM; // 0x3ea8f5c3 float:0.33 double:5.19391626E-315;
            if (r0 != r8) goto L_0x0475;
        L_0x03bd:
            r0 = r10.maxSizeWidth;
            r0 = (float) r0;
            r1 = r4.aspectRatio;
            r0 = r0 / r1;
            r0 = java.lang.Math.min(r0, r6);
            r0 = java.lang.Math.round(r0);
            r0 = (float) r0;
            r1 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r0 = r0 / r1;
            r19 = 0;
            r20 = 2;
            r21 = 0;
            r22 = 0;
            r1 = r10.maxSizeWidth;
            r25 = 7;
            r18 = r4;
            r23 = r1;
            r24 = r0;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r1 = r10.maxSizeWidth;
            r1 = (float) r1;
            r2 = r14.aspectRatio;
            r3 = r15.aspectRatio;
            r2 = r2 + r3;
            r3 = r13.aspectRatio;
            r2 = r2 + r3;
            r1 = r1 / r2;
            r1 = java.lang.Math.round(r1);
            r1 = (float) r1;
            r2 = (float) r9;
            r3 = r10.maxSizeWidth;
            r3 = (float) r3;
            r3 = r3 * r5;
            r4 = r14.aspectRatio;
            r4 = r4 * r1;
            r3 = java.lang.Math.min(r3, r4);
            r3 = java.lang.Math.max(r2, r3);
            r3 = (int) r3;
            r4 = r10.maxSizeWidth;
            r4 = (float) r4;
            r4 = r4 * r12;
            r2 = java.lang.Math.max(r2, r4);
            r4 = r13.aspectRatio;
            r4 = r4 * r1;
            r2 = java.lang.Math.max(r2, r4);
            r2 = (int) r2;
            r4 = r10.maxSizeWidth;
            r4 = r4 - r3;
            r4 = r4 - r2;
            r5 = NUM; // 0x42680000 float:58.0 double:5.50444465E-315;
            r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
            if (r4 >= r6) goto L_0x0435;
        L_0x0427:
            r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r6 = r6 - r4;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r5 = r6 / 2;
            r3 = r3 - r5;
            r6 = r6 - r5;
            r2 = r2 - r6;
        L_0x0435:
            r23 = r3;
            r3 = r2;
            r2 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r0 = r2 - r0;
            r0 = java.lang.Math.min(r0, r1);
            r0 = r0 / r2;
            r1 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1));
            if (r1 >= 0) goto L_0x0447;
        L_0x0446:
            r0 = r7;
        L_0x0447:
            r19 = 0;
            r20 = 0;
            r21 = 1;
            r22 = 1;
            r25 = 9;
            r18 = r14;
            r24 = r0;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r19 = 1;
            r20 = 1;
            r25 = 8;
            r18 = r15;
            r23 = r4;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r19 = 2;
            r20 = 2;
            r25 = 10;
            r18 = r13;
            r23 = r3;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r12 = 2;
            goto L_0x024f;
        L_0x0475:
            r0 = r14.aspectRatio;
            r5 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r0 = r5 / r0;
            r6 = r15.aspectRatio;
            r6 = r5 / r6;
            r0 = r0 + r6;
            r6 = r13.aspectRatio;
            r6 = r5 / r6;
            r0 = r0 + r6;
            r5 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r0 = r5 / r0;
            r0 = java.lang.Math.round(r0);
            r0 = java.lang.Math.max(r9, r0);
            r3 = (float) r3;
            r6 = (float) r0;
            r7 = r14.aspectRatio;
            r7 = r6 / r7;
            r7 = java.lang.Math.max(r3, r7);
            r7 = r7 / r5;
            r7 = java.lang.Math.min(r12, r7);
            r8 = r15.aspectRatio;
            r6 = r6 / r8;
            r3 = java.lang.Math.max(r3, r6);
            r3 = r3 / r5;
            r3 = java.lang.Math.min(r12, r3);
            r6 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r6 = r6 - r7;
            r6 = r6 - r3;
            r8 = r4.aspectRatio;
            r5 = r5 * r8;
            r2 = (float) r2;
            r5 = r5 + r2;
            r2 = r10.maxSizeWidth;
            r2 = r2 - r0;
            r2 = (float) r2;
            r2 = java.lang.Math.min(r5, r2);
            r2 = java.lang.Math.round(r2);
            r19 = 0;
            r20 = 0;
            r21 = 0;
            r22 = 2;
            r5 = r7 + r3;
            r24 = r5 + r6;
            r25 = 13;
            r18 = r4;
            r23 = r2;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r19 = 1;
            r20 = 1;
            r22 = 0;
            r25 = 6;
            r18 = r14;
            r23 = r0;
            r24 = r7;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r19 = 0;
            r21 = 1;
            r22 = 1;
            r25 = 2;
            r18 = r15;
            r24 = r3;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r5 = r10.maxSizeWidth;
            r15.spanSize = r5;
            r21 = 2;
            r22 = 2;
            r25 = 10;
            r18 = r13;
            r24 = r6;
            r18.set(r19, r20, r21, r22, r23, r24, r25);
            r5 = r10.maxSizeWidth;
            r13.spanSize = r5;
            if (r26 == 0) goto L_0x0513;
        L_0x050f:
            r5 = r5 - r0;
            r4.spanSize = r5;
            goto L_0x051a;
        L_0x0513:
            r5 = r5 - r2;
            r14.spanSize = r5;
            r15.leftSpanOffset = r2;
            r13.leftSpanOffset = r2;
        L_0x051a:
            r0 = new float[r1];
            r1 = 0;
            r0[r1] = r7;
            r1 = 1;
            r0[r1] = r3;
            r2 = 2;
            r0[r2] = r6;
            r4.siblingHeights = r0;
            r10.hasSibling = r1;
            r12 = 1;
            goto L_0x024f;
        L_0x052c:
            r17 = r11;
            r8 = 0;
            r12 = 0;
            goto L_0x077f;
        L_0x0532:
            r26 = r15;
            r0 = r10.posArray;
            r0 = r0.size();
            r12 = new float[r0];
            r0 = 0;
        L_0x053d:
            if (r0 >= r11) goto L_0x0580;
        L_0x053f:
            r2 = NUM; // 0x3f8ccccd float:1.1 double:5.26768877E-315;
            r2 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1));
            if (r2 <= 0) goto L_0x0559;
        L_0x0546:
            r2 = r10.posArray;
            r2 = r2.get(r0);
            r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2;
            r2 = r2.aspectRatio;
            r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r2 = java.lang.Math.max(r3, r2);
            r12[r0] = r2;
            goto L_0x056b;
        L_0x0559:
            r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r2 = r10.posArray;
            r2 = r2.get(r0);
            r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2;
            r2 = r2.aspectRatio;
            r2 = java.lang.Math.min(r3, r2);
            r12[r0] = r2;
        L_0x056b:
            r2 = NUM; // 0x3f2aaae3 float:0.66667 double:5.23591437E-315;
            r5 = NUM; // 0x3fd9999a float:1.7 double:5.29255591E-315;
            r6 = r12[r0];
            r5 = java.lang.Math.min(r5, r6);
            r2 = java.lang.Math.max(r2, r5);
            r12[r0] = r2;
            r0 = r0 + 1;
            goto L_0x053d;
        L_0x0580:
            r13 = new java.util.ArrayList;
            r13.<init>();
            r6 = 1;
        L_0x0586:
            r0 = r12.length;
            if (r6 >= r0) goto L_0x05bc;
        L_0x0589:
            r0 = r12.length;
            r3 = r0 - r6;
            if (r6 > r1) goto L_0x05b0;
        L_0x058e:
            if (r3 <= r1) goto L_0x0591;
        L_0x0590:
            goto L_0x05b0;
        L_0x0591:
            r14 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt;
            r0 = 0;
            r5 = r10.multiHeight(r12, r0, r6);
            r0 = r12.length;
            r15 = r10.multiHeight(r12, r6, r0);
            r0 = r14;
            r2 = 3;
            r1 = r36;
            r17 = r11;
            r11 = 3;
            r2 = r6;
            r18 = 4;
            r4 = r5;
            r5 = r15;
            r0.<init>(r2, r3, r4, r5);
            r13.add(r14);
            goto L_0x05b5;
        L_0x05b0:
            r17 = r11;
            r11 = 3;
            r18 = 4;
        L_0x05b5:
            r6 = r6 + 1;
            r11 = r17;
            r1 = 3;
            r4 = 4;
            goto L_0x0586;
        L_0x05bc:
            r17 = r11;
            r11 = 3;
            r18 = 4;
            r14 = 1;
        L_0x05c2:
            r0 = r12.length;
            r1 = 1;
            r0 = r0 - r1;
            if (r14 >= r0) goto L_0x0614;
        L_0x05c7:
            r15 = 1;
        L_0x05c8:
            r0 = r12.length;
            r0 = r0 - r14;
            if (r15 >= r0) goto L_0x060e;
        L_0x05cc:
            r0 = r12.length;
            r0 = r0 - r14;
            r4 = r0 - r15;
            if (r14 > r11) goto L_0x0606;
        L_0x05d2:
            r0 = NUM; // 0x3var_a float:0.85 double:5.25111068E-315;
            r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
            if (r0 >= 0) goto L_0x05db;
        L_0x05d9:
            r0 = 4;
            goto L_0x05dc;
        L_0x05db:
            r0 = 3;
        L_0x05dc:
            if (r15 > r0) goto L_0x0606;
        L_0x05de:
            if (r4 <= r11) goto L_0x05e1;
        L_0x05e0:
            goto L_0x0606;
        L_0x05e1:
            r6 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt;
            r0 = 0;
            r5 = r10.multiHeight(r12, r0, r14);
            r0 = r14 + r15;
            r19 = r10.multiHeight(r12, r14, r0);
            r1 = r12.length;
            r20 = r10.multiHeight(r12, r0, r1);
            r0 = r6;
            r1 = r36;
            r2 = r14;
            r3 = r15;
            r11 = r6;
            r6 = r19;
            r27 = r7;
            r7 = r20;
            r0.<init>(r2, r3, r4, r5, r6, r7);
            r13.add(r11);
            goto L_0x0608;
        L_0x0606:
            r27 = r7;
        L_0x0608:
            r15 = r15 + 1;
            r7 = r27;
            r11 = 3;
            goto L_0x05c8;
        L_0x060e:
            r27 = r7;
            r14 = r14 + 1;
            r11 = 3;
            goto L_0x05c2;
        L_0x0614:
            r27 = r7;
            r11 = 1;
        L_0x0617:
            r0 = r12.length;
            r1 = 2;
            r0 = r0 - r1;
            if (r11 >= r0) goto L_0x067d;
        L_0x061c:
            r14 = 1;
        L_0x061d:
            r0 = r12.length;
            r0 = r0 - r11;
            if (r14 >= r0) goto L_0x0676;
        L_0x0621:
            r15 = 1;
        L_0x0622:
            r0 = r12.length;
            r0 = r0 - r11;
            r0 = r0 - r14;
            if (r15 >= r0) goto L_0x066f;
        L_0x0627:
            r0 = r12.length;
            r0 = r0 - r11;
            r0 = r0 - r14;
            r5 = r0 - r15;
            r0 = 3;
            if (r11 > r0) goto L_0x0664;
        L_0x062f:
            if (r14 > r0) goto L_0x0664;
        L_0x0631:
            if (r15 > r0) goto L_0x0664;
        L_0x0633:
            if (r5 <= r0) goto L_0x0636;
        L_0x0635:
            goto L_0x0664;
        L_0x0636:
            r8 = new org.telegram.messenger.MessageObject$GroupedMessages$MessageGroupedLayoutAttempt;
            r0 = 0;
            r6 = r10.multiHeight(r12, r0, r11);
            r0 = r11 + r14;
            r7 = r10.multiHeight(r12, r11, r0);
            r1 = r0 + r15;
            r19 = r10.multiHeight(r12, r0, r1);
            r0 = r12.length;
            r20 = r10.multiHeight(r12, r1, r0);
            r0 = r8;
            r1 = r36;
            r2 = r11;
            r3 = r14;
            r4 = r15;
            r22 = r12;
            r12 = r8;
            r8 = r19;
            r28 = r9;
            r9 = r20;
            r0.<init>(r2, r3, r4, r5, r6, r7, r8, r9);
            r13.add(r12);
            goto L_0x0668;
        L_0x0664:
            r28 = r9;
            r22 = r12;
        L_0x0668:
            r15 = r15 + 1;
            r12 = r22;
            r9 = r28;
            goto L_0x0622;
        L_0x066f:
            r28 = r9;
            r22 = r12;
            r14 = r14 + 1;
            goto L_0x061d;
        L_0x0676:
            r28 = r9;
            r22 = r12;
            r11 = r11 + 1;
            goto L_0x0617;
        L_0x067d:
            r28 = r9;
            r22 = r12;
            r0 = 0;
            r1 = 0;
            r2 = r10.maxSizeWidth;
            r3 = 3;
            r2 = r2 / r3;
            r2 = r2 * 4;
            r2 = (float) r2;
            r1 = r0;
            r0 = 0;
            r3 = 0;
        L_0x068d:
            r4 = r13.size();
            if (r0 >= r4) goto L_0x06fd;
        L_0x0693:
            r4 = r13.get(r0);
            r4 = (org.telegram.messenger.MessageObject.GroupedMessages.MessageGroupedLayoutAttempt) r4;
            r5 = 0;
            r6 = NUM; // 0x7f7fffff float:3.4028235E38 double:1.056853372E-314;
            r5 = 0;
            r6 = 0;
            r7 = NUM; // 0x7f7fffff float:3.4028235E38 double:1.056853372E-314;
        L_0x06a2:
            r8 = r4.heights;
            r9 = r8.length;
            if (r5 >= r9) goto L_0x06b5;
        L_0x06a7:
            r9 = r8[r5];
            r6 = r6 + r9;
            r9 = r8[r5];
            r9 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1));
            if (r9 >= 0) goto L_0x06b2;
        L_0x06b0:
            r7 = r8[r5];
        L_0x06b2:
            r5 = r5 + 1;
            goto L_0x06a2;
        L_0x06b5:
            r6 = r6 - r2;
            r5 = java.lang.Math.abs(r6);
            r6 = r4.lineCounts;
            r8 = r6.length;
            r9 = 1;
            if (r8 <= r9) goto L_0x06e2;
        L_0x06c0:
            r8 = 0;
            r11 = r6[r8];
            r12 = r6[r9];
            if (r11 > r12) goto L_0x06de;
        L_0x06c7:
            r11 = r6.length;
            r12 = 2;
            if (r11 <= r12) goto L_0x06d1;
        L_0x06cb:
            r11 = r6[r9];
            r6 = r6[r12];
            if (r11 > r6) goto L_0x06de;
        L_0x06d1:
            r6 = r4.lineCounts;
            r9 = r6.length;
            r11 = 3;
            if (r9 <= r11) goto L_0x06e4;
        L_0x06d7:
            r9 = r6[r12];
            r6 = r6[r11];
            if (r9 <= r6) goto L_0x06e4;
        L_0x06dd:
            goto L_0x06df;
        L_0x06de:
            r11 = 3;
        L_0x06df:
            r5 = r5 * r16;
            goto L_0x06e4;
        L_0x06e2:
            r8 = 0;
            r11 = 3;
        L_0x06e4:
            r6 = r5;
            r5 = r28;
            r9 = (float) r5;
            r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
            if (r7 >= 0) goto L_0x06f0;
        L_0x06ec:
            r7 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
            r6 = r6 * r7;
        L_0x06f0:
            if (r1 == 0) goto L_0x06f6;
        L_0x06f2:
            r7 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1));
            if (r7 >= 0) goto L_0x06f8;
        L_0x06f6:
            r1 = r4;
            r3 = r6;
        L_0x06f8:
            r0 = r0 + 1;
            r28 = r5;
            goto L_0x068d;
        L_0x06fd:
            r8 = 0;
            if (r1 != 0) goto L_0x0701;
        L_0x0700:
            return;
        L_0x0701:
            r0 = 0;
            r2 = 0;
            r12 = 0;
        L_0x0704:
            r3 = r1.lineCounts;
            r4 = r3.length;
            if (r0 >= r4) goto L_0x077f;
        L_0x0709:
            r3 = r3[r0];
            r4 = r1.heights;
            r4 = r4[r0];
            r5 = r10.maxSizeWidth;
            r6 = 0;
            r7 = r3 + -1;
            r12 = java.lang.Math.max(r12, r7);
            r9 = r2;
            r2 = 0;
        L_0x071a:
            if (r2 >= r3) goto L_0x076c;
        L_0x071c:
            r11 = r22[r9];
            r11 = r11 * r4;
            r11 = (int) r11;
            r5 = r5 - r11;
            r13 = r10.posArray;
            r13 = r13.get(r9);
            r28 = r13;
            r28 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r28;
            if (r0 != 0) goto L_0x0730;
        L_0x072e:
            r13 = 4;
            goto L_0x0731;
        L_0x0730:
            r13 = 0;
        L_0x0731:
            r14 = r1.lineCounts;
            r14 = r14.length;
            r15 = 1;
            r14 = r14 - r15;
            if (r0 != r14) goto L_0x073a;
        L_0x0738:
            r13 = r13 | 8;
        L_0x073a:
            if (r2 != 0) goto L_0x0742;
        L_0x073c:
            r13 = r13 | 1;
            if (r26 == 0) goto L_0x0742;
        L_0x0740:
            r6 = r28;
        L_0x0742:
            if (r2 != r7) goto L_0x074d;
        L_0x0744:
            r13 = r13 | 2;
            if (r26 != 0) goto L_0x074d;
        L_0x0748:
            r35 = r13;
            r6 = r28;
            goto L_0x074f;
        L_0x074d:
            r35 = r13;
        L_0x074f:
            r13 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r14 = r4 / r13;
            r15 = r27;
            r34 = java.lang.Math.max(r15, r14);
            r29 = r2;
            r30 = r2;
            r31 = r0;
            r32 = r0;
            r33 = r11;
            r28.set(r29, r30, r31, r32, r33, r34, r35);
            r9 = r9 + 1;
            r2 = r2 + 1;
            goto L_0x071a;
        L_0x076c:
            r15 = r27;
            r13 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r2 = r6.pw;
            r2 = r2 + r5;
            r6.pw = r2;
            r2 = r6.spanSize;
            r2 = r2 + r5;
            r6.spanSize = r2;
            r0 = r0 + 1;
            r2 = r9;
            goto L_0x0704;
        L_0x077f:
            r0 = r17;
        L_0x0781:
            if (r8 >= r0) goto L_0x07ff;
        L_0x0783:
            r1 = r10.posArray;
            r1 = r1.get(r8);
            r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1;
            if (r26 == 0) goto L_0x07a3;
        L_0x078d:
            r2 = r1.minX;
            if (r2 != 0) goto L_0x0798;
        L_0x0791:
            r2 = r1.spanSize;
            r3 = r10.firstSpanAdditionalSize;
            r2 = r2 + r3;
            r1.spanSize = r2;
        L_0x0798:
            r2 = r1.flags;
            r3 = 2;
            r2 = r2 & r3;
            if (r2 == 0) goto L_0x07a1;
        L_0x079e:
            r2 = 1;
            r1.edge = r2;
        L_0x07a1:
            r3 = 1;
            goto L_0x07bc;
        L_0x07a3:
            r3 = 2;
            r2 = r1.maxX;
            if (r2 == r12) goto L_0x07ad;
        L_0x07a8:
            r2 = r1.flags;
            r2 = r2 & r3;
            if (r2 == 0) goto L_0x07b4;
        L_0x07ad:
            r2 = r1.spanSize;
            r3 = r10.firstSpanAdditionalSize;
            r2 = r2 + r3;
            r1.spanSize = r2;
        L_0x07b4:
            r2 = r1.flags;
            r3 = 1;
            r2 = r2 & r3;
            if (r2 == 0) goto L_0x07bc;
        L_0x07ba:
            r1.edge = r3;
        L_0x07bc:
            r2 = r10.messages;
            r2 = r2.get(r8);
            r2 = (org.telegram.messenger.MessageObject) r2;
            if (r26 != 0) goto L_0x07fb;
        L_0x07c6:
            r2 = r2.needDrawAvatarInternal();
            if (r2 == 0) goto L_0x07fb;
        L_0x07cc:
            r2 = r1.edge;
            if (r2 == 0) goto L_0x07e1;
        L_0x07d0:
            r2 = r1.spanSize;
            r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            if (r2 == r4) goto L_0x07da;
        L_0x07d6:
            r2 = r2 + 108;
            r1.spanSize = r2;
        L_0x07da:
            r2 = r1.pw;
            r2 = r2 + 108;
            r1.pw = r2;
            goto L_0x07fb;
        L_0x07e1:
            r2 = r1.flags;
            r4 = 2;
            r2 = r2 & r4;
            if (r2 == 0) goto L_0x07fc;
        L_0x07e7:
            r2 = r1.spanSize;
            r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            if (r2 == r5) goto L_0x07f2;
        L_0x07ed:
            r2 = r2 + -108;
            r1.spanSize = r2;
            goto L_0x07fc;
        L_0x07f2:
            r2 = r1.leftSpanOffset;
            if (r2 == 0) goto L_0x07fc;
        L_0x07f6:
            r2 = r2 + 108;
            r1.leftSpanOffset = r2;
            goto L_0x07fc;
        L_0x07fb:
            r4 = 2;
        L_0x07fc:
            r8 = r8 + 1;
            goto L_0x0781;
        L_0x07ff:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject$GroupedMessages.calculate():void");
        }

        public MessageObject findPrimaryMessageObject() {
            if (!this.messages.isEmpty() && this.positions.isEmpty()) {
                calculate();
            }
            for (int i = 0; i < this.messages.size(); i++) {
                MessageObject messageObject = (MessageObject) this.messages.get(i);
                GroupedMessagePosition groupedMessagePosition = (GroupedMessagePosition) this.positions.get(messageObject);
                if (groupedMessagePosition != null && (groupedMessagePosition.flags & 5) != 0) {
                    return messageObject;
                }
            }
            return null;
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

    public static class VCardData {
        private String company;
        private ArrayList<String> emails = new ArrayList();
        private ArrayList<String> phones = new ArrayList();

        public static CharSequence parse(String str) {
            int i;
            Object obj;
            VCardData vCardData;
            try {
                BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
                i = 0;
                obj = null;
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
                                obj = 1;
                            }
                        }
                        if (str2 != null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append(str2);
                            stringBuilder.append(readLine);
                            readLine = stringBuilder.toString();
                            str2 = null;
                        }
                        String str3 = "=";
                        if (readLine.contains("=QUOTED-PRINTABLE")) {
                            if (readLine.endsWith(str3)) {
                                str2 = readLine.substring(0, readLine.length() - 1);
                            }
                        }
                        int i2 = 2;
                        String[] strArr = readLine.indexOf(":") >= 0 ? new String[]{readLine.substring(0, readLine.indexOf(":")), readLine.substring(readLine.indexOf(":") + 1).trim()} : new String[]{readLine.trim()};
                        if (strArr.length >= 2) {
                            if (vCardData != null) {
                                if (strArr[0].startsWith("ORG")) {
                                    String[] split = strArr[0].split(";");
                                    int length = split.length;
                                    int i3 = 0;
                                    String str4 = null;
                                    String str5 = null;
                                    while (i3 < length) {
                                        String[] split2 = split[i3].split(str3);
                                        if (split2.length == i2) {
                                            if (split2[0].equals("CHARSET")) {
                                                str5 = split2[1];
                                            } else if (split2[0].equals("ENCODING")) {
                                                str4 = split2[1];
                                            }
                                        }
                                        i3++;
                                        i2 = 2;
                                    }
                                    vCardData.company = strArr[1];
                                    if (str4 != null && str4.equalsIgnoreCase("QUOTED-PRINTABLE")) {
                                        byte[] decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(vCardData.company));
                                        if (!(decodeQuotedPrintable == null || decodeQuotedPrintable.length == 0)) {
                                            vCardData.company = new String(decodeQuotedPrintable, str5);
                                        }
                                    }
                                    vCardData.company = vCardData.company.replace(';', ' ');
                                } else if (strArr[0].startsWith("TEL")) {
                                    if (strArr[1].length() > 0) {
                                        vCardData.phones.add(strArr[1]);
                                    }
                                } else if (strArr[0].startsWith("EMAIL")) {
                                    String str6 = strArr[1];
                                    if (str6.length() > 0) {
                                        vCardData.emails.add(str6);
                                    }
                                }
                            }
                        }
                    }
                }
                bufferedReader.close();
            } catch (Exception e) {
                FileLog.e(e);
            } catch (Throwable unused) {
            }
            if (obj != null) {
                StringBuilder stringBuilder2 = new StringBuilder();
                for (int i4 = 0; i4 < vCardData.phones.size(); i4++) {
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2.append(10);
                    }
                    String str7 = (String) vCardData.phones.get(i4);
                    if (!str7.contains("#")) {
                        if (!str7.contains("*")) {
                            stringBuilder2.append(PhoneFormat.getInstance().format(str7));
                        }
                    }
                    stringBuilder2.append(str7);
                }
                while (i < vCardData.emails.size()) {
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2.append(10);
                    }
                    stringBuilder2.append(PhoneFormat.getInstance().format((String) vCardData.emails.get(i)));
                    i++;
                }
                if (!TextUtils.isEmpty(vCardData.company)) {
                    if (stringBuilder2.length() > 0) {
                        stringBuilder2.append(10);
                    }
                    stringBuilder2.append(vCardData.company);
                }
                return stringBuilder2;
            }
            return null;
        }
    }

    public void checkForScam() {
    }

    public MessageObject(int i, Message message, String str, String str2, String str3, boolean z, boolean z2, boolean z3) {
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.localType = z ? 2 : 1;
        this.currentAccount = i;
        this.localName = str2;
        this.localUserName = str3;
        this.messageText = str;
        this.messageOwner = message;
        this.localChannel = z2;
        this.localEdit = z3;
    }

    public MessageObject(int i, Message message, AbstractMap<Integer, User> abstractMap, boolean z) {
        this(i, message, (AbstractMap) abstractMap, null, z);
    }

    public MessageObject(int i, Message message, SparseArray<User> sparseArray, boolean z) {
        this(i, message, (SparseArray) sparseArray, null, z);
    }

    public MessageObject(int i, Message message, boolean z) {
        this(i, message, null, null, null, null, null, z, 0);
    }

    public MessageObject(int i, Message message, MessageObject messageObject, boolean z) {
        this(i, message, messageObject, null, null, null, null, z, 0);
    }

    public MessageObject(int i, Message message, AbstractMap<Integer, User> abstractMap, AbstractMap<Integer, Chat> abstractMap2, boolean z) {
        this(i, message, (AbstractMap) abstractMap, (AbstractMap) abstractMap2, z, 0);
    }

    public MessageObject(int i, Message message, SparseArray<User> sparseArray, SparseArray<Chat> sparseArray2, boolean z) {
        this(i, message, null, null, null, sparseArray, sparseArray2, z, 0);
    }

    public MessageObject(int i, Message message, AbstractMap<Integer, User> abstractMap, AbstractMap<Integer, Chat> abstractMap2, boolean z, long j) {
        this(i, message, null, abstractMap, abstractMap2, null, null, z, j);
    }

    public MessageObject(int i, Message message, MessageObject messageObject, AbstractMap<Integer, User> abstractMap, AbstractMap<Integer, Chat> abstractMap2, SparseArray<User> sparseArray, SparseArray<Chat> sparseArray2, boolean z, long j) {
        User user;
        AbstractMap abstractMap3;
        SparseArray sparseArray3;
        Message message2 = message;
        AbstractMap<Integer, User> abstractMap4 = abstractMap;
        SparseArray<User> sparseArray4 = sparseArray;
        boolean z2 = z;
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        Theme.createChatResources(null, true);
        this.currentAccount = i;
        this.messageOwner = message2;
        this.replyMessageObject = messageObject;
        this.eventId = j;
        Message message3 = message2.replyMessage;
        if (message3 != null) {
            MessageObject messageObject2 = r2;
            MessageObject messageObject3 = new MessageObject(this.currentAccount, message3, null, abstractMap, abstractMap2, sparseArray, sparseArray2, false, j);
            this.replyMessageObject = messageObject2;
        }
        int i2 = message2.from_id;
        if (i2 > 0) {
            user = abstractMap4 != null ? (User) abstractMap4.get(Integer.valueOf(i2)) : sparseArray4 != null ? (User) sparseArray4.get(i2) : null;
            if (user == null) {
                user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(message2.from_id));
            }
            abstractMap3 = abstractMap2;
            sparseArray3 = sparseArray2;
        } else {
            abstractMap3 = abstractMap2;
            sparseArray3 = sparseArray2;
            user = null;
        }
        updateMessageText(abstractMap4, abstractMap3, sparseArray4, sparseArray3);
        setType();
        measureInlineBotButtons();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(((long) this.messageOwner.date) * 1000);
        int i3 = gregorianCalendar.get(6);
        int i4 = gregorianCalendar.get(1);
        i2 = gregorianCalendar.get(2);
        this.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i2), Integer.valueOf(i3)});
        this.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i2)});
        createMessageSendInfo();
        generateCaption();
        if (z2) {
            TextPaint textPaint;
            if (this.messageOwner.media instanceof TL_messageMediaGame) {
                textPaint = Theme.chat_msgGameTextPaint;
            } else {
                textPaint = Theme.chat_msgTextPaint;
            }
            int[] iArr = allowsBigEmoji() ? new int[1] : null;
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            checkEmojiOnly(iArr);
            this.emojiAnimatedSticker = null;
            if (this.emojiOnlyCount == 1 && !(message2.media instanceof TL_messageMediaWebPage) && message2.entities.isEmpty()) {
                CharSequence charSequence = this.messageText;
                i2 = TextUtils.indexOf(charSequence, "🏻");
                if (i2 >= 0) {
                    this.emojiAnimatedStickerColor = "_c1";
                    charSequence = charSequence.subSequence(0, i2);
                } else {
                    i2 = TextUtils.indexOf(charSequence, "🏼");
                    if (i2 >= 0) {
                        this.emojiAnimatedStickerColor = "_c2";
                        charSequence = charSequence.subSequence(0, i2);
                    } else {
                        i2 = TextUtils.indexOf(charSequence, "🏽");
                        if (i2 >= 0) {
                            this.emojiAnimatedStickerColor = "_c3";
                            charSequence = charSequence.subSequence(0, i2);
                        } else {
                            i2 = TextUtils.indexOf(charSequence, "🏾");
                            if (i2 >= 0) {
                                this.emojiAnimatedStickerColor = "_c4";
                                charSequence = charSequence.subSequence(0, i2);
                            } else {
                                i2 = TextUtils.indexOf(charSequence, "🏿");
                                if (i2 >= 0) {
                                    this.emojiAnimatedStickerColor = "_c5";
                                    charSequence = charSequence.subSequence(0, i2);
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
                generateLayout(user);
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

    private void createDateArray(int i, TL_channelAdminLogEvent tL_channelAdminLogEvent, ArrayList<MessageObject> arrayList, HashMap<String, ArrayList<MessageObject>> hashMap) {
        if (((ArrayList) hashMap.get(this.dateKey)) == null) {
            hashMap.put(this.dateKey, new ArrayList());
            TL_message tL_message = new TL_message();
            tL_message.message = LocaleController.formatDateChat((long) tL_channelAdminLogEvent.date);
            tL_message.id = 0;
            tL_message.date = tL_channelAdminLogEvent.date;
            MessageObject messageObject = new MessageObject(i, tL_message, false);
            messageObject.type = 10;
            messageObject.contentType = 1;
            messageObject.isDateObject = true;
            arrayList.add(messageObject);
        }
    }

    private void checkEmojiOnly(int[] iArr) {
        if (iArr != null) {
            int i = 0;
            if (iArr[0] >= 1 && iArr[0] <= 3) {
                TextPaint textPaint;
                int dp;
                int i2 = iArr[0];
                if (i2 == 1) {
                    textPaint = Theme.chat_msgTextPaintOneEmoji;
                    dp = AndroidUtilities.dp(32.0f);
                    this.emojiOnlyCount = 1;
                } else if (i2 != 2) {
                    textPaint = Theme.chat_msgTextPaintThreeEmoji;
                    dp = AndroidUtilities.dp(24.0f);
                    this.emojiOnlyCount = 3;
                } else {
                    textPaint = Theme.chat_msgTextPaintTwoEmoji;
                    int dp2 = AndroidUtilities.dp(28.0f);
                    this.emojiOnlyCount = 2;
                    dp = dp2;
                }
                CharSequence charSequence = this.messageText;
                EmojiSpan[] emojiSpanArr = (EmojiSpan[]) ((Spannable) charSequence).getSpans(0, charSequence.length(), EmojiSpan.class);
                if (emojiSpanArr != null && emojiSpanArr.length > 0) {
                    while (i < emojiSpanArr.length) {
                        emojiSpanArr[i].replaceFontMetrics(textPaint.getFontMetricsInt(), dp);
                        i++;
                    }
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:247:0x06b5  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x06a1  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06bb A:{LOOP_END, LOOP:0: B:228:0x0674->B:250:0x06bb} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x06d5 A:{SYNTHETIC, EDGE_INSN: B:552:0x06d5->B:252:0x06d5 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x06a1  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x06b5  */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x06d5 A:{SYNTHETIC, EDGE_INSN: B:552:0x06d5->B:252:0x06d5 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06bb A:{LOOP_END, LOOP:0: B:228:0x0674->B:250:0x06bb} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x06b5  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x06a1  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06bb A:{LOOP_END, LOOP:0: B:228:0x0674->B:250:0x06bb} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x06d5 A:{SYNTHETIC, EDGE_INSN: B:552:0x06d5->B:252:0x06d5 ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x0cf2  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0ce2  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d07  */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x0e7b  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0eba  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0ed2  */
    /* JADX WARNING: Removed duplicated region for block: B:554:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0var_  */
    /* JADX WARNING: Missing block: B:455:0x0cb2, code skipped:
            if (r9.id != r10.id) goto L_0x0ccb;
     */
    /* JADX WARNING: Missing block: B:461:0x0cc6, code skipped:
            if (r9.id != r10.id) goto L_0x0ccb;
     */
    public MessageObject(int r26, org.telegram.tgnet.TLRPC.TL_channelAdminLogEvent r27, java.util.ArrayList<org.telegram.messenger.MessageObject> r28, java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.MessageObject>> r29, org.telegram.tgnet.TLRPC.Chat r30, int[] r31) {
        /*
        r25 = this;
        r0 = r25;
        r1 = r27;
        r2 = r28;
        r3 = r29;
        r4 = r30;
        r25.<init>();
        r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r0.type = r5;
        r5 = -NUM; // 0xffffffffbvar_ float:-1.0 double:NaN;
        r0.forceSeekTo = r5;
        r0.currentEvent = r1;
        r5 = r26;
        r0.currentAccount = r5;
        r5 = r1.user_id;
        if (r5 <= 0) goto L_0x0030;
    L_0x001f:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r7 = r1.user_id;
        r7 = java.lang.Integer.valueOf(r7);
        r5 = r5.getUser(r7);
        goto L_0x0031;
    L_0x0030:
        r5 = 0;
    L_0x0031:
        r7 = new java.util.GregorianCalendar;
        r7.<init>();
        r8 = r1.date;
        r8 = (long) r8;
        r10 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r8 = r8 * r10;
        r7.setTimeInMillis(r8);
        r8 = 6;
        r8 = r7.get(r8);
        r9 = 1;
        r10 = r7.get(r9);
        r11 = 2;
        r7 = r7.get(r11);
        r12 = 3;
        r13 = new java.lang.Object[r12];
        r14 = java.lang.Integer.valueOf(r10);
        r15 = 0;
        r13[r15] = r14;
        r14 = java.lang.Integer.valueOf(r7);
        r13[r9] = r14;
        r8 = java.lang.Integer.valueOf(r8);
        r13[r11] = r8;
        r8 = "%d_%02d_%02d";
        r8 = java.lang.String.format(r8, r13);
        r0.dateKey = r8;
        r8 = new java.lang.Object[r11];
        r10 = java.lang.Integer.valueOf(r10);
        r8[r15] = r10;
        r7 = java.lang.Integer.valueOf(r7);
        r8[r9] = r7;
        r7 = "%d_%02d";
        r7 = java.lang.String.format(r7, r8);
        r0.monthKey = r7;
        r7 = new org.telegram.tgnet.TLRPC$TL_peerChannel;
        r7.<init>();
        r8 = r4.id;
        r7.channel_id = r8;
        r8 = r1.action;
        r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle;
        r13 = "";
        r14 = "un1";
        if (r10 == 0) goto L_0x00c9;
    L_0x0097:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle) r8;
        r7 = r8.new_value;
        r8 = r4.megagroup;
        if (r8 == 0) goto L_0x00b4;
    L_0x009f:
        r8 = NUM; // 0x7f0e045e float:1.8877305E38 double:1.053162709E-314;
        r10 = new java.lang.Object[r9];
        r10[r15] = r7;
        r7 = "EventLogEditedGroupTitle";
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e76;
    L_0x00b4:
        r8 = NUM; // 0x7f0e045b float:1.88773E38 double:1.0531627075E-314;
        r10 = new java.lang.Object[r9];
        r10[r15] = r7;
        r7 = "EventLogEditedChannelTitle";
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e76;
    L_0x00c9:
        r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangePhoto;
        if (r10 == 0) goto L_0x0144;
    L_0x00cd:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageService;
        r7.<init>();
        r0.messageOwner = r7;
        r7 = r1.action;
        r7 = r7.new_photo;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty;
        if (r7 == 0) goto L_0x010b;
    L_0x00dc:
        r7 = r0.messageOwner;
        r8 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto;
        r8.<init>();
        r7.action = r8;
        r7 = r4.megagroup;
        if (r7 == 0) goto L_0x00fa;
    L_0x00e9:
        r7 = NUM; // 0x7f0e048f float:1.8877404E38 double:1.053162733E-314;
        r8 = "EventLogRemovedWGroupPhoto";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e76;
    L_0x00fa:
        r7 = NUM; // 0x7f0e0489 float:1.8877392E38 double:1.05316273E-314;
        r8 = "EventLogRemovedChannelPhoto";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e76;
    L_0x010b:
        r7 = r0.messageOwner;
        r8 = new org.telegram.tgnet.TLRPC$TL_messageActionChatEditPhoto;
        r8.<init>();
        r7.action = r8;
        r7 = r0.messageOwner;
        r7 = r7.action;
        r8 = r1.action;
        r8 = r8.new_photo;
        r7.photo = r8;
        r7 = r4.megagroup;
        if (r7 == 0) goto L_0x0133;
    L_0x0122:
        r7 = NUM; // 0x7f0e045d float:1.8877303E38 double:1.0531627085E-314;
        r8 = "EventLogEditedGroupPhoto";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e76;
    L_0x0133:
        r7 = NUM; // 0x7f0e045a float:1.8877297E38 double:1.053162707E-314;
        r8 = "EventLogEditedChannelPhoto";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e76;
    L_0x0144:
        r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoin;
        r6 = "EventLogChannelJoined";
        if (r10 == 0) goto L_0x016e;
    L_0x014a:
        r7 = r4.megagroup;
        if (r7 == 0) goto L_0x015f;
    L_0x014e:
        r6 = NUM; // 0x7f0e0470 float:1.8877342E38 double:1.053162718E-314;
        r7 = "EventLogGroupJoined";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e76;
    L_0x015f:
        r7 = NUM; // 0x7f0e0453 float:1.8877283E38 double:1.0531627036E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e76;
    L_0x016e:
        r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantLeave;
        if (r10 == 0) goto L_0x01b0;
    L_0x0172:
        r6 = new org.telegram.tgnet.TLRPC$TL_messageService;
        r6.<init>();
        r0.messageOwner = r6;
        r6 = r0.messageOwner;
        r7 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeleteUser;
        r7.<init>();
        r6.action = r7;
        r6 = r0.messageOwner;
        r6 = r6.action;
        r7 = r1.user_id;
        r6.user_id = r7;
        r6 = r4.megagroup;
        if (r6 == 0) goto L_0x019f;
    L_0x018e:
        r6 = NUM; // 0x7f0e0475 float:1.8877352E38 double:1.0531627204E-314;
        r7 = "EventLogLeftGroup";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e76;
    L_0x019f:
        r6 = NUM; // 0x7f0e0474 float:1.887735E38 double:1.05316272E-314;
        r7 = "EventLogLeftChannel";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e76;
    L_0x01b0:
        r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantInvite;
        r11 = "un2";
        if (r10 == 0) goto L_0x0224;
    L_0x01b7:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageService;
        r7.<init>();
        r0.messageOwner = r7;
        r7 = r0.messageOwner;
        r8 = new org.telegram.tgnet.TLRPC$TL_messageActionChatAddUser;
        r8.<init>();
        r7.action = r8;
        r7 = r0.currentAccount;
        r7 = org.telegram.messenger.MessagesController.getInstance(r7);
        r8 = r1.action;
        r8 = r8.participant;
        r8 = r8.user_id;
        r8 = java.lang.Integer.valueOf(r8);
        r7 = r7.getUser(r8);
        r8 = r1.action;
        r8 = r8.participant;
        r8 = r8.user_id;
        r10 = r0.messageOwner;
        r10 = r10.from_id;
        if (r8 != r10) goto L_0x020b;
    L_0x01e7:
        r7 = r4.megagroup;
        if (r7 == 0) goto L_0x01fc;
    L_0x01eb:
        r6 = NUM; // 0x7f0e0470 float:1.8877342E38 double:1.053162718E-314;
        r7 = "EventLogGroupJoined";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e76;
    L_0x01fc:
        r7 = NUM; // 0x7f0e0453 float:1.8877283E38 double:1.0531627036E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e76;
    L_0x020b:
        r6 = NUM; // 0x7f0e0449 float:1.8877263E38 double:1.0531626986E-314;
        r8 = "EventLogAdded";
        r6 = org.telegram.messenger.LocaleController.getString(r8, r6);
        r6 = r0.replaceWithLink(r6, r11, r7);
        r0.messageText = r6;
        r6 = r0.messageText;
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e76;
    L_0x0224:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin;
        r10 = "%1$s";
        r12 = 10;
        if (r6 == 0) goto L_0x045f;
    L_0x022c:
        r6 = new org.telegram.tgnet.TLRPC$TL_message;
        r6.<init>();
        r0.messageOwner = r6;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r7 = r1.action;
        r7 = r7.prev_participant;
        r7 = r7.user_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getUser(r7);
        r7 = r1.action;
        r8 = r7.prev_participant;
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
        if (r8 != 0) goto L_0x0279;
    L_0x024f:
        r7 = r7.new_participant;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
        if (r7 == 0) goto L_0x0279;
    L_0x0255:
        r7 = NUM; // 0x7f0e0451 float:1.8877279E38 double:1.0531627026E-314;
        r8 = "EventLogChangedOwnership";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r8 = r7.indexOf(r10);
        r10 = new java.lang.StringBuilder;
        r11 = new java.lang.Object[r9];
        r12 = r0.messageOwner;
        r12 = r12.entities;
        r6 = r0.getUserName(r6, r12, r8);
        r11[r15] = r6;
        r6 = java.lang.String.format(r7, r11);
        r10.<init>(r6);
        goto L_0x0457;
    L_0x0279:
        r7 = NUM; // 0x7f0e047c float:1.8877366E38 double:1.053162724E-314;
        r8 = "EventLogPromoted";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r8 = r7.indexOf(r10);
        r10 = new java.lang.StringBuilder;
        r11 = new java.lang.Object[r9];
        r14 = r0.messageOwner;
        r14 = r14.entities;
        r6 = r0.getUserName(r6, r14, r8);
        r11[r15] = r6;
        r6 = java.lang.String.format(r7, r11);
        r10.<init>(r6);
        r6 = "\n";
        r10.append(r6);
        r6 = r1.action;
        r7 = r6.prev_participant;
        r7 = r7.admin_rights;
        r6 = r6.new_participant;
        r6 = r6.admin_rights;
        if (r7 != 0) goto L_0x02b1;
    L_0x02ac:
        r7 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights;
        r7.<init>();
    L_0x02b1:
        if (r6 != 0) goto L_0x02b8;
    L_0x02b3:
        r6 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights;
        r6.<init>();
    L_0x02b8:
        r8 = r1.action;
        r11 = r8.prev_participant;
        r11 = r11.rank;
        r8 = r8.new_participant;
        r8 = r8.rank;
        r8 = android.text.TextUtils.equals(r11, r8);
        if (r8 != 0) goto L_0x0311;
    L_0x02c8:
        r8 = r1.action;
        r8 = r8.new_participant;
        r8 = r8.rank;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 == 0) goto L_0x02ee;
    L_0x02d4:
        r10.append(r12);
        r8 = 45;
        r10.append(r8);
        r11 = 32;
        r10.append(r11);
        r14 = NUM; // 0x7f0e0486 float:1.8877386E38 double:1.053162729E-314;
        r8 = "EventLogPromotedRemovedTitle";
        r8 = org.telegram.messenger.LocaleController.getString(r8, r14);
        r10.append(r8);
        goto L_0x0311;
    L_0x02ee:
        r11 = 32;
        r10.append(r12);
        r8 = 43;
        r10.append(r8);
        r10.append(r11);
        r11 = NUM; // 0x7f0e0487 float:1.8877388E38 double:1.0531627293E-314;
        r14 = new java.lang.Object[r9];
        r8 = r1.action;
        r8 = r8.new_participant;
        r8 = r8.rank;
        r14[r15] = r8;
        r8 = "EventLogPromotedTitle";
        r8 = org.telegram.messenger.LocaleController.formatString(r8, r11, r14);
        r10.append(r8);
    L_0x0311:
        r8 = r7.change_info;
        r11 = r6.change_info;
        if (r8 == r11) goto L_0x0341;
    L_0x0317:
        r10.append(r12);
        r8 = r6.change_info;
        if (r8 == 0) goto L_0x0321;
    L_0x031e:
        r8 = 43;
        goto L_0x0323;
    L_0x0321:
        r8 = 45;
    L_0x0323:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = r4.megagroup;
        if (r8 == 0) goto L_0x0335;
    L_0x032f:
        r8 = NUM; // 0x7f0e0481 float:1.8877376E38 double:1.0531627263E-314;
        r11 = "EventLogPromotedChangeGroupInfo";
        goto L_0x033a;
    L_0x0335:
        r8 = NUM; // 0x7f0e0480 float:1.8877374E38 double:1.053162726E-314;
        r11 = "EventLogPromotedChangeChannelInfo";
    L_0x033a:
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x0341:
        r8 = r4.megagroup;
        if (r8 != 0) goto L_0x0391;
    L_0x0345:
        r8 = r7.post_messages;
        r11 = r6.post_messages;
        if (r8 == r11) goto L_0x036b;
    L_0x034b:
        r10.append(r12);
        r8 = r6.post_messages;
        if (r8 == 0) goto L_0x0355;
    L_0x0352:
        r8 = 43;
        goto L_0x0357;
    L_0x0355:
        r8 = 45;
    L_0x0357:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0e0485 float:1.8877384E38 double:1.0531627283E-314;
        r11 = "EventLogPromotedPostMessages";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x036b:
        r8 = r7.edit_messages;
        r11 = r6.edit_messages;
        if (r8 == r11) goto L_0x0391;
    L_0x0371:
        r10.append(r12);
        r8 = r6.edit_messages;
        if (r8 == 0) goto L_0x037b;
    L_0x0378:
        r8 = 43;
        goto L_0x037d;
    L_0x037b:
        r8 = 45;
    L_0x037d:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0e0483 float:1.887738E38 double:1.0531627273E-314;
        r11 = "EventLogPromotedEditMessages";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x0391:
        r8 = r7.delete_messages;
        r11 = r6.delete_messages;
        if (r8 == r11) goto L_0x03b7;
    L_0x0397:
        r10.append(r12);
        r8 = r6.delete_messages;
        if (r8 == 0) goto L_0x03a1;
    L_0x039e:
        r8 = 43;
        goto L_0x03a3;
    L_0x03a1:
        r8 = 45;
    L_0x03a3:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0e0482 float:1.8877378E38 double:1.053162727E-314;
        r11 = "EventLogPromotedDeleteMessages";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x03b7:
        r8 = r7.add_admins;
        r11 = r6.add_admins;
        if (r8 == r11) goto L_0x03dd;
    L_0x03bd:
        r10.append(r12);
        r8 = r6.add_admins;
        if (r8 == 0) goto L_0x03c7;
    L_0x03c4:
        r8 = 43;
        goto L_0x03c9;
    L_0x03c7:
        r8 = 45;
    L_0x03c9:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0e047d float:1.8877368E38 double:1.0531627243E-314;
        r11 = "EventLogPromotedAddAdmins";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x03dd:
        r8 = r4.megagroup;
        if (r8 == 0) goto L_0x0407;
    L_0x03e1:
        r8 = r7.ban_users;
        r11 = r6.ban_users;
        if (r8 == r11) goto L_0x0407;
    L_0x03e7:
        r10.append(r12);
        r8 = r6.ban_users;
        if (r8 == 0) goto L_0x03f1;
    L_0x03ee:
        r8 = 43;
        goto L_0x03f3;
    L_0x03f1:
        r8 = 45;
    L_0x03f3:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0e047f float:1.8877372E38 double:1.0531627253E-314;
        r11 = "EventLogPromotedBanUsers";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x0407:
        r8 = r7.invite_users;
        r11 = r6.invite_users;
        if (r8 == r11) goto L_0x042d;
    L_0x040d:
        r10.append(r12);
        r8 = r6.invite_users;
        if (r8 == 0) goto L_0x0417;
    L_0x0414:
        r8 = 43;
        goto L_0x0419;
    L_0x0417:
        r8 = 45;
    L_0x0419:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0e047e float:1.887737E38 double:1.053162725E-314;
        r11 = "EventLogPromotedAddUsers";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x042d:
        r8 = r4.megagroup;
        if (r8 == 0) goto L_0x0457;
    L_0x0431:
        r7 = r7.pin_messages;
        r8 = r6.pin_messages;
        if (r7 == r8) goto L_0x0457;
    L_0x0437:
        r10.append(r12);
        r6 = r6.pin_messages;
        if (r6 == 0) goto L_0x0441;
    L_0x043e:
        r6 = 43;
        goto L_0x0443;
    L_0x0441:
        r6 = 45;
    L_0x0443:
        r10.append(r6);
        r6 = 32;
        r10.append(r6);
        r6 = NUM; // 0x7f0e0484 float:1.8877382E38 double:1.053162728E-314;
        r7 = "EventLogPromotedPinMessages";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r10.append(r6);
    L_0x0457:
        r6 = r10.toString();
        r0.messageText = r6;
        goto L_0x0e76;
    L_0x045f:
        r18 = 43;
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDefaultBannedRights;
        if (r6 == 0) goto L_0x0607;
    L_0x0465:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDefaultBannedRights) r8;
        r6 = new org.telegram.tgnet.TLRPC$TL_message;
        r6.<init>();
        r0.messageOwner = r6;
        r6 = r8.prev_banned_rights;
        r7 = r8.new_banned_rights;
        r8 = new java.lang.StringBuilder;
        r10 = NUM; // 0x7f0e0456 float:1.8877289E38 double:1.053162705E-314;
        r11 = "EventLogDefaultPermissions";
        r10 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r8.<init>(r10);
        if (r6 != 0) goto L_0x0487;
    L_0x0482:
        r6 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r6.<init>();
    L_0x0487:
        if (r7 != 0) goto L_0x048e;
    L_0x0489:
        r7 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r7.<init>();
    L_0x048e:
        r10 = r6.send_messages;
        r11 = r7.send_messages;
        if (r10 == r11) goto L_0x04b9;
    L_0x0494:
        r8.append(r12);
        r8.append(r12);
        r10 = r7.send_messages;
        if (r10 != 0) goto L_0x04a1;
    L_0x049e:
        r10 = 43;
        goto L_0x04a3;
    L_0x04a1:
        r10 = 45;
    L_0x04a3:
        r8.append(r10);
        r10 = 32;
        r8.append(r10);
        r10 = NUM; // 0x7f0e0496 float:1.8877419E38 double:1.0531627367E-314;
        r11 = "EventLogRestrictedSendMessages";
        r10 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r8.append(r10);
        r10 = 1;
        goto L_0x04ba;
    L_0x04b9:
        r10 = 0;
    L_0x04ba:
        r11 = r6.send_stickers;
        r14 = r7.send_stickers;
        if (r11 != r14) goto L_0x04d2;
    L_0x04c0:
        r11 = r6.send_inline;
        r14 = r7.send_inline;
        if (r11 != r14) goto L_0x04d2;
    L_0x04c6:
        r11 = r6.send_gifs;
        r14 = r7.send_gifs;
        if (r11 != r14) goto L_0x04d2;
    L_0x04cc:
        r11 = r6.send_games;
        r14 = r7.send_games;
        if (r11 == r14) goto L_0x04f8;
    L_0x04d2:
        if (r10 != 0) goto L_0x04d8;
    L_0x04d4:
        r8.append(r12);
        r10 = 1;
    L_0x04d8:
        r8.append(r12);
        r11 = r7.send_stickers;
        if (r11 != 0) goto L_0x04e2;
    L_0x04df:
        r11 = 43;
        goto L_0x04e4;
    L_0x04e2:
        r11 = 45;
    L_0x04e4:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0e0498 float:1.8877423E38 double:1.0531627377E-314;
        r14 = "EventLogRestrictedSendStickers";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x04f8:
        r11 = r6.send_media;
        r14 = r7.send_media;
        if (r11 == r14) goto L_0x0524;
    L_0x04fe:
        if (r10 != 0) goto L_0x0504;
    L_0x0500:
        r8.append(r12);
        r10 = 1;
    L_0x0504:
        r8.append(r12);
        r11 = r7.send_media;
        if (r11 != 0) goto L_0x050e;
    L_0x050b:
        r11 = 43;
        goto L_0x0510;
    L_0x050e:
        r11 = 45;
    L_0x0510:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0e0495 float:1.8877417E38 double:1.053162736E-314;
        r14 = "EventLogRestrictedSendMedia";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x0524:
        r11 = r6.send_polls;
        r14 = r7.send_polls;
        if (r11 == r14) goto L_0x0550;
    L_0x052a:
        if (r10 != 0) goto L_0x0530;
    L_0x052c:
        r8.append(r12);
        r10 = 1;
    L_0x0530:
        r8.append(r12);
        r11 = r7.send_polls;
        if (r11 != 0) goto L_0x053a;
    L_0x0537:
        r11 = 43;
        goto L_0x053c;
    L_0x053a:
        r11 = 45;
    L_0x053c:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0e0497 float:1.887742E38 double:1.053162737E-314;
        r14 = "EventLogRestrictedSendPolls";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x0550:
        r11 = r6.embed_links;
        r14 = r7.embed_links;
        if (r11 == r14) goto L_0x057c;
    L_0x0556:
        if (r10 != 0) goto L_0x055c;
    L_0x0558:
        r8.append(r12);
        r10 = 1;
    L_0x055c:
        r8.append(r12);
        r11 = r7.embed_links;
        if (r11 != 0) goto L_0x0566;
    L_0x0563:
        r11 = 43;
        goto L_0x0568;
    L_0x0566:
        r11 = 45;
    L_0x0568:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0e0494 float:1.8877415E38 double:1.0531627357E-314;
        r14 = "EventLogRestrictedSendEmbed";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x057c:
        r11 = r6.change_info;
        r14 = r7.change_info;
        if (r11 == r14) goto L_0x05a8;
    L_0x0582:
        if (r10 != 0) goto L_0x0588;
    L_0x0584:
        r8.append(r12);
        r10 = 1;
    L_0x0588:
        r8.append(r12);
        r11 = r7.change_info;
        if (r11 != 0) goto L_0x0592;
    L_0x058f:
        r11 = 43;
        goto L_0x0594;
    L_0x0592:
        r11 = 45;
    L_0x0594:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0e0490 float:1.8877407E38 double:1.0531627337E-314;
        r14 = "EventLogRestrictedChangeInfo";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x05a8:
        r11 = r6.invite_users;
        r14 = r7.invite_users;
        if (r11 == r14) goto L_0x05d4;
    L_0x05ae:
        if (r10 != 0) goto L_0x05b4;
    L_0x05b0:
        r8.append(r12);
        r10 = 1;
    L_0x05b4:
        r8.append(r12);
        r11 = r7.invite_users;
        if (r11 != 0) goto L_0x05be;
    L_0x05bb:
        r11 = 43;
        goto L_0x05c0;
    L_0x05be:
        r11 = 45;
    L_0x05c0:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0e0491 float:1.8877409E38 double:1.053162734E-314;
        r14 = "EventLogRestrictedInviteUsers";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x05d4:
        r6 = r6.pin_messages;
        r11 = r7.pin_messages;
        if (r6 == r11) goto L_0x05ff;
    L_0x05da:
        if (r10 != 0) goto L_0x05df;
    L_0x05dc:
        r8.append(r12);
    L_0x05df:
        r8.append(r12);
        r6 = r7.pin_messages;
        if (r6 != 0) goto L_0x05e9;
    L_0x05e6:
        r6 = 43;
        goto L_0x05eb;
    L_0x05e9:
        r6 = 45;
    L_0x05eb:
        r8.append(r6);
        r6 = 32;
        r8.append(r6);
        r6 = NUM; // 0x7f0e0492 float:1.887741E38 double:1.0531627347E-314;
        r7 = "EventLogRestrictedPinMessages";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r8.append(r6);
    L_0x05ff:
        r6 = r8.toString();
        r0.messageText = r6;
        goto L_0x0e76;
    L_0x0607:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan;
        r12 = 60;
        if (r6 == 0) goto L_0x08fa;
    L_0x060d:
        r6 = new org.telegram.tgnet.TLRPC$TL_message;
        r6.<init>();
        r0.messageOwner = r6;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r7 = r1.action;
        r7 = r7.prev_participant;
        r7 = r7.user_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getUser(r7);
        r7 = r1.action;
        r8 = r7.prev_participant;
        r8 = r8.banned_rights;
        r7 = r7.new_participant;
        r7 = r7.banned_rights;
        r11 = r4.megagroup;
        if (r11 == 0) goto L_0x08c5;
    L_0x0636:
        if (r7 == 0) goto L_0x0646;
    L_0x0638:
        r11 = r7.view_messages;
        if (r11 == 0) goto L_0x0646;
    L_0x063c:
        if (r7 == 0) goto L_0x08c5;
    L_0x063e:
        if (r8 == 0) goto L_0x08c5;
    L_0x0640:
        r11 = r7.until_date;
        r14 = r8.until_date;
        if (r11 == r14) goto L_0x08c5;
    L_0x0646:
        if (r7 == 0) goto L_0x06c7;
    L_0x0648:
        r11 = org.telegram.messenger.AndroidUtilities.isBannedForever(r7);
        if (r11 != 0) goto L_0x06c7;
    L_0x064e:
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r14 = r7.until_date;
        r15 = r1.date;
        r14 = r14 - r15;
        r15 = r14 / 60;
        r15 = r15 / r12;
        r15 = r15 / 24;
        r22 = r15 * 60;
        r22 = r22 * 60;
        r22 = r22 * 24;
        r14 = r14 - r22;
        r22 = r14 / 60;
        r9 = r22 / 60;
        r22 = r9 * 60;
        r22 = r22 * 60;
        r14 = r14 - r22;
        r14 = r14 / r12;
        r2 = 3;
        r12 = 0;
        r17 = 0;
    L_0x0674:
        if (r12 >= r2) goto L_0x06d5;
    L_0x0676:
        if (r12 != 0) goto L_0x0683;
    L_0x0678:
        if (r15 == 0) goto L_0x0698;
    L_0x067a:
        r2 = "Days";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r15);
    L_0x0680:
        r17 = r17 + 1;
        goto L_0x0699;
    L_0x0683:
        r2 = 1;
        if (r12 != r2) goto L_0x068f;
    L_0x0686:
        if (r9 == 0) goto L_0x0698;
    L_0x0688:
        r2 = "Hours";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r9);
        goto L_0x0680;
    L_0x068f:
        if (r14 == 0) goto L_0x0698;
    L_0x0691:
        r2 = "Minutes";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r14);
        goto L_0x0680;
    L_0x0698:
        r2 = 0;
    L_0x0699:
        r24 = r17;
        r17 = r9;
        r9 = r24;
        if (r2 == 0) goto L_0x06b5;
    L_0x06a1:
        r23 = r11.length();
        if (r23 <= 0) goto L_0x06af;
    L_0x06a7:
        r23 = r14;
        r14 = ", ";
        r11.append(r14);
        goto L_0x06b1;
    L_0x06af:
        r23 = r14;
    L_0x06b1:
        r11.append(r2);
        goto L_0x06b7;
    L_0x06b5:
        r23 = r14;
    L_0x06b7:
        r2 = 2;
        if (r9 != r2) goto L_0x06bb;
    L_0x06ba:
        goto L_0x06d5;
    L_0x06bb:
        r12 = r12 + 1;
        r14 = r23;
        r2 = 3;
        r24 = r17;
        r17 = r9;
        r9 = r24;
        goto L_0x0674;
    L_0x06c7:
        r11 = new java.lang.StringBuilder;
        r2 = NUM; // 0x7f0e0bac float:1.8881098E38 double:1.053163633E-314;
        r9 = "UserRestrictionsUntilForever";
        r2 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r11.<init>(r2);
    L_0x06d5:
        r2 = NUM; // 0x7f0e0499 float:1.8877425E38 double:1.053162738E-314;
        r9 = "EventLogRestrictedUntil";
        r2 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r9 = r2.indexOf(r10);
        r10 = new java.lang.StringBuilder;
        r12 = 2;
        r12 = new java.lang.Object[r12];
        r14 = r0.messageOwner;
        r14 = r14.entities;
        r6 = r0.getUserName(r6, r14, r9);
        r9 = 0;
        r12[r9] = r6;
        r6 = r11.toString();
        r9 = 1;
        r12[r9] = r6;
        r2 = java.lang.String.format(r2, r12);
        r10.<init>(r2);
        if (r8 != 0) goto L_0x0707;
    L_0x0702:
        r8 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r8.<init>();
    L_0x0707:
        if (r7 != 0) goto L_0x070e;
    L_0x0709:
        r7 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r7.<init>();
    L_0x070e:
        r2 = r8.view_messages;
        r6 = r7.view_messages;
        if (r2 == r6) goto L_0x073b;
    L_0x0714:
        r2 = 10;
        r10.append(r2);
        r10.append(r2);
        r2 = r7.view_messages;
        if (r2 != 0) goto L_0x0723;
    L_0x0720:
        r2 = 43;
        goto L_0x0725;
    L_0x0723:
        r2 = 45;
    L_0x0725:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0e0493 float:1.8877413E38 double:1.053162735E-314;
        r6 = "EventLogRestrictedReadMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
        r9 = 1;
        goto L_0x073c;
    L_0x073b:
        r9 = 0;
    L_0x073c:
        r2 = r8.send_messages;
        r6 = r7.send_messages;
        if (r2 == r6) goto L_0x076a;
    L_0x0742:
        r2 = 10;
        if (r9 != 0) goto L_0x074a;
    L_0x0746:
        r10.append(r2);
        r9 = 1;
    L_0x074a:
        r10.append(r2);
        r2 = r7.send_messages;
        if (r2 != 0) goto L_0x0754;
    L_0x0751:
        r2 = 43;
        goto L_0x0756;
    L_0x0754:
        r2 = 45;
    L_0x0756:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0e0496 float:1.8877419E38 double:1.0531627367E-314;
        r6 = "EventLogRestrictedSendMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x076a:
        r2 = r8.send_stickers;
        r6 = r7.send_stickers;
        if (r2 != r6) goto L_0x0782;
    L_0x0770:
        r2 = r8.send_inline;
        r6 = r7.send_inline;
        if (r2 != r6) goto L_0x0782;
    L_0x0776:
        r2 = r8.send_gifs;
        r6 = r7.send_gifs;
        if (r2 != r6) goto L_0x0782;
    L_0x077c:
        r2 = r8.send_games;
        r6 = r7.send_games;
        if (r2 == r6) goto L_0x07aa;
    L_0x0782:
        r2 = 10;
        if (r9 != 0) goto L_0x078a;
    L_0x0786:
        r10.append(r2);
        r9 = 1;
    L_0x078a:
        r10.append(r2);
        r2 = r7.send_stickers;
        if (r2 != 0) goto L_0x0794;
    L_0x0791:
        r2 = 43;
        goto L_0x0796;
    L_0x0794:
        r2 = 45;
    L_0x0796:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0e0498 float:1.8877423E38 double:1.0531627377E-314;
        r6 = "EventLogRestrictedSendStickers";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x07aa:
        r2 = r8.send_media;
        r6 = r7.send_media;
        if (r2 == r6) goto L_0x07d8;
    L_0x07b0:
        r2 = 10;
        if (r9 != 0) goto L_0x07b8;
    L_0x07b4:
        r10.append(r2);
        r9 = 1;
    L_0x07b8:
        r10.append(r2);
        r2 = r7.send_media;
        if (r2 != 0) goto L_0x07c2;
    L_0x07bf:
        r2 = 43;
        goto L_0x07c4;
    L_0x07c2:
        r2 = 45;
    L_0x07c4:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0e0495 float:1.8877417E38 double:1.053162736E-314;
        r6 = "EventLogRestrictedSendMedia";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x07d8:
        r2 = r8.send_polls;
        r6 = r7.send_polls;
        if (r2 == r6) goto L_0x0806;
    L_0x07de:
        r2 = 10;
        if (r9 != 0) goto L_0x07e6;
    L_0x07e2:
        r10.append(r2);
        r9 = 1;
    L_0x07e6:
        r10.append(r2);
        r2 = r7.send_polls;
        if (r2 != 0) goto L_0x07f0;
    L_0x07ed:
        r2 = 43;
        goto L_0x07f2;
    L_0x07f0:
        r2 = 45;
    L_0x07f2:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0e0497 float:1.887742E38 double:1.053162737E-314;
        r6 = "EventLogRestrictedSendPolls";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x0806:
        r2 = r8.embed_links;
        r6 = r7.embed_links;
        if (r2 == r6) goto L_0x0834;
    L_0x080c:
        r2 = 10;
        if (r9 != 0) goto L_0x0814;
    L_0x0810:
        r10.append(r2);
        r9 = 1;
    L_0x0814:
        r10.append(r2);
        r2 = r7.embed_links;
        if (r2 != 0) goto L_0x081e;
    L_0x081b:
        r2 = 43;
        goto L_0x0820;
    L_0x081e:
        r2 = 45;
    L_0x0820:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0e0494 float:1.8877415E38 double:1.0531627357E-314;
        r6 = "EventLogRestrictedSendEmbed";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x0834:
        r2 = r8.change_info;
        r6 = r7.change_info;
        if (r2 == r6) goto L_0x0862;
    L_0x083a:
        r2 = 10;
        if (r9 != 0) goto L_0x0842;
    L_0x083e:
        r10.append(r2);
        r9 = 1;
    L_0x0842:
        r10.append(r2);
        r2 = r7.change_info;
        if (r2 != 0) goto L_0x084c;
    L_0x0849:
        r2 = 43;
        goto L_0x084e;
    L_0x084c:
        r2 = 45;
    L_0x084e:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0e0490 float:1.8877407E38 double:1.0531627337E-314;
        r6 = "EventLogRestrictedChangeInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x0862:
        r2 = r8.invite_users;
        r6 = r7.invite_users;
        if (r2 == r6) goto L_0x0890;
    L_0x0868:
        r2 = 10;
        if (r9 != 0) goto L_0x0870;
    L_0x086c:
        r10.append(r2);
        r9 = 1;
    L_0x0870:
        r10.append(r2);
        r2 = r7.invite_users;
        if (r2 != 0) goto L_0x087a;
    L_0x0877:
        r2 = 43;
        goto L_0x087c;
    L_0x087a:
        r2 = 45;
    L_0x087c:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0e0491 float:1.8877409E38 double:1.053162734E-314;
        r6 = "EventLogRestrictedInviteUsers";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x0890:
        r2 = r8.pin_messages;
        r6 = r7.pin_messages;
        if (r2 == r6) goto L_0x08bd;
    L_0x0896:
        r2 = 10;
        if (r9 != 0) goto L_0x089d;
    L_0x089a:
        r10.append(r2);
    L_0x089d:
        r10.append(r2);
        r2 = r7.pin_messages;
        if (r2 != 0) goto L_0x08a7;
    L_0x08a4:
        r2 = 43;
        goto L_0x08a9;
    L_0x08a7:
        r2 = 45;
    L_0x08a9:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0e0492 float:1.887741E38 double:1.0531627347E-314;
        r6 = "EventLogRestrictedPinMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x08bd:
        r2 = r10.toString();
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x08c5:
        if (r7 == 0) goto L_0x08d7;
    L_0x08c7:
        if (r8 == 0) goto L_0x08cd;
    L_0x08c9:
        r2 = r7.view_messages;
        if (r2 == 0) goto L_0x08d7;
    L_0x08cd:
        r2 = NUM; // 0x7f0e0454 float:1.8877285E38 double:1.053162704E-314;
        r7 = "EventLogChannelRestricted";
        r2 = org.telegram.messenger.LocaleController.getString(r7, r2);
        goto L_0x08e0;
    L_0x08d7:
        r2 = NUM; // 0x7f0e0455 float:1.8877287E38 double:1.0531627045E-314;
        r7 = "EventLogChannelUnrestricted";
        r2 = org.telegram.messenger.LocaleController.getString(r7, r2);
    L_0x08e0:
        r7 = r2.indexOf(r10);
        r8 = 1;
        r9 = new java.lang.Object[r8];
        r8 = r0.messageOwner;
        r8 = r8.entities;
        r6 = r0.getUserName(r6, r8, r7);
        r7 = 0;
        r9[r7] = r6;
        r2 = java.lang.String.format(r2, r9);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x08fa:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionUpdatePinned;
        if (r2 == 0) goto L_0x0977;
    L_0x08fe:
        if (r5 == 0) goto L_0x094d;
    L_0x0900:
        r2 = r5.id;
        r6 = NUM; // 0x827aCLASSNAME float:5.045703E-34 double:6.75969194E-316;
        if (r2 != r6) goto L_0x094d;
    L_0x0907:
        r2 = r8.message;
        r2 = r2.fwd_from;
        if (r2 == 0) goto L_0x094d;
    L_0x090d:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r6 = r1.action;
        r6 = r6.message;
        r6 = r6.fwd_from;
        r6 = r6.channel_id;
        r6 = java.lang.Integer.valueOf(r6);
        r2 = r2.getChat(r6);
        r6 = r1.action;
        r6 = r6.message;
        r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty;
        if (r6 == 0) goto L_0x093c;
    L_0x092b:
        r6 = NUM; // 0x7f0e04a4 float:1.8877447E38 double:1.0531627436E-314;
        r7 = "EventLogUnpinnedMessages";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r2 = r0.replaceWithLink(r6, r14, r2);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x093c:
        r6 = NUM; // 0x7f0e0479 float:1.887736E38 double:1.0531627223E-314;
        r7 = "EventLogPinnedMessages";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r2 = r0.replaceWithLink(r6, r14, r2);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x094d:
        r2 = r1.action;
        r2 = r2.message;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty;
        if (r2 == 0) goto L_0x0966;
    L_0x0955:
        r2 = NUM; // 0x7f0e04a4 float:1.8877447E38 double:1.0531627436E-314;
        r6 = "EventLogUnpinnedMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0966:
        r2 = NUM; // 0x7f0e0479 float:1.887736E38 double:1.0531627223E-314;
        r6 = "EventLogPinnedMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0977:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionStopPoll;
        if (r2 == 0) goto L_0x098c;
    L_0x097b:
        r2 = NUM; // 0x7f0e049b float:1.8877429E38 double:1.053162739E-314;
        r6 = "EventLogStopPoll";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x098c:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures;
        if (r2 == 0) goto L_0x09b8;
    L_0x0990:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures) r8;
        r2 = r8.new_value;
        if (r2 == 0) goto L_0x09a7;
    L_0x0996:
        r2 = NUM; // 0x7f0e04a1 float:1.887744E38 double:1.053162742E-314;
        r6 = "EventLogToggledSignaturesOn";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x09a7:
        r2 = NUM; // 0x7f0e04a0 float:1.8877439E38 double:1.0531627416E-314;
        r6 = "EventLogToggledSignaturesOff";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x09b8:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites;
        if (r2 == 0) goto L_0x09e4;
    L_0x09bc:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites) r8;
        r2 = r8.new_value;
        if (r2 == 0) goto L_0x09d3;
    L_0x09c2:
        r2 = NUM; // 0x7f0e049f float:1.8877437E38 double:1.053162741E-314;
        r6 = "EventLogToggledInvitesOn";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x09d3:
        r2 = NUM; // 0x7f0e049e float:1.8877435E38 double:1.0531627406E-314;
        r6 = "EventLogToggledInvitesOff";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x09e4:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDeleteMessage;
        if (r2 == 0) goto L_0x09f9;
    L_0x09e8:
        r2 = NUM; // 0x7f0e0457 float:1.887729E38 double:1.0531627055E-314;
        r6 = "EventLogDeletedMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x09f9:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat;
        if (r2 == 0) goto L_0x0aaa;
    L_0x09fd:
        r2 = r8;
        r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat) r2;
        r2 = r2.new_value;
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat) r8;
        r6 = r8.prev_value;
        r7 = r4.megagroup;
        if (r7 == 0) goto L_0x0a5a;
    L_0x0a0a:
        if (r2 != 0) goto L_0x0a33;
    L_0x0a0c:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r6 = java.lang.Integer.valueOf(r6);
        r2 = r2.getChat(r6);
        r6 = NUM; // 0x7f0e048b float:1.8877396E38 double:1.053162731E-314;
        r7 = "EventLogRemovedLinkedChannel";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        r6 = r0.messageText;
        r2 = r0.replaceWithLink(r6, r11, r2);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0a33:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r6.getChat(r2);
        r6 = NUM; // 0x7f0e044e float:1.8877273E38 double:1.053162701E-314;
        r7 = "EventLogChangedLinkedChannel";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        r6 = r0.messageText;
        r2 = r0.replaceWithLink(r6, r11, r2);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0a5a:
        if (r2 != 0) goto L_0x0a83;
    L_0x0a5c:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r6 = java.lang.Integer.valueOf(r6);
        r2 = r2.getChat(r6);
        r6 = NUM; // 0x7f0e048c float:1.8877398E38 double:1.0531627317E-314;
        r7 = "EventLogRemovedLinkedGroup";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        r6 = r0.messageText;
        r2 = r0.replaceWithLink(r6, r11, r2);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0a83:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r6.getChat(r2);
        r6 = NUM; // 0x7f0e044f float:1.8877275E38 double:1.0531627016E-314;
        r7 = "EventLogChangedLinkedGroup";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        r6 = r0.messageText;
        r2 = r0.replaceWithLink(r6, r11, r2);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0aaa:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden;
        if (r2 == 0) goto L_0x0ad6;
    L_0x0aae:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden) r8;
        r2 = r8.new_value;
        if (r2 == 0) goto L_0x0ac5;
    L_0x0ab4:
        r2 = NUM; // 0x7f0e049c float:1.887743E38 double:1.0531627396E-314;
        r6 = "EventLogToggledInvitesHistoryOff";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0ac5:
        r2 = NUM; // 0x7f0e049d float:1.8877433E38 double:1.05316274E-314;
        r6 = "EventLogToggledInvitesHistoryOn";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0ad6:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout;
        if (r2 == 0) goto L_0x0b58;
    L_0x0ada:
        r2 = r4.megagroup;
        if (r2 == 0) goto L_0x0ae4;
    L_0x0ade:
        r2 = NUM; // 0x7f0e045c float:1.8877301E38 double:1.053162708E-314;
        r6 = "EventLogEditedGroupDescription";
        goto L_0x0ae9;
    L_0x0ae4:
        r2 = NUM; // 0x7f0e0459 float:1.8877295E38 double:1.0531627065E-314;
        r6 = "EventLogEditedChannelDescription";
    L_0x0ae9:
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        r6 = new org.telegram.tgnet.TLRPC$TL_message;
        r6.<init>();
        r2 = 0;
        r6.out = r2;
        r6.unread = r2;
        r2 = r1.user_id;
        r6.from_id = r2;
        r6.to_id = r7;
        r2 = r1.date;
        r6.date = r2;
        r2 = r1.action;
        r7 = r2;
        r7 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r7;
        r7 = r7.new_value;
        r6.message = r7;
        r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r2;
        r2 = r2.prev_value;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0b4f;
    L_0x0b1a:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
        r2.<init>();
        r6.media = r2;
        r2 = r6.media;
        r7 = new org.telegram.tgnet.TLRPC$TL_webPage;
        r7.<init>();
        r2.webpage = r7;
        r2 = r6.media;
        r2 = r2.webpage;
        r7 = 10;
        r2.flags = r7;
        r2.display_url = r13;
        r2.url = r13;
        r7 = NUM; // 0x7f0e047a float:1.8877362E38 double:1.053162723E-314;
        r8 = "EventLogPreviousGroupDescription";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r2.site_name = r7;
        r2 = r6.media;
        r2 = r2.webpage;
        r7 = r1.action;
        r7 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r7;
        r7 = r7.prev_value;
        r2.description = r7;
        goto L_0x0e77;
    L_0x0b4f:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r2.<init>();
        r6.media = r2;
        goto L_0x0e77;
    L_0x0b58:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername;
        if (r2 == 0) goto L_0x0CLASSNAME;
    L_0x0b5c:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r8;
        r2 = r8.new_value;
        r6 = android.text.TextUtils.isEmpty(r2);
        if (r6 != 0) goto L_0x0b80;
    L_0x0b66:
        r6 = r4.megagroup;
        if (r6 == 0) goto L_0x0b70;
    L_0x0b6a:
        r6 = NUM; // 0x7f0e044d float:1.887727E38 double:1.0531627006E-314;
        r8 = "EventLogChangedGroupLink";
        goto L_0x0b75;
    L_0x0b70:
        r6 = NUM; // 0x7f0e044c float:1.8877269E38 double:1.0531627E-314;
        r8 = "EventLogChangedChannelLink";
    L_0x0b75:
        r6 = org.telegram.messenger.LocaleController.getString(r8, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0b99;
    L_0x0b80:
        r6 = r4.megagroup;
        if (r6 == 0) goto L_0x0b8a;
    L_0x0b84:
        r6 = NUM; // 0x7f0e048a float:1.8877394E38 double:1.0531627307E-314;
        r8 = "EventLogRemovedGroupLink";
        goto L_0x0b8f;
    L_0x0b8a:
        r6 = NUM; // 0x7f0e0488 float:1.887739E38 double:1.0531627297E-314;
        r8 = "EventLogRemovedChannelLink";
    L_0x0b8f:
        r6 = org.telegram.messenger.LocaleController.getString(r8, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
    L_0x0b99:
        r6 = new org.telegram.tgnet.TLRPC$TL_message;
        r6.<init>();
        r8 = 0;
        r6.out = r8;
        r6.unread = r8;
        r8 = r1.user_id;
        r6.from_id = r8;
        r6.to_id = r7;
        r7 = r1.date;
        r6.date = r7;
        r7 = android.text.TextUtils.isEmpty(r2);
        if (r7 != 0) goto L_0x0bd7;
    L_0x0bb3:
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "https://";
        r7.append(r8);
        r8 = r0.currentAccount;
        r8 = org.telegram.messenger.MessagesController.getInstance(r8);
        r8 = r8.linkPrefix;
        r7.append(r8);
        r8 = "/";
        r7.append(r8);
        r7.append(r2);
        r2 = r7.toString();
        r6.message = r2;
        goto L_0x0bd9;
    L_0x0bd7:
        r6.message = r13;
    L_0x0bd9:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageEntityUrl;
        r2.<init>();
        r7 = 0;
        r2.offset = r7;
        r7 = r6.message;
        r7 = r7.length();
        r2.length = r7;
        r7 = r6.entities;
        r7.add(r2);
        r2 = r1.action;
        r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r2;
        r2 = r2.prev_value;
        r2 = android.text.TextUtils.isEmpty(r2);
        if (r2 != 0) goto L_0x0CLASSNAME;
    L_0x0bfa:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
        r2.<init>();
        r6.media = r2;
        r2 = r6.media;
        r7 = new org.telegram.tgnet.TLRPC$TL_webPage;
        r7.<init>();
        r2.webpage = r7;
        r2 = r6.media;
        r2 = r2.webpage;
        r7 = 10;
        r2.flags = r7;
        r2.display_url = r13;
        r2.url = r13;
        r7 = NUM; // 0x7f0e047b float:1.8877364E38 double:1.0531627233E-314;
        r8 = "EventLogPreviousLink";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r2.site_name = r7;
        r2 = r6.media;
        r2 = r2.webpage;
        r7 = new java.lang.StringBuilder;
        r7.<init>();
        r8 = "https://";
        r7.append(r8);
        r8 = r0.currentAccount;
        r8 = org.telegram.messenger.MessagesController.getInstance(r8);
        r8 = r8.linkPrefix;
        r7.append(r8);
        r8 = "/";
        r7.append(r8);
        r8 = r1.action;
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r8;
        r8 = r8.prev_value;
        r7.append(r8);
        r7 = r7.toString();
        r2.description = r7;
        goto L_0x0e77;
    L_0x0CLASSNAME:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r2.<init>();
        r6.media = r2;
        goto L_0x0e77;
    L_0x0CLASSNAME:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage;
        if (r2 == 0) goto L_0x0da6;
    L_0x0c5d:
        r6 = new org.telegram.tgnet.TLRPC$TL_message;
        r6.<init>();
        r2 = 0;
        r6.out = r2;
        r6.unread = r2;
        r2 = r1.user_id;
        r6.from_id = r2;
        r6.to_id = r7;
        r2 = r1.date;
        r6.date = r2;
        r2 = r1.action;
        r7 = r2;
        r7 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage) r7;
        r7 = r7.new_message;
        r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage) r2;
        r2 = r2.prev_message;
        r8 = r7.media;
        if (r8 == 0) goto L_0x0d40;
    L_0x0CLASSNAME:
        r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
        if (r9 != 0) goto L_0x0d40;
    L_0x0CLASSNAME:
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r8 != 0) goto L_0x0d40;
    L_0x0CLASSNAME:
        r8 = r7.message;
        r9 = r2.message;
        r8 = android.text.TextUtils.equals(r8, r9);
        r9 = 1;
        r8 = r8 ^ r9;
        r9 = r7.media;
        r9 = r9.getClass();
        r10 = r2.media;
        r10 = r10.getClass();
        if (r9 != r10) goto L_0x0ccb;
    L_0x0ca0:
        r9 = r7.media;
        r9 = r9.photo;
        if (r9 == 0) goto L_0x0cb4;
    L_0x0ca6:
        r10 = r2.media;
        r10 = r10.photo;
        if (r10 == 0) goto L_0x0cb4;
    L_0x0cac:
        r11 = r9.id;
        r9 = r10.id;
        r15 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1));
        if (r15 != 0) goto L_0x0ccb;
    L_0x0cb4:
        r9 = r7.media;
        r9 = r9.document;
        if (r9 == 0) goto L_0x0cc9;
    L_0x0cba:
        r10 = r2.media;
        r10 = r10.document;
        if (r10 == 0) goto L_0x0cc9;
    L_0x0cc0:
        r11 = r9.id;
        r9 = r10.id;
        r15 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1));
        if (r15 == 0) goto L_0x0cc9;
    L_0x0cc8:
        goto L_0x0ccb;
    L_0x0cc9:
        r9 = 0;
        goto L_0x0ccc;
    L_0x0ccb:
        r9 = 1;
    L_0x0ccc:
        if (r9 == 0) goto L_0x0ce0;
    L_0x0cce:
        if (r8 == 0) goto L_0x0ce0;
    L_0x0cd0:
        r9 = NUM; // 0x7f0e0460 float:1.887731E38 double:1.05316271E-314;
        r10 = "EventLogEditedMediaCaption";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r9 = r0.replaceWithLink(r9, r14, r5);
        r0.messageText = r9;
        goto L_0x0d01;
    L_0x0ce0:
        if (r8 == 0) goto L_0x0cf2;
    L_0x0ce2:
        r9 = NUM; // 0x7f0e0458 float:1.8877293E38 double:1.053162706E-314;
        r10 = "EventLogEditedCaption";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r9 = r0.replaceWithLink(r9, r14, r5);
        r0.messageText = r9;
        goto L_0x0d01;
    L_0x0cf2:
        r9 = NUM; // 0x7f0e045f float:1.8877307E38 double:1.0531627095E-314;
        r10 = "EventLogEditedMedia";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r9 = r0.replaceWithLink(r9, r14, r5);
        r0.messageText = r9;
    L_0x0d01:
        r9 = r7.media;
        r6.media = r9;
        if (r8 == 0) goto L_0x0d92;
    L_0x0d07:
        r8 = r6.media;
        r9 = new org.telegram.tgnet.TLRPC$TL_webPage;
        r9.<init>();
        r8.webpage = r9;
        r8 = r6.media;
        r8 = r8.webpage;
        r9 = NUM; // 0x7f0e0476 float:1.8877354E38 double:1.053162721E-314;
        r10 = "EventLogOriginalCaption";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r8.site_name = r9;
        r8 = r2.message;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 == 0) goto L_0x0d37;
    L_0x0d27:
        r2 = r6.media;
        r2 = r2.webpage;
        r8 = NUM; // 0x7f0e0477 float:1.8877356E38 double:1.0531627213E-314;
        r9 = "EventLogOriginalCaptionEmpty";
        r8 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r2.description = r8;
        goto L_0x0d92;
    L_0x0d37:
        r8 = r6.media;
        r8 = r8.webpage;
        r2 = r2.message;
        r8.description = r2;
        goto L_0x0d92;
    L_0x0d40:
        r8 = NUM; // 0x7f0e0461 float:1.8877311E38 double:1.0531627105E-314;
        r9 = "EventLogEditedMessages";
        r8 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r8 = r0.replaceWithLink(r8, r14, r5);
        r0.messageText = r8;
        r8 = r7.message;
        r6.message = r8;
        r8 = new org.telegram.tgnet.TLRPC$TL_messageMediaWebPage;
        r8.<init>();
        r6.media = r8;
        r8 = r6.media;
        r9 = new org.telegram.tgnet.TLRPC$TL_webPage;
        r9.<init>();
        r8.webpage = r9;
        r8 = r6.media;
        r8 = r8.webpage;
        r9 = NUM; // 0x7f0e0478 float:1.8877358E38 double:1.053162722E-314;
        r10 = "EventLogOriginalMessages";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r8.site_name = r9;
        r8 = r2.message;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 == 0) goto L_0x0d8a;
    L_0x0d7a:
        r2 = r6.media;
        r2 = r2.webpage;
        r8 = NUM; // 0x7f0e0477 float:1.8877356E38 double:1.0531627213E-314;
        r9 = "EventLogOriginalCaptionEmpty";
        r8 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r2.description = r8;
        goto L_0x0d92;
    L_0x0d8a:
        r8 = r6.media;
        r8 = r8.webpage;
        r2 = r2.message;
        r8.description = r2;
    L_0x0d92:
        r2 = r7.reply_markup;
        r6.reply_markup = r2;
        r2 = r6.media;
        r2 = r2.webpage;
        if (r2 == 0) goto L_0x0e77;
    L_0x0d9c:
        r7 = 10;
        r2.flags = r7;
        r2.display_url = r13;
        r2.url = r13;
        goto L_0x0e77;
    L_0x0da6:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet;
        if (r2 == 0) goto L_0x0ddc;
    L_0x0daa:
        r2 = r8;
        r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet) r2;
        r2 = r2.new_stickerset;
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet) r8;
        r6 = r8.new_stickerset;
        if (r2 == 0) goto L_0x0dcb;
    L_0x0db5:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
        if (r2 == 0) goto L_0x0dba;
    L_0x0db9:
        goto L_0x0dcb;
    L_0x0dba:
        r2 = NUM; // 0x7f0e0452 float:1.887728E38 double:1.053162703E-314;
        r6 = "EventLogChangedStickersSet";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0dcb:
        r2 = NUM; // 0x7f0e048e float:1.8877402E38 double:1.0531627327E-314;
        r6 = "EventLogRemovedStickersSet";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0ddc:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLocation;
        if (r2 == 0) goto L_0x0e13;
    L_0x0de0:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLocation) r8;
        r2 = r8.new_value;
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelLocationEmpty;
        if (r6 == 0) goto L_0x0df9;
    L_0x0de8:
        r2 = NUM; // 0x7f0e048d float:1.88774E38 double:1.053162732E-314;
        r6 = "EventLogRemovedLocation";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0df9:
        r2 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r2;
        r6 = NUM; // 0x7f0e0450 float:1.8877277E38 double:1.053162702E-314;
        r7 = 1;
        r8 = new java.lang.Object[r7];
        r2 = r2.address;
        r7 = 0;
        r8[r7] = r2;
        r2 = "EventLogChangedLocation";
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r8);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0e13:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSlowMode;
        if (r2 == 0) goto L_0x0e60;
    L_0x0e17:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSlowMode) r8;
        r2 = r8.new_value;
        if (r2 != 0) goto L_0x0e2d;
    L_0x0e1d:
        r2 = NUM; // 0x7f0e04a2 float:1.8877443E38 double:1.0531627426E-314;
        r6 = "EventLogToggledSlowmodeOff";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0e2d:
        if (r2 >= r12) goto L_0x0e36;
    L_0x0e2f:
        r6 = "Seconds";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
        goto L_0x0e4a;
    L_0x0e36:
        r6 = 3600; // 0xe10 float:5.045E-42 double:1.7786E-320;
        if (r2 >= r6) goto L_0x0e42;
    L_0x0e3a:
        r2 = r2 / r12;
        r6 = "Minutes";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
        goto L_0x0e4a;
    L_0x0e42:
        r2 = r2 / r12;
        r2 = r2 / r12;
        r6 = "Hours";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
    L_0x0e4a:
        r6 = NUM; // 0x7f0e04a3 float:1.8877445E38 double:1.053162743E-314;
        r7 = 1;
        r8 = new java.lang.Object[r7];
        r7 = 0;
        r8[r7] = r2;
        r2 = "EventLogToggledSlowmodeOn";
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r8);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e76;
    L_0x0e60:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r6 = "unsupported ";
        r2.append(r6);
        r6 = r1.action;
        r2.append(r6);
        r2 = r2.toString();
        r0.messageText = r2;
    L_0x0e76:
        r6 = 0;
    L_0x0e77:
        r2 = r0.messageOwner;
        if (r2 != 0) goto L_0x0e82;
    L_0x0e7b:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageService;
        r2.<init>();
        r0.messageOwner = r2;
    L_0x0e82:
        r2 = r0.messageOwner;
        r7 = r0.messageText;
        r7 = r7.toString();
        r2.message = r7;
        r2 = r0.messageOwner;
        r7 = r1.user_id;
        r2.from_id = r7;
        r7 = r1.date;
        r2.date = r7;
        r7 = 0;
        r8 = r31[r7];
        r9 = r8 + 1;
        r31[r7] = r9;
        r2.id = r8;
        r8 = r1.id;
        r0.eventId = r8;
        r2.out = r7;
        r8 = new org.telegram.tgnet.TLRPC$TL_peerChannel;
        r8.<init>();
        r2.to_id = r8;
        r2 = r0.messageOwner;
        r8 = r2.to_id;
        r9 = r4.id;
        r8.channel_id = r9;
        r2.unread = r7;
        r7 = r4.megagroup;
        if (r7 == 0) goto L_0x0ec1;
    L_0x0eba:
        r7 = r2.flags;
        r8 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r7 = r7 | r8;
        r2.flags = r7;
    L_0x0ec1:
        r2 = org.telegram.messenger.MediaController.getInstance();
        r7 = r1.action;
        r7 = r7.message;
        if (r7 == 0) goto L_0x0ed0;
    L_0x0ecb:
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty;
        if (r8 != 0) goto L_0x0ed0;
    L_0x0ecf:
        r6 = r7;
    L_0x0ed0:
        if (r6 == 0) goto L_0x0var_;
    L_0x0ed2:
        r7 = 0;
        r6.out = r7;
        r8 = r31[r7];
        r9 = r8 + 1;
        r31[r7] = r9;
        r6.id = r8;
        r6.reply_to_msg_id = r7;
        r7 = r6.flags;
        r8 = -32769; // 0xffffffffffff7fff float:NaN double:NaN;
        r7 = r7 & r8;
        r6.flags = r7;
        r4 = r4.megagroup;
        if (r4 == 0) goto L_0x0ef2;
    L_0x0eeb:
        r4 = r6.flags;
        r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = r4 | r7;
        r6.flags = r4;
    L_0x0ef2:
        r4 = new org.telegram.messenger.MessageObject;
        r15 = r0.currentAccount;
        r17 = 0;
        r18 = 0;
        r19 = 1;
        r7 = r0.eventId;
        r14 = r4;
        r16 = r6;
        r20 = r7;
        r14.<init>(r15, r16, r17, r18, r19, r20);
        r6 = r4.contentType;
        if (r6 < 0) goto L_0x0f2d;
    L_0x0f0a:
        r6 = r2.isPlayingMessage(r4);
        if (r6 == 0) goto L_0x0f1c;
    L_0x0var_:
        r6 = r2.getPlayingMessageObject();
        r7 = r6.audioProgress;
        r4.audioProgress = r7;
        r6 = r6.audioProgressSec;
        r4.audioProgressSec = r6;
    L_0x0f1c:
        r6 = r0.currentAccount;
        r7 = r28;
        r0.createDateArray(r6, r1, r7, r3);
        r6 = r28.size();
        r8 = 1;
        r6 = r6 - r8;
        r7.add(r6, r4);
        goto L_0x0var_;
    L_0x0f2d:
        r7 = r28;
        r4 = -1;
        r0.contentType = r4;
        goto L_0x0var_;
    L_0x0var_:
        r7 = r28;
    L_0x0var_:
        r4 = r0.contentType;
        if (r4 < 0) goto L_0x0fa3;
    L_0x0var_:
        r4 = r0.currentAccount;
        r0.createDateArray(r4, r1, r7, r3);
        r1 = r28.size();
        r3 = 1;
        r1 = r1 - r3;
        r7.add(r1, r0);
        r1 = r0.messageText;
        if (r1 != 0) goto L_0x0f4d;
    L_0x0f4b:
        r0.messageText = r13;
    L_0x0f4d:
        r25.setType();
        r25.measureInlineBotButtons();
        r25.generateCaption();
        r1 = r0.messageOwner;
        r1 = r1.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 == 0) goto L_0x0var_;
    L_0x0f5e:
        r1 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint;
        goto L_0x0var_;
    L_0x0var_:
        r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;
    L_0x0var_:
        r3 = r25.allowsBigEmoji();
        if (r3 == 0) goto L_0x0f6d;
    L_0x0var_:
        r3 = 1;
        r6 = new int[r3];
        goto L_0x0f6e;
    L_0x0f6d:
        r6 = 0;
    L_0x0f6e:
        r3 = r0.messageText;
        r1 = r1.getFontMetricsInt();
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r7 = 0;
        r1 = org.telegram.messenger.Emoji.replaceEmoji(r3, r1, r4, r7, r6);
        r0.messageText = r1;
        r0.checkEmojiOnly(r6);
        r1 = r2.isPlayingMessage(r0);
        if (r1 == 0) goto L_0x0var_;
    L_0x0f8a:
        r1 = r2.getPlayingMessageObject();
        r2 = r1.audioProgress;
        r0.audioProgress = r2;
        r1 = r1.audioProgressSec;
        r0.audioProgressSec = r1;
    L_0x0var_:
        r0.generateLayout(r5);
        r1 = 1;
        r0.layoutCreated = r1;
        r1 = 0;
        r0.generateThumbs(r1);
        r25.checkMediaExistance();
    L_0x0fa3:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.<init>(int, org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent, java.util.ArrayList, java.util.HashMap, org.telegram.tgnet.TLRPC$Chat, int[]):void");
    }

    private String getUserName(User user, ArrayList<MessageEntity> arrayList, int i) {
        String formatName = user == null ? "" : ContactsController.formatName(user.first_name, user.last_name);
        if (i >= 0) {
            TL_messageEntityMentionName tL_messageEntityMentionName = new TL_messageEntityMentionName();
            tL_messageEntityMentionName.user_id = user.id;
            tL_messageEntityMentionName.offset = i;
            tL_messageEntityMentionName.length = formatName.length();
            arrayList.add(tL_messageEntityMentionName);
        }
        if (TextUtils.isEmpty(user.username)) {
            return formatName;
        }
        if (i >= 0) {
            TL_messageEntityMentionName tL_messageEntityMentionName2 = new TL_messageEntityMentionName();
            tL_messageEntityMentionName2.user_id = user.id;
            tL_messageEntityMentionName2.offset = (i + formatName.length()) + 2;
            tL_messageEntityMentionName2.length = user.username.length() + 1;
            arrayList.add(tL_messageEntityMentionName2);
        }
        return String.format("%1$s (@%2$s)", new Object[]{formatName, user.username});
    }

    public void applyNewText() {
        if (!TextUtils.isEmpty(this.messageOwner.message)) {
            TextPaint textPaint;
            int[] iArr = null;
            User user = isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id)) : null;
            Message message = this.messageOwner;
            this.messageText = message.message;
            if (message.media instanceof TL_messageMediaGame) {
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
        if (!SharedConfig.allowBigEmoji) {
            return false;
        }
        Message message = this.messageOwner;
        if (message != null) {
            Peer peer = message.to_id;
            if (!(peer == null || (peer.channel_id == 0 && peer.chat_id == 0))) {
                MessagesController instance = MessagesController.getInstance(this.currentAccount);
                Peer peer2 = this.messageOwner.to_id;
                int i = peer2.channel_id;
                if (i == 0) {
                    i = peer2.chat_id;
                }
                return ChatObject.isActionBanned(instance.getChat(Integer.valueOf(i)), 8) ^ 1;
            }
        }
        return true;
    }

    public void generateGameMessageText(User user) {
        if (user == null && this.messageOwner.from_id > 0) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        TLObject tLObject = null;
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject != null) {
            MessageMedia messageMedia = messageObject.messageOwner.media;
            if (messageMedia != null) {
                TLObject tLObject2 = messageMedia.game;
                if (tLObject2 != null) {
                    tLObject = tLObject2;
                }
            }
        }
        String str = "un1";
        String str2 = "Points";
        if (tLObject != null) {
            if (user == null || user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", NUM, LocaleController.formatPluralString(str2, this.messageOwner.action.score)), str, user);
            } else {
                this.messageText = LocaleController.formatString("ActionYouScoredInGame", NUM, LocaleController.formatPluralString(str2, this.messageOwner.action.score));
            }
            this.messageText = replaceWithLink(this.messageText, "un2", tLObject);
        } else if (user == null || user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", NUM, LocaleController.formatPluralString(str2, this.messageOwner.action.score)), str, user);
        } else {
            this.messageText = LocaleController.formatString("ActionYouScored", NUM, LocaleController.formatPluralString(str2, this.messageOwner.action.score));
        }
    }

    public boolean hasValidReplyMessageObject() {
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject != null) {
            Message message = messageObject.messageOwner;
            if (!((message instanceof TL_messageEmpty) || (message.action instanceof TL_messageActionHistoryClear))) {
                return true;
            }
        }
        return false;
    }

    public void generatePaymentSentMessageText(User user) {
        if (user == null) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int) getDialogId()));
        }
        String firstName = user != null ? UserObject.getFirstName(user) : "";
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject == null || !(messageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
            Object[] objArr = new Object[2];
            LocaleController instance = LocaleController.getInstance();
            MessageAction messageAction = this.messageOwner.action;
            objArr[0] = instance.formatCurrencyString(messageAction.total_amount, messageAction.currency);
            objArr[1] = firstName;
            this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", NUM, objArr);
            return;
        }
        r4 = new Object[3];
        LocaleController instance2 = LocaleController.getInstance();
        MessageAction messageAction2 = this.messageOwner.action;
        r4[0] = instance2.formatCurrencyString(messageAction2.total_amount, messageAction2.currency);
        r4[1] = firstName;
        r4[2] = this.replyMessageObject.messageOwner.media.title;
        this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", NUM, r4);
    }

    public void generatePinMessageText(User user, Chat chat) {
        TLObject user2;
        TLObject chat2;
        String string;
        if (user == null && chat2 == null) {
            if (this.messageOwner.from_id > 0) {
                user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            if (user2 == null) {
                chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
            }
        }
        MessageObject messageObject = this.replyMessageObject;
        String str = "ActionPinnedNoText";
        String str2 = "un1";
        if (messageObject != null) {
            Message message = messageObject.messageOwner;
            if (!((message instanceof TL_messageEmpty) || (message.action instanceof TL_messageActionHistoryClear))) {
                if (messageObject.isMusic()) {
                    string = LocaleController.getString("ActionPinnedMusic", NUM);
                    if (user2 == null) {
                        user2 = chat2;
                    }
                    this.messageText = replaceWithLink(string, str2, user2);
                    return;
                } else if (this.replyMessageObject.isVideo()) {
                    string = LocaleController.getString("ActionPinnedVideo", NUM);
                    if (user2 == null) {
                        user2 = chat2;
                    }
                    this.messageText = replaceWithLink(string, str2, user2);
                    return;
                } else if (this.replyMessageObject.isGif()) {
                    string = LocaleController.getString("ActionPinnedGif", NUM);
                    if (user2 == null) {
                        user2 = chat2;
                    }
                    this.messageText = replaceWithLink(string, str2, user2);
                    return;
                } else if (this.replyMessageObject.isVoice()) {
                    string = LocaleController.getString("ActionPinnedVoice", NUM);
                    if (user2 == null) {
                        user2 = chat2;
                    }
                    this.messageText = replaceWithLink(string, str2, user2);
                    return;
                } else if (this.replyMessageObject.isRoundVideo()) {
                    string = LocaleController.getString("ActionPinnedRound", NUM);
                    if (user2 == null) {
                        user2 = chat2;
                    }
                    this.messageText = replaceWithLink(string, str2, user2);
                    return;
                } else if (this.replyMessageObject.isSticker() || this.replyMessageObject.isAnimatedSticker()) {
                    string = LocaleController.getString("ActionPinnedSticker", NUM);
                    if (user2 == null) {
                        user2 = chat2;
                    }
                    this.messageText = replaceWithLink(string, str2, user2);
                    return;
                } else {
                    messageObject = this.replyMessageObject;
                    MessageMedia messageMedia = messageObject.messageOwner.media;
                    if (messageMedia instanceof TL_messageMediaDocument) {
                        string = LocaleController.getString("ActionPinnedFile", NUM);
                        if (user2 == null) {
                            user2 = chat2;
                        }
                        this.messageText = replaceWithLink(string, str2, user2);
                        return;
                    } else if (messageMedia instanceof TL_messageMediaGeo) {
                        string = LocaleController.getString("ActionPinnedGeo", NUM);
                        if (user2 == null) {
                            user2 = chat2;
                        }
                        this.messageText = replaceWithLink(string, str2, user2);
                        return;
                    } else if (messageMedia instanceof TL_messageMediaGeoLive) {
                        string = LocaleController.getString("ActionPinnedGeoLive", NUM);
                        if (user2 == null) {
                            user2 = chat2;
                        }
                        this.messageText = replaceWithLink(string, str2, user2);
                        return;
                    } else if (messageMedia instanceof TL_messageMediaContact) {
                        string = LocaleController.getString("ActionPinnedContact", NUM);
                        if (user2 == null) {
                            user2 = chat2;
                        }
                        this.messageText = replaceWithLink(string, str2, user2);
                        return;
                    } else if (messageMedia instanceof TL_messageMediaPoll) {
                        string = LocaleController.getString("ActionPinnedPoll", NUM);
                        if (user2 == null) {
                            user2 = chat2;
                        }
                        this.messageText = replaceWithLink(string, str2, user2);
                        return;
                    } else if (messageMedia instanceof TL_messageMediaPhoto) {
                        string = LocaleController.getString("ActionPinnedPhoto", NUM);
                        if (user2 == null) {
                            user2 = chat2;
                        }
                        this.messageText = replaceWithLink(string, str2, user2);
                        return;
                    } else if (messageMedia instanceof TL_messageMediaGame) {
                        Object[] objArr = new Object[1];
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("🎮 ");
                        stringBuilder.append(this.replyMessageObject.messageOwner.media.game.title);
                        objArr[0] = stringBuilder.toString();
                        string = LocaleController.formatString("ActionPinnedGame", NUM, objArr);
                        if (user2 == null) {
                            user2 = chat2;
                        }
                        this.messageText = replaceWithLink(string, str2, user2);
                        this.messageText = Emoji.replaceEmoji(this.messageText, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        return;
                    } else {
                        CharSequence charSequence = messageObject.messageText;
                        if (charSequence == null || charSequence.length() <= 0) {
                            string = LocaleController.getString(str, NUM);
                            if (user2 == null) {
                                user2 = chat2;
                            }
                            this.messageText = replaceWithLink(string, str2, user2);
                            return;
                        }
                        charSequence = this.replyMessageObject.messageText;
                        if (charSequence.length() > 20) {
                            StringBuilder stringBuilder2 = new StringBuilder();
                            stringBuilder2.append(charSequence.subSequence(0, 20));
                            stringBuilder2.append("...");
                            charSequence = stringBuilder2.toString();
                        }
                        charSequence = Emoji.replaceEmoji(charSequence, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        string = LocaleController.formatString("ActionPinnedText", NUM, charSequence);
                        if (user2 == null) {
                            user2 = chat2;
                        }
                        this.messageText = replaceWithLink(string, str2, user2);
                        return;
                    }
                }
            }
        }
        string = LocaleController.getString(str, NUM);
        if (user2 == null) {
            user2 = chat2;
        }
        this.messageText = replaceWithLink(string, str2, user2);
    }

    public static void updateReactions(Message message, TL_messageReactions tL_messageReactions) {
        if (message != null && tL_messageReactions != null) {
            if (tL_messageReactions.min) {
                TL_messageReactions tL_messageReactions2 = message.reactions;
                if (tL_messageReactions2 != null) {
                    int size = tL_messageReactions2.results.size();
                    int i = 0;
                    while (i < size) {
                        TL_reactionCount tL_reactionCount = (TL_reactionCount) message.reactions.results.get(i);
                        if (tL_reactionCount.chosen) {
                            size = tL_messageReactions.results.size();
                            for (int i2 = 0; i2 < size; i2++) {
                                TL_reactionCount tL_reactionCount2 = (TL_reactionCount) tL_messageReactions.results.get(i2);
                                if (tL_reactionCount.reaction.equals(tL_reactionCount2.reaction)) {
                                    tL_reactionCount2.chosen = true;
                                    break;
                                }
                            }
                        } else {
                            i++;
                        }
                    }
                }
            }
            message.reactions = tL_messageReactions;
            message.flags |= 1048576;
        }
    }

    public boolean hasReactions() {
        TL_messageReactions tL_messageReactions = this.messageOwner.reactions;
        return (tL_messageReactions == null || tL_messageReactions.results.isEmpty()) ? false : true;
    }

    public static void updatePollResults(TL_messageMediaPoll tL_messageMediaPoll, TL_pollResults tL_pollResults) {
        if ((tL_pollResults.flags & 2) != 0) {
            int size;
            byte[] bArr = null;
            if (tL_pollResults.min) {
                ArrayList arrayList = tL_messageMediaPoll.results.results;
                if (arrayList != null) {
                    size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        TL_pollAnswerVoters tL_pollAnswerVoters = (TL_pollAnswerVoters) tL_messageMediaPoll.results.results.get(i);
                        if (tL_pollAnswerVoters.chosen) {
                            bArr = tL_pollAnswerVoters.option;
                            break;
                        }
                    }
                }
            }
            TL_pollResults tL_pollResults2 = tL_messageMediaPoll.results;
            tL_pollResults2.results = tL_pollResults.results;
            if (bArr != null) {
                size = tL_pollResults2.results.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TL_pollAnswerVoters tL_pollAnswerVoters2 = (TL_pollAnswerVoters) tL_messageMediaPoll.results.results.get(i2);
                    if (Arrays.equals(tL_pollAnswerVoters2.option, bArr)) {
                        tL_pollAnswerVoters2.chosen = true;
                        break;
                    }
                }
            }
            TL_pollResults tL_pollResults3 = tL_messageMediaPoll.results;
            tL_pollResults3.flags |= 2;
        }
        if ((tL_pollResults.flags & 4) != 0) {
            TL_pollResults tL_pollResults4 = tL_messageMediaPoll.results;
            tL_pollResults4.total_voters = tL_pollResults.total_voters;
            tL_pollResults4.flags |= 4;
        }
    }

    public boolean isPollClosed() {
        if (this.type != 17) {
            return false;
        }
        return ((TL_messageMediaPoll) this.messageOwner.media).poll.closed;
    }

    public boolean isVoted() {
        if (this.type != 17) {
            return false;
        }
        TL_messageMediaPoll tL_messageMediaPoll = (TL_messageMediaPoll) this.messageOwner.media;
        TL_pollResults tL_pollResults = tL_messageMediaPoll.results;
        if (!(tL_pollResults == null || tL_pollResults.results.isEmpty())) {
            int size = tL_messageMediaPoll.results.results.size();
            for (int i = 0; i < size; i++) {
                if (((TL_pollAnswerVoters) tL_messageMediaPoll.results.results.get(i)).chosen) {
                    return true;
                }
            }
        }
        return false;
    }

    public long getPollId() {
        if (this.type != 17) {
            return 0;
        }
        return ((TL_messageMediaPoll) this.messageOwner.media).poll.id;
    }

    private Photo getPhotoWithId(WebPage webPage, long j) {
        if (!(webPage == null || webPage.cached_page == null)) {
            Photo photo = webPage.photo;
            if (photo != null && photo.id == j) {
                return photo;
            }
            for (int i = 0; i < webPage.cached_page.photos.size(); i++) {
                Photo photo2 = (Photo) webPage.cached_page.photos.get(i);
                if (photo2.id == j) {
                    return photo2;
                }
            }
        }
        return null;
    }

    private Document getDocumentWithId(WebPage webPage, long j) {
        if (!(webPage == null || webPage.cached_page == null)) {
            Document document = webPage.document;
            if (document != null && document.id == j) {
                return document;
            }
            for (int i = 0; i < webPage.cached_page.documents.size(); i++) {
                Document document2 = (Document) webPage.cached_page.documents.get(i);
                if (document2.id == j) {
                    return document2;
                }
            }
        }
        return null;
    }

    private MessageObject getMessageObjectForBlock(WebPage webPage, PageBlock pageBlock) {
        Message tL_message;
        if (pageBlock instanceof TL_pageBlockPhoto) {
            Photo photoWithId = getPhotoWithId(webPage, ((TL_pageBlockPhoto) pageBlock).photo_id);
            if (photoWithId == webPage.photo) {
                return this;
            }
            tL_message = new TL_message();
            tL_message.media = new TL_messageMediaPhoto();
            tL_message.media.photo = photoWithId;
        } else if (pageBlock instanceof TL_pageBlockVideo) {
            TL_pageBlockVideo tL_pageBlockVideo = (TL_pageBlockVideo) pageBlock;
            if (getDocumentWithId(webPage, tL_pageBlockVideo.video_id) == webPage.document) {
                return this;
            }
            Message tL_message2 = new TL_message();
            tL_message2.media = new TL_messageMediaDocument();
            tL_message2.media.document = getDocumentWithId(webPage, tL_pageBlockVideo.video_id);
            tL_message = tL_message2;
        } else {
            tL_message = null;
        }
        tL_message.message = "";
        tL_message.realId = getId();
        tL_message.id = Utilities.random.nextInt();
        Message message = this.messageOwner;
        tL_message.date = message.date;
        tL_message.to_id = message.to_id;
        tL_message.out = message.out;
        tL_message.from_id = message.from_id;
        return new MessageObject(this.currentAccount, tL_message, false);
    }

    public ArrayList<MessageObject> getWebPagePhotos(ArrayList<MessageObject> arrayList, ArrayList<PageBlock> arrayList2) {
        if (arrayList == null) {
            arrayList = new ArrayList();
        }
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia != null) {
            WebPage webPage = messageMedia.webpage;
            if (webPage != null) {
                Page page = webPage.cached_page;
                if (page == null) {
                    return arrayList;
                }
                ArrayList arrayList22;
                if (arrayList22 == null) {
                    arrayList22 = page.blocks;
                }
                for (int i = 0; i < arrayList22.size(); i++) {
                    PageBlock pageBlock = (PageBlock) arrayList22.get(i);
                    int i2;
                    if (pageBlock instanceof TL_pageBlockSlideshow) {
                        TL_pageBlockSlideshow tL_pageBlockSlideshow = (TL_pageBlockSlideshow) pageBlock;
                        for (i2 = 0; i2 < tL_pageBlockSlideshow.items.size(); i2++) {
                            arrayList.add(getMessageObjectForBlock(webPage, (PageBlock) tL_pageBlockSlideshow.items.get(i2)));
                        }
                    } else if (pageBlock instanceof TL_pageBlockCollage) {
                        TL_pageBlockCollage tL_pageBlockCollage = (TL_pageBlockCollage) pageBlock;
                        for (i2 = 0; i2 < tL_pageBlockCollage.items.size(); i2++) {
                            arrayList.add(getMessageObjectForBlock(webPage, (PageBlock) tL_pageBlockCollage.items.get(i2)));
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public void createMessageSendInfo() {
        Message message = this.messageOwner;
        if (message.message == null) {
            return;
        }
        if (message.id < 0 || isEditing()) {
            HashMap hashMap = this.messageOwner.params;
            if (hashMap != null) {
                String str = (String) hashMap.get("ve");
                if (str != null && (isVideo() || isNewGif() || isRoundVideo())) {
                    this.videoEditedInfo = new VideoEditedInfo();
                    if (this.videoEditedInfo.parseString(str)) {
                        this.videoEditedInfo.roundVideo = isRoundVideo();
                    } else {
                        this.videoEditedInfo = null;
                    }
                }
                message = this.messageOwner;
                if (message.send_state == 3) {
                    str = (String) message.params.get("prevMedia");
                    if (str != null) {
                        SerializedData serializedData = new SerializedData(Base64.decode(str, 0));
                        this.previousMedia = MessageMedia.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                        this.previousCaption = serializedData.readString(false);
                        this.previousAttachPath = serializedData.readString(false);
                        int readInt32 = serializedData.readInt32(false);
                        this.previousCaptionEntities = new ArrayList(readInt32);
                        for (int i = 0; i < readInt32; i++) {
                            this.previousCaptionEntities.add(MessageEntity.TLdeserialize(serializedData, serializedData.readInt32(false), false));
                        }
                        serializedData.cleanup();
                    }
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x003f  */
    /* JADX WARNING: Missing block: B:5:0x0018, code skipped:
            if (r2.results.isEmpty() == false) goto L_0x001a;
     */
    public void measureInlineBotButtons() {
        /*
        r21 = this;
        r0 = r21;
        r1 = 0;
        r0.wantedBotKeyboardWidth = r1;
        r2 = r0.messageOwner;
        r3 = r2.reply_markup;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
        r4 = 1;
        if (r3 != 0) goto L_0x001a;
    L_0x000e:
        r2 = r2.reactions;
        if (r2 == 0) goto L_0x002d;
    L_0x0012:
        r2 = r2.results;
        r2 = r2.isEmpty();
        if (r2 != 0) goto L_0x002d;
    L_0x001a:
        r2 = 0;
        org.telegram.ui.ActionBar.Theme.createChatResources(r2, r4);
        r2 = r0.botButtonsLayout;
        if (r2 != 0) goto L_0x002a;
    L_0x0022:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r0.botButtonsLayout = r2;
        goto L_0x002d;
    L_0x002a:
        r2.setLength(r1);
    L_0x002d:
        r2 = r0.messageOwner;
        r3 = r2.reply_markup;
        r3 = r3 instanceof org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
        r5 = NUM; // 0x40800000 float:4.0 double:5.34643471E-315;
        r6 = NUM; // 0x40a00000 float:5.0 double:5.356796015E-315;
        r7 = NUM; // 0x41400000 float:12.0 double:5.408602553E-315;
        r8 = NUM; // 0x44fa0000 float:2000.0 double:5.717499035E-315;
        r9 = NUM; // 0x41700000 float:15.0 double:5.424144515E-315;
        if (r3 == 0) goto L_0x00f0;
    L_0x003f:
        r2 = 0;
    L_0x0040:
        r3 = r0.messageOwner;
        r3 = r3.reply_markup;
        r3 = r3.rows;
        r3 = r3.size();
        if (r2 >= r3) goto L_0x0188;
    L_0x004c:
        r3 = r0.messageOwner;
        r3 = r3.reply_markup;
        r3 = r3.rows;
        r3 = r3.get(r2);
        r3 = (org.telegram.tgnet.TLRPC.TL_keyboardButtonRow) r3;
        r4 = r3.buttons;
        r4 = r4.size();
        r10 = 0;
        r11 = 0;
    L_0x0060:
        if (r10 >= r4) goto L_0x00d4;
    L_0x0062:
        r12 = r3.buttons;
        r12 = r12.get(r10);
        r12 = (org.telegram.tgnet.TLRPC.KeyboardButton) r12;
        r13 = r0.botButtonsLayout;
        r13.append(r2);
        r13.append(r10);
        r13 = r12 instanceof org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
        if (r13 == 0) goto L_0x008a;
    L_0x0076:
        r13 = r0.messageOwner;
        r13 = r13.media;
        r13 = r13.flags;
        r13 = r13 & 4;
        if (r13 == 0) goto L_0x008a;
    L_0x0080:
        r12 = NUM; // 0x7f0e0894 float:1.8879492E38 double:1.0531632416E-314;
        r13 = "PaymentReceipt";
        r12 = org.telegram.messenger.LocaleController.getString(r13, r12);
        goto L_0x009a;
    L_0x008a:
        r12 = r12.text;
        r13 = org.telegram.ui.ActionBar.Theme.chat_msgBotButtonPaint;
        r13 = r13.getFontMetricsInt();
        r14 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r12 = org.telegram.messenger.Emoji.replaceEmoji(r12, r13, r14, r1);
    L_0x009a:
        r14 = r12;
        r12 = new android.text.StaticLayout;
        r15 = org.telegram.ui.ActionBar.Theme.chat_msgBotButtonPaint;
        r16 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r17 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r18 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r19 = 0;
        r20 = 0;
        r13 = r12;
        r13.<init>(r14, r15, r16, r17, r18, r19, r20);
        r13 = r12.getLineCount();
        if (r13 <= 0) goto L_0x00d1;
    L_0x00b5:
        r13 = r12.getLineWidth(r1);
        r12 = r12.getLineLeft(r1);
        r14 = (r12 > r13 ? 1 : (r12 == r13 ? 0 : -1));
        if (r14 >= 0) goto L_0x00c2;
    L_0x00c1:
        r13 = r13 - r12;
    L_0x00c2:
        r12 = (double) r13;
        r12 = java.lang.Math.ceil(r12);
        r12 = (int) r12;
        r13 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r12 = r12 + r13;
        r11 = java.lang.Math.max(r11, r12);
    L_0x00d1:
        r10 = r10 + 1;
        goto L_0x0060;
    L_0x00d4:
        r3 = r0.wantedBotKeyboardWidth;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r11 = r11 + r10;
        r11 = r11 * r4;
        r10 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r4 = r4 + -1;
        r10 = r10 * r4;
        r11 = r11 + r10;
        r3 = java.lang.Math.max(r3, r11);
        r0.wantedBotKeyboardWidth = r3;
        r2 = r2 + 1;
        goto L_0x0040;
    L_0x00f0:
        r2 = r2.reactions;
        if (r2 == 0) goto L_0x0188;
    L_0x00f4:
        r2 = r2.results;
        r2 = r2.size();
        r3 = 0;
    L_0x00fb:
        if (r3 >= r2) goto L_0x0188;
    L_0x00fd:
        r10 = r0.messageOwner;
        r10 = r10.reactions;
        r10 = r10.results;
        r10 = r10.get(r3);
        r10 = (org.telegram.tgnet.TLRPC.TL_reactionCount) r10;
        r11 = r0.botButtonsLayout;
        r11.append(r1);
        r11.append(r3);
        r11 = 2;
        r11 = new java.lang.Object[r11];
        r12 = r10.count;
        r12 = java.lang.Integer.valueOf(r12);
        r11[r1] = r12;
        r10 = r10.reaction;
        r11[r4] = r10;
        r10 = "%d %s";
        r10 = java.lang.String.format(r10, r11);
        r11 = org.telegram.ui.ActionBar.Theme.chat_msgBotButtonPaint;
        r11 = r11.getFontMetricsInt();
        r12 = org.telegram.messenger.AndroidUtilities.dp(r9);
        r14 = org.telegram.messenger.Emoji.replaceEmoji(r10, r11, r12, r1);
        r10 = new android.text.StaticLayout;
        r15 = org.telegram.ui.ActionBar.Theme.chat_msgBotButtonPaint;
        r16 = org.telegram.messenger.AndroidUtilities.dp(r8);
        r17 = android.text.Layout.Alignment.ALIGN_NORMAL;
        r18 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r19 = 0;
        r20 = 0;
        r13 = r10;
        r13.<init>(r14, r15, r16, r17, r18, r19, r20);
        r11 = r10.getLineCount();
        if (r11 <= 0) goto L_0x016b;
    L_0x014e:
        r11 = r10.getLineWidth(r1);
        r10 = r10.getLineLeft(r1);
        r12 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1));
        if (r12 >= 0) goto L_0x015b;
    L_0x015a:
        r11 = r11 - r10;
    L_0x015b:
        r10 = (double) r11;
        r10 = java.lang.Math.ceil(r10);
        r10 = (int) r10;
        r11 = org.telegram.messenger.AndroidUtilities.dp(r5);
        r10 = r10 + r11;
        r10 = java.lang.Math.max(r1, r10);
        goto L_0x016c;
    L_0x016b:
        r10 = 0;
    L_0x016c:
        r11 = r0.wantedBotKeyboardWidth;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r10 = r10 + r12;
        r10 = r10 * r2;
        r12 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r13 = r2 + -1;
        r12 = r12 * r13;
        r10 = r10 + r12;
        r10 = java.lang.Math.max(r11, r10);
        r0.wantedBotKeyboardWidth = r10;
        r3 = r3 + 1;
        goto L_0x00fb;
    L_0x0188:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.measureInlineBotButtons():void");
    }

    public boolean isFcmMessage() {
        return this.localType != 0;
    }

    private void updateMessageText(AbstractMap<Integer, User> abstractMap, AbstractMap<Integer, Chat> abstractMap2, SparseArray<User> sparseArray, SparseArray<Chat> sparseArray2) {
        TLObject tLObject;
        AbstractMap<Integer, User> abstractMap3 = abstractMap;
        AbstractMap<Integer, Chat> abstractMap4 = abstractMap2;
        SparseArray<User> sparseArray3 = sparseArray;
        SparseArray<Chat> sparseArray4 = sparseArray2;
        int i = this.messageOwner.from_id;
        if (i > 0) {
            TLObject tLObject2 = abstractMap3 != null ? (User) abstractMap3.get(Integer.valueOf(i)) : sparseArray3 != null ? (User) sparseArray3.get(i) : null;
            if (tLObject2 == null) {
                tLObject2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            tLObject = tLObject2;
        } else {
            tLObject = null;
        }
        Message message = this.messageOwner;
        String str = "";
        String stringBuilder;
        if (message instanceof TL_messageService) {
            MessageAction messageAction = message.action;
            if (messageAction != null) {
                if (messageAction instanceof TL_messageActionCustomAction) {
                    this.messageText = messageAction.message;
                } else {
                    String str2 = "un1";
                    if (!(messageAction instanceof TL_messageActionChatCreate)) {
                        String str3 = "un2";
                        int i2;
                        TLObject tLObject3;
                        if (messageAction instanceof TL_messageActionChatDeleteUser) {
                            i2 = messageAction.user_id;
                            if (i2 != message.from_id) {
                                tLObject3 = abstractMap3 != null ? (User) abstractMap3.get(Integer.valueOf(i2)) : sparseArray3 != null ? (User) sparseArray3.get(i2) : null;
                                if (tLObject3 == null) {
                                    tLObject3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.action.user_id));
                                }
                                if (isOut()) {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", NUM), str3, tLObject3);
                                } else if (this.messageOwner.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", NUM), str2, tLObject);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", NUM), str3, tLObject3);
                                    this.messageText = replaceWithLink(this.messageText, str2, tLObject);
                                }
                            } else if (isOut()) {
                                this.messageText = LocaleController.getString("ActionYouLeftUser", NUM);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", NUM), str2, tLObject);
                            }
                        } else if (messageAction instanceof TL_messageActionChatAddUser) {
                            i2 = messageAction.user_id;
                            if (i2 == 0 && messageAction.users.size() == 1) {
                                i2 = ((Integer) this.messageOwner.action.users.get(0)).intValue();
                            }
                            String str4 = "ActionYouAddUser";
                            String str5 = "ActionAddUser";
                            if (i2 != 0) {
                                tLObject3 = abstractMap3 != null ? (User) abstractMap3.get(Integer.valueOf(i2)) : sparseArray3 != null ? (User) sparseArray3.get(i2) : null;
                                if (tLObject3 == null) {
                                    tLObject3 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i2));
                                }
                                Message message2 = this.messageOwner;
                                if (i2 == message2.from_id) {
                                    if (message2.to_id.channel_id != 0 && !isMegagroup()) {
                                        this.messageText = LocaleController.getString("ChannelJoined", NUM);
                                    } else if (this.messageOwner.to_id.channel_id == 0 || !isMegagroup()) {
                                        if (isOut()) {
                                            this.messageText = LocaleController.getString("ActionAddUserSelfYou", NUM);
                                        } else {
                                            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", NUM), str2, tLObject);
                                        }
                                    } else if (i2 == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                        this.messageText = LocaleController.getString("ChannelMegaJoined", NUM);
                                    } else {
                                        this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", NUM), str2, tLObject);
                                    }
                                } else if (isOut()) {
                                    this.messageText = replaceWithLink(LocaleController.getString(str4, NUM), str3, tLObject3);
                                } else if (i2 != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                    this.messageText = replaceWithLink(LocaleController.getString(str5, NUM), str3, tLObject3);
                                    this.messageText = replaceWithLink(this.messageText, str2, tLObject);
                                } else if (this.messageOwner.to_id.channel_id == 0) {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", NUM), str2, tLObject);
                                } else if (isMegagroup()) {
                                    this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", NUM), str2, tLObject);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", NUM), str2, tLObject);
                                }
                            } else if (isOut()) {
                                this.messageText = replaceWithLink(LocaleController.getString(str4, NUM), "un2", this.messageOwner.action.users, abstractMap, sparseArray);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString(str5, NUM), "un2", this.messageOwner.action.users, abstractMap, sparseArray);
                                this.messageText = replaceWithLink(this.messageText, str2, tLObject);
                            }
                        } else if (messageAction instanceof TL_messageActionChatJoinedByLink) {
                            if (isOut()) {
                                this.messageText = LocaleController.getString("ActionInviteYou", NUM);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", NUM), str2, tLObject);
                            }
                        } else if (messageAction instanceof TL_messageActionChatEditPhoto) {
                            if (message.to_id.channel_id != 0 && !isMegagroup()) {
                                this.messageText = LocaleController.getString("ActionChannelChangedPhoto", NUM);
                            } else if (isOut()) {
                                this.messageText = LocaleController.getString("ActionYouChangedPhoto", NUM);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", NUM), str2, tLObject);
                            }
                        } else if (messageAction instanceof TL_messageActionChatEditTitle) {
                            if (message.to_id.channel_id != 0 && !isMegagroup()) {
                                this.messageText = LocaleController.getString("ActionChannelChangedTitle", NUM).replace(str3, this.messageOwner.action.title);
                            } else if (isOut()) {
                                this.messageText = LocaleController.getString("ActionYouChangedTitle", NUM).replace(str3, this.messageOwner.action.title);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", NUM).replace(str3, this.messageOwner.action.title), str2, tLObject);
                            }
                        } else if (!(messageAction instanceof TL_messageActionChatDeletePhoto)) {
                            String str6 = "MessageLifetimeYouRemoved";
                            str3 = "MessageLifetimeRemoved";
                            StringBuilder stringBuilder2;
                            String firstName;
                            SpannableString spannableString;
                            int dialogId;
                            User user;
                            if (messageAction instanceof TL_messageActionTTLChange) {
                                if (messageAction.ttl != 0) {
                                    if (isOut()) {
                                        this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", NUM, LocaleController.formatTTLString(this.messageOwner.action.ttl));
                                    } else {
                                        this.messageText = LocaleController.formatString("MessageLifetimeChanged", NUM, UserObject.getFirstName(tLObject), LocaleController.formatTTLString(this.messageOwner.action.ttl));
                                    }
                                } else if (isOut()) {
                                    this.messageText = LocaleController.getString(str6, NUM);
                                } else {
                                    this.messageText = LocaleController.formatString(str3, NUM, UserObject.getFirstName(tLObject));
                                }
                            } else if (messageAction instanceof TL_messageActionLoginUnknownLocation) {
                                long j = ((long) message.date) * 1000;
                                if (LocaleController.getInstance().formatterDay == null || LocaleController.getInstance().formatterYear == null) {
                                    stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append(str);
                                    stringBuilder2.append(this.messageOwner.date);
                                    stringBuilder = stringBuilder2.toString();
                                } else {
                                    stringBuilder = LocaleController.formatString("formatDateAtTime", NUM, LocaleController.getInstance().formatterYear.format(j), LocaleController.getInstance().formatterDay.format(j));
                                }
                                User currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                                if (currentUser == null) {
                                    if (abstractMap3 != null) {
                                        currentUser = (User) abstractMap3.get(Integer.valueOf(this.messageOwner.to_id.user_id));
                                    } else if (sparseArray3 != null) {
                                        currentUser = (User) sparseArray3.get(this.messageOwner.to_id.user_id);
                                    }
                                    if (currentUser == null) {
                                        currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
                                    }
                                }
                                firstName = currentUser != null ? UserObject.getFirstName(currentUser) : str;
                                r3 = new Object[4];
                                MessageAction messageAction2 = this.messageOwner.action;
                                r3[2] = messageAction2.title;
                                r3[3] = messageAction2.address;
                                this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", NUM, r3);
                            } else if ((messageAction instanceof TL_messageActionUserJoined) || (messageAction instanceof TL_messageActionContactSignUp)) {
                                this.messageText = LocaleController.formatString("NotificationContactJoined", NUM, UserObject.getUserName(tLObject));
                            } else if (messageAction instanceof TL_messageActionUserUpdatedPhoto) {
                                this.messageText = LocaleController.formatString("NotificationContactNewPhoto", NUM, UserObject.getUserName(tLObject));
                            } else if (messageAction instanceof TL_messageEncryptedAction) {
                                DecryptedMessageAction decryptedMessageAction = messageAction.encryptedAction;
                                if (decryptedMessageAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                                    if (isOut()) {
                                        this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", NUM, new Object[0]);
                                    } else {
                                        this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", NUM), str2, tLObject);
                                    }
                                } else if (decryptedMessageAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                                    if (((TL_decryptedMessageActionSetMessageTTL) decryptedMessageAction).ttl_seconds != 0) {
                                        if (isOut()) {
                                            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", NUM, LocaleController.formatTTLString(r0.ttl_seconds));
                                        } else {
                                            this.messageText = LocaleController.formatString("MessageLifetimeChanged", NUM, UserObject.getFirstName(tLObject), LocaleController.formatTTLString(r0.ttl_seconds));
                                        }
                                    } else if (isOut()) {
                                        this.messageText = LocaleController.getString(str6, NUM);
                                    } else {
                                        this.messageText = LocaleController.formatString(str3, NUM, UserObject.getFirstName(tLObject));
                                    }
                                }
                            } else if (messageAction instanceof TL_messageActionScreenshotTaken) {
                                if (isOut()) {
                                    this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", NUM, new Object[0]);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", NUM), str2, tLObject);
                                }
                            } else if (messageAction instanceof TL_messageActionCreatedBroadcastList) {
                                this.messageText = LocaleController.formatString("YouCreatedBroadcastList", NUM, new Object[0]);
                            } else if (messageAction instanceof TL_messageActionChannelCreate) {
                                if (isMegagroup()) {
                                    this.messageText = LocaleController.getString("ActionCreateMega", NUM);
                                } else {
                                    this.messageText = LocaleController.getString("ActionCreateChannel", NUM);
                                }
                            } else if (messageAction instanceof TL_messageActionChatMigrateTo) {
                                this.messageText = LocaleController.getString("ActionMigrateFromGroup", NUM);
                            } else if (messageAction instanceof TL_messageActionChannelMigrateFrom) {
                                this.messageText = LocaleController.getString("ActionMigrateFromGroup", NUM);
                            } else if (messageAction instanceof TL_messageActionPinMessage) {
                                Chat chat;
                                if (tLObject == null) {
                                    if (abstractMap4 != null) {
                                        chat = (Chat) abstractMap4.get(Integer.valueOf(message.to_id.channel_id));
                                    } else if (sparseArray4 != null) {
                                        chat = (Chat) sparseArray4.get(message.to_id.channel_id);
                                    }
                                    generatePinMessageText(tLObject, chat);
                                }
                                chat = null;
                                generatePinMessageText(tLObject, chat);
                            } else if (messageAction instanceof TL_messageActionHistoryClear) {
                                this.messageText = LocaleController.getString("HistoryCleared", NUM);
                            } else if (messageAction instanceof TL_messageActionGameScore) {
                                generateGameMessageText(tLObject);
                            } else if (messageAction instanceof TL_messageActionPhoneCall) {
                                TL_messageActionPhoneCall tL_messageActionPhoneCall = (TL_messageActionPhoneCall) messageAction;
                                boolean z = tL_messageActionPhoneCall.reason instanceof TL_phoneCallDiscardReasonMissed;
                                if (message.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                    if (z) {
                                        this.messageText = LocaleController.getString("CallMessageOutgoingMissed", NUM);
                                    } else {
                                        this.messageText = LocaleController.getString("CallMessageOutgoing", NUM);
                                    }
                                } else if (z) {
                                    this.messageText = LocaleController.getString("CallMessageIncomingMissed", NUM);
                                } else if (tL_messageActionPhoneCall.reason instanceof TL_phoneCallDiscardReasonBusy) {
                                    this.messageText = LocaleController.getString("CallMessageIncomingDeclined", NUM);
                                } else {
                                    this.messageText = LocaleController.getString("CallMessageIncoming", NUM);
                                }
                                i2 = tL_messageActionPhoneCall.duration;
                                if (i2 > 0) {
                                    stringBuilder = LocaleController.formatCallDuration(i2);
                                    this.messageText = LocaleController.formatString("CallMessageWithDuration", NUM, this.messageText, stringBuilder);
                                    firstName = this.messageText.toString();
                                    i = firstName.indexOf(stringBuilder);
                                    if (i != -1) {
                                        spannableString = new SpannableString(this.messageText);
                                        i2 = stringBuilder.length() + i;
                                        if (i > 0 && firstName.charAt(i - 1) == '(') {
                                            i--;
                                        }
                                        if (i2 < firstName.length() && firstName.charAt(i2) == ')') {
                                            i2++;
                                        }
                                        spannableString.setSpan(new TypefaceSpan(Typeface.DEFAULT), i, i2, 0);
                                        this.messageText = spannableString;
                                    }
                                }
                            } else if (messageAction instanceof TL_messageActionPaymentSent) {
                                dialogId = (int) getDialogId();
                                user = abstractMap3 != null ? (User) abstractMap3.get(Integer.valueOf(dialogId)) : sparseArray3 != null ? (User) sparseArray3.get(dialogId) : null;
                                if (user == null) {
                                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(dialogId));
                                }
                                generatePaymentSentMessageText(user);
                            } else if (messageAction instanceof TL_messageActionBotAllowed) {
                                stringBuilder = ((TL_messageActionBotAllowed) messageAction).domain;
                                firstName = LocaleController.getString("ActionBotAllowed", NUM);
                                i = firstName.indexOf("%1$s");
                                spannableString = new SpannableString(String.format(firstName, new Object[]{stringBuilder}));
                                if (i >= 0) {
                                    StringBuilder stringBuilder3 = new StringBuilder();
                                    stringBuilder3.append("http://");
                                    stringBuilder3.append(stringBuilder);
                                    spannableString.setSpan(new URLSpanNoUnderlineBold(stringBuilder3.toString()), i, stringBuilder.length() + i, 33);
                                }
                                this.messageText = spannableString;
                            } else if (messageAction instanceof TL_messageActionSecureValuesSent) {
                                TL_messageActionSecureValuesSent tL_messageActionSecureValuesSent = (TL_messageActionSecureValuesSent) messageAction;
                                stringBuilder2 = new StringBuilder();
                                dialogId = tL_messageActionSecureValuesSent.types.size();
                                for (i = 0; i < dialogId; i++) {
                                    SecureValueType secureValueType = (SecureValueType) tL_messageActionSecureValuesSent.types.get(i);
                                    if (stringBuilder2.length() > 0) {
                                        stringBuilder2.append(", ");
                                    }
                                    if (secureValueType instanceof TL_secureValueTypePhone) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentPhone", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypeEmail) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentEmail", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypeAddress) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentAddress", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypePersonalDetails) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentIdentity", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypePassport) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentPassport", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypeDriverLicense) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentDriverLicence", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypeIdentityCard) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentIdentityCard", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypeUtilityBill) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentUtilityBill", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypeBankStatement) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentBankStatement", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypeRentalAgreement) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentRentalAgreement", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypeInternalPassport) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentInternalPassport", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypePassportRegistration) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentPassportRegistration", NUM));
                                    } else if (secureValueType instanceof TL_secureValueTypeTemporaryRegistration) {
                                        stringBuilder2.append(LocaleController.getString("ActionBotDocumentTemporaryRegistration", NUM));
                                    }
                                }
                                Peer peer = this.messageOwner.to_id;
                                if (peer != null) {
                                    user = abstractMap3 != null ? (User) abstractMap3.get(Integer.valueOf(peer.user_id)) : sparseArray3 != null ? (User) sparseArray3.get(peer.user_id) : null;
                                    if (user == null) {
                                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
                                    }
                                } else {
                                    user = null;
                                }
                                this.messageText = LocaleController.formatString("ActionBotDocuments", NUM, UserObject.getFirstName(user), stringBuilder2.toString());
                            }
                        } else if (message.to_id.channel_id != 0 && !isMegagroup()) {
                            this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", NUM);
                        } else if (isOut()) {
                            this.messageText = LocaleController.getString("ActionYouRemovedPhoto", NUM);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", NUM), str2, tLObject);
                        }
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouCreateGroup", NUM);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", NUM), str2, tLObject);
                    }
                }
            }
        } else {
            this.isRestrictedMessage = false;
            stringBuilder = MessagesController.getRestrictionReason(message.restriction_reason);
            if (!TextUtils.isEmpty(stringBuilder)) {
                this.messageText = stringBuilder;
                this.isRestrictedMessage = true;
            } else if (isMediaEmpty()) {
                this.messageText = this.messageOwner.message;
            } else {
                Message message3 = this.messageOwner;
                MessageMedia messageMedia = message3.media;
                if (messageMedia instanceof TL_messageMediaPoll) {
                    this.messageText = LocaleController.getString("Poll", NUM);
                } else if (messageMedia instanceof TL_messageMediaPhoto) {
                    if (messageMedia.ttl_seconds == 0 || (message3 instanceof TL_message_secret)) {
                        this.messageText = LocaleController.getString("AttachPhoto", NUM);
                    } else {
                        this.messageText = LocaleController.getString("AttachDestructingPhoto", NUM);
                    }
                } else if (isVideo() || ((this.messageOwner.media instanceof TL_messageMediaDocument) && (getDocument() instanceof TL_documentEmpty) && this.messageOwner.media.ttl_seconds != 0)) {
                    message3 = this.messageOwner;
                    if (message3.media.ttl_seconds == 0 || (message3 instanceof TL_message_secret)) {
                        this.messageText = LocaleController.getString("AttachVideo", NUM);
                    } else {
                        this.messageText = LocaleController.getString("AttachDestructingVideo", NUM);
                    }
                } else if (isVoice()) {
                    this.messageText = LocaleController.getString("AttachAudio", NUM);
                } else if (isRoundVideo()) {
                    this.messageText = LocaleController.getString("AttachRound", NUM);
                } else {
                    message3 = this.messageOwner;
                    messageMedia = message3.media;
                    if ((messageMedia instanceof TL_messageMediaGeo) || (messageMedia instanceof TL_messageMediaVenue)) {
                        this.messageText = LocaleController.getString("AttachLocation", NUM);
                    } else if (messageMedia instanceof TL_messageMediaGeoLive) {
                        this.messageText = LocaleController.getString("AttachLiveLocation", NUM);
                    } else if (messageMedia instanceof TL_messageMediaContact) {
                        this.messageText = LocaleController.getString("AttachContact", NUM);
                        if (!TextUtils.isEmpty(this.messageOwner.media.vcard)) {
                            this.vCardData = VCardData.parse(this.messageOwner.media.vcard);
                        }
                    } else if (messageMedia instanceof TL_messageMediaGame) {
                        this.messageText = message3.message;
                    } else if (messageMedia instanceof TL_messageMediaInvoice) {
                        this.messageText = messageMedia.description;
                    } else if (messageMedia instanceof TL_messageMediaUnsupported) {
                        this.messageText = LocaleController.getString("UnsupportedMedia", NUM);
                    } else if (messageMedia instanceof TL_messageMediaDocument) {
                        if (isSticker() || isAnimatedSticker()) {
                            stringBuilder = getStrickerChar();
                            if (stringBuilder == null || stringBuilder.length() <= 0) {
                                this.messageText = LocaleController.getString("AttachSticker", NUM);
                            } else {
                                this.messageText = String.format("%s %s", new Object[]{stringBuilder, LocaleController.getString("AttachSticker", NUM)});
                            }
                        } else if (isMusic()) {
                            this.messageText = LocaleController.getString("AttachMusic", NUM);
                        } else if (isGif()) {
                            this.messageText = LocaleController.getString("AttachGif", NUM);
                        } else {
                            stringBuilder = FileLoader.getDocumentFileName(getDocument());
                            if (stringBuilder == null || stringBuilder.length() <= 0) {
                                this.messageText = LocaleController.getString("AttachDocument", NUM);
                            } else {
                                this.messageText = stringBuilder;
                            }
                        }
                    }
                }
            }
        }
        if (this.messageText == null) {
            this.messageText = str;
        }
    }

    public void setType() {
        int i = this.type;
        this.isRoundVideoCached = 0;
        Message message = this.messageOwner;
        if ((message instanceof TL_message) || (message instanceof TL_messageForwarded_old2)) {
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
                MessageMedia messageMedia = this.messageOwner.media;
                if (messageMedia.ttl_seconds == 0 || !((messageMedia.photo instanceof TL_photoEmpty) || (getDocument() instanceof TL_documentEmpty))) {
                    messageMedia = this.messageOwner.media;
                    if (messageMedia instanceof TL_messageMediaPhoto) {
                        this.type = 1;
                    } else if ((messageMedia instanceof TL_messageMediaGeo) || (messageMedia instanceof TL_messageMediaVenue) || (messageMedia instanceof TL_messageMediaGeoLive)) {
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
                        messageMedia = this.messageOwner.media;
                        if (messageMedia instanceof TL_messageMediaContact) {
                            this.type = 12;
                        } else if (messageMedia instanceof TL_messageMediaPoll) {
                            this.type = 17;
                        } else if (messageMedia instanceof TL_messageMediaUnsupported) {
                            this.type = 0;
                        } else if (messageMedia instanceof TL_messageMediaDocument) {
                            Document document = getDocument();
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
                        } else if (messageMedia instanceof TL_messageMediaGame) {
                            this.type = 0;
                        } else if (messageMedia instanceof TL_messageMediaInvoice) {
                            this.type = 0;
                        }
                    }
                } else {
                    this.contentType = 1;
                    this.type = 10;
                }
            }
        } else if (message instanceof TL_messageService) {
            MessageAction messageAction = message.action;
            if (messageAction instanceof TL_messageActionLoginUnknownLocation) {
                this.type = 0;
            } else if ((messageAction instanceof TL_messageActionChatEditPhoto) || (messageAction instanceof TL_messageActionUserUpdatedPhoto)) {
                this.contentType = 1;
                this.type = 11;
            } else if (messageAction instanceof TL_messageEncryptedAction) {
                DecryptedMessageAction decryptedMessageAction = messageAction.encryptedAction;
                if ((decryptedMessageAction instanceof TL_decryptedMessageActionScreenshotMessages) || (decryptedMessageAction instanceof TL_decryptedMessageActionSetMessageTTL)) {
                    this.contentType = 1;
                    this.type = 10;
                } else {
                    this.contentType = -1;
                    this.type = -1;
                }
            } else if (messageAction instanceof TL_messageActionHistoryClear) {
                this.contentType = -1;
                this.type = -1;
            } else if (messageAction instanceof TL_messageActionPhoneCall) {
                this.type = 16;
            } else {
                this.contentType = 1;
                this.type = 10;
            }
        }
        if (i != 1000 && i != this.type) {
            updateMessageText(MessagesController.getInstance(this.currentAccount).getUsers(), MessagesController.getInstance(this.currentAccount).getChats(), null, null);
            generateThumbs(false);
        }
    }

    public boolean checkLayout() {
        if (this.type == 0 && this.messageOwner.to_id != null) {
            CharSequence charSequence = this.messageText;
            if (!(charSequence == null || charSequence.length() == 0)) {
                if (this.layoutCreated) {
                    if (Math.abs(this.generatedWithMinSize - (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) > AndroidUtilities.dp(52.0f) || this.generatedWithDensity != AndroidUtilities.density) {
                        this.layoutCreated = false;
                    }
                }
                if (!this.layoutCreated) {
                    TextPaint textPaint;
                    this.layoutCreated = true;
                    int[] iArr = null;
                    User user = isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id)) : null;
                    if (this.messageOwner.media instanceof TL_messageMediaGame) {
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
        }
        return false;
    }

    public void resetLayout() {
        this.layoutCreated = false;
    }

    public String getMimeType() {
        Document document = getDocument();
        if (document != null) {
            return document.mime_type;
        }
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TL_messageMediaInvoice) {
            WebDocument webDocument = ((TL_messageMediaInvoice) messageMedia).photo;
            if (webDocument != null) {
                return webDocument.mime_type;
            }
        }
        String str = "image/jpeg";
        if (messageMedia instanceof TL_messageMediaPhoto) {
            return str;
        }
        if ((messageMedia instanceof TL_messageMediaWebPage) && messageMedia.webpage.photo != null) {
            return str;
        }
        return "";
    }

    public boolean canPreviewDocument() {
        return canPreviewDocument(getDocument());
    }

    public static boolean isGifDocument(WebFile webFile) {
        return webFile != null && (webFile.mime_type.equals("image/gif") || isNewGifDocument(webFile));
    }

    public static boolean isGifDocument(Document document) {
        if (document != null) {
            String str = document.mime_type;
            if (str != null && (str.equals("image/gif") || isNewGifDocument(document))) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDocumentHasThumb(Document document) {
        if (!(document == null || document.thumbs.isEmpty())) {
            int size = document.thumbs.size();
            for (int i = 0; i < size; i++) {
                PhotoSize photoSize = (PhotoSize) document.thumbs.get(i);
                if (photoSize != null && !(photoSize instanceof TL_photoSizeEmpty) && !(photoSize.location instanceof TL_fileLocationUnavailable)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canPreviewDocument(Document document) {
        boolean z = false;
        if (document != null) {
            String str = document.mime_type;
            if (str != null) {
                str = str.toLowerCase();
                if (isDocumentHasThumb(document) && (str.equals("image/png") || str.equals("image/jpg") || str.equals("image/jpeg"))) {
                    for (int i = 0; i < document.attributes.size(); i++) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                        if (documentAttribute instanceof TL_documentAttributeImageSize) {
                            TL_documentAttributeImageSize tL_documentAttributeImageSize = (TL_documentAttributeImageSize) documentAttribute;
                            if (tL_documentAttributeImageSize.w < 6000 && tL_documentAttributeImageSize.h < 6000) {
                                z = true;
                            }
                            return z;
                        }
                    }
                } else if (BuildVars.DEBUG_PRIVATE_VERSION) {
                    String documentFileName = FileLoader.getDocumentFileName(document);
                    if ((documentFileName.startsWith("tg_secret_sticker") && documentFileName.endsWith("json")) || documentFileName.endsWith(".svg")) {
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean isRoundVideoDocument(Document document) {
        if (document != null) {
            if ("video/mp4".equals(document.mime_type)) {
                boolean z = false;
                int i = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < document.attributes.size(); i3++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i3);
                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                        i2 = documentAttribute.w;
                        z = documentAttribute.round_message;
                        i = i2;
                    }
                }
                if (z && i <= 1280 && i2 <= 1280) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNewGifDocument(WebFile webFile) {
        if (webFile != null) {
            if ("video/mp4".equals(webFile.mime_type)) {
                int i = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < webFile.attributes.size(); i3++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) webFile.attributes.get(i3);
                    if (!(documentAttribute instanceof TL_documentAttributeAnimated) && (documentAttribute instanceof TL_documentAttributeVideo)) {
                        i = documentAttribute.w;
                        i2 = documentAttribute.h;
                    }
                }
                if (i <= 1280 && i2 <= 1280) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isNewGifDocument(Document document) {
        if (document != null) {
            if ("video/mp4".equals(document.mime_type)) {
                Object obj = null;
                int i = 0;
                int i2 = 0;
                for (int i3 = 0; i3 < document.attributes.size(); i3++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i3);
                    if (documentAttribute instanceof TL_documentAttributeAnimated) {
                        obj = 1;
                    } else if (documentAttribute instanceof TL_documentAttributeVideo) {
                        i = documentAttribute.w;
                        i2 = documentAttribute.h;
                    }
                }
                if (obj == null || i > 1280 || i2 > 1280) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public void generateThumbs(boolean z) {
        Message message = this.messageOwner;
        Photo photo;
        ArrayList arrayList;
        int i;
        PhotoSize photoSize;
        int i2;
        PhotoSize photoSize2;
        if (message instanceof TL_messageService) {
            MessageAction messageAction = message.action;
            if (messageAction instanceof TL_messageActionChatEditPhoto) {
                photo = messageAction.photo;
                if (z) {
                    arrayList = this.photoThumbs;
                    if (!(arrayList == null || arrayList.isEmpty())) {
                        for (i = 0; i < this.photoThumbs.size(); i++) {
                            photoSize = (PhotoSize) this.photoThumbs.get(i);
                            for (i2 = 0; i2 < photo.sizes.size(); i2++) {
                                photoSize2 = (PhotoSize) photo.sizes.get(i2);
                                if (!(photoSize2 instanceof TL_photoSizeEmpty) && photoSize2.type.equals(photoSize.type)) {
                                    photoSize.location = photoSize2.location;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    this.photoThumbs = new ArrayList(photo.sizes);
                }
                if (photo.dc_id != 0) {
                    i = this.photoThumbs.size();
                    for (int i3 = 0; i3 < i; i3++) {
                        FileLocation fileLocation = ((PhotoSize) this.photoThumbs.get(i3)).location;
                        fileLocation.dc_id = photo.dc_id;
                        fileLocation.file_reference = photo.file_reference;
                    }
                }
                this.photoThumbsObject = this.messageOwner.action.photo;
            }
        } else if (this.emojiAnimatedSticker == null) {
            MessageMedia messageMedia = message.media;
            if (messageMedia != null && !(messageMedia instanceof TL_messageMediaEmpty)) {
                Document document;
                if (messageMedia instanceof TL_messageMediaPhoto) {
                    photo = messageMedia.photo;
                    if (z) {
                        arrayList = this.photoThumbs;
                        if (arrayList == null || arrayList.size() == photo.sizes.size()) {
                            arrayList = this.photoThumbs;
                            if (!(arrayList == null || arrayList.isEmpty())) {
                                for (i = 0; i < this.photoThumbs.size(); i++) {
                                    photoSize = (PhotoSize) this.photoThumbs.get(i);
                                    if (photoSize != null) {
                                        for (i2 = 0; i2 < photo.sizes.size(); i2++) {
                                            photoSize2 = (PhotoSize) photo.sizes.get(i2);
                                            if (photoSize2 != null && !(photoSize2 instanceof TL_photoSizeEmpty) && photoSize2.type.equals(photoSize.type)) {
                                                photoSize.location = photoSize2.location;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            this.photoThumbsObject = this.messageOwner.media.photo;
                        }
                    }
                    this.photoThumbs = new ArrayList(photo.sizes);
                    this.photoThumbsObject = this.messageOwner.media.photo;
                } else if (messageMedia instanceof TL_messageMediaDocument) {
                    document = getDocument();
                    if (isDocumentHasThumb(document)) {
                        if (z) {
                            arrayList = this.photoThumbs;
                            if (arrayList != null) {
                                if (!(arrayList == null || arrayList.isEmpty())) {
                                    updatePhotoSizeLocations(this.photoThumbs, document.thumbs);
                                }
                                this.photoThumbsObject = document;
                            }
                        }
                        this.photoThumbs = new ArrayList();
                        this.photoThumbs.addAll(document.thumbs);
                        this.photoThumbsObject = document;
                    }
                } else if (messageMedia instanceof TL_messageMediaGame) {
                    document = messageMedia.game.document;
                    if (document != null && isDocumentHasThumb(document)) {
                        if (z) {
                            ArrayList arrayList2 = this.photoThumbs;
                            if (!(arrayList2 == null || arrayList2.isEmpty())) {
                                updatePhotoSizeLocations(this.photoThumbs, document.thumbs);
                            }
                        } else {
                            this.photoThumbs = new ArrayList();
                            this.photoThumbs.addAll(document.thumbs);
                        }
                        this.photoThumbsObject = document;
                    }
                    photo = this.messageOwner.media.game.photo;
                    if (photo != null) {
                        if (z) {
                            arrayList = this.photoThumbs2;
                            if (arrayList != null) {
                                if (!arrayList.isEmpty()) {
                                    updatePhotoSizeLocations(this.photoThumbs2, photo.sizes);
                                }
                                this.photoThumbsObject2 = photo;
                            }
                        }
                        this.photoThumbs2 = new ArrayList(photo.sizes);
                        this.photoThumbsObject2 = photo;
                    }
                    if (this.photoThumbs == null) {
                        arrayList = this.photoThumbs2;
                        if (arrayList != null) {
                            this.photoThumbs = arrayList;
                            this.photoThumbs2 = null;
                            this.photoThumbsObject = this.photoThumbsObject2;
                            this.photoThumbsObject2 = null;
                        }
                    }
                } else if (messageMedia instanceof TL_messageMediaWebPage) {
                    WebPage webPage = messageMedia.webpage;
                    Photo photo2 = webPage.photo;
                    document = webPage.document;
                    if (photo2 != null) {
                        if (z) {
                            arrayList = this.photoThumbs;
                            if (arrayList != null) {
                                if (!arrayList.isEmpty()) {
                                    updatePhotoSizeLocations(this.photoThumbs, photo2.sizes);
                                }
                                this.photoThumbsObject = photo2;
                            }
                        }
                        this.photoThumbs = new ArrayList(photo2.sizes);
                        this.photoThumbsObject = photo2;
                    } else if (document != null && isDocumentHasThumb(document)) {
                        if (z) {
                            arrayList = this.photoThumbs;
                            if (!(arrayList == null || arrayList.isEmpty())) {
                                updatePhotoSizeLocations(this.photoThumbs, document.thumbs);
                            }
                        } else {
                            this.photoThumbs = new ArrayList();
                            this.photoThumbs.addAll(document.thumbs);
                        }
                        this.photoThumbsObject = document;
                    }
                }
            }
        } else if (TextUtils.isEmpty(this.emojiAnimatedStickerColor) && isDocumentHasThumb(this.emojiAnimatedSticker)) {
            if (z) {
                arrayList = this.photoThumbs;
                if (arrayList != null) {
                    if (!(arrayList == null || arrayList.isEmpty())) {
                        updatePhotoSizeLocations(this.photoThumbs, this.emojiAnimatedSticker.thumbs);
                    }
                    this.photoThumbsObject = this.emojiAnimatedSticker;
                }
            }
            this.photoThumbs = new ArrayList();
            this.photoThumbs.addAll(this.emojiAnimatedSticker.thumbs);
            this.photoThumbsObject = this.emojiAnimatedSticker;
        }
    }

    private static void updatePhotoSizeLocations(ArrayList<PhotoSize> arrayList, ArrayList<PhotoSize> arrayList2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            PhotoSize photoSize = (PhotoSize) arrayList.get(i);
            if (photoSize != null) {
                int size2 = arrayList2.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    PhotoSize photoSize2 = (PhotoSize) arrayList2.get(i2);
                    if (!(photoSize2 instanceof TL_photoSizeEmpty) && !(photoSize2 instanceof TL_photoCachedSize) && photoSize2 != null && photoSize2.type.equals(photoSize.type)) {
                        photoSize.location = photoSize2.location;
                        break;
                    }
                }
            }
        }
    }

    public CharSequence replaceWithLink(CharSequence charSequence, String str, ArrayList<Integer> arrayList, AbstractMap<Integer, User> abstractMap, SparseArray<User> sparseArray) {
        if (TextUtils.indexOf(charSequence, str) < 0) {
            return charSequence;
        }
        String str2 = "";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str2);
        for (int i = 0; i < arrayList.size(); i++) {
            User user = null;
            if (abstractMap != null) {
                user = (User) abstractMap.get(arrayList.get(i));
            } else if (sparseArray != null) {
                user = (User) sparseArray.get(((Integer) arrayList.get(i)).intValue());
            }
            if (user == null) {
                user = MessagesController.getInstance(this.currentAccount).getUser((Integer) arrayList.get(i));
            }
            if (user != null) {
                String userName = UserObject.getUserName(user);
                int length = spannableStringBuilder.length();
                if (spannableStringBuilder.length() != 0) {
                    spannableStringBuilder.append(", ");
                }
                spannableStringBuilder.append(userName);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(str2);
                stringBuilder.append(user.id);
                spannableStringBuilder.setSpan(new URLSpanNoUnderlineBold(stringBuilder.toString()), length, userName.length() + length, 33);
            }
        }
        return TextUtils.replace(charSequence, new String[]{str}, new CharSequence[]{spannableStringBuilder});
    }

    public CharSequence replaceWithLink(CharSequence charSequence, String str, TLObject tLObject) {
        int indexOf = TextUtils.indexOf(charSequence, str);
        if (indexOf < 0) {
            return charSequence;
        }
        String userName;
        String stringBuilder;
        String str2 = "";
        StringBuilder stringBuilder2;
        if (tLObject instanceof User) {
            User user = (User) tLObject;
            userName = UserObject.getUserName(user);
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str2);
            stringBuilder2.append(user.id);
            stringBuilder = stringBuilder2.toString();
        } else if (tLObject instanceof Chat) {
            Chat chat = (Chat) tLObject;
            userName = chat.title;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append(str2);
            stringBuilder2.append(-chat.id);
            stringBuilder = stringBuilder2.toString();
        } else if (tLObject instanceof TL_game) {
            userName = ((TL_game) tLObject).title;
            stringBuilder = "game";
        } else {
            stringBuilder = "0";
            userName = str2;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(TextUtils.replace(charSequence, new String[]{str}, new String[]{userName.replace(10, ' ')}));
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(str2);
        stringBuilder3.append(stringBuilder);
        spannableStringBuilder.setSpan(new URLSpanNoUnderlineBold(stringBuilder3.toString()), indexOf, userName.length() + indexOf, 33);
        return spannableStringBuilder;
    }

    public String getExtension() {
        String fileName = getFileName();
        int lastIndexOf = fileName.lastIndexOf(46);
        fileName = lastIndexOf != -1 ? fileName.substring(lastIndexOf + 1) : null;
        if (fileName == null || fileName.length() == 0) {
            fileName = getDocument().mime_type;
        }
        if (fileName == null) {
            fileName = "";
        }
        return fileName.toUpperCase();
    }

    public String getFileName() {
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(getDocument());
        }
        if (messageMedia instanceof TL_messageMediaPhoto) {
            ArrayList arrayList = messageMedia.photo.sizes;
            if (arrayList.size() > 0) {
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize != null) {
                    return FileLoader.getAttachFileName(closestPhotoSizeWithSize);
                }
            }
        } else if (messageMedia instanceof TL_messageMediaWebPage) {
            return FileLoader.getAttachFileName(messageMedia.webpage.document);
        }
        return "";
    }

    public int getMediaType() {
        if (isVideo()) {
            return 2;
        }
        if (isVoice()) {
            return 1;
        }
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TL_messageMediaDocument) {
            return 3;
        }
        return messageMedia instanceof TL_messageMediaPhoto ? 0 : 4;
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

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0094  */
    public void generateLinkDescription() {
        /*
        r5 = this;
        r0 = r5.linkDescription;
        if (r0 == 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        r0 = r5.messageOwner;
        r0 = r0.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        r2 = 1;
        r3 = 0;
        if (r1 == 0) goto L_0x004e;
    L_0x000f:
        r0 = r0.webpage;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r1 == 0) goto L_0x004e;
    L_0x0015:
        r0 = r0.description;
        if (r0 == 0) goto L_0x004e;
    L_0x0019:
        r0 = android.text.Spannable.Factory.getInstance();
        r1 = r5.messageOwner;
        r1 = r1.media;
        r1 = r1.webpage;
        r1 = r1.description;
        r0 = r0.newSpannable(r1);
        r5.linkDescription = r0;
        r0 = r5.messageOwner;
        r0 = r0.media;
        r0 = r0.webpage;
        r0 = r0.site_name;
        if (r0 == 0) goto L_0x0039;
    L_0x0035:
        r0 = r0.toLowerCase();
    L_0x0039:
        r1 = "instagram";
        r1 = r1.equals(r0);
        if (r1 == 0) goto L_0x0043;
    L_0x0041:
        r0 = 1;
        goto L_0x008c;
    L_0x0043:
        r1 = "twitter";
        r0 = r1.equals(r0);
        if (r0 == 0) goto L_0x008b;
    L_0x004c:
        r0 = 2;
        goto L_0x008c;
    L_0x004e:
        r0 = r5.messageOwner;
        r0 = r0.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 == 0) goto L_0x006f;
    L_0x0056:
        r0 = r0.game;
        r0 = r0.description;
        if (r0 == 0) goto L_0x006f;
    L_0x005c:
        r0 = android.text.Spannable.Factory.getInstance();
        r1 = r5.messageOwner;
        r1 = r1.media;
        r1 = r1.game;
        r1 = r1.description;
        r0 = r0.newSpannable(r1);
        r5.linkDescription = r0;
        goto L_0x008b;
    L_0x006f:
        r0 = r5.messageOwner;
        r0 = r0.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
        if (r1 == 0) goto L_0x008b;
    L_0x0077:
        r0 = r0.description;
        if (r0 == 0) goto L_0x008b;
    L_0x007b:
        r0 = android.text.Spannable.Factory.getInstance();
        r1 = r5.messageOwner;
        r1 = r1.media;
        r1 = r1.description;
        r0 = r0.newSpannable(r1);
        r5.linkDescription = r0;
    L_0x008b:
        r0 = 0;
    L_0x008c:
        r1 = r5.linkDescription;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x00d4;
    L_0x0094:
        r1 = r5.linkDescription;
        r1 = containsUrls(r1);
        if (r1 == 0) goto L_0x00a8;
    L_0x009c:
        r1 = r5.linkDescription;	 Catch:{ Exception -> 0x00a4 }
        r1 = (android.text.Spannable) r1;	 Catch:{ Exception -> 0x00a4 }
        org.telegram.messenger.AndroidUtilities.addLinks(r1, r2);	 Catch:{ Exception -> 0x00a4 }
        goto L_0x00a8;
    L_0x00a4:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
    L_0x00a8:
        r1 = r5.linkDescription;
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;
        r2 = r2.getFontMetricsInt();
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r1 = org.telegram.messenger.Emoji.replaceEmoji(r1, r2, r4, r3);
        r5.linkDescription = r1;
        if (r0 == 0) goto L_0x00d4;
    L_0x00be:
        r1 = r5.linkDescription;
        r2 = r1 instanceof android.text.Spannable;
        if (r2 != 0) goto L_0x00cb;
    L_0x00c4:
        r2 = new android.text.SpannableStringBuilder;
        r2.<init>(r1);
        r5.linkDescription = r2;
    L_0x00cb:
        r1 = r5.isOutOwner();
        r2 = r5.linkDescription;
        addUrlsByPattern(r1, r2, r3, r0, r3);
    L_0x00d4:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLinkDescription():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00da  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00cb  */
    /* JADX WARNING: Missing block: B:39:0x0095, code skipped:
            if (r7.messageOwner.send_state == 0) goto L_0x0097;
     */
    /* JADX WARNING: Missing block: B:41:0x009b, code skipped:
            if (r7.messageOwner.id >= 0) goto L_0x009f;
     */
    public void generateCaption() {
        /*
        r7 = this;
        r0 = r7.caption;
        if (r0 != 0) goto L_0x00f4;
    L_0x0004:
        r0 = r7.isRoundVideo();
        if (r0 == 0) goto L_0x000c;
    L_0x000a:
        goto L_0x00f4;
    L_0x000c:
        r0 = r7.isMediaEmpty();
        if (r0 != 0) goto L_0x00f4;
    L_0x0012:
        r0 = r7.messageOwner;
        r1 = r0.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 != 0) goto L_0x00f4;
    L_0x001a:
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x00f4;
    L_0x0022:
        r0 = r7.messageOwner;
        r0 = r0.message;
        r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;
        r1 = r1.getFontMetricsInt();
        r2 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = 0;
        r0 = org.telegram.messenger.Emoji.replaceEmoji(r0, r1, r2, r3);
        r7.caption = r0;
        r0 = r7.messageOwner;
        r1 = r0.send_state;
        r2 = 1;
        if (r1 == 0) goto L_0x005e;
    L_0x0040:
        r0 = 0;
    L_0x0041:
        r1 = r7.messageOwner;
        r1 = r1.entities;
        r1 = r1.size();
        if (r0 >= r1) goto L_0x005c;
    L_0x004b:
        r1 = r7.messageOwner;
        r1 = r1.entities;
        r1 = r1.get(r0);
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
        if (r1 != 0) goto L_0x0059;
    L_0x0057:
        r0 = 1;
        goto L_0x0065;
    L_0x0059:
        r0 = r0 + 1;
        goto L_0x0041;
    L_0x005c:
        r0 = 0;
        goto L_0x0065;
    L_0x005e:
        r0 = r0.entities;
        r0 = r0.isEmpty();
        r0 = r0 ^ r2;
    L_0x0065:
        if (r0 != 0) goto L_0x009f;
    L_0x0067:
        r0 = r7.eventId;
        r4 = 0;
        r6 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1));
        if (r6 != 0) goto L_0x009d;
    L_0x006f:
        r0 = r7.messageOwner;
        r0 = r0.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_old;
        if (r1 != 0) goto L_0x009d;
    L_0x0077:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_layer68;
        if (r1 != 0) goto L_0x009d;
    L_0x007b:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_layer74;
        if (r1 != 0) goto L_0x009d;
    L_0x007f:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument_old;
        if (r1 != 0) goto L_0x009d;
    L_0x0083:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument_layer68;
        if (r1 != 0) goto L_0x009d;
    L_0x0087:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument_layer74;
        if (r0 != 0) goto L_0x009d;
    L_0x008b:
        r0 = r7.isOut();
        if (r0 == 0) goto L_0x0097;
    L_0x0091:
        r0 = r7.messageOwner;
        r0 = r0.send_state;
        if (r0 != 0) goto L_0x009d;
    L_0x0097:
        r0 = r7.messageOwner;
        r0 = r0.id;
        if (r0 >= 0) goto L_0x009f;
    L_0x009d:
        r0 = 1;
        goto L_0x00a0;
    L_0x009f:
        r0 = 0;
    L_0x00a0:
        if (r0 == 0) goto L_0x00c0;
    L_0x00a2:
        r1 = r7.caption;
        r1 = containsUrls(r1);
        if (r1 == 0) goto L_0x00b7;
    L_0x00aa:
        r1 = r7.caption;	 Catch:{ Exception -> 0x00b3 }
        r1 = (android.text.Spannable) r1;	 Catch:{ Exception -> 0x00b3 }
        r4 = 5;
        org.telegram.messenger.AndroidUtilities.addLinks(r1, r4);	 Catch:{ Exception -> 0x00b3 }
        goto L_0x00b7;
    L_0x00b3:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
    L_0x00b7:
        r1 = r7.isOutOwner();
        r4 = r7.caption;
        addUrlsByPattern(r1, r4, r2, r3, r3);
    L_0x00c0:
        r1 = r7.caption;
        r7.addEntitiesToText(r1, r0);
        r0 = r7.isVideo();
        if (r0 == 0) goto L_0x00da;
    L_0x00cb:
        r0 = r7.isOutOwner();
        r1 = r7.caption;
        r3 = 3;
        r4 = r7.getDuration();
        addUrlsByPattern(r0, r1, r2, r3, r4);
        goto L_0x00f4;
    L_0x00da:
        r0 = r7.isMusic();
        if (r0 != 0) goto L_0x00e6;
    L_0x00e0:
        r0 = r7.isVoice();
        if (r0 == 0) goto L_0x00f4;
    L_0x00e6:
        r0 = r7.isOutOwner();
        r1 = r7.caption;
        r3 = 4;
        r4 = r7.getDuration();
        addUrlsByPattern(r0, r1, r2, r3, r4);
    L_0x00f4:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateCaption():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:79:0x01da A:{Catch:{ Exception -> 0x01df }} */
    private static void addUrlsByPattern(boolean r16, java.lang.CharSequence r17, boolean r18, int r19, int r20) {
        /*
        r0 = r17;
        r1 = r19;
        r2 = 4;
        r3 = 3;
        r4 = 1;
        if (r1 == r3) goto L_0x0034;
    L_0x0009:
        if (r1 != r2) goto L_0x000c;
    L_0x000b:
        goto L_0x0034;
    L_0x000c:
        if (r1 != r4) goto L_0x0021;
    L_0x000e:
        r5 = instagramUrlPattern;	 Catch:{ Exception -> 0x01df }
        if (r5 != 0) goto L_0x001a;
    L_0x0012:
        r5 = "(^|\\s|\\()@[a-zA-Z\\d_.]{1,32}|(^|\\s|\\()#[\\w.]+";
        r5 = java.util.regex.Pattern.compile(r5);	 Catch:{ Exception -> 0x01df }
        instagramUrlPattern = r5;	 Catch:{ Exception -> 0x01df }
    L_0x001a:
        r5 = instagramUrlPattern;	 Catch:{ Exception -> 0x01df }
        r5 = r5.matcher(r0);	 Catch:{ Exception -> 0x01df }
        goto L_0x0046;
    L_0x0021:
        r5 = urlPattern;	 Catch:{ Exception -> 0x01df }
        if (r5 != 0) goto L_0x002d;
    L_0x0025:
        r5 = "(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s|\\()@[a-zA-Z\\d_]{1,32}|(^|\\s|\\()#[\\w.]+|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)";
        r5 = java.util.regex.Pattern.compile(r5);	 Catch:{ Exception -> 0x01df }
        urlPattern = r5;	 Catch:{ Exception -> 0x01df }
    L_0x002d:
        r5 = urlPattern;	 Catch:{ Exception -> 0x01df }
        r5 = r5.matcher(r0);	 Catch:{ Exception -> 0x01df }
        goto L_0x0046;
    L_0x0034:
        r5 = videoTimeUrlPattern;	 Catch:{ Exception -> 0x01df }
        if (r5 != 0) goto L_0x0040;
    L_0x0038:
        r5 = "\\b(?:(\\d{1,2}):)?(\\d{1,3}):([0-5][0-9])\\b";
        r5 = java.util.regex.Pattern.compile(r5);	 Catch:{ Exception -> 0x01df }
        videoTimeUrlPattern = r5;	 Catch:{ Exception -> 0x01df }
    L_0x0040:
        r5 = videoTimeUrlPattern;	 Catch:{ Exception -> 0x01df }
        r5 = r5.matcher(r0);	 Catch:{ Exception -> 0x01df }
    L_0x0046:
        r6 = r0;
        r6 = (android.text.Spannable) r6;	 Catch:{ Exception -> 0x01df }
    L_0x0049:
        r7 = r5.find();	 Catch:{ Exception -> 0x01df }
        if (r7 == 0) goto L_0x01e3;
    L_0x004f:
        r7 = r5.start();	 Catch:{ Exception -> 0x01df }
        r8 = r5.end();	 Catch:{ Exception -> 0x01df }
        r9 = 0;
        r10 = 0;
        r11 = 2;
        if (r1 == r3) goto L_0x0144;
    L_0x005c:
        if (r1 != r2) goto L_0x0060;
    L_0x005e:
        goto L_0x0144;
    L_0x0060:
        r12 = r0.charAt(r7);	 Catch:{ Exception -> 0x01df }
        r13 = 47;
        r14 = 35;
        r15 = 64;
        if (r1 == 0) goto L_0x007b;
    L_0x006c:
        if (r12 == r15) goto L_0x0072;
    L_0x006e:
        if (r12 == r14) goto L_0x0072;
    L_0x0070:
        r7 = r7 + 1;
    L_0x0072:
        r12 = r0.charAt(r7);	 Catch:{ Exception -> 0x01df }
        if (r12 == r15) goto L_0x0087;
    L_0x0078:
        if (r12 == r14) goto L_0x0087;
    L_0x007a:
        goto L_0x0049;
    L_0x007b:
        if (r12 == r15) goto L_0x0087;
    L_0x007d:
        if (r12 == r14) goto L_0x0087;
    L_0x007f:
        if (r12 == r13) goto L_0x0087;
    L_0x0081:
        r2 = 36;
        if (r12 == r2) goto L_0x0087;
    L_0x0085:
        r7 = r7 + 1;
    L_0x0087:
        if (r1 != r4) goto L_0x00d0;
    L_0x0089:
        if (r12 != r15) goto L_0x00ad;
    L_0x008b:
        r9 = new org.telegram.ui.Components.URLSpanNoUnderline;	 Catch:{ Exception -> 0x01df }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01df }
        r2.<init>();	 Catch:{ Exception -> 0x01df }
        r11 = "https://instagram.com/";
        r2.append(r11);	 Catch:{ Exception -> 0x01df }
        r11 = r7 + 1;
        r11 = r0.subSequence(r11, r8);	 Catch:{ Exception -> 0x01df }
        r11 = r11.toString();	 Catch:{ Exception -> 0x01df }
        r2.append(r11);	 Catch:{ Exception -> 0x01df }
        r2 = r2.toString();	 Catch:{ Exception -> 0x01df }
        r9.<init>(r2);	 Catch:{ Exception -> 0x01df }
        goto L_0x0132;
    L_0x00ad:
        if (r12 != r14) goto L_0x0132;
    L_0x00af:
        r9 = new org.telegram.ui.Components.URLSpanNoUnderline;	 Catch:{ Exception -> 0x01df }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01df }
        r2.<init>();	 Catch:{ Exception -> 0x01df }
        r11 = "https://www.instagram.com/explore/tags/";
        r2.append(r11);	 Catch:{ Exception -> 0x01df }
        r11 = r7 + 1;
        r11 = r0.subSequence(r11, r8);	 Catch:{ Exception -> 0x01df }
        r11 = r11.toString();	 Catch:{ Exception -> 0x01df }
        r2.append(r11);	 Catch:{ Exception -> 0x01df }
        r2 = r2.toString();	 Catch:{ Exception -> 0x01df }
        r9.<init>(r2);	 Catch:{ Exception -> 0x01df }
        goto L_0x0132;
    L_0x00d0:
        if (r1 != r11) goto L_0x0118;
    L_0x00d2:
        if (r12 != r15) goto L_0x00f5;
    L_0x00d4:
        r9 = new org.telegram.ui.Components.URLSpanNoUnderline;	 Catch:{ Exception -> 0x01df }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01df }
        r2.<init>();	 Catch:{ Exception -> 0x01df }
        r11 = "https://twitter.com/";
        r2.append(r11);	 Catch:{ Exception -> 0x01df }
        r11 = r7 + 1;
        r11 = r0.subSequence(r11, r8);	 Catch:{ Exception -> 0x01df }
        r11 = r11.toString();	 Catch:{ Exception -> 0x01df }
        r2.append(r11);	 Catch:{ Exception -> 0x01df }
        r2 = r2.toString();	 Catch:{ Exception -> 0x01df }
        r9.<init>(r2);	 Catch:{ Exception -> 0x01df }
        goto L_0x0132;
    L_0x00f5:
        if (r12 != r14) goto L_0x0132;
    L_0x00f7:
        r9 = new org.telegram.ui.Components.URLSpanNoUnderline;	 Catch:{ Exception -> 0x01df }
        r2 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01df }
        r2.<init>();	 Catch:{ Exception -> 0x01df }
        r11 = "https://twitter.com/hashtag/";
        r2.append(r11);	 Catch:{ Exception -> 0x01df }
        r11 = r7 + 1;
        r11 = r0.subSequence(r11, r8);	 Catch:{ Exception -> 0x01df }
        r11 = r11.toString();	 Catch:{ Exception -> 0x01df }
        r2.append(r11);	 Catch:{ Exception -> 0x01df }
        r2 = r2.toString();	 Catch:{ Exception -> 0x01df }
        r9.<init>(r2);	 Catch:{ Exception -> 0x01df }
        goto L_0x0132;
    L_0x0118:
        r2 = r0.charAt(r7);	 Catch:{ Exception -> 0x01df }
        if (r2 != r13) goto L_0x0136;
    L_0x011e:
        if (r18 == 0) goto L_0x0132;
    L_0x0120:
        r9 = new org.telegram.ui.Components.URLSpanBotCommand;	 Catch:{ Exception -> 0x01df }
        r2 = r0.subSequence(r7, r8);	 Catch:{ Exception -> 0x01df }
        r2 = r2.toString();	 Catch:{ Exception -> 0x01df }
        if (r16 == 0) goto L_0x012e;
    L_0x012c:
        r11 = 1;
        goto L_0x012f;
    L_0x012e:
        r11 = 0;
    L_0x012f:
        r9.<init>(r2, r11);	 Catch:{ Exception -> 0x01df }
    L_0x0132:
        r2 = r20;
        goto L_0x01d8;
    L_0x0136:
        r9 = new org.telegram.ui.Components.URLSpanNoUnderline;	 Catch:{ Exception -> 0x01df }
        r2 = r0.subSequence(r7, r8);	 Catch:{ Exception -> 0x01df }
        r2 = r2.toString();	 Catch:{ Exception -> 0x01df }
        r9.<init>(r2);	 Catch:{ Exception -> 0x01df }
        goto L_0x0132;
    L_0x0144:
        r2 = android.text.style.URLSpan.class;
        r2 = r6.getSpans(r7, r8, r2);	 Catch:{ Exception -> 0x01df }
        r2 = (android.text.style.URLSpan[]) r2;	 Catch:{ Exception -> 0x01df }
        if (r2 == 0) goto L_0x0154;
    L_0x014e:
        r2 = r2.length;	 Catch:{ Exception -> 0x01df }
        if (r2 <= 0) goto L_0x0154;
    L_0x0151:
        r2 = 4;
        goto L_0x0049;
    L_0x0154:
        r5.groupCount();	 Catch:{ Exception -> 0x01df }
        r2 = r5.start(r4);	 Catch:{ Exception -> 0x01df }
        r9 = r5.end(r4);	 Catch:{ Exception -> 0x01df }
        r12 = r5.start(r11);	 Catch:{ Exception -> 0x01df }
        r11 = r5.end(r11);	 Catch:{ Exception -> 0x01df }
        r13 = r5.start(r3);	 Catch:{ Exception -> 0x01df }
        r14 = r5.end(r3);	 Catch:{ Exception -> 0x01df }
        r11 = r0.subSequence(r12, r11);	 Catch:{ Exception -> 0x01df }
        r11 = org.telegram.messenger.Utilities.parseInt(r11);	 Catch:{ Exception -> 0x01df }
        r11 = r11.intValue();	 Catch:{ Exception -> 0x01df }
        r12 = r0.subSequence(r13, r14);	 Catch:{ Exception -> 0x01df }
        r12 = org.telegram.messenger.Utilities.parseInt(r12);	 Catch:{ Exception -> 0x01df }
        r12 = r12.intValue();	 Catch:{ Exception -> 0x01df }
        if (r2 < 0) goto L_0x0198;
    L_0x0189:
        if (r9 < 0) goto L_0x0198;
    L_0x018b:
        r2 = r0.subSequence(r2, r9);	 Catch:{ Exception -> 0x01df }
        r2 = org.telegram.messenger.Utilities.parseInt(r2);	 Catch:{ Exception -> 0x01df }
        r2 = r2.intValue();	 Catch:{ Exception -> 0x01df }
        goto L_0x0199;
    L_0x0198:
        r2 = -1;
    L_0x0199:
        r11 = r11 * 60;
        r12 = r12 + r11;
        if (r2 <= 0) goto L_0x01a3;
    L_0x019e:
        r2 = r2 * 60;
        r2 = r2 * 60;
        r12 = r12 + r2;
    L_0x01a3:
        r2 = r20;
        if (r12 <= r2) goto L_0x01a8;
    L_0x01a7:
        goto L_0x0151;
    L_0x01a8:
        if (r1 != r3) goto L_0x01c2;
    L_0x01aa:
        r9 = new org.telegram.ui.Components.URLSpanNoUnderline;	 Catch:{ Exception -> 0x01df }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01df }
        r11.<init>();	 Catch:{ Exception -> 0x01df }
        r13 = "video?";
        r11.append(r13);	 Catch:{ Exception -> 0x01df }
        r11.append(r12);	 Catch:{ Exception -> 0x01df }
        r11 = r11.toString();	 Catch:{ Exception -> 0x01df }
        r9.<init>(r11);	 Catch:{ Exception -> 0x01df }
        goto L_0x01d8;
    L_0x01c2:
        r9 = new org.telegram.ui.Components.URLSpanNoUnderline;	 Catch:{ Exception -> 0x01df }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x01df }
        r11.<init>();	 Catch:{ Exception -> 0x01df }
        r13 = "audio?";
        r11.append(r13);	 Catch:{ Exception -> 0x01df }
        r11.append(r12);	 Catch:{ Exception -> 0x01df }
        r11 = r11.toString();	 Catch:{ Exception -> 0x01df }
        r9.<init>(r11);	 Catch:{ Exception -> 0x01df }
    L_0x01d8:
        if (r9 == 0) goto L_0x0151;
    L_0x01da:
        r6.setSpan(r9, r7, r8, r10);	 Catch:{ Exception -> 0x01df }
        goto L_0x0151;
    L_0x01df:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x01e3:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addUrlsByPattern(boolean, java.lang.CharSequence, boolean, int, int):void");
    }

    public static int[] getWebDocumentWidthAndHeight(WebDocument webDocument) {
        if (webDocument == null) {
            return null;
        }
        int size = webDocument.attributes.size();
        int i = 0;
        while (i < size) {
            DocumentAttribute documentAttribute = (DocumentAttribute) webDocument.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeImageSize) {
                return new int[]{documentAttribute.w, documentAttribute.h};
            } else if (documentAttribute instanceof TL_documentAttributeVideo) {
                return new int[]{documentAttribute.w, documentAttribute.h};
            } else {
                i++;
            }
        }
        return null;
    }

    public static int getWebDocumentDuration(WebDocument webDocument) {
        if (webDocument == null) {
            return 0;
        }
        int size = webDocument.attributes.size();
        for (int i = 0; i < size; i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) webDocument.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeVideo) {
                return documentAttribute.duration;
            }
            if (documentAttribute instanceof TL_documentAttributeAudio) {
                return documentAttribute.duration;
            }
        }
        return 0;
    }

    public static int[] getInlineResultWidthAndHeight(BotInlineResult botInlineResult) {
        int[] webDocumentWidthAndHeight = getWebDocumentWidthAndHeight(botInlineResult.content);
        if (webDocumentWidthAndHeight != null) {
            return webDocumentWidthAndHeight;
        }
        webDocumentWidthAndHeight = getWebDocumentWidthAndHeight(botInlineResult.thumb);
        return webDocumentWidthAndHeight == null ? new int[]{0, 0} : webDocumentWidthAndHeight;
    }

    public static int getInlineResultDuration(BotInlineResult botInlineResult) {
        int webDocumentDuration = getWebDocumentDuration(botInlineResult.content);
        return webDocumentDuration == 0 ? getWebDocumentDuration(botInlineResult.thumb) : webDocumentDuration;
    }

    public boolean hasValidGroupId() {
        if (getGroupId() != 0) {
            ArrayList arrayList = this.photoThumbs;
            if (!(arrayList == null || arrayList.isEmpty())) {
                return true;
            }
        }
        return false;
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
        addLinks(z, charSequence, true);
    }

    public static void addLinks(boolean z, CharSequence charSequence, boolean z2) {
        if ((charSequence instanceof Spannable) && containsUrls(charSequence)) {
            if (charSequence.length() < 1000) {
                try {
                    AndroidUtilities.addLinks((Spannable) charSequence, 5);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else {
                try {
                    AndroidUtilities.addLinks((Spannable) charSequence, 1);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            addUrlsByPattern(z, charSequence, z2, 0, 0);
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
            TL_messageEntityItalic tL_messageEntityItalic = new TL_messageEntityItalic();
            tL_messageEntityItalic.offset = 0;
            tL_messageEntityItalic.length = charSequence.length();
            arrayList.add(tL_messageEntityItalic);
            return addEntitiesToText(charSequence, arrayList, isOutOwner(), this.type, true, z, z2);
        }
        return addEntitiesToText(charSequence, this.messageOwner.entities, isOutOwner(), this.type, true, z, z2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:112:0x0169  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x0209 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x0204  */
    public static boolean addEntitiesToText(java.lang.CharSequence r16, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r17, boolean r18, int r19, boolean r20, boolean r21, boolean r22) {
        /*
        r0 = r16;
        r1 = r0 instanceof android.text.Spannable;
        r2 = 0;
        if (r1 != 0) goto L_0x0008;
    L_0x0007:
        return r2;
    L_0x0008:
        r1 = r0;
        r1 = (android.text.Spannable) r1;
        r3 = r16.length();
        r4 = android.text.style.URLSpan.class;
        r3 = r1.getSpans(r2, r3, r4);
        r3 = (android.text.style.URLSpan[]) r3;
        if (r3 == 0) goto L_0x001e;
    L_0x0019:
        r5 = r3.length;
        if (r5 <= 0) goto L_0x001e;
    L_0x001c:
        r5 = 1;
        goto L_0x001f;
    L_0x001e:
        r5 = 0;
    L_0x001f:
        r6 = r17.isEmpty();
        if (r6 == 0) goto L_0x0026;
    L_0x0025:
        return r5;
    L_0x0026:
        if (r21 == 0) goto L_0x002a;
    L_0x0028:
        r7 = 2;
        goto L_0x002f;
    L_0x002a:
        if (r18 == 0) goto L_0x002e;
    L_0x002c:
        r7 = 1;
        goto L_0x002f;
    L_0x002e:
        r7 = 0;
    L_0x002f:
        r8 = new java.util.ArrayList;
        r8.<init>();
        r9 = new java.util.ArrayList;
        r10 = r17;
        r9.<init>(r10);
        r10 = org.telegram.messenger.-$$Lambda$MessageObject$TayxkIvYR-DxCFsN9JUXpTKGe7s.INSTANCE;
        java.util.Collections.sort(r9, r10);
        r10 = r9.size();
        r11 = 0;
    L_0x0045:
        r13 = 0;
        if (r11 >= r10) goto L_0x020e;
    L_0x0048:
        r14 = r9.get(r11);
        r14 = (org.telegram.tgnet.TLRPC.MessageEntity) r14;
        r15 = r14.length;
        if (r15 <= 0) goto L_0x0208;
    L_0x0052:
        r15 = r14.offset;
        if (r15 < 0) goto L_0x0208;
    L_0x0056:
        r2 = r16.length();
        if (r15 < r2) goto L_0x005e;
    L_0x005c:
        goto L_0x0208;
    L_0x005e:
        r2 = r14.offset;
        r15 = r14.length;
        r2 = r2 + r15;
        r15 = r16.length();
        if (r2 <= r15) goto L_0x0072;
    L_0x0069:
        r2 = r16.length();
        r15 = r14.offset;
        r2 = r2 - r15;
        r14.length = r2;
    L_0x0072:
        if (r22 == 0) goto L_0x009c;
    L_0x0074:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBold;
        if (r2 != 0) goto L_0x009c;
    L_0x0078:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
        if (r2 != 0) goto L_0x009c;
    L_0x007c:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityStrike;
        if (r2 != 0) goto L_0x009c;
    L_0x0080:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUnderline;
        if (r2 != 0) goto L_0x009c;
    L_0x0084:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBlockquote;
        if (r2 != 0) goto L_0x009c;
    L_0x0088:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityCode;
        if (r2 != 0) goto L_0x009c;
    L_0x008c:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityPre;
        if (r2 != 0) goto L_0x009c;
    L_0x0090:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r2 != 0) goto L_0x009c;
    L_0x0094:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
        if (r2 != 0) goto L_0x009c;
    L_0x0098:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
        if (r2 == 0) goto L_0x00d2;
    L_0x009c:
        if (r3 == 0) goto L_0x00d2;
    L_0x009e:
        r2 = r3.length;
        if (r2 <= 0) goto L_0x00d2;
    L_0x00a1:
        r2 = 0;
    L_0x00a2:
        r15 = r3.length;
        if (r2 >= r15) goto L_0x00d2;
    L_0x00a5:
        r15 = r3[r2];
        if (r15 != 0) goto L_0x00aa;
    L_0x00a9:
        goto L_0x00cf;
    L_0x00aa:
        r15 = r3[r2];
        r15 = r1.getSpanStart(r15);
        r12 = r3[r2];
        r12 = r1.getSpanEnd(r12);
        r6 = r14.offset;
        if (r6 > r15) goto L_0x00bf;
    L_0x00ba:
        r4 = r14.length;
        r6 = r6 + r4;
        if (r6 >= r15) goto L_0x00c8;
    L_0x00bf:
        r4 = r14.offset;
        if (r4 > r12) goto L_0x00cf;
    L_0x00c3:
        r6 = r14.length;
        r4 = r4 + r6;
        if (r4 < r12) goto L_0x00cf;
    L_0x00c8:
        r4 = r3[r2];
        r1.removeSpan(r4);
        r3[r2] = r13;
    L_0x00cf:
        r2 = r2 + 1;
        goto L_0x00a2;
    L_0x00d2:
        r2 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;
        r2.<init>();
        r4 = r14.offset;
        r2.start = r4;
        r4 = r2.start;
        r6 = r14.length;
        r4 = r4 + r6;
        r2.end = r4;
        r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityStrike;
        if (r4 == 0) goto L_0x00ed;
    L_0x00e6:
        r4 = 8;
        r2.flags = r4;
    L_0x00ea:
        r4 = 2;
        goto L_0x0161;
    L_0x00ed:
        r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUnderline;
        if (r4 == 0) goto L_0x00f6;
    L_0x00f1:
        r4 = 16;
        r2.flags = r4;
        goto L_0x00ea;
    L_0x00f6:
        r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBlockquote;
        if (r4 == 0) goto L_0x00ff;
    L_0x00fa:
        r4 = 32;
        r2.flags = r4;
        goto L_0x00ea;
    L_0x00ff:
        r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBold;
        if (r4 == 0) goto L_0x0107;
    L_0x0103:
        r4 = 1;
        r2.flags = r4;
        goto L_0x00ea;
    L_0x0107:
        r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
        if (r4 == 0) goto L_0x010f;
    L_0x010b:
        r4 = 2;
        r2.flags = r4;
        goto L_0x0161;
    L_0x010f:
        r4 = 2;
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityCode;
        if (r6 != 0) goto L_0x015e;
    L_0x0114:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityPre;
        if (r6 == 0) goto L_0x0119;
    L_0x0118:
        goto L_0x015e;
    L_0x0119:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        r12 = 64;
        if (r6 == 0) goto L_0x0128;
    L_0x011f:
        if (r20 != 0) goto L_0x0123;
    L_0x0121:
        goto L_0x0208;
    L_0x0123:
        r2.flags = r12;
        r2.urlEntity = r14;
        goto L_0x0161;
    L_0x0128:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
        if (r6 == 0) goto L_0x0135;
    L_0x012c:
        if (r20 != 0) goto L_0x0130;
    L_0x012e:
        goto L_0x0208;
    L_0x0130:
        r2.flags = r12;
        r2.urlEntity = r14;
        goto L_0x0161;
    L_0x0135:
        if (r22 == 0) goto L_0x013d;
    L_0x0137:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
        if (r6 != 0) goto L_0x013d;
    L_0x013b:
        goto L_0x0208;
    L_0x013d:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
        if (r6 != 0) goto L_0x0145;
    L_0x0141:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
        if (r6 == 0) goto L_0x014f;
    L_0x0145:
        r6 = r14.url;
        r6 = org.telegram.messenger.browser.Browser.isPassportUrl(r6);
        if (r6 == 0) goto L_0x014f;
    L_0x014d:
        goto L_0x0208;
    L_0x014f:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMention;
        if (r6 == 0) goto L_0x0157;
    L_0x0153:
        if (r20 != 0) goto L_0x0157;
    L_0x0155:
        goto L_0x0208;
    L_0x0157:
        r6 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r2.flags = r6;
        r2.urlEntity = r14;
        goto L_0x0161;
    L_0x015e:
        r6 = 4;
        r2.flags = r6;
    L_0x0161:
        r6 = r8.size();
        r12 = r6;
        r6 = 0;
    L_0x0167:
        if (r6 >= r12) goto L_0x01fd;
    L_0x0169:
        r13 = r8.get(r6);
        r13 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r13;
        r14 = r2.start;
        r15 = r13.start;
        if (r14 <= r15) goto L_0x01bb;
    L_0x0175:
        r15 = r13.end;
        if (r14 < r15) goto L_0x017a;
    L_0x0179:
        goto L_0x01bf;
    L_0x017a:
        r14 = r2.end;
        if (r14 >= r15) goto L_0x019d;
    L_0x017e:
        r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;
        r14.<init>(r2);
        r14.merge(r13);
        r6 = r6 + 1;
        r12 = r12 + 1;
        r8.add(r6, r14);
        r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;
        r14.<init>(r13);
        r15 = r2.end;
        r14.start = r15;
        r15 = 1;
        r6 = r6 + r15;
        r12 = r12 + r15;
        r8.add(r6, r14);
        goto L_0x01b2;
    L_0x019d:
        if (r14 < r15) goto L_0x01b2;
    L_0x019f:
        r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;
        r14.<init>(r2);
        r14.merge(r13);
        r15 = r13.end;
        r14.end = r15;
        r6 = r6 + 1;
        r12 = r12 + 1;
        r8.add(r6, r14);
    L_0x01b2:
        r14 = r2.start;
        r15 = r13.end;
        r2.start = r15;
        r13.end = r14;
        goto L_0x01bf;
    L_0x01bb:
        r14 = r2.end;
        if (r15 < r14) goto L_0x01c1;
    L_0x01bf:
        r4 = 1;
        goto L_0x01f9;
    L_0x01c1:
        r4 = r13.end;
        if (r14 != r4) goto L_0x01c9;
    L_0x01c5:
        r13.merge(r2);
        goto L_0x01f6;
    L_0x01c9:
        if (r14 >= r4) goto L_0x01e3;
    L_0x01cb:
        r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;
        r4.<init>(r13);
        r4.merge(r2);
        r14 = r2.end;
        r4.end = r14;
        r6 = r6 + 1;
        r12 = r12 + 1;
        r8.add(r6, r4);
        r4 = r2.end;
        r13.start = r4;
        goto L_0x01f6;
    L_0x01e3:
        r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;
        r4.<init>(r2);
        r14 = r13.end;
        r4.start = r14;
        r6 = r6 + 1;
        r12 = r12 + 1;
        r8.add(r6, r4);
        r13.merge(r2);
    L_0x01f6:
        r2.end = r15;
        goto L_0x01bf;
    L_0x01f9:
        r6 = r6 + r4;
        r4 = 2;
        goto L_0x0167;
    L_0x01fd:
        r4 = 1;
        r6 = r2.start;
        r12 = r2.end;
        if (r6 >= r12) goto L_0x0209;
    L_0x0204:
        r8.add(r2);
        goto L_0x0209;
    L_0x0208:
        r4 = 1;
    L_0x0209:
        r11 = r11 + 1;
        r2 = 0;
        goto L_0x0045;
    L_0x020e:
        r4 = 1;
        r2 = r8.size();
        r3 = 0;
    L_0x0214:
        if (r3 >= r2) goto L_0x0391;
    L_0x0216:
        r6 = r8.get(r3);
        r6 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r6;
        r9 = r6.urlEntity;
        if (r9 == 0) goto L_0x022a;
    L_0x0220:
        r10 = r9.offset;
        r9 = r9.length;
        r9 = r9 + r10;
        r9 = android.text.TextUtils.substring(r0, r10, r9);
        goto L_0x022b;
    L_0x022a:
        r9 = r13;
    L_0x022b:
        r10 = r6.urlEntity;
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBotCommand;
        r12 = 33;
        if (r11 == 0) goto L_0x0241;
    L_0x0233:
        r10 = new org.telegram.ui.Components.URLSpanBotCommand;
        r10.<init>(r9, r7, r6);
        r9 = r6.start;
        r6 = r6.end;
        r1.setSpan(r10, r9, r6, r12);
        goto L_0x02ea;
    L_0x0241:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityHashtag;
        if (r11 != 0) goto L_0x0380;
    L_0x0245:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMention;
        if (r11 != 0) goto L_0x0380;
    L_0x0249:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityCashtag;
        if (r11 == 0) goto L_0x024f;
    L_0x024d:
        goto L_0x0380;
    L_0x024f:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
        if (r11 == 0) goto L_0x0272;
    L_0x0253:
        r10 = new org.telegram.ui.Components.URLSpanReplacement;
        r11 = new java.lang.StringBuilder;
        r11.<init>();
        r14 = "mailto:";
        r11.append(r14);
        r11.append(r9);
        r9 = r11.toString();
        r10.<init>(r9, r6);
        r9 = r6.start;
        r6 = r6.end;
        r1.setSpan(r10, r9, r6, r12);
        goto L_0x02ea;
    L_0x0272:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
        if (r11 == 0) goto L_0x02ad;
    L_0x0276:
        r5 = r9.toLowerCase();
        r10 = "://";
        r5 = r5.contains(r10);
        if (r5 != 0) goto L_0x02a0;
    L_0x0282:
        r5 = new org.telegram.ui.Components.URLSpanBrowser;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "http://";
        r10.append(r11);
        r10.append(r9);
        r9 = r10.toString();
        r5.<init>(r9, r6);
        r9 = r6.start;
        r6 = r6.end;
        r1.setSpan(r5, r9, r6, r12);
        goto L_0x02e9;
    L_0x02a0:
        r5 = new org.telegram.ui.Components.URLSpanBrowser;
        r5.<init>(r9, r6);
        r9 = r6.start;
        r6 = r6.end;
        r1.setSpan(r5, r9, r6, r12);
        goto L_0x02e9;
    L_0x02ad:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityPhone;
        if (r11 == 0) goto L_0x02ed;
    L_0x02b1:
        r5 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r9);
        r10 = "+";
        r9 = r9.startsWith(r10);
        if (r9 == 0) goto L_0x02cc;
    L_0x02bd:
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r10);
        r9.append(r5);
        r5 = r9.toString();
    L_0x02cc:
        r9 = new org.telegram.ui.Components.URLSpanBrowser;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r11 = "tel:";
        r10.append(r11);
        r10.append(r5);
        r5 = r10.toString();
        r9.<init>(r5, r6);
        r5 = r6.start;
        r6 = r6.end;
        r1.setSpan(r9, r5, r6, r12);
    L_0x02e9:
        r5 = 1;
    L_0x02ea:
        r10 = 4;
        goto L_0x038d;
    L_0x02ed:
        r9 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
        if (r9 == 0) goto L_0x0300;
    L_0x02f1:
        r9 = new org.telegram.ui.Components.URLSpanReplacement;
        r10 = r10.url;
        r9.<init>(r10, r6);
        r10 = r6.start;
        r6 = r6.end;
        r1.setSpan(r9, r10, r6, r12);
        goto L_0x02ea;
    L_0x0300:
        r9 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        r11 = "";
        if (r9 == 0) goto L_0x0328;
    L_0x0306:
        r9 = new org.telegram.ui.Components.URLSpanUserMention;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r11);
        r11 = r6.urlEntity;
        r11 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r11;
        r11 = r11.user_id;
        r10.append(r11);
        r10 = r10.toString();
        r9.<init>(r10, r7, r6);
        r10 = r6.start;
        r6 = r6.end;
        r1.setSpan(r9, r10, r6, r12);
        goto L_0x02ea;
    L_0x0328:
        r9 = r10 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
        if (r9 == 0) goto L_0x0350;
    L_0x032c:
        r9 = new org.telegram.ui.Components.URLSpanUserMention;
        r10 = new java.lang.StringBuilder;
        r10.<init>();
        r10.append(r11);
        r11 = r6.urlEntity;
        r11 = (org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName) r11;
        r11 = r11.user_id;
        r11 = r11.user_id;
        r10.append(r11);
        r10 = r10.toString();
        r9.<init>(r10, r7, r6);
        r10 = r6.start;
        r6 = r6.end;
        r1.setSpan(r9, r10, r6, r12);
        goto L_0x02ea;
    L_0x0350:
        r9 = r6.flags;
        r10 = 4;
        r9 = r9 & r10;
        if (r9 == 0) goto L_0x0373;
    L_0x0356:
        r9 = new org.telegram.ui.Components.URLSpanMono;
        r11 = r6.start;
        r14 = r6.end;
        r17 = r9;
        r18 = r1;
        r19 = r11;
        r20 = r14;
        r21 = r7;
        r22 = r6;
        r17.<init>(r18, r19, r20, r21, r22);
        r11 = r6.start;
        r6 = r6.end;
        r1.setSpan(r9, r11, r6, r12);
        goto L_0x038d;
    L_0x0373:
        r9 = new org.telegram.ui.Components.TextStyleSpan;
        r9.<init>(r6);
        r11 = r6.start;
        r6 = r6.end;
        r1.setSpan(r9, r11, r6, r12);
        goto L_0x038d;
    L_0x0380:
        r10 = 4;
        r11 = new org.telegram.ui.Components.URLSpanNoUnderline;
        r11.<init>(r9, r6);
        r9 = r6.start;
        r6 = r6.end;
        r1.setSpan(r11, r9, r6, r12);
    L_0x038d:
        r3 = r3 + 1;
        goto L_0x0214;
    L_0x0391:
        return r5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addEntitiesToText(java.lang.CharSequence, java.util.ArrayList, boolean, int, boolean, boolean, boolean):boolean");
    }

    static /* synthetic */ int lambda$addEntitiesToText$0(MessageEntity messageEntity, MessageEntity messageEntity2) {
        int i = messageEntity.offset;
        int i2 = messageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public boolean needDrawShareButton() {
        boolean z = false;
        if (this.scheduled || this.eventId != 0) {
            return false;
        }
        if (this.messageOwner.fwd_from != null && !isOutOwner() && this.messageOwner.fwd_from.saved_from_peer != null && getDialogId() == ((long) UserConfig.getInstance(this.currentAccount).getClientUserId())) {
            return true;
        }
        int i = this.type;
        if (!(i == 13 || i == 15)) {
            MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
            if (messageFwdHeader != null && messageFwdHeader.channel_id != 0 && !isOutOwner()) {
                return true;
            }
            if (isFromUser()) {
                MessageMedia messageMedia = this.messageOwner.media;
                if ((messageMedia instanceof TL_messageMediaEmpty) || messageMedia == null || ((messageMedia instanceof TL_messageMediaWebPage) && !(messageMedia.webpage instanceof TL_webPage))) {
                    return false;
                }
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                if (user != null && user.bot) {
                    return true;
                }
                if (!isOut()) {
                    messageMedia = this.messageOwner.media;
                    if ((messageMedia instanceof TL_messageMediaGame) || (messageMedia instanceof TL_messageMediaInvoice)) {
                        return true;
                    }
                    if (isMegagroup()) {
                        Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
                        if (chat != null) {
                            String str = chat.username;
                            if (str != null && str.length() > 0) {
                                messageMedia = this.messageOwner.media;
                                if (!((messageMedia instanceof TL_messageMediaContact) || (messageMedia instanceof TL_messageMediaGeo))) {
                                    z = true;
                                }
                            }
                        }
                        return z;
                    }
                }
            }
            Message message = this.messageOwner;
            if (message.from_id < 0 || message.post) {
                message = this.messageOwner;
                if (message.to_id.channel_id != 0) {
                    if (!(message.via_bot_id == 0 && message.reply_to_msg_id == 0)) {
                        i = this.type;
                        if (i == 13 || i == 15) {
                            return false;
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isYouTubeVideo() {
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            WebPage webPage = messageMedia.webpage;
            if (!(webPage == null || TextUtils.isEmpty(webPage.embed_url))) {
                if ("YouTube".equals(this.messageOwner.media.webpage.site_name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /* JADX WARNING: Removed duplicated region for block: B:47:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x008f  */
    public int getMaxMessageTextWidth() {
        /*
        r6 = this;
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x0018;
    L_0x0006:
        r0 = r6.eventId;
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 == 0) goto L_0x0018;
    L_0x000e:
        r0 = NUM; // 0x44048000 float:530.0 double:5.63800838E-315;
        r0 = org.telegram.messenger.AndroidUtilities.dp(r0);
        r6.generatedWithMinSize = r0;
        goto L_0x0029;
    L_0x0018:
        r0 = org.telegram.messenger.AndroidUtilities.isTablet();
        if (r0 == 0) goto L_0x0023;
    L_0x001e:
        r0 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
        goto L_0x0027;
    L_0x0023:
        r0 = org.telegram.messenger.AndroidUtilities.displaySize;
        r0 = r0.x;
    L_0x0027:
        r6.generatedWithMinSize = r0;
    L_0x0029:
        r0 = org.telegram.messenger.AndroidUtilities.density;
        r6.generatedWithDensity = r0;
        r0 = r6.messageOwner;
        r0 = r0.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        r2 = NUM; // 0x43480000 float:200.0 double:5.5769738E-315;
        r3 = 0;
        if (r1 == 0) goto L_0x0083;
    L_0x0038:
        r0 = r0.webpage;
        if (r0 == 0) goto L_0x0083;
    L_0x003c:
        r0 = r0.type;
        r1 = "telegram_background";
        r0 = r1.equals(r0);
        if (r0 == 0) goto L_0x0083;
    L_0x0046:
        r0 = r6.messageOwner;	 Catch:{ Exception -> 0x0081 }
        r0 = r0.media;	 Catch:{ Exception -> 0x0081 }
        r0 = r0.webpage;	 Catch:{ Exception -> 0x0081 }
        r0 = r0.url;	 Catch:{ Exception -> 0x0081 }
        r0 = android.net.Uri.parse(r0);	 Catch:{ Exception -> 0x0081 }
        r1 = r0.getLastPathSegment();	 Catch:{ Exception -> 0x0081 }
        r4 = "bg_color";
        r0 = r0.getQueryParameter(r4);	 Catch:{ Exception -> 0x0081 }
        if (r0 == 0) goto L_0x0065;
    L_0x005e:
        r0 = NUM; // 0x435CLASSNAME float:220.0 double:5.58344962E-315;
        r3 = org.telegram.messenger.AndroidUtilities.dp(r0);	 Catch:{ Exception -> 0x0081 }
        goto L_0x008d;
    L_0x0065:
        r0 = r1.length();	 Catch:{ Exception -> 0x0081 }
        r4 = 6;
        if (r0 == r4) goto L_0x007c;
    L_0x006c:
        r0 = r1.length();	 Catch:{ Exception -> 0x0081 }
        r5 = 13;
        if (r0 != r5) goto L_0x008d;
    L_0x0074:
        r0 = r1.charAt(r4);	 Catch:{ Exception -> 0x0081 }
        r1 = 45;
        if (r0 != r1) goto L_0x008d;
    L_0x007c:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);	 Catch:{ Exception -> 0x0081 }
        goto L_0x008d;
        goto L_0x008d;
    L_0x0083:
        r0 = r6.isAndroidTheme();
        if (r0 == 0) goto L_0x008d;
    L_0x0089:
        r3 = org.telegram.messenger.AndroidUtilities.dp(r2);
    L_0x008d:
        if (r3 != 0) goto L_0x00c8;
    L_0x008f:
        r0 = r6.generatedWithMinSize;
        r1 = r6.needDrawAvatarInternal();
        if (r1 == 0) goto L_0x00a0;
    L_0x0097:
        r1 = r6.isOutOwner();
        if (r1 != 0) goto L_0x00a0;
    L_0x009d:
        r1 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
        goto L_0x00a2;
    L_0x00a0:
        r1 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
    L_0x00a2:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r1);
        r0 = r0 - r1;
        r1 = r6.needDrawShareButton();
        r2 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        if (r1 == 0) goto L_0x00ba;
    L_0x00af:
        r1 = r6.isOutOwner();
        if (r1 != 0) goto L_0x00ba;
    L_0x00b5:
        r1 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r0 = r0 - r1;
    L_0x00ba:
        r3 = r0;
        r0 = r6.messageOwner;
        r0 = r0.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x00c8;
    L_0x00c3:
        r0 = org.telegram.messenger.AndroidUtilities.dp(r2);
        r3 = r3 - r0;
    L_0x00c8:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getMaxMessageTextWidth():int");
    }

    /* JADX WARNING: Removed duplicated region for block: B:117:0x028c A:{SYNTHETIC, Splitter:B:117:0x028c} */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x02b1  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x030e  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0313  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x03dc  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x013b A:{Catch:{ Exception -> 0x044d }} */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0119 A:{Catch:{ Exception -> 0x044d }} */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0163  */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x007f  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00aa  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x010d  */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0110  */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0119 A:{Catch:{ Exception -> 0x044d }} */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x013b A:{Catch:{ Exception -> 0x044d }} */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x0163  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x0177  */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x030e  */
    /* JADX WARNING: Removed duplicated region for block: B:151:0x0313  */
    /* JADX WARNING: Removed duplicated region for block: B:154:0x031f  */
    /* JADX WARNING: Removed duplicated region for block: B:192:0x03dc  */
    /* JADX WARNING: Missing block: B:36:0x0077, code skipped:
            if ((r0.media instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported) == false) goto L_0x007b;
     */
    public void generateLayout(org.telegram.tgnet.TLRPC.User r30) {
        /*
        r29 = this;
        r1 = r29;
        r0 = r1.type;
        if (r0 != 0) goto L_0x0451;
    L_0x0006:
        r0 = r1.messageOwner;
        r0 = r0.to_id;
        if (r0 == 0) goto L_0x0451;
    L_0x000c:
        r0 = r1.messageText;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 == 0) goto L_0x0016;
    L_0x0014:
        goto L_0x0451;
    L_0x0016:
        r29.generateLinkDescription();
        r0 = new java.util.ArrayList;
        r0.<init>();
        r1.textLayoutBlocks = r0;
        r2 = 0;
        r1.textWidth = r2;
        r0 = r1.messageOwner;
        r3 = r0.send_state;
        r4 = 1;
        if (r3 == 0) goto L_0x002c;
    L_0x002a:
        r0 = 0;
        goto L_0x0033;
    L_0x002c:
        r0 = r0.entities;
        r0 = r0.isEmpty();
        r0 = r0 ^ r4;
    L_0x0033:
        if (r0 != 0) goto L_0x007b;
    L_0x0035:
        r5 = r1.eventId;
        r7 = 0;
        r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1));
        if (r0 != 0) goto L_0x0079;
    L_0x003d:
        r0 = r1.messageOwner;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old;
        if (r3 != 0) goto L_0x0079;
    L_0x0043:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old2;
        if (r3 != 0) goto L_0x0079;
    L_0x0047:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old3;
        if (r3 != 0) goto L_0x0079;
    L_0x004b:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_old4;
        if (r3 != 0) goto L_0x0079;
    L_0x004f:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageForwarded_old;
        if (r3 != 0) goto L_0x0079;
    L_0x0053:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageForwarded_old2;
        if (r3 != 0) goto L_0x0079;
    L_0x0057:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_secret;
        if (r3 != 0) goto L_0x0079;
    L_0x005b:
        r0 = r0.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
        if (r0 != 0) goto L_0x0079;
    L_0x0061:
        r0 = r29.isOut();
        if (r0 == 0) goto L_0x006d;
    L_0x0067:
        r0 = r1.messageOwner;
        r0 = r0.send_state;
        if (r0 != 0) goto L_0x0079;
    L_0x006d:
        r0 = r1.messageOwner;
        r3 = r0.id;
        if (r3 < 0) goto L_0x0079;
    L_0x0073:
        r0 = r0.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
        if (r0 == 0) goto L_0x007b;
    L_0x0079:
        r3 = 1;
        goto L_0x007c;
    L_0x007b:
        r3 = 0;
    L_0x007c:
        r5 = 4;
        if (r3 == 0) goto L_0x0089;
    L_0x007f:
        r0 = r29.isOutOwner();
        r6 = r1.messageText;
        addLinks(r0, r6);
        goto L_0x00a3;
    L_0x0089:
        r0 = r1.messageText;
        r6 = r0 instanceof android.text.Spannable;
        if (r6 == 0) goto L_0x00a3;
    L_0x008f:
        r0 = r0.length();
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r0 >= r6) goto L_0x00a3;
    L_0x0097:
        r0 = r1.messageText;	 Catch:{ all -> 0x009f }
        r0 = (android.text.Spannable) r0;	 Catch:{ all -> 0x009f }
        org.telegram.messenger.AndroidUtilities.addLinks(r0, r5);	 Catch:{ all -> 0x009f }
        goto L_0x00a3;
    L_0x009f:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00a3:
        r0 = r29.isYouTubeVideo();
        r6 = 3;
        if (r0 != 0) goto L_0x00ef;
    L_0x00aa:
        r0 = r1.replyMessageObject;
        if (r0 == 0) goto L_0x00b5;
    L_0x00ae:
        r0 = r0.isYouTubeVideo();
        if (r0 == 0) goto L_0x00b5;
    L_0x00b4:
        goto L_0x00ef;
    L_0x00b5:
        r0 = r1.replyMessageObject;
        if (r0 == 0) goto L_0x00fb;
    L_0x00b9:
        r0 = r0.isVideo();
        if (r0 == 0) goto L_0x00cf;
    L_0x00bf:
        r0 = r29.isOutOwner();
        r5 = r1.messageText;
        r7 = r1.replyMessageObject;
        r7 = r7.getDuration();
        addUrlsByPattern(r0, r5, r2, r6, r7);
        goto L_0x00fb;
    L_0x00cf:
        r0 = r1.replyMessageObject;
        r0 = r0.isMusic();
        if (r0 != 0) goto L_0x00df;
    L_0x00d7:
        r0 = r1.replyMessageObject;
        r0 = r0.isVoice();
        if (r0 == 0) goto L_0x00fb;
    L_0x00df:
        r0 = r29.isOutOwner();
        r7 = r1.messageText;
        r8 = r1.replyMessageObject;
        r8 = r8.getDuration();
        addUrlsByPattern(r0, r7, r2, r5, r8);
        goto L_0x00fb;
    L_0x00ef:
        r0 = r29.isOutOwner();
        r5 = r1.messageText;
        r7 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        addUrlsByPattern(r0, r5, r2, r6, r7);
    L_0x00fb:
        r0 = r1.messageText;
        r3 = r1.addEntitiesToText(r0, r3);
        r5 = r29.getMaxMessageTextWidth();
        r0 = r1.messageOwner;
        r0 = r0.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x0110;
    L_0x010d:
        r0 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint;
        goto L_0x0112;
    L_0x0110:
        r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;
    L_0x0112:
        r15 = r0;
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x044d }
        r14 = 24;
        if (r0 < r14) goto L_0x013b;
    L_0x0119:
        r0 = r1.messageText;	 Catch:{ Exception -> 0x044d }
        r7 = r1.messageText;	 Catch:{ Exception -> 0x044d }
        r7 = r7.length();	 Catch:{ Exception -> 0x044d }
        r0 = android.text.StaticLayout.Builder.obtain(r0, r2, r7, r15, r5);	 Catch:{ Exception -> 0x044d }
        r0 = r0.setBreakStrategy(r4);	 Catch:{ Exception -> 0x044d }
        r0 = r0.setHyphenationFrequency(r2);	 Catch:{ Exception -> 0x044d }
        r7 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x044d }
        r0 = r0.setAlignment(r7);	 Catch:{ Exception -> 0x044d }
        r0 = r0.build();	 Catch:{ Exception -> 0x044d }
        r14 = r0;
        r6 = 24;
        goto L_0x0151;
    L_0x013b:
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x044d }
        r8 = r1.messageText;	 Catch:{ Exception -> 0x044d }
        r11 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x044d }
        r12 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r13 = 0;
        r16 = 0;
        r7 = r0;
        r9 = r15;
        r10 = r5;
        r6 = 24;
        r14 = r16;
        r7.<init>(r8, r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x044d }
        r14 = r0;
    L_0x0151:
        r0 = r14.getHeight();
        r1.textHeight = r0;
        r0 = r14.getLineCount();
        r1.linesCount = r0;
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r6) goto L_0x0163;
    L_0x0161:
        r13 = 1;
        goto L_0x0170;
    L_0x0163:
        r0 = r1.linesCount;
        r0 = (float) r0;
        r7 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r0 = r0 / r7;
        r7 = (double) r0;
        r7 = java.lang.Math.ceil(r7);
        r0 = (int) r7;
        r13 = r0;
    L_0x0170:
        r12 = 0;
        r10 = 0;
        r11 = 0;
        r17 = 0;
    L_0x0175:
        if (r11 >= r13) goto L_0x044c;
    L_0x0177:
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r6) goto L_0x017e;
    L_0x017b:
        r0 = r1.linesCount;
        goto L_0x0187;
    L_0x017e:
        r0 = 10;
        r7 = r1.linesCount;
        r7 = r7 - r10;
        r0 = java.lang.Math.min(r0, r7);
    L_0x0187:
        r9 = new org.telegram.messenger.MessageObject$TextLayoutBlock;
        r9.<init>();
        r8 = 2;
        if (r13 != r4) goto L_0x01fb;
    L_0x018f:
        r9.textLayout = r14;
        r9.textYOffset = r12;
        r9.charactersOffset = r2;
        r7 = r1.emojiOnlyCount;
        if (r7 == 0) goto L_0x01e7;
    L_0x0199:
        if (r7 == r4) goto L_0x01d0;
    L_0x019b:
        if (r7 == r8) goto L_0x01b9;
    L_0x019d:
        r2 = 3;
        if (r7 == r2) goto L_0x01a1;
    L_0x01a0:
        goto L_0x01e7;
    L_0x01a1:
        r7 = r1.textHeight;
        r16 = NUM; // 0x40866666 float:4.2 double:5.348506967E-315;
        r18 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r7 = r7 - r18;
        r1.textHeight = r7;
        r7 = r9.textYOffset;
        r2 = org.telegram.messenger.AndroidUtilities.dp(r16);
        r2 = (float) r2;
        r7 = r7 - r2;
        r9.textYOffset = r7;
        goto L_0x01e7;
    L_0x01b9:
        r2 = r1.textHeight;
        r7 = NUM; // 0x40900000 float:4.5 double:5.35161536E-315;
        r16 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r2 = r2 - r16;
        r1.textHeight = r2;
        r2 = r9.textYOffset;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = (float) r7;
        r2 = r2 - r7;
        r9.textYOffset = r2;
        goto L_0x01e7;
    L_0x01d0:
        r2 = r1.textHeight;
        r7 = NUM; // 0x40a9999a float:5.3 double:5.35990441E-315;
        r16 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r2 = r2 - r16;
        r1.textHeight = r2;
        r2 = r9.textYOffset;
        r7 = org.telegram.messenger.AndroidUtilities.dp(r7);
        r7 = (float) r7;
        r2 = r2 - r7;
        r9.textYOffset = r2;
    L_0x01e7:
        r2 = r1.textHeight;
        r9.height = r2;
        r18 = r3;
        r6 = r9;
        r2 = r10;
        r4 = r11;
        r8 = r13;
        r3 = r14;
        r19 = r15;
        r7 = r17;
        r23 = 2;
    L_0x01f8:
        r9 = r0;
        goto L_0x02d3;
    L_0x01fb:
        r2 = r14.getLineStart(r10);
        r7 = r10 + r0;
        r7 = r7 - r4;
        r7 = r14.getLineEnd(r7);
        if (r7 >= r2) goto L_0x0215;
    L_0x0208:
        r18 = r3;
        r7 = r10;
        r4 = r11;
        r8 = r13;
        r16 = r14;
        r19 = r15;
        r2 = 0;
        r10 = 1;
        goto L_0x043a;
    L_0x0215:
        r9.charactersOffset = r2;
        r9.charactersEnd = r7;
        if (r3 == 0) goto L_0x0250;
    L_0x021b:
        r8 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x042b }
        if (r8 < r6) goto L_0x0250;
    L_0x021f:
        r8 = r1.messageText;	 Catch:{ Exception -> 0x042b }
        r18 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r18 = org.telegram.messenger.AndroidUtilities.dp(r18);	 Catch:{ Exception -> 0x042b }
        r6 = r5 + r18;
        r2 = android.text.StaticLayout.Builder.obtain(r8, r2, r7, r15, r6);	 Catch:{ Exception -> 0x042b }
        r2 = r2.setBreakStrategy(r4);	 Catch:{ Exception -> 0x042b }
        r6 = 0;
        r2 = r2.setHyphenationFrequency(r6);	 Catch:{ Exception -> 0x042b }
        r7 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x042b }
        r2 = r2.setAlignment(r7);	 Catch:{ Exception -> 0x042b }
        r2 = r2.build();	 Catch:{ Exception -> 0x042b }
        r9.textLayout = r2;	 Catch:{ Exception -> 0x042b }
        r18 = r3;
        r6 = r9;
        r2 = r10;
        r24 = r11;
        r25 = r13;
        r3 = r14;
        r19 = r15;
        r23 = 2;
        goto L_0x0281;
    L_0x0250:
        r6 = 0;
        r8 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x042b }
        r6 = r1.messageText;	 Catch:{ Exception -> 0x042b }
        r18 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x042b }
        r19 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r20 = 0;
        r21 = 0;
        r22 = r7;
        r7 = r8;
        r4 = r8;
        r23 = 2;
        r8 = r6;
        r6 = r9;
        r9 = r2;
        r2 = r10;
        r10 = r22;
        r24 = r11;
        r11 = r15;
        r12 = r5;
        r25 = r13;
        r13 = r18;
        r18 = r3;
        r3 = r14;
        r14 = r19;
        r19 = r15;
        r15 = r20;
        r16 = r21;
        r7.<init>(r8, r9, r10, r11, r12, r13, r14, r15, r16);	 Catch:{ Exception -> 0x0422 }
        r6.textLayout = r4;	 Catch:{ Exception -> 0x0422 }
    L_0x0281:
        r4 = r3.getLineTop(r2);	 Catch:{ Exception -> 0x0422 }
        r4 = (float) r4;	 Catch:{ Exception -> 0x0422 }
        r6.textYOffset = r4;	 Catch:{ Exception -> 0x0422 }
        r4 = r24;
        if (r4 == 0) goto L_0x0293;
    L_0x028c:
        r7 = r6.textYOffset;	 Catch:{ Exception -> 0x041d }
        r7 = r7 - r17;
        r7 = (int) r7;	 Catch:{ Exception -> 0x041d }
        r6.height = r7;	 Catch:{ Exception -> 0x041d }
    L_0x0293:
        r7 = r6.height;	 Catch:{ Exception -> 0x041d }
        r8 = r6.textLayout;	 Catch:{ Exception -> 0x041d }
        r9 = r6.textLayout;	 Catch:{ Exception -> 0x041d }
        r9 = r9.getLineCount();	 Catch:{ Exception -> 0x041d }
        r10 = 1;
        r9 = r9 - r10;
        r8 = r8.getLineBottom(r9);	 Catch:{ Exception -> 0x041d }
        r7 = java.lang.Math.max(r7, r8);	 Catch:{ Exception -> 0x041d }
        r6.height = r7;	 Catch:{ Exception -> 0x041d }
        r7 = r6.textYOffset;	 Catch:{ Exception -> 0x041d }
        r8 = r25;
        r13 = r8 + -1;
        if (r4 != r13) goto L_0x01f8;
    L_0x02b1:
        r9 = r6.textLayout;
        r9 = r9.getLineCount();
        r9 = java.lang.Math.max(r0, r9);
        r0 = r1.textHeight;	 Catch:{ Exception -> 0x02cf }
        r10 = r6.textYOffset;	 Catch:{ Exception -> 0x02cf }
        r11 = r6.textLayout;	 Catch:{ Exception -> 0x02cf }
        r11 = r11.getHeight();	 Catch:{ Exception -> 0x02cf }
        r11 = (float) r11;	 Catch:{ Exception -> 0x02cf }
        r10 = r10 + r11;
        r10 = (int) r10;	 Catch:{ Exception -> 0x02cf }
        r0 = java.lang.Math.max(r0, r10);	 Catch:{ Exception -> 0x02cf }
        r1.textHeight = r0;	 Catch:{ Exception -> 0x02cf }
        goto L_0x02d3;
    L_0x02cf:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x02d3:
        r0 = r1.textLayoutBlocks;
        r0.add(r6);
        r0 = r6.textLayout;	 Catch:{ Exception -> 0x02ec }
        r10 = r9 + -1;
        r12 = r0.getLineLeft(r10);	 Catch:{ Exception -> 0x02ec }
        r10 = 0;
        if (r4 != 0) goto L_0x02f6;
    L_0x02e3:
        r0 = (r12 > r10 ? 1 : (r12 == r10 ? 0 : -1));
        if (r0 < 0) goto L_0x02f6;
    L_0x02e7:
        r1.textXOffset = r12;	 Catch:{ Exception -> 0x02ea }
        goto L_0x02f6;
    L_0x02ea:
        r0 = move-exception;
        goto L_0x02ee;
    L_0x02ec:
        r0 = move-exception;
        r10 = 0;
    L_0x02ee:
        if (r4 != 0) goto L_0x02f2;
    L_0x02f0:
        r1.textXOffset = r10;
    L_0x02f2:
        org.telegram.messenger.FileLog.e(r0);
        r12 = 0;
    L_0x02f6:
        r0 = r6.textLayout;	 Catch:{ Exception -> 0x02ff }
        r11 = r9 + -1;
        r0 = r0.getLineWidth(r11);	 Catch:{ Exception -> 0x02ff }
        goto L_0x0304;
    L_0x02ff:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
    L_0x0304:
        r13 = (double) r0;
        r13 = java.lang.Math.ceil(r13);
        r11 = (int) r13;
        r13 = r5 + 80;
        if (r11 <= r13) goto L_0x030f;
    L_0x030e:
        r11 = r5;
    L_0x030f:
        r13 = r8 + -1;
        if (r4 != r13) goto L_0x0315;
    L_0x0313:
        r1.lastLineWidth = r11;
    L_0x0315:
        r0 = r0 + r12;
        r14 = (double) r0;
        r14 = java.lang.Math.ceil(r14);
        r14 = (int) r14;
        r15 = 1;
        if (r9 <= r15) goto L_0x03dc;
    L_0x031f:
        r26 = r11;
        r28 = r14;
        r11 = 0;
        r12 = 0;
        r15 = 0;
        r27 = 0;
    L_0x0328:
        if (r11 >= r9) goto L_0x03b6;
    L_0x032a:
        r0 = r6.textLayout;	 Catch:{ Exception -> 0x0331 }
        r0 = r0.getLineWidth(r11);	 Catch:{ Exception -> 0x0331 }
        goto L_0x0336;
    L_0x0331:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
    L_0x0336:
        r10 = r5 + 20;
        r10 = (float) r10;
        r10 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1));
        if (r10 <= 0) goto L_0x033e;
    L_0x033d:
        r0 = (float) r5;
    L_0x033e:
        r10 = r0;
        r0 = r6.textLayout;	 Catch:{ Exception -> 0x0346 }
        r0 = r0.getLineLeft(r11);	 Catch:{ Exception -> 0x0346 }
        goto L_0x034b;
    L_0x0346:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
    L_0x034b:
        r16 = 0;
        r17 = (r0 > r16 ? 1 : (r0 == r16 ? 0 : -1));
        if (r17 <= 0) goto L_0x0367;
    L_0x0351:
        r16 = r3;
        r3 = r1.textXOffset;
        r3 = java.lang.Math.min(r3, r0);
        r1.textXOffset = r3;
        r3 = r6.directionFlags;
        r17 = r7;
        r7 = 1;
        r3 = r3 | r7;
        r3 = (byte) r3;
        r6.directionFlags = r3;
        r1.hasRtl = r7;
        goto L_0x0372;
    L_0x0367:
        r16 = r3;
        r17 = r7;
        r3 = r6.directionFlags;
        r3 = r3 | 2;
        r3 = (byte) r3;
        r6.directionFlags = r3;
    L_0x0372:
        if (r12 != 0) goto L_0x0388;
    L_0x0374:
        r3 = 0;
        r7 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r7 != 0) goto L_0x0388;
    L_0x0379:
        r3 = r6.textLayout;	 Catch:{ Exception -> 0x0384 }
        r3 = r3.getParagraphDirection(r11);	 Catch:{ Exception -> 0x0384 }
        r7 = 1;
        if (r3 != r7) goto L_0x0388;
    L_0x0382:
        r12 = 1;
        goto L_0x0388;
    L_0x0384:
        r3 = r27;
        r12 = 1;
        goto L_0x038a;
    L_0x0388:
        r3 = r27;
    L_0x038a:
        r27 = java.lang.Math.max(r3, r10);
        r0 = r0 + r10;
        r15 = java.lang.Math.max(r15, r0);
        r7 = r2;
        r2 = (double) r10;
        r2 = java.lang.Math.ceil(r2);
        r2 = (int) r2;
        r10 = r26;
        r26 = java.lang.Math.max(r10, r2);
        r2 = (double) r0;
        r2 = java.lang.Math.ceil(r2);
        r0 = (int) r2;
        r2 = r28;
        r28 = java.lang.Math.max(r2, r0);
        r11 = r11 + 1;
        r2 = r7;
        r3 = r16;
        r7 = r17;
        r10 = 0;
        goto L_0x0328;
    L_0x03b6:
        r16 = r3;
        r17 = r7;
        r10 = r26;
        r3 = r27;
        r7 = r2;
        if (r12 == 0) goto L_0x03c6;
    L_0x03c1:
        if (r4 != r13) goto L_0x03cb;
    L_0x03c3:
        r1.lastLineWidth = r14;
        goto L_0x03cb;
    L_0x03c6:
        if (r4 != r13) goto L_0x03ca;
    L_0x03c8:
        r1.lastLineWidth = r10;
    L_0x03ca:
        r15 = r3;
    L_0x03cb:
        r0 = r1.textWidth;
        r2 = (double) r15;
        r2 = java.lang.Math.ceil(r2);
        r2 = (int) r2;
        r0 = java.lang.Math.max(r0, r2);
        r1.textWidth = r0;
        r2 = 0;
        r10 = 1;
        goto L_0x041a;
    L_0x03dc:
        r16 = r3;
        r17 = r7;
        r7 = r2;
        r2 = 0;
        r0 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x0406;
    L_0x03e6:
        r0 = r1.textXOffset;
        r0 = java.lang.Math.min(r0, r12);
        r1.textXOffset = r0;
        r0 = r1.textXOffset;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 != 0) goto L_0x03f7;
    L_0x03f4:
        r0 = (float) r11;
        r0 = r0 + r12;
        r11 = (int) r0;
    L_0x03f7:
        r10 = 1;
        if (r8 == r10) goto L_0x03fc;
    L_0x03fa:
        r0 = 1;
        goto L_0x03fd;
    L_0x03fc:
        r0 = 0;
    L_0x03fd:
        r1.hasRtl = r0;
        r0 = r6.directionFlags;
        r0 = r0 | r10;
        r0 = (byte) r0;
        r6.directionFlags = r0;
        goto L_0x040e;
    L_0x0406:
        r10 = 1;
        r0 = r6.directionFlags;
        r0 = r0 | 2;
        r0 = (byte) r0;
        r6.directionFlags = r0;
    L_0x040e:
        r0 = r1.textWidth;
        r3 = java.lang.Math.min(r5, r11);
        r0 = java.lang.Math.max(r0, r3);
        r1.textWidth = r0;
    L_0x041a:
        r0 = r7 + r9;
        goto L_0x043b;
    L_0x041d:
        r0 = move-exception;
        r7 = r2;
        r16 = r3;
        goto L_0x0428;
    L_0x0422:
        r0 = move-exception;
        r7 = r2;
        r16 = r3;
        r4 = r24;
    L_0x0428:
        r8 = r25;
        goto L_0x0435;
    L_0x042b:
        r0 = move-exception;
        r18 = r3;
        r7 = r10;
        r4 = r11;
        r8 = r13;
        r16 = r14;
        r19 = r15;
    L_0x0435:
        r2 = 0;
        r10 = 1;
        org.telegram.messenger.FileLog.e(r0);
    L_0x043a:
        r0 = r7;
    L_0x043b:
        r11 = r4 + 1;
        r10 = r0;
        r13 = r8;
        r14 = r16;
        r3 = r18;
        r15 = r19;
        r2 = 0;
        r4 = 1;
        r6 = 24;
        r12 = 0;
        goto L_0x0175;
    L_0x044c:
        return;
    L_0x044d:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0451:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLayout(org.telegram.tgnet.TLRPC$User):void");
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    /* JADX WARNING: Missing block: B:16:0x0037, code skipped:
            if (r3.user_id != r0) goto L_0x0039;
     */
    public boolean isOutOwner() {
        /*
        r8 = this;
        r0 = r8.messageOwner;
        r1 = r0.out;
        r2 = 0;
        if (r1 == 0) goto L_0x0057;
    L_0x0007:
        r1 = r0.from_id;
        if (r1 <= 0) goto L_0x0057;
    L_0x000b:
        r1 = r0.post;
        if (r1 == 0) goto L_0x0010;
    L_0x000f:
        goto L_0x0057;
    L_0x0010:
        r0 = r0.fwd_from;
        r1 = 1;
        if (r0 != 0) goto L_0x0016;
    L_0x0015:
        return r1;
    L_0x0016:
        r0 = r8.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.getClientUserId();
        r3 = r8.getDialogId();
        r5 = (long) r0;
        r7 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r7 != 0) goto L_0x0048;
    L_0x0029:
        r3 = r8.messageOwner;
        r3 = r3.fwd_from;
        r4 = r3.from_id;
        if (r4 != r0) goto L_0x0039;
    L_0x0031:
        r3 = r3.saved_from_peer;
        if (r3 == 0) goto L_0x0047;
    L_0x0035:
        r3 = r3.user_id;
        if (r3 == r0) goto L_0x0047;
    L_0x0039:
        r3 = r8.messageOwner;
        r3 = r3.fwd_from;
        r3 = r3.saved_from_peer;
        if (r3 == 0) goto L_0x0046;
    L_0x0041:
        r3 = r3.user_id;
        if (r3 != r0) goto L_0x0046;
    L_0x0045:
        goto L_0x0047;
    L_0x0046:
        r1 = 0;
    L_0x0047:
        return r1;
    L_0x0048:
        r3 = r8.messageOwner;
        r3 = r3.fwd_from;
        r3 = r3.saved_from_peer;
        if (r3 == 0) goto L_0x0056;
    L_0x0050:
        r3 = r3.user_id;
        if (r3 != r0) goto L_0x0055;
    L_0x0054:
        goto L_0x0056;
    L_0x0055:
        r1 = 0;
    L_0x0056:
        return r1;
    L_0x0057:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isOutOwner():boolean");
    }

    public boolean needDrawAvatar() {
        if (!isFromUser() && this.eventId == 0) {
            MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
            if (messageFwdHeader == null || messageFwdHeader.saved_from_peer == null) {
                return false;
            }
        }
        return true;
    }

    private boolean needDrawAvatarInternal() {
        if (!(isFromChat() && isFromUser()) && this.eventId == 0) {
            MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
            if (messageFwdHeader == null || messageFwdHeader.saved_from_peer == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isFromChat() {
        if (!(getDialogId() == ((long) UserConfig.getInstance(this.currentAccount).clientUserId) || isMegagroup())) {
            Peer peer = this.messageOwner.to_id;
            if (peer == null || peer.chat_id == 0) {
                peer = this.messageOwner.to_id;
                boolean z = false;
                if (!(peer == null || peer.channel_id == 0)) {
                    Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
                    if (chat != null && chat.megagroup) {
                        z = true;
                    }
                }
                return z;
            }
        }
        return true;
    }

    public boolean isFromUser() {
        Message message = this.messageOwner;
        return message.from_id > 0 && !message.post;
    }

    public boolean isForwardedChannelPost() {
        Message message = this.messageOwner;
        if (message.from_id <= 0) {
            MessageFwdHeader messageFwdHeader = message.fwd_from;
            if (!(messageFwdHeader == null || messageFwdHeader.channel_post == 0)) {
                return true;
            }
        }
        return false;
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

    public static int getUnreadFlags(Message message) {
        int i = !message.unread ? 1 : 0;
        return !message.media_unread ? i | 2 : i;
    }

    public void setContentIsRead() {
        this.messageOwner.media_unread = false;
    }

    public int getId() {
        return this.messageOwner.id;
    }

    public int getRealId() {
        Message message = this.messageOwner;
        int i = message.realId;
        return i != 0 ? i : message.id;
    }

    public static int getMessageSize(Message message) {
        MessageMedia messageMedia = message.media;
        Document document = messageMedia instanceof TL_messageMediaWebPage ? messageMedia.webpage.document : messageMedia instanceof TL_messageMediaGame ? messageMedia.game.document : messageMedia != null ? messageMedia.document : null;
        return document != null ? document.size : 0;
    }

    public int getSize() {
        return getMessageSize(this.messageOwner);
    }

    public long getIdWithChannel() {
        Message message = this.messageOwner;
        long j = (long) message.id;
        Peer peer = message.to_id;
        if (peer == null) {
            return j;
        }
        int i = peer.channel_id;
        return i != 0 ? j | (((long) i) << 32) : j;
    }

    public int getChannelId() {
        Peer peer = this.messageOwner.to_id;
        return peer != null ? peer.channel_id : 0;
    }

    /* JADX WARNING: Missing block: B:9:0x0018, code skipped:
            if (r4 <= 60) goto L_0x001c;
     */
    public static boolean shouldEncryptPhotoOrVideo(org.telegram.tgnet.TLRPC.Message r4) {
        /*
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_message_secret;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x001d;
    L_0x0006:
        r0 = r4.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x0012;
    L_0x000c:
        r0 = isVideoMessage(r4);
        if (r0 == 0) goto L_0x001b;
    L_0x0012:
        r4 = r4.ttl;
        if (r4 <= 0) goto L_0x001b;
    L_0x0016:
        r0 = 60;
        if (r4 > r0) goto L_0x001b;
    L_0x001a:
        goto L_0x001c;
    L_0x001b:
        r1 = 0;
    L_0x001c:
        return r1;
    L_0x001d:
        r0 = r4.media;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r3 != 0) goto L_0x0027;
    L_0x0023:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r0 == 0) goto L_0x002e;
    L_0x0027:
        r4 = r4.media;
        r4 = r4.ttl_seconds;
        if (r4 == 0) goto L_0x002e;
    L_0x002d:
        goto L_0x002f;
    L_0x002e:
        r1 = 0;
    L_0x002f:
        return r1;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.shouldEncryptPhotoOrVideo(org.telegram.tgnet.TLRPC$Message):boolean");
    }

    public boolean shouldEncryptPhotoOrVideo() {
        return shouldEncryptPhotoOrVideo(this.messageOwner);
    }

    /* JADX WARNING: Missing block: B:11:0x001e, code skipped:
            if (r4 <= 60) goto L_0x0022;
     */
    public static boolean isSecretPhotoOrVideo(org.telegram.tgnet.TLRPC.Message r4) {
        /*
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_message_secret;
        r1 = 1;
        r2 = 0;
        if (r0 == 0) goto L_0x0023;
    L_0x0006:
        r0 = r4.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x0018;
    L_0x000c:
        r0 = isRoundVideoMessage(r4);
        if (r0 != 0) goto L_0x0018;
    L_0x0012:
        r0 = isVideoMessage(r4);
        if (r0 == 0) goto L_0x0021;
    L_0x0018:
        r4 = r4.ttl;
        if (r4 <= 0) goto L_0x0021;
    L_0x001c:
        r0 = 60;
        if (r4 > r0) goto L_0x0021;
    L_0x0020:
        goto L_0x0022;
    L_0x0021:
        r1 = 0;
    L_0x0022:
        return r1;
    L_0x0023:
        r0 = r4 instanceof org.telegram.tgnet.TLRPC.TL_message;
        if (r0 == 0) goto L_0x003a;
    L_0x0027:
        r0 = r4.media;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r3 != 0) goto L_0x0031;
    L_0x002d:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r0 == 0) goto L_0x0038;
    L_0x0031:
        r4 = r4.media;
        r4 = r4.ttl_seconds;
        if (r4 == 0) goto L_0x0038;
    L_0x0037:
        goto L_0x0039;
    L_0x0038:
        r1 = 0;
    L_0x0039:
        return r1;
    L_0x003a:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isSecretPhotoOrVideo(org.telegram.tgnet.TLRPC$Message):boolean");
    }

    public static boolean isSecretMedia(Message message) {
        boolean z = true;
        if (message instanceof TL_message_secret) {
            if (!((message.media instanceof TL_messageMediaPhoto) || isRoundVideoMessage(message) || isVideoMessage(message)) || message.media.ttl_seconds == 0) {
                z = false;
            }
            return z;
        } else if (!(message instanceof TL_message)) {
            return false;
        } else {
            MessageMedia messageMedia = message.media;
            if (!((messageMedia instanceof TL_messageMediaPhoto) || (messageMedia instanceof TL_messageMediaDocument)) || message.media.ttl_seconds == 0) {
                z = false;
            }
            return z;
        }
    }

    public boolean needDrawBluredPreview() {
        Message message = this.messageOwner;
        boolean z = true;
        if (message instanceof TL_message_secret) {
            int max = Math.max(message.ttl, message.media.ttl_seconds);
            if (max <= 0 || ((!((this.messageOwner.media instanceof TL_messageMediaPhoto) || isVideo() || isGif()) || max > 60) && !isRoundVideo())) {
                z = false;
            }
            return z;
        } else if (!(message instanceof TL_message)) {
            return false;
        } else {
            MessageMedia messageMedia = message.media;
            if (!((messageMedia instanceof TL_messageMediaPhoto) || (messageMedia instanceof TL_messageMediaDocument)) || this.messageOwner.media.ttl_seconds == 0) {
                z = false;
            }
            return z;
        }
    }

    /* JADX WARNING: Missing block: B:9:0x001c, code skipped:
            if (r0 <= 60) goto L_0x0032;
     */
    public boolean isSecretMedia() {
        /*
        r4 = this;
        r0 = r4.messageOwner;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message_secret;
        r2 = 1;
        r3 = 0;
        if (r1 == 0) goto L_0x0033;
    L_0x0008:
        r0 = r0.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r0 != 0) goto L_0x0014;
    L_0x000e:
        r0 = r4.isGif();
        if (r0 == 0) goto L_0x001e;
    L_0x0014:
        r0 = r4.messageOwner;
        r0 = r0.ttl;
        if (r0 <= 0) goto L_0x001e;
    L_0x001a:
        r1 = 60;
        if (r0 <= r1) goto L_0x0032;
    L_0x001e:
        r0 = r4.isVoice();
        if (r0 != 0) goto L_0x0032;
    L_0x0024:
        r0 = r4.isRoundVideo();
        if (r0 != 0) goto L_0x0032;
    L_0x002a:
        r0 = r4.isVideo();
        if (r0 == 0) goto L_0x0031;
    L_0x0030:
        goto L_0x0032;
    L_0x0031:
        r2 = 0;
    L_0x0032:
        return r2;
    L_0x0033:
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_message;
        if (r1 == 0) goto L_0x004c;
    L_0x0037:
        r0 = r0.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r1 != 0) goto L_0x0041;
    L_0x003d:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r0 == 0) goto L_0x004a;
    L_0x0041:
        r0 = r4.messageOwner;
        r0 = r0.media;
        r0 = r0.ttl_seconds;
        if (r0 == 0) goto L_0x004a;
    L_0x0049:
        goto L_0x004b;
    L_0x004a:
        r2 = 0;
    L_0x004b:
        return r2;
    L_0x004c:
        return r3;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isSecretMedia():boolean");
    }

    public static void setUnreadFlags(Message message, int i) {
        boolean z = false;
        message.unread = (i & 1) == 0;
        if ((i & 2) == 0) {
            z = true;
        }
        message.media_unread = z;
    }

    public static boolean isUnread(Message message) {
        return message.unread;
    }

    public static boolean isContentUnread(Message message) {
        return message.media_unread;
    }

    public boolean isMegagroup() {
        return isMegagroup(this.messageOwner);
    }

    public boolean isSavedFromMegagroup() {
        MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
        if (messageFwdHeader != null) {
            Peer peer = messageFwdHeader.saved_from_peer;
            if (!(peer == null || peer.channel_id == 0)) {
                return ChatObject.isMegagroup(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.saved_from_peer.channel_id)));
            }
        }
        return false;
    }

    public static boolean isMegagroup(Message message) {
        return (message.flags & Integer.MIN_VALUE) != 0;
    }

    public static boolean isOut(Message message) {
        return message.out;
    }

    public long getDialogId() {
        return getDialogId(this.messageOwner);
    }

    public boolean canStreamVideo() {
        Document document = getDocument();
        if (!(document == null || (document instanceof TL_documentEncrypted))) {
            if (SharedConfig.streamAllVideo) {
                return true;
            }
            for (int i = 0; i < document.attributes.size(); i++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                if (documentAttribute instanceof TL_documentAttributeVideo) {
                    return documentAttribute.supports_streaming;
                }
            }
            if (SharedConfig.streamMkv) {
                if ("video/x-matroska".equals(document.mime_type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static long getDialogId(Message message) {
        if (message.dialog_id == 0) {
            Peer peer = message.to_id;
            if (peer != null) {
                int i = peer.chat_id;
                if (i != 0) {
                    message.dialog_id = (long) (-i);
                } else {
                    int i2 = peer.channel_id;
                    if (i2 != 0) {
                        message.dialog_id = (long) (-i2);
                    } else if (isOut(message)) {
                        message.dialog_id = (long) message.to_id.user_id;
                    } else {
                        message.dialog_id = (long) message.from_id;
                    }
                }
            }
        }
        return message.dialog_id;
    }

    public boolean isSending() {
        Message message = this.messageOwner;
        return message.send_state == 1 && message.id < 0;
    }

    public boolean isEditing() {
        Message message = this.messageOwner;
        return message.send_state == 3 && message.id > 0;
    }

    /* JADX WARNING: Missing block: B:9:0x0023, code skipped:
            if (r0.date < (org.telegram.tgnet.ConnectionsManager.getInstance(r3.currentAccount).getCurrentTime() - 60)) goto L_0x0025;
     */
    public boolean isSendError() {
        /*
        r3 = this;
        r0 = r3.messageOwner;
        r1 = r0.send_state;
        r2 = 2;
        if (r1 != r2) goto L_0x000b;
    L_0x0007:
        r0 = r0.id;
        if (r0 < 0) goto L_0x0025;
    L_0x000b:
        r0 = r3.scheduled;
        if (r0 == 0) goto L_0x0027;
    L_0x000f:
        r0 = r3.messageOwner;
        r1 = r0.id;
        if (r1 <= 0) goto L_0x0027;
    L_0x0015:
        r0 = r0.date;
        r1 = r3.currentAccount;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r1);
        r1 = r1.getCurrentTime();
        r1 = r1 + -60;
        if (r0 >= r1) goto L_0x0027;
    L_0x0025:
        r0 = 1;
        goto L_0x0028;
    L_0x0027:
        r0 = 0;
    L_0x0028:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isSendError():boolean");
    }

    public boolean isSent() {
        Message message = this.messageOwner;
        return message.send_state == 0 || message.id > 0;
    }

    public int getSecretTimeLeft() {
        Message message = this.messageOwner;
        int i = message.ttl;
        int i2 = message.destroyTime;
        return i2 != 0 ? Math.max(0, i2 - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) : i;
    }

    public String getSecretTimeString() {
        if (!isSecretMedia()) {
            return null;
        }
        String stringBuilder;
        int secretTimeLeft = getSecretTimeLeft();
        if (secretTimeLeft < 60) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(secretTimeLeft);
            stringBuilder2.append("s");
            stringBuilder = stringBuilder2.toString();
        } else {
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(secretTimeLeft / 60);
            stringBuilder3.append("m");
            stringBuilder = stringBuilder3.toString();
        }
        return stringBuilder;
    }

    public String getDocumentName() {
        return FileLoader.getDocumentFileName(getDocument());
    }

    public static boolean isStickerDocument(Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                if (((DocumentAttribute) document.attributes.get(i)) instanceof TL_documentAttributeSticker) {
                    return "image/webp".equals(document.mime_type);
                }
            }
        }
        return false;
    }

    public static boolean isStickerHasSet(Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                if (documentAttribute instanceof TL_documentAttributeSticker) {
                    InputStickerSet inputStickerSet = documentAttribute.stickerset;
                    if (!(inputStickerSet == null || (inputStickerSet instanceof TL_inputStickerSetEmpty))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isAnimatedStickerDocument(Document document, boolean z) {
        if (document != null) {
            if ("application/x-tgsticker".equals(document.mime_type) && !document.thumbs.isEmpty()) {
                if (z) {
                    return true;
                }
                int size = document.attributes.size();
                for (int i = 0; i < size; i++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (documentAttribute instanceof TL_documentAttributeSticker) {
                        InputStickerSet inputStickerSet = documentAttribute.stickerset;
                        if (!(inputStickerSet == null || (inputStickerSet instanceof TL_inputStickerSetEmpty))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean canAutoplayAnimatedSticker(Document document) {
        return isAnimatedStickerDocument(document, true) && SharedConfig.getDevicePerfomanceClass() != 0;
    }

    public static boolean isMaskDocument(Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                if ((documentAttribute instanceof TL_documentAttributeSticker) && documentAttribute.mask) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceDocument(Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                if (documentAttribute instanceof TL_documentAttributeAudio) {
                    return documentAttribute.voice;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceWebDocument(WebFile webFile) {
        return webFile != null && webFile.mime_type.equals("audio/ogg");
    }

    public static boolean isImageWebDocument(WebFile webFile) {
        return (webFile == null || isGifDocument(webFile) || !webFile.mime_type.startsWith("image/")) ? false : true;
    }

    public static boolean isVideoWebDocument(WebFile webFile) {
        return webFile != null && webFile.mime_type.startsWith("video/");
    }

    public static boolean isMusicDocument(Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                if (documentAttribute instanceof TL_documentAttributeAudio) {
                    return documentAttribute.voice ^ 1;
                }
            }
            if (!TextUtils.isEmpty(document.mime_type)) {
                String toLowerCase = document.mime_type.toLowerCase();
                if (toLowerCase.equals("audio/flac") || toLowerCase.equals("audio/ogg") || toLowerCase.equals("audio/opus") || toLowerCase.equals("audio/x-opus+ogg") || (toLowerCase.equals("application/octet-stream") && FileLoader.getDocumentFileName(document).endsWith(".opus"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isVideoDocument(Document document) {
        boolean z = false;
        if (document != null) {
            Object obj = null;
            int i = 0;
            int i2 = 0;
            Object obj2 = null;
            for (int i3 = 0; i3 < document.attributes.size(); i3++) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i3);
                if (documentAttribute instanceof TL_documentAttributeVideo) {
                    if (documentAttribute.round_message) {
                        return false;
                    }
                    i = documentAttribute.w;
                    i2 = documentAttribute.h;
                    obj2 = 1;
                } else if (documentAttribute instanceof TL_documentAttributeAnimated) {
                    obj = 1;
                }
            }
            if (obj != null && (i > 1280 || i2 > 1280)) {
                obj = null;
            }
            if (SharedConfig.streamMkv && r5 == null) {
                if ("video/x-matroska".equals(document.mime_type)) {
                    obj2 = 1;
                }
            }
            if (obj2 != null && r2 == null) {
                z = true;
            }
        }
        return z;
    }

    public Document getDocument() {
        Document document = this.emojiAnimatedSticker;
        if (document != null) {
            return document;
        }
        return getDocument(this.messageOwner);
    }

    public static Document getDocument(Message message) {
        MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            return messageMedia.webpage.document;
        }
        if (messageMedia instanceof TL_messageMediaGame) {
            return messageMedia.game.document;
        }
        return messageMedia != null ? messageMedia.document : null;
    }

    public static Photo getPhoto(Message message) {
        MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            return messageMedia.webpage.photo;
        }
        return messageMedia != null ? messageMedia.photo : null;
    }

    public static boolean isStickerMessage(Message message) {
        MessageMedia messageMedia = message.media;
        return messageMedia != null && isStickerDocument(messageMedia.document);
    }

    public static boolean isAnimatedStickerMessage(Message message) {
        MessageMedia messageMedia = message.media;
        return messageMedia != null && isAnimatedStickerDocument(messageMedia.document, DialogObject.isSecretDialogId(message.dialog_id) ^ 1);
    }

    public static boolean isLocationMessage(Message message) {
        MessageMedia messageMedia = message.media;
        return (messageMedia instanceof TL_messageMediaGeo) || (messageMedia instanceof TL_messageMediaGeoLive) || (messageMedia instanceof TL_messageMediaVenue);
    }

    public static boolean isMaskMessage(Message message) {
        MessageMedia messageMedia = message.media;
        return messageMedia != null && isMaskDocument(messageMedia.document);
    }

    public static boolean isMusicMessage(Message message) {
        MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            return isMusicDocument(messageMedia.webpage.document);
        }
        boolean z = messageMedia != null && isMusicDocument(messageMedia.document);
        return z;
    }

    public static boolean isGifMessage(Message message) {
        MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            return isGifDocument(messageMedia.webpage.document);
        }
        boolean z = messageMedia != null && isGifDocument(messageMedia.document);
        return z;
    }

    public static boolean isRoundVideoMessage(Message message) {
        MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            return isRoundVideoDocument(messageMedia.webpage.document);
        }
        boolean z = messageMedia != null && isRoundVideoDocument(messageMedia.document);
        return z;
    }

    public static boolean isPhoto(Message message) {
        MessageMedia messageMedia = message.media;
        if (!(messageMedia instanceof TL_messageMediaWebPage)) {
            return messageMedia instanceof TL_messageMediaPhoto;
        }
        WebPage webPage = messageMedia.webpage;
        boolean z = (webPage.photo instanceof TL_photo) && !(webPage.document instanceof TL_document);
        return z;
    }

    public static boolean isVoiceMessage(Message message) {
        MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            return isVoiceDocument(messageMedia.webpage.document);
        }
        boolean z = messageMedia != null && isVoiceDocument(messageMedia.document);
        return z;
    }

    public static boolean isNewGifMessage(Message message) {
        MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            return isNewGifDocument(messageMedia.webpage.document);
        }
        boolean z = messageMedia != null && isNewGifDocument(messageMedia.document);
        return z;
    }

    public static boolean isLiveLocationMessage(Message message) {
        return message.media instanceof TL_messageMediaGeoLive;
    }

    public static boolean isVideoMessage(Message message) {
        MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            return isVideoDocument(messageMedia.webpage.document);
        }
        boolean z = messageMedia != null && isVideoDocument(messageMedia.document);
        return z;
    }

    public static boolean isGameMessage(Message message) {
        return message.media instanceof TL_messageMediaGame;
    }

    public static boolean isInvoiceMessage(Message message) {
        return message.media instanceof TL_messageMediaInvoice;
    }

    public static InputStickerSet getInputStickerSet(Message message) {
        MessageMedia messageMedia = message.media;
        if (messageMedia != null) {
            Document document = messageMedia.document;
            if (document != null) {
                return getInputStickerSet(document);
            }
        }
        return null;
    }

    public static InputStickerSet getInputStickerSet(Document document) {
        if (document == null) {
            return null;
        }
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TL_inputStickerSetEmpty) {
                    return null;
                }
                return inputStickerSet;
            }
        }
        return null;
    }

    public static long getStickerSetId(Document document) {
        if (document == null) {
            return -1;
        }
        for (int i = 0; i < document.attributes.size(); i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TL_inputStickerSetEmpty) {
                    return -1;
                }
                return inputStickerSet.id;
            }
        }
        return -1;
    }

    public String getStrickerChar() {
        Document document = getDocument();
        if (document != null) {
            Iterator it = document.attributes.iterator();
            while (it.hasNext()) {
                DocumentAttribute documentAttribute = (DocumentAttribute) it.next();
                if (documentAttribute instanceof TL_documentAttributeSticker) {
                    return documentAttribute.alt;
                }
            }
        }
        return null;
    }

    public int getApproximateHeight() {
        int i = this.type;
        int i2 = 0;
        if (i == 0) {
            i = this.textHeight;
            MessageMedia messageMedia = this.messageOwner.media;
            if ((messageMedia instanceof TL_messageMediaWebPage) && (messageMedia.webpage instanceof TL_webPage)) {
                i2 = AndroidUtilities.dp(100.0f);
            }
            i += i2;
            if (isReply()) {
                i += AndroidUtilities.dp(42.0f);
            }
            return i;
        } else if (i == 2) {
            return AndroidUtilities.dp(72.0f);
        } else {
            if (i == 12) {
                return AndroidUtilities.dp(71.0f);
            }
            if (i == 9) {
                return AndroidUtilities.dp(100.0f);
            }
            if (i == 4) {
                return AndroidUtilities.dp(114.0f);
            }
            if (i == 14) {
                return AndroidUtilities.dp(82.0f);
            }
            if (i == 10) {
                return AndroidUtilities.dp(30.0f);
            }
            if (i == 11) {
                return AndroidUtilities.dp(50.0f);
            }
            if (i == 5) {
                return AndroidUtilities.roundMessageSize;
            }
            if (i == 13 || i == 15) {
                int minTabletSide;
                int i3;
                float f = ((float) AndroidUtilities.displaySize.y) * 0.4f;
                if (AndroidUtilities.isTablet()) {
                    minTabletSide = AndroidUtilities.getMinTabletSide();
                } else {
                    minTabletSide = AndroidUtilities.displaySize.x;
                }
                float f2 = ((float) minTabletSide) * 0.5f;
                Document document = getDocument();
                int size = document.attributes.size();
                for (int i4 = 0; i4 < size; i4++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i4);
                    if (documentAttribute instanceof TL_documentAttributeImageSize) {
                        i2 = documentAttribute.w;
                        i3 = documentAttribute.h;
                        break;
                    }
                }
                i3 = 0;
                if (i2 == 0) {
                    i3 = (int) f;
                    i2 = AndroidUtilities.dp(100.0f) + i3;
                }
                float f3 = (float) i3;
                if (f3 > f) {
                    i2 = (int) (((float) i2) * (f / f3));
                    i3 = (int) f;
                }
                f = (float) i2;
                if (f > f2) {
                    i3 = (int) (((float) i3) * (f2 / f));
                }
                return i3 + AndroidUtilities.dp(14.0f);
            }
            Point point;
            if (AndroidUtilities.isTablet()) {
                i = AndroidUtilities.getMinTabletSide();
            } else {
                point = AndroidUtilities.displaySize;
                i = Math.min(point.x, point.y);
            }
            i = (int) (((float) i) * 0.7f);
            i2 = AndroidUtilities.dp(100.0f) + i;
            if (i > AndroidUtilities.getPhotoSize()) {
                i = AndroidUtilities.getPhotoSize();
            }
            if (i2 > AndroidUtilities.getPhotoSize()) {
                i2 = AndroidUtilities.getPhotoSize();
            }
            PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
            if (closestPhotoSizeWithSize != null) {
                i = (int) (((float) closestPhotoSizeWithSize.h) / (((float) closestPhotoSizeWithSize.w) / ((float) i)));
                if (i == 0) {
                    i = AndroidUtilities.dp(100.0f);
                }
                if (i <= i2) {
                    i2 = i < AndroidUtilities.dp(120.0f) ? AndroidUtilities.dp(120.0f) : i;
                }
                if (needDrawBluredPreview()) {
                    if (AndroidUtilities.isTablet()) {
                        i = AndroidUtilities.getMinTabletSide();
                    } else {
                        point = AndroidUtilities.displaySize;
                        i = Math.min(point.x, point.y);
                    }
                    i2 = (int) (((float) i) * 0.5f);
                }
            }
            return i2 + AndroidUtilities.dp(14.0f);
        }
    }

    public String getStickerEmoji() {
        Document document = getDocument();
        String str = null;
        if (document == null) {
            return null;
        }
        for (int i = 0; i < document.attributes.size(); i++) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeSticker) {
                String str2 = documentAttribute.alt;
                if (str2 != null && str2.length() > 0) {
                    str = documentAttribute.alt;
                }
                return str;
            }
        }
        return null;
    }

    public boolean isAnimatedEmoji() {
        return this.emojiAnimatedSticker != null;
    }

    public boolean isSticker() {
        int i = this.type;
        if (i == 1000) {
            return isStickerDocument(getDocument());
        }
        return i == 13;
    }

    public boolean isAnimatedSticker() {
        int i = this.type;
        boolean z = true;
        if (i == 1000) {
            return isAnimatedStickerDocument(getDocument(), 1 ^ DialogObject.isSecretDialogId(getDialogId()));
        }
        if (i != 15) {
            z = false;
        }
        return z;
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
            int i = (this.type == 5 || isRoundVideoMessage(this.messageOwner)) ? 1 : 2;
            this.isRoundVideoCached = i;
        }
        if (this.isRoundVideoCached == 1) {
            return true;
        }
        return false;
    }

    public boolean hasPhotoStickers() {
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia != null) {
            Photo photo = messageMedia.photo;
            if (photo != null && photo.has_stickers) {
                return true;
            }
        }
        return false;
    }

    public boolean isGif() {
        return isGifMessage(this.messageOwner);
    }

    public boolean isWebpageDocument() {
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            Document document = messageMedia.webpage.document;
            if (!(document == null || isGifDocument(document))) {
                return true;
            }
        }
        return false;
    }

    public boolean isWebpage() {
        return this.messageOwner.media instanceof TL_messageMediaWebPage;
    }

    public boolean isNewGif() {
        MessageMedia messageMedia = this.messageOwner.media;
        return messageMedia != null && isNewGifDocument(messageMedia.document);
    }

    public boolean isAndroidTheme() {
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia != null) {
            WebPage webPage = messageMedia.webpage;
            if (!(webPage == null || webPage.attributes.isEmpty())) {
                int size = this.messageOwner.media.webpage.attributes.size();
                for (int i = 0; i < size; i++) {
                    TL_webPageAttributeTheme tL_webPageAttributeTheme = (TL_webPageAttributeTheme) this.messageOwner.media.webpage.attributes.get(i);
                    ArrayList arrayList = tL_webPageAttributeTheme.documents;
                    int size2 = arrayList.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        if ("application/x-tgtheme-android".equals(((Document) arrayList.get(i2)).mime_type)) {
                            return true;
                        }
                    }
                    if (tL_webPageAttributeTheme.settings != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String getMusicTitle() {
        return getMusicTitle(true);
    }

    public String getMusicTitle(boolean z) {
        Document document = getDocument();
        String str = "AudioUnknownTitle";
        if (document != null) {
            int i = 0;
            while (i < document.attributes.size()) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                if (documentAttribute instanceof TL_documentAttributeAudio) {
                    if (!documentAttribute.voice) {
                        String str2 = documentAttribute.title;
                        if (str2 == null || str2.length() == 0) {
                            str2 = FileLoader.getDocumentFileName(document);
                            if (TextUtils.isEmpty(str2) && z) {
                                str2 = LocaleController.getString(str, NUM);
                            }
                        }
                        return str2;
                    } else if (z) {
                        return LocaleController.formatDateAudio((long) this.messageOwner.date);
                    } else {
                        return null;
                    }
                } else if ((documentAttribute instanceof TL_documentAttributeVideo) && documentAttribute.round_message) {
                    return LocaleController.formatDateAudio((long) this.messageOwner.date);
                } else {
                    i++;
                }
            }
            String documentFileName = FileLoader.getDocumentFileName(document);
            if (!TextUtils.isEmpty(documentFileName)) {
                return documentFileName;
            }
        }
        return LocaleController.getString(str, NUM);
    }

    public int getDuration() {
        Document document = getDocument();
        int i = 0;
        if (document == null) {
            return 0;
        }
        int i2 = this.audioPlayerDuration;
        if (i2 > 0) {
            return i2;
        }
        while (i < document.attributes.size()) {
            DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
            if (documentAttribute instanceof TL_documentAttributeAudio) {
                return documentAttribute.duration;
            }
            if (documentAttribute instanceof TL_documentAttributeVideo) {
                return documentAttribute.duration;
            }
            i++;
        }
        return this.audioPlayerDuration;
    }

    public String getArtworkUrl(boolean z) {
        Document document = getDocument();
        if (document != null) {
            int size = document.attributes.size();
            int i = 0;
            while (i < size) {
                DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                if (!(documentAttribute instanceof TL_documentAttributeAudio)) {
                    i++;
                } else if (documentAttribute.voice) {
                    return null;
                } else {
                    CharSequence charSequence = documentAttribute.performer;
                    String str = documentAttribute.title;
                    if (!TextUtils.isEmpty(charSequence)) {
                        String str2 = charSequence;
                        int i2 = 0;
                        while (true) {
                            String[] strArr = excludeWords;
                            if (i2 >= strArr.length) {
                                break;
                            }
                            str2 = str2.replace(strArr[i2], " ");
                            i2++;
                        }
                        charSequence = str2;
                    }
                    if (TextUtils.isEmpty(charSequence) && TextUtils.isEmpty(str)) {
                        return null;
                    }
                    try {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("athumb://itunes.apple.com/search?term=");
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(charSequence);
                        stringBuilder2.append(" - ");
                        stringBuilder2.append(str);
                        stringBuilder.append(URLEncoder.encode(stringBuilder2.toString(), "UTF-8"));
                        stringBuilder.append("&entity=song&limit=4");
                        stringBuilder.append(z ? "&s=1" : "");
                        return stringBuilder.toString();
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

    /* JADX WARNING: Removed duplicated region for block: B:72:0x010f A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0042  */
    /* JADX WARNING: Missing block: B:18:0x003d, code skipped:
            if (r5.round_message != false) goto L_0x0026;
     */
    public java.lang.String getMusicAuthor(boolean r10) {
        /*
        r9 = this;
        r0 = r9.getDocument();
        r1 = NUM; // 0x7f0e0165 float:1.8875762E38 double:1.053162333E-314;
        r2 = "AudioUnknownArtist";
        if (r0 == 0) goto L_0x0113;
    L_0x000b:
        r3 = 0;
        r4 = 0;
    L_0x000d:
        r5 = r0.attributes;
        r5 = r5.size();
        if (r3 >= r5) goto L_0x0113;
    L_0x0015:
        r5 = r0.attributes;
        r5 = r5.get(r3);
        r5 = (org.telegram.tgnet.TLRPC.DocumentAttribute) r5;
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
        r7 = 1;
        if (r6 == 0) goto L_0x0037;
    L_0x0022:
        r4 = r5.voice;
        if (r4 == 0) goto L_0x0028;
    L_0x0026:
        r4 = 1;
        goto L_0x0040;
    L_0x0028:
        r0 = r5.performer;
        r3 = android.text.TextUtils.isEmpty(r0);
        if (r3 == 0) goto L_0x0036;
    L_0x0030:
        if (r10 == 0) goto L_0x0036;
    L_0x0032:
        r0 = org.telegram.messenger.LocaleController.getString(r2, r1);
    L_0x0036:
        return r0;
    L_0x0037:
        r6 = r5 instanceof org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
        if (r6 == 0) goto L_0x0040;
    L_0x003b:
        r5 = r5.round_message;
        if (r5 == 0) goto L_0x0040;
    L_0x003f:
        goto L_0x0026;
    L_0x0040:
        if (r4 == 0) goto L_0x010f;
    L_0x0042:
        r5 = 0;
        if (r10 != 0) goto L_0x0046;
    L_0x0045:
        return r5;
    L_0x0046:
        r6 = r9.isOutOwner();
        if (r6 != 0) goto L_0x0105;
    L_0x004c:
        r6 = r9.messageOwner;
        r6 = r6.fwd_from;
        if (r6 == 0) goto L_0x0062;
    L_0x0052:
        r6 = r6.from_id;
        r7 = r9.currentAccount;
        r7 = org.telegram.messenger.UserConfig.getInstance(r7);
        r7 = r7.getClientUserId();
        if (r6 != r7) goto L_0x0062;
    L_0x0060:
        goto L_0x0105;
    L_0x0062:
        r6 = r9.messageOwner;
        r6 = r6.fwd_from;
        if (r6 == 0) goto L_0x0082;
    L_0x0068:
        r6 = r6.channel_id;
        if (r6 == 0) goto L_0x0082;
    L_0x006c:
        r6 = r9.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r7 = r9.messageOwner;
        r7 = r7.fwd_from;
        r7 = r7.channel_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getChat(r7);
        goto L_0x00f9;
    L_0x0082:
        r6 = r9.messageOwner;
        r6 = r6.fwd_from;
        if (r6 == 0) goto L_0x00a4;
    L_0x0088:
        r6 = r6.from_id;
        if (r6 == 0) goto L_0x00a4;
    L_0x008c:
        r6 = r9.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r7 = r9.messageOwner;
        r7 = r7.fwd_from;
        r7 = r7.from_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getUser(r7);
    L_0x00a0:
        r8 = r6;
        r6 = r5;
        r5 = r8;
        goto L_0x00f9;
    L_0x00a4:
        r6 = r9.messageOwner;
        r6 = r6.fwd_from;
        if (r6 == 0) goto L_0x00af;
    L_0x00aa:
        r6 = r6.from_name;
        if (r6 == 0) goto L_0x00af;
    L_0x00ae:
        return r6;
    L_0x00af:
        r6 = r9.messageOwner;
        r7 = r6.from_id;
        if (r7 >= 0) goto L_0x00c9;
    L_0x00b5:
        r6 = r9.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r7 = r9.messageOwner;
        r7 = r7.from_id;
        r7 = -r7;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getChat(r7);
        goto L_0x00f9;
    L_0x00c9:
        if (r7 != 0) goto L_0x00e6;
    L_0x00cb:
        r6 = r6.to_id;
        r6 = r6.channel_id;
        if (r6 == 0) goto L_0x00e6;
    L_0x00d1:
        r6 = r9.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r7 = r9.messageOwner;
        r7 = r7.to_id;
        r7 = r7.channel_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getChat(r7);
        goto L_0x00f9;
    L_0x00e6:
        r6 = r9.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r7 = r9.messageOwner;
        r7 = r7.from_id;
        r7 = java.lang.Integer.valueOf(r7);
        r6 = r6.getUser(r7);
        goto L_0x00a0;
    L_0x00f9:
        if (r5 == 0) goto L_0x0100;
    L_0x00fb:
        r10 = org.telegram.messenger.UserObject.getUserName(r5);
        return r10;
    L_0x0100:
        if (r6 == 0) goto L_0x010f;
    L_0x0102:
        r10 = r6.title;
        return r10;
    L_0x0105:
        r10 = NUM; // 0x7f0e051d float:1.8877693E38 double:1.0531628034E-314;
        r0 = "FromYou";
        r10 = org.telegram.messenger.LocaleController.getString(r0, r10);
        return r10;
    L_0x010f:
        r3 = r3 + 1;
        goto L_0x000d;
    L_0x0113:
        r10 = org.telegram.messenger.LocaleController.getString(r2, r1);
        return r10;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getMusicAuthor(boolean):java.lang.String");
    }

    public InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    public boolean needDrawForwarded() {
        Message message = this.messageOwner;
        if ((message.flags & 4) != 0) {
            MessageFwdHeader messageFwdHeader = message.fwd_from;
            if (messageFwdHeader != null) {
                Peer peer = messageFwdHeader.saved_from_peer;
                if ((peer == null || peer.channel_id != messageFwdHeader.channel_id) && ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()) != getDialogId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isForwardedMessage(Message message) {
        return ((message.flags & 4) == 0 || message.fwd_from == null) ? false : true;
    }

    public boolean isReply() {
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject == null || !(messageObject.messageOwner instanceof TL_messageEmpty)) {
            Message message = this.messageOwner;
            if (!((message.reply_to_msg_id == 0 && message.reply_to_random_id == 0) || (this.messageOwner.flags & 8) == 0)) {
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

    public static boolean isMediaEmpty(Message message) {
        if (message != null) {
            MessageMedia messageMedia = message.media;
            if (!(messageMedia == null || (messageMedia instanceof TL_messageMediaEmpty) || (messageMedia instanceof TL_messageMediaWebPage))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMediaEmptyWebpage(Message message) {
        if (message != null) {
            MessageMedia messageMedia = message.media;
            if (!(messageMedia == null || (messageMedia instanceof TL_messageMediaEmpty))) {
                return false;
            }
        }
        return true;
    }

    public boolean canEditMessage(Chat chat) {
        return canEditMessage(this.currentAccount, this.messageOwner, chat, this.scheduled);
    }

    public boolean canEditMessageScheduleTime(Chat chat) {
        return canEditMessageScheduleTime(this.currentAccount, this.messageOwner, chat);
    }

    public boolean canForwardMessage() {
        return ((this.messageOwner instanceof TL_message_secret) || needDrawBluredPreview() || isLiveLocation() || this.type == 16) ? false : true;
    }

    public boolean canEditMedia() {
        boolean z = false;
        if (isSecretMedia()) {
            return false;
        }
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TL_messageMediaPhoto) {
            return true;
        }
        if (!(!(messageMedia instanceof TL_messageMediaDocument) || isVoice() || isSticker() || isAnimatedSticker() || isRoundVideo())) {
            z = true;
        }
        return z;
    }

    public boolean canEditMessageAnytime(Chat chat) {
        return canEditMessageAnytime(this.currentAccount, this.messageOwner, chat);
    }

    public static boolean canEditMessageAnytime(int i, Message message, Chat chat) {
        if (!(message == null || message.to_id == null)) {
            MessageMedia messageMedia = message.media;
            if (messageMedia == null || !(isRoundVideoDocument(messageMedia.document) || isStickerDocument(message.media.document) || isAnimatedStickerDocument(message.media.document, true))) {
                MessageAction messageAction = message.action;
                if ((messageAction == null || (messageAction instanceof TL_messageActionEmpty)) && !isForwardedMessage(message) && message.via_bot_id == 0 && message.id >= 0) {
                    int i2 = message.from_id;
                    if (i2 == message.to_id.user_id && i2 == UserConfig.getInstance(i).getClientUserId() && !isLiveLocationMessage(message)) {
                        return true;
                    }
                    if (chat == null && message.to_id.channel_id != 0) {
                        chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(message.to_id.channel_id));
                        if (chat == null) {
                            return false;
                        }
                    }
                    if (message.out && chat != null && chat.megagroup) {
                        if (!chat.creator) {
                            TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
                            if (tL_chatAdminRights == null || !tL_chatAdminRights.pin_messages) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean canEditMessageScheduleTime(int i, Message message, Chat chat) {
        if (chat == null && message.to_id.channel_id != 0) {
            chat = MessagesController.getInstance(i).getChat(Integer.valueOf(message.to_id.channel_id));
            if (chat == null) {
                return false;
            }
        }
        if (!ChatObject.isChannel(chat) || chat.megagroup || chat.creator) {
            return true;
        }
        TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
        if (tL_chatAdminRights == null || (!tL_chatAdminRights.edit_messages && !message.out)) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Missing block: B:10:0x001c, code skipped:
            return false;
     */
    /* JADX WARNING: Missing block: B:70:0x00c5, code skipped:
            if (r1.pin_messages != false) goto L_0x00c7;
     */
    /* JADX WARNING: Missing block: B:94:0x0118, code skipped:
            if (r4 != null) goto L_0x011b;
     */
    /* JADX WARNING: Missing block: B:112:0x013a, code skipped:
            if (r4.post_messages != false) goto L_0x013c;
     */
    /* JADX WARNING: Missing block: B:114:0x013e, code skipped:
            if (r5.post != false) goto L_0x0140;
     */
    public static boolean canEditMessage(int r4, org.telegram.tgnet.TLRPC.Message r5, org.telegram.tgnet.TLRPC.Chat r6, boolean r7) {
        /*
        r0 = 0;
        if (r7 == 0) goto L_0x0012;
    L_0x0003:
        r1 = r5.date;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
        r2 = r2.getCurrentTime();
        r2 = r2 + -60;
        if (r1 >= r2) goto L_0x0012;
    L_0x0011:
        return r0;
    L_0x0012:
        if (r6 == 0) goto L_0x001d;
    L_0x0014:
        r1 = r6.left;
        if (r1 != 0) goto L_0x001c;
    L_0x0018:
        r1 = r6.kicked;
        if (r1 == 0) goto L_0x001d;
    L_0x001c:
        return r0;
    L_0x001d:
        if (r5 == 0) goto L_0x0163;
    L_0x001f:
        r1 = r5.to_id;
        if (r1 == 0) goto L_0x0163;
    L_0x0023:
        r1 = r5.media;
        r2 = 1;
        if (r1 == 0) goto L_0x004a;
    L_0x0028:
        r1 = r1.document;
        r1 = isRoundVideoDocument(r1);
        if (r1 != 0) goto L_0x0163;
    L_0x0030:
        r1 = r5.media;
        r1 = r1.document;
        r1 = isStickerDocument(r1);
        if (r1 != 0) goto L_0x0163;
    L_0x003a:
        r1 = r5.media;
        r1 = r1.document;
        r1 = isAnimatedStickerDocument(r1, r2);
        if (r1 != 0) goto L_0x0163;
    L_0x0044:
        r1 = isLocationMessage(r5);
        if (r1 != 0) goto L_0x0163;
    L_0x004a:
        r1 = r5.action;
        if (r1 == 0) goto L_0x0052;
    L_0x004e:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r1 == 0) goto L_0x0163;
    L_0x0052:
        r1 = isForwardedMessage(r5);
        if (r1 != 0) goto L_0x0163;
    L_0x0058:
        r1 = r5.via_bot_id;
        if (r1 != 0) goto L_0x0163;
    L_0x005c:
        r1 = r5.id;
        if (r1 >= 0) goto L_0x0062;
    L_0x0060:
        goto L_0x0163;
    L_0x0062:
        r1 = r5.from_id;
        r3 = r5.to_id;
        r3 = r3.user_id;
        if (r1 != r3) goto L_0x0081;
    L_0x006a:
        r3 = org.telegram.messenger.UserConfig.getInstance(r4);
        r3 = r3.getClientUserId();
        if (r1 != r3) goto L_0x0081;
    L_0x0074:
        r1 = isLiveLocationMessage(r5);
        if (r1 != 0) goto L_0x0081;
    L_0x007a:
        r1 = r5.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r1 != 0) goto L_0x0081;
    L_0x0080:
        return r2;
    L_0x0081:
        if (r6 != 0) goto L_0x009c;
    L_0x0083:
        r1 = r5.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x009c;
    L_0x0089:
        r6 = org.telegram.messenger.MessagesController.getInstance(r4);
        r1 = r5.to_id;
        r1 = r1.channel_id;
        r1 = java.lang.Integer.valueOf(r1);
        r6 = r6.getChat(r1);
        if (r6 != 0) goto L_0x009c;
    L_0x009b:
        return r0;
    L_0x009c:
        r1 = r5.media;
        if (r1 == 0) goto L_0x00b1;
    L_0x00a0:
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
        if (r3 != 0) goto L_0x00b1;
    L_0x00a4:
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r3 != 0) goto L_0x00b1;
    L_0x00a8:
        r3 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r3 != 0) goto L_0x00b1;
    L_0x00ac:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r1 != 0) goto L_0x00b1;
    L_0x00b0:
        return r0;
    L_0x00b1:
        r1 = r5.out;
        if (r1 == 0) goto L_0x00c8;
    L_0x00b5:
        if (r6 == 0) goto L_0x00c8;
    L_0x00b7:
        r1 = r6.megagroup;
        if (r1 == 0) goto L_0x00c8;
    L_0x00bb:
        r1 = r6.creator;
        if (r1 != 0) goto L_0x00c7;
    L_0x00bf:
        r1 = r6.admin_rights;
        if (r1 == 0) goto L_0x00c8;
    L_0x00c3:
        r1 = r1.pin_messages;
        if (r1 == 0) goto L_0x00c8;
    L_0x00c7:
        return r2;
    L_0x00c8:
        if (r7 != 0) goto L_0x00e2;
    L_0x00ca:
        r7 = r5.date;
        r1 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
        r1 = r1.getCurrentTime();
        r7 = r7 - r1;
        r7 = java.lang.Math.abs(r7);
        r1 = org.telegram.messenger.MessagesController.getInstance(r4);
        r1 = r1.maxEditTime;
        if (r7 <= r1) goto L_0x00e2;
    L_0x00e1:
        return r0;
    L_0x00e2:
        r7 = r5.to_id;
        r7 = r7.channel_id;
        if (r7 != 0) goto L_0x011c;
    L_0x00e8:
        r6 = r5.out;
        if (r6 != 0) goto L_0x00f8;
    L_0x00ec:
        r6 = r5.from_id;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.getClientUserId();
        if (r6 != r4) goto L_0x011b;
    L_0x00f8:
        r4 = r5.media;
        r6 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r6 != 0) goto L_0x011a;
    L_0x00fe:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r4 == 0) goto L_0x010e;
    L_0x0102:
        r4 = isStickerMessage(r5);
        if (r4 != 0) goto L_0x010e;
    L_0x0108:
        r4 = isAnimatedStickerMessage(r5);
        if (r4 == 0) goto L_0x011a;
    L_0x010e:
        r4 = r5.media;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
        if (r5 != 0) goto L_0x011a;
    L_0x0114:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r5 != 0) goto L_0x011a;
    L_0x0118:
        if (r4 != 0) goto L_0x011b;
    L_0x011a:
        r0 = 1;
    L_0x011b:
        return r0;
    L_0x011c:
        r4 = r6.megagroup;
        if (r4 == 0) goto L_0x0124;
    L_0x0120:
        r4 = r5.out;
        if (r4 != 0) goto L_0x0140;
    L_0x0124:
        r4 = r6.megagroup;
        if (r4 != 0) goto L_0x0163;
    L_0x0128:
        r4 = r6.creator;
        if (r4 != 0) goto L_0x013c;
    L_0x012c:
        r4 = r6.admin_rights;
        if (r4 == 0) goto L_0x0163;
    L_0x0130:
        r6 = r4.edit_messages;
        if (r6 != 0) goto L_0x013c;
    L_0x0134:
        r6 = r5.out;
        if (r6 == 0) goto L_0x0163;
    L_0x0138:
        r4 = r4.post_messages;
        if (r4 == 0) goto L_0x0163;
    L_0x013c:
        r4 = r5.post;
        if (r4 == 0) goto L_0x0163;
    L_0x0140:
        r4 = r5.media;
        r6 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r6 != 0) goto L_0x0162;
    L_0x0146:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r4 == 0) goto L_0x0156;
    L_0x014a:
        r4 = isStickerMessage(r5);
        if (r4 != 0) goto L_0x0156;
    L_0x0150:
        r4 = isAnimatedStickerMessage(r5);
        if (r4 == 0) goto L_0x0162;
    L_0x0156:
        r4 = r5.media;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
        if (r5 != 0) goto L_0x0162;
    L_0x015c:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r5 != 0) goto L_0x0162;
    L_0x0160:
        if (r4 != 0) goto L_0x0163;
    L_0x0162:
        return r2;
    L_0x0163:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.canEditMessage(int, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLRPC$Chat, boolean):boolean");
    }

    public boolean canDeleteMessage(boolean z, Chat chat) {
        return this.eventId == 0 && canDeleteMessage(this.currentAccount, z, this.messageOwner, chat);
    }

    /* JADX WARNING: Missing block: B:19:0x0039, code skipped:
            if (r4.out != false) goto L_0x003b;
     */
    /* JADX WARNING: Missing block: B:36:0x0059, code skipped:
            if (r2.post_messages != false) goto L_0x0067;
     */
    /* JADX WARNING: Missing block: B:42:0x0065, code skipped:
            if (r4.from_id > 0) goto L_0x0067;
     */
    public static boolean canDeleteMessage(int r2, boolean r3, org.telegram.tgnet.TLRPC.Message r4, org.telegram.tgnet.TLRPC.Chat r5) {
        /*
        r0 = r4.id;
        r1 = 1;
        if (r0 >= 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        if (r5 != 0) goto L_0x001e;
    L_0x0008:
        r0 = r4.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x001e;
    L_0x000e:
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r5 = r4.to_id;
        r5 = r5.channel_id;
        r5 = java.lang.Integer.valueOf(r5);
        r5 = r2.getChat(r5);
    L_0x001e:
        r2 = org.telegram.messenger.ChatObject.isChannel(r5);
        r0 = 0;
        if (r2 == 0) goto L_0x0069;
    L_0x0025:
        if (r3 == 0) goto L_0x003d;
    L_0x0027:
        r2 = r5.megagroup;
        if (r2 != 0) goto L_0x003d;
    L_0x002b:
        r2 = r5.creator;
        if (r2 != 0) goto L_0x003b;
    L_0x002f:
        r2 = r5.admin_rights;
        if (r2 == 0) goto L_0x003c;
    L_0x0033:
        r2 = r2.delete_messages;
        if (r2 != 0) goto L_0x003b;
    L_0x0037:
        r2 = r4.out;
        if (r2 == 0) goto L_0x003c;
    L_0x003b:
        r0 = 1;
    L_0x003c:
        return r0;
    L_0x003d:
        if (r3 != 0) goto L_0x0067;
    L_0x003f:
        r2 = r4.id;
        if (r2 == r1) goto L_0x0068;
    L_0x0043:
        r2 = r5.creator;
        if (r2 != 0) goto L_0x0067;
    L_0x0047:
        r2 = r5.admin_rights;
        if (r2 == 0) goto L_0x005b;
    L_0x004b:
        r3 = r2.delete_messages;
        if (r3 != 0) goto L_0x0067;
    L_0x004f:
        r3 = r4.out;
        if (r3 == 0) goto L_0x005b;
    L_0x0053:
        r3 = r5.megagroup;
        if (r3 != 0) goto L_0x0067;
    L_0x0057:
        r2 = r2.post_messages;
        if (r2 != 0) goto L_0x0067;
    L_0x005b:
        r2 = r5.megagroup;
        if (r2 == 0) goto L_0x0068;
    L_0x005f:
        r2 = r4.out;
        if (r2 == 0) goto L_0x0068;
    L_0x0063:
        r2 = r4.from_id;
        if (r2 <= 0) goto L_0x0068;
    L_0x0067:
        r0 = 1;
    L_0x0068:
        return r0;
    L_0x0069:
        if (r3 != 0) goto L_0x0077;
    L_0x006b:
        r2 = isOut(r4);
        if (r2 != 0) goto L_0x0077;
    L_0x0071:
        r2 = org.telegram.messenger.ChatObject.isChannel(r5);
        if (r2 != 0) goto L_0x0078;
    L_0x0077:
        r0 = 1;
    L_0x0078:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.canDeleteMessage(int, boolean, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public String getForwardedName() {
        MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
        if (messageFwdHeader != null) {
            if (messageFwdHeader.channel_id != 0) {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                if (chat != null) {
                    return chat.title;
                }
            } else if (messageFwdHeader.from_id != 0) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                if (user != null) {
                    return UserObject.getUserName(user);
                }
            } else {
                String str = messageFwdHeader.from_name;
                if (str != null) {
                    return str;
                }
            }
        }
        return null;
    }

    public int getFromId() {
        int i;
        MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
        if (messageFwdHeader != null) {
            Peer peer = messageFwdHeader.saved_from_peer;
            if (peer != null) {
                int i2 = peer.user_id;
                int i3;
                if (i2 != 0) {
                    i3 = messageFwdHeader.from_id;
                    return i3 != 0 ? i3 : i2;
                } else if (peer.channel_id != 0) {
                    if (isSavedFromMegagroup()) {
                        i3 = this.messageOwner.fwd_from.from_id;
                        if (i3 != 0) {
                            return i3;
                        }
                    }
                    messageFwdHeader = this.messageOwner.fwd_from;
                    i = messageFwdHeader.channel_id;
                    if (i != 0) {
                        return -i;
                    }
                    return -messageFwdHeader.saved_from_peer.channel_id;
                } else {
                    i = peer.chat_id;
                    if (i != 0) {
                        i2 = messageFwdHeader.from_id;
                        if (i2 != 0) {
                            return i2;
                        }
                        i3 = messageFwdHeader.channel_id;
                        return i3 != 0 ? -i3 : -i;
                    }
                    return 0;
                }
            }
        }
        Message message = this.messageOwner;
        i = message.from_id;
        if (i != 0) {
            return i;
        }
        if (message.post) {
            return message.to_id.channel_id;
        }
        return 0;
    }

    public boolean isWallpaper() {
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            WebPage webPage = messageMedia.webpage;
            if (webPage != null) {
                if ("telegram_background".equals(webPage.type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isTheme() {
        MessageMedia messageMedia = this.messageOwner.media;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            WebPage webPage = messageMedia.webpage;
            if (webPage != null) {
                if ("telegram_theme".equals(webPage.type)) {
                    return true;
                }
            }
        }
        return false;
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
        this.attachPathExists = false;
        this.mediaExists = false;
        int i = this.type;
        String str = ".enc";
        File pathToMessage;
        StringBuilder stringBuilder;
        if (i == 1) {
            if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
                pathToMessage = FileLoader.getPathToMessage(this.messageOwner);
                if (needDrawBluredPreview()) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(pathToMessage.getAbsolutePath());
                    stringBuilder.append(str);
                    this.mediaExists = new File(stringBuilder.toString()).exists();
                }
                if (!this.mediaExists) {
                    this.mediaExists = pathToMessage.exists();
                }
            }
        } else if (i == 8 || i == 3 || i == 9 || i == 2 || i == 14 || i == 5) {
            String str2 = this.messageOwner.attachPath;
            if (str2 != null && str2.length() > 0) {
                this.attachPathExists = new File(this.messageOwner.attachPath).exists();
            }
            if (!this.attachPathExists) {
                pathToMessage = FileLoader.getPathToMessage(this.messageOwner);
                if (this.type == 3 && needDrawBluredPreview()) {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(pathToMessage.getAbsolutePath());
                    stringBuilder.append(str);
                    this.mediaExists = new File(stringBuilder.toString()).exists();
                }
                if (!this.mediaExists) {
                    this.mediaExists = pathToMessage.exists();
                }
            }
        } else {
            Document document = getDocument();
            if (document != null) {
                if (isWallpaper()) {
                    this.mediaExists = FileLoader.getPathToAttach(document, true).exists();
                } else {
                    this.mediaExists = FileLoader.getPathToAttach(document).exists();
                }
            } else if (this.type == 0) {
                PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                if (!(closestPhotoSizeWithSize == null || closestPhotoSizeWithSize == null)) {
                    this.mediaExists = FileLoader.getPathToAttach(closestPhotoSizeWithSize, true).exists();
                }
            }
        }
    }

    public boolean equals(MessageObject messageObject) {
        return getId() == messageObject.getId() && getDialogId() == messageObject.getDialogId();
    }
}
