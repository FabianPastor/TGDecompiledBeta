package org.telegram.messenger;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.widget.Toast;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.MessagesStorage.IntCallback;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.voip.VoIPService;
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
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.PhoneCall;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.RecentMeUrl;
import org.telegram.tgnet.TLRPC.SendMessageAction;
import org.telegram.tgnet.TLRPC.TL_account_getContactSignUpNotification;
import org.telegram.tgnet.TLRPC.TL_account_getNotifySettings;
import org.telegram.tgnet.TLRPC.TL_account_installWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_registerDevice;
import org.telegram.tgnet.TLRPC.TL_account_saveWallPaper;
import org.telegram.tgnet.TLRPC.TL_account_unregisterDevice;
import org.telegram.tgnet.TLRPC.TL_account_updateStatus;
import org.telegram.tgnet.TLRPC.TL_account_uploadWallPaper;
import org.telegram.tgnet.TLRPC.TL_auth_logOut;
import org.telegram.tgnet.TLRPC.TL_boolFalse;
import org.telegram.tgnet.TLRPC.TL_boolTrue;
import org.telegram.tgnet.TLRPC.TL_botInfo;
import org.telegram.tgnet.TLRPC.TL_channel;
import org.telegram.tgnet.TLRPC.TL_channelForbidden;
import org.telegram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_createChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteChannel;
import org.telegram.tgnet.TLRPC.TL_channels_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_channels_deleteUserHistory;
import org.telegram.tgnet.TLRPC.TL_channels_editAdmin;
import org.telegram.tgnet.TLRPC.TL_channels_editBanned;
import org.telegram.tgnet.TLRPC.TL_channels_editPhoto;
import org.telegram.tgnet.TLRPC.TL_channels_editTitle;
import org.telegram.tgnet.TLRPC.TL_channels_getFullChannel;
import org.telegram.tgnet.TLRPC.TL_channels_getMessages;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipant;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.telegram.tgnet.TLRPC.TL_channels_leaveChannel;
import org.telegram.tgnet.TLRPC.TL_channels_readHistory;
import org.telegram.tgnet.TLRPC.TL_channels_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_channels_togglePreHistoryHidden;
import org.telegram.tgnet.TLRPC.TL_channels_toggleSignatures;
import org.telegram.tgnet.TLRPC.TL_channels_updateUsername;
import org.telegram.tgnet.TLRPC.TL_chat;
import org.telegram.tgnet.TLRPC.TL_chatAdminRights;
import org.telegram.tgnet.TLRPC.TL_chatBannedRights;
import org.telegram.tgnet.TLRPC.TL_chatFull;
import org.telegram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.telegram.tgnet.TLRPC.TL_chatOnlines;
import org.telegram.tgnet.TLRPC.TL_chatParticipant;
import org.telegram.tgnet.TLRPC.TL_chatParticipants;
import org.telegram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_config;
import org.telegram.tgnet.TLRPC.TL_contactBlocked;
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
import org.telegram.tgnet.TLRPC.TL_help_getProxyData;
import org.telegram.tgnet.TLRPC.TL_help_getRecentMeUrls;
import org.telegram.tgnet.TLRPC.TL_help_getTermsOfServiceUpdate;
import org.telegram.tgnet.TLRPC.TL_help_proxyDataEmpty;
import org.telegram.tgnet.TLRPC.TL_help_proxyDataPromo;
import org.telegram.tgnet.TLRPC.TL_help_recentMeUrls;
import org.telegram.tgnet.TLRPC.TL_help_termsOfServiceUpdate;
import org.telegram.tgnet.TLRPC.TL_help_termsOfServiceUpdateEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChannel;
import org.telegram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputChatUploadedPhoto;
import org.telegram.tgnet.TLRPC.TL_inputDialogPeer;
import org.telegram.tgnet.TLRPC.TL_inputDocument;
import org.telegram.tgnet.TLRPC.TL_inputEncryptedChat;
import org.telegram.tgnet.TLRPC.TL_inputMessagesFilterChatPhotos;
import org.telegram.tgnet.TLRPC.TL_inputNotifyBroadcasts;
import org.telegram.tgnet.TLRPC.TL_inputNotifyChats;
import org.telegram.tgnet.TLRPC.TL_inputNotifyUsers;
import org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC.TL_inputPeerChat;
import org.telegram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.telegram.tgnet.TLRPC.TL_inputPeerUser;
import org.telegram.tgnet.TLRPC.TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC.TL_inputPhotoEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUser;
import org.telegram.tgnet.TLRPC.TL_inputUserEmpty;
import org.telegram.tgnet.TLRPC.TL_inputUserSelf;
import org.telegram.tgnet.TLRPC.TL_inputWallPaper;
import org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.telegram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.telegram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.telegram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.telegram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.telegram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_messageService;
import org.telegram.tgnet.TLRPC.TL_messages_affectedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_affectedMessages;
import org.telegram.tgnet.TLRPC.TL_messages_channelMessages;
import org.telegram.tgnet.TLRPC.TL_messages_chatFull;
import org.telegram.tgnet.TLRPC.TL_messages_createChat;
import org.telegram.tgnet.TLRPC.TL_messages_deleteChatUser;
import org.telegram.tgnet.TLRPC.TL_messages_deleteMessages;
import org.telegram.tgnet.TLRPC.TL_messages_dialogs;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAbout;
import org.telegram.tgnet.TLRPC.TL_messages_editChatAdmin;
import org.telegram.tgnet.TLRPC.TL_messages_editChatDefaultBannedRights;
import org.telegram.tgnet.TLRPC.TL_messages_editChatPhoto;
import org.telegram.tgnet.TLRPC.TL_messages_editChatTitle;
import org.telegram.tgnet.TLRPC.TL_messages_getDialogUnreadMarks;
import org.telegram.tgnet.TLRPC.TL_messages_getDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getFullChat;
import org.telegram.tgnet.TLRPC.TL_messages_getHistory;
import org.telegram.tgnet.TLRPC.TL_messages_getMessages;
import org.telegram.tgnet.TLRPC.TL_messages_getMessagesViews;
import org.telegram.tgnet.TLRPC.TL_messages_getOnlines;
import org.telegram.tgnet.TLRPC.TL_messages_getPeerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getPeerSettings;
import org.telegram.tgnet.TLRPC.TL_messages_getPinnedDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_getPollResults;
import org.telegram.tgnet.TLRPC.TL_messages_getUnreadMentions;
import org.telegram.tgnet.TLRPC.TL_messages_getWebPagePreview;
import org.telegram.tgnet.TLRPC.TL_messages_hideReportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_markDialogUnread;
import org.telegram.tgnet.TLRPC.TL_messages_messages;
import org.telegram.tgnet.TLRPC.TL_messages_migrateChat;
import org.telegram.tgnet.TLRPC.TL_messages_peerDialogs;
import org.telegram.tgnet.TLRPC.TL_messages_readEncryptedHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readHistory;
import org.telegram.tgnet.TLRPC.TL_messages_readMentions;
import org.telegram.tgnet.TLRPC.TL_messages_readMessageContents;
import org.telegram.tgnet.TLRPC.TL_messages_reportEncryptedSpam;
import org.telegram.tgnet.TLRPC.TL_messages_reportSpam;
import org.telegram.tgnet.TLRPC.TL_messages_saveGif;
import org.telegram.tgnet.TLRPC.TL_messages_saveRecentSticker;
import org.telegram.tgnet.TLRPC.TL_messages_search;
import org.telegram.tgnet.TLRPC.TL_messages_setEncryptedTyping;
import org.telegram.tgnet.TLRPC.TL_messages_setTyping;
import org.telegram.tgnet.TLRPC.TL_messages_startBot;
import org.telegram.tgnet.TLRPC.TL_messages_toggleDialogPin;
import org.telegram.tgnet.TLRPC.TL_messages_updatePinnedMessage;
import org.telegram.tgnet.TLRPC.TL_notifyBroadcasts;
import org.telegram.tgnet.TLRPC.TL_notifyChats;
import org.telegram.tgnet.TLRPC.TL_notifyPeer;
import org.telegram.tgnet.TLRPC.TL_notifyUsers;
import org.telegram.tgnet.TLRPC.TL_peerChannel;
import org.telegram.tgnet.TLRPC.TL_peerChat;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettings;
import org.telegram.tgnet.TLRPC.TL_peerNotifySettingsEmpty_layer77;
import org.telegram.tgnet.TLRPC.TL_peerSettings;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC.TL_phoneCallRequested;
import org.telegram.tgnet.TLRPC.TL_phone_discardCall;
import org.telegram.tgnet.TLRPC.TL_photo;
import org.telegram.tgnet.TLRPC.TL_photoEmpty;
import org.telegram.tgnet.TLRPC.TL_photos_deletePhotos;
import org.telegram.tgnet.TLRPC.TL_photos_getUserPhotos;
import org.telegram.tgnet.TLRPC.TL_photos_photo;
import org.telegram.tgnet.TLRPC.TL_photos_photos;
import org.telegram.tgnet.TLRPC.TL_photos_updateProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.telegram.tgnet.TLRPC.TL_privacyKeyChatInvite;
import org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneCall;
import org.telegram.tgnet.TLRPC.TL_privacyKeyPhoneP2P;
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
import org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights;
import org.telegram.tgnet.TLRPC.TL_updateContactsReset;
import org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
import org.telegram.tgnet.TLRPC.TL_updateDeleteMessages;
import org.telegram.tgnet.TLRPC.TL_updateDialogPinned;
import org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark;
import org.telegram.tgnet.TLRPC.TL_updateDraftMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
import org.telegram.tgnet.TLRPC.TL_updateEditMessage;
import org.telegram.tgnet.TLRPC.TL_updateFavedStickers;
import org.telegram.tgnet.TLRPC.TL_updateGroupCall;
import org.telegram.tgnet.TLRPC.TL_updateGroupCallParticipant;
import org.telegram.tgnet.TLRPC.TL_updateLangPack;
import org.telegram.tgnet.TLRPC.TL_updateMessageID;
import org.telegram.tgnet.TLRPC.TL_updateMessagePoll;
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
import org.telegram.tgnet.TLRPC.TL_updateStickerSets;
import org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder;
import org.telegram.tgnet.TLRPC.TL_updateUserBlocked;
import org.telegram.tgnet.TLRPC.TL_updateUserName;
import org.telegram.tgnet.TLRPC.TL_updateUserPhone;
import org.telegram.tgnet.TLRPC.TL_updateUserPhoto;
import org.telegram.tgnet.TLRPC.TL_updateUserStatus;
import org.telegram.tgnet.TLRPC.TL_updateWebPage;
import org.telegram.tgnet.TLRPC.TL_updatesCombined;
import org.telegram.tgnet.TLRPC.TL_updates_difference;
import org.telegram.tgnet.TLRPC.TL_updates_differenceEmpty;
import org.telegram.tgnet.TLRPC.TL_updates_differenceSlice;
import org.telegram.tgnet.TLRPC.TL_updates_differenceTooLong;
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
import org.telegram.tgnet.TLRPC.TL_wallPaper;
import org.telegram.tgnet.TLRPC.TL_wallPaperSettings;
import org.telegram.tgnet.TLRPC.TL_webPage;
import org.telegram.tgnet.TLRPC.TL_webPageEmpty;
import org.telegram.tgnet.TLRPC.TL_webPagePending;
import org.telegram.tgnet.TLRPC.TL_webPageUrlPending;
import org.telegram.tgnet.TLRPC.Update;
import org.telegram.tgnet.TLRPC.Updates;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.UserProfilePhoto;
import org.telegram.tgnet.TLRPC.Vector;
import org.telegram.tgnet.TLRPC.WallPaper;
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
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ProfileActivity;

public class MessagesController implements NotificationCenterDelegate {
    private static volatile MessagesController[] Instance = new MessagesController[3];
    public static final int UPDATE_MASK_ALL = 1535;
    public static final int UPDATE_MASK_AVATAR = 2;
    public static final int UPDATE_MASK_CHAT = 8192;
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
    private static volatile long lastPasswordCheckTime;
    private static volatile long lastThemeCheckTime;
    public int availableMapProviders;
    public boolean blockedCountry;
    public SparseIntArray blockedUsers = new SparseIntArray();
    public int callConnectTimeout;
    public int callPacketTimeout;
    public int callReceiveTimeout;
    public int callRingTimeout;
    public boolean canRevokePmInbox;
    private SparseArray<ArrayList<Integer>> channelAdmins = new SparseArray();
    private SparseArray<ArrayList<Integer>> channelViewsToSend = new SparseArray();
    private SparseIntArray channelsPts = new SparseIntArray();
    private ConcurrentHashMap<Integer, Chat> chats = new ConcurrentHashMap(100, 1.0f, 2);
    private SparseBooleanArray checkingLastMessagesDialogs = new SparseBooleanArray();
    private boolean checkingProxyInfo;
    private int checkingProxyInfoRequestId;
    private boolean checkingTosUpdate;
    private LongSparseArray<TL_dialog> clearingHistoryDialogs = new LongSparseArray();
    private ArrayList<Long> createdDialogIds = new ArrayList();
    private ArrayList<Long> createdDialogMainThreadIds = new ArrayList();
    private int currentAccount;
    private Runnable currentDeleteTaskRunnable;
    private int currentDeletingTaskChannelId;
    private ArrayList<Integer> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    public String dcDomainName;
    public boolean defaultP2pContacts;
    public LongSparseArray<Integer> deletedHistory = new LongSparseArray();
    private LongSparseArray<TL_dialog> deletingDialogs = new LongSparseArray();
    private final Comparator<TL_dialog> dialogComparator = new Comparator<TL_dialog>() {
        public int compare(TL_dialog dialog1, TL_dialog dialog2) {
            if (!dialog1.pinned && dialog2.pinned) {
                return 1;
            }
            if (dialog1.pinned && !dialog2.pinned) {
                return -1;
            }
            if (!dialog1.pinned || !dialog2.pinned) {
                DraftMessage draftMessage = DataQuery.getInstance(MessagesController.this.currentAccount).getDraft(dialog1.id);
                int date1 = (draftMessage == null || draftMessage.date < dialog1.last_message_date) ? dialog1.last_message_date : draftMessage.date;
                draftMessage = DataQuery.getInstance(MessagesController.this.currentAccount).getDraft(dialog2.id);
                int date2 = (draftMessage == null || draftMessage.date < dialog2.last_message_date) ? dialog2.last_message_date : draftMessage.date;
                if (date1 < date2) {
                    return 1;
                }
                if (date1 > date2) {
                    return -1;
                }
                return 0;
            } else if (dialog1.pinnedNum < dialog2.pinnedNum) {
                return 1;
            } else {
                if (dialog1.pinnedNum > dialog2.pinnedNum) {
                    return -1;
                }
                return 0;
            }
        }
    };
    public LongSparseArray<MessageObject> dialogMessage = new LongSparseArray();
    public SparseArray<MessageObject> dialogMessagesByIds = new SparseArray();
    public LongSparseArray<MessageObject> dialogMessagesByRandomIds = new LongSparseArray();
    public ArrayList<TL_dialog> dialogs = new ArrayList();
    public ArrayList<TL_dialog> dialogsCanAddUsers = new ArrayList();
    public ArrayList<TL_dialog> dialogsChannelsOnly = new ArrayList();
    public boolean dialogsEndReached;
    public ArrayList<TL_dialog> dialogsForward = new ArrayList();
    public ArrayList<TL_dialog> dialogsGroupsOnly = new ArrayList();
    public ArrayList<TL_dialog> dialogsServerOnly = new ArrayList();
    public ArrayList<TL_dialog> dialogsUsersOnly = new ArrayList();
    public LongSparseArray<TL_dialog> dialogs_dict = new LongSparseArray();
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max = new ConcurrentHashMap(100, 1.0f, 2);
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max = new ConcurrentHashMap(100, 1.0f, 2);
    private SharedPreferences emojiPreferences;
    public boolean enableJoined;
    private ConcurrentHashMap<Integer, EncryptedChat> encryptedChats = new ConcurrentHashMap(10, 1.0f, 2);
    private SparseArray<ExportedChatInvite> exportedChats = new SparseArray();
    public boolean firstGettingTask;
    private SparseArray<TL_userFull> fullUsers = new SparseArray();
    private boolean getDifferenceFirstSync = true;
    public boolean gettingDifference;
    private SparseBooleanArray gettingDifferenceChannels = new SparseBooleanArray();
    private boolean gettingNewDeleteTask;
    private SparseBooleanArray gettingUnknownChannels = new SparseBooleanArray();
    public String gifSearchBot;
    public ArrayList<RecentMeUrl> hintDialogs = new ArrayList();
    public String imageSearchBot;
    private String installReferer;
    private boolean isLeftProxyChannel;
    private ArrayList<Integer> joiningToChannels = new ArrayList();
    private int lastPrintingStringCount;
    private long lastPushRegisterSendTime;
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    public String linkPrefix;
    private ArrayList<Integer> loadedFullChats = new ArrayList();
    private ArrayList<Integer> loadedFullParticipants = new ArrayList();
    private ArrayList<Integer> loadedFullUsers = new ArrayList();
    public boolean loadingBlockedUsers = false;
    private SparseIntArray loadingChannelAdmins = new SparseIntArray();
    public boolean loadingDialogs;
    private ArrayList<Integer> loadingFullChats = new ArrayList();
    private ArrayList<Integer> loadingFullParticipants = new ArrayList();
    private ArrayList<Integer> loadingFullUsers = new ArrayList();
    private int loadingNotificationSettings;
    private boolean loadingNotificationSignUpSettings;
    private LongSparseArray<Boolean> loadingPeerSettings = new LongSparseArray();
    private boolean loadingUnreadDialogs;
    private SharedPreferences mainPreferences;
    public String mapKey;
    public int mapProvider;
    public int maxBroadcastCount = 100;
    public int maxCaptionLength;
    public int maxEditTime;
    public int maxFaveStickersCount;
    public int maxGroupCount;
    public int maxMegagroupCount;
    public int maxMessageLength;
    public int maxPinnedDialogsCount;
    public int maxRecentGifsCount;
    public int maxRecentStickersCount;
    private SparseIntArray migratedChats = new SparseIntArray();
    private boolean migratingDialogs;
    public int minGroupConvertSize = 200;
    private SparseIntArray needShortPollChannels = new SparseIntArray();
    private SparseIntArray needShortPollOnlines = new SparseIntArray();
    public int nextDialogsCacheOffset;
    private int nextProxyInfoCheckTime;
    private int nextTosCheckTime;
    private SharedPreferences notificationsPreferences;
    private ConcurrentHashMap<String, TLObject> objectsByUsernames = new ConcurrentHashMap(100, 1.0f, 2);
    private boolean offlineSent;
    public ConcurrentHashMap<Integer, Integer> onlinePrivacy = new ConcurrentHashMap(20, 1.0f, 2);
    private Runnable passwordCheckRunnable = new Runnable() {
        public void run() {
            UserConfig.getInstance(MessagesController.this.currentAccount).checkSavedPassword();
        }
    };
    private LongSparseArray<SparseArray<MessageObject>> pollsToCheck = new LongSparseArray();
    private int pollsToCheckSize;
    public boolean preloadFeaturedStickers;
    public LongSparseArray<CharSequence> printingStrings = new LongSparseArray();
    public LongSparseArray<Integer> printingStringsTypes = new LongSparseArray();
    public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers = new ConcurrentHashMap(20, 1.0f, 2);
    private TL_dialog proxyDialog;
    private String proxyDialogAddress;
    private long proxyDialogId;
    public int ratingDecay;
    private ArrayList<ReadTask> readTasks = new ArrayList();
    private LongSparseArray<ReadTask> readTasksMap = new LongSparseArray();
    public boolean registeringForPush;
    private LongSparseArray<ArrayList<Integer>> reloadingMessages = new LongSparseArray();
    private HashMap<String, ArrayList<MessageObject>> reloadingWebpages = new HashMap();
    private LongSparseArray<ArrayList<MessageObject>> reloadingWebpagesPending = new LongSparseArray();
    private messages_Dialogs resetDialogsAll;
    private TL_messages_peerDialogs resetDialogsPinned;
    private boolean resetingDialogs;
    public int revokeTimeLimit;
    public int revokeTimePmLimit;
    public int secretWebpagePreview;
    public SparseArray<LongSparseArray<Boolean>> sendingTypings = new SparseArray();
    public boolean serverDialogsEndReached;
    private SparseIntArray shortPollChannels = new SparseIntArray();
    private SparseIntArray shortPollOnlines = new SparseIntArray();
    private int statusRequest;
    private int statusSettingState;
    public boolean suggestContacts = true;
    public String suggestedLangCode;
    private Runnable themeCheckRunnable = MessagesController$$Lambda$0.$instance;
    public int unreadUnmutedDialogs;
    private final Comparator<Update> updatesComparator = new MessagesController$$Lambda$1(this);
    private SparseArray<ArrayList<Updates>> updatesQueueChannels = new SparseArray();
    private ArrayList<Updates> updatesQueuePts = new ArrayList();
    private ArrayList<Updates> updatesQueueQts = new ArrayList();
    private ArrayList<Updates> updatesQueueSeq = new ArrayList();
    private SparseLongArray updatesStartWaitTimeChannels = new SparseLongArray();
    private long updatesStartWaitTimePts;
    private long updatesStartWaitTimeQts;
    private long updatesStartWaitTimeSeq;
    public boolean updatingState;
    private String uploadingAvatar;
    private String uploadingWallpaper;
    private boolean uploadingWallpaperBlurred;
    private boolean uploadingWallpaperMotion;
    private ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap(100, 1.0f, 2);
    public String venueSearchBot;
    private ArrayList<Long> visibleDialogMainThreadIds = new ArrayList();
    public int webFileDatacenterId;

    public static class PrintingUser {
        public SendMessageAction action;
        public long lastTime;
        public int userId;
    }

    private class ReadTask {
        public long dialogId;
        public int maxDate;
        public int maxId;
        public long sendRequestTime;

        private ReadTask() {
        }

        /* synthetic */ ReadTask(MessagesController x0, AnonymousClass1 x1) {
            this();
        }
    }

    private class UserActionUpdatesPts extends Updates {
        private UserActionUpdatesPts() {
        }

        /* synthetic */ UserActionUpdatesPts(MessagesController x0, AnonymousClass1 x1) {
            this();
        }
    }

    private class UserActionUpdatesSeq extends Updates {
        private UserActionUpdatesSeq() {
        }

        /* synthetic */ UserActionUpdatesSeq(MessagesController x0, AnonymousClass1 x1) {
            this();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ int lambda$new$0$MessagesController(Update lhs, Update rhs) {
        int ltype = getUpdateType(lhs);
        int rtype = getUpdateType(rhs);
        if (ltype != rtype) {
            return AndroidUtilities.compare(ltype, rtype);
        }
        if (ltype == 0) {
            return AndroidUtilities.compare(getUpdatePts(lhs), getUpdatePts(rhs));
        }
        if (ltype == 1) {
            return AndroidUtilities.compare(getUpdateQts(lhs), getUpdateQts(rhs));
        }
        if (ltype != 2) {
            return 0;
        }
        int lChannel = getUpdateChannelId(lhs);
        int rChannel = getUpdateChannelId(rhs);
        if (lChannel == rChannel) {
            return AndroidUtilities.compare(getUpdatePts(lhs), getUpdatePts(rhs));
        }
        return AndroidUtilities.compare(lChannel, rChannel);
    }

    public static MessagesController getInstance(int num) {
        Throwable th;
        MessagesController localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (MessagesController.class) {
                try {
                    localInstance = Instance[num];
                    if (localInstance == null) {
                        MessagesController[] messagesControllerArr = Instance;
                        MessagesController localInstance2 = new MessagesController(num);
                        try {
                            messagesControllerArr[num] = localInstance2;
                            localInstance = localInstance2;
                        } catch (Throwable th2) {
                            th = th2;
                            localInstance = localInstance2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th;
                }
            }
        }
        return localInstance;
    }

    public static SharedPreferences getNotificationsSettings(int account) {
        return getInstance(account).notificationsPreferences;
    }

    public static SharedPreferences getGlobalNotificationsSettings() {
        return getInstance(0).notificationsPreferences;
    }

    public static SharedPreferences getMainSettings(int account) {
        return getInstance(account).mainPreferences;
    }

    public static SharedPreferences getGlobalMainSettings() {
        return getInstance(0).mainPreferences;
    }

    public static SharedPreferences getEmojiSettings(int account) {
        return getInstance(account).emojiPreferences;
    }

    public static SharedPreferences getGlobalEmojiSettings() {
        return getInstance(0).emojiPreferences;
    }

    public MessagesController(int num) {
        this.currentAccount = num;
        ImageLoader.getInstance();
        MessagesStorage.getInstance(this.currentAccount);
        LocationController.getInstance(this.currentAccount);
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$2(this));
        addSupportUser();
        if (this.currentAccount == 0) {
            this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            this.emojiPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
        } else {
            this.notificationsPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications" + this.currentAccount, 0);
            this.mainPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig" + this.currentAccount, 0);
            this.emojiPreferences = ApplicationLoader.applicationContext.getSharedPreferences("emoji" + this.currentAccount, 0);
        }
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
        this.maxMessageLength = this.mainPreferences.getInt("maxMessageLength", 4096);
        this.maxCaptionLength = this.mainPreferences.getInt("maxCaptionLength", 1024);
        this.mapProvider = this.mainPreferences.getInt("mapProvider", 0);
        this.availableMapProviders = this.mainPreferences.getInt("availableMapProviders", 3);
        this.mapKey = this.mainPreferences.getString("pk", null);
        this.installReferer = this.mainPreferences.getString("installReferer", null);
        this.defaultP2pContacts = this.mainPreferences.getBoolean("defaultP2pContacts", false);
        this.revokeTimeLimit = this.mainPreferences.getInt("revokeTimeLimit", this.revokeTimeLimit);
        this.revokeTimePmLimit = this.mainPreferences.getInt("revokeTimePmLimit", this.revokeTimePmLimit);
        this.canRevokePmInbox = this.mainPreferences.getBoolean("canRevokePmInbox", this.canRevokePmInbox);
        this.preloadFeaturedStickers = this.mainPreferences.getBoolean("preloadFeaturedStickers", false);
        this.proxyDialogId = this.mainPreferences.getLong("proxy_dialog", 0);
        this.proxyDialogAddress = this.mainPreferences.getString("proxyDialogAddress", null);
        this.nextTosCheckTime = this.notificationsPreferences.getInt("nextTosCheckTime", 0);
        this.venueSearchBot = this.mainPreferences.getString("venueSearchBot", "foursquare");
        this.gifSearchBot = this.mainPreferences.getString("gifSearchBot", "gif");
        this.imageSearchBot = this.mainPreferences.getString("imageSearchBot", "pic");
        this.blockedCountry = this.mainPreferences.getBoolean("blockedCountry", false);
        this.dcDomainName = this.mainPreferences.getString("dcDomainName", ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? "tapv2.stel.com" : "apv2.stel.com");
        this.webFileDatacenterId = this.mainPreferences.getInt("webFileDatacenterId", ConnectionsManager.native_isTestBackend(this.currentAccount) != 0 ? 2 : 4);
        this.suggestedLangCode = this.mainPreferences.getString("suggestedLangCode", "en");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$new$1$MessagesController() {
        MessagesController messagesController = getInstance(this.currentAccount);
        NotificationCenter.getInstance(this.currentAccount).addObserver(messagesController, NotificationCenter.FileDidUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(messagesController, NotificationCenter.FileDidFailUpload);
        NotificationCenter.getInstance(this.currentAccount).addObserver(messagesController, NotificationCenter.fileDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(messagesController, NotificationCenter.fileDidFailedLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(messagesController, NotificationCenter.messageReceivedByServer);
        NotificationCenter.getInstance(this.currentAccount).addObserver(messagesController, NotificationCenter.updateMessageMedia);
    }

    public void updateConfig(TL_config config) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$3(this, config));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateConfig$2$MessagesController(TL_config config) {
        DownloadController.getInstance(this.currentAccount).loadAutoDownloadConfig(false);
        LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
        this.maxMegagroupCount = config.megagroup_size_max;
        this.maxGroupCount = config.chat_size_max;
        this.maxEditTime = config.edit_time_limit;
        this.ratingDecay = config.rating_e_decay;
        this.maxRecentGifsCount = config.saved_gifs_limit;
        this.maxRecentStickersCount = config.stickers_recent_limit;
        this.maxFaveStickersCount = config.stickers_faved_limit;
        this.revokeTimeLimit = config.revoke_time_limit;
        this.revokeTimePmLimit = config.revoke_pm_time_limit;
        this.canRevokePmInbox = config.revoke_pm_inbox;
        this.linkPrefix = config.me_url_prefix;
        if (this.linkPrefix.endsWith("/")) {
            this.linkPrefix = this.linkPrefix.substring(0, this.linkPrefix.length() - 1);
        }
        if (this.linkPrefix.startsWith("https://")) {
            this.linkPrefix = this.linkPrefix.substring(8);
        } else if (this.linkPrefix.startsWith("http://")) {
            this.linkPrefix = this.linkPrefix.substring(7);
        }
        this.callReceiveTimeout = config.call_receive_timeout_ms;
        this.callRingTimeout = config.call_ring_timeout_ms;
        this.callConnectTimeout = config.call_connect_timeout_ms;
        this.callPacketTimeout = config.call_packet_timeout_ms;
        this.maxPinnedDialogsCount = config.pinned_dialogs_count_max;
        this.maxMessageLength = config.message_length_max;
        this.maxCaptionLength = config.caption_length_max;
        this.defaultP2pContacts = config.default_p2p_contacts;
        this.preloadFeaturedStickers = config.preload_featured_stickers;
        if (config.venue_search_username != null) {
            this.venueSearchBot = config.venue_search_username;
        }
        if (config.gif_search_username != null) {
            this.gifSearchBot = config.gif_search_username;
        }
        if (this.imageSearchBot != null) {
            this.imageSearchBot = config.img_search_username;
        }
        this.blockedCountry = config.blocked_mode;
        this.dcDomainName = config.dc_txt_domain_name;
        this.webFileDatacenterId = config.webfile_dc_id;
        this.suggestedLangCode = config.suggested_lang_code;
        if (config.static_maps_provider == null) {
            config.static_maps_provider = "google";
        }
        this.mapKey = null;
        this.mapProvider = 0;
        this.availableMapProviders = 0;
        String[] providers = config.static_maps_provider.split(",");
        for (int a = 0; a < providers.length; a++) {
            String[] mapArgs = providers[a].split("\\+");
            if (mapArgs.length > 0) {
                String[] typeAndKey = mapArgs[0].split(":");
                if (typeAndKey.length > 0) {
                    if ("yandex".equals(typeAndKey[0])) {
                        if (a == 0) {
                            if (mapArgs.length > 1) {
                                this.mapProvider = 3;
                            } else {
                                this.mapProvider = 1;
                            }
                        }
                        this.availableMapProviders |= 4;
                    } else if ("google".equals(typeAndKey[0])) {
                        if (a == 0 && mapArgs.length > 1) {
                            this.mapProvider = 4;
                        }
                        this.availableMapProviders |= 1;
                    } else if ("telegram".equals(typeAndKey[0])) {
                        if (a == 0) {
                            this.mapProvider = 2;
                        }
                        this.availableMapProviders |= 2;
                    }
                    if (typeAndKey.length > 1) {
                        this.mapKey = typeAndKey[1];
                    }
                }
            }
        }
        Editor editor = this.mainPreferences.edit();
        editor.putInt("maxGroupCount", this.maxGroupCount);
        editor.putInt("maxMegagroupCount", this.maxMegagroupCount);
        editor.putInt("maxEditTime", this.maxEditTime);
        editor.putInt("ratingDecay", this.ratingDecay);
        editor.putInt("maxRecentGifsCount", this.maxRecentGifsCount);
        editor.putInt("maxRecentStickersCount", this.maxRecentStickersCount);
        editor.putInt("maxFaveStickersCount", this.maxFaveStickersCount);
        editor.putInt("callReceiveTimeout", this.callReceiveTimeout);
        editor.putInt("callRingTimeout", this.callRingTimeout);
        editor.putInt("callConnectTimeout", this.callConnectTimeout);
        editor.putInt("callPacketTimeout", this.callPacketTimeout);
        editor.putString("linkPrefix", this.linkPrefix);
        editor.putInt("maxPinnedDialogsCount", this.maxPinnedDialogsCount);
        editor.putInt("maxMessageLength", this.maxMessageLength);
        editor.putInt("maxCaptionLength", this.maxCaptionLength);
        editor.putBoolean("defaultP2pContacts", this.defaultP2pContacts);
        editor.putBoolean("preloadFeaturedStickers", this.preloadFeaturedStickers);
        editor.putInt("revokeTimeLimit", this.revokeTimeLimit);
        editor.putInt("revokeTimePmLimit", this.revokeTimePmLimit);
        editor.putInt("mapProvider", this.mapProvider);
        if (this.mapKey != null) {
            editor.putString("pk", this.mapKey);
        } else {
            editor.remove("pk");
        }
        editor.putBoolean("canRevokePmInbox", this.canRevokePmInbox);
        editor.putBoolean("blockedCountry", this.blockedCountry);
        editor.putString("venueSearchBot", this.venueSearchBot);
        editor.putString("gifSearchBot", this.gifSearchBot);
        editor.putString("imageSearchBot", this.imageSearchBot);
        editor.putString("dcDomainName", this.dcDomainName);
        editor.putInt("webFileDatacenterId", this.webFileDatacenterId);
        editor.putString("suggestedLangCode", this.suggestedLangCode);
        editor.commit();
        LocaleController.getInstance().checkUpdateForCurrentRemoteLocale(this.currentAccount, config.lang_pack_version, config.base_lang_pack_version);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.configLoaded, new Object[0]);
    }

    public void addSupportUser() {
        TL_userForeign_old2 user = new TL_userForeign_old2();
        user.phone = "333";
        user.id = 333000;
        user.first_name = "Telegram";
        user.last_name = "";
        user.status = null;
        user.photo = new TL_userProfilePhotoEmpty();
        putUser(user, true);
        user = new TL_userForeign_old2();
        user.phone = "42777";
        user.id = 777000;
        user.first_name = "Telegram";
        user.last_name = "Notifications";
        user.status = null;
        user.photo = new TL_userProfilePhotoEmpty();
        putUser(user, true);
    }

    public InputUser getInputUser(User user) {
        if (user == null) {
            return new TL_inputUserEmpty();
        }
        if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return new TL_inputUserSelf();
        }
        InputUser inputUser = new TL_inputUser();
        inputUser.user_id = user.id;
        inputUser.access_hash = user.access_hash;
        return inputUser;
    }

    public InputUser getInputUser(int user_id) {
        return getInputUser(getInstance(UserConfig.selectedAccount).getUser(Integer.valueOf(user_id)));
    }

    public static InputChannel getInputChannel(Chat chat) {
        if (!(chat instanceof TL_channel) && !(chat instanceof TL_channelForbidden)) {
            return new TL_inputChannelEmpty();
        }
        InputChannel inputChat = new TL_inputChannel();
        inputChat.channel_id = chat.id;
        inputChat.access_hash = chat.access_hash;
        return inputChat;
    }

    public InputChannel getInputChannel(int chatId) {
        return getInputChannel(getChat(Integer.valueOf(chatId)));
    }

    public InputPeer getInputPeer(int id) {
        InputPeer inputPeer;
        if (id < 0) {
            Chat chat = getChat(Integer.valueOf(-id));
            if (ChatObject.isChannel(chat)) {
                inputPeer = new TL_inputPeerChannel();
                inputPeer.channel_id = -id;
                inputPeer.access_hash = chat.access_hash;
                return inputPeer;
            }
            inputPeer = new TL_inputPeerChat();
            inputPeer.chat_id = -id;
            return inputPeer;
        }
        User user = getUser(Integer.valueOf(id));
        inputPeer = new TL_inputPeerUser();
        inputPeer.user_id = id;
        if (user == null) {
            return inputPeer;
        }
        inputPeer.access_hash = user.access_hash;
        return inputPeer;
    }

    public Peer getPeer(int id) {
        Peer inputPeer;
        if (id < 0) {
            Chat chat = getChat(Integer.valueOf(-id));
            if ((chat instanceof TL_channel) || (chat instanceof TL_channelForbidden)) {
                inputPeer = new TL_peerChannel();
                inputPeer.channel_id = -id;
                return inputPeer;
            }
            inputPeer = new TL_peerChat();
            inputPeer.chat_id = -id;
            return inputPeer;
        }
        User user = getUser(Integer.valueOf(id));
        inputPeer = new TL_peerUser();
        inputPeer.user_id = id;
        return inputPeer;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        String location;
        if (id == NotificationCenter.FileDidUpload) {
            location = args[0];
            InputFile file = args[1];
            if (this.uploadingAvatar != null && this.uploadingAvatar.equals(location)) {
                TL_photos_uploadProfilePhoto req = new TL_photos_uploadProfilePhoto();
                req.file = file;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$4(this));
            } else if (this.uploadingWallpaper != null && this.uploadingWallpaper.equals(location)) {
                TL_account_uploadWallPaper req2 = new TL_account_uploadWallPaper();
                req2.file = file;
                req2.mime_type = "image/jpeg";
                TL_wallPaperSettings settings = new TL_wallPaperSettings();
                settings.blur = this.uploadingWallpaperBlurred;
                settings.motion = this.uploadingWallpaperMotion;
                req2.settings = settings;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$5(this, settings));
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            location = (String) args[0];
            if (this.uploadingAvatar != null && this.uploadingAvatar.equals(location)) {
                this.uploadingAvatar = null;
            } else if (this.uploadingWallpaper != null && this.uploadingWallpaper.equals(location)) {
                this.uploadingWallpaper = null;
            }
        } else if (id == NotificationCenter.messageReceivedByServer) {
            Integer msgId = args[0];
            Integer newMsgId = args[1];
            Long did = args[3];
            MessageObject obj = (MessageObject) this.dialogMessage.get(did.longValue());
            if (obj != null && (obj.getId() == msgId.intValue() || obj.messageOwner.local_id == msgId.intValue())) {
                obj.messageOwner.id = newMsgId.intValue();
                obj.messageOwner.send_state = 0;
            }
            TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(did.longValue());
            if (dialog != null && dialog.top_message == msgId.intValue()) {
                dialog.top_message = newMsgId.intValue();
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            obj = (MessageObject) this.dialogMessagesByIds.get(msgId.intValue());
            this.dialogMessagesByIds.remove(msgId.intValue());
            if (obj != null) {
                this.dialogMessagesByIds.put(newMsgId.intValue(), obj);
            }
        } else if (id == NotificationCenter.updateMessageMedia) {
            Message message = args[0];
            MessageObject existMessageObject = (MessageObject) this.dialogMessagesByIds.get(message.id);
            if (existMessageObject != null) {
                existMessageObject.messageOwner.media = message.media;
                if (message.media.ttl_seconds == 0) {
                    return;
                }
                if ((message.media.photo instanceof TL_photoEmpty) || (message.media.document instanceof TL_documentEmpty)) {
                    existMessageObject.setType();
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$didReceivedNotification$4$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            User user = getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                putUser(user, true);
            } else {
                UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
            }
            if (user != null) {
                TL_photos_photo photo = (TL_photos_photo) response;
                ArrayList<PhotoSize> sizes = photo.photo.sizes;
                PhotoSize smallSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 100);
                PhotoSize bigSize = FileLoader.getClosestPhotoSizeWithSize(sizes, 1000);
                user.photo = new TL_userProfilePhoto();
                user.photo.photo_id = photo.photo.id;
                if (smallSize != null) {
                    user.photo.photo_small = smallSize.location;
                }
                if (bigSize != null) {
                    user.photo.photo_big = bigSize.location;
                } else if (smallSize != null) {
                    user.photo.photo_small = smallSize.location;
                }
                MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user.id);
                ArrayList<User> users = new ArrayList();
                users.add(user);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, false, true);
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$259(this));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$3$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(2));
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$didReceivedNotification$6$MessagesController(TL_wallPaperSettings settings, TLObject response, TL_error error) {
        TL_wallPaper wallPaper = (TL_wallPaper) response;
        File path = new File(ApplicationLoader.getFilesDirFixed(), this.uploadingWallpaperBlurred ? "wallpaper_original.jpg" : "wallpaper.jpg");
        if (wallPaper != null) {
            try {
                AndroidUtilities.copyFile(path, FileLoader.getPathToAttach(wallPaper.document, true));
            } catch (Exception e) {
            }
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$258(this, wallPaper, settings, path));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$5$MessagesController(TL_wallPaper wallPaper, TL_wallPaperSettings settings, File path) {
        if (this.uploadingWallpaper != null && wallPaper != null) {
            wallPaper.settings = settings;
            wallPaper.flags |= 4;
            Editor editor = getGlobalMainSettings().edit();
            editor.putLong("selectedBackground2", wallPaper.id);
            editor.commit();
            ArrayList<WallPaper> wallpapers = new ArrayList();
            wallpapers.add(wallPaper);
            MessagesStorage.getInstance(this.currentAccount).putWallpapers(wallpapers, 2);
            PhotoSize image = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 320);
            if (image != null) {
                String newKey = image.location.volume_id + "_" + image.location.local_id + "@100_100";
                ImageLoader.getInstance().replaceImageInCache(Utilities.MD5(path.getAbsolutePath()) + "@100_100", newKey, image.location, false);
            }
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.wallpapersNeedReload, Long.valueOf(wallPaper.id));
        }
    }

    public void cleanup() {
        ContactsController.getInstance(this.currentAccount).cleanup();
        MediaController.getInstance().cleanup();
        NotificationsController.getInstance(this.currentAccount).cleanup();
        SendMessagesHelper.getInstance(this.currentAccount).cleanup();
        SecretChatHelper.getInstance(this.currentAccount).cleanup();
        LocationController.getInstance(this.currentAccount).cleanup();
        DataQuery.getInstance(this.currentAccount).cleanup();
        DialogsActivity.dialogsLoaded[this.currentAccount] = false;
        this.notificationsPreferences.edit().clear().commit();
        this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).putLong("lastStickersLoadTime", 0).putLong("lastStickersLoadTimeMask", 0).putLong("lastStickersLoadTimeFavs", 0).commit();
        this.mainPreferences.edit().remove("gifhint").remove("soundHint").remove("dcDomainName").remove("webFileDatacenterId").commit();
        this.reloadingWebpages.clear();
        this.reloadingWebpagesPending.clear();
        this.dialogs_dict.clear();
        this.dialogs_read_inbox_max.clear();
        this.dialogs_read_outbox_max.clear();
        this.exportedChats.clear();
        this.fullUsers.clear();
        this.dialogs.clear();
        this.unreadUnmutedDialogs = 0;
        this.joiningToChannels.clear();
        this.migratedChats.clear();
        this.channelViewsToSend.clear();
        this.pollsToCheck.clear();
        this.pollsToCheckSize = 0;
        this.dialogsServerOnly.clear();
        this.dialogsForward.clear();
        this.dialogsCanAddUsers.clear();
        this.dialogsChannelsOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsUsersOnly.clear();
        this.dialogMessagesByIds.clear();
        this.dialogMessagesByRandomIds.clear();
        this.channelAdmins.clear();
        this.loadingChannelAdmins.clear();
        this.users.clear();
        this.objectsByUsernames.clear();
        this.chats.clear();
        this.dialogMessage.clear();
        this.deletedHistory.clear();
        this.printingUsers.clear();
        this.printingStrings.clear();
        this.printingStringsTypes.clear();
        this.onlinePrivacy.clear();
        this.loadingPeerSettings.clear();
        this.deletingDialogs.clear();
        this.clearingHistoryDialogs.clear();
        this.lastPrintingStringCount = 0;
        this.nextDialogsCacheOffset = 0;
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$6(this));
        this.createdDialogMainThreadIds.clear();
        this.visibleDialogMainThreadIds.clear();
        this.blockedUsers.clear();
        this.sendingTypings.clear();
        this.loadingFullUsers.clear();
        this.loadedFullUsers.clear();
        this.reloadingMessages.clear();
        this.loadingFullChats.clear();
        this.loadingFullParticipants.clear();
        this.loadedFullParticipants.clear();
        this.loadedFullChats.clear();
        this.checkingTosUpdate = false;
        this.nextTosCheckTime = 0;
        this.nextProxyInfoCheckTime = 0;
        this.checkingProxyInfo = false;
        this.loadingUnreadDialogs = false;
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
        this.lastStatusUpdateTime = 0;
        this.offlineSent = false;
        this.registeringForPush = false;
        this.getDifferenceFirstSync = true;
        this.uploadingAvatar = null;
        this.uploadingWallpaper = null;
        this.statusRequest = 0;
        this.statusSettingState = 0;
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$7(this));
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        addSupportUser();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$cleanup$7$MessagesController() {
        this.readTasks.clear();
        this.readTasksMap.clear();
        this.updatesQueueSeq.clear();
        this.updatesQueuePts.clear();
        this.updatesQueueQts.clear();
        this.gettingUnknownChannels.clear();
        this.updatesStartWaitTimeSeq = 0;
        this.updatesStartWaitTimePts = 0;
        this.updatesStartWaitTimeQts = 0;
        this.createdDialogIds.clear();
        this.gettingDifference = false;
        this.resetDialogsPinned = null;
        this.resetDialogsAll = null;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$cleanup$8$MessagesController() {
        ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(false);
        this.updatesQueueChannels.clear();
        this.updatesStartWaitTimeChannels.clear();
        this.gettingDifferenceChannels.clear();
        this.channelsPts.clear();
        this.shortPollChannels.clear();
        this.needShortPollChannels.clear();
        this.shortPollOnlines.clear();
        this.needShortPollOnlines.clear();
    }

    public User getUser(Integer id) {
        return (User) this.users.get(id);
    }

    public TLObject getUserOrChat(String username) {
        if (username == null || username.length() == 0) {
            return null;
        }
        return (TLObject) this.objectsByUsernames.get(username.toLowerCase());
    }

    public ConcurrentHashMap<Integer, User> getUsers() {
        return this.users;
    }

    public Chat getChat(Integer id) {
        return (Chat) this.chats.get(id);
    }

    public EncryptedChat getEncryptedChat(Integer id) {
        return (EncryptedChat) this.encryptedChats.get(id);
    }

    public EncryptedChat getEncryptedChatDB(int chat_id, boolean created) {
        EncryptedChat chat = (EncryptedChat) this.encryptedChats.get(Integer.valueOf(chat_id));
        if (chat != null) {
            if (!created) {
                return chat;
            }
            if (!((chat instanceof TL_encryptedChatWaiting) || (chat instanceof TL_encryptedChatRequested))) {
                return chat;
            }
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ArrayList<TLObject> result = new ArrayList();
        MessagesStorage.getInstance(this.currentAccount).getEncryptedChat(chat_id, countDownLatch, result);
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (result.size() != 2) {
            return chat;
        }
        chat = (EncryptedChat) result.get(0);
        User user = (User) result.get(1);
        putEncryptedChat(chat, false);
        putUser(user, true);
        return chat;
    }

    public boolean isDialogCreated(long dialog_id) {
        return this.createdDialogMainThreadIds.contains(Long.valueOf(dialog_id));
    }

    public boolean isDialogVisible(long dialog_id) {
        return this.visibleDialogMainThreadIds.contains(Long.valueOf(dialog_id));
    }

    public void setLastVisibleDialogId(long dialog_id, boolean set) {
        if (!set) {
            this.visibleDialogMainThreadIds.remove(Long.valueOf(dialog_id));
        } else if (!this.visibleDialogMainThreadIds.contains(Long.valueOf(dialog_id))) {
            this.visibleDialogMainThreadIds.add(Long.valueOf(dialog_id));
        }
    }

    public void setLastCreatedDialogId(long dialogId, boolean set) {
        if (!set) {
            this.createdDialogMainThreadIds.remove(Long.valueOf(dialogId));
            SparseArray<MessageObject> array = (SparseArray) this.pollsToCheck.get(dialogId);
            if (array != null) {
                int N = array.size();
                for (int a = 0; a < N; a++) {
                    ((MessageObject) array.valueAt(a)).pollVisibleOnScreen = false;
                }
            }
        } else if (!this.createdDialogMainThreadIds.contains(Long.valueOf(dialogId))) {
            this.createdDialogMainThreadIds.add(Long.valueOf(dialogId));
        } else {
            return;
        }
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$8(this, set, dialogId));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setLastCreatedDialogId$9$MessagesController(boolean set, long dialogId) {
        if (!set) {
            this.createdDialogIds.remove(Long.valueOf(dialogId));
        } else if (!this.createdDialogIds.contains(Long.valueOf(dialogId))) {
            this.createdDialogIds.add(Long.valueOf(dialogId));
        }
    }

    public ExportedChatInvite getExportedInvite(int chat_id) {
        return (ExportedChatInvite) this.exportedChats.get(chat_id);
    }

    public boolean putUser(User user, boolean fromCache) {
        if (user == null) {
            return false;
        }
        if (!fromCache || user.id / 1000 == 333 || user.id == 777000) {
            fromCache = false;
        } else {
            fromCache = true;
        }
        User oldUser = (User) this.users.get(Integer.valueOf(user.id));
        if (oldUser == user) {
            return false;
        }
        if (!(oldUser == null || TextUtils.isEmpty(oldUser.username))) {
            this.objectsByUsernames.remove(oldUser.username.toLowerCase());
        }
        if (!TextUtils.isEmpty(user.username)) {
            this.objectsByUsernames.put(user.username.toLowerCase(), user);
        }
        if (user.min) {
            if (oldUser == null) {
                this.users.put(Integer.valueOf(user.id), user);
                return false;
            } else if (fromCache) {
                return false;
            } else {
                if (user.bot) {
                    if (user.username != null) {
                        oldUser.username = user.username;
                        oldUser.flags |= 8;
                    } else {
                        oldUser.flags &= -9;
                        oldUser.username = null;
                    }
                }
                if (user.photo != null) {
                    oldUser.photo = user.photo;
                    oldUser.flags |= 32;
                    return false;
                }
                oldUser.flags &= -33;
                oldUser.photo = null;
                return false;
            }
        } else if (!fromCache) {
            this.users.put(Integer.valueOf(user.id), user);
            if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                UserConfig.getInstance(this.currentAccount).setCurrentUser(user);
                UserConfig.getInstance(this.currentAccount).saveConfig(true);
            }
            if (oldUser == null || user.status == null || oldUser.status == null || user.status.expires == oldUser.status.expires) {
                return false;
            }
            return true;
        } else if (oldUser == null) {
            this.users.put(Integer.valueOf(user.id), user);
            return false;
        } else if (!oldUser.min) {
            return false;
        } else {
            user.min = false;
            if (oldUser.bot) {
                if (oldUser.username != null) {
                    user.username = oldUser.username;
                    user.flags |= 8;
                } else {
                    user.flags &= -9;
                    user.username = null;
                }
            }
            if (oldUser.photo != null) {
                user.photo = oldUser.photo;
                user.flags |= 32;
            } else {
                user.flags &= -33;
                user.photo = null;
            }
            this.users.put(Integer.valueOf(user.id), user);
            return false;
        }
    }

    public void putUsers(ArrayList<User> users, boolean fromCache) {
        if (users != null && !users.isEmpty()) {
            boolean updateStatus = false;
            int count = users.size();
            for (int a = 0; a < count; a++) {
                if (putUser((User) users.get(a), fromCache)) {
                    updateStatus = true;
                }
            }
            if (updateStatus) {
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$9(this));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$putUsers$10$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
    }

    public void putChat(Chat chat, boolean fromCache) {
        if (chat != null) {
            Chat oldChat = (Chat) this.chats.get(Integer.valueOf(chat.id));
            if (oldChat != chat) {
                if (!(oldChat == null || TextUtils.isEmpty(oldChat.username))) {
                    this.objectsByUsernames.remove(oldChat.username.toLowerCase());
                }
                if (!TextUtils.isEmpty(chat.username)) {
                    this.objectsByUsernames.put(chat.username.toLowerCase(), chat);
                }
                if (chat.min) {
                    if (oldChat == null) {
                        this.chats.put(Integer.valueOf(chat.id), chat);
                    } else if (!fromCache) {
                        oldChat.title = chat.title;
                        oldChat.photo = chat.photo;
                        oldChat.broadcast = chat.broadcast;
                        oldChat.verified = chat.verified;
                        oldChat.megagroup = chat.megagroup;
                        if (chat.default_banned_rights != null) {
                            oldChat.default_banned_rights = chat.default_banned_rights;
                            oldChat.flags |= 262144;
                        }
                        if (chat.admin_rights != null) {
                            oldChat.admin_rights = chat.admin_rights;
                            oldChat.flags |= 16384;
                        }
                        if (chat.banned_rights != null) {
                            oldChat.banned_rights = chat.banned_rights;
                            oldChat.flags |= 32768;
                        }
                        if (chat.username != null) {
                            oldChat.username = chat.username;
                            oldChat.flags |= 64;
                        } else {
                            oldChat.flags &= -65;
                            oldChat.username = null;
                        }
                        if (chat.participants_count != 0) {
                            oldChat.participants_count = chat.participants_count;
                        }
                    }
                } else if (!fromCache) {
                    if (oldChat != null) {
                        int newFlags;
                        if (chat.version != oldChat.version) {
                            this.loadedFullChats.remove(Integer.valueOf(chat.id));
                        }
                        if (oldChat.participants_count != 0 && chat.participants_count == 0) {
                            chat.participants_count = oldChat.participants_count;
                            chat.flags |= 131072;
                        }
                        int oldFlags = oldChat.banned_rights != null ? oldChat.banned_rights.flags : 0;
                        if (chat.banned_rights != null) {
                            newFlags = chat.banned_rights.flags;
                        } else {
                            newFlags = 0;
                        }
                        int oldFlags2;
                        if (oldChat.default_banned_rights != null) {
                            oldFlags2 = oldChat.default_banned_rights.flags;
                        } else {
                            oldFlags2 = 0;
                        }
                        int newFlags2;
                        if (chat.default_banned_rights != null) {
                            newFlags2 = chat.default_banned_rights.flags;
                        } else {
                            newFlags2 = 0;
                        }
                        oldChat.default_banned_rights = chat.default_banned_rights;
                        if (oldChat.default_banned_rights == null) {
                            oldChat.flags &= -262145;
                        } else {
                            oldChat.flags |= 262144;
                        }
                        oldChat.banned_rights = chat.banned_rights;
                        if (oldChat.banned_rights == null) {
                            oldChat.flags &= -32769;
                        } else {
                            oldChat.flags |= 32768;
                        }
                        oldChat.admin_rights = chat.admin_rights;
                        if (oldChat.admin_rights == null) {
                            oldChat.flags &= -16385;
                        } else {
                            oldChat.flags |= 16384;
                        }
                        if (!(oldFlags == newFlags && oldFlags2 == newFlags2)) {
                            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$10(this, chat));
                        }
                    }
                    this.chats.put(Integer.valueOf(chat.id), chat);
                } else if (oldChat == null) {
                    this.chats.put(Integer.valueOf(chat.id), chat);
                } else if (oldChat.min) {
                    chat.min = false;
                    chat.title = oldChat.title;
                    chat.photo = oldChat.photo;
                    chat.broadcast = oldChat.broadcast;
                    chat.verified = oldChat.verified;
                    chat.megagroup = oldChat.megagroup;
                    if (oldChat.default_banned_rights != null) {
                        chat.default_banned_rights = oldChat.default_banned_rights;
                        chat.flags |= 262144;
                    }
                    if (oldChat.admin_rights != null) {
                        chat.admin_rights = oldChat.admin_rights;
                        chat.flags |= 16384;
                    }
                    if (oldChat.banned_rights != null) {
                        chat.banned_rights = oldChat.banned_rights;
                        chat.flags |= 32768;
                    }
                    if (oldChat.username != null) {
                        chat.username = oldChat.username;
                        chat.flags |= 64;
                    } else {
                        chat.flags &= -65;
                        chat.username = null;
                    }
                    if (oldChat.participants_count != 0 && chat.participants_count == 0) {
                        chat.participants_count = oldChat.participants_count;
                        chat.flags |= 131072;
                    }
                    this.chats.put(Integer.valueOf(chat.id), chat);
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$putChat$11$MessagesController(Chat chat) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.channelRightsUpdated, chat);
    }

    public void putChats(ArrayList<Chat> chats, boolean fromCache) {
        if (chats != null && !chats.isEmpty()) {
            int count = chats.size();
            for (int a = 0; a < count; a++) {
                putChat((Chat) chats.get(a), fromCache);
            }
        }
    }

    public void setReferer(String referer) {
        if (referer != null) {
            this.installReferer = referer;
            this.mainPreferences.edit().putString("installReferer", referer).commit();
        }
    }

    public void putEncryptedChat(EncryptedChat encryptedChat, boolean fromCache) {
        if (encryptedChat != null) {
            if (fromCache) {
                this.encryptedChats.putIfAbsent(Integer.valueOf(encryptedChat.id), encryptedChat);
            } else {
                this.encryptedChats.put(Integer.valueOf(encryptedChat.id), encryptedChat);
            }
        }
    }

    public void putEncryptedChats(ArrayList<EncryptedChat> encryptedChats, boolean fromCache) {
        if (encryptedChats != null && !encryptedChats.isEmpty()) {
            int count = encryptedChats.size();
            for (int a = 0; a < count; a++) {
                putEncryptedChat((EncryptedChat) encryptedChats.get(a), fromCache);
            }
        }
    }

    public TL_userFull getUserFull(int uid) {
        return (TL_userFull) this.fullUsers.get(uid);
    }

    public void cancelLoadFullUser(int uid) {
        this.loadingFullUsers.remove(Integer.valueOf(uid));
    }

    public void cancelLoadFullChat(int cid) {
        this.loadingFullChats.remove(Integer.valueOf(cid));
    }

    /* Access modifiers changed, original: protected */
    public void clearFullUsers() {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
    }

    private void reloadDialogsReadValue(ArrayList<TL_dialog> dialogs, long did) {
        if (did != 0 || (dialogs != null && !dialogs.isEmpty())) {
            TL_messages_getPeerDialogs req = new TL_messages_getPeerDialogs();
            InputPeer inputPeer;
            TL_inputDialogPeer inputDialogPeer;
            if (dialogs != null) {
                for (int a = 0; a < dialogs.size(); a++) {
                    inputPeer = getInputPeer((int) ((TL_dialog) dialogs.get(a)).id);
                    if (!(inputPeer instanceof TL_inputPeerChannel) || inputPeer.access_hash != 0) {
                        inputDialogPeer = new TL_inputDialogPeer();
                        inputDialogPeer.peer = inputPeer;
                        req.peers.add(inputDialogPeer);
                    }
                }
            } else {
                inputPeer = getInputPeer((int) did);
                if (!(inputPeer instanceof TL_inputPeerChannel) || inputPeer.access_hash != 0) {
                    inputDialogPeer = new TL_inputDialogPeer();
                    inputDialogPeer.peer = inputPeer;
                    req.peers.add(inputDialogPeer);
                } else {
                    return;
                }
            }
            if (!req.peers.isEmpty()) {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$11(this));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$reloadDialogsReadValue$12$MessagesController(TLObject response, TL_error error) {
        if (response != null) {
            TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
            ArrayList<Update> arrayList = new ArrayList();
            for (int a = 0; a < res.dialogs.size(); a++) {
                TL_dialog dialog = (TL_dialog) res.dialogs.get(a);
                if (dialog.read_inbox_max_id == 0) {
                    dialog.read_inbox_max_id = 1;
                }
                if (dialog.read_outbox_max_id == 0) {
                    dialog.read_outbox_max_id = 1;
                }
                if (dialog.id == 0 && dialog.peer != null) {
                    if (dialog.peer.user_id != 0) {
                        dialog.id = (long) dialog.peer.user_id;
                    } else if (dialog.peer.chat_id != 0) {
                        dialog.id = (long) (-dialog.peer.chat_id);
                    } else if (dialog.peer.channel_id != 0) {
                        dialog.id = (long) (-dialog.peer.channel_id);
                    }
                }
                Integer value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                if (value == null) {
                    value = Integer.valueOf(0);
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_inbox_max_id, value.intValue())));
                if (value.intValue() == 0) {
                    if (dialog.peer.channel_id != 0) {
                        TL_updateReadChannelInbox update = new TL_updateReadChannelInbox();
                        update.channel_id = dialog.peer.channel_id;
                        update.max_id = dialog.read_inbox_max_id;
                        arrayList.add(update);
                    } else {
                        TL_updateReadHistoryInbox update2 = new TL_updateReadHistoryInbox();
                        update2.peer = dialog.peer;
                        update2.max_id = dialog.read_inbox_max_id;
                        arrayList.add(update2);
                    }
                }
                value = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                if (value == null) {
                    value = Integer.valueOf(0);
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_outbox_max_id, value.intValue())));
                if (value.intValue() == 0) {
                    if (dialog.peer.channel_id != 0) {
                        TL_updateReadChannelOutbox update3 = new TL_updateReadChannelOutbox();
                        update3.channel_id = dialog.peer.channel_id;
                        update3.max_id = dialog.read_outbox_max_id;
                        arrayList.add(update3);
                    } else {
                        TL_updateReadHistoryOutbox update4 = new TL_updateReadHistoryOutbox();
                        update4.peer = dialog.peer;
                        update4.max_id = dialog.read_outbox_max_id;
                        arrayList.add(update4);
                    }
                }
            }
            if (!arrayList.isEmpty()) {
                processUpdateArray(arrayList, null, null, false);
            }
        }
    }

    public boolean isChannelAdmin(int chatId, int uid) {
        ArrayList<Integer> array = (ArrayList) this.channelAdmins.get(chatId);
        return array != null && array.indexOf(Integer.valueOf(uid)) >= 0;
    }

    public void loadChannelAdmins(int chatId, boolean cache) {
        if (this.loadingChannelAdmins.indexOfKey(chatId) < 0) {
            this.loadingChannelAdmins.put(chatId, 0);
            if (cache) {
                MessagesStorage.getInstance(this.currentAccount).loadChannelAdmins(chatId);
                return;
            }
            TL_channels_getParticipants req = new TL_channels_getParticipants();
            ArrayList<Integer> array = (ArrayList) this.channelAdmins.get(chatId);
            if (array != null) {
                long acc = 0;
                for (int a = 0; a < array.size(); a++) {
                    acc = (((20261 * acc) + 2147483648L) + ((long) ((Integer) array.get(a)).intValue())) % 2147483648L;
                }
                req.hash = (int) acc;
            }
            req.channel = getInputChannel(chatId);
            req.limit = 100;
            req.filter = new TL_channelParticipantsAdmins();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$12(this, chatId));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadChannelAdmins$13$MessagesController(int chatId, TLObject response, TL_error error) {
        if (response instanceof TL_channels_channelParticipants) {
            TL_channels_channelParticipants participants = (TL_channels_channelParticipants) response;
            ArrayList<Integer> array1 = new ArrayList(participants.participants.size());
            for (int a = 0; a < participants.participants.size(); a++) {
                array1.add(Integer.valueOf(((ChannelParticipant) participants.participants.get(a)).user_id));
            }
            processLoadedChannelAdmins(array1, chatId, false);
        }
    }

    public void processLoadedChannelAdmins(ArrayList<Integer> array, int chatId, boolean cache) {
        Collections.sort(array);
        if (!cache) {
            MessagesStorage.getInstance(this.currentAccount).putChannelAdmins(chatId, array);
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$13(this, chatId, array, cache));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processLoadedChannelAdmins$14$MessagesController(int chatId, ArrayList array, boolean cache) {
        this.loadingChannelAdmins.delete(chatId);
        this.channelAdmins.put(chatId, array);
        if (cache) {
            loadChannelAdmins(chatId, false);
        }
    }

    public void loadFullChat(int chat_id, int classGuid, boolean force) {
        boolean loaded = this.loadedFullChats.contains(Integer.valueOf(chat_id));
        if (!this.loadingFullChats.contains(Integer.valueOf(chat_id))) {
            if (force || !loaded) {
                TLObject request;
                this.loadingFullChats.add(Integer.valueOf(chat_id));
                long dialog_id = (long) (-chat_id);
                Chat chat = getChat(Integer.valueOf(chat_id));
                TLObject req;
                if (ChatObject.isChannel(chat)) {
                    req = new TL_channels_getFullChannel();
                    req.channel = getInputChannel(chat);
                    request = req;
                    if (chat.megagroup) {
                        loadChannelAdmins(chat_id, !loaded);
                    }
                } else {
                    req = new TL_messages_getFullChat();
                    req.chat_id = chat_id;
                    request = req;
                    if (this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id)) == null) {
                        reloadDialogsReadValue(null, dialog_id);
                    }
                }
                int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new MessagesController$$Lambda$14(this, chat, dialog_id, chat_id, classGuid));
                if (classGuid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, classGuid);
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadFullChat$17$MessagesController(Chat chat, long dialog_id, int chat_id, int classGuid, TLObject response, TL_error error) {
        if (error == null) {
            TL_messages_chatFull res = (TL_messages_chatFull) response;
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(res.full_chat, false);
            if (ChatObject.isChannel(chat)) {
                ArrayList<Update> arrayList;
                Integer value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(false, dialog_id));
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(res.full_chat.read_inbox_max_id, value.intValue())));
                if (value.intValue() == 0) {
                    arrayList = new ArrayList();
                    TL_updateReadChannelInbox update = new TL_updateReadChannelInbox();
                    update.channel_id = chat_id;
                    update.max_id = res.full_chat.read_inbox_max_id;
                    arrayList.add(update);
                    processUpdateArray(arrayList, null, null, false);
                }
                value = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, dialog_id));
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(res.full_chat.read_outbox_max_id, value.intValue())));
                if (value.intValue() == 0) {
                    arrayList = new ArrayList();
                    TL_updateReadChannelOutbox update2 = new TL_updateReadChannelOutbox();
                    update2.channel_id = chat_id;
                    update2.max_id = res.full_chat.read_outbox_max_id;
                    arrayList.add(update2);
                    processUpdateArray(arrayList, null, null, false);
                }
            }
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$256(this, chat_id, res, classGuid));
            return;
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$257(this, error, chat_id));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$15$MessagesController(int chat_id, TL_messages_chatFull res, int classGuid) {
        applyDialogNotificationsSettings((long) (-chat_id), res.full_chat.notify_settings);
        for (int a = 0; a < res.full_chat.bot_info.size(); a++) {
            DataQuery.getInstance(this.currentAccount).putBotInfo((BotInfo) res.full_chat.bot_info.get(a));
        }
        this.exportedChats.put(chat_id, res.full_chat.exported_invite);
        this.loadingFullChats.remove(Integer.valueOf(chat_id));
        this.loadedFullChats.add(Integer.valueOf(chat_id));
        putUsers(res.users, false);
        putChats(res.chats, false);
        if (res.full_chat.stickerset != null) {
            DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(res.full_chat.stickerset);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, res.full_chat, Integer.valueOf(classGuid), Boolean.valueOf(false), null);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$16$MessagesController(TL_error error, int chat_id) {
        checkChannelError(error.text, chat_id);
        this.loadingFullChats.remove(Integer.valueOf(chat_id));
    }

    public void loadFullUser(User user, int classGuid, boolean force) {
        if (user != null && !this.loadingFullUsers.contains(Integer.valueOf(user.id))) {
            if (force || !this.loadedFullUsers.contains(Integer.valueOf(user.id))) {
                this.loadingFullUsers.add(Integer.valueOf(user.id));
                TL_users_getFullUser req = new TL_users_getFullUser();
                req.id = getInputUser(user);
                long dialog_id = (long) user.id;
                if (this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id)) == null) {
                    reloadDialogsReadValue(null, dialog_id);
                }
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$15(this, user, classGuid)), classGuid);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadFullUser$20$MessagesController(User user, int classGuid, TLObject response, TL_error error) {
        if (error == null) {
            TL_userFull userFull = (TL_userFull) response;
            MessagesStorage.getInstance(this.currentAccount).updateUserInfo(userFull, false);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$254(this, user, userFull, classGuid));
            return;
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$255(this, user));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$18$MessagesController(User user, TL_userFull userFull, int classGuid) {
        applyDialogNotificationsSettings((long) user.id, userFull.notify_settings);
        if (userFull.bot_info instanceof TL_botInfo) {
            DataQuery.getInstance(this.currentAccount).putBotInfo(userFull.bot_info);
        }
        int index = this.blockedUsers.indexOfKey(user.id);
        if (userFull.blocked) {
            if (index < 0) {
                SparseIntArray ids = new SparseIntArray();
                ids.put(user.id, 1);
                MessagesStorage.getInstance(this.currentAccount).putBlockedUsers(ids, false);
                this.blockedUsers.put(user.id, 1);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            }
        } else if (index >= 0) {
            MessagesStorage.getInstance(this.currentAccount).deleteBlockedUser(user.id);
            this.blockedUsers.removeAt(index);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
        this.fullUsers.put(user.id, userFull);
        this.loadingFullUsers.remove(Integer.valueOf(user.id));
        this.loadedFullUsers.add(Integer.valueOf(user.id));
        String names = user.first_name + user.last_name + user.username;
        ArrayList<User> users = new ArrayList();
        users.add(userFull.user);
        putUsers(users, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, false, true);
        if (!(names == null || names.equals(userFull.user.first_name + userFull.user.last_name + userFull.user.username))) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
        }
        if (userFull.bot_info instanceof TL_botInfo) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoad, userFull.bot_info, Integer.valueOf(classGuid));
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(user.id), userFull, null);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$19$MessagesController(User user) {
        this.loadingFullUsers.remove(Integer.valueOf(user.id));
    }

    private void reloadMessages(ArrayList<Integer> mids, long dialog_id) {
        if (!mids.isEmpty()) {
            TLObject request;
            ArrayList<Integer> result = new ArrayList();
            Chat chat = ChatObject.getChatByDialog(dialog_id, this.currentAccount);
            TLObject req;
            if (ChatObject.isChannel(chat)) {
                req = new TL_channels_getMessages();
                req.channel = getInputChannel(chat);
                req.id = result;
                request = req;
            } else {
                req = new TL_messages_getMessages();
                req.id = result;
                request = req;
            }
            ArrayList<Integer> arrayList = (ArrayList) this.reloadingMessages.get(dialog_id);
            for (int a = 0; a < mids.size(); a++) {
                Integer mid = (Integer) mids.get(a);
                if (arrayList == null || !arrayList.contains(mid)) {
                    result.add(mid);
                }
            }
            if (!result.isEmpty()) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.reloadingMessages.put(dialog_id, arrayList);
                }
                arrayList.addAll(result);
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new MessagesController$$Lambda$16(this, dialog_id, chat, result));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$reloadMessages$22$MessagesController(long dialog_id, Chat chat, ArrayList result, TLObject response, TL_error error) {
        if (error == null) {
            int a;
            messages_Messages messagesRes = (messages_Messages) response;
            SparseArray usersLocal = new SparseArray();
            for (a = 0; a < messagesRes.users.size(); a++) {
                User u = (User) messagesRes.users.get(a);
                usersLocal.put(u.id, u);
            }
            SparseArray chatsLocal = new SparseArray();
            for (a = 0; a < messagesRes.chats.size(); a++) {
                Chat c = (Chat) messagesRes.chats.get(a);
                chatsLocal.put(c.id, c);
            }
            Integer inboxValue = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
            if (inboxValue == null) {
                inboxValue = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(false, dialog_id));
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), inboxValue);
            }
            Integer outboxValue = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
            if (outboxValue == null) {
                outboxValue = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, dialog_id));
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), outboxValue);
            }
            ArrayList<MessageObject> objects = new ArrayList();
            for (a = 0; a < messagesRes.messages.size(); a++) {
                Integer num;
                Message message = (Message) messagesRes.messages.get(a);
                if (chat != null && chat.megagroup) {
                    message.flags |= Integer.MIN_VALUE;
                }
                message.dialog_id = dialog_id;
                if (message.out) {
                    num = outboxValue;
                } else {
                    num = inboxValue;
                }
                message.unread = num.intValue() < message.id;
                objects.add(new MessageObject(this.currentAccount, message, usersLocal, chatsLocal, true));
            }
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
            MessagesStorage.getInstance(this.currentAccount).putMessages(messagesRes, dialog_id, -1, 0, false);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$253(this, dialog_id, result, objects));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$21$MessagesController(long dialog_id, ArrayList result, ArrayList objects) {
        ArrayList<Integer> arrayList1 = (ArrayList) this.reloadingMessages.get(dialog_id);
        if (arrayList1 != null) {
            arrayList1.removeAll(result);
            if (arrayList1.isEmpty()) {
                this.reloadingMessages.remove(dialog_id);
            }
        }
        MessageObject dialogObj = (MessageObject) this.dialogMessage.get(dialog_id);
        if (dialogObj != null) {
            int a = 0;
            while (a < objects.size()) {
                MessageObject obj = (MessageObject) objects.get(a);
                if (dialogObj == null || dialogObj.getId() != obj.getId()) {
                    a++;
                } else {
                    this.dialogMessage.put(dialog_id, obj);
                    if (obj.messageOwner.to_id.channel_id == 0) {
                        MessageObject obj2 = (MessageObject) this.dialogMessagesByIds.get(obj.getId());
                        this.dialogMessagesByIds.remove(obj.getId());
                        if (obj2 != null) {
                            this.dialogMessagesByIds.put(obj2.getId(), obj2);
                        }
                    }
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialog_id), objects);
    }

    public void hideReportSpam(long dialogId, User currentUser, Chat currentChat) {
        if (currentUser != null || currentChat != null) {
            Editor editor = this.notificationsPreferences.edit();
            editor.putInt("spam3_" + dialogId, 1);
            editor.commit();
            if (((int) dialogId) != 0) {
                TL_messages_hideReportSpam req = new TL_messages_hideReportSpam();
                if (currentUser != null) {
                    req.peer = getInputPeer(currentUser.id);
                } else if (currentChat != null) {
                    req.peer = getInputPeer(-currentChat.id);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, MessagesController$$Lambda$17.$instance);
            }
        }
    }

    static final /* synthetic */ void lambda$hideReportSpam$23$MessagesController(TLObject response, TL_error error) {
    }

    public void reportSpam(long dialogId, User currentUser, Chat currentChat, EncryptedChat currentEncryptedChat) {
        if (currentUser != null || currentChat != null || currentEncryptedChat != null) {
            Editor editor = this.notificationsPreferences.edit();
            editor.putInt("spam3_" + dialogId, 1);
            editor.commit();
            if (((int) dialogId) != 0) {
                TL_messages_reportSpam req = new TL_messages_reportSpam();
                if (currentChat != null) {
                    req.peer = getInputPeer(-currentChat.id);
                } else if (currentUser != null) {
                    req.peer = getInputPeer(currentUser.id);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, MessagesController$$Lambda$19.$instance, 2);
            } else if (currentEncryptedChat != null && currentEncryptedChat.access_hash != 0) {
                TL_messages_reportEncryptedSpam req2 = new TL_messages_reportEncryptedSpam();
                req2.peer = new TL_inputEncryptedChat();
                req2.peer.chat_id = currentEncryptedChat.id;
                req2.peer.access_hash = currentEncryptedChat.access_hash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, MessagesController$$Lambda$18.$instance, 2);
            }
        }
    }

    static final /* synthetic */ void lambda$reportSpam$24$MessagesController(TLObject response, TL_error error) {
    }

    static final /* synthetic */ void lambda$reportSpam$25$MessagesController(TLObject response, TL_error error) {
    }

    public void loadPeerSettings(User currentUser, Chat currentChat) {
        if (currentUser != null || currentChat != null) {
            long dialogId;
            if (currentUser != null) {
                dialogId = (long) currentUser.id;
            } else {
                dialogId = (long) (-currentChat.id);
            }
            if (this.loadingPeerSettings.indexOfKey(dialogId) < 0) {
                this.loadingPeerSettings.put(dialogId, Boolean.valueOf(true));
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("request spam button for " + dialogId);
                }
                if (this.notificationsPreferences.getInt("spam3_" + dialogId, 0) == 1) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("spam button already hidden for " + dialogId);
                    }
                } else if (this.notificationsPreferences.getBoolean("spam_" + dialogId, false)) {
                    TL_messages_hideReportSpam req = new TL_messages_hideReportSpam();
                    if (currentUser != null) {
                        req.peer = getInputPeer(currentUser.id);
                    } else if (currentChat != null) {
                        req.peer = getInputPeer(-currentChat.id);
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$20(this, dialogId));
                } else {
                    TL_messages_getPeerSettings req2 = new TL_messages_getPeerSettings();
                    if (currentUser != null) {
                        req2.peer = getInputPeer(currentUser.id);
                    } else if (currentChat != null) {
                        req2.peer = getInputPeer(-currentChat.id);
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$21(this, dialogId));
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadPeerSettings$27$MessagesController(long dialogId, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$252(this, dialogId));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$26$MessagesController(long dialogId) {
        this.loadingPeerSettings.remove(dialogId);
        Editor editor = this.notificationsPreferences.edit();
        editor.remove("spam_" + dialogId);
        editor.putInt("spam3_" + dialogId, 1);
        editor.commit();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadPeerSettings$29$MessagesController(long dialogId, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$251(this, dialogId, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$28$MessagesController(long dialogId, TLObject response) {
        this.loadingPeerSettings.remove(dialogId);
        if (response != null) {
            TL_peerSettings res = (TL_peerSettings) response;
            Editor editor = this.notificationsPreferences.edit();
            if (res.report_spam) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("show spam button for " + dialogId);
                }
                editor.putInt("spam3_" + dialogId, 2);
                editor.commit();
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.peerSettingsDidLoad, Long.valueOf(dialogId));
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("don't show spam button for " + dialogId);
            }
            editor.putInt("spam3_" + dialogId, 1);
            editor.commit();
        }
    }

    /* Access modifiers changed, original: protected */
    public void processNewChannelDifferenceParams(int pts, int pts_count, int channelId) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processNewChannelDifferenceParams pts = " + pts + " pts_count = " + pts_count + " channeldId = " + channelId);
        }
        int channelPts = this.channelsPts.get(channelId);
        if (channelPts == 0) {
            channelPts = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(channelId);
            if (channelPts == 0) {
                channelPts = 1;
            }
            this.channelsPts.put(channelId, channelPts);
        }
        if (channelPts + pts_count == pts) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("APPLY CHANNEL PTS");
            }
            this.channelsPts.put(channelId, pts);
            MessagesStorage.getInstance(this.currentAccount).saveChannelPts(channelId, pts);
        } else if (channelPts != pts) {
            long updatesStartWaitTime = this.updatesStartWaitTimeChannels.get(channelId);
            if (this.gettingDifferenceChannels.get(channelId) || updatesStartWaitTime == 0 || Math.abs(System.currentTimeMillis() - updatesStartWaitTime) <= 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("ADD CHANNEL UPDATE TO QUEUE pts = " + pts + " pts_count = " + pts_count);
                }
                if (updatesStartWaitTime == 0) {
                    this.updatesStartWaitTimeChannels.put(channelId, System.currentTimeMillis());
                }
                UserActionUpdatesPts updates = new UserActionUpdatesPts(this, null);
                updates.pts = pts;
                updates.pts_count = pts_count;
                updates.chat_id = channelId;
                ArrayList<Updates> arrayList = (ArrayList) this.updatesQueueChannels.get(channelId);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    this.updatesQueueChannels.put(channelId, arrayList);
                }
                arrayList.add(updates);
                return;
            }
            getChannelDifference(channelId);
        }
    }

    /* Access modifiers changed, original: protected */
    public void processNewDifferenceParams(int seq, int pts, int date, int pts_count) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processNewDifferenceParams seq = " + seq + " pts = " + pts + " date = " + date + " pts_count = " + pts_count);
        }
        if (pts != -1) {
            if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + pts_count == pts) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("APPLY PTS");
                }
                MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(pts);
                MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
            } else if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() != pts) {
                if (this.gettingDifference || this.updatesStartWaitTimePts == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("ADD UPDATE TO QUEUE pts = " + pts + " pts_count = " + pts_count);
                    }
                    if (this.updatesStartWaitTimePts == 0) {
                        this.updatesStartWaitTimePts = System.currentTimeMillis();
                    }
                    UserActionUpdatesPts updates = new UserActionUpdatesPts(this, null);
                    updates.pts = pts;
                    updates.pts_count = pts_count;
                    this.updatesQueuePts.add(updates);
                } else {
                    getDifference();
                }
            }
        }
        if (seq == -1) {
            return;
        }
        if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 == seq) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("APPLY SEQ");
            }
            MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(seq);
            if (date != -1) {
                MessagesStorage.getInstance(this.currentAccount).setLastDateValue(date);
            }
            MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
        } else if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() == seq) {
        } else {
            if (this.gettingDifference || this.updatesStartWaitTimeSeq == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) <= 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("ADD UPDATE TO QUEUE seq = " + seq);
                }
                if (this.updatesStartWaitTimeSeq == 0) {
                    this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                }
                UserActionUpdatesSeq updates2 = new UserActionUpdatesSeq(this, null);
                updates2.seq = seq;
                this.updatesQueueSeq.add(updates2);
                return;
            }
            getDifference();
        }
    }

    public void didAddedNewTask(int minDate, SparseArray<ArrayList<Long>> mids) {
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$22(this, minDate));
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$23(this, mids));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$didAddedNewTask$30$MessagesController(int minDate) {
        if ((this.currentDeletingTaskMids == null && !this.gettingNewDeleteTask) || (this.currentDeletingTaskTime != 0 && minDate < this.currentDeletingTaskTime)) {
            getNewDeleteTask(null, 0);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$didAddedNewTask$31$MessagesController(SparseArray mids) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didCreatedNewDeleteTask, mids);
    }

    public void getNewDeleteTask(ArrayList<Integer> oldTask, int channelId) {
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$24(this, oldTask, channelId));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$getNewDeleteTask$32$MessagesController(ArrayList oldTask, int channelId) {
        this.gettingNewDeleteTask = true;
        MessagesStorage.getInstance(this.currentAccount).getNewTask(oldTask, channelId);
    }

    private boolean checkDeletingTask(boolean runnable) {
        int currentServerTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (this.currentDeletingTaskMids == null) {
            return false;
        }
        if (!runnable && (this.currentDeletingTaskTime == 0 || this.currentDeletingTaskTime > currentServerTime)) {
            return false;
        }
        this.currentDeletingTaskTime = 0;
        if (!(this.currentDeleteTaskRunnable == null || runnable)) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        }
        this.currentDeleteTaskRunnable = null;
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$25(this, new ArrayList(this.currentDeletingTaskMids)));
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkDeletingTask$34$MessagesController(ArrayList mids) {
        if (mids.isEmpty() || ((Integer) mids.get(0)).intValue() <= 0) {
            deleteMessages(mids, null, null, 0, false);
        } else {
            MessagesStorage.getInstance(this.currentAccount).emptyMessagesMedia(mids);
        }
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$250(this, mids));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$33$MessagesController(ArrayList mids) {
        getNewDeleteTask(mids, this.currentDeletingTaskChannelId);
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
    }

    public void processLoadedDeleteTask(int taskTime, ArrayList<Integer> messages, int channelId) {
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$26(this, messages, taskTime));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processLoadedDeleteTask$36$MessagesController(ArrayList messages, int taskTime) {
        this.gettingNewDeleteTask = false;
        if (messages != null) {
            this.currentDeletingTaskTime = taskTime;
            this.currentDeletingTaskMids = messages;
            if (this.currentDeleteTaskRunnable != null) {
                Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
                this.currentDeleteTaskRunnable = null;
            }
            if (!checkDeletingTask(false)) {
                this.currentDeleteTaskRunnable = new MessagesController$$Lambda$249(this);
                Utilities.stageQueue.postRunnable(this.currentDeleteTaskRunnable, ((long) Math.abs(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.currentDeletingTaskTime)) * 1000);
                return;
            }
            return;
        }
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$35$MessagesController() {
        checkDeletingTask(true);
    }

    public void loadDialogPhotos(int did, int count, long max_id, boolean fromCache, int classGuid) {
        if (fromCache) {
            MessagesStorage.getInstance(this.currentAccount).getDialogPhotos(did, count, max_id, classGuid);
        } else if (did > 0) {
            User user = getUser(Integer.valueOf(did));
            if (user != null) {
                TL_photos_getUserPhotos req = new TL_photos_getUserPhotos();
                req.limit = count;
                req.offset = 0;
                req.max_id = (long) ((int) max_id);
                req.user_id = getInputUser(user);
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$27(this, did, count, max_id, classGuid)), classGuid);
            }
        } else if (did < 0) {
            TL_messages_search req2 = new TL_messages_search();
            req2.filter = new TL_inputMessagesFilterChatPhotos();
            req2.limit = count;
            req2.offset_id = (int) max_id;
            req2.q = "";
            req2.peer = getInputPeer(did);
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$28(this, did, count, max_id, classGuid)), classGuid);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadDialogPhotos$37$MessagesController(int did, int count, long max_id, int classGuid, TLObject response, TL_error error) {
        if (error == null) {
            processLoadedUserPhotos((photos_Photos) response, did, count, max_id, false, classGuid);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadDialogPhotos$38$MessagesController(int did, int count, long max_id, int classGuid, TLObject response, TL_error error) {
        if (error == null) {
            messages_Messages messages = (messages_Messages) response;
            TL_photos_photos res = new TL_photos_photos();
            res.count = messages.count;
            res.users.addAll(messages.users);
            for (int a = 0; a < messages.messages.size(); a++) {
                Message message = (Message) messages.messages.get(a);
                if (!(message.action == null || message.action.photo == null)) {
                    res.photos.add(message.action.photo);
                }
            }
            processLoadedUserPhotos(res, did, count, max_id, false, classGuid);
        }
    }

    public void blockUser(int user_id) {
        User user = getUser(Integer.valueOf(user_id));
        if (user != null && this.blockedUsers.indexOfKey(user_id) < 0) {
            this.blockedUsers.put(user_id, 1);
            if (user.bot) {
                DataQuery.getInstance(this.currentAccount).removeInline(user_id);
            } else {
                DataQuery.getInstance(this.currentAccount).removePeer(user_id);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            TL_contacts_block req = new TL_contacts_block();
            req.id = getInputUser(user);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$29(this, user));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$blockUser$39$MessagesController(User user, TLObject response, TL_error error) {
        if (error == null) {
            SparseIntArray ids = new SparseIntArray();
            ids.put(user.id, 1);
            MessagesStorage.getInstance(this.currentAccount).putBlockedUsers(ids, false);
        }
    }

    public void setUserBannedRole(int chatId, User user, TL_chatBannedRights rights, boolean isChannel, BaseFragment parentFragment) {
        if (user != null && rights != null) {
            TL_channels_editBanned req = new TL_channels_editBanned();
            req.channel = getInputChannel(chatId);
            req.user_id = getInputUser(user);
            req.banned_rights = rights;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$30(this, chatId, parentFragment, req, isChannel));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setUserBannedRole$42$MessagesController(int chatId, BaseFragment parentFragment, TL_channels_editBanned req, boolean isChannel, TLObject response, TL_error error) {
        if (error == null) {
            processUpdates((Updates) response, false);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$247(this, chatId), 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$248(this, error, parentFragment, req, isChannel));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$40$MessagesController(int chatId) {
        loadFullChat(chatId, 0, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$41$MessagesController(TL_error error, BaseFragment parentFragment, TL_channels_editBanned req, boolean isChannel) {
        AlertsCreator.processError(this.currentAccount, error, parentFragment, req, Boolean.valueOf(isChannel));
    }

    public void setDefaultBannedRole(int chatId, TL_chatBannedRights rights, boolean isChannel, BaseFragment parentFragment) {
        if (rights != null) {
            TL_messages_editChatDefaultBannedRights req = new TL_messages_editChatDefaultBannedRights();
            req.peer = getInputPeer(-chatId);
            req.banned_rights = rights;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$31(this, chatId, parentFragment, req, isChannel));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setDefaultBannedRole$45$MessagesController(int chatId, BaseFragment parentFragment, TL_messages_editChatDefaultBannedRights req, boolean isChannel, TLObject response, TL_error error) {
        if (error == null) {
            processUpdates((Updates) response, false);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$245(this, chatId), 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$246(this, error, parentFragment, req, isChannel));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$43$MessagesController(int chatId) {
        loadFullChat(chatId, 0, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$44$MessagesController(TL_error error, BaseFragment parentFragment, TL_messages_editChatDefaultBannedRights req, boolean isChannel) {
        AlertsCreator.processError(this.currentAccount, error, parentFragment, req, Boolean.valueOf(isChannel));
    }

    public void setUserAdminRole(int chatId, User user, TL_chatAdminRights rights, boolean isChannel, BaseFragment parentFragment, boolean addingNew) {
        if (user != null && rights != null) {
            Chat chat = getChat(Integer.valueOf(chatId));
            if (ChatObject.isChannel(chat)) {
                TL_channels_editAdmin req = new TL_channels_editAdmin();
                req.channel = getInputChannel(chat);
                req.user_id = getInputUser(user);
                req.admin_rights = rights;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$32(this, chatId, parentFragment, req, isChannel));
                return;
            }
            TL_messages_editChatAdmin req2 = new TL_messages_editChatAdmin();
            req2.chat_id = chatId;
            req2.user_id = getInputUser(user);
            boolean z = rights.change_info || rights.delete_messages || rights.ban_users || rights.invite_users || rights.pin_messages || rights.add_admins;
            req2.is_admin = z;
            RequestDelegate messagesController$$Lambda$33 = new MessagesController$$Lambda$33(this, chatId, parentFragment, req2);
            if (req2.is_admin && addingNew) {
                addUserToChat(chatId, user, null, 0, null, parentFragment, new MessagesController$$Lambda$34(this, req2, messagesController$$Lambda$33));
            } else {
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, messagesController$$Lambda$33);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setUserAdminRole$48$MessagesController(int chatId, BaseFragment parentFragment, TL_channels_editAdmin req, boolean isChannel, TLObject response, TL_error error) {
        if (error == null) {
            processUpdates((Updates) response, false);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$243(this, chatId), 1000);
            return;
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$244(this, error, parentFragment, req, isChannel));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$46$MessagesController(int chatId) {
        loadFullChat(chatId, 0, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$47$MessagesController(TL_error error, BaseFragment parentFragment, TL_channels_editAdmin req, boolean isChannel) {
        AlertsCreator.processError(this.currentAccount, error, parentFragment, req, Boolean.valueOf(isChannel));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setUserAdminRole$51$MessagesController(int chatId, BaseFragment parentFragment, TL_messages_editChatAdmin req, TLObject response, TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$241(this, chatId), 1000);
        } else {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$242(this, error, parentFragment, req));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$49$MessagesController(int chatId) {
        loadFullChat(chatId, 0, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$50$MessagesController(TL_error error, BaseFragment parentFragment, TL_messages_editChatAdmin req) {
        AlertsCreator.processError(this.currentAccount, error, parentFragment, req, Boolean.valueOf(false));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$setUserAdminRole$52$MessagesController(TL_messages_editChatAdmin req, RequestDelegate requestDelegate) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, requestDelegate);
    }

    public void unblockUser(int user_id) {
        TL_contacts_unblock req = new TL_contacts_unblock();
        User user = getUser(Integer.valueOf(user_id));
        if (user != null) {
            this.blockedUsers.delete(user.id);
            req.id = getInputUser(user);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$35(this, user));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$unblockUser$53$MessagesController(User user, TLObject response, TL_error error) {
        MessagesStorage.getInstance(this.currentAccount).deleteBlockedUser(user.id);
    }

    public void getBlockedUsers(boolean cache) {
        if (UserConfig.getInstance(this.currentAccount).isClientActivated() && !this.loadingBlockedUsers) {
            this.loadingBlockedUsers = true;
            if (cache) {
                MessagesStorage.getInstance(this.currentAccount).getBlockedUsers();
                return;
            }
            TL_contacts_getBlocked req = new TL_contacts_getBlocked();
            req.offset = 0;
            req.limit = 200;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$36(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$getBlockedUsers$54$MessagesController(TLObject response, TL_error error) {
        SparseIntArray blocked = new SparseIntArray();
        ArrayList<User> users = null;
        if (error == null) {
            contacts_Blocked res = (contacts_Blocked) response;
            Iterator it = res.blocked.iterator();
            while (it.hasNext()) {
                blocked.put(((TL_contactBlocked) it.next()).user_id, 1);
            }
            users = res.users;
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, null, true, true);
            MessagesStorage.getInstance(this.currentAccount).putBlockedUsers(blocked, true);
        }
        processLoadedBlockedUsers(blocked, users, false);
    }

    public void processLoadedBlockedUsers(SparseIntArray ids, ArrayList<User> users, boolean cache) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$37(this, users, cache, ids));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processLoadedBlockedUsers$55$MessagesController(ArrayList users, boolean cache, SparseIntArray ids) {
        if (users != null) {
            putUsers(users, cache);
        }
        this.loadingBlockedUsers = false;
        if (ids.size() == 0 && cache && !UserConfig.getInstance(this.currentAccount).blockedUsersLoaded) {
            getBlockedUsers(false);
            return;
        }
        if (!cache) {
            UserConfig.getInstance(this.currentAccount).blockedUsersLoaded = true;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        this.blockedUsers = ids;
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
    }

    public void deleteUserPhoto(InputPhoto photo) {
        if (photo == null) {
            TL_photos_updateProfilePhoto req = new TL_photos_updateProfilePhoto();
            req.id = new TL_inputPhotoEmpty();
            UserConfig.getInstance(this.currentAccount).getCurrentUser().photo = new TL_userProfilePhotoEmpty();
            User user = getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user == null) {
                user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            }
            if (user != null) {
                user.photo = UserConfig.getInstance(this.currentAccount).getCurrentUser().photo;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1535));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$38(this));
                return;
            }
            return;
        }
        TL_photos_deletePhotos req2 = new TL_photos_deletePhotos();
        req2.id.add(photo);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, MessagesController$$Lambda$39.$instance);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$deleteUserPhoto$57$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            User user1 = getUser(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
            if (user1 == null) {
                user1 = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                putUser(user1, false);
            } else {
                UserConfig.getInstance(this.currentAccount).setCurrentUser(user1);
            }
            if (user1 != null) {
                MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(user1.id);
                ArrayList<User> users = new ArrayList();
                users.add(user1);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(users, null, false, true);
                user1.photo = (UserProfilePhoto) response;
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$240(this));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$56$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1535));
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
    }

    static final /* synthetic */ void lambda$deleteUserPhoto$58$MessagesController(TLObject response, TL_error error) {
    }

    public void processLoadedUserPhotos(photos_Photos res, int did, int count, long max_id, boolean fromCache, int classGuid) {
        if (!fromCache) {
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, null, true, true);
            MessagesStorage.getInstance(this.currentAccount).putDialogPhotos(did, res);
        } else if (res == null || res.photos.isEmpty()) {
            loadDialogPhotos(did, count, max_id, false, classGuid);
            return;
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$40(this, res, fromCache, did, count, classGuid));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processLoadedUserPhotos$59$MessagesController(photos_Photos res, boolean fromCache, int did, int count, int classGuid) {
        putUsers(res.users, fromCache);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogPhotosLoaded, Integer.valueOf(did), Integer.valueOf(count), Boolean.valueOf(fromCache), Integer.valueOf(classGuid), res.photos);
    }

    public void uploadAndApplyUserAvatar(FileLocation location) {
        if (location != null) {
            this.uploadingAvatar = FileLoader.getDirectory(4) + "/" + location.volume_id + "_" + location.local_id + ".jpg";
            FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingAvatar, false, true, 16777216);
        }
    }

    public void saveWallpaperToServer(File path, long wallPaperId, long accessHash, boolean isBlurred, boolean isMotion, int backgroundColor, float intesity, boolean install, long taskId) {
        TLObject req;
        long newTaskId;
        Throwable e;
        if (this.uploadingWallpaper != null) {
            File finalPath = new File(ApplicationLoader.getFilesDirFixed(), this.uploadingWallpaperBlurred ? "wallpaper_original.jpg" : "wallpaper.jpg");
            if (path == null || !(path.getAbsolutePath().equals(this.uploadingWallpaper) || path.equals(finalPath))) {
                FileLoader.getInstance(this.currentAccount).cancelUploadFile(this.uploadingWallpaper, false);
                this.uploadingWallpaper = null;
            } else {
                this.uploadingWallpaperMotion = isMotion;
                this.uploadingWallpaperBlurred = isBlurred;
                return;
            }
        }
        if (path != null) {
            this.uploadingWallpaper = path.getAbsolutePath();
            this.uploadingWallpaperMotion = isMotion;
            this.uploadingWallpaperBlurred = isBlurred;
            FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingWallpaper, false, true, 16777216);
        } else if (accessHash != 0) {
            TL_inputWallPaper inputWallPaper = new TL_inputWallPaper();
            inputWallPaper.id = wallPaperId;
            inputWallPaper.access_hash = accessHash;
            TL_wallPaperSettings settings = new TL_wallPaperSettings();
            settings.blur = isBlurred;
            settings.motion = isMotion;
            if (backgroundColor != 0) {
                settings.background_color = backgroundColor;
                settings.flags |= 1;
                settings.intensity = (int) (100.0f * intesity);
                settings.flags |= 8;
            }
            TLObject request;
            if (install) {
                request = new TL_account_installWallPaper();
                request.wallpaper = inputWallPaper;
                request.settings = settings;
                req = request;
            } else {
                request = new TL_account_saveWallPaper();
                request.wallpaper = inputWallPaper;
                request.settings = settings;
                req = request;
            }
            if (taskId != 0) {
                newTaskId = taskId;
            } else {
                NativeByteBuffer data = null;
                try {
                    NativeByteBuffer data2 = new NativeByteBuffer(44);
                    try {
                        data2.writeInt32(12);
                        data2.writeInt64(wallPaperId);
                        data2.writeInt64(accessHash);
                        data2.writeBool(isBlurred);
                        data2.writeBool(isMotion);
                        data2.writeInt32(backgroundColor);
                        data2.writeDouble((double) intesity);
                        data2.writeBool(install);
                        data = data2;
                    } catch (Exception e2) {
                        e = e2;
                        data = data2;
                        FileLog.e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$41(this, newTaskId, install, wallPaperId));
                    }
                } catch (Exception e3) {
                    e = e3;
                    FileLog.e(e);
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$41(this, newTaskId, install, wallPaperId));
                }
                newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$41(this, newTaskId, install, wallPaperId));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$saveWallpaperToServer$60$MessagesController(long newTaskId, boolean install, long wallPaperId, TLObject response, TL_error error) {
        MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        if (!install && this.uploadingWallpaper != null) {
            Editor editor = getGlobalMainSettings().edit();
            editor.putLong("selectedBackground2", wallPaperId);
            editor.commit();
        }
    }

    public void markChannelDialogMessageAsDeleted(ArrayList<Integer> messages, int channelId) {
        MessageObject obj = (MessageObject) this.dialogMessage.get((long) (-channelId));
        if (obj != null) {
            for (int a = 0; a < messages.size(); a++) {
                if (obj.getId() == ((Integer) messages.get(a)).intValue()) {
                    obj.deleted = true;
                    return;
                }
            }
        }
    }

    public void deleteMessages(ArrayList<Integer> messages, ArrayList<Long> randoms, EncryptedChat encryptedChat, int channelId, boolean forAll) {
        deleteMessages(messages, randoms, encryptedChat, channelId, forAll, 0, null);
    }

    public void deleteMessages(ArrayList<Integer> messages, ArrayList<Long> randoms, EncryptedChat encryptedChat, int channelId, boolean forAll, long taskId, TLObject taskRequest) {
        long newTaskId;
        NativeByteBuffer data;
        Throwable e;
        if ((messages != null && !messages.isEmpty()) || taskRequest != null) {
            ArrayList<Integer> toSend = null;
            if (taskId == 0) {
                int a;
                if (channelId == 0) {
                    for (a = 0; a < messages.size(); a++) {
                        MessageObject obj = (MessageObject) this.dialogMessagesByIds.get(((Integer) messages.get(a)).intValue());
                        if (obj != null) {
                            obj.deleted = true;
                        }
                    }
                } else {
                    markChannelDialogMessageAsDeleted(messages, channelId);
                }
                toSend = new ArrayList();
                for (a = 0; a < messages.size(); a++) {
                    Integer mid = (Integer) messages.get(a);
                    if (mid.intValue() > 0) {
                        toSend.add(mid);
                    }
                }
                MessagesStorage.getInstance(this.currentAccount).markMessagesAsDeleted((ArrayList) messages, true, channelId);
                MessagesStorage.getInstance(this.currentAccount).updateDialogsWithDeletedMessages(messages, null, true, channelId);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, messages, Integer.valueOf(channelId));
            }
            NativeByteBuffer data2;
            if (channelId != 0) {
                TL_channels_deleteMessages req;
                if (taskRequest != null) {
                    req = (TL_channels_deleteMessages) taskRequest;
                    newTaskId = taskId;
                } else {
                    req = new TL_channels_deleteMessages();
                    req.id = toSend;
                    req.channel = getInputChannel(channelId);
                    data = null;
                    try {
                        data2 = new NativeByteBuffer(req.getObjectSize() + 8);
                        try {
                            data2.writeInt32(7);
                            data2.writeInt32(channelId);
                            req.serializeToStream(data2);
                            data = data2;
                        } catch (Exception e2) {
                            e = e2;
                            data = data2;
                            FileLog.e(e);
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$42(this, channelId, newTaskId));
                            return;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$42(this, channelId, newTaskId));
                        return;
                    }
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$42(this, channelId, newTaskId));
                return;
            }
            TL_messages_deleteMessages req2;
            if (!(randoms == null || encryptedChat == null || randoms.isEmpty())) {
                SecretChatHelper.getInstance(this.currentAccount).sendMessagesDeleteMessage(encryptedChat, randoms, null);
            }
            if (taskRequest != null) {
                req2 = (TL_messages_deleteMessages) taskRequest;
                newTaskId = taskId;
            } else {
                req2 = new TL_messages_deleteMessages();
                req2.id = toSend;
                req2.revoke = forAll;
                data = null;
                try {
                    data2 = new NativeByteBuffer(req2.getObjectSize() + 8);
                    try {
                        data2.writeInt32(7);
                        data2.writeInt32(channelId);
                        req2.serializeToStream(data2);
                        data = data2;
                    } catch (Exception e4) {
                        e = e4;
                        data = data2;
                        FileLog.e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$43(this, newTaskId));
                    }
                } catch (Exception e5) {
                    e = e5;
                    FileLog.e(e);
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$43(this, newTaskId));
                }
                newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$43(this, newTaskId));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$deleteMessages$61$MessagesController(int channelId, long newTaskId, TLObject response, TL_error error) {
        if (error == null) {
            TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
            processNewChannelDifferenceParams(res.pts, res.pts_count, channelId);
        }
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$deleteMessages$62$MessagesController(long newTaskId, TLObject response, TL_error error) {
        if (error == null) {
            TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
    }

    public void pinMessage(Chat chat, User user, int id, boolean notify) {
        if (chat != null || user != null) {
            TL_messages_updatePinnedMessage req = new TL_messages_updatePinnedMessage();
            req.peer = getInputPeer(chat != null ? -chat.id : user.id);
            req.id = id;
            req.silent = !notify;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$44(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$pinMessage$63$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            processUpdates((Updates) response, false);
        }
    }

    public void deleteUserChannelHistory(Chat chat, User user, int offset) {
        if (offset == 0) {
            MessagesStorage.getInstance(this.currentAccount).deleteUserChannelHistory(chat.id, user.id);
        }
        TL_channels_deleteUserHistory req = new TL_channels_deleteUserHistory();
        req.channel = getInputChannel(chat);
        req.user_id = getInputUser(user);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$45(this, chat, user));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$deleteUserChannelHistory$64$MessagesController(Chat chat, User user, TLObject response, TL_error error) {
        if (error == null) {
            TL_messages_affectedHistory res = (TL_messages_affectedHistory) response;
            if (res.offset > 0) {
                deleteUserChannelHistory(chat, user, res.offset);
            }
            processNewChannelDifferenceParams(res.pts, res.pts_count, chat.id);
        }
    }

    public void deleteDialog(long did, int onlyHistory) {
        deleteDialog(did, onlyHistory, false);
    }

    public void deleteDialog(long did, int onlyHistory, boolean revoke) {
        deleteDialog(did, true, onlyHistory, 0, revoke, null, 0);
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0417  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0289  */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0289  */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x0417  */
    public void deleteDialog(long r32, boolean r34, int r35, int r36, boolean r37, org.telegram.tgnet.TLRPC.InputPeer r38, long r39) {
        /*
        r31 = this;
        r4 = 2;
        r0 = r35;
        if (r0 != r4) goto L_0x0015;
    L_0x0005:
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r32;
        r2 = r35;
        r4.deleteDialog(r0, r2);
    L_0x0014:
        return;
    L_0x0015:
        if (r35 == 0) goto L_0x001c;
    L_0x0017:
        r4 = 3;
        r0 = r35;
        if (r0 != r4) goto L_0x0029;
    L_0x001c:
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.DataQuery.getInstance(r4);
        r0 = r32;
        r4.uninstallShortcut(r0);
    L_0x0029:
        r0 = r32;
        r0 = (int) r0;
        r24 = r0;
        r4 = 32;
        r6 = r32 >> r4;
        r0 = (int) r6;
        r21 = r0;
        r25 = r36;
        if (r34 == 0) goto L_0x01f7;
    L_0x0039:
        r22 = 0;
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r32;
        r2 = r35;
        r4.deleteDialog(r0, r2);
        r0 = r31;
        r4 = r0.dialogs_dict;
        r0 = r32;
        r19 = r4.get(r0);
        r19 = (org.telegram.tgnet.TLRPC.TL_dialog) r19;
        if (r19 == 0) goto L_0x01c5;
    L_0x0058:
        if (r25 != 0) goto L_0x0077;
    L_0x005a:
        r4 = 0;
        r0 = r19;
        r6 = r0.top_message;
        r25 = java.lang.Math.max(r4, r6);
        r0 = r19;
        r4 = r0.read_inbox_max_id;
        r0 = r25;
        r25 = java.lang.Math.max(r0, r4);
        r0 = r19;
        r4 = r0.read_outbox_max_id;
        r0 = r25;
        r25 = java.lang.Math.max(r0, r4);
    L_0x0077:
        if (r35 == 0) goto L_0x007e;
    L_0x0079:
        r4 = 3;
        r0 = r35;
        if (r0 != r4) goto L_0x0323;
    L_0x007e:
        r0 = r31;
        r4 = r0.proxyDialog;
        if (r4 == 0) goto L_0x029e;
    L_0x0084:
        r0 = r31;
        r4 = r0.proxyDialog;
        r6 = r4.id;
        r4 = (r6 > r32 ? 1 : (r6 == r32 ? 0 : -1));
        if (r4 != 0) goto L_0x029e;
    L_0x008e:
        r22 = 1;
    L_0x0090:
        if (r22 == 0) goto L_0x02a2;
    L_0x0092:
        r4 = 1;
        r0 = r31;
        r0.isLeftProxyChannel = r4;
        r0 = r31;
        r4 = r0.proxyDialog;
        r6 = r4.id;
        r10 = 0;
        r4 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r4 >= 0) goto L_0x00bc;
    L_0x00a3:
        r0 = r31;
        r4 = r0.proxyDialog;
        r6 = r4.id;
        r4 = (int) r6;
        r4 = -r4;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r31;
        r16 = r0.getChat(r4);
        if (r16 == 0) goto L_0x00bc;
    L_0x00b7:
        r4 = 1;
        r0 = r16;
        r0.left = r4;
    L_0x00bc:
        r4 = 0;
        r0 = r31;
        r0.sortDialogs(r4);
    L_0x00c2:
        if (r22 != 0) goto L_0x01c5;
    L_0x00c4:
        r0 = r31;
        r4 = r0.dialogMessage;
        r0 = r19;
        r6 = r0.id;
        r29 = r4.get(r6);
        r29 = (org.telegram.messenger.MessageObject) r29;
        r0 = r31;
        r4 = r0.dialogMessage;
        r0 = r19;
        r6 = r0.id;
        r4.remove(r6);
        if (r29 == 0) goto L_0x032a;
    L_0x00df:
        r23 = r29.getId();
        r0 = r31;
        r4 = r0.dialogMessagesByIds;
        r6 = r29.getId();
        r4.remove(r6);
    L_0x00ee:
        if (r29 == 0) goto L_0x0109;
    L_0x00f0:
        r0 = r29;
        r4 = r0.messageOwner;
        r6 = r4.random_id;
        r10 = 0;
        r4 = (r6 > r10 ? 1 : (r6 == r10 ? 0 : -1));
        if (r4 == 0) goto L_0x0109;
    L_0x00fc:
        r0 = r31;
        r4 = r0.dialogMessagesByRandomIds;
        r0 = r29;
        r6 = r0.messageOwner;
        r6 = r6.random_id;
        r4.remove(r6);
    L_0x0109:
        r4 = 1;
        r0 = r35;
        if (r0 != r4) goto L_0x0389;
    L_0x010e:
        if (r24 == 0) goto L_0x0389;
    L_0x0110:
        if (r23 <= 0) goto L_0x0389;
    L_0x0112:
        r26 = new org.telegram.tgnet.TLRPC$TL_messageService;
        r26.<init>();
        r0 = r19;
        r4 = r0.top_message;
        r0 = r26;
        r0.id = r4;
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.getClientUserId();
        r6 = (long) r4;
        r4 = (r6 > r32 ? 1 : (r6 == r32 ? 0 : -1));
        if (r4 != 0) goto L_0x034b;
    L_0x0130:
        r4 = 1;
    L_0x0131:
        r0 = r26;
        r0.out = r4;
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r4 = r4.getClientUserId();
        r0 = r26;
        r0.from_id = r4;
        r0 = r26;
        r4 = r0.flags;
        r4 = r4 | 256;
        r0 = r26;
        r0.flags = r4;
        r4 = new org.telegram.tgnet.TLRPC$TL_messageActionHistoryClear;
        r4.<init>();
        r0 = r26;
        r0.action = r4;
        r0 = r19;
        r4 = r0.last_message_date;
        r0 = r26;
        r0.date = r4;
        r0 = r24;
        r6 = (long) r0;
        r0 = r26;
        r0.dialog_id = r6;
        if (r24 <= 0) goto L_0x034e;
    L_0x0169:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r4.<init>();
        r0 = r26;
        r0.to_id = r4;
        r0 = r26;
        r4 = r0.to_id;
        r0 = r24;
        r4.user_id = r0;
    L_0x017a:
        r27 = new org.telegram.messenger.MessageObject;
        r0 = r31;
        r4 = r0.currentAccount;
        r0 = r31;
        r6 = r0.createdDialogIds;
        r0 = r26;
        r10 = r0.dialog_id;
        r7 = java.lang.Long.valueOf(r10);
        r6 = r6.contains(r7);
        r0 = r27;
        r1 = r26;
        r0.<init>(r4, r1, r6);
        r28 = new java.util.ArrayList;
        r28.<init>();
        r0 = r28;
        r1 = r27;
        r0.add(r1);
        r5 = new java.util.ArrayList;
        r5.<init>();
        r0 = r26;
        r5.add(r0);
        r0 = r31;
        r1 = r32;
        r3 = r28;
        r0.updateInterfaceWithMessages(r1, r3);
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r6 = 0;
        r7 = 1;
        r8 = 0;
        r9 = 0;
        r4.putMessages(r5, r6, r7, r8, r9);
    L_0x01c5:
        if (r22 == 0) goto L_0x0390;
    L_0x01c7:
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r6 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r7 = 1;
        r7 = new java.lang.Object[r7];
        r10 = 0;
        r11 = 1;
        r11 = java.lang.Boolean.valueOf(r11);
        r7[r10] = r11;
        r4.postNotificationName(r6, r7);
    L_0x01df:
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getStorageQueue();
        r6 = new org.telegram.messenger.MessagesController$$Lambda$47;
        r0 = r31;
        r1 = r32;
        r6.<init>(r0, r1);
        r4.postRunnable(r6);
    L_0x01f7:
        r4 = 1;
        r0 = r21;
        if (r0 == r4) goto L_0x0014;
    L_0x01fc:
        r4 = 3;
        r0 = r35;
        if (r0 == r4) goto L_0x0014;
    L_0x0201:
        if (r24 == 0) goto L_0x045f;
    L_0x0203:
        if (r38 != 0) goto L_0x020d;
    L_0x0205:
        r0 = r31;
        r1 = r24;
        r38 = r0.getInputPeer(r1);
    L_0x020d:
        if (r38 == 0) goto L_0x0014;
    L_0x020f:
        r0 = r38;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r4 == 0) goto L_0x0217;
    L_0x0215:
        if (r35 == 0) goto L_0x03cb;
    L_0x0217:
        if (r25 <= 0) goto L_0x022d;
    L_0x0219:
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        r0 = r25;
        if (r0 == r4) goto L_0x022d;
    L_0x0220:
        r0 = r31;
        r4 = r0.deletedHistory;
        r6 = java.lang.Integer.valueOf(r25);
        r0 = r32;
        r4.put(r0, r6);
    L_0x022d:
        r6 = 0;
        r4 = (r39 > r6 ? 1 : (r39 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x03c7;
    L_0x0233:
        r17 = 0;
        r18 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x03c1 }
        r4 = r38.getObjectSize();	 Catch:{ Exception -> 0x03c1 }
        r4 = r4 + 28;
        r0 = r18;
        r0.<init>(r4);	 Catch:{ Exception -> 0x03c1 }
        r4 = 13;
        r0 = r18;
        r0.writeInt32(r4);	 Catch:{ Exception -> 0x048b }
        r0 = r18;
        r1 = r32;
        r0.writeInt64(r1);	 Catch:{ Exception -> 0x048b }
        r0 = r18;
        r1 = r34;
        r0.writeBool(r1);	 Catch:{ Exception -> 0x048b }
        r0 = r18;
        r1 = r35;
        r0.writeInt32(r1);	 Catch:{ Exception -> 0x048b }
        r0 = r18;
        r1 = r25;
        r0.writeInt32(r1);	 Catch:{ Exception -> 0x048b }
        r0 = r18;
        r1 = r37;
        r0.writeBool(r1);	 Catch:{ Exception -> 0x048b }
        r0 = r38;
        r1 = r18;
        r0.serializeToStream(r1);	 Catch:{ Exception -> 0x048b }
        r17 = r18;
    L_0x0275:
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r17;
        r8 = r4.createPendingTask(r0);
    L_0x0283:
        r0 = r38;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel;
        if (r4 == 0) goto L_0x0417;
    L_0x0289:
        if (r35 != 0) goto L_0x03cf;
    L_0x028b:
        r6 = 0;
        r4 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x0014;
    L_0x0291:
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4.removePendingTask(r8);
        goto L_0x0014;
    L_0x029e:
        r22 = 0;
        goto L_0x0090;
    L_0x02a2:
        r0 = r31;
        r4 = r0.dialogs;
        r0 = r19;
        r4.remove(r0);
        r0 = r31;
        r4 = r0.dialogsServerOnly;
        r0 = r19;
        r4 = r4.remove(r0);
        if (r4 == 0) goto L_0x02cb;
    L_0x02b7:
        r4 = org.telegram.messenger.DialogObject.isChannel(r19);
        if (r4 == 0) goto L_0x02cb;
    L_0x02bd:
        r4 = org.telegram.messenger.Utilities.stageQueue;
        r6 = new org.telegram.messenger.MessagesController$$Lambda$46;
        r0 = r31;
        r1 = r32;
        r6.<init>(r0, r1);
        r4.postRunnable(r6);
    L_0x02cb:
        r0 = r31;
        r4 = r0.dialogsCanAddUsers;
        r0 = r19;
        r4.remove(r0);
        r0 = r31;
        r4 = r0.dialogsChannelsOnly;
        r0 = r19;
        r4.remove(r0);
        r0 = r31;
        r4 = r0.dialogsGroupsOnly;
        r0 = r19;
        r4.remove(r0);
        r0 = r31;
        r4 = r0.dialogsUsersOnly;
        r0 = r19;
        r4.remove(r0);
        r0 = r31;
        r4 = r0.dialogsForward;
        r0 = r19;
        r4.remove(r0);
        r0 = r31;
        r4 = r0.dialogs_dict;
        r0 = r32;
        r4.remove(r0);
        r0 = r31;
        r4 = r0.dialogs_read_inbox_max;
        r6 = java.lang.Long.valueOf(r32);
        r4.remove(r6);
        r0 = r31;
        r4 = r0.dialogs_read_outbox_max;
        r6 = java.lang.Long.valueOf(r32);
        r4.remove(r6);
        r0 = r31;
        r4 = r0.nextDialogsCacheOffset;
        r4 = r4 + -1;
        r0 = r31;
        r0.nextDialogsCacheOffset = r4;
        goto L_0x00c2;
    L_0x0323:
        r4 = 0;
        r0 = r19;
        r0.unread_count = r4;
        goto L_0x00c2;
    L_0x032a:
        r0 = r19;
        r0 = r0.top_message;
        r23 = r0;
        r0 = r31;
        r4 = r0.dialogMessagesByIds;
        r0 = r19;
        r6 = r0.top_message;
        r29 = r4.get(r6);
        r29 = (org.telegram.messenger.MessageObject) r29;
        r0 = r31;
        r4 = r0.dialogMessagesByIds;
        r0 = r19;
        r6 = r0.top_message;
        r4.remove(r6);
        goto L_0x00ee;
    L_0x034b:
        r4 = 0;
        goto L_0x0131;
    L_0x034e:
        r0 = r24;
        r4 = -r0;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r31;
        r16 = r0.getChat(r4);
        r4 = org.telegram.messenger.ChatObject.isChannel(r16);
        if (r4 == 0) goto L_0x0375;
    L_0x0361:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChannel;
        r4.<init>();
        r0 = r26;
        r0.to_id = r4;
        r0 = r26;
        r4 = r0.to_id;
        r0 = r24;
        r6 = -r0;
        r4.channel_id = r6;
        goto L_0x017a;
    L_0x0375:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat;
        r4.<init>();
        r0 = r26;
        r0.to_id = r4;
        r0 = r26;
        r4 = r0.to_id;
        r0 = r24;
        r6 = -r0;
        r4.chat_id = r6;
        goto L_0x017a;
    L_0x0389:
        r4 = 0;
        r0 = r19;
        r0.top_message = r4;
        goto L_0x01c5;
    L_0x0390:
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r6 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r7 = 0;
        r7 = new java.lang.Object[r7];
        r4.postNotificationName(r6, r7);
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.NotificationCenter.getInstance(r4);
        r6 = org.telegram.messenger.NotificationCenter.removeAllMessagesFromDialog;
        r7 = 2;
        r7 = new java.lang.Object[r7];
        r10 = 0;
        r11 = java.lang.Long.valueOf(r32);
        r7[r10] = r11;
        r10 = 1;
        r11 = 0;
        r11 = java.lang.Boolean.valueOf(r11);
        r7[r10] = r11;
        r4.postNotificationName(r6, r7);
        goto L_0x01df;
    L_0x03c1:
        r20 = move-exception;
    L_0x03c2:
        org.telegram.messenger.FileLog.e(r20);
        goto L_0x0275;
    L_0x03c7:
        r8 = r39;
        goto L_0x0283;
    L_0x03cb:
        r8 = r39;
        goto L_0x0283;
    L_0x03cf:
        r30 = new org.telegram.tgnet.TLRPC$TL_channels_deleteHistory;
        r30.<init>();
        r4 = new org.telegram.tgnet.TLRPC$TL_inputChannel;
        r4.<init>();
        r0 = r30;
        r0.channel = r4;
        r0 = r30;
        r4 = r0.channel;
        r0 = r38;
        r6 = r0.channel_id;
        r4.channel_id = r6;
        r0 = r30;
        r4 = r0.channel;
        r0 = r38;
        r6 = r0.access_hash;
        r4.access_hash = r6;
        if (r25 <= 0) goto L_0x0413;
    L_0x03f3:
        r0 = r25;
        r1 = r30;
        r1.max_id = r0;
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
        r6 = new org.telegram.messenger.MessagesController$$Lambda$48;
        r7 = r31;
        r10 = r32;
        r6.<init>(r7, r8, r10);
        r7 = 64;
        r0 = r30;
        r4.sendRequest(r0, r6, r7);
        goto L_0x0014;
    L_0x0413:
        r25 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        goto L_0x03f3;
    L_0x0417:
        r30 = new org.telegram.tgnet.TLRPC$TL_messages_deleteHistory;
        r30.<init>();
        r0 = r38;
        r1 = r30;
        r1.peer = r0;
        if (r35 != 0) goto L_0x045a;
    L_0x0424:
        r4 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
    L_0x0427:
        r0 = r30;
        r0.max_id = r4;
        if (r35 == 0) goto L_0x045d;
    L_0x042d:
        r4 = 1;
    L_0x042e:
        r0 = r30;
        r0.just_clear = r4;
        r0 = r37;
        r1 = r30;
        r1.revoke = r0;
        r13 = r25;
        r15 = r38;
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
        r6 = new org.telegram.messenger.MessagesController$$Lambda$49;
        r7 = r31;
        r10 = r32;
        r12 = r35;
        r14 = r37;
        r6.<init>(r7, r8, r10, r12, r13, r14, r15);
        r7 = 64;
        r0 = r30;
        r4.sendRequest(r0, r6, r7);
        goto L_0x0014;
    L_0x045a:
        r4 = r25;
        goto L_0x0427;
    L_0x045d:
        r4 = 0;
        goto L_0x042e;
    L_0x045f:
        r4 = 1;
        r0 = r35;
        if (r0 != r4) goto L_0x047c;
    L_0x0464:
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.SecretChatHelper.getInstance(r4);
        r6 = java.lang.Integer.valueOf(r21);
        r0 = r31;
        r6 = r0.getEncryptedChat(r6);
        r7 = 0;
        r4.sendClearHistoryMessage(r6, r7);
        goto L_0x0014;
    L_0x047c:
        r0 = r31;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.SecretChatHelper.getInstance(r4);
        r0 = r21;
        r4.declineSecretChat(r0);
        goto L_0x0014;
    L_0x048b:
        r20 = move-exception;
        r17 = r18;
        goto L_0x03c2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.deleteDialog(long, boolean, int, int, boolean, org.telegram.tgnet.TLRPC$InputPeer, long):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$deleteDialog$65$MessagesController(long did) {
        this.channelsPts.delete(-((int) did));
        this.shortPollChannels.delete(-((int) did));
        this.needShortPollChannels.delete(-((int) did));
        this.shortPollOnlines.delete(-((int) did));
        this.needShortPollOnlines.delete(-((int) did));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$deleteDialog$67$MessagesController(long did) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$239(this, did));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$66$MessagesController(long did) {
        NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(did);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$deleteDialog$69$MessagesController(long newTaskId, long did, TLObject response, TL_error error) {
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$238(this, did));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$68$MessagesController(long did) {
        this.deletedHistory.remove(did);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$deleteDialog$70$MessagesController(long newTaskId, long did, int onlyHistory, int max_id_delete_final, boolean revoke, InputPeer peerFinal, TLObject response, TL_error error) {
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
        if (error == null) {
            TL_messages_affectedHistory res = (TL_messages_affectedHistory) response;
            if (res.offset > 0) {
                deleteDialog(did, false, onlyHistory, max_id_delete_final, revoke, peerFinal, 0);
            }
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
            MessagesStorage.getInstance(this.currentAccount).onDeleteQueryComplete(did);
        }
    }

    public void saveGif(Object parentObject, Document document) {
        if (parentObject != null && MessageObject.isGifDocument(document)) {
            TL_messages_saveGif req = new TL_messages_saveGif();
            req.id = new TL_inputDocument();
            req.id.id = document.id;
            req.id.access_hash = document.access_hash;
            req.id.file_reference = document.file_reference;
            if (req.id.file_reference == null) {
                req.id.file_reference = new byte[0];
            }
            req.unsave = false;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$50(this, parentObject, req));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$saveGif$71$MessagesController(Object parentObject, TL_messages_saveGif req, TLObject response, TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text) && parentObject != null) {
            FileRefController.getInstance(this.currentAccount).requestReference(parentObject, req);
        }
    }

    public void saveRecentSticker(Object parentObject, Document document, boolean asMask) {
        if (parentObject != null && document != null) {
            TL_messages_saveRecentSticker req = new TL_messages_saveRecentSticker();
            req.id = new TL_inputDocument();
            req.id.id = document.id;
            req.id.access_hash = document.access_hash;
            req.id.file_reference = document.file_reference;
            if (req.id.file_reference == null) {
                req.id.file_reference = new byte[0];
            }
            req.unsave = false;
            req.attached = asMask;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$51(this, parentObject, req));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$saveRecentSticker$72$MessagesController(Object parentObject, TL_messages_saveRecentSticker req, TLObject response, TL_error error) {
        if (error != null && FileRefController.isFileRefError(error.text) && parentObject != null) {
            FileRefController.getInstance(this.currentAccount).requestReference(parentObject, req);
        }
    }

    public void loadChannelParticipants(Integer chat_id) {
        if (!this.loadingFullParticipants.contains(chat_id) && !this.loadedFullParticipants.contains(chat_id)) {
            this.loadingFullParticipants.add(chat_id);
            TL_channels_getParticipants req = new TL_channels_getParticipants();
            req.channel = getInputChannel(chat_id.intValue());
            req.filter = new TL_channelParticipantsRecent();
            req.offset = 0;
            req.limit = 32;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$52(this, chat_id));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadChannelParticipants$74$MessagesController(Integer chat_id, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$237(this, error, response, chat_id));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$73$MessagesController(TL_error error, TLObject response, Integer chat_id) {
        if (error == null) {
            TL_channels_channelParticipants res = (TL_channels_channelParticipants) response;
            putUsers(res.users, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, null, true, true);
            MessagesStorage.getInstance(this.currentAccount).updateChannelUsers(chat_id.intValue(), res.participants);
            this.loadedFullParticipants.add(chat_id);
        }
        this.loadingFullParticipants.remove(chat_id);
    }

    public void loadChatInfo(int chat_id, CountDownLatch countDownLatch, boolean force) {
        MessagesStorage.getInstance(this.currentAccount).loadChatInfo(chat_id, countDownLatch, force, false);
    }

    public void processChatInfo(int chat_id, ChatFull info, ArrayList<User> usersArr, boolean fromCache, boolean force, boolean byChannelUsers, MessageObject pinnedMessageObject) {
        if (fromCache && chat_id > 0 && !byChannelUsers) {
            loadFullChat(chat_id, 0, force);
        }
        if (info != null) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$53(this, usersArr, fromCache, info, byChannelUsers, pinnedMessageObject));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processChatInfo$75$MessagesController(ArrayList usersArr, boolean fromCache, ChatFull info, boolean byChannelUsers, MessageObject pinnedMessageObject) {
        putUsers(usersArr, fromCache);
        if (info.stickerset != null) {
            DataQuery.getInstance(this.currentAccount).getGroupStickerSetById(info.stickerset);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, info, Integer.valueOf(0), Boolean.valueOf(byChannelUsers), pinnedMessageObject);
    }

    public void loadUserInfo(User user, boolean force, int classGuid) {
        MessagesStorage.getInstance(this.currentAccount).loadUserInfo(user, force, classGuid);
    }

    public void processUserInfo(User user, TL_userFull info, boolean fromCache, boolean force, MessageObject pinnedMessageObject, int classGuid) {
        if (fromCache) {
            loadFullUser(user, classGuid, force);
        }
        if (info != null) {
            if (this.fullUsers.get(user.id) == null) {
                this.fullUsers.put(user.id, info);
            }
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$54(this, user, info, pinnedMessageObject));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUserInfo$76$MessagesController(User user, TL_userFull info, MessageObject pinnedMessageObject) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoad, Integer.valueOf(user.id), info, pinnedMessageObject);
    }

    public void updateTimerProc() {
        int a;
        int key;
        long currentTime = System.currentTimeMillis();
        checkDeletingTask(false);
        checkReadTasks();
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            TL_account_updateStatus req;
            if (ConnectionsManager.getInstance(this.currentAccount).getPauseTime() == 0 && ApplicationLoader.isScreenOn && !ApplicationLoader.mainInterfacePausedStageQueue) {
                if (ApplicationLoader.mainInterfacePausedStageQueueTime != 0 && Math.abs(ApplicationLoader.mainInterfacePausedStageQueueTime - System.currentTimeMillis()) > 1000 && this.statusSettingState != 1 && (this.lastStatusUpdateTime == 0 || Math.abs(System.currentTimeMillis() - this.lastStatusUpdateTime) >= 55000 || this.offlineSent)) {
                    this.statusSettingState = 1;
                    if (this.statusRequest != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.statusRequest, true);
                    }
                    req = new TL_account_updateStatus();
                    req.offline = false;
                    this.statusRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$55(this));
                }
            } else if (!(this.statusSettingState == 2 || this.offlineSent || Math.abs(System.currentTimeMillis() - ConnectionsManager.getInstance(this.currentAccount).getPauseTime()) < 2000)) {
                this.statusSettingState = 2;
                if (this.statusRequest != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.statusRequest, true);
                }
                req = new TL_account_updateStatus();
                req.offline = true;
                this.statusRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$56(this));
            }
            if (this.updatesQueueChannels.size() != 0) {
                for (a = 0; a < this.updatesQueueChannels.size(); a++) {
                    key = this.updatesQueueChannels.keyAt(a);
                    if (1500 + this.updatesStartWaitTimeChannels.valueAt(a) < currentTime) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("QUEUE CHANNEL " + key + " UPDATES WAIT TIMEOUT - CHECK QUEUE");
                        }
                        processChannelsUpdatesQueue(key, 0);
                    }
                }
            }
            a = 0;
            while (a < 3) {
                if (getUpdatesStartTime(a) != 0 && getUpdatesStartTime(a) + 1500 < currentTime) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d(a + " QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
                    }
                    processUpdatesQueue(a, 0);
                }
                a++;
            }
        }
        if (Math.abs(System.currentTimeMillis() - this.lastViewsCheckTime) >= 5000) {
            this.lastViewsCheckTime = System.currentTimeMillis();
            if (this.channelViewsToSend.size() != 0) {
                a = 0;
                while (a < this.channelViewsToSend.size()) {
                    key = this.channelViewsToSend.keyAt(a);
                    TL_messages_getMessagesViews req2 = new TL_messages_getMessagesViews();
                    req2.peer = getInputPeer(key);
                    req2.id = (ArrayList) this.channelViewsToSend.valueAt(a);
                    req2.increment = a == 0;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$57(this, key, req2));
                    a++;
                }
                this.channelViewsToSend.clear();
            }
            if (this.pollsToCheckSize > 0) {
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$58(this));
            }
        }
        if (!this.onlinePrivacy.isEmpty()) {
            ArrayList<Integer> toRemove = null;
            int currentServerTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            for (Entry<Integer, Integer> entry : this.onlinePrivacy.entrySet()) {
                if (((Integer) entry.getValue()).intValue() < currentServerTime - 30) {
                    if (toRemove == null) {
                        toRemove = new ArrayList();
                    }
                    toRemove.add(entry.getKey());
                }
            }
            if (toRemove != null) {
                Iterator it = toRemove.iterator();
                while (it.hasNext()) {
                    this.onlinePrivacy.remove((Integer) it.next());
                }
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$59(this));
            }
        }
        if (this.shortPollChannels.size() != 0) {
            a = 0;
            while (a < this.shortPollChannels.size()) {
                key = this.shortPollChannels.keyAt(a);
                if (((long) this.shortPollChannels.valueAt(a)) < System.currentTimeMillis() / 1000) {
                    this.shortPollChannels.delete(key);
                    a--;
                    if (this.needShortPollChannels.indexOfKey(key) >= 0) {
                        getChannelDifference(key);
                    }
                }
                a++;
            }
        }
        if (this.shortPollOnlines.size() != 0) {
            long time = SystemClock.uptimeMillis() / 1000;
            a = 0;
            while (a < this.shortPollOnlines.size()) {
                key = this.shortPollOnlines.keyAt(a);
                if (((long) this.shortPollOnlines.valueAt(a)) < time) {
                    if (this.needShortPollChannels.indexOfKey(key) >= 0) {
                        this.shortPollOnlines.put(key, (int) (300 + time));
                    } else {
                        this.shortPollOnlines.delete(key);
                        a--;
                    }
                    TL_messages_getOnlines req3 = new TL_messages_getOnlines();
                    req3.peer = getInputPeer(-key);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req3, new MessagesController$$Lambda$60(this, key));
                }
                a++;
            }
        }
        if (!(this.printingUsers.isEmpty() && this.lastPrintingStringCount == this.printingUsers.size())) {
            boolean updated = false;
            ArrayList<Long> keys = new ArrayList(this.printingUsers.keySet());
            int b = 0;
            while (b < keys.size()) {
                long key2 = ((Long) keys.get(b)).longValue();
                ArrayList<PrintingUser> arr = (ArrayList) this.printingUsers.get(Long.valueOf(key2));
                if (arr != null) {
                    a = 0;
                    while (a < arr.size()) {
                        int timeToRemove;
                        PrintingUser user = (PrintingUser) arr.get(a);
                        if (user.action instanceof TL_sendMessageGamePlayAction) {
                            timeToRemove = 30000;
                        } else {
                            timeToRemove = 5900;
                        }
                        if (user.lastTime + ((long) timeToRemove) < currentTime) {
                            updated = true;
                            arr.remove(user);
                            a--;
                        }
                        a++;
                    }
                }
                if (arr == null || arr.isEmpty()) {
                    this.printingUsers.remove(Long.valueOf(key2));
                    keys.remove(b);
                    b--;
                }
                b++;
            }
            updatePrintingStrings();
            if (updated) {
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$61(this));
            }
        }
        if (Theme.selectedAutoNightType == 1 && Math.abs(currentTime - lastThemeCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.themeCheckRunnable);
            lastThemeCheckTime = currentTime;
        }
        if (UserConfig.getInstance(this.currentAccount).savedPasswordHash != null && Math.abs(currentTime - lastPasswordCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.passwordCheckRunnable);
            lastPasswordCheckTime = currentTime;
        }
        if (this.lastPushRegisterSendTime != 0 && Math.abs(SystemClock.elapsedRealtime() - this.lastPushRegisterSendTime) >= 10800000) {
            GcmPushListenerService.sendRegistrationToServer(SharedConfig.pushString);
        }
        LocationController.getInstance(this.currentAccount).update();
        checkProxyInfoInternal(false);
        checkTosUpdate();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateTimerProc$77$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            this.lastStatusUpdateTime = System.currentTimeMillis();
            this.offlineSent = false;
            this.statusSettingState = 0;
        } else if (this.lastStatusUpdateTime != 0) {
            this.lastStatusUpdateTime += 5000;
        }
        this.statusRequest = 0;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateTimerProc$78$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            this.offlineSent = true;
        } else if (this.lastStatusUpdateTime != 0) {
            this.lastStatusUpdateTime += 5000;
        }
        this.statusRequest = 0;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateTimerProc$80$MessagesController(int key, TL_messages_getMessagesViews req, TLObject response, TL_error error) {
        if (response != null) {
            Vector vector = (Vector) response;
            SparseArray<SparseIntArray> channelViews = new SparseArray();
            SparseIntArray array = (SparseIntArray) channelViews.get(key);
            if (array == null) {
                array = new SparseIntArray();
                channelViews.put(key, array);
            }
            int a1 = 0;
            while (a1 < req.id.size() && a1 < vector.objects.size()) {
                array.put(((Integer) req.id.get(a1)).intValue(), ((Integer) vector.objects.get(a1)).intValue());
                a1++;
            }
            MessagesStorage.getInstance(this.currentAccount).putChannelViews(channelViews, req.peer instanceof TL_inputPeerChannel);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$236(this, channelViews));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$79$MessagesController(SparseArray channelViews) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didUpdatedMessagesViews, channelViews);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateTimerProc$82$MessagesController() {
        long time = SystemClock.uptimeMillis();
        int a = 0;
        int N = this.pollsToCheck.size();
        while (a < N) {
            SparseArray<MessageObject> array = (SparseArray) this.pollsToCheck.valueAt(a);
            if (array != null) {
                int b = 0;
                int N2 = array.size();
                while (b < N2) {
                    MessageObject messageObject = (MessageObject) array.valueAt(b);
                    if (Math.abs(time - messageObject.pollLastCheckTime) >= 30000) {
                        messageObject.pollLastCheckTime = time;
                        TL_messages_getPollResults req = new TL_messages_getPollResults();
                        req.peer = getInputPeer((int) messageObject.getDialogId());
                        req.msg_id = messageObject.getId();
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$235(this));
                    } else if (!messageObject.pollVisibleOnScreen) {
                        array.remove(messageObject.getId());
                        N2--;
                        b--;
                    }
                    b++;
                }
                if (array.size() == 0) {
                    this.pollsToCheck.remove(this.pollsToCheck.keyAt(a));
                    N--;
                    a--;
                }
            }
            a++;
        }
        this.pollsToCheckSize = this.pollsToCheck.size();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$81$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            processUpdates((Updates) response, false);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateTimerProc$83$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateTimerProc$85$MessagesController(int key, TLObject response, TL_error error) {
        if (response != null) {
            TL_chatOnlines res = (TL_chatOnlines) response;
            MessagesStorage.getInstance(this.currentAccount).updateChatOnlineCount(key, res.onlines);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$234(this, key, res));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$84$MessagesController(int key, TL_chatOnlines res) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatOnlineCountDidLoad, Integer.valueOf(key), Integer.valueOf(res.onlines));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateTimerProc$86$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
    }

    private void checkTosUpdate() {
        if (this.nextTosCheckTime <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() && !this.checkingTosUpdate && UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            this.checkingTosUpdate = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_help_getTermsOfServiceUpdate(), new MessagesController$$Lambda$62(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkTosUpdate$88$MessagesController(TLObject response, TL_error error) {
        this.checkingTosUpdate = false;
        if (response instanceof TL_help_termsOfServiceUpdateEmpty) {
            this.nextTosCheckTime = ((TL_help_termsOfServiceUpdateEmpty) response).expires;
        } else if (response instanceof TL_help_termsOfServiceUpdate) {
            TL_help_termsOfServiceUpdate res = (TL_help_termsOfServiceUpdate) response;
            this.nextTosCheckTime = res.expires;
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$233(this, res));
        } else {
            this.nextTosCheckTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 3600;
        }
        this.notificationsPreferences.edit().putInt("nextTosCheckTime", this.nextTosCheckTime).commit();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$87$MessagesController(TL_help_termsOfServiceUpdate res) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(4), res.terms_of_service);
    }

    public void checkProxyInfo(boolean reset) {
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$63(this, reset));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkProxyInfo$89$MessagesController(boolean reset) {
        checkProxyInfoInternal(reset);
    }

    private void checkProxyInfoInternal(boolean reset) {
        if (reset && this.checkingProxyInfo) {
            this.checkingProxyInfo = false;
        }
        if ((reset || this.nextProxyInfoCheckTime <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) && !this.checkingProxyInfo) {
            if (this.checkingProxyInfoRequestId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkingProxyInfoRequestId, true);
                this.checkingProxyInfoRequestId = 0;
            }
            SharedPreferences preferences = getGlobalMainSettings();
            boolean enabled = preferences.getBoolean("proxy_enabled", false);
            String proxyAddress = preferences.getString("proxy_ip", "");
            String proxySecret = preferences.getString("proxy_secret", "");
            int removeCurrent = 0;
            if (!(this.proxyDialogId == 0 || this.proxyDialogAddress == null || this.proxyDialogAddress.equals(proxyAddress + proxySecret))) {
                removeCurrent = 1;
            }
            if (!enabled || TextUtils.isEmpty(proxyAddress) || TextUtils.isEmpty(proxySecret)) {
                removeCurrent = 2;
            } else {
                this.checkingProxyInfo = true;
                this.checkingProxyInfoRequestId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_help_getProxyData(), new MessagesController$$Lambda$64(this, proxyAddress, proxySecret));
            }
            if (removeCurrent != 0) {
                this.proxyDialogId = 0;
                this.proxyDialogAddress = null;
                getGlobalMainSettings().edit().putLong("proxy_dialog", this.proxyDialogId).remove("proxyDialogAddress").commit();
                this.nextProxyInfoCheckTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 3600;
                if (removeCurrent == 2) {
                    this.checkingProxyInfo = false;
                    if (this.checkingProxyInfoRequestId != 0) {
                        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkingProxyInfoRequestId, true);
                        this.checkingProxyInfoRequestId = 0;
                    }
                }
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$65(this));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkProxyInfoInternal$95$MessagesController(String proxyAddress, String proxySecret, TLObject response, TL_error error) {
        if (this.checkingProxyInfoRequestId != 0) {
            boolean noDialog = false;
            if (response instanceof TL_help_proxyDataEmpty) {
                this.nextProxyInfoCheckTime = ((TL_help_proxyDataEmpty) response).expires;
                noDialog = true;
            } else if (response instanceof TL_help_proxyDataPromo) {
                long did;
                TL_help_proxyDataPromo res = (TL_help_proxyDataPromo) response;
                int a;
                Chat chat;
                if (res.peer.user_id != 0) {
                    did = (long) res.peer.user_id;
                } else if (res.peer.chat_id != 0) {
                    did = (long) (-res.peer.chat_id);
                    a = 0;
                    while (a < res.chats.size()) {
                        chat = (Chat) res.chats.get(a);
                        if (chat.id != res.peer.chat_id) {
                            a++;
                        } else if (chat.kicked || chat.restricted) {
                            noDialog = true;
                        }
                    }
                } else {
                    did = (long) (-res.peer.channel_id);
                    a = 0;
                    while (a < res.chats.size()) {
                        chat = (Chat) res.chats.get(a);
                        if (chat.id != res.peer.channel_id) {
                            a++;
                        } else if (chat.kicked || chat.restricted) {
                            noDialog = true;
                        }
                    }
                }
                this.proxyDialogId = did;
                this.proxyDialogAddress = proxyAddress + proxySecret;
                getGlobalMainSettings().edit().putLong("proxy_dialog", this.proxyDialogId).putString("proxyDialogAddress", this.proxyDialogAddress).commit();
                this.nextProxyInfoCheckTime = res.expires;
                if (!noDialog) {
                    AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$228(this, did, res));
                }
            } else {
                this.nextProxyInfoCheckTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 3600;
                noDialog = true;
            }
            if (noDialog) {
                this.proxyDialogId = 0;
                getGlobalMainSettings().edit().putLong("proxy_dialog", this.proxyDialogId).remove("proxyDialogAddress").commit();
                this.checkingProxyInfoRequestId = 0;
                this.checkingProxyInfo = false;
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$229(this));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$93$MessagesController(long did, TL_help_proxyDataPromo res) {
        this.proxyDialog = (TL_dialog) this.dialogs_dict.get(did);
        if (this.proxyDialog != null) {
            this.checkingProxyInfo = false;
            sortDialogs(null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
            return;
        }
        int a;
        SparseArray<User> usersDict = new SparseArray();
        SparseArray<Chat> chatsDict = new SparseArray();
        for (a = 0; a < res.users.size(); a++) {
            User u = (User) res.users.get(a);
            usersDict.put(u.id, u);
        }
        for (a = 0; a < res.chats.size(); a++) {
            Chat c = (Chat) res.chats.get(a);
            chatsDict.put(c.id, c);
        }
        TL_messages_getPeerDialogs req1 = new TL_messages_getPeerDialogs();
        TL_inputDialogPeer peer = new TL_inputDialogPeer();
        Chat chat;
        if (res.peer.user_id != 0) {
            peer.peer = new TL_inputPeerUser();
            peer.peer.user_id = res.peer.user_id;
            User user = (User) usersDict.get(res.peer.user_id);
            if (user != null) {
                peer.peer.access_hash = user.access_hash;
            }
        } else if (res.peer.chat_id != 0) {
            peer.peer = new TL_inputPeerChat();
            peer.peer.chat_id = res.peer.chat_id;
            chat = (Chat) chatsDict.get(res.peer.chat_id);
            if (chat != null) {
                peer.peer.access_hash = chat.access_hash;
            }
        } else {
            peer.peer = new TL_inputPeerChannel();
            peer.peer.channel_id = res.peer.channel_id;
            chat = (Chat) chatsDict.get(res.peer.channel_id);
            if (chat != null) {
                peer.peer.access_hash = chat.access_hash;
            }
        }
        req1.peers.add(peer);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req1, new MessagesController$$Lambda$230(this, res, did));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$92$MessagesController(TL_help_proxyDataPromo res, long did, TLObject response1, TL_error error1) {
        if (this.checkingProxyInfoRequestId != 0) {
            this.checkingProxyInfoRequestId = 0;
            TL_messages_peerDialogs res2 = (TL_messages_peerDialogs) response1;
            if (res2 == null || res2.dialogs.isEmpty()) {
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$232(this));
            } else {
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                TL_messages_dialogs dialogs = new TL_messages_dialogs();
                dialogs.chats = res2.chats;
                dialogs.users = res2.users;
                dialogs.dialogs = res2.dialogs;
                dialogs.messages = res2.messages;
                MessagesStorage.getInstance(this.currentAccount).putDialogs(dialogs, 2);
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$231(this, res, res2, did));
            }
            this.checkingProxyInfo = false;
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$90$MessagesController(TL_help_proxyDataPromo res, TL_messages_peerDialogs res2, long did) {
        putUsers(res.users, false);
        putChats(res.chats, false);
        putUsers(res2.users, false);
        putChats(res2.chats, false);
        this.proxyDialog = (TL_dialog) res2.dialogs.get(0);
        this.proxyDialog.id = did;
        if (DialogObject.isChannel(this.proxyDialog)) {
            this.channelsPts.put(-((int) this.proxyDialog.id), this.proxyDialog.pts);
        }
        Integer value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(this.proxyDialog.id));
        if (value == null) {
            value = Integer.valueOf(0);
        }
        this.dialogs_read_inbox_max.put(Long.valueOf(this.proxyDialog.id), Integer.valueOf(Math.max(value.intValue(), this.proxyDialog.read_inbox_max_id)));
        value = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(this.proxyDialog.id));
        if (value == null) {
            value = Integer.valueOf(0);
        }
        this.dialogs_read_outbox_max.put(Long.valueOf(this.proxyDialog.id), Integer.valueOf(Math.max(value.intValue(), this.proxyDialog.read_outbox_max_id)));
        this.dialogs_dict.put(did, this.proxyDialog);
        if (!res2.messages.isEmpty()) {
            int a;
            SparseArray usersDict1 = new SparseArray();
            SparseArray chatsDict1 = new SparseArray();
            for (a = 0; a < res2.users.size(); a++) {
                User u = (User) res2.users.get(a);
                usersDict1.put(u.id, u);
            }
            for (a = 0; a < res2.chats.size(); a++) {
                Chat c = (Chat) res2.chats.get(a);
                chatsDict1.put(c.id, c);
            }
            MessageObject messageObject = new MessageObject(this.currentAccount, (Message) res2.messages.get(0), usersDict1, chatsDict1, false);
            this.dialogMessage.put(did, messageObject);
            if (this.proxyDialog.last_message_date == 0) {
                this.proxyDialog.last_message_date = messageObject.messageOwner.date;
            }
        }
        sortDialogs(null);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$91$MessagesController() {
        if (this.proxyDialog != null) {
            if (this.proxyDialog.id < 0) {
                Chat chat = getChat(Integer.valueOf(-((int) this.proxyDialog.id)));
                if (chat == null || chat.left || chat.kicked || chat.restricted) {
                    this.dialogs_dict.remove(this.proxyDialog.id);
                    this.dialogs.remove(this.proxyDialog);
                }
            }
            this.proxyDialog = null;
            sortDialogs(null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$94$MessagesController() {
        if (this.proxyDialog != null) {
            if (this.proxyDialog.id < 0) {
                Chat chat = getChat(Integer.valueOf(-((int) this.proxyDialog.id)));
                if (chat == null || chat.left || chat.kicked || chat.restricted) {
                    this.dialogs_dict.remove(this.proxyDialog.id);
                    this.dialogs.remove(this.proxyDialog);
                }
            }
            this.proxyDialog = null;
            sortDialogs(null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkProxyInfoInternal$96$MessagesController() {
        if (this.proxyDialog != null) {
            if (this.proxyDialog.id < 0) {
                Chat chat = getChat(Integer.valueOf(-((int) this.proxyDialog.id)));
                if (chat == null || chat.left || chat.kicked || chat.restricted) {
                    this.dialogs_dict.remove(this.proxyDialog.id);
                    this.dialogs.remove(this.proxyDialog);
                }
            }
            this.proxyDialog = null;
            sortDialogs(null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
    }

    public boolean isProxyDialog(long did) {
        return this.proxyDialog != null && this.proxyDialog.id == did && this.isLeftProxyChannel;
    }

    private String getUserNameForTyping(User user) {
        if (user == null) {
            return "";
        }
        if (user.first_name != null && user.first_name.length() > 0) {
            return user.first_name;
        }
        if (user.last_name == null || user.last_name.length() <= 0) {
            return "";
        }
        return user.last_name;
    }

    private void updatePrintingStrings() {
        LongSparseArray<CharSequence> newPrintingStrings = new LongSparseArray();
        LongSparseArray<Integer> newPrintingStringsTypes = new LongSparseArray();
        for (Entry<Long, ArrayList<PrintingUser>> entry : this.printingUsers.entrySet()) {
            long key = ((Long) entry.getKey()).longValue();
            ArrayList<PrintingUser> arr = (ArrayList) entry.getValue();
            int lower_id = (int) key;
            User user;
            if (lower_id > 0 || lower_id == 0 || arr.size() == 1) {
                PrintingUser pu = (PrintingUser) arr.get(0);
                if (getUser(Integer.valueOf(pu.userId)) != null) {
                    if (pu.action instanceof TL_sendMessageRecordAudioAction) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsRecordingAudio", NUM, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("RecordingAudio", NUM));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(1));
                    } else if ((pu.action instanceof TL_sendMessageRecordRoundAction) || (pu.action instanceof TL_sendMessageUploadRoundAction)) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsRecordingRound", NUM, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("RecordingRound", NUM));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(4));
                    } else if (pu.action instanceof TL_sendMessageUploadAudioAction) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsSendingAudio", NUM, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("SendingAudio", NUM));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(2));
                    } else if ((pu.action instanceof TL_sendMessageUploadVideoAction) || (pu.action instanceof TL_sendMessageRecordVideoAction)) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsSendingVideo", NUM, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("SendingVideoStatus", NUM));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(2));
                    } else if (pu.action instanceof TL_sendMessageUploadDocumentAction) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsSendingFile", NUM, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("SendingFile", NUM));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(2));
                    } else if (pu.action instanceof TL_sendMessageUploadPhotoAction) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsSendingPhoto", NUM, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("SendingPhoto", NUM));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(2));
                    } else if (pu.action instanceof TL_sendMessageGamePlayAction) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsSendingGame", NUM, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("SendingGame", NUM));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(3));
                    } else {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsTypingGroup", NUM, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("Typing", NUM));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(0));
                    }
                }
            } else {
                int count = 0;
                StringBuilder label = new StringBuilder();
                Iterator it = arr.iterator();
                while (it.hasNext()) {
                    user = getUser(Integer.valueOf(((PrintingUser) it.next()).userId));
                    if (user != null) {
                        if (label.length() != 0) {
                            label.append(", ");
                        }
                        label.append(getUserNameForTyping(user));
                        count++;
                    }
                    if (count == 2) {
                        break;
                    }
                }
                if (label.length() != 0) {
                    if (count == 1) {
                        newPrintingStrings.put(key, LocaleController.formatString("IsTypingGroup", NUM, label.toString()));
                    } else if (arr.size() > 2) {
                        try {
                            newPrintingStrings.put(key, String.format(LocaleController.getPluralString("AndMoreTypingGroup", arr.size() - 2), new Object[]{label.toString(), Integer.valueOf(arr.size() - 2)}));
                        } catch (Exception e) {
                            newPrintingStrings.put(key, "LOC_ERR: AndMoreTypingGroup");
                        }
                    } else {
                        newPrintingStrings.put(key, LocaleController.formatString("AreTypingGroup", NUM, label.toString()));
                    }
                    newPrintingStringsTypes.put(key, Integer.valueOf(0));
                }
            }
        }
        this.lastPrintingStringCount = newPrintingStrings.size();
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$66(this, newPrintingStrings, newPrintingStringsTypes));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updatePrintingStrings$97$MessagesController(LongSparseArray newPrintingStrings, LongSparseArray newPrintingStringsTypes) {
        this.printingStrings = newPrintingStrings;
        this.printingStringsTypes = newPrintingStringsTypes;
    }

    public void cancelTyping(int action, long dialog_id) {
        LongSparseArray<Boolean> typings = (LongSparseArray) this.sendingTypings.get(action);
        if (typings != null) {
            typings.remove(dialog_id);
        }
    }

    public void sendTyping(long dialog_id, int action, int classGuid) {
        if (dialog_id != 0) {
            LongSparseArray<Boolean> typings = (LongSparseArray) this.sendingTypings.get(action);
            if (typings == null || typings.get(dialog_id) == null) {
                if (typings == null) {
                    typings = new LongSparseArray();
                    this.sendingTypings.put(action, typings);
                }
                int lower_part = (int) dialog_id;
                int high_id = (int) (dialog_id >> 32);
                int reqId;
                if (lower_part != 0) {
                    if (high_id != 1) {
                        TL_messages_setTyping req = new TL_messages_setTyping();
                        req.peer = getInputPeer(lower_part);
                        if (req.peer instanceof TL_inputPeerChannel) {
                            Chat chat = getChat(Integer.valueOf(req.peer.channel_id));
                            if (chat == null || !chat.megagroup) {
                                return;
                            }
                        }
                        if (req.peer != null) {
                            if (action == 0) {
                                req.action = new TL_sendMessageTypingAction();
                            } else if (action == 1) {
                                req.action = new TL_sendMessageRecordAudioAction();
                            } else if (action == 2) {
                                req.action = new TL_sendMessageCancelAction();
                            } else if (action == 3) {
                                req.action = new TL_sendMessageUploadDocumentAction();
                            } else if (action == 4) {
                                req.action = new TL_sendMessageUploadPhotoAction();
                            } else if (action == 5) {
                                req.action = new TL_sendMessageUploadVideoAction();
                            } else if (action == 6) {
                                req.action = new TL_sendMessageGamePlayAction();
                            } else if (action == 7) {
                                req.action = new TL_sendMessageRecordRoundAction();
                            } else if (action == 8) {
                                req.action = new TL_sendMessageUploadRoundAction();
                            } else if (action == 9) {
                                req.action = new TL_sendMessageUploadAudioAction();
                            }
                            typings.put(dialog_id, Boolean.valueOf(true));
                            reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$67(this, action, dialog_id), 2);
                            if (classGuid != 0) {
                                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, classGuid);
                            }
                        }
                    }
                } else if (action == 0) {
                    EncryptedChat chat2 = getEncryptedChat(Integer.valueOf(high_id));
                    if (chat2.auth_key != null && chat2.auth_key.length > 1 && (chat2 instanceof TL_encryptedChat)) {
                        TL_messages_setEncryptedTyping req2 = new TL_messages_setEncryptedTyping();
                        req2.peer = new TL_inputEncryptedChat();
                        req2.peer.chat_id = chat2.id;
                        req2.peer.access_hash = chat2.access_hash;
                        req2.typing = true;
                        typings.put(dialog_id, Boolean.valueOf(true));
                        reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$68(this, action, dialog_id), 2);
                        if (classGuid != 0) {
                            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, classGuid);
                        }
                    }
                }
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$sendTyping$99$MessagesController(int action, long dialog_id, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$227(this, action, dialog_id));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$98$MessagesController(int action, long dialog_id) {
        LongSparseArray<Boolean> typings1 = (LongSparseArray) this.sendingTypings.get(action);
        if (typings1 != null) {
            typings1.remove(dialog_id);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$sendTyping$101$MessagesController(int action, long dialog_id, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$226(this, action, dialog_id));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$100$MessagesController(int action, long dialog_id) {
        LongSparseArray<Boolean> typings12 = (LongSparseArray) this.sendingTypings.get(action);
        if (typings12 != null) {
            typings12.remove(dialog_id);
        }
    }

    /* Access modifiers changed, original: protected */
    public void removeDeletedMessagesFromArray(long dialog_id, ArrayList<Message> messages) {
        int maxDeletedId = ((Integer) this.deletedHistory.get(dialog_id, Integer.valueOf(0))).intValue();
        if (maxDeletedId != 0) {
            int a = 0;
            int N = messages.size();
            while (a < N) {
                if (((Message) messages.get(a)).id <= maxDeletedId) {
                    messages.remove(a);
                    a--;
                    N--;
                }
                a++;
            }
        }
    }

    public void loadMessages(long dialog_id, int count, int max_id, int offset_date, boolean fromCache, int midDate, int classGuid, int load_type, int last_message_id, boolean isChannel, int loadIndex) {
        loadMessages(dialog_id, count, max_id, offset_date, fromCache, midDate, classGuid, load_type, last_message_id, isChannel, loadIndex, 0, 0, 0, false, 0);
    }

    public void loadMessages(long dialog_id, int count, int max_id, int offset_date, boolean fromCache, int midDate, int classGuid, int load_type, int last_message_id, boolean isChannel, int loadIndex, int first_unread, int unread_count, int last_date, boolean queryFromServer, int mentionsCount) {
        loadMessagesInternal(dialog_id, count, max_id, offset_date, fromCache, midDate, classGuid, load_type, last_message_id, isChannel, loadIndex, first_unread, unread_count, last_date, queryFromServer, mentionsCount, true);
    }

    private void loadMessagesInternal(long dialog_id, int count, int max_id, int offset_date, boolean fromCache, int midDate, int classGuid, int load_type, int last_message_id, boolean isChannel, int loadIndex, int first_unread, int unread_count, int last_date, boolean queryFromServer, int mentionsCount, boolean loadDialog) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load messages in chat " + dialog_id + " count " + count + " max_id " + max_id + " cache " + fromCache + " mindate = " + midDate + " guid " + classGuid + " load_type " + load_type + " last_message_id " + last_message_id + " index " + loadIndex + " firstUnread " + first_unread + " unread_count " + unread_count + " last_date " + last_date + " queryFromServer " + queryFromServer);
        }
        int lower_part = (int) dialog_id;
        TLObject req;
        if (fromCache || lower_part == 0) {
            MessagesStorage.getInstance(this.currentAccount).getMessages(dialog_id, count, max_id, offset_date, midDate, classGuid, load_type, isChannel, loadIndex);
        } else if (loadDialog && ((load_type == 3 || load_type == 2) && last_message_id == 0)) {
            req = new TL_messages_getPeerDialogs();
            InputPeer inputPeer = getInputPeer((int) dialog_id);
            TL_inputDialogPeer inputDialogPeer = new TL_inputDialogPeer();
            inputDialogPeer.peer = inputPeer;
            req.peers.add(inputDialogPeer);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$69(this, dialog_id, count, max_id, offset_date, midDate, classGuid, load_type, isChannel, loadIndex, first_unread, last_date, queryFromServer));
        } else {
            req = new TL_messages_getHistory();
            req.peer = getInputPeer(lower_part);
            if (load_type == 4) {
                req.add_offset = (-count) + 5;
            } else if (load_type == 3) {
                req.add_offset = (-count) / 2;
            } else if (load_type == 1) {
                req.add_offset = (-count) - 1;
            } else if (load_type == 2 && max_id != 0) {
                req.add_offset = (-count) + 6;
            } else if (lower_part < 0 && max_id != 0) {
                if (ChatObject.isChannel(getChat(Integer.valueOf(-lower_part)))) {
                    req.add_offset = -1;
                    req.limit++;
                }
            }
            req.limit = count;
            req.offset_id = max_id;
            req.offset_date = offset_date;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$70(this, dialog_id, count, max_id, offset_date, classGuid, first_unread, last_message_id, unread_count, last_date, load_type, isChannel, loadIndex, queryFromServer, mentionsCount)), classGuid);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadMessagesInternal$102$MessagesController(long dialog_id, int count, int max_id, int offset_date, int midDate, int classGuid, int load_type, boolean isChannel, int loadIndex, int first_unread, int last_date, boolean queryFromServer, TLObject response, TL_error error) {
        if (response != null) {
            TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
            if (!res.dialogs.isEmpty()) {
                TL_dialog dialog = (TL_dialog) res.dialogs.get(0);
                if (dialog.top_message != 0) {
                    messages_Dialogs dialogs = new TL_messages_dialogs();
                    dialogs.chats = res.chats;
                    dialogs.users = res.users;
                    dialogs.dialogs = res.dialogs;
                    dialogs.messages = res.messages;
                    MessagesStorage.getInstance(this.currentAccount).putDialogs(dialogs, 0);
                }
                loadMessagesInternal(dialog_id, count, max_id, offset_date, false, midDate, classGuid, load_type, dialog.top_message, isChannel, loadIndex, first_unread, dialog.unread_count, last_date, queryFromServer, dialog.unread_mentions_count, false);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadMessagesInternal$103$MessagesController(long dialog_id, int count, int max_id, int offset_date, int classGuid, int first_unread, int last_message_id, int unread_count, int last_date, int load_type, boolean isChannel, int loadIndex, boolean queryFromServer, int mentionsCount, TLObject response, TL_error error) {
        if (response != null) {
            messages_Messages res = (messages_Messages) response;
            removeDeletedMessagesFromArray(dialog_id, res.messages);
            if (res.messages.size() > count) {
                res.messages.remove(0);
            }
            int mid = max_id;
            if (offset_date != 0 && !res.messages.isEmpty()) {
                mid = ((Message) res.messages.get(res.messages.size() - 1)).id;
                for (int a = res.messages.size() - 1; a >= 0; a--) {
                    Message message = (Message) res.messages.get(a);
                    if (message.date > offset_date) {
                        mid = message.id;
                        break;
                    }
                }
            }
            processLoadedMessages(res, dialog_id, count, mid, offset_date, false, classGuid, first_unread, last_message_id, unread_count, last_date, load_type, isChannel, false, loadIndex, queryFromServer, mentionsCount);
        }
    }

    public void reloadWebPages(long dialog_id, HashMap<String, ArrayList<MessageObject>> webpagesToReload) {
        for (Entry<String, ArrayList<MessageObject>> entry : webpagesToReload.entrySet()) {
            String url = (String) entry.getKey();
            ArrayList<MessageObject> messages = (ArrayList) entry.getValue();
            ArrayList<MessageObject> arrayList = (ArrayList) this.reloadingWebpages.get(url);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.reloadingWebpages.put(url, arrayList);
            }
            arrayList.addAll(messages);
            TL_messages_getWebPagePreview req = new TL_messages_getWebPagePreview();
            req.message = url;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$71(this, url, dialog_id));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$reloadWebPages$105$MessagesController(String url, long dialog_id, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$225(this, url, response, dialog_id));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$104$MessagesController(String url, TLObject response, long dialog_id) {
        ArrayList<MessageObject> arrayList1 = (ArrayList) this.reloadingWebpages.remove(url);
        if (arrayList1 != null) {
            messages_Messages messagesRes = new TL_messages_messages();
            int a;
            if (response instanceof TL_messageMediaWebPage) {
                TL_messageMediaWebPage media = (TL_messageMediaWebPage) response;
                if ((media.webpage instanceof TL_webPage) || (media.webpage instanceof TL_webPageEmpty)) {
                    for (a = 0; a < arrayList1.size(); a++) {
                        ((MessageObject) arrayList1.get(a)).messageOwner.media.webpage = media.webpage;
                        if (a == 0) {
                            ImageLoader.saveMessageThumbs(((MessageObject) arrayList1.get(a)).messageOwner);
                        }
                        messagesRes.messages.add(((MessageObject) arrayList1.get(a)).messageOwner);
                    }
                } else {
                    this.reloadingWebpagesPending.put(media.webpage.id, arrayList1);
                }
            } else {
                for (a = 0; a < arrayList1.size(); a++) {
                    ((MessageObject) arrayList1.get(a)).messageOwner.media.webpage = new TL_webPageEmpty();
                    messagesRes.messages.add(((MessageObject) arrayList1.get(a)).messageOwner);
                }
            }
            if (!messagesRes.messages.isEmpty()) {
                MessagesStorage.getInstance(this.currentAccount).putMessages(messagesRes, dialog_id, -2, 0, false);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialog_id), arrayList1);
            }
        }
    }

    public void processLoadedMessages(messages_Messages messagesRes, long dialog_id, int count, int max_id, int offset_date, boolean isCache, int classGuid, int first_unread, int last_message_id, int unread_count, int last_date, int load_type, boolean isChannel, boolean isEnd, int loadIndex, boolean queryFromServer, int mentionsCount) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("processLoadedMessages size " + messagesRes.messages.size() + " in chat " + dialog_id + " count " + count + " max_id " + max_id + " cache " + isCache + " guid " + classGuid + " load_type " + load_type + " last_message_id " + last_message_id + " isChannel " + isChannel + " index " + loadIndex + " firstUnread " + first_unread + " unread_count " + unread_count + " last_date " + last_date + " queryFromServer " + queryFromServer);
        }
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$72(this, messagesRes, dialog_id, isCache, count, load_type, queryFromServer, first_unread, max_id, offset_date, classGuid, last_message_id, isChannel, loadIndex, unread_count, last_date, mentionsCount, isEnd));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processLoadedMessages$108$MessagesController(messages_Messages messagesRes, long dialog_id, boolean isCache, int count, int load_type, boolean queryFromServer, int first_unread, int max_id, int offset_date, int classGuid, int last_message_id, boolean isChannel, int loadIndex, int unread_count, int last_date, int mentionsCount, boolean isEnd) {
        int a;
        boolean createDialog = false;
        boolean isMegagroup = false;
        if (messagesRes instanceof TL_messages_channelMessages) {
            int channelId = -((int) dialog_id);
            if (this.channelsPts.get(channelId) == 0 && MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(channelId) == 0) {
                this.channelsPts.put(channelId, messagesRes.pts);
                createDialog = true;
                if (this.needShortPollChannels.indexOfKey(channelId) < 0 || this.shortPollChannels.indexOfKey(channelId) >= 0) {
                    getChannelDifference(channelId);
                } else {
                    getChannelDifference(channelId, 2, 0, null);
                }
            }
            for (a = 0; a < messagesRes.chats.size(); a++) {
                Chat chat = (Chat) messagesRes.chats.get(a);
                if (chat.id == channelId) {
                    isMegagroup = chat.megagroup;
                    break;
                }
            }
        }
        int lower_id = (int) dialog_id;
        int high_id = (int) (dialog_id >> 32);
        if (!isCache) {
            ImageLoader.saveMessagesThumbs(messagesRes.messages);
        }
        if (high_id == 1 || lower_id == 0 || !isCache || messagesRes.messages.size() != 0) {
            Message message;
            SparseArray<User> usersDict = new SparseArray();
            SparseArray<Chat> chatsDict = new SparseArray();
            for (a = 0; a < messagesRes.users.size(); a++) {
                User u = (User) messagesRes.users.get(a);
                usersDict.put(u.id, u);
            }
            for (a = 0; a < messagesRes.chats.size(); a++) {
                Chat c = (Chat) messagesRes.chats.get(a);
                chatsDict.put(c.id, c);
            }
            int size = messagesRes.messages.size();
            if (!isCache) {
                Integer inboxValue = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                if (inboxValue == null) {
                    inboxValue = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(false, dialog_id));
                    this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), inboxValue);
                }
                Integer outboxValue = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                if (outboxValue == null) {
                    outboxValue = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, dialog_id));
                    this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), outboxValue);
                }
                for (a = 0; a < size; a++) {
                    message = (Message) messagesRes.messages.get(a);
                    if (isMegagroup) {
                        message.flags |= Integer.MIN_VALUE;
                    }
                    if (message.action instanceof TL_messageActionChatDeleteUser) {
                        User user = (User) usersDict.get(message.action.user_id);
                        if (user != null && user.bot) {
                            message.reply_markup = new TL_replyKeyboardHide();
                            message.flags |= 64;
                        }
                    }
                    if ((message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate)) {
                        message.unread = false;
                        message.media_unread = false;
                    } else {
                        message.unread = (message.out ? outboxValue : inboxValue).intValue() < message.id;
                    }
                }
                MessagesStorage.getInstance(this.currentAccount).putMessages(messagesRes, dialog_id, load_type, max_id, createDialog);
            }
            ArrayList<MessageObject> objects = new ArrayList();
            ArrayList<Integer> messagesToReload = new ArrayList();
            HashMap<String, ArrayList<MessageObject>> webpagesToReload = new HashMap();
            for (a = 0; a < size; a++) {
                message = (Message) messagesRes.messages.get(a);
                message.dialog_id = dialog_id;
                MessageObject messageObject = new MessageObject(this.currentAccount, message, (SparseArray) usersDict, (SparseArray) chatsDict, true);
                objects.add(messageObject);
                if (isCache) {
                    if (message.media instanceof TL_messageMediaUnsupported) {
                        if (message.media.bytes != null && (message.media.bytes.length == 0 || (message.media.bytes.length == 1 && message.media.bytes[0] < (byte) 97))) {
                            messagesToReload.add(Integer.valueOf(message.id));
                        }
                    } else if (message.media instanceof TL_messageMediaWebPage) {
                        if ((message.media.webpage instanceof TL_webPagePending) && message.media.webpage.date <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                            messagesToReload.add(Integer.valueOf(message.id));
                        } else if (message.media.webpage instanceof TL_webPageUrlPending) {
                            ArrayList<MessageObject> arrayList = (ArrayList) webpagesToReload.get(message.media.webpage.url);
                            if (arrayList == null) {
                                arrayList = new ArrayList();
                                webpagesToReload.put(message.media.webpage.url, arrayList);
                            }
                            arrayList.add(messageObject);
                        }
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$224(this, messagesRes, isCache, queryFromServer, load_type, first_unread, dialog_id, count, objects, last_message_id, unread_count, last_date, isEnd, classGuid, loadIndex, max_id, mentionsCount, messagesToReload, webpagesToReload));
            return;
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$223(this, dialog_id, count, load_type, queryFromServer, first_unread, max_id, offset_date, classGuid, last_message_id, isChannel, loadIndex, unread_count, last_date, mentionsCount));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$106$MessagesController(long dialog_id, int count, int load_type, boolean queryFromServer, int first_unread, int max_id, int offset_date, int classGuid, int last_message_id, boolean isChannel, int loadIndex, int unread_count, int last_date, int mentionsCount) {
        int i = (load_type == 2 && queryFromServer) ? first_unread : max_id;
        loadMessages(dialog_id, count, i, offset_date, false, 0, classGuid, load_type, last_message_id, isChannel, loadIndex, first_unread, unread_count, last_date, queryFromServer, mentionsCount);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$107$MessagesController(messages_Messages messagesRes, boolean isCache, boolean queryFromServer, int load_type, int first_unread, long dialog_id, int count, ArrayList objects, int last_message_id, int unread_count, int last_date, boolean isEnd, int classGuid, int loadIndex, int max_id, int mentionsCount, ArrayList messagesToReload, HashMap webpagesToReload) {
        putUsers(messagesRes.users, isCache);
        putChats(messagesRes.chats, isCache);
        int first_unread_final = Integer.MAX_VALUE;
        if (queryFromServer && load_type == 2) {
            for (int a = 0; a < messagesRes.messages.size(); a++) {
                Message message = (Message) messagesRes.messages.get(a);
                if (!message.out && message.id > first_unread && message.id < first_unread_final) {
                    first_unread_final = message.id;
                }
            }
        }
        if (first_unread_final == Integer.MAX_VALUE) {
            first_unread_final = first_unread;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDidLoad, Long.valueOf(dialog_id), Integer.valueOf(count), objects, Boolean.valueOf(isCache), Integer.valueOf(first_unread_final), Integer.valueOf(last_message_id), Integer.valueOf(unread_count), Integer.valueOf(last_date), Integer.valueOf(load_type), Boolean.valueOf(isEnd), Integer.valueOf(classGuid), Integer.valueOf(loadIndex), Integer.valueOf(max_id), Integer.valueOf(mentionsCount));
        if (!messagesToReload.isEmpty()) {
            reloadMessages(messagesToReload, dialog_id);
        }
        if (!webpagesToReload.isEmpty()) {
            reloadWebPages(dialog_id, webpagesToReload);
        }
    }

    public void loadHintDialogs() {
        if (this.hintDialogs.isEmpty() && !TextUtils.isEmpty(this.installReferer)) {
            TL_help_getRecentMeUrls req = new TL_help_getRecentMeUrls();
            req.referer = this.installReferer;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$73(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadHintDialogs$110$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$222(this, response));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$109$MessagesController(TLObject response) {
        TL_help_recentMeUrls res = (TL_help_recentMeUrls) response;
        putUsers(res.users, false);
        putChats(res.chats, false);
        this.hintDialogs.clear();
        this.hintDialogs.addAll(res.urls);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:0x0185  */
    public void loadDialogs(int r13, int r14, boolean r15) {
        /*
        r12 = this;
        r8 = r12.loadingDialogs;
        if (r8 != 0) goto L_0x0008;
    L_0x0004:
        r8 = r12.resetingDialogs;
        if (r8 == 0) goto L_0x0009;
    L_0x0008:
        return;
    L_0x0009:
        r8 = 1;
        r12.loadingDialogs = r8;
        r8 = r12.currentAccount;
        r8 = org.telegram.messenger.NotificationCenter.getInstance(r8);
        r9 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r10 = 0;
        r10 = new java.lang.Object[r10];
        r8.postNotificationName(r9, r10);
        r8 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r8 == 0) goto L_0x004b;
    L_0x001e:
        r8 = new java.lang.StringBuilder;
        r8.<init>();
        r9 = "load cacheOffset = ";
        r8 = r8.append(r9);
        r8 = r8.append(r13);
        r9 = " count = ";
        r8 = r8.append(r9);
        r8 = r8.append(r14);
        r9 = " cache = ";
        r8 = r8.append(r9);
        r8 = r8.append(r15);
        r8 = r8.toString();
        org.telegram.messenger.FileLog.d(r8);
    L_0x004b:
        if (r15 == 0) goto L_0x005d;
    L_0x004d:
        r8 = r12.currentAccount;
        r9 = org.telegram.messenger.MessagesStorage.getInstance(r8);
        if (r13 != 0) goto L_0x005a;
    L_0x0055:
        r8 = 0;
    L_0x0056:
        r9.getDialogs(r8, r14);
        goto L_0x0008;
    L_0x005a:
        r8 = r12.nextDialogsCacheOffset;
        goto L_0x0056;
    L_0x005d:
        r7 = new org.telegram.tgnet.TLRPC$TL_messages_getDialogs;
        r7.<init>();
        r7.limit = r14;
        r8 = 1;
        r7.exclude_pinned = r8;
        r8 = r12.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.dialogsLoadOffsetId;
        r9 = -1;
        if (r8 == r9) goto L_0x0123;
    L_0x0072:
        r8 = r12.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.dialogsLoadOffsetId;
        r9 = NUM; // 0x7fffffff float:NaN double:1.060997895E-314;
        if (r8 != r9) goto L_0x0098;
    L_0x007f:
        r8 = 1;
        r12.dialogsEndReached = r8;
        r8 = 1;
        r12.serverDialogsEndReached = r8;
        r8 = 0;
        r12.loadingDialogs = r8;
        r8 = r12.currentAccount;
        r8 = org.telegram.messenger.NotificationCenter.getInstance(r8);
        r9 = org.telegram.messenger.NotificationCenter.dialogsNeedReload;
        r10 = 0;
        r10 = new java.lang.Object[r10];
        r8.postNotificationName(r9, r10);
        goto L_0x0008;
    L_0x0098:
        r8 = r12.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.dialogsLoadOffsetId;
        r7.offset_id = r8;
        r8 = r12.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.dialogsLoadOffsetDate;
        r7.offset_date = r8;
        r8 = r7.offset_id;
        if (r8 != 0) goto L_0x00c7;
    L_0x00b0:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r8.<init>();
        r7.offset_peer = r8;
    L_0x00b7:
        r8 = r12.currentAccount;
        r8 = org.telegram.tgnet.ConnectionsManager.getInstance(r8);
        r9 = new org.telegram.messenger.MessagesController$$Lambda$74;
        r9.<init>(r12, r14);
        r8.sendRequest(r7, r9);
        goto L_0x0008;
    L_0x00c7:
        r8 = r12.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.dialogsLoadOffsetChannelId;
        if (r8 == 0) goto L_0x00f1;
    L_0x00d1:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
        r8.<init>();
        r7.offset_peer = r8;
        r8 = r7.offset_peer;
        r9 = r12.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r9 = r9.dialogsLoadOffsetChannelId;
        r8.channel_id = r9;
    L_0x00e4:
        r8 = r7.offset_peer;
        r9 = r12.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r10 = r9.dialogsLoadOffsetAccess;
        r8.access_hash = r10;
        goto L_0x00b7;
    L_0x00f1:
        r8 = r12.currentAccount;
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);
        r8 = r8.dialogsLoadOffsetUserId;
        if (r8 == 0) goto L_0x010f;
    L_0x00fb:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputPeerUser;
        r8.<init>();
        r7.offset_peer = r8;
        r8 = r7.offset_peer;
        r9 = r12.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r9 = r9.dialogsLoadOffsetUserId;
        r8.user_id = r9;
        goto L_0x00e4;
    L_0x010f:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputPeerChat;
        r8.<init>();
        r7.offset_peer = r8;
        r8 = r7.offset_peer;
        r9 = r12.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r9 = r9.dialogsLoadOffsetChatId;
        r8.chat_id = r9;
        goto L_0x00e4;
    L_0x0123:
        r2 = 0;
        r8 = r12.dialogs;
        r8 = r8.size();
        r0 = r8 + -1;
    L_0x012c:
        if (r0 < 0) goto L_0x0183;
    L_0x012e:
        r8 = r12.dialogs;
        r1 = r8.get(r0);
        r1 = (org.telegram.tgnet.TLRPC.TL_dialog) r1;
        r8 = r1.pinned;
        if (r8 == 0) goto L_0x013d;
    L_0x013a:
        r0 = r0 + -1;
        goto L_0x012c;
    L_0x013d:
        r8 = r1.id;
        r5 = (int) r8;
        r8 = r1.id;
        r10 = 32;
        r8 = r8 >> r10;
        r3 = (int) r8;
        if (r5 == 0) goto L_0x013a;
    L_0x0148:
        r8 = 1;
        if (r3 == r8) goto L_0x013a;
    L_0x014b:
        r8 = r1.top_message;
        if (r8 <= 0) goto L_0x013a;
    L_0x014f:
        r8 = r12.dialogMessage;
        r10 = r1.id;
        r6 = r8.get(r10);
        r6 = (org.telegram.messenger.MessageObject) r6;
        if (r6 == 0) goto L_0x013a;
    L_0x015b:
        r8 = r6.getId();
        if (r8 <= 0) goto L_0x013a;
    L_0x0161:
        r8 = r6.messageOwner;
        r8 = r8.date;
        r7.offset_date = r8;
        r8 = r6.messageOwner;
        r8 = r8.id;
        r7.offset_id = r8;
        r8 = r6.messageOwner;
        r8 = r8.to_id;
        r8 = r8.channel_id;
        if (r8 == 0) goto L_0x018e;
    L_0x0175:
        r8 = r6.messageOwner;
        r8 = r8.to_id;
        r8 = r8.channel_id;
        r4 = -r8;
    L_0x017c:
        r8 = r12.getInputPeer(r4);
        r7.offset_peer = r8;
        r2 = 1;
    L_0x0183:
        if (r2 != 0) goto L_0x00b7;
    L_0x0185:
        r8 = new org.telegram.tgnet.TLRPC$TL_inputPeerEmpty;
        r8.<init>();
        r7.offset_peer = r8;
        goto L_0x00b7;
    L_0x018e:
        r8 = r6.messageOwner;
        r8 = r8.to_id;
        r8 = r8.chat_id;
        if (r8 == 0) goto L_0x019e;
    L_0x0196:
        r8 = r6.messageOwner;
        r8 = r8.to_id;
        r8 = r8.chat_id;
        r4 = -r8;
        goto L_0x017c;
    L_0x019e:
        r8 = r6.messageOwner;
        r8 = r8.to_id;
        r4 = r8.user_id;
        goto L_0x017c;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.loadDialogs(int, int, boolean):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadDialogs$111$MessagesController(int count, TLObject response, TL_error error) {
        if (error == null) {
            processLoadedDialogs((messages_Dialogs) response, null, 0, count, 0, false, false, false);
        }
    }

    public void loadGlobalNotificationsSettings() {
        if (this.loadingNotificationSettings == 0 && !UserConfig.getInstance(this.currentAccount).notificationsSettingsLoaded) {
            boolean enabled;
            SharedPreferences preferences = getNotificationsSettings(this.currentAccount);
            Editor editor1 = null;
            if (preferences.contains("EnableGroup")) {
                enabled = preferences.getBoolean("EnableGroup", true);
                if (null == null) {
                    editor1 = preferences.edit();
                }
                if (!enabled) {
                    editor1.putInt("EnableGroup2", Integer.MAX_VALUE);
                    editor1.putInt("EnableChannel2", Integer.MAX_VALUE);
                }
                editor1.remove("EnableGroup").commit();
            }
            if (preferences.contains("EnableAll")) {
                enabled = preferences.getBoolean("EnableAll", true);
                if (editor1 == null) {
                    editor1 = preferences.edit();
                }
                if (!enabled) {
                    editor1.putInt("EnableAll2", Integer.MAX_VALUE);
                }
                editor1.remove("EnableAll").commit();
            }
            if (editor1 != null) {
                editor1.commit();
            }
            this.loadingNotificationSettings = 3;
            for (int a = 0; a < 3; a++) {
                TL_account_getNotifySettings req = new TL_account_getNotifySettings();
                if (a == 0) {
                    req.peer = new TL_inputNotifyChats();
                } else if (a == 1) {
                    req.peer = new TL_inputNotifyUsers();
                } else if (a == 2) {
                    req.peer = new TL_inputNotifyBroadcasts();
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$75(this, a));
            }
        }
        if (!UserConfig.getInstance(this.currentAccount).notificationsSignUpSettingsLoaded) {
            loadSignUpNotificationsSettings();
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadGlobalNotificationsSettings$113$MessagesController(int type, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$221(this, response, type));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$112$MessagesController(TLObject response, int type) {
        if (response != null) {
            this.loadingNotificationSettings--;
            TL_peerNotifySettings notify_settings = (TL_peerNotifySettings) response;
            Editor editor = this.notificationsPreferences.edit();
            if (type == 0) {
                if ((notify_settings.flags & 1) != 0) {
                    editor.putBoolean("EnablePreviewGroup", notify_settings.show_previews);
                }
                if ((notify_settings.flags & 2) != 0) {
                }
                if ((notify_settings.flags & 4) != 0) {
                    editor.putInt("EnableGroup2", notify_settings.mute_until);
                }
            } else if (type == 1) {
                if ((notify_settings.flags & 1) != 0) {
                    editor.putBoolean("EnablePreviewAll", notify_settings.show_previews);
                }
                if ((notify_settings.flags & 2) != 0) {
                }
                if ((notify_settings.flags & 4) != 0) {
                    editor.putInt("EnableAll2", notify_settings.mute_until);
                }
            } else if (type == 2) {
                if ((notify_settings.flags & 1) != 0) {
                    editor.putBoolean("EnablePreviewChannel", notify_settings.show_previews);
                }
                if ((notify_settings.flags & 2) != 0) {
                }
                if ((notify_settings.flags & 4) != 0) {
                    editor.putInt("EnableChannel2", notify_settings.mute_until);
                }
            }
            editor.commit();
            if (this.loadingNotificationSettings == 0) {
                UserConfig.getInstance(this.currentAccount).notificationsSettingsLoaded = true;
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
            }
        }
    }

    public void loadSignUpNotificationsSettings() {
        if (!this.loadingNotificationSignUpSettings) {
            this.loadingNotificationSignUpSettings = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_account_getContactSignUpNotification(), new MessagesController$$Lambda$76(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadSignUpNotificationsSettings$115$MessagesController(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$220(this, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$114$MessagesController(TLObject response) {
        this.loadingNotificationSignUpSettings = false;
        Editor editor = this.notificationsPreferences.edit();
        this.enableJoined = response instanceof TL_boolFalse;
        editor.putBoolean("EnableContactJoined", this.enableJoined);
        editor.commit();
        UserConfig.getInstance(this.currentAccount).notificationsSignUpSettingsLoaded = true;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public void forceResetDialogs() {
        resetDialogs(true, MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
    }

    private void resetDialogs(boolean query, int seq, int newPts, int date, int qts) {
        if (query) {
            if (!this.resetingDialogs) {
                this.resetingDialogs = true;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getPinnedDialogs(), new MessagesController$$Lambda$77(this, seq, newPts, date, qts));
                TLObject req2 = new TL_messages_getDialogs();
                req2.limit = 100;
                req2.exclude_pinned = true;
                req2.offset_peer = new TL_inputPeerEmpty();
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$78(this, seq, newPts, date, qts));
            }
        } else if (this.resetDialogsPinned != null && this.resetDialogsAll != null) {
            int a;
            Message message;
            Chat chat;
            Integer value;
            int messagesCount = this.resetDialogsAll.messages.size();
            int dialogsCount = this.resetDialogsAll.dialogs.size();
            this.resetDialogsAll.dialogs.addAll(this.resetDialogsPinned.dialogs);
            this.resetDialogsAll.messages.addAll(this.resetDialogsPinned.messages);
            this.resetDialogsAll.users.addAll(this.resetDialogsPinned.users);
            this.resetDialogsAll.chats.addAll(this.resetDialogsPinned.chats);
            LongSparseArray<TL_dialog> new_dialogs_dict = new LongSparseArray();
            LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray();
            SparseArray usersDict = new SparseArray();
            SparseArray chatsDict = new SparseArray();
            for (a = 0; a < this.resetDialogsAll.users.size(); a++) {
                User u = (User) this.resetDialogsAll.users.get(a);
                usersDict.put(u.id, u);
            }
            for (a = 0; a < this.resetDialogsAll.chats.size(); a++) {
                Chat c = (Chat) this.resetDialogsAll.chats.get(a);
                chatsDict.put(c.id, c);
            }
            Message lastMessage = null;
            for (a = 0; a < this.resetDialogsAll.messages.size(); a++) {
                message = (Message) this.resetDialogsAll.messages.get(a);
                if (a < messagesCount && (lastMessage == null || message.date < lastMessage.date)) {
                    lastMessage = message;
                }
                MessageObject messageObject;
                if (message.to_id.channel_id != 0) {
                    chat = (Chat) chatsDict.get(message.to_id.channel_id);
                    if (chat == null || !chat.left) {
                        if (chat != null && chat.megagroup) {
                            message.flags |= Integer.MIN_VALUE;
                        }
                        messageObject = new MessageObject(this.currentAccount, message, usersDict, chatsDict, false);
                        new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                    }
                } else {
                    if (message.to_id.chat_id != 0) {
                        chat = (Chat) chatsDict.get(message.to_id.chat_id);
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    messageObject = new MessageObject(this.currentAccount, message, usersDict, chatsDict, false);
                    new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                }
            }
            for (a = 0; a < this.resetDialogsAll.dialogs.size(); a++) {
                TL_dialog d = (TL_dialog) this.resetDialogsAll.dialogs.get(a);
                if (d.id == 0 && d.peer != null) {
                    if (d.peer.user_id != 0) {
                        d.id = (long) d.peer.user_id;
                    } else if (d.peer.chat_id != 0) {
                        d.id = (long) (-d.peer.chat_id);
                    } else if (d.peer.channel_id != 0) {
                        d.id = (long) (-d.peer.channel_id);
                    }
                }
                if (d.id != 0) {
                    if (d.last_message_date == 0) {
                        MessageObject mess = (MessageObject) new_dialogMessage.get(d.id);
                        if (mess != null) {
                            d.last_message_date = mess.messageOwner.date;
                        }
                    }
                    if (DialogObject.isChannel(d)) {
                        chat = (Chat) chatsDict.get(-((int) d.id));
                        if (chat == null || !chat.left) {
                            this.channelsPts.put(-((int) d.id), d.pts);
                        }
                    } else if (((int) d.id) < 0) {
                        chat = (Chat) chatsDict.get(-((int) d.id));
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    new_dialogs_dict.put(d.id, d);
                    value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                    value = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                }
            }
            ImageLoader.saveMessagesThumbs(this.resetDialogsAll.messages);
            for (a = 0; a < this.resetDialogsAll.messages.size(); a++) {
                message = (Message) this.resetDialogsAll.messages.get(a);
                if (message.action instanceof TL_messageActionChatDeleteUser) {
                    User user = (User) usersDict.get(message.action.user_id);
                    if (user != null && user.bot) {
                        message.reply_markup = new TL_replyKeyboardHide();
                        message.flags |= 64;
                    }
                }
                if ((message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate)) {
                    message.unread = false;
                    message.media_unread = false;
                } else {
                    boolean z;
                    ConcurrentHashMap<Long, Integer> read_max = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                    value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                    if (value == null) {
                        value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                        read_max.put(Long.valueOf(message.dialog_id), value);
                    }
                    if (value.intValue() < message.id) {
                        z = true;
                    } else {
                        z = false;
                    }
                    message.unread = z;
                }
            }
            MessagesStorage.getInstance(this.currentAccount).resetDialogs(this.resetDialogsAll, messagesCount, seq, newPts, date, qts, new_dialogs_dict, new_dialogMessage, lastMessage, dialogsCount);
            this.resetDialogsPinned = null;
            this.resetDialogsAll = null;
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$resetDialogs$116$MessagesController(int seq, int newPts, int date, int qts, TLObject response, TL_error error) {
        if (response != null) {
            this.resetDialogsPinned = (TL_messages_peerDialogs) response;
            resetDialogs(false, seq, newPts, date, qts);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$resetDialogs$117$MessagesController(int seq, int newPts, int date, int qts, TLObject response, TL_error error) {
        if (error == null) {
            this.resetDialogsAll = (messages_Dialogs) response;
            resetDialogs(false, seq, newPts, date, qts);
        }
    }

    /* Access modifiers changed, original: protected */
    public void completeDialogsReset(messages_Dialogs dialogsRes, int messagesCount, int seq, int newPts, int date, int qts, LongSparseArray<TL_dialog> new_dialogs_dict, LongSparseArray<MessageObject> new_dialogMessage, Message lastMessage) {
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$79(this, newPts, date, qts, dialogsRes, new_dialogs_dict, new_dialogMessage));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$completeDialogsReset$119$MessagesController(int newPts, int date, int qts, messages_Dialogs dialogsRes, LongSparseArray new_dialogs_dict, LongSparseArray new_dialogMessage) {
        this.gettingDifference = false;
        MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(newPts);
        MessagesStorage.getInstance(this.currentAccount).setLastDateValue(date);
        MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(qts);
        getDifference();
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$219(this, dialogsRes, new_dialogs_dict, new_dialogMessage));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$118$MessagesController(messages_Dialogs dialogsRes, LongSparseArray new_dialogs_dict, LongSparseArray new_dialogMessage) {
        int a;
        MessageObject messageObject;
        this.resetingDialogs = false;
        applyDialogsNotificationsSettings(dialogsRes.dialogs);
        if (!UserConfig.getInstance(this.currentAccount).draftsLoaded) {
            DataQuery.getInstance(this.currentAccount).loadDrafts();
        }
        putUsers(dialogsRes.users, false);
        putChats(dialogsRes.chats, false);
        for (a = 0; a < this.dialogs.size(); a++) {
            TL_dialog oldDialog = (TL_dialog) this.dialogs.get(a);
            if (((int) oldDialog.id) != 0) {
                this.dialogs_dict.remove(oldDialog.id);
                messageObject = (MessageObject) this.dialogMessage.get(oldDialog.id);
                this.dialogMessage.remove(oldDialog.id);
                if (messageObject != null) {
                    this.dialogMessagesByIds.remove(messageObject.getId());
                    if (messageObject.messageOwner.random_id != 0) {
                        this.dialogMessagesByRandomIds.remove(messageObject.messageOwner.random_id);
                    }
                }
            }
        }
        for (a = 0; a < new_dialogs_dict.size(); a++) {
            long key = new_dialogs_dict.keyAt(a);
            TL_dialog value = (TL_dialog) new_dialogs_dict.valueAt(a);
            if (value.draft instanceof TL_draftMessage) {
                DataQuery.getInstance(this.currentAccount).saveDraft(value.id, value.draft, null, false);
            }
            this.dialogs_dict.put(key, value);
            messageObject = (MessageObject) new_dialogMessage.get(value.id);
            this.dialogMessage.put(key, messageObject);
            if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                if (messageObject.messageOwner.random_id != 0) {
                    this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                }
            }
        }
        this.dialogs.clear();
        int size = this.dialogs_dict.size();
        for (a = 0; a < size; a++) {
            this.dialogs.add(this.dialogs_dict.valueAt(a));
        }
        sortDialogs(null);
        this.dialogsEndReached = true;
        this.serverDialogsEndReached = false;
        if (!(UserConfig.getInstance(this.currentAccount).totalDialogsLoadCount >= 400 || UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == -1 || UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == Integer.MAX_VALUE)) {
            loadDialogs(0, 100, false);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private void migrateDialogs(int offset, int offsetDate, int offsetUser, int offsetChat, int offsetChannel, long accessPeer) {
        if (!this.migratingDialogs && offset != -1) {
            this.migratingDialogs = true;
            TL_messages_getDialogs req = new TL_messages_getDialogs();
            req.exclude_pinned = true;
            req.limit = 100;
            req.offset_id = offset;
            req.offset_date = offsetDate;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start migrate with id " + offset + " date " + LocaleController.getInstance().formatterStats.format(((long) offsetDate) * 1000));
            }
            if (offset == 0) {
                req.offset_peer = new TL_inputPeerEmpty();
            } else {
                if (offsetChannel != 0) {
                    req.offset_peer = new TL_inputPeerChannel();
                    req.offset_peer.channel_id = offsetChannel;
                } else if (offsetUser != 0) {
                    req.offset_peer = new TL_inputPeerUser();
                    req.offset_peer.user_id = offsetUser;
                } else {
                    req.offset_peer = new TL_inputPeerChat();
                    req.offset_peer.chat_id = offsetChat;
                }
                req.offset_peer.access_hash = accessPeer;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$80(this, offset));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$migrateDialogs$123$MessagesController(int offset, TLObject response, TL_error error) {
        if (error == null) {
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new MessagesController$$Lambda$216(this, (messages_Dialogs) response, offset));
            return;
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$217(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$121$MessagesController(messages_Dialogs dialogsRes, int offset) {
        try {
            int a;
            Message message;
            int offsetId;
            TL_dialog dialog;
            long did;
            UserConfig instance = UserConfig.getInstance(this.currentAccount);
            instance.totalDialogsLoadCount += dialogsRes.dialogs.size();
            Message lastMessage = null;
            for (a = 0; a < dialogsRes.messages.size(); a++) {
                message = (Message) dialogsRes.messages.get(a);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("search migrate id " + message.id + " date " + LocaleController.getInstance().formatterStats.format(((long) message.date) * 1000));
                }
                if (lastMessage == null || message.date < lastMessage.date) {
                    lastMessage = message;
                }
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("migrate step with id " + lastMessage.id + " date " + LocaleController.getInstance().formatterStats.format(((long) lastMessage.date) * 1000));
            }
            if (dialogsRes.dialogs.size() >= 100) {
                offsetId = lastMessage.id;
            } else {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("migrate stop due to not 100 dialogs");
                }
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId = Integer.MAX_VALUE;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(this.currentAccount).migrateOffsetDate;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(this.currentAccount).migrateOffsetUserId;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(this.currentAccount).migrateOffsetChatId;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(this.currentAccount).migrateOffsetAccess;
                offsetId = -1;
            }
            StringBuilder stringBuilder = new StringBuilder(dialogsRes.dialogs.size() * 12);
            LongSparseArray<TL_dialog> dialogHashMap = new LongSparseArray();
            for (a = 0; a < dialogsRes.dialogs.size(); a++) {
                dialog = (TL_dialog) dialogsRes.dialogs.get(a);
                if (dialog.peer.channel_id != 0) {
                    dialog.id = (long) (-dialog.peer.channel_id);
                } else if (dialog.peer.chat_id != 0) {
                    dialog.id = (long) (-dialog.peer.chat_id);
                } else {
                    dialog.id = (long) dialog.peer.user_id;
                }
                if (stringBuilder.length() > 0) {
                    stringBuilder.append(",");
                }
                stringBuilder.append(dialog.id);
                dialogHashMap.put(dialog.id, dialog);
            }
            SQLiteCursor cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE did IN (%s)", new Object[]{stringBuilder.toString()}), new Object[0]);
            while (cursor.next()) {
                did = cursor.longValue(0);
                dialog = (TL_dialog) dialogHashMap.get(did);
                dialogHashMap.remove(did);
                if (dialog != null) {
                    dialogsRes.dialogs.remove(dialog);
                    a = 0;
                    while (a < dialogsRes.messages.size()) {
                        message = (Message) dialogsRes.messages.get(a);
                        if (MessageObject.getDialogId(message) == did) {
                            dialogsRes.messages.remove(a);
                            a--;
                            if (message.id == dialog.top_message) {
                                dialog.top_message = 0;
                                break;
                            }
                        }
                        a++;
                    }
                }
            }
            cursor.dispose();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("migrate found missing dialogs " + dialogsRes.dialogs.size());
            }
            cursor = MessagesStorage.getInstance(this.currentAccount).getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
            if (cursor.next()) {
                int date = Math.max(NUM, cursor.intValue(0));
                a = 0;
                while (a < dialogsRes.messages.size()) {
                    message = (Message) dialogsRes.messages.get(a);
                    if (message.date < date) {
                        if (offset != -1) {
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId = UserConfig.getInstance(this.currentAccount).migrateOffsetId;
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(this.currentAccount).migrateOffsetDate;
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(this.currentAccount).migrateOffsetUserId;
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(this.currentAccount).migrateOffsetChatId;
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId;
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(this.currentAccount).migrateOffsetAccess;
                            offsetId = -1;
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("migrate stop due to reached loaded dialogs " + LocaleController.getInstance().formatterStats.format(((long) date) * 1000));
                            }
                        }
                        dialogsRes.messages.remove(a);
                        a--;
                        did = MessageObject.getDialogId(message);
                        dialog = (TL_dialog) dialogHashMap.get(did);
                        dialogHashMap.remove(did);
                        if (dialog != null) {
                            dialogsRes.dialogs.remove(dialog);
                        }
                    }
                    a++;
                }
                if (!(lastMessage == null || lastMessage.date >= date || offset == -1)) {
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId = UserConfig.getInstance(this.currentAccount).migrateOffsetId;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(this.currentAccount).migrateOffsetDate;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(this.currentAccount).migrateOffsetUserId;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(this.currentAccount).migrateOffsetChatId;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(this.currentAccount).migrateOffsetAccess;
                    offsetId = -1;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("migrate stop due to reached loaded dialogs " + LocaleController.getInstance().formatterStats.format(((long) date) * 1000));
                    }
                }
            }
            cursor.dispose();
            UserConfig.getInstance(this.currentAccount).migrateOffsetDate = lastMessage.date;
            Chat chat;
            if (lastMessage.to_id.channel_id != 0) {
                UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId = lastMessage.to_id.channel_id;
                UserConfig.getInstance(this.currentAccount).migrateOffsetChatId = 0;
                UserConfig.getInstance(this.currentAccount).migrateOffsetUserId = 0;
                for (a = 0; a < dialogsRes.chats.size(); a++) {
                    chat = (Chat) dialogsRes.chats.get(a);
                    if (chat.id == UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId) {
                        UserConfig.getInstance(this.currentAccount).migrateOffsetAccess = chat.access_hash;
                        break;
                    }
                }
            } else if (lastMessage.to_id.chat_id != 0) {
                UserConfig.getInstance(this.currentAccount).migrateOffsetChatId = lastMessage.to_id.chat_id;
                UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId = 0;
                UserConfig.getInstance(this.currentAccount).migrateOffsetUserId = 0;
                for (a = 0; a < dialogsRes.chats.size(); a++) {
                    chat = (Chat) dialogsRes.chats.get(a);
                    if (chat.id == UserConfig.getInstance(this.currentAccount).migrateOffsetChatId) {
                        UserConfig.getInstance(this.currentAccount).migrateOffsetAccess = chat.access_hash;
                        break;
                    }
                }
            } else if (lastMessage.to_id.user_id != 0) {
                UserConfig.getInstance(this.currentAccount).migrateOffsetUserId = lastMessage.to_id.user_id;
                UserConfig.getInstance(this.currentAccount).migrateOffsetChatId = 0;
                UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId = 0;
                for (a = 0; a < dialogsRes.users.size(); a++) {
                    User user = (User) dialogsRes.users.get(a);
                    if (user.id == UserConfig.getInstance(this.currentAccount).migrateOffsetUserId) {
                        UserConfig.getInstance(this.currentAccount).migrateOffsetAccess = user.access_hash;
                        break;
                    }
                }
            }
            processLoadedDialogs(dialogsRes, null, offsetId, 0, 0, false, true, false);
        } catch (Exception e) {
            FileLog.e(e);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$218(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$120$MessagesController() {
        this.migratingDialogs = false;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$122$MessagesController() {
        this.migratingDialogs = false;
    }

    public void processLoadedDialogs(messages_Dialogs dialogsRes, ArrayList<EncryptedChat> encChats, int offset, int count, int loadType, boolean resetEnd, boolean migrate, boolean fromCache) {
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$81(this, loadType, dialogsRes, resetEnd, count, encChats, offset, fromCache, migrate));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processLoadedDialogs$126$MessagesController(int loadType, messages_Dialogs dialogsRes, boolean resetEnd, int count, ArrayList encChats, int offset, boolean fromCache, boolean migrate) {
        if (!this.firstGettingTask) {
            getNewDeleteTask(null, 0);
            this.firstGettingTask = true;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("loaded loadType " + loadType + " count " + dialogsRes.dialogs.size());
        }
        if (loadType == 1 && dialogsRes.dialogs.size() == 0) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$214(this, dialogsRes, resetEnd, count));
            return;
        }
        int a;
        Message message;
        Chat chat;
        User user;
        Integer value;
        LongSparseArray<TL_dialog> new_dialogs_dict = new LongSparseArray();
        LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray();
        SparseArray usersDict = new SparseArray();
        SparseArray chatsDict = new SparseArray();
        for (a = 0; a < dialogsRes.users.size(); a++) {
            User u = (User) dialogsRes.users.get(a);
            usersDict.put(u.id, u);
        }
        for (a = 0; a < dialogsRes.chats.size(); a++) {
            Chat c = (Chat) dialogsRes.chats.get(a);
            chatsDict.put(c.id, c);
        }
        SparseArray<EncryptedChat> enc_chats_dict;
        if (encChats != null) {
            enc_chats_dict = new SparseArray();
            int N = encChats.size();
            for (a = 0; a < N; a++) {
                EncryptedChat encryptedChat = (EncryptedChat) encChats.get(a);
                enc_chats_dict.put(encryptedChat.id, encryptedChat);
            }
        } else {
            enc_chats_dict = null;
        }
        if (loadType == 1) {
            this.nextDialogsCacheOffset = offset + count;
        }
        Message lastMessage = null;
        for (a = 0; a < dialogsRes.messages.size(); a++) {
            message = (Message) dialogsRes.messages.get(a);
            if (lastMessage == null || message.date < lastMessage.date) {
                lastMessage = message;
            }
            MessageObject messageObject;
            if (message.to_id.channel_id != 0) {
                chat = (Chat) chatsDict.get(message.to_id.channel_id);
                if (chat == null || !chat.left || (this.proxyDialogId != 0 && this.proxyDialogId == ((long) (-chat.id)))) {
                    if (chat != null && chat.megagroup) {
                        message.flags |= Integer.MIN_VALUE;
                    }
                    messageObject = new MessageObject(this.currentAccount, message, usersDict, chatsDict, false);
                    new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                }
            } else {
                if (message.to_id.chat_id != 0) {
                    chat = (Chat) chatsDict.get(message.to_id.chat_id);
                    if (!(chat == null || chat.migrated_to == null)) {
                    }
                }
                messageObject = new MessageObject(this.currentAccount, message, usersDict, chatsDict, false);
                new_dialogMessage.put(messageObject.getDialogId(), messageObject);
            }
        }
        if (!(fromCache || migrate || UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == -1 || loadType != 0)) {
            if (lastMessage == null || lastMessage.id == UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId) {
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId = Integer.MAX_VALUE;
            } else {
                UserConfig instance = UserConfig.getInstance(this.currentAccount);
                instance.totalDialogsLoadCount += dialogsRes.dialogs.size();
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId = lastMessage.id;
                UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate = lastMessage.date;
                if (lastMessage.to_id.channel_id != 0) {
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = lastMessage.to_id.channel_id;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = 0;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = 0;
                    for (a = 0; a < dialogsRes.chats.size(); a++) {
                        chat = (Chat) dialogsRes.chats.get(a);
                        if (chat.id == UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId) {
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = chat.access_hash;
                            break;
                        }
                    }
                } else if (lastMessage.to_id.chat_id != 0) {
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = lastMessage.to_id.chat_id;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = 0;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = 0;
                    for (a = 0; a < dialogsRes.chats.size(); a++) {
                        chat = (Chat) dialogsRes.chats.get(a);
                        if (chat.id == UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId) {
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = chat.access_hash;
                            break;
                        }
                    }
                } else if (lastMessage.to_id.user_id != 0) {
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId = lastMessage.to_id.user_id;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId = 0;
                    UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId = 0;
                    for (a = 0; a < dialogsRes.users.size(); a++) {
                        user = (User) dialogsRes.users.get(a);
                        if (user.id == UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId) {
                            UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess = user.access_hash;
                            break;
                        }
                    }
                }
            }
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        ArrayList<TL_dialog> dialogsToReload = new ArrayList();
        for (a = 0; a < dialogsRes.dialogs.size(); a++) {
            TL_dialog d = (TL_dialog) dialogsRes.dialogs.get(a);
            if (d.id == 0 && d.peer != null) {
                if (d.peer.user_id != 0) {
                    d.id = (long) d.peer.user_id;
                } else if (d.peer.chat_id != 0) {
                    d.id = (long) (-d.peer.chat_id);
                } else if (d.peer.channel_id != 0) {
                    d.id = (long) (-d.peer.channel_id);
                }
            }
            if (d.id != 0) {
                int high_id = (int) (d.id >> 32);
                if (((int) d.id) != 0 || enc_chats_dict == null || enc_chats_dict.get(high_id) != null) {
                    if (this.proxyDialogId != 0 && this.proxyDialogId == d.id) {
                        this.proxyDialog = d;
                    }
                    if (d.last_message_date == 0) {
                        MessageObject mess = (MessageObject) new_dialogMessage.get(d.id);
                        if (mess != null) {
                            d.last_message_date = mess.messageOwner.date;
                        }
                    }
                    boolean allowCheck = true;
                    if (DialogObject.isChannel(d)) {
                        chat = (Chat) chatsDict.get(-((int) d.id));
                        if (chat != null) {
                            if (!chat.megagroup) {
                                allowCheck = false;
                            }
                            if (chat.left) {
                                if (this.proxyDialogId != 0) {
                                    if (this.proxyDialogId != d.id) {
                                    }
                                }
                            }
                        }
                        this.channelsPts.put(-((int) d.id), d.pts);
                    } else if (((int) d.id) < 0) {
                        chat = (Chat) chatsDict.get(-((int) d.id));
                        if (!(chat == null || chat.migrated_to == null)) {
                        }
                    }
                    new_dialogs_dict.put(d.id, d);
                    if (allowCheck && loadType == 1 && ((d.read_outbox_max_id == 0 || d.read_inbox_max_id == 0) && d.top_message != 0)) {
                        dialogsToReload.add(d);
                    }
                    value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                    value = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                    if (value == null) {
                        value = Integer.valueOf(0);
                    }
                    this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                }
            }
        }
        if (loadType != 1) {
            ImageLoader.saveMessagesThumbs(dialogsRes.messages);
            for (a = 0; a < dialogsRes.messages.size(); a++) {
                message = (Message) dialogsRes.messages.get(a);
                if (message.action instanceof TL_messageActionChatDeleteUser) {
                    user = (User) usersDict.get(message.action.user_id);
                    if (user != null && user.bot) {
                        message.reply_markup = new TL_replyKeyboardHide();
                        message.flags |= 64;
                    }
                }
                if ((message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate)) {
                    message.unread = false;
                    message.media_unread = false;
                } else {
                    boolean z;
                    ConcurrentHashMap<Long, Integer> read_max = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                    value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                    if (value == null) {
                        value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                        read_max.put(Long.valueOf(message.dialog_id), value);
                    }
                    if (value.intValue() < message.id) {
                        z = true;
                    } else {
                        z = false;
                    }
                    message.unread = z;
                }
            }
            MessagesStorage.getInstance(this.currentAccount).putDialogs(dialogsRes, 0);
        }
        if (loadType == 2) {
            chat = (Chat) dialogsRes.chats.get(0);
            getChannelDifference(chat.id);
            checkChannelInviter(chat.id);
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$215(this, loadType, dialogsRes, encChats, migrate, new_dialogs_dict, new_dialogMessage, chatsDict, count, fromCache, offset, dialogsToReload));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$124$MessagesController(messages_Dialogs dialogsRes, boolean resetEnd, int count) {
        putUsers(dialogsRes.users, true);
        this.loadingDialogs = false;
        if (resetEnd) {
            this.dialogsEndReached = false;
            this.serverDialogsEndReached = false;
        } else if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == Integer.MAX_VALUE) {
            this.dialogsEndReached = true;
            this.serverDialogsEndReached = true;
        } else {
            loadDialogs(0, count, false);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$125$MessagesController(int loadType, messages_Dialogs dialogsRes, ArrayList encChats, boolean migrate, LongSparseArray new_dialogs_dict, LongSparseArray new_dialogMessage, SparseArray chatsDict, int count, boolean fromCache, int offset, ArrayList dialogsToReload) {
        int a;
        if (loadType != 1) {
            applyDialogsNotificationsSettings(dialogsRes.dialogs);
            if (!UserConfig.getInstance(this.currentAccount).draftsLoaded) {
                DataQuery.getInstance(this.currentAccount).loadDrafts();
            }
        }
        putUsers(dialogsRes.users, loadType == 1);
        putChats(dialogsRes.chats, loadType == 1);
        if (encChats != null) {
            for (a = 0; a < encChats.size(); a++) {
                EncryptedChat encryptedChat = (EncryptedChat) encChats.get(a);
                if ((encryptedChat instanceof TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(encryptedChat.layer) < 73) {
                    SecretChatHelper.getInstance(this.currentAccount).sendNotifyLayerMessage(encryptedChat, null);
                }
                putEncryptedChat(encryptedChat, true);
            }
        }
        if (!migrate) {
            this.loadingDialogs = false;
        }
        boolean added = false;
        int lastDialogDate = (!migrate || this.dialogs.isEmpty()) ? 0 : ((TL_dialog) this.dialogs.get(this.dialogs.size() - 1)).last_message_date;
        for (a = 0; a < new_dialogs_dict.size(); a++) {
            long key = new_dialogs_dict.keyAt(a);
            TL_dialog value = (TL_dialog) new_dialogs_dict.valueAt(a);
            if (!migrate || lastDialogDate == 0 || value.last_message_date >= lastDialogDate) {
                TL_dialog currentDialog = (TL_dialog) this.dialogs_dict.get(key);
                if (loadType != 1 && (value.draft instanceof TL_draftMessage)) {
                    DataQuery.getInstance(this.currentAccount).saveDraft(value.id, value.draft, null, false);
                }
                MessageObject messageObject;
                if (currentDialog == null) {
                    added = true;
                    this.dialogs_dict.put(key, value);
                    messageObject = (MessageObject) new_dialogMessage.get(value.id);
                    this.dialogMessage.put(key, messageObject);
                    if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                        if (messageObject.messageOwner.random_id != 0) {
                            this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                        }
                    }
                } else {
                    if (loadType != 1) {
                        currentDialog.notify_settings = value.notify_settings;
                    }
                    currentDialog.pinned = value.pinned;
                    currentDialog.pinnedNum = value.pinnedNum;
                    MessageObject oldMsg = (MessageObject) this.dialogMessage.get(key);
                    if ((oldMsg == null || !oldMsg.deleted) && oldMsg != null && currentDialog.top_message <= 0) {
                        MessageObject newMsg = (MessageObject) new_dialogMessage.get(value.id);
                        if (oldMsg.deleted || newMsg == null || newMsg.messageOwner.date > oldMsg.messageOwner.date) {
                            this.dialogs_dict.put(key, value);
                            this.dialogMessage.put(key, newMsg);
                            if (newMsg != null && newMsg.messageOwner.to_id.channel_id == 0) {
                                this.dialogMessagesByIds.put(newMsg.getId(), newMsg);
                                if (!(newMsg == null || newMsg.messageOwner.random_id == 0)) {
                                    this.dialogMessagesByRandomIds.put(newMsg.messageOwner.random_id, newMsg);
                                }
                            }
                            this.dialogMessagesByIds.remove(oldMsg.getId());
                            if (oldMsg.messageOwner.random_id != 0) {
                                this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                            }
                        }
                    } else if (value.top_message >= currentDialog.top_message) {
                        this.dialogs_dict.put(key, value);
                        messageObject = (MessageObject) new_dialogMessage.get(value.id);
                        this.dialogMessage.put(key, messageObject);
                        if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                            this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                            if (!(messageObject == null || messageObject.messageOwner.random_id == 0)) {
                                this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                            }
                        }
                        if (oldMsg != null) {
                            this.dialogMessagesByIds.remove(oldMsg.getId());
                            if (oldMsg.messageOwner.random_id != 0) {
                                this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                            }
                        }
                    }
                }
            }
        }
        this.dialogs.clear();
        int size = this.dialogs_dict.size();
        for (a = 0; a < size; a++) {
            this.dialogs.add(this.dialogs_dict.valueAt(a));
        }
        if (!migrate) {
            chatsDict = null;
        }
        sortDialogs(chatsDict);
        if (!(loadType == 2 || migrate)) {
            boolean z = (dialogsRes.dialogs.size() == 0 || dialogsRes.dialogs.size() != count) && loadType == 0;
            this.dialogsEndReached = z;
            if (!fromCache) {
                z = (dialogsRes.dialogs.size() == 0 || dialogsRes.dialogs.size() != count) && loadType == 0;
                this.serverDialogsEndReached = z;
            }
        }
        if (!(fromCache || migrate || UserConfig.getInstance(this.currentAccount).totalDialogsLoadCount >= 400 || UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == -1 || UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == Integer.MAX_VALUE)) {
            loadDialogs(0, 100, false);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        if (migrate) {
            UserConfig.getInstance(this.currentAccount).migrateOffsetId = offset;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            this.migratingDialogs = false;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
        } else {
            generateUpdateMessage();
            if (!added && loadType == 1) {
                loadDialogs(0, count, false);
            }
        }
        migrateDialogs(UserConfig.getInstance(this.currentAccount).migrateOffsetId, UserConfig.getInstance(this.currentAccount).migrateOffsetDate, UserConfig.getInstance(this.currentAccount).migrateOffsetUserId, UserConfig.getInstance(this.currentAccount).migrateOffsetChatId, UserConfig.getInstance(this.currentAccount).migrateOffsetChannelId, UserConfig.getInstance(this.currentAccount).migrateOffsetAccess);
        if (!dialogsToReload.isEmpty()) {
            reloadDialogsReadValue(dialogsToReload, 0);
        }
        loadUnreadDialogs();
    }

    private void applyDialogNotificationsSettings(long dialog_id, PeerNotifySettings notify_settings) {
        if (notify_settings != null) {
            int currentValue = this.notificationsPreferences.getInt("notify2_" + dialog_id, -1);
            int currentValue2 = this.notificationsPreferences.getInt("notifyuntil_" + dialog_id, 0);
            Editor editor = this.notificationsPreferences.edit();
            boolean updated = false;
            TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(dialog_id);
            if (dialog != null) {
                dialog.notify_settings = notify_settings;
            }
            if ((notify_settings.flags & 2) != 0) {
                editor.putBoolean("silent_" + dialog_id, notify_settings.silent);
            } else {
                editor.remove("silent_" + dialog_id);
            }
            if ((notify_settings.flags & 4) == 0) {
                if (currentValue != -1) {
                    updated = true;
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = 0;
                    }
                    editor.remove("notify2_" + dialog_id);
                }
                MessagesStorage.getInstance(this.currentAccount).setDialogFlags(dialog_id, 0);
            } else if (notify_settings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                int until = 0;
                if (notify_settings.mute_until <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000) {
                    if (!(currentValue == 3 && currentValue2 == notify_settings.mute_until)) {
                        updated = true;
                        editor.putInt("notify2_" + dialog_id, 3);
                        editor.putInt("notifyuntil_" + dialog_id, notify_settings.mute_until);
                        if (dialog != null) {
                            dialog.notify_settings.mute_until = 0;
                        }
                    }
                    until = notify_settings.mute_until;
                } else if (currentValue != 2) {
                    updated = true;
                    editor.putInt("notify2_" + dialog_id, 2);
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                    }
                }
                MessagesStorage.getInstance(this.currentAccount).setDialogFlags(dialog_id, (((long) until) << 32) | 1);
                NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(dialog_id);
            } else {
                if (!(currentValue == 0 || currentValue == 1)) {
                    updated = true;
                    if (dialog != null) {
                        dialog.notify_settings.mute_until = 0;
                    }
                    editor.putInt("notify2_" + dialog_id, 0);
                }
                MessagesStorage.getInstance(this.currentAccount).setDialogFlags(dialog_id, 0);
            }
            editor.commit();
            if (updated) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
            }
        }
    }

    private void applyDialogsNotificationsSettings(ArrayList<TL_dialog> dialogs) {
        Editor editor = null;
        for (int a = 0; a < dialogs.size(); a++) {
            TL_dialog dialog = (TL_dialog) dialogs.get(a);
            if (dialog.peer != null && (dialog.notify_settings instanceof TL_peerNotifySettings)) {
                int dialog_id;
                if (editor == null) {
                    editor = this.notificationsPreferences.edit();
                }
                if (dialog.peer.user_id != 0) {
                    dialog_id = dialog.peer.user_id;
                } else if (dialog.peer.chat_id != 0) {
                    dialog_id = -dialog.peer.chat_id;
                } else {
                    dialog_id = -dialog.peer.channel_id;
                }
                if ((dialog.notify_settings.flags & 2) != 0) {
                    editor.putBoolean("silent_" + dialog_id, dialog.notify_settings.silent);
                } else {
                    editor.remove("silent_" + dialog_id);
                }
                if ((dialog.notify_settings.flags & 4) == 0) {
                    editor.remove("notify2_" + dialog_id);
                } else if (dialog.notify_settings.mute_until <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                    editor.putInt("notify2_" + dialog_id, 0);
                } else if (dialog.notify_settings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000) {
                    editor.putInt("notify2_" + dialog_id, 2);
                    dialog.notify_settings.mute_until = Integer.MAX_VALUE;
                } else {
                    editor.putInt("notify2_" + dialog_id, 3);
                    editor.putInt("notifyuntil_" + dialog_id, dialog.notify_settings.mute_until);
                }
            }
        }
        if (editor != null) {
            editor.commit();
        }
    }

    public void reloadMentionsCountForChannels(ArrayList<Integer> arrayList) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$82(this, arrayList));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$reloadMentionsCountForChannels$129$MessagesController(ArrayList arrayList) {
        for (int a = 0; a < arrayList.size(); a++) {
            long dialog_id = (long) (-((Integer) arrayList.get(a)).intValue());
            TL_messages_getUnreadMentions req = new TL_messages_getUnreadMentions();
            req.peer = getInputPeer((int) dialog_id);
            req.limit = 1;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$212(this, dialog_id));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$128$MessagesController(long dialog_id, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$213(this, response, dialog_id));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$127$MessagesController(TLObject response, long dialog_id) {
        messages_Messages res = (messages_Messages) response;
        if (res != null) {
            int newCount;
            if (res.count != 0) {
                newCount = res.count;
            } else {
                newCount = res.messages.size();
            }
            MessagesStorage.getInstance(this.currentAccount).resetMentionsCount(dialog_id, newCount);
        }
    }

    public void processDialogsUpdateRead(LongSparseArray<Integer> dialogsToUpdate, LongSparseArray<Integer> dialogsMentionsToUpdate) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$83(this, dialogsToUpdate, dialogsMentionsToUpdate));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processDialogsUpdateRead$130$MessagesController(LongSparseArray dialogsToUpdate, LongSparseArray dialogsMentionsToUpdate) {
        int a;
        TL_dialog currentDialog;
        if (dialogsToUpdate != null) {
            for (a = 0; a < dialogsToUpdate.size(); a++) {
                long dialogId = dialogsToUpdate.keyAt(a);
                currentDialog = (TL_dialog) this.dialogs_dict.get(dialogId);
                if (currentDialog != null) {
                    int prevCount = currentDialog.unread_count;
                    currentDialog.unread_count = ((Integer) dialogsToUpdate.valueAt(a)).intValue();
                    if (prevCount != 0 && currentDialog.unread_count == 0 && !isDialogMuted(dialogId)) {
                        this.unreadUnmutedDialogs--;
                    } else if (!(prevCount != 0 || currentDialog.unread_mark || currentDialog.unread_count == 0 || isDialogMuted(dialogId))) {
                        this.unreadUnmutedDialogs++;
                    }
                }
            }
        }
        if (dialogsMentionsToUpdate != null) {
            for (a = 0; a < dialogsMentionsToUpdate.size(); a++) {
                currentDialog = (TL_dialog) this.dialogs_dict.get(dialogsMentionsToUpdate.keyAt(a));
                if (currentDialog != null) {
                    currentDialog.unread_mentions_count = ((Integer) dialogsMentionsToUpdate.valueAt(a)).intValue();
                    if (this.createdDialogMainThreadIds.contains(Long.valueOf(currentDialog.id))) {
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(currentDialog.id), Integer.valueOf(currentDialog.unread_mentions_count));
                    }
                }
            }
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
        if (dialogsToUpdate != null) {
            NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
        }
    }

    /* Access modifiers changed, original: protected */
    public void checkLastDialogMessage(TL_dialog dialog, InputPeer peer, long taskId) {
        Throwable e;
        long newTaskId;
        int lower_id = (int) dialog.id;
        if (lower_id != 0 && this.checkingLastMessagesDialogs.indexOfKey(lower_id) < 0) {
            InputPeer inputPeer;
            TL_messages_getHistory req = new TL_messages_getHistory();
            if (peer == null) {
                inputPeer = getInputPeer(lower_id);
            } else {
                inputPeer = peer;
            }
            req.peer = inputPeer;
            if (req.peer != null && !(req.peer instanceof TL_inputPeerChannel)) {
                req.limit = 1;
                this.checkingLastMessagesDialogs.put(lower_id, true);
                if (taskId == 0) {
                    NativeByteBuffer data = null;
                    try {
                        NativeByteBuffer data2 = new NativeByteBuffer(req.peer.getObjectSize() + 48);
                        try {
                            data2.writeInt32(10);
                            data2.writeInt64(dialog.id);
                            data2.writeInt32(dialog.top_message);
                            data2.writeInt32(dialog.read_inbox_max_id);
                            data2.writeInt32(dialog.read_outbox_max_id);
                            data2.writeInt32(dialog.unread_count);
                            data2.writeInt32(dialog.last_message_date);
                            data2.writeInt32(dialog.pts);
                            data2.writeInt32(dialog.flags);
                            data2.writeBool(dialog.pinned);
                            data2.writeInt32(dialog.pinnedNum);
                            data2.writeInt32(dialog.unread_mentions_count);
                            data2.writeBool(dialog.unread_mark);
                            peer.serializeToStream(data2);
                            data = data2;
                        } catch (Exception e2) {
                            e = e2;
                            data = data2;
                            FileLog.e(e);
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$84(this, lower_id, dialog, newTaskId));
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$84(this, lower_id, dialog, newTaskId));
                    }
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                } else {
                    newTaskId = taskId;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$84(this, lower_id, dialog, newTaskId));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkLastDialogMessage$133$MessagesController(int lower_id, TL_dialog dialog, long newTaskId, TLObject response, TL_error error) {
        if (response != null) {
            messages_Messages res = (messages_Messages) response;
            removeDeletedMessagesFromArray((long) lower_id, res.messages);
            if (res.messages.isEmpty()) {
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$210(this, dialog));
            } else {
                TL_messages_dialogs dialogs = new TL_messages_dialogs();
                Message newMessage = (Message) res.messages.get(0);
                TL_dialog newDialog = new TL_dialog();
                newDialog.flags = dialog.flags;
                newDialog.top_message = newMessage.id;
                newDialog.last_message_date = newMessage.date;
                newDialog.notify_settings = dialog.notify_settings;
                newDialog.pts = dialog.pts;
                newDialog.unread_count = dialog.unread_count;
                newDialog.unread_mark = dialog.unread_mark;
                newDialog.unread_mentions_count = dialog.unread_mentions_count;
                newDialog.read_inbox_max_id = dialog.read_inbox_max_id;
                newDialog.read_outbox_max_id = dialog.read_outbox_max_id;
                newDialog.pinned = dialog.pinned;
                newDialog.pinnedNum = dialog.pinnedNum;
                long j = dialog.id;
                newDialog.id = j;
                newMessage.dialog_id = j;
                dialogs.users.addAll(res.users);
                dialogs.chats.addAll(res.chats);
                dialogs.dialogs.add(newDialog);
                dialogs.messages.addAll(res.messages);
                dialogs.count = 1;
                processDialogsUpdate(dialogs, null);
                MessagesStorage.getInstance(this.currentAccount).putMessages(res.messages, true, true, false, DownloadController.getInstance(this.currentAccount).getAutodownloadMask(), true);
            }
        }
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$211(this, lower_id));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$131$MessagesController(TL_dialog dialog) {
        TL_dialog currentDialog = (TL_dialog) this.dialogs_dict.get(dialog.id);
        if (currentDialog != null && currentDialog.top_message == 0) {
            deleteDialog(dialog.id, 3);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$132$MessagesController(int lower_id) {
        this.checkingLastMessagesDialogs.delete(lower_id);
    }

    public void processDialogsUpdate(messages_Dialogs dialogsRes, ArrayList<EncryptedChat> arrayList) {
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$85(this, dialogsRes));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processDialogsUpdate$135$MessagesController(messages_Dialogs dialogsRes) {
        int a;
        Chat chat;
        LongSparseArray<TL_dialog> new_dialogs_dict = new LongSparseArray();
        LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray();
        SparseArray usersDict = new SparseArray(dialogsRes.users.size());
        SparseArray chatsDict = new SparseArray(dialogsRes.chats.size());
        LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray();
        for (a = 0; a < dialogsRes.users.size(); a++) {
            User u = (User) dialogsRes.users.get(a);
            usersDict.put(u.id, u);
        }
        for (a = 0; a < dialogsRes.chats.size(); a++) {
            Chat c = (Chat) dialogsRes.chats.get(a);
            chatsDict.put(c.id, c);
        }
        for (a = 0; a < dialogsRes.messages.size(); a++) {
            Message message = (Message) dialogsRes.messages.get(a);
            if (this.proxyDialogId == 0 || this.proxyDialogId != message.dialog_id) {
                if (message.to_id.channel_id != 0) {
                    chat = (Chat) chatsDict.get(message.to_id.channel_id);
                    if (chat != null && chat.left) {
                    }
                } else if (message.to_id.chat_id != 0) {
                    chat = (Chat) chatsDict.get(message.to_id.chat_id);
                    if (!(chat == null || chat.migrated_to == null)) {
                    }
                }
            }
            MessageObject messageObject = new MessageObject(this.currentAccount, message, usersDict, chatsDict, false);
            new_dialogMessage.put(messageObject.getDialogId(), messageObject);
        }
        for (a = 0; a < dialogsRes.dialogs.size(); a++) {
            TL_dialog d = (TL_dialog) dialogsRes.dialogs.get(a);
            if (d.id == 0) {
                if (d.peer.user_id != 0) {
                    d.id = (long) d.peer.user_id;
                } else if (d.peer.chat_id != 0) {
                    d.id = (long) (-d.peer.chat_id);
                } else if (d.peer.channel_id != 0) {
                    d.id = (long) (-d.peer.channel_id);
                }
            }
            if (this.proxyDialogId == 0 || this.proxyDialogId != d.id) {
                if (DialogObject.isChannel(d)) {
                    chat = (Chat) chatsDict.get(-((int) d.id));
                    if (chat != null && chat.left) {
                    }
                } else if (((int) d.id) < 0) {
                    chat = (Chat) chatsDict.get(-((int) d.id));
                    if (!(chat == null || chat.migrated_to == null)) {
                    }
                }
            }
            if (d.last_message_date == 0) {
                MessageObject mess = (MessageObject) new_dialogMessage.get(d.id);
                if (mess != null) {
                    d.last_message_date = mess.messageOwner.date;
                }
            }
            new_dialogs_dict.put(d.id, d);
            dialogsToUpdate.put(d.id, Integer.valueOf(d.unread_count));
            Integer value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
            if (value == null) {
                value = Integer.valueOf(0);
            }
            this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
            value = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
            if (value == null) {
                value = Integer.valueOf(0);
            }
            this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$209(this, dialogsRes, new_dialogs_dict, new_dialogMessage, dialogsToUpdate));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$134$MessagesController(messages_Dialogs dialogsRes, LongSparseArray new_dialogs_dict, LongSparseArray new_dialogMessage, LongSparseArray dialogsToUpdate) {
        int a;
        putUsers(dialogsRes.users, true);
        putChats(dialogsRes.chats, true);
        for (a = 0; a < new_dialogs_dict.size(); a++) {
            long key = new_dialogs_dict.keyAt(a);
            TL_dialog value = (TL_dialog) new_dialogs_dict.valueAt(a);
            TL_dialog currentDialog = (TL_dialog) this.dialogs_dict.get(key);
            MessageObject messageObject;
            if (currentDialog == null) {
                this.nextDialogsCacheOffset++;
                this.dialogs_dict.put(key, value);
                messageObject = (MessageObject) new_dialogMessage.get(value.id);
                this.dialogMessage.put(key, messageObject);
                if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                    this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                    if (messageObject.messageOwner.random_id != 0) {
                        this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                    }
                }
            } else {
                currentDialog.unread_count = value.unread_count;
                if (currentDialog.unread_mentions_count != value.unread_mentions_count) {
                    currentDialog.unread_mentions_count = value.unread_mentions_count;
                    if (this.createdDialogMainThreadIds.contains(Long.valueOf(currentDialog.id))) {
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(currentDialog.id), Integer.valueOf(currentDialog.unread_mentions_count));
                    }
                }
                MessageObject oldMsg = (MessageObject) this.dialogMessage.get(key);
                if (oldMsg != null && currentDialog.top_message <= 0) {
                    MessageObject newMsg = (MessageObject) new_dialogMessage.get(value.id);
                    if (oldMsg.deleted || newMsg == null || newMsg.messageOwner.date > oldMsg.messageOwner.date) {
                        this.dialogs_dict.put(key, value);
                        this.dialogMessage.put(key, newMsg);
                        if (newMsg != null && newMsg.messageOwner.to_id.channel_id == 0) {
                            this.dialogMessagesByIds.put(newMsg.getId(), newMsg);
                            if (newMsg.messageOwner.random_id != 0) {
                                this.dialogMessagesByRandomIds.put(newMsg.messageOwner.random_id, newMsg);
                            }
                        }
                        this.dialogMessagesByIds.remove(oldMsg.getId());
                        if (oldMsg.messageOwner.random_id != 0) {
                            this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                        }
                    }
                } else if ((oldMsg != null && oldMsg.deleted) || value.top_message > currentDialog.top_message) {
                    this.dialogs_dict.put(key, value);
                    messageObject = (MessageObject) new_dialogMessage.get(value.id);
                    this.dialogMessage.put(key, messageObject);
                    if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                        if (messageObject.messageOwner.random_id != 0) {
                            this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                        }
                    }
                    if (oldMsg != null) {
                        this.dialogMessagesByIds.remove(oldMsg.getId());
                        if (oldMsg.messageOwner.random_id != 0) {
                            this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                        }
                    }
                    if (messageObject == null) {
                        checkLastDialogMessage(value, null, 0);
                    }
                }
            }
        }
        this.dialogs.clear();
        int size = this.dialogs_dict.size();
        for (a = 0; a < size; a++) {
            this.dialogs.add(this.dialogs_dict.valueAt(a));
        }
        sortDialogs(null);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
    }

    public void addToViewsQueue(MessageObject messageObject) {
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$86(this, messageObject));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addToViewsQueue$136$MessagesController(MessageObject messageObject) {
        int peer = (int) messageObject.getDialogId();
        int id = messageObject.getId();
        ArrayList<Integer> ids = (ArrayList) this.channelViewsToSend.get(peer);
        if (ids == null) {
            ids = new ArrayList();
            this.channelViewsToSend.put(peer, ids);
        }
        if (!ids.contains(Integer.valueOf(id))) {
            ids.add(Integer.valueOf(id));
        }
    }

    public void addToPollsQueue(long dialogId, ArrayList<MessageObject> visibleObjects) {
        int a;
        SparseArray<MessageObject> array = (SparseArray) this.pollsToCheck.get(dialogId);
        if (array == null) {
            array = new SparseArray();
            this.pollsToCheck.put(dialogId, array);
            this.pollsToCheckSize++;
        }
        int N = array.size();
        for (a = 0; a < N; a++) {
            ((MessageObject) array.valueAt(a)).pollVisibleOnScreen = false;
        }
        N = visibleObjects.size();
        for (a = 0; a < N; a++) {
            MessageObject messageObject = (MessageObject) visibleObjects.get(a);
            if (messageObject.type == 17) {
                int id = messageObject.getId();
                MessageObject object = (MessageObject) array.get(id);
                if (object != null) {
                    object.pollVisibleOnScreen = true;
                } else {
                    array.put(id, messageObject);
                }
            }
        }
    }

    public void markMessageContentAsRead(MessageObject messageObject) {
        ArrayList<Long> arrayList = new ArrayList();
        long messageId = (long) messageObject.getId();
        if (messageObject.messageOwner.to_id.channel_id != 0) {
            messageId |= ((long) messageObject.messageOwner.to_id.channel_id) << 32;
        }
        if (messageObject.messageOwner.mentioned) {
            MessagesStorage.getInstance(this.currentAccount).markMentionMessageAsRead(messageObject.getId(), messageObject.messageOwner.to_id.channel_id, messageObject.getDialogId());
        }
        arrayList.add(Long.valueOf(messageId));
        MessagesStorage.getInstance(this.currentAccount).markMessagesContentAsRead(arrayList, 0);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, arrayList);
        if (messageObject.getId() < 0) {
            markMessageAsRead(messageObject.getDialogId(), messageObject.messageOwner.random_id, Integer.MIN_VALUE);
        } else if (messageObject.messageOwner.to_id.channel_id != 0) {
            TL_channels_readMessageContents req = new TL_channels_readMessageContents();
            req.channel = getInputChannel(messageObject.messageOwner.to_id.channel_id);
            if (req.channel != null) {
                req.id.add(Integer.valueOf(messageObject.getId()));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, MessagesController$$Lambda$87.$instance);
            }
        } else {
            TL_messages_readMessageContents req2 = new TL_messages_readMessageContents();
            req2.id.add(Integer.valueOf(messageObject.getId()));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$88(this));
        }
    }

    static final /* synthetic */ void lambda$markMessageContentAsRead$137$MessagesController(TLObject response, TL_error error) {
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$markMessageContentAsRead$138$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
    }

    public void markMentionMessageAsRead(int mid, int channelId, long did) {
        MessagesStorage.getInstance(this.currentAccount).markMentionMessageAsRead(mid, channelId, did);
        if (channelId != 0) {
            TL_channels_readMessageContents req = new TL_channels_readMessageContents();
            req.channel = getInputChannel(channelId);
            if (req.channel != null) {
                req.id.add(Integer.valueOf(mid));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, MessagesController$$Lambda$89.$instance);
                return;
            }
            return;
        }
        TL_messages_readMessageContents req2 = new TL_messages_readMessageContents();
        req2.id.add(Integer.valueOf(mid));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new MessagesController$$Lambda$90(this));
    }

    static final /* synthetic */ void lambda$markMentionMessageAsRead$139$MessagesController(TLObject response, TL_error error) {
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$markMentionMessageAsRead$140$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:28:0x0094  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0068  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x0094  */
    public void markMessageAsRead(int r16, int r17, org.telegram.tgnet.TLRPC.InputChannel r18, int r19, long r20) {
        /*
        r15 = this;
        if (r16 == 0) goto L_0x0004;
    L_0x0002:
        if (r19 > 0) goto L_0x0005;
    L_0x0004:
        return;
    L_0x0005:
        if (r17 == 0) goto L_0x0011;
    L_0x0007:
        if (r18 != 0) goto L_0x0011;
    L_0x0009:
        r0 = r17;
        r18 = r15.getInputChannel(r0);
        if (r18 == 0) goto L_0x0004;
    L_0x0011:
        r2 = 0;
        r2 = (r20 > r2 ? 1 : (r20 == r2 ? 0 : -1));
        if (r2 != 0) goto L_0x0091;
    L_0x0017:
        r9 = 0;
        r10 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x008c }
        if (r18 == 0) goto L_0x008a;
    L_0x001c:
        r2 = r18.getObjectSize();	 Catch:{ Exception -> 0x008c }
    L_0x0020:
        r2 = r2 + 16;
        r10.<init>(r2);	 Catch:{ Exception -> 0x008c }
        r2 = 11;
        r10.writeInt32(r2);	 Catch:{ Exception -> 0x00b2 }
        r0 = r16;
        r10.writeInt32(r0);	 Catch:{ Exception -> 0x00b2 }
        r0 = r17;
        r10.writeInt32(r0);	 Catch:{ Exception -> 0x00b2 }
        r0 = r19;
        r10.writeInt32(r0);	 Catch:{ Exception -> 0x00b2 }
        if (r17 == 0) goto L_0x0040;
    L_0x003b:
        r0 = r18;
        r0.serializeToStream(r10);	 Catch:{ Exception -> 0x00b2 }
    L_0x0040:
        r9 = r10;
    L_0x0041:
        r2 = r15.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r12 = r2.createPendingTask(r9);
    L_0x004b:
        r2 = r15.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r5 = r2.getCurrentTime();
        r2 = r15.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r8 = 0;
        r3 = r16;
        r4 = r17;
        r6 = r5;
        r7 = r19;
        r2.createTaskForMid(r3, r4, r5, r6, r7, r8);
        if (r17 == 0) goto L_0x0094;
    L_0x0068:
        r14 = new org.telegram.tgnet.TLRPC$TL_channels_readMessageContents;
        r14.<init>();
        r0 = r18;
        r14.channel = r0;
        r2 = r14.id;
        r3 = java.lang.Integer.valueOf(r16);
        r2.add(r3);
        r2 = r15.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r3 = new org.telegram.messenger.MessagesController$$Lambda$91;
        r3.<init>(r15, r12);
        r2.sendRequest(r14, r3);
        goto L_0x0004;
    L_0x008a:
        r2 = 0;
        goto L_0x0020;
    L_0x008c:
        r11 = move-exception;
    L_0x008d:
        org.telegram.messenger.FileLog.e(r11);
        goto L_0x0041;
    L_0x0091:
        r12 = r20;
        goto L_0x004b;
    L_0x0094:
        r14 = new org.telegram.tgnet.TLRPC$TL_messages_readMessageContents;
        r14.<init>();
        r2 = r14.id;
        r3 = java.lang.Integer.valueOf(r16);
        r2.add(r3);
        r2 = r15.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r3 = new org.telegram.messenger.MessagesController$$Lambda$92;
        r3.<init>(r15, r12);
        r2.sendRequest(r14, r3);
        goto L_0x0004;
    L_0x00b2:
        r11 = move-exception;
        r9 = r10;
        goto L_0x008d;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.markMessageAsRead(int, int, org.telegram.tgnet.TLRPC$InputChannel, int, long):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$markMessageAsRead$141$MessagesController(long newTaskId, TLObject response, TL_error error) {
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$markMessageAsRead$142$MessagesController(long newTaskId, TLObject response, TL_error error) {
        if (error == null) {
            TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
    }

    public void markMessageAsRead(long dialog_id, long random_id, int ttl) {
        if (random_id != 0 && dialog_id != 0) {
            if (ttl > 0 || ttl == Integer.MIN_VALUE) {
                int high_id = (int) (dialog_id >> 32);
                if (((int) dialog_id) == 0) {
                    EncryptedChat chat = getEncryptedChat(Integer.valueOf(high_id));
                    if (chat != null) {
                        ArrayList<Long> random_ids = new ArrayList();
                        random_ids.add(Long.valueOf(random_id));
                        SecretChatHelper.getInstance(this.currentAccount).sendMessagesReadMessage(chat, random_ids, null);
                        if (ttl > 0) {
                            int time = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                            MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(chat.id, time, time, 0, random_ids);
                        }
                    }
                }
            }
        }
    }

    private void completeReadTask(ReadTask task) {
        int lower_part = (int) task.dialogId;
        int high_id = (int) (task.dialogId >> 32);
        if (lower_part != 0) {
            TLObject req;
            InputPeer inputPeer = getInputPeer(lower_part);
            TLObject request;
            if (inputPeer instanceof TL_inputPeerChannel) {
                request = new TL_channels_readHistory();
                request.channel = getInputChannel(-lower_part);
                request.max_id = task.maxId;
                req = request;
            } else {
                request = new TL_messages_readHistory();
                request.peer = inputPeer;
                request.max_id = task.maxId;
                req = request;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$93(this));
            return;
        }
        EncryptedChat chat = getEncryptedChat(Integer.valueOf(high_id));
        if (chat.auth_key != null && chat.auth_key.length > 1 && (chat instanceof TL_encryptedChat)) {
            TL_messages_readEncryptedHistory req2 = new TL_messages_readEncryptedHistory();
            req2.peer = new TL_inputEncryptedChat();
            req2.peer.chat_id = chat.id;
            req2.peer.access_hash = chat.access_hash;
            req2.max_date = task.maxDate;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, MessagesController$$Lambda$94.$instance);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$completeReadTask$143$MessagesController(TLObject response, TL_error error) {
        if (error == null && (response instanceof TL_messages_affectedMessages)) {
            TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
            processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
        }
    }

    static final /* synthetic */ void lambda$completeReadTask$144$MessagesController(TLObject response, TL_error error) {
    }

    private void checkReadTasks() {
        long time = SystemClock.elapsedRealtime();
        int a = 0;
        int size = this.readTasks.size();
        while (a < size) {
            ReadTask task = (ReadTask) this.readTasks.get(a);
            if (task.sendRequestTime <= time) {
                completeReadTask(task);
                this.readTasks.remove(a);
                this.readTasksMap.remove(task.dialogId);
                a--;
                size--;
            }
            a++;
        }
    }

    public void markDialogAsReadNow(long dialogId) {
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$95(this, dialogId));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$markDialogAsReadNow$145$MessagesController(long dialogId) {
        ReadTask currentReadTask = (ReadTask) this.readTasksMap.get(dialogId);
        if (currentReadTask != null) {
            completeReadTask(currentReadTask);
            this.readTasks.remove(currentReadTask);
            this.readTasksMap.remove(dialogId);
        }
    }

    public void markMentionsAsRead(long dialogId) {
        if (((int) dialogId) != 0) {
            MessagesStorage.getInstance(this.currentAccount).resetMentionsCount(dialogId, 0);
            TL_messages_readMentions req = new TL_messages_readMentions();
            req.peer = getInstance(this.currentAccount).getInputPeer((int) dialogId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, MessagesController$$Lambda$96.$instance);
        }
    }

    static final /* synthetic */ void lambda$markMentionsAsRead$146$MessagesController(TLObject response, TL_error error) {
    }

    public void markDialogAsRead(long dialogId, int maxPositiveId, int maxNegativeId, int maxDate, boolean popup, int countDiff, boolean readNow) {
        boolean createReadTask;
        int lower_part = (int) dialogId;
        int high_id = (int) (dialogId >> 32);
        if (lower_part != 0) {
            if (maxPositiveId != 0 && high_id != 1) {
                long maxMessageId = (long) maxPositiveId;
                long minMessageId = (long) maxNegativeId;
                boolean isChannel = false;
                if (lower_part < 0) {
                    if (ChatObject.isChannel(getChat(Integer.valueOf(-lower_part)))) {
                        maxMessageId |= ((long) (-lower_part)) << 32;
                        minMessageId |= ((long) (-lower_part)) << 32;
                        isChannel = true;
                    }
                }
                Integer value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialogId));
                if (value == null) {
                    value = Integer.valueOf(0);
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialogId), Integer.valueOf(Math.max(value.intValue(), maxPositiveId)));
                MessagesStorage.getInstance(this.currentAccount).processPendingRead(dialogId, maxMessageId, minMessageId, maxDate, isChannel);
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new MessagesController$$Lambda$97(this, dialogId, countDiff, maxPositiveId, popup));
                createReadTask = maxPositiveId != Integer.MAX_VALUE;
            } else {
                return;
            }
        } else if (maxDate != 0) {
            createReadTask = true;
            EncryptedChat chat = getEncryptedChat(Integer.valueOf(high_id));
            MessagesStorage.getInstance(this.currentAccount).processPendingRead(dialogId, (long) maxPositiveId, (long) maxNegativeId, maxDate, false);
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new MessagesController$$Lambda$98(this, dialogId, maxDate, popup, countDiff, maxNegativeId));
            if (chat != null && chat.ttl > 0) {
                int serverTime = Math.max(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime(), maxDate);
                MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(chat.id, serverTime, serverTime, 0, null);
            }
        } else {
            return;
        }
        if (createReadTask) {
            Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$99(this, dialogId, readNow, maxDate, maxPositiveId));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$markDialogAsRead$148$MessagesController(long dialogId, int countDiff, int maxPositiveId, boolean popup) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$208(this, dialogId, countDiff, maxPositiveId, popup));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$147$MessagesController(long dialogId, int countDiff, int maxPositiveId, boolean popup) {
        TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(dialogId);
        if (dialog != null) {
            int prevCount = dialog.unread_count;
            if (countDiff == 0 || maxPositiveId >= dialog.top_message) {
                dialog.unread_count = 0;
            } else {
                dialog.unread_count = Math.max(dialog.unread_count - countDiff, 0);
                if (maxPositiveId != Integer.MIN_VALUE && dialog.unread_count > dialog.top_message - maxPositiveId) {
                    dialog.unread_count = dialog.top_message - maxPositiveId;
                }
            }
            if ((prevCount != 0 || dialog.unread_mark) && dialog.unread_count == 0 && !isDialogMuted(dialogId)) {
                this.unreadUnmutedDialogs--;
            }
            if (dialog.unread_mark) {
                dialog.unread_mark = false;
                MessagesStorage.getInstance(this.currentAccount).setDialogUnread(dialog.id, false);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
        }
        LongSparseArray<Integer> dialogsToUpdate;
        if (popup) {
            NotificationsController.getInstance(this.currentAccount).processReadMessages(null, dialogId, 0, maxPositiveId, true);
            dialogsToUpdate = new LongSparseArray(1);
            dialogsToUpdate.put(dialogId, Integer.valueOf(-1));
            NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
            return;
        }
        NotificationsController.getInstance(this.currentAccount).processReadMessages(null, dialogId, 0, maxPositiveId, false);
        dialogsToUpdate = new LongSparseArray(1);
        dialogsToUpdate.put(dialogId, Integer.valueOf(0));
        NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$markDialogAsRead$150$MessagesController(long dialogId, int maxDate, boolean popup, int countDiff, int maxNegativeId) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$207(this, dialogId, maxDate, popup, countDiff, maxNegativeId));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$149$MessagesController(long dialogId, int maxDate, boolean popup, int countDiff, int maxNegativeId) {
        NotificationsController.getInstance(this.currentAccount).processReadMessages(null, dialogId, maxDate, 0, popup);
        TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(dialogId);
        if (dialog != null) {
            int prevCount = dialog.unread_count;
            if (countDiff == 0 || maxNegativeId <= dialog.top_message) {
                dialog.unread_count = 0;
            } else {
                dialog.unread_count = Math.max(dialog.unread_count - countDiff, 0);
                if (maxNegativeId != Integer.MAX_VALUE && dialog.unread_count > maxNegativeId - dialog.top_message) {
                    dialog.unread_count = maxNegativeId - dialog.top_message;
                }
            }
            if ((prevCount != 0 || dialog.unread_mark) && dialog.unread_count == 0 && !isDialogMuted(dialogId)) {
                this.unreadUnmutedDialogs--;
            }
            if (dialog.unread_mark) {
                dialog.unread_mark = false;
                MessagesStorage.getInstance(this.currentAccount).setDialogUnread(dialog.id, false);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
        }
        LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray(1);
        dialogsToUpdate.put(dialogId, Integer.valueOf(0));
        NotificationsController.getInstance(this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$markDialogAsRead$151$MessagesController(long dialogId, boolean readNow, int maxDate, int maxPositiveId) {
        ReadTask currentReadTask = (ReadTask) this.readTasksMap.get(dialogId);
        if (currentReadTask == null) {
            currentReadTask = new ReadTask(this, null);
            currentReadTask.dialogId = dialogId;
            currentReadTask.sendRequestTime = SystemClock.elapsedRealtime() + 5000;
            if (!readNow) {
                this.readTasksMap.put(dialogId, currentReadTask);
                this.readTasks.add(currentReadTask);
            }
        }
        currentReadTask.maxDate = maxDate;
        currentReadTask.maxId = maxPositiveId;
        if (readNow) {
            completeReadTask(currentReadTask);
        }
    }

    public int createChat(String title, ArrayList<Integer> selectedContacts, String about, int type, BaseFragment fragment) {
        int a;
        TLObject req;
        if (type == 1) {
            TL_chat chat = new TL_chat();
            chat.id = UserConfig.getInstance(this.currentAccount).lastBroadcastId;
            chat.title = title;
            chat.photo = new TL_chatPhotoEmpty();
            chat.participants_count = selectedContacts.size();
            chat.date = (int) (System.currentTimeMillis() / 1000);
            chat.version = 1;
            UserConfig instance = UserConfig.getInstance(this.currentAccount);
            instance.lastBroadcastId--;
            putChat(chat, false);
            ArrayList<Chat> chatsArrays = new ArrayList();
            chatsArrays.add(chat);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, chatsArrays, true, true);
            TL_chatFull chatFull = new TL_chatFull();
            chatFull.id = chat.id;
            chatFull.chat_photo = new TL_photoEmpty();
            chatFull.notify_settings = new TL_peerNotifySettingsEmpty_layer77();
            chatFull.exported_invite = new TL_chatInviteEmpty();
            chatFull.participants = new TL_chatParticipants();
            chatFull.participants.chat_id = chat.id;
            chatFull.participants.admin_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
            chatFull.participants.version = 1;
            for (a = 0; a < selectedContacts.size(); a++) {
                TL_chatParticipant participant = new TL_chatParticipant();
                participant.user_id = ((Integer) selectedContacts.get(a)).intValue();
                participant.inviter_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                participant.date = (int) (System.currentTimeMillis() / 1000);
                chatFull.participants.participants.add(participant);
            }
            MessagesStorage.getInstance(this.currentAccount).updateChatInfo(chatFull, false);
            Message newMsg = new TL_messageService();
            newMsg.action = new TL_messageActionCreatedBroadcastList();
            int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
            newMsg.id = newMessageId;
            newMsg.local_id = newMessageId;
            newMsg.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
            newMsg.dialog_id = AndroidUtilities.makeBroadcastId(chat.id);
            newMsg.to_id = new TL_peerChat();
            newMsg.to_id.chat_id = chat.id;
            newMsg.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            newMsg.random_id = 0;
            newMsg.flags |= 256;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            MessageObject newMsgObj = new MessageObject(this.currentAccount, newMsg, this.users, true);
            newMsgObj.messageOwner.send_state = 0;
            ArrayList<MessageObject> objArr = new ArrayList();
            objArr.add(newMsgObj);
            ArrayList arr = new ArrayList();
            arr.add(newMsg);
            MessagesStorage.getInstance(this.currentAccount).putMessages(arr, false, true, false, 0);
            updateInterfaceWithMessages(newMsg.dialog_id, objArr);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(chat.id));
            return 0;
        } else if (type == 0) {
            req = new TL_messages_createChat();
            req.title = title;
            for (a = 0; a < selectedContacts.size(); a++) {
                User user = getUser((Integer) selectedContacts.get(a));
                if (user != null) {
                    req.users.add(getInputUser(user));
                }
            }
            return ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$100(this, fragment, req), 2);
        } else if (type != 2 && type != 4) {
            return 0;
        } else {
            req = new TL_channels_createChannel();
            req.title = title;
            req.about = about;
            if (type == 4) {
                req.megagroup = true;
            } else {
                req.broadcast = true;
            }
            return ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$101(this, fragment, req), 2);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createChat$154$MessagesController(BaseFragment fragment, TL_messages_createChat req, TLObject response, TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$205(this, error, fragment, req));
            return;
        }
        Updates updates = (Updates) response;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$206(this, updates));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$152$MessagesController(TL_error error, BaseFragment fragment, TL_messages_createChat req) {
        AlertsCreator.processError(this.currentAccount, error, fragment, req, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$153$MessagesController(Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        if (updates.chats == null || updates.chats.isEmpty()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(((Chat) updates.chats.get(0)).id));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$createChat$157$MessagesController(BaseFragment fragment, TL_channels_createChannel req, TLObject response, TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$203(this, error, fragment, req));
            return;
        }
        Updates updates = (Updates) response;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$204(this, updates));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$155$MessagesController(TL_error error, BaseFragment fragment, TL_channels_createChannel req) {
        AlertsCreator.processError(this.currentAccount, error, fragment, req, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$156$MessagesController(Updates updates) {
        putUsers(updates.users, false);
        putChats(updates.chats, false);
        if (updates.chats == null || updates.chats.isEmpty()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
            return;
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(((Chat) updates.chats.get(0)).id));
    }

    public void convertToMegaGroup(Context context, int chat_id, IntCallback convertRunnable) {
        TL_messages_migrateChat req = new TL_messages_migrateChat();
        req.chat_id = chat_id;
        AlertDialog progressDialog = new AlertDialog(context, 3);
        progressDialog.setOnCancelListener(new MessagesController$$Lambda$103(this, ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$102(this, context, progressDialog, convertRunnable))));
        try {
            progressDialog.show();
        } catch (Exception e) {
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$convertToMegaGroup$161$MessagesController(Context context, AlertDialog progressDialog, IntCallback convertRunnable, TLObject response, TL_error error) {
        if (error == null) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$200(context, progressDialog));
            Updates updates = (Updates) response;
            processUpdates((Updates) response, false);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$201(convertRunnable, updates));
            return;
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$202(context, progressDialog));
    }

    static final /* synthetic */ void lambda$null$158$MessagesController(Context context, AlertDialog progressDialog) {
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    static final /* synthetic */ void lambda$null$159$MessagesController(IntCallback convertRunnable, Updates updates) {
        if (convertRunnable != null) {
            for (int a = 0; a < updates.chats.size(); a++) {
                Chat chat = (Chat) updates.chats.get(a);
                if (ChatObject.isChannel(chat)) {
                    convertRunnable.run(chat.id);
                    return;
                }
            }
        }
    }

    static final /* synthetic */ void lambda$null$160$MessagesController(Context context, AlertDialog progressDialog) {
        if (!((Activity) context).isFinishing()) {
            try {
                progressDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
            Builder builder = new Builder(context);
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setMessage(LocaleController.getString("ErrorOccurred", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.show().setCanceledOnTouchOutside(true);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$convertToMegaGroup$162$MessagesController(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }

    public void addUsersToChannel(int chat_id, ArrayList<InputUser> users, BaseFragment fragment) {
        if (users != null && !users.isEmpty()) {
            TL_channels_inviteToChannel req = new TL_channels_inviteToChannel();
            req.channel = getInputChannel(chat_id);
            req.users = users;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$104(this, fragment, req));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addUsersToChannel$164$MessagesController(BaseFragment fragment, TL_channels_inviteToChannel req, TLObject response, TL_error error) {
        if (error != null) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$199(this, error, fragment, req));
        } else {
            processUpdates((Updates) response, false);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$163$MessagesController(TL_error error, BaseFragment fragment, TL_channels_inviteToChannel req) {
        AlertsCreator.processError(this.currentAccount, error, fragment, req, Boolean.valueOf(true));
    }

    public void toogleChannelSignatures(int chat_id, boolean enabled) {
        TL_channels_toggleSignatures req = new TL_channels_toggleSignatures();
        req.channel = getInputChannel(chat_id);
        req.enabled = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$105(this), 64);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$toogleChannelSignatures$166$MessagesController(TLObject response, TL_error error) {
        if (response != null) {
            processUpdates((Updates) response, false);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$198(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$165$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
    }

    public void toogleChannelInvitesHistory(int chat_id, boolean enabled) {
        TL_channels_togglePreHistoryHidden req = new TL_channels_togglePreHistoryHidden();
        req.channel = getInputChannel(chat_id);
        req.enabled = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$106(this), 64);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$toogleChannelInvitesHistory$168$MessagesController(TLObject response, TL_error error) {
        if (response != null) {
            processUpdates((Updates) response, false);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$197(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$167$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
    }

    public void updateChatAbout(int chat_id, String about, ChatFull info) {
        if (info != null) {
            TL_messages_editChatAbout req = new TL_messages_editChatAbout();
            req.peer = getInputPeer(-chat_id);
            req.about = about;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$107(this, info, about), 64);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateChatAbout$170$MessagesController(ChatFull info, String about, TLObject response, TL_error error) {
        if (response instanceof TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$196(this, info, about));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$169$MessagesController(ChatFull info, String about) {
        info.about = about;
        MessagesStorage.getInstance(this.currentAccount).updateChatInfo(info, false);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, info, Integer.valueOf(0), Boolean.valueOf(false), null);
    }

    public void updateChannelUserName(int chat_id, String userName) {
        TL_channels_updateUsername req = new TL_channels_updateUsername();
        req.channel = getInputChannel(chat_id);
        req.username = userName;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$108(this, chat_id, userName), 64);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$updateChannelUserName$172$MessagesController(int chat_id, String userName, TLObject response, TL_error error) {
        if (response instanceof TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$195(this, chat_id, userName));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$171$MessagesController(int chat_id, String userName) {
        Chat chat = getChat(Integer.valueOf(chat_id));
        if (userName.length() != 0) {
            chat.flags |= 64;
        } else {
            chat.flags &= -65;
        }
        chat.username = userName;
        ArrayList<Chat> arrayList = new ArrayList();
        arrayList.add(chat);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, arrayList, true, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(8192));
    }

    public void sendBotStart(User user, String botHash) {
        if (user != null) {
            TL_messages_startBot req = new TL_messages_startBot();
            req.bot = getInputUser(user);
            req.peer = getInputPeer(user.id);
            req.start_param = botHash;
            req.random_id = Utilities.random.nextLong();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$109(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$sendBotStart$173$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            processUpdates((Updates) response, false);
        }
    }

    public boolean isJoiningChannel(int chat_id) {
        return this.joiningToChannels.contains(Integer.valueOf(chat_id));
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:12:0x0030  */
    public void addUserToChat(int r18, org.telegram.tgnet.TLRPC.User r19, org.telegram.tgnet.TLRPC.ChatFull r20, int r21, java.lang.String r22, org.telegram.ui.ActionBar.BaseFragment r23, java.lang.Runnable r24) {
        /*
        r17 = this;
        if (r19 != 0) goto L_0x0003;
    L_0x0002:
        return;
    L_0x0003:
        if (r18 <= 0) goto L_0x00c9;
    L_0x0005:
        r0 = r17;
        r2 = r0.currentAccount;
        r0 = r18;
        r4 = org.telegram.messenger.ChatObject.isChannel(r0, r2);
        if (r4 == 0) goto L_0x0074;
    L_0x0011:
        r2 = java.lang.Integer.valueOf(r18);
        r0 = r17;
        r2 = r0.getChat(r2);
        r2 = r2.megagroup;
        if (r2 == 0) goto L_0x0074;
    L_0x001f:
        r9 = 1;
    L_0x0020:
        r0 = r17;
        r1 = r19;
        r5 = r0.getInputUser(r1);
        if (r22 == 0) goto L_0x002e;
    L_0x002a:
        if (r4 == 0) goto L_0x0099;
    L_0x002c:
        if (r9 != 0) goto L_0x0099;
    L_0x002e:
        if (r4 == 0) goto L_0x0088;
    L_0x0030:
        r2 = r5 instanceof org.telegram.tgnet.TLRPC.TL_inputUserSelf;
        if (r2 == 0) goto L_0x0076;
    L_0x0034:
        r0 = r17;
        r2 = r0.joiningToChannels;
        r3 = java.lang.Integer.valueOf(r18);
        r2 = r2.contains(r3);
        if (r2 != 0) goto L_0x0002;
    L_0x0042:
        r15 = new org.telegram.tgnet.TLRPC$TL_channels_joinChannel;
        r15.<init>();
        r2 = r17.getInputChannel(r18);
        r15.channel = r2;
        r8 = r15;
        r0 = r17;
        r2 = r0.joiningToChannels;
        r3 = java.lang.Integer.valueOf(r18);
        r2.add(r3);
    L_0x0059:
        r0 = r17;
        r2 = r0.currentAccount;
        r16 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = new org.telegram.messenger.MessagesController$$Lambda$110;
        r3 = r17;
        r6 = r18;
        r7 = r23;
        r10 = r24;
        r2.<init>(r3, r4, r5, r6, r7, r8, r9, r10);
        r0 = r16;
        r0.sendRequest(r8, r2);
        goto L_0x0002;
    L_0x0074:
        r9 = 0;
        goto L_0x0020;
    L_0x0076:
        r15 = new org.telegram.tgnet.TLRPC$TL_channels_inviteToChannel;
        r15.<init>();
        r2 = r17.getInputChannel(r18);
        r15.channel = r2;
        r2 = r15.users;
        r2.add(r5);
        r8 = r15;
        goto L_0x0059;
    L_0x0088:
        r15 = new org.telegram.tgnet.TLRPC$TL_messages_addChatUser;
        r15.<init>();
        r0 = r18;
        r15.chat_id = r0;
        r0 = r21;
        r15.fwd_limit = r0;
        r15.user_id = r5;
        r8 = r15;
        goto L_0x0059;
    L_0x0099:
        r15 = new org.telegram.tgnet.TLRPC$TL_messages_startBot;
        r15.<init>();
        r15.bot = r5;
        if (r4 == 0) goto L_0x00bb;
    L_0x00a2:
        r0 = r18;
        r2 = -r0;
        r0 = r17;
        r2 = r0.getInputPeer(r2);
        r15.peer = r2;
    L_0x00ad:
        r0 = r22;
        r15.start_param = r0;
        r2 = org.telegram.messenger.Utilities.random;
        r2 = r2.nextLong();
        r15.random_id = r2;
        r8 = r15;
        goto L_0x0059;
    L_0x00bb:
        r2 = new org.telegram.tgnet.TLRPC$TL_inputPeerChat;
        r2.<init>();
        r15.peer = r2;
        r2 = r15.peer;
        r0 = r18;
        r2.chat_id = r0;
        goto L_0x00ad;
    L_0x00c9:
        r0 = r20;
        r2 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatFull;
        if (r2 == 0) goto L_0x0002;
    L_0x00cf:
        r11 = 0;
    L_0x00d0:
        r0 = r20;
        r2 = r0.participants;
        r2 = r2.participants;
        r2 = r2.size();
        if (r11 >= r2) goto L_0x00f3;
    L_0x00dc:
        r0 = r20;
        r2 = r0.participants;
        r2 = r2.participants;
        r2 = r2.get(r11);
        r2 = (org.telegram.tgnet.TLRPC.ChatParticipant) r2;
        r2 = r2.user_id;
        r0 = r19;
        r3 = r0.id;
        if (r2 == r3) goto L_0x0002;
    L_0x00f0:
        r11 = r11 + 1;
        goto L_0x00d0;
    L_0x00f3:
        r2 = java.lang.Integer.valueOf(r18);
        r0 = r17;
        r12 = r0.getChat(r2);
        r2 = r12.participants_count;
        r2 = r2 + 1;
        r12.participants_count = r2;
        r13 = new java.util.ArrayList;
        r13.<init>();
        r13.add(r12);
        r0 = r17;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r3 = 0;
        r6 = 1;
        r7 = 1;
        r2.putUsersAndChats(r3, r13, r6, r7);
        r14 = new org.telegram.tgnet.TLRPC$TL_chatParticipant;
        r14.<init>();
        r0 = r19;
        r2 = r0.id;
        r14.user_id = r2;
        r0 = r17;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.UserConfig.getInstance(r2);
        r2 = r2.getClientUserId();
        r14.inviter_id = r2;
        r0 = r17;
        r2 = r0.currentAccount;
        r2 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = r2.getCurrentTime();
        r14.date = r2;
        r0 = r20;
        r2 = r0.participants;
        r2 = r2.participants;
        r3 = 0;
        r2.add(r3, r14);
        r0 = r17;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r3 = 1;
        r0 = r20;
        r2.updateChatInfo(r0, r3);
        r0 = r17;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.chatInfoDidLoad;
        r6 = 4;
        r6 = new java.lang.Object[r6];
        r7 = 0;
        r6[r7] = r20;
        r7 = 1;
        r10 = 0;
        r10 = java.lang.Integer.valueOf(r10);
        r6[r7] = r10;
        r7 = 2;
        r10 = 0;
        r10 = java.lang.Boolean.valueOf(r10);
        r6[r7] = r10;
        r7 = 3;
        r10 = 0;
        r6[r7] = r10;
        r2.postNotificationName(r3, r6);
        r0 = r17;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.NotificationCenter.getInstance(r2);
        r3 = org.telegram.messenger.NotificationCenter.updateInterfaces;
        r6 = 1;
        r6 = new java.lang.Object[r6];
        r7 = 0;
        r10 = 32;
        r10 = java.lang.Integer.valueOf(r10);
        r6[r7] = r10;
        r2.postNotificationName(r3, r6);
        goto L_0x0002;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.addUserToChat(int, org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$ChatFull, int, java.lang.String, org.telegram.ui.ActionBar.BaseFragment, java.lang.Runnable):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$addUserToChat$177$MessagesController(boolean isChannel, InputUser inputUser, int chat_id, BaseFragment fragment, TLObject request, boolean isMegagroup, Runnable onFinishRunnable, TLObject response, TL_error error) {
        if (isChannel && (inputUser instanceof TL_inputUserSelf)) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$192(this, chat_id));
        }
        if (error != null) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$193(this, error, fragment, request, isChannel, isMegagroup));
            return;
        }
        boolean hasJoinMessage = false;
        Updates updates = (Updates) response;
        for (int a = 0; a < updates.updates.size(); a++) {
            Update update = (Update) updates.updates.get(a);
            if ((update instanceof TL_updateNewChannelMessage) && (((TL_updateNewChannelMessage) update).message.action instanceof TL_messageActionChatAddUser)) {
                hasJoinMessage = true;
                break;
            }
        }
        processUpdates(updates, false);
        if (isChannel) {
            if (!hasJoinMessage && (inputUser instanceof TL_inputUserSelf)) {
                generateJoinMessage(chat_id, true);
            }
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$194(this, chat_id), 1000);
        }
        if (isChannel && (inputUser instanceof TL_inputUserSelf)) {
            MessagesStorage.getInstance(this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), null, true, chat_id);
        }
        if (onFinishRunnable != null) {
            AndroidUtilities.runOnUIThread(onFinishRunnable);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$174$MessagesController(int chat_id) {
        this.joiningToChannels.remove(Integer.valueOf(chat_id));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$175$MessagesController(TL_error error, BaseFragment fragment, TLObject request, boolean isChannel, boolean isMegagroup) {
        boolean z = true;
        int i = this.currentAccount;
        Object[] objArr = new Object[1];
        if (!isChannel || isMegagroup) {
            z = false;
        }
        objArr[0] = Boolean.valueOf(z);
        AlertsCreator.processError(i, error, fragment, request, objArr);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$176$MessagesController(int chat_id) {
        loadFullChat(chat_id, 0, true);
    }

    public void deleteUserFromChat(int chat_id, User user, ChatFull info) {
        deleteUserFromChat(chat_id, user, info, false, false);
    }

    public void deleteUserFromChat(int chat_id, User user, ChatFull info, boolean forceDelete, boolean revoke) {
        if (user != null) {
            Chat chat;
            if (chat_id > 0) {
                TLObject request;
                InputUser inputUser = getInputUser(user);
                chat = getChat(Integer.valueOf(chat_id));
                boolean isChannel = ChatObject.isChannel(chat);
                TLObject req;
                if (!isChannel) {
                    req = new TL_messages_deleteChatUser();
                    req.chat_id = chat_id;
                    req.user_id = getInputUser(user);
                    request = req;
                } else if (!(inputUser instanceof TL_inputUserSelf)) {
                    req = new TL_channels_editBanned();
                    req.channel = getInputChannel(chat);
                    req.user_id = inputUser;
                    req.banned_rights = new TL_chatBannedRights();
                    req.banned_rights.view_messages = true;
                    req.banned_rights.send_media = true;
                    req.banned_rights.send_messages = true;
                    req.banned_rights.send_stickers = true;
                    req.banned_rights.send_gifs = true;
                    req.banned_rights.send_games = true;
                    req.banned_rights.send_inline = true;
                    req.banned_rights.embed_links = true;
                    req.banned_rights.pin_messages = true;
                    req.banned_rights.send_polls = true;
                    req.banned_rights.invite_users = true;
                    req.banned_rights.change_info = true;
                    request = req;
                } else if (chat.creator && forceDelete) {
                    req = new TL_channels_deleteChannel();
                    req.channel = getInputChannel(chat);
                    request = req;
                } else {
                    req = new TL_channels_leaveChannel();
                    req.channel = getInputChannel(chat);
                    request = req;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new MessagesController$$Lambda$111(this, user, chat_id, revoke, isChannel, inputUser), 64);
            } else if (info instanceof TL_chatFull) {
                chat = getChat(Integer.valueOf(chat_id));
                chat.participants_count--;
                ArrayList<Chat> chatArrayList = new ArrayList();
                chatArrayList.add(chat);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, chatArrayList, true, true);
                boolean changed = false;
                for (int a = 0; a < info.participants.participants.size(); a++) {
                    if (((ChatParticipant) info.participants.participants.get(a)).user_id == user.id) {
                        info.participants.participants.remove(a);
                        changed = true;
                        break;
                    }
                }
                if (changed) {
                    MessagesStorage.getInstance(this.currentAccount).updateChatInfo(info, true);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoad, info, Integer.valueOf(0), Boolean.valueOf(false), null);
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(32));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$deleteUserFromChat$180$MessagesController(User user, int chat_id, boolean revoke, boolean isChannel, InputUser inputUser, TLObject response, TL_error error) {
        if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$190(this, chat_id, revoke));
        }
        if (error == null) {
            processUpdates((Updates) response, false);
            if (isChannel && !(inputUser instanceof TL_inputUserSelf)) {
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$191(this, chat_id), 1000);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$178$MessagesController(int chat_id, boolean revoke) {
        deleteDialog((long) (-chat_id), 0, revoke);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$179$MessagesController(int chat_id) {
        loadFullChat(chat_id, 0, true);
    }

    public void changeChatTitle(int chat_id, String title) {
        if (chat_id > 0) {
            TLObject request;
            TLObject req;
            if (ChatObject.isChannel(chat_id, this.currentAccount)) {
                req = new TL_channels_editTitle();
                req.channel = getInputChannel(chat_id);
                req.title = title;
                request = req;
            } else {
                req = new TL_messages_editChatTitle();
                req.chat_id = chat_id;
                req.title = title;
                request = req;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new MessagesController$$Lambda$112(this), 64);
            return;
        }
        Chat chat = getChat(Integer.valueOf(chat_id));
        chat.title = title;
        ArrayList<Chat> chatArrayList = new ArrayList();
        chatArrayList.add(chat);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, chatArrayList, true, true);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(16));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$changeChatTitle$181$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            processUpdates((Updates) response, false);
        }
    }

    public void changeChatAvatar(int chat_id, InputFile uploadedAvatar, FileLocation smallSize, FileLocation bigSize) {
        TLObject request;
        TLObject req;
        if (ChatObject.isChannel(chat_id, this.currentAccount)) {
            req = new TL_channels_editPhoto();
            req.channel = getInputChannel(chat_id);
            if (uploadedAvatar != null) {
                req.photo = new TL_inputChatUploadedPhoto();
                req.photo.file = uploadedAvatar;
            } else {
                req.photo = new TL_inputChatPhotoEmpty();
            }
            request = req;
        } else {
            req = new TL_messages_editChatPhoto();
            req.chat_id = chat_id;
            if (uploadedAvatar != null) {
                req.photo = new TL_inputChatUploadedPhoto();
                req.photo.file = uploadedAvatar;
            } else {
                req.photo = new TL_inputChatPhotoEmpty();
            }
            request = req;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new MessagesController$$Lambda$113(this, smallSize, bigSize), 64);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$changeChatAvatar$182$MessagesController(FileLocation smallSize, FileLocation bigSize, TLObject response, TL_error error) {
        if (error == null) {
            Updates updates = (Updates) response;
            Photo photo = null;
            int N = updates.updates.size();
            for (int a = 0; a < N; a++) {
                Update update = (Update) updates.updates.get(a);
                Message message;
                if (update instanceof TL_updateNewChannelMessage) {
                    message = ((TL_updateNewChannelMessage) update).message;
                    if ((message.action instanceof TL_messageActionChatEditPhoto) && (message.action.photo instanceof TL_photo)) {
                        photo = message.action.photo;
                        break;
                    }
                } else if (update instanceof TL_updateNewMessage) {
                    message = ((TL_updateNewMessage) update).message;
                    if ((message.action instanceof TL_messageActionChatEditPhoto) && (message.action.photo instanceof TL_photo)) {
                        photo = message.action.photo;
                        break;
                    }
                } else {
                    continue;
                }
            }
            if (photo != null) {
                PhotoSize small = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 150);
                if (!(small == null || smallSize == null)) {
                    FileLoader.getPathToAttach(smallSize, true).renameTo(FileLoader.getPathToAttach(small, true));
                    ImageLoader.getInstance().replaceImageInCache(smallSize.volume_id + "_" + smallSize.local_id + "@50_50", small.location.volume_id + "_" + small.location.local_id + "@50_50", small.location, true);
                }
                PhotoSize big = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 800);
                if (!(big == null || bigSize == null)) {
                    FileLoader.getPathToAttach(bigSize, true).renameTo(FileLoader.getPathToAttach(big, true));
                }
            }
            processUpdates(updates, false);
        }
    }

    public void unregistedPush() {
        if (UserConfig.getInstance(this.currentAccount).registeredForPush && SharedConfig.pushString.length() == 0) {
            TL_account_unregisterDevice req = new TL_account_unregisterDevice();
            req.token = SharedConfig.pushString;
            req.token_type = 2;
            for (int a = 0; a < 3; a++) {
                UserConfig userConfig = UserConfig.getInstance(a);
                if (a != this.currentAccount && userConfig.isClientActivated()) {
                    req.other_uids.add(Integer.valueOf(userConfig.getClientUserId()));
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, MessagesController$$Lambda$114.$instance);
        }
    }

    static final /* synthetic */ void lambda$unregistedPush$183$MessagesController(TLObject response, TL_error error) {
    }

    public void performLogout(int type) {
        boolean z = true;
        if (type == 1) {
            unregistedPush();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_logOut(), new MessagesController$$Lambda$115(this));
        } else {
            ConnectionsManager instance = ConnectionsManager.getInstance(this.currentAccount);
            if (type != 2) {
                z = false;
            }
            instance.cleanup(z);
        }
        UserConfig.getInstance(this.currentAccount).clearConfig();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
        MessagesStorage.getInstance(this.currentAccount).cleanup(false);
        cleanup();
        ContactsController.getInstance(this.currentAccount).deleteUnknownAppAccounts();
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$performLogout$184$MessagesController(TLObject response, TL_error error) {
        ConnectionsManager.getInstance(this.currentAccount).cleanup(false);
    }

    public void generateUpdateMessage() {
        if (!BuildVars.DEBUG_VERSION && SharedConfig.lastUpdateVersion != null && !SharedConfig.lastUpdateVersion.equals(BuildVars.BUILD_VERSION_STRING)) {
            TL_help_getAppChangelog req = new TL_help_getAppChangelog();
            req.prev_app_version = SharedConfig.lastUpdateVersion;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$116(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$generateUpdateMessage$185$MessagesController(TLObject response, TL_error error) {
        if (error == null) {
            SharedConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
            SharedConfig.saveConfig();
        }
        if (response instanceof Updates) {
            processUpdates((Updates) response, false);
        }
    }

    public void registerForPush(String regid) {
        if (!TextUtils.isEmpty(regid) && !this.registeringForPush && UserConfig.getInstance(this.currentAccount).getClientUserId() != 0) {
            if (!UserConfig.getInstance(this.currentAccount).registeredForPush || !regid.equals(SharedConfig.pushString)) {
                this.registeringForPush = true;
                this.lastPushRegisterSendTime = SystemClock.elapsedRealtime();
                if (SharedConfig.pushAuthKey == null) {
                    SharedConfig.pushAuthKey = new byte[256];
                    Utilities.random.nextBytes(SharedConfig.pushAuthKey);
                    SharedConfig.saveConfig();
                }
                TL_account_registerDevice req = new TL_account_registerDevice();
                req.token_type = 2;
                req.token = regid;
                req.secret = SharedConfig.pushAuthKey;
                for (int a = 0; a < 3; a++) {
                    UserConfig userConfig = UserConfig.getInstance(a);
                    if (a != this.currentAccount && userConfig.isClientActivated()) {
                        int uid = userConfig.getClientUserId();
                        req.other_uids.add(Integer.valueOf(uid));
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("add other uid = " + uid + " for account " + this.currentAccount);
                        }
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$117(this, regid));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$registerForPush$187$MessagesController(String regid, TLObject response, TL_error error) {
        if (response instanceof TL_boolTrue) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("account " + this.currentAccount + " registered for push");
            }
            UserConfig.getInstance(this.currentAccount).registeredForPush = true;
            SharedConfig.pushString = regid;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$189(this));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$186$MessagesController() {
        this.registeringForPush = false;
    }

    public void loadCurrentState() {
        if (!this.updatingState) {
            this.updatingState = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_updates_getState(), new MessagesController$$Lambda$118(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadCurrentState$188$MessagesController(TLObject response, TL_error error) {
        this.updatingState = false;
        if (error == null) {
            TL_updates_state res = (TL_updates_state) response;
            MessagesStorage.getInstance(this.currentAccount).setLastDateValue(res.date);
            MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(res.pts);
            MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(res.seq);
            MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(res.qts);
            for (int a = 0; a < 3; a++) {
                processUpdatesQueue(a, 2);
            }
            MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
        } else if (error.code != 401) {
            loadCurrentState();
        }
    }

    private int getUpdateSeq(Updates updates) {
        if (updates instanceof TL_updatesCombined) {
            return updates.seq_start;
        }
        return updates.seq;
    }

    private void setUpdatesStartTime(int type, long time) {
        if (type == 0) {
            this.updatesStartWaitTimeSeq = time;
        } else if (type == 1) {
            this.updatesStartWaitTimePts = time;
        } else if (type == 2) {
            this.updatesStartWaitTimeQts = time;
        }
    }

    public long getUpdatesStartTime(int type) {
        if (type == 0) {
            return this.updatesStartWaitTimeSeq;
        }
        if (type == 1) {
            return this.updatesStartWaitTimePts;
        }
        if (type == 2) {
            return this.updatesStartWaitTimeQts;
        }
        return 0;
    }

    private int isValidUpdate(Updates updates, int type) {
        if (type == 0) {
            int seq = getUpdateSeq(updates);
            if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 == seq || MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() == seq) {
                return 0;
            }
            if (MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() >= seq) {
                return 2;
            }
            return 1;
        } else if (type == 1) {
            if (updates.pts <= MessagesStorage.getInstance(this.currentAccount).getLastPtsValue()) {
                return 2;
            }
            if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + updates.pts_count == updates.pts) {
                return 0;
            }
            return 1;
        } else if (type != 2) {
            return 0;
        } else {
            if (updates.pts <= MessagesStorage.getInstance(this.currentAccount).getLastQtsValue()) {
                return 2;
            }
            if (MessagesStorage.getInstance(this.currentAccount).getLastQtsValue() + updates.updates.size() == updates.pts) {
                return 0;
            }
            return 1;
        }
    }

    private void processChannelsUpdatesQueue(int channelId, int state) {
        ArrayList<Updates> updatesQueue = (ArrayList) this.updatesQueueChannels.get(channelId);
        if (updatesQueue != null) {
            int channelPts = this.channelsPts.get(channelId);
            if (updatesQueue.isEmpty() || channelPts == 0) {
                this.updatesQueueChannels.remove(channelId);
                return;
            }
            Collections.sort(updatesQueue, MessagesController$$Lambda$119.$instance);
            boolean anyProceed = false;
            if (state == 2) {
                this.channelsPts.put(channelId, ((Updates) updatesQueue.get(0)).pts);
            }
            int a = 0;
            while (updatesQueue.size() > 0) {
                int updateState;
                Updates updates = (Updates) updatesQueue.get(a);
                if (updates.pts <= channelPts) {
                    updateState = 2;
                } else if (updates.pts_count + channelPts == updates.pts) {
                    updateState = 0;
                } else {
                    updateState = 1;
                }
                if (updateState == 0) {
                    processUpdates(updates, true);
                    anyProceed = true;
                    updatesQueue.remove(a);
                } else if (updateState == 1) {
                    long updatesStartWaitTime = this.updatesStartWaitTimeChannels.get(channelId);
                    if (updatesStartWaitTime == 0 || (!anyProceed && Math.abs(System.currentTimeMillis() - updatesStartWaitTime) > 1500)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN CHANNEL " + channelId + " UPDATES QUEUE - getChannelDifference ");
                        }
                        this.updatesStartWaitTimeChannels.delete(channelId);
                        this.updatesQueueChannels.remove(channelId);
                        getChannelDifference(channelId);
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN CHANNEL " + channelId + " UPDATES QUEUE - will wait more time");
                    }
                    if (anyProceed) {
                        this.updatesStartWaitTimeChannels.put(channelId, System.currentTimeMillis());
                        return;
                    }
                    return;
                } else {
                    updatesQueue.remove(a);
                }
                a = (a - 1) + 1;
            }
            this.updatesQueueChannels.remove(channelId);
            this.updatesStartWaitTimeChannels.delete(channelId);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("UPDATES CHANNEL " + channelId + " QUEUE PROCEED - OK");
            }
        }
    }

    private void processUpdatesQueue(int type, int state) {
        ArrayList<Updates> updatesQueue = null;
        if (type == 0) {
            updatesQueue = this.updatesQueueSeq;
            Collections.sort(updatesQueue, new MessagesController$$Lambda$120(this));
        } else if (type == 1) {
            updatesQueue = this.updatesQueuePts;
            Collections.sort(updatesQueue, MessagesController$$Lambda$121.$instance);
        } else if (type == 2) {
            updatesQueue = this.updatesQueueQts;
            Collections.sort(updatesQueue, MessagesController$$Lambda$122.$instance);
        }
        if (!(updatesQueue == null || updatesQueue.isEmpty())) {
            Updates updates;
            boolean anyProceed = false;
            if (state == 2) {
                updates = (Updates) updatesQueue.get(0);
                if (type == 0) {
                    MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(getUpdateSeq(updates));
                } else if (type == 1) {
                    MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(updates.pts);
                } else {
                    MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(updates.pts);
                }
            }
            int a = 0;
            while (updatesQueue.size() > 0) {
                updates = (Updates) updatesQueue.get(a);
                int updateState = isValidUpdate(updates, type);
                if (updateState == 0) {
                    processUpdates(updates, true);
                    anyProceed = true;
                    updatesQueue.remove(a);
                } else if (updateState != 1) {
                    updatesQueue.remove(a);
                } else if (getUpdatesStartTime(type) == 0 || (!anyProceed && Math.abs(System.currentTimeMillis() - getUpdatesStartTime(type)) > 1500)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN UPDATES QUEUE - getDifference");
                    }
                    setUpdatesStartTime(type, 0);
                    updatesQueue.clear();
                    getDifference();
                    return;
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN UPDATES QUEUE - will wait more time");
                    }
                    if (anyProceed) {
                        setUpdatesStartTime(type, System.currentTimeMillis());
                        return;
                    }
                    return;
                }
                a = (a - 1) + 1;
            }
            updatesQueue.clear();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("UPDATES QUEUE PROCEED - OK");
            }
        }
        setUpdatesStartTime(type, 0);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ int lambda$processUpdatesQueue$190$MessagesController(Updates updates, Updates updates2) {
        return AndroidUtilities.compare(getUpdateSeq(updates), getUpdateSeq(updates2));
    }

    /* Access modifiers changed, original: protected */
    public void loadUnknownChannel(Chat channel, long taskId) {
        Throwable e;
        long newTaskId;
        if ((channel instanceof TL_channel) && this.gettingUnknownChannels.indexOfKey(channel.id) < 0) {
            if (channel.access_hash != 0) {
                TL_inputPeerChannel inputPeer = new TL_inputPeerChannel();
                inputPeer.channel_id = channel.id;
                inputPeer.access_hash = channel.access_hash;
                this.gettingUnknownChannels.put(channel.id, true);
                TL_messages_getPeerDialogs req = new TL_messages_getPeerDialogs();
                TL_inputDialogPeer inputDialogPeer = new TL_inputDialogPeer();
                inputDialogPeer.peer = inputPeer;
                req.peers.add(inputDialogPeer);
                if (taskId == 0) {
                    NativeByteBuffer data = null;
                    try {
                        NativeByteBuffer data2 = new NativeByteBuffer(channel.getObjectSize() + 4);
                        try {
                            data2.writeInt32(0);
                            channel.serializeToStream(data2);
                            data = data2;
                        } catch (Exception e2) {
                            e = e2;
                            data = data2;
                            FileLog.e(e);
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$123(this, newTaskId, channel));
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$123(this, newTaskId, channel));
                    }
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                } else {
                    newTaskId = taskId;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$123(this, newTaskId, channel));
            } else if (taskId != 0) {
                MessagesStorage.getInstance(this.currentAccount).removePendingTask(taskId);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadUnknownChannel$193$MessagesController(long newTaskId, Chat channel, TLObject response, TL_error error) {
        if (response != null) {
            TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
            if (!(res.dialogs.isEmpty() || res.chats.isEmpty())) {
                TL_messages_dialogs dialogs = new TL_messages_dialogs();
                dialogs.dialogs.addAll(res.dialogs);
                dialogs.messages.addAll(res.messages);
                dialogs.users.addAll(res.users);
                dialogs.chats.addAll(res.chats);
                processLoadedDialogs(dialogs, null, 0, 1, 2, false, false, false);
            }
        }
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
        this.gettingUnknownChannels.delete(channel.id);
    }

    public void startShortPoll(Chat chat, boolean stop) {
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$124(this, stop, chat));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$startShortPoll$194$MessagesController(boolean stop, Chat chat) {
        if (stop) {
            this.needShortPollChannels.delete(chat.id);
            if (chat.megagroup) {
                this.needShortPollOnlines.delete(chat.id);
                return;
            }
            return;
        }
        this.needShortPollChannels.put(chat.id, 0);
        if (this.shortPollChannels.indexOfKey(chat.id) < 0) {
            getChannelDifference(chat.id, 3, 0, null);
        }
        if (chat.megagroup) {
            this.needShortPollOnlines.put(chat.id, 0);
            if (this.shortPollOnlines.indexOfKey(chat.id) < 0) {
                this.shortPollOnlines.put(chat.id, 0);
            }
        }
    }

    private void getChannelDifference(int channelId) {
        getChannelDifference(channelId, 0, 0, null);
    }

    public static boolean isSupportUser(User user) {
        return user != null && (user.support || user.id == 777000 || user.id == 333000 || user.id == 4240000 || user.id == 4240000 || user.id == 4244000 || user.id == 4245000 || user.id == 4246000 || user.id == 410000 || user.id == 420000 || user.id == 431000 || user.id == NUM || user.id == 434000 || user.id == 4243000 || user.id == 439000 || user.id == 449000 || user.id == 450000 || user.id == 452000 || user.id == 454000 || user.id == 4254000 || user.id == 455000 || user.id == 460000 || user.id == 470000 || user.id == 479000 || user.id == 796000 || user.id == 482000 || user.id == 490000 || user.id == 496000 || user.id == 497000 || user.id == 498000 || user.id == 4298000);
    }

    /* Access modifiers changed, original: protected */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00f4  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00fb  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00f4  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0141  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x00fb  */
    public void getChannelDifference(int r18, int r19, long r20, org.telegram.tgnet.TLRPC.InputChannel r22) {
        /*
        r17 = this;
        r0 = r17;
        r2 = r0.gettingDifferenceChannels;
        r0 = r18;
        r13 = r2.get(r0);
        if (r13 == 0) goto L_0x000d;
    L_0x000c:
        return;
    L_0x000d:
        r14 = 100;
        r2 = 1;
        r0 = r19;
        if (r0 != r2) goto L_0x006a;
    L_0x0014:
        r0 = r17;
        r2 = r0.channelsPts;
        r0 = r18;
        r8 = r2.get(r0);
        if (r8 != 0) goto L_0x000c;
    L_0x0020:
        r8 = 1;
        r14 = 1;
    L_0x0022:
        if (r22 != 0) goto L_0x004a;
    L_0x0024:
        r2 = java.lang.Integer.valueOf(r18);
        r0 = r17;
        r9 = r0.getChat(r2);
        if (r9 != 0) goto L_0x0046;
    L_0x0030:
        r0 = r17;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r0 = r18;
        r9 = r2.getChatSync(r0);
        if (r9 == 0) goto L_0x0046;
    L_0x0040:
        r2 = 1;
        r0 = r17;
        r0.putChat(r9, r2);
    L_0x0046:
        r22 = getInputChannel(r9);
    L_0x004a:
        if (r22 == 0) goto L_0x0056;
    L_0x004c:
        r0 = r22;
        r2 = r0.access_hash;
        r4 = 0;
        r2 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1));
        if (r2 != 0) goto L_0x009f;
    L_0x0056:
        r2 = 0;
        r2 = (r20 > r2 ? 1 : (r20 == r2 ? 0 : -1));
        if (r2 == 0) goto L_0x000c;
    L_0x005c:
        r0 = r17;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r0 = r20;
        r2.removePendingTask(r0);
        goto L_0x000c;
    L_0x006a:
        r0 = r17;
        r2 = r0.channelsPts;
        r0 = r18;
        r8 = r2.get(r0);
        if (r8 != 0) goto L_0x009b;
    L_0x0076:
        r0 = r17;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r0 = r18;
        r8 = r2.getChannelPtsSync(r0);
        if (r8 == 0) goto L_0x008f;
    L_0x0086:
        r0 = r17;
        r2 = r0.channelsPts;
        r0 = r18;
        r2.put(r0, r8);
    L_0x008f:
        if (r8 != 0) goto L_0x009b;
    L_0x0091:
        r2 = 2;
        r0 = r19;
        if (r0 == r2) goto L_0x000c;
    L_0x0096:
        r2 = 3;
        r0 = r19;
        if (r0 == r2) goto L_0x000c;
    L_0x009b:
        if (r8 != 0) goto L_0x0022;
    L_0x009d:
        goto L_0x000c;
    L_0x009f:
        r2 = 0;
        r2 = (r20 > r2 ? 1 : (r20 == r2 ? 0 : -1));
        if (r2 != 0) goto L_0x013e;
    L_0x00a5:
        r10 = 0;
        r11 = new org.telegram.tgnet.NativeByteBuffer;	 Catch:{ Exception -> 0x0139 }
        r2 = r22.getObjectSize();	 Catch:{ Exception -> 0x0139 }
        r2 = r2 + 12;
        r11.<init>(r2);	 Catch:{ Exception -> 0x0139 }
        r2 = 6;
        r11.writeInt32(r2);	 Catch:{ Exception -> 0x0143 }
        r0 = r18;
        r11.writeInt32(r0);	 Catch:{ Exception -> 0x0143 }
        r0 = r19;
        r11.writeInt32(r0);	 Catch:{ Exception -> 0x0143 }
        r0 = r22;
        r0.serializeToStream(r11);	 Catch:{ Exception -> 0x0143 }
        r10 = r11;
    L_0x00c5:
        r0 = r17;
        r2 = r0.currentAccount;
        r2 = org.telegram.messenger.MessagesStorage.getInstance(r2);
        r6 = r2.createPendingTask(r10);
    L_0x00d1:
        r0 = r17;
        r2 = r0.gettingDifferenceChannels;
        r3 = 1;
        r0 = r18;
        r2.put(r0, r3);
        r15 = new org.telegram.tgnet.TLRPC$TL_updates_getChannelDifference;
        r15.<init>();
        r0 = r22;
        r15.channel = r0;
        r2 = new org.telegram.tgnet.TLRPC$TL_channelMessagesFilterEmpty;
        r2.<init>();
        r15.filter = r2;
        r15.pts = r8;
        r15.limit = r14;
        r2 = 3;
        r0 = r19;
        if (r0 == r2) goto L_0x0141;
    L_0x00f4:
        r2 = 1;
    L_0x00f5:
        r15.force = r2;
        r2 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r2 == 0) goto L_0x011f;
    L_0x00fb:
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "start getChannelDifference with pts = ";
        r2 = r2.append(r3);
        r2 = r2.append(r8);
        r3 = " channelId = ";
        r2 = r2.append(r3);
        r0 = r18;
        r2 = r2.append(r0);
        r2 = r2.toString();
        org.telegram.messenger.FileLog.d(r2);
    L_0x011f:
        r0 = r17;
        r2 = r0.currentAccount;
        r16 = org.telegram.tgnet.ConnectionsManager.getInstance(r2);
        r2 = new org.telegram.messenger.MessagesController$$Lambda$125;
        r3 = r17;
        r4 = r18;
        r5 = r19;
        r2.<init>(r3, r4, r5, r6);
        r0 = r16;
        r0.sendRequest(r15, r2);
        goto L_0x000c;
    L_0x0139:
        r12 = move-exception;
    L_0x013a:
        org.telegram.messenger.FileLog.e(r12);
        goto L_0x00c5;
    L_0x013e:
        r6 = r20;
        goto L_0x00d1;
    L_0x0141:
        r2 = 0;
        goto L_0x00f5;
    L_0x0143:
        r12 = move-exception;
        r10 = r11;
        goto L_0x013a;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.getChannelDifference(int, int, long, org.telegram.tgnet.TLRPC$InputChannel):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$getChannelDifference$203$MessagesController(int channelId, int newDialogType, long newTaskId, TLObject response, TL_error error) {
        if (response != null) {
            int a;
            updates_ChannelDifference res = (updates_ChannelDifference) response;
            SparseArray<User> usersDict = new SparseArray();
            for (a = 0; a < res.users.size(); a++) {
                User user = (User) res.users.get(a);
                usersDict.put(user.id, user);
            }
            Chat channel = null;
            for (a = 0; a < res.chats.size(); a++) {
                Chat chat = (Chat) res.chats.get(a);
                if (chat.id == channelId) {
                    channel = chat;
                    break;
                }
            }
            Chat channelFinal = channel;
            ArrayList<TL_updateMessageID> msgUpdates = new ArrayList();
            if (!res.other_updates.isEmpty()) {
                a = 0;
                while (a < res.other_updates.size()) {
                    Update upd = (Update) res.other_updates.get(a);
                    if (upd instanceof TL_updateMessageID) {
                        msgUpdates.add((TL_updateMessageID) upd);
                        res.other_updates.remove(a);
                        a--;
                    }
                    a++;
                }
            }
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$181(this, res));
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new MessagesController$$Lambda$182(this, msgUpdates, channelId, res, channelFinal, usersDict, newDialogType, newTaskId));
        } else if (error != null) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$183(this, error, channelId));
            this.gettingDifferenceChannels.delete(channelId);
            if (newTaskId != 0) {
                MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$195$MessagesController(updates_ChannelDifference res) {
        putUsers(res.users, false);
        putChats(res.chats, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$201$MessagesController(ArrayList msgUpdates, int channelId, updates_ChannelDifference res, Chat channelFinal, SparseArray usersDict, int newDialogType, long newTaskId) {
        if (!msgUpdates.isEmpty()) {
            SparseArray<long[]> corrected = new SparseArray();
            Iterator it = msgUpdates.iterator();
            while (it.hasNext()) {
                TL_updateMessageID update = (TL_updateMessageID) it.next();
                long[] ids = MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(update.random_id, null, update.id, 0, false, channelId);
                if (ids != null) {
                    corrected.put(update.id, ids);
                }
            }
            if (corrected.size() != 0) {
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$184(this, corrected));
            }
        }
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$185(this, res, channelId, channelFinal, usersDict, newDialogType, newTaskId));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$196$MessagesController(SparseArray corrected) {
        for (int a = 0; a < corrected.size(); a++) {
            int newId = corrected.keyAt(a);
            SendMessagesHelper.getInstance(this.currentAccount).processSentMessage((int) ((long[]) corrected.valueAt(a))[1]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newId), null, Long.valueOf(ids[0]), Long.valueOf(0), Integer.valueOf(-1));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    /* JADX WARNING: Removed duplicated region for block: B:93:0x015e A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0154  */
    public final /* synthetic */ void lambda$null$200$MessagesController(org.telegram.tgnet.TLRPC.updates_ChannelDifference r25, int r26, org.telegram.tgnet.TLRPC.Chat r27, android.util.SparseArray r28, int r29, long r30) {
        /*
        r24 = this;
        r0 = r25;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifference;
        r18 = r0;
        if (r18 != 0) goto L_0x0010;
    L_0x0008:
        r0 = r25;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceEmpty;
        r18 = r0;
        if (r18 == 0) goto L_0x0301;
    L_0x0010:
        r0 = r25;
        r0 = r0.new_messages;
        r18 = r0;
        r18 = r18.isEmpty();
        if (r18 != 0) goto L_0x0194;
    L_0x001c:
        r12 = new android.util.LongSparseArray;
        r12.<init>();
        r0 = r25;
        r0 = r0.new_messages;
        r18 = r0;
        org.telegram.messenger.ImageLoader.saveMessagesThumbs(r18);
        r15 = new java.util.ArrayList;
        r15.<init>();
        r0 = r26;
        r0 = -r0;
        r18 = r0;
        r0 = r18;
        r8 = (long) r0;
        r0 = r24;
        r0 = r0.dialogs_read_inbox_max;
        r18 = r0;
        r19 = java.lang.Long.valueOf(r8);
        r10 = r18.get(r19);
        r10 = (java.lang.Integer) r10;
        if (r10 != 0) goto L_0x0072;
    L_0x0049:
        r0 = r24;
        r0 = r0.currentAccount;
        r18 = r0;
        r18 = org.telegram.messenger.MessagesStorage.getInstance(r18);
        r19 = 0;
        r0 = r18;
        r1 = r19;
        r18 = r0.getDialogReadMax(r1, r8);
        r10 = java.lang.Integer.valueOf(r18);
        r0 = r24;
        r0 = r0.dialogs_read_inbox_max;
        r18 = r0;
        r19 = java.lang.Long.valueOf(r8);
        r0 = r18;
        r1 = r19;
        r0.put(r1, r10);
    L_0x0072:
        r0 = r24;
        r0 = r0.dialogs_read_outbox_max;
        r18 = r0;
        r19 = java.lang.Long.valueOf(r8);
        r14 = r18.get(r19);
        r14 = (java.lang.Integer) r14;
        if (r14 != 0) goto L_0x00ad;
    L_0x0084:
        r0 = r24;
        r0 = r0.currentAccount;
        r18 = r0;
        r18 = org.telegram.messenger.MessagesStorage.getInstance(r18);
        r19 = 1;
        r0 = r18;
        r1 = r19;
        r18 = r0.getDialogReadMax(r1, r8);
        r14 = java.lang.Integer.valueOf(r18);
        r0 = r24;
        r0 = r0.dialogs_read_outbox_max;
        r18 = r0;
        r19 = java.lang.Long.valueOf(r8);
        r0 = r18;
        r1 = r19;
        r0.put(r1, r14);
    L_0x00ad:
        r6 = 0;
    L_0x00ae:
        r0 = r25;
        r0 = r0.new_messages;
        r18 = r0;
        r18 = r18.size();
        r0 = r18;
        if (r6 >= r0) goto L_0x016c;
    L_0x00bc:
        r0 = r25;
        r0 = r0.new_messages;
        r18 = r0;
        r0 = r18;
        r11 = r0.get(r6);
        r11 = (org.telegram.tgnet.TLRPC.Message) r11;
        if (r27 == 0) goto L_0x00d4;
    L_0x00cc:
        r0 = r27;
        r0 = r0.left;
        r18 = r0;
        if (r18 != 0) goto L_0x0169;
    L_0x00d4:
        r0 = r11.out;
        r18 = r0;
        if (r18 == 0) goto L_0x0165;
    L_0x00da:
        r18 = r14;
    L_0x00dc:
        r18 = r18.intValue();
        r0 = r11.id;
        r19 = r0;
        r0 = r18;
        r1 = r19;
        if (r0 >= r1) goto L_0x0169;
    L_0x00ea:
        r0 = r11.action;
        r18 = r0;
        r0 = r18;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        r18 = r0;
        if (r18 != 0) goto L_0x0169;
    L_0x00f6:
        r18 = 1;
    L_0x00f8:
        r0 = r18;
        r11.unread = r0;
        if (r27 == 0) goto L_0x0112;
    L_0x00fe:
        r0 = r27;
        r0 = r0.megagroup;
        r18 = r0;
        if (r18 == 0) goto L_0x0112;
    L_0x0106:
        r0 = r11.flags;
        r18 = r0;
        r19 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r18 = r18 | r19;
        r0 = r18;
        r11.flags = r0;
    L_0x0112:
        r13 = new org.telegram.messenger.MessageObject;
        r0 = r24;
        r0 = r0.currentAccount;
        r18 = r0;
        r0 = r24;
        r0 = r0.createdDialogIds;
        r19 = r0;
        r20 = java.lang.Long.valueOf(r8);
        r19 = r19.contains(r20);
        r0 = r18;
        r1 = r28;
        r2 = r19;
        r13.<init>(r0, r11, r1, r2);
        r18 = r13.isOut();
        if (r18 != 0) goto L_0x0140;
    L_0x0137:
        r18 = r13.isUnread();
        if (r18 == 0) goto L_0x0140;
    L_0x013d:
        r15.add(r13);
    L_0x0140:
        r0 = r26;
        r0 = -r0;
        r18 = r0;
        r0 = r18;
        r0 = (long) r0;
        r16 = r0;
        r0 = r16;
        r7 = r12.get(r0);
        r7 = (java.util.ArrayList) r7;
        if (r7 != 0) goto L_0x015e;
    L_0x0154:
        r7 = new java.util.ArrayList;
        r7.<init>();
        r0 = r16;
        r12.put(r0, r7);
    L_0x015e:
        r7.add(r13);
        r6 = r6 + 1;
        goto L_0x00ae;
    L_0x0165:
        r18 = r10;
        goto L_0x00dc;
    L_0x0169:
        r18 = 0;
        goto L_0x00f8;
    L_0x016c:
        r18 = new org.telegram.messenger.MessagesController$$Lambda$186;
        r0 = r18;
        r1 = r24;
        r0.<init>(r1, r12);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r18);
        r0 = r24;
        r0 = r0.currentAccount;
        r18 = r0;
        r18 = org.telegram.messenger.MessagesStorage.getInstance(r18);
        r18 = r18.getStorageQueue();
        r19 = new org.telegram.messenger.MessagesController$$Lambda$187;
        r0 = r19;
        r1 = r24;
        r2 = r25;
        r0.<init>(r1, r15, r2);
        r18.postRunnable(r19);
    L_0x0194:
        r0 = r25;
        r0 = r0.other_updates;
        r18 = r0;
        r18 = r18.isEmpty();
        if (r18 != 0) goto L_0x01c1;
    L_0x01a0:
        r0 = r25;
        r0 = r0.other_updates;
        r18 = r0;
        r0 = r25;
        r0 = r0.users;
        r19 = r0;
        r0 = r25;
        r0 = r0.chats;
        r20 = r0;
        r21 = 1;
        r0 = r24;
        r1 = r18;
        r2 = r19;
        r3 = r20;
        r4 = r21;
        r0.processUpdateArray(r1, r2, r3, r4);
    L_0x01c1:
        r18 = 1;
        r0 = r24;
        r1 = r26;
        r2 = r18;
        r0.processChannelsUpdatesQueue(r1, r2);
        r0 = r24;
        r0 = r0.currentAccount;
        r18 = r0;
        r18 = org.telegram.messenger.MessagesStorage.getInstance(r18);
        r0 = r25;
        r0 = r0.pts;
        r19 = r0;
        r0 = r18;
        r1 = r26;
        r2 = r19;
        r0.saveChannelPts(r1, r2);
    L_0x01e5:
        r0 = r24;
        r0 = r0.gettingDifferenceChannels;
        r18 = r0;
        r0 = r18;
        r1 = r26;
        r0.delete(r1);
        r0 = r24;
        r0 = r0.channelsPts;
        r18 = r0;
        r0 = r25;
        r0 = r0.pts;
        r19 = r0;
        r0 = r18;
        r1 = r26;
        r2 = r19;
        r0.put(r1, r2);
        r0 = r25;
        r0 = r0.flags;
        r18 = r0;
        r18 = r18 & 2;
        if (r18 == 0) goto L_0x0235;
    L_0x0211:
        r0 = r24;
        r0 = r0.shortPollChannels;
        r18 = r0;
        r20 = java.lang.System.currentTimeMillis();
        r22 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r20 = r20 / r22;
        r0 = r20;
        r0 = (int) r0;
        r19 = r0;
        r0 = r25;
        r0 = r0.timeout;
        r20 = r0;
        r19 = r19 + r20;
        r0 = r18;
        r1 = r26;
        r2 = r19;
        r0.put(r1, r2);
    L_0x0235:
        r0 = r25;
        r0 = r0.isFinal;
        r18 = r0;
        if (r18 != 0) goto L_0x0244;
    L_0x023d:
        r0 = r24;
        r1 = r26;
        r0.getChannelDifference(r1);
    L_0x0244:
        r18 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r18 == 0) goto L_0x02e9;
    L_0x0248:
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r19 = "received channel difference with pts = ";
        r18 = r18.append(r19);
        r0 = r25;
        r0 = r0.pts;
        r19 = r0;
        r18 = r18.append(r19);
        r19 = " channelId = ";
        r18 = r18.append(r19);
        r0 = r18;
        r1 = r26;
        r18 = r0.append(r1);
        r18 = r18.toString();
        org.telegram.messenger.FileLog.d(r18);
        r18 = new java.lang.StringBuilder;
        r18.<init>();
        r19 = "new_messages = ";
        r18 = r18.append(r19);
        r0 = r25;
        r0 = r0.new_messages;
        r19 = r0;
        r19 = r19.size();
        r18 = r18.append(r19);
        r19 = " messages = ";
        r18 = r18.append(r19);
        r0 = r25;
        r0 = r0.messages;
        r19 = r0;
        r19 = r19.size();
        r18 = r18.append(r19);
        r19 = " users = ";
        r18 = r18.append(r19);
        r0 = r25;
        r0 = r0.users;
        r19 = r0;
        r19 = r19.size();
        r18 = r18.append(r19);
        r19 = " chats = ";
        r18 = r18.append(r19);
        r0 = r25;
        r0 = r0.chats;
        r19 = r0;
        r19 = r19.size();
        r18 = r18.append(r19);
        r19 = " other updates = ";
        r18 = r18.append(r19);
        r0 = r25;
        r0 = r0.other_updates;
        r19 = r0;
        r19 = r19.size();
        r18 = r18.append(r19);
        r18 = r18.toString();
        org.telegram.messenger.FileLog.d(r18);
    L_0x02e9:
        r18 = 0;
        r18 = (r30 > r18 ? 1 : (r30 == r18 ? 0 : -1));
        if (r18 == 0) goto L_0x0300;
    L_0x02ef:
        r0 = r24;
        r0 = r0.currentAccount;
        r18 = r0;
        r18 = org.telegram.messenger.MessagesStorage.getInstance(r18);
        r0 = r18;
        r1 = r30;
        r0.removePendingTask(r1);
    L_0x0300:
        return;
    L_0x0301:
        r0 = r25;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
        r18 = r0;
        if (r18 == 0) goto L_0x01e5;
    L_0x0309:
        r0 = r26;
        r0 = -r0;
        r18 = r0;
        r0 = r18;
        r8 = (long) r0;
        r0 = r24;
        r0 = r0.dialogs_read_inbox_max;
        r18 = r0;
        r19 = java.lang.Long.valueOf(r8);
        r10 = r18.get(r19);
        r10 = (java.lang.Integer) r10;
        if (r10 != 0) goto L_0x034c;
    L_0x0323:
        r0 = r24;
        r0 = r0.currentAccount;
        r18 = r0;
        r18 = org.telegram.messenger.MessagesStorage.getInstance(r18);
        r19 = 0;
        r0 = r18;
        r1 = r19;
        r18 = r0.getDialogReadMax(r1, r8);
        r10 = java.lang.Integer.valueOf(r18);
        r0 = r24;
        r0 = r0.dialogs_read_inbox_max;
        r18 = r0;
        r19 = java.lang.Long.valueOf(r8);
        r0 = r18;
        r1 = r19;
        r0.put(r1, r10);
    L_0x034c:
        r0 = r24;
        r0 = r0.dialogs_read_outbox_max;
        r18 = r0;
        r19 = java.lang.Long.valueOf(r8);
        r14 = r18.get(r19);
        r14 = (java.lang.Integer) r14;
        if (r14 != 0) goto L_0x0387;
    L_0x035e:
        r0 = r24;
        r0 = r0.currentAccount;
        r18 = r0;
        r18 = org.telegram.messenger.MessagesStorage.getInstance(r18);
        r19 = 1;
        r0 = r18;
        r1 = r19;
        r18 = r0.getDialogReadMax(r1, r8);
        r14 = java.lang.Integer.valueOf(r18);
        r0 = r24;
        r0 = r0.dialogs_read_outbox_max;
        r18 = r0;
        r19 = java.lang.Long.valueOf(r8);
        r0 = r18;
        r1 = r19;
        r0.put(r1, r14);
    L_0x0387:
        r6 = 0;
    L_0x0388:
        r0 = r25;
        r0 = r0.messages;
        r18 = r0;
        r18 = r18.size();
        r0 = r18;
        if (r6 >= r0) goto L_0x0403;
    L_0x0396:
        r0 = r25;
        r0 = r0.messages;
        r18 = r0;
        r0 = r18;
        r11 = r0.get(r6);
        r11 = (org.telegram.tgnet.TLRPC.Message) r11;
        r0 = r26;
        r0 = -r0;
        r18 = r0;
        r0 = r18;
        r0 = (long) r0;
        r18 = r0;
        r0 = r18;
        r11.dialog_id = r0;
        r0 = r11.action;
        r18 = r0;
        r0 = r18;
        r0 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        r18 = r0;
        if (r18 != 0) goto L_0x0400;
    L_0x03be:
        if (r27 == 0) goto L_0x03c8;
    L_0x03c0:
        r0 = r27;
        r0 = r0.left;
        r18 = r0;
        if (r18 != 0) goto L_0x0400;
    L_0x03c8:
        r0 = r11.out;
        r18 = r0;
        if (r18 == 0) goto L_0x03fd;
    L_0x03ce:
        r18 = r14;
    L_0x03d0:
        r18 = r18.intValue();
        r0 = r11.id;
        r19 = r0;
        r0 = r18;
        r1 = r19;
        if (r0 >= r1) goto L_0x0400;
    L_0x03de:
        r18 = 1;
    L_0x03e0:
        r0 = r18;
        r11.unread = r0;
        if (r27 == 0) goto L_0x03fa;
    L_0x03e6:
        r0 = r27;
        r0 = r0.megagroup;
        r18 = r0;
        if (r18 == 0) goto L_0x03fa;
    L_0x03ee:
        r0 = r11.flags;
        r18 = r0;
        r19 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r18 = r18 | r19;
        r0 = r18;
        r11.flags = r0;
    L_0x03fa:
        r6 = r6 + 1;
        goto L_0x0388;
    L_0x03fd:
        r18 = r10;
        goto L_0x03d0;
    L_0x0400:
        r18 = 0;
        goto L_0x03e0;
    L_0x0403:
        r0 = r24;
        r0 = r0.currentAccount;
        r18 = r0;
        r19 = org.telegram.messenger.MessagesStorage.getInstance(r18);
        r18 = r25;
        r18 = (org.telegram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong) r18;
        r0 = r19;
        r1 = r26;
        r2 = r18;
        r3 = r29;
        r0.overwriteChannel(r1, r2, r3);
        goto L_0x01e5;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$null$200$MessagesController(org.telegram.tgnet.TLRPC$updates_ChannelDifference, int, org.telegram.tgnet.TLRPC$Chat, android.util.SparseArray, int, long):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$197$MessagesController(LongSparseArray messages) {
        for (int a = 0; a < messages.size(); a++) {
            updateInterfaceWithMessages(messages.keyAt(a), (ArrayList) messages.valueAt(a));
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$199$MessagesController(ArrayList pushMessages, updates_ChannelDifference res) {
        if (!pushMessages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$188(this, pushMessages));
        }
        MessagesStorage.getInstance(this.currentAccount).putMessages(res.new_messages, true, false, false, DownloadController.getInstance(this.currentAccount).getAutodownloadMask());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$198$MessagesController(ArrayList pushMessages) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(pushMessages, true, false, null);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$202$MessagesController(TL_error error, int channelId) {
        checkChannelError(error.text, channelId);
    }

    private void checkChannelError(String text, int channelId) {
        int i = -1;
        switch (text.hashCode()) {
            case -1809401834:
                if (text.equals("USER_BANNED_IN_CHANNEL")) {
                    i = 2;
                    break;
                }
                break;
            case -795226617:
                if (text.equals("CHANNEL_PRIVATE")) {
                    i = 0;
                    break;
                }
                break;
            case -471086771:
                if (text.equals("CHANNEL_PUBLIC_GROUP_NA")) {
                    i = 1;
                    break;
                }
                break;
        }
        switch (i) {
            case 0:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(0));
                return;
            case 1:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(1));
                return;
            case 2:
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoCantLoad, Integer.valueOf(channelId), Integer.valueOf(2));
                return;
            default:
                return;
        }
    }

    public void getDifference() {
        getDifference(MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue(), false);
    }

    public void getDifference(int pts, int date, int qts, boolean slice) {
        registerForPush(SharedConfig.pushString);
        if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() == 0) {
            loadCurrentState();
        } else if (slice || !this.gettingDifference) {
            this.gettingDifference = true;
            TL_updates_getDifference req = new TL_updates_getDifference();
            req.pts = pts;
            req.date = date;
            req.qts = qts;
            if (this.getDifferenceFirstSync) {
                req.flags |= 1;
                if (ApplicationLoader.isConnectedOrConnectingToWiFi()) {
                    req.pts_total_limit = 5000;
                } else {
                    req.pts_total_limit = 1000;
                }
                this.getDifferenceFirstSync = false;
            }
            if (req.date == 0) {
                req.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("start getDifference with date = " + date + " pts = " + pts + " qts = " + qts);
            }
            ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(true);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$126(this, date, qts));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$getDifference$212$MessagesController(int date, int qts, TLObject response, TL_error error) {
        if (error == null) {
            updates_Difference res = (updates_Difference) response;
            if (res instanceof TL_updates_differenceTooLong) {
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$173(this, res, date, qts));
                return;
            }
            int a;
            if (res instanceof TL_updates_differenceSlice) {
                getDifference(res.intermediate_state.pts, res.intermediate_state.date, res.intermediate_state.qts, true);
            }
            SparseArray<User> usersDict = new SparseArray();
            SparseArray<Chat> chatsDict = new SparseArray();
            for (a = 0; a < res.users.size(); a++) {
                User user = (User) res.users.get(a);
                usersDict.put(user.id, user);
            }
            for (a = 0; a < res.chats.size(); a++) {
                Chat chat = (Chat) res.chats.get(a);
                chatsDict.put(chat.id, chat);
            }
            ArrayList<TL_updateMessageID> msgUpdates = new ArrayList();
            if (!res.other_updates.isEmpty()) {
                a = 0;
                while (a < res.other_updates.size()) {
                    Update upd = (Update) res.other_updates.get(a);
                    if (upd instanceof TL_updateMessageID) {
                        msgUpdates.add((TL_updateMessageID) upd);
                        res.other_updates.remove(a);
                        a--;
                    } else if (getUpdateType(upd) == 2) {
                        int channelId = getUpdateChannelId(upd);
                        int channelPts = this.channelsPts.get(channelId);
                        if (channelPts == 0) {
                            channelPts = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(channelId);
                            if (channelPts != 0) {
                                this.channelsPts.put(channelId, channelPts);
                            }
                        }
                        if (channelPts != 0 && getUpdatePts(upd) <= channelPts) {
                            res.other_updates.remove(a);
                            a--;
                        }
                    }
                    a++;
                }
            }
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$174(this, res));
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new MessagesController$$Lambda$175(this, res, msgUpdates, usersDict, chatsDict));
            return;
        }
        this.gettingDifference = false;
        ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$204$MessagesController(updates_Difference res, int date, int qts) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        resetDialogs(true, MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), res.pts, date, qts);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$205$MessagesController(updates_Difference res) {
        this.loadedFullUsers.clear();
        this.loadedFullChats.clear();
        putUsers(res.users, false);
        putChats(res.chats, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$211$MessagesController(updates_Difference res, ArrayList msgUpdates, SparseArray usersDict, SparseArray chatsDict) {
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, false);
        if (!msgUpdates.isEmpty()) {
            SparseArray<long[]> corrected = new SparseArray();
            for (int a = 0; a < msgUpdates.size(); a++) {
                TL_updateMessageID update = (TL_updateMessageID) msgUpdates.get(a);
                long[] ids = MessagesStorage.getInstance(this.currentAccount).updateMessageStateAndId(update.random_id, null, update.id, 0, false, 0);
                if (ids != null) {
                    corrected.put(update.id, ids);
                }
            }
            if (corrected.size() != 0) {
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$176(this, corrected));
            }
        }
        Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$177(this, res, usersDict, chatsDict));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$206$MessagesController(SparseArray corrected) {
        for (int a = 0; a < corrected.size(); a++) {
            int newId = corrected.keyAt(a);
            SendMessagesHelper.getInstance(this.currentAccount).processSentMessage((int) ((long[]) corrected.valueAt(a))[1]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newId), null, Long.valueOf(ids[0]), Long.valueOf(0), Integer.valueOf(-1));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$210$MessagesController(updates_Difference res, SparseArray usersDict, SparseArray chatsDict) {
        int a;
        if (!(res.new_messages.isEmpty() && res.new_encrypted_messages.isEmpty())) {
            LongSparseArray<ArrayList<MessageObject>> messages = new LongSparseArray();
            for (int b = 0; b < res.new_encrypted_messages.size(); b++) {
                ArrayList<Message> decryptedMessages = SecretChatHelper.getInstance(this.currentAccount).decryptMessage((EncryptedMessage) res.new_encrypted_messages.get(b));
                if (!(decryptedMessages == null || decryptedMessages.isEmpty())) {
                    res.new_messages.addAll(decryptedMessages);
                }
            }
            ImageLoader.saveMessagesThumbs(res.new_messages);
            ArrayList<MessageObject> pushMessages = new ArrayList();
            int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            for (a = 0; a < res.new_messages.size(); a++) {
                Message message = (Message) res.new_messages.get(a);
                if (message.dialog_id == 0) {
                    if (message.to_id.chat_id != 0) {
                        message.dialog_id = (long) (-message.to_id.chat_id);
                    } else {
                        if (message.to_id.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                            message.to_id.user_id = message.from_id;
                        }
                        message.dialog_id = (long) message.to_id.user_id;
                    }
                }
                if (((int) message.dialog_id) != 0) {
                    if (message.action instanceof TL_messageActionChatDeleteUser) {
                        User user = (User) usersDict.get(message.action.user_id);
                        if (user != null && user.bot) {
                            message.reply_markup = new TL_replyKeyboardHide();
                            message.flags |= 64;
                        }
                    }
                    if ((message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate)) {
                        message.unread = false;
                        message.media_unread = false;
                    } else {
                        boolean z;
                        ConcurrentHashMap<Long, Integer> read_max = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                        Integer value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                        if (value == null) {
                            value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                            read_max.put(Long.valueOf(message.dialog_id), value);
                        }
                        if (value.intValue() < message.id) {
                            z = true;
                        } else {
                            z = false;
                        }
                        message.unread = z;
                    }
                }
                if (message.dialog_id == ((long) clientUserId)) {
                    message.unread = false;
                    message.media_unread = false;
                    message.out = true;
                }
                MessageObject obj = new MessageObject(this.currentAccount, message, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                if (!obj.isOut() && obj.isUnread()) {
                    pushMessages.add(obj);
                }
                ArrayList<MessageObject> arr = (ArrayList) messages.get(message.dialog_id);
                if (arr == null) {
                    arr = new ArrayList();
                    messages.put(message.dialog_id, arr);
                }
                arr.add(obj);
            }
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$178(this, messages));
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new MessagesController$$Lambda$179(this, pushMessages, res));
            SecretChatHelper.getInstance(this.currentAccount).processPendingEncMessages();
        }
        if (!res.other_updates.isEmpty()) {
            processUpdateArray(res.other_updates, res.users, res.chats, true);
        }
        if (res instanceof TL_updates_difference) {
            this.gettingDifference = false;
            MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(res.state.seq);
            MessagesStorage.getInstance(this.currentAccount).setLastDateValue(res.state.date);
            MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(res.state.pts);
            MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(res.state.qts);
            ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(false);
            for (a = 0; a < 3; a++) {
                processUpdatesQueue(a, 1);
            }
        } else if (res instanceof TL_updates_differenceSlice) {
            MessagesStorage.getInstance(this.currentAccount).setLastDateValue(res.intermediate_state.date);
            MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(res.intermediate_state.pts);
            MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(res.intermediate_state.qts);
        } else if (res instanceof TL_updates_differenceEmpty) {
            this.gettingDifference = false;
            MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(res.seq);
            MessagesStorage.getInstance(this.currentAccount).setLastDateValue(res.date);
            ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(false);
            for (a = 0; a < 3; a++) {
                processUpdatesQueue(a, 1);
            }
        }
        MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("received difference with date = " + MessagesStorage.getInstance(this.currentAccount).getLastDateValue() + " pts = " + MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + " seq = " + MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + " messages = " + res.new_messages.size() + " users = " + res.users.size() + " chats = " + res.chats.size() + " other updates = " + res.other_updates.size());
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$207$MessagesController(LongSparseArray messages) {
        for (int a = 0; a < messages.size(); a++) {
            updateInterfaceWithMessages(messages.keyAt(a), (ArrayList) messages.valueAt(a));
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$209$MessagesController(ArrayList pushMessages, updates_Difference res) {
        if (!pushMessages.isEmpty()) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$180(this, pushMessages, res));
        }
        MessagesStorage.getInstance(this.currentAccount).putMessages(res.new_messages, true, false, false, DownloadController.getInstance(this.currentAccount).getAutodownloadMask());
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$208$MessagesController(ArrayList pushMessages, updates_Difference res) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(pushMessages, !(res instanceof TL_updates_differenceSlice), false, null);
    }

    public boolean canPinDialog(boolean secret) {
        int count = 0;
        for (int a = 0; a < this.dialogs.size(); a++) {
            TL_dialog dialog = (TL_dialog) this.dialogs.get(a);
            int lower_id = (int) dialog.id;
            if ((!secret || lower_id == 0) && ((secret || lower_id != 0) && dialog.pinned)) {
                count++;
            }
        }
        return count < this.maxPinnedDialogsCount;
    }

    public void markDialogAsUnread(long did, InputPeer peer, long taskId) {
        Throwable e;
        long newTaskId;
        TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(did);
        if (dialog != null) {
            dialog.unread_mark = true;
            if (dialog.unread_count == 0 && !isDialogMuted(did)) {
                this.unreadUnmutedDialogs++;
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
            MessagesStorage.getInstance(this.currentAccount).setDialogUnread(did, true);
        }
        int lower_id = (int) did;
        if (lower_id != 0) {
            TL_messages_markDialogUnread req = new TL_messages_markDialogUnread();
            req.unread = true;
            if (peer == null) {
                peer = getInputPeer(lower_id);
            }
            if (!(peer instanceof TL_inputPeerEmpty)) {
                TL_inputDialogPeer inputDialogPeer = new TL_inputDialogPeer();
                inputDialogPeer.peer = peer;
                req.peer = inputDialogPeer;
                if (taskId == 0) {
                    NativeByteBuffer data = null;
                    try {
                        NativeByteBuffer data2 = new NativeByteBuffer(peer.getObjectSize() + 12);
                        try {
                            data2.writeInt32(9);
                            data2.writeInt64(did);
                            peer.serializeToStream(data2);
                            data = data2;
                        } catch (Exception e2) {
                            e = e2;
                            data = data2;
                            FileLog.e(e);
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$127(this, newTaskId));
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$127(this, newTaskId));
                    }
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                } else {
                    newTaskId = taskId;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$127(this, newTaskId));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$markDialogAsUnread$213$MessagesController(long newTaskId, TLObject response, TL_error error) {
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
    }

    public void loadUnreadDialogs() {
        if (!this.loadingUnreadDialogs && !UserConfig.getInstance(this.currentAccount).unreadDialogsLoaded) {
            this.loadingUnreadDialogs = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getDialogUnreadMarks(), new MessagesController$$Lambda$128(this));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$loadUnreadDialogs$215$MessagesController(TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$172(this, response));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$214$MessagesController(TLObject response) {
        if (response != null) {
            Vector vector = (Vector) response;
            int size = vector.objects.size();
            for (int a = 0; a < size; a++) {
                DialogPeer peer = (DialogPeer) vector.objects.get(a);
                if (peer instanceof TL_dialogPeer) {
                    long did;
                    TL_dialogPeer dialogPeer = (TL_dialogPeer) peer;
                    if (dialogPeer.peer.user_id == 0) {
                        did = 0;
                    } else if (dialogPeer.peer.user_id != 0) {
                        did = (long) dialogPeer.peer.user_id;
                    } else if (dialogPeer.peer.chat_id != 0) {
                        did = (long) (-dialogPeer.peer.chat_id);
                    } else {
                        did = (long) (-dialogPeer.peer.channel_id);
                    }
                    MessagesStorage.getInstance(this.currentAccount).setDialogUnread(did, true);
                    TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(did);
                    if (!(dialog == null || dialog.unread_mark)) {
                        dialog.unread_mark = true;
                        if (dialog.unread_count == 0 && !isDialogMuted(did)) {
                            this.unreadUnmutedDialogs++;
                        }
                    }
                }
            }
            UserConfig.getInstance(this.currentAccount).unreadDialogsLoaded = true;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
            this.loadingUnreadDialogs = false;
        }
    }

    public boolean pinDialog(long did, boolean pin, InputPeer peer, long taskId) {
        Throwable e;
        long newTaskId;
        int lower_id = (int) did;
        TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(did);
        if (dialog != null && dialog.pinned != pin) {
            dialog.pinned = pin;
            if (pin) {
                int maxPinnedNum = 0;
                for (int a = 0; a < this.dialogs.size(); a++) {
                    TL_dialog d = (TL_dialog) this.dialogs.get(a);
                    if (!d.pinned) {
                        break;
                    }
                    maxPinnedNum = Math.max(d.pinnedNum, maxPinnedNum);
                }
                dialog.pinnedNum = maxPinnedNum + 1;
            } else {
                dialog.pinnedNum = 0;
            }
            sortDialogs(null);
            if (!(pin || this.dialogs.get(this.dialogs.size() - 1) != dialog || this.dialogsEndReached)) {
                this.dialogs.remove(this.dialogs.size() - 1);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            if (!(lower_id == 0 || taskId == -1)) {
                TL_messages_toggleDialogPin req = new TL_messages_toggleDialogPin();
                req.pinned = pin;
                if (peer == null) {
                    peer = getInputPeer(lower_id);
                }
                if (peer instanceof TL_inputPeerEmpty) {
                    return false;
                }
                TL_inputDialogPeer inputDialogPeer = new TL_inputDialogPeer();
                inputDialogPeer.peer = peer;
                req.peer = inputDialogPeer;
                if (taskId == 0) {
                    NativeByteBuffer data = null;
                    try {
                        NativeByteBuffer data2 = new NativeByteBuffer(peer.getObjectSize() + 16);
                        try {
                            data2.writeInt32(4);
                            data2.writeInt64(did);
                            data2.writeBool(pin);
                            peer.serializeToStream(data2);
                            data = data2;
                        } catch (Exception e2) {
                            e = e2;
                            data = data2;
                            FileLog.e(e);
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$129(this, newTaskId));
                            MessagesStorage.getInstance(this.currentAccount).setDialogPinned(did, dialog.pinnedNum);
                            return true;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$129(this, newTaskId));
                        MessagesStorage.getInstance(this.currentAccount).setDialogPinned(did, dialog.pinnedNum);
                        return true;
                    }
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                } else {
                    newTaskId = taskId;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$129(this, newTaskId));
            }
            MessagesStorage.getInstance(this.currentAccount).setDialogPinned(did, dialog.pinnedNum);
            return true;
        } else if (dialog != null) {
            return true;
        } else {
            return false;
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$pinDialog$216$MessagesController(long newTaskId, TLObject response, TL_error error) {
        if (newTaskId != 0) {
            MessagesStorage.getInstance(this.currentAccount).removePendingTask(newTaskId);
        }
    }

    public void loadPinnedDialogs(long newDialogId, ArrayList<Long> order) {
        if (!UserConfig.getInstance(this.currentAccount).pinnedDialogsLoaded) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getPinnedDialogs(), new MessagesController$$Lambda$130(this, order, newDialogId));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x0180  */
    /* JADX WARNING: Removed duplicated region for block: B:60:0x01aa  */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x01e2  */
    public final /* synthetic */ void lambda$loadPinnedDialogs$219$MessagesController(java.util.ArrayList r27, long r28, org.telegram.tgnet.TLObject r30, org.telegram.tgnet.TLRPC.TL_error r31) {
        /*
        r26 = this;
        if (r30 == 0) goto L_0x0222;
    L_0x0002:
        r9 = r30;
        r9 = (org.telegram.tgnet.TLRPC.TL_messages_peerDialogs) r9;
        r15 = new org.telegram.tgnet.TLRPC$TL_messages_dialogs;
        r15.<init>();
        r3 = r15.users;
        r7 = r9.users;
        r3.addAll(r7);
        r3 = r15.chats;
        r7 = r9.chats;
        r3.addAll(r7);
        r3 = r15.dialogs;
        r7 = r9.dialogs;
        r3.addAll(r7);
        r3 = r15.messages;
        r7 = r9.messages;
        r3.addAll(r7);
        r14 = new android.util.LongSparseArray;
        r14.<init>();
        r5 = new android.util.SparseArray;
        r5.<init>();
        r6 = new android.util.SparseArray;
        r6.<init>();
        r11 = new java.util.ArrayList;
        r11.<init>();
        r16 = 0;
    L_0x003d:
        r3 = r9.users;
        r3 = r3.size();
        r0 = r16;
        if (r0 >= r3) goto L_0x005d;
    L_0x0047:
        r3 = r9.users;
        r0 = r16;
        r21 = r3.get(r0);
        r21 = (org.telegram.tgnet.TLRPC.User) r21;
        r0 = r21;
        r3 = r0.id;
        r0 = r21;
        r5.put(r3, r0);
        r16 = r16 + 1;
        goto L_0x003d;
    L_0x005d:
        r16 = 0;
    L_0x005f:
        r3 = r9.chats;
        r3 = r3.size();
        r0 = r16;
        if (r0 >= r3) goto L_0x007f;
    L_0x0069:
        r3 = r9.chats;
        r0 = r16;
        r17 = r3.get(r0);
        r17 = (org.telegram.tgnet.TLRPC.Chat) r17;
        r0 = r17;
        r3 = r0.id;
        r0 = r17;
        r6.put(r3, r0);
        r16 = r16 + 1;
        goto L_0x005f;
    L_0x007f:
        r16 = 0;
    L_0x0081:
        r3 = r9.messages;
        r3 = r3.size();
        r0 = r16;
        if (r0 >= r3) goto L_0x00da;
    L_0x008b:
        r3 = r9.messages;
        r0 = r16;
        r4 = r3.get(r0);
        r4 = (org.telegram.tgnet.TLRPC.Message) r4;
        r3 = r4.to_id;
        r3 = r3.channel_id;
        if (r3 == 0) goto L_0x00b0;
    L_0x009b:
        r3 = r4.to_id;
        r3 = r3.channel_id;
        r18 = r6.get(r3);
        r18 = (org.telegram.tgnet.TLRPC.Chat) r18;
        if (r18 == 0) goto L_0x00c8;
    L_0x00a7:
        r0 = r18;
        r3 = r0.left;
        if (r3 == 0) goto L_0x00c8;
    L_0x00ad:
        r16 = r16 + 1;
        goto L_0x0081;
    L_0x00b0:
        r3 = r4.to_id;
        r3 = r3.chat_id;
        if (r3 == 0) goto L_0x00c8;
    L_0x00b6:
        r3 = r4.to_id;
        r3 = r3.chat_id;
        r18 = r6.get(r3);
        r18 = (org.telegram.tgnet.TLRPC.Chat) r18;
        if (r18 == 0) goto L_0x00c8;
    L_0x00c2:
        r0 = r18;
        r3 = r0.migrated_to;
        if (r3 != 0) goto L_0x00ad;
    L_0x00c8:
        r2 = new org.telegram.messenger.MessageObject;
        r0 = r26;
        r3 = r0.currentAccount;
        r7 = 0;
        r2.<init>(r3, r4, r5, r6, r7);
        r12 = r2.getDialogId();
        r14.put(r12, r2);
        goto L_0x00ad;
    L_0x00da:
        r16 = 0;
    L_0x00dc:
        r3 = r9.dialogs;
        r3 = r3.size();
        r0 = r16;
        if (r0 >= r3) goto L_0x0208;
    L_0x00e6:
        r3 = r9.dialogs;
        r0 = r16;
        r19 = r3.get(r0);
        r19 = (org.telegram.tgnet.TLRPC.TL_dialog) r19;
        r0 = r19;
        r12 = r0.id;
        r24 = 0;
        r3 = (r12 > r24 ? 1 : (r12 == r24 ? 0 : -1));
        if (r3 != 0) goto L_0x010d;
    L_0x00fa:
        r0 = r19;
        r3 = r0.peer;
        r3 = r3.user_id;
        if (r3 == 0) goto L_0x0135;
    L_0x0102:
        r0 = r19;
        r3 = r0.peer;
        r3 = r3.user_id;
        r12 = (long) r3;
        r0 = r19;
        r0.id = r12;
    L_0x010d:
        r0 = r19;
        r12 = r0.id;
        r3 = java.lang.Long.valueOf(r12);
        r11.add(r3);
        r3 = org.telegram.messenger.DialogObject.isChannel(r19);
        if (r3 == 0) goto L_0x015f;
    L_0x011e:
        r0 = r19;
        r12 = r0.id;
        r3 = (int) r12;
        r3 = -r3;
        r18 = r6.get(r3);
        r18 = (org.telegram.tgnet.TLRPC.Chat) r18;
        if (r18 == 0) goto L_0x017a;
    L_0x012c:
        r0 = r18;
        r3 = r0.left;
        if (r3 == 0) goto L_0x017a;
    L_0x0132:
        r16 = r16 + 1;
        goto L_0x00dc;
    L_0x0135:
        r0 = r19;
        r3 = r0.peer;
        r3 = r3.chat_id;
        if (r3 == 0) goto L_0x014a;
    L_0x013d:
        r0 = r19;
        r3 = r0.peer;
        r3 = r3.chat_id;
        r3 = -r3;
        r12 = (long) r3;
        r0 = r19;
        r0.id = r12;
        goto L_0x010d;
    L_0x014a:
        r0 = r19;
        r3 = r0.peer;
        r3 = r3.channel_id;
        if (r3 == 0) goto L_0x010d;
    L_0x0152:
        r0 = r19;
        r3 = r0.peer;
        r3 = r3.channel_id;
        r3 = -r3;
        r12 = (long) r3;
        r0 = r19;
        r0.id = r12;
        goto L_0x010d;
    L_0x015f:
        r0 = r19;
        r12 = r0.id;
        r3 = (int) r12;
        if (r3 >= 0) goto L_0x017a;
    L_0x0166:
        r0 = r19;
        r12 = r0.id;
        r3 = (int) r12;
        r3 = -r3;
        r18 = r6.get(r3);
        r18 = (org.telegram.tgnet.TLRPC.Chat) r18;
        if (r18 == 0) goto L_0x017a;
    L_0x0174:
        r0 = r18;
        r3 = r0.migrated_to;
        if (r3 != 0) goto L_0x0132;
    L_0x017a:
        r0 = r19;
        r3 = r0.last_message_date;
        if (r3 != 0) goto L_0x0196;
    L_0x0180:
        r0 = r19;
        r12 = r0.id;
        r20 = r14.get(r12);
        r20 = (org.telegram.messenger.MessageObject) r20;
        if (r20 == 0) goto L_0x0196;
    L_0x018c:
        r0 = r20;
        r3 = r0.messageOwner;
        r3 = r3.date;
        r0 = r19;
        r0.last_message_date = r3;
    L_0x0196:
        r0 = r26;
        r3 = r0.dialogs_read_inbox_max;
        r0 = r19;
        r12 = r0.id;
        r7 = java.lang.Long.valueOf(r12);
        r22 = r3.get(r7);
        r22 = (java.lang.Integer) r22;
        if (r22 != 0) goto L_0x01af;
    L_0x01aa:
        r3 = 0;
        r22 = java.lang.Integer.valueOf(r3);
    L_0x01af:
        r0 = r26;
        r3 = r0.dialogs_read_inbox_max;
        r0 = r19;
        r12 = r0.id;
        r7 = java.lang.Long.valueOf(r12);
        r8 = r22.intValue();
        r0 = r19;
        r10 = r0.read_inbox_max_id;
        r8 = java.lang.Math.max(r8, r10);
        r8 = java.lang.Integer.valueOf(r8);
        r3.put(r7, r8);
        r0 = r26;
        r3 = r0.dialogs_read_outbox_max;
        r0 = r19;
        r12 = r0.id;
        r7 = java.lang.Long.valueOf(r12);
        r22 = r3.get(r7);
        r22 = (java.lang.Integer) r22;
        if (r22 != 0) goto L_0x01e7;
    L_0x01e2:
        r3 = 0;
        r22 = java.lang.Integer.valueOf(r3);
    L_0x01e7:
        r0 = r26;
        r3 = r0.dialogs_read_outbox_max;
        r0 = r19;
        r12 = r0.id;
        r7 = java.lang.Long.valueOf(r12);
        r8 = r22.intValue();
        r0 = r19;
        r10 = r0.read_outbox_max_id;
        r8 = java.lang.Math.max(r8, r10);
        r8 = java.lang.Integer.valueOf(r8);
        r3.put(r7, r8);
        goto L_0x0132;
    L_0x0208:
        r0 = r26;
        r3 = r0.currentAccount;
        r3 = org.telegram.messenger.MessagesStorage.getInstance(r3);
        r3 = r3.getStorageQueue();
        r7 = new org.telegram.messenger.MessagesController$$Lambda$170;
        r8 = r26;
        r10 = r27;
        r12 = r28;
        r7.<init>(r8, r9, r10, r11, r12, r14, r15);
        r3.postRunnable(r7);
    L_0x0222:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$loadPinnedDialogs$219$MessagesController(java.util.ArrayList, long, org.telegram.tgnet.TLObject, org.telegram.tgnet.TLRPC$TL_error):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$218$MessagesController(TL_messages_peerDialogs res, ArrayList order, ArrayList newPinnedOrder, long newDialogId, LongSparseArray new_dialogMessage, TL_messages_dialogs toCache) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$171(this, res, order, newPinnedOrder, newDialogId, new_dialogMessage, toCache));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$217$MessagesController(TL_messages_peerDialogs res, ArrayList order, ArrayList newPinnedOrder, long newDialogId, LongSparseArray new_dialogMessage, TL_messages_dialogs toCache) {
        int a;
        TL_dialog dialog;
        ArrayList<Long> orderArrayList;
        applyDialogsNotificationsSettings(res.dialogs);
        boolean changed = false;
        boolean added = false;
        int maxPinnedNum = 0;
        LongSparseArray<Integer> oldPinnedDialogNums = new LongSparseArray();
        ArrayList<Long> oldPinnedOrder = new ArrayList();
        for (a = 0; a < this.dialogs.size(); a++) {
            dialog = (TL_dialog) this.dialogs.get(a);
            if (((int) dialog.id) != 0) {
                if (!dialog.pinned) {
                    break;
                }
                maxPinnedNum = Math.max(dialog.pinnedNum, maxPinnedNum);
                oldPinnedDialogNums.put(dialog.id, Integer.valueOf(dialog.pinnedNum));
                oldPinnedOrder.add(Long.valueOf(dialog.id));
                dialog.pinned = false;
                dialog.pinnedNum = 0;
                changed = true;
            }
        }
        ArrayList<Long> pinnedDialogs = new ArrayList();
        if (order != null) {
            orderArrayList = order;
        } else {
            orderArrayList = newPinnedOrder;
        }
        if (orderArrayList.size() < oldPinnedOrder.size()) {
            orderArrayList.add(Long.valueOf(0));
        }
        while (oldPinnedOrder.size() < orderArrayList.size()) {
            oldPinnedOrder.add(0, Long.valueOf(0));
        }
        if (!res.dialogs.isEmpty()) {
            putUsers(res.users, false);
            putChats(res.chats, false);
            for (a = 0; a < res.dialogs.size(); a++) {
                dialog = (TL_dialog) res.dialogs.get(a);
                Integer oldNum;
                if (newDialogId != 0) {
                    oldNum = (Integer) oldPinnedDialogNums.get(dialog.id);
                    if (oldNum != null) {
                        dialog.pinnedNum = oldNum.intValue();
                    }
                } else {
                    int oldIdx = oldPinnedOrder.indexOf(Long.valueOf(dialog.id));
                    int newIdx = orderArrayList.indexOf(Long.valueOf(dialog.id));
                    if (!(oldIdx == -1 || newIdx == -1)) {
                        if (oldIdx == newIdx) {
                            oldNum = (Integer) oldPinnedDialogNums.get(dialog.id);
                            if (oldNum != null) {
                                dialog.pinnedNum = oldNum.intValue();
                            }
                        } else {
                            oldNum = (Integer) oldPinnedDialogNums.get(((Long) oldPinnedOrder.get(newIdx)).longValue());
                            if (oldNum != null) {
                                dialog.pinnedNum = oldNum.intValue();
                            }
                        }
                    }
                }
                if (dialog.pinnedNum == 0) {
                    dialog.pinnedNum = (res.dialogs.size() - a) + maxPinnedNum;
                }
                pinnedDialogs.add(Long.valueOf(dialog.id));
                TL_dialog d = (TL_dialog) this.dialogs_dict.get(dialog.id);
                if (d != null) {
                    d.pinned = true;
                    d.pinnedNum = dialog.pinnedNum;
                    MessagesStorage.getInstance(this.currentAccount).setDialogPinned(dialog.id, dialog.pinnedNum);
                } else {
                    added = true;
                    this.dialogs_dict.put(dialog.id, dialog);
                    MessageObject messageObject = (MessageObject) new_dialogMessage.get(dialog.id);
                    this.dialogMessage.put(dialog.id, messageObject);
                    if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                        this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                        if (messageObject.messageOwner.random_id != 0) {
                            this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                        }
                    }
                }
                changed = true;
            }
        }
        if (changed) {
            if (added) {
                this.dialogs.clear();
                int size = this.dialogs_dict.size();
                for (a = 0; a < size; a++) {
                    this.dialogs.add(this.dialogs_dict.valueAt(a));
                }
            }
            sortDialogs(null);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        MessagesStorage.getInstance(this.currentAccount).unpinAllDialogsExceptNew(pinnedDialogs);
        MessagesStorage.getInstance(this.currentAccount).putDialogs(toCache, 1);
        UserConfig.getInstance(this.currentAccount).pinnedDialogsLoaded = true;
        UserConfig.getInstance(this.currentAccount).saveConfig(false);
    }

    public void generateJoinMessage(int chat_id, boolean ignoreLeft) {
        Chat chat = getChat(Integer.valueOf(chat_id));
        if (chat != null && ChatObject.isChannel(chat_id, this.currentAccount)) {
            if ((!chat.left && !chat.kicked) || ignoreLeft) {
                TL_messageService message = new TL_messageService();
                message.flags = 256;
                int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                message.id = newMessageId;
                message.local_id = newMessageId;
                message.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                message.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                message.to_id = new TL_peerChannel();
                message.to_id.channel_id = chat_id;
                message.dialog_id = (long) (-chat_id);
                message.post = true;
                message.action = new TL_messageActionChatAddUser();
                message.action.users.add(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                if (chat.megagroup) {
                    message.flags |= Integer.MIN_VALUE;
                }
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ArrayList<MessageObject> pushMessages = new ArrayList();
                ArrayList messagesArr = new ArrayList();
                messagesArr.add(message);
                pushMessages.add(new MessageObject(this.currentAccount, message, true));
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new MessagesController$$Lambda$131(this, pushMessages));
                MessagesStorage.getInstance(this.currentAccount).putMessages(messagesArr, true, true, false, 0);
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$132(this, chat_id, pushMessages));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$generateJoinMessage$221$MessagesController(ArrayList pushMessages) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$169(this, pushMessages));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$220$MessagesController(ArrayList pushMessages) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(pushMessages, true, false, null);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$generateJoinMessage$222$MessagesController(int chat_id, ArrayList pushMessages) {
        updateInterfaceWithMessages((long) (-chat_id), pushMessages);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void checkChannelInviter(int chat_id) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$133(this, chat_id));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkChannelInviter$228$MessagesController(int chat_id) {
        Chat chat = getChat(Integer.valueOf(chat_id));
        if (chat != null && ChatObject.isChannel(chat_id, this.currentAccount) && !chat.creator) {
            TL_channels_getParticipant req = new TL_channels_getParticipant();
            req.channel = getInputChannel(chat_id);
            req.user_id = new TL_inputUserSelf();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$164(this, chat, chat_id));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$227$MessagesController(Chat chat, int chat_id, TLObject response, TL_error error) {
        TL_channels_channelParticipant res = (TL_channels_channelParticipant) response;
        if (res != null && (res.participant instanceof TL_channelParticipantSelf) && res.participant.inviter_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            if (!chat.megagroup || !MessagesStorage.getInstance(this.currentAccount).isMigratedChat(chat.id)) {
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$165(this, res));
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, null, true, true);
                Message message = new TL_messageService();
                message.media_unread = true;
                message.unread = true;
                message.flags = 256;
                message.post = true;
                if (chat.megagroup) {
                    message.flags |= Integer.MIN_VALUE;
                }
                int newMessageId = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                message.id = newMessageId;
                message.local_id = newMessageId;
                message.date = res.participant.date;
                message.action = new TL_messageActionChatAddUser();
                message.from_id = res.participant.inviter_id;
                message.action.users.add(Integer.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
                message.to_id = new TL_peerChannel();
                message.to_id.channel_id = chat_id;
                message.dialog_id = (long) (-chat_id);
                UserConfig.getInstance(this.currentAccount).saveConfig(false);
                ArrayList<MessageObject> pushMessages = new ArrayList();
                ArrayList messagesArr = new ArrayList();
                AbstractMap usersDict = new ConcurrentHashMap();
                for (int a = 0; a < res.users.size(); a++) {
                    User user = (User) res.users.get(a);
                    usersDict.put(Integer.valueOf(user.id), user);
                }
                messagesArr.add(message);
                pushMessages.add(new MessageObject(this.currentAccount, message, usersDict, true));
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new MessagesController$$Lambda$166(this, pushMessages));
                MessagesStorage.getInstance(this.currentAccount).putMessages(messagesArr, true, true, false, 0);
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$167(this, chat_id, pushMessages));
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$223$MessagesController(TL_channels_channelParticipant res) {
        putUsers(res.users, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$224$MessagesController(ArrayList pushMessages) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(pushMessages, true, false, null);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$225$MessagesController(ArrayList pushMessages) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$168(this, pushMessages));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$226$MessagesController(int chat_id, ArrayList pushMessages) {
        updateInterfaceWithMessages((long) (-chat_id), pushMessages);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    private int getUpdateType(Update update) {
        if ((update instanceof TL_updateNewMessage) || (update instanceof TL_updateReadMessagesContents) || (update instanceof TL_updateReadHistoryInbox) || (update instanceof TL_updateReadHistoryOutbox) || (update instanceof TL_updateDeleteMessages) || (update instanceof TL_updateWebPage) || (update instanceof TL_updateEditMessage)) {
            return 0;
        }
        if (update instanceof TL_updateNewEncryptedMessage) {
            return 1;
        }
        if ((update instanceof TL_updateNewChannelMessage) || (update instanceof TL_updateDeleteChannelMessages) || (update instanceof TL_updateEditChannelMessage) || (update instanceof TL_updateChannelWebPage)) {
            return 2;
        }
        return 3;
    }

    private static int getUpdatePts(Update update) {
        if (update instanceof TL_updateDeleteMessages) {
            return ((TL_updateDeleteMessages) update).pts;
        }
        if (update instanceof TL_updateNewChannelMessage) {
            return ((TL_updateNewChannelMessage) update).pts;
        }
        if (update instanceof TL_updateReadHistoryOutbox) {
            return ((TL_updateReadHistoryOutbox) update).pts;
        }
        if (update instanceof TL_updateNewMessage) {
            return ((TL_updateNewMessage) update).pts;
        }
        if (update instanceof TL_updateEditMessage) {
            return ((TL_updateEditMessage) update).pts;
        }
        if (update instanceof TL_updateWebPage) {
            return ((TL_updateWebPage) update).pts;
        }
        if (update instanceof TL_updateReadHistoryInbox) {
            return ((TL_updateReadHistoryInbox) update).pts;
        }
        if (update instanceof TL_updateChannelWebPage) {
            return ((TL_updateChannelWebPage) update).pts;
        }
        if (update instanceof TL_updateDeleteChannelMessages) {
            return ((TL_updateDeleteChannelMessages) update).pts;
        }
        if (update instanceof TL_updateEditChannelMessage) {
            return ((TL_updateEditChannelMessage) update).pts;
        }
        if (update instanceof TL_updateReadMessagesContents) {
            return ((TL_updateReadMessagesContents) update).pts;
        }
        if (update instanceof TL_updateChannelTooLong) {
            return ((TL_updateChannelTooLong) update).pts;
        }
        return 0;
    }

    private static int getUpdatePtsCount(Update update) {
        if (update instanceof TL_updateDeleteMessages) {
            return ((TL_updateDeleteMessages) update).pts_count;
        }
        if (update instanceof TL_updateNewChannelMessage) {
            return ((TL_updateNewChannelMessage) update).pts_count;
        }
        if (update instanceof TL_updateReadHistoryOutbox) {
            return ((TL_updateReadHistoryOutbox) update).pts_count;
        }
        if (update instanceof TL_updateNewMessage) {
            return ((TL_updateNewMessage) update).pts_count;
        }
        if (update instanceof TL_updateEditMessage) {
            return ((TL_updateEditMessage) update).pts_count;
        }
        if (update instanceof TL_updateWebPage) {
            return ((TL_updateWebPage) update).pts_count;
        }
        if (update instanceof TL_updateReadHistoryInbox) {
            return ((TL_updateReadHistoryInbox) update).pts_count;
        }
        if (update instanceof TL_updateChannelWebPage) {
            return ((TL_updateChannelWebPage) update).pts_count;
        }
        if (update instanceof TL_updateDeleteChannelMessages) {
            return ((TL_updateDeleteChannelMessages) update).pts_count;
        }
        if (update instanceof TL_updateEditChannelMessage) {
            return ((TL_updateEditChannelMessage) update).pts_count;
        }
        if (update instanceof TL_updateReadMessagesContents) {
            return ((TL_updateReadMessagesContents) update).pts_count;
        }
        return 0;
    }

    private static int getUpdateQts(Update update) {
        if (update instanceof TL_updateNewEncryptedMessage) {
            return ((TL_updateNewEncryptedMessage) update).qts;
        }
        return 0;
    }

    private static int getUpdateChannelId(Update update) {
        if (update instanceof TL_updateNewChannelMessage) {
            return ((TL_updateNewChannelMessage) update).message.to_id.channel_id;
        }
        if (update instanceof TL_updateEditChannelMessage) {
            return ((TL_updateEditChannelMessage) update).message.to_id.channel_id;
        }
        if (update instanceof TL_updateReadChannelOutbox) {
            return ((TL_updateReadChannelOutbox) update).channel_id;
        }
        if (update instanceof TL_updateChannelMessageViews) {
            return ((TL_updateChannelMessageViews) update).channel_id;
        }
        if (update instanceof TL_updateChannelTooLong) {
            return ((TL_updateChannelTooLong) update).channel_id;
        }
        if (update instanceof TL_updateChannelPinnedMessage) {
            return ((TL_updateChannelPinnedMessage) update).channel_id;
        }
        if (update instanceof TL_updateChannelReadMessagesContents) {
            return ((TL_updateChannelReadMessagesContents) update).channel_id;
        }
        if (update instanceof TL_updateChannelAvailableMessages) {
            return ((TL_updateChannelAvailableMessages) update).channel_id;
        }
        if (update instanceof TL_updateChannel) {
            return ((TL_updateChannel) update).channel_id;
        }
        if (update instanceof TL_updateChannelWebPage) {
            return ((TL_updateChannelWebPage) update).channel_id;
        }
        if (update instanceof TL_updateDeleteChannelMessages) {
            return ((TL_updateDeleteChannelMessages) update).channel_id;
        }
        if (update instanceof TL_updateReadChannelInbox) {
            return ((TL_updateReadChannelInbox) update).channel_id;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.e("trying to get unknown update channel_id for " + update);
        }
        return 0;
    }

    /* JADX WARNING: Removed duplicated region for block: B:131:0x03d9  */
    public void processUpdates(org.telegram.tgnet.TLRPC.Updates r57, boolean r58) {
        /*
        r56 = this;
        r32 = 0;
        r33 = 0;
        r34 = 0;
        r47 = 0;
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShort;
        if (r4 == 0) goto L_0x005a;
    L_0x000e:
        r11 = new java.util.ArrayList;
        r11.<init>();
        r0 = r57;
        r4 = r0.update;
        r11.add(r4);
        r4 = 0;
        r6 = 0;
        r7 = 0;
        r0 = r56;
        r0.processUpdateArray(r11, r4, r6, r7);
    L_0x0022:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.SecretChatHelper.getInstance(r4);
        r4.processPendingEncMessages();
        if (r58 != 0) goto L_0x0d7f;
    L_0x002f:
        r10 = 0;
    L_0x0030:
        r0 = r56;
        r4 = r0.updatesQueueChannels;
        r4 = r4.size();
        if (r10 >= r4) goto L_0x0d7a;
    L_0x003a:
        r0 = r56;
        r4 = r0.updatesQueueChannels;
        r26 = r4.keyAt(r10);
        if (r32 == 0) goto L_0x0d70;
    L_0x0044:
        r4 = java.lang.Integer.valueOf(r26);
        r0 = r32;
        r4 = r0.contains(r4);
        if (r4 == 0) goto L_0x0d70;
    L_0x0050:
        r0 = r56;
        r1 = r26;
        r0.getChannelDifference(r1);
    L_0x0057:
        r10 = r10 + 1;
        goto L_0x0030;
    L_0x005a:
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortChatMessage;
        if (r4 != 0) goto L_0x0066;
    L_0x0060:
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r4 == 0) goto L_0x0520;
    L_0x0066:
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortChatMessage;
        if (r4 == 0) goto L_0x01f8;
    L_0x006c:
        r0 = r57;
        r0 = r0.from_id;
        r54 = r0;
    L_0x0072:
        r4 = java.lang.Integer.valueOf(r54);
        r0 = r56;
        r49 = r0.getUser(r4);
        r52 = 0;
        r53 = 0;
        r16 = 0;
        if (r49 == 0) goto L_0x008a;
    L_0x0084:
        r0 = r49;
        r4 = r0.min;
        if (r4 == 0) goto L_0x00aa;
    L_0x008a:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r54;
        r49 = r4.getUserSync(r0);
        if (r49 == 0) goto L_0x00a2;
    L_0x009a:
        r0 = r49;
        r4 = r0.min;
        if (r4 == 0) goto L_0x00a2;
    L_0x00a0:
        r49 = 0;
    L_0x00a2:
        r4 = 1;
        r0 = r56;
        r1 = r49;
        r0.putUser(r1, r4);
    L_0x00aa:
        r31 = 0;
        r0 = r57;
        r4 = r0.fwd_from;
        if (r4 == 0) goto L_0x011e;
    L_0x00b2:
        r0 = r57;
        r4 = r0.fwd_from;
        r4 = r4.from_id;
        if (r4 == 0) goto L_0x00e8;
    L_0x00ba:
        r0 = r57;
        r4 = r0.fwd_from;
        r4 = r4.from_id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r56;
        r52 = r0.getUser(r4);
        if (r52 != 0) goto L_0x00e6;
    L_0x00cc:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.fwd_from;
        r6 = r6.from_id;
        r52 = r4.getUserSync(r6);
        r4 = 1;
        r0 = r56;
        r1 = r52;
        r0.putUser(r1, r4);
    L_0x00e6:
        r31 = 1;
    L_0x00e8:
        r0 = r57;
        r4 = r0.fwd_from;
        r4 = r4.channel_id;
        if (r4 == 0) goto L_0x011e;
    L_0x00f0:
        r0 = r57;
        r4 = r0.fwd_from;
        r4 = r4.channel_id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r56;
        r16 = r0.getChat(r4);
        if (r16 != 0) goto L_0x011c;
    L_0x0102:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.fwd_from;
        r6 = r6.channel_id;
        r16 = r4.getChatSync(r6);
        r4 = 1;
        r0 = r56;
        r1 = r16;
        r0.putChat(r1, r4);
    L_0x011c:
        r31 = 1;
    L_0x011e:
        r30 = 0;
        r0 = r57;
        r4 = r0.via_bot_id;
        if (r4 == 0) goto L_0x0150;
    L_0x0126:
        r0 = r57;
        r4 = r0.via_bot_id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r56;
        r53 = r0.getUser(r4);
        if (r53 != 0) goto L_0x014e;
    L_0x0136:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.via_bot_id;
        r53 = r4.getUserSync(r6);
        r4 = 1;
        r0 = r56;
        r1 = r53;
        r0.putUser(r1, r4);
    L_0x014e:
        r30 = 1;
    L_0x0150:
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r4 == 0) goto L_0x0204;
    L_0x0156:
        if (r49 == 0) goto L_0x0162;
    L_0x0158:
        if (r31 == 0) goto L_0x015e;
    L_0x015a:
        if (r52 != 0) goto L_0x015e;
    L_0x015c:
        if (r16 == 0) goto L_0x0162;
    L_0x015e:
        if (r30 == 0) goto L_0x0200;
    L_0x0160:
        if (r53 != 0) goto L_0x0200;
    L_0x0162:
        r29 = 1;
    L_0x0164:
        if (r29 != 0) goto L_0x01c1;
    L_0x0166:
        r0 = r57;
        r4 = r0.entities;
        r4 = r4.isEmpty();
        if (r4 != 0) goto L_0x01c1;
    L_0x0170:
        r10 = 0;
    L_0x0171:
        r0 = r57;
        r4 = r0.entities;
        r4 = r4.size();
        if (r10 >= r4) goto L_0x01c1;
    L_0x017b:
        r0 = r57;
        r4 = r0.entities;
        r22 = r4.get(r10);
        r22 = (org.telegram.tgnet.TLRPC.MessageEntity) r22;
        r0 = r22;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r4 == 0) goto L_0x0249;
    L_0x018b:
        r22 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r22;
        r0 = r22;
        r0 = r0.user_id;
        r44 = r0;
        r4 = java.lang.Integer.valueOf(r44);
        r0 = r56;
        r23 = r0.getUser(r4);
        if (r23 == 0) goto L_0x01a5;
    L_0x019f:
        r0 = r23;
        r4 = r0.min;
        if (r4 == 0) goto L_0x0249;
    L_0x01a5:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r44;
        r23 = r4.getUserSync(r0);
        if (r23 == 0) goto L_0x01bd;
    L_0x01b5:
        r0 = r23;
        r4 = r0.min;
        if (r4 == 0) goto L_0x01bd;
    L_0x01bb:
        r23 = 0;
    L_0x01bd:
        if (r23 != 0) goto L_0x0241;
    L_0x01bf:
        r29 = 1;
    L_0x01c1:
        if (r49 == 0) goto L_0x01f2;
    L_0x01c3:
        r0 = r49;
        r4 = r0.status;
        if (r4 == 0) goto L_0x01f2;
    L_0x01c9:
        r0 = r49;
        r4 = r0.status;
        r4 = r4.expires;
        if (r4 > 0) goto L_0x01f2;
    L_0x01d1:
        r0 = r56;
        r4 = r0.onlinePrivacy;
        r0 = r49;
        r6 = r0.id;
        r6 = java.lang.Integer.valueOf(r6);
        r0 = r56;
        r7 = r0.currentAccount;
        r7 = org.telegram.tgnet.ConnectionsManager.getInstance(r7);
        r7 = r7.getCurrentTime();
        r7 = java.lang.Integer.valueOf(r7);
        r4.put(r6, r7);
        r47 = 1;
    L_0x01f2:
        if (r29 == 0) goto L_0x024d;
    L_0x01f4:
        r33 = 1;
        goto L_0x0022;
    L_0x01f8:
        r0 = r57;
        r0 = r0.user_id;
        r54 = r0;
        goto L_0x0072;
    L_0x0200:
        r29 = 0;
        goto L_0x0164;
    L_0x0204:
        r0 = r57;
        r4 = r0.chat_id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r56;
        r19 = r0.getChat(r4);
        if (r19 != 0) goto L_0x022c;
    L_0x0214:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.chat_id;
        r19 = r4.getChatSync(r6);
        r4 = 1;
        r0 = r56;
        r1 = r19;
        r0.putChat(r1, r4);
    L_0x022c:
        if (r19 == 0) goto L_0x023a;
    L_0x022e:
        if (r49 == 0) goto L_0x023a;
    L_0x0230:
        if (r31 == 0) goto L_0x0236;
    L_0x0232:
        if (r52 != 0) goto L_0x0236;
    L_0x0234:
        if (r16 == 0) goto L_0x023a;
    L_0x0236:
        if (r30 == 0) goto L_0x023e;
    L_0x0238:
        if (r53 != 0) goto L_0x023e;
    L_0x023a:
        r29 = 1;
    L_0x023c:
        goto L_0x0164;
    L_0x023e:
        r29 = 0;
        goto L_0x023c;
    L_0x0241:
        r4 = 1;
        r0 = r56;
        r1 = r49;
        r0.putUser(r1, r4);
    L_0x0249:
        r10 = r10 + 1;
        goto L_0x0171;
    L_0x024d:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastPtsValue();
        r0 = r57;
        r6 = r0.pts_count;
        r4 = r4 + r6;
        r0 = r57;
        r6 = r0.pts;
        if (r4 != r6) goto L_0x047b;
    L_0x0264:
        r27 = new org.telegram.tgnet.TLRPC$TL_message;
        r27.<init>();
        r0 = r57;
        r4 = r0.id;
        r0 = r27;
        r0.id = r4;
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.UserConfig.getInstance(r4);
        r20 = r4.getClientUserId();
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r4 == 0) goto L_0x0423;
    L_0x0283:
        r0 = r57;
        r4 = r0.out;
        if (r4 == 0) goto L_0x041b;
    L_0x0289:
        r0 = r20;
        r1 = r27;
        r1.from_id = r0;
    L_0x028f:
        r4 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r4.<init>();
        r0 = r27;
        r0.to_id = r4;
        r0 = r27;
        r4 = r0.to_id;
        r0 = r54;
        r4.user_id = r0;
        r0 = r54;
        r6 = (long) r0;
        r0 = r27;
        r0.dialog_id = r6;
    L_0x02a7:
        r0 = r57;
        r4 = r0.fwd_from;
        r0 = r27;
        r0.fwd_from = r4;
        r0 = r57;
        r4 = r0.silent;
        r0 = r27;
        r0.silent = r4;
        r0 = r57;
        r4 = r0.out;
        r0 = r27;
        r0.out = r4;
        r0 = r57;
        r4 = r0.mentioned;
        r0 = r27;
        r0.mentioned = r4;
        r0 = r57;
        r4 = r0.media_unread;
        r0 = r27;
        r0.media_unread = r4;
        r0 = r57;
        r4 = r0.entities;
        r0 = r27;
        r0.entities = r4;
        r0 = r57;
        r4 = r0.message;
        r0 = r27;
        r0.message = r4;
        r0 = r57;
        r4 = r0.date;
        r0 = r27;
        r0.date = r4;
        r0 = r57;
        r4 = r0.via_bot_id;
        r0 = r27;
        r0.via_bot_id = r4;
        r0 = r57;
        r4 = r0.flags;
        r4 = r4 | 256;
        r0 = r27;
        r0.flags = r4;
        r0 = r57;
        r4 = r0.reply_to_msg_id;
        r0 = r27;
        r0.reply_to_msg_id = r4;
        r4 = new org.telegram.tgnet.TLRPC$TL_messageMediaEmpty;
        r4.<init>();
        r0 = r27;
        r0.media = r4;
        r0 = r27;
        r4 = r0.out;
        if (r4 == 0) goto L_0x0448;
    L_0x0310:
        r0 = r56;
        r0 = r0.dialogs_read_outbox_max;
        r41 = r0;
    L_0x0316:
        r0 = r27;
        r6 = r0.dialog_id;
        r4 = java.lang.Long.valueOf(r6);
        r0 = r41;
        r55 = r0.get(r4);
        r55 = (java.lang.Integer) r55;
        if (r55 != 0) goto L_0x034f;
    L_0x0328:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r27;
        r6 = r0.out;
        r0 = r27;
        r8 = r0.dialog_id;
        r4 = r4.getDialogReadMax(r6, r8);
        r55 = java.lang.Integer.valueOf(r4);
        r0 = r27;
        r6 = r0.dialog_id;
        r4 = java.lang.Long.valueOf(r6);
        r0 = r41;
        r1 = r55;
        r0.put(r4, r1);
    L_0x034f:
        r4 = r55.intValue();
        r0 = r27;
        r6 = r0.id;
        if (r4 >= r6) goto L_0x0450;
    L_0x0359:
        r4 = 1;
    L_0x035a:
        r0 = r27;
        r0.unread = r4;
        r0 = r27;
        r6 = r0.dialog_id;
        r0 = r20;
        r8 = (long) r0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0378;
    L_0x0369:
        r4 = 0;
        r0 = r27;
        r0.unread = r4;
        r4 = 0;
        r0 = r27;
        r0.media_unread = r4;
        r4 = 1;
        r0 = r27;
        r0.out = r4;
    L_0x0378:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.pts;
        r4.setLastPtsValue(r6);
        r35 = new org.telegram.messenger.MessageObject;
        r0 = r56;
        r4 = r0.currentAccount;
        r0 = r56;
        r6 = r0.createdDialogIds;
        r0 = r27;
        r8 = r0.dialog_id;
        r7 = java.lang.Long.valueOf(r8);
        r6 = r6.contains(r7);
        r0 = r35;
        r1 = r27;
        r0.<init>(r4, r1, r6);
        r36 = new java.util.ArrayList;
        r36.<init>();
        r0 = r36;
        r1 = r35;
        r0.add(r1);
        r5 = new java.util.ArrayList;
        r5.<init>();
        r0 = r27;
        r5.add(r0);
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateShortMessage;
        if (r4 == 0) goto L_0x0456;
    L_0x03c0:
        r0 = r57;
        r4 = r0.out;
        if (r4 != 0) goto L_0x0453;
    L_0x03c6:
        r0 = r57;
        r4 = r0.user_id;
        r6 = (long) r4;
        r0 = r56;
        r1 = r36;
        r4 = r0.updatePrintingUsersWithNewMessages(r6, r1);
        if (r4 == 0) goto L_0x0453;
    L_0x03d5:
        r37 = 1;
    L_0x03d7:
        if (r37 == 0) goto L_0x03dc;
    L_0x03d9:
        r56.updatePrintingStrings();
    L_0x03dc:
        r4 = new org.telegram.messenger.MessagesController$$Lambda$134;
        r0 = r56;
        r1 = r37;
        r2 = r54;
        r3 = r36;
        r4.<init>(r0, r1, r2, r3);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
    L_0x03ec:
        r4 = r35.isOut();
        if (r4 != 0) goto L_0x040a;
    L_0x03f2:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getStorageQueue();
        r6 = new org.telegram.messenger.MessagesController$$Lambda$136;
        r0 = r56;
        r1 = r36;
        r6.<init>(r0, r1);
        r4.postRunnable(r6);
    L_0x040a:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r6 = 0;
        r7 = 1;
        r8 = 0;
        r9 = 0;
        r4.putMessages(r5, r6, r7, r8, r9);
        goto L_0x0022;
    L_0x041b:
        r0 = r54;
        r1 = r27;
        r1.from_id = r0;
        goto L_0x028f;
    L_0x0423:
        r0 = r54;
        r1 = r27;
        r1.from_id = r0;
        r4 = new org.telegram.tgnet.TLRPC$TL_peerChat;
        r4.<init>();
        r0 = r27;
        r0.to_id = r4;
        r0 = r27;
        r4 = r0.to_id;
        r0 = r57;
        r6 = r0.chat_id;
        r4.chat_id = r6;
        r0 = r57;
        r4 = r0.chat_id;
        r4 = -r4;
        r6 = (long) r4;
        r0 = r27;
        r0.dialog_id = r6;
        goto L_0x02a7;
    L_0x0448:
        r0 = r56;
        r0 = r0.dialogs_read_inbox_max;
        r41 = r0;
        goto L_0x0316;
    L_0x0450:
        r4 = 0;
        goto L_0x035a;
    L_0x0453:
        r37 = 0;
        goto L_0x03d7;
    L_0x0456:
        r0 = r57;
        r4 = r0.chat_id;
        r4 = -r4;
        r6 = (long) r4;
        r0 = r56;
        r1 = r36;
        r37 = r0.updatePrintingUsersWithNewMessages(r6, r1);
        if (r37 == 0) goto L_0x0469;
    L_0x0466:
        r56.updatePrintingStrings();
    L_0x0469:
        r4 = new org.telegram.messenger.MessagesController$$Lambda$135;
        r0 = r56;
        r1 = r37;
        r2 = r57;
        r3 = r36;
        r4.<init>(r0, r1, r2, r3);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
        goto L_0x03ec;
    L_0x047b:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastPtsValue();
        r0 = r57;
        r6 = r0.pts;
        if (r4 == r6) goto L_0x0022;
    L_0x048d:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x04d2;
    L_0x0491:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = "need get diff short message, pts: ";
        r4 = r4.append(r6);
        r0 = r56;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastPtsValue();
        r4 = r4.append(r6);
        r6 = " ";
        r4 = r4.append(r6);
        r0 = r57;
        r6 = r0.pts;
        r4 = r4.append(r6);
        r6 = " count = ";
        r4 = r4.append(r6);
        r0 = r57;
        r6 = r0.pts_count;
        r4 = r4.append(r6);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x04d2:
        r0 = r56;
        r4 = r0.gettingDifference;
        if (r4 != 0) goto L_0x04f5;
    L_0x04d8:
        r0 = r56;
        r6 = r0.updatesStartWaitTimePts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x04f5;
    L_0x04e2:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r56;
        r8 = r0.updatesStartWaitTimePts;
        r6 = r6 - r8;
        r6 = java.lang.Math.abs(r6);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 > 0) goto L_0x051c;
    L_0x04f5:
        r0 = r56;
        r6 = r0.updatesStartWaitTimePts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0507;
    L_0x04ff:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r56;
        r0.updatesStartWaitTimePts = r6;
    L_0x0507:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0511;
    L_0x050b:
        r4 = "add to queue";
        org.telegram.messenger.FileLog.d(r4);
    L_0x0511:
        r0 = r56;
        r4 = r0.updatesQueuePts;
        r0 = r57;
        r4.add(r0);
        goto L_0x0022;
    L_0x051c:
        r33 = 1;
        goto L_0x0022;
    L_0x0520:
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatesCombined;
        if (r4 != 0) goto L_0x052c;
    L_0x0526:
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updates;
        if (r4 == 0) goto L_0x0d04;
    L_0x052c:
        r28 = 0;
        r10 = 0;
    L_0x052f:
        r0 = r57;
        r4 = r0.chats;
        r4 = r4.size();
        if (r10 >= r4) goto L_0x059a;
    L_0x0539:
        r0 = r57;
        r4 = r0.chats;
        r19 = r4.get(r10);
        r19 = (org.telegram.tgnet.TLRPC.Chat) r19;
        r0 = r19;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_channel;
        if (r4 == 0) goto L_0x0597;
    L_0x0549:
        r0 = r19;
        r4 = r0.min;
        if (r4 == 0) goto L_0x0597;
    L_0x054f:
        r0 = r19;
        r4 = r0.id;
        r4 = java.lang.Integer.valueOf(r4);
        r0 = r56;
        r24 = r0.getChat(r4);
        if (r24 == 0) goto L_0x0565;
    L_0x055f:
        r0 = r24;
        r4 = r0.min;
        if (r4 == 0) goto L_0x057d;
    L_0x0565:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.chat_id;
        r15 = r4.getChatSync(r6);
        r4 = 1;
        r0 = r56;
        r0.putChat(r15, r4);
        r24 = r15;
    L_0x057d:
        if (r24 == 0) goto L_0x0585;
    L_0x057f:
        r0 = r24;
        r4 = r0.min;
        if (r4 == 0) goto L_0x0597;
    L_0x0585:
        if (r28 != 0) goto L_0x058c;
    L_0x0587:
        r28 = new android.util.SparseArray;
        r28.<init>();
    L_0x058c:
        r0 = r19;
        r4 = r0.id;
        r0 = r28;
        r1 = r19;
        r0.put(r4, r1);
    L_0x0597:
        r10 = r10 + 1;
        goto L_0x052f;
    L_0x059a:
        if (r28 == 0) goto L_0x05f0;
    L_0x059c:
        r10 = 0;
    L_0x059d:
        r0 = r57;
        r4 = r0.updates;
        r4 = r4.size();
        if (r10 >= r4) goto L_0x05f0;
    L_0x05a7:
        r0 = r57;
        r4 = r0.updates;
        r45 = r4.get(r10);
        r45 = (org.telegram.tgnet.TLRPC.Update) r45;
        r0 = r45;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
        if (r4 == 0) goto L_0x06a6;
    L_0x05b7:
        r45 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r45;
        r0 = r45;
        r0 = r0.message;
        r27 = r0;
        r0 = r27;
        r4 = r0.to_id;
        r0 = r4.channel_id;
        r17 = r0;
        r0 = r28;
        r1 = r17;
        r4 = r0.indexOfKey(r1);
        if (r4 < 0) goto L_0x06a6;
    L_0x05d1:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x05ee;
    L_0x05d5:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = "need get diff because of min channel ";
        r4 = r4.append(r6);
        r0 = r17;
        r4 = r4.append(r0);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x05ee:
        r33 = 1;
    L_0x05f0:
        if (r33 != 0) goto L_0x0022;
    L_0x05f2:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.users;
        r0 = r57;
        r7 = r0.chats;
        r8 = 1;
        r9 = 1;
        r4.putUsersAndChats(r6, r7, r8, r9);
        r0 = r57;
        r4 = r0.updates;
        r0 = r56;
        r6 = r0.updatesComparator;
        java.util.Collections.sort(r4, r6);
        r10 = 0;
    L_0x0613:
        r0 = r57;
        r4 = r0.updates;
        r4 = r4.size();
        if (r4 <= 0) goto L_0x0ba4;
    L_0x061d:
        r0 = r57;
        r4 = r0.updates;
        r45 = r4.get(r10);
        r45 = (org.telegram.tgnet.TLRPC.Update) r45;
        r0 = r56;
        r1 = r45;
        r4 = r0.getUpdateType(r1);
        if (r4 != 0) goto L_0x07e0;
    L_0x0631:
        r48 = new org.telegram.tgnet.TLRPC$TL_updates;
        r48.<init>();
        r0 = r48;
        r4 = r0.updates;
        r0 = r45;
        r4.add(r0);
        r4 = getUpdatePts(r45);
        r0 = r48;
        r0.pts = r4;
        r4 = getUpdatePtsCount(r45);
        r0 = r48;
        r0.pts_count = r4;
        r13 = r10 + 1;
    L_0x0651:
        r0 = r57;
        r4 = r0.updates;
        r4 = r4.size();
        if (r13 >= r4) goto L_0x06aa;
    L_0x065b:
        r0 = r57;
        r4 = r0.updates;
        r46 = r4.get(r13);
        r46 = (org.telegram.tgnet.TLRPC.Update) r46;
        r39 = getUpdatePts(r46);
        r21 = getUpdatePtsCount(r46);
        r0 = r56;
        r1 = r46;
        r4 = r0.getUpdateType(r1);
        if (r4 != 0) goto L_0x06aa;
    L_0x0677:
        r0 = r48;
        r4 = r0.pts;
        r4 = r4 + r21;
        r0 = r39;
        if (r4 != r0) goto L_0x06aa;
    L_0x0681:
        r0 = r48;
        r4 = r0.updates;
        r0 = r46;
        r4.add(r0);
        r0 = r39;
        r1 = r48;
        r1.pts = r0;
        r0 = r48;
        r4 = r0.pts_count;
        r4 = r4 + r21;
        r0 = r48;
        r0.pts_count = r4;
        r0 = r57;
        r4 = r0.updates;
        r4.remove(r13);
        r13 = r13 + -1;
        r13 = r13 + 1;
        goto L_0x0651;
    L_0x06a6:
        r10 = r10 + 1;
        goto L_0x059d;
    L_0x06aa:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastPtsValue();
        r0 = r48;
        r6 = r0.pts_count;
        r4 = r4 + r6;
        r0 = r48;
        r6 = r0.pts;
        if (r4 != r6) goto L_0x072b;
    L_0x06c1:
        r0 = r48;
        r4 = r0.updates;
        r0 = r57;
        r6 = r0.users;
        r0 = r57;
        r7 = r0.chats;
        r8 = 0;
        r0 = r56;
        r4 = r0.processUpdateArray(r4, r6, r7, r8);
        if (r4 != 0) goto L_0x071b;
    L_0x06d6:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x070c;
    L_0x06da:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = "need get diff inner TL_updates, pts: ";
        r4 = r4.append(r6);
        r0 = r56;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastPtsValue();
        r4 = r4.append(r6);
        r6 = " ";
        r4 = r4.append(r6);
        r0 = r57;
        r6 = r0.seq;
        r4 = r4.append(r6);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x070c:
        r33 = 1;
    L_0x070e:
        r0 = r57;
        r4 = r0.updates;
        r4.remove(r10);
        r10 = r10 + -1;
        r10 = r10 + 1;
        goto L_0x0613;
    L_0x071b:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r48;
        r6 = r0.pts;
        r4.setLastPtsValue(r6);
        goto L_0x070e;
    L_0x072b:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastPtsValue();
        r0 = r48;
        r6 = r0.pts;
        if (r4 == r6) goto L_0x070e;
    L_0x073d:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0788;
    L_0x0741:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r45;
        r4 = r4.append(r0);
        r6 = " need get diff, pts: ";
        r4 = r4.append(r6);
        r0 = r56;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastPtsValue();
        r4 = r4.append(r6);
        r6 = " ";
        r4 = r4.append(r6);
        r0 = r48;
        r6 = r0.pts;
        r4 = r4.append(r6);
        r6 = " count = ";
        r4 = r4.append(r6);
        r0 = r48;
        r6 = r0.pts_count;
        r4 = r4.append(r6);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x0788:
        r0 = r56;
        r4 = r0.gettingDifference;
        if (r4 != 0) goto L_0x07b5;
    L_0x078e:
        r0 = r56;
        r6 = r0.updatesStartWaitTimePts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x07b5;
    L_0x0798:
        r0 = r56;
        r6 = r0.updatesStartWaitTimePts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x07dc;
    L_0x07a2:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r56;
        r8 = r0.updatesStartWaitTimePts;
        r6 = r6 - r8;
        r6 = java.lang.Math.abs(r6);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 > 0) goto L_0x07dc;
    L_0x07b5:
        r0 = r56;
        r6 = r0.updatesStartWaitTimePts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x07c7;
    L_0x07bf:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r56;
        r0.updatesStartWaitTimePts = r6;
    L_0x07c7:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x07d1;
    L_0x07cb:
        r4 = "add to queue";
        org.telegram.messenger.FileLog.d(r4);
    L_0x07d1:
        r0 = r56;
        r4 = r0.updatesQueuePts;
        r0 = r48;
        r4.add(r0);
        goto L_0x070e;
    L_0x07dc:
        r33 = 1;
        goto L_0x070e;
    L_0x07e0:
        r0 = r56;
        r1 = r45;
        r4 = r0.getUpdateType(r1);
        r6 = 1;
        if (r4 != r6) goto L_0x093f;
    L_0x07eb:
        r48 = new org.telegram.tgnet.TLRPC$TL_updates;
        r48.<init>();
        r0 = r48;
        r4 = r0.updates;
        r0 = r45;
        r4.add(r0);
        r4 = getUpdateQts(r45);
        r0 = r48;
        r0.pts = r4;
        r13 = r10 + 1;
    L_0x0803:
        r0 = r57;
        r4 = r0.updates;
        r4 = r4.size();
        if (r13 >= r4) goto L_0x084b;
    L_0x080d:
        r0 = r57;
        r4 = r0.updates;
        r46 = r4.get(r13);
        r46 = (org.telegram.tgnet.TLRPC.Update) r46;
        r40 = getUpdateQts(r46);
        r0 = r56;
        r1 = r46;
        r4 = r0.getUpdateType(r1);
        r6 = 1;
        if (r4 != r6) goto L_0x084b;
    L_0x0826:
        r0 = r48;
        r4 = r0.pts;
        r4 = r4 + 1;
        r0 = r40;
        if (r4 != r0) goto L_0x084b;
    L_0x0830:
        r0 = r48;
        r4 = r0.updates;
        r0 = r46;
        r4.add(r0);
        r0 = r40;
        r1 = r48;
        r1.pts = r0;
        r0 = r57;
        r4 = r0.updates;
        r4.remove(r13);
        r13 = r13 + -1;
        r13 = r13 + 1;
        goto L_0x0803;
    L_0x084b:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastQtsValue();
        if (r4 == 0) goto L_0x0874;
    L_0x0859:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastQtsValue();
        r0 = r48;
        r6 = r0.updates;
        r6 = r6.size();
        r4 = r4 + r6;
        r0 = r48;
        r6 = r0.pts;
        if (r4 != r6) goto L_0x0899;
    L_0x0874:
        r0 = r48;
        r4 = r0.updates;
        r0 = r57;
        r6 = r0.users;
        r0 = r57;
        r7 = r0.chats;
        r8 = 0;
        r0 = r56;
        r0.processUpdateArray(r4, r6, r7, r8);
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r48;
        r6 = r0.pts;
        r4.setLastQtsValue(r6);
        r34 = 1;
        goto L_0x070e;
    L_0x0899:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastPtsValue();
        r0 = r48;
        r6 = r0.pts;
        if (r4 == r6) goto L_0x070e;
    L_0x08ab:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x08e7;
    L_0x08af:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r45;
        r4 = r4.append(r0);
        r6 = " need get diff, qts: ";
        r4 = r4.append(r6);
        r0 = r56;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastQtsValue();
        r4 = r4.append(r6);
        r6 = " ";
        r4 = r4.append(r6);
        r0 = r48;
        r6 = r0.pts;
        r4 = r4.append(r6);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x08e7:
        r0 = r56;
        r4 = r0.gettingDifference;
        if (r4 != 0) goto L_0x0914;
    L_0x08ed:
        r0 = r56;
        r6 = r0.updatesStartWaitTimeQts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x0914;
    L_0x08f7:
        r0 = r56;
        r6 = r0.updatesStartWaitTimeQts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x093b;
    L_0x0901:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r56;
        r8 = r0.updatesStartWaitTimeQts;
        r6 = r6 - r8;
        r6 = java.lang.Math.abs(r6);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 > 0) goto L_0x093b;
    L_0x0914:
        r0 = r56;
        r6 = r0.updatesStartWaitTimeQts;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0926;
    L_0x091e:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r56;
        r0.updatesStartWaitTimeQts = r6;
    L_0x0926:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0930;
    L_0x092a:
        r4 = "add to queue";
        org.telegram.messenger.FileLog.d(r4);
    L_0x0930:
        r0 = r56;
        r4 = r0.updatesQueueQts;
        r0 = r48;
        r4.add(r0);
        goto L_0x070e;
    L_0x093b:
        r33 = 1;
        goto L_0x070e;
    L_0x093f:
        r0 = r56;
        r1 = r45;
        r4 = r0.getUpdateType(r1);
        r6 = 2;
        if (r4 != r6) goto L_0x0ba4;
    L_0x094a:
        r17 = getUpdateChannelId(r45);
        r43 = 0;
        r0 = r56;
        r4 = r0.channelsPts;
        r0 = r17;
        r18 = r4.get(r0);
        if (r18 != 0) goto L_0x0994;
    L_0x095c:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r17;
        r18 = r4.getChannelPtsSync(r0);
        if (r18 != 0) goto L_0x0a16;
    L_0x096c:
        r14 = 0;
    L_0x096d:
        r0 = r57;
        r4 = r0.chats;
        r4 = r4.size();
        if (r14 >= r4) goto L_0x0994;
    L_0x0977:
        r0 = r57;
        r4 = r0.chats;
        r19 = r4.get(r14);
        r19 = (org.telegram.tgnet.TLRPC.Chat) r19;
        r0 = r19;
        r4 = r0.id;
        r0 = r17;
        if (r4 != r0) goto L_0x0a12;
    L_0x0989:
        r6 = 0;
        r0 = r56;
        r1 = r19;
        r0.loadUnknownChannel(r1, r6);
        r43 = 1;
    L_0x0994:
        r48 = new org.telegram.tgnet.TLRPC$TL_updates;
        r48.<init>();
        r0 = r48;
        r4 = r0.updates;
        r0 = r45;
        r4.add(r0);
        r4 = getUpdatePts(r45);
        r0 = r48;
        r0.pts = r4;
        r4 = getUpdatePtsCount(r45);
        r0 = r48;
        r0.pts_count = r4;
        r13 = r10 + 1;
    L_0x09b4:
        r0 = r57;
        r4 = r0.updates;
        r4 = r4.size();
        if (r13 >= r4) goto L_0x0a23;
    L_0x09be:
        r0 = r57;
        r4 = r0.updates;
        r46 = r4.get(r13);
        r46 = (org.telegram.tgnet.TLRPC.Update) r46;
        r39 = getUpdatePts(r46);
        r21 = getUpdatePtsCount(r46);
        r0 = r56;
        r1 = r46;
        r4 = r0.getUpdateType(r1);
        r6 = 2;
        if (r4 != r6) goto L_0x0a23;
    L_0x09db:
        r4 = getUpdateChannelId(r46);
        r0 = r17;
        if (r0 != r4) goto L_0x0a23;
    L_0x09e3:
        r0 = r48;
        r4 = r0.pts;
        r4 = r4 + r21;
        r0 = r39;
        if (r4 != r0) goto L_0x0a23;
    L_0x09ed:
        r0 = r48;
        r4 = r0.updates;
        r0 = r46;
        r4.add(r0);
        r0 = r39;
        r1 = r48;
        r1.pts = r0;
        r0 = r48;
        r4 = r0.pts_count;
        r4 = r4 + r21;
        r0 = r48;
        r0.pts_count = r4;
        r0 = r57;
        r4 = r0.updates;
        r4.remove(r13);
        r13 = r13 + -1;
        r13 = r13 + 1;
        goto L_0x09b4;
    L_0x0a12:
        r14 = r14 + 1;
        goto L_0x096d;
    L_0x0a16:
        r0 = r56;
        r4 = r0.channelsPts;
        r0 = r17;
        r1 = r18;
        r4.put(r0, r1);
        goto L_0x0994;
    L_0x0a23:
        if (r43 != 0) goto L_0x0b85;
    L_0x0a25:
        r0 = r48;
        r4 = r0.pts_count;
        r4 = r4 + r18;
        r0 = r48;
        r6 = r0.pts;
        if (r4 != r6) goto L_0x0aa3;
    L_0x0a31:
        r0 = r48;
        r4 = r0.updates;
        r0 = r57;
        r6 = r0.users;
        r0 = r57;
        r7 = r0.chats;
        r8 = 0;
        r0 = r56;
        r4 = r0.processUpdateArray(r4, r6, r7, r8);
        if (r4 != 0) goto L_0x0a83;
    L_0x0a46:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0a63;
    L_0x0a4a:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = "need get channel diff inner TL_updates, channel_id = ";
        r4 = r4.append(r6);
        r0 = r17;
        r4 = r4.append(r0);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x0a63:
        if (r32 != 0) goto L_0x0a6c;
    L_0x0a65:
        r32 = new java.util.ArrayList;
        r32.<init>();
        goto L_0x070e;
    L_0x0a6c:
        r4 = java.lang.Integer.valueOf(r17);
        r0 = r32;
        r4 = r0.contains(r4);
        if (r4 != 0) goto L_0x070e;
    L_0x0a78:
        r4 = java.lang.Integer.valueOf(r17);
        r0 = r32;
        r0.add(r4);
        goto L_0x070e;
    L_0x0a83:
        r0 = r56;
        r4 = r0.channelsPts;
        r0 = r48;
        r6 = r0.pts;
        r0 = r17;
        r4.put(r0, r6);
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r48;
        r6 = r0.pts;
        r0 = r17;
        r4.saveChannelPts(r0, r6);
        goto L_0x070e;
    L_0x0aa3:
        r0 = r48;
        r4 = r0.pts;
        r0 = r18;
        if (r0 == r4) goto L_0x070e;
    L_0x0aab:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0af9;
    L_0x0aaf:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r0 = r45;
        r4 = r4.append(r0);
        r6 = " need get channel diff, pts: ";
        r4 = r4.append(r6);
        r0 = r18;
        r4 = r4.append(r0);
        r6 = " ";
        r4 = r4.append(r6);
        r0 = r48;
        r6 = r0.pts;
        r4 = r4.append(r6);
        r6 = " count = ";
        r4 = r4.append(r6);
        r0 = r48;
        r6 = r0.pts_count;
        r4 = r4.append(r6);
        r6 = " channelId = ";
        r4 = r4.append(r6);
        r0 = r17;
        r4 = r4.append(r0);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x0af9:
        r0 = r56;
        r4 = r0.updatesStartWaitTimeChannels;
        r0 = r17;
        r50 = r4.get(r0);
        r0 = r56;
        r4 = r0.gettingDifferenceChannels;
        r0 = r17;
        r25 = r4.get(r0);
        if (r25 != 0) goto L_0x0b25;
    L_0x0b0f:
        r6 = 0;
        r4 = (r50 > r6 ? 1 : (r50 == r6 ? 0 : -1));
        if (r4 == 0) goto L_0x0b25;
    L_0x0b15:
        r6 = java.lang.System.currentTimeMillis();
        r6 = r6 - r50;
        r6 = java.lang.Math.abs(r6);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 > 0) goto L_0x0b65;
    L_0x0b25:
        r6 = 0;
        r4 = (r50 > r6 ? 1 : (r50 == r6 ? 0 : -1));
        if (r4 != 0) goto L_0x0b38;
    L_0x0b2b:
        r0 = r56;
        r4 = r0.updatesStartWaitTimeChannels;
        r6 = java.lang.System.currentTimeMillis();
        r0 = r17;
        r4.put(r0, r6);
    L_0x0b38:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0b42;
    L_0x0b3c:
        r4 = "add to queue";
        org.telegram.messenger.FileLog.d(r4);
    L_0x0b42:
        r0 = r56;
        r4 = r0.updatesQueueChannels;
        r0 = r17;
        r12 = r4.get(r0);
        r12 = (java.util.ArrayList) r12;
        if (r12 != 0) goto L_0x0b5e;
    L_0x0b50:
        r12 = new java.util.ArrayList;
        r12.<init>();
        r0 = r56;
        r4 = r0.updatesQueueChannels;
        r0 = r17;
        r4.put(r0, r12);
    L_0x0b5e:
        r0 = r48;
        r12.add(r0);
        goto L_0x070e;
    L_0x0b65:
        if (r32 != 0) goto L_0x0b6e;
    L_0x0b67:
        r32 = new java.util.ArrayList;
        r32.<init>();
        goto L_0x070e;
    L_0x0b6e:
        r4 = java.lang.Integer.valueOf(r17);
        r0 = r32;
        r4 = r0.contains(r4);
        if (r4 != 0) goto L_0x070e;
    L_0x0b7a:
        r4 = java.lang.Integer.valueOf(r17);
        r0 = r32;
        r0.add(r4);
        goto L_0x070e;
    L_0x0b85:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x070e;
    L_0x0b89:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = "need load unknown channel = ";
        r4 = r4.append(r6);
        r0 = r17;
        r4 = r4.append(r0);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
        goto L_0x070e;
    L_0x0ba4:
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatesCombined;
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0baa:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastSeqValue();
        r4 = r4 + 1;
        r0 = r57;
        r6 = r0.seq_start;
        if (r4 == r6) goto L_0x0bd0;
    L_0x0bbe:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastSeqValue();
        r0 = r57;
        r6 = r0.seq_start;
        if (r4 != r6) goto L_0x0CLASSNAME;
    L_0x0bd0:
        r38 = 1;
    L_0x0bd2:
        if (r38 == 0) goto L_0x0CLASSNAME;
    L_0x0bd4:
        r0 = r57;
        r4 = r0.updates;
        r0 = r57;
        r6 = r0.users;
        r0 = r57;
        r7 = r0.chats;
        r8 = 0;
        r0 = r56;
        r0.processUpdateArray(r4, r6, r7, r8);
        r0 = r57;
        r4 = r0.seq;
        if (r4 == 0) goto L_0x0022;
    L_0x0bec:
        r0 = r57;
        r4 = r0.date;
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0bf2:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.date;
        r4.setLastDateValue(r6);
    L_0x0CLASSNAME:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.seq;
        r4.setLastSeqValue(r6);
        goto L_0x0022;
    L_0x0CLASSNAME:
        r38 = 0;
        goto L_0x0bd2;
    L_0x0CLASSNAME:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastSeqValue();
        r4 = r4 + 1;
        r0 = r57;
        r6 = r0.seq;
        if (r4 == r6) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r0 = r57;
        r4 = r0.seq;
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0c2f:
        r0 = r57;
        r4 = r0.seq;
        r0 = r56;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastSeqValue();
        if (r4 != r6) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r38 = 1;
    L_0x0CLASSNAME:
        goto L_0x0bd2;
    L_0x0CLASSNAME:
        r38 = 0;
        goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0CLASSNAME;
    L_0x0c4b:
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatesCombined;
        if (r4 == 0) goto L_0x0ccd;
    L_0x0CLASSNAME:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = "need get diff TL_updatesCombined, seq: ";
        r4 = r4.append(r6);
        r0 = r56;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastSeqValue();
        r4 = r4.append(r6);
        r6 = " ";
        r4 = r4.append(r6);
        r0 = r57;
        r6 = r0.seq_start;
        r4 = r4.append(r6);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
    L_0x0CLASSNAME:
        r0 = r56;
        r4 = r0.gettingDifference;
        if (r4 != 0) goto L_0x0ca6;
    L_0x0CLASSNAME:
        r0 = r56;
        r6 = r0.updatesStartWaitTimeSeq;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 == 0) goto L_0x0ca6;
    L_0x0CLASSNAME:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r56;
        r8 = r0.updatesStartWaitTimeSeq;
        r6 = r6 - r8;
        r6 = java.lang.Math.abs(r6);
        r8 = 1500; // 0x5dc float:2.102E-42 double:7.41E-321;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 > 0) goto L_0x0d00;
    L_0x0ca6:
        r0 = r56;
        r6 = r0.updatesStartWaitTimeSeq;
        r8 = 0;
        r4 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1));
        if (r4 != 0) goto L_0x0cb8;
    L_0x0cb0:
        r6 = java.lang.System.currentTimeMillis();
        r0 = r56;
        r0.updatesStartWaitTimeSeq = r6;
    L_0x0cb8:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0cc2;
    L_0x0cbc:
        r4 = "add TL_updates/Combined to queue";
        org.telegram.messenger.FileLog.d(r4);
    L_0x0cc2:
        r0 = r56;
        r4 = r0.updatesQueueSeq;
        r0 = r57;
        r4.add(r0);
        goto L_0x0022;
    L_0x0ccd:
        r4 = new java.lang.StringBuilder;
        r4.<init>();
        r6 = "need get diff TL_updates, seq: ";
        r4 = r4.append(r6);
        r0 = r56;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastSeqValue();
        r4 = r4.append(r6);
        r6 = " ";
        r4 = r4.append(r6);
        r0 = r57;
        r6 = r0.seq;
        r4 = r4.append(r6);
        r4 = r4.toString();
        org.telegram.messenger.FileLog.d(r4);
        goto L_0x0CLASSNAME;
    L_0x0d00:
        r33 = 1;
        goto L_0x0022;
    L_0x0d04:
        r0 = r57;
        r4 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatesTooLong;
        if (r4 == 0) goto L_0x0d18;
    L_0x0d0a:
        r4 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r4 == 0) goto L_0x0d14;
    L_0x0d0e:
        r4 = "need get diff TL_updatesTooLong";
        org.telegram.messenger.FileLog.d(r4);
    L_0x0d14:
        r33 = 1;
        goto L_0x0022;
    L_0x0d18:
        r0 = r57;
        r4 = r0 instanceof org.telegram.messenger.MessagesController.UserActionUpdatesSeq;
        if (r4 == 0) goto L_0x0d2f;
    L_0x0d1e:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.seq;
        r4.setLastSeqValue(r6);
        goto L_0x0022;
    L_0x0d2f:
        r0 = r57;
        r4 = r0 instanceof org.telegram.messenger.MessagesController.UserActionUpdatesPts;
        if (r4 == 0) goto L_0x0022;
    L_0x0d35:
        r0 = r57;
        r4 = r0.chat_id;
        if (r4 == 0) goto L_0x0d5f;
    L_0x0d3b:
        r0 = r56;
        r4 = r0.channelsPts;
        r0 = r57;
        r6 = r0.chat_id;
        r0 = r57;
        r7 = r0.pts;
        r4.put(r6, r7);
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.chat_id;
        r0 = r57;
        r7 = r0.pts;
        r4.saveChannelPts(r6, r7);
        goto L_0x0022;
    L_0x0d5f:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r57;
        r6 = r0.pts;
        r4.setLastPtsValue(r6);
        goto L_0x0022;
    L_0x0d70:
        r4 = 0;
        r0 = r56;
        r1 = r26;
        r0.processChannelsUpdatesQueue(r1, r4);
        goto L_0x0057;
    L_0x0d7a:
        if (r33 == 0) goto L_0x0ded;
    L_0x0d7c:
        r56.getDifference();
    L_0x0d7f:
        if (r34 == 0) goto L_0x0da5;
    L_0x0d81:
        r42 = new org.telegram.tgnet.TLRPC$TL_messages_receivedQueue;
        r42.<init>();
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r4 = r4.getLastQtsValue();
        r0 = r42;
        r0.max_qts = r4;
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.tgnet.ConnectionsManager.getInstance(r4);
        r6 = org.telegram.messenger.MessagesController$$Lambda$137.$instance;
        r0 = r42;
        r4.sendRequest(r0, r6);
    L_0x0da5:
        if (r47 == 0) goto L_0x0db1;
    L_0x0da7:
        r4 = new org.telegram.messenger.MessagesController$$Lambda$138;
        r0 = r56;
        r4.<init>(r0);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r4);
    L_0x0db1:
        r0 = r56;
        r4 = r0.currentAccount;
        r4 = org.telegram.messenger.MessagesStorage.getInstance(r4);
        r0 = r56;
        r6 = r0.currentAccount;
        r6 = org.telegram.messenger.MessagesStorage.getInstance(r6);
        r6 = r6.getLastSeqValue();
        r0 = r56;
        r7 = r0.currentAccount;
        r7 = org.telegram.messenger.MessagesStorage.getInstance(r7);
        r7 = r7.getLastPtsValue();
        r0 = r56;
        r8 = r0.currentAccount;
        r8 = org.telegram.messenger.MessagesStorage.getInstance(r8);
        r8 = r8.getLastDateValue();
        r0 = r56;
        r9 = r0.currentAccount;
        r9 = org.telegram.messenger.MessagesStorage.getInstance(r9);
        r9 = r9.getLastQtsValue();
        r4.saveDiffParams(r6, r7, r8, r9);
        return;
    L_0x0ded:
        r10 = 0;
    L_0x0dee:
        r4 = 3;
        if (r10 >= r4) goto L_0x0d7f;
    L_0x0df1:
        r4 = 0;
        r0 = r56;
        r0.processUpdatesQueue(r10, r4);
        r10 = r10 + 1;
        goto L_0x0dee;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdates(org.telegram.tgnet.TLRPC$Updates, boolean):void");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdates$229$MessagesController(boolean printUpdate, int user_id, ArrayList objArr) {
        if (printUpdate) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
        }
        updateInterfaceWithMessages((long) user_id, objArr);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdates$230$MessagesController(boolean printUpdate, Updates updates, ArrayList objArr) {
        if (printUpdate) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
        }
        updateInterfaceWithMessages((long) (-updates.chat_id), objArr);
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$231$MessagesController(ArrayList objArr) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(objArr, true, false, null);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdates$232$MessagesController(ArrayList objArr) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$163(this, objArr));
    }

    static final /* synthetic */ void lambda$processUpdates$233$MessagesController(TLObject response, TL_error error) {
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdates$234$MessagesController() {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
    }

    /* JADX WARNING: Removed duplicated region for block: B:277:0x074a  */
    public boolean processUpdateArray(java.util.ArrayList<org.telegram.tgnet.TLRPC.Update> r113, java.util.ArrayList<org.telegram.tgnet.TLRPC.User> r114, java.util.ArrayList<org.telegram.tgnet.TLRPC.Chat> r115, boolean r116) {
        /*
        r112 = this;
        r5 = r113.isEmpty();
        if (r5 == 0) goto L_0x001a;
    L_0x0006:
        if (r114 != 0) goto L_0x000a;
    L_0x0008:
        if (r115 == 0) goto L_0x0018;
    L_0x000a:
        r5 = new org.telegram.messenger.MessagesController$$Lambda$139;
        r0 = r112;
        r1 = r114;
        r2 = r115;
        r5.<init>(r0, r1, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r5);
    L_0x0018:
        r5 = 1;
    L_0x0019:
        return r5;
    L_0x001a:
        r62 = java.lang.System.currentTimeMillis();
        r91 = 0;
        r88 = 0;
        r111 = 0;
        r93 = 0;
        r89 = 0;
        r68 = 0;
        r50 = 0;
        r83 = 0;
        r84 = 0;
        r82 = 0;
        r79 = 0;
        r65 = 0;
        r57 = 0;
        r53 = 0;
        r106 = 0;
        r99 = 0;
        r59 = 0;
        r55 = 1;
        if (r114 == 0) goto L_0x006f;
    L_0x0044:
        r7 = new java.util.concurrent.ConcurrentHashMap;
        r7.<init>();
        r39 = 0;
        r96 = r114.size();
    L_0x004f:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x0075;
    L_0x0055:
        r0 = r114;
        r1 = r39;
        r107 = r0.get(r1);
        r107 = (org.telegram.tgnet.TLRPC.User) r107;
        r0 = r107;
        r5 = r0.id;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r107;
        r7.put(r5, r0);
        r39 = r39 + 1;
        goto L_0x004f;
    L_0x006f:
        r55 = 0;
        r0 = r112;
        r7 = r0.users;
    L_0x0075:
        if (r115 == 0) goto L_0x00a2;
    L_0x0077:
        r8 = new java.util.concurrent.ConcurrentHashMap;
        r8.<init>();
        r39 = 0;
        r96 = r115.size();
    L_0x0082:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x00a8;
    L_0x0088:
        r0 = r115;
        r1 = r39;
        r51 = r0.get(r1);
        r51 = (org.telegram.tgnet.TLRPC.Chat) r51;
        r0 = r51;
        r5 = r0.id;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r51;
        r8.put(r5, r0);
        r39 = r39 + 1;
        goto L_0x0082;
    L_0x00a2:
        r55 = 0;
        r0 = r112;
        r8 = r0.chats;
    L_0x00a8:
        if (r116 == 0) goto L_0x00ac;
    L_0x00aa:
        r55 = 0;
    L_0x00ac:
        if (r114 != 0) goto L_0x00b0;
    L_0x00ae:
        if (r115 == 0) goto L_0x00be;
    L_0x00b0:
        r5 = new org.telegram.messenger.MessagesController$$Lambda$140;
        r0 = r112;
        r1 = r114;
        r2 = r115;
        r5.<init>(r0, r1, r2);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r5);
    L_0x00be:
        r77 = 0;
        r48 = 0;
        r98 = r113.size();
    L_0x00c6:
        r0 = r48;
        r1 = r98;
        if (r0 >= r1) goto L_0x1403;
    L_0x00cc:
        r0 = r113;
        r1 = r48;
        r47 = r0.get(r1);
        r47 = (org.telegram.tgnet.TLRPC.Update) r47;
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x00f3;
    L_0x00da:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r9 = "process update ";
        r5 = r5.append(r9);
        r0 = r47;
        r5 = r5.append(r0);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x00f3:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNewMessage;
        if (r5 != 0) goto L_0x00ff;
    L_0x00f9:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage;
        if (r5 == 0) goto L_0x0457;
    L_0x00ff:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNewMessage;
        if (r5 == 0) goto L_0x0173;
    L_0x0105:
        r47 = (org.telegram.tgnet.TLRPC.TL_updateNewMessage) r47;
        r0 = r47;
        r6 = r0.message;
    L_0x010b:
        r51 = 0;
        r54 = 0;
        r108 = 0;
        r5 = r6.to_id;
        r5 = r5.channel_id;
        if (r5 == 0) goto L_0x01b7;
    L_0x0117:
        r5 = r6.to_id;
        r0 = r5.channel_id;
        r54 = r0;
    L_0x011d:
        if (r54 == 0) goto L_0x014d;
    L_0x011f:
        r5 = java.lang.Integer.valueOf(r54);
        r51 = r8.get(r5);
        r51 = (org.telegram.tgnet.TLRPC.Chat) r51;
        if (r51 != 0) goto L_0x0135;
    L_0x012b:
        r5 = java.lang.Integer.valueOf(r54);
        r0 = r112;
        r51 = r0.getChat(r5);
    L_0x0135:
        if (r51 != 0) goto L_0x014d;
    L_0x0137:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r54;
        r51 = r5.getChatSync(r0);
        r5 = 1;
        r0 = r112;
        r1 = r51;
        r0.putChat(r1, r5);
    L_0x014d:
        if (r55 == 0) goto L_0x02c8;
    L_0x014f:
        if (r54 == 0) goto L_0x01d3;
    L_0x0151:
        if (r51 != 0) goto L_0x01d3;
    L_0x0153:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0170;
    L_0x0157:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r9 = "not found chat ";
        r5 = r5.append(r9);
        r0 = r54;
        r5 = r5.append(r0);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x0170:
        r5 = 0;
        goto L_0x0019;
    L_0x0173:
        r5 = r47;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateNewChannelMessage) r5;
        r6 = r5.message;
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x019e;
    L_0x017d:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r47;
        r5 = r5.append(r0);
        r9 = " channelId = ";
        r5 = r5.append(r9);
        r9 = r6.to_id;
        r9 = r9.channel_id;
        r5 = r5.append(r9);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x019e:
        r5 = r6.out;
        if (r5 != 0) goto L_0x010b;
    L_0x01a2:
        r5 = r6.from_id;
        r0 = r112;
        r9 = r0.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r9 = r9.getClientUserId();
        if (r5 != r9) goto L_0x010b;
    L_0x01b2:
        r5 = 1;
        r6.out = r5;
        goto L_0x010b;
    L_0x01b7:
        r5 = r6.to_id;
        r5 = r5.chat_id;
        if (r5 == 0) goto L_0x01c5;
    L_0x01bd:
        r5 = r6.to_id;
        r0 = r5.chat_id;
        r54 = r0;
        goto L_0x011d;
    L_0x01c5:
        r5 = r6.to_id;
        r5 = r5.user_id;
        if (r5 == 0) goto L_0x011d;
    L_0x01cb:
        r5 = r6.to_id;
        r0 = r5.user_id;
        r108 = r0;
        goto L_0x011d;
    L_0x01d3:
        r5 = r6.entities;
        r5 = r5.size();
        r60 = r5 + 3;
        r39 = 0;
    L_0x01dd:
        r0 = r39;
        r1 = r60;
        if (r0 >= r1) goto L_0x02c8;
    L_0x01e3:
        r41 = 0;
        if (r39 == 0) goto L_0x01f6;
    L_0x01e7:
        r5 = 1;
        r0 = r39;
        if (r0 != r5) goto L_0x0264;
    L_0x01ec:
        r0 = r6.from_id;
        r108 = r0;
        r5 = r6.post;
        if (r5 == 0) goto L_0x01f6;
    L_0x01f4:
        r41 = 1;
    L_0x01f6:
        if (r108 <= 0) goto L_0x02c4;
    L_0x01f8:
        r5 = java.lang.Integer.valueOf(r108);
        r107 = r7.get(r5);
        r107 = (org.telegram.tgnet.TLRPC.User) r107;
        if (r107 == 0) goto L_0x020c;
    L_0x0204:
        if (r41 != 0) goto L_0x0216;
    L_0x0206:
        r0 = r107;
        r5 = r0.min;
        if (r5 == 0) goto L_0x0216;
    L_0x020c:
        r5 = java.lang.Integer.valueOf(r108);
        r0 = r112;
        r107 = r0.getUser(r5);
    L_0x0216:
        if (r107 == 0) goto L_0x0220;
    L_0x0218:
        if (r41 != 0) goto L_0x0242;
    L_0x021a:
        r0 = r107;
        r5 = r0.min;
        if (r5 == 0) goto L_0x0242;
    L_0x0220:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r108;
        r107 = r5.getUserSync(r0);
        if (r107 == 0) goto L_0x023a;
    L_0x0230:
        if (r41 != 0) goto L_0x023a;
    L_0x0232:
        r0 = r107;
        r5 = r0.min;
        if (r5 == 0) goto L_0x023a;
    L_0x0238:
        r107 = 0;
    L_0x023a:
        r5 = 1;
        r0 = r112;
        r1 = r107;
        r0.putUser(r1, r5);
    L_0x0242:
        if (r107 != 0) goto L_0x0294;
    L_0x0244:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0261;
    L_0x0248:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r9 = "not found user ";
        r5 = r5.append(r9);
        r0 = r108;
        r5 = r5.append(r0);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x0261:
        r5 = 0;
        goto L_0x0019;
    L_0x0264:
        r5 = 2;
        r0 = r39;
        if (r0 != r5) goto L_0x0277;
    L_0x0269:
        r5 = r6.fwd_from;
        if (r5 == 0) goto L_0x0274;
    L_0x026d:
        r5 = r6.fwd_from;
        r0 = r5.from_id;
        r108 = r0;
    L_0x0273:
        goto L_0x01f6;
    L_0x0274:
        r108 = 0;
        goto L_0x0273;
    L_0x0277:
        r5 = r6.entities;
        r9 = r39 + -3;
        r70 = r5.get(r9);
        r70 = (org.telegram.tgnet.TLRPC.MessageEntity) r70;
        r0 = r70;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r5 == 0) goto L_0x0291;
    L_0x0287:
        r70 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r70;
        r0 = r70;
        r0 = r0.user_id;
        r108 = r0;
    L_0x028f:
        goto L_0x01f6;
    L_0x0291:
        r108 = 0;
        goto L_0x028f;
    L_0x0294:
        r5 = 1;
        r0 = r39;
        if (r0 != r5) goto L_0x02c4;
    L_0x0299:
        r0 = r107;
        r5 = r0.status;
        if (r5 == 0) goto L_0x02c4;
    L_0x029f:
        r0 = r107;
        r5 = r0.status;
        r5 = r5.expires;
        if (r5 > 0) goto L_0x02c4;
    L_0x02a7:
        r0 = r112;
        r5 = r0.onlinePrivacy;
        r9 = java.lang.Integer.valueOf(r108);
        r0 = r112;
        r10 = r0.currentAccount;
        r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r10);
        r10 = r10.getCurrentTime();
        r10 = java.lang.Integer.valueOf(r10);
        r5.put(r9, r10);
        r77 = r77 | 4;
    L_0x02c4:
        r39 = r39 + 1;
        goto L_0x01dd;
    L_0x02c8:
        if (r51 == 0) goto L_0x02d7;
    L_0x02ca:
        r0 = r51;
        r5 = r0.megagroup;
        if (r5 == 0) goto L_0x02d7;
    L_0x02d0:
        r5 = r6.flags;
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r5 = r5 | r9;
        r6.flags = r5;
    L_0x02d7:
        r5 = r6.action;
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
        if (r5 == 0) goto L_0x0300;
    L_0x02dd:
        r5 = r6.action;
        r5 = r5.user_id;
        r5 = java.lang.Integer.valueOf(r5);
        r107 = r7.get(r5);
        r107 = (org.telegram.tgnet.TLRPC.User) r107;
        if (r107 == 0) goto L_0x03f8;
    L_0x02ed:
        r0 = r107;
        r5 = r0.bot;
        if (r5 == 0) goto L_0x03f8;
    L_0x02f3:
        r5 = new org.telegram.tgnet.TLRPC$TL_replyKeyboardHide;
        r5.<init>();
        r6.reply_markup = r5;
        r5 = r6.flags;
        r5 = r5 | 64;
        r6.flags = r5;
    L_0x0300:
        if (r89 != 0) goto L_0x0307;
    L_0x0302:
        r89 = new java.util.ArrayList;
        r89.<init>();
    L_0x0307:
        r0 = r89;
        r0.add(r6);
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r6);
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r58 = r5.getClientUserId();
        r5 = r6.to_id;
        r5 = r5.chat_id;
        if (r5 == 0) goto L_0x041b;
    L_0x0321:
        r5 = r6.to_id;
        r5 = r5.chat_id;
        r5 = -r5;
        r14 = (long) r5;
        r6.dialog_id = r14;
    L_0x0329:
        r5 = r6.out;
        if (r5 == 0) goto L_0x0442;
    L_0x032d:
        r0 = r112;
        r0 = r0.dialogs_read_outbox_max;
        r95 = r0;
    L_0x0333:
        r14 = r6.dialog_id;
        r5 = java.lang.Long.valueOf(r14);
        r0 = r95;
        r109 = r0.get(r5);
        r109 = (java.lang.Integer) r109;
        if (r109 != 0) goto L_0x0364;
    L_0x0343:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = r6.out;
        r14 = r6.dialog_id;
        r5 = r5.getDialogReadMax(r9, r14);
        r109 = java.lang.Integer.valueOf(r5);
        r14 = r6.dialog_id;
        r5 = java.lang.Long.valueOf(r14);
        r0 = r95;
        r1 = r109;
        r0.put(r5, r1);
    L_0x0364:
        r5 = r109.intValue();
        r9 = r6.id;
        if (r5 >= r9) goto L_0x044a;
    L_0x036c:
        if (r51 == 0) goto L_0x0374;
    L_0x036e:
        r5 = org.telegram.messenger.ChatObject.isNotInChat(r51);
        if (r5 != 0) goto L_0x044a;
    L_0x0374:
        r5 = r6.action;
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
        if (r5 != 0) goto L_0x044a;
    L_0x037a:
        r5 = r6.action;
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_messageActionChannelCreate;
        if (r5 != 0) goto L_0x044a;
    L_0x0380:
        r5 = 1;
    L_0x0381:
        r6.unread = r5;
        r14 = r6.dialog_id;
        r0 = r58;
        r0 = (long) r0;
        r34 = r0;
        r5 = (r14 > r34 ? 1 : (r14 == r34 ? 0 : -1));
        if (r5 != 0) goto L_0x0397;
    L_0x038e:
        r5 = 0;
        r6.unread = r5;
        r5 = 0;
        r6.media_unread = r5;
        r5 = 1;
        r6.out = r5;
    L_0x0397:
        r4 = new org.telegram.messenger.MessageObject;
        r0 = r112;
        r5 = r0.currentAccount;
        r0 = r112;
        r9 = r0.createdDialogIds;
        r14 = r6.dialog_id;
        r10 = java.lang.Long.valueOf(r14);
        r9 = r9.contains(r10);
        r4.<init>(r5, r6, r7, r8, r9);
        r5 = r4.type;
        r9 = 11;
        if (r5 != r9) goto L_0x044d;
    L_0x03b4:
        r77 = r77 | 8;
    L_0x03b6:
        if (r88 != 0) goto L_0x03bd;
    L_0x03b8:
        r88 = new android.util.LongSparseArray;
        r88.<init>();
    L_0x03bd:
        r14 = r6.dialog_id;
        r0 = r88;
        r42 = r0.get(r14);
        r42 = (java.util.ArrayList) r42;
        if (r42 != 0) goto L_0x03d7;
    L_0x03c9:
        r42 = new java.util.ArrayList;
        r42.<init>();
        r14 = r6.dialog_id;
        r0 = r88;
        r1 = r42;
        r0.put(r14, r1);
    L_0x03d7:
        r0 = r42;
        r0.add(r4);
        r5 = r4.isOut();
        if (r5 != 0) goto L_0x03f4;
    L_0x03e2:
        r5 = r4.isUnread();
        if (r5 == 0) goto L_0x03f4;
    L_0x03e8:
        if (r93 != 0) goto L_0x03ef;
    L_0x03ea:
        r93 = new java.util.ArrayList;
        r93.<init>();
    L_0x03ef:
        r0 = r93;
        r0.add(r4);
    L_0x03f4:
        r48 = r48 + 1;
        goto L_0x00c6;
    L_0x03f8:
        r5 = r6.from_id;
        r0 = r112;
        r9 = r0.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r9 = r9.getClientUserId();
        if (r5 != r9) goto L_0x0300;
    L_0x0408:
        r5 = r6.action;
        r5 = r5.user_id;
        r0 = r112;
        r9 = r0.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r9 = r9.getClientUserId();
        if (r5 != r9) goto L_0x0300;
    L_0x041a:
        goto L_0x03f4;
    L_0x041b:
        r5 = r6.to_id;
        r5 = r5.channel_id;
        if (r5 == 0) goto L_0x042b;
    L_0x0421:
        r5 = r6.to_id;
        r5 = r5.channel_id;
        r5 = -r5;
        r14 = (long) r5;
        r6.dialog_id = r14;
        goto L_0x0329;
    L_0x042b:
        r5 = r6.to_id;
        r5 = r5.user_id;
        r0 = r58;
        if (r5 != r0) goto L_0x0439;
    L_0x0433:
        r5 = r6.to_id;
        r9 = r6.from_id;
        r5.user_id = r9;
    L_0x0439:
        r5 = r6.to_id;
        r5 = r5.user_id;
        r14 = (long) r5;
        r6.dialog_id = r14;
        goto L_0x0329;
    L_0x0442:
        r0 = r112;
        r0 = r0.dialogs_read_inbox_max;
        r95 = r0;
        goto L_0x0333;
    L_0x044a:
        r5 = 0;
        goto L_0x0381;
    L_0x044d:
        r5 = r4.type;
        r9 = 10;
        if (r5 != r9) goto L_0x03b6;
    L_0x0453:
        r77 = r77 | 16;
        goto L_0x03b6;
    L_0x0457:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents;
        if (r5 == 0) goto L_0x0497;
    L_0x045d:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateReadMessagesContents) r103;
        if (r82 != 0) goto L_0x0468;
    L_0x0463:
        r82 = new java.util.ArrayList;
        r82.<init>();
    L_0x0468:
        r39 = 0;
        r0 = r103;
        r5 = r0.messages;
        r96 = r5.size();
    L_0x0472:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x03f4;
    L_0x0478:
        r0 = r103;
        r5 = r0.messages;
        r0 = r39;
        r5 = r5.get(r0);
        r5 = (java.lang.Integer) r5;
        r5 = r5.intValue();
        r0 = (long) r5;
        r74 = r0;
        r5 = java.lang.Long.valueOf(r74);
        r0 = r82;
        r0.add(r5);
        r39 = r39 + 1;
        goto L_0x0472;
    L_0x0497:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelReadMessagesContents;
        if (r5 == 0) goto L_0x04e1;
    L_0x049d:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChannelReadMessagesContents) r103;
        if (r82 != 0) goto L_0x04a8;
    L_0x04a3:
        r82 = new java.util.ArrayList;
        r82.<init>();
    L_0x04a8:
        r39 = 0;
        r0 = r103;
        r5 = r0.messages;
        r96 = r5.size();
    L_0x04b2:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x03f4;
    L_0x04b8:
        r0 = r103;
        r5 = r0.messages;
        r0 = r39;
        r5 = r5.get(r0);
        r5 = (java.lang.Integer) r5;
        r5 = r5.intValue();
        r0 = (long) r5;
        r74 = r0;
        r0 = r103;
        r5 = r0.channel_id;
        r14 = (long) r5;
        r5 = 32;
        r14 = r14 << r5;
        r74 = r74 | r14;
        r5 = java.lang.Long.valueOf(r74);
        r0 = r82;
        r0.add(r5);
        r39 = r39 + 1;
        goto L_0x04b2;
    L_0x04e1:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox;
        if (r5 == 0) goto L_0x056f;
    L_0x04e7:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateReadHistoryInbox) r103;
        if (r83 != 0) goto L_0x04f2;
    L_0x04ed:
        r83 = new org.telegram.messenger.support.SparseLongArray;
        r83.<init>();
    L_0x04f2:
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.chat_id;
        if (r5 == 0) goto L_0x0555;
    L_0x04fa:
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.chat_id;
        r5 = -r5;
        r0 = r103;
        r9 = r0.max_id;
        r14 = (long) r9;
        r0 = r83;
        r0.put(r5, r14);
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.chat_id;
        r5 = -r5;
        r0 = (long) r5;
        r66 = r0;
    L_0x0515:
        r0 = r112;
        r5 = r0.dialogs_read_inbox_max;
        r9 = java.lang.Long.valueOf(r66);
        r109 = r5.get(r9);
        r109 = (java.lang.Integer) r109;
        if (r109 != 0) goto L_0x0538;
    L_0x0525:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = 0;
        r0 = r66;
        r5 = r5.getDialogReadMax(r9, r0);
        r109 = java.lang.Integer.valueOf(r5);
    L_0x0538:
        r0 = r112;
        r5 = r0.dialogs_read_inbox_max;
        r9 = java.lang.Long.valueOf(r66);
        r10 = r109.intValue();
        r0 = r103;
        r12 = r0.max_id;
        r10 = java.lang.Math.max(r10, r12);
        r10 = java.lang.Integer.valueOf(r10);
        r5.put(r9, r10);
        goto L_0x03f4;
    L_0x0555:
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.user_id;
        r0 = r103;
        r9 = r0.max_id;
        r14 = (long) r9;
        r0 = r83;
        r0.put(r5, r14);
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.user_id;
        r0 = (long) r5;
        r66 = r0;
        goto L_0x0515;
    L_0x056f:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox;
        if (r5 == 0) goto L_0x05fd;
    L_0x0575:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateReadHistoryOutbox) r103;
        if (r84 != 0) goto L_0x0580;
    L_0x057b:
        r84 = new org.telegram.messenger.support.SparseLongArray;
        r84.<init>();
    L_0x0580:
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.chat_id;
        if (r5 == 0) goto L_0x05e3;
    L_0x0588:
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.chat_id;
        r5 = -r5;
        r0 = r103;
        r9 = r0.max_id;
        r14 = (long) r9;
        r0 = r84;
        r0.put(r5, r14);
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.chat_id;
        r5 = -r5;
        r0 = (long) r5;
        r66 = r0;
    L_0x05a3:
        r0 = r112;
        r5 = r0.dialogs_read_outbox_max;
        r9 = java.lang.Long.valueOf(r66);
        r109 = r5.get(r9);
        r109 = (java.lang.Integer) r109;
        if (r109 != 0) goto L_0x05c6;
    L_0x05b3:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = 1;
        r0 = r66;
        r5 = r5.getDialogReadMax(r9, r0);
        r109 = java.lang.Integer.valueOf(r5);
    L_0x05c6:
        r0 = r112;
        r5 = r0.dialogs_read_outbox_max;
        r9 = java.lang.Long.valueOf(r66);
        r10 = r109.intValue();
        r0 = r103;
        r12 = r0.max_id;
        r10 = java.lang.Math.max(r10, r12);
        r10 = java.lang.Integer.valueOf(r10);
        r5.put(r9, r10);
        goto L_0x03f4;
    L_0x05e3:
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.user_id;
        r0 = r103;
        r9 = r0.max_id;
        r14 = (long) r9;
        r0 = r84;
        r0.put(r5, r14);
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.user_id;
        r0 = (long) r5;
        r66 = r0;
        goto L_0x05a3;
    L_0x05fd:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDeleteMessages;
        if (r5 == 0) goto L_0x0631;
    L_0x0603:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateDeleteMessages) r103;
        if (r65 != 0) goto L_0x060e;
    L_0x0609:
        r65 = new android.util.SparseArray;
        r65.<init>();
    L_0x060e:
        r5 = 0;
        r0 = r65;
        r45 = r0.get(r5);
        r45 = (java.util.ArrayList) r45;
        if (r45 != 0) goto L_0x0626;
    L_0x0619:
        r45 = new java.util.ArrayList;
        r45.<init>();
        r5 = 0;
        r0 = r65;
        r1 = r45;
        r0.put(r5, r1);
    L_0x0626:
        r0 = r103;
        r5 = r0.messages;
        r0 = r45;
        r0.addAll(r5);
        goto L_0x03f4;
    L_0x0631:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserTyping;
        if (r5 != 0) goto L_0x063d;
    L_0x0637:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChatUserTyping;
        if (r5 == 0) goto L_0x076c;
    L_0x063d:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserTyping;
        if (r5 == 0) goto L_0x06e1;
    L_0x0643:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateUserTyping) r103;
        r0 = r103;
        r0 = r0.user_id;
        r108 = r0;
        r0 = r103;
        r0 = r0.action;
        r40 = r0;
        r54 = 0;
    L_0x0655:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r5 = r5.getClientUserId();
        r0 = r108;
        if (r0 == r5) goto L_0x03f4;
    L_0x0665:
        r0 = r54;
        r5 = -r0;
        r0 = (long) r5;
        r104 = r0;
        r14 = 0;
        r5 = (r104 > r14 ? 1 : (r104 == r14 ? 0 : -1));
        if (r5 != 0) goto L_0x0676;
    L_0x0671:
        r0 = r108;
        r0 = (long) r0;
        r104 = r0;
    L_0x0676:
        r0 = r112;
        r5 = r0.printingUsers;
        r9 = java.lang.Long.valueOf(r104);
        r43 = r5.get(r9);
        r43 = (java.util.ArrayList) r43;
        r0 = r40;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_sendMessageCancelAction;
        if (r5 == 0) goto L_0x06fc;
    L_0x068a:
        if (r43 == 0) goto L_0x06c4;
    L_0x068c:
        r39 = 0;
        r96 = r43.size();
    L_0x0692:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x06b3;
    L_0x0698:
        r0 = r43;
        r1 = r39;
        r92 = r0.get(r1);
        r92 = (org.telegram.messenger.MessagesController.PrintingUser) r92;
        r0 = r92;
        r5 = r0.userId;
        r0 = r108;
        if (r5 != r0) goto L_0x06f9;
    L_0x06aa:
        r0 = r43;
        r1 = r39;
        r0.remove(r1);
        r91 = 1;
    L_0x06b3:
        r5 = r43.isEmpty();
        if (r5 == 0) goto L_0x06c4;
    L_0x06b9:
        r0 = r112;
        r5 = r0.printingUsers;
        r9 = java.lang.Long.valueOf(r104);
        r5.remove(r9);
    L_0x06c4:
        r0 = r112;
        r5 = r0.onlinePrivacy;
        r9 = java.lang.Integer.valueOf(r108);
        r0 = r112;
        r10 = r0.currentAccount;
        r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r10);
        r10 = r10.getCurrentTime();
        r10 = java.lang.Integer.valueOf(r10);
        r5.put(r9, r10);
        goto L_0x03f4;
    L_0x06e1:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChatUserTyping) r103;
        r0 = r103;
        r0 = r0.chat_id;
        r54 = r0;
        r0 = r103;
        r0 = r0.user_id;
        r108 = r0;
        r0 = r103;
        r0 = r0.action;
        r40 = r0;
        goto L_0x0655;
    L_0x06f9:
        r39 = r39 + 1;
        goto L_0x0692;
    L_0x06fc:
        if (r43 != 0) goto L_0x0710;
    L_0x06fe:
        r43 = new java.util.ArrayList;
        r43.<init>();
        r0 = r112;
        r5 = r0.printingUsers;
        r9 = java.lang.Long.valueOf(r104);
        r0 = r43;
        r5.put(r9, r0);
    L_0x0710:
        r71 = 0;
        r5 = r43.iterator();
    L_0x0716:
        r9 = r5.hasNext();
        if (r9 == 0) goto L_0x0748;
    L_0x071c:
        r102 = r5.next();
        r102 = (org.telegram.messenger.MessagesController.PrintingUser) r102;
        r0 = r102;
        r9 = r0.userId;
        r0 = r108;
        if (r9 != r0) goto L_0x0716;
    L_0x072a:
        r71 = 1;
        r0 = r62;
        r2 = r102;
        r2.lastTime = r0;
        r0 = r102;
        r5 = r0.action;
        r5 = r5.getClass();
        r9 = r40.getClass();
        if (r5 == r9) goto L_0x0742;
    L_0x0740:
        r91 = 1;
    L_0x0742:
        r0 = r40;
        r1 = r102;
        r1.action = r0;
    L_0x0748:
        if (r71 != 0) goto L_0x06c4;
    L_0x074a:
        r90 = new org.telegram.messenger.MessagesController$PrintingUser;
        r90.<init>();
        r0 = r108;
        r1 = r90;
        r1.userId = r0;
        r0 = r62;
        r2 = r90;
        r2.lastTime = r0;
        r0 = r40;
        r1 = r90;
        r1.action = r0;
        r0 = r43;
        r1 = r90;
        r0.add(r1);
        r91 = 1;
        goto L_0x06c4;
    L_0x076c:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipants;
        if (r5 == 0) goto L_0x078a;
    L_0x0772:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipants) r103;
        r77 = r77 | 32;
        if (r53 != 0) goto L_0x077f;
    L_0x077a:
        r53 = new java.util.ArrayList;
        r53.<init>();
    L_0x077f:
        r0 = r103;
        r5 = r0.participants;
        r0 = r53;
        r0.add(r5);
        goto L_0x03f4;
    L_0x078a:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserStatus;
        if (r5 == 0) goto L_0x07a2;
    L_0x0790:
        r77 = r77 | 4;
        if (r106 != 0) goto L_0x0799;
    L_0x0794:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x0799:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x07a2:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserName;
        if (r5 == 0) goto L_0x07ba;
    L_0x07a8:
        r77 = r77 | 1;
        if (r106 != 0) goto L_0x07b1;
    L_0x07ac:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x07b1:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x07ba:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhoto;
        if (r5 == 0) goto L_0x07e5;
    L_0x07c0:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateUserPhoto) r103;
        r77 = r77 | 2;
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r9 = r0.user_id;
        r5.clearUserPhotos(r9);
        if (r106 != 0) goto L_0x07dc;
    L_0x07d7:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x07dc:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x07e5:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPhone;
        if (r5 == 0) goto L_0x0801;
    L_0x07eb:
        r0 = r77;
        r0 = r0 | 1024;
        r77 = r0;
        if (r106 != 0) goto L_0x07f8;
    L_0x07f3:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x07f8:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x0801:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateContactLink;
        if (r5 == 0) goto L_0x088e;
    L_0x0807:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateContactLink) r103;
        if (r59 != 0) goto L_0x0812;
    L_0x080d:
        r59 = new java.util.ArrayList;
        r59.<init>();
    L_0x0812:
        r0 = r103;
        r5 = r0.my_link;
        r5 = r5 instanceof org.telegram.tgnet.TLRPC.TL_contactLinkContact;
        if (r5 == 0) goto L_0x0854;
    L_0x081a:
        r0 = r103;
        r5 = r0.user_id;
        r5 = -r5;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r59;
        r76 = r0.indexOf(r5);
        r5 = -1;
        r0 = r76;
        if (r0 == r5) goto L_0x0835;
    L_0x082e:
        r0 = r59;
        r1 = r76;
        r0.remove(r1);
    L_0x0835:
        r0 = r103;
        r5 = r0.user_id;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r59;
        r5 = r0.contains(r5);
        if (r5 != 0) goto L_0x03f4;
    L_0x0845:
        r0 = r103;
        r5 = r0.user_id;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r59;
        r0.add(r5);
        goto L_0x03f4;
    L_0x0854:
        r0 = r103;
        r5 = r0.user_id;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r59;
        r76 = r0.indexOf(r5);
        r5 = -1;
        r0 = r76;
        if (r0 == r5) goto L_0x086e;
    L_0x0867:
        r0 = r59;
        r1 = r76;
        r0.remove(r1);
    L_0x086e:
        r0 = r103;
        r5 = r0.user_id;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r59;
        r5 = r0.contains(r5);
        if (r5 != 0) goto L_0x03f4;
    L_0x087e:
        r0 = r103;
        r5 = r0.user_id;
        r5 = -r5;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r59;
        r0.add(r5);
        goto L_0x03f4;
    L_0x088e:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage;
        if (r5 == 0) goto L_0x092e;
    L_0x0894:
        r0 = r112;
        r5 = r0.currentAccount;
        r9 = org.telegram.messenger.SecretChatHelper.getInstance(r5);
        r5 = r47;
        r5 = (org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage) r5;
        r5 = r5.message;
        r64 = r9.decryptMessage(r5);
        if (r64 == 0) goto L_0x03f4;
    L_0x08a8:
        r5 = r64.isEmpty();
        if (r5 != 0) goto L_0x03f4;
    L_0x08ae:
        r47 = (org.telegram.tgnet.TLRPC.TL_updateNewEncryptedMessage) r47;
        r0 = r47;
        r5 = r0.message;
        r0 = r5.chat_id;
        r56 = r0;
        r0 = r56;
        r14 = (long) r0;
        r5 = 32;
        r104 = r14 << r5;
        if (r88 != 0) goto L_0x08c6;
    L_0x08c1:
        r88 = new android.util.LongSparseArray;
        r88.<init>();
    L_0x08c6:
        r0 = r88;
        r1 = r104;
        r42 = r0.get(r1);
        r42 = (java.util.ArrayList) r42;
        if (r42 != 0) goto L_0x08e0;
    L_0x08d2:
        r42 = new java.util.ArrayList;
        r42.<init>();
        r0 = r88;
        r1 = r104;
        r3 = r42;
        r0.put(r1, r3);
    L_0x08e0:
        r39 = 0;
        r96 = r64.size();
    L_0x08e6:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x03f4;
    L_0x08ec:
        r0 = r64;
        r1 = r39;
        r6 = r0.get(r1);
        r6 = (org.telegram.tgnet.TLRPC.Message) r6;
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r6);
        if (r89 != 0) goto L_0x0900;
    L_0x08fb:
        r89 = new java.util.ArrayList;
        r89.<init>();
    L_0x0900:
        r0 = r89;
        r0.add(r6);
        r4 = new org.telegram.messenger.MessageObject;
        r0 = r112;
        r5 = r0.currentAccount;
        r0 = r112;
        r9 = r0.createdDialogIds;
        r10 = java.lang.Long.valueOf(r104);
        r9 = r9.contains(r10);
        r4.<init>(r5, r6, r7, r8, r9);
        r0 = r42;
        r0.add(r4);
        if (r93 != 0) goto L_0x0926;
    L_0x0921:
        r93 = new java.util.ArrayList;
        r93.<init>();
    L_0x0926:
        r0 = r93;
        r0.add(r4);
        r39 = r39 + 1;
        goto L_0x08e6;
    L_0x092e:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping;
        if (r5 == 0) goto L_0x09ee;
    L_0x0934:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedChatTyping) r103;
        r0 = r103;
        r5 = r0.chat_id;
        r9 = 1;
        r0 = r112;
        r69 = r0.getEncryptedChatDB(r5, r9);
        if (r69 == 0) goto L_0x03f4;
    L_0x0945:
        r0 = r103;
        r5 = r0.chat_id;
        r14 = (long) r5;
        r5 = 32;
        r104 = r14 << r5;
        r0 = r112;
        r5 = r0.printingUsers;
        r9 = java.lang.Long.valueOf(r104);
        r43 = r5.get(r9);
        r43 = (java.util.ArrayList) r43;
        if (r43 != 0) goto L_0x0970;
    L_0x095e:
        r43 = new java.util.ArrayList;
        r43.<init>();
        r0 = r112;
        r5 = r0.printingUsers;
        r9 = java.lang.Long.valueOf(r104);
        r0 = r43;
        r5.put(r9, r0);
    L_0x0970:
        r71 = 0;
        r39 = 0;
        r96 = r43.size();
    L_0x0978:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x09a3;
    L_0x097e:
        r0 = r43;
        r1 = r39;
        r102 = r0.get(r1);
        r102 = (org.telegram.messenger.MessagesController.PrintingUser) r102;
        r0 = r102;
        r5 = r0.userId;
        r0 = r69;
        r9 = r0.user_id;
        if (r5 != r9) goto L_0x09eb;
    L_0x0992:
        r71 = 1;
        r0 = r62;
        r2 = r102;
        r2.lastTime = r0;
        r5 = new org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction;
        r5.<init>();
        r0 = r102;
        r0.action = r5;
    L_0x09a3:
        if (r71 != 0) goto L_0x09ca;
    L_0x09a5:
        r90 = new org.telegram.messenger.MessagesController$PrintingUser;
        r90.<init>();
        r0 = r69;
        r5 = r0.user_id;
        r0 = r90;
        r0.userId = r5;
        r0 = r62;
        r2 = r90;
        r2.lastTime = r0;
        r5 = new org.telegram.tgnet.TLRPC$TL_sendMessageTypingAction;
        r5.<init>();
        r0 = r90;
        r0.action = r5;
        r0 = r43;
        r1 = r90;
        r0.add(r1);
        r91 = 1;
    L_0x09ca:
        r0 = r112;
        r5 = r0.onlinePrivacy;
        r0 = r69;
        r9 = r0.user_id;
        r9 = java.lang.Integer.valueOf(r9);
        r0 = r112;
        r10 = r0.currentAccount;
        r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r10);
        r10 = r10.getCurrentTime();
        r10 = java.lang.Integer.valueOf(r10);
        r5.put(r9, r10);
        goto L_0x03f4;
    L_0x09eb:
        r39 = r39 + 1;
        goto L_0x0978;
    L_0x09ee:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead;
        if (r5 == 0) goto L_0x0a1e;
    L_0x09f4:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead) r103;
        if (r79 != 0) goto L_0x09ff;
    L_0x09fa:
        r79 = new android.util.SparseIntArray;
        r79.<init>();
    L_0x09ff:
        r0 = r103;
        r5 = r0.chat_id;
        r0 = r103;
        r9 = r0.max_date;
        r0 = r79;
        r0.put(r5, r9);
        if (r99 != 0) goto L_0x0a13;
    L_0x0a0e:
        r99 = new java.util.ArrayList;
        r99.<init>();
    L_0x0a13:
        r47 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead) r47;
        r0 = r99;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x0a1e:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd;
        if (r5 == 0) goto L_0x0a46;
    L_0x0a24:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdd) r103;
        r0 = r112;
        r5 = r0.currentAccount;
        r9 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r10 = r0.chat_id;
        r0 = r103;
        r11 = r0.user_id;
        r12 = 0;
        r0 = r103;
        r13 = r0.inviter_id;
        r0 = r103;
        r14 = r0.version;
        r9.updateChatInfo(r10, r11, r12, r13, r14);
        goto L_0x03f4;
    L_0x0a46:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete;
        if (r5 == 0) goto L_0x0a6b;
    L_0x0a4c:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantDelete) r103;
        r0 = r112;
        r5 = r0.currentAccount;
        r9 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r10 = r0.chat_id;
        r0 = r103;
        r11 = r0.user_id;
        r12 = 1;
        r13 = 0;
        r0 = r103;
        r14 = r0.version;
        r9.updateChatInfo(r10, r11, r12, r13, r14);
        goto L_0x03f4;
    L_0x0a6b:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDcOptions;
        if (r5 != 0) goto L_0x0a77;
    L_0x0a71:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateConfig;
        if (r5 == 0) goto L_0x0a84;
    L_0x0a77:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.tgnet.ConnectionsManager.getInstance(r5);
        r5.updateDcSettings();
        goto L_0x03f4;
    L_0x0a84:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateEncryption;
        if (r5 == 0) goto L_0x0a9b;
    L_0x0a8a:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.SecretChatHelper.getInstance(r5);
        r47 = (org.telegram.tgnet.TLRPC.TL_updateEncryption) r47;
        r0 = r47;
        r5.processUpdateEncryption(r0, r7);
        goto L_0x03f4;
    L_0x0a9b:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserBlocked;
        if (r5 == 0) goto L_0x0af2;
    L_0x0aa1:
        r72 = r47;
        r72 = (org.telegram.tgnet.TLRPC.TL_updateUserBlocked) r72;
        r0 = r72;
        r5 = r0.blocked;
        if (r5 == 0) goto L_0x0ae2;
    L_0x0aab:
        r73 = new android.util.SparseIntArray;
        r73.<init>();
        r0 = r72;
        r5 = r0.user_id;
        r9 = 1;
        r0 = r73;
        r0.put(r5, r9);
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = 0;
        r0 = r73;
        r5.putBlockedUsers(r0, r9);
    L_0x0ac8:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r5.getStorageQueue();
        r9 = new org.telegram.messenger.MessagesController$$Lambda$141;
        r0 = r112;
        r1 = r72;
        r9.<init>(r0, r1);
        r5.postRunnable(r9);
        goto L_0x03f4;
    L_0x0ae2:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r72;
        r9 = r0.user_id;
        r5.deleteBlockedUser(r9);
        goto L_0x0ac8;
    L_0x0af2:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNotifySettings;
        if (r5 == 0) goto L_0x0b08;
    L_0x0af8:
        if (r106 != 0) goto L_0x0aff;
    L_0x0afa:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x0aff:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x0b08:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateServiceNotification;
        if (r5 == 0) goto L_0x0c1c;
    L_0x0b0e:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateServiceNotification) r103;
        r0 = r103;
        r5 = r0.popup;
        if (r5 == 0) goto L_0x0b34;
    L_0x0b18:
        r0 = r103;
        r5 = r0.message;
        if (r5 == 0) goto L_0x0b34;
    L_0x0b1e:
        r0 = r103;
        r5 = r0.message;
        r5 = r5.length();
        if (r5 <= 0) goto L_0x0b34;
    L_0x0b28:
        r5 = new org.telegram.messenger.MessagesController$$Lambda$142;
        r0 = r112;
        r1 = r103;
        r5.<init>(r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r5);
    L_0x0b34:
        r0 = r103;
        r5 = r0.flags;
        r5 = r5 & 2;
        if (r5 == 0) goto L_0x03f4;
    L_0x0b3c:
        r11 = new org.telegram.tgnet.TLRPC$TL_message;
        r11.<init>();
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r5 = r5.getNewMessageId();
        r11.id = r5;
        r11.local_id = r5;
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r9 = 0;
        r5.saveConfig(r9);
        r5 = 1;
        r11.unread = r5;
        r5 = 256; // 0x100 float:3.59E-43 double:1.265E-321;
        r11.flags = r5;
        r0 = r103;
        r5 = r0.inbox_date;
        if (r5 == 0) goto L_0x0c0f;
    L_0x0b6a:
        r0 = r103;
        r5 = r0.inbox_date;
        r11.date = r5;
    L_0x0b70:
        r5 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r11.from_id = r5;
        r5 = new org.telegram.tgnet.TLRPC$TL_peerUser;
        r5.<init>();
        r11.to_id = r5;
        r5 = r11.to_id;
        r0 = r112;
        r9 = r0.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r9 = r9.getClientUserId();
        r5.user_id = r9;
        r14 = 777000; // 0xbdb28 float:1.088809E-39 double:3.83889E-318;
        r11.dialog_id = r14;
        r0 = r103;
        r5 = r0.media;
        if (r5 == 0) goto L_0x0ba3;
    L_0x0b97:
        r0 = r103;
        r5 = r0.media;
        r11.media = r5;
        r5 = r11.flags;
        r5 = r5 | 512;
        r11.flags = r5;
    L_0x0ba3:
        r0 = r103;
        r5 = r0.message;
        r11.message = r5;
        r0 = r103;
        r5 = r0.entities;
        if (r5 == 0) goto L_0x0bb5;
    L_0x0baf:
        r0 = r103;
        r5 = r0.entities;
        r11.entities = r5;
    L_0x0bb5:
        if (r89 != 0) goto L_0x0bbc;
    L_0x0bb7:
        r89 = new java.util.ArrayList;
        r89.<init>();
    L_0x0bbc:
        r0 = r89;
        r0.add(r11);
        r4 = new org.telegram.messenger.MessageObject;
        r0 = r112;
        r10 = r0.currentAccount;
        r0 = r112;
        r5 = r0.createdDialogIds;
        r14 = r11.dialog_id;
        r9 = java.lang.Long.valueOf(r14);
        r14 = r5.contains(r9);
        r9 = r4;
        r12 = r7;
        r13 = r8;
        r9.<init>(r10, r11, r12, r13, r14);
        if (r88 != 0) goto L_0x0be2;
    L_0x0bdd:
        r88 = new android.util.LongSparseArray;
        r88.<init>();
    L_0x0be2:
        r14 = r11.dialog_id;
        r0 = r88;
        r42 = r0.get(r14);
        r42 = (java.util.ArrayList) r42;
        if (r42 != 0) goto L_0x0bfc;
    L_0x0bee:
        r42 = new java.util.ArrayList;
        r42.<init>();
        r14 = r11.dialog_id;
        r0 = r88;
        r1 = r42;
        r0.put(r14, r1);
    L_0x0bfc:
        r0 = r42;
        r0.add(r4);
        if (r93 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r93 = new java.util.ArrayList;
        r93.<init>();
    L_0x0CLASSNAME:
        r0 = r93;
        r0.add(r4);
        goto L_0x03f4;
    L_0x0c0f:
        r14 = java.lang.System.currentTimeMillis();
        r34 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r14 = r14 / r34;
        r5 = (int) r14;
        r11.date = r5;
        goto L_0x0b70;
    L_0x0c1c:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogPinned;
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        if (r106 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x0CLASSNAME:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x0CLASSNAME:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePinnedDialogs;
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        if (r106 != 0) goto L_0x0c3f;
    L_0x0c3a:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x0c3f:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x0CLASSNAME:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePrivacy;
        if (r5 == 0) goto L_0x0c5e;
    L_0x0c4e:
        if (r106 != 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x0CLASSNAME:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x0c5e:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateWebPage;
        if (r5 == 0) goto L_0x0CLASSNAME;
    L_0x0CLASSNAME:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateWebPage) r103;
        if (r111 != 0) goto L_0x0c6f;
    L_0x0c6a:
        r111 = new android.util.LongSparseArray;
        r111.<init>();
    L_0x0c6f:
        r0 = r103;
        r5 = r0.webpage;
        r14 = r5.id;
        r0 = r103;
        r5 = r0.webpage;
        r0 = r111;
        r0.put(r14, r5);
        goto L_0x03f4;
    L_0x0CLASSNAME:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelWebPage;
        if (r5 == 0) goto L_0x0ca2;
    L_0x0CLASSNAME:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChannelWebPage) r103;
        if (r111 != 0) goto L_0x0CLASSNAME;
    L_0x0c8c:
        r111 = new android.util.LongSparseArray;
        r111.<init>();
    L_0x0CLASSNAME:
        r0 = r103;
        r5 = r0.webpage;
        r14 = r5.id;
        r0 = r103;
        r5 = r0.webpage;
        r0 = r111;
        r0.put(r14, r5);
        goto L_0x03f4;
    L_0x0ca2:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelTooLong;
        if (r5 == 0) goto L_0x0d7c;
    L_0x0ca8:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChannelTooLong) r103;
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0cd1;
    L_0x0cb0:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r47;
        r5 = r5.append(r0);
        r9 = " channelId = ";
        r5 = r5.append(r9);
        r0 = r103;
        r9 = r0.channel_id;
        r5 = r5.append(r9);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x0cd1:
        r0 = r112;
        r5 = r0.channelsPts;
        r0 = r103;
        r9 = r0.channel_id;
        r49 = r5.get(r9);
        if (r49 != 0) goto L_0x0d46;
    L_0x0cdf:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r9 = r0.channel_id;
        r49 = r5.getChannelPtsSync(r9);
        if (r49 != 0) goto L_0x0d63;
    L_0x0cf1:
        r0 = r103;
        r5 = r0.channel_id;
        r5 = java.lang.Integer.valueOf(r5);
        r51 = r8.get(r5);
        r51 = (org.telegram.tgnet.TLRPC.Chat) r51;
        if (r51 == 0) goto L_0x0d07;
    L_0x0d01:
        r0 = r51;
        r5 = r0.min;
        if (r5 == 0) goto L_0x0d15;
    L_0x0d07:
        r0 = r103;
        r5 = r0.channel_id;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r112;
        r51 = r0.getChat(r5);
    L_0x0d15:
        if (r51 == 0) goto L_0x0d1d;
    L_0x0d17:
        r0 = r51;
        r5 = r0.min;
        if (r5 == 0) goto L_0x0d35;
    L_0x0d1d:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r9 = r0.channel_id;
        r51 = r5.getChatSync(r9);
        r5 = 1;
        r0 = r112;
        r1 = r51;
        r0.putChat(r1, r5);
    L_0x0d35:
        if (r51 == 0) goto L_0x0d46;
    L_0x0d37:
        r0 = r51;
        r5 = r0.min;
        if (r5 != 0) goto L_0x0d46;
    L_0x0d3d:
        r14 = 0;
        r0 = r112;
        r1 = r51;
        r0.loadUnknownChannel(r1, r14);
    L_0x0d46:
        if (r49 == 0) goto L_0x03f4;
    L_0x0d48:
        r0 = r103;
        r5 = r0.flags;
        r5 = r5 & 1;
        if (r5 == 0) goto L_0x0d71;
    L_0x0d50:
        r0 = r103;
        r5 = r0.pts;
        r0 = r49;
        if (r5 <= r0) goto L_0x03f4;
    L_0x0d58:
        r0 = r103;
        r5 = r0.channel_id;
        r0 = r112;
        r0.getChannelDifference(r5);
        goto L_0x03f4;
    L_0x0d63:
        r0 = r112;
        r5 = r0.channelsPts;
        r0 = r103;
        r9 = r0.channel_id;
        r0 = r49;
        r5.put(r9, r0);
        goto L_0x0d46;
    L_0x0d71:
        r0 = r103;
        r5 = r0.channel_id;
        r0 = r112;
        r0.getChannelDifference(r5);
        goto L_0x03f4;
    L_0x0d7c:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox;
        if (r5 == 0) goto L_0x0df2;
    L_0x0d82:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateReadChannelInbox) r103;
        r0 = r103;
        r5 = r0.max_id;
        r0 = (long) r5;
        r86 = r0;
        r0 = r103;
        r5 = r0.channel_id;
        r14 = (long) r5;
        r5 = 32;
        r14 = r14 << r5;
        r86 = r86 | r14;
        r0 = r103;
        r5 = r0.channel_id;
        r5 = -r5;
        r0 = (long) r5;
        r66 = r0;
        if (r83 != 0) goto L_0x0da6;
    L_0x0da1:
        r83 = new org.telegram.messenger.support.SparseLongArray;
        r83.<init>();
    L_0x0da6:
        r0 = r103;
        r5 = r0.channel_id;
        r5 = -r5;
        r0 = r83;
        r1 = r86;
        r0.put(r5, r1);
        r0 = r112;
        r5 = r0.dialogs_read_inbox_max;
        r9 = java.lang.Long.valueOf(r66);
        r109 = r5.get(r9);
        r109 = (java.lang.Integer) r109;
        if (r109 != 0) goto L_0x0dd5;
    L_0x0dc2:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = 0;
        r0 = r66;
        r5 = r5.getDialogReadMax(r9, r0);
        r109 = java.lang.Integer.valueOf(r5);
    L_0x0dd5:
        r0 = r112;
        r5 = r0.dialogs_read_inbox_max;
        r9 = java.lang.Long.valueOf(r66);
        r10 = r109.intValue();
        r0 = r103;
        r12 = r0.max_id;
        r10 = java.lang.Math.max(r10, r12);
        r10 = java.lang.Integer.valueOf(r10);
        r5.put(r9, r10);
        goto L_0x03f4;
    L_0x0df2:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox;
        if (r5 == 0) goto L_0x0e68;
    L_0x0df8:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateReadChannelOutbox) r103;
        r0 = r103;
        r5 = r0.max_id;
        r0 = (long) r5;
        r86 = r0;
        r0 = r103;
        r5 = r0.channel_id;
        r14 = (long) r5;
        r5 = 32;
        r14 = r14 << r5;
        r86 = r86 | r14;
        r0 = r103;
        r5 = r0.channel_id;
        r5 = -r5;
        r0 = (long) r5;
        r66 = r0;
        if (r84 != 0) goto L_0x0e1c;
    L_0x0e17:
        r84 = new org.telegram.messenger.support.SparseLongArray;
        r84.<init>();
    L_0x0e1c:
        r0 = r103;
        r5 = r0.channel_id;
        r5 = -r5;
        r0 = r84;
        r1 = r86;
        r0.put(r5, r1);
        r0 = r112;
        r5 = r0.dialogs_read_outbox_max;
        r9 = java.lang.Long.valueOf(r66);
        r109 = r5.get(r9);
        r109 = (java.lang.Integer) r109;
        if (r109 != 0) goto L_0x0e4b;
    L_0x0e38:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = 1;
        r0 = r66;
        r5 = r5.getDialogReadMax(r9, r0);
        r109 = java.lang.Integer.valueOf(r5);
    L_0x0e4b:
        r0 = r112;
        r5 = r0.dialogs_read_outbox_max;
        r9 = java.lang.Long.valueOf(r66);
        r10 = r109.intValue();
        r0 = r103;
        r12 = r0.max_id;
        r10 = java.lang.Math.max(r10, r12);
        r10 = java.lang.Integer.valueOf(r10);
        r5.put(r9, r10);
        goto L_0x03f4;
    L_0x0e68:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages;
        if (r5 == 0) goto L_0x0ec7;
    L_0x0e6e:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateDeleteChannelMessages) r103;
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0e97;
    L_0x0e76:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r47;
        r5 = r5.append(r0);
        r9 = " channelId = ";
        r5 = r5.append(r9);
        r0 = r103;
        r9 = r0.channel_id;
        r5 = r5.append(r9);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x0e97:
        if (r65 != 0) goto L_0x0e9e;
    L_0x0e99:
        r65 = new android.util.SparseArray;
        r65.<init>();
    L_0x0e9e:
        r0 = r103;
        r5 = r0.channel_id;
        r0 = r65;
        r45 = r0.get(r5);
        r45 = (java.util.ArrayList) r45;
        if (r45 != 0) goto L_0x0ebc;
    L_0x0eac:
        r45 = new java.util.ArrayList;
        r45.<init>();
        r0 = r103;
        r5 = r0.channel_id;
        r0 = r65;
        r1 = r45;
        r0.put(r5, r1);
    L_0x0ebc:
        r0 = r103;
        r5 = r0.messages;
        r0 = r45;
        r0.addAll(r5);
        goto L_0x03f4;
    L_0x0ec7:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChannel;
        if (r5 == 0) goto L_0x0var_;
    L_0x0ecd:
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0ef6;
    L_0x0ed1:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChannel) r103;
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r47;
        r5 = r5.append(r0);
        r9 = " channelId = ";
        r5 = r5.append(r9);
        r0 = r103;
        r9 = r0.channel_id;
        r5 = r5.append(r9);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x0ef6:
        if (r106 != 0) goto L_0x0efd;
    L_0x0ef8:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x0efd:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x0var_:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews;
        if (r5 == 0) goto L_0x0var_;
    L_0x0f0c:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChannelMessageViews) r103;
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x0var_;
    L_0x0var_:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r47;
        r5 = r5.append(r0);
        r9 = " channelId = ";
        r5 = r5.append(r9);
        r0 = r103;
        r9 = r0.channel_id;
        r5 = r5.append(r9);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x0var_:
        if (r50 != 0) goto L_0x0f3c;
    L_0x0var_:
        r50 = new android.util.SparseArray;
        r50.<init>();
    L_0x0f3c:
        r0 = r103;
        r5 = r0.channel_id;
        r0 = r50;
        r44 = r0.get(r5);
        r44 = (android.util.SparseIntArray) r44;
        if (r44 != 0) goto L_0x0f5a;
    L_0x0f4a:
        r44 = new android.util.SparseIntArray;
        r44.<init>();
        r0 = r103;
        r5 = r0.channel_id;
        r0 = r50;
        r1 = r44;
        r0.put(r5, r1);
    L_0x0f5a:
        r0 = r103;
        r5 = r0.id;
        r0 = r103;
        r9 = r0.views;
        r0 = r44;
        r0.put(r5, r9);
        goto L_0x03f4;
    L_0x0var_:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin;
        if (r5 == 0) goto L_0x0f9a;
    L_0x0f6f:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChatParticipantAdmin) r103;
        r0 = r112;
        r5 = r0.currentAccount;
        r12 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r13 = r0.chat_id;
        r0 = r103;
        r14 = r0.user_id;
        r15 = 2;
        r0 = r103;
        r5 = r0.is_admin;
        if (r5 == 0) goto L_0x0var_;
    L_0x0f8a:
        r16 = 1;
    L_0x0f8c:
        r0 = r103;
        r0 = r0.version;
        r17 = r0;
        r12.updateChatInfo(r13, r14, r15, r16, r17);
        goto L_0x03f4;
    L_0x0var_:
        r16 = 0;
        goto L_0x0f8c;
    L_0x0f9a:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights;
        if (r5 == 0) goto L_0x0fe2;
    L_0x0fa0:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChatDefaultBannedRights) r103;
        r0 = r103;
        r5 = r0.peer;
        r5 = r5.channel_id;
        if (r5 == 0) goto L_0x0fd9;
    L_0x0fac:
        r0 = r103;
        r5 = r0.peer;
        r0 = r5.channel_id;
        r52 = r0;
    L_0x0fb4:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r9 = r0.default_banned_rights;
        r0 = r103;
        r10 = r0.version;
        r0 = r52;
        r5.updateChatDefaultBannedRights(r0, r9, r10);
        if (r106 != 0) goto L_0x0fd0;
    L_0x0fcb:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x0fd0:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x0fd9:
        r0 = r103;
        r5 = r0.peer;
        r0 = r5.chat_id;
        r52 = r0;
        goto L_0x0fb4;
    L_0x0fe2:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSets;
        if (r5 == 0) goto L_0x0ff8;
    L_0x0fe8:
        if (r106 != 0) goto L_0x0fef;
    L_0x0fea:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x0fef:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x0ff8:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateStickerSetsOrder;
        if (r5 == 0) goto L_0x100e;
    L_0x0ffe:
        if (r106 != 0) goto L_0x1005;
    L_0x1000:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x1005:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x100e:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateNewStickerSet;
        if (r5 == 0) goto L_0x1024;
    L_0x1014:
        if (r106 != 0) goto L_0x101b;
    L_0x1016:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x101b:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x1024:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDraftMessage;
        if (r5 == 0) goto L_0x103a;
    L_0x102a:
        if (r106 != 0) goto L_0x1031;
    L_0x102c:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x1031:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x103a:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateSavedGifs;
        if (r5 == 0) goto L_0x1050;
    L_0x1040:
        if (r106 != 0) goto L_0x1047;
    L_0x1042:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x1047:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x1050:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
        if (r5 != 0) goto L_0x105c;
    L_0x1056:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateEditMessage;
        if (r5 == 0) goto L_0x125b;
    L_0x105c:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.UserConfig.getInstance(r5);
        r58 = r5.getClientUserId();
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage;
        if (r5 == 0) goto L_0x1143;
    L_0x106e:
        r47 = (org.telegram.tgnet.TLRPC.TL_updateEditChannelMessage) r47;
        r0 = r47;
        r6 = r0.message;
        r5 = r6.to_id;
        r5 = r5.channel_id;
        r5 = java.lang.Integer.valueOf(r5);
        r51 = r8.get(r5);
        r51 = (org.telegram.tgnet.TLRPC.Chat) r51;
        if (r51 != 0) goto L_0x1092;
    L_0x1084:
        r5 = r6.to_id;
        r5 = r5.channel_id;
        r5 = java.lang.Integer.valueOf(r5);
        r0 = r112;
        r51 = r0.getChat(r5);
    L_0x1092:
        if (r51 != 0) goto L_0x10ac;
    L_0x1094:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = r6.to_id;
        r9 = r9.channel_id;
        r51 = r5.getChatSync(r9);
        r5 = 1;
        r0 = r112;
        r1 = r51;
        r0.putChat(r1, r5);
    L_0x10ac:
        if (r51 == 0) goto L_0x10bb;
    L_0x10ae:
        r0 = r51;
        r5 = r0.megagroup;
        if (r5 == 0) goto L_0x10bb;
    L_0x10b4:
        r5 = r6.flags;
        r9 = -NUM; // 0xfffffffvar_ float:-0.0 double:NaN;
        r5 = r5 | r9;
        r6.flags = r5;
    L_0x10bb:
        r5 = r6.out;
        if (r5 != 0) goto L_0x10d2;
    L_0x10bf:
        r5 = r6.from_id;
        r0 = r112;
        r9 = r0.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r9 = r9.getClientUserId();
        if (r5 != r9) goto L_0x10d2;
    L_0x10cf:
        r5 = 1;
        r6.out = r5;
    L_0x10d2:
        if (r116 != 0) goto L_0x1163;
    L_0x10d4:
        r39 = 0;
        r5 = r6.entities;
        r60 = r5.size();
    L_0x10dc:
        r0 = r39;
        r1 = r60;
        if (r0 >= r1) goto L_0x1163;
    L_0x10e2:
        r5 = r6.entities;
        r0 = r39;
        r70 = r5.get(r0);
        r70 = (org.telegram.tgnet.TLRPC.MessageEntity) r70;
        r0 = r70;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_messageEntityMentionName;
        if (r5 == 0) goto L_0x115f;
    L_0x10f2:
        r70 = (org.telegram.tgnet.TLRPC.TL_messageEntityMentionName) r70;
        r0 = r70;
        r0 = r0.user_id;
        r108 = r0;
        r5 = java.lang.Integer.valueOf(r108);
        r107 = r7.get(r5);
        r107 = (org.telegram.tgnet.TLRPC.User) r107;
        if (r107 == 0) goto L_0x110c;
    L_0x1106:
        r0 = r107;
        r5 = r0.min;
        if (r5 == 0) goto L_0x1116;
    L_0x110c:
        r5 = java.lang.Integer.valueOf(r108);
        r0 = r112;
        r107 = r0.getUser(r5);
    L_0x1116:
        if (r107 == 0) goto L_0x111e;
    L_0x1118:
        r0 = r107;
        r5 = r0.min;
        if (r5 == 0) goto L_0x113e;
    L_0x111e:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r108;
        r107 = r5.getUserSync(r0);
        if (r107 == 0) goto L_0x1136;
    L_0x112e:
        r0 = r107;
        r5 = r0.min;
        if (r5 == 0) goto L_0x1136;
    L_0x1134:
        r107 = 0;
    L_0x1136:
        r5 = 1;
        r0 = r112;
        r1 = r107;
        r0.putUser(r1, r5);
    L_0x113e:
        if (r107 != 0) goto L_0x115f;
    L_0x1140:
        r5 = 0;
        goto L_0x0019;
    L_0x1143:
        r47 = (org.telegram.tgnet.TLRPC.TL_updateEditMessage) r47;
        r0 = r47;
        r6 = r0.message;
        r14 = r6.dialog_id;
        r0 = r58;
        r0 = (long) r0;
        r34 = r0;
        r5 = (r14 > r34 ? 1 : (r14 == r34 ? 0 : -1));
        if (r5 != 0) goto L_0x10bb;
    L_0x1154:
        r5 = 0;
        r6.unread = r5;
        r5 = 0;
        r6.media_unread = r5;
        r5 = 1;
        r6.out = r5;
        goto L_0x10bb;
    L_0x115f:
        r39 = r39 + 1;
        goto L_0x10dc;
    L_0x1163:
        r5 = r6.to_id;
        r5 = r5.chat_id;
        if (r5 == 0) goto L_0x121f;
    L_0x1169:
        r5 = r6.to_id;
        r5 = r5.chat_id;
        r5 = -r5;
        r14 = (long) r5;
        r6.dialog_id = r14;
    L_0x1171:
        r5 = r6.out;
        if (r5 == 0) goto L_0x1250;
    L_0x1175:
        r0 = r112;
        r0 = r0.dialogs_read_outbox_max;
        r95 = r0;
    L_0x117b:
        r14 = r6.dialog_id;
        r5 = java.lang.Long.valueOf(r14);
        r0 = r95;
        r109 = r0.get(r5);
        r109 = (java.lang.Integer) r109;
        if (r109 != 0) goto L_0x11ac;
    L_0x118b:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = r6.out;
        r14 = r6.dialog_id;
        r5 = r5.getDialogReadMax(r9, r14);
        r109 = java.lang.Integer.valueOf(r5);
        r14 = r6.dialog_id;
        r5 = java.lang.Long.valueOf(r14);
        r0 = r95;
        r1 = r109;
        r0.put(r5, r1);
    L_0x11ac:
        r5 = r109.intValue();
        r9 = r6.id;
        if (r5 >= r9) goto L_0x1258;
    L_0x11b4:
        r5 = 1;
    L_0x11b5:
        r6.unread = r5;
        r14 = r6.dialog_id;
        r0 = r58;
        r0 = (long) r0;
        r34 = r0;
        r5 = (r14 > r34 ? 1 : (r14 == r34 ? 0 : -1));
        if (r5 != 0) goto L_0x11cb;
    L_0x11c2:
        r5 = 1;
        r6.out = r5;
        r5 = 0;
        r6.unread = r5;
        r5 = 0;
        r6.media_unread = r5;
    L_0x11cb:
        r5 = r6.out;
        if (r5 == 0) goto L_0x11dd;
    L_0x11cf:
        r5 = r6.message;
        if (r5 != 0) goto L_0x11dd;
    L_0x11d3:
        r5 = "";
        r6.message = r5;
        r5 = "";
        r6.attachPath = r5;
    L_0x11dd:
        org.telegram.messenger.ImageLoader.saveMessageThumbs(r6);
        r4 = new org.telegram.messenger.MessageObject;
        r0 = r112;
        r5 = r0.currentAccount;
        r0 = r112;
        r9 = r0.createdDialogIds;
        r14 = r6.dialog_id;
        r10 = java.lang.Long.valueOf(r14);
        r9 = r9.contains(r10);
        r4.<init>(r5, r6, r7, r8, r9);
        if (r68 != 0) goto L_0x11fe;
    L_0x11f9:
        r68 = new android.util.LongSparseArray;
        r68.<init>();
    L_0x11fe:
        r14 = r6.dialog_id;
        r0 = r68;
        r42 = r0.get(r14);
        r42 = (java.util.ArrayList) r42;
        if (r42 != 0) goto L_0x1218;
    L_0x120a:
        r42 = new java.util.ArrayList;
        r42.<init>();
        r14 = r6.dialog_id;
        r0 = r68;
        r1 = r42;
        r0.put(r14, r1);
    L_0x1218:
        r0 = r42;
        r0.add(r4);
        goto L_0x03f4;
    L_0x121f:
        r5 = r6.to_id;
        r5 = r5.channel_id;
        if (r5 == 0) goto L_0x122f;
    L_0x1225:
        r5 = r6.to_id;
        r5 = r5.channel_id;
        r5 = -r5;
        r14 = (long) r5;
        r6.dialog_id = r14;
        goto L_0x1171;
    L_0x122f:
        r5 = r6.to_id;
        r5 = r5.user_id;
        r0 = r112;
        r9 = r0.currentAccount;
        r9 = org.telegram.messenger.UserConfig.getInstance(r9);
        r9 = r9.getClientUserId();
        if (r5 != r9) goto L_0x1247;
    L_0x1241:
        r5 = r6.to_id;
        r9 = r6.from_id;
        r5.user_id = r9;
    L_0x1247:
        r5 = r6.to_id;
        r5 = r5.user_id;
        r14 = (long) r5;
        r6.dialog_id = r14;
        goto L_0x1171;
    L_0x1250:
        r0 = r112;
        r0 = r0.dialogs_read_inbox_max;
        r95 = r0;
        goto L_0x117b;
    L_0x1258:
        r5 = 0;
        goto L_0x11b5;
    L_0x125b:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage;
        if (r5 == 0) goto L_0x129f;
    L_0x1261:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChannelPinnedMessage) r103;
        r5 = org.telegram.messenger.BuildVars.LOGS_ENABLED;
        if (r5 == 0) goto L_0x128a;
    L_0x1269:
        r5 = new java.lang.StringBuilder;
        r5.<init>();
        r0 = r47;
        r5 = r5.append(r0);
        r9 = " channelId = ";
        r5 = r5.append(r9);
        r0 = r103;
        r9 = r0.channel_id;
        r5 = r5.append(r9);
        r5 = r5.toString();
        org.telegram.messenger.FileLog.d(r5);
    L_0x128a:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r9 = r0.channel_id;
        r0 = r103;
        r10 = r0.id;
        r5.updateChatPinnedMessage(r9, r10);
        goto L_0x03f4;
    L_0x129f:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChatPinnedMessage;
        if (r5 == 0) goto L_0x12be;
    L_0x12a5:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChatPinnedMessage) r103;
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r9 = r0.chat_id;
        r0 = r103;
        r10 = r0.id;
        r5.updateChatPinnedMessage(r9, r10);
        goto L_0x03f4;
    L_0x12be:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateUserPinnedMessage;
        if (r5 == 0) goto L_0x12dd;
    L_0x12c4:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateUserPinnedMessage) r103;
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r9 = r0.user_id;
        r0 = r103;
        r10 = r0.id;
        r5.updateUserPinnedMessage(r9, r10);
        goto L_0x03f4;
    L_0x12dd:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateReadFeaturedStickers;
        if (r5 == 0) goto L_0x12f3;
    L_0x12e3:
        if (r106 != 0) goto L_0x12ea;
    L_0x12e5:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x12ea:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x12f3:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updatePhoneCall;
        if (r5 == 0) goto L_0x1309;
    L_0x12f9:
        if (r106 != 0) goto L_0x1300;
    L_0x12fb:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x1300:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x1309:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateLangPack;
        if (r5 == 0) goto L_0x1321;
    L_0x130f:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateLangPack) r103;
        r5 = new org.telegram.messenger.MessagesController$$Lambda$143;
        r0 = r112;
        r1 = r103;
        r5.<init>(r0, r1);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r5);
        goto L_0x03f4;
    L_0x1321:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateLangPackTooLong;
        if (r5 == 0) goto L_0x133c;
    L_0x1327:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateLangPackTooLong) r103;
        r5 = org.telegram.messenger.LocaleController.getInstance();
        r0 = r112;
        r9 = r0.currentAccount;
        r0 = r103;
        r10 = r0.lang_code;
        r5.reloadCurrentRemoteLocale(r9, r10);
        goto L_0x03f4;
    L_0x133c:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateFavedStickers;
        if (r5 == 0) goto L_0x1352;
    L_0x1342:
        if (r106 != 0) goto L_0x1349;
    L_0x1344:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x1349:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x1352:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateContactsReset;
        if (r5 == 0) goto L_0x1368;
    L_0x1358:
        if (r106 != 0) goto L_0x135f;
    L_0x135a:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x135f:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x1368:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateChannelAvailableMessages;
        if (r5 == 0) goto L_0x139c;
    L_0x136e:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateChannelAvailableMessages) r103;
        if (r57 != 0) goto L_0x1379;
    L_0x1374:
        r57 = new android.util.SparseIntArray;
        r57.<init>();
    L_0x1379:
        r0 = r103;
        r5 = r0.channel_id;
        r0 = r57;
        r61 = r0.get(r5);
        if (r61 == 0) goto L_0x138d;
    L_0x1385:
        r0 = r103;
        r5 = r0.available_min_id;
        r0 = r61;
        if (r0 >= r5) goto L_0x03f4;
    L_0x138d:
        r0 = r103;
        r5 = r0.channel_id;
        r0 = r103;
        r9 = r0.available_min_id;
        r0 = r57;
        r0.put(r5, r9);
        goto L_0x03f4;
    L_0x139c:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateDialogUnreadMark;
        if (r5 == 0) goto L_0x13b2;
    L_0x13a2:
        if (r106 != 0) goto L_0x13a9;
    L_0x13a4:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x13a9:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x13b2:
        r0 = r47;
        r5 = r0 instanceof org.telegram.tgnet.TLRPC.TL_updateMessagePoll;
        if (r5 == 0) goto L_0x03f4;
    L_0x13b8:
        r103 = r47;
        r103 = (org.telegram.tgnet.TLRPC.TL_updateMessagePoll) r103;
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.SendMessagesHelper.getInstance(r5);
        r0 = r103;
        r14 = r0.poll_id;
        r100 = r5.getVoteSendTime(r14);
        r14 = android.os.SystemClock.uptimeMillis();
        r14 = r14 - r100;
        r14 = java.lang.Math.abs(r14);
        r34 = 600; // 0x258 float:8.41E-43 double:2.964E-321;
        r5 = (r14 > r34 ? 1 : (r14 == r34 ? 0 : -1));
        if (r5 < 0) goto L_0x03f4;
    L_0x13dc:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r14 = r0.poll_id;
        r0 = r103;
        r9 = r0.poll;
        r0 = r103;
        r10 = r0.results;
        r5.updateMessagePollResults(r14, r9, r10);
        if (r106 != 0) goto L_0x13fa;
    L_0x13f5:
        r106 = new java.util.ArrayList;
        r106.<init>();
    L_0x13fa:
        r0 = r106;
        r1 = r47;
        r0.add(r1);
        goto L_0x03f4;
    L_0x1403:
        if (r88 == 0) goto L_0x1434;
    L_0x1405:
        r39 = 0;
        r96 = r88.size();
    L_0x140b:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x1434;
    L_0x1411:
        r0 = r88;
        r1 = r39;
        r80 = r0.keyAt(r1);
        r0 = r88;
        r1 = r39;
        r110 = r0.valueAt(r1);
        r110 = (java.util.ArrayList) r110;
        r0 = r112;
        r1 = r80;
        r3 = r110;
        r5 = r0.updatePrintingUsersWithNewMessages(r1, r3);
        if (r5 == 0) goto L_0x1431;
    L_0x142f:
        r91 = 1;
    L_0x1431:
        r39 = r39 + 1;
        goto L_0x140b;
    L_0x1434:
        if (r91 == 0) goto L_0x1439;
    L_0x1436:
        r112.updatePrintingStrings();
    L_0x1439:
        r78 = r77;
        r21 = r91;
        if (r59 == 0) goto L_0x144c;
    L_0x143f:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.ContactsController.getInstance(r5);
        r0 = r59;
        r5.processContactsUpdates(r0, r7);
    L_0x144c:
        if (r93 == 0) goto L_0x1468;
    L_0x144e:
        r94 = r93;
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r5.getStorageQueue();
        r9 = new org.telegram.messenger.MessagesController$$Lambda$144;
        r0 = r112;
        r1 = r94;
        r9.<init>(r0, r1);
        r5.postRunnable(r9);
    L_0x1468:
        if (r89 == 0) goto L_0x149b;
    L_0x146a:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.StatsController.getInstance(r5);
        r9 = org.telegram.messenger.ApplicationLoader.getCurrentNetworkType();
        r10 = 1;
        r12 = r89.size();
        r5.incrementReceivedItemsCount(r9, r10, r12);
        r0 = r112;
        r5 = r0.currentAccount;
        r12 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r14 = 1;
        r15 = 1;
        r16 = 0;
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.DownloadController.getInstance(r5);
        r17 = r5.getAutodownloadMask();
        r13 = r89;
        r12.putMessages(r13, r14, r15, r16, r17);
    L_0x149b:
        if (r68 == 0) goto L_0x14f4;
    L_0x149d:
        r46 = 0;
        r96 = r68.size();
    L_0x14a3:
        r0 = r46;
        r1 = r96;
        if (r0 >= r1) goto L_0x14f4;
    L_0x14a9:
        r13 = new org.telegram.tgnet.TLRPC$TL_messages_messages;
        r13.<init>();
        r0 = r68;
        r1 = r46;
        r85 = r0.valueAt(r1);
        r85 = (java.util.ArrayList) r85;
        r39 = 0;
        r97 = r85.size();
    L_0x14be:
        r0 = r39;
        r1 = r97;
        if (r0 >= r1) goto L_0x14d8;
    L_0x14c4:
        r9 = r13.messages;
        r0 = r85;
        r1 = r39;
        r5 = r0.get(r1);
        r5 = (org.telegram.messenger.MessageObject) r5;
        r5 = r5.messageOwner;
        r9.add(r5);
        r39 = r39 + 1;
        goto L_0x14be;
    L_0x14d8:
        r0 = r112;
        r5 = r0.currentAccount;
        r12 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r68;
        r1 = r46;
        r14 = r0.keyAt(r1);
        r16 = -2;
        r17 = 0;
        r18 = 0;
        r12.putMessages(r13, r14, r16, r17, r18);
        r46 = r46 + 1;
        goto L_0x14a3;
    L_0x14f4:
        if (r50 == 0) goto L_0x1504;
    L_0x14f6:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = 1;
        r0 = r50;
        r5.putChannelViews(r0, r9);
    L_0x1504:
        r20 = r68;
        r24 = r50;
        r18 = r111;
        r19 = r88;
        r23 = r53;
        r22 = r59;
        r17 = r106;
        r14 = new org.telegram.messenger.MessagesController$$Lambda$145;
        r15 = r112;
        r16 = r78;
        r14.<init>(r15, r16, r17, r18, r19, r20, r21, r22, r23, r24);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r14);
        r27 = r83;
        r28 = r84;
        r30 = r82;
        r29 = r79;
        r31 = r65;
        r32 = r57;
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r5.getStorageQueue();
        r25 = new org.telegram.messenger.MessagesController$$Lambda$146;
        r26 = r112;
        r25.<init>(r26, r27, r28, r29, r30, r31, r32);
        r0 = r25;
        r5.postRunnable(r0);
        if (r111 == 0) goto L_0x1551;
    L_0x1544:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r111;
        r5.putWebPages(r0);
    L_0x1551:
        if (r83 != 0) goto L_0x1559;
    L_0x1553:
        if (r84 != 0) goto L_0x1559;
    L_0x1555:
        if (r79 != 0) goto L_0x1559;
    L_0x1557:
        if (r82 == 0) goto L_0x1581;
    L_0x1559:
        if (r83 != 0) goto L_0x155d;
    L_0x155b:
        if (r82 == 0) goto L_0x156f;
    L_0x155d:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = 1;
        r0 = r83;
        r1 = r84;
        r2 = r82;
        r5.updateDialogsWithReadMessages(r0, r1, r2, r9);
    L_0x156f:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r9 = 1;
        r0 = r83;
        r1 = r84;
        r2 = r79;
        r5.markMessagesAsRead(r0, r1, r2, r9);
    L_0x1581:
        if (r82 == 0) goto L_0x159c;
    L_0x1583:
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r112;
        r9 = r0.currentAccount;
        r9 = org.telegram.tgnet.ConnectionsManager.getInstance(r9);
        r9 = r9.getCurrentTime();
        r0 = r82;
        r5.markMessagesContentAsRead(r0, r9);
    L_0x159c:
        if (r65 == 0) goto L_0x15d9;
    L_0x159e:
        r39 = 0;
        r96 = r65.size();
    L_0x15a4:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x15d9;
    L_0x15aa:
        r0 = r65;
        r1 = r39;
        r80 = r0.keyAt(r1);
        r0 = r65;
        r1 = r39;
        r45 = r0.valueAt(r1);
        r45 = (java.util.ArrayList) r45;
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r5.getStorageQueue();
        r9 = new org.telegram.messenger.MessagesController$$Lambda$147;
        r0 = r112;
        r1 = r45;
        r2 = r80;
        r9.<init>(r0, r1, r2);
        r5.postRunnable(r9);
        r39 = r39 + 1;
        goto L_0x15a4;
    L_0x15d9:
        if (r57 == 0) goto L_0x1614;
    L_0x15db:
        r39 = 0;
        r96 = r57.size();
    L_0x15e1:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x1614;
    L_0x15e7:
        r0 = r57;
        r1 = r39;
        r80 = r0.keyAt(r1);
        r0 = r57;
        r1 = r39;
        r74 = r0.valueAt(r1);
        r0 = r112;
        r5 = r0.currentAccount;
        r5 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r5 = r5.getStorageQueue();
        r9 = new org.telegram.messenger.MessagesController$$Lambda$148;
        r0 = r112;
        r1 = r80;
        r2 = r74;
        r9.<init>(r0, r1, r2);
        r5.postRunnable(r9);
        r39 = r39 + 1;
        goto L_0x15e1;
    L_0x1614:
        if (r99 == 0) goto L_0x1650;
    L_0x1616:
        r39 = 0;
        r96 = r99.size();
    L_0x161c:
        r0 = r39;
        r1 = r96;
        if (r0 >= r1) goto L_0x1650;
    L_0x1622:
        r0 = r99;
        r1 = r39;
        r103 = r0.get(r1);
        r103 = (org.telegram.tgnet.TLRPC.TL_updateEncryptedMessagesRead) r103;
        r0 = r112;
        r5 = r0.currentAccount;
        r33 = org.telegram.messenger.MessagesStorage.getInstance(r5);
        r0 = r103;
        r0 = r0.chat_id;
        r34 = r0;
        r0 = r103;
        r0 = r0.max_date;
        r35 = r0;
        r0 = r103;
        r0 = r0.date;
        r36 = r0;
        r37 = 1;
        r38 = 0;
        r33.createTaskForSecretChat(r34, r35, r36, r37, r38);
        r39 = r39 + 1;
        goto L_0x161c;
    L_0x1650:
        r5 = 1;
        goto L_0x0019;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.processUpdateArray(java.util.ArrayList, java.util.ArrayList, java.util.ArrayList, boolean):boolean");
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdateArray$235$MessagesController(ArrayList usersArr, ArrayList chatsArr) {
        putUsers(usersArr, false);
        putChats(chatsArr, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdateArray$236$MessagesController(ArrayList usersArr, ArrayList chatsArr) {
        putUsers(usersArr, false);
        putChats(chatsArr, false);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdateArray$238$MessagesController(TL_updateUserBlocked finalUpdate) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$162(this, finalUpdate));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$237$MessagesController(TL_updateUserBlocked finalUpdate) {
        if (!finalUpdate.blocked) {
            this.blockedUsers.delete(finalUpdate.user_id);
        } else if (this.blockedUsers.indexOfKey(finalUpdate.user_id) < 0) {
            this.blockedUsers.put(finalUpdate.user_id, 1);
        }
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoad, new Object[0]);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdateArray$239$MessagesController(TL_updateServiceNotification update) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(2), update.message, update.type);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdateArray$240$MessagesController(TL_updateLangPack update) {
        LocaleController.getInstance().saveRemoteLocaleStringsForCurrentLocale(update.difference, this.currentAccount);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$241$MessagesController(ArrayList pushMessagesFinal) {
        NotificationsController.getInstance(this.currentAccount).processNewMessages(pushMessagesFinal, true, false, null);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdateArray$242$MessagesController(ArrayList pushMessagesFinal) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$161(this, pushMessagesFinal));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdateArray$247$MessagesController(int interfaceUpdateMaskFinal, ArrayList updatesOnMainThreadFinal, LongSparseArray webPagesFinal, LongSparseArray messagesFinal, LongSparseArray editingMessagesFinal, boolean printChangedArg, ArrayList contactsIdsFinal, ArrayList chatInfoToUpdateFinal, SparseArray channelViewsFinal) {
        int size;
        int a;
        int size2;
        int b;
        long dialog_id;
        ArrayList<MessageObject> arrayList;
        int updateMask = interfaceUpdateMaskFinal;
        boolean hasDraftUpdates = false;
        if (updatesOnMainThreadFinal != null) {
            ArrayList<User> dbUsers = new ArrayList();
            ArrayList<User> dbUsersStatus = new ArrayList();
            Editor editor = null;
            size = updatesOnMainThreadFinal.size();
            for (a = 0; a < size; a++) {
                Update baseUpdate = (Update) updatesOnMainThreadFinal.get(a);
                User currentUser;
                User toDbUser;
                Peer peer;
                long did;
                TL_dialog dialog;
                Chat chat;
                if (baseUpdate instanceof TL_updatePrivacy) {
                    TL_updatePrivacy update = (TL_updatePrivacy) baseUpdate;
                    if (update.key instanceof TL_privacyKeyStatusTimestamp) {
                        ContactsController.getInstance(this.currentAccount).setPrivacyRules(update.rules, 0);
                    } else if (update.key instanceof TL_privacyKeyChatInvite) {
                        ContactsController.getInstance(this.currentAccount).setPrivacyRules(update.rules, 1);
                    } else if (update.key instanceof TL_privacyKeyPhoneCall) {
                        ContactsController.getInstance(this.currentAccount).setPrivacyRules(update.rules, 2);
                    } else if (update.key instanceof TL_privacyKeyPhoneP2P) {
                        ContactsController.getInstance(this.currentAccount).setPrivacyRules(update.rules, 3);
                    }
                } else if (baseUpdate instanceof TL_updateUserStatus) {
                    TL_updateUserStatus update2 = (TL_updateUserStatus) baseUpdate;
                    currentUser = getUser(Integer.valueOf(update2.user_id));
                    if (update2.status instanceof TL_userStatusRecently) {
                        update2.status.expires = -100;
                    } else if (update2.status instanceof TL_userStatusLastWeek) {
                        update2.status.expires = -101;
                    } else if (update2.status instanceof TL_userStatusLastMonth) {
                        update2.status.expires = -102;
                    }
                    if (currentUser != null) {
                        currentUser.id = update2.user_id;
                        currentUser.status = update2.status;
                    }
                    toDbUser = new TL_user();
                    toDbUser.id = update2.user_id;
                    toDbUser.status = update2.status;
                    dbUsersStatus.add(toDbUser);
                    if (update2.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        NotificationsController.getInstance(this.currentAccount).setLastOnlineFromOtherDevice(update2.status.expires);
                    }
                } else if (baseUpdate instanceof TL_updateUserName) {
                    TL_updateUserName update3 = (TL_updateUserName) baseUpdate;
                    currentUser = getUser(Integer.valueOf(update3.user_id));
                    if (currentUser != null) {
                        if (!UserObject.isContact(currentUser)) {
                            currentUser.first_name = update3.first_name;
                            currentUser.last_name = update3.last_name;
                        }
                        if (!TextUtils.isEmpty(currentUser.username)) {
                            this.objectsByUsernames.remove(currentUser.username);
                        }
                        if (TextUtils.isEmpty(update3.username)) {
                            this.objectsByUsernames.put(update3.username, currentUser);
                        }
                        currentUser.username = update3.username;
                    }
                    toDbUser = new TL_user();
                    toDbUser.id = update3.user_id;
                    toDbUser.first_name = update3.first_name;
                    toDbUser.last_name = update3.last_name;
                    toDbUser.username = update3.username;
                    dbUsers.add(toDbUser);
                } else if (baseUpdate instanceof TL_updateDialogPinned) {
                    TL_updateDialogPinned updateDialogPinned = (TL_updateDialogPinned) baseUpdate;
                    if (updateDialogPinned.peer instanceof TL_dialogPeer) {
                        peer = ((TL_dialogPeer) updateDialogPinned.peer).peer;
                        if (peer instanceof TL_peerUser) {
                            did = (long) peer.user_id;
                        } else if (peer instanceof TL_peerChat) {
                            did = (long) (-peer.chat_id);
                        } else {
                            did = (long) (-peer.channel_id);
                        }
                    } else {
                        did = 0;
                    }
                    if (!pinDialog(did, updateDialogPinned.pinned, null, -1)) {
                        UserConfig.getInstance(this.currentAccount).pinnedDialogsLoaded = false;
                        UserConfig.getInstance(this.currentAccount).saveConfig(false);
                        loadPinnedDialogs(did, null);
                    }
                } else if (baseUpdate instanceof TL_updatePinnedDialogs) {
                    ArrayList<Long> order;
                    TL_updatePinnedDialogs update4 = (TL_updatePinnedDialogs) baseUpdate;
                    UserConfig.getInstance(this.currentAccount).pinnedDialogsLoaded = false;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    if ((update4.flags & 1) != 0) {
                        order = new ArrayList();
                        ArrayList<DialogPeer> peers = ((TL_updatePinnedDialogs) baseUpdate).order;
                        size2 = peers.size();
                        for (b = 0; b < size2; b++) {
                            DialogPeer dialogPeer = (DialogPeer) peers.get(b);
                            if (dialogPeer instanceof TL_dialogPeer) {
                                peer = ((TL_dialogPeer) dialogPeer).peer;
                                if (peer.user_id != 0) {
                                    did = (long) peer.user_id;
                                } else if (peer.chat_id != 0) {
                                    did = (long) (-peer.chat_id);
                                } else {
                                    did = (long) (-peer.channel_id);
                                }
                            } else {
                                did = 0;
                            }
                            order.add(Long.valueOf(did));
                        }
                    } else {
                        order = null;
                    }
                    loadPinnedDialogs(0, order);
                } else if (baseUpdate instanceof TL_updateUserPhoto) {
                    TL_updateUserPhoto update5 = (TL_updateUserPhoto) baseUpdate;
                    currentUser = getUser(Integer.valueOf(update5.user_id));
                    if (currentUser != null) {
                        currentUser.photo = update5.photo;
                    }
                    toDbUser = new TL_user();
                    toDbUser.id = update5.user_id;
                    toDbUser.photo = update5.photo;
                    dbUsers.add(toDbUser);
                } else if (baseUpdate instanceof TL_updateUserPhone) {
                    TL_updateUserPhone update6 = (TL_updateUserPhone) baseUpdate;
                    currentUser = getUser(Integer.valueOf(update6.user_id));
                    if (currentUser != null) {
                        currentUser.phone = update6.phone;
                        Utilities.phoneBookQueue.postRunnable(new MessagesController$$Lambda$157(this, currentUser));
                    }
                    toDbUser = new TL_user();
                    toDbUser.id = update6.user_id;
                    toDbUser.phone = update6.phone;
                    dbUsers.add(toDbUser);
                } else if (baseUpdate instanceof TL_updateNotifySettings) {
                    TL_updateNotifySettings update7 = (TL_updateNotifySettings) baseUpdate;
                    if (update7.notify_settings instanceof TL_peerNotifySettings) {
                        if (editor == null) {
                            editor = this.notificationsPreferences.edit();
                        }
                        int currentTime1 = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                        if (update7.peer instanceof TL_notifyPeer) {
                            TL_notifyPeer notifyPeer = (TL_notifyPeer) update7.peer;
                            if (notifyPeer.peer.user_id != 0) {
                                dialog_id = (long) notifyPeer.peer.user_id;
                            } else if (notifyPeer.peer.chat_id != 0) {
                                dialog_id = (long) (-notifyPeer.peer.chat_id);
                            } else {
                                dialog_id = (long) (-notifyPeer.peer.channel_id);
                            }
                            dialog = (TL_dialog) this.dialogs_dict.get(dialog_id);
                            if (dialog != null) {
                                dialog.notify_settings = update7.notify_settings;
                            }
                            if ((update7.notify_settings.flags & 2) != 0) {
                                editor.putBoolean("silent_" + dialog_id, update7.notify_settings.silent);
                            } else {
                                editor.remove("silent_" + dialog_id);
                            }
                            if ((update7.notify_settings.flags & 4) == 0) {
                                if (dialog != null) {
                                    update7.notify_settings.mute_until = 0;
                                }
                                editor.remove("notify2_" + dialog_id);
                                MessagesStorage.getInstance(this.currentAccount).setDialogFlags(dialog_id, 0);
                            } else if (update7.notify_settings.mute_until > currentTime1) {
                                int until = 0;
                                if (update7.notify_settings.mute_until > 31536000 + currentTime1) {
                                    editor.putInt("notify2_" + dialog_id, 2);
                                    if (dialog != null) {
                                        update7.notify_settings.mute_until = Integer.MAX_VALUE;
                                    }
                                } else {
                                    until = update7.notify_settings.mute_until;
                                    editor.putInt("notify2_" + dialog_id, 3);
                                    editor.putInt("notifyuntil_" + dialog_id, update7.notify_settings.mute_until);
                                    if (dialog != null) {
                                        update7.notify_settings.mute_until = until;
                                    }
                                }
                                MessagesStorage.getInstance(this.currentAccount).setDialogFlags(dialog_id, (((long) until) << 32) | 1);
                                NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(dialog_id);
                            } else {
                                if (dialog != null) {
                                    update7.notify_settings.mute_until = 0;
                                }
                                editor.putInt("notify2_" + dialog_id, 0);
                                MessagesStorage.getInstance(this.currentAccount).setDialogFlags(dialog_id, 0);
                            }
                        } else if (update7.peer instanceof TL_notifyChats) {
                            if ((update7.notify_settings.flags & 1) != 0) {
                                editor.putBoolean("EnablePreviewGroup", update7.notify_settings.show_previews);
                            }
                            if ((update7.notify_settings.flags & 2) != 0) {
                            }
                            if ((update7.notify_settings.flags & 4) != 0) {
                                editor.putInt("EnableGroup2", update7.notify_settings.mute_until);
                            }
                        } else if (update7.peer instanceof TL_notifyUsers) {
                            if ((update7.notify_settings.flags & 1) != 0) {
                                editor.putBoolean("EnablePreviewAll", update7.notify_settings.show_previews);
                            }
                            if ((update7.notify_settings.flags & 2) != 0) {
                            }
                            if ((update7.notify_settings.flags & 4) != 0) {
                                editor.putInt("EnableAll2", update7.notify_settings.mute_until);
                            }
                        } else if (update7.peer instanceof TL_notifyBroadcasts) {
                            if ((update7.notify_settings.flags & 1) != 0) {
                                editor.putBoolean("EnablePreviewChannel", update7.notify_settings.show_previews);
                            }
                            if ((update7.notify_settings.flags & 2) != 0) {
                            }
                            if ((update7.notify_settings.flags & 4) != 0) {
                                editor.putInt("EnableChannel2", update7.notify_settings.mute_until);
                            }
                        }
                    }
                } else if (baseUpdate instanceof TL_updateChannel) {
                    TL_updateChannel update8 = (TL_updateChannel) baseUpdate;
                    dialog = (TL_dialog) this.dialogs_dict.get(-((long) update8.channel_id));
                    chat = getChat(Integer.valueOf(update8.channel_id));
                    if (chat != null) {
                        if (dialog == null && (chat instanceof TL_channel) && !chat.left) {
                            Utilities.stageQueue.postRunnable(new MessagesController$$Lambda$158(this, update8));
                        } else if (chat.left && dialog != null && (this.proxyDialog == null || this.proxyDialog.id != dialog.id)) {
                            deleteDialog(dialog.id, 0);
                        }
                    }
                    updateMask |= 8192;
                    loadFullChat(update8.channel_id, 0, true);
                } else if (baseUpdate instanceof TL_updateChatDefaultBannedRights) {
                    int chatId;
                    TL_updateChatDefaultBannedRights update9 = (TL_updateChatDefaultBannedRights) baseUpdate;
                    if (update9.peer.channel_id != 0) {
                        chatId = update9.peer.channel_id;
                    } else {
                        chatId = update9.peer.chat_id;
                    }
                    chat = getChat(Integer.valueOf(chatId));
                    if (chat != null) {
                        chat.default_banned_rights = update9.default_banned_rights;
                        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$159(this, chat));
                    }
                } else if (baseUpdate instanceof TL_updateStickerSets) {
                    TL_updateStickerSets update10 = (TL_updateStickerSets) baseUpdate;
                    DataQuery.getInstance(this.currentAccount).loadStickers(0, false, true);
                } else if (baseUpdate instanceof TL_updateStickerSetsOrder) {
                    DataQuery.getInstance(this.currentAccount).reorderStickers(((TL_updateStickerSetsOrder) baseUpdate).masks ? 1 : 0, ((TL_updateStickerSetsOrder) baseUpdate).order);
                } else if (baseUpdate instanceof TL_updateFavedStickers) {
                    DataQuery.getInstance(this.currentAccount).loadRecents(2, false, false, true);
                } else if (baseUpdate instanceof TL_updateContactsReset) {
                    ContactsController.getInstance(this.currentAccount).forceImportContacts();
                } else if (baseUpdate instanceof TL_updateNewStickerSet) {
                    DataQuery.getInstance(this.currentAccount).addNewStickerSet(((TL_updateNewStickerSet) baseUpdate).stickerset);
                } else if (baseUpdate instanceof TL_updateSavedGifs) {
                    this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).commit();
                } else if (baseUpdate instanceof TL_updateRecentStickers) {
                    this.emojiPreferences.edit().putLong("lastStickersLoadTime", 0).commit();
                } else if (baseUpdate instanceof TL_updateDraftMessage) {
                    TL_updateDraftMessage update11 = (TL_updateDraftMessage) baseUpdate;
                    hasDraftUpdates = true;
                    peer = ((TL_updateDraftMessage) baseUpdate).peer;
                    if (peer.user_id != 0) {
                        did = (long) peer.user_id;
                    } else if (peer.channel_id != 0) {
                        did = (long) (-peer.channel_id);
                    } else {
                        did = (long) (-peer.chat_id);
                    }
                    DataQuery.getInstance(this.currentAccount).saveDraft(did, update11.draft, null, true);
                } else if (baseUpdate instanceof TL_updateReadFeaturedStickers) {
                    DataQuery.getInstance(this.currentAccount).markFaturedStickersAsRead(false);
                } else if (baseUpdate instanceof TL_updatePhoneCall) {
                    PhoneCall call = ((TL_updatePhoneCall) baseUpdate).phone_call;
                    VoIPService svc = VoIPService.getSharedInstance();
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("Received call in update: " + call);
                        FileLog.d("call id " + call.id);
                    }
                    if (call instanceof TL_phoneCallRequested) {
                        if (call.date + (this.callRingTimeout / 1000) < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("ignoring too old call");
                            }
                        } else if (VERSION.SDK_INT < 21 || NotificationManagerCompat.from(ApplicationLoader.applicationContext).areNotificationsEnabled()) {
                            TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                            if (svc == null && VoIPService.callIShouldHavePutIntoIntent == null && tm.getCallState() == 0) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("Starting service for call " + call.id);
                                }
                                VoIPService.callIShouldHavePutIntoIntent = call;
                                Intent intent = new Intent(ApplicationLoader.applicationContext, VoIPService.class);
                                intent.putExtra("is_outgoing", false);
                                intent.putExtra("user_id", call.participant_id == UserConfig.getInstance(this.currentAccount).getClientUserId() ? call.admin_id : call.participant_id);
                                intent.putExtra("account", this.currentAccount);
                                try {
                                    if (VERSION.SDK_INT >= 26) {
                                        ApplicationLoader.applicationContext.startForegroundService(intent);
                                    } else {
                                        ApplicationLoader.applicationContext.startService(intent);
                                    }
                                } catch (Throwable e) {
                                    FileLog.e(e);
                                }
                            } else {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("Auto-declining call " + call.id + " because there's already active one");
                                }
                                TLObject req = new TL_phone_discardCall();
                                req.peer = new TL_inputPhoneCall();
                                req.peer.access_hash = call.access_hash;
                                req.peer.id = call.id;
                                req.reason = new TL_phoneCallDiscardReasonBusy();
                                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$160(this));
                            }
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("Ignoring incoming call because notifications are disabled in system");
                        }
                    } else if (svc != null && call != null) {
                        svc.onCallUpdated(call);
                    } else if (VoIPService.callIShouldHavePutIntoIntent != null) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("Updated the call while the service is starting");
                        }
                        if (call.id == VoIPService.callIShouldHavePutIntoIntent.id) {
                            VoIPService.callIShouldHavePutIntoIntent = call;
                        }
                    }
                } else if (baseUpdate instanceof TL_updateDialogUnreadMark) {
                    TL_updateDialogUnreadMark update12 = (TL_updateDialogUnreadMark) baseUpdate;
                    if (update12.peer instanceof TL_dialogPeer) {
                        TL_dialogPeer dialogPeer2 = (TL_dialogPeer) update12.peer;
                        if (dialogPeer2.peer.user_id != 0) {
                            did = (long) dialogPeer2.peer.user_id;
                        } else if (dialogPeer2.peer.chat_id != 0) {
                            did = (long) (-dialogPeer2.peer.chat_id);
                        } else {
                            did = (long) (-dialogPeer2.peer.channel_id);
                        }
                    } else {
                        did = 0;
                    }
                    MessagesStorage.getInstance(this.currentAccount).setDialogUnread(did, update12.unread);
                    dialog = (TL_dialog) this.dialogs_dict.get(did);
                    if (!(dialog == null || dialog.unread_mark == update12.unread)) {
                        dialog.unread_mark = update12.unread;
                        if (dialog.unread_count == 0 && !isDialogMuted(did)) {
                            if (dialog.unread_mark) {
                                this.unreadUnmutedDialogs++;
                            } else {
                                this.unreadUnmutedDialogs--;
                            }
                        }
                        updateMask |= 256;
                    }
                } else if (baseUpdate instanceof TL_updateMessagePoll) {
                    TL_updateMessagePoll update13 = (TL_updateMessagePoll) baseUpdate;
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didUpdatePollResults, Long.valueOf(update13.poll_id), update13.poll, update13.results);
                } else if (!((baseUpdate instanceof TL_updateGroupCall) || (baseUpdate instanceof TL_updateGroupCallParticipant))) {
                }
            }
            if (editor != null) {
                editor.commit();
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
            }
            MessagesStorage.getInstance(this.currentAccount).updateUsers(dbUsersStatus, true, true, true);
            MessagesStorage.getInstance(this.currentAccount).updateUsers(dbUsers, false, true, true);
        }
        if (webPagesFinal != null) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didReceivedWebpagesInUpdates, webPagesFinal);
            size = webPagesFinal.size();
            for (b = 0; b < size; b++) {
                long key = webPagesFinal.keyAt(b);
                arrayList = (ArrayList) this.reloadingWebpagesPending.get(key);
                this.reloadingWebpagesPending.remove(key);
                if (arrayList != null) {
                    WebPage webpage = (WebPage) webPagesFinal.valueAt(b);
                    ArrayList arr = new ArrayList();
                    dialog_id = 0;
                    if ((webpage instanceof TL_webPage) || (webpage instanceof TL_webPageEmpty)) {
                        size2 = arrayList.size();
                        for (a = 0; a < size2; a++) {
                            ((MessageObject) arrayList.get(a)).messageOwner.media.webpage = webpage;
                            if (a == 0) {
                                dialog_id = ((MessageObject) arrayList.get(a)).getDialogId();
                                ImageLoader.saveMessageThumbs(((MessageObject) arrayList.get(a)).messageOwner);
                            }
                            arr.add(((MessageObject) arrayList.get(a)).messageOwner);
                        }
                    } else {
                        this.reloadingWebpagesPending.put(webpage.id, arrayList);
                    }
                    if (!arr.isEmpty()) {
                        MessagesStorage.getInstance(this.currentAccount).putMessages(arr, true, true, false, DownloadController.getInstance(this.currentAccount).getAutodownloadMask());
                        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialog_id), arrayList);
                    }
                }
            }
        }
        boolean updateDialogs = false;
        if (messagesFinal != null) {
            size = messagesFinal.size();
            for (a = 0; a < size; a++) {
                updateInterfaceWithMessages(messagesFinal.keyAt(a), (ArrayList) messagesFinal.valueAt(a));
            }
            updateDialogs = true;
        } else if (hasDraftUpdates) {
            sortDialogs(null);
            updateDialogs = true;
        }
        if (editingMessagesFinal != null) {
            size = editingMessagesFinal.size();
            for (b = 0; b < size; b++) {
                dialog_id = editingMessagesFinal.keyAt(b);
                arrayList = (ArrayList) editingMessagesFinal.valueAt(b);
                MessageObject oldObject = (MessageObject) this.dialogMessage.get(dialog_id);
                if (oldObject != null) {
                    a = 0;
                    size2 = arrayList.size();
                    while (a < size2) {
                        MessageObject newMessage = (MessageObject) arrayList.get(a);
                        if (oldObject.getId() != newMessage.getId()) {
                            if (oldObject.getDialogId() == newMessage.getDialogId() && (oldObject.messageOwner.action instanceof TL_messageActionPinMessage) && oldObject.replyMessageObject != null && oldObject.replyMessageObject.getId() == newMessage.getId()) {
                                oldObject.replyMessageObject = newMessage;
                                oldObject.generatePinMessageText(null, null);
                                updateDialogs = true;
                                break;
                            }
                            a++;
                        } else {
                            this.dialogMessage.put(dialog_id, newMessage);
                            if (newMessage.messageOwner.to_id != null && newMessage.messageOwner.to_id.channel_id == 0) {
                                this.dialogMessagesByIds.put(newMessage.getId(), newMessage);
                            }
                            updateDialogs = true;
                        }
                    }
                }
                DataQuery.getInstance(this.currentAccount).loadReplyMessagesForMessages(arrayList, dialog_id);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialog_id), arrayList);
            }
        }
        if (updateDialogs) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        if (printChangedArg) {
            updateMask |= 64;
        }
        if (contactsIdsFinal != null) {
            updateMask = (updateMask | 1) | 128;
        }
        if (chatInfoToUpdateFinal != null) {
            size = chatInfoToUpdateFinal.size();
            for (a = 0; a < size; a++) {
                MessagesStorage.getInstance(this.currentAccount).updateChatParticipants((ChatParticipants) chatInfoToUpdateFinal.get(a));
            }
        }
        if (channelViewsFinal != null) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didUpdatedMessagesViews, channelViewsFinal);
        }
        if (updateMask != 0) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(updateMask));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$243$MessagesController(User currentUser) {
        ContactsController.getInstance(this.currentAccount).addContactToPhoneBook(currentUser, true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$244$MessagesController(TL_updateChannel update) {
        getChannelDifference(update.channel_id, 1, 0, null);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$245$MessagesController(Chat chat) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.channelRightsUpdated, chat);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$246$MessagesController(TLObject response, TL_error error) {
        if (response != null) {
            processUpdates((Updates) response, false);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdateArray$249$MessagesController(SparseLongArray markAsReadMessagesInboxFinal, SparseLongArray markAsReadMessagesOutboxFinal, SparseIntArray markAsReadEncryptedFinal, ArrayList markAsReadMessagesFinal, SparseArray deletedMessagesFinal, SparseIntArray clearHistoryMessagesFinal) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$156(this, markAsReadMessagesInboxFinal, markAsReadMessagesOutboxFinal, markAsReadEncryptedFinal, markAsReadMessagesFinal, deletedMessagesFinal, clearHistoryMessagesFinal));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$248$MessagesController(SparseLongArray markAsReadMessagesInboxFinal, SparseLongArray markAsReadMessagesOutboxFinal, SparseIntArray markAsReadEncryptedFinal, ArrayList markAsReadMessagesFinal, SparseArray deletedMessagesFinal, SparseIntArray clearHistoryMessagesFinal) {
        int size;
        int b;
        int key;
        MessageObject obj;
        int a;
        int updateMask = 0;
        if (!(markAsReadMessagesInboxFinal == null && markAsReadMessagesOutboxFinal == null)) {
            int messageId;
            TL_dialog dialog;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesRead, markAsReadMessagesInboxFinal, markAsReadMessagesOutboxFinal);
            if (markAsReadMessagesInboxFinal != null) {
                NotificationsController.getInstance(this.currentAccount).processReadMessages(markAsReadMessagesInboxFinal, 0, 0, 0, false);
                Editor editor = this.notificationsPreferences.edit();
                size = markAsReadMessagesInboxFinal.size();
                for (b = 0; b < size; b++) {
                    key = markAsReadMessagesInboxFinal.keyAt(b);
                    messageId = (int) markAsReadMessagesInboxFinal.valueAt(b);
                    dialog = (TL_dialog) this.dialogs_dict.get((long) key);
                    if (dialog != null && dialog.top_message > 0 && dialog.top_message <= messageId) {
                        obj = (MessageObject) this.dialogMessage.get(dialog.id);
                        if (!(obj == null || obj.isOut())) {
                            obj.setIsRead();
                            updateMask |= 256;
                        }
                    }
                    if (key != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        editor.remove("diditem" + key);
                        editor.remove("diditemo" + key);
                    }
                }
                editor.commit();
            }
            if (markAsReadMessagesOutboxFinal != null) {
                size = markAsReadMessagesOutboxFinal.size();
                for (b = 0; b < size; b++) {
                    messageId = (int) markAsReadMessagesOutboxFinal.valueAt(b);
                    dialog = (TL_dialog) this.dialogs_dict.get((long) markAsReadMessagesOutboxFinal.keyAt(b));
                    if (dialog != null && dialog.top_message > 0 && dialog.top_message <= messageId) {
                        obj = (MessageObject) this.dialogMessage.get(dialog.id);
                        if (obj != null && obj.isOut()) {
                            obj.setIsRead();
                            updateMask |= 256;
                        }
                    }
                }
            }
        }
        if (markAsReadEncryptedFinal != null) {
            size = markAsReadEncryptedFinal.size();
            for (a = 0; a < size; a++) {
                key = markAsReadEncryptedFinal.keyAt(a);
                int value = markAsReadEncryptedFinal.valueAt(a);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadEncrypted, Integer.valueOf(key), Integer.valueOf(value));
                long dialog_id = ((long) key) << 32;
                if (((TL_dialog) this.dialogs_dict.get(dialog_id)) != null) {
                    MessageObject message = (MessageObject) this.dialogMessage.get(dialog_id);
                    if (message != null && message.messageOwner.date <= value) {
                        message.setIsRead();
                        updateMask |= 256;
                    }
                }
            }
        }
        if (markAsReadMessagesFinal != null) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, markAsReadMessagesFinal);
        }
        if (deletedMessagesFinal != null) {
            size = deletedMessagesFinal.size();
            for (a = 0; a < size; a++) {
                key = deletedMessagesFinal.keyAt(a);
                ArrayList<Integer> arrayList = (ArrayList) deletedMessagesFinal.valueAt(a);
                if (arrayList != null) {
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(key));
                    int size2;
                    if (key == 0) {
                        size2 = arrayList.size();
                        for (b = 0; b < size2; b++) {
                            obj = (MessageObject) this.dialogMessagesByIds.get(((Integer) arrayList.get(b)).intValue());
                            if (obj != null) {
                                obj.deleted = true;
                            }
                        }
                    } else {
                        obj = (MessageObject) this.dialogMessage.get((long) (-key));
                        if (obj != null) {
                            size2 = arrayList.size();
                            for (b = 0; b < size2; b++) {
                                if (obj.getId() == ((Integer) arrayList.get(b)).intValue()) {
                                    obj.deleted = true;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            NotificationsController.getInstance(this.currentAccount).removeDeletedMessagesFromNotifications(deletedMessagesFinal);
        }
        if (clearHistoryMessagesFinal != null) {
            size = clearHistoryMessagesFinal.size();
            for (a = 0; a < size; a++) {
                key = clearHistoryMessagesFinal.keyAt(a);
                int id = clearHistoryMessagesFinal.valueAt(a);
                long did = (long) (-key);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.historyCleared, Long.valueOf(did), Integer.valueOf(id));
                obj = (MessageObject) this.dialogMessage.get(did);
                if (obj != null && obj.getId() <= id) {
                    obj.deleted = true;
                    break;
                }
            }
            NotificationsController.getInstance(this.currentAccount).removeDeletedHisoryFromNotifications(clearHistoryMessagesFinal);
        }
        if (updateMask != 0) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(updateMask));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdateArray$250$MessagesController(ArrayList arrayList, int key) {
        MessagesStorage.getInstance(this.currentAccount).updateDialogsWithDeletedMessages(arrayList, MessagesStorage.getInstance(this.currentAccount).markMessagesAsDeleted(arrayList, false, key), false, key);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$processUpdateArray$251$MessagesController(int key, int id) {
        MessagesStorage.getInstance(this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), MessagesStorage.getInstance(this.currentAccount).markMessagesAsDeleted(key, id, false), false, key);
    }

    public boolean isDialogMuted(long dialog_id) {
        int mute_type = this.notificationsPreferences.getInt("notify2_" + dialog_id, -1);
        if (mute_type == -1) {
            if (NotificationsController.getInstance(this.currentAccount).isGlobalNotificationsEnabled(dialog_id)) {
                return false;
            }
            return true;
        } else if (mute_type == 2) {
            return true;
        } else {
            if (mute_type != 3 || this.notificationsPreferences.getInt("notifyuntil_" + dialog_id, 0) < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
                return false;
            }
            return true;
        }
    }

    private boolean updatePrintingUsersWithNewMessages(long uid, ArrayList<MessageObject> messages) {
        if (uid > 0) {
            if (((ArrayList) this.printingUsers.get(Long.valueOf(uid))) != null) {
                this.printingUsers.remove(Long.valueOf(uid));
                return true;
            }
        } else if (uid < 0) {
            ArrayList<Integer> messagesUsers = new ArrayList();
            Iterator it = messages.iterator();
            while (it.hasNext()) {
                MessageObject message = (MessageObject) it.next();
                if (!messagesUsers.contains(Integer.valueOf(message.messageOwner.from_id))) {
                    messagesUsers.add(Integer.valueOf(message.messageOwner.from_id));
                }
            }
            ArrayList<PrintingUser> arr = (ArrayList) this.printingUsers.get(Long.valueOf(uid));
            boolean changed = false;
            if (arr != null) {
                int a = 0;
                while (a < arr.size()) {
                    if (messagesUsers.contains(Integer.valueOf(((PrintingUser) arr.get(a)).userId))) {
                        arr.remove(a);
                        a--;
                        if (arr.isEmpty()) {
                            this.printingUsers.remove(Long.valueOf(uid));
                        }
                        changed = true;
                    }
                    a++;
                }
            }
            if (changed) {
                return true;
            }
        }
        return false;
    }

    /* Access modifiers changed, original: protected */
    public void updateInterfaceWithMessages(long uid, ArrayList<MessageObject> messages) {
        updateInterfaceWithMessages(uid, messages, false);
    }

    /* Access modifiers changed, original: protected */
    public void updateInterfaceWithMessages(long uid, ArrayList<MessageObject> messages, boolean isBroadcast) {
        if (messages != null && !messages.isEmpty()) {
            boolean isEncryptedChat = ((int) uid) == 0;
            MessageObject lastMessage = null;
            int channelId = 0;
            boolean updateRating = false;
            for (int a = 0; a < messages.size(); a++) {
                MessageObject message = (MessageObject) messages.get(a);
                if (lastMessage == null || ((!isEncryptedChat && message.getId() > lastMessage.getId()) || (((isEncryptedChat || (message.getId() < 0 && lastMessage.getId() < 0)) && message.getId() < lastMessage.getId()) || message.messageOwner.date > lastMessage.messageOwner.date))) {
                    lastMessage = message;
                    if (message.messageOwner.to_id.channel_id != 0) {
                        channelId = message.messageOwner.to_id.channel_id;
                    }
                }
                if (!(!message.isOut() || message.isSending() || message.isForwarded())) {
                    if (message.isNewGif()) {
                        DataQuery.getInstance(this.currentAccount).addRecentGif(message.messageOwner.media.document, message.messageOwner.date);
                    } else if (message.isSticker()) {
                        DataQuery.getInstance(this.currentAccount).addRecentSticker(0, message, message.messageOwner.media.document, message.messageOwner.date, false);
                    }
                }
                if (message.isOut() && message.isSent()) {
                    updateRating = true;
                }
            }
            DataQuery.getInstance(this.currentAccount).loadReplyMessagesForMessages(messages, uid);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didReceiveNewMessages, Long.valueOf(uid), messages);
            if (lastMessage != null) {
                TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(uid);
                MessageObject object;
                if (!(lastMessage.messageOwner.action instanceof TL_messageActionChatMigrateTo)) {
                    boolean changed = false;
                    if (dialog == null) {
                        if (!isBroadcast) {
                            Chat chat = getChat(Integer.valueOf(channelId));
                            if (channelId != 0 && chat == null) {
                                return;
                            }
                            if (chat == null || !chat.left) {
                                dialog = new TL_dialog();
                                dialog.id = uid;
                                dialog.unread_count = 0;
                                dialog.top_message = lastMessage.getId();
                                dialog.last_message_date = lastMessage.messageOwner.date;
                                dialog.flags = ChatObject.isChannel(chat) ? 1 : 0;
                                this.dialogs_dict.put(uid, dialog);
                                this.dialogs.add(dialog);
                                this.dialogMessage.put(uid, lastMessage);
                                if (lastMessage.messageOwner.to_id.channel_id == 0) {
                                    this.dialogMessagesByIds.put(lastMessage.getId(), lastMessage);
                                    if (lastMessage.messageOwner.random_id != 0) {
                                        this.dialogMessagesByRandomIds.put(lastMessage.messageOwner.random_id, lastMessage);
                                    }
                                }
                                this.nextDialogsCacheOffset++;
                                changed = true;
                            } else {
                                return;
                            }
                        }
                    } else if ((dialog.top_message > 0 && lastMessage.getId() > 0 && lastMessage.getId() > dialog.top_message) || ((dialog.top_message < 0 && lastMessage.getId() < 0 && lastMessage.getId() < dialog.top_message) || this.dialogMessage.indexOfKey(uid) < 0 || dialog.top_message < 0 || dialog.last_message_date <= lastMessage.messageOwner.date)) {
                        object = (MessageObject) this.dialogMessagesByIds.get(dialog.top_message);
                        this.dialogMessagesByIds.remove(dialog.top_message);
                        if (!(object == null || object.messageOwner.random_id == 0)) {
                            this.dialogMessagesByRandomIds.remove(object.messageOwner.random_id);
                        }
                        dialog.top_message = lastMessage.getId();
                        if (!isBroadcast) {
                            dialog.last_message_date = lastMessage.messageOwner.date;
                            changed = true;
                        }
                        this.dialogMessage.put(uid, lastMessage);
                        if (lastMessage.messageOwner.to_id.channel_id == 0) {
                            this.dialogMessagesByIds.put(lastMessage.getId(), lastMessage);
                            if (lastMessage.messageOwner.random_id != 0) {
                                this.dialogMessagesByRandomIds.put(lastMessage.messageOwner.random_id, lastMessage);
                            }
                        }
                    }
                    if (changed) {
                        sortDialogs(null);
                    }
                    if (updateRating) {
                        DataQuery.getInstance(this.currentAccount).increasePeerRaiting(uid);
                    }
                } else if (dialog != null) {
                    this.dialogs.remove(dialog);
                    this.dialogsServerOnly.remove(dialog);
                    this.dialogsCanAddUsers.remove(dialog);
                    this.dialogsChannelsOnly.remove(dialog);
                    this.dialogsGroupsOnly.remove(dialog);
                    this.dialogsUsersOnly.remove(dialog);
                    this.dialogsForward.remove(dialog);
                    this.dialogs_dict.remove(dialog.id);
                    this.dialogs_read_inbox_max.remove(Long.valueOf(dialog.id));
                    this.dialogs_read_outbox_max.remove(Long.valueOf(dialog.id));
                    this.nextDialogsCacheOffset--;
                    this.dialogMessage.remove(dialog.id);
                    object = (MessageObject) this.dialogMessagesByIds.get(dialog.top_message);
                    this.dialogMessagesByIds.remove(dialog.top_message);
                    if (!(object == null || object.messageOwner.random_id == 0)) {
                        this.dialogMessagesByRandomIds.remove(object.messageOwner.random_id);
                    }
                    dialog.top_message = 0;
                    NotificationsController.getInstance(this.currentAccount).removeNotificationsForDialog(dialog.id);
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                }
            }
        }
    }

    public void addDialogAction(long did, boolean clean) {
        TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(did);
        if (dialog != null) {
            if (clean) {
                this.clearingHistoryDialogs.put(did, dialog);
            } else {
                this.deletingDialogs.put(did, dialog);
                this.dialogs.remove(dialog);
                sortDialogs(null);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
        }
    }

    public void removeDialogAction(long did, boolean clean, boolean apply) {
        TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(did);
        if (dialog != null) {
            if (clean) {
                this.clearingHistoryDialogs.remove(did);
            } else {
                this.deletingDialogs.remove(did);
                if (!apply) {
                    this.dialogs.add(dialog);
                    sortDialogs(null);
                }
            }
            if (!apply) {
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, Boolean.valueOf(true));
            }
        }
    }

    public boolean isClearingDialog(long did) {
        return this.clearingHistoryDialogs.get(did) != null;
    }

    public void sortDialogs(SparseArray<Chat> chatsDict) {
        Chat chat;
        this.dialogsServerOnly.clear();
        this.dialogsCanAddUsers.clear();
        this.dialogsChannelsOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsUsersOnly.clear();
        this.dialogsForward.clear();
        this.unreadUnmutedDialogs = 0;
        boolean selfAdded = false;
        int selfId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        Collections.sort(this.dialogs, this.dialogComparator);
        this.isLeftProxyChannel = true;
        if (this.proxyDialog != null && this.proxyDialog.id < 0) {
            chat = getChat(Integer.valueOf(-((int) this.proxyDialog.id)));
            if (!(chat == null || chat.left)) {
                this.isLeftProxyChannel = false;
            }
        }
        int a = 0;
        while (a < this.dialogs.size()) {
            TL_dialog d = (TL_dialog) this.dialogs.get(a);
            int high_id = (int) (d.id >> 32);
            int lower_id = (int) d.id;
            if (lower_id == selfId) {
                this.dialogsForward.add(0, d);
                selfAdded = true;
            } else {
                this.dialogsForward.add(d);
            }
            if (!(lower_id == 0 || high_id == 1)) {
                this.dialogsServerOnly.add(d);
                if (DialogObject.isChannel(d)) {
                    chat = getChat(Integer.valueOf(-lower_id));
                    if (chat != null && ((chat.megagroup && chat.admin_rights != null && (chat.admin_rights.post_messages || chat.admin_rights.add_admins)) || chat.creator)) {
                        this.dialogsCanAddUsers.add(d);
                    }
                    if (chat == null || !chat.megagroup) {
                        this.dialogsChannelsOnly.add(d);
                    } else {
                        this.dialogsGroupsOnly.add(d);
                    }
                } else if (lower_id < 0) {
                    if (chatsDict != null) {
                        chat = (Chat) chatsDict.get(-lower_id);
                        if (!(chat == null || chat.migrated_to == null)) {
                            this.dialogs.remove(a);
                            a--;
                            a++;
                        }
                    }
                    this.dialogsCanAddUsers.add(d);
                    this.dialogsGroupsOnly.add(d);
                } else if (lower_id > 0) {
                    this.dialogsUsersOnly.add(d);
                }
            }
            if (this.proxyDialog != null && d.id == this.proxyDialog.id && this.isLeftProxyChannel) {
                this.dialogs.remove(a);
                a--;
            }
            if ((d.unread_count != 0 || d.unread_mark) && !isDialogMuted(d.id)) {
                this.unreadUnmutedDialogs++;
            }
            a++;
        }
        if (this.proxyDialog != null && this.isLeftProxyChannel) {
            this.dialogs.add(0, this.proxyDialog);
        }
        if (!selfAdded) {
            User user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
            if (user != null) {
                TL_dialog dialog = new TL_dialog();
                dialog.id = (long) user.id;
                dialog.notify_settings = new TL_peerNotifySettings();
                dialog.peer = new TL_peerUser();
                dialog.peer.user_id = user.id;
                this.dialogsForward.add(0, dialog);
            }
        }
    }

    private static String getRestrictionReason(String reason) {
        if (reason == null || reason.length() == 0) {
            return null;
        }
        int index = reason.indexOf(": ");
        if (index <= 0) {
            return null;
        }
        String type = reason.substring(0, index);
        if (type.contains("-all") || type.contains("-android")) {
            return reason.substring(index + 2);
        }
        return null;
    }

    private static void showCantOpenAlert(BaseFragment fragment, String reason) {
        if (fragment != null && fragment.getParentActivity() != null) {
            Builder builder = new Builder(fragment.getParentActivity());
            builder.setTitle(LocaleController.getString("AppName", NUM));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), null);
            builder.setMessage(reason);
            fragment.showDialog(builder.create());
        }
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment fragment) {
        return checkCanOpenChat(bundle, fragment, null);
    }

    public boolean checkCanOpenChat(Bundle bundle, BaseFragment fragment, MessageObject originalMessage) {
        if (bundle == null || fragment == null) {
            return true;
        }
        User user = null;
        Chat chat = null;
        int user_id = bundle.getInt("user_id", 0);
        int chat_id = bundle.getInt("chat_id", 0);
        int messageId = bundle.getInt("message_id", 0);
        if (user_id != 0) {
            user = getUser(Integer.valueOf(user_id));
        } else if (chat_id != 0) {
            chat = getChat(Integer.valueOf(chat_id));
        }
        if (user == null && chat == null) {
            return true;
        }
        String reason = null;
        if (chat != null) {
            reason = getRestrictionReason(chat.restriction_reason);
        } else if (user != null) {
            reason = getRestrictionReason(user.restriction_reason);
        }
        if (reason != null) {
            showCantOpenAlert(fragment, reason);
            return false;
        }
        if (!(messageId == 0 || originalMessage == null || chat == null || chat.access_hash != 0)) {
            int did = (int) originalMessage.getDialogId();
            if (did != 0) {
                TLObject req;
                AlertDialog progressDialog = new AlertDialog(fragment.getParentActivity(), 3);
                if (did < 0) {
                    chat = getChat(Integer.valueOf(-did));
                }
                TLObject request;
                if (did > 0 || !ChatObject.isChannel(chat)) {
                    request = new TL_messages_getMessages();
                    request.id.add(Integer.valueOf(originalMessage.getId()));
                    req = request;
                } else {
                    chat = getChat(Integer.valueOf(-did));
                    request = new TL_channels_getMessages();
                    request.channel = getInputChannel(chat);
                    request.id.add(Integer.valueOf(originalMessage.getId()));
                    req = request;
                }
                progressDialog.setOnCancelListener(new MessagesController$$Lambda$150(this, ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$149(this, progressDialog, fragment, bundle)), fragment));
                fragment.setVisibleDialog(progressDialog);
                progressDialog.show();
                return false;
            }
        }
        return true;
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkCanOpenChat$253$MessagesController(AlertDialog progressDialog, BaseFragment fragment, Bundle bundle, TLObject response, TL_error error) {
        if (response != null) {
            AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$155(this, progressDialog, response, fragment, bundle));
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$252$MessagesController(AlertDialog progressDialog, TLObject response, BaseFragment fragment, Bundle bundle) {
        try {
            progressDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        messages_Messages res = (messages_Messages) response;
        putUsers(res.users, false);
        putChats(res.chats, false);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
        fragment.presentFragment(new ChatActivity(bundle), true);
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$checkCanOpenChat$254$MessagesController(int reqId, BaseFragment fragment, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
        if (fragment != null) {
            fragment.setVisibleDialog(null);
        }
    }

    public static void openChatOrProfileWith(User user, Chat chat, BaseFragment fragment, int type, boolean closeLast) {
        if ((user != null || chat != null) && fragment != null) {
            String reason = null;
            if (chat != null) {
                reason = getRestrictionReason(chat.restriction_reason);
            } else if (user != null) {
                reason = getRestrictionReason(user.restriction_reason);
                if (user.bot) {
                    type = 1;
                    closeLast = true;
                }
            }
            if (reason != null) {
                showCantOpenAlert(fragment, reason);
                return;
            }
            Bundle args = new Bundle();
            if (chat != null) {
                args.putInt("chat_id", chat.id);
            } else {
                args.putInt("user_id", user.id);
            }
            if (type == 0) {
                fragment.presentFragment(new ProfileActivity(args));
            } else if (type == 2) {
                fragment.presentFragment(new ChatActivity(args), true, true);
            } else {
                fragment.presentFragment(new ChatActivity(args), closeLast);
            }
        }
    }

    public void openByUserName(String username, BaseFragment fragment, int type) {
        if (username != null && fragment != null) {
            TLObject object = getUserOrChat(username);
            User user = null;
            Chat chat = null;
            if (object instanceof User) {
                user = (User) object;
                if (user.min) {
                    user = null;
                }
            } else if (object instanceof Chat) {
                chat = (Chat) object;
                if (chat.min) {
                    chat = null;
                }
            }
            if (user != null) {
                openChatOrProfileWith(user, null, fragment, type, false);
            } else if (chat != null) {
                openChatOrProfileWith(null, chat, fragment, 1, false);
            } else if (fragment.getParentActivity() != null) {
                AlertDialog[] progressDialog = new AlertDialog[]{new AlertDialog(fragment.getParentActivity(), 3)};
                TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
                req.username = username;
                AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$152(this, progressDialog, ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new MessagesController$$Lambda$151(this, progressDialog, fragment, type)), fragment), 500);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$openByUserName$256$MessagesController(AlertDialog[] progressDialog, BaseFragment fragment, int type, TLObject response, TL_error error) {
        AndroidUtilities.runOnUIThread(new MessagesController$$Lambda$154(this, progressDialog, fragment, error, response, type));
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$255$MessagesController(AlertDialog[] progressDialog, BaseFragment fragment, TL_error error, TLObject response, int type) {
        try {
            progressDialog[0].dismiss();
        } catch (Exception e) {
        }
        progressDialog[0] = null;
        fragment.setVisibleDialog(null);
        if (error == null) {
            TL_contacts_resolvedPeer res = (TL_contacts_resolvedPeer) response;
            putUsers(res.users, false);
            putChats(res.chats, false);
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, res.chats, false, true);
            if (!res.chats.isEmpty()) {
                openChatOrProfileWith(null, (Chat) res.chats.get(0), fragment, 1, false);
            } else if (!res.users.isEmpty()) {
                openChatOrProfileWith((User) res.users.get(0), null, fragment, type, false);
            }
        } else if (fragment != null && fragment.getParentActivity() != null) {
            try {
                Toast.makeText(fragment.getParentActivity(), LocaleController.getString("NoUsernameFound", NUM), 0).show();
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$openByUserName$258$MessagesController(AlertDialog[] progressDialog, int reqId, BaseFragment fragment) {
        if (progressDialog[0] != null) {
            progressDialog[0].setOnCancelListener(new MessagesController$$Lambda$153(this, reqId));
            fragment.showDialog(progressDialog[0]);
        }
    }

    /* Access modifiers changed, original: final|synthetic */
    public final /* synthetic */ void lambda$null$257$MessagesController(int reqId, DialogInterface dialog) {
        ConnectionsManager.getInstance(this.currentAccount).cancelRequest(reqId, true);
    }
}
