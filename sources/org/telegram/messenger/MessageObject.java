package org.telegram.messenger;

import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.StaticLayout.Builder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.SparseArray;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.Emoji.EmojiSpan;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.upstream.cache.CacheDataSink;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInlineResult;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.PageBlock;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeAbout;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangePhoto;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeTitle;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeUsername;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionDeleteMessage;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionEditMessage;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantInvite;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantJoin;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantLeave;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleAdmin;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionParticipantToggleBan;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleInvites;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionTogglePreHistoryHidden;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionToggleSignatures;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionUpdatePinned;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
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
import org.telegram.tgnet.TLRPC.TL_messageActionTTLChange;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityBotCommand;
import org.telegram.tgnet.TLRPC.TL_messageEntityCashtag;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC.TL_messageEntityHashtag;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityMention;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageEntityPhone;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageForwarded_old;
import org.telegram.tgnet.TLRPC.TL_messageForwarded_old2;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument_layer68;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument_layer74;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument_old;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeoLive;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_layer68;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_layer74;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto_old;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_old;
import org.telegram.tgnet.TLRPC.TL_message_old2;
import org.telegram.tgnet.TLRPC.TL_message_old3;
import org.telegram.tgnet.TLRPC.TL_message_old4;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_pageBlockCollage;
import org.telegram.tgnet.TLRPC.TL_pageBlockPhoto;
import org.telegram.tgnet.TLRPC.TL_pageBlockSlideshow;
import org.telegram.tgnet.TLRPC.TL_pageBlockVideo;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photoSize;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebDocument;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;

public class MessageObject {
    private static final int LINES_PER_BLOCK = 10;
    public static final int MESSAGE_SEND_STATE_SENDING = 1;
    public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
    public static final int MESSAGE_SEND_STATE_SENT = 0;
    public static final int POSITION_FLAG_BOTTOM = 8;
    public static final int POSITION_FLAG_LEFT = 1;
    public static final int POSITION_FLAG_RIGHT = 2;
    public static final int POSITION_FLAG_TOP = 4;
    public static Pattern urlPattern;
    public boolean attachPathExists;
    public int audioPlayerDuration;
    public float audioProgress;
    public int audioProgressSec;
    public StringBuilder botButtonsLayout;
    public float bufferedProgress;
    public CharSequence caption;
    public int contentType;
    public int currentAccount;
    public TL_channelAdminLogEvent currentEvent;
    public String customReplyName;
    public String dateKey;
    public boolean deleted;
    public long eventId;
    public boolean forceUpdate;
    private int generatedWithMinSize;
    public float gifState;
    public boolean hasRtl;
    public boolean isDateObject;
    private int isRoundVideoCached;
    public int lastLineWidth;
    private boolean layoutCreated;
    public int linesCount;
    public CharSequence linkDescription;
    public boolean localChannel;
    public long localGroupId;
    public String localName;
    public int localType;
    public String localUserName;
    public boolean mediaExists;
    public Message messageOwner;
    public CharSequence messageText;
    public String monthKey;
    public ArrayList<PhotoSize> photoThumbs;
    public ArrayList<PhotoSize> photoThumbs2;
    public MessageObject replyMessageObject;
    public boolean resendAsIs;
    public int textHeight;
    public ArrayList<TextLayoutBlock> textLayoutBlocks;
    public int textWidth;
    public float textXOffset;
    public int type;
    public boolean useCustomPhoto;
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

        public void set(int minX, int maxX, int minY, int maxY, int w, float h, int flags) {
            this.minX = (byte) minX;
            this.maxX = (byte) maxX;
            this.minY = (byte) minY;
            this.maxY = (byte) maxY;
            this.pw = w;
            this.spanSize = w;
            this.ph = h;
            this.flags = (byte) flags;
        }
    }

    public static class GroupedMessages {
        private int firstSpanAdditionalSize = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
        public long groupId;
        public boolean hasSibling;
        private int maxSizeWidth = 800;
        public ArrayList<MessageObject> messages = new ArrayList();
        public ArrayList<GroupedMessagePosition> posArray = new ArrayList();
        public HashMap<MessageObject, GroupedMessagePosition> positions = new HashMap();

        private class MessageGroupedLayoutAttempt {
            public float[] heights;
            public int[] lineCounts;

            public MessageGroupedLayoutAttempt(int i1, int i2, float f1, float f2) {
                this.lineCounts = new int[]{i1, i2};
                this.heights = new float[]{f1, f2};
            }

            public MessageGroupedLayoutAttempt(int i1, int i2, int i3, float f1, float f2, float f3) {
                this.lineCounts = new int[]{i1, i2, i3};
                this.heights = new float[]{f1, f2, f3};
            }

            public MessageGroupedLayoutAttempt(int i1, int i2, int i3, int i4, float f1, float f2, float f3, float f4) {
                this.lineCounts = new int[]{i1, i2, i3, i4};
                this.heights = new float[]{f1, f2, f3, f4};
            }
        }

        private float multiHeight(float[] array, int start, int end) {
            float sum = 0.0f;
            for (int a = start; a < end; a++) {
                sum += array[a];
            }
            return ((float) this.maxSizeWidth) / sum;
        }

        public void calculate() {
            this.posArray.clear();
            this.positions.clear();
            int count = this.messages.size();
            if (count > 1) {
                MessageObject messageObject;
                boolean isOut;
                GroupedMessagePosition position;
                int i;
                float averageAspectRatio;
                float f;
                byte maxX;
                GroupedMessagePosition position2;
                int firstWidth;
                int i2;
                float thirdHeight;
                int leftWidth;
                int i3;
                int width;
                float min;
                float h;
                int w1;
                int i4;
                int i5;
                int i6;
                float f2;
                byte maxX2;
                float averageAspectRatio2;
                float maxAspectRatio;
                int i7;
                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt;
                int minHeight;
                int i8;
                int i9;
                float f3;
                float f4;
                float f5;
                float maxSizeHeight = 814.0f;
                StringBuilder proportions = new StringBuilder();
                byte minWidth = (byte) 0;
                r10.hasSibling = false;
                boolean isOut2 = false;
                boolean forceCalc = false;
                boolean needShare = false;
                float averageAspectRatio3 = 1.0f;
                int a = 0;
                while (a < count) {
                    messageObject = (MessageObject) r10.messages.get(a);
                    if (a == 0) {
                        isOut = messageObject.isOutOwner();
                        boolean needShare2 = !isOut && (!(messageObject.messageOwner.fwd_from == null || messageObject.messageOwner.fwd_from.saved_from_peer == null) || (messageObject.messageOwner.from_id > 0 && (messageObject.messageOwner.to_id.channel_id != 0 || messageObject.messageOwner.to_id.chat_id != 0 || (messageObject.messageOwner.media instanceof TL_messageMediaGame) || (messageObject.messageOwner.media instanceof TL_messageMediaInvoice))));
                        isOut2 = isOut;
                        needShare = needShare2;
                    }
                    PhotoSize photoSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                    position = new GroupedMessagePosition();
                    position.last = a == count + -1;
                    position.aspectRatio = photoSize == null ? 1.0f : ((float) photoSize.f43w) / ((float) photoSize.f42h);
                    if (position.aspectRatio > 1.2f) {
                        proportions.append("w");
                    } else if (position.aspectRatio < 0.8f) {
                        proportions.append("n");
                    } else {
                        proportions.append("q");
                    }
                    averageAspectRatio3 += position.aspectRatio;
                    if (position.aspectRatio > 2.0f) {
                        forceCalc = true;
                    }
                    r10.positions.put(messageObject, position);
                    r10.posArray.add(position);
                    a++;
                }
                if (needShare) {
                    r10.maxSizeWidth -= 50;
                    r10.firstSpanAdditionalSize += 50;
                }
                int minHeight2 = AndroidUtilities.dp(120.0f);
                int minWidth2 = (int) (((float) AndroidUtilities.dp(120.0f)) / (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) r10.maxSizeWidth)));
                int paddingsWidth = (int) (((float) AndroidUtilities.dp(40.0f)) / (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) r10.maxSizeWidth)));
                float maxAspectRatio2 = ((float) r10.maxSizeWidth) / 814.0f;
                float averageAspectRatio4 = averageAspectRatio3 / ((float) count);
                int i10 = 4;
                StringBuilder stringBuilder;
                if (forceCalc) {
                    i = 2;
                    averageAspectRatio = averageAspectRatio4;
                    f = maxAspectRatio2;
                    stringBuilder = proportions;
                    maxX = (byte) 0;
                } else {
                    if (!(count == 2 || count == 3)) {
                        if (count != 4) {
                            i = 2;
                            averageAspectRatio = averageAspectRatio4;
                            f = maxAspectRatio2;
                            maxX = (byte) 0;
                        }
                    }
                    GroupedMessagePosition position1;
                    GroupedMessagePosition position22;
                    float round;
                    if (count == 2) {
                        GroupedMessagePosition position12;
                        position1 = (GroupedMessagePosition) r10.posArray.get(0);
                        position2 = (GroupedMessagePosition) r10.posArray.get(1);
                        String pString = proportions.toString();
                        if (pString.equals("ww")) {
                            GroupedMessagePosition position23 = position2;
                            GroupedMessagePosition position13 = position1;
                            if (((double) averageAspectRatio4) > 1.4d * ((double) maxAspectRatio2)) {
                                position12 = position13;
                                position22 = position23;
                                averageAspectRatio = averageAspectRatio4;
                                if (((double) (position12.aspectRatio - position22.aspectRatio)) < 0.2d) {
                                    round = ((float) Math.round(Math.min(((float) r10.maxSizeWidth) / position12.aspectRatio, Math.min(((float) r10.maxSizeWidth) / position22.aspectRatio, 814.0f / 2.0f)))) / NUM;
                                    position12.set(0, 0, 0, 0, r10.maxSizeWidth, round, 7);
                                    position22.set(0, 0, 1, 1, r10.maxSizeWidth, round, 11);
                                    f = maxAspectRatio2;
                                }
                            } else {
                                averageAspectRatio = averageAspectRatio4;
                                position22 = position23;
                                position12 = position13;
                            }
                        } else {
                            position12 = position1;
                            position22 = position2;
                            averageAspectRatio = averageAspectRatio4;
                        }
                        if (pString.equals("ww")) {
                            f = maxAspectRatio2;
                        } else if (pString.equals("qq")) {
                            f = maxAspectRatio2;
                        } else {
                            f = maxAspectRatio2;
                            i10 = (int) Math.max(((float) r10.maxSizeWidth) * 0.4f, (float) Math.round((((float) r10.maxSizeWidth) / position12.aspectRatio) / ((1.0f / position12.aspectRatio) + (1.0f / position22.aspectRatio))));
                            firstWidth = r10.maxSizeWidth - i10;
                            if (firstWidth < minWidth2) {
                                averageAspectRatio4 = minWidth2 - firstWidth;
                                firstWidth = minWidth2;
                                i10 -= averageAspectRatio4;
                            }
                            round = Math.min(814.0f, (float) Math.round(Math.min(((float) firstWidth) / position12.aspectRatio, ((float) i10) / position22.aspectRatio))) / 814.0f;
                            position12.set(0, 0, 0, 0, firstWidth, round, 13);
                            position22.set(1, 1, 0, 0, i10, round, 14);
                            minWidth = (byte) 1;
                        }
                        i10 = r10.maxSizeWidth / 2;
                        i2 = i10;
                        round = ((float) Math.round(Math.min(((float) i10) / position12.aspectRatio, Math.min(((float) i10) / position22.aspectRatio, 814.0f)))) / 814.0f;
                        position12.set(0, 0, 0, 0, i2, round, 13);
                        position22.set(Float.MIN_VALUE, Float.MIN_VALUE, 0, 0, i2, round, 14);
                        minWidth = (byte) 1;
                    } else {
                        averageAspectRatio = averageAspectRatio4;
                        f = maxAspectRatio2;
                        GroupedMessagePosition position3;
                        float firstHeight;
                        if (count == 3) {
                            byte firstHeight2;
                            position1 = (GroupedMessagePosition) r10.posArray.get(0);
                            position2 = (GroupedMessagePosition) r10.posArray.get(1);
                            position3 = (GroupedMessagePosition) r10.posArray.get(2);
                            int maxX3;
                            if (proportions.charAt(0) == 'n') {
                                thirdHeight = Math.min(814.0f * 0.5f, (float) Math.round((position2.aspectRatio * ((float) r10.maxSizeWidth)) / (position3.aspectRatio + position2.aspectRatio)));
                                maxAspectRatio2 = 814.0f - thirdHeight;
                                maxX3 = 0;
                                a = (int) Math.max((float) minWidth2, Math.min(((float) r10.maxSizeWidth) * 0.5f, (float) Math.round(Math.min(position3.aspectRatio * thirdHeight, position2.aspectRatio * maxAspectRatio2))));
                                leftWidth = Math.round(Math.min((position1.aspectRatio * 814.0f) + ((float) paddingsWidth), (float) (r10.maxSizeWidth - a)));
                                position1.set(0, 0, 0, 1, leftWidth, 1.0f, 13);
                                i3 = a;
                                position2.set(1, 1, 0, 0, i3, maxAspectRatio2 / 814.0f, 6);
                                position3.set(0, 1, 1, 1, i3, thirdHeight / 814.0f, 10);
                                position3.spanSize = r10.maxSizeWidth;
                                position1.siblingHeights = new float[]{thirdHeight / 814.0f, maxAspectRatio2 / 814.0f};
                                if (isOut2) {
                                    position1.spanSize = r10.maxSizeWidth - a;
                                } else {
                                    position2.spanSize = r10.maxSizeWidth - leftWidth;
                                    position3.leftSpanOffset = leftWidth;
                                }
                                r10.hasSibling = true;
                                firstHeight2 = (byte) 1;
                            } else {
                                maxX3 = 0;
                                firstHeight = ((float) Math.round(Math.min(((float) r10.maxSizeWidth) / position1.aspectRatio, 0.66f * 814.0f))) / 814.0f;
                                position1.set(0, 1, 0, 0, r10.maxSizeWidth, firstHeight, 7);
                                width = r10.maxSizeWidth / 2;
                                i3 = width;
                                min = Math.min(814.0f - firstHeight, (float) Math.round(Math.min(((float) width) / position2.aspectRatio, ((float) width) / position3.aspectRatio))) / 814.0f;
                                position2.set(0, 0, 1, 1, i3, min, 9);
                                position3.set(1, 1, 1, 1, i3, min, 10);
                                firstHeight2 = (byte) 1;
                            }
                            minWidth = firstHeight2;
                        } else {
                            maxX = (byte) 0;
                            if (count == 4) {
                                position1 = (GroupedMessagePosition) r10.posArray.get(0);
                                position22 = (GroupedMessagePosition) r10.posArray.get(1);
                                position3 = (GroupedMessagePosition) r10.posArray.get(2);
                                GroupedMessagePosition maxAspectRatio3 = (GroupedMessagePosition) r10.posArray.get(3);
                                if (proportions.charAt(0) == 'w') {
                                    firstHeight = ((float) Math.round(Math.min(((float) r10.maxSizeWidth) / position1.aspectRatio, 0.66f * 814.0f))) / 814.0f;
                                    position1.set(0, 2, 0, 0, r10.maxSizeWidth, firstHeight, 7);
                                    h = (float) Math.round(((float) r10.maxSizeWidth) / ((position22.aspectRatio + position3.aspectRatio) + maxAspectRatio3.aspectRatio));
                                    leftWidth = (int) Math.max((float) minWidth2, Math.min(((float) r10.maxSizeWidth) * 0.4f, position22.aspectRatio * h));
                                    int w2 = (int) Math.max(Math.max((float) minWidth2, ((float) r10.maxSizeWidth) * 0.33f), maxAspectRatio3.aspectRatio * h);
                                    w1 = (r10.maxSizeWidth - leftWidth) - w2;
                                    stringBuilder = proportions;
                                    round = Math.min(NUM - firstHeight, h) / 814.0f;
                                    position22.set(0, 0, 1, 1, leftWidth, round, 9);
                                    position3.set(1, 1, 1, 1, w1, round, 8);
                                    maxAspectRatio3.set(2, 2, 1, 1, w2, round, 10);
                                    minWidth = (byte) 2;
                                } else {
                                    a = Math.max(minWidth2, Math.round(814.0f / (((1.0f / position22.aspectRatio) + (1.0f / position3.aspectRatio)) + (1.0f / ((GroupedMessagePosition) r10.posArray.get(3)).aspectRatio))));
                                    float h0 = Math.min(0.33f, Math.max((float) minHeight2, ((float) a) / position22.aspectRatio) / 814.0f);
                                    h = Math.min(0.33f, Math.max((float) minHeight2, ((float) a) / position3.aspectRatio) / 814.0f);
                                    float h2 = (1.0f - h0) - h;
                                    proportions = Math.round(Math.min((position1.aspectRatio * NUM) + ((float) paddingsWidth), (float) (r10.maxSizeWidth - a)));
                                    position1.set(0, 0, 0, 2, proportions, (h0 + h) + h2, 13);
                                    i2 = a;
                                    position22.set(1, 1, 0, 0, i2, h0, 6);
                                    position3.set(0, 1, 1, 1, i2, h, 2);
                                    position3.spanSize = r10.maxSizeWidth;
                                    maxAspectRatio3.set(0, 1, 2, 2, i2, h2, 10);
                                    maxAspectRatio3.spanSize = r10.maxSizeWidth;
                                    if (isOut2) {
                                        position1.spanSize = r10.maxSizeWidth - a;
                                    } else {
                                        position22.spanSize = r10.maxSizeWidth - proportions;
                                        position3.leftSpanOffset = proportions;
                                        maxAspectRatio3.leftSpanOffset = proportions;
                                    }
                                    r15 = new float[3];
                                    r15[1] = h;
                                    r15[2] = h2;
                                    position1.siblingHeights = r15;
                                    r10.hasSibling = true;
                                    minWidth = (byte) 1;
                                }
                                i4 = paddingsWidth;
                                i5 = minHeight2;
                                i6 = count;
                                f2 = 814.0f;
                                maxX2 = minWidth;
                                averageAspectRatio2 = averageAspectRatio;
                                maxAspectRatio = f;
                                i7 = 0;
                                w1 = minWidth2;
                                while (true) {
                                    i10 = i7;
                                    width = i6;
                                    if (i10 >= width) {
                                        position2 = (GroupedMessagePosition) r10.posArray.get(i10);
                                        if (isOut2) {
                                            if (position2.maxX == maxX2 || (position2.flags & 2) != 0) {
                                                position2.spanSize += r10.firstSpanAdditionalSize;
                                            }
                                            if ((position2.flags & 1) != 0) {
                                                position2.edge = true;
                                            }
                                        } else {
                                            if (position2.minX == (byte) 0) {
                                                position2.spanSize += r10.firstSpanAdditionalSize;
                                            }
                                            if ((position2.flags & 2) == 0) {
                                                position2.edge = true;
                                                isOut = true;
                                            }
                                        }
                                        messageObject = (MessageObject) r10.messages.get(i10);
                                        if (!isOut2 && messageObject.needDrawAvatar()) {
                                            if (position2.edge) {
                                                if ((position2.flags & 2) == 0) {
                                                    if (position2.spanSize == 1000) {
                                                        position2.spanSize -= 108;
                                                    } else if (position2.leftSpanOffset == 0) {
                                                        position2.leftSpanOffset += 108;
                                                    }
                                                }
                                                i7 = i10 + 1;
                                                i6 = width;
                                            } else {
                                                if (position2.spanSize != 1000) {
                                                    position2.spanSize += 108;
                                                }
                                                position2.pw += 108;
                                            }
                                        }
                                        i7 = i10 + 1;
                                        i6 = width;
                                    } else {
                                        return;
                                    }
                                }
                            }
                            i4 = paddingsWidth;
                            w1 = minWidth2;
                            i5 = minHeight2;
                            i6 = count;
                            f2 = 814.0f;
                            averageAspectRatio2 = averageAspectRatio;
                            maxAspectRatio = f;
                            maxX2 = maxX;
                            i7 = 0;
                            while (true) {
                                i10 = i7;
                                width = i6;
                                if (i10 >= width) {
                                    position2 = (GroupedMessagePosition) r10.posArray.get(i10);
                                    if (isOut2) {
                                        position2.spanSize += r10.firstSpanAdditionalSize;
                                        if ((position2.flags & 1) != 0) {
                                            position2.edge = true;
                                        }
                                    } else {
                                        if (position2.minX == (byte) 0) {
                                            position2.spanSize += r10.firstSpanAdditionalSize;
                                        }
                                        if ((position2.flags & 2) == 0) {
                                            position2.edge = true;
                                            isOut = true;
                                        }
                                    }
                                    messageObject = (MessageObject) r10.messages.get(i10);
                                    if (position2.edge) {
                                        if ((position2.flags & 2) == 0) {
                                            if (position2.spanSize == 1000) {
                                                position2.spanSize -= 108;
                                            } else if (position2.leftSpanOffset == 0) {
                                                position2.leftSpanOffset += 108;
                                            }
                                        }
                                        i7 = i10 + 1;
                                        i6 = width;
                                    } else {
                                        if (position2.spanSize != 1000) {
                                            position2.spanSize += 108;
                                        }
                                        position2.pw += 108;
                                        i7 = i10 + 1;
                                        i6 = width;
                                    }
                                } else {
                                    return;
                                }
                            }
                        }
                    }
                    i6 = count;
                    f2 = 814.0f;
                    maxX2 = minWidth;
                    averageAspectRatio2 = averageAspectRatio;
                    maxAspectRatio = f;
                    i7 = 0;
                    w1 = minWidth2;
                    while (true) {
                        i10 = i7;
                        width = i6;
                        if (i10 >= width) {
                            position2 = (GroupedMessagePosition) r10.posArray.get(i10);
                            if (isOut2) {
                                if (position2.minX == (byte) 0) {
                                    position2.spanSize += r10.firstSpanAdditionalSize;
                                }
                                if ((position2.flags & 2) == 0) {
                                    position2.edge = true;
                                    isOut = true;
                                }
                            } else {
                                position2.spanSize += r10.firstSpanAdditionalSize;
                                if ((position2.flags & 1) != 0) {
                                    position2.edge = true;
                                }
                            }
                            messageObject = (MessageObject) r10.messages.get(i10);
                            if (position2.edge) {
                                if (position2.spanSize != 1000) {
                                    position2.spanSize += 108;
                                }
                                position2.pw += 108;
                                i7 = i10 + 1;
                                i6 = width;
                            } else {
                                if ((position2.flags & 2) == 0) {
                                    if (position2.spanSize == 1000) {
                                        position2.spanSize -= 108;
                                    } else if (position2.leftSpanOffset == 0) {
                                        position2.leftSpanOffset += 108;
                                    }
                                }
                                i7 = i10 + 1;
                                i6 = width;
                            }
                        } else {
                            return;
                        }
                    }
                }
                float[] croppedRatios = new float[r10.posArray.size()];
                for (a = 0; a < count; a++) {
                    if (averageAspectRatio > 1.1f) {
                        croppedRatios[a] = Math.max(1.0f, ((GroupedMessagePosition) r10.posArray.get(a)).aspectRatio);
                    } else {
                        croppedRatios[a] = Math.min(1.0f, ((GroupedMessagePosition) r10.posArray.get(a)).aspectRatio);
                    }
                    croppedRatios[a] = Math.max(0.66667f, Math.min(1.7f, croppedRatios[a]));
                }
                ArrayList<MessageGroupedLayoutAttempt> attempts = new ArrayList();
                a = 1;
                while (true) {
                    leftWidth = a;
                    if (leftWidth >= croppedRatios.length) {
                        break;
                    }
                    w1 = croppedRatios.length - leftWidth;
                    if (leftWidth > 3) {
                        i = i10;
                        i6 = count;
                        averageAspectRatio2 = averageAspectRatio;
                        maxAspectRatio = f;
                    } else if (w1 > 3) {
                        i = i10;
                        i6 = count;
                        averageAspectRatio2 = averageAspectRatio;
                        maxAspectRatio = f;
                    } else {
                        width = i;
                        i = i10;
                        i6 = count;
                        count = width;
                        count = 3;
                        averageAspectRatio2 = averageAspectRatio;
                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt2 = messageGroupedLayoutAttempt;
                        maxAspectRatio = f;
                        messageGroupedLayoutAttempt = new MessageGroupedLayoutAttempt(leftWidth, w1, multiHeight(croppedRatios, 0, leftWidth), multiHeight(croppedRatios, leftWidth, croppedRatios.length));
                        attempts.add(messageGroupedLayoutAttempt2);
                    }
                    a = leftWidth + 1;
                    i10 = i;
                    averageAspectRatio = averageAspectRatio2;
                    f = maxAspectRatio;
                    count = i6;
                    i = 2;
                }
                i = i10;
                i6 = count;
                averageAspectRatio2 = averageAspectRatio;
                maxAspectRatio = f;
                for (leftWidth = 1; leftWidth < croppedRatios.length - 1; leftWidth++) {
                    a = 1;
                    while (true) {
                        count = a;
                        if (count >= croppedRatios.length - leftWidth) {
                            break;
                        }
                        int i11;
                        w1 = (croppedRatios.length - leftWidth) - count;
                        if (leftWidth <= 3) {
                            if (count <= (averageAspectRatio2 < 0.85f ? i : 3)) {
                                if (w1 > 3) {
                                    i4 = paddingsWidth;
                                    minHeight = minHeight2;
                                    i11 = w1;
                                    w1 = minWidth2;
                                } else {
                                    minHeight = minHeight2;
                                    MessageGroupedLayoutAttempt messageGroupedLayoutAttempt3 = messageGroupedLayoutAttempt;
                                    i4 = paddingsWidth;
                                    w1 = minWidth2;
                                    messageGroupedLayoutAttempt = new MessageGroupedLayoutAttempt(leftWidth, count, w1, multiHeight(croppedRatios, 0, leftWidth), multiHeight(croppedRatios, leftWidth, leftWidth + count), multiHeight(croppedRatios, leftWidth + count, croppedRatios.length));
                                    attempts.add(messageGroupedLayoutAttempt3);
                                }
                                a = count + 1;
                                minWidth2 = w1;
                                paddingsWidth = i4;
                                minHeight2 = minHeight;
                            }
                        }
                        i4 = paddingsWidth;
                        minHeight = minHeight2;
                        i11 = w1;
                        w1 = minWidth2;
                        a = count + 1;
                        minWidth2 = w1;
                        paddingsWidth = i4;
                        minHeight2 = minHeight;
                    }
                    w1 = minWidth2;
                    minHeight = minHeight2;
                }
                w1 = minWidth2;
                minHeight = minHeight2;
                for (count = 1; count < croppedRatios.length - 2; count++) {
                    a = 1;
                    while (true) {
                        leftWidth = a;
                        if (leftWidth >= croppedRatios.length - count) {
                            break;
                        }
                        int thirdLine;
                        a = 1;
                        while (true) {
                            minHeight2 = a;
                            if (minHeight2 >= (croppedRatios.length - count) - leftWidth) {
                                break;
                            }
                            minWidth2 = ((croppedRatios.length - count) - leftWidth) - minHeight2;
                            if (count > 3 || leftWidth > 3 || minHeight2 > 3) {
                                thirdLine = minHeight2;
                                i2 = leftWidth;
                                f2 = maxSizeHeight;
                                i5 = minHeight;
                            } else if (minWidth2 > 3) {
                                i3 = minWidth2;
                                thirdLine = minHeight2;
                                i2 = leftWidth;
                                f2 = maxSizeHeight;
                                i5 = minHeight;
                            } else {
                                float multiHeight = multiHeight(croppedRatios, 0, count);
                                min = multiHeight(croppedRatios, count, count + leftWidth);
                                float multiHeight2 = multiHeight(croppedRatios, count + leftWidth, (count + leftWidth) + minHeight2);
                                int multiHeight3 = multiHeight(croppedRatios, (count + leftWidth) + minHeight2, croppedRatios.length);
                                i8 = minHeight2;
                                i7 = 0;
                                i9 = minWidth2;
                                f2 = maxSizeHeight;
                                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt4 = messageGroupedLayoutAttempt;
                                f3 = multiHeight;
                                f4 = min;
                                thirdLine = minHeight2;
                                i5 = minHeight;
                                f5 = multiHeight2;
                                i2 = leftWidth;
                                messageGroupedLayoutAttempt = new MessageGroupedLayoutAttempt(count, leftWidth, i8, i9, f3, f4, f5, multiHeight3);
                                attempts.add(messageGroupedLayoutAttempt4);
                            }
                            a = thirdLine + 1;
                            minHeight = i5;
                            leftWidth = i2;
                            maxSizeHeight = f2;
                        }
                        thirdLine = minHeight2;
                        f2 = maxSizeHeight;
                        i5 = minHeight;
                        a = leftWidth + 1;
                    }
                    i2 = leftWidth;
                    f2 = maxSizeHeight;
                    i5 = minHeight;
                }
                f2 = maxSizeHeight;
                i5 = minHeight;
                i7 = 0;
                thirdHeight = (float) ((r10.maxSizeWidth / 3) * 4);
                h = 0.0f;
                MessageGroupedLayoutAttempt optimal = null;
                for (a = 0; a < attempts.size(); a++) {
                    MessageGroupedLayoutAttempt attempt = (MessageGroupedLayoutAttempt) attempts.get(a);
                    f4 = Float.MAX_VALUE;
                    f3 = 0.0f;
                    for (i9 = 0; i9 < attempt.heights.length; i9++) {
                        f3 += attempt.heights[i9];
                        if (attempt.heights[i9] < f4) {
                            f4 = attempt.heights[i9];
                        }
                    }
                    maxAspectRatio2 = Math.abs(f3 - thirdHeight);
                    if (attempt.lineCounts.length > 1) {
                        if (attempt.lineCounts[0] <= attempt.lineCounts[1] && (attempt.lineCounts.length <= 2 || attempt.lineCounts[1] <= attempt.lineCounts[2])) {
                            if (attempt.lineCounts.length > 3 && attempt.lineCounts[2] > attempt.lineCounts[3]) {
                            }
                        }
                        maxAspectRatio2 *= 1.2f;
                    }
                    if (f4 < ((float) w1)) {
                        maxAspectRatio2 *= 1.5f;
                    }
                    if (optimal == null || maxAspectRatio2 < optimalDiff) {
                        optimal = attempt;
                        h = maxAspectRatio2;
                    }
                }
                if (optimal != null) {
                    maxAspectRatio2 = 0.0f;
                    maxX2 = maxX;
                    i8 = 0;
                    a = 0;
                    while (a < optimal.lineCounts.length) {
                        byte maxX4;
                        minWidth2 = optimal.lineCounts[a];
                        f5 = optimal.heights[a];
                        leftWidth = r10.maxSizeWidth;
                        GroupedMessagePosition posToFix = null;
                        float maxHeight = thirdHeight;
                        maxX2 = Math.max(maxX2, minWidth2 - 1);
                        width = 0;
                        while (width < minWidth2) {
                            float optimalDiff = h;
                            firstWidth = (int) (croppedRatios[i8] * f5);
                            leftWidth -= firstWidth;
                            maxX4 = maxX2;
                            position = (GroupedMessagePosition) r10.posArray.get(i8);
                            i = 0;
                            if (a == 0) {
                                i = 0 | 4;
                            }
                            int spanLeft = leftWidth;
                            if (a == optimal.lineCounts.length - 1) {
                                i |= 8;
                            }
                            if (width == 0) {
                                i |= 1;
                                if (isOut2) {
                                    posToFix = position;
                                }
                            }
                            if (width == minWidth2 - 1) {
                                leftWidth = i | 2;
                                if (!isOut2) {
                                    posToFix = position;
                                }
                            } else {
                                leftWidth = i;
                            }
                            position.set(width, width, a, a, firstWidth, f5 / f2, leftWidth);
                            i8++;
                            width++;
                            h = optimalDiff;
                            maxX2 = maxX4;
                            leftWidth = spanLeft;
                        }
                        maxX4 = maxX2;
                        posToFix.pw += leftWidth;
                        posToFix.spanSize += leftWidth;
                        maxAspectRatio2 += f5;
                        a++;
                        thirdHeight = maxHeight;
                    }
                    while (true) {
                        i10 = i7;
                        width = i6;
                        if (i10 >= width) {
                            position2 = (GroupedMessagePosition) r10.posArray.get(i10);
                            if (isOut2) {
                                position2.spanSize += r10.firstSpanAdditionalSize;
                                if ((position2.flags & 1) != 0) {
                                    position2.edge = true;
                                }
                            } else {
                                if (position2.minX == (byte) 0) {
                                    position2.spanSize += r10.firstSpanAdditionalSize;
                                }
                                if ((position2.flags & 2) == 0) {
                                    position2.edge = true;
                                    isOut = true;
                                }
                            }
                            messageObject = (MessageObject) r10.messages.get(i10);
                            if (position2.edge) {
                                if ((position2.flags & 2) == 0) {
                                    if (position2.spanSize == 1000) {
                                        position2.spanSize -= 108;
                                    } else if (position2.leftSpanOffset == 0) {
                                        position2.leftSpanOffset += 108;
                                    }
                                }
                                i7 = i10 + 1;
                                i6 = width;
                            } else {
                                if (position2.spanSize != 1000) {
                                    position2.spanSize += 108;
                                }
                                position2.pw += 108;
                                i7 = i10 + 1;
                                i6 = width;
                            }
                        } else {
                            return;
                        }
                    }
                }
            }
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
            return (this.directionFlags & 1) != 0 && (this.directionFlags & 2) == 0;
        }
    }

    public MessageObject(int accountNum, Message message, String formattedMessage, String name, String userName, boolean localMessage, boolean isChannel) {
        this.type = 1000;
        this.localType = localMessage ? 2 : 1;
        this.currentAccount = accountNum;
        this.localName = name;
        this.localUserName = userName;
        this.messageText = formattedMessage;
        this.messageOwner = message;
        this.localChannel = isChannel;
    }

    public MessageObject(int accountNum, Message message, AbstractMap<Integer, User> users, boolean generateLayout) {
        this(accountNum, message, (AbstractMap) users, null, generateLayout);
    }

    public MessageObject(int accountNum, Message message, SparseArray<User> users, boolean generateLayout) {
        this(accountNum, message, (SparseArray) users, null, generateLayout);
    }

    public MessageObject(int accountNum, Message message, boolean generateLayout) {
        this(accountNum, message, null, null, null, null, generateLayout, 0);
    }

    public MessageObject(int accountNum, Message message, AbstractMap<Integer, User> users, AbstractMap<Integer, Chat> chats, boolean generateLayout) {
        this(accountNum, message, (AbstractMap) users, (AbstractMap) chats, generateLayout, 0);
    }

    public MessageObject(int accountNum, Message message, SparseArray<User> users, SparseArray<Chat> chats, boolean generateLayout) {
        this(accountNum, message, null, null, users, chats, generateLayout, 0);
    }

    public MessageObject(int accountNum, Message message, AbstractMap<Integer, User> users, AbstractMap<Integer, Chat> chats, boolean generateLayout, long eid) {
        this(accountNum, message, users, chats, null, null, generateLayout, eid);
    }

    public MessageObject(int accountNum, Message message, AbstractMap<Integer, User> users, AbstractMap<Integer, Chat> chats, SparseArray<User> sUsers, SparseArray<Chat> sChats, boolean generateLayout, long eid) {
        boolean z;
        String duration;
        int uid;
        VideoEditedInfo videoEditedInfo;
        boolean z2;
        TextPaint paint;
        int[] emojiOnly;
        Message message2 = message;
        AbstractMap<Integer, User> abstractMap = users;
        AbstractMap<Integer, Chat> abstractMap2 = chats;
        SparseArray<User> sparseArray = sUsers;
        SparseArray<Chat> sparseArray2 = sChats;
        boolean z3 = generateLayout;
        this.type = 1000;
        Theme.createChatResources(null, true);
        int i = accountNum;
        this.currentAccount = i;
        this.messageOwner = message2;
        this.eventId = eid;
        if (message2.replyMessage != null) {
            MessageObject messageObject = r8;
            z = true;
            MessageObject messageObject2 = new MessageObject(i, message2.replyMessage, abstractMap, abstractMap2, sparseArray, sparseArray2, false, eid);
            r6.replyMessageObject = messageObject;
        } else {
            z = true;
        }
        User fromUser = null;
        if (message2.from_id > 0) {
            if (abstractMap != null) {
                fromUser = (User) abstractMap.get(Integer.valueOf(message2.from_id));
            } else if (sparseArray != null) {
                fromUser = (User) sparseArray.get(message2.from_id);
            }
            if (fromUser == null) {
                fromUser = MessagesController.getInstance(accountNum).getUser(Integer.valueOf(message2.from_id));
            }
        }
        User fromUser2 = fromUser;
        SparseArray<User> sparseArray3;
        AbstractMap<Integer, User> abstractMap3;
        AbstractMap<Integer, Chat> abstractMap4;
        SparseArray<Chat> sparseArray4;
        String domain;
        if (message2 instanceof TL_messageService) {
            if (message2.action != null) {
                if (message2.action instanceof TL_messageActionCustomAction) {
                    r6.messageText = message2.action.message;
                } else if (message2.action instanceof TL_messageActionChatCreate) {
                    if (isOut()) {
                        r6.messageText = LocaleController.getString("ActionYouCreateGroup", R.string.ActionYouCreateGroup);
                    } else {
                        r6.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", R.string.ActionCreateGroup), "un1", fromUser2);
                    }
                } else if (!(message2.action instanceof TL_messageActionChatDeleteUser)) {
                    if (message2.action instanceof TL_messageActionChatAddUser) {
                        int singleUserId = r6.messageOwner.action.user_id;
                        if (singleUserId == 0 && r6.messageOwner.action.users.size() == z) {
                            singleUserId = ((Integer) r6.messageOwner.action.users.get(0)).intValue();
                        }
                        i = singleUserId;
                        if (i != 0) {
                            User whoUser = null;
                            if (abstractMap != null) {
                                whoUser = (User) abstractMap.get(Integer.valueOf(i));
                            } else if (sparseArray != null) {
                                whoUser = (User) sparseArray.get(i);
                            }
                            if (whoUser == null) {
                                whoUser = MessagesController.getInstance(accountNum).getUser(Integer.valueOf(i));
                            }
                            User whoUser2 = whoUser;
                            if (i == message2.from_id) {
                                if (message2.to_id.channel_id != 0 && !isMegagroup()) {
                                    r6.messageText = LocaleController.getString("ChannelJoined", R.string.ChannelJoined);
                                } else if (message2.to_id.channel_id == 0 || !isMegagroup()) {
                                    if (isOut()) {
                                        r6.messageText = LocaleController.getString("ActionAddUserSelfYou", R.string.ActionAddUserSelfYou);
                                    } else {
                                        r6.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", R.string.ActionAddUserSelf), "un1", fromUser2);
                                    }
                                } else if (i == UserConfig.getInstance(r6.currentAccount).getClientUserId()) {
                                    r6.messageText = LocaleController.getString("ChannelMegaJoined", R.string.ChannelMegaJoined);
                                } else {
                                    r6.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", R.string.ActionAddUserSelfMega), "un1", fromUser2);
                                }
                            } else if (isOut()) {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", R.string.ActionYouAddUser), "un2", whoUser2);
                            } else if (i != UserConfig.getInstance(r6.currentAccount).getClientUserId()) {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", R.string.ActionAddUser), "un2", whoUser2);
                                r6.messageText = replaceWithLink(r6.messageText, "un1", fromUser2);
                            } else if (message2.to_id.channel_id == 0) {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", R.string.ActionAddUserYou), "un1", fromUser2);
                            } else if (isMegagroup()) {
                                r6.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", R.string.MegaAddedBy), "un1", fromUser2);
                            } else {
                                r6.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", R.string.ChannelAddedBy), "un1", fromUser2);
                            }
                            sparseArray3 = sparseArray;
                            abstractMap3 = abstractMap;
                            abstractMap4 = chats;
                        } else if (isOut()) {
                            SparseArray<Chat> sparseArray5 = sparseArray2;
                            abstractMap4 = chats;
                            abstractMap3 = abstractMap;
                            r6.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", R.string.ActionYouAddUser), "un2", message2.action.users, abstractMap, sparseArray);
                        } else {
                            abstractMap4 = chats;
                            r6.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", R.string.ActionAddUser), "un2", message2.action.users, abstractMap, sparseArray);
                            r6.messageText = replaceWithLink(r6.messageText, "un1", fromUser2);
                        }
                    } else {
                        sparseArray3 = sparseArray;
                        boolean z4 = z;
                        abstractMap3 = abstractMap;
                        abstractMap4 = chats;
                        if (message2.action instanceof TL_messageActionChatJoinedByLink) {
                            if (isOut()) {
                                r6.messageText = LocaleController.getString("ActionInviteYou", R.string.ActionInviteYou);
                            } else {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", R.string.ActionInviteUser), "un1", fromUser2);
                            }
                        } else if (message2.action instanceof TL_messageActionChatEditPhoto) {
                            if (message2.to_id.channel_id != 0 && !isMegagroup()) {
                                r6.messageText = LocaleController.getString("ActionChannelChangedPhoto", R.string.ActionChannelChangedPhoto);
                            } else if (isOut()) {
                                r6.messageText = LocaleController.getString("ActionYouChangedPhoto", R.string.ActionYouChangedPhoto);
                            } else {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", R.string.ActionChangedPhoto), "un1", fromUser2);
                            }
                        } else if (message2.action instanceof TL_messageActionChatEditTitle) {
                            if (message2.to_id.channel_id != 0 && !isMegagroup()) {
                                r6.messageText = LocaleController.getString("ActionChannelChangedTitle", R.string.ActionChannelChangedTitle).replace("un2", message2.action.title);
                            } else if (isOut()) {
                                r6.messageText = LocaleController.getString("ActionYouChangedTitle", R.string.ActionYouChangedTitle).replace("un2", message2.action.title);
                            } else {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", R.string.ActionChangedTitle).replace("un2", message2.action.title), "un1", fromUser2);
                            }
                        } else if (message2.action instanceof TL_messageActionChatDeletePhoto) {
                            if (message2.to_id.channel_id != 0 && !isMegagroup()) {
                                r6.messageText = LocaleController.getString("ActionChannelRemovedPhoto", R.string.ActionChannelRemovedPhoto);
                            } else if (isOut()) {
                                r6.messageText = LocaleController.getString("ActionYouRemovedPhoto", R.string.ActionYouRemovedPhoto);
                            } else {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", R.string.ActionRemovedPhoto), "un1", fromUser2);
                            }
                        } else if (message2.action instanceof TL_messageActionTTLChange) {
                            if (message2.action.ttl != 0) {
                                if (isOut()) {
                                    Object[] objArr = new Object[z4];
                                    objArr[0] = LocaleController.formatTTLString(message2.action.ttl);
                                    r6.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", R.string.MessageLifetimeChangedOutgoing, objArr);
                                } else {
                                    r6.messageText = LocaleController.formatString("MessageLifetimeChanged", R.string.MessageLifetimeChanged, UserObject.getFirstName(fromUser2), LocaleController.formatTTLString(message2.action.ttl));
                                }
                            } else if (isOut()) {
                                r6.messageText = LocaleController.getString("MessageLifetimeYouRemoved", R.string.MessageLifetimeYouRemoved);
                            } else {
                                r2 = new Object[z4];
                                r2[0] = UserObject.getFirstName(fromUser2);
                                r6.messageText = LocaleController.formatString("MessageLifetimeRemoved", R.string.MessageLifetimeRemoved, r2);
                            }
                        } else if (message2.action instanceof TL_messageActionLoginUnknownLocation) {
                            long time = ((long) message2.date) * 1000;
                            if (LocaleController.getInstance().formatterDay == null || LocaleController.getInstance().formatterYear == null) {
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                stringBuilder.append(message2.date);
                                date = stringBuilder.toString();
                            } else {
                                date = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(time), LocaleController.getInstance().formatterDay.format(time));
                            }
                            User to_user = UserConfig.getInstance(r6.currentAccount).getCurrentUser();
                            if (to_user == null) {
                                if (abstractMap3 != null) {
                                    to_user = (User) abstractMap3.get(Integer.valueOf(r6.messageOwner.to_id.user_id));
                                } else if (sparseArray3 != null) {
                                    to_user = (User) sparseArray3.get(r6.messageOwner.to_id.user_id);
                                }
                                if (to_user == null) {
                                    to_user = MessagesController.getInstance(accountNum).getUser(Integer.valueOf(r6.messageOwner.to_id.user_id));
                                }
                            }
                            name = to_user != null ? UserObject.getFirstName(to_user) : TtmlNode.ANONYMOUS_REGION_ID;
                            r6.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", R.string.NotificationUnrecognizedDevice, name, date, message2.action.title, message2.action.address);
                        } else if (message2.action instanceof TL_messageActionUserJoined) {
                            r2 = new Object[z4];
                            r2[0] = UserObject.getUserName(fromUser2);
                            r6.messageText = LocaleController.formatString("NotificationContactJoined", R.string.NotificationContactJoined, r2);
                        } else if (message2.action instanceof TL_messageActionUserUpdatedPhoto) {
                            r2 = new Object[z4];
                            r2[0] = UserObject.getUserName(fromUser2);
                            r6.messageText = LocaleController.formatString("NotificationContactNewPhoto", R.string.NotificationContactNewPhoto, r2);
                        } else if (message2.action instanceof TL_messageEncryptedAction) {
                            if (message2.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                                if (isOut()) {
                                    r6.messageText = LocaleController.formatString("ActionTakeScreenshootYou", R.string.ActionTakeScreenshootYou, new Object[0]);
                                } else {
                                    r6.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", R.string.ActionTakeScreenshoot), "un1", fromUser2);
                                }
                            } else if (message2.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                                TL_decryptedMessageActionSetMessageTTL action = message2.action.encryptedAction;
                                if (action.ttl_seconds != 0) {
                                    if (isOut()) {
                                        r2 = new Object[z4];
                                        r2[0] = LocaleController.formatTTLString(action.ttl_seconds);
                                        r6.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", R.string.MessageLifetimeChangedOutgoing, r2);
                                    } else {
                                        r6.messageText = LocaleController.formatString("MessageLifetimeChanged", R.string.MessageLifetimeChanged, UserObject.getFirstName(fromUser2), LocaleController.formatTTLString(action.ttl_seconds));
                                    }
                                } else if (isOut()) {
                                    r6.messageText = LocaleController.getString("MessageLifetimeYouRemoved", R.string.MessageLifetimeYouRemoved);
                                } else {
                                    Object[] objArr2 = new Object[z4];
                                    objArr2[0] = UserObject.getFirstName(fromUser2);
                                    r6.messageText = LocaleController.formatString("MessageLifetimeRemoved", R.string.MessageLifetimeRemoved, objArr2);
                                }
                            }
                        } else if (message2.action instanceof TL_messageActionScreenshotTaken) {
                            if (isOut()) {
                                r6.messageText = LocaleController.formatString("ActionTakeScreenshootYou", R.string.ActionTakeScreenshootYou, new Object[0]);
                            } else {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", R.string.ActionTakeScreenshoot), "un1", fromUser2);
                            }
                        } else if (message2.action instanceof TL_messageActionCreatedBroadcastList) {
                            r6.messageText = LocaleController.formatString("YouCreatedBroadcastList", R.string.YouCreatedBroadcastList, new Object[0]);
                        } else if (message2.action instanceof TL_messageActionChannelCreate) {
                            if (isMegagroup()) {
                                r6.messageText = LocaleController.getString("ActionCreateMega", R.string.ActionCreateMega);
                            } else {
                                r6.messageText = LocaleController.getString("ActionCreateChannel", R.string.ActionCreateChannel);
                            }
                        } else if (message2.action instanceof TL_messageActionChatMigrateTo) {
                            r6.messageText = LocaleController.getString("ActionMigrateFromGroup", R.string.ActionMigrateFromGroup);
                        } else if (message2.action instanceof TL_messageActionChannelMigrateFrom) {
                            r6.messageText = LocaleController.getString("ActionMigrateFromGroup", R.string.ActionMigrateFromGroup);
                        } else if (message2.action instanceof TL_messageActionPinMessage) {
                            Chat chat;
                            if (fromUser2 != null) {
                                sparseArray4 = sChats;
                                chat = null;
                            } else if (abstractMap4 != null) {
                                chat = (Chat) abstractMap4.get(Integer.valueOf(message2.to_id.channel_id));
                                sparseArray4 = sChats;
                            } else {
                                sparseArray4 = sChats;
                                if (sparseArray4 != null) {
                                    chat = (Chat) sparseArray4.get(message2.to_id.channel_id);
                                } else {
                                    chat = null;
                                }
                            }
                            generatePinMessageText(fromUser2, chat);
                        } else {
                            sparseArray4 = sChats;
                            if (message2.action instanceof TL_messageActionHistoryClear) {
                                r6.messageText = LocaleController.getString("HistoryCleared", R.string.HistoryCleared);
                            } else if (message2.action instanceof TL_messageActionGameScore) {
                                generateGameMessageText(fromUser2);
                            } else if (message2.action instanceof TL_messageActionPhoneCall) {
                                TL_messageActionPhoneCall call = r6.messageOwner.action;
                                boolean isMissed = call.reason instanceof TL_phoneCallDiscardReasonMissed;
                                if (r6.messageOwner.from_id == UserConfig.getInstance(r6.currentAccount).getClientUserId()) {
                                    if (isMissed) {
                                        r6.messageText = LocaleController.getString("CallMessageOutgoingMissed", R.string.CallMessageOutgoingMissed);
                                    } else {
                                        r6.messageText = LocaleController.getString("CallMessageOutgoing", R.string.CallMessageOutgoing);
                                    }
                                } else if (isMissed) {
                                    r6.messageText = LocaleController.getString("CallMessageIncomingMissed", R.string.CallMessageIncomingMissed);
                                } else if (call.reason instanceof TL_phoneCallDiscardReasonBusy) {
                                    r6.messageText = LocaleController.getString("CallMessageIncomingDeclined", R.string.CallMessageIncomingDeclined);
                                } else {
                                    r6.messageText = LocaleController.getString("CallMessageIncoming", R.string.CallMessageIncoming);
                                }
                                if (call.duration > 0) {
                                    duration = LocaleController.formatCallDuration(call.duration);
                                    r6.messageText = LocaleController.formatString("CallMessageWithDuration", R.string.CallMessageWithDuration, r6.messageText, duration);
                                    name = r6.messageText.toString();
                                    int start = name.indexOf(duration);
                                    if (start != -1) {
                                        SpannableString sp = new SpannableString(r6.messageText);
                                        i = duration.length() + start;
                                        if (start > 0 && name.charAt(start - 1) == '(') {
                                            start--;
                                        }
                                        if (i < name.length() && name.charAt(i) == ')') {
                                            i++;
                                        }
                                        sp.setSpan(new TypefaceSpan(Typeface.DEFAULT), start, i, 0);
                                        r6.messageText = sp;
                                    }
                                }
                            } else if (message2.action instanceof TL_messageActionPaymentSent) {
                                uid = (int) getDialogId();
                                if (abstractMap3 != null) {
                                    fromUser2 = (User) abstractMap3.get(Integer.valueOf(uid));
                                } else if (sparseArray3 != null) {
                                    fromUser2 = (User) sparseArray3.get(uid);
                                }
                                if (fromUser2 == null) {
                                    fromUser2 = MessagesController.getInstance(accountNum).getUser(Integer.valueOf(uid));
                                }
                                generatePaymentSentMessageText(null);
                            } else if (message2.action instanceof TL_messageActionBotAllowed) {
                                domain = ((TL_messageActionBotAllowed) message2.action).domain;
                                date = LocaleController.getString("ActionBotAllowed", R.string.ActionBotAllowed);
                                int start2 = date.indexOf("%1$s");
                                SpannableString str = new SpannableString(String.format(date, new Object[]{domain}));
                                if (start2 >= 0) {
                                    StringBuilder stringBuilder2 = new StringBuilder();
                                    stringBuilder2.append("http://");
                                    stringBuilder2.append(domain);
                                    str.setSpan(new URLSpanNoUnderlineBold(stringBuilder2.toString()), start2, domain.length() + start2, 33);
                                }
                                r6.messageText = str;
                            }
                        }
                    }
                    sparseArray4 = sChats;
                } else if (message2.action.user_id != message2.from_id) {
                    fromUser = null;
                    if (abstractMap != null) {
                        fromUser = (User) abstractMap.get(Integer.valueOf(message2.action.user_id));
                    } else if (sparseArray != null) {
                        fromUser = (User) sparseArray.get(message2.action.user_id);
                    }
                    if (fromUser == null) {
                        fromUser = MessagesController.getInstance(accountNum).getUser(Integer.valueOf(message2.action.user_id));
                    }
                    if (isOut()) {
                        r6.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", R.string.ActionYouKickUser), "un2", fromUser);
                    } else if (message2.action.user_id == UserConfig.getInstance(r6.currentAccount).getClientUserId()) {
                        r6.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", R.string.ActionKickUserYou), "un1", fromUser2);
                    } else {
                        r6.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", R.string.ActionKickUser), "un2", fromUser);
                        r6.messageText = replaceWithLink(r6.messageText, "un1", fromUser2);
                    }
                } else if (isOut()) {
                    r6.messageText = LocaleController.getString("ActionYouLeftUser", R.string.ActionYouLeftUser);
                } else {
                    r6.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", R.string.ActionLeftUser), "un1", fromUser2);
                }
            }
            sparseArray4 = sparseArray2;
            sparseArray3 = sparseArray;
            abstractMap3 = abstractMap;
            abstractMap4 = chats;
        } else {
            sparseArray4 = sparseArray2;
            sparseArray3 = sparseArray;
            abstractMap3 = abstractMap;
            abstractMap4 = chats;
            if (isMediaEmpty()) {
                r6.messageText = message2.message;
            } else if (message2.media instanceof TL_messageMediaPhoto) {
                r6.messageText = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
            } else {
                if (!isVideo()) {
                    if (!(message2.media instanceof TL_messageMediaDocument) || !(message2.media.document instanceof TL_documentEmpty) || message2.media.ttl_seconds == 0) {
                        if (isVoice()) {
                            r6.messageText = LocaleController.getString("AttachAudio", R.string.AttachAudio);
                        } else if (isRoundVideo()) {
                            r6.messageText = LocaleController.getString("AttachRound", R.string.AttachRound);
                        } else {
                            if (!(message2.media instanceof TL_messageMediaGeo)) {
                                if (!(message2.media instanceof TL_messageMediaVenue)) {
                                    if (message2.media instanceof TL_messageMediaGeoLive) {
                                        r6.messageText = LocaleController.getString("AttachLiveLocation", R.string.AttachLiveLocation);
                                    } else if (message2.media instanceof TL_messageMediaContact) {
                                        r6.messageText = LocaleController.getString("AttachContact", R.string.AttachContact);
                                    } else if (message2.media instanceof TL_messageMediaGame) {
                                        r6.messageText = message2.message;
                                    } else if (message2.media instanceof TL_messageMediaInvoice) {
                                        r6.messageText = message2.media.description;
                                    } else if (message2.media instanceof TL_messageMediaUnsupported) {
                                        r6.messageText = LocaleController.getString("UnsupportedMedia", R.string.UnsupportedMedia);
                                    } else if (message2.media instanceof TL_messageMediaDocument) {
                                        if (isSticker()) {
                                            domain = getStrickerChar();
                                            if (domain == null || domain.length() <= 0) {
                                                r6.messageText = LocaleController.getString("AttachSticker", R.string.AttachSticker);
                                            } else {
                                                r6.messageText = String.format("%s %s", new Object[]{domain, LocaleController.getString("AttachSticker", R.string.AttachSticker)});
                                            }
                                        } else if (isMusic()) {
                                            r6.messageText = LocaleController.getString("AttachMusic", R.string.AttachMusic);
                                        } else if (isGif()) {
                                            r6.messageText = LocaleController.getString("AttachGif", R.string.AttachGif);
                                        } else {
                                            domain = FileLoader.getDocumentFileName(message2.media.document);
                                            if (domain == null || domain.length() <= 0) {
                                                r6.messageText = LocaleController.getString("AttachDocument", R.string.AttachDocument);
                                            } else {
                                                r6.messageText = domain;
                                            }
                                        }
                                    }
                                }
                            }
                            r6.messageText = LocaleController.getString("AttachLocation", R.string.AttachLocation);
                        }
                    }
                }
                r6.messageText = LocaleController.getString("AttachVideo", R.string.AttachVideo);
            }
        }
        if (r6.messageText == null) {
            r6.messageText = TtmlNode.ANONYMOUS_REGION_ID;
        }
        setType();
        measureInlineBotButtons();
        Calendar rightNow = new GregorianCalendar();
        rightNow.setTimeInMillis(((long) r6.messageOwner.date) * 1000);
        uid = rightNow.get(6);
        int dateYear = rightNow.get(1);
        int dateMonth = rightNow.get(2);
        r6.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(dateYear), Integer.valueOf(dateMonth), Integer.valueOf(uid)});
        r6.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(dateYear), Integer.valueOf(dateMonth)});
        if (!(r6.messageOwner.message == null || r6.messageOwner.id >= 0 || r6.messageOwner.params == null)) {
            duration = (String) r6.messageOwner.params.get("ve");
            String ve = duration;
            if (duration != null && (isVideo() || isNewGif() || isRoundVideo())) {
                r6.videoEditedInfo = new VideoEditedInfo();
                if (r6.videoEditedInfo.parseString(ve)) {
                    videoEditedInfo = null;
                    r6.videoEditedInfo.roundVideo = isRoundVideo();
                } else {
                    videoEditedInfo = null;
                    r6.videoEditedInfo = null;
                }
                generateCaption();
                z2 = generateLayout;
                if (z2) {
                    int i2 = uid;
                } else {
                    if (r6.messageOwner.media instanceof TL_messageMediaGame) {
                        paint = Theme.chat_msgTextPaint;
                    } else {
                        paint = Theme.chat_msgGameTextPaint;
                    }
                    emojiOnly = SharedConfig.allowBigEmoji ? new int[1] : videoEditedInfo;
                    r6.messageText = Emoji.replaceEmoji(r6.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly);
                    if (emojiOnly != null || emojiOnly[0] < 1 || emojiOnly[0] > 3) {
                        int[] iArr = emojiOnly;
                    } else {
                        TextPaint emojiPaint;
                        switch (emojiOnly[0]) {
                            case 1:
                                emojiPaint = Theme.chat_msgTextPaintOneEmoji;
                                rightNow = AndroidUtilities.dp(32.0f);
                                break;
                            case 2:
                                emojiPaint = Theme.chat_msgTextPaintTwoEmoji;
                                rightNow = AndroidUtilities.dp(28.0f);
                                break;
                            default:
                                emojiPaint = Theme.chat_msgTextPaintThreeEmoji;
                                rightNow = AndroidUtilities.dp(24.0f);
                                break;
                        }
                        EmojiSpan[] dateDay = (EmojiSpan[]) ((Spannable) r6.messageText).getSpans(null, r6.messageText.length(), EmojiSpan.class);
                        if (dateDay != null && dateDay.length > 0) {
                            for (EmojiSpan replaceFontMetrics : dateDay) {
                                replaceFontMetrics.replaceFontMetrics(emojiPaint.getFontMetricsInt(), rightNow);
                            }
                        }
                    }
                    generateLayout(fromUser2);
                }
                r6.layoutCreated = z2;
                generateThumbs(false);
                checkMediaExistance();
            }
        }
        videoEditedInfo = null;
        generateCaption();
        z2 = generateLayout;
        if (z2) {
            int i22 = uid;
        } else {
            if (r6.messageOwner.media instanceof TL_messageMediaGame) {
                paint = Theme.chat_msgTextPaint;
            } else {
                paint = Theme.chat_msgGameTextPaint;
            }
            if (SharedConfig.allowBigEmoji) {
            }
            emojiOnly = SharedConfig.allowBigEmoji ? new int[1] : videoEditedInfo;
            r6.messageText = Emoji.replaceEmoji(r6.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly);
            if (emojiOnly != null) {
            }
            int[] iArr2 = emojiOnly;
            generateLayout(fromUser2);
        }
        r6.layoutCreated = z2;
        generateThumbs(false);
        checkMediaExistance();
    }

    private void createDateArray(int accountNum, TL_channelAdminLogEvent event, ArrayList<MessageObject> messageObjects, HashMap<String, ArrayList<MessageObject>> messagesByDays) {
        if (((ArrayList) messagesByDays.get(this.dateKey)) == null) {
            messagesByDays.put(this.dateKey, new ArrayList());
            TL_message dateMsg = new TL_message();
            dateMsg.message = LocaleController.formatDateChat((long) event.date);
            dateMsg.id = 0;
            dateMsg.date = event.date;
            MessageObject dateObj = new MessageObject(accountNum, dateMsg, false);
            dateObj.type = 10;
            dateObj.contentType = 1;
            dateObj.isDateObject = true;
            messageObjects.add(dateObj);
        }
    }

    public MessageObject(int accountNum, TL_channelAdminLogEvent event, ArrayList<MessageObject> messageObjects, HashMap<String, ArrayList<MessageObject>> messagesByDays, Chat chat, int[] mid) {
        int offset;
        int i;
        int i2;
        Message message;
        Message newMessage;
        MediaController mediaController;
        int i3;
        MessageObject messageObject;
        MessageObject player;
        TextPaint paint;
        int[] emojiOnly;
        TextPaint emojiPaint;
        EmojiSpan[] spans;
        MessageObject player2;
        TL_channelAdminLogEvent tL_channelAdminLogEvent = event;
        ArrayList<MessageObject> arrayList = messageObjects;
        Chat chat2 = chat;
        this.type = 1000;
        User fromUser = null;
        if (tL_channelAdminLogEvent.user_id > 0 && null == null) {
            fromUser = MessagesController.getInstance(accountNum).getUser(Integer.valueOf(tL_channelAdminLogEvent.user_id));
        }
        r0.currentEvent = tL_channelAdminLogEvent;
        Calendar rightNow = new GregorianCalendar();
        rightNow.setTimeInMillis(((long) tL_channelAdminLogEvent.date) * 1000);
        int dateDay = rightNow.get(6);
        int dateYear = rightNow.get(1);
        int dateMonth = rightNow.get(2);
        r0.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(dateYear), Integer.valueOf(dateMonth), Integer.valueOf(dateDay)});
        r0.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(dateYear), Integer.valueOf(dateMonth)});
        Peer to_id = new TL_peerChannel();
        to_id.channel_id = chat2.id;
        Message message2 = null;
        int i4;
        if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionChangeTitle) {
            String title = ((TL_channelAdminLogEventActionChangeTitle) tL_channelAdminLogEvent.action).new_value;
            if (chat2.megagroup) {
                r0.messageText = replaceWithLink(LocaleController.formatString("EventLogEditedGroupTitle", R.string.EventLogEditedGroupTitle, title), "un1", fromUser);
            } else {
                i4 = dateDay;
                r0.messageText = replaceWithLink(LocaleController.formatString("EventLogEditedChannelTitle", R.string.EventLogEditedChannelTitle, title), "un1", fromUser);
            }
        } else {
            i4 = dateDay;
            if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionChangePhoto) {
                r0.messageOwner = new TL_messageService();
                if (tL_channelAdminLogEvent.action.new_photo instanceof TL_chatPhotoEmpty) {
                    r0.messageOwner.action = new TL_messageActionChatDeletePhoto();
                    if (chat2.megagroup) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogRemovedWGroupPhoto", R.string.EventLogRemovedWGroupPhoto), "un1", fromUser);
                    } else {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogRemovedChannelPhoto", R.string.EventLogRemovedChannelPhoto), "un1", fromUser);
                    }
                } else {
                    r0.messageOwner.action = new TL_messageActionChatEditPhoto();
                    r0.messageOwner.action.photo = new TL_photo();
                    TL_photoSize photoSize = new TL_photoSize();
                    photoSize.location = tL_channelAdminLogEvent.action.new_photo.photo_small;
                    photoSize.type = "s";
                    photoSize.h = 80;
                    photoSize.w = 80;
                    r0.messageOwner.action.photo.sizes.add(photoSize);
                    photoSize = new TL_photoSize();
                    photoSize.location = tL_channelAdminLogEvent.action.new_photo.photo_big;
                    photoSize.type = "m";
                    photoSize.h = 640;
                    photoSize.w = 640;
                    r0.messageOwner.action.photo.sizes.add(photoSize);
                    if (chat2.megagroup) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogEditedGroupPhoto", R.string.EventLogEditedGroupPhoto), "un1", fromUser);
                    } else {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogEditedChannelPhoto", R.string.EventLogEditedChannelPhoto), "un1", fromUser);
                    }
                }
            } else if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionParticipantJoin) {
                if (chat2.megagroup) {
                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogGroupJoined", R.string.EventLogGroupJoined), "un1", fromUser);
                } else {
                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogChannelJoined", R.string.EventLogChannelJoined), "un1", fromUser);
                }
            } else if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionParticipantLeave) {
                r0.messageOwner = new TL_messageService();
                r0.messageOwner.action = new TL_messageActionChatDeleteUser();
                r0.messageOwner.action.user_id = tL_channelAdminLogEvent.user_id;
                if (chat2.megagroup) {
                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogLeftGroup", R.string.EventLogLeftGroup), "un1", fromUser);
                } else {
                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogLeftChannel", R.string.EventLogLeftChannel), "un1", fromUser);
                }
            } else if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionParticipantInvite) {
                r0.messageOwner = new TL_messageService();
                r0.messageOwner.action = new TL_messageActionChatAddUser();
                whoUser = MessagesController.getInstance(accountNum).getUser(Integer.valueOf(tL_channelAdminLogEvent.action.participant.user_id));
                if (tL_channelAdminLogEvent.action.participant.user_id != r0.messageOwner.from_id) {
                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogAdded", R.string.EventLogAdded), "un2", whoUser);
                    r0.messageText = replaceWithLink(r0.messageText, "un1", fromUser);
                } else if (chat2.megagroup) {
                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogGroupJoined", R.string.EventLogGroupJoined), "un1", fromUser);
                } else {
                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogChannelJoined", R.string.EventLogChannelJoined), "un1", fromUser);
                }
            } else if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionParticipantToggleAdmin) {
                r0.messageOwner = new TL_message();
                whoUser = MessagesController.getInstance(accountNum).getUser(Integer.valueOf(tL_channelAdminLogEvent.action.prev_participant.user_id));
                String str = LocaleController.getString("EventLogPromoted", R.string.EventLogPromoted);
                offset = str.indexOf("%1$s");
                rights = new StringBuilder(String.format(str, new Object[]{getUserName(whoUser, r0.messageOwner.entities, offset)}));
                rights.append("\n");
                TL_channelAdminRights o = tL_channelAdminLogEvent.action.prev_participant.admin_rights;
                TL_channelAdminRights n = tL_channelAdminLogEvent.action.new_participant.admin_rights;
                if (o == null) {
                    o = new TL_channelAdminRights();
                }
                if (n == null) {
                    n = new TL_channelAdminRights();
                }
                if (o.change_info != n.change_info) {
                    rights.append('\n');
                    rights.append(n.change_info ? '+' : '-');
                    rights.append(' ');
                    if (chat2.megagroup) {
                        r5 = "EventLogPromotedChangeGroupInfo";
                        dateDay = R.string.EventLogPromotedChangeGroupInfo;
                    } else {
                        r5 = "EventLogPromotedChangeChannelInfo";
                        dateDay = R.string.EventLogPromotedChangeChannelInfo;
                    }
                    rights.append(LocaleController.getString(r5, dateDay));
                }
                if (!chat2.megagroup) {
                    if (o.post_messages != n.post_messages) {
                        rights.append('\n');
                        rights.append(n.post_messages ? '+' : '-');
                        rights.append(' ');
                        rights.append(LocaleController.getString("EventLogPromotedPostMessages", R.string.EventLogPromotedPostMessages));
                    }
                    if (o.edit_messages != n.edit_messages) {
                        rights.append('\n');
                        rights.append(n.edit_messages ? '+' : '-');
                        rights.append(' ');
                        rights.append(LocaleController.getString("EventLogPromotedEditMessages", R.string.EventLogPromotedEditMessages));
                    }
                }
                if (o.delete_messages != n.delete_messages) {
                    rights.append('\n');
                    rights.append(n.delete_messages ? '+' : '-');
                    rights.append(' ');
                    rights.append(LocaleController.getString("EventLogPromotedDeleteMessages", R.string.EventLogPromotedDeleteMessages));
                }
                if (o.add_admins != n.add_admins) {
                    rights.append('\n');
                    rights.append(n.add_admins ? '+' : '-');
                    rights.append(' ');
                    rights.append(LocaleController.getString("EventLogPromotedAddAdmins", R.string.EventLogPromotedAddAdmins));
                }
                if (chat2.megagroup && o.ban_users != n.ban_users) {
                    rights.append('\n');
                    rights.append(n.ban_users ? '+' : '-');
                    rights.append(' ');
                    rights.append(LocaleController.getString("EventLogPromotedBanUsers", R.string.EventLogPromotedBanUsers));
                }
                if (o.invite_users != n.invite_users) {
                    rights.append('\n');
                    rights.append(n.invite_users ? '+' : '-');
                    rights.append(' ');
                    rights.append(LocaleController.getString("EventLogPromotedAddUsers", R.string.EventLogPromotedAddUsers));
                }
                if (chat2.megagroup && o.pin_messages != n.pin_messages) {
                    rights.append('\n');
                    rights.append(n.pin_messages ? '+' : '-');
                    rights.append(' ');
                    rights.append(LocaleController.getString("EventLogPromotedPinMessages", R.string.EventLogPromotedPinMessages));
                }
                r0.messageText = rights.toString();
            } else {
                StringBuilder bannedDuration;
                if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionParticipantToggleBan) {
                    String str2;
                    r0.messageOwner = new TL_message();
                    whoUser = MessagesController.getInstance(accountNum).getUser(Integer.valueOf(tL_channelAdminLogEvent.action.prev_participant.user_id));
                    TL_channelBannedRights o2 = tL_channelAdminLogEvent.action.prev_participant.banned_rights;
                    TL_channelBannedRights n2 = tL_channelAdminLogEvent.action.new_participant.banned_rights;
                    if (chat2.megagroup) {
                        char c;
                        if (n2 != null && n2.view_messages) {
                            if (n2 == null || o2 == null || n2.until_date == o2.until_date) {
                                i = dateYear;
                                i2 = dateMonth;
                                message = null;
                            }
                        }
                        if (n2 != null && !AndroidUtilities.isBannedForever(n2.until_date)) {
                            bannedDuration = new StringBuilder();
                            offset = n2.until_date - tL_channelAdminLogEvent.date;
                            int days = ((offset / 60) / 60) / 24;
                            offset -= ((days * 60) * 60) * 24;
                            dateYear = (offset / 60) / 60;
                            offset -= (dateYear * 60) * 60;
                            dateMonth = offset / 60;
                            int count = 0;
                            int a = 0;
                            while (true) {
                                int duration = offset;
                                message = message2;
                                offset = a;
                                if (offset < 3) {
                                    String addStr;
                                    int hours;
                                    int minutes;
                                    String addStr2;
                                    if (offset != 0) {
                                        addStr2 = null;
                                        if (offset == 1) {
                                            if (dateYear != 0) {
                                                addStr = LocaleController.formatPluralString("Hours", dateYear);
                                                count++;
                                            }
                                            hours = dateYear;
                                            dateYear = count;
                                            addStr = addStr2;
                                            if (addStr == null) {
                                                minutes = dateMonth;
                                            } else {
                                                if (bannedDuration.length() <= 0) {
                                                    minutes = dateMonth;
                                                } else {
                                                    minutes = dateMonth;
                                                    bannedDuration.append(", ");
                                                }
                                                bannedDuration.append(addStr);
                                            }
                                            if (dateYear != 2) {
                                                break;
                                            }
                                            a = offset + 1;
                                            count = dateYear;
                                            offset = duration;
                                            message2 = message;
                                            dateYear = hours;
                                            dateMonth = minutes;
                                        } else {
                                            if (dateMonth != 0) {
                                                addStr = LocaleController.formatPluralString("Minutes", dateMonth);
                                                count++;
                                            }
                                            hours = dateYear;
                                            dateYear = count;
                                            addStr = addStr2;
                                            if (addStr == null) {
                                                if (bannedDuration.length() <= 0) {
                                                    minutes = dateMonth;
                                                    bannedDuration.append(", ");
                                                } else {
                                                    minutes = dateMonth;
                                                }
                                                bannedDuration.append(addStr);
                                            } else {
                                                minutes = dateMonth;
                                            }
                                            if (dateYear != 2) {
                                                break;
                                            }
                                            a = offset + 1;
                                            count = dateYear;
                                            offset = duration;
                                            message2 = message;
                                            dateYear = hours;
                                            dateMonth = minutes;
                                        }
                                    } else if (days != 0) {
                                        addStr2 = null;
                                        addStr = LocaleController.formatPluralString("Days", days);
                                        count++;
                                    } else {
                                        addStr2 = null;
                                        hours = dateYear;
                                        dateYear = count;
                                        addStr = addStr2;
                                        if (addStr == null) {
                                            if (bannedDuration.length() <= 0) {
                                                minutes = dateMonth;
                                                bannedDuration.append(", ");
                                            } else {
                                                minutes = dateMonth;
                                            }
                                            bannedDuration.append(addStr);
                                        } else {
                                            minutes = dateMonth;
                                        }
                                        if (dateYear != 2) {
                                            break;
                                        }
                                        a = offset + 1;
                                        count = dateYear;
                                        offset = duration;
                                        message2 = message;
                                        dateYear = hours;
                                        dateMonth = minutes;
                                    }
                                    hours = dateYear;
                                    dateYear = count;
                                    if (addStr == null) {
                                        minutes = dateMonth;
                                    } else {
                                        if (bannedDuration.length() <= 0) {
                                            minutes = dateMonth;
                                        } else {
                                            minutes = dateMonth;
                                            bannedDuration.append(", ");
                                        }
                                        bannedDuration.append(addStr);
                                    }
                                    if (dateYear != 2) {
                                        break;
                                    }
                                    a = offset + 1;
                                    count = dateYear;
                                    offset = duration;
                                    message2 = message;
                                    dateYear = hours;
                                    dateMonth = minutes;
                                } else {
                                    break;
                                }
                            }
                        }
                        i2 = dateMonth;
                        message = null;
                        bannedDuration = new StringBuilder(LocaleController.getString("UserRestrictionsUntilForever", R.string.UserRestrictionsUntilForever));
                        dateYear = LocaleController.getString("EventLogRestrictedUntil", R.string.EventLogRestrictedUntil);
                        offset = dateYear.indexOf("%1$s");
                        dateMonth = new StringBuilder(String.format(dateYear, new Object[]{getUserName(whoUser, r0.messageOwner.entities, offset), bannedDuration.toString()}));
                        boolean added = false;
                        if (o2 == null) {
                            o2 = new TL_channelBannedRights();
                        }
                        if (n2 == null) {
                            n2 = new TL_channelBannedRights();
                        }
                        if (o2.view_messages != n2.view_messages) {
                            if (null == null) {
                                c = '\n';
                                dateMonth.append('\n');
                                added = true;
                            } else {
                                c = '\n';
                            }
                            dateMonth.append(c);
                            dateMonth.append(!n2.view_messages ? '+' : '-');
                            dateMonth.append(' ');
                            dateMonth.append(LocaleController.getString("EventLogRestrictedReadMessages", R.string.EventLogRestrictedReadMessages));
                        }
                        if (o2.send_messages != n2.send_messages) {
                            if (added) {
                                c = '\n';
                            } else {
                                c = '\n';
                                dateMonth.append('\n');
                                added = true;
                            }
                            dateMonth.append(c);
                            dateMonth.append(!n2.send_messages ? '+' : '-');
                            dateMonth.append(' ');
                            dateMonth.append(LocaleController.getString("EventLogRestrictedSendMessages", R.string.EventLogRestrictedSendMessages));
                        }
                        if (!(o2.send_stickers == n2.send_stickers && o2.send_inline == n2.send_inline && o2.send_gifs == n2.send_gifs && o2.send_games == n2.send_games)) {
                            if (added) {
                                c = '\n';
                            } else {
                                c = '\n';
                                dateMonth.append('\n');
                                added = true;
                            }
                            dateMonth.append(c);
                            dateMonth.append(!n2.send_stickers ? '+' : '-');
                            dateMonth.append(' ');
                            dateMonth.append(LocaleController.getString("EventLogRestrictedSendStickers", R.string.EventLogRestrictedSendStickers));
                        }
                        if (o2.send_media != n2.send_media) {
                            if (added) {
                                c = '\n';
                            } else {
                                c = '\n';
                                dateMonth.append('\n');
                                added = true;
                            }
                            dateMonth.append(c);
                            dateMonth.append(!n2.send_media ? '+' : '-');
                            dateMonth.append(' ');
                            dateMonth.append(LocaleController.getString("EventLogRestrictedSendMedia", R.string.EventLogRestrictedSendMedia));
                        }
                        if (o2.embed_links != n2.embed_links) {
                            if (added) {
                                c = '\n';
                            } else {
                                c = '\n';
                                dateMonth.append('\n');
                            }
                            dateMonth.append(c);
                            dateMonth.append(!n2.embed_links ? '+' : '-');
                            dateMonth.append(' ');
                            dateMonth.append(LocaleController.getString("EventLogRestrictedSendEmbed", R.string.EventLogRestrictedSendEmbed));
                        }
                        r0.messageText = dateMonth.toString();
                    } else {
                        i2 = dateMonth;
                        message = null;
                    }
                    if (n2 == null || !(o2 == null || n2.view_messages)) {
                        str2 = LocaleController.getString("EventLogChannelUnrestricted", R.string.EventLogChannelUnrestricted);
                    } else {
                        str2 = LocaleController.getString("EventLogChannelRestricted", R.string.EventLogChannelRestricted);
                    }
                    dateYear = str2.indexOf("%1$s");
                    r0.messageText = String.format(str2, new Object[]{getUserName(whoUser, r0.messageOwner.entities, dateYear)});
                } else {
                    i2 = dateMonth;
                    message = null;
                    if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionUpdatePinned) {
                        if (tL_channelAdminLogEvent.action.message instanceof TL_messageEmpty) {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogUnpinnedMessages", R.string.EventLogUnpinnedMessages), "un1", fromUser);
                        } else {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogPinnedMessages", R.string.EventLogPinnedMessages), "un1", fromUser);
                        }
                    } else if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionToggleSignatures) {
                        if (((TL_channelAdminLogEventActionToggleSignatures) tL_channelAdminLogEvent.action).new_value) {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledSignaturesOn", R.string.EventLogToggledSignaturesOn), "un1", fromUser);
                        } else {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledSignaturesOff", R.string.EventLogToggledSignaturesOff), "un1", fromUser);
                        }
                    } else if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionToggleInvites) {
                        if (((TL_channelAdminLogEventActionToggleInvites) tL_channelAdminLogEvent.action).new_value) {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesOn", R.string.EventLogToggledInvitesOn), "un1", fromUser);
                        } else {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesOff", R.string.EventLogToggledInvitesOff), "un1", fromUser);
                        }
                    } else if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionDeleteMessage) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogDeletedMessages", R.string.EventLogDeletedMessages), "un1", fromUser);
                    } else if (!(tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionTogglePreHistoryHidden)) {
                        if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionChangeAbout) {
                            int i5;
                            if (chat2.megagroup) {
                                r5 = "EventLogEditedGroupDescription";
                                i5 = R.string.EventLogEditedGroupDescription;
                            } else {
                                r5 = "EventLogEditedChannelDescription";
                                i5 = R.string.EventLogEditedChannelDescription;
                            }
                            r0.messageText = replaceWithLink(LocaleController.getString(r5, i5), "un1", fromUser);
                            message2 = new TL_message();
                            message2.out = false;
                            message2.unread = false;
                            message2.from_id = tL_channelAdminLogEvent.user_id;
                            message2.to_id = to_id;
                            message2.date = tL_channelAdminLogEvent.date;
                            message2.message = ((TL_channelAdminLogEventActionChangeAbout) tL_channelAdminLogEvent.action).new_value;
                            if (TextUtils.isEmpty(((TL_channelAdminLogEventActionChangeAbout) tL_channelAdminLogEvent.action).prev_value)) {
                                message2.media = new TL_messageMediaEmpty();
                            } else {
                                message2.media = new TL_messageMediaWebPage();
                                message2.media.webpage = new TL_webPage();
                                message2.media.webpage.flags = 10;
                                message2.media.webpage.display_url = TtmlNode.ANONYMOUS_REGION_ID;
                                message2.media.webpage.url = TtmlNode.ANONYMOUS_REGION_ID;
                                message2.media.webpage.site_name = LocaleController.getString("EventLogPreviousGroupDescription", R.string.EventLogPreviousGroupDescription);
                                message2.media.webpage.description = ((TL_channelAdminLogEventActionChangeAbout) tL_channelAdminLogEvent.action).prev_value;
                            }
                        } else if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionChangeUsername) {
                            r5 = ((TL_channelAdminLogEventActionChangeUsername) tL_channelAdminLogEvent.action).new_value;
                            String str3;
                            if (TextUtils.isEmpty(r5)) {
                                if (chat2.megagroup) {
                                    str3 = "EventLogRemovedGroupLink";
                                    dateDay = R.string.EventLogRemovedGroupLink;
                                } else {
                                    str3 = "EventLogRemovedChannelLink";
                                    dateDay = R.string.EventLogRemovedChannelLink;
                                }
                                r0.messageText = replaceWithLink(LocaleController.getString(str3, dateDay), "un1", fromUser);
                            } else {
                                if (chat2.megagroup) {
                                    str3 = "EventLogChangedGroupLink";
                                    dateDay = R.string.EventLogChangedGroupLink;
                                } else {
                                    str3 = "EventLogChangedChannelLink";
                                    dateDay = R.string.EventLogChangedChannelLink;
                                }
                                r0.messageText = replaceWithLink(LocaleController.getString(str3, dateDay), "un1", fromUser);
                            }
                            message2 = new TL_message();
                            message2.out = false;
                            message2.unread = false;
                            message2.from_id = tL_channelAdminLogEvent.user_id;
                            message2.to_id = to_id;
                            message2.date = tL_channelAdminLogEvent.date;
                            if (TextUtils.isEmpty(r5)) {
                                message2.message = TtmlNode.ANONYMOUS_REGION_ID;
                            } else {
                                rights = new StringBuilder();
                                rights.append("https://");
                                rights.append(MessagesController.getInstance(accountNum).linkPrefix);
                                rights.append("/");
                                rights.append(r5);
                                message2.message = rights.toString();
                            }
                            TL_messageEntityUrl url = new TL_messageEntityUrl();
                            url.offset = 0;
                            url.length = message2.message.length();
                            message2.entities.add(url);
                            if (TextUtils.isEmpty(((TL_channelAdminLogEventActionChangeUsername) tL_channelAdminLogEvent.action).prev_value)) {
                                message2.media = new TL_messageMediaEmpty();
                            } else {
                                message2.media = new TL_messageMediaWebPage();
                                message2.media.webpage = new TL_webPage();
                                message2.media.webpage.flags = 10;
                                message2.media.webpage.display_url = TtmlNode.ANONYMOUS_REGION_ID;
                                message2.media.webpage.url = TtmlNode.ANONYMOUS_REGION_ID;
                                message2.media.webpage.site_name = LocaleController.getString("EventLogPreviousLink", R.string.EventLogPreviousLink);
                                WebPage webPage = message2.media.webpage;
                                bannedDuration = new StringBuilder();
                                bannedDuration.append("https://");
                                bannedDuration.append(MessagesController.getInstance(accountNum).linkPrefix);
                                bannedDuration.append("/");
                                bannedDuration.append(((TL_channelAdminLogEventActionChangeUsername) tL_channelAdminLogEvent.action).prev_value);
                                webPage.description = bannedDuration.toString();
                            }
                        } else if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionEditMessage) {
                            message2 = new TL_message();
                            message2.out = false;
                            message2.unread = false;
                            message2.from_id = tL_channelAdminLogEvent.user_id;
                            message2.to_id = to_id;
                            message2.date = tL_channelAdminLogEvent.date;
                            newMessage = ((TL_channelAdminLogEventActionEditMessage) tL_channelAdminLogEvent.action).new_message;
                            Message oldMessage = ((TL_channelAdminLogEventActionEditMessage) tL_channelAdminLogEvent.action).prev_message;
                            if (newMessage.media == null || (newMessage.media instanceof TL_messageMediaEmpty) || (newMessage.media instanceof TL_messageMediaWebPage) || !TextUtils.isEmpty(newMessage.message)) {
                                r0.messageText = replaceWithLink(LocaleController.getString("EventLogEditedMessages", R.string.EventLogEditedMessages), "un1", fromUser);
                                message2.message = newMessage.message;
                                message2.media = new TL_messageMediaWebPage();
                                message2.media.webpage = new TL_webPage();
                                message2.media.webpage.site_name = LocaleController.getString("EventLogOriginalMessages", R.string.EventLogOriginalMessages);
                                if (TextUtils.isEmpty(oldMessage.message)) {
                                    message2.media.webpage.description = LocaleController.getString("EventLogOriginalCaptionEmpty", R.string.EventLogOriginalCaptionEmpty);
                                } else {
                                    message2.media.webpage.description = oldMessage.message;
                                }
                            } else {
                                r0.messageText = replaceWithLink(LocaleController.getString("EventLogEditedCaption", R.string.EventLogEditedCaption), "un1", fromUser);
                                message2.media = newMessage.media;
                                message2.media.webpage = new TL_webPage();
                                message2.media.webpage.site_name = LocaleController.getString("EventLogOriginalCaption", R.string.EventLogOriginalCaption);
                                if (TextUtils.isEmpty(oldMessage.message)) {
                                    message2.media.webpage.description = LocaleController.getString("EventLogOriginalCaptionEmpty", R.string.EventLogOriginalCaptionEmpty);
                                } else {
                                    message2.media.webpage.description = oldMessage.message;
                                }
                            }
                            message2.reply_markup = newMessage.reply_markup;
                            message2.media.webpage.flags = 10;
                            message2.media.webpage.display_url = TtmlNode.ANONYMOUS_REGION_ID;
                            message2.media.webpage.url = TtmlNode.ANONYMOUS_REGION_ID;
                        } else if (tL_channelAdminLogEvent.action instanceof TL_channelAdminLogEventActionChangeStickerSet) {
                            InputStickerSet newStickerset = ((TL_channelAdminLogEventActionChangeStickerSet) tL_channelAdminLogEvent.action).new_stickerset;
                            InputStickerSet oldStickerset = ((TL_channelAdminLogEventActionChangeStickerSet) tL_channelAdminLogEvent.action).new_stickerset;
                            if (newStickerset != null) {
                                if (!(newStickerset instanceof TL_inputStickerSetEmpty)) {
                                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogChangedStickersSet", R.string.EventLogChangedStickersSet), "un1", fromUser);
                                }
                            }
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogRemovedStickersSet", R.string.EventLogRemovedStickersSet), "un1", fromUser);
                        } else {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("unsupported ");
                            stringBuilder.append(tL_channelAdminLogEvent.action);
                            r0.messageText = stringBuilder.toString();
                        }
                        if (r0.messageOwner == null) {
                            r0.messageOwner = new TL_messageService();
                        }
                        r0.messageOwner.message = r0.messageText.toString();
                        r0.messageOwner.from_id = tL_channelAdminLogEvent.user_id;
                        r0.messageOwner.date = tL_channelAdminLogEvent.date;
                        newMessage = r0.messageOwner;
                        dateDay = mid[0];
                        mid[0] = dateDay + 1;
                        newMessage.id = dateDay;
                        r0.eventId = tL_channelAdminLogEvent.id;
                        r0.messageOwner.out = false;
                        r0.messageOwner.to_id = new TL_peerChannel();
                        r0.messageOwner.to_id.channel_id = chat2.id;
                        r0.messageOwner.unread = false;
                        if (chat2.megagroup) {
                            newMessage = r0.messageOwner;
                            newMessage.flags |= Integer.MIN_VALUE;
                        }
                        mediaController = MediaController.getInstance();
                        if (!(tL_channelAdminLogEvent.action.message == null || (tL_channelAdminLogEvent.action.message instanceof TL_messageEmpty))) {
                            message2 = tL_channelAdminLogEvent.action.message;
                        }
                        if (message2 != null) {
                            message2.out = false;
                            i3 = mid[0];
                            mid[0] = i3 + 1;
                            message2.id = i3;
                            message2.reply_to_msg_id = 0;
                            message2.flags &= -32769;
                            if (chat2.megagroup) {
                                message2.flags = Integer.MIN_VALUE | message2.flags;
                            }
                            messageObject = new MessageObject(accountNum, message2, null, null, true, r0.eventId);
                            if (messageObject.contentType >= 0) {
                                if (mediaController.isPlayingMessage(messageObject)) {
                                    player = mediaController.getPlayingMessageObject();
                                    messageObject.audioProgress = player.audioProgress;
                                    messageObject.audioProgressSec = player.audioProgressSec;
                                }
                                createDateArray(accountNum, event, messageObjects, messagesByDays);
                                arrayList.add(messageObjects.size() - 1, messageObject);
                            } else {
                                r0.contentType = -1;
                            }
                        }
                        if (r0.contentType < 0) {
                            createDateArray(accountNum, event, messageObjects, messagesByDays);
                            arrayList.add(messageObjects.size() - 1, r0);
                            if (r0.messageText == null) {
                                r0.messageText = TtmlNode.ANONYMOUS_REGION_ID;
                            }
                            setType();
                            measureInlineBotButtons();
                            if (r0.messageOwner.message != null && r0.messageOwner.id < 0 && r0.messageOwner.message.length() > 6 && (isVideo() || isNewGif() || isRoundVideo())) {
                                r0.videoEditedInfo = new VideoEditedInfo();
                                if (r0.videoEditedInfo.parseString(r0.messageOwner.message)) {
                                    r0.videoEditedInfo.roundVideo = isRoundVideo();
                                } else {
                                    r0.videoEditedInfo = null;
                                }
                            }
                            generateCaption();
                            if (r0.messageOwner.media instanceof TL_messageMediaGame) {
                                paint = Theme.chat_msgGameTextPaint;
                            } else {
                                paint = Theme.chat_msgTextPaint;
                            }
                            emojiOnly = SharedConfig.allowBigEmoji ? new int[1] : null;
                            r0.messageText = Emoji.replaceEmoji(r0.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly);
                            if (emojiOnly != null && emojiOnly[0] >= 1 && emojiOnly[0] <= 3) {
                                switch (emojiOnly[0]) {
                                    case 1:
                                        emojiPaint = Theme.chat_msgTextPaintOneEmoji;
                                        dateYear = AndroidUtilities.dp(32.0f);
                                        break;
                                    case 2:
                                        emojiPaint = Theme.chat_msgTextPaintTwoEmoji;
                                        dateYear = AndroidUtilities.dp(NUM);
                                        break;
                                    default:
                                        emojiPaint = Theme.chat_msgTextPaintThreeEmoji;
                                        dateYear = AndroidUtilities.dp(24.0f);
                                        break;
                                }
                                spans = (EmojiSpan[]) ((Spannable) r0.messageText).getSpans(0, r0.messageText.length(), EmojiSpan.class);
                                if (spans != null && spans.length > 0) {
                                    for (EmojiSpan replaceFontMetrics : spans) {
                                        replaceFontMetrics.replaceFontMetrics(emojiPaint.getFontMetricsInt(), dateYear);
                                    }
                                }
                            }
                            if (mediaController.isPlayingMessage(r0)) {
                                player2 = mediaController.getPlayingMessageObject();
                                r0.audioProgress = player2.audioProgress;
                                r0.audioProgressSec = player2.audioProgressSec;
                            }
                            generateLayout(fromUser);
                            r0.layoutCreated = true;
                            generateThumbs(false);
                            checkMediaExistance();
                        }
                    } else if (((TL_channelAdminLogEventActionTogglePreHistoryHidden) tL_channelAdminLogEvent.action).new_value) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesHistoryOff", R.string.EventLogToggledInvitesHistoryOff), "un1", fromUser);
                    } else {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesHistoryOn", R.string.EventLogToggledInvitesHistoryOn), "un1", fromUser);
                    }
                }
                message2 = message;
                if (r0.messageOwner == null) {
                    r0.messageOwner = new TL_messageService();
                }
                r0.messageOwner.message = r0.messageText.toString();
                r0.messageOwner.from_id = tL_channelAdminLogEvent.user_id;
                r0.messageOwner.date = tL_channelAdminLogEvent.date;
                newMessage = r0.messageOwner;
                dateDay = mid[0];
                mid[0] = dateDay + 1;
                newMessage.id = dateDay;
                r0.eventId = tL_channelAdminLogEvent.id;
                r0.messageOwner.out = false;
                r0.messageOwner.to_id = new TL_peerChannel();
                r0.messageOwner.to_id.channel_id = chat2.id;
                r0.messageOwner.unread = false;
                if (chat2.megagroup) {
                    newMessage = r0.messageOwner;
                    newMessage.flags |= Integer.MIN_VALUE;
                }
                mediaController = MediaController.getInstance();
                message2 = tL_channelAdminLogEvent.action.message;
                if (message2 != null) {
                    message2.out = false;
                    i3 = mid[0];
                    mid[0] = i3 + 1;
                    message2.id = i3;
                    message2.reply_to_msg_id = 0;
                    message2.flags &= -32769;
                    if (chat2.megagroup) {
                        message2.flags = Integer.MIN_VALUE | message2.flags;
                    }
                    messageObject = new MessageObject(accountNum, message2, null, null, true, r0.eventId);
                    if (messageObject.contentType >= 0) {
                        r0.contentType = -1;
                    } else {
                        if (mediaController.isPlayingMessage(messageObject)) {
                            player = mediaController.getPlayingMessageObject();
                            messageObject.audioProgress = player.audioProgress;
                            messageObject.audioProgressSec = player.audioProgressSec;
                        }
                        createDateArray(accountNum, event, messageObjects, messagesByDays);
                        arrayList.add(messageObjects.size() - 1, messageObject);
                    }
                }
                if (r0.contentType < 0) {
                    createDateArray(accountNum, event, messageObjects, messagesByDays);
                    arrayList.add(messageObjects.size() - 1, r0);
                    if (r0.messageText == null) {
                        r0.messageText = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    setType();
                    measureInlineBotButtons();
                    r0.videoEditedInfo = new VideoEditedInfo();
                    if (r0.videoEditedInfo.parseString(r0.messageOwner.message)) {
                        r0.videoEditedInfo.roundVideo = isRoundVideo();
                    } else {
                        r0.videoEditedInfo = null;
                    }
                    generateCaption();
                    if (r0.messageOwner.media instanceof TL_messageMediaGame) {
                        paint = Theme.chat_msgTextPaint;
                    } else {
                        paint = Theme.chat_msgGameTextPaint;
                    }
                    if (SharedConfig.allowBigEmoji) {
                    }
                    emojiOnly = SharedConfig.allowBigEmoji ? new int[1] : null;
                    r0.messageText = Emoji.replaceEmoji(r0.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly);
                    switch (emojiOnly[0]) {
                        case 1:
                            emojiPaint = Theme.chat_msgTextPaintOneEmoji;
                            dateYear = AndroidUtilities.dp(32.0f);
                            break;
                        case 2:
                            emojiPaint = Theme.chat_msgTextPaintTwoEmoji;
                            dateYear = AndroidUtilities.dp(NUM);
                            break;
                        default:
                            emojiPaint = Theme.chat_msgTextPaintThreeEmoji;
                            dateYear = AndroidUtilities.dp(24.0f);
                            break;
                    }
                    spans = (EmojiSpan[]) ((Spannable) r0.messageText).getSpans(0, r0.messageText.length(), EmojiSpan.class);
                    while (offset < spans.length) {
                        replaceFontMetrics.replaceFontMetrics(emojiPaint.getFontMetricsInt(), dateYear);
                    }
                    if (mediaController.isPlayingMessage(r0)) {
                        player2 = mediaController.getPlayingMessageObject();
                        r0.audioProgress = player2.audioProgress;
                        r0.audioProgressSec = player2.audioProgressSec;
                    }
                    generateLayout(fromUser);
                    r0.layoutCreated = true;
                    generateThumbs(false);
                    checkMediaExistance();
                }
            }
        }
        i = dateYear;
        i2 = dateMonth;
        message = null;
        message2 = message;
        if (r0.messageOwner == null) {
            r0.messageOwner = new TL_messageService();
        }
        r0.messageOwner.message = r0.messageText.toString();
        r0.messageOwner.from_id = tL_channelAdminLogEvent.user_id;
        r0.messageOwner.date = tL_channelAdminLogEvent.date;
        newMessage = r0.messageOwner;
        dateDay = mid[0];
        mid[0] = dateDay + 1;
        newMessage.id = dateDay;
        r0.eventId = tL_channelAdminLogEvent.id;
        r0.messageOwner.out = false;
        r0.messageOwner.to_id = new TL_peerChannel();
        r0.messageOwner.to_id.channel_id = chat2.id;
        r0.messageOwner.unread = false;
        if (chat2.megagroup) {
            newMessage = r0.messageOwner;
            newMessage.flags |= Integer.MIN_VALUE;
        }
        mediaController = MediaController.getInstance();
        message2 = tL_channelAdminLogEvent.action.message;
        if (message2 != null) {
            message2.out = false;
            i3 = mid[0];
            mid[0] = i3 + 1;
            message2.id = i3;
            message2.reply_to_msg_id = 0;
            message2.flags &= -32769;
            if (chat2.megagroup) {
                message2.flags = Integer.MIN_VALUE | message2.flags;
            }
            messageObject = new MessageObject(accountNum, message2, null, null, true, r0.eventId);
            if (messageObject.contentType >= 0) {
                if (mediaController.isPlayingMessage(messageObject)) {
                    player = mediaController.getPlayingMessageObject();
                    messageObject.audioProgress = player.audioProgress;
                    messageObject.audioProgressSec = player.audioProgressSec;
                }
                createDateArray(accountNum, event, messageObjects, messagesByDays);
                arrayList.add(messageObjects.size() - 1, messageObject);
            } else {
                r0.contentType = -1;
            }
        }
        if (r0.contentType < 0) {
            createDateArray(accountNum, event, messageObjects, messagesByDays);
            arrayList.add(messageObjects.size() - 1, r0);
            if (r0.messageText == null) {
                r0.messageText = TtmlNode.ANONYMOUS_REGION_ID;
            }
            setType();
            measureInlineBotButtons();
            r0.videoEditedInfo = new VideoEditedInfo();
            if (r0.videoEditedInfo.parseString(r0.messageOwner.message)) {
                r0.videoEditedInfo = null;
            } else {
                r0.videoEditedInfo.roundVideo = isRoundVideo();
            }
            generateCaption();
            if (r0.messageOwner.media instanceof TL_messageMediaGame) {
                paint = Theme.chat_msgGameTextPaint;
            } else {
                paint = Theme.chat_msgTextPaint;
            }
            if (SharedConfig.allowBigEmoji) {
            }
            emojiOnly = SharedConfig.allowBigEmoji ? new int[1] : null;
            r0.messageText = Emoji.replaceEmoji(r0.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly);
            switch (emojiOnly[0]) {
                case 1:
                    emojiPaint = Theme.chat_msgTextPaintOneEmoji;
                    dateYear = AndroidUtilities.dp(32.0f);
                    break;
                case 2:
                    emojiPaint = Theme.chat_msgTextPaintTwoEmoji;
                    dateYear = AndroidUtilities.dp(NUM);
                    break;
                default:
                    emojiPaint = Theme.chat_msgTextPaintThreeEmoji;
                    dateYear = AndroidUtilities.dp(24.0f);
                    break;
            }
            spans = (EmojiSpan[]) ((Spannable) r0.messageText).getSpans(0, r0.messageText.length(), EmojiSpan.class);
            while (offset < spans.length) {
                replaceFontMetrics.replaceFontMetrics(emojiPaint.getFontMetricsInt(), dateYear);
            }
            if (mediaController.isPlayingMessage(r0)) {
                player2 = mediaController.getPlayingMessageObject();
                r0.audioProgress = player2.audioProgress;
                r0.audioProgressSec = player2.audioProgressSec;
            }
            generateLayout(fromUser);
            r0.layoutCreated = true;
            generateThumbs(false);
            checkMediaExistance();
        }
    }

    private String getUserName(User user, ArrayList<MessageEntity> entities, int offset) {
        String name;
        if (user == null) {
            name = TtmlNode.ANONYMOUS_REGION_ID;
        } else {
            name = ContactsController.formatName(user.first_name, user.last_name);
        }
        if (offset >= 0) {
            TL_messageEntityMentionName entity = new TL_messageEntityMentionName();
            entity.user_id = user.id;
            entity.offset = offset;
            entity.length = name.length();
            entities.add(entity);
        }
        if (TextUtils.isEmpty(user.username)) {
            return name;
        }
        if (offset >= 0) {
            TL_messageEntityMentionName entity2 = new TL_messageEntityMentionName();
            entity2.user_id = user.id;
            entity2.offset = (name.length() + offset) + 2;
            entity2.length = user.username.length() + 1;
            entities.add(entity2);
        }
        return String.format("%1$s (@%2$s)", new Object[]{name, user.username});
    }

    public void applyNewText() {
        if (!TextUtils.isEmpty(this.messageOwner.message)) {
            TextPaint paint;
            User fromUser = null;
            if (isFromUser()) {
                fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            this.messageText = this.messageOwner.message;
            if (this.messageOwner.media instanceof TL_messageMediaGame) {
                paint = Theme.chat_msgGameTextPaint;
            } else {
                paint = Theme.chat_msgTextPaint;
            }
            this.messageText = Emoji.replaceEmoji(this.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            generateLayout(fromUser);
        }
    }

    public void generateGameMessageText(User fromUser) {
        if (fromUser == null && this.messageOwner.from_id > 0) {
            fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        TL_game game = null;
        if (!(this.replyMessageObject == null || this.replyMessageObject.messageOwner.media == null || this.replyMessageObject.messageOwner.media.game == null)) {
            game = this.replyMessageObject.messageOwner.media.game;
        }
        if (game != null) {
            if (fromUser == null || fromUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", R.string.ActionUserScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", fromUser);
            } else {
                this.messageText = LocaleController.formatString("ActionYouScoredInGame", R.string.ActionYouScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
            }
            this.messageText = replaceWithLink(this.messageText, "un2", game);
        } else if (fromUser == null || fromUser.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", R.string.ActionUserScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", fromUser);
        } else {
            this.messageText = LocaleController.formatString("ActionYouScored", R.string.ActionYouScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
        }
    }

    public boolean hasValidReplyMessageObject() {
        return (this.replyMessageObject == null || (this.replyMessageObject.messageOwner instanceof TL_messageEmpty) || (this.replyMessageObject.messageOwner.action instanceof TL_messageActionHistoryClear)) ? false : true;
    }

    public void generatePaymentSentMessageText(User fromUser) {
        String name;
        if (fromUser == null) {
            fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int) getDialogId()));
        }
        if (fromUser != null) {
            name = UserObject.getFirstName(fromUser);
        } else {
            name = TtmlNode.ANONYMOUS_REGION_ID;
        }
        if (this.replyMessageObject == null || !(this.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
            this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", R.string.PaymentSuccessfullyPaidNoItem, LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), name);
            return;
        }
        this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", R.string.PaymentSuccessfullyPaid, LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), name, this.replyMessageObject.messageOwner.media.title);
    }

    public void generatePinMessageText(User fromUser, Chat chat) {
        TLObject fromUser2;
        TLObject chat2;
        if (fromUser == null && chat == null) {
            if (this.messageOwner.from_id > 0) {
                fromUser2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            if (fromUser2 == null) {
                chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
            }
        }
        if (!(this.replyMessageObject == null || (this.replyMessageObject.messageOwner instanceof TL_messageEmpty))) {
            if (!(this.replyMessageObject.messageOwner.action instanceof TL_messageActionHistoryClear)) {
                if (this.replyMessageObject.isMusic()) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedMusic", R.string.ActionPinnedMusic), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.isVideo()) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedVideo", R.string.ActionPinnedVideo), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.isGif()) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedGif", R.string.ActionPinnedGif), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.isVoice()) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedVoice", R.string.ActionPinnedVoice), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.isRoundVideo()) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedRound", R.string.ActionPinnedRound), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.isSticker()) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedSticker", R.string.ActionPinnedSticker), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedFile", R.string.ActionPinnedFile), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGeo) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedGeo", R.string.ActionPinnedGeo), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedGeoLive", R.string.ActionPinnedGeoLive), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaContact) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedContact", R.string.ActionPinnedContact), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedPhoto", R.string.ActionPinnedPhoto), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
                    Object[] objArr = new Object[1];
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("\ud83c\udfae ");
                    stringBuilder.append(this.replyMessageObject.messageOwner.media.game.title);
                    objArr[0] = stringBuilder.toString();
                    this.messageText = replaceWithLink(LocaleController.formatString("ActionPinnedGame", R.string.ActionPinnedGame, objArr), "un1", fromUser2 != null ? fromUser2 : chat2);
                    this.messageText = Emoji.replaceEmoji(this.messageText, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    return;
                } else if (this.replyMessageObject.messageText == null || this.replyMessageObject.messageText.length() <= 0) {
                    this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedNoText", R.string.ActionPinnedNoText), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                } else {
                    CharSequence mess = this.replyMessageObject.messageText;
                    if (mess.length() > 20) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(mess.subSequence(0, 20));
                        stringBuilder2.append("...");
                        mess = stringBuilder2.toString();
                    }
                    mess = Emoji.replaceEmoji(mess, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    this.messageText = replaceWithLink(LocaleController.formatString("ActionPinnedText", R.string.ActionPinnedText, mess), "un1", fromUser2 != null ? fromUser2 : chat2);
                    return;
                }
            }
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionPinnedNoText", R.string.ActionPinnedNoText), "un1", fromUser2 != null ? fromUser2 : chat2);
    }

    private Photo getPhotoWithId(WebPage webPage, long id) {
        if (webPage != null) {
            if (webPage.cached_page != null) {
                if (webPage.photo != null && webPage.photo.id == id) {
                    return webPage.photo;
                }
                for (int a = 0; a < webPage.cached_page.photos.size(); a++) {
                    Photo photo = (Photo) webPage.cached_page.photos.get(a);
                    if (photo.id == id) {
                        return photo;
                    }
                }
                return null;
            }
        }
        return null;
    }

    private Document getDocumentWithId(WebPage webPage, long id) {
        if (webPage != null) {
            if (webPage.cached_page != null) {
                if (webPage.document != null && webPage.document.id == id) {
                    return webPage.document;
                }
                for (int a = 0; a < webPage.cached_page.documents.size(); a++) {
                    Document document = (Document) webPage.cached_page.documents.get(a);
                    if (document.id == id) {
                        return document;
                    }
                }
                return null;
            }
        }
        return null;
    }

    private MessageObject getMessageObjectForBlock(WebPage webPage, PageBlock pageBlock) {
        TL_message message = null;
        if (pageBlock instanceof TL_pageBlockPhoto) {
            Photo photo = getPhotoWithId(webPage, pageBlock.photo_id);
            if (photo == webPage.photo) {
                return this;
            }
            message = new TL_message();
            message.media = new TL_messageMediaPhoto();
            message.media.photo = photo;
        } else if (pageBlock instanceof TL_pageBlockVideo) {
            if (getDocumentWithId(webPage, pageBlock.video_id) == webPage.document) {
                return this;
            }
            message = new TL_message();
            message.media = new TL_messageMediaDocument();
            message.media.document = getDocumentWithId(webPage, pageBlock.video_id);
        }
        message.message = TtmlNode.ANONYMOUS_REGION_ID;
        message.id = Utilities.random.nextInt();
        message.date = this.messageOwner.date;
        message.to_id = this.messageOwner.to_id;
        message.out = this.messageOwner.out;
        message.from_id = this.messageOwner.from_id;
        return new MessageObject(this.currentAccount, message, false);
    }

    public ArrayList<MessageObject> getWebPagePhotos(ArrayList<MessageObject> array, ArrayList<PageBlock> blocksToSearch) {
        WebPage webPage = this.messageOwner.media.webpage;
        ArrayList<MessageObject> messageObjects = array == null ? new ArrayList() : array;
        if (webPage.cached_page == null) {
            return messageObjects;
        }
        ArrayList<PageBlock> blocks = blocksToSearch == null ? webPage.cached_page.blocks : blocksToSearch;
        for (int a = 0; a < blocks.size(); a++) {
            PageBlock block = (PageBlock) blocks.get(a);
            int b;
            if (block instanceof TL_pageBlockSlideshow) {
                TL_pageBlockSlideshow slideshow = (TL_pageBlockSlideshow) block;
                for (b = 0; b < slideshow.items.size(); b++) {
                    messageObjects.add(getMessageObjectForBlock(webPage, (PageBlock) slideshow.items.get(b)));
                }
            } else if (block instanceof TL_pageBlockCollage) {
                TL_pageBlockCollage slideshow2 = (TL_pageBlockCollage) block;
                for (b = 0; b < slideshow2.items.size(); b++) {
                    messageObjects.add(getMessageObjectForBlock(webPage, (PageBlock) slideshow2.items.get(b)));
                }
            }
        }
        return messageObjects;
    }

    public void measureInlineBotButtons() {
        this.wantedBotKeyboardWidth = 0;
        if (this.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
            Theme.createChatResources(null, true);
            if (r0.botButtonsLayout == null) {
                r0.botButtonsLayout = new StringBuilder();
            } else {
                r0.botButtonsLayout.setLength(0);
            }
            for (int a = 0; a < r0.messageOwner.reply_markup.rows.size(); a++) {
                TL_keyboardButtonRow row = (TL_keyboardButtonRow) r0.messageOwner.reply_markup.rows.get(a);
                int size = row.buttons.size();
                int maxButtonSize = 0;
                for (int b = 0; b < size; b++) {
                    String replaceEmoji;
                    KeyboardButton button = (KeyboardButton) row.buttons.get(b);
                    StringBuilder stringBuilder = r0.botButtonsLayout;
                    stringBuilder.append(a);
                    stringBuilder.append(b);
                    if (!(button instanceof TL_keyboardButtonBuy) || (r0.messageOwner.media.flags & 4) == 0) {
                        replaceEmoji = Emoji.replaceEmoji(button.text, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                    } else {
                        replaceEmoji = LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt);
                    }
                    StaticLayout staticLayout = new StaticLayout(replaceEmoji, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (staticLayout.getLineCount() > 0) {
                        float width = staticLayout.getLineWidth(0);
                        float left = staticLayout.getLineLeft(0);
                        if (left < width) {
                            width -= left;
                        }
                        maxButtonSize = Math.max(maxButtonSize, ((int) Math.ceil((double) width)) + AndroidUtilities.dp(4.0f));
                    }
                }
                r0.wantedBotKeyboardWidth = Math.max(r0.wantedBotKeyboardWidth, ((AndroidUtilities.dp(12.0f) + maxButtonSize) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
            }
        }
    }

    public boolean isFcmMessage() {
        return this.localType != 0;
    }

    public void setType() {
        int oldType = this.type;
        this.isRoundVideoCached = 0;
        if (!(this.messageOwner instanceof TL_message)) {
            if (!(this.messageOwner instanceof TL_messageForwarded_old2)) {
                if (this.messageOwner instanceof TL_messageService) {
                    if (this.messageOwner.action instanceof TL_messageActionLoginUnknownLocation) {
                        this.type = 0;
                    } else {
                        if (!(this.messageOwner.action instanceof TL_messageActionChatEditPhoto)) {
                            if (!(this.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto)) {
                                if (this.messageOwner.action instanceof TL_messageEncryptedAction) {
                                    if (!(this.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages)) {
                                        if (!(this.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) {
                                            this.contentType = -1;
                                            this.type = -1;
                                        }
                                    }
                                    this.contentType = 1;
                                    this.type = 10;
                                } else if (this.messageOwner.action instanceof TL_messageActionHistoryClear) {
                                    this.contentType = -1;
                                    this.type = -1;
                                } else if (this.messageOwner.action instanceof TL_messageActionPhoneCall) {
                                    this.type = 16;
                                } else {
                                    this.contentType = 1;
                                    this.type = 10;
                                }
                            }
                        }
                        this.contentType = 1;
                        this.type = 11;
                    }
                }
                if (oldType != 1000 && oldType != this.type) {
                    generateThumbs(false);
                    return;
                }
            }
        }
        if (isMediaEmpty()) {
            this.type = 0;
            if (TextUtils.isEmpty(this.messageText) && this.eventId == 0) {
                this.messageText = "Empty message";
            }
        } else if (this.messageOwner.media.ttl_seconds != 0 && ((this.messageOwner.media.photo instanceof TL_photoEmpty) || (this.messageOwner.media.document instanceof TL_documentEmpty))) {
            this.contentType = 1;
            this.type = 10;
        } else if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
            this.type = 1;
        } else {
            if (!((this.messageOwner.media instanceof TL_messageMediaGeo) || (this.messageOwner.media instanceof TL_messageMediaVenue))) {
                if (!(this.messageOwner.media instanceof TL_messageMediaGeoLive)) {
                    if (isRoundVideo()) {
                        this.type = 5;
                    } else if (isVideo()) {
                        this.type = 3;
                    } else if (isVoice()) {
                        this.type = 2;
                    } else if (isMusic()) {
                        this.type = 14;
                    } else if (this.messageOwner.media instanceof TL_messageMediaContact) {
                        this.type = 12;
                    } else if (this.messageOwner.media instanceof TL_messageMediaUnsupported) {
                        this.type = 0;
                    } else if (this.messageOwner.media instanceof TL_messageMediaDocument) {
                        if (this.messageOwner.media.document == null || this.messageOwner.media.document.mime_type == null) {
                            this.type = 9;
                        } else if (isGifDocument(this.messageOwner.media.document)) {
                            this.type = 8;
                        } else if (this.messageOwner.media.document.mime_type.equals("image/webp") && isSticker()) {
                            this.type = 13;
                        } else {
                            this.type = 9;
                        }
                    } else if (this.messageOwner.media instanceof TL_messageMediaGame) {
                        this.type = 0;
                    } else if (this.messageOwner.media instanceof TL_messageMediaInvoice) {
                        this.type = 0;
                    }
                }
            }
            this.type = 4;
        }
        if (oldType != 1000) {
        }
    }

    public boolean checkLayout() {
        if (!(this.type != 0 || this.messageOwner.to_id == null || this.messageText == null)) {
            if (this.messageText.length() != 0) {
                if (this.layoutCreated) {
                    if (Math.abs(this.generatedWithMinSize - (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) > AndroidUtilities.dp(52.0f)) {
                        this.layoutCreated = false;
                    }
                }
                if (this.layoutCreated) {
                    return false;
                }
                TextPaint paint;
                this.layoutCreated = true;
                User fromUser = null;
                if (isFromUser()) {
                    fromUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                }
                if (this.messageOwner.media instanceof TL_messageMediaGame) {
                    paint = Theme.chat_msgGameTextPaint;
                } else {
                    paint = Theme.chat_msgTextPaint;
                }
                this.messageText = Emoji.replaceEmoji(this.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                generateLayout(fromUser);
                return true;
            }
        }
        return false;
    }

    public String getMimeType() {
        if (this.messageOwner.media instanceof TL_messageMediaDocument) {
            return this.messageOwner.media.document.mime_type;
        }
        if (this.messageOwner.media instanceof TL_messageMediaInvoice) {
            WebDocument photo = ((TL_messageMediaInvoice) this.messageOwner.media).photo;
            if (photo != null) {
                return photo.mime_type;
            }
        } else if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
            return "image/jpeg";
        } else {
            if (this.messageOwner.media instanceof TL_messageMediaWebPage) {
                if (this.messageOwner.media.webpage.document != null) {
                    return this.messageOwner.media.document.mime_type;
                }
                if (this.messageOwner.media.webpage.photo != null) {
                    return "image/jpeg";
                }
            }
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static boolean isGifDocument(TL_webDocument document) {
        return document != null && (document.mime_type.equals("image/gif") || isNewGifDocument(document));
    }

    public static boolean isGifDocument(Document document) {
        return (document == null || document.thumb == null || document.mime_type == null || (!document.mime_type.equals("image/gif") && !isNewGifDocument(document))) ? false : true;
    }

    public static boolean isRoundVideoDocument(Document document) {
        if (!(document == null || document.mime_type == null || !document.mime_type.equals(MimeTypes.VIDEO_MP4))) {
            boolean round = false;
            int height = 0;
            int width = 0;
            for (int a = 0; a < document.attributes.size(); a++) {
                DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
                if (attribute instanceof TL_documentAttributeVideo) {
                    width = attribute.f36w;
                    height = attribute.f36w;
                    round = attribute.round_message;
                }
            }
            if (round && width <= 1280 && height <= 1280) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNewGifDocument(TL_webDocument document) {
        if (!(document == null || document.mime_type == null || !document.mime_type.equals(MimeTypes.VIDEO_MP4))) {
            boolean animated = false;
            int height = 0;
            int width = 0;
            for (int a = 0; a < document.attributes.size(); a++) {
                DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
                if (!(attribute instanceof TL_documentAttributeAnimated)) {
                    if (attribute instanceof TL_documentAttributeVideo) {
                        width = attribute.f36w;
                        height = attribute.f36w;
                    }
                }
            }
            if (width <= 1280 && height <= 1280) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNewGifDocument(Document document) {
        if (!(document == null || document.mime_type == null || !document.mime_type.equals(MimeTypes.VIDEO_MP4))) {
            boolean animated = false;
            int height = 0;
            int width = 0;
            for (int a = 0; a < document.attributes.size(); a++) {
                DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
                if (attribute instanceof TL_documentAttributeAnimated) {
                    animated = true;
                } else if (attribute instanceof TL_documentAttributeVideo) {
                    width = attribute.f36w;
                    height = attribute.f36w;
                }
            }
            if (animated && width <= 1280 && height <= 1280) {
                return true;
            }
        }
        return false;
    }

    public void generateThumbs(boolean update) {
        int a;
        PhotoSize photoObject;
        int b;
        PhotoSize size;
        if (this.messageOwner instanceof TL_messageService) {
            if (!(this.messageOwner.action instanceof TL_messageActionChatEditPhoto)) {
                return;
            }
            if (!update) {
                this.photoThumbs = new ArrayList(this.messageOwner.action.photo.sizes);
            } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty()) {
                for (a = 0; a < this.photoThumbs.size(); a++) {
                    photoObject = (PhotoSize) this.photoThumbs.get(a);
                    for (b = 0; b < this.messageOwner.action.photo.sizes.size(); b++) {
                        size = (PhotoSize) this.messageOwner.action.photo.sizes.get(b);
                        if (!(size instanceof TL_photoSizeEmpty)) {
                            if (size.type.equals(photoObject.type)) {
                                photoObject.location = size.location;
                                break;
                            }
                        }
                    }
                }
            }
        } else if (this.messageOwner.media != null && !(this.messageOwner.media instanceof TL_messageMediaEmpty)) {
            if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
                if (update) {
                    if (this.photoThumbs == null || this.photoThumbs.size() == this.messageOwner.media.photo.sizes.size()) {
                        if (this.photoThumbs != null && !this.photoThumbs.isEmpty()) {
                            for (a = 0; a < this.photoThumbs.size(); a++) {
                                photoObject = (PhotoSize) this.photoThumbs.get(a);
                                for (b = 0; b < this.messageOwner.media.photo.sizes.size(); b++) {
                                    size = (PhotoSize) this.messageOwner.media.photo.sizes.get(b);
                                    if (!(size instanceof TL_photoSizeEmpty)) {
                                        if (size.type.equals(photoObject.type)) {
                                            photoObject.location = size.location;
                                            break;
                                        }
                                    }
                                }
                            }
                            return;
                        }
                        return;
                    }
                }
                this.photoThumbs = new ArrayList(this.messageOwner.media.photo.sizes);
            } else if (this.messageOwner.media instanceof TL_messageMediaDocument) {
                if (!(this.messageOwner.media.document.thumb instanceof TL_photoSizeEmpty)) {
                    if (!update) {
                        this.photoThumbs = new ArrayList();
                        this.photoThumbs.add(this.messageOwner.media.document.thumb);
                    } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty() && this.messageOwner.media.document.thumb != null) {
                        PhotoSize photoObject2 = (PhotoSize) this.photoThumbs.get(0);
                        photoObject2.location = this.messageOwner.media.document.thumb.location;
                        photoObject2.f43w = this.messageOwner.media.document.thumb.f43w;
                        photoObject2.f42h = this.messageOwner.media.document.thumb.f42h;
                    }
                }
            } else if (this.messageOwner.media instanceof TL_messageMediaGame) {
                if (!(this.messageOwner.media.game.document == null || (this.messageOwner.media.game.document.thumb instanceof TL_photoSizeEmpty))) {
                    if (!update) {
                        this.photoThumbs = new ArrayList();
                        this.photoThumbs.add(this.messageOwner.media.game.document.thumb);
                    } else if (!(this.photoThumbs == null || this.photoThumbs.isEmpty() || this.messageOwner.media.game.document.thumb == null)) {
                        ((PhotoSize) this.photoThumbs.get(0)).location = this.messageOwner.media.game.document.thumb.location;
                    }
                }
                if (this.messageOwner.media.game.photo != null) {
                    if (update) {
                        if (this.photoThumbs2 != null) {
                            if (!this.photoThumbs2.isEmpty()) {
                                for (a = 0; a < this.photoThumbs2.size(); a++) {
                                    photoObject = (PhotoSize) this.photoThumbs2.get(a);
                                    for (b = 0; b < this.messageOwner.media.game.photo.sizes.size(); b++) {
                                        size = (PhotoSize) this.messageOwner.media.game.photo.sizes.get(b);
                                        if (!(size instanceof TL_photoSizeEmpty)) {
                                            if (size.type.equals(photoObject.type)) {
                                                photoObject.location = size.location;
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.photoThumbs2 = new ArrayList(this.messageOwner.media.game.photo.sizes);
                }
                if (this.photoThumbs == null && this.photoThumbs2 != null) {
                    this.photoThumbs = this.photoThumbs2;
                    this.photoThumbs2 = null;
                }
            } else if (!(this.messageOwner.media instanceof TL_messageMediaWebPage)) {
            } else {
                if (this.messageOwner.media.webpage.photo != null) {
                    if (update) {
                        if (this.photoThumbs != null) {
                            if (!this.photoThumbs.isEmpty()) {
                                for (a = 0; a < this.photoThumbs.size(); a++) {
                                    photoObject = (PhotoSize) this.photoThumbs.get(a);
                                    for (b = 0; b < this.messageOwner.media.webpage.photo.sizes.size(); b++) {
                                        size = (PhotoSize) this.messageOwner.media.webpage.photo.sizes.get(b);
                                        if (!(size instanceof TL_photoSizeEmpty)) {
                                            if (size.type.equals(photoObject.type)) {
                                                photoObject.location = size.location;
                                                break;
                                            }
                                        }
                                    }
                                }
                                return;
                            }
                            return;
                        }
                    }
                    this.photoThumbs = new ArrayList(this.messageOwner.media.webpage.photo.sizes);
                } else if (this.messageOwner.media.webpage.document != null && !(this.messageOwner.media.webpage.document.thumb instanceof TL_photoSizeEmpty)) {
                    if (!update) {
                        this.photoThumbs = new ArrayList();
                        this.photoThumbs.add(this.messageOwner.media.webpage.document.thumb);
                    } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty() && this.messageOwner.media.webpage.document.thumb != null) {
                        ((PhotoSize) this.photoThumbs.get(0)).location = this.messageOwner.media.webpage.document.thumb.location;
                    }
                }
            }
        }
    }

    public CharSequence replaceWithLink(CharSequence source, String param, ArrayList<Integer> uids, AbstractMap<Integer, User> usersDict, SparseArray<User> sUsersDict) {
        int start = TextUtils.indexOf(source, param);
        if (start < 0) {
            return source;
        }
        SpannableStringBuilder names = new SpannableStringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
        for (start = 0; start < uids.size(); start++) {
            User user = null;
            if (usersDict != null) {
                user = (User) usersDict.get(uids.get(start));
            } else if (sUsersDict != null) {
                user = (User) sUsersDict.get(((Integer) uids.get(start)).intValue());
            }
            if (user == null) {
                user = MessagesController.getInstance(this.currentAccount).getUser((Integer) uids.get(start));
            }
            if (user != null) {
                String name = UserObject.getUserName(user);
                int start2 = names.length();
                if (names.length() != 0) {
                    names.append(", ");
                }
                names.append(name);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                stringBuilder.append(user.id);
                names.setSpan(new URLSpanNoUnderlineBold(stringBuilder.toString()), start2, name.length() + start2, 33);
            }
        }
        return TextUtils.replace(source, new String[]{param}, new CharSequence[]{names});
    }

    public CharSequence replaceWithLink(CharSequence source, String param, TLObject object) {
        int start = TextUtils.indexOf(source, param);
        if (start < 0) {
            return source;
        }
        String name;
        String id;
        SpannableStringBuilder builder;
        StringBuilder stringBuilder;
        if (object instanceof User) {
            name = UserObject.getUserName((User) object);
            id = new StringBuilder();
            id.append(TtmlNode.ANONYMOUS_REGION_ID);
            id.append(((User) object).id);
            id = id.toString();
        } else if (object instanceof Chat) {
            name = ((Chat) object).title;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder2.append(-((Chat) object).id);
            id = stringBuilder2.toString();
        } else {
            if (object instanceof TL_game) {
                String str = ((TL_game) object).title;
                id = "game";
                name = str;
            } else {
                name = TtmlNode.ANONYMOUS_REGION_ID;
                id = "0";
            }
            builder = new SpannableStringBuilder(TextUtils.replace(source, new String[]{param}, new String[]{name.replace('\n', ' ')}));
            stringBuilder = new StringBuilder();
            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
            stringBuilder.append(id);
            builder.setSpan(new URLSpanNoUnderlineBold(stringBuilder.toString()), start, name.length() + start, 33);
            return builder;
        }
        builder = new SpannableStringBuilder(TextUtils.replace(source, new String[]{param}, new String[]{name.replace('\n', ' ')}));
        stringBuilder = new StringBuilder();
        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
        stringBuilder.append(id);
        builder.setSpan(new URLSpanNoUnderlineBold(stringBuilder.toString()), start, name.length() + start, 33);
        return builder;
    }

    public String getExtension() {
        String fileName = getFileName();
        int idx = fileName.lastIndexOf(46);
        String ext = null;
        if (idx != -1) {
            ext = fileName.substring(idx + 1);
        }
        if (ext == null || ext.length() == 0) {
            ext = this.messageOwner.media.document.mime_type;
        }
        if (ext == null) {
            ext = TtmlNode.ANONYMOUS_REGION_ID;
        }
        return ext.toUpperCase();
    }

    public String getFileName() {
        if (this.messageOwner.media instanceof TL_messageMediaDocument) {
            return FileLoader.getAttachFileName(this.messageOwner.media.document);
        }
        if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
            ArrayList<PhotoSize> sizes = this.messageOwner.media.photo.sizes;
            if (sizes.size() > 0) {
                PhotoSize sizeFull = FileLoader.getClosestPhotoSizeWithSize(sizes, AndroidUtilities.getPhotoSize());
                if (sizeFull != null) {
                    return FileLoader.getAttachFileName(sizeFull);
                }
            }
        } else if (this.messageOwner.media instanceof TL_messageMediaWebPage) {
            return FileLoader.getAttachFileName(this.messageOwner.media.webpage.document);
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public int getFileType() {
        if (isVideo()) {
            return 2;
        }
        if (isVoice()) {
            return 1;
        }
        if (this.messageOwner.media instanceof TL_messageMediaDocument) {
            return 3;
        }
        if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
            return 0;
        }
        return 4;
    }

    private static boolean containsUrls(CharSequence message) {
        if (message != null && message.length() >= 2) {
            if (message.length() <= CacheDataSink.DEFAULT_BUFFER_SIZE) {
                int length = message.length();
                char lastChar = '\u0000';
                int dotSequence = 0;
                int schemeSequence = 0;
                int digitsInRow = 0;
                int i = 0;
                while (i < length) {
                    char c = message.charAt(i);
                    if (c >= '0' && c <= '9') {
                        digitsInRow++;
                        if (digitsInRow >= 6) {
                            return true;
                        }
                        schemeSequence = 0;
                        dotSequence = 0;
                    } else if (c == ' ' || digitsInRow <= 0) {
                        digitsInRow = 0;
                    }
                    if ((c != '@' && c != '#' && c != '/' && c != '$') || i != 0) {
                        if (i != 0) {
                            if (message.charAt(i - 1) != ' ') {
                                if (message.charAt(i - 1) == '\n') {
                                }
                            }
                        }
                        if (c == ':') {
                            if (schemeSequence == 0) {
                                schemeSequence = 1;
                            } else {
                                schemeSequence = 0;
                            }
                        } else if (c == '/') {
                            if (schemeSequence == 2) {
                                return true;
                            }
                            if (schemeSequence == 1) {
                                schemeSequence++;
                            } else {
                                schemeSequence = 0;
                            }
                        } else if (c == '.') {
                            if (dotSequence != 0 || lastChar == ' ') {
                                dotSequence = 0;
                            } else {
                                dotSequence++;
                            }
                        } else if (c != ' ' && lastChar == '.' && dotSequence == 1) {
                            return true;
                        } else {
                            dotSequence = 0;
                        }
                        lastChar = c;
                        i++;
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public void generateLinkDescription() {
        if (this.linkDescription == null) {
            if ((this.messageOwner.media instanceof TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TL_webPage) && this.messageOwner.media.webpage.description != null) {
                this.linkDescription = Factory.getInstance().newSpannable(this.messageOwner.media.webpage.description);
            } else if ((this.messageOwner.media instanceof TL_messageMediaGame) && this.messageOwner.media.game.description != null) {
                this.linkDescription = Factory.getInstance().newSpannable(this.messageOwner.media.game.description);
            } else if ((this.messageOwner.media instanceof TL_messageMediaInvoice) && this.messageOwner.media.description != null) {
                this.linkDescription = Factory.getInstance().newSpannable(this.messageOwner.media.description);
            }
            if (this.linkDescription != null) {
                if (containsUrls(this.linkDescription)) {
                    try {
                        Linkify.addLinks((Spannable) this.linkDescription, 1);
                    } catch (Throwable e) {
                        FileLog.m3e(e);
                    }
                }
                this.linkDescription = Emoji.replaceEmoji(this.linkDescription, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
        }
    }

    public void generateCaption() {
        if (this.caption == null) {
            if (!isRoundVideo()) {
                if (!(isMediaEmpty() || (this.messageOwner.media instanceof TL_messageMediaGame) || TextUtils.isEmpty(this.messageOwner.message))) {
                    boolean hasEntities;
                    boolean z = false;
                    this.caption = Emoji.replaceEmoji(this.messageOwner.message, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    if (this.messageOwner.send_state != 0) {
                        hasEntities = false;
                        for (int a = 0; a < this.messageOwner.entities.size(); a++) {
                            if (!(this.messageOwner.entities.get(a) instanceof TL_inputMessageEntityMentionName)) {
                                hasEntities = true;
                                break;
                            }
                        }
                    } else {
                        hasEntities = this.messageOwner.entities.isEmpty() ^ true;
                    }
                    if (!hasEntities && (this.eventId != 0 || (this.messageOwner.media instanceof TL_messageMediaPhoto_old) || (this.messageOwner.media instanceof TL_messageMediaPhoto_layer68) || (this.messageOwner.media instanceof TL_messageMediaPhoto_layer74) || (this.messageOwner.media instanceof TL_messageMediaDocument_old) || (this.messageOwner.media instanceof TL_messageMediaDocument_layer68) || (this.messageOwner.media instanceof TL_messageMediaDocument_layer74) || ((isOut() && this.messageOwner.send_state != 0) || this.messageOwner.id < 0))) {
                        z = true;
                    }
                    boolean useManualParse = z;
                    if (useManualParse) {
                        if (containsUrls(this.caption)) {
                            try {
                                Linkify.addLinks((Spannable) this.caption, 5);
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                        addUsernamesAndHashtags(isOutOwner(), this.caption, true);
                    } else {
                        try {
                            Linkify.addLinks((Spannable) this.caption, 4);
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                    addEntitiesToText(this.caption, useManualParse);
                }
            }
        }
    }

    private static void addUsernamesAndHashtags(boolean isOut, CharSequence charSequence, boolean botCommands) {
        try {
            if (urlPattern == null) {
                urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s)@[a-zA-Z\\d_]{1,32}|(^|\\s)#[\\w\\.]+|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)");
            }
            Matcher matcher = urlPattern.matcher(charSequence);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                char ch = charSequence.charAt(start);
                if (!(ch == '@' || ch == '#' || ch == '/' || ch == '$')) {
                    start++;
                }
                URLSpanNoUnderline url = null;
                if (charSequence.charAt(start) != '/') {
                    url = new URLSpanNoUnderline(charSequence.subSequence(start, end).toString());
                } else if (botCommands) {
                    url = new URLSpanBotCommand(charSequence.subSequence(start, end).toString(), isOut);
                }
                if (url != null) {
                    ((Spannable) charSequence).setSpan(url, start, end, 0);
                }
            }
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }

    public static int[] getWebDocumentWidthAndHeight(WebDocument document) {
        if (document == null) {
            return null;
        }
        int a = 0;
        int size = document.attributes.size();
        while (a < size) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeImageSize) {
                return new int[]{attribute.f36w, attribute.f35h};
            } else if (attribute instanceof TL_documentAttributeVideo) {
                return new int[]{attribute.f36w, attribute.f35h};
            } else {
                a++;
            }
        }
        return null;
    }

    public static int getWebDocumentDuration(WebDocument document) {
        if (document == null) {
            return 0;
        }
        int size = document.attributes.size();
        for (int a = 0; a < size; a++) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeVideo) {
                return attribute.duration;
            }
            if (attribute instanceof TL_documentAttributeAudio) {
                return attribute.duration;
            }
        }
        return 0;
    }

    public static int[] getInlineResultWidthAndHeight(BotInlineResult inlineResult) {
        int[] result = getWebDocumentWidthAndHeight(inlineResult.content);
        if (result != null) {
            return result;
        }
        result = getWebDocumentWidthAndHeight(inlineResult.thumb);
        if (result == null) {
            return new int[]{0, 0};
        }
        return result;
    }

    public static int getInlineResultDuration(BotInlineResult inlineResult) {
        int result = getWebDocumentDuration(inlineResult.content);
        if (result == 0) {
            return getWebDocumentDuration(inlineResult.thumb);
        }
        return result;
    }

    public boolean hasValidGroupId() {
        return (getGroupId() == 0 || this.photoThumbs == null || this.photoThumbs.isEmpty()) ? false : true;
    }

    public long getGroupId() {
        return this.localGroupId != 0 ? this.localGroupId : this.messageOwner.grouped_id;
    }

    public static void addLinks(boolean isOut, CharSequence messageText) {
        addLinks(isOut, messageText, true);
    }

    public static void addLinks(boolean isOut, CharSequence messageText, boolean botCommands) {
        if ((messageText instanceof Spannable) && containsUrls(messageText)) {
            if (messageText.length() < 1000) {
                try {
                    Linkify.addLinks((Spannable) messageText, 5);
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            } else {
                try {
                    Linkify.addLinks((Spannable) messageText, 1);
                } catch (Throwable e2) {
                    FileLog.m3e(e2);
                }
            }
            addUsernamesAndHashtags(isOut, messageText, botCommands);
        }
    }

    public void resetPlayingProgress() {
        this.audioProgress = 0.0f;
        this.audioProgressSec = 0;
        this.bufferedProgress = 0.0f;
    }

    private boolean addEntitiesToText(CharSequence text, boolean useManualParse) {
        return addEntitiesToText(text, false, useManualParse);
    }

    public boolean addEntitiesToText(CharSequence text, boolean photoViewer, boolean useManualParse) {
        MessageObject messageObject = this;
        CharSequence charSequence = text;
        boolean hasUrls = false;
        boolean z = false;
        if (!(charSequence instanceof Spannable)) {
            return false;
        }
        Spannable spannable = (Spannable) charSequence;
        int count = messageObject.messageOwner.entities.size();
        URLSpan[] spans = (URLSpan[]) spannable.getSpans(0, text.length(), URLSpan.class);
        if (spans != null && spans.length > 0) {
            hasUrls = true;
        }
        boolean hasUrls2 = hasUrls;
        int a = 0;
        while (a < count) {
            MessageEntity entity = (MessageEntity) messageObject.messageOwner.entities.get(a);
            if (entity.length > 0 && entity.offset >= 0) {
                if (entity.offset < text.length()) {
                    if (entity.offset + entity.length > text.length()) {
                        entity.length = text.length() - entity.offset;
                    }
                    if ((!useManualParse || (entity instanceof TL_messageEntityBold) || (entity instanceof TL_messageEntityItalic) || (entity instanceof TL_messageEntityCode) || (entity instanceof TL_messageEntityPre) || (entity instanceof TL_messageEntityMentionName) || (entity instanceof TL_inputMessageEntityMentionName)) && spans != null && spans.length > 0) {
                        for (int b = z; b < spans.length; b++) {
                            if (spans[b] != null) {
                                int start = spannable.getSpanStart(spans[b]);
                                int end = spannable.getSpanEnd(spans[b]);
                                if ((entity.offset <= start && entity.offset + entity.length >= start) || (entity.offset <= end && entity.offset + entity.length >= end)) {
                                    spannable.removeSpan(spans[b]);
                                    spans[b] = null;
                                }
                            }
                        }
                    }
                    if (entity instanceof TL_messageEntityBold) {
                        spannable.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), entity.offset, entity.offset + entity.length, 33);
                    } else if (entity instanceof TL_messageEntityItalic) {
                        spannable.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), entity.offset, entity.offset + entity.length, 33);
                    } else {
                        byte type;
                        if (!(entity instanceof TL_messageEntityCode)) {
                            if (!(entity instanceof TL_messageEntityPre)) {
                                StringBuilder stringBuilder;
                                if (entity instanceof TL_messageEntityMentionName) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(((TL_messageEntityMentionName) entity).user_id);
                                    spannable.setSpan(new URLSpanUserMention(stringBuilder.toString(), messageObject.type), entity.offset, entity.offset + entity.length, 33);
                                } else if (entity instanceof TL_inputMessageEntityMentionName) {
                                    stringBuilder = new StringBuilder();
                                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                    stringBuilder.append(((TL_inputMessageEntityMentionName) entity).user_id.user_id);
                                    spannable.setSpan(new URLSpanUserMention(stringBuilder.toString(), messageObject.type), entity.offset, entity.offset + entity.length, 33);
                                } else if (!useManualParse) {
                                    String url = TextUtils.substring(charSequence, entity.offset, entity.offset + entity.length);
                                    if (entity instanceof TL_messageEntityBotCommand) {
                                        spannable.setSpan(new URLSpanBotCommand(url, messageObject.type), entity.offset, entity.offset + entity.length, 33);
                                    } else {
                                        if (!((entity instanceof TL_messageEntityHashtag) || (entity instanceof TL_messageEntityMention))) {
                                            if (!(entity instanceof TL_messageEntityCashtag)) {
                                                StringBuilder stringBuilder2;
                                                if (entity instanceof TL_messageEntityEmail) {
                                                    stringBuilder2 = new StringBuilder();
                                                    stringBuilder2.append("mailto:");
                                                    stringBuilder2.append(url);
                                                    spannable.setSpan(new URLSpanReplacement(stringBuilder2.toString()), entity.offset, entity.offset + entity.length, 33);
                                                } else if (entity instanceof TL_messageEntityUrl) {
                                                    hasUrls2 = true;
                                                    if (url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("tg://")) {
                                                        spannable.setSpan(new URLSpanBrowser(url), entity.offset, entity.offset + entity.length, 33);
                                                    } else {
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append("http://");
                                                        stringBuilder2.append(url);
                                                        spannable.setSpan(new URLSpanBrowser(stringBuilder2.toString()), entity.offset, entity.offset + entity.length, 33);
                                                    }
                                                } else if (entity instanceof TL_messageEntityPhone) {
                                                    hasUrls2 = true;
                                                    String tel = PhoneFormat.stripExceptNumbers(url);
                                                    if (url.startsWith("+")) {
                                                        stringBuilder2 = new StringBuilder();
                                                        stringBuilder2.append("+");
                                                        stringBuilder2.append(tel);
                                                        tel = stringBuilder2.toString();
                                                    }
                                                    StringBuilder stringBuilder3 = new StringBuilder();
                                                    stringBuilder3.append("tel://");
                                                    stringBuilder3.append(tel);
                                                    spannable.setSpan(new URLSpanBrowser(stringBuilder3.toString()), entity.offset, entity.offset + entity.length, 33);
                                                } else if (entity instanceof TL_messageEntityTextUrl) {
                                                    spannable.setSpan(new URLSpanReplacement(entity.url), entity.offset, entity.offset + entity.length, 33);
                                                }
                                            }
                                        }
                                        spannable.setSpan(new URLSpanNoUnderline(url), entity.offset, entity.offset + entity.length, 33);
                                    }
                                }
                            }
                        }
                        if (photoViewer) {
                            type = (byte) 2;
                        } else if (isOutOwner()) {
                            type = (byte) 1;
                        } else {
                            type = (byte) 0;
                            spannable.setSpan(new URLSpanMono(spannable, entity.offset, entity.offset + entity.length, type), entity.offset, entity.offset + entity.length, 33);
                        }
                        spannable.setSpan(new URLSpanMono(spannable, entity.offset, entity.offset + entity.length, type), entity.offset, entity.offset + entity.length, 33);
                    }
                }
            }
            a++;
            z = false;
        }
        return hasUrls2;
    }

    public void generateLayout(User fromUser) {
        Throwable th;
        Throwable e;
        boolean z;
        TextPaint textPaint;
        boolean z2;
        boolean z3;
        boolean z4;
        int linesOffset;
        StaticLayout staticLayout;
        User user = fromUser;
        if (this.type == 0 && r1.messageOwner.to_id != null) {
            MessageObject messageObject;
            if (!TextUtils.isEmpty(messageObject.messageText)) {
                boolean hasEntities;
                float f;
                TextPaint paint;
                StaticLayout textLayout;
                int i;
                StaticLayout textLayout2;
                boolean blocksCount;
                int linesOffset2;
                float prevOffset;
                boolean a;
                boolean a2;
                int currentBlockLinesCount;
                TextLayoutBlock block;
                int i2;
                int linesOffset3;
                StaticLayout textLayout3;
                boolean startCharacter;
                boolean z5;
                boolean z6;
                float lastLine;
                int linesMaxWidth;
                int a3;
                int currentBlockLinesCount2;
                float lineLeft;
                int maxWidth;
                boolean lastLeft;
                float textRealMaxWidth;
                float f2;
                generateLinkDescription();
                messageObject.textLayoutBlocks = new ArrayList();
                messageObject.textWidth = 0;
                boolean z7 = true;
                if (messageObject.messageOwner.send_state != 0) {
                    hasEntities = false;
                    for (int a4 = 0; a4 < messageObject.messageOwner.entities.size(); a4++) {
                        if (!(messageObject.messageOwner.entities.get(a4) instanceof TL_inputMessageEntityMentionName)) {
                            hasEntities = true;
                            break;
                        }
                    }
                } else {
                    hasEntities = messageObject.messageOwner.entities.isEmpty() ^ true;
                }
                boolean useManualParse = !hasEntities && (messageObject.eventId != 0 || (messageObject.messageOwner instanceof TL_message_old) || (messageObject.messageOwner instanceof TL_message_old2) || (messageObject.messageOwner instanceof TL_message_old3) || (messageObject.messageOwner instanceof TL_message_old4) || (messageObject.messageOwner instanceof TL_messageForwarded_old) || (messageObject.messageOwner instanceof TL_messageForwarded_old2) || (messageObject.messageOwner instanceof TL_message_secret) || (messageObject.messageOwner.media instanceof TL_messageMediaInvoice) || ((isOut() && messageObject.messageOwner.send_state != 0) || messageObject.messageOwner.id < 0 || (messageObject.messageOwner.media instanceof TL_messageMediaUnsupported)));
                if (useManualParse) {
                    addLinks(isOutOwner(), messageObject.messageText);
                } else if ((messageObject.messageText instanceof Spannable) && messageObject.messageText.length() < 1000) {
                    try {
                        Linkify.addLinks((Spannable) messageObject.messageText, 4);
                    } catch (Throwable th2) {
                        FileLog.m3e(th2);
                    }
                }
                boolean hasUrls = addEntitiesToText(messageObject.messageText, useManualParse);
                boolean needShare = messageObject.eventId == 0 && !isOutOwner() && (!(messageObject.messageOwner.fwd_from == null || (messageObject.messageOwner.fwd_from.saved_from_peer == null && messageObject.messageOwner.fwd_from.from_id == 0 && messageObject.messageOwner.fwd_from.channel_id == 0)) || (messageObject.messageOwner.from_id > 0 && (messageObject.messageOwner.to_id.channel_id != 0 || messageObject.messageOwner.to_id.chat_id != 0 || (messageObject.messageOwner.media instanceof TL_messageMediaGame) || (messageObject.messageOwner.media instanceof TL_messageMediaInvoice))));
                messageObject.generatedWithMinSize = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x;
                int maxWidth2 = messageObject.generatedWithMinSize;
                if (!needShare) {
                    if (messageObject.eventId == 0) {
                        f = 80.0f;
                        maxWidth2 -= AndroidUtilities.dp(f);
                        if ((user != null && user.bot) || ((isMegagroup() || !(messageObject.messageOwner.fwd_from == null || messageObject.messageOwner.fwd_from.channel_id == 0)) && !isOut())) {
                            maxWidth2 -= AndroidUtilities.dp(20.0f);
                        }
                        if (messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                            maxWidth2 -= AndroidUtilities.dp(10.0f);
                        }
                        if (messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                            paint = Theme.chat_msgTextPaint;
                        } else {
                            paint = Theme.chat_msgGameTextPaint;
                        }
                        if (VERSION.SDK_INT < 24) {
                            try {
                                textLayout = Builder.obtain(messageObject.messageText, 0, messageObject.messageText.length(), paint, maxWidth2).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Alignment.ALIGN_NORMAL).build();
                                i = 24;
                            } catch (Throwable th22) {
                                e = th22;
                                z = hasEntities;
                                textPaint = paint;
                                z2 = useManualParse;
                                z3 = hasUrls;
                                z4 = needShare;
                                FileLog.m3e(e);
                            }
                        }
                        i = 24;
                        textLayout = new StaticLayout(messageObject.messageText, paint, maxWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                        textLayout2 = textLayout;
                        messageObject.textHeight = textLayout2.getHeight();
                        messageObject.linesCount = textLayout2.getLineCount();
                        if (VERSION.SDK_INT < i) {
                            blocksCount = true;
                        } else {
                            blocksCount = (int) Math.ceil((double) (((float) messageObject.linesCount) / 10.0f));
                        }
                        linesOffset2 = 0;
                        prevOffset = 0.0f;
                        a = false;
                        while (true) {
                            a2 = a;
                            if (a2 >= blocksCount) {
                                if (VERSION.SDK_INT < i) {
                                    currentBlockLinesCount = messageObject.linesCount;
                                } else {
                                    currentBlockLinesCount = Math.min(10, messageObject.linesCount - linesOffset2);
                                }
                                i = currentBlockLinesCount;
                                block = new TextLayoutBlock();
                                if (blocksCount != z7) {
                                    block.textLayout = textLayout2;
                                    block.textYOffset = 0.0f;
                                    block.charactersOffset = 0;
                                    block.height = messageObject.textHeight;
                                    z = hasEntities;
                                    z2 = useManualParse;
                                    z3 = hasUrls;
                                    z4 = needShare;
                                    hasUrls = block;
                                    i2 = a2;
                                    linesOffset3 = linesOffset2;
                                    textLayout3 = textLayout2;
                                } else {
                                    startCharacter = textLayout2.getLineStart(linesOffset2);
                                    z = hasEntities;
                                    hasEntities = textLayout2.getLineEnd((linesOffset2 + i) - 1);
                                    if (hasEntities >= startCharacter) {
                                        textPaint = paint;
                                        z2 = useManualParse;
                                        z3 = hasUrls;
                                        z4 = needShare;
                                        i2 = a2;
                                        linesOffset = linesOffset2;
                                        staticLayout = textLayout2;
                                        useManualParse = z7;
                                    } else {
                                        block.charactersOffset = startCharacter;
                                        block.charactersEnd = hasEntities;
                                        if (hasUrls) {
                                            z2 = useManualParse;
                                        } else {
                                            try {
                                                z2 = useManualParse;
                                                if (VERSION.SDK_INT >= true) {
                                                    try {
                                                        useManualParse = true;
                                                        try {
                                                            block.textLayout = Builder.obtain(messageObject.messageText, startCharacter, hasEntities, paint, AndroidUtilities.dp(2.0f) + maxWidth2).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Alignment.ALIGN_NORMAL).build();
                                                            z5 = startCharacter;
                                                            z3 = hasUrls;
                                                            z4 = needShare;
                                                            hasUrls = block;
                                                            i2 = a2;
                                                            linesOffset3 = linesOffset2;
                                                            textLayout3 = textLayout2;
                                                            hasUrls.textYOffset = (float) textLayout3.getLineTop(linesOffset3);
                                                            if (i2 == true) {
                                                                try {
                                                                    hasUrls.height = (int) (hasUrls.textYOffset - prevOffset);
                                                                } catch (Exception e2) {
                                                                    th22 = e2;
                                                                    staticLayout = textLayout3;
                                                                    textPaint = paint;
                                                                    linesOffset = linesOffset3;
                                                                    useManualParse = true;
                                                                    e = th22;
                                                                    FileLog.m3e(e);
                                                                    linesOffset2 = linesOffset;
                                                                    a = i2 + 1;
                                                                    z7 = useManualParse;
                                                                    hasEntities = z;
                                                                    useManualParse = z2;
                                                                    hasUrls = z3;
                                                                    needShare = z4;
                                                                    textLayout2 = staticLayout;
                                                                    paint = textPaint;
                                                                    user = fromUser;
                                                                    i = 24;
                                                                }
                                                            }
                                                            hasUrls.height = Math.max(hasUrls.height, hasUrls.textLayout.getLineBottom(hasUrls.textLayout.getLineCount() - 1));
                                                            prevOffset = hasUrls.textYOffset;
                                                            if (i2 == blocksCount - 1) {
                                                                i = Math.max(i, hasUrls.textLayout.getLineCount());
                                                                try {
                                                                    messageObject.textHeight = Math.max(messageObject.textHeight, (int) (hasUrls.textYOffset + ((float) hasUrls.textLayout.getHeight())));
                                                                } catch (Throwable th222) {
                                                                    FileLog.m3e(th222);
                                                                }
                                                            }
                                                        } catch (Exception e3) {
                                                            th222 = e3;
                                                            z5 = startCharacter;
                                                            textPaint = paint;
                                                            z3 = hasUrls;
                                                            z4 = needShare;
                                                            hasUrls = block;
                                                            i2 = a2;
                                                            linesOffset = linesOffset2;
                                                            staticLayout = textLayout2;
                                                            e = th222;
                                                            FileLog.m3e(e);
                                                            linesOffset2 = linesOffset;
                                                            a = i2 + 1;
                                                            z7 = useManualParse;
                                                            hasEntities = z;
                                                            useManualParse = z2;
                                                            hasUrls = z3;
                                                            needShare = z4;
                                                            textLayout2 = staticLayout;
                                                            paint = textPaint;
                                                            user = fromUser;
                                                            i = 24;
                                                        }
                                                    } catch (Exception e4) {
                                                        th222 = e4;
                                                        textPaint = paint;
                                                        z3 = hasUrls;
                                                        z4 = needShare;
                                                        hasUrls = block;
                                                        i2 = a2;
                                                        linesOffset = linesOffset2;
                                                        staticLayout = textLayout2;
                                                        useManualParse = true;
                                                        e = th222;
                                                        FileLog.m3e(e);
                                                        linesOffset2 = linesOffset;
                                                        a = i2 + 1;
                                                        z7 = useManualParse;
                                                        hasEntities = z;
                                                        useManualParse = z2;
                                                        hasUrls = z3;
                                                        needShare = z4;
                                                        textLayout2 = staticLayout;
                                                        paint = textPaint;
                                                        user = fromUser;
                                                        i = 24;
                                                    }
                                                }
                                            } catch (Throwable th2222) {
                                                z2 = useManualParse;
                                                z5 = startCharacter;
                                                textPaint = paint;
                                                z3 = hasUrls;
                                                z4 = needShare;
                                                hasUrls = block;
                                                i2 = a2;
                                                linesOffset = linesOffset2;
                                                staticLayout = textLayout2;
                                                useManualParse = true;
                                                e = th2222;
                                                FileLog.m3e(e);
                                                linesOffset2 = linesOffset;
                                                a = i2 + 1;
                                                z7 = useManualParse;
                                                hasEntities = z;
                                                useManualParse = z2;
                                                hasUrls = z3;
                                                needShare = z4;
                                                textLayout2 = staticLayout;
                                                paint = textPaint;
                                                user = fromUser;
                                                i = 24;
                                            }
                                        }
                                        try {
                                            z3 = hasUrls;
                                            hasUrls = block;
                                            textLayout = textLayout;
                                            z4 = needShare;
                                            i2 = a2;
                                            linesOffset3 = linesOffset2;
                                            z6 = startCharacter;
                                            z5 = startCharacter;
                                            textLayout3 = textLayout2;
                                        } catch (Throwable th22222) {
                                            z5 = startCharacter;
                                            textPaint = paint;
                                            z3 = hasUrls;
                                            z4 = needShare;
                                            hasUrls = block;
                                            i2 = a2;
                                            linesOffset = linesOffset2;
                                            staticLayout = textLayout2;
                                            useManualParse = true;
                                            e = th22222;
                                            FileLog.m3e(e);
                                            linesOffset2 = linesOffset;
                                            a = i2 + 1;
                                            z7 = useManualParse;
                                            hasEntities = z;
                                            useManualParse = z2;
                                            hasUrls = z3;
                                            needShare = z4;
                                            textLayout2 = staticLayout;
                                            paint = textPaint;
                                            user = fromUser;
                                            i = 24;
                                        }
                                        try {
                                            hasUrls.textLayout = new StaticLayout(messageObject.messageText, z6, hasEntities, paint, maxWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                            hasUrls.textYOffset = (float) textLayout3.getLineTop(linesOffset3);
                                            if (i2 == true) {
                                                hasUrls.height = (int) (hasUrls.textYOffset - prevOffset);
                                            }
                                            hasUrls.height = Math.max(hasUrls.height, hasUrls.textLayout.getLineBottom(hasUrls.textLayout.getLineCount() - 1));
                                            prevOffset = hasUrls.textYOffset;
                                            if (i2 == blocksCount - 1) {
                                                i = Math.max(i, hasUrls.textLayout.getLineCount());
                                                messageObject.textHeight = Math.max(messageObject.textHeight, (int) (hasUrls.textYOffset + ((float) hasUrls.textLayout.getHeight())));
                                            }
                                        } catch (Throwable th222222) {
                                            staticLayout = textLayout3;
                                            textPaint = paint;
                                            linesOffset = linesOffset3;
                                            useManualParse = true;
                                            e = th222222;
                                            FileLog.m3e(e);
                                            linesOffset2 = linesOffset;
                                            a = i2 + 1;
                                            z7 = useManualParse;
                                            hasEntities = z;
                                            useManualParse = z2;
                                            hasUrls = z3;
                                            needShare = z4;
                                            textLayout2 = staticLayout;
                                            paint = textPaint;
                                            user = fromUser;
                                            i = 24;
                                        }
                                    }
                                    linesOffset2 = linesOffset;
                                    a = i2 + 1;
                                    z7 = useManualParse;
                                    hasEntities = z;
                                    useManualParse = z2;
                                    hasUrls = z3;
                                    needShare = z4;
                                    textLayout2 = staticLayout;
                                    paint = textPaint;
                                    user = fromUser;
                                    i = 24;
                                }
                                messageObject.textLayoutBlocks.add(hasUrls);
                                try {
                                    hasEntities = hasUrls.textLayout.getLineLeft(i - 1);
                                    if (i2 == 0 && hasEntities < false) {
                                        messageObject.textXOffset = hasEntities;
                                    }
                                } catch (Throwable th2222222) {
                                    Throwable hasEntities2 = th2222222;
                                    if (i2 == 0) {
                                        messageObject.textXOffset = 0.0f;
                                    }
                                    FileLog.m3e(hasEntities2);
                                    hasEntities = false;
                                }
                                try {
                                    lastLine = hasUrls.textLayout.getLineWidth(i - 1);
                                } catch (Throwable th22222222) {
                                    lastLine = 0.0f;
                                    FileLog.m3e(th22222222);
                                }
                                linesMaxWidth = (int) Math.ceil((double) lastLine);
                                if (i2 == blocksCount - 1) {
                                    messageObject.lastLineWidth = linesMaxWidth;
                                }
                                a3 = (int) Math.ceil((double) (lastLine + hasEntities));
                                linesOffset2 = a3;
                                if (i <= 1) {
                                    textLayout2 = null;
                                    staticLayout = textLayout3;
                                    textPaint = paint;
                                    lastLine = a3;
                                    textLayout3 = 0.0f;
                                    a3 = 0;
                                    paint = linesMaxWidth;
                                    linesMaxWidth = 0;
                                    while (linesMaxWidth < i) {
                                        currentBlockLinesCount2 = i;
                                        try {
                                            i = hasUrls.textLayout.getLineWidth(linesMaxWidth);
                                        } catch (Throwable th222222222) {
                                            FileLog.m3e(th222222222);
                                            i = 0;
                                        }
                                        linesOffset = linesOffset3;
                                        if (i > ((float) (maxWidth2 + 20))) {
                                            i = (float) maxWidth2;
                                        }
                                        try {
                                            lineLeft = hasUrls.textLayout.getLineLeft(linesMaxWidth);
                                        } catch (Throwable thNUM) {
                                            FileLog.m3e(thNUM);
                                            lineLeft = 0.0f;
                                        }
                                        if (lineLeft <= 0.0f) {
                                            maxWidth = maxWidth2;
                                            messageObject.textXOffset = Math.min(messageObject.textXOffset, lineLeft);
                                            lastLeft = hasEntities;
                                            hasUrls.directionFlags = (byte) (hasUrls.directionFlags | 1);
                                            messageObject.hasRtl = true;
                                        } else {
                                            lastLeft = hasEntities;
                                            maxWidth = maxWidth2;
                                            hasUrls.directionFlags = (byte) (hasUrls.directionFlags | 2);
                                        }
                                        if (textLayout2 == null && !lineLeft != false) {
                                            try {
                                                if (hasUrls.textLayout.getParagraphDirection(linesMaxWidth)) {
                                                    textLayout2 = true;
                                                }
                                            } catch (Exception e5) {
                                                hasEntities = e5;
                                                textLayout2 = true;
                                            }
                                        }
                                        textLayout3 = Math.max(textLayout3, i);
                                        a3 = Math.max(a3, i + lineLeft);
                                        textRealMaxWidth = textLayout3;
                                        paint = Math.max(paint, (int) Math.ceil((double) i));
                                        lastLine = Math.max(lastLine, (int) Math.ceil((double) (i + lineLeft)));
                                        linesMaxWidth++;
                                        i = currentBlockLinesCount2;
                                        linesOffset3 = linesOffset;
                                        maxWidth2 = maxWidth;
                                        hasEntities = lastLeft;
                                        textLayout3 = textRealMaxWidth;
                                        messageObject = this;
                                    }
                                    currentBlockLinesCount2 = i;
                                    lastLeft = hasEntities;
                                    linesOffset = linesOffset3;
                                    maxWidth = maxWidth2;
                                    if (textLayout2 == null) {
                                        textLayout3 = a3;
                                        if (i2 != blocksCount - 1) {
                                            this.lastLineWidth = linesOffset2;
                                        } else {
                                            messageObject = this;
                                        }
                                    } else {
                                        messageObject = this;
                                        if (i2 == blocksCount - 1) {
                                            messageObject.lastLineWidth = paint;
                                        }
                                    }
                                    messageObject.textWidth = Math.max(messageObject.textWidth, (int) Math.ceil((double) textLayout3));
                                    maxWidth2 = maxWidth;
                                    hasEntities = lastLeft;
                                    useManualParse = true;
                                } else {
                                    staticLayout = textLayout3;
                                    currentBlockLinesCount2 = i;
                                    lastLeft = hasEntities;
                                    textPaint = paint;
                                    linesOffset = linesOffset3;
                                    maxWidth = maxWidth2;
                                    f2 = lastLine;
                                    if (lastLeft <= 0.0f) {
                                        hasEntities = lastLeft;
                                        messageObject.textXOffset = Math.min(messageObject.textXOffset, hasEntities);
                                        if (messageObject.textXOffset == 0.0f) {
                                            linesMaxWidth = (int) (((float) linesMaxWidth) + hasEntities);
                                        }
                                        useManualParse = true;
                                        messageObject.hasRtl = blocksCount;
                                        hasUrls.directionFlags = (byte) (hasUrls.directionFlags | 1);
                                    } else {
                                        hasEntities = lastLeft;
                                        useManualParse = true;
                                        hasUrls.directionFlags = (byte) (hasUrls.directionFlags | 2);
                                    }
                                    maxWidth2 = maxWidth;
                                    messageObject.textWidth = Math.max(messageObject.textWidth, Math.min(maxWidth2, linesMaxWidth));
                                    block = a3;
                                }
                                linesOffset2 = linesOffset + currentBlockLinesCount2;
                                a = i2 + 1;
                                z7 = useManualParse;
                                hasEntities = z;
                                useManualParse = z2;
                                hasUrls = z3;
                                needShare = z4;
                                textLayout2 = staticLayout;
                                paint = textPaint;
                                user = fromUser;
                                i = 24;
                            } else {
                                textPaint = paint;
                                z2 = useManualParse;
                                z3 = hasUrls;
                                z4 = needShare;
                                linesOffset = linesOffset2;
                                staticLayout = textLayout2;
                                return;
                            }
                        }
                    }
                }
                f = 132.0f;
                maxWidth2 -= AndroidUtilities.dp(f);
                maxWidth2 -= AndroidUtilities.dp(20.0f);
                if (messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                    maxWidth2 -= AndroidUtilities.dp(10.0f);
                }
                if (messageObject.messageOwner.media instanceof TL_messageMediaGame) {
                    paint = Theme.chat_msgTextPaint;
                } else {
                    paint = Theme.chat_msgGameTextPaint;
                }
                try {
                    if (VERSION.SDK_INT < 24) {
                        i = 24;
                        textLayout = new StaticLayout(messageObject.messageText, paint, maxWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    } else {
                        textLayout = Builder.obtain(messageObject.messageText, 0, messageObject.messageText.length(), paint, maxWidth2).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Alignment.ALIGN_NORMAL).build();
                        i = 24;
                    }
                    textLayout2 = textLayout;
                    messageObject.textHeight = textLayout2.getHeight();
                    messageObject.linesCount = textLayout2.getLineCount();
                    if (VERSION.SDK_INT < i) {
                        blocksCount = (int) Math.ceil((double) (((float) messageObject.linesCount) / 10.0f));
                    } else {
                        blocksCount = true;
                    }
                    linesOffset2 = 0;
                    prevOffset = 0.0f;
                    a = false;
                    while (true) {
                        a2 = a;
                        if (a2 >= blocksCount) {
                            textPaint = paint;
                            z2 = useManualParse;
                            z3 = hasUrls;
                            z4 = needShare;
                            linesOffset = linesOffset2;
                            staticLayout = textLayout2;
                            return;
                        }
                        if (VERSION.SDK_INT < i) {
                            currentBlockLinesCount = Math.min(10, messageObject.linesCount - linesOffset2);
                        } else {
                            currentBlockLinesCount = messageObject.linesCount;
                        }
                        i = currentBlockLinesCount;
                        block = new TextLayoutBlock();
                        if (blocksCount != z7) {
                            startCharacter = textLayout2.getLineStart(linesOffset2);
                            z = hasEntities;
                            hasEntities = textLayout2.getLineEnd((linesOffset2 + i) - 1);
                            if (hasEntities >= startCharacter) {
                                block.charactersOffset = startCharacter;
                                block.charactersEnd = hasEntities;
                                if (hasUrls) {
                                    z2 = useManualParse;
                                } else {
                                    z2 = useManualParse;
                                    if (VERSION.SDK_INT >= true) {
                                        useManualParse = true;
                                        block.textLayout = Builder.obtain(messageObject.messageText, startCharacter, hasEntities, paint, AndroidUtilities.dp(2.0f) + maxWidth2).setBreakStrategy(1).setHyphenationFrequency(0).setAlignment(Alignment.ALIGN_NORMAL).build();
                                        z5 = startCharacter;
                                        z3 = hasUrls;
                                        z4 = needShare;
                                        hasUrls = block;
                                        i2 = a2;
                                        linesOffset3 = linesOffset2;
                                        textLayout3 = textLayout2;
                                        hasUrls.textYOffset = (float) textLayout3.getLineTop(linesOffset3);
                                        if (i2 == true) {
                                            hasUrls.height = (int) (hasUrls.textYOffset - prevOffset);
                                        }
                                        hasUrls.height = Math.max(hasUrls.height, hasUrls.textLayout.getLineBottom(hasUrls.textLayout.getLineCount() - 1));
                                        prevOffset = hasUrls.textYOffset;
                                        if (i2 == blocksCount - 1) {
                                            i = Math.max(i, hasUrls.textLayout.getLineCount());
                                            messageObject.textHeight = Math.max(messageObject.textHeight, (int) (hasUrls.textYOffset + ((float) hasUrls.textLayout.getHeight())));
                                        }
                                    }
                                }
                                z3 = hasUrls;
                                hasUrls = block;
                                textLayout = textLayout;
                                z4 = needShare;
                                i2 = a2;
                                linesOffset3 = linesOffset2;
                                z6 = startCharacter;
                                z5 = startCharacter;
                                textLayout3 = textLayout2;
                                hasUrls.textLayout = new StaticLayout(messageObject.messageText, z6, hasEntities, paint, maxWidth2, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                hasUrls.textYOffset = (float) textLayout3.getLineTop(linesOffset3);
                                if (i2 == true) {
                                    hasUrls.height = (int) (hasUrls.textYOffset - prevOffset);
                                }
                                hasUrls.height = Math.max(hasUrls.height, hasUrls.textLayout.getLineBottom(hasUrls.textLayout.getLineCount() - 1));
                                prevOffset = hasUrls.textYOffset;
                                if (i2 == blocksCount - 1) {
                                    i = Math.max(i, hasUrls.textLayout.getLineCount());
                                    messageObject.textHeight = Math.max(messageObject.textHeight, (int) (hasUrls.textYOffset + ((float) hasUrls.textLayout.getHeight())));
                                }
                            } else {
                                textPaint = paint;
                                z2 = useManualParse;
                                z3 = hasUrls;
                                z4 = needShare;
                                i2 = a2;
                                linesOffset = linesOffset2;
                                staticLayout = textLayout2;
                                useManualParse = z7;
                                linesOffset2 = linesOffset;
                                a = i2 + 1;
                                z7 = useManualParse;
                                hasEntities = z;
                                useManualParse = z2;
                                hasUrls = z3;
                                needShare = z4;
                                textLayout2 = staticLayout;
                                paint = textPaint;
                                user = fromUser;
                                i = 24;
                            }
                        } else {
                            block.textLayout = textLayout2;
                            block.textYOffset = 0.0f;
                            block.charactersOffset = 0;
                            block.height = messageObject.textHeight;
                            z = hasEntities;
                            z2 = useManualParse;
                            z3 = hasUrls;
                            z4 = needShare;
                            hasUrls = block;
                            i2 = a2;
                            linesOffset3 = linesOffset2;
                            textLayout3 = textLayout2;
                        }
                        messageObject.textLayoutBlocks.add(hasUrls);
                        hasEntities = hasUrls.textLayout.getLineLeft(i - 1);
                        messageObject.textXOffset = hasEntities;
                        lastLine = hasUrls.textLayout.getLineWidth(i - 1);
                        linesMaxWidth = (int) Math.ceil((double) lastLine);
                        if (i2 == blocksCount - 1) {
                            messageObject.lastLineWidth = linesMaxWidth;
                        }
                        a3 = (int) Math.ceil((double) (lastLine + hasEntities));
                        linesOffset2 = a3;
                        if (i <= 1) {
                            staticLayout = textLayout3;
                            currentBlockLinesCount2 = i;
                            lastLeft = hasEntities;
                            textPaint = paint;
                            linesOffset = linesOffset3;
                            maxWidth = maxWidth2;
                            f2 = lastLine;
                            if (lastLeft <= 0.0f) {
                                hasEntities = lastLeft;
                                useManualParse = true;
                                hasUrls.directionFlags = (byte) (hasUrls.directionFlags | 2);
                            } else {
                                hasEntities = lastLeft;
                                messageObject.textXOffset = Math.min(messageObject.textXOffset, hasEntities);
                                if (messageObject.textXOffset == 0.0f) {
                                    linesMaxWidth = (int) (((float) linesMaxWidth) + hasEntities);
                                }
                                useManualParse = true;
                                if (blocksCount) {
                                }
                                messageObject.hasRtl = blocksCount;
                                hasUrls.directionFlags = (byte) (hasUrls.directionFlags | 1);
                            }
                            maxWidth2 = maxWidth;
                            messageObject.textWidth = Math.max(messageObject.textWidth, Math.min(maxWidth2, linesMaxWidth));
                            block = a3;
                        } else {
                            textLayout2 = null;
                            staticLayout = textLayout3;
                            textPaint = paint;
                            lastLine = a3;
                            textLayout3 = 0.0f;
                            a3 = 0;
                            paint = linesMaxWidth;
                            linesMaxWidth = 0;
                            while (linesMaxWidth < i) {
                                currentBlockLinesCount2 = i;
                                i = hasUrls.textLayout.getLineWidth(linesMaxWidth);
                                linesOffset = linesOffset3;
                                if (i > ((float) (maxWidth2 + 20))) {
                                    i = (float) maxWidth2;
                                }
                                lineLeft = hasUrls.textLayout.getLineLeft(linesMaxWidth);
                                if (lineLeft <= 0.0f) {
                                    lastLeft = hasEntities;
                                    maxWidth = maxWidth2;
                                    hasUrls.directionFlags = (byte) (hasUrls.directionFlags | 2);
                                } else {
                                    maxWidth = maxWidth2;
                                    messageObject.textXOffset = Math.min(messageObject.textXOffset, lineLeft);
                                    lastLeft = hasEntities;
                                    hasUrls.directionFlags = (byte) (hasUrls.directionFlags | 1);
                                    messageObject.hasRtl = true;
                                }
                                if (hasUrls.textLayout.getParagraphDirection(linesMaxWidth)) {
                                    textLayout2 = true;
                                }
                                textLayout3 = Math.max(textLayout3, i);
                                a3 = Math.max(a3, i + lineLeft);
                                textRealMaxWidth = textLayout3;
                                paint = Math.max(paint, (int) Math.ceil((double) i));
                                lastLine = Math.max(lastLine, (int) Math.ceil((double) (i + lineLeft)));
                                linesMaxWidth++;
                                i = currentBlockLinesCount2;
                                linesOffset3 = linesOffset;
                                maxWidth2 = maxWidth;
                                hasEntities = lastLeft;
                                textLayout3 = textRealMaxWidth;
                                messageObject = this;
                            }
                            currentBlockLinesCount2 = i;
                            lastLeft = hasEntities;
                            linesOffset = linesOffset3;
                            maxWidth = maxWidth2;
                            if (textLayout2 == null) {
                                messageObject = this;
                                if (i2 == blocksCount - 1) {
                                    messageObject.lastLineWidth = paint;
                                }
                            } else {
                                textLayout3 = a3;
                                if (i2 != blocksCount - 1) {
                                    messageObject = this;
                                } else {
                                    this.lastLineWidth = linesOffset2;
                                }
                            }
                            messageObject.textWidth = Math.max(messageObject.textWidth, (int) Math.ceil((double) textLayout3));
                            maxWidth2 = maxWidth;
                            hasEntities = lastLeft;
                            useManualParse = true;
                        }
                        linesOffset2 = linesOffset + currentBlockLinesCount2;
                        a = i2 + 1;
                        z7 = useManualParse;
                        hasEntities = z;
                        useManualParse = z2;
                        hasUrls = z3;
                        needShare = z4;
                        textLayout2 = staticLayout;
                        paint = textPaint;
                        user = fromUser;
                        i = 24;
                    }
                } catch (Throwable th2NUM) {
                    z = hasEntities;
                    textPaint = paint;
                    z2 = useManualParse;
                    z3 = hasUrls;
                    z4 = needShare;
                    e = th2NUM;
                    FileLog.m3e(e);
                }
            }
        }
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    public boolean isOutOwner() {
        boolean z = false;
        if (this.messageOwner.out && this.messageOwner.from_id > 0) {
            if (!this.messageOwner.post) {
                if (this.messageOwner.fwd_from == null) {
                    return true;
                }
                int selfUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                if (getDialogId() == ((long) selfUserId)) {
                    if (this.messageOwner.fwd_from.from_id != selfUserId) {
                        if (this.messageOwner.fwd_from.saved_from_peer == null || this.messageOwner.fwd_from.saved_from_peer.user_id != selfUserId) {
                            return z;
                        }
                    }
                    z = true;
                    return z;
                }
                if (this.messageOwner.fwd_from.saved_from_peer != null) {
                    if (this.messageOwner.fwd_from.saved_from_peer.user_id != selfUserId) {
                        return z;
                    }
                }
                z = true;
                return z;
            }
        }
        return false;
    }

    public boolean needDrawAvatar() {
        if (!isFromUser() && this.eventId == 0) {
            if (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null) {
                return false;
            }
        }
        return true;
    }

    public boolean isFromUser() {
        return this.messageOwner.from_id > 0 && !this.messageOwner.post;
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
        int flags = 0;
        if (!message.unread) {
            flags = 0 | 1;
        }
        if (message.media_unread) {
            return flags;
        }
        return flags | 2;
    }

    public void setContentIsRead() {
        this.messageOwner.media_unread = false;
    }

    public int getId() {
        return this.messageOwner.id;
    }

    public static int getMessageSize(Message message) {
        if (message.media == null || message.media.document == null) {
            return 0;
        }
        return message.media.document.size;
    }

    public int getSize() {
        return getMessageSize(this.messageOwner);
    }

    public long getIdWithChannel() {
        long id = (long) this.messageOwner.id;
        if (this.messageOwner.to_id == null || this.messageOwner.to_id.channel_id == 0) {
            return id;
        }
        return id | (((long) this.messageOwner.to_id.channel_id) << 32);
    }

    public int getChannelId() {
        if (this.messageOwner.to_id != null) {
            return this.messageOwner.to_id.channel_id;
        }
        return 0;
    }

    public static boolean shouldEncryptPhotoOrVideo(Message message) {
        boolean z = false;
        if (message instanceof TL_message_secret) {
            if (((message.media instanceof TL_messageMediaPhoto) || isVideoMessage(message)) && message.ttl > 0 && message.ttl <= 60) {
                z = true;
            }
            return z;
        }
        if (((message.media instanceof TL_messageMediaPhoto) || (message.media instanceof TL_messageMediaDocument)) && message.media.ttl_seconds != 0) {
            z = true;
        }
        return z;
    }

    public boolean shouldEncryptPhotoOrVideo() {
        return shouldEncryptPhotoOrVideo(this.messageOwner);
    }

    public static boolean isSecretPhotoOrVideo(Message message) {
        boolean z = true;
        if (message instanceof TL_message_secret) {
            if ((!(message.media instanceof TL_messageMediaPhoto) && !isRoundVideoMessage(message) && !isVideoMessage(message)) || message.ttl <= 0 || message.ttl > 60) {
                z = false;
            }
            return z;
        } else if (!(message instanceof TL_message)) {
            return false;
        } else {
            if ((!(message.media instanceof TL_messageMediaPhoto) && !(message.media instanceof TL_messageMediaDocument)) || message.media.ttl_seconds == 0) {
                z = false;
            }
            return z;
        }
    }

    public boolean needDrawBluredPreview() {
        boolean z = true;
        if (this.messageOwner instanceof TL_message_secret) {
            int ttl = Math.max(this.messageOwner.ttl, this.messageOwner.media.ttl_seconds);
            if (ttl <= 0 || ((!((this.messageOwner.media instanceof TL_messageMediaPhoto) || isVideo() || isGif()) || ttl > 60) && !isRoundVideo())) {
                z = false;
            }
            return z;
        } else if (!(this.messageOwner instanceof TL_message)) {
            return false;
        } else {
            if ((!(this.messageOwner.media instanceof TL_messageMediaPhoto) && !(this.messageOwner.media instanceof TL_messageMediaDocument)) || this.messageOwner.media.ttl_seconds == 0) {
                z = false;
            }
            return z;
        }
    }

    public boolean isSecretMedia() {
        boolean z = true;
        if (this.messageOwner instanceof TL_message_secret) {
            if (!((((this.messageOwner.media instanceof TL_messageMediaPhoto) || isGif()) && this.messageOwner.ttl > 0 && this.messageOwner.ttl <= 60) || isVoice() || isRoundVideo())) {
                if (!isVideo()) {
                    z = false;
                }
            }
            return z;
        } else if (!(this.messageOwner instanceof TL_message)) {
            return false;
        } else {
            if ((!(this.messageOwner.media instanceof TL_messageMediaPhoto) && !(this.messageOwner.media instanceof TL_messageMediaDocument)) || this.messageOwner.media.ttl_seconds == 0) {
                z = false;
            }
            return z;
        }
    }

    public static void setUnreadFlags(Message message, int flag) {
        boolean z = false;
        message.unread = (flag & 1) == 0;
        if ((flag & 2) == 0) {
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
        if (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null || this.messageOwner.fwd_from.saved_from_peer.channel_id == 0) {
            return false;
        }
        return ChatObject.isMegagroup(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.saved_from_peer.channel_id)));
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
        if (document == null) {
            return false;
        }
        if (SharedConfig.streamAllVideo) {
            return true;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeVideo) {
                return attribute.supports_streaming;
            }
        }
        return false;
    }

    public static long getDialogId(Message message) {
        if (message.dialog_id == 0 && message.to_id != null) {
            if (message.to_id.chat_id != 0) {
                if (message.to_id.chat_id < 0) {
                    message.dialog_id = AndroidUtilities.makeBroadcastId(message.to_id.chat_id);
                } else {
                    message.dialog_id = (long) (-message.to_id.chat_id);
                }
            } else if (message.to_id.channel_id != 0) {
                message.dialog_id = (long) (-message.to_id.channel_id);
            } else if (isOut(message)) {
                message.dialog_id = (long) message.to_id.user_id;
            } else {
                message.dialog_id = (long) message.from_id;
            }
        }
        return message.dialog_id;
    }

    public boolean isSending() {
        return this.messageOwner.send_state == 1 && this.messageOwner.id < 0;
    }

    public boolean isSendError() {
        return this.messageOwner.send_state == 2 && this.messageOwner.id < 0;
    }

    public boolean isSent() {
        if (this.messageOwner.send_state != 0) {
            if (this.messageOwner.id <= 0) {
                return false;
            }
        }
        return true;
    }

    public int getSecretTimeLeft() {
        int secondsLeft = this.messageOwner.ttl;
        if (this.messageOwner.destroyTime != 0) {
            return Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
        }
        return secondsLeft;
    }

    public String getSecretTimeString() {
        if (!isSecretMedia()) {
            return null;
        }
        String str;
        int secondsLeft = getSecretTimeLeft();
        if (secondsLeft < 60) {
            str = new StringBuilder();
            str.append(secondsLeft);
            str.append("s");
            str = str.toString();
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(secondsLeft / 60);
            stringBuilder.append("m");
            str = stringBuilder.toString();
        }
        return str;
    }

    public String getDocumentName() {
        if (this.messageOwner.media instanceof TL_messageMediaDocument) {
            return FileLoader.getDocumentFileName(this.messageOwner.media.document);
        }
        if (this.messageOwner.media instanceof TL_messageMediaWebPage) {
            return FileLoader.getDocumentFileName(this.messageOwner.media.webpage.document);
        }
        return TtmlNode.ANONYMOUS_REGION_ID;
    }

    public static boolean isStickerDocument(Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                if (((DocumentAttribute) document.attributes.get(a)) instanceof TL_documentAttributeSticker) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMaskDocument(Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
                if ((attribute instanceof TL_documentAttributeSticker) && attribute.mask) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceDocument(Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
                if (attribute instanceof TL_documentAttributeAudio) {
                    return attribute.voice;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceWebDocument(TL_webDocument webDocument) {
        return webDocument != null && webDocument.mime_type.equals("audio/ogg");
    }

    public static boolean isImageWebDocument(TL_webDocument webDocument) {
        return (webDocument == null || isGifDocument(webDocument) || !webDocument.mime_type.startsWith("image/")) ? false : true;
    }

    public static boolean isVideoWebDocument(TL_webDocument webDocument) {
        return webDocument != null && webDocument.mime_type.startsWith("video/");
    }

    public static boolean isMusicDocument(Document document) {
        if (document != null) {
            for (int a = 0; a < document.attributes.size(); a++) {
                DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
                if (attribute instanceof TL_documentAttributeAudio) {
                    return attribute.voice ^ true;
                }
            }
            if (!TextUtils.isEmpty(document.mime_type)) {
                String mime = document.mime_type.toLowerCase();
                if (!(mime.equals(MimeTypes.AUDIO_FLAC) || mime.equals("audio/ogg") || mime.equals(MimeTypes.AUDIO_OPUS))) {
                    if (!mime.equals("audio/x-opus+ogg")) {
                        if (mime.equals("application/octet-stream") && FileLoader.getDocumentFileName(document).endsWith(".opus")) {
                            return true;
                        }
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isVideoDocument(Document document) {
        boolean z = false;
        if (document == null) {
            return false;
        }
        int width = 0;
        int height = 0;
        boolean isVideo = false;
        boolean isAnimated = false;
        for (int a = 0; a < document.attributes.size(); a++) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeVideo) {
                if (attribute.round_message) {
                    return false;
                }
                isVideo = true;
                width = attribute.f36w;
                height = attribute.f35h;
            } else if (attribute instanceof TL_documentAttributeAnimated) {
                isAnimated = true;
            }
        }
        if (isAnimated && (width > 1280 || height > 1280)) {
            isAnimated = false;
        }
        if (isVideo && !isAnimated) {
            z = true;
        }
        return z;
    }

    public Document getDocument() {
        if (this.messageOwner.media instanceof TL_messageMediaWebPage) {
            return this.messageOwner.media.webpage.document;
        }
        return this.messageOwner.media != null ? this.messageOwner.media.document : null;
    }

    public static boolean isStickerMessage(Message message) {
        return (message.media == null || message.media.document == null || !isStickerDocument(message.media.document)) ? false : true;
    }

    public static boolean isMaskMessage(Message message) {
        return (message.media == null || message.media.document == null || !isMaskDocument(message.media.document)) ? false : true;
    }

    public static boolean isMusicMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isMusicDocument(message.media.webpage.document);
        }
        boolean z = (message.media == null || message.media.document == null || !isMusicDocument(message.media.document)) ? false : true;
        return z;
    }

    public static boolean isGifMessage(Message message) {
        return (message.media == null || message.media.document == null || !isGifDocument(message.media.document)) ? false : true;
    }

    public static boolean isRoundVideoMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isRoundVideoDocument(message.media.webpage.document);
        }
        boolean z = (message.media == null || message.media.document == null || !isRoundVideoDocument(message.media.document)) ? false : true;
        return z;
    }

    public static boolean isPhoto(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return message.media.webpage.photo instanceof TL_photo;
        }
        return message.media instanceof TL_messageMediaPhoto;
    }

    public static boolean isVoiceMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isVoiceDocument(message.media.webpage.document);
        }
        boolean z = (message.media == null || message.media.document == null || !isVoiceDocument(message.media.document)) ? false : true;
        return z;
    }

    public static boolean isNewGifMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isNewGifDocument(message.media.webpage.document);
        }
        boolean z = (message.media == null || message.media.document == null || !isNewGifDocument(message.media.document)) ? false : true;
        return z;
    }

    public static boolean isLiveLocationMessage(Message message) {
        return message.media instanceof TL_messageMediaGeoLive;
    }

    public static boolean isVideoMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isVideoDocument(message.media.webpage.document);
        }
        boolean z = (message.media == null || message.media.document == null || !isVideoDocument(message.media.document)) ? false : true;
        return z;
    }

    public static boolean isGameMessage(Message message) {
        return message.media instanceof TL_messageMediaGame;
    }

    public static boolean isInvoiceMessage(Message message) {
        return message.media instanceof TL_messageMediaInvoice;
    }

    public static InputStickerSet getInputStickerSet(Message message) {
        if (!(message.media == null || message.media.document == null)) {
            Iterator it = message.media.document.attributes.iterator();
            while (it.hasNext()) {
                DocumentAttribute attribute = (DocumentAttribute) it.next();
                if (attribute instanceof TL_documentAttributeSticker) {
                    if (attribute.stickerset instanceof TL_inputStickerSetEmpty) {
                        return null;
                    }
                    return attribute.stickerset;
                }
            }
        }
        return null;
    }

    public static long getStickerSetId(Document document) {
        if (document == null) {
            return -1;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (!(attribute instanceof TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.stickerset instanceof TL_inputStickerSetEmpty) {
                return -1;
            } else {
                return attribute.stickerset.id;
            }
        }
        return -1;
    }

    public String getStrickerChar() {
        if (!(this.messageOwner.media == null || this.messageOwner.media.document == null)) {
            Iterator it = this.messageOwner.media.document.attributes.iterator();
            while (it.hasNext()) {
                DocumentAttribute attribute = (DocumentAttribute) it.next();
                if (attribute instanceof TL_documentAttributeSticker) {
                    return attribute.alt;
                }
            }
        }
        return null;
    }

    public int getApproximateHeight() {
        int height;
        if (this.type == 0) {
            height = this.textHeight;
            int dp = ((this.messageOwner.media instanceof TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TL_webPage)) ? AndroidUtilities.dp(100.0f) : 0;
            height += dp;
            if (isReply()) {
                height += AndroidUtilities.dp(42.0f);
            }
            return height;
        } else if (this.type == 2) {
            return AndroidUtilities.dp(72.0f);
        } else {
            if (this.type == 12) {
                return AndroidUtilities.dp(71.0f);
            }
            if (this.type == 9) {
                return AndroidUtilities.dp(100.0f);
            }
            if (this.type == 4) {
                return AndroidUtilities.dp(114.0f);
            }
            if (this.type == 14) {
                return AndroidUtilities.dp(82.0f);
            }
            if (this.type == 10) {
                return AndroidUtilities.dp(30.0f);
            }
            if (this.type == 11) {
                return AndroidUtilities.dp(50.0f);
            }
            if (this.type == 5) {
                return AndroidUtilities.roundMessageSize;
            }
            if (this.type == 13) {
                float maxWidth;
                float maxHeight = ((float) AndroidUtilities.displaySize.y) * 0.4f;
                if (AndroidUtilities.isTablet()) {
                    maxWidth = ((float) AndroidUtilities.getMinTabletSide()) * 0.5f;
                } else {
                    maxWidth = ((float) AndroidUtilities.displaySize.x) * 0.5f;
                }
                int photoHeight = 0;
                int photoWidth = 0;
                Iterator it = this.messageOwner.media.document.attributes.iterator();
                while (it.hasNext()) {
                    DocumentAttribute attribute = (DocumentAttribute) it.next();
                    if (attribute instanceof TL_documentAttributeImageSize) {
                        photoWidth = attribute.f36w;
                        photoHeight = attribute.f35h;
                        break;
                    }
                }
                if (photoWidth == 0) {
                    photoHeight = (int) maxHeight;
                    photoWidth = photoHeight + AndroidUtilities.dp(100.0f);
                }
                if (((float) photoHeight) > maxHeight) {
                    photoWidth = (int) (((float) photoWidth) * (maxHeight / ((float) photoHeight)));
                    photoHeight = (int) maxHeight;
                }
                if (((float) photoWidth) > maxWidth) {
                    photoHeight = (int) (((float) photoHeight) * (maxWidth / ((float) photoWidth)));
                }
                return AndroidUtilities.dp(14.0f) + photoHeight;
            }
            if (AndroidUtilities.isTablet()) {
                height = (int) (((float) AndroidUtilities.getMinTabletSide()) * NUM);
            } else {
                height = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
            }
            int photoHeight2 = AndroidUtilities.dp(100.0f) + height;
            if (height > AndroidUtilities.getPhotoSize()) {
                height = AndroidUtilities.getPhotoSize();
            }
            if (photoHeight2 > AndroidUtilities.getPhotoSize()) {
                photoHeight2 = AndroidUtilities.getPhotoSize();
            }
            PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
            if (currentPhotoObject != null) {
                int h = (int) (((float) currentPhotoObject.f42h) / (((float) currentPhotoObject.f43w) / ((float) height)));
                if (h == 0) {
                    h = AndroidUtilities.dp(100.0f);
                }
                if (h > photoHeight2) {
                    h = photoHeight2;
                } else if (h < AndroidUtilities.dp(120.0f)) {
                    h = AndroidUtilities.dp(120.0f);
                }
                if (needDrawBluredPreview()) {
                    if (AndroidUtilities.isTablet()) {
                        h = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                    } else {
                        h = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f);
                    }
                }
                photoHeight2 = h;
            }
            return AndroidUtilities.dp(14.0f) + photoHeight2;
        }
    }

    public String getStickerEmoji() {
        String str;
        int a = 0;
        while (true) {
            str = null;
            if (a >= this.messageOwner.media.document.attributes.size()) {
                return null;
            }
            DocumentAttribute attribute = (DocumentAttribute) this.messageOwner.media.document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeSticker) {
                break;
            }
            a++;
        }
        if (attribute.alt != null && attribute.alt.length() > 0) {
            str = attribute.alt;
        }
        return str;
    }

    public boolean isSticker() {
        if (this.type == 1000) {
            return isStickerMessage(this.messageOwner);
        }
        return this.type == 13;
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
            int i;
            if (this.type != 5) {
                if (!isRoundVideoMessage(this.messageOwner)) {
                    i = 2;
                    this.isRoundVideoCached = i;
                }
            }
            i = 1;
            this.isRoundVideoCached = i;
        }
        if (this.isRoundVideoCached == 1) {
            return true;
        }
        return false;
    }

    public boolean hasPhotoStickers() {
        return (this.messageOwner.media == null || this.messageOwner.media.photo == null || !this.messageOwner.media.photo.has_stickers) ? false : true;
    }

    public boolean isGif() {
        return isGifMessage(this.messageOwner);
    }

    public boolean isWebpageDocument() {
        return (!(this.messageOwner.media instanceof TL_messageMediaWebPage) || this.messageOwner.media.webpage.document == null || isGifDocument(this.messageOwner.media.webpage.document)) ? false : true;
    }

    public boolean isWebpage() {
        return this.messageOwner.media instanceof TL_messageMediaWebPage;
    }

    public boolean isNewGif() {
        return this.messageOwner.media != null && isNewGifDocument(this.messageOwner.media.document);
    }

    public String getMusicTitle() {
        return getMusicTitle(true);
    }

    public String getMusicTitle(boolean unknown) {
        Document document;
        if (this.type == 0) {
            document = this.messageOwner.media.webpage.document;
        } else {
            document = this.messageOwner.media.document;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeAudio) {
                if (!attribute.voice) {
                    String title = attribute.title;
                    if (title == null || title.length() == 0) {
                        title = FileLoader.getDocumentFileName(document);
                        if (TextUtils.isEmpty(title) && unknown) {
                            title = LocaleController.getString("AudioUnknownTitle", R.string.AudioUnknownTitle);
                        }
                    }
                    return title;
                } else if (unknown) {
                    return LocaleController.formatDateAudio((long) this.messageOwner.date);
                } else {
                    return null;
                }
            } else if ((attribute instanceof TL_documentAttributeVideo) && attribute.round_message) {
                return LocaleController.formatDateAudio((long) this.messageOwner.date);
            } else {
                a++;
            }
        }
        String fileName = FileLoader.getDocumentFileName(document);
        return TextUtils.isEmpty(fileName) ? LocaleController.getString("AudioUnknownTitle", R.string.AudioUnknownTitle) : fileName;
    }

    public int getDuration() {
        Document document;
        if (this.type == 0) {
            document = this.messageOwner.media.webpage.document;
        } else {
            document = this.messageOwner.media.document;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeAudio) {
                return attribute.duration;
            }
            if (attribute instanceof TL_documentAttributeVideo) {
                return attribute.duration;
            }
        }
        return this.audioPlayerDuration;
    }

    public String getMusicAuthor() {
        return getMusicAuthor(true);
    }

    public String getMusicAuthor(boolean unknown) {
        Document document;
        if (this.type == 0) {
            document = this.messageOwner.media.webpage.document;
        } else {
            document = this.messageOwner.media.document;
        }
        boolean isVoice = false;
        for (int a = 0; a < document.attributes.size(); a++) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeAudio) {
                if (attribute.voice) {
                    isVoice = true;
                } else {
                    String performer = attribute.performer;
                    if (TextUtils.isEmpty(performer) && unknown) {
                        performer = LocaleController.getString("AudioUnknownArtist", R.string.AudioUnknownArtist);
                    }
                    return performer;
                }
            } else if ((attribute instanceof TL_documentAttributeVideo) && attribute.round_message) {
                isVoice = true;
            }
            if (isVoice) {
                if (!unknown) {
                    return null;
                }
                if (!isOutOwner()) {
                    if (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.from_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        User user = null;
                        Chat chat = null;
                        if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.channel_id != 0) {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                        } else if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_id != 0) {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                        } else if (this.messageOwner.from_id < 0) {
                            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-this.messageOwner.from_id));
                        } else {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                        }
                        if (user != null) {
                            return UserObject.getUserName(user);
                        }
                        if (chat != null) {
                            return chat.title;
                        }
                    }
                }
                return LocaleController.getString("FromYou", R.string.FromYou);
            }
        }
        return LocaleController.getString("AudioUnknownArtist", R.string.AudioUnknownArtist);
    }

    public InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    public boolean needDrawForwarded() {
        return ((this.messageOwner.flags & 4) == 0 || this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer != null || ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()) == getDialogId()) ? false : true;
    }

    public static boolean isForwardedMessage(Message message) {
        return ((message.flags & 4) == 0 || message.fwd_from == null) ? false : true;
    }

    public boolean isReply() {
        return (this.replyMessageObject == null || !(this.replyMessageObject.messageOwner instanceof TL_messageEmpty)) && !((this.messageOwner.reply_to_msg_id == 0 && this.messageOwner.reply_to_random_id == 0) || (this.messageOwner.flags & 8) == 0);
    }

    public boolean isMediaEmpty() {
        return isMediaEmpty(this.messageOwner);
    }

    public static boolean isMediaEmpty(Message message) {
        if (!(message == null || message.media == null || (message.media instanceof TL_messageMediaEmpty))) {
            if (!(message.media instanceof TL_messageMediaWebPage)) {
                return false;
            }
        }
        return true;
    }

    public boolean canEditMessage(Chat chat) {
        return canEditMessage(this.currentAccount, this.messageOwner, chat);
    }

    public boolean canEditMessageAnytime(Chat chat) {
        return canEditMessageAnytime(this.currentAccount, this.messageOwner, chat);
    }

    public static boolean canEditMessageAnytime(int currentAccount, Message message, Chat chat) {
        if (!(message == null || message.to_id == null || ((message.media != null && (isRoundVideoDocument(message.media.document) || isStickerDocument(message.media.document))) || ((message.action != null && !(message.action instanceof TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0)))) {
            if (message.id >= 0) {
                if (message.from_id == message.to_id.user_id && message.from_id == UserConfig.getInstance(currentAccount).getClientUserId() && !isLiveLocationMessage(message)) {
                    return true;
                }
                if (chat == null && message.to_id.channel_id != 0) {
                    chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(message.to_id.channel_id));
                    if (chat == null) {
                        return false;
                    }
                }
                if (!message.out || chat == null || !chat.megagroup || (!chat.creator && (chat.admin_rights == null || !chat.admin_rights.pin_messages))) {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean canEditMessage(int currentAccount, Message message, Chat chat) {
        boolean z = false;
        if (!(message == null || message.to_id == null || ((message.media != null && (isRoundVideoDocument(message.media.document) || isStickerDocument(message.media.document))) || ((message.action != null && !(message.action instanceof TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0)))) {
            if (message.id >= 0) {
                if (message.from_id == message.to_id.user_id && message.from_id == UserConfig.getInstance(currentAccount).getClientUserId() && !isLiveLocationMessage(message) && !(message.media instanceof TL_messageMediaContact)) {
                    return true;
                }
                if (chat == null && message.to_id.channel_id != 0) {
                    chat = MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(message.to_id.channel_id));
                    if (chat == null) {
                        return false;
                    }
                }
                if (message.media != null && !(message.media instanceof TL_messageMediaEmpty) && !(message.media instanceof TL_messageMediaPhoto) && !(message.media instanceof TL_messageMediaDocument) && !(message.media instanceof TL_messageMediaWebPage)) {
                    return false;
                }
                if (message.out && chat != null && chat.megagroup && (chat.creator || (chat.admin_rights != null && chat.admin_rights.pin_messages))) {
                    return true;
                }
                if (Math.abs(message.date - ConnectionsManager.getInstance(currentAccount).getCurrentTime()) > MessagesController.getInstance(currentAccount).maxEditTime) {
                    return false;
                }
                if (message.to_id.channel_id == 0) {
                    if (message.out || message.from_id == UserConfig.getInstance(currentAccount).getClientUserId()) {
                        if (!((message.media instanceof TL_messageMediaPhoto) || (((message.media instanceof TL_messageMediaDocument) && !isStickerMessage(message)) || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage)))) {
                            if (message.media == null) {
                            }
                        }
                        z = true;
                        return z;
                    }
                    return z;
                } else if (((!chat.megagroup || !message.out) && (chat.megagroup || ((!chat.creator && (chat.admin_rights == null || (!chat.admin_rights.edit_messages && !message.out))) || !message.post))) || (!(message.media instanceof TL_messageMediaPhoto) && ((!(message.media instanceof TL_messageMediaDocument) || isStickerMessage(message)) && !(message.media instanceof TL_messageMediaEmpty) && !(message.media instanceof TL_messageMediaWebPage) && message.media != null))) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canDeleteMessage(Chat chat) {
        return this.eventId == 0 && canDeleteMessage(this.currentAccount, this.messageOwner, chat);
    }

    public static boolean canDeleteMessage(int currentAccount, Message message, Chat chat) {
        boolean z = true;
        if (message.id < 0) {
            return true;
        }
        if (chat == null && message.to_id.channel_id != 0) {
            chat = MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(message.to_id.channel_id));
        }
        if (ChatObject.isChannel(chat)) {
            if (message.id == 1 || (!chat.creator && ((chat.admin_rights == null || !(chat.admin_rights.delete_messages || message.out)) && !(chat.megagroup && message.out && message.from_id > 0)))) {
                z = false;
            }
            return z;
        }
        if (!isOut(message)) {
            if (ChatObject.isChannel(chat)) {
                z = false;
            }
        }
        return z;
    }

    public String getForwardedName() {
        if (this.messageOwner.fwd_from != null) {
            if (this.messageOwner.fwd_from.channel_id != 0) {
                Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                if (chat != null) {
                    return chat.title;
                }
            } else if (this.messageOwner.fwd_from.from_id != 0) {
                User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                if (user != null) {
                    return UserObject.getUserName(user);
                }
            }
        }
        return null;
    }

    public int getFromId() {
        if (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null) {
            if (this.messageOwner.from_id != 0) {
                return this.messageOwner.from_id;
            }
            if (this.messageOwner.post) {
                return this.messageOwner.to_id.channel_id;
            }
        } else if (this.messageOwner.fwd_from.saved_from_peer.user_id != 0) {
            if (this.messageOwner.fwd_from.from_id != 0) {
                return this.messageOwner.fwd_from.from_id;
            }
            return this.messageOwner.fwd_from.saved_from_peer.user_id;
        } else if (this.messageOwner.fwd_from.saved_from_peer.channel_id != 0) {
            if (isSavedFromMegagroup() && this.messageOwner.fwd_from.from_id != 0) {
                return this.messageOwner.fwd_from.from_id;
            }
            if (this.messageOwner.fwd_from.channel_id != 0) {
                return -this.messageOwner.fwd_from.channel_id;
            }
            return -this.messageOwner.fwd_from.saved_from_peer.channel_id;
        } else if (this.messageOwner.fwd_from.saved_from_peer.chat_id != 0) {
            if (this.messageOwner.fwd_from.from_id != 0) {
                return this.messageOwner.fwd_from.from_id;
            }
            if (this.messageOwner.fwd_from.channel_id != 0) {
                return -this.messageOwner.fwd_from.channel_id;
            }
            return -this.messageOwner.fwd_from.saved_from_peer.chat_id;
        }
        return 0;
    }

    public void checkMediaExistance() {
        this.attachPathExists = false;
        this.mediaExists = false;
        if (this.type != 1) {
            if (!(this.type == 8 || this.type == 3 || this.type == 9 || this.type == 2 || this.type == 14)) {
                if (this.type != 5) {
                    Document document = getDocument();
                    if (document != null) {
                        this.mediaExists = FileLoader.getPathToAttach(document).exists();
                    } else if (this.type == 0) {
                        PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                        if (!(currentPhotoObject == null || currentPhotoObject == null)) {
                            this.mediaExists = FileLoader.getPathToAttach(currentPhotoObject, true).exists();
                        }
                    }
                }
            }
            if (this.messageOwner.attachPath != null && this.messageOwner.attachPath.length() > 0) {
                this.attachPathExists = new File(this.messageOwner.attachPath).exists();
            }
            if (!this.attachPathExists) {
                File file = FileLoader.getPathToMessage(this.messageOwner);
                if (this.type == 3 && needDrawBluredPreview()) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(file.getAbsolutePath());
                    stringBuilder.append(".enc");
                    this.mediaExists = new File(stringBuilder.toString()).exists();
                }
                if (!this.mediaExists) {
                    this.mediaExists = file.exists();
                }
            }
        } else if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
            File file2 = FileLoader.getPathToMessage(this.messageOwner);
            if (needDrawBluredPreview()) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append(file2.getAbsolutePath());
                stringBuilder2.append(".enc");
                this.mediaExists = new File(stringBuilder2.toString()).exists();
            }
            if (!this.mediaExists) {
                this.mediaExists = file2.exists();
            }
        }
    }
}
