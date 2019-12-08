package org.telegram.messenger;

import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.util.Linkify;
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
import java.util.regex.Matcher;
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
import org.telegram.tgnet.TLRPC.KeyboardButton;
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
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
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
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;
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
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
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
import org.telegram.tgnet.TLRPC.TL_stickerPack;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanNoUnderline;
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
    private int emojiOnlyCount;
    public long eventId;
    public boolean forceUpdate;
    private float generatedWithDensity;
    private int generatedWithMinSize;
    public float gifState;
    public boolean hadAnimationNotReadyLoading;
    public boolean hasRtl;
    public boolean isDateObject;
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
        /* JADX WARNING: Missing block: B:179:0x06d9, code skipped:
            if (r6[2] > r6[3]) goto L_0x06dd;
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
            if (r2 >= r11) goto L_0x00c2;
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
            if (r9 <= 0) goto L_0x0098;
        L_0x0092:
            r7 = "w";
            r0.append(r7);
            goto L_0x00aa;
        L_0x0098:
            r9 = NUM; // 0x3f4ccccd float:0.8 double:5.246966156E-315;
            r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
            if (r7 >= 0) goto L_0x00a5;
        L_0x009f:
            r7 = "n";
            r0.append(r7);
            goto L_0x00aa;
        L_0x00a5:
            r7 = "q";
            r0.append(r7);
        L_0x00aa:
            r7 = r8.aspectRatio;
            r4 = r4 + r7;
            r9 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
            r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
            if (r7 <= 0) goto L_0x00b4;
        L_0x00b3:
            r5 = 1;
        L_0x00b4:
            r7 = r10.positions;
            r7.put(r6, r8);
            r6 = r10.posArray;
            r6.add(r8);
            r2 = r2 + 1;
            goto L_0x0027;
        L_0x00c2:
            if (r3 == 0) goto L_0x00d0;
        L_0x00c4:
            r2 = r10.maxSizeWidth;
            r2 = r2 + -50;
            r10.maxSizeWidth = r2;
            r2 = r10.firstSpanAdditionalSize;
            r2 = r2 + 50;
            r10.firstSpanAdditionalSize = r2;
        L_0x00d0:
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
            if (r5 != 0) goto L_0x0530;
        L_0x0117:
            if (r11 == r13) goto L_0x011d;
        L_0x0119:
            if (r11 == r1) goto L_0x011d;
        L_0x011b:
            if (r11 != r4) goto L_0x0530;
        L_0x011d:
            r5 = NUM; // 0x3ecccccd float:0.4 double:5.205520926E-315;
            r4 = NUM; // 0x43cb8000 float:407.0 double:5.6195523E-315;
            if (r11 != r13) goto L_0x0252;
        L_0x0125:
            r1 = r10.posArray;
            r1 = r1.get(r14);
            r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1;
            r2 = r10.posArray;
            r2 = r2.get(r12);
            r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2;
            r0 = r0.toString();
            r3 = "ww";
            r7 = r0.equals(r3);
            if (r7 == 0) goto L_0x01a1;
        L_0x0141:
            r7 = (double) r8;
            r18 = NUM; // 0x3ffNUM float:2.720083E23 double:1.4;
            r26 = r15;
            r14 = (double) r6;
            java.lang.Double.isNaN(r14);
            r14 = r14 * r18;
            r6 = (r7 > r14 ? 1 : (r7 == r14 ? 0 : -1));
            if (r6 <= 0) goto L_0x01a3;
        L_0x0153:
            r6 = r1.aspectRatio;
            r7 = r2.aspectRatio;
            r8 = r6 - r7;
            r14 = (double) r8;
            r18 = NUM; // 0x3fCLASSNAMEa float:-1.5881868E-23 double:0.2;
            r8 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1));
            if (r8 >= 0) goto L_0x01a3;
        L_0x0163:
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
            goto L_0x024c;
        L_0x01a1:
            r26 = r15;
        L_0x01a3:
            r3 = r0.equals(r3);
            if (r3 != 0) goto L_0x0211;
        L_0x01a9:
            r3 = "qq";
            r0 = r0.equals(r3);
            if (r0 == 0) goto L_0x01b2;
        L_0x01b1:
            goto L_0x0211;
        L_0x01b2:
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
            if (r3 >= r9) goto L_0x01d7;
        L_0x01d3:
            r3 = r9 - r3;
            r0 = r0 - r3;
            r3 = r9;
        L_0x01d7:
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
            goto L_0x024b;
        L_0x0211:
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
        L_0x024b:
            r0 = 1;
        L_0x024c:
            r12 = r0;
        L_0x024d:
            r17 = r11;
            r8 = 0;
            goto L_0x077d;
        L_0x0252:
            r26 = r15;
            r6 = NUM; // 0x44064f5d float:537.24005 double:5.638594444E-315;
            if (r11 != r1) goto L_0x038c;
        L_0x0259:
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
            if (r0 != r3) goto L_0x0321;
        L_0x027a:
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
            if (r26 == 0) goto L_0x0318;
        L_0x0314:
            r6 = r6 - r3;
            r1.spanSize = r6;
            goto L_0x031d;
        L_0x0318:
            r6 = r6 - r2;
            r5.spanSize = r6;
            r8.leftSpanOffset = r2;
        L_0x031d:
            r10.hasSibling = r12;
            goto L_0x024d;
        L_0x0321:
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
            if (r2 >= 0) goto L_0x036c;
        L_0x036b:
            r0 = r7;
        L_0x036c:
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
            goto L_0x024d;
        L_0x038c:
            r4 = 4;
            if (r11 != r4) goto L_0x052a;
        L_0x038f:
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
            if (r0 != r8) goto L_0x0473;
        L_0x03bb:
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
            if (r4 >= r6) goto L_0x0433;
        L_0x0425:
            r6 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r6 = r6 - r4;
            r4 = org.telegram.messenger.AndroidUtilities.dp(r5);
            r5 = r6 / 2;
            r3 = r3 - r5;
            r6 = r6 - r5;
            r2 = r2 - r6;
        L_0x0433:
            r23 = r3;
            r3 = r2;
            r2 = NUM; // 0x444b8000 float:814.0 double:5.66099753E-315;
            r0 = r2 - r0;
            r0 = java.lang.Math.min(r0, r1);
            r0 = r0 / r2;
            r1 = (r0 > r7 ? 1 : (r0 == r7 ? 0 : -1));
            if (r1 >= 0) goto L_0x0445;
        L_0x0444:
            r0 = r7;
        L_0x0445:
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
            goto L_0x024d;
        L_0x0473:
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
            if (r26 == 0) goto L_0x0511;
        L_0x050d:
            r5 = r5 - r0;
            r4.spanSize = r5;
            goto L_0x0518;
        L_0x0511:
            r5 = r5 - r2;
            r14.spanSize = r5;
            r15.leftSpanOffset = r2;
            r13.leftSpanOffset = r2;
        L_0x0518:
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
            goto L_0x024d;
        L_0x052a:
            r17 = r11;
            r8 = 0;
            r12 = 0;
            goto L_0x077d;
        L_0x0530:
            r26 = r15;
            r0 = r10.posArray;
            r0 = r0.size();
            r12 = new float[r0];
            r0 = 0;
        L_0x053b:
            if (r0 >= r11) goto L_0x057e;
        L_0x053d:
            r2 = NUM; // 0x3f8ccccd float:1.1 double:5.26768877E-315;
            r2 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1));
            if (r2 <= 0) goto L_0x0557;
        L_0x0544:
            r2 = r10.posArray;
            r2 = r2.get(r0);
            r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2;
            r2 = r2.aspectRatio;
            r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r2 = java.lang.Math.max(r3, r2);
            r12[r0] = r2;
            goto L_0x0569;
        L_0x0557:
            r3 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
            r2 = r10.posArray;
            r2 = r2.get(r0);
            r2 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r2;
            r2 = r2.aspectRatio;
            r2 = java.lang.Math.min(r3, r2);
            r12[r0] = r2;
        L_0x0569:
            r2 = NUM; // 0x3f2aaae3 float:0.66667 double:5.23591437E-315;
            r5 = NUM; // 0x3fd9999a float:1.7 double:5.29255591E-315;
            r6 = r12[r0];
            r5 = java.lang.Math.min(r5, r6);
            r2 = java.lang.Math.max(r2, r5);
            r12[r0] = r2;
            r0 = r0 + 1;
            goto L_0x053b;
        L_0x057e:
            r13 = new java.util.ArrayList;
            r13.<init>();
            r6 = 1;
        L_0x0584:
            r0 = r12.length;
            if (r6 >= r0) goto L_0x05ba;
        L_0x0587:
            r0 = r12.length;
            r3 = r0 - r6;
            if (r6 > r1) goto L_0x05ae;
        L_0x058c:
            if (r3 <= r1) goto L_0x058f;
        L_0x058e:
            goto L_0x05ae;
        L_0x058f:
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
            goto L_0x05b3;
        L_0x05ae:
            r17 = r11;
            r11 = 3;
            r18 = 4;
        L_0x05b3:
            r6 = r6 + 1;
            r11 = r17;
            r1 = 3;
            r4 = 4;
            goto L_0x0584;
        L_0x05ba:
            r17 = r11;
            r11 = 3;
            r18 = 4;
            r14 = 1;
        L_0x05c0:
            r0 = r12.length;
            r1 = 1;
            r0 = r0 - r1;
            if (r14 >= r0) goto L_0x0612;
        L_0x05c5:
            r15 = 1;
        L_0x05c6:
            r0 = r12.length;
            r0 = r0 - r14;
            if (r15 >= r0) goto L_0x060c;
        L_0x05ca:
            r0 = r12.length;
            r0 = r0 - r14;
            r4 = r0 - r15;
            if (r14 > r11) goto L_0x0604;
        L_0x05d0:
            r0 = NUM; // 0x3var_a float:0.85 double:5.25111068E-315;
            r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
            if (r0 >= 0) goto L_0x05d9;
        L_0x05d7:
            r0 = 4;
            goto L_0x05da;
        L_0x05d9:
            r0 = 3;
        L_0x05da:
            if (r15 > r0) goto L_0x0604;
        L_0x05dc:
            if (r4 <= r11) goto L_0x05df;
        L_0x05de:
            goto L_0x0604;
        L_0x05df:
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
            goto L_0x0606;
        L_0x0604:
            r27 = r7;
        L_0x0606:
            r15 = r15 + 1;
            r7 = r27;
            r11 = 3;
            goto L_0x05c6;
        L_0x060c:
            r27 = r7;
            r14 = r14 + 1;
            r11 = 3;
            goto L_0x05c0;
        L_0x0612:
            r27 = r7;
            r11 = 1;
        L_0x0615:
            r0 = r12.length;
            r1 = 2;
            r0 = r0 - r1;
            if (r11 >= r0) goto L_0x067b;
        L_0x061a:
            r14 = 1;
        L_0x061b:
            r0 = r12.length;
            r0 = r0 - r11;
            if (r14 >= r0) goto L_0x0674;
        L_0x061f:
            r15 = 1;
        L_0x0620:
            r0 = r12.length;
            r0 = r0 - r11;
            r0 = r0 - r14;
            if (r15 >= r0) goto L_0x066d;
        L_0x0625:
            r0 = r12.length;
            r0 = r0 - r11;
            r0 = r0 - r14;
            r5 = r0 - r15;
            r0 = 3;
            if (r11 > r0) goto L_0x0662;
        L_0x062d:
            if (r14 > r0) goto L_0x0662;
        L_0x062f:
            if (r15 > r0) goto L_0x0662;
        L_0x0631:
            if (r5 <= r0) goto L_0x0634;
        L_0x0633:
            goto L_0x0662;
        L_0x0634:
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
            goto L_0x0666;
        L_0x0662:
            r28 = r9;
            r22 = r12;
        L_0x0666:
            r15 = r15 + 1;
            r12 = r22;
            r9 = r28;
            goto L_0x0620;
        L_0x066d:
            r28 = r9;
            r22 = r12;
            r14 = r14 + 1;
            goto L_0x061b;
        L_0x0674:
            r28 = r9;
            r22 = r12;
            r11 = r11 + 1;
            goto L_0x0615;
        L_0x067b:
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
        L_0x068b:
            r4 = r13.size();
            if (r0 >= r4) goto L_0x06fb;
        L_0x0691:
            r4 = r13.get(r0);
            r4 = (org.telegram.messenger.MessageObject.GroupedMessages.MessageGroupedLayoutAttempt) r4;
            r5 = 0;
            r6 = NUM; // 0x7f7fffff float:3.4028235E38 double:1.056853372E-314;
            r5 = 0;
            r6 = 0;
            r7 = NUM; // 0x7f7fffff float:3.4028235E38 double:1.056853372E-314;
        L_0x06a0:
            r8 = r4.heights;
            r9 = r8.length;
            if (r5 >= r9) goto L_0x06b3;
        L_0x06a5:
            r9 = r8[r5];
            r6 = r6 + r9;
            r9 = r8[r5];
            r9 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1));
            if (r9 >= 0) goto L_0x06b0;
        L_0x06ae:
            r7 = r8[r5];
        L_0x06b0:
            r5 = r5 + 1;
            goto L_0x06a0;
        L_0x06b3:
            r6 = r6 - r2;
            r5 = java.lang.Math.abs(r6);
            r6 = r4.lineCounts;
            r8 = r6.length;
            r9 = 1;
            if (r8 <= r9) goto L_0x06e0;
        L_0x06be:
            r8 = 0;
            r11 = r6[r8];
            r12 = r6[r9];
            if (r11 > r12) goto L_0x06dc;
        L_0x06c5:
            r11 = r6.length;
            r12 = 2;
            if (r11 <= r12) goto L_0x06cf;
        L_0x06c9:
            r11 = r6[r9];
            r6 = r6[r12];
            if (r11 > r6) goto L_0x06dc;
        L_0x06cf:
            r6 = r4.lineCounts;
            r9 = r6.length;
            r11 = 3;
            if (r9 <= r11) goto L_0x06e2;
        L_0x06d5:
            r9 = r6[r12];
            r6 = r6[r11];
            if (r9 <= r6) goto L_0x06e2;
        L_0x06db:
            goto L_0x06dd;
        L_0x06dc:
            r11 = 3;
        L_0x06dd:
            r5 = r5 * r16;
            goto L_0x06e2;
        L_0x06e0:
            r8 = 0;
            r11 = 3;
        L_0x06e2:
            r6 = r5;
            r5 = r28;
            r9 = (float) r5;
            r7 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1));
            if (r7 >= 0) goto L_0x06ee;
        L_0x06ea:
            r7 = NUM; // 0x3fCLASSNAME float:1.5 double:5.28426686E-315;
            r6 = r6 * r7;
        L_0x06ee:
            if (r1 == 0) goto L_0x06f4;
        L_0x06f0:
            r7 = (r6 > r3 ? 1 : (r6 == r3 ? 0 : -1));
            if (r7 >= 0) goto L_0x06f6;
        L_0x06f4:
            r1 = r4;
            r3 = r6;
        L_0x06f6:
            r0 = r0 + 1;
            r28 = r5;
            goto L_0x068b;
        L_0x06fb:
            r8 = 0;
            if (r1 != 0) goto L_0x06ff;
        L_0x06fe:
            return;
        L_0x06ff:
            r0 = 0;
            r2 = 0;
            r12 = 0;
        L_0x0702:
            r3 = r1.lineCounts;
            r4 = r3.length;
            if (r0 >= r4) goto L_0x077d;
        L_0x0707:
            r3 = r3[r0];
            r4 = r1.heights;
            r4 = r4[r0];
            r5 = r10.maxSizeWidth;
            r6 = 0;
            r7 = r3 + -1;
            r12 = java.lang.Math.max(r12, r7);
            r9 = r2;
            r2 = 0;
        L_0x0718:
            if (r2 >= r3) goto L_0x076a;
        L_0x071a:
            r11 = r22[r9];
            r11 = r11 * r4;
            r11 = (int) r11;
            r5 = r5 - r11;
            r13 = r10.posArray;
            r13 = r13.get(r9);
            r28 = r13;
            r28 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r28;
            if (r0 != 0) goto L_0x072e;
        L_0x072c:
            r13 = 4;
            goto L_0x072f;
        L_0x072e:
            r13 = 0;
        L_0x072f:
            r14 = r1.lineCounts;
            r14 = r14.length;
            r15 = 1;
            r14 = r14 - r15;
            if (r0 != r14) goto L_0x0738;
        L_0x0736:
            r13 = r13 | 8;
        L_0x0738:
            if (r2 != 0) goto L_0x0740;
        L_0x073a:
            r13 = r13 | 1;
            if (r26 == 0) goto L_0x0740;
        L_0x073e:
            r6 = r28;
        L_0x0740:
            if (r2 != r7) goto L_0x074b;
        L_0x0742:
            r13 = r13 | 2;
            if (r26 != 0) goto L_0x074b;
        L_0x0746:
            r35 = r13;
            r6 = r28;
            goto L_0x074d;
        L_0x074b:
            r35 = r13;
        L_0x074d:
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
            goto L_0x0718;
        L_0x076a:
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
            goto L_0x0702;
        L_0x077d:
            r0 = r17;
        L_0x077f:
            if (r8 >= r0) goto L_0x07fd;
        L_0x0781:
            r1 = r10.posArray;
            r1 = r1.get(r8);
            r1 = (org.telegram.messenger.MessageObject.GroupedMessagePosition) r1;
            if (r26 == 0) goto L_0x07a1;
        L_0x078b:
            r2 = r1.minX;
            if (r2 != 0) goto L_0x0796;
        L_0x078f:
            r2 = r1.spanSize;
            r3 = r10.firstSpanAdditionalSize;
            r2 = r2 + r3;
            r1.spanSize = r2;
        L_0x0796:
            r2 = r1.flags;
            r3 = 2;
            r2 = r2 & r3;
            if (r2 == 0) goto L_0x079f;
        L_0x079c:
            r2 = 1;
            r1.edge = r2;
        L_0x079f:
            r3 = 1;
            goto L_0x07ba;
        L_0x07a1:
            r3 = 2;
            r2 = r1.maxX;
            if (r2 == r12) goto L_0x07ab;
        L_0x07a6:
            r2 = r1.flags;
            r2 = r2 & r3;
            if (r2 == 0) goto L_0x07b2;
        L_0x07ab:
            r2 = r1.spanSize;
            r3 = r10.firstSpanAdditionalSize;
            r2 = r2 + r3;
            r1.spanSize = r2;
        L_0x07b2:
            r2 = r1.flags;
            r3 = 1;
            r2 = r2 & r3;
            if (r2 == 0) goto L_0x07ba;
        L_0x07b8:
            r1.edge = r3;
        L_0x07ba:
            r2 = r10.messages;
            r2 = r2.get(r8);
            r2 = (org.telegram.messenger.MessageObject) r2;
            if (r26 != 0) goto L_0x07f9;
        L_0x07c4:
            r2 = r2.needDrawAvatarInternal();
            if (r2 == 0) goto L_0x07f9;
        L_0x07ca:
            r2 = r1.edge;
            if (r2 == 0) goto L_0x07df;
        L_0x07ce:
            r2 = r1.spanSize;
            r4 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            if (r2 == r4) goto L_0x07d8;
        L_0x07d4:
            r2 = r2 + 108;
            r1.spanSize = r2;
        L_0x07d8:
            r2 = r1.pw;
            r2 = r2 + 108;
            r1.pw = r2;
            goto L_0x07f9;
        L_0x07df:
            r2 = r1.flags;
            r4 = 2;
            r2 = r2 & r4;
            if (r2 == 0) goto L_0x07fa;
        L_0x07e5:
            r2 = r1.spanSize;
            r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
            if (r2 == r5) goto L_0x07f0;
        L_0x07eb:
            r2 = r2 + -108;
            r1.spanSize = r2;
            goto L_0x07fa;
        L_0x07f0:
            r2 = r1.leftSpanOffset;
            if (r2 == 0) goto L_0x07fa;
        L_0x07f4:
            r2 = r2 + 108;
            r1.leftSpanOffset = r2;
            goto L_0x07fa;
        L_0x07f9:
            r4 = 2;
        L_0x07fa:
            r8 = r8 + 1;
            goto L_0x077f;
        L_0x07fd:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject$GroupedMessages.calculate():void");
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
        this(i, message, null, null, null, null, z, 0);
    }

    public MessageObject(int i, Message message, AbstractMap<Integer, User> abstractMap, AbstractMap<Integer, Chat> abstractMap2, boolean z) {
        this(i, message, (AbstractMap) abstractMap, (AbstractMap) abstractMap2, z, 0);
    }

    public MessageObject(int i, Message message, SparseArray<User> sparseArray, SparseArray<Chat> sparseArray2, boolean z) {
        this(i, message, null, null, (SparseArray) sparseArray, (SparseArray) sparseArray2, z, 0);
    }

    public MessageObject(int i, Message message, AbstractMap<Integer, User> abstractMap, AbstractMap<Integer, Chat> abstractMap2, boolean z, long j) {
        this(i, message, (AbstractMap) abstractMap, (AbstractMap) abstractMap2, null, null, z, j);
    }

    public MessageObject(int i, Message message, AbstractMap<Integer, User> abstractMap, AbstractMap<Integer, Chat> abstractMap2, SparseArray<User> sparseArray, SparseArray<Chat> sparseArray2, boolean z, long j) {
        int i2;
        User user;
        AbstractMap abstractMap3;
        SparseArray sparseArray3;
        Message message2 = message;
        AbstractMap<Integer, User> abstractMap4 = abstractMap;
        SparseArray<User> sparseArray4 = sparseArray;
        boolean z2 = z;
        this.type = 1000;
        Theme.createChatResources(null, true);
        this.currentAccount = i;
        this.messageOwner = message2;
        this.eventId = j;
        Message message3 = message2.replyMessage;
        if (message3 != null) {
            MessageObject messageObject = r2;
            i2 = 1;
            MessageObject messageObject2 = new MessageObject(this.currentAccount, message3, (AbstractMap) abstractMap, (AbstractMap) abstractMap2, (SparseArray) sparseArray, (SparseArray) sparseArray2, false, j);
            this.replyMessageObject = messageObject;
        } else {
            i2 = 1;
        }
        int i3 = message2.from_id;
        if (i3 > 0) {
            user = abstractMap4 != null ? (User) abstractMap4.get(Integer.valueOf(i3)) : sparseArray4 != null ? (User) sparseArray4.get(i3) : null;
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
        i3 = gregorianCalendar.get(6);
        int i4 = gregorianCalendar.get(i2);
        int i5 = gregorianCalendar.get(2);
        this.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i3)});
        this.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5)});
        createMessageSendInfo();
        generateCaption();
        boolean z3 = z;
        if (z3) {
            TextPaint textPaint;
            if (this.messageOwner.media instanceof TL_messageMediaGame) {
                textPaint = Theme.chat_msgGameTextPaint;
            } else {
                textPaint = Theme.chat_msgTextPaint;
            }
            int[] iArr = SharedConfig.allowBigEmoji ? new int[1] : null;
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            checkEmojiOnly(iArr);
            this.emojiAnimatedSticker = null;
            if (this.emojiOnlyCount == 1) {
                String replace = this.messageText.toString().replace("️", "");
                ArrayList stickerSets = MediaDataController.getInstance(this.currentAccount).getStickerSets(4);
                int size = stickerSets.size();
                for (int i6 = 0; i6 < size; i6++) {
                    TL_messages_stickerSet tL_messages_stickerSet = (TL_messages_stickerSet) stickerSets.get(i6);
                    int size2 = tL_messages_stickerSet.packs.size();
                    for (int i7 = 0; i7 < size2; i7++) {
                        TL_stickerPack tL_stickerPack = (TL_stickerPack) tL_messages_stickerSet.packs.get(i7);
                        if (!tL_stickerPack.documents.isEmpty() && TextUtils.equals(tL_stickerPack.emoticon, replace)) {
                            this.emojiAnimatedSticker = (Document) MediaDataController.getInstance(this.currentAccount).getStickerByIds(4).get(((Long) tL_stickerPack.documents.get(0)).longValue());
                            break;
                        }
                    }
                    if (this.emojiAnimatedSticker != null) {
                        break;
                    }
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
        this.layoutCreated = z3;
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

    /* JADX WARNING: Removed duplicated region for block: B:247:0x06af  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x069b  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06b5 A:{LOOP_END, LOOP:0: B:228:0x066e->B:250:0x06b5} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x06cf A:{SYNTHETIC, EDGE_INSN: B:552:0x06cf->B:252:0x06cf ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x069b  */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x06af  */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x06cf A:{SYNTHETIC, EDGE_INSN: B:552:0x06cf->B:252:0x06cf ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06b5 A:{LOOP_END, LOOP:0: B:228:0x066e->B:250:0x06b5} */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x06af  */
    /* JADX WARNING: Removed duplicated region for block: B:242:0x069b  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x06b5 A:{LOOP_END, LOOP:0: B:228:0x066e->B:250:0x06b5} */
    /* JADX WARNING: Removed duplicated region for block: B:552:0x06cf A:{SYNTHETIC, EDGE_INSN: B:552:0x06cf->B:252:0x06cf ?: BREAK  } */
    /* JADX WARNING: Removed duplicated region for block: B:469:0x0d04  */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0cf4  */
    /* JADX WARNING: Removed duplicated region for block: B:472:0x0d19  */
    /* JADX WARNING: Removed duplicated region for block: B:513:0x0e8c  */
    /* JADX WARNING: Removed duplicated region for block: B:516:0x0ecb  */
    /* JADX WARNING: Removed duplicated region for block: B:533:0x0var_  */
    /* JADX WARNING: Removed duplicated region for block: B:523:0x0ee3  */
    /* JADX WARNING: Removed duplicated region for block: B:554:? A:{SYNTHETIC, RETURN} */
    /* JADX WARNING: Removed duplicated region for block: B:536:0x0f4a  */
    /* JADX WARNING: Missing block: B:455:0x0cc4, code skipped:
            if (r9.id != r10.id) goto L_0x0cdd;
     */
    /* JADX WARNING: Missing block: B:461:0x0cd8, code skipped:
            if (r9.id != r10.id) goto L_0x0cdd;
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
        r0.currentEvent = r1;
        r5 = r26;
        r0.currentAccount = r5;
        r5 = r1.user_id;
        if (r5 <= 0) goto L_0x002c;
    L_0x001b:
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesController.getInstance(r5);
        r7 = r1.user_id;
        r7 = java.lang.Integer.valueOf(r7);
        r5 = r5.getUser(r7);
        goto L_0x002d;
    L_0x002c:
        r5 = 0;
    L_0x002d:
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
        if (r10 == 0) goto L_0x00c4;
    L_0x0092:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle) r8;
        r7 = r8.new_value;
        r8 = r4.megagroup;
        if (r8 == 0) goto L_0x00af;
    L_0x009a:
        r8 = NUM; // 0x7f0d0415 float:1.8744234E38 double:1.053130294E-314;
        r10 = new java.lang.Object[r9];
        r10[r15] = r7;
        r7 = "EventLogEditedGroupTitle";
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e87;
    L_0x00af:
        r8 = NUM; // 0x7f0d0412 float:1.8744228E38 double:1.0531302924E-314;
        r10 = new java.lang.Object[r9];
        r10[r15] = r7;
        r7 = "EventLogEditedChannelTitle";
        r7 = org.telegram.messenger.LocaleController.formatString(r7, r8, r10);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e87;
    L_0x00c4:
        r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangePhoto;
        if (r10 == 0) goto L_0x013f;
    L_0x00c8:
        r7 = new org.telegram.tgnet.TLRPC$TL_messageService;
        r7.<init>();
        r0.messageOwner = r7;
        r7 = r1.action;
        r7 = r7.new_photo;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_photoEmpty;
        if (r7 == 0) goto L_0x0106;
    L_0x00d7:
        r7 = r0.messageOwner;
        r8 = new org.telegram.tgnet.TLRPC$TL_messageActionChatDeletePhoto;
        r8.<init>();
        r7.action = r8;
        r7 = r4.megagroup;
        if (r7 == 0) goto L_0x00f5;
    L_0x00e4:
        r7 = NUM; // 0x7f0d0446 float:1.8744334E38 double:1.053130318E-314;
        r8 = "EventLogRemovedWGroupPhoto";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e87;
    L_0x00f5:
        r7 = NUM; // 0x7f0d0440 float:1.8744321E38 double:1.053130315E-314;
        r8 = "EventLogRemovedChannelPhoto";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e87;
    L_0x0106:
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
        if (r7 == 0) goto L_0x012e;
    L_0x011d:
        r7 = NUM; // 0x7f0d0414 float:1.8744232E38 double:1.0531302933E-314;
        r8 = "EventLogEditedGroupPhoto";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e87;
    L_0x012e:
        r7 = NUM; // 0x7f0d0411 float:1.8744226E38 double:1.053130292E-314;
        r8 = "EventLogEditedChannelPhoto";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r7 = r0.replaceWithLink(r7, r14, r5);
        r0.messageText = r7;
        goto L_0x0e87;
    L_0x013f:
        r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoin;
        r6 = "EventLogChannelJoined";
        if (r10 == 0) goto L_0x0169;
    L_0x0145:
        r7 = r4.megagroup;
        if (r7 == 0) goto L_0x015a;
    L_0x0149:
        r6 = NUM; // 0x7f0d0427 float:1.874427E38 double:1.0531303027E-314;
        r7 = "EventLogGroupJoined";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e87;
    L_0x015a:
        r7 = NUM; // 0x7f0d040a float:1.8744212E38 double:1.0531302884E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e87;
    L_0x0169:
        r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantLeave;
        if (r10 == 0) goto L_0x01ab;
    L_0x016d:
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
        if (r6 == 0) goto L_0x019a;
    L_0x0189:
        r6 = NUM; // 0x7f0d042c float:1.874428E38 double:1.053130305E-314;
        r7 = "EventLogLeftGroup";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e87;
    L_0x019a:
        r6 = NUM; // 0x7f0d042b float:1.8744279E38 double:1.0531303047E-314;
        r7 = "EventLogLeftChannel";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e87;
    L_0x01ab:
        r10 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantInvite;
        r11 = "un2";
        if (r10 == 0) goto L_0x021e;
    L_0x01b1:
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
        if (r8 != r10) goto L_0x0205;
    L_0x01e1:
        r7 = r4.megagroup;
        if (r7 == 0) goto L_0x01f6;
    L_0x01e5:
        r6 = NUM; // 0x7f0d0427 float:1.874427E38 double:1.0531303027E-314;
        r7 = "EventLogGroupJoined";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e87;
    L_0x01f6:
        r7 = NUM; // 0x7f0d040a float:1.8744212E38 double:1.0531302884E-314;
        r6 = org.telegram.messenger.LocaleController.getString(r6, r7);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e87;
    L_0x0205:
        r6 = NUM; // 0x7f0d0400 float:1.8744192E38 double:1.0531302835E-314;
        r8 = "EventLogAdded";
        r6 = org.telegram.messenger.LocaleController.getString(r8, r6);
        r6 = r0.replaceWithLink(r6, r11, r7);
        r0.messageText = r6;
        r6 = r0.messageText;
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0e87;
    L_0x021e:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin;
        r10 = "%1$s";
        r12 = 10;
        if (r6 == 0) goto L_0x0459;
    L_0x0226:
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
        if (r8 != 0) goto L_0x0273;
    L_0x0249:
        r7 = r7.new_participant;
        r7 = r7 instanceof org.telegram.tgnet.TLRPC.TL_channelParticipantCreator;
        if (r7 == 0) goto L_0x0273;
    L_0x024f:
        r7 = NUM; // 0x7f0d0408 float:1.8744208E38 double:1.0531302874E-314;
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
        goto L_0x0451;
    L_0x0273:
        r7 = NUM; // 0x7f0d0433 float:1.8744295E38 double:1.0531303087E-314;
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
        if (r7 != 0) goto L_0x02ab;
    L_0x02a6:
        r7 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights;
        r7.<init>();
    L_0x02ab:
        if (r6 != 0) goto L_0x02b2;
    L_0x02ad:
        r6 = new org.telegram.tgnet.TLRPC$TL_chatAdminRights;
        r6.<init>();
    L_0x02b2:
        r8 = r1.action;
        r11 = r8.prev_participant;
        r11 = r11.rank;
        r8 = r8.new_participant;
        r8 = r8.rank;
        r8 = android.text.TextUtils.equals(r11, r8);
        if (r8 != 0) goto L_0x030b;
    L_0x02c2:
        r8 = r1.action;
        r8 = r8.new_participant;
        r8 = r8.rank;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 == 0) goto L_0x02e8;
    L_0x02ce:
        r10.append(r12);
        r8 = 45;
        r10.append(r8);
        r11 = 32;
        r10.append(r11);
        r14 = NUM; // 0x7f0d043d float:1.8744315E38 double:1.0531303136E-314;
        r8 = "EventLogPromotedRemovedTitle";
        r8 = org.telegram.messenger.LocaleController.getString(r8, r14);
        r10.append(r8);
        goto L_0x030b;
    L_0x02e8:
        r11 = 32;
        r10.append(r12);
        r8 = 43;
        r10.append(r8);
        r10.append(r11);
        r11 = NUM; // 0x7f0d043e float:1.8744317E38 double:1.053130314E-314;
        r14 = new java.lang.Object[r9];
        r8 = r1.action;
        r8 = r8.new_participant;
        r8 = r8.rank;
        r14[r15] = r8;
        r8 = "EventLogPromotedTitle";
        r8 = org.telegram.messenger.LocaleController.formatString(r8, r11, r14);
        r10.append(r8);
    L_0x030b:
        r8 = r7.change_info;
        r11 = r6.change_info;
        if (r8 == r11) goto L_0x033b;
    L_0x0311:
        r10.append(r12);
        r8 = r6.change_info;
        if (r8 == 0) goto L_0x031b;
    L_0x0318:
        r8 = 43;
        goto L_0x031d;
    L_0x031b:
        r8 = 45;
    L_0x031d:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = r4.megagroup;
        if (r8 == 0) goto L_0x032f;
    L_0x0329:
        r8 = NUM; // 0x7f0d0438 float:1.8744305E38 double:1.053130311E-314;
        r11 = "EventLogPromotedChangeGroupInfo";
        goto L_0x0334;
    L_0x032f:
        r8 = NUM; // 0x7f0d0437 float:1.8744303E38 double:1.0531303106E-314;
        r11 = "EventLogPromotedChangeChannelInfo";
    L_0x0334:
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x033b:
        r8 = r4.megagroup;
        if (r8 != 0) goto L_0x038b;
    L_0x033f:
        r8 = r7.post_messages;
        r11 = r6.post_messages;
        if (r8 == r11) goto L_0x0365;
    L_0x0345:
        r10.append(r12);
        r8 = r6.post_messages;
        if (r8 == 0) goto L_0x034f;
    L_0x034c:
        r8 = 43;
        goto L_0x0351;
    L_0x034f:
        r8 = 45;
    L_0x0351:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0d043c float:1.8744313E38 double:1.053130313E-314;
        r11 = "EventLogPromotedPostMessages";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x0365:
        r8 = r7.edit_messages;
        r11 = r6.edit_messages;
        if (r8 == r11) goto L_0x038b;
    L_0x036b:
        r10.append(r12);
        r8 = r6.edit_messages;
        if (r8 == 0) goto L_0x0375;
    L_0x0372:
        r8 = 43;
        goto L_0x0377;
    L_0x0375:
        r8 = 45;
    L_0x0377:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0d043a float:1.874431E38 double:1.053130312E-314;
        r11 = "EventLogPromotedEditMessages";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x038b:
        r8 = r7.delete_messages;
        r11 = r6.delete_messages;
        if (r8 == r11) goto L_0x03b1;
    L_0x0391:
        r10.append(r12);
        r8 = r6.delete_messages;
        if (r8 == 0) goto L_0x039b;
    L_0x0398:
        r8 = 43;
        goto L_0x039d;
    L_0x039b:
        r8 = 45;
    L_0x039d:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0d0439 float:1.8744307E38 double:1.0531303116E-314;
        r11 = "EventLogPromotedDeleteMessages";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x03b1:
        r8 = r7.add_admins;
        r11 = r6.add_admins;
        if (r8 == r11) goto L_0x03d7;
    L_0x03b7:
        r10.append(r12);
        r8 = r6.add_admins;
        if (r8 == 0) goto L_0x03c1;
    L_0x03be:
        r8 = 43;
        goto L_0x03c3;
    L_0x03c1:
        r8 = 45;
    L_0x03c3:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0d0434 float:1.8744297E38 double:1.053130309E-314;
        r11 = "EventLogPromotedAddAdmins";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x03d7:
        r8 = r4.megagroup;
        if (r8 == 0) goto L_0x0401;
    L_0x03db:
        r8 = r7.ban_users;
        r11 = r6.ban_users;
        if (r8 == r11) goto L_0x0401;
    L_0x03e1:
        r10.append(r12);
        r8 = r6.ban_users;
        if (r8 == 0) goto L_0x03eb;
    L_0x03e8:
        r8 = 43;
        goto L_0x03ed;
    L_0x03eb:
        r8 = 45;
    L_0x03ed:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0d0436 float:1.8744301E38 double:1.05313031E-314;
        r11 = "EventLogPromotedBanUsers";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x0401:
        r8 = r7.invite_users;
        r11 = r6.invite_users;
        if (r8 == r11) goto L_0x0427;
    L_0x0407:
        r10.append(r12);
        r8 = r6.invite_users;
        if (r8 == 0) goto L_0x0411;
    L_0x040e:
        r8 = 43;
        goto L_0x0413;
    L_0x0411:
        r8 = 45;
    L_0x0413:
        r10.append(r8);
        r8 = 32;
        r10.append(r8);
        r8 = NUM; // 0x7f0d0435 float:1.87443E38 double:1.0531303097E-314;
        r11 = "EventLogPromotedAddUsers";
        r8 = org.telegram.messenger.LocaleController.getString(r11, r8);
        r10.append(r8);
    L_0x0427:
        r8 = r4.megagroup;
        if (r8 == 0) goto L_0x0451;
    L_0x042b:
        r7 = r7.pin_messages;
        r8 = r6.pin_messages;
        if (r7 == r8) goto L_0x0451;
    L_0x0431:
        r10.append(r12);
        r6 = r6.pin_messages;
        if (r6 == 0) goto L_0x043b;
    L_0x0438:
        r6 = 43;
        goto L_0x043d;
    L_0x043b:
        r6 = 45;
    L_0x043d:
        r10.append(r6);
        r6 = 32;
        r10.append(r6);
        r6 = NUM; // 0x7f0d043b float:1.8744311E38 double:1.0531303126E-314;
        r7 = "EventLogPromotedPinMessages";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r10.append(r6);
    L_0x0451:
        r6 = r10.toString();
        r0.messageText = r6;
        goto L_0x0e87;
    L_0x0459:
        r18 = 43;
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDefaultBannedRights;
        if (r6 == 0) goto L_0x0601;
    L_0x045f:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDefaultBannedRights) r8;
        r6 = new org.telegram.tgnet.TLRPC$TL_message;
        r6.<init>();
        r0.messageOwner = r6;
        r6 = r8.prev_banned_rights;
        r7 = r8.new_banned_rights;
        r8 = new java.lang.StringBuilder;
        r10 = NUM; // 0x7f0d040d float:1.8744218E38 double:1.05313029E-314;
        r11 = "EventLogDefaultPermissions";
        r10 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r8.<init>(r10);
        if (r6 != 0) goto L_0x0481;
    L_0x047c:
        r6 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r6.<init>();
    L_0x0481:
        if (r7 != 0) goto L_0x0488;
    L_0x0483:
        r7 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r7.<init>();
    L_0x0488:
        r10 = r6.send_messages;
        r11 = r7.send_messages;
        if (r10 == r11) goto L_0x04b3;
    L_0x048e:
        r8.append(r12);
        r8.append(r12);
        r10 = r7.send_messages;
        if (r10 != 0) goto L_0x049b;
    L_0x0498:
        r10 = 43;
        goto L_0x049d;
    L_0x049b:
        r10 = 45;
    L_0x049d:
        r8.append(r10);
        r10 = 32;
        r8.append(r10);
        r10 = NUM; // 0x7f0d044d float:1.8744348E38 double:1.0531303215E-314;
        r11 = "EventLogRestrictedSendMessages";
        r10 = org.telegram.messenger.LocaleController.getString(r11, r10);
        r8.append(r10);
        r10 = 1;
        goto L_0x04b4;
    L_0x04b3:
        r10 = 0;
    L_0x04b4:
        r11 = r6.send_stickers;
        r14 = r7.send_stickers;
        if (r11 != r14) goto L_0x04cc;
    L_0x04ba:
        r11 = r6.send_inline;
        r14 = r7.send_inline;
        if (r11 != r14) goto L_0x04cc;
    L_0x04c0:
        r11 = r6.send_gifs;
        r14 = r7.send_gifs;
        if (r11 != r14) goto L_0x04cc;
    L_0x04c6:
        r11 = r6.send_games;
        r14 = r7.send_games;
        if (r11 == r14) goto L_0x04f2;
    L_0x04cc:
        if (r10 != 0) goto L_0x04d2;
    L_0x04ce:
        r8.append(r12);
        r10 = 1;
    L_0x04d2:
        r8.append(r12);
        r11 = r7.send_stickers;
        if (r11 != 0) goto L_0x04dc;
    L_0x04d9:
        r11 = 43;
        goto L_0x04de;
    L_0x04dc:
        r11 = 45;
    L_0x04de:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0d044f float:1.8744352E38 double:1.0531303225E-314;
        r14 = "EventLogRestrictedSendStickers";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x04f2:
        r11 = r6.send_media;
        r14 = r7.send_media;
        if (r11 == r14) goto L_0x051e;
    L_0x04f8:
        if (r10 != 0) goto L_0x04fe;
    L_0x04fa:
        r8.append(r12);
        r10 = 1;
    L_0x04fe:
        r8.append(r12);
        r11 = r7.send_media;
        if (r11 != 0) goto L_0x0508;
    L_0x0505:
        r11 = 43;
        goto L_0x050a;
    L_0x0508:
        r11 = 45;
    L_0x050a:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0d044c float:1.8744346E38 double:1.053130321E-314;
        r14 = "EventLogRestrictedSendMedia";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x051e:
        r11 = r6.send_polls;
        r14 = r7.send_polls;
        if (r11 == r14) goto L_0x054a;
    L_0x0524:
        if (r10 != 0) goto L_0x052a;
    L_0x0526:
        r8.append(r12);
        r10 = 1;
    L_0x052a:
        r8.append(r12);
        r11 = r7.send_polls;
        if (r11 != 0) goto L_0x0534;
    L_0x0531:
        r11 = 43;
        goto L_0x0536;
    L_0x0534:
        r11 = 45;
    L_0x0536:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0d044e float:1.874435E38 double:1.053130322E-314;
        r14 = "EventLogRestrictedSendPolls";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x054a:
        r11 = r6.embed_links;
        r14 = r7.embed_links;
        if (r11 == r14) goto L_0x0576;
    L_0x0550:
        if (r10 != 0) goto L_0x0556;
    L_0x0552:
        r8.append(r12);
        r10 = 1;
    L_0x0556:
        r8.append(r12);
        r11 = r7.embed_links;
        if (r11 != 0) goto L_0x0560;
    L_0x055d:
        r11 = 43;
        goto L_0x0562;
    L_0x0560:
        r11 = 45;
    L_0x0562:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0d044b float:1.8744344E38 double:1.0531303205E-314;
        r14 = "EventLogRestrictedSendEmbed";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x0576:
        r11 = r6.change_info;
        r14 = r7.change_info;
        if (r11 == r14) goto L_0x05a2;
    L_0x057c:
        if (r10 != 0) goto L_0x0582;
    L_0x057e:
        r8.append(r12);
        r10 = 1;
    L_0x0582:
        r8.append(r12);
        r11 = r7.change_info;
        if (r11 != 0) goto L_0x058c;
    L_0x0589:
        r11 = 43;
        goto L_0x058e;
    L_0x058c:
        r11 = 45;
    L_0x058e:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0d0447 float:1.8744336E38 double:1.0531303185E-314;
        r14 = "EventLogRestrictedChangeInfo";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x05a2:
        r11 = r6.invite_users;
        r14 = r7.invite_users;
        if (r11 == r14) goto L_0x05ce;
    L_0x05a8:
        if (r10 != 0) goto L_0x05ae;
    L_0x05aa:
        r8.append(r12);
        r10 = 1;
    L_0x05ae:
        r8.append(r12);
        r11 = r7.invite_users;
        if (r11 != 0) goto L_0x05b8;
    L_0x05b5:
        r11 = 43;
        goto L_0x05ba;
    L_0x05b8:
        r11 = 45;
    L_0x05ba:
        r8.append(r11);
        r11 = 32;
        r8.append(r11);
        r11 = NUM; // 0x7f0d0448 float:1.8744338E38 double:1.053130319E-314;
        r14 = "EventLogRestrictedInviteUsers";
        r11 = org.telegram.messenger.LocaleController.getString(r14, r11);
        r8.append(r11);
    L_0x05ce:
        r6 = r6.pin_messages;
        r11 = r7.pin_messages;
        if (r6 == r11) goto L_0x05f9;
    L_0x05d4:
        if (r10 != 0) goto L_0x05d9;
    L_0x05d6:
        r8.append(r12);
    L_0x05d9:
        r8.append(r12);
        r6 = r7.pin_messages;
        if (r6 != 0) goto L_0x05e3;
    L_0x05e0:
        r6 = 43;
        goto L_0x05e5;
    L_0x05e3:
        r6 = 45;
    L_0x05e5:
        r8.append(r6);
        r6 = 32;
        r8.append(r6);
        r6 = NUM; // 0x7f0d0449 float:1.874434E38 double:1.0531303195E-314;
        r7 = "EventLogRestrictedPinMessages";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r8.append(r6);
    L_0x05f9:
        r6 = r8.toString();
        r0.messageText = r6;
        goto L_0x0e87;
    L_0x0601:
        r6 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan;
        r12 = 60;
        if (r6 == 0) goto L_0x090c;
    L_0x0607:
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
        if (r11 == 0) goto L_0x08d7;
    L_0x0630:
        if (r7 == 0) goto L_0x0640;
    L_0x0632:
        r11 = r7.view_messages;
        if (r11 == 0) goto L_0x0640;
    L_0x0636:
        if (r7 == 0) goto L_0x08d7;
    L_0x0638:
        if (r8 == 0) goto L_0x08d7;
    L_0x063a:
        r11 = r7.until_date;
        r14 = r8.until_date;
        if (r11 == r14) goto L_0x08d7;
    L_0x0640:
        if (r7 == 0) goto L_0x06c1;
    L_0x0642:
        r11 = org.telegram.messenger.AndroidUtilities.isBannedForever(r7);
        if (r11 != 0) goto L_0x06c1;
    L_0x0648:
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
    L_0x066e:
        if (r12 >= r2) goto L_0x06cf;
    L_0x0670:
        if (r12 != 0) goto L_0x067d;
    L_0x0672:
        if (r15 == 0) goto L_0x0692;
    L_0x0674:
        r2 = "Days";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r15);
    L_0x067a:
        r17 = r17 + 1;
        goto L_0x0693;
    L_0x067d:
        r2 = 1;
        if (r12 != r2) goto L_0x0689;
    L_0x0680:
        if (r9 == 0) goto L_0x0692;
    L_0x0682:
        r2 = "Hours";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r9);
        goto L_0x067a;
    L_0x0689:
        if (r14 == 0) goto L_0x0692;
    L_0x068b:
        r2 = "Minutes";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r2, r14);
        goto L_0x067a;
    L_0x0692:
        r2 = 0;
    L_0x0693:
        r24 = r17;
        r17 = r9;
        r9 = r24;
        if (r2 == 0) goto L_0x06af;
    L_0x069b:
        r23 = r11.length();
        if (r23 <= 0) goto L_0x06a9;
    L_0x06a1:
        r23 = r14;
        r14 = ", ";
        r11.append(r14);
        goto L_0x06ab;
    L_0x06a9:
        r23 = r14;
    L_0x06ab:
        r11.append(r2);
        goto L_0x06b1;
    L_0x06af:
        r23 = r14;
    L_0x06b1:
        r2 = 2;
        if (r9 != r2) goto L_0x06b5;
    L_0x06b4:
        goto L_0x06cf;
    L_0x06b5:
        r12 = r12 + 1;
        r14 = r23;
        r2 = 3;
        r24 = r17;
        r17 = r9;
        r9 = r24;
        goto L_0x066e;
    L_0x06c1:
        r11 = new java.lang.StringBuilder;
        r2 = NUM; // 0x7f0d0ab6 float:1.8747676E38 double:1.0531311323E-314;
        r9 = "UserRestrictionsUntilForever";
        r2 = org.telegram.messenger.LocaleController.getString(r9, r2);
        r11.<init>(r2);
    L_0x06cf:
        r2 = NUM; // 0x7f0d0450 float:1.8744354E38 double:1.053130323E-314;
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
        if (r8 != 0) goto L_0x0701;
    L_0x06fc:
        r8 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r8.<init>();
    L_0x0701:
        if (r7 != 0) goto L_0x0708;
    L_0x0703:
        r7 = new org.telegram.tgnet.TLRPC$TL_chatBannedRights;
        r7.<init>();
    L_0x0708:
        r2 = r8.view_messages;
        r6 = r7.view_messages;
        if (r2 == r6) goto L_0x0735;
    L_0x070e:
        r2 = 10;
        r10.append(r2);
        r10.append(r2);
        r2 = r7.view_messages;
        if (r2 != 0) goto L_0x071d;
    L_0x071a:
        r2 = 43;
        goto L_0x071f;
    L_0x071d:
        r2 = 45;
    L_0x071f:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0d044a float:1.8744342E38 double:1.05313032E-314;
        r6 = "EventLogRestrictedReadMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
        r9 = 1;
        goto L_0x0736;
    L_0x0735:
        r9 = 0;
    L_0x0736:
        r2 = r8.send_messages;
        r6 = r7.send_messages;
        if (r2 == r6) goto L_0x0767;
    L_0x073c:
        if (r9 != 0) goto L_0x0745;
    L_0x073e:
        r2 = 10;
        r10.append(r2);
        r9 = 1;
        goto L_0x0747;
    L_0x0745:
        r2 = 10;
    L_0x0747:
        r10.append(r2);
        r2 = r7.send_messages;
        if (r2 != 0) goto L_0x0751;
    L_0x074e:
        r2 = 43;
        goto L_0x0753;
    L_0x0751:
        r2 = 45;
    L_0x0753:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0d044d float:1.8744348E38 double:1.0531303215E-314;
        r6 = "EventLogRestrictedSendMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x0767:
        r2 = r8.send_stickers;
        r6 = r7.send_stickers;
        if (r2 != r6) goto L_0x077f;
    L_0x076d:
        r2 = r8.send_inline;
        r6 = r7.send_inline;
        if (r2 != r6) goto L_0x077f;
    L_0x0773:
        r2 = r8.send_gifs;
        r6 = r7.send_gifs;
        if (r2 != r6) goto L_0x077f;
    L_0x0779:
        r2 = r8.send_games;
        r6 = r7.send_games;
        if (r2 == r6) goto L_0x07aa;
    L_0x077f:
        if (r9 != 0) goto L_0x0788;
    L_0x0781:
        r2 = 10;
        r10.append(r2);
        r9 = 1;
        goto L_0x078a;
    L_0x0788:
        r2 = 10;
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
        r2 = NUM; // 0x7f0d044f float:1.8744352E38 double:1.0531303225E-314;
        r6 = "EventLogRestrictedSendStickers";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x07aa:
        r2 = r8.send_media;
        r6 = r7.send_media;
        if (r2 == r6) goto L_0x07db;
    L_0x07b0:
        if (r9 != 0) goto L_0x07b9;
    L_0x07b2:
        r2 = 10;
        r10.append(r2);
        r9 = 1;
        goto L_0x07bb;
    L_0x07b9:
        r2 = 10;
    L_0x07bb:
        r10.append(r2);
        r2 = r7.send_media;
        if (r2 != 0) goto L_0x07c5;
    L_0x07c2:
        r2 = 43;
        goto L_0x07c7;
    L_0x07c5:
        r2 = 45;
    L_0x07c7:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0d044c float:1.8744346E38 double:1.053130321E-314;
        r6 = "EventLogRestrictedSendMedia";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x07db:
        r2 = r8.send_polls;
        r6 = r7.send_polls;
        if (r2 == r6) goto L_0x080c;
    L_0x07e1:
        if (r9 != 0) goto L_0x07ea;
    L_0x07e3:
        r2 = 10;
        r10.append(r2);
        r9 = 1;
        goto L_0x07ec;
    L_0x07ea:
        r2 = 10;
    L_0x07ec:
        r10.append(r2);
        r2 = r7.send_polls;
        if (r2 != 0) goto L_0x07f6;
    L_0x07f3:
        r2 = 43;
        goto L_0x07f8;
    L_0x07f6:
        r2 = 45;
    L_0x07f8:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0d044e float:1.874435E38 double:1.053130322E-314;
        r6 = "EventLogRestrictedSendPolls";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x080c:
        r2 = r8.embed_links;
        r6 = r7.embed_links;
        if (r2 == r6) goto L_0x083d;
    L_0x0812:
        if (r9 != 0) goto L_0x081b;
    L_0x0814:
        r2 = 10;
        r10.append(r2);
        r9 = 1;
        goto L_0x081d;
    L_0x081b:
        r2 = 10;
    L_0x081d:
        r10.append(r2);
        r2 = r7.embed_links;
        if (r2 != 0) goto L_0x0827;
    L_0x0824:
        r2 = 43;
        goto L_0x0829;
    L_0x0827:
        r2 = 45;
    L_0x0829:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0d044b float:1.8744344E38 double:1.0531303205E-314;
        r6 = "EventLogRestrictedSendEmbed";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x083d:
        r2 = r8.change_info;
        r6 = r7.change_info;
        if (r2 == r6) goto L_0x086e;
    L_0x0843:
        if (r9 != 0) goto L_0x084c;
    L_0x0845:
        r2 = 10;
        r10.append(r2);
        r9 = 1;
        goto L_0x084e;
    L_0x084c:
        r2 = 10;
    L_0x084e:
        r10.append(r2);
        r2 = r7.change_info;
        if (r2 != 0) goto L_0x0858;
    L_0x0855:
        r2 = 43;
        goto L_0x085a;
    L_0x0858:
        r2 = 45;
    L_0x085a:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0d0447 float:1.8744336E38 double:1.0531303185E-314;
        r6 = "EventLogRestrictedChangeInfo";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x086e:
        r2 = r8.invite_users;
        r6 = r7.invite_users;
        if (r2 == r6) goto L_0x089f;
    L_0x0874:
        if (r9 != 0) goto L_0x087d;
    L_0x0876:
        r2 = 10;
        r10.append(r2);
        r9 = 1;
        goto L_0x087f;
    L_0x087d:
        r2 = 10;
    L_0x087f:
        r10.append(r2);
        r2 = r7.invite_users;
        if (r2 != 0) goto L_0x0889;
    L_0x0886:
        r2 = 43;
        goto L_0x088b;
    L_0x0889:
        r2 = 45;
    L_0x088b:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0d0448 float:1.8744338E38 double:1.053130319E-314;
        r6 = "EventLogRestrictedInviteUsers";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x089f:
        r2 = r8.pin_messages;
        r6 = r7.pin_messages;
        if (r2 == r6) goto L_0x08cf;
    L_0x08a5:
        if (r9 != 0) goto L_0x08ad;
    L_0x08a7:
        r2 = 10;
        r10.append(r2);
        goto L_0x08af;
    L_0x08ad:
        r2 = 10;
    L_0x08af:
        r10.append(r2);
        r2 = r7.pin_messages;
        if (r2 != 0) goto L_0x08b9;
    L_0x08b6:
        r2 = 43;
        goto L_0x08bb;
    L_0x08b9:
        r2 = 45;
    L_0x08bb:
        r10.append(r2);
        r2 = 32;
        r10.append(r2);
        r2 = NUM; // 0x7f0d0449 float:1.874434E38 double:1.0531303195E-314;
        r6 = "EventLogRestrictedPinMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r10.append(r2);
    L_0x08cf:
        r2 = r10.toString();
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x08d7:
        if (r7 == 0) goto L_0x08e9;
    L_0x08d9:
        if (r8 == 0) goto L_0x08df;
    L_0x08db:
        r2 = r7.view_messages;
        if (r2 == 0) goto L_0x08e9;
    L_0x08df:
        r2 = NUM; // 0x7f0d040b float:1.8744214E38 double:1.053130289E-314;
        r7 = "EventLogChannelRestricted";
        r2 = org.telegram.messenger.LocaleController.getString(r7, r2);
        goto L_0x08f2;
    L_0x08e9:
        r2 = NUM; // 0x7f0d040c float:1.8744216E38 double:1.0531302894E-314;
        r7 = "EventLogChannelUnrestricted";
        r2 = org.telegram.messenger.LocaleController.getString(r7, r2);
    L_0x08f2:
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
        goto L_0x0e87;
    L_0x090c:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionUpdatePinned;
        if (r2 == 0) goto L_0x0989;
    L_0x0910:
        if (r5 == 0) goto L_0x095f;
    L_0x0912:
        r2 = r5.id;
        r6 = NUM; // 0x827aCLASSNAME float:5.045703E-34 double:6.75969194E-316;
        if (r2 != r6) goto L_0x095f;
    L_0x0919:
        r2 = r8.message;
        r2 = r2.fwd_from;
        if (r2 == 0) goto L_0x095f;
    L_0x091f:
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
        if (r6 == 0) goto L_0x094e;
    L_0x093d:
        r6 = NUM; // 0x7f0d045b float:1.8744376E38 double:1.0531303284E-314;
        r7 = "EventLogUnpinnedMessages";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r2 = r0.replaceWithLink(r6, r14, r2);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x094e:
        r6 = NUM; // 0x7f0d0430 float:1.874429E38 double:1.053130307E-314;
        r7 = "EventLogPinnedMessages";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r2 = r0.replaceWithLink(r6, r14, r2);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x095f:
        r2 = r1.action;
        r2 = r2.message;
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty;
        if (r2 == 0) goto L_0x0978;
    L_0x0967:
        r2 = NUM; // 0x7f0d045b float:1.8744376E38 double:1.0531303284E-314;
        r6 = "EventLogUnpinnedMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0978:
        r2 = NUM; // 0x7f0d0430 float:1.874429E38 double:1.053130307E-314;
        r6 = "EventLogPinnedMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0989:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionStopPoll;
        if (r2 == 0) goto L_0x099e;
    L_0x098d:
        r2 = NUM; // 0x7f0d0452 float:1.8744358E38 double:1.053130324E-314;
        r6 = "EventLogStopPoll";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x099e:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures;
        if (r2 == 0) goto L_0x09ca;
    L_0x09a2:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures) r8;
        r2 = r8.new_value;
        if (r2 == 0) goto L_0x09b9;
    L_0x09a8:
        r2 = NUM; // 0x7f0d0458 float:1.874437E38 double:1.053130327E-314;
        r6 = "EventLogToggledSignaturesOn";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x09b9:
        r2 = NUM; // 0x7f0d0457 float:1.8744368E38 double:1.0531303265E-314;
        r6 = "EventLogToggledSignaturesOff";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x09ca:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites;
        if (r2 == 0) goto L_0x09f6;
    L_0x09ce:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites) r8;
        r2 = r8.new_value;
        if (r2 == 0) goto L_0x09e5;
    L_0x09d4:
        r2 = NUM; // 0x7f0d0456 float:1.8744366E38 double:1.053130326E-314;
        r6 = "EventLogToggledInvitesOn";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x09e5:
        r2 = NUM; // 0x7f0d0455 float:1.8744364E38 double:1.0531303255E-314;
        r6 = "EventLogToggledInvitesOff";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x09f6:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDeleteMessage;
        if (r2 == 0) goto L_0x0a0b;
    L_0x09fa:
        r2 = NUM; // 0x7f0d040e float:1.874422E38 double:1.0531302904E-314;
        r6 = "EventLogDeletedMessages";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0a0b:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat;
        if (r2 == 0) goto L_0x0abc;
    L_0x0a0f:
        r2 = r8;
        r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat) r2;
        r2 = r2.new_value;
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLinkedChat) r8;
        r6 = r8.prev_value;
        r7 = r4.megagroup;
        if (r7 == 0) goto L_0x0a6c;
    L_0x0a1c:
        if (r2 != 0) goto L_0x0a45;
    L_0x0a1e:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r6 = java.lang.Integer.valueOf(r6);
        r2 = r2.getChat(r6);
        r6 = NUM; // 0x7f0d0442 float:1.8744326E38 double:1.053130316E-314;
        r7 = "EventLogRemovedLinkedChannel";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        r6 = r0.messageText;
        r2 = r0.replaceWithLink(r6, r11, r2);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0a45:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r6.getChat(r2);
        r6 = NUM; // 0x7f0d0405 float:1.8744202E38 double:1.053130286E-314;
        r7 = "EventLogChangedLinkedChannel";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        r6 = r0.messageText;
        r2 = r0.replaceWithLink(r6, r11, r2);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0a6c:
        if (r2 != 0) goto L_0x0a95;
    L_0x0a6e:
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r6 = java.lang.Integer.valueOf(r6);
        r2 = r2.getChat(r6);
        r6 = NUM; // 0x7f0d0443 float:1.8744328E38 double:1.0531303166E-314;
        r7 = "EventLogRemovedLinkedGroup";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        r6 = r0.messageText;
        r2 = r0.replaceWithLink(r6, r11, r2);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0a95:
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesController.getInstance(r6);
        r2 = java.lang.Integer.valueOf(r2);
        r2 = r6.getChat(r2);
        r6 = NUM; // 0x7f0d0406 float:1.8744204E38 double:1.0531302864E-314;
        r7 = "EventLogChangedLinkedGroup";
        r6 = org.telegram.messenger.LocaleController.getString(r7, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        r6 = r0.messageText;
        r2 = r0.replaceWithLink(r6, r11, r2);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0abc:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden;
        if (r2 == 0) goto L_0x0ae8;
    L_0x0ac0:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden) r8;
        r2 = r8.new_value;
        if (r2 == 0) goto L_0x0ad7;
    L_0x0ac6:
        r2 = NUM; // 0x7f0d0453 float:1.874436E38 double:1.0531303245E-314;
        r6 = "EventLogToggledInvitesHistoryOff";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0ad7:
        r2 = NUM; // 0x7f0d0454 float:1.8744362E38 double:1.053130325E-314;
        r6 = "EventLogToggledInvitesHistoryOn";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0ae8:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout;
        if (r2 == 0) goto L_0x0b6a;
    L_0x0aec:
        r2 = r4.megagroup;
        if (r2 == 0) goto L_0x0af6;
    L_0x0af0:
        r2 = NUM; // 0x7f0d0413 float:1.874423E38 double:1.053130293E-314;
        r6 = "EventLogEditedGroupDescription";
        goto L_0x0afb;
    L_0x0af6:
        r2 = NUM; // 0x7f0d0410 float:1.8744224E38 double:1.0531302914E-314;
        r6 = "EventLogEditedChannelDescription";
    L_0x0afb:
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
        if (r2 != 0) goto L_0x0b61;
    L_0x0b2c:
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
        r7 = NUM; // 0x7f0d0431 float:1.8744291E38 double:1.0531303077E-314;
        r8 = "EventLogPreviousGroupDescription";
        r7 = org.telegram.messenger.LocaleController.getString(r8, r7);
        r2.site_name = r7;
        r2 = r6.media;
        r2 = r2.webpage;
        r7 = r1.action;
        r7 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout) r7;
        r7 = r7.prev_value;
        r2.description = r7;
        goto L_0x0e88;
    L_0x0b61:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r2.<init>();
        r6.media = r2;
        goto L_0x0e88;
    L_0x0b6a:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername;
        if (r2 == 0) goto L_0x0c6b;
    L_0x0b6e:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername) r8;
        r2 = r8.new_value;
        r6 = android.text.TextUtils.isEmpty(r2);
        if (r6 != 0) goto L_0x0b92;
    L_0x0b78:
        r6 = r4.megagroup;
        if (r6 == 0) goto L_0x0b82;
    L_0x0b7c:
        r6 = NUM; // 0x7f0d0404 float:1.87442E38 double:1.0531302854E-314;
        r8 = "EventLogChangedGroupLink";
        goto L_0x0b87;
    L_0x0b82:
        r6 = NUM; // 0x7f0d0403 float:1.8744198E38 double:1.053130285E-314;
        r8 = "EventLogChangedChannelLink";
    L_0x0b87:
        r6 = org.telegram.messenger.LocaleController.getString(r8, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
        goto L_0x0bab;
    L_0x0b92:
        r6 = r4.megagroup;
        if (r6 == 0) goto L_0x0b9c;
    L_0x0b96:
        r6 = NUM; // 0x7f0d0441 float:1.8744323E38 double:1.0531303156E-314;
        r8 = "EventLogRemovedGroupLink";
        goto L_0x0ba1;
    L_0x0b9c:
        r6 = NUM; // 0x7f0d043f float:1.874432E38 double:1.0531303146E-314;
        r8 = "EventLogRemovedChannelLink";
    L_0x0ba1:
        r6 = org.telegram.messenger.LocaleController.getString(r8, r6);
        r6 = r0.replaceWithLink(r6, r14, r5);
        r0.messageText = r6;
    L_0x0bab:
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
        if (r7 != 0) goto L_0x0be9;
    L_0x0bc5:
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
        goto L_0x0beb;
    L_0x0be9:
        r6.message = r13;
    L_0x0beb:
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
    L_0x0c0c:
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
        r7 = NUM; // 0x7f0d0432 float:1.8744293E38 double:1.053130308E-314;
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
        goto L_0x0e88;
    L_0x0CLASSNAME:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r2.<init>();
        r6.media = r2;
        goto L_0x0e88;
    L_0x0c6b:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage;
        if (r2 == 0) goto L_0x0db8;
    L_0x0c6f:
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
        if (r8 == 0) goto L_0x0d52;
    L_0x0CLASSNAME:
        r9 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
        if (r9 != 0) goto L_0x0d52;
    L_0x0CLASSNAME:
        r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r8 != 0) goto L_0x0d52;
    L_0x0c9a:
        r8 = r7.message;
        r9 = r2.message;
        r8 = android.text.TextUtils.equals(r8, r9);
        r9 = 1;
        r8 = r8 ^ r9;
        r9 = r7.media;
        r9 = r9.getClass();
        r10 = r2.media;
        r10 = r10.getClass();
        if (r9 != r10) goto L_0x0cdd;
    L_0x0cb2:
        r9 = r7.media;
        r9 = r9.photo;
        if (r9 == 0) goto L_0x0cc6;
    L_0x0cb8:
        r10 = r2.media;
        r10 = r10.photo;
        if (r10 == 0) goto L_0x0cc6;
    L_0x0cbe:
        r11 = r9.id;
        r9 = r10.id;
        r15 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1));
        if (r15 != 0) goto L_0x0cdd;
    L_0x0cc6:
        r9 = r7.media;
        r9 = r9.document;
        if (r9 == 0) goto L_0x0cdb;
    L_0x0ccc:
        r10 = r2.media;
        r10 = r10.document;
        if (r10 == 0) goto L_0x0cdb;
    L_0x0cd2:
        r11 = r9.id;
        r9 = r10.id;
        r15 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1));
        if (r15 == 0) goto L_0x0cdb;
    L_0x0cda:
        goto L_0x0cdd;
    L_0x0cdb:
        r9 = 0;
        goto L_0x0cde;
    L_0x0cdd:
        r9 = 1;
    L_0x0cde:
        if (r9 == 0) goto L_0x0cf2;
    L_0x0ce0:
        if (r8 == 0) goto L_0x0cf2;
    L_0x0ce2:
        r9 = NUM; // 0x7f0d0417 float:1.8744238E38 double:1.053130295E-314;
        r10 = "EventLogEditedMediaCaption";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r9 = r0.replaceWithLink(r9, r14, r5);
        r0.messageText = r9;
        goto L_0x0d13;
    L_0x0cf2:
        if (r8 == 0) goto L_0x0d04;
    L_0x0cf4:
        r9 = NUM; // 0x7f0d040f float:1.8744222E38 double:1.053130291E-314;
        r10 = "EventLogEditedCaption";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r9 = r0.replaceWithLink(r9, r14, r5);
        r0.messageText = r9;
        goto L_0x0d13;
    L_0x0d04:
        r9 = NUM; // 0x7f0d0416 float:1.8744236E38 double:1.0531302943E-314;
        r10 = "EventLogEditedMedia";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r9 = r0.replaceWithLink(r9, r14, r5);
        r0.messageText = r9;
    L_0x0d13:
        r9 = r7.media;
        r6.media = r9;
        if (r8 == 0) goto L_0x0da4;
    L_0x0d19:
        r8 = r6.media;
        r9 = new org.telegram.tgnet.TLRPC$TL_webPage;
        r9.<init>();
        r8.webpage = r9;
        r8 = r6.media;
        r8 = r8.webpage;
        r9 = NUM; // 0x7f0d042d float:1.8744283E38 double:1.0531303057E-314;
        r10 = "EventLogOriginalCaption";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r8.site_name = r9;
        r8 = r2.message;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 == 0) goto L_0x0d49;
    L_0x0d39:
        r2 = r6.media;
        r2 = r2.webpage;
        r8 = NUM; // 0x7f0d042e float:1.8744285E38 double:1.053130306E-314;
        r9 = "EventLogOriginalCaptionEmpty";
        r8 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r2.description = r8;
        goto L_0x0da4;
    L_0x0d49:
        r8 = r6.media;
        r8 = r8.webpage;
        r2 = r2.message;
        r8.description = r2;
        goto L_0x0da4;
    L_0x0d52:
        r8 = NUM; // 0x7f0d0418 float:1.874424E38 double:1.0531302953E-314;
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
        r9 = NUM; // 0x7f0d042f float:1.8744287E38 double:1.0531303067E-314;
        r10 = "EventLogOriginalMessages";
        r9 = org.telegram.messenger.LocaleController.getString(r10, r9);
        r8.site_name = r9;
        r8 = r2.message;
        r8 = android.text.TextUtils.isEmpty(r8);
        if (r8 == 0) goto L_0x0d9c;
    L_0x0d8c:
        r2 = r6.media;
        r2 = r2.webpage;
        r8 = NUM; // 0x7f0d042e float:1.8744285E38 double:1.053130306E-314;
        r9 = "EventLogOriginalCaptionEmpty";
        r8 = org.telegram.messenger.LocaleController.getString(r9, r8);
        r2.description = r8;
        goto L_0x0da4;
    L_0x0d9c:
        r8 = r6.media;
        r8 = r8.webpage;
        r2 = r2.message;
        r8.description = r2;
    L_0x0da4:
        r2 = r7.reply_markup;
        r6.reply_markup = r2;
        r2 = r6.media;
        r2 = r2.webpage;
        if (r2 == 0) goto L_0x0e88;
    L_0x0dae:
        r7 = 10;
        r2.flags = r7;
        r2.display_url = r13;
        r2.url = r13;
        goto L_0x0e88;
    L_0x0db8:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet;
        if (r2 == 0) goto L_0x0dee;
    L_0x0dbc:
        r2 = r8;
        r2 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet) r2;
        r2 = r2.new_stickerset;
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet) r8;
        r6 = r8.new_stickerset;
        if (r2 == 0) goto L_0x0ddd;
    L_0x0dc7:
        r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
        if (r2 == 0) goto L_0x0dcc;
    L_0x0dcb:
        goto L_0x0ddd;
    L_0x0dcc:
        r2 = NUM; // 0x7f0d0409 float:1.874421E38 double:1.053130288E-314;
        r6 = "EventLogChangedStickersSet";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0ddd:
        r2 = NUM; // 0x7f0d0445 float:1.8744332E38 double:1.0531303176E-314;
        r6 = "EventLogRemovedStickersSet";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0dee:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLocation;
        if (r2 == 0) goto L_0x0e25;
    L_0x0df2:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeLocation) r8;
        r2 = r8.new_value;
        r6 = r2 instanceof org.telegram.tgnet.TLRPC.TL_channelLocationEmpty;
        if (r6 == 0) goto L_0x0e0b;
    L_0x0dfa:
        r2 = NUM; // 0x7f0d0444 float:1.874433E38 double:1.053130317E-314;
        r6 = "EventLogRemovedLocation";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0e0b:
        r2 = (org.telegram.tgnet.TLRPC.TL_channelLocation) r2;
        r6 = NUM; // 0x7f0d0407 float:1.8744206E38 double:1.053130287E-314;
        r7 = 1;
        r8 = new java.lang.Object[r7];
        r2 = r2.address;
        r7 = 0;
        r8[r7] = r2;
        r2 = "EventLogChangedLocation";
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r8);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0e25:
        r2 = r8 instanceof org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSlowMode;
        if (r2 == 0) goto L_0x0e72;
    L_0x0e29:
        r8 = (org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSlowMode) r8;
        r2 = r8.new_value;
        if (r2 != 0) goto L_0x0e3f;
    L_0x0e2f:
        r2 = NUM; // 0x7f0d0459 float:1.8744372E38 double:1.0531303274E-314;
        r6 = "EventLogToggledSlowmodeOff";
        r2 = org.telegram.messenger.LocaleController.getString(r6, r2);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0e3f:
        if (r2 >= r12) goto L_0x0e48;
    L_0x0e41:
        r6 = "Seconds";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
        goto L_0x0e5c;
    L_0x0e48:
        r6 = 3600; // 0xe10 float:5.045E-42 double:1.7786E-320;
        if (r2 >= r6) goto L_0x0e54;
    L_0x0e4c:
        r2 = r2 / r12;
        r6 = "Minutes";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
        goto L_0x0e5c;
    L_0x0e54:
        r2 = r2 / r12;
        r2 = r2 / r12;
        r6 = "Hours";
        r2 = org.telegram.messenger.LocaleController.formatPluralString(r6, r2);
    L_0x0e5c:
        r6 = NUM; // 0x7f0d045a float:1.8744374E38 double:1.053130328E-314;
        r7 = 1;
        r8 = new java.lang.Object[r7];
        r7 = 0;
        r8[r7] = r2;
        r2 = "EventLogToggledSlowmodeOn";
        r2 = org.telegram.messenger.LocaleController.formatString(r2, r6, r8);
        r2 = r0.replaceWithLink(r2, r14, r5);
        r0.messageText = r2;
        goto L_0x0e87;
    L_0x0e72:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r6 = "unsupported ";
        r2.append(r6);
        r6 = r1.action;
        r2.append(r6);
        r2 = r2.toString();
        r0.messageText = r2;
    L_0x0e87:
        r6 = 0;
    L_0x0e88:
        r2 = r0.messageOwner;
        if (r2 != 0) goto L_0x0e93;
    L_0x0e8c:
        r2 = new org.telegram.tgnet.TLRPC$TL_messageService;
        r2.<init>();
        r0.messageOwner = r2;
    L_0x0e93:
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
        if (r7 == 0) goto L_0x0ed2;
    L_0x0ecb:
        r7 = r2.flags;
        r8 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r7 = r7 | r8;
        r2.flags = r7;
    L_0x0ed2:
        r2 = org.telegram.messenger.MediaController.getInstance();
        r7 = r1.action;
        r7 = r7.message;
        if (r7 == 0) goto L_0x0ee1;
    L_0x0edc:
        r8 = r7 instanceof org.telegram.tgnet.TLRPC.TL_messageEmpty;
        if (r8 != 0) goto L_0x0ee1;
    L_0x0ee0:
        r6 = r7;
    L_0x0ee1:
        if (r6 == 0) goto L_0x0var_;
    L_0x0ee3:
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
        if (r4 == 0) goto L_0x0var_;
    L_0x0efc:
        r4 = r6.flags;
        r7 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r4 = r4 | r7;
        r6.flags = r4;
    L_0x0var_:
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
        if (r6 < 0) goto L_0x0f3e;
    L_0x0f1b:
        r6 = r2.isPlayingMessage(r4);
        if (r6 == 0) goto L_0x0f2d;
    L_0x0var_:
        r6 = r2.getPlayingMessageObject();
        r7 = r6.audioProgress;
        r4.audioProgress = r7;
        r6 = r6.audioProgressSec;
        r4.audioProgressSec = r6;
    L_0x0f2d:
        r6 = r0.currentAccount;
        r7 = r28;
        r0.createDateArray(r6, r1, r7, r3);
        r6 = r28.size();
        r8 = 1;
        r6 = r6 - r8;
        r7.add(r6, r4);
        goto L_0x0var_;
    L_0x0f3e:
        r7 = r28;
        r4 = -1;
        r0.contentType = r4;
        goto L_0x0var_;
    L_0x0var_:
        r7 = r28;
    L_0x0var_:
        r4 = r0.contentType;
        if (r4 < 0) goto L_0x0fb2;
    L_0x0f4a:
        r4 = r0.currentAccount;
        r0.createDateArray(r4, r1, r7, r3);
        r1 = r28.size();
        r3 = 1;
        r1 = r1 - r3;
        r7.add(r1, r0);
        r1 = r0.messageText;
        if (r1 != 0) goto L_0x0f5e;
    L_0x0f5c:
        r0.messageText = r13;
    L_0x0f5e:
        r25.setType();
        r25.measureInlineBotButtons();
        r25.generateCaption();
        r1 = r0.messageOwner;
        r1 = r1.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 == 0) goto L_0x0var_;
    L_0x0f6f:
        r1 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint;
        goto L_0x0var_;
    L_0x0var_:
        r1 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;
    L_0x0var_:
        r3 = org.telegram.messenger.SharedConfig.allowBigEmoji;
        if (r3 == 0) goto L_0x0f7c;
    L_0x0var_:
        r3 = 1;
        r6 = new int[r3];
        goto L_0x0f7d;
    L_0x0f7c:
        r6 = 0;
    L_0x0f7d:
        r3 = r0.messageText;
        r1 = r1.getFontMetricsInt();
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r7 = 0;
        r1 = org.telegram.messenger.Emoji.replaceEmoji(r3, r1, r4, r7, r6);
        r0.messageText = r1;
        r0.checkEmojiOnly(r6);
        r1 = r2.isPlayingMessage(r0);
        if (r1 == 0) goto L_0x0fa5;
    L_0x0var_:
        r1 = r2.getPlayingMessageObject();
        r2 = r1.audioProgress;
        r0.audioProgress = r2;
        r1 = r1.audioProgressSec;
        r0.audioProgressSec = r1;
    L_0x0fa5:
        r0.generateLayout(r5);
        r1 = 1;
        r0.layoutCreated = r1;
        r1 = 0;
        r0.generateThumbs(r1);
        r25.checkMediaExistance();
    L_0x0fb2:
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
            if (SharedConfig.allowBigEmoji) {
                iArr = new int[1];
            }
            this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
            checkEmojiOnly(iArr);
            generateLayout(user);
        }
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

    public void measureInlineBotButtons() {
        this.wantedBotKeyboardWidth = 0;
        if (this.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
            Theme.createChatResources(null, true);
            StringBuilder stringBuilder = this.botButtonsLayout;
            if (stringBuilder == null) {
                this.botButtonsLayout = new StringBuilder();
            } else {
                stringBuilder.setLength(0);
            }
            for (int i = 0; i < this.messageOwner.reply_markup.rows.size(); i++) {
                TL_keyboardButtonRow tL_keyboardButtonRow = (TL_keyboardButtonRow) this.messageOwner.reply_markup.rows.get(i);
                int size = tL_keyboardButtonRow.buttons.size();
                int i2 = 0;
                for (int i3 = 0; i3 < size; i3++) {
                    String replaceEmoji;
                    KeyboardButton keyboardButton = (KeyboardButton) tL_keyboardButtonRow.buttons.get(i3);
                    StringBuilder stringBuilder2 = this.botButtonsLayout;
                    stringBuilder2.append(i);
                    stringBuilder2.append(i3);
                    if (!(keyboardButton instanceof TL_keyboardButtonBuy) || (this.messageOwner.media.flags & 4) == 0) {
                        replaceEmoji = Emoji.replaceEmoji(keyboardButton.text, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                    } else {
                        replaceEmoji = LocaleController.getString("PaymentReceipt", NUM);
                    }
                    StaticLayout staticLayout = new StaticLayout(replaceEmoji, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (staticLayout.getLineCount() > 0) {
                        float lineWidth = staticLayout.getLineWidth(0);
                        float lineLeft = staticLayout.getLineLeft(0);
                        if (lineLeft < lineWidth) {
                            lineWidth -= lineLeft;
                        }
                        i2 = Math.max(i2, ((int) Math.ceil((double) lineWidth)) + AndroidUtilities.dp(4.0f));
                    }
                }
                this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((i2 + AndroidUtilities.dp(12.0f)) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
            }
        }
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
        if (this.messageText == null) {
            this.messageText = str;
        }
    }

    public void setType() {
        int i = this.type;
        this.isRoundVideoCached = 0;
        Message message = this.messageOwner;
        if ((message instanceof TL_message) || (message instanceof TL_messageForwarded_old2)) {
            if (this.emojiAnimatedSticker != null) {
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
                    if (SharedConfig.allowBigEmoji) {
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
                    if (documentFileName.startsWith("tg_secret_sticker") && documentFileName.endsWith("json")) {
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
                        i2 = i;
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
                        i2 = i;
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
                return;
            }
            return;
        }
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
    }

    private static void updatePhotoSizeLocations(ArrayList<PhotoSize> arrayList, ArrayList<PhotoSize> arrayList2) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            PhotoSize photoSize = (PhotoSize) arrayList.get(i);
            int size2 = arrayList2.size();
            for (int i2 = 0; i2 < size2; i2++) {
                PhotoSize photoSize2 = (PhotoSize) arrayList2.get(i2);
                if (!(photoSize2 instanceof TL_photoSizeEmpty) && !(photoSize2 instanceof TL_photoCachedSize) && photoSize2.type.equals(photoSize.type)) {
                    photoSize.location = photoSize2.location;
                    break;
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

    public int getFileType() {
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

    /* JADX WARNING: Removed duplicated region for block: B:31:0x0093  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0093  */
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
        if (r1 == 0) goto L_0x004d;
    L_0x000f:
        r0 = r0.webpage;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r1 == 0) goto L_0x004d;
    L_0x0015:
        r0 = r0.description;
        if (r0 == 0) goto L_0x004d;
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
        goto L_0x008b;
    L_0x0043:
        r1 = "twitter";
        r0 = r1.equals(r0);
        if (r0 == 0) goto L_0x008a;
    L_0x004b:
        r0 = 2;
        goto L_0x008b;
    L_0x004d:
        r0 = r5.messageOwner;
        r0 = r0.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 == 0) goto L_0x006e;
    L_0x0055:
        r0 = r0.game;
        r0 = r0.description;
        if (r0 == 0) goto L_0x006e;
    L_0x005b:
        r0 = android.text.Spannable.Factory.getInstance();
        r1 = r5.messageOwner;
        r1 = r1.media;
        r1 = r1.game;
        r1 = r1.description;
        r0 = r0.newSpannable(r1);
        r5.linkDescription = r0;
        goto L_0x008a;
    L_0x006e:
        r0 = r5.messageOwner;
        r0 = r0.media;
        r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
        if (r1 == 0) goto L_0x008a;
    L_0x0076:
        r0 = r0.description;
        if (r0 == 0) goto L_0x008a;
    L_0x007a:
        r0 = android.text.Spannable.Factory.getInstance();
        r1 = r5.messageOwner;
        r1 = r1.media;
        r1 = r1.description;
        r0 = r0.newSpannable(r1);
        r5.linkDescription = r0;
    L_0x008a:
        r0 = 0;
    L_0x008b:
        r1 = r5.linkDescription;
        r1 = android.text.TextUtils.isEmpty(r1);
        if (r1 != 0) goto L_0x00d3;
    L_0x0093:
        r1 = r5.linkDescription;
        r1 = containsUrls(r1);
        if (r1 == 0) goto L_0x00a7;
    L_0x009b:
        r1 = r5.linkDescription;	 Catch:{ Exception -> 0x00a3 }
        r1 = (android.text.Spannable) r1;	 Catch:{ Exception -> 0x00a3 }
        android.text.util.Linkify.addLinks(r1, r2);	 Catch:{ Exception -> 0x00a3 }
        goto L_0x00a7;
    L_0x00a3:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
    L_0x00a7:
        r1 = r5.linkDescription;
        r2 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;
        r2 = r2.getFontMetricsInt();
        r4 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
        r4 = org.telegram.messenger.AndroidUtilities.dp(r4);
        r1 = org.telegram.messenger.Emoji.replaceEmoji(r1, r2, r4, r3);
        r5.linkDescription = r1;
        if (r0 == 0) goto L_0x00d3;
    L_0x00bd:
        r1 = r5.linkDescription;
        r2 = r1 instanceof android.text.Spannable;
        if (r2 != 0) goto L_0x00ca;
    L_0x00c3:
        r2 = new android.text.SpannableStringBuilder;
        r2.<init>(r1);
        r5.linkDescription = r2;
    L_0x00ca:
        r1 = r5.isOutOwner();
        r2 = r5.linkDescription;
        addUsernamesAndHashtags(r1, r2, r3, r0);
    L_0x00d3:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLinkDescription():void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:52:0x00c1 A:{SYNTHETIC, Splitter:B:52:0x00c1} */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00a2  */
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
        if (r0 != 0) goto L_0x00d3;
    L_0x0004:
        r0 = r7.isRoundVideo();
        if (r0 == 0) goto L_0x000c;
    L_0x000a:
        goto L_0x00d3;
    L_0x000c:
        r0 = r7.isMediaEmpty();
        if (r0 != 0) goto L_0x00d3;
    L_0x0012:
        r0 = r7.messageOwner;
        r1 = r0.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r1 != 0) goto L_0x00d3;
    L_0x001a:
        r0 = r0.message;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 != 0) goto L_0x00d3;
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
        if (r0 == 0) goto L_0x00c1;
    L_0x00a2:
        r1 = r7.caption;
        r1 = containsUrls(r1);
        if (r1 == 0) goto L_0x00b7;
    L_0x00aa:
        r1 = r7.caption;	 Catch:{ Exception -> 0x00b3 }
        r1 = (android.text.Spannable) r1;	 Catch:{ Exception -> 0x00b3 }
        r4 = 5;
        android.text.util.Linkify.addLinks(r1, r4);	 Catch:{ Exception -> 0x00b3 }
        goto L_0x00b7;
    L_0x00b3:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
    L_0x00b7:
        r1 = r7.isOutOwner();
        r4 = r7.caption;
        addUsernamesAndHashtags(r1, r4, r2, r3);
        goto L_0x00ce;
    L_0x00c1:
        r1 = r7.caption;	 Catch:{ Throwable -> 0x00ca }
        r1 = (android.text.Spannable) r1;	 Catch:{ Throwable -> 0x00ca }
        r2 = 4;
        android.text.util.Linkify.addLinks(r1, r2);	 Catch:{ Throwable -> 0x00ca }
        goto L_0x00ce;
    L_0x00ca:
        r1 = move-exception;
        org.telegram.messenger.FileLog.e(r1);
    L_0x00ce:
        r1 = r7.caption;
        r7.addEntitiesToText(r1, r0);
    L_0x00d3:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateCaption():void");
    }

    private static void addUsernamesAndHashtags(boolean z, CharSequence charSequence, boolean z2, int i) {
        Matcher matcher;
        if (i == 1) {
            try {
                if (instagramUrlPattern == null) {
                    instagramUrlPattern = Pattern.compile("(^|\\s|\\()@[a-zA-Z\\d_.]{1,32}|(^|\\s|\\()#[\\w.]+");
                }
                matcher = instagramUrlPattern.matcher(charSequence);
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        if (urlPattern == null) {
            urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s|\\()@[a-zA-Z\\d_]{1,32}|(^|\\s|\\()#[\\w.]+|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)");
        }
        matcher = urlPattern.matcher(charSequence);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            char charAt = charSequence.charAt(start);
            if (i != 0) {
                if (!(charAt == '@' || charAt == '#')) {
                    start++;
                }
                charAt = charSequence.charAt(start);
                if (!(charAt == '@' || charAt == '#')) {
                }
            } else if (!(charAt == '@' || charAt == '#' || charAt == '/' || charAt == '$')) {
                start++;
            }
            Object obj = null;
            StringBuilder stringBuilder;
            if (i == 1) {
                if (charAt == '@') {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("https://instagram.com/");
                    stringBuilder.append(charSequence.subSequence(start + 1, end).toString());
                    obj = new URLSpanNoUnderline(stringBuilder.toString());
                } else if (charAt == '#') {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("https://www.instagram.com/explore/tags/");
                    stringBuilder.append(charSequence.subSequence(start + 1, end).toString());
                    obj = new URLSpanNoUnderline(stringBuilder.toString());
                }
            } else if (i == 2) {
                if (charAt == '@') {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("https://twitter.com/");
                    stringBuilder.append(charSequence.subSequence(start + 1, end).toString());
                    obj = new URLSpanNoUnderline(stringBuilder.toString());
                } else if (charAt == '#') {
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("https://twitter.com/hashtag/");
                    stringBuilder.append(charSequence.subSequence(start + 1, end).toString());
                    obj = new URLSpanNoUnderline(stringBuilder.toString());
                }
            } else if (charSequence.charAt(start) != '/') {
                obj = new URLSpanNoUnderline(charSequence.subSequence(start, end).toString());
            } else if (z2) {
                obj = new URLSpanBotCommand(charSequence.subSequence(start, end).toString(), z ? 1 : 0);
            }
            if (obj != null) {
                ((Spannable) charSequence).setSpan(obj, start, end, 0);
            }
        }
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
                    Linkify.addLinks((Spannable) charSequence, 5);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else {
                try {
                    Linkify.addLinks((Spannable) charSequence, 1);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            addUsernamesAndHashtags(z, charSequence, z2, 0);
        }
    }

    public void resetPlayingProgress() {
        this.audioProgress = 0.0f;
        this.audioProgressSec = 0;
        this.bufferedProgress = 0.0f;
    }

    private boolean addEntitiesToText(CharSequence charSequence, boolean z) {
        return addEntitiesToText(charSequence, this.messageOwner.entities, isOutOwner(), this.type, true, false, z);
    }

    public boolean addEntitiesToText(CharSequence charSequence, boolean z, boolean z2) {
        return addEntitiesToText(charSequence, this.messageOwner.entities, isOutOwner(), this.type, true, z, z2);
    }

    /* JADX WARNING: Removed duplicated region for block: B:108:0x0161  */
    /* JADX WARNING: Removed duplicated region for block: B:186:0x0201 A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:131:0x01fc  */
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
        if (r11 >= r10) goto L_0x0206;
    L_0x0048:
        r14 = r9.get(r11);
        r14 = (org.telegram.tgnet.TLRPC.MessageEntity) r14;
        r15 = r14.length;
        if (r15 <= 0) goto L_0x0200;
    L_0x0052:
        r15 = r14.offset;
        if (r15 < 0) goto L_0x0200;
    L_0x0056:
        r2 = r16.length();
        if (r15 < r2) goto L_0x005e;
    L_0x005c:
        goto L_0x0200;
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
        if (r22 == 0) goto L_0x0098;
    L_0x0074:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBold;
        if (r2 != 0) goto L_0x0098;
    L_0x0078:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
        if (r2 != 0) goto L_0x0098;
    L_0x007c:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityStrike;
        if (r2 != 0) goto L_0x0098;
    L_0x0080:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUnderline;
        if (r2 != 0) goto L_0x0098;
    L_0x0084:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBlockquote;
        if (r2 != 0) goto L_0x0098;
    L_0x0088:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityCode;
        if (r2 != 0) goto L_0x0098;
    L_0x008c:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityPre;
        if (r2 != 0) goto L_0x0098;
    L_0x0090:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r2 != 0) goto L_0x0098;
    L_0x0094:
        r2 = r14 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
        if (r2 == 0) goto L_0x00ce;
    L_0x0098:
        if (r3 == 0) goto L_0x00ce;
    L_0x009a:
        r2 = r3.length;
        if (r2 <= 0) goto L_0x00ce;
    L_0x009d:
        r2 = 0;
    L_0x009e:
        r15 = r3.length;
        if (r2 >= r15) goto L_0x00ce;
    L_0x00a1:
        r15 = r3[r2];
        if (r15 != 0) goto L_0x00a6;
    L_0x00a5:
        goto L_0x00cb;
    L_0x00a6:
        r15 = r3[r2];
        r15 = r1.getSpanStart(r15);
        r12 = r3[r2];
        r12 = r1.getSpanEnd(r12);
        r6 = r14.offset;
        if (r6 > r15) goto L_0x00bb;
    L_0x00b6:
        r4 = r14.length;
        r6 = r6 + r4;
        if (r6 >= r15) goto L_0x00c4;
    L_0x00bb:
        r4 = r14.offset;
        if (r4 > r12) goto L_0x00cb;
    L_0x00bf:
        r6 = r14.length;
        r4 = r4 + r6;
        if (r4 < r12) goto L_0x00cb;
    L_0x00c4:
        r4 = r3[r2];
        r1.removeSpan(r4);
        r3[r2] = r13;
    L_0x00cb:
        r2 = r2 + 1;
        goto L_0x009e;
    L_0x00ce:
        r2 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;
        r2.<init>();
        r4 = r14.offset;
        r2.start = r4;
        r4 = r2.start;
        r6 = r14.length;
        r4 = r4 + r6;
        r2.end = r4;
        r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityStrike;
        if (r4 == 0) goto L_0x00e9;
    L_0x00e2:
        r4 = 8;
        r2.flags = r4;
    L_0x00e6:
        r4 = 2;
        goto L_0x0159;
    L_0x00e9:
        r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUnderline;
        if (r4 == 0) goto L_0x00f2;
    L_0x00ed:
        r4 = 16;
        r2.flags = r4;
        goto L_0x00e6;
    L_0x00f2:
        r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBlockquote;
        if (r4 == 0) goto L_0x00fb;
    L_0x00f6:
        r4 = 32;
        r2.flags = r4;
        goto L_0x00e6;
    L_0x00fb:
        r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBold;
        if (r4 == 0) goto L_0x0103;
    L_0x00ff:
        r4 = 1;
        r2.flags = r4;
        goto L_0x00e6;
    L_0x0103:
        r4 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
        if (r4 == 0) goto L_0x010b;
    L_0x0107:
        r4 = 2;
        r2.flags = r4;
        goto L_0x0159;
    L_0x010b:
        r4 = 2;
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityCode;
        if (r6 != 0) goto L_0x0156;
    L_0x0110:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityPre;
        if (r6 == 0) goto L_0x0115;
    L_0x0114:
        goto L_0x0156;
    L_0x0115:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        r12 = 64;
        if (r6 == 0) goto L_0x0124;
    L_0x011b:
        if (r20 != 0) goto L_0x011f;
    L_0x011d:
        goto L_0x0200;
    L_0x011f:
        r2.flags = r12;
        r2.urlEntity = r14;
        goto L_0x0159;
    L_0x0124:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
        if (r6 == 0) goto L_0x0131;
    L_0x0128:
        if (r20 != 0) goto L_0x012c;
    L_0x012a:
        goto L_0x0200;
    L_0x012c:
        r2.flags = r12;
        r2.urlEntity = r14;
        goto L_0x0159;
    L_0x0131:
        if (r22 == 0) goto L_0x0135;
    L_0x0133:
        goto L_0x0200;
    L_0x0135:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
        if (r6 != 0) goto L_0x013d;
    L_0x0139:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
        if (r6 == 0) goto L_0x0147;
    L_0x013d:
        r6 = r14.url;
        r6 = org.telegram.messenger.browser.Browser.isPassportUrl(r6);
        if (r6 == 0) goto L_0x0147;
    L_0x0145:
        goto L_0x0200;
    L_0x0147:
        r6 = r14 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMention;
        if (r6 == 0) goto L_0x014f;
    L_0x014b:
        if (r20 != 0) goto L_0x014f;
    L_0x014d:
        goto L_0x0200;
    L_0x014f:
        r6 = 128; // 0x80 float:1.794E-43 double:6.32E-322;
        r2.flags = r6;
        r2.urlEntity = r14;
        goto L_0x0159;
    L_0x0156:
        r6 = 4;
        r2.flags = r6;
    L_0x0159:
        r6 = r8.size();
        r12 = r6;
        r6 = 0;
    L_0x015f:
        if (r6 >= r12) goto L_0x01f5;
    L_0x0161:
        r13 = r8.get(r6);
        r13 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r13;
        r14 = r2.start;
        r15 = r13.start;
        if (r14 <= r15) goto L_0x01b3;
    L_0x016d:
        r15 = r13.end;
        if (r14 < r15) goto L_0x0172;
    L_0x0171:
        goto L_0x01b7;
    L_0x0172:
        r14 = r2.end;
        if (r14 >= r15) goto L_0x0195;
    L_0x0176:
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
        goto L_0x01aa;
    L_0x0195:
        if (r14 < r15) goto L_0x01aa;
    L_0x0197:
        r14 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;
        r14.<init>(r2);
        r14.merge(r13);
        r15 = r13.end;
        r14.end = r15;
        r6 = r6 + 1;
        r12 = r12 + 1;
        r8.add(r6, r14);
    L_0x01aa:
        r14 = r2.start;
        r15 = r13.end;
        r2.start = r15;
        r13.end = r14;
        goto L_0x01b7;
    L_0x01b3:
        r14 = r2.end;
        if (r15 < r14) goto L_0x01b9;
    L_0x01b7:
        r4 = 1;
        goto L_0x01f1;
    L_0x01b9:
        r4 = r13.end;
        if (r14 != r4) goto L_0x01c1;
    L_0x01bd:
        r13.merge(r2);
        goto L_0x01ee;
    L_0x01c1:
        if (r14 >= r4) goto L_0x01db;
    L_0x01c3:
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
        goto L_0x01ee;
    L_0x01db:
        r4 = new org.telegram.ui.Components.TextStyleSpan$TextStyleRun;
        r4.<init>(r2);
        r14 = r13.end;
        r4.start = r14;
        r6 = r6 + 1;
        r12 = r12 + 1;
        r8.add(r6, r4);
        r13.merge(r2);
    L_0x01ee:
        r2.end = r15;
        goto L_0x01b7;
    L_0x01f1:
        r6 = r6 + r4;
        r4 = 2;
        goto L_0x015f;
    L_0x01f5:
        r4 = 1;
        r6 = r2.start;
        r12 = r2.end;
        if (r6 >= r12) goto L_0x0201;
    L_0x01fc:
        r8.add(r2);
        goto L_0x0201;
    L_0x0200:
        r4 = 1;
    L_0x0201:
        r11 = r11 + 1;
        r2 = 0;
        goto L_0x0045;
    L_0x0206:
        r4 = 1;
        r2 = r8.size();
        r3 = 0;
    L_0x020c:
        if (r3 >= r2) goto L_0x0395;
    L_0x020e:
        r6 = r8.get(r3);
        r6 = (org.telegram.ui.Components.TextStyleSpan.TextStyleRun) r6;
        r9 = r6.urlEntity;
        if (r9 == 0) goto L_0x0222;
    L_0x0218:
        r10 = r9.offset;
        r9 = r9.length;
        r9 = r9 + r10;
        r9 = android.text.TextUtils.substring(r0, r10, r9);
        goto L_0x0223;
    L_0x0222:
        r9 = r13;
    L_0x0223:
        r10 = r6.urlEntity;
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityBotCommand;
        r12 = 33;
        if (r11 == 0) goto L_0x0239;
    L_0x022b:
        r10 = new org.telegram.ui.Components.URLSpanBotCommand;
        r10.<init>(r9, r7, r6);
        r9 = r6.start;
        r6 = r6.end;
        r1.setSpan(r10, r9, r6, r12);
        goto L_0x02b0;
    L_0x0239:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityHashtag;
        if (r11 != 0) goto L_0x0384;
    L_0x023d:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMention;
        if (r11 != 0) goto L_0x0384;
    L_0x0241:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityCashtag;
        if (r11 == 0) goto L_0x0247;
    L_0x0245:
        goto L_0x0384;
    L_0x0247:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
        if (r11 == 0) goto L_0x0269;
    L_0x024b:
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
        goto L_0x02b0;
    L_0x0269:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
        if (r11 == 0) goto L_0x02b3;
    L_0x026d:
        r5 = r9.toLowerCase();
        r10 = "http";
        r5 = r5.startsWith(r10);
        if (r5 != 0) goto L_0x02a3;
    L_0x0279:
        r5 = r9.toLowerCase();
        r10 = "tg://";
        r5 = r5.startsWith(r10);
        if (r5 != 0) goto L_0x02a3;
    L_0x0285:
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
        goto L_0x02af;
    L_0x02a3:
        r5 = new org.telegram.ui.Components.URLSpanBrowser;
        r5.<init>(r9, r6);
        r9 = r6.start;
        r6 = r6.end;
        r1.setSpan(r5, r9, r6, r12);
    L_0x02af:
        r5 = 1;
    L_0x02b0:
        r10 = 4;
        goto L_0x0391;
    L_0x02b3:
        r11 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityPhone;
        if (r11 == 0) goto L_0x02f0;
    L_0x02b7:
        r5 = org.telegram.PhoneFormat.PhoneFormat.stripExceptNumbers(r9);
        r10 = "+";
        r9 = r9.startsWith(r10);
        if (r9 == 0) goto L_0x02d2;
    L_0x02c3:
        r9 = new java.lang.StringBuilder;
        r9.<init>();
        r9.append(r10);
        r9.append(r5);
        r5 = r9.toString();
    L_0x02d2:
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
        goto L_0x02af;
    L_0x02f0:
        r9 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
        if (r9 == 0) goto L_0x0303;
    L_0x02f4:
        r9 = new org.telegram.ui.Components.URLSpanReplacement;
        r10 = r10.url;
        r9.<init>(r10, r6);
        r10 = r6.start;
        r6 = r6.end;
        r1.setSpan(r9, r10, r6, r12);
        goto L_0x02b0;
    L_0x0303:
        r9 = r10 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        r11 = "";
        if (r9 == 0) goto L_0x032b;
    L_0x0309:
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
        goto L_0x02b0;
    L_0x032b:
        r9 = r10 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
        if (r9 == 0) goto L_0x0354;
    L_0x032f:
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
        goto L_0x02b0;
    L_0x0354:
        r9 = r6.flags;
        r10 = 4;
        r9 = r9 & r10;
        if (r9 == 0) goto L_0x0377;
    L_0x035a:
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
        goto L_0x0391;
    L_0x0377:
        r9 = new org.telegram.ui.Components.TextStyleSpan;
        r9.<init>(r6);
        r11 = r6.start;
        r6 = r6.end;
        r1.setSpan(r9, r11, r6, r12);
        goto L_0x0391;
    L_0x0384:
        r10 = 4;
        r11 = new org.telegram.ui.Components.URLSpanNoUnderline;
        r11.<init>(r9, r6);
        r9 = r6.start;
        r6 = r6.end;
        r1.setSpan(r11, r9, r6, r12);
    L_0x0391:
        r3 = r3 + 1;
        goto L_0x020c;
    L_0x0395:
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

    /* JADX WARNING: Missing block: B:54:0x00c7, code skipped:
            if ((r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo) == false) goto L_0x00cb;
     */
    public boolean needDrawShareButton() {
        /*
        r7 = this;
        r0 = r7.eventId;
        r2 = 0;
        r3 = 0;
        r5 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r5 == 0) goto L_0x000a;
    L_0x0009:
        return r2;
    L_0x000a:
        r0 = r7.messageOwner;
        r0 = r0.fwd_from;
        r1 = 1;
        if (r0 == 0) goto L_0x0033;
    L_0x0011:
        r0 = r7.isOutOwner();
        if (r0 != 0) goto L_0x0033;
    L_0x0017:
        r0 = r7.messageOwner;
        r0 = r0.fwd_from;
        r0 = r0.saved_from_peer;
        if (r0 == 0) goto L_0x0033;
    L_0x001f:
        r3 = r7.getDialogId();
        r0 = r7.currentAccount;
        r0 = org.telegram.messenger.UserConfig.getInstance(r0);
        r0 = r0.getClientUserId();
        r5 = (long) r0;
        r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1));
        if (r0 != 0) goto L_0x0033;
    L_0x0032:
        return r1;
    L_0x0033:
        r0 = r7.type;
        r3 = 13;
        if (r0 == r3) goto L_0x00ee;
    L_0x0039:
        r4 = 15;
        if (r0 != r4) goto L_0x003f;
    L_0x003d:
        goto L_0x00ee;
    L_0x003f:
        r0 = r7.messageOwner;
        r0 = r0.fwd_from;
        if (r0 == 0) goto L_0x0050;
    L_0x0045:
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x0050;
    L_0x0049:
        r0 = r7.isOutOwner();
        if (r0 != 0) goto L_0x0050;
    L_0x004f:
        return r1;
    L_0x0050:
        r0 = r7.isFromUser();
        if (r0 == 0) goto L_0x00cd;
    L_0x0056:
        r0 = r7.messageOwner;
        r0 = r0.media;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
        if (r3 != 0) goto L_0x00cc;
    L_0x005e:
        if (r0 == 0) goto L_0x00cc;
    L_0x0060:
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r3 == 0) goto L_0x006b;
    L_0x0064:
        r0 = r0.webpage;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_webPage;
        if (r0 != 0) goto L_0x006b;
    L_0x006a:
        goto L_0x00cc;
    L_0x006b:
        r0 = r7.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r3 = r7.messageOwner;
        r3 = r3.from_id;
        r3 = java.lang.Integer.valueOf(r3);
        r0 = r0.getUser(r3);
        if (r0 == 0) goto L_0x0084;
    L_0x007f:
        r0 = r0.bot;
        if (r0 == 0) goto L_0x0084;
    L_0x0083:
        return r1;
    L_0x0084:
        r0 = r7.isOut();
        if (r0 != 0) goto L_0x00ee;
    L_0x008a:
        r0 = r7.messageOwner;
        r0 = r0.media;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r3 != 0) goto L_0x00cb;
    L_0x0092:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
        if (r0 == 0) goto L_0x0097;
    L_0x0096:
        goto L_0x00cb;
    L_0x0097:
        r0 = r7.isMegagroup();
        if (r0 == 0) goto L_0x00ee;
    L_0x009d:
        r0 = r7.currentAccount;
        r0 = org.telegram.messenger.MessagesController.getInstance(r0);
        r3 = r7.messageOwner;
        r3 = r3.to_id;
        r3 = r3.channel_id;
        r3 = java.lang.Integer.valueOf(r3);
        r0 = r0.getChat(r3);
        if (r0 == 0) goto L_0x00ca;
    L_0x00b3:
        r0 = r0.username;
        if (r0 == 0) goto L_0x00ca;
    L_0x00b7:
        r0 = r0.length();
        if (r0 <= 0) goto L_0x00ca;
    L_0x00bd:
        r0 = r7.messageOwner;
        r0 = r0.media;
        r3 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r3 != 0) goto L_0x00ca;
    L_0x00c5:
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
        if (r0 != 0) goto L_0x00ca;
    L_0x00c9:
        goto L_0x00cb;
    L_0x00ca:
        r1 = 0;
    L_0x00cb:
        return r1;
    L_0x00cc:
        return r2;
    L_0x00cd:
        r0 = r7.messageOwner;
        r5 = r0.from_id;
        if (r5 < 0) goto L_0x00d7;
    L_0x00d3:
        r0 = r0.post;
        if (r0 == 0) goto L_0x00ee;
    L_0x00d7:
        r0 = r7.messageOwner;
        r5 = r0.to_id;
        r5 = r5.channel_id;
        if (r5 == 0) goto L_0x00ee;
    L_0x00df:
        r5 = r0.via_bot_id;
        if (r5 != 0) goto L_0x00e7;
    L_0x00e3:
        r0 = r0.reply_to_msg_id;
        if (r0 == 0) goto L_0x00ed;
    L_0x00e7:
        r0 = r7.type;
        if (r0 == r3) goto L_0x00ee;
    L_0x00eb:
        if (r0 == r4) goto L_0x00ee;
    L_0x00ed:
        return r1;
    L_0x00ee:
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.needDrawShareButton():boolean");
    }

    public int getMaxMessageTextWidth() {
        int dp;
        if (!AndroidUtilities.isTablet() || this.eventId == 0) {
            this.generatedWithMinSize = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x;
        } else {
            this.generatedWithMinSize = AndroidUtilities.dp(530.0f);
        }
        this.generatedWithDensity = AndroidUtilities.density;
        MessageMedia messageMedia = this.messageOwner.media;
        int i = 0;
        if (messageMedia instanceof TL_messageMediaWebPage) {
            WebPage webPage = messageMedia.webpage;
            if (webPage != null) {
                if ("telegram_background".equals(webPage.type)) {
                    try {
                        Uri parse = Uri.parse(this.messageOwner.media.webpage.url);
                        if (parse.getQueryParameter("bg_color") != null) {
                            dp = AndroidUtilities.dp(220.0f);
                        } else if (parse.getLastPathSegment().length() == 6) {
                            dp = AndroidUtilities.dp(200.0f);
                        }
                        i = dp;
                    } catch (Exception unused) {
                    }
                }
            }
        }
        if (i != 0) {
            return i;
        }
        dp = this.generatedWithMinSize;
        float f = (!needDrawAvatarInternal() || isOutOwner()) ? 80.0f : 132.0f;
        dp -= AndroidUtilities.dp(f);
        if (needDrawShareButton() && !isOutOwner()) {
            dp -= AndroidUtilities.dp(10.0f);
        }
        return this.messageOwner.media instanceof TL_messageMediaGame ? dp - AndroidUtilities.dp(10.0f) : dp;
    }

    /* JADX WARNING: Removed duplicated region for block: B:99:0x0237 A:{Catch:{ Exception -> 0x03d4 }} */
    /* JADX WARNING: Removed duplicated region for block: B:103:0x025c  */
    /* JADX WARNING: Removed duplicated region for block: B:117:0x0297  */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x028d  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02bb  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x02c0  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0384  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x02cd  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x007e  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x00e0 A:{Catch:{ Exception -> 0x040f }} */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x00c1 A:{Catch:{ Exception -> 0x040f }} */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0101  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0117  */
    /* JADX WARNING: Removed duplicated region for block: B:129:0x02bb  */
    /* JADX WARNING: Removed duplicated region for block: B:132:0x02c0  */
    /* JADX WARNING: Removed duplicated region for block: B:135:0x02cd  */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0384  */
    /* JADX WARNING: Missing block: B:36:0x0077, code skipped:
            if ((r0.media instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported) == false) goto L_0x007b;
     */
    public void generateLayout(org.telegram.tgnet.TLRPC.User r31) {
        /*
        r30 = this;
        r1 = r30;
        r0 = r1.type;
        if (r0 != 0) goto L_0x0413;
    L_0x0006:
        r0 = r1.messageOwner;
        r0 = r0.to_id;
        if (r0 == 0) goto L_0x0413;
    L_0x000c:
        r0 = r1.messageText;
        r0 = android.text.TextUtils.isEmpty(r0);
        if (r0 == 0) goto L_0x0016;
    L_0x0014:
        goto L_0x0413;
    L_0x0016:
        r30.generateLinkDescription();
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
        r0 = r30.isOut();
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
        if (r3 == 0) goto L_0x0088;
    L_0x007e:
        r0 = r30.isOutOwner();
        r5 = r1.messageText;
        addLinks(r0, r5);
        goto L_0x00a3;
    L_0x0088:
        r0 = r1.messageText;
        r5 = r0 instanceof android.text.Spannable;
        if (r5 == 0) goto L_0x00a3;
    L_0x008e:
        r0 = r0.length();
        r5 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        if (r0 >= r5) goto L_0x00a3;
    L_0x0096:
        r0 = r1.messageText;	 Catch:{ Throwable -> 0x009f }
        r0 = (android.text.Spannable) r0;	 Catch:{ Throwable -> 0x009f }
        r5 = 4;
        android.text.util.Linkify.addLinks(r0, r5);	 Catch:{ Throwable -> 0x009f }
        goto L_0x00a3;
    L_0x009f:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x00a3:
        r0 = r1.messageText;
        r3 = r1.addEntitiesToText(r0, r3);
        r15 = r30.getMaxMessageTextWidth();
        r0 = r1.messageOwner;
        r0 = r0.media;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
        if (r0 == 0) goto L_0x00b8;
    L_0x00b5:
        r0 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint;
        goto L_0x00ba;
    L_0x00b8:
        r0 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;
    L_0x00ba:
        r14 = r0;
        r0 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x040f }
        r13 = 24;
        if (r0 < r13) goto L_0x00e0;
    L_0x00c1:
        r0 = r1.messageText;	 Catch:{ Exception -> 0x040f }
        r5 = r1.messageText;	 Catch:{ Exception -> 0x040f }
        r5 = r5.length();	 Catch:{ Exception -> 0x040f }
        r0 = android.text.StaticLayout.Builder.obtain(r0, r2, r5, r14, r15);	 Catch:{ Exception -> 0x040f }
        r0 = r0.setBreakStrategy(r4);	 Catch:{ Exception -> 0x040f }
        r0 = r0.setHyphenationFrequency(r2);	 Catch:{ Exception -> 0x040f }
        r5 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x040f }
        r0 = r0.setAlignment(r5);	 Catch:{ Exception -> 0x040f }
        r0 = r0.build();	 Catch:{ Exception -> 0x040f }
        goto L_0x00f0;
    L_0x00e0:
        r0 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x040f }
        r6 = r1.messageText;	 Catch:{ Exception -> 0x040f }
        r9 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x040f }
        r10 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r11 = 0;
        r12 = 0;
        r5 = r0;
        r7 = r14;
        r8 = r15;
        r5.<init>(r6, r7, r8, r9, r10, r11, r12);	 Catch:{ Exception -> 0x040f }
    L_0x00f0:
        r12 = r0;
        r0 = r12.getHeight();
        r1.textHeight = r0;
        r0 = r12.getLineCount();
        r1.linesCount = r0;
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r13) goto L_0x0103;
    L_0x0101:
        r11 = 1;
        goto L_0x0110;
    L_0x0103:
        r0 = r1.linesCount;
        r0 = (float) r0;
        r5 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
        r0 = r0 / r5;
        r5 = (double) r0;
        r5 = java.lang.Math.ceil(r5);
        r0 = (int) r5;
        r11 = r0;
    L_0x0110:
        r10 = 0;
        r8 = 0;
        r9 = 0;
        r16 = 0;
    L_0x0115:
        if (r9 >= r11) goto L_0x040e;
    L_0x0117:
        r0 = android.os.Build.VERSION.SDK_INT;
        if (r0 < r13) goto L_0x011e;
    L_0x011b:
        r0 = r1.linesCount;
        goto L_0x0127;
    L_0x011e:
        r0 = 10;
        r5 = r1.linesCount;
        r5 = r5 - r8;
        r0 = java.lang.Math.min(r0, r5);
    L_0x0127:
        r7 = new org.telegram.messenger.MessageObject$TextLayoutBlock;
        r7.<init>();
        r6 = 2;
        if (r11 != r4) goto L_0x019b;
    L_0x012f:
        r7.textLayout = r12;
        r7.textYOffset = r10;
        r7.charactersOffset = r2;
        r5 = r1.emojiOnlyCount;
        if (r5 == 0) goto L_0x0187;
    L_0x0139:
        if (r5 == r4) goto L_0x0170;
    L_0x013b:
        if (r5 == r6) goto L_0x0159;
    L_0x013d:
        r6 = 3;
        if (r5 == r6) goto L_0x0141;
    L_0x0140:
        goto L_0x0187;
    L_0x0141:
        r5 = r1.textHeight;
        r6 = NUM; // 0x40866666 float:4.2 double:5.348506967E-315;
        r17 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r17;
        r1.textHeight = r5;
        r5 = r7.textYOffset;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r5 = r5 - r6;
        r7.textYOffset = r5;
        goto L_0x0187;
    L_0x0159:
        r5 = r1.textHeight;
        r6 = NUM; // 0x40900000 float:4.5 double:5.35161536E-315;
        r17 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r17;
        r1.textHeight = r5;
        r5 = r7.textYOffset;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r5 = r5 - r6;
        r7.textYOffset = r5;
        goto L_0x0187;
    L_0x0170:
        r5 = r1.textHeight;
        r6 = NUM; // 0x40a9999a float:5.3 double:5.35990441E-315;
        r17 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r5 = r5 - r17;
        r1.textHeight = r5;
        r5 = r7.textYOffset;
        r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
        r6 = (float) r6;
        r5 = r5 - r6;
        r7.textYOffset = r5;
    L_0x0187:
        r5 = r1.textHeight;
        r7.height = r5;
        r5 = r7;
        r2 = r8;
        r4 = r9;
        r8 = r11;
        r6 = r12;
        r18 = r14;
        r7 = r16;
        r17 = 24;
        r25 = 2;
    L_0x0198:
        r9 = r0;
        goto L_0x027e;
    L_0x019b:
        r6 = r12.getLineStart(r8);
        r5 = r8 + r0;
        r5 = r5 - r4;
        r5 = r12.getLineEnd(r5);
        if (r5 >= r6) goto L_0x01b9;
    L_0x01a8:
        r19 = r3;
        r20 = r8;
        r4 = r9;
        r8 = r11;
        r28 = r12;
        r18 = r14;
        r3 = r15;
        r2 = 0;
        r10 = 1;
        r17 = 24;
        goto L_0x03fa;
    L_0x01b9:
        r7.charactersOffset = r6;
        r7.charactersEnd = r5;
        if (r3 == 0) goto L_0x01f2;
    L_0x01bf:
        r10 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x03e7 }
        if (r10 < r13) goto L_0x01f2;
    L_0x01c3:
        r10 = r1.messageText;	 Catch:{ Exception -> 0x03e7 }
        r18 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r18 = org.telegram.messenger.AndroidUtilities.dp(r18);	 Catch:{ Exception -> 0x03e7 }
        r13 = r15 + r18;
        r5 = android.text.StaticLayout.Builder.obtain(r10, r6, r5, r14, r13);	 Catch:{ Exception -> 0x03e7 }
        r5 = r5.setBreakStrategy(r4);	 Catch:{ Exception -> 0x03e7 }
        r5 = r5.setHyphenationFrequency(r2);	 Catch:{ Exception -> 0x03e7 }
        r6 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x03e7 }
        r5 = r5.setAlignment(r6);	 Catch:{ Exception -> 0x03e7 }
        r5 = r5.build();	 Catch:{ Exception -> 0x03e7 }
        r7.textLayout = r5;	 Catch:{ Exception -> 0x03e7 }
        r5 = r7;
        r2 = r8;
        r4 = r9;
        r27 = r11;
        r6 = r12;
        r18 = r14;
        r17 = 24;
        r25 = 2;
        goto L_0x022e;
    L_0x01f2:
        r13 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x03e7 }
        r10 = r1.messageText;	 Catch:{ Exception -> 0x03e7 }
        r18 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x03e7 }
        r20 = NUM; // 0x3var_ float:1.0 double:5.263544247E-315;
        r21 = 0;
        r22 = 0;
        r23 = r5;
        r5 = r13;
        r24 = r6;
        r25 = 2;
        r6 = r10;
        r10 = r7;
        r7 = r24;
        r2 = r8;
        r8 = r23;
        r4 = r9;
        r9 = r14;
        r26 = r10;
        r10 = r15;
        r27 = r11;
        r11 = r18;
        r28 = r12;
        r12 = r20;
        r29 = r13;
        r17 = 24;
        r13 = r21;
        r18 = r14;
        r14 = r22;
        r5.<init>(r6, r7, r8, r9, r10, r11, r12, r13, r14);	 Catch:{ Exception -> 0x03dc }
        r5 = r26;
        r6 = r29;
        r5.textLayout = r6;	 Catch:{ Exception -> 0x03dc }
        r6 = r28;
    L_0x022e:
        r7 = r6.getLineTop(r2);	 Catch:{ Exception -> 0x03d4 }
        r7 = (float) r7;	 Catch:{ Exception -> 0x03d4 }
        r5.textYOffset = r7;	 Catch:{ Exception -> 0x03d4 }
        if (r4 == 0) goto L_0x023e;
    L_0x0237:
        r7 = r5.textYOffset;	 Catch:{ Exception -> 0x03d4 }
        r7 = r7 - r16;
        r7 = (int) r7;	 Catch:{ Exception -> 0x03d4 }
        r5.height = r7;	 Catch:{ Exception -> 0x03d4 }
    L_0x023e:
        r7 = r5.height;	 Catch:{ Exception -> 0x03d4 }
        r8 = r5.textLayout;	 Catch:{ Exception -> 0x03d4 }
        r9 = r5.textLayout;	 Catch:{ Exception -> 0x03d4 }
        r9 = r9.getLineCount();	 Catch:{ Exception -> 0x03d4 }
        r10 = 1;
        r9 = r9 - r10;
        r8 = r8.getLineBottom(r9);	 Catch:{ Exception -> 0x03d4 }
        r7 = java.lang.Math.max(r7, r8);	 Catch:{ Exception -> 0x03d4 }
        r5.height = r7;	 Catch:{ Exception -> 0x03d4 }
        r7 = r5.textYOffset;	 Catch:{ Exception -> 0x03d4 }
        r8 = r27;
        r11 = r8 + -1;
        if (r4 != r11) goto L_0x0198;
    L_0x025c:
        r9 = r5.textLayout;
        r9 = r9.getLineCount();
        r9 = java.lang.Math.max(r0, r9);
        r0 = r1.textHeight;	 Catch:{ Exception -> 0x027a }
        r10 = r5.textYOffset;	 Catch:{ Exception -> 0x027a }
        r11 = r5.textLayout;	 Catch:{ Exception -> 0x027a }
        r11 = r11.getHeight();	 Catch:{ Exception -> 0x027a }
        r11 = (float) r11;	 Catch:{ Exception -> 0x027a }
        r10 = r10 + r11;
        r10 = (int) r10;	 Catch:{ Exception -> 0x027a }
        r0 = java.lang.Math.max(r0, r10);	 Catch:{ Exception -> 0x027a }
        r1.textHeight = r0;	 Catch:{ Exception -> 0x027a }
        goto L_0x027e;
    L_0x027a:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x027e:
        r0 = r1.textLayoutBlocks;
        r0.add(r5);
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x0299 }
        r10 = r9 + -1;
        r10 = r0.getLineLeft(r10);	 Catch:{ Exception -> 0x0299 }
        if (r4 != 0) goto L_0x0297;
    L_0x028d:
        r11 = 0;
        r0 = (r10 > r11 ? 1 : (r10 == r11 ? 0 : -1));
        if (r0 < 0) goto L_0x02a3;
    L_0x0292:
        r1.textXOffset = r10;	 Catch:{ Exception -> 0x0295 }
        goto L_0x02a3;
    L_0x0295:
        r0 = move-exception;
        goto L_0x029b;
    L_0x0297:
        r11 = 0;
        goto L_0x02a3;
    L_0x0299:
        r0 = move-exception;
        r11 = 0;
    L_0x029b:
        if (r4 != 0) goto L_0x029f;
    L_0x029d:
        r1.textXOffset = r11;
    L_0x029f:
        org.telegram.messenger.FileLog.e(r0);
        r10 = 0;
    L_0x02a3:
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x02ac }
        r12 = r9 + -1;
        r0 = r0.getLineWidth(r12);	 Catch:{ Exception -> 0x02ac }
        goto L_0x02b1;
    L_0x02ac:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
    L_0x02b1:
        r12 = (double) r0;
        r12 = java.lang.Math.ceil(r12);
        r12 = (int) r12;
        r13 = r15 + 80;
        if (r12 <= r13) goto L_0x02bc;
    L_0x02bb:
        r12 = r15;
    L_0x02bc:
        r13 = r8 + -1;
        if (r4 != r13) goto L_0x02c2;
    L_0x02c0:
        r1.lastLineWidth = r12;
    L_0x02c2:
        r0 = r0 + r10;
        r14 = r12;
        r11 = (double) r0;
        r11 = java.lang.Math.ceil(r11);
        r11 = (int) r11;
        r12 = 1;
        if (r9 <= r12) goto L_0x0384;
    L_0x02cd:
        r19 = r3;
        r28 = r6;
        r16 = r7;
        r7 = r11;
        r3 = r14;
        r6 = 0;
        r10 = 0;
        r12 = 0;
        r14 = 0;
    L_0x02d9:
        if (r10 >= r9) goto L_0x035f;
    L_0x02db:
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x02e2 }
        r0 = r0.getLineWidth(r10);	 Catch:{ Exception -> 0x02e2 }
        goto L_0x02e7;
    L_0x02e2:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
    L_0x02e7:
        r20 = r2;
        r2 = r15 + 20;
        r2 = (float) r2;
        r2 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r2 <= 0) goto L_0x02f1;
    L_0x02f0:
        r0 = (float) r15;
    L_0x02f1:
        r2 = r0;
        r0 = r5.textLayout;	 Catch:{ Exception -> 0x02f9 }
        r0 = r0.getLineLeft(r10);	 Catch:{ Exception -> 0x02f9 }
        goto L_0x02fe;
    L_0x02f9:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
        r0 = 0;
    L_0x02fe:
        r21 = 0;
        r22 = (r0 > r21 ? 1 : (r0 == r21 ? 0 : -1));
        if (r22 <= 0) goto L_0x031a;
    L_0x0304:
        r21 = r9;
        r9 = r1.textXOffset;
        r9 = java.lang.Math.min(r9, r0);
        r1.textXOffset = r9;
        r9 = r5.directionFlags;
        r22 = r15;
        r15 = 1;
        r9 = r9 | r15;
        r9 = (byte) r9;
        r5.directionFlags = r9;
        r1.hasRtl = r15;
        goto L_0x0325;
    L_0x031a:
        r21 = r9;
        r22 = r15;
        r9 = r5.directionFlags;
        r9 = r9 | 2;
        r9 = (byte) r9;
        r5.directionFlags = r9;
    L_0x0325:
        if (r12 != 0) goto L_0x0336;
    L_0x0327:
        r9 = 0;
        r15 = (r0 > r9 ? 1 : (r0 == r9 ? 0 : -1));
        if (r15 != 0) goto L_0x0336;
    L_0x032c:
        r9 = r5.textLayout;	 Catch:{ Exception -> 0x0335 }
        r9 = r9.getParagraphDirection(r10);	 Catch:{ Exception -> 0x0335 }
        r15 = 1;
        if (r9 != r15) goto L_0x0336;
    L_0x0335:
        r12 = 1;
    L_0x0336:
        r6 = java.lang.Math.max(r6, r2);
        r0 = r0 + r2;
        r14 = java.lang.Math.max(r14, r0);
        r9 = r14;
        r14 = (double) r2;
        r14 = java.lang.Math.ceil(r14);
        r2 = (int) r14;
        r3 = java.lang.Math.max(r3, r2);
        r14 = (double) r0;
        r14 = java.lang.Math.ceil(r14);
        r0 = (int) r14;
        r7 = java.lang.Math.max(r7, r0);
        r10 = r10 + 1;
        r14 = r9;
        r2 = r20;
        r9 = r21;
        r15 = r22;
        goto L_0x02d9;
    L_0x035f:
        r20 = r2;
        r21 = r9;
        r22 = r15;
        if (r12 == 0) goto L_0x036c;
    L_0x0367:
        if (r4 != r13) goto L_0x0371;
    L_0x0369:
        r1.lastLineWidth = r11;
        goto L_0x0371;
    L_0x036c:
        if (r4 != r13) goto L_0x0370;
    L_0x036e:
        r1.lastLineWidth = r3;
    L_0x0370:
        r14 = r6;
    L_0x0371:
        r0 = r1.textWidth;
        r2 = (double) r14;
        r2 = java.lang.Math.ceil(r2);
        r2 = (int) r2;
        r0 = java.lang.Math.max(r0, r2);
        r1.textWidth = r0;
        r3 = r22;
        r2 = 0;
        r10 = 1;
        goto L_0x03d1;
    L_0x0384:
        r20 = r2;
        r19 = r3;
        r28 = r6;
        r16 = r7;
        r21 = r9;
        r22 = r15;
        r2 = 0;
        r0 = (r10 > r2 ? 1 : (r10 == r2 ? 0 : -1));
        if (r0 <= 0) goto L_0x03ba;
    L_0x0395:
        r0 = r1.textXOffset;
        r0 = java.lang.Math.min(r0, r10);
        r1.textXOffset = r0;
        r0 = r1.textXOffset;
        r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r0 != 0) goto L_0x03a8;
    L_0x03a3:
        r15 = r14;
        r0 = (float) r15;
        r0 = r0 + r10;
        r12 = (int) r0;
        goto L_0x03aa;
    L_0x03a8:
        r15 = r14;
        r12 = r15;
    L_0x03aa:
        r10 = 1;
        if (r8 == r10) goto L_0x03af;
    L_0x03ad:
        r0 = 1;
        goto L_0x03b0;
    L_0x03af:
        r0 = 0;
    L_0x03b0:
        r1.hasRtl = r0;
        r0 = r5.directionFlags;
        r0 = r0 | r10;
        r0 = (byte) r0;
        r5.directionFlags = r0;
        r15 = r12;
        goto L_0x03c3;
    L_0x03ba:
        r15 = r14;
        r10 = 1;
        r0 = r5.directionFlags;
        r0 = r0 | 2;
        r0 = (byte) r0;
        r5.directionFlags = r0;
    L_0x03c3:
        r0 = r1.textWidth;
        r3 = r22;
        r5 = java.lang.Math.min(r3, r15);
        r0 = java.lang.Math.max(r0, r5);
        r1.textWidth = r0;
    L_0x03d1:
        r0 = r20 + r21;
        goto L_0x03fc;
    L_0x03d4:
        r0 = move-exception;
        r20 = r2;
        r19 = r3;
        r28 = r6;
        goto L_0x03e1;
    L_0x03dc:
        r0 = move-exception;
        r20 = r2;
        r19 = r3;
    L_0x03e1:
        r3 = r15;
        r8 = r27;
        r2 = 0;
        r10 = 1;
        goto L_0x03f7;
    L_0x03e7:
        r0 = move-exception;
        r19 = r3;
        r20 = r8;
        r4 = r9;
        r8 = r11;
        r28 = r12;
        r18 = r14;
        r3 = r15;
        r2 = 0;
        r10 = 1;
        r17 = 24;
    L_0x03f7:
        org.telegram.messenger.FileLog.e(r0);
    L_0x03fa:
        r0 = r20;
    L_0x03fc:
        r9 = r4 + 1;
        r15 = r3;
        r11 = r8;
        r14 = r18;
        r3 = r19;
        r12 = r28;
        r2 = 0;
        r4 = 1;
        r10 = 0;
        r13 = 24;
        r8 = r0;
        goto L_0x0115;
    L_0x040e:
        return;
    L_0x040f:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x0413:
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
                if (i == 0) {
                    int i2 = peer.channel_id;
                    if (i2 != 0) {
                        message.dialog_id = (long) (-i2);
                    } else if (isOut(message)) {
                        message.dialog_id = (long) message.to_id.user_id;
                    } else {
                        message.dialog_id = (long) message.from_id;
                    }
                } else if (i < 0) {
                    message.dialog_id = AndroidUtilities.makeBroadcastId(i);
                } else {
                    message.dialog_id = (long) (-i);
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

    public boolean isSendError() {
        Message message = this.messageOwner;
        return message.send_state == 2 && message.id < 0;
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

    public static boolean isAnimatedStickerDocument(Document document) {
        if (document != null) {
            if ("application/x-tgsticker".equals(document.mime_type) && !document.thumbs.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public static boolean canAutoplayAnimatedSticker(Document document) {
        return isAnimatedStickerDocument(document) && SharedConfig.getDevicePerfomanceClass() != 0;
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
        return messageMedia != null && isAnimatedStickerDocument(messageMedia.document);
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
                Iterator it = document.attributes.iterator();
                while (it.hasNext()) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) it.next();
                    if (documentAttribute instanceof TL_documentAttributeSticker) {
                        InputStickerSet inputStickerSet = documentAttribute.stickerset;
                        if (inputStickerSet instanceof TL_inputStickerSetEmpty) {
                            return null;
                        }
                        return inputStickerSet;
                    }
                }
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
        if (i == 1000) {
            return isAnimatedStickerDocument(getDocument());
        }
        return i == 15;
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
        return isNewGifDocument(getDocument());
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
        r1 = NUM; // 0x7f0d0157 float:1.874281E38 double:1.053129947E-314;
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
        r10 = NUM; // 0x7f0d04d1 float:1.8744616E38 double:1.0531303867E-314;
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
        return canEditMessage(this.currentAccount, this.messageOwner, chat);
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
            if (messageMedia == null || !(isRoundVideoDocument(messageMedia.document) || isStickerDocument(message.media.document) || isAnimatedStickerDocument(message.media.document))) {
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

    /* JADX WARNING: Missing block: B:6:0x000b, code skipped:
            return false;
     */
    /* JADX WARNING: Missing block: B:66:0x00b4, code skipped:
            if (r1.pin_messages != false) goto L_0x00b6;
     */
    /* JADX WARNING: Missing block: B:89:0x0105, code skipped:
            if (r4 != null) goto L_0x0108;
     */
    /* JADX WARNING: Missing block: B:105:0x0123, code skipped:
            if (r5.out != false) goto L_0x0125;
     */
    /* JADX WARNING: Missing block: B:107:0x0127, code skipped:
            if (r5.post != false) goto L_0x0129;
     */
    public static boolean canEditMessage(int r4, org.telegram.tgnet.TLRPC.Message r5, org.telegram.tgnet.TLRPC.Chat r6) {
        /*
        r0 = 0;
        if (r6 == 0) goto L_0x000c;
    L_0x0003:
        r1 = r6.left;
        if (r1 != 0) goto L_0x000b;
    L_0x0007:
        r1 = r6.kicked;
        if (r1 == 0) goto L_0x000c;
    L_0x000b:
        return r0;
    L_0x000c:
        if (r5 == 0) goto L_0x014c;
    L_0x000e:
        r1 = r5.to_id;
        if (r1 == 0) goto L_0x014c;
    L_0x0012:
        r1 = r5.media;
        if (r1 == 0) goto L_0x0038;
    L_0x0016:
        r1 = r1.document;
        r1 = isRoundVideoDocument(r1);
        if (r1 != 0) goto L_0x014c;
    L_0x001e:
        r1 = r5.media;
        r1 = r1.document;
        r1 = isStickerDocument(r1);
        if (r1 != 0) goto L_0x014c;
    L_0x0028:
        r1 = r5.media;
        r1 = r1.document;
        r1 = isAnimatedStickerDocument(r1);
        if (r1 != 0) goto L_0x014c;
    L_0x0032:
        r1 = isLocationMessage(r5);
        if (r1 != 0) goto L_0x014c;
    L_0x0038:
        r1 = r5.action;
        if (r1 == 0) goto L_0x0040;
    L_0x003c:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
        if (r1 == 0) goto L_0x014c;
    L_0x0040:
        r1 = isForwardedMessage(r5);
        if (r1 != 0) goto L_0x014c;
    L_0x0046:
        r1 = r5.via_bot_id;
        if (r1 != 0) goto L_0x014c;
    L_0x004a:
        r1 = r5.id;
        if (r1 >= 0) goto L_0x0050;
    L_0x004e:
        goto L_0x014c;
    L_0x0050:
        r1 = r5.from_id;
        r2 = r5.to_id;
        r2 = r2.user_id;
        r3 = 1;
        if (r1 != r2) goto L_0x0070;
    L_0x0059:
        r2 = org.telegram.messenger.UserConfig.getInstance(r4);
        r2 = r2.getClientUserId();
        if (r1 != r2) goto L_0x0070;
    L_0x0063:
        r1 = isLiveLocationMessage(r5);
        if (r1 != 0) goto L_0x0070;
    L_0x0069:
        r1 = r5.media;
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaContact;
        if (r1 != 0) goto L_0x0070;
    L_0x006f:
        return r3;
    L_0x0070:
        if (r6 != 0) goto L_0x008b;
    L_0x0072:
        r1 = r5.to_id;
        r1 = r1.channel_id;
        if (r1 == 0) goto L_0x008b;
    L_0x0078:
        r6 = org.telegram.messenger.MessagesController.getInstance(r4);
        r1 = r5.to_id;
        r1 = r1.channel_id;
        r1 = java.lang.Integer.valueOf(r1);
        r6 = r6.getChat(r1);
        if (r6 != 0) goto L_0x008b;
    L_0x008a:
        return r0;
    L_0x008b:
        r1 = r5.media;
        if (r1 == 0) goto L_0x00a0;
    L_0x008f:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
        if (r2 != 0) goto L_0x00a0;
    L_0x0093:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r2 != 0) goto L_0x00a0;
    L_0x0097:
        r2 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r2 != 0) goto L_0x00a0;
    L_0x009b:
        r1 = r1 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r1 != 0) goto L_0x00a0;
    L_0x009f:
        return r0;
    L_0x00a0:
        r1 = r5.out;
        if (r1 == 0) goto L_0x00b7;
    L_0x00a4:
        if (r6 == 0) goto L_0x00b7;
    L_0x00a6:
        r1 = r6.megagroup;
        if (r1 == 0) goto L_0x00b7;
    L_0x00aa:
        r1 = r6.creator;
        if (r1 != 0) goto L_0x00b6;
    L_0x00ae:
        r1 = r6.admin_rights;
        if (r1 == 0) goto L_0x00b7;
    L_0x00b2:
        r1 = r1.pin_messages;
        if (r1 == 0) goto L_0x00b7;
    L_0x00b6:
        return r3;
    L_0x00b7:
        r1 = r5.date;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
        r2 = r2.getCurrentTime();
        r1 = r1 - r2;
        r1 = java.lang.Math.abs(r1);
        r2 = org.telegram.messenger.MessagesController.getInstance(r4);
        r2 = r2.maxEditTime;
        if (r1 <= r2) goto L_0x00cf;
    L_0x00ce:
        return r0;
    L_0x00cf:
        r1 = r5.to_id;
        r1 = r1.channel_id;
        if (r1 != 0) goto L_0x0109;
    L_0x00d5:
        r6 = r5.out;
        if (r6 != 0) goto L_0x00e5;
    L_0x00d9:
        r6 = r5.from_id;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.getClientUserId();
        if (r6 != r4) goto L_0x0108;
    L_0x00e5:
        r4 = r5.media;
        r6 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r6 != 0) goto L_0x0107;
    L_0x00eb:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r4 == 0) goto L_0x00fb;
    L_0x00ef:
        r4 = isStickerMessage(r5);
        if (r4 != 0) goto L_0x00fb;
    L_0x00f5:
        r4 = isAnimatedStickerMessage(r5);
        if (r4 == 0) goto L_0x0107;
    L_0x00fb:
        r4 = r5.media;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
        if (r5 != 0) goto L_0x0107;
    L_0x0101:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r5 != 0) goto L_0x0107;
    L_0x0105:
        if (r4 != 0) goto L_0x0108;
    L_0x0107:
        r0 = 1;
    L_0x0108:
        return r0;
    L_0x0109:
        r4 = r6.megagroup;
        if (r4 == 0) goto L_0x0111;
    L_0x010d:
        r4 = r5.out;
        if (r4 != 0) goto L_0x0129;
    L_0x0111:
        r4 = r6.megagroup;
        if (r4 != 0) goto L_0x014c;
    L_0x0115:
        r4 = r6.creator;
        if (r4 != 0) goto L_0x0125;
    L_0x0119:
        r4 = r6.admin_rights;
        if (r4 == 0) goto L_0x014c;
    L_0x011d:
        r4 = r4.edit_messages;
        if (r4 != 0) goto L_0x0125;
    L_0x0121:
        r4 = r5.out;
        if (r4 == 0) goto L_0x014c;
    L_0x0125:
        r4 = r5.post;
        if (r4 == 0) goto L_0x014c;
    L_0x0129:
        r4 = r5.media;
        r6 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
        if (r6 != 0) goto L_0x014b;
    L_0x012f:
        r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
        if (r4 == 0) goto L_0x013f;
    L_0x0133:
        r4 = isStickerMessage(r5);
        if (r4 != 0) goto L_0x013f;
    L_0x0139:
        r4 = isAnimatedStickerMessage(r5);
        if (r4 == 0) goto L_0x014b;
    L_0x013f:
        r4 = r5.media;
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
        if (r5 != 0) goto L_0x014b;
    L_0x0145:
        r5 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
        if (r5 != 0) goto L_0x014b;
    L_0x0149:
        if (r4 != 0) goto L_0x014c;
    L_0x014b:
        return r3;
    L_0x014c:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.canEditMessage(int, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLRPC$Chat):boolean");
    }

    public boolean canDeleteMessage(Chat chat) {
        return this.eventId == 0 && canDeleteMessage(this.currentAccount, this.messageOwner, chat);
    }

    /* JADX WARNING: Missing block: B:18:0x0037, code skipped:
            if (r3.out != false) goto L_0x0045;
     */
    /* JADX WARNING: Missing block: B:24:0x0043, code skipped:
            if (r3.from_id > 0) goto L_0x0045;
     */
    public static boolean canDeleteMessage(int r2, org.telegram.tgnet.TLRPC.Message r3, org.telegram.tgnet.TLRPC.Chat r4) {
        /*
        r0 = r3.id;
        r1 = 1;
        if (r0 >= 0) goto L_0x0006;
    L_0x0005:
        return r1;
    L_0x0006:
        if (r4 != 0) goto L_0x001e;
    L_0x0008:
        r0 = r3.to_id;
        r0 = r0.channel_id;
        if (r0 == 0) goto L_0x001e;
    L_0x000e:
        r2 = org.telegram.messenger.MessagesController.getInstance(r2);
        r4 = r3.to_id;
        r4 = r4.channel_id;
        r4 = java.lang.Integer.valueOf(r4);
        r4 = r2.getChat(r4);
    L_0x001e:
        r2 = org.telegram.messenger.ChatObject.isChannel(r4);
        r0 = 0;
        if (r2 == 0) goto L_0x0047;
    L_0x0025:
        r2 = r3.id;
        if (r2 == r1) goto L_0x0046;
    L_0x0029:
        r2 = r4.creator;
        if (r2 != 0) goto L_0x0045;
    L_0x002d:
        r2 = r4.admin_rights;
        if (r2 == 0) goto L_0x0039;
    L_0x0031:
        r2 = r2.delete_messages;
        if (r2 != 0) goto L_0x0045;
    L_0x0035:
        r2 = r3.out;
        if (r2 != 0) goto L_0x0045;
    L_0x0039:
        r2 = r4.megagroup;
        if (r2 == 0) goto L_0x0046;
    L_0x003d:
        r2 = r3.out;
        if (r2 == 0) goto L_0x0046;
    L_0x0041:
        r2 = r3.from_id;
        if (r2 <= 0) goto L_0x0046;
    L_0x0045:
        r0 = 1;
    L_0x0046:
        return r0;
    L_0x0047:
        r2 = isOut(r3);
        if (r2 != 0) goto L_0x0053;
    L_0x004d:
        r2 = org.telegram.messenger.ChatObject.isChannel(r4);
        if (r2 != 0) goto L_0x0054;
    L_0x0053:
        r0 = 1;
    L_0x0054:
        return r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.canDeleteMessage(int, org.telegram.tgnet.TLRPC$Message, org.telegram.tgnet.TLRPC$Chat):boolean");
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
