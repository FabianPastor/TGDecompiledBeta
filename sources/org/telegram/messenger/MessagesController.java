package org.telegram.messenger;

import android.app.Activity;
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
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.trackselection.AdaptiveTrackSelection;
import org.telegram.messenger.support.SparseLongArray;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;
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
import org.telegram.tgnet.TLRPC.InputChannel;
import org.telegram.tgnet.TLRPC.InputFile;
import org.telegram.tgnet.TLRPC.InputPeer;
import org.telegram.tgnet.TLRPC.InputPhoto;
import org.telegram.tgnet.TLRPC.InputUser;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageEntity;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.PeerNotifySettings;
import org.telegram.tgnet.TLRPC.PhoneCall;
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
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.ProfileActivity;

public class MessagesController implements NotificationCenterDelegate {
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
    public int callConnectTimeout = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
    public int callPacketTimeout = 10000;
    public int callReceiveTimeout = 20000;
    public int callRingTimeout = 90000;
    public boolean canRevokePmInbox;
    private SparseArray<ArrayList<Integer>> channelAdmins = new SparseArray();
    private SparseArray<ArrayList<Integer>> channelViewsToSend = new SparseArray();
    private SparseIntArray channelsPts = new SparseIntArray();
    private ConcurrentHashMap<Integer, Chat> chats = new ConcurrentHashMap(100, 1.0f, 2);
    private SparseBooleanArray checkingLastMessagesDialogs = new SparseBooleanArray();
    private ArrayList<Long> createdDialogIds = new ArrayList();
    private ArrayList<Long> createdDialogMainThreadIds = new ArrayList();
    private int currentAccount;
    private Runnable currentDeleteTaskRunnable;
    private int currentDeletingTaskChannelId;
    private ArrayList<Integer> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    public boolean defaultP2pContacts = false;
    private final Comparator<TL_dialog> dialogComparator = new C03272();
    public LongSparseArray<MessageObject> dialogMessage = new LongSparseArray();
    public SparseArray<MessageObject> dialogMessagesByIds = new SparseArray();
    public LongSparseArray<MessageObject> dialogMessagesByRandomIds = new LongSparseArray();
    public ArrayList<TL_dialog> dialogs = new ArrayList();
    public boolean dialogsEndReached;
    public ArrayList<TL_dialog> dialogsForward = new ArrayList();
    public ArrayList<TL_dialog> dialogsGroupsOnly = new ArrayList();
    public ArrayList<TL_dialog> dialogsServerOnly = new ArrayList();
    public LongSparseArray<TL_dialog> dialogs_dict = new LongSparseArray();
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max = new ConcurrentHashMap(100, 1.0f, 2);
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max = new ConcurrentHashMap(100, 1.0f, 2);
    private SharedPreferences emojiPreferences;
    public boolean enableJoined = true;
    private ConcurrentHashMap<Integer, EncryptedChat> encryptedChats = new ConcurrentHashMap(10, 1.0f, 2);
    private SparseArray<ExportedChatInvite> exportedChats = new SparseArray();
    public boolean firstGettingTask;
    private SparseArray<TL_userFull> fullUsers = new SparseArray();
    private boolean getDifferenceFirstSync = true;
    public boolean gettingDifference;
    private SparseBooleanArray gettingDifferenceChannels = new SparseBooleanArray();
    private boolean gettingNewDeleteTask;
    private SparseBooleanArray gettingUnknownChannels = new SparseBooleanArray();
    public ArrayList<RecentMeUrl> hintDialogs = new ArrayList();
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
    public int maxGroupCount = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    public int maxMegagroupCount = 10000;
    public int maxPinnedDialogsCount = 5;
    public int maxRecentGifsCount = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    public int maxRecentStickersCount = 30;
    private boolean migratingDialogs;
    public int minGroupConvertSize = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
    private SparseIntArray needShortPollChannels = new SparseIntArray();
    public int nextDialogsCacheOffset;
    private SharedPreferences notificationsPreferences;
    private ConcurrentHashMap<String, TLObject> objectsByUsernames = new ConcurrentHashMap(100, 1.0f, 2);
    private boolean offlineSent;
    public ConcurrentHashMap<Integer, Integer> onlinePrivacy = new ConcurrentHashMap(20, 1.0f, 2);
    public boolean preloadFeaturedStickers;
    public LongSparseArray<CharSequence> printingStrings = new LongSparseArray();
    public LongSparseArray<Integer> printingStringsTypes = new LongSparseArray();
    public ConcurrentHashMap<Long, ArrayList<PrintingUser>> printingUsers = new ConcurrentHashMap(20, 1.0f, 2);
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
    public int revokeTimeLimit = 172800;
    public int revokeTimePmLimit = 172800;
    public int secretWebpagePreview = 2;
    public SparseArray<LongSparseArray<Boolean>> sendingTypings = new SparseArray();
    public boolean serverDialogsEndReached;
    private SparseIntArray shortPollChannels = new SparseIntArray();
    private int statusRequest;
    private int statusSettingState;
    private Runnable themeCheckRunnable = new C03221();
    private final Comparator<Update> updatesComparator = new C03333();
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
    private ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap(100, 1.0f, 2);

    /* renamed from: org.telegram.messenger.MessagesController$1 */
    class C03221 implements Runnable {
        C03221() {
        }

        public void run() {
            Theme.checkAutoNightThemeConditions();
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController$2 */
    class C03272 implements Comparator<TL_dialog> {
        C03272() {
        }

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
    }

    /* renamed from: org.telegram.messenger.MessagesController$3 */
    class C03333 implements Comparator<Update> {
        C03333() {
        }

        public int compare(Update lhs, Update rhs) {
            int ltype = MessagesController.this.getUpdateType(lhs);
            int rtype = MessagesController.this.getUpdateType(rhs);
            if (ltype != rtype) {
                return AndroidUtilities.compare(ltype, rtype);
            }
            if (ltype == 0) {
                return AndroidUtilities.compare(MessagesController.getUpdatePts(lhs), MessagesController.getUpdatePts(rhs));
            }
            if (ltype == 1) {
                return AndroidUtilities.compare(MessagesController.getUpdateQts(lhs), MessagesController.getUpdateQts(rhs));
            }
            if (ltype != 2) {
                return 0;
            }
            int lChannel = MessagesController.getUpdateChannelId(lhs);
            int rChannel = MessagesController.getUpdateChannelId(rhs);
            if (lChannel == rChannel) {
                return AndroidUtilities.compare(MessagesController.getUpdatePts(lhs), MessagesController.getUpdatePts(rhs));
            }
            return AndroidUtilities.compare(lChannel, rChannel);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController$4 */
    class C03364 implements Runnable {
        C03364() {
        }

        public void run() {
            MessagesController messagesController = MessagesController.getInstance(MessagesController.this.currentAccount);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.FileDidUpload);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.FileDidFailUpload);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.FileDidLoaded);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.FileDidFailedLoad);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.messageReceivedByServer);
            NotificationCenter.getInstance(MessagesController.this.currentAccount).addObserver(messagesController, NotificationCenter.updateMessageMedia);
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController$7 */
    class C03567 implements Runnable {
        C03567() {
        }

        public void run() {
            MessagesController.this.readTasks.clear();
            MessagesController.this.readTasksMap.clear();
            MessagesController.this.updatesQueueSeq.clear();
            MessagesController.this.updatesQueuePts.clear();
            MessagesController.this.updatesQueueQts.clear();
            MessagesController.this.gettingUnknownChannels.clear();
            MessagesController.this.updatesStartWaitTimeSeq = 0;
            MessagesController.this.updatesStartWaitTimePts = 0;
            MessagesController.this.updatesStartWaitTimeQts = 0;
            MessagesController.this.createdDialogIds.clear();
            MessagesController.this.gettingDifference = false;
            MessagesController.this.resetDialogsPinned = null;
            MessagesController.this.resetDialogsAll = null;
        }
    }

    /* renamed from: org.telegram.messenger.MessagesController$8 */
    class C03658 implements Runnable {
        C03658() {
        }

        public void run() {
            ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
            MessagesController.this.updatesQueueChannels.clear();
            MessagesController.this.updatesStartWaitTimeChannels.clear();
            MessagesController.this.gettingDifferenceChannels.clear();
            MessagesController.this.channelsPts.clear();
            MessagesController.this.shortPollChannels.clear();
            MessagesController.this.needShortPollChannels.clear();
        }
    }

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
    }

    /* renamed from: org.telegram.messenger.MessagesController$6 */
    class C18176 implements RequestDelegate {

        /* renamed from: org.telegram.messenger.MessagesController$6$1 */
        class C03411 implements Runnable {
            C03411() {
            }

            public void run() {
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(2));
                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(true);
            }
        }

        C18176() {
        }

