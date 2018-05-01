package org.telegram.messenger;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.widget.Toast;
import java.security.SecureRandom;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.BotInfo;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.ChatFull;
import org.telegram.tgnet.TLRPC.ChatParticipant;
import org.telegram.tgnet.TLRPC.ChatParticipants;
import org.telegram.tgnet.TLRPC.DialogPeer;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DraftMessage;
import org.telegram.tgnet.TLRPC.EncryptedChat;
import org.telegram.tgnet.TLRPC.EncryptedMessage;
import org.telegram.tgnet.TLRPC.ExportedChatInvite;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputPhoto;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageAction;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.MessageFwdHeader;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.NotifyPeer;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.PhoneCall;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.RecentMeUrl;
import org.telegram.tgnet.TLRPC.SendMessageAction;
import org.telegram.tgnet.TLRPC.TL_account_registerDevice;
import org.telegram.tgnet.TLRPC.TL_account_unregisterDevice;
import org.telegram.tgnet.TLRPC.TL_account_updateStatus;
import org.telegram.tgnet.TLRPC.TL_auth_logOut;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_botInfo;
import org.telegram.tgnet.TLRPC.TL_channel;
import org.telegram.tgnet.TLRPC.TL_channelAdminRights;
import org.telegram.tgnet.TLRPC.TL_channelBannedRights;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_channelMessagesFilterEmpty;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteHistory;
import org.telegram.tgnet.TLRPC.TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_channels_deleteUserHistory;
import org.telegram.tgnet.TLRPC.TL_channels_editAbout;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_editBanned;
import org.telegram.tgnet.TLRPC.TL_channels_editPhoto;
import org.telegram.tgnet.TLRPC.TL_channels_editTitle;
import org.telegram.tgnet.TLRPC.TL_channels_getFullChannel;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_channels_leaveChannel;
import org.telegram.tgnet.TLRPC.TL_channels_readHistory;
import org.telegram.tgnet.TLRPC.TL_channels_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_channels_toggleInvites;
import org.telegram.tgnet.TLRPC.TL_channels_togglePreHistoryHidden;
import org.telegram.tgnet.TLRPC.TL_channels_toggleSignatures;
import org.telegram.tgnet.TLRPC.TL_channels_updatePinnedMessage;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chat;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_config;
import org.telegram.tgnet.TLRPC.TL_contactBlocked;
import org.telegram.tgnet.TLRPC.TL_contactLinkContact;
import org.telegram.tgnet.TLRPC.TL_contacts_block;
import org.telegram.tgnet.TLRPC.TL_contacts_getBlocked;
import org.telegram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.telegram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.telegram.tgnet.TLRPC.TL_contacts_unblock;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_dialogPeer;
import org.telegram.tgnet.TLRPC.TL_documentEmpty;
import org.telegram.tgnet.TLRPC.TL_draftMessage;
import org.telegram.tgnet.TLRPC.TL_encryptedChat;
import org.telegram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.telegram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_help_getAppChangelog;
import org.telegram.tgnet.TLRPC.TL_help_getRecentMeUrls;
import org.telegram.tgnet.TLRPC.TL_help_recentMeUrls;
import org.telegram.tgnet.TLRPC.TL_inputChannel;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputDialogPeer;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerChat;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC.TL_inputPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUser;
import org.telegram.tgnet.TLRPC.TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUserSelf;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.telegram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.telegram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.telegram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_messages_addChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_affectedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_affectedMessages;
import org.telegram.tgnet.TLRPC.TL_messages_channelMessages;
import org.telegram.tgnet.TLRPC.TL_messages_chatFull;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.tgnet.TLRPC.TL_messages_deleteChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_deleteHistory;
import org.telegram.tgnet.TLRPC.TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
import org.telegram.tgnet.TLRPC.TL_messages_editChatPhoto;
import org.telegram.tgnet.TLRPC.TL_messages_editChatTitle;
import org.telegram.tgnet.TLRPC.TL_messages_getDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getFullChat;
import org.telegram.tgnet.TLRPC.TL_messages_getHistory;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getMessagesViews;
import org.telegram.tgnet.TLRPC.TL_messages_getPeerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getPeerSettings;
import org.telegram.tgnet.TLRPC.TL_messages_getPinnedDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getUnreadMentions;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_messages_hideReportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_readEncryptedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_messages_receivedQueue;
import org.telegram.tgnet.TLRPC.TL_messages_reportEncryptedSpam;
import org.telegram.tgnet.TLRPC.TL_messages_reportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_messages_setEncryptedTyping;
import org.telegram.tgnet.TLRPC.TL_messages_setTyping;
import org.telegram.tgnet.TLRPC.TL_messages_startBot;
import org.telegram.tgnet.TLRPC.TL_messages_toggleChatAdmins;
import org.telegram.tgnet.TLRPC.TL_messages_toggleDialogPin;
import org.telegram.tgnet.TLRPC.TL_notifyPeer;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import org.telegram.tgnet.TLRPC.TL_peerSettings;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallRequested;
import org.telegram.tgnet.TLRPC.TL_phone_discardCall;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_deletePhotos;
import org.telegram.tgnet.TLRPC.TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_photos_updateProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_privacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC.TL_privacyKeyStatusTimestamp;
import org.telegram.tgnet.TLRPC.TL_replyKeyboardHide;
import org.telegram.tgnet.TLRPC.TL_sendMessageCancelAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageGamePlayAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageRecordAudioAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageRecordRoundAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageRecordVideoAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageTypingAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadAudioAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadDocumentAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadPhotoAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadRoundAction;
import org.telegram.tgnet.TLRPC.TL_sendMessageUploadVideoAction;
import org.telegram.tgnet.TLRPC.TL_updateChannel;
import org.telegram.tgnet.TLRPC.TL_updateChannelAvailableMessages;
import org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews;
import org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage;
import org.telegram.tgnet.TLRPC.TL_updateChannelReadMessagesContents;
import org.telegram.tgnet.TLRPC.TL_updateChannelTooLong;
import org.telegram.tgnet.TLRPC.TL_updateChannelWebPage;
import org.telegram.tgnet.TLRPC.TL_updateChatAdmins;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete;
import org.telegram.tgnet.TLRPC.TL_updateChatParticipants;
import org.telegram.tgnet.TLRPC.TL_updateChatUserTyping;
import org.telegram.tgnet.TLRPC.TL_updateConfig;
import org.telegram.tgnet.TLRPC.TL_updateContactLink;
import org.telegram.tgnet.TLRPC.TL_updateContactRegistered;
import org.telegram.tgnet.TLRPC.TL_updateContactsReset;
import org.telegram.tgnet.TLRPC.TL_updateDcOptions;
import org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
import org.telegram.tgnet.TLRPC.TL_updateDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_updateDialogPinned;
import org.telegram.tgnet.TLRPC.TL_updateDraftMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping;
import org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead;
import org.telegram.tgnet.TLRPC.TL_updateEncryption;
import org.telegram.tgnet.TLRPC.TL_updateFavedStickers;
import org.telegram.tgnet.TLRPC.TL_updateGroupCall;
import org.telegram.tgnet.TLRPC.TL_updateGroupCallParticipant;
import org.telegram.tgnet.TLRPC.TL_updateLangPack;
import org.telegram.tgnet.TLRPC.TL_updateLangPackTooLong;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewMessage;
import org.telegram.tgnet.TLRPC.TL_updateNewStickerSet;
import org.telegram.tgnet.TLRPC.TL_updateNotifySettings;
import org.telegram.tgnet.TLRPC.TL_updatePhoneCall;
import org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs;
import org.telegram.tgnet.TLRPC.TL_updatePrivacy;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox;
import org.telegram.tgnet.TLRPC.TL_updateReadFeaturedStickers;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
import org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox;
import org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents;
import org.telegram.tgnet.TLRPC.TL_updateRecentStickers;
import org.telegram.tgnet.TLRPC.TL_updateSavedGifs;
import org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
import org.telegram.tgnet.TLRPC.TL_updateShort;
import org.telegram.tgnet.TLRPC.TL_updateShortChatMessage;
import org.telegram.tgnet.TLRPC.TL_updateShortMessage;
import org.telegram.tgnet.TLRPC.TL_updateStickerSets;
import org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder;
import org.telegram.tgnet.TLRPC.TL_updateUserBlocked;
import org.telegram.tgnet.TLRPC.TL_updateUserName;
import org.telegram.tgnet.TLRPC.TL_updateUserPhone;
import org.telegram.tgnet.TLRPC.TL_updateUserPhoto;
import org.telegram.tgnet.TLRPC.TL_updateUserStatus;
import org.telegram.tgnet.TLRPC.TL_updateUserTyping;
import org.telegram.tgnet.TLRPC.TL_updateWebPage;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.tgnet.TLRPC.TL_updatesCombined;
import org.telegram.tgnet.TLRPC.TL_updatesTooLong;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifference;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceEmpty;
import org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.telegram.tgnet.TLRPC.TL_updates_difference;
import org.telegram.tgnet.TLRPC.TL_updates_differenceEmpty;
import org.telegram.tgnet.TLRPC.TL_updates_differenceSlice;
import org.telegram.tgnet.TLRPC.TL_updates_differenceTooLong;
import org.telegram.tgnet.TLRPC.TL_updates_getChannelDifference;
import org.telegram.tgnet.TLRPC.TL_updates_getDifference;
import org.telegram.tgnet.TLRPC.TL_updates_getState;
import org.telegram.tgnet.TLRPC.TL_updates_state;
import org.telegram.tgnet.TLRPC.TL_user;
import org.telegram.tgnet.TLRPC.TL_userForeign_old2;
import org.telegram.tgnet.TLRPC.TL_userFull;
import org.telegram.tgnet.TLRPC.TL_userProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.telegram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.telegram.tgnet.TLRPC.TL_userStatusRecently;
import org.telegram.tgnet.TLRPC.TL_users_getFullUser;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.UserStatus;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.tgnet.TLRPC.contacts_Blocked;
import org.telegram.tgnet.TLRPC.messages_Dialogs;
import org.telegram.tgnet.TLRPC.messages_Messages;
import org.telegram.tgnet.TLRPC.photos_Photos;
import org.telegram.tgnet.TLRPC.updates_ChannelDifference;
import org.telegram.tgnet.TLRPC.updates_Difference;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.ProfileActivity;

public class MessagesController
  implements NotificationCenter.NotificationCenterDelegate
{
  private static volatile MessagesController[] Instance = new MessagesController[3];
  public static final int UPDATE_MASK_ALL = 1535;
  public static final int UPDATE_MASK_AVATAR = 2;
  public static final int UPDATE_MASK_CHANNEL = 8192;
  public static final int UPDATE_MASK_CHAT_ADMINS = 16384;
  public static final int UPDATE_MASK_CHAT_AVATAR = 8;
  public static final int UPDATE_MASK_CHAT_MEMBERS = 32;
  public static final int UPDATE_MASK_CHAT_NAME = 16;
  public static final int UPDATE_MASK_MESSAGE_TEXT = 32768;
  public static final int UPDATE_MASK_NAME = 1;
  public static final int UPDATE_MASK_NEW_MESSAGE = 2048;
  public static final int UPDATE_MASK_PHONE = 1024;
  public static final int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
  public static final int UPDATE_MASK_SELECT_DIALOG = 512;
  public static final int UPDATE_MASK_SEND_STATE = 4096;
  public static final int UPDATE_MASK_STATUS = 4;
  public static final int UPDATE_MASK_USER_PHONE = 128;
  public static final int UPDATE_MASK_USER_PRINT = 64;
  private static volatile long lastThemeCheckTime;
  public ArrayList<Integer> blockedUsers = new ArrayList();
  public int callConnectTimeout = 30000;
  public int callPacketTimeout = 10000;
  public int callReceiveTimeout = 20000;
  public int callRingTimeout = 90000;
  public boolean canRevokePmInbox;
  private SparseArray<ArrayList<Integer>> channelAdmins = new SparseArray();
  private SparseArray<ArrayList<Integer>> channelViewsToSend = new SparseArray();
  private SparseIntArray channelsPts = new SparseIntArray();
  private ConcurrentHashMap<Integer, TLRPC.Chat> chats = new ConcurrentHashMap(100, 1.0F, 2);
  private SparseBooleanArray checkingLastMessagesDialogs = new SparseBooleanArray();
  private ArrayList<Long> createdDialogIds = new ArrayList();
  private ArrayList<Long> createdDialogMainThreadIds = new ArrayList();
  private int currentAccount;
  private Runnable currentDeleteTaskRunnable;
  private int currentDeletingTaskChannelId;
  private ArrayList<Integer> currentDeletingTaskMids;
  private int currentDeletingTaskTime;
  public boolean defaultP2pContacts = false;
  private final Comparator<TLRPC.TL_dialog> dialogComparator = new Comparator()
  {
    public int compare(TLRPC.TL_dialog paramAnonymousTL_dialog1, TLRPC.TL_dialog paramAnonymousTL_dialog2)
    {
      int i = 1;
      if ((!paramAnonymousTL_dialog1.pinned) && (paramAnonymousTL_dialog2.pinned)) {}
      for (;;)
      {
        return i;
        if ((paramAnonymousTL_dialog1.pinned) && (!paramAnonymousTL_dialog2.pinned))
        {
          i = -1;
        }
        else if ((paramAnonymousTL_dialog1.pinned) && (paramAnonymousTL_dialog2.pinned))
        {
          if (paramAnonymousTL_dialog1.pinnedNum >= paramAnonymousTL_dialog2.pinnedNum) {
            if (paramAnonymousTL_dialog1.pinnedNum > paramAnonymousTL_dialog2.pinnedNum) {
              i = -1;
            } else {
              i = 0;
            }
          }
        }
        else
        {
          TLRPC.DraftMessage localDraftMessage = DataQuery.getInstance(MessagesController.this.currentAccount).getDraft(paramAnonymousTL_dialog1.id);
          int j;
          if ((localDraftMessage != null) && (localDraftMessage.date >= paramAnonymousTL_dialog1.last_message_date))
          {
            j = localDraftMessage.date;
            label126:
            paramAnonymousTL_dialog1 = DataQuery.getInstance(MessagesController.this.currentAccount).getDraft(paramAnonymousTL_dialog2.id);
            if ((paramAnonymousTL_dialog1 == null) || (paramAnonymousTL_dialog1.date < paramAnonymousTL_dialog2.last_message_date)) {
              break label193;
            }
          }
          label193:
          for (int k = paramAnonymousTL_dialog1.date;; k = paramAnonymousTL_dialog2.last_message_date)
          {
            if (j < k) {
              break label200;
            }
            if (j <= k) {
              break label202;
            }
            i = -1;
            break;
            j = paramAnonymousTL_dialog1.last_message_date;
            break label126;
          }
          label200:
          continue;
          label202:
          i = 0;
        }
      }
    }
  };
  public LongSparseArray<MessageObject> dialogMessage = new LongSparseArray();
  public SparseArray<MessageObject> dialogMessagesByIds = new SparseArray();
  public LongSparseArray<MessageObject> dialogMessagesByRandomIds = new LongSparseArray();
  public ArrayList<TLRPC.TL_dialog> dialogs = new ArrayList();
  public boolean dialogsEndReached;
  public ArrayList<TLRPC.TL_dialog> dialogsForward = new ArrayList();
  public ArrayList<TLRPC.TL_dialog> dialogsGroupsOnly = new ArrayList();
  public ArrayList<TLRPC.TL_dialog> dialogsServerOnly = new ArrayList();
  public LongSparseArray<TLRPC.TL_dialog> dialogs_dict = new LongSparseArray();
  public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max = new ConcurrentHashMap(100, 1.0F, 2);
  public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max = new ConcurrentHashMap(100, 1.0F, 2);
  private SharedPreferences emojiPreferences;
  public boolean enableJoined = true;
  private ConcurrentHashMap<Integer, TLRPC.EncryptedChat> encryptedChats = new ConcurrentHashMap(10, 1.0F, 2);
  private SparseArray<TLRPC.ExportedChatInvite> exportedChats = new SparseArray();
  public boolean firstGettingTask;
  private SparseArray<TLRPC.TL_userFull> fullUsers = new SparseArray();
  private boolean getDifferenceFirstSync = true;
  public boolean gettingDifference;
  private SparseBooleanArray gettingDifferenceChannels = new SparseBooleanArray();
  private boolean gettingNewDeleteTask;
  private SparseBooleanArray gettingUnknownChannels = new SparseBooleanArray();
  public ArrayList<TLRPC.RecentMeUrl> hintDialogs = new ArrayList();
  private String installReferer;
  private ArrayList<Integer> joiningToChannels = new ArrayList();
  private int lastPrintingStringCount;
  private long lastPushRegisterSendTime;
  private long lastStatusUpdateTime;
  private long lastViewsCheckTime;
  public String linkPrefix = "t.me";
  private ArrayList<Integer> loadedFullChats = new ArrayList();
  private ArrayList<Integer> loadedFullParticipants = new ArrayList();
  private ArrayList<Integer> loadedFullUsers = new ArrayList();
  public boolean loadingBlockedUsers = false;
  private SparseIntArray loadingChannelAdmins = new SparseIntArray();
  public boolean loadingDialogs;
  private ArrayList<Integer> loadingFullChats = new ArrayList();
  private ArrayList<Integer> loadingFullParticipants = new ArrayList();
  private ArrayList<Integer> loadingFullUsers = new ArrayList();
  private LongSparseArray<Boolean> loadingPeerSettings = new LongSparseArray();
  private SharedPreferences mainPreferences;
  public int maxBroadcastCount = 100;
  public int maxEditTime = 172800;
  public int maxFaveStickersCount = 5;
  public int maxGroupCount = 200;
  public int maxMegagroupCount = 10000;
  public int maxPinnedDialogsCount = 5;
  public int maxRecentGifsCount = 200;
  public int maxRecentStickersCount = 30;
  private boolean migratingDialogs;
  public int minGroupConvertSize = 200;
  private SparseIntArray needShortPollChannels = new SparseIntArray();
  public int nextDialogsCacheOffset;
  private SharedPreferences notificationsPreferences;
  private ConcurrentHashMap<String, TLObject> objectsByUsernames = new ConcurrentHashMap(100, 1.0F, 2);
  private boolean offlineSent;
  public ConcurrentHashMap<Integer, Integer> onlinePrivacy = new ConcurrentHashMap(20, 1.0F, 2);
  public boolean preloadFeaturedStickers;
  public LongSparseArray<CharSequence> printingStrings = new LongSparseArray();
  public LongSparseArray<Integer> printingStringsTypes = new LongSparseArray();
  public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers = new ConcurrentHashMap(20, 1.0F, 2);
  public int ratingDecay;
  private ArrayList<ReadTask> readTasks = new ArrayList();
  private LongSparseArray<ReadTask> readTasksMap = new LongSparseArray();
  public boolean registeringForPush;
  private LongSparseArray<ArrayList<Integer>> reloadingMessages = new LongSparseArray();
  private HashMap<String, ArrayList<MessageObject>> reloadingWebpages = new HashMap();
  private LongSparseArray<ArrayList<MessageObject>> reloadingWebpagesPending = new LongSparseArray();
  private TLRPC.messages_Dialogs resetDialogsAll;
  private TLRPC.TL_messages_peerDialogs resetDialogsPinned;
  private boolean resetingDialogs;
  public int revokeTimeLimit = 172800;
  public int revokeTimePmLimit = 172800;
  public int secretWebpagePreview = 2;
  public SparseArray<LongSparseArray<Boolean>> sendingTypings = new SparseArray();
  public boolean serverDialogsEndReached;
  private SparseIntArray shortPollChannels = new SparseIntArray();
  private int statusRequest;
  private int statusSettingState;
  private Runnable themeCheckRunnable = new Runnable()
  {
    public void run() {}
  };
  private final Comparator<TLRPC.Update> updatesComparator = new Comparator()
  {
    public int compare(TLRPC.Update paramAnonymousUpdate1, TLRPC.Update paramAnonymousUpdate2)
    {
      int i = MessagesController.this.getUpdateType(paramAnonymousUpdate1);
      int j = MessagesController.this.getUpdateType(paramAnonymousUpdate2);
      if (i != j) {
        i = AndroidUtilities.compare(i, j);
      }
      for (;;)
      {
        return i;
        if (i == 0)
        {
          i = AndroidUtilities.compare(MessagesController.getUpdatePts(paramAnonymousUpdate1), MessagesController.getUpdatePts(paramAnonymousUpdate2));
        }
        else if (i == 1)
        {
          i = AndroidUtilities.compare(MessagesController.getUpdateQts(paramAnonymousUpdate1), MessagesController.getUpdateQts(paramAnonymousUpdate2));
        }
        else if (i == 2)
        {
          i = MessagesController.getUpdateChannelId(paramAnonymousUpdate1);
          j = MessagesController.getUpdateChannelId(paramAnonymousUpdate2);
          if (i == j) {
            i = AndroidUtilities.compare(MessagesController.getUpdatePts(paramAnonymousUpdate1), MessagesController.getUpdatePts(paramAnonymousUpdate2));
          } else {
            i = AndroidUtilities.compare(i, j);
          }
        }
        else
        {
          i = 0;
        }
      }
    }
  };
  private SparseArray<ArrayList<TLRPC.Updates>> updatesQueueChannels = new SparseArray();
  private ArrayList<TLRPC.Updates> updatesQueuePts = new ArrayList();
  private ArrayList<TLRPC.Updates> updatesQueueQts = new ArrayList();
  private ArrayList<TLRPC.Updates> updatesQueueSeq = new ArrayList();
  private SparseLongArray updatesStartWaitTimeChannels = new SparseLongArray();
  private long updatesStartWaitTimePts;
  private long updatesStartWaitTimeQts;
  private long updatesStartWaitTimeSeq;
  public boolean updatingState;
  private String uploadingAvatar;
  private ConcurrentHashMap<Integer, TLRPC.User> users = new ConcurrentHashMap(100, 1.0F, 2);
  
  public MessagesController(int paramInt)
  {
    this.currentAccount = paramInt;
    ImageLoader.getInstance();
    MessagesStorage.getInstance(this.currentAccount);
    LocationController.getInstance(this.currentAccount);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController localMessagesController = MessagesController.getInstance(MessagesController.this.currentAccount);
        NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(localMessagesController, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(localMessagesController, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(localMessagesController, NotificationCenter.FileDidLoaded);
        NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(localMessagesController, NotificationCenter.FileDidFailedLoad);
        NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(localMessagesController, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(localMessagesController, NotificationCenter.updateMessageMedia);
      }
    });
    addSupportUser();
    if (this.currentAccount == 0)
    {
      this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
      this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    }
    for (this.emojiPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);; this.emojiPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji" + this.currentAccount, 0))
    {
      this.enableJoined = this.notificationsPreferences.getBoolean("EnableContactJoined", true);
      this.secretWebpagePreview = this.mainPreferences.getInt("secretWebpage2", 2);
      this.maxGroupCount = this.mainPreferences.getInt("maxGroupCount", 200);
      this.maxMegagroupCount = this.mainPreferences.getInt("maxMegagroupCount", 10000);
      this.maxRecentGifsCount = this.mainPreferences.getInt("maxRecentGifsCount", 200);
      this.maxRecentStickersCount = this.mainPreferences.getInt("maxRecentStickersCount", 30);
      this.maxFaveStickersCount = this.mainPreferences.getInt("maxFaveStickersCount", 5);
      this.maxEditTime = this.mainPreferences.getInt("maxEditTime", 3600);
      this.ratingDecay = this.mainPreferences.getInt("ratingDecay", 2419200);
      this.linkPrefix = this.mainPreferences.getString("linkPrefix", "t.me");
      this.callReceiveTimeout = this.mainPreferences.getInt("callReceiveTimeout", 20000);
      this.callRingTimeout = this.mainPreferences.getInt("callRingTimeout", 90000);
      this.callConnectTimeout = this.mainPreferences.getInt("callConnectTimeout", 30000);
      this.callPacketTimeout = this.mainPreferences.getInt("callPacketTimeout", 10000);
      this.maxPinnedDialogsCount = this.mainPreferences.getInt("maxPinnedDialogsCount", 5);
      this.installReferer = this.mainPreferences.getString("installReferer", null);
      this.defaultP2pContacts = this.mainPreferences.getBoolean("defaultP2pContacts", false);
      this.revokeTimeLimit = this.mainPreferences.getInt("revokeTimeLimit", this.revokeTimeLimit);
      this.revokeTimePmLimit = this.mainPreferences.getInt("revokeTimePmLimit", this.revokeTimePmLimit);
      this.canRevokePmInbox = this.mainPreferences.getBoolean("canRevokePmInbox", this.canRevokePmInbox);
      this.preloadFeaturedStickers = this.mainPreferences.getBoolean("preloadFeaturedStickers", false);
      return;
      this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications" + this.currentAccount, 0);
      this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig" + this.currentAccount, 0);
    }
  }
  
  private void applyDialogNotificationsSettings(long paramLong, TLRPC.PeerNotifySettings paramPeerNotifySettings)
  {
    int i = this.notificationsPreferences.getInt("notify2_" + paramLong, 0);
    int j = this.notificationsPreferences.getInt("notifyuntil_" + paramLong, 0);
    SharedPreferences.Editor localEditor = this.notificationsPreferences.edit();
    int k = 0;
    int m = 0;
    int n = 0;
    TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)this.dialogs_dict.get(paramLong);
    if (localTL_dialog != null) {
      localTL_dialog.notify_settings = paramPeerNotifySettings;
    }
    localEditor.putBoolean("silent_" + paramLong, paramPeerNotifySettings.silent);
    if (paramPeerNotifySettings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())
    {
      m = 0;
      if (paramPeerNotifySettings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000)
      {
        k = m;
        if (i != 2)
        {
          i = 1;
          localEditor.putInt("notify2_" + paramLong, 2);
          k = m;
          n = i;
          if (localTL_dialog != null)
          {
            localTL_dialog.notify_settings.mute_until = Integer.MAX_VALUE;
            n = i;
            k = m;
          }
        }
        MessagesStorage.getInstance(this.currentAccount).setDialogFlags(paramLong, k << 32 | 1L);
        NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(paramLong);
      }
    }
    for (;;)
    {
      localEditor.commit();
      if (n != 0) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
      }
      return;
      if (i == 3)
      {
        n = k;
        if (j == paramPeerNotifySettings.mute_until) {}
      }
      else
      {
        k = 1;
        localEditor.putInt("notify2_" + paramLong, 3);
        localEditor.putInt("notifyuntil_" + paramLong, paramPeerNotifySettings.mute_until);
        n = k;
        if (localTL_dialog != null)
        {
          localTL_dialog.notify_settings.mute_until = 0;
          n = k;
        }
      }
      k = paramPeerNotifySettings.mute_until;
      break;
      n = m;
      if (i != 0)
      {
        n = m;
        if (i != 1)
        {
          n = 1;
          if (localTL_dialog != null) {
            localTL_dialog.notify_settings.mute_until = 0;
          }
          localEditor.remove("notify2_" + paramLong);
        }
      }
      MessagesStorage.getInstance(this.currentAccount).setDialogFlags(paramLong, 0L);
    }
  }
  
  private void applyDialogsNotificationsSettings(ArrayList<TLRPC.TL_dialog> paramArrayList)
  {
    Object localObject1 = null;
    int i = 0;
    if (i < paramArrayList.size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)paramArrayList.get(i);
      Object localObject2 = localObject1;
      int j;
      if (localTL_dialog.peer != null)
      {
        localObject2 = localObject1;
        if ((localTL_dialog.notify_settings instanceof TLRPC.TL_peerNotifySettings))
        {
          localObject2 = localObject1;
          if (localObject1 == null) {
            localObject2 = this.notificationsPreferences.edit();
          }
          if (localTL_dialog.peer.user_id == 0) {
            break label209;
          }
          j = localTL_dialog.peer.user_id;
          label86:
          ((SharedPreferences.Editor)localObject2).putBoolean("silent_" + j, localTL_dialog.notify_settings.silent);
          if (localTL_dialog.notify_settings.mute_until == 0) {
            break label318;
          }
          if (localTL_dialog.notify_settings.mute_until <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000) {
            break label248;
          }
          ((SharedPreferences.Editor)localObject2).putInt("notify2_" + j, 2);
          localTL_dialog.notify_settings.mute_until = Integer.MAX_VALUE;
        }
      }
      for (;;)
      {
        i++;
        localObject1 = localObject2;
        break;
        label209:
        if (localTL_dialog.peer.chat_id != 0)
        {
          j = -localTL_dialog.peer.chat_id;
          break label86;
        }
        j = -localTL_dialog.peer.channel_id;
        break label86;
        label248:
        ((SharedPreferences.Editor)localObject2).putInt("notify2_" + j, 3);
        ((SharedPreferences.Editor)localObject2).putInt("notifyuntil_" + j, localTL_dialog.notify_settings.mute_until);
        continue;
        label318:
        ((SharedPreferences.Editor)localObject2).remove("notify2_" + j);
      }
    }
    if (localObject1 != null) {
      ((SharedPreferences.Editor)localObject1).commit();
    }
  }
  
  private void checkChannelError(String paramString, int paramInt)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      }
      break;
    }
    for (;;)
    {
      return;
      if (!paramString.equals("CHANNEL_PRIVATE")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("CHANNEL_PUBLIC_GROUP_NA")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("USER_BANNED_IN_CHANNEL")) {
        break;
      }
      i = 2;
      break;
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(0) });
      continue;
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(1) });
      continue;
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, new Object[] { Integer.valueOf(paramInt), Integer.valueOf(2) });
    }
  }
  
  private boolean checkDeletingTask(boolean paramBoolean)
  {
    boolean bool1 = false;
    int i = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
    boolean bool2 = bool1;
    if (this.currentDeletingTaskMids != null) {
      if (!paramBoolean)
      {
        bool2 = bool1;
        if (this.currentDeletingTaskTime != 0)
        {
          bool2 = bool1;
          if (this.currentDeletingTaskTime > i) {}
        }
      }
      else
      {
        this.currentDeletingTaskTime = 0;
        if ((this.currentDeleteTaskRunnable != null) && (!paramBoolean)) {
          Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        }
        this.currentDeleteTaskRunnable = null;
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if ((!this.val$mids.isEmpty()) && (((Integer)this.val$mids.get(0)).intValue() > 0)) {
              MessagesStorage.getInstance(MessagesController.this.currentAccount).emptyMessagesMedia(this.val$mids);
            }
            for (;;)
            {
              Utilities.stageQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.getNewDeleteTask(MessagesController.26.this.val$mids, MessagesController.this.currentDeletingTaskChannelId);
                  MessagesController.access$4102(MessagesController.this, 0);
                  MessagesController.access$3902(MessagesController.this, null);
                }
              });
              return;
              MessagesController.this.deleteMessages(this.val$mids, null, null, 0, false);
            }
          }
        });
        bool2 = true;
      }
    }
    return bool2;
  }
  
  private void checkReadTasks()
  {
    long l = SystemClock.uptimeMillis();
    int i = 0;
    int j = this.readTasks.size();
    if (i < j)
    {
      ReadTask localReadTask = (ReadTask)this.readTasks.get(i);
      if (localReadTask.sendRequestTime > l) {}
      for (;;)
      {
        i++;
        break;
        completeReadTask(localReadTask);
        this.readTasks.remove(i);
        this.readTasksMap.remove(localReadTask.dialogId);
        i--;
        j--;
      }
    }
  }
  
  private void completeReadTask(ReadTask paramReadTask)
  {
    int i = (int)paramReadTask.dialogId;
    int j = (int)(paramReadTask.dialogId >> 32);
    Object localObject1;
    Object localObject2;
    if (i != 0)
    {
      localObject1 = getInputPeer(i);
      if ((localObject1 instanceof TLRPC.TL_inputPeerChannel))
      {
        localObject2 = new TLRPC.TL_channels_readHistory();
        ((TLRPC.TL_channels_readHistory)localObject2).channel = getInputChannel(-i);
        ((TLRPC.TL_channels_readHistory)localObject2).max_id = paramReadTask.maxId;
        paramReadTask = (ReadTask)localObject2;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramReadTask, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if ((paramAnonymousTL_error == null) && ((paramAnonymousTLObject instanceof TLRPC.TL_messages_affectedMessages)))
            {
              paramAnonymousTLObject = (TLRPC.TL_messages_affectedMessages)paramAnonymousTLObject;
              MessagesController.this.processNewDifferenceParams(-1, paramAnonymousTLObject.pts, -1, paramAnonymousTLObject.pts_count);
            }
          }
        });
      }
    }
    for (;;)
    {
      return;
      localObject2 = new TLRPC.TL_messages_readHistory();
      ((TLRPC.TL_messages_readHistory)localObject2).peer = ((TLRPC.InputPeer)localObject1);
      ((TLRPC.TL_messages_readHistory)localObject2).max_id = paramReadTask.maxId;
      paramReadTask = (ReadTask)localObject2;
      break;
      localObject1 = getEncryptedChat(Integer.valueOf(j));
      if ((((TLRPC.EncryptedChat)localObject1).auth_key != null) && (((TLRPC.EncryptedChat)localObject1).auth_key.length > 1) && ((localObject1 instanceof TLRPC.TL_encryptedChat)))
      {
        localObject2 = new TLRPC.TL_messages_readEncryptedHistory();
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).peer = new TLRPC.TL_inputEncryptedChat();
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).peer.chat_id = ((TLRPC.EncryptedChat)localObject1).id;
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).peer.access_hash = ((TLRPC.EncryptedChat)localObject1).access_hash;
        ((TLRPC.TL_messages_readEncryptedHistory)localObject2).max_date = paramReadTask.maxDate;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject2, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
        });
      }
    }
  }
  
  private void deleteDialog(final long paramLong, boolean paramBoolean, int paramInt1, final int paramInt2)
  {
    int i = (int)paramLong;
    int j = (int)(paramLong >> 32);
    int k = paramInt2;
    if (paramInt1 == 2) {
      MessagesStorage.getInstance(this.currentAccount).deleteDialog(paramLong, paramInt1);
    }
    for (;;)
    {
      return;
      if ((paramInt1 == 0) || (paramInt1 == 3)) {
        DataQuery.getInstance(this.currentAccount).uninstallShortcut(paramLong);
      }
      paramInt2 = k;
      label228:
      Object localObject2;
      if (paramBoolean)
      {
        MessagesStorage.getInstance(this.currentAccount).deleteDialog(paramLong, paramInt1);
        localObject1 = (TLRPC.TL_dialog)this.dialogs_dict.get(paramLong);
        paramInt2 = k;
        if (localObject1 != null)
        {
          paramInt2 = k;
          if (k == 0) {
            paramInt2 = Math.max(0, ((TLRPC.TL_dialog)localObject1).top_message);
          }
          if ((paramInt1 == 0) || (paramInt1 == 3))
          {
            this.dialogs.remove(localObject1);
            if ((this.dialogsServerOnly.remove(localObject1)) && (DialogObject.isChannel((TLRPC.TL_dialog)localObject1))) {
              Utilities.stageQueue.postRunnable(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.channelsPts.delete(-(int)paramLong);
                  MessagesController.this.shortPollChannels.delete(-(int)paramLong);
                  MessagesController.this.needShortPollChannels.delete(-(int)paramLong);
                }
              });
            }
            this.dialogsGroupsOnly.remove(localObject1);
            this.dialogs_dict.remove(paramLong);
            this.dialogs_read_inbox_max.remove(Long.valueOf(paramLong));
            this.dialogs_read_outbox_max.remove(Long.valueOf(paramLong));
            this.nextDialogsCacheOffset -= 1;
            localObject2 = (MessageObject)this.dialogMessage.get(((TLRPC.TL_dialog)localObject1).id);
            this.dialogMessage.remove(((TLRPC.TL_dialog)localObject1).id);
            if (localObject2 == null) {
              break label745;
            }
            k = ((MessageObject)localObject2).getId();
            this.dialogMessagesByIds.remove(((MessageObject)localObject2).getId());
            label281:
            if ((localObject2 != null) && (((MessageObject)localObject2).messageOwner.random_id != 0L)) {
              this.dialogMessagesByRandomIds.remove(((MessageObject)localObject2).messageOwner.random_id);
            }
            if ((paramInt1 != 1) || (i == 0) || (k <= 0)) {
              break label857;
            }
            localObject2 = new TLRPC.TL_messageService();
            ((TLRPC.TL_messageService)localObject2).id = ((TLRPC.TL_dialog)localObject1).top_message;
            if (UserConfig.getInstance(this.currentAccount).getClientUserId() != paramLong) {
              break label784;
            }
            paramBoolean = true;
            label367:
            ((TLRPC.TL_messageService)localObject2).out = paramBoolean;
            ((TLRPC.TL_messageService)localObject2).from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
            ((TLRPC.TL_messageService)localObject2).flags |= 0x100;
            ((TLRPC.TL_messageService)localObject2).action = new TLRPC.TL_messageActionHistoryClear();
            ((TLRPC.TL_messageService)localObject2).date = ((TLRPC.TL_dialog)localObject1).last_message_date;
            if (i <= 0) {
              break label789;
            }
            ((TLRPC.TL_messageService)localObject2).to_id = new TLRPC.TL_peerUser();
            ((TLRPC.TL_messageService)localObject2).to_id.user_id = i;
            label451:
            Object localObject3 = new MessageObject(this.currentAccount, (TLRPC.Message)localObject2, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_messageService)localObject2).dialog_id)));
            localObject1 = new ArrayList();
            ((ArrayList)localObject1).add(localObject3);
            localObject3 = new ArrayList();
            ((ArrayList)localObject3).add(localObject2);
            updateInterfaceWithMessages(paramLong, (ArrayList)localObject1);
            MessagesStorage.getInstance(this.currentAccount).putMessages((ArrayList)localObject3, false, true, false, 0);
          }
        }
        else
        {
          label538:
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, new Object[] { Long.valueOf(paramLong), Boolean.valueOf(false) });
          MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
          {
            public void run()
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationsController.getInstance(MessagesController.this.currentAccount).removeNotificationsForDialog(MessagesController.44.this.val$did);
                }
              });
            }
          });
        }
      }
      else
      {
        if ((j == 1) || (paramInt1 == 3)) {
          continue;
        }
        if (i == 0) {
          break label963;
        }
        localObject2 = getInputPeer(i);
        if (localObject2 == null) {
          continue;
        }
        if (!(localObject2 instanceof TLRPC.TL_inputPeerChannel)) {
          break label874;
        }
        if (paramInt1 == 0) {
          continue;
        }
        localObject1 = new TLRPC.TL_channels_deleteHistory();
        ((TLRPC.TL_channels_deleteHistory)localObject1).channel = new TLRPC.TL_inputChannel();
        ((TLRPC.TL_channels_deleteHistory)localObject1).channel.channel_id = ((TLRPC.InputPeer)localObject2).channel_id;
        ((TLRPC.TL_channels_deleteHistory)localObject1).channel.access_hash = ((TLRPC.InputPeer)localObject2).access_hash;
        if (paramInt2 <= 0) {
          break label866;
        }
      }
      for (;;)
      {
        ((TLRPC.TL_channels_deleteHistory)localObject1).max_id = paramInt2;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
        }, 64);
        break;
        ((TLRPC.TL_dialog)localObject1).unread_count = 0;
        break label228;
        label745:
        k = ((TLRPC.TL_dialog)localObject1).top_message;
        localObject2 = (MessageObject)this.dialogMessagesByIds.get(((TLRPC.TL_dialog)localObject1).top_message);
        this.dialogMessagesByIds.remove(((TLRPC.TL_dialog)localObject1).top_message);
        break label281;
        label784:
        paramBoolean = false;
        break label367;
        label789:
        if (ChatObject.isChannel(getChat(Integer.valueOf(-i))))
        {
          ((TLRPC.TL_messageService)localObject2).to_id = new TLRPC.TL_peerChannel();
          ((TLRPC.TL_messageService)localObject2).to_id.channel_id = (-i);
          break label451;
        }
        ((TLRPC.TL_messageService)localObject2).to_id = new TLRPC.TL_peerChat();
        ((TLRPC.TL_messageService)localObject2).to_id.chat_id = (-i);
        break label451;
        label857:
        ((TLRPC.TL_dialog)localObject1).top_message = 0;
        break label538;
        label866:
        paramInt2 = Integer.MAX_VALUE;
      }
      label874:
      Object localObject1 = new TLRPC.TL_messages_deleteHistory();
      ((TLRPC.TL_messages_deleteHistory)localObject1).peer = ((TLRPC.InputPeer)localObject2);
      if (paramInt1 == 0)
      {
        k = Integer.MAX_VALUE;
        label900:
        ((TLRPC.TL_messages_deleteHistory)localObject1).max_id = k;
        if (paramInt1 == 0) {
          break label958;
        }
      }
      label958:
      for (paramBoolean = true;; paramBoolean = false)
      {
        ((TLRPC.TL_messages_deleteHistory)localObject1).just_clear = paramBoolean;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = (TLRPC.TL_messages_affectedHistory)paramAnonymousTLObject;
              if (paramAnonymousTLObject.offset > 0) {
                MessagesController.this.deleteDialog(paramLong, false, paramInt2, this.val$max_id_delete_final);
              }
              MessagesController.this.processNewDifferenceParams(-1, paramAnonymousTLObject.pts, -1, paramAnonymousTLObject.pts_count);
            }
          }
        }, 64);
        break;
        k = paramInt2;
        break label900;
      }
      label963:
      if (paramInt1 == 1) {
        SecretChatHelper.getInstance(this.currentAccount).sendClearHistoryMessage(getEncryptedChat(Integer.valueOf(j)), null);
      } else {
        SecretChatHelper.getInstance(this.currentAccount).declineSecretChat(j);
      }
    }
  }
  
  private void getChannelDifference(int paramInt)
  {
    getChannelDifference(paramInt, 0, 0L, null);
  }
  
  public static SharedPreferences getEmojiSettings(int paramInt)
  {
    return getInstance(paramInt).emojiPreferences;
  }
  
  public static SharedPreferences getGlobalEmojiSettings()
  {
    return getInstance(0).emojiPreferences;
  }
  
  public static SharedPreferences getGlobalMainSettings()
  {
    return getInstance(0).mainPreferences;
  }
  
  public static SharedPreferences getGlobalNotificationsSettings()
  {
    return getInstance(0).notificationsPreferences;
  }
  
  public static TLRPC.InputChannel getInputChannel(TLRPC.Chat paramChat)
  {
    TLRPC.TL_inputChannel localTL_inputChannel;
    if (((paramChat instanceof TLRPC.TL_channel)) || ((paramChat instanceof TLRPC.TL_channelForbidden)))
    {
      localTL_inputChannel = new TLRPC.TL_inputChannel();
      localTL_inputChannel.channel_id = paramChat.id;
      localTL_inputChannel.access_hash = paramChat.access_hash;
    }
    for (paramChat = localTL_inputChannel;; paramChat = new TLRPC.TL_inputChannelEmpty()) {
      return paramChat;
    }
  }
  
  public static MessagesController getInstance(int paramInt)
  {
    Object localObject1 = Instance[paramInt];
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject1 = Instance[paramInt];
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = Instance;
        localObject2 = new org/telegram/messenger/MessagesController;
        ((MessagesController)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (MessagesController)localObject2;
    }
    finally {}
  }
  
  public static SharedPreferences getMainSettings(int paramInt)
  {
    return getInstance(paramInt).mainPreferences;
  }
  
  public static SharedPreferences getNotificationsSettings(int paramInt)
  {
    return getInstance(paramInt).notificationsPreferences;
  }
  
  private static String getRestrictionReason(String paramString)
  {
    Object localObject1 = null;
    Object localObject2 = localObject1;
    if (paramString != null)
    {
      if (paramString.length() != 0) {
        break label19;
      }
      localObject2 = localObject1;
    }
    for (;;)
    {
      return (String)localObject2;
      label19:
      int i = paramString.indexOf(": ");
      localObject2 = localObject1;
      if (i > 0)
      {
        String str = paramString.substring(0, i);
        if (!str.contains("-all"))
        {
          localObject2 = localObject1;
          if (!str.contains("-android")) {}
        }
        else
        {
          localObject2 = paramString.substring(i + 2);
        }
      }
    }
  }
  
  private static int getUpdateChannelId(TLRPC.Update paramUpdate)
  {
    int i;
    if ((paramUpdate instanceof TLRPC.TL_updateNewChannelMessage)) {
      i = ((TLRPC.TL_updateNewChannelMessage)paramUpdate).message.to_id.channel_id;
    }
    for (;;)
    {
      return i;
      if ((paramUpdate instanceof TLRPC.TL_updateEditChannelMessage))
      {
        i = ((TLRPC.TL_updateEditChannelMessage)paramUpdate).message.to_id.channel_id;
      }
      else if ((paramUpdate instanceof TLRPC.TL_updateReadChannelOutbox))
      {
        i = ((TLRPC.TL_updateReadChannelOutbox)paramUpdate).channel_id;
      }
      else if ((paramUpdate instanceof TLRPC.TL_updateChannelMessageViews))
      {
        i = ((TLRPC.TL_updateChannelMessageViews)paramUpdate).channel_id;
      }
      else if ((paramUpdate instanceof TLRPC.TL_updateChannelTooLong))
      {
        i = ((TLRPC.TL_updateChannelTooLong)paramUpdate).channel_id;
      }
      else if ((paramUpdate instanceof TLRPC.TL_updateChannelPinnedMessage))
      {
        i = ((TLRPC.TL_updateChannelPinnedMessage)paramUpdate).channel_id;
      }
      else if ((paramUpdate instanceof TLRPC.TL_updateChannelReadMessagesContents))
      {
        i = ((TLRPC.TL_updateChannelReadMessagesContents)paramUpdate).channel_id;
      }
      else if ((paramUpdate instanceof TLRPC.TL_updateChannelAvailableMessages))
      {
        i = ((TLRPC.TL_updateChannelAvailableMessages)paramUpdate).channel_id;
      }
      else if ((paramUpdate instanceof TLRPC.TL_updateChannel))
      {
        i = ((TLRPC.TL_updateChannel)paramUpdate).channel_id;
      }
      else if ((paramUpdate instanceof TLRPC.TL_updateChannelWebPage))
      {
        i = ((TLRPC.TL_updateChannelWebPage)paramUpdate).channel_id;
      }
      else if ((paramUpdate instanceof TLRPC.TL_updateDeleteChannelMessages))
      {
        i = ((TLRPC.TL_updateDeleteChannelMessages)paramUpdate).channel_id;
      }
      else if ((paramUpdate instanceof TLRPC.TL_updateReadChannelInbox))
      {
        i = ((TLRPC.TL_updateReadChannelInbox)paramUpdate).channel_id;
      }
      else
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e("trying to get unknown update channel_id for " + paramUpdate);
        }
        i = 0;
      }
    }
  }
  
  private static int getUpdatePts(TLRPC.Update paramUpdate)
  {
    int i;
    if ((paramUpdate instanceof TLRPC.TL_updateDeleteMessages)) {
      i = ((TLRPC.TL_updateDeleteMessages)paramUpdate).pts;
    }
    for (;;)
    {
      return i;
      if ((paramUpdate instanceof TLRPC.TL_updateNewChannelMessage)) {
        i = ((TLRPC.TL_updateNewChannelMessage)paramUpdate).pts;
      } else if ((paramUpdate instanceof TLRPC.TL_updateReadHistoryOutbox)) {
        i = ((TLRPC.TL_updateReadHistoryOutbox)paramUpdate).pts;
      } else if ((paramUpdate instanceof TLRPC.TL_updateNewMessage)) {
        i = ((TLRPC.TL_updateNewMessage)paramUpdate).pts;
      } else if ((paramUpdate instanceof TLRPC.TL_updateEditMessage)) {
        i = ((TLRPC.TL_updateEditMessage)paramUpdate).pts;
      } else if ((paramUpdate instanceof TLRPC.TL_updateWebPage)) {
        i = ((TLRPC.TL_updateWebPage)paramUpdate).pts;
      } else if ((paramUpdate instanceof TLRPC.TL_updateReadHistoryInbox)) {
        i = ((TLRPC.TL_updateReadHistoryInbox)paramUpdate).pts;
      } else if ((paramUpdate instanceof TLRPC.TL_updateChannelWebPage)) {
        i = ((TLRPC.TL_updateChannelWebPage)paramUpdate).pts;
      } else if ((paramUpdate instanceof TLRPC.TL_updateDeleteChannelMessages)) {
        i = ((TLRPC.TL_updateDeleteChannelMessages)paramUpdate).pts;
      } else if ((paramUpdate instanceof TLRPC.TL_updateEditChannelMessage)) {
        i = ((TLRPC.TL_updateEditChannelMessage)paramUpdate).pts;
      } else if ((paramUpdate instanceof TLRPC.TL_updateReadMessagesContents)) {
        i = ((TLRPC.TL_updateReadMessagesContents)paramUpdate).pts;
      } else if ((paramUpdate instanceof TLRPC.TL_updateChannelTooLong)) {
        i = ((TLRPC.TL_updateChannelTooLong)paramUpdate).pts;
      } else {
        i = 0;
      }
    }
  }
  
  private static int getUpdatePtsCount(TLRPC.Update paramUpdate)
  {
    int i;
    if ((paramUpdate instanceof TLRPC.TL_updateDeleteMessages)) {
      i = ((TLRPC.TL_updateDeleteMessages)paramUpdate).pts_count;
    }
    for (;;)
    {
      return i;
      if ((paramUpdate instanceof TLRPC.TL_updateNewChannelMessage)) {
        i = ((TLRPC.TL_updateNewChannelMessage)paramUpdate).pts_count;
      } else if ((paramUpdate instanceof TLRPC.TL_updateReadHistoryOutbox)) {
        i = ((TLRPC.TL_updateReadHistoryOutbox)paramUpdate).pts_count;
      } else if ((paramUpdate instanceof TLRPC.TL_updateNewMessage)) {
        i = ((TLRPC.TL_updateNewMessage)paramUpdate).pts_count;
      } else if ((paramUpdate instanceof TLRPC.TL_updateEditMessage)) {
        i = ((TLRPC.TL_updateEditMessage)paramUpdate).pts_count;
      } else if ((paramUpdate instanceof TLRPC.TL_updateWebPage)) {
        i = ((TLRPC.TL_updateWebPage)paramUpdate).pts_count;
      } else if ((paramUpdate instanceof TLRPC.TL_updateReadHistoryInbox)) {
        i = ((TLRPC.TL_updateReadHistoryInbox)paramUpdate).pts_count;
      } else if ((paramUpdate instanceof TLRPC.TL_updateChannelWebPage)) {
        i = ((TLRPC.TL_updateChannelWebPage)paramUpdate).pts_count;
      } else if ((paramUpdate instanceof TLRPC.TL_updateDeleteChannelMessages)) {
        i = ((TLRPC.TL_updateDeleteChannelMessages)paramUpdate).pts_count;
      } else if ((paramUpdate instanceof TLRPC.TL_updateEditChannelMessage)) {
        i = ((TLRPC.TL_updateEditChannelMessage)paramUpdate).pts_count;
      } else if ((paramUpdate instanceof TLRPC.TL_updateReadMessagesContents)) {
        i = ((TLRPC.TL_updateReadMessagesContents)paramUpdate).pts_count;
      } else {
        i = 0;
      }
    }
  }
  
  private static int getUpdateQts(TLRPC.Update paramUpdate)
  {
    if ((paramUpdate instanceof TLRPC.TL_updateNewEncryptedMessage)) {}
    for (int i = ((TLRPC.TL_updateNewEncryptedMessage)paramUpdate).qts;; i = 0) {
      return i;
    }
  }
  
  private int getUpdateSeq(TLRPC.Updates paramUpdates)
  {
    if ((paramUpdates instanceof TLRPC.TL_updatesCombined)) {}
    for (int i = paramUpdates.seq_start;; i = paramUpdates.seq) {
      return i;
    }
  }
  
  private int getUpdateType(TLRPC.Update paramUpdate)
  {
    int i;
    if (((paramUpdate instanceof TLRPC.TL_updateNewMessage)) || ((paramUpdate instanceof TLRPC.TL_updateReadMessagesContents)) || ((paramUpdate instanceof TLRPC.TL_updateReadHistoryInbox)) || ((paramUpdate instanceof TLRPC.TL_updateReadHistoryOutbox)) || ((paramUpdate instanceof TLRPC.TL_updateDeleteMessages)) || ((paramUpdate instanceof TLRPC.TL_updateWebPage)) || ((paramUpdate instanceof TLRPC.TL_updateEditMessage))) {
      i = 0;
    }
    for (;;)
    {
      return i;
      if ((paramUpdate instanceof TLRPC.TL_updateNewEncryptedMessage)) {
        i = 1;
      } else if (((paramUpdate instanceof TLRPC.TL_updateNewChannelMessage)) || ((paramUpdate instanceof TLRPC.TL_updateDeleteChannelMessages)) || ((paramUpdate instanceof TLRPC.TL_updateEditChannelMessage)) || ((paramUpdate instanceof TLRPC.TL_updateChannelWebPage))) {
        i = 2;
      } else {
        i = 3;
      }
    }
  }
  
  private String getUserNameForTyping(TLRPC.User paramUser)
  {
    if (paramUser == null) {
      paramUser = "";
    }
    for (;;)
    {
      return paramUser;
      if ((paramUser.first_name != null) && (paramUser.first_name.length() > 0)) {
        paramUser = paramUser.first_name;
      } else if ((paramUser.last_name != null) && (paramUser.last_name.length() > 0)) {
        paramUser = paramUser.last_name;
      } else {
        paramUser = "";
      }
    }
  }
  
  private boolean isNotifySettingsMuted(TLRPC.PeerNotifySettings paramPeerNotifySettings)
  {
    if (((paramPeerNotifySettings instanceof TLRPC.TL_peerNotifySettings)) && (paramPeerNotifySettings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isSupportId(int paramInt)
  {
    if ((paramInt / 1000 == 777) || (paramInt == 333000) || (paramInt == 4240000) || (paramInt == 4240000) || (paramInt == 4244000) || (paramInt == 4245000) || (paramInt == 4246000) || (paramInt == 410000) || (paramInt == 420000) || (paramInt == 431000) || (paramInt == 431415000) || (paramInt == 434000) || (paramInt == 4243000) || (paramInt == 439000) || (paramInt == 449000) || (paramInt == 450000) || (paramInt == 452000) || (paramInt == 454000) || (paramInt == 4254000) || (paramInt == 455000) || (paramInt == 460000) || (paramInt == 470000) || (paramInt == 479000) || (paramInt == 796000) || (paramInt == 482000) || (paramInt == 490000) || (paramInt == 496000) || (paramInt == 497000) || (paramInt == 498000) || (paramInt == 4298000)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private int isValidUpdate(TLRPC.Updates paramUpdates, int paramInt)
  {
    int i = 1;
    int j;
    if (paramInt == 0)
    {
      j = getUpdateSeq(paramUpdates);
      if ((MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 == j) || (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() == j)) {
        paramInt = 0;
      }
    }
    for (;;)
    {
      return paramInt;
      paramInt = i;
      if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() >= j)
      {
        paramInt = 2;
        continue;
        if (paramInt == 1)
        {
          if (paramUpdates.pts <= MessagesStorage.getInstance(this.currentAccount).getLastPtsValue())
          {
            paramInt = 2;
          }
          else
          {
            paramInt = i;
            if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + paramUpdates.pts_count == paramUpdates.pts) {
              paramInt = 0;
            }
          }
        }
        else if (paramInt == 2)
        {
          if (paramUpdates.pts <= MessagesStorage.getInstance(this.currentAccount).getLastQtsValue())
          {
            paramInt = 2;
          }
          else
          {
            paramInt = i;
            if (MessagesStorage.getInstance(this.currentAccount).getLastQtsValue() + paramUpdates.updates.size() == paramUpdates.pts) {
              paramInt = 0;
            }
          }
        }
        else {
          paramInt = 0;
        }
      }
    }
  }
  
  private void loadMessagesInternal(final long paramLong, final int paramInt1, final int paramInt2, final int paramInt3, boolean paramBoolean1, final int paramInt4, final int paramInt5, final int paramInt6, final int paramInt7, final boolean paramBoolean2, final int paramInt8, final int paramInt9, final int paramInt10, final int paramInt11, final boolean paramBoolean3, final int paramInt12, boolean paramBoolean4)
  {
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("load messages in chat " + paramLong + " count " + paramInt1 + " max_id " + paramInt2 + " cache " + paramBoolean1 + " mindate = " + paramInt4 + " guid " + paramInt5 + " load_type " + paramInt6 + " last_message_id " + paramInt7 + " index " + paramInt8 + " firstUnread " + paramInt9 + " unread_count " + paramInt10 + " last_date " + paramInt11 + " queryFromServer " + paramBoolean3);
    }
    int i = (int)paramLong;
    if ((paramBoolean1) || (i == 0)) {
      MessagesStorage.getInstance(this.currentAccount).getMessages(paramLong, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramInt6, paramBoolean2, paramInt8);
    }
    for (;;)
    {
      return;
      if ((!paramBoolean4) || ((paramInt6 != 3) && (paramInt6 != 2)) || (paramInt7 != 0)) {
        break;
      }
      TLRPC.TL_messages_getPeerDialogs localTL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
      TLRPC.InputPeer localInputPeer = getInputPeer((int)paramLong);
      localObject = new TLRPC.TL_inputDialogPeer();
      ((TLRPC.TL_inputDialogPeer)localObject).peer = localInputPeer;
      localTL_messages_getPeerDialogs.peers.add(localObject);
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getPeerDialogs, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null)
          {
            TLRPC.TL_messages_peerDialogs localTL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs)paramAnonymousTLObject;
            if (!localTL_messages_peerDialogs.dialogs.isEmpty())
            {
              paramAnonymousTL_error = (TLRPC.TL_dialog)localTL_messages_peerDialogs.dialogs.get(0);
              if (paramAnonymousTL_error.top_message != 0)
              {
                paramAnonymousTLObject = new TLRPC.TL_messages_dialogs();
                paramAnonymousTLObject.chats = localTL_messages_peerDialogs.chats;
                paramAnonymousTLObject.users = localTL_messages_peerDialogs.users;
                paramAnonymousTLObject.dialogs = localTL_messages_peerDialogs.dialogs;
                paramAnonymousTLObject.messages = localTL_messages_peerDialogs.messages;
                MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(paramAnonymousTLObject, false);
              }
              MessagesController.this.loadMessagesInternal(paramLong, paramInt2, paramInt3, paramInt4, false, paramInt5, paramInt6, paramBoolean2, paramAnonymousTL_error.top_message, paramInt8, paramInt9, paramInt11, paramAnonymousTL_error.unread_count, paramBoolean3, this.val$queryFromServer, paramAnonymousTL_error.unread_mentions_count, false);
            }
          }
        }
      });
    }
    Object localObject = new TLRPC.TL_messages_getHistory();
    ((TLRPC.TL_messages_getHistory)localObject).peer = getInputPeer(i);
    if (paramInt6 == 4) {
      ((TLRPC.TL_messages_getHistory)localObject).add_offset = (-paramInt1 + 5);
    }
    for (;;)
    {
      ((TLRPC.TL_messages_getHistory)localObject).limit = paramInt1;
      ((TLRPC.TL_messages_getHistory)localObject).offset_id = paramInt2;
      ((TLRPC.TL_messages_getHistory)localObject).offset_date = paramInt3;
      paramInt1 = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          int j;
          int k;
          if (paramAnonymousTLObject != null)
          {
            paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
            if (paramAnonymousTLObject.messages.size() > paramInt1) {
              paramAnonymousTLObject.messages.remove(0);
            }
            i = paramInt2;
            j = i;
            if (paramInt3 != 0)
            {
              j = i;
              if (!paramAnonymousTLObject.messages.isEmpty()) {
                k = ((TLRPC.Message)paramAnonymousTLObject.messages.get(paramAnonymousTLObject.messages.size() - 1)).id;
              }
            }
          }
          for (int i = paramAnonymousTLObject.messages.size() - 1;; i--)
          {
            j = k;
            if (i >= 0)
            {
              paramAnonymousTL_error = (TLRPC.Message)paramAnonymousTLObject.messages.get(i);
              if (paramAnonymousTL_error.date > paramInt3) {
                j = paramAnonymousTL_error.id;
              }
            }
            else
            {
              MessagesController.this.processLoadedMessages(paramAnonymousTLObject, paramLong, paramInt1, j, paramInt3, false, paramInt9, paramInt7, paramInt10, paramInt11, paramInt6, paramBoolean2, paramInt8, false, paramBoolean3, paramInt12, this.val$mentionsCount);
              return;
            }
          }
        }
      });
      ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(paramInt1, paramInt5);
      break;
      if (paramInt6 == 3)
      {
        ((TLRPC.TL_messages_getHistory)localObject).add_offset = (-paramInt1 / 2);
      }
      else if (paramInt6 == 1)
      {
        ((TLRPC.TL_messages_getHistory)localObject).add_offset = (-paramInt1 - 1);
      }
      else if ((paramInt6 == 2) && (paramInt2 != 0))
      {
        ((TLRPC.TL_messages_getHistory)localObject).add_offset = (-paramInt1 + 6);
      }
      else if ((i < 0) && (paramInt2 != 0) && (ChatObject.isChannel(getChat(Integer.valueOf(-i)))))
      {
        ((TLRPC.TL_messages_getHistory)localObject).add_offset = -1;
        ((TLRPC.TL_messages_getHistory)localObject).limit += 1;
      }
    }
  }
  
  private void migrateDialogs(final int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, long paramLong)
  {
    if ((this.migratingDialogs) || (paramInt1 == -1)) {}
    TLRPC.TL_messages_getDialogs localTL_messages_getDialogs;
    for (;;)
    {
      return;
      this.migratingDialogs = true;
      localTL_messages_getDialogs = new TLRPC.TL_messages_getDialogs();
      localTL_messages_getDialogs.exclude_pinned = true;
      localTL_messages_getDialogs.limit = 100;
      localTL_messages_getDialogs.offset_id = paramInt1;
      localTL_messages_getDialogs.offset_date = paramInt2;
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("start migrate with id " + paramInt1 + " date " + LocaleController.getInstance().formatterStats.format(paramInt2 * 1000L));
      }
      if (paramInt1 != 0) {
        break;
      }
      localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerEmpty();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getDialogs, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.messages_Dialogs)paramAnonymousTLObject;
            MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
            {
              public void run()
              {
                int i;
                Object localObject4;
                Object localObject5;
                for (;;)
                {
                  try
                  {
                    Object localObject1 = UserConfig.getInstance(MessagesController.this.currentAccount);
                    ((UserConfig)localObject1).totalDialogsLoadCount += paramAnonymousTLObject.dialogs.size();
                    localObject1 = null;
                    i = 0;
                    if (i < paramAnonymousTLObject.messages.size())
                    {
                      localObject3 = (TLRPC.Message)paramAnonymousTLObject.messages.get(i);
                      if (BuildVars.LOGS_ENABLED)
                      {
                        localObject4 = new java/lang/StringBuilder;
                        ((StringBuilder)localObject4).<init>();
                        FileLog.d("search migrate id " + ((TLRPC.Message)localObject3).id + " date " + LocaleController.getInstance().formatterStats.format(((TLRPC.Message)localObject3).date * 1000L));
                      }
                      if (localObject1 != null)
                      {
                        localObject4 = localObject1;
                        if (((TLRPC.Message)localObject3).date >= ((TLRPC.Message)localObject1).date) {}
                      }
                      else
                      {
                        localObject4 = localObject3;
                      }
                      i++;
                      localObject1 = localObject4;
                      continue;
                    }
                    if (BuildVars.LOGS_ENABLED)
                    {
                      localObject4 = new java/lang/StringBuilder;
                      ((StringBuilder)localObject4).<init>();
                      FileLog.d("migrate step with id " + ((TLRPC.Message)localObject1).id + " date " + LocaleController.getInstance().formatterStats.format(((TLRPC.Message)localObject1).date * 1000L));
                    }
                    if (paramAnonymousTLObject.dialogs.size() >= 100)
                    {
                      i = ((TLRPC.Message)localObject1).id;
                      localObject3 = new java/lang/StringBuilder;
                      ((StringBuilder)localObject3).<init>(paramAnonymousTLObject.dialogs.size() * 12);
                      localObject4 = new android/util/LongSparseArray;
                      ((LongSparseArray)localObject4).<init>();
                      j = 0;
                      if (j >= paramAnonymousTLObject.dialogs.size()) {
                        break;
                      }
                      localObject5 = (TLRPC.TL_dialog)paramAnonymousTLObject.dialogs.get(j);
                      if (((TLRPC.TL_dialog)localObject5).peer.channel_id != 0)
                      {
                        ((TLRPC.TL_dialog)localObject5).id = (-((TLRPC.TL_dialog)localObject5).peer.channel_id);
                        if (((StringBuilder)localObject3).length() > 0) {
                          ((StringBuilder)localObject3).append(",");
                        }
                        ((StringBuilder)localObject3).append(((TLRPC.TL_dialog)localObject5).id);
                        ((LongSparseArray)localObject4).put(((TLRPC.TL_dialog)localObject5).id, localObject5);
                        j++;
                        continue;
                      }
                    }
                    else
                    {
                      if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("migrate stop due to not 100 dialogs");
                      }
                      UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = Integer.MAX_VALUE;
                      UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                      UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                      UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                      UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                      UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
                      i = -1;
                      continue;
                    }
                    if (((TLRPC.TL_dialog)localObject5).peer.chat_id != 0)
                    {
                      ((TLRPC.TL_dialog)localObject5).id = (-((TLRPC.TL_dialog)localObject5).peer.chat_id);
                      continue;
                      return;
                    }
                  }
                  catch (Exception localException)
                  {
                    FileLog.e(localException);
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        MessagesController.access$6002(MessagesController.this, false);
                      }
                    });
                  }
                  ((TLRPC.TL_dialog)localObject5).id = ((TLRPC.TL_dialog)localObject5).peer.user_id;
                }
                Object localObject6 = MessagesStorage.getInstance(MessagesController.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE did IN (%s)", new Object[] { ((StringBuilder)localObject3).toString() }), new Object[0]);
                long l;
                while (((SQLiteCursor)localObject6).next())
                {
                  l = ((SQLiteCursor)localObject6).longValue(0);
                  localObject5 = (TLRPC.TL_dialog)((LongSparseArray)localObject4).get(l);
                  ((LongSparseArray)localObject4).remove(l);
                  if (localObject5 != null)
                  {
                    paramAnonymousTLObject.dialogs.remove(localObject5);
                    j = 0;
                    label731:
                    if (j < paramAnonymousTLObject.messages.size())
                    {
                      localObject3 = (TLRPC.Message)paramAnonymousTLObject.messages.get(j);
                      if (MessageObject.getDialogId((TLRPC.Message)localObject3) == l) {
                        break label778;
                      }
                    }
                    label778:
                    do
                    {
                      j++;
                      break label731;
                      break;
                      paramAnonymousTLObject.messages.remove(j);
                      j--;
                    } while (((TLRPC.Message)localObject3).id != ((TLRPC.TL_dialog)localObject5).top_message);
                    ((TLRPC.TL_dialog)localObject5).top_message = 0;
                  }
                }
                ((SQLiteCursor)localObject6).dispose();
                if (BuildVars.LOGS_ENABLED)
                {
                  localObject3 = new java/lang/StringBuilder;
                  ((StringBuilder)localObject3).<init>();
                  FileLog.d("migrate found missing dialogs " + paramAnonymousTLObject.dialogs.size());
                }
                Object localObject3 = MessagesStorage.getInstance(MessagesController.this.currentAccount).getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
                int j = i;
                if (((SQLiteCursor)localObject3).next())
                {
                  int k = Math.max(NUM, ((SQLiteCursor)localObject3).intValue(0));
                  j = 0;
                  while (j < paramAnonymousTLObject.messages.size())
                  {
                    localObject5 = (TLRPC.Message)paramAnonymousTLObject.messages.get(j);
                    int m = i;
                    int n = j;
                    if (((TLRPC.Message)localObject5).date < k)
                    {
                      if (MessagesController.68.this.val$offset != -1)
                      {
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
                        m = -1;
                        i = m;
                        if (BuildVars.LOGS_ENABLED)
                        {
                          localObject6 = new java/lang/StringBuilder;
                          ((StringBuilder)localObject6).<init>();
                          FileLog.d("migrate stop due to reached loaded dialogs " + LocaleController.getInstance().formatterStats.format(k * 1000L));
                          i = m;
                        }
                      }
                      paramAnonymousTLObject.messages.remove(j);
                      j--;
                      l = MessageObject.getDialogId((TLRPC.Message)localObject5);
                      localObject5 = (TLRPC.TL_dialog)((LongSparseArray)localObject4).get(l);
                      ((LongSparseArray)localObject4).remove(l);
                      m = i;
                      n = j;
                      if (localObject5 != null)
                      {
                        paramAnonymousTLObject.dialogs.remove(localObject5);
                        n = j;
                        m = i;
                      }
                    }
                    j = n + 1;
                    i = m;
                  }
                  j = i;
                  if (localException != null)
                  {
                    j = i;
                    if (localException.date < k)
                    {
                      j = i;
                      if (MessagesController.68.this.val$offset != -1)
                      {
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
                        i = -1;
                        j = i;
                        if (BuildVars.LOGS_ENABLED)
                        {
                          localObject4 = new java/lang/StringBuilder;
                          ((StringBuilder)localObject4).<init>();
                          FileLog.d("migrate stop due to reached loaded dialogs " + LocaleController.getInstance().formatterStats.format(k * 1000L));
                          j = i;
                        }
                      }
                    }
                  }
                }
                ((SQLiteCursor)localObject3).dispose();
                UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate = localException.date;
                label1684:
                Object localObject2;
                if (localException.to_id.channel_id != 0)
                {
                  UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = localException.to_id.channel_id;
                  UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = 0;
                  UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = 0;
                  i = 0;
                  if (i < paramAnonymousTLObject.chats.size())
                  {
                    localObject2 = (TLRPC.Chat)paramAnonymousTLObject.chats.get(i);
                    if (((TLRPC.Chat)localObject2).id != UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId) {
                      break label1781;
                    }
                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = ((TLRPC.Chat)localObject2).access_hash;
                  }
                }
                label1781:
                label1935:
                label2085:
                for (;;)
                {
                  MessagesController.this.processLoadedDialogs(paramAnonymousTLObject, null, j, 0, 0, false, true, false);
                  break;
                  i++;
                  break label1684;
                  if (((TLRPC.Message)localObject2).to_id.chat_id != 0)
                  {
                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = ((TLRPC.Message)localObject2).to_id.chat_id;
                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = 0;
                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = 0;
                    for (i = 0;; i++)
                    {
                      if (i >= paramAnonymousTLObject.chats.size()) {
                        break label1935;
                      }
                      localObject2 = (TLRPC.Chat)paramAnonymousTLObject.chats.get(i);
                      if (((TLRPC.Chat)localObject2).id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId)
                      {
                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = ((TLRPC.Chat)localObject2).access_hash;
                        break;
                      }
                    }
                  }
                  else if (((TLRPC.Message)localObject2).to_id.user_id != 0)
                  {
                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = ((TLRPC.Message)localObject2).to_id.user_id;
                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = 0;
                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = 0;
                    for (i = 0;; i++)
                    {
                      if (i >= paramAnonymousTLObject.users.size()) {
                        break label2085;
                      }
                      localObject2 = (TLRPC.User)paramAnonymousTLObject.users.get(i);
                      if (((TLRPC.User)localObject2).id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId)
                      {
                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = ((TLRPC.User)localObject2).access_hash;
                        break;
                      }
                    }
                  }
                }
              }
            });
          }
          for (;;)
          {
            return;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.access$6002(MessagesController.this, false);
              }
            });
          }
        }
      });
    }
    if (paramInt5 != 0)
    {
      localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerChannel();
      localTL_messages_getDialogs.offset_peer.channel_id = paramInt5;
    }
    for (;;)
    {
      localTL_messages_getDialogs.offset_peer.access_hash = paramLong;
      break;
      if (paramInt3 != 0)
      {
        localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerUser();
        localTL_messages_getDialogs.offset_peer.user_id = paramInt3;
      }
      else
      {
        localTL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerChat();
        localTL_messages_getDialogs.offset_peer.chat_id = paramInt4;
      }
    }
  }
  
  public static void openChatOrProfileWith(TLRPC.User paramUser, TLRPC.Chat paramChat, BaseFragment paramBaseFragment, int paramInt, boolean paramBoolean)
  {
    if (((paramUser == null) && (paramChat == null)) || (paramBaseFragment == null)) {}
    for (;;)
    {
      return;
      Object localObject = null;
      boolean bool;
      int i;
      if (paramChat != null)
      {
        localObject = getRestrictionReason(paramChat.restriction_reason);
        bool = paramBoolean;
        i = paramInt;
      }
      for (;;)
      {
        if (localObject == null) {
          break label101;
        }
        showCantOpenAlert(paramBaseFragment, (String)localObject);
        break;
        i = paramInt;
        bool = paramBoolean;
        if (paramUser != null)
        {
          String str = getRestrictionReason(paramUser.restriction_reason);
          localObject = str;
          i = paramInt;
          bool = paramBoolean;
          if (paramUser.bot)
          {
            i = 1;
            bool = true;
            localObject = str;
          }
        }
      }
      label101:
      localObject = new Bundle();
      if (paramChat != null) {
        ((Bundle)localObject).putInt("chat_id", paramChat.id);
      }
      for (;;)
      {
        if (i != 0) {
          break label163;
        }
        paramBaseFragment.presentFragment(new ProfileActivity((Bundle)localObject));
        break;
        ((Bundle)localObject).putInt("user_id", paramUser.id);
      }
      label163:
      if (i == 2) {
        paramBaseFragment.presentFragment(new ChatActivity((Bundle)localObject), true, true);
      } else {
        paramBaseFragment.presentFragment(new ChatActivity((Bundle)localObject), bool);
      }
    }
  }
  
  private void processChannelsUpdatesQueue(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = (ArrayList)this.updatesQueueChannels.get(paramInt1);
    if (localArrayList == null) {}
    for (;;)
    {
      return;
      int i = this.channelsPts.get(paramInt1);
      if ((localArrayList.isEmpty()) || (i == 0))
      {
        this.updatesQueueChannels.remove(paramInt1);
      }
      else
      {
        Collections.sort(localArrayList, new Comparator()
        {
          public int compare(TLRPC.Updates paramAnonymousUpdates1, TLRPC.Updates paramAnonymousUpdates2)
          {
            return AndroidUtilities.compare(paramAnonymousUpdates1.pts, paramAnonymousUpdates2.pts);
          }
        });
        int j = 0;
        if (paramInt2 == 2) {
          this.channelsPts.put(paramInt1, ((TLRPC.Updates)localArrayList.get(0)).pts);
        }
        int k = 0;
        label92:
        if (localArrayList.size() > 0)
        {
          TLRPC.Updates localUpdates = (TLRPC.Updates)localArrayList.get(k);
          if (localUpdates.pts <= i)
          {
            paramInt2 = 2;
            label122:
            if (paramInt2 != 0) {
              break label182;
            }
            processUpdates(localUpdates, true);
            j = 1;
            localArrayList.remove(k);
          }
          for (paramInt2 = k - 1;; paramInt2 = k - 1)
          {
            k = paramInt2 + 1;
            break label92;
            if (localUpdates.pts_count + i == localUpdates.pts)
            {
              paramInt2 = 0;
              break label122;
            }
            paramInt2 = 1;
            break label122;
            label182:
            if (paramInt2 == 1)
            {
              long l = this.updatesStartWaitTimeChannels.get(paramInt1);
              if ((l != 0L) && ((j != 0) || (Math.abs(System.currentTimeMillis() - l) <= 1500L)))
              {
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.d("HOLE IN CHANNEL " + paramInt1 + " UPDATES QUEUE - will wait more time");
                }
                if (j == 0) {
                  break;
                }
                this.updatesStartWaitTimeChannels.put(paramInt1, System.currentTimeMillis());
                break;
              }
              if (BuildVars.LOGS_ENABLED) {
                FileLog.d("HOLE IN CHANNEL " + paramInt1 + " UPDATES QUEUE - getChannelDifference ");
              }
              this.updatesStartWaitTimeChannels.delete(paramInt1);
              this.updatesQueueChannels.remove(paramInt1);
              getChannelDifference(paramInt1);
              break;
            }
            localArrayList.remove(k);
          }
        }
        this.updatesQueueChannels.remove(paramInt1);
        this.updatesStartWaitTimeChannels.delete(paramInt1);
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("UPDATES CHANNEL " + paramInt1 + " QUEUE PROCEED - OK");
        }
      }
    }
  }
  
  private void processUpdatesQueue(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = null;
    int i;
    TLRPC.Updates localUpdates;
    if (paramInt1 == 0)
    {
      localArrayList = this.updatesQueueSeq;
      Collections.sort(localArrayList, new Comparator()
      {
        public int compare(TLRPC.Updates paramAnonymousUpdates1, TLRPC.Updates paramAnonymousUpdates2)
        {
          return AndroidUtilities.compare(MessagesController.this.getUpdateSeq(paramAnonymousUpdates1), MessagesController.this.getUpdateSeq(paramAnonymousUpdates2));
        }
      });
      if ((localArrayList == null) || (localArrayList.isEmpty())) {
        break label348;
      }
      i = 0;
      if (paramInt2 == 2)
      {
        localUpdates = (TLRPC.Updates)localArrayList.get(0);
        if (paramInt1 != 0) {
          break label185;
        }
        MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(getUpdateSeq(localUpdates));
      }
    }
    int j;
    for (;;)
    {
      j = 0;
      paramInt2 = i;
      for (;;)
      {
        if (localArrayList.size() <= 0) {
          break label332;
        }
        localUpdates = (TLRPC.Updates)localArrayList.get(j);
        i = isValidUpdate(localUpdates, paramInt1);
        if (i != 0) {
          break;
        }
        processUpdates(localUpdates, true);
        paramInt2 = 1;
        localArrayList.remove(j);
        j--;
        j++;
      }
      if (paramInt1 == 1)
      {
        localArrayList = this.updatesQueuePts;
        Collections.sort(localArrayList, new Comparator()
        {
          public int compare(TLRPC.Updates paramAnonymousUpdates1, TLRPC.Updates paramAnonymousUpdates2)
          {
            return AndroidUtilities.compare(paramAnonymousUpdates1.pts, paramAnonymousUpdates2.pts);
          }
        });
        break;
      }
      if (paramInt1 != 2) {
        break;
      }
      localArrayList = this.updatesQueueQts;
      Collections.sort(localArrayList, new Comparator()
      {
        public int compare(TLRPC.Updates paramAnonymousUpdates1, TLRPC.Updates paramAnonymousUpdates2)
        {
          return AndroidUtilities.compare(paramAnonymousUpdates1.pts, paramAnonymousUpdates2.pts);
        }
      });
      break;
      label185:
      if (paramInt1 == 1) {
        MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(localUpdates.pts);
      } else {
        MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(localUpdates.pts);
      }
    }
    if (i == 1) {
      if ((getUpdatesStartTime(paramInt1) != 0L) && ((paramInt2 != 0) || (Math.abs(System.currentTimeMillis() - getUpdatesStartTime(paramInt1)) <= 1500L)))
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("HOLE IN UPDATES QUEUE - will wait more time");
        }
        if (paramInt2 != 0) {
          setUpdatesStartTime(paramInt1, System.currentTimeMillis());
        }
      }
    }
    for (;;)
    {
      return;
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("HOLE IN UPDATES QUEUE - getDifference");
      }
      setUpdatesStartTime(paramInt1, 0L);
      localArrayList.clear();
      getDifference();
      continue;
      localArrayList.remove(j);
      j--;
      break;
      label332:
      localArrayList.clear();
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("UPDATES QUEUE PROCEED - OK");
      }
      label348:
      setUpdatesStartTime(paramInt1, 0L);
    }
  }
  
  private void reloadDialogsReadValue(ArrayList<TLRPC.TL_dialog> paramArrayList, long paramLong)
  {
    if ((paramLong == 0L) && ((paramArrayList == null) || (paramArrayList.isEmpty()))) {}
    for (;;)
    {
      return;
      TLRPC.TL_messages_getPeerDialogs localTL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
      TLRPC.TL_inputDialogPeer localTL_inputDialogPeer;
      if (paramArrayList != null)
      {
        int i = 0;
        if (i < paramArrayList.size())
        {
          TLRPC.InputPeer localInputPeer = getInputPeer((int)((TLRPC.TL_dialog)paramArrayList.get(i)).id);
          if (((localInputPeer instanceof TLRPC.TL_inputPeerChannel)) && (localInputPeer.access_hash == 0L)) {}
          for (;;)
          {
            i++;
            break;
            localTL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
            localTL_inputDialogPeer.peer = localInputPeer;
            localTL_messages_getPeerDialogs.peers.add(localTL_inputDialogPeer);
          }
        }
      }
      else
      {
        paramArrayList = getInputPeer((int)paramLong);
        if (((paramArrayList instanceof TLRPC.TL_inputPeerChannel)) && (paramArrayList.access_hash == 0L)) {
          continue;
        }
        localTL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
        localTL_inputDialogPeer.peer = paramArrayList;
        localTL_messages_getPeerDialogs.peers.add(localTL_inputDialogPeer);
      }
      if (!localTL_messages_getPeerDialogs.peers.isEmpty()) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getPeerDialogs, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTLObject != null)
            {
              TLRPC.TL_messages_peerDialogs localTL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs)paramAnonymousTLObject;
              ArrayList localArrayList = new ArrayList();
              int i = 0;
              if (i < localTL_messages_peerDialogs.dialogs.size())
              {
                TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)localTL_messages_peerDialogs.dialogs.get(i);
                if (localTL_dialog.read_inbox_max_id == 0) {
                  localTL_dialog.read_inbox_max_id = 1;
                }
                if (localTL_dialog.read_outbox_max_id == 0) {
                  localTL_dialog.read_outbox_max_id = 1;
                }
                if ((localTL_dialog.id == 0L) && (localTL_dialog.peer != null))
                {
                  if (localTL_dialog.peer.user_id != 0) {
                    localTL_dialog.id = localTL_dialog.peer.user_id;
                  }
                }
                else
                {
                  label118:
                  paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(localTL_dialog.id));
                  paramAnonymousTLObject = paramAnonymousTL_error;
                  if (paramAnonymousTL_error == null) {
                    paramAnonymousTLObject = Integer.valueOf(0);
                  }
                  MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(localTL_dialog.id), Integer.valueOf(Math.max(localTL_dialog.read_inbox_max_id, paramAnonymousTLObject.intValue())));
                  if (paramAnonymousTLObject.intValue() == 0)
                  {
                    if (localTL_dialog.peer.channel_id == 0) {
                      break label424;
                    }
                    paramAnonymousTLObject = new TLRPC.TL_updateReadChannelInbox();
                    paramAnonymousTLObject.channel_id = localTL_dialog.peer.channel_id;
                    paramAnonymousTLObject.max_id = localTL_dialog.read_inbox_max_id;
                    localArrayList.add(paramAnonymousTLObject);
                  }
                  label239:
                  paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(localTL_dialog.id));
                  paramAnonymousTLObject = paramAnonymousTL_error;
                  if (paramAnonymousTL_error == null) {
                    paramAnonymousTLObject = Integer.valueOf(0);
                  }
                  MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(localTL_dialog.id), Integer.valueOf(Math.max(localTL_dialog.read_outbox_max_id, paramAnonymousTLObject.intValue())));
                  if (paramAnonymousTLObject.intValue() == 0)
                  {
                    if (localTL_dialog.peer.channel_id == 0) {
                      break label460;
                    }
                    paramAnonymousTLObject = new TLRPC.TL_updateReadChannelOutbox();
                    paramAnonymousTLObject.channel_id = localTL_dialog.peer.channel_id;
                    paramAnonymousTLObject.max_id = localTL_dialog.read_outbox_max_id;
                    localArrayList.add(paramAnonymousTLObject);
                  }
                }
                for (;;)
                {
                  i++;
                  break;
                  if (localTL_dialog.peer.chat_id != 0)
                  {
                    localTL_dialog.id = (-localTL_dialog.peer.chat_id);
                    break label118;
                  }
                  if (localTL_dialog.peer.channel_id == 0) {
                    break label118;
                  }
                  localTL_dialog.id = (-localTL_dialog.peer.channel_id);
                  break label118;
                  label424:
                  paramAnonymousTLObject = new TLRPC.TL_updateReadHistoryInbox();
                  paramAnonymousTLObject.peer = localTL_dialog.peer;
                  paramAnonymousTLObject.max_id = localTL_dialog.read_inbox_max_id;
                  localArrayList.add(paramAnonymousTLObject);
                  break label239;
                  label460:
                  paramAnonymousTLObject = new TLRPC.TL_updateReadHistoryOutbox();
                  paramAnonymousTLObject.peer = localTL_dialog.peer;
                  paramAnonymousTLObject.max_id = localTL_dialog.read_outbox_max_id;
                  localArrayList.add(paramAnonymousTLObject);
                }
              }
              if (!localArrayList.isEmpty()) {
                MessagesController.this.processUpdateArray(localArrayList, null, null, false);
              }
            }
          }
        });
      }
    }
  }
  
  private void reloadMessages(ArrayList<Integer> paramArrayList, final long paramLong)
  {
    if (paramArrayList.isEmpty()) {}
    for (;;)
    {
      return;
      final ArrayList localArrayList1 = new ArrayList();
      TLRPC.Chat localChat = ChatObject.getChatByDialog(paramLong, this.currentAccount);
      Object localObject;
      ArrayList localArrayList2;
      int i;
      label77:
      Integer localInteger;
      if (ChatObject.isChannel(localChat))
      {
        localObject = new TLRPC.TL_channels_getMessages();
        ((TLRPC.TL_channels_getMessages)localObject).channel = getInputChannel(localChat);
        ((TLRPC.TL_channels_getMessages)localObject).id = localArrayList1;
        localArrayList2 = (ArrayList)this.reloadingMessages.get(paramLong);
        i = 0;
        if (i >= paramArrayList.size()) {
          break label148;
        }
        localInteger = (Integer)paramArrayList.get(i);
        if ((localArrayList2 == null) || (!localArrayList2.contains(localInteger))) {
          break label137;
        }
      }
      for (;;)
      {
        i++;
        break label77;
        localObject = new TLRPC.TL_messages_getMessages();
        ((TLRPC.TL_messages_getMessages)localObject).id = localArrayList1;
        break;
        label137:
        localArrayList1.add(localInteger);
      }
      label148:
      if (!localArrayList1.isEmpty())
      {
        paramArrayList = localArrayList2;
        if (localArrayList2 == null)
        {
          paramArrayList = new ArrayList();
          this.reloadingMessages.put(paramLong, paramArrayList);
        }
        paramArrayList.addAll(localArrayList1);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null)
            {
              TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)paramAnonymousTLObject;
              SparseArray localSparseArray1 = new SparseArray();
              for (int i = 0; i < localmessages_Messages.users.size(); i++)
              {
                paramAnonymousTLObject = (TLRPC.User)localmessages_Messages.users.get(i);
                localSparseArray1.put(paramAnonymousTLObject.id, paramAnonymousTLObject);
              }
              SparseArray localSparseArray2 = new SparseArray();
              for (i = 0; i < localmessages_Messages.chats.size(); i++)
              {
                paramAnonymousTLObject = (TLRPC.Chat)localmessages_Messages.chats.get(i);
                localSparseArray2.put(paramAnonymousTLObject.id, paramAnonymousTLObject);
              }
              paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(paramLong));
              paramAnonymousTLObject = paramAnonymousTL_error;
              if (paramAnonymousTL_error == null)
              {
                paramAnonymousTLObject = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, paramLong));
                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(paramLong), paramAnonymousTLObject);
              }
              Object localObject = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(paramLong));
              paramAnonymousTL_error = (TLRPC.TL_error)localObject;
              if (localObject == null)
              {
                paramAnonymousTL_error = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, paramLong));
                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(paramLong), paramAnonymousTL_error);
              }
              final ArrayList localArrayList = new ArrayList();
              i = 0;
              if (i < localmessages_Messages.messages.size())
              {
                TLRPC.Message localMessage = (TLRPC.Message)localmessages_Messages.messages.get(i);
                if ((localArrayList1 != null) && (localArrayList1.megagroup)) {
                  localMessage.flags |= 0x80000000;
                }
                localMessage.dialog_id = paramLong;
                if (localMessage.out)
                {
                  localObject = paramAnonymousTL_error;
                  label342:
                  if (((Integer)localObject).intValue() >= localMessage.id) {
                    break label404;
                  }
                }
                label404:
                for (boolean bool = true;; bool = false)
                {
                  localMessage.unread = bool;
                  localArrayList.add(new MessageObject(MessagesController.this.currentAccount, localMessage, localSparseArray1, localSparseArray2, true));
                  i++;
                  break;
                  localObject = paramAnonymousTLObject;
                  break label342;
                }
              }
              ImageLoader.saveMessagesThumbs(localmessages_Messages.messages);
              MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(localmessages_Messages, paramLong, -1, 0, false);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  Object localObject = (ArrayList)MessagesController.this.reloadingMessages.get(MessagesController.17.this.val$dialog_id);
                  if (localObject != null)
                  {
                    ((ArrayList)localObject).removeAll(MessagesController.17.this.val$result);
                    if (((ArrayList)localObject).isEmpty()) {
                      MessagesController.this.reloadingMessages.remove(MessagesController.17.this.val$dialog_id);
                    }
                  }
                  MessageObject localMessageObject = (MessageObject)MessagesController.this.dialogMessage.get(MessagesController.17.this.val$dialog_id);
                  if (localMessageObject != null) {}
                  for (int i = 0;; i++) {
                    if (i < localArrayList.size())
                    {
                      localObject = (MessageObject)localArrayList.get(i);
                      if ((localMessageObject != null) && (localMessageObject.getId() == ((MessageObject)localObject).getId()))
                      {
                        MessagesController.this.dialogMessage.put(MessagesController.17.this.val$dialog_id, localObject);
                        if (((MessageObject)localObject).messageOwner.to_id.channel_id == 0)
                        {
                          localMessageObject = (MessageObject)MessagesController.this.dialogMessagesByIds.get(((MessageObject)localObject).getId());
                          MessagesController.this.dialogMessagesByIds.remove(((MessageObject)localObject).getId());
                          if (localMessageObject != null) {
                            MessagesController.this.dialogMessagesByIds.put(localMessageObject.getId(), localMessageObject);
                          }
                        }
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                      }
                    }
                    else
                    {
                      NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(MessagesController.17.this.val$dialog_id), localArrayList });
                      return;
                    }
                  }
                }
              });
            }
          }
        });
      }
    }
  }
  
  private void resetDialogs(boolean paramBoolean, final int paramInt1, final int paramInt2, final int paramInt3, final int paramInt4)
  {
    if (paramBoolean) {
      if (!this.resetingDialogs) {}
    }
    for (;;)
    {
      return;
      this.resetingDialogs = true;
      Object localObject1 = new TLRPC.TL_messages_getPinnedDialogs();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null)
          {
            MessagesController.access$1602(MessagesController.this, (TLRPC.TL_messages_peerDialogs)paramAnonymousTLObject);
            MessagesController.this.resetDialogs(false, paramInt1, paramInt2, paramInt3, paramInt4);
          }
        }
      });
      localObject1 = new TLRPC.TL_messages_getDialogs();
      ((TLRPC.TL_messages_getDialogs)localObject1).limit = 100;
      ((TLRPC.TL_messages_getDialogs)localObject1).exclude_pinned = true;
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = new TLRPC.TL_inputPeerEmpty();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            MessagesController.access$1702(MessagesController.this, (TLRPC.messages_Dialogs)paramAnonymousTLObject);
            MessagesController.this.resetDialogs(false, paramInt1, paramInt2, paramInt3, paramInt4);
          }
        }
      });
      continue;
      if ((this.resetDialogsPinned != null) && (this.resetDialogsAll != null))
      {
        int i = this.resetDialogsAll.messages.size();
        int j = this.resetDialogsAll.dialogs.size();
        this.resetDialogsAll.dialogs.addAll(this.resetDialogsPinned.dialogs);
        this.resetDialogsAll.messages.addAll(this.resetDialogsPinned.messages);
        this.resetDialogsAll.users.addAll(this.resetDialogsPinned.users);
        this.resetDialogsAll.chats.addAll(this.resetDialogsPinned.chats);
        LongSparseArray localLongSparseArray1 = new LongSparseArray();
        LongSparseArray localLongSparseArray2 = new LongSparseArray();
        SparseArray localSparseArray = new SparseArray();
        Object localObject2 = new SparseArray();
        for (int k = 0; k < this.resetDialogsAll.users.size(); k++)
        {
          localObject1 = (TLRPC.User)this.resetDialogsAll.users.get(k);
          localSparseArray.put(((TLRPC.User)localObject1).id, localObject1);
        }
        for (k = 0; k < this.resetDialogsAll.chats.size(); k++)
        {
          localObject1 = (TLRPC.Chat)this.resetDialogsAll.chats.get(k);
          ((SparseArray)localObject2).put(((TLRPC.Chat)localObject1).id, localObject1);
        }
        localObject1 = null;
        k = 0;
        Object localObject3;
        Object localObject4;
        if (k < this.resetDialogsAll.messages.size())
        {
          localObject3 = (TLRPC.Message)this.resetDialogsAll.messages.get(k);
          localObject4 = localObject1;
          if (k < i) {
            if (localObject1 != null)
            {
              localObject4 = localObject1;
              if (((TLRPC.Message)localObject3).date >= ((TLRPC.Message)localObject1).date) {}
            }
            else
            {
              localObject4 = localObject3;
            }
          }
          if (((TLRPC.Message)localObject3).to_id.channel_id != 0)
          {
            localObject1 = (TLRPC.Chat)((SparseArray)localObject2).get(((TLRPC.Message)localObject3).to_id.channel_id);
            if ((localObject1 == null) || (!((TLRPC.Chat)localObject1).left)) {}
          }
          for (;;)
          {
            k++;
            localObject1 = localObject4;
            break;
            if ((localObject1 != null) && (((TLRPC.Chat)localObject1).megagroup)) {
              ((TLRPC.Message)localObject3).flags |= 0x80000000;
            }
            do
            {
              do
              {
                localObject1 = new MessageObject(this.currentAccount, (TLRPC.Message)localObject3, localSparseArray, (SparseArray)localObject2, false);
                localLongSparseArray2.put(((MessageObject)localObject1).getDialogId(), localObject1);
                break;
              } while (((TLRPC.Message)localObject3).to_id.chat_id == 0);
              localObject1 = (TLRPC.Chat)((SparseArray)localObject2).get(((TLRPC.Message)localObject3).to_id.chat_id);
            } while ((localObject1 == null) || (((TLRPC.Chat)localObject1).migrated_to == null));
          }
        }
        k = 0;
        Object localObject5;
        if (k < this.resetDialogsAll.dialogs.size())
        {
          localObject5 = (TLRPC.TL_dialog)this.resetDialogsAll.dialogs.get(k);
          if ((((TLRPC.TL_dialog)localObject5).id == 0L) && (((TLRPC.TL_dialog)localObject5).peer != null))
          {
            if (((TLRPC.TL_dialog)localObject5).peer.user_id != 0) {
              ((TLRPC.TL_dialog)localObject5).id = ((TLRPC.TL_dialog)localObject5).peer.user_id;
            }
          }
          else {
            label681:
            if (((TLRPC.TL_dialog)localObject5).id != 0L) {
              break label755;
            }
          }
          for (;;)
          {
            k++;
            break;
            if (((TLRPC.TL_dialog)localObject5).peer.chat_id != 0)
            {
              ((TLRPC.TL_dialog)localObject5).id = (-((TLRPC.TL_dialog)localObject5).peer.chat_id);
              break label681;
            }
            if (((TLRPC.TL_dialog)localObject5).peer.channel_id == 0) {
              break label681;
            }
            ((TLRPC.TL_dialog)localObject5).id = (-((TLRPC.TL_dialog)localObject5).peer.channel_id);
            break label681;
            label755:
            if (((TLRPC.TL_dialog)localObject5).last_message_date == 0)
            {
              localObject4 = (MessageObject)localLongSparseArray2.get(((TLRPC.TL_dialog)localObject5).id);
              if (localObject4 != null) {
                ((TLRPC.TL_dialog)localObject5).last_message_date = ((MessageObject)localObject4).messageOwner.date;
              }
            }
            if (DialogObject.isChannel((TLRPC.TL_dialog)localObject5))
            {
              localObject4 = (TLRPC.Chat)((SparseArray)localObject2).get(-(int)((TLRPC.TL_dialog)localObject5).id);
              if ((localObject4 == null) || (!((TLRPC.Chat)localObject4).left)) {
                this.channelsPts.put(-(int)((TLRPC.TL_dialog)localObject5).id, ((TLRPC.TL_dialog)localObject5).pts);
              }
            }
            else
            {
              do
              {
                do
                {
                  localLongSparseArray1.put(((TLRPC.TL_dialog)localObject5).id, localObject5);
                  localObject3 = (Integer)this.dialogs_read_inbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject5).id));
                  localObject4 = localObject3;
                  if (localObject3 == null) {
                    localObject4 = Integer.valueOf(0);
                  }
                  this.dialogs_read_inbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject5).id), Integer.valueOf(Math.max(((Integer)localObject4).intValue(), ((TLRPC.TL_dialog)localObject5).read_inbox_max_id)));
                  localObject3 = (Integer)this.dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject5).id));
                  localObject4 = localObject3;
                  if (localObject3 == null) {
                    localObject4 = Integer.valueOf(0);
                  }
                  this.dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject5).id), Integer.valueOf(Math.max(((Integer)localObject4).intValue(), ((TLRPC.TL_dialog)localObject5).read_outbox_max_id)));
                  break;
                } while ((int)((TLRPC.TL_dialog)localObject5).id >= 0);
                localObject4 = (TLRPC.Chat)((SparseArray)localObject2).get(-(int)((TLRPC.TL_dialog)localObject5).id);
              } while ((localObject4 == null) || (((TLRPC.Chat)localObject4).migrated_to == null));
            }
          }
        }
        ImageLoader.saveMessagesThumbs(this.resetDialogsAll.messages);
        k = 0;
        while (k < this.resetDialogsAll.messages.size())
        {
          localObject5 = (TLRPC.Message)this.resetDialogsAll.messages.get(k);
          if ((((TLRPC.Message)localObject5).action instanceof TLRPC.TL_messageActionChatDeleteUser))
          {
            localObject4 = (TLRPC.User)localSparseArray.get(((TLRPC.Message)localObject5).action.user_id);
            if ((localObject4 != null) && (((TLRPC.User)localObject4).bot))
            {
              ((TLRPC.Message)localObject5).reply_markup = new TLRPC.TL_replyKeyboardHide();
              ((TLRPC.Message)localObject5).flags |= 0x40;
            }
          }
          if (((((TLRPC.Message)localObject5).action instanceof TLRPC.TL_messageActionChatMigrateTo)) || ((((TLRPC.Message)localObject5).action instanceof TLRPC.TL_messageActionChannelCreate)))
          {
            ((TLRPC.Message)localObject5).unread = false;
            ((TLRPC.Message)localObject5).media_unread = false;
            k++;
          }
          else
          {
            if (((TLRPC.Message)localObject5).out)
            {
              localObject4 = this.dialogs_read_outbox_max;
              label1210:
              localObject2 = (Integer)((ConcurrentHashMap)localObject4).get(Long.valueOf(((TLRPC.Message)localObject5).dialog_id));
              localObject3 = localObject2;
              if (localObject2 == null)
              {
                localObject3 = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(((TLRPC.Message)localObject5).out, ((TLRPC.Message)localObject5).dialog_id));
                ((ConcurrentHashMap)localObject4).put(Long.valueOf(((TLRPC.Message)localObject5).dialog_id), localObject3);
              }
              if (((Integer)localObject3).intValue() >= ((TLRPC.Message)localObject5).id) {
                break label1311;
              }
            }
            label1311:
            for (paramBoolean = true;; paramBoolean = false)
            {
              ((TLRPC.Message)localObject5).unread = paramBoolean;
              break;
              localObject4 = this.dialogs_read_inbox_max;
              break label1210;
            }
          }
        }
        MessagesStorage.getInstance(this.currentAccount).resetDialogs(this.resetDialogsAll, i, paramInt1, paramInt2, paramInt3, paramInt4, localLongSparseArray1, localLongSparseArray2, (TLRPC.Message)localObject1, j);
        this.resetDialogsPinned = null;
        this.resetDialogsAll = null;
      }
    }
  }
  
  private void setUpdatesStartTime(int paramInt, long paramLong)
  {
    if (paramInt == 0) {
      this.updatesStartWaitTimeSeq = paramLong;
    }
    for (;;)
    {
      return;
      if (paramInt == 1) {
        this.updatesStartWaitTimePts = paramLong;
      } else if (paramInt == 2) {
        this.updatesStartWaitTimeQts = paramLong;
      }
    }
  }
  
  private static void showCantOpenAlert(BaseFragment paramBaseFragment, String paramString)
  {
    if ((paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null)) {}
    for (;;)
    {
      return;
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", NUM));
      localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
      localBuilder.setMessage(paramString);
      paramBaseFragment.showDialog(localBuilder.create());
    }
  }
  
  private void updatePrintingStrings()
  {
    final LongSparseArray localLongSparseArray1 = new LongSparseArray();
    final LongSparseArray localLongSparseArray2 = new LongSparseArray();
    new ArrayList(this.printingUsers.keySet());
    Iterator localIterator1 = this.printingUsers.entrySet().iterator();
    while (localIterator1.hasNext())
    {
      Object localObject = (Map.Entry)localIterator1.next();
      long l = ((Long)((Map.Entry)localObject).getKey()).longValue();
      ArrayList localArrayList = (ArrayList)((Map.Entry)localObject).getValue();
      int i = (int)l;
      TLRPC.User localUser;
      if ((i > 0) || (i == 0) || (localArrayList.size() == 1))
      {
        localObject = (PrintingUser)localArrayList.get(0);
        localUser = getUser(Integer.valueOf(((PrintingUser)localObject).userId));
        if (localUser != null)
        {
          if ((((PrintingUser)localObject).action instanceof TLRPC.TL_sendMessageRecordAudioAction))
          {
            if (i < 0) {
              localLongSparseArray1.put(l, LocaleController.formatString("IsRecordingAudio", NUM, new Object[] { getUserNameForTyping(localUser) }));
            }
            for (;;)
            {
              localLongSparseArray2.put(l, Integer.valueOf(1));
              break;
              localLongSparseArray1.put(l, LocaleController.getString("RecordingAudio", NUM));
            }
          }
          if (((((PrintingUser)localObject).action instanceof TLRPC.TL_sendMessageRecordRoundAction)) || ((((PrintingUser)localObject).action instanceof TLRPC.TL_sendMessageUploadRoundAction)))
          {
            if (i < 0) {
              localLongSparseArray1.put(l, LocaleController.formatString("IsRecordingRound", NUM, new Object[] { getUserNameForTyping(localUser) }));
            }
            for (;;)
            {
              localLongSparseArray2.put(l, Integer.valueOf(4));
              break;
              localLongSparseArray1.put(l, LocaleController.getString("RecordingRound", NUM));
            }
          }
          if ((((PrintingUser)localObject).action instanceof TLRPC.TL_sendMessageUploadAudioAction))
          {
            if (i < 0) {
              localLongSparseArray1.put(l, LocaleController.formatString("IsSendingAudio", NUM, new Object[] { getUserNameForTyping(localUser) }));
            }
            for (;;)
            {
              localLongSparseArray2.put(l, Integer.valueOf(2));
              break;
              localLongSparseArray1.put(l, LocaleController.getString("SendingAudio", NUM));
            }
          }
          if (((((PrintingUser)localObject).action instanceof TLRPC.TL_sendMessageUploadVideoAction)) || ((((PrintingUser)localObject).action instanceof TLRPC.TL_sendMessageRecordVideoAction)))
          {
            if (i < 0) {
              localLongSparseArray1.put(l, LocaleController.formatString("IsSendingVideo", NUM, new Object[] { getUserNameForTyping(localUser) }));
            }
            for (;;)
            {
              localLongSparseArray2.put(l, Integer.valueOf(2));
              break;
              localLongSparseArray1.put(l, LocaleController.getString("SendingVideoStatus", NUM));
            }
          }
          if ((((PrintingUser)localObject).action instanceof TLRPC.TL_sendMessageUploadDocumentAction))
          {
            if (i < 0) {
              localLongSparseArray1.put(l, LocaleController.formatString("IsSendingFile", NUM, new Object[] { getUserNameForTyping(localUser) }));
            }
            for (;;)
            {
              localLongSparseArray2.put(l, Integer.valueOf(2));
              break;
              localLongSparseArray1.put(l, LocaleController.getString("SendingFile", NUM));
            }
          }
          if ((((PrintingUser)localObject).action instanceof TLRPC.TL_sendMessageUploadPhotoAction))
          {
            if (i < 0) {
              localLongSparseArray1.put(l, LocaleController.formatString("IsSendingPhoto", NUM, new Object[] { getUserNameForTyping(localUser) }));
            }
            for (;;)
            {
              localLongSparseArray2.put(l, Integer.valueOf(2));
              break;
              localLongSparseArray1.put(l, LocaleController.getString("SendingPhoto", NUM));
            }
          }
          if ((((PrintingUser)localObject).action instanceof TLRPC.TL_sendMessageGamePlayAction))
          {
            if (i < 0) {
              localLongSparseArray1.put(l, LocaleController.formatString("IsSendingGame", NUM, new Object[] { getUserNameForTyping(localUser) }));
            }
            for (;;)
            {
              localLongSparseArray2.put(l, Integer.valueOf(3));
              break;
              localLongSparseArray1.put(l, LocaleController.getString("SendingGame", NUM));
            }
          }
          if (i < 0) {
            localLongSparseArray1.put(l, LocaleController.formatString("IsTypingGroup", NUM, new Object[] { getUserNameForTyping(localUser) }));
          }
          for (;;)
          {
            localLongSparseArray2.put(l, Integer.valueOf(0));
            break;
            localLongSparseArray1.put(l, LocaleController.getString("Typing", NUM));
          }
        }
      }
      else
      {
        i = 0;
        localObject = new StringBuilder();
        Iterator localIterator2 = localArrayList.iterator();
        int j;
        do
        {
          j = i;
          if (!localIterator2.hasNext()) {
            break;
          }
          localUser = getUser(Integer.valueOf(((PrintingUser)localIterator2.next()).userId));
          j = i;
          if (localUser != null)
          {
            if (((StringBuilder)localObject).length() != 0) {
              ((StringBuilder)localObject).append(", ");
            }
            ((StringBuilder)localObject).append(getUserNameForTyping(localUser));
            j = i + 1;
          }
          i = j;
        } while (j != 2);
        if (((StringBuilder)localObject).length() != 0)
        {
          if (j == 1) {
            localLongSparseArray1.put(l, LocaleController.formatString("IsTypingGroup", NUM, new Object[] { ((StringBuilder)localObject).toString() }));
          }
          for (;;)
          {
            localLongSparseArray2.put(l, Integer.valueOf(0));
            break;
            if (localArrayList.size() > 2) {
              localLongSparseArray1.put(l, String.format(LocaleController.getPluralString("AndMoreTypingGroup", localArrayList.size() - 2), new Object[] { ((StringBuilder)localObject).toString(), Integer.valueOf(localArrayList.size() - 2) }));
            } else {
              localLongSparseArray1.put(l, LocaleController.formatString("AreTypingGroup", NUM, new Object[] { ((StringBuilder)localObject).toString() }));
            }
          }
        }
      }
    }
    this.lastPrintingStringCount = localLongSparseArray1.size();
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.this.printingStrings = localLongSparseArray1;
        MessagesController.this.printingStringsTypes = localLongSparseArray2;
      }
    });
  }
  
  private boolean updatePrintingUsersWithNewMessages(long paramLong, ArrayList<MessageObject> paramArrayList)
  {
    boolean bool = true;
    if (paramLong > 0L)
    {
      if ((ArrayList)this.printingUsers.get(Long.valueOf(paramLong)) == null) {
        break label237;
      }
      this.printingUsers.remove(Long.valueOf(paramLong));
    }
    for (;;)
    {
      return bool;
      if (paramLong < 0L)
      {
        ArrayList localArrayList = new ArrayList();
        paramArrayList = paramArrayList.iterator();
        while (paramArrayList.hasNext())
        {
          MessageObject localMessageObject = (MessageObject)paramArrayList.next();
          if (!localArrayList.contains(Integer.valueOf(localMessageObject.messageOwner.from_id))) {
            localArrayList.add(Integer.valueOf(localMessageObject.messageOwner.from_id));
          }
        }
        paramArrayList = (ArrayList)this.printingUsers.get(Long.valueOf(paramLong));
        int i = 0;
        int j = 0;
        if (paramArrayList != null) {
          for (int k = 0;; k = i + 1)
          {
            i = j;
            if (k >= paramArrayList.size()) {
              break;
            }
            i = k;
            if (localArrayList.contains(Integer.valueOf(((PrintingUser)paramArrayList.get(k)).userId)))
            {
              paramArrayList.remove(k);
              i = k - 1;
              if (paramArrayList.isEmpty()) {
                this.printingUsers.remove(Long.valueOf(paramLong));
              }
              j = 1;
            }
          }
        }
        if (i != 0) {}
      }
      else
      {
        label237:
        bool = false;
      }
    }
  }
  
  public void addSupportUser()
  {
    TLRPC.TL_userForeign_old2 localTL_userForeign_old2 = new TLRPC.TL_userForeign_old2();
    localTL_userForeign_old2.phone = "333";
    localTL_userForeign_old2.id = 333000;
    localTL_userForeign_old2.first_name = "Telegram";
    localTL_userForeign_old2.last_name = "";
    localTL_userForeign_old2.status = null;
    localTL_userForeign_old2.photo = new TLRPC.TL_userProfilePhotoEmpty();
    putUser(localTL_userForeign_old2, true);
    localTL_userForeign_old2 = new TLRPC.TL_userForeign_old2();
    localTL_userForeign_old2.phone = "42777";
    localTL_userForeign_old2.id = 777000;
    localTL_userForeign_old2.first_name = "Telegram";
    localTL_userForeign_old2.last_name = "Notifications";
    localTL_userForeign_old2.status = null;
    localTL_userForeign_old2.photo = new TLRPC.TL_userProfilePhotoEmpty();
    putUser(localTL_userForeign_old2, true);
  }
  
  public void addToViewsQueue(final TLRPC.Message paramMessage)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i;
        if (paramMessage.to_id.channel_id != 0) {
          i = -paramMessage.to_id.channel_id;
        }
        for (;;)
        {
          ArrayList localArrayList1 = (ArrayList)MessagesController.this.channelViewsToSend.get(i);
          ArrayList localArrayList2 = localArrayList1;
          if (localArrayList1 == null)
          {
            localArrayList2 = new ArrayList();
            MessagesController.this.channelViewsToSend.put(i, localArrayList2);
          }
          if (!localArrayList2.contains(Integer.valueOf(paramMessage.id))) {
            localArrayList2.add(Integer.valueOf(paramMessage.id));
          }
          return;
          if (paramMessage.to_id.chat_id != 0) {
            i = -paramMessage.to_id.chat_id;
          } else {
            i = paramMessage.to_id.user_id;
          }
        }
      }
    });
  }
  
  public void addUserToChat(final int paramInt1, final TLRPC.User paramUser, final TLRPC.ChatFull paramChatFull, int paramInt2, String paramString, final BaseFragment paramBaseFragment)
  {
    if (paramUser == null) {}
    for (;;)
    {
      return;
      if (paramInt1 > 0)
      {
        final boolean bool1 = ChatObject.isChannel(paramInt1, this.currentAccount);
        final boolean bool2;
        if ((bool1) && (getChat(Integer.valueOf(paramInt1)).megagroup))
        {
          bool2 = true;
          label41:
          paramChatFull = getInputUser(paramUser);
          if ((paramString != null) && ((!bool1) || (bool2))) {
            break label211;
          }
          if (!bool1) {
            break label184;
          }
          if (!(paramChatFull instanceof TLRPC.TL_inputUserSelf)) {
            break label155;
          }
          if (this.joiningToChannels.contains(Integer.valueOf(paramInt1))) {
            continue;
          }
          paramUser = new TLRPC.TL_channels_joinChannel();
          paramUser.channel = getInputChannel(paramInt1);
          this.joiningToChannels.add(Integer.valueOf(paramInt1));
        }
        for (;;)
        {
          ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramUser, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
            {
              if ((bool1) && ((paramChatFull instanceof TLRPC.TL_inputUserSelf))) {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.this.joiningToChannels.remove(Integer.valueOf(MessagesController.100.this.val$chat_id));
                  }
                });
              }
              if (paramAnonymousTL_error != null)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    boolean bool = true;
                    int i = MessagesController.this.currentAccount;
                    TLRPC.TL_error localTL_error = paramAnonymousTL_error;
                    BaseFragment localBaseFragment = MessagesController.100.this.val$fragment;
                    TLObject localTLObject = MessagesController.100.this.val$request;
                    if ((MessagesController.100.this.val$isChannel) && (!MessagesController.100.this.val$isMegagroup)) {}
                    for (;;)
                    {
                      AlertsCreator.processError(i, localTL_error, localBaseFragment, localTLObject, new Object[] { Boolean.valueOf(bool) });
                      return;
                      bool = false;
                    }
                  }
                });
                return;
              }
              int i = 0;
              paramAnonymousTL_error = (TLRPC.Updates)paramAnonymousTLObject;
              for (int j = 0;; j++)
              {
                int k = i;
                if (j < paramAnonymousTL_error.updates.size())
                {
                  paramAnonymousTLObject = (TLRPC.Update)paramAnonymousTL_error.updates.get(j);
                  if (((paramAnonymousTLObject instanceof TLRPC.TL_updateNewChannelMessage)) && ((((TLRPC.TL_updateNewChannelMessage)paramAnonymousTLObject).message.action instanceof TLRPC.TL_messageActionChatAddUser))) {
                    k = 1;
                  }
                }
                else
                {
                  MessagesController.this.processUpdates(paramAnonymousTL_error, false);
                  if (bool1)
                  {
                    if ((k == 0) && ((paramChatFull instanceof TLRPC.TL_inputUserSelf))) {
                      MessagesController.this.generateJoinMessage(paramInt1, true);
                    }
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        MessagesController.this.loadFullChat(MessagesController.100.this.val$chat_id, 0, true);
                      }
                    }, 1000L);
                  }
                  if ((!bool1) || (!(paramChatFull instanceof TLRPC.TL_inputUserSelf))) {
                    break;
                  }
                  MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), null, true, paramInt1);
                  break;
                }
              }
            }
          });
          break;
          bool2 = false;
          break label41;
          label155:
          paramUser = new TLRPC.TL_channels_inviteToChannel();
          paramUser.channel = getInputChannel(paramInt1);
          paramUser.users.add(paramChatFull);
          continue;
          label184:
          paramUser = new TLRPC.TL_messages_addChatUser();
          paramUser.chat_id = paramInt1;
          paramUser.fwd_limit = paramInt2;
          paramUser.user_id = paramChatFull;
        }
        label211:
        paramUser = new TLRPC.TL_messages_startBot();
        paramUser.bot = paramChatFull;
        if (bool1) {
          paramUser.peer = getInputPeer(-paramInt1);
        }
        for (;;)
        {
          paramUser.start_param = paramString;
          paramUser.random_id = Utilities.random.nextLong();
          break;
          paramUser.peer = new TLRPC.TL_inputPeerChat();
          paramUser.peer.chat_id = paramInt1;
        }
      }
      else if ((paramChatFull instanceof TLRPC.TL_chatFull))
      {
        for (paramInt2 = 0;; paramInt2++)
        {
          if (paramInt2 >= paramChatFull.participants.participants.size()) {
            break label336;
          }
          if (((TLRPC.ChatParticipant)paramChatFull.participants.participants.get(paramInt2)).user_id == paramUser.id) {
            break;
          }
        }
        label336:
        paramString = getChat(Integer.valueOf(paramInt1));
        paramString.participants_count += 1;
        paramBaseFragment = new ArrayList();
        paramBaseFragment.add(paramString);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, paramBaseFragment, true, true);
        paramString = new TLRPC.TL_chatParticipant();
        paramString.user_id = paramUser.id;
        paramString.inviter_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
        paramString.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        paramChatFull.participants.participants.add(0, paramString);
        MessagesStorage.getInstance(this.currentAccount).updateChatInfo(paramChatFull, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { paramChatFull, Integer.valueOf(0), Boolean.valueOf(false), null });
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(32) });
      }
    }
  }
  
  public void addUsersToChannel(int paramInt, ArrayList<TLRPC.InputUser> paramArrayList, final BaseFragment paramBaseFragment)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      final TLRPC.TL_channels_inviteToChannel localTL_channels_inviteToChannel = new TLRPC.TL_channels_inviteToChannel();
      localTL_channels_inviteToChannel.channel = getInputChannel(paramInt);
      localTL_channels_inviteToChannel.users = paramArrayList;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_inviteToChannel, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                AlertsCreator.processError(MessagesController.this.currentAccount, paramAnonymousTL_error, MessagesController.91.this.val$fragment, MessagesController.91.this.val$req, new Object[] { Boolean.valueOf(true) });
              }
            });
          }
          for (;;)
          {
            return;
            MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          }
        }
      });
    }
  }
  
  public void blockUser(int paramInt)
  {
    final TLRPC.User localUser = getUser(Integer.valueOf(paramInt));
    if ((localUser == null) || (this.blockedUsers.contains(Integer.valueOf(paramInt)))) {
      return;
    }
    this.blockedUsers.add(Integer.valueOf(paramInt));
    if (localUser.bot) {
      DataQuery.getInstance(this.currentAccount).removeInline(paramInt);
    }
    for (;;)
    {
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
      TLRPC.TL_contacts_block localTL_contacts_block = new TLRPC.TL_contacts_block();
      localTL_contacts_block.id = getInputUser(localUser);
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_block, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = new ArrayList();
            paramAnonymousTLObject.add(Integer.valueOf(localUser.id));
            MessagesStorage.getInstance(MessagesController.this.currentAccount).putBlockedUsers(paramAnonymousTLObject, false);
          }
        }
      });
      break;
      DataQuery.getInstance(this.currentAccount).removePeer(paramInt);
    }
  }
  
  public boolean canPinDialog(boolean paramBoolean)
  {
    int i = 0;
    int j = 0;
    if (j < this.dialogs.size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)this.dialogs.get(j);
      int k = (int)localTL_dialog.id;
      int m;
      if (paramBoolean)
      {
        m = i;
        if (k != 0) {}
      }
      else
      {
        if ((paramBoolean) || (k != 0)) {
          break label69;
        }
        m = i;
      }
      for (;;)
      {
        j++;
        i = m;
        break;
        label69:
        m = i;
        if (localTL_dialog.pinned) {
          m = i + 1;
        }
      }
    }
    if (i < this.maxPinnedDialogsCount) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  public void cancelLoadFullChat(int paramInt)
  {
    this.loadingFullChats.remove(Integer.valueOf(paramInt));
  }
  
  public void cancelLoadFullUser(int paramInt)
  {
    this.loadingFullUsers.remove(Integer.valueOf(paramInt));
  }
  
  public void cancelTyping(int paramInt, long paramLong)
  {
    LongSparseArray localLongSparseArray = (LongSparseArray)this.sendingTypings.get(paramInt);
    if (localLongSparseArray != null) {
      localLongSparseArray.remove(paramLong);
    }
  }
  
  public void changeChatAvatar(int paramInt, TLRPC.InputFile paramInputFile)
  {
    if (ChatObject.isChannel(paramInt, this.currentAccount))
    {
      localObject = new TLRPC.TL_channels_editPhoto();
      ((TLRPC.TL_channels_editPhoto)localObject).channel = getInputChannel(paramInt);
      if (paramInputFile != null)
      {
        ((TLRPC.TL_channels_editPhoto)localObject).photo = new TLRPC.TL_inputChatUploadedPhoto();
        ((TLRPC.TL_channels_editPhoto)localObject).photo.file = paramInputFile;
      }
      for (;;)
      {
        paramInputFile = (TLRPC.InputFile)localObject;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramInputFile, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error != null) {}
            for (;;)
            {
              return;
              MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
            }
          }
        }, 64);
        return;
        ((TLRPC.TL_channels_editPhoto)localObject).photo = new TLRPC.TL_inputChatPhotoEmpty();
      }
    }
    Object localObject = new TLRPC.TL_messages_editChatPhoto();
    ((TLRPC.TL_messages_editChatPhoto)localObject).chat_id = paramInt;
    if (paramInputFile != null)
    {
      ((TLRPC.TL_messages_editChatPhoto)localObject).photo = new TLRPC.TL_inputChatUploadedPhoto();
      ((TLRPC.TL_messages_editChatPhoto)localObject).photo.file = paramInputFile;
    }
    for (;;)
    {
      paramInputFile = (TLRPC.InputFile)localObject;
      break;
      ((TLRPC.TL_messages_editChatPhoto)localObject).photo = new TLRPC.TL_inputChatPhotoEmpty();
    }
  }
  
  public void changeChatTitle(int paramInt, String paramString)
  {
    Object localObject;
    if (paramInt > 0) {
      if (ChatObject.isChannel(paramInt, this.currentAccount))
      {
        localObject = new TLRPC.TL_channels_editTitle();
        ((TLRPC.TL_channels_editTitle)localObject).channel = getInputChannel(paramInt);
        ((TLRPC.TL_channels_editTitle)localObject).title = paramString;
        paramString = (String)localObject;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramString, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error != null) {}
            for (;;)
            {
              return;
              MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
            }
          }
        }, 64);
      }
    }
    for (;;)
    {
      return;
      localObject = new TLRPC.TL_messages_editChatTitle();
      ((TLRPC.TL_messages_editChatTitle)localObject).chat_id = paramInt;
      ((TLRPC.TL_messages_editChatTitle)localObject).title = paramString;
      paramString = (String)localObject;
      break;
      localObject = getChat(Integer.valueOf(paramInt));
      ((TLRPC.Chat)localObject).title = paramString;
      paramString = new ArrayList();
      paramString.add(localObject);
      MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, paramString, true, true);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(16) });
    }
  }
  
  public boolean checkCanOpenChat(Bundle paramBundle, BaseFragment paramBaseFragment)
  {
    return checkCanOpenChat(paramBundle, paramBaseFragment, null);
  }
  
  public boolean checkCanOpenChat(final Bundle paramBundle, final BaseFragment paramBaseFragment, MessageObject paramMessageObject)
  {
    boolean bool;
    if ((paramBundle == null) || (paramBaseFragment == null)) {
      bool = true;
    }
    for (;;)
    {
      return bool;
      final Object localObject1 = null;
      Object localObject2 = null;
      final int i = paramBundle.getInt("user_id", 0);
      int j = paramBundle.getInt("chat_id", 0);
      int k = paramBundle.getInt("message_id", 0);
      Object localObject3;
      if (i != 0) {
        localObject3 = getUser(Integer.valueOf(i));
      }
      for (;;)
      {
        if ((localObject3 != null) || (localObject2 != null)) {
          break label109;
        }
        bool = true;
        break;
        localObject3 = localObject1;
        if (j != 0)
        {
          localObject2 = getChat(Integer.valueOf(j));
          localObject3 = localObject1;
        }
      }
      label109:
      localObject1 = null;
      if (localObject2 != null) {
        localObject1 = getRestrictionReason(((TLRPC.Chat)localObject2).restriction_reason);
      }
      for (;;)
      {
        if (localObject1 == null) {
          break label162;
        }
        showCantOpenAlert(paramBaseFragment, (String)localObject1);
        bool = false;
        break;
        if (localObject3 != null) {
          localObject1 = getRestrictionReason(((TLRPC.User)localObject3).restriction_reason);
        }
      }
      label162:
      if ((k != 0) && (paramMessageObject != null) && (localObject2 != null) && (((TLRPC.Chat)localObject2).access_hash == 0L))
      {
        i = (int)paramMessageObject.getDialogId();
        if (i != 0)
        {
          localObject1 = new AlertDialog(paramBaseFragment.getParentActivity(), 1);
          ((AlertDialog)localObject1).setMessage(LocaleController.getString("Loading", NUM));
          ((AlertDialog)localObject1).setCanceledOnTouchOutside(false);
          ((AlertDialog)localObject1).setCancelable(false);
          if (i < 0) {
            localObject2 = getChat(Integer.valueOf(-i));
          }
          if ((i > 0) || (!ChatObject.isChannel((TLRPC.Chat)localObject2)))
          {
            localObject2 = new TLRPC.TL_messages_getMessages();
            ((TLRPC.TL_messages_getMessages)localObject2).id.add(Integer.valueOf(paramMessageObject.getId()));
          }
          for (paramMessageObject = (MessageObject)localObject2;; paramMessageObject = (MessageObject)localObject2)
          {
            i = ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramMessageObject, new RequestDelegate()
            {
              public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
              {
                if (paramAnonymousTLObject != null) {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      try
                      {
                        MessagesController.136.this.val$progressDialog.dismiss();
                        TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)paramAnonymousTLObject;
                        MessagesController.this.putUsers(localmessages_Messages.users, false);
                        MessagesController.this.putChats(localmessages_Messages.chats, false);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(localmessages_Messages.users, localmessages_Messages.chats, true, true);
                        MessagesController.136.this.val$fragment.presentFragment(new ChatActivity(MessagesController.136.this.val$bundle), true);
                        return;
                      }
                      catch (Exception localException)
                      {
                        for (;;)
                        {
                          FileLog.e(localException);
                        }
                      }
                    }
                  });
                }
              }
            });
            ((AlertDialog)localObject1).setButton(-2, LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
              {
                ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(i, true);
                try
                {
                  paramAnonymousDialogInterface.dismiss();
                  if (paramBaseFragment != null) {
                    paramBaseFragment.setVisibleDialog(null);
                  }
                  return;
                }
                catch (Exception paramAnonymousDialogInterface)
                {
                  for (;;)
                  {
                    FileLog.e(paramAnonymousDialogInterface);
                  }
                }
              }
            });
            paramBaseFragment.setVisibleDialog((Dialog)localObject1);
            ((AlertDialog)localObject1).show();
            bool = false;
            break;
            localObject3 = getChat(Integer.valueOf(-i));
            localObject2 = new TLRPC.TL_channels_getMessages();
            ((TLRPC.TL_channels_getMessages)localObject2).channel = getInputChannel((TLRPC.Chat)localObject3);
            ((TLRPC.TL_channels_getMessages)localObject2).id.add(Integer.valueOf(paramMessageObject.getId()));
          }
        }
      }
      bool = true;
    }
  }
  
  public void checkChannelInviter(final int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        final TLRPC.Chat localChat = MessagesController.this.getChat(Integer.valueOf(paramInt));
        if ((localChat == null) || (!ChatObject.isChannel(paramInt, MessagesController.this.currentAccount)) || (localChat.creator)) {}
        for (;;)
        {
          return;
          TLRPC.TL_channels_getParticipant localTL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
          localTL_channels_getParticipant.channel = MessagesController.this.getInputChannel(paramInt);
          localTL_channels_getParticipant.user_id = new TLRPC.TL_inputUserSelf();
          ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(localTL_channels_getParticipant, new RequestDelegate()
          {
            public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
            {
              final TLRPC.TL_channels_channelParticipant localTL_channels_channelParticipant = (TLRPC.TL_channels_channelParticipant)paramAnonymous2TLObject;
              if ((localTL_channels_channelParticipant == null) || (!(localTL_channels_channelParticipant.participant instanceof TLRPC.TL_channelParticipantSelf)) || (localTL_channels_channelParticipant.participant.inviter_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) || ((localChat.megagroup) && (MessagesStorage.getInstance(MessagesController.this.currentAccount).isMigratedChat(localChat.id)))) {}
              for (;;)
              {
                return;
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.this.putUsers(localTL_channels_channelParticipant.users, false);
                  }
                });
                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(localTL_channels_channelParticipant.users, null, true, true);
                paramAnonymous2TLObject = new TLRPC.TL_messageService();
                paramAnonymous2TLObject.media_unread = true;
                paramAnonymous2TLObject.unread = true;
                paramAnonymous2TLObject.flags = 256;
                paramAnonymous2TLObject.post = true;
                if (localChat.megagroup) {
                  paramAnonymous2TLObject.flags |= 0x80000000;
                }
                int i = UserConfig.getInstance(MessagesController.this.currentAccount).getNewMessageId();
                paramAnonymous2TLObject.id = i;
                paramAnonymous2TLObject.local_id = i;
                paramAnonymous2TLObject.date = localTL_channels_channelParticipant.participant.date;
                paramAnonymous2TLObject.action = new TLRPC.TL_messageActionChatAddUser();
                paramAnonymous2TLObject.from_id = localTL_channels_channelParticipant.participant.inviter_id;
                paramAnonymous2TLObject.action.users.add(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
                paramAnonymous2TLObject.to_id = new TLRPC.TL_peerChannel();
                paramAnonymous2TLObject.to_id.channel_id = MessagesController.121.this.val$chat_id;
                paramAnonymous2TLObject.dialog_id = (-MessagesController.121.this.val$chat_id);
                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                final ArrayList localArrayList1 = new ArrayList();
                ArrayList localArrayList2 = new ArrayList();
                ConcurrentHashMap localConcurrentHashMap = new ConcurrentHashMap();
                for (i = 0; i < localTL_channels_channelParticipant.users.size(); i++)
                {
                  paramAnonymous2TL_error = (TLRPC.User)localTL_channels_channelParticipant.users.get(i);
                  localConcurrentHashMap.put(Integer.valueOf(paramAnonymous2TL_error.id), paramAnonymous2TL_error);
                }
                localArrayList2.add(paramAnonymous2TLObject);
                localArrayList1.add(new MessageObject(MessagesController.this.currentAccount, paramAnonymous2TLObject, localConcurrentHashMap, true));
                MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
                {
                  public void run()
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(MessagesController.121.1.2.this.val$pushMessages, true, false);
                      }
                    });
                  }
                });
                MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(localArrayList2, true, true, false, 0);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.this.updateInterfaceWithMessages(-MessagesController.121.this.val$chat_id, localArrayList1);
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                  }
                });
              }
            }
          });
        }
      }
    });
  }
  
  protected void checkLastDialogMessage(final TLRPC.TL_dialog paramTL_dialog, TLRPC.InputPeer paramInputPeer, final long paramLong)
  {
    int i = (int)paramTL_dialog.id;
    if ((i == 0) || (this.checkingLastMessagesDialogs.indexOfKey(i) >= 0)) {}
    TLRPC.TL_messages_getHistory localTL_messages_getHistory;
    label46:
    do
    {
      return;
      localTL_messages_getHistory = new TLRPC.TL_messages_getHistory();
      if (paramInputPeer != null) {
        break;
      }
      localObject1 = getInputPeer(i);
      localTL_messages_getHistory.peer = ((TLRPC.InputPeer)localObject1);
    } while ((localTL_messages_getHistory.peer == null) || ((localTL_messages_getHistory.peer instanceof TLRPC.TL_inputPeerChannel)));
    localTL_messages_getHistory.limit = 1;
    this.checkingLastMessagesDialogs.put(i, true);
    Object localObject2;
    if (paramLong == 0L) {
      localObject2 = null;
    }
    for (;;)
    {
      try
      {
        localObject1 = new org/telegram/tgnet/NativeByteBuffer;
        ((NativeByteBuffer)localObject1).<init>(localTL_messages_getHistory.peer.getObjectSize() + 48);
      }
      catch (Exception localException1)
      {
        paramInputPeer = (TLRPC.InputPeer)localObject2;
      }
      try
      {
        ((NativeByteBuffer)localObject1).writeInt32(8);
        ((NativeByteBuffer)localObject1).writeInt64(paramTL_dialog.id);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.top_message);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.read_inbox_max_id);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.read_outbox_max_id);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.unread_count);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.last_message_date);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.pts);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.flags);
        ((NativeByteBuffer)localObject1).writeBool(paramTL_dialog.pinned);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.pinnedNum);
        ((NativeByteBuffer)localObject1).writeInt32(paramTL_dialog.unread_mentions_count);
        paramInputPeer.serializeToStream((AbstractSerializedData)localObject1);
        paramInputPeer = (TLRPC.InputPeer)localObject1;
        paramLong = MessagesStorage.getInstance(this.currentAccount).createPendingTask(paramInputPeer);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getHistory, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTLObject != null)
            {
              paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
              if (paramAnonymousTLObject.messages.isEmpty()) {
                break label328;
              }
              paramAnonymousTL_error = new TLRPC.TL_messages_dialogs();
              TLRPC.Message localMessage = (TLRPC.Message)paramAnonymousTLObject.messages.get(0);
              TLRPC.TL_dialog localTL_dialog = new TLRPC.TL_dialog();
              localTL_dialog.flags = paramTL_dialog.flags;
              localTL_dialog.top_message = localMessage.id;
              localTL_dialog.last_message_date = localMessage.date;
              localTL_dialog.notify_settings = paramTL_dialog.notify_settings;
              localTL_dialog.pts = paramTL_dialog.pts;
              localTL_dialog.unread_count = paramTL_dialog.unread_count;
              localTL_dialog.unread_mentions_count = paramTL_dialog.unread_mentions_count;
              localTL_dialog.read_inbox_max_id = paramTL_dialog.read_inbox_max_id;
              localTL_dialog.read_outbox_max_id = paramTL_dialog.read_outbox_max_id;
              localTL_dialog.pinned = paramTL_dialog.pinned;
              localTL_dialog.pinnedNum = paramTL_dialog.pinnedNum;
              long l = paramTL_dialog.id;
              localTL_dialog.id = l;
              localMessage.dialog_id = l;
              paramAnonymousTL_error.users.addAll(paramAnonymousTLObject.users);
              paramAnonymousTL_error.chats.addAll(paramAnonymousTLObject.chats);
              paramAnonymousTL_error.dialogs.add(localTL_dialog);
              paramAnonymousTL_error.messages.addAll(paramAnonymousTLObject.messages);
              paramAnonymousTL_error.count = 1;
              MessagesController.this.processDialogsUpdate(paramAnonymousTL_error, null);
              MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(paramAnonymousTLObject.messages, true, true, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask(), true);
            }
            for (;;)
            {
              if (paramLong != 0L) {
                MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(paramLong);
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.checkingLastMessagesDialogs.delete(MessagesController.72.this.val$lower_id);
                }
              });
              return;
              label328:
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(MessagesController.72.this.val$dialog.id);
                  if ((localTL_dialog != null) && (localTL_dialog.top_message == 0)) {
                    MessagesController.this.deleteDialog(MessagesController.72.this.val$dialog.id, 3);
                  }
                }
              });
            }
          }
        });
      }
      catch (Exception localException2)
      {
        for (;;)
        {
          paramInputPeer = (TLRPC.InputPeer)localObject1;
        }
      }
      localObject1 = paramInputPeer;
      break label46;
      FileLog.e(localException1);
    }
  }
  
  public void cleanup()
  {
    ContactsController.getInstance(this.currentAccount).cleanup();
    MediaController.getInstance().cleanup();
    NotificationsController.getInstance(this.currentAccount).cleanup();
    SendMessagesHelper.getInstance(this.currentAccount).cleanup();
    SecretChatHelper.getInstance(this.currentAccount).cleanup();
    LocationController.getInstance(this.currentAccount).cleanup();
    DataQuery.getInstance(this.currentAccount).cleanup();
    org.telegram.ui.DialogsActivity.dialogsLoaded[this.currentAccount] = false;
    this.reloadingWebpages.clear();
    this.reloadingWebpagesPending.clear();
    this.dialogs_dict.clear();
    this.dialogs_read_inbox_max.clear();
    this.dialogs_read_outbox_max.clear();
    this.exportedChats.clear();
    this.fullUsers.clear();
    this.dialogs.clear();
    this.joiningToChannels.clear();
    this.channelViewsToSend.clear();
    this.dialogsServerOnly.clear();
    this.dialogsForward.clear();
    this.dialogsGroupsOnly.clear();
    this.dialogMessagesByIds.clear();
    this.dialogMessagesByRandomIds.clear();
    this.channelAdmins.clear();
    this.loadingChannelAdmins.clear();
    this.users.clear();
    this.objectsByUsernames.clear();
    this.chats.clear();
    this.dialogMessage.clear();
    this.printingUsers.clear();
    this.printingStrings.clear();
    this.printingStringsTypes.clear();
    this.onlinePrivacy.clear();
    this.loadingPeerSettings.clear();
    this.lastPrintingStringCount = 0;
    this.nextDialogsCacheOffset = 0;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.this.readTasks.clear();
        MessagesController.this.readTasksMap.clear();
        MessagesController.this.updatesQueueSeq.clear();
        MessagesController.this.updatesQueuePts.clear();
        MessagesController.this.updatesQueueQts.clear();
        MessagesController.this.gettingUnknownChannels.clear();
        MessagesController.access$1202(MessagesController.this, 0L);
        MessagesController.access$1302(MessagesController.this, 0L);
        MessagesController.access$1402(MessagesController.this, 0L);
        MessagesController.this.createdDialogIds.clear();
        MessagesController.this.gettingDifference = false;
        MessagesController.access$1602(MessagesController.this, null);
        MessagesController.access$1702(MessagesController.this, null);
      }
    });
    this.createdDialogMainThreadIds.clear();
    this.blockedUsers.clear();
    this.sendingTypings.clear();
    this.loadingFullUsers.clear();
    this.loadedFullUsers.clear();
    this.reloadingMessages.clear();
    this.loadingFullChats.clear();
    this.loadingFullParticipants.clear();
    this.loadedFullParticipants.clear();
    this.loadedFullChats.clear();
    this.currentDeletingTaskTime = 0;
    this.currentDeletingTaskMids = null;
    this.currentDeletingTaskChannelId = 0;
    this.gettingNewDeleteTask = false;
    this.loadingDialogs = false;
    this.dialogsEndReached = false;
    this.serverDialogsEndReached = false;
    this.loadingBlockedUsers = false;
    this.firstGettingTask = false;
    this.updatingState = false;
    this.resetingDialogs = false;
    this.lastStatusUpdateTime = 0L;
    this.offlineSent = false;
    this.registeringForPush = false;
    this.getDifferenceFirstSync = true;
    this.uploadingAvatar = null;
    this.statusRequest = 0;
    this.statusSettingState = 0;
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
        MessagesController.this.updatesQueueChannels.clear();
        MessagesController.this.updatesStartWaitTimeChannels.clear();
        MessagesController.this.gettingDifferenceChannels.clear();
        MessagesController.this.channelsPts.clear();
        MessagesController.this.shortPollChannels.clear();
        MessagesController.this.needShortPollChannels.clear();
      }
    });
    if (this.currentDeleteTaskRunnable != null)
    {
      Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
      this.currentDeleteTaskRunnable = null;
    }
    addSupportUser();
    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
  }
  
  protected void clearFullUsers()
  {
    this.loadedFullUsers.clear();
    this.loadedFullChats.clear();
  }
  
  protected void completeDialogsReset(final TLRPC.messages_Dialogs parammessages_Dialogs, int paramInt1, int paramInt2, final int paramInt3, final int paramInt4, final int paramInt5, final LongSparseArray<TLRPC.TL_dialog> paramLongSparseArray, final LongSparseArray<MessageObject> paramLongSparseArray1, TLRPC.Message paramMessage)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.this.gettingDifference = false;
        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(paramInt3);
        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(paramInt4);
        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(paramInt5);
        MessagesController.this.getDifference();
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.access$5802(MessagesController.this, false);
            MessagesController.this.applyDialogsNotificationsSettings(MessagesController.67.this.val$dialogsRes.dialogs);
            if (!UserConfig.getInstance(MessagesController.this.currentAccount).draftsLoaded) {
              DataQuery.getInstance(MessagesController.this.currentAccount).loadDrafts();
            }
            MessagesController.this.putUsers(MessagesController.67.this.val$dialogsRes.users, false);
            MessagesController.this.putChats(MessagesController.67.this.val$dialogsRes.chats, false);
            Object localObject;
            for (int i = 0; i < MessagesController.this.dialogs.size(); i++)
            {
              TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.this.dialogs.get(i);
              if ((int)localTL_dialog.id != 0)
              {
                MessagesController.this.dialogs_dict.remove(localTL_dialog.id);
                localObject = (MessageObject)MessagesController.this.dialogMessage.get(localTL_dialog.id);
                MessagesController.this.dialogMessage.remove(localTL_dialog.id);
                if (localObject != null)
                {
                  MessagesController.this.dialogMessagesByIds.remove(((MessageObject)localObject).getId());
                  if (((MessageObject)localObject).messageOwner.random_id != 0L) {
                    MessagesController.this.dialogMessagesByRandomIds.remove(((MessageObject)localObject).messageOwner.random_id);
                  }
                }
              }
            }
            for (i = 0; i < MessagesController.67.this.val$new_dialogs_dict.size(); i++)
            {
              long l = MessagesController.67.this.val$new_dialogs_dict.keyAt(i);
              localObject = (TLRPC.TL_dialog)MessagesController.67.this.val$new_dialogs_dict.valueAt(i);
              if ((((TLRPC.TL_dialog)localObject).draft instanceof TLRPC.TL_draftMessage)) {
                DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(((TLRPC.TL_dialog)localObject).id, ((TLRPC.TL_dialog)localObject).draft, null, false);
              }
              MessagesController.this.dialogs_dict.put(l, localObject);
              localObject = (MessageObject)MessagesController.67.this.val$new_dialogMessage.get(((TLRPC.TL_dialog)localObject).id);
              MessagesController.this.dialogMessage.put(l, localObject);
              if ((localObject != null) && (((MessageObject)localObject).messageOwner.to_id.channel_id == 0))
              {
                MessagesController.this.dialogMessagesByIds.put(((MessageObject)localObject).getId(), localObject);
                if (((MessageObject)localObject).messageOwner.random_id != 0L) {
                  MessagesController.this.dialogMessagesByRandomIds.put(((MessageObject)localObject).messageOwner.random_id, localObject);
                }
              }
            }
            MessagesController.this.dialogs.clear();
            i = 0;
            int j = MessagesController.this.dialogs_dict.size();
            while (i < j)
            {
              MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(i));
              i++;
            }
            MessagesController.this.sortDialogs(null);
            MessagesController.this.dialogsEndReached = true;
            MessagesController.this.serverDialogsEndReached = false;
            if ((UserConfig.getInstance(MessagesController.this.currentAccount).totalDialogsLoadCount < 400) && (UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId != -1) && (UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId != Integer.MAX_VALUE)) {
              MessagesController.this.loadDialogs(0, 100, false);
            }
            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
          }
        });
      }
    });
  }
  
  public void convertToMegaGroup(final Context paramContext, final int paramInt)
  {
    TLRPC.TL_messages_migrateChat localTL_messages_migrateChat = new TLRPC.TL_messages_migrateChat();
    localTL_messages_migrateChat.chat_id = paramInt;
    final AlertDialog localAlertDialog = new AlertDialog(paramContext, 1);
    localAlertDialog.setMessage(LocaleController.getString("Loading", NUM));
    localAlertDialog.setCanceledOnTouchOutside(false);
    localAlertDialog.setCancelable(false);
    paramInt = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_migrateChat, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (!((Activity)MessagesController.89.this.val$context).isFinishing()) {}
              try
              {
                MessagesController.89.this.val$progressDialog.dismiss();
                return;
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  FileLog.e(localException);
                }
              }
            }
          });
          paramAnonymousTL_error = (TLRPC.Updates)paramAnonymousTLObject;
          MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
        }
        for (;;)
        {
          return;
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (!((Activity)MessagesController.89.this.val$context).isFinishing()) {}
              try
              {
                MessagesController.89.this.val$progressDialog.dismiss();
                AlertDialog.Builder localBuilder = new AlertDialog.Builder(MessagesController.89.this.val$context);
                localBuilder.setTitle(LocaleController.getString("AppName", NUM));
                localBuilder.setMessage(LocaleController.getString("ErrorOccurred", NUM));
                localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
                localBuilder.show().setCanceledOnTouchOutside(true);
                return;
              }
              catch (Exception localException)
              {
                for (;;)
                {
                  FileLog.e(localException);
                }
              }
            }
          });
        }
      }
    });
    localAlertDialog.setButton(-2, LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
      {
        ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(paramInt, true);
        try
        {
          paramAnonymousDialogInterface.dismiss();
          return;
        }
        catch (Exception paramAnonymousDialogInterface)
        {
          for (;;)
          {
            FileLog.e(paramAnonymousDialogInterface);
          }
        }
      }
    });
    try
    {
      localAlertDialog.show();
      return;
    }
    catch (Exception paramContext)
    {
      for (;;) {}
    }
  }
  
  public int createChat(String paramString1, final ArrayList<Integer> paramArrayList, final String paramString2, int paramInt, final BaseFragment paramBaseFragment)
  {
    if (paramInt == 1)
    {
      paramString2 = new TLRPC.TL_chat();
      paramString2.id = UserConfig.getInstance(this.currentAccount).lastBroadcastId;
      paramString2.title = paramString1;
      paramString2.photo = new TLRPC.TL_chatPhotoEmpty();
      paramString2.participants_count = paramArrayList.size();
      paramString2.date = ((int)(System.currentTimeMillis() / 1000L));
      paramString2.version = 1;
      paramString1 = UserConfig.getInstance(this.currentAccount);
      paramString1.lastBroadcastId -= 1;
      putChat(paramString2, false);
      paramString1 = new ArrayList();
      paramString1.add(paramString2);
      MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, paramString1, true, true);
      paramBaseFragment = new TLRPC.TL_chatFull();
      paramBaseFragment.id = paramString2.id;
      paramBaseFragment.chat_photo = new TLRPC.TL_photoEmpty();
      paramBaseFragment.notify_settings = new TLRPC.TL_peerNotifySettingsEmpty();
      paramBaseFragment.exported_invite = new TLRPC.TL_chatInviteEmpty();
      paramBaseFragment.participants = new TLRPC.TL_chatParticipants();
      paramBaseFragment.participants.chat_id = paramString2.id;
      paramBaseFragment.participants.admin_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
      paramBaseFragment.participants.version = 1;
      for (paramInt = 0; paramInt < paramArrayList.size(); paramInt++)
      {
        paramString1 = new TLRPC.TL_chatParticipant();
        paramString1.user_id = ((Integer)paramArrayList.get(paramInt)).intValue();
        paramString1.inviter_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
        paramString1.date = ((int)(System.currentTimeMillis() / 1000L));
        paramBaseFragment.participants.participants.add(paramString1);
      }
      MessagesStorage.getInstance(this.currentAccount).updateChatInfo(paramBaseFragment, false);
      paramString1 = new TLRPC.TL_messageService();
      paramString1.action = new TLRPC.TL_messageActionCreatedBroadcastList();
      paramInt = UserConfig.getInstance(this.currentAccount).getNewMessageId();
      paramString1.id = paramInt;
      paramString1.local_id = paramInt;
      paramString1.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
      paramString1.dialog_id = AndroidUtilities.makeBroadcastId(paramString2.id);
      paramString1.to_id = new TLRPC.TL_peerChat();
      paramString1.to_id.chat_id = paramString2.id;
      paramString1.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
      paramString1.random_id = 0L;
      paramString1.flags |= 0x100;
      UserConfig.getInstance(this.currentAccount).saveConfig(false);
      paramBaseFragment = new MessageObject(this.currentAccount, paramString1, this.users, true);
      paramBaseFragment.messageOwner.send_state = 0;
      paramArrayList = new ArrayList();
      paramArrayList.add(paramBaseFragment);
      paramBaseFragment = new ArrayList();
      paramBaseFragment.add(paramString1);
      MessagesStorage.getInstance(this.currentAccount).putMessages(paramBaseFragment, false, true, false, 0);
      updateInterfaceWithMessages(paramString1.dialog_id, paramArrayList);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, new Object[] { Integer.valueOf(paramString2.id) });
      paramInt = 0;
    }
    for (;;)
    {
      return paramInt;
      if (paramInt == 0)
      {
        paramString2 = new TLRPC.TL_messages_createChat();
        paramString2.title = paramString1;
        paramInt = 0;
        if (paramInt < paramArrayList.size())
        {
          paramString1 = getUser((Integer)paramArrayList.get(paramInt));
          if (paramString1 == null) {}
          for (;;)
          {
            paramInt++;
            break;
            paramString2.users.add(getInputUser(paramString1));
          }
        }
        paramInt = ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramString2, new RequestDelegate()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error != null) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  AlertsCreator.processError(MessagesController.this.currentAccount, paramAnonymousTL_error, MessagesController.87.this.val$fragment, MessagesController.87.this.val$req, new Object[0]);
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                }
              });
            }
            for (;;)
            {
              return;
              paramAnonymousTLObject = (TLRPC.Updates)paramAnonymousTLObject;
              MessagesController.this.processUpdates(paramAnonymousTLObject, false);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.putUsers(paramAnonymousTLObject.users, false);
                  MessagesController.this.putChats(paramAnonymousTLObject.chats, false);
                  if ((paramAnonymousTLObject.chats != null) && (!paramAnonymousTLObject.chats.isEmpty())) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, new Object[] { Integer.valueOf(((TLRPC.Chat)paramAnonymousTLObject.chats.get(0)).id) });
                  }
                  for (;;)
                  {
                    return;
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                  }
                }
              });
            }
          }
        }, 2);
      }
      else
      {
        if ((paramInt == 2) || (paramInt == 4))
        {
          paramArrayList = new TLRPC.TL_channels_createChannel();
          paramArrayList.title = paramString1;
          paramArrayList.about = paramString2;
          if (paramInt == 4) {
            paramArrayList.megagroup = true;
          }
          for (;;)
          {
            paramInt = ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramArrayList, new RequestDelegate()
            {
              public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
              {
                if (paramAnonymousTL_error != null) {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      AlertsCreator.processError(MessagesController.this.currentAccount, paramAnonymousTL_error, MessagesController.88.this.val$fragment, MessagesController.88.this.val$req, new Object[0]);
                      NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                    }
                  });
                }
                for (;;)
                {
                  return;
                  paramAnonymousTLObject = (TLRPC.Updates)paramAnonymousTLObject;
                  MessagesController.this.processUpdates(paramAnonymousTLObject, false);
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      MessagesController.this.putUsers(paramAnonymousTLObject.users, false);
                      MessagesController.this.putChats(paramAnonymousTLObject.chats, false);
                      if ((paramAnonymousTLObject.chats != null) && (!paramAnonymousTLObject.chats.isEmpty())) {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, new Object[] { Integer.valueOf(((TLRPC.Chat)paramAnonymousTLObject.chats.get(0)).id) });
                      }
                      for (;;)
                      {
                        return;
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                      }
                    }
                  });
                }
              }
            }, 2);
            break;
            paramArrayList.broadcast = true;
          }
        }
        paramInt = 0;
      }
    }
  }
  
  public void deleteDialog(long paramLong, int paramInt)
  {
    deleteDialog(paramLong, true, paramInt, 0);
  }
  
  public void deleteMessages(ArrayList<Integer> paramArrayList, ArrayList<Long> paramArrayList1, TLRPC.EncryptedChat paramEncryptedChat, int paramInt, boolean paramBoolean)
  {
    deleteMessages(paramArrayList, paramArrayList1, paramEncryptedChat, paramInt, paramBoolean, 0L, null);
  }
  
  public void deleteMessages(ArrayList<Integer> paramArrayList, ArrayList<Long> paramArrayList1, TLRPC.EncryptedChat paramEncryptedChat, final int paramInt, boolean paramBoolean, final long paramLong, TLObject paramTLObject)
  {
    if (((paramArrayList == null) || (paramArrayList.isEmpty())) && (paramTLObject == null)) {}
    Object localObject;
    for (;;)
    {
      return;
      localObject = null;
      if (paramLong == 0L)
      {
        if (paramInt == 0) {
          for (i = 0; i < paramArrayList.size(); i++)
          {
            localObject = (Integer)paramArrayList.get(i);
            localObject = (MessageObject)this.dialogMessagesByIds.get(((Integer)localObject).intValue());
            if (localObject != null) {
              ((MessageObject)localObject).deleted = true;
            }
          }
        }
        markChannelDialogMessageAsDeleted(paramArrayList, paramInt);
        localObject = new ArrayList();
        for (int i = 0; i < paramArrayList.size(); i++)
        {
          Integer localInteger = (Integer)paramArrayList.get(i);
          if (localInteger.intValue() > 0) {
            ((ArrayList)localObject).add(localInteger);
          }
        }
        MessagesStorage.getInstance(this.currentAccount).markMessagesAsDeleted(paramArrayList, true, paramInt);
        MessagesStorage.getInstance(this.currentAccount).updateDialogsWithDeletedMessages(paramArrayList, null, true, paramInt);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, new Object[] { paramArrayList, Integer.valueOf(paramInt) });
      }
      if (paramInt == 0) {
        break label340;
      }
      if (paramTLObject == null) {
        break;
      }
      paramArrayList = (TLRPC.TL_channels_deleteMessages)paramTLObject;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramArrayList, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.TL_messages_affectedMessages)paramAnonymousTLObject;
            MessagesController.this.processNewChannelDifferenceParams(paramAnonymousTLObject.pts, paramAnonymousTLObject.pts_count, paramInt);
          }
          if (paramLong != 0L) {
            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(paramLong);
          }
        }
      });
    }
    paramEncryptedChat = new TLRPC.TL_channels_deleteMessages();
    paramEncryptedChat.id = ((ArrayList)localObject);
    paramEncryptedChat.channel = getInputChannel(paramInt);
    paramTLObject = null;
    for (;;)
    {
      try
      {
        paramArrayList = new org/telegram/tgnet/NativeByteBuffer;
        paramArrayList.<init>(paramEncryptedChat.getObjectSize() + 8);
      }
      catch (Exception paramArrayList1)
      {
        try
        {
          paramArrayList.writeInt32(7);
          paramArrayList.writeInt32(paramInt);
          paramEncryptedChat.serializeToStream(paramArrayList);
          paramLong = MessagesStorage.getInstance(this.currentAccount).createPendingTask(paramArrayList);
          paramArrayList = paramEncryptedChat;
        }
        catch (Exception paramArrayList1)
        {
          for (;;) {}
        }
        paramArrayList1 = paramArrayList1;
        paramArrayList = paramTLObject;
      }
      FileLog.e(paramArrayList1);
    }
    label340:
    if ((paramArrayList1 != null) && (paramEncryptedChat != null) && (!paramArrayList1.isEmpty())) {
      SecretChatHelper.getInstance(this.currentAccount).sendMessagesDeleteMessage(paramEncryptedChat, paramArrayList1, null);
    }
    if (paramTLObject != null) {
      paramArrayList = (TLRPC.TL_messages_deleteMessages)paramTLObject;
    }
    for (;;)
    {
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramArrayList, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.TL_messages_affectedMessages)paramAnonymousTLObject;
            MessagesController.this.processNewDifferenceParams(-1, paramAnonymousTLObject.pts, -1, paramAnonymousTLObject.pts_count);
          }
          if (paramLong != 0L) {
            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(paramLong);
          }
        }
      });
      break;
      paramEncryptedChat = new TLRPC.TL_messages_deleteMessages();
      paramEncryptedChat.id = ((ArrayList)localObject);
      paramEncryptedChat.revoke = paramBoolean;
      paramTLObject = null;
      try
      {
        paramArrayList = new org/telegram/tgnet/NativeByteBuffer;
        paramArrayList.<init>(paramEncryptedChat.getObjectSize() + 8);
      }
      catch (Exception paramArrayList1)
      {
        for (;;)
        {
          try
          {
            paramArrayList.writeInt32(7);
            paramArrayList.writeInt32(paramInt);
            paramEncryptedChat.serializeToStream(paramArrayList);
            paramLong = MessagesStorage.getInstance(this.currentAccount).createPendingTask(paramArrayList);
            paramArrayList = paramEncryptedChat;
          }
          catch (Exception paramArrayList1)
          {
            continue;
          }
          paramArrayList1 = paramArrayList1;
          paramArrayList = paramTLObject;
          FileLog.e(paramArrayList1);
        }
      }
    }
  }
  
  public void deleteUserChannelHistory(final TLRPC.Chat paramChat, final TLRPC.User paramUser, int paramInt)
  {
    if (paramInt == 0) {
      MessagesStorage.getInstance(this.currentAccount).deleteUserChannelHistory(paramChat.id, paramUser.id);
    }
    TLRPC.TL_channels_deleteUserHistory localTL_channels_deleteUserHistory = new TLRPC.TL_channels_deleteUserHistory();
    localTL_channels_deleteUserHistory.channel = getInputChannel(paramChat);
    localTL_channels_deleteUserHistory.user_id = getInputUser(paramUser);
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_deleteUserHistory, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          paramAnonymousTLObject = (TLRPC.TL_messages_affectedHistory)paramAnonymousTLObject;
          if (paramAnonymousTLObject.offset > 0) {
            MessagesController.this.deleteUserChannelHistory(paramChat, paramUser, paramAnonymousTLObject.offset);
          }
          MessagesController.this.processNewChannelDifferenceParams(paramAnonymousTLObject.pts, paramAnonymousTLObject.pts_count, paramChat.id);
        }
      }
    });
  }
  
  public void deleteUserFromChat(int paramInt, TLRPC.User paramUser, TLRPC.ChatFull paramChatFull)
  {
    deleteUserFromChat(paramInt, paramUser, paramChatFull, false);
  }
  
  public void deleteUserFromChat(final int paramInt, final TLRPC.User paramUser, TLRPC.ChatFull paramChatFull, boolean paramBoolean)
  {
    if (paramUser == null) {}
    do
    {
      return;
      if (paramInt > 0)
      {
        localObject = getInputUser(paramUser);
        localChat = getChat(Integer.valueOf(paramInt));
        final boolean bool = ChatObject.isChannel(localChat);
        if (bool) {
          if ((localObject instanceof TLRPC.TL_inputUserSelf)) {
            if ((localChat.creator) && (paramBoolean))
            {
              paramChatFull = new TLRPC.TL_channels_deleteChannel();
              paramChatFull.channel = getInputChannel(localChat);
            }
          }
        }
        for (;;)
        {
          ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramChatFull, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramUser.id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.this.deleteDialog(-MessagesController.101.this.val$chat_id, 0);
                  }
                });
              }
              if (paramAnonymousTL_error != null) {}
              for (;;)
              {
                return;
                paramAnonymousTLObject = (TLRPC.Updates)paramAnonymousTLObject;
                MessagesController.this.processUpdates(paramAnonymousTLObject, false);
                if ((bool) && (!(localObject instanceof TLRPC.TL_inputUserSelf))) {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      MessagesController.this.loadFullChat(MessagesController.101.this.val$chat_id, 0, true);
                    }
                  }, 1000L);
                }
              }
            }
          }, 64);
          break;
          paramChatFull = new TLRPC.TL_channels_leaveChannel();
          paramChatFull.channel = getInputChannel(localChat);
          continue;
          paramChatFull = new TLRPC.TL_channels_editBanned();
          paramChatFull.channel = getInputChannel(localChat);
          paramChatFull.user_id = ((TLRPC.InputUser)localObject);
          paramChatFull.banned_rights = new TLRPC.TL_channelBannedRights();
          paramChatFull.banned_rights.view_messages = true;
          paramChatFull.banned_rights.send_media = true;
          paramChatFull.banned_rights.send_messages = true;
          paramChatFull.banned_rights.send_stickers = true;
          paramChatFull.banned_rights.send_gifs = true;
          paramChatFull.banned_rights.send_games = true;
          paramChatFull.banned_rights.send_inline = true;
          paramChatFull.banned_rights.embed_links = true;
          continue;
          paramChatFull = new TLRPC.TL_messages_deleteChatUser();
          paramChatFull.chat_id = paramInt;
          paramChatFull.user_id = getInputUser(paramUser);
        }
      }
    } while (!(paramChatFull instanceof TLRPC.TL_chatFull));
    TLRPC.Chat localChat = getChat(Integer.valueOf(paramInt));
    localChat.participants_count -= 1;
    final Object localObject = new ArrayList();
    ((ArrayList)localObject).add(localChat);
    MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, (ArrayList)localObject, true, true);
    int i = 0;
    for (int j = 0;; j++)
    {
      paramInt = i;
      if (j < paramChatFull.participants.participants.size())
      {
        if (((TLRPC.ChatParticipant)paramChatFull.participants.participants.get(j)).user_id == paramUser.id)
        {
          paramChatFull.participants.participants.remove(j);
          paramInt = 1;
        }
      }
      else
      {
        if (paramInt != 0)
        {
          MessagesStorage.getInstance(this.currentAccount).updateChatInfo(paramChatFull, true);
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { paramChatFull, Integer.valueOf(0), Boolean.valueOf(false), null });
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(32) });
        break;
      }
    }
  }
  
  public void deleteUserPhoto(TLRPC.InputPhoto paramInputPhoto)
  {
    TLRPC.TL_photos_updateProfilePhoto localTL_photos_updateProfilePhoto;
    Object localObject;
    if (paramInputPhoto == null)
    {
      localTL_photos_updateProfilePhoto = new TLRPC.TL_photos_updateProfilePhoto();
      localTL_photos_updateProfilePhoto.id = new TLRPC.TL_inputPhotoEmpty();
      UserConfig.getInstance(this.currentAccount).getCurrentUser().photo = new TLRPC.TL_userProfilePhotoEmpty();
      localObject = getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
      paramInputPhoto = (TLRPC.InputPhoto)localObject;
      if (localObject == null) {
        paramInputPhoto = UserConfig.getInstance(this.currentAccount).getCurrentUser();
      }
      if (paramInputPhoto != null) {}
    }
    for (;;)
    {
      return;
      paramInputPhoto.photo = UserConfig.getInstance(this.currentAccount).getCurrentUser().photo;
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1535) });
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_photos_updateProfilePhoto, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTL_error = MessagesController.this.getUser(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
            if (paramAnonymousTL_error != null) {
              break label61;
            }
            paramAnonymousTL_error = UserConfig.getInstance(MessagesController.this.currentAccount).getCurrentUser();
            MessagesController.this.putUser(paramAnonymousTL_error, false);
            if (paramAnonymousTL_error != null) {
              break label78;
            }
          }
          for (;;)
          {
            return;
            label61:
            UserConfig.getInstance(MessagesController.this.currentAccount).setCurrentUser(paramAnonymousTL_error);
            break;
            label78:
            MessagesStorage.getInstance(MessagesController.this.currentAccount).clearUserPhotos(paramAnonymousTL_error.id);
            ArrayList localArrayList = new ArrayList();
            localArrayList.add(paramAnonymousTL_error);
            MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(localArrayList, null, false, true);
            paramAnonymousTL_error.photo = ((TLRPC.UserProfilePhoto)paramAnonymousTLObject);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1535) });
                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(true);
              }
            });
          }
        }
      });
      continue;
      localObject = new TLRPC.TL_photos_deletePhotos();
      ((TLRPC.TL_photos_deletePhotos)localObject).id.add(paramInputPhoto);
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
    }
  }
  
  public void didAddedNewTask(final int paramInt, final SparseArray<ArrayList<Long>> paramSparseArray)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (((MessagesController.this.currentDeletingTaskMids == null) && (!MessagesController.this.gettingNewDeleteTask)) || ((MessagesController.this.currentDeletingTaskTime != 0) && (paramInt < MessagesController.this.currentDeletingTaskTime))) {
          MessagesController.this.getNewDeleteTask(null, 0);
        }
      }
    });
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didCreatedNewDeleteTask, new Object[] { paramSparseArray });
      }
    });
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    Object localObject;
    if (paramInt1 == NotificationCenter.FileDidUpload)
    {
      localObject = (String)paramVarArgs[0];
      paramVarArgs = (TLRPC.InputFile)paramVarArgs[1];
      if ((this.uploadingAvatar != null) && (this.uploadingAvatar.equals(localObject)))
      {
        localObject = new TLRPC.TL_photos_uploadProfilePhoto();
        ((TLRPC.TL_photos_uploadProfilePhoto)localObject).file = paramVarArgs;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTL_error = MessagesController.this.getUser(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
              if (paramAnonymousTL_error != null) {
                break label61;
              }
              paramAnonymousTL_error = UserConfig.getInstance(MessagesController.this.currentAccount).getCurrentUser();
              MessagesController.this.putUser(paramAnonymousTL_error, true);
            }
            while (paramAnonymousTL_error == null)
            {
              return;
              label61:
              UserConfig.getInstance(MessagesController.this.currentAccount).setCurrentUser(paramAnonymousTL_error);
            }
            paramAnonymousTLObject = (TLRPC.TL_photos_photo)paramAnonymousTLObject;
            Object localObject = paramAnonymousTLObject.photo.sizes;
            TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 100);
            localObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 1000);
            paramAnonymousTL_error.photo = new TLRPC.TL_userProfilePhoto();
            paramAnonymousTL_error.photo.photo_id = paramAnonymousTLObject.photo.id;
            if (localPhotoSize != null) {
              paramAnonymousTL_error.photo.photo_small = localPhotoSize.location;
            }
            if (localObject != null) {
              paramAnonymousTL_error.photo.photo_big = ((TLRPC.PhotoSize)localObject).location;
            }
            for (;;)
            {
              MessagesStorage.getInstance(MessagesController.this.currentAccount).clearUserPhotos(paramAnonymousTL_error.id);
              paramAnonymousTLObject = new ArrayList();
              paramAnonymousTLObject.add(paramAnonymousTL_error);
              MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(paramAnonymousTLObject, null, false, true);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(2) });
                  UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(true);
                }
              });
              break;
              if (localPhotoSize != null) {
                paramAnonymousTL_error.photo.photo_small = localPhotoSize.location;
              }
            }
          }
        });
      }
    }
    for (;;)
    {
      return;
      if (paramInt1 == NotificationCenter.FileDidFailUpload)
      {
        paramVarArgs = (String)paramVarArgs[0];
        if ((this.uploadingAvatar != null) && (this.uploadingAvatar.equals(paramVarArgs))) {
          this.uploadingAvatar = null;
        }
      }
      else if (paramInt1 == NotificationCenter.messageReceivedByServer)
      {
        Integer localInteger = (Integer)paramVarArgs[0];
        localObject = (Integer)paramVarArgs[1];
        paramVarArgs = (Long)paramVarArgs[3];
        MessageObject localMessageObject = (MessageObject)this.dialogMessage.get(paramVarArgs.longValue());
        if ((localMessageObject != null) && ((localMessageObject.getId() == localInteger.intValue()) || (localMessageObject.messageOwner.local_id == localInteger.intValue())))
        {
          localMessageObject.messageOwner.id = ((Integer)localObject).intValue();
          localMessageObject.messageOwner.send_state = 0;
        }
        paramVarArgs = (TLRPC.TL_dialog)this.dialogs_dict.get(paramVarArgs.longValue());
        if ((paramVarArgs != null) && (paramVarArgs.top_message == localInteger.intValue()))
        {
          paramVarArgs.top_message = ((Integer)localObject).intValue();
          NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        paramVarArgs = (MessageObject)this.dialogMessagesByIds.get(localInteger.intValue());
        this.dialogMessagesByIds.remove(localInteger.intValue());
        if (paramVarArgs != null) {
          this.dialogMessagesByIds.put(((Integer)localObject).intValue(), paramVarArgs);
        }
      }
      else if (paramInt1 == NotificationCenter.updateMessageMedia)
      {
        localObject = (TLRPC.Message)paramVarArgs[0];
        paramVarArgs = (MessageObject)this.dialogMessagesByIds.get(((TLRPC.Message)localObject).id);
        if (paramVarArgs != null)
        {
          paramVarArgs.messageOwner.media = ((TLRPC.Message)localObject).media;
          if ((((TLRPC.Message)localObject).media.ttl_seconds != 0) && (((((TLRPC.Message)localObject).media.photo instanceof TLRPC.TL_photoEmpty)) || ((((TLRPC.Message)localObject).media.document instanceof TLRPC.TL_documentEmpty))))
          {
            paramVarArgs.setType();
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
          }
        }
      }
    }
  }
  
  public void forceResetDialogs()
  {
    resetDialogs(true, MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
  }
  
  public void generateJoinMessage(final int paramInt, boolean paramBoolean)
  {
    final Object localObject = getChat(Integer.valueOf(paramInt));
    if ((localObject == null) || (!ChatObject.isChannel(paramInt, this.currentAccount)) || (((((TLRPC.Chat)localObject).left) || (((TLRPC.Chat)localObject).kicked)) && (!paramBoolean))) {}
    for (;;)
    {
      return;
      TLRPC.TL_messageService localTL_messageService = new TLRPC.TL_messageService();
      localTL_messageService.flags = 256;
      int i = UserConfig.getInstance(this.currentAccount).getNewMessageId();
      localTL_messageService.id = i;
      localTL_messageService.local_id = i;
      localTL_messageService.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
      localTL_messageService.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
      localTL_messageService.to_id = new TLRPC.TL_peerChannel();
      localTL_messageService.to_id.channel_id = paramInt;
      localTL_messageService.dialog_id = (-paramInt);
      localTL_messageService.post = true;
      localTL_messageService.action = new TLRPC.TL_messageActionChatAddUser();
      localTL_messageService.action.users.add(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
      if (((TLRPC.Chat)localObject).megagroup) {
        localTL_messageService.flags |= 0x80000000;
      }
      UserConfig.getInstance(this.currentAccount).saveConfig(false);
      localObject = new ArrayList();
      ArrayList localArrayList = new ArrayList();
      localArrayList.add(localTL_messageService);
      ((ArrayList)localObject).add(new MessageObject(this.currentAccount, localTL_messageService, true));
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(MessagesController.119.this.val$pushMessages, true, false);
            }
          });
        }
      });
      MessagesStorage.getInstance(this.currentAccount).putMessages(localArrayList, true, true, false, 0);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.this.updateInterfaceWithMessages(-paramInt, localObject);
          NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
      });
    }
  }
  
  public void generateUpdateMessage()
  {
    if ((BuildVars.DEBUG_VERSION) || (SharedConfig.lastUpdateVersion == null) || (SharedConfig.lastUpdateVersion.equals(BuildVars.BUILD_VERSION_STRING))) {}
    for (;;)
    {
      return;
      TLRPC.TL_help_getAppChangelog localTL_help_getAppChangelog = new TLRPC.TL_help_getAppChangelog();
      localTL_help_getAppChangelog.prev_app_version = SharedConfig.lastUpdateVersion;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_help_getAppChangelog, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            SharedConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
            SharedConfig.saveConfig();
          }
          if ((paramAnonymousTLObject instanceof TLRPC.Updates)) {
            MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          }
        }
      });
    }
  }
  
  public void getBlockedUsers(boolean paramBoolean)
  {
    if ((!UserConfig.getInstance(this.currentAccount).isClientActivated()) || (this.loadingBlockedUsers)) {}
    for (;;)
    {
      return;
      this.loadingBlockedUsers = true;
      if (paramBoolean)
      {
        MessagesStorage.getInstance(this.currentAccount).getBlockedUsers();
      }
      else
      {
        TLRPC.TL_contacts_getBlocked localTL_contacts_getBlocked = new TLRPC.TL_contacts_getBlocked();
        localTL_contacts_getBlocked.offset = 0;
        localTL_contacts_getBlocked.limit = 200;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_getBlocked, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            ArrayList localArrayList1 = new ArrayList();
            ArrayList localArrayList2 = null;
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = (TLRPC.contacts_Blocked)paramAnonymousTLObject;
              paramAnonymousTL_error = paramAnonymousTLObject.blocked.iterator();
              while (paramAnonymousTL_error.hasNext()) {
                localArrayList1.add(Integer.valueOf(((TLRPC.TL_contactBlocked)paramAnonymousTL_error.next()).user_id));
              }
              localArrayList2 = paramAnonymousTLObject.users;
              MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(paramAnonymousTLObject.users, null, true, true);
              MessagesStorage.getInstance(MessagesController.this.currentAccount).putBlockedUsers(localArrayList1, true);
            }
            MessagesController.this.processLoadedBlockedUsers(localArrayList1, localArrayList2, false);
          }
        });
      }
    }
  }
  
  protected void getChannelDifference(final int paramInt1, final int paramInt2, final long paramLong, TLRPC.InputChannel paramInputChannel)
  {
    if (this.gettingDifferenceChannels.get(paramInt1)) {}
    int i;
    int j;
    Object localObject1;
    for (;;)
    {
      return;
      i = 100;
      if (paramInt2 == 1)
      {
        if (this.channelsPts.get(paramInt1) == 0)
        {
          j = 1;
          i = 1;
        }
      }
      else
      {
        int k;
        do
        {
          localObject1 = paramInputChannel;
          if (paramInputChannel == null)
          {
            localObject1 = getChat(Integer.valueOf(paramInt1));
            paramInputChannel = (TLRPC.InputChannel)localObject1;
            if (localObject1 == null)
            {
              localObject1 = MessagesStorage.getInstance(this.currentAccount).getChatSync(paramInt1);
              paramInputChannel = (TLRPC.InputChannel)localObject1;
              if (localObject1 != null)
              {
                putChat((TLRPC.Chat)localObject1, true);
                paramInputChannel = (TLRPC.InputChannel)localObject1;
              }
            }
            localObject1 = getInputChannel(paramInputChannel);
          }
          if ((localObject1 != null) && (((TLRPC.InputChannel)localObject1).access_hash != 0L)) {
            break label223;
          }
          if (paramLong == 0L) {
            break;
          }
          MessagesStorage.getInstance(this.currentAccount).removePendingTask(paramLong);
          break;
          j = this.channelsPts.get(paramInt1);
          k = j;
          if (j == 0)
          {
            j = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(paramInt1);
            if (j != 0) {
              this.channelsPts.put(paramInt1, j);
            }
            k = j;
            if (j == 0)
            {
              if ((paramInt2 == 2) || (paramInt2 == 3)) {
                break;
              }
              k = j;
            }
          }
          j = k;
        } while (k != 0);
      }
    }
    label223:
    Object localObject2;
    if (paramLong == 0L) {
      localObject2 = null;
    }
    for (;;)
    {
      try
      {
        paramInputChannel = new org/telegram/tgnet/NativeByteBuffer;
        paramInputChannel.<init>(((TLRPC.InputChannel)localObject1).getObjectSize() + 12);
      }
      catch (Exception localException1)
      {
        try
        {
          paramInputChannel.writeInt32(6);
          paramInputChannel.writeInt32(paramInt1);
          paramInputChannel.writeInt32(paramInt2);
          ((TLRPC.InputChannel)localObject1).serializeToStream(paramInputChannel);
          paramLong = MessagesStorage.getInstance(this.currentAccount).createPendingTask(paramInputChannel);
          this.gettingDifferenceChannels.put(paramInt1, true);
          paramInputChannel = new TLRPC.TL_updates_getChannelDifference();
          paramInputChannel.channel = ((TLRPC.InputChannel)localObject1);
          paramInputChannel.filter = new TLRPC.TL_channelMessagesFilterEmpty();
          paramInputChannel.pts = j;
          paramInputChannel.limit = i;
          if (paramInt2 == 3) {
            break label439;
          }
          bool = true;
          paramInputChannel.force = bool;
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("start getChannelDifference with pts = " + j + " channelId = " + paramInt1);
          }
          ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramInputChannel, new RequestDelegate()
          {
            public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTL_error == null)
              {
                final TLRPC.updates_ChannelDifference localupdates_ChannelDifference = (TLRPC.updates_ChannelDifference)paramAnonymousTLObject;
                final SparseArray localSparseArray = new SparseArray();
                for (int i = 0; i < localupdates_ChannelDifference.users.size(); i++)
                {
                  paramAnonymousTLObject = (TLRPC.User)localupdates_ChannelDifference.users.get(i);
                  localSparseArray.put(paramAnonymousTLObject.id, paramAnonymousTLObject);
                }
                paramAnonymousTL_error = null;
                final ArrayList localArrayList;
                for (i = 0;; i++)
                {
                  paramAnonymousTLObject = paramAnonymousTL_error;
                  if (i < localupdates_ChannelDifference.chats.size())
                  {
                    paramAnonymousTLObject = (TLRPC.Chat)localupdates_ChannelDifference.chats.get(i);
                    if (paramAnonymousTLObject.id != paramInt1) {}
                  }
                  else
                  {
                    localArrayList = new ArrayList();
                    if (localupdates_ChannelDifference.other_updates.isEmpty()) {
                      break;
                    }
                    int j;
                    for (i = 0; i < localupdates_ChannelDifference.other_updates.size(); i = j + 1)
                    {
                      paramAnonymousTL_error = (TLRPC.Update)localupdates_ChannelDifference.other_updates.get(i);
                      j = i;
                      if ((paramAnonymousTL_error instanceof TLRPC.TL_updateMessageID))
                      {
                        localArrayList.add((TLRPC.TL_updateMessageID)paramAnonymousTL_error);
                        localupdates_ChannelDifference.other_updates.remove(i);
                        j = i - 1;
                      }
                    }
                  }
                }
                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(localupdates_ChannelDifference.users, localupdates_ChannelDifference.chats, true, true);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.this.putUsers(localupdates_ChannelDifference.users, false);
                    MessagesController.this.putChats(localupdates_ChannelDifference.chats, false);
                  }
                });
                MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
                {
                  public void run()
                  {
                    if (!localArrayList.isEmpty())
                    {
                      final SparseArray localSparseArray = new SparseArray();
                      Iterator localIterator = localArrayList.iterator();
                      while (localIterator.hasNext())
                      {
                        TLRPC.TL_updateMessageID localTL_updateMessageID = (TLRPC.TL_updateMessageID)localIterator.next();
                        long[] arrayOfLong = MessagesStorage.getInstance(MessagesController.this.currentAccount).updateMessageStateAndId(localTL_updateMessageID.random_id, null, localTL_updateMessageID.id, 0, false, MessagesController.115.this.val$channelId);
                        if (arrayOfLong != null) {
                          localSparseArray.put(localTL_updateMessageID.id, arrayOfLong);
                        }
                      }
                      if (localSparseArray.size() != 0) {
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            for (int i = 0; i < localSparseArray.size(); i++)
                            {
                              int j = localSparseArray.keyAt(i);
                              long[] arrayOfLong = (long[])localSparseArray.valueAt(i);
                              int k = (int)arrayOfLong[1];
                              SendMessagesHelper.getInstance(MessagesController.this.currentAccount).processSentMessage(k);
                              NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { Integer.valueOf(k), Integer.valueOf(j), null, Long.valueOf(arrayOfLong[0]) });
                            }
                          }
                        });
                      }
                    }
                    Utilities.stageQueue.postRunnable(new Runnable()
                    {
                      public void run()
                      {
                        long l1;
                        Object localObject1;
                        Object localObject2;
                        Object localObject3;
                        int i;
                        Object localObject4;
                        label326:
                        boolean bool;
                        if (((MessagesController.115.2.this.val$res instanceof TLRPC.TL_updates_channelDifference)) || ((MessagesController.115.2.this.val$res instanceof TLRPC.TL_updates_channelDifferenceEmpty)))
                        {
                          if (!MessagesController.115.2.this.val$res.new_messages.isEmpty())
                          {
                            final LongSparseArray localLongSparseArray = new LongSparseArray();
                            ImageLoader.saveMessagesThumbs(MessagesController.115.2.this.val$res.new_messages);
                            final ArrayList localArrayList = new ArrayList();
                            l1 = -MessagesController.115.this.val$channelId;
                            localObject1 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(l1));
                            localObject2 = localObject1;
                            if (localObject1 == null)
                            {
                              localObject2 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, l1));
                              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(l1), localObject2);
                            }
                            localObject3 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(l1));
                            localObject1 = localObject3;
                            if (localObject3 == null)
                            {
                              localObject1 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, l1));
                              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(l1), localObject1);
                            }
                            i = 0;
                            if (i < MessagesController.115.2.this.val$res.new_messages.size())
                            {
                              localObject4 = (TLRPC.Message)MessagesController.115.2.this.val$res.new_messages.get(i);
                              if ((MessagesController.115.2.this.val$channelFinal == null) || (!MessagesController.115.2.this.val$channelFinal.left)) {
                                if (((TLRPC.Message)localObject4).out)
                                {
                                  localObject3 = localObject1;
                                  if ((((Integer)localObject3).intValue() >= ((TLRPC.Message)localObject4).id) || ((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChannelCreate))) {
                                    break label542;
                                  }
                                }
                              }
                              label542:
                              for (bool = true;; bool = false)
                              {
                                ((TLRPC.Message)localObject4).unread = bool;
                                if ((MessagesController.115.2.this.val$channelFinal != null) && (MessagesController.115.2.this.val$channelFinal.megagroup)) {
                                  ((TLRPC.Message)localObject4).flags |= 0x80000000;
                                }
                                MessageObject localMessageObject = new MessageObject(MessagesController.this.currentAccount, (TLRPC.Message)localObject4, MessagesController.115.2.this.val$usersDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(l1)));
                                if ((!localMessageObject.isOut()) && (localMessageObject.isUnread())) {
                                  localArrayList.add(localMessageObject);
                                }
                                long l2 = -MessagesController.115.this.val$channelId;
                                localObject4 = (ArrayList)localLongSparseArray.get(l2);
                                localObject3 = localObject4;
                                if (localObject4 == null)
                                {
                                  localObject3 = new ArrayList();
                                  localLongSparseArray.put(l2, localObject3);
                                }
                                ((ArrayList)localObject3).add(localMessageObject);
                                i++;
                                break;
                                localObject3 = localObject2;
                                break label326;
                              }
                            }
                            AndroidUtilities.runOnUIThread(new Runnable()
                            {
                              public void run()
                              {
                                for (int i = 0; i < localLongSparseArray.size(); i++)
                                {
                                  long l = localLongSparseArray.keyAt(i);
                                  ArrayList localArrayList = (ArrayList)localLongSparseArray.valueAt(i);
                                  MessagesController.this.updateInterfaceWithMessages(l, localArrayList);
                                }
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                              }
                            });
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
                            {
                              public void run()
                              {
                                if (!localArrayList.isEmpty()) {
                                  AndroidUtilities.runOnUIThread(new Runnable()
                                  {
                                    public void run()
                                    {
                                      NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(MessagesController.115.2.2.2.this.val$pushMessages, true, false);
                                    }
                                  });
                                }
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(MessagesController.115.2.this.val$res.new_messages, true, false, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                              }
                            });
                          }
                          if (!MessagesController.115.2.this.val$res.other_updates.isEmpty()) {
                            MessagesController.this.processUpdateArray(MessagesController.115.2.this.val$res.other_updates, MessagesController.115.2.this.val$res.users, MessagesController.115.2.this.val$res.chats, true);
                          }
                          MessagesController.this.processChannelsUpdatesQueue(MessagesController.115.this.val$channelId, 1);
                          MessagesStorage.getInstance(MessagesController.this.currentAccount).saveChannelPts(MessagesController.115.this.val$channelId, MessagesController.115.2.this.val$res.pts);
                        }
                        for (;;)
                        {
                          MessagesController.this.gettingDifferenceChannels.delete(MessagesController.115.this.val$channelId);
                          MessagesController.this.channelsPts.put(MessagesController.115.this.val$channelId, MessagesController.115.2.this.val$res.pts);
                          if ((MessagesController.115.2.this.val$res.flags & 0x2) != 0) {
                            MessagesController.this.shortPollChannels.put(MessagesController.115.this.val$channelId, (int)(System.currentTimeMillis() / 1000L) + MessagesController.115.2.this.val$res.timeout);
                          }
                          if (!MessagesController.115.2.this.val$res.isFinal) {
                            MessagesController.this.getChannelDifference(MessagesController.115.this.val$channelId);
                          }
                          if (BuildVars.LOGS_ENABLED)
                          {
                            FileLog.d("received channel difference with pts = " + MessagesController.115.2.this.val$res.pts + " channelId = " + MessagesController.115.this.val$channelId);
                            FileLog.d("new_messages = " + MessagesController.115.2.this.val$res.new_messages.size() + " messages = " + MessagesController.115.2.this.val$res.messages.size() + " users = " + MessagesController.115.2.this.val$res.users.size() + " chats = " + MessagesController.115.2.this.val$res.chats.size() + " other updates = " + MessagesController.115.2.this.val$res.other_updates.size());
                          }
                          if (MessagesController.115.this.val$newTaskId != 0L) {
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(MessagesController.115.this.val$newTaskId);
                          }
                          return;
                          if ((MessagesController.115.2.this.val$res instanceof TLRPC.TL_updates_channelDifferenceTooLong))
                          {
                            l1 = -MessagesController.115.this.val$channelId;
                            localObject1 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(l1));
                            localObject2 = localObject1;
                            if (localObject1 == null)
                            {
                              localObject2 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, l1));
                              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(l1), localObject2);
                            }
                            localObject3 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(l1));
                            localObject1 = localObject3;
                            if (localObject3 == null)
                            {
                              localObject1 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, l1));
                              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(l1), localObject1);
                            }
                            i = 0;
                            if (i < MessagesController.115.2.this.val$res.messages.size())
                            {
                              localObject4 = (TLRPC.Message)MessagesController.115.2.this.val$res.messages.get(i);
                              ((TLRPC.Message)localObject4).dialog_id = (-MessagesController.115.this.val$channelId);
                              if ((!(((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChannelCreate)) && ((MessagesController.115.2.this.val$channelFinal == null) || (!MessagesController.115.2.this.val$channelFinal.left))) {
                                if (((TLRPC.Message)localObject4).out)
                                {
                                  localObject3 = localObject1;
                                  label1394:
                                  if (((Integer)localObject3).intValue() >= ((TLRPC.Message)localObject4).id) {
                                    break label1466;
                                  }
                                }
                              }
                              label1466:
                              for (bool = true;; bool = false)
                              {
                                ((TLRPC.Message)localObject4).unread = bool;
                                if ((MessagesController.115.2.this.val$channelFinal != null) && (MessagesController.115.2.this.val$channelFinal.megagroup)) {
                                  ((TLRPC.Message)localObject4).flags |= 0x80000000;
                                }
                                i++;
                                break;
                                localObject3 = localObject2;
                                break label1394;
                              }
                            }
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).overwriteChannel(MessagesController.115.this.val$channelId, (TLRPC.TL_updates_channelDifferenceTooLong)MessagesController.115.2.this.val$res, MessagesController.115.this.val$newDialogType);
                          }
                        }
                      }
                    });
                  }
                });
              }
              for (;;)
              {
                return;
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.this.checkChannelError(paramAnonymousTL_error.text, MessagesController.115.this.val$channelId);
                  }
                });
                MessagesController.this.gettingDifferenceChannels.delete(paramInt1);
                if (paramLong != 0L) {
                  MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(paramLong);
                }
              }
            }
          });
        }
        catch (Exception localException2)
        {
          boolean bool;
          for (;;) {}
        }
        localException1 = localException1;
        paramInputChannel = (TLRPC.InputChannel)localObject2;
      }
      FileLog.e(localException1);
      continue;
      continue;
      label439:
      bool = false;
    }
  }
  
  public TLRPC.Chat getChat(Integer paramInteger)
  {
    return (TLRPC.Chat)this.chats.get(paramInteger);
  }
  
  public void getDifference()
  {
    getDifference(MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue(), false);
  }
  
  public void getDifference(int paramInt1, final int paramInt2, final int paramInt3, boolean paramBoolean)
  {
    registerForPush(SharedConfig.pushString);
    if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() == 0)
    {
      loadCurrentState();
      return;
    }
    TLRPC.TL_updates_getDifference localTL_updates_getDifference;
    if ((paramBoolean) || (!this.gettingDifference))
    {
      this.gettingDifference = true;
      localTL_updates_getDifference = new TLRPC.TL_updates_getDifference();
      localTL_updates_getDifference.pts = paramInt1;
      localTL_updates_getDifference.date = paramInt2;
      localTL_updates_getDifference.qts = paramInt3;
      if (this.getDifferenceFirstSync)
      {
        localTL_updates_getDifference.flags |= 0x1;
        if (!ConnectionsManager.isConnectedOrConnectingToWiFi()) {
          break label216;
        }
      }
    }
    label216:
    for (localTL_updates_getDifference.pts_total_limit = 5000;; localTL_updates_getDifference.pts_total_limit = 1000)
    {
      this.getDifferenceFirstSync = false;
      if (localTL_updates_getDifference.date == 0) {
        localTL_updates_getDifference.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
      }
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("start getDifference with date = " + paramInt2 + " pts = " + paramInt1 + " qts = " + paramInt3);
      }
      ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(true);
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_updates_getDifference, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.updates_Difference)paramAnonymousTLObject;
            if ((paramAnonymousTLObject instanceof TLRPC.TL_updates_differenceTooLong)) {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  MessagesController.this.loadedFullUsers.clear();
                  MessagesController.this.loadedFullChats.clear();
                  MessagesController.this.resetDialogs(true, MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), paramAnonymousTLObject.pts, MessagesController.116.this.val$date, MessagesController.116.this.val$qts);
                }
              });
            }
          }
          for (;;)
          {
            return;
            if ((paramAnonymousTLObject instanceof TLRPC.TL_updates_differenceSlice)) {
              MessagesController.this.getDifference(paramAnonymousTLObject.intermediate_state.pts, paramAnonymousTLObject.intermediate_state.date, paramAnonymousTLObject.intermediate_state.qts, true);
            }
            paramAnonymousTL_error = new SparseArray();
            final SparseArray localSparseArray = new SparseArray();
            Object localObject;
            for (int i = 0; i < paramAnonymousTLObject.users.size(); i++)
            {
              localObject = (TLRPC.User)paramAnonymousTLObject.users.get(i);
              paramAnonymousTL_error.put(((TLRPC.User)localObject).id, localObject);
            }
            for (i = 0; i < paramAnonymousTLObject.chats.size(); i++)
            {
              localObject = (TLRPC.Chat)paramAnonymousTLObject.chats.get(i);
              localSparseArray.put(((TLRPC.Chat)localObject).id, localObject);
            }
            final ArrayList localArrayList = new ArrayList();
            if (!paramAnonymousTLObject.other_updates.isEmpty())
            {
              i = 0;
              if (i < paramAnonymousTLObject.other_updates.size())
              {
                localObject = (TLRPC.Update)paramAnonymousTLObject.other_updates.get(i);
                int j;
                if ((localObject instanceof TLRPC.TL_updateMessageID))
                {
                  localArrayList.add((TLRPC.TL_updateMessageID)localObject);
                  paramAnonymousTLObject.other_updates.remove(i);
                  j = i - 1;
                }
                for (;;)
                {
                  i = j + 1;
                  break;
                  j = i;
                  if (MessagesController.this.getUpdateType((TLRPC.Update)localObject) == 2)
                  {
                    int k = MessagesController.getUpdateChannelId((TLRPC.Update)localObject);
                    j = MessagesController.this.channelsPts.get(k);
                    int m = j;
                    if (j == 0)
                    {
                      j = MessagesStorage.getInstance(MessagesController.this.currentAccount).getChannelPtsSync(k);
                      m = j;
                      if (j != 0)
                      {
                        MessagesController.this.channelsPts.put(k, j);
                        m = j;
                      }
                    }
                    j = i;
                    if (m != 0)
                    {
                      j = i;
                      if (MessagesController.getUpdatePts((TLRPC.Update)localObject) <= m)
                      {
                        paramAnonymousTLObject.other_updates.remove(i);
                        j = i - 1;
                      }
                    }
                  }
                }
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.loadedFullUsers.clear();
                MessagesController.this.loadedFullChats.clear();
                MessagesController.this.putUsers(paramAnonymousTLObject.users, false);
                MessagesController.this.putChats(paramAnonymousTLObject.chats, false);
              }
            });
            MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
            {
              public void run()
              {
                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(paramAnonymousTLObject.users, paramAnonymousTLObject.chats, true, false);
                if (!localArrayList.isEmpty())
                {
                  final SparseArray localSparseArray = new SparseArray();
                  for (int i = 0; i < localArrayList.size(); i++)
                  {
                    TLRPC.TL_updateMessageID localTL_updateMessageID = (TLRPC.TL_updateMessageID)localArrayList.get(i);
                    long[] arrayOfLong = MessagesStorage.getInstance(MessagesController.this.currentAccount).updateMessageStateAndId(localTL_updateMessageID.random_id, null, localTL_updateMessageID.id, 0, false, 0);
                    if (arrayOfLong != null) {
                      localSparseArray.put(localTL_updateMessageID.id, arrayOfLong);
                    }
                  }
                  if (localSparseArray.size() != 0) {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        for (int i = 0; i < localSparseArray.size(); i++)
                        {
                          int j = localSparseArray.keyAt(i);
                          long[] arrayOfLong = (long[])localSparseArray.valueAt(i);
                          int k = (int)arrayOfLong[1];
                          SendMessagesHelper.getInstance(MessagesController.this.currentAccount).processSentMessage(k);
                          NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { Integer.valueOf(k), Integer.valueOf(j), null, Long.valueOf(arrayOfLong[0]) });
                        }
                      }
                    });
                  }
                }
                Utilities.stageQueue.postRunnable(new Runnable()
                {
                  public void run()
                  {
                    int i;
                    if ((!MessagesController.116.3.this.val$res.new_messages.isEmpty()) || (!MessagesController.116.3.this.val$res.new_encrypted_messages.isEmpty()))
                    {
                      final LongSparseArray localLongSparseArray = new LongSparseArray();
                      Object localObject1;
                      for (i = 0; i < MessagesController.116.3.this.val$res.new_encrypted_messages.size(); i++)
                      {
                        localObject1 = (TLRPC.EncryptedMessage)MessagesController.116.3.this.val$res.new_encrypted_messages.get(i);
                        localObject1 = SecretChatHelper.getInstance(MessagesController.this.currentAccount).decryptMessage((TLRPC.EncryptedMessage)localObject1);
                        if ((localObject1 != null) && (!((ArrayList)localObject1).isEmpty())) {
                          MessagesController.116.3.this.val$res.new_messages.addAll((Collection)localObject1);
                        }
                      }
                      ImageLoader.saveMessagesThumbs(MessagesController.116.3.this.val$res.new_messages);
                      final ArrayList localArrayList = new ArrayList();
                      int j = UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId();
                      i = 0;
                      if (i < MessagesController.116.3.this.val$res.new_messages.size())
                      {
                        TLRPC.Message localMessage = (TLRPC.Message)MessagesController.116.3.this.val$res.new_messages.get(i);
                        if (localMessage.dialog_id == 0L) {
                          if (localMessage.to_id.chat_id == 0) {
                            break label528;
                          }
                        }
                        Object localObject2;
                        Object localObject3;
                        for (localMessage.dialog_id = (-localMessage.to_id.chat_id);; localMessage.dialog_id = localMessage.to_id.user_id)
                        {
                          if ((int)localMessage.dialog_id != 0)
                          {
                            if ((localMessage.action instanceof TLRPC.TL_messageActionChatDeleteUser))
                            {
                              localObject1 = (TLRPC.User)MessagesController.116.3.this.val$usersDict.get(localMessage.action.user_id);
                              if ((localObject1 != null) && (((TLRPC.User)localObject1).bot))
                              {
                                localMessage.reply_markup = new TLRPC.TL_replyKeyboardHide();
                                localMessage.flags |= 0x40;
                              }
                            }
                            if ((!(localMessage.action instanceof TLRPC.TL_messageActionChatMigrateTo)) && (!(localMessage.action instanceof TLRPC.TL_messageActionChannelCreate))) {
                              break label588;
                            }
                            localMessage.unread = false;
                            localMessage.media_unread = false;
                          }
                          if (localMessage.dialog_id == j)
                          {
                            localMessage.unread = false;
                            localMessage.media_unread = false;
                            localMessage.out = true;
                          }
                          localObject2 = new MessageObject(MessagesController.this.currentAccount, localMessage, MessagesController.116.3.this.val$usersDict, MessagesController.116.3.this.val$chatsDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(localMessage.dialog_id)));
                          if ((!((MessageObject)localObject2).isOut()) && (((MessageObject)localObject2).isUnread())) {
                            localArrayList.add(localObject2);
                          }
                          localObject3 = (ArrayList)localLongSparseArray.get(localMessage.dialog_id);
                          localObject1 = localObject3;
                          if (localObject3 == null)
                          {
                            localObject1 = new ArrayList();
                            localLongSparseArray.put(localMessage.dialog_id, localObject1);
                          }
                          ((ArrayList)localObject1).add(localObject2);
                          i++;
                          break;
                          label528:
                          if (localMessage.to_id.user_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                            localMessage.to_id.user_id = localMessage.from_id;
                          }
                        }
                        label588:
                        if (localMessage.out)
                        {
                          localObject1 = MessagesController.this.dialogs_read_outbox_max;
                          label610:
                          localObject2 = (Integer)((ConcurrentHashMap)localObject1).get(Long.valueOf(localMessage.dialog_id));
                          localObject3 = localObject2;
                          if (localObject2 == null)
                          {
                            localObject3 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(localMessage.out, localMessage.dialog_id));
                            ((ConcurrentHashMap)localObject1).put(Long.valueOf(localMessage.dialog_id), localObject3);
                          }
                          if (((Integer)localObject3).intValue() >= localMessage.id) {
                            break label728;
                          }
                        }
                        label728:
                        for (boolean bool = true;; bool = false)
                        {
                          localMessage.unread = bool;
                          break;
                          localObject1 = MessagesController.this.dialogs_read_inbox_max;
                          break label610;
                        }
                      }
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          for (int i = 0; i < localLongSparseArray.size(); i++)
                          {
                            long l = localLongSparseArray.keyAt(i);
                            ArrayList localArrayList = (ArrayList)localLongSparseArray.valueAt(i);
                            MessagesController.this.updateInterfaceWithMessages(l, localArrayList);
                          }
                          NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        }
                      });
                      MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
                      {
                        public void run()
                        {
                          if (!localArrayList.isEmpty()) {
                            AndroidUtilities.runOnUIThread(new Runnable()
                            {
                              public void run()
                              {
                                NotificationsController localNotificationsController = NotificationsController.getInstance(MessagesController.this.currentAccount);
                                ArrayList localArrayList = MessagesController.116.3.2.2.this.val$pushMessages;
                                if (!(MessagesController.116.3.this.val$res instanceof TLRPC.TL_updates_differenceSlice)) {}
                                for (boolean bool = true;; bool = false)
                                {
                                  localNotificationsController.processNewMessages(localArrayList, bool, false);
                                  return;
                                }
                              }
                            });
                          }
                          MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(MessagesController.116.3.this.val$res.new_messages, true, false, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                        }
                      });
                      SecretChatHelper.getInstance(MessagesController.this.currentAccount).processPendingEncMessages();
                    }
                    if (!MessagesController.116.3.this.val$res.other_updates.isEmpty()) {
                      MessagesController.this.processUpdateArray(MessagesController.116.3.this.val$res.other_updates, MessagesController.116.3.this.val$res.users, MessagesController.116.3.this.val$res.chats, true);
                    }
                    if ((MessagesController.116.3.this.val$res instanceof TLRPC.TL_updates_difference))
                    {
                      MessagesController.this.gettingDifference = false;
                      MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(MessagesController.116.3.this.val$res.state.seq);
                      MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(MessagesController.116.3.this.val$res.state.date);
                      MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(MessagesController.116.3.this.val$res.state.pts);
                      MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(MessagesController.116.3.this.val$res.state.qts);
                      ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                      for (i = 0; i < 3; i++) {
                        MessagesController.this.processUpdatesQueue(i, 1);
                      }
                    }
                    if ((MessagesController.116.3.this.val$res instanceof TLRPC.TL_updates_differenceSlice))
                    {
                      MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(MessagesController.116.3.this.val$res.intermediate_state.date);
                      MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(MessagesController.116.3.this.val$res.intermediate_state.pts);
                      MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(MessagesController.116.3.this.val$res.intermediate_state.qts);
                    }
                    for (;;)
                    {
                      MessagesStorage.getInstance(MessagesController.this.currentAccount).saveDiffParams(MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastQtsValue());
                      if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("received difference with date = " + MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastDateValue() + " pts = " + MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastPtsValue() + " seq = " + MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue() + " messages = " + MessagesController.116.3.this.val$res.new_messages.size() + " users = " + MessagesController.116.3.this.val$res.users.size() + " chats = " + MessagesController.116.3.this.val$res.chats.size() + " other updates = " + MessagesController.116.3.this.val$res.other_updates.size());
                      }
                      return;
                      if ((MessagesController.116.3.this.val$res instanceof TLRPC.TL_updates_differenceEmpty))
                      {
                        MessagesController.this.gettingDifference = false;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(MessagesController.116.3.this.val$res.seq);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(MessagesController.116.3.this.val$res.date);
                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                        for (i = 0; i < 3; i++) {
                          MessagesController.this.processUpdatesQueue(i, 1);
                        }
                      }
                    }
                  }
                });
              }
            });
            continue;
            MessagesController.this.gettingDifference = false;
            ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
          }
        }
      });
      break;
      break;
    }
  }
  
  public TLRPC.EncryptedChat getEncryptedChat(Integer paramInteger)
  {
    return (TLRPC.EncryptedChat)this.encryptedChats.get(paramInteger);
  }
  
  public TLRPC.EncryptedChat getEncryptedChatDB(int paramInt, boolean paramBoolean)
  {
    Object localObject1 = (TLRPC.EncryptedChat)this.encryptedChats.get(Integer.valueOf(paramInt));
    if (localObject1 != null)
    {
      localObject2 = localObject1;
      if (!paramBoolean) {
        break label129;
      }
      if (!(localObject1 instanceof TLRPC.TL_encryptedChatWaiting))
      {
        localObject2 = localObject1;
        if (!(localObject1 instanceof TLRPC.TL_encryptedChatRequested)) {
          break label129;
        }
      }
    }
    Object localObject2 = new CountDownLatch(1);
    ArrayList localArrayList = new ArrayList();
    MessagesStorage.getInstance(this.currentAccount).getEncryptedChat(paramInt, (CountDownLatch)localObject2, localArrayList);
    try
    {
      ((CountDownLatch)localObject2).await();
      localObject2 = localObject1;
      if (localArrayList.size() == 2)
      {
        localObject2 = (TLRPC.EncryptedChat)localArrayList.get(0);
        localObject1 = (TLRPC.User)localArrayList.get(1);
        putEncryptedChat((TLRPC.EncryptedChat)localObject2, false);
        putUser((TLRPC.User)localObject1, true);
      }
      label129:
      return (TLRPC.EncryptedChat)localObject2;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        FileLog.e(localException);
      }
    }
  }
  
  public TLRPC.ExportedChatInvite getExportedInvite(int paramInt)
  {
    return (TLRPC.ExportedChatInvite)this.exportedChats.get(paramInt);
  }
  
  public TLRPC.InputChannel getInputChannel(int paramInt)
  {
    return getInputChannel(getChat(Integer.valueOf(paramInt)));
  }
  
  public TLRPC.InputPeer getInputPeer(int paramInt)
  {
    Object localObject1;
    Object localObject2;
    if (paramInt < 0)
    {
      localObject1 = getChat(Integer.valueOf(-paramInt));
      if (ChatObject.isChannel((TLRPC.Chat)localObject1))
      {
        localObject2 = new TLRPC.TL_inputPeerChannel();
        ((TLRPC.InputPeer)localObject2).channel_id = (-paramInt);
        ((TLRPC.InputPeer)localObject2).access_hash = ((TLRPC.Chat)localObject1).access_hash;
      }
    }
    for (;;)
    {
      return (TLRPC.InputPeer)localObject2;
      localObject2 = new TLRPC.TL_inputPeerChat();
      ((TLRPC.InputPeer)localObject2).chat_id = (-paramInt);
      continue;
      TLRPC.User localUser = getUser(Integer.valueOf(paramInt));
      localObject1 = new TLRPC.TL_inputPeerUser();
      ((TLRPC.InputPeer)localObject1).user_id = paramInt;
      localObject2 = localObject1;
      if (localUser != null)
      {
        ((TLRPC.InputPeer)localObject1).access_hash = localUser.access_hash;
        localObject2 = localObject1;
      }
    }
  }
  
  public TLRPC.InputUser getInputUser(int paramInt)
  {
    return getInputUser(getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(paramInt)));
  }
  
  public TLRPC.InputUser getInputUser(TLRPC.User paramUser)
  {
    if (paramUser == null) {
      paramUser = new TLRPC.TL_inputUserEmpty();
    }
    for (;;)
    {
      return paramUser;
      if (paramUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId())
      {
        paramUser = new TLRPC.TL_inputUserSelf();
      }
      else
      {
        TLRPC.TL_inputUser localTL_inputUser = new TLRPC.TL_inputUser();
        localTL_inputUser.user_id = paramUser.id;
        localTL_inputUser.access_hash = paramUser.access_hash;
        paramUser = localTL_inputUser;
      }
    }
  }
  
  public void getNewDeleteTask(final ArrayList<Integer> paramArrayList, final int paramInt)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.access$4002(MessagesController.this, true);
        MessagesStorage.getInstance(MessagesController.this.currentAccount).getNewTask(paramArrayList, paramInt);
      }
    });
  }
  
  public TLRPC.Peer getPeer(int paramInt)
  {
    Object localObject;
    if (paramInt < 0)
    {
      localObject = getChat(Integer.valueOf(-paramInt));
      if (((localObject instanceof TLRPC.TL_channel)) || ((localObject instanceof TLRPC.TL_channelForbidden)))
      {
        localObject = new TLRPC.TL_peerChannel();
        ((TLRPC.Peer)localObject).channel_id = (-paramInt);
      }
    }
    for (;;)
    {
      return (TLRPC.Peer)localObject;
      localObject = new TLRPC.TL_peerChat();
      ((TLRPC.Peer)localObject).chat_id = (-paramInt);
      continue;
      getUser(Integer.valueOf(paramInt));
      localObject = new TLRPC.TL_peerUser();
      ((TLRPC.Peer)localObject).user_id = paramInt;
    }
  }
  
  public long getUpdatesStartTime(int paramInt)
  {
    long l;
    if (paramInt == 0) {
      l = this.updatesStartWaitTimeSeq;
    }
    for (;;)
    {
      return l;
      if (paramInt == 1) {
        l = this.updatesStartWaitTimePts;
      } else if (paramInt == 2) {
        l = this.updatesStartWaitTimeQts;
      } else {
        l = 0L;
      }
    }
  }
  
  public TLRPC.User getUser(Integer paramInteger)
  {
    return (TLRPC.User)this.users.get(paramInteger);
  }
  
  public TLRPC.TL_userFull getUserFull(int paramInt)
  {
    return (TLRPC.TL_userFull)this.fullUsers.get(paramInt);
  }
  
  public TLObject getUserOrChat(String paramString)
  {
    if ((paramString == null) || (paramString.length() == 0)) {}
    for (paramString = null;; paramString = (TLObject)this.objectsByUsernames.get(paramString.toLowerCase())) {
      return paramString;
    }
  }
  
  public ConcurrentHashMap<Integer, TLRPC.User> getUsers()
  {
    return this.users;
  }
  
  public void hideReportSpam(long paramLong, TLRPC.User paramUser, TLRPC.Chat paramChat)
  {
    if ((paramUser == null) && (paramChat == null)) {}
    do
    {
      return;
      localObject = this.notificationsPreferences.edit();
      ((SharedPreferences.Editor)localObject).putInt("spam3_" + paramLong, 1);
      ((SharedPreferences.Editor)localObject).commit();
    } while ((int)paramLong == 0);
    Object localObject = new TLRPC.TL_messages_hideReportSpam();
    if (paramUser != null) {
      ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(paramUser.id);
    }
    for (;;)
    {
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
      break;
      if (paramChat != null) {
        ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(-paramChat.id);
      }
    }
  }
  
  public boolean isChannelAdmin(int paramInt1, int paramInt2)
  {
    ArrayList localArrayList = (ArrayList)this.channelAdmins.get(paramInt1);
    if ((localArrayList != null) && (localArrayList.indexOf(Integer.valueOf(paramInt2)) >= 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isDialogCreated(long paramLong)
  {
    return this.createdDialogMainThreadIds.contains(Long.valueOf(paramLong));
  }
  
  public boolean isDialogMuted(long paramLong)
  {
    boolean bool = true;
    int i = this.notificationsPreferences.getInt("notify2_" + paramLong, 0);
    if (i == 2) {}
    for (;;)
    {
      return bool;
      if ((i != 3) || (this.notificationsPreferences.getInt("notifyuntil_" + paramLong, 0) < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime())) {
        bool = false;
      }
    }
  }
  
  public void loadChannelAdmins(final int paramInt, boolean paramBoolean)
  {
    if (this.loadingChannelAdmins.indexOfKey(paramInt) >= 0) {}
    for (;;)
    {
      return;
      this.loadingChannelAdmins.put(paramInt, 0);
      if (paramBoolean)
      {
        MessagesStorage.getInstance(this.currentAccount).loadChannelAdmins(paramInt);
      }
      else
      {
        TLRPC.TL_channels_getParticipants localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
        ArrayList localArrayList = (ArrayList)this.channelAdmins.get(paramInt);
        if (localArrayList != null)
        {
          long l = 0L;
          for (int i = 0; i < localArrayList.size(); i++) {
            l = (20261L * l + 2147483648L + ((Integer)localArrayList.get(i)).intValue()) % 2147483648L;
          }
          localTL_channels_getParticipants.hash = ((int)l);
        }
        localTL_channels_getParticipants.channel = getInputChannel(paramInt);
        localTL_channels_getParticipants.limit = 100;
        localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsAdmins();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_getParticipants, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if ((paramAnonymousTLObject instanceof TLRPC.TL_channels_channelParticipants))
            {
              paramAnonymousTLObject = (TLRPC.TL_channels_channelParticipants)paramAnonymousTLObject;
              paramAnonymousTL_error = new ArrayList(paramAnonymousTLObject.participants.size());
              for (int i = 0; i < paramAnonymousTLObject.participants.size(); i++) {
                paramAnonymousTL_error.add(Integer.valueOf(((TLRPC.ChannelParticipant)paramAnonymousTLObject.participants.get(i)).user_id));
              }
              MessagesController.this.processLoadedChannelAdmins(paramAnonymousTL_error, paramInt, false);
            }
          }
        });
      }
    }
  }
  
  public void loadChannelParticipants(final Integer paramInteger)
  {
    if ((this.loadingFullParticipants.contains(paramInteger)) || (this.loadedFullParticipants.contains(paramInteger))) {}
    for (;;)
    {
      return;
      this.loadingFullParticipants.add(paramInteger);
      TLRPC.TL_channels_getParticipants localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
      localTL_channels_getParticipants.channel = getInputChannel(paramInteger.intValue());
      localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
      localTL_channels_getParticipants.offset = 0;
      localTL_channels_getParticipants.limit = 32;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_getParticipants, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (paramAnonymousTL_error == null)
              {
                TLRPC.TL_channels_channelParticipants localTL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants)paramAnonymousTLObject;
                MessagesController.this.putUsers(localTL_channels_channelParticipants.users, false);
                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(localTL_channels_channelParticipants.users, null, true, true);
                MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChannelUsers(MessagesController.49.this.val$chat_id.intValue(), localTL_channels_channelParticipants.participants);
                MessagesController.this.loadedFullParticipants.add(MessagesController.49.this.val$chat_id);
              }
              MessagesController.this.loadingFullParticipants.remove(MessagesController.49.this.val$chat_id);
            }
          });
        }
      });
    }
  }
  
  public void loadChatInfo(int paramInt, CountDownLatch paramCountDownLatch, boolean paramBoolean)
  {
    MessagesStorage.getInstance(this.currentAccount).loadChatInfo(paramInt, paramCountDownLatch, paramBoolean, false);
  }
  
  public void loadCurrentState()
  {
    if (this.updatingState) {}
    for (;;)
    {
      return;
      this.updatingState = true;
      TLRPC.TL_updates_getState localTL_updates_getState = new TLRPC.TL_updates_getState();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_updates_getState, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          MessagesController.this.updatingState = false;
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.TL_updates_state)paramAnonymousTLObject;
            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(paramAnonymousTLObject.date);
            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(paramAnonymousTLObject.pts);
            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(paramAnonymousTLObject.seq);
            MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(paramAnonymousTLObject.qts);
            for (int i = 0; i < 3; i++) {
              MessagesController.this.processUpdatesQueue(i, 2);
            }
            MessagesStorage.getInstance(MessagesController.this.currentAccount).saveDiffParams(MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastQtsValue());
          }
          for (;;)
          {
            return;
            if (paramAnonymousTL_error.code != 401) {
              MessagesController.this.loadCurrentState();
            }
          }
        }
      });
    }
  }
  
  public void loadDialogPhotos(final int paramInt1, final int paramInt2, final long paramLong, boolean paramBoolean, int paramInt3)
  {
    if (paramBoolean) {
      MessagesStorage.getInstance(this.currentAccount).getDialogPhotos(paramInt1, paramInt2, paramLong, paramInt3);
    }
    for (;;)
    {
      return;
      Object localObject;
      if (paramInt1 > 0)
      {
        TLRPC.User localUser = getUser(Integer.valueOf(paramInt1));
        if (localUser != null)
        {
          localObject = new TLRPC.TL_photos_getUserPhotos();
          ((TLRPC.TL_photos_getUserPhotos)localObject).limit = paramInt2;
          ((TLRPC.TL_photos_getUserPhotos)localObject).offset = 0;
          ((TLRPC.TL_photos_getUserPhotos)localObject).max_id = ((int)paramLong);
          ((TLRPC.TL_photos_getUserPhotos)localObject).user_id = getInputUser(localUser);
          paramInt1 = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTL_error == null)
              {
                paramAnonymousTLObject = (TLRPC.photos_Photos)paramAnonymousTLObject;
                MessagesController.this.processLoadedUserPhotos(paramAnonymousTLObject, paramInt1, paramInt2, paramLong, false, this.val$classGuid);
              }
            }
          });
          ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(paramInt1, paramInt3);
        }
      }
      else if (paramInt1 < 0)
      {
        localObject = new TLRPC.TL_messages_search();
        ((TLRPC.TL_messages_search)localObject).filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
        ((TLRPC.TL_messages_search)localObject).limit = paramInt2;
        ((TLRPC.TL_messages_search)localObject).offset_id = ((int)paramLong);
        ((TLRPC.TL_messages_search)localObject).q = "";
        ((TLRPC.TL_messages_search)localObject).peer = getInputPeer(paramInt1);
        paramInt1 = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = (TLRPC.messages_Messages)paramAnonymousTLObject;
              TLRPC.TL_photos_photos localTL_photos_photos = new TLRPC.TL_photos_photos();
              localTL_photos_photos.count = paramAnonymousTLObject.count;
              localTL_photos_photos.users.addAll(paramAnonymousTLObject.users);
              int i = 0;
              if (i < paramAnonymousTLObject.messages.size())
              {
                paramAnonymousTL_error = (TLRPC.Message)paramAnonymousTLObject.messages.get(i);
                if ((paramAnonymousTL_error.action == null) || (paramAnonymousTL_error.action.photo == null)) {}
                for (;;)
                {
                  i++;
                  break;
                  localTL_photos_photos.photos.add(paramAnonymousTL_error.action.photo);
                }
              }
              MessagesController.this.processLoadedUserPhotos(localTL_photos_photos, paramInt1, paramInt2, paramLong, false, this.val$classGuid);
            }
          }
        });
        ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(paramInt1, paramInt3);
      }
    }
  }
  
  public void loadDialogs(int paramInt1, final int paramInt2, boolean paramBoolean)
  {
    if ((this.loadingDialogs) || (this.resetingDialogs)) {}
    Object localObject1;
    for (;;)
    {
      return;
      this.loadingDialogs = true;
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("load cacheOffset = " + paramInt1 + " count = " + paramInt2 + " cache = " + paramBoolean);
      }
      if (paramBoolean)
      {
        localObject1 = MessagesStorage.getInstance(this.currentAccount);
        if (paramInt1 == 0) {}
        for (paramInt1 = 0;; paramInt1 = this.nextDialogsCacheOffset)
        {
          ((MessagesStorage)localObject1).getDialogs(paramInt1, paramInt2);
          break;
        }
      }
      localObject1 = new TLRPC.TL_messages_getDialogs();
      ((TLRPC.TL_messages_getDialogs)localObject1).limit = paramInt2;
      ((TLRPC.TL_messages_getDialogs)localObject1).exclude_pinned = true;
      if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == -1) {
        break label427;
      }
      if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == Integer.MAX_VALUE)
      {
        this.dialogsEndReached = true;
        this.serverDialogsEndReached = true;
        this.loadingDialogs = false;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      }
      else
      {
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId;
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_date = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate;
        if (((TLRPC.TL_messages_getDialogs)localObject1).offset_id != 0) {
          break;
        }
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = new TLRPC.TL_inputPeerEmpty();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = (TLRPC.messages_Dialogs)paramAnonymousTLObject;
              MessagesController.this.processLoadedDialogs(paramAnonymousTLObject, null, 0, paramInt2, 0, false, false, false);
            }
          }
        });
      }
    }
    if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId != 0)
    {
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = new TLRPC.TL_inputPeerChannel();
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer.channel_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId;
    }
    for (;;)
    {
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer.access_hash = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess;
      break;
      if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId != 0)
      {
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = new TLRPC.TL_inputPeerUser();
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer.user_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId;
      }
      else
      {
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = new TLRPC.TL_inputPeerChat();
        ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer.chat_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId;
      }
    }
    label427:
    int i = 0;
    paramInt1 = this.dialogs.size() - 1;
    int j = i;
    Object localObject2;
    if (paramInt1 >= 0)
    {
      localObject2 = (TLRPC.TL_dialog)this.dialogs.get(paramInt1);
      if (((TLRPC.TL_dialog)localObject2).pinned) {}
      do
      {
        int k;
        do
        {
          paramInt1--;
          break;
          k = (int)((TLRPC.TL_dialog)localObject2).id;
          j = (int)(((TLRPC.TL_dialog)localObject2).id >> 32);
        } while ((k == 0) || (j == 1) || (((TLRPC.TL_dialog)localObject2).top_message <= 0));
        localObject2 = (MessageObject)this.dialogMessage.get(((TLRPC.TL_dialog)localObject2).id);
      } while ((localObject2 == null) || (((MessageObject)localObject2).getId() <= 0));
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_date = ((MessageObject)localObject2).messageOwner.date;
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_id = ((MessageObject)localObject2).messageOwner.id;
      if (((MessageObject)localObject2).messageOwner.to_id.channel_id == 0) {
        break label629;
      }
      paramInt1 = -((MessageObject)localObject2).messageOwner.to_id.channel_id;
    }
    for (;;)
    {
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = getInputPeer(paramInt1);
      j = 1;
      if (j != 0) {
        break;
      }
      ((TLRPC.TL_messages_getDialogs)localObject1).offset_peer = new TLRPC.TL_inputPeerEmpty();
      break;
      label629:
      if (((MessageObject)localObject2).messageOwner.to_id.chat_id != 0) {
        paramInt1 = -((MessageObject)localObject2).messageOwner.to_id.chat_id;
      } else {
        paramInt1 = ((MessageObject)localObject2).messageOwner.to_id.user_id;
      }
    }
  }
  
  public void loadFullChat(int paramInt1, final int paramInt2, boolean paramBoolean)
  {
    boolean bool = this.loadedFullChats.contains(Integer.valueOf(paramInt1));
    if ((this.loadingFullChats.contains(Integer.valueOf(paramInt1))) || ((!paramBoolean) && (bool))) {
      return;
    }
    this.loadingFullChats.add(Integer.valueOf(paramInt1));
    final long l = -paramInt1;
    final TLRPC.Chat localChat = getChat(Integer.valueOf(paramInt1));
    Object localObject1;
    Object localObject2;
    if (ChatObject.isChannel(localChat))
    {
      localObject1 = new TLRPC.TL_channels_getFullChannel();
      ((TLRPC.TL_channels_getFullChannel)localObject1).channel = getInputChannel(localChat);
      localObject2 = localObject1;
      if (localChat.megagroup)
      {
        if (bool) {
          break label166;
        }
        paramBoolean = true;
        label110:
        loadChannelAdmins(paramInt1, paramBoolean);
        localObject2 = localObject1;
      }
    }
    for (;;)
    {
      paramInt1 = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject2, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            final TLRPC.TL_messages_chatFull localTL_messages_chatFull = (TLRPC.TL_messages_chatFull)paramAnonymousTLObject;
            MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(localTL_messages_chatFull.users, localTL_messages_chatFull.chats, true, true);
            MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatInfo(localTL_messages_chatFull.full_chat, false);
            if (ChatObject.isChannel(localChat))
            {
              paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(l));
              paramAnonymousTLObject = paramAnonymousTL_error;
              if (paramAnonymousTL_error == null) {
                paramAnonymousTLObject = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, l));
              }
              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(l), Integer.valueOf(Math.max(localTL_messages_chatFull.full_chat.read_inbox_max_id, paramAnonymousTLObject.intValue())));
              if (paramAnonymousTLObject.intValue() == 0)
              {
                paramAnonymousTL_error = new ArrayList();
                paramAnonymousTLObject = new TLRPC.TL_updateReadChannelInbox();
                paramAnonymousTLObject.channel_id = paramInt2;
                paramAnonymousTLObject.max_id = localTL_messages_chatFull.full_chat.read_inbox_max_id;
                paramAnonymousTL_error.add(paramAnonymousTLObject);
                MessagesController.this.processUpdateArray(paramAnonymousTL_error, null, null, false);
              }
              paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(l));
              paramAnonymousTLObject = paramAnonymousTL_error;
              if (paramAnonymousTL_error == null) {
                paramAnonymousTLObject = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, l));
              }
              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(l), Integer.valueOf(Math.max(localTL_messages_chatFull.full_chat.read_outbox_max_id, paramAnonymousTLObject.intValue())));
              if (paramAnonymousTLObject.intValue() == 0)
              {
                paramAnonymousTLObject = new ArrayList();
                paramAnonymousTL_error = new TLRPC.TL_updateReadChannelOutbox();
                paramAnonymousTL_error.channel_id = paramInt2;
                paramAnonymousTL_error.max_id = localTL_messages_chatFull.full_chat.read_outbox_max_id;
                paramAnonymousTLObject.add(paramAnonymousTL_error);
                MessagesController.this.processUpdateArray(paramAnonymousTLObject, null, null, false);
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.applyDialogNotificationsSettings(-MessagesController.15.this.val$chat_id, localTL_messages_chatFull.full_chat.notify_settings);
                for (int i = 0; i < localTL_messages_chatFull.full_chat.bot_info.size(); i++)
                {
                  TLRPC.BotInfo localBotInfo = (TLRPC.BotInfo)localTL_messages_chatFull.full_chat.bot_info.get(i);
                  DataQuery.getInstance(MessagesController.this.currentAccount).putBotInfo(localBotInfo);
                }
                MessagesController.this.exportedChats.put(MessagesController.15.this.val$chat_id, localTL_messages_chatFull.full_chat.exported_invite);
                MessagesController.this.loadingFullChats.remove(Integer.valueOf(MessagesController.15.this.val$chat_id));
                MessagesController.this.loadedFullChats.add(Integer.valueOf(MessagesController.15.this.val$chat_id));
                MessagesController.this.putUsers(localTL_messages_chatFull.users, false);
                MessagesController.this.putChats(localTL_messages_chatFull.chats, false);
                if (localTL_messages_chatFull.full_chat.stickerset != null) {
                  DataQuery.getInstance(MessagesController.this.currentAccount).getGroupStickerSetById(localTL_messages_chatFull.full_chat.stickerset);
                }
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { localTL_messages_chatFull.full_chat, Integer.valueOf(MessagesController.15.this.val$classGuid), Boolean.valueOf(false), null });
              }
            });
          }
          for (;;)
          {
            return;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.checkChannelError(paramAnonymousTL_error.text, MessagesController.15.this.val$chat_id);
                MessagesController.this.loadingFullChats.remove(Integer.valueOf(MessagesController.15.this.val$chat_id));
              }
            });
          }
        }
      });
      if (paramInt2 == 0) {
        break;
      }
      ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(paramInt1, paramInt2);
      break;
      label166:
      paramBoolean = false;
      break label110;
      localObject1 = new TLRPC.TL_messages_getFullChat();
      ((TLRPC.TL_messages_getFullChat)localObject1).chat_id = paramInt1;
      if (this.dialogs_read_inbox_max.get(Long.valueOf(l)) != null)
      {
        localObject2 = localObject1;
        if (this.dialogs_read_outbox_max.get(Long.valueOf(l)) != null) {}
      }
      else
      {
        reloadDialogsReadValue(null, l);
        localObject2 = localObject1;
      }
    }
  }
  
  public void loadFullUser(final TLRPC.User paramUser, final int paramInt, boolean paramBoolean)
  {
    if ((paramUser == null) || (this.loadingFullUsers.contains(Integer.valueOf(paramUser.id))) || ((!paramBoolean) && (this.loadedFullUsers.contains(Integer.valueOf(paramUser.id))))) {}
    for (;;)
    {
      return;
      this.loadingFullUsers.add(Integer.valueOf(paramUser.id));
      TLRPC.TL_users_getFullUser localTL_users_getFullUser = new TLRPC.TL_users_getFullUser();
      localTL_users_getFullUser.id = getInputUser(paramUser);
      long l = paramUser.id;
      if ((this.dialogs_read_inbox_max.get(Long.valueOf(l)) == null) || (this.dialogs_read_outbox_max.get(Long.valueOf(l)) == null)) {
        reloadDialogsReadValue(null, l);
      }
      int i = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_users_getFullUser, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                TLRPC.TL_userFull localTL_userFull = (TLRPC.TL_userFull)paramAnonymousTLObject;
                MessagesController.this.applyDialogNotificationsSettings(MessagesController.16.this.val$user.id, localTL_userFull.notify_settings);
                if ((localTL_userFull.bot_info instanceof TLRPC.TL_botInfo)) {
                  DataQuery.getInstance(MessagesController.this.currentAccount).putBotInfo(localTL_userFull.bot_info);
                }
                MessagesController.this.fullUsers.put(MessagesController.16.this.val$user.id, localTL_userFull);
                MessagesController.this.loadingFullUsers.remove(Integer.valueOf(MessagesController.16.this.val$user.id));
                MessagesController.this.loadedFullUsers.add(Integer.valueOf(MessagesController.16.this.val$user.id));
                String str = MessagesController.16.this.val$user.first_name + MessagesController.16.this.val$user.last_name + MessagesController.16.this.val$user.username;
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localTL_userFull.user);
                MessagesController.this.putUsers(localArrayList, false);
                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(localArrayList, null, false, true);
                if ((str != null) && (!str.equals(localTL_userFull.user.first_name + localTL_userFull.user.last_name + localTL_userFull.user.username))) {
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
                }
                if ((localTL_userFull.bot_info instanceof TLRPC.TL_botInfo)) {
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoaded, new Object[] { localTL_userFull.bot_info, Integer.valueOf(MessagesController.16.this.val$classGuid) });
                }
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoaded, new Object[] { Integer.valueOf(MessagesController.16.this.val$user.id), localTL_userFull });
              }
            });
          }
          for (;;)
          {
            return;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.loadingFullUsers.remove(Integer.valueOf(MessagesController.16.this.val$user.id));
              }
            });
          }
        }
      });
      ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(i, paramInt);
    }
  }
  
  public void loadHintDialogs()
  {
    if ((!this.hintDialogs.isEmpty()) || (TextUtils.isEmpty(this.installReferer))) {}
    for (;;)
    {
      return;
      TLRPC.TL_help_getRecentMeUrls localTL_help_getRecentMeUrls = new TLRPC.TL_help_getRecentMeUrls();
      localTL_help_getRecentMeUrls.referer = this.installReferer;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_help_getRecentMeUrls, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                TLRPC.TL_help_recentMeUrls localTL_help_recentMeUrls = (TLRPC.TL_help_recentMeUrls)paramAnonymousTLObject;
                MessagesController.this.putUsers(localTL_help_recentMeUrls.users, false);
                MessagesController.this.putChats(localTL_help_recentMeUrls.chats, false);
                MessagesController.this.hintDialogs.clear();
                MessagesController.this.hintDialogs.addAll(localTL_help_recentMeUrls.urls);
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
              }
            });
          }
        }
      });
    }
  }
  
  public void loadMessages(long paramLong, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean2, int paramInt8)
  {
    loadMessages(paramLong, paramInt1, paramInt2, paramInt3, paramBoolean1, paramInt4, paramInt5, paramInt6, paramInt7, paramBoolean2, paramInt8, 0, 0, 0, false, 0);
  }
  
  public void loadMessages(long paramLong, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean1, int paramInt4, int paramInt5, int paramInt6, int paramInt7, boolean paramBoolean2, int paramInt8, int paramInt9, int paramInt10, int paramInt11, boolean paramBoolean3, int paramInt12)
  {
    loadMessagesInternal(paramLong, paramInt1, paramInt2, paramInt3, paramBoolean1, paramInt4, paramInt5, paramInt6, paramInt7, paramBoolean2, paramInt8, paramInt9, paramInt10, paramInt11, paramBoolean3, paramInt12, true);
  }
  
  public void loadPeerSettings(TLRPC.User paramUser, TLRPC.Chat paramChat)
  {
    if ((paramUser == null) && (paramChat == null)) {}
    final long l;
    label145:
    for (;;)
    {
      return;
      if (paramUser != null) {}
      for (l = paramUser.id;; l = -paramChat.id)
      {
        if (this.loadingPeerSettings.indexOfKey(l) >= 0) {
          break label145;
        }
        this.loadingPeerSettings.put(l, Boolean.valueOf(true));
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("request spam button for " + l);
        }
        if (this.notificationsPreferences.getInt("spam3_" + l, 0) != 1) {
          break label147;
        }
        if (!BuildVars.LOGS_ENABLED) {
          break;
        }
        FileLog.d("spam button already hidden for " + l);
        break;
      }
    }
    label147:
    if (this.notificationsPreferences.getBoolean("spam_" + l, false))
    {
      localObject = new TLRPC.TL_messages_hideReportSpam();
      if (paramUser != null) {
        ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(paramUser.id);
      }
      for (;;)
      {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.loadingPeerSettings.remove(MessagesController.21.this.val$dialogId);
                SharedPreferences.Editor localEditor = MessagesController.this.notificationsPreferences.edit();
                localEditor.remove("spam_" + MessagesController.21.this.val$dialogId);
                localEditor.putInt("spam3_" + MessagesController.21.this.val$dialogId, 1);
                localEditor.commit();
              }
            });
          }
        });
        break;
        if (paramChat != null) {
          ((TLRPC.TL_messages_hideReportSpam)localObject).peer = getInputPeer(-paramChat.id);
        }
      }
    }
    Object localObject = new TLRPC.TL_messages_getPeerSettings();
    if (paramUser != null) {
      ((TLRPC.TL_messages_getPeerSettings)localObject).peer = getInputPeer(paramUser.id);
    }
    for (;;)
    {
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.this.loadingPeerSettings.remove(MessagesController.22.this.val$dialogId);
              SharedPreferences.Editor localEditor;
              if (paramAnonymousTLObject != null)
              {
                TLRPC.TL_peerSettings localTL_peerSettings = (TLRPC.TL_peerSettings)paramAnonymousTLObject;
                localEditor = MessagesController.this.notificationsPreferences.edit();
                if (localTL_peerSettings.report_spam) {
                  break label133;
                }
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.d("don't show spam button for " + MessagesController.22.this.val$dialogId);
                }
                localEditor.putInt("spam3_" + MessagesController.22.this.val$dialogId, 1);
                localEditor.commit();
              }
              for (;;)
              {
                return;
                label133:
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.d("show spam button for " + MessagesController.22.this.val$dialogId);
                }
                localEditor.putInt("spam3_" + MessagesController.22.this.val$dialogId, 2);
                localEditor.commit();
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.peerSettingsDidLoaded, new Object[] { Long.valueOf(MessagesController.22.this.val$dialogId) });
              }
            }
          });
        }
      });
      break;
      if (paramChat != null) {
        ((TLRPC.TL_messages_getPeerSettings)localObject).peer = getInputPeer(-paramChat.id);
      }
    }
  }
  
  public void loadPinnedDialogs(final long paramLong, final ArrayList<Long> paramArrayList)
  {
    if (UserConfig.getInstance(this.currentAccount).pinnedDialogsLoaded) {}
    for (;;)
    {
      return;
      TLRPC.TL_messages_getPinnedDialogs localTL_messages_getPinnedDialogs = new TLRPC.TL_messages_getPinnedDialogs();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getPinnedDialogs, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null)
          {
            final TLRPC.TL_messages_peerDialogs localTL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs)paramAnonymousTLObject;
            final TLRPC.TL_messages_dialogs localTL_messages_dialogs = new TLRPC.TL_messages_dialogs();
            localTL_messages_dialogs.users.addAll(localTL_messages_peerDialogs.users);
            localTL_messages_dialogs.chats.addAll(localTL_messages_peerDialogs.chats);
            localTL_messages_dialogs.dialogs.addAll(localTL_messages_peerDialogs.dialogs);
            localTL_messages_dialogs.messages.addAll(localTL_messages_peerDialogs.messages);
            final LongSparseArray localLongSparseArray = new LongSparseArray();
            paramAnonymousTLObject = new SparseArray();
            SparseArray localSparseArray = new SparseArray();
            final ArrayList localArrayList = new ArrayList();
            for (int i = 0; i < localTL_messages_peerDialogs.users.size(); i++)
            {
              paramAnonymousTL_error = (TLRPC.User)localTL_messages_peerDialogs.users.get(i);
              paramAnonymousTLObject.put(paramAnonymousTL_error.id, paramAnonymousTL_error);
            }
            for (i = 0; i < localTL_messages_peerDialogs.chats.size(); i++)
            {
              paramAnonymousTL_error = (TLRPC.Chat)localTL_messages_peerDialogs.chats.get(i);
              localSparseArray.put(paramAnonymousTL_error.id, paramAnonymousTL_error);
            }
            i = 0;
            Object localObject;
            if (i < localTL_messages_peerDialogs.messages.size())
            {
              paramAnonymousTL_error = (TLRPC.Message)localTL_messages_peerDialogs.messages.get(i);
              if (paramAnonymousTL_error.to_id.channel_id != 0)
              {
                localObject = (TLRPC.Chat)localSparseArray.get(paramAnonymousTL_error.to_id.channel_id);
                if ((localObject == null) || (!((TLRPC.Chat)localObject).left)) {
                  break label306;
                }
              }
              for (;;)
              {
                i++;
                break;
                if (paramAnonymousTL_error.to_id.chat_id != 0)
                {
                  localObject = (TLRPC.Chat)localSparseArray.get(paramAnonymousTL_error.to_id.chat_id);
                  if ((localObject != null) && (((TLRPC.Chat)localObject).migrated_to != null)) {}
                }
                else
                {
                  label306:
                  paramAnonymousTL_error = new MessageObject(MessagesController.this.currentAccount, paramAnonymousTL_error, paramAnonymousTLObject, localSparseArray, false);
                  localLongSparseArray.put(paramAnonymousTL_error.getDialogId(), paramAnonymousTL_error);
                }
              }
            }
            i = 0;
            if (i < localTL_messages_peerDialogs.dialogs.size())
            {
              localObject = (TLRPC.TL_dialog)localTL_messages_peerDialogs.dialogs.get(i);
              if (((TLRPC.TL_dialog)localObject).id == 0L)
              {
                if (((TLRPC.TL_dialog)localObject).peer.user_id != 0) {
                  ((TLRPC.TL_dialog)localObject).id = ((TLRPC.TL_dialog)localObject).peer.user_id;
                }
              }
              else
              {
                label403:
                localArrayList.add(Long.valueOf(((TLRPC.TL_dialog)localObject).id));
                if (!DialogObject.isChannel((TLRPC.TL_dialog)localObject)) {
                  break label516;
                }
                paramAnonymousTLObject = (TLRPC.Chat)localSparseArray.get(-(int)((TLRPC.TL_dialog)localObject).id);
                if ((paramAnonymousTLObject == null) || (!paramAnonymousTLObject.left)) {
                  break label552;
                }
              }
              for (;;)
              {
                i++;
                break;
                if (((TLRPC.TL_dialog)localObject).peer.chat_id != 0)
                {
                  ((TLRPC.TL_dialog)localObject).id = (-((TLRPC.TL_dialog)localObject).peer.chat_id);
                  break label403;
                }
                if (((TLRPC.TL_dialog)localObject).peer.channel_id == 0) {
                  break label403;
                }
                ((TLRPC.TL_dialog)localObject).id = (-((TLRPC.TL_dialog)localObject).peer.channel_id);
                break label403;
                label516:
                if ((int)((TLRPC.TL_dialog)localObject).id < 0)
                {
                  paramAnonymousTLObject = (TLRPC.Chat)localSparseArray.get(-(int)((TLRPC.TL_dialog)localObject).id);
                  if ((paramAnonymousTLObject != null) && (paramAnonymousTLObject.migrated_to != null)) {}
                }
                else
                {
                  label552:
                  if (((TLRPC.TL_dialog)localObject).last_message_date == 0)
                  {
                    paramAnonymousTLObject = (MessageObject)localLongSparseArray.get(((TLRPC.TL_dialog)localObject).id);
                    if (paramAnonymousTLObject != null) {
                      ((TLRPC.TL_dialog)localObject).last_message_date = paramAnonymousTLObject.messageOwner.date;
                    }
                  }
                  paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject).id));
                  paramAnonymousTLObject = paramAnonymousTL_error;
                  if (paramAnonymousTL_error == null) {
                    paramAnonymousTLObject = Integer.valueOf(0);
                  }
                  MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject).id), Integer.valueOf(Math.max(paramAnonymousTLObject.intValue(), ((TLRPC.TL_dialog)localObject).read_inbox_max_id)));
                  paramAnonymousTL_error = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject).id));
                  paramAnonymousTLObject = paramAnonymousTL_error;
                  if (paramAnonymousTL_error == null) {
                    paramAnonymousTLObject = Integer.valueOf(0);
                  }
                  MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject).id), Integer.valueOf(Math.max(paramAnonymousTLObject.intValue(), ((TLRPC.TL_dialog)localObject).read_outbox_max_id)));
                }
              }
            }
            MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable()
            {
              public void run()
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.this.applyDialogsNotificationsSettings(MessagesController.118.1.this.val$res.dialogs);
                    int i = 0;
                    int j = 0;
                    int k = 0;
                    int m = 0;
                    LongSparseArray localLongSparseArray = new LongSparseArray();
                    ArrayList localArrayList1 = new ArrayList();
                    int n = 0;
                    while (n < MessagesController.this.dialogs.size())
                    {
                      localObject1 = (TLRPC.TL_dialog)MessagesController.this.dialogs.get(n);
                      if ((int)((TLRPC.TL_dialog)localObject1).id == 0) {
                        n++;
                      } else {
                        if (((TLRPC.TL_dialog)localObject1).pinned) {
                          break label203;
                        }
                      }
                    }
                    ArrayList localArrayList2 = new ArrayList();
                    if (MessagesController.118.this.val$order != null) {}
                    for (Object localObject1 = MessagesController.118.this.val$order;; localObject1 = MessagesController.118.1.this.val$newPinnedOrder)
                    {
                      if (((ArrayList)localObject1).size() < localArrayList1.size()) {
                        ((ArrayList)localObject1).add(Long.valueOf(0L));
                      }
                      while (localArrayList1.size() < ((ArrayList)localObject1).size()) {
                        localArrayList1.add(0, Long.valueOf(0L));
                      }
                      label203:
                      m = Math.max(((TLRPC.TL_dialog)localObject1).pinnedNum, m);
                      localLongSparseArray.put(((TLRPC.TL_dialog)localObject1).id, Integer.valueOf(((TLRPC.TL_dialog)localObject1).pinnedNum));
                      localArrayList1.add(Long.valueOf(((TLRPC.TL_dialog)localObject1).id));
                      ((TLRPC.TL_dialog)localObject1).pinned = false;
                      ((TLRPC.TL_dialog)localObject1).pinnedNum = 0;
                      i = 1;
                      break;
                    }
                    n = i;
                    if (!MessagesController.118.1.this.val$res.dialogs.isEmpty())
                    {
                      MessagesController.this.putUsers(MessagesController.118.1.this.val$res.users, false);
                      MessagesController.this.putChats(MessagesController.118.1.this.val$res.chats, false);
                      j = 0;
                      n = i;
                      i = k;
                      k = j;
                      j = i;
                      if (k < MessagesController.118.1.this.val$res.dialogs.size())
                      {
                        TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.118.1.this.val$res.dialogs.get(k);
                        Object localObject2;
                        if (MessagesController.118.this.val$newDialogId != 0L)
                        {
                          localObject2 = (Integer)localLongSparseArray.get(localTL_dialog.id);
                          if (localObject2 != null) {
                            localTL_dialog.pinnedNum = ((Integer)localObject2).intValue();
                          }
                          label435:
                          if (localTL_dialog.pinnedNum == 0) {
                            localTL_dialog.pinnedNum = (MessagesController.118.1.this.val$res.dialogs.size() - k + m);
                          }
                          localArrayList2.add(Long.valueOf(localTL_dialog.id));
                          localObject2 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(localTL_dialog.id);
                          if (localObject2 == null) {
                            break label684;
                          }
                          ((TLRPC.TL_dialog)localObject2).pinned = true;
                          ((TLRPC.TL_dialog)localObject2).pinnedNum = localTL_dialog.pinnedNum;
                          MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogPinned(localTL_dialog.id, localTL_dialog.pinnedNum);
                        }
                        for (;;)
                        {
                          n = 1;
                          k++;
                          break;
                          n = localArrayList1.indexOf(Long.valueOf(localTL_dialog.id));
                          j = ((ArrayList)localObject1).indexOf(Long.valueOf(localTL_dialog.id));
                          if ((n == -1) || (j == -1)) {
                            break label435;
                          }
                          if (n == j)
                          {
                            localObject2 = (Integer)localLongSparseArray.get(localTL_dialog.id);
                            if (localObject2 == null) {
                              break label435;
                            }
                            localTL_dialog.pinnedNum = ((Integer)localObject2).intValue();
                            break label435;
                          }
                          localObject2 = (Integer)localLongSparseArray.get(((Long)localArrayList1.get(j)).longValue());
                          if (localObject2 == null) {
                            break label435;
                          }
                          localTL_dialog.pinnedNum = ((Integer)localObject2).intValue();
                          break label435;
                          label684:
                          n = 1;
                          MessagesController.this.dialogs_dict.put(localTL_dialog.id, localTL_dialog);
                          localObject2 = (MessageObject)MessagesController.118.1.this.val$new_dialogMessage.get(localTL_dialog.id);
                          MessagesController.this.dialogMessage.put(localTL_dialog.id, localObject2);
                          i = n;
                          if (localObject2 != null)
                          {
                            i = n;
                            if (((MessageObject)localObject2).messageOwner.to_id.channel_id == 0)
                            {
                              MessagesController.this.dialogMessagesByIds.put(((MessageObject)localObject2).getId(), localObject2);
                              i = n;
                              if (((MessageObject)localObject2).messageOwner.random_id != 0L)
                              {
                                MessagesController.this.dialogMessagesByRandomIds.put(((MessageObject)localObject2).messageOwner.random_id, localObject2);
                                i = n;
                              }
                            }
                          }
                        }
                      }
                    }
                    if (n != 0)
                    {
                      if (j != 0)
                      {
                        MessagesController.this.dialogs.clear();
                        i = 0;
                        n = MessagesController.this.dialogs_dict.size();
                        while (i < n)
                        {
                          MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(i));
                          i++;
                        }
                      }
                      MessagesController.this.sortDialogs(null);
                      NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    }
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).unpinAllDialogsExceptNew(localArrayList2);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(MessagesController.118.1.this.val$toCache, true);
                    UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = true;
                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                  }
                });
              }
            });
          }
        }
      });
    }
  }
  
  protected void loadUnknownChannel(TLRPC.Chat paramChat, final long paramLong)
  {
    if ((!(paramChat instanceof TLRPC.TL_channel)) || (this.gettingUnknownChannels.indexOfKey(paramChat.id) >= 0)) {}
    for (;;)
    {
      return;
      if (paramChat.access_hash != 0L) {
        break;
      }
      if (paramLong != 0L) {
        MessagesStorage.getInstance(this.currentAccount).removePendingTask(paramLong);
      }
    }
    TLRPC.TL_inputPeerChannel localTL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
    localTL_inputPeerChannel.channel_id = paramChat.id;
    localTL_inputPeerChannel.access_hash = paramChat.access_hash;
    this.gettingUnknownChannels.put(paramChat.id, true);
    TLRPC.TL_messages_getPeerDialogs localTL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
    Object localObject1 = new TLRPC.TL_inputDialogPeer();
    ((TLRPC.TL_inputDialogPeer)localObject1).peer = localTL_inputPeerChannel;
    localTL_messages_getPeerDialogs.peers.add(localObject1);
    Object localObject2;
    if (paramLong == 0L) {
      localObject2 = null;
    }
    for (;;)
    {
      try
      {
        localObject1 = new org/telegram/tgnet/NativeByteBuffer;
        ((NativeByteBuffer)localObject1).<init>(paramChat.getObjectSize() + 4);
      }
      catch (Exception localException1)
      {
        try
        {
          ((NativeByteBuffer)localObject1).writeInt32(0);
          paramChat.serializeToStream((AbstractSerializedData)localObject1);
          paramLong = MessagesStorage.getInstance(this.currentAccount).createPendingTask((NativeByteBuffer)localObject1);
          ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getPeerDialogs, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTLObject != null)
              {
                paramAnonymousTLObject = (TLRPC.TL_messages_peerDialogs)paramAnonymousTLObject;
                if ((!paramAnonymousTLObject.dialogs.isEmpty()) && (!paramAnonymousTLObject.chats.isEmpty()))
                {
                  paramAnonymousTL_error = new TLRPC.TL_messages_dialogs();
                  paramAnonymousTL_error.dialogs.addAll(paramAnonymousTLObject.dialogs);
                  paramAnonymousTL_error.messages.addAll(paramAnonymousTLObject.messages);
                  paramAnonymousTL_error.users.addAll(paramAnonymousTLObject.users);
                  paramAnonymousTL_error.chats.addAll(paramAnonymousTLObject.chats);
                  MessagesController.this.processLoadedDialogs(paramAnonymousTL_error, null, 0, 1, 2, false, false, false);
                }
              }
              if (paramLong != 0L) {
                MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(paramLong);
              }
              MessagesController.this.gettingUnknownChannels.delete(this.val$channel.id);
            }
          });
        }
        catch (Exception localException2)
        {
          for (;;) {}
        }
        localException1 = localException1;
        localObject1 = localObject2;
      }
      FileLog.e(localException1);
    }
  }
  
  public void markChannelDialogMessageAsDeleted(ArrayList<Integer> paramArrayList, int paramInt)
  {
    MessageObject localMessageObject = (MessageObject)this.dialogMessage.get(-paramInt);
    if (localMessageObject != null) {}
    for (paramInt = 0;; paramInt++) {
      if (paramInt < paramArrayList.size())
      {
        Integer localInteger = (Integer)paramArrayList.get(paramInt);
        if (localMessageObject.getId() == localInteger.intValue()) {
          localMessageObject.deleted = true;
        }
      }
      else
      {
        return;
      }
    }
  }
  
  public void markDialogAsRead(final long paramLong, final int paramInt1, final int paramInt2, final int paramInt3, final boolean paramBoolean1, final int paramInt4, boolean paramBoolean2)
  {
    int i = (int)paramLong;
    int j = (int)(paramLong >> 32);
    if (i != 0) {
      if ((paramInt1 != 0) && (j != 1)) {}
    }
    label379:
    for (;;)
    {
      return;
      long l1 = paramInt1;
      long l2 = paramInt2;
      boolean bool1 = false;
      long l3 = l1;
      long l4 = l2;
      boolean bool2 = bool1;
      if (i < 0)
      {
        l3 = l1;
        l4 = l2;
        bool2 = bool1;
        if (ChatObject.isChannel(getChat(Integer.valueOf(-i))))
        {
          l3 = l1 | -i << 32;
          l4 = l2 | -i << 32;
          bool2 = true;
        }
      }
      Integer localInteger = (Integer)this.dialogs_read_inbox_max.get(Long.valueOf(paramLong));
      Object localObject = localInteger;
      if (localInteger == null) {
        localObject = Integer.valueOf(0);
      }
      this.dialogs_read_inbox_max.put(Long.valueOf(paramLong), Integer.valueOf(Math.max(((Integer)localObject).intValue(), paramInt1)));
      MessagesStorage.getInstance(this.currentAccount).processPendingRead(paramLong, l3, l4, paramInt3, bool2);
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              Object localObject = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(MessagesController.84.this.val$dialogId);
              if (localObject != null)
              {
                if ((MessagesController.84.this.val$countDiff == 0) || (MessagesController.84.this.val$maxPositiveId >= ((TLRPC.TL_dialog)localObject).top_message))
                {
                  ((TLRPC.TL_dialog)localObject).unread_count = 0;
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(256) });
                }
              }
              else
              {
                if (MessagesController.84.this.val$popup) {
                  break label244;
                }
                NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, MessagesController.84.this.val$dialogId, 0, MessagesController.84.this.val$maxPositiveId, false);
                localObject = new LongSparseArray(1);
                ((LongSparseArray)localObject).put(MessagesController.84.this.val$dialogId, Integer.valueOf(0));
                NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead((LongSparseArray)localObject);
              }
              for (;;)
              {
                return;
                ((TLRPC.TL_dialog)localObject).unread_count = Math.max(((TLRPC.TL_dialog)localObject).unread_count - MessagesController.84.this.val$countDiff, 0);
                if ((MessagesController.84.this.val$maxPositiveId == Integer.MIN_VALUE) || (((TLRPC.TL_dialog)localObject).unread_count <= ((TLRPC.TL_dialog)localObject).top_message - MessagesController.84.this.val$maxPositiveId)) {
                  break;
                }
                ((TLRPC.TL_dialog)localObject).unread_count = (((TLRPC.TL_dialog)localObject).top_message - MessagesController.84.this.val$maxPositiveId);
                break;
                label244:
                NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, MessagesController.84.this.val$dialogId, 0, MessagesController.84.this.val$maxPositiveId, true);
                localObject = new LongSparseArray(1);
                ((LongSparseArray)localObject).put(MessagesController.84.this.val$dialogId, Integer.valueOf(-1));
                NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead((LongSparseArray)localObject);
              }
            }
          });
        }
      });
      if (paramInt1 != Integer.MAX_VALUE) {
        paramInt2 = 1;
      }
      for (;;)
      {
        if (paramInt2 == 0) {
          break label379;
        }
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            MessagesController.ReadTask localReadTask1 = (MessagesController.ReadTask)MessagesController.this.readTasksMap.get(paramLong);
            MessagesController.ReadTask localReadTask2 = localReadTask1;
            if (localReadTask1 == null)
            {
              localReadTask1 = new MessagesController.ReadTask(MessagesController.this, null);
              localReadTask1.dialogId = paramLong;
              localReadTask1.sendRequestTime = (SystemClock.uptimeMillis() + 5000L);
              localReadTask2 = localReadTask1;
              if (!paramInt3)
              {
                MessagesController.this.readTasksMap.put(paramLong, localReadTask1);
                MessagesController.this.readTasks.add(localReadTask1);
                localReadTask2 = localReadTask1;
              }
            }
            localReadTask2.maxDate = paramInt1;
            localReadTask2.maxId = this.val$maxPositiveId;
            if (paramInt3) {
              MessagesController.this.completeReadTask(localReadTask2);
            }
          }
        });
        break;
        paramInt2 = 0;
        continue;
        if (paramInt3 == 0) {
          break;
        }
        i = 1;
        localObject = getEncryptedChat(Integer.valueOf(j));
        MessagesStorage.getInstance(this.currentAccount).processPendingRead(paramLong, paramInt1, paramInt2, paramInt3, false);
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
        {
          public void run()
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, MessagesController.85.this.val$dialogId, MessagesController.85.this.val$maxDate, 0, MessagesController.85.this.val$popup);
                Object localObject = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(MessagesController.85.this.val$dialogId);
                if (localObject != null) {
                  if ((MessagesController.85.this.val$countDiff != 0) && (MessagesController.85.this.val$maxNegativeId > ((TLRPC.TL_dialog)localObject).top_message)) {
                    break label170;
                  }
                }
                for (((TLRPC.TL_dialog)localObject).unread_count = 0;; ((TLRPC.TL_dialog)localObject).unread_count = (MessagesController.85.this.val$maxNegativeId - ((TLRPC.TL_dialog)localObject).top_message)) {
                  label170:
                  do
                  {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(256) });
                    localObject = new LongSparseArray(1);
                    ((LongSparseArray)localObject).put(MessagesController.85.this.val$dialogId, Integer.valueOf(0));
                    NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead((LongSparseArray)localObject);
                    return;
                    ((TLRPC.TL_dialog)localObject).unread_count = Math.max(((TLRPC.TL_dialog)localObject).unread_count - MessagesController.85.this.val$countDiff, 0);
                  } while ((MessagesController.85.this.val$maxNegativeId == Integer.MAX_VALUE) || (((TLRPC.TL_dialog)localObject).unread_count <= MessagesController.85.this.val$maxNegativeId - ((TLRPC.TL_dialog)localObject).top_message));
                }
              }
            });
          }
        });
        paramInt2 = i;
        if (((TLRPC.EncryptedChat)localObject).ttl > 0)
        {
          paramInt2 = Math.max(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime(), paramInt3);
          MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(((TLRPC.EncryptedChat)localObject).id, paramInt2, paramInt2, 0, null);
          paramInt2 = i;
        }
      }
    }
  }
  
  public void markDialogAsReadNow(final long paramLong)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.ReadTask localReadTask = (MessagesController.ReadTask)MessagesController.this.readTasksMap.get(paramLong);
        if (localReadTask == null) {}
        for (;;)
        {
          return;
          MessagesController.this.completeReadTask(localReadTask);
          MessagesController.this.readTasks.remove(localReadTask);
          MessagesController.this.readTasksMap.remove(paramLong);
        }
      }
    });
  }
  
  public void markMentionMessageAsRead(int paramInt1, int paramInt2, long paramLong)
  {
    MessagesStorage.getInstance(this.currentAccount).markMentionMessageAsRead(paramInt1, paramInt2, paramLong);
    Object localObject;
    if (paramInt2 != 0)
    {
      localObject = new TLRPC.TL_channels_readMessageContents();
      ((TLRPC.TL_channels_readMessageContents)localObject).channel = getInputChannel(paramInt2);
      if (((TLRPC.TL_channels_readMessageContents)localObject).channel != null) {}
    }
    for (;;)
    {
      return;
      ((TLRPC.TL_channels_readMessageContents)localObject).id.add(Integer.valueOf(paramInt1));
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
      continue;
      localObject = new TLRPC.TL_messages_readMessageContents();
      ((TLRPC.TL_messages_readMessageContents)localObject).id.add(Integer.valueOf(paramInt1));
      ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.TL_messages_affectedMessages)paramAnonymousTLObject;
            MessagesController.this.processNewDifferenceParams(-1, paramAnonymousTLObject.pts, -1, paramAnonymousTLObject.pts_count);
          }
        }
      });
    }
  }
  
  public void markMessageAsRead(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt1 == 0) || (paramInt3 <= 0)) {}
    for (;;)
    {
      return;
      int i = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
      MessagesStorage.getInstance(this.currentAccount).createTaskForMid(paramInt1, paramInt2, i, i, paramInt3, false);
      Object localObject;
      if (paramInt2 != 0)
      {
        localObject = new TLRPC.TL_channels_readMessageContents();
        ((TLRPC.TL_channels_readMessageContents)localObject).channel = getInputChannel(paramInt2);
        ((TLRPC.TL_channels_readMessageContents)localObject).id.add(Integer.valueOf(paramInt1));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
        });
      }
      else
      {
        localObject = new TLRPC.TL_messages_readMessageContents();
        ((TLRPC.TL_messages_readMessageContents)localObject).id.add(Integer.valueOf(paramInt1));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = (TLRPC.TL_messages_affectedMessages)paramAnonymousTLObject;
              MessagesController.this.processNewDifferenceParams(-1, paramAnonymousTLObject.pts, -1, paramAnonymousTLObject.pts_count);
            }
          }
        });
      }
    }
  }
  
  public void markMessageAsRead(long paramLong1, long paramLong2, int paramInt)
  {
    if ((paramLong2 == 0L) || (paramLong1 == 0L) || ((paramInt <= 0) && (paramInt != Integer.MIN_VALUE))) {}
    for (;;)
    {
      return;
      int i = (int)paramLong1;
      int j = (int)(paramLong1 >> 32);
      if (i == 0)
      {
        TLRPC.EncryptedChat localEncryptedChat = getEncryptedChat(Integer.valueOf(j));
        if (localEncryptedChat != null)
        {
          ArrayList localArrayList = new ArrayList();
          localArrayList.add(Long.valueOf(paramLong2));
          SecretChatHelper.getInstance(this.currentAccount).sendMessagesReadMessage(localEncryptedChat, localArrayList, null);
          if (paramInt > 0)
          {
            paramInt = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(localEncryptedChat.id, paramInt, paramInt, 0, localArrayList);
          }
        }
      }
    }
  }
  
  public void markMessageContentAsRead(MessageObject paramMessageObject)
  {
    Object localObject = new ArrayList();
    long l1 = paramMessageObject.getId();
    long l2 = l1;
    if (paramMessageObject.messageOwner.to_id.channel_id != 0) {
      l2 = l1 | paramMessageObject.messageOwner.to_id.channel_id << 32;
    }
    if (paramMessageObject.messageOwner.mentioned) {
      MessagesStorage.getInstance(this.currentAccount).markMentionMessageAsRead(paramMessageObject.getId(), paramMessageObject.messageOwner.to_id.channel_id, paramMessageObject.getDialogId());
    }
    ((ArrayList)localObject).add(Long.valueOf(l2));
    MessagesStorage.getInstance(this.currentAccount).markMessagesContentAsRead((ArrayList)localObject, 0);
    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, new Object[] { localObject });
    if (paramMessageObject.getId() < 0) {
      markMessageAsRead(paramMessageObject.getDialogId(), paramMessageObject.messageOwner.random_id, Integer.MIN_VALUE);
    }
    for (;;)
    {
      return;
      if (paramMessageObject.messageOwner.to_id.channel_id != 0)
      {
        localObject = new TLRPC.TL_channels_readMessageContents();
        ((TLRPC.TL_channels_readMessageContents)localObject).channel = getInputChannel(paramMessageObject.messageOwner.to_id.channel_id);
        if (((TLRPC.TL_channels_readMessageContents)localObject).channel != null)
        {
          ((TLRPC.TL_channels_readMessageContents)localObject).id.add(Integer.valueOf(paramMessageObject.getId()));
          ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
          });
        }
      }
      else
      {
        localObject = new TLRPC.TL_messages_readMessageContents();
        ((TLRPC.TL_messages_readMessageContents)localObject).id.add(Integer.valueOf(paramMessageObject.getId()));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if (paramAnonymousTL_error == null)
            {
              paramAnonymousTLObject = (TLRPC.TL_messages_affectedMessages)paramAnonymousTLObject;
              MessagesController.this.processNewDifferenceParams(-1, paramAnonymousTLObject.pts, -1, paramAnonymousTLObject.pts_count);
            }
          }
        });
      }
    }
  }
  
  public void openByUserName(String paramString, final BaseFragment paramBaseFragment, final int paramInt)
  {
    if ((paramString == null) || (paramBaseFragment == null)) {}
    for (;;)
    {
      return;
      TLObject localTLObject = getUserOrChat(paramString);
      TLRPC.User localUser = null;
      TLRPC.Chat localChat = null;
      Object localObject1;
      final Object localObject2;
      if ((localTLObject instanceof TLRPC.User))
      {
        localUser = (TLRPC.User)localTLObject;
        localObject1 = localChat;
        localObject2 = localUser;
        if (localUser.min)
        {
          localObject2 = null;
          localObject1 = localChat;
        }
      }
      for (;;)
      {
        if (localObject2 == null) {
          break label126;
        }
        openChatOrProfileWith((TLRPC.User)localObject2, null, paramBaseFragment, paramInt, false);
        break;
        localObject1 = localChat;
        localObject2 = localUser;
        if ((localTLObject instanceof TLRPC.Chat))
        {
          localChat = (TLRPC.Chat)localTLObject;
          localObject1 = localChat;
          localObject2 = localUser;
          if (localChat.min)
          {
            localObject1 = null;
            localObject2 = localUser;
          }
        }
      }
      label126:
      if (localObject1 != null)
      {
        openChatOrProfileWith(null, (TLRPC.Chat)localObject1, paramBaseFragment, 1, false);
      }
      else if (paramBaseFragment.getParentActivity() != null)
      {
        localObject2 = new AlertDialog[1];
        localObject2[0] = new AlertDialog(paramBaseFragment.getParentActivity(), 1);
        localObject1 = new TLRPC.TL_contacts_resolveUsername();
        ((TLRPC.TL_contacts_resolveUsername)localObject1).username = paramString;
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                try
                {
                  MessagesController.138.this.val$progressDialog[0].dismiss();
                  MessagesController.138.this.val$progressDialog[0] = null;
                  MessagesController.138.this.val$fragment.setVisibleDialog(null);
                  TLRPC.TL_contacts_resolvedPeer localTL_contacts_resolvedPeer;
                  if (paramAnonymousTL_error == null)
                  {
                    localTL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer)paramAnonymousTLObject;
                    MessagesController.this.putUsers(localTL_contacts_resolvedPeer.users, false);
                    MessagesController.this.putChats(localTL_contacts_resolvedPeer.chats, false);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(localTL_contacts_resolvedPeer.users, localTL_contacts_resolvedPeer.chats, false, true);
                    if (!localTL_contacts_resolvedPeer.chats.isEmpty()) {
                      MessagesController.openChatOrProfileWith(null, (TLRPC.Chat)localTL_contacts_resolvedPeer.chats.get(0), MessagesController.138.this.val$fragment, 1, false);
                    }
                  }
                  for (;;)
                  {
                    return;
                    if (!localTL_contacts_resolvedPeer.users.isEmpty())
                    {
                      MessagesController.openChatOrProfileWith((TLRPC.User)localTL_contacts_resolvedPeer.users.get(0), null, MessagesController.138.this.val$fragment, MessagesController.138.this.val$type, false);
                      continue;
                      if ((MessagesController.138.this.val$fragment != null) && (MessagesController.138.this.val$fragment.getParentActivity() != null)) {
                        try
                        {
                          Toast.makeText(MessagesController.138.this.val$fragment.getParentActivity(), LocaleController.getString("NoUsernameFound", NUM), 0).show();
                        }
                        catch (Exception localException1)
                        {
                          FileLog.e(localException1);
                        }
                      }
                    }
                  }
                }
                catch (Exception localException2)
                {
                  for (;;) {}
                }
              }
            });
          }
        }
        {
          public void run()
          {
            if (localObject2[0] == null) {}
            for (;;)
            {
              return;
              localObject2[0].setMessage(LocaleController.getString("Loading", NUM));
              localObject2[0].setCanceledOnTouchOutside(false);
              localObject2[0].setCancelable(false);
              localObject2[0].setButton(-2, LocaleController.getString("Cancel", NUM), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(MessagesController.139.this.val$reqId, true);
                  try
                  {
                    paramAnonymous2DialogInterface.dismiss();
                    return;
                  }
                  catch (Exception paramAnonymous2DialogInterface)
                  {
                    for (;;)
                    {
                      FileLog.e(paramAnonymous2DialogInterface);
                    }
                  }
                }
              });
              paramBaseFragment.showDialog(localObject2[0]);
            }
          }
        }, 500L);
      }
    }
  }
  
  public void performLogout(boolean paramBoolean)
  {
    this.notificationsPreferences.edit().clear().commit();
    this.emojiPreferences.edit().putLong("lastGifLoadTime", 0L).putLong("lastStickersLoadTime", 0L).putLong("lastStickersLoadTimeMask", 0L).putLong("lastStickersLoadTimeFavs", 0L).commit();
    this.mainPreferences.edit().remove("gifhint").commit();
    if (paramBoolean)
    {
      unregistedPush();
      TLRPC.TL_auth_logOut localTL_auth_logOut = new TLRPC.TL_auth_logOut();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_auth_logOut, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          ConnectionsManager.getInstance(MessagesController.this.currentAccount).cleanup();
        }
      });
    }
    for (;;)
    {
      UserConfig.getInstance(this.currentAccount).clearConfig();
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
      MessagesStorage.getInstance(this.currentAccount).cleanup(false);
      cleanup();
      ContactsController.getInstance(this.currentAccount).deleteUnknownAppAccounts();
      return;
      ConnectionsManager.getInstance(this.currentAccount).cleanup();
    }
  }
  
  public void pinChannelMessage(TLRPC.Chat paramChat, int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_channels_updatePinnedMessage localTL_channels_updatePinnedMessage = new TLRPC.TL_channels_updatePinnedMessage();
    localTL_channels_updatePinnedMessage.channel = getInputChannel(paramChat);
    localTL_channels_updatePinnedMessage.id = paramInt;
    if (!paramBoolean) {}
    for (paramBoolean = true;; paramBoolean = false)
    {
      localTL_channels_updatePinnedMessage.silent = paramBoolean;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_updatePinnedMessage, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            paramAnonymousTLObject = (TLRPC.Updates)paramAnonymousTLObject;
            MessagesController.this.processUpdates(paramAnonymousTLObject, false);
          }
        }
      });
      return;
    }
  }
  
  public boolean pinDialog(long paramLong1, boolean paramBoolean, TLRPC.InputPeer paramInputPeer, final long paramLong2)
  {
    int i = (int)paramLong1;
    TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)this.dialogs_dict.get(paramLong1);
    if ((localTL_dialog == null) || (localTL_dialog.pinned == paramBoolean))
    {
      if (localTL_dialog != null) {}
      for (paramBoolean = true;; paramBoolean = false) {
        return paramBoolean;
      }
    }
    localTL_dialog.pinned = paramBoolean;
    int j;
    int k;
    label61:
    Object localObject1;
    if (paramBoolean)
    {
      j = 0;
      k = 0;
      if (k < this.dialogs.size())
      {
        localObject1 = (TLRPC.TL_dialog)this.dialogs.get(k);
        if (((TLRPC.TL_dialog)localObject1).pinned) {
          break label227;
        }
      }
    }
    TLRPC.TL_messages_toggleDialogPin localTL_messages_toggleDialogPin;
    for (localTL_dialog.pinnedNum = (j + 1);; localTL_dialog.pinnedNum = 0)
    {
      sortDialogs(null);
      if ((!paramBoolean) && (this.dialogs.get(this.dialogs.size() - 1) == localTL_dialog)) {
        this.dialogs.remove(this.dialogs.size() - 1);
      }
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      if ((i == 0) || (paramLong2 == -1L)) {
        break label367;
      }
      localTL_messages_toggleDialogPin = new TLRPC.TL_messages_toggleDialogPin();
      localTL_messages_toggleDialogPin.pinned = paramBoolean;
      localObject1 = paramInputPeer;
      if (paramInputPeer == null) {
        localObject1 = getInputPeer(i);
      }
      if (!(localObject1 instanceof TLRPC.TL_inputPeerEmpty)) {
        break label254;
      }
      paramBoolean = false;
      break;
      label227:
      j = Math.max(((TLRPC.TL_dialog)localObject1).pinnedNum, j);
      k++;
      break label61;
    }
    label254:
    paramInputPeer = new TLRPC.TL_inputDialogPeer();
    paramInputPeer.peer = ((TLRPC.InputPeer)localObject1);
    localTL_messages_toggleDialogPin.peer = paramInputPeer;
    Object localObject2;
    if (paramLong2 == 0L) {
      localObject2 = null;
    }
    for (;;)
    {
      try
      {
        paramInputPeer = new org/telegram/tgnet/NativeByteBuffer;
        paramInputPeer.<init>(((TLRPC.InputPeer)localObject1).getObjectSize() + 16);
      }
      catch (Exception localException1)
      {
        try
        {
          paramInputPeer.writeInt32(1);
          paramInputPeer.writeInt64(paramLong1);
          paramInputPeer.writeBool(paramBoolean);
          ((TLRPC.InputPeer)localObject1).serializeToStream(paramInputPeer);
          paramLong2 = MessagesStorage.getInstance(this.currentAccount).createPendingTask(paramInputPeer);
          ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_toggleDialogPin, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramLong2 != 0L) {
                MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(paramLong2);
              }
            }
          });
          label367:
          MessagesStorage.getInstance(this.currentAccount).setDialogPinned(paramLong1, localTL_dialog.pinnedNum);
          paramBoolean = true;
        }
        catch (Exception localException2)
        {
          for (;;) {}
        }
        localException1 = localException1;
        paramInputPeer = (TLRPC.InputPeer)localObject2;
      }
      FileLog.e(localException1);
    }
  }
  
  public void processChatInfo(int paramInt, final TLRPC.ChatFull paramChatFull, final ArrayList<TLRPC.User> paramArrayList, final boolean paramBoolean1, boolean paramBoolean2, final boolean paramBoolean3, final MessageObject paramMessageObject)
  {
    if ((paramBoolean1) && (paramInt > 0) && (!paramBoolean3)) {
      loadFullChat(paramInt, 0, paramBoolean2);
    }
    if (paramChatFull != null) {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.this.putUsers(paramArrayList, paramBoolean1);
          if (paramChatFull.stickerset != null) {
            DataQuery.getInstance(MessagesController.this.currentAccount).getGroupStickerSetById(paramChatFull.stickerset);
          }
          NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { paramChatFull, Integer.valueOf(0), Boolean.valueOf(paramBoolean3), paramMessageObject });
        }
      });
    }
  }
  
  public void processDialogsUpdate(final TLRPC.messages_Dialogs parammessages_Dialogs, ArrayList<TLRPC.EncryptedChat> paramArrayList)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final LongSparseArray localLongSparseArray1 = new LongSparseArray();
        final LongSparseArray localLongSparseArray2 = new LongSparseArray();
        Object localObject1 = new SparseArray(parammessages_Dialogs.users.size());
        SparseArray localSparseArray = new SparseArray(parammessages_Dialogs.chats.size());
        final LongSparseArray localLongSparseArray3 = new LongSparseArray();
        Object localObject2;
        for (int i = 0; i < parammessages_Dialogs.users.size(); i++)
        {
          localObject2 = (TLRPC.User)parammessages_Dialogs.users.get(i);
          ((SparseArray)localObject1).put(((TLRPC.User)localObject2).id, localObject2);
        }
        for (i = 0; i < parammessages_Dialogs.chats.size(); i++)
        {
          localObject2 = (TLRPC.Chat)parammessages_Dialogs.chats.get(i);
          localSparseArray.put(((TLRPC.Chat)localObject2).id, localObject2);
        }
        i = 0;
        Object localObject3;
        if (i < parammessages_Dialogs.messages.size())
        {
          localObject2 = (TLRPC.Message)parammessages_Dialogs.messages.get(i);
          if (((TLRPC.Message)localObject2).to_id.channel_id != 0)
          {
            localObject3 = (TLRPC.Chat)localSparseArray.get(((TLRPC.Message)localObject2).to_id.channel_id);
            if ((localObject3 == null) || (!((TLRPC.Chat)localObject3).left)) {
              break label292;
            }
          }
          for (;;)
          {
            i++;
            break;
            if (((TLRPC.Message)localObject2).to_id.chat_id != 0)
            {
              localObject3 = (TLRPC.Chat)localSparseArray.get(((TLRPC.Message)localObject2).to_id.chat_id);
              if ((localObject3 != null) && (((TLRPC.Chat)localObject3).migrated_to != null)) {}
            }
            else
            {
              label292:
              localObject2 = new MessageObject(MessagesController.this.currentAccount, (TLRPC.Message)localObject2, (SparseArray)localObject1, localSparseArray, false);
              localLongSparseArray2.put(((MessageObject)localObject2).getDialogId(), localObject2);
            }
          }
        }
        i = 0;
        if (i < parammessages_Dialogs.dialogs.size())
        {
          localObject3 = (TLRPC.TL_dialog)parammessages_Dialogs.dialogs.get(i);
          if (((TLRPC.TL_dialog)localObject3).id == 0L)
          {
            if (((TLRPC.TL_dialog)localObject3).peer.user_id != 0) {
              ((TLRPC.TL_dialog)localObject3).id = ((TLRPC.TL_dialog)localObject3).peer.user_id;
            }
          }
          else
          {
            label398:
            if (!DialogObject.isChannel((TLRPC.TL_dialog)localObject3)) {
              break label497;
            }
            localObject1 = (TLRPC.Chat)localSparseArray.get(-(int)((TLRPC.TL_dialog)localObject3).id);
            if ((localObject1 == null) || (!((TLRPC.Chat)localObject1).left)) {
              break label533;
            }
          }
          for (;;)
          {
            i++;
            break;
            if (((TLRPC.TL_dialog)localObject3).peer.chat_id != 0)
            {
              ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.chat_id);
              break label398;
            }
            if (((TLRPC.TL_dialog)localObject3).peer.channel_id == 0) {
              break label398;
            }
            ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.channel_id);
            break label398;
            label497:
            if ((int)((TLRPC.TL_dialog)localObject3).id < 0)
            {
              localObject1 = (TLRPC.Chat)localSparseArray.get(-(int)((TLRPC.TL_dialog)localObject3).id);
              if ((localObject1 != null) && (((TLRPC.Chat)localObject1).migrated_to != null)) {}
            }
            else
            {
              label533:
              if (((TLRPC.TL_dialog)localObject3).last_message_date == 0)
              {
                localObject1 = (MessageObject)localLongSparseArray2.get(((TLRPC.TL_dialog)localObject3).id);
                if (localObject1 != null) {
                  ((TLRPC.TL_dialog)localObject3).last_message_date = ((MessageObject)localObject1).messageOwner.date;
                }
              }
              localLongSparseArray1.put(((TLRPC.TL_dialog)localObject3).id, localObject3);
              localLongSparseArray3.put(((TLRPC.TL_dialog)localObject3).id, Integer.valueOf(((TLRPC.TL_dialog)localObject3).unread_count));
              localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
              localObject1 = localObject2;
              if (localObject2 == null) {
                localObject1 = Integer.valueOf(0);
              }
              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_inbox_max_id)));
              localObject2 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
              localObject1 = localObject2;
              if (localObject2 == null) {
                localObject1 = Integer.valueOf(0);
              }
              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_outbox_max_id)));
            }
          }
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.putUsers(MessagesController.73.this.val$dialogsRes.users, true);
            MessagesController.this.putChats(MessagesController.73.this.val$dialogsRes.chats, true);
            int i = 0;
            if (i < localLongSparseArray1.size())
            {
              long l = localLongSparseArray1.keyAt(i);
              Object localObject1 = (TLRPC.TL_dialog)localLongSparseArray1.valueAt(i);
              Object localObject2 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(l);
              Object localObject3;
              if (localObject2 == null)
              {
                localObject3 = MessagesController.this;
                ((MessagesController)localObject3).nextDialogsCacheOffset += 1;
                MessagesController.this.dialogs_dict.put(l, localObject1);
                localObject1 = (MessageObject)localLongSparseArray2.get(((TLRPC.TL_dialog)localObject1).id);
                MessagesController.this.dialogMessage.put(l, localObject1);
                if ((localObject1 != null) && (((MessageObject)localObject1).messageOwner.to_id.channel_id == 0))
                {
                  MessagesController.this.dialogMessagesByIds.put(((MessageObject)localObject1).getId(), localObject1);
                  if (((MessageObject)localObject1).messageOwner.random_id != 0L) {
                    MessagesController.this.dialogMessagesByRandomIds.put(((MessageObject)localObject1).messageOwner.random_id, localObject1);
                  }
                }
              }
              for (;;)
              {
                i++;
                break;
                ((TLRPC.TL_dialog)localObject2).unread_count = ((TLRPC.TL_dialog)localObject1).unread_count;
                if (((TLRPC.TL_dialog)localObject2).unread_mentions_count != ((TLRPC.TL_dialog)localObject1).unread_mentions_count)
                {
                  ((TLRPC.TL_dialog)localObject2).unread_mentions_count = ((TLRPC.TL_dialog)localObject1).unread_mentions_count;
                  if (MessagesController.this.createdDialogMainThreadIds.contains(Long.valueOf(((TLRPC.TL_dialog)localObject2).id))) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, new Object[] { Long.valueOf(((TLRPC.TL_dialog)localObject2).id), Integer.valueOf(((TLRPC.TL_dialog)localObject2).unread_mentions_count) });
                  }
                }
                localObject3 = (MessageObject)MessagesController.this.dialogMessage.get(l);
                if ((localObject3 == null) || (((TLRPC.TL_dialog)localObject2).top_message > 0))
                {
                  if (((localObject3 != null) && (((MessageObject)localObject3).deleted)) || (((TLRPC.TL_dialog)localObject1).top_message > ((TLRPC.TL_dialog)localObject2).top_message))
                  {
                    MessagesController.this.dialogs_dict.put(l, localObject1);
                    localObject2 = (MessageObject)localLongSparseArray2.get(((TLRPC.TL_dialog)localObject1).id);
                    MessagesController.this.dialogMessage.put(l, localObject2);
                    if ((localObject2 != null) && (((MessageObject)localObject2).messageOwner.to_id.channel_id == 0))
                    {
                      MessagesController.this.dialogMessagesByIds.put(((MessageObject)localObject2).getId(), localObject2);
                      if (((MessageObject)localObject2).messageOwner.random_id != 0L) {
                        MessagesController.this.dialogMessagesByRandomIds.put(((MessageObject)localObject2).messageOwner.random_id, localObject2);
                      }
                    }
                    if (localObject3 != null)
                    {
                      MessagesController.this.dialogMessagesByIds.remove(((MessageObject)localObject3).getId());
                      if (((MessageObject)localObject3).messageOwner.random_id != 0L) {
                        MessagesController.this.dialogMessagesByRandomIds.remove(((MessageObject)localObject3).messageOwner.random_id);
                      }
                    }
                    if (localObject2 == null) {
                      MessagesController.this.checkLastDialogMessage((TLRPC.TL_dialog)localObject1, null, 0L);
                    }
                  }
                }
                else
                {
                  localObject2 = (MessageObject)localLongSparseArray2.get(((TLRPC.TL_dialog)localObject1).id);
                  if ((((MessageObject)localObject3).deleted) || (localObject2 == null) || (((MessageObject)localObject2).messageOwner.date > ((MessageObject)localObject3).messageOwner.date))
                  {
                    MessagesController.this.dialogs_dict.put(l, localObject1);
                    MessagesController.this.dialogMessage.put(l, localObject2);
                    if ((localObject2 != null) && (((MessageObject)localObject2).messageOwner.to_id.channel_id == 0))
                    {
                      MessagesController.this.dialogMessagesByIds.put(((MessageObject)localObject2).getId(), localObject2);
                      if (((MessageObject)localObject2).messageOwner.random_id != 0L) {
                        MessagesController.this.dialogMessagesByRandomIds.put(((MessageObject)localObject2).messageOwner.random_id, localObject2);
                      }
                    }
                    MessagesController.this.dialogMessagesByIds.remove(((MessageObject)localObject3).getId());
                    if (((MessageObject)localObject3).messageOwner.random_id != 0L) {
                      MessagesController.this.dialogMessagesByRandomIds.remove(((MessageObject)localObject3).messageOwner.random_id);
                    }
                  }
                }
              }
            }
            MessagesController.this.dialogs.clear();
            i = 0;
            int j = MessagesController.this.dialogs_dict.size();
            while (i < j)
            {
              MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(i));
              i++;
            }
            MessagesController.this.sortDialogs(null);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(localLongSparseArray3);
          }
        });
      }
    });
  }
  
  public void processDialogsUpdateRead(final LongSparseArray<Integer> paramLongSparseArray1, final LongSparseArray<Integer> paramLongSparseArray2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        int i;
        long l;
        TLRPC.TL_dialog localTL_dialog;
        if (paramLongSparseArray1 != null) {
          for (i = 0; i < paramLongSparseArray1.size(); i++)
          {
            l = paramLongSparseArray1.keyAt(i);
            localTL_dialog = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(l);
            if (localTL_dialog != null) {
              localTL_dialog.unread_count = ((Integer)paramLongSparseArray1.valueAt(i)).intValue();
            }
          }
        }
        if (paramLongSparseArray2 != null) {
          for (i = 0; i < paramLongSparseArray2.size(); i++)
          {
            l = paramLongSparseArray2.keyAt(i);
            localTL_dialog = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(l);
            if (localTL_dialog != null)
            {
              localTL_dialog.unread_mentions_count = ((Integer)paramLongSparseArray2.valueAt(i)).intValue();
              if (MessagesController.this.createdDialogMainThreadIds.contains(Long.valueOf(localTL_dialog.id))) {
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, new Object[] { Long.valueOf(localTL_dialog.id), Integer.valueOf(localTL_dialog.unread_mentions_count) });
              }
            }
          }
        }
        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(256) });
        if (paramLongSparseArray1 != null) {
          NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(paramLongSparseArray1);
        }
      }
    });
  }
  
  public void processLoadedBlockedUsers(final ArrayList<Integer> paramArrayList, final ArrayList<TLRPC.User> paramArrayList1, final boolean paramBoolean)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (paramArrayList1 != null) {
          MessagesController.this.putUsers(paramArrayList1, paramBoolean);
        }
        MessagesController.this.loadingBlockedUsers = false;
        if ((paramArrayList.isEmpty()) && (paramBoolean) && (!UserConfig.getInstance(MessagesController.this.currentAccount).blockedUsersLoaded)) {
          MessagesController.this.getBlockedUsers(false);
        }
        for (;;)
        {
          return;
          if (!paramBoolean)
          {
            UserConfig.getInstance(MessagesController.this.currentAccount).blockedUsersLoaded = true;
            UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
          }
          MessagesController.this.blockedUsers = paramArrayList;
          NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
        }
      }
    });
  }
  
  public void processLoadedChannelAdmins(final ArrayList<Integer> paramArrayList, final int paramInt, final boolean paramBoolean)
  {
    Collections.sort(paramArrayList);
    if (!paramBoolean) {
      MessagesStorage.getInstance(this.currentAccount).putChannelAdmins(paramInt, paramArrayList);
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        MessagesController.this.loadingChannelAdmins.delete(paramInt);
        MessagesController.this.channelAdmins.put(paramInt, paramArrayList);
        if (paramBoolean) {
          MessagesController.this.loadChannelAdmins(paramInt, false);
        }
      }
    });
  }
  
  public void processLoadedDeleteTask(final int paramInt1, final ArrayList<Integer> paramArrayList, int paramInt2)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        MessagesController.access$4002(MessagesController.this, false);
        if (paramArrayList != null)
        {
          MessagesController.access$4102(MessagesController.this, paramInt1);
          MessagesController.access$3902(MessagesController.this, paramArrayList);
          if (MessagesController.this.currentDeleteTaskRunnable != null)
          {
            Utilities.stageQueue.cancelRunnable(MessagesController.this.currentDeleteTaskRunnable);
            MessagesController.access$4302(MessagesController.this, null);
          }
          if (!MessagesController.this.checkDeletingTask(false))
          {
            MessagesController.access$4302(MessagesController.this, new Runnable()
            {
              public void run()
              {
                MessagesController.this.checkDeletingTask(true);
              }
            });
            int i = ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime();
            Utilities.stageQueue.postRunnable(MessagesController.this.currentDeleteTaskRunnable, Math.abs(i - MessagesController.this.currentDeletingTaskTime) * 1000L);
          }
        }
        for (;;)
        {
          return;
          MessagesController.access$4102(MessagesController.this, 0);
          MessagesController.access$3902(MessagesController.this, null);
        }
      }
    });
  }
  
  public void processLoadedDialogs(final TLRPC.messages_Dialogs parammessages_Dialogs, final ArrayList<TLRPC.EncryptedChat> paramArrayList, final int paramInt1, final int paramInt2, final int paramInt3, final boolean paramBoolean1, final boolean paramBoolean2, final boolean paramBoolean3)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (!MessagesController.this.firstGettingTask)
        {
          MessagesController.this.getNewDeleteTask(null, 0);
          MessagesController.this.firstGettingTask = true;
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("loaded loadType " + paramInt3 + " count " + parammessages_Dialogs.dialogs.size());
        }
        if ((paramInt3 == 1) && (parammessages_Dialogs.dialogs.size() == 0)) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.this.putUsers(MessagesController.69.this.val$dialogsRes.users, true);
              MessagesController.this.loadingDialogs = false;
              if (MessagesController.69.this.val$resetEnd)
              {
                MessagesController.this.dialogsEndReached = false;
                MessagesController.this.serverDialogsEndReached = false;
              }
              for (;;)
              {
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                return;
                if (UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == Integer.MAX_VALUE)
                {
                  MessagesController.this.dialogsEndReached = true;
                  MessagesController.this.serverDialogsEndReached = true;
                }
                else
                {
                  MessagesController.this.loadDialogs(0, MessagesController.69.this.val$count, false);
                }
              }
            }
          });
        }
        for (;;)
        {
          return;
          final LongSparseArray localLongSparseArray1 = new LongSparseArray();
          final LongSparseArray localLongSparseArray2 = new LongSparseArray();
          SparseArray localSparseArray1 = new SparseArray();
          final SparseArray localSparseArray2 = new SparseArray();
          Object localObject1;
          for (int i = 0; i < parammessages_Dialogs.users.size(); i++)
          {
            localObject1 = (TLRPC.User)parammessages_Dialogs.users.get(i);
            localSparseArray1.put(((TLRPC.User)localObject1).id, localObject1);
          }
          for (i = 0; i < parammessages_Dialogs.chats.size(); i++)
          {
            localObject1 = (TLRPC.Chat)parammessages_Dialogs.chats.get(i);
            localSparseArray2.put(((TLRPC.Chat)localObject1).id, localObject1);
          }
          if (paramInt3 == 1) {
            MessagesController.this.nextDialogsCacheOffset = (paramInt1 + paramInt2);
          }
          Object localObject2 = null;
          i = 0;
          Object localObject3;
          if (i < parammessages_Dialogs.messages.size())
          {
            localObject3 = (TLRPC.Message)parammessages_Dialogs.messages.get(i);
            if (localObject2 != null)
            {
              localObject1 = localObject2;
              if (((TLRPC.Message)localObject3).date >= ((TLRPC.Message)localObject2).date) {}
            }
            else
            {
              localObject1 = localObject3;
            }
            if (((TLRPC.Message)localObject3).to_id.channel_id != 0)
            {
              localObject2 = (TLRPC.Chat)localSparseArray2.get(((TLRPC.Message)localObject3).to_id.channel_id);
              if ((localObject2 == null) || (!((TLRPC.Chat)localObject2).left)) {}
            }
            for (;;)
            {
              i++;
              localObject2 = localObject1;
              break;
              if ((localObject2 != null) && (((TLRPC.Chat)localObject2).megagroup)) {
                ((TLRPC.Message)localObject3).flags |= 0x80000000;
              }
              do
              {
                do
                {
                  localObject2 = new MessageObject(MessagesController.this.currentAccount, (TLRPC.Message)localObject3, localSparseArray1, localSparseArray2, false);
                  localLongSparseArray2.put(((MessageObject)localObject2).getDialogId(), localObject2);
                  break;
                } while (((TLRPC.Message)localObject3).to_id.chat_id == 0);
                localObject2 = (TLRPC.Chat)localSparseArray2.get(((TLRPC.Message)localObject3).to_id.chat_id);
              } while ((localObject2 == null) || (((TLRPC.Chat)localObject2).migrated_to == null));
            }
          }
          label761:
          final ArrayList localArrayList;
          int j;
          if ((!paramBoolean3) && (!paramBoolean2) && (UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId != -1) && (paramInt3 == 0))
          {
            if ((localObject2 == null) || (((TLRPC.Message)localObject2).id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId)) {
              break label1170;
            }
            localObject1 = UserConfig.getInstance(MessagesController.this.currentAccount);
            ((UserConfig)localObject1).totalDialogsLoadCount += parammessages_Dialogs.dialogs.size();
            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = ((TLRPC.Message)localObject2).id;
            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = ((TLRPC.Message)localObject2).date;
            if (((TLRPC.Message)localObject2).to_id.channel_id == 0) {
              break label884;
            }
            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = ((TLRPC.Message)localObject2).to_id.channel_id;
            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = 0;
            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = 0;
            i = 0;
            if (i < parammessages_Dialogs.chats.size())
            {
              localObject1 = (TLRPC.Chat)parammessages_Dialogs.chats.get(i);
              if (((TLRPC.Chat)localObject1).id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId) {
                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = ((TLRPC.Chat)localObject1).access_hash;
              }
            }
            else
            {
              UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
            }
          }
          else
          {
            localArrayList = new ArrayList();
            j = 0;
            label787:
            if (j >= parammessages_Dialogs.dialogs.size()) {
              break label1629;
            }
            localObject3 = (TLRPC.TL_dialog)parammessages_Dialogs.dialogs.get(j);
            if ((((TLRPC.TL_dialog)localObject3).id == 0L) && (((TLRPC.TL_dialog)localObject3).peer != null))
            {
              if (((TLRPC.TL_dialog)localObject3).peer.user_id == 0) {
                break label1188;
              }
              ((TLRPC.TL_dialog)localObject3).id = ((TLRPC.TL_dialog)localObject3).peer.user_id;
            }
            label862:
            if (((TLRPC.TL_dialog)localObject3).id != 0L) {
              break label1246;
            }
          }
          for (;;)
          {
            j++;
            break label787;
            i++;
            break;
            label884:
            if (((TLRPC.Message)localObject2).to_id.chat_id != 0)
            {
              UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = ((TLRPC.Message)localObject2).to_id.chat_id;
              UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = 0;
              UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = 0;
              for (i = 0;; i++)
              {
                if (i >= parammessages_Dialogs.chats.size()) {
                  break label1025;
                }
                localObject1 = (TLRPC.Chat)parammessages_Dialogs.chats.get(i);
                if (((TLRPC.Chat)localObject1).id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId)
                {
                  UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = ((TLRPC.Chat)localObject1).access_hash;
                  break;
                }
              }
              label1025:
              break label761;
            }
            if (((TLRPC.Message)localObject2).to_id.user_id == 0) {
              break label761;
            }
            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = ((TLRPC.Message)localObject2).to_id.user_id;
            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = 0;
            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = 0;
            for (i = 0;; i++)
            {
              if (i >= parammessages_Dialogs.users.size()) {
                break label1168;
              }
              localObject1 = (TLRPC.User)parammessages_Dialogs.users.get(i);
              if (((TLRPC.User)localObject1).id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId)
              {
                UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = ((TLRPC.User)localObject1).access_hash;
                break;
              }
            }
            label1168:
            break label761;
            label1170:
            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = Integer.MAX_VALUE;
            break label761;
            label1188:
            if (((TLRPC.TL_dialog)localObject3).peer.chat_id != 0)
            {
              ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.chat_id);
              break label862;
            }
            if (((TLRPC.TL_dialog)localObject3).peer.channel_id == 0) {
              break label862;
            }
            ((TLRPC.TL_dialog)localObject3).id = (-((TLRPC.TL_dialog)localObject3).peer.channel_id);
            break label862;
            label1246:
            if (((TLRPC.TL_dialog)localObject3).last_message_date == 0)
            {
              localObject1 = (MessageObject)localLongSparseArray2.get(((TLRPC.TL_dialog)localObject3).id);
              if (localObject1 != null) {
                ((TLRPC.TL_dialog)localObject3).last_message_date = ((MessageObject)localObject1).messageOwner.date;
              }
            }
            i = 1;
            int k = 1;
            int m = 1;
            if (DialogObject.isChannel((TLRPC.TL_dialog)localObject3))
            {
              localObject1 = (TLRPC.Chat)localSparseArray2.get(-(int)((TLRPC.TL_dialog)localObject3).id);
              if (localObject1 != null)
              {
                i = m;
                if (!((TLRPC.Chat)localObject1).megagroup) {
                  i = 0;
                }
                if (((TLRPC.Chat)localObject1).left) {}
              }
              else
              {
                MessagesController.this.channelsPts.put(-(int)((TLRPC.TL_dialog)localObject3).id, ((TLRPC.TL_dialog)localObject3).pts);
              }
            }
            else
            {
              do
              {
                do
                {
                  do
                  {
                    localLongSparseArray1.put(((TLRPC.TL_dialog)localObject3).id, localObject3);
                    if ((i != 0) && (paramInt3 == 1) && ((((TLRPC.TL_dialog)localObject3).read_outbox_max_id == 0) || (((TLRPC.TL_dialog)localObject3).read_inbox_max_id == 0)) && (((TLRPC.TL_dialog)localObject3).top_message != 0)) {
                      localArrayList.add(localObject3);
                    }
                    localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
                    localObject1 = localObject2;
                    if (localObject2 == null) {
                      localObject1 = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_inbox_max_id)));
                    localObject2 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.TL_dialog)localObject3).id));
                    localObject1 = localObject2;
                    if (localObject2 == null) {
                      localObject1 = Integer.valueOf(0);
                    }
                    MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.TL_dialog)localObject3).id), Integer.valueOf(Math.max(((Integer)localObject1).intValue(), ((TLRPC.TL_dialog)localObject3).read_outbox_max_id)));
                    break;
                    i = k;
                  } while ((int)((TLRPC.TL_dialog)localObject3).id >= 0);
                  localObject1 = (TLRPC.Chat)localSparseArray2.get(-(int)((TLRPC.TL_dialog)localObject3).id);
                  i = k;
                } while (localObject1 == null);
                i = k;
              } while (((TLRPC.Chat)localObject1).migrated_to == null);
            }
          }
          label1629:
          if (paramInt3 != 1)
          {
            ImageLoader.saveMessagesThumbs(parammessages_Dialogs.messages);
            i = 0;
            while (i < parammessages_Dialogs.messages.size())
            {
              TLRPC.Message localMessage = (TLRPC.Message)parammessages_Dialogs.messages.get(i);
              if ((localMessage.action instanceof TLRPC.TL_messageActionChatDeleteUser))
              {
                localObject1 = (TLRPC.User)localSparseArray1.get(localMessage.action.user_id);
                if ((localObject1 != null) && (((TLRPC.User)localObject1).bot))
                {
                  localMessage.reply_markup = new TLRPC.TL_replyKeyboardHide();
                  localMessage.flags |= 0x40;
                }
              }
              if (((localMessage.action instanceof TLRPC.TL_messageActionChatMigrateTo)) || ((localMessage.action instanceof TLRPC.TL_messageActionChannelCreate)))
              {
                localMessage.unread = false;
                localMessage.media_unread = false;
                i++;
              }
              else
              {
                if (localMessage.out)
                {
                  localObject1 = MessagesController.this.dialogs_read_outbox_max;
                  label1805:
                  localObject3 = (Integer)((ConcurrentHashMap)localObject1).get(Long.valueOf(localMessage.dialog_id));
                  localObject2 = localObject3;
                  if (localObject3 == null)
                  {
                    localObject2 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(localMessage.out, localMessage.dialog_id));
                    ((ConcurrentHashMap)localObject1).put(Long.valueOf(localMessage.dialog_id), localObject2);
                  }
                  if (((Integer)localObject2).intValue() >= localMessage.id) {
                    break label1914;
                  }
                }
                label1914:
                for (boolean bool = true;; bool = false)
                {
                  localMessage.unread = bool;
                  break;
                  localObject1 = MessagesController.this.dialogs_read_inbox_max;
                  break label1805;
                }
              }
            }
            MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(parammessages_Dialogs, false);
          }
          if (paramInt3 == 2)
          {
            localObject1 = (TLRPC.Chat)parammessages_Dialogs.chats.get(0);
            MessagesController.this.getChannelDifference(((TLRPC.Chat)localObject1).id);
            MessagesController.this.checkChannelInviter(((TLRPC.Chat)localObject1).id);
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (MessagesController.69.this.val$loadType != 1)
              {
                MessagesController.this.applyDialogsNotificationsSettings(MessagesController.69.this.val$dialogsRes.dialogs);
                if (!UserConfig.getInstance(MessagesController.this.currentAccount).draftsLoaded) {
                  DataQuery.getInstance(MessagesController.this.currentAccount).loadDrafts();
                }
              }
              Object localObject1 = MessagesController.this;
              Object localObject2 = MessagesController.69.this.val$dialogsRes.users;
              if (MessagesController.69.this.val$loadType == 1)
              {
                bool = true;
                ((MessagesController)localObject1).putUsers((ArrayList)localObject2, bool);
                localObject2 = MessagesController.this;
                localObject1 = MessagesController.69.this.val$dialogsRes.chats;
                if (MessagesController.69.this.val$loadType != 1) {
                  break label246;
                }
              }
              label246:
              for (boolean bool = true;; bool = false)
              {
                ((MessagesController)localObject2).putChats((ArrayList)localObject1, bool);
                if (MessagesController.69.this.val$encChats == null) {
                  break label251;
                }
                for (i = 0; i < MessagesController.69.this.val$encChats.size(); i++)
                {
                  localObject2 = (TLRPC.EncryptedChat)MessagesController.69.this.val$encChats.get(i);
                  if (((localObject2 instanceof TLRPC.TL_encryptedChat)) && (AndroidUtilities.getMyLayerVersion(((TLRPC.EncryptedChat)localObject2).layer) < 73)) {
                    SecretChatHelper.getInstance(MessagesController.this.currentAccount).sendNotifyLayerMessage((TLRPC.EncryptedChat)localObject2, null);
                  }
                  MessagesController.this.putEncryptedChat((TLRPC.EncryptedChat)localObject2, true);
                }
                bool = false;
                break;
              }
              label251:
              if (!MessagesController.69.this.val$migrate) {
                MessagesController.this.loadingDialogs = false;
              }
              int i = 0;
              int k;
              label340:
              long l;
              if ((MessagesController.69.this.val$migrate) && (!MessagesController.this.dialogs.isEmpty()))
              {
                j = ((TLRPC.TL_dialog)MessagesController.this.dialogs.get(MessagesController.this.dialogs.size() - 1)).last_message_date;
                k = 0;
                if (k >= localLongSparseArray1.size()) {
                  break label1135;
                }
                l = localLongSparseArray1.keyAt(k);
                localObject1 = (TLRPC.TL_dialog)localLongSparseArray1.valueAt(k);
                if ((!MessagesController.69.this.val$migrate) || (j == 0) || (((TLRPC.TL_dialog)localObject1).last_message_date >= j)) {
                  break label420;
                }
                m = i;
              }
              for (;;)
              {
                k++;
                i = m;
                break label340;
                j = 0;
                break;
                label420:
                Object localObject3 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(l);
                if ((MessagesController.69.this.val$loadType != 1) && ((((TLRPC.TL_dialog)localObject1).draft instanceof TLRPC.TL_draftMessage))) {
                  DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(((TLRPC.TL_dialog)localObject1).id, ((TLRPC.TL_dialog)localObject1).draft, null, false);
                }
                if (localObject3 == null)
                {
                  i = 1;
                  MessagesController.this.dialogs_dict.put(l, localObject1);
                  localObject2 = (MessageObject)localLongSparseArray2.get(((TLRPC.TL_dialog)localObject1).id);
                  MessagesController.this.dialogMessage.put(l, localObject2);
                  m = i;
                  if (localObject2 != null)
                  {
                    m = i;
                    if (((MessageObject)localObject2).messageOwner.to_id.channel_id == 0)
                    {
                      MessagesController.this.dialogMessagesByIds.put(((MessageObject)localObject2).getId(), localObject2);
                      m = i;
                      if (((MessageObject)localObject2).messageOwner.random_id != 0L)
                      {
                        MessagesController.this.dialogMessagesByRandomIds.put(((MessageObject)localObject2).messageOwner.random_id, localObject2);
                        m = i;
                      }
                    }
                  }
                }
                else
                {
                  if (MessagesController.69.this.val$loadType != 1) {
                    ((TLRPC.TL_dialog)localObject3).notify_settings = ((TLRPC.TL_dialog)localObject1).notify_settings;
                  }
                  ((TLRPC.TL_dialog)localObject3).pinned = ((TLRPC.TL_dialog)localObject1).pinned;
                  ((TLRPC.TL_dialog)localObject3).pinnedNum = ((TLRPC.TL_dialog)localObject1).pinnedNum;
                  localObject2 = (MessageObject)MessagesController.this.dialogMessage.get(l);
                  if (((localObject2 != null) && (((MessageObject)localObject2).deleted)) || (localObject2 == null) || (((TLRPC.TL_dialog)localObject3).top_message > 0))
                  {
                    m = i;
                    if (((TLRPC.TL_dialog)localObject1).top_message >= ((TLRPC.TL_dialog)localObject3).top_message)
                    {
                      MessagesController.this.dialogs_dict.put(l, localObject1);
                      localObject1 = (MessageObject)localLongSparseArray2.get(((TLRPC.TL_dialog)localObject1).id);
                      MessagesController.this.dialogMessage.put(l, localObject1);
                      if ((localObject1 != null) && (((MessageObject)localObject1).messageOwner.to_id.channel_id == 0))
                      {
                        MessagesController.this.dialogMessagesByIds.put(((MessageObject)localObject1).getId(), localObject1);
                        if ((localObject1 != null) && (((MessageObject)localObject1).messageOwner.random_id != 0L)) {
                          MessagesController.this.dialogMessagesByRandomIds.put(((MessageObject)localObject1).messageOwner.random_id, localObject1);
                        }
                      }
                      m = i;
                      if (localObject2 != null)
                      {
                        MessagesController.this.dialogMessagesByIds.remove(((MessageObject)localObject2).getId());
                        m = i;
                        if (((MessageObject)localObject2).messageOwner.random_id != 0L)
                        {
                          MessagesController.this.dialogMessagesByRandomIds.remove(((MessageObject)localObject2).messageOwner.random_id);
                          m = i;
                        }
                      }
                    }
                  }
                  else
                  {
                    localObject3 = (MessageObject)localLongSparseArray2.get(((TLRPC.TL_dialog)localObject1).id);
                    if ((!((MessageObject)localObject2).deleted) && (localObject3 != null))
                    {
                      m = i;
                      if (((MessageObject)localObject3).messageOwner.date <= ((MessageObject)localObject2).messageOwner.date) {}
                    }
                    else
                    {
                      MessagesController.this.dialogs_dict.put(l, localObject1);
                      MessagesController.this.dialogMessage.put(l, localObject3);
                      if ((localObject3 != null) && (((MessageObject)localObject3).messageOwner.to_id.channel_id == 0))
                      {
                        MessagesController.this.dialogMessagesByIds.put(((MessageObject)localObject3).getId(), localObject3);
                        if ((localObject3 != null) && (((MessageObject)localObject3).messageOwner.random_id != 0L)) {
                          MessagesController.this.dialogMessagesByRandomIds.put(((MessageObject)localObject3).messageOwner.random_id, localObject3);
                        }
                      }
                      MessagesController.this.dialogMessagesByIds.remove(((MessageObject)localObject2).getId());
                      m = i;
                      if (((MessageObject)localObject2).messageOwner.random_id != 0L)
                      {
                        MessagesController.this.dialogMessagesByRandomIds.remove(((MessageObject)localObject2).messageOwner.random_id);
                        m = i;
                      }
                    }
                  }
                }
              }
              label1135:
              MessagesController.this.dialogs.clear();
              int j = 0;
              int m = MessagesController.this.dialogs_dict.size();
              while (j < m)
              {
                MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(j));
                j++;
              }
              localObject1 = MessagesController.this;
              if (MessagesController.69.this.val$migrate)
              {
                localObject2 = localSparseArray2;
                ((MessagesController)localObject1).sortDialogs((SparseArray)localObject2);
                if ((MessagesController.69.this.val$loadType != 2) && (!MessagesController.69.this.val$migrate))
                {
                  localObject2 = MessagesController.this;
                  if (((MessagesController.69.this.val$dialogsRes.dialogs.size() != 0) && (MessagesController.69.this.val$dialogsRes.dialogs.size() == MessagesController.69.this.val$count)) || (MessagesController.69.this.val$loadType != 0)) {
                    break label1738;
                  }
                  bool = true;
                  label1316:
                  ((MessagesController)localObject2).dialogsEndReached = bool;
                  if (!MessagesController.69.this.val$fromCache)
                  {
                    localObject2 = MessagesController.this;
                    if (((MessagesController.69.this.val$dialogsRes.dialogs.size() != 0) && (MessagesController.69.this.val$dialogsRes.dialogs.size() == MessagesController.69.this.val$count)) || (MessagesController.69.this.val$loadType != 0)) {
                      break label1743;
                    }
                    bool = true;
                    label1390:
                    ((MessagesController)localObject2).serverDialogsEndReached = bool;
                  }
                }
                if ((!MessagesController.69.this.val$fromCache) && (!MessagesController.69.this.val$migrate) && (UserConfig.getInstance(MessagesController.this.currentAccount).totalDialogsLoadCount < 400) && (UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId != -1) && (UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId != Integer.MAX_VALUE)) {
                  MessagesController.this.loadDialogs(0, 100, false);
                }
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                if (!MessagesController.69.this.val$migrate) {
                  break label1748;
                }
                UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId = MessagesController.69.this.val$offset;
                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                MessagesController.access$6002(MessagesController.this, false);
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
              }
              for (;;)
              {
                MessagesController.this.migrateDialogs(UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess);
                if (!localArrayList.isEmpty()) {
                  MessagesController.this.reloadDialogsReadValue(localArrayList, 0L);
                }
                return;
                localObject2 = null;
                break;
                label1738:
                bool = false;
                break label1316;
                label1743:
                bool = false;
                break label1390;
                label1748:
                MessagesController.this.generateUpdateMessage();
                if ((i == 0) && (MessagesController.69.this.val$loadType == 1)) {
                  MessagesController.this.loadDialogs(0, MessagesController.69.this.val$count, false);
                }
              }
            }
          });
        }
      }
    });
  }
  
  public void processLoadedMessages(final TLRPC.messages_Messages parammessages_Messages, final long paramLong, final int paramInt1, final int paramInt2, final int paramInt3, boolean paramBoolean1, final int paramInt4, final int paramInt5, final int paramInt6, final int paramInt7, final int paramInt8, final int paramInt9, final boolean paramBoolean2, final boolean paramBoolean3, final int paramInt10, final boolean paramBoolean4, final int paramInt11)
  {
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("processLoadedMessages size " + parammessages_Messages.messages.size() + " in chat " + paramLong + " count " + paramInt1 + " max_id " + paramInt2 + " cache " + paramBoolean1 + " guid " + paramInt4 + " load_type " + paramInt9 + " last_message_id " + paramInt6 + " isChannel " + paramBoolean2 + " index " + paramInt10 + " firstUnread " + paramInt5 + " unread_count " + paramInt7 + " last_date " + paramInt8 + " queryFromServer " + paramBoolean4);
    }
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        boolean bool1 = false;
        boolean bool2 = false;
        boolean bool3 = false;
        boolean bool4 = bool3;
        boolean bool5;
        label133:
        Object localObject1;
        if ((parammessages_Messages instanceof TLRPC.TL_messages_channelMessages))
        {
          i = -(int)paramLong;
          bool5 = bool2;
          if (MessagesController.this.channelsPts.get(i) == 0)
          {
            bool5 = bool2;
            if (MessagesStorage.getInstance(MessagesController.this.currentAccount).getChannelPtsSync(i) == 0)
            {
              MessagesController.this.channelsPts.put(i, parammessages_Messages.pts);
              bool5 = true;
              if ((MessagesController.this.needShortPollChannels.indexOfKey(i) < 0) || (MessagesController.this.shortPollChannels.indexOfKey(i) >= 0)) {
                break label268;
              }
              MessagesController.this.getChannelDifference(i, 2, 0L, null);
            }
          }
          j = 0;
          bool1 = bool5;
          bool4 = bool3;
          if (j < parammessages_Messages.chats.size())
          {
            localObject1 = (TLRPC.Chat)parammessages_Messages.chats.get(j);
            if (((TLRPC.Chat)localObject1).id != i) {
              break label280;
            }
            bool4 = ((TLRPC.Chat)localObject1).megagroup;
            bool1 = bool5;
          }
        }
        int i = (int)paramLong;
        int j = (int)(paramLong >> 32);
        if (!paramInt1) {
          ImageLoader.saveMessagesThumbs(parammessages_Messages.messages);
        }
        if ((j != 1) && (i != 0) && (paramInt1) && (parammessages_Messages.messages.size() == 0)) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController localMessagesController = MessagesController.this;
              long l = MessagesController.62.this.val$dialog_id;
              int i = MessagesController.62.this.val$count;
              if ((MessagesController.62.this.val$load_type == 2) && (MessagesController.62.this.val$queryFromServer)) {}
              for (int j = MessagesController.62.this.val$first_unread;; j = MessagesController.62.this.val$max_id)
              {
                localMessagesController.loadMessages(l, i, j, MessagesController.62.this.val$offset_date, false, 0, MessagesController.62.this.val$classGuid, MessagesController.62.this.val$load_type, MessagesController.62.this.val$last_message_id, MessagesController.62.this.val$isChannel, MessagesController.62.this.val$loadIndex, MessagesController.62.this.val$first_unread, MessagesController.62.this.val$unread_count, MessagesController.62.this.val$last_date, MessagesController.62.this.val$queryFromServer, MessagesController.62.this.val$mentionsCount);
                return;
              }
            }
          });
        }
        for (;;)
        {
          return;
          label268:
          MessagesController.this.getChannelDifference(i);
          break;
          label280:
          j++;
          break label133;
          SparseArray localSparseArray1 = new SparseArray();
          SparseArray localSparseArray2 = new SparseArray();
          for (j = 0; j < parammessages_Messages.users.size(); j++)
          {
            localObject1 = (TLRPC.User)parammessages_Messages.users.get(j);
            localSparseArray1.put(((TLRPC.User)localObject1).id, localObject1);
          }
          for (j = 0; j < parammessages_Messages.chats.size(); j++)
          {
            localObject1 = (TLRPC.Chat)parammessages_Messages.chats.get(j);
            localSparseArray2.put(((TLRPC.Chat)localObject1).id, localObject1);
          }
          i = parammessages_Messages.messages.size();
          Object localObject2;
          Object localObject3;
          Object localObject4;
          if (!paramInt1)
          {
            localObject2 = (Integer)MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(paramLong));
            localObject1 = localObject2;
            if (localObject2 == null)
            {
              localObject1 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, paramLong));
              MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(paramLong), localObject1);
            }
            localObject3 = (Integer)MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(paramLong));
            localObject2 = localObject3;
            if (localObject3 == null)
            {
              localObject2 = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, paramLong));
              MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(paramLong), localObject2);
            }
            j = 0;
            while (j < i)
            {
              localObject4 = (TLRPC.Message)parammessages_Messages.messages.get(j);
              if (bool4) {
                ((TLRPC.Message)localObject4).flags |= 0x80000000;
              }
              if ((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChatDeleteUser))
              {
                localObject3 = (TLRPC.User)localSparseArray1.get(((TLRPC.Message)localObject4).action.user_id);
                if ((localObject3 != null) && (((TLRPC.User)localObject3).bot))
                {
                  ((TLRPC.Message)localObject4).reply_markup = new TLRPC.TL_replyKeyboardHide();
                  ((TLRPC.Message)localObject4).flags |= 0x40;
                }
              }
              if (((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChatMigrateTo)) || ((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageActionChannelCreate)))
              {
                ((TLRPC.Message)localObject4).unread = false;
                ((TLRPC.Message)localObject4).media_unread = false;
                j++;
              }
              else
              {
                if (((TLRPC.Message)localObject4).out)
                {
                  localObject3 = localObject2;
                  label741:
                  if (((Integer)localObject3).intValue() >= ((TLRPC.Message)localObject4).id) {
                    break label774;
                  }
                }
                label774:
                for (bool5 = true;; bool5 = false)
                {
                  ((TLRPC.Message)localObject4).unread = bool5;
                  break;
                  localObject3 = localObject1;
                  break label741;
                }
              }
            }
            MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(parammessages_Messages, paramLong, paramBoolean4, paramInt3, bool1);
          }
          final ArrayList localArrayList1 = new ArrayList();
          final ArrayList localArrayList2 = new ArrayList();
          final HashMap localHashMap = new HashMap();
          j = 0;
          if (j < i)
          {
            localObject3 = (TLRPC.Message)parammessages_Messages.messages.get(j);
            ((TLRPC.Message)localObject3).dialog_id = paramLong;
            localObject4 = new MessageObject(MessagesController.this.currentAccount, (TLRPC.Message)localObject3, localSparseArray1, localSparseArray2, true);
            localArrayList1.add(localObject4);
            if (paramInt1)
            {
              if (!(((TLRPC.Message)localObject3).media instanceof TLRPC.TL_messageMediaUnsupported)) {
                break label993;
              }
              if ((((TLRPC.Message)localObject3).media.bytes != null) && ((((TLRPC.Message)localObject3).media.bytes.length == 0) || ((((TLRPC.Message)localObject3).media.bytes.length == 1) && (localObject3.media.bytes[0] < 76)))) {
                localArrayList2.add(Integer.valueOf(((TLRPC.Message)localObject3).id));
              }
            }
            for (;;)
            {
              j++;
              break;
              label993:
              if ((((TLRPC.Message)localObject3).media instanceof TLRPC.TL_messageMediaWebPage)) {
                if (((((TLRPC.Message)localObject3).media.webpage instanceof TLRPC.TL_webPagePending)) && (((TLRPC.Message)localObject3).media.webpage.date <= ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime()))
                {
                  localArrayList2.add(Integer.valueOf(((TLRPC.Message)localObject3).id));
                }
                else if ((((TLRPC.Message)localObject3).media.webpage instanceof TLRPC.TL_webPageUrlPending))
                {
                  localObject2 = (ArrayList)localHashMap.get(((TLRPC.Message)localObject3).media.webpage.url);
                  localObject1 = localObject2;
                  if (localObject2 == null)
                  {
                    localObject1 = new ArrayList();
                    localHashMap.put(((TLRPC.Message)localObject3).media.webpage.url, localObject1);
                  }
                  ((ArrayList)localObject1).add(localObject4);
                }
              }
            }
          }
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.this.putUsers(MessagesController.62.this.val$messagesRes.users, MessagesController.62.this.val$isCache);
              MessagesController.this.putChats(MessagesController.62.this.val$messagesRes.chats, MessagesController.62.this.val$isCache);
              int i = Integer.MAX_VALUE;
              int j = i;
              if (MessagesController.62.this.val$queryFromServer)
              {
                j = i;
                if (MessagesController.62.this.val$load_type == 2)
                {
                  int k = 0;
                  for (;;)
                  {
                    j = i;
                    if (k >= MessagesController.62.this.val$messagesRes.messages.size()) {
                      break;
                    }
                    TLRPC.Message localMessage = (TLRPC.Message)MessagesController.62.this.val$messagesRes.messages.get(k);
                    j = i;
                    if (!localMessage.out)
                    {
                      j = i;
                      if (localMessage.id > MessagesController.62.this.val$first_unread)
                      {
                        j = i;
                        if (localMessage.id < i) {
                          j = localMessage.id;
                        }
                      }
                    }
                    k++;
                    i = j;
                  }
                }
              }
              i = j;
              if (j == Integer.MAX_VALUE) {
                i = MessagesController.62.this.val$first_unread;
              }
              NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesDidLoaded, new Object[] { Long.valueOf(MessagesController.62.this.val$dialog_id), Integer.valueOf(MessagesController.62.this.val$count), localArrayList1, Boolean.valueOf(MessagesController.62.this.val$isCache), Integer.valueOf(i), Integer.valueOf(MessagesController.62.this.val$last_message_id), Integer.valueOf(MessagesController.62.this.val$unread_count), Integer.valueOf(MessagesController.62.this.val$last_date), Integer.valueOf(MessagesController.62.this.val$load_type), Boolean.valueOf(MessagesController.62.this.val$isEnd), Integer.valueOf(MessagesController.62.this.val$classGuid), Integer.valueOf(MessagesController.62.this.val$loadIndex), Integer.valueOf(MessagesController.62.this.val$max_id), Integer.valueOf(MessagesController.62.this.val$mentionsCount) });
              if (!localArrayList2.isEmpty()) {
                MessagesController.this.reloadMessages(localArrayList2, MessagesController.62.this.val$dialog_id);
              }
              if (!localHashMap.isEmpty()) {
                MessagesController.this.reloadWebPages(MessagesController.62.this.val$dialog_id, localHashMap);
              }
            }
          });
        }
      }
    });
  }
  
  public void processLoadedUserPhotos(final TLRPC.photos_Photos paramphotos_Photos, final int paramInt1, final int paramInt2, long paramLong, final boolean paramBoolean, final int paramInt3)
  {
    if (!paramBoolean)
    {
      MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(paramphotos_Photos.users, null, true, true);
      MessagesStorage.getInstance(this.currentAccount).putDialogPhotos(paramInt1, paramphotos_Photos);
    }
    for (;;)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          MessagesController.this.putUsers(paramphotos_Photos.users, paramBoolean);
          NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogPhotosLoaded, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Boolean.valueOf(paramBoolean), Integer.valueOf(paramInt3), paramphotos_Photos.photos });
        }
      });
      for (;;)
      {
        return;
        if ((paramphotos_Photos != null) && (!paramphotos_Photos.photos.isEmpty())) {
          break;
        }
        loadDialogPhotos(paramInt1, paramInt2, paramLong, false, paramInt3);
      }
    }
  }
  
  protected void processNewChannelDifferenceParams(int paramInt1, int paramInt2, int paramInt3)
  {
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("processNewChannelDifferenceParams pts = " + paramInt1 + " pts_count = " + paramInt2 + " channeldId = " + paramInt3);
    }
    int i = this.channelsPts.get(paramInt3);
    int j = i;
    if (i == 0)
    {
      i = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(paramInt3);
      j = i;
      if (i == 0) {
        j = 1;
      }
      this.channelsPts.put(paramInt3, j);
    }
    if (j + paramInt2 == paramInt1)
    {
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("APPLY CHANNEL PTS");
      }
      this.channelsPts.put(paramInt3, paramInt1);
      MessagesStorage.getInstance(this.currentAccount).saveChannelPts(paramInt3, paramInt1);
    }
    for (;;)
    {
      return;
      if (j != paramInt1)
      {
        long l = this.updatesStartWaitTimeChannels.get(paramInt3);
        if ((this.gettingDifferenceChannels.get(paramInt3)) || (l == 0L) || (Math.abs(System.currentTimeMillis() - l) <= 1500L))
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("ADD CHANNEL UPDATE TO QUEUE pts = " + paramInt1 + " pts_count = " + paramInt2);
          }
          if (l == 0L) {
            this.updatesStartWaitTimeChannels.put(paramInt3, System.currentTimeMillis());
          }
          UserActionUpdatesPts localUserActionUpdatesPts = new UserActionUpdatesPts(null);
          localUserActionUpdatesPts.pts = paramInt1;
          localUserActionUpdatesPts.pts_count = paramInt2;
          localUserActionUpdatesPts.chat_id = paramInt3;
          ArrayList localArrayList1 = (ArrayList)this.updatesQueueChannels.get(paramInt3);
          ArrayList localArrayList2 = localArrayList1;
          if (localArrayList1 == null)
          {
            localArrayList2 = new ArrayList();
            this.updatesQueueChannels.put(paramInt3, localArrayList2);
          }
          localArrayList2.add(localUserActionUpdatesPts);
        }
        else
        {
          getChannelDifference(paramInt3);
        }
      }
    }
  }
  
  protected void processNewDifferenceParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (BuildVars.LOGS_ENABLED) {
      FileLog.d("processNewDifferenceParams seq = " + paramInt1 + " pts = " + paramInt2 + " date = " + paramInt3 + " pts_count = " + paramInt4);
    }
    if (paramInt2 != -1)
    {
      if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + paramInt4 != paramInt2) {
        break label266;
      }
      if (BuildVars.LOGS_ENABLED) {
        FileLog.d("APPLY PTS");
      }
      MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(paramInt2);
      MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
    }
    label266:
    label414:
    label542:
    for (;;)
    {
      if (paramInt1 != -1)
      {
        if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 != paramInt1) {
          break label414;
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("APPLY SEQ");
        }
        MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(paramInt1);
        if (paramInt3 != -1) {
          MessagesStorage.getInstance(this.currentAccount).setLastDateValue(paramInt3);
        }
        MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
      }
      for (;;)
      {
        return;
        if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() == paramInt2) {
          break label542;
        }
        Object localObject;
        if ((this.gettingDifference) || (this.updatesStartWaitTimePts == 0L) || (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500L))
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("ADD UPDATE TO QUEUE pts = " + paramInt2 + " pts_count = " + paramInt4);
          }
          if (this.updatesStartWaitTimePts == 0L) {
            this.updatesStartWaitTimePts = System.currentTimeMillis();
          }
          localObject = new UserActionUpdatesPts(null);
          ((UserActionUpdatesPts)localObject).pts = paramInt2;
          ((UserActionUpdatesPts)localObject).pts_count = paramInt4;
          this.updatesQueuePts.add(localObject);
          break;
        }
        getDifference();
        break;
        if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() != paramInt1) {
          if ((this.gettingDifference) || (this.updatesStartWaitTimeSeq == 0L) || (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) <= 1500L))
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("ADD UPDATE TO QUEUE seq = " + paramInt1);
            }
            if (this.updatesStartWaitTimeSeq == 0L) {
              this.updatesStartWaitTimeSeq = System.currentTimeMillis();
            }
            localObject = new UserActionUpdatesSeq(null);
            ((UserActionUpdatesSeq)localObject).seq = paramInt1;
            this.updatesQueueSeq.add(localObject);
          }
          else
          {
            getDifference();
          }
        }
      }
    }
  }
  
  public boolean processUpdateArray(final ArrayList<TLRPC.Update> paramArrayList, final ArrayList<TLRPC.User> paramArrayList1, final ArrayList<TLRPC.Chat> paramArrayList2, final boolean paramBoolean)
  {
    if (paramArrayList.isEmpty())
    {
      if ((paramArrayList1 != null) || (paramArrayList2 != null)) {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.putUsers(paramArrayList1, false);
            MessagesController.this.putChats(paramArrayList2, false);
          }
        });
      }
      paramBoolean = true;
    }
    for (;;)
    {
      return paramBoolean;
      long l1 = System.currentTimeMillis();
      boolean bool1 = false;
      final Object localObject1 = null;
      final Object localObject2 = null;
      final Object localObject3 = null;
      Object localObject4 = null;
      final Object localObject5 = null;
      final Object localObject6 = null;
      final Object localObject7 = null;
      final Object localObject8 = null;
      final Object localObject9 = null;
      final Object localObject10 = null;
      final Object localObject11 = null;
      final Object localObject12 = null;
      final Object localObject13 = null;
      Object localObject14 = null;
      Object localObject15 = null;
      final Object localObject16 = null;
      final int i = 1;
      Object localObject17;
      final int k;
      Object localObject19;
      if (paramArrayList1 != null)
      {
        localObject17 = new ConcurrentHashMap();
        j = 0;
        k = paramArrayList1.size();
        for (;;)
        {
          localObject18 = localObject17;
          m = i;
          if (j >= k) {
            break;
          }
          localObject19 = (TLRPC.User)paramArrayList1.get(j);
          ((ConcurrentHashMap)localObject17).put(Integer.valueOf(((TLRPC.User)localObject19).id), localObject19);
          j++;
        }
      }
      int m = 0;
      Object localObject18 = this.users;
      if (paramArrayList2 != null)
      {
        localObject17 = new ConcurrentHashMap();
        i = 0;
        k = paramArrayList2.size();
        for (;;)
        {
          localObject20 = localObject17;
          j = m;
          if (i >= k) {
            break;
          }
          localObject19 = (TLRPC.Chat)paramArrayList2.get(i);
          ((ConcurrentHashMap)localObject17).put(Integer.valueOf(((TLRPC.Chat)localObject19).id), localObject19);
          i++;
        }
      }
      final int j = 0;
      Object localObject20 = this.chats;
      int n = j;
      if (paramBoolean) {
        n = 0;
      }
      if ((paramArrayList1 != null) || (paramArrayList2 != null)) {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            MessagesController.this.putUsers(paramArrayList1, false);
            MessagesController.this.putChats(paramArrayList2, false);
          }
        });
      }
      j = 0;
      int i1 = 0;
      int i2 = paramArrayList.size();
      paramArrayList1 = (ArrayList<TLRPC.User>)localObject14;
      label299:
      if (i1 < i2)
      {
        final Object localObject21 = (TLRPC.Update)paramArrayList.get(i1);
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("process update " + localObject21);
        }
        label381:
        label618:
        label674:
        int i4;
        label947:
        label1115:
        label1210:
        label1279:
        label1293:
        boolean bool2;
        label1410:
        Object localObject22;
        label1498:
        Object localObject23;
        Object localObject24;
        Object localObject25;
        Object localObject26;
        Object localObject27;
        Object localObject28;
        Object localObject29;
        Object localObject30;
        Object localObject31;
        Object localObject32;
        Object localObject33;
        Object localObject34;
        if (((localObject21 instanceof TLRPC.TL_updateNewMessage)) || ((localObject21 instanceof TLRPC.TL_updateNewChannelMessage)))
        {
          if ((localObject21 instanceof TLRPC.TL_updateNewMessage))
          {
            localObject17 = ((TLRPC.TL_updateNewMessage)localObject21).message;
            paramArrayList2 = null;
            m = 0;
            i = 0;
            if (((TLRPC.Message)localObject17).to_id.channel_id == 0) {
              break label618;
            }
            k = ((TLRPC.Message)localObject17).to_id.channel_id;
          }
          for (;;)
          {
            if (k != 0)
            {
              paramArrayList2 = (TLRPC.Chat)((ConcurrentHashMap)localObject20).get(Integer.valueOf(k));
              localObject14 = paramArrayList2;
              if (paramArrayList2 == null) {
                localObject14 = getChat(Integer.valueOf(k));
              }
              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
              if (localObject14 == null)
              {
                paramArrayList2 = MessagesStorage.getInstance(this.currentAccount).getChatSync(k);
                putChat(paramArrayList2, true);
              }
            }
            m = j;
            if (n == 0) {
              break label1115;
            }
            if ((k == 0) || (paramArrayList2 != null)) {
              break label674;
            }
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("not found chat " + k);
            }
            paramBoolean = false;
            break;
            paramArrayList2 = ((TLRPC.TL_updateNewChannelMessage)localObject21).message;
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d(localObject21 + " channelId = " + paramArrayList2.to_id.channel_id);
            }
            localObject17 = paramArrayList2;
            if (paramArrayList2.out) {
              break label381;
            }
            localObject17 = paramArrayList2;
            if (paramArrayList2.from_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
              break label381;
            }
            paramArrayList2.out = true;
            localObject17 = paramArrayList2;
            break label381;
            if (((TLRPC.Message)localObject17).to_id.chat_id != 0)
            {
              k = ((TLRPC.Message)localObject17).to_id.chat_id;
            }
            else
            {
              k = m;
              if (((TLRPC.Message)localObject17).to_id.user_id != 0)
              {
                i = ((TLRPC.Message)localObject17).to_id.user_id;
                k = m;
              }
            }
          }
          int i3 = ((TLRPC.Message)localObject17).entities.size();
          m = 0;
          i4 = i;
          i = m;
          for (;;)
          {
            m = j;
            if (i >= i3 + 3) {
              break label1115;
            }
            int i5 = 0;
            k = i5;
            m = i4;
            if (i != 0)
            {
              if (i != 1) {
                break label947;
              }
              i4 = ((TLRPC.Message)localObject17).from_id;
              k = i5;
              m = i4;
              if (((TLRPC.Message)localObject17).post)
              {
                k = 1;
                m = i4;
              }
            }
            i4 = j;
            if (m > 0)
            {
              localObject19 = (TLRPC.User)((ConcurrentHashMap)localObject18).get(Integer.valueOf(m));
              if (localObject19 != null)
              {
                localObject14 = localObject19;
                if (k == 0)
                {
                  localObject14 = localObject19;
                  if (!((TLRPC.User)localObject19).min) {}
                }
              }
              else
              {
                localObject14 = getUser(Integer.valueOf(m));
              }
              if (localObject14 != null)
              {
                localObject19 = localObject14;
                if (k == 0)
                {
                  localObject19 = localObject14;
                  if (!((TLRPC.User)localObject14).min) {}
                }
              }
              else
              {
                localObject19 = MessagesStorage.getInstance(this.currentAccount).getUserSync(m);
                localObject14 = localObject19;
                if (localObject19 != null)
                {
                  localObject14 = localObject19;
                  if (k == 0)
                  {
                    localObject14 = localObject19;
                    if (((TLRPC.User)localObject19).min) {
                      localObject14 = null;
                    }
                  }
                }
                putUser((TLRPC.User)localObject14, true);
                localObject19 = localObject14;
              }
              if (localObject19 == null)
              {
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.d("not found user " + m);
                }
                paramBoolean = false;
                break;
                if (i == 2)
                {
                  if (((TLRPC.Message)localObject17).fwd_from != null) {}
                  for (m = ((TLRPC.Message)localObject17).fwd_from.from_id;; m = 0)
                  {
                    k = i5;
                    break;
                  }
                }
                localObject14 = (TLRPC.MessageEntity)((TLRPC.Message)localObject17).entities.get(i - 3);
                if ((localObject14 instanceof TLRPC.TL_messageEntityMentionName)) {}
                for (m = ((TLRPC.TL_messageEntityMentionName)localObject14).user_id;; m = 0)
                {
                  k = i5;
                  break;
                }
              }
              i4 = j;
              if (i == 1)
              {
                i4 = j;
                if (((TLRPC.User)localObject19).status != null)
                {
                  i4 = j;
                  if (((TLRPC.User)localObject19).status.expires <= 0)
                  {
                    this.onlinePrivacy.put(Integer.valueOf(m), Integer.valueOf(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
                    i4 = j | 0x4;
                  }
                }
              }
            }
            i++;
            j = i4;
            i4 = m;
          }
          if ((paramArrayList2 != null) && (paramArrayList2.megagroup)) {
            ((TLRPC.Message)localObject17).flags |= 0x80000000;
          }
          if ((((TLRPC.Message)localObject17).action instanceof TLRPC.TL_messageActionChatDeleteUser))
          {
            localObject14 = (TLRPC.User)((ConcurrentHashMap)localObject18).get(Integer.valueOf(((TLRPC.Message)localObject17).action.user_id));
            if ((localObject14 != null) && (((TLRPC.User)localObject14).bot))
            {
              ((TLRPC.Message)localObject17).reply_markup = new TLRPC.TL_replyKeyboardHide();
              ((TLRPC.Message)localObject17).flags |= 0x40;
            }
          }
          else
          {
            localObject21 = localObject4;
            if (localObject4 == null) {
              localObject21 = new ArrayList();
            }
            ((ArrayList)localObject21).add(localObject17);
            ImageLoader.saveMessageThumbs((TLRPC.Message)localObject17);
            j = UserConfig.getInstance(this.currentAccount).getClientUserId();
            if (((TLRPC.Message)localObject17).to_id.chat_id == 0) {
              break label1997;
            }
            ((TLRPC.Message)localObject17).dialog_id = (-((TLRPC.Message)localObject17).to_id.chat_id);
            if (!((TLRPC.Message)localObject17).out) {
              break label2069;
            }
            localObject4 = this.dialogs_read_outbox_max;
            localObject19 = (Integer)((ConcurrentHashMap)localObject4).get(Long.valueOf(((TLRPC.Message)localObject17).dialog_id));
            localObject14 = localObject19;
            if (localObject19 == null)
            {
              localObject14 = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(((TLRPC.Message)localObject17).out, ((TLRPC.Message)localObject17).dialog_id));
              ((ConcurrentHashMap)localObject4).put(Long.valueOf(((TLRPC.Message)localObject17).dialog_id), localObject14);
            }
            if ((((Integer)localObject14).intValue() >= ((TLRPC.Message)localObject17).id) || ((paramArrayList2 != null) && (ChatObject.isNotInChat(paramArrayList2))) || ((((TLRPC.Message)localObject17).action instanceof TLRPC.TL_messageActionChatMigrateTo)) || ((((TLRPC.Message)localObject17).action instanceof TLRPC.TL_messageActionChannelCreate))) {
              break label2078;
            }
            bool2 = true;
            ((TLRPC.Message)localObject17).unread = bool2;
            if (((TLRPC.Message)localObject17).dialog_id == j)
            {
              ((TLRPC.Message)localObject17).unread = false;
              ((TLRPC.Message)localObject17).media_unread = false;
              ((TLRPC.Message)localObject17).out = true;
            }
            localObject22 = new MessageObject(this.currentAccount, (TLRPC.Message)localObject17, (AbstractMap)localObject18, (AbstractMap)localObject20, this.createdDialogIds.contains(Long.valueOf(((TLRPC.Message)localObject17).dialog_id)));
            if (((MessageObject)localObject22).type != 11) {
              break label2084;
            }
            j = m | 0x8;
            localObject4 = localObject1;
            if (localObject1 == null) {
              localObject4 = new LongSparseArray();
            }
            localObject1 = (ArrayList)((LongSparseArray)localObject4).get(((TLRPC.Message)localObject17).dialog_id);
            paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject1;
            if (localObject1 == null)
            {
              paramArrayList2 = new ArrayList();
              ((LongSparseArray)localObject4).put(((TLRPC.Message)localObject17).dialog_id, paramArrayList2);
            }
            paramArrayList2.add(localObject22);
            localObject23 = localObject6;
            localObject24 = localObject13;
            localObject25 = localObject12;
            localObject26 = localObject16;
            localObject27 = localObject11;
            localObject28 = localObject5;
            m = j;
            localObject29 = localObject10;
            localObject30 = localObject9;
            localObject31 = localObject7;
            localObject32 = localObject8;
            localObject14 = localObject4;
            localObject17 = localObject21;
            bool2 = bool1;
            localObject19 = localObject3;
            localObject33 = localObject15;
            paramArrayList2 = paramArrayList1;
            localObject34 = localObject2;
            if (!((MessageObject)localObject22).isOut())
            {
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject4;
              localObject17 = localObject21;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
              if (((MessageObject)localObject22).isUnread())
              {
                localObject19 = localObject3;
                if (localObject3 == null) {
                  localObject19 = new ArrayList();
                }
                ((ArrayList)localObject19).add(localObject22);
                localObject34 = localObject2;
                paramArrayList2 = paramArrayList1;
                localObject33 = localObject15;
                bool2 = bool1;
                localObject17 = localObject21;
                localObject14 = localObject4;
                localObject32 = localObject8;
                localObject31 = localObject7;
                localObject30 = localObject9;
                localObject29 = localObject10;
                m = j;
                localObject28 = localObject5;
                localObject27 = localObject11;
                localObject26 = localObject16;
                localObject25 = localObject12;
                localObject24 = localObject13;
                localObject23 = localObject6;
              }
            }
          }
        }
        for (;;)
        {
          i1++;
          localObject6 = localObject23;
          localObject13 = localObject24;
          localObject12 = localObject25;
          localObject16 = localObject26;
          localObject11 = localObject27;
          localObject5 = localObject28;
          j = m;
          localObject10 = localObject29;
          localObject9 = localObject30;
          localObject7 = localObject31;
          localObject8 = localObject32;
          localObject1 = localObject14;
          localObject4 = localObject17;
          bool1 = bool2;
          localObject3 = localObject19;
          localObject15 = localObject33;
          paramArrayList1 = paramArrayList2;
          localObject2 = localObject34;
          break label299;
          if ((((TLRPC.Message)localObject17).from_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) || (((TLRPC.Message)localObject17).action.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId())) {
            break label1210;
          }
          localObject23 = localObject6;
          localObject24 = localObject13;
          localObject25 = localObject12;
          localObject26 = localObject16;
          localObject27 = localObject11;
          localObject28 = localObject5;
          localObject29 = localObject10;
          localObject30 = localObject9;
          localObject31 = localObject7;
          localObject32 = localObject8;
          localObject14 = localObject1;
          localObject17 = localObject4;
          bool2 = bool1;
          localObject19 = localObject3;
          localObject33 = localObject15;
          paramArrayList2 = paramArrayList1;
          localObject34 = localObject2;
          continue;
          label1997:
          if (((TLRPC.Message)localObject17).to_id.channel_id != 0)
          {
            ((TLRPC.Message)localObject17).dialog_id = (-((TLRPC.Message)localObject17).to_id.channel_id);
            break label1279;
          }
          if (((TLRPC.Message)localObject17).to_id.user_id == j) {
            ((TLRPC.Message)localObject17).to_id.user_id = ((TLRPC.Message)localObject17).from_id;
          }
          ((TLRPC.Message)localObject17).dialog_id = ((TLRPC.Message)localObject17).to_id.user_id;
          break label1279;
          label2069:
          localObject4 = this.dialogs_read_inbox_max;
          break label1293;
          label2078:
          bool2 = false;
          break label1410;
          label2084:
          j = m;
          if (((MessageObject)localObject22).type != 10) {
            break label1498;
          }
          j = m | 0x10;
          break label1498;
          if ((localObject21 instanceof TLRPC.TL_updateReadMessagesContents))
          {
            localObject22 = (TLRPC.TL_updateReadMessagesContents)localObject21;
            localObject21 = localObject9;
            if (localObject9 == null) {
              localObject21 = new ArrayList();
            }
            i = 0;
            k = ((TLRPC.TL_updateReadMessagesContents)localObject22).messages.size();
            for (;;)
            {
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject21;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
              if (i >= k) {
                break;
              }
              ((ArrayList)localObject21).add(Long.valueOf(((Integer)((TLRPC.TL_updateReadMessagesContents)localObject22).messages.get(i)).intValue()));
              i++;
            }
          }
          if ((localObject21 instanceof TLRPC.TL_updateChannelReadMessagesContents))
          {
            localObject22 = (TLRPC.TL_updateChannelReadMessagesContents)localObject21;
            localObject21 = localObject9;
            if (localObject9 == null) {
              localObject21 = new ArrayList();
            }
            i = 0;
            k = ((TLRPC.TL_updateChannelReadMessagesContents)localObject22).messages.size();
            for (;;)
            {
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject21;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
              if (i >= k) {
                break;
              }
              ((ArrayList)localObject21).add(Long.valueOf(((Integer)((TLRPC.TL_updateChannelReadMessagesContents)localObject22).messages.get(i)).intValue() | ((TLRPC.TL_updateChannelReadMessagesContents)localObject22).channel_id << 32));
              i++;
            }
          }
          long l2;
          if ((localObject21 instanceof TLRPC.TL_updateReadHistoryInbox))
          {
            localObject17 = (TLRPC.TL_updateReadHistoryInbox)localObject21;
            localObject31 = localObject7;
            if (localObject7 == null) {
              localObject31 = new SparseLongArray();
            }
            if (((TLRPC.TL_updateReadHistoryInbox)localObject17).peer.chat_id != 0) {
              ((SparseLongArray)localObject31).put(-((TLRPC.TL_updateReadHistoryInbox)localObject17).peer.chat_id, ((TLRPC.TL_updateReadHistoryInbox)localObject17).max_id);
            }
            for (l2 = -((TLRPC.TL_updateReadHistoryInbox)localObject17).peer.chat_id;; l2 = ((TLRPC.TL_updateReadHistoryInbox)localObject17).peer.user_id)
            {
              localObject14 = (Integer)this.dialogs_read_inbox_max.get(Long.valueOf(l2));
              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
              if (localObject14 == null) {
                paramArrayList2 = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(false, l2));
              }
              this.dialogs_read_inbox_max.put(Long.valueOf(l2), Integer.valueOf(Math.max(paramArrayList2.intValue(), ((TLRPC.TL_updateReadHistoryInbox)localObject17).max_id)));
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
              break;
              ((SparseLongArray)localObject31).put(((TLRPC.TL_updateReadHistoryInbox)localObject17).peer.user_id, ((TLRPC.TL_updateReadHistoryInbox)localObject17).max_id);
            }
          }
          if ((localObject21 instanceof TLRPC.TL_updateReadHistoryOutbox))
          {
            localObject17 = (TLRPC.TL_updateReadHistoryOutbox)localObject21;
            localObject32 = localObject8;
            if (localObject8 == null) {
              localObject32 = new SparseLongArray();
            }
            if (((TLRPC.TL_updateReadHistoryOutbox)localObject17).peer.chat_id != 0) {
              ((SparseLongArray)localObject32).put(-((TLRPC.TL_updateReadHistoryOutbox)localObject17).peer.chat_id, ((TLRPC.TL_updateReadHistoryOutbox)localObject17).max_id);
            }
            for (l2 = -((TLRPC.TL_updateReadHistoryOutbox)localObject17).peer.chat_id;; l2 = ((TLRPC.TL_updateReadHistoryOutbox)localObject17).peer.user_id)
            {
              localObject14 = (Integer)this.dialogs_read_outbox_max.get(Long.valueOf(l2));
              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
              if (localObject14 == null) {
                paramArrayList2 = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, l2));
              }
              this.dialogs_read_outbox_max.put(Long.valueOf(l2), Integer.valueOf(Math.max(paramArrayList2.intValue(), ((TLRPC.TL_updateReadHistoryOutbox)localObject17).max_id)));
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
              break;
              ((SparseLongArray)localObject32).put(((TLRPC.TL_updateReadHistoryOutbox)localObject17).peer.user_id, ((TLRPC.TL_updateReadHistoryOutbox)localObject17).max_id);
            }
          }
          if ((localObject21 instanceof TLRPC.TL_updateDeleteMessages))
          {
            localObject17 = (TLRPC.TL_updateDeleteMessages)localObject21;
            localObject27 = localObject11;
            if (localObject11 == null) {
              localObject27 = new SparseArray();
            }
            localObject14 = (ArrayList)((SparseArray)localObject27).get(0);
            paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
            if (localObject14 == null)
            {
              paramArrayList2 = new ArrayList();
              ((SparseArray)localObject27).put(0, paramArrayList2);
            }
            paramArrayList2.addAll(((TLRPC.TL_updateDeleteMessages)localObject17).messages);
            localObject23 = localObject6;
            localObject24 = localObject13;
            localObject25 = localObject12;
            localObject26 = localObject16;
            localObject28 = localObject5;
            m = j;
            localObject29 = localObject10;
            localObject30 = localObject9;
            localObject31 = localObject7;
            localObject32 = localObject8;
            localObject14 = localObject1;
            localObject17 = localObject4;
            bool2 = bool1;
            localObject19 = localObject3;
            localObject33 = localObject15;
            paramArrayList2 = paramArrayList1;
            localObject34 = localObject2;
          }
          else
          {
            label3115:
            long l3;
            if (((localObject21 instanceof TLRPC.TL_updateUserTyping)) || ((localObject21 instanceof TLRPC.TL_updateChatUserTyping)))
            {
              if ((localObject21 instanceof TLRPC.TL_updateUserTyping))
              {
                paramArrayList2 = (TLRPC.TL_updateUserTyping)localObject21;
                i = paramArrayList2.user_id;
                localObject21 = paramArrayList2.action;
                k = 0;
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                paramArrayList2 = paramArrayList1;
                localObject34 = localObject2;
                if (i == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                  continue;
                }
                l3 = -k;
                l2 = l3;
                if (l3 == 0L) {
                  l2 = i;
                }
                localObject14 = (ArrayList)this.printingUsers.get(Long.valueOf(l2));
                if (!(localObject21 instanceof TLRPC.TL_sendMessageCancelAction)) {
                  break label3463;
                }
                bool2 = bool1;
                if (localObject14 != null)
                {
                  m = 0;
                  k = ((ArrayList)localObject14).size();
                  label3266:
                  boolean bool3 = bool1;
                  if (m < k)
                  {
                    if (((PrintingUser)((ArrayList)localObject14).get(m)).userId != i) {
                      break label3457;
                    }
                    ((ArrayList)localObject14).remove(m);
                    bool3 = true;
                  }
                  bool2 = bool3;
                  if (((ArrayList)localObject14).isEmpty())
                  {
                    this.printingUsers.remove(Long.valueOf(l2));
                    bool2 = bool3;
                  }
                }
              }
              for (;;)
              {
                this.onlinePrivacy.put(Integer.valueOf(i), Integer.valueOf(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                localObject19 = localObject3;
                localObject33 = localObject15;
                paramArrayList2 = paramArrayList1;
                localObject34 = localObject2;
                break;
                paramArrayList2 = (TLRPC.TL_updateChatUserTyping)localObject21;
                k = paramArrayList2.chat_id;
                i = paramArrayList2.user_id;
                localObject21 = paramArrayList2.action;
                break label3115;
                label3457:
                m++;
                break label3266;
                label3463:
                paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
                if (localObject14 == null)
                {
                  paramArrayList2 = new ArrayList();
                  this.printingUsers.put(Long.valueOf(l2), paramArrayList2);
                }
                k = 0;
                localObject14 = paramArrayList2.iterator();
                do
                {
                  m = k;
                  bool2 = bool1;
                  if (!((Iterator)localObject14).hasNext()) {
                    break;
                  }
                  localObject17 = (PrintingUser)((Iterator)localObject14).next();
                } while (((PrintingUser)localObject17).userId != i);
                m = 1;
                ((PrintingUser)localObject17).lastTime = l1;
                if (((PrintingUser)localObject17).action.getClass() != localObject21.getClass()) {
                  bool1 = true;
                }
                ((PrintingUser)localObject17).action = ((TLRPC.SendMessageAction)localObject21);
                bool2 = bool1;
                if (m == 0)
                {
                  localObject14 = new PrintingUser();
                  ((PrintingUser)localObject14).userId = i;
                  ((PrintingUser)localObject14).lastTime = l1;
                  ((PrintingUser)localObject14).action = ((TLRPC.SendMessageAction)localObject21);
                  paramArrayList2.add(localObject14);
                  bool2 = true;
                }
              }
            }
            else if ((localObject21 instanceof TLRPC.TL_updateChatParticipants))
            {
              localObject14 = (TLRPC.TL_updateChatParticipants)localObject21;
              m = j | 0x20;
              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject13;
              if (localObject13 == null) {
                paramArrayList2 = new ArrayList();
              }
              paramArrayList2.add(((TLRPC.TL_updateChatParticipants)localObject14).participants);
              localObject23 = localObject6;
              localObject24 = paramArrayList2;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
            }
            else if ((localObject21 instanceof TLRPC.TL_updateUserStatus))
            {
              m = j | 0x4;
              paramArrayList2 = paramArrayList1;
              if (paramArrayList1 == null) {
                paramArrayList2 = new ArrayList();
              }
              paramArrayList2.add(localObject21);
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              localObject34 = localObject2;
            }
            else if ((localObject21 instanceof TLRPC.TL_updateUserName))
            {
              m = j | 0x1;
              paramArrayList2 = paramArrayList1;
              if (paramArrayList1 == null) {
                paramArrayList2 = new ArrayList();
              }
              paramArrayList2.add(localObject21);
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              localObject34 = localObject2;
            }
            else if ((localObject21 instanceof TLRPC.TL_updateUserPhoto))
            {
              paramArrayList2 = (TLRPC.TL_updateUserPhoto)localObject21;
              m = j | 0x2;
              MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(paramArrayList2.user_id);
              paramArrayList2 = paramArrayList1;
              if (paramArrayList1 == null) {
                paramArrayList2 = new ArrayList();
              }
              paramArrayList2.add(localObject21);
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              localObject34 = localObject2;
            }
            else if ((localObject21 instanceof TLRPC.TL_updateUserPhone))
            {
              m = j | 0x400;
              paramArrayList2 = paramArrayList1;
              if (paramArrayList1 == null) {
                paramArrayList2 = new ArrayList();
              }
              paramArrayList2.add(localObject21);
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              localObject34 = localObject2;
            }
            else if ((localObject21 instanceof TLRPC.TL_updateContactRegistered))
            {
              localObject21 = (TLRPC.TL_updateContactRegistered)localObject21;
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
              if (this.enableJoined)
              {
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                paramArrayList2 = paramArrayList1;
                localObject34 = localObject2;
                if (((ConcurrentHashMap)localObject18).containsKey(Integer.valueOf(((TLRPC.TL_updateContactRegistered)localObject21).user_id)))
                {
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject16;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                  if (!MessagesStorage.getInstance(this.currentAccount).isDialogHasMessages(((TLRPC.TL_updateContactRegistered)localObject21).user_id))
                  {
                    localObject19 = new TLRPC.TL_messageService();
                    ((TLRPC.TL_messageService)localObject19).action = new TLRPC.TL_messageActionUserJoined();
                    m = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                    ((TLRPC.TL_messageService)localObject19).id = m;
                    ((TLRPC.TL_messageService)localObject19).local_id = m;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    ((TLRPC.TL_messageService)localObject19).unread = false;
                    ((TLRPC.TL_messageService)localObject19).flags = 256;
                    ((TLRPC.TL_messageService)localObject19).date = ((TLRPC.TL_updateContactRegistered)localObject21).date;
                    ((TLRPC.TL_messageService)localObject19).from_id = ((TLRPC.TL_updateContactRegistered)localObject21).user_id;
                    ((TLRPC.TL_messageService)localObject19).to_id = new TLRPC.TL_peerUser();
                    ((TLRPC.TL_messageService)localObject19).to_id.user_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    ((TLRPC.TL_messageService)localObject19).dialog_id = ((TLRPC.TL_updateContactRegistered)localObject21).user_id;
                    localObject17 = localObject4;
                    if (localObject4 == null) {
                      localObject17 = new ArrayList();
                    }
                    ((ArrayList)localObject17).add(localObject19);
                    localObject4 = new MessageObject(this.currentAccount, (TLRPC.Message)localObject19, (AbstractMap)localObject18, (AbstractMap)localObject20, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_messageService)localObject19).dialog_id)));
                    localObject14 = localObject1;
                    if (localObject1 == null) {
                      localObject14 = new LongSparseArray();
                    }
                    localObject1 = (ArrayList)((LongSparseArray)localObject14).get(((TLRPC.TL_messageService)localObject19).dialog_id);
                    paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject1;
                    if (localObject1 == null)
                    {
                      paramArrayList2 = new ArrayList();
                      ((LongSparseArray)localObject14).put(((TLRPC.TL_messageService)localObject19).dialog_id, paramArrayList2);
                    }
                    paramArrayList2.add(localObject4);
                    localObject23 = localObject6;
                    localObject24 = localObject13;
                    localObject25 = localObject12;
                    localObject26 = localObject16;
                    localObject27 = localObject11;
                    localObject28 = localObject5;
                    m = j;
                    localObject29 = localObject10;
                    localObject30 = localObject9;
                    localObject31 = localObject7;
                    localObject32 = localObject8;
                    bool2 = bool1;
                    localObject19 = localObject3;
                    localObject33 = localObject15;
                    paramArrayList2 = paramArrayList1;
                    localObject34 = localObject2;
                  }
                }
              }
            }
            else if ((localObject21 instanceof TLRPC.TL_updateContactLink))
            {
              localObject22 = (TLRPC.TL_updateContactLink)localObject21;
              localObject21 = localObject16;
              if (localObject16 == null) {
                localObject21 = new ArrayList();
              }
              if ((((TLRPC.TL_updateContactLink)localObject22).my_link instanceof TLRPC.TL_contactLinkContact))
              {
                m = ((ArrayList)localObject21).indexOf(Integer.valueOf(-((TLRPC.TL_updateContactLink)localObject22).user_id));
                if (m != -1) {
                  ((ArrayList)localObject21).remove(m);
                }
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject21;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                paramArrayList2 = paramArrayList1;
                localObject34 = localObject2;
                if (!((ArrayList)localObject21).contains(Integer.valueOf(((TLRPC.TL_updateContactLink)localObject22).user_id)))
                {
                  ((ArrayList)localObject21).add(Integer.valueOf(((TLRPC.TL_updateContactLink)localObject22).user_id));
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject21;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                }
              }
              else
              {
                m = ((ArrayList)localObject21).indexOf(Integer.valueOf(((TLRPC.TL_updateContactLink)localObject22).user_id));
                if (m != -1) {
                  ((ArrayList)localObject21).remove(m);
                }
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject21;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                paramArrayList2 = paramArrayList1;
                localObject34 = localObject2;
                if (!((ArrayList)localObject21).contains(Integer.valueOf(((TLRPC.TL_updateContactLink)localObject22).user_id)))
                {
                  ((ArrayList)localObject21).add(Integer.valueOf(-((TLRPC.TL_updateContactLink)localObject22).user_id));
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject21;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                }
              }
            }
            else if ((localObject21 instanceof TLRPC.TL_updateNewEncryptedMessage))
            {
              localObject22 = SecretChatHelper.getInstance(this.currentAccount).decryptMessage(((TLRPC.TL_updateNewEncryptedMessage)localObject21).message);
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
              if (localObject22 != null)
              {
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                paramArrayList2 = paramArrayList1;
                localObject34 = localObject2;
                if (!((ArrayList)localObject22).isEmpty())
                {
                  l2 = ((TLRPC.TL_updateNewEncryptedMessage)localObject21).message.chat_id << 32;
                  localObject21 = localObject1;
                  if (localObject1 == null) {
                    localObject21 = new LongSparseArray();
                  }
                  paramArrayList2 = (ArrayList)((LongSparseArray)localObject21).get(l2);
                  localObject1 = paramArrayList2;
                  if (paramArrayList2 == null)
                  {
                    localObject1 = new ArrayList();
                    ((LongSparseArray)localObject21).put(l2, localObject1);
                  }
                  i = 0;
                  k = ((ArrayList)localObject22).size();
                  for (;;)
                  {
                    localObject23 = localObject6;
                    localObject24 = localObject13;
                    localObject25 = localObject12;
                    localObject26 = localObject16;
                    localObject27 = localObject11;
                    localObject28 = localObject5;
                    m = j;
                    localObject29 = localObject10;
                    localObject30 = localObject9;
                    localObject31 = localObject7;
                    localObject32 = localObject8;
                    localObject14 = localObject21;
                    localObject17 = localObject4;
                    bool2 = bool1;
                    localObject19 = localObject3;
                    localObject33 = localObject15;
                    paramArrayList2 = paramArrayList1;
                    localObject34 = localObject2;
                    if (i >= k) {
                      break;
                    }
                    localObject14 = (TLRPC.Message)((ArrayList)localObject22).get(i);
                    ImageLoader.saveMessageThumbs((TLRPC.Message)localObject14);
                    paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject4;
                    if (localObject4 == null) {
                      paramArrayList2 = new ArrayList();
                    }
                    paramArrayList2.add(localObject14);
                    localObject4 = new MessageObject(this.currentAccount, (TLRPC.Message)localObject14, (AbstractMap)localObject18, (AbstractMap)localObject20, this.createdDialogIds.contains(Long.valueOf(l2)));
                    ((ArrayList)localObject1).add(localObject4);
                    localObject14 = localObject3;
                    if (localObject3 == null) {
                      localObject14 = new ArrayList();
                    }
                    ((ArrayList)localObject14).add(localObject4);
                    i++;
                    localObject4 = paramArrayList2;
                    localObject3 = localObject14;
                  }
                }
              }
            }
            else if ((localObject21 instanceof TLRPC.TL_updateEncryptedChatTyping))
            {
              localObject22 = (TLRPC.TL_updateEncryptedChatTyping)localObject21;
              localObject21 = getEncryptedChatDB(((TLRPC.TL_updateEncryptedChatTyping)localObject22).chat_id, true);
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
              if (localObject21 != null)
              {
                l2 = ((TLRPC.TL_updateEncryptedChatTyping)localObject22).chat_id << 32;
                localObject14 = (ArrayList)this.printingUsers.get(Long.valueOf(l2));
                paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
                if (localObject14 == null)
                {
                  paramArrayList2 = new ArrayList();
                  this.printingUsers.put(Long.valueOf(l2), paramArrayList2);
                }
                k = 0;
                i = 0;
                i4 = paramArrayList2.size();
                for (;;)
                {
                  m = k;
                  if (i < i4)
                  {
                    localObject14 = (PrintingUser)paramArrayList2.get(i);
                    if (((PrintingUser)localObject14).userId == ((TLRPC.EncryptedChat)localObject21).user_id)
                    {
                      m = 1;
                      ((PrintingUser)localObject14).lastTime = l1;
                      ((PrintingUser)localObject14).action = new TLRPC.TL_sendMessageTypingAction();
                    }
                  }
                  else
                  {
                    if (m == 0)
                    {
                      localObject14 = new PrintingUser();
                      ((PrintingUser)localObject14).userId = ((TLRPC.EncryptedChat)localObject21).user_id;
                      ((PrintingUser)localObject14).lastTime = l1;
                      ((PrintingUser)localObject14).action = new TLRPC.TL_sendMessageTypingAction();
                      paramArrayList2.add(localObject14);
                      bool1 = true;
                    }
                    this.onlinePrivacy.put(Integer.valueOf(((TLRPC.EncryptedChat)localObject21).user_id), Integer.valueOf(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
                    localObject23 = localObject6;
                    localObject24 = localObject13;
                    localObject25 = localObject12;
                    localObject26 = localObject16;
                    localObject27 = localObject11;
                    localObject28 = localObject5;
                    m = j;
                    localObject29 = localObject10;
                    localObject30 = localObject9;
                    localObject31 = localObject7;
                    localObject32 = localObject8;
                    localObject14 = localObject1;
                    localObject17 = localObject4;
                    bool2 = bool1;
                    localObject19 = localObject3;
                    localObject33 = localObject15;
                    paramArrayList2 = paramArrayList1;
                    localObject34 = localObject2;
                    break;
                  }
                  i++;
                }
              }
            }
            else if ((localObject21 instanceof TLRPC.TL_updateEncryptedMessagesRead))
            {
              paramArrayList2 = (TLRPC.TL_updateEncryptedMessagesRead)localObject21;
              localObject14 = localObject10;
              if (localObject10 == null) {
                localObject14 = new SparseIntArray();
              }
              ((SparseIntArray)localObject14).put(paramArrayList2.chat_id, Math.max(paramArrayList2.max_date, paramArrayList2.date));
              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject15;
              if (localObject15 == null) {
                paramArrayList2 = new ArrayList();
              }
              paramArrayList2.add((TLRPC.TL_updateEncryptedMessagesRead)localObject21);
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject14;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = paramArrayList2;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
            }
            else if ((localObject21 instanceof TLRPC.TL_updateChatParticipantAdd))
            {
              paramArrayList2 = (TLRPC.TL_updateChatParticipantAdd)localObject21;
              MessagesStorage.getInstance(this.currentAccount).updateChatInfo(paramArrayList2.chat_id, paramArrayList2.user_id, 0, paramArrayList2.inviter_id, paramArrayList2.version);
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
            }
            else if ((localObject21 instanceof TLRPC.TL_updateChatParticipantDelete))
            {
              paramArrayList2 = (TLRPC.TL_updateChatParticipantDelete)localObject21;
              MessagesStorage.getInstance(this.currentAccount).updateChatInfo(paramArrayList2.chat_id, paramArrayList2.user_id, 1, 0, paramArrayList2.version);
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
            }
            else if (((localObject21 instanceof TLRPC.TL_updateDcOptions)) || ((localObject21 instanceof TLRPC.TL_updateConfig)))
            {
              ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
            }
            else if ((localObject21 instanceof TLRPC.TL_updateEncryption))
            {
              SecretChatHelper.getInstance(this.currentAccount).processUpdateEncryption((TLRPC.TL_updateEncryption)localObject21, (ConcurrentHashMap)localObject18);
              localObject23 = localObject6;
              localObject24 = localObject13;
              localObject25 = localObject12;
              localObject26 = localObject16;
              localObject27 = localObject11;
              localObject28 = localObject5;
              m = j;
              localObject29 = localObject10;
              localObject30 = localObject9;
              localObject31 = localObject7;
              localObject32 = localObject8;
              localObject14 = localObject1;
              localObject17 = localObject4;
              bool2 = bool1;
              localObject19 = localObject3;
              localObject33 = localObject15;
              paramArrayList2 = paramArrayList1;
              localObject34 = localObject2;
            }
            else
            {
              if ((localObject21 instanceof TLRPC.TL_updateUserBlocked))
              {
                paramArrayList2 = (TLRPC.TL_updateUserBlocked)localObject21;
                if (paramArrayList2.blocked)
                {
                  localObject14 = new ArrayList();
                  ((ArrayList)localObject14).add(Integer.valueOf(paramArrayList2.user_id));
                  MessagesStorage.getInstance(this.currentAccount).putBlockedUsers((ArrayList)localObject14, false);
                }
                for (;;)
                {
                  MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
                  {
                    public void run()
                    {
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          if (MessagesController.129.this.val$finalUpdate.blocked) {
                            if (!MessagesController.this.blockedUsers.contains(Integer.valueOf(MessagesController.129.this.val$finalUpdate.user_id))) {
                              MessagesController.this.blockedUsers.add(Integer.valueOf(MessagesController.129.this.val$finalUpdate.user_id));
                            }
                          }
                          for (;;)
                          {
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
                            return;
                            MessagesController.this.blockedUsers.remove(Integer.valueOf(MessagesController.129.this.val$finalUpdate.user_id));
                          }
                        }
                      });
                    }
                  });
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject16;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                  break;
                  MessagesStorage.getInstance(this.currentAccount).deleteBlockedUser(paramArrayList2.user_id);
                }
              }
              if ((localObject21 instanceof TLRPC.TL_updateNotifySettings))
              {
                paramArrayList2 = paramArrayList1;
                if (paramArrayList1 == null) {
                  paramArrayList2 = new ArrayList();
                }
                paramArrayList2.add(localObject21);
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                localObject34 = localObject2;
              }
              else if ((localObject21 instanceof TLRPC.TL_updateServiceNotification))
              {
                localObject21 = (TLRPC.TL_updateServiceNotification)localObject21;
                if ((((TLRPC.TL_updateServiceNotification)localObject21).popup) && (((TLRPC.TL_updateServiceNotification)localObject21).message != null) && (((TLRPC.TL_updateServiceNotification)localObject21).message.length() > 0)) {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(2), localObject21.message });
                    }
                  });
                }
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                paramArrayList2 = paramArrayList1;
                localObject34 = localObject2;
                if ((((TLRPC.TL_updateServiceNotification)localObject21).flags & 0x2) != 0)
                {
                  localObject19 = new TLRPC.TL_message();
                  m = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                  ((TLRPC.TL_message)localObject19).id = m;
                  ((TLRPC.TL_message)localObject19).local_id = m;
                  UserConfig.getInstance(this.currentAccount).saveConfig(false);
                  ((TLRPC.TL_message)localObject19).unread = true;
                  ((TLRPC.TL_message)localObject19).flags = 256;
                  if (((TLRPC.TL_updateServiceNotification)localObject21).inbox_date != 0) {}
                  for (((TLRPC.TL_message)localObject19).date = ((TLRPC.TL_updateServiceNotification)localObject21).inbox_date;; ((TLRPC.TL_message)localObject19).date = ((int)(System.currentTimeMillis() / 1000L)))
                  {
                    ((TLRPC.TL_message)localObject19).from_id = 777000;
                    ((TLRPC.TL_message)localObject19).to_id = new TLRPC.TL_peerUser();
                    ((TLRPC.TL_message)localObject19).to_id.user_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    ((TLRPC.TL_message)localObject19).dialog_id = 777000L;
                    if (((TLRPC.TL_updateServiceNotification)localObject21).media != null)
                    {
                      ((TLRPC.TL_message)localObject19).media = ((TLRPC.TL_updateServiceNotification)localObject21).media;
                      ((TLRPC.TL_message)localObject19).flags |= 0x200;
                    }
                    ((TLRPC.TL_message)localObject19).message = ((TLRPC.TL_updateServiceNotification)localObject21).message;
                    if (((TLRPC.TL_updateServiceNotification)localObject21).entities != null) {
                      ((TLRPC.TL_message)localObject19).entities = ((TLRPC.TL_updateServiceNotification)localObject21).entities;
                    }
                    localObject17 = localObject4;
                    if (localObject4 == null) {
                      localObject17 = new ArrayList();
                    }
                    ((ArrayList)localObject17).add(localObject19);
                    localObject4 = new MessageObject(this.currentAccount, (TLRPC.Message)localObject19, (AbstractMap)localObject18, (AbstractMap)localObject20, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_message)localObject19).dialog_id)));
                    localObject14 = localObject1;
                    if (localObject1 == null) {
                      localObject14 = new LongSparseArray();
                    }
                    localObject1 = (ArrayList)((LongSparseArray)localObject14).get(((TLRPC.TL_message)localObject19).dialog_id);
                    paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject1;
                    if (localObject1 == null)
                    {
                      paramArrayList2 = new ArrayList();
                      ((LongSparseArray)localObject14).put(((TLRPC.TL_message)localObject19).dialog_id, paramArrayList2);
                    }
                    paramArrayList2.add(localObject4);
                    localObject19 = localObject3;
                    if (localObject3 == null) {
                      localObject19 = new ArrayList();
                    }
                    ((ArrayList)localObject19).add(localObject4);
                    localObject23 = localObject6;
                    localObject24 = localObject13;
                    localObject25 = localObject12;
                    localObject26 = localObject16;
                    localObject27 = localObject11;
                    localObject28 = localObject5;
                    m = j;
                    localObject29 = localObject10;
                    localObject30 = localObject9;
                    localObject31 = localObject7;
                    localObject32 = localObject8;
                    bool2 = bool1;
                    localObject33 = localObject15;
                    paramArrayList2 = paramArrayList1;
                    localObject34 = localObject2;
                    break;
                  }
                }
              }
              else if ((localObject21 instanceof TLRPC.TL_updateDialogPinned))
              {
                paramArrayList2 = paramArrayList1;
                if (paramArrayList1 == null) {
                  paramArrayList2 = new ArrayList();
                }
                paramArrayList2.add(localObject21);
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                localObject34 = localObject2;
              }
              else if ((localObject21 instanceof TLRPC.TL_updatePinnedDialogs))
              {
                paramArrayList2 = paramArrayList1;
                if (paramArrayList1 == null) {
                  paramArrayList2 = new ArrayList();
                }
                paramArrayList2.add(localObject21);
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                localObject34 = localObject2;
              }
              else if ((localObject21 instanceof TLRPC.TL_updatePrivacy))
              {
                paramArrayList2 = paramArrayList1;
                if (paramArrayList1 == null) {
                  paramArrayList2 = new ArrayList();
                }
                paramArrayList2.add(localObject21);
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                localObject34 = localObject2;
              }
              else if ((localObject21 instanceof TLRPC.TL_updateWebPage))
              {
                paramArrayList2 = (TLRPC.TL_updateWebPage)localObject21;
                localObject34 = localObject2;
                if (localObject2 == null) {
                  localObject34 = new LongSparseArray();
                }
                ((LongSparseArray)localObject34).put(paramArrayList2.webpage.id, paramArrayList2.webpage);
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                paramArrayList2 = paramArrayList1;
              }
              else if ((localObject21 instanceof TLRPC.TL_updateChannelWebPage))
              {
                paramArrayList2 = (TLRPC.TL_updateChannelWebPage)localObject21;
                localObject34 = localObject2;
                if (localObject2 == null) {
                  localObject34 = new LongSparseArray();
                }
                ((LongSparseArray)localObject34).put(paramArrayList2.webpage.id, paramArrayList2.webpage);
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                paramArrayList2 = paramArrayList1;
              }
              else if ((localObject21 instanceof TLRPC.TL_updateChannelTooLong))
              {
                localObject22 = (TLRPC.TL_updateChannelTooLong)localObject21;
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.d(localObject21 + " channelId = " + ((TLRPC.TL_updateChannelTooLong)localObject22).channel_id);
                }
                m = this.channelsPts.get(((TLRPC.TL_updateChannelTooLong)localObject22).channel_id);
                i = m;
                if (m == 0)
                {
                  m = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(((TLRPC.TL_updateChannelTooLong)localObject22).channel_id);
                  if (m != 0) {
                    break label8450;
                  }
                  localObject14 = (TLRPC.Chat)((ConcurrentHashMap)localObject20).get(Integer.valueOf(((TLRPC.TL_updateChannelTooLong)localObject22).channel_id));
                  if (localObject14 != null)
                  {
                    paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
                    if (!((TLRPC.Chat)localObject14).min) {}
                  }
                  else
                  {
                    paramArrayList2 = getChat(Integer.valueOf(((TLRPC.TL_updateChannelTooLong)localObject22).channel_id));
                  }
                  if (paramArrayList2 != null)
                  {
                    localObject14 = paramArrayList2;
                    if (!paramArrayList2.min) {}
                  }
                  else
                  {
                    localObject14 = MessagesStorage.getInstance(this.currentAccount).getChatSync(((TLRPC.TL_updateChannelTooLong)localObject22).channel_id);
                    putChat((TLRPC.Chat)localObject14, true);
                  }
                  i = m;
                  if (localObject14 != null)
                  {
                    i = m;
                    if (!((TLRPC.Chat)localObject14).min) {
                      loadUnknownChannel((TLRPC.Chat)localObject14, 0L);
                    }
                  }
                }
                for (i = m;; i = m)
                {
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject16;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                  if (i == 0) {
                    break;
                  }
                  if ((((TLRPC.TL_updateChannelTooLong)localObject22).flags & 0x1) == 0) {
                    break label8471;
                  }
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject16;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                  if (((TLRPC.TL_updateChannelTooLong)localObject22).pts <= i) {
                    break;
                  }
                  getChannelDifference(((TLRPC.TL_updateChannelTooLong)localObject22).channel_id);
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject16;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                  break;
                  label8450:
                  this.channelsPts.put(((TLRPC.TL_updateChannelTooLong)localObject22).channel_id, m);
                }
                label8471:
                getChannelDifference(((TLRPC.TL_updateChannelTooLong)localObject22).channel_id);
                localObject23 = localObject6;
                localObject24 = localObject13;
                localObject25 = localObject12;
                localObject26 = localObject16;
                localObject27 = localObject11;
                localObject28 = localObject5;
                m = j;
                localObject29 = localObject10;
                localObject30 = localObject9;
                localObject31 = localObject7;
                localObject32 = localObject8;
                localObject14 = localObject1;
                localObject17 = localObject4;
                bool2 = bool1;
                localObject19 = localObject3;
                localObject33 = localObject15;
                paramArrayList2 = paramArrayList1;
                localObject34 = localObject2;
              }
              else
              {
                long l4;
                if ((localObject21 instanceof TLRPC.TL_updateReadChannelInbox))
                {
                  localObject17 = (TLRPC.TL_updateReadChannelInbox)localObject21;
                  l4 = ((TLRPC.TL_updateReadChannelInbox)localObject17).max_id;
                  l3 = ((TLRPC.TL_updateReadChannelInbox)localObject17).channel_id;
                  l2 = -((TLRPC.TL_updateReadChannelInbox)localObject17).channel_id;
                  localObject31 = localObject7;
                  if (localObject7 == null) {
                    localObject31 = new SparseLongArray();
                  }
                  ((SparseLongArray)localObject31).put(-((TLRPC.TL_updateReadChannelInbox)localObject17).channel_id, l4 | l3 << 32);
                  localObject14 = (Integer)this.dialogs_read_inbox_max.get(Long.valueOf(l2));
                  paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
                  if (localObject14 == null) {
                    paramArrayList2 = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(false, l2));
                  }
                  this.dialogs_read_inbox_max.put(Long.valueOf(l2), Integer.valueOf(Math.max(paramArrayList2.intValue(), ((TLRPC.TL_updateReadChannelInbox)localObject17).max_id)));
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject16;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                }
                else if ((localObject21 instanceof TLRPC.TL_updateReadChannelOutbox))
                {
                  localObject17 = (TLRPC.TL_updateReadChannelOutbox)localObject21;
                  l4 = ((TLRPC.TL_updateReadChannelOutbox)localObject17).max_id;
                  l2 = ((TLRPC.TL_updateReadChannelOutbox)localObject17).channel_id;
                  l3 = -((TLRPC.TL_updateReadChannelOutbox)localObject17).channel_id;
                  localObject32 = localObject8;
                  if (localObject8 == null) {
                    localObject32 = new SparseLongArray();
                  }
                  ((SparseLongArray)localObject32).put(-((TLRPC.TL_updateReadChannelOutbox)localObject17).channel_id, l4 | l2 << 32);
                  localObject14 = (Integer)this.dialogs_read_outbox_max.get(Long.valueOf(l3));
                  paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
                  if (localObject14 == null) {
                    paramArrayList2 = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, l3));
                  }
                  this.dialogs_read_outbox_max.put(Long.valueOf(l3), Integer.valueOf(Math.max(paramArrayList2.intValue(), ((TLRPC.TL_updateReadChannelOutbox)localObject17).max_id)));
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject16;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                }
                else if ((localObject21 instanceof TLRPC.TL_updateDeleteChannelMessages))
                {
                  localObject17 = (TLRPC.TL_updateDeleteChannelMessages)localObject21;
                  if (BuildVars.LOGS_ENABLED) {
                    FileLog.d(localObject21 + " channelId = " + ((TLRPC.TL_updateDeleteChannelMessages)localObject17).channel_id);
                  }
                  localObject27 = localObject11;
                  if (localObject11 == null) {
                    localObject27 = new SparseArray();
                  }
                  localObject14 = (ArrayList)((SparseArray)localObject27).get(((TLRPC.TL_updateDeleteChannelMessages)localObject17).channel_id);
                  paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
                  if (localObject14 == null)
                  {
                    paramArrayList2 = new ArrayList();
                    ((SparseArray)localObject27).put(((TLRPC.TL_updateDeleteChannelMessages)localObject17).channel_id, paramArrayList2);
                  }
                  paramArrayList2.addAll(((TLRPC.TL_updateDeleteChannelMessages)localObject17).messages);
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject16;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                }
                else if ((localObject21 instanceof TLRPC.TL_updateChannel))
                {
                  if (BuildVars.LOGS_ENABLED)
                  {
                    paramArrayList2 = (TLRPC.TL_updateChannel)localObject21;
                    FileLog.d(localObject21 + " channelId = " + paramArrayList2.channel_id);
                  }
                  paramArrayList2 = paramArrayList1;
                  if (paramArrayList1 == null) {
                    paramArrayList2 = new ArrayList();
                  }
                  paramArrayList2.add(localObject21);
                  localObject23 = localObject6;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject16;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  localObject34 = localObject2;
                }
                else if ((localObject21 instanceof TLRPC.TL_updateChannelMessageViews))
                {
                  localObject19 = (TLRPC.TL_updateChannelMessageViews)localObject21;
                  if (BuildVars.LOGS_ENABLED) {
                    FileLog.d(localObject21 + " channelId = " + ((TLRPC.TL_updateChannelMessageViews)localObject19).channel_id);
                  }
                  paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject6;
                  if (localObject6 == null) {
                    paramArrayList2 = new SparseArray();
                  }
                  localObject17 = (SparseIntArray)paramArrayList2.get(((TLRPC.TL_updateChannelMessageViews)localObject19).channel_id);
                  localObject14 = localObject17;
                  if (localObject17 == null)
                  {
                    localObject14 = new SparseIntArray();
                    paramArrayList2.put(((TLRPC.TL_updateChannelMessageViews)localObject19).channel_id, localObject14);
                  }
                  ((SparseIntArray)localObject14).put(((TLRPC.TL_updateChannelMessageViews)localObject19).id, ((TLRPC.TL_updateChannelMessageViews)localObject19).views);
                  localObject23 = paramArrayList2;
                  localObject24 = localObject13;
                  localObject25 = localObject12;
                  localObject26 = localObject16;
                  localObject27 = localObject11;
                  localObject28 = localObject5;
                  m = j;
                  localObject29 = localObject10;
                  localObject30 = localObject9;
                  localObject31 = localObject7;
                  localObject32 = localObject8;
                  localObject14 = localObject1;
                  localObject17 = localObject4;
                  bool2 = bool1;
                  localObject19 = localObject3;
                  localObject33 = localObject15;
                  paramArrayList2 = paramArrayList1;
                  localObject34 = localObject2;
                }
                else
                {
                  if ((localObject21 instanceof TLRPC.TL_updateChatParticipantAdmin))
                  {
                    paramArrayList2 = (TLRPC.TL_updateChatParticipantAdmin)localObject21;
                    localObject14 = MessagesStorage.getInstance(this.currentAccount);
                    i = paramArrayList2.chat_id;
                    k = paramArrayList2.user_id;
                    if (paramArrayList2.is_admin) {}
                    for (m = 1;; m = 0)
                    {
                      ((MessagesStorage)localObject14).updateChatInfo(i, k, 2, m, paramArrayList2.version);
                      localObject23 = localObject6;
                      localObject24 = localObject13;
                      localObject25 = localObject12;
                      localObject26 = localObject16;
                      localObject27 = localObject11;
                      localObject28 = localObject5;
                      m = j;
                      localObject29 = localObject10;
                      localObject30 = localObject9;
                      localObject31 = localObject7;
                      localObject32 = localObject8;
                      localObject14 = localObject1;
                      localObject17 = localObject4;
                      bool2 = bool1;
                      localObject19 = localObject3;
                      localObject33 = localObject15;
                      paramArrayList2 = paramArrayList1;
                      localObject34 = localObject2;
                      break;
                    }
                  }
                  if ((localObject21 instanceof TLRPC.TL_updateChatAdmins))
                  {
                    paramArrayList2 = paramArrayList1;
                    if (paramArrayList1 == null) {
                      paramArrayList2 = new ArrayList();
                    }
                    paramArrayList2.add(localObject21);
                    localObject23 = localObject6;
                    localObject24 = localObject13;
                    localObject25 = localObject12;
                    localObject26 = localObject16;
                    localObject27 = localObject11;
                    localObject28 = localObject5;
                    m = j;
                    localObject29 = localObject10;
                    localObject30 = localObject9;
                    localObject31 = localObject7;
                    localObject32 = localObject8;
                    localObject14 = localObject1;
                    localObject17 = localObject4;
                    bool2 = bool1;
                    localObject19 = localObject3;
                    localObject33 = localObject15;
                    localObject34 = localObject2;
                  }
                  else if ((localObject21 instanceof TLRPC.TL_updateStickerSets))
                  {
                    paramArrayList2 = paramArrayList1;
                    if (paramArrayList1 == null) {
                      paramArrayList2 = new ArrayList();
                    }
                    paramArrayList2.add(localObject21);
                    localObject23 = localObject6;
                    localObject24 = localObject13;
                    localObject25 = localObject12;
                    localObject26 = localObject16;
                    localObject27 = localObject11;
                    localObject28 = localObject5;
                    m = j;
                    localObject29 = localObject10;
                    localObject30 = localObject9;
                    localObject31 = localObject7;
                    localObject32 = localObject8;
                    localObject14 = localObject1;
                    localObject17 = localObject4;
                    bool2 = bool1;
                    localObject19 = localObject3;
                    localObject33 = localObject15;
                    localObject34 = localObject2;
                  }
                  else if ((localObject21 instanceof TLRPC.TL_updateStickerSetsOrder))
                  {
                    paramArrayList2 = paramArrayList1;
                    if (paramArrayList1 == null) {
                      paramArrayList2 = new ArrayList();
                    }
                    paramArrayList2.add(localObject21);
                    localObject23 = localObject6;
                    localObject24 = localObject13;
                    localObject25 = localObject12;
                    localObject26 = localObject16;
                    localObject27 = localObject11;
                    localObject28 = localObject5;
                    m = j;
                    localObject29 = localObject10;
                    localObject30 = localObject9;
                    localObject31 = localObject7;
                    localObject32 = localObject8;
                    localObject14 = localObject1;
                    localObject17 = localObject4;
                    bool2 = bool1;
                    localObject19 = localObject3;
                    localObject33 = localObject15;
                    localObject34 = localObject2;
                  }
                  else if ((localObject21 instanceof TLRPC.TL_updateNewStickerSet))
                  {
                    paramArrayList2 = paramArrayList1;
                    if (paramArrayList1 == null) {
                      paramArrayList2 = new ArrayList();
                    }
                    paramArrayList2.add(localObject21);
                    localObject23 = localObject6;
                    localObject24 = localObject13;
                    localObject25 = localObject12;
                    localObject26 = localObject16;
                    localObject27 = localObject11;
                    localObject28 = localObject5;
                    m = j;
                    localObject29 = localObject10;
                    localObject30 = localObject9;
                    localObject31 = localObject7;
                    localObject32 = localObject8;
                    localObject14 = localObject1;
                    localObject17 = localObject4;
                    bool2 = bool1;
                    localObject19 = localObject3;
                    localObject33 = localObject15;
                    localObject34 = localObject2;
                  }
                  else if ((localObject21 instanceof TLRPC.TL_updateDraftMessage))
                  {
                    paramArrayList2 = paramArrayList1;
                    if (paramArrayList1 == null) {
                      paramArrayList2 = new ArrayList();
                    }
                    paramArrayList2.add(localObject21);
                    localObject23 = localObject6;
                    localObject24 = localObject13;
                    localObject25 = localObject12;
                    localObject26 = localObject16;
                    localObject27 = localObject11;
                    localObject28 = localObject5;
                    m = j;
                    localObject29 = localObject10;
                    localObject30 = localObject9;
                    localObject31 = localObject7;
                    localObject32 = localObject8;
                    localObject14 = localObject1;
                    localObject17 = localObject4;
                    bool2 = bool1;
                    localObject19 = localObject3;
                    localObject33 = localObject15;
                    localObject34 = localObject2;
                  }
                  else if ((localObject21 instanceof TLRPC.TL_updateSavedGifs))
                  {
                    paramArrayList2 = paramArrayList1;
                    if (paramArrayList1 == null) {
                      paramArrayList2 = new ArrayList();
                    }
                    paramArrayList2.add(localObject21);
                    localObject23 = localObject6;
                    localObject24 = localObject13;
                    localObject25 = localObject12;
                    localObject26 = localObject16;
                    localObject27 = localObject11;
                    localObject28 = localObject5;
                    m = j;
                    localObject29 = localObject10;
                    localObject30 = localObject9;
                    localObject31 = localObject7;
                    localObject32 = localObject8;
                    localObject14 = localObject1;
                    localObject17 = localObject4;
                    bool2 = bool1;
                    localObject19 = localObject3;
                    localObject33 = localObject15;
                    localObject34 = localObject2;
                  }
                  else
                  {
                    if (((localObject21 instanceof TLRPC.TL_updateEditChannelMessage)) || ((localObject21 instanceof TLRPC.TL_updateEditMessage)))
                    {
                      i = UserConfig.getInstance(this.currentAccount).getClientUserId();
                      if ((localObject21 instanceof TLRPC.TL_updateEditChannelMessage))
                      {
                        localObject19 = ((TLRPC.TL_updateEditChannelMessage)localObject21).message;
                        localObject14 = (TLRPC.Chat)((ConcurrentHashMap)localObject20).get(Integer.valueOf(((TLRPC.Message)localObject19).to_id.channel_id));
                        paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject14;
                        if (localObject14 == null) {
                          paramArrayList2 = getChat(Integer.valueOf(((TLRPC.Message)localObject19).to_id.channel_id));
                        }
                        localObject17 = paramArrayList2;
                        if (paramArrayList2 == null)
                        {
                          localObject17 = MessagesStorage.getInstance(this.currentAccount).getChatSync(((TLRPC.Message)localObject19).to_id.channel_id);
                          putChat((TLRPC.Chat)localObject17, true);
                        }
                        localObject14 = localObject19;
                        if (localObject17 != null)
                        {
                          localObject14 = localObject19;
                          if (((TLRPC.Chat)localObject17).megagroup)
                          {
                            ((TLRPC.Message)localObject19).flags |= 0x80000000;
                            localObject14 = localObject19;
                          }
                        }
                        label10423:
                        if ((!((TLRPC.Message)localObject14).out) && (((TLRPC.Message)localObject14).from_id == UserConfig.getInstance(this.currentAccount).getClientUserId())) {
                          ((TLRPC.Message)localObject14).out = true;
                        }
                        if (!paramBoolean)
                        {
                          m = 0;
                          k = ((TLRPC.Message)localObject14).entities.size();
                        }
                      }
                      else
                      {
                        for (;;)
                        {
                          if (m >= k) {
                            break label10671;
                          }
                          paramArrayList2 = (TLRPC.MessageEntity)((TLRPC.Message)localObject14).entities.get(m);
                          if ((paramArrayList2 instanceof TLRPC.TL_messageEntityMentionName))
                          {
                            i4 = ((TLRPC.TL_messageEntityMentionName)paramArrayList2).user_id;
                            localObject17 = (TLRPC.User)((ConcurrentHashMap)localObject18).get(Integer.valueOf(i4));
                            if (localObject17 != null)
                            {
                              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject17;
                              if (!((TLRPC.User)localObject17).min) {}
                            }
                            else
                            {
                              paramArrayList2 = getUser(Integer.valueOf(i4));
                            }
                            if (paramArrayList2 != null)
                            {
                              localObject17 = paramArrayList2;
                              if (!paramArrayList2.min) {}
                            }
                            else
                            {
                              localObject17 = MessagesStorage.getInstance(this.currentAccount).getUserSync(i4);
                              paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject17;
                              if (localObject17 != null)
                              {
                                paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject17;
                                if (((TLRPC.User)localObject17).min) {
                                  paramArrayList2 = null;
                                }
                              }
                              putUser(paramArrayList2, true);
                              localObject17 = paramArrayList2;
                            }
                            if (localObject17 == null)
                            {
                              paramBoolean = false;
                              break;
                              paramArrayList2 = ((TLRPC.TL_updateEditMessage)localObject21).message;
                              localObject14 = paramArrayList2;
                              if (paramArrayList2.dialog_id != i) {
                                break label10423;
                              }
                              paramArrayList2.unread = false;
                              paramArrayList2.media_unread = false;
                              paramArrayList2.out = true;
                              localObject14 = paramArrayList2;
                              break label10423;
                            }
                          }
                          m++;
                        }
                      }
                      label10671:
                      if (((TLRPC.Message)localObject14).to_id.chat_id != 0)
                      {
                        ((TLRPC.Message)localObject14).dialog_id = (-((TLRPC.Message)localObject14).to_id.chat_id);
                        label10697:
                        if (!((TLRPC.Message)localObject14).out) {
                          break label11119;
                        }
                        paramArrayList2 = this.dialogs_read_outbox_max;
                        label10710:
                        localObject19 = (Integer)paramArrayList2.get(Long.valueOf(((TLRPC.Message)localObject14).dialog_id));
                        localObject17 = localObject19;
                        if (localObject19 == null)
                        {
                          localObject17 = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(((TLRPC.Message)localObject14).out, ((TLRPC.Message)localObject14).dialog_id));
                          paramArrayList2.put(Long.valueOf(((TLRPC.Message)localObject14).dialog_id), localObject17);
                        }
                        if (((Integer)localObject17).intValue() >= ((TLRPC.Message)localObject14).id) {
                          break label11127;
                        }
                      }
                      label11119:
                      label11127:
                      for (bool2 = true;; bool2 = false)
                      {
                        ((TLRPC.Message)localObject14).unread = bool2;
                        if (((TLRPC.Message)localObject14).dialog_id == i)
                        {
                          ((TLRPC.Message)localObject14).out = true;
                          ((TLRPC.Message)localObject14).unread = false;
                          ((TLRPC.Message)localObject14).media_unread = false;
                        }
                        if ((((TLRPC.Message)localObject14).out) && (((TLRPC.Message)localObject14).message == null))
                        {
                          ((TLRPC.Message)localObject14).message = "";
                          ((TLRPC.Message)localObject14).attachPath = "";
                        }
                        ImageLoader.saveMessageThumbs((TLRPC.Message)localObject14);
                        localObject27 = new MessageObject(this.currentAccount, (TLRPC.Message)localObject14, (AbstractMap)localObject18, (AbstractMap)localObject20, this.createdDialogIds.contains(Long.valueOf(((TLRPC.Message)localObject14).dialog_id)));
                        paramArrayList2 = (ArrayList<TLRPC.Chat>)localObject5;
                        if (localObject5 == null) {
                          paramArrayList2 = new LongSparseArray();
                        }
                        localObject19 = (ArrayList)paramArrayList2.get(((TLRPC.Message)localObject14).dialog_id);
                        localObject17 = localObject19;
                        if (localObject19 == null)
                        {
                          localObject17 = new ArrayList();
                          paramArrayList2.put(((TLRPC.Message)localObject14).dialog_id, localObject17);
                        }
                        ((ArrayList)localObject17).add(localObject27);
                        localObject23 = localObject6;
                        localObject24 = localObject13;
                        localObject25 = localObject12;
                        localObject26 = localObject16;
                        localObject27 = localObject11;
                        localObject28 = paramArrayList2;
                        m = j;
                        localObject29 = localObject10;
                        localObject30 = localObject9;
                        localObject31 = localObject7;
                        localObject32 = localObject8;
                        localObject14 = localObject1;
                        localObject17 = localObject4;
                        bool2 = bool1;
                        localObject19 = localObject3;
                        localObject33 = localObject15;
                        paramArrayList2 = paramArrayList1;
                        localObject34 = localObject2;
                        break;
                        if (((TLRPC.Message)localObject14).to_id.channel_id != 0)
                        {
                          ((TLRPC.Message)localObject14).dialog_id = (-((TLRPC.Message)localObject14).to_id.channel_id);
                          break label10697;
                        }
                        if (((TLRPC.Message)localObject14).to_id.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                          ((TLRPC.Message)localObject14).to_id.user_id = ((TLRPC.Message)localObject14).from_id;
                        }
                        ((TLRPC.Message)localObject14).dialog_id = ((TLRPC.Message)localObject14).to_id.user_id;
                        break label10697;
                        paramArrayList2 = this.dialogs_read_inbox_max;
                        break label10710;
                      }
                    }
                    if ((localObject21 instanceof TLRPC.TL_updateChannelPinnedMessage))
                    {
                      paramArrayList2 = (TLRPC.TL_updateChannelPinnedMessage)localObject21;
                      if (BuildVars.LOGS_ENABLED) {
                        FileLog.d(localObject21 + " channelId = " + paramArrayList2.channel_id);
                      }
                      MessagesStorage.getInstance(this.currentAccount).updateChannelPinnedMessage(paramArrayList2.channel_id, paramArrayList2.id);
                      localObject23 = localObject6;
                      localObject24 = localObject13;
                      localObject25 = localObject12;
                      localObject26 = localObject16;
                      localObject27 = localObject11;
                      localObject28 = localObject5;
                      m = j;
                      localObject29 = localObject10;
                      localObject30 = localObject9;
                      localObject31 = localObject7;
                      localObject32 = localObject8;
                      localObject14 = localObject1;
                      localObject17 = localObject4;
                      bool2 = bool1;
                      localObject19 = localObject3;
                      localObject33 = localObject15;
                      paramArrayList2 = paramArrayList1;
                      localObject34 = localObject2;
                    }
                    else if ((localObject21 instanceof TLRPC.TL_updateReadFeaturedStickers))
                    {
                      paramArrayList2 = paramArrayList1;
                      if (paramArrayList1 == null) {
                        paramArrayList2 = new ArrayList();
                      }
                      paramArrayList2.add(localObject21);
                      localObject23 = localObject6;
                      localObject24 = localObject13;
                      localObject25 = localObject12;
                      localObject26 = localObject16;
                      localObject27 = localObject11;
                      localObject28 = localObject5;
                      m = j;
                      localObject29 = localObject10;
                      localObject30 = localObject9;
                      localObject31 = localObject7;
                      localObject32 = localObject8;
                      localObject14 = localObject1;
                      localObject17 = localObject4;
                      bool2 = bool1;
                      localObject19 = localObject3;
                      localObject33 = localObject15;
                      localObject34 = localObject2;
                    }
                    else if ((localObject21 instanceof TLRPC.TL_updatePhoneCall))
                    {
                      paramArrayList2 = paramArrayList1;
                      if (paramArrayList1 == null) {
                        paramArrayList2 = new ArrayList();
                      }
                      paramArrayList2.add(localObject21);
                      localObject23 = localObject6;
                      localObject24 = localObject13;
                      localObject25 = localObject12;
                      localObject26 = localObject16;
                      localObject27 = localObject11;
                      localObject28 = localObject5;
                      m = j;
                      localObject29 = localObject10;
                      localObject30 = localObject9;
                      localObject31 = localObject7;
                      localObject32 = localObject8;
                      localObject14 = localObject1;
                      localObject17 = localObject4;
                      bool2 = bool1;
                      localObject19 = localObject3;
                      localObject33 = localObject15;
                      localObject34 = localObject2;
                    }
                    else if ((localObject21 instanceof TLRPC.TL_updateLangPack))
                    {
                      paramArrayList2 = (TLRPC.TL_updateLangPack)localObject21;
                      LocaleController.getInstance().saveRemoteLocaleStrings(paramArrayList2.difference, this.currentAccount);
                      localObject23 = localObject6;
                      localObject24 = localObject13;
                      localObject25 = localObject12;
                      localObject26 = localObject16;
                      localObject27 = localObject11;
                      localObject28 = localObject5;
                      m = j;
                      localObject29 = localObject10;
                      localObject30 = localObject9;
                      localObject31 = localObject7;
                      localObject32 = localObject8;
                      localObject14 = localObject1;
                      localObject17 = localObject4;
                      bool2 = bool1;
                      localObject19 = localObject3;
                      localObject33 = localObject15;
                      paramArrayList2 = paramArrayList1;
                      localObject34 = localObject2;
                    }
                    else if ((localObject21 instanceof TLRPC.TL_updateLangPackTooLong))
                    {
                      LocaleController.getInstance().reloadCurrentRemoteLocale(this.currentAccount);
                      localObject23 = localObject6;
                      localObject24 = localObject13;
                      localObject25 = localObject12;
                      localObject26 = localObject16;
                      localObject27 = localObject11;
                      localObject28 = localObject5;
                      m = j;
                      localObject29 = localObject10;
                      localObject30 = localObject9;
                      localObject31 = localObject7;
                      localObject32 = localObject8;
                      localObject14 = localObject1;
                      localObject17 = localObject4;
                      bool2 = bool1;
                      localObject19 = localObject3;
                      localObject33 = localObject15;
                      paramArrayList2 = paramArrayList1;
                      localObject34 = localObject2;
                    }
                    else if ((localObject21 instanceof TLRPC.TL_updateFavedStickers))
                    {
                      paramArrayList2 = paramArrayList1;
                      if (paramArrayList1 == null) {
                        paramArrayList2 = new ArrayList();
                      }
                      paramArrayList2.add(localObject21);
                      localObject23 = localObject6;
                      localObject24 = localObject13;
                      localObject25 = localObject12;
                      localObject26 = localObject16;
                      localObject27 = localObject11;
                      localObject28 = localObject5;
                      m = j;
                      localObject29 = localObject10;
                      localObject30 = localObject9;
                      localObject31 = localObject7;
                      localObject32 = localObject8;
                      localObject14 = localObject1;
                      localObject17 = localObject4;
                      bool2 = bool1;
                      localObject19 = localObject3;
                      localObject33 = localObject15;
                      localObject34 = localObject2;
                    }
                    else if ((localObject21 instanceof TLRPC.TL_updateContactsReset))
                    {
                      paramArrayList2 = paramArrayList1;
                      if (paramArrayList1 == null) {
                        paramArrayList2 = new ArrayList();
                      }
                      paramArrayList2.add(localObject21);
                      localObject23 = localObject6;
                      localObject24 = localObject13;
                      localObject25 = localObject12;
                      localObject26 = localObject16;
                      localObject27 = localObject11;
                      localObject28 = localObject5;
                      m = j;
                      localObject29 = localObject10;
                      localObject30 = localObject9;
                      localObject31 = localObject7;
                      localObject32 = localObject8;
                      localObject14 = localObject1;
                      localObject17 = localObject4;
                      bool2 = bool1;
                      localObject19 = localObject3;
                      localObject33 = localObject15;
                      localObject34 = localObject2;
                    }
                    else
                    {
                      localObject23 = localObject6;
                      localObject24 = localObject13;
                      localObject25 = localObject12;
                      localObject26 = localObject16;
                      localObject27 = localObject11;
                      localObject28 = localObject5;
                      m = j;
                      localObject29 = localObject10;
                      localObject30 = localObject9;
                      localObject31 = localObject7;
                      localObject32 = localObject8;
                      localObject14 = localObject1;
                      localObject17 = localObject4;
                      bool2 = bool1;
                      localObject19 = localObject3;
                      localObject33 = localObject15;
                      paramArrayList2 = paramArrayList1;
                      localObject34 = localObject2;
                      if ((localObject21 instanceof TLRPC.TL_updateChannelAvailableMessages))
                      {
                        localObject22 = (TLRPC.TL_updateChannelAvailableMessages)localObject21;
                        localObject21 = localObject12;
                        if (localObject12 == null) {
                          localObject21 = new SparseIntArray();
                        }
                        i = ((SparseIntArray)localObject21).get(((TLRPC.TL_updateChannelAvailableMessages)localObject22).channel_id);
                        if (i != 0)
                        {
                          localObject23 = localObject6;
                          localObject24 = localObject13;
                          localObject25 = localObject21;
                          localObject26 = localObject16;
                          localObject27 = localObject11;
                          localObject28 = localObject5;
                          m = j;
                          localObject29 = localObject10;
                          localObject30 = localObject9;
                          localObject31 = localObject7;
                          localObject32 = localObject8;
                          localObject14 = localObject1;
                          localObject17 = localObject4;
                          bool2 = bool1;
                          localObject19 = localObject3;
                          localObject33 = localObject15;
                          paramArrayList2 = paramArrayList1;
                          localObject34 = localObject2;
                          if (i >= ((TLRPC.TL_updateChannelAvailableMessages)localObject22).available_min_id) {}
                        }
                        else
                        {
                          ((SparseIntArray)localObject21).put(((TLRPC.TL_updateChannelAvailableMessages)localObject22).channel_id, ((TLRPC.TL_updateChannelAvailableMessages)localObject22).available_min_id);
                          localObject23 = localObject6;
                          localObject24 = localObject13;
                          localObject25 = localObject21;
                          localObject26 = localObject16;
                          localObject27 = localObject11;
                          localObject28 = localObject5;
                          m = j;
                          localObject29 = localObject10;
                          localObject30 = localObject9;
                          localObject31 = localObject7;
                          localObject32 = localObject8;
                          localObject14 = localObject1;
                          localObject17 = localObject4;
                          bool2 = bool1;
                          localObject19 = localObject3;
                          localObject33 = localObject15;
                          paramArrayList2 = paramArrayList1;
                          localObject34 = localObject2;
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
      paramBoolean = bool1;
      if (localObject1 != null)
      {
        m = 0;
        i = ((LongSparseArray)localObject1).size();
        for (;;)
        {
          paramBoolean = bool1;
          if (m >= i) {
            break;
          }
          if (updatePrintingUsersWithNewMessages(((LongSparseArray)localObject1).keyAt(m), (ArrayList)((LongSparseArray)localObject1).valueAt(m))) {
            bool1 = true;
          }
          m++;
        }
      }
      if (paramBoolean) {
        updatePrintingStrings();
      }
      if (localObject16 != null) {
        ContactsController.getInstance(this.currentAccount).processContactsUpdates((ArrayList)localObject16, (ConcurrentHashMap)localObject18);
      }
      if (localObject3 != null) {
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
        {
          public void run()
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(MessagesController.131.this.val$pushMessagesFinal, true, false);
              }
            });
          }
        });
      }
      if (localObject4 != null)
      {
        StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, ((ArrayList)localObject4).size());
        MessagesStorage.getInstance(this.currentAccount).putMessages((ArrayList)localObject4, true, true, false, DownloadController.getInstance(this.currentAccount).getAutodownloadMask());
      }
      if (localObject5 != null)
      {
        m = 0;
        k = ((LongSparseArray)localObject5).size();
        while (m < k)
        {
          paramArrayList2 = new TLRPC.TL_messages_messages();
          paramArrayList = (ArrayList)((LongSparseArray)localObject5).valueAt(m);
          i = 0;
          n = paramArrayList.size();
          while (i < n)
          {
            paramArrayList2.messages.add(((MessageObject)paramArrayList.get(i)).messageOwner);
            i++;
          }
          MessagesStorage.getInstance(this.currentAccount).putMessages(paramArrayList2, ((LongSparseArray)localObject5).keyAt(m), -2, 0, false);
          m++;
        }
      }
      if (localObject6 != null) {
        MessagesStorage.getInstance(this.currentAccount).putChannelViews((SparseArray)localObject6, true);
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          int i = j;
          int j = 0;
          int k = 0;
          int m = i;
          Object localObject1;
          int n;
          final Object localObject3;
          int i1;
          long l1;
          label1383:
          label1645:
          label1746:
          label1941:
          Object localObject5;
          if (paramArrayList1 != null)
          {
            ArrayList localArrayList1 = new ArrayList();
            ArrayList localArrayList2 = new ArrayList();
            localObject1 = null;
            j = 0;
            n = paramArrayList1.size();
            m = i;
            i = k;
            k = j;
            if (k < n)
            {
              final Object localObject2 = (TLRPC.Update)paramArrayList1.get(k);
              Object localObject4;
              if ((localObject2 instanceof TLRPC.TL_updatePrivacy))
              {
                localObject3 = (TLRPC.TL_updatePrivacy)localObject2;
                if ((((TLRPC.TL_updatePrivacy)localObject3).key instanceof TLRPC.TL_privacyKeyStatusTimestamp))
                {
                  ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(((TLRPC.TL_updatePrivacy)localObject3).rules, 0);
                  j = m;
                  i1 = i;
                  localObject4 = localObject1;
                }
              }
              for (;;)
              {
                k++;
                localObject1 = localObject4;
                i = i1;
                m = j;
                break;
                if ((((TLRPC.TL_updatePrivacy)localObject3).key instanceof TLRPC.TL_privacyKeyChatInvite))
                {
                  ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(((TLRPC.TL_updatePrivacy)localObject3).rules, 1);
                  localObject4 = localObject1;
                  i1 = i;
                  j = m;
                }
                else
                {
                  localObject4 = localObject1;
                  i1 = i;
                  j = m;
                  if ((((TLRPC.TL_updatePrivacy)localObject3).key instanceof TLRPC.TL_privacyKeyPhoneCall))
                  {
                    ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(((TLRPC.TL_updatePrivacy)localObject3).rules, 2);
                    localObject4 = localObject1;
                    i1 = i;
                    j = m;
                    continue;
                    if ((localObject2 instanceof TLRPC.TL_updateUserStatus))
                    {
                      localObject3 = (TLRPC.TL_updateUserStatus)localObject2;
                      localObject4 = MessagesController.this.getUser(Integer.valueOf(((TLRPC.TL_updateUserStatus)localObject3).user_id));
                      if ((((TLRPC.TL_updateUserStatus)localObject3).status instanceof TLRPC.TL_userStatusRecently)) {
                        ((TLRPC.TL_updateUserStatus)localObject3).status.expires = -100;
                      }
                      for (;;)
                      {
                        if (localObject4 != null)
                        {
                          ((TLRPC.User)localObject4).id = ((TLRPC.TL_updateUserStatus)localObject3).user_id;
                          ((TLRPC.User)localObject4).status = ((TLRPC.TL_updateUserStatus)localObject3).status;
                        }
                        localObject4 = new TLRPC.TL_user();
                        ((TLRPC.User)localObject4).id = ((TLRPC.TL_updateUserStatus)localObject3).user_id;
                        ((TLRPC.User)localObject4).status = ((TLRPC.TL_updateUserStatus)localObject3).status;
                        localArrayList2.add(localObject4);
                        localObject4 = localObject1;
                        i1 = i;
                        j = m;
                        if (((TLRPC.TL_updateUserStatus)localObject3).user_id != UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                          break;
                        }
                        NotificationsController.getInstance(MessagesController.this.currentAccount).setLastOnlineFromOtherDevice(((TLRPC.TL_updateUserStatus)localObject3).status.expires);
                        localObject4 = localObject1;
                        i1 = i;
                        j = m;
                        break;
                        if ((((TLRPC.TL_updateUserStatus)localObject3).status instanceof TLRPC.TL_userStatusLastWeek)) {
                          ((TLRPC.TL_updateUserStatus)localObject3).status.expires = -101;
                        } else if ((((TLRPC.TL_updateUserStatus)localObject3).status instanceof TLRPC.TL_userStatusLastMonth)) {
                          ((TLRPC.TL_updateUserStatus)localObject3).status.expires = -102;
                        }
                      }
                    }
                    if ((localObject2 instanceof TLRPC.TL_updateUserName))
                    {
                      localObject4 = (TLRPC.TL_updateUserName)localObject2;
                      localObject3 = MessagesController.this.getUser(Integer.valueOf(((TLRPC.TL_updateUserName)localObject4).user_id));
                      if (localObject3 != null)
                      {
                        if (!UserObject.isContact((TLRPC.User)localObject3))
                        {
                          ((TLRPC.User)localObject3).first_name = ((TLRPC.TL_updateUserName)localObject4).first_name;
                          ((TLRPC.User)localObject3).last_name = ((TLRPC.TL_updateUserName)localObject4).last_name;
                        }
                        if (!TextUtils.isEmpty(((TLRPC.User)localObject3).username)) {
                          MessagesController.this.objectsByUsernames.remove(((TLRPC.User)localObject3).username);
                        }
                        if (TextUtils.isEmpty(((TLRPC.TL_updateUserName)localObject4).username)) {
                          MessagesController.this.objectsByUsernames.put(((TLRPC.TL_updateUserName)localObject4).username, localObject3);
                        }
                        ((TLRPC.User)localObject3).username = ((TLRPC.TL_updateUserName)localObject4).username;
                      }
                      localObject3 = new TLRPC.TL_user();
                      ((TLRPC.User)localObject3).id = ((TLRPC.TL_updateUserName)localObject4).user_id;
                      ((TLRPC.User)localObject3).first_name = ((TLRPC.TL_updateUserName)localObject4).first_name;
                      ((TLRPC.User)localObject3).last_name = ((TLRPC.TL_updateUserName)localObject4).last_name;
                      ((TLRPC.User)localObject3).username = ((TLRPC.TL_updateUserName)localObject4).username;
                      localArrayList1.add(localObject3);
                      localObject4 = localObject1;
                      i1 = i;
                      j = m;
                    }
                    else
                    {
                      if ((localObject2 instanceof TLRPC.TL_updateDialogPinned))
                      {
                        localObject3 = (TLRPC.TL_updateDialogPinned)localObject2;
                        if ((((TLRPC.TL_updateDialogPinned)localObject3).peer instanceof TLRPC.TL_dialogPeer))
                        {
                          localObject4 = ((TLRPC.TL_dialogPeer)((TLRPC.TL_updateDialogPinned)localObject3).peer).peer;
                          if ((localObject4 instanceof TLRPC.TL_peerUser)) {
                            l1 = ((TLRPC.Peer)localObject4).user_id;
                          }
                        }
                        for (;;)
                        {
                          localObject4 = localObject1;
                          i1 = i;
                          j = m;
                          if (MessagesController.this.pinDialog(l1, ((TLRPC.TL_updateDialogPinned)localObject3).pinned, null, -1L)) {
                            break;
                          }
                          UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = false;
                          UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                          MessagesController.this.loadPinnedDialogs(l1, null);
                          localObject4 = localObject1;
                          i1 = i;
                          j = m;
                          break;
                          if ((localObject4 instanceof TLRPC.TL_peerChat))
                          {
                            l1 = -((TLRPC.Peer)localObject4).chat_id;
                          }
                          else
                          {
                            l1 = -((TLRPC.Peer)localObject4).channel_id;
                            continue;
                            l1 = 0L;
                          }
                        }
                      }
                      if ((localObject2 instanceof TLRPC.TL_updatePinnedDialogs))
                      {
                        localObject4 = (TLRPC.TL_updatePinnedDialogs)localObject2;
                        UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = false;
                        UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                        if ((((TLRPC.TL_updatePinnedDialogs)localObject4).flags & 0x1) != 0)
                        {
                          localObject3 = new ArrayList();
                          localObject2 = ((TLRPC.TL_updatePinnedDialogs)localObject2).order;
                          j = 0;
                          i1 = ((ArrayList)localObject2).size();
                          localObject4 = localObject3;
                          if (j < i1)
                          {
                            localObject4 = (TLRPC.DialogPeer)((ArrayList)localObject2).get(j);
                            if ((localObject4 instanceof TLRPC.TL_dialogPeer))
                            {
                              localObject4 = ((TLRPC.TL_dialogPeer)localObject4).peer;
                              if (((TLRPC.Peer)localObject4).user_id != 0) {
                                l1 = ((TLRPC.Peer)localObject4).user_id;
                              }
                            }
                            for (;;)
                            {
                              ((ArrayList)localObject3).add(Long.valueOf(l1));
                              j++;
                              break;
                              if (((TLRPC.Peer)localObject4).chat_id != 0)
                              {
                                l1 = -((TLRPC.Peer)localObject4).chat_id;
                              }
                              else
                              {
                                l1 = -((TLRPC.Peer)localObject4).channel_id;
                                continue;
                                l1 = 0L;
                              }
                            }
                          }
                        }
                        else
                        {
                          localObject4 = null;
                        }
                        MessagesController.this.loadPinnedDialogs(0L, (ArrayList)localObject4);
                        localObject4 = localObject1;
                        i1 = i;
                        j = m;
                      }
                      else if ((localObject2 instanceof TLRPC.TL_updateUserPhoto))
                      {
                        localObject4 = (TLRPC.TL_updateUserPhoto)localObject2;
                        localObject3 = MessagesController.this.getUser(Integer.valueOf(((TLRPC.TL_updateUserPhoto)localObject4).user_id));
                        if (localObject3 != null) {
                          ((TLRPC.User)localObject3).photo = ((TLRPC.TL_updateUserPhoto)localObject4).photo;
                        }
                        localObject3 = new TLRPC.TL_user();
                        ((TLRPC.User)localObject3).id = ((TLRPC.TL_updateUserPhoto)localObject4).user_id;
                        ((TLRPC.User)localObject3).photo = ((TLRPC.TL_updateUserPhoto)localObject4).photo;
                        localArrayList1.add(localObject3);
                        localObject4 = localObject1;
                        i1 = i;
                        j = m;
                      }
                      else if ((localObject2 instanceof TLRPC.TL_updateUserPhone))
                      {
                        localObject4 = (TLRPC.TL_updateUserPhone)localObject2;
                        localObject3 = MessagesController.this.getUser(Integer.valueOf(((TLRPC.TL_updateUserPhone)localObject4).user_id));
                        if (localObject3 != null)
                        {
                          ((TLRPC.User)localObject3).phone = ((TLRPC.TL_updateUserPhone)localObject4).phone;
                          Utilities.phoneBookQueue.postRunnable(new Runnable()
                          {
                            public void run()
                            {
                              ContactsController.getInstance(MessagesController.this.currentAccount).addContactToPhoneBook(localObject3, true);
                            }
                          });
                        }
                        localObject3 = new TLRPC.TL_user();
                        ((TLRPC.User)localObject3).id = ((TLRPC.TL_updateUserPhone)localObject4).user_id;
                        ((TLRPC.User)localObject3).phone = ((TLRPC.TL_updateUserPhone)localObject4).phone;
                        localArrayList1.add(localObject3);
                        localObject4 = localObject1;
                        i1 = i;
                        j = m;
                      }
                      else if ((localObject2 instanceof TLRPC.TL_updateNotifySettings))
                      {
                        localObject3 = (TLRPC.TL_updateNotifySettings)localObject2;
                        localObject4 = localObject1;
                        i1 = i;
                        j = m;
                        if ((((TLRPC.TL_updateNotifySettings)localObject3).notify_settings instanceof TLRPC.TL_peerNotifySettings))
                        {
                          localObject4 = localObject1;
                          i1 = i;
                          j = m;
                          if ((((TLRPC.TL_updateNotifySettings)localObject3).peer instanceof TLRPC.TL_notifyPeer))
                          {
                            localObject4 = localObject1;
                            if (localObject1 == null) {
                              localObject4 = MessagesController.this.notificationsPreferences.edit();
                            }
                            if (((TLRPC.TL_updateNotifySettings)localObject3).peer.peer.user_id != 0)
                            {
                              l1 = ((TLRPC.TL_updateNotifySettings)localObject3).peer.peer.user_id;
                              localObject1 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(l1);
                              if (localObject1 != null) {
                                ((TLRPC.TL_dialog)localObject1).notify_settings = ((TLRPC.TL_updateNotifySettings)localObject3).notify_settings;
                              }
                              ((SharedPreferences.Editor)localObject4).putBoolean("silent_" + l1, ((TLRPC.TL_updateNotifySettings)localObject3).notify_settings.silent);
                              j = ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime();
                              if (((TLRPC.TL_updateNotifySettings)localObject3).notify_settings.mute_until <= j) {
                                break label1746;
                              }
                              i1 = 0;
                              if (((TLRPC.TL_updateNotifySettings)localObject3).notify_settings.mute_until <= 31536000 + j) {
                                break label1645;
                              }
                              ((SharedPreferences.Editor)localObject4).putInt("notify2_" + l1, 2);
                              j = i1;
                              if (localObject1 != null)
                              {
                                ((TLRPC.TL_dialog)localObject1).notify_settings.mute_until = Integer.MAX_VALUE;
                                j = i1;
                              }
                            }
                            for (;;)
                            {
                              MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogFlags(l1, j << 32 | 1L);
                              NotificationsController.getInstance(MessagesController.this.currentAccount).removeNotificationsForDialog(l1);
                              i1 = i;
                              j = m;
                              break;
                              if (((TLRPC.TL_updateNotifySettings)localObject3).peer.peer.chat_id != 0)
                              {
                                l1 = -((TLRPC.TL_updateNotifySettings)localObject3).peer.peer.chat_id;
                                break label1383;
                              }
                              l1 = -((TLRPC.TL_updateNotifySettings)localObject3).peer.peer.channel_id;
                              break label1383;
                              i1 = ((TLRPC.TL_updateNotifySettings)localObject3).notify_settings.mute_until;
                              ((SharedPreferences.Editor)localObject4).putInt("notify2_" + l1, 3);
                              ((SharedPreferences.Editor)localObject4).putInt("notifyuntil_" + l1, ((TLRPC.TL_updateNotifySettings)localObject3).notify_settings.mute_until);
                              j = i1;
                              if (localObject1 != null)
                              {
                                ((TLRPC.TL_dialog)localObject1).notify_settings.mute_until = i1;
                                j = i1;
                              }
                            }
                            if (localObject1 != null) {
                              ((TLRPC.TL_dialog)localObject1).notify_settings.mute_until = 0;
                            }
                            ((SharedPreferences.Editor)localObject4).remove("notify2_" + l1);
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogFlags(l1, 0L);
                            i1 = i;
                            j = m;
                          }
                        }
                      }
                      else
                      {
                        if ((localObject2 instanceof TLRPC.TL_updateChannel))
                        {
                          localObject2 = (TLRPC.TL_updateChannel)localObject2;
                          localObject4 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(-((TLRPC.TL_updateChannel)localObject2).channel_id);
                          localObject3 = MessagesController.this.getChat(Integer.valueOf(((TLRPC.TL_updateChannel)localObject2).channel_id));
                          if (localObject3 != null)
                          {
                            if ((localObject4 != null) || (!(localObject3 instanceof TLRPC.TL_channel)) || (((TLRPC.Chat)localObject3).left)) {
                              break label1941;
                            }
                            Utilities.stageQueue.postRunnable(new Runnable()
                            {
                              public void run()
                              {
                                MessagesController.this.getChannelDifference(localObject2.channel_id, 1, 0L, null);
                              }
                            });
                          }
                          for (;;)
                          {
                            j = m | 0x2000;
                            MessagesController.this.loadFullChat(((TLRPC.TL_updateChannel)localObject2).channel_id, 0, true);
                            localObject4 = localObject1;
                            i1 = i;
                            break;
                            if ((((TLRPC.Chat)localObject3).left) && (localObject4 != null)) {
                              MessagesController.this.deleteDialog(((TLRPC.TL_dialog)localObject4).id, 0);
                            }
                          }
                        }
                        if ((localObject2 instanceof TLRPC.TL_updateChatAdmins))
                        {
                          j = m | 0x4000;
                          localObject4 = localObject1;
                          i1 = i;
                        }
                        else if ((localObject2 instanceof TLRPC.TL_updateStickerSets))
                        {
                          localObject4 = (TLRPC.TL_updateStickerSets)localObject2;
                          DataQuery.getInstance(MessagesController.this.currentAccount).loadStickers(0, false, true);
                          localObject4 = localObject1;
                          i1 = i;
                          j = m;
                        }
                        else
                        {
                          if ((localObject2 instanceof TLRPC.TL_updateStickerSetsOrder))
                          {
                            localObject3 = (TLRPC.TL_updateStickerSetsOrder)localObject2;
                            localObject4 = DataQuery.getInstance(MessagesController.this.currentAccount);
                            if (((TLRPC.TL_updateStickerSetsOrder)localObject3).masks) {}
                            for (j = 1;; j = 0)
                            {
                              ((DataQuery)localObject4).reorderStickers(j, ((TLRPC.TL_updateStickerSetsOrder)localObject2).order);
                              localObject4 = localObject1;
                              i1 = i;
                              j = m;
                              break;
                            }
                          }
                          if ((localObject2 instanceof TLRPC.TL_updateFavedStickers))
                          {
                            DataQuery.getInstance(MessagesController.this.currentAccount).loadRecents(2, false, false, true);
                            localObject4 = localObject1;
                            i1 = i;
                            j = m;
                          }
                          else if ((localObject2 instanceof TLRPC.TL_updateContactsReset))
                          {
                            ContactsController.getInstance(MessagesController.this.currentAccount).forceImportContacts();
                            localObject4 = localObject1;
                            i1 = i;
                            j = m;
                          }
                          else if ((localObject2 instanceof TLRPC.TL_updateNewStickerSet))
                          {
                            localObject4 = (TLRPC.TL_updateNewStickerSet)localObject2;
                            DataQuery.getInstance(MessagesController.this.currentAccount).addNewStickerSet(((TLRPC.TL_updateNewStickerSet)localObject4).stickerset);
                            localObject4 = localObject1;
                            i1 = i;
                            j = m;
                          }
                          else if ((localObject2 instanceof TLRPC.TL_updateSavedGifs))
                          {
                            MessagesController.this.emojiPreferences.edit().putLong("lastGifLoadTime", 0L).commit();
                            localObject4 = localObject1;
                            i1 = i;
                            j = m;
                          }
                          else if ((localObject2 instanceof TLRPC.TL_updateRecentStickers))
                          {
                            MessagesController.this.emojiPreferences.edit().putLong("lastStickersLoadTime", 0L).commit();
                            localObject4 = localObject1;
                            i1 = i;
                            j = m;
                          }
                          else
                          {
                            if ((localObject2 instanceof TLRPC.TL_updateDraftMessage))
                            {
                              localObject4 = (TLRPC.TL_updateDraftMessage)localObject2;
                              i1 = 1;
                              localObject3 = ((TLRPC.TL_updateDraftMessage)localObject2).peer;
                              if (((TLRPC.Peer)localObject3).user_id != 0) {
                                l1 = ((TLRPC.Peer)localObject3).user_id;
                              }
                              for (;;)
                              {
                                DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(l1, ((TLRPC.TL_updateDraftMessage)localObject4).draft, null, true);
                                localObject4 = localObject1;
                                j = m;
                                break;
                                if (((TLRPC.Peer)localObject3).channel_id != 0) {
                                  l1 = -((TLRPC.Peer)localObject3).channel_id;
                                } else {
                                  l1 = -((TLRPC.Peer)localObject3).chat_id;
                                }
                              }
                            }
                            if ((localObject2 instanceof TLRPC.TL_updateReadFeaturedStickers))
                            {
                              DataQuery.getInstance(MessagesController.this.currentAccount).markFaturedStickersAsRead(false);
                              localObject4 = localObject1;
                              i1 = i;
                              j = m;
                            }
                            else if ((localObject2 instanceof TLRPC.TL_updatePhoneCall))
                            {
                              localObject3 = ((TLRPC.TL_updatePhoneCall)localObject2).phone_call;
                              localObject2 = VoIPService.getSharedInstance();
                              if (BuildVars.LOGS_ENABLED)
                              {
                                FileLog.d("Received call in update: " + localObject3);
                                FileLog.d("call id " + ((TLRPC.PhoneCall)localObject3).id);
                              }
                              if ((localObject3 instanceof TLRPC.TL_phoneCallRequested))
                              {
                                if (((TLRPC.PhoneCall)localObject3).date + MessagesController.this.callRingTimeout / 1000 < ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime())
                                {
                                  localObject4 = localObject1;
                                  i1 = i;
                                  j = m;
                                  if (BuildVars.LOGS_ENABLED)
                                  {
                                    FileLog.d("ignoring too old call");
                                    localObject4 = localObject1;
                                    i1 = i;
                                    j = m;
                                  }
                                }
                                else
                                {
                                  localObject4 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
                                  if ((localObject2 != null) || (VoIPService.callIShouldHavePutIntoIntent != null) || (((TelephonyManager)localObject4).getCallState() != 0))
                                  {
                                    if (BuildVars.LOGS_ENABLED) {
                                      FileLog.d("Auto-declining call " + ((TLRPC.PhoneCall)localObject3).id + " because there's already active one");
                                    }
                                    localObject4 = new TLRPC.TL_phone_discardCall();
                                    ((TLRPC.TL_phone_discardCall)localObject4).peer = new TLRPC.TL_inputPhoneCall();
                                    ((TLRPC.TL_phone_discardCall)localObject4).peer.access_hash = ((TLRPC.PhoneCall)localObject3).access_hash;
                                    ((TLRPC.TL_phone_discardCall)localObject4).peer.id = ((TLRPC.PhoneCall)localObject3).id;
                                    ((TLRPC.TL_phone_discardCall)localObject4).reason = new TLRPC.TL_phoneCallDiscardReasonBusy();
                                    ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest((TLObject)localObject4, new RequestDelegate()
                                    {
                                      public void run(TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
                                      {
                                        if (paramAnonymous2TLObject != null)
                                        {
                                          paramAnonymous2TLObject = (TLRPC.Updates)paramAnonymous2TLObject;
                                          MessagesController.this.processUpdates(paramAnonymous2TLObject, false);
                                        }
                                      }
                                    });
                                    localObject4 = localObject1;
                                    i1 = i;
                                    j = m;
                                  }
                                  else
                                  {
                                    if (BuildVars.LOGS_ENABLED) {
                                      FileLog.d("Starting service for call " + ((TLRPC.PhoneCall)localObject3).id);
                                    }
                                    VoIPService.callIShouldHavePutIntoIntent = (TLRPC.PhoneCall)localObject3;
                                    localObject4 = new Intent(ApplicationLoader.applicationContext, VoIPService.class);
                                    ((Intent)localObject4).putExtra("is_outgoing", false);
                                    if (((TLRPC.PhoneCall)localObject3).participant_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {}
                                    for (j = ((TLRPC.PhoneCall)localObject3).admin_id;; j = ((TLRPC.PhoneCall)localObject3).participant_id)
                                    {
                                      ((Intent)localObject4).putExtra("user_id", j);
                                      ((Intent)localObject4).putExtra("account", MessagesController.this.currentAccount);
                                      try
                                      {
                                        if (Build.VERSION.SDK_INT < 26) {
                                          break label2964;
                                        }
                                        ApplicationLoader.applicationContext.startForegroundService((Intent)localObject4);
                                        localObject4 = localObject1;
                                        i1 = i;
                                        j = m;
                                      }
                                      catch (Throwable localThrowable)
                                      {
                                        FileLog.e(localThrowable);
                                        localObject5 = localObject1;
                                        i1 = i;
                                        j = m;
                                      }
                                      break;
                                    }
                                    label2964:
                                    ApplicationLoader.applicationContext.startService((Intent)localObject5);
                                    localObject5 = localObject1;
                                    i1 = i;
                                    j = m;
                                  }
                                }
                              }
                              else if ((localObject2 != null) && (localObject3 != null))
                              {
                                ((VoIPService)localObject2).onCallUpdated((TLRPC.PhoneCall)localObject3);
                                localObject5 = localObject1;
                                i1 = i;
                                j = m;
                              }
                              else
                              {
                                localObject5 = localObject1;
                                i1 = i;
                                j = m;
                                if (VoIPService.callIShouldHavePutIntoIntent != null)
                                {
                                  if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("Updated the call while the service is starting");
                                  }
                                  localObject5 = localObject1;
                                  i1 = i;
                                  j = m;
                                  if (((TLRPC.PhoneCall)localObject3).id == VoIPService.callIShouldHavePutIntoIntent.id)
                                  {
                                    VoIPService.callIShouldHavePutIntoIntent = (TLRPC.PhoneCall)localObject3;
                                    localObject5 = localObject1;
                                    i1 = i;
                                    j = m;
                                  }
                                }
                              }
                            }
                            else
                            {
                              localObject5 = localObject1;
                              i1 = i;
                              j = m;
                              if (!(localObject2 instanceof TLRPC.TL_updateGroupCall))
                              {
                                localObject5 = localObject1;
                                i1 = i;
                                j = m;
                                if ((localObject2 instanceof TLRPC.TL_updateGroupCallParticipant))
                                {
                                  localObject5 = localObject1;
                                  i1 = i;
                                  j = m;
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
            }
            if (localObject1 != null)
            {
              ((SharedPreferences.Editor)localObject1).commit();
              NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
            }
            MessagesStorage.getInstance(MessagesController.this.currentAccount).updateUsers(localArrayList2, true, true, true);
            MessagesStorage.getInstance(MessagesController.this.currentAccount).updateUsers(localArrayList1, false, true, true);
            j = i;
          }
          if (localObject2 != null)
          {
            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didReceivedWebpagesInUpdates, new Object[] { localObject2 });
            i = 0;
            i1 = localObject2.size();
            while (i < i1)
            {
              l1 = localObject2.keyAt(i);
              localObject5 = (ArrayList)MessagesController.this.reloadingWebpagesPending.get(l1);
              MessagesController.this.reloadingWebpagesPending.remove(l1);
              if (localObject5 != null)
              {
                localObject1 = (TLRPC.WebPage)localObject2.valueAt(i);
                localObject3 = new ArrayList();
                l1 = 0L;
                if (((localObject1 instanceof TLRPC.TL_webPage)) || ((localObject1 instanceof TLRPC.TL_webPageEmpty)))
                {
                  k = 0;
                  n = ((ArrayList)localObject5).size();
                  for (;;)
                  {
                    l2 = l1;
                    if (k >= n) {
                      break;
                    }
                    ((MessageObject)((ArrayList)localObject5).get(k)).messageOwner.media.webpage = ((TLRPC.WebPage)localObject1);
                    if (k == 0)
                    {
                      l1 = ((MessageObject)((ArrayList)localObject5).get(k)).getDialogId();
                      ImageLoader.saveMessageThumbs(((MessageObject)((ArrayList)localObject5).get(k)).messageOwner);
                    }
                    ((ArrayList)localObject3).add(((MessageObject)((ArrayList)localObject5).get(k)).messageOwner);
                    k++;
                  }
                }
                MessagesController.this.reloadingWebpagesPending.put(((TLRPC.WebPage)localObject1).id, localObject5);
                long l2 = l1;
                if (!((ArrayList)localObject3).isEmpty())
                {
                  MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages((ArrayList)localObject3, true, true, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(l2), localObject5 });
                }
              }
              i++;
            }
          }
          i = 0;
          label3624:
          int i2;
          if (localObject1 != null)
          {
            i = 0;
            j = localObject1.size();
            while (i < j)
            {
              l1 = localObject1.keyAt(i);
              localObject1 = (ArrayList)localObject1.valueAt(i);
              MessagesController.this.updateInterfaceWithMessages(l1, (ArrayList)localObject1);
              i++;
            }
            i = 1;
            k = i;
            if (localObject5 == null) {
              break label3862;
            }
            j = 0;
            n = localObject5.size();
            k = i;
            if (j >= n) {
              break label3862;
            }
            l1 = localObject5.keyAt(j);
            localObject1 = (ArrayList)localObject5.valueAt(j);
            localObject3 = (MessageObject)MessagesController.this.dialogMessage.get(l1);
            k = i;
            if (localObject3 != null)
            {
              i1 = 0;
              i2 = ((ArrayList)localObject1).size();
            }
          }
          for (;;)
          {
            k = i;
            if (i1 < i2)
            {
              localObject5 = (MessageObject)((ArrayList)localObject1).get(i1);
              if (((MessageObject)localObject3).getId() == ((MessageObject)localObject5).getId())
              {
                MessagesController.this.dialogMessage.put(l1, localObject5);
                if ((((MessageObject)localObject5).messageOwner.to_id != null) && (((MessageObject)localObject5).messageOwner.to_id.channel_id == 0)) {
                  MessagesController.this.dialogMessagesByIds.put(((MessageObject)localObject5).getId(), localObject5);
                }
                k = 1;
              }
            }
            else
            {
              DataQuery.getInstance(MessagesController.this.currentAccount).loadReplyMessagesForMessages((ArrayList)localObject1, l1);
              NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(l1), localObject1 });
              j++;
              i = k;
              break label3624;
              if (j == 0) {
                break;
              }
              MessagesController.this.sortDialogs(null);
              i = 1;
              break;
            }
            i1++;
          }
          label3862:
          if (k != 0) {
            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
          }
          i = m;
          if (paramBoolean) {
            i = m | 0x40;
          }
          m = i;
          if (localObject16 != null) {
            m = i | 0x1 | 0x80;
          }
          if (localObject13 != null)
          {
            i = 0;
            j = localObject13.size();
            while (i < j)
            {
              localObject1 = (TLRPC.ChatParticipants)localObject13.get(i);
              MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatParticipants((TLRPC.ChatParticipants)localObject1);
              i++;
            }
          }
          if (localObject6 != null) {
            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didUpdatedMessagesViews, new Object[] { localObject6 });
          }
          if (m != 0) {
            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(m) });
          }
        }
      });
      MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i = 0;
              int j = 0;
              int k = 0;
              Object localObject1;
              int m;
              int n;
              Object localObject2;
              if ((MessagesController.133.this.val$markAsReadMessagesInboxFinal != null) || (MessagesController.133.this.val$markAsReadMessagesOutboxFinal != null))
              {
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesRead, new Object[] { MessagesController.133.this.val$markAsReadMessagesInboxFinal, MessagesController.133.this.val$markAsReadMessagesOutboxFinal });
                if (MessagesController.133.this.val$markAsReadMessagesInboxFinal != null)
                {
                  NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(MessagesController.133.this.val$markAsReadMessagesInboxFinal, 0L, 0, 0, false);
                  localObject1 = MessagesController.this.notificationsPreferences.edit();
                  j = 0;
                  m = MessagesController.133.this.val$markAsReadMessagesInboxFinal.size();
                  for (i = k; j < m; i = k)
                  {
                    n = MessagesController.133.this.val$markAsReadMessagesInboxFinal.keyAt(j);
                    int i1 = (int)MessagesController.133.this.val$markAsReadMessagesInboxFinal.valueAt(j);
                    localObject2 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(n);
                    k = i;
                    if (localObject2 != null)
                    {
                      k = i;
                      if (((TLRPC.TL_dialog)localObject2).top_message > 0)
                      {
                        k = i;
                        if (((TLRPC.TL_dialog)localObject2).top_message <= i1)
                        {
                          localObject2 = (MessageObject)MessagesController.this.dialogMessage.get(((TLRPC.TL_dialog)localObject2).id);
                          k = i;
                          if (localObject2 != null)
                          {
                            k = i;
                            if (!((MessageObject)localObject2).isOut())
                            {
                              ((MessageObject)localObject2).setIsRead();
                              k = i | 0x100;
                            }
                          }
                        }
                      }
                    }
                    if (n != UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId())
                    {
                      ((SharedPreferences.Editor)localObject1).remove("diditem" + n);
                      ((SharedPreferences.Editor)localObject1).remove("diditemo" + n);
                    }
                    j++;
                  }
                  ((SharedPreferences.Editor)localObject1).commit();
                }
                j = i;
                if (MessagesController.133.this.val$markAsReadMessagesOutboxFinal != null)
                {
                  k = 0;
                  m = MessagesController.133.this.val$markAsReadMessagesOutboxFinal.size();
                  for (;;)
                  {
                    j = i;
                    if (k >= m) {
                      break;
                    }
                    j = MessagesController.133.this.val$markAsReadMessagesOutboxFinal.keyAt(k);
                    n = (int)MessagesController.133.this.val$markAsReadMessagesOutboxFinal.valueAt(k);
                    localObject1 = (TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(j);
                    j = i;
                    if (localObject1 != null)
                    {
                      j = i;
                      if (((TLRPC.TL_dialog)localObject1).top_message > 0)
                      {
                        j = i;
                        if (((TLRPC.TL_dialog)localObject1).top_message <= n)
                        {
                          localObject1 = (MessageObject)MessagesController.this.dialogMessage.get(((TLRPC.TL_dialog)localObject1).id);
                          j = i;
                          if (localObject1 != null)
                          {
                            j = i;
                            if (((MessageObject)localObject1).isOut())
                            {
                              ((MessageObject)localObject1).setIsRead();
                              j = i | 0x100;
                            }
                          }
                        }
                      }
                    }
                    k++;
                    i = j;
                  }
                }
              }
              i = j;
              long l;
              if (MessagesController.133.this.val$markAsReadEncryptedFinal != null)
              {
                k = 0;
                m = MessagesController.133.this.val$markAsReadEncryptedFinal.size();
                for (;;)
                {
                  i = j;
                  if (k >= m) {
                    break;
                  }
                  i = MessagesController.133.this.val$markAsReadEncryptedFinal.keyAt(k);
                  n = MessagesController.133.this.val$markAsReadEncryptedFinal.valueAt(k);
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesReadEncrypted, new Object[] { Integer.valueOf(i), Integer.valueOf(n) });
                  l = i << 32;
                  i = j;
                  if ((TLRPC.TL_dialog)MessagesController.this.dialogs_dict.get(l) != null)
                  {
                    localObject1 = (MessageObject)MessagesController.this.dialogMessage.get(l);
                    i = j;
                    if (localObject1 != null)
                    {
                      i = j;
                      if (((MessageObject)localObject1).messageOwner.date <= n)
                      {
                        ((MessageObject)localObject1).setIsRead();
                        i = j | 0x100;
                      }
                    }
                  }
                  k++;
                  j = i;
                }
              }
              if (MessagesController.133.this.val$markAsReadMessagesFinal != null) {
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, new Object[] { MessagesController.133.this.val$markAsReadMessagesFinal });
              }
              if (MessagesController.133.this.val$deletedMessagesFinal != null)
              {
                j = 0;
                m = MessagesController.133.this.val$deletedMessagesFinal.size();
                if (j < m)
                {
                  k = MessagesController.133.this.val$deletedMessagesFinal.keyAt(j);
                  localObject1 = (ArrayList)MessagesController.133.this.val$deletedMessagesFinal.valueAt(j);
                  if (localObject1 == null) {}
                  label1013:
                  for (;;)
                  {
                    j++;
                    break;
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, new Object[] { localObject1, Integer.valueOf(k) });
                    if (k == 0)
                    {
                      k = 0;
                      n = ((ArrayList)localObject1).size();
                      while (k < n)
                      {
                        localObject2 = (Integer)((ArrayList)localObject1).get(k);
                        localObject2 = (MessageObject)MessagesController.this.dialogMessagesByIds.get(((Integer)localObject2).intValue());
                        if (localObject2 != null) {
                          ((MessageObject)localObject2).deleted = true;
                        }
                        k++;
                      }
                    }
                    else
                    {
                      localObject2 = (MessageObject)MessagesController.this.dialogMessage.get(-k);
                      if (localObject2 != null)
                      {
                        k = 0;
                        n = ((ArrayList)localObject1).size();
                        for (;;)
                        {
                          if (k >= n) {
                            break label1013;
                          }
                          if (((MessageObject)localObject2).getId() == ((Integer)((ArrayList)localObject1).get(k)).intValue())
                          {
                            ((MessageObject)localObject2).deleted = true;
                            break;
                          }
                          k++;
                        }
                      }
                    }
                  }
                }
                NotificationsController.getInstance(MessagesController.this.currentAccount).removeDeletedMessagesFromNotifications(MessagesController.133.this.val$deletedMessagesFinal);
              }
              if (MessagesController.133.this.val$clearHistoryMessagesFinal != null)
              {
                j = 0;
                k = MessagesController.133.this.val$clearHistoryMessagesFinal.size();
              }
              for (;;)
              {
                if (j < k)
                {
                  n = MessagesController.133.this.val$clearHistoryMessagesFinal.keyAt(j);
                  m = MessagesController.133.this.val$clearHistoryMessagesFinal.valueAt(j);
                  l = -n;
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.historyCleared, new Object[] { Long.valueOf(l), Integer.valueOf(m) });
                  localObject1 = (MessageObject)MessagesController.this.dialogMessage.get(l);
                  if ((localObject1 != null) && (((MessageObject)localObject1).getId() <= m)) {
                    ((MessageObject)localObject1).deleted = true;
                  }
                }
                else
                {
                  NotificationsController.getInstance(MessagesController.this.currentAccount).removeDeletedHisoryFromNotifications(MessagesController.133.this.val$clearHistoryMessagesFinal);
                  if (i != 0) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(i) });
                  }
                  return;
                }
                j++;
              }
            }
          });
        }
      });
      if (localObject2 != null) {
        MessagesStorage.getInstance(this.currentAccount).putWebPages((LongSparseArray)localObject2);
      }
      if ((localObject7 != null) || (localObject8 != null) || (localObject10 != null) || (localObject9 != null))
      {
        if ((localObject7 != null) || (localObject9 != null)) {
          MessagesStorage.getInstance(this.currentAccount).updateDialogsWithReadMessages((SparseLongArray)localObject7, (SparseLongArray)localObject8, (ArrayList)localObject9, true);
        }
        MessagesStorage.getInstance(this.currentAccount).markMessagesAsRead((SparseLongArray)localObject7, (SparseLongArray)localObject8, (SparseIntArray)localObject10, true);
      }
      if (localObject9 != null) {
        MessagesStorage.getInstance(this.currentAccount).markMessagesContentAsRead((ArrayList)localObject9, ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
      }
      if (localObject11 != null)
      {
        m = 0;
        j = ((SparseArray)localObject11).size();
        while (m < j)
        {
          i = ((SparseArray)localObject11).keyAt(m);
          paramArrayList = (ArrayList)((SparseArray)localObject11).valueAt(m);
          MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
          {
            public void run()
            {
              ArrayList localArrayList = MessagesStorage.getInstance(MessagesController.this.currentAccount).markMessagesAsDeleted(paramArrayList, false, i);
              MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(paramArrayList, localArrayList, false, i);
            }
          });
          m++;
        }
      }
      if (localObject12 != null)
      {
        m = 0;
        j = ((SparseIntArray)localObject12).size();
        while (m < j)
        {
          i = ((SparseIntArray)localObject12).keyAt(m);
          k = ((SparseIntArray)localObject12).valueAt(m);
          MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
          {
            public void run()
            {
              ArrayList localArrayList = MessagesStorage.getInstance(MessagesController.this.currentAccount).markMessagesAsDeleted(i, k, false);
              MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), localArrayList, false, i);
            }
          });
          m++;
        }
      }
      if (localObject15 != null)
      {
        m = 0;
        j = ((ArrayList)localObject15).size();
        while (m < j)
        {
          paramArrayList = (TLRPC.TL_updateEncryptedMessagesRead)((ArrayList)localObject15).get(m);
          MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(paramArrayList.chat_id, paramArrayList.max_date, paramArrayList.date, 1, null);
          m++;
        }
      }
      paramBoolean = true;
    }
  }
  
  public void processUpdates(final TLRPC.Updates paramUpdates, boolean paramBoolean)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int i = 0;
    int j = 0;
    final int k = 0;
    int m = 0;
    int n = 0;
    int i1 = 0;
    final Object localObject3;
    int i2;
    Object localObject4;
    int i3;
    if ((paramUpdates instanceof TLRPC.TL_updateShort))
    {
      localObject3 = new ArrayList();
      ((ArrayList)localObject3).add(paramUpdates.update);
      processUpdateArray((ArrayList)localObject3, null, null, false);
      i = i1;
      n = m;
      i2 = j;
      localObject4 = localObject2;
      SecretChatHelper.getInstance(this.currentAccount).processPendingEncMessages();
      if (paramBoolean) {
        break label4549;
      }
      i3 = 0;
      label92:
      if (i3 >= this.updatesQueueChannels.size()) {
        break label4540;
      }
      k = this.updatesQueueChannels.keyAt(i3);
      if ((localObject4 == null) || (!((ArrayList)localObject4).contains(Integer.valueOf(k)))) {
        break label4530;
      }
      getChannelDifference(k);
    }
    for (;;)
    {
      i3++;
      break label92;
      label172:
      Object localObject6;
      Object localObject7;
      if (((paramUpdates instanceof TLRPC.TL_updateShortChatMessage)) || ((paramUpdates instanceof TLRPC.TL_updateShortMessage)))
      {
        TLRPC.Chat localChat;
        if ((paramUpdates instanceof TLRPC.TL_updateShortChatMessage))
        {
          k = paramUpdates.from_id;
          localObject3 = getUser(Integer.valueOf(k));
          localObject1 = null;
          Object localObject5 = null;
          localObject6 = null;
          localChat = null;
          if (localObject3 != null)
          {
            localObject7 = localObject3;
            if (!((TLRPC.User)localObject3).min) {}
          }
          else
          {
            localObject4 = MessagesStorage.getInstance(this.currentAccount).getUserSync(k);
            localObject3 = localObject4;
            if (localObject4 != null)
            {
              localObject3 = localObject4;
              if (((TLRPC.User)localObject4).min) {
                localObject3 = null;
              }
            }
            putUser((TLRPC.User)localObject3, true);
            localObject7 = localObject3;
          }
          i2 = 0;
          i3 = 0;
          localObject4 = localChat;
          if (paramUpdates.fwd_from != null)
          {
            i2 = i3;
            localObject3 = localObject5;
            if (paramUpdates.fwd_from.from_id != 0)
            {
              localObject4 = getUser(Integer.valueOf(paramUpdates.fwd_from.from_id));
              localObject3 = localObject4;
              if (localObject4 == null)
              {
                localObject3 = MessagesStorage.getInstance(this.currentAccount).getUserSync(paramUpdates.fwd_from.from_id);
                putUser((TLRPC.User)localObject3, true);
              }
              i2 = 1;
            }
            localObject4 = localChat;
            localObject1 = localObject3;
            if (paramUpdates.fwd_from.channel_id != 0)
            {
              localObject1 = getChat(Integer.valueOf(paramUpdates.fwd_from.channel_id));
              localObject4 = localObject1;
              if (localObject1 == null)
              {
                localObject4 = MessagesStorage.getInstance(this.currentAccount).getChatSync(paramUpdates.fwd_from.channel_id);
                putChat((TLRPC.Chat)localObject4, true);
              }
              i2 = 1;
              localObject1 = localObject3;
            }
          }
          i3 = 0;
          localObject3 = localObject6;
          if (paramUpdates.via_bot_id != 0)
          {
            localObject6 = getUser(Integer.valueOf(paramUpdates.via_bot_id));
            localObject3 = localObject6;
            if (localObject6 == null)
            {
              localObject3 = MessagesStorage.getInstance(this.currentAccount).getUserSync(paramUpdates.via_bot_id);
              putUser((TLRPC.User)localObject3, true);
            }
            i3 = 1;
          }
          if (!(paramUpdates instanceof TLRPC.TL_updateShortMessage)) {
            break label774;
          }
          if ((localObject7 != null) && ((i2 == 0) || (localObject1 != null) || (localObject4 != null)) && ((i3 == 0) || (localObject3 != null))) {
            break label768;
          }
          i2 = 1;
          label524:
          i = i2;
          if (i2 == 0)
          {
            i = i2;
            if (paramUpdates.entities.isEmpty()) {}
          }
        }
        for (i3 = 0;; i3++)
        {
          i = i2;
          if (i3 < paramUpdates.entities.size())
          {
            localObject3 = (TLRPC.MessageEntity)paramUpdates.entities.get(i3);
            if (!(localObject3 instanceof TLRPC.TL_messageEntityMentionName)) {
              continue;
            }
            i = ((TLRPC.TL_messageEntityMentionName)localObject3).user_id;
            localObject3 = getUser(Integer.valueOf(i));
            if ((localObject3 != null) && (!((TLRPC.User)localObject3).min)) {
              continue;
            }
            localObject4 = MessagesStorage.getInstance(this.currentAccount).getUserSync(i);
            localObject3 = localObject4;
            if (localObject4 != null)
            {
              localObject3 = localObject4;
              if (((TLRPC.User)localObject4).min) {
                localObject3 = null;
              }
            }
            if (localObject3 == null) {
              i = 1;
            }
          }
          else
          {
            i3 = n;
            if (localObject7 != null)
            {
              i3 = n;
              if (((TLRPC.User)localObject7).status != null)
              {
                i3 = n;
                if (((TLRPC.User)localObject7).status.expires <= 0)
                {
                  this.onlinePrivacy.put(Integer.valueOf(((TLRPC.User)localObject7).id), Integer.valueOf(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
                  i3 = 1;
                }
              }
            }
            if (i == 0) {
              break label879;
            }
            i2 = 1;
            localObject4 = localObject2;
            n = m;
            i = i3;
            break;
            k = paramUpdates.user_id;
            break label172;
            label768:
            i2 = 0;
            break label524;
            label774:
            localChat = getChat(Integer.valueOf(paramUpdates.chat_id));
            localObject6 = localChat;
            if (localChat == null)
            {
              localObject6 = MessagesStorage.getInstance(this.currentAccount).getChatSync(paramUpdates.chat_id);
              putChat((TLRPC.Chat)localObject6, true);
            }
            if ((localObject6 == null) || (localObject7 == null) || ((i2 != 0) && (localObject1 == null) && (localObject4 == null)) || ((i3 != 0) && (localObject3 == null))) {}
            for (i2 = 1;; i2 = 0) {
              break;
            }
          }
          putUser((TLRPC.User)localObject7, true);
        }
        label879:
        if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + paramUpdates.pts_count == paramUpdates.pts)
        {
          localObject1 = new TLRPC.TL_message();
          ((TLRPC.TL_message)localObject1).id = paramUpdates.id;
          i2 = UserConfig.getInstance(this.currentAccount).getClientUserId();
          label949:
          label976:
          label1092:
          final boolean bool;
          if ((paramUpdates instanceof TLRPC.TL_updateShortMessage)) {
            if (paramUpdates.out)
            {
              ((TLRPC.TL_message)localObject1).from_id = i2;
              ((TLRPC.TL_message)localObject1).to_id = new TLRPC.TL_peerUser();
              ((TLRPC.TL_message)localObject1).to_id.user_id = k;
              ((TLRPC.TL_message)localObject1).dialog_id = k;
              ((TLRPC.TL_message)localObject1).fwd_from = paramUpdates.fwd_from;
              ((TLRPC.TL_message)localObject1).silent = paramUpdates.silent;
              ((TLRPC.TL_message)localObject1).out = paramUpdates.out;
              ((TLRPC.TL_message)localObject1).mentioned = paramUpdates.mentioned;
              ((TLRPC.TL_message)localObject1).media_unread = paramUpdates.media_unread;
              ((TLRPC.TL_message)localObject1).entities = paramUpdates.entities;
              ((TLRPC.TL_message)localObject1).message = paramUpdates.message;
              ((TLRPC.TL_message)localObject1).date = paramUpdates.date;
              ((TLRPC.TL_message)localObject1).via_bot_id = paramUpdates.via_bot_id;
              ((TLRPC.TL_message)localObject1).flags = (paramUpdates.flags | 0x100);
              ((TLRPC.TL_message)localObject1).reply_to_msg_id = paramUpdates.reply_to_msg_id;
              ((TLRPC.TL_message)localObject1).media = new TLRPC.TL_messageMediaEmpty();
              if (!((TLRPC.TL_message)localObject1).out) {
                break label1451;
              }
              localObject3 = this.dialogs_read_outbox_max;
              localObject7 = (Integer)((ConcurrentHashMap)localObject3).get(Long.valueOf(((TLRPC.TL_message)localObject1).dialog_id));
              localObject4 = localObject7;
              if (localObject7 == null)
              {
                localObject4 = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(((TLRPC.TL_message)localObject1).out, ((TLRPC.TL_message)localObject1).dialog_id));
                ((ConcurrentHashMap)localObject3).put(Long.valueOf(((TLRPC.TL_message)localObject1).dialog_id), localObject4);
              }
              if (((Integer)localObject4).intValue() >= ((TLRPC.TL_message)localObject1).id) {
                break label1460;
              }
              bool = true;
              label1171:
              ((TLRPC.TL_message)localObject1).unread = bool;
              if (((TLRPC.TL_message)localObject1).dialog_id == i2)
              {
                ((TLRPC.TL_message)localObject1).unread = false;
                ((TLRPC.TL_message)localObject1).media_unread = false;
                ((TLRPC.TL_message)localObject1).out = true;
              }
              MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(paramUpdates.pts);
              localObject7 = new MessageObject(this.currentAccount, (TLRPC.Message)localObject1, this.createdDialogIds.contains(Long.valueOf(((TLRPC.TL_message)localObject1).dialog_id)));
              localObject3 = new ArrayList();
              ((ArrayList)localObject3).add(localObject7);
              localObject4 = new ArrayList();
              ((ArrayList)localObject4).add(localObject1);
              if (!(paramUpdates instanceof TLRPC.TL_updateShortMessage)) {
                break label1472;
              }
              if ((paramUpdates.out) || (!updatePrintingUsersWithNewMessages(paramUpdates.user_id, (ArrayList)localObject3))) {
                break label1466;
              }
              bool = true;
              label1309:
              if (bool) {
                updatePrintingStrings();
              }
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  if (bool) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(64) });
                  }
                  MessagesController.this.updateInterfaceWithMessages(k, localObject3);
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
              });
            }
          }
          for (;;)
          {
            if (!((MessageObject)localObject7).isOut()) {
              MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable()
              {
                public void run()
                {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(MessagesController.124.this.val$objArr, true, false);
                    }
                  });
                }
              });
            }
            MessagesStorage.getInstance(this.currentAccount).putMessages((ArrayList)localObject4, false, true, false, 0);
            localObject4 = localObject2;
            i2 = j;
            n = m;
            i = i3;
            break;
            ((TLRPC.TL_message)localObject1).from_id = k;
            break label949;
            ((TLRPC.TL_message)localObject1).from_id = k;
            ((TLRPC.TL_message)localObject1).to_id = new TLRPC.TL_peerChat();
            ((TLRPC.TL_message)localObject1).to_id.chat_id = paramUpdates.chat_id;
            ((TLRPC.TL_message)localObject1).dialog_id = (-paramUpdates.chat_id);
            break label976;
            label1451:
            localObject3 = this.dialogs_read_inbox_max;
            break label1092;
            label1460:
            bool = false;
            break label1171;
            label1466:
            bool = false;
            break label1309;
            label1472:
            bool = updatePrintingUsersWithNewMessages(-paramUpdates.chat_id, (ArrayList)localObject3);
            if (bool) {
              updatePrintingStrings();
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (bool) {
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(64) });
                }
                MessagesController.this.updateInterfaceWithMessages(-paramUpdates.chat_id, localObject3);
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
              }
            });
          }
        }
        localObject4 = localObject2;
        i2 = j;
        n = m;
        i = i3;
        if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() == paramUpdates.pts) {
          break;
        }
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("need get diff short message, pts: " + MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + " " + paramUpdates.pts + " count = " + paramUpdates.pts_count);
        }
        if ((this.gettingDifference) || (this.updatesStartWaitTimePts == 0L) || (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500L))
        {
          if (this.updatesStartWaitTimePts == 0L) {
            this.updatesStartWaitTimePts = System.currentTimeMillis();
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("add to queue");
          }
          this.updatesQueuePts.add(paramUpdates);
          localObject4 = localObject2;
          i2 = j;
          n = m;
          i = i3;
          break;
        }
        i2 = 1;
        localObject4 = localObject2;
        n = m;
        i = i3;
        break;
      }
      if (((paramUpdates instanceof TLRPC.TL_updatesCombined)) || ((paramUpdates instanceof TLRPC.TL_updates)))
      {
        localObject3 = null;
        i2 = 0;
        while (i2 < paramUpdates.chats.size())
        {
          localObject6 = (TLRPC.Chat)paramUpdates.chats.get(i2);
          localObject7 = localObject3;
          if ((localObject6 instanceof TLRPC.TL_channel))
          {
            localObject7 = localObject3;
            if (((TLRPC.Chat)localObject6).min)
            {
              localObject7 = getChat(Integer.valueOf(((TLRPC.Chat)localObject6).id));
              if (localObject7 != null)
              {
                localObject4 = localObject7;
                if (!((TLRPC.Chat)localObject7).min) {}
              }
              else
              {
                localObject4 = MessagesStorage.getInstance(this.currentAccount).getChatSync(paramUpdates.chat_id);
                putChat((TLRPC.Chat)localObject4, true);
              }
              if (localObject4 != null)
              {
                localObject7 = localObject3;
                if (!((TLRPC.Chat)localObject4).min) {}
              }
              else
              {
                localObject4 = localObject3;
                if (localObject3 == null) {
                  localObject4 = new SparseArray();
                }
                ((SparseArray)localObject4).put(((TLRPC.Chat)localObject6).id, localObject6);
                localObject7 = localObject4;
              }
            }
          }
          i2++;
          localObject3 = localObject7;
        }
        i3 = i;
        if (localObject3 != null) {}
        for (i2 = 0;; i2++)
        {
          i3 = i;
          if (i2 < paramUpdates.updates.size())
          {
            localObject4 = (TLRPC.Update)paramUpdates.updates.get(i2);
            if ((localObject4 instanceof TLRPC.TL_updateNewChannelMessage))
            {
              i3 = ((TLRPC.TL_updateNewChannelMessage)localObject4).message.to_id.channel_id;
              if (((SparseArray)localObject3).indexOfKey(i3) >= 0)
              {
                if (BuildVars.LOGS_ENABLED) {
                  FileLog.d("need get diff because of min channel " + i3);
                }
                i3 = 1;
              }
            }
          }
          else
          {
            localObject4 = localObject2;
            i2 = i3;
            n = m;
            i = i1;
            if (i3 != 0) {
              break;
            }
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(paramUpdates.users, paramUpdates.chats, true, true);
            Collections.sort(paramUpdates.updates, this.updatesComparator);
            i = 0;
            localObject3 = localObject1;
            if (paramUpdates.updates.size() <= 0) {
              break label3917;
            }
            localObject7 = (TLRPC.Update)paramUpdates.updates.get(i);
            if (getUpdateType((TLRPC.Update)localObject7) != 0) {
              break label2634;
            }
            localObject1 = new TLRPC.TL_updates();
            ((TLRPC.TL_updates)localObject1).updates.add(localObject7);
            ((TLRPC.TL_updates)localObject1).pts = getUpdatePts((TLRPC.Update)localObject7);
            ((TLRPC.TL_updates)localObject1).pts_count = getUpdatePtsCount((TLRPC.Update)localObject7);
            for (i2 = i + 1; i2 < paramUpdates.updates.size(); i2 = i2 - 1 + 1)
            {
              localObject4 = (TLRPC.Update)paramUpdates.updates.get(i2);
              m = getUpdatePts((TLRPC.Update)localObject4);
              n = getUpdatePtsCount((TLRPC.Update)localObject4);
              if ((getUpdateType((TLRPC.Update)localObject4) != 0) || (((TLRPC.TL_updates)localObject1).pts + n != m)) {
                break;
              }
              ((TLRPC.TL_updates)localObject1).updates.add(localObject4);
              ((TLRPC.TL_updates)localObject1).pts = m;
              ((TLRPC.TL_updates)localObject1).pts_count += n;
              paramUpdates.updates.remove(i2);
            }
          }
        }
        if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + ((TLRPC.TL_updates)localObject1).pts_count == ((TLRPC.TL_updates)localObject1).pts) {
          if (!processUpdateArray(((TLRPC.TL_updates)localObject1).updates, paramUpdates.users, paramUpdates.chats, false))
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("need get diff inner TL_updates, pts: " + MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + " " + paramUpdates.seq);
            }
            i2 = 1;
            m = k;
            localObject4 = localObject3;
          }
        }
        for (;;)
        {
          paramUpdates.updates.remove(i);
          i = i - 1 + 1;
          localObject3 = localObject4;
          i3 = i2;
          k = m;
          break;
          MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(((TLRPC.TL_updates)localObject1).pts);
          localObject4 = localObject3;
          i2 = i3;
          m = k;
          continue;
          localObject4 = localObject3;
          i2 = i3;
          m = k;
          if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() != ((TLRPC.TL_updates)localObject1).pts)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d(localObject7 + " need get diff, pts: " + MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + " " + ((TLRPC.TL_updates)localObject1).pts + " count = " + ((TLRPC.TL_updates)localObject1).pts_count);
            }
            if ((this.gettingDifference) || (this.updatesStartWaitTimePts == 0L) || ((this.updatesStartWaitTimePts != 0L) && (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500L)))
            {
              if (this.updatesStartWaitTimePts == 0L) {
                this.updatesStartWaitTimePts = System.currentTimeMillis();
              }
              if (BuildVars.LOGS_ENABLED) {
                FileLog.d("add to queue");
              }
              this.updatesQueuePts.add(localObject1);
              localObject4 = localObject3;
              i2 = i3;
              m = k;
            }
            else
            {
              i2 = 1;
              localObject4 = localObject3;
              m = k;
              continue;
              label2634:
              if (getUpdateType((TLRPC.Update)localObject7) == 1)
              {
                localObject1 = new TLRPC.TL_updates();
                ((TLRPC.TL_updates)localObject1).updates.add(localObject7);
                ((TLRPC.TL_updates)localObject1).pts = getUpdateQts((TLRPC.Update)localObject7);
                for (i2 = i + 1; i2 < paramUpdates.updates.size(); i2 = i2 - 1 + 1)
                {
                  localObject4 = (TLRPC.Update)paramUpdates.updates.get(i2);
                  n = getUpdateQts((TLRPC.Update)localObject4);
                  if ((getUpdateType((TLRPC.Update)localObject4) != 1) || (((TLRPC.TL_updates)localObject1).pts + 1 != n)) {
                    break;
                  }
                  ((TLRPC.TL_updates)localObject1).updates.add(localObject4);
                  ((TLRPC.TL_updates)localObject1).pts = n;
                  paramUpdates.updates.remove(i2);
                }
                if ((MessagesStorage.getInstance(this.currentAccount).getLastQtsValue() == 0) || (MessagesStorage.getInstance(this.currentAccount).getLastQtsValue() + ((TLRPC.TL_updates)localObject1).updates.size() == ((TLRPC.TL_updates)localObject1).pts))
                {
                  processUpdateArray(((TLRPC.TL_updates)localObject1).updates, paramUpdates.users, paramUpdates.chats, false);
                  MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(((TLRPC.TL_updates)localObject1).pts);
                  m = 1;
                  localObject4 = localObject3;
                  i2 = i3;
                }
                else
                {
                  localObject4 = localObject3;
                  i2 = i3;
                  m = k;
                  if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() != ((TLRPC.TL_updates)localObject1).pts)
                  {
                    if (BuildVars.LOGS_ENABLED) {
                      FileLog.d(localObject7 + " need get diff, qts: " + MessagesStorage.getInstance(this.currentAccount).getLastQtsValue() + " " + ((TLRPC.TL_updates)localObject1).pts);
                    }
                    if ((this.gettingDifference) || (this.updatesStartWaitTimeQts == 0L) || ((this.updatesStartWaitTimeQts != 0L) && (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeQts) <= 1500L)))
                    {
                      if (this.updatesStartWaitTimeQts == 0L) {
                        this.updatesStartWaitTimeQts = System.currentTimeMillis();
                      }
                      if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("add to queue");
                      }
                      this.updatesQueueQts.add(localObject1);
                      localObject4 = localObject3;
                      i2 = i3;
                      m = k;
                    }
                    else
                    {
                      i2 = 1;
                      localObject4 = localObject3;
                      m = k;
                    }
                  }
                }
              }
              else
              {
                if (getUpdateType((TLRPC.Update)localObject7) != 2) {
                  break label3917;
                }
                int i4 = getUpdateChannelId((TLRPC.Update)localObject7);
                int i5 = 0;
                m = this.channelsPts.get(i4);
                n = m;
                i2 = i5;
                if (m == 0)
                {
                  j = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(i4);
                  if (j != 0) {
                    break label3338;
                  }
                  m = 0;
                  n = j;
                  i2 = i5;
                  if (m < paramUpdates.chats.size())
                  {
                    localObject4 = (TLRPC.Chat)paramUpdates.chats.get(m);
                    if (((TLRPC.Chat)localObject4).id != i4) {
                      break label3332;
                    }
                    loadUnknownChannel((TLRPC.Chat)localObject4, 0L);
                    i2 = 1;
                    n = j;
                  }
                }
                for (;;)
                {
                  localObject1 = new TLRPC.TL_updates();
                  ((TLRPC.TL_updates)localObject1).updates.add(localObject7);
                  ((TLRPC.TL_updates)localObject1).pts = getUpdatePts((TLRPC.Update)localObject7);
                  ((TLRPC.TL_updates)localObject1).pts_count = getUpdatePtsCount((TLRPC.Update)localObject7);
                  for (m = i + 1; m < paramUpdates.updates.size(); m = m - 1 + 1)
                  {
                    localObject4 = (TLRPC.Update)paramUpdates.updates.get(m);
                    i5 = getUpdatePts((TLRPC.Update)localObject4);
                    j = getUpdatePtsCount((TLRPC.Update)localObject4);
                    if ((getUpdateType((TLRPC.Update)localObject4) != 2) || (i4 != getUpdateChannelId((TLRPC.Update)localObject4)) || (((TLRPC.TL_updates)localObject1).pts + j != i5)) {
                      break;
                    }
                    ((TLRPC.TL_updates)localObject1).updates.add(localObject4);
                    ((TLRPC.TL_updates)localObject1).pts = i5;
                    ((TLRPC.TL_updates)localObject1).pts_count += j;
                    paramUpdates.updates.remove(m);
                  }
                  label3332:
                  m++;
                  break;
                  label3338:
                  this.channelsPts.put(i4, j);
                  n = j;
                  i2 = i5;
                }
                if (i2 == 0)
                {
                  if (((TLRPC.TL_updates)localObject1).pts_count + n == ((TLRPC.TL_updates)localObject1).pts)
                  {
                    if (!processUpdateArray(((TLRPC.TL_updates)localObject1).updates, paramUpdates.users, paramUpdates.chats, false))
                    {
                      if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("need get channel diff inner TL_updates, channel_id = " + i4);
                      }
                      if (localObject3 == null)
                      {
                        localObject4 = new ArrayList();
                        i2 = i3;
                        m = k;
                      }
                      else
                      {
                        localObject4 = localObject3;
                        i2 = i3;
                        m = k;
                        if (!((ArrayList)localObject3).contains(Integer.valueOf(i4)))
                        {
                          ((ArrayList)localObject3).add(Integer.valueOf(i4));
                          localObject4 = localObject3;
                          i2 = i3;
                          m = k;
                        }
                      }
                    }
                    else
                    {
                      this.channelsPts.put(i4, ((TLRPC.TL_updates)localObject1).pts);
                      MessagesStorage.getInstance(this.currentAccount).saveChannelPts(i4, ((TLRPC.TL_updates)localObject1).pts);
                      localObject4 = localObject3;
                      i2 = i3;
                      m = k;
                    }
                  }
                  else
                  {
                    localObject4 = localObject3;
                    i2 = i3;
                    m = k;
                    if (n != ((TLRPC.TL_updates)localObject1).pts)
                    {
                      if (BuildVars.LOGS_ENABLED) {
                        FileLog.d(localObject7 + " need get channel diff, pts: " + n + " " + ((TLRPC.TL_updates)localObject1).pts + " count = " + ((TLRPC.TL_updates)localObject1).pts_count + " channelId = " + i4);
                      }
                      long l = this.updatesStartWaitTimeChannels.get(i4);
                      if ((this.gettingDifferenceChannels.get(i4)) || (l == 0L) || (Math.abs(System.currentTimeMillis() - l) <= 1500L))
                      {
                        if (l == 0L) {
                          this.updatesStartWaitTimeChannels.put(i4, System.currentTimeMillis());
                        }
                        if (BuildVars.LOGS_ENABLED) {
                          FileLog.d("add to queue");
                        }
                        localObject7 = (ArrayList)this.updatesQueueChannels.get(i4);
                        localObject4 = localObject7;
                        if (localObject7 == null)
                        {
                          localObject4 = new ArrayList();
                          this.updatesQueueChannels.put(i4, localObject4);
                        }
                        ((ArrayList)localObject4).add(localObject1);
                        localObject4 = localObject3;
                        i2 = i3;
                        m = k;
                      }
                      else if (localObject3 == null)
                      {
                        localObject4 = new ArrayList();
                        i2 = i3;
                        m = k;
                      }
                      else
                      {
                        localObject4 = localObject3;
                        i2 = i3;
                        m = k;
                        if (!((ArrayList)localObject3).contains(Integer.valueOf(i4)))
                        {
                          ((ArrayList)localObject3).add(Integer.valueOf(i4));
                          localObject4 = localObject3;
                          i2 = i3;
                          m = k;
                        }
                      }
                    }
                  }
                }
                else
                {
                  localObject4 = localObject3;
                  i2 = i3;
                  m = k;
                  if (BuildVars.LOGS_ENABLED)
                  {
                    FileLog.d("need load unknown channel = " + i4);
                    localObject4 = localObject3;
                    i2 = i3;
                    m = k;
                  }
                }
              }
            }
          }
        }
        label3917:
        if ((paramUpdates instanceof TLRPC.TL_updatesCombined))
        {
          if ((MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 == paramUpdates.seq_start) || (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() == paramUpdates.seq_start)) {}
          for (i2 = 1;; i2 = 0)
          {
            if (i2 == 0) {
              break label4124;
            }
            processUpdateArray(paramUpdates.updates, paramUpdates.users, paramUpdates.chats, false);
            localObject4 = localObject3;
            i2 = i3;
            n = k;
            i = i1;
            if (paramUpdates.seq == 0) {
              break;
            }
            if (paramUpdates.date != 0) {
              MessagesStorage.getInstance(this.currentAccount).setLastDateValue(paramUpdates.date);
            }
            MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(paramUpdates.seq);
            localObject4 = localObject3;
            i2 = i3;
            n = k;
            i = i1;
            break;
          }
        }
        if ((MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 == paramUpdates.seq) || (paramUpdates.seq == 0) || (paramUpdates.seq == MessagesStorage.getInstance(this.currentAccount).getLastSeqValue())) {}
        for (i2 = 1;; i2 = 0) {
          break;
        }
        label4124:
        if (BuildVars.LOGS_ENABLED)
        {
          if (!(paramUpdates instanceof TLRPC.TL_updatesCombined)) {
            break label4272;
          }
          FileLog.d("need get diff TL_updatesCombined, seq: " + MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + " " + paramUpdates.seq_start);
        }
        for (;;)
        {
          if ((!this.gettingDifference) && (this.updatesStartWaitTimeSeq != 0L) && (Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) > 1500L)) {
            break label4320;
          }
          if (this.updatesStartWaitTimeSeq == 0L) {
            this.updatesStartWaitTimeSeq = System.currentTimeMillis();
          }
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d("add TL_updates/Combined to queue");
          }
          this.updatesQueueSeq.add(paramUpdates);
          localObject4 = localObject3;
          i2 = i3;
          n = k;
          i = i1;
          break;
          label4272:
          FileLog.d("need get diff TL_updates, seq: " + MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + " " + paramUpdates.seq);
        }
        label4320:
        i2 = 1;
        localObject4 = localObject3;
        n = k;
        i = i1;
        break;
      }
      if ((paramUpdates instanceof TLRPC.TL_updatesTooLong))
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.d("need get diff TL_updatesTooLong");
        }
        i2 = 1;
        localObject4 = localObject2;
        n = m;
        i = i1;
        break;
      }
      if ((paramUpdates instanceof UserActionUpdatesSeq))
      {
        MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(paramUpdates.seq);
        localObject4 = localObject2;
        i2 = j;
        n = m;
        i = i1;
        break;
      }
      localObject4 = localObject2;
      i2 = j;
      n = m;
      i = i1;
      if (!(paramUpdates instanceof UserActionUpdatesPts)) {
        break;
      }
      if (paramUpdates.chat_id != 0)
      {
        this.channelsPts.put(paramUpdates.chat_id, paramUpdates.pts);
        MessagesStorage.getInstance(this.currentAccount).saveChannelPts(paramUpdates.chat_id, paramUpdates.pts);
        localObject4 = localObject2;
        i2 = j;
        n = m;
        i = i1;
        break;
      }
      MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(paramUpdates.pts);
      localObject4 = localObject2;
      i2 = j;
      n = m;
      i = i1;
      break;
      label4530:
      processChannelsUpdatesQueue(k, 0);
    }
    label4540:
    if (i2 != 0) {
      getDifference();
    }
    for (;;)
    {
      label4549:
      if (n != 0)
      {
        paramUpdates = new TLRPC.TL_messages_receivedQueue();
        paramUpdates.max_qts = MessagesStorage.getInstance(this.currentAccount).getLastQtsValue();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramUpdates, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
        });
      }
      if (i != 0) {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
          }
        });
      }
      MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
      return;
      for (i2 = 0; i2 < 3; i2++) {
        processUpdatesQueue(i2, 0);
      }
    }
  }
  
  public void putChat(final TLRPC.Chat paramChat, boolean paramBoolean)
  {
    if (paramChat == null) {}
    TLRPC.Chat localChat;
    label207:
    label312:
    label366:
    label372:
    do
    {
      for (;;)
      {
        return;
        localChat = (TLRPC.Chat)this.chats.get(Integer.valueOf(paramChat.id));
        if (localChat != paramChat)
        {
          if ((localChat != null) && (!TextUtils.isEmpty(localChat.username))) {
            this.objectsByUsernames.remove(localChat.username.toLowerCase());
          }
          if (!TextUtils.isEmpty(paramChat.username)) {
            this.objectsByUsernames.put(paramChat.username.toLowerCase(), paramChat);
          }
          if (paramChat.min)
          {
            if (localChat != null)
            {
              if (!paramBoolean)
              {
                localChat.title = paramChat.title;
                localChat.photo = paramChat.photo;
                localChat.broadcast = paramChat.broadcast;
                localChat.verified = paramChat.verified;
                localChat.megagroup = paramChat.megagroup;
                localChat.democracy = paramChat.democracy;
                if (paramChat.username != null)
                {
                  localChat.username = paramChat.username;
                  localChat.flags |= 0x40;
                }
                for (;;)
                {
                  if (paramChat.participants_count == 0) {
                    break label207;
                  }
                  localChat.participants_count = paramChat.participants_count;
                  break;
                  localChat.flags &= 0xFFFFFFBF;
                  localChat.username = null;
                }
              }
            }
            else {
              this.chats.put(Integer.valueOf(paramChat.id), paramChat);
            }
          }
          else
          {
            if (!paramBoolean)
            {
              int i;
              if (localChat != null)
              {
                if (paramChat.version != localChat.version) {
                  this.loadedFullChats.remove(Integer.valueOf(paramChat.id));
                }
                if ((localChat.participants_count != 0) && (paramChat.participants_count == 0))
                {
                  paramChat.participants_count = localChat.participants_count;
                  paramChat.flags |= 0x20000;
                }
                if (localChat.banned_rights == null) {
                  break label366;
                }
                i = localChat.banned_rights.flags;
                if (paramChat.banned_rights == null) {
                  break label372;
                }
              }
              for (int j = paramChat.banned_rights.flags;; j = 0)
              {
                if (i != j) {
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.channelRightsUpdated, new Object[] { paramChat });
                    }
                  });
                }
                this.chats.put(Integer.valueOf(paramChat.id), paramChat);
                break;
                i = 0;
                break label312;
              }
            }
            if (localChat != null) {
              break;
            }
            this.chats.put(Integer.valueOf(paramChat.id), paramChat);
          }
        }
      }
    } while (!localChat.min);
    paramChat.min = false;
    paramChat.title = localChat.title;
    paramChat.photo = localChat.photo;
    paramChat.broadcast = localChat.broadcast;
    paramChat.verified = localChat.verified;
    paramChat.megagroup = localChat.megagroup;
    paramChat.democracy = localChat.democracy;
    if (localChat.username != null)
    {
      paramChat.username = localChat.username;
      paramChat.flags |= 0x40;
    }
    for (;;)
    {
      if ((localChat.participants_count != 0) && (paramChat.participants_count == 0))
      {
        paramChat.participants_count = localChat.participants_count;
        paramChat.flags |= 0x20000;
      }
      this.chats.put(Integer.valueOf(paramChat.id), paramChat);
      break;
      paramChat.flags &= 0xFFFFFFBF;
      paramChat.username = null;
    }
  }
  
  public void putChats(ArrayList<TLRPC.Chat> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      int i = paramArrayList.size();
      for (int j = 0; j < i; j++) {
        putChat((TLRPC.Chat)paramArrayList.get(j), paramBoolean);
      }
    }
  }
  
  public void putEncryptedChat(TLRPC.EncryptedChat paramEncryptedChat, boolean paramBoolean)
  {
    if (paramEncryptedChat == null) {}
    for (;;)
    {
      return;
      if (paramBoolean) {
        this.encryptedChats.putIfAbsent(Integer.valueOf(paramEncryptedChat.id), paramEncryptedChat);
      } else {
        this.encryptedChats.put(Integer.valueOf(paramEncryptedChat.id), paramEncryptedChat);
      }
    }
  }
  
  public void putEncryptedChats(ArrayList<TLRPC.EncryptedChat> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      int i = paramArrayList.size();
      for (int j = 0; j < i; j++) {
        putEncryptedChat((TLRPC.EncryptedChat)paramArrayList.get(j), paramBoolean);
      }
    }
  }
  
  public boolean putUser(TLRPC.User paramUser, boolean paramBoolean)
  {
    boolean bool = false;
    if (paramUser == null) {
      paramBoolean = bool;
    }
    label41:
    TLRPC.User localUser;
    label221:
    label243:
    label267:
    label288:
    do
    {
      for (;;)
      {
        return paramBoolean;
        int i;
        if ((paramBoolean) && (paramUser.id / 1000 != 333) && (paramUser.id != 777000))
        {
          i = 1;
          localUser = (TLRPC.User)this.users.get(Integer.valueOf(paramUser.id));
          paramBoolean = bool;
          if (localUser == paramUser) {
            continue;
          }
          if ((localUser != null) && (!TextUtils.isEmpty(localUser.username))) {
            this.objectsByUsernames.remove(localUser.username.toLowerCase());
          }
          if (!TextUtils.isEmpty(paramUser.username)) {
            this.objectsByUsernames.put(paramUser.username.toLowerCase(), paramUser);
          }
          if (!paramUser.min) {
            break label288;
          }
          if (localUser == null) {
            break label267;
          }
          paramBoolean = bool;
          if (i != 0) {
            continue;
          }
          if (paramUser.bot)
          {
            if (paramUser.username == null) {
              break label221;
            }
            localUser.username = paramUser.username;
            localUser.flags |= 0x8;
          }
        }
        for (;;)
        {
          if (paramUser.photo == null) {
            break label243;
          }
          localUser.photo = paramUser.photo;
          localUser.flags |= 0x20;
          paramBoolean = bool;
          break;
          i = 0;
          break label41;
          localUser.flags &= 0xFFFFFFF7;
          localUser.username = null;
        }
        localUser.flags &= 0xFFFFFFDF;
        localUser.photo = null;
        paramBoolean = bool;
        continue;
        this.users.put(Integer.valueOf(paramUser.id), paramUser);
        paramBoolean = bool;
        continue;
        if (i == 0)
        {
          this.users.put(Integer.valueOf(paramUser.id), paramUser);
          if (paramUser.id == UserConfig.getInstance(this.currentAccount).getClientUserId())
          {
            UserConfig.getInstance(this.currentAccount).setCurrentUser(paramUser);
            UserConfig.getInstance(this.currentAccount).saveConfig(true);
          }
          paramBoolean = bool;
          if (localUser != null)
          {
            paramBoolean = bool;
            if (paramUser.status != null)
            {
              paramBoolean = bool;
              if (localUser.status != null)
              {
                paramBoolean = bool;
                if (paramUser.status.expires != localUser.status.expires) {
                  paramBoolean = true;
                }
              }
            }
          }
        }
        else
        {
          if (localUser != null) {
            break;
          }
          this.users.put(Integer.valueOf(paramUser.id), paramUser);
          paramBoolean = bool;
        }
      }
      paramBoolean = bool;
    } while (!localUser.min);
    paramUser.min = false;
    if (localUser.bot)
    {
      if (localUser.username != null)
      {
        paramUser.username = localUser.username;
        paramUser.flags |= 0x8;
      }
    }
    else
    {
      label476:
      if (localUser.photo == null) {
        break label544;
      }
      paramUser.photo = localUser.photo;
      paramUser.flags |= 0x20;
    }
    for (;;)
    {
      this.users.put(Integer.valueOf(paramUser.id), paramUser);
      paramBoolean = bool;
      break;
      paramUser.flags &= 0xFFFFFFF7;
      paramUser.username = null;
      break label476;
      label544:
      paramUser.flags &= 0xFFFFFFDF;
      paramUser.photo = null;
    }
  }
  
  public void putUsers(ArrayList<TLRPC.User> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    for (;;)
    {
      return;
      int i = 0;
      int j = paramArrayList.size();
      for (int k = 0; k < j; k++) {
        if (putUser((TLRPC.User)paramArrayList.get(k), paramBoolean)) {
          i = 1;
        }
      }
      if (i != 0) {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
          }
        });
      }
    }
  }
  
  public void registerForPush(final String paramString)
  {
    if ((TextUtils.isEmpty(paramString)) || (this.registeringForPush) || (UserConfig.getInstance(this.currentAccount).getClientUserId() == 0)) {}
    for (;;)
    {
      return;
      if ((!UserConfig.getInstance(this.currentAccount).registeredForPush) || (!paramString.equals(SharedConfig.pushString)))
      {
        this.registeringForPush = true;
        this.lastPushRegisterSendTime = SystemClock.uptimeMillis();
        if (SharedConfig.pushAuthKey == null)
        {
          SharedConfig.pushAuthKey = new byte['Ā'];
          Utilities.random.nextBytes(SharedConfig.pushAuthKey);
          SharedConfig.saveConfig();
        }
        TLRPC.TL_account_registerDevice localTL_account_registerDevice = new TLRPC.TL_account_registerDevice();
        localTL_account_registerDevice.token_type = 2;
        localTL_account_registerDevice.token = paramString;
        localTL_account_registerDevice.secret = SharedConfig.pushAuthKey;
        for (int i = 0; i < 3; i++)
        {
          UserConfig localUserConfig = UserConfig.getInstance(i);
          if ((i != this.currentAccount) && (localUserConfig.isClientActivated())) {
            localTL_account_registerDevice.other_uids.add(Integer.valueOf(localUserConfig.getClientUserId()));
          }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_account_registerDevice, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
          {
            if ((paramAnonymousTLObject instanceof TLRPC.TL_boolTrue))
            {
              if (BuildVars.LOGS_ENABLED) {
                FileLog.d("account " + MessagesController.this.currentAccount + " registered for push");
              }
              UserConfig.getInstance(MessagesController.this.currentAccount).registeredForPush = true;
              SharedConfig.pushString = paramString;
              UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.registeringForPush = false;
              }
            });
          }
        });
      }
    }
  }
  
  public void reloadMentionsCountForChannels(final ArrayList<Integer> paramArrayList)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        for (int i = 0; i < paramArrayList.size(); i++)
        {
          final long l = -((Integer)paramArrayList.get(i)).intValue();
          TLRPC.TL_messages_getUnreadMentions localTL_messages_getUnreadMentions = new TLRPC.TL_messages_getUnreadMentions();
          localTL_messages_getUnreadMentions.peer = MessagesController.this.getInputPeer((int)l);
          localTL_messages_getUnreadMentions.limit = 1;
          ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(localTL_messages_getUnreadMentions, new RequestDelegate()
          {
            public void run(final TLObject paramAnonymous2TLObject, TLRPC.TL_error paramAnonymous2TL_error)
            {
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)paramAnonymous2TLObject;
                  if (localmessages_Messages != null) {
                    if (localmessages_Messages.count == 0) {
                      break label52;
                    }
                  }
                  label52:
                  for (int i = localmessages_Messages.count;; i = localmessages_Messages.messages.size())
                  {
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).resetMentionsCount(MessagesController.70.1.this.val$dialog_id, i);
                    return;
                  }
                }
              });
            }
          });
        }
      }
    });
  }
  
  public void reloadWebPages(final long paramLong, HashMap<String, ArrayList<MessageObject>> paramHashMap)
  {
    Iterator localIterator = paramHashMap.entrySet().iterator();
    while (localIterator.hasNext())
    {
      paramHashMap = (Map.Entry)localIterator.next();
      final String str = (String)paramHashMap.getKey();
      ArrayList localArrayList1 = (ArrayList)paramHashMap.getValue();
      ArrayList localArrayList2 = (ArrayList)this.reloadingWebpages.get(str);
      paramHashMap = localArrayList2;
      if (localArrayList2 == null)
      {
        paramHashMap = new ArrayList();
        this.reloadingWebpages.put(str, paramHashMap);
      }
      paramHashMap.addAll(localArrayList1);
      paramHashMap = new TLRPC.TL_messages_getWebPagePreview();
      paramHashMap.message = str;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramHashMap, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              ArrayList localArrayList = (ArrayList)MessagesController.this.reloadingWebpages.remove(MessagesController.61.this.val$url);
              if (localArrayList == null) {}
              for (;;)
              {
                return;
                TLRPC.TL_messages_messages localTL_messages_messages = new TLRPC.TL_messages_messages();
                int i;
                if (!(paramAnonymousTLObject instanceof TLRPC.TL_messageMediaWebPage)) {
                  for (i = 0; i < localArrayList.size(); i++)
                  {
                    ((MessageObject)localArrayList.get(i)).messageOwner.media.webpage = new TLRPC.TL_webPageEmpty();
                    localTL_messages_messages.messages.add(((MessageObject)localArrayList.get(i)).messageOwner);
                  }
                }
                TLRPC.TL_messageMediaWebPage localTL_messageMediaWebPage = (TLRPC.TL_messageMediaWebPage)paramAnonymousTLObject;
                if (((localTL_messageMediaWebPage.webpage instanceof TLRPC.TL_webPage)) || ((localTL_messageMediaWebPage.webpage instanceof TLRPC.TL_webPageEmpty))) {
                  i = 0;
                }
                while (i < localArrayList.size())
                {
                  ((MessageObject)localArrayList.get(i)).messageOwner.media.webpage = localTL_messageMediaWebPage.webpage;
                  if (i == 0) {
                    ImageLoader.saveMessageThumbs(((MessageObject)localArrayList.get(i)).messageOwner);
                  }
                  localTL_messages_messages.messages.add(((MessageObject)localArrayList.get(i)).messageOwner);
                  i++;
                  continue;
                  MessagesController.this.reloadingWebpagesPending.put(localTL_messageMediaWebPage.webpage.id, localArrayList);
                }
                if (!localTL_messages_messages.messages.isEmpty())
                {
                  MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(localTL_messages_messages, MessagesController.61.this.val$dialog_id, -2, 0, false);
                  NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, new Object[] { Long.valueOf(MessagesController.61.this.val$dialog_id), localArrayList });
                }
              }
            }
          });
        }
      });
    }
  }
  
  public void reportSpam(long paramLong, TLRPC.User paramUser, TLRPC.Chat paramChat, TLRPC.EncryptedChat paramEncryptedChat)
  {
    if ((paramUser == null) && (paramChat == null) && (paramEncryptedChat == null)) {}
    for (;;)
    {
      return;
      SharedPreferences.Editor localEditor = this.notificationsPreferences.edit();
      localEditor.putInt("spam3_" + paramLong, 1);
      localEditor.commit();
      if ((int)paramLong != 0) {
        break;
      }
      if ((paramEncryptedChat != null) && (paramEncryptedChat.access_hash != 0L))
      {
        paramUser = new TLRPC.TL_messages_reportEncryptedSpam();
        paramUser.peer = new TLRPC.TL_inputEncryptedChat();
        paramUser.peer.chat_id = paramEncryptedChat.id;
        paramUser.peer.access_hash = paramEncryptedChat.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramUser, new RequestDelegate()
        {
          public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
        }, 2);
      }
    }
    paramEncryptedChat = new TLRPC.TL_messages_reportSpam();
    if (paramChat != null) {
      paramEncryptedChat.peer = getInputPeer(-paramChat.id);
    }
    for (;;)
    {
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(paramEncryptedChat, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      }, 2);
      break;
      if (paramUser != null) {
        paramEncryptedChat.peer = getInputPeer(paramUser.id);
      }
    }
  }
  
  public void saveGif(TLRPC.Document paramDocument)
  {
    TLRPC.TL_messages_saveGif localTL_messages_saveGif = new TLRPC.TL_messages_saveGif();
    localTL_messages_saveGif.id = new TLRPC.TL_inputDocument();
    localTL_messages_saveGif.id.id = paramDocument.id;
    localTL_messages_saveGif.id.access_hash = paramDocument.access_hash;
    localTL_messages_saveGif.unsave = false;
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_saveGif, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  public void saveRecentSticker(TLRPC.Document paramDocument, boolean paramBoolean)
  {
    TLRPC.TL_messages_saveRecentSticker localTL_messages_saveRecentSticker = new TLRPC.TL_messages_saveRecentSticker();
    localTL_messages_saveRecentSticker.id = new TLRPC.TL_inputDocument();
    localTL_messages_saveRecentSticker.id.id = paramDocument.id;
    localTL_messages_saveRecentSticker.id.access_hash = paramDocument.access_hash;
    localTL_messages_saveRecentSticker.unsave = false;
    localTL_messages_saveRecentSticker.attached = paramBoolean;
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_saveRecentSticker, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  public void sendBotStart(TLRPC.User paramUser, String paramString)
  {
    if (paramUser == null) {}
    for (;;)
    {
      return;
      TLRPC.TL_messages_startBot localTL_messages_startBot = new TLRPC.TL_messages_startBot();
      localTL_messages_startBot.bot = getInputUser(paramUser);
      localTL_messages_startBot.peer = getInputPeer(paramUser.id);
      localTL_messages_startBot.start_param = paramString;
      localTL_messages_startBot.random_id = Utilities.random.nextLong();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_startBot, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error != null) {}
          for (;;)
          {
            return;
            MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          }
        }
      });
    }
  }
  
  public void sendTyping(final long paramLong, final int paramInt1, int paramInt2)
  {
    if (paramLong == 0L) {}
    for (;;)
    {
      return;
      Object localObject1 = (LongSparseArray)this.sendingTypings.get(paramInt1);
      if ((localObject1 == null) || (((LongSparseArray)localObject1).get(paramLong) == null))
      {
        Object localObject2 = localObject1;
        if (localObject1 == null)
        {
          localObject2 = new LongSparseArray();
          this.sendingTypings.put(paramInt1, localObject2);
        }
        int i = (int)paramLong;
        int j = (int)(paramLong >> 32);
        Object localObject3;
        if (i != 0)
        {
          if (j != 1)
          {
            localObject3 = new TLRPC.TL_messages_setTyping();
            ((TLRPC.TL_messages_setTyping)localObject3).peer = getInputPeer(i);
            if ((((TLRPC.TL_messages_setTyping)localObject3).peer instanceof TLRPC.TL_inputPeerChannel))
            {
              localObject1 = getChat(Integer.valueOf(((TLRPC.TL_messages_setTyping)localObject3).peer.channel_id));
              if ((localObject1 == null) || (!((TLRPC.Chat)localObject1).megagroup)) {}
            }
            else if (((TLRPC.TL_messages_setTyping)localObject3).peer != null)
            {
              if (paramInt1 == 0) {
                ((TLRPC.TL_messages_setTyping)localObject3).action = new TLRPC.TL_sendMessageTypingAction();
              }
              for (;;)
              {
                ((LongSparseArray)localObject2).put(paramLong, Boolean.valueOf(true));
                paramInt1 = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject3, new RequestDelegate()
                {
                  public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        LongSparseArray localLongSparseArray = (LongSparseArray)MessagesController.this.sendingTypings.get(MessagesController.57.this.val$action);
                        if (localLongSparseArray != null) {
                          localLongSparseArray.remove(MessagesController.57.this.val$dialog_id);
                        }
                      }
                    });
                  }
                }, 2);
                if (paramInt2 == 0) {
                  break;
                }
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(paramInt1, paramInt2);
                break;
                if (paramInt1 == 1) {
                  ((TLRPC.TL_messages_setTyping)localObject3).action = new TLRPC.TL_sendMessageRecordAudioAction();
                } else if (paramInt1 == 2) {
                  ((TLRPC.TL_messages_setTyping)localObject3).action = new TLRPC.TL_sendMessageCancelAction();
                } else if (paramInt1 == 3) {
                  ((TLRPC.TL_messages_setTyping)localObject3).action = new TLRPC.TL_sendMessageUploadDocumentAction();
                } else if (paramInt1 == 4) {
                  ((TLRPC.TL_messages_setTyping)localObject3).action = new TLRPC.TL_sendMessageUploadPhotoAction();
                } else if (paramInt1 == 5) {
                  ((TLRPC.TL_messages_setTyping)localObject3).action = new TLRPC.TL_sendMessageUploadVideoAction();
                } else if (paramInt1 == 6) {
                  ((TLRPC.TL_messages_setTyping)localObject3).action = new TLRPC.TL_sendMessageGamePlayAction();
                } else if (paramInt1 == 7) {
                  ((TLRPC.TL_messages_setTyping)localObject3).action = new TLRPC.TL_sendMessageRecordRoundAction();
                } else if (paramInt1 == 8) {
                  ((TLRPC.TL_messages_setTyping)localObject3).action = new TLRPC.TL_sendMessageUploadRoundAction();
                } else if (paramInt1 == 9) {
                  ((TLRPC.TL_messages_setTyping)localObject3).action = new TLRPC.TL_sendMessageUploadAudioAction();
                }
              }
            }
          }
        }
        else if (paramInt1 == 0)
        {
          localObject1 = getEncryptedChat(Integer.valueOf(j));
          if ((((TLRPC.EncryptedChat)localObject1).auth_key != null) && (((TLRPC.EncryptedChat)localObject1).auth_key.length > 1) && ((localObject1 instanceof TLRPC.TL_encryptedChat)))
          {
            localObject3 = new TLRPC.TL_messages_setEncryptedTyping();
            ((TLRPC.TL_messages_setEncryptedTyping)localObject3).peer = new TLRPC.TL_inputEncryptedChat();
            ((TLRPC.TL_messages_setEncryptedTyping)localObject3).peer.chat_id = ((TLRPC.EncryptedChat)localObject1).id;
            ((TLRPC.TL_messages_setEncryptedTyping)localObject3).peer.access_hash = ((TLRPC.EncryptedChat)localObject1).access_hash;
            ((TLRPC.TL_messages_setEncryptedTyping)localObject3).typing = true;
            ((LongSparseArray)localObject2).put(paramLong, Boolean.valueOf(true));
            paramInt1 = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject3, new RequestDelegate()
            {
              public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    LongSparseArray localLongSparseArray = (LongSparseArray)MessagesController.this.sendingTypings.get(MessagesController.58.this.val$action);
                    if (localLongSparseArray != null) {
                      localLongSparseArray.remove(MessagesController.58.this.val$dialog_id);
                    }
                  }
                });
              }
            }, 2);
            if (paramInt2 != 0) {
              ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(paramInt1, paramInt2);
            }
          }
        }
      }
    }
  }
  
  public void setLastCreatedDialogId(final long paramLong, final boolean paramBoolean)
  {
    if (paramBoolean) {
      this.createdDialogMainThreadIds.add(Long.valueOf(paramLong));
    }
    for (;;)
    {
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          if (paramBoolean) {
            MessagesController.this.createdDialogIds.add(Long.valueOf(paramLong));
          }
          for (;;)
          {
            return;
            MessagesController.this.createdDialogIds.remove(Long.valueOf(paramLong));
          }
        }
      });
      return;
      this.createdDialogMainThreadIds.remove(Long.valueOf(paramLong));
    }
  }
  
  public void setReferer(String paramString)
  {
    if (paramString == null) {}
    for (;;)
    {
      return;
      this.installReferer = paramString;
      this.mainPreferences.edit().putString("installReferer", paramString).commit();
    }
  }
  
  public void setUserAdminRole(final int paramInt, TLRPC.User paramUser, TLRPC.TL_channelAdminRights paramTL_channelAdminRights, final boolean paramBoolean, final BaseFragment paramBaseFragment)
  {
    if ((paramUser == null) || (paramTL_channelAdminRights == null)) {}
    for (;;)
    {
      return;
      final TLRPC.TL_channels_editAdmin localTL_channels_editAdmin = new TLRPC.TL_channels_editAdmin();
      localTL_channels_editAdmin.channel = getInputChannel(paramInt);
      localTL_channels_editAdmin.user_id = getInputUser(paramUser);
      localTL_channels_editAdmin.admin_rights = paramTL_channelAdminRights;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_editAdmin, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.loadFullChat(MessagesController.32.this.val$chatId, 0, true);
              }
            }, 1000L);
          }
          for (;;)
          {
            return;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                boolean bool = true;
                int i = MessagesController.this.currentAccount;
                TLRPC.TL_error localTL_error = paramAnonymousTL_error;
                BaseFragment localBaseFragment = MessagesController.32.this.val$parentFragment;
                TLRPC.TL_channels_editAdmin localTL_channels_editAdmin = MessagesController.32.this.val$req;
                if (!MessagesController.32.this.val$isMegagroup) {}
                for (;;)
                {
                  AlertsCreator.processError(i, localTL_error, localBaseFragment, localTL_channels_editAdmin, new Object[] { Boolean.valueOf(bool) });
                  return;
                  bool = false;
                }
              }
            });
          }
        }
      });
    }
  }
  
  public void setUserBannedRole(final int paramInt, TLRPC.User paramUser, TLRPC.TL_channelBannedRights paramTL_channelBannedRights, final boolean paramBoolean, final BaseFragment paramBaseFragment)
  {
    if ((paramUser == null) || (paramTL_channelBannedRights == null)) {}
    for (;;)
    {
      return;
      final TLRPC.TL_channels_editBanned localTL_channels_editBanned = new TLRPC.TL_channels_editBanned();
      localTL_channels_editBanned.channel = getInputChannel(paramInt);
      localTL_channels_editBanned.user_id = getInputUser(paramUser);
      localTL_channels_editBanned.banned_rights = paramTL_channelBannedRights;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_editBanned, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTL_error == null)
          {
            MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.this.loadFullChat(MessagesController.31.this.val$chatId, 0, true);
              }
            }, 1000L);
          }
          for (;;)
          {
            return;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                boolean bool = true;
                int i = MessagesController.this.currentAccount;
                TLRPC.TL_error localTL_error = paramAnonymousTL_error;
                BaseFragment localBaseFragment = MessagesController.31.this.val$parentFragment;
                TLRPC.TL_channels_editBanned localTL_channels_editBanned = MessagesController.31.this.val$req;
                if (!MessagesController.31.this.val$isMegagroup) {}
                for (;;)
                {
                  AlertsCreator.processError(i, localTL_error, localBaseFragment, localTL_channels_editBanned, new Object[] { Boolean.valueOf(bool) });
                  return;
                  bool = false;
                }
              }
            });
          }
        }
      });
    }
  }
  
  public void sortDialogs(SparseArray<TLRPC.Chat> paramSparseArray)
  {
    this.dialogsServerOnly.clear();
    this.dialogsGroupsOnly.clear();
    this.dialogsForward.clear();
    int i = 0;
    int j = UserConfig.getInstance(this.currentAccount).getClientUserId();
    Collections.sort(this.dialogs, this.dialogComparator);
    int k = 0;
    Object localObject;
    if (k < this.dialogs.size())
    {
      localObject = (TLRPC.TL_dialog)this.dialogs.get(k);
      int m = (int)(((TLRPC.TL_dialog)localObject).id >> 32);
      int n = (int)((TLRPC.TL_dialog)localObject).id;
      label111:
      int i1;
      TLRPC.Chat localChat;
      if (n == j)
      {
        this.dialogsForward.add(0, localObject);
        i = 1;
        i1 = k;
        if (n != 0)
        {
          i1 = k;
          if (m != 1)
          {
            this.dialogsServerOnly.add(localObject);
            if (!DialogObject.isChannel((TLRPC.TL_dialog)localObject)) {
              break label255;
            }
            localChat = getChat(Integer.valueOf(-n));
            i1 = k;
            if (localChat != null) {
              if ((!localChat.megagroup) || (localChat.admin_rights == null) || ((!localChat.admin_rights.post_messages) && (!localChat.admin_rights.add_admins)))
              {
                i1 = k;
                if (!localChat.creator) {}
              }
              else
              {
                this.dialogsGroupsOnly.add(localObject);
                i1 = k;
              }
            }
          }
        }
      }
      for (;;)
      {
        k = i1 + 1;
        break;
        this.dialogsForward.add(localObject);
        break label111;
        label255:
        i1 = k;
        if (n < 0)
        {
          if (paramSparseArray != null)
          {
            localChat = (TLRPC.Chat)paramSparseArray.get(-n);
            if ((localChat != null) && (localChat.migrated_to != null))
            {
              this.dialogs.remove(k);
              i1 = k - 1;
              continue;
            }
          }
          this.dialogsGroupsOnly.add(localObject);
          i1 = k;
        }
      }
    }
    if (i == 0)
    {
      localObject = UserConfig.getInstance(this.currentAccount).getCurrentUser();
      if (localObject != null)
      {
        paramSparseArray = new TLRPC.TL_dialog();
        paramSparseArray.id = ((TLRPC.User)localObject).id;
        paramSparseArray.notify_settings = new TLRPC.TL_peerNotifySettings();
        paramSparseArray.peer = new TLRPC.TL_peerUser();
        paramSparseArray.peer.user_id = ((TLRPC.User)localObject).id;
        this.dialogsForward.add(0, paramSparseArray);
      }
    }
  }
  
  public void startShortPoll(final int paramInt, final boolean paramBoolean)
  {
    Utilities.stageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        if (paramBoolean) {
          MessagesController.this.needShortPollChannels.delete(paramInt);
        }
        for (;;)
        {
          return;
          MessagesController.this.needShortPollChannels.put(paramInt, 0);
          if (MessagesController.this.shortPollChannels.indexOfKey(paramInt) < 0) {
            MessagesController.this.getChannelDifference(paramInt, 3, 0L, null);
          }
        }
      }
    });
  }
  
  public void toggleAdminMode(final int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_messages_toggleChatAdmins localTL_messages_toggleChatAdmins = new TLRPC.TL_messages_toggleChatAdmins();
    localTL_messages_toggleChatAdmins.chat_id = paramInt;
    localTL_messages_toggleChatAdmins.enabled = paramBoolean;
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_toggleChatAdmins, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTL_error == null)
        {
          MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          MessagesController.this.loadFullChat(paramInt, 0, true);
        }
      }
    });
  }
  
  public void toggleUserAdmin(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    TLRPC.TL_messages_editChatAdmin localTL_messages_editChatAdmin = new TLRPC.TL_messages_editChatAdmin();
    localTL_messages_editChatAdmin.chat_id = paramInt1;
    localTL_messages_editChatAdmin.user_id = getInputUser(paramInt2);
    localTL_messages_editChatAdmin.is_admin = paramBoolean;
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_editChatAdmin, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
    });
  }
  
  public void toogleChannelInvites(int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_channels_toggleInvites localTL_channels_toggleInvites = new TLRPC.TL_channels_toggleInvites();
    localTL_channels_toggleInvites.channel = getInputChannel(paramInt);
    localTL_channels_toggleInvites.enabled = paramBoolean;
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_toggleInvites, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTLObject != null) {
          MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
        }
      }
    }, 64);
  }
  
  public void toogleChannelInvitesHistory(int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_channels_togglePreHistoryHidden localTL_channels_togglePreHistoryHidden = new TLRPC.TL_channels_togglePreHistoryHidden();
    localTL_channels_togglePreHistoryHidden.channel = getInputChannel(paramInt);
    localTL_channels_togglePreHistoryHidden.enabled = paramBoolean;
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_togglePreHistoryHidden, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTLObject != null)
        {
          MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(8192) });
            }
          });
        }
      }
    }, 64);
  }
  
  public void toogleChannelSignatures(int paramInt, boolean paramBoolean)
  {
    TLRPC.TL_channels_toggleSignatures localTL_channels_toggleSignatures = new TLRPC.TL_channels_toggleSignatures();
    localTL_channels_toggleSignatures.channel = getInputChannel(paramInt);
    localTL_channels_toggleSignatures.enabled = paramBoolean;
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_toggleSignatures, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if (paramAnonymousTLObject != null)
        {
          MessagesController.this.processUpdates((TLRPC.Updates)paramAnonymousTLObject, false);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(8192) });
            }
          });
        }
      }
    }, 64);
  }
  
  public void unblockUser(int paramInt)
  {
    TLRPC.TL_contacts_unblock localTL_contacts_unblock = new TLRPC.TL_contacts_unblock();
    final TLRPC.User localUser = getUser(Integer.valueOf(paramInt));
    if (localUser == null) {}
    for (;;)
    {
      return;
      this.blockedUsers.remove(Integer.valueOf(localUser.id));
      localTL_contacts_unblock.id = getInputUser(localUser);
      NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_contacts_unblock, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          MessagesStorage.getInstance(MessagesController.this.currentAccount).deleteBlockedUser(localUser.id);
        }
      });
    }
  }
  
  public void unregistedPush()
  {
    if ((UserConfig.getInstance(this.currentAccount).registeredForPush) && (SharedConfig.pushString.length() == 0))
    {
      TLRPC.TL_account_unregisterDevice localTL_account_unregisterDevice = new TLRPC.TL_account_unregisterDevice();
      localTL_account_unregisterDevice.token = SharedConfig.pushString;
      localTL_account_unregisterDevice.token_type = 2;
      for (int i = 0; i < 3; i++)
      {
        UserConfig localUserConfig = UserConfig.getInstance(i);
        if ((i != this.currentAccount) && (localUserConfig.isClientActivated())) {
          localTL_account_unregisterDevice.other_uids.add(Integer.valueOf(localUserConfig.getClientUserId()));
        }
      }
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_account_unregisterDevice, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error) {}
      });
    }
  }
  
  public void updateChannelAbout(int paramInt, final String paramString, final TLRPC.ChatFull paramChatFull)
  {
    if (paramChatFull == null) {}
    for (;;)
    {
      return;
      TLRPC.TL_channels_editAbout localTL_channels_editAbout = new TLRPC.TL_channels_editAbout();
      localTL_channels_editAbout.channel = getInputChannel(paramInt);
      localTL_channels_editAbout.about = paramString;
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_editAbout, new RequestDelegate()
      {
        public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if ((paramAnonymousTLObject instanceof TLRPC.TL_boolTrue)) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.95.this.val$info.about = MessagesController.95.this.val$about;
                MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatInfo(MessagesController.95.this.val$info, false);
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { MessagesController.95.this.val$info, Integer.valueOf(0), Boolean.valueOf(false), null });
              }
            });
          }
        }
      }, 64);
    }
  }
  
  public void updateChannelUserName(final int paramInt, final String paramString)
  {
    TLRPC.TL_channels_updateUsername localTL_channels_updateUsername = new TLRPC.TL_channels_updateUsername();
    localTL_channels_updateUsername.channel = getInputChannel(paramInt);
    localTL_channels_updateUsername.username = paramString;
    ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_updateUsername, new RequestDelegate()
    {
      public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        if ((paramAnonymousTLObject instanceof TLRPC.TL_boolTrue)) {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              TLRPC.Chat localChat = MessagesController.this.getChat(Integer.valueOf(MessagesController.96.this.val$chat_id));
              if (MessagesController.96.this.val$userName.length() != 0) {}
              for (localChat.flags |= 0x40;; localChat.flags &= 0xFFFFFFBF)
              {
                localChat.username = MessagesController.96.this.val$userName;
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localChat);
                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(null, localArrayList, true, true);
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(8192) });
                return;
              }
            }
          });
        }
      }
    }, 64);
  }
  
  public void updateConfig(final TLRPC.TL_config paramTL_config)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        LocaleController.getInstance().loadRemoteLanguages(MessagesController.this.currentAccount);
        MessagesController.this.maxMegagroupCount = paramTL_config.megagroup_size_max;
        MessagesController.this.maxGroupCount = paramTL_config.chat_size_max;
        MessagesController.this.maxEditTime = paramTL_config.edit_time_limit;
        MessagesController.this.ratingDecay = paramTL_config.rating_e_decay;
        MessagesController.this.maxRecentGifsCount = paramTL_config.saved_gifs_limit;
        MessagesController.this.maxRecentStickersCount = paramTL_config.stickers_recent_limit;
        MessagesController.this.maxFaveStickersCount = paramTL_config.stickers_faved_limit;
        MessagesController.this.revokeTimeLimit = paramTL_config.revoke_time_limit;
        MessagesController.this.revokeTimePmLimit = paramTL_config.revoke_pm_time_limit;
        MessagesController.this.canRevokePmInbox = paramTL_config.revoke_pm_inbox;
        MessagesController.this.linkPrefix = paramTL_config.me_url_prefix;
        if (MessagesController.this.linkPrefix.endsWith("/")) {
          MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(0, MessagesController.this.linkPrefix.length() - 1);
        }
        if (MessagesController.this.linkPrefix.startsWith("https://")) {
          MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(8);
        }
        for (;;)
        {
          MessagesController.this.callReceiveTimeout = paramTL_config.call_receive_timeout_ms;
          MessagesController.this.callRingTimeout = paramTL_config.call_ring_timeout_ms;
          MessagesController.this.callConnectTimeout = paramTL_config.call_connect_timeout_ms;
          MessagesController.this.callPacketTimeout = paramTL_config.call_packet_timeout_ms;
          MessagesController.this.maxPinnedDialogsCount = paramTL_config.pinned_dialogs_count_max;
          MessagesController.this.defaultP2pContacts = paramTL_config.default_p2p_contacts;
          MessagesController.this.preloadFeaturedStickers = paramTL_config.preload_featured_stickers;
          SharedPreferences.Editor localEditor = MessagesController.this.mainPreferences.edit();
          localEditor.putInt("maxGroupCount", MessagesController.this.maxGroupCount);
          localEditor.putInt("maxMegagroupCount", MessagesController.this.maxMegagroupCount);
          localEditor.putInt("maxEditTime", MessagesController.this.maxEditTime);
          localEditor.putInt("ratingDecay", MessagesController.this.ratingDecay);
          localEditor.putInt("maxRecentGifsCount", MessagesController.this.maxRecentGifsCount);
          localEditor.putInt("maxRecentStickersCount", MessagesController.this.maxRecentStickersCount);
          localEditor.putInt("maxFaveStickersCount", MessagesController.this.maxFaveStickersCount);
          localEditor.putInt("callReceiveTimeout", MessagesController.this.callReceiveTimeout);
          localEditor.putInt("callRingTimeout", MessagesController.this.callRingTimeout);
          localEditor.putInt("callConnectTimeout", MessagesController.this.callConnectTimeout);
          localEditor.putInt("callPacketTimeout", MessagesController.this.callPacketTimeout);
          localEditor.putString("linkPrefix", MessagesController.this.linkPrefix);
          localEditor.putInt("maxPinnedDialogsCount", MessagesController.this.maxPinnedDialogsCount);
          localEditor.putBoolean("defaultP2pContacts", MessagesController.this.defaultP2pContacts);
          localEditor.putBoolean("preloadFeaturedStickers", MessagesController.this.preloadFeaturedStickers);
          localEditor.putInt("revokeTimeLimit", MessagesController.this.revokeTimeLimit);
          localEditor.putInt("revokeTimePmLimit", MessagesController.this.revokeTimePmLimit);
          localEditor.putBoolean("canRevokePmInbox", MessagesController.this.canRevokePmInbox);
          localEditor.commit();
          LocaleController.getInstance().checkUpdateForCurrentRemoteLocale(MessagesController.this.currentAccount, paramTL_config.lang_pack_version);
          return;
          if (MessagesController.this.linkPrefix.startsWith("http://")) {
            MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(7);
          }
        }
      }
    });
  }
  
  protected void updateInterfaceWithMessages(long paramLong, ArrayList<MessageObject> paramArrayList)
  {
    updateInterfaceWithMessages(paramLong, paramArrayList, false);
  }
  
  protected void updateInterfaceWithMessages(long paramLong, ArrayList<MessageObject> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {}
    int i;
    Object localObject1;
    int j;
    int k;
    label32:
    int n;
    Object localObject2;
    label291:
    label333:
    do
    {
      for (;;)
      {
        return;
        int m;
        MessageObject localMessageObject;
        if ((int)paramLong == 0)
        {
          i = 1;
          localObject1 = null;
          j = 0;
          k = 0;
          m = 0;
          if (m >= paramArrayList.size()) {
            break label333;
          }
          localMessageObject = (MessageObject)paramArrayList.get(m);
          if ((localObject1 != null) && ((i != 0) || (localMessageObject.getId() <= ((MessageObject)localObject1).getId())) && (((i == 0) && ((localMessageObject.getId() >= 0) || (((MessageObject)localObject1).getId() >= 0))) || (localMessageObject.getId() >= ((MessageObject)localObject1).getId())))
          {
            n = j;
            localObject2 = localObject1;
            if (localMessageObject.messageOwner.date <= ((MessageObject)localObject1).messageOwner.date) {}
          }
          else
          {
            localObject1 = localMessageObject;
            n = j;
            localObject2 = localObject1;
            if (localMessageObject.messageOwner.to_id.channel_id != 0)
            {
              n = localMessageObject.messageOwner.to_id.channel_id;
              localObject2 = localObject1;
            }
          }
          if ((localMessageObject.isOut()) && (!localMessageObject.isSending()) && (!localMessageObject.isForwarded()))
          {
            if (!localMessageObject.isNewGif()) {
              break label291;
            }
            DataQuery.getInstance(this.currentAccount).addRecentGif(localMessageObject.messageOwner.media.document, localMessageObject.messageOwner.date);
          }
        }
        for (;;)
        {
          int i1 = k;
          if (localMessageObject.isOut())
          {
            i1 = k;
            if (localMessageObject.isSent()) {
              i1 = 1;
            }
          }
          m++;
          j = n;
          localObject1 = localObject2;
          k = i1;
          break label32;
          i = 0;
          break;
          if (localMessageObject.isSticker()) {
            DataQuery.getInstance(this.currentAccount).addRecentSticker(0, localMessageObject.messageOwner.media.document, localMessageObject.messageOwner.date, false);
          }
        }
        DataQuery.getInstance(this.currentAccount).loadReplyMessagesForMessages(paramArrayList, paramLong);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didReceivedNewMessages, new Object[] { Long.valueOf(paramLong), paramArrayList });
        if (localObject1 != null)
        {
          paramArrayList = (TLRPC.TL_dialog)this.dialogs_dict.get(paramLong);
          if (!(((MessageObject)localObject1).messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo)) {
            break;
          }
          if (paramArrayList != null)
          {
            this.dialogs.remove(paramArrayList);
            this.dialogsServerOnly.remove(paramArrayList);
            this.dialogsGroupsOnly.remove(paramArrayList);
            this.dialogs_dict.remove(paramArrayList.id);
            this.dialogs_read_inbox_max.remove(Long.valueOf(paramArrayList.id));
            this.dialogs_read_outbox_max.remove(Long.valueOf(paramArrayList.id));
            this.nextDialogsCacheOffset -= 1;
            this.dialogMessage.remove(paramArrayList.id);
            localObject1 = (MessageObject)this.dialogMessagesByIds.get(paramArrayList.top_message);
            this.dialogMessagesByIds.remove(paramArrayList.top_message);
            if ((localObject1 != null) && (((MessageObject)localObject1).messageOwner.random_id != 0L)) {
              this.dialogMessagesByRandomIds.remove(((MessageObject)localObject1).messageOwner.random_id);
            }
            paramArrayList.top_message = 0;
            NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(paramArrayList.id);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
          }
        }
      }
      n = 0;
      i = 0;
      if (paramArrayList != null) {
        break label835;
      }
      if (paramBoolean) {
        break;
      }
      localObject2 = getChat(Integer.valueOf(j));
    } while (((j != 0) && (localObject2 == null)) || ((localObject2 != null) && (((TLRPC.Chat)localObject2).left)));
    paramArrayList = new TLRPC.TL_dialog();
    paramArrayList.id = paramLong;
    paramArrayList.unread_count = 0;
    paramArrayList.top_message = ((MessageObject)localObject1).getId();
    paramArrayList.last_message_date = ((MessageObject)localObject1).messageOwner.date;
    if (ChatObject.isChannel((TLRPC.Chat)localObject2))
    {
      i = 1;
      label695:
      paramArrayList.flags = i;
      this.dialogs_dict.put(paramLong, paramArrayList);
      this.dialogs.add(paramArrayList);
      this.dialogMessage.put(paramLong, localObject1);
      if (((MessageObject)localObject1).messageOwner.to_id.channel_id == 0)
      {
        this.dialogMessagesByIds.put(((MessageObject)localObject1).getId(), localObject1);
        if (((MessageObject)localObject1).messageOwner.random_id != 0L) {
          this.dialogMessagesByRandomIds.put(((MessageObject)localObject1).messageOwner.random_id, localObject1);
        }
      }
      this.nextDialogsCacheOffset += 1;
      i = 1;
    }
    for (;;)
    {
      if (i != 0) {
        sortDialogs(null);
      }
      if (k == 0) {
        break;
      }
      DataQuery.getInstance(this.currentAccount).increasePeerRaiting(paramLong);
      break;
      i = 0;
      break label695;
      label835:
      if (((paramArrayList.top_message > 0) && (((MessageObject)localObject1).getId() > 0) && (((MessageObject)localObject1).getId() > paramArrayList.top_message)) || ((paramArrayList.top_message < 0) && (((MessageObject)localObject1).getId() < 0) && (((MessageObject)localObject1).getId() < paramArrayList.top_message)) || (this.dialogMessage.indexOfKey(paramLong) < 0) || (paramArrayList.top_message < 0) || (paramArrayList.last_message_date <= ((MessageObject)localObject1).messageOwner.date))
      {
        localObject2 = (MessageObject)this.dialogMessagesByIds.get(paramArrayList.top_message);
        this.dialogMessagesByIds.remove(paramArrayList.top_message);
        if ((localObject2 != null) && (((MessageObject)localObject2).messageOwner.random_id != 0L)) {
          this.dialogMessagesByRandomIds.remove(((MessageObject)localObject2).messageOwner.random_id);
        }
        paramArrayList.top_message = ((MessageObject)localObject1).getId();
        j = n;
        if (!paramBoolean)
        {
          paramArrayList.last_message_date = ((MessageObject)localObject1).messageOwner.date;
          j = 1;
        }
        this.dialogMessage.put(paramLong, localObject1);
        i = j;
        if (((MessageObject)localObject1).messageOwner.to_id.channel_id == 0)
        {
          this.dialogMessagesByIds.put(((MessageObject)localObject1).getId(), localObject1);
          i = j;
          if (((MessageObject)localObject1).messageOwner.random_id != 0L)
          {
            this.dialogMessagesByRandomIds.put(((MessageObject)localObject1).messageOwner.random_id, localObject1);
            i = j;
          }
        }
      }
    }
  }
  
  public void updateTimerProc()
  {
    long l1 = System.currentTimeMillis();
    checkDeletingTask(false);
    checkReadTasks();
    final Object localObject1;
    label184:
    int i;
    final int j;
    if (UserConfig.getInstance(this.currentAccount).isClientActivated())
    {
      if ((ConnectionsManager.getInstance(this.currentAccount).getPauseTime() == 0L) && (ApplicationLoader.isScreenOn) && (!ApplicationLoader.mainInterfacePausedStageQueue)) {
        if ((ApplicationLoader.mainInterfacePausedStageQueueTime != 0L) && (Math.abs(ApplicationLoader.mainInterfacePausedStageQueueTime - System.currentTimeMillis()) > 1000L) && (this.statusSettingState != 1) && ((this.lastStatusUpdateTime == 0L) || (Math.abs(System.currentTimeMillis() - this.lastStatusUpdateTime) >= 55000L) || (this.offlineSent)))
        {
          this.statusSettingState = 1;
          if (this.statusRequest != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.statusRequest, true);
          }
          localObject1 = new TLRPC.TL_account_updateStatus();
          ((TLRPC.TL_account_updateStatus)localObject1).offline = false;
        }
      }
      for (this.statusRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTL_error == null)
              {
                MessagesController.access$4802(MessagesController.this, System.currentTimeMillis());
                MessagesController.access$4902(MessagesController.this, false);
                MessagesController.access$5002(MessagesController.this, 0);
              }
              for (;;)
              {
                MessagesController.access$5102(MessagesController.this, 0);
                return;
                if (MessagesController.this.lastStatusUpdateTime != 0L) {
                  MessagesController.access$4802(MessagesController.this, MessagesController.this.lastStatusUpdateTime + 5000L);
                }
              }
            }
          }); this.updatesQueueChannels.size() != 0; this.statusRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              if (paramAnonymousTL_error == null) {
                MessagesController.access$4902(MessagesController.this, true);
              }
              for (;;)
              {
                MessagesController.access$5102(MessagesController.this, 0);
                return;
                if (MessagesController.this.lastStatusUpdateTime != 0L) {
                  MessagesController.access$4802(MessagesController.this, MessagesController.this.lastStatusUpdateTime + 5000L);
                }
              }
            }
          }))
      {
        for (i = 0; i < this.updatesQueueChannels.size(); i++)
        {
          j = this.updatesQueueChannels.keyAt(i);
          if (1500L + this.updatesStartWaitTimeChannels.valueAt(i) < l1)
          {
            if (BuildVars.LOGS_ENABLED) {
              FileLog.d("QUEUE CHANNEL " + j + " UPDATES WAIT TIMEOUT - CHECK QUEUE");
            }
            processChannelsUpdatesQueue(j, 0);
          }
        }
        if ((this.statusSettingState == 2) || (this.offlineSent) || (Math.abs(System.currentTimeMillis() - ConnectionsManager.getInstance(this.currentAccount).getPauseTime()) < 2000L)) {
          break label184;
        }
        this.statusSettingState = 2;
        if (this.statusRequest != 0) {
          ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.statusRequest, true);
        }
        localObject1 = new TLRPC.TL_account_updateStatus();
        ((TLRPC.TL_account_updateStatus)localObject1).offline = true;
      }
      for (i = 0; i < 3; i++) {
        if ((getUpdatesStartTime(i) != 0L) && (getUpdatesStartTime(i) + 1500L < l1))
        {
          if (BuildVars.LOGS_ENABLED) {
            FileLog.d(i + " QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
          }
          processUpdatesQueue(i, 0);
        }
      }
    }
    if ((this.channelViewsToSend.size() != 0) && (Math.abs(System.currentTimeMillis() - this.lastViewsCheckTime) >= 5000L))
    {
      this.lastViewsCheckTime = System.currentTimeMillis();
      i = 0;
      if (i < this.channelViewsToSend.size())
      {
        j = this.channelViewsToSend.keyAt(i);
        localObject1 = new TLRPC.TL_messages_getMessagesViews();
        ((TLRPC.TL_messages_getMessagesViews)localObject1).peer = getInputPeer(j);
        ((TLRPC.TL_messages_getMessagesViews)localObject1).id = ((ArrayList)this.channelViewsToSend.valueAt(i));
        if (i == 0) {}
        for (boolean bool = true;; bool = false)
        {
          ((TLRPC.TL_messages_getMessagesViews)localObject1).increment = bool;
          ConnectionsManager.getInstance(this.currentAccount).sendRequest((TLObject)localObject1, new RequestDelegate()
          {
            public void run(TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
            {
              TLRPC.Vector localVector;
              final SparseArray localSparseArray;
              if (paramAnonymousTL_error == null)
              {
                localVector = (TLRPC.Vector)paramAnonymousTLObject;
                localSparseArray = new SparseArray();
                paramAnonymousTL_error = (SparseIntArray)localSparseArray.get(j);
                paramAnonymousTLObject = paramAnonymousTL_error;
                if (paramAnonymousTL_error == null)
                {
                  paramAnonymousTLObject = new SparseIntArray();
                  localSparseArray.put(j, paramAnonymousTLObject);
                }
              }
              for (int i = 0;; i++)
              {
                if ((i >= localObject1.id.size()) || (i >= localVector.objects.size()))
                {
                  MessagesStorage.getInstance(MessagesController.this.currentAccount).putChannelViews(localSparseArray, localObject1.peer instanceof TLRPC.TL_inputPeerChannel);
                  AndroidUtilities.runOnUIThread(new Runnable()
                  {
                    public void run()
                    {
                      NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didUpdatedMessagesViews, new Object[] { localSparseArray });
                    }
                  });
                  return;
                }
                paramAnonymousTLObject.put(((Integer)localObject1.id.get(i)).intValue(), ((Integer)localVector.objects.get(i)).intValue());
              }
            }
          });
          i++;
          break;
        }
      }
      this.channelViewsToSend.clear();
    }
    Object localObject2;
    Object localObject3;
    if (!this.onlinePrivacy.isEmpty())
    {
      localObject1 = null;
      i = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
      localObject2 = this.onlinePrivacy.entrySet().iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Map.Entry localEntry = (Map.Entry)((Iterator)localObject2).next();
        if (((Integer)localEntry.getValue()).intValue() < i - 30)
        {
          localObject3 = localObject1;
          if (localObject1 == null) {
            localObject3 = new ArrayList();
          }
          ((ArrayList)localObject3).add(localEntry.getKey());
          localObject1 = localObject3;
        }
      }
      if (localObject1 != null)
      {
        localObject3 = ((ArrayList)localObject1).iterator();
        while (((Iterator)localObject3).hasNext())
        {
          localObject1 = (Integer)((Iterator)localObject3).next();
          this.onlinePrivacy.remove(localObject1);
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(4) });
          }
        });
      }
    }
    if (this.shortPollChannels.size() != 0) {
      for (i = 0; i < this.shortPollChannels.size(); i++)
      {
        j = this.shortPollChannels.keyAt(i);
        if (this.shortPollChannels.valueAt(i) < System.currentTimeMillis() / 1000L)
        {
          this.shortPollChannels.delete(j);
          if (this.needShortPollChannels.indexOfKey(j) >= 0) {
            getChannelDifference(j);
          }
        }
      }
    }
    if ((!this.printingUsers.isEmpty()) || (this.lastPrintingStringCount != this.printingUsers.size()))
    {
      int k = 0;
      localObject1 = new ArrayList(this.printingUsers.keySet());
      i = 0;
      while (i < ((ArrayList)localObject1).size())
      {
        long l2 = ((Long)((ArrayList)localObject1).get(i)).longValue();
        localObject3 = (ArrayList)this.printingUsers.get(Long.valueOf(l2));
        int m = k;
        if (localObject3 != null)
        {
          j = 0;
          m = k;
          if (j < ((ArrayList)localObject3).size())
          {
            localObject2 = (PrintingUser)((ArrayList)localObject3).get(j);
            if ((((PrintingUser)localObject2).action instanceof TLRPC.TL_sendMessageGamePlayAction)) {}
            for (int n = 30000;; n = 5900)
            {
              m = j;
              if (((PrintingUser)localObject2).lastTime + n < l1)
              {
                k = 1;
                ((ArrayList)localObject3).remove(localObject2);
                m = j - 1;
              }
              j = m + 1;
              break;
            }
          }
        }
        if (localObject3 != null)
        {
          j = i;
          if (!((ArrayList)localObject3).isEmpty()) {}
        }
        else
        {
          this.printingUsers.remove(Long.valueOf(l2));
          ((ArrayList)localObject1).remove(i);
          j = i - 1;
        }
        i = j + 1;
        k = m;
      }
      updatePrintingStrings();
      if (k != 0) {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(64) });
          }
        });
      }
    }
    if ((Theme.selectedAutoNightType == 1) && (Math.abs(l1 - lastThemeCheckTime) >= 60L))
    {
      AndroidUtilities.runOnUIThread(this.themeCheckRunnable);
      lastThemeCheckTime = l1;
    }
    if ((this.lastPushRegisterSendTime != 0L) && (Math.abs(SystemClock.uptimeMillis() - this.lastPushRegisterSendTime) >= 10800000L)) {
      GcmInstanceIDListenerService.sendRegistrationToServer(SharedConfig.pushString);
    }
    LocationController.getInstance(this.currentAccount).update();
  }
  
  public void uploadAndApplyUserAvatar(TLRPC.PhotoSize paramPhotoSize)
  {
    if (paramPhotoSize != null)
    {
      this.uploadingAvatar = (FileLoader.getDirectory(4) + "/" + paramPhotoSize.location.volume_id + "_" + paramPhotoSize.location.local_id + ".jpg");
      FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingAvatar, false, true, 16777216);
    }
  }
  
  public static class PrintingUser
  {
    public TLRPC.SendMessageAction action;
    public long lastTime;
    public int userId;
  }
  
  private class ReadTask
  {
    public long dialogId;
    public int maxDate;
    public int maxId;
    public long sendRequestTime;
    
    private ReadTask() {}
  }
  
  private class UserActionUpdatesPts
    extends TLRPC.Updates
  {
    private UserActionUpdatesPts() {}
  }
  
  private class UserActionUpdatesSeq
    extends TLRPC.Updates
  {
    private UserActionUpdatesSeq() {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/MessagesController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */