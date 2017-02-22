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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.Emoji.EmojiSpan;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_game;
import org.telegram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.telegram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.telegram.tgnet.TLRPC.TL_message;
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
import org.telegram.tgnet.TLRPC.TL_messageActionEmpty;
import org.telegram.tgnet.TLRPC.TL_messageActionGameScore;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.telegram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.telegram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageActionTTLChange;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.telegram.tgnet.TLRPC.TL_messageEmpty;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_messageEntityBold;
import org.telegram.tgnet.TLRPC.TL_messageEntityBotCommand;
import org.telegram.tgnet.TLRPC.TL_messageEntityCode;
import org.telegram.tgnet.TLRPC.TL_messageEntityEmail;
import org.telegram.tgnet.TLRPC.TL_messageEntityHashtag;
import org.telegram.tgnet.TLRPC.TL_messageEntityItalic;
import org.telegram.tgnet.TLRPC.TL_messageEntityMention;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageEntityPre;
import org.telegram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.telegram.tgnet.TLRPC.TL_messageEntityUrl;
import org.telegram.tgnet.TLRPC.TL_messageForwarded_old;
import org.telegram.tgnet.TLRPC.TL_messageForwarded_old2;
import org.telegram.tgnet.TLRPC.TL_messageMediaContact;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaGame;
import org.telegram.tgnet.TLRPC.TL_messageMediaGeo;
import org.telegram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaVenue;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_message_old;
import org.telegram.tgnet.TLRPC.TL_message_old2;
import org.telegram.tgnet.TLRPC.TL_message_old3;
import org.telegram.tgnet.TLRPC.TL_message_old4;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.TL_webDocument;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;

public class MessageObject {
    private static final int LINES_PER_BLOCK = 2;
    public static final int MESSAGE_SEND_STATE_SENDING = 1;
    public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
    public static final int MESSAGE_SEND_STATE_SENT = 0;
    public static Pattern urlPattern;
    public boolean attachPathExists;
    public float audioProgress;
    public int audioProgressSec;
    public CharSequence caption;
    public int contentType;
    public String customReplyName;
    public String dateKey;
    public boolean deleted;
    public boolean forceUpdate;
    private int generatedWithMinSize;
    public boolean isDateObject;
    public int lastLineWidth;
    private boolean layoutCreated;
    public CharSequence linkDescription;
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
    public int type;
    public boolean useCustomPhoto;
    public VideoEditedInfo videoEditedInfo;
    public boolean viewsReloaded;
    public int wantedBotKeyboardWidth;

    public static class TextLayoutBlock {
        public int charactersEnd;
        public int charactersOffset;
        public int height;
        public StaticLayout textLayout;
        public float textXOffset;
        public float textYOffset;
    }

    public MessageObject(Message message, AbstractMap<Integer, User> users, boolean generateLayout) {
        this(message, users, null, generateLayout);
    }

