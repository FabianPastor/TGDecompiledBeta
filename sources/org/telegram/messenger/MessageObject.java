package org.telegram.messenger;

import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
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

        public void calculate() {
            this.posArray.clear();
            this.positions.clear();
            int size = this.messages.size();
            int i = 1;
            if (size > 1) {
                boolean isOutOwner;
                int i2;
                GroupedMessagePosition groupedMessagePosition;
                float f;
                int i3;
                byte b;
                int max;
                int i4;
                int i5;
                StringBuilder stringBuilder = new StringBuilder();
                r10.hasSibling = false;
                int i6 = 0;
                int i7 = i6;
                int i8 = i7;
                int i9 = i8;
                float f2 = 1.0f;
                while (i6 < size) {
                    MessageObject messageObject = (MessageObject) r10.messages.get(i6);
                    if (i6 == 0) {
                        isOutOwner = messageObject.isOutOwner();
                        i2 = (isOutOwner || ((messageObject.messageOwner.fwd_from == null || messageObject.messageOwner.fwd_from.saved_from_peer == null) && (messageObject.messageOwner.from_id <= 0 || (messageObject.messageOwner.to_id.channel_id == 0 && messageObject.messageOwner.to_id.chat_id == 0 && !(messageObject.messageOwner.media instanceof TL_messageMediaGame) && !(messageObject.messageOwner.media instanceof TL_messageMediaInvoice))))) ? false : 1;
                        i9 = isOutOwner;
                        i7 = i2;
                    }
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                    groupedMessagePosition = new GroupedMessagePosition();
                    groupedMessagePosition.last = i6 == size + -1;
                    if (closestPhotoSizeWithSize == null) {
                        f = 1.0f;
                    } else {
                        f = ((float) closestPhotoSizeWithSize.f43w) / ((float) closestPhotoSizeWithSize.f42h);
                    }
                    groupedMessagePosition.aspectRatio = f;
                    if (groupedMessagePosition.aspectRatio > 1.2f) {
                        stringBuilder.append("w");
                    } else if (groupedMessagePosition.aspectRatio < 0.8f) {
                        stringBuilder.append("n");
                    } else {
                        stringBuilder.append("q");
                    }
                    f2 += groupedMessagePosition.aspectRatio;
                    if (groupedMessagePosition.aspectRatio > 2.0f) {
                        i8 = 1;
                    }
                    r10.positions.put(messageObject, groupedMessagePosition);
                    r10.posArray.add(groupedMessagePosition);
                    i6++;
                }
                if (i7 != 0) {
                    r10.maxSizeWidth -= 50;
                    r10.firstSpanAdditionalSize += 50;
                }
                i7 = AndroidUtilities.dp(120.0f);
                int dp = (int) (((float) AndroidUtilities.dp(120.0f)) / (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) r10.maxSizeWidth)));
                i6 = (int) (((float) AndroidUtilities.dp(40.0f)) / (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) / ((float) r10.maxSizeWidth)));
                float f3 = ((float) r10.maxSizeWidth) / 814.0f;
                float f4 = f2 / ((float) size);
                int i10 = 3;
                int i11 = 2;
                float min;
                float f5;
                if (i8 == 0 && (size == 2 || size == 3 || size == 4)) {
                    GroupedMessagePosition groupedMessagePosition2;
                    float round;
                    int i12;
                    if (size == 2) {
                        byte b2;
                        GroupedMessagePosition groupedMessagePosition3 = (GroupedMessagePosition) r10.posArray.get(0);
                        groupedMessagePosition2 = (GroupedMessagePosition) r10.posArray.get(1);
                        String stringBuilder2 = stringBuilder.toString();
                        if (stringBuilder2.equals("ww")) {
                            i3 = i9;
                            if (((double) f4) > 1.4d * ((double) f3) && ((double) (groupedMessagePosition3.aspectRatio - groupedMessagePosition2.aspectRatio)) < 0.2d) {
                                round = ((float) Math.round(Math.min(((float) r10.maxSizeWidth) / groupedMessagePosition3.aspectRatio, Math.min(((float) r10.maxSizeWidth) / groupedMessagePosition2.aspectRatio, 407.0f)))) / 814.0f;
                                groupedMessagePosition3.set(0, 0, 0, 0, r10.maxSizeWidth, round, 7);
                                groupedMessagePosition2.set(0, 0, 1, 1, r10.maxSizeWidth, round, 11);
                                b2 = (byte) 0;
                                b = b2;
                            }
                        } else {
                            i3 = i9;
                        }
                        if (!stringBuilder2.equals("ww")) {
                            if (!stringBuilder2.equals("qq")) {
                                max = (int) Math.max(0.4f * ((float) r10.maxSizeWidth), (float) Math.round((((float) r10.maxSizeWidth) / groupedMessagePosition3.aspectRatio) / ((1.0f / groupedMessagePosition3.aspectRatio) + (1.0f / groupedMessagePosition2.aspectRatio))));
                                i10 = r10.maxSizeWidth - max;
                                if (i10 < dp) {
                                    max -= dp - i10;
                                    i10 = dp;
                                }
                                round = Math.min(814.0f, (float) Math.round(Math.min(((float) i10) / groupedMessagePosition3.aspectRatio, ((float) max) / groupedMessagePosition2.aspectRatio))) / 814.0f;
                                groupedMessagePosition3.set(0, 0, 0, 0, i10, round, 13);
                                groupedMessagePosition2.set(1, 1, 0, 0, max, round, 14);
                                b2 = (byte) 1;
                                b = b2;
                            }
                        }
                        max = r10.maxSizeWidth / 2;
                        f2 = (float) max;
                        i12 = max;
                        round = ((float) Math.round(Math.min(f2 / groupedMessagePosition3.aspectRatio, Math.min(f2 / groupedMessagePosition2.aspectRatio, 814.0f)))) / 814.0f;
                        groupedMessagePosition3.set(0, 0, 0, 0, i12, round, 13);
                        groupedMessagePosition2.set(1, 1, 0, 0, i12, round, 14);
                        b2 = (byte) 1;
                        b = b2;
                    } else {
                        i3 = i9;
                        GroupedMessagePosition groupedMessagePosition4;
                        float f6;
                        if (size == 3) {
                            groupedMessagePosition2 = (GroupedMessagePosition) r10.posArray.get(0);
                            groupedMessagePosition4 = (GroupedMessagePosition) r10.posArray.get(1);
                            groupedMessagePosition = (GroupedMessagePosition) r10.posArray.get(2);
                            int i13;
                            if (stringBuilder.charAt(0) == 'n') {
                                min = Math.min(407.0f, (float) Math.round((groupedMessagePosition4.aspectRatio * ((float) r10.maxSizeWidth)) / (groupedMessagePosition.aspectRatio + groupedMessagePosition4.aspectRatio)));
                                f2 = 814.0f - min;
                                i8 = (int) Math.max((float) dp, Math.min(((float) r10.maxSizeWidth) * 0.5f, (float) Math.round(Math.min(groupedMessagePosition.aspectRatio * min, groupedMessagePosition4.aspectRatio * f2))));
                                i6 = Math.round(Math.min((groupedMessagePosition2.aspectRatio * 814.0f) + ((float) i6), (float) (r10.maxSizeWidth - i8)));
                                groupedMessagePosition2.set(0, 0, 0, 1, i6, 1.0f, 13);
                                i13 = i8;
                                groupedMessagePosition4.set(1, 1, 0, 0, i13, f2 / 814.0f, 6);
                                groupedMessagePosition.set(0, 1, 1, 1, i13, min / 814.0f, 10);
                                groupedMessagePosition.spanSize = r10.maxSizeWidth;
                                groupedMessagePosition2.siblingHeights = new float[]{min, f2};
                                if (i3 != 0) {
                                    groupedMessagePosition2.spanSize = r10.maxSizeWidth - i8;
                                } else {
                                    groupedMessagePosition4.spanSize = r10.maxSizeWidth - i6;
                                    groupedMessagePosition.leftSpanOffset = i6;
                                }
                                r10.hasSibling = true;
                            } else {
                                min = ((float) Math.round(Math.min(((float) r10.maxSizeWidth) / groupedMessagePosition2.aspectRatio, 537.24005f))) / 814.0f;
                                groupedMessagePosition2.set(0, 1, 0, 0, r10.maxSizeWidth, min, 7);
                                i6 = r10.maxSizeWidth / 2;
                                f6 = (float) i6;
                                i13 = i6;
                                float min2 = Math.min(814.0f - min, (float) Math.round(Math.min(f6 / groupedMessagePosition4.aspectRatio, f6 / groupedMessagePosition.aspectRatio))) / 814.0f;
                                groupedMessagePosition4.set(0, 0, 1, 1, i13, min2, 9);
                                groupedMessagePosition.set(1, 1, 1, 1, i13, min2, 10);
                            }
                        } else {
                            if (size == 4) {
                                byte b3;
                                groupedMessagePosition4 = (GroupedMessagePosition) r10.posArray.get(0);
                                groupedMessagePosition = (GroupedMessagePosition) r10.posArray.get(1);
                                GroupedMessagePosition groupedMessagePosition5 = (GroupedMessagePosition) r10.posArray.get(2);
                                GroupedMessagePosition groupedMessagePosition6 = (GroupedMessagePosition) r10.posArray.get(3);
                                if (stringBuilder.charAt(0) == 'w') {
                                    min = ((float) Math.round(Math.min(((float) r10.maxSizeWidth) / groupedMessagePosition4.aspectRatio, 537.24005f))) / 814.0f;
                                    groupedMessagePosition4.set(0, 2, 0, 0, r10.maxSizeWidth, min, 7);
                                    float round2 = (float) Math.round(((float) r10.maxSizeWidth) / ((groupedMessagePosition.aspectRatio + groupedMessagePosition5.aspectRatio) + groupedMessagePosition6.aspectRatio));
                                    f6 = (float) dp;
                                    i10 = (int) Math.max(f6, Math.min(((float) r10.maxSizeWidth) * 0.4f, groupedMessagePosition.aspectRatio * round2));
                                    i11 = (int) Math.max(Math.max(f6, ((float) r10.maxSizeWidth) * 0.33f), groupedMessagePosition6.aspectRatio * round2);
                                    i7 = (r10.maxSizeWidth - i10) - i11;
                                    round = Math.min(814.0f - min, round2) / 814.0f;
                                    groupedMessagePosition.set(0, 0, 1, 1, i10, round, 9);
                                    groupedMessagePosition5.set(1, 1, 1, 1, i7, round, 8);
                                    groupedMessagePosition6.set(2, 2, 1, 1, i11, round, 10);
                                    b3 = (byte) 2;
                                } else {
                                    max = Math.max(dp, Math.round(814.0f / (((1.0f / groupedMessagePosition.aspectRatio) + (1.0f / groupedMessagePosition5.aspectRatio)) + (1.0f / ((GroupedMessagePosition) r10.posArray.get(3)).aspectRatio))));
                                    f6 = (float) i7;
                                    f5 = (float) max;
                                    f = Math.min(0.33f, Math.max(f6, f5 / groupedMessagePosition.aspectRatio) / 814.0f);
                                    float min3 = Math.min(0.33f, Math.max(f6, f5 / groupedMessagePosition5.aspectRatio) / 814.0f);
                                    f6 = (1.0f - f) - min3;
                                    i6 = Math.round(Math.min((814.0f * groupedMessagePosition4.aspectRatio) + ((float) i6), (float) (r10.maxSizeWidth - max)));
                                    groupedMessagePosition4.set(0, 0, 0, 2, i6, (f + min3) + f6, 13);
                                    i12 = max;
                                    groupedMessagePosition.set(1, 1, 0, 0, i12, f, 6);
                                    groupedMessagePosition5.set(0, 1, 1, 1, i12, min3, 2);
                                    groupedMessagePosition5.spanSize = r10.maxSizeWidth;
                                    groupedMessagePosition6.set(0, 1, 2, 2, i12, f6, 10);
                                    groupedMessagePosition6.spanSize = r10.maxSizeWidth;
                                    if (i3 != 0) {
                                        groupedMessagePosition4.spanSize = r10.maxSizeWidth - max;
                                    } else {
                                        groupedMessagePosition.spanSize = r10.maxSizeWidth - i6;
                                        groupedMessagePosition5.leftSpanOffset = i6;
                                        groupedMessagePosition6.leftSpanOffset = i6;
                                    }
                                    groupedMessagePosition4.siblingHeights = new float[]{f, min3, f6};
                                    r10.hasSibling = true;
                                    b3 = (byte) 1;
                                }
                                i4 = size;
                                b = b3;
                            } else {
                                i4 = size;
                                b = (byte) 0;
                            }
                            i5 = 0;
                        }
                    }
                    i4 = size;
                    i5 = 0;
                } else {
                    MessageGroupedLayoutAttempt messageGroupedLayoutAttempt;
                    int i14;
                    int i15;
                    int i16;
                    i3 = i9;
                    float[] fArr = new float[r10.posArray.size()];
                    for (max = 0; max < size; max++) {
                        if (f4 > 1.1f) {
                            fArr[max] = Math.max(1.0f, ((GroupedMessagePosition) r10.posArray.get(max)).aspectRatio);
                        } else {
                            fArr[max] = Math.min(1.0f, ((GroupedMessagePosition) r10.posArray.get(max)).aspectRatio);
                        }
                        fArr[max] = Math.max(0.66667f, Math.min(1.7f, fArr[max]));
                    }
                    ArrayList arrayList = new ArrayList();
                    int i17 = 1;
                    while (i17 < fArr.length) {
                        i7 = fArr.length - i17;
                        if (i17 <= i10) {
                            if (i7 <= i10) {
                                i5 = i11;
                                i5 = i10;
                                MessageGroupedLayoutAttempt messageGroupedLayoutAttempt2 = messageGroupedLayoutAttempt;
                                messageGroupedLayoutAttempt = new MessageGroupedLayoutAttempt(i17, i7, multiHeight(fArr, 0, i17), multiHeight(fArr, i17, fArr.length));
                                arrayList.add(messageGroupedLayoutAttempt2);
                                i17++;
                                i10 = i5;
                                i11 = 2;
                            }
                        }
                        i5 = i10;
                        i17++;
                        i10 = i5;
                        i11 = 2;
                    }
                    i5 = i10;
                    i2 = 1;
                    while (i2 < fArr.length - i) {
                        i17 = i;
                        while (i17 < fArr.length - i2) {
                            i10 = (fArr.length - i2) - i17;
                            if (i2 <= i5) {
                                if (i17 <= (f4 < 0.85f ? 4 : i5)) {
                                    if (i10 <= i5) {
                                        max = i2 + i17;
                                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt3 = messageGroupedLayoutAttempt;
                                        i14 = i17;
                                        i15 = i2;
                                        messageGroupedLayoutAttempt = new MessageGroupedLayoutAttempt(i2, i17, i10, multiHeight(fArr, 0, i2), multiHeight(fArr, i2, max), multiHeight(fArr, max, fArr.length));
                                        arrayList.add(messageGroupedLayoutAttempt3);
                                        i17 = i14 + 1;
                                        i2 = i15;
                                    }
                                }
                            }
                            i14 = i17;
                            i15 = i2;
                            i17 = i14 + 1;
                            i2 = i15;
                        }
                        i2++;
                        i = 1;
                    }
                    i = 1;
                    while (i < fArr.length - 2) {
                        i16 = 1;
                        while (i16 < fArr.length - i) {
                            i2 = 1;
                            while (i2 < (fArr.length - i) - i16) {
                                i8 = ((fArr.length - i) - i16) - i2;
                                if (i <= i5 && i16 <= i5 && i2 <= i5) {
                                    if (i8 <= i5) {
                                        max = i + i16;
                                        i11 = max + i2;
                                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt4 = messageGroupedLayoutAttempt;
                                        i14 = i2;
                                        i15 = i16;
                                        i4 = size;
                                        size = dp;
                                        messageGroupedLayoutAttempt = new MessageGroupedLayoutAttempt(i, i16, i2, i8, multiHeight(fArr, 0, i), multiHeight(fArr, i, max), multiHeight(fArr, max, i11), multiHeight(fArr, i11, fArr.length));
                                        arrayList.add(messageGroupedLayoutAttempt4);
                                        i2 = i14 + 1;
                                        dp = size;
                                        i16 = i15;
                                        size = i4;
                                        i5 = 3;
                                    }
                                }
                                i14 = i2;
                                i15 = i16;
                                i4 = size;
                                size = dp;
                                i2 = i14 + 1;
                                dp = size;
                                i16 = i15;
                                size = i4;
                                i5 = 3;
                            }
                            i4 = size;
                            size = dp;
                            i16++;
                            size = i4;
                            i5 = 3;
                        }
                        i4 = size;
                        size = dp;
                        i++;
                        size = i4;
                        i5 = 3;
                    }
                    i4 = size;
                    size = dp;
                    min = (float) ((r10.maxSizeWidth / 3) * 4);
                    MessageGroupedLayoutAttempt messageGroupedLayoutAttempt5 = null;
                    f5 = 0.0f;
                    for (i7 = 0; i7 < arrayList.size(); i7++) {
                        MessageGroupedLayoutAttempt messageGroupedLayoutAttempt6 = (MessageGroupedLayoutAttempt) arrayList.get(i7);
                        float f7 = Float.MAX_VALUE;
                        f4 = 0.0f;
                        for (i2 = 0; i2 < messageGroupedLayoutAttempt6.heights.length; i2++) {
                            f4 += messageGroupedLayoutAttempt6.heights[i2];
                            if (messageGroupedLayoutAttempt6.heights[i2] < f7) {
                                f7 = messageGroupedLayoutAttempt6.heights[i2];
                            }
                        }
                        f = Math.abs(f4 - min);
                        if (messageGroupedLayoutAttempt6.lineCounts.length > 1) {
                            if (messageGroupedLayoutAttempt6.lineCounts[0] <= messageGroupedLayoutAttempt6.lineCounts[1] && (messageGroupedLayoutAttempt6.lineCounts.length <= 2 || messageGroupedLayoutAttempt6.lineCounts[1] <= messageGroupedLayoutAttempt6.lineCounts[2])) {
                                if (messageGroupedLayoutAttempt6.lineCounts.length > 3 && messageGroupedLayoutAttempt6.lineCounts[2] > messageGroupedLayoutAttempt6.lineCounts[3]) {
                                }
                            }
                            f *= 1.2f;
                        }
                        if (f7 < ((float) size)) {
                            f *= 1.5f;
                        }
                        if (messageGroupedLayoutAttempt5 == null || f < r5) {
                            messageGroupedLayoutAttempt5 = messageGroupedLayoutAttempt6;
                            f5 = f;
                        }
                    }
                    i5 = 0;
                    if (messageGroupedLayoutAttempt5 != null) {
                        max = 0;
                        i11 = max;
                        b = i11;
                        while (max < messageGroupedLayoutAttempt5.lineCounts.length) {
                            i7 = messageGroupedLayoutAttempt5.lineCounts[max];
                            f5 = messageGroupedLayoutAttempt5.heights[max];
                            i17 = r10.maxSizeWidth;
                            i2 = i7 - 1;
                            b = Math.max(b, i2);
                            i16 = i17;
                            GroupedMessagePosition groupedMessagePosition7 = null;
                            i17 = i11;
                            for (i11 = 0; i11 < i7; i11++) {
                                int i18;
                                size = (int) (fArr[i17] * f5);
                                i16 -= size;
                                GroupedMessagePosition groupedMessagePosition8 = (GroupedMessagePosition) r10.posArray.get(i17);
                                i9 = max == 0 ? 4 : 0;
                                if (max == messageGroupedLayoutAttempt5.lineCounts.length - 1) {
                                    i9 |= 8;
                                }
                                if (i11 == 0) {
                                    i9 |= 1;
                                    if (i3 != 0) {
                                        groupedMessagePosition7 = groupedMessagePosition8;
                                    }
                                }
                                if (i11 == i2) {
                                    i6 = i9 | 2;
                                    if (i3 == 0) {
                                        i18 = i6;
                                        groupedMessagePosition7 = groupedMessagePosition8;
                                    } else {
                                        i18 = i6;
                                    }
                                } else {
                                    i18 = i9;
                                }
                                groupedMessagePosition8.set(i11, i11, max, max, size, f5 / 814.0f, i18);
                                i17++;
                            }
                            r9.pw += i16;
                            r9.spanSize += i16;
                            max++;
                            i11 = i17;
                        }
                    } else {
                        return;
                    }
                }
                max = i4;
                for (i5 = 
/*
Method generation error in method: org.telegram.messenger.MessageObject.GroupedMessages.calculate():void, dex: classes.dex
jadx.core.utils.exceptions.CodegenException: Error generate insn: PHI: (r13_20 'i5' int) = (r13_3 'i5' int), (r13_19 'i5' int) binds: {(r13_3 'i5' int)=B:98:0x0501, (r13_19 'i5' int)=B:280:0x076c} in method: org.telegram.messenger.MessageObject.GroupedMessages.calculate():void, dex: classes.dex
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
	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:187)
	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:320)
	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:257)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:220)
	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
	at jadx.core.codegen.ClassGen.addInnerClasses(ClassGen.java:233)
	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:219)
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
	... 25 more

*/
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

            public MessageObject(int i, Message message, String str, String str2, String str3, boolean z, boolean z2) {
                this.type = 1000;
                this.localType = z ? true : true;
                this.currentAccount = i;
                this.localName = str2;
                this.localUserName = str3;
                this.messageText = str;
                this.messageOwner = message;
                this.localChannel = z2;
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
                this(i, message, null, null, sparseArray, sparseArray2, z, 0);
            }

            public MessageObject(int i, Message message, AbstractMap<Integer, User> abstractMap, AbstractMap<Integer, Chat> abstractMap2, boolean z, long j) {
                this(i, message, abstractMap, abstractMap2, null, null, z, j);
            }

            public MessageObject(int i, Message message, AbstractMap<Integer, User> abstractMap, AbstractMap<Integer, Chat> abstractMap2, SparseArray<User> sparseArray, SparseArray<Chat> sparseArray2, boolean z, long j) {
                User user;
                int i2;
                int i3;
                boolean z2;
                String formatCallDuration;
                int indexOf;
                int length;
                int[] iArr;
                int indexOf2;
                Message message2 = message;
                AbstractMap<Integer, User> abstractMap3 = abstractMap;
                AbstractMap<Integer, Chat> abstractMap4 = abstractMap2;
                SparseArray<User> sparseArray3 = sparseArray;
                SparseArray<Chat> sparseArray4 = sparseArray2;
                boolean z3 = z;
                this.type = 1000;
                Theme.createChatResources(null, true);
                int i4 = i;
                this.currentAccount = i4;
                this.messageOwner = message2;
                this.eventId = j;
                if (message2.replyMessage != null) {
                    MessageObject messageObject = r7;
                    z3 = true;
                    MessageObject messageObject2 = new MessageObject(i4, message2.replyMessage, abstractMap3, abstractMap4, sparseArray3, sparseArray4, false, j);
                    r6.replyMessageObject = messageObject;
                } else {
                    z3 = true;
                }
                if (message2.from_id > 0) {
                    user = abstractMap3 != null ? (User) abstractMap3.get(Integer.valueOf(message2.from_id)) : sparseArray3 != null ? (User) sparseArray3.get(message2.from_id) : null;
                    if (user == null) {
                        user = MessagesController.getInstance(i).getUser(Integer.valueOf(message2.from_id));
                    }
                } else {
                    user = null;
                }
                if (message2 instanceof TL_messageService) {
                    if (message2.action != null) {
                        if (message2.action instanceof TL_messageActionCustomAction) {
                            r6.messageText = message2.action.message;
                        } else if (message2.action instanceof TL_messageActionChatCreate) {
                            if (isOut()) {
                                r6.messageText = LocaleController.getString("ActionYouCreateGroup", C0446R.string.ActionYouCreateGroup);
                            } else {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", C0446R.string.ActionCreateGroup), "un1", user);
                            }
                        } else if (!(message2.action instanceof TL_messageActionChatDeleteUser)) {
                            if (message2.action instanceof TL_messageActionChatAddUser) {
                                i2 = r6.messageOwner.action.user_id;
                                if (i2 == 0 && r6.messageOwner.action.users.size() == z3) {
                                    i2 = ((Integer) r6.messageOwner.action.users.get(0)).intValue();
                                }
                                if (i2 != 0) {
                                    TLObject tLObject = abstractMap3 != null ? (User) abstractMap3.get(Integer.valueOf(i2)) : sparseArray3 != null ? (User) sparseArray3.get(i2) : null;
                                    if (tLObject == null) {
                                        tLObject = MessagesController.getInstance(i).getUser(Integer.valueOf(i2));
                                    }
                                    if (i2 == message2.from_id) {
                                        if (message2.to_id.channel_id != 0 && !isMegagroup()) {
                                            r6.messageText = LocaleController.getString("ChannelJoined", C0446R.string.ChannelJoined);
                                        } else if (message2.to_id.channel_id == 0 || !isMegagroup()) {
                                            if (isOut()) {
                                                r6.messageText = LocaleController.getString("ActionAddUserSelfYou", C0446R.string.ActionAddUserSelfYou);
                                            } else {
                                                r6.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", C0446R.string.ActionAddUserSelf), "un1", user);
                                            }
                                        } else if (i2 == UserConfig.getInstance(r6.currentAccount).getClientUserId()) {
                                            r6.messageText = LocaleController.getString("ChannelMegaJoined", C0446R.string.ChannelMegaJoined);
                                        } else {
                                            r6.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", C0446R.string.ActionAddUserSelfMega), "un1", user);
                                        }
                                    } else if (isOut()) {
                                        r6.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", C0446R.string.ActionYouAddUser), "un2", tLObject);
                                    } else if (i2 != UserConfig.getInstance(r6.currentAccount).getClientUserId()) {
                                        r6.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", C0446R.string.ActionAddUser), "un2", tLObject);
                                        r6.messageText = replaceWithLink(r6.messageText, "un1", user);
                                    } else if (message2.to_id.channel_id == 0) {
                                        r6.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", C0446R.string.ActionAddUserYou), "un1", user);
                                    } else if (isMegagroup()) {
                                        r6.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", C0446R.string.MegaAddedBy), "un1", user);
                                    } else {
                                        r6.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", C0446R.string.ChannelAddedBy), "un1", user);
                                    }
                                    i3 = z3;
                                    z2 = z;
                                } else if (isOut()) {
                                    i3 = z3;
                                    z2 = z;
                                    r6.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", C0446R.string.ActionYouAddUser), "un2", message2.action.users, abstractMap3, sparseArray3);
                                } else {
                                    i3 = z3;
                                    z2 = z;
                                    r6.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", C0446R.string.ActionAddUser), "un2", message2.action.users, abstractMap3, sparseArray3);
                                    r6.messageText = replaceWithLink(r6.messageText, "un1", user);
                                }
                            } else {
                                i3 = z3;
                                z2 = z;
                                if (message2.action instanceof TL_messageActionChatJoinedByLink) {
                                    if (isOut()) {
                                        r6.messageText = LocaleController.getString("ActionInviteYou", C0446R.string.ActionInviteYou);
                                    } else {
                                        r6.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", C0446R.string.ActionInviteUser), "un1", user);
                                    }
                                } else if (message2.action instanceof TL_messageActionChatEditPhoto) {
                                    if (message2.to_id.channel_id != 0 && !isMegagroup()) {
                                        r6.messageText = LocaleController.getString("ActionChannelChangedPhoto", C0446R.string.ActionChannelChangedPhoto);
                                    } else if (isOut()) {
                                        r6.messageText = LocaleController.getString("ActionYouChangedPhoto", C0446R.string.ActionYouChangedPhoto);
                                    } else {
                                        r6.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", C0446R.string.ActionChangedPhoto), "un1", user);
                                    }
                                } else if (message2.action instanceof TL_messageActionChatEditTitle) {
                                    if (message2.to_id.channel_id != 0 && !isMegagroup()) {
                                        r6.messageText = LocaleController.getString("ActionChannelChangedTitle", C0446R.string.ActionChannelChangedTitle).replace("un2", message2.action.title);
                                    } else if (isOut()) {
                                        r6.messageText = LocaleController.getString("ActionYouChangedTitle", C0446R.string.ActionYouChangedTitle).replace("un2", message2.action.title);
                                    } else {
                                        r6.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", C0446R.string.ActionChangedTitle).replace("un2", message2.action.title), "un1", user);
                                    }
                                } else if (message2.action instanceof TL_messageActionChatDeletePhoto) {
                                    if (message2.to_id.channel_id != 0 && !isMegagroup()) {
                                        r6.messageText = LocaleController.getString("ActionChannelRemovedPhoto", C0446R.string.ActionChannelRemovedPhoto);
                                    } else if (isOut()) {
                                        r6.messageText = LocaleController.getString("ActionYouRemovedPhoto", C0446R.string.ActionYouRemovedPhoto);
                                    } else {
                                        r6.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", C0446R.string.ActionRemovedPhoto), "un1", user);
                                    }
                                } else if (message2.action instanceof TL_messageActionTTLChange) {
                                    if (message2.action.ttl != 0) {
                                        if (isOut()) {
                                            r2 = new Object[i3];
                                            r2[0] = LocaleController.formatTTLString(message2.action.ttl);
                                            r6.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", C0446R.string.MessageLifetimeChangedOutgoing, r2);
                                        } else {
                                            r6.messageText = LocaleController.formatString("MessageLifetimeChanged", C0446R.string.MessageLifetimeChanged, UserObject.getFirstName(user), LocaleController.formatTTLString(message2.action.ttl));
                                        }
                                    } else if (isOut()) {
                                        r6.messageText = LocaleController.getString("MessageLifetimeYouRemoved", C0446R.string.MessageLifetimeYouRemoved);
                                    } else {
                                        r1 = new Object[i3];
                                        r1[0] = UserObject.getFirstName(user);
                                        r6.messageText = LocaleController.formatString("MessageLifetimeRemoved", C0446R.string.MessageLifetimeRemoved, r1);
                                    }
                                } else if (message2.action instanceof TL_messageActionLoginUnknownLocation) {
                                    long j2 = ((long) message2.date) * 1000;
                                    if (LocaleController.getInstance().formatterDay == null || LocaleController.getInstance().formatterYear == null) {
                                        StringBuilder stringBuilder = new StringBuilder();
                                        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                        stringBuilder.append(message2.date);
                                        r1 = stringBuilder.toString();
                                    } else {
                                        r1 = LocaleController.formatString("formatDateAtTime", C0446R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(j2), LocaleController.getInstance().formatterDay.format(j2));
                                    }
                                    User currentUser = UserConfig.getInstance(r6.currentAccount).getCurrentUser();
                                    if (currentUser == null) {
                                        if (abstractMap3 != null) {
                                            currentUser = (User) abstractMap3.get(Integer.valueOf(r6.messageOwner.to_id.user_id));
                                        } else if (sparseArray3 != null) {
                                            currentUser = (User) sparseArray3.get(r6.messageOwner.to_id.user_id);
                                        }
                                        if (currentUser == null) {
                                            currentUser = MessagesController.getInstance(i).getUser(Integer.valueOf(r6.messageOwner.to_id.user_id));
                                        }
                                    }
                                    r2 = currentUser != null ? UserObject.getFirstName(currentUser) : TtmlNode.ANONYMOUS_REGION_ID;
                                    r6.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", C0446R.string.NotificationUnrecognizedDevice, r2, r1, message2.action.title, message2.action.address);
                                } else if (message2.action instanceof TL_messageActionUserJoined) {
                                    r2 = new Object[i3];
                                    r2[0] = UserObject.getUserName(user);
                                    r6.messageText = LocaleController.formatString("NotificationContactJoined", C0446R.string.NotificationContactJoined, r2);
                                } else if (message2.action instanceof TL_messageActionUserUpdatedPhoto) {
                                    r2 = new Object[i3];
                                    r2[0] = UserObject.getUserName(user);
                                    r6.messageText = LocaleController.formatString("NotificationContactNewPhoto", C0446R.string.NotificationContactNewPhoto, r2);
                                } else if (message2.action instanceof TL_messageEncryptedAction) {
                                    if (message2.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                                        if (isOut()) {
                                            r6.messageText = LocaleController.formatString("ActionTakeScreenshootYou", C0446R.string.ActionTakeScreenshootYou, new Object[0]);
                                        } else {
                                            r6.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", C0446R.string.ActionTakeScreenshoot), "un1", user);
                                        }
                                    } else if (message2.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                                        TL_decryptedMessageActionSetMessageTTL tL_decryptedMessageActionSetMessageTTL = (TL_decryptedMessageActionSetMessageTTL) message2.action.encryptedAction;
                                        if (tL_decryptedMessageActionSetMessageTTL.ttl_seconds != 0) {
                                            if (isOut()) {
                                                r2 = new Object[i3];
                                                r2[0] = LocaleController.formatTTLString(tL_decryptedMessageActionSetMessageTTL.ttl_seconds);
                                                r6.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", C0446R.string.MessageLifetimeChangedOutgoing, r2);
                                            } else {
                                                r6.messageText = LocaleController.formatString("MessageLifetimeChanged", C0446R.string.MessageLifetimeChanged, UserObject.getFirstName(user), LocaleController.formatTTLString(tL_decryptedMessageActionSetMessageTTL.ttl_seconds));
                                            }
                                        } else if (isOut()) {
                                            r6.messageText = LocaleController.getString("MessageLifetimeYouRemoved", C0446R.string.MessageLifetimeYouRemoved);
                                        } else {
                                            r1 = new Object[i3];
                                            r1[0] = UserObject.getFirstName(user);
                                            r6.messageText = LocaleController.formatString("MessageLifetimeRemoved", C0446R.string.MessageLifetimeRemoved, r1);
                                        }
                                    }
                                } else if (message2.action instanceof TL_messageActionScreenshotTaken) {
                                    if (isOut()) {
                                        r6.messageText = LocaleController.formatString("ActionTakeScreenshootYou", C0446R.string.ActionTakeScreenshootYou, new Object[0]);
                                    } else {
                                        r6.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", C0446R.string.ActionTakeScreenshoot), "un1", user);
                                    }
                                } else if (message2.action instanceof TL_messageActionCreatedBroadcastList) {
                                    r6.messageText = LocaleController.formatString("YouCreatedBroadcastList", C0446R.string.YouCreatedBroadcastList, new Object[0]);
                                } else if (message2.action instanceof TL_messageActionChannelCreate) {
                                    if (isMegagroup()) {
                                        r6.messageText = LocaleController.getString("ActionCreateMega", C0446R.string.ActionCreateMega);
                                    } else {
                                        r6.messageText = LocaleController.getString("ActionCreateChannel", C0446R.string.ActionCreateChannel);
                                    }
                                } else if (message2.action instanceof TL_messageActionChatMigrateTo) {
                                    r6.messageText = LocaleController.getString("ActionMigrateFromGroup", C0446R.string.ActionMigrateFromGroup);
                                } else if (message2.action instanceof TL_messageActionChannelMigrateFrom) {
                                    r6.messageText = LocaleController.getString("ActionMigrateFromGroup", C0446R.string.ActionMigrateFromGroup);
                                } else if (message2.action instanceof TL_messageActionPinMessage) {
                                    Chat chat;
                                    if (user == null) {
                                        if (abstractMap4 != null) {
                                            chat = (Chat) abstractMap4.get(Integer.valueOf(message2.to_id.channel_id));
                                        } else if (sparseArray4 != null) {
                                            chat = (Chat) sparseArray4.get(message2.to_id.channel_id);
                                        }
                                        generatePinMessageText(user, chat);
                                    }
                                    chat = null;
                                    generatePinMessageText(user, chat);
                                } else if (message2.action instanceof TL_messageActionHistoryClear) {
                                    r6.messageText = LocaleController.getString("HistoryCleared", C0446R.string.HistoryCleared);
                                } else if (message2.action instanceof TL_messageActionGameScore) {
                                    generateGameMessageText(user);
                                } else if (message2.action instanceof TL_messageActionPhoneCall) {
                                    TL_messageActionPhoneCall tL_messageActionPhoneCall = (TL_messageActionPhoneCall) r6.messageOwner.action;
                                    boolean z4 = tL_messageActionPhoneCall.reason instanceof TL_phoneCallDiscardReasonMissed;
                                    if (r6.messageOwner.from_id == UserConfig.getInstance(r6.currentAccount).getClientUserId()) {
                                        if (z4) {
                                            r6.messageText = LocaleController.getString("CallMessageOutgoingMissed", C0446R.string.CallMessageOutgoingMissed);
                                        } else {
                                            r6.messageText = LocaleController.getString("CallMessageOutgoing", C0446R.string.CallMessageOutgoing);
                                        }
                                    } else if (z4) {
                                        r6.messageText = LocaleController.getString("CallMessageIncomingMissed", C0446R.string.CallMessageIncomingMissed);
                                    } else if (tL_messageActionPhoneCall.reason instanceof TL_phoneCallDiscardReasonBusy) {
                                        r6.messageText = LocaleController.getString("CallMessageIncomingDeclined", C0446R.string.CallMessageIncomingDeclined);
                                    } else {
                                        r6.messageText = LocaleController.getString("CallMessageIncoming", C0446R.string.CallMessageIncoming);
                                    }
                                    if (tL_messageActionPhoneCall.duration > 0) {
                                        formatCallDuration = LocaleController.formatCallDuration(tL_messageActionPhoneCall.duration);
                                        r6.messageText = LocaleController.formatString("CallMessageWithDuration", C0446R.string.CallMessageWithDuration, r6.messageText, formatCallDuration);
                                        r1 = r6.messageText.toString();
                                        indexOf = r1.indexOf(formatCallDuration);
                                        if (indexOf != -1) {
                                            CharSequence spannableString = new SpannableString(r6.messageText);
                                            length = formatCallDuration.length() + indexOf;
                                            if (indexOf > 0 && r1.charAt(indexOf - 1) == '(') {
                                                indexOf--;
                                            }
                                            if (length < r1.length() && r1.charAt(length) == ')') {
                                                length++;
                                            }
                                            spannableString.setSpan(new TypefaceSpan(Typeface.DEFAULT), indexOf, length, 0);
                                            r6.messageText = spannableString;
                                        }
                                    }
                                } else if (message2.action instanceof TL_messageActionPaymentSent) {
                                    length = (int) getDialogId();
                                    if (abstractMap3 != null) {
                                        user = (User) abstractMap3.get(Integer.valueOf(length));
                                    } else if (sparseArray3 != null) {
                                        user = (User) sparseArray3.get(length);
                                    }
                                    if (user == null) {
                                        user = MessagesController.getInstance(i).getUser(Integer.valueOf(length));
                                    }
                                    iArr = null;
                                    generatePaymentSentMessageText(null);
                                } else {
                                    iArr = null;
                                    if (message2.action instanceof TL_messageActionBotAllowed) {
                                        formatCallDuration = ((TL_messageActionBotAllowed) message2.action).domain;
                                        r2 = LocaleController.getString("ActionBotAllowed", C0446R.string.ActionBotAllowed);
                                        indexOf2 = r2.indexOf("%1$s");
                                        Object[] objArr = new Object[i3];
                                        objArr[0] = formatCallDuration;
                                        CharSequence spannableString2 = new SpannableString(String.format(r2, objArr));
                                        if (indexOf2 >= 0) {
                                            StringBuilder stringBuilder2 = new StringBuilder();
                                            stringBuilder2.append("http://");
                                            stringBuilder2.append(formatCallDuration);
                                            spannableString2.setSpan(new URLSpanNoUnderlineBold(stringBuilder2.toString()), indexOf2, formatCallDuration.length() + indexOf2, 33);
                                        }
                                        r6.messageText = spannableString2;
                                    }
                                }
                            }
                            iArr = null;
                        } else if (message2.action.user_id != message2.from_id) {
                            TLObject tLObject2 = abstractMap3 != null ? (User) abstractMap3.get(Integer.valueOf(message2.action.user_id)) : sparseArray3 != null ? (User) sparseArray3.get(message2.action.user_id) : null;
                            if (tLObject2 == null) {
                                tLObject2 = MessagesController.getInstance(i).getUser(Integer.valueOf(message2.action.user_id));
                            }
                            if (isOut()) {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", C0446R.string.ActionYouKickUser), "un2", tLObject2);
                            } else if (message2.action.user_id == UserConfig.getInstance(r6.currentAccount).getClientUserId()) {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", C0446R.string.ActionKickUserYou), "un1", user);
                            } else {
                                r6.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", C0446R.string.ActionKickUser), "un2", tLObject2);
                                r6.messageText = replaceWithLink(r6.messageText, "un1", user);
                            }
                        } else if (isOut()) {
                            r6.messageText = LocaleController.getString("ActionYouLeftUser", C0446R.string.ActionYouLeftUser);
                        } else {
                            r6.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", C0446R.string.ActionLeftUser), "un1", user);
                        }
                    }
                    i3 = z3;
                    iArr = null;
                    z2 = z;
                } else {
                    i3 = z3;
                    iArr = null;
                    z2 = z;
                    if (isMediaEmpty()) {
                        r6.messageText = message2.message;
                    } else if (message2.media instanceof TL_messageMediaPhoto) {
                        r6.messageText = LocaleController.getString("AttachPhoto", C0446R.string.AttachPhoto);
                    } else {
                        if (!isVideo()) {
                            if (!(message2.media instanceof TL_messageMediaDocument) || !(message2.media.document instanceof TL_documentEmpty) || message2.media.ttl_seconds == 0) {
                                if (isVoice()) {
                                    r6.messageText = LocaleController.getString("AttachAudio", C0446R.string.AttachAudio);
                                } else if (isRoundVideo()) {
                                    r6.messageText = LocaleController.getString("AttachRound", C0446R.string.AttachRound);
                                } else {
                                    if (!(message2.media instanceof TL_messageMediaGeo)) {
                                        if (!(message2.media instanceof TL_messageMediaVenue)) {
                                            if (message2.media instanceof TL_messageMediaGeoLive) {
                                                r6.messageText = LocaleController.getString("AttachLiveLocation", C0446R.string.AttachLiveLocation);
                                            } else if (message2.media instanceof TL_messageMediaContact) {
                                                r6.messageText = LocaleController.getString("AttachContact", C0446R.string.AttachContact);
                                            } else if (message2.media instanceof TL_messageMediaGame) {
                                                r6.messageText = message2.message;
                                            } else if (message2.media instanceof TL_messageMediaInvoice) {
                                                r6.messageText = message2.media.description;
                                            } else if (message2.media instanceof TL_messageMediaUnsupported) {
                                                r6.messageText = LocaleController.getString("UnsupportedMedia", C0446R.string.UnsupportedMedia);
                                            } else if (message2.media instanceof TL_messageMediaDocument) {
                                                if (isSticker()) {
                                                    formatCallDuration = getStrickerChar();
                                                    if (formatCallDuration == null || formatCallDuration.length() <= 0) {
                                                        r6.messageText = LocaleController.getString("AttachSticker", C0446R.string.AttachSticker);
                                                    } else {
                                                        r6.messageText = String.format("%s %s", new Object[]{formatCallDuration, LocaleController.getString("AttachSticker", C0446R.string.AttachSticker)});
                                                    }
                                                } else if (isMusic()) {
                                                    r6.messageText = LocaleController.getString("AttachMusic", C0446R.string.AttachMusic);
                                                } else if (isGif()) {
                                                    r6.messageText = LocaleController.getString("AttachGif", C0446R.string.AttachGif);
                                                } else {
                                                    CharSequence documentFileName = FileLoader.getDocumentFileName(message2.media.document);
                                                    if (documentFileName == null || documentFileName.length() <= 0) {
                                                        r6.messageText = LocaleController.getString("AttachDocument", C0446R.string.AttachDocument);
                                                    } else {
                                                        r6.messageText = documentFileName;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    r6.messageText = LocaleController.getString("AttachLocation", C0446R.string.AttachLocation);
                                }
                            }
                        }
                        r6.messageText = LocaleController.getString("AttachVideo", C0446R.string.AttachVideo);
                    }
                }
                if (r6.messageText == null) {
                    r6.messageText = TtmlNode.ANONYMOUS_REGION_ID;
                }
                setType();
                measureInlineBotButtons();
                Calendar gregorianCalendar = new GregorianCalendar();
                gregorianCalendar.setTimeInMillis(((long) r6.messageOwner.date) * 1000);
                indexOf = gregorianCalendar.get(6);
                indexOf2 = gregorianCalendar.get(i3);
                length = gregorianCalendar.get(2);
                r6.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(indexOf2), Integer.valueOf(length), Integer.valueOf(indexOf)});
                r6.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(indexOf2), Integer.valueOf(length)});
                if (!(r6.messageOwner.message == null || r6.messageOwner.id >= 0 || r6.messageOwner.params == null)) {
                    formatCallDuration = (String) r6.messageOwner.params.get("ve");
                    if (formatCallDuration != null && (isVideo() || isNewGif() || isRoundVideo())) {
                        r6.videoEditedInfo = new VideoEditedInfo();
                        if (r6.videoEditedInfo.parseString(formatCallDuration)) {
                            r6.videoEditedInfo.roundVideo = isRoundVideo();
                        } else {
                            r6.videoEditedInfo = iArr;
                        }
                    }
                }
                generateCaption();
                if (z2) {
                    TextPaint textPaint;
                    if (r6.messageOwner.media instanceof TL_messageMediaGame) {
                        textPaint = Theme.chat_msgGameTextPaint;
                    } else {
                        textPaint = Theme.chat_msgTextPaint;
                    }
                    if (SharedConfig.allowBigEmoji) {
                        iArr = new int[i3];
                    }
                    r6.messageText = Emoji.replaceEmoji(r6.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr);
                    if (iArr != null && iArr[0] >= i3 && iArr[0] <= 3) {
                        switch (iArr[0]) {
                            case 1:
                                textPaint = Theme.chat_msgTextPaintOneEmoji;
                                i2 = AndroidUtilities.dp(32.0f);
                                break;
                            case 2:
                                textPaint = Theme.chat_msgTextPaintTwoEmoji;
                                i2 = AndroidUtilities.dp(28.0f);
                                break;
                            default:
                                textPaint = Theme.chat_msgTextPaintThreeEmoji;
                                i2 = AndroidUtilities.dp(24.0f);
                                break;
                        }
                        EmojiSpan[] emojiSpanArr = (EmojiSpan[]) ((Spannable) r6.messageText).getSpans(0, r6.messageText.length(), EmojiSpan.class);
                        if (emojiSpanArr != null && emojiSpanArr.length > 0) {
                            for (EmojiSpan replaceFontMetrics : emojiSpanArr) {
                                replaceFontMetrics.replaceFontMetrics(textPaint.getFontMetricsInt(), i2);
                            }
                        }
                    }
                    generateLayout(user);
                }
                r6.layoutCreated = z2;
                generateThumbs(false);
                checkMediaExistance();
            }

            private void createDateArray(int i, TL_channelAdminLogEvent tL_channelAdminLogEvent, ArrayList<MessageObject> arrayList, HashMap<String, ArrayList<MessageObject>> hashMap) {
                if (((ArrayList) hashMap.get(this.dateKey)) == null) {
                    hashMap.put(this.dateKey, new ArrayList());
                    hashMap = new TL_message();
                    hashMap.message = LocaleController.formatDateChat((long) tL_channelAdminLogEvent.date);
                    hashMap.id = 0;
                    hashMap.date = tL_channelAdminLogEvent.date;
                    tL_channelAdminLogEvent = new MessageObject(i, hashMap, false);
                    tL_channelAdminLogEvent.type = 10;
                    tL_channelAdminLogEvent.contentType = 1;
                    tL_channelAdminLogEvent.isDateObject = true;
                    arrayList.add(tL_channelAdminLogEvent);
                }
            }

            public MessageObject(int i, TL_channelAdminLogEvent tL_channelAdminLogEvent, ArrayList<MessageObject> arrayList, HashMap<String, ArrayList<MessageObject>> hashMap, Chat chat, int[] iArr) {
                int indexOf;
                Message tL_message;
                Message message;
                MediaController instance;
                Message message2;
                MessageObject messageObject;
                int[] iArr2;
                TextPaint textPaint;
                int i2;
                TextPaint textPaint2;
                int dp;
                EmojiSpan[] emojiSpanArr;
                TL_channelAdminLogEvent tL_channelAdminLogEvent2 = tL_channelAdminLogEvent;
                ArrayList<MessageObject> arrayList2 = arrayList;
                Chat chat2 = chat;
                this.type = 1000;
                TLObject user = tL_channelAdminLogEvent2.user_id > 0 ? MessagesController.getInstance(i).getUser(Integer.valueOf(tL_channelAdminLogEvent2.user_id)) : null;
                r0.currentEvent = tL_channelAdminLogEvent2;
                Calendar gregorianCalendar = new GregorianCalendar();
                gregorianCalendar.setTimeInMillis(((long) tL_channelAdminLogEvent2.date) * 1000);
                int i3 = gregorianCalendar.get(6);
                int i4 = gregorianCalendar.get(1);
                int i5 = gregorianCalendar.get(2);
                int i6 = 3;
                r15 = new Object[3];
                int i7 = 0;
                r15[0] = Integer.valueOf(i4);
                r15[1] = Integer.valueOf(i5);
                r15[2] = Integer.valueOf(i3);
                r0.dateKey = String.format("%d_%02d_%02d", r15);
                r0.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(i4), Integer.valueOf(i5)});
                Peer tL_peerChannel = new TL_peerChannel();
                tL_peerChannel.channel_id = chat2.id;
                if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionChangeTitle) {
                    String str = ((TL_channelAdminLogEventActionChangeTitle) tL_channelAdminLogEvent2.action).new_value;
                    if (chat2.megagroup) {
                        r0.messageText = replaceWithLink(LocaleController.formatString("EventLogEditedGroupTitle", C0446R.string.EventLogEditedGroupTitle, str), "un1", user);
                    } else {
                        r0.messageText = replaceWithLink(LocaleController.formatString("EventLogEditedChannelTitle", C0446R.string.EventLogEditedChannelTitle, str), "un1", user);
                    }
                } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionChangePhoto) {
                    r0.messageOwner = new TL_messageService();
                    if (tL_channelAdminLogEvent2.action.new_photo instanceof TL_chatPhotoEmpty) {
                        r0.messageOwner.action = new TL_messageActionChatDeletePhoto();
                        if (chat2.megagroup) {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogRemovedWGroupPhoto", C0446R.string.EventLogRemovedWGroupPhoto), "un1", user);
                        } else {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogRemovedChannelPhoto", C0446R.string.EventLogRemovedChannelPhoto), "un1", user);
                        }
                    } else {
                        r0.messageOwner.action = new TL_messageActionChatEditPhoto();
                        r0.messageOwner.action.photo = new TL_photo();
                        TL_photoSize tL_photoSize = new TL_photoSize();
                        tL_photoSize.location = tL_channelAdminLogEvent2.action.new_photo.photo_small;
                        tL_photoSize.type = "s";
                        tL_photoSize.h = 80;
                        tL_photoSize.w = 80;
                        r0.messageOwner.action.photo.sizes.add(tL_photoSize);
                        tL_photoSize = new TL_photoSize();
                        tL_photoSize.location = tL_channelAdminLogEvent2.action.new_photo.photo_big;
                        tL_photoSize.type = "m";
                        tL_photoSize.h = 640;
                        tL_photoSize.w = 640;
                        r0.messageOwner.action.photo.sizes.add(tL_photoSize);
                        if (chat2.megagroup) {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogEditedGroupPhoto", C0446R.string.EventLogEditedGroupPhoto), "un1", user);
                        } else {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogEditedChannelPhoto", C0446R.string.EventLogEditedChannelPhoto), "un1", user);
                        }
                    }
                } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionParticipantJoin) {
                    if (chat2.megagroup) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogGroupJoined", C0446R.string.EventLogGroupJoined), "un1", user);
                    } else {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogChannelJoined", C0446R.string.EventLogChannelJoined), "un1", user);
                    }
                } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionParticipantLeave) {
                    r0.messageOwner = new TL_messageService();
                    r0.messageOwner.action = new TL_messageActionChatDeleteUser();
                    r0.messageOwner.action.user_id = tL_channelAdminLogEvent2.user_id;
                    if (chat2.megagroup) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogLeftGroup", C0446R.string.EventLogLeftGroup), "un1", user);
                    } else {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogLeftChannel", C0446R.string.EventLogLeftChannel), "un1", user);
                    }
                } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionParticipantInvite) {
                    r0.messageOwner = new TL_messageService();
                    r0.messageOwner.action = new TL_messageActionChatAddUser();
                    TLObject user2 = MessagesController.getInstance(i).getUser(Integer.valueOf(tL_channelAdminLogEvent2.action.participant.user_id));
                    if (tL_channelAdminLogEvent2.action.participant.user_id != r0.messageOwner.from_id) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogAdded", C0446R.string.EventLogAdded), "un2", user2);
                        r0.messageText = replaceWithLink(r0.messageText, "un1", user);
                    } else if (chat2.megagroup) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogGroupJoined", C0446R.string.EventLogGroupJoined), "un1", user);
                    } else {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogChannelJoined", C0446R.string.EventLogChannelJoined), "un1", user);
                    }
                } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionParticipantToggleAdmin) {
                    r0.messageOwner = new TL_message();
                    r7 = MessagesController.getInstance(i).getUser(Integer.valueOf(tL_channelAdminLogEvent2.action.prev_participant.user_id));
                    String string = LocaleController.getString("EventLogPromoted", C0446R.string.EventLogPromoted);
                    r12 = string.indexOf("%1$s");
                    StringBuilder stringBuilder = new StringBuilder(String.format(string, new Object[]{getUserName(r7, r0.messageOwner.entities, r12)}));
                    stringBuilder.append("\n");
                    TL_channelAdminRights tL_channelAdminRights = tL_channelAdminLogEvent2.action.prev_participant.admin_rights;
                    TL_channelAdminRights tL_channelAdminRights2 = tL_channelAdminLogEvent2.action.new_participant.admin_rights;
                    if (tL_channelAdminRights == null) {
                        tL_channelAdminRights = new TL_channelAdminRights();
                    }
                    if (tL_channelAdminRights2 == null) {
                        tL_channelAdminRights2 = new TL_channelAdminRights();
                    }
                    if (tL_channelAdminRights.change_info != tL_channelAdminRights2.change_info) {
                        int i8;
                        stringBuilder.append('\n');
                        stringBuilder.append(tL_channelAdminRights2.change_info ? '+' : '-');
                        stringBuilder.append(' ');
                        if (chat2.megagroup) {
                            string = "EventLogPromotedChangeGroupInfo";
                            i8 = C0446R.string.EventLogPromotedChangeGroupInfo;
                        } else {
                            string = "EventLogPromotedChangeChannelInfo";
                            i8 = C0446R.string.EventLogPromotedChangeChannelInfo;
                        }
                        stringBuilder.append(LocaleController.getString(string, i8));
                    }
                    if (!chat2.megagroup) {
                        if (tL_channelAdminRights.post_messages != tL_channelAdminRights2.post_messages) {
                            stringBuilder.append('\n');
                            stringBuilder.append(tL_channelAdminRights2.post_messages ? '+' : '-');
                            stringBuilder.append(' ');
                            stringBuilder.append(LocaleController.getString("EventLogPromotedPostMessages", C0446R.string.EventLogPromotedPostMessages));
                        }
                        if (tL_channelAdminRights.edit_messages != tL_channelAdminRights2.edit_messages) {
                            stringBuilder.append('\n');
                            stringBuilder.append(tL_channelAdminRights2.edit_messages ? '+' : '-');
                            stringBuilder.append(' ');
                            stringBuilder.append(LocaleController.getString("EventLogPromotedEditMessages", C0446R.string.EventLogPromotedEditMessages));
                        }
                    }
                    if (tL_channelAdminRights.delete_messages != tL_channelAdminRights2.delete_messages) {
                        stringBuilder.append('\n');
                        stringBuilder.append(tL_channelAdminRights2.delete_messages ? '+' : '-');
                        stringBuilder.append(' ');
                        stringBuilder.append(LocaleController.getString("EventLogPromotedDeleteMessages", C0446R.string.EventLogPromotedDeleteMessages));
                    }
                    if (tL_channelAdminRights.add_admins != tL_channelAdminRights2.add_admins) {
                        stringBuilder.append('\n');
                        stringBuilder.append(tL_channelAdminRights2.add_admins ? '+' : '-');
                        stringBuilder.append(' ');
                        stringBuilder.append(LocaleController.getString("EventLogPromotedAddAdmins", C0446R.string.EventLogPromotedAddAdmins));
                    }
                    if (chat2.megagroup && tL_channelAdminRights.ban_users != tL_channelAdminRights2.ban_users) {
                        stringBuilder.append('\n');
                        stringBuilder.append(tL_channelAdminRights2.ban_users ? '+' : '-');
                        stringBuilder.append(' ');
                        stringBuilder.append(LocaleController.getString("EventLogPromotedBanUsers", C0446R.string.EventLogPromotedBanUsers));
                    }
                    if (tL_channelAdminRights.invite_users != tL_channelAdminRights2.invite_users) {
                        stringBuilder.append('\n');
                        stringBuilder.append(tL_channelAdminRights2.invite_users ? '+' : '-');
                        stringBuilder.append(' ');
                        stringBuilder.append(LocaleController.getString("EventLogPromotedAddUsers", C0446R.string.EventLogPromotedAddUsers));
                    }
                    if (chat2.megagroup && tL_channelAdminRights.pin_messages != tL_channelAdminRights2.pin_messages) {
                        stringBuilder.append('\n');
                        stringBuilder.append(tL_channelAdminRights2.pin_messages ? '+' : '-');
                        stringBuilder.append(' ');
                        stringBuilder.append(LocaleController.getString("EventLogPromotedPinMessages", C0446R.string.EventLogPromotedPinMessages));
                    }
                    r0.messageText = stringBuilder.toString();
                } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionParticipantToggleBan) {
                    r0.messageOwner = new TL_message();
                    r7 = MessagesController.getInstance(i).getUser(Integer.valueOf(tL_channelAdminLogEvent2.action.prev_participant.user_id));
                    TL_channelBannedRights tL_channelBannedRights = tL_channelAdminLogEvent2.action.prev_participant.banned_rights;
                    TL_channelBannedRights tL_channelBannedRights2 = tL_channelAdminLogEvent2.action.new_participant.banned_rights;
                    if (!chat2.megagroup || (tL_channelBannedRights2 != null && tL_channelBannedRights2.view_messages && (tL_channelBannedRights2 == null || tL_channelBannedRights == null || tL_channelBannedRights2.until_date == tL_channelBannedRights.until_date))) {
                        if (tL_channelBannedRights2 == null || !(tL_channelBannedRights == null || tL_channelBannedRights2.view_messages)) {
                            r6 = LocaleController.getString("EventLogChannelUnrestricted", C0446R.string.EventLogChannelUnrestricted);
                        } else {
                            r6 = LocaleController.getString("EventLogChannelRestricted", C0446R.string.EventLogChannelRestricted);
                        }
                        indexOf = r6.indexOf("%1$s");
                        r0.messageText = String.format(r6, new Object[]{getUserName(r7, r0.messageOwner.entities, indexOf)});
                    } else {
                        StringBuilder stringBuilder2;
                        Object obj;
                        char c;
                        if (tL_channelBannedRights2 != null && !AndroidUtilities.isBannedForever(tL_channelBannedRights2.until_date)) {
                            stringBuilder2 = new StringBuilder();
                            int i9 = tL_channelBannedRights2.until_date - tL_channelAdminLogEvent2.date;
                            i4 = ((i9 / 60) / 60) / 24;
                            i9 -= ((i4 * 60) * 60) * 24;
                            int i10 = (i9 / 60) / 60;
                            i9 = (i9 - ((i10 * 60) * 60)) / 60;
                            int i11 = 0;
                            while (i7 < i6) {
                                String formatPluralString;
                                int i12;
                                if (i7 == 0) {
                                    if (i4 != 0) {
                                        formatPluralString = LocaleController.formatPluralString("Days", i4);
                                        i11++;
                                    }
                                    r12 = i11;
                                    formatPluralString = null;
                                    if (formatPluralString == null) {
                                        if (stringBuilder2.length() <= 0) {
                                            i12 = i4;
                                            stringBuilder2.append(", ");
                                        } else {
                                            i12 = i4;
                                        }
                                        stringBuilder2.append(formatPluralString);
                                    } else {
                                        i12 = i4;
                                    }
                                    if (r12 != 2) {
                                        break;
                                    }
                                    i7++;
                                    i11 = r12;
                                    i4 = i12;
                                    i6 = 3;
                                } else if (i7 == 1) {
                                    if (i10 != 0) {
                                        formatPluralString = LocaleController.formatPluralString("Hours", i10);
                                        i11++;
                                    }
                                    r12 = i11;
                                    formatPluralString = null;
                                    if (formatPluralString == null) {
                                        i12 = i4;
                                    } else {
                                        if (stringBuilder2.length() <= 0) {
                                            i12 = i4;
                                        } else {
                                            i12 = i4;
                                            stringBuilder2.append(", ");
                                        }
                                        stringBuilder2.append(formatPluralString);
                                    }
                                    if (r12 != 2) {
                                        break;
                                    }
                                    i7++;
                                    i11 = r12;
                                    i4 = i12;
                                    i6 = 3;
                                } else {
                                    if (i9 != 0) {
                                        formatPluralString = LocaleController.formatPluralString("Minutes", i9);
                                        i11++;
                                    }
                                    r12 = i11;
                                    formatPluralString = null;
                                    if (formatPluralString == null) {
                                        if (stringBuilder2.length() <= 0) {
                                            i12 = i4;
                                            stringBuilder2.append(", ");
                                        } else {
                                            i12 = i4;
                                        }
                                        stringBuilder2.append(formatPluralString);
                                    } else {
                                        i12 = i4;
                                    }
                                    if (r12 != 2) {
                                        break;
                                    }
                                    i7++;
                                    i11 = r12;
                                    i4 = i12;
                                    i6 = 3;
                                }
                                r12 = i11;
                                if (formatPluralString == null) {
                                    i12 = i4;
                                } else {
                                    if (stringBuilder2.length() <= 0) {
                                        i12 = i4;
                                    } else {
                                        i12 = i4;
                                        stringBuilder2.append(", ");
                                    }
                                    stringBuilder2.append(formatPluralString);
                                }
                                if (r12 != 2) {
                                    break;
                                }
                                i7++;
                                i11 = r12;
                                i4 = i12;
                                i6 = 3;
                            }
                        } else {
                            stringBuilder2 = new StringBuilder(LocaleController.getString("UserRestrictionsUntilForever", C0446R.string.UserRestrictionsUntilForever));
                        }
                        r6 = LocaleController.getString("EventLogRestrictedUntil", C0446R.string.EventLogRestrictedUntil);
                        i4 = r6.indexOf("%1$s");
                        StringBuilder stringBuilder3 = new StringBuilder(String.format(r6, new Object[]{getUserName(r7, r0.messageOwner.entities, i4), stringBuilder2.toString()}));
                        if (tL_channelBannedRights == null) {
                            tL_channelBannedRights = new TL_channelBannedRights();
                        }
                        if (tL_channelBannedRights2 == null) {
                            tL_channelBannedRights2 = new TL_channelBannedRights();
                        }
                        if (tL_channelBannedRights.view_messages != tL_channelBannedRights2.view_messages) {
                            stringBuilder3.append('\n');
                            stringBuilder3.append('\n');
                            stringBuilder3.append(!tL_channelBannedRights2.view_messages ? '+' : '-');
                            stringBuilder3.append(' ');
                            stringBuilder3.append(LocaleController.getString("EventLogRestrictedReadMessages", C0446R.string.EventLogRestrictedReadMessages));
                            obj = 1;
                        } else {
                            obj = null;
                        }
                        if (tL_channelBannedRights.send_messages != tL_channelBannedRights2.send_messages) {
                            if (obj == null) {
                                c = '\n';
                                stringBuilder3.append('\n');
                                obj = 1;
                            } else {
                                c = '\n';
                            }
                            stringBuilder3.append(c);
                            stringBuilder3.append(!tL_channelBannedRights2.send_messages ? '+' : '-');
                            stringBuilder3.append(' ');
                            stringBuilder3.append(LocaleController.getString("EventLogRestrictedSendMessages", C0446R.string.EventLogRestrictedSendMessages));
                        }
                        if (!(tL_channelBannedRights.send_stickers == tL_channelBannedRights2.send_stickers && tL_channelBannedRights.send_inline == tL_channelBannedRights2.send_inline && tL_channelBannedRights.send_gifs == tL_channelBannedRights2.send_gifs && tL_channelBannedRights.send_games == tL_channelBannedRights2.send_games)) {
                            if (obj == null) {
                                c = '\n';
                                stringBuilder3.append('\n');
                                obj = 1;
                            } else {
                                c = '\n';
                            }
                            stringBuilder3.append(c);
                            stringBuilder3.append(!tL_channelBannedRights2.send_stickers ? '+' : '-');
                            stringBuilder3.append(' ');
                            stringBuilder3.append(LocaleController.getString("EventLogRestrictedSendStickers", C0446R.string.EventLogRestrictedSendStickers));
                        }
                        if (tL_channelBannedRights.send_media != tL_channelBannedRights2.send_media) {
                            if (obj == null) {
                                c = '\n';
                                stringBuilder3.append('\n');
                                obj = 1;
                            } else {
                                c = '\n';
                            }
                            stringBuilder3.append(c);
                            stringBuilder3.append(!tL_channelBannedRights2.send_media ? '+' : '-');
                            stringBuilder3.append(' ');
                            stringBuilder3.append(LocaleController.getString("EventLogRestrictedSendMedia", C0446R.string.EventLogRestrictedSendMedia));
                        }
                        if (tL_channelBannedRights.embed_links != tL_channelBannedRights2.embed_links) {
                            if (obj == null) {
                                c = '\n';
                                stringBuilder3.append('\n');
                            } else {
                                c = '\n';
                            }
                            stringBuilder3.append(c);
                            stringBuilder3.append(!tL_channelBannedRights2.embed_links ? '+' : '-');
                            stringBuilder3.append(' ');
                            stringBuilder3.append(LocaleController.getString("EventLogRestrictedSendEmbed", C0446R.string.EventLogRestrictedSendEmbed));
                        }
                        r0.messageText = stringBuilder3.toString();
                    }
                } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionUpdatePinned) {
                    if (tL_channelAdminLogEvent2.action.message instanceof TL_messageEmpty) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogUnpinnedMessages", C0446R.string.EventLogUnpinnedMessages), "un1", user);
                    } else {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogPinnedMessages", C0446R.string.EventLogPinnedMessages), "un1", user);
                    }
                } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionToggleSignatures) {
                    if (((TL_channelAdminLogEventActionToggleSignatures) tL_channelAdminLogEvent2.action).new_value) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledSignaturesOn", C0446R.string.EventLogToggledSignaturesOn), "un1", user);
                    } else {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledSignaturesOff", C0446R.string.EventLogToggledSignaturesOff), "un1", user);
                    }
                } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionToggleInvites) {
                    if (((TL_channelAdminLogEventActionToggleInvites) tL_channelAdminLogEvent2.action).new_value) {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesOn", C0446R.string.EventLogToggledInvitesOn), "un1", user);
                    } else {
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesOff", C0446R.string.EventLogToggledInvitesOff), "un1", user);
                    }
                } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionDeleteMessage) {
                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogDeletedMessages", C0446R.string.EventLogDeletedMessages), "un1", user);
                } else if (!(tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionTogglePreHistoryHidden)) {
                    if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionChangeAbout) {
                        if (chat2.megagroup) {
                            r6 = "EventLogEditedGroupDescription";
                            indexOf = C0446R.string.EventLogEditedGroupDescription;
                        } else {
                            r6 = "EventLogEditedChannelDescription";
                            indexOf = C0446R.string.EventLogEditedChannelDescription;
                        }
                        r0.messageText = replaceWithLink(LocaleController.getString(r6, indexOf), "un1", user);
                        tL_message = new TL_message();
                        tL_message.out = false;
                        tL_message.unread = false;
                        tL_message.from_id = tL_channelAdminLogEvent2.user_id;
                        tL_message.to_id = tL_peerChannel;
                        tL_message.date = tL_channelAdminLogEvent2.date;
                        tL_message.message = ((TL_channelAdminLogEventActionChangeAbout) tL_channelAdminLogEvent2.action).new_value;
                        if (TextUtils.isEmpty(((TL_channelAdminLogEventActionChangeAbout) tL_channelAdminLogEvent2.action).prev_value)) {
                            tL_message.media = new TL_messageMediaEmpty();
                        } else {
                            tL_message.media = new TL_messageMediaWebPage();
                            tL_message.media.webpage = new TL_webPage();
                            tL_message.media.webpage.flags = 10;
                            tL_message.media.webpage.display_url = TtmlNode.ANONYMOUS_REGION_ID;
                            tL_message.media.webpage.url = TtmlNode.ANONYMOUS_REGION_ID;
                            tL_message.media.webpage.site_name = LocaleController.getString("EventLogPreviousGroupDescription", C0446R.string.EventLogPreviousGroupDescription);
                            tL_message.media.webpage.description = ((TL_channelAdminLogEventActionChangeAbout) tL_channelAdminLogEvent2.action).prev_value;
                        }
                    } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionChangeUsername) {
                        StringBuilder stringBuilder4;
                        Object obj2 = ((TL_channelAdminLogEventActionChangeUsername) tL_channelAdminLogEvent2.action).new_value;
                        String str2;
                        if (TextUtils.isEmpty(obj2)) {
                            if (chat2.megagroup) {
                                str2 = "EventLogRemovedGroupLink";
                                i3 = C0446R.string.EventLogRemovedGroupLink;
                            } else {
                                str2 = "EventLogRemovedChannelLink";
                                i3 = C0446R.string.EventLogRemovedChannelLink;
                            }
                            r0.messageText = replaceWithLink(LocaleController.getString(str2, i3), "un1", user);
                        } else {
                            if (chat2.megagroup) {
                                str2 = "EventLogChangedGroupLink";
                                i3 = C0446R.string.EventLogChangedGroupLink;
                            } else {
                                str2 = "EventLogChangedChannelLink";
                                i3 = C0446R.string.EventLogChangedChannelLink;
                            }
                            r0.messageText = replaceWithLink(LocaleController.getString(str2, i3), "un1", user);
                        }
                        r8 = new TL_message();
                        r8.out = false;
                        r8.unread = false;
                        r8.from_id = tL_channelAdminLogEvent2.user_id;
                        r8.to_id = tL_peerChannel;
                        r8.date = tL_channelAdminLogEvent2.date;
                        if (TextUtils.isEmpty(obj2)) {
                            r8.message = TtmlNode.ANONYMOUS_REGION_ID;
                        } else {
                            stringBuilder4 = new StringBuilder();
                            stringBuilder4.append("https://");
                            stringBuilder4.append(MessagesController.getInstance(i).linkPrefix);
                            stringBuilder4.append("/");
                            stringBuilder4.append(obj2);
                            r8.message = stringBuilder4.toString();
                        }
                        TL_messageEntityUrl tL_messageEntityUrl = new TL_messageEntityUrl();
                        tL_messageEntityUrl.offset = 0;
                        tL_messageEntityUrl.length = r8.message.length();
                        r8.entities.add(tL_messageEntityUrl);
                        if (TextUtils.isEmpty(((TL_channelAdminLogEventActionChangeUsername) tL_channelAdminLogEvent2.action).prev_value)) {
                            r8.media = new TL_messageMediaEmpty();
                        } else {
                            r8.media = new TL_messageMediaWebPage();
                            r8.media.webpage = new TL_webPage();
                            r8.media.webpage.flags = 10;
                            r8.media.webpage.display_url = TtmlNode.ANONYMOUS_REGION_ID;
                            r8.media.webpage.url = TtmlNode.ANONYMOUS_REGION_ID;
                            r8.media.webpage.site_name = LocaleController.getString("EventLogPreviousLink", C0446R.string.EventLogPreviousLink);
                            WebPage webPage = r8.media.webpage;
                            stringBuilder4 = new StringBuilder();
                            stringBuilder4.append("https://");
                            stringBuilder4.append(MessagesController.getInstance(i).linkPrefix);
                            stringBuilder4.append("/");
                            stringBuilder4.append(((TL_channelAdminLogEventActionChangeUsername) tL_channelAdminLogEvent2.action).prev_value);
                            webPage.description = stringBuilder4.toString();
                        }
                        tL_message = r8;
                    } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionEditMessage) {
                        tL_message = new TL_message();
                        tL_message.out = false;
                        tL_message.unread = false;
                        tL_message.from_id = tL_channelAdminLogEvent2.user_id;
                        tL_message.to_id = tL_peerChannel;
                        tL_message.date = tL_channelAdminLogEvent2.date;
                        message = ((TL_channelAdminLogEventActionEditMessage) tL_channelAdminLogEvent2.action).new_message;
                        r8 = ((TL_channelAdminLogEventActionEditMessage) tL_channelAdminLogEvent2.action).prev_message;
                        if (message.media == null || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage) || !TextUtils.isEmpty(message.message)) {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogEditedMessages", C0446R.string.EventLogEditedMessages), "un1", user);
                            tL_message.message = message.message;
                            tL_message.media = new TL_messageMediaWebPage();
                            tL_message.media.webpage = new TL_webPage();
                            tL_message.media.webpage.site_name = LocaleController.getString("EventLogOriginalMessages", C0446R.string.EventLogOriginalMessages);
                            if (TextUtils.isEmpty(r8.message)) {
                                tL_message.media.webpage.description = LocaleController.getString("EventLogOriginalCaptionEmpty", C0446R.string.EventLogOriginalCaptionEmpty);
                            } else {
                                tL_message.media.webpage.description = r8.message;
                            }
                        } else {
                            r0.messageText = replaceWithLink(LocaleController.getString("EventLogEditedCaption", C0446R.string.EventLogEditedCaption), "un1", user);
                            tL_message.media = message.media;
                            tL_message.media.webpage = new TL_webPage();
                            tL_message.media.webpage.site_name = LocaleController.getString("EventLogOriginalCaption", C0446R.string.EventLogOriginalCaption);
                            if (TextUtils.isEmpty(r8.message)) {
                                tL_message.media.webpage.description = LocaleController.getString("EventLogOriginalCaptionEmpty", C0446R.string.EventLogOriginalCaptionEmpty);
                            } else {
                                tL_message.media.webpage.description = r8.message;
                            }
                        }
                        tL_message.reply_markup = message.reply_markup;
                        tL_message.media.webpage.flags = 10;
                        tL_message.media.webpage.display_url = TtmlNode.ANONYMOUS_REGION_ID;
                        tL_message.media.webpage.url = TtmlNode.ANONYMOUS_REGION_ID;
                    } else if (tL_channelAdminLogEvent2.action instanceof TL_channelAdminLogEventActionChangeStickerSet) {
                        InputStickerSet inputStickerSet = ((TL_channelAdminLogEventActionChangeStickerSet) tL_channelAdminLogEvent2.action).new_stickerset;
                        InputStickerSet inputStickerSet2 = ((TL_channelAdminLogEventActionChangeStickerSet) tL_channelAdminLogEvent2.action).new_stickerset;
                        if (inputStickerSet != null) {
                            if (!(inputStickerSet instanceof TL_inputStickerSetEmpty)) {
                                r0.messageText = replaceWithLink(LocaleController.getString("EventLogChangedStickersSet", C0446R.string.EventLogChangedStickersSet), "un1", user);
                            }
                        }
                        r0.messageText = replaceWithLink(LocaleController.getString("EventLogRemovedStickersSet", C0446R.string.EventLogRemovedStickersSet), "un1", user);
                    } else {
                        StringBuilder stringBuilder5 = new StringBuilder();
                        stringBuilder5.append("unsupported ");
                        stringBuilder5.append(tL_channelAdminLogEvent2.action);
                        r0.messageText = stringBuilder5.toString();
                    }
                    if (r0.messageOwner == null) {
                        r0.messageOwner = new TL_messageService();
                    }
                    r0.messageOwner.message = r0.messageText.toString();
                    r0.messageOwner.from_id = tL_channelAdminLogEvent2.user_id;
                    r0.messageOwner.date = tL_channelAdminLogEvent2.date;
                    message = r0.messageOwner;
                    i3 = iArr[0];
                    iArr[0] = i3 + 1;
                    message.id = i3;
                    r0.eventId = tL_channelAdminLogEvent2.id;
                    r0.messageOwner.out = false;
                    r0.messageOwner.to_id = new TL_peerChannel();
                    r0.messageOwner.to_id.channel_id = chat2.id;
                    r0.messageOwner.unread = false;
                    if (chat2.megagroup) {
                        message = r0.messageOwner;
                        message.flags |= Integer.MIN_VALUE;
                    }
                    instance = MediaController.getInstance();
                    if (!(tL_channelAdminLogEvent2.action.message == null || (tL_channelAdminLogEvent2.action.message instanceof TL_messageEmpty))) {
                        tL_message = tL_channelAdminLogEvent2.action.message;
                    }
                    message2 = tL_message;
                    if (message2 == null) {
                        message2.out = false;
                        indexOf = iArr[0];
                        iArr[0] = indexOf + 1;
                        message2.id = indexOf;
                        message2.reply_to_msg_id = 0;
                        message2.flags &= -32769;
                        if (chat2.megagroup) {
                            message2.flags |= Integer.MIN_VALUE;
                        }
                        messageObject = new MessageObject(i, message2, null, null, true, r0.eventId);
                        if (messageObject.contentType < 0) {
                            if (instance.isPlayingMessage(messageObject)) {
                                MessageObject playingMessageObject = instance.getPlayingMessageObject();
                                messageObject.audioProgress = playingMessageObject.audioProgress;
                                messageObject.audioProgressSec = playingMessageObject.audioProgressSec;
                            }
                            createDateArray(i, tL_channelAdminLogEvent, arrayList, hashMap);
                            i7 = 1;
                            arrayList2.add(arrayList.size() - 1, messageObject);
                        } else {
                            i7 = 1;
                            r0.contentType = -1;
                        }
                    } else {
                        i7 = 1;
                    }
                    if (r0.contentType >= 0) {
                        createDateArray(i, tL_channelAdminLogEvent, arrayList, hashMap);
                        arrayList2.add(arrayList.size() - i7, r0);
                        if (r0.messageText == null) {
                            r0.messageText = TtmlNode.ANONYMOUS_REGION_ID;
                        }
                        setType();
                        measureInlineBotButtons();
                        if (r0.messageOwner.message != null || r0.messageOwner.id >= 0 || r0.messageOwner.message.length() <= 6 || !(isVideo() || isNewGif() || isRoundVideo())) {
                            iArr2 = null;
                        } else {
                            r0.videoEditedInfo = new VideoEditedInfo();
                            if (r0.videoEditedInfo.parseString(r0.messageOwner.message)) {
                                iArr2 = null;
                                r0.videoEditedInfo.roundVideo = isRoundVideo();
                            } else {
                                iArr2 = null;
                                r0.videoEditedInfo = null;
                            }
                        }
                        generateCaption();
                        if (r0.messageOwner.media instanceof TL_messageMediaGame) {
                            textPaint = Theme.chat_msgTextPaint;
                        } else {
                            textPaint = Theme.chat_msgGameTextPaint;
                        }
                        if (SharedConfig.allowBigEmoji) {
                            i2 = 1;
                        } else {
                            i2 = 1;
                            iArr2 = new int[1];
                        }
                        r0.messageText = Emoji.replaceEmoji(r0.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr2);
                        if (iArr2 != null && iArr2[0] >= r3 && iArr2[0] <= 3) {
                            switch (iArr2[0]) {
                                case 1:
                                    textPaint2 = Theme.chat_msgTextPaintOneEmoji;
                                    dp = AndroidUtilities.dp(32.0f);
                                    break;
                                case 2:
                                    textPaint2 = Theme.chat_msgTextPaintTwoEmoji;
                                    dp = AndroidUtilities.dp(28.0f);
                                    break;
                                default:
                                    textPaint2 = Theme.chat_msgTextPaintThreeEmoji;
                                    dp = AndroidUtilities.dp(24.0f);
                                    break;
                            }
                            emojiSpanArr = (EmojiSpan[]) ((Spannable) r0.messageText).getSpans(0, r0.messageText.length(), EmojiSpan.class);
                            if (emojiSpanArr != null && emojiSpanArr.length > 0) {
                                for (EmojiSpan replaceFontMetrics : emojiSpanArr) {
                                    replaceFontMetrics.replaceFontMetrics(textPaint2.getFontMetricsInt(), dp);
                                }
                            }
                        }
                        if (instance.isPlayingMessage(r0)) {
                            MessageObject playingMessageObject2 = instance.getPlayingMessageObject();
                            r0.audioProgress = playingMessageObject2.audioProgress;
                            r0.audioProgressSec = playingMessageObject2.audioProgressSec;
                        }
                        generateLayout(user);
                        r0.layoutCreated = true;
                        generateThumbs(false);
                        checkMediaExistance();
                    }
                } else if (((TL_channelAdminLogEventActionTogglePreHistoryHidden) tL_channelAdminLogEvent2.action).new_value) {
                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesHistoryOff", C0446R.string.EventLogToggledInvitesHistoryOff), "un1", user);
                } else {
                    r0.messageText = replaceWithLink(LocaleController.getString("EventLogToggledInvitesHistoryOn", C0446R.string.EventLogToggledInvitesHistoryOn), "un1", user);
                }
                tL_message = null;
                if (r0.messageOwner == null) {
                    r0.messageOwner = new TL_messageService();
                }
                r0.messageOwner.message = r0.messageText.toString();
                r0.messageOwner.from_id = tL_channelAdminLogEvent2.user_id;
                r0.messageOwner.date = tL_channelAdminLogEvent2.date;
                message = r0.messageOwner;
                i3 = iArr[0];
                iArr[0] = i3 + 1;
                message.id = i3;
                r0.eventId = tL_channelAdminLogEvent2.id;
                r0.messageOwner.out = false;
                r0.messageOwner.to_id = new TL_peerChannel();
                r0.messageOwner.to_id.channel_id = chat2.id;
                r0.messageOwner.unread = false;
                if (chat2.megagroup) {
                    message = r0.messageOwner;
                    message.flags |= Integer.MIN_VALUE;
                }
                instance = MediaController.getInstance();
                tL_message = tL_channelAdminLogEvent2.action.message;
                message2 = tL_message;
                if (message2 == null) {
                    i7 = 1;
                } else {
                    message2.out = false;
                    indexOf = iArr[0];
                    iArr[0] = indexOf + 1;
                    message2.id = indexOf;
                    message2.reply_to_msg_id = 0;
                    message2.flags &= -32769;
                    if (chat2.megagroup) {
                        message2.flags |= Integer.MIN_VALUE;
                    }
                    messageObject = new MessageObject(i, message2, null, null, true, r0.eventId);
                    if (messageObject.contentType < 0) {
                        i7 = 1;
                        r0.contentType = -1;
                    } else {
                        if (instance.isPlayingMessage(messageObject)) {
                            MessageObject playingMessageObject3 = instance.getPlayingMessageObject();
                            messageObject.audioProgress = playingMessageObject3.audioProgress;
                            messageObject.audioProgressSec = playingMessageObject3.audioProgressSec;
                        }
                        createDateArray(i, tL_channelAdminLogEvent, arrayList, hashMap);
                        i7 = 1;
                        arrayList2.add(arrayList.size() - 1, messageObject);
                    }
                }
                if (r0.contentType >= 0) {
                    createDateArray(i, tL_channelAdminLogEvent, arrayList, hashMap);
                    arrayList2.add(arrayList.size() - i7, r0);
                    if (r0.messageText == null) {
                        r0.messageText = TtmlNode.ANONYMOUS_REGION_ID;
                    }
                    setType();
                    measureInlineBotButtons();
                    if (r0.messageOwner.message != null) {
                    }
                    iArr2 = null;
                    generateCaption();
                    if (r0.messageOwner.media instanceof TL_messageMediaGame) {
                        textPaint = Theme.chat_msgTextPaint;
                    } else {
                        textPaint = Theme.chat_msgGameTextPaint;
                    }
                    if (SharedConfig.allowBigEmoji) {
                        i2 = 1;
                    } else {
                        i2 = 1;
                        iArr2 = new int[1];
                    }
                    r0.messageText = Emoji.replaceEmoji(r0.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, iArr2);
                    switch (iArr2[0]) {
                        case 1:
                            textPaint2 = Theme.chat_msgTextPaintOneEmoji;
                            dp = AndroidUtilities.dp(32.0f);
                            break;
                        case 2:
                            textPaint2 = Theme.chat_msgTextPaintTwoEmoji;
                            dp = AndroidUtilities.dp(28.0f);
                            break;
                        default:
                            textPaint2 = Theme.chat_msgTextPaintThreeEmoji;
                            dp = AndroidUtilities.dp(24.0f);
                            break;
                    }
                    emojiSpanArr = (EmojiSpan[]) ((Spannable) r0.messageText).getSpans(0, r0.messageText.length(), EmojiSpan.class);
                    while (r4 < emojiSpanArr.length) {
                        replaceFontMetrics.replaceFontMetrics(textPaint2.getFontMetricsInt(), dp);
                    }
                    if (instance.isPlayingMessage(r0)) {
                        MessageObject playingMessageObject22 = instance.getPlayingMessageObject();
                        r0.audioProgress = playingMessageObject22.audioProgress;
                        r0.audioProgressSec = playingMessageObject22.audioProgressSec;
                    }
                    generateLayout(user);
                    r0.layoutCreated = true;
                    generateThumbs(false);
                    checkMediaExistance();
                }
            }

            private String getUserName(User user, ArrayList<MessageEntity> arrayList, int i) {
                String formatName = user == null ? TtmlNode.ANONYMOUS_REGION_ID : ContactsController.formatName(user.first_name, user.last_name);
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
                    User user = null;
                    if (isFromUser()) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                    }
                    this.messageText = this.messageOwner.message;
                    if (this.messageOwner.media instanceof TL_messageMediaGame) {
                        textPaint = Theme.chat_msgGameTextPaint;
                    } else {
                        textPaint = Theme.chat_msgTextPaint;
                    }
                    this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                    generateLayout(user);
                }
            }

            public void generateGameMessageText(User user) {
                if (user == null && this.messageOwner.from_id > 0) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                }
                TLObject tLObject = null;
                if (!(this.replyMessageObject == null || this.replyMessageObject.messageOwner.media == null || this.replyMessageObject.messageOwner.media.game == null)) {
                    tLObject = this.replyMessageObject.messageOwner.media.game;
                }
                if (tLObject != null) {
                    if (user == null || user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", C0446R.string.ActionUserScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", user);
                    } else {
                        this.messageText = LocaleController.formatString("ActionYouScoredInGame", C0446R.string.ActionYouScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
                    }
                    this.messageText = replaceWithLink(this.messageText, "un2", tLObject);
                } else if (user == null || user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", C0446R.string.ActionUserScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", user);
                } else {
                    this.messageText = LocaleController.formatString("ActionYouScored", C0446R.string.ActionYouScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
                }
            }

            public boolean hasValidReplyMessageObject() {
                return (this.replyMessageObject == null || (this.replyMessageObject.messageOwner instanceof TL_messageEmpty) || (this.replyMessageObject.messageOwner.action instanceof TL_messageActionHistoryClear)) ? false : true;
            }

            public void generatePaymentSentMessageText(User user) {
                if (user == null) {
                    user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf((int) getDialogId()));
                }
                user = user != null ? UserObject.getFirstName(user) : TtmlNode.ANONYMOUS_REGION_ID;
                if (this.replyMessageObject == null || !(this.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
                    this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", C0446R.string.PaymentSuccessfullyPaidNoItem, LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), user);
                } else {
                    this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", C0446R.string.PaymentSuccessfullyPaid, LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), user, this.replyMessageObject.messageOwner.media.title);
                }
            }

            public void generatePinMessageText(User user, Chat chat) {
                CharSequence string;
                String str;
                if (user == null && chat == null) {
                    if (this.messageOwner.from_id > 0) {
                        user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                    }
                    if (user == null) {
                        TLObject chat2 = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
                    }
                }
                if (!(this.replyMessageObject == null || (this.replyMessageObject.messageOwner instanceof TL_messageEmpty))) {
                    if (!(this.replyMessageObject.messageOwner.action instanceof TL_messageActionHistoryClear)) {
                        if (this.replyMessageObject.isMusic()) {
                            string = LocaleController.getString("ActionPinnedMusic", C0446R.string.ActionPinnedMusic);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.isVideo()) {
                            string = LocaleController.getString("ActionPinnedVideo", C0446R.string.ActionPinnedVideo);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.isGif()) {
                            string = LocaleController.getString("ActionPinnedGif", C0446R.string.ActionPinnedGif);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.isVoice()) {
                            string = LocaleController.getString("ActionPinnedVoice", C0446R.string.ActionPinnedVoice);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.isRoundVideo()) {
                            string = LocaleController.getString("ActionPinnedRound", C0446R.string.ActionPinnedRound);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.isSticker()) {
                            string = LocaleController.getString("ActionPinnedSticker", C0446R.string.ActionPinnedSticker);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaDocument) {
                            string = LocaleController.getString("ActionPinnedFile", C0446R.string.ActionPinnedFile);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGeo) {
                            string = LocaleController.getString("ActionPinnedGeo", C0446R.string.ActionPinnedGeo);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGeoLive) {
                            string = LocaleController.getString("ActionPinnedGeoLive", C0446R.string.ActionPinnedGeoLive);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaContact) {
                            string = LocaleController.getString("ActionPinnedContact", C0446R.string.ActionPinnedContact);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
                            string = LocaleController.getString("ActionPinnedPhoto", C0446R.string.ActionPinnedPhoto);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
                            Object[] objArr = new Object[1];
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("\ud83c\udfae ");
                            stringBuilder.append(this.replyMessageObject.messageOwner.media.game.title);
                            objArr[0] = stringBuilder.toString();
                            string = LocaleController.formatString("ActionPinnedGame", C0446R.string.ActionPinnedGame, objArr);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            this.messageText = Emoji.replaceEmoji(this.messageText, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                            return;
                        } else if (this.replyMessageObject.messageText == null || this.replyMessageObject.messageText.length() <= 0) {
                            string = LocaleController.getString("ActionPinnedNoText", C0446R.string.ActionPinnedNoText);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        } else {
                            string = this.replyMessageObject.messageText;
                            if (string.length() > 20) {
                                StringBuilder stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(string.subSequence(0, 20));
                                stringBuilder2.append("...");
                                string = stringBuilder2.toString();
                            }
                            string = Emoji.replaceEmoji(string, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                            string = LocaleController.formatString("ActionPinnedText", C0446R.string.ActionPinnedText, string);
                            str = "un1";
                            if (user == null) {
                                user = chat2;
                            }
                            this.messageText = replaceWithLink(string, str, user);
                            return;
                        }
                    }
                }
                string = LocaleController.getString("ActionPinnedNoText", C0446R.string.ActionPinnedNoText);
                str = "un1";
                if (user == null) {
                    user = chat2;
                }
                this.messageText = replaceWithLink(string, str, user);
            }

            private Photo getPhotoWithId(WebPage webPage, long j) {
                if (webPage != null) {
                    if (webPage.cached_page != null) {
                        if (webPage.photo != null && webPage.photo.id == j) {
                            return webPage.photo;
                        }
                        for (int i = 0; i < webPage.cached_page.photos.size(); i++) {
                            Photo photo = (Photo) webPage.cached_page.photos.get(i);
                            if (photo.id == j) {
                                return photo;
                            }
                        }
                        return null;
                    }
                }
                return null;
            }

            private Document getDocumentWithId(WebPage webPage, long j) {
                if (webPage != null) {
                    if (webPage.cached_page != null) {
                        if (webPage.document != null && webPage.document.id == j) {
                            return webPage.document;
                        }
                        for (int i = 0; i < webPage.cached_page.documents.size(); i++) {
                            Document document = (Document) webPage.cached_page.documents.get(i);
                            if (document.id == j) {
                                return document;
                            }
                        }
                        return null;
                    }
                }
                return null;
            }

            private MessageObject getMessageObjectForBlock(WebPage webPage, PageBlock pageBlock) {
                if (pageBlock instanceof TL_pageBlockPhoto) {
                    TLObject photoWithId = getPhotoWithId(webPage, pageBlock.photo_id);
                    if (photoWithId == webPage.photo) {
                        return this;
                    }
                    webPage = new TL_message();
                    webPage.media = new TL_messageMediaPhoto();
                    webPage.media.photo = photoWithId;
                } else if (!(pageBlock instanceof TL_pageBlockVideo)) {
                    webPage = null;
                } else if (getDocumentWithId(webPage, pageBlock.video_id) == webPage.document) {
                    return this;
                } else {
                    TLObject tL_message = new TL_message();
                    tL_message.media = new TL_messageMediaDocument();
                    tL_message.media.document = getDocumentWithId(webPage, pageBlock.video_id);
                    webPage = tL_message;
                }
                webPage.message = TtmlNode.ANONYMOUS_REGION_ID;
                webPage.id = Utilities.random.nextInt();
                webPage.date = this.messageOwner.date;
                webPage.to_id = this.messageOwner.to_id;
                webPage.out = this.messageOwner.out;
                webPage.from_id = this.messageOwner.from_id;
                return new MessageObject(this.currentAccount, webPage, false);
            }

            public ArrayList<MessageObject> getWebPagePhotos(ArrayList<MessageObject> arrayList, ArrayList<PageBlock> arrayList2) {
                WebPage webPage = this.messageOwner.media.webpage;
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                if (webPage.cached_page == null) {
                    return arrayList;
                }
                if (arrayList2 == null) {
                    arrayList2 = webPage.cached_page.blocks;
                }
                for (int i = 0; i < arrayList2.size(); i++) {
                    PageBlock pageBlock = (PageBlock) arrayList2.get(i);
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
                return arrayList;
            }

            public void measureInlineBotButtons() {
                this.wantedBotKeyboardWidth = 0;
                if (this.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
                    Theme.createChatResources(null, true);
                    if (this.botButtonsLayout == null) {
                        this.botButtonsLayout = new StringBuilder();
                    } else {
                        this.botButtonsLayout.setLength(0);
                    }
                    for (int i = 0; i < this.messageOwner.reply_markup.rows.size(); i++) {
                        TL_keyboardButtonRow tL_keyboardButtonRow = (TL_keyboardButtonRow) this.messageOwner.reply_markup.rows.get(i);
                        int size = tL_keyboardButtonRow.buttons.size();
                        int i2 = 0;
                        int i3 = i2;
                        while (i2 < size) {
                            String replaceEmoji;
                            KeyboardButton keyboardButton = (KeyboardButton) tL_keyboardButtonRow.buttons.get(i2);
                            StringBuilder stringBuilder = this.botButtonsLayout;
                            stringBuilder.append(i);
                            stringBuilder.append(i2);
                            if (!(keyboardButton instanceof TL_keyboardButtonBuy) || (this.messageOwner.media.flags & 4) == 0) {
                                replaceEmoji = Emoji.replaceEmoji(keyboardButton.text, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                            } else {
                                replaceEmoji = LocaleController.getString("PaymentReceipt", C0446R.string.PaymentReceipt);
                            }
                            StaticLayout staticLayout = new StaticLayout(replaceEmoji, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            if (staticLayout.getLineCount() > 0) {
                                float lineWidth = staticLayout.getLineWidth(0);
                                float lineLeft = staticLayout.getLineLeft(0);
                                if (lineLeft < lineWidth) {
                                    lineWidth -= lineLeft;
                                }
                                i3 = Math.max(i3, ((int) Math.ceil((double) lineWidth)) + AndroidUtilities.dp(4.0f));
                            }
                            i2++;
                        }
                        this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((i3 + AndroidUtilities.dp(12.0f)) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
                    }
                }
            }

            public boolean isFcmMessage() {
                return this.localType != 0;
            }

            public void setType() {
                int i = this.type;
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
                        if (i != 1000 && i != this.type) {
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
                if (i != 1000) {
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
                        TextPaint textPaint;
                        this.layoutCreated = true;
                        User user = null;
                        if (isFromUser()) {
                            user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                        }
                        if (this.messageOwner.media instanceof TL_messageMediaGame) {
                            textPaint = Theme.chat_msgGameTextPaint;
                        } else {
                            textPaint = Theme.chat_msgTextPaint;
                        }
                        this.messageText = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                        generateLayout(user);
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
                    WebDocument webDocument = ((TL_messageMediaInvoice) this.messageOwner.media).photo;
                    if (webDocument != null) {
                        return webDocument.mime_type;
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

            public static boolean isGifDocument(TL_webDocument tL_webDocument) {
                return (tL_webDocument == null || (!tL_webDocument.mime_type.equals("image/gif") && isNewGifDocument(tL_webDocument) == null)) ? null : true;
            }

            public static boolean isGifDocument(Document document) {
                return (document == null || document.thumb == null || document.mime_type == null || (!document.mime_type.equals("image/gif") && isNewGifDocument(document) == null)) ? null : true;
            }

            public static boolean isRoundVideoDocument(Document document) {
                if (!(document == null || document.mime_type == null || !document.mime_type.equals(MimeTypes.VIDEO_MP4))) {
                    int i = 0;
                    int i2 = i;
                    int i3 = i2;
                    int i4 = i3;
                    while (i < document.attributes.size()) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                        if (documentAttribute instanceof TL_documentAttributeVideo) {
                            i2 = documentAttribute.f36w;
                            int i5 = documentAttribute.f36w;
                            i3 = i2;
                            i2 = documentAttribute.round_message;
                            i4 = i5;
                        }
                        i++;
                    }
                    if (i2 != 0 && r3 <= 1280 && r4 <= 1280) {
                        return true;
                    }
                }
                return false;
            }

            public static boolean isNewGifDocument(TL_webDocument tL_webDocument) {
                if (!(tL_webDocument == null || tL_webDocument.mime_type == null || !tL_webDocument.mime_type.equals(MimeTypes.VIDEO_MP4))) {
                    int i = 0;
                    int i2 = i;
                    int i3 = i2;
                    while (i < tL_webDocument.attributes.size()) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) tL_webDocument.attributes.get(i);
                        if (!(documentAttribute instanceof TL_documentAttributeAnimated)) {
                            if (documentAttribute instanceof TL_documentAttributeVideo) {
                                i2 = documentAttribute.f36w;
                                i3 = documentAttribute.f36w;
                            }
                        }
                        i++;
                    }
                    if (i2 <= 1280 && r3 <= 1280) {
                        return true;
                    }
                }
                return false;
            }

            public static boolean isNewGifDocument(Document document) {
                if (!(document == null || document.mime_type == null || !document.mime_type.equals(MimeTypes.VIDEO_MP4))) {
                    int i = 0;
                    int i2 = i;
                    int i3 = i2;
                    int i4 = i3;
                    while (i < document.attributes.size()) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                        if (documentAttribute instanceof TL_documentAttributeAnimated) {
                            i2 = true;
                        } else if (documentAttribute instanceof TL_documentAttributeVideo) {
                            i3 = documentAttribute.f36w;
                            i4 = documentAttribute.f36w;
                        }
                        i++;
                    }
                    if (i2 == 0 || r3 > 1280 || r4 > 1280) {
                        return false;
                    }
                    return true;
                }
                return false;
            }

            public void generateThumbs(boolean z) {
                PhotoSize photoSize;
                int i;
                PhotoSize photoSize2;
                if (this.messageOwner instanceof TL_messageService) {
                    if (!(this.messageOwner.action instanceof TL_messageActionChatEditPhoto)) {
                        return;
                    }
                    if (!z) {
                        this.photoThumbs = new ArrayList(this.messageOwner.action.photo.sizes);
                    } else if (this.photoThumbs && !this.photoThumbs.isEmpty()) {
                        for (z = false; z < this.photoThumbs.size(); z++) {
                            photoSize = (PhotoSize) this.photoThumbs.get(z);
                            for (i = 0; i < this.messageOwner.action.photo.sizes.size(); i++) {
                                photoSize2 = (PhotoSize) this.messageOwner.action.photo.sizes.get(i);
                                if (!(photoSize2 instanceof TL_photoSizeEmpty)) {
                                    if (photoSize2.type.equals(photoSize.type)) {
                                        photoSize.location = photoSize2.location;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                } else if (this.messageOwner.media != null && !(this.messageOwner.media instanceof TL_messageMediaEmpty)) {
                    if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
                        if (z) {
                            if (!this.photoThumbs || this.photoThumbs.size() == this.messageOwner.media.photo.sizes.size()) {
                                if (this.photoThumbs && !this.photoThumbs.isEmpty()) {
                                    for (z = false; z < this.photoThumbs.size(); z++) {
                                        photoSize = (PhotoSize) this.photoThumbs.get(z);
                                        for (i = 0; i < this.messageOwner.media.photo.sizes.size(); i++) {
                                            photoSize2 = (PhotoSize) this.messageOwner.media.photo.sizes.get(i);
                                            if (!(photoSize2 instanceof TL_photoSizeEmpty)) {
                                                if (photoSize2.type.equals(photoSize.type)) {
                                                    photoSize.location = photoSize2.location;
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
                            if (!z) {
                                this.photoThumbs = new ArrayList();
                                this.photoThumbs.add(this.messageOwner.media.document.thumb);
                            } else if (this.photoThumbs && !this.photoThumbs.isEmpty() && this.messageOwner.media.document.thumb) {
                                PhotoSize photoSize3 = (PhotoSize) this.photoThumbs.get(0);
                                photoSize3.location = this.messageOwner.media.document.thumb.location;
                                photoSize3.f43w = this.messageOwner.media.document.thumb.f43w;
                                photoSize3.f42h = this.messageOwner.media.document.thumb.f42h;
                            }
                        }
                    } else if (this.messageOwner.media instanceof TL_messageMediaGame) {
                        if (!(this.messageOwner.media.game.document == null || (this.messageOwner.media.game.document.thumb instanceof TL_photoSizeEmpty))) {
                            if (!z) {
                                this.photoThumbs = new ArrayList();
                                this.photoThumbs.add(this.messageOwner.media.game.document.thumb);
                            } else if (!(this.photoThumbs == null || this.photoThumbs.isEmpty() || this.messageOwner.media.game.document.thumb == null)) {
                                ((PhotoSize) this.photoThumbs.get(0)).location = this.messageOwner.media.game.document.thumb.location;
                            }
                        }
                        if (this.messageOwner.media.game.photo != null) {
                            if (z) {
                                if (this.photoThumbs2) {
                                    if (!this.photoThumbs2.isEmpty()) {
                                        for (z = false; z < this.photoThumbs2.size(); z++) {
                                            photoSize = (PhotoSize) this.photoThumbs2.get(z);
                                            for (i = 0; i < this.messageOwner.media.game.photo.sizes.size(); i++) {
                                                photoSize2 = (PhotoSize) this.messageOwner.media.game.photo.sizes.get(i);
                                                if (!(photoSize2 instanceof TL_photoSizeEmpty)) {
                                                    if (photoSize2.type.equals(photoSize.type)) {
                                                        photoSize.location = photoSize2.location;
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
                        if (!this.photoThumbs && this.photoThumbs2) {
                            this.photoThumbs = this.photoThumbs2;
                            this.photoThumbs2 = false;
                        }
                    } else if (!(this.messageOwner.media instanceof TL_messageMediaWebPage)) {
                    } else {
                        if (this.messageOwner.media.webpage.photo != null) {
                            if (z) {
                                if (this.photoThumbs) {
                                    if (!this.photoThumbs.isEmpty()) {
                                        for (z = false; z < this.photoThumbs.size(); z++) {
                                            photoSize = (PhotoSize) this.photoThumbs.get(z);
                                            for (i = 0; i < this.messageOwner.media.webpage.photo.sizes.size(); i++) {
                                                photoSize2 = (PhotoSize) this.messageOwner.media.webpage.photo.sizes.get(i);
                                                if (!(photoSize2 instanceof TL_photoSizeEmpty)) {
                                                    if (photoSize2.type.equals(photoSize.type)) {
                                                        photoSize.location = photoSize2.location;
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
                            if (!z) {
                                this.photoThumbs = new ArrayList();
                                this.photoThumbs.add(this.messageOwner.media.webpage.document.thumb);
                            } else if (this.photoThumbs && !this.photoThumbs.isEmpty() && this.messageOwner.media.webpage.document.thumb) {
                                ((PhotoSize) this.photoThumbs.get(0)).location = this.messageOwner.media.webpage.document.thumb.location;
                            }
                        }
                    }
                }
            }

            public CharSequence replaceWithLink(CharSequence charSequence, String str, ArrayList<Integer> arrayList, AbstractMap<Integer, User> abstractMap, SparseArray<User> sparseArray) {
                if (TextUtils.indexOf(charSequence, str) < 0) {
                    return charSequence;
                }
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(TtmlNode.ANONYMOUS_REGION_ID);
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
                        Object userName = UserObject.getUserName(user);
                        int length = spannableStringBuilder.length();
                        if (spannableStringBuilder.length() != 0) {
                            spannableStringBuilder.append(", ");
                        }
                        spannableStringBuilder.append(userName);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
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
                StringBuilder stringBuilder;
                if (tLObject instanceof User) {
                    User user = (User) tLObject;
                    userName = UserObject.getUserName(user);
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder.append(user.id);
                    tLObject = stringBuilder.toString();
                } else if (tLObject instanceof Chat) {
                    Chat chat = (Chat) tLObject;
                    userName = chat.title;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                    stringBuilder.append(-chat.id);
                    tLObject = stringBuilder.toString();
                } else if (tLObject instanceof TL_game) {
                    userName = ((TL_game) tLObject).title;
                    tLObject = "game";
                } else {
                    userName = TtmlNode.ANONYMOUS_REGION_ID;
                    tLObject = "0";
                }
                CharSequence spannableStringBuilder = new SpannableStringBuilder(TextUtils.replace(charSequence, new String[]{str}, new String[]{userName.replace('\n', ' ')}));
                str = new StringBuilder();
                str.append(TtmlNode.ANONYMOUS_REGION_ID);
                str.append(tLObject);
                spannableStringBuilder.setSpan(new URLSpanNoUnderlineBold(str.toString()), indexOf, userName.length() + indexOf, 33);
                return spannableStringBuilder;
            }

            public String getExtension() {
                String fileName = getFileName();
                int lastIndexOf = fileName.lastIndexOf(46);
                fileName = lastIndexOf != -1 ? fileName.substring(lastIndexOf + 1) : null;
                if (fileName == null || fileName.length() == 0) {
                    fileName = this.messageOwner.media.document.mime_type;
                }
                if (fileName == null) {
                    fileName = TtmlNode.ANONYMOUS_REGION_ID;
                }
                return fileName.toUpperCase();
            }

            public String getFileName() {
                if (this.messageOwner.media instanceof TL_messageMediaDocument) {
                    return FileLoader.getAttachFileName(this.messageOwner.media.document);
                }
                if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
                    ArrayList arrayList = this.messageOwner.media.photo.sizes;
                    if (arrayList.size() > 0) {
                        TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
                        if (closestPhotoSizeWithSize != null) {
                            return FileLoader.getAttachFileName(closestPhotoSizeWithSize);
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
                return this.messageOwner.media instanceof TL_messageMediaPhoto ? 0 : 4;
            }

            private static boolean containsUrls(CharSequence charSequence) {
                if (charSequence != null && charSequence.length() >= 2) {
                    if (charSequence.length() <= CacheDataSink.DEFAULT_BUFFER_SIZE) {
                        int length = charSequence.length();
                        int i = 0;
                        int i2 = i;
                        int i3 = i2;
                        int i4 = i3;
                        int i5 = i4;
                        while (i < length) {
                            char charAt = charSequence.charAt(i);
                            if (charAt >= '0' && charAt <= '9') {
                                i2++;
                                if (i2 >= 6) {
                                    return true;
                                }
                                i3 = 0;
                                i4 = i3;
                            } else if (charAt == ' ' || i2 <= 0) {
                                i2 = 0;
                            }
                            if ((charAt != '@' && charAt != '#' && charAt != '/' && charAt != '$') || i != 0) {
                                char c;
                                if (i != 0) {
                                    int i6 = i - 1;
                                    if (charSequence.charAt(i6) != ' ') {
                                        if (charSequence.charAt(i6) == '\n') {
                                        }
                                    }
                                }
                                if (charAt != ':') {
                                    if (charAt != '/') {
                                        if (charAt == '.') {
                                            if (i4 == 0 && r7 != 32) {
                                                i4++;
                                            }
                                        } else if (charAt != ' ' && r7 == 46 && i4 == 1) {
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
                            int i;
                            boolean z = false;
                            this.caption = Emoji.replaceEmoji(this.messageOwner.message, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
                            if (this.messageOwner.send_state != 0) {
                                for (i = 0; i < this.messageOwner.entities.size(); i++) {
                                    if (!(this.messageOwner.entities.get(i) instanceof TL_inputMessageEntityMentionName)) {
                                        i = 1;
                                        break;
                                    }
                                }
                                i = 0;
                            } else {
                                i = this.messageOwner.entities.isEmpty() ^ 1;
                            }
                            if (i == 0 && (this.eventId != 0 || (this.messageOwner.media instanceof TL_messageMediaPhoto_old) || (this.messageOwner.media instanceof TL_messageMediaPhoto_layer68) || (this.messageOwner.media instanceof TL_messageMediaPhoto_layer74) || (this.messageOwner.media instanceof TL_messageMediaDocument_old) || (this.messageOwner.media instanceof TL_messageMediaDocument_layer68) || (this.messageOwner.media instanceof TL_messageMediaDocument_layer74) || ((isOut() && this.messageOwner.send_state != 0) || this.messageOwner.id < 0))) {
                                z = true;
                            }
                            if (z) {
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
                            addEntitiesToText(this.caption, z);
                        }
                    }
                }
            }

            private static void addUsernamesAndHashtags(boolean z, CharSequence charSequence, boolean z2) {
                try {
                    if (urlPattern == null) {
                        urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s)@[a-zA-Z\\d_]{1,32}|(^|\\s)#[\\w\\.]+|(^|\\s)\\$[A-Z]{3,8}([ ,.]|$)");
                    }
                    Matcher matcher = urlPattern.matcher(charSequence);
                    while (matcher.find()) {
                        int start = matcher.start();
                        int end = matcher.end();
                        char charAt = charSequence.charAt(start);
                        if (!(charAt == '@' || charAt == '#' || charAt == '/' || charAt == '$')) {
                            start++;
                        }
                        Object obj = null;
                        if (charSequence.charAt(start) != '/') {
                            obj = new URLSpanNoUnderline(charSequence.subSequence(start, end).toString());
                        } else if (z2) {
                            obj = new URLSpanBotCommand(charSequence.subSequence(start, end).toString(), z);
                        }
                        if (obj != null) {
                            ((Spannable) charSequence).setSpan(obj, start, end, 0);
                        }
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
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
                        return new int[]{documentAttribute.f36w, documentAttribute.f35h};
                    } else if (documentAttribute instanceof TL_documentAttributeVideo) {
                        return new int[]{documentAttribute.f36w, documentAttribute.f35h};
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
                return (getGroupId() == 0 || this.photoThumbs == null || this.photoThumbs.isEmpty()) ? false : true;
            }

            public long getGroupId() {
                return this.localGroupId != 0 ? this.localGroupId : this.messageOwner.grouped_id;
            }

            public static void addLinks(boolean z, CharSequence charSequence) {
                addLinks(z, charSequence, true);
            }

            public static void addLinks(boolean z, CharSequence charSequence, boolean z2) {
                if ((charSequence instanceof Spannable) && containsUrls(charSequence)) {
                    if (charSequence.length() < 1000) {
                        try {
                            Linkify.addLinks((Spannable) charSequence, 5);
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    } else {
                        try {
                            Linkify.addLinks((Spannable) charSequence, 1);
                        } catch (Throwable e2) {
                            FileLog.m3e(e2);
                        }
                    }
                    addUsernamesAndHashtags(z, charSequence, z2);
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
                MessageObject messageObject = this;
                CharSequence charSequence2 = charSequence;
                boolean z3 = false;
                if (!(charSequence2 instanceof Spannable)) {
                    return false;
                }
                Spannable spannable = (Spannable) charSequence2;
                int size = messageObject.messageOwner.entities.size();
                URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(0, charSequence.length(), URLSpan.class);
                boolean z4 = uRLSpanArr != null && uRLSpanArr.length > 0;
                boolean z5 = z4;
                int i = 0;
                while (i < size) {
                    MessageEntity messageEntity = (MessageEntity) messageObject.messageOwner.entities.get(i);
                    if (messageEntity.length > 0 && messageEntity.offset >= 0) {
                        if (messageEntity.offset < charSequence.length()) {
                            if (messageEntity.offset + messageEntity.length > charSequence.length()) {
                                messageEntity.length = charSequence.length() - messageEntity.offset;
                            }
                            if ((!z2 || (messageEntity instanceof TL_messageEntityBold) || (messageEntity instanceof TL_messageEntityItalic) || (messageEntity instanceof TL_messageEntityCode) || (messageEntity instanceof TL_messageEntityPre) || (messageEntity instanceof TL_messageEntityMentionName) || (messageEntity instanceof TL_inputMessageEntityMentionName)) && uRLSpanArr != null && uRLSpanArr.length > 0) {
                                for (int i2 = z3; i2 < uRLSpanArr.length; i2++) {
                                    if (uRLSpanArr[i2] != null) {
                                        int spanStart = spannable.getSpanStart(uRLSpanArr[i2]);
                                        int spanEnd = spannable.getSpanEnd(uRLSpanArr[i2]);
                                        if ((messageEntity.offset <= spanStart && messageEntity.offset + messageEntity.length >= spanStart) || (messageEntity.offset <= spanEnd && messageEntity.offset + messageEntity.length >= spanEnd)) {
                                            spannable.removeSpan(uRLSpanArr[i2]);
                                            uRLSpanArr[i2] = null;
                                        }
                                    }
                                }
                            }
                            if (messageEntity instanceof TL_messageEntityBold) {
                                spannable.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf")), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                            } else if (messageEntity instanceof TL_messageEntityItalic) {
                                spannable.setSpan(new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf")), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                            } else {
                                if (!(messageEntity instanceof TL_messageEntityCode)) {
                                    if (!(messageEntity instanceof TL_messageEntityPre)) {
                                        StringBuilder stringBuilder;
                                        if (messageEntity instanceof TL_messageEntityMentionName) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                            stringBuilder.append(((TL_messageEntityMentionName) messageEntity).user_id);
                                            spannable.setSpan(new URLSpanUserMention(stringBuilder.toString(), messageObject.type), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                        } else if (messageEntity instanceof TL_inputMessageEntityMentionName) {
                                            stringBuilder = new StringBuilder();
                                            stringBuilder.append(TtmlNode.ANONYMOUS_REGION_ID);
                                            stringBuilder.append(((TL_inputMessageEntityMentionName) messageEntity).user_id.user_id);
                                            spannable.setSpan(new URLSpanUserMention(stringBuilder.toString(), messageObject.type), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                        } else if (!z2) {
                                            String substring = TextUtils.substring(charSequence2, messageEntity.offset, messageEntity.offset + messageEntity.length);
                                            if (messageEntity instanceof TL_messageEntityBotCommand) {
                                                spannable.setSpan(new URLSpanBotCommand(substring, messageObject.type), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                            } else {
                                                if (!((messageEntity instanceof TL_messageEntityHashtag) || (messageEntity instanceof TL_messageEntityMention))) {
                                                    if (!(messageEntity instanceof TL_messageEntityCashtag)) {
                                                        if (messageEntity instanceof TL_messageEntityEmail) {
                                                            StringBuilder stringBuilder2 = new StringBuilder();
                                                            stringBuilder2.append("mailto:");
                                                            stringBuilder2.append(substring);
                                                            spannable.setSpan(new URLSpanReplacement(stringBuilder2.toString()), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                                        } else {
                                                            if (messageEntity instanceof TL_messageEntityUrl) {
                                                                if (substring.toLowerCase().startsWith("http") || substring.toLowerCase().startsWith("tg://")) {
                                                                    spannable.setSpan(new URLSpanBrowser(substring), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                                                } else {
                                                                    stringBuilder = new StringBuilder();
                                                                    stringBuilder.append("http://");
                                                                    stringBuilder.append(substring);
                                                                    spannable.setSpan(new URLSpanBrowser(stringBuilder.toString()), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                                                }
                                                            } else if (messageEntity instanceof TL_messageEntityPhone) {
                                                                String stripExceptNumbers = PhoneFormat.stripExceptNumbers(substring);
                                                                if (substring.startsWith("+")) {
                                                                    StringBuilder stringBuilder3 = new StringBuilder();
                                                                    stringBuilder3.append("+");
                                                                    stringBuilder3.append(stripExceptNumbers);
                                                                    stripExceptNumbers = stringBuilder3.toString();
                                                                }
                                                                stringBuilder = new StringBuilder();
                                                                stringBuilder.append("tel://");
                                                                stringBuilder.append(stripExceptNumbers);
                                                                spannable.setSpan(new URLSpanBrowser(stringBuilder.toString()), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                                            } else if (messageEntity instanceof TL_messageEntityTextUrl) {
                                                                spannable.setSpan(new URLSpanReplacement(messageEntity.url), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                                            }
                                                            z5 = true;
                                                        }
                                                    }
                                                }
                                                spannable.setSpan(new URLSpanNoUnderline(substring), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                                            }
                                        }
                                    }
                                }
                                byte b = z ? (byte) 2 : isOutOwner() ? (byte) 1 : z3;
                                spannable.setSpan(new URLSpanMono(spannable, messageEntity.offset, messageEntity.offset + messageEntity.length, b), messageEntity.offset, messageEntity.offset + messageEntity.length, 33);
                            }
                        }
                    }
                    i++;
                    z3 = false;
                }
                return z5;
            }

            public void generateLayout(org.telegram.tgnet.TLRPC.User r32) {
                /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r7_7 android.text.StaticLayout) in PHI: PHI: (r7_8 android.text.StaticLayout) = (r7_6 android.text.StaticLayout), (r7_7 android.text.StaticLayout) binds: {(r7_6 android.text.StaticLayout)=B:117:0x019e, (r7_7 android.text.StaticLayout)=B:118:0x01bd}
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
                r31 = this;
                r1 = r31;
                r2 = r32;
                r3 = r1.type;
                if (r3 != 0) goto L_0x047d;
            L_0x0008:
                r3 = r1.messageOwner;
                r3 = r3.to_id;
                if (r3 == 0) goto L_0x047d;
            L_0x000e:
                r3 = r1.messageText;
                r3 = android.text.TextUtils.isEmpty(r3);
                if (r3 == 0) goto L_0x0018;
            L_0x0016:
                goto L_0x047d;
            L_0x0018:
                r31.generateLinkDescription();
                r3 = new java.util.ArrayList;
                r3.<init>();
                r1.textLayoutBlocks = r3;
                r3 = 0;
                r1.textWidth = r3;
                r4 = r1.messageOwner;
                r4 = r4.send_state;
                r5 = 1;
                if (r4 == 0) goto L_0x004a;
            L_0x002c:
                r4 = r3;
            L_0x002d:
                r6 = r1.messageOwner;
                r6 = r6.entities;
                r6 = r6.size();
                if (r4 >= r6) goto L_0x0048;
            L_0x0037:
                r6 = r1.messageOwner;
                r6 = r6.entities;
                r6 = r6.get(r4);
                r6 = r6 instanceof org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
                if (r6 != 0) goto L_0x0045;
            L_0x0043:
                r4 = r5;
                goto L_0x0053;
            L_0x0045:
                r4 = r4 + 1;
                goto L_0x002d;
            L_0x0048:
                r4 = r3;
                goto L_0x0053;
            L_0x004a:
                r4 = r1.messageOwner;
                r4 = r4.entities;
                r4 = r4.isEmpty();
                r4 = r4 ^ r5;
            L_0x0053:
                r6 = 0;
                if (r4 != 0) goto L_0x00ab;
            L_0x0057:
                r8 = r1.eventId;
                r4 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
                if (r4 != 0) goto L_0x00a9;
            L_0x005d:
                r4 = r1.messageOwner;
                r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_message_old;
                if (r4 != 0) goto L_0x00a9;
            L_0x0063:
                r4 = r1.messageOwner;
                r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_message_old2;
                if (r4 != 0) goto L_0x00a9;
            L_0x0069:
                r4 = r1.messageOwner;
                r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_message_old3;
                if (r4 != 0) goto L_0x00a9;
            L_0x006f:
                r4 = r1.messageOwner;
                r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_message_old4;
                if (r4 != 0) goto L_0x00a9;
            L_0x0075:
                r4 = r1.messageOwner;
                r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageForwarded_old;
                if (r4 != 0) goto L_0x00a9;
            L_0x007b:
                r4 = r1.messageOwner;
                r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageForwarded_old2;
                if (r4 != 0) goto L_0x00a9;
            L_0x0081:
                r4 = r1.messageOwner;
                r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_message_secret;
                if (r4 != 0) goto L_0x00a9;
            L_0x0087:
                r4 = r1.messageOwner;
                r4 = r4.media;
                r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
                if (r4 != 0) goto L_0x00a9;
            L_0x008f:
                r4 = r31.isOut();
                if (r4 == 0) goto L_0x009b;
            L_0x0095:
                r4 = r1.messageOwner;
                r4 = r4.send_state;
                if (r4 != 0) goto L_0x00a9;
            L_0x009b:
                r4 = r1.messageOwner;
                r4 = r4.id;
                if (r4 < 0) goto L_0x00a9;
            L_0x00a1:
                r4 = r1.messageOwner;
                r4 = r4.media;
                r4 = r4 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
                if (r4 == 0) goto L_0x00ab;
            L_0x00a9:
                r4 = r5;
                goto L_0x00ac;
            L_0x00ab:
                r4 = r3;
            L_0x00ac:
                if (r4 == 0) goto L_0x00b8;
            L_0x00ae:
                r8 = r31.isOutOwner();
                r9 = r1.messageText;
                addLinks(r8, r9);
                goto L_0x00d6;
            L_0x00b8:
                r8 = r1.messageText;
                r8 = r8 instanceof android.text.Spannable;
                if (r8 == 0) goto L_0x00d6;
            L_0x00be:
                r8 = r1.messageText;
                r8 = r8.length();
                r9 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
                if (r8 >= r9) goto L_0x00d6;
            L_0x00c8:
                r8 = r1.messageText;	 Catch:{ Throwable -> 0x00d1 }
                r8 = (android.text.Spannable) r8;	 Catch:{ Throwable -> 0x00d1 }
                r9 = 4;	 Catch:{ Throwable -> 0x00d1 }
                android.text.util.Linkify.addLinks(r8, r9);	 Catch:{ Throwable -> 0x00d1 }
                goto L_0x00d6;
            L_0x00d1:
                r0 = move-exception;
                r8 = r0;
                org.telegram.messenger.FileLog.m3e(r8);
            L_0x00d6:
                r8 = r1.messageText;
                r4 = r1.addEntitiesToText(r8, r4);
                r8 = r1.eventId;
                r10 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
                if (r10 != 0) goto L_0x012e;
            L_0x00e2:
                r8 = r31.isOutOwner();
                if (r8 != 0) goto L_0x012e;
            L_0x00e8:
                r8 = r1.messageOwner;
                r8 = r8.fwd_from;
                if (r8 == 0) goto L_0x0106;
            L_0x00ee:
                r8 = r1.messageOwner;
                r8 = r8.fwd_from;
                r8 = r8.saved_from_peer;
                if (r8 != 0) goto L_0x012c;
            L_0x00f6:
                r8 = r1.messageOwner;
                r8 = r8.fwd_from;
                r8 = r8.from_id;
                if (r8 != 0) goto L_0x012c;
            L_0x00fe:
                r8 = r1.messageOwner;
                r8 = r8.fwd_from;
                r8 = r8.channel_id;
                if (r8 != 0) goto L_0x012c;
            L_0x0106:
                r8 = r1.messageOwner;
                r8 = r8.from_id;
                if (r8 <= 0) goto L_0x012e;
            L_0x010c:
                r8 = r1.messageOwner;
                r8 = r8.to_id;
                r8 = r8.channel_id;
                if (r8 != 0) goto L_0x012c;
            L_0x0114:
                r8 = r1.messageOwner;
                r8 = r8.to_id;
                r8 = r8.chat_id;
                if (r8 != 0) goto L_0x012c;
            L_0x011c:
                r8 = r1.messageOwner;
                r8 = r8.media;
                r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
                if (r8 != 0) goto L_0x012c;
            L_0x0124:
                r8 = r1.messageOwner;
                r8 = r8.media;
                r8 = r8 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
                if (r8 == 0) goto L_0x012e;
            L_0x012c:
                r8 = r5;
                goto L_0x012f;
            L_0x012e:
                r8 = r3;
            L_0x012f:
                r9 = org.telegram.messenger.AndroidUtilities.isTablet();
                if (r9 == 0) goto L_0x013a;
            L_0x0135:
                r9 = org.telegram.messenger.AndroidUtilities.getMinTabletSide();
                goto L_0x013e;
            L_0x013a:
                r9 = org.telegram.messenger.AndroidUtilities.displaySize;
                r9 = r9.x;
            L_0x013e:
                r1.generatedWithMinSize = r9;
                r9 = r1.generatedWithMinSize;
                if (r8 != 0) goto L_0x014e;
            L_0x0144:
                r10 = r1.eventId;
                r8 = (r10 > r6 ? 1 : (r10 == r6 ? 0 : -1));
                if (r8 == 0) goto L_0x014b;
            L_0x014a:
                goto L_0x014e;
            L_0x014b:
                r6 = NUM; // 0x42a00000 float:80.0 double:5.522576936E-315;
                goto L_0x0150;
            L_0x014e:
                r6 = NUM; // 0x43040000 float:132.0 double:5.554956023E-315;
            L_0x0150:
                r6 = org.telegram.messenger.AndroidUtilities.dp(r6);
                r9 = r9 - r6;
                if (r2 == 0) goto L_0x015b;
            L_0x0157:
                r2 = r2.bot;
                if (r2 != 0) goto L_0x0175;
            L_0x015b:
                r2 = r31.isMegagroup();
                if (r2 != 0) goto L_0x016f;
            L_0x0161:
                r2 = r1.messageOwner;
                r2 = r2.fwd_from;
                if (r2 == 0) goto L_0x017c;
            L_0x0167:
                r2 = r1.messageOwner;
                r2 = r2.fwd_from;
                r2 = r2.channel_id;
                if (r2 == 0) goto L_0x017c;
            L_0x016f:
                r2 = r31.isOut();
                if (r2 != 0) goto L_0x017c;
            L_0x0175:
                r2 = NUM; // 0x41a00000 float:20.0 double:5.439686476E-315;
                r2 = org.telegram.messenger.AndroidUtilities.dp(r2);
                r9 = r9 - r2;
            L_0x017c:
                r2 = r1.messageOwner;
                r2 = r2.media;
                r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
                r6 = NUM; // 0x41200000 float:10.0 double:5.398241246E-315;
                if (r2 == 0) goto L_0x018b;
            L_0x0186:
                r2 = org.telegram.messenger.AndroidUtilities.dp(r6);
                r9 = r9 - r2;
            L_0x018b:
                r2 = r1.messageOwner;
                r2 = r2.media;
                r2 = r2 instanceof org.telegram.tgnet.TLRPC.TL_messageMediaGame;
                if (r2 == 0) goto L_0x0196;
            L_0x0193:
                r2 = org.telegram.ui.ActionBar.Theme.chat_msgGameTextPaint;
                goto L_0x0198;
            L_0x0196:
                r2 = org.telegram.ui.ActionBar.Theme.chat_msgTextPaint;
            L_0x0198:
                r7 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0477 }
                r8 = 24;	 Catch:{ Exception -> 0x0477 }
                if (r7 < r8) goto L_0x01bd;	 Catch:{ Exception -> 0x0477 }
            L_0x019e:
                r7 = r1.messageText;	 Catch:{ Exception -> 0x0477 }
                r10 = r1.messageText;	 Catch:{ Exception -> 0x0477 }
                r10 = r10.length();	 Catch:{ Exception -> 0x0477 }
                r7 = android.text.StaticLayout.Builder.obtain(r7, r3, r10, r2, r9);	 Catch:{ Exception -> 0x0477 }
                r7 = r7.setBreakStrategy(r5);	 Catch:{ Exception -> 0x0477 }
                r7 = r7.setHyphenationFrequency(r3);	 Catch:{ Exception -> 0x0477 }
                r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0477 }
                r7 = r7.setAlignment(r10);	 Catch:{ Exception -> 0x0477 }
                r7 = r7.build();	 Catch:{ Exception -> 0x0477 }
                goto L_0x01cf;	 Catch:{ Exception -> 0x0477 }
            L_0x01bd:
                r7 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0477 }
                r11 = r1.messageText;	 Catch:{ Exception -> 0x0477 }
                r14 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0477 }
                r15 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;	 Catch:{ Exception -> 0x0477 }
                r16 = 0;	 Catch:{ Exception -> 0x0477 }
                r17 = 0;	 Catch:{ Exception -> 0x0477 }
                r10 = r7;	 Catch:{ Exception -> 0x0477 }
                r12 = r2;	 Catch:{ Exception -> 0x0477 }
                r13 = r9;	 Catch:{ Exception -> 0x0477 }
                r10.<init>(r11, r12, r13, r14, r15, r16, r17);	 Catch:{ Exception -> 0x0477 }
            L_0x01cf:
                r10 = r7.getHeight();
                r1.textHeight = r10;
                r10 = r7.getLineCount();
                r1.linesCount = r10;
                r10 = android.os.Build.VERSION.SDK_INT;
                if (r10 < r8) goto L_0x01e1;
            L_0x01df:
                r6 = r5;
                goto L_0x01eb;
            L_0x01e1:
                r10 = r1.linesCount;
                r10 = (float) r10;
                r10 = r10 / r6;
                r10 = (double) r10;
                r10 = java.lang.Math.ceil(r10);
                r6 = (int) r10;
            L_0x01eb:
                r15 = 0;
                r13 = r3;
                r14 = r13;
                r20 = r15;
            L_0x01f0:
                if (r14 >= r6) goto L_0x0476;
            L_0x01f2:
                r10 = android.os.Build.VERSION.SDK_INT;
                if (r10 < r8) goto L_0x01fa;
            L_0x01f6:
                r10 = r1.linesCount;
            L_0x01f8:
                r12 = r10;
                goto L_0x0204;
            L_0x01fa:
                r10 = 10;
                r11 = r1.linesCount;
                r11 = r11 - r13;
                r10 = java.lang.Math.min(r10, r11);
                goto L_0x01f8;
            L_0x0204:
                r11 = new org.telegram.messenger.MessageObject$TextLayoutBlock;
                r11.<init>();
                if (r6 != r5) goto L_0x0220;
            L_0x020b:
                r11.textLayout = r7;
                r11.textYOffset = r15;
                r11.charactersOffset = r3;
                r10 = r1.textHeight;
                r11.height = r10;
                r23 = r2;
                r22 = r4;
                r5 = r11;
                r4 = r13;
                r2 = r14;
                r3 = r20;
                goto L_0x02f6;
            L_0x0220:
                r10 = r7.getLineStart(r13);
                r16 = r13 + r12;
                r15 = r16 + -1;
                r15 = r7.getLineEnd(r15);
                if (r15 >= r10) goto L_0x023b;
            L_0x022e:
                r23 = r2;
                r22 = r4;
                r12 = r5;
                r26 = r7;
                r27 = r13;
                r2 = r14;
                r3 = 0;
                goto L_0x0465;
            L_0x023b:
                r11.charactersOffset = r10;
                r11.charactersEnd = r15;
                if (r4 == 0) goto L_0x0281;
            L_0x0241:
                r3 = android.os.Build.VERSION.SDK_INT;	 Catch:{ Exception -> 0x0272 }
                if (r3 < r8) goto L_0x0270;	 Catch:{ Exception -> 0x0272 }
            L_0x0245:
                r3 = r1.messageText;	 Catch:{ Exception -> 0x0272 }
                r8 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;	 Catch:{ Exception -> 0x0272 }
                r8 = org.telegram.messenger.AndroidUtilities.dp(r8);	 Catch:{ Exception -> 0x0272 }
                r8 = r8 + r9;	 Catch:{ Exception -> 0x0272 }
                r3 = android.text.StaticLayout.Builder.obtain(r3, r10, r15, r2, r8);	 Catch:{ Exception -> 0x0272 }
                r3 = r3.setBreakStrategy(r5);	 Catch:{ Exception -> 0x0272 }
                r8 = 0;	 Catch:{ Exception -> 0x0272 }
                r3 = r3.setHyphenationFrequency(r8);	 Catch:{ Exception -> 0x0272 }
                r10 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0272 }
                r3 = r3.setAlignment(r10);	 Catch:{ Exception -> 0x0272 }
                r3 = r3.build();	 Catch:{ Exception -> 0x0272 }
                r11.textLayout = r3;	 Catch:{ Exception -> 0x0272 }
                r23 = r2;	 Catch:{ Exception -> 0x0272 }
                r22 = r4;	 Catch:{ Exception -> 0x0272 }
                r5 = r11;	 Catch:{ Exception -> 0x0272 }
                r8 = r12;	 Catch:{ Exception -> 0x0272 }
                r4 = r13;	 Catch:{ Exception -> 0x0272 }
                r2 = r14;	 Catch:{ Exception -> 0x0272 }
                goto L_0x02a5;	 Catch:{ Exception -> 0x0272 }
            L_0x0270:
                r8 = 0;	 Catch:{ Exception -> 0x0272 }
                goto L_0x0282;	 Catch:{ Exception -> 0x0272 }
            L_0x0272:
                r0 = move-exception;	 Catch:{ Exception -> 0x0272 }
                r23 = r2;	 Catch:{ Exception -> 0x0272 }
                r22 = r4;	 Catch:{ Exception -> 0x0272 }
                r12 = r5;	 Catch:{ Exception -> 0x0272 }
                r26 = r7;	 Catch:{ Exception -> 0x0272 }
                r27 = r13;	 Catch:{ Exception -> 0x0272 }
                r2 = r14;	 Catch:{ Exception -> 0x0272 }
                r3 = 0;	 Catch:{ Exception -> 0x0272 }
            L_0x027e:
                r4 = r0;	 Catch:{ Exception -> 0x0272 }
                goto L_0x0462;	 Catch:{ Exception -> 0x0272 }
            L_0x0281:
                r8 = r3;	 Catch:{ Exception -> 0x0272 }
            L_0x0282:
                r3 = new android.text.StaticLayout;	 Catch:{ Exception -> 0x0272 }
                r8 = r1.messageText;	 Catch:{ Exception -> 0x0272 }
                r16 = android.text.Layout.Alignment.ALIGN_NORMAL;	 Catch:{ Exception -> 0x0272 }
                r17 = NUM; // 0x3f800000 float:1.0 double:5.263544247E-315;
                r18 = 0;
                r19 = 0;
                r21 = r10;
                r10 = r3;
                r5 = r11;
                r11 = r8;
                r8 = r12;
                r12 = r21;
                r22 = r4;
                r4 = r13;
                r13 = r15;
                r15 = r14;
                r14 = r2;
                r23 = r2;
                r2 = r15;
                r15 = r9;
                r10.<init>(r11, r12, r13, r14, r15, r16, r17, r18, r19);	 Catch:{ Exception -> 0x0459 }
                r5.textLayout = r3;	 Catch:{ Exception -> 0x0459 }
            L_0x02a5:
                r3 = r7.getLineTop(r4);	 Catch:{ Exception -> 0x0459 }
                r3 = (float) r3;	 Catch:{ Exception -> 0x0459 }
                r5.textYOffset = r3;	 Catch:{ Exception -> 0x0459 }
                if (r2 == 0) goto L_0x02b5;	 Catch:{ Exception -> 0x0459 }
            L_0x02ae:
                r3 = r5.textYOffset;	 Catch:{ Exception -> 0x0459 }
                r3 = r3 - r20;	 Catch:{ Exception -> 0x0459 }
                r3 = (int) r3;	 Catch:{ Exception -> 0x0459 }
                r5.height = r3;	 Catch:{ Exception -> 0x0459 }
            L_0x02b5:
                r3 = r5.height;	 Catch:{ Exception -> 0x0459 }
                r10 = r5.textLayout;	 Catch:{ Exception -> 0x0459 }
                r11 = r5.textLayout;	 Catch:{ Exception -> 0x0459 }
                r11 = r11.getLineCount();	 Catch:{ Exception -> 0x0459 }
                r12 = 1;	 Catch:{ Exception -> 0x0459 }
                r11 = r11 - r12;	 Catch:{ Exception -> 0x0459 }
                r10 = r10.getLineBottom(r11);	 Catch:{ Exception -> 0x0459 }
                r3 = java.lang.Math.max(r3, r10);	 Catch:{ Exception -> 0x0459 }
                r5.height = r3;	 Catch:{ Exception -> 0x0459 }
                r3 = r5.textYOffset;	 Catch:{ Exception -> 0x0459 }
                r10 = r6 + -1;
                if (r2 != r10) goto L_0x02f5;
            L_0x02d1:
                r10 = r5.textLayout;
                r10 = r10.getLineCount();
                r12 = java.lang.Math.max(r8, r10);
                r8 = r1.textHeight;	 Catch:{ Exception -> 0x02ef }
                r10 = r5.textYOffset;	 Catch:{ Exception -> 0x02ef }
                r11 = r5.textLayout;	 Catch:{ Exception -> 0x02ef }
                r11 = r11.getHeight();	 Catch:{ Exception -> 0x02ef }
                r11 = (float) r11;	 Catch:{ Exception -> 0x02ef }
                r10 = r10 + r11;	 Catch:{ Exception -> 0x02ef }
                r10 = (int) r10;	 Catch:{ Exception -> 0x02ef }
                r8 = java.lang.Math.max(r8, r10);	 Catch:{ Exception -> 0x02ef }
                r1.textHeight = r8;	 Catch:{ Exception -> 0x02ef }
                goto L_0x02f6;
            L_0x02ef:
                r0 = move-exception;
                r8 = r0;
                org.telegram.messenger.FileLog.m3e(r8);
                goto L_0x02f6;
            L_0x02f5:
                r12 = r8;
            L_0x02f6:
                r8 = r1.textLayoutBlocks;
                r8.add(r5);
                r8 = r5.textLayout;	 Catch:{ Exception -> 0x0311 }
                r10 = r12 + -1;	 Catch:{ Exception -> 0x0311 }
                r15 = r8.getLineLeft(r10);	 Catch:{ Exception -> 0x0311 }
                if (r2 != 0) goto L_0x030f;
            L_0x0305:
                r8 = 0;
                r10 = (r15 > r8 ? 1 : (r15 == r8 ? 0 : -1));
                if (r10 < 0) goto L_0x031c;
            L_0x030a:
                r1.textXOffset = r15;	 Catch:{ Exception -> 0x030d }
                goto L_0x031c;
            L_0x030d:
                r0 = move-exception;
                goto L_0x0313;
            L_0x030f:
                r8 = 0;
                goto L_0x031c;
            L_0x0311:
                r0 = move-exception;
                r8 = 0;
            L_0x0313:
                r10 = r0;
                if (r2 != 0) goto L_0x0318;
            L_0x0316:
                r1.textXOffset = r8;
            L_0x0318:
                org.telegram.messenger.FileLog.m3e(r10);
                r15 = r8;
            L_0x031c:
                r10 = r5.textLayout;	 Catch:{ Exception -> 0x0325 }
                r11 = r12 + -1;	 Catch:{ Exception -> 0x0325 }
                r10 = r10.getLineWidth(r11);	 Catch:{ Exception -> 0x0325 }
                goto L_0x032b;
            L_0x0325:
                r0 = move-exception;
                r10 = r0;
                org.telegram.messenger.FileLog.m3e(r10);
                r10 = r8;
            L_0x032b:
                r13 = (double) r10;
                r13 = java.lang.Math.ceil(r13);
                r11 = (int) r13;
                r13 = r6 + -1;
                if (r2 != r13) goto L_0x0337;
            L_0x0335:
                r1.lastLineWidth = r11;
            L_0x0337:
                r10 = r10 + r15;
                r24 = r9;
                r8 = (double) r10;
                r8 = java.lang.Math.ceil(r8);
                r8 = (int) r8;
                r9 = 1;
                if (r12 <= r9) goto L_0x0408;
            L_0x0343:
                r25 = r3;
                r3 = r8;
                r14 = r11;
                r9 = 0;
                r10 = 0;
                r11 = 0;
                r15 = 0;
            L_0x034b:
                if (r9 >= r12) goto L_0x03dd;
            L_0x034d:
                r26 = r7;
                r7 = r5.textLayout;	 Catch:{ Exception -> 0x0356 }
                r7 = r7.getLineWidth(r9);	 Catch:{ Exception -> 0x0356 }
                goto L_0x035c;
            L_0x0356:
                r0 = move-exception;
                r7 = r0;
                org.telegram.messenger.FileLog.m3e(r7);
                r7 = 0;
            L_0x035c:
                r27 = r4;
                r4 = r24 + 20;
                r4 = (float) r4;
                r4 = (r7 > r4 ? 1 : (r7 == r4 ? 0 : -1));
                if (r4 <= 0) goto L_0x0369;
            L_0x0365:
                r4 = r24;
                r7 = (float) r4;
                goto L_0x036b;
            L_0x0369:
                r4 = r24;
            L_0x036b:
                r28 = r12;
                r12 = r5.textLayout;	 Catch:{ Exception -> 0x0374 }
                r12 = r12.getLineLeft(r9);	 Catch:{ Exception -> 0x0374 }
                goto L_0x037a;
            L_0x0374:
                r0 = move-exception;
                r12 = r0;
                org.telegram.messenger.FileLog.m3e(r12);
                r12 = 0;
            L_0x037a:
                r16 = 0;
                r17 = (r12 > r16 ? 1 : (r12 == r16 ? 0 : -1));
                if (r17 <= 0) goto L_0x0396;
            L_0x0380:
                r29 = r4;
                r4 = r1.textXOffset;
                r4 = java.lang.Math.min(r4, r12);
                r1.textXOffset = r4;
                r4 = r5.directionFlags;
                r30 = r6;
                r6 = 1;
                r4 = r4 | r6;
                r4 = (byte) r4;
                r5.directionFlags = r4;
                r1.hasRtl = r6;
                goto L_0x03a1;
            L_0x0396:
                r29 = r4;
                r30 = r6;
                r4 = r5.directionFlags;
                r4 = r4 | 2;
                r4 = (byte) r4;
                r5.directionFlags = r4;
            L_0x03a1:
                if (r10 != 0) goto L_0x03b2;
            L_0x03a3:
                r4 = 0;
                r6 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1));
                if (r6 != 0) goto L_0x03b2;
            L_0x03a8:
                r4 = r5.textLayout;	 Catch:{ Exception -> 0x03b1 }
                r4 = r4.getParagraphDirection(r9);	 Catch:{ Exception -> 0x03b1 }
                r6 = 1;
                if (r4 != r6) goto L_0x03b2;
            L_0x03b1:
                r10 = 1;
            L_0x03b2:
                r15 = java.lang.Math.max(r15, r7);
                r12 = r12 + r7;
                r11 = java.lang.Math.max(r11, r12);
                r6 = (double) r7;
                r6 = java.lang.Math.ceil(r6);
                r4 = (int) r6;
                r14 = java.lang.Math.max(r14, r4);
                r6 = (double) r12;
                r6 = java.lang.Math.ceil(r6);
                r4 = (int) r6;
                r3 = java.lang.Math.max(r3, r4);
                r9 = r9 + 1;
                r7 = r26;
                r4 = r27;
                r12 = r28;
                r24 = r29;
                r6 = r30;
                goto L_0x034b;
            L_0x03dd:
                r27 = r4;
                r30 = r6;
                r26 = r7;
                r28 = r12;
                r29 = r24;
                if (r10 == 0) goto L_0x03ef;
            L_0x03e9:
                if (r2 != r13) goto L_0x03ed;
            L_0x03eb:
                r1.lastLineWidth = r8;
            L_0x03ed:
                r15 = r11;
                goto L_0x03f3;
            L_0x03ef:
                if (r2 != r13) goto L_0x03f3;
            L_0x03f1:
                r1.lastLineWidth = r14;
            L_0x03f3:
                r3 = r1.textWidth;
                r4 = (double) r15;
                r4 = java.lang.Math.ceil(r4);
                r4 = (int) r4;
                r3 = java.lang.Math.max(r3, r4);
                r1.textWidth = r3;
                r9 = r29;
                r6 = r30;
                r3 = 0;
                r12 = 1;
                goto L_0x0454;
            L_0x0408:
                r25 = r3;
                r27 = r4;
                r30 = r6;
                r26 = r7;
                r28 = r12;
                r29 = r24;
                r3 = 0;
                r4 = (r15 > r3 ? 1 : (r15 == r3 ? 0 : -1));
                if (r4 <= 0) goto L_0x043c;
            L_0x0419:
                r4 = r1.textXOffset;
                r4 = java.lang.Math.min(r4, r15);
                r1.textXOffset = r4;
                r4 = r1.textXOffset;
                r4 = (r4 > r3 ? 1 : (r4 == r3 ? 0 : -1));
                if (r4 != 0) goto L_0x042b;
            L_0x0427:
                r4 = (float) r11;
                r4 = r4 + r15;
                r4 = (int) r4;
                r11 = r4;
            L_0x042b:
                r6 = r30;
                r12 = 1;
                if (r6 == r12) goto L_0x0432;
            L_0x0430:
                r4 = r12;
                goto L_0x0433;
            L_0x0432:
                r4 = 0;
            L_0x0433:
                r1.hasRtl = r4;
                r4 = r5.directionFlags;
                r4 = r4 | r12;
                r4 = (byte) r4;
                r5.directionFlags = r4;
                goto L_0x0446;
            L_0x043c:
                r6 = r30;
                r12 = 1;
                r4 = r5.directionFlags;
                r4 = r4 | 2;
                r4 = (byte) r4;
                r5.directionFlags = r4;
            L_0x0446:
                r4 = r1.textWidth;
                r9 = r29;
                r5 = java.lang.Math.min(r9, r11);
                r4 = java.lang.Math.max(r4, r5);
                r1.textWidth = r4;
            L_0x0454:
                r13 = r27 + r28;
                r20 = r25;
                goto L_0x0467;
            L_0x0459:
                r0 = move-exception;
                r27 = r4;
                r26 = r7;
                r3 = 0;
                r12 = 1;
                goto L_0x027e;
            L_0x0462:
                org.telegram.messenger.FileLog.m3e(r4);
            L_0x0465:
                r13 = r27;
            L_0x0467:
                r14 = r2 + 1;
                r15 = r3;
                r5 = r12;
                r4 = r22;
                r2 = r23;
                r7 = r26;
                r3 = 0;
                r8 = 24;
                goto L_0x01f0;
            L_0x0476:
                return;
            L_0x0477:
                r0 = move-exception;
                r2 = r0;
                org.telegram.messenger.FileLog.m3e(r2);
                return;
            L_0x047d:
                return;
                */
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLayout(org.telegram.tgnet.TLRPC$User):void");
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
                        int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                        if (getDialogId() == ((long) clientUserId)) {
                            if (this.messageOwner.fwd_from.from_id == clientUserId || (this.messageOwner.fwd_from.saved_from_peer != null && this.messageOwner.fwd_from.saved_from_peer.user_id == clientUserId)) {
                                z = true;
                            }
                            return z;
                        }
                        if (this.messageOwner.fwd_from.saved_from_peer == null || this.messageOwner.fwd_from.saved_from_peer.user_id == clientUserId) {
                            z = true;
                        }
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
                int i = !message.unread ? 1 : 0;
                return message.media_unread == null ? i | 2 : i;
            }

            public void setContentIsRead() {
                this.messageOwner.media_unread = false;
            }

            public int getId() {
                return this.messageOwner.id;
            }

            public static int getMessageSize(Message message) {
                return (message.media == null || message.media.document == null) ? null : message.media.document.size;
            }

            public int getSize() {
                return getMessageSize(this.messageOwner);
            }

            public long getIdWithChannel() {
                long j = (long) this.messageOwner.id;
                return (this.messageOwner.to_id == null || this.messageOwner.to_id.channel_id == 0) ? j : j | (((long) this.messageOwner.to_id.channel_id) << 32);
            }

            public int getChannelId() {
                return this.messageOwner.to_id != null ? this.messageOwner.to_id.channel_id : 0;
            }

            public static boolean shouldEncryptPhotoOrVideo(Message message) {
                boolean z = false;
                if (message instanceof TL_message_secret) {
                    if (((message.media instanceof TL_messageMediaPhoto) || isVideoMessage(message)) && message.ttl > 0 && message.ttl <= 60) {
                        z = true;
                    }
                    return z;
                }
                if (((message.media instanceof TL_messageMediaPhoto) || (message.media instanceof TL_messageMediaDocument)) && message.media.ttl_seconds != null) {
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
                    if ((!(message.media instanceof TL_messageMediaPhoto) && !(message.media instanceof TL_messageMediaDocument)) || message.media.ttl_seconds == null) {
                        z = false;
                    }
                    return z;
                }
            }

            public boolean needDrawBluredPreview() {
                boolean z = true;
                if (this.messageOwner instanceof TL_message_secret) {
                    int max = Math.max(this.messageOwner.ttl, this.messageOwner.media.ttl_seconds);
                    if (max > 0) {
                        if (!((this.messageOwner.media instanceof TL_messageMediaPhoto) || isVideo() || isGif()) || max > 60) {
                            if (isRoundVideo()) {
                            }
                        }
                        return z;
                    }
                    z = false;
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
                return (this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.saved_from_peer == null || this.messageOwner.fwd_from.saved_from_peer.channel_id == 0) ? false : ChatObject.isMegagroup(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.saved_from_peer.channel_id)));
            }

            public static boolean isMegagroup(Message message) {
                return (message.flags & Integer.MIN_VALUE) != null ? true : null;
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
                for (int i = 0; i < document.attributes.size(); i++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                        return documentAttribute.supports_streaming;
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
                return this.messageOwner.destroyTime != 0 ? Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) : this.messageOwner.ttl;
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
                if (this.messageOwner.media instanceof TL_messageMediaDocument) {
                    return FileLoader.getDocumentFileName(this.messageOwner.media.document);
                }
                return this.messageOwner.media instanceof TL_messageMediaWebPage ? FileLoader.getDocumentFileName(this.messageOwner.media.webpage.document) : TtmlNode.ANONYMOUS_REGION_ID;
            }

            public static boolean isStickerDocument(Document document) {
                if (document != null) {
                    for (int i = 0; i < document.attributes.size(); i++) {
                        if (((DocumentAttribute) document.attributes.get(i)) instanceof TL_documentAttributeSticker) {
                            return true;
                        }
                    }
                }
                return false;
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

            public static boolean isVoiceWebDocument(TL_webDocument tL_webDocument) {
                return (tL_webDocument == null || tL_webDocument.mime_type.equals("audio/ogg") == null) ? null : true;
            }

            public static boolean isImageWebDocument(TL_webDocument tL_webDocument) {
                return (tL_webDocument == null || isGifDocument(tL_webDocument) || tL_webDocument.mime_type.startsWith("image/") == null) ? null : true;
            }

            public static boolean isVideoWebDocument(TL_webDocument tL_webDocument) {
                return (tL_webDocument == null || tL_webDocument.mime_type.startsWith("video/") == null) ? null : true;
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
                        if (!(toLowerCase.equals(MimeTypes.AUDIO_FLAC) || toLowerCase.equals("audio/ogg") || toLowerCase.equals(MimeTypes.AUDIO_OPUS))) {
                            if (!toLowerCase.equals("audio/x-opus+ogg")) {
                                return toLowerCase.equals("application/octet-stream") && FileLoader.getDocumentFileName(document).endsWith(".opus") != null;
                            }
                        }
                        return true;
                    }
                }
            }

            public static boolean isVideoDocument(Document document) {
                boolean z = false;
                if (document == null) {
                    return false;
                }
                int i = 0;
                int i2 = i;
                int i3 = i2;
                int i4 = i3;
                int i5 = i4;
                while (i < document.attributes.size()) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                        if (documentAttribute.round_message) {
                            return false;
                        }
                        i3 = documentAttribute.f36w;
                        i4 = documentAttribute.f35h;
                        i5 = 1;
                    } else if (documentAttribute instanceof TL_documentAttributeAnimated) {
                        i2 = 1;
                    }
                    i++;
                }
                if (i2 != 0 && (r3 > 1280 || r4 > 1280)) {
                    i2 = false;
                }
                if (i5 != 0 && r2 == 0) {
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
                return (message.media == null || message.media.document == null || isStickerDocument(message.media.document) == null) ? null : true;
            }

            public static boolean isMaskMessage(Message message) {
                return (message.media == null || message.media.document == null || isMaskDocument(message.media.document) == null) ? null : true;
            }

            public static boolean isMusicMessage(Message message) {
                if (message.media instanceof TL_messageMediaWebPage) {
                    return isMusicDocument(message.media.webpage.document);
                }
                message = (message.media == null || message.media.document == null || isMusicDocument(message.media.document) == null) ? null : true;
                return message;
            }

            public static boolean isGifMessage(Message message) {
                return (message.media == null || message.media.document == null || isGifDocument(message.media.document) == null) ? null : true;
            }

            public static boolean isRoundVideoMessage(Message message) {
                if (message.media instanceof TL_messageMediaWebPage) {
                    return isRoundVideoDocument(message.media.webpage.document);
                }
                message = (message.media == null || message.media.document == null || isRoundVideoDocument(message.media.document) == null) ? null : true;
                return message;
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
                message = (message.media == null || message.media.document == null || isVoiceDocument(message.media.document) == null) ? null : true;
                return message;
            }

            public static boolean isNewGifMessage(Message message) {
                if (message.media instanceof TL_messageMediaWebPage) {
                    return isNewGifDocument(message.media.webpage.document);
                }
                message = (message.media == null || message.media.document == null || isNewGifDocument(message.media.document) == null) ? null : true;
                return message;
            }

            public static boolean isLiveLocationMessage(Message message) {
                return message.media instanceof TL_messageMediaGeoLive;
            }

            public static boolean isVideoMessage(Message message) {
                if (message.media instanceof TL_messageMediaWebPage) {
                    return isVideoDocument(message.media.webpage.document);
                }
                message = (message.media == null || message.media.document == null || isVideoDocument(message.media.document) == null) ? null : true;
                return message;
            }

            public static boolean isGameMessage(Message message) {
                return message.media instanceof TL_messageMediaGame;
            }

            public static boolean isInvoiceMessage(Message message) {
                return message.media instanceof TL_messageMediaInvoice;
            }

            public static InputStickerSet getInputStickerSet(Message message) {
                if (!(message.media == null || message.media.document == null)) {
                    message = message.media.document.attributes.iterator();
                    while (message.hasNext()) {
                        DocumentAttribute documentAttribute = (DocumentAttribute) message.next();
                        if (documentAttribute instanceof TL_documentAttributeSticker) {
                            if ((documentAttribute.stickerset instanceof TL_inputStickerSetEmpty) != null) {
                                return null;
                            }
                            return documentAttribute.stickerset;
                        }
                    }
                }
                return null;
            }

            public static long getStickerSetId(Document document) {
                if (document == null) {
                    return -1;
                }
                int i = 0;
                while (i < document.attributes.size()) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (!(documentAttribute instanceof TL_documentAttributeSticker)) {
                        i++;
                    } else if ((documentAttribute.stickerset instanceof TL_inputStickerSetEmpty) != null) {
                        return -1;
                    } else {
                        return documentAttribute.stickerset.id;
                    }
                }
                return -1;
            }

            public String getStrickerChar() {
                if (!(this.messageOwner.media == null || this.messageOwner.media.document == null)) {
                    Iterator it = this.messageOwner.media.document.attributes.iterator();
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
                int i = 0;
                int i2;
                if (this.type == 0) {
                    i2 = this.textHeight;
                    if ((this.messageOwner.media instanceof TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TL_webPage)) {
                        i = AndroidUtilities.dp(100.0f);
                    }
                    i2 += i;
                    if (isReply()) {
                        i2 += AndroidUtilities.dp(42.0f);
                    }
                    return i2;
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
                        float minTabletSide;
                        int i3;
                        float f = ((float) AndroidUtilities.displaySize.y) * 0.4f;
                        if (AndroidUtilities.isTablet()) {
                            minTabletSide = ((float) AndroidUtilities.getMinTabletSide()) * 0.5f;
                        } else {
                            minTabletSide = ((float) AndroidUtilities.displaySize.x) * 0.5f;
                        }
                        Iterator it = this.messageOwner.media.document.attributes.iterator();
                        while (it.hasNext()) {
                            DocumentAttribute documentAttribute = (DocumentAttribute) it.next();
                            if (documentAttribute instanceof TL_documentAttributeImageSize) {
                                i = documentAttribute.f36w;
                                i3 = documentAttribute.f35h;
                                break;
                            }
                        }
                        i3 = 0;
                        if (i == 0) {
                            i3 = (int) f;
                            i = AndroidUtilities.dp(100.0f) + i3;
                        }
                        float f2 = (float) i3;
                        if (f2 > f) {
                            i = (int) (((float) i) * (f / f2));
                            i3 = (int) f;
                        }
                        f = (float) i;
                        if (f > minTabletSide) {
                            i3 = (int) (((float) i3) * (minTabletSide / f));
                        }
                        return i3 + AndroidUtilities.dp(14.0f);
                    }
                    if (AndroidUtilities.isTablet()) {
                        i2 = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
                    } else {
                        i2 = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
                    }
                    i = AndroidUtilities.dp(100.0f) + i2;
                    if (i2 > AndroidUtilities.getPhotoSize()) {
                        i2 = AndroidUtilities.getPhotoSize();
                    }
                    if (i > AndroidUtilities.getPhotoSize()) {
                        i = AndroidUtilities.getPhotoSize();
                    }
                    PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                    if (closestPhotoSizeWithSize != null) {
                        i2 = (int) (((float) closestPhotoSizeWithSize.f42h) / (((float) closestPhotoSizeWithSize.f43w) / ((float) i2)));
                        if (i2 == 0) {
                            i2 = AndroidUtilities.dp(100.0f);
                        }
                        if (i2 <= i) {
                            i = i2 < AndroidUtilities.dp(120.0f) ? AndroidUtilities.dp(120.0f) : i2;
                        }
                        if (needDrawBluredPreview()) {
                            if (AndroidUtilities.isTablet()) {
                                i2 = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                            } else {
                                i2 = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f);
                            }
                            i = i2;
                        }
                    }
                    return i + AndroidUtilities.dp(14.0f);
                }
            }

            public String getStickerEmoji() {
                String str;
                int i = 0;
                while (true) {
                    str = null;
                    if (i >= this.messageOwner.media.document.attributes.size()) {
                        return null;
                    }
                    DocumentAttribute documentAttribute = (DocumentAttribute) this.messageOwner.media.document.attributes.get(i);
                    if (documentAttribute instanceof TL_documentAttributeSticker) {
                        break;
                    }
                    i++;
                }
                if (documentAttribute.alt != null && documentAttribute.alt.length() > 0) {
                    str = documentAttribute.alt;
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

            public String getMusicTitle(boolean z) {
                Document document;
                if (this.type == 0) {
                    document = this.messageOwner.media.webpage.document;
                } else {
                    document = this.messageOwner.media.document;
                }
                int i = 0;
                while (i < document.attributes.size()) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (documentAttribute instanceof TL_documentAttributeAudio) {
                        if (!documentAttribute.voice) {
                            String str = documentAttribute.title;
                            if (str == null || str.length() == 0) {
                                str = FileLoader.getDocumentFileName(document);
                                if (TextUtils.isEmpty(str) && z) {
                                    str = LocaleController.getString("AudioUnknownTitle", C0446R.string.AudioUnknownTitle);
                                }
                            }
                            return str;
                        } else if (z) {
                            return LocaleController.formatDateAudio((long) this.messageOwner.date);
                        } else {
                            return false;
                        }
                    } else if ((documentAttribute instanceof TL_documentAttributeVideo) && documentAttribute.round_message) {
                        return LocaleController.formatDateAudio((long) this.messageOwner.date);
                    } else {
                        i++;
                    }
                }
                z = FileLoader.getDocumentFileName(document);
                if (TextUtils.isEmpty(z)) {
                    z = LocaleController.getString("AudioUnknownTitle", C0446R.string.AudioUnknownTitle);
                }
                return z;
            }

            public int getDuration() {
                Document document;
                if (this.type == 0) {
                    document = this.messageOwner.media.webpage.document;
                } else {
                    document = this.messageOwner.media.document;
                }
                for (int i = 0; i < document.attributes.size(); i++) {
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (documentAttribute instanceof TL_documentAttributeAudio) {
                        return documentAttribute.duration;
                    }
                    if (documentAttribute instanceof TL_documentAttributeVideo) {
                        return documentAttribute.duration;
                    }
                }
                return this.audioPlayerDuration;
            }

            public String getMusicAuthor() {
                return getMusicAuthor(true);
            }

            public String getMusicAuthor(boolean z) {
                Document document;
                if (this.type == 0) {
                    document = this.messageOwner.media.webpage.document;
                } else {
                    document = this.messageOwner.media.document;
                }
                int i = 0;
                int i2 = 0;
                while (i < document.attributes.size()) {
                    User user;
                    User user2;
                    Chat chat;
                    DocumentAttribute documentAttribute = (DocumentAttribute) document.attributes.get(i);
                    if (!(documentAttribute instanceof TL_documentAttributeAudio)) {
                        if ((documentAttribute instanceof TL_documentAttributeVideo) && documentAttribute.round_message) {
                        }
                        if (i2 != 0) {
                            user = null;
                            if (!z) {
                                return null;
                            }
                            if (isOutOwner()) {
                                if (this.messageOwner.fwd_from != null || this.messageOwner.fwd_from.from_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                                    if (this.messageOwner.fwd_from != null || this.messageOwner.fwd_from.channel_id == 0) {
                                        if (this.messageOwner.fwd_from == null && this.messageOwner.fwd_from.from_id != 0) {
                                            user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                                        } else if (this.messageOwner.from_id >= 0) {
                                            chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-this.messageOwner.from_id));
                                        } else {
                                            user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                                        }
                                        User user3 = user2;
                                        chat = null;
                                        user = user3;
                                    } else {
                                        chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                                    }
                                    if (user == null) {
                                        return UserObject.getUserName(user);
                                    }
                                    if (chat == null) {
                                        return chat.title;
                                    }
                                }
                            }
                            return LocaleController.getString("FromYou", C0446R.string.FromYou);
                        }
                        i++;
                    } else if (!documentAttribute.voice) {
                        String str = documentAttribute.performer;
                        if (TextUtils.isEmpty(str) && z) {
                            str = LocaleController.getString("AudioUnknownArtist", C0446R.string.AudioUnknownArtist);
                        }
                        return str;
                    }
                    i2 = 1;
                    if (i2 != 0) {
                        user = null;
                        if (!z) {
                            return null;
                        }
                        if (isOutOwner()) {
                            if (this.messageOwner.fwd_from != null) {
                            }
                            if (this.messageOwner.fwd_from != null) {
                            }
                            if (this.messageOwner.fwd_from == null) {
                            }
                            if (this.messageOwner.from_id >= 0) {
                                user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.messageOwner.from_id));
                                User user32 = user2;
                                chat = null;
                                user = user32;
                                if (user == null) {
                                    return UserObject.getUserName(user);
                                }
                                if (chat == null) {
                                    return chat.title;
                                }
                            } else {
                                chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-this.messageOwner.from_id));
                                if (user == null) {
                                    return UserObject.getUserName(user);
                                }
                                if (chat == null) {
                                    return chat.title;
                                }
                            }
                        }
                        return LocaleController.getString("FromYou", C0446R.string.FromYou);
                    }
                    i++;
                }
                return LocaleController.getString("AudioUnknownArtist", C0446R.string.AudioUnknownArtist);
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
                return ((message.flags & 4) == 0 || message.fwd_from == null) ? null : true;
            }

            public boolean isReply() {
                return (this.replyMessageObject == null || !(this.replyMessageObject.messageOwner instanceof TL_messageEmpty)) && !((this.messageOwner.reply_to_msg_id == 0 && this.messageOwner.reply_to_random_id == 0) || (this.messageOwner.flags & 8) == 0);
            }

            public boolean isMediaEmpty() {
                return isMediaEmpty(this.messageOwner);
            }

            public static boolean isMediaEmpty(Message message) {
                if (!(message == null || message.media == null || (message.media instanceof TL_messageMediaEmpty))) {
                    if ((message.media instanceof TL_messageMediaWebPage) == null) {
                        return null;
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

            public static boolean canEditMessageAnytime(int i, Message message, Chat chat) {
                if (!(message == null || message.to_id == null || ((message.media != null && (isRoundVideoDocument(message.media.document) || isStickerDocument(message.media.document))) || ((message.action != null && !(message.action instanceof TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0)))) {
                    if (message.id >= 0) {
                        if (message.from_id == message.to_id.user_id && message.from_id == UserConfig.getInstance(i).getClientUserId() && isLiveLocationMessage(message) == 0) {
                            return true;
                        }
                        if (chat == null && message.to_id.channel_id != 0) {
                            chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Integer.valueOf(message.to_id.channel_id));
                            if (chat == null) {
                                return false;
                            }
                        }
                        if (message.out == 0 || r6 == null || r6.megagroup == 0 || (r6.creator == 0 && (r6.admin_rights == 0 || r6.admin_rights.pin_messages == 0))) {
                            return false;
                        }
                        return true;
                    }
                }
                return false;
            }

            public static boolean canEditMessage(int i, Message message, Chat chat) {
                boolean z = false;
                if (!(message == null || message.to_id == null || ((message.media != null && (isRoundVideoDocument(message.media.document) || isStickerDocument(message.media.document))) || ((message.action != null && !(message.action instanceof TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0)))) {
                    if (message.id >= 0) {
                        if (message.from_id == message.to_id.user_id && message.from_id == UserConfig.getInstance(i).getClientUserId() && !isLiveLocationMessage(message) && !(message.media instanceof TL_messageMediaContact)) {
                            return true;
                        }
                        if (chat == null && message.to_id.channel_id != 0) {
                            chat = MessagesController.getInstance(i).getChat(Integer.valueOf(message.to_id.channel_id));
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
                        if (Math.abs(message.date - ConnectionsManager.getInstance(i).getCurrentTime()) > MessagesController.getInstance(i).maxEditTime) {
                            return false;
                        }
                        if (message.to_id.channel_id == 0) {
                            if ((message.out != null || message.from_id == UserConfig.getInstance(i).getClientUserId()) && ((message.media instanceof TL_messageMediaPhoto) != 0 || (((message.media instanceof TL_messageMediaDocument) != 0 && isStickerMessage(message) == 0) || (message.media instanceof TL_messageMediaEmpty) != 0 || (message.media instanceof TL_messageMediaWebPage) != 0 || message.media == 0))) {
                                z = true;
                            }
                            return z;
                        } else if (((chat.megagroup == 0 || message.out == 0) && (chat.megagroup != 0 || ((chat.creator == 0 && (chat.admin_rights == 0 || (chat.admin_rights.edit_messages == 0 && message.out == 0))) || message.post == 0))) || ((message.media instanceof TL_messageMediaPhoto) == 0 && (((message.media instanceof TL_messageMediaDocument) == 0 || isStickerMessage(message) != 0) && (message.media instanceof TL_messageMediaEmpty) == 0 && (message.media instanceof TL_messageMediaWebPage) == 0 && message.media != 0))) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                }
                return false;
            }

            public boolean canDeleteMessage(Chat chat) {
                return (this.eventId != 0 || canDeleteMessage(this.currentAccount, this.messageOwner, chat) == null) ? null : true;
            }

            public static boolean canDeleteMessage(int i, Message message, Chat chat) {
                if (message.id < 0) {
                    return true;
                }
                if (chat == null && message.to_id.channel_id != 0) {
                    chat = MessagesController.getInstance(i).getChat(Integer.valueOf(message.to_id.channel_id));
                }
                boolean z = false;
                if (ChatObject.isChannel(chat) != 0) {
                    if (!(message.id == 1 || (chat.creator == 0 && ((chat.admin_rights == 0 || (chat.admin_rights.delete_messages == 0 && message.out == 0)) && (chat.megagroup == 0 || message.out == 0 || message.from_id <= 0))))) {
                        z = true;
                    }
                    return z;
                }
                if (isOut(message) != 0 || ChatObject.isChannel(chat) == 0) {
                    z = true;
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
                File pathToMessage;
                StringBuilder stringBuilder;
                if (this.type != 1) {
                    if (!(this.type == 8 || this.type == 3 || this.type == 9 || this.type == 2 || this.type == 14)) {
                        if (this.type != 5) {
                            TLObject document = getDocument();
                            if (document != null) {
                                this.mediaExists = FileLoader.getPathToAttach(document).exists();
                            } else if (this.type == 0) {
                                document = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                                if (!(document == null || document == null)) {
                                    this.mediaExists = FileLoader.getPathToAttach(document, true).exists();
                                }
                            }
                        }
                    }
                    if (this.messageOwner.attachPath != null && this.messageOwner.attachPath.length() > 0) {
                        this.attachPathExists = new File(this.messageOwner.attachPath).exists();
                    }
                    if (!this.attachPathExists) {
                        pathToMessage = FileLoader.getPathToMessage(this.messageOwner);
                        if (this.type == 3 && needDrawBluredPreview()) {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append(pathToMessage.getAbsolutePath());
                            stringBuilder.append(".enc");
                            this.mediaExists = new File(stringBuilder.toString()).exists();
                        }
                        if (!this.mediaExists) {
                            this.mediaExists = pathToMessage.exists();
                        }
                    }
                } else if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
                    pathToMessage = FileLoader.getPathToMessage(this.messageOwner);
                    if (needDrawBluredPreview()) {
                        stringBuilder = new StringBuilder();
                        stringBuilder.append(pathToMessage.getAbsolutePath());
                        stringBuilder.append(".enc");
                        this.mediaExists = new File(stringBuilder.toString()).exists();
                    }
                    if (!this.mediaExists) {
                        this.mediaExists = pathToMessage.exists();
                    }
                }
            }
        }