        public void run(TLObject response, TL_error error) {
            if (error == null) {
                User user = MessagesController.this.getUser(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
                if (user == null) {
                    user = UserConfig.getInstance(MessagesController.this.currentAccount).getCurrentUser();
                    MessagesController.this.putUser(user, true);
                } else {
                    UserConfig.getInstance(MessagesController.this.currentAccount).setCurrentUser(user);
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
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).clearUserPhotos(user.id);
                    ArrayList<User> users = new ArrayList();
                    users.add(user);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(users, null, false, true);
                    AndroidUtilities.runOnUIThread(new C03411());
                }
            }
        }
    }

    private class UserActionUpdatesPts extends Updates {
        private UserActionUpdatesPts() {
        }
    }

    private class UserActionUpdatesSeq extends Updates {
        private UserActionUpdatesSeq() {
        }
    }

    public static MessagesController getInstance(int num) {
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
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            localInstance = localInstance2;
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                    th2 = th3;
                    throw th2;
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
        AndroidUtilities.runOnUIThread(new C03364());
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
        this.maxGroupCount = this.mainPreferences.getInt("maxGroupCount", Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        this.maxMegagroupCount = this.mainPreferences.getInt("maxMegagroupCount", 10000);
        this.maxRecentGifsCount = this.mainPreferences.getInt("maxRecentGifsCount", Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        this.maxRecentStickersCount = this.mainPreferences.getInt("maxRecentStickersCount", 30);
        this.maxFaveStickersCount = this.mainPreferences.getInt("maxFaveStickersCount", 5);
        this.maxEditTime = this.mainPreferences.getInt("maxEditTime", 3600);
        this.ratingDecay = this.mainPreferences.getInt("ratingDecay", 2419200);
        this.linkPrefix = this.mainPreferences.getString("linkPrefix", "t.me");
        this.callReceiveTimeout = this.mainPreferences.getInt("callReceiveTimeout", 20000);
        this.callRingTimeout = this.mainPreferences.getInt("callRingTimeout", 90000);
        this.callConnectTimeout = this.mainPreferences.getInt("callConnectTimeout", DefaultLoadControl.DEFAULT_MAX_BUFFER_MS);
        this.callPacketTimeout = this.mainPreferences.getInt("callPacketTimeout", 10000);
        this.maxPinnedDialogsCount = this.mainPreferences.getInt("maxPinnedDialogsCount", 5);
        this.installReferer = this.mainPreferences.getString("installReferer", null);
        this.defaultP2pContacts = this.mainPreferences.getBoolean("defaultP2pContacts", false);
        this.revokeTimeLimit = this.mainPreferences.getInt("revokeTimeLimit", this.revokeTimeLimit);
        this.revokeTimePmLimit = this.mainPreferences.getInt("revokeTimePmLimit", this.revokeTimePmLimit);
        this.canRevokePmInbox = this.mainPreferences.getBoolean("canRevokePmInbox", this.canRevokePmInbox);
        this.preloadFeaturedStickers = this.mainPreferences.getBoolean("preloadFeaturedStickers", false);
    }

    public void updateConfig(final TL_config config) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                LocaleController.getInstance().loadRemoteLanguages(MessagesController.this.currentAccount);
                MessagesController.this.maxMegagroupCount = config.megagroup_size_max;
                MessagesController.this.maxGroupCount = config.chat_size_max;
                MessagesController.this.maxEditTime = config.edit_time_limit;
                MessagesController.this.ratingDecay = config.rating_e_decay;
                MessagesController.this.maxRecentGifsCount = config.saved_gifs_limit;
                MessagesController.this.maxRecentStickersCount = config.stickers_recent_limit;
                MessagesController.this.maxFaveStickersCount = config.stickers_faved_limit;
                MessagesController.this.revokeTimeLimit = config.revoke_time_limit;
                MessagesController.this.revokeTimePmLimit = config.revoke_pm_time_limit;
                MessagesController.this.canRevokePmInbox = config.revoke_pm_inbox;
                MessagesController.this.linkPrefix = config.me_url_prefix;
                if (MessagesController.this.linkPrefix.endsWith("/")) {
                    MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(0, MessagesController.this.linkPrefix.length() - 1);
                }
                if (MessagesController.this.linkPrefix.startsWith("https://")) {
                    MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(8);
                } else if (MessagesController.this.linkPrefix.startsWith("http://")) {
                    MessagesController.this.linkPrefix = MessagesController.this.linkPrefix.substring(7);
                }
                MessagesController.this.callReceiveTimeout = config.call_receive_timeout_ms;
                MessagesController.this.callRingTimeout = config.call_ring_timeout_ms;
                MessagesController.this.callConnectTimeout = config.call_connect_timeout_ms;
                MessagesController.this.callPacketTimeout = config.call_packet_timeout_ms;
                MessagesController.this.maxPinnedDialogsCount = config.pinned_dialogs_count_max;
                MessagesController.this.defaultP2pContacts = config.default_p2p_contacts;
                MessagesController.this.preloadFeaturedStickers = config.preload_featured_stickers;
                Editor editor = MessagesController.this.mainPreferences.edit();
                editor.putInt("maxGroupCount", MessagesController.this.maxGroupCount);
                editor.putInt("maxMegagroupCount", MessagesController.this.maxMegagroupCount);
                editor.putInt("maxEditTime", MessagesController.this.maxEditTime);
                editor.putInt("ratingDecay", MessagesController.this.ratingDecay);
                editor.putInt("maxRecentGifsCount", MessagesController.this.maxRecentGifsCount);
                editor.putInt("maxRecentStickersCount", MessagesController.this.maxRecentStickersCount);
                editor.putInt("maxFaveStickersCount", MessagesController.this.maxFaveStickersCount);
                editor.putInt("callReceiveTimeout", MessagesController.this.callReceiveTimeout);
                editor.putInt("callRingTimeout", MessagesController.this.callRingTimeout);
                editor.putInt("callConnectTimeout", MessagesController.this.callConnectTimeout);
                editor.putInt("callPacketTimeout", MessagesController.this.callPacketTimeout);
                editor.putString("linkPrefix", MessagesController.this.linkPrefix);
                editor.putInt("maxPinnedDialogsCount", MessagesController.this.maxPinnedDialogsCount);
                editor.putBoolean("defaultP2pContacts", MessagesController.this.defaultP2pContacts);
                editor.putBoolean("preloadFeaturedStickers", MessagesController.this.preloadFeaturedStickers);
                editor.putInt("revokeTimeLimit", MessagesController.this.revokeTimeLimit);
                editor.putInt("revokeTimePmLimit", MessagesController.this.revokeTimePmLimit);
                editor.putBoolean("canRevokePmInbox", MessagesController.this.canRevokePmInbox);
                editor.commit();
                LocaleController.getInstance().checkUpdateForCurrentRemoteLocale(MessagesController.this.currentAccount, config.lang_pack_version);
            }
        });
    }

    public void addSupportUser() {
        TL_userForeign_old2 user = new TL_userForeign_old2();
        user.phone = "333";
        user.id = 333000;
        user.first_name = "Telegram";
        user.last_name = TtmlNode.ANONYMOUS_REGION_ID;
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new C18176());
            }
        } else if (id == NotificationCenter.FileDidFailUpload) {
            location = (String) args[0];
            if (this.uploadingAvatar != null && this.uploadingAvatar.equals(location)) {
                this.uploadingAvatar = null;
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

    public void cleanup() {
        ContactsController.getInstance(this.currentAccount).cleanup();
        MediaController.getInstance().cleanup();
        NotificationsController.getInstance(this.currentAccount).cleanup();
        SendMessagesHelper.getInstance(this.currentAccount).cleanup();
        SecretChatHelper.getInstance(this.currentAccount).cleanup();
        LocationController.getInstance(this.currentAccount).cleanup();
        DataQuery.getInstance(this.currentAccount).cleanup();
        DialogsActivity.dialogsLoaded[this.currentAccount] = false;
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
        Utilities.stageQueue.postRunnable(new C03567());
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
        this.lastStatusUpdateTime = 0;
        this.offlineSent = false;
        this.registeringForPush = false;
        this.getDifferenceFirstSync = true;
        this.uploadingAvatar = null;
        this.statusRequest = 0;
        this.statusSettingState = 0;
        Utilities.stageQueue.postRunnable(new C03658());
        if (this.currentDeleteTaskRunnable != null) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
            this.currentDeleteTaskRunnable = null;
        }
        addSupportUser();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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
        } catch (Throwable e) {
            FileLog.m3e(e);
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

    public void setLastCreatedDialogId(final long dialog_id, final boolean set) {
        if (set) {
            this.createdDialogMainThreadIds.add(Long.valueOf(dialog_id));
        } else {
            this.createdDialogMainThreadIds.remove(Long.valueOf(dialog_id));
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (set) {
                    MessagesController.this.createdDialogIds.add(Long.valueOf(dialog_id));
                } else {
                    MessagesController.this.createdDialogIds.remove(Long.valueOf(dialog_id));
                }
            }
        });
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
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
                    }
                });
            }
        }
    }

    public void putChat(final Chat chat, boolean fromCache) {
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
                        oldChat.democracy = chat.democracy;
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
                        if (oldFlags != newFlags) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.channelRightsUpdated, chat);
                                }
                            });
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
                    chat.democracy = oldChat.democracy;
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

    protected void clearFullUsers() {
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
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
                                Integer value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                                if (value == null) {
                                    value = Integer.valueOf(0);
                                }
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_inbox_max_id, value.intValue())));
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
                                value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                                if (value == null) {
                                    value = Integer.valueOf(0);
                                }
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_outbox_max_id, value.intValue())));
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
                                MessagesController.this.processUpdateArray(arrayList, null, null, false);
                            }
                        }
                    }
                });
            }
        }
    }

    public boolean isChannelAdmin(int chatId, int uid) {
        ArrayList<Integer> array = (ArrayList) this.channelAdmins.get(chatId);
        return array != null && array.indexOf(Integer.valueOf(uid)) >= 0;
    }

    public void loadChannelAdmins(final int chatId, boolean cache) {
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (response instanceof TL_channels_channelParticipants) {
                        TL_channels_channelParticipants participants = (TL_channels_channelParticipants) response;
                        ArrayList<Integer> array = new ArrayList(participants.participants.size());
                        for (int a = 0; a < participants.participants.size(); a++) {
                            array.add(Integer.valueOf(((ChannelParticipant) participants.participants.get(a)).user_id));
                        }
                        MessagesController.this.processLoadedChannelAdmins(array, chatId, false);
                    }
                }
            });
        }
    }

    public void processLoadedChannelAdmins(final ArrayList<Integer> array, final int chatId, final boolean cache) {
        Collections.sort(array);
        if (!cache) {
            MessagesStorage.getInstance(this.currentAccount).putChannelAdmins(chatId, array);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.this.loadingChannelAdmins.delete(chatId);
                MessagesController.this.channelAdmins.put(chatId, array);
                if (cache) {
                    MessagesController.this.loadChannelAdmins(chatId, false);
                }
            }
        });
    }

    public void loadFullChat(int chat_id, int classGuid, boolean force) {
        boolean loaded = this.loadedFullChats.contains(Integer.valueOf(chat_id));
        if (!this.loadingFullChats.contains(Integer.valueOf(chat_id))) {
            if (force || !loaded) {
                TLObject request;
                this.loadingFullChats.add(Integer.valueOf(chat_id));
                final long dialog_id = (long) (-chat_id);
                final Chat chat = getChat(Integer.valueOf(chat_id));
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
                final int i = chat_id;
                final int i2 = classGuid;
                int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() {
                    public void run(TLObject response, final TL_error error) {
                        if (error == null) {
                            final TL_messages_chatFull res = (TL_messages_chatFull) response;
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatInfo(res.full_chat, false);
                            if (ChatObject.isChannel(chat)) {
                                ArrayList<Update> arrayList;
                                Integer value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                if (value == null) {
                                    value = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, dialog_id));
                                }
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(res.full_chat.read_inbox_max_id, value.intValue())));
                                if (value.intValue() == 0) {
                                    arrayList = new ArrayList();
                                    TL_updateReadChannelInbox update = new TL_updateReadChannelInbox();
                                    update.channel_id = i;
                                    update.max_id = res.full_chat.read_inbox_max_id;
                                    arrayList.add(update);
                                    MessagesController.this.processUpdateArray(arrayList, null, null, false);
                                }
                                value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                                if (value == null) {
                                    value = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, dialog_id));
                                }
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(res.full_chat.read_outbox_max_id, value.intValue())));
                                if (value.intValue() == 0) {
                                    arrayList = new ArrayList();
                                    TL_updateReadChannelOutbox update2 = new TL_updateReadChannelOutbox();
                                    update2.channel_id = i;
                                    update2.max_id = res.full_chat.read_outbox_max_id;
                                    arrayList.add(update2);
                                    MessagesController.this.processUpdateArray(arrayList, null, null, false);
                                }
                            }
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.this.applyDialogNotificationsSettings((long) (-i), res.full_chat.notify_settings);
                                    for (int a = 0; a < res.full_chat.bot_info.size(); a++) {
                                        DataQuery.getInstance(MessagesController.this.currentAccount).putBotInfo((BotInfo) res.full_chat.bot_info.get(a));
                                    }
                                    MessagesController.this.exportedChats.put(i, res.full_chat.exported_invite);
                                    MessagesController.this.loadingFullChats.remove(Integer.valueOf(i));
                                    MessagesController.this.loadedFullChats.add(Integer.valueOf(i));
                                    MessagesController.this.putUsers(res.users, false);
                                    MessagesController.this.putChats(res.chats, false);
                                    if (res.full_chat.stickerset != null) {
                                        DataQuery.getInstance(MessagesController.this.currentAccount).getGroupStickerSetById(res.full_chat.stickerset);
                                    }
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, res.full_chat, Integer.valueOf(i2), Boolean.valueOf(false), null);
                                }
                            });
                            return;
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.this.checkChannelError(error.text, i);
                                MessagesController.this.loadingFullChats.remove(Integer.valueOf(i));
                            }
                        });
                    }
                });
                if (classGuid != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, classGuid);
                }
            }
        }
    }

    public void loadFullUser(final User user, final int classGuid, boolean force) {
        if (user != null && !this.loadingFullUsers.contains(Integer.valueOf(user.id))) {
            if (force || !this.loadedFullUsers.contains(Integer.valueOf(user.id))) {
                this.loadingFullUsers.add(Integer.valueOf(user.id));
                TL_users_getFullUser req = new TL_users_getFullUser();
                req.id = getInputUser(user);
                long dialog_id = (long) user.id;
                if (this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id)) == null) {
                    reloadDialogsReadValue(null, dialog_id);
                }
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$16$2 */
                    class C03202 implements Runnable {
                        C03202() {
                        }

                        public void run() {
                            MessagesController.this.loadingFullUsers.remove(Integer.valueOf(user.id));
                        }
                    }

                    public void run(final TLObject response, TL_error error) {
                        if (error == null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    TL_userFull userFull = response;
                                    MessagesController.this.applyDialogNotificationsSettings((long) user.id, userFull.notify_settings);
                                    if (userFull.bot_info instanceof TL_botInfo) {
                                        DataQuery.getInstance(MessagesController.this.currentAccount).putBotInfo(userFull.bot_info);
                                    }
                                    MessagesController.this.fullUsers.put(user.id, userFull);
                                    MessagesController.this.loadingFullUsers.remove(Integer.valueOf(user.id));
                                    MessagesController.this.loadedFullUsers.add(Integer.valueOf(user.id));
                                    String names = user.first_name + user.last_name + user.username;
                                    ArrayList<User> users = new ArrayList();
                                    users.add(userFull.user);
                                    MessagesController.this.putUsers(users, false);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(users, null, false, true);
                                    if (!(names == null || names.equals(userFull.user.first_name + userFull.user.last_name + userFull.user.username))) {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(1));
                                    }
                                    if (userFull.bot_info instanceof TL_botInfo) {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.botInfoDidLoaded, userFull.bot_info, Integer.valueOf(classGuid));
                                    }
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.userInfoDidLoaded, Integer.valueOf(user.id), userFull);
                                }
                            });
                        } else {
                            AndroidUtilities.runOnUIThread(new C03202());
                        }
                    }
                }), classGuid);
            }
        }
    }

    private void reloadMessages(ArrayList<Integer> mids, long dialog_id) {
        if (!mids.isEmpty()) {
            TLObject request;
            final ArrayList<Integer> result = new ArrayList();
            final Chat chat = ChatObject.getChatByDialog(dialog_id, this.currentAccount);
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
                final long j = dialog_id;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
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
                            Integer inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j));
                            if (inboxValue == null) {
                                inboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, j));
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j), inboxValue);
                            }
                            Integer outboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j));
                            if (outboxValue == null) {
                                outboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, j));
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j), outboxValue);
                            }
                            ArrayList<MessageObject> objects = new ArrayList();
                            for (a = 0; a < messagesRes.messages.size(); a++) {
                                Integer num;
                                Message message = (Message) messagesRes.messages.get(a);
                                if (chat != null && chat.megagroup) {
                                    message.flags |= Integer.MIN_VALUE;
                                }
                                message.dialog_id = j;
                                if (message.out) {
                                    num = outboxValue;
                                } else {
                                    num = inboxValue;
                                }
                                message.unread = num.intValue() < message.id;
                                objects.add(new MessageObject(MessagesController.this.currentAccount, message, usersLocal, chatsLocal, true));
                            }
                            ImageLoader.saveMessagesThumbs(messagesRes.messages);
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(messagesRes, j, -1, 0, false);
                            final ArrayList<MessageObject> arrayList = objects;
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    ArrayList<Integer> arrayList = (ArrayList) MessagesController.this.reloadingMessages.get(j);
                                    if (arrayList != null) {
                                        arrayList.removeAll(result);
                                        if (arrayList.isEmpty()) {
                                            MessagesController.this.reloadingMessages.remove(j);
                                        }
                                    }
                                    MessageObject dialogObj = (MessageObject) MessagesController.this.dialogMessage.get(j);
                                    if (dialogObj != null) {
                                        int a = 0;
                                        while (a < arrayList.size()) {
                                            MessageObject obj = (MessageObject) arrayList.get(a);
                                            if (dialogObj == null || dialogObj.getId() != obj.getId()) {
                                                a++;
                                            } else {
                                                MessagesController.this.dialogMessage.put(j, obj);
                                                if (obj.messageOwner.to_id.channel_id == 0) {
                                                    MessageObject obj2 = (MessageObject) MessagesController.this.dialogMessagesByIds.get(obj.getId());
                                                    MessagesController.this.dialogMessagesByIds.remove(obj.getId());
                                                    if (obj2 != null) {
                                                        MessagesController.this.dialogMessagesByIds.put(obj2.getId(), obj2);
                                                    }
                                                }
                                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                            }
                                        }
                                    }
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(j), arrayList);
                                }
                            });
                        }
                    }
                });
            }
        }
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
            }
        }
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                }, 2);
            } else if (currentEncryptedChat != null && currentEncryptedChat.access_hash != 0) {
                TL_messages_reportEncryptedSpam req2 = new TL_messages_reportEncryptedSpam();
                req2.peer = new TL_inputEncryptedChat();
                req2.peer.chat_id = currentEncryptedChat.id;
                req2.peer.access_hash = currentEncryptedChat.access_hash;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                }, 2);
            }
        }
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
                    FileLog.m0d("request spam button for " + dialogId);
                }
                if (this.notificationsPreferences.getInt("spam3_" + dialogId, 0) == 1) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("spam button already hidden for " + dialogId);
                    }
                } else if (this.notificationsPreferences.getBoolean("spam_" + dialogId, false)) {
                    TL_messages_hideReportSpam req = new TL_messages_hideReportSpam();
                    if (currentUser != null) {
                        req.peer = getInputPeer(currentUser.id);
                    } else if (currentChat != null) {
                        req.peer = getInputPeer(-currentChat.id);
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                        /* renamed from: org.telegram.messenger.MessagesController$21$1 */
                        class C03231 implements Runnable {
                            C03231() {
                            }

                            public void run() {
                                MessagesController.this.loadingPeerSettings.remove(dialogId);
                                Editor editor = MessagesController.this.notificationsPreferences.edit();
                                editor.remove("spam_" + dialogId);
                                editor.putInt("spam3_" + dialogId, 1);
                                editor.commit();
                            }
                        }

                        public void run(TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new C03231());
                        }
                    });
                } else {
                    TL_messages_getPeerSettings req2 = new TL_messages_getPeerSettings();
                    if (currentUser != null) {
                        req2.peer = getInputPeer(currentUser.id);
                    } else if (currentChat != null) {
                        req2.peer = getInputPeer(-currentChat.id);
                    }
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                        public void run(final TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.this.loadingPeerSettings.remove(dialogId);
                                    if (response != null) {
                                        TL_peerSettings res = response;
                                        Editor editor = MessagesController.this.notificationsPreferences.edit();
                                        if (res.report_spam) {
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.m0d("show spam button for " + dialogId);
                                            }
                                            editor.putInt("spam3_" + dialogId, 2);
                                            editor.commit();
                                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.peerSettingsDidLoaded, Long.valueOf(dialogId));
                                            return;
                                        }
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("don't show spam button for " + dialogId);
                                        }
                                        editor.putInt("spam3_" + dialogId, 1);
                                        editor.commit();
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    protected void processNewChannelDifferenceParams(int pts, int pts_count, int channelId) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("processNewChannelDifferenceParams pts = " + pts + " pts_count = " + pts_count + " channeldId = " + channelId);
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
                FileLog.m0d("APPLY CHANNEL PTS");
            }
            this.channelsPts.put(channelId, pts);
            MessagesStorage.getInstance(this.currentAccount).saveChannelPts(channelId, pts);
        } else if (channelPts != pts) {
            long updatesStartWaitTime = this.updatesStartWaitTimeChannels.get(channelId);
            if (this.gettingDifferenceChannels.get(channelId) || updatesStartWaitTime == 0 || Math.abs(System.currentTimeMillis() - updatesStartWaitTime) <= 1500) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("ADD CHANNEL UPDATE TO QUEUE pts = " + pts + " pts_count = " + pts_count);
                }
                if (updatesStartWaitTime == 0) {
                    this.updatesStartWaitTimeChannels.put(channelId, System.currentTimeMillis());
                }
                UserActionUpdatesPts updates = new UserActionUpdatesPts();
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

    protected void processNewDifferenceParams(int seq, int pts, int date, int pts_count) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("processNewDifferenceParams seq = " + seq + " pts = " + pts + " date = " + date + " pts_count = " + pts_count);
        }
        if (pts != -1) {
            if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + pts_count == pts) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("APPLY PTS");
                }
                MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(pts);
                MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
            } else if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() != pts) {
                if (this.gettingDifference || this.updatesStartWaitTimePts == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("ADD UPDATE TO QUEUE pts = " + pts + " pts_count = " + pts_count);
                    }
                    if (this.updatesStartWaitTimePts == 0) {
                        this.updatesStartWaitTimePts = System.currentTimeMillis();
                    }
                    UserActionUpdatesPts updates = new UserActionUpdatesPts();
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
                FileLog.m0d("APPLY SEQ");
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
                    FileLog.m0d("ADD UPDATE TO QUEUE seq = " + seq);
                }
                if (this.updatesStartWaitTimeSeq == 0) {
                    this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                }
                UserActionUpdatesSeq updates2 = new UserActionUpdatesSeq();
                updates2.seq = seq;
                this.updatesQueueSeq.add(updates2);
                return;
            }
            getDifference();
        }
    }

    public void didAddedNewTask(final int minDate, final SparseArray<ArrayList<Long>> mids) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if ((MessagesController.this.currentDeletingTaskMids == null && !MessagesController.this.gettingNewDeleteTask) || (MessagesController.this.currentDeletingTaskTime != 0 && minDate < MessagesController.this.currentDeletingTaskTime)) {
                    MessagesController.this.getNewDeleteTask(null, 0);
                }
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didCreatedNewDeleteTask, mids);
            }
        });
    }

    public void getNewDeleteTask(final ArrayList<Integer> oldTask, final int channelId) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                MessagesController.this.gettingNewDeleteTask = true;
                MessagesStorage.getInstance(MessagesController.this.currentAccount).getNewTask(oldTask, channelId);
            }
        });
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
        final ArrayList<Integer> mids = new ArrayList(this.currentDeletingTaskMids);
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$26$1 */
            class C03251 implements Runnable {
                C03251() {
                }

                public void run() {
                    MessagesController.this.getNewDeleteTask(mids, MessagesController.this.currentDeletingTaskChannelId);
                    MessagesController.this.currentDeletingTaskTime = 0;
                    MessagesController.this.currentDeletingTaskMids = null;
                }
            }

            public void run() {
                if (mids.isEmpty() || ((Integer) mids.get(0)).intValue() <= 0) {
                    MessagesController.this.deleteMessages(mids, null, null, 0, false);
                } else {
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).emptyMessagesMedia(mids);
                }
                Utilities.stageQueue.postRunnable(new C03251());
            }
        });
        return true;
    }

    public void processLoadedDeleteTask(final int taskTime, final ArrayList<Integer> messages, int channelId) {
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$27$1 */
            class C03261 implements Runnable {
                C03261() {
                }

                public void run() {
                    MessagesController.this.checkDeletingTask(true);
                }
            }

            public void run() {
                MessagesController.this.gettingNewDeleteTask = false;
                if (messages != null) {
                    MessagesController.this.currentDeletingTaskTime = taskTime;
                    MessagesController.this.currentDeletingTaskMids = messages;
                    if (MessagesController.this.currentDeleteTaskRunnable != null) {
                        Utilities.stageQueue.cancelRunnable(MessagesController.this.currentDeleteTaskRunnable);
                        MessagesController.this.currentDeleteTaskRunnable = null;
                    }
                    if (!MessagesController.this.checkDeletingTask(false)) {
                        MessagesController.this.currentDeleteTaskRunnable = new C03261();
                        Utilities.stageQueue.postRunnable(MessagesController.this.currentDeleteTaskRunnable, ((long) Math.abs(ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime() - MessagesController.this.currentDeletingTaskTime)) * 1000);
                        return;
                    }
                    return;
                }
                MessagesController.this.currentDeletingTaskTime = 0;
                MessagesController.this.currentDeletingTaskMids = null;
            }
        });
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
                r4 = did;
                r5 = count;
                r6 = max_id;
                r8 = classGuid;
                ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            MessagesController.this.processLoadedUserPhotos((photos_Photos) response, r4, r5, r6, false, r8);
                        }
                    }
                }), classGuid);
            }
        } else if (did < 0) {
            TL_messages_search req2 = new TL_messages_search();
            req2.filter = new TL_inputMessagesFilterChatPhotos();
            req2.limit = count;
            req2.offset_id = (int) max_id;
            req2.f49q = TtmlNode.ANONYMOUS_REGION_ID;
            req2.peer = getInputPeer(did);
            r4 = did;
            r5 = count;
            r6 = max_id;
            r8 = classGuid;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
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
                        MessagesController.this.processLoadedUserPhotos(res, r4, r5, r6, false, r8);
                    }
                }
            }), classGuid);
        }
    }

    public void blockUser(int user_id) {
        final User user = getUser(Integer.valueOf(user_id));
        if (user != null && !this.blockedUsers.contains(Integer.valueOf(user_id))) {
            this.blockedUsers.add(Integer.valueOf(user_id));
            if (user.bot) {
                DataQuery.getInstance(this.currentAccount).removeInline(user_id);
            } else {
                DataQuery.getInstance(this.currentAccount).removePeer(user_id);
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            TL_contacts_block req = new TL_contacts_block();
            req.id = getInputUser(user);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        ArrayList<Integer> ids = new ArrayList();
                        ids.add(Integer.valueOf(user.id));
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putBlockedUsers(ids, false);
                    }
                }
            });
        }
    }

    public void setUserBannedRole(int chatId, User user, TL_channelBannedRights rights, boolean isMegagroup, BaseFragment parentFragment) {
        if (user != null && rights != null) {
            final TL_channels_editBanned req = new TL_channels_editBanned();
            req.channel = getInputChannel(chatId);
            req.user_id = getInputUser(user);
            req.banned_rights = rights;
            final int i = chatId;
            final BaseFragment baseFragment = parentFragment;
            final boolean z = isMegagroup;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.MessagesController$31$1 */
                class C03281 implements Runnable {
                    C03281() {
                    }

                    public void run() {
                        MessagesController.this.loadFullChat(i, 0, true);
                    }
                }

                public void run(TLObject response, final TL_error error) {
                    if (error == null) {
                        MessagesController.this.processUpdates((Updates) response, false);
                        AndroidUtilities.runOnUIThread(new C03281(), 1000);
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            boolean z = true;
                            int access$000 = MessagesController.this.currentAccount;
                            TL_error tL_error = error;
                            BaseFragment baseFragment = baseFragment;
                            TLObject tLObject = req;
                            Object[] objArr = new Object[1];
                            if (z) {
                                z = false;
                            }
                            objArr[0] = Boolean.valueOf(z);
                            AlertsCreator.processError(access$000, tL_error, baseFragment, tLObject, objArr);
                        }
                    });
                }
            });
        }
    }

    public void setUserAdminRole(int chatId, User user, TL_channelAdminRights rights, boolean isMegagroup, BaseFragment parentFragment) {
        if (user != null && rights != null) {
            final TL_channels_editAdmin req = new TL_channels_editAdmin();
            req.channel = getInputChannel(chatId);
            req.user_id = getInputUser(user);
            req.admin_rights = rights;
            final int i = chatId;
            final BaseFragment baseFragment = parentFragment;
            final boolean z = isMegagroup;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.MessagesController$32$1 */
                class C03301 implements Runnable {
                    C03301() {
                    }

                    public void run() {
                        MessagesController.this.loadFullChat(i, 0, true);
                    }
                }

                public void run(TLObject response, final TL_error error) {
                    if (error == null) {
                        MessagesController.this.processUpdates((Updates) response, false);
                        AndroidUtilities.runOnUIThread(new C03301(), 1000);
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            boolean z = true;
                            int access$000 = MessagesController.this.currentAccount;
                            TL_error tL_error = error;
                            BaseFragment baseFragment = baseFragment;
                            TLObject tLObject = req;
                            Object[] objArr = new Object[1];
                            if (z) {
                                z = false;
                            }
                            objArr[0] = Boolean.valueOf(z);
                            AlertsCreator.processError(access$000, tL_error, baseFragment, tLObject, objArr);
                        }
                    });
                }
            });
        }
    }

    public void unblockUser(int user_id) {
        TL_contacts_unblock req = new TL_contacts_unblock();
        final User user = getUser(Integer.valueOf(user_id));
        if (user != null) {
            this.blockedUsers.remove(Integer.valueOf(user.id));
            req.id = getInputUser(user);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).deleteBlockedUser(user.id);
                }
            });
        }
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
            req.limit = Callback.DEFAULT_DRAG_ANIMATION_DURATION;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    ArrayList<Integer> blocked = new ArrayList();
                    ArrayList<User> users = null;
                    if (error == null) {
                        contacts_Blocked res = (contacts_Blocked) response;
                        Iterator it = res.blocked.iterator();
                        while (it.hasNext()) {
                            blocked.add(Integer.valueOf(((TL_contactBlocked) it.next()).user_id));
                        }
                        users = res.users;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, null, true, true);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putBlockedUsers(blocked, true);
                    }
                    MessagesController.this.processLoadedBlockedUsers(blocked, users, false);
                }
            });
        }
    }

    public void processLoadedBlockedUsers(final ArrayList<Integer> ids, final ArrayList<User> users, final boolean cache) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                if (users != null) {
                    MessagesController.this.putUsers(users, cache);
                }
                MessagesController.this.loadingBlockedUsers = false;
                if (ids.isEmpty() && cache && !UserConfig.getInstance(MessagesController.this.currentAccount).blockedUsersLoaded) {
                    MessagesController.this.getBlockedUsers(false);
                    return;
                }
                if (!cache) {
                    UserConfig.getInstance(MessagesController.this.currentAccount).blockedUsersLoaded = true;
                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                }
                MessagesController.this.blockedUsers = ids;
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
            }
        });
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
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_ALL));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$36$1 */
                    class C03321 implements Runnable {
                        C03321() {
                        }

                        public void run() {
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
                            UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(true);
                        }
                    }

                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            User user = MessagesController.this.getUser(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
                            if (user == null) {
                                user = UserConfig.getInstance(MessagesController.this.currentAccount).getCurrentUser();
                                MessagesController.this.putUser(user, false);
                            } else {
                                UserConfig.getInstance(MessagesController.this.currentAccount).setCurrentUser(user);
                            }
                            if (user != null) {
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).clearUserPhotos(user.id);
                                ArrayList<User> users = new ArrayList();
                                users.add(user);
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(users, null, false, true);
                                user.photo = (UserProfilePhoto) response;
                                AndroidUtilities.runOnUIThread(new C03321());
                            }
                        }
                    }
                });
                return;
            }
            return;
        }
        TL_photos_deletePhotos req2 = new TL_photos_deletePhotos();
        req2.id.add(photo);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void processLoadedUserPhotos(photos_Photos res, int did, int count, long max_id, boolean fromCache, int classGuid) {
        if (!fromCache) {
            MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(res.users, null, true, true);
            MessagesStorage.getInstance(this.currentAccount).putDialogPhotos(did, res);
        } else if (res == null || res.photos.isEmpty()) {
            loadDialogPhotos(did, count, max_id, false, classGuid);
            return;
        }
        final photos_Photos photos_photos = res;
        final boolean z = fromCache;
        final int i = did;
        final int i2 = count;
        final int i3 = classGuid;
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.this.putUsers(photos_photos.users, z);
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogPhotosLoaded, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z), Integer.valueOf(i3), photos_photos.photos);
            }
        });
    }

    public void uploadAndApplyUserAvatar(PhotoSize bigPhoto) {
        if (bigPhoto != null) {
            this.uploadingAvatar = FileLoader.getDirectory(4) + "/" + bigPhoto.location.volume_id + "_" + bigPhoto.location.local_id + ".jpg";
            FileLoader.getInstance(this.currentAccount).uploadFile(this.uploadingAvatar, false, true, 16777216);
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
        final int i;
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
                            FileLog.m3e(e);
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                            i = channelId;
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                                public void run(TLObject response, TL_error error) {
                                    if (error == null) {
                                        TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                                        MessagesController.this.processNewChannelDifferenceParams(res.pts, res.pts_count, i);
                                    }
                                    if (newTaskId != 0) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                                    }
                                }
                            });
                            return;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.m3e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        i = channelId;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
                        return;
                    }
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                }
                i = channelId;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
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
                        FileLog.m3e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                            public void run(TLObject response, TL_error error) {
                                if (error == null) {
                                    TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                                    MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                                }
                                if (newTaskId != 0) {
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                                }
                            }
                        });
                    }
                } catch (Exception e5) {
                    e = e5;
                    FileLog.m3e(e);
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, /* anonymous class already generated */);
                }
                newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, /* anonymous class already generated */);
        }
    }

    public void pinChannelMessage(Chat chat, int id, boolean notify) {
        TL_channels_updatePinnedMessage req = new TL_channels_updatePinnedMessage();
        req.channel = getInputChannel(chat);
        req.id = id;
        req.silent = !notify;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                }
            }
        });
    }

    public void deleteUserChannelHistory(final Chat chat, final User user, int offset) {
        if (offset == 0) {
            MessagesStorage.getInstance(this.currentAccount).deleteUserChannelHistory(chat.id, user.id);
        }
        TL_channels_deleteUserHistory req = new TL_channels_deleteUserHistory();
        req.channel = getInputChannel(chat);
        req.user_id = getInputUser(user);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    TL_messages_affectedHistory res = (TL_messages_affectedHistory) response;
                    if (res.offset > 0) {
                        MessagesController.this.deleteUserChannelHistory(chat, user, res.offset);
                    }
                    MessagesController.this.processNewChannelDifferenceParams(res.pts, res.pts_count, chat.id);
                }
            }
        });
    }

    public void deleteDialog(long did, int onlyHistory) {
        deleteDialog(did, true, onlyHistory, 0);
    }

    private void deleteDialog(long did, boolean first, int onlyHistory, int max_id) {
        int lower_part = (int) did;
        int high_id = (int) (did >> 32);
        int max_id_delete = max_id;
        if (onlyHistory == 2) {
            MessagesStorage.getInstance(this.currentAccount).deleteDialog(did, onlyHistory);
            return;
        }
        if (onlyHistory == 0 || onlyHistory == 3) {
            DataQuery.getInstance(this.currentAccount).uninstallShortcut(did);
        }
        if (first) {
            final long j;
            MessagesStorage.getInstance(this.currentAccount).deleteDialog(did, onlyHistory);
            TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(did);
            if (dialog != null) {
                if (max_id_delete == 0) {
                    max_id_delete = Math.max(0, dialog.top_message);
                }
                if (onlyHistory == 0 || onlyHistory == 3) {
                    this.dialogs.remove(dialog);
                    if (this.dialogsServerOnly.remove(dialog) && DialogObject.isChannel(dialog)) {
                        j = did;
                        Utilities.stageQueue.postRunnable(new Runnable() {
                            public void run() {
                                MessagesController.this.channelsPts.delete(-((int) j));
                                MessagesController.this.shortPollChannels.delete(-((int) j));
                                MessagesController.this.needShortPollChannels.delete(-((int) j));
                            }
                        });
                    }
                    this.dialogsGroupsOnly.remove(dialog);
                    this.dialogs_dict.remove(did);
                    this.dialogs_read_inbox_max.remove(Long.valueOf(did));
                    this.dialogs_read_outbox_max.remove(Long.valueOf(did));
                    this.nextDialogsCacheOffset--;
                } else {
                    dialog.unread_count = 0;
                }
                MessageObject object = (MessageObject) this.dialogMessage.get(dialog.id);
                this.dialogMessage.remove(dialog.id);
                int lastMessageId;
                if (object != null) {
                    lastMessageId = object.getId();
                    this.dialogMessagesByIds.remove(object.getId());
                } else {
                    lastMessageId = dialog.top_message;
                    object = (MessageObject) this.dialogMessagesByIds.get(dialog.top_message);
                    this.dialogMessagesByIds.remove(dialog.top_message);
                }
                if (!(object == null || object.messageOwner.random_id == 0)) {
                    this.dialogMessagesByRandomIds.remove(object.messageOwner.random_id);
                }
                if (onlyHistory != 1 || lower_part == 0 || lastMessageId <= 0) {
                    dialog.top_message = 0;
                } else {
                    Message message = new TL_messageService();
                    message.id = dialog.top_message;
                    message.out = ((long) UserConfig.getInstance(this.currentAccount).getClientUserId()) == did;
                    message.from_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    message.flags |= 256;
                    message.action = new TL_messageActionHistoryClear();
                    message.date = dialog.last_message_date;
                    if (lower_part > 0) {
                        message.to_id = new TL_peerUser();
                        message.to_id.user_id = lower_part;
                    } else if (ChatObject.isChannel(getChat(Integer.valueOf(-lower_part)))) {
                        message.to_id = new TL_peerChannel();
                        message.to_id.channel_id = -lower_part;
                    } else {
                        message.to_id = new TL_peerChat();
                        message.to_id.chat_id = -lower_part;
                    }
                    MessageObject messageObject = new MessageObject(this.currentAccount, message, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                    ArrayList<MessageObject> objArr = new ArrayList();
                    objArr.add(messageObject);
                    ArrayList arr = new ArrayList();
                    arr.add(message);
                    updateInterfaceWithMessages(did, objArr);
                    MessagesStorage.getInstance(this.currentAccount).putMessages(arr, false, true, false, 0);
                }
            }
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(did), Boolean.valueOf(false));
            j = did;
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MessagesController$44$1 */
                class C03341 implements Runnable {
                    C03341() {
                    }

                    public void run() {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).removeNotificationsForDialog(j);
                    }
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C03341());
                }
            });
        }
        if (high_id != 1 && onlyHistory != 3) {
            if (lower_part != 0) {
                InputPeer peer = getInputPeer(lower_part);
                if (peer == null) {
                    return;
                }
                TLObject req;
                if (!(peer instanceof TL_inputPeerChannel)) {
                    int i;
                    req = new TL_messages_deleteHistory();
                    req.peer = peer;
                    if (onlyHistory == 0) {
                        i = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    } else {
                        i = max_id_delete;
                    }
                    req.max_id = i;
                    req.just_clear = onlyHistory != 0;
                    final int max_id_delete_final = max_id_delete;
                    final long j2 = did;
                    final int i2 = onlyHistory;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            if (error == null) {
                                TL_messages_affectedHistory res = (TL_messages_affectedHistory) response;
                                if (res.offset > 0) {
                                    MessagesController.this.deleteDialog(j2, false, i2, max_id_delete_final);
                                }
                                MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                            }
                        }
                    }, 64);
                } else if (onlyHistory != 0) {
                    req = new TL_channels_deleteHistory();
                    req.channel = new TL_inputChannel();
                    req.channel.channel_id = peer.channel_id;
                    req.channel.access_hash = peer.access_hash;
                    req.max_id = max_id_delete > 0 ? max_id_delete : ConnectionsManager.DEFAULT_DATACENTER_ID;
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                        }
                    }, 64);
                }
            } else if (onlyHistory == 1) {
                SecretChatHelper.getInstance(this.currentAccount).sendClearHistoryMessage(getEncryptedChat(Integer.valueOf(high_id)), null);
            } else {
                SecretChatHelper.getInstance(this.currentAccount).declineSecretChat(high_id);
            }
        }
    }

    public void saveGif(Document document) {
        TL_messages_saveGif req = new TL_messages_saveGif();
        req.id = new TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.unsave = false;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void saveRecentSticker(Document document, boolean asMask) {
        TL_messages_saveRecentSticker req = new TL_messages_saveRecentSticker();
        req.id = new TL_inputDocument();
        req.id.id = document.id;
        req.id.access_hash = document.access_hash;
        req.unsave = false;
        req.attached = asMask;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void loadChannelParticipants(final Integer chat_id) {
        if (!this.loadingFullParticipants.contains(chat_id) && !this.loadedFullParticipants.contains(chat_id)) {
            this.loadingFullParticipants.add(chat_id);
            TL_channels_getParticipants req = new TL_channels_getParticipants();
            req.channel = getInputChannel(chat_id.intValue());
            req.filter = new TL_channelParticipantsRecent();
            req.offset = 0;
            req.limit = 32;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, final TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (error == null) {
                                TL_channels_channelParticipants res = response;
                                MessagesController.this.putUsers(res.users, false);
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, null, true, true);
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChannelUsers(chat_id.intValue(), res.participants);
                                MessagesController.this.loadedFullParticipants.add(chat_id);
                            }
                            MessagesController.this.loadingFullParticipants.remove(chat_id);
                        }
                    });
                }
            });
        }
    }

    public void loadChatInfo(int chat_id, CountDownLatch countDownLatch, boolean force) {
        MessagesStorage.getInstance(this.currentAccount).loadChatInfo(chat_id, countDownLatch, force, false);
    }

    public void processChatInfo(int chat_id, ChatFull info, ArrayList<User> usersArr, boolean fromCache, boolean force, boolean byChannelUsers, MessageObject pinnedMessageObject) {
        if (fromCache && chat_id > 0 && !byChannelUsers) {
            loadFullChat(chat_id, 0, force);
        }
        if (info != null) {
            final ArrayList<User> arrayList = usersArr;
            final boolean z = fromCache;
            final ChatFull chatFull = info;
            final boolean z2 = byChannelUsers;
            final MessageObject messageObject = pinnedMessageObject;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MessagesController.this.putUsers(arrayList, z);
                    if (chatFull.stickerset != null) {
                        DataQuery.getInstance(MessagesController.this.currentAccount).getGroupStickerSetById(chatFull.stickerset);
                    }
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, chatFull, Integer.valueOf(0), Boolean.valueOf(z2), messageObject);
                }
            });
        }
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
                    this.statusRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            if (error == null) {
                                MessagesController.this.lastStatusUpdateTime = System.currentTimeMillis();
                                MessagesController.this.offlineSent = false;
                                MessagesController.this.statusSettingState = 0;
                            } else if (MessagesController.this.lastStatusUpdateTime != 0) {
                                MessagesController.this.lastStatusUpdateTime = MessagesController.this.lastStatusUpdateTime + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                            }
                            MessagesController.this.statusRequest = 0;
                        }
                    });
                }
            } else if (!(this.statusSettingState == 2 || this.offlineSent || Math.abs(System.currentTimeMillis() - ConnectionsManager.getInstance(this.currentAccount).getPauseTime()) < AdaptiveTrackSelection.DEFAULT_MIN_TIME_BETWEEN_BUFFER_REEVALUTATION_MS)) {
                this.statusSettingState = 2;
                if (this.statusRequest != 0) {
                    ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.statusRequest, true);
                }
                req = new TL_account_updateStatus();
                req.offline = true;
                this.statusRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            MessagesController.this.offlineSent = true;
                        } else if (MessagesController.this.lastStatusUpdateTime != 0) {
                            MessagesController.this.lastStatusUpdateTime = MessagesController.this.lastStatusUpdateTime + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                        }
                        MessagesController.this.statusRequest = 0;
                    }
                });
            }
            if (this.updatesQueueChannels.size() != 0) {
                for (a = 0; a < this.updatesQueueChannels.size(); a++) {
                    key = this.updatesQueueChannels.keyAt(a);
                    if (1500 + this.updatesStartWaitTimeChannels.valueAt(a) < currentTime) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("QUEUE CHANNEL " + key + " UPDATES WAIT TIMEOUT - CHECK QUEUE");
                        }
                        processChannelsUpdatesQueue(key, 0);
                    }
                }
            }
            a = 0;
            while (a < 3) {
                if (getUpdatesStartTime(a) != 0 && getUpdatesStartTime(a) + 1500 < currentTime) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d(a + " QUEUE UPDATES WAIT TIMEOUT - CHECK QUEUE");
                    }
                    processUpdatesQueue(a, 0);
                }
                a++;
            }
        }
        if (this.channelViewsToSend.size() != 0 && Math.abs(System.currentTimeMillis() - this.lastViewsCheckTime) >= DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS) {
            this.lastViewsCheckTime = System.currentTimeMillis();
            a = 0;
            while (a < this.channelViewsToSend.size()) {
                key = this.channelViewsToSend.keyAt(a);
                final TL_messages_getMessagesViews req2 = new TL_messages_getMessagesViews();
                req2.peer = getInputPeer(key);
                req2.id = (ArrayList) this.channelViewsToSend.valueAt(a);
                req2.increment = a == 0;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            Vector vector = (Vector) response;
                            final SparseArray<SparseIntArray> channelViews = new SparseArray();
                            SparseIntArray array = (SparseIntArray) channelViews.get(key);
                            if (array == null) {
                                array = new SparseIntArray();
                                channelViews.put(key, array);
                            }
                            int a = 0;
                            while (a < req2.id.size() && a < vector.objects.size()) {
                                array.put(((Integer) req2.id.get(a)).intValue(), ((Integer) vector.objects.get(a)).intValue());
                                a++;
                            }
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putChannelViews(channelViews, req2.peer instanceof TL_inputPeerChannel);
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didUpdatedMessagesViews, channelViews);
                                }
                            });
                        }
                    }
                });
                a++;
            }
            this.channelViewsToSend.clear();
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
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
                    }
                });
            }
        }
        if (this.shortPollChannels.size() != 0) {
            for (a = 0; a < this.shortPollChannels.size(); a++) {
                key = this.shortPollChannels.keyAt(a);
                if (((long) this.shortPollChannels.valueAt(a)) < System.currentTimeMillis() / 1000) {
                    this.shortPollChannels.delete(key);
                    if (this.needShortPollChannels.indexOfKey(key) >= 0) {
                        getChannelDifference(key);
                    }
                }
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
                            timeToRemove = DefaultLoadControl.DEFAULT_MAX_BUFFER_MS;
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
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
                    }
                });
            }
        }
        if (Theme.selectedAutoNightType == 1 && Math.abs(currentTime - lastThemeCheckTime) >= 60) {
            AndroidUtilities.runOnUIThread(this.themeCheckRunnable);
            lastThemeCheckTime = currentTime;
        }
        if (this.lastPushRegisterSendTime != 0 && Math.abs(SystemClock.uptimeMillis() - this.lastPushRegisterSendTime) >= 10800000) {
            GcmInstanceIDListenerService.sendRegistrationToServer(SharedConfig.pushString);
        }
        LocationController.getInstance(this.currentAccount).update();
    }

    private String getUserNameForTyping(User user) {
        if (user == null) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        if (user.first_name != null && user.first_name.length() > 0) {
            return user.first_name;
        }
        if (user.last_name == null || user.last_name.length() <= 0) {
            return TtmlNode.ANONYMOUS_REGION_ID;
        }
        return user.last_name;
    }

    private void updatePrintingStrings() {
        final LongSparseArray<CharSequence> newPrintingStrings = new LongSparseArray();
        final LongSparseArray<Integer> newPrintingStringsTypes = new LongSparseArray();
        ArrayList<Long> keys = new ArrayList(this.printingUsers.keySet());
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
                            newPrintingStrings.put(key, LocaleController.formatString("IsRecordingAudio", R.string.IsRecordingAudio, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("RecordingAudio", R.string.RecordingAudio));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(1));
                    } else if ((pu.action instanceof TL_sendMessageRecordRoundAction) || (pu.action instanceof TL_sendMessageUploadRoundAction)) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsRecordingRound", R.string.IsRecordingRound, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("RecordingRound", R.string.RecordingRound));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(4));
                    } else if (pu.action instanceof TL_sendMessageUploadAudioAction) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsSendingAudio", R.string.IsSendingAudio, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("SendingAudio", R.string.SendingAudio));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(2));
                    } else if ((pu.action instanceof TL_sendMessageUploadVideoAction) || (pu.action instanceof TL_sendMessageRecordVideoAction)) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsSendingVideo", R.string.IsSendingVideo, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("SendingVideoStatus", R.string.SendingVideoStatus));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(2));
                    } else if (pu.action instanceof TL_sendMessageUploadDocumentAction) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsSendingFile", R.string.IsSendingFile, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("SendingFile", R.string.SendingFile));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(2));
                    } else if (pu.action instanceof TL_sendMessageUploadPhotoAction) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsSendingPhoto", R.string.IsSendingPhoto, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("SendingPhoto", R.string.SendingPhoto));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(2));
                    } else if (pu.action instanceof TL_sendMessageGamePlayAction) {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsSendingGame", R.string.IsSendingGame, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("SendingGame", R.string.SendingGame));
                        }
                        newPrintingStringsTypes.put(key, Integer.valueOf(3));
                    } else {
                        if (lower_id < 0) {
                            newPrintingStrings.put(key, LocaleController.formatString("IsTypingGroup", R.string.IsTypingGroup, getUserNameForTyping(user)));
                        } else {
                            newPrintingStrings.put(key, LocaleController.getString("Typing", R.string.Typing));
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
                        newPrintingStrings.put(key, LocaleController.formatString("IsTypingGroup", R.string.IsTypingGroup, label.toString()));
                    } else if (arr.size() > 2) {
                        newPrintingStrings.put(key, String.format(LocaleController.getPluralString("AndMoreTypingGroup", arr.size() - 2), new Object[]{label.toString(), Integer.valueOf(arr.size() - 2)}));
                    } else {
                        newPrintingStrings.put(key, LocaleController.formatString("AreTypingGroup", R.string.AreTypingGroup, label.toString()));
                    }
                    newPrintingStringsTypes.put(key, Integer.valueOf(0));
                }
            }
        }
        this.lastPrintingStringCount = newPrintingStrings.size();
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                MessagesController.this.printingStrings = newPrintingStrings;
                MessagesController.this.printingStringsTypes = newPrintingStringsTypes;
            }
        });
    }

    public void cancelTyping(int action, long dialog_id) {
        LongSparseArray<Boolean> typings = (LongSparseArray) this.sendingTypings.get(action);
        if (typings != null) {
            typings.remove(dialog_id);
        }
    }

    public void sendTyping(final long dialog_id, final int action, int classGuid) {
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
                            reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                                /* renamed from: org.telegram.messenger.MessagesController$57$1 */
                                class C03381 implements Runnable {
                                    C03381() {
                                    }

                                    public void run() {
                                        LongSparseArray<Boolean> typings = (LongSparseArray) MessagesController.this.sendingTypings.get(action);
                                        if (typings != null) {
                                            typings.remove(dialog_id);
                                        }
                                    }
                                }

                                public void run(TLObject response, TL_error error) {
                                    AndroidUtilities.runOnUIThread(new C03381());
                                }
                            }, 2);
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
                        reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {

                            /* renamed from: org.telegram.messenger.MessagesController$58$1 */
                            class C03391 implements Runnable {
                                C03391() {
                                }

                                public void run() {
                                    LongSparseArray<Boolean> typings = (LongSparseArray) MessagesController.this.sendingTypings.get(action);
                                    if (typings != null) {
                                        typings.remove(dialog_id);
                                    }
                                }
                            }

                            public void run(TLObject response, TL_error error) {
                                AndroidUtilities.runOnUIThread(new C03391());
                            }
                        }, 2);
                        if (classGuid != 0) {
                            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(reqId, classGuid);
                        }
                    }
                }
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
            FileLog.m0d("load messages in chat " + dialog_id + " count " + count + " max_id " + max_id + " cache " + fromCache + " mindate = " + midDate + " guid " + classGuid + " load_type " + load_type + " last_message_id " + last_message_id + " index " + loadIndex + " firstUnread " + first_unread + " unread_count " + unread_count + " last_date " + last_date + " queryFromServer " + queryFromServer);
        }
        int lower_part = (int) dialog_id;
        if (fromCache || lower_part == 0) {
            MessagesStorage.getInstance(this.currentAccount).getMessages(dialog_id, count, max_id, offset_date, midDate, classGuid, load_type, isChannel, loadIndex);
        } else if (loadDialog && ((load_type == 3 || load_type == 2) && last_message_id == 0)) {
            req = new TL_messages_getPeerDialogs();
            InputPeer inputPeer = getInputPeer((int) dialog_id);
            TL_inputDialogPeer inputDialogPeer = new TL_inputDialogPeer();
            inputDialogPeer.peer = inputPeer;
            req.peers.add(inputDialogPeer);
            final long j = dialog_id;
            r6 = count;
            r7 = max_id;
            final int i = offset_date;
            final int i2 = midDate;
            r10 = classGuid;
            r11 = load_type;
            final boolean z = isChannel;
            r13 = loadIndex;
            r14 = first_unread;
            r15 = last_date;
            r16 = queryFromServer;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
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
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(dialogs, false);
                            }
                            MessagesController.this.loadMessagesInternal(j, r6, r7, i, false, i2, r10, r11, dialog.top_message, z, r13, r14, dialog.unread_count, r15, r16, dialog.unread_mentions_count, false);
                        }
                    }
                }
            });
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
            } else if (lower_part < 0 && max_id != 0 && ChatObject.isChannel(getChat(Integer.valueOf(-lower_part)))) {
                req.add_offset = -1;
                req.limit++;
            }
            req.limit = count;
            req.offset_id = max_id;
            req.offset_date = offset_date;
            final int i3 = count;
            r6 = max_id;
            r7 = offset_date;
            final long j2 = dialog_id;
            r10 = classGuid;
            r11 = first_unread;
            final int i4 = last_message_id;
            r13 = unread_count;
            r14 = last_date;
            r15 = load_type;
            r16 = isChannel;
            final int i5 = loadIndex;
            final boolean z2 = queryFromServer;
            final int i6 = mentionsCount;
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (response != null) {
                        messages_Messages res = (messages_Messages) response;
                        if (res.messages.size() > i3) {
                            res.messages.remove(0);
                        }
                        int mid = r6;
                        if (r7 != 0 && !res.messages.isEmpty()) {
                            mid = ((Message) res.messages.get(res.messages.size() - 1)).id;
                            for (int a = res.messages.size() - 1; a >= 0; a--) {
                                Message message = (Message) res.messages.get(a);
                                if (message.date > r7) {
                                    mid = message.id;
                                    break;
                                }
                            }
                        }
                        MessagesController.this.processLoadedMessages(res, j2, i3, mid, r7, false, r10, r11, i4, r13, r14, r15, r16, false, i5, z2, i6);
                    }
                }
            }), classGuid);
        }
    }

    public void reloadWebPages(final long dialog_id, HashMap<String, ArrayList<MessageObject>> webpagesToReload) {
        for (Entry<String, ArrayList<MessageObject>> entry : webpagesToReload.entrySet()) {
            final String url = (String) entry.getKey();
            ArrayList<MessageObject> messages = (ArrayList) entry.getValue();
            ArrayList<MessageObject> arrayList = (ArrayList) this.reloadingWebpages.get(url);
            if (arrayList == null) {
                arrayList = new ArrayList();
                this.reloadingWebpages.put(url, arrayList);
            }
            arrayList.addAll(messages);
            TL_messages_getWebPagePreview req = new TL_messages_getWebPagePreview();
            req.message = url;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            ArrayList<MessageObject> arrayList = (ArrayList) MessagesController.this.reloadingWebpages.remove(url);
                            if (arrayList != null) {
                                messages_Messages messagesRes = new TL_messages_messages();
                                int a;
                                if (response instanceof TL_messageMediaWebPage) {
                                    TL_messageMediaWebPage media = response;
                                    if ((media.webpage instanceof TL_webPage) || (media.webpage instanceof TL_webPageEmpty)) {
                                        for (a = 0; a < arrayList.size(); a++) {
                                            ((MessageObject) arrayList.get(a)).messageOwner.media.webpage = media.webpage;
                                            if (a == 0) {
                                                ImageLoader.saveMessageThumbs(((MessageObject) arrayList.get(a)).messageOwner);
                                            }
                                            messagesRes.messages.add(((MessageObject) arrayList.get(a)).messageOwner);
                                        }
                                    } else {
                                        MessagesController.this.reloadingWebpagesPending.put(media.webpage.id, arrayList);
                                    }
                                } else {
                                    for (a = 0; a < arrayList.size(); a++) {
                                        ((MessageObject) arrayList.get(a)).messageOwner.media.webpage = new TL_webPageEmpty();
                                        messagesRes.messages.add(((MessageObject) arrayList.get(a)).messageOwner);
                                    }
                                }
                                if (!messagesRes.messages.isEmpty()) {
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(messagesRes, dialog_id, -2, 0, false);
                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialog_id), arrayList);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    public void processLoadedMessages(messages_Messages messagesRes, long dialog_id, int count, int max_id, int offset_date, boolean isCache, int classGuid, int first_unread, int last_message_id, int unread_count, int last_date, int load_type, boolean isChannel, boolean isEnd, int loadIndex, boolean queryFromServer, int mentionsCount) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("processLoadedMessages size " + messagesRes.messages.size() + " in chat " + dialog_id + " count " + count + " max_id " + max_id + " cache " + isCache + " guid " + classGuid + " load_type " + load_type + " last_message_id " + last_message_id + " isChannel " + isChannel + " index " + loadIndex + " firstUnread " + first_unread + " unread_count " + unread_count + " last_date " + last_date + " queryFromServer " + queryFromServer);
        }
        final messages_Messages messages_messages = messagesRes;
        final long j = dialog_id;
        final boolean z = isCache;
        final int i = count;
        final int i2 = load_type;
        final boolean z2 = queryFromServer;
        final int i3 = first_unread;
        final int i4 = max_id;
        final int i5 = offset_date;
        final int i6 = classGuid;
        final int i7 = last_message_id;
        final boolean z3 = isChannel;
        final int i8 = loadIndex;
        final int i9 = unread_count;
        final int i10 = last_date;
        final int i11 = mentionsCount;
        final boolean z4 = isEnd;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$62$1 */
            class C03431 implements Runnable {
                C03431() {
                }

                public void run() {
                    MessagesController messagesController = MessagesController.this;
                    long j = j;
                    int i = i;
                    int i2 = (i2 == 2 && z2) ? i3 : i4;
                    messagesController.loadMessages(j, i, i2, i5, false, 0, i6, i2, i7, z3, i8, i3, i9, i10, z2, i11);
                }
            }

            public void run() {
                int a;
                boolean createDialog = false;
                boolean isMegagroup = false;
                if (messages_messages instanceof TL_messages_channelMessages) {
                    int channelId = -((int) j);
                    if (MessagesController.this.channelsPts.get(channelId) == 0 && MessagesStorage.getInstance(MessagesController.this.currentAccount).getChannelPtsSync(channelId) == 0) {
                        MessagesController.this.channelsPts.put(channelId, messages_messages.pts);
                        createDialog = true;
                        if (MessagesController.this.needShortPollChannels.indexOfKey(channelId) < 0 || MessagesController.this.shortPollChannels.indexOfKey(channelId) >= 0) {
                            MessagesController.this.getChannelDifference(channelId);
                        } else {
                            MessagesController.this.getChannelDifference(channelId, 2, 0, null);
                        }
                    }
                    for (a = 0; a < messages_messages.chats.size(); a++) {
                        Chat chat = (Chat) messages_messages.chats.get(a);
                        if (chat.id == channelId) {
                            isMegagroup = chat.megagroup;
                            break;
                        }
                    }
                }
                int lower_id = (int) j;
                int high_id = (int) (j >> 32);
                if (!z) {
                    ImageLoader.saveMessagesThumbs(messages_messages.messages);
                }
                if (high_id == 1 || lower_id == 0 || !z || messages_messages.messages.size() != 0) {
                    Message message;
                    SparseArray<User> usersDict = new SparseArray();
                    SparseArray<Chat> chatsDict = new SparseArray();
                    for (a = 0; a < messages_messages.users.size(); a++) {
                        User u = (User) messages_messages.users.get(a);
                        usersDict.put(u.id, u);
                    }
                    for (a = 0; a < messages_messages.chats.size(); a++) {
                        Chat c = (Chat) messages_messages.chats.get(a);
                        chatsDict.put(c.id, c);
                    }
                    int size = messages_messages.messages.size();
                    if (!z) {
                        Integer inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(j));
                        if (inboxValue == null) {
                            inboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, j));
                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(j), inboxValue);
                        }
                        Integer outboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(j));
                        if (outboxValue == null) {
                            outboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, j));
                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(j), outboxValue);
                        }
                        for (a = 0; a < size; a++) {
                            message = (Message) messages_messages.messages.get(a);
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
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(messages_messages, j, i2, i4, createDialog);
                    }
                    ArrayList<MessageObject> objects = new ArrayList();
                    ArrayList<Integer> messagesToReload = new ArrayList();
                    HashMap<String, ArrayList<MessageObject>> webpagesToReload = new HashMap();
                    for (a = 0; a < size; a++) {
                        message = (Message) messages_messages.messages.get(a);
                        message.dialog_id = j;
                        MessageObject messageObject = new MessageObject(MessagesController.this.currentAccount, message, (SparseArray) usersDict, (SparseArray) chatsDict, true);
                        objects.add(messageObject);
                        if (z) {
                            if (message.media instanceof TL_messageMediaUnsupported) {
                                if (message.media.bytes != null && (message.media.bytes.length == 0 || (message.media.bytes.length == 1 && message.media.bytes[0] < (byte) 76))) {
                                    messagesToReload.add(Integer.valueOf(message.id));
                                }
                            } else if (message.media instanceof TL_messageMediaWebPage) {
                                if ((message.media.webpage instanceof TL_webPagePending) && message.media.webpage.date <= ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime()) {
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
                    final ArrayList<MessageObject> arrayList2 = objects;
                    final ArrayList<Integer> arrayList3 = messagesToReload;
                    final HashMap<String, ArrayList<MessageObject>> hashMap = webpagesToReload;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessagesController.this.putUsers(messages_messages.users, z);
                            MessagesController.this.putChats(messages_messages.chats, z);
                            int first_unread_final = ConnectionsManager.DEFAULT_DATACENTER_ID;
                            if (z2 && i2 == 2) {
                                for (int a = 0; a < messages_messages.messages.size(); a++) {
                                    Message message = (Message) messages_messages.messages.get(a);
                                    if (!message.out && message.id > i3 && message.id < first_unread_final) {
                                        first_unread_final = message.id;
                                    }
                                }
                            }
                            if (first_unread_final == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                                first_unread_final = i3;
                            }
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesDidLoaded, Long.valueOf(j), Integer.valueOf(i), arrayList2, Boolean.valueOf(z), Integer.valueOf(first_unread_final), Integer.valueOf(i7), Integer.valueOf(i9), Integer.valueOf(i10), Integer.valueOf(i2), Boolean.valueOf(z4), Integer.valueOf(i6), Integer.valueOf(i8), Integer.valueOf(i4), Integer.valueOf(i11));
                            if (!arrayList3.isEmpty()) {
                                MessagesController.this.reloadMessages(arrayList3, j);
                            }
                            if (!hashMap.isEmpty()) {
                                MessagesController.this.reloadWebPages(j, hashMap);
                            }
                        }
                    });
                    return;
                }
                AndroidUtilities.runOnUIThread(new C03431());
            }
        });
    }

    public void loadHintDialogs() {
        if (this.hintDialogs.isEmpty() && !TextUtils.isEmpty(this.installReferer)) {
            TL_help_getRecentMeUrls req = new TL_help_getRecentMeUrls();
            req.referer = this.installReferer;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(final TLObject response, TL_error error) {
                    if (error == null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                TL_help_recentMeUrls res = response;
                                MessagesController.this.putUsers(res.users, false);
                                MessagesController.this.putChats(res.chats, false);
                                MessagesController.this.hintDialogs.clear();
                                MessagesController.this.hintDialogs.addAll(res.urls);
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                        });
                    }
                }
            });
        }
    }

    public void loadDialogs(int offset, final int count, boolean fromCache) {
        if (!this.loadingDialogs && !this.resetingDialogs) {
            this.loadingDialogs = true;
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("load cacheOffset = " + offset + " count = " + count + " cache = " + fromCache);
            }
            if (fromCache) {
                MessagesStorage.getInstance(this.currentAccount).getDialogs(offset == 0 ? 0 : this.nextDialogsCacheOffset, count);
                return;
            }
            TL_messages_getDialogs req = new TL_messages_getDialogs();
            req.limit = count;
            req.exclude_pinned = true;
            if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == -1) {
                boolean found = false;
                for (int a = this.dialogs.size() - 1; a >= 0; a--) {
                    TL_dialog dialog = (TL_dialog) this.dialogs.get(a);
                    if (!dialog.pinned) {
                        int high_id = (int) (dialog.id >> 32);
                        if (!(((int) dialog.id) == 0 || high_id == 1 || dialog.top_message <= 0)) {
                            MessageObject message = (MessageObject) this.dialogMessage.get(dialog.id);
                            if (message != null && message.getId() > 0) {
                                int id;
                                req.offset_date = message.messageOwner.date;
                                req.offset_id = message.messageOwner.id;
                                if (message.messageOwner.to_id.channel_id != 0) {
                                    id = -message.messageOwner.to_id.channel_id;
                                } else if (message.messageOwner.to_id.chat_id != 0) {
                                    id = -message.messageOwner.to_id.chat_id;
                                } else {
                                    id = message.messageOwner.to_id.user_id;
                                }
                                req.offset_peer = getInputPeer(id);
                                found = true;
                                if (!found) {
                                    req.offset_peer = new TL_inputPeerEmpty();
                                }
                            }
                        }
                    }
                }
                if (found) {
                    req.offset_peer = new TL_inputPeerEmpty();
                }
            } else if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                this.dialogsEndReached = true;
                this.serverDialogsEndReached = true;
                this.loadingDialogs = false;
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                return;
            } else {
                req.offset_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetId;
                req.offset_date = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetDate;
                if (req.offset_id == 0) {
                    req.offset_peer = new TL_inputPeerEmpty();
                } else {
                    if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId != 0) {
                        req.offset_peer = new TL_inputPeerChannel();
                        req.offset_peer.channel_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChannelId;
                    } else if (UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId != 0) {
                        req.offset_peer = new TL_inputPeerUser();
                        req.offset_peer.user_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetUserId;
                    } else {
                        req.offset_peer = new TL_inputPeerChat();
                        req.offset_peer.chat_id = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetChatId;
                    }
                    req.offset_peer.access_hash = UserConfig.getInstance(this.currentAccount).dialogsLoadOffsetAccess;
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesController.this.processLoadedDialogs((messages_Dialogs) response, null, 0, count, 0, false, false, false);
                    }
                }
            });
        }
    }

    public void forceResetDialogs() {
        resetDialogs(true, MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
    }

    private void resetDialogs(boolean query, int seq, int newPts, int date, int qts) {
        if (query) {
            if (!this.resetingDialogs) {
                this.resetingDialogs = true;
                final int i = seq;
                final int i2 = newPts;
                final int i3 = date;
                final int i4 = qts;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getPinnedDialogs(), new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (response != null) {
                            MessagesController.this.resetDialogsPinned = (TL_messages_peerDialogs) response;
                            MessagesController.this.resetDialogs(false, i, i2, i3, i4);
                        }
                    }
                });
                TLObject req2 = new TL_messages_getDialogs();
                req2.limit = 100;
                req2.exclude_pinned = true;
                req2.offset_peer = new TL_inputPeerEmpty();
                i = seq;
                i2 = newPts;
                i3 = date;
                i4 = qts;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (error == null) {
                            MessagesController.this.resetDialogsAll = (messages_Dialogs) response;
                            MessagesController.this.resetDialogs(false, i, i2, i3, i4);
                        }
                    }
                });
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

    protected void completeDialogsReset(messages_Dialogs dialogsRes, int messagesCount, int seq, int newPts, int date, int qts, LongSparseArray<TL_dialog> new_dialogs_dict, LongSparseArray<MessageObject> new_dialogMessage, Message lastMessage) {
        final int i = newPts;
        final int i2 = date;
        final int i3 = qts;
        final messages_Dialogs messages_dialogs = dialogsRes;
        final LongSparseArray<TL_dialog> longSparseArray = new_dialogs_dict;
        final LongSparseArray<MessageObject> longSparseArray2 = new_dialogMessage;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$67$1 */
            class C03461 implements Runnable {
                C03461() {
                }

                public void run() {
                    int a;
                    MessagesController.this.resetingDialogs = false;
                    MessagesController.this.applyDialogsNotificationsSettings(messages_dialogs.dialogs);
                    if (!UserConfig.getInstance(MessagesController.this.currentAccount).draftsLoaded) {
                        DataQuery.getInstance(MessagesController.this.currentAccount).loadDrafts();
                    }
                    MessagesController.this.putUsers(messages_dialogs.users, false);
                    MessagesController.this.putChats(messages_dialogs.chats, false);
                    for (a = 0; a < MessagesController.this.dialogs.size(); a++) {
                        MessageObject messageObject;
                        TL_dialog oldDialog = (TL_dialog) MessagesController.this.dialogs.get(a);
                        if (((int) oldDialog.id) != 0) {
                            MessagesController.this.dialogs_dict.remove(oldDialog.id);
                            messageObject = (MessageObject) MessagesController.this.dialogMessage.get(oldDialog.id);
                            MessagesController.this.dialogMessage.remove(oldDialog.id);
                            if (messageObject != null) {
                                MessagesController.this.dialogMessagesByIds.remove(messageObject.getId());
                                if (messageObject.messageOwner.random_id != 0) {
                                    MessagesController.this.dialogMessagesByRandomIds.remove(messageObject.messageOwner.random_id);
                                }
                            }
                        }
                    }
                    for (a = 0; a < longSparseArray.size(); a++) {
                        long key = longSparseArray.keyAt(a);
                        TL_dialog value = (TL_dialog) longSparseArray.valueAt(a);
                        if (value.draft instanceof TL_draftMessage) {
                            DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(value.id, value.draft, null, false);
                        }
                        MessagesController.this.dialogs_dict.put(key, value);
                        messageObject = (MessageObject) longSparseArray2.get(value.id);
                        MessagesController.this.dialogMessage.put(key, messageObject);
                        if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                            MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                            if (messageObject.messageOwner.random_id != 0) {
                                MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                            }
                        }
                    }
                    MessagesController.this.dialogs.clear();
                    int size = MessagesController.this.dialogs_dict.size();
                    for (a = 0; a < size; a++) {
                        MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(a));
                    }
                    MessagesController.this.sortDialogs(null);
                    MessagesController.this.dialogsEndReached = true;
                    MessagesController.this.serverDialogsEndReached = false;
                    if (!(UserConfig.getInstance(MessagesController.this.currentAccount).totalDialogsLoadCount >= 400 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == -1 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == ConnectionsManager.DEFAULT_DATACENTER_ID)) {
                        MessagesController.this.loadDialogs(0, 100, false);
                    }
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }

            public void run() {
                MessagesController.this.gettingDifference = false;
                MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(i);
                MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(i2);
                MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(i3);
                MessagesController.this.getDifference();
                AndroidUtilities.runOnUIThread(new C03461());
            }
        });
    }

    private void migrateDialogs(final int offset, int offsetDate, int offsetUser, int offsetChat, int offsetChannel, long accessPeer) {
        if (!this.migratingDialogs && offset != -1) {
            this.migratingDialogs = true;
            TL_messages_getDialogs req = new TL_messages_getDialogs();
            req.exclude_pinned = true;
            req.limit = 100;
            req.offset_id = offset;
            req.offset_date = offsetDate;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("start migrate with id " + offset + " date " + LocaleController.getInstance().formatterStats.format(((long) offsetDate) * 1000));
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.MessagesController$68$2 */
                class C03492 implements Runnable {
                    C03492() {
                    }

                    public void run() {
                        MessagesController.this.migratingDialogs = false;
                    }
                }

                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        final messages_Dialogs dialogsRes = (messages_Dialogs) response;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.MessagesController$68$1$1 */
                            class C03471 implements Runnable {
                                C03471() {
                                }

                                public void run() {
                                    MessagesController.this.migratingDialogs = false;
                                }
                            }

                            public void run() {
                                try {
                                    int a;
                                    Message message;
                                    int offsetId;
                                    TL_dialog dialog;
                                    long did;
                                    UserConfig instance = UserConfig.getInstance(MessagesController.this.currentAccount);
                                    instance.totalDialogsLoadCount += dialogsRes.dialogs.size();
                                    Message lastMessage = null;
                                    for (a = 0; a < dialogsRes.messages.size(); a++) {
                                        message = (Message) dialogsRes.messages.get(a);
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("search migrate id " + message.id + " date " + LocaleController.getInstance().formatterStats.format(((long) message.date) * 1000));
                                        }
                                        if (lastMessage == null || message.date < lastMessage.date) {
                                            lastMessage = message;
                                        }
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("migrate step with id " + lastMessage.id + " date " + LocaleController.getInstance().formatterStats.format(((long) lastMessage.date) * 1000));
                                    }
                                    if (dialogsRes.dialogs.size() >= 100) {
                                        offsetId = lastMessage.id;
                                    } else {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("migrate stop due to not 100 dialogs");
                                        }
                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
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
                                    SQLiteCursor cursor = MessagesStorage.getInstance(MessagesController.this.currentAccount).getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE did IN (%s)", new Object[]{stringBuilder.toString()}), new Object[0]);
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
                                        FileLog.m0d("migrate found missing dialogs " + dialogsRes.dialogs.size());
                                    }
                                    cursor = MessagesStorage.getInstance(MessagesController.this.currentAccount).getDatabase().queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND did >> 32 IN (0, -1)", new Object[0]);
                                    if (cursor.next()) {
                                        int date = Math.max(NUM, cursor.intValue(0));
                                        a = 0;
                                        while (a < dialogsRes.messages.size()) {
                                            message = (Message) dialogsRes.messages.get(a);
                                            if (message.date < date) {
                                                if (offset != -1) {
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId;
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
                                                    offsetId = -1;
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        FileLog.m0d("migrate stop due to reached loaded dialogs " + LocaleController.getInstance().formatterStats.format(((long) date) * 1000));
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
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId;
                                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess;
                                            offsetId = -1;
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.m0d("migrate stop due to reached loaded dialogs " + LocaleController.getInstance().formatterStats.format(((long) date) * 1000));
                                            }
                                        }
                                    }
                                    cursor.dispose();
                                    UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate = lastMessage.date;
                                    Chat chat;
                                    if (lastMessage.to_id.channel_id != 0) {
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = lastMessage.to_id.channel_id;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = 0;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = 0;
                                        for (a = 0; a < dialogsRes.chats.size(); a++) {
                                            chat = (Chat) dialogsRes.chats.get(a);
                                            if (chat.id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId) {
                                                UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = chat.access_hash;
                                                break;
                                            }
                                        }
                                    } else if (lastMessage.to_id.chat_id != 0) {
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = lastMessage.to_id.chat_id;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = 0;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = 0;
                                        for (a = 0; a < dialogsRes.chats.size(); a++) {
                                            chat = (Chat) dialogsRes.chats.get(a);
                                            if (chat.id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId) {
                                                UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = chat.access_hash;
                                                break;
                                            }
                                        }
                                    } else if (lastMessage.to_id.user_id != 0) {
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId = lastMessage.to_id.user_id;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId = 0;
                                        UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId = 0;
                                        for (a = 0; a < dialogsRes.users.size(); a++) {
                                            User user = (User) dialogsRes.users.get(a);
                                            if (user.id == UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId) {
                                                UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess = user.access_hash;
                                                break;
                                            }
                                        }
                                    }
                                    MessagesController.this.processLoadedDialogs(dialogsRes, null, offsetId, 0, 0, false, true, false);
                                } catch (Throwable e) {
                                    FileLog.m3e(e);
                                    AndroidUtilities.runOnUIThread(new C03471());
                                }
                            }
                        });
                        return;
                    }
                    AndroidUtilities.runOnUIThread(new C03492());
                }
            });
        }
    }

    public void processLoadedDialogs(messages_Dialogs dialogsRes, ArrayList<EncryptedChat> encChats, int offset, int count, int loadType, boolean resetEnd, boolean migrate, boolean fromCache) {
        final int i = loadType;
        final messages_Dialogs messages_dialogs = dialogsRes;
        final boolean z = resetEnd;
        final int i2 = count;
        final int i3 = offset;
        final boolean z2 = fromCache;
        final boolean z3 = migrate;
        final ArrayList<EncryptedChat> arrayList = encChats;
        Utilities.stageQueue.postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$69$1 */
            class C03501 implements Runnable {
                C03501() {
                }

                public void run() {
                    MessagesController.this.putUsers(messages_dialogs.users, true);
                    MessagesController.this.loadingDialogs = false;
                    if (z) {
                        MessagesController.this.dialogsEndReached = false;
                        MessagesController.this.serverDialogsEndReached = false;
                    } else if (UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == ConnectionsManager.DEFAULT_DATACENTER_ID) {
                        MessagesController.this.dialogsEndReached = true;
                        MessagesController.this.serverDialogsEndReached = true;
                    } else {
                        MessagesController.this.loadDialogs(0, i2, false);
                    }
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }

            public void run() {
                if (!MessagesController.this.firstGettingTask) {
                    MessagesController.this.getNewDeleteTask(null, 0);
                    MessagesController.this.firstGettingTask = true;
                }
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("loaded loadType " + i + " count " + messages_dialogs.dialogs.size());
                }
                if (i == 1 && messages_dialogs.dialogs.size() == 0) {
                    AndroidUtilities.runOnUIThread(new C03501());
                    return;
                }
                int a;
                User user;
                final LongSparseArray<TL_dialog> new_dialogs_dict = new LongSparseArray();
                final LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray();
                SparseArray usersDict = new SparseArray();
                SparseArray chatsDict = new SparseArray();
                for (a = 0; a < messages_dialogs.users.size(); a++) {
                    User u = (User) messages_dialogs.users.get(a);
                    usersDict.put(u.id, u);
                }
                for (a = 0; a < messages_dialogs.chats.size(); a++) {
                    Chat c = (Chat) messages_dialogs.chats.get(a);
                    chatsDict.put(c.id, c);
                }
                if (i == 1) {
                    MessagesController.this.nextDialogsCacheOffset = i3 + i2;
                }
                Message lastMessage = null;
                for (a = 0; a < messages_dialogs.messages.size(); a++) {
                    Chat chat;
                    Message message = (Message) messages_dialogs.messages.get(a);
                    if (lastMessage == null || message.date < lastMessage.date) {
                        lastMessage = message;
                    }
                    MessageObject messageObject;
                    if (message.to_id.channel_id != 0) {
                        chat = (Chat) chatsDict.get(message.to_id.channel_id);
                        if (chat == null || !chat.left) {
                            if (chat != null && chat.megagroup) {
                                message.flags |= Integer.MIN_VALUE;
                            }
                            messageObject = new MessageObject(MessagesController.this.currentAccount, message, usersDict, chatsDict, false);
                            new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                        }
                    } else {
                        if (message.to_id.chat_id != 0) {
                            chat = (Chat) chatsDict.get(message.to_id.chat_id);
                            if (!(chat == null || chat.migrated_to == null)) {
                            }
                        }
                        messageObject = new MessageObject(MessagesController.this.currentAccount, message, usersDict, chatsDict, false);
                        new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                    }
                }
                if (!(z2 || z3 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == -1 || i != 0)) {
                    if (lastMessage == null || lastMessage.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId) {
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = ConnectionsManager.DEFAULT_DATACENTER_ID;
                    } else {
                        UserConfig instance = UserConfig.getInstance(MessagesController.this.currentAccount);
                        instance.totalDialogsLoadCount += messages_dialogs.dialogs.size();
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId = lastMessage.id;
                        UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetDate = lastMessage.date;
                        if (lastMessage.to_id.channel_id != 0) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = lastMessage.to_id.channel_id;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = 0;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = 0;
                            for (a = 0; a < messages_dialogs.chats.size(); a++) {
                                chat = (Chat) messages_dialogs.chats.get(a);
                                if (chat.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId) {
                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = chat.access_hash;
                                    break;
                                }
                            }
                        } else if (lastMessage.to_id.chat_id != 0) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = lastMessage.to_id.chat_id;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = 0;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = 0;
                            for (a = 0; a < messages_dialogs.chats.size(); a++) {
                                chat = (Chat) messages_dialogs.chats.get(a);
                                if (chat.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId) {
                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = chat.access_hash;
                                    break;
                                }
                            }
                        } else if (lastMessage.to_id.user_id != 0) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId = lastMessage.to_id.user_id;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChatId = 0;
                            UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetChannelId = 0;
                            for (a = 0; a < messages_dialogs.users.size(); a++) {
                                user = (User) messages_dialogs.users.get(a);
                                if (user.id == UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetUserId) {
                                    UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetAccess = user.access_hash;
                                    break;
                                }
                            }
                        }
                    }
                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                }
                final ArrayList<TL_dialog> dialogsToReload = new ArrayList();
                for (a = 0; a < messages_dialogs.dialogs.size(); a++) {
                    Integer value;
                    TL_dialog d = (TL_dialog) messages_dialogs.dialogs.get(a);
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
                        boolean allowCheck = true;
                        if (DialogObject.isChannel(d)) {
                            chat = (Chat) chatsDict.get(-((int) d.id));
                            if (chat != null) {
                                if (!chat.megagroup) {
                                    allowCheck = false;
                                }
                                if (chat.left) {
                                }
                            }
                            MessagesController.this.channelsPts.put(-((int) d.id), d.pts);
                        } else if (((int) d.id) < 0) {
                            chat = (Chat) chatsDict.get(-((int) d.id));
                            if (!(chat == null || chat.migrated_to == null)) {
                            }
                        }
                        new_dialogs_dict.put(d.id, d);
                        if (allowCheck && i == 1 && ((d.read_outbox_max_id == 0 || d.read_inbox_max_id == 0) && d.top_message != 0)) {
                            dialogsToReload.add(d);
                        }
                        value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                        value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                    }
                }
                if (i != 1) {
                    ImageLoader.saveMessagesThumbs(messages_dialogs.messages);
                    for (a = 0; a < messages_dialogs.messages.size(); a++) {
                        message = (Message) messages_dialogs.messages.get(a);
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
                            ConcurrentHashMap<Long, Integer> read_max = message.out ? MessagesController.this.dialogs_read_outbox_max : MessagesController.this.dialogs_read_inbox_max;
                            value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                            if (value == null) {
                                value = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
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
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(messages_dialogs, false);
                }
                if (i == 2) {
                    chat = (Chat) messages_dialogs.chats.get(0);
                    MessagesController.this.getChannelDifference(chat.id);
                    MessagesController.this.checkChannelInviter(chat.id);
                }
                final SparseArray sparseArray = chatsDict;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int a;
                        if (i != 1) {
                            MessagesController.this.applyDialogsNotificationsSettings(messages_dialogs.dialogs);
                            if (!UserConfig.getInstance(MessagesController.this.currentAccount).draftsLoaded) {
                                DataQuery.getInstance(MessagesController.this.currentAccount).loadDrafts();
                            }
                        }
                        MessagesController.this.putUsers(messages_dialogs.users, i == 1);
                        MessagesController.this.putChats(messages_dialogs.chats, i == 1);
                        if (arrayList != null) {
                            for (a = 0; a < arrayList.size(); a++) {
                                EncryptedChat encryptedChat = (EncryptedChat) arrayList.get(a);
                                if ((encryptedChat instanceof TL_encryptedChat) && AndroidUtilities.getMyLayerVersion(encryptedChat.layer) < 73) {
                                    SecretChatHelper.getInstance(MessagesController.this.currentAccount).sendNotifyLayerMessage(encryptedChat, null);
                                }
                                MessagesController.this.putEncryptedChat(encryptedChat, true);
                            }
                        }
                        if (!z3) {
                            MessagesController.this.loadingDialogs = false;
                        }
                        boolean added = false;
                        int lastDialogDate = (!z3 || MessagesController.this.dialogs.isEmpty()) ? 0 : ((TL_dialog) MessagesController.this.dialogs.get(MessagesController.this.dialogs.size() - 1)).last_message_date;
                        for (a = 0; a < new_dialogs_dict.size(); a++) {
                            long key = new_dialogs_dict.keyAt(a);
                            TL_dialog value = (TL_dialog) new_dialogs_dict.valueAt(a);
                            if (!z3 || lastDialogDate == 0 || value.last_message_date >= lastDialogDate) {
                                TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(key);
                                if (i != 1 && (value.draft instanceof TL_draftMessage)) {
                                    DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(value.id, value.draft, null, false);
                                }
                                MessageObject messageObject;
                                if (currentDialog == null) {
                                    added = true;
                                    MessagesController.this.dialogs_dict.put(key, value);
                                    messageObject = (MessageObject) new_dialogMessage.get(value.id);
                                    MessagesController.this.dialogMessage.put(key, messageObject);
                                    if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                        MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                        if (messageObject.messageOwner.random_id != 0) {
                                            MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                        }
                                    }
                                } else {
                                    if (i != 1) {
                                        currentDialog.notify_settings = value.notify_settings;
                                    }
                                    currentDialog.pinned = value.pinned;
                                    currentDialog.pinnedNum = value.pinnedNum;
                                    MessageObject oldMsg = (MessageObject) MessagesController.this.dialogMessage.get(key);
                                    if ((oldMsg == null || !oldMsg.deleted) && oldMsg != null && currentDialog.top_message <= 0) {
                                        MessageObject newMsg = (MessageObject) new_dialogMessage.get(value.id);
                                        if (oldMsg.deleted || newMsg == null || newMsg.messageOwner.date > oldMsg.messageOwner.date) {
                                            MessagesController.this.dialogs_dict.put(key, value);
                                            MessagesController.this.dialogMessage.put(key, newMsg);
                                            if (newMsg != null && newMsg.messageOwner.to_id.channel_id == 0) {
                                                MessagesController.this.dialogMessagesByIds.put(newMsg.getId(), newMsg);
                                                if (!(newMsg == null || newMsg.messageOwner.random_id == 0)) {
                                                    MessagesController.this.dialogMessagesByRandomIds.put(newMsg.messageOwner.random_id, newMsg);
                                                }
                                            }
                                            MessagesController.this.dialogMessagesByIds.remove(oldMsg.getId());
                                            if (oldMsg.messageOwner.random_id != 0) {
                                                MessagesController.this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                                            }
                                        }
                                    } else if (value.top_message >= currentDialog.top_message) {
                                        MessagesController.this.dialogs_dict.put(key, value);
                                        messageObject = (MessageObject) new_dialogMessage.get(value.id);
                                        MessagesController.this.dialogMessage.put(key, messageObject);
                                        if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                            MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                            if (!(messageObject == null || messageObject.messageOwner.random_id == 0)) {
                                                MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                            }
                                        }
                                        if (oldMsg != null) {
                                            MessagesController.this.dialogMessagesByIds.remove(oldMsg.getId());
                                            if (oldMsg.messageOwner.random_id != 0) {
                                                MessagesController.this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        MessagesController.this.dialogs.clear();
                        int size = MessagesController.this.dialogs_dict.size();
                        for (a = 0; a < size; a++) {
                            MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(a));
                        }
                        MessagesController.this.sortDialogs(z3 ? sparseArray : null);
                        if (!(i == 2 || z3)) {
                            MessagesController messagesController = MessagesController.this;
                            boolean z = (messages_dialogs.dialogs.size() == 0 || messages_dialogs.dialogs.size() != i2) && i == 0;
                            messagesController.dialogsEndReached = z;
                            if (!z2) {
                                messagesController = MessagesController.this;
                                z = (messages_dialogs.dialogs.size() == 0 || messages_dialogs.dialogs.size() != i2) && i == 0;
                                messagesController.serverDialogsEndReached = z;
                            }
                        }
                        if (!(z2 || z3 || UserConfig.getInstance(MessagesController.this.currentAccount).totalDialogsLoadCount >= 400 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == -1 || UserConfig.getInstance(MessagesController.this.currentAccount).dialogsLoadOffsetId == ConnectionsManager.DEFAULT_DATACENTER_ID)) {
                            MessagesController.this.loadDialogs(0, 100, false);
                        }
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        if (z3) {
                            UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId = i3;
                            UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                            MessagesController.this.migratingDialogs = false;
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
                        } else {
                            MessagesController.this.generateUpdateMessage();
                            if (!added && i == 1) {
                                MessagesController.this.loadDialogs(0, i2, false);
                            }
                        }
                        MessagesController.this.migrateDialogs(UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetDate, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetUserId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChatId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetChannelId, UserConfig.getInstance(MessagesController.this.currentAccount).migrateOffsetAccess);
                        if (!dialogsToReload.isEmpty()) {
                            MessagesController.this.reloadDialogsReadValue(dialogsToReload, 0);
                        }
                    }
                });
            }
        });
    }

    private void applyDialogNotificationsSettings(long dialog_id, PeerNotifySettings notify_settings) {
        int currentValue = this.notificationsPreferences.getInt("notify2_" + dialog_id, 0);
        int currentValue2 = this.notificationsPreferences.getInt("notifyuntil_" + dialog_id, 0);
        Editor editor = this.notificationsPreferences.edit();
        boolean updated = false;
        TL_dialog dialog = (TL_dialog) this.dialogs_dict.get(dialog_id);
        if (dialog != null) {
            dialog.notify_settings = notify_settings;
        }
        editor.putBoolean("silent_" + dialog_id, notify_settings.silent);
        if (notify_settings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
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
                    dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
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
                editor.remove("notify2_" + dialog_id);
            }
            MessagesStorage.getInstance(this.currentAccount).setDialogFlags(dialog_id, 0);
        }
        editor.commit();
        if (updated) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
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
                editor.putBoolean("silent_" + dialog_id, dialog.notify_settings.silent);
                if (dialog.notify_settings.mute_until == 0) {
                    editor.remove("notify2_" + dialog_id);
                } else if (dialog.notify_settings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() + 31536000) {
                    editor.putInt("notify2_" + dialog_id, 2);
                    dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
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

    public void reloadMentionsCountForChannels(final ArrayList<Integer> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                for (int a = 0; a < arrayList.size(); a++) {
                    final long dialog_id = (long) (-((Integer) arrayList.get(a)).intValue());
                    TL_messages_getUnreadMentions req = new TL_messages_getUnreadMentions();
                    req.peer = MessagesController.this.getInputPeer((int) dialog_id);
                    req.limit = 1;
                    ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(final TLObject response, TL_error error) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    messages_Messages res = response;
                                    if (res != null) {
                                        int newCount;
                                        if (res.count != 0) {
                                            newCount = res.count;
                                        } else {
                                            newCount = res.messages.size();
                                        }
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).resetMentionsCount(dialog_id, newCount);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void processDialogsUpdateRead(final LongSparseArray<Integer> dialogsToUpdate, final LongSparseArray<Integer> dialogsMentionsToUpdate) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                int a;
                TL_dialog currentDialog;
                if (dialogsToUpdate != null) {
                    for (a = 0; a < dialogsToUpdate.size(); a++) {
                        currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(dialogsToUpdate.keyAt(a));
                        if (currentDialog != null) {
                            currentDialog.unread_count = ((Integer) dialogsToUpdate.valueAt(a)).intValue();
                        }
                    }
                }
                if (dialogsMentionsToUpdate != null) {
                    for (a = 0; a < dialogsMentionsToUpdate.size(); a++) {
                        currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(dialogsMentionsToUpdate.keyAt(a));
                        if (currentDialog != null) {
                            currentDialog.unread_mentions_count = ((Integer) dialogsMentionsToUpdate.valueAt(a)).intValue();
                            if (MessagesController.this.createdDialogMainThreadIds.contains(Long.valueOf(currentDialog.id))) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(currentDialog.id), Integer.valueOf(currentDialog.unread_mentions_count));
                            }
                        }
                    }
                }
                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                if (dialogsToUpdate != null) {
                    NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                }
            }
        });
    }

    protected void checkLastDialogMessage(TL_dialog dialog, InputPeer peer, long taskId) {
        Throwable e;
        long newTaskId;
        final TL_dialog tL_dialog;
        final int lower_id = (int) dialog.id;
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
                            data2.writeInt32(8);
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
                            peer.serializeToStream(data2);
                            data = data2;
                        } catch (Exception e2) {
                            e = e2;
                            data = data2;
                            FileLog.m3e(e);
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                            tL_dialog = dialog;
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                                /* renamed from: org.telegram.messenger.MessagesController$72$1 */
                                class C03531 implements Runnable {
                                    C03531() {
                                    }

                                    public void run() {
                                        TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(tL_dialog.id);
                                        if (currentDialog != null && currentDialog.top_message == 0) {
                                            MessagesController.this.deleteDialog(tL_dialog.id, 3);
                                        }
                                    }
                                }

                                /* renamed from: org.telegram.messenger.MessagesController$72$2 */
                                class C03542 implements Runnable {
                                    C03542() {
                                    }

                                    public void run() {
                                        MessagesController.this.checkingLastMessagesDialogs.delete(lower_id);
                                    }
                                }

                                public void run(TLObject response, TL_error error) {
                                    if (response != null) {
                                        messages_Messages res = (messages_Messages) response;
                                        if (res.messages.isEmpty()) {
                                            AndroidUtilities.runOnUIThread(new C03531());
                                        } else {
                                            TL_messages_dialogs dialogs = new TL_messages_dialogs();
                                            Message newMessage = (Message) res.messages.get(0);
                                            TL_dialog newDialog = new TL_dialog();
                                            newDialog.flags = tL_dialog.flags;
                                            newDialog.top_message = newMessage.id;
                                            newDialog.last_message_date = newMessage.date;
                                            newDialog.notify_settings = tL_dialog.notify_settings;
                                            newDialog.pts = tL_dialog.pts;
                                            newDialog.unread_count = tL_dialog.unread_count;
                                            newDialog.unread_mentions_count = tL_dialog.unread_mentions_count;
                                            newDialog.read_inbox_max_id = tL_dialog.read_inbox_max_id;
                                            newDialog.read_outbox_max_id = tL_dialog.read_outbox_max_id;
                                            newDialog.pinned = tL_dialog.pinned;
                                            newDialog.pinnedNum = tL_dialog.pinnedNum;
                                            long j = tL_dialog.id;
                                            newDialog.id = j;
                                            newMessage.dialog_id = j;
                                            dialogs.users.addAll(res.users);
                                            dialogs.chats.addAll(res.chats);
                                            dialogs.dialogs.add(newDialog);
                                            dialogs.messages.addAll(res.messages);
                                            dialogs.count = 1;
                                            MessagesController.this.processDialogsUpdate(dialogs, null);
                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(res.messages, true, true, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask(), true);
                                        }
                                    }
                                    if (newTaskId != 0) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                                    }
                                    AndroidUtilities.runOnUIThread(new C03542());
                                }
                            });
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.m3e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        tL_dialog = dialog;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
                    }
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                } else {
                    newTaskId = taskId;
                }
                tL_dialog = dialog;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
            }
        }
    }

    public void processDialogsUpdate(final messages_Dialogs dialogsRes, ArrayList<EncryptedChat> arrayList) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                int a;
                final LongSparseArray<TL_dialog> new_dialogs_dict = new LongSparseArray();
                final LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray();
                SparseArray usersDict = new SparseArray(dialogsRes.users.size());
                SparseArray chatsDict = new SparseArray(dialogsRes.chats.size());
                final LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray();
                for (a = 0; a < dialogsRes.users.size(); a++) {
                    User u = (User) dialogsRes.users.get(a);
                    usersDict.put(u.id, u);
                }
                for (a = 0; a < dialogsRes.chats.size(); a++) {
                    Chat c = (Chat) dialogsRes.chats.get(a);
                    chatsDict.put(c.id, c);
                }
                for (a = 0; a < dialogsRes.messages.size(); a++) {
                    Chat chat;
                    Message message = (Message) dialogsRes.messages.get(a);
                    MessageObject messageObject;
                    if (message.to_id.channel_id != 0) {
                        chat = (Chat) chatsDict.get(message.to_id.channel_id);
                        if (chat != null && chat.left) {
                        }
                        messageObject = new MessageObject(MessagesController.this.currentAccount, message, usersDict, chatsDict, false);
                        new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                    } else {
                        if (message.to_id.chat_id != 0) {
                            chat = (Chat) chatsDict.get(message.to_id.chat_id);
                            if (!(chat == null || chat.migrated_to == null)) {
                            }
                        }
                        messageObject = new MessageObject(MessagesController.this.currentAccount, message, usersDict, chatsDict, false);
                        new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                    }
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
                    MessageObject mess;
                    Integer value;
                    if (DialogObject.isChannel(d)) {
                        chat = (Chat) chatsDict.get(-((int) d.id));
                        if (chat != null && chat.left) {
                        }
                        if (d.last_message_date == 0) {
                            mess = (MessageObject) new_dialogMessage.get(d.id);
                            if (mess != null) {
                                d.last_message_date = mess.messageOwner.date;
                            }
                        }
                        new_dialogs_dict.put(d.id, d);
                        dialogsToUpdate.put(d.id, Integer.valueOf(d.unread_count));
                        value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                        value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                    } else {
                        if (((int) d.id) < 0) {
                            chat = (Chat) chatsDict.get(-((int) d.id));
                            if (!(chat == null || chat.migrated_to == null)) {
                            }
                        }
                        if (d.last_message_date == 0) {
                            mess = (MessageObject) new_dialogMessage.get(d.id);
                            if (mess != null) {
                                d.last_message_date = mess.messageOwner.date;
                            }
                        }
                        new_dialogs_dict.put(d.id, d);
                        dialogsToUpdate.put(d.id, Integer.valueOf(d.unread_count));
                        value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                        value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                        if (value == null) {
                            value = Integer.valueOf(0);
                        }
                        MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        int a;
                        MessagesController.this.putUsers(dialogsRes.users, true);
                        MessagesController.this.putChats(dialogsRes.chats, true);
                        for (a = 0; a < new_dialogs_dict.size(); a++) {
                            long key = new_dialogs_dict.keyAt(a);
                            TL_dialog value = (TL_dialog) new_dialogs_dict.valueAt(a);
                            TL_dialog currentDialog = (TL_dialog) MessagesController.this.dialogs_dict.get(key);
                            MessageObject messageObject;
                            if (currentDialog == null) {
                                MessagesController messagesController = MessagesController.this;
                                messagesController.nextDialogsCacheOffset++;
                                MessagesController.this.dialogs_dict.put(key, value);
                                messageObject = (MessageObject) new_dialogMessage.get(value.id);
                                MessagesController.this.dialogMessage.put(key, messageObject);
                                if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                    MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                    if (messageObject.messageOwner.random_id != 0) {
                                        MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                    }
                                }
                            } else {
                                currentDialog.unread_count = value.unread_count;
                                if (currentDialog.unread_mentions_count != value.unread_mentions_count) {
                                    currentDialog.unread_mentions_count = value.unread_mentions_count;
                                    if (MessagesController.this.createdDialogMainThreadIds.contains(Long.valueOf(currentDialog.id))) {
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateMentionsCount, Long.valueOf(currentDialog.id), Integer.valueOf(currentDialog.unread_mentions_count));
                                    }
                                }
                                MessageObject oldMsg = (MessageObject) MessagesController.this.dialogMessage.get(key);
                                if (oldMsg != null && currentDialog.top_message <= 0) {
                                    MessageObject newMsg = (MessageObject) new_dialogMessage.get(value.id);
                                    if (oldMsg.deleted || newMsg == null || newMsg.messageOwner.date > oldMsg.messageOwner.date) {
                                        MessagesController.this.dialogs_dict.put(key, value);
                                        MessagesController.this.dialogMessage.put(key, newMsg);
                                        if (newMsg != null && newMsg.messageOwner.to_id.channel_id == 0) {
                                            MessagesController.this.dialogMessagesByIds.put(newMsg.getId(), newMsg);
                                            if (newMsg.messageOwner.random_id != 0) {
                                                MessagesController.this.dialogMessagesByRandomIds.put(newMsg.messageOwner.random_id, newMsg);
                                            }
                                        }
                                        MessagesController.this.dialogMessagesByIds.remove(oldMsg.getId());
                                        if (oldMsg.messageOwner.random_id != 0) {
                                            MessagesController.this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                                        }
                                    }
                                } else if ((oldMsg != null && oldMsg.deleted) || value.top_message > currentDialog.top_message) {
                                    MessagesController.this.dialogs_dict.put(key, value);
                                    messageObject = (MessageObject) new_dialogMessage.get(value.id);
                                    MessagesController.this.dialogMessage.put(key, messageObject);
                                    if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                        MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                        if (messageObject.messageOwner.random_id != 0) {
                                            MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                        }
                                    }
                                    if (oldMsg != null) {
                                        MessagesController.this.dialogMessagesByIds.remove(oldMsg.getId());
                                        if (oldMsg.messageOwner.random_id != 0) {
                                            MessagesController.this.dialogMessagesByRandomIds.remove(oldMsg.messageOwner.random_id);
                                        }
                                    }
                                    if (messageObject == null) {
                                        MessagesController.this.checkLastDialogMessage(value, null, 0);
                                    }
                                }
                            }
                        }
                        MessagesController.this.dialogs.clear();
                        int size = MessagesController.this.dialogs_dict.size();
                        for (a = 0; a < size; a++) {
                            MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(a));
                        }
                        MessagesController.this.sortDialogs(null);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                    }
                });
            }
        });
    }

    public void addToViewsQueue(final Message message) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                int peer;
                if (message.to_id.channel_id != 0) {
                    peer = -message.to_id.channel_id;
                } else if (message.to_id.chat_id != 0) {
                    peer = -message.to_id.chat_id;
                } else {
                    peer = message.to_id.user_id;
                }
                ArrayList<Integer> ids = (ArrayList) MessagesController.this.channelViewsToSend.get(peer);
                if (ids == null) {
                    ids = new ArrayList();
                    MessagesController.this.channelViewsToSend.put(peer, ids);
                }
                if (!ids.contains(Integer.valueOf(message.id))) {
                    ids.add(Integer.valueOf(message.id));
                }
            }
        });
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
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
            }
        } else {
            TL_messages_readMessageContents req2 = new TL_messages_readMessageContents();
            req2.id.add(Integer.valueOf(messageObject.getId()));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                        MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                    }
                }
            });
        }
    }

    public void markMentionMessageAsRead(int mid, int channelId, long did) {
        MessagesStorage.getInstance(this.currentAccount).markMentionMessageAsRead(mid, channelId, did);
        if (channelId != 0) {
            TL_channels_readMessageContents req = new TL_channels_readMessageContents();
            req.channel = getInputChannel(channelId);
            if (req.channel != null) {
                req.id.add(Integer.valueOf(mid));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
                return;
            }
            return;
        }
        TL_messages_readMessageContents req2 = new TL_messages_readMessageContents();
        req2.id.add(Integer.valueOf(mid));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                    MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                }
            }
        });
    }

    public void markMessageAsRead(int mid, int channelId, int ttl) {
        if (mid != 0 && ttl > 0) {
            int time = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            MessagesStorage.getInstance(this.currentAccount).createTaskForMid(mid, channelId, time, time, ttl, false);
            if (channelId != 0) {
                TL_channels_readMessageContents req = new TL_channels_readMessageContents();
                req.channel = getInputChannel(channelId);
                req.id.add(Integer.valueOf(mid));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                    }
                });
                return;
            }
            TL_messages_readMessageContents req2 = new TL_messages_readMessageContents();
            req2.id.add(Integer.valueOf(mid));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                        MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                    }
                }
            });
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null && (response instanceof TL_messages_affectedMessages)) {
                        TL_messages_affectedMessages res = (TL_messages_affectedMessages) response;
                        MessagesController.this.processNewDifferenceParams(-1, res.pts, -1, res.pts_count);
                    }
                }
            });
            return;
        }
        EncryptedChat chat = getEncryptedChat(Integer.valueOf(high_id));
        if (chat.auth_key != null && chat.auth_key.length > 1 && (chat instanceof TL_encryptedChat)) {
            TL_messages_readEncryptedHistory req2 = new TL_messages_readEncryptedHistory();
            req2.peer = new TL_inputEncryptedChat();
            req2.peer.chat_id = chat.id;
            req2.peer.access_hash = chat.access_hash;
            req2.max_date = task.maxDate;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req2, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
        }
    }

    private void checkReadTasks() {
        long time = SystemClock.uptimeMillis();
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

    public void markDialogAsReadNow(final long dialogId) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                ReadTask currentReadTask = (ReadTask) MessagesController.this.readTasksMap.get(dialogId);
                if (currentReadTask != null) {
                    MessagesController.this.completeReadTask(currentReadTask);
                    MessagesController.this.readTasks.remove(currentReadTask);
                    MessagesController.this.readTasksMap.remove(dialogId);
                }
            }
        });
    }

    public void markDialogAsRead(long dialogId, int maxPositiveId, int maxNegativeId, int maxDate, boolean popup, int countDiff, boolean readNow) {
        boolean createReadTask;
        int lower_part = (int) dialogId;
        int high_id = (int) (dialogId >> 32);
        final long j;
        final int i;
        if (lower_part != 0) {
            if (maxPositiveId != 0 && high_id != 1) {
                long maxMessageId = (long) maxPositiveId;
                long minMessageId = (long) maxNegativeId;
                boolean isChannel = false;
                if (lower_part < 0 && ChatObject.isChannel(getChat(Integer.valueOf(-lower_part)))) {
                    maxMessageId |= ((long) (-lower_part)) << 32;
                    minMessageId |= ((long) (-lower_part)) << 32;
                    isChannel = true;
                }
                Integer value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialogId));
                if (value == null) {
                    value = Integer.valueOf(0);
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialogId), Integer.valueOf(Math.max(value.intValue(), maxPositiveId)));
                MessagesStorage.getInstance(this.currentAccount).processPendingRead(dialogId, maxMessageId, minMessageId, maxDate, isChannel);
                j = dialogId;
                i = countDiff;
                final int i2 = maxPositiveId;
                final boolean z = popup;
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.MessagesController$84$1 */
                    class C03571 implements Runnable {
                        C03571() {
                        }

                        public void run() {
                            TL_dialog dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(j);
                            if (dialog != null) {
                                if (i == 0 || i2 >= dialog.top_message) {
                                    dialog.unread_count = 0;
                                } else {
                                    dialog.unread_count = Math.max(dialog.unread_count - i, 0);
                                    if (i2 != Integer.MIN_VALUE && dialog.unread_count > dialog.top_message - i2) {
                                        dialog.unread_count = dialog.top_message - i2;
                                    }
                                }
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                            }
                            if (z) {
                                NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, j, 0, i2, true);
                                LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray(1);
                                dialogsToUpdate.put(j, Integer.valueOf(-1));
                                NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                                return;
                            }
                            NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, j, 0, i2, false);
                            dialogsToUpdate = new LongSparseArray(1);
                            dialogsToUpdate.put(j, Integer.valueOf(0));
                            NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                        }
                    }

                    public void run() {
                        AndroidUtilities.runOnUIThread(new C03571());
                    }
                });
                createReadTask = maxPositiveId != Integer.MAX_VALUE;
            } else {
                return;
            }
        } else if (maxDate != 0) {
            createReadTask = true;
            EncryptedChat chat = getEncryptedChat(Integer.valueOf(high_id));
            MessagesStorage.getInstance(this.currentAccount).processPendingRead(dialogId, (long) maxPositiveId, (long) maxNegativeId, maxDate, false);
            j = dialogId;
            i = maxDate;
            final boolean z2 = popup;
            final int i3 = countDiff;
            final int i4 = maxNegativeId;
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MessagesController$85$1 */
                class C03581 implements Runnable {
                    C03581() {
                    }

                    public void run() {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(null, j, i, 0, z2);
                        TL_dialog dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(j);
                        if (dialog != null) {
                            if (i3 == 0 || i4 <= dialog.top_message) {
                                dialog.unread_count = 0;
                            } else {
                                dialog.unread_count = Math.max(dialog.unread_count - i3, 0);
                                if (i4 != ConnectionsManager.DEFAULT_DATACENTER_ID && dialog.unread_count > i4 - dialog.top_message) {
                                    dialog.unread_count = i4 - dialog.top_message;
                                }
                            }
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(256));
                        }
                        LongSparseArray<Integer> dialogsToUpdate = new LongSparseArray(1);
                        dialogsToUpdate.put(j, Integer.valueOf(0));
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processDialogsUpdateRead(dialogsToUpdate);
                    }
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C03581());
                }
            });
            if (chat.ttl > 0) {
                int serverTime = Math.max(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime(), maxDate);
                MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(chat.id, serverTime, serverTime, 0, null);
            }
        } else {
            return;
        }
        if (createReadTask) {
            final long j2 = dialogId;
            final boolean z3 = readNow;
            final int i5 = maxDate;
            final int i6 = maxPositiveId;
            Utilities.stageQueue.postRunnable(new Runnable() {
                public void run() {
                    ReadTask currentReadTask = (ReadTask) MessagesController.this.readTasksMap.get(j2);
                    if (currentReadTask == null) {
                        currentReadTask = new ReadTask();
                        currentReadTask.dialogId = j2;
                        currentReadTask.sendRequestTime = SystemClock.uptimeMillis() + DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS;
                        if (!z3) {
                            MessagesController.this.readTasksMap.put(j2, currentReadTask);
                            MessagesController.this.readTasks.add(currentReadTask);
                        }
                    }
                    currentReadTask.maxDate = i5;
                    currentReadTask.maxId = i6;
                    if (z3) {
                        MessagesController.this.completeReadTask(currentReadTask);
                    }
                }
            });
        }
    }

    public int createChat(String title, ArrayList<Integer> selectedContacts, String about, int type, BaseFragment fragment) {
        int a;
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
            chatFull.notify_settings = new TL_peerNotifySettingsEmpty();
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
            r1 = fragment;
            r2 = req;
            return ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    if (error != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, error, r1, r2, new Object[0]);
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                            }
                        });
                        return;
                    }
                    final Updates updates = (Updates) response;
                    MessagesController.this.processUpdates(updates, false);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessagesController.this.putUsers(updates.users, false);
                            MessagesController.this.putChats(updates.chats, false);
                            if (updates.chats == null || updates.chats.isEmpty()) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                                return;
                            }
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(((Chat) updates.chats.get(0)).id));
                        }
                    });
                }
            }, 2);
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
            r1 = fragment;
            r2 = req;
            return ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    if (error != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, error, r1, r2, new Object[0]);
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                            }
                        });
                        return;
                    }
                    final Updates updates = (Updates) response;
                    MessagesController.this.processUpdates(updates, false);
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            MessagesController.this.putUsers(updates.users, false);
                            MessagesController.this.putChats(updates.chats, false);
                            if (updates.chats == null || updates.chats.isEmpty()) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidFailCreate, new Object[0]);
                                return;
                            }
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatDidCreated, Integer.valueOf(((Chat) updates.chats.get(0)).id));
                        }
                    });
                }
            }, 2);
        }
    }

    public void convertToMegaGroup(final Context context, int chat_id) {
        TL_messages_migrateChat req = new TL_messages_migrateChat();
        req.chat_id = chat_id;
        final AlertDialog progressDialog = new AlertDialog(context, 1);
        progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$89$1 */
            class C03631 implements Runnable {
                C03631() {
                }

                public void run() {
                    if (!((Activity) context).isFinishing()) {
                        try {
                            progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                    }
                }
            }

            /* renamed from: org.telegram.messenger.MessagesController$89$2 */
            class C03642 implements Runnable {
                C03642() {
                }

                public void run() {
                    if (!((Activity) context).isFinishing()) {
                        try {
                            progressDialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        Builder builder = new Builder(context);
                        builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
                        builder.setMessage(LocaleController.getString("ErrorOccurred", R.string.ErrorOccurred));
                        builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
                        builder.show().setCanceledOnTouchOutside(true);
                    }
                }
            }

            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    AndroidUtilities.runOnUIThread(new C03631());
                    Updates updates = (Updates) response;
                    MessagesController.this.processUpdates((Updates) response, false);
                    return;
                }
                AndroidUtilities.runOnUIThread(new C03642());
            }
        });
        progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(reqId, true);
                try {
                    dialog.dismiss();
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
            }
        });
        try {
            progressDialog.show();
        } catch (Exception e) {
        }
    }

    public void addUsersToChannel(int chat_id, ArrayList<InputUser> users, final BaseFragment fragment) {
        if (users != null && !users.isEmpty()) {
            final TL_channels_inviteToChannel req = new TL_channels_inviteToChannel();
            req.channel = getInputChannel(chat_id);
            req.users = users;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, final TL_error error) {
                    if (error != null) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                AlertsCreator.processError(MessagesController.this.currentAccount, error, fragment, req, Boolean.valueOf(true));
                            }
                        });
                    } else {
                        MessagesController.this.processUpdates((Updates) response, false);
                    }
                }
            });
        }
    }

    public void toogleChannelInvites(int chat_id, boolean enabled) {
        TL_channels_toggleInvites req = new TL_channels_toggleInvites();
        req.channel = getInputChannel(chat_id);
        req.enabled = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (response != null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                }
            }
        }, 64);
    }

    public void toogleChannelSignatures(int chat_id, boolean enabled) {
        TL_channels_toggleSignatures req = new TL_channels_toggleSignatures();
        req.channel = getInputChannel(chat_id);
        req.enabled = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$93$1 */
            class C03671 implements Runnable {
                C03671() {
                }

                public void run() {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL));
                }
            }

            public void run(TLObject response, TL_error error) {
                if (response != null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                    AndroidUtilities.runOnUIThread(new C03671());
                }
            }
        }, 64);
    }

    public void toogleChannelInvitesHistory(int chat_id, boolean enabled) {
        TL_channels_togglePreHistoryHidden req = new TL_channels_togglePreHistoryHidden();
        req.channel = getInputChannel(chat_id);
        req.enabled = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$94$1 */
            class C03681 implements Runnable {
                C03681() {
                }

                public void run() {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL));
                }
            }

            public void run(TLObject response, TL_error error) {
                if (response != null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                    AndroidUtilities.runOnUIThread(new C03681());
                }
            }
        }, 64);
    }

    public void updateChannelAbout(int chat_id, final String about, final ChatFull info) {
        if (info != null) {
            TL_channels_editAbout req = new TL_channels_editAbout();
            req.channel = getInputChannel(chat_id);
            req.about = about;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                /* renamed from: org.telegram.messenger.MessagesController$95$1 */
                class C03691 implements Runnable {
                    C03691() {
                    }

                    public void run() {
                        info.about = about;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatInfo(info, false);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, info, Integer.valueOf(0), Boolean.valueOf(false), null);
                    }
                }

                public void run(TLObject response, TL_error error) {
                    if (response instanceof TL_boolTrue) {
                        AndroidUtilities.runOnUIThread(new C03691());
                    }
                }
            }, 64);
        }
    }

    public void updateChannelUserName(final int chat_id, final String userName) {
        TL_channels_updateUsername req = new TL_channels_updateUsername();
        req.channel = getInputChannel(chat_id);
        req.username = userName;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

            /* renamed from: org.telegram.messenger.MessagesController$96$1 */
            class C03701 implements Runnable {
                C03701() {
                }

                public void run() {
                    Chat chat = MessagesController.this.getChat(Integer.valueOf(chat_id));
                    if (userName.length() != 0) {
                        chat.flags |= 64;
                    } else {
                        chat.flags &= -65;
                    }
                    chat.username = userName;
                    ArrayList<Chat> arrayList = new ArrayList();
                    arrayList.add(chat);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(null, arrayList, true, true);
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHANNEL));
                }
            }

            public void run(TLObject response, TL_error error) {
                if (response instanceof TL_boolTrue) {
                    AndroidUtilities.runOnUIThread(new C03701());
                }
            }
        }, 64);
    }

    public void sendBotStart(User user, String botHash) {
        if (user != null) {
            TL_messages_startBot req = new TL_messages_startBot();
            req.bot = getInputUser(user);
            req.peer = getInputPeer(user.id);
            req.start_param = botHash;
            req.random_id = Utilities.random.nextLong();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesController.this.processUpdates((Updates) response, false);
                    }
                }
            });
        }
    }

    public void toggleAdminMode(final int chat_id, boolean enabled) {
        TL_messages_toggleChatAdmins req = new TL_messages_toggleChatAdmins();
        req.chat_id = chat_id;
        req.enabled = enabled;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                    MessagesController.this.loadFullChat(chat_id, 0, true);
                }
            }
        });
    }

    public void toggleUserAdmin(int chat_id, int user_id, boolean admin) {
        TL_messages_editChatAdmin req = new TL_messages_editChatAdmin();
        req.chat_id = chat_id;
        req.user_id = getInputUser(user_id);
        req.is_admin = admin;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
            }
        });
    }

    public void addUserToChat(int chat_id, User user, ChatFull info, int count_fwd, String botHash, BaseFragment fragment) {
        if (user != null) {
            if (chat_id > 0) {
                TLObject request;
                final boolean isChannel = ChatObject.isChannel(chat_id, this.currentAccount);
                final boolean isMegagroup = isChannel && getChat(Integer.valueOf(chat_id)).megagroup;
                final InputUser inputUser = getInputUser(user);
                TLObject req;
                if (botHash != null && (!isChannel || isMegagroup)) {
                    req = new TL_messages_startBot();
                    req.bot = inputUser;
                    if (isChannel) {
                        req.peer = getInputPeer(-chat_id);
                    } else {
                        req.peer = new TL_inputPeerChat();
                        req.peer.chat_id = chat_id;
                    }
                    req.start_param = botHash;
                    req.random_id = Utilities.random.nextLong();
                    request = req;
                } else if (!isChannel) {
                    req = new TL_messages_addChatUser();
                    req.chat_id = chat_id;
                    req.fwd_limit = count_fwd;
                    req.user_id = inputUser;
                    request = req;
                } else if (!(inputUser instanceof TL_inputUserSelf)) {
                    req = new TL_channels_inviteToChannel();
                    req.channel = getInputChannel(chat_id);
                    req.users.add(inputUser);
                    request = req;
                } else if (!this.joiningToChannels.contains(Integer.valueOf(chat_id))) {
                    req = new TL_channels_joinChannel();
                    req.channel = getInputChannel(chat_id);
                    request = req;
                    this.joiningToChannels.add(Integer.valueOf(chat_id));
                } else {
                    return;
                }
                final int i = chat_id;
                final BaseFragment baseFragment = fragment;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$100$1 */
                    class C02791 implements Runnable {
                        C02791() {
                        }

                        public void run() {
                            MessagesController.this.joiningToChannels.remove(Integer.valueOf(i));
                        }
                    }

                    /* renamed from: org.telegram.messenger.MessagesController$100$3 */
                    class C02813 implements Runnable {
                        C02813() {
                        }

                        public void run() {
                            MessagesController.this.loadFullChat(i, 0, true);
                        }
                    }

                    public void run(TLObject response, final TL_error error) {
                        if (isChannel && (inputUser instanceof TL_inputUserSelf)) {
                            AndroidUtilities.runOnUIThread(new C02791());
                        }
                        if (error != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    boolean z = true;
                                    int access$000 = MessagesController.this.currentAccount;
                                    TL_error tL_error = error;
                                    BaseFragment baseFragment = baseFragment;
                                    TLObject tLObject = request;
                                    Object[] objArr = new Object[1];
                                    if (!isChannel || isMegagroup) {
                                        z = false;
                                    }
                                    objArr[0] = Boolean.valueOf(z);
                                    AlertsCreator.processError(access$000, tL_error, baseFragment, tLObject, objArr);
                                }
                            });
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
                        MessagesController.this.processUpdates(updates, false);
                        if (isChannel) {
                            if (!hasJoinMessage && (inputUser instanceof TL_inputUserSelf)) {
                                MessagesController.this.generateJoinMessage(i, true);
                            }
                            AndroidUtilities.runOnUIThread(new C02813(), 1000);
                        }
                        if (isChannel && (inputUser instanceof TL_inputUserSelf)) {
                            MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), null, true, i);
                        }
                    }
                });
            } else if (info instanceof TL_chatFull) {
                int a = 0;
                while (a < info.participants.participants.size()) {
                    if (((ChatParticipant) info.participants.participants.get(a)).user_id != user.id) {
                        a++;
                    } else {
                        return;
                    }
                }
                Chat chat = getChat(Integer.valueOf(chat_id));
                chat.participants_count++;
                ArrayList<Chat> chatArrayList = new ArrayList();
                chatArrayList.add(chat);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(null, chatArrayList, true, true);
                TL_chatParticipant newPart = new TL_chatParticipant();
                newPart.user_id = user.id;
                newPart.inviter_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                newPart.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
                info.participants.participants.add(0, newPart);
                MessagesStorage.getInstance(this.currentAccount).updateChatInfo(info, true);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, info, Integer.valueOf(0), Boolean.valueOf(false), null);
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(32));
            }
        }
    }

    public void deleteUserFromChat(int chat_id, User user, ChatFull info) {
        deleteUserFromChat(chat_id, user, info, false);
    }

    public void deleteUserFromChat(int chat_id, User user, ChatFull info, boolean forceDelete) {
        if (user != null) {
            Chat chat;
            if (chat_id > 0) {
                TLObject request;
                final InputUser inputUser = getInputUser(user);
                chat = getChat(Integer.valueOf(chat_id));
                final boolean isChannel = ChatObject.isChannel(chat);
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
                    req.banned_rights = new TL_channelBannedRights();
                    req.banned_rights.view_messages = true;
                    req.banned_rights.send_media = true;
                    req.banned_rights.send_messages = true;
                    req.banned_rights.send_stickers = true;
                    req.banned_rights.send_gifs = true;
                    req.banned_rights.send_games = true;
                    req.banned_rights.send_inline = true;
                    req.banned_rights.embed_links = true;
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
                final User user2 = user;
                final int i = chat_id;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$101$1 */
                    class C02821 implements Runnable {
                        C02821() {
                        }

                        public void run() {
                            MessagesController.this.deleteDialog((long) (-i), 0);
                        }
                    }

                    /* renamed from: org.telegram.messenger.MessagesController$101$2 */
                    class C02832 implements Runnable {
                        C02832() {
                        }

                        public void run() {
                            MessagesController.this.loadFullChat(i, 0, true);
                        }
                    }

                    public void run(TLObject response, TL_error error) {
                        if (user2.id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                            AndroidUtilities.runOnUIThread(new C02821());
                        }
                        if (error == null) {
                            MessagesController.this.processUpdates((Updates) response, false);
                            if (isChannel && !(inputUser instanceof TL_inputUserSelf)) {
                                AndroidUtilities.runOnUIThread(new C02832(), 1000);
                            }
                        }
                    }
                }, 64);
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
                    NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.chatInfoDidLoaded, info, Integer.valueOf(0), Boolean.valueOf(false), null);
                }
                NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(32));
            }
        }
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        MessagesController.this.processUpdates((Updates) response, false);
                    }
                }
            }, 64);
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

    public void changeChatAvatar(int chat_id, InputFile uploadedAvatar) {
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
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(request, new RequestDelegate() {
            public void run(TLObject response, TL_error error) {
                if (error == null) {
                    MessagesController.this.processUpdates((Updates) response, false);
                }
            }
        }, 64);
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
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
        }
    }

    public void performLogout(boolean byUser) {
        this.notificationsPreferences.edit().clear().commit();
        this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).putLong("lastStickersLoadTime", 0).putLong("lastStickersLoadTimeMask", 0).putLong("lastStickersLoadTimeFavs", 0).commit();
        this.mainPreferences.edit().remove("gifhint").commit();
        if (byUser) {
            unregistedPush();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_auth_logOut(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    ConnectionsManager.getInstance(MessagesController.this.currentAccount).cleanup();
                }
            });
        } else {
            ConnectionsManager.getInstance(this.currentAccount).cleanup();
        }
        UserConfig.getInstance(this.currentAccount).clearConfig();
        NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.appDidLogout, new Object[0]);
        MessagesStorage.getInstance(this.currentAccount).cleanup(false);
        cleanup();
        ContactsController.getInstance(this.currentAccount).deleteUnknownAppAccounts();
    }

    public void generateUpdateMessage() {
        if (!BuildVars.DEBUG_VERSION && SharedConfig.lastUpdateVersion != null && !SharedConfig.lastUpdateVersion.equals(BuildVars.BUILD_VERSION_STRING)) {
            TL_help_getAppChangelog req = new TL_help_getAppChangelog();
            req.prev_app_version = SharedConfig.lastUpdateVersion;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        SharedConfig.lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
                        SharedConfig.saveConfig();
                    }
                    if (response instanceof Updates) {
                        MessagesController.this.processUpdates((Updates) response, false);
                    }
                }
            });
        }
    }

    public void registerForPush(final String regid) {
        if (!TextUtils.isEmpty(regid) && !this.registeringForPush && UserConfig.getInstance(this.currentAccount).getClientUserId() != 0) {
            if (!UserConfig.getInstance(this.currentAccount).registeredForPush || !regid.equals(SharedConfig.pushString)) {
                this.registeringForPush = true;
                this.lastPushRegisterSendTime = SystemClock.uptimeMillis();
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
                        req.other_uids.add(Integer.valueOf(userConfig.getClientUserId()));
                    }
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {

                    /* renamed from: org.telegram.messenger.MessagesController$107$1 */
                    class C02841 implements Runnable {
                        C02841() {
                        }

                        public void run() {
                            MessagesController.this.registeringForPush = false;
                        }
                    }

                    public void run(TLObject response, TL_error error) {
                        if (response instanceof TL_boolTrue) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("account " + MessagesController.this.currentAccount + " registered for push");
                            }
                            UserConfig.getInstance(MessagesController.this.currentAccount).registeredForPush = true;
                            SharedConfig.pushString = regid;
                            UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                        }
                        AndroidUtilities.runOnUIThread(new C02841());
                    }
                });
            }
        }
    }

    public void loadCurrentState() {
        if (!this.updatingState) {
            this.updatingState = true;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_updates_getState(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    MessagesController.this.updatingState = false;
                    if (error == null) {
                        TL_updates_state res = (TL_updates_state) response;
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(res.date);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(res.pts);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(res.seq);
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(res.qts);
                        for (int a = 0; a < 3; a++) {
                            MessagesController.this.processUpdatesQueue(a, 2);
                        }
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).saveDiffParams(MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastQtsValue());
                    } else if (error.code != 401) {
                        MessagesController.this.loadCurrentState();
                    }
                }
            });
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
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
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
                    a--;
                } else if (updateState == 1) {
                    long updatesStartWaitTime = this.updatesStartWaitTimeChannels.get(channelId);
                    if (updatesStartWaitTime == 0 || (!anyProceed && Math.abs(System.currentTimeMillis() - updatesStartWaitTime) > 1500)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("HOLE IN CHANNEL " + channelId + " UPDATES QUEUE - getChannelDifference ");
                        }
                        this.updatesStartWaitTimeChannels.delete(channelId);
                        this.updatesQueueChannels.remove(channelId);
                        getChannelDifference(channelId);
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("HOLE IN CHANNEL " + channelId + " UPDATES QUEUE - will wait more time");
                    }
                    if (anyProceed) {
                        this.updatesStartWaitTimeChannels.put(channelId, System.currentTimeMillis());
                        return;
                    }
                    return;
                } else {
                    updatesQueue.remove(a);
                    a--;
                }
                a++;
            }
            this.updatesQueueChannels.remove(channelId);
            this.updatesStartWaitTimeChannels.delete(channelId);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("UPDATES CHANNEL " + channelId + " QUEUE PROCEED - OK");
            }
        }
    }

    private void processUpdatesQueue(int type, int state) {
        ArrayList<Updates> updatesQueue = null;
        if (type == 0) {
            updatesQueue = this.updatesQueueSeq;
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(MessagesController.this.getUpdateSeq(updates), MessagesController.this.getUpdateSeq(updates2));
                }
            });
        } else if (type == 1) {
            updatesQueue = this.updatesQueuePts;
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
        } else if (type == 2) {
            updatesQueue = this.updatesQueueQts;
            Collections.sort(updatesQueue, new Comparator<Updates>() {
                public int compare(Updates updates, Updates updates2) {
                    return AndroidUtilities.compare(updates.pts, updates2.pts);
                }
            });
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
                    a--;
                } else if (updateState != 1) {
                    updatesQueue.remove(a);
                    a--;
                } else if (getUpdatesStartTime(type) == 0 || (!anyProceed && Math.abs(System.currentTimeMillis() - getUpdatesStartTime(type)) > 1500)) {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("HOLE IN UPDATES QUEUE - getDifference");
                    }
                    setUpdatesStartTime(type, 0);
                    updatesQueue.clear();
                    getDifference();
                    return;
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("HOLE IN UPDATES QUEUE - will wait more time");
                    }
                    if (anyProceed) {
                        setUpdatesStartTime(type, System.currentTimeMillis());
                        return;
                    }
                    return;
                }
                a++;
            }
            updatesQueue.clear();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("UPDATES QUEUE PROCEED - OK");
            }
        }
        setUpdatesStartTime(type, 0);
    }

    protected void loadUnknownChannel(final Chat channel, long taskId) {
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
                            FileLog.m3e(e);
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                                public void run(TLObject response, TL_error error) {
                                    if (response != null) {
                                        TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
                                        if (!(res.dialogs.isEmpty() || res.chats.isEmpty())) {
                                            TL_messages_dialogs dialogs = new TL_messages_dialogs();
                                            dialogs.dialogs.addAll(res.dialogs);
                                            dialogs.messages.addAll(res.messages);
                                            dialogs.users.addAll(res.users);
                                            dialogs.chats.addAll(res.chats);
                                            MessagesController.this.processLoadedDialogs(dialogs, null, 0, 1, 2, false, false, false);
                                        }
                                    }
                                    if (newTaskId != 0) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                                    }
                                    MessagesController.this.gettingUnknownChannels.delete(channel.id);
                                }
                            });
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.m3e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
                    }
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                } else {
                    newTaskId = taskId;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
            } else if (taskId != 0) {
                MessagesStorage.getInstance(this.currentAccount).removePendingTask(taskId);
            }
        }
    }

    public void startShortPoll(final int channelId, final boolean stop) {
        Utilities.stageQueue.postRunnable(new Runnable() {
            public void run() {
                if (stop) {
                    MessagesController.this.needShortPollChannels.delete(channelId);
                    return;
                }
                MessagesController.this.needShortPollChannels.put(channelId, 0);
                if (MessagesController.this.shortPollChannels.indexOfKey(channelId) < 0) {
                    MessagesController.this.getChannelDifference(channelId, 3, 0, null);
                }
            }
        });
    }

    private void getChannelDifference(int channelId) {
        getChannelDifference(channelId, 0, 0, null);
    }

    public static boolean isSupportId(int id) {
        return id / 1000 == 777 || id == 333000 || id == 4240000 || id == 4240000 || id == 4244000 || id == 4245000 || id == 4246000 || id == 410000 || id == 420000 || id == 431000 || id == 431415000 || id == 434000 || id == 4243000 || id == 439000 || id == 449000 || id == 450000 || id == 452000 || id == 454000 || id == 4254000 || id == 455000 || id == 460000 || id == 470000 || id == 479000 || id == 796000 || id == 482000 || id == 490000 || id == 496000 || id == 497000 || id == 498000 || id == 4298000;
    }

    protected void getChannelDifference(int channelId, int newDialogType, long taskId, InputChannel inputChannel) {
        Throwable e;
        long newTaskId;
        TL_updates_getChannelDifference req;
        final int i;
        final int i2;
        if (!this.gettingDifferenceChannels.get(channelId)) {
            int channelPts;
            int limit = 100;
            if (newDialogType != 1) {
                channelPts = this.channelsPts.get(channelId);
                if (channelPts == 0) {
                    channelPts = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(channelId);
                    if (channelPts != 0) {
                        this.channelsPts.put(channelId, channelPts);
                    }
                    if (channelPts == 0 && (newDialogType == 2 || newDialogType == 3)) {
                        return;
                    }
                }
                if (channelPts == 0) {
                    return;
                }
            } else if (this.channelsPts.get(channelId) == 0) {
                channelPts = 1;
                limit = 1;
            } else {
                return;
            }
            if (inputChannel == null) {
                Chat chat = getChat(Integer.valueOf(channelId));
                if (chat == null) {
                    chat = MessagesStorage.getInstance(this.currentAccount).getChatSync(channelId);
                    if (chat != null) {
                        putChat(chat, true);
                    }
                }
                inputChannel = getInputChannel(chat);
            }
            if (inputChannel != null && inputChannel.access_hash != 0) {
                if (taskId == 0) {
                    NativeByteBuffer data = null;
                    try {
                        NativeByteBuffer data2 = new NativeByteBuffer(inputChannel.getObjectSize() + 12);
                        try {
                            data2.writeInt32(6);
                            data2.writeInt32(channelId);
                            data2.writeInt32(newDialogType);
                            inputChannel.serializeToStream(data2);
                            data = data2;
                        } catch (Exception e2) {
                            e = e2;
                            data = data2;
                            FileLog.m3e(e);
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                            this.gettingDifferenceChannels.put(channelId, true);
                            req = new TL_updates_getChannelDifference();
                            req.channel = inputChannel;
                            req.filter = new TL_channelMessagesFilterEmpty();
                            req.pts = channelPts;
                            req.limit = limit;
                            req.force = newDialogType == 3;
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("start getChannelDifference with pts = " + channelPts + " channelId = " + channelId);
                            }
                            i = channelId;
                            i2 = newDialogType;
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                                public void run(TLObject response, TL_error error) {
                                    if (error == null) {
                                        int a;
                                        final updates_ChannelDifference res = (updates_ChannelDifference) response;
                                        final SparseArray<User> usersDict = new SparseArray();
                                        for (a = 0; a < res.users.size(); a++) {
                                            User user = (User) res.users.get(a);
                                            usersDict.put(user.id, user);
                                        }
                                        Chat channel = null;
                                        for (a = 0; a < res.chats.size(); a++) {
                                            Chat chat = (Chat) res.chats.get(a);
                                            if (chat.id == i) {
                                                channel = chat;
                                                break;
                                            }
                                        }
                                        final Chat channelFinal = channel;
                                        final ArrayList<TL_updateMessageID> msgUpdates = new ArrayList();
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
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                MessagesController.this.putUsers(res.users, false);
                                                MessagesController.this.putChats(res.chats, false);
                                            }
                                        });
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                            /* renamed from: org.telegram.messenger.MessagesController$115$2$2 */
                                            class C02902 implements Runnable {
                                                C02902() {
                                                }

                                                public void run() {
                                                    long dialog_id;
                                                    Integer inboxValue;
                                                    Integer outboxValue;
                                                    int a;
                                                    Message message;
                                                    Integer num;
                                                    boolean z;
                                                    if ((res instanceof TL_updates_channelDifference) || (res instanceof TL_updates_channelDifferenceEmpty)) {
                                                        if (!res.new_messages.isEmpty()) {
                                                            final LongSparseArray<ArrayList<MessageObject>> messages = new LongSparseArray();
                                                            ImageLoader.saveMessagesThumbs(res.new_messages);
                                                            final ArrayList<MessageObject> pushMessages = new ArrayList();
                                                            dialog_id = (long) (-i);
                                                            inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                                            if (inboxValue == null) {
                                                                inboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, dialog_id));
                                                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), inboxValue);
                                                            }
                                                            outboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                                                            if (outboxValue == null) {
                                                                outboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, dialog_id));
                                                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), outboxValue);
                                                            }
                                                            for (a = 0; a < res.new_messages.size(); a++) {
                                                                MessageObject obj;
                                                                long uid;
                                                                ArrayList<MessageObject> arr;
                                                                message = (Message) res.new_messages.get(a);
                                                                if (channelFinal == null || !channelFinal.left) {
                                                                    if (message.out) {
                                                                        num = outboxValue;
                                                                    } else {
                                                                        num = inboxValue;
                                                                    }
                                                                    if (num.intValue() < message.id && !(message.action instanceof TL_messageActionChannelCreate)) {
                                                                        z = true;
                                                                        message.unread = z;
                                                                        if (channelFinal != null && channelFinal.megagroup) {
                                                                            message.flags |= Integer.MIN_VALUE;
                                                                        }
                                                                        obj = new MessageObject(MessagesController.this.currentAccount, message, usersDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(dialog_id)));
                                                                        if (!obj.isOut() && obj.isUnread()) {
                                                                            pushMessages.add(obj);
                                                                        }
                                                                        uid = (long) (-i);
                                                                        arr = (ArrayList) messages.get(uid);
                                                                        if (arr == null) {
                                                                            arr = new ArrayList();
                                                                            messages.put(uid, arr);
                                                                        }
                                                                        arr.add(obj);
                                                                    }
                                                                }
                                                                z = false;
                                                                message.unread = z;
                                                                message.flags |= Integer.MIN_VALUE;
                                                                obj = new MessageObject(MessagesController.this.currentAccount, message, usersDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(dialog_id)));
                                                                pushMessages.add(obj);
                                                                uid = (long) (-i);
                                                                arr = (ArrayList) messages.get(uid);
                                                                if (arr == null) {
                                                                    arr = new ArrayList();
                                                                    messages.put(uid, arr);
                                                                }
                                                                arr.add(obj);
                                                            }
                                                            AndroidUtilities.runOnUIThread(new Runnable() {
                                                                public void run() {
                                                                    for (int a = 0; a < messages.size(); a++) {
                                                                        MessagesController.this.updateInterfaceWithMessages(messages.keyAt(a), (ArrayList) messages.valueAt(a));
                                                                    }
                                                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                                                }
                                                            });
                                                            MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                                                /* renamed from: org.telegram.messenger.MessagesController$115$2$2$2$1 */
                                                                class C02881 implements Runnable {
                                                                    C02881() {
                                                                    }

                                                                    public void run() {
                                                                        NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(pushMessages, true, false);
                                                                    }
                                                                }

                                                                public void run() {
                                                                    if (!pushMessages.isEmpty()) {
                                                                        AndroidUtilities.runOnUIThread(new C02881());
                                                                    }
                                                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(res.new_messages, true, false, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                                                                }
                                                            });
                                                        }
                                                        if (!res.other_updates.isEmpty()) {
                                                            MessagesController.this.processUpdateArray(res.other_updates, res.users, res.chats, true);
                                                        }
                                                        MessagesController.this.processChannelsUpdatesQueue(i, 1);
                                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).saveChannelPts(i, res.pts);
                                                    } else if (res instanceof TL_updates_channelDifferenceTooLong) {
                                                        dialog_id = (long) (-i);
                                                        inboxValue = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                                                        if (inboxValue == null) {
                                                            inboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(false, dialog_id));
                                                            MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), inboxValue);
                                                        }
                                                        outboxValue = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                                                        if (outboxValue == null) {
                                                            outboxValue = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(true, dialog_id));
                                                            MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), outboxValue);
                                                        }
                                                        for (a = 0; a < res.messages.size(); a++) {
                                                            message = (Message) res.messages.get(a);
                                                            message.dialog_id = (long) (-i);
                                                            if (!(message.action instanceof TL_messageActionChannelCreate) && (channelFinal == null || !channelFinal.left)) {
                                                                if (message.out) {
                                                                    num = outboxValue;
                                                                } else {
                                                                    num = inboxValue;
                                                                }
                                                                if (num.intValue() < message.id) {
                                                                    z = true;
                                                                    message.unread = z;
                                                                    if (channelFinal != null && channelFinal.megagroup) {
                                                                        message.flags |= Integer.MIN_VALUE;
                                                                    }
                                                                }
                                                            }
                                                            z = false;
                                                            message.unread = z;
                                                            message.flags |= Integer.MIN_VALUE;
                                                        }
                                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).overwriteChannel(i, (TL_updates_channelDifferenceTooLong) res, i2);
                                                    }
                                                    MessagesController.this.gettingDifferenceChannels.delete(i);
                                                    MessagesController.this.channelsPts.put(i, res.pts);
                                                    if ((res.flags & 2) != 0) {
                                                        MessagesController.this.shortPollChannels.put(i, ((int) (System.currentTimeMillis() / 1000)) + res.timeout);
                                                    }
                                                    if (!res.isFinal) {
                                                        MessagesController.this.getChannelDifference(i);
                                                    }
                                                    if (BuildVars.LOGS_ENABLED) {
                                                        FileLog.m0d("received channel difference with pts = " + res.pts + " channelId = " + i);
                                                        FileLog.m0d("new_messages = " + res.new_messages.size() + " messages = " + res.messages.size() + " users = " + res.users.size() + " chats = " + res.chats.size() + " other updates = " + res.other_updates.size());
                                                    }
                                                    if (newTaskId != 0) {
                                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                                                    }
                                                }
                                            }

                                            public void run() {
                                                if (!msgUpdates.isEmpty()) {
                                                    final SparseArray<long[]> corrected = new SparseArray();
                                                    Iterator it = msgUpdates.iterator();
                                                    while (it.hasNext()) {
                                                        TL_updateMessageID update = (TL_updateMessageID) it.next();
                                                        long[] ids = MessagesStorage.getInstance(MessagesController.this.currentAccount).updateMessageStateAndId(update.random_id, null, update.id, 0, false, i);
                                                        if (ids != null) {
                                                            corrected.put(update.id, ids);
                                                        }
                                                    }
                                                    if (corrected.size() != 0) {
                                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                                            public void run() {
                                                                for (int a = 0; a < corrected.size(); a++) {
                                                                    int newId = corrected.keyAt(a);
                                                                    SendMessagesHelper.getInstance(MessagesController.this.currentAccount).processSentMessage((int) ((long[]) corrected.valueAt(a))[1]);
                                                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newId), null, Long.valueOf(ids[0]));
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                                Utilities.stageQueue.postRunnable(new C02902());
                                            }
                                        });
                                        return;
                                    }
                                    final TL_error tL_error = error;
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.this.checkChannelError(tL_error.text, i);
                                        }
                                    });
                                    MessagesController.this.gettingDifferenceChannels.delete(i);
                                    if (newTaskId != 0) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                                    }
                                }
                            });
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.m3e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        this.gettingDifferenceChannels.put(channelId, true);
                        req = new TL_updates_getChannelDifference();
                        req.channel = inputChannel;
                        req.filter = new TL_channelMessagesFilterEmpty();
                        req.pts = channelPts;
                        req.limit = limit;
                        if (newDialogType == 3) {
                        }
                        req.force = newDialogType == 3;
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("start getChannelDifference with pts = " + channelPts + " channelId = " + channelId);
                        }
                        i = channelId;
                        i2 = newDialogType;
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
                    }
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                } else {
                    newTaskId = taskId;
                }
                this.gettingDifferenceChannels.put(channelId, true);
                req = new TL_updates_getChannelDifference();
                req.channel = inputChannel;
                req.filter = new TL_channelMessagesFilterEmpty();
                req.pts = channelPts;
                req.limit = limit;
                if (newDialogType == 3) {
                }
                req.force = newDialogType == 3;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("start getChannelDifference with pts = " + channelPts + " channelId = " + channelId);
                }
                i = channelId;
                i2 = newDialogType;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
            } else if (taskId != 0) {
                MessagesStorage.getInstance(this.currentAccount).removePendingTask(taskId);
            }
        }
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

    public void getDifference(int pts, final int date, final int qts, boolean slice) {
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
                if (ConnectionsManager.isConnectedOrConnectingToWiFi()) {
                    req.pts_total_limit = DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS;
                } else {
                    req.pts_total_limit = 1000;
                }
                this.getDifferenceFirstSync = false;
            }
            if (req.date == 0) {
                req.date = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("start getDifference with date = " + date + " pts = " + pts + " qts = " + qts);
            }
            ConnectionsManager.getInstance(this.currentAccount).setIsUpdating(true);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (error == null) {
                        final updates_Difference res = (updates_Difference) response;
                        if (res instanceof TL_updates_differenceTooLong) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    MessagesController.this.loadedFullUsers.clear();
                                    MessagesController.this.loadedFullChats.clear();
                                    MessagesController.this.resetDialogs(true, MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), res.pts, date, qts);
                                }
                            });
                            return;
                        }
                        int a;
                        if (res instanceof TL_updates_differenceSlice) {
                            MessagesController.this.getDifference(res.intermediate_state.pts, res.intermediate_state.date, res.intermediate_state.qts, true);
                        }
                        final SparseArray<User> usersDict = new SparseArray();
                        final SparseArray<Chat> chatsDict = new SparseArray();
                        for (a = 0; a < res.users.size(); a++) {
                            User user = (User) res.users.get(a);
                            usersDict.put(user.id, user);
                        }
                        for (a = 0; a < res.chats.size(); a++) {
                            Chat chat = (Chat) res.chats.get(a);
                            chatsDict.put(chat.id, chat);
                        }
                        final ArrayList<TL_updateMessageID> msgUpdates = new ArrayList();
                        if (!res.other_updates.isEmpty()) {
                            a = 0;
                            while (a < res.other_updates.size()) {
                                Update upd = (Update) res.other_updates.get(a);
                                if (upd instanceof TL_updateMessageID) {
                                    msgUpdates.add((TL_updateMessageID) upd);
                                    res.other_updates.remove(a);
                                    a--;
                                } else if (MessagesController.this.getUpdateType(upd) == 2) {
                                    int channelId = MessagesController.getUpdateChannelId(upd);
                                    int channelPts = MessagesController.this.channelsPts.get(channelId);
                                    if (channelPts == 0) {
                                        channelPts = MessagesStorage.getInstance(MessagesController.this.currentAccount).getChannelPtsSync(channelId);
                                        if (channelPts != 0) {
                                            MessagesController.this.channelsPts.put(channelId, channelPts);
                                        }
                                    }
                                    if (channelPts != 0 && MessagesController.getUpdatePts(upd) <= channelPts) {
                                        res.other_updates.remove(a);
                                        a--;
                                    }
                                }
                                a++;
                            }
                        }
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                MessagesController.this.loadedFullUsers.clear();
                                MessagesController.this.loadedFullChats.clear();
                                MessagesController.this.putUsers(res.users, false);
                                MessagesController.this.putChats(res.chats, false);
                            }
                        });
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.MessagesController$116$3$2 */
                            class C02992 implements Runnable {
                                C02992() {
                                }

                                public void run() {
                                    int a;
                                    if (!(res.new_messages.isEmpty() && res.new_encrypted_messages.isEmpty())) {
                                        final LongSparseArray<ArrayList<MessageObject>> messages = new LongSparseArray();
                                        for (int b = 0; b < res.new_encrypted_messages.size(); b++) {
                                            ArrayList<Message> decryptedMessages = SecretChatHelper.getInstance(MessagesController.this.currentAccount).decryptMessage((EncryptedMessage) res.new_encrypted_messages.get(b));
                                            if (!(decryptedMessages == null || decryptedMessages.isEmpty())) {
                                                res.new_messages.addAll(decryptedMessages);
                                            }
                                        }
                                        ImageLoader.saveMessagesThumbs(res.new_messages);
                                        final ArrayList<MessageObject> pushMessages = new ArrayList();
                                        int clientUserId = UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId();
                                        for (a = 0; a < res.new_messages.size(); a++) {
                                            Message message = (Message) res.new_messages.get(a);
                                            if (message.dialog_id == 0) {
                                                if (message.to_id.chat_id != 0) {
                                                    message.dialog_id = (long) (-message.to_id.chat_id);
                                                } else {
                                                    if (message.to_id.user_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
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
                                                    ConcurrentHashMap<Long, Integer> read_max = message.out ? MessagesController.this.dialogs_read_outbox_max : MessagesController.this.dialogs_read_inbox_max;
                                                    Integer value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                                                    if (value == null) {
                                                        value = Integer.valueOf(MessagesStorage.getInstance(MessagesController.this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
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
                                            MessageObject obj = new MessageObject(MessagesController.this.currentAccount, message, usersDict, chatsDict, MessagesController.this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
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
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                for (int a = 0; a < messages.size(); a++) {
                                                    MessagesController.this.updateInterfaceWithMessages(messages.keyAt(a), (ArrayList) messages.valueAt(a));
                                                }
                                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                            }
                                        });
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                            /* renamed from: org.telegram.messenger.MessagesController$116$3$2$2$1 */
                                            class C02971 implements Runnable {
                                                C02971() {
                                                }

                                                public void run() {
                                                    boolean z;
                                                    NotificationsController instance = NotificationsController.getInstance(MessagesController.this.currentAccount);
                                                    ArrayList arrayList = pushMessages;
                                                    if (res instanceof TL_updates_differenceSlice) {
                                                        z = false;
                                                    } else {
                                                        z = true;
                                                    }
                                                    instance.processNewMessages(arrayList, z, false);
                                                }
                                            }

                                            public void run() {
                                                if (!pushMessages.isEmpty()) {
                                                    AndroidUtilities.runOnUIThread(new C02971());
                                                }
                                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(res.new_messages, true, false, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                                            }
                                        });
                                        SecretChatHelper.getInstance(MessagesController.this.currentAccount).processPendingEncMessages();
                                    }
                                    if (!res.other_updates.isEmpty()) {
                                        MessagesController.this.processUpdateArray(res.other_updates, res.users, res.chats, true);
                                    }
                                    if (res instanceof TL_updates_difference) {
                                        MessagesController.this.gettingDifference = false;
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(res.state.seq);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(res.state.date);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(res.state.pts);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(res.state.qts);
                                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                                        for (a = 0; a < 3; a++) {
                                            MessagesController.this.processUpdatesQueue(a, 1);
                                        }
                                    } else if (res instanceof TL_updates_differenceSlice) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(res.intermediate_state.date);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastPtsValue(res.intermediate_state.pts);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastQtsValue(res.intermediate_state.qts);
                                    } else if (res instanceof TL_updates_differenceEmpty) {
                                        MessagesController.this.gettingDifference = false;
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastSeqValue(res.seq);
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).setLastDateValue(res.date);
                                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                                        for (a = 0; a < 3; a++) {
                                            MessagesController.this.processUpdatesQueue(a, 1);
                                        }
                                    }
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).saveDiffParams(MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastQtsValue());
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("received difference with date = " + MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastDateValue() + " pts = " + MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastPtsValue() + " seq = " + MessagesStorage.getInstance(MessagesController.this.currentAccount).getLastSeqValue() + " messages = " + res.new_messages.size() + " users = " + res.users.size() + " chats = " + res.chats.size() + " other updates = " + res.other_updates.size());
                                    }
                                }
                            }

                            public void run() {
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, res.chats, true, false);
                                if (!msgUpdates.isEmpty()) {
                                    final SparseArray<long[]> corrected = new SparseArray();
                                    for (int a = 0; a < msgUpdates.size(); a++) {
                                        TL_updateMessageID update = (TL_updateMessageID) msgUpdates.get(a);
                                        long[] ids = MessagesStorage.getInstance(MessagesController.this.currentAccount).updateMessageStateAndId(update.random_id, null, update.id, 0, false, 0);
                                        if (ids != null) {
                                            corrected.put(update.id, ids);
                                        }
                                    }
                                    if (corrected.size() != 0) {
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            public void run() {
                                                for (int a = 0; a < corrected.size(); a++) {
                                                    int newId = corrected.keyAt(a);
                                                    SendMessagesHelper.getInstance(MessagesController.this.currentAccount).processSentMessage((int) ((long[]) corrected.valueAt(a))[1]);
                                                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messageReceivedByServer, Integer.valueOf(oldId), Integer.valueOf(newId), null, Long.valueOf(ids[0]));
                                                }
                                            }
                                        });
                                    }
                                }
                                Utilities.stageQueue.postRunnable(new C02992());
                            }
                        });
                        return;
                    }
                    MessagesController.this.gettingDifference = false;
                    ConnectionsManager.getInstance(MessagesController.this.currentAccount).setIsUpdating(false);
                }
            });
        }
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
            if (!pin && this.dialogs.get(this.dialogs.size() - 1) == dialog) {
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
                            data2.writeInt32(1);
                            data2.writeInt64(did);
                            data2.writeBool(pin);
                            peer.serializeToStream(data2);
                            data = data2;
                        } catch (Exception e2) {
                            e = e2;
                            data = data2;
                            FileLog.m3e(e);
                            newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                                public void run(TLObject response, TL_error error) {
                                    if (newTaskId != 0) {
                                        MessagesStorage.getInstance(MessagesController.this.currentAccount).removePendingTask(newTaskId);
                                    }
                                }
                            });
                            MessagesStorage.getInstance(this.currentAccount).setDialogPinned(did, dialog.pinnedNum);
                            return true;
                        }
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.m3e(e);
                        newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
                        MessagesStorage.getInstance(this.currentAccount).setDialogPinned(did, dialog.pinnedNum);
                        return true;
                    }
                    newTaskId = MessagesStorage.getInstance(this.currentAccount).createPendingTask(data);
                } else {
                    newTaskId = taskId;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, /* anonymous class already generated */);
            }
            MessagesStorage.getInstance(this.currentAccount).setDialogPinned(did, dialog.pinnedNum);
            return true;
        } else if (dialog != null) {
            return true;
        } else {
            return false;
        }
    }

    public void loadPinnedDialogs(final long newDialogId, final ArrayList<Long> order) {
        if (!UserConfig.getInstance(this.currentAccount).pinnedDialogsLoaded) {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_messages_getPinnedDialogs(), new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                    if (response != null) {
                        int a;
                        Chat chat;
                        final TL_messages_peerDialogs res = (TL_messages_peerDialogs) response;
                        final TL_messages_dialogs toCache = new TL_messages_dialogs();
                        toCache.users.addAll(res.users);
                        toCache.chats.addAll(res.chats);
                        toCache.dialogs.addAll(res.dialogs);
                        toCache.messages.addAll(res.messages);
                        final LongSparseArray<MessageObject> new_dialogMessage = new LongSparseArray();
                        SparseArray usersDict = new SparseArray();
                        SparseArray chatsDict = new SparseArray();
                        final ArrayList<Long> newPinnedOrder = new ArrayList();
                        for (a = 0; a < res.users.size(); a++) {
                            User u = (User) res.users.get(a);
                            usersDict.put(u.id, u);
                        }
                        for (a = 0; a < res.chats.size(); a++) {
                            Chat c = (Chat) res.chats.get(a);
                            chatsDict.put(c.id, c);
                        }
                        for (a = 0; a < res.messages.size(); a++) {
                            Message message = (Message) res.messages.get(a);
                            MessageObject messageObject;
                            if (message.to_id.channel_id != 0) {
                                chat = (Chat) chatsDict.get(message.to_id.channel_id);
                                if (chat != null && chat.left) {
                                }
                                messageObject = new MessageObject(MessagesController.this.currentAccount, message, usersDict, chatsDict, false);
                                new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                            } else {
                                if (message.to_id.chat_id != 0) {
                                    chat = (Chat) chatsDict.get(message.to_id.chat_id);
                                    if (!(chat == null || chat.migrated_to == null)) {
                                    }
                                }
                                messageObject = new MessageObject(MessagesController.this.currentAccount, message, usersDict, chatsDict, false);
                                new_dialogMessage.put(messageObject.getDialogId(), messageObject);
                            }
                        }
                        for (a = 0; a < res.dialogs.size(); a++) {
                            TL_dialog d = (TL_dialog) res.dialogs.get(a);
                            if (d.id == 0) {
                                if (d.peer.user_id != 0) {
                                    d.id = (long) d.peer.user_id;
                                } else if (d.peer.chat_id != 0) {
                                    d.id = (long) (-d.peer.chat_id);
                                } else if (d.peer.channel_id != 0) {
                                    d.id = (long) (-d.peer.channel_id);
                                }
                            }
                            newPinnedOrder.add(Long.valueOf(d.id));
                            MessageObject mess;
                            Integer value;
                            if (DialogObject.isChannel(d)) {
                                chat = (Chat) chatsDict.get(-((int) d.id));
                                if (chat != null && chat.left) {
                                }
                                if (d.last_message_date == 0) {
                                    mess = (MessageObject) new_dialogMessage.get(d.id);
                                    if (mess != null) {
                                        d.last_message_date = mess.messageOwner.date;
                                    }
                                }
                                value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                                if (value == null) {
                                    value = Integer.valueOf(0);
                                }
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                                value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                                if (value == null) {
                                    value = Integer.valueOf(0);
                                }
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                            } else {
                                if (((int) d.id) < 0) {
                                    chat = (Chat) chatsDict.get(-((int) d.id));
                                    if (!(chat == null || chat.migrated_to == null)) {
                                    }
                                }
                                if (d.last_message_date == 0) {
                                    mess = (MessageObject) new_dialogMessage.get(d.id);
                                    if (mess != null) {
                                        d.last_message_date = mess.messageOwner.date;
                                    }
                                }
                                value = (Integer) MessagesController.this.dialogs_read_inbox_max.get(Long.valueOf(d.id));
                                if (value == null) {
                                    value = Integer.valueOf(0);
                                }
                                MessagesController.this.dialogs_read_inbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_inbox_max_id)));
                                value = (Integer) MessagesController.this.dialogs_read_outbox_max.get(Long.valueOf(d.id));
                                if (value == null) {
                                    value = Integer.valueOf(0);
                                }
                                MessagesController.this.dialogs_read_outbox_max.put(Long.valueOf(d.id), Integer.valueOf(Math.max(value.intValue(), d.read_outbox_max_id)));
                            }
                        }
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                            /* renamed from: org.telegram.messenger.MessagesController$118$1$1 */
                            class C03011 implements Runnable {
                                C03011() {
                                }

                                public void run() {
                                    int a;
                                    MessagesController.this.applyDialogsNotificationsSettings(res.dialogs);
                                    boolean changed = false;
                                    boolean added = false;
                                    int maxPinnedNum = 0;
                                    LongSparseArray<Integer> oldPinnedDialogNums = new LongSparseArray();
                                    ArrayList<Long> oldPinnedOrder = new ArrayList();
                                    for (a = 0; a < MessagesController.this.dialogs.size(); a++) {
                                        TL_dialog dialog = (TL_dialog) MessagesController.this.dialogs.get(a);
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
                                    ArrayList<Long> orderArrayList = order != null ? order : newPinnedOrder;
                                    if (orderArrayList.size() < oldPinnedOrder.size()) {
                                        orderArrayList.add(Long.valueOf(0));
                                    }
                                    while (oldPinnedOrder.size() < orderArrayList.size()) {
                                        oldPinnedOrder.add(0, Long.valueOf(0));
                                    }
                                    if (!res.dialogs.isEmpty()) {
                                        MessagesController.this.putUsers(res.users, false);
                                        MessagesController.this.putChats(res.chats, false);
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
                                            TL_dialog d = (TL_dialog) MessagesController.this.dialogs_dict.get(dialog.id);
                                            if (d != null) {
                                                d.pinned = true;
                                                d.pinnedNum = dialog.pinnedNum;
                                                MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogPinned(dialog.id, dialog.pinnedNum);
                                            } else {
                                                added = true;
                                                MessagesController.this.dialogs_dict.put(dialog.id, dialog);
                                                MessageObject messageObject = (MessageObject) new_dialogMessage.get(dialog.id);
                                                MessagesController.this.dialogMessage.put(dialog.id, messageObject);
                                                if (messageObject != null && messageObject.messageOwner.to_id.channel_id == 0) {
                                                    MessagesController.this.dialogMessagesByIds.put(messageObject.getId(), messageObject);
                                                    if (messageObject.messageOwner.random_id != 0) {
                                                        MessagesController.this.dialogMessagesByRandomIds.put(messageObject.messageOwner.random_id, messageObject);
                                                    }
                                                }
                                            }
                                            changed = true;
                                        }
                                    }
                                    if (changed) {
                                        if (added) {
                                            MessagesController.this.dialogs.clear();
                                            int size = MessagesController.this.dialogs_dict.size();
                                            for (a = 0; a < size; a++) {
                                                MessagesController.this.dialogs.add(MessagesController.this.dialogs_dict.valueAt(a));
                                            }
                                        }
                                        MessagesController.this.sortDialogs(null);
                                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                    }
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).unpinAllDialogsExceptNew(pinnedDialogs);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putDialogs(toCache, true);
                                    UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = true;
                                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                                }
                            }

                            public void run() {
                                AndroidUtilities.runOnUIThread(new C03011());
                            }
                        });
                    }
                }
            });
        }
    }

    public void generateJoinMessage(final int chat_id, boolean ignoreLeft) {
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
                final ArrayList<MessageObject> pushMessages = new ArrayList();
                ArrayList messagesArr = new ArrayList();
                messagesArr.add(message);
                pushMessages.add(new MessageObject(this.currentAccount, message, true));
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.MessagesController$119$1 */
                    class C03031 implements Runnable {
                        C03031() {
                        }

                        public void run() {
                            NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(pushMessages, true, false);
                        }
                    }

                    public void run() {
                        AndroidUtilities.runOnUIThread(new C03031());
                    }
                });
                MessagesStorage.getInstance(this.currentAccount).putMessages(messagesArr, true, true, false, 0);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        MessagesController.this.updateInterfaceWithMessages((long) (-chat_id), pushMessages);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                    }
                });
            }
        }
    }

    public void checkChannelInviter(final int chat_id) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            public void run() {
                final Chat chat = MessagesController.this.getChat(Integer.valueOf(chat_id));
                if (chat != null && ChatObject.isChannel(chat_id, MessagesController.this.currentAccount) && !chat.creator) {
                    TL_channels_getParticipant req = new TL_channels_getParticipant();
                    req.channel = MessagesController.this.getInputChannel(chat_id);
                    req.user_id = new TL_inputUserSelf();
                    ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(req, new RequestDelegate() {
                        public void run(TLObject response, TL_error error) {
                            final TL_channels_channelParticipant res = (TL_channels_channelParticipant) response;
                            if (res != null && (res.participant instanceof TL_channelParticipantSelf) && res.participant.inviter_id != UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                                if (!chat.megagroup || !MessagesStorage.getInstance(MessagesController.this.currentAccount).isMigratedChat(chat.id)) {
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.this.putUsers(res.users, false);
                                        }
                                    });
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, null, true, true);
                                    Message message = new TL_messageService();
                                    message.media_unread = true;
                                    message.unread = true;
                                    message.flags = 256;
                                    message.post = true;
                                    if (chat.megagroup) {
                                        message.flags |= Integer.MIN_VALUE;
                                    }
                                    int newMessageId = UserConfig.getInstance(MessagesController.this.currentAccount).getNewMessageId();
                                    message.id = newMessageId;
                                    message.local_id = newMessageId;
                                    message.date = res.participant.date;
                                    message.action = new TL_messageActionChatAddUser();
                                    message.from_id = res.participant.inviter_id;
                                    message.action.users.add(Integer.valueOf(UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()));
                                    message.to_id = new TL_peerChannel();
                                    message.to_id.channel_id = chat_id;
                                    message.dialog_id = (long) (-chat_id);
                                    UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                                    final ArrayList<MessageObject> pushMessages = new ArrayList();
                                    ArrayList messagesArr = new ArrayList();
                                    AbstractMap usersDict = new ConcurrentHashMap();
                                    for (int a = 0; a < res.users.size(); a++) {
                                        User user = (User) res.users.get(a);
                                        usersDict.put(Integer.valueOf(user.id), user);
                                    }
                                    messagesArr.add(message);
                                    pushMessages.add(new MessageObject(MessagesController.this.currentAccount, message, usersDict, true));
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                                        /* renamed from: org.telegram.messenger.MessagesController$121$1$2$1 */
                                        class C03051 implements Runnable {
                                            C03051() {
                                            }

                                            public void run() {
                                                NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(pushMessages, true, false);
                                            }
                                        }

                                        public void run() {
                                            AndroidUtilities.runOnUIThread(new C03051());
                                        }
                                    });
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(messagesArr, true, true, false, 0);
                                    AndroidUtilities.runOnUIThread(new Runnable() {
                                        public void run() {
                                            MessagesController.this.updateInterfaceWithMessages((long) (-chat_id), pushMessages);
                                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
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
            FileLog.m1e("trying to get unknown update channel_id for " + update);
        }
        return 0;
    }

    public void processUpdates(Updates updates, boolean fromQueue) {
        int a;
        ArrayList<Integer> needGetChannelsDiff = null;
        boolean needGetDiff = false;
        boolean needReceivedQueue = false;
        boolean updateStatus = false;
        if (updates instanceof TL_updateShort) {
            ArrayList<Update> arr = new ArrayList();
            arr.add(updates.update);
            processUpdateArray(arr, null, null, false);
        } else if ((updates instanceof TL_updateShortChatMessage) || (updates instanceof TL_updateShortMessage)) {
            int user_id;
            boolean missingData;
            if (updates instanceof TL_updateShortChatMessage) {
                user_id = updates.from_id;
            } else {
                user_id = updates.user_id;
            }
            User user = getUser(Integer.valueOf(user_id));
            User user2 = null;
            User user3 = null;
            Chat channel = null;
            if (user == null || user.min) {
                user = MessagesStorage.getInstance(this.currentAccount).getUserSync(user_id);
                if (user != null && user.min) {
                    user = null;
                }
                putUser(user, true);
            }
            boolean needFwdUser = false;
            if (updates.fwd_from != null) {
                if (updates.fwd_from.from_id != 0) {
                    user2 = getUser(Integer.valueOf(updates.fwd_from.from_id));
                    if (user2 == null) {
                        user2 = MessagesStorage.getInstance(this.currentAccount).getUserSync(updates.fwd_from.from_id);
                        putUser(user2, true);
                    }
                    needFwdUser = true;
                }
                if (updates.fwd_from.channel_id != 0) {
                    channel = getChat(Integer.valueOf(updates.fwd_from.channel_id));
                    if (channel == null) {
                        channel = MessagesStorage.getInstance(this.currentAccount).getChatSync(updates.fwd_from.channel_id);
                        putChat(channel, true);
                    }
                    needFwdUser = true;
                }
            }
            boolean needBotUser = false;
            if (updates.via_bot_id != 0) {
                user3 = getUser(Integer.valueOf(updates.via_bot_id));
                if (user3 == null) {
                    user3 = MessagesStorage.getInstance(this.currentAccount).getUserSync(updates.via_bot_id);
                    putUser(user3, true);
                }
                needBotUser = true;
            }
            if (updates instanceof TL_updateShortMessage) {
                missingData = user == null || ((needFwdUser && user2 == null && channel == null) || (needBotUser && user3 == null));
            } else {
                chat = getChat(Integer.valueOf(updates.chat_id));
                if (chat == null) {
                    chat = MessagesStorage.getInstance(this.currentAccount).getChatSync(updates.chat_id);
                    putChat(chat, true);
                }
                missingData = chat == null || user == null || ((needFwdUser && user2 == null && channel == null) || (needBotUser && user3 == null));
            }
            if (!missingData && !updates.entities.isEmpty()) {
                for (a = 0; a < updates.entities.size(); a++) {
                    MessageEntity entity = (MessageEntity) updates.entities.get(a);
                    if (entity instanceof TL_messageEntityMentionName) {
                        int uid = ((TL_messageEntityMentionName) entity).user_id;
                        User entityUser = getUser(Integer.valueOf(uid));
                        if (entityUser == null || entityUser.min) {
                            entityUser = MessagesStorage.getInstance(this.currentAccount).getUserSync(uid);
                            if (entityUser != null && entityUser.min) {
                                entityUser = null;
                            }
                            if (entityUser == null) {
                                missingData = true;
                                break;
                            }
                            putUser(user, true);
                        }
                    }
                }
            }
            if (!(user == null || user.status == null || user.status.expires > 0)) {
                this.onlinePrivacy.put(Integer.valueOf(user.id), Integer.valueOf(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
                updateStatus = true;
            }
            if (missingData) {
                needGetDiff = true;
            } else if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + updates.pts_count == updates.pts) {
                Message message = new TL_message();
                message.id = updates.id;
                int clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                if (updates instanceof TL_updateShortMessage) {
                    if (updates.out) {
                        message.from_id = clientUserId;
                    } else {
                        message.from_id = user_id;
                    }
                    message.to_id = new TL_peerUser();
                    message.to_id.user_id = user_id;
                    message.dialog_id = (long) user_id;
                } else {
                    message.from_id = user_id;
                    message.to_id = new TL_peerChat();
                    message.to_id.chat_id = updates.chat_id;
                    message.dialog_id = (long) (-updates.chat_id);
                }
                message.fwd_from = updates.fwd_from;
                message.silent = updates.silent;
                message.out = updates.out;
                message.mentioned = updates.mentioned;
                message.media_unread = updates.media_unread;
                message.entities = updates.entities;
                message.message = updates.message;
                message.date = updates.date;
                message.via_bot_id = updates.via_bot_id;
                message.flags = updates.flags | 256;
                message.reply_to_msg_id = updates.reply_to_msg_id;
                message.media = new TL_messageMediaEmpty();
                ConcurrentHashMap<Long, Integer> read_max = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                Integer value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                    read_max.put(Long.valueOf(message.dialog_id), value);
                }
                message.unread = value.intValue() < message.id;
                if (message.dialog_id == ((long) clientUserId)) {
                    message.unread = false;
                    message.media_unread = false;
                    message.out = true;
                }
                MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(updates.pts);
                MessageObject messageObject = new MessageObject(this.currentAccount, message, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                ArrayList<MessageObject> objArr = new ArrayList();
                objArr.add(messageObject);
                ArrayList arr2 = new ArrayList();
                arr2.add(message);
                boolean printUpdate;
                final boolean z;
                final ArrayList<MessageObject> arrayList;
                if (updates instanceof TL_updateShortMessage) {
                    printUpdate = !updates.out && updatePrintingUsersWithNewMessages((long) updates.user_id, objArr);
                    if (printUpdate) {
                        updatePrintingStrings();
                    }
                    z = printUpdate;
                    final int i = user_id;
                    arrayList = objArr;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (z) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
                            }
                            MessagesController.this.updateInterfaceWithMessages((long) i, arrayList);
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        }
                    });
                } else {
                    printUpdate = updatePrintingUsersWithNewMessages((long) (-updates.chat_id), objArr);
                    if (printUpdate) {
                        updatePrintingStrings();
                    }
                    z = printUpdate;
                    final Updates updates2 = updates;
                    arrayList = objArr;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            if (z) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(64));
                            }
                            MessagesController.this.updateInterfaceWithMessages((long) (-updates2.chat_id), arrayList);
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                        }
                    });
                }
                if (!messageObject.isOut()) {
                    final ArrayList<MessageObject> arrayList2 = objArr;
                    MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                        /* renamed from: org.telegram.messenger.MessagesController$124$1 */
                        class C03081 implements Runnable {
                            C03081() {
                            }

                            public void run() {
                                NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(arrayList2, true, false);
                            }
                        }

                        public void run() {
                            AndroidUtilities.runOnUIThread(new C03081());
                        }
                    });
                }
                MessagesStorage.getInstance(this.currentAccount).putMessages(arr2, false, true, false, 0);
            } else if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() != updates.pts) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d("need get diff short message, pts: " + MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + " " + updates.pts + " count = " + updates.pts_count);
                }
                if (this.gettingDifference || this.updatesStartWaitTimePts == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500) {
                    if (this.updatesStartWaitTimePts == 0) {
                        this.updatesStartWaitTimePts = System.currentTimeMillis();
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d("add to queue");
                    }
                    this.updatesQueuePts.add(updates);
                } else {
                    needGetDiff = true;
                }
            }
        } else if ((updates instanceof TL_updatesCombined) || (updates instanceof TL_updates)) {
            Update update;
            int channelId;
            SparseArray<Chat> minChannels = null;
            for (a = 0; a < updates.chats.size(); a++) {
                chat = (Chat) updates.chats.get(a);
                if ((chat instanceof TL_channel) && chat.min) {
                    Chat existChat = getChat(Integer.valueOf(chat.id));
                    if (existChat == null || existChat.min) {
                        Chat cacheChat = MessagesStorage.getInstance(this.currentAccount).getChatSync(updates.chat_id);
                        putChat(cacheChat, true);
                        existChat = cacheChat;
                    }
                    if (existChat == null || existChat.min) {
                        if (minChannels == null) {
                            minChannels = new SparseArray();
                        }
                        minChannels.put(chat.id, chat);
                    }
                }
            }
            if (minChannels != null) {
                for (a = 0; a < updates.updates.size(); a++) {
                    update = (Update) updates.updates.get(a);
                    if (update instanceof TL_updateNewChannelMessage) {
                        channelId = ((TL_updateNewChannelMessage) update).message.to_id.channel_id;
                        if (minChannels.indexOfKey(channelId) >= 0) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("need get diff because of min channel " + channelId);
                            }
                            needGetDiff = true;
                        }
                    }
                }
            }
            if (!needGetDiff) {
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(updates.users, updates.chats, true, true);
                Collections.sort(updates.updates, this.updatesComparator);
                a = 0;
                while (updates.updates.size() > 0) {
                    update = (Update) updates.updates.get(a);
                    TL_updates updatesNew;
                    int b;
                    Update update2;
                    int pts2;
                    int count2;
                    if (getUpdateType(update) != 0) {
                        if (getUpdateType(update) != 1) {
                            if (getUpdateType(update) != 2) {
                                break;
                            }
                            channelId = getUpdateChannelId(update);
                            boolean skipUpdate = false;
                            int channelPts = this.channelsPts.get(channelId);
                            if (channelPts == 0) {
                                channelPts = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(channelId);
                                if (channelPts == 0) {
                                    for (int c = 0; c < updates.chats.size(); c++) {
                                        chat = (Chat) updates.chats.get(c);
                                        if (chat.id == channelId) {
                                            loadUnknownChannel(chat, 0);
                                            skipUpdate = true;
                                            break;
                                        }
                                    }
                                } else {
                                    this.channelsPts.put(channelId, channelPts);
                                }
                            }
                            updatesNew = new TL_updates();
                            updatesNew.updates.add(update);
                            updatesNew.pts = getUpdatePts(update);
                            updatesNew.pts_count = getUpdatePtsCount(update);
                            for (b = a + 1; b < updates.updates.size(); b = (b - 1) + 1) {
                                update2 = (Update) updates.updates.get(b);
                                pts2 = getUpdatePts(update2);
                                count2 = getUpdatePtsCount(update2);
                                if (getUpdateType(update2) != 2 || channelId != getUpdateChannelId(update2) || updatesNew.pts + count2 != pts2) {
                                    break;
                                }
                                updatesNew.updates.add(update2);
                                updatesNew.pts = pts2;
                                updatesNew.pts_count += count2;
                                updates.updates.remove(b);
                            }
                            if (skipUpdate) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("need load unknown channel = " + channelId);
                                }
                            } else if (updatesNew.pts_count + channelPts == updatesNew.pts) {
                                if (processUpdateArray(updatesNew.updates, updates.users, updates.chats, false)) {
                                    this.channelsPts.put(channelId, updatesNew.pts);
                                    MessagesStorage.getInstance(this.currentAccount).saveChannelPts(channelId, updatesNew.pts);
                                } else {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("need get channel diff inner TL_updates, channel_id = " + channelId);
                                    }
                                    if (needGetChannelsDiff == null) {
                                        needGetChannelsDiff = new ArrayList();
                                    } else {
                                        if (!needGetChannelsDiff.contains(Integer.valueOf(channelId))) {
                                            needGetChannelsDiff.add(Integer.valueOf(channelId));
                                        }
                                    }
                                }
                            } else if (channelPts != updatesNew.pts) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d(update + " need get channel diff, pts: " + channelPts + " " + updatesNew.pts + " count = " + updatesNew.pts_count + " channelId = " + channelId);
                                }
                                long updatesStartWaitTime = this.updatesStartWaitTimeChannels.get(channelId);
                                if (this.gettingDifferenceChannels.get(channelId) || updatesStartWaitTime == 0 || Math.abs(System.currentTimeMillis() - updatesStartWaitTime) <= 1500) {
                                    if (updatesStartWaitTime == 0) {
                                        this.updatesStartWaitTimeChannels.put(channelId, System.currentTimeMillis());
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("add to queue");
                                    }
                                    ArrayList<Updates> arrayList3 = (ArrayList) this.updatesQueueChannels.get(channelId);
                                    if (arrayList3 == null) {
                                        arrayList3 = new ArrayList();
                                        this.updatesQueueChannels.put(channelId, arrayList3);
                                    }
                                    arrayList3.add(updatesNew);
                                } else if (needGetChannelsDiff == null) {
                                    needGetChannelsDiff = new ArrayList();
                                } else {
                                    if (!needGetChannelsDiff.contains(Integer.valueOf(channelId))) {
                                        needGetChannelsDiff.add(Integer.valueOf(channelId));
                                    }
                                }
                            }
                        } else {
                            updatesNew = new TL_updates();
                            updatesNew.updates.add(update);
                            updatesNew.pts = getUpdateQts(update);
                            for (b = a + 1; b < updates.updates.size(); b = (b - 1) + 1) {
                                update2 = (Update) updates.updates.get(b);
                                int qts2 = getUpdateQts(update2);
                                if (getUpdateType(update2) != 1 || updatesNew.pts + 1 != qts2) {
                                    break;
                                }
                                updatesNew.updates.add(update2);
                                updatesNew.pts = qts2;
                                updates.updates.remove(b);
                            }
                            if (MessagesStorage.getInstance(this.currentAccount).getLastQtsValue() == 0 || MessagesStorage.getInstance(this.currentAccount).getLastQtsValue() + updatesNew.updates.size() == updatesNew.pts) {
                                processUpdateArray(updatesNew.updates, updates.users, updates.chats, false);
                                MessagesStorage.getInstance(this.currentAccount).setLastQtsValue(updatesNew.pts);
                                needReceivedQueue = true;
                            } else if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() != updatesNew.pts) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d(update + " need get diff, qts: " + MessagesStorage.getInstance(this.currentAccount).getLastQtsValue() + " " + updatesNew.pts);
                                }
                                if (this.gettingDifference || this.updatesStartWaitTimeQts == 0 || (this.updatesStartWaitTimeQts != 0 && Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeQts) <= 1500)) {
                                    if (this.updatesStartWaitTimeQts == 0) {
                                        this.updatesStartWaitTimeQts = System.currentTimeMillis();
                                    }
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("add to queue");
                                    }
                                    this.updatesQueueQts.add(updatesNew);
                                } else {
                                    needGetDiff = true;
                                }
                            }
                        }
                    } else {
                        updatesNew = new TL_updates();
                        updatesNew.updates.add(update);
                        updatesNew.pts = getUpdatePts(update);
                        updatesNew.pts_count = getUpdatePtsCount(update);
                        for (b = a + 1; b < updates.updates.size(); b = (b - 1) + 1) {
                            update2 = (Update) updates.updates.get(b);
                            pts2 = getUpdatePts(update2);
                            count2 = getUpdatePtsCount(update2);
                            if (getUpdateType(update2) != 0 || updatesNew.pts + count2 != pts2) {
                                break;
                            }
                            updatesNew.updates.add(update2);
                            updatesNew.pts = pts2;
                            updatesNew.pts_count += count2;
                            updates.updates.remove(b);
                        }
                        if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + updatesNew.pts_count == updatesNew.pts) {
                            if (processUpdateArray(updatesNew.updates, updates.users, updates.chats, false)) {
                                MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(updatesNew.pts);
                            } else {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("need get diff inner TL_updates, pts: " + MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + " " + updates.seq);
                                }
                                needGetDiff = true;
                            }
                        } else if (MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() != updatesNew.pts) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d(update + " need get diff, pts: " + MessagesStorage.getInstance(this.currentAccount).getLastPtsValue() + " " + updatesNew.pts + " count = " + updatesNew.pts_count);
                            }
                            if (this.gettingDifference || this.updatesStartWaitTimePts == 0 || (this.updatesStartWaitTimePts != 0 && Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimePts) <= 1500)) {
                                if (this.updatesStartWaitTimePts == 0) {
                                    this.updatesStartWaitTimePts = System.currentTimeMillis();
                                }
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("add to queue");
                                }
                                this.updatesQueuePts.add(updatesNew);
                            } else {
                                needGetDiff = true;
                            }
                        }
                    }
                    updates.updates.remove(a);
                    a = (a - 1) + 1;
                }
                boolean processUpdate = updates instanceof TL_updatesCombined ? MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 == updates.seq_start || MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() == updates.seq_start : MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + 1 == updates.seq || updates.seq == 0 || updates.seq == MessagesStorage.getInstance(this.currentAccount).getLastSeqValue();
                if (processUpdate) {
                    processUpdateArray(updates.updates, updates.users, updates.chats, false);
                    if (updates.seq != 0) {
                        if (updates.date != 0) {
                            MessagesStorage.getInstance(this.currentAccount).setLastDateValue(updates.date);
                        }
                        MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(updates.seq);
                    }
                } else {
                    if (BuildVars.LOGS_ENABLED) {
                        if (updates instanceof TL_updatesCombined) {
                            FileLog.m0d("need get diff TL_updatesCombined, seq: " + MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + " " + updates.seq_start);
                        } else {
                            FileLog.m0d("need get diff TL_updates, seq: " + MessagesStorage.getInstance(this.currentAccount).getLastSeqValue() + " " + updates.seq);
                        }
                    }
                    if (this.gettingDifference || this.updatesStartWaitTimeSeq == 0 || Math.abs(System.currentTimeMillis() - this.updatesStartWaitTimeSeq) <= 1500) {
                        if (this.updatesStartWaitTimeSeq == 0) {
                            this.updatesStartWaitTimeSeq = System.currentTimeMillis();
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("add TL_updates/Combined to queue");
                        }
                        this.updatesQueueSeq.add(updates);
                    } else {
                        needGetDiff = true;
                    }
                }
            }
        } else if (updates instanceof TL_updatesTooLong) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("need get diff TL_updatesTooLong");
            }
            needGetDiff = true;
        } else if (updates instanceof UserActionUpdatesSeq) {
            MessagesStorage.getInstance(this.currentAccount).setLastSeqValue(updates.seq);
        } else if (updates instanceof UserActionUpdatesPts) {
            if (updates.chat_id != 0) {
                this.channelsPts.put(updates.chat_id, updates.pts);
                MessagesStorage.getInstance(this.currentAccount).saveChannelPts(updates.chat_id, updates.pts);
            } else {
                MessagesStorage.getInstance(this.currentAccount).setLastPtsValue(updates.pts);
            }
        }
        SecretChatHelper.getInstance(this.currentAccount).processPendingEncMessages();
        if (!fromQueue) {
            for (a = 0; a < this.updatesQueueChannels.size(); a++) {
                int key = this.updatesQueueChannels.keyAt(a);
                if (needGetChannelsDiff != null) {
                    if (needGetChannelsDiff.contains(Integer.valueOf(key))) {
                        getChannelDifference(key);
                    }
                }
                processChannelsUpdatesQueue(key, 0);
            }
            if (needGetDiff) {
                getDifference();
            } else {
                for (a = 0; a < 3; a++) {
                    processUpdatesQueue(a, 0);
                }
            }
        }
        if (needReceivedQueue) {
            TLObject req = new TL_messages_receivedQueue();
            req.max_qts = MessagesStorage.getInstance(this.currentAccount).getLastQtsValue();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                public void run(TLObject response, TL_error error) {
                }
            });
        }
        if (updateStatus) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(4));
                }
            });
        }
        MessagesStorage.getInstance(this.currentAccount).saveDiffParams(MessagesStorage.getInstance(this.currentAccount).getLastSeqValue(), MessagesStorage.getInstance(this.currentAccount).getLastPtsValue(), MessagesStorage.getInstance(this.currentAccount).getLastDateValue(), MessagesStorage.getInstance(this.currentAccount).getLastQtsValue());
    }

    public boolean processUpdateArray(ArrayList<Update> updates, ArrayList<User> usersArr, ArrayList<Chat> chatsArr, boolean fromGetDifference) {
        if (updates.isEmpty()) {
            if (!(usersArr == null && chatsArr == null)) {
                final ArrayList<User> arrayList = usersArr;
                final ArrayList<Chat> arrayList2 = chatsArr;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        MessagesController.this.putUsers(arrayList, false);
                        MessagesController.this.putChats(arrayList2, false);
                    }
                });
            }
            return true;
        }
        AbstractMap usersDict;
        int size;
        int a;
        AbstractMap chatsDict;
        long currentTime = System.currentTimeMillis();
        boolean printChanged = false;
        LongSparseArray<ArrayList<MessageObject>> messages = null;
        LongSparseArray<WebPage> webPages = null;
        ArrayList<MessageObject> pushMessages = null;
        ArrayList<Message> messagesArr = null;
        LongSparseArray<ArrayList<MessageObject>> editingMessages = null;
        SparseArray<SparseIntArray> channelViews = null;
        SparseLongArray markAsReadMessagesInbox = null;
        SparseLongArray markAsReadMessagesOutbox = null;
        ArrayList<Long> markAsReadMessages = null;
        SparseIntArray markAsReadEncrypted = null;
        SparseArray<ArrayList<Integer>> deletedMessages = null;
        SparseIntArray clearHistoryMessages = null;
        ArrayList<ChatParticipants> chatInfoToUpdate = null;
        ArrayList<Update> updatesOnMainThread = null;
        ArrayList<TL_updateEncryptedMessagesRead> tasks = null;
        ArrayList<Integer> contactsIds = null;
        boolean checkForUsers = true;
        if (usersArr != null) {
            usersDict = new ConcurrentHashMap();
            size = usersArr.size();
            for (a = 0; a < size; a++) {
                User user = (User) usersArr.get(a);
                usersDict.put(Integer.valueOf(user.id), user);
            }
        } else {
            checkForUsers = false;
            usersDict = this.users;
        }
        if (chatsArr != null) {
            chatsDict = new ConcurrentHashMap();
            size = chatsArr.size();
            for (a = 0; a < size; a++) {
                Chat chat = (Chat) chatsArr.get(a);
                chatsDict.put(Integer.valueOf(chat.id), chat);
            }
        } else {
            checkForUsers = false;
            chatsDict = this.chats;
        }
        if (fromGetDifference) {
            checkForUsers = false;
        }
        if (!(usersArr == null && chatsArr == null)) {
            arrayList = usersArr;
            arrayList2 = chatsArr;
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    MessagesController.this.putUsers(arrayList, false);
                    MessagesController.this.putChats(arrayList2, false);
                }
            });
        }
        int interfaceUpdateMask = 0;
        int size3 = updates.size();
        for (int c = 0; c < size3; c++) {
            Update baseUpdate = (Update) updates.get(c);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("process update " + baseUpdate);
            }
            Message message;
            int chat_id;
            int user_id;
            int count;
            MessageEntity entity;
            int clientUserId;
            ConcurrentHashMap<Long, Integer> read_max;
            Integer value;
            MessageObject obj;
            ArrayList<MessageObject> arr;
            if ((baseUpdate instanceof TL_updateNewMessage) || (baseUpdate instanceof TL_updateNewChannelMessage)) {
                if (baseUpdate instanceof TL_updateNewMessage) {
                    message = ((TL_updateNewMessage) baseUpdate).message;
                } else {
                    message = ((TL_updateNewChannelMessage) baseUpdate).message;
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.m0d(baseUpdate + " channelId = " + message.to_id.channel_id);
                    }
                    if (!message.out && message.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        message.out = true;
                    }
                }
                chat = null;
                chat_id = 0;
                user_id = 0;
                if (message.to_id.channel_id != 0) {
                    chat_id = message.to_id.channel_id;
                } else if (message.to_id.chat_id != 0) {
                    chat_id = message.to_id.chat_id;
                } else if (message.to_id.user_id != 0) {
                    user_id = message.to_id.user_id;
                }
                if (chat_id != 0) {
                    chat = (Chat) chatsDict.get(Integer.valueOf(chat_id));
                    if (chat == null) {
                        chat = getChat(Integer.valueOf(chat_id));
                    }
                    if (chat == null) {
                        chat = MessagesStorage.getInstance(this.currentAccount).getChatSync(chat_id);
                        putChat(chat, true);
                    }
                }
                if (checkForUsers) {
                    if (chat_id == 0 || chat != null) {
                        count = message.entities.size() + 3;
                        for (a = 0; a < count; a++) {
                            boolean allowMin = false;
                            if (a != 0) {
                                if (a == 1) {
                                    user_id = message.from_id;
                                    if (message.post) {
                                        allowMin = true;
                                    }
                                } else if (a == 2) {
                                    user_id = message.fwd_from != null ? message.fwd_from.from_id : 0;
                                } else {
                                    entity = (MessageEntity) message.entities.get(a - 3);
                                    user_id = entity instanceof TL_messageEntityMentionName ? ((TL_messageEntityMentionName) entity).user_id : 0;
                                }
                            }
                            if (user_id > 0) {
                                user = (User) usersDict.get(Integer.valueOf(user_id));
                                if (user == null || (!allowMin && user.min)) {
                                    user = getUser(Integer.valueOf(user_id));
                                }
                                if (user == null || (!allowMin && user.min)) {
                                    user = MessagesStorage.getInstance(this.currentAccount).getUserSync(user_id);
                                    if (!(user == null || allowMin || !user.min)) {
                                        user = null;
                                    }
                                    putUser(user, true);
                                }
                                if (user == null) {
                                    if (BuildVars.LOGS_ENABLED) {
                                        FileLog.m0d("not found user " + user_id);
                                    }
                                    return false;
                                } else if (a == 1 && user.status != null && user.status.expires <= 0) {
                                    this.onlinePrivacy.put(Integer.valueOf(user_id), Integer.valueOf(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
                                    interfaceUpdateMask |= 4;
                                }
                            }
                        }
                    } else {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m0d("not found chat " + chat_id);
                        }
                        return false;
                    }
                }
                if (chat != null && chat.megagroup) {
                    message.flags |= Integer.MIN_VALUE;
                }
                if (message.action instanceof TL_messageActionChatDeleteUser) {
                    user = (User) usersDict.get(Integer.valueOf(message.action.user_id));
                    if (user != null && user.bot) {
                        message.reply_markup = new TL_replyKeyboardHide();
                        message.flags |= 64;
                    } else if (message.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId() && message.action.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    }
                }
                if (messagesArr == null) {
                    messagesArr = new ArrayList();
                }
                messagesArr.add(message);
                ImageLoader.saveMessageThumbs(message);
                clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                if (message.to_id.chat_id != 0) {
                    message.dialog_id = (long) (-message.to_id.chat_id);
                } else if (message.to_id.channel_id != 0) {
                    message.dialog_id = (long) (-message.to_id.channel_id);
                } else {
                    if (message.to_id.user_id == clientUserId) {
                        message.to_id.user_id = message.from_id;
                    }
                    message.dialog_id = (long) message.to_id.user_id;
                }
                read_max = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                    read_max.put(Long.valueOf(message.dialog_id), value);
                }
                boolean z = value.intValue() < message.id && !((chat != null && ChatObject.isNotInChat(chat)) || (message.action instanceof TL_messageActionChatMigrateTo) || (message.action instanceof TL_messageActionChannelCreate));
                message.unread = z;
                if (message.dialog_id == ((long) clientUserId)) {
                    message.unread = false;
                    message.media_unread = false;
                    message.out = true;
                }
                obj = new MessageObject(this.currentAccount, message, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                if (obj.type == 11) {
                    interfaceUpdateMask |= 8;
                } else if (obj.type == 10) {
                    interfaceUpdateMask |= 16;
                }
                if (messages == null) {
                    messages = new LongSparseArray();
                }
                arr = (ArrayList) messages.get(message.dialog_id);
                if (arr == null) {
                    arr = new ArrayList();
                    messages.put(message.dialog_id, arr);
                }
                arr.add(obj);
                if (!obj.isOut() && obj.isUnread()) {
                    if (pushMessages == null) {
                        pushMessages = new ArrayList();
                    }
                    pushMessages.add(obj);
                }
            } else if (baseUpdate instanceof TL_updateReadMessagesContents) {
                TL_updateReadMessagesContents update = (TL_updateReadMessagesContents) baseUpdate;
                if (markAsReadMessages == null) {
                    markAsReadMessages = new ArrayList();
                }
                size = update.messages.size();
                for (a = 0; a < size; a++) {
                    markAsReadMessages.add(Long.valueOf((long) ((Integer) update.messages.get(a)).intValue()));
                }
            } else if (baseUpdate instanceof TL_updateChannelReadMessagesContents) {
                TL_updateChannelReadMessagesContents update2 = (TL_updateChannelReadMessagesContents) baseUpdate;
                if (markAsReadMessages == null) {
                    markAsReadMessages = new ArrayList();
                }
                size = update2.messages.size();
                for (a = 0; a < size; a++) {
                    markAsReadMessages.add(Long.valueOf(((long) ((Integer) update2.messages.get(a)).intValue()) | (((long) update2.channel_id) << 32)));
                }
            } else if (baseUpdate instanceof TL_updateReadHistoryInbox) {
                TL_updateReadHistoryInbox update3 = (TL_updateReadHistoryInbox) baseUpdate;
                if (markAsReadMessagesInbox == null) {
                    markAsReadMessagesInbox = new SparseLongArray();
                }
                if (update3.peer.chat_id != 0) {
                    markAsReadMessagesInbox.put(-update3.peer.chat_id, (long) update3.max_id);
                    dialog_id = (long) (-update3.peer.chat_id);
                } else {
                    markAsReadMessagesInbox.put(update3.peer.user_id, (long) update3.max_id);
                    dialog_id = (long) update3.peer.user_id;
                }
                value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(false, dialog_id));
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(value.intValue(), update3.max_id)));
            } else if (baseUpdate instanceof TL_updateReadHistoryOutbox) {
                TL_updateReadHistoryOutbox update4 = (TL_updateReadHistoryOutbox) baseUpdate;
                if (markAsReadMessagesOutbox == null) {
                    markAsReadMessagesOutbox = new SparseLongArray();
                }
                if (update4.peer.chat_id != 0) {
                    markAsReadMessagesOutbox.put(-update4.peer.chat_id, (long) update4.max_id);
                    dialog_id = (long) (-update4.peer.chat_id);
                } else {
                    markAsReadMessagesOutbox.put(update4.peer.user_id, (long) update4.max_id);
                    dialog_id = (long) update4.peer.user_id;
                }
                value = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, dialog_id));
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(value.intValue(), update4.max_id)));
            } else if (baseUpdate instanceof TL_updateDeleteMessages) {
                TL_updateDeleteMessages update5 = (TL_updateDeleteMessages) baseUpdate;
                if (deletedMessages == null) {
                    deletedMessages = new SparseArray();
                }
                arrayList = (ArrayList) deletedMessages.get(0);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    deletedMessages.put(0, arrayList);
                }
                arrayList.addAll(update5.messages);
            } else if ((baseUpdate instanceof TL_updateUserTyping) || (baseUpdate instanceof TL_updateChatUserTyping)) {
                SendMessageAction action;
                if (baseUpdate instanceof TL_updateUserTyping) {
                    TL_updateUserTyping update6 = (TL_updateUserTyping) baseUpdate;
                    user_id = update6.user_id;
                    action = update6.action;
                    chat_id = 0;
                } else {
                    TL_updateChatUserTyping update7 = (TL_updateChatUserTyping) baseUpdate;
                    chat_id = update7.chat_id;
                    user_id = update7.user_id;
                    action = update7.action;
                }
                if (user_id != UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    uid = (long) (-chat_id);
                    if (uid == 0) {
                        uid = (long) user_id;
                    }
                    arr = (ArrayList) this.printingUsers.get(Long.valueOf(uid));
                    if (!(action instanceof TL_sendMessageCancelAction)) {
                        if (arr == null) {
                            arr = new ArrayList();
                            this.printingUsers.put(Long.valueOf(uid), arr);
                        }
                        exist = false;
                        Iterator it = arr.iterator();
                        while (it.hasNext()) {
                            u = (PrintingUser) it.next();
                            if (u.userId == user_id) {
                                exist = true;
                                u.lastTime = currentTime;
                                if (u.action.getClass() != action.getClass()) {
                                    printChanged = true;
                                }
                                u.action = action;
                                if (!exist) {
                                    newUser = new PrintingUser();
                                    newUser.userId = user_id;
                                    newUser.lastTime = currentTime;
                                    newUser.action = action;
                                    arr.add(newUser);
                                    printChanged = true;
                                }
                            }
                        }
                        if (exist) {
                            newUser = new PrintingUser();
                            newUser.userId = user_id;
                            newUser.lastTime = currentTime;
                            newUser.action = action;
                            arr.add(newUser);
                            printChanged = true;
                        }
                    } else if (arr != null) {
                        size = arr.size();
                        for (a = 0; a < size; a++) {
                            if (((PrintingUser) arr.get(a)).userId == user_id) {
                                arr.remove(a);
                                printChanged = true;
                                break;
                            }
                        }
                        if (arr.isEmpty()) {
                            this.printingUsers.remove(Long.valueOf(uid));
                        }
                    }
                    this.onlinePrivacy.put(Integer.valueOf(user_id), Integer.valueOf(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
                }
            } else if (baseUpdate instanceof TL_updateChatParticipants) {
                TL_updateChatParticipants update8 = (TL_updateChatParticipants) baseUpdate;
                interfaceUpdateMask |= 32;
                if (chatInfoToUpdate == null) {
                    chatInfoToUpdate = new ArrayList();
                }
                chatInfoToUpdate.add(update8.participants);
            } else if (baseUpdate instanceof TL_updateUserStatus) {
                interfaceUpdateMask |= 4;
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateUserName) {
                interfaceUpdateMask |= 1;
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateUserPhoto) {
                interfaceUpdateMask |= 2;
                MessagesStorage.getInstance(this.currentAccount).clearUserPhotos(((TL_updateUserPhoto) baseUpdate).user_id);
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateUserPhone) {
                interfaceUpdateMask |= 1024;
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateContactRegistered) {
                TL_updateContactRegistered update9 = (TL_updateContactRegistered) baseUpdate;
                if (this.enableJoined && usersDict.containsKey(Integer.valueOf(update9.user_id)) && !MessagesStorage.getInstance(this.currentAccount).isDialogHasMessages((long) update9.user_id)) {
                    newMessage = new TL_messageService();
                    newMessage.action = new TL_messageActionUserJoined();
                    r5 = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                    newMessage.id = r5;
                    newMessage.local_id = r5;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    newMessage.unread = false;
                    newMessage.flags = 256;
                    newMessage.date = update9.date;
                    newMessage.from_id = update9.user_id;
                    newMessage.to_id = new TL_peerUser();
                    newMessage.to_id.user_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    newMessage.dialog_id = (long) update9.user_id;
                    if (messagesArr == null) {
                        messagesArr = new ArrayList();
                    }
                    messagesArr.add(newMessage);
                    r9 = new MessageObject(this.currentAccount, newMessage, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(newMessage.dialog_id)));
                    if (messages == null) {
                        messages = new LongSparseArray();
                    }
                    arr = (ArrayList) messages.get(newMessage.dialog_id);
                    if (arr == null) {
                        arr = new ArrayList();
                        messages.put(newMessage.dialog_id, arr);
                    }
                    arr.add(r9);
                }
            } else if (baseUpdate instanceof TL_updateContactLink) {
                TL_updateContactLink update10 = (TL_updateContactLink) baseUpdate;
                if (contactsIds == null) {
                    contactsIds = new ArrayList();
                }
                int idx;
                if (update10.my_link instanceof TL_contactLinkContact) {
                    idx = contactsIds.indexOf(Integer.valueOf(-update10.user_id));
                    if (idx != -1) {
                        contactsIds.remove(idx);
                    }
                    if (!contactsIds.contains(Integer.valueOf(update10.user_id))) {
                        contactsIds.add(Integer.valueOf(update10.user_id));
                    }
                } else {
                    idx = contactsIds.indexOf(Integer.valueOf(update10.user_id));
                    if (idx != -1) {
                        contactsIds.remove(idx);
                    }
                    if (!contactsIds.contains(Integer.valueOf(update10.user_id))) {
                        contactsIds.add(Integer.valueOf(-update10.user_id));
                    }
                }
            } else if (baseUpdate instanceof TL_updateNewEncryptedMessage) {
                ArrayList<Message> decryptedMessages = SecretChatHelper.getInstance(this.currentAccount).decryptMessage(((TL_updateNewEncryptedMessage) baseUpdate).message);
                if (!(decryptedMessages == null || decryptedMessages.isEmpty())) {
                    uid = ((long) ((TL_updateNewEncryptedMessage) baseUpdate).message.chat_id) << 32;
                    if (messages == null) {
                        messages = new LongSparseArray();
                    }
                    arr = (ArrayList) messages.get(uid);
                    if (arr == null) {
                        arr = new ArrayList();
                        messages.put(uid, arr);
                    }
                    size = decryptedMessages.size();
                    for (a = 0; a < size; a++) {
                        message = (Message) decryptedMessages.get(a);
                        ImageLoader.saveMessageThumbs(message);
                        if (messagesArr == null) {
                            messagesArr = new ArrayList();
                        }
                        messagesArr.add(message);
                        obj = new MessageObject(this.currentAccount, message, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(uid)));
                        arr.add(obj);
                        if (pushMessages == null) {
                            pushMessages = new ArrayList();
                        }
                        pushMessages.add(obj);
                    }
                }
            } else if (baseUpdate instanceof TL_updateEncryptedChatTyping) {
                TL_updateEncryptedChatTyping update11 = (TL_updateEncryptedChatTyping) baseUpdate;
                EncryptedChat encryptedChat = getEncryptedChatDB(update11.chat_id, true);
                if (encryptedChat != null) {
                    uid = ((long) update11.chat_id) << 32;
                    arr = (ArrayList) this.printingUsers.get(Long.valueOf(uid));
                    if (arr == null) {
                        arr = new ArrayList();
                        this.printingUsers.put(Long.valueOf(uid), arr);
                    }
                    exist = false;
                    size = arr.size();
                    for (a = 0; a < size; a++) {
                        u = (PrintingUser) arr.get(a);
                        if (u.userId == encryptedChat.user_id) {
                            exist = true;
                            u.lastTime = currentTime;
                            u.action = new TL_sendMessageTypingAction();
                            break;
                        }
                    }
                    if (!exist) {
                        newUser = new PrintingUser();
                        newUser.userId = encryptedChat.user_id;
                        newUser.lastTime = currentTime;
                        newUser.action = new TL_sendMessageTypingAction();
                        arr.add(newUser);
                        printChanged = true;
                    }
                    this.onlinePrivacy.put(Integer.valueOf(encryptedChat.user_id), Integer.valueOf(ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()));
                }
            } else if (baseUpdate instanceof TL_updateEncryptedMessagesRead) {
                TL_updateEncryptedMessagesRead update12 = (TL_updateEncryptedMessagesRead) baseUpdate;
                if (markAsReadEncrypted == null) {
                    markAsReadEncrypted = new SparseIntArray();
                }
                markAsReadEncrypted.put(update12.chat_id, Math.max(update12.max_date, update12.date));
                if (tasks == null) {
                    tasks = new ArrayList();
                }
                tasks.add((TL_updateEncryptedMessagesRead) baseUpdate);
            } else if (baseUpdate instanceof TL_updateChatParticipantAdd) {
                TL_updateChatParticipantAdd update13 = (TL_updateChatParticipantAdd) baseUpdate;
                MessagesStorage.getInstance(this.currentAccount).updateChatInfo(update13.chat_id, update13.user_id, 0, update13.inviter_id, update13.version);
            } else if (baseUpdate instanceof TL_updateChatParticipantDelete) {
                TL_updateChatParticipantDelete update14 = (TL_updateChatParticipantDelete) baseUpdate;
                MessagesStorage.getInstance(this.currentAccount).updateChatInfo(update14.chat_id, update14.user_id, 1, 0, update14.version);
            } else if ((baseUpdate instanceof TL_updateDcOptions) || (baseUpdate instanceof TL_updateConfig)) {
                ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
            } else if (baseUpdate instanceof TL_updateEncryption) {
                SecretChatHelper.getInstance(this.currentAccount).processUpdateEncryption((TL_updateEncryption) baseUpdate, usersDict);
            } else if (baseUpdate instanceof TL_updateUserBlocked) {
                TL_updateUserBlocked finalUpdate = (TL_updateUserBlocked) baseUpdate;
                if (finalUpdate.blocked) {
                    ArrayList<Integer> ids = new ArrayList();
                    ids.add(Integer.valueOf(finalUpdate.user_id));
                    MessagesStorage.getInstance(this.currentAccount).putBlockedUsers(ids, false);
                } else {
                    MessagesStorage.getInstance(this.currentAccount).deleteBlockedUser(finalUpdate.user_id);
                }
                final TL_updateUserBlocked tL_updateUserBlocked = finalUpdate;
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                    /* renamed from: org.telegram.messenger.MessagesController$129$1 */
                    class C03091 implements Runnable {
                        C03091() {
                        }

                        public void run() {
                            if (!tL_updateUserBlocked.blocked) {
                                MessagesController.this.blockedUsers.remove(Integer.valueOf(tL_updateUserBlocked.user_id));
                            } else if (!MessagesController.this.blockedUsers.contains(Integer.valueOf(tL_updateUserBlocked.user_id))) {
                                MessagesController.this.blockedUsers.add(Integer.valueOf(tL_updateUserBlocked.user_id));
                            }
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.blockedUsersDidLoaded, new Object[0]);
                        }
                    }

                    public void run() {
                        AndroidUtilities.runOnUIThread(new C03091());
                    }
                });
            } else if (baseUpdate instanceof TL_updateNotifySettings) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateServiceNotification) {
                TL_updateServiceNotification update15 = (TL_updateServiceNotification) baseUpdate;
                if (update15.popup && update15.message != null && update15.message.length() > 0) {
                    final TL_updateServiceNotification tL_updateServiceNotification = update15;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.needShowAlert, Integer.valueOf(2), tL_updateServiceNotification.message);
                        }
                    });
                }
                if ((update15.flags & 2) != 0) {
                    newMessage = new TL_message();
                    r5 = UserConfig.getInstance(this.currentAccount).getNewMessageId();
                    newMessage.id = r5;
                    newMessage.local_id = r5;
                    UserConfig.getInstance(this.currentAccount).saveConfig(false);
                    newMessage.unread = true;
                    newMessage.flags = 256;
                    if (update15.inbox_date != 0) {
                        newMessage.date = update15.inbox_date;
                    } else {
                        newMessage.date = (int) (System.currentTimeMillis() / 1000);
                    }
                    newMessage.from_id = 777000;
                    newMessage.to_id = new TL_peerUser();
                    newMessage.to_id.user_id = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    newMessage.dialog_id = 777000;
                    if (update15.media != null) {
                        newMessage.media = update15.media;
                        newMessage.flags |= 512;
                    }
                    newMessage.message = update15.message;
                    if (update15.entities != null) {
                        newMessage.entities = update15.entities;
                    }
                    if (messagesArr == null) {
                        messagesArr = new ArrayList();
                    }
                    messagesArr.add(newMessage);
                    r9 = new MessageObject(this.currentAccount, newMessage, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(newMessage.dialog_id)));
                    if (messages == null) {
                        messages = new LongSparseArray();
                    }
                    arr = (ArrayList) messages.get(newMessage.dialog_id);
                    if (arr == null) {
                        arr = new ArrayList();
                        messages.put(newMessage.dialog_id, arr);
                    }
                    arr.add(r9);
                    if (pushMessages == null) {
                        pushMessages = new ArrayList();
                    }
                    pushMessages.add(r9);
                }
            } else if (baseUpdate instanceof TL_updateDialogPinned) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updatePinnedDialogs) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updatePrivacy) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateWebPage) {
                TL_updateWebPage update16 = (TL_updateWebPage) baseUpdate;
                if (webPages == null) {
                    webPages = new LongSparseArray();
                }
                webPages.put(update16.webpage.id, update16.webpage);
            } else if (baseUpdate instanceof TL_updateChannelWebPage) {
                TL_updateChannelWebPage update17 = (TL_updateChannelWebPage) baseUpdate;
                if (webPages == null) {
                    webPages = new LongSparseArray();
                }
                webPages.put(update17.webpage.id, update17.webpage);
            } else if (baseUpdate instanceof TL_updateChannelTooLong) {
                TL_updateChannelTooLong update18 = (TL_updateChannelTooLong) baseUpdate;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d(baseUpdate + " channelId = " + update18.channel_id);
                }
                int channelPts = this.channelsPts.get(update18.channel_id);
                if (channelPts == 0) {
                    channelPts = MessagesStorage.getInstance(this.currentAccount).getChannelPtsSync(update18.channel_id);
                    if (channelPts == 0) {
                        chat = (Chat) chatsDict.get(Integer.valueOf(update18.channel_id));
                        if (chat == null || chat.min) {
                            chat = getChat(Integer.valueOf(update18.channel_id));
                        }
                        if (chat == null || chat.min) {
                            chat = MessagesStorage.getInstance(this.currentAccount).getChatSync(update18.channel_id);
                            putChat(chat, true);
                        }
                        if (!(chat == null || chat.min)) {
                            loadUnknownChannel(chat, 0);
                        }
                    } else {
                        this.channelsPts.put(update18.channel_id, channelPts);
                    }
                }
                if (channelPts != 0) {
                    if ((update18.flags & 1) == 0) {
                        getChannelDifference(update18.channel_id);
                    } else if (update18.pts > channelPts) {
                        getChannelDifference(update18.channel_id);
                    }
                }
            } else if (baseUpdate instanceof TL_updateReadChannelInbox) {
                TL_updateReadChannelInbox update19 = (TL_updateReadChannelInbox) baseUpdate;
                message_id = ((long) update19.max_id) | (((long) update19.channel_id) << 32);
                dialog_id = (long) (-update19.channel_id);
                if (markAsReadMessagesInbox == null) {
                    markAsReadMessagesInbox = new SparseLongArray();
                }
                markAsReadMessagesInbox.put(-update19.channel_id, message_id);
                value = (Integer) this.dialogs_read_inbox_max.get(Long.valueOf(dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(false, dialog_id));
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(value.intValue(), update19.max_id)));
            } else if (baseUpdate instanceof TL_updateReadChannelOutbox) {
                TL_updateReadChannelOutbox update20 = (TL_updateReadChannelOutbox) baseUpdate;
                message_id = ((long) update20.max_id) | (((long) update20.channel_id) << 32);
                dialog_id = (long) (-update20.channel_id);
                if (markAsReadMessagesOutbox == null) {
                    markAsReadMessagesOutbox = new SparseLongArray();
                }
                markAsReadMessagesOutbox.put(-update20.channel_id, message_id);
                value = (Integer) this.dialogs_read_outbox_max.get(Long.valueOf(dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(true, dialog_id));
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog_id), Integer.valueOf(Math.max(value.intValue(), update20.max_id)));
            } else if (baseUpdate instanceof TL_updateDeleteChannelMessages) {
                TL_updateDeleteChannelMessages update21 = (TL_updateDeleteChannelMessages) baseUpdate;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d(baseUpdate + " channelId = " + update21.channel_id);
                }
                if (deletedMessages == null) {
                    deletedMessages = new SparseArray();
                }
                arrayList = (ArrayList) deletedMessages.get(update21.channel_id);
                if (arrayList == null) {
                    arrayList = new ArrayList();
                    deletedMessages.put(update21.channel_id, arrayList);
                }
                arrayList.addAll(update21.messages);
            } else if (baseUpdate instanceof TL_updateChannel) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d(baseUpdate + " channelId = " + ((TL_updateChannel) baseUpdate).channel_id);
                }
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateChannelMessageViews) {
                TL_updateChannelMessageViews update22 = (TL_updateChannelMessageViews) baseUpdate;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d(baseUpdate + " channelId = " + update22.channel_id);
                }
                if (channelViews == null) {
                    channelViews = new SparseArray();
                }
                SparseIntArray array = (SparseIntArray) channelViews.get(update22.channel_id);
                if (array == null) {
                    array = new SparseIntArray();
                    channelViews.put(update22.channel_id, array);
                }
                array.put(update22.id, update22.views);
            } else if (baseUpdate instanceof TL_updateChatParticipantAdmin) {
                TL_updateChatParticipantAdmin update23 = (TL_updateChatParticipantAdmin) baseUpdate;
                MessagesStorage.getInstance(this.currentAccount).updateChatInfo(update23.chat_id, update23.user_id, 2, update23.is_admin ? 1 : 0, update23.version);
            } else if (baseUpdate instanceof TL_updateChatAdmins) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateStickerSets) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateStickerSetsOrder) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateNewStickerSet) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateDraftMessage) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateSavedGifs) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if ((baseUpdate instanceof TL_updateEditChannelMessage) || (baseUpdate instanceof TL_updateEditMessage)) {
                clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                if (baseUpdate instanceof TL_updateEditChannelMessage) {
                    message = ((TL_updateEditChannelMessage) baseUpdate).message;
                    chat = (Chat) chatsDict.get(Integer.valueOf(message.to_id.channel_id));
                    if (chat == null) {
                        chat = getChat(Integer.valueOf(message.to_id.channel_id));
                    }
                    if (chat == null) {
                        chat = MessagesStorage.getInstance(this.currentAccount).getChatSync(message.to_id.channel_id);
                        putChat(chat, true);
                    }
                    if (chat != null && chat.megagroup) {
                        message.flags |= Integer.MIN_VALUE;
                    }
                } else {
                    message = ((TL_updateEditMessage) baseUpdate).message;
                    if (message.dialog_id == ((long) clientUserId)) {
                        message.unread = false;
                        message.media_unread = false;
                        message.out = true;
                    }
                }
                if (!message.out && message.from_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                    message.out = true;
                }
                if (!fromGetDifference) {
                    count = message.entities.size();
                    for (a = 0; a < count; a++) {
                        entity = (MessageEntity) message.entities.get(a);
                        if (entity instanceof TL_messageEntityMentionName) {
                            user_id = ((TL_messageEntityMentionName) entity).user_id;
                            user = (User) usersDict.get(Integer.valueOf(user_id));
                            if (user == null || user.min) {
                                user = getUser(Integer.valueOf(user_id));
                            }
                            if (user == null || user.min) {
                                user = MessagesStorage.getInstance(this.currentAccount).getUserSync(user_id);
                                if (user != null && user.min) {
                                    user = null;
                                }
                                putUser(user, true);
                            }
                            if (user == null) {
                                return false;
                            }
                        }
                    }
                }
                if (message.to_id.chat_id != 0) {
                    message.dialog_id = (long) (-message.to_id.chat_id);
                } else if (message.to_id.channel_id != 0) {
                    message.dialog_id = (long) (-message.to_id.channel_id);
                } else {
                    if (message.to_id.user_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                        message.to_id.user_id = message.from_id;
                    }
                    message.dialog_id = (long) message.to_id.user_id;
                }
                read_max = message.out ? this.dialogs_read_outbox_max : this.dialogs_read_inbox_max;
                value = (Integer) read_max.get(Long.valueOf(message.dialog_id));
                if (value == null) {
                    value = Integer.valueOf(MessagesStorage.getInstance(this.currentAccount).getDialogReadMax(message.out, message.dialog_id));
                    read_max.put(Long.valueOf(message.dialog_id), value);
                }
                message.unread = value.intValue() < message.id;
                if (message.dialog_id == ((long) clientUserId)) {
                    message.out = true;
                    message.unread = false;
                    message.media_unread = false;
                }
                if (message.out && message.message == null) {
                    message.message = TtmlNode.ANONYMOUS_REGION_ID;
                    message.attachPath = TtmlNode.ANONYMOUS_REGION_ID;
                }
                ImageLoader.saveMessageThumbs(message);
                obj = new MessageObject(this.currentAccount, message, usersDict, chatsDict, this.createdDialogIds.contains(Long.valueOf(message.dialog_id)));
                if (editingMessages == null) {
                    editingMessages = new LongSparseArray();
                }
                arr = (ArrayList) editingMessages.get(message.dialog_id);
                if (arr == null) {
                    arr = new ArrayList();
                    editingMessages.put(message.dialog_id, arr);
                }
                arr.add(obj);
            } else if (baseUpdate instanceof TL_updateChannelPinnedMessage) {
                TL_updateChannelPinnedMessage update24 = (TL_updateChannelPinnedMessage) baseUpdate;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m0d(baseUpdate + " channelId = " + update24.channel_id);
                }
                MessagesStorage.getInstance(this.currentAccount).updateChannelPinnedMessage(update24.channel_id, update24.id);
            } else if (baseUpdate instanceof TL_updateReadFeaturedStickers) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updatePhoneCall) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateLangPack) {
                LocaleController.getInstance().saveRemoteLocaleStrings(((TL_updateLangPack) baseUpdate).difference, this.currentAccount);
            } else if (baseUpdate instanceof TL_updateLangPackTooLong) {
                LocaleController.getInstance().reloadCurrentRemoteLocale(this.currentAccount);
            } else if (baseUpdate instanceof TL_updateFavedStickers) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateContactsReset) {
                if (updatesOnMainThread == null) {
                    updatesOnMainThread = new ArrayList();
                }
                updatesOnMainThread.add(baseUpdate);
            } else if (baseUpdate instanceof TL_updateChannelAvailableMessages) {
                TL_updateChannelAvailableMessages update25 = (TL_updateChannelAvailableMessages) baseUpdate;
                if (clearHistoryMessages == null) {
                    clearHistoryMessages = new SparseIntArray();
                }
                int currentValue = clearHistoryMessages.get(update25.channel_id);
                if (currentValue == 0 || currentValue < update25.available_min_id) {
                    clearHistoryMessages.put(update25.channel_id, update25.available_min_id);
                }
            }
        }
        if (messages != null) {
            size = messages.size();
            for (a = 0; a < size; a++) {
                if (updatePrintingUsersWithNewMessages(messages.keyAt(a), (ArrayList) messages.valueAt(a))) {
                    printChanged = true;
                }
            }
        }
        if (printChanged) {
            updatePrintingStrings();
        }
        int interfaceUpdateMaskFinal = interfaceUpdateMask;
        final boolean printChangedArg = printChanged;
        if (contactsIds != null) {
            ContactsController.getInstance(this.currentAccount).processContactsUpdates(contactsIds, usersDict);
        }
        if (pushMessages != null) {
            final ArrayList<MessageObject> arrayList3 = pushMessages;
            MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

                /* renamed from: org.telegram.messenger.MessagesController$131$1 */
                class C03101 implements Runnable {
                    C03101() {
                    }

                    public void run() {
                        NotificationsController.getInstance(MessagesController.this.currentAccount).processNewMessages(arrayList3, true, false);
                    }
                }

                public void run() {
                    AndroidUtilities.runOnUIThread(new C03101());
                }
            });
        }
        if (messagesArr != null) {
            StatsController.getInstance(this.currentAccount).incrementReceivedItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, messagesArr.size());
            MessagesStorage.getInstance(this.currentAccount).putMessages((ArrayList) messagesArr, true, true, false, DownloadController.getInstance(this.currentAccount).getAutodownloadMask());
        }
        if (editingMessages != null) {
            size = editingMessages.size();
            for (int b = 0; b < size; b++) {
                messages_Messages messagesRes = new TL_messages_messages();
                ArrayList<MessageObject> messageObjects = (ArrayList) editingMessages.valueAt(b);
                int size2 = messageObjects.size();
                for (a = 0; a < size2; a++) {
                    messagesRes.messages.add(((MessageObject) messageObjects.get(a)).messageOwner);
                }
                MessagesStorage.getInstance(this.currentAccount).putMessages(messagesRes, editingMessages.keyAt(b), -2, 0, false);
            }
        }
        if (channelViews != null) {
            MessagesStorage.getInstance(this.currentAccount).putChannelViews(channelViews, true);
        }
        final LongSparseArray<ArrayList<MessageObject>> editingMessagesFinal = editingMessages;
        final SparseArray<SparseIntArray> channelViewsFinal = channelViews;
        final LongSparseArray<WebPage> webPagesFinal = webPages;
        final LongSparseArray<ArrayList<MessageObject>> messagesFinal = messages;
        final ArrayList<ChatParticipants> chatInfoToUpdateFinal = chatInfoToUpdate;
        final ArrayList<Integer> contactsIdsFinal = contactsIds;
        final ArrayList<Update> updatesOnMainThreadFinal = updatesOnMainThread;
        final int i = interfaceUpdateMaskFinal;
        AndroidUtilities.runOnUIThread(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$132$3 */
            class C18163 implements RequestDelegate {
                C18163() {
                }

                public void run(TLObject response, TL_error error) {
                    if (response != null) {
                        MessagesController.this.processUpdates((Updates) response, false);
                    }
                }
            }

            public void run() {
                int size;
                int a;
                int size2;
                int b;
                long dialog_id;
                ArrayList<MessageObject> arrayList;
                int updateMask = i;
                boolean hasDraftUpdates = false;
                if (updatesOnMainThreadFinal != null) {
                    ArrayList<User> dbUsers = new ArrayList();
                    ArrayList<User> dbUsersStatus = new ArrayList();
                    Editor editor = null;
                    size = updatesOnMainThreadFinal.size();
                    for (a = 0; a < size; a++) {
                        Update baseUpdate = (Update) updatesOnMainThreadFinal.get(a);
                        if (baseUpdate instanceof TL_updatePrivacy) {
                            TL_updatePrivacy update = (TL_updatePrivacy) baseUpdate;
                            if (update.key instanceof TL_privacyKeyStatusTimestamp) {
                                ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(update.rules, 0);
                            } else if (update.key instanceof TL_privacyKeyChatInvite) {
                                ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(update.rules, 1);
                            } else if (update.key instanceof TL_privacyKeyPhoneCall) {
                                ContactsController.getInstance(MessagesController.this.currentAccount).setPrivacyRules(update.rules, 2);
                            }
                        } else if (baseUpdate instanceof TL_updateUserStatus) {
                            TL_updateUserStatus update2 = (TL_updateUserStatus) baseUpdate;
                            currentUser = MessagesController.this.getUser(Integer.valueOf(update2.user_id));
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
                            if (update2.user_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
                                NotificationsController.getInstance(MessagesController.this.currentAccount).setLastOnlineFromOtherDevice(update2.status.expires);
                            }
                        } else if (baseUpdate instanceof TL_updateUserName) {
                            TL_updateUserName update3 = (TL_updateUserName) baseUpdate;
                            currentUser = MessagesController.this.getUser(Integer.valueOf(update3.user_id));
                            if (currentUser != null) {
                                if (!UserObject.isContact(currentUser)) {
                                    currentUser.first_name = update3.first_name;
                                    currentUser.last_name = update3.last_name;
                                }
                                if (!TextUtils.isEmpty(currentUser.username)) {
                                    MessagesController.this.objectsByUsernames.remove(currentUser.username);
                                }
                                if (TextUtils.isEmpty(update3.username)) {
                                    MessagesController.this.objectsByUsernames.put(update3.username, currentUser);
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
                            if (!MessagesController.this.pinDialog(did, updateDialogPinned.pinned, null, -1)) {
                                UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = false;
                                UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
                                MessagesController.this.loadPinnedDialogs(did, null);
                            }
                        } else if (baseUpdate instanceof TL_updatePinnedDialogs) {
                            ArrayList<Long> order;
                            TL_updatePinnedDialogs update4 = (TL_updatePinnedDialogs) baseUpdate;
                            UserConfig.getInstance(MessagesController.this.currentAccount).pinnedDialogsLoaded = false;
                            UserConfig.getInstance(MessagesController.this.currentAccount).saveConfig(false);
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
                            MessagesController.this.loadPinnedDialogs(0, order);
                        } else if (baseUpdate instanceof TL_updateUserPhoto) {
                            TL_updateUserPhoto update5 = (TL_updateUserPhoto) baseUpdate;
                            currentUser = MessagesController.this.getUser(Integer.valueOf(update5.user_id));
                            if (currentUser != null) {
                                currentUser.photo = update5.photo;
                            }
                            toDbUser = new TL_user();
                            toDbUser.id = update5.user_id;
                            toDbUser.photo = update5.photo;
                            dbUsers.add(toDbUser);
                        } else if (baseUpdate instanceof TL_updateUserPhone) {
                            TL_updateUserPhone update6 = (TL_updateUserPhone) baseUpdate;
                            currentUser = MessagesController.this.getUser(Integer.valueOf(update6.user_id));
                            if (currentUser != null) {
                                currentUser.phone = update6.phone;
                                final User user = currentUser;
                                Utilities.phoneBookQueue.postRunnable(new Runnable() {
                                    public void run() {
                                        ContactsController.getInstance(MessagesController.this.currentAccount).addContactToPhoneBook(user, true);
                                    }
                                });
                            }
                            toDbUser = new TL_user();
                            toDbUser.id = update6.user_id;
                            toDbUser.phone = update6.phone;
                            dbUsers.add(toDbUser);
                        } else if (baseUpdate instanceof TL_updateNotifySettings) {
                            TL_updateNotifySettings update7 = (TL_updateNotifySettings) baseUpdate;
                            if ((update7.notify_settings instanceof TL_peerNotifySettings) && (update7.peer instanceof TL_notifyPeer)) {
                                if (editor == null) {
                                    editor = MessagesController.this.notificationsPreferences.edit();
                                }
                                if (update7.peer.peer.user_id != 0) {
                                    dialog_id = (long) update7.peer.peer.user_id;
                                } else if (update7.peer.peer.chat_id != 0) {
                                    dialog_id = (long) (-update7.peer.peer.chat_id);
                                } else {
                                    dialog_id = (long) (-update7.peer.peer.channel_id);
                                }
                                dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(dialog_id);
                                if (dialog != null) {
                                    dialog.notify_settings = update7.notify_settings;
                                }
                                editor.putBoolean("silent_" + dialog_id, update7.notify_settings.silent);
                                int currentTime = ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime();
                                if (update7.notify_settings.mute_until > currentTime) {
                                    int until = 0;
                                    if (update7.notify_settings.mute_until > 31536000 + currentTime) {
                                        editor.putInt("notify2_" + dialog_id, 2);
                                        if (dialog != null) {
                                            dialog.notify_settings.mute_until = ConnectionsManager.DEFAULT_DATACENTER_ID;
                                        }
                                    } else {
                                        until = update7.notify_settings.mute_until;
                                        editor.putInt("notify2_" + dialog_id, 3);
                                        editor.putInt("notifyuntil_" + dialog_id, update7.notify_settings.mute_until);
                                        if (dialog != null) {
                                            dialog.notify_settings.mute_until = until;
                                        }
                                    }
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogFlags(dialog_id, (((long) until) << 32) | 1);
                                    NotificationsController.getInstance(MessagesController.this.currentAccount).removeNotificationsForDialog(dialog_id);
                                } else {
                                    if (dialog != null) {
                                        dialog.notify_settings.mute_until = 0;
                                    }
                                    editor.remove("notify2_" + dialog_id);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).setDialogFlags(dialog_id, 0);
                                }
                            }
                        } else if (baseUpdate instanceof TL_updateChannel) {
                            TL_updateChannel update8 = (TL_updateChannel) baseUpdate;
                            dialog = (TL_dialog) MessagesController.this.dialogs_dict.get(-((long) update8.channel_id));
                            Chat chat = MessagesController.this.getChat(Integer.valueOf(update8.channel_id));
                            if (chat != null) {
                                if (dialog == null && (chat instanceof TL_channel) && !chat.left) {
                                    final TL_updateChannel tL_updateChannel = update8;
                                    Utilities.stageQueue.postRunnable(new Runnable() {
                                        public void run() {
                                            MessagesController.this.getChannelDifference(tL_updateChannel.channel_id, 1, 0, null);
                                        }
                                    });
                                } else if (chat.left && dialog != null) {
                                    MessagesController.this.deleteDialog(dialog.id, 0);
                                }
                            }
                            updateMask |= MessagesController.UPDATE_MASK_CHANNEL;
                            MessagesController.this.loadFullChat(update8.channel_id, 0, true);
                        } else if (baseUpdate instanceof TL_updateChatAdmins) {
                            updateMask |= MessagesController.UPDATE_MASK_CHAT_ADMINS;
                        } else if (baseUpdate instanceof TL_updateStickerSets) {
                            TL_updateStickerSets update9 = (TL_updateStickerSets) baseUpdate;
                            DataQuery.getInstance(MessagesController.this.currentAccount).loadStickers(0, false, true);
                        } else if (baseUpdate instanceof TL_updateStickerSetsOrder) {
                            DataQuery.getInstance(MessagesController.this.currentAccount).reorderStickers(((TL_updateStickerSetsOrder) baseUpdate).masks ? 1 : 0, ((TL_updateStickerSetsOrder) baseUpdate).order);
                        } else if (baseUpdate instanceof TL_updateFavedStickers) {
                            DataQuery.getInstance(MessagesController.this.currentAccount).loadRecents(2, false, false, true);
                        } else if (baseUpdate instanceof TL_updateContactsReset) {
                            ContactsController.getInstance(MessagesController.this.currentAccount).forceImportContacts();
                        } else if (baseUpdate instanceof TL_updateNewStickerSet) {
                            DataQuery.getInstance(MessagesController.this.currentAccount).addNewStickerSet(((TL_updateNewStickerSet) baseUpdate).stickerset);
                        } else if (baseUpdate instanceof TL_updateSavedGifs) {
                            MessagesController.this.emojiPreferences.edit().putLong("lastGifLoadTime", 0).commit();
                        } else if (baseUpdate instanceof TL_updateRecentStickers) {
                            MessagesController.this.emojiPreferences.edit().putLong("lastStickersLoadTime", 0).commit();
                        } else if (baseUpdate instanceof TL_updateDraftMessage) {
                            TL_updateDraftMessage update10 = (TL_updateDraftMessage) baseUpdate;
                            hasDraftUpdates = true;
                            peer = ((TL_updateDraftMessage) baseUpdate).peer;
                            if (peer.user_id != 0) {
                                did = (long) peer.user_id;
                            } else if (peer.channel_id != 0) {
                                did = (long) (-peer.channel_id);
                            } else {
                                did = (long) (-peer.chat_id);
                            }
                            DataQuery.getInstance(MessagesController.this.currentAccount).saveDraft(did, update10.draft, null, true);
                        } else if (baseUpdate instanceof TL_updateReadFeaturedStickers) {
                            DataQuery.getInstance(MessagesController.this.currentAccount).markFaturedStickersAsRead(false);
                        } else if (baseUpdate instanceof TL_updatePhoneCall) {
                            PhoneCall call = ((TL_updatePhoneCall) baseUpdate).phone_call;
                            VoIPService svc = VoIPService.getSharedInstance();
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m0d("Received call in update: " + call);
                                FileLog.m0d("call id " + call.id);
                            }
                            if (call instanceof TL_phoneCallRequested) {
                                if (call.date + (MessagesController.this.callRingTimeout / 1000) >= ConnectionsManager.getInstance(MessagesController.this.currentAccount).getCurrentTime()) {
                                    TelephonyManager tm = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                                    if (svc == null && VoIPService.callIShouldHavePutIntoIntent == null && tm.getCallState() == 0) {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("Starting service for call " + call.id);
                                        }
                                        VoIPService.callIShouldHavePutIntoIntent = call;
                                        Intent intent = new Intent(ApplicationLoader.applicationContext, VoIPService.class);
                                        intent.putExtra("is_outgoing", false);
                                        intent.putExtra("user_id", call.participant_id == UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId() ? call.admin_id : call.participant_id);
                                        intent.putExtra("account", MessagesController.this.currentAccount);
                                        try {
                                            if (VERSION.SDK_INT >= 26) {
                                                ApplicationLoader.applicationContext.startForegroundService(intent);
                                            } else {
                                                ApplicationLoader.applicationContext.startService(intent);
                                            }
                                        } catch (Throwable e) {
                                            FileLog.m3e(e);
                                        }
                                    } else {
                                        if (BuildVars.LOGS_ENABLED) {
                                            FileLog.m0d("Auto-declining call " + call.id + " because there's already active one");
                                        }
                                        TLObject req = new TL_phone_discardCall();
                                        req.peer = new TL_inputPhoneCall();
                                        req.peer.access_hash = call.access_hash;
                                        req.peer.id = call.id;
                                        req.reason = new TL_phoneCallDiscardReasonBusy();
                                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).sendRequest(req, new C18163());
                                    }
                                } else if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("ignoring too old call");
                                }
                            } else if (svc != null && call != null) {
                                svc.onCallUpdated(call);
                            } else if (VoIPService.callIShouldHavePutIntoIntent != null) {
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.m0d("Updated the call while the service is starting");
                                }
                                if (call.id == VoIPService.callIShouldHavePutIntoIntent.id) {
                                    VoIPService.callIShouldHavePutIntoIntent = call;
                                }
                            }
                        } else if (!(baseUpdate instanceof TL_updateGroupCall) && (baseUpdate instanceof TL_updateGroupCallParticipant)) {
                        }
                    }
                    if (editor != null) {
                        editor.commit();
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                    }
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateUsers(dbUsersStatus, true, true, true);
                    MessagesStorage.getInstance(MessagesController.this.currentAccount).updateUsers(dbUsers, false, true, true);
                }
                if (webPagesFinal != null) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didReceivedWebpagesInUpdates, webPagesFinal);
                    size = webPagesFinal.size();
                    for (b = 0; b < size; b++) {
                        long key = webPagesFinal.keyAt(b);
                        arrayList = (ArrayList) MessagesController.this.reloadingWebpagesPending.get(key);
                        MessagesController.this.reloadingWebpagesPending.remove(key);
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
                                MessagesController.this.reloadingWebpagesPending.put(webpage.id, arrayList);
                            }
                            if (!arr.isEmpty()) {
                                MessagesStorage.getInstance(MessagesController.this.currentAccount).putMessages(arr, true, true, false, DownloadController.getInstance(MessagesController.this.currentAccount).getAutodownloadMask());
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialog_id), arrayList);
                            }
                        }
                    }
                }
                boolean updateDialogs = false;
                if (messagesFinal != null) {
                    size = messagesFinal.size();
                    for (a = 0; a < size; a++) {
                        MessagesController.this.updateInterfaceWithMessages(messagesFinal.keyAt(a), (ArrayList) messagesFinal.valueAt(a));
                    }
                    updateDialogs = true;
                } else if (hasDraftUpdates) {
                    MessagesController.this.sortDialogs(null);
                    updateDialogs = true;
                }
                if (editingMessagesFinal != null) {
                    size = editingMessagesFinal.size();
                    for (b = 0; b < size; b++) {
                        dialog_id = editingMessagesFinal.keyAt(b);
                        arrayList = (ArrayList) editingMessagesFinal.valueAt(b);
                        MessageObject oldObject = (MessageObject) MessagesController.this.dialogMessage.get(dialog_id);
                        if (oldObject != null) {
                            a = 0;
                            size2 = arrayList.size();
                            while (a < size2) {
                                MessageObject newMessage = (MessageObject) arrayList.get(a);
                                if (oldObject.getId() == newMessage.getId()) {
                                    MessagesController.this.dialogMessage.put(dialog_id, newMessage);
                                    if (newMessage.messageOwner.to_id != null && newMessage.messageOwner.to_id.channel_id == 0) {
                                        MessagesController.this.dialogMessagesByIds.put(newMessage.getId(), newMessage);
                                    }
                                    updateDialogs = true;
                                } else {
                                    a++;
                                }
                            }
                        }
                        DataQuery.getInstance(MessagesController.this.currentAccount).loadReplyMessagesForMessages(arrayList, dialog_id);
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.replaceMessagesObjects, Long.valueOf(dialog_id), arrayList);
                    }
                }
                if (updateDialogs) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
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
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateChatParticipants((ChatParticipants) chatInfoToUpdateFinal.get(a));
                    }
                }
                if (channelViewsFinal != null) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.didUpdatedMessagesViews, channelViewsFinal);
                }
                if (updateMask != 0) {
                    NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(updateMask));
                }
            }
        });
        final SparseLongArray markAsReadMessagesInboxFinal = markAsReadMessagesInbox;
        final SparseLongArray markAsReadMessagesOutboxFinal = markAsReadMessagesOutbox;
        final ArrayList<Long> markAsReadMessagesFinal = markAsReadMessages;
        final SparseIntArray markAsReadEncryptedFinal = markAsReadEncrypted;
        final SparseArray<ArrayList<Integer>> deletedMessagesFinal = deletedMessages;
        final SparseIntArray clearHistoryMessagesFinal = clearHistoryMessages;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {

            /* renamed from: org.telegram.messenger.MessagesController$133$1 */
            class C03131 implements Runnable {
                C03131() {
                }

                public void run() {
                    int size;
                    int b;
                    int key;
                    MessageObject obj;
                    int a;
                    int updateMask = 0;
                    if (!(markAsReadMessagesInboxFinal == null && markAsReadMessagesOutboxFinal == null)) {
                        int messageId;
                        TL_dialog dialog;
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesRead, markAsReadMessagesInboxFinal, markAsReadMessagesOutboxFinal);
                        if (markAsReadMessagesInboxFinal != null) {
                            NotificationsController.getInstance(MessagesController.this.currentAccount).processReadMessages(markAsReadMessagesInboxFinal, 0, 0, 0, false);
                            Editor editor = MessagesController.this.notificationsPreferences.edit();
                            size = markAsReadMessagesInboxFinal.size();
                            for (b = 0; b < size; b++) {
                                key = markAsReadMessagesInboxFinal.keyAt(b);
                                messageId = (int) markAsReadMessagesInboxFinal.valueAt(b);
                                dialog = (TL_dialog) MessagesController.this.dialogs_dict.get((long) key);
                                if (dialog != null && dialog.top_message > 0 && dialog.top_message <= messageId) {
                                    obj = (MessageObject) MessagesController.this.dialogMessage.get(dialog.id);
                                    if (!(obj == null || obj.isOut())) {
                                        obj.setIsRead();
                                        updateMask |= 256;
                                    }
                                }
                                if (key != UserConfig.getInstance(MessagesController.this.currentAccount).getClientUserId()) {
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
                                dialog = (TL_dialog) MessagesController.this.dialogs_dict.get((long) markAsReadMessagesOutboxFinal.keyAt(b));
                                if (dialog != null && dialog.top_message > 0 && dialog.top_message <= messageId) {
                                    obj = (MessageObject) MessagesController.this.dialogMessage.get(dialog.id);
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
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesReadEncrypted, Integer.valueOf(key), Integer.valueOf(value));
                            long dialog_id = ((long) key) << 32;
                            if (((TL_dialog) MessagesController.this.dialogs_dict.get(dialog_id)) != null) {
                                MessageObject message = (MessageObject) MessagesController.this.dialogMessage.get(dialog_id);
                                if (message != null && message.messageOwner.date <= value) {
                                    message.setIsRead();
                                    updateMask |= 256;
                                }
                            }
                        }
                    }
                    if (markAsReadMessagesFinal != null) {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesReadContent, markAsReadMessagesFinal);
                    }
                    if (deletedMessagesFinal != null) {
                        size = deletedMessagesFinal.size();
                        for (a = 0; a < size; a++) {
                            key = deletedMessagesFinal.keyAt(a);
                            ArrayList<Integer> arrayList = (ArrayList) deletedMessagesFinal.valueAt(a);
                            if (arrayList != null) {
                                NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.messagesDeleted, arrayList, Integer.valueOf(key));
                                int size2;
                                if (key == 0) {
                                    size2 = arrayList.size();
                                    for (b = 0; b < size2; b++) {
                                        obj = (MessageObject) MessagesController.this.dialogMessagesByIds.get(((Integer) arrayList.get(b)).intValue());
                                        if (obj != null) {
                                            obj.deleted = true;
                                        }
                                    }
                                } else {
                                    obj = (MessageObject) MessagesController.this.dialogMessage.get((long) (-key));
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
                        NotificationsController.getInstance(MessagesController.this.currentAccount).removeDeletedMessagesFromNotifications(deletedMessagesFinal);
                    }
                    if (clearHistoryMessagesFinal != null) {
                        size = clearHistoryMessagesFinal.size();
                        for (a = 0; a < size; a++) {
                            key = clearHistoryMessagesFinal.keyAt(a);
                            int id = clearHistoryMessagesFinal.valueAt(a);
                            long did = (long) (-key);
                            NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.historyCleared, Long.valueOf(did), Integer.valueOf(id));
                            obj = (MessageObject) MessagesController.this.dialogMessage.get(did);
                            if (obj != null && obj.getId() <= id) {
                                obj.deleted = true;
                                break;
                            }
                        }
                        NotificationsController.getInstance(MessagesController.this.currentAccount).removeDeletedHisoryFromNotifications(clearHistoryMessagesFinal);
                    }
                    if (updateMask != 0) {
                        NotificationCenter.getInstance(MessagesController.this.currentAccount).postNotificationName(NotificationCenter.updateInterfaces, Integer.valueOf(updateMask));
                    }
                }
            }

            public void run() {
                AndroidUtilities.runOnUIThread(new C03131());
            }
        });
        if (webPages != null) {
            MessagesStorage.getInstance(this.currentAccount).putWebPages(webPages);
        }
        if (!(markAsReadMessagesInbox == null && markAsReadMessagesOutbox == null && markAsReadEncrypted == null && markAsReadMessages == null)) {
            if (!(markAsReadMessagesInbox == null && markAsReadMessages == null)) {
                MessagesStorage.getInstance(this.currentAccount).updateDialogsWithReadMessages(markAsReadMessagesInbox, markAsReadMessagesOutbox, markAsReadMessages, true);
            }
            MessagesStorage.getInstance(this.currentAccount).markMessagesAsRead(markAsReadMessagesInbox, markAsReadMessagesOutbox, markAsReadEncrypted, true);
        }
        if (markAsReadMessages != null) {
            MessagesStorage.getInstance(this.currentAccount).markMessagesContentAsRead(markAsReadMessages, ConnectionsManager.getInstance(this.currentAccount).getCurrentTime());
        }
        if (deletedMessages != null) {
            size = deletedMessages.size();
            for (a = 0; a < size; a++) {
                final ArrayList<Integer> arrayList4 = (ArrayList) deletedMessages.valueAt(a);
                final int keyAt = deletedMessages.keyAt(a);
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(arrayList4, MessagesStorage.getInstance(MessagesController.this.currentAccount).markMessagesAsDeleted(arrayList4, false, keyAt), false, keyAt);
                    }
                });
            }
        }
        if (clearHistoryMessages != null) {
            size = clearHistoryMessages.size();
            for (a = 0; a < size; a++) {
                final int keyAt2 = clearHistoryMessages.keyAt(a);
                keyAt = clearHistoryMessages.valueAt(a);
                MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() {
                    public void run() {
                        MessagesStorage.getInstance(MessagesController.this.currentAccount).updateDialogsWithDeletedMessages(new ArrayList(), MessagesStorage.getInstance(MessagesController.this.currentAccount).markMessagesAsDeleted(keyAt2, keyAt, false), false, keyAt2);
                    }
                });
            }
        }
        if (tasks != null) {
            size = tasks.size();
            for (a = 0; a < size; a++) {
                update12 = (TL_updateEncryptedMessagesRead) tasks.get(a);
                MessagesStorage.getInstance(this.currentAccount).createTaskForSecretChat(update12.chat_id, update12.max_date, update12.date, 1, null);
            }
        }
        return true;
    }

    private boolean isNotifySettingsMuted(PeerNotifySettings settings) {
        return (settings instanceof TL_peerNotifySettings) && settings.mute_until > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
    }

    public boolean isDialogMuted(long dialog_id) {
        int mute_type = this.notificationsPreferences.getInt("notify2_" + dialog_id, 0);
        if (mute_type == 2) {
            return true;
        }
        if (mute_type != 3 || this.notificationsPreferences.getInt("notifyuntil_" + dialog_id, 0) < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) {
            return false;
        }
        return true;
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

    protected void updateInterfaceWithMessages(long uid, ArrayList<MessageObject> messages) {
        updateInterfaceWithMessages(uid, messages, false);
    }

    protected void updateInterfaceWithMessages(long uid, ArrayList<MessageObject> messages, boolean isBroadcast) {
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
                        DataQuery.getInstance(this.currentAccount).addRecentSticker(0, message.messageOwner.media.document, message.messageOwner.date, false);
                    }
                }
                if (message.isOut() && message.isSent()) {
                    updateRating = true;
                }
            }
            DataQuery.getInstance(this.currentAccount).loadReplyMessagesForMessages(messages, uid);
            NotificationCenter.getInstance(this.currentAccount).postNotificationName(NotificationCenter.didReceivedNewMessages, Long.valueOf(uid), messages);
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
                    this.dialogsGroupsOnly.remove(dialog);
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

    public void sortDialogs(SparseArray<Chat> chatsDict) {
        this.dialogsServerOnly.clear();
        this.dialogsGroupsOnly.clear();
        this.dialogsForward.clear();
        boolean selfAdded = false;
        int selfId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        Collections.sort(this.dialogs, this.dialogComparator);
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
                Chat chat;
                if (DialogObject.isChannel(d)) {
                    chat = getChat(Integer.valueOf(-lower_id));
                    if (chat != null && ((chat.megagroup && chat.admin_rights != null && (chat.admin_rights.post_messages || chat.admin_rights.add_admins)) || chat.creator)) {
                        this.dialogsGroupsOnly.add(d);
                    }
                } else if (lower_id < 0) {
                    if (chatsDict != null) {
                        chat = (Chat) chatsDict.get(-lower_id);
                        if (!(chat == null || chat.migrated_to == null)) {
                            this.dialogs.remove(a);
                            a--;
                        }
                    }
                    this.dialogsGroupsOnly.add(d);
                }
            }
            a++;
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
            builder.setTitle(LocaleController.getString("AppName", R.string.AppName));
            builder.setPositiveButton(LocaleController.getString("OK", R.string.OK), null);
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
                final AlertDialog progressDialog = new AlertDialog(fragment.getParentActivity(), 1);
                progressDialog.setMessage(LocaleController.getString("Loading", R.string.Loading));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
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
                final BaseFragment baseFragment = fragment;
                final Bundle bundle2 = bundle;
                final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, TL_error error) {
                        if (response != null) {
                            AndroidUtilities.runOnUIThread(new Runnable() {
                                public void run() {
                                    try {
                                        progressDialog.dismiss();
                                    } catch (Throwable e) {
                                        FileLog.m3e(e);
                                    }
                                    messages_Messages res = response;
                                    MessagesController.this.putUsers(res.users, false);
                                    MessagesController.this.putChats(res.chats, false);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, res.chats, true, true);
                                    baseFragment.presentFragment(new ChatActivity(bundle2), true);
                                }
                            });
                        }
                    }
                });
                baseFragment = fragment;
                progressDialog.setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(reqId, true);
                        try {
                            dialog.dismiss();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                        }
                        if (baseFragment != null) {
                            baseFragment.setVisibleDialog(null);
                        }
                    }
                });
                fragment.setVisibleDialog(progressDialog);
                progressDialog.show();
                return false;
            }
        }
        return true;
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

    public void openByUserName(String username, final BaseFragment fragment, final int type) {
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
                final AlertDialog[] progressDialog = new AlertDialog[]{new AlertDialog(fragment.getParentActivity(), 1)};
                TL_contacts_resolveUsername req = new TL_contacts_resolveUsername();
                req.username = username;
                final int reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RequestDelegate() {
                    public void run(final TLObject response, final TL_error error) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            public void run() {
                                try {
                                    progressDialog[0].dismiss();
                                } catch (Exception e) {
                                }
                                progressDialog[0] = null;
                                fragment.setVisibleDialog(null);
                                if (error == null) {
                                    TL_contacts_resolvedPeer res = response;
                                    MessagesController.this.putUsers(res.users, false);
                                    MessagesController.this.putChats(res.chats, false);
                                    MessagesStorage.getInstance(MessagesController.this.currentAccount).putUsersAndChats(res.users, res.chats, false, true);
                                    if (!res.chats.isEmpty()) {
                                        MessagesController.openChatOrProfileWith(null, (Chat) res.chats.get(0), fragment, 1, false);
                                    } else if (!res.users.isEmpty()) {
                                        MessagesController.openChatOrProfileWith((User) res.users.get(0), null, fragment, type, false);
                                    }
                                } else if (fragment != null && fragment.getParentActivity() != null) {
                                    try {
                                        Toast.makeText(fragment.getParentActivity(), LocaleController.getString("NoUsernameFound", R.string.NoUsernameFound), 0).show();
                                    } catch (Throwable e2) {
                                        FileLog.m3e(e2);
                                    }
                                }
                            }
                        });
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() {

                    /* renamed from: org.telegram.messenger.MessagesController$139$1 */
                    class C03161 implements OnClickListener {
                        C03161() {
                        }

                        public void onClick(DialogInterface dialog, int which) {
                            ConnectionsManager.getInstance(MessagesController.this.currentAccount).cancelRequest(reqId, true);
                            try {
                                dialog.dismiss();
                            } catch (Throwable e) {
                                FileLog.m3e(e);
                            }
                        }
                    }

                    public void run() {
                        if (progressDialog[0] != null) {
                            progressDialog[0].setMessage(LocaleController.getString("Loading", R.string.Loading));
                            progressDialog[0].setCanceledOnTouchOutside(false);
                            progressDialog[0].setCancelable(false);
                            progressDialog[0].setButton(-2, LocaleController.getString("Cancel", R.string.Cancel), new C03161());
                            fragment.showDialog(progressDialog[0]);
                        }
                    }
                }, 500);
            }
        }
    }
}