    public MessageObject(Message message, AbstractMap<Integer, User> users, AbstractMap<Integer, Chat> chats, boolean generateLayout) {
        this.type = 1000;
        Theme.createChatResources(null, true);
        this.messageOwner = message;
        if (message.replyMessage != null) {
            this.replyMessageObject = new MessageObject(message.replyMessage, users, chats, false);
        }
        User fromUser = null;
        if (message.from_id > 0) {
            if (users != null) {
                fromUser = (User) users.get(Integer.valueOf(message.from_id));
            }
            if (fromUser == null) {
                fromUser = MessagesController.getInstance().getUser(Integer.valueOf(message.from_id));
            }
        }
        String name;
        if (message instanceof TL_messageService) {
            if (message.action != null) {
                if (message.action instanceof TL_messageActionChatCreate) {
                    if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouCreateGroup", R.string.ActionYouCreateGroup);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", R.string.ActionCreateGroup), "un1", fromUser);
                    }
                } else if (message.action instanceof TL_messageActionChatDeleteUser) {
                    if (message.action.user_id != message.from_id) {
                        whoUser = null;
                        if (users != null) {
                            whoUser = (User) users.get(Integer.valueOf(message.action.user_id));
                        }
                        if (whoUser == null) {
                            whoUser = MessagesController.getInstance().getUser(Integer.valueOf(message.action.user_id));
                        }
                        if (isOut()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", R.string.ActionYouKickUser), "un2", whoUser);
                        } else {
                            if (message.action.user_id == UserConfig.getClientUserId()) {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", R.string.ActionKickUserYou), "un1", fromUser);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", R.string.ActionKickUser), "un2", whoUser);
                                this.messageText = replaceWithLink(this.messageText, "un1", fromUser);
                            }
                        }
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouLeftUser", R.string.ActionYouLeftUser);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", R.string.ActionLeftUser), "un1", fromUser);
                    }
                } else if (message.action instanceof TL_messageActionChatAddUser) {
                    int singleUserId = this.messageOwner.action.user_id;
                    if (singleUserId == 0 && this.messageOwner.action.users.size() == 1) {
                        singleUserId = ((Integer) this.messageOwner.action.users.get(0)).intValue();
                    }
                    if (singleUserId != 0) {
                        whoUser = null;
                        if (users != null) {
                            whoUser = (User) users.get(Integer.valueOf(singleUserId));
                        }
                        if (whoUser == null) {
                            whoUser = MessagesController.getInstance().getUser(Integer.valueOf(singleUserId));
                        }
                        if (singleUserId == message.from_id) {
                            if (message.to_id.channel_id != 0 && !isMegagroup()) {
                                this.messageText = LocaleController.getString("ChannelJoined", R.string.ChannelJoined);
                            } else if (message.to_id.channel_id == 0 || !isMegagroup()) {
                                if (isOut()) {
                                    this.messageText = LocaleController.getString("ActionAddUserSelfYou", R.string.ActionAddUserSelfYou);
                                } else {
                                    this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", R.string.ActionAddUserSelf), "un1", fromUser);
                                }
                            } else if (singleUserId == UserConfig.getClientUserId()) {
                                this.messageText = LocaleController.getString("ChannelMegaJoined", R.string.ChannelMegaJoined);
                            } else {
                                this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", R.string.ActionAddUserSelfMega), "un1", fromUser);
                            }
                        } else if (isOut()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", R.string.ActionYouAddUser), "un2", whoUser);
                        } else if (singleUserId != UserConfig.getClientUserId()) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", R.string.ActionAddUser), "un2", whoUser);
                            this.messageText = replaceWithLink(this.messageText, "un1", fromUser);
                        } else if (message.to_id.channel_id == 0) {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", R.string.ActionAddUserYou), "un1", fromUser);
                        } else if (isMegagroup()) {
                            this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", R.string.MegaAddedBy), "un1", fromUser);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", R.string.ChannelAddedBy), "un1", fromUser);
                        }
                    } else if (isOut()) {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", R.string.ActionYouAddUser), "un2", message.action.users, users);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", R.string.ActionAddUser), "un2", message.action.users, users);
                        this.messageText = replaceWithLink(this.messageText, "un1", fromUser);
                    }
                } else if (message.action instanceof TL_messageActionChatJoinedByLink) {
                    if (isOut()) {
                        this.messageText = LocaleController.getString("ActionInviteYou", R.string.ActionInviteYou);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", R.string.ActionInviteUser), "un1", fromUser);
                    }
                } else if (message.action instanceof TL_messageActionChatEditPhoto) {
                    if (message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelChangedPhoto", R.string.ActionChannelChangedPhoto);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouChangedPhoto", R.string.ActionYouChangedPhoto);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", R.string.ActionChangedPhoto), "un1", fromUser);
                    }
                } else if (message.action instanceof TL_messageActionChatEditTitle) {
                    if (message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelChangedTitle", R.string.ActionChannelChangedTitle).replace("un2", message.action.title);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouChangedTitle", R.string.ActionYouChangedTitle).replace("un2", message.action.title);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", R.string.ActionChangedTitle).replace("un2", message.action.title), "un1", fromUser);
                    }
                } else if (message.action instanceof TL_messageActionChatDeletePhoto) {
                    if (message.to_id.channel_id != 0 && !isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", R.string.ActionChannelRemovedPhoto);
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("ActionYouRemovedPhoto", R.string.ActionYouRemovedPhoto);
                    } else {
                        this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", R.string.ActionRemovedPhoto), "un1", fromUser);
                    }
                } else if (message.action instanceof TL_messageActionTTLChange) {
                    if (message.action.ttl != 0) {
                        if (isOut()) {
                            this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", R.string.MessageLifetimeChangedOutgoing, LocaleController.formatTTLString(message.action.ttl));
                        } else {
                            this.messageText = LocaleController.formatString("MessageLifetimeChanged", R.string.MessageLifetimeChanged, UserObject.getFirstName(fromUser), LocaleController.formatTTLString(message.action.ttl));
                        }
                    } else if (isOut()) {
                        this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", R.string.MessageLifetimeYouRemoved);
                    } else {
                        this.messageText = LocaleController.formatString("MessageLifetimeRemoved", R.string.MessageLifetimeRemoved, UserObject.getFirstName(fromUser));
                    }
                } else if (message.action instanceof TL_messageActionLoginUnknownLocation) {
                    String date;
                    long time = ((long) message.date) * 1000;
                    if (LocaleController.getInstance().formatterDay == null || LocaleController.getInstance().formatterYear == null) {
                        date = "" + message.date;
                    } else {
                        date = LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(time), LocaleController.getInstance().formatterDay.format(time));
                    }
                    User to_user = UserConfig.getCurrentUser();
                    if (to_user == null) {
                        if (users != null) {
                            to_user = (User) users.get(Integer.valueOf(this.messageOwner.to_id.user_id));
                        }
                        if (to_user == null) {
                            to_user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
                        }
                    }
                    name = to_user != null ? UserObject.getFirstName(to_user) : "";
                    this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", R.string.NotificationUnrecognizedDevice, name, date, message.action.title, message.action.address);
                } else if (message.action instanceof TL_messageActionUserJoined) {
                    this.messageText = LocaleController.formatString("NotificationContactJoined", R.string.NotificationContactJoined, UserObject.getUserName(fromUser));
                } else if (message.action instanceof TL_messageActionUserUpdatedPhoto) {
                    this.messageText = LocaleController.formatString("NotificationContactNewPhoto", R.string.NotificationContactNewPhoto, UserObject.getUserName(fromUser));
                } else if (message.action instanceof TL_messageEncryptedAction) {
                    if (message.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) {
                        if (isOut()) {
                            this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", R.string.ActionTakeScreenshootYou, new Object[0]);
                        } else {
                            this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", R.string.ActionTakeScreenshoot), "un1", fromUser);
                        }
                    } else if (message.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL) {
                        if (message.action.encryptedAction.ttl_seconds != 0) {
                            if (isOut()) {
                                this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", R.string.MessageLifetimeChangedOutgoing, LocaleController.formatTTLString(action.ttl_seconds));
                            } else {
                                this.messageText = LocaleController.formatString("MessageLifetimeChanged", R.string.MessageLifetimeChanged, UserObject.getFirstName(fromUser), LocaleController.formatTTLString(action.ttl_seconds));
                            }
                        } else if (isOut()) {
                            this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", R.string.MessageLifetimeYouRemoved);
                        } else {
                            this.messageText = LocaleController.formatString("MessageLifetimeRemoved", R.string.MessageLifetimeRemoved, UserObject.getFirstName(fromUser));
                        }
                    }
                } else if (message.action instanceof TL_messageActionCreatedBroadcastList) {
                    this.messageText = LocaleController.formatString("YouCreatedBroadcastList", R.string.YouCreatedBroadcastList, new Object[0]);
                } else if (message.action instanceof TL_messageActionChannelCreate) {
                    if (isMegagroup()) {
                        this.messageText = LocaleController.getString("ActionCreateMega", R.string.ActionCreateMega);
                    } else {
                        this.messageText = LocaleController.getString("ActionCreateChannel", R.string.ActionCreateChannel);
                    }
                } else if (message.action instanceof TL_messageActionChatMigrateTo) {
                    this.messageText = LocaleController.getString("ActionMigrateFromGroup", R.string.ActionMigrateFromGroup);
                } else if (message.action instanceof TL_messageActionChannelMigrateFrom) {
                    this.messageText = LocaleController.getString("ActionMigrateFromGroup", R.string.ActionMigrateFromGroup);
                } else if (message.action instanceof TL_messageActionPinMessage) {
                    generatePinMessageText(fromUser, fromUser == null ? (Chat) chats.get(Integer.valueOf(message.to_id.channel_id)) : null);
                } else if (message.action instanceof TL_messageActionHistoryClear) {
                    this.messageText = LocaleController.getString("HistoryCleared", R.string.HistoryCleared);
                } else if (message.action instanceof TL_messageActionGameScore) {
                    generateGameMessageText(fromUser);
                } else if (message.action instanceof TL_messageActionPhoneCall) {
                    TL_messageActionPhoneCall call = this.messageOwner.action;
                    boolean isMissed = call.reason instanceof TL_phoneCallDiscardReasonMissed;
                    if (this.messageOwner.from_id == UserConfig.getClientUserId()) {
                        if (isMissed) {
                            this.messageText = LocaleController.getString("CallMessageOutgoingMissed", R.string.CallMessageOutgoingMissed);
                        } else {
                            this.messageText = LocaleController.getString("CallMessageOutgoing", R.string.CallMessageOutgoing);
                        }
                    } else if (isMissed) {
                        this.messageText = LocaleController.getString("CallMessageIncomingMissed", R.string.CallMessageIncomingMissed);
                    } else {
                        this.messageText = LocaleController.getString("CallMessageIncoming", R.string.CallMessageIncoming);
                    }
                    if (call.duration > 0) {
                        String duration = formatDuration(call.duration);
                        this.messageText = LocaleController.formatString("CallMessageWithDuration", R.string.CallMessageWithDuration, this.messageText, duration);
                        String _messageText = this.messageText.toString();
                        int start = _messageText.indexOf(duration);
                        if (start != -1) {
                            CharSequence spannableString = new SpannableString(this.messageText);
                            int end = start + duration.length();
                            if (start > 0 && _messageText.charAt(start - 1) == '(') {
                                start--;
                            }
                            if (end < _messageText.length() && _messageText.charAt(end) == ')') {
                                end++;
                            }
                            spannableString.setSpan(new TypefaceSpan(Typeface.DEFAULT), start, end, 0);
                            this.messageText = spannableString;
                        }
                    }
                } else if (message.action instanceof TL_messageActionPaymentSent) {
                    int uid = (int) getDialogId();
                    if (users != null) {
                        fromUser = (User) users.get(Integer.valueOf(uid));
                    }
                    if (fromUser == null) {
                        fromUser = MessagesController.getInstance().getUser(Integer.valueOf(uid));
                    }
                    generatePaymentSentMessageText(null);
                }
            }
        } else if (isMediaEmpty()) {
            this.messageText = message.message;
        } else if (message.media instanceof TL_messageMediaPhoto) {
            this.messageText = LocaleController.getString("AttachPhoto", R.string.AttachPhoto);
        } else if (isVideo()) {
            this.messageText = LocaleController.getString("AttachVideo", R.string.AttachVideo);
        } else if (isVoice()) {
            this.messageText = LocaleController.getString("AttachAudio", R.string.AttachAudio);
        } else if ((message.media instanceof TL_messageMediaGeo) || (message.media instanceof TL_messageMediaVenue)) {
            this.messageText = LocaleController.getString("AttachLocation", R.string.AttachLocation);
        } else if (message.media instanceof TL_messageMediaContact) {
            this.messageText = LocaleController.getString("AttachContact", R.string.AttachContact);
        } else if (message.media instanceof TL_messageMediaGame) {
            this.messageText = message.message;
        } else if (message.media instanceof TL_messageMediaInvoice) {
            this.messageText = message.media.description;
        } else if (message.media instanceof TL_messageMediaUnsupported) {
            this.messageText = LocaleController.getString("UnsupportedMedia", R.string.UnsupportedMedia);
        } else if (message.media instanceof TL_messageMediaDocument) {
            if (isSticker()) {
                String sch = getStrickerChar();
                if (sch == null || sch.length() <= 0) {
                    this.messageText = LocaleController.getString("AttachSticker", R.string.AttachSticker);
                } else {
                    this.messageText = String.format("%s %s", new Object[]{sch, LocaleController.getString("AttachSticker", R.string.AttachSticker)});
                }
            } else if (isMusic()) {
                this.messageText = LocaleController.getString("AttachMusic", R.string.AttachMusic);
            } else if (isGif()) {
                this.messageText = LocaleController.getString("AttachGif", R.string.AttachGif);
            } else {
                name = FileLoader.getDocumentFileName(message.media.document);
                if (name == null || name.length() <= 0) {
                    this.messageText = LocaleController.getString("AttachDocument", R.string.AttachDocument);
                } else {
                    this.messageText = name;
                }
            }
        }
        if (this.messageText == null) {
            this.messageText = "";
        }
        setType();
        measureInlineBotButtons();
        Calendar rightNow = new GregorianCalendar();
        rightNow.setTimeInMillis(((long) this.messageOwner.date) * 1000);
        int dateDay = rightNow.get(6);
        int dateYear = rightNow.get(1);
        int dateMonth = rightNow.get(2);
        this.dateKey = String.format("%d_%02d_%02d", new Object[]{Integer.valueOf(dateYear), Integer.valueOf(dateMonth), Integer.valueOf(dateDay)});
        this.monthKey = String.format("%d_%02d", new Object[]{Integer.valueOf(dateYear), Integer.valueOf(dateMonth)});
        if (this.messageOwner.message != null && this.messageOwner.id < 0 && this.messageOwner.message.length() > 6 && (isVideo() || isNewGif())) {
            this.videoEditedInfo = new VideoEditedInfo();
            if (!this.videoEditedInfo.parseString(this.messageOwner.message)) {
                this.videoEditedInfo = null;
            }
        }
        generateCaption();
        if (generateLayout) {
            TextPaint paint;
            if (this.messageOwner.media instanceof TL_messageMediaGame) {
                paint = Theme.chat_msgGameTextPaint;
            } else {
                paint = Theme.chat_msgTextPaint;
            }
            int[] emojiOnly = MessagesController.getInstance().allowBigEmoji ? new int[1] : null;
            this.messageText = Emoji.replaceEmoji(this.messageText, paint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false, emojiOnly);
            if (emojiOnly != null && emojiOnly[0] >= 1 && emojiOnly[0] <= 3) {
                TextPaint emojiPaint;
                int size;
                switch (emojiOnly[0]) {
                    case 1:
                        emojiPaint = Theme.chat_msgTextPaintOneEmoji;
                        size = AndroidUtilities.dp(32.0f);
                        break;
                    case 2:
                        emojiPaint = Theme.chat_msgTextPaintTwoEmoji;
                        size = AndroidUtilities.dp(28.0f);
                        break;
                    default:
                        emojiPaint = Theme.chat_msgTextPaintThreeEmoji;
                        size = AndroidUtilities.dp(24.0f);
                        break;
                }
                EmojiSpan[] spans = (EmojiSpan[]) ((Spannable) this.messageText).getSpans(0, this.messageText.length(), EmojiSpan.class);
                if (spans != null && spans.length > 0) {
                    for (EmojiSpan replaceFontMetrics : spans) {
                        replaceFontMetrics.replaceFontMetrics(emojiPaint.getFontMetricsInt(), size);
                    }
                }
            }
            generateLayout(fromUser);
        }
        this.layoutCreated = generateLayout;
        generateThumbs(false);
        checkMediaExistance();
    }

    public void applyNewText() {
        if (!TextUtils.isEmpty(this.messageOwner.message)) {
            TextPaint paint;
            User fromUser = null;
            if (isFromUser()) {
                fromUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
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
            fromUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
        }
        TL_game game = null;
        if (!(this.replyMessageObject == null || this.replyMessageObject.messageOwner.media == null || this.replyMessageObject.messageOwner.media.game == null)) {
            game = this.replyMessageObject.messageOwner.media.game;
        }
        if (game != null) {
            if (fromUser == null || fromUser.id != UserConfig.getClientUserId()) {
                this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", R.string.ActionUserScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", fromUser);
            } else {
                this.messageText = LocaleController.formatString("ActionYouScoredInGame", R.string.ActionYouScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
            }
            this.messageText = replaceWithLink(this.messageText, "un2", game);
        } else if (fromUser == null || fromUser.id != UserConfig.getClientUserId()) {
            this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", R.string.ActionUserScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score)), "un1", fromUser);
        } else {
            this.messageText = LocaleController.formatString("ActionYouScored", R.string.ActionYouScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score));
        }
    }

    public void generatePaymentSentMessageText(User fromUser) {
        String name;
        if (fromUser == null) {
            fromUser = MessagesController.getInstance().getUser(Integer.valueOf((int) getDialogId()));
        }
        if (fromUser != null) {
            name = UserObject.getFirstName(fromUser);
        } else {
            name = "";
        }
        if (this.replyMessageObject == null || !(this.replyMessageObject.messageOwner.media instanceof TL_messageMediaInvoice)) {
            this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", R.string.PaymentSuccessfullyPaidNoItem, LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), name);
            return;
        }
        this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", R.string.PaymentSuccessfullyPaid, LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), name, this.replyMessageObject.messageOwner.media.title);
    }

    private String formatDuration(int duration) {
        if (duration > 3600) {
            return LocaleController.formatPluralString("Hours", duration / 3600) + ", " + LocaleController.formatPluralString("Minutes", (duration % 3600) / 60);
        }
        if (duration > 60) {
            return LocaleController.formatPluralString("Minutes", duration / 60);
        }
        return LocaleController.formatPluralString("Seconds", duration);
    }

    public void generatePinMessageText(User fromUser, Chat chat) {
        if (fromUser == null && chat == null) {
            if (this.messageOwner.from_id > 0) {
                fromUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
            }
            if (fromUser == null) {
                TLObject chat2 = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
            }
        }
        CharSequence string;
        String str;
        TLObject fromUser2;
        if (this.replyMessageObject == null) {
            string = LocaleController.getString("ActionPinnedNoText", R.string.ActionPinnedNoText);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.isMusic()) {
            string = LocaleController.getString("ActionPinnedMusic", R.string.ActionPinnedMusic);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.isVideo()) {
            string = LocaleController.getString("ActionPinnedVideo", R.string.ActionPinnedVideo);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.isGif()) {
            string = LocaleController.getString("ActionPinnedGif", R.string.ActionPinnedGif);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.isVoice()) {
            string = LocaleController.getString("ActionPinnedVoice", R.string.ActionPinnedVoice);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.isSticker()) {
            string = LocaleController.getString("ActionPinnedSticker", R.string.ActionPinnedSticker);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaDocument) {
            string = LocaleController.getString("ActionPinnedFile", R.string.ActionPinnedFile);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGeo) {
            string = LocaleController.getString("ActionPinnedGeo", R.string.ActionPinnedGeo);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaContact) {
            string = LocaleController.getString("ActionPinnedContact", R.string.ActionPinnedContact);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaPhoto) {
            string = LocaleController.getString("ActionPinnedPhoto", R.string.ActionPinnedPhoto);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else if (this.replyMessageObject.messageOwner.media instanceof TL_messageMediaGame) {
            string = LocaleController.formatString("ActionPinnedGame", R.string.ActionPinnedGame, "🎮 " + this.replyMessageObject.messageOwner.media.game.title);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
            this.messageText = Emoji.replaceEmoji(this.messageText, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
        } else if (this.replyMessageObject.messageText == null || this.replyMessageObject.messageText.length() <= 0) {
            string = LocaleController.getString("ActionPinnedNoText", R.string.ActionPinnedNoText);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        } else {
            CharSequence mess = this.replyMessageObject.messageText;
            if (mess.length() > 20) {
                mess = mess.subSequence(0, 20) + "...";
            }
            mess = Emoji.replaceEmoji(mess, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            string = LocaleController.formatString("ActionPinnedText", R.string.ActionPinnedText, mess);
            str = "un1";
            if (fromUser == null) {
                fromUser2 = chat2;
            }
            this.messageText = replaceWithLink(string, str, fromUser);
        }
    }

    public void measureInlineBotButtons() {
        this.wantedBotKeyboardWidth = 0;
        if (this.messageOwner.reply_markup instanceof TL_replyInlineMarkup) {
            Theme.createChatResources(null, true);
            for (int a = 0; a < this.messageOwner.reply_markup.rows.size(); a++) {
                TL_keyboardButtonRow row = (TL_keyboardButtonRow) this.messageOwner.reply_markup.rows.get(a);
                int maxButtonSize = 0;
                int size = row.buttons.size();
                for (int b = 0; b < size; b++) {
                    CharSequence text;
                    KeyboardButton button = (KeyboardButton) row.buttons.get(b);
                    if (!(button instanceof TL_keyboardButtonBuy) || (this.messageOwner.media.flags & 4) == 0) {
                        text = Emoji.replaceEmoji(button.text, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0f), false);
                    } else {
                        text = LocaleController.getString("PaymentReceipt", R.string.PaymentReceipt);
                    }
                    StaticLayout staticLayout = new StaticLayout(text, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    if (staticLayout.getLineCount() > 0) {
                        maxButtonSize = Math.max(maxButtonSize, ((int) Math.ceil((double) (staticLayout.getLineWidth(0) - staticLayout.getLineLeft(0)))) + AndroidUtilities.dp(4.0f));
                    }
                }
                this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((AndroidUtilities.dp(12.0f) + maxButtonSize) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
            }
        }
    }

    public void setType() {
        int oldType = this.type;
        if ((this.messageOwner instanceof TL_message) || (this.messageOwner instanceof TL_messageForwarded_old2)) {
            if (isMediaEmpty()) {
                this.type = 0;
                if (this.messageText == null || this.messageText.length() == 0) {
                    this.messageText = "Empty message";
                }
            } else if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
                this.type = 1;
            } else if ((this.messageOwner.media instanceof TL_messageMediaGeo) || (this.messageOwner.media instanceof TL_messageMediaVenue)) {
                this.type = 4;
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
        } else if (this.messageOwner instanceof TL_messageService) {
            if (this.messageOwner.action instanceof TL_messageActionLoginUnknownLocation) {
                this.type = 0;
            } else if ((this.messageOwner.action instanceof TL_messageActionChatEditPhoto) || (this.messageOwner.action instanceof TL_messageActionUserUpdatedPhoto)) {
                this.contentType = 1;
                this.type = 11;
            } else if (this.messageOwner.action instanceof TL_messageEncryptedAction) {
                if ((this.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionScreenshotMessages) || (this.messageOwner.action.encryptedAction instanceof TL_decryptedMessageActionSetMessageTTL)) {
                    this.contentType = 1;
                    this.type = 10;
                } else {
                    this.contentType = -1;
                    this.type = -1;
                }
            } else if (this.messageOwner.action instanceof TL_messageActionHistoryClear) {
                this.contentType = -1;
                this.type = -1;
            } else {
                this.contentType = 1;
                this.type = 10;
            }
        }
        if (oldType != 1000 && oldType != this.type) {
            generateThumbs(false);
        }
    }

    public boolean checkLayout() {
        if (this.type != 0 || this.messageOwner.to_id == null || this.messageText == null || this.messageText.length() == 0) {
            return false;
        }
        if (this.layoutCreated) {
            int newMinSize;
            if (AndroidUtilities.isTablet()) {
                newMinSize = AndroidUtilities.getMinTabletSide();
            } else {
                newMinSize = AndroidUtilities.displaySize.x;
            }
            if (Math.abs(this.generatedWithMinSize - newMinSize) > AndroidUtilities.dp(52.0f)) {
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
            fromUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
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

    public static boolean isGifDocument(Document document) {
        return (document == null || document.thumb == null || document.mime_type == null || (!document.mime_type.equals("image/gif") && !isNewGifDocument(document))) ? false : true;
    }

    public static boolean isNewGifDocument(Document document) {
        if (!(document == null || document.mime_type == null || !document.mime_type.equals(MimeTypes.VIDEO_MP4))) {
            for (int a = 0; a < document.attributes.size(); a++) {
                if (document.attributes.get(a) instanceof TL_documentAttributeAnimated) {
                    return true;
                }
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
                        if (!(size instanceof TL_photoSizeEmpty) && size.type.equals(photoObject.type)) {
                            photoObject.location = size.location;
                            break;
                        }
                    }
                }
            }
        } else if (this.messageOwner.media != null && !(this.messageOwner.media instanceof TL_messageMediaEmpty)) {
            if (this.messageOwner.media instanceof TL_messageMediaPhoto) {
                if (!update || (this.photoThumbs != null && this.photoThumbs.size() != this.messageOwner.media.photo.sizes.size())) {
                    this.photoThumbs = new ArrayList(this.messageOwner.media.photo.sizes);
                } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty()) {
                    for (a = 0; a < this.photoThumbs.size(); a++) {
                        photoObject = (PhotoSize) this.photoThumbs.get(a);
                        for (b = 0; b < this.messageOwner.media.photo.sizes.size(); b++) {
                            size = (PhotoSize) this.messageOwner.media.photo.sizes.get(b);
                            if (!(size instanceof TL_photoSizeEmpty) && size.type.equals(photoObject.type)) {
                                photoObject.location = size.location;
                                break;
                            }
                        }
                    }
                }
            } else if (this.messageOwner.media instanceof TL_messageMediaDocument) {
                if (!(this.messageOwner.media.document.thumb instanceof TL_photoSizeEmpty)) {
                    if (!update) {
                        this.photoThumbs = new ArrayList();
                        this.photoThumbs.add(this.messageOwner.media.document.thumb);
                    } else if (this.photoThumbs != null && !this.photoThumbs.isEmpty() && this.messageOwner.media.document.thumb != null) {
                        photoObject = (PhotoSize) this.photoThumbs.get(0);
                        photoObject.location = this.messageOwner.media.document.thumb.location;
                        photoObject.w = this.messageOwner.media.document.thumb.w;
                        photoObject.h = this.messageOwner.media.document.thumb.h;
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
                    if (!update || this.photoThumbs2 == null) {
                        this.photoThumbs2 = new ArrayList(this.messageOwner.media.game.photo.sizes);
                    } else if (!this.photoThumbs2.isEmpty()) {
                        for (a = 0; a < this.photoThumbs2.size(); a++) {
                            photoObject = (PhotoSize) this.photoThumbs2.get(a);
                            for (b = 0; b < this.messageOwner.media.game.photo.sizes.size(); b++) {
                                size = (PhotoSize) this.messageOwner.media.game.photo.sizes.get(b);
                                if (!(size instanceof TL_photoSizeEmpty) && size.type.equals(photoObject.type)) {
                                    photoObject.location = size.location;
                                    break;
                                }
                            }
                        }
                    }
                }
                if (this.photoThumbs == null && this.photoThumbs2 != null) {
                    this.photoThumbs = this.photoThumbs2;
                    this.photoThumbs2 = null;
                }
            } else if (!(this.messageOwner.media instanceof TL_messageMediaWebPage)) {
            } else {
                if (this.messageOwner.media.webpage.photo != null) {
                    if (!update || this.photoThumbs == null) {
                        this.photoThumbs = new ArrayList(this.messageOwner.media.webpage.photo.sizes);
                    } else if (!this.photoThumbs.isEmpty()) {
                        for (a = 0; a < this.photoThumbs.size(); a++) {
                            photoObject = (PhotoSize) this.photoThumbs.get(a);
                            for (b = 0; b < this.messageOwner.media.webpage.photo.sizes.size(); b++) {
                                size = (PhotoSize) this.messageOwner.media.webpage.photo.sizes.get(b);
                                if (!(size instanceof TL_photoSizeEmpty) && size.type.equals(photoObject.type)) {
                                    photoObject.location = size.location;
                                    break;
                                }
                            }
                        }
                    }
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

    public CharSequence replaceWithLink(CharSequence source, String param, ArrayList<Integer> uids, AbstractMap<Integer, User> usersDict) {
        if (TextUtils.indexOf(source, param) < 0) {
            return source;
        }
        SpannableStringBuilder names = new SpannableStringBuilder("");
        for (int a = 0; a < uids.size(); a++) {
            User user = null;
            if (usersDict != null) {
                user = (User) usersDict.get(uids.get(a));
            }
            if (user == null) {
                user = MessagesController.getInstance().getUser((Integer) uids.get(a));
            }
            if (user != null) {
                String name = UserObject.getUserName(user);
                int start = names.length();
                if (names.length() != 0) {
                    names.append(", ");
                }
                names.append(name);
                names.setSpan(new URLSpanNoUnderlineBold("" + user.id), start, name.length() + start, 33);
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
        if (object instanceof User) {
            name = UserObject.getUserName((User) object);
            id = "" + ((User) object).id;
        } else if (object instanceof Chat) {
            name = ((Chat) object).title;
            id = "" + (-((Chat) object).id);
        } else if (object instanceof TL_game) {
            name = ((TL_game) object).title;
            id = "game";
        } else {
            name = "";
            id = "0";
        }
        SpannableStringBuilder builder = new SpannableStringBuilder(TextUtils.replace(source, new String[]{param}, new String[]{name}));
        builder.setSpan(new URLSpanNoUnderlineBold("" + id), start, name.length() + start, 33);
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
            ext = "";
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
        return "";
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
        if (message == null || message.length() < 2 || message.length() > 20480) {
            return false;
        }
        int length = message.length();
        int digitsInRow = 0;
        int schemeSequence = 0;
        int dotSequence = 0;
        char lastChar = '\u0000';
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
            if ((c == '@' || c == '#' || c == '/') && i == 0) {
                return true;
            }
            if (i != 0 && (message.charAt(i - 1) == ' ' || message.charAt(i - 1) == '\n')) {
                return true;
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
                        FileLog.e(e);
                    }
                }
                this.linkDescription = Emoji.replaceEmoji(this.linkDescription, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            }
        }
    }

    public void generateCaption() {
        if (this.caption == null && this.messageOwner.media != null && this.messageOwner.media.caption != null && this.messageOwner.media.caption.length() > 0) {
            this.caption = Emoji.replaceEmoji(this.messageOwner.media.caption, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false);
            if (containsUrls(this.caption)) {
                try {
                    Linkify.addLinks((Spannable) this.caption, 5);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
                addUsernamesAndHashtags(isOutOwner(), this.caption, true);
            }
        }
    }

    private static void addUsernamesAndHashtags(boolean isOut, CharSequence charSequence, boolean botCommands) {
        try {
            if (urlPattern == null) {
                urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s)@[a-zA-Z\\d_]{1,32}|(^|\\s)#[\\w\\.]+");
            }
            Matcher matcher = urlPattern.matcher(charSequence);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                if (!(charSequence.charAt(start) == '@' || charSequence.charAt(start) == '#' || charSequence.charAt(start) == '/')) {
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
            FileLog.e(e);
        }
    }

    public static void addLinks(boolean isOut, CharSequence messageText) {
        addLinks(isOut, messageText, true);
    }

    public static void addLinks(boolean isOut, CharSequence messageText, boolean botCommands) {
        if ((messageText instanceof Spannable) && containsUrls(messageText)) {
            if (messageText.length() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                try {
                    Linkify.addLinks((Spannable) messageText, 5);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            } else {
                try {
                    Linkify.addLinks((Spannable) messageText, 1);
                } catch (Throwable e2) {
                    FileLog.e(e2);
                }
            }
            addUsernamesAndHashtags(isOut, messageText, botCommands);
        }
    }

    public void generateLayout(User fromUser) {
        if (this.type == 0 && this.messageOwner.to_id != null && this.messageText != null && this.messageText.length() != 0) {
            boolean hasEntities;
            int a;
            TextPaint paint;
            generateLinkDescription();
            this.textLayoutBlocks = new ArrayList();
            this.textWidth = 0;
            if (this.messageOwner.send_state != 0) {
                hasEntities = false;
                for (a = 0; a < this.messageOwner.entities.size(); a++) {
                    if (!(this.messageOwner.entities.get(a) instanceof TL_inputMessageEntityMentionName)) {
                        hasEntities = true;
                        break;
                    }
                }
            } else {
                hasEntities = !this.messageOwner.entities.isEmpty();
            }
            boolean useManualParse = !hasEntities && ((this.messageOwner instanceof TL_message_old) || (this.messageOwner instanceof TL_message_old2) || (this.messageOwner instanceof TL_message_old3) || (this.messageOwner instanceof TL_message_old4) || (this.messageOwner instanceof TL_messageForwarded_old) || (this.messageOwner instanceof TL_messageForwarded_old2) || (this.messageOwner instanceof TL_message_secret) || (this.messageOwner.media instanceof TL_messageMediaInvoice) || ((isOut() && this.messageOwner.send_state != 0) || this.messageOwner.id < 0 || (this.messageOwner.media instanceof TL_messageMediaUnsupported)));
            if (useManualParse) {
                addLinks(isOutOwner(), this.messageText);
            } else if ((this.messageText instanceof Spannable) && this.messageText.length() < Callback.DEFAULT_DRAG_ANIMATION_DURATION) {
                try {
                    Linkify.addLinks((Spannable) this.messageText, 4);
                } catch (Throwable e) {
                    FileLog.e(e);
                }
            }
            if (this.messageText instanceof Spannable) {
                Spannable spannable = (Spannable) this.messageText;
                int count = this.messageOwner.entities.size();
                URLSpan[] spans = (URLSpan[]) spannable.getSpans(0, this.messageText.length(), URLSpan.class);
                for (a = 0; a < count; a++) {
                    MessageEntity entity = (MessageEntity) this.messageOwner.entities.get(a);
                    if (entity.length > 0 && entity.offset >= 0 && entity.offset < this.messageOwner.message.length()) {
                        if (entity.offset + entity.length > this.messageOwner.message.length()) {
                            entity.length = this.messageOwner.message.length() - entity.offset;
                        }
                        if (spans != null && spans.length > 0) {
                            for (int b = 0; b < spans.length; b++) {
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
                        } else if ((entity instanceof TL_messageEntityCode) || (entity instanceof TL_messageEntityPre)) {
                            spannable.setSpan(new URLSpanMono(spannable, entity.offset, entity.offset + entity.length, isOutOwner()), entity.offset, entity.offset + entity.length, 33);
                        } else if (entity instanceof TL_messageEntityMentionName) {
                            spannable.setSpan(new URLSpanUserMention("" + ((TL_messageEntityMentionName) entity).user_id, isOutOwner()), entity.offset, entity.offset + entity.length, 33);
                        } else if (entity instanceof TL_inputMessageEntityMentionName) {
                            spannable.setSpan(new URLSpanUserMention("" + ((TL_inputMessageEntityMentionName) entity).user_id.user_id, isOutOwner()), entity.offset, entity.offset + entity.length, 33);
                        } else if (!useManualParse) {
                            String url = this.messageOwner.message.substring(entity.offset, entity.offset + entity.length);
                            if (entity instanceof TL_messageEntityBotCommand) {
                                spannable.setSpan(new URLSpanBotCommand(url, isOutOwner()), entity.offset, entity.offset + entity.length, 33);
                            } else if ((entity instanceof TL_messageEntityHashtag) || (entity instanceof TL_messageEntityMention)) {
                                spannable.setSpan(new URLSpanNoUnderline(url), entity.offset, entity.offset + entity.length, 33);
                            } else if (entity instanceof TL_messageEntityEmail) {
                                spannable.setSpan(new URLSpanReplacement("mailto:" + url), entity.offset, entity.offset + entity.length, 33);
                            } else if (entity instanceof TL_messageEntityUrl) {
                                if (url.toLowerCase().startsWith("http")) {
                                    spannable.setSpan(new URLSpan(url), entity.offset, entity.offset + entity.length, 33);
                                } else {
                                    spannable.setSpan(new URLSpan("http://" + url), entity.offset, entity.offset + entity.length, 33);
                                }
                            } else if (entity instanceof TL_messageEntityTextUrl) {
                                spannable.setSpan(new URLSpanReplacement(entity.url), entity.offset, entity.offset + entity.length, 33);
                            }
                        }
                    }
                }
            }
            boolean needShare = this.messageOwner.from_id > 0 && ((this.messageOwner.to_id.channel_id != 0 || this.messageOwner.to_id.chat_id != 0 || (this.messageOwner.media instanceof TL_messageMediaGame) || (this.messageOwner.media instanceof TL_messageMediaInvoice)) && !isOut());
            this.generatedWithMinSize = AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x;
            int maxWidth = this.generatedWithMinSize - AndroidUtilities.dp(needShare ? 122.0f : 80.0f);
            if ((fromUser != null && fromUser.bot) || ((isMegagroup() || !(this.messageOwner.fwd_from == null || this.messageOwner.fwd_from.channel_id == 0)) && !isOut())) {
                maxWidth -= AndroidUtilities.dp(20.0f);
            }
            if (this.messageOwner.media instanceof TL_messageMediaGame) {
                maxWidth -= AndroidUtilities.dp(10.0f);
            }
            if (this.messageOwner.media instanceof TL_messageMediaGame) {
                paint = Theme.chat_msgGameTextPaint;
            } else {
                paint = Theme.chat_msgTextPaint;
            }
            try {
                StaticLayout textLayout = new StaticLayout(this.messageText, paint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.textHeight = textLayout.getHeight();
                int linesCount = textLayout.getLineCount();
                int blocksCount = (int) Math.ceil((double) (((float) linesCount) / 2.0f));
                int linesOffset = 0;
                float prevOffset = 0.0f;
                for (a = 0; a < blocksCount; a++) {
                    int currentBlockLinesCount = Math.min(2, linesCount - linesOffset);
                    TextLayoutBlock block = new TextLayoutBlock();
                    if (blocksCount == 1) {
                        block.textLayout = textLayout;
                        block.textYOffset = 0.0f;
                        block.charactersOffset = 0;
                        block.height = this.textHeight;
                    } else {
                        int startCharacter = textLayout.getLineStart(linesOffset);
                        int endCharacter = textLayout.getLineEnd((linesOffset + currentBlockLinesCount) - 1);
                        if (endCharacter >= startCharacter) {
                            block.charactersOffset = startCharacter;
                            block.charactersEnd = endCharacter;
                            try {
                                if (VERSION.SDK_INT >= 24) {
                                    block.textLayout = Builder.obtain(this.messageText, startCharacter, endCharacter, paint, maxWidth).setAlignment(Alignment.ALIGN_NORMAL).setLineSpacing(0.0f, 1.0f).setEllipsize(null).setIncludePad(false).setBreakStrategy(1).setHyphenationFrequency(0).build();
                                } else {
                                    block.textLayout = new StaticLayout(this.messageText, startCharacter, endCharacter, paint, maxWidth, Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                                }
                                block.textYOffset = (float) textLayout.getLineTop(linesOffset);
                                if (a != 0) {
                                    block.height = (int) (block.textYOffset - prevOffset);
                                }
                                block.height = Math.max(block.height, block.textLayout.getLineBottom(block.textLayout.getLineCount() - 1));
                                prevOffset = block.textYOffset;
                                if (a == blocksCount - 1) {
                                    currentBlockLinesCount = Math.max(currentBlockLinesCount, block.textLayout.getLineCount());
                                    try {
                                        this.textHeight = Math.max(this.textHeight, (int) (block.textYOffset + ((float) block.textLayout.getHeight())));
                                    } catch (Throwable e2) {
                                        FileLog.e(e2);
                                    }
                                }
                            } catch (Throwable e22) {
                                FileLog.e(e22);
                            }
                        }
                    }
                    this.textLayoutBlocks.add(block);
                    float lastLeft = 0.0f;
                    block.textXOffset = 0.0f;
                    try {
                        float lastLeft2 = block.textLayout.getLineLeft(currentBlockLinesCount - 1);
                        block.textXOffset = lastLeft2;
                        lastLeft = lastLeft2;
                    } catch (Throwable e222) {
                        FileLog.e(e222);
                    }
                    float lastLine = 0.0f;
                    try {
                        lastLine = block.textLayout.getLineWidth(currentBlockLinesCount - 1);
                    } catch (Throwable e2222) {
                        FileLog.e(e2222);
                    }
                    int linesMaxWidth = (int) Math.ceil((double) lastLine);
                    boolean hasNonRTL = false;
                    if (a == blocksCount - 1) {
                        this.lastLineWidth = linesMaxWidth;
                    }
                    int lastLineWidthWithLeft = (int) Math.ceil((double) (lastLine + lastLeft));
                    int linesMaxWidthWithLeft = lastLineWidthWithLeft;
                    if (lastLeft == 0.0f) {
                        hasNonRTL = true;
                    }
                    if (currentBlockLinesCount > 1) {
                        float textRealMaxWidth = 0.0f;
                        float textRealMaxWidthWithLeft = 0.0f;
                        for (int n = 0; n < currentBlockLinesCount; n++) {
                            float lineWidth;
                            float lineLeft;
                            try {
                                lineWidth = block.textLayout.getLineWidth(n);
                            } catch (Throwable e22222) {
                                FileLog.e(e22222);
                                lineWidth = 0.0f;
                            }
                            if (lineWidth > ((float) (maxWidth + 20))) {
                                lineWidth = (float) maxWidth;
                            }
                            try {
                                lineLeft = block.textLayout.getLineLeft(n);
                            } catch (Throwable e222222) {
                                FileLog.e(e222222);
                                lineLeft = 0.0f;
                            }
                            if (lineLeft >= 0.0f) {
                                block.textXOffset = Math.min(block.textXOffset, lineLeft);
                            }
                            if (lineLeft == 0.0f) {
                                hasNonRTL = true;
                            }
                            textRealMaxWidth = Math.max(textRealMaxWidth, lineWidth);
                            textRealMaxWidthWithLeft = Math.max(textRealMaxWidthWithLeft, lineWidth + lineLeft);
                            linesMaxWidth = Math.max(linesMaxWidth, (int) Math.ceil((double) lineWidth));
                            linesMaxWidthWithLeft = Math.max(linesMaxWidthWithLeft, (int) Math.ceil((double) (lineWidth + lineLeft)));
                        }
                        if (hasNonRTL) {
                            textRealMaxWidth = textRealMaxWidthWithLeft;
                            if (a == blocksCount - 1) {
                                this.lastLineWidth = lastLineWidthWithLeft;
                            }
                        } else if (a == blocksCount - 1) {
                            this.lastLineWidth = linesMaxWidth;
                        }
                        this.textWidth = Math.max(this.textWidth, (int) Math.ceil((double) textRealMaxWidth));
                    } else {
                        this.textWidth = Math.max(this.textWidth, Math.min(maxWidth, linesMaxWidth));
                    }
                    if (hasNonRTL) {
                        block.textXOffset = 0.0f;
                    }
                    linesOffset += currentBlockLinesCount;
                }
            } catch (Throwable e2222222) {
                FileLog.e(e2222222);
            }
        }
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    public boolean isOutOwner() {
        return this.messageOwner.out && this.messageOwner.from_id > 0 && !this.messageOwner.post;
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

    public boolean isSecretPhoto() {
        return (this.messageOwner instanceof TL_message_secret) && (this.messageOwner.media instanceof TL_messageMediaPhoto) && this.messageOwner.ttl > 0 && this.messageOwner.ttl <= 60;
    }

    public boolean isSecretMedia() {
        return (this.messageOwner instanceof TL_message_secret) && (((this.messageOwner.media instanceof TL_messageMediaPhoto) && this.messageOwner.ttl > 0 && this.messageOwner.ttl <= 60) || isVoice() || isVideo());
    }

    public static void setUnreadFlags(Message message, int flag) {
        boolean z;
        boolean z2 = true;
        if ((flag & 1) == 0) {
            z = true;
        } else {
            z = false;
        }
        message.unread = z;
        if ((flag & 2) != 0) {
            z2 = false;
        }
        message.media_unread = z2;
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

    public static boolean isMegagroup(Message message) {
        return (message.flags & Integer.MIN_VALUE) != 0;
    }

    public static boolean isOut(Message message) {
        return message.out;
    }

    public long getDialogId() {
        return getDialogId(this.messageOwner);
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
        return this.messageOwner.send_state == 0 || this.messageOwner.id > 0;
    }

    public String getSecretTimeString() {
        if (!isSecretMedia()) {
            return null;
        }
        int secondsLeft = this.messageOwner.ttl;
        if (this.messageOwner.destroyTime != 0) {
            secondsLeft = Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance().getCurrentTime());
        }
        if (secondsLeft < 60) {
            return secondsLeft + "s";
        }
        return (secondsLeft / 60) + "m";
    }

    public String getDocumentName() {
        if (this.messageOwner.media instanceof TL_messageMediaDocument) {
            return FileLoader.getDocumentFileName(this.messageOwner.media.document);
        }
        if (this.messageOwner.media instanceof TL_messageMediaWebPage) {
            return FileLoader.getDocumentFileName(this.messageOwner.media.webpage.document);
        }
        return "";
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
        return webDocument != null && webDocument.mime_type.startsWith("image/");
    }

    public static boolean isVideoWebDocument(TL_webDocument webDocument) {
        return webDocument != null && webDocument.mime_type.startsWith("video/");
    }

    public static boolean isMusicDocument(Document document) {
        if (document == null) {
            return false;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (!(attribute instanceof TL_documentAttributeAudio)) {
                a++;
            } else if (attribute.voice) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isVideoDocument(Document document) {
        if (document == null) {
            return false;
        }
        boolean isAnimated = false;
        boolean isVideo = false;
        for (int a = 0; a < document.attributes.size(); a++) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeVideo) {
                isVideo = true;
            } else if (attribute instanceof TL_documentAttributeAnimated) {
                isAnimated = true;
            }
        }
        if (!isVideo || isAnimated) {
            return false;
        }
        return true;
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
        return (message.media == null || message.media.document == null || !isMusicDocument(message.media.document)) ? false : true;
    }

    public static boolean isVoiceMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isVoiceDocument(message.media.webpage.document);
        }
        return (message.media == null || message.media.document == null || !isVoiceDocument(message.media.document)) ? false : true;
    }

    public static boolean isNewGifMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isNewGifDocument(message.media.webpage.document);
        }
        return (message.media == null || message.media.document == null || !isNewGifDocument(message.media.document)) ? false : true;
    }

    public static boolean isVideoMessage(Message message) {
        if (message.media instanceof TL_messageMediaWebPage) {
            return isVideoDocument(message.media.webpage.document);
        }
        return (message.media == null || message.media.document == null || !isVideoDocument(message.media.document)) ? false : true;
    }

    public static boolean isGameMessage(Message message) {
        return message.media instanceof TL_messageMediaGame;
    }

    public static boolean isInvoiceMessage(Message message) {
        return message.media instanceof TL_messageMediaInvoice;
    }

    public static InputStickerSet getInputStickerSet(Message message) {
        if (message.media == null || message.media.document == null) {
            return null;
        }
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
        return null;
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
        if (this.type == 0) {
            int i = this.textHeight;
            int dp = ((this.messageOwner.media instanceof TL_messageMediaWebPage) && (this.messageOwner.media.webpage instanceof TL_webPage)) ? AndroidUtilities.dp(100.0f) : 0;
            int height = i + dp;
            return isReply() ? height + AndroidUtilities.dp(42.0f) : height;
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
                return AndroidUtilities.dp(BitmapDescriptorFactory.HUE_ORANGE);
            }
            if (this.type == 11) {
                return AndroidUtilities.dp(50.0f);
            }
            int photoHeight;
            int photoWidth;
            if (this.type == 13) {
                float maxWidth;
                float maxHeight = ((float) AndroidUtilities.displaySize.y) * 0.4f;
                if (AndroidUtilities.isTablet()) {
                    maxWidth = ((float) AndroidUtilities.getMinTabletSide()) * 0.5f;
                } else {
                    maxWidth = ((float) AndroidUtilities.displaySize.x) * 0.5f;
                }
                photoHeight = 0;
                photoWidth = 0;
                Iterator it = this.messageOwner.media.document.attributes.iterator();
                while (it.hasNext()) {
                    DocumentAttribute attribute = (DocumentAttribute) it.next();
                    if (attribute instanceof TL_documentAttributeImageSize) {
                        photoWidth = attribute.w;
                        photoHeight = attribute.h;
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
                return photoHeight + AndroidUtilities.dp(14.0f);
            }
            if (AndroidUtilities.isTablet()) {
                photoWidth = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.7f);
            } else {
                photoWidth = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.7f);
            }
            photoHeight = photoWidth + AndroidUtilities.dp(100.0f);
            if (photoWidth > AndroidUtilities.getPhotoSize()) {
                photoWidth = AndroidUtilities.getPhotoSize();
            }
            if (photoHeight > AndroidUtilities.getPhotoSize()) {
                photoHeight = AndroidUtilities.getPhotoSize();
            }
            PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
            if (currentPhotoObject != null) {
                int h = (int) (((float) currentPhotoObject.h) / (((float) currentPhotoObject.w) / ((float) photoWidth)));
                if (h == 0) {
                    h = AndroidUtilities.dp(100.0f);
                }
                if (h > photoHeight) {
                    h = photoHeight;
                } else if (h < AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN)) {
                    h = AndroidUtilities.dp(BitmapDescriptorFactory.HUE_GREEN);
                }
                if (isSecretPhoto()) {
                    if (AndroidUtilities.isTablet()) {
                        h = (int) (((float) AndroidUtilities.getMinTabletSide()) * 0.5f);
                    } else {
                        h = (int) (((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f);
                    }
                }
                photoHeight = h;
            }
            return photoHeight + AndroidUtilities.dp(14.0f);
        }
    }

    public String getStickerEmoji() {
        int a = 0;
        while (a < this.messageOwner.media.document.attributes.size()) {
            DocumentAttribute attribute = (DocumentAttribute) this.messageOwner.media.document.attributes.get(a);
            if (!(attribute instanceof TL_documentAttributeSticker)) {
                a++;
            } else if (attribute.alt == null || attribute.alt.length() <= 0) {
                return null;
            } else {
                return attribute.alt;
            }
        }
        return null;
    }

    public boolean isSticker() {
        if (this.type != 1000) {
            return this.type == 13;
        } else {
            return isStickerMessage(this.messageOwner);
        }
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

    public boolean isGame() {
        return isGameMessage(this.messageOwner);
    }

    public boolean isInvoice() {
        return isInvoiceMessage(this.messageOwner);
    }

    public boolean hasPhotoStickers() {
        return (this.messageOwner.media == null || this.messageOwner.media.photo == null || !this.messageOwner.media.photo.has_stickers) ? false : true;
    }

    public boolean isGif() {
        return (this.messageOwner.media instanceof TL_messageMediaDocument) && isGifDocument(this.messageOwner.media.document);
    }

    public boolean isWebpageDocument() {
        return (!(this.messageOwner.media instanceof TL_messageMediaWebPage) || this.messageOwner.media.webpage.document == null || isGifDocument(this.messageOwner.media.webpage.document)) ? false : true;
    }

    public boolean isNewGif() {
        return this.messageOwner.media != null && isNewGifDocument(this.messageOwner.media.document);
    }

    public String getMusicTitle() {
        Document document;
        if (this.type == 0) {
            document = this.messageOwner.media.webpage.document;
        } else {
            document = this.messageOwner.media.document;
        }
        int a = 0;
        while (a < document.attributes.size()) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (!(attribute instanceof TL_documentAttributeAudio)) {
                a++;
            } else if (attribute.voice) {
                return LocaleController.formatDateAudio((long) this.messageOwner.date);
            } else {
                String title = attribute.title;
                if (title != null && title.length() != 0) {
                    return title;
                }
                title = FileLoader.getDocumentFileName(document);
                if (title == null || title.length() == 0) {
                    return LocaleController.getString("AudioUnknownTitle", R.string.AudioUnknownTitle);
                }
                return title;
            }
        }
        return "";
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
        }
        return 0;
    }

    public String getMusicAuthor() {
        Document document;
        if (this.type == 0) {
            document = this.messageOwner.media.webpage.document;
        } else {
            document = this.messageOwner.media.document;
        }
        for (int a = 0; a < document.attributes.size(); a++) {
            DocumentAttribute attribute = (DocumentAttribute) document.attributes.get(a);
            if (attribute instanceof TL_documentAttributeAudio) {
                if (attribute.voice) {
                    if (isOutOwner() || (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_id == UserConfig.getClientUserId())) {
                        return LocaleController.getString("FromYou", R.string.FromYou);
                    }
                    User user = null;
                    Chat chat = null;
                    if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.channel_id != 0) {
                        chat = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                    } else if (this.messageOwner.fwd_from != null && this.messageOwner.fwd_from.from_id != 0) {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                    } else if (this.messageOwner.from_id < 0) {
                        chat = MessagesController.getInstance().getChat(Integer.valueOf(-this.messageOwner.from_id));
                    } else {
                        user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
                    }
                    if (user != null) {
                        return UserObject.getUserName(user);
                    }
                    if (chat != null) {
                        return chat.title;
                    }
                }
                String performer = attribute.performer;
                if (performer == null || performer.length() == 0) {
                    return LocaleController.getString("AudioUnknownArtist", R.string.AudioUnknownArtist);
                }
                return performer;
            }
        }
        return "";
    }

    public InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    public static boolean isForwardedMessage(Message message) {
        return (message.flags & 4) != 0;
    }

    public boolean isReply() {
        return (this.replyMessageObject == null || !(this.replyMessageObject.messageOwner instanceof TL_messageEmpty)) && !((this.messageOwner.reply_to_msg_id == 0 && this.messageOwner.reply_to_random_id == 0) || (this.messageOwner.flags & 8) == 0);
    }

    public boolean isMediaEmpty() {
        return isMediaEmpty(this.messageOwner);
    }

    public static boolean isMediaEmpty(Message message) {
        return message == null || message.media == null || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage);
    }

    public boolean canEditMessage(Chat chat) {
        return canEditMessage(this.messageOwner, chat);
    }

    public static boolean canEditMessage(Message message, Chat chat) {
        boolean z = true;
        if (message == null || message.to_id == null) {
            return false;
        }
        if ((message.action != null && !(message.action instanceof TL_messageActionEmpty)) || isForwardedMessage(message) || message.via_bot_id != 0 || message.id < 0) {
            return false;
        }
        if (message.from_id == message.to_id.user_id && message.from_id == UserConfig.getClientUserId()) {
            return true;
        }
        if (Math.abs(message.date - ConnectionsManager.getInstance().getCurrentTime()) > MessagesController.getInstance().maxEditTime) {
            return false;
        }
        if (message.to_id.channel_id == 0) {
            if (!((message.out || message.from_id == UserConfig.getClientUserId()) && ((message.media instanceof TL_messageMediaPhoto) || (((message.media instanceof TL_messageMediaDocument) && !isStickerMessage(message)) || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage) || message.media == null)))) {
                z = false;
            }
            return z;
        }
        if (chat == null && message.to_id.channel_id != 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(message.to_id.channel_id));
            if (chat == null) {
                return false;
            }
        }
        if (!(chat.megagroup && message.out)) {
            if (chat.megagroup) {
                return false;
            }
            if (!((chat.creator || (chat.editor && isOut(message))) && message.post)) {
                return false;
            }
        }
        if ((message.media instanceof TL_messageMediaPhoto) || (((message.media instanceof TL_messageMediaDocument) && !isStickerMessage(message)) || (message.media instanceof TL_messageMediaEmpty) || (message.media instanceof TL_messageMediaWebPage) || message.media == null)) {
            return true;
        }
        return false;
    }

    public boolean canDeleteMessage(Chat chat) {
        return canDeleteMessage(this.messageOwner, chat);
    }

    public static boolean canDeleteMessage(Message message, Chat chat) {
        boolean z = false;
        if (message.id < 0) {
            return true;
        }
        if (chat == null && message.to_id.channel_id != 0) {
            chat = MessagesController.getInstance().getChat(Integer.valueOf(message.to_id.channel_id));
        }
        if (ChatObject.isChannel(chat)) {
            if (message.id == 1) {
                return false;
            }
            if (chat.creator) {
                return true;
            }
            if (chat.editor) {
                if (isOut(message)) {
                    return true;
                }
                if (message.from_id > 0 && !message.post) {
                    return true;
                }
            } else if (chat.moderator) {
                if (message.from_id > 0 && !message.post) {
                    return true;
                }
            } else if (chat.megagroup && isOut(message) && message.from_id > 0) {
                return true;
            } else {
                return false;
            }
        }
        if (isOut(message) || !ChatObject.isChannel(chat)) {
            z = true;
        }
        return z;
    }

    public String getForwardedName() {
        if (this.messageOwner.fwd_from != null) {
            if (this.messageOwner.fwd_from.channel_id != 0) {
                Chat chat = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
                if (chat != null) {
                    return chat.title;
                }
            } else if (this.messageOwner.fwd_from.from_id != 0) {
                User user = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
                if (user != null) {
                    return UserObject.getUserName(user);
                }
            }
        }
        return null;
    }

    public void checkMediaExistance() {
        this.attachPathExists = false;
        this.mediaExists = false;
        if (this.type == 1) {
            if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
                this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
            }
        } else if (this.type == 8 || this.type == 3 || this.type == 9 || this.type == 2 || this.type == 14) {
            if (this.messageOwner.attachPath != null && this.messageOwner.attachPath.length() > 0) {
                this.attachPathExists = new File(this.messageOwner.attachPath).exists();
            }
            if (!this.attachPathExists) {
                this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
            }
        } else {
            Document document = getDocument();
            if (document != null) {
                this.mediaExists = FileLoader.getPathToAttach(document).exists();
            } else if (this.type == 0) {
                PhotoSize currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                if (currentPhotoObject != null && currentPhotoObject != null) {
                    this.mediaExists = FileLoader.getPathToAttach(currentPhotoObject, true).exists();
                }
            }
        }
    }
}
